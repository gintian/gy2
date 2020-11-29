package com.hjsj.hrms.transaction.gz.formula;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:计算公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class SaveCalculaCondTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		String item = (String)hm.get("item");
		item=item!=null&&item.trim().length()>0?item:"";

		String salaryid = (String)hm.get("salaryid");
		salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		
		String conditions = (String)hm.get("conditions");
		conditions=conditions!=null&&conditions.trim().length()>0?conditions:"";
		conditions = SafeCode.decode(conditions);		
		conditions = PubFunc.keyWord_reback(conditions);
		try {
			String cond = "";
			String hzname = "";
			String sql = "select hzname,cond from salaryformula where salaryid="+salaryid+" and itemid="+item;
			RowSet rs = dao.search(sql);
			if(rs.next()){
				cond = rs.getString("cond")==null?"":rs.getString("cond");
				hzname = rs.getString("hzname");
			}
			//-------------------------计算条件保存，记入日志 zhaoxg add 2015-6-16--------------------------------
			SalaryTemplateBo bo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.getUserView());
			if(!cond.equals(conditions)){
				StringBuffer context = new StringBuffer();
				context.append("计算条件保存："+bo.getSalaryName(salaryid)+"("+salaryid+")修改（"+hzname+"）计算公式计算条件<br>");
				context.append("<table>");
				context.append("<tr>");
				context.append("<td>属性名</td>");
				context.append("<td>变化前</td>");
				context.append("<td>变化后</td>");
				context.append("</tr>");
				
				context.append("<tr>");
				context.append("<td>"+hzname+"计算条件</td>");
				context.append("<td>"+cond.replaceAll("\"", "＂").replaceAll("\r\n", "<br>")+"</td>");
				context.append("<td>"+conditions.replaceAll("\"", "＂").replaceAll("\r\n", "<br>")+"</td>");
				context.append("</tr>");
				this.getFormHM().put("@eventlog", context.toString());
			}else{
				this.getFormHM().put("@eventlog", "计算条件保存："+bo.getSalaryName(salaryid)+"("+salaryid+")修改（"+hzname+"）计算公式计算条件,但没修改具体内容");
			}
			//-----------------------------------------end--------------------------------------------------
			String sqlstr = "update salaryformula set cond='"+conditions+"' where salaryid="+salaryid+" and itemid="+item;
			
			dao.update(sqlstr);
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		hm.put("info","ok");
	}

}
