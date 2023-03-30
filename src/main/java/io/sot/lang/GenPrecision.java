package io.sot.lang;

/**
 * <b>PRECISION:</b> An unsigned single byte (8-bit) value representing the precision of a numeric number. PRECISION =
 * 8BIT
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/d2ed21d6-527b-46ac-8035-94f6f68eb9a8">General
 * Rules</a>
 * <p>
 *
 * @author user
 */
public class GenPrecision
        extends GenUByte
{
    public GenPrecision()
    {
        super();
    }

    public GenPrecision(byte value)
    {
        super(value);
    }

    /**
     * @deprecated nothing to swap.
     */
    @Deprecated
    @Override
    public GenPrecision swapBytes()
    {
        return new GenPrecision(this.x);
    }
}
