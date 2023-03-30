package io.sot.message;

import io.sot.Session;
import io.sot.lang.GenLong;
import io.sot.lang.PacketDataOutput;
import io.sot.lang.PacketDataWriter;
import io.sot.lang.PacketRWException;

import java.io.IOException;

/**
 * <p>
 * Used to send the status value of an RPC to the client. The server also uses this token to send the result status
 * value of a T-SQL EXEC query.
 * </p>
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/c719f199-e71b-4187-90b9-94f78bd1870e">RETURNSTATUS</a>
 * </p>
 *
 * @author user
 */
public class ReturnStatus
        extends TokenStream
        implements PacketDataWriter
{
    private GenLong value;

    public ReturnStatus(Session session)
    {
        super(session);
        value = new GenLong(0);
    }

    public GenLong getValue()
    {
        return value;
    }

    public void setValue(GenLong value)
    {
        this.value = value;
    }

    @Override
    public Token getToken()
    {
        return Token.RETURN_STATUS;
    }

    @Override
    public void write(PacketDataOutput out)
            throws PacketRWException, IOException
    {
        out.write(getToken().getValue());
        out.write(value);
    }
}
