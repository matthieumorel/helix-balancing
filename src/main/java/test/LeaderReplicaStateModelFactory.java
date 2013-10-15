package test;
import org.apache.helix.NotificationContext;
import org.apache.helix.model.Message;
import org.apache.helix.participant.statemachine.StateModel;
import org.apache.helix.participant.statemachine.StateModelFactory;
import org.apache.helix.participant.statemachine.StateModelInfo;
import org.apache.helix.participant.statemachine.Transition;

public class LeaderReplicaStateModelFactory extends StateModelFactory<StateModel> {

    public static final String OFFLINE = "OFFLINE";
    public static final String LEADER = "LEADER";
    public static final String REPLICA = "REPLICA";
    public static final String DROPPED = "DROPPED";

    final String name;

    public LeaderReplicaStateModelFactory(String name) {
        this.name = name;
    }

    @Override
    public StateModel createNewStateModel(String stateUnitKey) {
        System.out.println("Assigning " + stateUnitKey + " to " + name);
        LeaderReplicaStateModel stateModel = new LeaderReplicaStateModel(name, stateUnitKey);
        return stateModel;
    }

    @StateModelInfo(states = "{'" + OFFLINE + "','" + REPLICA + "','" + LEADER + "'}", initialState = OFFLINE)
    public static class LeaderReplicaStateModel extends StateModel {
        final String name;
        final String partition;

        LeaderReplicaStateModel(String name, String partition) {
            this.name = name;
            this.partition = partition;
        }

        @Transition(from = OFFLINE, to = REPLICA)
        public void onBecomeSlaveFromOffline(Message message, NotificationContext context) {
            System.out.println(String.format("OFFLINE -> REPLICA (%s, %s)", new Object[] { name, partition }));
        }

        @Transition(from = REPLICA, to = LEADER)
        public void onBecomeLeaderFromReplica(Message message, NotificationContext context) {
            System.out.println(String.format("REPLICA -> LEADER (%s, %s)", new Object[] { name, partition }));
        }

        @Transition(from = LEADER, to = OFFLINE)
        public void onBecomeOfflineFromLeader(Message message, NotificationContext context) {
            System.out.println(String.format("LEADER -> OFFLINE (%s, %s)", new Object[] { name, partition }));
        }

        @Transition(from = LEADER, to = DROPPED)
        public void onBecomeDroppedFromLeader(Message message, NotificationContext context) {
            System.out.println(String.format("LEADER -> DROPPED (%s, %s)", new Object[] { name, partition }));
        }

        @Transition(from = REPLICA, to = DROPPED)
        public void onBecomeDroppedFromReplica(Message message, NotificationContext context) {
            System.out.println(String.format("REPLICA -> DROPPED (%s, %s)", new Object[] { name, partition }));
        }
        
        @Transition(from = OFFLINE, to = DROPPED)
        public void onBecomeDroppedFromOffline(Message message, NotificationContext context) {
            System.out.println(String.format("OFFLINE -> DROPPED (%s, %s)", new Object[] { name, partition }));
        }

    }
}