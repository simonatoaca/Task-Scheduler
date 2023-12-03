# APD Homework #2 - Task Scheduler
### Implemented by Toaca Alexandra Simona, 332CA

#### Dispatcher setup
- In the constructor, a **scheduling strategy** is assigned to the dispatcher, depending
on the chosen algorithm. (I used the Strategy design pattern here, avoiding unnecessary if
clauses everytime a task comes in).
- The host to which the incoming task is assigned is returned in the ```getReceiverHost```
method, common to all scheduling strategies.
- There is no list or queue of tasks in the dispatcher, the task allocation being done on
the spot, as fast as possible.

#### Host implementation
- The host uses a **PriorityBlockingQueue** for storing the tasks that are yet to be completed.
This type of queue is thread-safe and is suitable for the producer-consumer problem. The call
to ```take()``` is blocking, unblocking when a task is available to be retrieved from the queue.
- The priority queue orders the tasks decreasingly by their priority and increasingly by their id.
So the task with the highest priority and lowest id (from the ones with the same priority) is
extracted first from the queue when ```take()``` is called. The order in which the tasks entered the
system is preserved naturally.
- The workflow of the host is as follows:
    - initially, the queue is empty and no task is being executed, so the host waits for a task.
    - when the dispatcher calls ```addTask()``` for the first time on the host,
   it unblocks the ```take()``` method by adding the task to the queue.
    - the first task is taken from the queue and the host tries to execute it for ```getLeft()```
   seconds via the ```wait(currentTask.getLeft())``` call. Why getLeft? Because later the host might
   have tasks that have completed only partially, and need to execute for less amount of time.
    - the wait call transfers the host to a **waiting state**, so the thread itself does
   not consume CPU cycles while waiting.
    - during the wait, 2 things can happen: 
      - the thread finishes the wait uninterrupted, in which case the task is considered finished, and marked as such.
      Then, the host takes the next planned task from the queue (if any), or waits for one (similar
      to the first step).
      - the wait is interrupted by a ```notify()``` call in the ```addTask()``` method of the host. This
      essentially signals to the host that another task needs to be planned on that thread. If the
      incoming task has a higher priority than the current one (and this can be preempted), the current
      task is being put back in the queue, with its time left updated. The incoming task starts executing
      instead, for its own amount of time. If the incoming task does not lead to the current one's preemption,
      it goes in the queue.
    - this process goes on until ```shutdown()``` is called, adding a special task to the queue
      (end task), which signals to the thread to break out of the while(true) loop and end.
    - if ```shutdown()``` is called while there are still tasks left in the queue, those are
    processed before ending the thread, because the end task has an id of -1 and a priority of 0,
    being left last in the queue no matter what other tasks are there. So, the queue only gets to
    the end task after all the other tasks are executed.

#### Other implementation details:
  - I used ```notify()``` because there is only one thread (the host's) that needs to be notified,
  otherwise I could have used ```notifyAll()```.
  - The ```wait()``` and ```notify()``` methods are synchronized over ```MyHost.this```. I did not
  make the ```addTask()``` method synchronized because it lead to a deadlock when the queue was empty.
  - The ```InterruptedException``` is handled by interrupting the thread.

