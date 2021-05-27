package server;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FileSystem {

    private String currentPath;

    public FileSystem(String currentPath) {
        this.currentPath = currentPath;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    public String getFiles(SocketChannel channel) throws IOException {
        StringBuilder answer = new StringBuilder();
        Path p = Paths.get(currentPath);
        AtomicInteger files = new AtomicInteger();
        Files.walk(p)
                .forEach(x->{files.getAndIncrement();answer.append(x.toString() + "\n\r");});
        answer.append("----- total files ").append(files).append(" - ok\n\r");
        return answer.toString();
    }

    public String getCatalogFiles(SocketChannel channel, String[] cmdparts) throws IOException {
        String answer = "";
        int lines = 0;
        if(cmdparts.length<2){
            return "Wrong params: file name expected!\n\r";
        }
        if(cmdparts[1].isEmpty()){
            return "Wrong format!\n\r";
        }        Path p = Paths.get(currentPath + cmdparts[1]);
        List<String> ls = Files.readAllLines(p);
        for (String str:ls) {
            lines++;
            answer+=str + "\n\r";
        }
        answer+="----- total lines "+lines;
        return answer;
    }

    public String createCatalog(SocketChannel channel, String[] cmdparts) throws IOException {
        String answer = "";
        if(cmdparts.length<2){
            return "Wrong params: directory name expected!\n\r";
        }
        if(cmdparts[1].isEmpty()){
            return "Wrong format!\n\r";
        }
        Path p = Paths.get(currentPath + cmdparts[1]);
        Files.createDirectory(p);
        answer+="Directory create - ok";
        return answer;
    }

    public String createFile(SocketChannel channel, String[] cmdparts) throws IOException {
        String answer = "";
        if(cmdparts.length<2){
            return "Wrong params: file name expected!\n\r";
        }
        if(cmdparts[1].isEmpty()){
            return "Wrong format!\n\r";
        }
        Path p = Paths.get(currentPath + cmdparts[1]);
        Files.createFile(p);
        answer+="File create - ok";
        return answer;
    }

    public String pushMessageToFile(SocketChannel channel, String[] cmdparts) throws IOException {
        String answer = "";
        if(cmdparts.length<2){
            return "Wrong params: 1 - message expected!\n\r";
        }
        String newtext = "";
        int i;
        for(i = 1; i<cmdparts.length; i++) {
            if(cmdparts[i].equals("to")) break;
            newtext+=cmdparts[i];
        }
        if(i==cmdparts.length){
            return "Wrong params: TO expected or file name is empty!\n\r";
        }
        if(cmdparts[i++].isEmpty()){
            return "Wrong params: 2 - file name expected!\n\r";
        }
        Path p = Paths.get(currentPath + cmdparts[i]);
        Files.write(p, cmdparts[1].getBytes(StandardCharsets.UTF_8));
        answer+="File change - ok";
        return answer;
    }
}
