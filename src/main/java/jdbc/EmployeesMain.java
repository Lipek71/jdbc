package jdbc;


import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

public class EmployeesMain {


    public static void main(String[] args) {

        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL("jdbc:mysql://localhost:3306/employees?useUnicode=true");
        dataSource.setUser("employees");
        dataSource.setPassword("employees");

        /*try (
                Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("INSERT INTO employees(emp_name) VALUES ('John Doe')");

        } catch (SQLException se) {
            throw new IllegalStateException("Can't insert!");
        }*/
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO employees(emp_name) VALUES (?)")) {

            stmt.setString(1, "Jack Doe");
            stmt.executeUpdate();

        } catch (SQLException se) {
            throw new IllegalStateException("Can't insert!");
        }


        EmployeesDao employeesDao = new EmployeesDao(dataSource);
        employeesDao.createEmployee("John Doe");

        List<String> names = employeesDao.listEmployeeNames();
        System.out.println(names);

        String name = employeesDao.findEmployeeNameById(4L);
        System.out.println(name);
    }
}
