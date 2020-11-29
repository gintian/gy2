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
public class SaveStandardIDTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String info = "no";
		
		String salaryid = (String)hm.get("salaryid");
		salaryid=salaryid!=null&&salaryid.length()>0?salaryid:"";
		
		String itemid = (String)hm.get("item");
		itemid=itemid!=null&&itemid.length()>0?itemid:"";
		
		String standardid = (String)hm.get("standardid");
		standardid=standardid!=null&&standardid.length()>0?standardid:"";
		
		String runflag = (String)hm.get("runflag");
		runflag=runflag!=null&&runflag.length()>0?runflag:"";
		
		String operating = (String)hm.get("operating");
		operating=operating!=null&&operating.length()>0?operating:"alert";
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		
		if("alert".equals(operating)){
			try {
				//----------------------执行公式的时候  日志信息  zhaoxg add 2015-4-29------
				RecordVo vo=new RecordVo("salaryformula");
				vo.setInt("salaryid", Integer.parseInt(salaryid));
				vo.setInt("itemid", Integer.parseInt(itemid));
				try {
					vo = dao.findByPrimaryKey(vo);	
				} catch(SQLException e) {
					e.printStackTrace();
				}
				String value = vo.getString("runflag");//=0,计算公式 =1,执行标准 =2,执行税表
				if("0".equals(value)){
					value = "公式";
				}else if("1".equals(value)){
					value = "标准";
				}else if("2".equals(value)){
					value = "税率表";
				}
				String tovalue = "税率表";
				if("0".equals(runflag)){
					tovalue = "公式";
				}else if("1".equals(runflag)){
					tovalue = "标准";
				}else if("2".equals(runflag)){
					tovalue = "税率表";
				}
				StringBuffer context = new StringBuffer();
				SalaryTemplateBo bo = new SalaryTemplateBo(this.frameconn);
				String name = bo.getSalaryName(salaryid);
				context.append("执行："+name+"（"+salaryid+"）修改计算公式("+vo.getString("hzname")+")："+value+"--->"+tovalue+"<br>");
				this.getFormHM().put("@eventlog", context.toString());
				//------------------------------------------------------------------------
				String sqlstr = "update salaryformula set standid="+standardid+",runflag="+runflag+" where salaryid="+salaryid+" and itemid="+itemid;
				dao.update(sqlstr);
				info="ok";
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				info="no";
			}
		}else{
			try {
				dao.update("delete from gz_stand where id="+standardid);
				
				dao.update("delete from gz_stand_history where pkg_id=0 and id="+standardid);
				
				info="ok";
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				info="no";
			}
		}
		hm.put("info",info);
		hm.put("operating",operating);
	}

}
