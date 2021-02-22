package spring;

import org.flywaydb.core.Flyway;
import org.mariadb.jdbc.MariaDbDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class Config {


    @Bean
    public DataSource dataSource() {
        MariaDbDataSource dataSource = new MariaDbDataSource();
        try {
            dataSource.setUrl("jdbc:mariadb://localhost:3306/employees?useUnicode=true");
            dataSource.setUser("employees");
            dataSource.setPassword("employees");
            return dataSource;
        } catch (SQLException se) {
            throw new IllegalStateException("Can't connect to database", se);
        }
    }

    @Bean
    public Flyway flyway() {
        return Flyway.configure().dataSource(dataSource()).load();
    }

    @Bean
    public EmployeeDao employeeDao() {
        return new EmployeeDao(dataSource());
    }
}
