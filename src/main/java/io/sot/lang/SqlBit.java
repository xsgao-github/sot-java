package io.sot.lang;

/**
 * <p>
 * {@code SqlBit} system data type value.
 * </p>
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/76425d61-416d-4c64-a60b-06072f83e180">Integers</a>
 * </p>
 * <p>
 * Also refers <code>bit</code> in
 * <a href="https://docs.microsoft.com/en-us/sql/t-sql/data-types/data-types-transact-sql?view=sql-server-2017">Data
 * types (Transact-SQL)</a>
 * </p>
 *
 * @author user
 */
public class SqlBit
        extends SqlDataValue
{
    public static final byte LENGTH = 1;

    boolean x;

    public SqlBit()
    {
        super();
    }

    public SqlBit(boolean value)
    {
        this.x = value;
    }

    public SqlBit(byte value)
    {
        this.x = value != 0;
    }

    public void setValue(boolean value)
    {
        this.x = value;
    }

    public void setValue(byte value)
    {
        this.x = value != 0;
    }

    public boolean booleanValue()
    {
        return x;
    }

    public byte byteValue()
    {
        return (byte) (x ? 1 : 0);
    }

    @Override
    public String stringValue()
    {
        return x ? "1" : "0";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (x ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SqlBit other = (SqlBit) obj;
        return x == other.x;
    }

    @Override
    public String toString()
    {
        return x ? "1" : "0";
    }
}
