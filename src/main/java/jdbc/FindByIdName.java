package jdbc;


import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FindByIdName {

    public void selectNameByPreparedStatement(PreparedStatement ps) {
        try (ResultSet rs = ps.executeQuery()){
            if (rs.next()){
                String name = rs.getString("emp_name");
                System.out.println(name);
            }
            throw new IllegalArgumentException("Not found");
        } catch (SQLException sqle) {
            throw new IllegalStateException("Can't query", sqle);
        }
    }

    public void selectNameById(DataSource ds, long id) {
        try (
                Connection conn = ds.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT emp_name FROM employees WHERE id = ?")
        ) {
            ps.setLong(1, id);

            selectNameByPreparedStatement(ps);

        } catch (SQLException sqle) {
            throw new IllegalStateException("Can't query", sqle);
        }

    }

    public static void main(String[] args) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL("jdbc:mysql://localhost:3306/employees?useUnicode=true");
        dataSource.setUser("employees");
        dataSource.setPassword("employees");

        new FindByIdName().selectNameById(dataSource, 4);
    }
}
