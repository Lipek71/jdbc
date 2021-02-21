package jdbc;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class ImagesDAOTest {

    private ImagesDAO imagesDAO;

    @BeforeEach
    public void init() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL("jdbc:mysql://localhost:3306/employees?useUnicode=true");
        dataSource.setUser("employees");
        dataSource.setPassword("employees");

        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);

        flyway.clean();
        flyway.migrate();
        System.out.println("Kész!");

        imagesDAO = new ImagesDAO(dataSource);
    }

    @Test
    void saveImageTest() throws IOException {
        imagesDAO.saveImage("training360.gif", ImagesDAOTest.class.getResourceAsStream("/training360.gif"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();


        try (InputStream is = imagesDAO.getImageByName("training360.gif")){
            is.transferTo(baos);
        }
        assertTrue(baos.size() > 5000);
    }


}