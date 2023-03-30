package io.sot.lang;

import java.math.BigInteger;

/**
 * {@code GenInteger} class represents atomic/integer data values transmitted via TDS.
 * <p>
 * All integer types are represented in reverse byte order (little-endian) unless otherwise specified.
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/d2ed21d6-527b-46ac-8035-94f6f68eb9a8">General
 * Rules</a>
 * <p>
 *
 * @author user
 */
public abstract class GenInteger<E extends GenInteger<?>>
{
    private static final String[] MAX_VALUES = new String[] {"127", "255", "32,767", "65,535", "(2^31)-1", "(2^32)-1", "(2^63)-1", "(2^64)-1"};

    public abstract byte byteValue();

    public abstract short shortValue();

    public abstract int intValue();

    public abstract long longValue();

    public abstract BigInteger bigIntegerValue();

    /**
     * Get the length of bytes.
     *
     * @return
     */
    public abstract byte getLength();

    /**
     * Swap bytes of this <code>GenInteger</code>'s value and create a new <code>E</code> using swapped bytes. By default,
     * all integer types are transferred in reverse byte order (little-endian) over network, this method provides an
     * easy way to convert between little-endian and big-ending numbers.
     */
    public abstract E swapBytes();

    static final String getCastWarning(CastType from, CastType to)
    {
        return String.format(
                "Narrow casting %1$s to %2$s will generate incorrect result when %1$s is greater than %3$s.",
                from.name(), to.name(), MAX_VALUES[to.ordinal()]);
    }

    enum CastType
    {
        BYTE,
        UNSIGNED_BYTE,
        SHORT,
        UNSIGNED_SHORT,
        INT,
        UNSIGNED_INT,
        LONG,
        UNSIGNED_LONG
    }
}
