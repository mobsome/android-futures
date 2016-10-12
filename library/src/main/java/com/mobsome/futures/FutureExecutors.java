/*
 * Copyright (C) 2016 Mobsome
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mobsome.futures;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.google.common.util.concurrent.ListeningExecutorService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Factory and utility methods for {@link java.util.concurrent.Executor}, {@link
 * ExecutorService}
 */
public class FutureExecutors {
    private static final ListeningExecutorService ioExecutor =
            com.google.common.util.concurrent.MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    private static final ScheduledExecutor mainThreadExecutor = new ScheduledExecutor() {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            this.handler.post(command);
        }

        @Override
        public void execute(@NonNull Runnable r, long delay, @NonNull TimeUnit unit) {
            this.handler.postDelayed(r, unit.toMillis(delay));
        }

        @Override
        public void cancel(@NonNull Runnable r) {
            this.handler.removeCallbacks(r);
        }
    };

    /**
     * Executor that runs commands on main (UI) thread
     *
     * @return Android UI thread executor
     */
    public static ScheduledExecutor mainThreadExecutor() {
        return mainThreadExecutor;
    }

    /**
     * Executor for common I/O operations
     *
     * @return I/O executor
     */
    public static ListeningExecutorService ioExecutor() {
        return ioExecutor;
    }
}
