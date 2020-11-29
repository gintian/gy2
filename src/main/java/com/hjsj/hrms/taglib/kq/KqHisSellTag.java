package com.hjsj.hrms.taglib.kq;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.util.ArrayList;

public class KqHisSellTag extends BodyTagSupport{
	private String id;
	
	public int doEndTag() throws JspException 
	{
		try{
			StringBuffer sql=new StringBuffer();
			sql.append("select q1519,q15z0,q15z5 from q15_arc");
			sql.append(" where q1519='"+id+"' and q1517='1'");
			ArrayList mylist = (ArrayList) ExecuteSQL.executeMyQuery(sql.toString());
			if(mylist==null||mylist.size()<=0)
			{
				pageContext.getOut().println("未销假");
			}
			String q1519="";
			String q15z0="";
			String q15z5="";
			for(int i=0;i<mylist.size();i++)
			{
				LazyDynaBean dynabean=(LazyDynaBean) mylist.get(i);
				q1519=(String)dynabean.get("q1519");
				q15z0=(String)dynabean.get("q15z0");
				q15z5=(String)dynabean.get("q15z5");
				if(q1519==null||!"01".equals(q15z0)||!"03".equals(q15z5)||q1519.length()<=0){
					if("07".equals(q15z5))
					{
						pageContext.getOut().println("驳回");
					}else
					{
						pageContext.getOut().println("待批");
					}
				}else
				{
					pageContext.getOut().println("已销假");
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return SKIP_BODY;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
