package test;


import org.apache.helix.manager.zk.ZKHelixAdmin;
import org.apache.helix.model.IdealState.IdealStateModeProperty;
import org.apache.helix.model.IdealState.RebalanceMode;
import org.apache.helix.model.StateModelDefinition;

public class ClusterConfigInit {

    
    private static final String DROPPED = "DROPPED";
    private static final String RESOURCE = "MY_RESOURCE";
    private static final int PARTITIONS = 3;
    public static String LEADER = "LEADER";
    public static String REPLICA = "REPLICA";
    public static String OFFLINE = "OFFLINE";
    public static String DEFAULT_CLUSTER_NAME = "my_cluster";

    /**
     * Initialize Helix cluster
     * 
     * @param zkString
     *            ZooKeeper connection string
     */
    public static void init(String zkString) {
        ZKHelixAdmin admin = new ZKHelixAdmin(zkString);

        admin.addCluster(DEFAULT_CLUSTER_NAME, true);

        admin.addStateModelDef(DEFAULT_CLUSTER_NAME, 
                "LEADER_REPLICA", 
                generateConfigForMasterSlave(admin));
        admin.addResource(DEFAULT_CLUSTER_NAME, 
                RESOURCE, 
                PARTITIONS,
                "LEADER_REPLICA",
                RebalanceMode.FULL_AUTO.toString());

        admin.rebalance(DEFAULT_CLUSTER_NAME, RESOURCE, 2);
    }

       
    public static StateModelDefinition generateConfigForMasterSlave(ZKHelixAdmin admin)
    {
        StateModelDefinition.Builder builder = new StateModelDefinition.Builder(
                "LEADER_REPLICA");
            // Add states and their rank to indicate priority. Lower the rank higher the
            // priority
            builder.addState(LEADER, 1);
            builder.addState(REPLICA, 2);
            builder.addState(OFFLINE);
            builder.addState(DROPPED);
            // Set the initial state when the node starts
            builder.initialState(OFFLINE);

            // Add transitions between the states.
            builder.addTransition(OFFLINE, REPLICA);
            builder.addTransition(REPLICA, OFFLINE);
            builder.addTransition(REPLICA, LEADER);
            builder.addTransition(LEADER, OFFLINE);
            builder.addTransition(REPLICA, DROPPED);
            builder.addTransition(LEADER, DROPPED);
            builder.addTransition(OFFLINE, DROPPED);

            // set constraints on states.
            // static constraint
            builder.upperBound(LEADER, 1);
            // dynamic constraint, R means it should be derived based on the replica,
            // this allows use different replication factor for each resource without 
            //having to define a new state model
            builder.dynamicUpperBound(REPLICA, "R");

            StateModelDefinition statemodelDefinition = builder.build();
            return statemodelDefinition;
    }

}
