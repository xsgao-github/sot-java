package io.sot.lang;

import java.util.HashMap;
import java.util.Map;

/**
 * Defined in <a href=
 * "https://docs.microsoft.com/en-us/sql/relational-databases/system-catalog-views/sys-fulltext-languages-transact-sql?view=sql-server-ver15">sys.fulltext_languages
 * (Transact-SQL)</a>.
 * <p>
 *
 * @author user
 */
public enum LCID
{
    Arabic(1025),
    Bengali_India(1093),
    British_English(2057),
    Bulgarian(1026),
    Catalan(1027),
    Chinese_HongKong(3076),
    Chinese_Macao(5124),
    Chinese_Singapore(4100),
    Croatian(1050),
    Czech(1029),
    Danish(1030),
    Dutch(1043),
    English(1033),
    French(1036),
    German(1031),
    Greek(1032),
    Gujarati(1095),
    Hebrew(1037),
    Hindi(1081),
    Icelandic(1039),
    Indonesian(1057),
    Italian(1040),
    Japanese(1041),
    Kannada(1099),
    Korean(1042),
    Latvian(1062),
    Lithuanian(1063),
    Malay_Malaysia(1086),
    Malayalam(1100),
    Marathi(1102),
    Neutral(0),
    Norwegian_Bokmal(1044),
    Polish(1045),
    Portuguese_Brazil(1046),
    Portuguese_Portugal(2070),
    Punjabi(1094),
    Romanian(1048),
    Russian(1049),
    Serbian_Cyrillic(3098),
    Serbian_Latin(2074),
    SimplifiedChinese(2052),
    Slovak(1051),
    Slovenian(1060),
    Spanish(3082),
    Swedish(1053),
    Tamil(1097),
    Telugu(1098),
    Thai(1054),
    TraditionalChinese(1028),
    Turkish(1055),
    Ukrainian(1058),
    Urdu(1056),
    Vietnamese(1066);

    private final GenDWord code;

    LCID(int code)
    {
        this.code = new GenDWord(code);
    }

    public GenDWord getCode()
    {
        return code;
    }

    private static final Map<GenDWord, LCID> m = new HashMap<GenDWord, LCID>();

    static {
        for (LCID l : LCID.values()) {
            m.put(l.getCode(), l);
        }
    }

    public static LCID valueOf(GenDWord code)
            throws EnumValueLookupException
    {
        LCID lcid = m.get(code);
        if (lcid == null) {
            throw new EnumValueLookupException(LCID.class, code);
        }
        return lcid;
    }
}
