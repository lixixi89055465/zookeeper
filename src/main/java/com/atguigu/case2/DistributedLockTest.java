package com.atguigu.case2;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;

public class DistributedLockTest {
    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        final DistributedLock lock1 = new DistributedLock();
        final DistributedLock lock2 = new DistributedLock();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock1.zklock();
                    System.out.println("线程1启动，获取到锁 ");
                    Thread.sleep(5000);
                    lock1.unZklock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock2.zklock();
                    System.out.println("线程2启动，获取到锁 ");
                    Thread.sleep(5000);
                    lock2.unZklock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
