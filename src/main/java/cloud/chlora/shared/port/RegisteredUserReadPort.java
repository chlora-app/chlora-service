package cloud.chlora.shared.port;

import java.util.List;

public interface RegisteredUserReadPort {

    List<String> findAllActiveUserIds();
}