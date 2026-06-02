package cloud.chlora.management.device.domain.port;

public interface PotExistencePort {

    boolean existsByPotId(String potId);
}