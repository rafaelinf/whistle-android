package app.whistle.android.com.br.whistle.database;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rafael.soares on 06/04/2016.
 */
public class DatabaseManager {

    private static DatabaseManager instance;
    private static SQLiteOpenHelper mDatabaseHelper;

    public static synchronized void initializeInstance(SQLiteOpenHelper helper) {
        if (instance == null) {
            instance = new DatabaseManager();
            mDatabaseHelper = helper;
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(DatabaseManager.class.getSimpleName() +
                    " is not initialized, call initialize(..) method first.");
        }

        return instance;
    }

    public synchronized SQLiteDatabase getDatabase() {
        return mDatabaseHelper.getWritableDatabase();
    }

}