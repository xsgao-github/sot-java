package io.sot.lang;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * <p>
 * {@code SqlDateTime} system data type value.
 * </p>
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/786f5b8a-f87d-4980-9070-b9b7274c681d">Dates and
 * Times</a>
 * </p>
 * <p>
 * Also refers <code>datetime</code>, <tt>datetime2</tt>, <tt>smalldatetime</tt> and <tt>datetimeoffset</tt> in
 * <a href="https://docs.microsoft.com/en-us/sql/t-sql/data-types/data-types-transact-sql?view=sql-server-2017">Data
 * types (Transact-SQL)</a>
 * </p>
 *
 * @author user
 */
public class SqlDatetime
        extends SqlDataValue
{
    private static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

    Timestamp x;

    public SqlDatetime()
    {
    }

    public SqlDatetime(Timestamp value)
    {
        this.x = value;
    }

    public void setValue(Timestamp value)
    {
        this.x = value;
    }

    public Timestamp timestampValue()
    {
        return x;
    }

    @Override
    public String stringValue()
    {
        return x == null ? "null" : "'" + TIMESTAMP_FORMAT.format(x) + "'";
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
        SqlDatetime other = (SqlDatetime) obj;
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
