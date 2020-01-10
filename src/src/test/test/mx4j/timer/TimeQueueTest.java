/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.mx4j.timer;

import java.lang.reflect.Field;

import mx4j.timer.TimeQueue;
import mx4j.timer.TimeTask;
import test.MX4JTestCase;

/**
 * @version $Revision: 1.3 $
 */
public class TimeQueueTest extends MX4JTestCase
{
   public TimeQueueTest(String s)
   {
      super(s);
   }

   public void testStop() throws Exception
   {
      TimeQueue queue = new TimeQueue();
      queue.start();

      // Wait a while to let the thread start
      sleep(1000);

      final int sleep = 5000;

      // Post a task to simulate work
      TimeTask task = new TimeTask()
      {
         public void run()
         {
            sleep(sleep);
         }
      };

      queue.schedule(task);

      // Wait for the task to be executed
      sleep(1000);

      // Stop the queue. This will cause the task above to interrupt,
      // but we set the flag again as would be in a normal task
      queue.stop();

      // Wait until the task is finished; the TimeQueue should have cleaned up
      sleep(sleep);

      // I want to be sure the thread has really shutdown
      Field field = queue.getClass().getDeclaredField("thread");
      field.setAccessible(true);
      Thread thread = (Thread)field.get(queue);
      if (thread != null && thread.isAlive()) fail("TimeQueue not stopped");
   }
}
