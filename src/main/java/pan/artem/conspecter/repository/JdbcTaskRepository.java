package pan.artem.conspecter.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pan.artem.conspecter.dto.TaskDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@AllArgsConstructor
@Repository
public class JdbcTaskRepository implements TaskRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public int create(String text, String answer, int conspectId) {
        jdbcTemplate.update(
                "INSERT INTO tasks (text, answer, conspect_id) VALUES (?, ?, ?)",
                text,
                answer,
                conspectId
        );
        return jdbcTemplate.query(      // FIXME: probably not the best way to obtain new id
                "SELECT id FROM tasks WHERE text = ? and answer = ? and conspect_id = ?",
                (ResultSet row, int colNum) -> row.getInt(1),
                text,
                answer,
                conspectId
        ).stream().findAny().orElse(0);
    }

    @Override
    public List<TaskDto> findUnsolved(int conspectId, String username) {
        return jdbcTemplate.query(
                "SELECT id, text, answer " +
                        "FROM tasks WHERE conspect_id = ? and id NOT IN " +
                        "(SELECT task_id FROM user_task WHERE username = ?)",
                this::mapRowToTaskDto,
                conspectId,
                username
        );
    }

    @Override
    public void deleteTasksFromRepo(int repoId) {
        jdbcTemplate.update(
                "DELETE FROM tasks WHERE tasks.id IN (SELECT t.id " +
                        "FROM tasks t INNER JOIN conspects ON conspects.id = conspect_id " +
                        "WHERE repo_id = ?)",
                repoId
        );
    }

    private TaskDto mapRowToTaskDto(ResultSet row, int colNum) throws SQLException {
        return new TaskDto(
                row.getInt("id"),
                row.getString("text"),
                row.getString("answer")
        );
    }
}
