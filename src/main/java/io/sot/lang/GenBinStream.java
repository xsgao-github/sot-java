package io.sot.lang;

/**
 * {@code GenBinStream} class represents binnary data stream transmitted via TDS.
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/1893c1ff-edce-4465-b194-022039c98928">Unknown
 * Length Data Streams</a> and <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/3f983fde-0509-485a-8c40-a9fa6679a828">Variable-Length
 * Data Streams</a>.
 * <p>
 */
public abstract class GenBinStream
{
    byte[] x;

    public GenBinStream(byte[] bytes)
    {
        this.x = bytes;
    }

    public byte[] getBytes()
    {
        return x;
    }
}
