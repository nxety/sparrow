package com.sparrow.redis;

import com.sparrow.cache.CacheClient;
import com.sparrow.cache.CacheDataNotFound;
import com.sparrow.constant.cache.KEY;
import com.sparrow.container.Container;
import com.sparrow.container.impl.SparrowContainerImpl;
import com.sparrow.exception.CacheConnectionException;
import com.sparrow.support.ModuleSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harry on 2018/1/26.
 */
public class RedisListTest {
    public static void main(String[] args) throws CacheConnectionException {
        Container container = new SparrowContainerImpl("/redis_config.xml");
        //定义模块，一个业务会存在多个模块
        ModuleSupport OD = new ModuleSupport() {
            @Override
            public String code() {
                return "01";
            }

            @Override
            public String name() {
                return "OD";
            }
        };


        //相同模块下会存在多个业务
        KEY.Business od = new KEY.Business(OD, "POOL");
        KEY key = new KEY.Builder().business(od).businessId("BJS", "CHI", "HU","LIST").build();

        container.init();
        CacheClient client = container.getBean("cacheClient");
        client.key().delete(key);
        client.list().add(key, 1);
        client.list().add(key, "1", "2", "3", "4", "end");

        System.out.println(client.list().getSize(key));

        List<Object> list = new ArrayList<Object>();
        list.add("s1");
        list.add("s2");
        list.add("s3");
        client.list().add(key, list);
        System.out.println(client.list().getSize(key));

        client.key().delete(key);
        List<String> fromdb = client.list().list(key, new CacheDataNotFound<List<String>>() {
            @Override
            public List<String> read(KEY key) {
                List<String> set = new ArrayList<String>();
                set.add("from db");
                return set;
            }
        });

        for (String db : fromdb) {
            System.out.println(db);
        }

        client.key().delete(key);
        List<RedisEntity> set=new ArrayList<RedisEntity>();
        set.add(new RedisEntity(1,"1"));
        client.list().add(key,set);
        set=client.list().list(key,RedisEntity.class);
        for(RedisEntity re:set){
            System.out.println(re.getId()+"-"+re.getName());
        }
    }
}
