package ksxsdk;

public enum NODECONTROL_COMMAND {

    OPERATION_LOCAL(1), OPERATION_REMOTE(2), OPERATION_MANUAL(3);

    private final int value;

    NODECONTROL_COMMAND(int mtype) {
        this.value = mtype;
    }

    public int getValue() {
        return value;
    }

    public static NODECONTROL_COMMAND getEnum(int value) {
        for (NODECONTROL_COMMAND e : NODECONTROL_COMMAND.values()) {
            if (e.getValue() == value)
                return e;
        }
        return NODECONTROL_COMMAND.OPERATION_LOCAL;
    }

}
