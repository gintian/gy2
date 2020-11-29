package com.hjsj.hrms.taglib.sys;


import com.hjsj.hrms.businessobject.general.approve.personinfo.FindBDInfo;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.utility.AdminCode;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
public class Codetoinfo extends BodyTagSupport {
	private String itemid;
	private String abkflag;
	private String pdbflag;
	public int doEndTag() throws JspException{
		JspWriter out=pageContext.getOut();
		StringBuffer sbsql=new StringBuffer();
		String abkflag=this.getAbkflag();
		if("a".equalsIgnoreCase(abkflag)){
		sbsql.append("select a0101,b0110,e0122,e01a1 from "+this.getPdbflag()+"a01 where");
		String[] itemid=this.getItemid().split(",");
		for(int i=0;i<itemid.length;i++){
			if(i==0){
				sbsql.append(" a0100='"+itemid[0]+"' ");
			}
			else{
				sbsql.append(" or a0100='"+itemid[i]+"'");
			}
		}
//		ExecuteSQL exsql=null;
		ArrayList mylist = (ArrayList) ExecuteSQL.executeMyQuery(sbsql.toString());
		for(int i=0;i<mylist.size();i++){
			LazyDynaBean dynabean=(LazyDynaBean) mylist.get(i);
			try {
				out.println("<td  class=\"RecordRow\" nowrap>");
				out.println( AdminCode.getCodeName("UN",(String)dynabean.get("b0110")) );
				out.println("</td>");
				out.println("<td  class=\"RecordRow\" nowrap>");
				out.println( AdminCode.getCodeName("UM", (String)dynabean.get("e0122")));
				out.println("</td>");
				out.println("<td  class=\"RecordRow\" nowrap>");
				out.println( AdminCode.getCodeName("@k", (String)dynabean.get("e01a1")));
				out.println("</td>");
				out.println("<td  class=\"RecordRow\" nowrap>");
				out.println("<a href=\"#\" onclick=\"openpage('"+this.getItemid()+"');\">");
				out.println((String)dynabean.get("a0101"));
				out.println("</a>");
				out.println("</td>");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		}
		if("k".equalsIgnoreCase(abkflag)){
			sbsql.append("select e0122,e01a1 from k01 where");
			String[] itemid=this.getItemid().split(",");
			for(int i=0;i<itemid.length;i++){
				if(i==0){
					sbsql.append(" e01a1='"+itemid[0]+"' ");
				}
				else{
					sbsql.append(" or e01a1='"+itemid[i]+"'");
				}
			}
			ArrayList mylist = (ArrayList) ExecuteSQL.executeMyQuery(sbsql.toString());
			for(int i=0;i<mylist.size();i++){
				LazyDynaBean dynabean=(LazyDynaBean) mylist.get(i);
				try {

					HashMap hm=FindBDInfo.getUDinfo((String)dynabean.get("e01a1"));
					out.println("<td  class=\"RecordRow\" nowrap>");
					out.println( AdminCode.getCodeName("UN", (String)hm.get("UN")));
					out.println("</td>");
					out.println("<td  class=\"RecordRow\" nowrap>");
					out.println( AdminCode.getCodeName("UM", (String)dynabean.get("e0122")));
					out.println("</td>");
					if(!"K01".equalsIgnoreCase(this.getPdbflag())){
					out.println("<td  class=\"RecordRow\" nowrap>");
					out.println( AdminCode.getCodeName("@k", (String)dynabean.get("e01a1")));
					out.println("</td>");
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

			}

		return super.doEndTag();
		
	}
	public int doStartTag() throws JspException{
		return super.doStartTag();
	}
	public String getAbkflag() {
		return abkflag;
	}
	public void setAbkflag(String abkflag) {
		this.abkflag = abkflag;
	}
	public String getItemid() {
		return itemid;
	}
	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
	public String getPdbflag() {
		return pdbflag;
	}
	public void setPdbflag(String pdbflag) {
		this.pdbflag = pdbflag;
	}

	
	
	

}
