package io.sot.lang;

import java.io.IOException;

public interface PacketDataWriter
{
    /**
     * Write data into <code>out</code>, starting at current position.
     *
     * @param out
     * @throws PacketRWException
     * @throws IOException
     */
    void write(PacketDataOutput out)
            throws PacketRWException, IOException;
}
