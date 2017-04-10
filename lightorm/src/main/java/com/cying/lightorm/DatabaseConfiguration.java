package com.cying.lightorm;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * Created by Cying on 17/3/30.
 * 配置数据库，如数据库升级、版本号、名称等信息
 */
public class DatabaseConfiguration {

    public interface OnGradeChangedListener {
        void onGradeChanged(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion);
    }

    public interface OnCreatedListener {
        void onCreated(String databaseName);
    }

    private static final OnGradeChangedListener EMPTY_GRADE_LISTENER = new OnGradeChangedListener() {
        @Override
        public void onGradeChanged(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        }
    };

    private static final OnCreatedListener EMPTY_DATABASE_CREATED_LISTENER = new OnCreatedListener() {
        @Override
        public void onCreated(String databaseName) {

        }
    };

    private String databaseName;
    private int databaseVersion;
    private OnGradeChangedListener downgradeListener;
    private OnGradeChangedListener upgradeListener;
    private OnCreatedListener databaseCreatedListener;

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

    OnCreatedListener getDatabaseCreatedListener() {
        return databaseCreatedListener == null ? EMPTY_DATABASE_CREATED_LISTENER : databaseCreatedListener;
    }


    OnGradeChangedListener getDowngradeListener() {
        return downgradeListener == null ? EMPTY_GRADE_LISTENER : downgradeListener;
    }

    OnGradeChangedListener getUpgradeListener() {
        return upgradeListener == null ? EMPTY_GRADE_LISTENER : upgradeListener;
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
     * @param downgradeListener 降级回调函数
     */
    public void setOnDowngradeListener(OnGradeChangedListener downgradeListener) {
        this.downgradeListener = downgradeListener;
    }

    /**
     * 设置数据库升级回调函数
     *
     * @param upgradeListener 升级回调函数
     */
    public void setOnUpgradeListener(OnGradeChangedListener upgradeListener) {
        this.upgradeListener = upgradeListener;
    }

    /**
     * 设置数据库创建后的回调
     *
     * @param listener
     */
    public void setOnCreatedListener(OnCreatedListener listener) {
        this.databaseCreatedListener = listener;
    }
}
