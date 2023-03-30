package io.sot.lang;

/**
 * {@linkplain EnumValueLookupException} indicates that looking up by long value yield no matching enum.
 *
 * @author user
 */
public class EnumValueLookupException
        extends PacketRWException
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public EnumValueLookupException(Class<?> cls, GenInteger<?> value)
    {
        super(String.format("Invalid %s value 0x%0" + value.getLength() * 2 + "X.", cls.getSimpleName(),
                value.longValue()));
    }

    public EnumValueLookupException(Class<?> pCls, Class<?> cls, GenInteger<?> value)
    {
        super(String.format("Invalid %s.%s value 0x%0" + value.getLength() * 2 + "X.", pCls.getSimpleName(),
                cls.getSimpleName(), value.longValue()));
    }

    public EnumValueLookupException(Class<?> cls, byte value)
    {
        super(String.format("Invalid %s value 0x%02X", cls.getSimpleName(), value));
    }

    public EnumValueLookupException(Class<?> pCls, Class<?> cls, byte value)
    {
        super(String.format("Invalid %s.%s value 0x%02X", pCls.getSimpleName(), cls.getSimpleName(), value));
    }

    public EnumValueLookupException(Class<?> cls, short value)
    {
        super(String.format("Invalid %s value 0x%04X", cls.getSimpleName(), value));
    }

    public EnumValueLookupException(Class<?> pCls, Class<?> cls, short value)
    {
        super(String.format("Invalid %s.%s value 0x%04X", pCls.getSimpleName(), cls.getSimpleName(), value));
    }

    public EnumValueLookupException(Class<?> cls, int value)
    {
        super(String.format("Invalid %s value 0x%04X", cls.getSimpleName(), value));
    }

    public EnumValueLookupException(Class<?> pCls, Class<?> cls, int value)
    {
        super(String.format("Invalid %s.%s value 0x%04X", pCls.getSimpleName(), cls.getSimpleName(), value));
    }

    public EnumValueLookupException(Class<?> cls, long value)
    {
        super(String.format("Invalid %s value 0x%08X", cls.getSimpleName(), value));
    }

    public EnumValueLookupException(Class<?> pCls, Class<?> cls, long value)
    {
        super(String.format("Invalid %s.%s value 0x%08X", pCls.getSimpleName(), cls.getSimpleName(), value));
    }
}
