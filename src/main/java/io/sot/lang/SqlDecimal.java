package io.sot.lang;

import java.math.BigDecimal;

/**
 * <p>
 * {@code SqlDecimal} system data type value.
 * </p>
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/5e02042c-a741-4b5a-b91d-af5e236c5252">Decimal/Numeric</a>
 * </p>
 * <p>
 * Also refers <code>decimal</code> in
 * <a href="https://docs.microsoft.com/en-us/sql/t-sql/data-types/data-types-transact-sql?view=sql-server-2017">Data
 * types (Transact-SQL)</a>
 * </p>
 *
 * @author user
 */
public class SqlDecimal
        extends SqlDataValue
{
    BigDecimal x;

    public SqlDecimal()
    {
    }

    public SqlDecimal(BigDecimal value)
    {
        this.x = value;
    }

    public void setValue(BigDecimal value)
    {
        this.x = value;
    }

    public BigDecimal bigDecimalValue()
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
        result = prime * result + ((x == null) ? 0 : x.hashCode());
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
        SqlDecimal other = (SqlDecimal) obj;
        if (x == null) {
            return other.x == null;
        }
        else {
            return x.equals(other.x);
        }
    }

    @Override
    public String toString()
    {
        return String.valueOf(x);
    }
}
