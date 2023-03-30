package io.sot.message;

import io.sot.Session;
import io.sot.lang.GenBVarChar;
import io.sot.lang.GenLong;
import io.sot.lang.GenUSVarChar;
import io.sot.lang.GenUShort;
import io.sot.lang.PacketDataOutput;
import io.sot.lang.PacketDataWriter;
import io.sot.lang.PacketRWException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to send an error message to the client.
 * <p>
 * <p>
 * <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/9805e9fa-1f8b-4cf8-8f78-8d2602228635">ERROR</a>
 * <p>
 * <p>
 * <a href=
 * "https://docs.microsoft.com/en-us/sql/relational-databases/errors-events/database-engine-events-and-errors?view=sql-server-2017">Database
 * engine errors</a>
 * <p>
 *
 * @author user
 */
public class ErrorStream
        extends TokenStream
        implements PacketDataWriter
{
    private GenUShort length = new GenUShort((short) 0);
    private GenLong number;
    private byte state;
    private byte cls;
    private GenUSVarChar msgText;
    private GenBVarChar serverName;
    private GenBVarChar procName;
    private GenLong lineNumber;

    private ErrorStream(Session session, GenLong number, byte cls, String msg)
    {
        super(session);
        this.number = number;
        this.state = 0;
        this.cls = cls;
        this.msgText = new GenUSVarChar(msg);
        this.serverName = session.getServerName();
        this.procName = new GenBVarChar("");
        this.lineNumber = new GenLong(0);
    }

    public GenUShort getLength()
    {
        return length;
    }

    public void setLength(GenUShort length)
    {
        this.length = length;
    }

    public GenLong getNumber()
    {
        return number;
    }

    public void setNumber(GenLong number)
    {
        this.number = number;
    }

    public byte getState()
    {
        return state;
    }

    public void setState(byte state)
    {
        this.state = state;
    }

    public byte getCls()
    {
        return cls;
    }

    public void setCls(byte cls)
    {
        this.cls = cls;
    }

    public GenUSVarChar getMsgText()
    {
        return msgText;
    }

    public void setMsgText(GenUSVarChar msgText)
    {
        this.msgText = msgText;
    }

    public GenBVarChar getServerName()
    {
        return serverName;
    }

    public void setServerName(GenBVarChar serverName)
    {
        this.serverName = serverName;
    }

    public GenBVarChar getProcName()
    {
        return procName;
    }

    public void setProcName(GenBVarChar procName)
    {
        this.procName = procName;
    }

    public GenLong getLineNumber()
    {
        return lineNumber;
    }

    public void setLineNumber(GenLong lineNumber)
    {
        this.lineNumber = lineNumber;
    }

    @Override
    public void write(PacketDataOutput out)
            throws PacketRWException, IOException
    {
        out.write(Token.ERROR.getValue());
        int lenIndex = out.getPos();
        out.write(length);
        out.write(number);
        out.write(state);
        out.write(cls);
        out.write(msgText);
        out.write(serverName);
        out.write(procName);
        out.write(lineNumber);

        // update length
        out.write(lenIndex, new GenUShort((short) (out.size() - lenIndex - GenUShort.LENGTH)));
    }

    @Override
    public Token getToken()
    {
        return Token.ERROR;
    }

    @Override
    public String toString()
    {
        return this.msgText == null ? "null" : this.msgText.getString();
    }

    private static final Map<Number, Template> TEMPLATES = new HashMap<>();

    static {
        TEMPLATES.put(Number.INVALID_PRELOGIN, new Template(17828, (byte) 20,
                "The prelogin packet used to open the connection is structurally invalid; the connection has been closed."));
        TEMPLATES.put(Number.INVALID_NETOTIATE_HEADER, new Template(11247, (byte) 16,
                "A corrupted message has been received. The login negotiate header is invalid."));
        TEMPLATES.put(Number.INVALID_SSPI_HEADER, new Template(11248, (byte) 16,
                "A corrupted message has been received. The SSPI login header is invalid."));
        TEMPLATES.put(Number.WINDOWS_LOGIN_SUCCEEDED, new Template(18453, (byte) 10,
                "Login succeeded for user '%1$s'. Connection made using Windows Authentication."));
        TEMPLATES.put(Number.SQL_LOGIN_SUCCEEDED, new Template(18454, (byte) 10,
                "Login succeeded for user '%1$s'. Connection made using SQL Server Authentication."));
        TEMPLATES.put(Number.LOGIN_FAILED, new Template(18456, (byte) 14, "Login failed for user '%1$s'."));
        TEMPLATES.put(Number.POST_LOGIN_FAILED, new Template(15375, (byte) 16,
                "Failed to generate a user instance of SQL Server due to a failure in making a connection to the user instance. The connection will be closed."));
        TEMPLATES.put(Number.NETWORK_FATAL_ERROR, new Template(4014, (byte) 20,
                "A fatal error occurred while reading the input stream from the network. The session will be terminated (input error: %1$s, output error: %2$s."));
        TEMPLATES.put(Number.INCORRECT_INCOMING_TDS, new Template(4002, (byte) 16,
                "The incoming tabular data stream (TDS) protocol stream is incorrect."));
        TEMPLATES.put(Number.QUERY_EXECUTION_FAILED, new Template(14661, (byte) 16,
                "Query execution failed: %1$s."));
        TEMPLATES.put(Number.TEMPDB_IS_SKIPPED, new Template(949, (byte) 16,
                "tempdb is skipped. You cannot run a query that requires tempdb."));
    }

    public static ErrorStream getInstance(Session session, Number number, Object... msgParams)
    {
        Template tmpl = TEMPLATES.get(number);
        if (tmpl == null) {
            tmpl = TEMPLATES.get(Number.QUERY_EXECUTION_FAILED);
        }

        Object[] args = new Object[10];
        Arrays.fill(args, "");
        System.arraycopy(msgParams, 0, args, 0, msgParams.length);
        return new ErrorStream(session, tmpl.number, tmpl.cls, String.format(tmpl.msgTmpl, args));
    }

    private static class Template
    {
        private final GenLong number;
        private final byte cls;
        private final String msgTmpl;

        private Template(int number, byte cls, String msgTmpl)
        {
            this.number = new GenLong(number);
            this.cls = cls;
            this.msgTmpl = msgTmpl;
        }
    }

    public enum Number
    {
        INVALID_PRELOGIN,
        INVALID_NETOTIATE_HEADER,
        INVALID_SSPI_HEADER,
        WINDOWS_LOGIN_SUCCEEDED,
        SQL_LOGIN_SUCCEEDED,
        LOGIN_FAILED,
        POST_LOGIN_FAILED,
        NETWORK_FATAL_ERROR,
        INCORRECT_INCOMING_TDS,
        QUERY_EXECUTION_FAILED,
        TEMPDB_IS_SKIPPED,
    }
}
