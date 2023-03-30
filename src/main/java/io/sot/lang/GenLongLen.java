package io.sot.lang;

/**
 * <b>LONGLEN:</b> A signed 4-byte (32-bit) value representing the length of the associated data. The range is -(2^31)
 * to (2^31)-1. LONGLEN = 4BYTE
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/d2ed21d6-527b-46ac-8035-94f6f68eb9a8">General
 * Rules</a>
 * <p>
 *
 * @author user
 */
public class GenLongLen
        extends GenLong
{
    public GenLongLen()
    {
        super();
    }

    public GenLongLen(int value)
    {
        super(value);
    }

    @Override
    public GenLongLen swapBytes()
    {
        return new GenLongLen(_swapBytes());
    }
}
