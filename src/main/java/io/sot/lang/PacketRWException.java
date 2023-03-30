package io.sot.lang;

/**
 * {@code PacketRWException} represents a failure of reading data from inbound packet/client request, or of writing data
 * to outbound packet/server response..
 * <p>
 * It should only be caused by a malformed TDS read/write error. Any other exceptions, e.g. <code>IOException</code>,
 * should not cause {@linkplain PacketRWException}.
 *
 * @author user
 */
public class PacketRWException
        extends Exception
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public PacketRWException()
    {
        super();
    }

    public PacketRWException(String fmt, Object... args)
    {
        super(String.format(fmt, args));
    }

    public PacketRWException(Throwable th)
    {
        super(th);
    }

    public PacketRWException(Throwable th, String fmt, Object... args)
    {
        super(String.format(fmt, args), th);
    }
}
