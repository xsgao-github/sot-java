/**
 *
 */
package io.sot.message;

import io.sot.Session;

/**
 * {@code TokenStream} represents the various token streams, defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/67b6113c-d722-42d1-902c-3f6e8de09173">Packet
 * Data Token Stream Definition</a>.
 * <p>
 * More complex messages (for example, colmetadata, row data, and data type data) are constructed by using tokens. As
 * previously described, a token stream consists of a single byte identifier, followed by token-specific data. The
 * definitions of the different token streams can be found in section <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/67b6113c-d722-42d1-902c-3f6e8de09173">Packet
 * Data Token Stream Definition</a>.
 * <p>
 * The various tokens supported can be found in section <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/7cfc401b-f9b6-404d-a447-22e21e593742">Token
 * Stream</a>.
 * <p>
 * The corresponding message types that use token-based packet data streams are identified in the table in section
 * <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/dc3a0854-8230-482f-bbb9-d94a3b905a26">Token
 * stream and tokenless stream</a>.
 * <p>
 *
 * @author user
 */
public abstract class TokenStream
{
    protected final Session session;

    protected TokenStream(Session session)
    {
        this.session = session;
    }

    public abstract Token getToken();
}
