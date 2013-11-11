/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zookeepertest;

import java.io.IOException;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher;

/**
 *
 * @author Administrator
 */
public class ZookeeperTest {

    /**
     * @param args the command line arguments
     */
    private WriteLock lock;
    private String lockPath = "/lock";
    private ZooKeeper zooKeeper;

    public ZookeeperTest(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    /**
     * get distr lock
     *
     * Author: yuchao86@gmail.com
     *
     * @return boolean
     */
    public boolean lock() {
        lock = new WriteLock(zooKeeper, lockPath, null);
        try {
            while (true) {
                if (lock.lock()) {
                    return true;
                }

            }
        } catch (KeeperException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * unlock Author: yuchao86@gmail.com
     *
     */
    public void unlock() {
        lock.unlock();
    }

    public static void main(String args[]) {
        System.out.println("== start test the dist lock===");

        try {
            Watcher wh = new Watcher() {
                public void process(org.apache.zookeeper.WatchedEvent event) {
                    System.out.println(event.toString());
                }
            };
            System.out.println("==start connect the zooKeeper server===");

            ZooKeeper zooKeeper = new ZooKeeper("localhost:2181", 20000, wh);
            
            final ZookeeperTest zookeeperTest = new ZookeeperTest(zooKeeper);
            for (int i = 0; i < 100; i++) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (zookeeperTest.lock()) {
                            System.out.println("get the dist lock-----------");
                        }
                        zookeeperTest.unlock();
                    }
                });
            }
            Thread.sleep(200 * 10);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    /*
     ZooKeeper zk;
     zk = new ZooKeeper("10.18.6.245:2181", 500000, new Watcher() {
     @Override
     public void process(WatchedEvent event) {
     //dosomething
     }
     });
     zk.create("/root", "mydata".getBytes(), Ids.OPEN_ACL_UNSAFE, 
        CreateMode.PERSISTENT);

     zk.create("/root/childone", "childone".getBytes(), Ids.OPEN_ACL_UNSAFE, 
        CreateMode.PERSISTENT);

     zk.getChildren("/root", true);

     zk.getData("/root/childone", true, null);

     zk.setData("/root/childone", "childonemodify".getBytes(), -1);

     zk.delete("/root/childone", -1);

     zk.close();
     */
}


//////////////////////////////////////////////////////////////////