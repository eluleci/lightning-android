package com.android.moment.moment.db.dao;



public interface Dao<Entity> {
    //TODO fill with needed methods likeEntry the following examples
    public boolean createEntity(Entity e);
    public Entity readEntity(String id);
    public boolean updateEntiy(Entity e);
    public boolean deleteEntity(Entity e);
}
