package cloud.chlora.management.shared.error;

import org.springframework.http.HttpStatus;

public interface AppErrorCode {
    String code();
    String message();
    HttpStatus status();
    String domain();
}