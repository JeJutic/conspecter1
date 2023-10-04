package pan.artem.conspecter.service;

import java.io.IOException;
import java.text.ParseException;

public interface ConspectRepoMaintainer {

    void loadConspectRepo(String url, String fullName, String author, String pathName) throws IOException, InterruptedException;

    void reloadConspectRepo(String author, String pathName) throws IOException, InterruptedException, ParseException;
}
