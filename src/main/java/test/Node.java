package test;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.helix.HelixManager;
import org.apache.helix.HelixManagerFactory;
import org.apache.helix.InstanceType;
import org.apache.helix.manager.zk.ZKHelixAdmin;
import org.apache.helix.manager.zk.ZNRecordSerializer;
import org.apache.helix.manager.zk.ZkClient;
import org.apache.helix.model.InstanceConfig;
import org.apache.helix.participant.StateMachineEngine;


public class Node {
    
    int port;
    private HelixManager zkHelixManager;
    private String instanceName;
    
    public Node(int port) {
        
        this.port = port;
    }
    
    public void start() throws Exception {
        
        ZkClient zkClient = new ZkClient("localhost:2181");
        zkClient.setZkSerializer(new ZNRecordSerializer());
        ZKHelixAdmin admin = new ZKHelixAdmin(zkClient);
        instanceName = "Node:"
                + InetAddress.getLocalHost().getHostName() + ":" + port;
        InstanceConfig instanceConfig = new InstanceConfig(instanceName);
        instanceConfig.setHostName(InetAddress.getLocalHost().getHostName());
        instanceConfig.setPort(String.valueOf(port));
        instanceConfig.setInstanceEnabled(true);
        
        System.out.println(String.format("Starting instance %s", instanceName));
        
        admin.addInstance(ClusterConfigInit.DEFAULT_CLUSTER_NAME, instanceConfig);

        zkHelixManager = HelixManagerFactory.getZKHelixManager(
                ClusterConfigInit.DEFAULT_CLUSTER_NAME, instanceName,
                InstanceType.CONTROLLER_PARTICIPANT, "localhost:2181");
        StateMachineEngine stateMachine = zkHelixManager
                .getStateMachineEngine();

        LeaderReplicaStateModelFactory stateModelFactory = new LeaderReplicaStateModelFactory(instanceName);
        stateMachine.registerStateModelFactory("LEADER_REPLICA", stateModelFactory);
        zkHelixManager.connect();
    }
    
    public void stop() throws Exception {
        System.out.println(String.format("Stopping instance %s", instanceName));
        zkHelixManager.disconnect();
    }

}
