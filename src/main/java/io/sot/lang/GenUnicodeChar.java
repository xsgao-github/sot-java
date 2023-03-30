package io.sot.lang;

/**
 * <b>UNICODECHAR:</b> A single Unicode character in UCS-2 (UTF-16) encoding, as specified in Unicode. UNICODECHAR =
 * 2BYTE
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/d2ed21d6-527b-46ac-8035-94f6f68eb9a8">General
 * Rules</a>
 * <p>
 *
 * @author user
 */
public class GenUnicodeChar
        extends GenUShort
{
    public GenUnicodeChar()
    {
        super();
    }

    public GenUnicodeChar(short value)
    {
        super(value);
    }

    public GenUnicodeChar swapBytes()
    {
        return new GenUnicodeChar(_swapBytes());
    }
}
