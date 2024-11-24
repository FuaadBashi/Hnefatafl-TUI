package ws.aperture.hnefatafl.model.enums;

public enum Side {
    ATTACKING,
    DEFENDING;

    public static Side otherSide(Side side) {
        return ( side == ATTACKING ) ? DEFENDING : ATTACKING;
    }

    @Override
    public String toString() {
        return this.name();
    }
}