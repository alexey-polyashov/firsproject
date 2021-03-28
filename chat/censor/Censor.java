package chat.censor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


//Порядок составления файла:
//в каждой строке указывается правильное слово
//используя разделитель <- можно указать несколько неправильных вариантов написания слова, которые будут приведены к правильному варианту
//в вариантах неправильного написания слова регистр не имеет значения
//правильное написание слова должно быть написано с учетом регистра

public class Censor implements CensService{

    private final String fname;
    private Map<String, String> dictionary = new HashMap<>();

    public Censor(String fname) {
        this.fname = fname;
    }

    @Override
    public void load() {
        try(BufferedReader br = new BufferedReader( new FileReader(fname))){
            String str;
            while((str = br.readLine()) != null){
                String[] words = str.split("<-");
                if(words.length>1) {
                    for(int i=1; i<words.length; i++) {
                        dictionary.put(words[i].trim().toLowerCase(), words[0].trim());
                    }
                }
                else{
                    dictionary.put(words[0].trim().toLowerCase(), words[0].trim());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String checkContent(String content) {

        String[] wirdsAr = content.split("\\b");
        for (String word: wirdsAr) {
            if(!word.trim().isEmpty()) {
                String rightWord = dictionary.get(word.toLowerCase());
                if(rightWord!=null) {
                    content = content.replaceAll(word, rightWord);
                }
            }
        }

        return content;
    }
}
