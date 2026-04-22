package cloud.chlora.management.device.domain.port;

import java.util.Map;
import java.util.Set;

public interface PotNamePort {

    String getPotName(String potId);

    Map<String, String> getPotNames(Set<String> potIds);
}