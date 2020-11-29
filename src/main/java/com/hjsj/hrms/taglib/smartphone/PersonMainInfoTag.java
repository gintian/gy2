package com.hjsj.hrms.taglib.smartphone;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import org.apache.struts.taglib.TagUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PersonMainInfoTag extends BodyTagSupport {

	private String dbpre;
	private String a0100;

	public int doStartTag() throws JspException {
		Connection conn = null;
		RowSet rs = null;
		try
        {  
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			Map map=(Map)TagUtils.getInstance().lookup(pageContext,"sphoneForm","basicinfo_template","session");
			if(map==null)
				return 0;
			String basicinfo_template = (String)map.get("basicinfo_template");
			Map mapsets = (Map)map.get("mapsets");
			Map mapsetstr = (Map)map.get("mapsetstr");
			for(Iterator i = mapsets.keySet().iterator();i.hasNext();){
				String setid = (String)i.next();
				List itemids = (List)mapsets.get(setid);
				String itemidstr = ((StringBuffer)mapsetstr.get(setid)).substring(1);
				StringBuffer sql=new StringBuffer();
				sql.append("select "+itemidstr+" from "+dbpre+setid+" where a0100='"+a0100+"'");
				if(!"A01".equals(setid))
					sql.append(" and i9999=(select max(i9999) from "+dbpre+setid+" where a0100='"+a0100+"')");
				rs = dao.search(sql.toString());
				if(rs.next()){
					for(int n=0;n<itemids.size();n++){
						String itemid = (String)itemids.get(n);
						FieldItem fielditem = DataDictionary.getFieldItem(itemid);
						String itemtype = fielditem.getItemtype();
						String value = "";
						if("N".equals(itemtype)){
							value=String.valueOf(rs.getInt(itemid));
						}else if("D".equals(itemtype)){
							Object obj=rs.getDate(itemid);
							value = String.valueOf(obj==null?"":obj);
							value=value.replace('-', '.');
						}else if("A".equals(itemtype)){
							String codesetid = fielditem.getCodesetid();
							value=rs.getString(itemid);
							value=value==null?"":value;
							if(!(codesetid.length()==0||"0".equals(codesetid))){
								value=com.hrms.frame.utility.AdminCode.getCodeName(codesetid, value);
							}
						}
						basicinfo_template = basicinfo_template.replace("["+itemid+"]", value);
					}
				}else{
					for(int n=0;n<itemids.size();n++){
						String itemid = (String)itemids.get(n);
						basicinfo_template = basicinfo_template.replace("["+itemid+"]", "");
					}
				}
			}
			if(basicinfo_template.length()>8)
				basicinfo_template=basicinfo_template.substring(0,15)+"<br/>"+basicinfo_template.substring(15);
	        pageContext.getOut().println(basicinfo_template);
	        return EVAL_BODY_BUFFERED;           
        }
        catch(Exception ge)
        {
            ge.printStackTrace();
            return 0;
        }finally{
        	if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
        	if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
        }
	}

	public String getDbpre() {
		return dbpre;
	}

	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}


}
