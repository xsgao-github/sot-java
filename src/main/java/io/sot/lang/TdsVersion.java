package io.sot.lang;

import java.util.HashMap;
import java.util.Map;

/**
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/135d0ebe-5c4c-4a94-99bf-1811eccb9f4a#Appendix_A_60">Appendix
 * A: Product Behavior</a>
 * <p>
 * <p>
 * &lt;60&gt; Section 2.2.7.14: The following table shows the values in network transfer format.</a>
 * <p>
 *
 * @author user
 */
public enum TdsVersion
{
    TDS70(new GenDWord(0x07000000), "SQL Server 7.0"),
    TDS71(new GenDWord(0x07010000), "SQL Server 2000"),
    TDS71_SP1(new GenDWord(0x71000001), "SQL Server 2000 SP1"),
    TDS729(new GenDWord(0x72090002), "SQL Server 2005"),
    TDS73A(new GenDWord(0x730A0003), "SQL Server 2008"),
    TDS73B(new GenDWord(0x730B0003), "SQL Server 2008 R2"),
    TDS74(new GenDWord(0x74000004), "SQL Server 2012+");

    private final GenDWord value;
    private final String dscription;

    TdsVersion(GenDWord serverValue, String sqlServerVersion)
    {
        this.value = serverValue;
        this.dscription = sqlServerVersion;
    }

    public GenDWord getValue()
    {
        return value;
    }

    public String getDescription()
    {
        return dscription;
    }

    private static final Map<GenDWord, TdsVersion> value2Version = new HashMap<>();

    static {
        for (TdsVersion t : TdsVersion.values()) {
            value2Version.put(t.value, t);
        }
    }

    public static TdsVersion valueOf(GenDWord value)
            throws EnumValueLookupException
    {
        TdsVersion t = value2Version.get(value);
        if (t == null) {
            throw new EnumValueLookupException(TdsVersion.class, value);
        }
        return t;
    }

    @Override
    public String toString()
    {
        return String.format("%s(0x%08X)", this.name(), this.value.longValue());
    }
}
