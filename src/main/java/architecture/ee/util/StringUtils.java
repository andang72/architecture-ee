/**
 *    Copyright 2015-2017 donghyuck
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package architecture.ee.util;

import java.net.IDN;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import architecture.ee.component.VariableMapImpl;
import architecture.ee.service.VariableMap;

public class StringUtils extends org.springframework.util.StringUtils {

	private static final Logger log = LoggerFactory.getLogger(StringUtils.class);
	
	public static final int INDEX_NOT_FOUND = -1;
	public static final String EMPTY = "";
    
	// Constants used by escapeHTMLTags
    private static final char[] QUOTE_ENCODE = "&quot;".toCharArray();
    private static final char[] AMP_ENCODE = "&amp;".toCharArray();
    private static final char[] LT_ENCODE = "&lt;".toCharArray();
    private static final char[] GT_ENCODE = "&gt;".toCharArray();
    
	public static String defaultString(final String str, final String defaultStr) {
		return str == null ? defaultStr : str;
	}

	public static String expend(String expression, Properties props) {
		VariableMap variables = new VariableMapImpl(props);
		return variables.expand(expression);
	}

	public static boolean isNullOrEmpty(String str) {
		return com.google.common.base.Strings.isNullOrEmpty(str);
	}

	public static String substringBeforeLast(final String str, final String separator) {
		if (isEmpty(str) || isEmpty(separator)) {
			return str;
		}
		final int pos = str.lastIndexOf(separator);
		if (pos == INDEX_NOT_FOUND) {
			return str;
		}
		return str.substring(0, pos);
	}

	public static String substringAfterLast(final String str, final String separator) {
		if (isEmpty(str)) {
			return str;
		}
		if (isEmpty(separator)) {
			return EMPTY;
		}
		final int pos = str.lastIndexOf(separator);
		if (pos == INDEX_NOT_FOUND || pos == str.length() - separator.length()) {
			return EMPTY;
		}
		return str.substring(pos + separator.length());
	}

	public static boolean equals(CharSequence cs1, CharSequence cs2) {
		return cs1 == null ? cs2 == null : cs1.equals(cs2);
	}
	/**
     * This method takes a string which may contain HTML tags (ie, &lt;b&gt;,
     * &lt;table&gt;, etc) and converts the '&lt;' and '&gt;' characters to
     * their HTML escape sequences. It will also replace LF  with &lt;br&gt;.
     *
     * @param in the text to be converted.
     * @return the input string with the characters '&lt;' and '&gt;' replaced
     *         with their HTML escape sequences.
     */
    public static String escapeHTMLTags(String in) {
        return escapeHTMLTags(in, true);
    }


    /**
     * This method takes a string which may contain HTML tags (ie, &lt;b&gt;,
     * &lt;table&gt;, etc) and converts the '&lt;' and '&gt;' characters to
     * their HTML escape sequences.
     *
     * @param in the text to be converted.
     * @param includeLF set to true to replace \n with <br>.
     * @return the input string with the characters '&lt;' and '&gt;' replaced
     *         with their HTML escape sequences.
     */
    public static String escapeHTMLTags(String in, boolean includeLF) {
        if (in == null) {
            return null;
        }
        char ch;
        int i = 0;
        int last = 0;
        char[] input = in.toCharArray();
        int len = input.length;
        StringBuilder out = new StringBuilder((int)(len * 1.3));
        for (; i < len; i++) {
            ch = input[i];
            if (ch > '>') {
            }
            else if (ch == '<') {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(LT_ENCODE);
            }
            else if (ch == '>') {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(GT_ENCODE);
            }
            else if (ch == '\n' && includeLF == true) {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append("<br>");
            }
        }
        if (last == 0) {
            return in;
        }
        if (i > last) {
            out.append(input, last, i - last);
        }
        return out.toString();
    }
	/**
	 * Abbreviates a string to a specified length and then adds an ellipsis if the
	 * input is greater than the maxWidth. Example input:
	 * 
	 * <pre>
	 *      user1@demo.com/home
	 * </pre>
	 * 
	 * and a maximum length of 20 characters, the abbreviate method will return:
	 * 
	 * <pre>
	 *      user1@demo.c...
	 * </pre>
	 * 
	 * @param str
	 *            the String to abbreviate.
	 * @param maxWidth
	 *            the maximum size of the string, minus the ellipsis.
	 * @return the abbreviated String, or {@code null} if the string was
	 *         {@code null}.
	 */
	public static String abbreviate(String str, int maxWidth) {
		if (null == str) {
			return null;
		}

		if (str.length() <= maxWidth) {
			return str;
		}

		return str.substring(0, maxWidth) + "...";
	}

	/**
     * Returns a valid domain name, possibly as an ACE-encoded IDN 
     * (per <a href="http://www.ietf.org/rfc/rfc3490.txt">RFC 3490</a>).
     * 
     * @param domain Proposed domain name
     * @return The validated domain name, possibly ACE-encoded
     * @throws IllegalArgumentException The given domain name is not valid
     */
    public static String validateDomainName(String domain) {
        if (domain == null || domain.trim().length() == 0) {
            throw new IllegalArgumentException("Domain name cannot be null or empty");
        }
        String result = IDN.toASCII(domain);
        if (result.equals(domain)) {
            // no conversion; validate again via USE_STD3_ASCII_RULES
            IDN.toASCII(domain, IDN.USE_STD3_ASCII_RULES);
        } else {
            log.info(MessageFormat.format("Converted domain name: from '{0}' to '{1}'",  domain, result));
        }
        return result;
    }
    
	/**
	 * Removes characters likely to enable Cross Site Scripting attacks from the
	 * provided input string. The characters that are removed from the input string,
	 * if present, are:
	 * 
	 * <pre>
	 * &lt; &gt; &quot; ' % ; ) ( &amp; + -
	 * </pre>
	 * 
	 * @param input
	 *            the string to be scrubbed
	 * @return Input without certain characters;
	 */
	public static String removeXSSCharacters(String input) {
		final String[] xss = { "<", ">", "\"", "'", "%", ";", ")", "(", "&", "+", "-" };
		for (int i = 0; i < xss.length; i++) {
			input = input.replace(xss[i], "");
		}
		return input;
	}
	
	 /**
     * Returns an array of stringtokens from the source string.
     * The String "Leon Power Tools" with delimiter ' ' will return {"Leon","Power","Tools"}
     * Last change:  LR   15 Aug 98    4:23 pm
     *
     * @param source a {@link java.lang.String} object.
     * @param delimiter a char.
     * @return an array of {@link java.lang.String} objects.
     */
    public static final String[] tokenize(String source, char delimiter) {
        List<String> v = tokenize2vector(source, delimiter);
        String[] ret = new String[v.size()];
        for (int i = 0; i < v.size(); i++) {
            ret[i] = v.get(i);
        }
        return ret;
    }/*end fun tokenize()*/	
    
    /**
    * Return a Vector with tokens from the source string tokenized using the delimiter char.
    *
    * @param source a {@link java.lang.String} object.
    * @param delimiter a char.
    * @return a {@link java.util.List} object.
    */
   public static List<String> tokenize2vector(String source, char delimiter) {
       List<String> v = new ArrayList<>();
       StringBuilder currentS = new StringBuilder(source.length());
       for (int i = 0; i < source.length(); i++) {
           char c = source.charAt(i);
           if (c == delimiter) {
               if (currentS.length() > 0) {
                   v.add(currentS.toString());
               } else {
                   v.add("");
               }
               currentS = new StringBuilder();
           } else {
               currentS.append(c);
           }
       }
       if (currentS != null && currentS.length() > 0)
           v.add(currentS.toString());
       return v;
   }
    
   /**
    * Returns a source String with all occurences of 'c' removed.
    * removeChar("Leon's Power Tools", ' ') will return "Leon'sPowerTools".
    *
    * @param src a {@link java.lang.String} object.
    * @param c a char.
    * @return a {@link java.lang.String} object.
    */
   public static String removeChar(String src, char c) {
       StringBuilder ret = new StringBuilder(src.length());
       for (int i = 0; i < src.length(); i++)
           if (src.charAt(i) != c)
               ret.append(src.charAt(i));
       return ret.toString();
   }   
}
