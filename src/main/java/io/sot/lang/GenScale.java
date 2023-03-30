package io.sot.lang;

/**
 * <b>SCALE:</b> An unsigned single byte (8-bit) value representing the scale of a numeric number. SCALE = 8BIT
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/d2ed21d6-527b-46ac-8035-94f6f68eb9a8">General
 * Rules</a>
 * <p>
 *
 * @author user
 */
public class GenScale
        extends GenUByte
{
    public GenScale()
    {
        super();
    }

    public GenScale(byte value)
    {
        super(value);
    }

    /**
     * @deprecated nothing to swap.
     */
    @Deprecated
    @Override
    public GenScale swapBytes()
    {
        return new GenScale(this.x);
    }
}
