package io.sot.message;

import io.sot.Session;
import io.sot.lang.GenDWord;
import io.sot.lang.GenLVarByte;
import io.sot.lang.PacketDataInput;
import io.sot.lang.PacketDataReader;
import io.sot.lang.PacketRWException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Defines the authentication rules for use between client and server.
 * <p>
 * <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/773a62b6-ee89-4c02-9e5e-344882630aac">LOGIN7</a>
 *
 * @author user
 */
public class FedAuthToken
        implements PacketDataReader
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LogManager.getLogger(FedAuthToken.class);

    @SuppressWarnings("unused")
    private final Session session;

    private GenLVarByte token;
    private byte[] nonce;

    public FedAuthToken(Session session)
    {
        this.session = session;
    }

    @Override
    public void read(PacketDataInput data)
            throws PacketRWException, IOException
    {
        /*
         * Login packet header
         */
        // The length of a LOGIN7 stream MUST NOT be longer than 128K-1(byte) bytes.
        long packetLength = data.readGenDWord().longValue();
        this.token = data.readGenLVarByte();
        int remainLen = (int) (packetLength - GenDWord.LENGTH - token.getBytes().length);
        if (remainLen == 0) {
            // no nonce
        }
        else if (remainLen == 32) {
            this.nonce = data.readBytes(32);
        }
        else {
            throw new PacketRWException("There are %d bytes after FedAuthToken.",
                    remainLen);
        }
    }

    public GenLVarByte getToken()
    {
        return token;
    }

    public byte[] getNonce()
    {
        return nonce;
    }
}
