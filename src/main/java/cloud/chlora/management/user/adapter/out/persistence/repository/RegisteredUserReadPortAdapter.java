package cloud.chlora.management.user.adapter.out.persistence.repository;

import cloud.chlora.shared.port.RegisteredUserReadPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RegisteredUserReadPortAdapter implements RegisteredUserReadPort {

    private final UserReadJpaRepository userReadJpaRepository;

    @Override
    public List<String> findAllActiveUserIds() {
        return userReadJpaRepository.findAllActiveUserIds();
    }
}