/**
 * 
 */
package com.hjsj.hrms.taglib.sys;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import org.apache.commons.beanutils.DynaBean;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.util.ArrayList;

/**
 * <p>Title:OrgTreeTag</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Jun 23, 20061:34:10 PM
 * @author chenmengqing
 * @version 4.0
 */
public class RelatingcodeTag extends BodyTagSupport {
	/**选中节点相应，弹出的网页*/
	private String codesetid;
	/**codesetid*/

	
	public int doEndTag() throws JspException {
		StringBuffer strhtml=new StringBuffer();
		try
		{
			String codesetid=this.getCodesetid();
//			String sqlstr="select fieldsetdesc from t_hr_relatingcode thr left join " +
//					"(select * from t_hr_busitable) " +
//					"tht on thr.codetable=tht.fieldsetid where codesetid='"
//				+codesetid+"'";
			String sqlstr="select thb.itemdesc from t_hr_relatingcode thr left join "+
				"(select * from t_hr_busifield) thb on thr.codedesc=thb.itemid and thr.codetable=thb.fieldsetid "+
				" left join (select * from t_hr_busifield) fds on  thr.codevalue=fds.itemid where thr.codesetid= '"+
				codesetid+"'";
			ArrayList mylist = (ArrayList) ExecuteSQL.executeMyQuery(sqlstr);
			if(mylist.size()>0){
				DynaBean dynabean=(DynaBean) mylist.get(0);
				String reltable=(String)dynabean.get("itemdesc");
				strhtml.append(reltable);
			}
			
			pageContext.getOut().println(strhtml.toString());
			return SKIP_BODY;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return SKIP_BODY;			
		}
	}

	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	public String getCodesetid() {
		return codesetid;
	}

	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}



}
