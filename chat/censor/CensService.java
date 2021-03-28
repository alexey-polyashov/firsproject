package chat.censor;

public interface CensService {

    void load();

    String checkContent(String content);

}
