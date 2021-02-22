package jdbc;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MetaDataDaoTest {

    private MetaDataDao metaDataDao;

    @BeforeEach
    public void init() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL("jdbc:mysql://localhost:3306/employees?useUnicode=true");
        dataSource.setUser("employees");
        dataSource.setPassword("employees");

        Flyway flyway = Flyway.configure().dataSource(dataSource).load();

        flyway.clean();
        flyway.migrate();
        System.out.println("Kész!");

        metaDataDao = new MetaDataDao(dataSource);
    }

    @Test
    void testTableNames() {
        List<String> names = metaDataDao.getTableNames();
        System.out.println(names);

        assertTrue(names.contains("employees"));
    }
}