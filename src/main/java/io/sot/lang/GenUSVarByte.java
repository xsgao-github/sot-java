package io.sot.lang;

/**
 * {@code GenUSVarByte} is a <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/47acf60e-9c36-4184-b016-a4947263e25f">Variable-Length
 * Data Streams</a> contains up to 65,535 bytes. US_VARBYTE = USHORTLEN *BYTE
 * <p>
 *
 * @author user
 */
public class GenUSVarByte
        extends GenBinStream
{

    public GenUSVarByte(byte[] bytes)
    {
        super(bytes);
    }
}
