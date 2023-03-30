package io.sot.lang;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * <p>
 * {@code SqlTimel} system data type value.
 * </p>
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/786f5b8a-f87d-4980-9070-b9b7274c681d">Dates and Times</a>
 * </p>
 * <p>
 * Also refers <code>time</code> in
 * <a href="https://docs.microsoft.com/en-us/sql/t-sql/data-types/data-types-transact-sql?view=sql-server-2017">Data
 * types (Transact-SQL)</a>
 * </p>
 *
 * @author user
 */
public class SqlTime
        extends SqlDataValue
{
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss.S");

    Time x;

    public SqlTime()
    {
    }

    public SqlTime(Time value)
    {
        this.x = value;
    }

    public void setValue(Time value)
    {
        this.x = value;
    }

    public Time timeValue()
    {
        return x;
    }

    @Override
    public String stringValue()
    {
        return x == null ? "null" : "'" + TIME_FORMAT.format(x) + "'";
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
        SqlTime other = (SqlTime) obj;
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
