
package ksxsdk;

public class ActuatorDev {

    public int REGISTER_READ_START_ADDRESS = 0;
    public int REGISTER_READ_WORD_LENGTH = 0;
    public int REGISTER_WRITE_START_ADDRESS = 0;

    public final KSX_Device mDevice;

    private boolean is_hold_time;
    private boolean is_position;

    public int status;
    public int opid;
    public int remain_time;
    public int position;
    public int hold_time;
    public int ratio;
    public int opentime;
    public int closetime;

    public byte[] bulid_control_msg(ACTUATOR_COMMAND mOperation, int mOpid, long mHoldtime, short mRatio, int mPosition,
            int opentime, int closetime) {
        byte[] buffer = null;

        int mindex = 0;

        // 각데이터 형에 맞추어서 타입을 변경함.
        double set_operation = (double) mOperation.getValue();
        double set_opid = mOpid;
        double set_position = mPosition;

        double set_opentime = opentime;
        double set_closetime = closetime;

        switch (mOperation) {
        case OPERATION_OFF_STOP:
        case OPERATION_SWITCH_ON:
        case OPERATION_RETRACTABLE_CLOSE:
        case OPERATION_RETRACTABLE_OPEN:
            buffer = new byte[4];

            mindex += ByteUtil.cast_value_to_bytes_insert_buffer(set_operation, buffer, mindex);
            mindex += ByteUtil.cast_value_to_bytes_insert_buffer(set_opid, buffer, mindex);

            break;

        case OPERATION_SWITCH_TIMED_ON:
        case OPERATION_RETRACTABLE_TIMED_CLOSE:
        case OPERATION_RETRACTABLE_TIMED_OPEN:
            if (mDevice.getControllevel().getValue() >= (int) CONTROL_LEVEL.LV_1.getValue()) {
                buffer = new byte[8];
                mindex += ByteUtil.cast_value_to_bytes_insert_buffer(set_operation, buffer, mindex);
                mindex += ByteUtil.cast_value_to_bytes_insert_buffer(set_opid, buffer, mindex);
                mindex += ByteUtil.cast_value_to_bytes_insert_buffer(mHoldtime, buffer, mindex);

                // ByteUtil.Debugout_hexstring(buffer);

            }
            break;

        case OPERATION_SWITCH_DIREATIONAL_ON:
            if (mDevice.getControllevel().getValue() >= (int) CONTROL_LEVEL.LV_2.getValue()) {
                buffer = new byte[10];

                mindex += ByteUtil.cast_value_to_bytes_insert_buffer(set_operation, buffer, mindex);
                mindex += ByteUtil.cast_value_to_bytes_insert_buffer(set_opid, buffer, mindex);
                mindex += ByteUtil.cast_value_to_bytes_insert_buffer(mHoldtime, buffer, mindex);
                mindex += ByteUtil.cast_value_to_bytes_insert_buffer(mRatio, buffer, mindex);

            }
            break;

        case OPERATION_RETRACTABLE_SET_POSITION:
            if (mDevice.getControllevel().getValue() >= (int) CONTROL_LEVEL.LV_2.getValue()) {
                buffer = new byte[6];
                mindex += ByteUtil.cast_value_to_bytes_insert_buffer(set_operation, buffer, mindex);
                mindex += ByteUtil.cast_value_to_bytes_insert_buffer(set_opid, buffer, mindex);
                mindex += ByteUtil.cast_value_to_bytes_insert_buffer(set_position, buffer, mindex);
            }
            break;

        case OPERATION_RETRACTABLE_SET_CONFIG:
            if (mDevice.getControllevel().getValue() >= (int) CONTROL_LEVEL.LV_2.getValue()) {
                buffer = new byte[8];

                mindex += ByteUtil.cast_value_to_bytes_insert_buffer(set_operation, buffer, mindex);
                mindex += ByteUtil.cast_value_to_bytes_insert_buffer(set_opid, buffer, mindex);
                mindex += ByteUtil.cast_value_to_bytes_insert_buffer(set_opentime, buffer, mindex);
                mindex += ByteUtil.cast_value_to_bytes_insert_buffer(set_closetime, buffer, mindex);

            }
            break;

        }

        return buffer;

    }

    public ActuatorDev(KSX_Device mdev) {

        mDevice = mdev;

        this.REGISTER_READ_START_ADDRESS = mDevice.CommSpec.KS_X_3267_2018.read.starting_register;
        this.REGISTER_WRITE_START_ADDRESS = mDevice.CommSpec.KS_X_3267_2018.write.starting_register;

        this.is_hold_time = false;
        this.is_position = false;

        if (mDevice.getControllevel() == CONTROL_LEVEL.LV_0) {
            this.REGISTER_READ_WORD_LENGTH = 1;
        } else if (mDevice.getControllevel().getValue() >= CONTROL_LEVEL.LV_0.getValue()) {
            if (mDevice.getActuatorType() == ACTUATOR_TYPE.RETRACTABLE) {

                int sizeholdtime = KSX326xMetadata.getsizeWithItemcheck(mDevice.CommSpec.KS_X_3267_2018.read.items,
                        KSX326xMetadata.ELEMENT_stateholdtime, 2);
                int sizeposition = KSX326xMetadata.getsizeWithItemcheck(mDevice.CommSpec.KS_X_3267_2018.read.items,
                        KSX326xMetadata.ELEMENT_position, 2);

                if (sizeholdtime > 0) {
                    is_hold_time = true;
                }

                if (mDevice.getControllevel().getValue() >= (int) CONTROL_LEVEL.LV_2.getValue()) {
                    this.REGISTER_READ_WORD_LENGTH = 1 + 1 + sizeholdtime + 1 + 2 + 1 + 1;

                } else {
                    this.REGISTER_READ_WORD_LENGTH = 1 + 1 + sizeholdtime + sizeposition + 2;

                    if (sizeposition > 0) {
                        this.is_position = true;
                    }

                }

            } else {
                int sizeholdtime = KSX326xMetadata.getsizeWithItemcheck(mDevice.CommSpec.KS_X_3267_2018.read.items,
                        KSX326xMetadata.ELEMENT_stateholdtime, 2);

                if (sizeholdtime > 0) {
                    is_hold_time = true;
                }

                if (mDevice.getControllevel().getValue() >= (int) CONTROL_LEVEL.LV_2.getValue()) {
                    this.REGISTER_READ_WORD_LENGTH = 1 + 1 + 2 + 1 + sizeholdtime;
                } else {
                    this.REGISTER_READ_WORD_LENGTH = 1 + 1 + 2 + sizeholdtime;
                }

            }

        }

    }

    public boolean deSerialize(byte[] buffer) {
        ByteUtil wrapper = ByteUtil.wrap(buffer);

        if (REGISTER_READ_WORD_LENGTH * 2 != buffer.length) {
            return false;
        }

        this.status = wrapper.getUShort();

        if (mDevice.getControllevel().getValue() >= (int) CONTROL_LEVEL.LV_1.getValue()) {

            if (mDevice.getActuatorType() == ACTUATOR_TYPE.SWITCH) {
                this.opid = wrapper.getUShort();

                if (mDevice.getControllevel().getValue() >= (int) CONTROL_LEVEL.LV_2.getValue()) {

                    this.remain_time = wrapper.getUInt();
                    this.ratio = wrapper.getUShort();

                } else {
                    this.remain_time = (int) wrapper.getInt();

                    if (this.is_hold_time == true) {
                        this.hold_time = wrapper.getUInt();
                    }

                }

            }

            if (mDevice.getActuatorType() == ACTUATOR_TYPE.RETRACTABLE) {
                this.opid = wrapper.getUShort();

                if (mDevice.getControllevel().getValue() >= CONTROL_LEVEL.LV_2.getValue()) {
                    this.remain_time = wrapper.getUInt();
                    this.position = wrapper.getUShort();

                    if (this.is_hold_time == true) {
                        this.hold_time = wrapper.getUInt();

                    }

                } else {
                    this.remain_time = wrapper.getUInt();

                    if (this.is_position == true) {
                        this.position = wrapper.getUShort();

                    }
                    if (this.is_hold_time == true) {
                        this.hold_time = wrapper.getUInt();

                    }

                }

            }
        }

        return true;

    }

}
