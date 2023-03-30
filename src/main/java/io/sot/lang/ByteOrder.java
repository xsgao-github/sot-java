package io.sot.lang;

/**
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/773a62b6-ee89-4c02-9e5e-344882630aac">LOGIN7</a>.
 * <p>
 * <p>
 * fByteOrder: The byte order used by client for numeric and datetime data types.
 * <p>
 * <ul>
 * <li>0 = ORDER_X86</li>
 * <li>1 = ORDER_68000</li>
 * </ul>
 *
 * @author user
 */
public enum ByteOrder
{
    ORDER_X86,
    /* The value "1" for fByteOrder is supported only by SQL Server 7.0. */
    ORDER_68000
}
