package test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.I0Itec.zkclient.IDefaultNameSpace;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkServer;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestRebalancing {

    private static ZkServer zkServer;
    final static int NB_NODES = 3;

    public static void main(String[] args) {
        try {

            // init things
            File dataDir = new File(System.getProperty("java.io.tmpdir") + "/data");
            cleanRecursively(dataDir);
            File logDir = new File(System.getProperty("java.io.tmpdir") + "/log");
            cleanRecursively(logDir);

            zkServer = new ZkServer(dataDir.getAbsolutePath(), logDir.getAbsolutePath(), new IDefaultNameSpace() {

                public void createDefaultNameSpace(ZkClient zkClient) {
                }
            }, 2181);
            zkServer.start();

            ClusterConfigInit.init("localhost:2181");

            // instantiate nodes
            List<Node> nodes = new ArrayList<Node>();

            for (int i = 0; i < NB_NODES; i++) {
                nodes.add(new Node(10000 + i));
            }

            // start nodes
            for (Node node : nodes) {
                node.start();
                Thread.sleep(2000);
            }

            // stop 1 node
            nodes.iterator().next().stop();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    private static void cleanRecursively(File f) {
        if (f.isDirectory()) {
            for (File contained : f.listFiles())
                cleanRecursively(contained);
        }
        if (!f.delete()) {
            if (f.exists()) {
                throw new RuntimeException("Cannot delete " + f.getAbsolutePath());
            }
        }
    }

    

}
