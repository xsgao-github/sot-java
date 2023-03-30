package io.sot.lang;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;

/**
 * <b>USHORT:</b> An unsigned 2-byte (16-bit) value. The range is 0 to 65535. USHORT = 2BYTE
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/d2ed21d6-527b-46ac-8035-94f6f68eb9a8">General
 * Rules</a>
 * <p>
 *
 * @author user
 */
public class GenUShort
        extends GenInteger<GenUShort>
        implements Comparable<GenUShort>
{
    public static final long MAX_VALUE = (long) (Math.pow(2, 16) - 1);

    public static final byte LENGTH = 2;

    private static final Logger LOG = LogManager.getLogger(GenUShort.class);

    short x;

    public GenUShort()
    {
    }

    public GenUShort(short value)
    {
        this.x = value;
    }

    public GenUShort setShort(short value)
    {
        this.x = value;
        return this;
    }

    /**
     * @deprecated Narrow casting GenUShort to byte will generate incorrect result when GenUShort is greater than 127.
     */
    @Deprecated
    @Override
    public byte byteValue()
    {
        LOG.warn(GenInteger.getCastWarning(CastType.UNSIGNED_SHORT, CastType.BYTE));
        return (byte) this.x;
    }

    /**
     * @deprecated Narrow casting GenUShort to short will generate incorrect result when GenUShort is greater than 32,767.
     */
    @Deprecated
    @Override
    public short shortValue()
    {
        LOG.warn(GenInteger.getCastWarning(CastType.UNSIGNED_SHORT, CastType.SHORT));
        return this.x;
    }

    @Override
    public int intValue()
    {
        return this.x & 0xFFFF;
    }

    @Override
    public long longValue()
    {
        return this.x & 0xFFFF;
    }

    @Override
    public BigInteger bigIntegerValue()
    {
        return BigInteger.valueOf(this.x & 0xFFFF);
    }

    @Override
    public byte getLength()
    {
        return GenUShort.LENGTH;
    }

    /*
     * (non-Javadoc)
     *
     * @see io.sot.lang.GenInteger#swapBytes()
     */
    @Override
    public GenUShort swapBytes()
    {
        return new GenUShort(_swapBytes());
    }

    protected short _swapBytes()
    {
        return (short) (((x & 0xFF) << 8) | ((x >> 8) & 0xFF));
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
        GenUShort other = (GenUShort) obj;
        return x == other.x;
    }

    @Override
    public String toString()
    {
        return String.valueOf(x);
    }

    @Override
    public int compareTo(GenUShort o)
    {
        return this.x - o.x;
    }
}
