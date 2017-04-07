package com.cying.lightorm.sample;


import com.cying.lightorm.Column;
import com.cying.lightorm.Key;

import java.util.Date;

/**
 * User: Cying
 * Date: 2015/7/6
 * Time: 23:02
 */
//@Table("entity")
public class TestEntity {

    @Key
    Long id;

    @Column
    String name;

    @Column
    int a;

    @Column
    long b;

    @Column
    double c;

    @Column
    float d;

    @Column
    Integer e;

    @Column
    Double f;

    @Column
    Float g;

    @Column
    Date h;

    @Column
    byte[] n;

    @Column(unique = false)
    boolean o;

    @Column
    Boolean p;

    @Column
    byte q;

    @Column
    Byte r;

    @Column
    short s;

    @Column
    Short t;

}
