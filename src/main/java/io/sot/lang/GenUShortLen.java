package io.sot.lang;

/**
 * <b>USHORTLEN:</b> An unsigned 2-byte (16-bit) value representing the length of the associated data. The range is 0 to
 * 65535. USHORTLEN = 2BYTE
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/d2ed21d6-527b-46ac-8035-94f6f68eb9a8">General
 * Rules</a>
 * <p>
 *
 * @author user
 */
public class GenUShortLen
        extends GenUShort
{
    public GenUShortLen()
    {
        super();
    }

    public GenUShortLen(short value)
    {
        super(value);
    }

    public GenUShortLen swapBytes()
    {
        return new GenUShortLen(_swapBytes());
    }
}
