package io.sot.lang;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * The TYPE_INFO rule applies to several messages used to describe column information.
 * </p>
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/cbe9c510-eae6-4b1f-9893-a098944d430a">Type Info
 * Rule Definition</a>
 * </p>
 *
 * @author user
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "type-info")
@XmlType(propOrder = {"type", "length", "precision", "scale"})
@SuppressWarnings("unused")
public class TypeInfo
        implements PacketDataWriter, PacketDataReader
{
    /*
     * Some static/immutable TypeInfo to be reused
     */
    private static final TypeInfo TI_NULL = new TypeInfo(Type.NULLTYPE, null, null, null);
    private static final TypeInfo TI_BIT = new TypeInfo(Type.BITTYPE, null, null, null);
    private static final TypeInfo TI_INT1 = new TypeInfo(Type.INT1TYPE, null, null, null);
    private static final TypeInfo TI_INT2 = new TypeInfo(Type.INT2TYPE, null, null, null);
    private static final TypeInfo TI_INT4 = new TypeInfo(Type.INT4TYPE, null, null, null);
    private static final TypeInfo TI_INT8 = new TypeInfo(Type.INT8TYPE, null, null, null);
    private static final TypeInfo TI_FLOAT4 = new TypeInfo(Type.FLT4TYPE, null, null, null);
    private static final TypeInfo TI_FLOAT8 = new TypeInfo(Type.FLT8TYPE, null, null, null);
    private static final TypeInfo TI_MONEY4 = new TypeInfo(Type.MONEY4TYPE, null, null, null);
    private static final TypeInfo TI_MONEY = new TypeInfo(Type.MONEYTYPE, null, null, null);
    private static final TypeInfo TI_DATETIM4 = new TypeInfo(Type.DATETIM4TYPE, null, null, null);
    private static final TypeInfo TI_DATETIME = new TypeInfo(Type.DATETIMETYPE, null, null, null);

    private static final TypeInfo TI_MONEY4_N = new TypeInfo(Type.MONEYNTYPE, new GenUByte((byte) 4), null, null);
    private static final TypeInfo TI_MONEY8_N = new TypeInfo(Type.MONEYNTYPE, new GenUByte((byte) 8), null, null);
    private static final TypeInfo TI_DATETIME4_N = new TypeInfo(Type.DATETIMNTYPE, new GenUByte((byte) 4), null, null);
    private static final TypeInfo TI_DATETIME8_N = new TypeInfo(Type.DATETIMNTYPE, new GenUByte((byte) 8), null, null);
    private static final TypeInfo TI_INT1_N = new TypeInfo(Type.INTNTYPE, new GenUByte((byte) 1), null, null);
    private static final TypeInfo TI_INT2_N = new TypeInfo(Type.INTNTYPE, new GenUByte((byte) 2), null, null);
    private static final TypeInfo TI_INT4_N = new TypeInfo(Type.INTNTYPE, new GenUByte((byte) 4), null, null);
    private static final TypeInfo TI_INT8_N = new TypeInfo(Type.INTNTYPE, new GenUByte((byte) 8), null, null);
    private static final TypeInfo TI_FLOAT4_N = new TypeInfo(Type.FLTNTYPE, new GenUByte((byte) 4), null, null);
    private static final TypeInfo TI_FLOAT8_N = new TypeInfo(Type.FLTNTYPE, new GenUByte((byte) 8), null, null);
    private static final TypeInfo TI_GUID_N = new TypeInfo(Type.GUIDTYPE, new GenUByte((byte) 16), null, null);
    private static final TypeInfo TI_BIT_N = new TypeInfo(Type.BITNTYPE, new GenUByte((byte) 1), null, null);
    private static final TypeInfo TI_DATE_N = new TypeInfo(Type.DATENTYPE, null, null, null);
    private static final TypeInfo TI_TIME_N = new TypeInfo(Type.TIMENTYPE, null, null, new GenScale((byte) 3));
    private static final TypeInfo TI_DATETIME2_N = new TypeInfo(Type.DATETIME2NTYPE, null, null, new GenScale((byte) 3));
    private static final TypeInfo TI_DATETIME_OFFSET_N = new TypeInfo(Type.DATETIMEOFFSETNTYPE, null, null, new GenScale((byte) 3));
    private static final TypeInfo TI_NVARCHAR_N = new TypeInfo(Type.NVARCHARTYPE, new GenUShort((short) JdbcDataType.NVARCHAR_LENGTH), null, null);

    @XmlAttribute
    private Type type;

    @XmlAttribute
    private GenInteger<?> length;

    private Collation collation;

    @XmlAttribute
    private GenPrecision precision;

    @XmlAttribute
    private GenScale scale;

    public TypeInfo()
    {
    }

    private TypeInfo(Type type, GenInteger<?> length, GenPrecision precision, GenScale scale)
    {
        this.type = type;
        this.length = length;
        this.precision = precision;
        this.scale = scale;
    }

    public Type getType()
    {
        return type;
    }

    public GenInteger<?> getLength()
    {
        return length;
    }

    public Collation getCollation()
    {
        switch (type) {
            case NCHARTYPE:
            case NVARCHARTYPE:
            case TEXTTYPE:
            case NTEXTTYPE:
            case BIGCHARTYPE:
            case BIGVARCHARTYPE:
                return collation;
            default:
                return null;
        }
    }

    public GenPrecision getPrecision()
    {
        return precision;
    }

    public GenScale getScale()
    {
        return scale;
    }

    /**
     * Get TypeInfo by JDBC column/parameter metadata.
     *
     * @param jType
     * @param precision
     * @param scale
     * @param nullable
     * @return
     * @throws PacketRWException
     */
    public static TypeInfo getTypeInfo(int jType, int precision, int scale, boolean nullable)
            throws PacketRWException
    {
        // Non-nullable values are returned using these fixed-length data types
        if (!nullable) {
            switch (jType) {
                case JdbcDataType.TINYINT:
                    return TI_INT1;
                case JdbcDataType.SMALLINT:
                    return TI_INT2;
                case JdbcDataType.INTEGER:
                    return TI_INT4;
                case JdbcDataType.BIGINT:
                    return TI_INT8;
                case JdbcDataType.REAL:
                    return TI_FLOAT4;
                case JdbcDataType.FLOAT:
                case JdbcDataType.DOUBLE:
                    return TI_FLOAT8;
                case JdbcDataType.TIMESTAMP:
                    return TI_DATETIME;
                case JdbcDataType.BOOLEAN:
                    return TI_BIT;
            }
        }

        switch (jType) {
            case JdbcDataType.TINYINT:
                return TI_INT1_N;
            case JdbcDataType.SMALLINT:
                return TI_INT2_N;
            case JdbcDataType.INTEGER:
                return TI_INT4_N;
            case JdbcDataType.BIGINT:
                return TI_INT8_N;
            case JdbcDataType.REAL:
                return TI_FLOAT4_N;
            case JdbcDataType.DOUBLE:
            case JdbcDataType.FLOAT:
                return TI_FLOAT8_N;
            case JdbcDataType.DECIMAL:
                return new TypeInfo(Type.DECIMALNTYPE, new GenUByte(getDecimalSize((byte) precision)),
                        new GenPrecision((byte) precision), new GenScale((byte) scale));
            case JdbcDataType.NUMERIC:
                return new TypeInfo(Type.NUMERICNTYPE, new GenUByte(getDecimalSize((byte) precision)),
                        new GenPrecision((byte) precision), new GenScale((byte) scale));
            case JdbcDataType.CHAR:
                return new TypeInfo(Type.BIGCHARTYPE, new GenUShort((short) precision), null, null);
            case JdbcDataType.NCHAR:
                return new TypeInfo(Type.NCHARTYPE, new GenUShort((short) (precision * 2)), null, null);
            case JdbcDataType.VARCHAR:
                return new TypeInfo(Type.BIGVARCHARTYPE, new GenUShort((short) precision), null, null);
            case JdbcDataType.NVARCHAR:
                return new TypeInfo(Type.NVARCHARTYPE, new GenUShort((short) (precision * 2)), null, null);
            case JdbcDataType.DATE:
                return TI_DATE_N;
            case JdbcDataType.TIME:
                return TI_TIME_N;
            case JdbcDataType.TIMESTAMP:
                return TI_DATETIME8_N;
            case JdbcDataType.BOOLEAN:
                return TI_BIT_N;
            case JdbcDataType.UNKNOWN:
                return TI_NVARCHAR_N;
            default:
                throw new PacketRWException("Unexpected JDBC type %d.", jType);
        }
    }

    /*
     * https://docs.microsoft.com/en-us/sql/t-sql/data-types/data-types-transact-sql?view=sql-server-ver15
     */
    private static final Map<String, TypeInfo> NOT_NULL_TYPE_INFO = new HashMap<>();

    static {
        NOT_NULL_TYPE_INFO.put("BIT", TI_BIT);
        NOT_NULL_TYPE_INFO.put("TINYINT", TI_INT1);
        NOT_NULL_TYPE_INFO.put("SMALLINT", TI_INT2);
        NOT_NULL_TYPE_INFO.put("INT", TI_INT4);
        NOT_NULL_TYPE_INFO.put("INTEGER", TI_INT4);
        NOT_NULL_TYPE_INFO.put("BIGINT", TI_INT8);
        NOT_NULL_TYPE_INFO.put("REAL", TI_FLOAT4);
        NOT_NULL_TYPE_INFO.put("FLOAT", TI_FLOAT8);
        NOT_NULL_TYPE_INFO.put("SMALLMONEY", TI_MONEY4);
        NOT_NULL_TYPE_INFO.put("MONEY", TI_MONEY);
        NOT_NULL_TYPE_INFO.put("DATETIIME", TI_DATETIME);
        NOT_NULL_TYPE_INFO.put("SMALLDATETIME", TI_DATETIME);
        NOT_NULL_TYPE_INFO.put("DATETIME2", TI_DATETIME);
        NOT_NULL_TYPE_INFO.put("DATETIMEOFFSET", TI_DATETIME);
        NOT_NULL_TYPE_INFO.put("TIMESTAMP", TI_DATETIME);
    }

    private static final Map<String, TypeInfo> NULL_TYPE_INFO = new HashMap<>();

    static {
        NULL_TYPE_INFO.put("BIT", TI_BIT_N);
        NULL_TYPE_INFO.put("TINYINT", TI_INT1_N);
        NULL_TYPE_INFO.put("SMALLINT", TI_INT2_N);
        NULL_TYPE_INFO.put("INT", TI_INT4_N);
        NULL_TYPE_INFO.put("INTEGER", TI_INT4_N);
        NULL_TYPE_INFO.put("BIGINT", TI_INT8_N);
        NULL_TYPE_INFO.put("REAL", TI_FLOAT4_N);
        NULL_TYPE_INFO.put("FLOAT", TI_FLOAT8_N);
        NULL_TYPE_INFO.put("DATE", TI_DATE_N);
        NULL_TYPE_INFO.put("TIME", TI_TIME_N);
        NULL_TYPE_INFO.put("DATETIME", TI_DATETIME8_N);
        NULL_TYPE_INFO.put("SMALLDATETIME", TI_DATETIME8_N);
        NULL_TYPE_INFO.put("DATETIME2", TI_DATETIME8_N);
        NULL_TYPE_INFO.put("DATETIMEOFFSET", TI_DATETIME8_N);
        NULL_TYPE_INFO.put("SMALLMONEY", TI_MONEY4_N);
        NULL_TYPE_INFO.put("MONEY", TI_MONEY8_N);
    }

    /**
     * Get TypeInfo by SQL column/parameter metadata.
     *
     * @param sqlType
     * @param precision
     * @param scale
     * @param nullable
     * @return
     * @throws PacketRWException
     */
    public static TypeInfo getTypeInfo(String sqlType, int precision, int scale, boolean nullable)
            throws PacketRWException
    {
        if (sqlType == null || sqlType.length() == 0) {
            throw new PacketRWException("Parameter sqlType is null or blank.");
        }
        sqlType = sqlType.trim().toUpperCase();

        TypeInfo ti = null;
        // Non-nullable values are returned using these fixed-length data types
        if (!nullable) {
            ti = NOT_NULL_TYPE_INFO.get(sqlType.toUpperCase());
        }

        // if cannot find from not_null map, get from a null map, someone may declare <tt>@p1 date not null</tt>, but
        // actually date will always be nullable in TDS stream
        if (ti == null) {
            ti = NULL_TYPE_INFO.get(sqlType.toUpperCase());
        }

        // if ti is till null, then it must have variable precision/scale/length
        if ("DECIMAL".equals(sqlType)) {
            ti = new TypeInfo(Type.DECIMALNTYPE, new GenUByte(getDecimalSize((byte) precision)),
                    new GenPrecision((byte) precision), new GenScale((byte) scale));
        }
        else if ("NUMERIC".equals(sqlType)) {
            ti = new TypeInfo(Type.NUMERICNTYPE, new GenUByte(getDecimalSize((byte) precision)),
                    new GenPrecision((byte) precision), new GenScale((byte) scale));
        }
        else if ("CHAR".equals(sqlType)) {
            ti = new TypeInfo(Type.BIGCHARTYPE, new GenUShort((short) precision), null, null);
        }
        else if ("NCHAR".equals(sqlType)) {
            ti = new TypeInfo(Type.NCHARTYPE, new GenUShort((short) (precision * 2)), null, null);
        }
        else if ("VARCHAR".equals(sqlType)) {
            ti = new TypeInfo(Type.BIGVARCHARTYPE, new GenUShort((short) precision), null, null);
        }
        else if ("NVARCHAR".equals(sqlType)) {
            ti = new TypeInfo(Type.NVARCHARTYPE, new GenUShort((short) (precision * 2)), null, null);
        }

        if (ti == null) {
            throw new PacketRWException("Unexpected sqlType %s.", sqlType);
        }

        return ti;
    }

    private static final Pattern p = Pattern.compile("^([A-Z]{3,})\\s*(?:\\(\\s*(?:(\\d+|MAX)(?:\\s*,\\s*(\\d+))?)\\s*\\))?\\s*(NOT)?\\s*(?:NULL)?$");

    /**
     * Parse the date type information.
     *
     * @param tiString
     * @return
     * @throws PacketRWException
     */
    public static TypeInfo parse(String tiString)
            throws PacketRWException
    {
        if (tiString == null || tiString.trim().isEmpty()) {
            throw new PacketRWException("Parameter sqlType is null or blank.");
        }

        Matcher m = p.matcher(tiString.trim().toUpperCase());
        if (m.matches()) {
            String type = m.group(1);
            int precision = (m.group(2) == null ? -1 : Integer.parseInt(m.group(2)));
            int scale = (m.group(3) == null ? -1 : Integer.parseInt(m.group(3)));
            boolean nullable = (m.group(4) == null);

            return getTypeInfo(type, precision, scale, nullable);
        }
        else {
            throw new PacketRWException(String.format("Invalid data type '%s'.", tiString));
        }
    }

    /**
     * Get TypeInfo by SQL column/parameter metadata.
     *
     * @param systemTypeId
     * @param length
     * @param precision
     * @param scale
     * @param nullable
     * @return
     * @throws PacketRWException
     */
    public static TypeInfo getTypeInfo(int systemTypeId, int length, int precision, int scale, boolean nullable)
            throws PacketRWException
    {
        // Non-nullable values are returned using these fixed-length data types
        SqlDataType sqlDataType = SqlDataType.fromSystemTypeID(systemTypeId);
        if (!nullable) {
            switch (sqlDataType) {
                case BIT:
                    return TI_BIT;
                case TINYINT:
                    return TI_INT1;
                case SMALLINT:
                    return TI_INT2;
                case INT:
                    return TI_INT4;
                case BIGINT:
                    return TI_INT8;
                case REAL:
                    return TI_FLOAT4;
                case FLOAT:
                    return TI_FLOAT8;
                case SMALLMONEY:
                    return TI_MONEY4;
                case MONEY:
                    return TI_MONEY;
                case DATETIME:
                    return TI_DATETIME;
                case SMALLDATETIME:
                    return TI_DATETIM4;
                default:
                    throw new PacketRWException("Invalide combination of SqlDataType %s and nullable = %s", sqlDataType,
                            nullable);
            }
        }
        else {
            switch (sqlDataType) {
                case BIT:
                    return TI_BIT_N;
                case TINYINT:
                    return TI_INT1_N;
                case SMALLINT:
                    return TI_INT2_N;
                case INT:
                    return TI_INT4_N;
                case BIGINT:
                    return TI_INT8_N;
                case REAL:
                    return TI_FLOAT4_N;
                case FLOAT:
                    return TI_FLOAT8_N;
                case DECIMAL:
                    return new TypeInfo(Type.DECIMALNTYPE, new GenUByte(getDecimalSize((byte) precision)),
                            new GenPrecision((byte) precision), new GenScale((byte) scale));
                case NUMERIC:
                    return new TypeInfo(Type.NUMERICNTYPE, new GenUByte(getDecimalSize((byte) precision)),
                            new GenPrecision((byte) precision), new GenScale((byte) scale));
                case MONEY:
                    return TI_MONEY8_N;
                case SMALLMONEY:
                    return TI_MONEY4_N;
                case CHAR:
                    return new TypeInfo(Type.BIGCHARTYPE, new GenUShort((short) precision), null, null);
                case VARCHAR:
                case TEXT:
                    return new TypeInfo(Type.BIGVARCHARTYPE, new GenUShort((short) precision), null, null);
                case NCHAR:
                    return new TypeInfo(Type.NCHARTYPE, new GenUShort((short) (precision * 2)), null, null);
                case NVARCHAR:
                case NTEXT:
                    return new TypeInfo(Type.NVARCHARTYPE, new GenUShort((short) (precision * 2)), null, null);
                case DATE:
                    return TI_DATE_N;
                case TIME:
                    return TI_TIME_N;
                case DATETIME2:
                case DATETIME:
                case SMALLDATETIME:
                case DATETIMEOFFSET:
                    return TI_DATETIME8_N;
                case UNIQUEIDENTIFIER:
                    return TI_GUID_N;
                default:
                    throw new PacketRWException("Invalide combination of SqlDataType %s and nullable = %s", sqlDataType,
                            nullable);
            }
        }
    }

    /*
     * https://docs.microsoft.com/en-us/sql/t-sql/data-types/decimal-and-numeric-transact-sql?view=sql-server-ver15
     */
    public static final byte getDecimalSize(byte precision)
    {
        if (precision >= 29) {
            return 17;
        }
        else if (precision >= 20) {
            return 13;
        }
        else if (precision >= 10) {
            return 9;
        }
        else {
            return 5;
        }
    }

    @Override
    public void read(PacketDataInput in)
            throws PacketRWException, IOException
    {
        type = Type.valueOf(in.readGenUByte());

        switch (type.lengthBytes) {
            case 1:
                length = in.readGenUByte();
                break;
            case 2:
                length = in.readGenUShort();
                break;
            case 4:
                length = in.readGenLong();
                break;
            default:
                throw new PacketRWException("Unexpected length field bytes number %d for Type %s.", type.lengthBytes, type);
        }

        if (type.hasCollation) {
            try {
                in.mark();
                collation = new Collation();
                collation.read(in);
            }
            catch (EnumValueLookupException e) {
                // client may send an invalid collation with all bytes set to 0
                in.reset();
                in.skip(Collation.LENGTH);
            }
        }

        if (type.hasPrecision) {
            precision = in.readGenPrecision();
        }

        if (type.hasScale) {
            scale = in.readGenScale();
        }
    }

    @Override
    public void write(PacketDataOutput out)
            throws PacketRWException, IOException
    {
        out.write(type.value);

        if (type.lengthBytes > 0) {
            out.write(length);
        }

        if (type.hasCollation) {
            out.write(SqlServer.COLLATION);
        }

        if (type.hasPrecision) {
            out.write(precision);
        }

        if (type.hasScale) {
            out.write(scale);
        }
    }

    @Override
    public String toString()
    {
        return String.format("%s(0x%02X)", type.name(), type.value.shortValue());
    }

    /**
     * {@code DataType} describes the different sets of data types and how they are categorized. Specifically, data
     * values are interpreted and represented in association with their data type.
     * <p>
     *
     * <ol>
     * <li><a href=
     * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/859eb3d2-80d3-40f6-a637-414552c9c552">Fixed-Length
     * Data Types</a></li>
     * <li><a href=
     * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/ce3183a6-9d89-47e8-a02f-de5a1a1303de">Variable-Length
     * Data Types</a></li>
     * <li><a href=
     * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/7d26a257-083e-409b-81ba-897e0c672be0">Partially
     * Length-Prefixed Data Types</a></li>
     * </ol>
     *
     * @author user
     */
    public enum Type
    {
        // TODO add JDBC type
        /*
         * enum list
         */
        // Fixed-Length Data Types
        NULLTYPE(0x1F, 0, false, false, false), // Null
        INT1TYPE(0x30, 0, false, false, false), // TinyInt
        BITTYPE(0x32, 0, false, false, false), // Bit
        INT2TYPE(0x34, 0, false, false, false), // SmallInt
        INT4TYPE(0x38, 0, false, false, false), // Int
        DATETIM4TYPE(0x3A, 0, false, false, false), // SmallDateTime
        FLT4TYPE(0x3B, 0, false, false, false), // Real
        MONEYTYPE(0x3C, 0, false, false, false), // Money
        DATETIMETYPE(0x3D, 0, false, false, false), // DateTime
        FLT8TYPE(0x3E, 0, false, false, false), // Float
        MONEY4TYPE(0x7A, 0, false, false, false), // SmallMoney
        INT8TYPE(0x7F, 0, false, false, false), // BigInt
        // Variable-Length Data Types
        // BYTELEN_TYPE
        GUIDTYPE(0x24, 1, false, false, false), // UniqueIdentifier
        INTNTYPE(0x26, 1, false, false, false), // (see below)
        DECIMALTYPE(0x37, 0, false, true, true), // Decimal (legacy support)
        NUMERICTYPE(0x3F, 0, false, true, true), // Numeric (legacy support)
        BITNTYPE(0x68, 1, false, false, false), // (see below)
        DECIMALNTYPE(0x6A, 1, false, true, true), // Decimal
        NUMERICNTYPE(0x6C, 1, false, true, true), // Numeric
        FLTNTYPE(0x6D, 1, false, false, false), // (see below)
        MONEYNTYPE(0x6E, 1, false, false, false), // (see below)
        DATETIMNTYPE(0x6F, 1, false, false, false), // (see below)
        DATENTYPE(0x28, 0, false, false, false), // (introduced in TDS 7.3)
        TIMENTYPE(0x29, 0, false, false, true), // (introduced in TDS 7.3)
        DATETIME2NTYPE(0x2A, 0, false, false, true), // (introduced in TDS 7.3)
        DATETIMEOFFSETNTYPE(0x2B, 0, false, false, true), // (introduced in TDS 7.3)
        CHARTYPE(0x2F, 1, false, false, false), // Char (legacy support)
        VARCHARTYPE(0x27, 1, false, false, false), // VarChar (legacy support)
        BINARYTYPE(0x2D, 1, false, false, false), // Binary (legacy support)
        VARBINARYTYPE(0x25, 1, false, false, false), // VarBinary (legacy support)
        // USHORTLEN_TYPE
        BIGVARBINARYTYPE(0xA5, 2, false, false, false), // VarBinary
        BIGVARCHARTYPE(0xA7, 2, true, false, false), // VarChar
        BIGBINARYTYPE(0xAD, 2, false, false, false), // Binary
        BIGCHARTYPE(0xAF, 2, true, false, false), // Char
        NVARCHARTYPE(0xE7, 2, true, false, false), // NVarChar
        NCHARTYPE(0xEF, 2, true, false, false), // NChar
        XMLTYPE(0xF1, 2, false, false, false), // XML (introduced in TDS 7.2)
        UDTTYPE(0xF0, 2, false, false, false), // CLR UDT (introduced in TDS 7.2)
        // LONGLEN_TYPE
        TEXTTYPE(0x23, 4, true, false, false), // Text
        IMAGETYPE(0x22, 4, false, false, false), // Image
        NTEXTTYPE(0x63, 4, true, false, false), // NText
        SSVARIANTTYPE(0x62, 4, false, false, false); // _Variant (introduced in TDS 7.2)
        // Partially Length-Prefixed Data Types - don't support
        //XMLTYPE
        //BIGVARCHARTYPE
        //BIGVARBINARYTYPE
        //NVARCHARTYPE
        //UDTTYPE

        final GenUByte value;
        final int lengthBytes;
        final boolean hasCollation;
        final boolean hasPrecision;
        final boolean hasScale;

        // XXX assign system_type_id and user_type_id
        private GenUByte systemTypeId;
        private GenULong userTypeId;

        Type(int value, int lengthBytes, boolean hasCollation, boolean hasPrecision, boolean hasScale)
        {
            this.value = new GenUByte((byte) value);
            this.lengthBytes = lengthBytes;
            this.hasCollation = hasCollation;
            this.hasPrecision = hasPrecision;
            this.hasScale = hasScale;
        }

        public GenUByte getValue()
        {
            return value;
        }

        private static final Map<GenUByte, Type> valueTypeMap = initValueTypeMap();

        private static Map<GenUByte, Type> initValueTypeMap()
        {
            Map<GenUByte, Type> m = new HashMap<>();
            for (Type t : Type.values()) {
                m.put(t.value, t);
            }
            return m;
        }

        public static Type valueOf(GenUByte value)
                throws EnumValueLookupException
        {
            Type t = valueTypeMap.get(value);
            if (t != null) {
                return t;
            }
            else {
                throw new EnumValueLookupException(TypeInfo.class, TypeInfo.Type.class, value);
            }
        }
    }
}
