package com.hjsj.hrms.taglib.kq;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.util.ArrayList;

public class KqSellTag extends BodyTagSupport{
	private String id;
	private String tableName;
	
	public int doEndTag() throws JspException 
	{
		try{
			StringBuffer sql=new StringBuffer();
			sql.append("select " + this.tableName + "19," + this.tableName + "z0," + this.tableName + "z5 from " + this.tableName);
			sql.append(" where " + this.tableName + "19='"+id+"' and " + this.tableName + "17='1'");
			ArrayList mylist = (ArrayList) ExecuteSQL.executeMyQuery(sql.toString());
			if(mylist==null||mylist.size()<=0)
			{
				if("q11".equalsIgnoreCase(this.tableName.toLowerCase()))
					pageContext.getOut().println("未撤销加班");
				else if("q13".equalsIgnoreCase(this.tableName.toLowerCase()))
					pageContext.getOut().println("未撤销公出");
				else if("q15".equalsIgnoreCase(this.tableName.toLowerCase()))
					pageContext.getOut().println("未销假");
			}
			String q1519="";
			String q15z0="";
			String q15z5="";
			for(int i=0;i<mylist.size();i++)
			{
				LazyDynaBean dynabean=(LazyDynaBean) mylist.get(i);
				q1519=(String)dynabean.get(this.tableName + "19");
				q15z0=(String)dynabean.get(this.tableName + "z0");
				q15z5=(String)dynabean.get(this.tableName + "z5");
				if(q1519==null||!"01".equals(q15z0)||!"03".equals(q15z5)||q1519.length()<=0){
					if("07".equals(q15z5)){
						pageContext.getOut().println("驳回");
					} else if("01".equals(q15z5)){
						pageContext.getOut().println("起草");
					}else{
						pageContext.getOut().println("待批");
					}
				}else
				{
					if("q11".equalsIgnoreCase(this.tableName.toLowerCase()))
						pageContext.getOut().println("已撤销加班");
					else if("q13".equalsIgnoreCase(this.tableName.toLowerCase()))
						pageContext.getOut().println("已撤销公出");
					else if("q15".equalsIgnoreCase(this.tableName.toLowerCase()))
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

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
}
