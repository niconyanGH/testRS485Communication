
package ksxsdk;
public class SensorDev {
        public  int REGISTER_READ_WORD_LENGTH = 3;
        public  int REGISTER_READ_START_ADDRESS = 0;
        public final KSX_Device mDevice;

        public float value;
        public int status;

        public SensorDev(KSX_Device mdev)
        {
            mDevice = mdev;
            this.REGISTER_READ_START_ADDRESS = mDevice.CommSpec.KS_X_3267_2018.read.starting_register;
            this.REGISTER_READ_WORD_LENGTH = 3;
        }
 
        public boolean deSerialize(byte[] buffer)
        {
            ByteUtil wrapper = ByteUtil.wrap(buffer);
            if (buffer.length != (REGISTER_READ_WORD_LENGTH * 2))
            {
                return false;
            }
            this.value = wrapper.getFloat();
            this.status = wrapper.getUShort();

            return true;
        }

        public String GetValuestring(boolean iswithunit)
        {
            String cs= KSX326xMetadata.GetStringValueByDigit(mDevice.SignificantDigit, value);
            if (iswithunit == true)
            {
                cs += mDevice.ValueUnit;
            }

            return cs;
        }


}
