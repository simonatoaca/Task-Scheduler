import java.util.List;

/**
 * Template for a scheduling algorithm
 */
public abstract class SchedulingAlgorithmStrategy {
    protected List<Host> hosts;

    protected SchedulingAlgorithmStrategy(List<Host> hosts) {
        this.hosts = hosts;
    }

    public abstract Host getReceiverHost(Task task);
}
