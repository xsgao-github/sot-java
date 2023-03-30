package io.sot.lang;

/**
 * <p>
 * {@code SqlInt} system data type value.
 * </p>
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/76425d61-416d-4c64-a60b-06072f83e180">Integers</a>
 * </p>
 * <p>
 * Also refers <code>int</code> in
 * <a href="https://docs.microsoft.com/en-us/sql/t-sql/data-types/data-types-transact-sql?view=sql-server-2017">Data
 * types (Transact-SQL)</a>
 * </p>
 *
 * @author user
 */
public class SqlInt
        extends SqlDataValue
{
    public static final byte LENGTH = 4;

    int x;

    public SqlInt()
    {
    }

    public SqlInt(int value)
    {
        this.x = value;
    }

    public void setValue(int value)
    {
        this.x = value;
    }

    public int intValue()
    {
        return x;
    }

    @Override
    public String stringValue()
    {
        return String.valueOf(x);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
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
        SqlInt other = (SqlInt) obj;
        return x == other.x;
    }

    @Override
    public String toString()
    {
        return String.valueOf(x);
    }
}
