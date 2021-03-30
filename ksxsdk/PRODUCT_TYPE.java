package ksxsdk;
public enum PRODUCT_TYPE {
    
    NONE(0),
    SENSORNODE(1), 
    ACTUATORNODE(2),
    INTEGRATEDNODE(3);
    
    private final int value;
    PRODUCT_TYPE(int mtype)
    {
        this.value = mtype; 
    }
    public int getValue() {
        return value;
    }
    public static PRODUCT_TYPE  getEnum(int value){
        for (PRODUCT_TYPE e:PRODUCT_TYPE.values()) 
        {
            if(e.getValue() == value)
                return e;
        }
        return PRODUCT_TYPE.NONE;
    }

}
