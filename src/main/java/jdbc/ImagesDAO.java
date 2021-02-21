package jdbc;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.sql.*;

public class ImagesDAO {

    private DataSource dataSource;

    public ImagesDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveImage(String filename, InputStream is) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO images(filename, content) VALUES (?, ?)")
        ) {
            ps.setString(1, filename);
            Blob blob = conn.createBlob();

            fillBlob(is, blob);

            ps.setBlob(2, blob);

            ps.executeUpdate();

        } catch (SQLException se) {
            throw new IllegalStateException("Can't insert image", se);
        }
    }

    public InputStream getImageByName(String name) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT content FROM images WHERE filename = ?")) {

            stmt.setString(1, name);
            return readInputStreamFromStatement(stmt);

        } catch (SQLException se) {
            throw new IllegalStateException("Can't read image", se);
        }
    }

    private InputStream readInputStreamFromStatement(PreparedStatement stmt) throws SQLException {
        try (
                ResultSet rs = stmt.executeQuery()
                ){
            if (rs.next()) {
                Blob blob = rs.getBlob("content");
                return blob.getBinaryStream();
            }
            throw new IllegalStateException("Not foun");
        }
    }

    private void fillBlob(InputStream is, Blob blob) throws SQLException {
        try (OutputStream os = blob.setBinaryStream(1)) {
            is.transferTo(os);
        } catch (IOException ioe) {
            throw new IllegalStateException("Can't write blob", ioe);
        }
    }
}
