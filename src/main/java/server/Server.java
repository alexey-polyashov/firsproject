package server;

import javafx.beans.binding.StringBinding;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public class Server {

    private ServerSocketChannel serverSocketChannel;
    private int port;
    private String dir;

    FileSystem fs;

    private Selector selector;
    private ByteBuffer buffer;

    public Server(int port, String dir) {
        this.port = port;
        this.dir = dir;
        fs = new FileSystem(dir);
    }

    public Server() {
        this.port = InitParams.SERVER_PORT;
        this.dir = InitParams.SERVER_DEFAULT_FOLDER;
        fs = new FileSystem(dir);
    }

    public void stop() throws IOException {
        serverSocketChannel.close();
    }

    public void start() throws IOException {
        this.buffer = ByteBuffer.allocate(100);
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(this.port));
        serverSocketChannel.configureBlocking(false);
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while(serverSocketChannel.isOpen()){
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> keys = selectionKeys.iterator();
            while(keys.hasNext()){
                SelectionKey key = keys.next();
                if(key.isAcceptable()){
                    makeAccept(key);
                }
                if(key.isReadable()){
                    makeRead(key);
                }
                keys.remove();
            }

        }
    }

    private void makeRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        StringBuilder str = new StringBuilder();
        int bytes;
        while(true){
            bytes = channel.read(buffer);
            if(bytes == -1){
                channel.close();
                return;
            }
            if(bytes == 0){
                break;
            }
            buffer.flip();
            while(buffer.hasRemaining()){
                str.append((char)buffer.get());
            }
            buffer.clear();
        }
        parseCommand(str.toString(), channel);
    }

    private void parseCommand(String cmdline, SocketChannel channel) throws IOException {
        String[] cmdparts = cmdline.split("\\s");
        if(cmdparts.length == 0){
            return;
        }
        String cmd = cmdparts[0].toLowerCase(Locale.ROOT);
        String answer;
        try {
            if (cmd.equals("ls")) {
                answer = fs.getFiles(channel);
            } else if (cmd.equals("cat")) {
                answer = fs.getCatalogFiles(channel, cmdparts);
            } else if (cmd.equals("mkdir")) {
                answer = fs.createCatalog(channel, cmdparts);
            } else if (cmd.equals("touch")) {
                answer = fs.createFile(channel, cmdparts);
            } else if (cmd.equals("read")) {
                answer = fs.pushMessageToFile(channel, cmdparts);
            } else {
                answer = "Unknown command";
            }
        }
        catch (Exception e){
            answer = "Error: " + e.toString();
        }
        answer+="\n\r" + fs.getCurrentPath();

        channel.write(ByteBuffer.wrap(answer.getBytes(StandardCharsets.UTF_8)));

    }

    private void makeAccept(SelectionKey key) throws IOException {
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

}
