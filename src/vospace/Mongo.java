package vospace;

import com.mongodb.MongoClient;

public class Mongo {
    private static Mongo instance;
    private Mongo() {
       MongoClient m = new MongoClient();
    }

    public static Mongo getInstance() {
        if (instance == null) {
            synchronized (Mongo.class) {
                if (instance == null) { 
                    instance = new Mongo();
                }
            }
        }

        return instance;
    }
}
