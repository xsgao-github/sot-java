package io.sot.lang;

/**
 * <p>
 * <b>UCHAR:</b> An unsigned single byte (8-bit) value representing a character. The range is 0 to 255. UCHAR = BYTE
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/d2ed21d6-527b-46ac-8035-94f6f68eb9a8">General
 * Rules</a>
 *
 * @author user
 */
public class GenUChar
        extends GenUByte
{
    public GenUChar()
    {
        super();
    }

    public GenUChar(byte value)
    {
        super(value);
    }

    /**
     * @deprecated nothing to swap.
     */
    @Deprecated
    @Override
    public GenUChar swapBytes()
    {
        return new GenUChar(this.x);
    }
}
