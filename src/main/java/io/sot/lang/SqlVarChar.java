package io.sot.lang;

/**
 * <p>
 * {@code SqlChar} system data type value.
 * </p>
 * <p>
 * Defined in
 * <ul>
 * <li><a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/9bb849df-2a6e-49ff-96ed-5695e64cf898">Character
 * and Binary Strings</a></li>
 * <li><a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/ce3183a6-9d89-47e8-a02f-de5a1a1303de">Variable-Length
 * Data Types</a></li>
 * <li><a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/7d26a257-083e-409b-81ba-897e0c672be0">Partially
 * Length-Prefixed Data Types</a></li>
 * </ul>
 * <p>
 * </p>
 * <p>
 * Also refers <code>varchar</code> in
 * <a href="https://docs.microsoft.com/en-us/sql/t-sql/data-types/data-types-transact-sql?view=sql-server-2017">Data
 * types (Transact-SQL)</a>
 * </p>
 *
 * @author user
 */
public class SqlVarChar
        extends SqlDataValue
{
    String x;

    public SqlVarChar()
    {
    }

    public SqlVarChar(String value)
    {
        this.x = value;
    }

    public void setValue(String value)
    {
        this.x = value;
    }

    @Override
    public String stringValue()
    {
        return x;
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
        SqlVarChar other = (SqlVarChar) obj;
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
        return x == null ? "null" : "'" + x.replaceAll("'", "''") + "'";
    }
}
