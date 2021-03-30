package ksxsdk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KSXNode {

    public static final int OPERATION_NODE_CONTROL = 2;

    public final int RESPONSE_TIME_MSEC = 300;
    public final int REGISTER_DEVICECODE_READ_START_ADDRESS = 101;
    public final int REGISTER_DEVICECODE_READ_WORD_LENGTH = 200;

    public int StationNumber = 0;
    public final STDModbusRTUMaster mModbusMaster;
    public NodeInfoByModbus mNodeInfo;

    public List<Integer> mDeviceCodes;

    public KSX_NodeMeta mNodeMeta;

    public List<Object> mDevices;

    public List<SensorDev> mSensorDevices;
    public List<ActuatorDev> mActuatorDevices;
    public List<NutrientDev> mNutrientDevices;

    public KSXNode(int stationnum, STDModbusRTUMaster mMaster) {
        StationNumber = stationnum;
        mModbusMaster = mMaster;

        mNodeInfo = new NodeInfoByModbus();
        mDeviceCodes = new ArrayList<>();

        mDevices = new ArrayList<>();
        mSensorDevices = new ArrayList<>();
        mActuatorDevices = new ArrayList<>();
        mNutrientDevices = new ArrayList<>();

    }

    public boolean ReadNodeInformation() {

        STDModbusResponse rv = mModbusMaster.StandardManualWordRead_F3(StationNumber,
                NodeInfoByModbus.REGISTER_START_ADDRESS, NodeInfoByModbus.REGISTER_WORD_LENGTH, 1000);

        if (rv != null && rv.rep_function == MDFunction.MODBUS_FC_READ_HOLDING_REGISTERS) {
            if (mNodeInfo.deSerialize(rv.byteDatas) == true) {
                mNodeMeta = KSX326xMetadata.GetNodeMetaData(mNodeInfo);
                return true;
            }
        }

        return false;

    }

    public boolean readDeviceStatus(SensorDev mdevice) {

        STDModbusResponse rv1 = mModbusMaster.StandardManualWordRead_F3(StationNumber,
                mdevice.REGISTER_READ_START_ADDRESS, mdevice.REGISTER_READ_WORD_LENGTH, RESPONSE_TIME_MSEC);
        if (rv1 != null) {
            mdevice.deSerialize(rv1.byteDatas);
            return true;
        }
        return false;
    }

    public boolean readDeviceStatus(ActuatorDev mdevice) {

        STDModbusResponse rv1 = mModbusMaster.StandardManualWordRead_F3(StationNumber,
                mdevice.REGISTER_READ_START_ADDRESS, mdevice.REGISTER_READ_WORD_LENGTH, RESPONSE_TIME_MSEC);
        if (rv1 != null) {
            mdevice.deSerialize(rv1.byteDatas);
            return true;
        }
        return false;
    }

    public boolean readDeviceStatus(NutrientDev mdevice) {

        STDModbusResponse rv1 = mModbusMaster.StandardManualWordRead_F3(StationNumber,
                mdevice.REGISTER_READ_START_ADDRESS, mdevice.REGISTER_READ_WORD_LENGTH, RESPONSE_TIME_MSEC);
        if (rv1 != null) {
            mdevice.deSerialize(rv1.byteDatas);
            return true;
        }
        return false;
    }

    public boolean devicecodeDeSerialize(byte[] buffer) {
        ByteUtil wrapper = ByteUtil.wrap(buffer);

        mDeviceCodes.clear();

        for (int i = 0; i < buffer.length / 2; i++) {
            int md = (int) wrapper.getUShort();

            mDeviceCodes.add(md);
        }

        return true;
    }



    public boolean ReadDeviceList() {

        if (mNodeMeta == null) {
            return false;
        }

        int devicecount = mNodeMeta.Devices.size();

        STDModbusResponse rv1 = mModbusMaster.StandardManualWordRead_F3(StationNumber,
                REGISTER_DEVICECODE_READ_START_ADDRESS, devicecount, 1000);

        if (rv1 != null) {
            return devicecodeDeSerialize(rv1.byteDatas);

        }
        return false;
    }

    public boolean createDevice() {

        mDevices.clear();

        mSensorDevices.clear();
        mActuatorDevices.clear();
        mNutrientDevices.clear();

        for (int i = 0; i < mDeviceCodes.size(); i++) {

            if (mDeviceCodes.get(i) != 0 && i < mNodeMeta.Devices.size()) {
                KSX_Device mDevice = mNodeMeta.Devices.get(i);

                if (mDevice != null) {
                    Object mdev = null;

                    if (mDevice.Class.contains(KSX326xMetadata.ELEMENT_classsensor) == true) {
                        mdev = new SensorDev(mDevice);
                        mSensorDevices.add((SensorDev) mdev);

                    } else if (mDevice.Class.contains(KSX326xMetadata.ELEMENT_classactuator) == true) {
                        // 구동기 분류에서 양액기인지 구동기인지 구별
                        if (mDevice.Type.contains(KSX326xMetadata.ELEMENT_typenutrient) == true) {

                            mdev = new NutrientDev(mDevice);
                            mNutrientDevices.add((NutrientDev) mdev);

                        } else {
                            mdev = new ActuatorDev(mDevice);
                            mActuatorDevices.add((ActuatorDev) mdev);
                        }

                    }
                    if (mdev != null) {
                        mDevices.add(mdev);
                    }
                }

            }

        }

        return true;

    }

    public boolean controlReactable(ActuatorDev mactdevice, ACTUATOR_COMMAND mOperation, int mOpid, long mHoldtime,
            int mPosition, int mOpentime, int mClosetime) {

        byte[] mbuffer;

        mbuffer = mactdevice.bulid_control_msg(mOperation, mOpid, mHoldtime, (short) 0, mPosition, mOpentime,
                mClosetime);

        if (mbuffer != null) {

            STDModbusResponse rv1 = mModbusMaster.StandardManualWordWrite_F10(StationNumber,
                    mactdevice.REGISTER_WRITE_START_ADDRESS, mbuffer, RESPONSE_TIME_MSEC);

            if (rv1 != null) {
                return true;
            }

        }

        return false;
    }

    public boolean controlSwitch(ActuatorDev mactdevice, ACTUATOR_COMMAND mOperation, int mOpid, long mHoldtime,
            short mRatio) {
        boolean ret = false;

        byte[] mbuffer = mactdevice.bulid_control_msg(mOperation, mOpid, mHoldtime, mRatio, 0, 0, 0);

        if (mbuffer != null) {

            STDModbusResponse rv1 = mModbusMaster.StandardManualWordWrite_F10(StationNumber,
                    mactdevice.REGISTER_WRITE_START_ADDRESS, mbuffer, RESPONSE_TIME_MSEC);

            if (rv1 != null) {

                ret = true;
            } else {

            }

        } else {
            System.out.println(" 지원하지 않는 명령어 입니다.  mOperation code :  " + mOperation);
        }

        return ret;
    }

    public boolean controlNutrient(NutrientDev mNut, NUTRIENT_COMMAND mOperation, int mOpid, int startarea, int endarea,
            int osec, float ec, float ph) {

        byte[] mbuffer = mNut.bulid_control_msg(mOperation, mOpid, startarea, endarea, osec, ec, ph);
        STDModbusResponse rv1 = mModbusMaster.StandardManualWordWrite_F10(StationNumber,
                mNut.REGISTER_WRITE_START_ADDRESS, mbuffer, RESPONSE_TIME_MSEC);
        if (rv1 != null) {
            return true;
        }

        return false;
    }

    public boolean controlNode(int mOpid, NODECONTROL_COMMAND control) {
        byte[] buffer = null;
        int mindex = 0;

        buffer = new byte[6];

        mindex += ByteUtil.cast_value_to_bytes_insert_buffer((double) OPERATION_NODE_CONTROL, buffer, mindex);
        mindex += ByteUtil.cast_value_to_bytes_insert_buffer((double) mOpid, buffer, mindex);
        mindex += ByteUtil.cast_value_to_bytes_insert_buffer((double) control.getValue(), buffer, mindex);

        STDModbusResponse rv1 = mModbusMaster.StandardManualWordWrite_F10(StationNumber,
                mNodeMeta.CommSpec.KS_X_3267_2018.write.starting_register, buffer, RESPONSE_TIME_MSEC);

        if (rv1 != null) {
            return true;
        }
        return false;

    }

}
