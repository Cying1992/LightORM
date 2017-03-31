package com.cying.lightorm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Cying on 17/3/30.
 * email:chengying@souche.com
 */
class Database {

    final String databaseName;
    final List<String> createSqls = new ArrayList<>();

    private SQLiteOpenHelper helper;

    private final AtomicInteger count = new AtomicInteger(0);

    private volatile SQLiteDatabase db;

    Database(String databaseName) {
        this.databaseName = databaseName;
    }

    void init(Context context, DatabaseConfiguration databaseConfiguration) {
        final DatabaseConfiguration.DatabaseGradeListener downGradeListener = databaseConfiguration.getDownGradeListener();
        final DatabaseConfiguration.DatabaseGradeListener upGradeListener = databaseConfiguration.getUpGradeListener();
        helper = new SQLiteOpenHelper(context, databaseName, null, databaseConfiguration.getDatabaseVersion()) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                for (String sql : createSqls) {
                    db.execSQL(sql);
                }
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                upGradeListener.onGradeChanged(db, oldVersion, newVersion);
            }

            @Override
            public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                downGradeListener.onGradeChanged(db, oldVersion, newVersion);
            }
        };
    }

    synchronized SQLiteDatabase open() {
        if (count.incrementAndGet() == 1 || db == null) {
            db = helper.getWritableDatabase();
        }
        return db;
    }

    synchronized void close() {
        if (count.decrementAndGet() == 0) {
            /**
             *  坑爹啊，这里之前一直写成sqLiteDatabase.close()了，没检查是否是打开的
             *  这可能是导致attempt to re-open an already-closed object问题的主要原因
             */

            helper.close();
            db = null;
        }
    }

}
