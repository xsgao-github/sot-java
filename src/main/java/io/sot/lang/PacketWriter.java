package io.sot.lang;

import com.google.common.io.BaseEncoding;
import io.sot.Session;
import io.sot.Socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * {@code PacketWriter} writes server response to packet(s).
 *
 * @author user
 */
public class PacketWriter
{
    private static final Logger LOG = LogManager.getLogger(PacketWriter.class);

    private final Session session;

    private final Socket socket;

    private final PacketHeader header;

    private final PacketDataOutput data;

    private final int bufferSize;

    public PacketWriter(Session session, PacketHeader header)
    {
        this.session = session;
        socket = this.session.getSocket();
        this.header = header;
        this.bufferSize = this.session.getPacketSize() - PacketHeader.LENGTH;
        data = new PacketDataOutput(this, this.bufferSize);
    }

    public PacketHeader getHeader()
    {
        return header;
    }

    /**
     * Triggered by {@link #data} when its current data block is full, before
     * allocating a new block and write the reset bytes to new block.
     * <p>
     *
     * @throws IOException
     */
    public void beforeNewPacket()
            throws IOException
    {
        // write data
        header.setStatus(new GenUByte(PacketHeader.StatusFlag.NORMAL.getValue()));
        header.setLength(new GenUShort((short) (PacketHeader.LENGTH + data.size())));

        if (LOG.isTraceEnabled()) {
            logPacket(new byte[][] {header.toByteArray(), data.getData()});
        }

        socket.writePacket(header, data.getData());
    }

    public void flush()
            throws IOException
    {
        // write last part data
        header.setStatus(new GenUByte(PacketHeader.StatusFlag.EOM.getValue()));
        header.setLength(new GenUShort((short) (PacketHeader.LENGTH + data.size())));

        if (LOG.isTraceEnabled()) {
            logPacket(new byte[][] {header.toByteArray(), data.getData()});
        }

        socket.writePacket(header, data.getData());

        // flush
        socket.flush(header);
    }

    public void write(PacketDataWriter w)
            throws PacketRWException, IOException
    {
        data.write(w);
    }

    private void logPacket(byte[][] b)
    {
        LOG.trace(String.format("Write %s message, packet %s:\n", header.getType(), header.getPacketID())
                + BaseEncoding.base16().encode(b[0]) + "\n" + BaseEncoding.base16().encode(b[1]));
    }
}
