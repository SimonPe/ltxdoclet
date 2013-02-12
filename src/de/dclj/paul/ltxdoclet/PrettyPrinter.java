package de.dclj.paul.ltxdoclet;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import com.sun.javadoc.ProgramElementDoc;
import com.sun.source.tree.TreeVisitor;

/**
 * Ein Schön-Drucker für Quelltext.
 * 
 * Wir implementieren {@link TreeVisitor}, um den Syntaxbaum abzuarbeiten.
 * 
 * Von außen müssen nur {@link #printSource} und der Konstruktor verwendet
 * werden.
 * 
 * @author <a href="mailto:paulo@heribert.local">Paul Ebermann</a>
 * @version $Id$
 */
public class PrettyPrinter {

	/**
	 * Druckt den Quelltext zum angegebenen dokumentierten Element aus.
	 */
	public void printSource(ProgramElementDoc doc, LaTeXWriter target) {

		//TODO: clean method to detect implicit Methods
		if(doc.containingClass().position().line()==doc.position().line() )
			return;
		
		try {

			target.println("\\begin{lstlisting}[tabsize=4, breaklines, resetmargins=true, breakautoindent,numbers=left, numberstyle=\\tiny, numbersep=5pt, language=Java, firstnumber="
					+ doc.position().line() + "]");

			LineNumberReader reader = new LineNumberReader(new FileReader(doc
					.position().file()));
			for (int i = 1; i < doc.position().line(); i++)
				reader.readLine();
			int pos = doc.position().column() - 1;
			int status = 0;
			int tabs = 0;
			char quote = 0;
			boolean blockComment = false;
			boolean go = true;
			boolean method = doc.isConstructor() || doc.isMethod();
			while (go) {
				String line = reader.readLine();
				if (line == null)
					break;
				try {
					if(tabs==0){
						while (line.startsWith("\t", tabs))
							tabs++;
						pos -= tabs*8;
						if (pos < 0)
							pos = 0;
					}
					while (pos < line.length()) {
						char cur = line.charAt(pos);
						if (blockComment) {
							if (cur == '/' && pos > 0
									&& line.charAt(pos - 1) == '*')
								blockComment = false;
						} else if (quote != 0) {
							if (cur == quote) {
								int i;
								for (i = 1; i < pos
										&& line.charAt(pos - i) == '\\'; i++)
									;
								if (i % 2 == 1)
									quote = 0;
							}
						} else
							switch (cur) {
							case '"':
							case '\'':
								quote = cur;
								break;
							case '{':
							case '(':
								status++;
								break;
							case '}':
								go = !(--status == 0);
								break;
							case ')':
								status--;
								break;
							case '/':
								if (pos + 1 < line.length())
									switch (line.charAt(pos + 1)) {
									case '/':
										pos = line.length();
										break;
									case '*':
										pos++;
										blockComment = true;
									}
								break;
							case ',':
								if (method)
									break;
							case ';':
								go = !(status == 0);
							}
						pos++;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
				}
				target.println(line);
				pos = 0;
			}
			reader.close();
			target.println("\\end{lstlisting}");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
