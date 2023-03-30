package io.sot.lang;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * To implement messages on top of existing, arbitrary transport layers, a packet header is included as part of the
 * packet. The packet header precedes all data within the packet. It is always 8 bytes in length. Most importantly, the
 * packet header states the Type and Length of the entire packet.
 * <p>
 * <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/7af53667-1b72-4703-8258-7984e838f746">Packet
 * Header</a>
 *
 * @author user
 */
public class PacketHeader
{
    public static final int TYPE_POS = 0;
    public static final int STATUS_POS = 1;
    public static final int LENGTH_POS = 2;
    public static final int SPID_POS = 4;
    public static final int PACKET_ID_POS = 6;
    public static final int WINDOW_POS = 7;

    public static final byte LENGTH = 8;

    public static final short MIN_NEG_LENGTH = 512;
    public static final short MAX_NEG_LENGTH = 32767;

    private Type type;
    private GenUByte status;
    private GenUShort length;
    private GenUShort SPID;
    private GenUByte packetID;
    private GenNull window;

    public PacketHeader()
    {
    }

    public void read(byte[] b)
            throws PacketRWException, IOException
    {
        type = Type.valueOf(new GenUByte(b[PacketHeader.TYPE_POS]));
        status = new GenUByte(b[PacketHeader.STATUS_POS]);
        length = new GenUShort((short) (((b[LENGTH_POS] & 0xFF) << 8) | (b[LENGTH_POS + 1] & 0xFF)));
        SPID = new GenUShort((short) (((b[SPID_POS] & 0xFF) << 8) | (b[SPID_POS + 1] & 0xFF)));
        packetID = new GenUByte(b[PacketHeader.PACKET_ID_POS]);
        if (b[PacketHeader.WINDOW_POS] == GenNull.BYTE_VALUE) {
            window = GenNull.NULL;
        }
        else {
            throw new PacketRWException("WINDOW is 0x%02X instead of 0x%0sX", b[PacketHeader.WINDOW_POS],
                    GenNull.BYTE_VALUE);
        }
    }

    public byte[] toByteArray()
    {
        byte[] b = new byte[PacketHeader.LENGTH];
        b[PacketHeader.TYPE_POS] = (byte) type.value.shortValue();
        b[PacketHeader.STATUS_POS] = (byte) status.shortValue();
        // length is big-endian
        b[PacketHeader.LENGTH_POS] = (byte) ((length.intValue() >> 8) & 0xFF);
        b[PacketHeader.LENGTH_POS + 1] = (byte) (length.intValue() & 0xFF);
        // SPID is big-endian
        b[PacketHeader.SPID_POS] = (byte) ((SPID.intValue() >> 8) & 0xFF);
        b[PacketHeader.SPID_POS + 1] = (byte) (SPID.intValue() & 0xFF);
        b[PacketHeader.PACKET_ID_POS] = (byte) packetID.shortValue();
        b[PacketHeader.WINDOW_POS] = window.byteValue();

        return b;
    }

    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    public GenUByte getStatus()
    {
        return status;
    }

    public void setStatus(GenUByte status)
    {
        this.status = status;
    }

    public GenUShort getLength()
    {
        return length;
    }

    public void setLength(GenUShort length)
    {
        this.length = length;
    }

    public GenUShort getSPID()
    {
        return SPID;
    }

    public void setSPID(GenUShort sPID)
    {
        SPID = sPID;
    }

    public GenUByte getPacketID()
    {
        return packetID;
    }

    public void setPacketID(GenUByte packetID)
    {
        this.packetID = packetID;
    }

    public GenNull getWindow()
    {
        return window;
    }

    public void setWindow(GenNull window)
    {
        this.window = window;
    }

    /**
     * Type defines the type of message. Type is a 1-byte unsigned char. For more information, please refer to
     * <a href="https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/9b4a463c-2634-4a4b-ac35-bebfff2fb0f7">Type</a>.
     *
     * @author user
     */
    public enum Type
    {
        SQL_BATCH((byte) 1, "SQL Batch", true),
        PRE_TDS7_LOGIN((byte) 2, "Pre-TDS7 Login", true),
        RPC((byte) 3, "RPC", true),
        TABULAR((byte) 4, "Tabular result", true),
        UNUSED_5((byte) 5, "Unused 5", false),
        ATTENTION((byte) 6, "Attention signal", false),
        BULK((byte) 7, "Bulk load data", true),
        FED_AUTH_TOKEN((byte) 8, "Federated Authentication TokenStream", true),
        UNUSED_9((byte) 9, "Unused 9", false),
        UNUSED_10((byte) 10, "Unused 10", false),
        UNUSED_11((byte) 11, "Unused 11", false),
        UNUSED_12((byte) 12, "Unused 12", false),
        UNUSED_13((byte) 13, "Unused 13", false),
        TX_MGMT((byte) 14, "Transaction manager request", true),
        UNUSED_15((byte) 15, "Unused 15", false),
        TDS7_LOGIN((byte) 16, "TDS7 Login", true),
        SSPI((byte) 17, "SSPI", true),
        PRE_LOGIN((byte) 18, "Pre-Login", true);

        final private GenUByte value;
        final String description;
        final boolean containsData;

        Type(byte value, String description, boolean containsData)
        {
            this.value = new GenUByte(value);
            this.description = description;
            this.containsData = containsData;
        }

        private static final Map<GenUByte, Type> byteTypeMap = initByteTypeMap();

        private static Map<GenUByte, Type> initByteTypeMap()
        {
            Map<GenUByte, Type> m = new HashMap<>();
            for (Type t : Type.values()) {
                m.put(t.value, t);
            }
            return m;
        }

        public GenUByte getValue()
        {
            return value;
        }

        public static Type valueOf(GenUByte value)
                throws EnumValueLookupException
        {
            Type t = byteTypeMap.get(value);
            if (t != null) {
                return t;
            }
            else {
                throw new EnumValueLookupException(PacketHeader.class, PacketHeader.Type.class, value);
            }
        }

        @Override
        public String toString()
        {
            return String.format("%s(0x%02X)", name(), value.shortValue());
        }
    }

    public enum StatusFlag
    {
        NORMAL((byte) 0x00),
        EOM((byte) 0x01),
        IGNORE((byte) 0x20),
        RESET_CONNECTION((byte) 0x80),
        RESET_CONNECTION_SKIP_TRAN((byte) 0x10);

        final byte value;

        StatusFlag(byte value)
        {
            this.value = value;
        }

        public byte getValue()
        {
            return value;
        }
    }
}
