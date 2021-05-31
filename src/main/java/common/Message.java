package common;

import java.io.Serializable;

public class Message implements Serializable {
    public int length = 0;
    public CommandIDs command;
    public String commandData;
    public byte[] data;
    public int partNum = 0;
}
