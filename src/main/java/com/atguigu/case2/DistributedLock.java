package com.atguigu.case2;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DistributedLock {
    private final ZooKeeper zk;
    private String connectString =
            "hadoop102:2181," +
                    "hadoop103:2181," +
                    "hadoop104:2181";
    private int sessionTimeOut = 2000;
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private CountDownLatch waitLatch = new CountDownLatch(1);
    private String waitPath;
    private String currentMode;
    private String rootNode = "locks";
    private String subNode = "seq-";

    public DistributedLock() throws IOException, InterruptedException, KeeperException {
        //获取连接
        zk = new ZooKeeper(connectString, sessionTimeOut, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                //connectLatch 如果连接上 zk 可以释放
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    countDownLatch.countDown();
                }
                // wait Latch 需要释放
                if (event.getType() == Event.EventType.NodeDeleted && event.getPath().equals(waitPath)) {
                    waitLatch.countDown();
                }
            }
        });
        //等待zk 正常连接后，往下走程序
        countDownLatch.await();
        //判断根节点/locks 是否存在
        Stat stat = zk.exists("/" + rootNode, false);
        if (stat == null) {
            //创建一下根节点
            System.out.println("根节点不存在 ");
            zk.create("/" + rootNode, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    // 对 zk 加锁
    public void zklock() {
        try {
            currentMode = zk.create("/" + rootNode + "/" + subNode, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            //wait 一小会 ，让结果清晰一点
            Thread.sleep(10);
            //创建对应的临时带序号节点
            List<String> children = zk.getChildren("/" + rootNode, false);

            //判断创建的节点是否是最小的序号节点，如果是获取到锁，如果不是，则监听
            if (children.size() == 1) {
                return;
            } else {
                Collections.sort(children);
                //获取节点名称
                String thisNode = currentMode.substring(("/" + rootNode + "/").length());
                //通过节点名称获取该节点在children集合的位置
                int index = children.indexOf(thisNode);
                //判断
                if (index == -1) {
                    System.out.println("数据异常 ");
                } else if (index == 0) {
                    //就一个节点，可以获取锁了
                    return;
                } else {
                    //需要监听 他前一个节点变化
                    waitPath = "/"+rootNode +"/"+ children.get(index - 1);
                    zk.getData(waitPath, true, new Stat());
                    //等待监听
                    waitLatch.await();
                    return;
                }
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //解锁
    public void unZklock() {
        //删除节点
        try {
            zk.delete(currentMode, -1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

    }
}
