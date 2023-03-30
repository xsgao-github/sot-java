package io.sot.lang;

import java.io.IOException;

/**
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/3d29e8dc-218a-42c6-9ba4-947ebca9fd7e">Collation
 * Rule Definition</a>
 * <p>
 *
 * @author user
 */
public class Collation
        implements PacketDataReader, PacketDataWriter
{
    public static byte LENGTH = 5;

    protected LCID lcid;
    protected boolean ignoreCase;
    protected boolean ignoreAccent;
    protected boolean ignoreWidth;
    protected boolean ignoreKana;
    protected boolean binary;
    protected boolean binary2;
    protected boolean utf8;
    protected byte version;
    protected SortId sortId;

    public Collation()
    {
    }

    public LCID getLcid()
    {
        return lcid;
    }

    public void setLcid(LCID lcid)
    {
        this.lcid = lcid;
    }

    public boolean isIgnoreCase()
    {
        return ignoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase)
    {
        this.ignoreCase = ignoreCase;
    }

    public boolean isIgnoreAccent()
    {
        return ignoreAccent;
    }

    public void setIgnoreAccent(boolean ignoreAccent)
    {
        this.ignoreAccent = ignoreAccent;
    }

    public boolean isIgnoreWidth()
    {
        return ignoreWidth;
    }

    public void setIgnoreWidth(boolean ignoreWidth)
    {
        this.ignoreWidth = ignoreWidth;
    }

    public boolean isIgnoreKana()
    {
        return ignoreKana;
    }

    public void setIgnoreKana(boolean ignoreKana)
    {
        this.ignoreKana = ignoreKana;
    }

    public boolean isBinary()
    {
        return binary;
    }

    public void setBinary(boolean binary)
    {
        this.binary = binary;
    }

    public boolean isBinary2()
    {
        return binary2;
    }

    public void setBinary2(boolean binary2)
    {
        this.binary2 = binary2;
    }

    public boolean isUtf8()
    {
        return utf8;
    }

    public void setUtf8(boolean utf8)
    {
        this.utf8 = utf8;
    }

    public byte getVersion()
    {
        return version;
    }

    public void setVersion(byte version)
    {
        this.version = version;
    }

    public SortId getSortId()
    {
        return sortId;
    }

    public void setSortId(SortId sortId)
    {
        this.sortId = sortId;
    }

    public byte[] getBytes()
    {
        int flags = 0;
        flags |= (this.ignoreCase ? 0x01 : 0x00);
        flags |= (this.ignoreAccent ? 0x02 : 0x00);
        flags |= (this.ignoreWidth ? 0x04 : 0x00);
        flags |= (this.ignoreKana ? 0x08 : 0x00);
        flags |= (this.binary ? 0x10 : 0x00);
        flags |= (this.binary2 ? 0x20 : 0x00);
        flags |= (this.utf8 ? 0x40 : 0x00);
        flags |= (this.version << 7);

        byte[] b = new byte[5];
        int i = (int) this.lcid.getCode().longValue() | (flags << 20);
        System.arraycopy(PacketDataOutput.toBytes(i), 0, b, 0, 4);
        b[4] = this.sortId.getValue().x;

        return b;
    }

    @Override
    public void read(PacketDataInput in)
            throws PacketRWException, IOException
    {
        int value = (int) in.readGenDWord().longValue();

        this.lcid = LCID.valueOf(new GenDWord(value & 0x000FFFFF));
        int flags = (value >> 20) & 0xFFF;
        this.ignoreCase = ((flags & 0x01) == 1);
        this.ignoreAccent = ((flags & 0x02) == 1);
        this.ignoreWidth = ((flags & 0x04) == 1);
        this.ignoreKana = ((flags & 0x08) == 1);
        this.binary = ((flags & 0x10) == 1);
        this.binary2 = ((flags & 0x20) == 1);
        this.utf8 = ((flags & 0x40) == 1);
        this.version = (byte) (flags & 0x0780);

        this.sortId = SortId.valueOf(in.readGenUByte());
    }

    @Override
    public void write(PacketDataOutput out)
            throws PacketRWException, IOException
    {
        out.write(getBytes());
    }
}
