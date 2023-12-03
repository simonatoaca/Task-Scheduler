/* Implement this class. */

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

public class MyHost extends Host {
    // Flag inserted into the queue to signal shutdown
    private final Task endTask = new Task(-1, 0, 0, TaskType.SHORT, 0,false);
    private final BlockingQueue<Task> tasks = new PriorityBlockingQueue<>(10,
            Comparator.comparing(Task::getPriority, Comparator.reverseOrder()).thenComparing(Task::getId));
    private Task currentTask;
    private Task incomingTask;
    private double taskStartTime;

    @Override
    public synchronized void run() {
        taskStartTime = 0;

        // Execute tasks
        try {
            while (true) {
                // Get task from queue if there is none currently executing
                if (currentTask == null || currentTask.getLeft() == 0) {
                    currentTask = tasks.take();

                    // Shutdown was called
                    if (currentTask == endTask) {
                        break;
                    }

                    taskStartTime = Timer.getTimeDouble();
                }

                // Try to execute currentTask for getLeft() seconds
                wait(currentTask.getLeft());

                double duration = (Timer.getTimeDouble() - taskStartTime) * 1000L;
                currentTask.setLeft((long) (currentTask.getLeft() - duration));

                taskStartTime = Timer.getTimeDouble();

                if (currentTask.getLeft() <= 0 && currentTask.getFinish() == 0) {
                    // Set this to 0 as a "finish" flag for later checks
                    currentTask.setLeft(0);
                    currentTask.finish();
                    continue;
                }

                // The task is preempted
                if (currentTask.isPreemptible() && currentTask.getPriority() < incomingTask.getPriority()) {
                    tasks.add(currentTask);
                    currentTask = incomingTask;
                } else if (incomingTask != null) {
                    // The incoming task was not more important than the current one, so it goes in the queue
                    tasks.add(incomingTask);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void addTask(Task task) {
        if (currentTask == null || currentTask.getLeft() == 0) {
            tasks.add(task);
        } else {
            incomingTask = task;

            // Interrupt the wait call
            synchronized (MyHost.this) {
                notify();
            }
        }
    }

    @Override
    public int getQueueSize() {
        return tasks.size() + (currentTask != null && currentTask.getLeft() > 0 ? 1 : 0);
    }

    @Override
    public long getWorkLeft() {
        double workLeftQueue = tasks.stream().collect(Collectors.summarizingDouble(Task::getLeft)).getSum();
        double workLeftCurrentTask = (currentTask != null && currentTask.getLeft() > 0) ?
                (currentTask.getLeft() - (Timer.getTimeDouble() - taskStartTime) * 1000L) : 0;

        return Math.round((workLeftQueue + workLeftCurrentTask) / 1000);
    }

    @Override
    public void shutdown() {
        // Add termination flag to the queue
        tasks.add(endTask);
    }
}
