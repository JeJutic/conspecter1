package pan.artem.conspecter.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pan.artem.conspecter.dto.ConspectRepo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@AllArgsConstructor
@Repository
public class JdbcConspectRepoRepository implements ConspectRepoRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<ConspectRepo> findAll() {
        return jdbcTemplate.query(
                "SELECT id, fullName FROM repos ORDER BY id",
                this::mapRowToConspectRepo
        );
    }

    private ConspectRepo mapRowToConspectRepo(ResultSet row, int colNum) throws SQLException {
        return new ConspectRepo(
                row.getInt("id"),
                row.getString("fullName"),
                null,
                null
        );
    }

    @Override
    public void create(ConspectRepo conspect) {
        String sqlQuery = "INSERT INTO repos (fullName, author, pathName) VALUES (?, ?, ?)";
        jdbcTemplate.update(
                sqlQuery,
                conspect.getFullName(),
                conspect.getAuthor(),
                conspect.getPathName()
        );
    }
}
