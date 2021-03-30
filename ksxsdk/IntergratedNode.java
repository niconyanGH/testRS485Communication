
package ksxsdk;
public class IntergratedNode extends KSXNode
{
    
    public int status;
    public int opid;
    public int control;

    public IntergratedNode(int stationnum, STDModbusRTUMaster mMaster)
    {
        super(stationnum, mMaster);
    }
    
    public boolean readNodeStatus()
    {
        //양액기노드 경우 "control" 항목이 있는지 확인
        int sizecontrol = KSX326xMetadata.getsizeWithItemcheck(mNodeMeta.CommSpec.KS_X_3267_2018.read.items, KSX326xMetadata.ELEMENT_control,1);
        STDModbusResponse rv = mModbusMaster.StandardManualWordRead_F3(StationNumber, mNodeMeta.CommSpec.KS_X_3267_2018.read.starting_register, 2+sizecontrol, 1000);

        if (rv != null && rv.rep_function == MDFunction.MODBUS_FC_READ_HOLDING_REGISTERS)
        {
            
            ByteUtil wrapper = ByteUtil.wrap(rv.byteDatas);
                this.status =wrapper.getUShort();
                this.opid = wrapper.getUShort();
                if (sizecontrol > 0)
                {
                    this.control = wrapper.getUShort();
                }
                else
                {
                    this.control = (int)0xFFFF;//지원하지않음상태 표시
                }
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
