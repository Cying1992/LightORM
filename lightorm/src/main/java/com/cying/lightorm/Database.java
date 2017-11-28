package com.cying.lightorm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Cying on 17/3/30.
 */
class Database {

    final String databaseName;
    final List<String> createSqls = new ArrayList<>();

    private final Map<String, BaseDao> daoMap = new HashMap<>();
    private final Map<String, String[]> updateSqls = new HashMap<>();

    private SQLiteOpenHelper helper;

    private final AtomicInteger count = new AtomicInteger(0);

    private volatile SQLiteDatabase db;

    Database(String databaseName) {
        this.databaseName = databaseName;
    }

    void addUpdateSql(int fromVersion, int toVersion, String... sqls) {
        if (sqls == null) {
            return;
        }
        if (fromVersion < 1 || toVersion < 1 || fromVersion >= toVersion) {
            throw new IllegalArgumentException("数据库版本号不正确，开始版本号为" + fromVersion + ", 结束版本号为" + toVersion);
        }
        final String key = fromVersion + "" + toVersion;
        if (updateSqls.containsKey(key)) {
            throw new IllegalStateException("已经存在从版本" + fromVersion + "到" + toVersion + "的数据库升级SQL");
        }
        updateSqls.put(key, sqls);
    }

    void putDao(String tableName, BaseDao dao) {
        daoMap.put(tableName, dao);
    }

    BaseDao getDao(String tableName) {
        return daoMap.get(tableName);
    }

    void init(Context context, DatabaseConfiguration databaseConfiguration) {
        final DatabaseConfiguration.OnGradeChangedListener downgradeListener = databaseConfiguration.getDowngradeListener();
        final DatabaseConfiguration.OnGradeChangedListener upgradeListener = databaseConfiguration.getUpgradeListener();
        final DatabaseConfiguration.OnCreatedListener databaseCreatedListener = databaseConfiguration.getDatabaseCreatedListener();
        helper = new SQLiteOpenHelper(context, databaseName, null, databaseConfiguration.getDatabaseVersion()) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                for (String sql : createSqls) {
                    db.execSQL(sql);
                }
                databaseCreatedListener.onCreated(databaseName);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, final int oldVersion, final int newVersion) {
                //数据库升级
                LightORM.debug("开始升级数据库：" + databaseName);
                for (int i = oldVersion; i < newVersion; i++) {
                    String key = i + "" + (i + 1);
                    String[] sqls = updateSqls.get(key);
                    if (sqls != null) {
                        for (String sql : sqls) {
                            db.execSQL(sql);
                            LightORM.debug("执行数据库升级语句：" + sql);
                        }
                    }
                }

                updateSqls.clear();
                upgradeListener.onGradeChanged(db, oldVersion, newVersion);
            }

            @Override
            public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                downgradeListener.onGradeChanged(db, oldVersion, newVersion);
            }
        };
    }

    SQLiteDatabase get() {
        return db;
    }

    synchronized SQLiteDatabase open() {
        if (count.incrementAndGet() == 1 || db == null) {
            db = helper.getWritableDatabase();
        }
        return db;
    }

    synchronized void close() {
        if (db != null && count.decrementAndGet() == 0) {
            /**
             *  坑爹啊，这里之前一直写成sqLiteDatabase.close()了，没检查是否是打开的
             *  这可能是导致attempt to re-open an already-closed object问题的主要原因
             */

            helper.close();
            db = null;
        }
    }

    boolean isOpen() {
        return db != null && db.isOpen();
    }

    int getOpenCount() {
        return count.get();
    }

}
