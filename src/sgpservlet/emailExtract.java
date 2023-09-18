package sgpservlet;


//package sgp_project;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
//package com.memorynotfound.pdf.pdfbox;
import java.sql.*;
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
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Extract")
public class emailExtract extends HttpServlet {
private static final long serialVersionUID = 1L;

public void doGet(HttpServletRequest request, HttpServletResponse response)
throws ServletException, IndexOutOfBoundsException 
{
String host = "imap.googlemail.com";
String port = "993";
String userName = "kap51164project@gmail.com";
String passwd = "kap.51164";

System.out.println("Driver loaded!");

//***** Get The Absolute Path Of The Web Application *****/
String applicationPath = getServletContext().getRealPath("");
String myuploadPath = applicationPath + File.separator + "Attachment";

File fileUploadDirectory = new File(myuploadPath);
if (!fileUploadDirectory.exists()) {
  fileUploadDirectory.mkdirs();
}


emailExtract receiver = new emailExtract();
//receiver.setuploadPath(uploadPath);

//FOR THE JDBC CONNECTION
String url = "jdbc:mysql://localhost:3306/resumemaster";
String username = "root";
String password = "sonia@123";
Connection con = null;

/// add try
////CONNECTING WITH MYSQL (JDBC)
try {
Class.forName("com.mysql.jdbc.Driver");
System.out.println("Driver loaded!");
con = DriverManager.getConnection(url, username, password);
System.out.println("connected to the database");
} catch (ClassNotFoundException e) {
//TODO Auto-generated catch block
e.printStackTrace();
} catch (SQLException e) {
//TODO Auto-generated catch block
e.printStackTrace();
}

int entries=receiver.downloadEmailAttachments(host, port, userName, passwd, con,myuploadPath);
System.out.println("count:"+entries);


String referer = request.getHeader("Referer");
String ref=referer.substring(11);
RequestDispatcher requestDispatcher = request.getRequestDispatcher("home.jsp");
request.setAttribute("rcount",entries);
try {
	requestDispatcher.forward(request, response);
} catch (ServletException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
}

static // extracting mails from inbox
String sfrom;
private String uploadPath;

/**
* Sets the directory where attached files will be stored.
*
* @param dir absolute path of the directory
*/
public void setuploadPath(String dir) {
this.uploadPath = dir;
}

public int downloadEmailAttachments(String host, String port, String userName, String password, Connection conn,String myuploadPath) 
{
    int count=0;

Properties properties = new Properties();

//server setting
properties.put("mail.imaps.host", host);
properties.put("mail.imaps.port", port);

//SSL setting
properties.setProperty("mail.imaps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
properties.setProperty("mail.imaps.socketFactory.fallback", "false");
properties.setProperty("mail.imaps.socketFactory.port", String.valueOf(port));

Session session = Session.getDefaultInstance(properties);



try {
//connects to the message store
Store store = session.getStore("imaps");
store.connect(userName, password);



//opens the inbox folder
Folder folderInbox = store.getFolder("INBOX");
folderInbox.open(Folder.READ_ONLY);

SearchTerm term = null;

Flags seen = new Flags(Flags.Flag.SEEN);
FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
Message arrayMessages[] = folderInbox.search(unseenFlagTerm);
Arrays.sort(arrayMessages, (m1, m2) -> {
try {
return m2.getSentDate().compareTo(m1.getSentDate());
} catch (MessagingException e) {
throw new RuntimeException(e);
}
});

System.out.println(arrayMessages.length);

for (int i = 0; i < arrayMessages.length; i++) 
{
	System.out.println("inside");
Message message = arrayMessages[i];
Address[] fromAddress = message.getFrom();
String from = fromAddress[0].toString();
String subject = message.getSubject();
String sentDate = message.getSentDate().toString();
String contentType = message.getContentType();
String messageContent = "";
FileInputStream input = null;
PreparedStatement Stmt = null;
ResultSet rs = null;
//store attachment file name, separated by comma
String attachFiles = "";


if (contentType.contains("multipart") && ((subject.indexOf("Resume") >= 0) || (subject.indexOf("Biodata") >= 0))|| (subject.indexOf("CV") >= 0))
{
System.out.println("inside");
//content may contain attachments
String text = null;
Multipart multiPart = (Multipart) message.getContent();
int numberOfParts = multiPart.getCount();
		for (int partCount = 0; partCount < numberOfParts; partCount++)
		{
		MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
		if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) 
		{
		System.out.println("inside1");
		//this part is attachment
		String fileName = part.getFileName();
		if (attachFiles == "")
			attachFiles=fileName;
		else	
			attachFiles += fileName + ", ";
		
		part.saveFile(myuploadPath + File.separator + fileName);
		part.saveFile(myuploadPath + File.separator+ "new" + File.separator+ fileName);
		PDFManager pdfManager1 = new PDFManager();
		pdfManager1.setFilePath(myuploadPath + File.separator + fileName);
		text = pdfManager1.toText();
		//System.out.println(text);		
		pdfManager1=null;
		part=null;
		
		count++;		
		}
		}

		
		
if (attachFiles.length() > 1) {
//attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
//print out details of each message
System.out.println("Message #" + (i + 1) + ":");
System.out.println("\t From: " + from);
sfrom = from;
System.out.println("\t Subject: " + subject);
System.out.println("\t Sent Date: " + sentDate.toString());
//System.out.println("\t Message: " + messageContent);
System.out.println("\t Attachments: " + attachFiles);

//connect and save to database

String query = "INSERT INTO resume_info(DATE_OF_EXTRACTION,DATE_OF_RECEIVING,SENDER,ATTACHMENTS,SENDER_EMAIL,FILENAME)"
+ "VALUES (?,?,?,?,?,?)";

////INSERTING THE VALUES
//String sender= sfrom;
String edate = sentDate;
Date utilDate = new Date();
try {
utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(edate);
} catch (ParseException e) {
e.printStackTrace();
}
java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

try {
Stmt = conn.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);

Calendar calendar = Calendar.getInstance();
java.sql.Date ourJavaDateObject = new java.sql.Date(calendar.getTime().getTime());
//PreparedStatement st = conn.prepareStatement(query);

Stmt.setDate(1, sqlDate);
//Stmt.setDate(2, sqlDate);
Stmt.setDate(2, ourJavaDateObject);
String separator ="<";
int sepPos = sfrom.lastIndexOf(separator);
int start=sepPos+1;
String sname=sfrom.substring(0,sepPos);
Stmt.setString(3, sname);

String sep=">";
int id=sfrom.lastIndexOf(sep);
String sid=sfrom.substring(start,id);
Stmt.setString(4, text);
Stmt.setString(5, sid);
Stmt.setString(6, attachFiles);
Stmt.executeUpdate();
rs = Stmt.getGeneratedKeys();

String newfilename="";
rs.next();
System.out.println("Auto Generated Primary Key " + rs.getInt(1));
newfilename=  "Att" + String.valueOf(rs.getInt(1))+ attachFiles.substring(attachFiles.length() - 4) ;


	System.out.println(myuploadPath + File.separator + newfilename);
	System.out.println(myuploadPath + File.separator + attachFiles);
	
File orifile = new File(myuploadPath + File.separator  +"new" + File.separator + attachFiles);
File newfile = new File(myuploadPath + File.separator  +"new" + File.separator + newfilename);
boolean suc=orifile.renameTo(newfile);
if (suc) { 
	  
    // display that the file is renamed 
    // to the abstract path name 
    System.out.println("File is renamed"); 
} 
else { 
    // display that the file cannot be renamed 
    // to the abstract path name 
    System.out.println("File cannot be renamed"); 
} 
} catch (SQLException e) {
e.printStackTrace();
}

}

}

//else
//break;

}

//disconnect
folderInbox.close(false);
store.close();



} catch (NoSuchProviderException ex) {
System.out.println("No provider for pop3.");
ex.printStackTrace();
} catch (MessagingException ex) {
System.out.println("Could not connect to the message store");
ex.printStackTrace();
} catch (IOException ex) {
ex.printStackTrace();
}

return count;}

}
