package ksxsdk;
public enum NUTRIENT_COMMAND {
    
    OPERATION_NUITRIENT_OFF (0),
    OPERATION_NUITRIENT_ON (401),
    OPERATION_NUITRIENT_AREA_ON (402),
    OPERATION_NUITRIENT_PARAM_ON(403);

    
    private final int value;
    NUTRIENT_COMMAND(int mtype)
    {
        this.value = mtype; 
    }
    public int getValue() {
        return value;
    }

    public static NUTRIENT_COMMAND  getEnum(int value){
        for (NUTRIENT_COMMAND e:NUTRIENT_COMMAND.values()) 
        {
            if(e.getValue() == value)
                return e;
        }
        return NUTRIENT_COMMAND.OPERATION_NUITRIENT_OFF;
    }

}
