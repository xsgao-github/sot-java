package io.sot.message;

import io.sot.Session;
import io.sot.lang.GenUByte;
import io.sot.lang.GenULong;
import io.sot.lang.PacketDataOutput;
import io.sot.lang.PacketDataWriter;
import io.sot.lang.PacketRWException;

import java.io.IOException;

public class FedAuthInfo
        extends TokenStream
        implements PacketDataWriter
{
    private String stsUrl;

    private String spn;

    public FedAuthInfo(Session session)
    {
        super(session);
    }

    public byte[] toBytes()
    {
        byte[] stsUrlBytes = PacketDataOutput.toBytes(stsUrl);
        byte[] spnBytes = PacketDataOutput.toBytes(spn);

        int optOffset = 0;
        int dataOffset = GenULong.LENGTH + (GenUByte.LENGTH + GenULong.LENGTH * 2) * 2;
        byte[] b = new byte[dataOffset + stsUrlBytes.length + spnBytes.length];

        // CountOfInfoIDs
        System.arraycopy(PacketDataOutput.toBytes(2), 0, b, optOffset, GenULong.LENGTH);
        optOffset += GenULong.LENGTH;

        // spn
        b[optOffset] = 0x02;
        optOffset += GenUByte.LENGTH;
        System.arraycopy(PacketDataOutput.toBytes(spnBytes.length), 0, b, optOffset, GenULong.LENGTH);
        optOffset += GenULong.LENGTH;
        System.arraycopy(PacketDataOutput.toBytes(dataOffset), 0, b, optOffset, GenULong.LENGTH);
        optOffset += GenULong.LENGTH;
        System.arraycopy(spnBytes, 0, b, dataOffset, spnBytes.length);
        dataOffset += spnBytes.length;

        // stsUrl
        b[optOffset] = 0x01;
        optOffset += GenUByte.LENGTH;
        System.arraycopy(PacketDataOutput.toBytes(stsUrlBytes.length), 0, b, optOffset, GenULong.LENGTH);
        optOffset += GenULong.LENGTH;
        System.arraycopy(PacketDataOutput.toBytes(dataOffset), 0, b, optOffset, GenULong.LENGTH);
        optOffset += GenULong.LENGTH;
        System.arraycopy(stsUrlBytes, 0, b, dataOffset, stsUrlBytes.length);
        dataOffset += stsUrlBytes.length;

        return b;
    }

    @Override
    public Token getToken()
    {
        return Token.FED_AUTH_INFO;
    }

    public String getStsUrl()
    {
        return stsUrl;
    }

    public void setStsUrl(String stsUrl)
    {
        this.stsUrl = stsUrl;
    }

    public String getSpn()
    {
        return spn;
    }

    public void setSpn(String spn)
    {
        this.spn = spn;
    }

    @Override
    public void write(PacketDataOutput out)
            throws PacketRWException, IOException
    {
        out.write(this.getToken().getValue());
        byte[] b = this.toBytes();
        out.write(b.length);
        out.write(b);
    }
}
