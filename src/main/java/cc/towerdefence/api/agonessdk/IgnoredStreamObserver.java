package cc.towerdefence.api.agonessdk;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class IgnoredStreamObserver<T> implements StreamObserver<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(IgnoredStreamObserver.class);

    @Override
    public void onNext(T value) {
    }

    @Override
    public void onError(Throwable t) {
        LOGGER.error("Error within observer: ", t);
    }

    @Override
    public void onCompleted() {

    }
}
