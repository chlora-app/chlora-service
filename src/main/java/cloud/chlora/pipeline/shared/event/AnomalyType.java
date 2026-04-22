package cloud.chlora.pipeline.shared.event;

public enum AnomalyType {
    SOIL_MOISTURE_OUT_OF_RANGE,
    TEMPERATURE_OUT_OF_RANGE,
    HUMIDITY_OUT_OF_RANGE,
    BATTERY_LOW,
    TIMESTAMP_DRIFT,
    SENSOR_UNRESPONSIVE
}
