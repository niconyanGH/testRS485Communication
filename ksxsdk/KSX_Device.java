package ksxsdk;

public class KSX_Device {

    public String Class;
    public String Type;
    public String Model;
    public String Name;
    public String ValueUnit;
    public String ValueType;
    public int SignificantDigit;
    public int Channel;

    public KSX_CommSpec CommSpec;

    public ACTUATOR_TYPE getActuatorType() {

        ACTUATOR_TYPE mtype = ACTUATOR_TYPE.SWITCH;
        if (Type.contains(KSX326xMetadata.ELEMENT_typeswitch) == true) {
            mtype = ACTUATOR_TYPE.SWITCH;
        }
        if (Type.contains(KSX326xMetadata.ELEMENT_typeretractable) == true) {
            mtype = ACTUATOR_TYPE.RETRACTABLE;
        }
        if (Type.contains(KSX326xMetadata.ELEMENT_typenutrient) == true) {
            mtype = ACTUATOR_TYPE.NUTRIENT;
        }
        return mtype;
    }

    public CONTROL_LEVEL getControllevel() {

        CONTROL_LEVEL mlevel =  CONTROL_LEVEL.LV_0;

        if (Type.contains(KSX326xMetadata.ELEMENT_level1) == true) {
            mlevel = CONTROL_LEVEL.LV_1;
        }
        if (Type.contains(KSX326xMetadata.ELEMENT_level2) == true) {
            mlevel = CONTROL_LEVEL.LV_2;
        }
        if (Type.contains(KSX326xMetadata.ELEMENT_level3) == true) {
            mlevel = CONTROL_LEVEL.LV_3;
        }
        return mlevel;
    }

}
