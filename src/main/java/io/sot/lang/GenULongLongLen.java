package io.sot.lang;

/**
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/d2ed21d6-527b-46ac-8035-94f6f68eb9a8">General
 * Rules</a>
 * <p>
 * <b>ULONGLONGLEN:</b> An unsigned 8-byte (64-bit) value representing the length of the associated data. The range is 0
 * to (2^64)-1.
 * <p>
 * ULONGLONGLEN = 8BYTE
 *
 * @author user
 */
public class GenULongLongLen
        extends GenULongLong
{
    public GenULongLongLen()
    {
        super();
    }

    public GenULongLongLen(long value)
    {
        super(value);
    }

    @Override
    public GenULongLongLen swapBytes()
    {
        return new GenULongLongLen(_swapBytes());
    }
}
