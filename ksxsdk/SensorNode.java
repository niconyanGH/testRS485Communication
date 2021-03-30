
package ksxsdk;
public class SensorNode extends KSXNode
{
    public int opid;
    public int status;
    
    public SensorNode(int stationnum, STDModbusRTUMaster mMaster)
    {
        super(stationnum, mMaster);

    }
    
    public boolean readNodeStatus()
    {

        STDModbusResponse rv = mModbusMaster.StandardManualWordRead_F3(StationNumber, mNodeMeta.CommSpec.KS_X_3267_2018.read.starting_register, 2, 1000);
        if (rv != null && rv.rep_function == MDFunction.MODBUS_FC_READ_HOLDING_REGISTERS)
        {

            ByteUtil wrapper = ByteUtil.wrap(rv.byteDatas);
            
            
            this.opid = wrapper.getUShort();
            this.status = wrapper.getUShort();
            return true;

        }

        return false;
    }

    public void readAllsensors()
    {
        for(SensorDev mobj : mSensorDevices)
        {
                readDeviceStatus(mobj);
        }
    }


}
