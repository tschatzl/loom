/*
 * Copyright (c) 2019, 2022, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/**
 * @test
 * @summary Test virtual threads using Object.wait/notifyAll
 * @compile --enable-preview -source ${jdk.version} WaitNotify.java
 * @run testng/othervm --enable-preview WaitNotify
 */

import java.util.concurrent.Semaphore;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class WaitNotify {

    /**
     * Test virtual thread waits, notified by platform thread.
     */
    @Test
    public void testWaitNotify1() throws Exception {
        var lock = new Object();
        var ready = new Semaphore(0);
        var thread = Thread.ofVirtual().start(() -> {
            synchronized (lock) {
                ready.release();
                try {
                    lock.wait();
                } catch (InterruptedException e) { }
            }
        });
        // thread invokes notify
        ready.acquire();
        synchronized (lock) {
            lock.notifyAll();
        }
        thread.join();
    }

    /**
     * Test platform thread waits, notified by virtual thread.
     */
    @Test
    public void testWaitNotify2() throws Exception {
        var lock = new Object();
        var ready = new Semaphore(0);
        var thread = Thread.ofVirtual().start(() -> {
            ready.acquireUninterruptibly();
            synchronized (lock) {
                lock.notifyAll();
            }
        });
        synchronized (lock) {
            ready.release();
            lock.wait();
        }
        thread.join();
    }

    /**
     * Test virtual thread waits, notified by another virtual thread.
     */
    @Test
    public void testWaitNotify3() throws Exception {
        var lock = new Object();
        var ready = new Semaphore(0);
        var thread1 = Thread.ofVirtual().start(() -> {
            synchronized (lock) {
                ready.release();
                try {
                    lock.wait();
                } catch (InterruptedException e) { }
            }
        });
        var thread2 = Thread.ofVirtual().start(() -> {
            ready.acquireUninterruptibly();
            synchronized (lock) {
                lock.notifyAll();
            }
        });
        thread1.join();
        thread2.join();
    }

    /**
     * Test interrupt status set when calling Object.wait.
     */
    @Test
    public void testWaitNotify4() throws Exception {
        TestHelper.runInVirtualThread(() -> {
            Thread t = Thread.currentThread();
            t.interrupt();
            Object lock = new Object();
            synchronized (lock) {
                try {
                    lock.wait();
                    fail();
                } catch (InterruptedException e) {
                    // interrupt status should be cleared
                    assertFalse(t.isInterrupted());
                }
            }
        });
    }

    /**
     * Test interrupt when blocked in Object.wait.
     */
    @Test
    public void testWaitNotify5() throws Exception {
        TestHelper.runInVirtualThread(() -> {
            Thread t = Thread.currentThread();
            TestHelper.scheduleInterrupt(t, 1000);
            Object lock = new Object();
            synchronized (lock) {
                try {
                    lock.wait();
                    fail();
                } catch (InterruptedException e) {
                    // interrupt status should be cleared
                    assertFalse(t.isInterrupted());
                }
            }
        });
    }
}
