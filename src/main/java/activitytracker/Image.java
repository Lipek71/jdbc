package activitytracker;

public class Image {

    private int id;
    private String filename;
    private byte[] content;

    public Image(int id, String filename, byte[] content) {
        this.id = id;
        this.filename = filename;
        this.content = content;
    }
}
