package io.sot.lang;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class AllHeaders
        implements PacketDataReader
{
    private GenDWord totalLength;
    private List<StreamHeader> headers;

    public AllHeaders()
    {
    }

    public GenDWord getTotalLength()
    {
        return totalLength;
    }

    public void setTotalLength(GenDWord totalLength)
    {
        this.totalLength = totalLength;
    }

    public List<StreamHeader> getHeaders()
    {
        return headers;
    }

    public void setHeaders(List<StreamHeader> headers)
    {
        this.headers = headers;
    }

    @Override
    public void read(PacketDataInput data)
            throws PacketRWException, IOException
    {
        this.headers = new ArrayList<>();
        this.totalLength = data.readGenDWord();
        long remaining = totalLength.longValue() - totalLength.getLength();

        while (remaining > 0) {
            GenDWord length = data.readGenDWord();
            StreamHeader header = null;
            StreamHeader.Type type = StreamHeader.Type.valueOf(data.readGenUShort());
            if (StreamHeader.Type.QUERY_NOTIFICATIONS.equals(type)) {
                header = new QryNotifHeader();
            }
            else if (StreamHeader.Type.TRANSACTION_DESCRIPTOR.equals(type)) {
                header = new TxDescHeader();
            }
            else if (StreamHeader.Type.TRACE_ACTIVITY.equals(type)) {
                header = new TraceActHeader();
            }
            else {
                throw new PacketRWException("Unexpected $%.%s %s", StreamHeader.class.getSimpleName(),
                        StreamHeader.Type.class.getSimpleName(), type);
            }

            header.setLength(length);
            header.setType(type);
            header.read(data);

            this.headers.add(header);

            remaining -= length.longValue();
        }
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
