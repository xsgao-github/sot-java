package io.sot.lang;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;

/**
 * <b>ULONG:</b> An unsigned 4-byte (32-bit) value. The range is 0 to (2^32)-1. LONG = 4BYTE
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/d2ed21d6-527b-46ac-8035-94f6f68eb9a8">General
 * Rules</a>
 * <p>
 *
 * @author user
 */
public class GenULong
        extends GenLong
{
    private static final Logger LOG = LogManager.getLogger(GenULong.class);

    public GenULong()
    {
        super();
    }

    public GenULong(int value)
    {
        super(value);
    }

    /**
     * @deprecated Narrow casting GenULong to int will generate incorrect result when GenULong is greater than (2^31)-1.
     */
    @Deprecated
    @Override
    public int intValue()
    {
        LOG.warn(GenInteger.getCastWarning(CastType.UNSIGNED_INT, CastType.INT));
        return super.x;
    }

    @Override
    public long longValue()
    {
        return this.x & 0xFFFFFFFFL;
    }

    @Override
    public BigInteger bigIntegerValue()
    {
        return BigInteger.valueOf(this.x & 0xFFFFFFFFL);
    }

    @Override
    public GenULong swapBytes()
    {
        return new GenULong(_swapBytes());
    }

    @Override
    public String toString()
    {
        return String.valueOf(longValue());
    }
}
