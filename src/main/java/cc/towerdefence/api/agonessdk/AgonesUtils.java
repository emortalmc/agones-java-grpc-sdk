package cc.towerdefence.api.agonessdk;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.agones.allocation.AllocationProto;
import dev.agones.allocation.AllocationServiceGrpc;
import dev.agones.sdk.AgonesSDKProto;
import dev.agones.sdk.SDKGrpc;
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

    public static void startHealthTask(SDKGrpc.SDKStub stub, long period, TimeUnit unit) {
        StreamObserver<AgonesSDKProto.Empty> healthStream = stub.health(new EmptyStreamObserver<>());
        Executors.newSingleThreadScheduledExecutor(THREAD_FACTORY)
                .scheduleAtFixedRate(() -> healthStream.onNext(AgonesSDKProto.Empty.getDefaultInstance()), 0, period, unit);
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
