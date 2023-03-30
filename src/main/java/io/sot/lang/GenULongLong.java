package io.sot.lang;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;

/**
 * <b>ULONGLONG:</b> An unsigned 8-byte (64-bit) value. The range is 0 to (2^64)-1. ULONGLONG = 8BYTE
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/d2ed21d6-527b-46ac-8035-94f6f68eb9a8">General
 * Rules</a>
 * <p>
 *
 * @author user
 */
public class GenULongLong
        extends GenLongLong
{
    private static final Logger LOG = LogManager.getLogger(GenULongLong.class);

    public GenULongLong()
    {
        super();
    }

    public GenULongLong(long value)
    {
        super(value);
    }

    /**
     * @deprecated Narrow casting GenULongLong to long will generate incorrect result when GenULong is greater than (2^63)-1.
     */
    @Deprecated
    @Override
    public long longValue()
    {
        LOG.warn(GenInteger.getCastWarning(CastType.UNSIGNED_LONG, CastType.LONG));
        return super.longValue();
    }

    @Override
    public BigInteger bigIntegerValue()
    {
        return new BigInteger(new byte[] {(byte) 0, (byte) (x >> 56), (byte) (x >> 48), (byte) (x >> 40),
                (byte) (x >> 32), (byte) (x >> 24), (byte) (x >> 16), (byte) (x >> 8), (byte) x});
    }

    @Override
    public GenULongLong swapBytes()
    {
        return new GenULongLong(_swapBytes());
    }
}
