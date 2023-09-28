package cz.upce.gremis.gremisng.ui;

public interface AsyncDataCallback<T> {

    void operationFinished(T data);

    void operationFailed(Throwable error);

}
