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

    /** default */
    DEFAULT(null),
    /** mysql_armscii8 */
    MYSQL_ARMSCII8("armscii8"),
    /** mysql_ascii */
    MYSQL_ASCII("ascii"),
    /** mysql_big5 */
    MYSQL_BIG5("big5"),
    /** mysql_binary */
    MYSQL_BINARY("binary"),
    /** mysql_cp850 */
    MYSQL_CP850("cp850"),
    /** mysql_cp852 */
    MYSQL_CP852("cp852"),
    /** mysql_cp866 */
    MYSQL_CP866("cp866"),
    /** mysql_cp932 */
    MYSQL_CP932("cp932"),
    /** mysql_cp1250 */
    MYSQL_CP1250("cp1250"),
    /** mysql_cp1251 */
    MYSQL_CP1251("cp1251"),
    /** mysql_cp1256 */
    MYSQL_CP1256("cp1256"),
    /** mysql_cp1257 */
    MYSQL_CP1257("cp1257"),
    /** mysql_dec8 */
    MYSQL_DEC8("dec8"),
    /** mysql_eucjpms */
    MYSQL_EUCJPMS("eucjpms"),
    /** mysql_euckr */
    MYSQL_EUCKR("euckr"),
    /** mysql_gb2312 */
    MYSQL_GB2312("gb2312"),
    /** mysql_gbk */
    MYSQL_GBK("gbk"),
    /** mysql_geostd8 */
    MYSQL_GEOSTD8("geostd8"),
    /** mysql_greek */
    MYSQL_GREEK("greek"),
    /** mysql_hebrew */
    MYSQL_HEBREW("hebrew"),
    /** mysql_hp8 */
    MYSQL_HP8("hp8"),
    /** mysql_keybcs2 */
    MYSQL_KEYBCS2("keybcs2"),
    /** mysql_koi8r */
    MYSQL_KOI8R("koi8r"),
    /** mysql_koi8u */
    MYSQL_KOI8U("koi8u"),
    /** mysql_latin1 */
    MYSQL_LATIN1("latin1"),
    /** mysql_latin2 */
    MYSQL_LATIN2("latin2"),
    /** mysql_latin5 */
    MYSQL_LATIN5("latin5"),
    /** mysql_latin7 */
    MYSQL_LATIN7("latin7"),
    /** mysql_macce */
    MYSQL_MACCE("macce"),
    /** mysql_macroman */
    MYSQL_MACROMAN("macroman"),
    /** mysql_sjis */
    MYSQL_SJIS("sjis"),
    /** mysql_swe7 */
    MYSQL_SWE7("swe7"),
    /** mysql_tis620 */
    MYSQL_TIS620("tis620"),
    /** mysql_ucs2 */
    MYSQL_UCS2("ucs2"),
    /** mysql_ujis */
    MYSQL_UJIS("ujis"),
    /** mysql_utf8 */
    MYSQL_UTF8("utf8"),
    /** mysql_utf8mb4 */
    MYSQL_UTF8MB4("utf8mb4"),
    /** mysql_utf16 */
    MYSQL_UTF16("utf16"),
    /** mysql_utf32 */
    MYSQL_UTF32("utf32");

    private final String value;
}
