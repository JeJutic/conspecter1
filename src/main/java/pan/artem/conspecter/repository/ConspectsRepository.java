package pan.artem.conspecter.repository;

import pan.artem.conspecter.dto.ConspectDto;

import java.util.List;

public interface ConspectsRepository {

    List<ConspectDto> findAll(String username, int repoId);
}
