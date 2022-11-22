package com.atguigu.case1;

import com.atguigui.zk.zkClient;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;

public class DistributeServer {
    private String connectionString =
            "hadoop102:2181," +
                    "hadoop103:2181," +
                    "hadoop104:2181";
    private int sessionTimeOut = 2000;
    private ZooKeeper zk;

    public static void main(String[] args) throws KeeperException, InterruptedException, IOException {
        DistributeServer server = new DistributeServer();
        //1 获取 zk链接
        server.getConnect();
        //2 注册服务器到zk集群
        server.regist(args[0]);
        //3 启动业务逻辑（睡觉 ）
        server.business();
    }

    private void business() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    private void regist(String hostname) throws KeeperException, InterruptedException {
        zk.create("/servers", hostname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(hostname + " is online!!");
    }

    private void getConnect() throws IOException {
        ZooKeeper zk = new ZooKeeper(connectionString, sessionTimeOut, new Watcher() {
            public void process(WatchedEvent watchedEvent) {

            }
        });
    }
}
