package cloud.chlora.pipeline.notification.application.usecase;

import cloud.chlora.pipeline.notification.domain.port.UserNotificationStatusWriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarkNotificationReadUseCase {

    private final UserNotificationStatusWriteRepository statusWriteRepository;

    @Transactional
    public void execute(String notificationId, String userId) {
        statusWriteRepository.markAsRead(notificationId, userId);
    }
}