package sgpservlet;

import java.sql.Date;


public class resume {
	
	private String sender;
    public String getsender() {
        return sender;
    }
    public void setsender(String sender) {
        this.sender=sender ;
    }

    private String sender_email;
    public String getsender_email() {
        return  sender_email;
    }
    public void setsender_email(String sender_email) {
        this. sender_email= sender_email ;
    }
    
    
   
    
    private String filename;
    public String getfilename() {
        return filename;
    }
    public void setfilename(String filename) {
        this.filename=filename ;
    }
    
    private Date date_of_receiving;
    public Date getdate_of_receiving() {
        return date_of_receiving;
    }
    public void setdate_of_receiving(Date date_of_receiving) {
        this.date_of_receiving=date_of_receiving ;
    }
    
    
}
