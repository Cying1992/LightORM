package com.cying.lightorm.sample;


import com.cying.lightorm.Column;
import com.cying.lightorm.Key;
import com.cying.lightorm.Table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * User: Cying
 * Date: 2015/7/6
 * Time: 23:02
 */
@Table("entity")
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
    Calendar i;

    @Column
    Timestamp j;

    @Column
    BigDecimal k;

    @Column
    MyEnum m;

    @Column
    byte[] n;

    @Column
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

    @Column
    char u;

    @Column
    Character v;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public long getB() {
        return b;
    }

    public void setB(long b) {
        this.b = b;
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }

    public float getD() {
        return d;
    }

    public void setD(float d) {
        this.d = d;
    }

    public Integer getE() {
        return e;
    }

    public void setE(Integer e) {
        this.e = e;
    }

    public Double getF() {
        return f;
    }

    public void setF(Double f) {
        this.f = f;
    }

    public Float getG() {
        return g;
    }

    public void setG(Float g) {
        this.g = g;
    }

    public Date getH() {
        return h;
    }

    public void setH(Date h) {
        this.h = h;
    }

    public Calendar getI() {
        return i;
    }

    public void setI(Calendar i) {
        this.i = i;
    }

    public Timestamp getJ() {
        return j;
    }

    public void setJ(Timestamp j) {
        this.j = j;
    }

    public BigDecimal getK() {
        return k;
    }

    public void setK(BigDecimal k) {
        this.k = k;
    }

    public MyEnum getM() {
        return m;
    }

    public void setM(MyEnum m) {
        this.m = m;
    }

    public byte[] getN() {
        return n;
    }

    public void setN(byte[] n) {
        this.n = n;
    }

    public boolean isO() {
        return o;
    }

    public void setO(boolean o) {
        this.o = o;
    }

    public Boolean getP() {
        return p;
    }

    public void setP(Boolean p) {
        this.p = p;
    }

    @Override
    public boolean equals(Object o1) {
        if (this == o1) return true;
        if (o1 == null || getClass() != o1.getClass()) return false;

        TestEntity that = (TestEntity) o1;

        if (a != that.a) return false;
        if (b != that.b) return false;
        if (Double.compare(that.c, c) != 0) return false;
        if (Float.compare(that.d, d) != 0) return false;
        if (o != that.o) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (e != null ? !e.equals(that.e) : that.e != null) return false;
        if (f != null ? !f.equals(that.f) : that.f != null) return false;
        if (g != null ? !g.equals(that.g) : that.g != null) return false;
        if (h != null ? !h.equals(that.h) : that.h != null) return false;
        if (i != null ? !i.equals(that.i) : that.i != null) return false;
        if (j != null ? !j.equals(that.j) : that.j != null) return false;
        if (k != null ? !k.equals(that.k) : that.k != null) return false;
        if (!Arrays.equals(n, that.n)) return false;
        return p != null ? p.equals(that.p) : that.p == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + a;
        result = 31 * result + (int) (b ^ (b >>> 32));
        temp = Double.doubleToLongBits(c);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (d != +0.0f ? Float.floatToIntBits(d) : 0);
        result = 31 * result + (e != null ? e.hashCode() : 0);
        result = 31 * result + (f != null ? f.hashCode() : 0);
        result = 31 * result + (g != null ? g.hashCode() : 0);
        result = 31 * result + (h != null ? h.hashCode() : 0);
        result = 31 * result + (i != null ? i.hashCode() : 0);
        result = 31 * result + (j != null ? j.hashCode() : 0);
        result = 31 * result + (k != null ? k.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(n);
        result = 31 * result + (o ? 1 : 0);
        result = 31 * result + (p != null ? p.hashCode() : 0);
        return result;
    }

    @Table
    public static class InnerEntity {
        @Key
        long innerId;

        @Column
        String innerName;

        public long getInnerId() {
            return innerId;
        }

        public void setInnerId(long innerId) {
            this.innerId = innerId;
        }

        public String getInnerName() {
            return innerName;
        }

        public void setInnerName(String innerName) {
            this.innerName = innerName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            InnerEntity that = (InnerEntity) o;

            if (innerId != that.innerId) return false;
            return innerName != null ? innerName.equals(that.innerName) : that.innerName == null;

        }

        @Override
        public int hashCode() {
            int result = (int) (innerId ^ (innerId >>> 32));
            result = 31 * result + (innerName != null ? innerName.hashCode() : 0);
            return result;
        }
    }


    enum MyEnum {

    }
}
