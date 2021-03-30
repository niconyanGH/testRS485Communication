package ksxsdk;

public class STDModbusResponse {

    public int rep_function;
    public int byte_length;
    public byte[] byteDatas;
    public int[] wordDatas;
    public float[] floatDatas;

    public STDModbusResponse(byte[] mResponseMsg) {
        byte_length = (int) (mResponseMsg[2] & 0xFF);

        rep_function = (int) mResponseMsg[1];
        boolean is_reg_read = false;

        switch (rep_function) {

        case MDFunction.MODBUS_FC_READ_HOLDING_REGISTERS:
        case MDFunction.MODBUS_FC_READ_INPUT_REGISTERS:

            is_reg_read = true;
            break;
        default:

            break;
        }

        if (is_reg_read == true) {
            byteDatas = new byte[byte_length];
            wordDatas = new int[byte_length / 2];

            for (int i = 0; i < wordDatas.length; i++) {
                byteDatas[i * 2 + 1] = mResponseMsg[3 + i * 2];
                byteDatas[i * 2 + 0] = mResponseMsg[4 + i * 2];
            }
            for (int i = 0; i < wordDatas.length; i++) {
                short regv = (short) (mResponseMsg[3 + (i * 2)] << 8 | mResponseMsg[4 + (i * 2)]);
                wordDatas[i] = (int) regv;
            }
            if (byte_length >= 4) {
                floatDatas = new float[byte_length / 4];
                byte[] bytefloat = new byte[4];

                for (int i = 0; i < floatDatas.length; i++) {
                    bytefloat[0] = mResponseMsg[4 + (i * 4)];
                    bytefloat[1] = mResponseMsg[3 + (i * 4)];
                    bytefloat[2] = mResponseMsg[6 + (i * 4)];
                    bytefloat[3] = mResponseMsg[5 + (i * 4)];

                    floatDatas[i] = castByteArray_To_Float(bytefloat);

                }

            }
        }

    }

    public static float castByteArray_To_Float(byte[] srcArray) {
        int asInt = (srcArray[0] & 0xFF) | ((srcArray[1] & 0xFF) << 8) | ((srcArray[2] & 0xFF) << 16)
                | ((srcArray[3] & 0xFF) << 24);
        return Float.intBitsToFloat(asInt);
    }

}
