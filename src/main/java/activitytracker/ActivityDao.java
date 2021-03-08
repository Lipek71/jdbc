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

            int returnId = getIdFromDb(stmt);
            saveTrackPoint(activity.getTrackPoints(), returnId);

            activity.setId(returnId);
            return activity;

        } catch (SQLException se) {
            throw new IllegalStateException("Can't insert the row!");
        }
    }

    private void saveTrackPoint(List<TrackPoint> trackPoints, int returnId) {
        try (Connection connTrans = dataSource.getConnection()) {
            connTrans.setAutoCommit(false);

            saveTrackPointTransaction(trackPoints, returnId, connTrans);

        } catch (SQLException se) {
            throw new IllegalStateException("Can't insert the row!");
        }
    }

    private void saveTrackPointTransaction(List<TrackPoint> trackPoints, int returnId, Connection connTrans) throws SQLException {
        try (PreparedStatement stmt = connTrans.prepareStatement("INSERT INTO track_point(activities_id, time, lat, lon) VALUES (?,?,?,?)")) {
            for (TrackPoint trackPoint : trackPoints) {
                if (trackPoint.getLat() < -90 || trackPoint.getLat() > 90 || trackPoint.getLon() < -180 || trackPoint.getLon() > 180) {
                    throw new IllegalArgumentException("Invalid value!");
                }
                stmt.setInt(1, returnId);
                stmt.setTimestamp(2, Timestamp.valueOf(trackPoint.getTime()));
                stmt.setDouble(3, trackPoint.getLat());
                stmt.setDouble(4, trackPoint.getLon());
                stmt.executeUpdate();
            }
            connTrans.commit();
        } catch (IllegalArgumentException iae) {
            connTrans.rollback();
        }
    }

    private int getIdFromDb(PreparedStatement stmt) throws SQLException {
        try (
                ResultSet rs = stmt.getGeneratedKeys()
        ) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new IllegalStateException("Can't get id!");
        }
    }

    public Activity findActivityById(long id) {
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

    private Activity readActivityFromDbById(PreparedStatement stmt) {
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int id = rs.getInt("id");
                Timestamp timestamp = rs.getTimestamp("start_time");
                String name = rs.getString("activity_desc");
                String type = rs.getString("activity_type");

                List<TrackPoint> returnTrackPoints = readActivityTrackPointFromDbById(id);
                System.out.println(returnTrackPoints);

                return new Activity(id, timestamp.toLocalDateTime(), name, ActivityType.valueOf(type), returnTrackPoints);

            } else {
                throw new IllegalArgumentException("Not found");
            }

        } catch (SQLException sqle) {
            throw new IllegalStateException("Can't query", sqle);
        }
    }

    private List<TrackPoint> readActivityTrackPointFromDbById(int id) {
        try (
                Connection connTP = dataSource.getConnection();
                PreparedStatement stmtTP = connTP.prepareStatement("SELECT * FROM track_point WHERE activities_id = ?")
        ) {

            stmtTP.setInt(1,id);
            return readActivityTrackPointFromDbById2(stmtTP);

        } catch (SQLException sqle) {
            throw new IllegalStateException("Can't query", sqle);
        }

    }

    private List<TrackPoint> readActivityTrackPointFromDbById2(PreparedStatement stmtTP) {

        List<TrackPoint> returnTrackPoints = new ArrayList<>();

        try (ResultSet rsTR = stmtTP.executeQuery()){
            while (rsTR.next()) {
                int id = rsTR.getInt("id");
                Timestamp timestamp = rsTR.getTimestamp("time");
                double lat = rsTR.getDouble("lat");
                double lon = rsTR.getDouble("lon");
                returnTrackPoints.add(new TrackPoint(id, timestamp.toLocalDateTime(), lat, lon));
            }
        } catch (SQLException sqle) {
            throw new IllegalStateException("Can't query", sqle);
        }
        return returnTrackPoints;
    }

    public List<Activity> listActivities() {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM activities WHERE 1")
        ) {

            return readActivitiesFromDb(stmt);

        } catch (SQLException sqle) {
            throw new IllegalStateException("Can't query", sqle);
        }
    }

    private List<Activity> readActivitiesFromDb(PreparedStatement stmt) {

        List<Activity> activities = new ArrayList<>();

        try (ResultSet rs = stmt.executeQuery()) {
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

    public void saveImageToActivity(long activityId, Image image){

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
