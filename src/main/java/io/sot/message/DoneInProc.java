package io.sot.message;

import io.sot.Session;

/**
 * Indicates the completion status of a stored procedure. This is also generated for stored procedures executed through
 * SQL statements.
 * <p>
 * <p>
 * <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/65e24140-edea-46e5-b710-209af2016195">DONE_PROC</a>
 * <p>
 *
 * @author user
 */
public class DoneInProc
        extends AbstractDone
{
    public DoneInProc(Session session)
    {
        super(session);
    }

    @Override
    public Token getToken()
    {
        return Token.DONE_IN_PROC;
    }
}
