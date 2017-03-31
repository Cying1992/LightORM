package com.cying.lightorm.sample;

import android.content.ContentValues;
import android.database.Cursor;

import com.cying.lightorm.BaseDao;

/**
 * Created by Cying on 17/3/29.
 * email:chengying@souche.com
 */
public class EntityDao extends BaseDao<Entity> {


    @Override
    public MetaData getMetaData() {
        return null;
    }

    @Override
    public Long getIdentity(Entity entity) {
        return entity.id;
    }

    @Override
    public void setIdentity(Entity entity, Long value) {
        entity.id = value;
    }

    @Override
    public Entity cursorToEntity(Cursor cursor) {
        Entity entity = new Entity();
        //entity.calendar = convertLongToCalendar(getLong(cursor, "calendar"));
        return null;
    }

    @Override
    public ContentValues entityToValues(Entity entity) {
        ContentValues contentValues = new ContentValues();
        return null;
    }
}
