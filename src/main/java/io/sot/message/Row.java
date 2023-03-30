package io.sot.message;

import io.sot.Session;
import io.sot.lang.PacketDataOutput;
import io.sot.lang.PacketDataWriter;
import io.sot.lang.PacketRWException;
import io.sot.lang.TypeInfo;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Used to send a complete row, as defined by the COLMETADATA token, to the client, defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/3840ef93-3b10-4aca-9fd1-a210b8bb6d0c">ROW</a>
 *
 * @author user
 */
public class Row
        extends TokenStream
        implements PacketDataWriter
{

    private final ColMetaData dsMD;
    private final ResultSet rs;
    private final int rowNum;

    public Row(Session session, ColMetaData colMetaData, ResultSet rs, int rowNum)
    {
        super(session);
        this.dsMD = colMetaData;
        this.rs = rs;
        this.rowNum = rowNum;
    }

    @Override
    public Token getToken()
    {
        return Token.ROW;
    }

    @Override
    public void write(PacketDataOutput out)
            throws PacketRWException, IOException
    {
        try {
            out.write(Token.ROW.getValue());

            ResultSetMetaData rsMD = rs.getMetaData();
            for (int index = 1; index <= rsMD.getColumnCount(); index++) {
                TypeInfo ti = this.dsMD.getColumnMetaData(index).getTypeInfo();

                switch (rsMD.getColumnType(index)) {
                    case JdbcDataType.TINYINT:
                        out.write(ti, (byte) rs.getShort(index), rs.wasNull());
                        break;
                    case JdbcDataType.SMALLINT:
                        out.write(ti, rs.getShort(index), rs.wasNull());
                        break;
                    case JdbcDataType.INTEGER:
                        out.write(ti, rs.getInt(index), rs.wasNull());
                        break;
                    case JdbcDataType.BIGINT:
                        out.write(ti, rs.getLong(index), rs.wasNull());
                        break;
                    case JdbcDataType.REAL:
                        out.write(ti, rs.getFloat(index), rs.wasNull());
                        break;
                    case JdbcDataType.DOUBLE:
                    case JdbcDataType.FLOAT:
                        out.write(ti, rs.getDouble(index), rs.wasNull());
                        break;
                    case JdbcDataType.DECIMAL:
                    case JdbcDataType.NUMERIC:
                        out.write(ti, rs.getBigDecimal(index), rs.wasNull());
                        break;
                    case JdbcDataType.CHAR:
                    case JdbcDataType.NCHAR:
                    case JdbcDataType.VARCHAR:
                    case JdbcDataType.NVARCHAR:
                        out.write(ti, rs.getString(index), rs.wasNull());
                        break;
                    case JdbcDataType.DATE:
                        out.write(ti, rs.getDate(index), rs.wasNull());
                        break;
                    case JdbcDataType.TIME:
                        out.write(ti, rs.getTime(index), rs.wasNull());
                        break;
                    case JdbcDataType.TIMESTAMP:
                        out.write(ti, rs.getTimestamp(index), rs.wasNull());
                        break;
                    case JdbcDataType.BOOLEAN:
                        out.write(ti, rs.getBoolean(index), rs.wasNull());
                        break;
                    case JdbcDataType.UNKNOWN:
                    default:
                        out.write(ti, rs.getString(index), rs.wasNull());
                }
            }
        }
        catch (SQLException e) {
            throw new PacketRWException(e, "Failed to write row %d.", this.rowNum);
        }
    }
}
