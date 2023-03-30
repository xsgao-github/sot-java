package io.sot.lang;

/**
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/d2ed21d6-527b-46ac-8035-94f6f68eb9a8">General
 * Rules</a>
 * <p>
 * <b>GEN_NULL:</b> A single byte (8-bit) value representing a NULL value.
 * <p>
 * GEN_NULL = %x00
 *
 * @author user
 */
public class GenNull
        implements Comparable<GenNull>
{
    public static final byte LENGTH = 1;
    public static final byte BYTE_VALUE = (byte) 0x00;

    private static final byte[] BYTE_ARRAY = new byte[] {BYTE_VALUE};

    public static final GenNull NULL = new GenNull();

    protected GenNull()
    {
    }

    public byte byteValue()
    {
        return 0;
    }

    public byte[] toBytes()
    {
        return BYTE_ARRAY;
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
    public int compareTo(GenNull o)
    {
        return 0;
    }
}
