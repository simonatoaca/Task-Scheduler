import java.util.List;

/**
 * Round Robin scheduling algorithm
 */
public class RRStrategy extends SchedulingAlgorithmStrategy {
    private int lastHost;
    private int numberOfHosts;
    protected RRStrategy(List<Host> hosts) {
        super(hosts);
        numberOfHosts = hosts.size();
        lastHost = -1; // So we get first host to be 0
    }

    @Override
    public Host getReceiverHost(Task task) {
        lastHost = (lastHost + 1) % numberOfHosts;
        return hosts.get(lastHost);
    }
}
