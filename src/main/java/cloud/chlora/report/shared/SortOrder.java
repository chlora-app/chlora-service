package cloud.chlora.report.shared;

public enum SortOrder {
    ASC, DESC;

    public static SortOrder fromString(String value) {
        if (value == null || value.isBlank()) return DESC;
        return switch (value.strip().toUpperCase()) {
            case "ASC"  -> ASC;
            case "DESC" -> DESC;
            default     -> throw new IllegalArgumentException(
                    "Invalid sort order: '%s'. Accepted values: asc, desc".formatted(value)
            );
        };
    }

    public String toSql() {
        return this == ASC ? "ASC" : "DESC";
    }
}