package pan.artem.conspecter.repository;

import pan.artem.conspecter.dto.ConspectRepo;

import java.util.List;

public interface ConspectRepoRepository {

    int getId(String author, String pathName);

    List<ConspectRepo> findAll();

    void create(String fullName, String author, String pathName);

    void removeEmpty();
}
