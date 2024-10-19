package dev.emortal.api.agonessdk;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.agones.sdk.AgonesSDKProto;
import dev.agones.sdk.SDKGrpc;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class AgonesUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgonesUtils.class);
    private static final ThreadFactory THREAD_FACTORY = threadFactory();

    private static final String POD_NAME = System.getenv("HOSTNAME");
    private static StreamObserver<AgonesSDKProto.Empty> HEALTH_STREAM;

    public static void startHealthTask(SDKGrpc.SDKStub stub, long period, TimeUnit unit) {
        waitForSidecar(stub);

        HEALTH_STREAM = stub.health(new EmptyStreamObserver<>());
        Executors.newSingleThreadScheduledExecutor(THREAD_FACTORY)
                .scheduleAtFixedRate(() -> HEALTH_STREAM.onNext(AgonesSDKProto.Empty.getDefaultInstance()), 0, period, unit);
    }

    private static void waitForSidecar(SDKGrpc.SDKStub stub) {
        boolean sidecarAvailable = false;
        int tries = 0;
        while (!sidecarAvailable) {
            if (++tries > 10) throw new RuntimeException("Agones sidecar not ready after 10 tries");

            try {
                stub.health(new EmptyStreamObserver<>());
                sidecarAvailable = true;
            } catch (StatusRuntimeException e) {
                LOGGER.warn("Agones sidecar not ready, retrying in 500ms...");
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted while waiting for Agones sidecar", ie);
                }
            }
        }
    }

    public static void shutdownHealthTask() {
        HEALTH_STREAM.onCompleted();
    }

    private static ThreadFactory threadFactory() {
        return new ThreadFactoryBuilder()
                .setNameFormat("agones-sdk-%d")
                .setUncaughtExceptionHandler((t, e) -> LOGGER.error("Uncaught exception in thread {}", t.getName(), e))
                .build();
    }

    public static String getPodName() {
        return POD_NAME;
    }
}
