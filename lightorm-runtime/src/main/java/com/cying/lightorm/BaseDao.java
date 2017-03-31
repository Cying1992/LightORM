package com.cying.lightorm;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Created by Cying on 17/3/29.
 * email:chengying@souche.com
 */
public abstract class BaseDao<T> {

    private static final String LIMIT_ONE = " limit 1 ";
    private static final String TRUE = "1";

    protected static <T> void saveDao(Class<T> entityClass, BaseDao<T> dao) {
        LightORM.saveDao(entityClass, dao);
    }

    protected static <E extends Enum<E>> String valuesEnumToString(E e) {
        return e == null ? "" : e.name();
    }

    protected static int valuesCharacterToInt(Character value) {
        return value == null ? 0 : value;
    }

    protected static Long valuesCalendarToLong(Calendar calendar) {
        return calendar == null ? 0 : calendar.getTimeInMillis();
    }


    protected static Long valuesDateToLong(Date date) {
        return date == null ? 0 : date.getTime();
    }

    protected static Long valuesTimestampToLong(Timestamp timestamp) {
        return timestamp == null ? 0 : timestamp.getTime();
    }


    protected static String valuesBigDecimalToString(BigDecimal num) {
        return num == null ? null : num.toString();
    }


    protected <E extends Enum<E>> E cursorStringToEnum(Class<E> enumClass, Cursor cursor, String columnName) {
        String enumName = getString(cursor, columnName);
        try {

            return enumName == null ? null : Enum.valueOf(enumClass, enumName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected BigDecimal cursorStringToBigDecimal(Cursor cursor, String columnName) {
        String val = getString(cursor, columnName);
        new Date();
        try {
            return val == null ? null : new BigDecimal(val);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Boolean cursorIntToBoolean(Cursor cursor, String columnName) {
        int value = getInt(cursor, columnName);
        return value != 0;
    }

    protected Byte cursorIntToByte(Cursor cursor, String columnName) {
        int value = getInt(cursor, columnName);
        return (byte) value;
    }

    protected char cursorIntToCharacter(Cursor cursor, String columnName) {
        int value = getInt(cursor, columnName);
        return (char) value;
    }

    protected Calendar cursorLongToCalendar(Cursor cursor, String columnName) {
        long millis = getLong(cursor, columnName);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar;
    }

    protected Date cursorLongToDate(Cursor cursor, String columnName) {
        long millis = getLong(cursor, columnName);
        Date date = new Date();
        date.setTime(millis);
        return date;
    }

    protected Timestamp cursorLongToTimestamp(Cursor cursor, String columnName) {
        long millis = getLong(cursor, columnName);
        return new Timestamp(millis);
    }


    private final Map<String, Integer> sColumnIndexMap = new HashMap<>();

    private MetaData mMetaData;

    public BaseDao() {
        mMetaData = getMetaData();
    }

    private int getColumnIndex(Cursor cursor, String columnName) {
        if (sColumnIndexMap.containsKey(columnName)) {
            return sColumnIndexMap.get(columnName);
        } else {
            int index = cursor.getColumnIndex(columnName);
            sColumnIndexMap.put(columnName, index);
            return index;
        }
    }

    protected final long getLong(Cursor cursor, String columnName) {
        return cursor.getLong(getColumnIndex(cursor, columnName));
    }

    protected final int getInt(Cursor cursor, String columnName) {
        return cursor.getInt(getColumnIndex(cursor, columnName));
    }

    protected final String getString(Cursor cursor, String columnName) {
        return cursor.getString(getColumnIndex(cursor, columnName));
    }

    protected final short getShort(Cursor cursor, String columnName) {
        return cursor.getShort(getColumnIndex(cursor, columnName));
    }

    protected final byte[] getBlob(Cursor cursor, String columnName) {
        return cursor.getBlob(getColumnIndex(cursor, columnName));
    }

    protected final double getDouble(Cursor cursor, String columnName) {
        return cursor.getDouble(getColumnIndex(cursor, columnName));
    }

    protected final float getFloat(Cursor cursor, String columnName) {
        return cursor.getFloat(getColumnIndex(cursor, columnName));
    }

    public abstract MetaData getMetaData();

    protected abstract Long getIdentity(T entity);

    protected abstract void setIdentity(T entity, Long value);

    protected abstract T cursorToEntity(Cursor cursor);

    protected abstract ContentValues entityToValues(T entity);

    public SQLiteDatabase getDatabase() {
        return LightORM.openDatabase(mMetaData.databaseName);
    }

    public void closeDatabase() {
        LightORM.closeDatabase(mMetaData.databaseName);
    }

    private List<T> cursorToEntityList(Cursor cursor) {
        List<T> result = new ArrayList<>();
        if (cursor == null) {
            return result;
        }
        T entity;
        try {
            while (cursor.moveToNext()) {
                entity = cursorToEntity(cursor);
                result.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return result;
    }

    /**
     * @return first inserted entity
     */
    public T first() {
        String query = "SELECT * FROM " + mMetaData.tableName + " ORDER BY " + mMetaData.primaryKey + " ASC LIMIT 1";
        List<T> list = findWithQuery(query);
        if (list.isEmpty()) return null;
        return list.get(0);
    }

    public T first(String whereClause, String... whereArgs) {
        List<T> list = find(whereClause, whereArgs, null, mMetaData.primaryKey + " ASC ", "1");
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * @return last inserted entity
     */
    public T last() {
        String query = "SELECT * FROM " + mMetaData.tableName + " ORDER BY " + mMetaData.primaryKey + " DESC LIMIT 1";
        List<T> list = findWithQuery(query);
        if (list.isEmpty()) return null;
        return list.get(0);
    }

    public T last(String whereClause, String... whereArgs) {
        List<T> list = find(whereClause, whereArgs, null, mMetaData.primaryKey + " DESC ", "1");
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * @return the entity list in the table
     */
    public List<T> listAll() {
        return find(null, null, null, null, null);
    }


    public List<T> listAll(String orderBy) {
        return find(null, null, null, orderBy, null);
    }

    public List<T> listPage(int count, int pageIndex) {
        return listPage(count, pageIndex, null, null);
    }

    public List<T> listPage(int count, int pageIndex, String orderBy) {
        return listPage(count, pageIndex, orderBy, null);
    }

    /**
     * list all row by id asc
     *
     * @param count     the returned item count
     * @param pageIndex the page index ,start from 0
     * @return the data list
     */
    public List<T> listEarlierPage(int count, int pageIndex) {
        String orderBy = mMetaData.primaryKey + " ASC ";
        return listPage(count, pageIndex, orderBy, null);
    }

    /**
     * list all row by id desc
     *
     * @param count
     * @param pageIndex
     * @return 结果列表
     */
    public List<T> listLaterPage(int count, int pageIndex) {
        String orderBy = mMetaData.primaryKey + " DESC ";
        return listPage(count, pageIndex, orderBy, null);
    }

    /**
     * @param count       item count each page
     * @param pageIndex   page index ,start from 0
     * @param orderBy
     * @param whereClause
     * @param whereArgs
     * @return
     */
    public List<T> listPage(int count, int pageIndex, String orderBy, String whereClause, String... whereArgs) {
        if (count < 1 || pageIndex < 0) {
            throw new IllegalArgumentException("count and pageIndex can not be less than 1");
        }
        long offset = pageIndex * count;
        String limit = offset + "," + count;
        return find(whereClause, whereArgs, null, orderBy, limit);
    }

    public List<T> listLimitLater(int count, int offset) {
        String orderBy = mMetaData.primaryKey + " DESC ";
        return listLimit(count, offset, orderBy, null);
    }

    public List<T> listLimitEarlier(int count, int offset) {
        String orderBy = mMetaData.primaryKey + " ASC ";
        return listLimit(count, offset, orderBy, null);
    }

    public List<T> listLimit(int count, int offset) {
        return listLimit(count, offset, null, null);
    }

    public List<T> listLimit(int count, int offset, String orderBy) {
        return listLimit(count, offset, orderBy, null);
    }

    public List<T> listLimit(int count, int offset, String orderBy, String whereClause, String... whereArgs) {
        if (count < 1 || offset < 0) {
            throw new IllegalArgumentException("count and offset is not valid");
        }
        String limit = offset + "," + count;
        return find(whereClause, whereArgs, null, orderBy, limit);
    }

    public T findById(Long id) {
        if (id != null) {
            List<T> list = find(mMetaData.primaryKey + "=?", new String[]{String.valueOf(id)}, null, null, "1");
            if (list.isEmpty()) return null;
            return list.get(0);
        }
        return null;
    }

    public T findById(Integer id) {
        if (id == null) return null;
        return findById(Long.valueOf(id));
    }

    public Iterator<T> findAsIterator(String whereClause, String... whereArgs) {
        return findAsIterator(whereClause, whereArgs, null, null, null);
    }

    public Iterator<T> findAsIterator(String whereClause, String[] whereArgs, String groupBy, String orderBy, String limit) {
        Cursor cursor = getDatabase().query(mMetaData.tableName, null, whereClause, whereArgs, groupBy, null, orderBy, limit);
        return new EntityIterator(cursor);
    }

    public List<T> find(String whereClause, String[] whereArgs, String groupBy, String orderBy, String limit) {
        Cursor cursor = getDatabase().query(mMetaData.tableName, null, whereClause, whereArgs, groupBy, null, orderBy, limit);
        List<T> list = cursorToEntityList(cursor);
        closeDatabase();
        return list;
    }


    public List<T> find(String whereClause, String... whereArgs) {
        return find(whereClause, whereArgs, null, null, null);
    }

    public List<T> findWithQuery(String query, String... arguments) {
        Cursor cursor = getDatabase().rawQuery(query, arguments);
        List<T> list = cursorToEntityList(cursor);
        closeDatabase();
        return list;
    }


    /**
     * If the id of this entity is null or less than 1 ,it will ignore the id and insert this entity directly;
     * Otherwise it will insert or replace this entity according to whether the id is exists or not;
     * 按照规定，主键值必须>0，若<=0则视为插入数据库。若大于0，
     * 判断要保存的数据行是否违反Unique约束，若违反Unique约束，则更新对应的数据行的无Unique约束的列的值。
     * 若不违反Unique约束，则直接插入数据。
     */
    public Long save(T entity) {
        if (entity == null) return null;
        ContentValues values = entityToValues(entity);
        Long entityId = getIdentity(entity);
        long id;
        if (entityId != null && entityId < 1) {
            values.putNull(mMetaData.primaryKey);
        }
        id = getDatabase().insertWithOnConflict(mMetaData.tableName, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        setIdentity(entity, id);
        closeDatabase();
        return id;
    }

    public void saveAll(Iterator<T> entities) {
        SQLiteDatabase db = getDatabase();
        db.beginTransaction();
        try {
            T entity;
            Long entityId;
            long id;
            ContentValues values;
            while (entities.hasNext()) {
                entity = entities.next();
                if (entity != null) {
                    entityId = getIdentity(entity);
                    values = entityToValues(entity);
                    if (entityId != null && entityId < 1) {
                        values.putNull(mMetaData.primaryKey);
                    }
                    id = db.insertWithOnConflict(mMetaData.tableName, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                    setIdentity(entity, id);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            closeDatabase();
        }
    }

    /**
     * 级联删除
     *
     * @param entity
     * @return 是否删除成功
     */
    public boolean delete(T entity) {
        if (entity == null) return false;
        if (getIdentity(entity) != null) {
            boolean result = getDatabase().delete(mMetaData.tableName, mMetaData.primaryKey + "=?",
                    new String[]{String.valueOf(getIdentity(entity))}) == 1;
            closeDatabase();

            return result;
        }
        return false;
    }

    public void deleteAll(Iterator<T> entities) {
        SQLiteDatabase db = getDatabase();
        db.beginTransaction();
        try {
            T entity;
            Long id;
            while (entities.hasNext()) {
                entity = entities.next();
                if (entity != null) {
                    id = getIdentity(entity);
                    if (id != null) {
                        db.delete(mMetaData.tableName, mMetaData.primaryKey + "=?",
                                new String[]{String.valueOf(id)});
                    }
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            closeDatabase();
        }
    }

    public int deleteAll() {
        return deleteAll((String) null);
    }

    public int deleteAll(String whereClause, String... whereArgs) {
        int result = getDatabase().delete(mMetaData.tableName, whereClause, whereArgs);
        closeDatabase();
        return result;
    }

    public long count() {
        return count(null);
    }

    public long count(String whereClause, String... whereArgs) {
        long result = -1;
        final SQLiteDatabase db = getDatabase();
        SQLiteStatement sqliteStatement = null;
        try {
            String filter = (whereClause == null || whereClause.trim().isEmpty()) ? "" : " where " + whereClause;
            sqliteStatement = db.compileStatement("SELECT count(1) FROM " + mMetaData.tableName + filter);

            if (whereArgs != null) {
                for (int i = whereArgs.length; i != 0; i--) {
                    sqliteStatement.bindString(i, whereArgs[i - 1]);
                }
            }
            result = sqliteStatement.simpleQueryForLong();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (sqliteStatement != null) {
                sqliteStatement.close();
            }
            closeDatabase();
        }
        return result;

    }

    public boolean exists() {
        return exists(null);
    }

    /**
     * 检查是否存在记录，比count更高效点
     *
     * @param whereClause
     * @param whereArgs
     * @return
     */
    public boolean exists(String whereClause, String... whereArgs) {

        boolean result = false;
        SQLiteDatabase db = getDatabase();

        String filter = (whereClause == null || whereClause.trim().isEmpty()) ? LIMIT_ONE : " where " + whereClause + LIMIT_ONE;
        SQLiteStatement sqliteStatement = null;
        try {
            sqliteStatement = db.compileStatement("SELECT EXISTS ( SELECT 1 FROM " + mMetaData.tableName + filter + ")");
            if (whereArgs != null) {
                for (int i = whereArgs.length; i != 0; i--) {
                    sqliteStatement.bindString(i, whereArgs[i - 1]);
                }
            }
            result = TRUE.equals(sqliteStatement.simpleQueryForString());
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (sqliteStatement != null) {
                sqliteStatement.close();
            }
            closeDatabase();
        }
        return result;
    }

    class EntityIterator implements Iterator<T> {

        final Cursor cursor;

        public EntityIterator(Cursor cursor) {
            this.cursor = cursor;
        }

        @Override
        public boolean hasNext() {
            return cursor != null && !cursor.isClosed() && !cursor.isAfterLast();
        }

        @Override
        public T next() {
            T entity = null;
            if (this.cursor == null || this.cursor.isAfterLast()) {
                closeDatabase();
                throw new NoSuchElementException();
            }
            if (this.cursor.isBeforeFirst()) {
                this.cursor.moveToFirst();
            }

            try {
                entity = cursorToEntity(this.cursor);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.cursor.moveToNext();
                if (this.cursor.isAfterLast()) {
                    this.cursor.close();
                    closeDatabase();
                }
            }
            return entity;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }


    public static class MetaData {

        private final String databaseName, tableName, primaryKey, createSql;

        public MetaData(String createSql, String databaseName, String tableName, String primaryKey) {
            this.createSql = createSql;
            this.databaseName = databaseName;
            this.tableName = tableName;
            this.primaryKey = primaryKey;
        }

        public String getCreateSql() {
            return createSql;
        }

        public String getDatabaseName() {
            return databaseName;
        }

        public String getPrimaryKey() {
            return primaryKey;
        }

        public String getTableName() {
            return tableName;
        }
    }

}
