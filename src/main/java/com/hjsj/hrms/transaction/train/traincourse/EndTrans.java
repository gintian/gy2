package com.hjsj.hrms.transaction.train.traincourse;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:培训班</p>
 * <p>Description:结束培训班</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class EndTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("data_table_table");
		cat.debug("table name="+name);
		ArrayList list=(ArrayList)hm.get("data_table_record");
		try{
			String namestr="";
			StringBuffer exper = new StringBuffer("");
			for(int i=0;i<list.size();i++){
				RecordVo vo=(RecordVo)list.get(i);
				String sp = vo.getString("r3127");
				String r3101 =  vo.getString("r3101");
				if("01".equals(sp)){
					exper.append("\n\n["+vo.getString("r3130")+"]"+ResourceFactory.getProperty("train.b_plan.end.submit.drafting")+"!");
					continue;
				}else if("02".equals(sp)){
					exper.append("\n\n["+vo.getString("r3130")+"]"+ResourceFactory.getProperty("train.b_plan.end.submit.approvalr")+"!");
					continue;
				}else if("03".equals(sp)){
					exper.append("\n\n["+vo.getString("r3130")+"]"+ResourceFactory.getProperty("train.b_plan.end.approved")+"!");
					continue;
				}else if("04".equals(sp)){
					exper.append("\n\n["+vo.getString("r3130")+"]"+ResourceFactory.getProperty("train.b_plan.end.published")+"!");
					continue;
				}else if("06".equals(sp)){
					exper.append("\n\n["+vo.getString("r3130")+"]"+ResourceFactory.getProperty("train.b_plan.end.end")+"!");
					continue;
				}
				namestr+="'"+r3101+"'";
			}
			if(namestr.trim().length()>0){
				StringBuffer sqlstr = new StringBuffer("");
				sqlstr.append("update r31 set r3127='06' where R3101 in(");
				sqlstr.append(namestr);
				sqlstr.append(")");
				ContentDAO dao=new ContentDAO(this.getFrameconn());	
				dao.update(sqlstr.toString());
			}
			if(exper.length()>1)
				throw GeneralExceptionHandler.Handle(new Exception(exper.toString()));
		}catch(Exception ex){
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
