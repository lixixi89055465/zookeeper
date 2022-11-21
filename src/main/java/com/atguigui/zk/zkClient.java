package com.atguigui.zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

import java.io.IOException;

public class zkClient {
    private String connectionString=
            "hadoop102:2181," +
            "hadoop103:2181," +
            "hadoop104:2181";
    private int sessionTimeOut=2000;

    @Test
    public void init() throws IOException {
        ZooKeeper zooKeeper = new ZooKeeper(connectionString, sessionTimeOut, new Watcher() {
            public void process(WatchedEvent watchedEvent) {

            }
        });
    }

}
