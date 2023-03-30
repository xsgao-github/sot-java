package io.sot.lang;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

/**
 * <p>
 * <code>PacketDataOutput</code> wraps a byte array that will be written to outbound TDS packet data stream (excluding
 * packet header), and defines methods to write data to it.
 * </p>
 * <p>
 * It provides both stream write methods, e.g. {@link #write(GenUByte)} and random access write methods, e.g.
 * {@link #write(int, GenUByte)}.
 * </p>
 * <p>
 * For each write method, {@link PacketRWException} will raise if writing fails, and {@code IOException} if underlying
 * output is broken.
 * </p>
 * <p>
 * TDS defined read/write rules can be found at <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/d2ed21d6-527b-46ac-8035-94f6f68eb9a8">General
 * Rules</a>, <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/355f7890-6e91-4978-ab76-2ded17ee09bc">Data Type
 * Details</a> and sub sections.
 * </p>
 *
 * @author user
 * @see PacketDataInput
 */
public class PacketDataOutput
{
    private static final Charset CS_UTF16LE = StandardCharsets.UTF_16LE;

    /**
     * The parent {@linkplain PacketWriter}.
     */
    private final PacketWriter packetWriter;
    /**
     * Byte array.
     */
    private final byte[] buffer;
    /**
     * Length of buffer.
     */
    private final int bufLen;
    /**
     * Current cursor.
     */
    private int cursor;
    /**
     * Last marked position.
     */
    private int mark;
    /**
     * Size of data that has been written.
     */
    private int size;

    public PacketDataOutput(PacketWriter w, int bufferSize)
    {
        this.packetWriter = w;
        this.bufLen = bufferSize;
        this.buffer = new byte[this.bufLen];
        this.cursor = 0;
        this.mark = 0;
        this.size = 0;
    }

    /**
     * Get the length of this data (including header).
     *
     * @return
     */
    public int size()
    {
        return size;
    }

    /**
     * Get the data written to this instance.
     *
     * @return
     */
    public byte[] getData()
    {
        byte[] b = new byte[size];
        System.arraycopy(buffer, 0, b, 0, b.length);
        return b;
    }

    /**
     * Get the available bytes to write from current cursor to end.
     *
     * @return
     */
    public int available()
    {
        return bufLen - cursor;
    }

    /**
     * Get the current cursor.
     *
     * @return
     */
    public int getPos()
    {
        return cursor;
    }

    /**
     * Move current cursor to specific <code>pos</code>.
     *
     * @param pos
     */
    public void seek(int pos)
    {
        if (pos < 0 || pos >= bufLen) {
            throw new ArrayIndexOutOfBoundsException(pos);
        }
        cursor = pos;
    }

    /**
     * Marks the current cursor in this stream. A subsequent call to the {@link #reset} repositions this stream at the
     * last marked position so that subsequent reads re-read the same bytes.
     *
     * @see #reset()
     */
    public void mark()
    {
        mark = cursor;
    }

    /**
     * Repositions this stream to the position at the time the {@link #mark} method was last called on this stream.
     *
     * @see #mark()
     */
    public void reset()
    {
        cursor = mark;
    }

    //
    // primitive Java data
    //
    public void write(byte x)
            throws PacketRWException, IOException
    {
        _write(x);
    }

    public void write(int pos, byte x)
            throws PacketRWException, IOException
    {
        _write(pos, x);
    }

    public void write(short x)
            throws PacketRWException, IOException
    {
        _write(toBytes(x));
    }

    public void write(int pos, short x)
            throws PacketRWException, IOException
    {
        _write(pos, toBytes(x));
    }

    public void write(int x)
            throws PacketRWException, IOException
    {
        _write(toBytes(x));
    }

    public void write(int pos, int x)
            throws PacketRWException, IOException
    {
        _write(pos, toBytes(x));
    }

    public void write(long x)
            throws PacketRWException, IOException
    {
        _write(toBytes(x));
    }

    public void write(int pos, long x)
            throws PacketRWException, IOException
    {
        _write(pos, toBytes(x));
    }

    public void write(byte[] bytes)
            throws PacketRWException, IOException
    {
        _write(bytes);
    }

    public void write(int pos, byte[] bytes)
            throws PacketRWException, IOException
    {
        _write(pos, bytes);
    }

    public void write(String x)
            throws PacketRWException, IOException
    {
        byte[] b = CS_UTF16LE.encode(x).array();
        _write(b);
    }

    public void write(int pos, String x)
            throws PacketRWException, IOException
    {
        byte[] b = CS_UTF16LE.encode(x).array();
        _write(pos, b);
    }

    //
    // TDS data
    //

    public void write(GenNull x)
            throws PacketRWException, IOException
    {
        // NULLTYPE is a zero-length data
    }

    public void write(int pos, GenNull x)
            throws PacketRWException, IOException
    {
        // NULLTYPE is a zero-length data
    }

    public void write(int length, GenCharBinNull x)
            throws PacketRWException, IOException
    {
        _write((length == 2 ? GenCharBinNull.NULL_2 : GenCharBinNull.NULL_4).toBytes());
    }

    public void write(int pos, int length, GenCharBinNull x)
            throws PacketRWException, IOException
    {
        _write(pos, (length == 2 ? GenCharBinNull.NULL_2 : GenCharBinNull.NULL_4).toBytes());
    }

    public void write(GenUByte x)
            throws PacketRWException, IOException
    {
        _write(x.x);
    }

    public void write(int pos, GenUByte x)
            throws PacketRWException, IOException
    {
        _write(pos, x.x);
    }

    public void write(GenUShort x)
            throws PacketRWException, IOException
    {
        _write(toBytes(x.x));
    }

    public void write(int pos, GenUShort x)
            throws PacketRWException, IOException
    {
        _write(pos, toBytes(x.x));
    }

    public void write(GenLong x)
            throws PacketRWException, IOException
    {
        _write(toBytes(x.x));
    }

    public void write(int pos, GenLong x)
            throws PacketRWException, IOException
    {
        _write(pos, toBytes(x.x));
    }

    public void write(GenLongLong x)
            throws PacketRWException, IOException
    {
        _write(toBytes(x.x));
    }

    public void write(int pos, GenLongLong x)
            throws PacketRWException, IOException
    {
        _write(pos, toBytes(x.x));
    }

    public void write(GenInteger<?> x)
            throws PacketRWException, IOException
    {
        switch (x.getLength()) {
            case GenUByte.LENGTH:
                _write(((GenUByte) x).x);
                break;
            case GenUShort.LENGTH:
                _write(toBytes(((GenUShort) x).x));
                break;
            case GenLong.LENGTH:
                _write(toBytes(((GenLong) x).x));
                break;
            case GenLongLong.LENGTH:
                _write(toBytes(((GenLongLong) x).x));
                break;
        }
    }

    public void write(int pos, GenInteger<?> x)
            throws PacketRWException, IOException
    {
        switch (x.getLength()) {
            case GenUByte.LENGTH:
                _write(pos, ((GenUByte) x).x);
                break;
            case GenUShort.LENGTH:
                _write(pos, toBytes(((GenUShort) x).x));
                break;
            case GenLong.LENGTH:
                _write(pos, toBytes(((GenLong) x).x));
                break;
            case GenLongLong.LENGTH:
                _write(pos, toBytes(((GenLongLong) x).x));
                break;
        }
    }

    public void write(GenByteStream x)
            throws PacketRWException, IOException
    {
        _write(x.x);
    }

    public void write(int pos, GenByteStream x)
            throws PacketRWException, IOException
    {
        _write(pos, x.x);
    }

    public void write(GenBVarByte x)
            throws PacketRWException, IOException
    {
        _write((byte) x.x.length);
        _write(x.x);
    }

    public void write(int pos, GenBVarByte x)
            throws PacketRWException, IOException
    {
        _write(pos, (byte) x.x.length);
        _write(pos + 1, x.x);
    }

    public void write(GenUSVarByte x)
            throws PacketRWException, IOException
    {
        _write(toBytes((short) x.x.length));
        _write(x.x);
    }

    public void write(int pos, GenUSVarByte x)
            throws PacketRWException, IOException
    {
        _write(pos, toBytes((short) x.x.length));
        _write(pos + GenUShort.LENGTH, x.x);
    }

    public void write(GenLVarByte x)
            throws PacketRWException, IOException
    {
        _write(toBytes(x.x.length));
        _write(x.x);
    }

    public void write(int pos, GenLVarByte x)
            throws PacketRWException, IOException
    {
        _write(pos, toBytes(x.x.length));
        _write(pos + GenLong.LENGTH, x.x);
    }

    public void write(GenUnicodeStream x)
            throws PacketRWException, IOException
    {
        byte[] b = CS_UTF16LE.encode(x.x).array();
        _write(b);
    }

    public void write(int pos, GenUnicodeStream x)
            throws PacketRWException, IOException
    {
        byte[] b = CS_UTF16LE.encode(x.x).array();
        _write(pos, b);
    }

    public void write(GenBVarChar x)
            throws PacketRWException, IOException
    {
        _write((byte) x.x.length());
        _write(CS_UTF16LE.encode(x.x).array());
    }

    public void write(int pos, GenBVarChar x)
            throws PacketRWException, IOException
    {
        _write(pos, (byte) x.x.length());
        _write(pos + GenUByte.LENGTH, CS_UTF16LE.encode(x.x).array());
    }

    public void write(GenUSVarChar x)
            throws PacketRWException, IOException
    {
        _write(toBytes((short) x.x.length()));
        _write(CS_UTF16LE.encode(x.x).array());
    }

    public void write(int pos, GenUSVarChar x)
            throws PacketRWException, IOException
    {
        _write(pos, toBytes((short) x.x.length()));
        _write(pos + GenUShort.LENGTH, CS_UTF16LE.encode(x.x).array());
    }

    public void write(PacketDataWriter w)
            throws PacketRWException, IOException
    {
        w.write(this);
    }

    //
    // SqlDataValue
    //

    public void write(TypeInfo ti, SqlDataValue x)
            throws PacketRWException, IOException
    {
        // TODO read data based on typeInfo
        TypeInfo.Type t = ti.getType();
//		int len; // = ti.getLength().intValue();
//		Collation col; // = ti.getCollation();
//		int precision; // = ti.getPrecision().intValue();
//		int scale; // = ti.getScale().intValue();
//		int dLen;
//		byte[] bytes;
//		String str;

        switch (t) {
            // Fixed-Length Data Types
            case NULLTYPE:
                break;
            case INT1TYPE:
                _write(((SqlTinyInt) x).byteValue());
                break;
            case BITTYPE:
                _write(((SqlBit) x).byteValue());
                break;
            case INT2TYPE:
                _write(toBytes(((SqlSmallInt) x).shortValue()));
                break;
            case INT4TYPE:
                _write(toBytes(((SqlInt) x).intValue()));
                break;
            case DATETIM4TYPE:
                // ((SqlSmallDateTime)x).
                break;
            case FLT4TYPE:
                _write(toBytes(Float.floatToIntBits(((SqlReal) x).floatValue())));
                break;
            case MONEYTYPE:
                break;
            case DATETIMETYPE:
                break;
            case FLT8TYPE:
                _write(toBytes(Double.doubleToLongBits(((SqlFloat) x).doubleValue())));
                break;
            case MONEY4TYPE:
                break;
            case INT8TYPE:
                _write(toBytes(((SqlBigInt) x).longValue()));
                break;
            case DECIMALTYPE:
                break;
            case NUMERICTYPE:
                break;
            // Variable-Length Data Types:
            // BYTELEN_TYPE:
            case GUIDTYPE:
                break;
            case INTNTYPE:
                if (x == null) {
                    _write(GenNull.BYTE_VALUE);
                }
                else {
                    if (x instanceof SqlTinyInt) {
                        _write((byte) 1);
                        _write(((SqlTinyInt) x).byteValue());
                    }
                    else if (x instanceof SqlSmallInt) {
                        _write((byte) 2);
                        _write(toBytes(((SqlSmallInt) x).shortValue()));
                    }
                    else if (x instanceof SqlInt) {
                        _write((byte) 4);
                        _write(toBytes(((SqlInt) x).intValue()));
                    }
                    else if (x instanceof SqlBigInt) {
                        _write((byte) 8);
                        _write(toBytes(((SqlBigInt) x).longValue()));
                    }
                    else {
                        throw new IllegalArgumentException(_unexpectedSqlDataTypeValue(x.getClass(), ti.getType()));
                    }
                }
                break;
            case BITNTYPE:
                if (x == null) {
                    _write(GenNull.BYTE_VALUE);
                }
                else {
                    _write((byte) 1);
                    _write(((SqlBit) x).byteValue());
                }
                break;
            case DECIMALNTYPE:
                break;
            case NUMERICNTYPE:
                break;
            case FLTNTYPE:
                break;
            case MONEYNTYPE:
                break;
            case DATETIMNTYPE:
                break;
            case DATENTYPE:
                break;
            case TIMENTYPE:
                break;
            case DATETIME2NTYPE:
                break;
            case DATETIMEOFFSETNTYPE:
                break;
            case CHARTYPE:
                break;
            case VARCHARTYPE:
                break;
            case BINARYTYPE:
                break;
            case VARBINARYTYPE:
                break;
            // USHORTLEN_TYPE:
            case BIGVARBINARYTYPE:
                break;
            case BIGVARCHARTYPE:
                break;
            case BIGBINARYTYPE:
                break;
            case BIGCHARTYPE:
                break;
            case NVARCHARTYPE:
                break;
            case NCHARTYPE:
                break;
            case XMLTYPE:
                break;
            case UDTTYPE:
                break;
            // LONGLEN_TYPE:
            case TEXTTYPE:
                break;
            case IMAGETYPE:
                break;
            case NTEXTTYPE:
                break;
            case SSVARIANTTYPE:
                break;
            // Partially Length-Prefixed Data Types - don't support
            // Other - unexpected
            default:
                throw new PacketRWException("Unexpected %s.%s %s.", TypeInfo.class.getSimpleName(), TypeInfo.Type.class.getSimpleName(), t);
        }
    }

    private String _unexpectedSqlDataTypeValue(Class<? extends SqlDataValue> clz, TypeInfo.Type t)
    {
        return String.format("Unexpected value data type %s when %s.%s is %s.", clz.getSimpleName(),
                TypeInfo.class.getSimpleName(), TypeInfo.Type.class.getSimpleName(), t);
    }

    //
    // Java ResultSet values
    //

    public void write(TypeInfo ti, boolean x, boolean bNull)
            throws PacketRWException, IOException
    {
        if (TypeInfo.Type.BITNTYPE == ti.getType() && bNull) {
            _write(GenNull.BYTE_VALUE);
        }
        else {
            if (TypeInfo.Type.BITNTYPE == ti.getType()) {
                _write(SqlBit.LENGTH);
            }
            _write(new byte[] {(byte) (x ? 1 : 0)});
        }
    }

    public void write(TypeInfo ti, byte x, boolean bNull)
            throws PacketRWException, IOException
    {
        if (TypeInfo.Type.INTNTYPE == ti.getType() && bNull) {
            _write(GenNull.BYTE_VALUE);
        }
        else {
            if (TypeInfo.Type.INTNTYPE == ti.getType()) {
                _write(SqlTinyInt.LENGTH);
            }
            _write(new byte[] {x});
        }
    }

    public void write(TypeInfo ti, short x, boolean bNull)
            throws PacketRWException, IOException
    {
        if (TypeInfo.Type.INTNTYPE == ti.getType() && bNull) {
            _write(GenNull.BYTE_VALUE);
        }
        else {
            if (TypeInfo.Type.INTNTYPE == ti.getType()) {
                _write(SqlSmallInt.LENGTH);
            }
            _write(toBytes(x));
        }
    }

    public void write(TypeInfo ti, int x, boolean bNull)
            throws PacketRWException, IOException
    {
        if (TypeInfo.Type.INTNTYPE == ti.getType() && bNull) {
            _write(GenNull.BYTE_VALUE);
        }
        else {
            if (TypeInfo.Type.INTNTYPE == ti.getType()) {
                _write(SqlInt.LENGTH);
            }
            _write(toBytes(x));
        }
    }

    public void write(TypeInfo ti, long x, boolean bNull)
            throws PacketRWException, IOException
    {
        if (TypeInfo.Type.INTNTYPE == ti.getType() && bNull) {
            _write(GenNull.BYTE_VALUE);
        }
        else {
            if (TypeInfo.Type.INTNTYPE == ti.getType()) {
                _write(SqlBigInt.LENGTH);
            }
            _write(toBytes(x));
        }
    }

    public void write(TypeInfo ti, float x, boolean bNull)
            throws PacketRWException, IOException
    {
        if (TypeInfo.Type.FLTNTYPE == ti.getType() && bNull) {
            _write(GenNull.BYTE_VALUE);
        }
        else {
            if (TypeInfo.Type.FLTNTYPE == ti.getType()) {
                _write(SqlReal.LENGTH);
            }
            _write(toBytes(Float.floatToIntBits(x)));
        }
    }

    public void write(TypeInfo ti, double x, boolean bNull)
            throws PacketRWException, IOException
    {
        if (TypeInfo.Type.FLTNTYPE == ti.getType() && bNull) {
            _write(GenNull.BYTE_VALUE);
        }
        else {
            if (TypeInfo.Type.FLTNTYPE == ti.getType()) {
                _write(SqlFloat.LENGTH);
            }
            _write(toBytes(Double.doubleToLongBits(x)));
        }
    }

    private static final BigDecimal[] POWERS_OF_TEN = new BigDecimal[39];

    static {
        for (int i = 0; i <= 38; i++) {
            POWERS_OF_TEN[i] = new BigDecimal(BigInteger.TEN.pow(i));
        }
    }

    /**
     * Write decimal {@code x} based on this TDS <a href=
     * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/5e02042c-a741-4b5a-b91d-af5e236c5252">rule</a>.
     *
     * @param ti
     * @param x
     * @param bNull
     * @throws PacketRWException
     * @throws IOException
     */
    public void write(TypeInfo ti, BigDecimal x, boolean bNull)
            throws PacketRWException, IOException
    {
        if (TypeInfo.Type.DECIMALNTYPE == ti.getType() && (bNull || x == null)) {
            _write(GenNull.BYTE_VALUE);
        }
        else {
            byte s = ti.getScale().x;

            // sign
            byte sign = (byte) (x.signum() >= 0 ? 1 : 0);

            // convert abs value to bytes
            byte[] value = x.abs().multiply(POWERS_OF_TEN[s]).toBigInteger().toByteArray();
            _swapBytes(value);

            byte[] b;

            // length
            if (value.length > 12) {
                _write((byte) 17);
                b = new byte[16];
            }
            else if (value.length > 8) {
                _write((byte) 13);
                b = new byte[12];
            }
            else if (value.length > 4) {
                _write((byte) 9);
                b = new byte[8];
            }
            else {
                _write((byte) 5);
                b = new byte[4];
            }

            // sign
            _write(sign);

            // value
            System.arraycopy(value, 0, b, 0, value.length);
            _write(b);
        }
    }

    private static final byte[] SPACE_UTF_16LE = StandardCharsets.UTF_16LE.encode(" ").array();

    /**
     * Write string {@code x} based on this TDS <a href=
     * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/5e02042c-a741-4b5a-b91d-af5e236c5252">rule</a>.
     *
     * @param ti
     * @param x
     * @param bNull
     * @throws PacketRWException
     * @throws IOException
     */
    public void write(TypeInfo ti, String x, boolean bNull)
            throws PacketRWException, IOException
    {
        // String.toCharArray(), padding blank
        if (bNull || x == null) {
            _write(GenCharBinNull.NULL_2.toBytes());
        }
        else {
            short tiLen = (short) ti.getLength().longValue();
            byte[] value, b;
            switch (ti.getType()) {
                case BIGCHARTYPE:
                    value = x.getBytes(StandardCharsets.UTF_8);

                    // TYPE_VARLEN
                    _write(toBytes(tiLen));
                    // BYTES
                    if (value.length == tiLen) {
                        _write(value);
                    }
                    else {
                        // append spaces
                        b = new byte[tiLen];
                        System.arraycopy(value, 0, b, 0, value.length);
                        Arrays.fill(b, value.length, b.length, (byte) ' ');
                        _write(b);
                    }
                    break;
                case BIGVARCHARTYPE:
                    value = x.getBytes(StandardCharsets.UTF_8);

                    // TYPE_VARLEN
                    _write(toBytes((short) value.length));
                    // BYTES
                    _write(value);
                    break;
                case NCHARTYPE:
                    value = CS_UTF16LE.encode(x).array();

                    // TYPE_VARLEN
                    _write(toBytes(tiLen));
                    // BYTES
                    if (value.length == tiLen) {
                        _write(value);
                    }
                    else {
                        // append spaces
                        b = new byte[tiLen];
                        System.arraycopy(value, 0, b, 0, value.length);
                        for (int i = value.length; i < b.length; i += 2) {
                            b[i] = SPACE_UTF_16LE[0];
                            b[i + 1] = SPACE_UTF_16LE[1];
                        }
                        _write(b);
                    }
                    break;
                case NVARCHARTYPE:
                    value = CS_UTF16LE.encode(x).array();

                    // TYPE_VARLEN
                    _write(toBytes((short) value.length));
                    // BYTES
                    _write(value);
                    break;
                default:
                    throw new PacketRWException("Unexpecte DataType %s", ti);
            }
        }
    }

    private static final LocalDate LOCAL_DAY_1 = LocalDate.of(1, 1, 1);
    private static final LocalDate LOCAL_DAY_1900 = LocalDate.of(1900, 1, 1);

    /**
     * Write date {@code x}, based on this TDS <a href=
     * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/786f5b8a-f87d-4980-9070-b9b7274c681d">rule</a>.
     *
     * @param ti
     * @param x
     * @param bNull
     * @throws PacketRWException
     * @throws IOException
     * @see TypeInfo#getTypeInfo(int, int, int, boolean)
     */
    public void write(TypeInfo ti, Date x, boolean bNull)
            throws PacketRWException, IOException
    {
        if (bNull || x == null) {
            _write(GenNull.BYTE_VALUE);
        }
        else {
            _write((byte) 3);
            int days = (int) ChronoUnit.DAYS.between(LOCAL_DAY_1, x.toLocalDate());
            _write(new byte[] {(byte) (days & 0xFF), (byte) ((days >> 8) & 0xFF), (byte) ((days >> 16) & 0xFF)});
        }
    }

    /**
     * Write time {@code x}, based on this TDS <a href=
     * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/786f5b8a-f87d-4980-9070-b9b7274c681d">rule</a>.
     *
     * @param ti
     * @param x
     * @param bNull
     * @throws PacketRWException
     * @throws IOException
     * @see TypeInfo#getTypeInfo(int, int, int, boolean)
     */
    public void write(TypeInfo ti, Time x, boolean bNull)
            throws PacketRWException, IOException
    {
        if (bNull || x == null) {
            _write(GenNull.BYTE_VALUE);
        }
        else {
            // Java support milliseconds, which maps scale = 3 in TDS
            _write((byte) 4);
            LocalTime lt = x.toLocalTime();
            int ms = ((lt.getHour() * 60 + lt.getMinute()) * 60 + lt.getSecond()) * 1000 + lt.getNano() / 1000000;
            _write(toBytes(ms));
        }
    }

    /**
     * Write time {@code x}, based on this TDS <a href=
     * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/786f5b8a-f87d-4980-9070-b9b7274c681d">rule</a>.
     *
     * @param ti
     * @param x
     * @param bNull
     * @throws PacketRWException
     * @throws IOException
     * @see TypeInfo#getTypeInfo(int, int, int, boolean)
     */
    public void write(TypeInfo ti, Timestamp x, boolean bNull)
            throws PacketRWException, IOException
    {
        if (bNull || x == null) {
            _write(GenNull.BYTE_VALUE);
        }
        else {
            // 4 bytes date and 4 bytes time

            LocalDateTime ldt = x.toLocalDateTime();
            int days = (int) ChronoUnit.DAYS.between(LOCAL_DAY_1900, ldt.toLocalDate());
            int t = ((ldt.getHour() * 60 + ldt.getMinute()) * 60 + ldt.getSecond()) * 300
                    + (int) (ldt.getNano() * 300.0 / 1000000);

            switch (ti.getType()) {
                case DATETIMETYPE:
                    _write(toBytes(days));
                    _write(toBytes(t));
                    break;
                case DATETIMNTYPE:
                    _write((byte) 8);
                    _write(toBytes(days));
                    _write(toBytes(t));
                    break;
                default:
                    throw new PacketRWException("Unexpecte DataType %s", ti);
            }
        }
    }

    //
    // buffer writer and helper methods
    //

    public static byte[] toBytes(short x)
    {
        return new byte[] {(byte) x, (byte) ((x >> 8) & 0xFF)};
    }

    public static byte[] toBytes(int x)
    {
        return new byte[] {(byte) x, (byte) ((x >> 8) & 0xFF), (byte) ((x >> 16) & 0xFF), (byte) ((x >> 24) & 0xFF)};
    }

    public static byte[] toBytes(long x)
    {
        return new byte[] {(byte) x, (byte) ((x >> 8) & 0xFF), (byte) ((x >> 16) & 0xFF), (byte) ((x >> 24) & 0xFF),
                (byte) ((x >> 32) & 0xFF), (byte) ((x >> 40) & 0xFF), (byte) ((x >> 48) & 0xFF),
                (byte) ((x >> 56) & 0xFF)};
    }

    public static byte[] toBytes(String x)
    {
        return CS_UTF16LE.encode(x).array();
    }

    private void _swapBytes(byte[] b)
    {
        int i = 0, j = b.length - 1;
        byte temp;
        while (j > i) {
            temp = b[i];
            b[i] = b[j];
            b[j] = temp;
            i++;
            j--;
        }
    }

    /**
     * Write {@code x} into this output data, starting at current cursor, and move cursor by one.
     *
     * @param x
     * @throws PacketRWException
     * @throws IOException
     */
    private void _write(byte x)
            throws PacketRWException, IOException
    {
        if (cursor + 1 <= bufLen) {
            // all bytes can fit into current buffer
            buffer[cursor] = x;

            cursor += 1;
            size = (cursor > size ? cursor : size);
        }
        else {
            // reach EOP
            // notify parent
            packetWriter.beforeNewPacket();

            // reset buffer
            Arrays.fill(buffer, (byte) 0);
            cursor = 0;
            size = 0;
            mark = 0;

            // write x
            buffer[cursor] = x;

            // calculate cursor & size
            cursor += 1;
            size = cursor;
        }
    }

    /**
     * Write {@code x} into this output data, at specified {@code pos}.
     *
     * @param pos
     * @param x
     * @throws PacketRWException
     * @throws IOException
     */
    private void _write(int pos, byte x)
            throws PacketRWException, IOException
    {
        if (pos + 1 <= bufLen) {
            buffer[pos] = x;
        }
        else {
            // reach EOP - don't support random write cross packets
            throw new IOException("Encountered EOP(end of packet).");
        }
    }

    /**
     * Write {@code x} into this output data, starting at current cursor, and move cursor by {@code x.length}.
     *
     * @param x
     * @throws PacketRWException
     * @throws IOException
     */
    private void _write(byte[] x)
            throws PacketRWException, IOException
    {
        if (cursor + x.length <= bufLen) {
            // all bytes can fit into current buffer
            System.arraycopy(x, 0, buffer, cursor, x.length);
            cursor += x.length;
            size = (cursor > size ? cursor : size);
        }
        else {
            int written = 0;
            int toWrite = 0;

            while (true) {
                // write bytes from current buffer
                toWrite = Math.min(x.length - written, bufLen - cursor);
                System.arraycopy(x, written, buffer, cursor, toWrite);
                written += toWrite;
                cursor += toWrite;
                size = (cursor > size ? cursor : size);

                if (written < x.length) {
                    // notify parent before reset buffer for next packet
                    packetWriter.beforeNewPacket();

                    // reset buffer
                    Arrays.fill(buffer, (byte) 0);
                    cursor = 0;
                    size = 0;
                    mark = 0;
                }
                else {
                    break;
                }
            }
        }
    }

    /**
     * Write {@code x} into this output data, starting at specified {@code pos}.
     *
     * @param pos
     * @param x
     * @throws PacketRWException
     * @throws IOException
     */
    private void _write(int pos, byte[] x)
            throws PacketRWException, IOException
    {
        if (pos + x.length <= bufLen) {
            // all bytes can fit into current buffer
            System.arraycopy(x, 0, buffer, pos, x.length);
        }
        else {
            // reach EOP - don't support random write cross packets
            throw new IOException("Encountered EOP(end of packet).");
        }
    }
}
