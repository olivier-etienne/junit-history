package com.francetelecom.orangetv.junithistory.server.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.francetelecom.orangetv.junithistory.server.manager.IHtmlBalise;

/**
 * Helper class for HTML rendering
 */
public class HtmlUtils implements IHtmlBalise {
	/**
	 * Writes the given stack as HTML.
	 * 
	 * @param writer
	 * @param iThrowable
	 * @throws IOException
	 */
	public static void writeStack(PrintWriter writer, Throwable iThrowable) throws IOException {
		// --- 1: title is the exception message (or classname)
		writer.print("<p style='font-size: 10pt; color: black;'>");
		writer.print("<span style='font-size: 12pt; color: #FF9933;'>");

		if (iThrowable.getMessage() == null) {
			writer.print(iThrowable.getClass().getName());
		} else {
			writer.print(iThrowable.getMessage());
		}

		writer.print("</span>\n");
		writer.print("<br>\n");

		// --- 2: exception type (classname)
		writer.print("Exception type : ");
		writer.print(iThrowable.getClass().getName());
		writer.print("<br>\n");

		// --- 3: exception origin
		StringWriter stackInString = new StringWriter();
		PrintWriter print = new PrintWriter(stackInString);
		iThrowable.printStackTrace(print);
		print.close();

		StringTokenizer tokens = new StringTokenizer(stackInString.toString(), "\n");

		if (tokens.hasMoreElements()) {
			tokens.nextElement();
		}

		while (tokens.hasMoreElements()) {
			String token = tokens.nextToken().trim();
			if (token.startsWith("at ")) {
				token = token.substring(3);
				writer.print("Exception thrown in : ");
				writer.print("<span style='font-size: 12px; color: #FF9933;'>");
				// write(shortenClass(token));
				writer.print(token);
				writer.print("</span>\n");
				writer.print("<br>\n");
				break;
			}
		}

		// --- 4: short stack trace
		/*
		tokens= new StringTokenizer( stackInString.toString(), "\n" );
		write("Stack trace :\n");
		write("<UL style='font-size: 10px; color: white;'>\n");
		if( tokens.hasMoreElements() ) tokens.nextElement();
		while( tokens.hasMoreElements() )
		{
			String token= tokens.nextToken().trim();
			if( token.startsWith("at ") )
				token= token.substring(3);
			else
				continue;
			if( token.indexOf("java.lang.reflect.Method.invoke")>=0 )
				break;
			if( token.indexOf("javax.servlet.http.HttpServlet.service")>=0 )
				break;
			write( "<LI>"+shortenClass(token)+"</LI>\n" );
		}
		write( "</UL>\n");
		*/

		// --- 5: expandable full stack trace
		// writer.print(
		// "<span style='font-size: 12px; color: #FF9933; text-decoration: underline; cursor: hand;' onclick='stack.style.display=\"block\";'>Show Full Stack</span><br/>\n"
		// );
		writer.print("<pre id=stack style='font: sans-serif; font-size: 8pt; color: black;'>\n");
		writer.print(stackInString.toString());
		writer.print("\n</pre>\n");
		writer.print("</p>\n");
	}

	/**
	 * Encodes the given text to HTML encoding.
	 * 
	 * @param text
	 *            Text to encode.
	 * @return The encoded HTML text.
	 */
	public static String encode2HTML(String text) {
		return encode2HTML(text, false);
	}

	public static String encode2HTML(ListLines lines) {

		StringBuilder sb = new StringBuilder();
		if (lines != null) {

			for (String line : lines.getLines()) {
				sb.append(encode2HTML(line));
				sb.append(BR);
			}
		}

		return sb.toString();
	}

	/**
	 * Encodes the given text to HTML encoding.<br>
	 * If the text starts with "\html\", then this prefix is stipped from text,
	 * and
	 * the text is returned "as is" (no encoding applied, assuming the text is
	 * already encoded).
	 * 
	 * @param text
	 *            Text to encode.
	 * @param iNBSP
	 *            Tells whether to translate white spaces into non-breaking
	 *            spaces or not.
	 * @return The encoded HTML text.
	 */
	public static String encode2HTML(String text, boolean iNBSP) {
		if (text == null) {
			return "";
		}
		if (text.startsWith("\\html\\")) {
			return text.substring(6);
		}

		int nbChars = text.length();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < nbChars; i++) {
			char c = text.charAt(i);
			if (iNBSP && c == ' ') {
				sb.append(SPACE);
			} else {
				switch (c) {
				case '\n': // \n turned into <br>
					sb.append(BR);
					break;
				case '\r': // \r turned into breaking whitespace
					sb.append(" ");
					break;
				case '\t': // TAB is turned into 4 non-braking spaces
					sb.append(SPACE);
					sb.append(SPACE);
					sb.append(SPACE);
					sb.append(SPACE);
					break;
				case '<':
					sb.append(INF);
					break;
				case '>':
					sb.append(SUP);
					break;
				case '&':
					sb.append(ESPERLUETTE);
					break;
				case '\"':
					sb.append(DOUBLE_QUOTE);
					break;
				case '\'':
					// no: doesn't work with IE
					// sb.append("&apos;");
					sb.append(SIMPLE_QUOTE);
					break;
				default:
					sb.append(c);
					break;
				}
			}
		}
		return sb.toString();
	}

	public static String encodeOutput2HTML(String output) {
		if (output == null) {
			return "";
		}

		// first convert all ANSI commands to non HTML chars
		output = output.replaceAll("&#27;\\[", "##27;[");

		// then encode to HTML
		output = HtmlUtils.encode2HTML(output);

		// finally convert all ANSI color codes
		Pattern colorPattern = Pattern.compile("##27;\\[(\\d);(\\d+)m");
		Matcher m = colorPattern.matcher(output);
		StringBuffer replaced = new StringBuffer(output.length());
		while (m.find()) {
			int i1 = Integer.parseInt(m.group(1));
			int i2 = Integer.parseInt(m.group(2));
			String color = "black";
			switch (i1) {
			case 0: {
				switch (i2) {
				case 30: // BLACK
					color = "#000";
					break;
				case 31: // RED
					color = "#F00";
					break;
				case 32: // GREEN
					color = "#080";
					break;
				case 33: // BROWN
					color = "brown";
					break;
				case 34: // BLUE
					color = "#00F";
					break;
				case 35: // PURPLE
					color = "purple";
					break;
				case 36: // CYAN
					color = "#5FF";
					break;
				case 37: // LIGHT_GRAY
					color = "#999";
					break;
				}
			}
			case 1: {
				switch (i2) {
				case 30: // DARK_GRAY
					color = "#555";
					break;
				case 31: // LIGHT_RED
					color = "#F55";
					break;
				case 32: // LIGHT_GREEN
					color = "#292";
					break;
				case 33: // YELLOW
					color = "#FA0";
					break;
				case 34: // LIGHT_BLUE
					color = "#55F";
					break;
				case 35: // LIGHT_PURPLE
					color = "mediumpurple";
					break;
				case 36: // LIGHT_CYAN
					color = "#05F";
					break;
				case 37: // WHITE
					color = "white";
					break;
				}
			}
			}
			m.appendReplacement(replaced, "<span style='color: " + color + "'>");
		}
		m.appendTail(replaced);
		output = replaced.toString();
		output = output.replaceAll("##27;\\[0m", "</span>");

		return output;
	}

	public static String encode2Title(String text) {
		if (text == null)
			return "";

		int nbChars = text.length();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < nbChars; i++) {
			char c = text.charAt(i);
			{
				switch (c) {
				case ' ':
					sb.append("&nbsp;");
					break;
				case '\n':
					sb.append("\n");
					break;
				case '\r':
					break;
				case '\t': // TAB is turned into 4 non-braking spaces
					sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
					break;
				case '<':
					sb.append("&lt;");
					break;
				case '>':
					sb.append("&gt;");
					break;
				case '&':
					sb.append("&amp;");
					break;
				case '\"':
					sb.append("&quot;");
					break;
				case '\'':
					// no: doesn't work with IE
					// sb.append("&apos;");
					sb.append("&#39;");
					break;
				default:
					sb.append(c);
					break;
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Encodes the given text to JavaScript string (with escaping).
	 * 
	 * @param text
	 *            Text to encode.
	 * @return The escaped JavaScript string.
	 */
	public static String encode2JsString(String text) {
		int nbChars = text.length();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < nbChars; i++) {
			char c = text.charAt(i);
			switch (c) {
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\"':
				sb.append("\\\"");
				break;
			case '\'':
				sb.append("\\'");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}
}
