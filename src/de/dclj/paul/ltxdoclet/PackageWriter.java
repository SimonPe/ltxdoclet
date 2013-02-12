package de.dclj.paul.ltxdoclet;

import com.sun.javadoc.*;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Schreibt die Dokumentation für ein Package.
 */
public class PackageWriter extends LaTeXWriter {

	/**
	 * Erstellt einen neuen PackageWriter.
	 * 
	 * @param pd
	 *            das zu dokumentierende Paket.
	 */
	public PackageWriter(PackageDoc pd) throws IOException {
		super(new File(configuration.toOutputFileName(pd), "package-doc.tex"));
		this.doc = pd;
	}

	private PackageDoc doc;

	/**
	 * Erstellt die Doku für das Paket.
	 */
	public void writeDoc() {
		configuration.root.printNotice("ltxdoclet: package-doc.tex für \""
				+ doc + "\" wird erstellt ...");
		ClassDoc[] classes = doc.allClasses();
		Arrays.sort(classes,new Comparator<ClassDoc>() {
			public int compare(ClassDoc a, ClassDoc b) {
				return String.CASE_INSENSITIVE_ORDER.compare(a.name(),b.name());
			}
		});
		// parallele Threads für die einzelnen Dateien.
		writeClasses(classes);
		chapter(Translator.getString("Package")+" ", doc);
		println();
		writeInlineTags(doc.firstSentenceTags());
		println();
		section(Translator.getString("Class-list"));
		println("\\begin{description}");
		for (int i = 0; i < classes.length; i++) {
			ClassDoc cd = classes[i];
			print("\\item[{");
			if (cd.isInterface()) {
				print("\\textit{" + createLink(cd) + "}");
			} else {
				print(createLink(cd));
			}
			println("}]");
			writeInlineTags(cd.firstSentenceTags());
			print("\\hfill");
			println(referenceTo(cd));
			println();
		}
		println("\\end{description}");
		section(Translator.getString("Package-description"));
		writeDescription(doc);
		// writeTags(doc.inlineTags());
		// Tag[] tags = doc.tags();
		// for (int i = 0; i < tags.length; i++)
		// {
		// print("[" + tags[i]+ "| kind:" + tags[i].kind()+ "| name: "
		// + tags[i].name() + "|text: "+ tags[i].text() + "]");
		// newLine();
		// }
		writeClassImports(classes);
		close();
	} // of PackageWriter.writeDoc()

	/**
	 * Erstellt die Doku für die Klassen in einzelnen Threads.
	 * 
	 * @param classes
	 *            die zu dokumentierenden Klassen.
	 */
	public void writeClasses(ClassDoc[] classes) {
		for (int i = 0; i < classes.length; i++) {
			final ClassDoc cd = classes[i];
//			Thread tr = new Thread(cd + "-Writer") {
//				public void run() {
					try {
						new ClassWriter(cd).writeDoc();
					} catch (IOException io) {
						io.printStackTrace();
						configuration.wasError = true;
						configuration.root.printError("Ausgabe für " + cd
								+ " konnte nicht " + "geschrieben werden.");
					} catch (RuntimeException ex) {
						configuration.wasError = true;
						throw ex;
					}

					finally {
//						configuration.threads.remove(this);
					}
//				}
//			}; // of Thread
			// configuration.threads.add(tr);
			// tr.start();
//			tr.run();
		} // of for
	}

	/**
	 * Erstellt die Input-Anweisungen, um die parallel erstellten Klassendokus
	 * in die Package-Doku einzubinden.
	 */
	public void writeClassImports(ClassDoc[] classes) {
		String pkgDir = configuration.toInputFileName(doc) + "/";
		for (int i = 0; i < classes.length; i++) {
			println("\\input{" + pkgDir + classes[i].name() + "}");
		}
	}

}
