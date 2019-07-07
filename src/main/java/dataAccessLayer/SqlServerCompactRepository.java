package dataAccessLayer;

import business.IRepository;
import business.Speaker;

public class SqlServerCompactRepository implements IRepository {
    public int saveSpeaker(Speaker speaker) {
        //TODO: Save speaker to DB for now. For demo, just assume success and return 1.
        return 1;
    }
}
