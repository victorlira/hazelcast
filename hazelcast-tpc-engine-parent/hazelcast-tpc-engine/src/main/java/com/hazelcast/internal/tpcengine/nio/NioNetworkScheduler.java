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

package com.hazelcast.internal.tpcengine.nio;

import com.hazelcast.internal.tpcengine.net.NetworkScheduler;
import org.jctools.queues.MpscArrayQueue;

import java.util.Queue;

/**
 * The {@link NetworkScheduler} specific to the {@link NioReactor}.
 */
public class NioNetworkScheduler implements NetworkScheduler<NioAsyncSocket> {

    private final Queue<NioAsyncSocket> dirtyQueue;

    public NioNetworkScheduler(int maxSockets) {
        this.dirtyQueue = new MpscArrayQueue<>(maxSockets);
    }

    @Override
    public boolean schedule(NioAsyncSocket socket) {
        return dirtyQueue.offer(socket);
    }

    public void tick() {
        for (; ; ) {
            NioAsyncSocket socket = dirtyQueue.poll();
            if (socket == null) {
                break;
            }

            socket.handler.run();
        }
    }

    @Override
    public boolean isDirty() {
        return !dirtyQueue.isEmpty();
    }
}
