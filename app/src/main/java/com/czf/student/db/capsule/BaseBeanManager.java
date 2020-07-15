package com.czf.student.db.capsule;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

//自己封装的greendao方法，其中的save方法被我修改了，greendao原生的save对应的是insert而不是update
public class BaseBeanManager<T, K>
{
    private AbstractDao<T, K> mDao;

    public BaseBeanManager(AbstractDao dao) {
        mDao = dao;
    }

    public  void save(T item) {
        mDao.insertOrReplace(item);
    }

    public void save(T... items) {
        mDao.insertOrReplaceInTx(items);
    }

    public void save(List<T> items) {
        mDao.insertOrReplaceInTx(items);
    }

    public void deleteByKey(K key) {
        mDao.deleteByKey(key);
    }

    public void delete(T item) {
        mDao.delete(item);
    }

    public void delete(T... items) {
        mDao.deleteInTx(items);
    }

    public void delete(List<T> items) {
        mDao.deleteInTx(items);
    }

    public void deleteAll() {
        mDao.deleteAll();
    }

    public void update(T item) {
        mDao.update(item);
    }

    public void update(T... items) {
        mDao.updateInTx(items);
    }

    public void update(List<T> items) {
        mDao.updateInTx(items);
    }

    public T query(K key) {
        return mDao.load(key);
    }

    public  List<T> queryAll() {
        return mDao.loadAll();
    }

    public List<T> query(String where, String... params) {
        return mDao.queryRaw(where, params);
    }

    public QueryBuilder<T> queryBuilder() {
        return mDao.queryBuilder();
    }

    public long count() {
        return mDao.count();
    }

    public void refresh(T item) {
        mDao.refresh(item);
    }

    public void detach(T item) {
        mDao.detach(item);
    }
}
