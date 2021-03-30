package ksxsdk;

public enum ACTUATOR_TYPE {

    SWITCH(0), 
    RETRACTABLE(1), 
    NUTRIENT(2);

    private final int value;

    ACTUATOR_TYPE(int mtype) {
        this.value = mtype;
    }

    public int getValue() {
        return value;
    }

    public static ACTUATOR_TYPE getEnum(int value) {
        for (ACTUATOR_TYPE e : ACTUATOR_TYPE.values()) {
            if (e.getValue() == value)
                return e;
        }
        return ACTUATOR_TYPE.SWITCH;
    }

}
