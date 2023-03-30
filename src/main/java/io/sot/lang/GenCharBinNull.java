package io.sot.lang;

/**
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/d2ed21d6-527b-46ac-8035-94f6f68eb9a8">General
 * Rules</a>
 * <p>
 *
 * <b>CHARBIN_NULL:</b> A 2-byte (16-bit) or 4-byte (32-bit) value representing a T-SQL NULL value for a character or
 * binary data type. Please refer to TYPE_VARBYTE (see section 2.2.5.2.3) for additional details.
 * <p>
 * CHARBIN_NULL = (%xFF %xFF) / (%xFF %xFF %xFF %xFF)
 *
 * @author user
 */
public class GenCharBinNull
        implements Comparable<GenCharBinNull>
{
    public static final byte BYTE_VALUE = (byte) 0xFF;

    public static final byte LENGTH_2 = 2;
    public static final byte LENGTH_4 = 4;

    public static final GenCharBinNull NULL_2 = new GenCharBinNull((byte) 2);
    public static final GenCharBinNull NULL_4 = new GenCharBinNull((byte) 4);

    private static final byte[] BYTE_ARRAY_2 = new byte[] {BYTE_VALUE, BYTE_VALUE};
    private static final byte[] BYTE_ARRAY_4 = new byte[] {BYTE_VALUE, BYTE_VALUE, BYTE_VALUE, BYTE_VALUE};

    private final byte len;

    private GenCharBinNull(byte len)
    {
        this.len = len;
    }

    public byte[] toBytes()
    {
        return this.len == 2 ? BYTE_ARRAY_2 : BYTE_ARRAY_4;
    }

    @Override
    public int hashCode()
    {
        return 0;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        return getClass() == obj.getClass();
    }

    @Override
    public String toString()
    {
        return "NULL";
    }

    @Override
    public int compareTo(GenCharBinNull o)
    {
        return 0;
    }
}
