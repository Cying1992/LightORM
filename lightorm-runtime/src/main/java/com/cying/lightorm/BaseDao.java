package com.cying.lightorm;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Cying on 17/3/29.
 */
public abstract class BaseDao<T> {

    protected static <T> void saveDao(Class<T> entityClass, BaseDao<T> dao) {
        LightORM.getInstance().saveDao(entityClass, dao);
    }

    protected static Long valuesDateToLong(Date date) {
        return date == null ? 0 : date.getTime();
    }

    protected Boolean cursorIntToBoolean(Cursor cursor, String columnName) {
        int value = getInt(cursor, columnName);
        return value != 0;
    }

    protected Byte cursorIntToByte(Cursor cursor, String columnName) {
        int value = getInt(cursor, columnName);
        return (byte) value;
    }

    protected Date cursorLongToDate(Cursor cursor, String columnName) {
        long millis = getLong(cursor, columnName);
        Date date = new Date();
        date.setTime(millis);
        return date;
    }

    private final Map<String, Integer> sColumnIndexMap = new HashMap<>();

    private MetaData mMetaData;

    private Map<String, FieldType> mFieldTypes;
    private List<EntityInterceptor<T>> mEntityInterceptors;

    protected BaseDao() {
        mMetaData = getMetaData();
    }

    void addEntityInterceptor(EntityInterceptor<T> interceptor) {
        if (mEntityInterceptors == null) {
            mEntityInterceptors = new ArrayList<>();
        }
        mEntityInterceptors.add(interceptor);
    }

    FieldType getFieldType(String columnName) {
        if (mFieldTypes == null) {
            mFieldTypes = collectFieldTypes();
        }
        return mFieldTypes.get(columnName);
    }

    /**
     * 检查列的数据类型是否一致
     *
     * @param columnName
     * @param fieldTypes
     * @return
     */
    boolean isColumnValid(String columnName, FieldType... fieldTypes) {
        if (TextUtils.isEmpty(columnName)) {
            return false;
        }
        if (mFieldTypes == null) {
            mFieldTypes = collectFieldTypes();
        }

        FieldType targetType = mFieldTypes.get(columnName);
        if (targetType == null) {
            return false;
        }

        if (fieldTypes == null || fieldTypes.length == 0) {
            return true;
        }

        for (FieldType type : fieldTypes) {
            if (type == targetType) {
                return true;
            }
        }
        return false;
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

    protected abstract HashMap<String, FieldType> collectFieldTypes();

    SQLiteDatabase getDatabase() {
        return LightORM.getInstance().openDatabase(mMetaData.databaseName);
    }

    void closeDatabase() {
        LightORM.getInstance().closeDatabase(mMetaData.databaseName);
    }

    List<T> cursorToEntityList(Cursor cursor) {
        if (cursor == null) {
            return Collections.emptyList();
        }

        T entity;
        try {
            List<T> result = new ArrayList<>();
            while (cursor.moveToNext()) {
                entity = cursorToEntity(cursor);
                if (mEntityInterceptors != null) {
                    for (EntityInterceptor<T> interceptor : mEntityInterceptors) {
                        entity = interceptor.process(entity);
                    }
                }
                result.add(entity);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return Collections.emptyList();
    }

    /**
     * If the id of this entity is null or less than 1 ,it will ignore the id and insert this entity directly;
     * Otherwise it will insert or replace this entity according to whether the id is exists or not;
     * 按照规定，主键值必须>0，若<=0则视为插入数据库。若大于0，
     * 判断要保存的数据行是否违反Unique约束，若违反Unique约束，则更新对应的数据行的无Unique约束的列的值。
     * 若不违反Unique约束，则直接插入数据。
     */
    Long save(T entity) {
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

    void saveAll(Iterator<T> entities) {
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
    boolean delete(T entity) {
        if (entity == null) return false;
        if (getIdentity(entity) != null) {
            boolean result = getDatabase().delete(mMetaData.tableName, mMetaData.primaryKey + "=?",
                    new String[]{String.valueOf(getIdentity(entity))}) == 1;
            closeDatabase();

            return result;
        }
        return false;
    }

    void deleteAll(Iterator<T> entities) {
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

    int deleteAll(String whereClause, String... whereArgs) {
        int result = getDatabase().delete(mMetaData.tableName, whereClause, whereArgs);
        closeDatabase();
        return result;
    }

    protected static class MetaData {

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

    /**
     * Created by Cying on 17/3/31.
     */
    protected enum FieldType {

        INTEGER,
        BOOLEAN,
        STRING,
        BINARY,
        DATE,
        FLOAT,
        DOUBLE
    }
}
