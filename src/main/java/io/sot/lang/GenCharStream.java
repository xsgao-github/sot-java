package io.sot.lang;

/**
 * {@code GenCharStream} class represents character data stream transmitted via TDS.
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/1893c1ff-edce-4465-b194-022039c98928">Unknown
 * Length Data Streams</a> and <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/3f983fde-0509-485a-8c40-a9fa6679a828">Variable-Length
 * Data Streams</a>.
 * <p>
 */
public abstract class GenCharStream
{
    String x;

    public GenCharStream(String value)
    {
        this.x = value;
    }

    public String getString()
    {
        return this.x;
    }

    @Override
    public String toString()
    {
        return x == null ? "null" : x;
    }
}
