package io.sot.lang;

/**
 * <b>ULONGLEN:</b> An unsigned 4-byte (32-bit) value representing the length of the associated data. The range is 0 to
 * (2^32)-1. ULONGLEN = 4BYTE
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/d2ed21d6-527b-46ac-8035-94f6f68eb9a8">General
 * Rules</a>
 * <p>
 *
 * @author user
 */
public class GenULongLen
        extends GenULong
{
    public GenULongLen()
    {
        super();
    }

    public GenULongLen(int value)
    {
        super(value);
    }

    @Override
    public GenULongLen swapBytes()
    {
        return new GenULongLen(_swapBytes());
    }
}
