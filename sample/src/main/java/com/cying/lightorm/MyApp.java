package com.cying.lightorm;

import android.app.Application;

/**
 * Created by Cying on 17/4/1.
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initORM();
    }

    private void initORM() {
        DatabaseConfiguration dc = new DatabaseConfiguration("db.db", 1);
        LightORM.init(this, dc);
    }
}
