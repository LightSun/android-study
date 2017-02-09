package com.heaven7.android.pack.realm;

/**
 * Created by heaven7 on 2017/2/9.
 */

import java.util.List;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.R.attr.id;

/**
 * 1、支持的数据类型：
 * boolean, byte, short, int, long, float, double, String, Date and byte[]
 * 在Realm中byte, short, int, long最终都被映射成long类型
 * <p>
 * 2、注解说明
 *
 * @PrimaryKey ①字段必须是String、 integer、byte、short、 int、long 以及它们的封装类Byte, Short, Integer, and Long
 * <p>
 * ②使用了该注解之后可以使用copyToRealmOrUpdate()方法，通过主键查询它的对象，如果查询到了，则更新它，否则新建一个对象来代替。
 * <p>
 * ③使用了该注解将默认设置（@index）注解
 * <p>
 * ④使用了该注解之后，创建和更新数据将会慢一点，查询数据会快一点。
 * @Required 数据不能为null
 * @Ignore 忽略，即该字段不被存储到本地
 * @Index 为这个字段添加一个搜索引擎，这将使插入数据变慢、数据增大，但是查询会变快。建议在需要优化读取性能的情况下使用。
 */
public class RealTest {

    private Realm mRealm = Realm.getDefaultInstance();

    //================= begin 异步 ==============================/
    /**
     * 大多数情况下，Realm的增删改查操作足够快，可以在UI线程中执行操作。但是如果遇到较复杂的增删改查，或增删改查操作的数据较多时，就可以子线程进行操作。
     * 异步删除同理。
     */
    private void addDog(final Dog cat) {
        RealmAsyncTask task =  mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(cat);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // ToastUtil.showShortToast(mContext,"收藏成功");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                //ToastUtil.showShortToast(mContext,"收藏失败");
            }
        });
        //销毁 task.cancel().
    }

    public void testAsyncQuery(){
        RealmResults<Dog>   cats = mRealm.where(Dog.class).findAllAsync();
        cats.addChangeListener(new RealmChangeListener<RealmResults<Dog>>() {
            @Override
            public void onChange(RealmResults<Dog> element) {
                element= element.sort("id");
                List<Dog> datas = mRealm.copyFromRealm(element);
            }
        });
        //销毁监听 cats.removeChangeListeners();
    }

    //  ================= end 异步 ==============================

    public void testTransaction() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        User user = realm.createObject(User.class); // Create a new object
        user.setName("John");
        user.setEmail("john@corporation.com");
        realm.commitTransaction();
    }

    public void testTransaction2() {
        Realm realm = Realm.getDefaultInstance();

        User user = new User("John");
        user.setEmail("john@corporation.com");
// Copy the object to Realm. Any further changes must happen on realmUser
        realm.beginTransaction();
        realm.copyToRealm(user);
        realm.commitTransaction();
    }

    public void testTransaction3() {
        Realm mRealm = Realm.getDefaultInstance();

        final User user = new User("John");
        user.setEmail("john@corporation.com");

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(user);
            }
        });
    }

    // 也可以使用同上的beginTransaction和commitTransaction方法进行删除
    public void testDelete() {
        Realm mRealm = Realm.getDefaultInstance();
        final RealmResults<Dog> dogs = mRealm.where(Dog.class).findAll();

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Dog dog = dogs.get(5);
                dog.deleteFromRealm();
                //删除第一个数据
                dogs.deleteFirstFromRealm();
                //删除最后一个数据
                dogs.deleteLastFromRealm();
                //删除位置为1的数据
                dogs.deleteFromRealm(1);
                //删除所有数据
                dogs.deleteAllFromRealm();
            }
        });
    }

    public void testUpdate() {
        Realm mRealm = Realm.getDefaultInstance();
        Dog dog = mRealm.where(Dog.class).equalTo("id", id).findFirst();
        mRealm.beginTransaction();
        dog.setName("newName");
        mRealm.commitTransaction();
    }

    public List<Dog> testQueryAll() {
        Realm mRealm = Realm.getDefaultInstance();
        RealmResults<Dog> dogs = mRealm.where(Dog.class).findAll();
        return mRealm.copyFromRealm(dogs);
    }

    /**
     * 常见的条件如下（详细资料请查官方文档）：
     * between(), greaterThan(), lessThan(), greaterThanOrEqualTo() & lessThanOrEqualTo()
     * equalTo() & notEqualTo()
     * contains(), beginsWith() & endsWith()
     * isNull() & isNotNull()
     * isEmpty() & isNotEmpty()
     * <p>
     * ps: sum，min，max，average只支持整型数据字段
     *
     * @param id
     */
    public Dog testQuery(String id) {
        Realm mRealm = Realm.getDefaultInstance();
        Dog dog = mRealm.where(Dog.class).equalTo("id", id).findFirst();
        return dog;
    }

    /**
     * query （查询所有）
     */
    public List<Dog> testQueryOrder() {
        Realm mRealm = Realm.getDefaultInstance();
        RealmResults<Dog> dogs = mRealm.where(Dog.class).findAll();
        /**
         * 对查询结果，按Id进行排序，只能对查询结果进行排序
         */
        //增序排列
        dogs = dogs.sort("id");
        //降序排列
        dogs = dogs.sort("id", Sort.DESCENDING);
        return mRealm.copyFromRealm(dogs);
    }

    /**
     * 查询平均年龄
     */
    private void getAverageAge() {
        double avgAge = mRealm.where(Dog.class).findAll().average("age");
    }

    /**
     * 查询总年龄
     */
    private void getSumAge() {
        Number sum = mRealm.where(Dog.class).findAll().sum("age");
        int sumAge = sum.intValue();
    }

    /**
     * 查询最大年龄
     */
    private void getMaxId() {
        Number max = mRealm.where(Dog.class).findAll().max("age");
        int maxAge = max.intValue();
    }
}
