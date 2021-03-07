package activitytracker;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ActivityDao {

    private final DataSource dataSource;

    public ActivityDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Activity saveActivity(Activity activity) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO activities(start_time,activity_desc, activity_type) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, Timestamp.valueOf(activity.getStartTime()));
            stmt.setString(2, activity.getDesc());
            stmt.setString(3, String.valueOf(activity.getType()));
            stmt.executeUpdate();

            activity.setId(getIdFromDb(stmt));
            return activity;

        } catch (SQLException se) {
            throw new IllegalStateException("Can't insert the row!");
        }
    }

    private int getIdFromDb(PreparedStatement stmt) throws SQLException {
        try (
                ResultSet rs = stmt.getGeneratedKeys()
                ){
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new IllegalStateException("Can't get id!");
        }
    }

    public Activity findActivityById(long id){
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM activities WHERE id = ?")
        ) {
            stmt.setLong(1, id);
            return readActivityFromDbById(stmt);

        } catch (SQLException sqle) {
            throw new IllegalStateException("Can't query", sqle);
        }
    }

    private static Activity readActivityFromDbById(PreparedStatement stmt) {
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int id = rs.getInt("id");
                Timestamp timestamp = rs.getTimestamp("start_time");
                String name = rs.getString("activity_desc");
                String type = rs.getString("activity_type");

                return new Activity(id, timestamp.toLocalDateTime(), name, ActivityType.valueOf(type), null);

            } else {
                throw new IllegalArgumentException("Not found");
            }

        } catch (SQLException sqle) {
            throw new IllegalStateException("Can't query", sqle);
        }
    }

    public List<Activity> listActivities(){
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM activities WHERE 1")
        ) {

            return readActivitiesFromDb(stmt);

        } catch (SQLException sqle) {
            throw new IllegalStateException("Can't query", sqle);
        }
    }

    private static List<Activity> readActivitiesFromDb(PreparedStatement stmt) {

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

                activities.add(new Activity(id, timestamp.toLocalDateTime(), name, ActivityType.valueOf(type), null));

            }

        } catch (SQLException sqle) {
            throw new IllegalStateException("Can't query", sqle);
        }
        return activities;
    }

    public static void main(String[] args) {

        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL("jdbc:mysql://localhost:3306/activitytracker?useUnicode=true");
        dataSource.setUser("activitytracker");
        dataSource.setPassword("activitytracker");

        ActivityDao activityDao = new ActivityDao(dataSource);

        Activity activity01 = new Activity(1, LocalDateTime.of(2021, 2, 21, 10, 0, 0), "Mátra", ActivityType.RUNNING, null);

        activityDao.saveActivity(activity01);

        System.out.println(activityDao.findActivityById(3));

        System.out.println(activityDao.listActivities());
    }
}
