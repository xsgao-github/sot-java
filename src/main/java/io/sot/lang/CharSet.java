package io.sot.lang;

/**
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/773a62b6-ee89-4c02-9e5e-344882630aac">LOGIN7</a>.
 * <p>
 * <p>
 * fChar: The character set used on the client.
 * <p>
 * <ul>
 * <li>0 = CHARSET_ASCII</li>
 * <li>1 = CHARSET_EBCDIC</li>
 * </ul>
 *
 * @author user
 */
public enum CharSet
{
    CHARSET_ASCII, CHARSET_EBCDIC
}
