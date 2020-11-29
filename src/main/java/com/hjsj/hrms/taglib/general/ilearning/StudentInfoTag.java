package com.hjsj.hrms.taglib.general.ilearning;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * <p>Title:学员基本信息标签</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create date:2012-11-10</p>
 * @author zxj
 * @version 1.0
 * 
 */
public class StudentInfoTag extends BodyTagSupport {

    private static final long serialVersionUID = 1L;
    
    public StudentInfoTag() {
        super();
    }

    public int doStartTag() throws JspException {
        
        UserView userView = (UserView)this.pageContext.getSession().getAttribute(WebConstant.userView);
        
        String dbname = userView.getDbname();
        if(dbname == null || "".equals(dbname))
            return 0;
        
        String a0100 = userView.getA0100();        
        if(a0100 == null || "".equals(a0100))
            return 0;
        
        String absPath = "";
        Connection conn = null;
        try {   
        	conn = AdminDb.getConnection();
        	PhotoImgBo pib = new PhotoImgBo(conn);
        	try{
    			absPath = pib.getPhotoRootDir();
    		}catch(Exception ex){
    		}
        	
        	//zxj 20160429 成功生成了照片
			boolean genPhotoSuccess = false;
			StringBuffer photourl = new StringBuffer();

		    //如果设置了附件路径
			if (absPath != null && absPath.length() > 0) {
				absPath += pib.getPhotoRelativeDir(dbname, a0100);
				
				String guid = pib.getGuid();
				//获取 文件名为 “photo.xxx”的文件，格式未知
				String fileWName = pib.getPersonImageWholeName(absPath, "photo");

				// 如果不存在文件，创建文件
				if (fileWName.length() < 1) {
					fileWName = pib.createPersonPhoto(absPath, conn, dbname,
							a0100, "photo");
				}

				//如果有图片或创建了图片，使用新图片
				if (fileWName.length() > 0) {
					absPath += fileWName;
					photourl.append("/servlet/DisplayOleContent?perguid="+guid);
					HttpServletRequest req = (HttpServletRequest)this.pageContext.getRequest();
					HttpSession session = req.getSession();
					session.setAttribute(guid, absPath);
					//zxj 只要能走到这里，表示照片成功产生了
					genPhotoSuccess = true;
				}
			}
			
            if(!genPhotoSuccess) {
            	String filename=ServletUtilities.createPhotoFile(dbname+"A00",a0100,"P",pageContext.getSession());
            	String url=((HttpServletRequest)pageContext.getRequest()).getContextPath();
            	
            	if(!"".equals(filename)) {
            		photourl.append(url);
            		photourl.append("/servlet/DisplayOleContent?filename=");
            		photourl.append(SafeCode.encode(PubFunc.encrypt(filename)));
            	} else {
            		photourl.append("/images/photo.jpg");
            	}
            }
            
            HashMap stuMap = queryStudentInfo(dbname, a0100);
            StringBuffer str_html=new StringBuffer();
            str_html.append("<table><tr><td align='center'>");
            str_html.append("<div class=\"photo\">");
            str_html.append("<a href='javascript:' onclick='window.top.tabs(0)'>");
            str_html.append("<img src=\"");
            str_html.append(photourl.toString());
            str_html.append("\" ");
            str_html.append("height=\"120\" width=\"85\"");
            str_html.append(" border=0 ></a>");
            str_html.append("</div>");
            str_html.append("</td>");
            str_html.append("<td align='center'>");
            str_html.append("<a href='javascript:' onclick='window.top.tabs(0)'>");
            String a0101 = (String)stuMap.get("a0101");
            a0101 = a0101 == null ? "" : a0101;
            
            String b0110 = (String)stuMap.get("b0110");
            if(b0110!=null && b0110.length()>0)
                b0110 = AdminCode.getCodeName("UN", b0110);
            b0110 = b0110 == null ? "" : b0110;
            
            String e0122 = (String)stuMap.get("e0122");
            if(e0122!=null && e0122.length()>0)
                e0122 = AdminCode.getCodeName("UN", e0122);
            e0122 = e0122 == null ? "" : e0122;
            
            str_html.append("<br>");
            //str_html.append(ResourceFactory.getProperty("label.title.name"));
            //str_html.append("：&nbsp;");
            str_html.append(a0101);
            str_html.append("<br><br>");
            //str_html.append(ResourceFactory.getProperty("label.title.org"));
            //str_html.append("：&nbsp;");
            str_html.append(b0110);
            str_html.append("<br>");
            //str_html.append(ResourceFactory.getProperty("label.title.dept"));
            //str_html.append("：&nbsp;");
            str_html.append(e0122);
            str_html.append("<br>");  
            str_html.append("<br>"); 
            str_html.append("</a></td>");
            
            pageContext.getOut().println(str_html.toString());
            return EVAL_BODY_BUFFERED;           
        } catch(Exception ge) {
            ge.printStackTrace();
            return 0;
        } finally {
        	PubFunc.closeResource(conn);
        }        
    }
    
    private HashMap queryStudentInfo(String dbpre, String a0100)
    {
        HashMap stuInfo = new HashMap();
        
        RowSet rs = null;
        Connection conn = null;
        try
        {
            conn = AdminDb.getConnection();
            
            if(conn != null)
            {
                StringBuffer sql = new StringBuffer();
                sql.append("SELECT A0101,B0110,E0122 FROM ");
                sql.append(dbpre + "A01");
                sql.append(" WHERE a0100='");
                sql.append(a0100);
                sql.append("'");
                
                ContentDAO dao = new ContentDAO(conn);
                try
                {
                    rs = dao.search(sql.toString());
                    if(rs.next())
                    {
                        stuInfo.put("a0101", rs.getString("A0101"));
                        stuInfo.put("b0110", rs.getString("B0110"));
                        stuInfo.put("e0122", rs.getString("E0122"));
                    }
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }   
                finally
                {
                    if(rs != null)
                        try
                        {
                            rs.close();
                        }
                        catch (SQLException e)
                        {
                            e.printStackTrace();
                        }
                }
           
            }
        }
        catch (GeneralException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(conn!=null)
            {
                try
                {
                    conn.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        return stuInfo;
    }
    
}

