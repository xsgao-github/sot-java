package io.sot.lang;

import org.apache.logging.log4j.*;

import java.math.BigInteger;

/**
 * <b>BYTE:</b> An unsigned single byte (8-bit) value. The range is 0 to 255. BYTE = 8BIT
 * <p>
 * TDS defines data type as BYTE, but since it's always unsigned, we select <code>GenUByte</code> as it's name here.
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/d2ed21d6-527b-46ac-8035-94f6f68eb9a8">General
 * Rules</a>
 * <p>
 *
 * @author user
 */
public class GenUByte
        extends GenInteger<GenUByte>
        implements Comparable<GenUByte>
{
    public static final long MAX_VALUE;

    static {
        MAX_VALUE = (long) (Math.pow(2, 8) - 1);
    }

    public static final byte LENGTH = 1;

    private static final Logger LOG = LogManager.getLogger(GenUByte.class);

    byte x;

    public GenUByte()
    {
    }

    public GenUByte(byte value)
    {
        this.x = value;
    }

    public GenUByte setByte(byte value)
    {
        this.x = value;
        return this;
    }

    /**
     * @deprecated Narrow casting GenUByte to byte will generate incorrect result when GenUByte is greater than 127.
     */
    @Deprecated
    @Override
    public byte byteValue()
    {
        LOG.warn(GenInteger.getCastWarning(CastType.UNSIGNED_BYTE, CastType.BYTE));
        return this.x;
    }

    @Override
    public short shortValue()
    {
        return (short) (this.x & 0xFF);
    }

    @Override
    public int intValue()
    {
        return this.x & 0xFF;
    }

    @Override
    public long longValue()
    {
        return this.x & 0xFF;
    }

    @Override
    public BigInteger bigIntegerValue()
    {
        return BigInteger.valueOf(this.x & 0xFF);
    }

    @Override
    public byte getLength()
    {
        return GenUByte.LENGTH;
    }

    /**
     * @deprecated nothing to swap.
     */
    @Deprecated
    @Override
    public GenUByte swapBytes()
    {
        return new GenUByte(this.x);
    }

    @Override
    public int hashCode()
    {
        return x;
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        GenUByte other = (GenUByte) obj;
        return x == other.x;
    }

    @Override
    public String toString()
    {
        return String.valueOf(this.x & 0xFF);
    }

    @Override
    public int compareTo(GenUByte o)
    {
        return this.x - o.x;
    }
}
