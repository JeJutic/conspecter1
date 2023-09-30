package pan.artem.conspecter.repository;

import pan.artem.conspecter.dto.ConspectRepo;

import java.util.List;

public interface ConspectRepoRepository {

    List<ConspectRepo> findAll();

    void create(ConspectRepo conspect);
}
