package io.sot.message;

import io.sot.Session;
import io.sot.lang.GenUShort;
import io.sot.lang.PacketDataOutput;
import io.sot.lang.PacketDataWriter;
import io.sot.lang.PacketRWException;
import io.sot.lang.SqlInterface;

import java.io.IOException;

/**
 * Used to send a response to a login request (LOGIN7) to the client.
 * <p>
 * <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/490e563d-cc6e-4c86-bb95-ef0186b98032">LOGIN_ACK</a>
 *
 * @author user
 */
public class LoginAck
        extends TokenStream
        implements PacketDataWriter
{

    private final SqlInterface sqlInterface;

    public LoginAck(Session session, SqlInterface sqlInterface)
    {
        super(session);
        this.sqlInterface = sqlInterface;
    }

    @Override
    public Token getToken()
    {
        return Token.LOGIN_ACK;
    }

    @Override
    public void write(PacketDataOutput out)
            throws PacketRWException, IOException
    {
        out.write(Token.LOGIN_ACK.getValue());

        // remember starting position
        int lengthOffset = out.getPos();
        out.write(new GenUShort((short) 0));

        out.write(this.sqlInterface.getValue());
        out.write(SqlServer.TDS_VERSION.getValue().swapBytes());
        out.write(SqlServer.PROG_NAME);
        out.write(SqlServer.MAJOR_VERISON);
        out.write(SqlServer.MINOR_VERISON);
        out.write(SqlServer.BUILD.swapBytes());

        // update length
        out.write(lengthOffset, new GenUShort((short) (out.getPos() - lengthOffset - GenUShort.LENGTH)));
    }
}
