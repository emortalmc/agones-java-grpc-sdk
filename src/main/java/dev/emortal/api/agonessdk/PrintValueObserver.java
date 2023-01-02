package dev.emortal.api.agonessdk;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrintValueObserver<T> implements StreamObserver<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(IgnoredStreamObserver.class);
    private final String formatMessage;

    public PrintValueObserver(String formatMessage) {
        this.formatMessage = formatMessage;
    }

    @Override
    public void onNext(T value) {
        LOGGER.info(this.formatMessage.formatted(value));
    }

    @Override
    public void onError(Throwable t) {
        LOGGER.error("Error within observer: ", t);
    }

    @Override
    public void onCompleted() {

    }
}
