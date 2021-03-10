package activitytracker;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MetaDataDao {
    private DataSource dataSource;

    public MetaDataDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<String> getTableNames(String table){
        try(
                Connection conn = dataSource.getConnection();
        ){
            DatabaseMetaData metaData = conn.getMetaData();
            return getTableNamesByMetaData(metaData, table);
        } catch (SQLException se){
            throw new IllegalStateException("Can't read table names", se);
        }
    }

    private List<String> getTableNamesByMetaData(DatabaseMetaData metaData, String table) throws SQLException {
        try (
                ResultSet rs = metaData.getColumns("activitytracker",null,table,null )
        ) {
            List<String> names = new ArrayList<>();
            while (rs.next()){
                String name = rs.getString(4);
                names.add(name);
            }
            return names;
        }
    }

    public static void main(String[] args) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL("jdbc:mysql://localhost:3306/activitytracker?useUnicode=true");
        dataSource.setUser("activitytracker");
        dataSource.setPassword("activitytracker");

        MetaDataDao metaDataDao = new MetaDataDao(dataSource);

        System.out.println(metaDataDao.getTableNames("images"));
    }
}
