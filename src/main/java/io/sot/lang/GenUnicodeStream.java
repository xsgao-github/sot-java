package io.sot.lang;

/**
 * {@linkplain GenUnicodeStream} is a <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/1893c1ff-edce-4465-b194-022039c98928">Unknown
 * Length Data Streams</a> UNICODESTREAM = *(2BYTE)
 * <p>
 *
 * @author user
 */
public class GenUnicodeStream
        extends GenCharStream
{

    public GenUnicodeStream(String value)
    {
        super(value);
    }
}
