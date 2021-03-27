package censor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


//Порядок составления файла:
//в каждой строке указывается правильное слово
//используя разделитель <- можно указать несколько неправильных вариантов написания слова, которые будут приведены к правильному варианту
//в вариантах неправильного написания слова регистр не имеет значения
//правильное написание слова должно быть написано с учетом регистра

public class Censor implements CensService{

    private final String fname;
    private List<Term> dictionary = new ArrayList<>();

    private class Term {

        protected String rightWord;
        protected String wrongWord;

        public Term(String rightWord, String wrongWord) {
            this.rightWord = rightWord;
            this.wrongWord = wrongWord;
        }

    }

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
                        dictionary.add(new Term(words[0].trim(), words[i].trim()));
                    }
                }
                else{
                    dictionary.add(new Term(words[0].trim(), words[0].trim()));
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

        for (Term term:dictionary) {
            content = content.replaceAll("(?iu)\\b" + term.wrongWord + "\\b", term.rightWord);
        }

        return content;
    }
}
