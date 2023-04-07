package cn.bootx.mybatis.table.modify.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 表数据类型
 * @author xxm
 * @date 2023/4/7
 */
@AllArgsConstructor
@Getter
public enum TableCharset {

    /** DEFAULT */
    DEFAULT(null),
    /** MYSQL_ARMSCII8 */
    MYSQL_ARMSCII8("ARMSCII8"),
    /** MYSQL_ASCII */
    MYSQL_ASCII("ASCII"),
    /** MYSQL_BIG5 */
    MYSQL_BIG5("BIG5"),
    /** MYSQL_BINARY */
    MYSQL_BINARY("BINARY"),
    /** MYSQL_CP850 */
    MYSQL_CP850("CP850"),
    /** MYSQL_CP852 */
    MYSQL_CP852("CP852"),
    /** MYSQL_CP866 */
    MYSQL_CP866("CP866"),
    /** MYSQL_CP932 */
    MYSQL_CP932("CP932"),
    /** MYSQL_CP1250 */
    MYSQL_CP1250("CP1250"),
    /** MYSQL_CP1251 */
    MYSQL_CP1251("CP1251"),
    /** MYSQL_CP1256 */
    MYSQL_CP1256("CP1256"),
    /** MYSQL_CP1257 */
    MYSQL_CP1257("CP1257"),
    /** MYSQL_DEC8 */
    MYSQL_DEC8("DEC8"),
    /** MYSQL_EUCJPMS */
    MYSQL_EUCJPMS("EUCJPMS"),
    /** MYSQL_EUCKR */
    MYSQL_EUCKR("EUCKR"),
    /** MYSQL_GB2312 */
    MYSQL_GB2312("GB2312"),
    /** MYSQL_GBK */
    MYSQL_GBK("GBK"),
    /** MYSQL_GEOSTD8 */
    MYSQL_GEOSTD8("GEOSTD8"),
    /** MYSQL_GREEK */
    MYSQL_GREEK("GREEK"),
    /** MYSQL_HEBREW */
    MYSQL_HEBREW("HEBREW"),
    /** MYSQL_HP8 */
    MYSQL_HP8("HP8"),
    /** MYSQL_KEYBCS2 */
    MYSQL_KEYBCS2("KEYBCS2"),
    /** MYSQL_KOI8R */
    MYSQL_KOI8R("KOI8R"),
    /** MYSQL_KOI8U */
    MYSQL_KOI8U("KOI8U"),
    /** MYSQL_LATIN1 */
    MYSQL_LATIN1("LATIN1"),
    /** MYSQL_LATIN2 */
    MYSQL_LATIN2("LATIN2"),
    /** MYSQL_LATIN5 */
    MYSQL_LATIN5("LATIN5"),
    /** MYSQL_LATIN7 */
    MYSQL_LATIN7("LATIN7"),
    /** MYSQL_MACCE */
    MYSQL_MACCE("MACCE"),
    /** MYSQL_MACROMAN */
    MYSQL_MACROMAN("MACROMAN"),
    /** MYSQL_SJIS */
    MYSQL_SJIS("SJIS"),
    /** MYSQL_SWE7 */
    MYSQL_SWE7("SWE7"),
    /** MYSQL_TIS620 */
    MYSQL_TIS620("TIS620"),
    /** MYSQL_UCS2 */
    MYSQL_UCS2("UCS2"),
    /** MYSQL_UJIS */
    MYSQL_UJIS("UJIS"),
    /** MYSQL_UTF8 */
    MYSQL_UTF8("UTF8"),
    /** MYSQL_UTF8MB4 */
    MYSQL_UTF8MB4("UTF8MB4"),
    /** MYSQL_UTF16 */
    MYSQL_UTF16("UTF16"),
    /** MYSQL_UTF32 */
    MYSQL_UTF32("UTF32");

    private final String value;
}
