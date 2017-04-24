package com.cying.lightorm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class LightORM {

    private static final String TAG = "LightORM";
    static final String DAO_SUFFIX = "$$Dao";

    static final String PACKAGE_DAO_COLLECTIONS = "com.cying.lightorm";
    static final String CLASS_DAO_COLLECTIONS = "LightORM$$DaoCollections";
    private static boolean DEBUG = false;
    private static final LightORM INSTANCE = new LightORM();
    private static boolean hasInit = false;

    public static LightORM getInstance() {
        if (!hasInit) {
            throw new IllegalStateException("你还未初始化LightORM,请先调用LightORM.init方法");
        }
        return INSTANCE;
    }

    public static void setDebug(boolean debug) {
        DEBUG = debug;
    }

    static void debug(String message) {
        if (DEBUG) {
            Log.i(TAG, message);
            System.out.println(message);
        }
    }

    /**
     * 初始化LightORM框架
     *
     * @param context
     * @param defaultDatabaseConfiguration 默认数据库的配置
     * @param otherDatabaseConfigurations  其他数据库的配置信息
     */
    public static void init(@NonNull Context context, @NonNull DatabaseConfiguration defaultDatabaseConfiguration, DatabaseConfiguration... otherDatabaseConfigurations) {
        INSTANCE.initSelf(context, defaultDatabaseConfiguration, otherDatabaseConfigurations);
    }


    private String defaultDatabaseName;

    private final Map<Class<?>, BaseDao<?>> daoMap = new HashMap<>();

    private final Map<String, Database> databaseMap = new HashMap<>();

    private final Map<String, DatabaseConfiguration> databaseConfigurationMap = new HashMap<>();

    <T> void saveDao(Class<T> entityClass, BaseDao<T> dao) {

        BaseDao.MetaData metaData = dao.getMetaData();
        String databaseName = metaData.getDatabaseName();
        if (TextUtils.isEmpty(databaseName)) {
            databaseName = defaultDatabaseName;
        }

        if (!databaseConfigurationMap.containsKey(databaseName)) {
            throw new IllegalStateException("未找到数据库" + databaseName + "的配置信息");
        }

        Database database = databaseMap.get(databaseName);
        if (database == null) {
            database = new Database(databaseName);
            databaseMap.put(databaseName, database);
        }
        database.putDao(metaData.getTableName(), dao);
        database.createSqls.add(metaData.getCreateSql());
        daoMap.put(entityClass, dao);
    }

    /**
     * 初始化LightORM框架
     *
     * @param context
     * @param defaultDatabaseConfiguration 默认数据库的配置
     * @param otherDatabaseConfigurations  其他数据库的配置信息
     */
    private void initSelf(Context context, DatabaseConfiguration defaultDatabaseConfiguration, DatabaseConfiguration... otherDatabaseConfigurations) {
        if (hasInit) {
            throw new IllegalStateException("你已经初始化过LightORM，只能初始化一次");
        }
        this.defaultDatabaseName = defaultDatabaseConfiguration.getDatabaseName();

        //保存配置信息
        databaseConfigurationMap.put(defaultDatabaseConfiguration.getDatabaseName(), defaultDatabaseConfiguration);
        if (otherDatabaseConfigurations != null) {
            for (DatabaseConfiguration configuration : otherDatabaseConfigurations) {
                String databaseName = configuration.getDatabaseName();
                if (databaseConfigurationMap.containsKey(databaseName)) {
                    throw new IllegalArgumentException("已经存在数据库" + databaseName + "的配置信息,请勿重复配置");
                }
                databaseConfigurationMap.put(configuration.getDatabaseName(), configuration);
            }
        }

        hasInit = true;
        try {
            //触发saveDao，收集所有BaseDao对象
            Class.forName(PACKAGE_DAO_COLLECTIONS + "." + CLASS_DAO_COLLECTIONS).newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("LightORM初始化失败", e);
        }

        //初始化SqliteOpenHelper
        for (Database database : databaseMap.values()) {
            database.init(context, databaseConfigurationMap.get(database.databaseName));
        }

        databaseConfigurationMap.clear();
    }

    @VisibleForTesting
    Database findDatabase(String databaseName) {
        return databaseMap.get(databaseName);
    }


    @NonNull
    public String getDefaultDatabaseName() {
        return defaultDatabaseName;
    }

    /**
     * 打开数据库，打开后需要手动调用{@link #closeDatabase(String)}关闭数据库
     *
     * @param databaseName 数据库名称
     * @return 数据库
     */
    public SQLiteDatabase openDatabase(@NonNull String databaseName) {
        databaseName = TextUtils.isEmpty(databaseName) ? defaultDatabaseName : databaseName;
        return databaseMap.get(databaseName).open();
    }

    @Nullable
    SQLiteDatabase getOpenedDatabase(@NonNull String databaseName) {
        databaseName = TextUtils.isEmpty(databaseName) ? defaultDatabaseName : databaseName;
        return databaseMap.get(databaseName).get();
    }

    /**
     * 打开默认数据库
     *
     * @return
     */
    public SQLiteDatabase openDatabase() {
        return openDatabase(defaultDatabaseName);
    }

    /**
     * 关闭默认数据库
     */
    public void closeDatabase() {
        closeDatabase(defaultDatabaseName);
    }

    /**
     * 关闭数据库
     *
     * @param databaseName 数据库名称
     */
    public void closeDatabase(@NonNull String databaseName) {
        databaseName = TextUtils.isEmpty(databaseName) ? defaultDatabaseName : databaseName;
        databaseMap.get(databaseName).close();
    }

    @SuppressWarnings("unchecked")
    <T> BaseDao<T> getDao(Class<T> entityClass) {
        BaseDao<T> dao = (BaseDao<T>) daoMap.get(entityClass);
        if (dao == null) {
            throw new IllegalArgumentException("未找到类" + entityClass.getCanonicalName() + "对应的BaseDao");
        }
        return dao;
    }

    /**
     * 根据数据库名和表名获取BaseDao，数据库和表必须存在，否则会抛出IllegalArgumentException异常
     *
     * @param databaseName
     * @param tableName
     * @return 返回对应的BaseDao, 若不存在 则返回null
     */
    BaseDao<?> getDao(String databaseName, String tableName) {
        Database database = databaseMap.get(databaseName);
        BaseDao<?> result = null;
        if (database != null) {
            result = database.getDao(tableName);
        }
        if (result == null) {
            throw new IllegalArgumentException("不存在表" + databaseName + "." + tableName);
        }
        return result;
    }


    /**
     * 保存实体到数据库,保存成功后会给{@code entity}设置主键字段值
     *
     * @param entity 要保存的实体
     * @param <T>    实体类型
     * @return 插入的数据行主键值
     */
    @SuppressWarnings("unchecked")
    public <T> long save(@NonNull T entity) {
        BaseDao<T> baseDao = getDao((Class<T>) entity.getClass());
        return baseDao.save(entity);
    }

    public <T> void saveAll(@NonNull Class<T> entityClass, @NonNull Iterator<T> entities) {
        BaseDao<T> baseDao = getDao(entityClass);
        baseDao.saveAll(entities);
    }

    public <T> void deleteAll(@NonNull Class<T> entityClass, @NonNull Iterable<T> entities) {
        BaseDao<T> baseDao = getDao(entityClass);
        baseDao.deleteAll(entities);
    }

    public <T> int deleteAll(@NonNull Class<T> entityClass) {
        return deleteAll(entityClass, (String) null);
    }

    public <T> int deleteAll(@NonNull Class<T> entityClass, @Nullable String whereClause, String... whereArgs) {
        BaseDao<T> baseDao = getDao(entityClass);
        return baseDao.deleteAll(whereClause, whereArgs);
    }

    /**
     * 根据条件删除
     *
     * @param query
     * @param <T>
     * @return 删除的数量
     * @see #where(Class)
     */
    public <T> int deleteAll(@NonNull Query<T> query) {
        query.checkValid();
        TableQuery<T> tableQuery = query.query;
        int count = query.dao.deleteAll(tableQuery.getSelection(), tableQuery.getSelectionArgs());
        query.reset();
        return count;
    }

    /**
     * 根据条件删除
     *
     * @param entityClass
     * @param condition
     * @param <T>
     * @return 删除的数量
     */
    public <T> int deleteAll(@NonNull Class<T> entityClass, @NonNull Condition<T> condition) {
        BaseDao<T> dao = getDao(entityClass);
        Query<T> query = new Query<>(dao);
        condition.where(query);
        query.checkValid();
        TableQuery<T> tableQuery = query.query;
        int count = dao.deleteAll(tableQuery.getSelection(), tableQuery.getSelectionArgs());
        query.reset();
        return count;
    }

    /**
     * 删除保存在数据库的实体
     *
     * @param entity 要删除的实体
     * @param <T>    实体类型
     * @return 是否删除成功
     */
    @SuppressWarnings("unchecked")
    public <T> boolean delete(@NonNull T entity) {
        BaseDao<T> baseDao = getDao((Class<T>) entity.getClass());
        return baseDao.delete(entity);
    }

    /**
     * 更新对应数据表
     *
     * @param entityClass 要更新的数据库表对应的实体类
     * @param <T>         实体类类型
     * @return
     */
    public <T> Update<T> beginUpdate(Class<T> entityClass) {
        return new Update<>(getDao(entityClass));
    }

    /**
     * 添加查询后处理器，即从数据库查询到实体后可对实体进行处理，如进行数据转换
     *
     * @param entityClass 数据库实体类
     * @param processor   查询后处理器
     * @param <T>         实体类型
     */
    public <T> void addQueryPostprocessor(@NonNull Class<T> entityClass, @NonNull EntityProcessor<T> processor) {
        BaseDao<T> baseDao = getDao(entityClass);
        baseDao.addQueryPostprocessor(processor);
    }

    /**
     * 添加保存预处理器，即保存到数据库前对实体进行处理,如在保存前对为null的字段设置默认值
     *
     * @param entityClass 数据库实体类
     * @param processor   保存预处理器
     * @param <T>         实体类型
     */
    public <T> void addSavePreprocessor(@NonNull Class<T> entityClass, @NonNull EntityProcessor<T> processor) {
        BaseDao<T> baseDao = getDao(entityClass);
        baseDao.addSavePreprocessor(processor);
    }

    /**
     * 执行sql语句,执行后会自动关闭数据库连接
     *
     * @param databaseName
     * @param sql
     * @param args
     */
    public void execSQL(String databaseName, String sql, Object... args) {
        SQLiteDatabase sqLiteDatabase = openDatabase(databaseName);
        sqLiteDatabase.execSQL(sql, args);
        closeDatabase(databaseName);
    }

    /**
     * 查询
     *
     * @param entityClass
     * @param <T>
     * @return
     */
    public <T> Query<T> where(@NonNull Class<T> entityClass) {
        return new Query<>(getDao(entityClass));
    }


    private LightORM() {
    }
}
