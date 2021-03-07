package activitytracker;

import java.time.LocalDateTime;

public class TrackPoint {
    private int id;
    private LocalDateTime time;
    private double lat;
    private double lon;

    public TrackPoint(int id, LocalDateTime time, double lat, double lon) {
        this.id = id;
        this.time = time;
        this.lat = lat;
        this.lon = lon;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

}
