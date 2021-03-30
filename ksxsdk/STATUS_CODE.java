package ksxsdk;
public enum STATUS_CODE {
    
     //공통
     READY(0),
     ERROR(1),
     BUSY(2),
     VOLTAGE_ERROR(3),
     CURRENT_ERROR(4),
     TEMPERATURE_ERROR(5),
     FUSE_ERROR(6),
     COMMON_RESERVED(7),

     //센서
     SENSOR_NEED_REPLACE(101),
     SENSOR_NEED_CALIBRATION(102),
     SENSOR_NEED_CHECK(103) ,

     //구동기
     SWITCH_ON(201),
     SWITCH_USER_CONTROL(202),

     REACTABLE_OPENING(301),
     REACTABLE_CLOSING(302),
     REACTABLE_MANUAL_CONTROL(303),
     //양액기

     NUTRIENT_PREPARING(401),
     NUTRIENT_SUPPLYING(402),
     NUTRIENT_STOPPING(403),

     

     //기타
     VENDOR_SPECIFIC_ERROR(900);
    private final int value;
    STATUS_CODE(int mtype)
    {
        this.value = mtype; 
    }
    public int getValue() {
        return value;
    }

    public static STATUS_CODE  getEnum(int value){
        for (STATUS_CODE e:STATUS_CODE.values()) 
        {
            if(e.getValue() == value)
                return e;
        }
        return STATUS_CODE.READY;
    }

}
