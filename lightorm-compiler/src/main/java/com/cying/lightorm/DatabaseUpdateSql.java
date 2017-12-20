package com.cying.lightorm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cying on 17/12/20.
 */
public class DatabaseUpdateSql {

    String database;
    List<Sql> updateSql = new ArrayList<>();

    public static class Sql {
        int from;
        int to;
        List<String> sql = new ArrayList<>();
    }
}
