import java.util.Comparator;
import java.util.List;

/**
 * Shortest Queue scheduling algorithm
 */
public class SQStrategy extends SchedulingAlgorithmStrategy {
    protected SQStrategy(List<Host> hosts) {
        super(hosts);
    }
    
    @Override
    public Host getReceiverHost(Task task) {
        return hosts.stream().min(Comparator.comparingLong(Host::getQueueSize)).orElse(hosts.get(0));
    }
}
