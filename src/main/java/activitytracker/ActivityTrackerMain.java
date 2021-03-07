package activitytracker;

import com.mysql.cj.jdbc.MysqlDataSource;
import jdbc.EmployeesDao;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ActivityTrackerMain {

    public static void insert(DataSource dataSource, LocalDateTime localDateTime, String name, ActivityType activityType) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO activities(start_time,activity_desc, activity_type) VALUES (?,?,?)")) {
            stmt.setTimestamp(1, Timestamp.valueOf(localDateTime));
            stmt.setString(2, name);
            stmt.setString(3, String.valueOf(activityType));
            stmt.executeUpdate();
        } catch (SQLException se) {
            throw new IllegalStateException("Can't insert the row!");
        }
    }

    public static void readDatabase(DataSource dataSource) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM activities WHERE 1")
        ) {

            System.out.println(writeData(stmt));

        } catch (SQLException sqle) {
            throw new IllegalStateException("Can't query", sqle);
        }

    }

    public static void findById(DataSource dataSource, int id) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM activities WHERE id = ?")
        ) {
            stmt.setLong(1, id);
            System.out.println(writeById(stmt).toString());

        } catch (SQLException sqle) {
            throw new IllegalStateException("Can't query", sqle);
        }

    }

    private static List<Activity> writeData(PreparedStatement stmt) {

        List<Activity> activities = new ArrayList<>();

        try (ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) {
                throw new IllegalArgumentException("Not found");
            }
            while (rs.next()) {
                int id = rs.getInt("id");
                Timestamp timestamp = rs.getTimestamp("start_time");
                String name = rs.getString("activity_desc");
                String type = rs.getString("activity_type");

                //System.out.println(id + ", " + timestamp + ", " + name + ", " + type);

                activities.add(new Activity(id, timestamp.toLocalDateTime(), name, ActivityType.valueOf(type), null));

            }

        } catch (SQLException sqle) {
            throw new IllegalStateException("Can't query", sqle);
        }
        return activities;
    }

    private static Activity writeById(PreparedStatement stmt) {
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int id = rs.getInt("id");
                Timestamp timestamp = rs.getTimestamp("start_time");
                String name = rs.getString("activity_desc");
                String type = rs.getString("activity_type");

                //System.out.println(id + ", " + timestamp + ", " + name + ", " + type);

                return new Activity(id, timestamp.toLocalDateTime(), name, ActivityType.valueOf(type), null);

            } else {
                throw new IllegalArgumentException("Not found");
            }

        } catch (SQLException sqle) {
            throw new IllegalStateException("Can't query", sqle);
        }
    }

    public static void main(String[] args) {

        Activity activity01 = new Activity(1, LocalDateTime.of(2021, 2, 21, 10, 0, 0), "Mátra", ActivityType.RUNNING, null);
        Activity activity02 = new Activity(1, LocalDateTime.of(2021, 2, 20, 14, 0, 0), "Gerecse", ActivityType.BIKING, null);
        Activity activity03 = new Activity(1, LocalDateTime.of(2021, 3, 1, 12, 0, 0), "Budapest", ActivityType.BASKETBALL, null);
        Activity activity04 = new Activity(1, LocalDateTime.of(2021, 3, 2, 9, 0, 0), "Mátra", ActivityType.RUNNING, null);
        Activity activity05 = new Activity(1, LocalDateTime.of(2021, 3, 4, 16, 0, 0), "Velence", ActivityType.HIKING, null);

        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL("jdbc:mysql://localhost:3306/activitytracker?useUnicode=true");
        dataSource.setUser("activitytracker");
        dataSource.setPassword("activitytracker");


        //insert(dataSource, activity01.getStartTime(), activity01.getDesc(), activity01.getType());
        //insert(dataSource, activity02.getStartTime(), activity02.getDesc(), activity02.getType());
        //insert(dataSource, activity03.getStartTime(), activity03.getDesc(), activity03.getType());
        //insert(dataSource, activity04.getStartTime(), activity04.getDesc(), activity04.getType());
        //insert(dataSource, activity05.getStartTime(), activity05.getDesc(), activity05.getType());

        System.out.println();
        findById(dataSource, 1);
        System.out.println();
        findById(dataSource, 3);

        System.out.println();
        readDatabase(dataSource);
    }

}