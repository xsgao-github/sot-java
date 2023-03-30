package io.sot.message;

import io.sot.Session;
import io.sot.lang.EnumValueLookupException;
import io.sot.lang.GenBVarChar;
import io.sot.lang.GenUByte;
import io.sot.lang.GenULong;
import io.sot.lang.GenUSVarChar;
import io.sot.lang.PacketDataOutput;
import io.sot.lang.PacketDataWriter;
import io.sot.lang.PacketRWException;
import io.sot.lang.TypeInfo;

import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Describes the result set for interpretation of following ROW data streams.
 * <p>
 * <p>
 * <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/58880b9f-381c-43b2-bf8b-0727a98c4f4c">COLMETADATA</a>
 * <p>
 *
 * @author user
 */
public class ColMetaData
        extends TokenStream
        implements PacketDataWriter
{
    private final List<ColumnData> columns = new ArrayList<>();

    public ColMetaData(Session session, ResultSetMetaData rsmd)
            throws SQLException, PacketRWException
    {
        super(session);
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            ColumnData cd = new ColumnData();

            cd.nullable = rsmd.isNullable(i) != ResultSetMetaData.columnNoNulls;
            cd.caseSensitive = rsmd.isCaseSensitive(i);
            cd.updateable = rsmd.isReadOnly(i) ? ColMetaData.Updateable.READ_ONLY
                    : ColMetaData.Updateable.READ_WRITE;
            cd.identity = rsmd.isAutoIncrement(i);
            cd.computed = false;
            cd.fixedLenCLRType = false;
            cd.sparseColumnSet = false;
            cd.encrypted = false;
            cd.hidden = false;
            cd.key = false;
            cd.nullableUnknown = rsmd.isNullable(i) == ResultSetMetaData.columnNullableUnknown;

            cd.typeInfo = TypeInfo.getTypeInfo(rsmd.getColumnType(i), rsmd.getPrecision(i), rsmd.getScale(i),
                    cd.nullable);

            // valid values are 0x0000 or 0x00000000, with the exceptions of data type timestamp (0x0050 or 0x00000050)
            // and alias types (greater than 0x00FF or 0x000000FF).
            if (rsmd.getColumnType(i) == Types.TIMESTAMP) {
                cd.userType = new GenULong(0x50);
            }
            else {
                cd.userType = new GenULong(0);
            }

            cd.setColumnName(new GenBVarChar(rsmd.getColumnName(i)));

            columns.add(cd);
        }
    }

    public ColMetaData(Session session, List<ColumnData> columns)
    {
        super(session);
        this.columns.clear();
        this.columns.addAll(columns);
    }

    /**
     * Get {@link ColumnData} from specified <tt>columnIndex</tt>. The first column index is 1 and so on.
     *
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return
     */
    public ColumnData getColumnMetaData(int columnIndex)
    {
        return columns.get(columnIndex - 1);
    }

    @Override
    public Token getToken()
    {
        return Token.COL_METADATA;
    }

    @Override
    public void write(PacketDataOutput out)
            throws PacketRWException, IOException
    {
        out.write(Token.COL_METADATA.getValue());
        out.write((short) this.columns.size());
        // skip [CekTable] since we don't support encryption
        if (this.columns.size() == 0) {
            out.write((short) 0xFFFF);
        }
        else {
            for (ColumnData col : this.columns) {
                col.write(out);
            }
        }
    }

    public static class ColumnData
    {
        // compatible with TDS 7.3B and above (SQL Server 2008 R2 or later)
        // UserType
        GenULong userType;

        // Flags
        boolean nullable;
        boolean caseSensitive;
        Updateable updateable;
        boolean identity;
        boolean computed;
        // usReservedODBC = 2BIT; (only exists in TDS 7.3.A and below)
        boolean fixedLenCLRType;
        // FRESERVEDBIT
        boolean sparseColumnSet;
        boolean encrypted;
        // FRESERVEDBIT
        boolean hidden;
        boolean key;
        boolean nullableUnknown;

        // TypeInfo
        TypeInfo typeInfo;

        // [TableName], only applies to text, ntext and image columns
        GenUByte numParts;
        List<GenUSVarChar> partNames;

        // [CryptoMetaData]

        // ColName
        private GenBVarChar columnName;

        public List<GenUSVarChar> getTableNames()
        {
            return partNames;
        }

        public GenBVarChar getColumnName()
        {
            return columnName;
        }

        public void setColumnName(GenBVarChar columnName)
        {
            this.columnName = columnName;
        }

        public GenULong getUserType()
        {
            return userType;
        }

        public void setUserType(GenULong userType)
        {
            this.userType = userType;
        }

        public TypeInfo getTypeInfo()
        {
            return typeInfo;
        }

        public void setTypeInfo(TypeInfo typeInfo)
        {
            this.typeInfo = typeInfo;
        }

        public boolean isNullable()
        {
            return nullable;
        }

        public void setNullable(boolean nullable)
        {
            this.nullable = nullable;
        }

        public boolean isNullableUnknown()
        {
            return nullableUnknown;
        }

        public boolean isCaseSensitive()
        {
            return caseSensitive;
        }

        public Updateable getUpdateable()
        {
            return updateable;
        }

        public boolean isIdentity()
        {
            return identity;
        }

        public boolean isComputed()
        {
            return computed;
        }

        public boolean isFixedLenCLRType()
        {
            return fixedLenCLRType;
        }

        public boolean isSparseColumnSet()
        {
            return sparseColumnSet;
        }

        public boolean isEncrypted()
        {
            return encrypted;
        }

        public boolean isHidden()
        {
            return hidden;
        }

        public void write(PacketDataOutput out)
                throws PacketRWException, IOException
        {
            // UserType
            out.write(this.userType);

            // Flags
            short flags = (short) 0;
            // low byte
            flags |= (nullable ? 1 : 0);
            flags |= ((caseSensitive ? 1 : 0) << 1);
            flags |= (updateable.value << 2);
            flags |= ((identity ? 1 : 0) << 4);
            flags |= ((computed ? 1 : 0) << 5);
            // usReservedODBC
            // high byte
            flags |= ((fixedLenCLRType ? 1 : 0) << 8);
            // FRESERVEDBIT
            flags |= ((sparseColumnSet ? 1 : 0) << 10);
            flags |= ((encrypted ? 1 : 0) << 11);
            // usReserved3
            flags |= ((hidden ? 1 : 0) << 13);
            flags |= ((key ? 1 : 0) << 14);
            flags |= ((nullableUnknown ? 1 : 0) << 15);
            // write
            out.write(flags);

            // TYPE_INFO
            out.write(typeInfo);
            // skip [TableName]
            // skip [CryptoMetaData]
            // ColName
            out.write(getColumnName());
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
