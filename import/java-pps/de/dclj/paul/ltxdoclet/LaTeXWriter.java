package paul.ltxdoclet;

import com.sun.tools.doclets.*;
import com.sun.javadoc.*;

import java.io.*;


/**
 * Einige generelle Methoden zum Schreiben von LaTeX-Dokumenten.
 */
public class LaTeXWriter
	extends PrintWriter
{
	/*
	 * Wenn im LaTeX-Quelltext ein "\" herauskommen soll, muss hier immer "\\" eingetippt
	 * werden.
	 */


	public LaTeXWriter(File filename)
		throws IOException
	{
		super(new FileWriter(filename));
		println("   % /--------------------------------------------\\");
		println("   % | API-Dokumentation f�r einige Java-Packages |");
		println("   % |    (genaueres siehe doku-main.tex).        |");
		println("   % | LaTeX-Ausgabe erstellt von 'ltxdoclet'.    |");
		println("   % | Dieses Programm stammt von Paul Ebermann.  |");
		println("   % \\--------------------------------------------/");
		println();
	}
	
	private String replace(String org, String sub1, String sub2)
	{
		StringBuffer buf = new StringBuffer(org);
		int slen = sub1.length();
		for (int i = 0; i < buf.length(); i++)
		{
			if(buf.substring(i, i + slen).equals(sub1))
			{
				buf.replace(i, i+slen, sub2);
			}
		}
		return buf.toString();
	}
	
		// Hier alle Ersetzungen eintragen ...
	final String[][] ltxsymb =
	{
		{"�", "!`"},
		{"�", "{\\pounds}"},
		{"�", "{\\S}"},
		{"�", "{\\copyright}"},
		{"�", "{\\pm}"},
		{"�", "{\\P}"},
		{"�", "{\\cdot}"},
		{"�", "?`"},
		{"�", "\\`{A}"},
		{"�", "\\'{A}"},
		{"�", "\\^{A}"},
		{"�", "\\~{A}"},
		{"�", "\\\"{A}"},
		{"�", "{\\AA}"},
		{"�", "{\\AE}"},
		{"�", "\\c{C}"},
		{"�", "\\`{E}"},
		{"�", "\\'{E}"},
		{"�", "\\^{E}"},
		{"�", "\\\"{E}"},
		{"�", "\\`{I}"},
		{"�", "\\'{I}"},
		{"�", "\\^{I}"},
		{"�", "\\\"{I}"},
		{"�", "\\~{N}"},
		{"�", "\\`{O}"},
		{"�", "\\'{O}"},
		{"�", "\\^{O}"},
		{"�", "\\~{O}"},
		{"�", "\\\"{O}"},
		{"�", "{\\times}"},
		{"�", "{\\O}"},
		{"�", "\\`{U}"},
		{"�", "\\'{U}"},
		{"�", "\\^{U}"},
		{"�", "\\\"{U}"},
		{"�", "\\'{Y}"},
		{"�", "{\\ss}"},
		{"�", "\\`{a}"},
		{"�", "\\'{a}"},
		{"�", "\\^{a}"},
		{"�", "\\~{a}"},
		{"�", "\\\"{a}"},
		{"�", "{\\aa}"},
		{"�", "{\\ae}"},
		{"�", "\\c{c}"},
		{"�", "\\`{e}"},
		{"�", "\\'{e}"},
		{"�", "\\^{e}"},
		{"�", "\\\"{e}"},
		{"�", "\\`{\\i}"},
		{"�", "\\'{\\i}"},
		{"�", "\\^{\\i}"},
		{"�", "\\\"{\\i}"},
		{"�", "\\~{n}"},
		{"�", "\\`{o}"},
		{"�", "\\'{o}"},
		{"�", "\\^{o}"},
		{"�", "\\~{o}"},
		{"�", "\\\"{o}"},
		{"�", "{\\div}"},
		{"�", "{\\o}"},
		{"�", "\\`{u}"},
		{"�", "\\'{u}"},
		{"�", "\\^{u}"},
		{"�", "\\\"{u}"},
		{"�", "\\'{y}"},
		{"�", "\\\"{y}"}
	};
	
		/**
		 * wandelt einen Unicode-String in die entsprechenden
		 * LaTeX-Symbole um.
		 */
	public String asLaTeXString(String s)
	{
	
		for (int i = 0; i < ltxsymb.length; i++)
		{
			s = replace(s, ltxsymb[i][0], ltxsymb[i][1]);
		}
		return s;
	}
	
	
	public String asLaTeXString(Doc d)
	{
		return asLaTeXString(d.toString());
	}
	
		/**
		 * gibt den angegebenen Text, umgewandelt in LaTeX-Befehle, aus.
		 */
	public void ltxwrite(String text)
	{
		println(asLaTeXString(text));
	}
	
		/**
		 * Beginnt ein neues Kapitel.
		 * @param name  Name / �berschrift des Kapitels
		 * @param num  Numerierung erw�nscht?
		 */
	public void chapter(String name, boolean num)
	{
		println("\\chapter" + (num? "" : "*") + "{" + asLaTeXString(name) + "}");
	}
	
	public void chapter(String name)
	{
		chapter(name, true);
	}
	
	
		/**
		 * Beginnt einen neuen Abschnitt.
		 */
	public void section(String name)
	{
		println("\\section{" + asLaTeXString(name) + "}");
	}
	
		/**
		 * Beginnt einen neuen Unterabschnitt.
		 */
	public void subsection(String name)
	{
		println("\\subsection{" + asLaTeXString(name) + "}");
	}
	
		/**
		 * Beginnt einen neuen Unterunterabschnitt.
		 */
	public void subsubsection(String name)
	{
		println("\\subsubsection{" + asLaTeXString(name) + "}");
	}
	
		/**
		 * gibt den angegebenen Text kursiv aus.
		 */
	public void italic(String name)
	{
		println("\\textit{" + asLaTeXString(name) + "}");
	}
	
		/**
		 * gibt den angegebenen Text fett aus.
		 */
	public void bold(String text)
	{
		println("\\textbf{" + asLaTeXString(text) + "}");
	}
	
		/**
		 * Ermittelt eine Referenz zu dem angegebenen Programmelement.
		 */
	public String referenceTo(Doc doc)
	{
		if (doc instanceof PackageDoc)
		{
			return "\\pageref{" + doc + "-package}";
		}
		if (doc instanceof ClassDoc)
		{
			return "\\pageref{" + doc + "-class}";
		}
		if (doc instanceof RootDoc)
		{
			return "\\pageref{over-view}";
		}
		// sonst MemberDoc
		return "\\pageref{doc}";
	}
	
	public String referenceTarget(Doc doc)
	{
		if (doc instanceof PackageDoc)
		{
			return "\\label{" + doc + "-package}";
		}
		if (doc instanceof ClassDoc)
		{
			return "\\label{" + doc + "-class}";
		}
		if (doc instanceof RootDoc)
		{
			return "\\label{over-view}";
		}
		return "\\label{" + doc + "}";
	}
	
		/**
		 * Schreibt mehrere Tags nacheinander aus. Dies ist gedacht f�r Inline- und Text-Tags.
		 * Diese werden z.B. von Doc.inlineTags() und Doc.firstSentenceTags() zur�ckgegeben.
		 */
	public void writeTags(Tag[] tags)
	{
		for (int i = 0; i < tags.length; i++)
		{
			Tag t = tags[i];
			if (t.kind().equalsIgnoreCase("Text"))    // Puren Text umwandeln
				ltxwrite(t.text());
			else if(t.name().equals("@LaTeX"))  // LaTeX-Quelltext direkt verwenden
				println(t.text());
			else if(t instanceof SeeTag)        // Inline-See-Tag (@link)
			{
				SeeTag st = (SeeTag)t;
				MemberDoc md = st.referencedMember();
				if (md != null)
				{
					if (md.isIncluded())
					{
						if (st.label().equals(""))
						{
							println(asLaTeXString(md) + " [" + referenceTo(md)+ "]");
						}
						else 
						{
							println(st.label() + " ["+ referenceTo(md)+ "]");
						}
					}
					else
					{
						if (st.label().equals(""))
						{
							println(asLaTeXString(md) + " [" + referenceTo(md)+ "]");
						}
						else 
						{
							println(st.label() + "[ nicht hier ]");
						}
					}
					continue;
				}        // if if
				ClassDoc cd = st.referencedClass();
				if (cd != null)
				{
					if (cd.isIncluded())
					{
						if (st.label().equals(""))
						{
							println(asLaTeXString(cd) + " [" + referenceTo(cd)+ "]");
						}
						else 
						{
							println(st.label() + " ["+ referenceTo(cd)+ "]");
						}
					}
					else 
					{
						if (st.label().equals(""))
						{
							println(asLaTeXString(cd) + " [" + referenceTo(cd)+ "]");
						}
						else 
						{
							println(st.label() + "[ nicht hier ]");
						}
					}
					continue;
				}
				PackageDoc pd = st.referencedPackage();
				// ...
			}        // of else
		}        // of for
	}        // of writeTags



	public void newLine()
	{
		println("\\\\");
	}

	/************/
	
	static LtxDocletConfiguration configuration;


}
