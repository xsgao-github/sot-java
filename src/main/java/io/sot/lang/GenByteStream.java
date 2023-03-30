package io.sot.lang;

/**
 * {@code GenByteStream} is a <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/1893c1ff-edce-4465-b194-022039c98928">Unknown
 * Length Data Streams</a>. BYTESTREAM = *BYTE
 * <p>
 *
 * @author user
 */
public class GenByteStream
        extends GenBinStream
{

    public GenByteStream(byte[] bytes)
    {
        super(bytes);
    }
}
