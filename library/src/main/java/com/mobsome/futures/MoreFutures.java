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
import android.support.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;

/**
 * {@link com.google.common.util.concurrent.Futures} class extension
 */
public class MoreFutures {

    /**
     * Checks whether provided future is pending (not yet done or cancelled)
     *
     * @param future future to be checked
     * @return if provided future is still pending
     */
    public static boolean isPending(@Nullable Future<?> future) {
        return future != null && !(future.isDone() || future.isCancelled());
    }

    /**
     * Creates an identity function
     *
     * @param value will be returned by the resulting function
     * @param <T>   input type of the resulting identity function
     * @param <U>   output type of the resulting identity function
     * @return an instance of {@link Function} that just returns a value
     * passed as a parameter
     */
    public static <T, U> Function<T, U> identity(final U value) {
        return new Function<T, U>() {
            @Override
            public U apply(T input) {
                return value;
            }
        };
    }

    /**
     * Transforms future with an fallback in case of error
     *
     * @param future   future to be transformed
     * @param function transforming function
     * @param fallback fallback to be used in case of error
     * @param <T>      input type of the future
     * @param <U>      output type of the resulting function
     * @return transformed future
     */
    public static <T, U> ListenableFuture<U> transformAsyncWithFallback(
            @NonNull ListenableFuture<T> future,
            @NonNull AsyncFunction<T, U> function,
            @NonNull AsyncFunction<Throwable, U> fallback) {
        Preconditions.checkNotNull(future, "future must not be null");
        Preconditions.checkNotNull(function, "function must not be null");
        Preconditions.checkNotNull(fallback, "fallback must not be null");

        return transformAsyncWithFallback(future, function, fallback,
                MoreExecutors.directExecutor());
    }

    /**
     * Transforms future with an fallback in case of error
     *
     * @param future   future to be transformed
     * @param function transforming function
     * @param fallback fallback to be used in case of error
     * @param executor executor for this future
     * @param <T>      input type of the future
     * @param <U>      output type of the resulting function
     * @return transformed future
     */
    public static <T, U> ListenableFuture<U> transformAsyncWithFallback(
            @NonNull ListenableFuture<T> future,
            @NonNull AsyncFunction<T, U> function,
            @NonNull AsyncFunction<Throwable, U> fallback,
            @NonNull Executor executor) {
        Preconditions.checkNotNull(future, "future must not be null");
        Preconditions.checkNotNull(function, "function must not be null");
        Preconditions.checkNotNull(fallback, "fallback must not be null");
        Preconditions.checkNotNull(executor, "executor must not be null");

        return Futures.catchingAsync(Futures.transformAsync(future, function), Throwable.class,
                fallback, executor);
    }

    /**
     * Transforms future with an fallback in case of error
     *
     * @param future   future to be transformed
     * @param function transforming function
     * @param fallback fallback to be used in case of error
     * @param <T>      input type of the future
     * @param <U>      output type of the resulting function
     * @return transformed future
     */
    public static <T, U> ListenableFuture<U> transformWithFallback(
            @NonNull ListenableFuture<T> future,
            @NonNull Function<T, U> function,
            @NonNull Function<Throwable, U> fallback) {
        Preconditions.checkNotNull(future, "future must not be null");
        Preconditions.checkNotNull(function, "function must not be null");
        Preconditions.checkNotNull(fallback, "fallback must not be null");

        return transformWithFallback(future, function, fallback, MoreExecutors.directExecutor());
    }

    /**
     * Transforms future with an fallback in case of error
     *
     * @param future   future to be transformed
     * @param function transforming function
     * @param fallback fallback to be used in case of error
     * @param executor executor for this future
     * @param <T>      input type of the future
     * @param <U>      output type of the resulting function
     * @return transformed future
     */
    public static <T, U> ListenableFuture<U> transformWithFallback(
            @NonNull ListenableFuture<T> future,
            @NonNull Function<T, U> function,
            @NonNull Function<Throwable, U> fallback,
            @NonNull Executor executor) {
        Preconditions.checkNotNull(future, "future must not be null");
        Preconditions.checkNotNull(function, "function must not be null");
        Preconditions.checkNotNull(fallback, "fallback must not be null");
        Preconditions.checkNotNull(executor, "executor must not be null");

        return Futures.catching(Futures.transform(future, function), Throwable.class, fallback,
                executor);
    }

    /**
     * Transforms future regardless whether error occurs or not
     *
     * @param future   future to be transformed
     * @param function transforming function
     * @param <T>      input type of the future
     * @param <U>      output type of the resulting function
     * @return transformed future
     */
    public static <T, U> ListenableFuture<U> ignoreFailure(
            @NonNull ListenableFuture<T> future, @NonNull final Function<T, U> function) {
        Preconditions.checkNotNull(future, "future must not be null");
        Preconditions.checkNotNull(function, "function must not be null");

        return ignoreFailure(future, function, MoreExecutors.directExecutor());
    }

    /**
     * Transforms future regardless whether error occurs or not
     *
     * @param future   future to be transformed
     * @param function transforming function
     * @param executor executor for this future
     * @param <T>      input type of the future
     * @param <U>      output type of the resulting function
     * @return transformed future
     */
    public static <T, U> ListenableFuture<U> ignoreFailure(
            @NonNull ListenableFuture<T> future,
            @NonNull final Function<T, U> function,
            @NonNull Executor executor) {
        Preconditions.checkNotNull(future, "future must not be null");
        Preconditions.checkNotNull(function, "function must not be null");
        Preconditions.checkNotNull(executor, "executor must not be null");

        return Futures.catching(Futures.transform(future, function), Throwable.class,
                new Function<Throwable, U>() {
                    @Override
                    public U apply(Throwable input) {
                        return function.apply(null);
                    }
                }, executor);
    }

    /**
     * Ignores the failure of provided future
     *
     * @param future future to be transformed
     * @param <T>    input type of the future
     * @return transformed future
     */
    public static <T> ListenableFuture<Void> ignoreFailure(@NonNull ListenableFuture<T> future) {
        Preconditions.checkNotNull(future, "future must not be null");
        return ignoreFailure(future, MoreFutures.<T, Void>identity(null));
    }

    /**
     * Ignores the return value of provided future
     *
     * @param future future to be transformed
     * @param <T>    input type of the future
     * @return transformed future
     */
    public static <T> ListenableFuture<Void> ignoreValue(@NonNull ListenableFuture<T> future) {
        Preconditions.checkNotNull(future, "future must not be null");
        return Futures.transform(future, MoreFutures.<T, Void>identity(null));
    }

    /**
     * Forwards result of a {@link java.util.concurrent.Future} to a different future
     *
     * @param deliverer {@link java.util.concurrent.Future} that delivers result
     * @param receiver  {@link java.util.concurrent.Future} that receives result
     */
    public static <U> void forwardResult(final ListenableFuture<U> deliverer, final SettableFuture<U> receiver) {
        Futures.addCallback(deliverer, new FutureCallback<U>() {
            @Override
            public void onSuccess(@Nullable U result) {
                receiver.set(result);
            }

            @Override
            public void onFailure(Throwable t) {
                receiver.setException(t);
            }
        });
    }
}