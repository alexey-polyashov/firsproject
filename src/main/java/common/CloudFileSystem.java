package common;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CloudFileSystem {

    final static int FILES = 2;
    final static int DIRECTORIES = 1;

    private Path userFolder;
    private Path currentFolder;

    public CloudFileSystem(Path currentFolder, Path userFolder) {
        this.currentFolder = currentFolder.toAbsolutePath();
        this.userFolder = userFolder.toAbsolutePath();
    }

    public void setUserFolder(Path userFolder){
        this.userFolder = userFolder.toAbsolutePath();
    }

    public Path getCurrentFolder() {
        return currentFolder;
    }

    public void goToParentFolder(){
        if(!userFolder.equals(currentFolder)){
            currentFolder = currentFolder.toAbsolutePath().getParent();
        }
    }

    public boolean goToSubFolder(String folder){
        Path newFolder = Paths.get(currentFolder.toString(), folder);
        if(Files.exists(newFolder)){
            currentFolder = newFolder;
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
        if(!currentFolder.equals(userFolder)){
            fileList.add(new FileInfo(FileTypes.PARENT_DIRECTORY, "..", 0));
        }

        try {
            Files.walkFileTree(currentFolder, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException
                {
                    if(listType >=2) {
                        fileList.add(new FileInfo(FileTypes.FILE, file.getFileName().toString(), attrs.size()));
                        return FileVisitResult.CONTINUE;
                    }else{
                        return FileVisitResult.CONTINUE;
                    }
                }
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                        throws IOException
                {
                    if (dir.equals(userFolder)) {
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
            log.error("e=", e);
            return fileList;
        }
        return fileList;

    }

    public boolean delete(Path file){
        try {
            Files.delete(file);
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

    public boolean makeDir(String newDir){
        try {
            Path fName= Paths.get(currentFolder.toString(), newDir);
            Files.createDirectory(fName);
        } catch (IOException e) {
            log.error("e=", e);
            return false;
        }
        return true;
    }

}
