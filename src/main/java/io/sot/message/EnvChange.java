package io.sot.message;

import io.sot.Session;
import io.sot.lang.Collation;
import io.sot.lang.GenBVarByte;
import io.sot.lang.GenBVarChar;
import io.sot.lang.GenLVarByte;
import io.sot.lang.GenUByte;
import io.sot.lang.GenUShort;
import io.sot.lang.PacketDataOutput;
import io.sot.lang.PacketDataWriter;
import io.sot.lang.PacketRWException;

import java.io.IOException;

/**
 * A notification of an environment change (for example, database, language, and so on).
 * <p>
 * <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/2b3eb7e5-d43d-4d1b-bf4d-76b9e3afc791">ENV_CHANGE</a>
 *
 * @author user
 */
public class EnvChange
        extends TokenStream
        implements PacketDataWriter
{
    private Type type;
    private Object oldValue;
    private Object newValue;

    public EnvChange(Session session, Type type, Object oldValue, Object newValue)
    {
        super(session);
        setValues(type, oldValue, newValue);
    }

    private void setValues(Type type, Object oldValue, Object newValue)
    {
        switch (type) {
            case DATABASE:
            case LANGUAGE:
            case PACKET_SIZE:
                // GenBVarChar -> GenBVarChar
                this.oldValue = castBVarChar(oldValue);
                this.newValue = castBVarChar(newValue);
                break;

            case RT_LOG_SHIPPING:
            case SENDS_USER_INST_PER_LOGIN:
                // %x00 -> GenBVarChar
                this.oldValue = null;
                this.newValue = castBVarChar(newValue);
                break;

            case SQL_COLLATION:
                // GenBVarByte -> GenBVarByte
                this.oldValue = castBVarByte((Collation) oldValue);
                this.newValue = castBVarByte((Collation) newValue);
                break;

            case BEGIN_TX:
            case DEFECT_TX:
                // %x00 -> GenBVarByte
                this.oldValue = null;
                this.newValue = castBVarByte(newValue);
                break;

            case COMMIT_TX:
            case ROLLBACK_TX:
            case ENLIST_DTC_TX:
            case TX_ENDED:
                // GenBVarByte -> %x00
                this.oldValue = castBVarByte(oldValue);
                this.newValue = null;
                break;

            case PROMOTE_TX:
                // %x00 -> GenLVarByte
                this.oldValue = null;
                this.newValue = castLVarByte(oldValue);
                break;

            case RESET_CONN_ACK:
                // %x00 -> %x00
                this.oldValue = null;
                this.newValue = null;
                break;

            case CHARACTER_SET:
            case UNICODE_SORT_LOCAL_ID:
            case UNICODE_SORT_COMP_FLAGS:
                // only sent back to clients running TDS 7.0 or earlier
            case TX_MANAGER_ADDRESS:
                // not used
            case INVALID_0:
            case INVALID_14:
                // invalid
            case SEND_ROUT_INFO:
                // %x00 %x00 -> composite : does not support now
            default:
                // do nothing
                throw new UnsupportedOperationException(String.format("Unsupported EnvChange type %s.", type));
        }

        this.type = type;
    }

    @Override
    public Token getToken()
    {
        return Token.ENV_CHANGE;
    }

    @Override
    public void write(PacketDataOutput out)
            throws PacketRWException, IOException
    {
        out.write(Token.ENV_CHANGE.getValue());

        // remember starting position
        int lengthOffset = out.getPos();
        out.write(new GenUShort((short) 0));

        out.write(new GenUByte((byte) type.ordinal()));
        switch (type) {
            case DATABASE:
            case LANGUAGE:
            case PACKET_SIZE:
                // GenBVarChar -> GenBVarChar
                write(out, (GenBVarChar) newValue);
                write(out, (GenBVarChar) oldValue);
                break;

            case RT_LOG_SHIPPING:
            case SENDS_USER_INST_PER_LOGIN:
                // %x00 -> GenBVarChar
                write(out, (GenBVarChar) newValue);
                out.write((byte) 0);
                break;

            case SQL_COLLATION:
                // GenBVarByte -> GenBVarByte
                write(out, (GenBVarByte) newValue);
                write(out, (GenBVarByte) oldValue);
                break;

            case BEGIN_TX:
            case DEFECT_TX:
                // %x00 -> GenBVarByte
                write(out, (GenBVarByte) newValue);
                out.write((byte) 0);
                break;

            case COMMIT_TX:
            case ROLLBACK_TX:
            case ENLIST_DTC_TX:
            case TX_ENDED:
                // GenBVarByte -> %x00
                out.write((byte) 0);
                write(out, (GenBVarChar) oldValue);
                break;

            case PROMOTE_TX:
                // %x00 -> GenLVarByte
                write(out, (GenLVarByte) newValue);
                out.write((byte) 0);
                break;

            case RESET_CONN_ACK:
                // %x00 -> %x00
                out.write((byte) 0);
                out.write((byte) 0);
                break;

            case CHARACTER_SET:
            case UNICODE_SORT_LOCAL_ID:
            case UNICODE_SORT_COMP_FLAGS:
                // only sent back to clients running TDS 7.0 or earlier
            case TX_MANAGER_ADDRESS:
                // not used
            case INVALID_0:
            case INVALID_14:
                // invalid
            case SEND_ROUT_INFO:
                // %x00 %x00 -> composite : does not support now
            default:
                // do nothing
                throw new UnsupportedOperationException(String.format("Unsupported EnvChange type %s.", type));
        }

        // update length
        out.write(lengthOffset, new GenUShort((short) (out.getPos() - lengthOffset - GenUShort.LENGTH)));
    }

    private GenBVarChar castBVarChar(Object x)
    {
        if (x == null) {
            return null;
        }
        return x instanceof GenBVarChar ? (GenBVarChar) x : new GenBVarChar((String) x);
    }

    private GenBVarByte castBVarByte(Object x)
    {
        if (x == null) {
            return null;
        }
        return x instanceof GenBVarByte ? (GenBVarByte) x : new GenBVarByte((byte[]) x);
    }

    private GenBVarByte castBVarByte(Collation x)
    {
        if (x == null) {
            return null;
        }
        return new GenBVarByte(x.getBytes());
    }

    private GenLVarByte castLVarByte(Object x)
    {
        if (x == null) {
            return null;
        }
        return x instanceof GenLVarByte ? (GenLVarByte) x : new GenLVarByte((byte[]) x);
    }

    private void write(PacketDataOutput out, GenBVarChar x)
            throws PacketRWException, IOException
    {
        if (x == null) {
            out.write((byte) 0);
        }
        else {
            out.write(x);
        }
    }

    private void write(PacketDataOutput out, GenBVarByte x)
            throws PacketRWException, IOException
    {
        if (x == null) {
            out.write((byte) 0);
        }
        else {
            out.write(x);
        }
    }

    private void write(PacketDataOutput out, GenLVarByte x)
            throws PacketRWException, IOException
    {
        if (x == null) {
            out.write((byte) 0);
        }
        else {
            out.write(x);
        }
    }

    public enum Type
    {
        INVALID_0,
        DATABASE,
        LANGUAGE,
        CHARACTER_SET,
        PACKET_SIZE,
        UNICODE_SORT_LOCAL_ID,
        UNICODE_SORT_COMP_FLAGS,
        SQL_COLLATION,
        BEGIN_TX,
        COMMIT_TX,
        ROLLBACK_TX,
        ENLIST_DTC_TX,
        DEFECT_TX,
        RT_LOG_SHIPPING,
        INVALID_14,
        PROMOTE_TX,
        TX_MANAGER_ADDRESS,
        TX_ENDED,
        RESET_CONN_ACK,
        SENDS_USER_INST_PER_LOGIN,
        SEND_ROUT_INFO
    }
}
