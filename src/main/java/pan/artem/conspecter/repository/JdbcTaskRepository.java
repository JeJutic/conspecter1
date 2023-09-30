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

    private TaskDto mapRowToTaskDto(ResultSet row, int colNum) throws SQLException {
        return new TaskDto(
                row.getInt("id"),
                row.getString("text"),
                row.getString("answer")
        );
    }
}
