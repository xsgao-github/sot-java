package io.sot.lang;

import java.util.HashMap;
import java.util.Map;

/**
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/773a62b6-ee89-4c02-9e5e-344882630aac">LOGIN7</a>.
 * <p>
 * <p>
 * fSQLType: The type of SQL the client sends to the server.
 * <ul>
 * <li>0 = SQL_DFLT</li>
 * <li>1 = SQL_TSQL</li>
 * </ul>
 *
 * @author user
 */
public enum SqlInterface
{
    SQL_DFLT((byte) 0),
    SQL_TSQL((byte) 1);

    final GenUByte value;

    SqlInterface(byte value)
    {
        this.value = new GenUByte(value);
    }

    public GenUByte getValue()
    {
        return this.value;
    }

    private static final Map<GenUByte, SqlInterface> m = new HashMap<>();

    static {
        for (SqlInterface e : SqlInterface.values()) {
            m.put(e.value, e);
        }
    }

    public static SqlInterface valueOf(GenUByte value)
            throws PacketRWException
    {
        SqlInterface e = m.get(value);
        if (e == null) {
            throw new PacketRWException("Invalid %s value 0x%02X.", SqlInterface.class.getSimpleName(),
                    value.shortValue());
        }
        return e;
    }

    @Override
    public String toString()
    {
        return String.format("%s(0x%02X)", this.name(), this.value);
    }
}
