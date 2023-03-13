package com.geoway.atlas;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

public class curatorConnect {
    private static Logger log = LoggerFactory.getLogger(curatorConnect.class);
    private CuratorFramework client;

    @Before
    public void connect() {
        String connectString = "atlasmaster:2181,atlas01:2181,atlas02:2181";
        //String connectString = "172.16.76.129:2181";
        //重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 10);
        /**
         *
         */
        log.info("初始化zookeeper连接" + connectString);
        //CuratorFramework client= CuratorFrameworkFactory.newClient(connectString,retryPolicy);
        client = CuratorFrameworkFactory.builder().connectString(connectString)
                .sessionTimeoutMs(60 * 1000).connectionTimeoutMs(15 * 1000).retryPolicy(retryPolicy).namespace("atlas/plugins").build();
        //开启
        client.start();
    }

//    @Test
//    public void create(String nodeName,String nodeValue) throws Exception {
//        String path=client.create().forPath("/"+nodeName,nodeValue.getBytes());
//    }

    @Test
    public void GetAll() {
        List<String> nodepaths = null;
        try {
            nodepaths = client.getChildren().forPath("/");
        } catch (Exception e) {
            log.error("获取zookeeper节点失败");
            throw new RuntimeException(e);
        }
        for (String nodepath : nodepaths) {
            byte[] bytes;
            try {
                bytes = client.getData().forPath("/" + nodepath);
            } catch (Exception e) {
                log.error("获取zookeeper节点" + nodepath + "的值失败");
                throw new RuntimeException(e);
            }
            String nodeValue = new String(bytes);
            System.out.println(nodepath + "---" + nodeValue);
        }
    }

    @Test
    public void create() throws Exception {
        String tempPath1="/DemoRestService-v1.1.0";
        byte[] bytes1="{\"Ip\":\"172.16.67.176\",\"Host\":\"8811\",\"Cpu\":4.0,\"MemorySize\":70,\"Scale\":\"\",\"TotalPercentage\":0,\"UsedPercentage\":0,\"UsablePercentage\":0}".getBytes();
        String tempPath2="/DemoRestService-v1.2.0";
        byte[] bytes2="{\"Ip\":\"172.16.67.176\",\"Host\":\"8812\",\"Cpu\":4.0,\"MemorySize\":30,\"Scale\":\"\",\"TotalPercentage\":0,\"UsedPercentage\":0,\"UsablePercentage\":0}".getBytes();
        String tempPath3="/DemoRestService-v1.3.0";
        byte[] bytes3="{\"Ip\":\"172.16.67.176\",\"Host\":\"8813\",\"Cpu\":4.0,\"MemorySize\":5,\"Scale\":\"\",\"TotalPercentage\":0,\"UsedPercentage\":0,\"UsablePercentage\":0}".getBytes();
        String tempPath4="/DemoRestService-v1.4.0";
        byte[] bytes4="{\"Ip\":\"172.16.67.176\",\"Host\":\"8814\",\"Cpu\":4.0,\"MemorySize\":15,\"Scale\":\"\",\"TotalPercentage\":0,\"UsedPercentage\":0,\"UsablePercentage\":0}".getBytes();
        client.create().forPath(tempPath2 ,bytes2);
        //client.create().forPath(tempPath2+ "/8812",bytes2);
        //client.create().forPath("/service2");
        //client.create().forPath("/service3");
    }

    @Test
    public void set() throws Exception{
        String tempPath1="/DemoRestService-v1.1.0";
        byte[] bytes1="{\"Ip\":\"172.16.67.176\",\"Host\":\"8811\",\"Cpu\":4.0,\"MemorySize\":70,\"Scale\":\"\",\"TotalPercentage\":0,\"UsedPercentage\":0,\"UsablePercentage\":0}".getBytes();
        String tempPath2="/DemoRestService-v1.2.0";
        byte[] bytes2="{\"Ip\":\"172.16.67.176\",\"Host\":\"8812\",\"Cpu\":4.0,\"MemorySize\":30,\"Scale\":\"\",\"TotalPercentage\":0,\"UsedPercentage\":0,\"UsablePercentage\":0}".getBytes();
        String tempPath3="/DemoRestService-v1.3.0";
        byte[] bytes3="{\"Ip\":\"172.16.67.176\",\"Host\":\"8813\",\"Cpu\":4.0,\"MemorySize\":5,\"Scale\":\"\",\"TotalPercentage\":0,\"UsedPercentage\":0,\"UsablePercentage\":0}".getBytes();
        //String tempPath4="/service4";
        //byte[] bytes4="{\"Ip\":\"172.16.67.168\",\"Host\":\"8003\",\"Cpu\":4.0,\"MemorySize\":15,\"Scale\":\"\",\"TotalPercentage\":0,\"UsedPercentage\":0,\"UsablePercentage\":0}".getBytes();
        client.setData().forPath(tempPath1,bytes1);
        client.setData().forPath(tempPath2,bytes2);
        client.setData().forPath(tempPath3,bytes3);
        //client.create().forPath(tempPath2,bytes2);
        //client.setData().forPath(tempPath3,bytes3);
    }

    @Test
    public void delete() throws Exception {
        //client.delete().forPath("/DemoRestService-v1.4.0");
        //client.delete().forPath("/DemoRestService-v1.2.0/8812");
        //client.delete().forPath("/DemoRestService-v1.2.0");
        //client.delete().forPath("/DemoRestService-v1.3.0");
        client.delete().forPath("/DemoRestService-v1.2.0");
    }

    public void getMyNodeByAppoint(String node) {

    }

    @Test
    public void testNodeCache() throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, "/", true);
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
            System.out.println("子节点发生变化...");
            //获取类型
            PathChildrenCacheEvent.Type type = event.getType();
            //判断类型是否为update
                if(type.equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)){
                    System.out.println("子节点数据发生变化...");
                    event.getData().getData();
                }
            }
        });
        pathChildrenCache.start();
        while (true) {

        }
    }


    @After
    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
