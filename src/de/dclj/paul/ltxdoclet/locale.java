package de.dclj.paul.ltxdoclet;

import java.util.HashMap;
import java.util.ListResourceBundle;

public class locale extends ListResourceBundle {

	@Override
	protected Object[][] getContents() {
		return contents;
	}
	private Object[][] contents = {
			{"opthelp", new HashMap<String,String[]>(){{
				put("d", new String[]{"dir", "Directory in which to put the generated .tex files."});
				put("includesource", new String[]{"Add source code to the created documentation. We only add source code for documented elements, so make sure to also add -private if you want the complete source code."});
				put("docencoding", new String[]{"enc", "Name of the encoding of the LaTeX files. Default is the default encoding of the system."});
				put("pdf", new String[]{"Compile the resulting Latex files using xelatex (note: xelatex must be in your PATH"});
				put("pdfonly", new String[]{"Remove all generated files except for the pdf file, implies -pdf"});
				put("doctitle", new String[]{"title", "Document title."});
				put("linkhtml", new String[]{"url","Create external links to a javadoc HTML documentation."});
				put("link", new String[]{"url","Synonymous to -linkhtml"});
				put("linkfootnotehtml", new String[]{"url", "linktitle", "Create external links to a javadoc HTML documentation in the form of footnotes, using the title for the reference."});
				put("linkendhtml", new String[]{"url", "linktitle", "Create external links to a javadoc HTML documentation at the end of the file, using the title for the reference."});
				put("linkofflinehtml", new String[]{"url", "package-list-url", "Like -linkhtml, but look for the package list at 〈package-list-url〉 instead of 〈url〉."});
				put("linkoffline", new String[]{"url","package-list-url", "Synonymous to -linkofflinehtml"});
				put("linkofflinefootnotehtml", new String[]{"url", "package-list-url", "Like -linkfootnotehtml,  but look for the package list at 〈package-list-url〉 instead of 〈url〉."});
				put("linkofflineendhtml", new String[]{"url", "package-list-url", "linktitle", "Like -linkendhtml,  but look for the package list at 〈package-list-url〉 instead of 〈url〉."});
				put("linkpdf", new String[]{"pdf-url", "Creates links to another PDF file (should be created withthis doclet). The package-list will be searched in the same directory."});
				put("linkofflinepdf", new String[]{"pdf-url", "pkglst-url", "Creates links to another PDF file (should be created with this doclet). The package-list will be searched in 〈pkglst-url〉."});
				put("linkfootnotepdf", new String[]{"pdf-url", "idx-url", "linktitle", "Creates links to another PDF file (should be created with this doclet), in the form of footnotes. 〈idx-url〉 is a LaTeX .idx file used to get the page numbers, 〈linktitle〉 will be used for as the reference title."});
				put("linkendpdf", new String[]{"pdf-url", "idx-url", "linktitle", "Creates links to another PDF file (should be created with this doclet), at the end of the file. 〈idx-url〉 is a LaTeX .idx file used to get the page numbers, 〈linktitle〉 will be used for as the reference title."});
			}}},
			{ "overview", "Overview" },
			{ "defaultDoctitle", "The Package-Collection"},
			{ "overviewIntro", " consists of the following packages. A brief description follows thereafter." },

			{ "Package", "Package"},
			{ "Interface", "Interface"},
			{ "Class", "Class"},
			{ "Exception", "Exception"},
			{ "Error", "Error"},
			{ "Enum", "Enum"},
			
			{ "Fields", "Fields"},
			{ "Constructors", "Constructors"},
			{ "Methods", "Methods" },
			{ "Enum-const", "Enum constants" },
			
			{ "Class-list","Classlist"},
			{ "Package-description", "Package description"},
			{ "Contents", "Contents"},
			
			{ "Typeparameter","Type Parameter"},
			{ "Parameter", "Parameter"},
			{ "Exceptions", "Exceptions"},
			{ "SeeAlso", "See also"},
			{ "throws", "Throws"},
			{ "ReturnValue", "Return"}
			
	};

}
