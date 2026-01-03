package mg.waterfall.core.util.log.level;

public enum Level {
    INFO("Info"),
    WARNING("Warning"),
    SEVERE("Severe");

    private final String label;

    Level(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
