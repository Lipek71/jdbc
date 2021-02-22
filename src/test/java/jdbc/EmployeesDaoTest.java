package jdbc;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class EmployeesDaoTest {

    private EmployeesDao employeesDao;

    @BeforeEach
    public void init() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL("jdbc:mysql://localhost:3306/employees?useUnicode=true");
        dataSource.setUser("employees");
        dataSource.setPassword("employees");

        Flyway flyway = Flyway.configure().dataSource(dataSource).load();

        flyway.clean();
        flyway.migrate();

        employeesDao = new EmployeesDao(dataSource);
    }

    @Test
    public void testInsert() {
        employeesDao.createEmployee("John Doe");
        employeesDao.createEmployee("Jane Doe");
        assertEquals(Arrays.asList("John Doe", "Jane Doe"), employeesDao.listEmployeeNames());
    }

    @Test
    public void testById(){
        long id = employeesDao.createEmployee("Jane Doe");
        System.out.println(id);
        id = employeesDao.createEmployee("Jack Doe");
        System.out.println(id);
        String name = employeesDao.findEmployeeNameById(id);
        assertEquals("Jack Doe", name);
    }

    @Test
    public void testCreateEmployees(){
        employeesDao.createEmployees(Arrays.asList("Jack Doe", "Jane Doe", "Joe Doe"));
        List<String> names = employeesDao.listEmployeeNames();
        assertEquals(Arrays.asList("Jack Doe", "Jane Doe", "Joe Doe"), names);
    }

    @Test
    public void testCreateEmployeesRollback(){
        employeesDao.createEmployees(Arrays.asList("Jack Doe", "Jane Doe", "xJoe Doe"));
        List<String> names = employeesDao.listEmployeeNames();
        assertEquals(Collections.emptyList(), names);
    }

    @Test
    public void testOddNames() {
        employeesDao.createEmployees(Arrays.asList("Jack Doe", "Jane Doe", "Joe Doe"));
        List<String> names = employeesDao.listOddEmployeeNames();
        assertEquals(Arrays.asList("Jack Doe", "Joe Doe"), names);
    }

    @Test
    public void testUpdateNames() {
        employeesDao.createEmployees(Arrays.asList("Jack Doe", "Jane Doe", "Joe Doe"));
        employeesDao.updateNames();
        List<String> names = employeesDao.listEmployeeNames();
        assertEquals(Arrays.asList("Mr. Jack Doe", "Jane Doe", "Mr. Joe Doe"), names);
    }
}