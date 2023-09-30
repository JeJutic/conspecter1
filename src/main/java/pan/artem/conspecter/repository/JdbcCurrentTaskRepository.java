package pan.artem.conspecter.repository;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pan.artem.conspecter.dto.TaskDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class JdbcCurrentTaskRepository implements CurrentTaskRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void setTask(String username, int taskId) {
        jdbcTemplate.update(
                "INSERT INTO current_task (username, task_id) VALUES (?, ?)",
                username,
                taskId
        );
    }

    @Override
    public Optional<TaskDto> getTask(String username) {
        return jdbcTemplate.query(
                "SELECT tasks.id as id, text, answer " +
                        "FROM current_task INNER JOIN tasks " +
                        "ON current_task.task_id = tasks.id",
                this::mapRowToTaskDto
        ).stream().findAny();
    }

    private TaskDto mapRowToTaskDto(ResultSet row, int colNum) throws SQLException {
        return new TaskDto(
                row.getInt("id"),
                row.getString("text"),
                row.getString("answer")
        );
    }

    @Override
    public void closeTask(String username, boolean success) {
        if (success) {
            jdbcTemplate.update(
                    "INSERT INTO user_task (username, task_id) " +
                            "(SELECT username, task_id FROM current_task " +
                            "WHERE username = ?)",
                    username
            );
        }
        jdbcTemplate.update(
                "DELETE FROM current_task WHERE username = ?",
                username
        );
    }
}
