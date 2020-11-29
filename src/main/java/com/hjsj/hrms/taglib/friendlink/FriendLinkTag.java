/*
 * Created on 2006-1-9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.taglib.friendlink;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FriendLinkTag extends TagSupport {
	private int cols; //行
	private int rows; //列
	private int count;//显示的个数
	private int width=50;
	private int height=50;	
	public int doEndTag() throws JspException{
		try{
			//加载打印数据
			showfriendlink();
		}catch(Exception e)
		{    				
		}
   		return SKIP_BODY;
	}
	public void release(){
		super.release();
	}
    private void showfriendlink(){
        File tempFile = null;
        String filename="";
        ServletUtilities.createTempDir();
        ResultSet rs = null;
        Connection conn = null;
        InputStream in = null;
        java.io.FileOutputStream fout = null;
        try {
            StringBuffer strsql = new StringBuffer();
            strsql.append("select site_id,url,ext,name,log_icon from hr_friend_website");
            strsql.append(" where flag=1 order by site_id");
            
            conn = AdminDb.getConnection();
            Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
            String link_p_width=sysbo.getValue(Sys_Oth_Parameter.LIKN_P_WIDTH);
   		    String link_p_height=sysbo.getValue(Sys_Oth_Parameter.LIKN_P_HEIGHT);
   		    if(link_p_width!=null&&link_p_width.length()>0)
   		    	this.width=Integer.parseInt(link_p_width);
   		    if(link_p_height!=null&&link_p_height.length()>0)
   		    	this.height=Integer.parseInt(link_p_height);
   		    ContentDAO dao  = new ContentDAO(conn);
   		    rs=dao.search(strsql.toString());
            HttpSession session=pageContext.getSession();
            StringBuffer str_html=new StringBuffer();
            str_html.append("<table border=\"0\" cellspacing=\"1\"  align=\"center\" cellpadding=\"1\">");
            str_html.append("<tr>");  
            for(int i=1,j=0;(i<rows || (i>=rows && j%cols!=0)) && rs.next();j++) {
            	  if(j!=0 && j%cols==0){
        	    	i++;
        	    	str_html.append("</tr>");
        	    	str_html.append("<tr>");
           	    }
                String urlhttp=rs.getString(2);
                //String urlhttp="ddd";
                String ext=rs.getString(3);
                String name=rs.getString(4);
                in = rs.getBinaryStream(5);
         
                
                        
              
                
                tempFile = File.createTempFile(ServletUtilities.tempFilePrefix, "." + ext,
                        new File(System.getProperty("java.io.tmpdir")));
                       
                fout = new java.io.FileOutputStream(tempFile);                
                int len;
                byte buf[] = new byte[1024];            
                while (in!=null && (len = in.read(buf, 0, 1024)) != -1) {
                    fout.write(buf, 0, len);
               
                }
                if (session != null) {
                	ServletUtilities.registerPhotoForDeletion(tempFile, session);
                }
                filename= tempFile.getName();   
                //图片名称加密   jingq add  2014.09.26
                filename = SafeCode.encode(PubFunc.encrypt(filename));
                String url=((HttpServletRequest)pageContext.getRequest()).getContextPath();
        	    StringBuffer photourl=new StringBuffer();
        	  
        	        if(in!=null && !"".equals(filename))
        	        {
        	        	photourl.append(url);
        	        	photourl.append("/servlet/DisplayOleContent?filename=");
        	        	photourl.append(filename);
        	        }
        	        else
        	        {
        	        	photourl.append("/images/photo.jpg");
        	        }       	        
        	        str_html.append("<td align=\"center\">");
        	        str_html.append("<a class=\"link\" href=\"");
        	        str_html.append(urlhttp);
        	        str_html.append("\" target=\"_blank\">");
        	        str_html.append("<img src=\"");
        	        str_html.append(photourl.toString());
        	        str_html.append("\" ");
        	        //System.out.println("height=" + height);
        	        //System.out.println("width=" + width);
        	        
        	        if(this.height!=0)
        	        {
        	          str_html.append(" height=\"");
        	          str_html.append(this.height);
        	          str_html.append("\"");
        	        }
        	        if(this.width!=0)
        	        {
        	        	str_html.append(" width=\"");
        	            str_html.append(this.width);
            	        str_html.append("\"");
        	        }
        	        str_html.append(" title=\"");
        	        str_html.append(name);
        	        str_html.append("\"");        	        
        	        str_html.append(" border=0>");
        	        str_html.append("</a>");
        	        str_html.append("</td>");        	                
            }
            str_html.append("</tr>");
            str_html.append("</table>");
               //System.out.println("str_html.toString()" + str_html.toString());
               pageContext.getOut().println(str_html.toString());         
        } catch (SQLException sqle) {
            sqle.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	PubFunc.closeIoResource(in); //关闭资源
        	PubFunc.closeIoResource(fout);
        	PubFunc.closeIoResource(rs);
        	PubFunc.closeIoResource(conn);
        }
    
           
    
    }
 
	/**
	 * @return Returns the cols.
	 */
	public int getCols() {
		return cols;
	}
	/**
	 * @param cols The cols to set.
	 */
	public void setCols(int cols) {
		this.cols = cols;
	}
	/**
	 * @return Returns the count.
	 */
	public int getCount() {
		return count;
	}
	/**
	 * @param count The count to set.
	 */
	public void setCount(int count) {
		this.count = count;
	}
	/**
	 * @return Returns the rows.
	 */
	public int getRows() {
		return rows;
	}
	/**
	 * @param rows The rows to set.
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}
	/**
	 * @return Returns the height.
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * @param height The height to set.
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	/**
	 * @return Returns the width.
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * @param width The width to set.
	 */
	public void setWidth(int width) {
		this.width = width;
	}
}
