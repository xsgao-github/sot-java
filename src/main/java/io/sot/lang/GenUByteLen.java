package io.sot.lang;

/**
 * <b>BYTELEN:</b> An unsigned single byte (8-bit) value representing the length of the associated data. The range is 0
 * to 255. BYTELEN = BYTE
 * <p>
 * TDS defines data type as BYTELEN, but since it's always unsigned, we select <code>GenUByteLen</code> as it's name here.
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/d2ed21d6-527b-46ac-8035-94f6f68eb9a8">General
 * Rules</a>
 * <p>
 *
 * @author user
 */
public class GenUByteLen
        extends GenUByte
{
    public GenUByteLen()
    {
        super();
    }

    public GenUByteLen(byte value)
    {
        super(value);
    }

    /**
     * @deprecated nothing to swap.
     */
    @Deprecated
    @Override
    public GenUByteLen swapBytes()
    {
        return new GenUByteLen(this.x);
    }
}
