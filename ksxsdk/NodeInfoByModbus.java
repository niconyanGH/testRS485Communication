
package ksxsdk;

public class NodeInfoByModbus {
    public static final int REGISTER_WORD_LENGTH = 8;
    public static final int REGISTER_START_ADDRESS = 1;

    public int CertificateAuthority;
    public int CompanyCode;
    public int ProductType;
    public int ProductCode;
    public int ProtocolVersion;
    public int ChannelNumber;
    public int SerialNumber;

    public boolean deSerialize(byte[] buffer) {
        ByteUtil wrapper = ByteUtil.wrap(buffer);

        if (buffer.length != (REGISTER_WORD_LENGTH * 2)) {
            return false;
        }
        this.CertificateAuthority = wrapper.getUShort();
        this.CompanyCode = wrapper.getUShort();
        this.ProductType = wrapper.getUShort();
        this.ProductCode = wrapper.getUShort();
        this.ProtocolVersion = wrapper.getUShort();
        this.ChannelNumber = wrapper.getUShort();
        this.SerialNumber = wrapper.getUInt();
        return true;
    }

}
