package com.cying.lightorm;

import java.util.Date;

/**
 * Created by Cying on 17/4/7.
 */
@Table
public class Entity {
    @Key
    Long id;

    @Column
    String string;

    @Column
    long smallLong;

    @Column
    Long bigLong;

    @Column
    int smallInt;

    @Column
    double smallDouble;

    @Column
    float smallFloat;

    @Column
    Integer bigInt;

    @Column
    Double bigDouble;

    @Column
    Float bigFloat;

    @Column
    Date date;

    @Column
    byte[] byteArray;

    @Column(unique = false)
    boolean smallBoolean;

    @Column
    Boolean bigBoolean;

    @Column
    byte smallByte;

    @Column
    Byte bigByte;

    @Column
    short smallShort;

    @Column
    Short bigShort;

    public Long getBigLong() {
        return bigLong;
    }

    public void setBigLong(Long bigLong) {
        this.bigLong = bigLong;
    }

    public long getSmallLong() {
        return smallLong;
    }

    public void setSmallLong(long smallLong) {
        this.smallLong = smallLong;
    }

    public Boolean getBigBoolean() {
        return bigBoolean;
    }

    public void setBigBoolean(Boolean bigBoolean) {
        this.bigBoolean = bigBoolean;
    }

    public Byte getBigByte() {
        return bigByte;
    }

    public void setBigByte(Byte bigByte) {
        this.bigByte = bigByte;
    }

    public Double getBigDouble() {
        return bigDouble;
    }

    public void setBigDouble(Double bigDouble) {
        this.bigDouble = bigDouble;
    }

    public Float getBigFloat() {
        return bigFloat;
    }

    public void setBigFloat(Float bigFloat) {
        this.bigFloat = bigFloat;
    }

    public Integer getBigInt() {
        return bigInt;
    }

    public void setBigInt(Integer bigInt) {
        this.bigInt = bigInt;
    }

    public Short getBigShort() {
        return bigShort;
    }

    public void setBigShort(Short bigShort) {
        this.bigShort = bigShort;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public boolean isSmallBoolean() {
        return smallBoolean;
    }

    public void setSmallBoolean(boolean smallBoolean) {
        this.smallBoolean = smallBoolean;
    }

    public byte getSmallByte() {
        return smallByte;
    }

    public void setSmallByte(byte smallByte) {
        this.smallByte = smallByte;
    }

    public double getSmallDouble() {
        return smallDouble;
    }

    public void setSmallDouble(double smallDouble) {
        this.smallDouble = smallDouble;
    }

    public float getSmallFloat() {
        return smallFloat;
    }

    public void setSmallFloat(float smallFloat) {
        this.smallFloat = smallFloat;
    }

    public int getSmallInt() {
        return smallInt;
    }

    public void setSmallInt(int smallInt) {
        this.smallInt = smallInt;
    }

    public short getSmallShort() {
        return smallShort;
    }

    public void setSmallShort(short smallShort) {
        this.smallShort = smallShort;
    }
}
