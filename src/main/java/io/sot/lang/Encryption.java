package io.sot.lang;

import java.util.HashMap;
import java.util.Map;

/**
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/60f56408-0188-4cd5-8b90-25c6f2423868">PRELOGIN</a>
 * <p>
 *
 * @author user
 */
public enum Encryption
{
    ENCRYPT_OFF((byte) 0x00),
    ENCRYPT_ON((byte) 0x01),
    ENCRYPT_NOT_SUP((byte) 0x02),
    ENCRYPT_REQ((byte) 0x03),
    ENCRYPT_CLIENT_CERT_OFF((byte) 0x80),
    ENCRYPT_CLIENT_CERT_ON((byte) 0x81),
    ENCRYPT_CLIENT_CERT_REQ((byte) 0x83);

    final GenUByte value;

    Encryption(byte value)
    {
        this.value = new GenUByte(value);
    }

    public GenUByte getValue()
    {
        return this.value;
    }

    private static final Map<GenUByte, Encryption> m = new HashMap<>();

    static {
        for (Encryption e : Encryption.values()) {
            m.put(e.value, e);
        }
    }

    public static Encryption valueOf(GenUByte value)
            throws EnumValueLookupException
    {
        Encryption e = m.get(value);
        if (e == null) {
            throw new EnumValueLookupException(Encryption.class, value);
        }
        return e;
    }

    @Override
    public String toString()
    {
        return String.format("%s(0x%02X)", this.name(), this.value.x);
    }
}
