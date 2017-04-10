package com.cying.lightorm;

import android.app.Activity;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by Cying on 17/4/7.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class QueryTest {

    static final LightORM orm;
    static final BaseDao<Entity> dao;
    static final Query<Entity> query;
    static final String databaseName = "test_database";
    static final Database database;

    static {
        DatabaseConfiguration config = new DatabaseConfiguration(databaseName, 1);
        LightORM.init(Robolectric.setupActivity(Activity.class), config);
        LightORM.setDebug(true);
        orm = LightORM.getInstance();
        dao = orm.getDao(Entity.class);
        query = orm.where(Entity.class);
        database = orm.findDatabase(databaseName);
    }

    @After
    public void tearDown() {
        orm.deleteAll(Entity.class);
        query.reset();
        orm.closeDatabase();
    }

    private void testDatabaseClosed() {
        assertThat(database.isOpen(), is(false));
        assertThat(database.getOpenCount(), is(0));
    }

    @Test
    public void save() {
        Entity entity = new Entity();
        entity.id = -1L;
        entity.string = "string";
        assertThat(orm.save(entity), is(1L));
        assertThat(entity.id, is(1L));
        entity.string = "changed";
        assertThat(orm.save(entity), is(1L));
        assertThat(query.findFirst().string, is("changed"));
        entity.id = 8L;
        assertThat(orm.save(entity), is(8L));
        assertThat(query.count(), is(2L));
        testDatabaseClosed();
    }

    @Test
    public void contains() {
        saveEntity();
        assertThat(query.contains("string", "STR", false).findFirst(), is(notNullValue()));
        assertThat(query.contains("string", "STR", true).findFirst(), is(nullValue()));
        assertThat(query.like("string", "STRing", false).findFirst(), is(notNullValue()));
        assertThat(query.like("string", "STRing", true).findFirst(), is(nullValue()));
        testDatabaseClosed();
    }

    @Test
    public void delete() {
        assertThat(orm.delete(saveEntity()), is(true));
        saveEntity();
        assertThat(orm.deleteAll(Entity.class), is(1));
        saveEntity();
        assertThat(orm.deleteAll(query.contains("string", "str")), is(1));
        saveEntity();
        assertThat(orm.deleteAll(query.contains("string", "mm")), is(0));
        testDatabaseClosed();
    }


    private void checkColumnsValid(BaseDao.FieldType fieldType, String columnName, String... otherColumnNames) {
        assertThat("columnName = " + columnName, dao.getFieldType(columnName), is(fieldType));

        if (otherColumnNames != null) {
            for (String column : otherColumnNames) {
                assertThat("columnName = " + column, dao.getFieldType(column), is(fieldType));
            }
        }
    }


    @Test
    public void checkFieldType() {
        checkColumnsValid(BaseDao.FieldType.INTEGER, "id", "smallInt", "smallLong", "smallShort", "smallByte", "bigInt", "bigLong", "bigShort", "bigByte");
        checkColumnsValid(BaseDao.FieldType.BINARY, "byteArray");
        checkColumnsValid(BaseDao.FieldType.BOOLEAN, "smallBoolean", "bigBoolean");
        checkColumnsValid(BaseDao.FieldType.DATE, "date");
        checkColumnsValid(BaseDao.FieldType.STRING, "string");
        checkColumnsValid(BaseDao.FieldType.FLOAT, "smallFloat", "bigFloat");
        checkColumnsValid(BaseDao.FieldType.DOUBLE, "smallDouble", "bigDouble");
        testDatabaseClosed();
    }

    private Entity saveEntity() {
        return saveEntity(new Date());
    }

    private Entity saveEntity(Date date) {
        Entity entity = new Entity();
        entity.string = "string";
        entity.smallInt = 90;
        entity.bigInt = 100;
        entity.date = date;
        entity.smallBoolean = false;
        entity.bigBoolean = true;
        entity.smallFloat = 23F;
        entity.smallShort = 2;
        entity.smallByte = 1;
        entity.smallDouble = 89D;
        orm.save(entity);
        return entity;
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void equalTo() {
        Date date = new Date();
        saveEntity(date);
        byte b = 1;
        short s = 2;
        assertThat(query.equalTo("smallByte", 1).findFirst().smallByte, is(b));
        assertThat(query.equalTo("smallShort", 2).findFirst().smallShort, is(s));

        assertThat(query.equalTo("smallDouble", 89D).findFirst().smallDouble, is(89D));
        assertThat(query.equalTo("smallFloat", 23F).findFirst().smallFloat, is(23F));
        assertThat(query.equalTo("bigBoolean", true).findFirst().bigBoolean, is(true));
        assertThat(query.equalTo("smallBoolean", false).findFirst().smallBoolean, is(false));
        assertThat(query.equalTo("date", date).findFirst().date, is(date));
        assertThat(query.equalTo("string", "string").findFirst().string, is("string"));
        assertThat(query.equalTo("smallInt", 90).findFirst().smallInt, is(90));
        testDatabaseClosed();
    }

    @Test
    public void notEqualTo() {
        saveEntity();
        assertThat(query.notEqualTo("string", "string").findFirst(), is(nullValue()));
        assertThat(query.notEqualTo("string", "mm").findFirst(), is(notNullValue()));
        testDatabaseClosed();
    }

    @Test
    public void exists() {
        saveEntity();
        assertThat(query.contains("string", "String", true).exists(), is(false));
        assertThat(query.contains("string", "String", false).exists(), is(true));
        testDatabaseClosed();
    }

    @Test
    public void testOr() {
        saveEntity();
        assertThat(query.equalTo("smallInt", 80).or().equalTo("smallInt", 90).exists(), is(true));
        assertThat(query.equalTo("smallInt", 80).or().equalTo("smallInt", 70).exists(), is(false));
        assertThat(query.equalTo("smallInt", 80).or().beginGroup().equalTo("smallInt", 90).equalTo("bigInt", 100).endGroup().exists(), is(true));
        assertThat(query.equalTo("smallInt", 80).beginGroup().equalTo("smallInt", 90).or().equalTo("bigInt", 100).endGroup().exists(), is(false));
        assertThat(query.equalTo("smallInt", 80).beginGroup().equalTo("smallInt", 90).or().isEmpty("string").endGroup().exists(), is(false));
        assertThat(
                query.equalTo("smallInt", 80)
                        .or()
                        .beginGroup()
                        .equalTo("smallInt", 90)
                        .or()
                        .isEmpty("string")
                        .endGroup()
                        .exists(), is(true));
        testDatabaseClosed();
    }

    @Test
    public void nullOrEmpty() {
        Entity entity = new Entity();
        orm.save(entity);
        assertThat(query.isNull("string").count(), is(1L));
        assertThat(query.isEmpty("string").count(), is(1L));
        entity.string = "string";
        orm.save(entity);
        assertThat(query.isNull("string").count(), is(0L));
        assertThat(query.isEmpty("string").count(), is(0L));
        assertThat(query.isNotNull("string").count(), is(1L));
        assertThat(query.isNotEmpty("string").count(), is(1L));
        testDatabaseClosed();
    }


    @Test
    public void testSort() {

        assertThat(query.findAll(Sort.create().distinct(true).limit(1).groupBy("string").orderBy(true, "string", "smallInt").orderBy(false, "bigInt", "bigLong").having("smallDouble>0")), is(Matchers.<Entity>empty()));
        testDatabaseClosed();
    }

    @Test
    public void testOpenDatabase() {
        orm.openDatabase();

        assertThat(database.getOpenCount(), is(1));
        orm.closeDatabase();
        assertThat(database.getOpenCount(), is(0));
        orm.openDatabase();
        orm.openDatabase();
        orm.closeDatabase();
        assertThat(database.getOpenCount(), is(1));
        orm.closeDatabase();
        orm.closeDatabase();
        orm.closeDatabase();
        assertThat(database.getOpenCount(), is(0));
        orm.openDatabase();
        assertThat(database.getOpenCount(), is(1));
    }
}
