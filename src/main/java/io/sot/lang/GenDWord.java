package io.sot.lang;

/**
 * <b>DWORD:</b> An unsigned 4-byte (32-bit) value. The range when used as a numeric value is 0 to (2^32)-1. DWORD =
 * 32BIT
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/d2ed21d6-527b-46ac-8035-94f6f68eb9a8">General
 * Rules</a>
 * <p>
 *
 * @author user
 */
public class GenDWord
        extends GenULong
{
    public GenDWord()
    {
        super();
    }

    public GenDWord(int value)
    {
        super(value);
    }

    @Override
    public GenDWord swapBytes()
    {
        return new GenDWord(_swapBytes());
    }
}
