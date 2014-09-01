package kilim.examples;

import java.util.Arrays;
import java.util.Comparator;

import kilim.Mailbox;
import kilim.Mailbox;
import kilim.ExitMsg;
import kilim.MailboxSPSC;
import kilim.Pausable;
import kilim.Task;

public class TestCell_Old extends Task {

    public static final int QUEUE_CAPACITY = 32*1024 ;
    public static final int PRODUCERS = Integer.getInteger("producers", 5);
    public static final long REPETITIONS = PRODUCERS
            * ((Integer.getInteger("reps", 32) * 1024*1024) / PRODUCERS);
    public static final Integer TEST_VALUE = Integer.valueOf(777);
    private static final Comparator<Producer2> START_TIME_COMPARATOR = new Comparator<Producer2>() {

        @Override
        public int compare(Producer2 o1, Producer2 o2) {
            return (int) (o2.start - o1.start);
        }

    };
    private static final Comparator<Consumer2> END_TIME_COMPARATOR = new Comparator<Consumer2>() {

        @Override
        public int compare(Consumer2 o1, Consumer2 o2) {
            return (int) (o2.end - o1.end);
        }

    };
    private static final int ITERATIONS =1000*100;
    private static final int CELLS = 100;

    public static void main(final String[] args) throws Exception {
        // System.out.println("capacity:" + QUEUE_CAPACITY + " reps:"
        // + REPETITIONS + " producers:" + PRODUCERS);

        final Mailbox<Integer> mbox[] = new Mailbox[CELLS];
        // final Mailbox<Integer> mbox1[] = new Mailbox[CELLS];

        for (int i = 0; i < CELLS; i++) {
            mbox[i] = new Mailbox<Integer>(QUEUE_CAPACITY,QUEUE_CAPACITY);
            // mbox1[i] = new Mailbox<Integer>(QUEUE_CAPACITY);
        }
        for (int i = 0; i < ITERATIONS; i++) {
            System.gc();
            // Thread.sleep(1000);
            performanceRun(i, mbox);
            Thread.sleep(100);

        }
        System.exit(0);
    }

    private static long performanceRun(final int runNumber,
            final Mailbox<Integer> mbox[]) {
        try {
            Consumer2 ci[] = new Consumer2[CELLS];
            Producer2 pi[] = new Producer2[CELLS * PRODUCERS];
            Mailbox<ExitMsg> exitmb1 = new Mailbox<ExitMsg>();
            Mailbox<ExitMsg> exitmb2 = new Mailbox<ExitMsg>();
            for (int i = 0; i < CELLS; i++) {
                for (int j = 0; j < PRODUCERS; j++) {
                    pi[(i*PRODUCERS) + j] = new Producer2(mbox[i]);
                    pi[(i*PRODUCERS) + j].informOnExit(exitmb1);
                }
                ci[i] = new Consumer2(mbox[i]);
                ci[i].informOnExit(exitmb2);

            }

            perfTest(ci, pi, exitmb1, exitmb2);
            Arrays.sort(pi, START_TIME_COMPARATOR);
            Arrays.sort(ci, END_TIME_COMPARATOR);
            // System.out.println((ci[0].end > ci[ci.length - 1].end) + ":"
            // + (pi[0].start > pi[pi.length - 1].start));
            long duration = ci[0].end - pi[pi.length - 1].start;
            // int sum2 = 0;
            // int sum1 = 0;
            // for (int i = 0; i < pi.length; i++) {
            // sum1 += pi[i].putFailCount;
            // sum2 += ci[i].getFailCount;
            // }
            // for (int i = 0; i < ci.length; i++) {
            // sum1 += pi[i].putFailCount;
            // sum2 += ci[i].getFailCount;
            // }
            // System.out.println("put fail: " + sum1 + "get fail:" + sum2);

            final long ops = (REPETITIONS * CELLS * 1000L) / duration;
            // final long avgTime = (2 * duration) / (100 * CELLS);
            // System.out.println("avgtime::" + avgTime);
            // System.out.format("%d - ops/sec=%,d -result=%d , -time=%d\n",
            // Integer.valueOf(runNumber), Long.valueOf(ops), ci[0].result,
            // duration);
            System.out.println(ops);
            return ops;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static void perfTest(Consumer2[] c, Producer2[] p,
            Mailbox<ExitMsg> m1, Mailbox<ExitMsg> m2) {
        for (int i = 0; i < CELLS; i++) {
            c[i].start();
            for (int j = 0; j < PRODUCERS; j++) {
                p[(i*PRODUCERS) + j].start();
            }
        }
        for (int i = 0; i < CELLS; i++) {
            for (int j = 0; j < PRODUCERS; j++) {
                m1.getb();
            }
            m2.getb();
        }
    }

    public static class Consumer2 extends Task {
        // final Mailbox<Integer> queue1;
        final Mailbox<Integer> queue;
        Integer result;
        long end;
        int getFailCount;
        int putfailCount;

        public Consumer2(Mailbox<Integer> queue) {
            this.queue = queue;
            getFailCount = 0;
            putfailCount = 0;
            // this.queue1 = queue1;
        }

        public void execute() throws Pausable {
            Integer result = 777;
            long i = REPETITIONS;
            // long start = System.currentTimeMillis();
            do {
                // if ((i % 2) == 0) {
                result = queue.get();

                // // if (result == null) {
                // // getFailCount++;
                // // } else {
                i--;
                // // }
                // } else {
                // queue1.put(TEST_VALUE);
                // i--;
                // // } else {
                // // putfailCount++;
                // // }
                // }
                // queue.put(TEST_VALUE, 20);
            //    System.out.println("get:"+i);
            } while (0 != i);
           this.result = result;
            end = System.currentTimeMillis();
            // long duration =end-start;
            // System.out.println("Consumer: "+ (duration/REPETITIONS));
        }
    }

    public static class Producer2 extends Task {
        private final Mailbox<Integer> queue;
        // private final Mailbox<Integer> queue1;
        long start;
        int getFailCount;
        int putFailCount;

        public Producer2(final Mailbox<Integer> queue) {
            this.queue = queue;
            putFailCount = 0;
            getFailCount = 0;
            // this.queue1 = queue1;
        }

        public void execute() throws Pausable {
            start = System.currentTimeMillis();
            // Integer result = TEST_VALUE;
            long i = REPETITIONS / PRODUCERS;
            do {
                // if ((i % 2) == 0) {
                // queue.put(TEST_VALUE);
                // i--;
                // // } else {
                // // putFailCount++;
                // // }
                // } else {
                // result = queue1.get();
                // // if (result == null) {
                // // getFailCount++;
                // // } else {
                // i--;
                // // }
                // }
                queue.put(TEST_VALUE);
                // Task.yield();
                i--;
                // queue.get(20);
                // System.out.println();
            } while (0 != i);
            // long duration = System.currentTimeMillis()-start;
            // System.out.println("producer: "+ (duration/(REPETITIONS /
            // PRODUCERS)));
        }
    }
    // public static class TimerMiss1 extends Task {
    // final Mailbox<Integer> queue;
    // Integer result;
    // long end;
    // int getFailCount;
    //
    // public TimerMiss1(Mailbox<Integer> queue) {
    // this.queue = queue;
    // getFailCount = 0;
    // }
    //
    // public void execute() throws Pausable {
    // Integer result;
    // long i = 1000;
    // System.out.println("get started");
    // long start= System.currentTimeMillis();
    // do {
    // result = queue.get();
    // // if (result == null) {
    // // getFailCount++;
    // // } else {
    // // i--;
    // // }
    // } while (0 != --i);
    // long duration = (System.currentTimeMillis()-start) /1000;
    // System.out.println("get succed"+ duration);
    // this.result = result;
    // end = System.nanoTime();
    // }
    // }
}
