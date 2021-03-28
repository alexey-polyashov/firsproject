package chat.client;

import java.util.List;

public interface FileService {

    public List<String> readLines(int lineCount);

    void appendText(String textLine);


}
