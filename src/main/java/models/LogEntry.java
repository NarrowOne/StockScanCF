package models;

public class LogEntry {
    private String id;
    private int type;
    private String date;
    private String subject;
    private String description;

    public LogEntry(String id, int type, String subject, String description) {
        this.id = id;
        this.type = type;
        this.subject = subject;
        this.description = description;
    }

    public LogEntry(int type, String date, String subject, String description) {
        this.type = type;
        this.subject = subject;
        this.date = date;
        this.description = description;
    }

    public LogEntry(int type, String subject, String description) {
        this.type = type;
        this.subject = subject;
        this.description = description;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}

