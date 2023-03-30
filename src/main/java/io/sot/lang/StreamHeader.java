package io.sot.lang;

import java.util.HashMap;
import java.util.Map;

/**
 * Message streams can be preceded by a variable number of headers as specified by the ALL_HEADERS rule. The ALL_HEADERS
 * rule, the Query Notifications header, and the Transaction Descriptor header were introduced in TDS 7.2. The Trace
 * Activity header was introduced in TDS 7.4.
 * <p>
 * defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/e17e54ae-0fac-48b7-b8a8-c267be297923">Packet
 * Data Stream Headers - ALL_HEADERS Rule Definition</a>
 *
 * @author user
 */
public abstract class StreamHeader
        implements PacketDataReader
{
    protected GenDWord length;
    protected Type type;

    public StreamHeader()
    {
    }

    public GenDWord getLength()
    {
        return length;
    }

    public void setLength(GenDWord length)
    {
        this.length = length;
    }

    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    public enum Type
    {
        QUERY_NOTIFICATIONS((short) 1),
        TRANSACTION_DESCRIPTOR((short) 2),
        TRACE_ACTIVITY((short) 3);

        private final GenUShort value;

        Type(short value)
        {
            this.value = new GenUShort(value);
        }

        public GenUShort getValue()
        {
            return this.value;
        }

        private static final Map<GenUShort, Type> m = new HashMap<>();

        static {
            for (Type tt : Type.values()) {
                m.put(tt.value, tt);
            }
        }

        public static Type valueOf(GenUShort value)
                throws EnumValueLookupException
        {
            Type tt = m.get(value);
            if (tt == null) {
                throw new EnumValueLookupException(Type.class, value);
            }
            return tt;
        }

        @Override
        public String toString()
        {
            return String.format("%s(0x%04X)", this.name(), this.value.intValue());
        }
    }
}
