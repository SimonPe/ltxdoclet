package de.dclj.paul.ltxdoclet;

import com.sun.javadoc.*;
import java.io.*;

/**
 * Ein Writer zum Schreiben einer Klasse (inklusive der Methoden).
 */
public class ClassWriter extends LaTeXWriter {

	private ClassDoc doc;

	public ClassWriter(ClassDoc cd) throws IOException {
		super(new File(configuration.toOutputFileName(cd.containingPackage()),
				cd.name() + ".tex"));
		this.doc = cd;
	}

	public void writeDoc() {
		try {
			configuration.root.printNotice("ltxdoclet: Klassen-Doku für \""
					+ doc + "\" wird erstellt ...");
			println("   % Api-Dokumentation für Klasse " + doc
					+ " (noch nicht fertig). ");

			if (doc.isInterface()) {
				section(Translator.getString("Interface")+" ", doc, doc.name());
			} else if (doc.isOrdinaryClass()) {
				section(Translator.getString("Class")+" ", doc, doc.name());
			} else if (doc.isException()) {
				section(Translator.getString("Exception")+" ", doc, doc.name());
			} else if (doc.isError()) {
				section(Translator.getString("Error")+" ", doc, doc.name());
			} else if (doc.isEnum()) {
				section(Translator.getString("Enum")+" ", doc, doc.name());
			}
			// println(referenceTarget(doc));

			subsection(Translator.getString("overview"));
			// TODO: Deklaration
			writeDescription(doc);

			ConstructorDoc[] konstr = doc.constructors();
			MethodDoc[] meth = doc.methods();
			FieldDoc[] fields = doc.fields();
			FieldDoc[] consts = doc.enumConstants();

			subsection(Translator.getString("Contents"));
			// TODO

			writeMemberList(consts, Translator.getString("Enum-const"));
			writeMemberList(fields, Translator.getString("Fields"));
			writeMemberList(konstr, Translator.getString("Constructors"));
			writeMemberList(meth, Translator.getString("Methods"));

		} finally {
			configuration.root.printNotice("ltxdoclet: ... Klassen-Doku für \""
					+ doc + "\" beendet.");
			close();
		}
	}

	public <X extends MemberDoc> void writeMemberList(X[] liste, String titel) {
		if (liste.length > 0) {
			subsection(titel);
			println("\\begin{description}");
			for (X d : liste) {
				writeMemberDoc(d);
			}
			println("\\end{description}");
		}
	}

	public <X extends MemberDoc> void writeMemberDoc(X d) {
		println("\\item[{" + referenceTarget(d, asLaTeXString(d.name())) + "}]");
		print("~ "); // damit newParagraph() unten auch wirklich eine neue Zeile
						// anfängt.
		// TODO: Signatur
		writeDescription(d);
		// if (d.isField()) {
		// writeDeclaration((FieldDoc) d);
		// }
		if (configuration.getOpt("includesource") != null) {
			try {
				configuration.pp.printSource(d, this);
			} catch (RuntimeException ex) {
				configuration.root
						.printError("bei printSource(\"" + d + "\"):");
				configuration.wasError = true;
				// ex.printStackTrace();
				throw ex;
			} finally {
				newParagraph();
			}
		}
	}

}
