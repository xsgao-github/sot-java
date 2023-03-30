package io.sot.lang;

/**
 * {@code GenLVarByte} is a <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/47acf60e-9c36-4184-b016-a4947263e25f">Variable-Length
 * Data Streams</a> contains up to 2^31-1 bytes (2GB). L_VARBYTE = LONGLEN *BYTE
 * <p>
 *
 * @author user
 */
public class GenLVarByte
        extends GenBinStream
{

    public GenLVarByte(byte[] bytes)
    {
        super(bytes);
    }
}
