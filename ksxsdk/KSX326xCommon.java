package ksxsdk;

public class KSX326xCommon {

    public static boolean isvalidcode(int certificatecode, int companycode) {
        boolean ret = false;

        if (certificatecode == 0 && companycode == 0) // 디폴트 맵노드
        {
            ret = true;
        } else if (certificatecode == 0 && companycode == 8877) // 코리아디지탈
        {
            ret = true;
        }

        return ret;
    }

    public static PRODUCT_TYPE IsKSXNode(int slaveid, STDModbusRTUMaster mRTUMaster) {
        PRODUCT_TYPE mtype = PRODUCT_TYPE.NONE;

        STDModbusResponse rv = mRTUMaster.StandardManualWordRead_F3(slaveid,
                NodeInfoByModbus.REGISTER_START_ADDRESS, NodeInfoByModbus.REGISTER_WORD_LENGTH, 200);

        if (rv != null && rv.rep_function == MDFunction.MODBUS_FC_READ_HOLDING_REGISTERS) {
            NodeInfoByModbus mNodeInfo = new NodeInfoByModbus();

            if (mNodeInfo.deSerialize(rv.byteDatas) == true) {
                if (isvalidcode(mNodeInfo.CertificateAuthority, mNodeInfo.CompanyCode) == true) {
                    mtype = PRODUCT_TYPE.getEnum(mNodeInfo.ProductType);
                }
            }
        }

        return mtype;
    }

}
