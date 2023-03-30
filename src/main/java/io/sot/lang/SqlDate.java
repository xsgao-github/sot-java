package io.sot.lang;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * <p>
 * {@code SqlDate} system data type value.
 * </p>
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/786f5b8a-f87d-4980-9070-b9b7274c681d">Dates and Times</a>
 * </p>
 * <p>
 * Also refers <code>date</code> in
 * <a href="https://docs.microsoft.com/en-us/sql/t-sql/data-types/data-types-transact-sql?view=sql-server-2017">Data
 * types (Transact-SQL)</a>
 * </p>
 *
 * @author user
 */
public class SqlDate
        extends SqlDataValue
{
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    Date x;

    public SqlDate()
    {
    }

    public SqlDate(Date value)
    {
        this.x = value;
    }

    public void setValue(Date value)
    {
        this.x = value;
    }

    public Date dateValue()
    {
        return x;
    }

    @Override
    public String stringValue()
    {
        return x == null ? "null" : "'" + DATE_FORMAT.format(x) + "'";
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
        SqlDate other = (SqlDate) obj;
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
