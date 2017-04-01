package com.cying.lightorm.sample;

import com.cying.lightorm.Column;
import com.cying.lightorm.Key;
import com.cying.lightorm.Table;

/**
 * Created by Cying on 17/3/29.

 */
@Table("tabile")
public class Entity {

    @Key
    long id;


    @Column
    String name;

}
