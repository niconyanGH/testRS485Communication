package ksxsdk;
public class MDFunction {

    public static final int  MODBUS_FC_READ_COILS  =  0x01;

    public static final int MODBUS_FC_READ_DISCRETE_INPUTS     = 0x02;
    public static final int MODBUS_FC_READ_HOLDING_REGISTERS   = 0x03;     //읽기용
    public static final int MODBUS_FC_READ_INPUT_REGISTERS     = 0x04;
    public static final int MODBUS_FC_WRITE_SINGLE_COIL        = 0x05;
    public static final int MODBUS_FC_WRITE_SINGLE_REGISTER    = 0x06;  // 제어용(write)
    public static final int MODBUS_FC_READ_EXCEPTION_STATUS    = 0x07;
    public static final int MODBUS_FC_WRITE_MULTIPLE_COILS     = 0x0F;
    public static final int MODBUS_FC_WRITE_MULTIPLE_REGISTERS = 0x10;  // 제어용(write)
    public static final int MODBUS_FC_REPORT_SLAVE_ID          = 0x11;
    public static final int MODBUS_FC_MASK_WRITE_REGISTER      = 0x16;
    public static final int MODBUS_FC_WRITE_AND_READ_REGISTERS = 0x17;
}
