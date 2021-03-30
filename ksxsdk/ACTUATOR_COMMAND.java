package ksxsdk;
public enum ACTUATOR_COMMAND {
    
    OPERATION_OFF_STOP (0),
    OPERATION_SWITCH_ON (201),
    OPERATION_SWITCH_TIMED_ON (202),
    OPERATION_SWITCH_DIREATIONAL_ON(203),

    OPERATION_RETRACTABLE_OPEN (301),
    OPERATION_RETRACTABLE_CLOSE (302),
    OPERATION_RETRACTABLE_TIMED_OPEN (303),
    OPERATION_RETRACTABLE_TIMED_CLOSE (304),
    OPERATION_RETRACTABLE_SET_POSITION (305),
    OPERATION_RETRACTABLE_SET_CONFIG (306);
    private final int value;
    ACTUATOR_COMMAND(int mtype)
    {
        this.value = mtype; 
    }
    public int getValue() {
        return value;
    }

    public static ACTUATOR_COMMAND  getEnum(int value){
        for (ACTUATOR_COMMAND e:ACTUATOR_COMMAND.values()) 
        {
            if(e.getValue() == value)
                return e;
        }
        return ACTUATOR_COMMAND.OPERATION_OFF_STOP;
    }

}
