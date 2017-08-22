package cn.com.bluemoon.delivery.db.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import cn.com.bluemoon.delivery.db.entity.ReqBody;

import cn.com.bluemoon.delivery.db.gen.ReqBodyDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig reqBodyDaoConfig;

    private final ReqBodyDao reqBodyDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        reqBodyDaoConfig = daoConfigMap.get(ReqBodyDao.class).clone();
        reqBodyDaoConfig.initIdentityScope(type);

        reqBodyDao = new ReqBodyDao(reqBodyDaoConfig, this);

        registerDao(ReqBody.class, reqBodyDao);
    }
    
    public void clear() {
        reqBodyDaoConfig.clearIdentityScope();
    }

    public ReqBodyDao getReqBodyDao() {
        return reqBodyDao;
    }

}