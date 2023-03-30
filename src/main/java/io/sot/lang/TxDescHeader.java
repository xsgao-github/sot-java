package io.sot.lang;

import java.io.IOException;

/**
 * This packet data stream contains information regarding transaction descriptor and number of outstanding requests as
 * they apply to Multiple Active Result Sets (MARS) [MSDN-MARS].
 * <p>
 * defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/4257dd95-ef6c-4621-b75d-270738487d68">Transaction
 * Descriptor Header</a>
 *
 * @author user
 */
public class TxDescHeader
        extends StreamHeader
{
    private GenULongLong txDescriptor = null;
    private GenDWord requestCount = null;

    public TxDescHeader()
    {
        super();
    }

    public GenULongLong getTxDescriptor()
    {
        return txDescriptor;
    }

    public void setTxDescriptor(GenULongLong txDescriptor)
    {
        this.txDescriptor = txDescriptor;
    }

    public GenDWord getRequestCount()
    {
        return requestCount;
    }

    public void setRequestCount(GenDWord requestCount)
    {
        this.requestCount = requestCount;
    }

    @Override
    public void read(PacketDataInput data)
            throws PacketRWException, IOException
    {
        this.txDescriptor = data.readGenULongLong();
        this.requestCount = data.readGenDWord();
    }
}
