package io.sot.lang;

import java.io.IOException;

public interface PacketDataReader
{

    /**
     * Read data from <code>in</code>.
     *
     * @param in
     * @throws PacketRWException
     * @throws IOException
     */
    void read(PacketDataInput in)
            throws PacketRWException, IOException;
}
