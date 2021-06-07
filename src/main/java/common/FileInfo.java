package common;

import java.io.Serializable;

public class FileInfo implements Serializable {

    public FileTypes fileType;
    public String icon;
    public String name;
    public long size;

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
