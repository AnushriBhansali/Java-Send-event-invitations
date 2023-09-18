package sgpservlet;

//package sgp_project;

import java.io.File;
//import java.io.FileInputStream;
import java.io.IOException;
/*import java.sql.Connection;
//package com.memorynotfound.pdf.pdfbox;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;*/

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

class PDFManager {

//CONVERTING PDF TO PLAIN TEXT
private PDFParser parser;
private PDFTextStripper pdfStripper;
private PDDocument pdDoc;
private COSDocument cosDoc;

private String Text;
private String filePath;
private File file;

public PDFManager() {

}

public String toText() throws IOException {
this.pdfStripper = null;
this.pdDoc = null;
this.cosDoc = null;

file = new File(filePath);
parser = new PDFParser(new RandomAccessFile(file, "r")); // update for PDFBox V 2.0

parser.parse();
cosDoc = parser.getDocument();
pdfStripper = new PDFTextStripper();
pdDoc = new PDDocument(cosDoc);
pdDoc.getNumberOfPages();
pdfStripper.setStartPage(0);
pdfStripper.setEndPage(pdDoc.getNumberOfPages());
Text = pdfStripper.getText(pdDoc);
return Text;
}

public void setFilePath(String filePath) {
this.filePath = filePath;
}

public PDDocument getPdDoc() {
return pdDoc;
}

}