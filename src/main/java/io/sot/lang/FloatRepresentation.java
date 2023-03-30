package io.sot.lang;

/**
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/773a62b6-ee89-4c02-9e5e-344882630aac">LOGIN7</a>.
 * <p>
 * <p>
 * fFloat: The type of floating point representation used by the client.
 * <ul>
 * <li>0 = FLOAT_IEEE_754</li>
 * <li>1 = FLOAT_VAX</li>
 * <li>2 = ND5000</li>
 * </ul>
 *
 * @author user
 */
public enum FloatRepresentation
{
    FLOAT_IEEE_754, FLOAT_VAX, ND5000
}
