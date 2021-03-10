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

    public int getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getContent() {
        return content;
    }
}
