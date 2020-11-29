package com.hjsj.hrms.transaction.gz.formula;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

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
public class DelProjectTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		
		String base = "no";
		
		String itemid= (String)hm.get("itemid");
		itemid=itemid!=null&&itemid.trim().length()>0?itemid:"";
		
		String salaryid= (String)hm.get("salaryid");
		salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		StringBuffer strsql = new StringBuffer();
		strsql.append("delete from  salaryformula where salaryid=");
		strsql.append(salaryid);
		strsql.append(" and itemid=");
		strsql.append(itemid);
		RecordVo vo=new RecordVo("salaryformula");
		vo.setInt("salaryid", Integer.parseInt(salaryid));
		vo.setInt("itemid", Integer.parseInt(itemid));
		try {
			//------------------删除计算公式 日志记录  zhaoxg add 2015-4-29--------
			vo = dao.findByPrimaryKey(vo);			
			StringBuffer context = new StringBuffer();
			SalaryTemplateBo bo = new SalaryTemplateBo(this.frameconn);
			String name = bo.getSalaryName(salaryid);
			context.append("删除："+name+"（"+salaryid+"）删除计算公式（"+vo.getString("hzname")+"）<br>");

			this.getFormHM().put("@eventlog", context.toString());
			//-----------------------------------------------------------------
			dao.update(strsql.toString());
			base = "ok";
		} catch(SQLException e) {
			// TODO Auto-generated catch block
			base = "no";
			e.printStackTrace();
		}
		hm.put("base",base);
	}

}
