package gk17.rsmain.dto.responseWrapper;

import java.util.concurrent.CompletableFuture;

public final class AsyncResult {

    private AsyncResult() {
        // private constructor to prevent instantiation
    }

    public static <T> CompletableFuture<ServiceResult<T>> success(T data) {
        return CompletableFuture.completedFuture(ServiceResult.success(data));
    }

    public static <T> CompletableFuture<ServiceResult<T>> error(String message) {
        return CompletableFuture.completedFuture(ServiceResult.error(message));
    }
}

/*
// была попытка заменить тип CompletableFuture<ServiceResult<T>> на AsyncResult<T>
// но spring не разрешил

public class AsyncResult<T> extends CompletableFuture<ServiceResult<T>> {

    public static <T> AsyncResult<T> success(T data) {
        AsyncResult<T> result = new AsyncResult<>();
        result.complete(ServiceResult.success(data));
        return result;
    }

    public static <T> AsyncResult<T> error(String message) {
        AsyncResult<T> result = new AsyncResult<>();
        result.complete(ServiceResult.error(message));
        return result;
    }
}
 */