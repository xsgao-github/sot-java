package io.sot.message;

import io.sot.Session;
import io.sot.lang.ByteOrder;
import io.sot.lang.CharSet;
import io.sot.lang.Collation;
import io.sot.lang.EnumValueLookupException;
import io.sot.lang.FloatRepresentation;
import io.sot.lang.GenByteStream;
import io.sot.lang.GenDWord;
import io.sot.lang.GenLVarByte;
import io.sot.lang.GenLong;
import io.sot.lang.GenUByte;
import io.sot.lang.GenUShort;
import io.sot.lang.LCID;
import io.sot.lang.PacketDataInput;
import io.sot.lang.PacketDataReader;
import io.sot.lang.PacketRWException;
import io.sot.lang.SortId;
import io.sot.lang.SqlInterface;
import io.sot.lang.TdsVersion;
import io.sot.lang.UserType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines the authentication rules for use between client and server.
 * <p>
 * <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/773a62b6-ee89-4c02-9e5e-344882630aac">LOGIN7</a>
 *
 * @author user
 */
public class Login7
        implements PacketDataReader
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LogManager.getLogger(Login7.class);

    private final Charset csUTF16LE = StandardCharsets.UTF_16LE;

    @SuppressWarnings("unused")
    private final Session session;

    private TdsVersion tdsVersion;
    private GenDWord packetSize;
    private GenDWord clientProgVer;
    private GenDWord clientPID;
    private GenDWord clientConnID;

    // OptionFlags1
    private ByteOrder byteOrder;
    private CharSet charSet;
    private FloatRepresentation floatRep;
    private boolean supportDumpLoad;
    private boolean warnUseDB;
    private boolean needInitDatabase;
    private boolean warnSetLang;

    // OptionFlags2
    private boolean needInitLanguage;
    private boolean isODBC;
    // fTranBoundary = BIT; (removed in TDS 7.2)
    // fCacheConnect = BIT; (removed in TDS 7.2)
    private UserType userType;
    private boolean intSecurity;

    // TypeFlags
    private SqlInterface sqlInterface;
    private boolean isOLEDB;
    private boolean readOnly;

    // OptionFlags3
    private boolean changePoassword;
    private boolean sendYukonBinaryXML;
    private boolean userInstance;
    private boolean unknownCollationHandling;
    private boolean useExtension;

    // ClientTimeZone = LONG; This field is not used and can be set to zero.
    private ClientLCID clientLCID;

    private String hostName;
    private String userName;
    private byte[] password;
    private String appName;
    private String serverName;
    private GenDWord extOffset;
    private String libraryName;
    private String initLanguage;
    private String initDatabase;
    private byte[] clientID;
    private byte[] SSPI;
    private String attachDBFile;
    private byte[] newPassword;
    private FedAuth fedAuth;

    public Login7(Session session)
    {
        this.session = session;
    }

    @Override
    public void read(PacketDataInput data)
            throws PacketRWException, IOException
    {
        /*
         * Login packet header
         */
        // The length of a LOGIN7 stream MUST NOT be longer than 128K-1(byte) bytes.
        @SuppressWarnings("unused")
        long packetLength = data.readGenDWord().longValue();
        this.tdsVersion = TdsVersion.valueOf(data.readGenDWord());
        this.packetSize = data.readGenDWord();
        this.clientProgVer = data.readGenDWord();
        this.clientPID = data.readGenDWord();
        this.clientConnID = data.readGenDWord();

        // OptionFlags1
        byte flags = (byte) data.readGenUByte().shortValue();
        this.byteOrder = ByteOrder.values()[flags & 0x01];
        this.charSet = CharSet.values()[(flags >> 1) & 0x01];
        this.floatRep = FloatRepresentation.values()[(flags >> 2) & 0x03];
        this.supportDumpLoad = (((flags >> 4) & 0x01) == 1);
        this.warnUseDB = (((flags >> 5) & 0x01) == 1);
        this.needInitDatabase = (((flags >> 6) & 0x01) == 1);
        this.warnSetLang = (((flags >> 7) & 0x01) == 1);

        // OptionFlags2
        flags = (byte) data.readGenUByte().shortValue();
        this.needInitLanguage = ((flags & 0x01) == 1);
        this.isODBC = (((flags >> 1) & 0x01) == 1);
        this.userType = UserType.values()[(flags >> 4) & 0x03];
        this.intSecurity = (((flags >> 7) & 0x01) == 1);

        // TypeFlags
        flags = (byte) data.readGenUByte().shortValue();
        this.sqlInterface = SqlInterface.values()[flags & 0x0F];
        this.isOLEDB = (((flags >> 5) & 0x01) == 1);
        this.readOnly = (((flags >> 6) & 0x01) == 1);

        // OptionFlags3
        flags = (byte) data.readGenUByte().shortValue();
        this.changePoassword = ((flags & 0x01) == 1);
        this.userInstance = (((flags >> 1) & 0x01) == 1);
        this.sendYukonBinaryXML = (((flags >> 2) & 0x01) == 1);
        this.unknownCollationHandling = (((flags >> 3) & 0x01) == 1);
        this.useExtension = (((flags >> 4) & 0x01) == 1);

        data.skip(GenLong.LENGTH); // ClientTimeZone
        this.clientLCID = ClientLCID.fromDWord(data.readGenDWord());

        /*
         * Offsets and lengths, followed by data
         */
        this.hostName = data.readGenUnicodeStream(data.readGenUShort().intValue(), data.readGenUShort().intValue()).getString();
        this.userName = data.readGenUnicodeStream(data.readGenUShort().intValue(), data.readGenUShort().intValue()).getString();
        this.password = decodePassword(data.readBytes(data.readGenUShort().intValue(), data.readGenUShort().intValue() * 2));
        this.appName = data.readGenUnicodeStream(data.readGenUShort().intValue(), data.readGenUShort().intValue()).getString();
        this.serverName = data.readGenUnicodeStream(data.readGenUShort().intValue(), data.readGenUShort().intValue()).getString();

        if (this.useExtension) {
            this.extOffset = data.readGenDWord(data.readGenUShort().intValue());
            data.skip(GenUShort.LENGTH);
        }
        else {
            data.skip(GenUShort.LENGTH);
            data.skip(GenUShort.LENGTH);
        }

        this.libraryName = data.readGenUnicodeStream(data.readGenUShort().intValue(), data.readGenUShort().intValue()).getString();
        this.initLanguage = data.readGenUnicodeStream(data.readGenUShort().intValue(), data.readGenUShort().intValue()).getString();
        this.initDatabase = data.readGenUnicodeStream(data.readGenUShort().intValue(), data.readGenUShort().intValue()).getString();
        this.clientID = data.readBytes(6);
        int ibSSPI = data.readGenUShort().intValue();
        int cbSSPI = data.readGenUShort().intValue();
        this.attachDBFile = data.readGenUnicodeStream(data.readGenUShort().intValue(), data.readGenUShort().intValue()).getString();
        this.newPassword = decodePassword(data.readBytes(data.readGenUShort().intValue(), data.readGenUShort().intValue() * 2));
        long cbSSPILong = data.readGenDWord().longValue();

        if (cbSSPI < GenUShort.MAX_VALUE) {
            this.SSPI = data.readBytes(ibSSPI, cbSSPI);
        }
        else if (cbSSPILong > 0) {
            if (cbSSPILong > Integer.MAX_VALUE) {
                throw new PacketRWException("SSPI byte array length %d is greater than than %s.", cbSSPILong, Integer.MAX_VALUE);
            }
            else {
                this.SSPI = data.readBytes(ibSSPI, (int) cbSSPILong);
            }
        }
        else {
            this.SSPI = data.readBytes(ibSSPI, cbSSPI);
        }
        if (intSecurity && (SSPI == null || SSPI.length == 0)) {
            throw new PacketRWException("Integrated Security is set to on, but SSPI data is missing.");
        }

        /*
         * FeatureExt
         */
        if (useExtension) {
            data.seek((int) extOffset.longValue());
            while (true) {
                byte featureId = data.read();
                if (featureId == (byte) 0xff) {
                    break;
                }

                switch (featureId) {
                    case (byte) 0x01:
                        // SESSIONRECOVERY - not supported
                        data.skip(data.readInt());
                        break;
                    case (byte) 0x02:
                        // FEDAUTH - conditionally read FedAuth
                        this.fedAuth = FedAuth.read(data, data.readInt());
                        break;
                    case (byte) 0x04:
                        // COLUMNENCRYPTION - not supported
                        data.skip(data.readInt());
                        break;
                    case (byte) 0x05:
                        // GLOBALTRANSACTIONS - not supported
                        data.skip(data.readInt());
                        break;
                    case (byte) 0x08:
                        // AZURESQLSUPPORT - not supported
                        data.skip(data.readInt());
                        break;
                    case (byte) 0x09:
                        // DATACLASSIFICATION - not supported
                        data.skip(data.readInt());
                        break;
                    case (byte) 0x0a:
                        // UTF8_SUPPORT - not supported
                        data.skip(data.readInt());
                        break;
                    case (byte) 0x0b:
                        // AZURESQLDNSCACHING - not supported
                        data.skip(data.readInt());
                        break;
                    default:
                        throw new PacketRWException("Unexpected feature id 0x%02x at offset %d.", featureId, data.position() - 1);
                }
            }
        }
    }

    /*
     * After reading a submitted password/newPassword, for every byte in the password buffer starting with the position
     * pointed to by ibPassword or ibChangePassword, the server SHOULD first do a bit-XOR with 0xA5 (10100101) and then
     * swap the four high bits with the four low bits.
     */
    private byte[] decodePassword(byte[] b)
    {
        for (int i = 0; i < b.length; i++) {
            b[i] ^= 0xA5;
            b[i] = (byte) (((b[i] & 0x0F) << 4) | ((b[i] & 0xF0) >> 4));
        }

        return csUTF16LE.decode(ByteBuffer.wrap(b)).toString().getBytes(StandardCharsets.UTF_8);
    }

    public TdsVersion getTdsVersion()
    {
        return tdsVersion;
    }

    public GenDWord getPacketSize()
    {
        return packetSize;
    }

    public GenDWord getClientProgVer()
    {
        return clientProgVer;
    }

    public GenDWord getClientPID()
    {
        return clientPID;
    }

    public GenDWord getConnectionID()
    {
        return clientConnID;
    }

    public ByteOrder getByteOrder()
    {
        return byteOrder;
    }

    public CharSet getCharSet()
    {
        return charSet;
    }

    public FloatRepresentation getFloatRep()
    {
        return floatRep;
    }

    public boolean supportDumpLoad()
    {
        return supportDumpLoad;
    }

    public boolean warnUseDB()
    {
        return warnUseDB;
    }

    public boolean needInitDatabase()
    {
        return needInitDatabase;
    }

    public boolean warnSetLang()
    {
        return warnSetLang;
    }

    public boolean needInitLanguage()
    {
        return needInitLanguage;
    }

    public boolean isODBC()
    {
        return isODBC;
    }

    public UserType getUserType()
    {
        return userType;
    }

    public boolean intSecurity()
    {
        return intSecurity;
    }

    public SqlInterface getSqlInterface()
    {
        return sqlInterface;
    }

    public boolean isOLEDB()
    {
        return isOLEDB;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public boolean changePoassword()
    {
        return changePoassword;
    }

    public boolean sendYukonBinaryXML()
    {
        return sendYukonBinaryXML;
    }

    public boolean userInstance()
    {
        return userInstance;
    }

    public boolean unknownCollationHandling()
    {
        return unknownCollationHandling;
    }

    public boolean useExtension()
    {
        return useExtension;
    }

    public ClientLCID getClientLCID()
    {
        return clientLCID;
    }

    public String getHostName()
    {
        return hostName;
    }

    public String getUserName()
    {
        return userName;
    }

    public byte[] getPassword()
    {
        return password;
    }

    public String getAppName()
    {
        return appName;
    }

    public String getServerName()
    {
        return serverName;
    }

    public GenDWord getExtension()
    {
        return extOffset;
    }

    public String getLibraryName()
    {
        return libraryName;
    }

    public String getInitLanguage()
    {
        return initLanguage;
    }

    public String getInitDatabase()
    {
        return initDatabase;
    }

    public byte[] getClientID()
    {
        return clientID;
    }

    public byte[] getSSPI()
    {
        return SSPI;
    }

    public String getAttachDBFile()
    {
        return attachDBFile;
    }

    public byte[] getNewPassword()
    {
        return newPassword;
    }

    public FedAuth getFedAuth()
    {
        return fedAuth;
    }

    public static class ClientLCID
            extends Collation
    {
        /**
         * There is no support for SQL Sort orders.
         */
        @Deprecated
        @Override
        public SortId getSortId()
        {
            return null;
        }

        public static ClientLCID fromDWord(GenDWord value)
                throws EnumValueLookupException
        {
            ClientLCID lcid = new ClientLCID();
            lcid.lcid = LCID.valueOf(new GenDWord((int) (value.longValue() & 0xFFFFF)));
            int flags = (int) ((value.longValue() >> 20) & 0xFFF);
            lcid.ignoreCase = ((flags & 0x01) == 1);
            lcid.ignoreAccent = ((flags & 0x02) == 1);
            lcid.ignoreWidth = ((flags & 0x04) == 1);
            lcid.ignoreKana = ((flags & 0x08) == 1);
            lcid.binary = ((flags & 0x10) == 1);
            lcid.binary2 = ((flags & 0x20) == 1);
            lcid.utf8 = ((flags & 0x40) == 1);
            lcid.version = (byte) (flags & 0x0780);
            return lcid;
        }
    }

    public abstract static class FedAuth
    {
        private boolean echo;

        public FedAuth()
        {
        }

        private FedAuth(boolean echo)
        {
            this.echo = echo;
        }

        /**
         * Read {@code FedAuth} from {@code in}.
         *
         * @param in
         * @param len number of bytes to read
         * @return
         * @throws PacketRWException
         * @throws IOException
         */
        public static FedAuth read(PacketDataInput in, int len)
                throws PacketRWException, IOException
        {
            FedAuth ret = null;

            byte b = in.read();
            len--;
            Library lib = Library.valueOf(new GenUByte((byte) (b >>> 1)));
            switch (lib) {
                case LIVE_ID_COMPACT_TOKEN:
                    ret = new LiveIdCompactTokenFedAuth((b & 1) != 0);
                    break;
                case SECURITY_TOKEN:
                    ret = new SecurityTokenFedAuth((b & 1) != 0);
                    break;
                case ADAL:
                    ret = new ADALFedAuth((b & 1) != 0);
                    break;
                default:
                    throw new PacketRWException("Unsupported %s library %s.", FedAuth.class.getSimpleName(), lib);
            }

            ret.readMore(in, len);

            return ret;
        }

        /**
         * Read more data from {@code in} using sub class specific rules.
         *
         * @param in
         * @param len number of bytes to read
         * @throws IOException
         * @throws PacketRWException
         */
        protected abstract void readMore(PacketDataInput in, int len)
                throws PacketRWException, IOException;

        public abstract Library getLibrary();

        public boolean isEcho()
        {
            return echo;
        }

        public enum Library
        {
            LIVE_ID_COMPACT_TOKEN((byte) 0x00), SECURITY_TOKEN((byte) 0x01), ADAL((byte) 0x02);

            private final GenUByte value;

            Library(byte value)
            {
                this.value = new GenUByte(value);
            }

            public GenUByte getValue()
            {
                return this.value;
            }

            private static final Map<GenUByte, Library> m = new HashMap<>();

            static {
                for (Library lib : Library.values()) {
                    m.put(lib.value, lib);
                }
            }

            public static Library valueOf(GenUByte value)
                    throws EnumValueLookupException
            {
                Library lib = m.get(value);
                if (lib == null) {
                    throw new EnumValueLookupException(Library.class, value);
                }
                return lib;
            }

            @Override
            public String toString()
            {
                return String.format("%s.%s.%s.%s(0x%02X)", Login7.class.getSimpleName(), FedAuth.class.getSimpleName(),
                        Library.class.getSimpleName(), this.name(), this.value.shortValue());
            }
        }
    }

    public static class LiveIdCompactTokenFedAuth
            extends FedAuth
    {
        private GenLVarByte token;
        private byte[] nonce;
        private GenByteStream channelBindingStream;
        private byte[] signature;

        public LiveIdCompactTokenFedAuth()
        {
            super();
        }

        private LiveIdCompactTokenFedAuth(boolean echo)
        {
            super(echo);
        }

        @Override
        protected void readMore(PacketDataInput in, int len)
                throws PacketRWException, IOException
        {
            this.token = in.readGenLVarByte();
            len -= this.token.getBytes().length;
            this.nonce = in.readBytes(32);
            len -= this.nonce.length;
            if (len - 32 > 0) {
                this.channelBindingStream = in.readGenByteStream(len - 32);
                len -= this.channelBindingStream.getBytes().length;
            }
            this.signature = in.readBytes(32);
            len -= this.signature.length;
        }

        @Override
        public Library getLibrary()
        {
            return Library.LIVE_ID_COMPACT_TOKEN;
        }

        public GenLVarByte getToken()
        {
            return token;
        }

        public byte[] getNonce()
        {
            return nonce;
        }

        public GenByteStream getChannelBindingStream()
        {
            return channelBindingStream;
        }

        public byte[] getSignature()
        {
            return signature;
        }
    }

    public static class SecurityTokenFedAuth
            extends FedAuth
    {
        private GenLVarByte token;
        private byte[] nonce;

        public SecurityTokenFedAuth()
        {
            super();
        }

        private SecurityTokenFedAuth(boolean echo)
        {
            super(echo);
        }

        @Override
        protected void readMore(PacketDataInput in, int len)
                throws PacketRWException, IOException
        {
            this.token = in.readGenLVarByte();
            len -= this.token.getBytes().length;
            if (len == 32) {
                this.nonce = in.readBytes(32);
                len -= this.nonce.length;
            }
        }

        @Override
        public Library getLibrary()
        {
            return Library.SECURITY_TOKEN;
        }

        public GenLVarByte getToken()
        {
            return token;
        }

        public byte[] getNonce()
        {
            return nonce;
        }
    }

    public static class ADALFedAuth
            extends FedAuth
    {
        private WorkFlow workFlow;

        private ADALFedAuth()
        {
            super();
        }

        private ADALFedAuth(boolean echo)
        {
            super(echo);
        }

        @Override
        protected void readMore(PacketDataInput in, int len)
                throws PacketRWException, IOException
        {
            this.workFlow = WorkFlow.valueOf(in.readGenUByte());
        }

        @Override
        public Library getLibrary()
        {
            return Library.ADAL;
        }

        public WorkFlow getWorkFlow()
        {
            return workFlow;
        }

        /**
         * Refer to <a href=
         * "https://github.com/microsoft/mssql-jdbc/blob/dev/src/main/java/com/microsoft/sqlserver/jdbc/SQLServerConnection.java">SQLServerConnection</a>$FederatedAuthenticationFeatureExtensionData
         *
         * @author user
         */
        public enum WorkFlow
        {
            PASSWORD_SERVICE_PRINCIPAL((byte) 0x01),
            INTEGRATED((byte) 0x02),
            MSI_INTERACTIVE((byte) 0x03);

            private final GenUByte value;

            WorkFlow(byte value)
            {
                this.value = new GenUByte(value);
            }

            public GenUByte getValue()
            {
                return this.value;
            }

            private static final Map<GenUByte, WorkFlow> m = new HashMap<>();

            static {
                for (WorkFlow lib : WorkFlow.values()) {
                    m.put(lib.value, lib);
                }
            }

            public static WorkFlow valueOf(GenUByte value)
                    throws EnumValueLookupException
            {
                WorkFlow lib = m.get(value);
                if (lib == null) {
                    throw new EnumValueLookupException(WorkFlow.class, value);
                }
                return lib;
            }

            @Override
            public String toString()
            {
                return String.format("%s.%s.%s.%s(0x%02X)", Login7.class.getSimpleName(),
                        ADALFedAuth.class.getSimpleName(), WorkFlow.class.getSimpleName(), this.name(),
                        this.value.shortValue());
            }
        }
    }
}
