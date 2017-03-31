package com.cying.lightorm;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

/**
 * Created by Cying on 17/3/30.
 * email:chengying@souche.com
 */
public class DatabaseConfiguration {

    public interface DatabaseGradeListener {
        void onGradeChanged(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion);
    }

    private static final DatabaseGradeListener EMPTY_GRADE_LISTENER = new DatabaseGradeListener() {
        @Override
        public void onGradeChanged(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        }
    };

    private String databaseName;
    private int databaseVersion;
    private DatabaseGradeListener downGradeListener;
    private DatabaseGradeListener upGradeListener;

    /**
     * 数据库名称
     *
     * @param databaseName 数据库名称会忽略所有空白字符
     */
    public DatabaseConfiguration(String databaseName) {
        this.databaseName = databaseName == null ? null : databaseName.trim();
        if (TextUtils.isEmpty(databaseName)) {
            throw new IllegalArgumentException("数据库名称不能为空");
        }
    }

    DatabaseGradeListener getDownGradeListener() {
        return downGradeListener == null ? EMPTY_GRADE_LISTENER : downGradeListener;
    }

    DatabaseGradeListener getUpGradeListener() {
        return upGradeListener == null ? EMPTY_GRADE_LISTENER : upGradeListener;
    }

    String getDatabaseName() {
        return databaseName;
    }

    int getDatabaseVersion() {
        return databaseVersion < 1 ? 1 : databaseVersion;
    }


    /**
     * 设置数据库版本，若版本号小于1，则取1
     *
     * @param databaseVersion 数据库版本号
     * @return
     */
    public DatabaseConfiguration setDatabaseVersion(int databaseVersion) {
        this.databaseVersion = databaseVersion;
        return this;
    }

    /**
     * 设置数据库降级回调函数
     *
     * @param downGradeListener 降级回调函数
     * @return
     */
    public DatabaseConfiguration setDownGradeListener(DatabaseGradeListener downGradeListener) {
        this.downGradeListener = downGradeListener;
        return this;
    }

    /**
     * 设置数据库升级回调函数
     *
     * @param upGradeListener 升级回调函数
     * @return
     */
    public DatabaseConfiguration setUpGradeListener(DatabaseGradeListener upGradeListener) {
        this.upGradeListener = upGradeListener;
        return this;
    }
}
