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
 * Used to send an information message to the client.
 *
 * <p>
 * <p>
 * <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/284bb815-d083-4ed5-b33a-bdc2492e322b">INFO</a>
 * <p>
 * <p>
 * <a href=
 * "https://docs.microsoft.com/en-us/sql/relational-databases/errors-events/database-engine-events-and-errors?view=sql-server-2017">Database
 * engine errors</a>
 * <p>
 *
 * @author user
 */
public class Info
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

    private Info(Session session, GenLong number, byte state, byte cls, String msg)
    {
        super(session);
        this.number = number;
        this.state = state;
        this.cls = cls;
        this.msgText = new GenUSVarChar(msg);
        this.serverName = session.getServerName();
        this.procName = new GenBVarChar("");
        this.lineNumber = new GenLong(1);
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
        out.write(getToken().getValue());
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
        return Token.INFO;
    }

    @Override
    public String toString()
    {
        return this.msgText == null ? "null" : this.msgText.getString();
    }

    private static final Map<Number, Template> TEMPLATES = new HashMap<>();

    static {
        TEMPLATES.put(Number.GENERAL_MESSAGE, new Template(5000, (byte) 10,
                "General messag: %s."));
        TEMPLATES.put(Number.CHANGE_DATABASE, new Template(5701, (byte) 10,
                "Changed database context to '%s'."));
        TEMPLATES.put(Number.CHANGE_LANGUAGE, new Template(5703, (byte) 10,
                "Changed language setting to '%S'."));
    }

    public static Info getInstance(Session session, Number number, byte state, Object... msgParams)
    {
        Template tmpl = TEMPLATES.get(number);
        if (tmpl == null) {
            tmpl = TEMPLATES.get(Number.GENERAL_MESSAGE);
        }

        Object[] args = new Object[10];
        Arrays.fill(args, "");
        System.arraycopy(msgParams, 0, args, 0, msgParams.length);
        return new Info(session, tmpl.number, state, tmpl.cls, String.format(tmpl.msgTmpl, args));
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
        GENERAL_MESSAGE, CHANGE_DATABASE, CHANGE_LANGUAGE,
    }
}
