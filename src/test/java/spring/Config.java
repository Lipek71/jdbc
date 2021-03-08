package spring;

import org.flywaydb.core.Flyway;
import org.mariadb.jdbc.MariaDbDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class Config {

    private static final String NAME_AND_PW = "employees";
    MariaDbDataSource dataSource = new MariaDbDataSource();

    @Bean
    public DataSource dataSource() {
        dataSource = new MariaDbDataSource();
        try {
            dataSource.setUrl("jdbc:mariadb://localhost:3306/employees?useUnicode=true");
            dataSource.setUser(NAME_AND_PW);
            dataSource.setPassword(NAME_AND_PW);
            return dataSource;
        } catch (SQLException se) {
            throw new IllegalStateException("Can not connect to database", se);
        }
    }


    @Bean
    public Flyway flyway() {
        return Flyway.configure().locations("filesystem:src/main/resources/db/migration/employees").dataSource(dataSource).load();
    }

    @Bean
    public EmployeeDao employeeDao() {
        return new EmployeeDao(dataSource());
    }

}
