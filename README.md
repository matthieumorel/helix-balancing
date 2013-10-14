

This program deploys a simple set of nodes that are coordinated through Helix with the AUTO_REBALANCE mode. 


The nodes do nothing except report state transitions.

We user a basic LEADER-REPLICA state model.

### Running the program

Simplest way is `mvn exec:java -Dexec.mainClass="test.TestRebalancing"`


### Results

		
		Starting instance Node:myhost:10000
		Assigning MY_RESOURCE_0 to Node:myhost:10000
		Assigning MY_RESOURCE_1 to Node:myhost:10000
		Assigning MY_RESOURCE_2 to Node:myhost:10000
		OFFLINE -> REPLICA (Node:myhost:10000, MY_RESOURCE_0)
		OFFLINE -> REPLICA (Node:myhost:10000, MY_RESOURCE_2)
		OFFLINE -> REPLICA (Node:myhost:10000, MY_RESOURCE_1)
		REPLICA -> LEADER (Node:myhost:10000, MY_RESOURCE_1)
		REPLICA -> LEADER (Node:myhost:10000, MY_RESOURCE_2)
		REPLICA -> LEADER (Node:myhost:10000, MY_RESOURCE_0)
		// so far we have all partitions assigned to node 0 with LEADER state
		// there are no replica
		
		Starting instance Node:myhost:10001
		Assigning MY_RESOURCE_2 to Node:myhost:10001
		OFFLINE -> REPLICA (Node:myhost:10001, MY_RESOURCE_2)
		
		Starting instance Node:myhost:10002
		Assigning MY_RESOURCE_1 to Node:myhost:10002
		OFFLINE -> REPLICA (Node:myhost:10002, MY_RESOURCE_1)
		
		Stopping instance Node:myhost:10000
		REPLICA -> LEADER (Node:myhost:10001, MY_RESOURCE_2)
		Assigning MY_RESOURCE_0 to Node:myhost:10002
		OFFLINE -> REPLICA (Node:myhost:10002, MY_RESOURCE_0)
		REPLICA -> LEADER (Node:myhost:10002, MY_RESOURCE_0)

		// There is no LEADER for MY_RESOURCE_1, and there are no replica for resources 0 and 2