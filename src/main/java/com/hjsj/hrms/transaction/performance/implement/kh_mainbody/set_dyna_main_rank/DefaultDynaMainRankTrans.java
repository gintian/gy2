package com.hjsj.hrms.transaction.performance.implement.kh_mainbody.set_dyna_main_rank;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:DefaultDynaMainRankTrans.java</p> 
 *<p>Description:默认权重就是把以前设置的权重记录删除掉</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Jun 10, 2009:10:45:56 AM</p> 
 *@author JinChunhai
 *@version 1.0
 */

public class DefaultDynaMainRankTrans extends IBusiness 
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
		else if("lb".equalsIgnoreCase(codesetid)){
			dyna_obj_type = "5";
		}else
		{
			dyna_obj_type = "4";
			codeitemid = codeid.substring(1);
		}
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer sql = new StringBuffer();
		
		RecordVo vo = new RecordVo("per_dyna_bodyrank");
		try 
		{
			HashMap defaultRankMap = new HashMap();
			sql.append("select * from per_plan_body where plan_id ="+planid);
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next())
				defaultRankMap.put(this.frowset.getString("body_id"), new Double(this.frowset.getDouble("rank")));
			
			for(int i=0;i<rolelist.size();i++)
			{
				sql.setLength(0);
				LazyDynaBean bean = (LazyDynaBean)rolelist.get(i);
				sql.append("select * from per_dyna_bodyrank where plan_id = "+planid+" and body_id ="+bean.get("body_id")+" and dyna_obj_type ='"+bean.get("dyna_obj_type")+"' and dyna_obj ='"+bean.get("dyna_obj")+"'");
				this.frowset = dao.search(sql.toString());
				while(this.frowset.next())
				{
					uplist.add(bean);
					rolelist.remove(bean);
					i--;
				}
			}
			
//			for(int i=0;i<rolelist.size();i++){
//				LazyDynaBean bean = (LazyDynaBean)rolelist.get(i);
//				vo = new RecordVo("per_dyna_bodyrank");
//				vo.setInt("body_id",Integer.parseInt(bean.get("body_id").toString()));
//				vo.setInt("plan_id",Integer.parseInt(planid));
//				vo.setInt("dyna_obj_type",Integer.parseInt(bean.get("dyna_obj_type").toString()));
//				vo.setString("dyna_obj",codeitemid);	
//				vo.setDouble("rank",((Double)defaultRankMap.get(bean.get("body_id").toString())).doubleValue());
//				dao.addValueObject(vo);
//				}
			
			for(int i=0;i<uplist.size();i++)
			{			    	
				vo = new RecordVo("per_dyna_bodyrank");
				LazyDynaBean bean = (LazyDynaBean)uplist.get(i);
				vo.setInt("body_id",Integer.parseInt(bean.get("body_id").toString()));
				vo.setInt("plan_id",Integer.parseInt(planid));
				vo.setInt("dyna_obj_type",Integer.parseInt(bean.get("dyna_obj_type").toString()));
				vo.setString("dyna_obj",codeitemid);
				dao.deleteValueObject(vo);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
