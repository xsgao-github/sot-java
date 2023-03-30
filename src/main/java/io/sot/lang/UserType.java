package io.sot.lang;

/**
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/773a62b6-ee89-4c02-9e5e-344882630aac">LOGIN7</a>.
 * <p>
 * <p>
 * fUserType: The type of user connecting to the server.
 * <ul>
 * <li>0 = USER_NORMAL-regular logins</li>
 * <li>1 = USER_SERVER-reserved</li>
 * <li>2 = USER_REMUSER-Distributed Query login</li>
 * <li>3 = USER_SQLREPL-replication login</li>
 * </ul>
 *
 * @author user
 */
public enum UserType
{
    USER_NORMAL, USER_SERVER, USER_REMUSER, USER_SQLREPL
}
