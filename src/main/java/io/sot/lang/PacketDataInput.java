package io.sot.lang;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * <code>PacketDataInput</code> wraps a byte array read from one or multiple inbound TDS packets of the same message
 * (excluding packet header), and defines methods to read TDS data from it.
 * </p>
 * <p>
 * It provides both stream read methods, e.g. {@link #readGenUByte()} and random access read methods, e.g.
 * {@link #readGenUByte(int)}.
 * </p>
 * <p>
 * For each read method, {@link PacketRWException} will raise if reading fails, and {@code IOException} if underlying
 * input is broken.
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
 * @see PacketDataOutput
 */
public class PacketDataInput
{
    private final Charset csUTF16LE = StandardCharsets.UTF_16LE;

    /**
     * Parent {@linkplain PacketReader}.
     */
    private final PacketReader reader;

    private final byte[] buffer;
    private final int bufLen;
    private int cursor;
    private int mark;

    // reused byte arrays
    private final byte[] bytes2 = new byte[2];
    private final byte[] bytes4 = new byte[4];
    private final byte[] bytes8 = new byte[8];

    public PacketDataInput(PacketReader r, byte[] b)
            throws PacketRWException, IOException
    {
        reader = r;
        cursor = 0;
        mark = 0;
        buffer = b;
        bufLen = buffer.length;
    }

    /**
     * Move current position to specific <code>pos</code>.
     *
     * @param pos
     * @throws PacketRWException
     * @throws IOException
     */
    public void seek(int pos)
            throws PacketRWException, IOException
    {
        if (pos < 0 || pos >= bufLen) {
            throw new PacketRWException("Parameter len %d is out of bound 0 - %d.", pos, bufLen);
        }
        cursor = pos;
    }

    /**
     * Get current position.
     *
     * @throws PacketRWException
     * @throws IOException
     */
    public int position()
            throws PacketRWException, IOException
    {
        return cursor;
    }

    /**
     * Skip <code>len</code> bytes.
     *
     * @param len
     * @throws PacketRWException
     * @throws IOException
     */
    public void skip(int len)
            throws PacketRWException, IOException
    {
        if (len < 0) {
            throw new PacketRWException("Parameter len nagetive: %d.", len);
        }
        else if (len > bufLen - cursor) {
            throw new PacketRWException("Parameter len is greater than available bytes: %d > %d.", len,
                    bufLen - cursor);
        }
        cursor += len;
    }

    /**
     * Marks the current position in this stream. A subsequent call to the {@link #reset} repositions this stream at the
     * last marked position so that subsequent reads re-read the same bytes.
     *
     * @throws PacketRWException
     * @throws IOException
     * @see #reset()
     */
    public void mark()
            throws PacketRWException, IOException
    {
        mark = cursor;
    }

    /**
     * Repositions this stream to the position at the time the {@link #mark} method was last called on this stream.
     *
     * @throws PacketRWException
     * @throws IOException
     * @see #mark()
     */
    public void reset()
            throws PacketRWException, IOException
    {
        cursor = mark;
    }

    /**
     * Has more bytes to read..
     *
     * @return
     */
    public boolean hasMore()
    {
        return cursor < bufLen || (reader.getHeader().getStatus().shortValue() & PacketHeader.StatusFlag.EOM.getValue()) == 0;
    }

    /**
     * Reads a single byte from the stream (at current position), and move cursor.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     * @see java.io.InputStream#read()
     */
    public byte read()
            throws PacketRWException, IOException
    {
        if (cursor >= bufLen) {
            // reach EOM
            throw new PacketRWException("EOM exception, cursor is placed at the end of message.");
        }

        // return byte and move cursor
        return buffer[cursor++];
    }

    /**
     * Reads a single byte from the specific <code>pos</code>, without moving cursor.
     *
     * @param pos
     * @return
     */
    public byte read(int pos)
    {
        if (pos < 0 || pos >= bufLen) {
            throw new IndexOutOfBoundsException(
                    String.format("Parameter pos %d is out of bounds 0 - %d.", pos, bufLen));
        }

        return buffer[pos];
    }

    public short readShort()
            throws PacketRWException, IOException
    {
        read(bytes2);
        return (short) (((bytes2[1] & 0xff) << 8) | (bytes2[0] & 0xff));
    }

    public short readShort(int pos)
            throws PacketRWException, IOException
    {
        read(pos, bytes2);
        return (short) (((bytes2[1] & 0xff) << 8) | (bytes2[0] & 0xff));
    }

    public int readUShort()
            throws PacketRWException, IOException
    {
        read(bytes2);
        return ((bytes2[1] & 0xff) << 8) | (bytes2[0] & 0xff);
    }

    public int readInt()
            throws PacketRWException, IOException
    {
        read(bytes4);
        return (((bytes4[3] & 0xff) << 24) | ((bytes4[2] & 0xff) << 16) | ((bytes4[1] & 0xff) << 8) | (bytes4[0] & 0xff));
    }

    public int readInt(int pos)
            throws PacketRWException, IOException
    {
        read(pos, bytes4);
        return (((bytes4[3] & 0xff) << 24) | ((bytes4[2] & 0xff) << 16) | ((bytes4[1] & 0xff) << 8) | (bytes4[0] & 0xff));
    }

    public long readLong()
            throws PacketRWException, IOException
    {
        read(bytes8);
        return (((long) (bytes8[7] & 0xff) << 56) | ((long) (bytes8[6] & 0xff) << 48) | ((long) (bytes8[5] & 0xff) << 40)
                | ((long) (bytes8[4] & 0xff) << 32) | ((long) (bytes8[3] & 0xff) << 24) | ((long) (bytes8[2] & 0xff) << 16)
                | ((long) (bytes8[1] & 0xff) << 8) | (bytes8[0] & 0xff));
    }

    public long readLong(int pos)
            throws PacketRWException, IOException
    {
        read(pos, bytes8);
        return (((long) (bytes8[7] & 0xff) << 56) | ((long) (bytes8[6] & 0xff) << 48) | ((long) (bytes8[5] & 0xff) << 40)
                | ((long) (bytes8[4] & 0xff) << 32) | ((long) (bytes8[3] & 0xff) << 24) | ((long) (bytes8[2] & 0xff) << 16)
                | ((long) (bytes8[1] & 0xff) << 8) | (bytes8[0] & 0xff));
    }

    public String readString(int len)
            throws PacketRWException, IOException
    {
        byte[] bytes = new byte[len];
        read(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public String readString(int pos, int len)
            throws PacketRWException, IOException
    {
        byte[] bytes = new byte[len];
        read(pos, bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public String readNString(int len)
            throws PacketRWException, IOException
    {
        byte[] bytes = new byte[len * 2];
        read(bytes);
        return csUTF16LE.decode(ByteBuffer.wrap(bytes)).toString();
    }

    public String readNString(int pos, int len)
            throws PacketRWException, IOException
    {
        byte[] bytes = new byte[len * 2];
        read(pos, bytes);
        return csUTF16LE.decode(ByteBuffer.wrap(bytes)).toString();
    }

    /**
     * Reads rest bytes of this message from current position, and move cursor.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public byte[] readBytes()
            throws PacketRWException, IOException
    {
        byte[] ret = new byte[bufLen - cursor];
        read(ret);
        return ret;
    }

    /**
     * Reads exact <code>len</code> bytes from current position, and move cursor.
     *
     * @param len
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public byte[] readBytes(int len)
            throws PacketRWException, IOException
    {
        byte[] bytes = new byte[len];
        read(bytes);
        return bytes;
    }

    /**
     * Reads exact <code>len</code> bytes from specific {@code pos}, without moving cursor..
     *
     * @param len
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public byte[] readBytes(int pos, int len)
            throws PacketRWException, IOException
    {
        byte[] bytes = new byte[len];
        read(pos, bytes);
        return bytes;
    }

    /**
     * Read {@link GenUByte} from current position and move cursor.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenUByte readGenUByte()
            throws PacketRWException, IOException
    {
        return new GenUByte(read());
    }

    /**
     * Read {@link GenUByte} from specific <code>pos</code> but don't move cursor.
     *
     * @param pos
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenUByte readGenUByte(int pos)
            throws PacketRWException, IOException
    {
        return new GenUByte(read(pos));
    }

    /**
     * Read {@link GenUByteLen} from current position and move cursor.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenUByteLen readGenUByteLen()
            throws PacketRWException, IOException
    {
        return new GenUByteLen(read());
    }

    /**
     * Read {@link GenUByteLen} from specific <code>pos</code> but don't move cursor.
     *
     * @param pos
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenUByteLen readGenUByteLen(int pos)
            throws PacketRWException, IOException
    {
        return new GenUByteLen(read(pos));
    }

    /**
     * Read {@link GenUChar} from current position and move cursor.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenUChar readGenUChar()
            throws PacketRWException, IOException
    {
        return new GenUChar(read());
    }

    /**
     * Read {@link GenUChar} from specific <code>pos</code> but don't move cursor.
     *
     * @param pos
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenUChar readGenUChar(int pos)
            throws PacketRWException, IOException
    {
        return new GenUChar(read(pos));
    }

    /**
     * Read {@link GenPrecision} from current position and move cursor.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenPrecision readGenPrecision()
            throws PacketRWException, IOException
    {
        return new GenPrecision(read());
    }

    /**
     * Read {@link GenPrecision} from specific <code>pos</code> but don't move cursor.
     *
     * @param pos
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenPrecision readGenPrecision(int pos)
            throws PacketRWException, IOException
    {
        return new GenPrecision(read(pos));
    }

    /**
     * Read {@link GenScale} from current position and move cursor.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenScale readGenScale()
            throws PacketRWException, IOException
    {
        return new GenScale(read());
    }

    /**
     * Read {@link GenScale} from specific <code>pos</code> but don't move cursor.
     *
     * @param pos
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenScale readGenScale(int pos)
            throws PacketRWException, IOException
    {
        return new GenScale(read(pos));
    }

    /**
     * Read {@link GenUShort} from current position and move cursor.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenUShort readGenUShort()
            throws PacketRWException, IOException
    {
        return new GenUShort(readShort());
    }

    /**
     * Read {@link GenUShort} from specific <code>pos</code> but don't move cursor.
     *
     * @param pos
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenUShort readGenUShort(int pos)
            throws PacketRWException, IOException
    {
        return new GenUShort(readShort(pos));
    }

    /**
     * Read {@link GenUnicodeChar} from current position and move cursor.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenUnicodeChar readGenUnicodeChar()
            throws PacketRWException, IOException
    {
        return new GenUnicodeChar(readShort());
    }

    /**
     * Read {@link GenUnicodeChar} from specific <code>pos</code> but don't move cursor.
     *
     * @param pos
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenUnicodeChar readGenUnicodeChar(int pos)
            throws PacketRWException, IOException
    {
        return new GenUnicodeChar(readShort(pos));
    }

    /**
     * Read {@link GenUShortCharBinLen} from current position and move cursor.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenUShortCharBinLen readGenUShortCharBinLen()
            throws PacketRWException, IOException
    {
        return new GenUShortCharBinLen(readShort());
    }

    /**
     * Read {@link GenUShortCharBinLen} from specific <code>pos</code> but don't move cursor.
     *
     * @param pos
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenUShortCharBinLen readGenUShortCharBinLen(int pos)
            throws PacketRWException, IOException
    {
        return new GenUShortCharBinLen(readShort(pos));
    }

    /**
     * Read {@link GenUShortLen} from current position and move cursor.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenUShortLen readGenUShortLen()
            throws PacketRWException, IOException
    {
        return new GenUShortLen(readShort());
    }

    /**
     * Read {@link GenUShortLen} from specific <code>pos</code> but don't move cursor.
     *
     * @param pos
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenUShortLen readGenUShortLen(int pos)
            throws PacketRWException, IOException
    {
        return new GenUShortLen(readShort(pos));
    }

    /**
     * Read {@link GenLong} from current position and move cursor.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenLong readGenLong()
            throws PacketRWException, IOException
    {
        return new GenLong(readInt());
    }

    /**
     * Read {@link GenLong} from specific <code>pos</code> but don't move cursor.
     *
     * @param pos
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenLong readGenLong(int pos)
            throws PacketRWException, IOException
    {
        return new GenLong(readInt(pos));
    }

    /**
     * Read {@link GenLongLen} from current position and move cursor.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenLongLen readGenLongLen()
            throws PacketRWException, IOException
    {
        return new GenLongLen(readInt());
    }

    /**
     * Read {@link GenLongLen} from specific <code>pos</code> but don't move cursor.
     *
     * @param pos
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenLongLen readGenLongLen(int pos)
            throws PacketRWException, IOException
    {
        return new GenLongLen(readInt(pos));
    }

    /**
     * Read {@link GenULong} from current position and move cursor.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenULong readGenULong()
            throws PacketRWException, IOException
    {
        return new GenULong(readInt());
    }

    /**
     * Read {@link GenULong} from specific <code>pos</code> but don't move cursor.
     *
     * @param pos
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenULong readGenULong(int pos)
            throws PacketRWException, IOException
    {
        return new GenULong(readInt(pos));
    }

    /**
     * Read {@link GenDWord} from current position and move cursor.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenDWord readGenDWord()
            throws PacketRWException, IOException
    {
        return new GenDWord(readInt());
    }

    /**
     * Read {@link GenDWord} from specific <code>pos</code> but don't move cursor.
     *
     * @param pos
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenDWord readGenDWord(int pos)
            throws PacketRWException, IOException
    {
        return new GenDWord(readInt(pos));
    }

    /**
     * Read {@link GenLongLong} from current position and move cursor.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenLongLong readGenLongLong()
            throws PacketRWException, IOException
    {
        return new GenLongLong(readLong());
    }

    /**
     * Read {@link GenLongLong} from specific <code>pos</code> but don't move cursor.
     *
     * @param pos
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenLongLong readGenLongLong(int pos)
            throws PacketRWException, IOException
    {
        return new GenLongLong(readLong(pos));
    }

    /**
     * Read {@link GenULongLong} from current position and move cursor.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenULongLong readGenULongLong()
            throws PacketRWException, IOException
    {
        return new GenULongLong(readLong());
    }

    /**
     * Read {@link GenULongLong} from specific <code>pos</code> but don't move cursor.
     *
     * @param pos
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenULongLong readGenULongLong(int pos)
            throws PacketRWException, IOException
    {
        return new GenULongLong(readLong(pos));
    }

    /**
     * Read {@link GenULongLongLen} from current position and move cursor.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenULongLongLen readGenULongLongLen()
            throws PacketRWException, IOException
    {
        return new GenULongLongLen(readLong());
    }

    /**
     * Read {@link GenULongLongLen} from specific <code>pos</code> but don't move cursor.
     *
     * @param pos
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenULongLongLen readGenULongLongLen(int pos)
            throws PacketRWException, IOException
    {
        return new GenULongLongLen(readLong(pos));
    }

    /**
     * Read <code>len</code> characters from current position, return it as {@link GenUnicodeStream} and move cursor.
     *
     * @param len
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenUnicodeStream readGenUniCodeStream(int len)
            throws PacketRWException, IOException
    {
        byte[] bytes = new byte[len * 2];
        read(bytes);
        String str = csUTF16LE.decode(ByteBuffer.wrap(bytes)).toString();
        return new GenUnicodeStream(str);
    }

    /**
     * Read <code>len</code> characters from specific <code>pos</code>, return it as {@link GenUnicodeStream} but without
     * moving cursor.
     *
     * @param pos
     * @param len
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenUnicodeStream readGenUnicodeStream(int pos, int len)
            throws PacketRWException, IOException
    {
        byte[] bytes = new byte[len * 2];
        read(pos, bytes);
        String str = csUTF16LE.decode(ByteBuffer.wrap(bytes)).toString();
        return new GenUnicodeStream(str);
    }

    /**
     * Read rest bytes in this message from current position, return it as {@link GenUnicodeStream} and move cursor to
     * the end.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenUnicodeStream readGenUniCodeStream()
            throws PacketRWException, IOException
    {
        byte[] bytes = readBytes();
        String str = csUTF16LE.decode(ByteBuffer.wrap(bytes)).toString();
        return new GenUnicodeStream(str);
    }

    /**
     * Read {@link GenBVarChar} from current position, return it and move cursor.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenBVarChar readGenBVarChar()
            throws PacketRWException, IOException
    {
        short len = (read());
        byte[] bytes = new byte[len * 2];
        read(bytes);
        String str = csUTF16LE.decode(ByteBuffer.wrap(bytes)).toString();
        return new GenBVarChar(str);
    }

    /**
     * Read {@link GenBVarChar} from specific <code>pos</code>, without moving cursor.
     *
     * @param pos
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenBVarChar readGenBVarChar(int pos)
            throws PacketRWException, IOException
    {
        short len = (read(pos));
        byte[] bytes = new byte[len * 2];
        read(pos + 1, bytes);
        String str = csUTF16LE.decode(ByteBuffer.wrap(bytes)).toString();
        return new GenBVarChar(str);
    }

    /**
     * Read {@link GenUSVarChar} from current position, return it and move cursor.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenUSVarChar readGenUSVarChar()
            throws PacketRWException, IOException
    {
        int len = (readShort());
        byte[] bytes = new byte[len * 2];
        read(bytes);
        String str = csUTF16LE.decode(ByteBuffer.wrap(bytes)).toString();
        return new GenUSVarChar(str);
    }

    /**
     * Read {@link GenUSVarChar} from specific <code>pos</code>, without moving cursor.
     *
     * @param pos
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenUSVarChar readGenUSVarChar(int pos)
            throws PacketRWException, IOException
    {
        int len = (readShort(pos));
        byte[] bytes = new byte[len * 2];
        read(pos + 2, bytes);
        String str = csUTF16LE.decode(ByteBuffer.wrap(bytes)).toString();
        return new GenUSVarChar(str);
    }

    /**
     * Read rest bytes in this message from current position, return it as {@link GenByteStream} and move cursor to the
     * end.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenByteStream readGenByteStream()
            throws PacketRWException, IOException
    {
        byte[] bytes = readBytes();
        return new GenByteStream(bytes);
    }

    /**
     * Read <code>len</code> bytes from current position, return it as {@link GenByteStream} and move cursor.
     *
     * @param len
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenByteStream readGenByteStream(int len)
            throws PacketRWException, IOException
    {
        byte[] bytes = new byte[len];
        read(bytes);
        return new GenByteStream(bytes);
    }

    /**
     * Read <code>len</code> bytes from specific <code>pos</code>, return it as {@link GenByteStream} but without
     * moving cursor.
     *
     * @param pos
     * @param len
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenByteStream readGenByteStream(int pos, int len)
            throws PacketRWException, IOException
    {
        byte[] bytes = new byte[len];
        read(pos, bytes);
        return new GenByteStream(bytes);
    }

    /**
     * Read {@link GenBVarByte} from current position, and move cursor.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenBVarByte readGenBVarByte()
            throws PacketRWException, IOException
    {
        short len = (read());
        byte[] bytes = new byte[len];
        read(bytes);
        return new GenBVarByte(bytes);
    }

    /**
     * Read {@link GenBVarByte} from specific <code>pos</code>, without moving cursor.
     *
     * @param pos
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenBVarByte readGenBVarByte(int pos)
            throws PacketRWException, IOException
    {
        short len = (read(pos));
        byte[] bytes = new byte[len];
        read(pos + 1, bytes);
        return new GenBVarByte(bytes);
    }

    /**
     * Read {@link GenUSVarByte} from current position, and move cursor.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenUSVarByte readGenUSVarByte()
            throws PacketRWException, IOException
    {
        int len = (readShort());
        byte[] bytes = new byte[len];
        read(bytes);
        return new GenUSVarByte(bytes);
    }

    /**
     * Read {@link GenUSVarByte} from specific <code>pos</code>, without moving cursor.
     *
     * @param pos
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenUSVarByte readGenUSVarByte(int pos)
            throws PacketRWException, IOException
    {
        int len = (readShort(pos));
        byte[] bytes = new byte[len];
        read(bytes);
        return new GenUSVarByte(bytes);
    }

    /**
     * Read {@link GenLVarByte} from current position, and move cursor.
     *
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenLVarByte readGenLVarByte()
            throws PacketRWException, IOException
    {
        int len = (readInt());
        byte[] bytes = new byte[len];
        read(bytes);
        return new GenLVarByte(bytes);
    }

    /**
     * Read {@link GenLVarByte} from specific <code>pos</code>, without moving cursor.
     *
     * @param pos
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public GenLVarByte readGenLVarByte(int pos)
            throws PacketRWException, IOException
    {
        int len = (readInt(pos));
        byte[] bytes = new byte[len];
        read(bytes);
        return new GenLVarByte(bytes);
    }

    /**
     * Read Java object from input based on <code>ti</code>.
     *
     * @param ti
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    public SqlDataValue read(TypeInfo ti)
            throws PacketRWException, IOException
    {
        // FIXME read data based on typeInfo
        TypeInfo.Type t = ti.getType();
//		int len; // = ti.getLength().intValue();
//		Collation col; // = ti.getCollation();
//		int precision; // = ti.getPrecision().intValue();
//		int scale; // = ti.getScale().intValue();
        int dLen;
        byte[] bytes;

        switch (t) {
            /*
             * Fixed-Length Data Types
             */
            case NULLTYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case INT1TYPE:
                return new SqlTinyInt(read());
            case BITTYPE:
                return new SqlBit(read());
            case INT2TYPE:
                return new SqlSmallInt(readShort());
            case INT4TYPE:
                return new SqlInt(readInt());
            case DATETIM4TYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case FLT4TYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case MONEYTYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case DATETIMETYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case FLT8TYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case MONEY4TYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case INT8TYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case DECIMALTYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case NUMERICTYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
                /*
                 * Variable-Length Data Types:
                 */
                // BYTELEN_TYPE:
            case GUIDTYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case INTNTYPE:
                dLen = read();
                if (dLen == 0) {
                    return null;
                }
                else {
                    return new SqlInt(readInt());
                }
            case BITNTYPE:
                dLen = read();
                if (dLen == 0) {
                    return null;
                }
                else {
                    return new SqlBit(read());
                }
            case DECIMALNTYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case NUMERICNTYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case FLTNTYPE:
                dLen = read();
                if (dLen == 0) {
                    return null;
                }
                else {
                    long l = (dLen == 4 ? readInt() : readLong());
                    return new SqlFloat(Double.longBitsToDouble(l));
                }
            case MONEYNTYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case DATETIMNTYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case DATENTYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case TIMENTYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case DATETIME2NTYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case DATETIMEOFFSETNTYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case CHARTYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case VARCHARTYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case BINARYTYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case VARBINARYTYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
                // USHORTLEN_TYPE:
            case BIGVARBINARYTYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case BIGVARCHARTYPE:
                dLen = readShort();
                if (dLen == 0xFFFF) {
                    return null;
                }
                else {
                    return new SqlVarChar(this.readString(dLen));
                }
            case BIGBINARYTYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case BIGCHARTYPE:
                if (ti.getLength().intValue() == 0xFFFF) {
                    byte[] plpBytes = readPLPBytes();
                    if (plpBytes == null) {
                        return null;
                    }
                    else {
                        return new SqlNVarChar(new String(plpBytes, StandardCharsets.UTF_8));
                    }
                }
                else {
                    dLen = readShort();
                    if (dLen == 0xFFFF) {
                        return new SqlChar();
                    }
                    else {
                        return new SqlChar(this.readString(dLen));
                    }
                }
            case NVARCHARTYPE:
                if (ti.getLength().intValue() == 0xFFFF) {
                    byte[] plpBytes = readPLPBytes();
                    if (plpBytes == null) {
                        return null;
                    }
                    else {
                        return new SqlNVarChar(csUTF16LE.decode(ByteBuffer.wrap(plpBytes)).toString());
                    }
                }
                else {
                    dLen = readUShort();
                    if (dLen == 0xFFFF) {
                        return null;
                    }
                    else {
                        bytes = new byte[dLen];
                        read(bytes);
                        return new SqlNVarChar(csUTF16LE.decode(ByteBuffer.wrap(bytes)).toString());
                    }
                }
            case NCHARTYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case XMLTYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case UDTTYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
                // LONGLEN_TYPE:
            case TEXTTYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case IMAGETYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
            case NTEXTTYPE:
                dLen = readInt();
                if (dLen == 0xFFFFFFFF) {
                    return null;
                }
                else {
                    bytes = new byte[dLen];
                    read(bytes);
                    return new SqlNText(csUTF16LE.decode(ByteBuffer.wrap(bytes)).toString());
                }
            case SSVARIANTTYPE:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
                /*
                 * Partially Length-Prefixed Data Types: XMLTYPE, BIGVARBINARYTYPE, BIGVARCHARTYPE, NVARCHARTYPE, UDTTYPE.
                 */
                // Other - unexpected
            default:
                throw new UnsupportedOperationException(String.format("read(%s) is not implemented yet.", t));
        }
    }

    /**
     * Section <tt>Partially Length-prefixed Bytes</tt> in this doc: <a href=
     * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/3f983fde-0509-485a-8c40-a9fa6679a828">Data
     * Type Dependent Data Streams</a>
     *
     * @throws IOException
     * @throws PacketRWException
     */
    private byte[] readPLPBytes()
            throws PacketRWException, IOException
    {
        // partially length-prefixed
        // PLP_NULL
        // /
        // ((ULONGLONGLEN / UNKNOWN_PLP_LEN) *PLP_CHUNK PLP_TERMINATOR)
        long totalLen = readLong();
        if (totalLen == -1) {
            // PLP_NULL = %xFFFFFFFFFFFFFFFF
            return null;
        }

        // PLP_CHUNK = ULONGLEN 1*BYTE
        // PLP_TERMINATOR = %x00000000
        ByteArrayOutputStream out = new ByteArrayOutputStream(buffer.length);
        int len = 0;
        byte[] bytes = null;
        while ((len = readInt()) != 0) {
            bytes = new byte[len];
            read(bytes);
            out.write(bytes);
        }
        // validate
        if (totalLen != -2 && totalLen != out.size()) {
            throw new PacketRWException("Prtially length-fixed bytes length is marked as %d, but %d bytes are read.",
                    totalLen, out.size());
        }

        return out.toByteArray();
    }

    /**
     * Read data from current position into {@code r}, and move cursor.
     *
     * @param r
     * @throws PacketRWException
     * @throws IOException
     */
    public PacketDataReader read(PacketDataReader r)
            throws PacketRWException, IOException
    {
        r.read(this);
        return r;
    }

    /**
     * Read data from specific {@code pos} into {@code r}, without moving cursor.
     *
     * @param r
     * @throws PacketRWException
     * @throws IOException
     */
    public PacketDataReader read(int pos, PacketDataReader r)
            throws PacketRWException, IOException
    {
        mark();
        r.read(this);
        reset();

        return r;
    }

    /**
     * Reads exact <code>bytes.length</code> bytes from the stream (starting from current position) into
     * <code>bytes</code>.
     *
     * @param x
     * @throws PacketRWException
     * @throws IOException
     */
    private byte[] read(byte[] x)
            throws PacketRWException, IOException
    {
        if (x == null) {
            throw new NullPointerException("Parameter x is null.");
        }

        if (cursor + x.length > bufLen) {
            throw new PacketRWException(String.format(
                    "EOM exception, cursor + x.length > bufLen: %d + %d > %d.", cursor, x.length, bufLen));
        }

        System.arraycopy(buffer, cursor, x, 0, x.length);
        cursor += x.length;

        return x;
    }

    /**
     * Reads exact <code>bytes.length</code> bytes from the stream (starting from position <code>pos</code>) into
     * <code>bytes</code>.
     *
     * @param pos
     * @param x
     * @return
     * @throws PacketRWException
     * @throws IOException
     */
    private byte[] read(int pos, byte[] x)
            throws PacketRWException, IOException
    {
        if (x == null) {
            throw new NullPointerException("Parameter bytes is null.");
        }

        if (pos < 0 || pos > bufLen) {
            throw new IndexOutOfBoundsException(
                    String.format("Parameter pos %d is out of bounds 0 - %d.", pos, bufLen));
        }
        if (pos + x.length > bufLen) {
            throw new PacketRWException(String.format(
                    "EOM exception, pos + x.length > bufLen: %d + %d > %d.", pos, x.length, bufLen));
        }

        System.arraycopy(buffer, pos, x, 0, x.length);

        return x;
    }
}
