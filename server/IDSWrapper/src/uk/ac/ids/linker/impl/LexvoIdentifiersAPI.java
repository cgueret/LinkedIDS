package uk.ac.ids.linker.impl;

import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Lexvo.org Identifier API
 * 
 * @author Gerard de Melo
 */
public class LexvoIdentifiersAPI {
	/**
	 * prefix for Lexvo Identifiers
	 */
	private static final String LEXVO_URI_BASE = "http://lexvo.org/id/";

	/**
	 * pattern matching (at least) valid ISO 639-3 codes
	 */
	private static Pattern valid639P3Codes = Pattern.compile("[a-z]{3}");

	/**
	 * get Lexvo.org Term URI
	 * 
	 * @param term
	 * @param langISO639P3Code
	 * @return Lexvo.org Term URI
	 */
	public static String getTermURI(String term, String langISO639P3Code) {
		if (!valid639P3Codes.matcher(langISO639P3Code).matches())
			throw new IllegalArgumentException("Invalid ISO 639-3 code: " + langISO639P3Code);
		// NFC normalization
		String base = Normalizer.normalize(term, Normalizer.Form.NFC);
		// escape characters
		base = escapeURIComponent(base);
		// prepend address and language
		return LEXVO_URI_BASE + "term/" + langISO639P3Code + "/" + base;
	}

	/**
	 * get Lexvo.org Language URI
	 * 
	 * @param iso639P1Code
	 *            ISO 639-1 code
	 * @return Lexvo.org Language URI
	 */
	public static String getLanguageURIforISO639P1(String iso639P1Code) {
		String iso639P3Code = convertIso639P1ToP3(iso639P1Code);
		if (iso639P3Code == null)
			throw new IllegalArgumentException("Invalid ISO 639-1 code: " + iso639P1Code);
		return LEXVO_URI_BASE + "iso639-3/" + iso639P3Code;
	}

	/**
	 * get Lexvo.org Language URI
	 * 
	 * @param iso639P3Code
	 *            ISO 639-3 code
	 * @return Lexvo.org Language URI
	 */
	public static String getLanguageURIforISO639P3(String iso639P3Code) {
		if (!valid639P3Codes.matcher(iso639P3Code).matches())
			throw new IllegalArgumentException("Invalid ISO 639-3 code: " + iso639P3Code);
		return LEXVO_URI_BASE + "iso639-3/" + iso639P3Code;
	}

	/**
	 * allowed characters within URI path components
	 */
	private static String validURICharactersString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_.!~*\'()";

	/**
	 * set storing allowed characters within URI path components for more
	 * efficient access
	 */
	private static HashSet<Byte> validURICharacters = new HashSet<Byte>(validURICharactersString.length());
	static {
		int nChars = validURICharactersString.length();
		for (int i = 0; i < nChars; i++)
			validURICharacters.add(Byte.valueOf((byte) validURICharactersString.charAt(i)));
	}

	/**
	 * hexadecimal digits
	 */
	private static String HEX_DIGITS = "0123456789ABCDEF";

	/**
	 * escape URI component
	 * 
	 * @param s
	 *            input string
	 * @return escaped string
	 */
	private static String escapeURIComponent(String s) {
		byte[] utf8Encoding;
		try {
			utf8Encoding = s.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		StringBuilder sb = new StringBuilder(utf8Encoding.length);
		for (int i = 0; i < utf8Encoding.length; i++) {
			if (validURICharacters.contains(utf8Encoding[i]))
				sb.append((char) utf8Encoding[i]);
			else {
				sb.append('%');
				byte b = utf8Encoding[i];
				sb.append(HEX_DIGITS.charAt((b >> 4) & 0x0F));
				sb.append(HEX_DIGITS.charAt(b & 0x0F));
			}
		}
		return sb.toString();
	}

	/**
	 * convert ISO 639-1 code to ISO 639-3 code
	 * 
	 * @param iso639P1Code
	 *            ISO 639-1 code
	 * @return ISO 639-3 code (or null if no mapping available)
	 */
	public static String convertIso639P1ToP3(String iso639P1Code) {
		return iso639P1ToP3Mapping.get(iso639P1Code);
	}

	private static Map<String, String> iso639P1ToP3Mapping = new HashMap<String, String>();
	static {
		iso639P1ToP3Mapping.put("aa", "aar");
		iso639P1ToP3Mapping.put("ab", "abk");
		iso639P1ToP3Mapping.put("af", "afr");
		iso639P1ToP3Mapping.put("ak", "aka");
		iso639P1ToP3Mapping.put("am", "amh");
		iso639P1ToP3Mapping.put("ar", "ara");
		iso639P1ToP3Mapping.put("an", "arg");
		iso639P1ToP3Mapping.put("as", "asm");
		iso639P1ToP3Mapping.put("av", "ava");
		iso639P1ToP3Mapping.put("ae", "ave");
		iso639P1ToP3Mapping.put("ay", "aym");
		iso639P1ToP3Mapping.put("az", "aze");
		iso639P1ToP3Mapping.put("ba", "bak");
		iso639P1ToP3Mapping.put("bm", "bam");
		iso639P1ToP3Mapping.put("be", "bel");
		iso639P1ToP3Mapping.put("bn", "ben");
		iso639P1ToP3Mapping.put("bi", "bis");
		iso639P1ToP3Mapping.put("bo", "bod");
		iso639P1ToP3Mapping.put("bs", "bos");
		iso639P1ToP3Mapping.put("br", "bre");
		iso639P1ToP3Mapping.put("bg", "bul");
		iso639P1ToP3Mapping.put("ca", "cat");
		iso639P1ToP3Mapping.put("cs", "ces");
		iso639P1ToP3Mapping.put("ch", "cha");
		iso639P1ToP3Mapping.put("ce", "che");
		iso639P1ToP3Mapping.put("cu", "chu");
		iso639P1ToP3Mapping.put("cv", "chv");
		iso639P1ToP3Mapping.put("kw", "cor");
		iso639P1ToP3Mapping.put("co", "cos");
		iso639P1ToP3Mapping.put("cr", "cre");
		iso639P1ToP3Mapping.put("cy", "cym");
		iso639P1ToP3Mapping.put("da", "dan");
		iso639P1ToP3Mapping.put("de", "deu");
		iso639P1ToP3Mapping.put("dv", "div");
		iso639P1ToP3Mapping.put("dz", "dzo");
		iso639P1ToP3Mapping.put("el", "ell");
		iso639P1ToP3Mapping.put("en", "eng");
		iso639P1ToP3Mapping.put("eo", "epo");
		iso639P1ToP3Mapping.put("et", "est");
		iso639P1ToP3Mapping.put("eu", "eus");
		iso639P1ToP3Mapping.put("ee", "ewe");
		iso639P1ToP3Mapping.put("fo", "fao");
		iso639P1ToP3Mapping.put("fa", "fas");
		iso639P1ToP3Mapping.put("fj", "fij");
		iso639P1ToP3Mapping.put("fi", "fin");
		iso639P1ToP3Mapping.put("fr", "fra");
		iso639P1ToP3Mapping.put("fy", "fry");
		iso639P1ToP3Mapping.put("ff", "ful");
		iso639P1ToP3Mapping.put("gd", "gla");
		iso639P1ToP3Mapping.put("ga", "gle");
		iso639P1ToP3Mapping.put("gl", "glg");
		iso639P1ToP3Mapping.put("gv", "glv");
		iso639P1ToP3Mapping.put("gn", "grn");
		iso639P1ToP3Mapping.put("gu", "guj");
		iso639P1ToP3Mapping.put("ht", "hat");
		iso639P1ToP3Mapping.put("ha", "hau");
		iso639P1ToP3Mapping.put("sh", "hbs");
		iso639P1ToP3Mapping.put("he", "heb");
		iso639P1ToP3Mapping.put("hz", "her");
		iso639P1ToP3Mapping.put("hi", "hin");
		iso639P1ToP3Mapping.put("ho", "hmo");
		iso639P1ToP3Mapping.put("hr", "hrv");
		iso639P1ToP3Mapping.put("hu", "hun");
		iso639P1ToP3Mapping.put("hy", "hye");
		iso639P1ToP3Mapping.put("ig", "ibo");
		iso639P1ToP3Mapping.put("io", "ido");
		iso639P1ToP3Mapping.put("ii", "iii");
		iso639P1ToP3Mapping.put("iu", "iku");
		iso639P1ToP3Mapping.put("ie", "ile");
		iso639P1ToP3Mapping.put("ia", "ina");
		iso639P1ToP3Mapping.put("id", "ind");
		iso639P1ToP3Mapping.put("ik", "ipk");
		iso639P1ToP3Mapping.put("is", "isl");
		iso639P1ToP3Mapping.put("it", "ita");
		iso639P1ToP3Mapping.put("jv", "jav");
		iso639P1ToP3Mapping.put("ja", "jpn");
		iso639P1ToP3Mapping.put("kl", "kal");
		iso639P1ToP3Mapping.put("kn", "kan");
		iso639P1ToP3Mapping.put("ks", "kas");
		iso639P1ToP3Mapping.put("ka", "kat");
		iso639P1ToP3Mapping.put("kr", "kau");
		iso639P1ToP3Mapping.put("kk", "kaz");
		iso639P1ToP3Mapping.put("km", "khm");
		iso639P1ToP3Mapping.put("ki", "kik");
		iso639P1ToP3Mapping.put("rw", "kin");
		iso639P1ToP3Mapping.put("ky", "kir");
		iso639P1ToP3Mapping.put("kv", "kom");
		iso639P1ToP3Mapping.put("kg", "kon");
		iso639P1ToP3Mapping.put("ko", "kor");
		iso639P1ToP3Mapping.put("kj", "kua");
		iso639P1ToP3Mapping.put("ku", "kur");
		iso639P1ToP3Mapping.put("lo", "lao");
		iso639P1ToP3Mapping.put("la", "lat");
		iso639P1ToP3Mapping.put("lv", "lav");
		iso639P1ToP3Mapping.put("li", "lim");
		iso639P1ToP3Mapping.put("ln", "lin");
		iso639P1ToP3Mapping.put("lt", "lit");
		iso639P1ToP3Mapping.put("lb", "ltz");
		iso639P1ToP3Mapping.put("lu", "lub");
		iso639P1ToP3Mapping.put("lg", "lug");
		iso639P1ToP3Mapping.put("mh", "mah");
		iso639P1ToP3Mapping.put("ml", "mal");
		iso639P1ToP3Mapping.put("mr", "mar");
		iso639P1ToP3Mapping.put("mk", "mkd");
		iso639P1ToP3Mapping.put("mg", "mlg");
		iso639P1ToP3Mapping.put("mt", "mlt");
		iso639P1ToP3Mapping.put("mo", "mol");
		iso639P1ToP3Mapping.put("mn", "mon");
		iso639P1ToP3Mapping.put("mi", "mri");
		iso639P1ToP3Mapping.put("ms", "msa");
		iso639P1ToP3Mapping.put("my", "mya");
		iso639P1ToP3Mapping.put("na", "nau");
		iso639P1ToP3Mapping.put("nv", "nav");
		iso639P1ToP3Mapping.put("nr", "nbl");
		iso639P1ToP3Mapping.put("nd", "nde");
		iso639P1ToP3Mapping.put("ng", "ndo");
		iso639P1ToP3Mapping.put("ne", "nep");
		iso639P1ToP3Mapping.put("nl", "nld");
		iso639P1ToP3Mapping.put("nn", "nno");
		iso639P1ToP3Mapping.put("nb", "nob");
		iso639P1ToP3Mapping.put("no", "nor");
		iso639P1ToP3Mapping.put("ny", "nya");
		iso639P1ToP3Mapping.put("oc", "oci");
		iso639P1ToP3Mapping.put("oj", "oji");
		iso639P1ToP3Mapping.put("or", "ori");
		iso639P1ToP3Mapping.put("om", "orm");
		iso639P1ToP3Mapping.put("os", "oss");
		iso639P1ToP3Mapping.put("pa", "pan");
		iso639P1ToP3Mapping.put("pi", "pli");
		iso639P1ToP3Mapping.put("pl", "pol");
		iso639P1ToP3Mapping.put("pt", "por");
		iso639P1ToP3Mapping.put("ps", "pus");
		iso639P1ToP3Mapping.put("qu", "que");
		iso639P1ToP3Mapping.put("rm", "roh");
		iso639P1ToP3Mapping.put("ro", "ron");
		iso639P1ToP3Mapping.put("rn", "run");
		iso639P1ToP3Mapping.put("ru", "rus");
		iso639P1ToP3Mapping.put("sg", "sag");
		iso639P1ToP3Mapping.put("sa", "san");
		iso639P1ToP3Mapping.put("si", "sin");
		iso639P1ToP3Mapping.put("sk", "slk");
		iso639P1ToP3Mapping.put("sl", "slv");
		iso639P1ToP3Mapping.put("se", "sme");
		iso639P1ToP3Mapping.put("sm", "smo");
		iso639P1ToP3Mapping.put("sn", "sna");
		iso639P1ToP3Mapping.put("sd", "snd");
		iso639P1ToP3Mapping.put("so", "som");
		iso639P1ToP3Mapping.put("st", "sot");
		iso639P1ToP3Mapping.put("es", "spa");
		iso639P1ToP3Mapping.put("sq", "sqi");
		iso639P1ToP3Mapping.put("sc", "srd");
		iso639P1ToP3Mapping.put("sr", "srp");
		iso639P1ToP3Mapping.put("ss", "ssw");
		iso639P1ToP3Mapping.put("su", "sun");
		iso639P1ToP3Mapping.put("sw", "swa");
		iso639P1ToP3Mapping.put("sv", "swe");
		iso639P1ToP3Mapping.put("ty", "tah");
		iso639P1ToP3Mapping.put("ta", "tam");
		iso639P1ToP3Mapping.put("tt", "tat");
		iso639P1ToP3Mapping.put("te", "tel");
		iso639P1ToP3Mapping.put("tg", "tgk");
		iso639P1ToP3Mapping.put("tl", "tgl");
		iso639P1ToP3Mapping.put("th", "tha");
		iso639P1ToP3Mapping.put("ti", "tir");
		iso639P1ToP3Mapping.put("to", "ton");
		iso639P1ToP3Mapping.put("tn", "tsn");
		iso639P1ToP3Mapping.put("ts", "tso");
		iso639P1ToP3Mapping.put("tk", "tuk");
		iso639P1ToP3Mapping.put("tr", "tur");
		iso639P1ToP3Mapping.put("tw", "twi");
		iso639P1ToP3Mapping.put("ug", "uig");
		iso639P1ToP3Mapping.put("uk", "ukr");
		iso639P1ToP3Mapping.put("ur", "urd");
		iso639P1ToP3Mapping.put("uz", "uzb");
		iso639P1ToP3Mapping.put("ve", "ven");
		iso639P1ToP3Mapping.put("vi", "vie");
		iso639P1ToP3Mapping.put("vo", "vol");
		iso639P1ToP3Mapping.put("wa", "wln");
		iso639P1ToP3Mapping.put("wo", "wol");
		iso639P1ToP3Mapping.put("xh", "xho");
		iso639P1ToP3Mapping.put("yi", "yid");
		iso639P1ToP3Mapping.put("yo", "yor");
		iso639P1ToP3Mapping.put("za", "zha");
		iso639P1ToP3Mapping.put("zh", "zho");
		iso639P1ToP3Mapping.put("zu", "zul");
	}
}
