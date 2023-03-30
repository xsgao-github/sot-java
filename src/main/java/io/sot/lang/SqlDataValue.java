package io.sot.lang;

/**
 * <p>
 * System pre-defined data type.
 * </p>
 * <p>
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/5773bd3e-a8cf-45cc-a058-3fd3ec3a8aff">System
 * Data Type Values</a> and subsections.
 * </p>
 * <p>
 * Also refers to
 * <a href="https://docs.microsoft.com/en-us/sql/t-sql/data-types/data-types-transact-sql?view=sql-server-2017">Data
 * types (Transact-SQL)</a>
 * </p>
 *
 * @author user
 */
public abstract class SqlDataValue
{
    public SqlDataValue()
    {
    }

    public abstract String stringValue();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract String toString();
}
