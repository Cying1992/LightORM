package com.cying.lightorm.sample;

import com.cying.lightorm.Column;
import com.cying.lightorm.Key;
import com.cying.lightorm.Table;

import java.util.Calendar;

/**
 * Created by Cying on 17/3/29.
 * email:chengying@souche.com
 */
@Table("tabile")
public class Entity {

    @Key
    long id;


    @Column
    String name;

    @Column
    Calendar calendar;
}
