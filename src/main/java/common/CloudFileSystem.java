package common;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CloudFileSystem {

    final static int FILES = 2;
    final static int DIRECTORIES = 1;
    boolean serverSide;

    private SeekableByteChannel sbCh;
    private int partNum = 0;
    private long totalTransferBytes = 0;
    private FileSystemStates state = FileSystemStates.IDLE;

    private Path userFolder; //absolute path, may be null
    private Path currentFolder;

    public CloudFileSystem(Path currentFolder, Path userFolder, boolean serverSide) {

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

    public boolean delete(Path filePath) throws NoSuchFileException{
        try {
            if(!Files.exists(filePath)){
                throw new NoSuchFileException(filePath.toString());
            }
            Files.delete(filePath);
        } catch (IOException e) {
            log.error("e=", e);
            return false;
        }
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

    public boolean makeDir(Path newDir){
        try {
            Files.createDirectory(newDir.toAbsolutePath());
        } catch (IOException e) {
            log.error("e=", e);
            return false;
        }
        return true;
    }

    public Path getUserFolder() {
        return userFolder;
    }


    public boolean pathExists(Path path) {
        return Files.exists(path);
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
        return Paths.get(currentFolder.toString(), fName);
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
                .commandData(absFilePath.getFileName().toString())
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



}
