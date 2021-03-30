
package ksxsdk;
public class ActuatorNode extends KSXNode
{

    
    public int opid;
    public int status;

    
    public ActuatorNode(int stationnum, STDModbusRTUMaster mMaster)
    {
        super(stationnum, mMaster);

    }
    
    public boolean readNodeStatus()
    {

        STDModbusResponse rv = mModbusMaster.StandardManualWordRead_F3(StationNumber, mNodeMeta.CommSpec.KS_X_3267_2018.read.starting_register, 2, 1000);

        if (rv != null && rv.rep_function == MDFunction.MODBUS_FC_READ_HOLDING_REGISTERS)
        {
            ByteUtil wrapper = ByteUtil.wrap(rv.byteDatas);
            this.opid = wrapper.getShort();
            this.status =wrapper.getShort();
            return true;
        }
        return false;
    }


    public void readAllactuatorstatus()
    {
        for (Object mobj : mDevices)
        {
            if (mobj.getClass() == ActuatorDev.class)
            {
                readDeviceStatus((ActuatorDev)mobj);

            }

        }

    }

               



}
