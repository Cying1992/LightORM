package com.cying.lightorm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

public final class LightORM {

    static final String DAO_SUFFIX = "$$Dao";

    static final String PACKAGE_DAO_COLLECTIONS = "com.cying.lightorm";
    static final String CLASS_DAO_COLLECTIONS = "LightORM$$DaoCollections";

    static String defaultDatabaseName;

    private static boolean hasInit = false;

    private static final Map<Class<?>, BaseDao<?>> daoMap = new HashMap<>();

    private static final Map<String, Database> databaseMap = new HashMap<>();

    private static final Map<String, DatabaseConfiguration> databaseConfigurationMap = new HashMap<>();

    static <T> void saveDao(Class<T> entityClass, BaseDao<T> dao) {

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
        database.createSqls.add(metaData.getCreateSql());
        daoMap.put(entityClass, dao);
    }


    private static void checkInit() {
        if (!hasInit) {
            throw new IllegalStateException("你还未初始化LightORM");
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
        if (hasInit) {
            throw new IllegalStateException("你已经初始化过LightORM，只能初始化一次");
        }
        LightORM.defaultDatabaseName = defaultDatabaseConfiguration.getDatabaseName();

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

    /**
     * 打开数据库，打开后需要手动调用{@link #closeDatabase(String)}关闭数据库
     *
     * @param databaseName 数据库名称
     * @return 数据库
     */
    public static SQLiteDatabase openDatabase(String databaseName) {
        checkInit();
        return databaseMap.get(databaseName).open();
    }

    /**
     * 关闭数据库
     *
     * @param databaseName 数据库名称
     */
    public static void closeDatabase(String databaseName) {
        checkInit();
        databaseMap.get(databaseName).close();
    }

    @SuppressWarnings("unchecked")
    public static <T> BaseDao<T> getDao(Class<T> entityClass) {
        checkInit();
        return (BaseDao<T>) daoMap.get(entityClass);
    }

    /**
     * 保存实体到数据库
     *
     * @param entity 要保存的实体
     * @param <T>    实体类型
     * @return 插入的数据行主键值
     */
    @SuppressWarnings("unchecked")
    public static <T> long save(T entity) {
        BaseDao<T> baseDao = getDao((Class<T>) entity.getClass());
        return baseDao.save(entity);
    }

    /**
     * 删除保存在数据库的实体
     *
     * @param entity 要删除的实体
     * @param <T>    实体类型
     * @return 是否删除成功
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean delete(T entity) {
        BaseDao<T> baseDao = getDao((Class<T>) entity.getClass());
        return baseDao.delete(entity);
    }

    public static <T> Query<T> where(Class<T> entityClass) {
        return new Query<>(getDao(entityClass));
    }

    private LightORM() {
    }
}
