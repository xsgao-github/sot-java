package io.sot.lang;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;

/**
 * <b>LONGLONG:</b> A signed 8-byte (64-bit) value. The range is -(2^63) to (2^63)-1. LONGLONG = 8BYTE
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/d2ed21d6-527b-46ac-8035-94f6f68eb9a8">General
 * Rules</a>
 * <p>
 *
 * @author user
 */
public class GenLongLong
        extends GenInteger<GenLongLong>
        implements Comparable<GenLongLong>
{
    public static final BigInteger MAX_VALUE = BigInteger.valueOf(2).pow(64).subtract(BigInteger.ONE);

    public static final byte LENGTH = 8;

    long x;

    private static final Logger LOG = LogManager.getLogger(GenLongLong.class);

    public GenLongLong()
    {
    }

    public GenLongLong(long value)
    {
        this.x = value;
    }

    public GenLongLong setLong(long value)
    {
        this.x = value;
        return this;
    }

    /**
     * @deprecated Narrow casting GenLongLong to byte will generate incorrect result when GenLongLong is greater than 127.
     */
    @Deprecated
    @Override
    public byte byteValue()
    {
        LOG.warn(GenInteger.getCastWarning(CastType.LONG, CastType.BYTE));
        return (byte) this.x;
    }

    /**
     * @deprecated Narrow casting GenLongLong to short will generate incorrect result when GenLongLong is greater than 32,767.
     */
    @Deprecated
    @Override
    public short shortValue()
    {
        LOG.warn(GenInteger.getCastWarning(CastType.LONG, CastType.SHORT));
        return (short) this.x;
    }

    /**
     * @deprecated Narrow casting GenLongLong to int will generate incorrect result when GenLongLong is greater than (2^31)-1.
     */
    @Deprecated
    @Override
    public int intValue()
    {
        LOG.warn(GenInteger.getCastWarning(CastType.LONG, CastType.INT));
        return (int) this.x;
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
        return GenLongLong.LENGTH;
    }

    @Override
    public GenLongLong swapBytes()
    {
        return new GenLongLong(_swapBytes());
    }

    protected long _swapBytes()
    {
        return ((x & 0xFF) << 56) | (((x >> 8) & 0xFF) << 48) | (((x >> 16) & 0xFF) << 40) | (((x >> 24) & 0xFF) << 32)
                | (((x >> 32) & 0xFF) << 24) | (((x >> 40) & 0xFF) << 16) | (((x >> 48) & 0xFF) << 8)
                | ((x >> 56) & 0xFF);
    }

    @Override
    public int hashCode()
    {
        return (int) x;
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
        GenLongLong other = (GenLongLong) obj;
        return x == other.x;
    }

    @Override
    public String toString()
    {
        return String.valueOf(x);
    }

    @Override
    public int compareTo(GenLongLong o)
    {
        if (this.x > o.x) {
            return 1;
        }
        else if (this.x == o.x) {
            return 0;
        }
        else {
            return -1;
        }
    }
}
