package com.cying.lightorm;

/**
 * Created by Cying on 17/3/30.
 * email:chengying@souche.com
 */
public class Query<T> {


    private static final String SECTION_DELIMITER_AND = " AND ";
    private static final String SECTION_DELIMITER_OR = " OR ";

    private BaseDao<T> dao;
    private BaseDao.MetaData metaData;
    private StringBuilder section;
    private String sectionDelimiter;

    Query(BaseDao<T> dao) {
        this.dao = dao;
        this.metaData = dao.getMetaData();
    }

    private void checkSection() {
        if (section == null) {
            section = new StringBuilder();
        }

        if (sectionDelimiter != null) {
            section.append(sectionDelimiter);
            sectionDelimiter = null;
        }
    }

    Query<T> equalTo(String columnName, Object value) {
        checkSection();
        section.append(columnName).append("=?");
        return this;
    }

}
