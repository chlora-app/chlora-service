package cloud.chlora.shared.port;

public interface DeviceRegistrationPort {

    boolean isDeviceRegistered(String deviceId);

    void registerDevice(String deviceId);

    void setDeviceOnline(String deviceId);
}
