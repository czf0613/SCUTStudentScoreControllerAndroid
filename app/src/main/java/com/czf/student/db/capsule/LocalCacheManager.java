package com.czf.student.db.capsule;

import com.czf.student.db.LocalCache;

import org.greenrobot.greendao.AbstractDao;

public class LocalCacheManager extends BaseBeanManager<LocalCache,Integer> {
    public LocalCacheManager(AbstractDao dao) {
        super(dao);
    }
}
