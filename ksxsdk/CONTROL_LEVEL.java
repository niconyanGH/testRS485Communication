package ksxsdk;
public enum CONTROL_LEVEL {
    
    LV_0(0), 
    LV_1(1), 
    LV_2(2), 
    LV_3(3);

    private final int value;

    CONTROL_LEVEL(int mtype) {
        this.value = mtype;
    }

    public int getValue() {
        return value;
    }

    public static CONTROL_LEVEL getEnum(int value) {
        for (CONTROL_LEVEL e : CONTROL_LEVEL.values()) {
            if (e.getValue() == value)
                return e;
        }
        return CONTROL_LEVEL.LV_0;
    }

    
}
