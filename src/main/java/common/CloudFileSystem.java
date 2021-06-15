package common;

import client.MainWndController;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

@Slf4j
public class CloudFileSystem {

    final static int FILES = 2;
    final static int DIRECTORIES = 1;
    boolean serverSide;

    private SeekableByteChannel sbCh;
    private int partNum = 0;
    private long totalTransferBytes = 0;
    private FileSystemStates state = FileSystemStates.IDLE;

    private Queue<FileInfo> fileList = new LinkedList<>();
    private int totalFilesInList = 0;

    private Path userFolder; //absolute path, may be null
    private Path currentFolder;

    public CloudFileSystem(Path currentFolder, Path userFolder, boolean serverSide) throws IOException {

        if(!currentFolder.isAbsolute()){
            currentFolder = Paths.get(Options.SERVER_ROOT, currentFolder.toString());
        }

        this.currentFolder = currentFolder.toAbsolutePath();
        this.serverSide = serverSide;

        if(serverSide) {

            if(userFolder.isAbsolute()){
                this.userFolder = userFolder.toAbsolutePath();
            }
            else{
                this.userFolder = Paths.get(Options.SERVER_ROOT, userFolder.getFileName().toString());
            }

            if (!Files.exists(Paths.get(Options.SERVER_ROOT))) {
                makeDir(Paths.get(Options.SERVER_ROOT));
            }

        }
    }

    public void setUserFolder(Path userFolder){
        if(userFolder.isAbsolute()){
            this.userFolder = userFolder.toAbsolutePath();
        }
        else{
            this.userFolder = Paths.get(Options.SERVER_ROOT, userFolder.getFileName().toString());
        }
    }

    public Path getCurrentFolder() {
        return currentFolder;
    }

    public String getCurrentPath(){
        if(serverSide){
            return userFolder.relativize(currentFolder).toString();
        }
        return currentFolder.toString();
    }


    public void goToParentFolder(){
        if(serverSide){
            String relPath = userFolder.relativize(currentFolder).toString();
            if(relPath.equals("..") || relPath.isEmpty()){
                return;
            }
        }
        currentFolder = currentFolder.toAbsolutePath().getParent();
    }

    public boolean goToSubFolder(String folder){
        Path newFolder = Paths.get(currentFolder.toString(), folder);
        if(Files.exists(newFolder)){
            currentFolder = newFolder;
            return true;
        }
        return false;
    }

    public boolean changeDir(String folder){
        if(folder==null){
            return true;
        }
        Path newPath = Paths.get(folder);
        if(serverSide) {
            newPath = Paths.get(currentFolder.toString(), folder);
        }
        else if(!newPath.isAbsolute()){
            newPath = newPath.toAbsolutePath();
        }
        if(Files.exists(newPath)){
            currentFolder = newPath;
            return true;
        }
        return false;
    }

    public List<FileInfo> getFoldersList(){
        return getFullList(DIRECTORIES);
    }

    public List<FileInfo> getFilesList(){
        return getFullList(FILES);
    }

    public List<FileInfo> getFullList(){
        return getFullList(FILES | DIRECTORIES);
    }

    public List<FileInfo>  getFilesInDirForDelete(Path dirPath){

        List<FileInfo> fileList = new ArrayList<>();

        if(!dirPath.isAbsolute()){
            dirPath = getAbsolutePathToFile(dirPath);
        }

        log.debug("Find all files in directory {}", dirPath);

        File filePath = new File(String.valueOf(dirPath));
        for (File f: filePath.listFiles()) {
            if(f.isFile()){
                fileList.add(new FileInfo(FileTypes.FILE, f.getPath().toString(), 0));
            }
        }
        for (File f: filePath.listFiles()) {
            if(f.isDirectory()){
                fileList.add(new FileInfo(FileTypes.DIRECTORY, f.getPath().toString(), 0));
            }
        }
        return fileList;
    }

    public List<FileInfo> getFilesInDir(Path dir){

        List<FileInfo> fileList = new ArrayList<>();

        try {

            if(!dir.isAbsolute()){
                dir = getAbsolutePathToFile(dir);
            }

            log.debug("Find all files in directory {}", dir);

            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    Path relPath;
                    if(serverSide){
                        relPath = userFolder.relativize(file);
                    }else{
                        relPath = file;
                    }

                    fileList.add(new FileInfo(FileTypes.FILE, relPath.toString(), attrs.size()));
                    log.debug("File add in list {}", file.toString());
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException e) {
            log.error("e=", e.toString());
            return fileList;
        }


        return fileList;
    }

    public List<FileInfo> getSubFoldersInDir(Path dir){

        List<FileInfo> folderList = new ArrayList<>();

        try {

            if(!dir.isAbsolute()){
                dir = getAbsolutePathToFile(dir);
            }
            log.debug("Find all subfolders in directory {}", dir);

            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path relPath;
                    if(serverSide){
                        relPath = userFolder.relativize(dir);
                    }else{
                        relPath = dir;
                    }
                    folderList.add(new FileInfo(FileTypes.DIRECTORY, relPath.toString(), attrs.size()));
                    log.debug("Subfolder add in list {}", dir.toString());
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException e) {
            log.error("e=", e.toString());
            return folderList;
        }


        return folderList;
    }

    public List<FileInfo> getFullList(int listType){

        List<FileInfo> fileList = new ArrayList<>();

        fileList.add(new FileInfo(FileTypes.ROOT_DIRECTORY, ".", 0));
        if(!serverSide){
            if(!currentFolder.getRoot().equals(currentFolder)){
                fileList.add(new FileInfo(FileTypes.PARENT_DIRECTORY, "..", 0));
            }
        }else{
            if(!currentFolder.equals(userFolder)){
                fileList.add(new FileInfo(FileTypes.PARENT_DIRECTORY, "..", 0));
            }
        }

        try {
            Files.walkFileTree(currentFolder, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    if(Files.isDirectory(file)){
                        fileList.add(new FileInfo(FileTypes.DIRECTORY, file.getFileName().toString(), 0));
                    }
                    else{
                        fileList.add(new FileInfo(FileTypes.FILE, file.getFileName().toString(), 0));
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    if(listType >=2) {
                        fileList.add(new FileInfo(FileTypes.FILE, file.getFileName().toString(), attrs.size()));
                        return FileVisitResult.CONTINUE;
                    }else{
                        return FileVisitResult.CONTINUE;
                    }
                }
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
                {
                    if (dir.equals(currentFolder)) {
                        return FileVisitResult.CONTINUE;
                    } else {
                        if(listType == 1 || listType >= 3) {
                            fileList.add(new FileInfo(FileTypes.DIRECTORY, dir.getFileName().toString(), attrs.size()));
                        }
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                }
            });
        } catch (IOException e) {
            log.error("e=", e.toString());
            return fileList;
        }
        return fileList;

    }

    public boolean delete(Path filePath) throws IOException {
        Path abs = filePath;
        if(!filePath.isAbsolute()){
            abs = getAbsolutePathToFile(filePath);
        }
        Files.delete(abs);
        return true;
    }

    public boolean createFile(String file){
        try {
            Path fName= Paths.get(currentFolder.toString(), file);
            Files.createFile(fName);
        } catch (IOException e) {
            log.error("e=", e);
            return false;
        }
        return true;
    }

    public boolean makeDir(Path newDir) throws IOException {
        log.debug("Make dir {}", newDir.toString());
        if(newDir.isAbsolute()){
            Files.createDirectory(newDir);
        }else{
            Path absPath = getAbsolutePathToFile(newDir);
            log.debug("Transform to absolute path {}", absPath.toString());
            Files.createDirectory(absPath);
        }
        return true;
    }

    public Path getUserFolder() {
        return userFolder;
    }


    public boolean pathExists(Path path) {
        if(path.isAbsolute()){
            return Files.exists(path);
        }else{
            return Files.exists(getAbsolutePathToFile(path));
        }
    }

    public boolean move(String oldName, String newName) throws IOException {

        Path oldPath = Paths.get(currentFolder.toString(), oldName);
        Path newPath = Paths.get(currentFolder.toString(), newName);

        Files.move(oldPath, newPath);

        return true;

    }

    public void goToRoot(){
        if(serverSide){
            currentFolder = userFolder;
        }else {
            currentFolder = currentFolder.getRoot();
        }
    }

    public Path getAbsolutePathToFile(String fName){
        if(Paths.get(fName).isAbsolute()){
            return Paths.get(fName);
        }else{
            return Paths.get(currentFolder.toString(), fName);
        }
    }

    public Path getAbsolutePathToFile(Path fName){
        if(fName.isAbsolute()){
            return fName;
        }else{
            return Paths.get(currentFolder.toString(), fName.toString());
        }
    }

    public Message getFilePart(Path absFilePath) throws IOException {

        if(state == FileSystemStates.WRITEFILES){
            throw new IOException("Filesystem expected write data");
        }

        if(sbCh == null){
            sbCh = Files.newByteChannel(absFilePath, StandardOpenOption.READ);
            partNum = 0;
        }
        long fSize = Files.size(absFilePath);
        ByteBuffer data = ByteBuffer.allocate(Options.CHUNK_SIZE);
        int len = sbCh.read(data);

        Message msg = Message.builder()
                .length(fSize)
                .partLen(len)
                .command(serverSide ? CommandIDs.RESPONCE_SENDFILE:CommandIDs.REQUEST_SENDFILE)
                .commandData(currentFolder.relativize(absFilePath).toString())
                .partNum(partNum++)
                .data(data.array())
                .build();

        if(len<=0 || sbCh.position() == fSize){
            log.debug("End of file");
            resetChannel();
        }

        return msg;
    }

    public Message putFilePart(Path absFilePath, Message incMes) throws IOException {

        if(state == FileSystemStates.READFILES){
            throw new IOException("Filesystem expected write data");
        }

        if(sbCh == null){
            sbCh = Files.newByteChannel(absFilePath, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            partNum = 0;
        }

        ByteBuffer wData = ByteBuffer.wrap(incMes.getData(), 0, incMes.getPartLen());
        totalTransferBytes+=incMes.getPartLen();
        sbCh.position(incMes.getPartNum()*Options.CHUNK_SIZE);
        int len = sbCh.write(wData);

        Message msg = Message.builder()
                .length(len)
                .partLen(len)
                .command(serverSide ? CommandIDs.RESPONCE_FILERECIEVED:CommandIDs.REQUEST_RECEIVEFILE )
                .commandData(incMes.getCommandData())
                .partNum(incMes.getPartNum())
                .build();

        log.debug("Saved part {}, total transfer bytes {}, file size {}", incMes.getPartNum(), totalTransferBytes, incMes.getLength());

        if(len<=0 || totalTransferBytes == incMes.getLength()){
            log.debug("End of file");
            resetChannel();
        }

        return msg;

    }

    public void resetChannel(){
        log.debug("Reset channel");
        try {
            if(sbCh!=null) {
                sbCh.close();
            }
        } catch (IOException e) {
            log.error(e.toString());
        }
        state = FileSystemStates.IDLE;
        sbCh = null;
        partNum = 0;
        totalTransferBytes = 0;
    }

    public boolean isChannelReady(){
        return sbCh!=null && sbCh.isOpen();
    }


    public void prepareToDelete(List<FileInfo> listFromClient) {

        fileList = new LinkedList<>();
        List<FileInfo> buf;

        for (FileInfo fi:listFromClient) {
            if(fi.getFileType() == FileTypes.DIRECTORY){
                buf = getFilesInDir(Paths.get(fi.getName()));
                fileList.addAll(buf);
                totalFilesInList += buf.size();
                buf.clear();
                buf = getSubFoldersInDir(Paths.get(fi.getName()));
                Collections.reverse(buf);
                fileList.addAll(buf);
                totalFilesInList += buf.size();
                buf.clear();
            }else{
                fileList.add(new FileInfo(FileTypes.FILE, fi.getName(), fi.getSize()));
                totalFilesInList++;
            }
        }

    }

    public Message deleteFromFileList() {

        FileInfo fi = new FileInfo(FileTypes.FILE, "", 0);

        for (int i = 0; i < 10; i++) {

            if(fileList.isEmpty()){
                Message srvMes = Message.builder()
                        .command(CommandIDs.RESPONCE_OK)
                        .commandData(fi.getName())
                        .build();
                return srvMes;
            }

            fi = fileList.remove();

            try {
                log.debug("Delete file/dir {}", fi.getName());
                delete(Paths.get(fi.getName()));
            } catch (IOException e) {
                log.error("Delete error: {}" + e.toString());
                Message srvMes = Message.builder()
                        .command(CommandIDs.RESPONCE_SERVERERROR)
                        .commandData(e.toString())
                        .build();
                return srvMes;
            }

        }

        Message srvMes = Message.builder()
                .command(CommandIDs.RESPONCE_DELETEPROGRESS)
                .commandData(fi.getName())
                .partLen(totalFilesInList)
                .partNum(totalFilesInList - fileList.size())
                .build();

        return srvMes;

    }
}
