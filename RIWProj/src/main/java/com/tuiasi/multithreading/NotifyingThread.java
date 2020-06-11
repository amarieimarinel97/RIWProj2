package com.tuiasi.multithreading;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class NotifyingThread extends Thread {
    private final Set<ThreadListener> threadListeners
            = new CopyOnWriteArraySet<>();

    private final void notifyListeners() {
        for (ThreadListener listener : threadListeners) {
            listener.onThreadComplete(this);
        }
    }

    public final void addListener(final ThreadListener listener) {
        threadListeners.add(listener);
    }

    public final void removeListener(final ThreadListener listener) {
        threadListeners.remove(listener);
    }

    @Override
    public final void run() {
        try {
            doRun();
        } finally {
            notifyListeners();
        }
    }

    public abstract void doRun();
}
