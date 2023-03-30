package io.sot.lang;

import java.io.IOException;

/**
 * This packet data stream contains a client trace activity ID intended to be used by the server for debugging purposes,
 * to allow correlating the server's processing of the request with the client request.
 * <p>
 * defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/6e9f106b-df6e-4cbe-a6eb-45ceb10c63be">Trace
 * Activity Header</a>
 *
 * @author user
 */
public class TraceActHeader
        extends StreamHeader
{
    private byte[] guidActivityID = null;
    private GenULong activitySequence = null;

    public TraceActHeader()
    {
        super();
    }

    public byte[] getGuidActivityID()
    {
        return guidActivityID;
    }

    public GenULong getActivitySequence()
    {
        return activitySequence;
    }

    public void setGuidActivityID(byte[] guidActivityID)
    {
        this.guidActivityID = guidActivityID;
    }

    public void setActivitySequence(GenULong activitySequence)
    {
        this.activitySequence = activitySequence;
    }

    @Override
    public void read(PacketDataInput data)
            throws PacketRWException, IOException
    {
        this.guidActivityID = data.readBytes(16);
        this.activitySequence = data.readGenULong();
    }
}
