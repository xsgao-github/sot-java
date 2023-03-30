package io.sot.message;

import io.sot.Session;
import io.sot.lang.Encryption;
import io.sot.lang.EnumValueLookupException;
import io.sot.lang.GenUByte;
import io.sot.lang.GenULong;
import io.sot.lang.GenULongLong;
import io.sot.lang.GenUShort;
import io.sot.lang.PacketDataInput;
import io.sot.lang.PacketDataOutput;
import io.sot.lang.PacketDataReader;
import io.sot.lang.PacketDataWriter;
import io.sot.lang.PacketRWException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A message sent by the client to set up context for login. The server responds to a client PRELOGIN message with a
 * message of packet header type 0x04 and the packet data containing a PRELOGIN structure.
 * <p>
 * This message stream is also used to wrap SSL handshake payload, if encryption is needed. In this scenario, where
 * PRELOGIN message is transporting the SSL handshake payload, the packet data is simply the raw bytes of the SSL
 * handshake payload.
 * <p>
 * <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/60f56408-0188-4cd5-8b90-25c6f2423868">PRELOGIN</a>
 *
 * @author user
 * * */
public class PreLogin
        implements PacketDataReader, PacketDataWriter
{
    private static final int GUID_LENGTH = 16;

    private final Session session;

    // very first PRE_LOGIN packet
    private GenUByte majorVersion;
    private GenUByte minorVersion;
    private GenUShort build;
    private GenUShort subBuild;
    private Encryption encryption;
    // name of the instance of the database server that supports SQL or just %x00: B_INSTVALIDITY = *BYTE %x00
    private byte[] instValidity;
    // client application thread id used for debugging purposes
    private GenULong threadID;
    private GenUByte mars;
    // client application trace id used for debugging purposes
    private byte[] guidConnID;
    // client application activity id used for debugging purposes
    private byte[] guidActivityID;
    // client application activity sequence used for debugging purposes
    private GenULong activitySequence;
    // The FEDAUTHREQUIRED payload option token is not supported by SQL Server.
    private FedAuthRequired fedAuthRequired;
    // nonce to be encrypted by using session key from federated authentication handshake
    private GenULongLong nonce;

    // or TSL/SSL negotiation payload
    byte[] sslPayload;

    public PreLogin(Session session)
    {
        this.session = session;
    }

    public GenUByte getMajorVersion()
    {
        return majorVersion;
    }

    public void setMajorVersion(GenUByte majorVersion)
    {
        this.majorVersion = majorVersion;
    }

    public GenUByte getMinorVersion()
    {
        return minorVersion;
    }

    public void setMinorVersion(GenUByte minorVersion)
    {
        this.minorVersion = minorVersion;
    }

    public GenUShort getBuild()
    {
        return build;
    }

    public void setBuild(GenUShort build)
    {
        this.build = build;
    }

    public GenUShort getSubBuild()
    {
        return subBuild;
    }

    public void setSubBuild(GenUShort subBuild)
    {
        this.subBuild = subBuild;
    }

    public Encryption getEncryption()
    {
        return encryption;
    }

    public void setEncryption(Encryption encryption)
    {
        this.encryption = encryption;
    }

    public byte[] getInstValidity()
    {
        return instValidity;
    }

    public void setInstValidity(byte[] instValidity)
    {
        this.instValidity = instValidity;
    }

    public GenULong getThreadID()
    {
        return threadID;
    }

    public void setThreadID(GenULong threadID)
    {
        this.threadID = threadID;
    }

    public GenUByte getMars()
    {
        return mars;
    }

    public void setMars(GenUByte mars)
    {
        this.mars = mars;
    }

    public byte[] getGuidConnID()
    {
        return guidConnID;
    }

    public void setGuidConnID(byte[] guidConnID)
    {
        this.guidConnID = guidConnID;
    }

    public byte[] getGuidActivityID()
    {
        return guidActivityID;
    }

    public void setGuidActivityID(byte[] guidActivityID)
    {
        this.guidActivityID = guidActivityID;
    }

    public GenULong getActivitySequence()
    {
        return activitySequence;
    }

    public void setActivitySequence(GenULong activitySequence)
    {
        this.activitySequence = activitySequence;
    }

    public FedAuthRequired getFedAuthRequired()
    {
        return fedAuthRequired;
    }

    public void setFedAuthRequired(FedAuthRequired fedAuthRequired)
    {
        this.fedAuthRequired = fedAuthRequired;
    }

    public GenULongLong getNonce()
    {
        return nonce;
    }

    public void setNonce(GenULongLong nonce)
    {
        this.nonce = nonce;
    }

    public byte[] getSslPayload()
    {
        return sslPayload;
    }

    public void setSslPayload(byte[] sslPayload)
    {
        this.sslPayload = sslPayload;
    }

    @Override
    public void read(PacketDataInput data)
            throws PacketRWException, IOException
    {
        data.seek(0);

        // PRELOGIN = (*PRELOGIN_OPTION *PL_OPTION_DATA) / SSL_PAYLOAD

        if (Session.State.INITIAL.equals(this.session.getState())) {
            // *PRELOGIN_OPTION
            List<Option> opts = new ArrayList<>();
            Set<OptionToken> tks = new HashSet<>();

            while (true) {
                OptionToken t = OptionToken.valueOf(data.readGenUByte());

                // PL_OPTION_TOKEN VERSION is a required token, and it MUST be the first token sent as part of PRELOGIN
                if (tks.size() == 0) {
                    if (!t.equals(OptionToken.VERSION)) {
                        throw new PacketRWException("First PL_OPTION_TOKEN is %s, not %s.", t, OptionToken.VERSION);
                    }
                }

                // TERMINATOR is a required token, and it MUST be the last token of PRELOGIN_OPTION
                if (t == OptionToken.TERMINATOR) {
                    break;
                }

                if (tks.contains(t)) {
                    throw new PacketRWException("Duplicate PL_OPTION_TOKEN %s.", t);
                }

                Option o = new Option();
                o.token = t;
                o.offset = data.readGenUShort().swapBytes();
                o.length = data.readGenUShort().swapBytes();
                opts.add(o);
                tks.add(o.token);
                tks.add(t);
            }

            // *PL_OPTION_DATA
            for (Option o : opts) {
                int offset = o.offset.intValue();
                int length = o.length.intValue();

                if (length != 0) {
                    switch (o.token) {
                        case VERSION:
                            data.mark();
                            data.seek(offset);
                            // UL_VERSION is composed of major version (1 byte), minor version (1 byte), and build number (2
                            // bytes). It is represented in network byte order (big-endian).
                            majorVersion = data.readGenUByte();
                            minorVersion = data.readGenUByte();
                            build = data.readGenUShort().swapBytes();
                            subBuild = data.readGenUShort();
                            data.reset();
                            break;
                        case ENCRYPTION:
                            GenUByte genUByte = data.readGenUByte(offset);
                            encryption = Encryption.valueOf(genUByte);
                            break;
                        case INST_OPT:
                            instValidity = data.readBytes(offset, length);
                            break;
                        case THREAD_ID:
                            threadID = data.readGenULong(offset);
                            break;
                        case MARS:
                            mars = data.readGenUByte(offset);
                            break;
                        case TRACE_ID:
                            data.mark();
                            data.seek(offset);
                            guidConnID = data.readBytes(GUID_LENGTH);
                            guidActivityID = data.readBytes(GUID_LENGTH);
                            activitySequence = data.readGenULong();
                            data.reset();
                            break;
                        case FED_AUTH_REQUIRED:
                            fedAuthRequired = FedAuthRequired.valueOf(data.readGenUByte(offset));
                            break;
                        case NONCE_OPT:
                            nonce = data.readGenULongLong(offset);
                            break;
                        default:
                            throw new PacketRWException("Invalid PL_OPTION_TOKEN %s.", o.token);
                    }
                }
            }
        }
        else if (Session.State.SSL_NEG.equals(this.session.getState())) {
            // SSL_PAYLOAD
            sslPayload = data.readBytes();
        }
    }

    @Override
    public void write(PacketDataOutput out)
            throws PacketRWException, IOException
    {
        out.seek(0);

        // PRELOGIN = (*PRELOGIN_OPTION *PL_OPTION_DATA) / SSL_PAYLOAD

        if (Session.State.INITIAL.equals(this.session.getState())) {
            // *PL_OPTION_DATA
            int plLength = (GenUByte.LENGTH + GenUShort.LENGTH * 2) * 6 + GenUByte.LENGTH;
            if (this.fedAuthRequired != null) {
                plLength += (GenUByte.LENGTH + GenUShort.LENGTH * 2);
            }
            out.seek(plLength);

            List<Option> opts = new ArrayList<>();
            opts.add(writeOptionData(OptionToken.VERSION, out));
            opts.add(writeOptionData(OptionToken.ENCRYPTION, out));
            opts.add(writeOptionData(OptionToken.INST_OPT, out));
            opts.add(writeOptionData(OptionToken.THREAD_ID, out));
            opts.add(writeOptionData(OptionToken.MARS, out));
            opts.add(writeOptionData(OptionToken.TRACE_ID, out));
            if (this.fedAuthRequired != null) {
                opts.add(writeOptionData(OptionToken.FED_AUTH_REQUIRED, out));
            }
//			// XXX nonce
//			opts.add(writeOptionData(OptionToken.NONCE_OPT, data));

            // *PRELOGIN_OPTION
            out.seek(0);
            for (Option o : opts) {
                out.write(o.token.value);
                out.write(o.offset.swapBytes());
                out.write(o.length.swapBytes());
            }

            // TERMINATOR
            out.write(OptionToken.TERMINATOR.value);
        }
        else if (Session.State.SSL_NEG.equals(this.session.getState())) {
            // SSL_PAYLOAD
            out.write(sslPayload);
        }
    }

    private Option writeOptionData(OptionToken token, PacketDataOutput out)
            throws PacketRWException, IOException
    {
        // pos before writing
        int pos = out.getPos();

        // write data
        switch (token) {
            case VERSION:
                out.write(majorVersion);
                out.write(minorVersion);
                out.write(build.swapBytes());
                out.write(subBuild);
                break;
            case ENCRYPTION:
                out.write(encryption.getValue());
                break;
            case INST_OPT:
                out.write(instValidity);
                break;
            case THREAD_ID:
                // don't write client debug information back
                break;
            case MARS:
                out.write(mars);
                break;
            case TRACE_ID:
                // don't write client debug information back
                break;
            case FED_AUTH_REQUIRED:
                out.write(fedAuthRequired.getValue());
                break;
            case NONCE_OPT:
                out.write(nonce);
                break;
            default:
                throw new IllegalArgumentException(String.format("Invalid PL_OPTION_TOKEN %s.", token));
        }

        // record option
        Option o = new Option();
        o.token = token;
        o.offset = new GenUShort((short) pos);
        o.length = new GenUShort((short) (out.getPos() - pos));

        return o;
    }

    private static final class Option
    {
        OptionToken token;
        GenUShort offset; // big endian
        GenUShort length; // big endian
    }

    private enum OptionToken
    {
        VERSION((byte) 0x00),
        ENCRYPTION((byte) 0x01),
        INST_OPT((byte) 0x02),
        THREAD_ID((byte) 0x03),
        MARS((byte) 0x04),
        TRACE_ID((byte) 0x05),
        FED_AUTH_REQUIRED((byte) 0x06),
        NONCE_OPT((byte) 0x07),
        TERMINATOR((byte) 0xFF);

        private final GenUByte value;

        OptionToken(byte value)
        {
            this.value = new GenUByte(value);
        }

        private static final Map<GenUByte, OptionToken> m = new HashMap<>();

        static {
            for (OptionToken t : OptionToken.values()) {
                m.put(t.value, t);
            }
        }

        private static OptionToken valueOf(GenUByte value)
                throws PacketRWException
        {
            OptionToken t = m.get(value);
            if (t == null) {
                throw new PacketRWException("Invalid PL_OPTION_TOKEN 0x%02X.", value.intValue());
            }
            return t;
        }

        @Override
        public String toString()
        {
            return String.format("%s(0x%02X)", this.name(), this.value.shortValue());
        }
    }

    public enum FedAuthRequired
    {
        SSPI((byte) 0),
        FED_AUTH((byte) 1);

        private final GenUByte value;

        FedAuthRequired(byte value)
        {
            this.value = new GenUByte(value);
        }

        public GenUByte getValue()
        {
            return this.value;
        }

        private static final Map<GenUByte, FedAuthRequired> m = new HashMap<>();

        static {
            for (FedAuthRequired tt : FedAuthRequired.values()) {
                m.put(tt.value, tt);
            }
        }

        public static FedAuthRequired valueOf(GenUByte value)
                throws EnumValueLookupException
        {
            FedAuthRequired tt = m.get(value);
            if (tt == null) {
                throw new EnumValueLookupException(FedAuthRequired.class, value);
            }
            return tt;
        }

        @Override
        public String toString()
        {
            return String.format("%s(0x%02X)", this.name(), this.value.intValue());
        }
    }
}
