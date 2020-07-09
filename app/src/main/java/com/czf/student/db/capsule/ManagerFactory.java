package com.czf.student.db.capsule;

import com.czf.student.helper.MyApplication;

//数据库工厂，用于获取各个表的管理员
public class ManagerFactory
{
    private static LocalCacheManager localCacheManager=new LocalCacheManager(DaoManger.getInstance(MyApplication.getContext()).getDaoSession().getLocalCacheDao());

    public static LocalCacheManager getLocalCacheManager(){
        return localCacheManager;
    }
}
