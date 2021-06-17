package common;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Message implements Serializable {
    private long length = 0;
    private CommandIDs command;
    private String commandData;
    private String commandData2;
    private byte[] data;
    private int partNum = 0;
    private int partLen = 0;
}
