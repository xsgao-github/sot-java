package io.sot.lang;

/**
 * {@code GenBVarChar} is a <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/47acf60e-9c36-4184-b016-a4947263e25f">Variable-Length
 * Data Streams</a> contains up to 255 chars. B_VARCHAR = BYTELEN *CHAR
 * <p>
 *
 * @author user
 */
public class GenBVarChar
        extends GenCharStream
{

    public GenBVarChar(String value)
    {
        super(value);
    }
}
