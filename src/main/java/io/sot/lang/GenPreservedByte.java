package io.sot.lang;

/**
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/d2ed21d6-527b-46ac-8035-94f6f68eb9a8">General
 * Rules</a>
 * <p>
 * <b>FRESERVEDBYTE:</b> A FRESERVEDBYTE is a BYTE value used for padding that does not transmit information.
 * FRESERVEDBYTE fields SHOULD be set to %x00 and MUST be ignored on receipt.
 * <p>
 * FRESERVEDBYTE = %x00
 *
 * @author user
 */
public class GenPreservedByte
        extends GenNull
{
    public static final GenPreservedByte NULL = new GenPreservedByte();

    protected GenPreservedByte()
    {
        super();
    }
}
