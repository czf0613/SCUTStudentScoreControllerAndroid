package com.czf.student.db.capsule;

import android.content.Context;

import gen.DaoMaster;
import gen.DaoSession;

//DaoManager的封装，为数据库查询manager工厂化处理做准备
public class DaoManger
{
    /**
     * @Param 数据库名称
     * @Param 实例对象，是一个静态的数据
     * @Param DaoMaster.DevOpenHelper用于打开一个可读写的数据库
     * @Param DaoMaster数据库管理员
     * @Param DaoSession 用于管理事务以及同步方法
     * @Param 上下文
     */
    private static final String name="app.db";
    private static DaoManger daoManger;
    private DaoMaster.DevOpenHelper devOpenHelper;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private Context context;

    //获取上下文
    public DaoManger(Context context)
    {
        this.context = context;
    }

    //获取instance，这是一个静态的量
    public static DaoManger getInstance(Context context)
    {
        if(daoManger==null)
        {
            synchronized (DaoManger.class)
            {
                if(daoManger==null)
                {
                    daoManger=new DaoManger(context);
                }
            }
        }
        return daoManger;
    }

    //获取事务，注意是一个同步方法
    public synchronized DaoSession getDaoSession()
    {
        if(daoSession==null)
        {
            daoSession=getDaoMaster().newSession();
        }
        return daoSession;
    }

    //获取数据库管理对象
    private DaoMaster getDaoMaster()
    {
        if(daoMaster==null)
        {
            devOpenHelper=new DaoMaster.DevOpenHelper(context,name,null);
            //获取加密的数据库，密码是乱写的，这个的底层实现是一个AES2048
            daoMaster=new DaoMaster((devOpenHelper.getEncryptedWritableDb("'a4cYt?s9IV_QrB|Mkg;'Z>9#6&sj}$Egd{#(?M8a:l{(a*&/E&cbM'}T&4YJ4udAUEGWmnDFL&lAFTe)++nz%_@f^.rL@U2KHr5t^(#y_e'F&X&}nbqLr1@nFAD1+O'")));
        }
        return daoMaster;
    }
}
