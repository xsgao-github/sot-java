package io.sot.message;

import io.sot.Session;
import io.sot.lang.PacketDataInput;
import io.sot.lang.PacketDataReader;
import io.sot.lang.PacketRWException;

/**
 * The client can interrupt and cancel the current request by sending an Attention message.
 * <p>
 * <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/dc28579f-49b1-4a78-9c5f-63fbda002d2e">Attention</a>
 *
 * @author user
 */
public class Attention
        implements PacketDataReader
{
    @SuppressWarnings("unused")
    private final Session session;

    public Attention(Session session)
    {
        this.session = session;
    }

    @Override
    public void read(PacketDataInput in)
            throws PacketRWException
    {
        // no data
    }
}
