package io.sot.message;

import io.sot.Session;
import io.sot.lang.AllHeaders;
import io.sot.lang.EnumValueLookupException;
import io.sot.lang.GenBVarChar;
import io.sot.lang.GenUSVarChar;
import io.sot.lang.GenUShort;
import io.sot.lang.PacketDataInput;
import io.sot.lang.PacketDataReader;
import io.sot.lang.PacketRWException;
import io.sot.lang.SqlDataValue;
import io.sot.lang.TypeInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Request to execute an RPC.
 * <p>
 * <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/619c43b6-9495-4a58-9e49-a4950db245b3">RPC
 * Request</a>
 *
 * @author user
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "rpc")
@XmlType(propOrder = {"procID", "procName", "params"})
public class RPC
        implements PacketDataReader
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LogManager.getLogger(RPC.class);

    private static final GenUShort PROC_ID_SWITCH = new GenUShort((short) 0xFFFF);

    @SuppressWarnings("unused")
    private final Session session;

    private AllHeaders allHeaders;

    @XmlAttribute(name = "proc-id")
    private ProcID procID;

    @XmlAttribute(name = "proc-name")
    @XmlJavaTypeAdapter(GenUSVarCharAdapter.class)
    private GenUSVarChar procName;

    private boolean withRecomp;

    private boolean noMetaData;

    private boolean reuseMetaData;

    @XmlElement(name = "params")
    private final List<ParameterData> params;

    // The EnclavePackage parameter is not supported by SQL Server 7.0, SQL Server 2000, SQL Server 2005, SQL Server
    // 2008, SQL Server 2008 R2, SQL Server 2012, SQL Server 2014, SQL Server 2016, and SQL Server 2017

    public RPC()
    {
        this.session = null;
        params = new ArrayList<>();
    }

    public RPC(Session session)
    {
        this.session = session;
        params = new ArrayList<>();
    }

    public ProcID getProcID()
    {
        return procID;
    }

    public GenUSVarChar getProcName()
    {
        return procName;
    }

    public boolean withRecomp()
    {
        return withRecomp;
    }

    public boolean noMetaData()
    {
        return noMetaData;
    }

    public boolean reuseMetaData()
    {
        return reuseMetaData;
    }

    public List<ParameterData> getParameters()
    {
        return params;
    }

    @Override
    public void read(PacketDataInput in)
            throws PacketRWException, IOException
    {
        allHeaders = new AllHeaders();
        allHeaders.read(in);

        GenUShort len = in.readGenUShort();
        if (len.equals(PROC_ID_SWITCH)) {
            procID = ProcID.valueOf(in.readGenUShort());
        }
        else {
            procName = new GenUSVarChar(in.readGenUniCodeStream(len.intValue()).getString());
        }

        short opts = (byte) in.readGenUShort().intValue();
        withRecomp = ((opts & 0x01) == 0x01);
        noMetaData = ((opts & 0x02) == 0x02);
        reuseMetaData = ((opts & 0x08) == 0x08);

        params.clear();

        // read rest stream as parameters
        while (in.hasMore()) {
            ParameterData p = new ParameterData();
            p.read(in, procID, params.size());
            params.add(p);
        }
    }

    public String toXml()
    {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            JAXBContext context = JAXBContext.newInstance(RPC.class);
            Marshaller mar = context.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            mar.marshal(this, out);
            return out.toString(StandardCharsets.UTF_8);
        }
        catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    public enum ProcID
    {
        Sp_Cursor(new GenUShort((short) 1)),
        Sp_CursorOpen(new GenUShort((short) 2)),
        Sp_CursorPrepare(new GenUShort((short) 3)),
        Sp_CursorExecute(new GenUShort((short) 4)),
        Sp_CursorPrepExec(new GenUShort((short) 5)),
        Sp_CursorUnprepare(new GenUShort((short) 6)),
        Sp_CursorFetch(new GenUShort((short) 7)),
        Sp_CursorOption(new GenUShort((short) 8)),
        Sp_CursorClose(new GenUShort((short) 9)),
        /**
         * <p>
         * <code>sp_executesql [@stmt =] statement [, [@params =] N'@parameter_name data_type [OUT|OUTPUT]*' [, [@param1 =] 'value1']* ]</code>
         * </p>
         * <p>
         * Defined in <a href=
         * "https://docs.microsoft.com/en-us/sql/relational-databases/system-stored-procedures/sp-executesql-transact-sql?view=sql-server-2016">sp_executesql</a>
         * </p>
         */
        Sp_ExecuteSql(new GenUShort((short) 10)),
        /**
         * <p>
         * <code>sp_prepare handle OUTPUT, params, stmt, options</code>
         * </p>
         * <p>
         * Defined in <a href=
         * "https://docs.microsoft.com/en-us/sql/relational-databases/system-stored-procedures/sp-prepare-transact-sql?view=sql-server-2016">sp_prepare</a>
         * </p>
         */
        Sp_Prepare(new GenUShort((short) 11)),
        /**
         * <p>
         * <code>sp_execute handle OUTPUT [, bound_param]*</code>
         * </p>
         * <p>
         * Defined in <a href=
         * "https://docs.microsoft.com/en-us/sql/relational-databases/system-stored-procedures/sp-execute-transact-sql?view=sql-server-2016">sp_execute</a>
         * </p>
         */
        Sp_Execute(new GenUShort((short) 12)),
        /**
         * <p>
         * <code>sp_prepexec handle OUTPUT, params, stmt [, bound_param]*</code>
         * </p>
         * <p>
         * Defined in <a href=
         * "https://docs.microsoft.com/en-us/sql/relational-databases/system-stored-procedures/sp-prepexec-transact-sql?view=sql-server-2016">sp_prepexec</a>
         */
        Sp_PrepExec(new GenUShort((short) 13)),
        /**
         * <p>
         * <code>sp_prepexecrpc handle OUTPUT, RPCCall [, bound_param]*</code>
         * </p>
         * <p>
         * Defined in <a href=
         * "https://docs.microsoft.com/en-us/sql/relational-databases/system-stored-procedures/sp-prepexecrpc-transact-sql?view=sql-server-2016">sp_prepexecrpc</a>
         * </p>
         */
        Sp_PrepExecRpc(new GenUShort((short) 14)),
        /**
         * <p>
         * <code>sp_unprepare handle</code>
         * </p>
         * <p>
         * Defined in <a href=
         * "https://docs.microsoft.com/en-us/sql/relational-databases/system-stored-procedures/sp-unprepare-transact-sql?view=sql-server-2016">sp_unprepare</a>
         * </p>
         */
        Sp_Unprepare(new GenUShort((short) 15));

        final GenUShort value;

        ProcID(GenUShort value)
        {
            this.value = value;
        }

        public GenUShort getValue()
        {
            return value;
        }

        private static final Map<GenUShort, ProcID> genUShortTypeMap = initGenUShortTypeMap();

        private static Map<GenUShort, ProcID> initGenUShortTypeMap()
        {
            Map<GenUShort, ProcID> m = new HashMap<>();
            for (ProcID p : ProcID.values()) {
                m.put(p.value, p);
            }
            return m;
        }

        public static ProcID valueOf(GenUShort value)
                throws EnumValueLookupException
        {
            ProcID p = genUShortTypeMap.get(value);
            if (p != null) {
                return p;
            }
            else {
                throw new EnumValueLookupException(RPC.class, RPC.ProcID.class, value);
            }
        }
    }

    @XmlAccessorType(XmlAccessType.NONE)
    @XmlRootElement(name = "param-data")
    @XmlType(propOrder = {"name", "typeInfo", "value"})
    public static class ParameterData
    {
        @XmlAttribute(name = "name")
        @XmlJavaTypeAdapter(GenBVarCharAdapter.class)
        private GenBVarChar name;

        private boolean byRefValue;

        private boolean defaultValue;

        private boolean encrypted;

        @XmlAttribute(name = "type")
        @XmlJavaTypeAdapter(TypeInfoAdapter.class)
        private TypeInfo typeInfo;

        @XmlAttribute(name = "value")
        @XmlJavaTypeAdapter(SqlDataValueAdapter.class)
        private SqlDataValue value;

        public ParameterData()
        {
        }

        public GenBVarChar getName()
        {
            return name;
        }

        public void setName(GenBVarChar name)
        {
            this.name = name;
        }

        public boolean isByRefValue()
        {
            return byRefValue;
        }

        public boolean isDefaultValue()
        {
            return defaultValue;
        }

        public boolean isEncrypted()
        {
            return encrypted;
        }

        public TypeInfo getTypeInfo()
        {
            return typeInfo;
        }

        public void setTypeInfo(TypeInfo typeInfo)
        {
            this.typeInfo = typeInfo;
        }

        public SqlDataValue getValue()
        {
            return value;
        }

        public void getValue(SqlDataValue value)
        {
            this.value = value;
        }

        public void read(PacketDataInput in, ProcID procID, int index)
                throws PacketRWException, IOException
        {
            name = in.readGenBVarChar();

            byte status = (byte) in.readGenUByte().shortValue();
            byRefValue = ((status & 0x01) == 0x01);
            defaultValue = ((status & 0x02) == 0x02);
            encrypted = ((status & 0x08) == 0x0F);

            typeInfo = new TypeInfo();
            typeInfo.read(in);

            value = in.read(typeInfo);
        }

        @Override
        public String toString()
        {
            return String.format("%s[ti = %s, value = %s]", getClass().getSimpleName(), typeInfo, value);
        }
    }

    public static class TypeInfoAdapter
            extends XmlAdapter<String, TypeInfo>
    {
        @Override
        public TypeInfo unmarshal(String str)
                throws Exception
        {
            throw new UnsupportedEncodingException();
        }

        @Override
        public String marshal(TypeInfo ti)
                throws Exception
        {
            return ti.getType().name();
        }
    }

    public static class SqlDataValueAdapter
            extends XmlAdapter<String, SqlDataValue>
    {
        @Override
        public SqlDataValue unmarshal(String str)
                throws Exception
        {
            throw new UnsupportedEncodingException();
        }

        @Override
        public String marshal(SqlDataValue v)
                throws Exception
        {
            return v == null ? null : v.stringValue();
        }
    }

    public static class GenBVarCharAdapter
            extends XmlAdapter<String, GenBVarChar>
    {
        @Override
        public GenBVarChar unmarshal(String str)
                throws Exception
        {
            throw new UnsupportedEncodingException();
        }

        @Override
        public String marshal(GenBVarChar v)
                throws Exception
        {
            return v == null ? null : v.getString();
        }
    }

    public static class GenUSVarCharAdapter
            extends XmlAdapter<String, GenUSVarChar>
    {
        @Override
        public GenUSVarChar unmarshal(String str)
                throws Exception
        {
            throw new UnsupportedEncodingException();
        }

        @Override
        public String marshal(GenUSVarChar v)
                throws Exception
        {
            return v == null ? null : v.getString();
        }
    }
}
