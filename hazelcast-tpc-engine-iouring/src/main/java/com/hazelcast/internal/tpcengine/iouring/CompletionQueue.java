/*
 * Copyright (c) 2008-2023, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.internal.tpcengine.iouring;

import com.hazelcast.internal.tpcengine.logging.TpcLogger;
import com.hazelcast.internal.tpcengine.logging.TpcLoggerLocator;
import com.hazelcast.internal.tpcengine.util.UnsafeLocator;
import sun.misc.Unsafe;

import java.io.UncheckedIOException;

import static com.hazelcast.internal.tpcengine.iouring.IOUring.opcodeToString;
import static com.hazelcast.internal.tpcengine.iouring.Linux.errorcode;
import static com.hazelcast.internal.tpcengine.iouring.Linux.strerror;
import static com.hazelcast.internal.tpcengine.util.ExceptionUtil.newUncheckedIOException;

@SuppressWarnings("checkstyle:VisibilityModifier")
public final class CompletionQueue {

    public static final Unsafe UNSAFE = UnsafeLocator.UNSAFE;
    public static final int OFFSET_CQE_USERDATA = 0;
    public static final int OFFSET_CQE_RES = 8;
    public static final int OFFSET_CQE_FLAGS = 12;
    public static final int CQE_SIZE = 16;

    public int localHead;
    public int localTail;

    public long headAddr;
    public long tailAddr;
    public long cqesAddr;

    public int ringMask;

    private final TpcLogger logger = TpcLoggerLocator.getLogger(CompletionQueue.class);
    private final IOUring uring;

    CompletionQueue(IOUring uring) {
        this.uring = uring;
    }

    static UncheckedIOException newCompletionFailedException(String msg, int opcode, int errnum) {
        return newUncheckedIOException(msg + " "
                + "Opcode " + opcodeToString(opcode) + " failed with error " + errorcode(errnum)
                + " '" + strerror(errnum) + "'. "
                + "Go to https://man7.org/linux/man-pages/man2/io_uring_enter.2.html section 'CQE ERRORS', "
                + "for a proper explanation of the errorcode.");
    }

    /**
     * Checks if there are any completion events.
     *
     * @return true if there are any completion events, false otherwise.
     */
    public boolean hasCompletions() {
        if (localHead != localTail) {
            return true;
        }

        localTail = UNSAFE.getIntVolatile(null, tailAddr);
        //System.out.println("hasCompletions count:"+(tail-head));
        return localHead != localTail;
    }

    /**
     * Processes all pending completions.
     *
     * @param completionHandler callback for every completed entry.
     * @return the number of processed completions.
     */
    public int process(IOCompletionHandler completionHandler) {
        // acquire load.
        localTail = UNSAFE.getIntVolatile(null, tailAddr);

        int processed = 0;
        while (localHead < localTail) {
            int index = localHead & ringMask;
            long cqeAddress = cqesAddr + index * CQE_SIZE;

            long userdata = UNSAFE.getLong(null, cqeAddress + OFFSET_CQE_USERDATA);
            int res = UNSAFE.getInt(null, cqeAddress + OFFSET_CQE_RES);
            int flags = UNSAFE.getInt(null, cqeAddress + OFFSET_CQE_FLAGS);

            try {
                completionHandler.handle(res, flags, userdata);
            } catch (Exception e) {
                logger.severe("Failed to process " + completionHandler + " res:" + res + " flags:"
                        + flags + " userdata:" + userdata, e);
            }

            localHead++;
            processed++;
        }

        //System.out.println("Cq::process processed:"+processed);

        // release-store.
        UNSAFE.putOrderedInt(null, headAddr, localHead);
        return processed;
    }

    public int ringMask() {
        return ringMask;
    }

    public IOUring ioUring() {
        return uring;
    }

    public int acquireHead() {
        return UNSAFE.getIntVolatile(null, headAddr);
    }

    public void releaseHead(int newHead) {
        UNSAFE.putOrderedInt(null, headAddr, newHead);
    }

    public int acquireTail() {
        return UNSAFE.getIntVolatile(null, tailAddr);
    }

    native void init(long uring);

    void onClose() {
        headAddr = 0;
        tailAddr = 0;
        cqesAddr = 0;
        ringMask = 0;
    }
}
