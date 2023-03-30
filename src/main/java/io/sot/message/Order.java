package io.sot.message;

import io.sot.Session;
import io.sot.lang.GenUShort;
import io.sot.lang.PacketDataOutput;
import io.sot.lang.PacketDataWriter;
import io.sot.lang.PacketRWException;

import java.io.IOException;
import java.util.List;

/**
 * Used to inform the client by which columns the data is ordered.
 * <p>
 * <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/252759be-9d74-4435-809d-d55dd860ea78">ORDER</a>
 *
 * @author user
 */
public class Order
        extends TokenStream
        implements PacketDataWriter
{
    private final List<GenUShort> colNums;

    public Order(Session session, List<GenUShort> colNums)
    {
        super(session);
        this.colNums = colNums;
    }

    @Override
    public Token getToken()
    {
        return Token.ORDER;
    }

    @Override
    public void write(PacketDataOutput out)
            throws PacketRWException, IOException
    {
        if (this.colNums == null || this.colNums.size() == 0) {
            return;
        }

        out.write(Token.ORDER.getValue());
        out.write((byte) this.colNums.size());
        for (GenUShort col : this.colNums) {
            out.write(col);
        }
    }
}
