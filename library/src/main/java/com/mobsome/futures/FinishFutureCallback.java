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

import android.support.annotation.Nullable;

import com.google.common.util.concurrent.FutureCallback;

/**
 * A callback for accepting the results of a {@link java.util.concurrent.Future}
 * computation asynchronously that has only one callback method which is called once the
 * {@link java.util.concurrent.Future} computation is done (successful or failed)
 * <p>
 * <p>To attach to a {@link com.google.common.util.concurrent.ListenableFuture}
 * use {@link com.google.common.util.concurrent.Futures#addCallback}.
 */
public abstract class FinishFutureCallback<T> implements FutureCallback<T> {

    @Override
    /**
     * {@inheritDoc}
     */
    public final void onSuccess(@Nullable T result) {
        onFinished(true);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public final void onFailure(Throwable t) {
        onFinished(false);
    }

    /**
     * Invoked when the {@code Future} computation is finished, regardless if successful or failed.
     */
    protected abstract void onFinished(boolean success);
}
