package common;

import lombok.Data;

import java.io.Serializable;

@Data
public class FileInfo implements Serializable {

    private FileTypes fileType;
    private String icon;
    private String name;
    private long size;

    public FileInfo(FileTypes fileType, String name, long size) {
        this.fileType = fileType;
        this.name = name;
        this.size = size;
        if(fileType == FileTypes.FILE){
            this.icon = "";
        }else{
            this.icon = "DIR";
        }
    }

    @Override
    public String toString() {
        return "[" + fileType + "]" + name + "( size=" + size + ")";
    }

}
