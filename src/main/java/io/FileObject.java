package io;

import java.io.Serializable;

public class FileObject implements Serializable {
    private String name;
    private long len;
    private byte[] data;
}
