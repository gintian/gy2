package com.hjsj.hrms.transaction.performance.implement.kh_mainbody.set_dyna_main_rank;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 *<p>Title:SaveDynaMainRankTrans.java</p> 
 *<p>Description:修改主体动态 只是修改当前节点的值不影响上下层节点的值</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Jun 6, 2008:10:45:56 AM</p> 
 *@author JinChunhai
 *@version 1.0
 */

public class SaveDynaMainRankTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		// TODO Auto-generated method stub
		ArrayList rolelist = (ArrayList)this.getFormHM().get("rolelist");
		ArrayList uplist  = new ArrayList();
		String planid = (String)this.getFormHM().get("planid");
		String codeid = (String)this.getFormHM().get("codeid");
		String codesetid = codeid.substring(0,2);
		String codeitemid = codeid.substring(2);
		String dyna_obj_type = "";
		if("UN".equalsIgnoreCase(codesetid))
			dyna_obj_type ="1";
		else if("UM".equalsIgnoreCase(codesetid))
			dyna_obj_type ="2";
		else if("@K".equalsIgnoreCase(codesetid))
			dyna_obj_type ="3";
		else if("lb".equalsIgnoreCase(codesetid))
			dyna_obj_type ="5";
		else
		{
			dyna_obj_type = "4";
			codeitemid = codeid.substring(1);
		}
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer sql = new StringBuffer();
		
		RecordVo vo = new RecordVo("per_dyna_bodyrank");
		try 
		{
//			for(int i=0;i<rolelist.size();i++){
//				sql.setLength(0);
//				LazyDynaBean bean = (LazyDynaBean)rolelist.get(i);
//				sql.append("select * from per_plan_body where plan_id = '"+planid+"' and body_id ='"+bean.get("body_id")+"' and rank ='"+bean.get("rank")+"'");
//				this.frowset = dao.search(sql.toString());
//				while(this.frowset.next()){
//					rolelist.remove(bean);
//					i--;
//				}
//			}
		
			for(int i=0;i<rolelist.size();i++)
			{
				sql.setLength(0);
				LazyDynaBean bean = (LazyDynaBean)rolelist.get(i);
				sql.append("select * from per_dyna_bodyrank where plan_id = '"+planid+"' and body_id ='"+bean.get("body_id")+"' and dyna_obj_type ='"+bean.get("dyna_obj_type")+"' and dyna_obj ='"+bean.get("dyna_obj")+"'");
				this.frowset = dao.search(sql.toString());
				while(this.frowset.next())
				{
					uplist.add(bean);
					rolelist.remove(bean);
					i--;
				}
			}
			
			for(int i=0;i<rolelist.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)rolelist.get(i);
				vo = new RecordVo("per_dyna_bodyrank");
				vo.setInt("body_id",Integer.parseInt(bean.get("body_id").toString()));
				vo.setInt("plan_id",Integer.parseInt(planid));
				vo.setInt("dyna_obj_type",Integer.parseInt(bean.get("dyna_obj_type").toString()));
				vo.setString("dyna_obj",codeitemid);	
				vo.setDouble("rank",Double.parseDouble(bean.get("rank").toString()));
				dao.addValueObject(vo);
			}
			
			for(int i=0;i<uplist.size();i++)
			{
				sql.setLength(0);
				LazyDynaBean bean = (LazyDynaBean)uplist.get(i);
				vo = new RecordVo("per_dyna_bodyrank");
				vo.setInt("body_id",Integer.parseInt(bean.get("body_id").toString()));
				vo.setInt("plan_id",Integer.parseInt(planid));
				vo.setInt("dyna_obj_type",Integer.parseInt(bean.get("dyna_obj_type").toString()));
				vo.setString("dyna_obj",bean.get("dyna_obj").toString());
				vo.setDouble("rank",Double.parseDouble(bean.get("rank").toString()));
				dao.updateValueObject(vo);										
			}
			this.getFormHM().put("successflag", "0");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
