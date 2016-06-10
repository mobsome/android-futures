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

import android.support.annotation.NonNull;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Binds {@link FutureCallback} with {@link ListenableFuture} with pausing/resuming functionality
 */
public class FutureCallbackBinder<T> {
    private final ListenableFuture<T> future;
    private final FutureCallback<T> callback;
    private Optional<FutureCallback<T>> pendingCallback = Optional.absent();
    private final AtomicBoolean resumed = new AtomicBoolean(false);
    private final Executor executor;

    private FutureCallbackBinder(Builder<T> builder) {
        this.future = builder.future;
        this.callback = builder.callback;
        this.executor = builder.executor;
        this.resumed.set(builder.resumed);

        pendingCallback = Optional.of(callback);
        addCallback();
    }

    /**
     * Re-triggers future callback execution in case future is already done
     */
    public synchronized void triggerCallback() {
        if (future.isDone()) {
            pendingCallback = Optional.of(callback);
            addCallback();
        }
    }

    /**
     * Resumes previously paused future callback execution
     * Not effect if already resumed
     */
    public synchronized void resume() {
        if (resumed.getAndSet(true)) {
            // already resumed
            return;
        }

        if (future.isDone()) {
            addCallback();
        }
    }

    /**
     * Pauses future callback execution
     */
    public synchronized void pause() {
        resumed.set(false);
    }

    /**
     * Cancels underlying future execution if not done yet
     *
     * @param triggerCallback whether future callback should be executed
     *                        with {@link java.util.concurrent.CancellationException}
     */
    public synchronized void cancel(boolean triggerCallback) {
        if (!triggerCallback) {
            pendingCallback = Optional.absent();
        }
        future.cancel(true);
    }

    /**
     * Returns whether underlying future is already done
     *
     * @return whether underlying future is already done
     */
    public boolean isDone() {
        return future.isDone();
    }

    /**
     * Returns whether underlying future is canceled
     *
     * @return whether underlying future is canceled
     */
    public boolean isCancelled() {
        return future.isCancelled();
    }

    /**
     * Builder pattern for {@link FutureCallbackBinder}
     */
    public static class Builder<T> {
        private final ListenableFuture<T> future;
        private final FutureCallback<T> callback;
        private boolean resumed = true;
        private Executor executor = MoreExecutors.directExecutor();

        /**
         * @param future   future to which this binder should be bound
         * @param callback callback to be bound to future
         */
        public Builder(@NonNull ListenableFuture<T> future, @NonNull FutureCallback<T> callback) {
            Preconditions.checkNotNull(future, "future must not be null");
            Preconditions.checkNotNull(callback, "callback must not be null");

            this.future = future;
            this.callback = callback;
        }

        /**
         * {@link Executor} on which callback should be executed. By default callback is called on future thread
         *
         * @param executor callback executor
         * @return this builder
         */
        public Builder<T> executor(@NonNull Executor executor) {
            Preconditions.checkNotNull(executor, "executor must not be null");
            this.executor = executor;
            return this;
        }

        /**
         * Binds callback to underlying future without resuming the binder. Callback will not be called until resume
         * is called manually
         *
         * @return {@link FutureCallbackBinder} future binder
         */
        public FutureCallbackBinder<T> bind() {
            this.resumed = false;
            return new FutureCallbackBinder<>(this);
        }

        /**
         * Binds callback to underlying future and resuming the binder. Callback will be called as soon
         * as the underlying future is done/canceled
         *
         * @return {@link FutureCallbackBinder} future binder
         */
        public FutureCallbackBinder<T> bindAndResume() {
            this.resumed = true;
            return new FutureCallbackBinder<>(this);
        }
    }

    private void addCallback() {
        Futures.addCallback(future, internalCallback, executor);
    }

    private synchronized void onSyncSuccess(T result) {
        if (pendingCallback.isPresent() && resumed.get()) {
            pendingCallback.get().onSuccess(result);
            pendingCallback = Optional.absent();
        }
    }

    private synchronized void onSyncError(Throwable error) {
        if (pendingCallback.isPresent() && resumed.get()) {
            pendingCallback.get().onFailure(error);
            pendingCallback = Optional.absent();
        }
    }

    private final FutureCallback<T> internalCallback = new FutureCallback<T>() {
        @Override
        public void onSuccess(T result) {
            onSyncSuccess(result);
        }

        @Override
        public void onFailure(Throwable t) {
            onSyncError(t);
        }
    };
}