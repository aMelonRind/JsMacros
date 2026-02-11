package xyz.wagyourtail.jsmacros.client;

import net.minecraft.client.MinecraftClient;

import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

public class McUtil {
    public static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void runOnMain(Runnable runnable) {
        runOnMain(true, runnable);
    }

    public static void runOnMain(boolean await, Runnable runnable) {
        if (JsMacrosClient.clientCore.profile.checkJoinedThreadStack()) {
            runnable.run();
            return;
        }

        final Throwable[] throwable = {null};
        final Semaphore semaphore = new Semaphore(await ? 0 : 1);
        mc.execute(() -> {
            try {
                runnable.run();
            } catch (Throwable t) {
                if (!semaphore.hasQueuedThreads()) {
                    JsMacrosClient.clientCore.profile.logError(t);
                } else {
                    throwable[0] = t;
                }
            }
            semaphore.release();
        });
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (throwable[0] != null) {
            throw new RuntimeException(throwable[0]);
        }
    }

    public static <T> T fetchOnMain(Supplier<T> runnable) {
        if (JsMacrosClient.clientCore.profile.checkJoinedThreadStack()) {
            return runnable.get();
        }

        final Object[] temp = {null};
        final Throwable[] throwable = {null};
        final Semaphore semaphore = new Semaphore(0);
        mc.execute(() -> {
            try {
                temp[0] = runnable.get();
            } catch (Throwable t) {
                if (!semaphore.hasQueuedThreads()) {
                    JsMacrosClient.clientCore.profile.logError(t);
                } else {
                    throwable[0] = t;
                }
            }
            semaphore.release();
        });
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (throwable[0] != null) {
            throw new RuntimeException(throwable[0]);
        }
        //noinspection unchecked
        return (T) temp[0];
    }
}
