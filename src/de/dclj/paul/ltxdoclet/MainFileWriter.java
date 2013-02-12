package de.dclj.paul.ltxdoclet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

import com.sun.javadoc.PackageDoc;

/**
 * Ein Writer für die Haupt-Datei.
 */
public class MainFileWriter extends LaTeXWriter {

	/**
     *
     */
	public MainFileWriter() throws IOException {
		super(new File(configuration.texdir, "doku-main.tex"));
	}

	/**
	 * Erstellt die komplette Doku für das Projekt.
	 * 
	 * Dazu werden in parallelen Threads {@link PackageWriter} für die einzelnen
	 * Packages aufgerufen und dann die Main-Datei erstellt.
	 */
	public void writeDoku() {
		writePackages();
		configuration.root
				.printNotice("ltxdoclet: doku-main.tex wird erstellt ...");
		println("   % Damit beim Compilieren nicht bei jedem Fehler angehalten wird");
		println("\\scrollmode");
		println();
		writePreamble();
		println("\\begin{document}");
		println();
		chapter(Translator.getString("overview"), false);
		ltxwrite(configuration.getOpt("doctitle")[0]
				+ Translator.getString("overviewIntro"));
		// section("Package-Liste", );
		writePackageList();
		section("Description", false);
		writeOverview();
		// println("\renewcommand{\thechapter}{\ara{chapter}}");
		// println("\\setcounter{chapter}{0}");
		writePackageImports();
		println("\\appendix");
		// ...
		println("\\end{document}");
		close();
		configuration.root.printNotice("ltxdoclet: ... doku-main.tex fertig.");
		configuration.root
				.printNotice("ltxdoclet: warte auf Beendigung der anderen Dateien ...");
		waitForAllThreads();
		if (configuration.getOpt("pdf") != null) {
			configuration.root.printNotice("comiling tex file");
			try {
				String[] cmdArray = new String[] {
						"xelatex",
						"-output-directory="
								+ configuration.destdir.getAbsolutePath(),
						"doku-main.tex"} ;
				Process xelatex = Runtime.getRuntime()
						.exec(cmdArray, null, configuration.texdir);
				xelatex.waitFor();
				configuration.root.printNotice("second pass");				
				xelatex = Runtime.getRuntime()
						.exec(cmdArray, null, configuration.texdir);
				xelatex.waitFor();
			} catch (IOException e) {
				configuration.root
						.printError("could not execute xelatex, please check your instalation");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (configuration.getOpt("pdfonly") != null) {
			
		}
		configuration.root.printNotice("ltxdoclet: Fertig!");
	}

	/**
	 * Wartet, bis alle Threads in LaTeXWriter.configuration.threads beendet
	 * wurden und sich aus der Liste streichen.
	 */
	public void waitForAllThreads() {
		List<Thread> threads = configuration.threads;
		while (true) {
			Thread akt;
			synchronized (threads) {
				if (!configuration.threads.isEmpty())
					akt = (Thread) threads.get(0);
				else
					break;
			}
			try {
				akt.join();
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
				configuration.wasError = true;
				return;
			}
		} // of while
	} // of MainFileWriter.waitForAllThreads()

	/**
	 * Kopiert eine Datei (z.B. ein LaTeX-Paket) aus unseren Programm-Ressourcen
	 * in das Ausgabeverzeichnis.
	 * 
	 * @param packageName
	 *            der Name der Datei, z.B. {@code ltxdoclet.sty}.
	 */
	private void copyPackage(String packageName) {
		try {
			InputStream in = new BufferedInputStream(
					MainFileWriter.class.getResourceAsStream(packageName));
			OutputStream out = new BufferedOutputStream(new FileOutputStream(
					new File(configuration.destdir, packageName)));
			pipeInToOut(in, out);
			in.close();
			out.close();
		} catch (IOException io) {
			throw new RuntimeException("konnte das Package nicht kopieren: ",
					io);
		}
	}

	/**
	 * Leitet den kompletten Inhalt einen InputStreams in einen OutputStream um.
	 * 
	 * Diese Methode liest und schreibt jedes byte einzeln - man sollte also
	 * gepufferte Streams verwenden, damit es nicht zu langsam wird.
	 */
	private void pipeInToOut(InputStream in, OutputStream out)
			throws IOException {
		int r;
		while (0 <= (r = in.read())) {
			out.write(r);
		}
	}

	private void writePreamble() {
		println("   % Report scheint für eine API jedenfalls besser als Artikel");
		println("\\documentclass[final, 11pt, a4paper]{scrreprt}");
		println();
//		println("\\usepackage{fontspec}");
//		println("\\usepackage{xunicode}");
		println("\\usepackage{listings}");
//		println("\\setmonofont{Liberation Mono}");
		// TODO: polyglossia immer mit passender Option laden
		if (Locale.getDefault().getLanguage().equals("de")) {
			println("  % Neue deutsche Silbentrennung");
			println("\\usepackage[german]{polyglossia}");
			println();
		}
		println("\\usepackage[pdfborderstyle={/S/U/W 1}]{hyperref}");
		println("\\usepackage{enumerate}");
		println();
//		println("\\usepackage[dvipsnames]{color}");
		println();
		println("\\usepackage{ltxdoclet}");
		println();
		;
		copyPackage("ltxdoclet.sty");
	}

	/**
	 * Erstellt den Überblick über die Package-Sammlung, mit Beschreibung etc.
	 */
	private void writeOverview() {
		writeDescription(configuration.root);
	}

	/**
	 * Gibt eine Liste der Packages aus.
	 */
	private void writePackageList() {
		println("   % Liste der Packages:");
		println("\\begin{enumerate}[1.]");
		PackageDoc[] pkgs = configuration.packages;
		for (int i = 0; i < pkgs.length; i++) {
			PackageDoc pd = pkgs[i];
			println("\\item " + createLink(pd) + "\\dotfill " + referenceTo(pd));
			newLine();
			writeInlineTags(pd.firstSentenceTags());

		}
		println("\\end{enumerate}");
	}

	private void writePackageImports() {
		PackageDoc[] pkgs = configuration.packages;
		for (int i = 0; i < pkgs.length; i++) {
			String pkgName = configuration.toInputFileName(pkgs[i]);
			println("\\input{" + pkgName + "/package-doc.tex" + "}");
		} // of for
	}

	private void writePackages() {
		configuration.root.printNotice("Package-Dokus werden erstellt ...");
		PackageDoc[] pkgs = configuration.packages;
		for (int i = 0; i < pkgs.length; i++) {
			final PackageDoc pd = pkgs[i];
			Thread thread = new Thread(pd + "-Writer") {
			public void run() {
			try {
				new PackageWriter(pd).writeDoc();
			} catch (IOException io) {
				configuration.root.printError("Ausgabe für " + pd + " konnte "
						+ "nicht geschrieben werden!");
			} catch (RuntimeException ex) {
				configuration.wasError = true;
				throw ex;
			} finally {
				configuration.threads.remove(this);
			}
			} // of run()
			};
			configuration.threads.add(thread);
			thread.start();
		} // of for
	} // of MainFileWriter.writePackages()
}
