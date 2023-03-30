package io.sot.lang;

/**
 * <b>USHORTCHARBINLEN:</b> An unsigned 2-byte (16-bit) value representing the length of the associated character or
 * binary data. The range is 0 to 8000. USHORTCHARBINLEN = 2BYTE
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/d2ed21d6-527b-46ac-8035-94f6f68eb9a8">General
 * Rules</a>
 * <p>
 *
 * @author user
 */
public class GenUShortCharBinLen
        extends GenUShort
{
    public GenUShortCharBinLen()
    {
        super();
    }

    public GenUShortCharBinLen(short value)
    {
        super(value);
    }

    public GenUShortCharBinLen swapBytes()
    {
        return new GenUShortCharBinLen(_swapBytes());
    }
}
