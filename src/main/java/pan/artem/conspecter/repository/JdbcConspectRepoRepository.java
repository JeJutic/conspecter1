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
    public int getId(String author, String pathName) throws IllegalArgumentException {
        var ids = jdbcTemplate.query(
                "SELECT id FROM repos WHERE author = ? and pathName = ?",
                (ResultSet row, int colNum) -> row.getInt(1),
                author,
                pathName
        );
        if (ids.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return ids.get(0);
    }

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
    public void create(String fullName, String author, String pathName) {
        jdbcTemplate.update(
                "INSERT INTO repos (fullName, author, pathName) VALUES (?, ?, ?)",
                fullName,
                author,
                pathName
        );
    }

    @Override
    public void removeEmpty() {
        jdbcTemplate.update(
                "DELETE conspects FROM conspects " +
                        "LEFT JOIN tasks ON conspects.id = conspect_id " +
                        "WHERE conspect_id IS NULL"
        );
    }
}
