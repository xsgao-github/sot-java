package io.sot.message;

import io.sot.Session;

/**
 * Indicates the completion status of a SQL statement within a stored procedure.
 * <p>
 * <p>
 * <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/43e891c5-f7a1-432f-8f9f-233c4cd96afb">DONE_IN_PROC</a>
 * <p>
 *
 * @author user
 */
public class DoneProc
        extends AbstractDone
{
    public DoneProc(Session session)
    {
        super(session);
    }

    @Override
    public Token getToken()
    {
        return Token.DONE_PROC;
    }
}
