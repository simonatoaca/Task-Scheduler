import java.util.List;

/**
 * Task size based scheduling algorithm
 */
public class SITAStrategy extends SchedulingAlgorithmStrategy {
    protected SITAStrategy(List<Host> hosts) {
        super(hosts);
    }

    @Override
    public Host getReceiverHost(Task task) {
        int hostIdx = -1;
        switch (task.getType()) {
            case SHORT -> hostIdx = 0;
            case MEDIUM -> hostIdx = 1;
            case LONG -> hostIdx = 2;
        }

        return hosts.get(hostIdx);
    }
}
