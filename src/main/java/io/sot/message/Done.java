package io.sot.message;

import io.sot.Session;

/**
 * Indicates the completion status of a SQL statement.
 * <p>
 * <p>
 * <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/3c06f110-98bd-4d5b-b836-b1ba66452cb7">DONE</a>
 * <p>
 *
 * @author user
 */
public class Done
        extends AbstractDone
{
    public Done(Session session)
    {
        super(session);
    }

    @Override
    public Token getToken()
    {
        return Token.DONE;
    }
}
