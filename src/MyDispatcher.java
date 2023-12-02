/* Implement this class. */

import java.util.List;

public class MyDispatcher extends Dispatcher {
    SchedulingAlgorithmStrategy schedulingAlgorithm;
    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);

        switch (algorithm) {
            case ROUND_ROBIN -> schedulingAlgorithm = new RRStrategy(hosts);
            case SHORTEST_QUEUE -> schedulingAlgorithm = new SQStrategy(hosts);
            case SIZE_INTERVAL_TASK_ASSIGNMENT -> schedulingAlgorithm = new SITAStrategy(hosts);
            case LEAST_WORK_LEFT -> schedulingAlgorithm = new LWLStrategy(hosts);
        }
    }

    @Override
    public void addTask(Task task) {
        // Allocate tasks according to the chosen algorithm
        schedulingAlgorithm.getReceiverHost(task).addTask(task);
    }
}
