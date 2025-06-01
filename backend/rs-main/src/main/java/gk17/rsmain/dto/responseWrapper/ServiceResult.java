package gk17.rsmain.dto.responseWrapper;

public record ServiceResult<T>(String message, T data) {
    public static <T> ServiceResult<T> success(T data) {
        return new ServiceResult<T>("Ok",data);
    }
    public static <T> ServiceResult<T> error (String message) {
        return new ServiceResult<T>(message,null);
    }
    public boolean isSuccess() {
        return "Ok".equals(message);
    }
}
