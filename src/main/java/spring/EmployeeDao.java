package spring;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

public class EmployeeDao {

    private final JdbcTemplate jdbcTemplate;

    public EmployeeDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createEmployee(String name) {
        jdbcTemplate.update("INSERT INTO `employees`(emp_name) VALUES (?);", name);
    }

    public long createEmployeeWithId(String name) {
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO `employees`(emp_name) VALUES (?);", Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, name);
            return statement;
        }, holder);
        return Objects.requireNonNull(holder.getKey()).longValue();
    }

    public List<String> listEmployeeNames() {
        return jdbcTemplate.query("SELECT `emp_name` FROM `employees` ORDER BY `id`", (resultSet, rowNumber) -> resultSet.getString("emp_name"));
    }

    public String findEmployeeNameById(long id) {
        return jdbcTemplate.queryForObject("SELECT `emp_name` FROM `employees` WHERE `id` = ?",
                (resultSet, rowNum) -> resultSet.getString("emp_name"), id);
    }

}