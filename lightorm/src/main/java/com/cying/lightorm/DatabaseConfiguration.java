package com.cying.lightorm;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * Created by Cying on 17/3/30.
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
     * @param databaseName    数据库名称会忽略所有空白字符
     * @param databaseVersion 设置数据库版本，若版本号小于1，则取1
     */
    public DatabaseConfiguration(@NonNull String databaseName, @IntRange(from = 1) int databaseVersion) {
        if (databaseVersion < 1) {
            throw new IllegalArgumentException("数据库版本号不能小于1");
        }
        this.databaseVersion = databaseVersion;
        this.databaseName = databaseName.trim();
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
     * 设置数据库降级回调函数
     *
     * @param downGradeListener 降级回调函数
     * @return
     */
    public void setDownGradeListener(DatabaseGradeListener downGradeListener) {
        this.downGradeListener = downGradeListener;
    }

    /**
     * 设置数据库升级回调函数
     *
     * @param upGradeListener 升级回调函数
     * @return
     */
    public void setUpGradeListener(DatabaseGradeListener upGradeListener) {
        this.upGradeListener = upGradeListener;
    }
}
