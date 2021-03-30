
package ksxsdk;

public class NutrientDev {

    public static final int OPERATION_NUITRIENT_ON = 401;
    public static final int OPERATION_NUITRIENT_OFF = 0;
    public static final int OPERATION_NUITRIENT_AREA_ON = 402;
    public static final int OPERATION_NUITRIENT_PARAM_ON = 403;

    public int REGISTER_READ_START_ADDRESS = 0;
    public int REGISTER_READ_WORD_LENGTH = 0;
    public int REGISTER_WRITE_START_ADDRESS = 0;

    public final KSX_Device mDevice;

    public int status;
    public int irrigation_area;
    public int alert_information;
    public int opid;

    public NutrientDev(KSX_Device mdev) {

        mDevice = mdev;

        this.REGISTER_READ_START_ADDRESS = mDevice.CommSpec.KS_X_3267_2018.read.starting_register;
        this.REGISTER_WRITE_START_ADDRESS = mDevice.CommSpec.KS_X_3267_2018.write.starting_register;

        if (mDevice.getControllevel() == CONTROL_LEVEL.LV_0) {
            this.REGISTER_READ_WORD_LENGTH = 3;
        } else {
            this.REGISTER_READ_WORD_LENGTH = 4;

        }

    }

    public byte[] bulid_control_msg(NUTRIENT_COMMAND mOperation, int mOpid, int mStartarea, int mEndarea, long mOnsec,
            float ec, float ph) {
        byte[] buffer = null;

        int mindex = 0;

        // 각데이터 형에 맞추어서 타입을 변경함.
        double set_operation = (double) mOperation.getValue();
        double set_opid = mOpid;

        double set_startarea = mStartarea;
        double set_endarea = mEndarea;

        switch (mOperation) {
        case OPERATION_NUITRIENT_ON:
        case OPERATION_NUITRIENT_OFF:
            buffer = new byte[4];

            mindex += ByteUtil.cast_value_to_bytes_insert_buffer(set_operation, buffer, mindex);
            mindex += ByteUtil.cast_value_to_bytes_insert_buffer(set_opid, buffer, mindex);
            break;

        case OPERATION_NUITRIENT_AREA_ON:
            buffer = new byte[12];
            mindex += ByteUtil.cast_value_to_bytes_insert_buffer(set_operation, buffer, mindex);
            mindex += ByteUtil.cast_value_to_bytes_insert_buffer(set_opid, buffer, mindex);

            mindex += ByteUtil.cast_value_to_bytes_insert_buffer(set_startarea, buffer, mindex);
            mindex += ByteUtil.cast_value_to_bytes_insert_buffer(set_endarea, buffer, mindex);
            mindex += ByteUtil.cast_value_to_bytes_insert_buffer(mOnsec, buffer, mindex);

            break;

        case OPERATION_NUITRIENT_PARAM_ON:
            buffer = new byte[20];

            mindex += ByteUtil.cast_value_to_bytes_insert_buffer(set_operation, buffer, mindex);
            mindex += ByteUtil.cast_value_to_bytes_insert_buffer(set_opid, buffer, mindex);

            mindex += ByteUtil.cast_value_to_bytes_insert_buffer(set_startarea, buffer, mindex);
            mindex += ByteUtil.cast_value_to_bytes_insert_buffer(set_endarea, buffer, mindex);
            mindex += ByteUtil.cast_value_to_bytes_insert_buffer(mOnsec, buffer, mindex);

            mindex += ByteUtil.cast_value_to_bytes_insert_buffer(ec, buffer, mindex);
            mindex += ByteUtil.cast_value_to_bytes_insert_buffer(ph, buffer, mindex);

            break;

        }

        return buffer;

    }

    public boolean deSerialize(byte[] buffer) {
        ByteUtil wrapper = ByteUtil.wrap(buffer);

        if (REGISTER_READ_WORD_LENGTH * 2 != buffer.length) {
            return false;
        }

        this.status = wrapper.getUShort();

        if (mDevice.getControllevel() == CONTROL_LEVEL.LV_0) {

        } else {
            this.irrigation_area = wrapper.getUShort();
            this.alert_information = wrapper.getUShort();
            this.opid = wrapper.getUShort();
        }

        return true;

    }
}
