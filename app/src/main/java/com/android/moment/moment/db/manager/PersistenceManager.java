package com.android.moment.moment.db.manager;

import com.android.moment.moment.db.dao.ProfileDao;

public interface PersistenceManager {

    /**
     * initialize or restore database with name Database
     *
     * @param databaseName
     * @return
     */
    public boolean initDb(String databaseName);

    /**
     * @return the Dao to persist and retrieve Profiles
     */
    public ProfileDao getProfileDao();
}
