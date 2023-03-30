package io.sot.message;

import io.sot.lang.EnumValueLookupException;
import io.sot.lang.GenUByte;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This section describes the various tokens supported in a token-based packet data stream, as described in section
 * 2.2.4.2. The corresponding message types that use token-based packet data streams are identified in the table in
 * section 2.2.4.
 * </p>
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/67b6113c-d722-42d1-902c-3f6e8de09173">Packet
 * Data Token Stream Definition</a> and subsections.
 * </p>
 *
 * @author user
 */
public enum Token
{
    COL_METADATA((byte) 0x81),
    DONE((byte) 0xFD),
    DONE_IN_PROC((byte) 0xFF),
    DONE_PROC((byte) 0xFE),
    ENV_CHANGE((byte) 0xE3),
    ERROR((byte) 0xAA),
    INFO((byte) 0xAB),
    LOGIN_ACK((byte) 0xAD),
    ORDER((byte) 0xA9),
    RETURN_STATUS((byte) 0x79),
    RETURN_VALUE((byte) 0xAC),
    ROW((byte) 0xD1),
    SSPI((byte) 0xED),
    FED_AUTH_INFO((byte) 0xEE);

    private final GenUByte value;

    Token(byte value)
    {
        this.value = new GenUByte(value);
    }

    public GenUByte getValue()
    {
        return this.value;
    }

    private static final Map<GenUByte, Token> m = new HashMap<>();

    static {
        for (Token tt : Token.values()) {
            m.put(tt.value, tt);
        }
    }

    public static Token valueOf(GenUByte value)
            throws EnumValueLookupException
    {
        Token tt = m.get(value);
        if (tt == null) {
            throw new EnumValueLookupException(Token.class, value);
        }
        return tt;
    }

    @Override
    public String toString()
    {
        return String.format("%s(0x%02X)", this.name(), this.value.shortValue());
    }
}
