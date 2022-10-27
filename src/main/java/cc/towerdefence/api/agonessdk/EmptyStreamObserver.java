package cc.towerdefence.api.agonessdk;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class EmptyStreamObserver<T> implements StreamObserver<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmptyStreamObserver.class);

    @Override
    public void onNext(T value) {
        LOGGER.warn("Received a response from Agones, but it should not have sent one");
    }

    @Override
    public void onError(Throwable t) {
        LOGGER.error("Error within observer: ", t);
    }

    @Override
    public void onCompleted() {

    }
}
