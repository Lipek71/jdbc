package activitytracker;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActivityDaoTest {

    private ActivityDao activityDao;

    @BeforeEach
    public void init() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL("jdbc:mysql://localhost:3306/activitytracker?useUnicode=true");
        dataSource.setUser("activitytracker");
        dataSource.setPassword("activitytracker");

        Flyway flyway = Flyway.configure().dataSource(dataSource).load();

        flyway.clean();
        flyway.migrate();

        activityDao = new ActivityDao(dataSource);
    }

    @Test
    void testSaveActivity() {
        TrackPoint trackPoint01 = new TrackPoint(1, LocalDateTime.of(2021, 2, 21, 10, 0, 0), 42.2342, 18.2355);
        TrackPoint trackPoint02 = new TrackPoint(1, LocalDateTime.of(2021, 2, 21, 10, 30, 0), 42.2442, 18.2655);

        List<TrackPoint> trackPoints = Arrays.asList(trackPoint01, trackPoint02);

        Activity activity01 = new Activity(1, LocalDateTime.of(2021, 2, 21, 10, 0, 0), "Mátra", ActivityType.RUNNING, trackPoints);
        Activity activity02 = new Activity(1, LocalDateTime.of(2021, 2, 20, 14, 0, 0), "Gerecse", ActivityType.BIKING, trackPoints);
        Activity activity03 = new Activity(1, LocalDateTime.of(2021, 3, 1, 12, 0, 0), "Budapest", ActivityType.BASKETBALL, trackPoints);
        Activity activity04 = new Activity(1, LocalDateTime.of(2021, 3, 2, 9, 0, 0), "Mátra", ActivityType.RUNNING, trackPoints);
        Activity activity05 = new Activity(1, LocalDateTime.of(2021, 3, 4, 16, 0, 0), "Velence", ActivityType.HIKING, trackPoints);

        activityDao.saveActivity(activity01);
        activityDao.saveActivity(activity02);
        assertEquals("Activity{id=3, startTime=2021-03-01T12:00, desc='Budapest', type=BASKETBALL}", activityDao.saveActivity(activity03).toString());
        activityDao.saveActivity(activity04);
        activityDao.saveActivity(activity05);

        assertEquals("Activity{id=1, startTime=2021-02-21T10:00, desc='Mátra', type=RUNNING}", activityDao.findActivityById(1).toString());
    }

    @Test
    void testFindActivityById() {
        TrackPoint trackPoint01 = new TrackPoint(1, LocalDateTime.of(2021, 2, 21, 10, 0, 0), 42.2342, 18.2355);
        TrackPoint trackPoint02 = new TrackPoint(1, LocalDateTime.of(2021, 2, 21, 10, 30, 0), 42.2442, 18.2655);

        List<TrackPoint> trackPoints = Arrays.asList(trackPoint01, trackPoint02);

        Activity activity01 = new Activity(1, LocalDateTime.of(2021, 2, 21, 10, 0, 0), "Mátra", ActivityType.RUNNING, trackPoints);
        Activity activity02 = new Activity(1, LocalDateTime.of(2021, 2, 20, 14, 0, 0), "Gerecse", ActivityType.BIKING, trackPoints);
        Activity activity03 = new Activity(1, LocalDateTime.of(2021, 3, 1, 12, 0, 0), "Budapest", ActivityType.BASKETBALL, trackPoints);
        Activity activity04 = new Activity(1, LocalDateTime.of(2021, 3, 2, 9, 0, 0), "Mátra", ActivityType.RUNNING, trackPoints);
        Activity activity05 = new Activity(1, LocalDateTime.of(2021, 3, 4, 16, 0, 0), "Velence", ActivityType.HIKING, trackPoints);

        activityDao.saveActivity(activity01);
        activityDao.saveActivity(activity02);
        activityDao.saveActivity(activity03);
        activityDao.saveActivity(activity04);
        activityDao.saveActivity(activity05);

        assertEquals("Activity{id=3, startTime=2021-03-01T12:00, desc='Budapest', type=BASKETBALL}", activityDao.findActivityById(3).toString());
    }

    @Test
    void testListActivities() {
        TrackPoint trackPoint01 = new TrackPoint(1, LocalDateTime.of(2021, 2, 21, 10, 0, 0), 42.2342, 18.2355);
        TrackPoint trackPoint02 = new TrackPoint(1, LocalDateTime.of(2021, 2, 21, 10, 30, 0), 42.2442, 18.2655);

        List<TrackPoint> trackPoints = Arrays.asList(trackPoint01, trackPoint02);

        Activity activity01 = new Activity(1, LocalDateTime.of(2021, 2, 21, 10, 0, 0), "Mátra", ActivityType.RUNNING, trackPoints);
        Activity activity02 = new Activity(1, LocalDateTime.of(2021, 2, 20, 14, 0, 0), "Gerecse", ActivityType.BIKING, trackPoints);
        Activity activity03 = new Activity(1, LocalDateTime.of(2021, 3, 1, 12, 0, 0), "Budapest", ActivityType.BASKETBALL, trackPoints);

        activityDao.saveActivity(activity01);
        activityDao.saveActivity(activity02);
        activityDao.saveActivity(activity03);

        assertEquals("[Activity{id=2, startTime=2021-02-20T14:00, desc='Gerecse', type=BIKING}, Activity{id=3, startTime=2021-03-01T12:00, desc='Budapest', type=BASKETBALL}]", activityDao.listActivities().toString());

    }
}