package pan.artem.conspecter.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pan.artem.conspecter.dto.ConspectDto;
import pan.artem.conspecter.dto.ConspectRepo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@AllArgsConstructor
@Repository
public class JdbcConspectsRepository implements ConspectsRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<ConspectDto> findAll(String username, int repoId) {
        return jdbcTemplate.query(
                "SELECT conspects.id as id, path, " +
                        "(SELECT count(*) FROM tasks INNER JOIN user_task " +
                        "ON user_task.task_id = tasks.id WHERE tasks.conspect_id = conspects.id " +
                        "and username = ?) as score, " +
                        "(SELECT count(*) FROM tasks " +
                        "WHERE tasks.conspect_id = conspects.id) AS outOf " +
                        "FROM conspects " +
                        "WHERE repo_id=? ORDER BY id",
                this::mapRowToConspectDto,
                username,
                repoId
        );
    }

    private ConspectDto mapRowToConspectDto(ResultSet row, int colNum) throws SQLException {
        return new ConspectDto(
                row.getInt("id"),
                row.getString("path"),
                row.getInt("score"),
                row.getInt("outOf")
        );
    }
}
