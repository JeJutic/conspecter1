package pan.artem.conspecter.repository;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pan.artem.conspecter.dto.ConspectDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@AllArgsConstructor
@Repository
public class JdbcConspectsRepository implements ConspectsRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final JdbcTemplate jdbcTemplate;

    private int getId(String path, int repoId) {
        var ids = jdbcTemplate.query(
                "SELECT id FROM conspects WHERE path = ? and repo_id = ?",
                (ResultSet row, int colNum) -> row.getInt(1),
                path,
                repoId
        );
        if (ids.isEmpty()) {
            return -1;
        }
        return ids.get(0);
    }


    @Override
    public int getIdOrCreate(String path, int repoId) {
        logger.info("Getting Id of conspect with path {} and repo id {}", path, repoId);

        int id = getId(path, repoId);
        if (id == -1) {
            jdbcTemplate.update(
                    "INSERT INTO conspects (path, repo_id) VALUES (?, ?)",
                    path,
                    repoId
            );
            id = getId(path, repoId);
            logger.info("Created conspect with id: {}", id);
        }
        return id;
    }

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
