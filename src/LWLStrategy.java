import java.util.Comparator;
import java.util.List;

/**
 * Least Work Left scheduling algorithm
 */
public class LWLStrategy extends SchedulingAlgorithmStrategy{
    protected LWLStrategy(List<Host> hosts) {
        super(hosts);
    }

    @Override
    public Host getReceiverHost(Task task) {
        return hosts.stream().min(Comparator.comparingLong(Host::getWorkLeft).thenComparing(Host::getId)).orElse(hosts.get(0));
    }
}
