package io.sot.lang;

import com.google.common.io.BaseEncoding;
import io.sot.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * {@code PacketReader} reads packet(s) out of a client request.
 *
 * @author user
 */
public class PacketReader
{
    private static final Logger LOG = LogManager.getLogger(PacketReader.class);

    private static final int MAX_SIZE = 128 * 1024;

    private final Session session;

    private final PacketHeader header;

    private final PacketDataInput data;

    /**
     * @param session
     * @throws PacketRWException
     * @throws IOException
     */
    public PacketReader(Session session)
            throws PacketRWException, IOException
    {
        this.session = session;
        header = new PacketHeader();
        data = new PacketDataInput(this, readAllPackets());
    }

    /**
     * Get {@link #header}.
     *
     * @return
     * @throws PacketRWException
     */
    public PacketHeader getHeader()
    {
        return header;
    }

    public <T extends PacketDataReader> T read(T t)
            throws PacketRWException, IOException
    {
        data.read(t);
        return t;
    }

    public byte[] readBytes(int i)
            throws PacketRWException, IOException
    {
        return data.readBytes(i);
    }

    private byte[] readAllPackets()
            throws PacketRWException, IOException
    {
        ByteBuffer bb = ByteBuffer.allocate(MAX_SIZE);

        while (true) {
            // read data
            byte[][] b = this.session.getSocket().nextPacket();

            if (LOG.isTraceEnabled()) {
                logPacket(b);
            }

            header.read(b[0]);
            if (bb.position() + b[1].length > MAX_SIZE) {
                throw new PacketRWException(
                        String.format("Message size %d is greater than %d.", bb.position() + b[1].length, MAX_SIZE));
            }
            else {
                bb.put(b[1]);
            }

            if ((header.getStatus().shortValue() & PacketHeader.StatusFlag.EOM.getValue()) != 0) {
                // EOM - no more data
                if (LOG.isTraceEnabled()) {
                    LOG.trace("End of message (EOM), no more packet.");
                }

                // return byte array
                byte[] ret = new byte[bb.position()];
                bb.rewind();
                bb.get(ret);
                return ret;
            }
        }
    }

    private void logPacket(byte[][] b)
    {
        LOG.trace(String.format("Read %s message, packet %s:\n", header.getType(), header.getPacketID())
                + BaseEncoding.base16().encode(b[0]) + (b[1].length == 0 ? "" : "\n" + BaseEncoding.base16().encode(b[1])));
    }
}
