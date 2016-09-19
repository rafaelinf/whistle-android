package app.whistle.android.com.br.whistle.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import app.whistle.android.com.br.whistle.entity.Contact;
import app.whistle.android.com.br.whistle.entity.User;

/**
 * Created by rafael on 02/12/2015.
 */
public class WhistleBD extends SQLiteOpenHelper{

    private static final String NAME_DB = "whistledb";
    private static final int VERSAO_DB = 236;

    public WhistleBD(Context ctx){
        super(ctx, NAME_DB, null, VERSAO_DB);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(User.CREATE_TABLE);
        db.execSQL(Contact.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(User.DROP_TABLE);
        db.execSQL(Contact.DROP_TABLE);
        onCreate(db);
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
}
