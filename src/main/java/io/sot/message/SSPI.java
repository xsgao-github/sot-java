package io.sot.message;

import io.sot.Session;
import io.sot.lang.GenUSVarByte;
import io.sot.lang.PacketDataOutput;
import io.sot.lang.PacketDataWriter;
import io.sot.lang.PacketRWException;

import java.io.IOException;

/**
 * The SSPI token returned during the login process.
 * <p>
 * <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/07e2bb7b-8ba6-445f-89b1-cc76d8bfa9c6">SSPI</a>
 *
 * @author user
 */
public class SSPI
        extends TokenStream
        implements PacketDataWriter
{
    private GenUSVarByte SSPI;

    public SSPI(Session session)
    {
        super(session);
    }

    @Override
    public Token getToken()
    {
        return Token.SSPI;
    }

    public GenUSVarByte getSSPI()
    {
        return SSPI;
    }

    public void setSSPI(GenUSVarByte sspi)
    {
        SSPI = sspi;
    }

    @Override
    public void write(PacketDataOutput out)
            throws PacketRWException, IOException
    {
        out.write(Token.SSPI.getValue());
        out.write(this.getSSPI());
    }
}
