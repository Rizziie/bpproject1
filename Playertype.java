public enum PlayerType {
    CROSS, NOUGHT, NO_SEED;

    @Override
    public String toString() {
        return switch (this) {
            case CROSS -> "X";
            case NOUGHT -> "O";
            case NO_SEED -> " ";
        };
    }
}
