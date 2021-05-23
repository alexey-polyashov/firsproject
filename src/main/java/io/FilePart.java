package io;

import java.io.Serializable;

public class FilePart implements Serializable {

    private String filename;
    private long len;
    private byte[] data;
    private long part;
    private boolean finish;

    public FilePart(String filename, long len, byte[] data, long part, boolean finish) {
        this.filename = filename;
        this.len = len;
        this.data = data;
        this.part = part;
        this.finish = finish;
    }

    public String getFilename() {
        return filename;
    }

    public long getLen() {
        return len;
    }

    public byte[] getData() {
        return data;
    }

    public long getPart() {
        return part;
    }

    public boolean isFinish() {
        return finish;
    }
}
