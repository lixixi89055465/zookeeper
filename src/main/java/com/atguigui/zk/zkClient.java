package com.atguigui.zk;

import org.apache.zookeeper.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class zkClient {
    private String connectionString =
            "hadoop102:2181," +
                    "hadoop103:2181," +
                    "hadoop104:2181";
    private int sessionTimeOut = 2000;

    private ZooKeeper zkClient;

    @Before
    public void init() throws IOException {
        zkClient = new ZooKeeper(connectionString, sessionTimeOut, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                System.out.println("------------------------------------------" );
                List<String> children = null;
                try {
                    children = zkClient.getChildren("/", true);
                    for (String child : children) {
                        System.out.println(child);
                    }
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("------------------------------------------" );
            }
        });
    }

    @Test
    public void create() throws KeeperException, InterruptedException {
        String nodeCreated = zkClient.create("/atguigu", "ss.avi".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    @Test
    public void getChildren() throws KeeperException, InterruptedException {
        List<String> children = zkClient.getChildren("/", true);
        for (String child : children) {
            System.out.println(child);
        }
        //延时
        Thread.sleep(Long.MAX_VALUE);
    }

}
