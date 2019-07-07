package business;

/// <summary>
/// Represents a single conference session
/// </summary>
public class Session {
    private String title;
    private String description;
    private boolean approved;

    public Session(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public Session setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Session setDescription(String description) {
        this.description = description;
        return this;
    }

    public boolean isApproved() {
        return approved;
    }

    public Session setApproved(boolean approved) {
        this.approved = approved;
        return this;
    }
}

