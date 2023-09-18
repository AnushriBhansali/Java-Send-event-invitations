package sgpservlet;

import java.io.*;  
import java.sql.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;  
import java.util.ArrayList;
import java.util.List;

@WebServlet("/Filter")
public class filterresume extends HttpServlet {  
	private static final long serialVersionUID = 1L;
  
public void doGet(HttpServletRequest request, HttpServletResponse response)  
            throws ServletException, IOException {  
	
	response.setContentType("text/html");  
	PrintWriter out = response.getWriter();  
	
	String k1=String.valueOf(request.getParameter("k1"));  
	String k2=String.valueOf(request.getParameter("k2"));   
	String k3=String.valueOf(request.getParameter("k3"));   
	String k4=String.valueOf(request.getParameter("k4"));  
	System.out.println(k1);
	
	//FOR THE JDBC CONNECTION
	String url = "jdbc:mysql://localhost:3306/resumemaster";
	String username = "root";
	String password = "sonia@123";
	Connection con = null;
	System.out.println("here1");
	
	////CONNECTING WITH MYSQL (JDBC)
	try {
	Class.forName("com.mysql.jdbc.Driver");
	System.out.println("Driver loaded!");
	con = DriverManager.getConnection(url, username, password);
	
	System.out.println("connected to the database");
	
    String querystr= "SELECT SENDER,ATTACHMENTS,SENDER_EMAIL,FILENAME,DATE_OF_RECEIVING FROM resume_info WHERE MATCH(ATTACHMENTS)against('" + k1 + "+"+ k2 +"')";
    System.out.println(querystr);
    
PreparedStatement ps=con.prepareStatement(querystr);  
ResultSet rs=ps.executeQuery();  

/* Printing column names */  
ResultSetMetaData rsmd=rs.getMetaData();  
int total=rsmd.getColumnCount();  
List<resume>rlst = new ArrayList<resume>();

while(rs.next())  
{  
	 resume res=new resume();	
	 res.setsender(rs.getString("sender"));
	 res.setsender_email(rs.getString("sender_email"));
	 res.setfilename(rs.getString("filename"));
	 res.setdate_of_receiving(rs.getDate("date_of_receiving"));
	
     rlst.add(res);           
}
request.setAttribute("list",rlst);
RequestDispatcher requestDispatcher = request.getRequestDispatcher("home.jsp");
requestDispatcher.forward(request,response); 
//send to homepage

	} catch (ClassNotFoundException e) {
	//TODO Auto-generated catch block
	e.printStackTrace();
	} catch (SQLException e) {
	//TODO Auto-generated catch block
	e.printStackTrace();
	}
  }
}
	          
	