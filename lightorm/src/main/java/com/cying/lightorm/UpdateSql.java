package com.cying.lightorm;

/**
 * Created by Cying on 17/11/28.
 */
class UpdateSql {
    int fromVersion;
    int toVersion;
    String[] sqls;

    static UpdateSql create(int fromVersion, int toVersion, String... sqls) {
        UpdateSql updateSql = new UpdateSql();
        updateSql.fromVersion = fromVersion;
        updateSql.toVersion = toVersion;
        updateSql.sqls = sqls;
        return updateSql;
    }
}
