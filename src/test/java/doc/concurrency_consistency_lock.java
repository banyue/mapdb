package doc;

import org.mapdb.*;
import java.util.*;


public class concurrency_consistency_lock {

    public static void main(String[] args) {
        //a
        DB db = DBMaker.memoryDB().make();

        // there are two counters which needs to be incremented at the same time.
        Atomic.Long a = db.atomicLong("a");
        Atomic.Long b = db.atomicLong("b");


        // update those two counters together
        db.consistencyLock().readLock().lock(); //note readLock
        try{
            a.incrementAndGet();
            // if snapshot or commit would happen here, two counters would be inconsistent
            b.incrementAndGet();
        }finally {
            db.consistencyLock().readLock().unlock();
        }

        //now backup two counters (simulates taking snapshot)
        db.consistencyLock().readLock().lock();  //not writeLock
        try{
            System.out.println(
                    a.get() + " = " + b.get()
            );
        }finally {
            db.consistencyLock().readLock().unlock();
        }
        //z
    }
}
