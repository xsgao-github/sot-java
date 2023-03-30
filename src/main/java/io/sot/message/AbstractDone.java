package io.sot.message;

import io.sot.Session;
import io.sot.lang.GenULongLong;
import io.sot.lang.GenUShort;
import io.sot.lang.PacketDataOutput;
import io.sot.lang.PacketDataWriter;
import io.sot.lang.PacketRWException;

import java.io.IOException;

/**
 * <p>
 * Indicates the completion status of a SQL statement within SQL Batch or within a stored procedure. Sub classes are
 * defined in {@link Done},
 * </p>
 *
 * @author user
 */
public abstract class AbstractDone
        extends TokenStream
        implements PacketDataWriter
{
    public static final short DONE_FINAL = 0x00;
    public static final short DONE_MORE = 0x01;
    public static final short DONE_ERROR = 0x02;
    public static final short DONE_INXACT = 0x04;
    public static final short DONE_COUNT = 0x10;
    public static final short DONE_ATTN = 0x20;
    public static final short DONE_SRVERROR = 0x100;

    private GenUShort status = new GenUShort((short) 0);
    private Command curCmd = Command.UNDEFINED;
    private GenULongLong rowCount = new GenULongLong(0);

    public AbstractDone(Session session)
    {
        super(session);
    }

    public GenUShort getStatus()
    {
        return this.status;
    }

    public void setStatus(GenUShort status)
    {
        this.status = status;
    }

    public void setStatusFlag(short flag)
    {
        this.status.setShort((short) (this.status.intValue() | flag));
    }

    public void clearStatusFlag(short flag)
    {
        this.status.setShort((short) (this.status.intValue() & (~flag)));
    }

    public Command getCurCmd()
    {
        return curCmd;
    }

    public void setCurCmd(Command curCmd)
    {
        this.curCmd = curCmd;
    }

    public GenULongLong getRowCount()
    {
        return rowCount;
    }

    public void setRowCount(GenULongLong rowCount)
    {
        this.rowCount = rowCount;
    }

    @Override
    public void write(PacketDataOutput out)
            throws PacketRWException, IOException
    {
        out.write(getToken().getValue());
        out.write(status);
        out.write(curCmd.getValue());
        out.write(rowCount);
    }

    public static AbstractDone createInstance(Session session, Token token)
    {
        switch (token) {
            case DONE:
                return new Done(session);
            case DONE_IN_PROC:
                return new DoneInProc(session);
            case DONE_PROC:
                return new DoneProc(session);
            default:
                throw new IllegalArgumentException(String.format("Unexpected token %s, must be one of: %s, %s or %s.", token,
                        Token.DONE, Token.DONE_IN_PROC, Token.DONE_PROC));
        }
    }

    public enum Command
    {
        // used in DONE or DONE_INPROC
        SELECT((short) 0x00c1),
        SET((short) 0x00f9),
        // used in DONE_PROC
        SP((short) 0x00e0),
        UNDEFINED((short) 0);

        final GenUShort value;

        Command(short value)
        {
            this.value = new GenUShort(value);
        }

        public GenUShort getValue()
        {
            return this.value;
        }
    }
}
