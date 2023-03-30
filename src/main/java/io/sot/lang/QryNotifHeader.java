package io.sot.lang;

import java.io.IOException;

/**
 * <p>
 * This packet data stream header allows the client to specify that a notification is to be supplied on the results of
 * the request. The contents of the header specify the information necessary for delivery of the notification. For more
 * information about query notifications<11> functionality for a database server that supports SQL, see <a href=
 * "https://docs.microsoft.com/en-us/previous-versions/sql/sql-server-2008-r2/ms175110(v=sql.105)">[MSDN-QUERYNOTE]</a>.
 * </p>
 * <p>
 * SQL Server 2005 introduced query notifications, new functionality that allows an application to request a
 * notification from SQL Server when the results of a query change. Query notifications allow programmers to design
 * applications that query the database only when there is a change to information that the application has previously
 * retrieved.
 * </p>
 * <p>
 * For example, an online catalog application may cache the results of a query that lists the items that are on sale.
 * The application presents the catalog based on the cached data. When the list of sale items changes, an event handler
 * in the application receives the notification event, and discards the cached data. The next time a customer requests
 * the list of sale items, the application queries the database for the current data and renews the notification
 * subscription.
 * </p>
 * defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/e168d373-a7b7-41aa-b6ca-25985466a7e0">Query
 * Notifications Header</a>
 *
 * @author user
 */
public class QryNotifHeader
        extends StreamHeader
{
    private GenUnicodeStream notifyID = null;
    private GenUnicodeStream ssbDeployment = null;
    private GenULong notifyTimeout = null;

    public QryNotifHeader()
    {
        super();
    }

    public GenUnicodeStream getNotifyID()
    {
        return notifyID;
    }

    public void setNotifyID(GenUnicodeStream notifyID)
    {
        this.notifyID = notifyID;
    }

    public GenUnicodeStream getSsbDeployment()
    {
        return ssbDeployment;
    }

    public void setSsbDeployment(GenUnicodeStream ssbDeployment)
    {
        this.ssbDeployment = ssbDeployment;
    }

    public GenULong getNotifyTimeout()
    {
        return notifyTimeout;
    }

    public void setNotifyTimeout(GenULong notifyTimeout)
    {
        this.notifyTimeout = notifyTimeout;
    }

    @Override
    public void read(PacketDataInput data)
            throws PacketRWException, IOException
    {
        GenUShort notifyIDLen = data.readGenUShort();
        this.notifyID = data.readGenUniCodeStream(notifyIDLen.intValue());
        GenUShort ssbDeploymentLen = data.readGenUShort();
        this.ssbDeployment = data.readGenUniCodeStream(ssbDeploymentLen.intValue());
        int remaining = (int) this.length.longValue() - this.length.getLength() - this.type.getValue().getLength()
                - notifyIDLen.getLength() - notifyIDLen.intValue() - ssbDeploymentLen.getLength()
                - ssbDeploymentLen.intValue();
        if (remaining == GenULong.LENGTH) {
            this.notifyTimeout = data.readGenULong();
        }
    }
}
