package io.sot.lang;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;

/**
 * <p>
 * <b>LONG:</b> A signed 4-byte (32-bit) value. The range is -(2^31) to (2^31)-1. LONG = 4BYTE
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/d2ed21d6-527b-46ac-8035-94f6f68eb9a8">General
 * Rules</a>
 *
 * @author user
 */
public class GenLong
        extends GenInteger<GenLong>
        implements Comparable<GenLong>
{
    public static final long MAX_VALUE = (long) (Math.pow(2, 32) - 1);

    public static final byte LENGTH = 4;

    int x;

    private static final Logger LOG = LogManager.getLogger(GenLong.class);

    public GenLong()
    {
    }

    public GenLong(int value)
    {
        this.x = value;
    }

    public GenLong setInt(int value)
    {
        this.x = value;
        return this;
    }

    /**
     * @deprecated Narrow casting Long to byte will generate incorrect result when Long is greater than 127.
     */
    @Deprecated
    @Override
    public byte byteValue()
    {
        LOG.warn(GenInteger.getCastWarning(CastType.INT, CastType.BYTE));
        return (byte) this.x;
    }

    /**
     * @deprecated Narrow casting GenLong to short will generate incorrect result when GenLong is greater than 32,767.
     */
    @Deprecated
    @Override
    public short shortValue()
    {
        LOG.warn(GenInteger.getCastWarning(CastType.INT, CastType.SHORT));
        return (short) this.x;
    }

    @Override
    public int intValue()
    {
        return this.x;
    }

    @Override
    public long longValue()
    {
        return this.x;
    }

    @Override
    public BigInteger bigIntegerValue()
    {
        return BigInteger.valueOf(this.x);
    }

    @Override
    public byte getLength()
    {
        return GenLong.LENGTH;
    }

    @Override
    public GenLong swapBytes()
    {
        return new GenLong(_swapBytes());
    }

    protected int _swapBytes()
    {
        return ((x & 0xFF) << 24) | (((x >> 8) & 0xFF) << 16) | (((x >> 16) & 0xFF) << 8) | ((x >> 24) & 0xFF);
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
        GenLong other = (GenLong) obj;
        return x == other.x;
    }

    @Override
    public String toString()
    {
        return String.valueOf(x);
    }

    @Override
    public int compareTo(GenLong o)
    {
        return this.x - o.x;
    }
}
