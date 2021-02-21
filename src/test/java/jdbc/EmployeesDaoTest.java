package jdbc;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import javax.sql.DataSource;


class EmployeesDaoTest {

    private EmployeesDao employeesDao;

    @BeforeEach
    public void init() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL("jdbc:mysql://localhost:3306/employees?useUnicode=true");
        dataSource.setUser("employees");
        dataSource.setPassword("employees");

        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);

        //flyway.clean();
        flyway.migrate();
        System.out.println("Kész!");

        employeesDao = new EmployeesDao(dataSource);
    }

    @Test
    public void testInsert() {

    }

    @Test
    void listEmployeeNames() {
    }

    @Test
    void findEmployeeNameById() {
    }
}