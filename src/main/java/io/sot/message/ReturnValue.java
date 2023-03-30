package io.sot.message;

import io.sot.Session;
import io.sot.lang.EnumValueLookupException;
import io.sot.lang.GenBVarChar;
import io.sot.lang.GenULong;
import io.sot.lang.GenUShort;
import io.sot.lang.PacketDataOutput;
import io.sot.lang.PacketDataWriter;
import io.sot.lang.PacketRWException;
import io.sot.lang.SqlDataValue;
import io.sot.lang.TypeInfo;

import java.io.IOException;

/**
 * <p>
 * Used to send the return value of an RPC to the client. When an RPC is executed, the associated parameters might be
 * defined as input or output (or "return") parameters. This token is used to send a description of the return parameter
 * to the client. This token is also used to describe the value returned by a UDF when executed as an RPC.
 * </p>
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/7091f6f6-b83d-4ed2-afeb-ba5013dfb18f">RETURNVALUE</a>
 * </p>
 *
 * @author user
 */
public class ReturnValue
        extends TokenStream
        implements PacketDataWriter
{
    private GenUShort paramOrdinal;

    private GenBVarChar paramName;

    private Status status = Status.OUTPUT;

    private GenULong userType = new GenULong(0);

    // flags
    private boolean nullable = false;
    private boolean caseSen = false;
    private Updateable updateable = Updateable.READ_ONLY;
    private boolean identity = false;
    private boolean computed = false;
    // usReservedODBC = 2BIT; (only exists in TDS 7.3.A and below)
    private boolean fixedLenCLRType = false;
    // usReserved2 = 2BIT
    // fEncrypted = BIT; (introduced in TDS 7.4)
    // usReserved3 = 4BIT

    private TypeInfo typeInfo;

    // CryptoMetadata - don't support encryption

    private SqlDataValue value;

    public ReturnValue(Session session)
    {
        super(session);
    }

    public void setParamOrdinal(GenUShort paramOrdinal)
    {
        this.paramOrdinal = paramOrdinal;
    }

    public void setParamName(GenBVarChar paramName)
    {
        this.paramName = paramName;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    public void setUserType(GenULong userType)
    {
        this.userType = userType;
    }

    public void setNullable(boolean nullable)
    {
        this.nullable = nullable;
    }

    public void setCaseSen(boolean caseSen)
    {
        this.caseSen = caseSen;
    }

    public void setUpdateable(Updateable updateable)
    {
        this.updateable = updateable;
    }

    public void setIdentity(boolean identity)
    {
        this.identity = identity;
    }

    public void setComputed(boolean computed)
    {
        this.computed = computed;
    }

    public void setFixedLenCLRType(boolean fixedLenCLRType)
    {
        this.fixedLenCLRType = fixedLenCLRType;
    }

    public void setTypeInfo(TypeInfo typeInfo)
    {
        this.typeInfo = typeInfo;
    }

    public void setValue(SqlDataValue value)
    {
        this.value = value;
    }

    @Override
    public Token getToken()
    {
        return Token.RETURN_VALUE;
    }

    @Override
    public void write(PacketDataOutput out)
            throws PacketRWException, IOException
    {
        // token
        out.write(getToken().getValue());

        // ordinal, name, status, userType
        out.write(paramOrdinal);
        out.write(paramName);
        out.write(status.value);
        out.write(userType);

        // Flags
        short flags = (short) 0;
        // low byte
        flags |= (nullable ? 1 : 0);
        flags |= ((caseSen ? 1 : 0) << 1);
        flags |= (updateable.value << 2);
        flags |= ((identity ? 1 : 0) << 4);
        flags |= ((computed ? 1 : 0) << 5);
        // usReservedODBC
        // high byte
        flags |= ((fixedLenCLRType ? 1 : 0) << 8);
        // usReserved2
        // fEncrypted
        // usReserved3
        // write
        out.write(flags);

        // TYPE_INFO
        out.write(typeInfo);

        // skip [CryptoMetaData]

        // value
        out.write(typeInfo, value);
    }

    public enum Status
    {
        OUTPUT((byte) 0x01),
        UDF((byte) 0x02);

        private final byte value;

        Status(byte value)
        {
            this.value = value;
        }

        public static Status valueOf(byte value)
                throws EnumValueLookupException
        {
            switch (value) {
                case 0x01:
                    return OUTPUT;
                case 0x02:
                    return UDF;
                default:
                    throw new EnumValueLookupException(ReturnValue.class, ReturnValue.Status.class, value);
            }
        }
    }

    public enum Updateable
    {
        READ_ONLY((byte) 0x00),
        READ_WRITE((byte) 0x01),
        UNUSED((byte) 0x02);

        private final byte value;

        Updateable(byte value)
        {
            this.value = value;
        }

        public static Updateable valueOf(byte value)
                throws EnumValueLookupException
        {
            switch (value) {
                case 0x00:
                    return READ_ONLY;
                case 0x01:
                    return READ_WRITE;
                case 0x02:
                    return UNUSED;
                default:
                    throw new EnumValueLookupException(ReturnValue.class, ReturnValue.Updateable.class, value);
            }
        }
    }
}
