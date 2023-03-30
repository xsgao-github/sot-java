package io.sot.lang;

/**
 * {@linkplain GenUSVarChar} is a <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/47acf60e-9c36-4184-b016-a4947263e25f">Variable-Length
 * Data Streams</a> contains up to 65,535 chars. US_VARCHAR = USHORTLEN *CHAR
 * <p>
 *
 * @author user
 */
public class GenUSVarChar
        extends GenCharStream
{

    public GenUSVarChar(String value)
    {
        super(value);
    }

    @Override
    public String toString()
    {
        return this.x == null ? "null" : this.x;
    }
}
