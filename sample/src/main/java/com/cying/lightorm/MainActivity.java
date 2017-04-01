package com.cying.lightorm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cying.lightorm.sample.TestEntity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LightORM.getInstance()
                .where(TestEntity.class)
                .isNotNull("b")
                .or()
                .beginGroup()
                .between("a", 1, 2)
                .contains("name", "m")
                .between("a", 2, 3)
                .or()
                .between("a", 3, 4)
                .endGroup()
                .findAll(Sort.create().groupBy("k").limit(1, 2));

    }
}
