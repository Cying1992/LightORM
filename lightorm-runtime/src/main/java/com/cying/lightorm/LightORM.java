package com.cying.lightorm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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
        }
    }

    /**
     * 初始化LightORM框架
     *
     * @param context
     * @param defaultDatabaseConfiguration 默认数据库的配置
     * @param otherDatabaseConfigurations  其他数据库的配置信息
     */
    public static void init(Context context, DatabaseConfiguration defaultDatabaseConfiguration, DatabaseConfiguration... otherDatabaseConfigurations) {
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

        try {
            //触发saveDao，收集所有BaseDao对象
            Class.forName(PACKAGE_DAO_COLLECTIONS + "." + CLASS_DAO_COLLECTIONS);
        } catch (Exception e) {
            throw new IllegalStateException("LightORM初始化失败", e);
        }

        //初始化SqliteOpenHelper
        for (Database database : databaseMap.values()) {
            database.init(context, databaseConfigurationMap.get(database.databaseName));
        }

        databaseConfigurationMap.clear();
        hasInit = true;
    }

    public String getDefaultDatabaseName() {
        return defaultDatabaseName;
    }

    /**
     * 打开数据库，打开后需要手动调用{@link #closeDatabase(String)}关闭数据库
     *
     * @param databaseName 数据库名称
     * @return 数据库
     */
    public SQLiteDatabase openDatabase(String databaseName) {
        databaseName = TextUtils.isEmpty(databaseName) ? defaultDatabaseName : databaseName;
        return databaseMap.get(databaseName).open();
    }

    /**
     * 关闭数据库
     *
     * @param databaseName 数据库名称
     */
    public void closeDatabase(String databaseName) {
        databaseName = TextUtils.isEmpty(databaseName) ? defaultDatabaseName : databaseName;
        databaseMap.get(databaseName).close();
    }

    @SuppressWarnings("unchecked")
    public <T> BaseDao<T> getDao(Class<T> entityClass) {
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
    public BaseDao<?> getDao(String databaseName, String tableName) {
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
     * 保存实体到数据库
     *
     * @param entity 要保存的实体
     * @param <T>    实体类型
     * @return 插入的数据行主键值
     */
    @SuppressWarnings("unchecked")
    public <T> long save(T entity) {
        BaseDao<T> baseDao = getDao((Class<T>) entity.getClass());
        return baseDao.save(entity);
    }

    public <T> void saveAll(Class<T> entityClass, Iterator<T> entities) {
        BaseDao<T> baseDao = getDao(entityClass);
        baseDao.saveAll(entities);
    }

    public <T> void deleteAll(Class<T> entityClass, Iterator<T> entities) {
        BaseDao<T> baseDao = getDao(entityClass);
        baseDao.deleteAll(entities);
    }

    public <T> int deleteAll(Class<T> entityClass) {
        return deleteAll(entityClass, (String) null);
    }

    public <T> int deleteAll(Class<T> entityClass, String whereClause, String... whereArgs) {
        BaseDao<T> baseDao = getDao(entityClass);
        return baseDao.deleteAll(whereClause, whereArgs);
    }


    /**
     * 删除保存在数据库的实体
     *
     * @param entity 要删除的实体
     * @param <T>    实体类型
     * @return 是否删除成功
     */
    @SuppressWarnings("unchecked")
    public <T> boolean delete(T entity) {
        BaseDao<T> baseDao = getDao((Class<T>) entity.getClass());
        return baseDao.delete(entity);
    }


    /**
     * 添加实体类后处理器，即从数据库查询到实体后可对实体进行处理
     *
     * @param entityClass 数据库实体类
     * @param interceptor 实体后处理器
     * @param <T>         实体类型
     */
    public <T> void addEntityIntercetor(Class<T> entityClass, EntityInterceptor<T> interceptor) {
        BaseDao<T> baseDao = getDao(entityClass);
        baseDao.addEntityInterceptor(interceptor);
    }

    /**
     * 查询
     *
     * @param entityClass
     * @param <T>
     * @return
     */
    public <T> Query<T> where(Class<T> entityClass) {
        return new Query<>(getDao(entityClass));
    }


    private LightORM() {
    }
}
