/**
 * Provides classes that reassemble various data types in TDS as defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/b7b3f6ba-65dc-491e-b84d-dbb12654228d">Grammar
 * Definition for Token Description</a>, and classes to read/write packets as defined in <a href=
 * "https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-tds/e5ea8520-1ea3-4a75-a2a9-c17e63e9ee19">Packets</a>.
 * <p>
 * Some data will always follow the same streaming format, for example data in a
 * {@link io.sot.message.Login7} request, or a {@link io.sot.message.LoginAck} token.
 * <p>
 * But some data will have different data streams depend on the data type declaration that applies to them. For example,
 * when writing a 32-bit integer ({@link io.sot.lang.SqlInt}) within a dataset to output stream, it may
 * generate 4 bytes if it's declared as a INT4TYPE (not-nullable int), or it may generate 1 byte (with value set to 0,
 * means null) or 5 bytes (1 byte for the length of 4, and 4 bytes for actual value) if it's declared as INTNTYPE
 * (nullable int).
 * <p>
 * To distinguish these two different kinds of data, we name classes using following naming conventions:
 * <ul>
 * <li>{@code GenAbc} for the first kind, means general purpose data type.</li>
 * <li>{@code SqlAbc} for the second kind, means SQL server system data type that used transfer data values via
 * TDS.</li>
 * </ul>
 * <p>
 * Also, read/write methods are different for these two kinds. Because the second kind of data depend on the data type
 * information, we need to pass an extra parameter {@link io.sot.lang.TypeInfo} when read/write them.
 *
 * @author user
 */
package io.sot.lang;