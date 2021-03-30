package ksxsdk;

import java.nio.ByteOrder;
import com.fazecast.jSerialComm.SerialPort;

public class STDModbusRTUMaster {

    private ByteCircularBuffer mRecvBytesQueue_modbus;
    private SerialPort mSerialPort;

    private boolean STD_ModbusRTU_check_integrity(int mslave, byte[] msg) {
        int crc_calculated;
        int crc_received;
        int slave = msg[0];
        int msg_length = msg.length;

        if (slave != mslave && slave != 0) {

            System.out.println("잘못된 슬레이브 주소입니다. my address =" + mslave + " recived address = " + slave);

            return false;
        }

        crc_calculated = STDModbusRTUCommon.GetCRC16(msg, msg_length - 2);
        int hi = (int) (msg[msg_length - 2] & 0xFF);
        int lo = (int) (msg[msg_length - 1] & 0xFF);

        crc_received = ((hi << 8) | lo) & 0xFFFF;

        if (crc_calculated == crc_received) {

            return true;
        } else {

            System.out.println("잘못된 CRC 값.계산 = " + Integer.toHexString(crc_calculated) + " ,받은 ="
                    + Integer.toHexString(crc_received));

            for (int i = 0; i < msg_length; i++) {
                System.out.println("[ " + i + "]=0x" + Integer.toHexString(msg[i]));
            }
            return false;
        }

    }

    public STDModbusResponse StandardManualWordRead_F3(int Slave, int StartAddr, int Length, int waitMillisecond) {
        byte[] data;
        int ResponseByteCount = Length * 2 + 5;

        data = STDModbusRTUCommon.bulid_read_registers(Slave, MDFunction.MODBUS_FC_READ_HOLDING_REGISTERS, StartAddr,
                Length);

        Write(data, true);

        for (int i = 0; i < 10; i++) {

            int div110msec = waitMillisecond / 10;

            ThreadSleep(div110msec);

            ReadPolling();

            if (mRecvBytesQueue_modbus.size() >= ResponseByteCount) {
                byte[] ResponseBytes = new byte[ResponseByteCount];
                mRecvBytesQueue_modbus.popBytes(ResponseBytes, 0, ResponseBytes.length);
                if (STD_ModbusRTU_check_integrity(Slave, ResponseBytes) == true) {

                    return (new STDModbusResponse(ResponseBytes));

                }
                System.out.println("잘못된 응답입니다.");

                return null;

            }

        }
        System.out.println("응답시간초과..");

        return null;

    }

    public STDModbusResponse StandardManualWordWrite_F10(int Slave, int StartAddr, byte[] Values,
            int waitMillisecond) {
        byte[] data;
        int ResponseByteCount = 8;

        data = STDModbusRTUCommon.bulid_write_registers(Slave, MDFunction.MODBUS_FC_WRITE_MULTIPLE_REGISTERS, StartAddr,
                Values);

        Write(data, true);

        for (int i = 0; i < 10; i++) {

            int div110msec = waitMillisecond / 10;

            ThreadSleep(div110msec);
            ReadPolling();

            if (mRecvBytesQueue_modbus.size() >= ResponseByteCount) {
                byte[] ResponseBytes = new byte[ResponseByteCount];
                mRecvBytesQueue_modbus.popBytes(ResponseBytes, 0, ResponseBytes.length);
                if (STD_ModbusRTU_check_integrity(Slave, ResponseBytes) == true) {
                    return (new STDModbusResponse(ResponseBytes));
                }
                System.out.println("잘못된 응답입니다.");
                return null;

            }

        }

        System.out.println("응답시간초과..");

        return null;

    }

    private void ReadPolling() {

        if (IsOpen() == true) {
            byte[] buffer = new byte[0x10000];

            try {
                int nsize = mSerialPort.readBytes(buffer, buffer.length);
                if (nsize > 0) {

                    mRecvBytesQueue_modbus.pushBytes(buffer, 0, nsize);

                }

            } catch (Exception e) {
                System.out.println("통신포트 읽기 에러 = " + e.toString());
            }

        }
    }

    private boolean Write(byte[] mdatas, boolean queclear) {

        if (IsOpen() == true && mdatas != null) {
            try {
                if (queclear == true) {
                    mRecvBytesQueue_modbus.clear();
                }

                mSerialPort.writeBytes(mdatas, mdatas.length, 0);

            } catch (Exception e1) {
                System.out.println(e1.toString());
                return false;
            }
            return true;

        }
        return false;

    }

    private void ThreadSleep(int timemsec) {
        try {
            Thread.sleep(timemsec);
        } catch (InterruptedException e) {
            System.out.println(e.toString());
        }
    }

    public STDModbusRTUMaster() {

        mRecvBytesQueue_modbus = new ByteCircularBuffer((int) 40960, ByteOrder.LITTLE_ENDIAN);

    }

    protected void finalize() {

        Close();
    }

    public void Close() {
        mSerialPort.closePort();

    }

    public boolean IsOpen() {
        if (mSerialPort == null) {
            return false;
        }

        return true;
    }

    public boolean Open(String portNumber, int mbaudrate) {
        if (mSerialPort != null) {
            System.out.println("Serial::Open // 이미 open 되었습니다.");
            return true;
        }

        try {

            SerialPort[] ports = SerialPort.getCommPorts();

            for (int i = 0; i < ports.length; ++i) {
                System.out.println("   [" + i + "] " + ports[i].getSystemPortName() + ": "
                        + ports[i].getDescriptivePortName() + " - " + ports[i].getPortDescription());

                if (portNumber.contains(ports[i].getSystemPortName()) == true) {

                    mSerialPort = ports[i];
                    mSerialPort.openPort(0);
                    mSerialPort.setBaudRate(mbaudrate);
                    mSerialPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 1000, 0);

                }

            }

        } catch (Exception e) {
            System.out.println("Serial::Open // Fail!! // Device is not reponse. " + portNumber);
            System.out.println(e.toString());
            return false;
        }

        System.out.println("Serial::Open // Success!!  " + portNumber);

        try {
            byte[] buffer = new byte[10000];

            int i = mSerialPort.readBytes(buffer, buffer.length);
            System.out.println("read =  " + i);
        } catch (Exception e) {
            System.out.println("IOException =  ");
        }

        return true;
    }

}
