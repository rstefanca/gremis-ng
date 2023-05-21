package cz.upce.gremis.gremisng.common;

public enum GremisRole {
    ROOT, ADMIN, PRIVILEGED, MEMBER, GUEST;

    public static GremisRole[] available() {
        return new GremisRole[] {
                ADMIN, PRIVILEGED, MEMBER, GUEST
        };
    }
}
