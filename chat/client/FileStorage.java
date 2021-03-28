package chat.client;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class FileStorage implements FileService{

    private String fname;

    public FileStorage(String fname) {
        this.fname = fname;
    }

    @Override
    public List<String> readLines(int lineCount) {
        List<String> content = new LinkedList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(fname))){
            String str;
            while ( (str = br.readLine()) != null){
                content.add(str);
                if(lineCount>0 && content.size()>lineCount){
                    content.remove(0);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Не найден файл " + fname);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    @Override
    public void appendText(String textLine) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fname, true))) {
            bw.write(textLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
