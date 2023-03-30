package io.sot.message;

import io.sot.Session;
import io.sot.lang.AllHeaders;
import io.sot.lang.GenUnicodeStream;
import io.sot.lang.PacketDataInput;
import io.sot.lang.PacketDataReader;
import io.sot.lang.PacketRWException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * Describes the format of the SQL Batch message.
 * <p>
 * <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/f2026cd3-9a46-4a3f-9a08-f63140bcbbe3">SQLBatch</a>
 *
 * @author user
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "sql-batch")
@XmlType(propOrder = {"sqlText"})
public class SqlBatch
        implements PacketDataReader
{
    @SuppressWarnings("unused")
    private final Session session;

    private AllHeaders allHeaders;

    @XmlElement(name = "statement")
    @XmlJavaTypeAdapter(GenUnicodeStreamAdapter.class)
    private GenUnicodeStream sqlText;

    public SqlBatch()
    {
        this.session = null;
    }

    public SqlBatch(Session session)
    {
        this.session = session;
    }

    public AllHeaders getAllHeaders()
    {
        return allHeaders;
    }

    public void setAllHeaders(AllHeaders allHeaders)
    {
        this.allHeaders = allHeaders;
    }

    public GenUnicodeStream getSqlText()
    {
        return sqlText;
    }

    public void setSqlText(GenUnicodeStream sqlText)
    {
        this.sqlText = sqlText;
    }

    @Override
    public void read(PacketDataInput data)
            throws PacketRWException, IOException
    {
        this.allHeaders = new AllHeaders();
        allHeaders.read(data);
        this.sqlText = data.readGenUniCodeStream();
    }

    public String toXml()
    {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            JAXBContext context = JAXBContext.newInstance(SqlBatch.class);
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

    public static class GenUnicodeStreamAdapter
            extends XmlAdapter<String, GenUnicodeStream>
    {
        @Override
        public GenUnicodeStream unmarshal(String str)
                throws Exception
        {
            throw new UnsupportedEncodingException();
        }

        @Override
        public String marshal(GenUnicodeStream v)
                throws Exception
        {
            return v == null ? null : v.getString();
        }
    }
}
