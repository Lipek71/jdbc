package jdbc;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmployeesDao {

    private DataSource dataSource;

    public EmployeesDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public long createEmployee(String name) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO employees(emp_name) VALUES (?)"
                        , Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.executeUpdate();

            return getIdByStatement(stmt);

        } catch (SQLException se) {
            throw new IllegalStateException("Can't insert!");
        }
    }

    public void createEmployees(List<String> names){
        try (Connection conn = dataSource.getConnection()){
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO employees(emp_name) VALUES (?)")){
                for(String name:names){
                    if (name.startsWith("x")){
                        throw new IllegalArgumentException("Invalid name");
                    }
                    stmt.setString(1, name);
                    stmt.executeUpdate();
                }
                conn.commit();
            } catch (IllegalArgumentException iae){
                conn.rollback();
            }
        } catch (SQLException sqle){
            throw new IllegalStateException("Can't insert", sqle);
        }
    }

    private long getIdByStatement(PreparedStatement stmt) {
        try (
                ResultSet rs = stmt.getGeneratedKeys()
                ) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            throw new IllegalStateException("Can't get id");
        } catch (SQLException sqle){
            throw new IllegalStateException("Can't get id", sqle);
        }
    }

    public List<String> listEmployeeNames() {
        try (
                Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT emp_name FROM employees")) {
            List<String> names = new ArrayList<>();
            while (rs.next()) {
                String name = rs.getString("emp_name");
                names.add(name);
            }
            return names;
        } catch (SQLException se) {
            throw new IllegalStateException("Can't select employees", se);
        }
    }

    public List<String> listOddEmployeeNames(){
        try (
                Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = stmt.executeQuery("SELECT emp_name FROM employees ORDER BY emp_name")) {

            if (!rs.next()){
                return Collections.EMPTY_LIST;
            }
            List<String> names = new ArrayList<>();
            names.add(rs.getString("emp_name"));
            while (rs.relative(2)) {
                names.add(rs.getString("emp_name"));
            }
            return names;

        } catch (SQLException se) {
            throw new IllegalStateException("Can't list names", se);
        }
    }

    public void updateNames(){
        try (
                Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet rs = stmt.executeQuery("SELECT id, emp_name FROM employees")){

            while (rs.next()){
                String name = rs.getString("emp_name");
                if (!name.startsWith("Jane")){
                    rs.updateString("emp_name", "Mr. " + name);
                    rs.updateRow();
                }
            }

        } catch (SQLException se){
            throw new IllegalStateException("Can't update names", se);
        }
    }

    public String findEmployeeNameById(long id) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT emp_name FROM employees WHERE id = ?")
        ) {
            ps.setLong(1, id);

            return selectNameByPreparedStatement(ps);

        } catch (SQLException sqle) {
            throw new IllegalStateException("Can't query", sqle);
        }
    }

    private String  selectNameByPreparedStatement(PreparedStatement ps) {
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String name = rs.getString("emp_name");
                return  name;
            }
            throw new IllegalArgumentException("Not found");
        } catch (SQLException sqle) {
            throw new IllegalStateException("Can't query", sqle);
        }
    }
}
