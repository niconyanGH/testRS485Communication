
package ksxsdk;

import java.util.List;

public class KSX326xMetadata {

    public static String ELEMENT_classnode = "node";

    public static String ELEMENT_sensornode = "sensor-node";
    public static String ELEMENT_actuatornode = "actuator-node";
    public static String ELEMENT_integratednode = "integrated-node";

    public static String ELEMENT_classsensor = "sensor";
    public static String ELEMENT_classactuator = "actuator";

    public static String ELEMENT_typeswitch = "switch";
    public static String ELEMENT_typeretractable = "retractable";
    public static String ELEMENT_typenutrient = "nutrient-supply";

    public static String ELEMENT_level1 = "level1";
    public static String ELEMENT_level2 = "level2";
    public static String ELEMENT_level3 = "level3";

    public static String ELEMENT_value = "value";
    public static String ELEMENT_opid = "opid";
    public static String ELEMENT_operation = "operation";
    public static String ELEMENT_control = "control";
    public static String ELEMENT_status = "status";
    public static String ELEMENT_remaintime = "remain-time";
    public static String ELEMENT_holdtime = "hold-time";
    public static String ELEMENT_time = "time";

    public static String ELEMENT_irrgationarea = "irrgation-area";
    public static String ELEMENT_alertinformation = "alert-information";

    public static String ELEMENT_stateholdtime = "state-hold-time";
    public static String ELEMENT_position = "position";

    public static String ELEMENT_startarea = "start-area";
    public static String ELEMENT_endarea = "end-area";
    public static String ELEMENT_onesec = "on-sec";
    public static String ELEMENT_ec = "EC";
    public static String ELEMENT_ph = "pH";

    public static String GetStatusDescrition(STATUS_CODE mStatus) {
        String cs = "";

        switch (mStatus) {

        case READY:
            cs = "정상";
            break;
        case ERROR:
            cs = "오류";
            break;
        case BUSY:
            cs = "처리 불능";
            break;
        case VOLTAGE_ERROR:
            cs = "동작 전압 이상";
            break;
        case CURRENT_ERROR:
            cs = "동작 전류 이상";
            break;
        case FUSE_ERROR:
            cs = "휴즈 이상";
            break;
        case COMMON_RESERVED:
            cs = "공통 예약";
            break;

        case SENSOR_NEED_REPLACE:
            cs = "센서 및 소모품 교체 요망";
            break;
        case SENSOR_NEED_CALIBRATION:
            cs = "센서 교정 요망";
            break;
        case SENSOR_NEED_CHECK:
            cs = "센서 점검 필요";
            break;

        case SWITCH_ON:
            cs = "작동 중";
            break;
        case SWITCH_USER_CONTROL:
            cs = "사용자 제어 중";
            break;

        case REACTABLE_OPENING:
            cs = "여는 중";
            break;
        case REACTABLE_CLOSING:
            cs = "닫는 중";
            break;
        case REACTABLE_MANUAL_CONTROL:
            cs = "사용자 제어 중";
            break;

        case NUTRIENT_PREPARING:
            cs = "준비 중";
            break;
        case NUTRIENT_SUPPLYING:
            cs = "제공 중";
            break;
        case NUTRIENT_STOPPING:
            cs = "정지 중";
            break;

        case VENDOR_SPECIFIC_ERROR:
            cs = "제조사 정의 에러 코드";
            break;

        default:
            cs = "제조사정의";
            break;
        }

        return cs;

    }

    public static String GetStringValueByDigit(int mdigit, float value) {
        String formatstr = "";

        switch (mdigit) {

        case 0:
            formatstr = "%d";
            break;
        case 1:
            formatstr = "%.1f";
            break;
        case 2:
            formatstr = "%.2f";
            break;
        case 3:
            formatstr = "%.3f";
            break;
        case 4:
            formatstr = "%.4f";
            break;
        case 5:
            formatstr = "%.5f";
            break;
        case 6:
            formatstr = "%.6f";
            break;
        default:
            formatstr = "%.3f";
            break;
        }

        String cs;

        if (mdigit == 0) {
            cs = String.format(formatstr, (int) value);
        } else {
            cs = String.format(formatstr, value);
        }

        return cs;

    }

    /// <summary>
    /// 노드에서 응답메시지의 크기를 결정하기 위해 옵션으로 설정되어있는 항목이 있는지 확인함.
    /// </summary>
    /// <param name="items"></param>
    /// <param name="checkstr"></param>
    /// <param name="retsize"></param>
    /// <returns></returns>
    public static int getsizeWithItemcheck(List<String> items, String checkstr, int retsize) {
        int retint = 0;
        for (String istr : items) {
            if (istr.contains(checkstr) == true) {

                retint = retsize;
                break;
            }
        }

        return retint;

    }

    /// <summary>
    /// 노드의 기본정보와 맞는 메타데이터를 리턴한다.
    /// </summary>
    /// <param name="minfo"></param>
    /// <returns></returns>
    public static KSX_NodeMeta GetNodeMetaData(NodeInfoByModbus minfo) {

        List<KSX_NodeMeta> mLMata = JsonFiles.getNodeMetadatas();

        for (KSX_NodeMeta mAMata : mLMata) {

            // 회사코드와 기관코드 , 제품타입을 비교
            if (minfo.CompanyCode == mAMata.CompanyCode && minfo.CertificateAuthority == mAMata.CertificateAuthority) {
                if (mAMata.Class.contains("node") == true) {
                    PRODUCT_TYPE ptype = PRODUCT_TYPE.getEnum(minfo.ProductType);

                    switch (ptype) {
                    case SENSORNODE:
                        if (mAMata.Type.contains(ELEMENT_sensornode) == true) {
                            return mAMata;
                        }

                        break;
                    case ACTUATORNODE:
                        if (mAMata.Type.contains(ELEMENT_actuatornode) == true) {
                            return mAMata;
                        }

                        break;
                    case INTEGRATEDNODE:

                        if (mAMata.Type.contains(ELEMENT_integratednode) == true) {
                            return mAMata;
                        }

                        break;
                    default:
                        break;
                    }

                }

            }

        }

        return null;
    }

}
