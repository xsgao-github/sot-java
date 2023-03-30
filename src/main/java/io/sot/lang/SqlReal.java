package io.sot.lang;

/**
 * <p>
 * {@code SqlReal} system data type value. Follows the 32-bit [IEEE754] binary specification and is represented in reverse byte
 * order (little-endian).
 * </p>
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/0473264e-26ca-4969-b2db-905a05bb9cca">Floating-Point
 * Numbers</a>
 * </p>
 * <p>
 * Also refers <code>real</code> in
 * <a href="https://docs.microsoft.com/en-us/sql/t-sql/data-types/data-types-transact-sql?view=sql-server-2017">Data
 * types (Transact-SQL)</a>
 * </p>
 *
 * @author user
 */
public class SqlReal
        extends SqlDataValue
{
    public static final byte LENGTH = 4;

    float x;

    public SqlReal()
    {
    }

    public SqlReal(float value)
    {
        this.x = value;
    }

    public void setValue(float value)
    {
        this.x = value;
    }

    public float floatValue()
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
        result = prime * result + Float.floatToIntBits(x);
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
        SqlReal other = (SqlReal) obj;
        return Float.floatToIntBits(x) == Float.floatToIntBits(other.x);
    }

    @Override
    public String toString()
    {
        return String.valueOf(x);
    }
}
