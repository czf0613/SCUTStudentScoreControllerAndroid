package com.czf.student.helper

import com.alibaba.fastjson.JSON
import com.czf.student.db.LocalCache
import com.czf.student.db.capsule.ManagerFactory
import gen.LocalCacheDao

object LocalPreferences {
    private val localCacheManager=ManagerFactory.getLocalCacheManager()

    @JvmStatic
    fun put(label:String,content:Int){
        var exist:LocalCache?= localCacheManager.queryBuilder().where(LocalCacheDao.Properties.Label.eq(label)).unique()
        if(exist==null) {
            exist = LocalCache()
            exist.label=label
        }
        exist.content=content.toString()
        localCacheManager.save(exist)
    }

    @JvmStatic
    fun put(label:String,content:Long){
        var exist:LocalCache?= localCacheManager.queryBuilder().where(LocalCacheDao.Properties.Label.eq(label)).unique()
        if(exist==null) {
            exist = LocalCache()
            exist.label=label
        }
        exist.content=content.toString()
        localCacheManager.save(exist)
    }

    @JvmStatic
    fun put(label:String,content:Double){
        var exist:LocalCache?= localCacheManager.queryBuilder().where(LocalCacheDao.Properties.Label.eq(label)).unique()
        if(exist==null) {
            exist = LocalCache()
            exist.label=label
        }
        exist.content=content.toString()
        localCacheManager.save(exist)
    }

    @JvmStatic
    fun put(label:String,content:String){
        var exist:LocalCache?= localCacheManager.queryBuilder().where(LocalCacheDao.Properties.Label.eq(label)).unique()
        if(exist==null) {
            exist = LocalCache()
            exist.label=label
        }
        exist.content= content
        localCacheManager.save(exist)
    }

    @JvmStatic
    fun put(label:String,content:Any,clazz: Class<*>){
        var exist:LocalCache?= localCacheManager.queryBuilder().where(LocalCacheDao.Properties.Label.eq(label)).unique()
        if(exist==null) {
            exist = LocalCache()
            exist.label=label
        }
        exist.content= JSON.toJSONString(content)
        localCacheManager.save(exist)
    }

    @JvmStatic
    fun getInt(label: String):Int?{
        val exist:LocalCache?= localCacheManager.queryBuilder().where(LocalCacheDao.Properties.Label.eq(label)).unique()
        return exist?.content?.toInt()
    }

    @JvmStatic
    fun getLong(label: String):Long?{
        val exist:LocalCache?= localCacheManager.queryBuilder().where(LocalCacheDao.Properties.Label.eq(label)).unique()
        return exist?.content?.toLong()
    }

    @JvmStatic
    fun getString(label: String):String?{
        val exist:LocalCache?= localCacheManager.queryBuilder().where(LocalCacheDao.Properties.Label.eq(label)).unique()
        return exist?.content
    }

    @JvmStatic
    fun getDouble(label: String):Double?{
        val exist:LocalCache?= localCacheManager.queryBuilder().where(LocalCacheDao.Properties.Label.eq(label)).unique()
        return exist?.content?.toDouble()
    }

    @JvmStatic
    fun getObj(label: String, clazz: Class<*>): Any? {
        val exist:LocalCache?= localCacheManager.queryBuilder().where(LocalCacheDao.Properties.Label.eq(label)).unique()
        return JSON.parseObject(exist?.content,clazz)
    }
}