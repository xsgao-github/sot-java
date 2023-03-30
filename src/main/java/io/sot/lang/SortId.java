package io.sot.lang;

import io.sot.lang.AllHeaders.Type;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The following SQL collations are listed on the Collation Settings page of the SQL Server Installation Wizard.
 * <p>
 * defined in
 * <a href="https://docs.microsoft.com/en-us/previous-versions/sql/sql-server-2008-r2/ms144250(v=sql.105)">SQL Server
 * Collation</a> and
 * <a href="https://docs.microsoft.com/en-us/previous-versions/sql/sql-server-2008-r2/ms180175(v=sql.105)">SQL Server
 * Collation Name (Transact-SQL)</a>
 *
 * @author user
 */
public enum SortId
        implements PacketDataWriter
{
    SQL_Latin1_General_Cp437_BIN(30),
    SQL_Latin1_General_Cp437_CS_AS(31),
    SQL_Latin1_General_Cp437_CI_AS(32),
    SQL_Latin1_General_Pref_CP437_CI_AS(33),
    SQL_Latin1_General_Cp437_CI_AI(34),
    SQL_Latin1_General_Cp850_BIN(40),
    SQL_Latin1_General_Cp850_CS_AS(41),
    SQL_Latin1_General_Cp850_CI_AS(42),
    SQL_Latin1_General_Pref_CP850_CI_AS(43),
    SQL_Latin1_General_Cp850_CI_AI(44),
    SQL_1Xcompat_CP850_CI_AS(49),
    Latin1_General_BIN(50),
    SQL_Latin1_General_Cp1_CS_AS(51),
    SQL_Latin1_General_Cp1_CI_AS(52),
    SQL_Latin1_General_Pref_CP1_CI_AS(53),
    SQL_Latin1_General_Cp1_CI_AI(54),
    SQL_AltDiction_Cp850_CS_AS(55),
    SQL_AltDiction_Pref_CP850_CI_AS(56),
    SQL_AltDiction_Cp850_CI_AI(57),
    SQL_Scandinavian_Pref_Cp850_CI_AS(58),
    SQL_Scandinavian_Cp850_CS_AS(59),
    SQL_Scandinavian_Cp850_CI_AS(60),
    SQL_AltDiction_Cp850_CI_AS(61),
    Latin1_General_CS_AS(71),
    Latin1_General_CI_AS(72),
    Danish_Norwegian_CS_AS(73),
    Finnish_Swedish_CS_AS(74),
    Icelandic_CS_AS(75),
    Hungarian_BIN(80),
    SQL_Latin1_General_Cp1250_CS_AS(81),
    SQL_Latin1_General_Cp1250_CI_AS(82),
    SQL_Czech_Cp1250_CS_AS(83),
    SQL_Czech_Cp1250_CI_AS(84),
    SQL_Hungarian_Cp1250_CS_AS(85),
    SQL_Hungarian_Cp1250_CI_AS(86),
    SQL_Polish_Cp1250_CS_AS(87),
    SQL_Polish_Cp1250_CI_AS(88),
    SQL_Romanian_Cp1250_CS_AS(89),
    SQL_Romanian_Cp1250_CI_AS(90),
    SQL_Croatian_Cp1250_CS_AS(91),
    SQL_Croatian_Cp1250_CI_AS(92),
    SQL_Slovak_Cp1250_CS_AS(93),
    SQL_Slovak_Cp1250_CI_AS(94),
    SQL_Slovenian_Cp1250_CS_AS(95),
    SQL_Slovenian_Cp1250_CI_AS(96),
    Cyrillic_General_BIN(104),
    SQL_Latin1_General_Cp1251_CS_AS(105),
    SQL_Latin1_General_Cp1251_CI_AS(106),
    SQL_Ukrainian_Cp1251_CS_AS(107),
    SQL_Ukrainian_Cp1251_CI_AS(108),
    Greek_BIN(112),
    SQL_Latin1_General_Cp1253_CS_AS(113),
    SQL_Latin1_General_Cp1253_CI_AS(114),
    SQL_MixDiction_Cp1253_CS_AS(120),
    SQL_AltDiction_Cp1253_CS_AS(121),
    SQL_Latin1_General_Cp1253_CI_AI(124),
    Turkish_BIN(128),
    SQL_Latin1_General_Cp1254_CS_AS(129),
    SQL_Latin1_General_Cp1254_CI_AS(130),
    Hebrew_BIN(136),
    SQL_Latin1_General_Cp1255_CS_AS(137),
    SQL_Latin1_General_Cp1255_CI_AS(138),
    Arabic_BIN(144),
    SQL_Latin1_General_Cp1256_CS_AS(145),
    SQL_Latin1_General_Cp1256_CI_AS(146),
    SQL_Latin1_General_Cp1257_CS_AS(153),
    SQL_Latin1_General_Cp1257_CI_AS(154),
    SQL_Estonian_Cp1257_CS_AS(155),
    SQL_Estonian_Cp1257_CI_AS(156),
    SQL_Latvian_Cp1257_CS_AS(157),
    SQL_Latvian_Cp1257_CI_AS(158),
    SQL_Lithuanian_Cp1257_CS_AS(159),
    SQL_Lithuanian_Cp1257_CI_AS(160),
    SQL_Danish_Pref_Cp1_CI_AS(183),
    SQL_SwedishPhone_Pref_Cp1_CI_AS(184),
    SQL_SwedishStd_Pref_Cp1_CI_AS(185),
    SQL_Icelandic_Pref_Cp1_CI_AS(186),
    Japanese_BIN(192),
    Japanese_CI_AS(193),
    Korean_Wansung_BIN(194),
    Korean_Wansung_CI_AS(195),
    Chinese_Taiwan_Stroke_BIN(196),
    Chinese_Taiwan_Stroke_CI_AS(197),
    Chinese_PRC_BIN(198),
    Chinese_PRC_CI_AS(199),
    Japanese_CS_AS(200),
    Korean_Wansung_CS_AS(201),
    Chinese_Taiwan_Stroke_CS_AS(202),
    Chinese_PRC_CS_AS(203),
    Thai_BIN(204),
    Thai_CI_AS(205),
    Thai_CS_AS(206),
    SQL_EBCDIC037_CP1_CS_AS(210),
    SQL_EBCDIC273_CP1_CS_AS(211),
    SQL_EBCDIC277_CP1_CS_AS(212),
    SQL_EBCDIC278_CP1_CS_AS(213),
    SQL_EBCDIC280_CP1_CS_AS(214),
    SQL_EBCDIC284_CP1_CS_AS(215),
    SQL_EBCDIC285_CP1_CS_AS(216),
    SQL_EBCDIC297_CP1_CS_AS(217);

    private final GenUByte value;

    SortId(int value)
    {
        this.value = new GenUByte((byte) value);
    }

    public GenUByte getValue()
    {
        return value;
    }

    private static final Map<GenUByte, SortId> value2SortId = initValue2SortId();

    private static Map<GenUByte, SortId> initValue2SortId()
    {
        Map<GenUByte, SortId> m = new HashMap<>();
        for (SortId si : SortId.values()) {
            m.put(si.value, si);
        }
        return m;
    }

    public static SortId valueOf(GenUByte value)
            throws EnumValueLookupException
    {
        SortId si = value2SortId.get(value);
        if (si == null) {
            throw new EnumValueLookupException(Type.class, value);
        }
        return si;
    }

    @Override
    public void write(PacketDataOutput out)
            throws PacketRWException, IOException
    {
        out.write(this.value);
    }
}
