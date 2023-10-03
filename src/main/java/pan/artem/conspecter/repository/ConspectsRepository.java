package pan.artem.conspecter.repository;

import pan.artem.conspecter.dto.ConspectDto;

import java.util.List;

public interface ConspectsRepository {

    int getIdOrCreate(String path, int repoId);

    List<ConspectDto> findAll(String username, int repoId);
}
