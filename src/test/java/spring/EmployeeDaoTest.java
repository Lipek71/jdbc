package spring;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = Config.class)
class EmployeeDaoTest {

    @Autowired
    private final Flyway flyway;

    @Autowired
    private final EmployeeDao employeeDao;

    EmployeeDaoTest(Flyway flyway, EmployeeDao employeeDao) {
        this.flyway = flyway;
        this.employeeDao = employeeDao;
    }

    @BeforeEach
    void setUp() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void createEmployee() {
        employeeDao.createEmployee("John Doe");
        List<String> result = employeeDao.listEmployeeNames();
        assertEquals(1, result.size());
    }

    @Test
    void createEmployeeWithId() {
        assertEquals(1, employeeDao.createEmployeeWithId("John Doe"));
    }

    @Test
    void listEmployeeNames() {
        employeeDao.createEmployee("John Doe");
        employeeDao.createEmployee("Jane Doe");
        employeeDao.createEmployee("Jack Doe");
        employeeDao.createEmployee("Joe Doe");
        List<String> asserted = Arrays.asList("John Doe", "Jane Doe", "Jack Doe", "Joe Doe");
        assertEquals(asserted, employeeDao.listEmployeeNames());
    }

    @Test
    void findEmployeeNameById() {
        long id = employeeDao.createEmployeeWithId("Jane Doe");
        assertEquals("Jane Doe", employeeDao.findEmployeeNameById(id));
    }

}