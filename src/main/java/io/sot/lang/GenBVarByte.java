package io.sot.lang;

/**
 * {@code GenBVarByte} is a <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/47acf60e-9c36-4184-b016-a4947263e25f">Variable-Length
 * Data Streams</a> contains up to 255 bytes. B_VARBYTE = BYTELEN *BYTE
 * <p>
 *
 * @author user
 */
public class GenBVarByte
        extends GenBinStream
{

    public GenBVarByte(byte[] bytes)
    {
        super(bytes);
    }
}
