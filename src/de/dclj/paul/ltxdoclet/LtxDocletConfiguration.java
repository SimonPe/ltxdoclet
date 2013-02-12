package de.dclj.paul.ltxdoclet;

//import com.sun.tools.doclets.internal.toolkit.*;
//import com.sun.tools.doclets.formats.html.*;

import java.io.File;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
// Compiler- und Tree-API.

/**
 * Konfiguration für unser Doclet.
 */
public class LtxDocletConfiguration {
	public enum Options {
		d(2), includesource(1), docencoding(2), doctitle(2), link(2, "link"), linkhtml(
				2, "link"), linkoffline(3, "link"), linkofflinehtml(3, "link"), linkfootnotehtml(
				4, "link"), linkendhtml(4, "link"), linkofflinefootnotehtml(3,
				"link"), linkofflineendhtml(3, "link"), linkpdf(2, "link"), linkofflinepdf(
				3, "link"), linkfootnotepdf(4, "link"), linkendpdf(4, "link"), pdf(
				1), pdfonly(1),

		classpath(2, "javac"), sourcepath(2, "javac"), encoding(2, "javac"), source(
				2, "javac");

		public final int numArgs;
		public final OptionKind kind;

		Options(int num) {
			this(num, "");
		};

		Options(int num, String kind) {
			this.numArgs = num;
			OptionKind tmp;
			try {
				tmp = OptionKind.valueOf(kind);
			} catch (Exception e) {
				tmp = null;
			}
			this.kind = tmp;
		}
		public static Options fromString(String str){
			if (str.charAt(0) == '-')
				str = str.substring(1);
			try {
				return Options.valueOf(str);
			} catch (Exception e) {
				return null;
			}
		}
	}

	public static enum OptionKind {
		javac, link
	}

	/**
	 * Das Verzeichnis, in dem alle erzeugten Daten abgelegt werden sollen.
	 * 
	 * Wird durch »-d« festgelegt.
	 */
	public File destdir;
	public File texdir;

	/**
	 * Die zu dokumentierende Software ist in diesem {@link RootDoc}-Objekt
	 * versteckt.
	 */
	public RootDoc root;

	/**
	 * Die Liste der Packages.
	 */
	public PackageDoc[] packages;

	/**
	 * Das zu verwendende Encoding für die Ausgabe-Dateien. Wird durch
	 * »-docencoding« festgelegt, wie auch beim Standarddoclet.
	 */
	public Charset docencoding;

	public List<Thread> threads = Collections
			.synchronizedList(new ArrayList<Thread>());
	public boolean wasError;

	/**
	 * Dieses Objekt erstellt Links.
	 */
	public LinkCreator linker;

	PrettyPrinter pp = new PrettyPrinter();

	private Map<Options, String[]> options = new HashMap<Options, String[]>();

	public boolean boolOpt(String opt) {
		return boolOpt(Options.fromString(opt));
	}
	public boolean boolOpt(Options opt) {
		return options.containsKey(opt);
	}

	public String[] getOpt(String opt) {
		return getOpt(Options.fromString(opt));
	}
	public String[] getOpt(Options opt) {
		return options.get(opt);
	}

	/**
	 * Merkt sich die Optionen aus {@code rd}.
	 */
	public void setOptions(RootDoc rd) {
		this.docencoding = Charset.defaultCharset();
		this.destdir = new File(System.getProperty("user.dir"));
		this.options.put(Options.doctitle, new String[] {
				Translator.getString("defaultDoctitle")});

		UniversalLinkCreator lc = new UniversalLinkCreator();
		this.linker = lc;

		this.root = rd;
		root.printNotice("Lese Optionen ...");
		for (String[] op : rd.options()) {
			root.printNotice("Option: " + Arrays.toString(op));
			try {
				Options value = Options.valueOf(op[0].substring(1));
				options.put(value, Arrays.copyOfRange(op, 1, value.numArgs));
				if (value.kind != null) {
					switch (value.kind) {
					case link:
						lc.addOption(op);
						break;
					default:
						break;
					}
				}
			} catch (IllegalArgumentException e) {
				root.printError("Option: " + Arrays.toString(op)
						+ " is not supported");
			}
		}

		String[] arg;
		if ((arg = getOpt("d")) != null) {
			this.texdir = this.destdir = new File(arg[0]);
			destdir.mkdirs();
		}
		if ((arg = getOpt("docencoding")) != null) {
			this.docencoding = Charset.forName(arg[0]);
		}
		if (boolOpt("pdfonly")) {
			this.texdir = new File(this.destdir, "tex");
			this.texdir.mkdirs();
			this.options.put(Options.pdf, new String[]{});
		}

		this.packages = rd.specifiedPackages();
		Arrays.sort(this.packages, new Comparator<PackageDoc>() {
			public int compare(PackageDoc a, PackageDoc b) {
				return String.CASE_INSENSITIVE_ORDER.compare(a.name(), b.name());
			}
		});
		// TODO
		root.printNotice("... Optionen gelesen.");
	}

	/**
	 * Ermittelt, ob dieses Doclet eine Option annimmt, und wenn ja, wie viele
	 * Argumente sie nimmt.
	 * 
	 * @return die Anzahl der Kommandozeilenargumente, die diese Option
	 *         darstellen, inklusive der Option selbst.
	 */
	public int optionLength(String option) {
		if ("-help".equals(option)) {
			System.out.println(optionHelp());
			return 1;
		}
		try {
			return Options.valueOf(option.substring(1)).numArgs;
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * prints some help about the options. We will load the text from the
	 * resource bundle.
	 */
	public String optionHelp() {

		ResourceBundle bundle = ResourceBundle.getBundle(
				"de.dclj.paul.ltxdoclet.help", new HelpTextBundleControl());
		Charset cs = Charset.defaultCharset();
		String text = bundle.getString("help");
		if (!"〈...〉".contentEquals(cs.decode(cs.encode("〈...〉")))) {
			text = text.replace('〈', '<').replace('〉', '>');
		}
		return MessageFormat.format(text, cs);
	}

	public boolean validOptions(String[][] options, DocErrorReporter rep) {
		// TODO
		return true;
	}

	// public WriterFactory getWriterFactory() {
	// return null;
	// }

	// public Comparator getMemberComparator() {
	// return null;
	// }

	/**
	 * Erstellt den Label-Namen für das angegebene Programmelement.
	 */
	public String toRefLabel(Doc doc) {
		if (doc instanceof PackageDoc) {
			return doc + "-package";
		}
		if (doc instanceof ClassDoc) {
			return doc + "-class";
		}
		if (doc instanceof RootDoc) {
			return "over-view";
		}
		// TODO
		return removeSpaces(doc.toString());
	}

	private String removeSpaces(String t) {
		StringBuilder b = new StringBuilder(t);
		int index = 0;
		while ((index = b.indexOf(" ", index)) >= 0) {
			b.deleteCharAt(index);
		}
		return b.toString();
	}

	String toInputFileName(PackageDoc d) {
		return d.toString().replace('.', '/');
	}

	File toOutputFileName(PackageDoc d) {
		String newName = d.toString().replace('.', '/');
		File dir = new File(destdir, newName);
		dir.mkdirs();
		return dir;
	}

}
