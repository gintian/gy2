package com.hjsj.hrms.transaction.performance.implement.kh_object.set_dyna_target_rank;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:DefaultDynaTargetRankTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Jul 7, 2008:2:59:01 PM</p> 
 *@author JinChunhai
 *@version 1.0
 */

public class DefaultDynaTargetRankTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		ArrayList rolelist = (ArrayList)this.getFormHM().get("rolelist");
		ArrayList uplist  = new ArrayList();
		String planid = (String)this.getFormHM().get("planid");
		String codeid = (String)this.getFormHM().get("codeid");
		String template_id = (String)this.getFormHM().get("template_id");
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
		
		try 
		{
			HashMap pointRankMap_default = new HashMap();
			sql.append("select pp.pointname,ptp.point_id,ptp.rank from per_template_point ptp,per_point pp where ptp.item_id in"
					+" (select item_id from per_template_item where UPPER(template_id)=(select template_id from per_plan where plan_id="+planid+")) and ptp.point_id=pp.point_id");
			this.frowset = dao.search(sql.toString());
			while(this.frowset.next())
				pointRankMap_default.put(this.frowset.getString("point_id"),new Double(this.frowset.getFloat("rank")));
			
			for(int i=0;i<rolelist.size();i++)
			{
				sql.setLength(0);
				LazyDynaBean bean = (LazyDynaBean)rolelist.get(i);
				sql.append("select * from per_dyna_rank where plan_id = '"+planid+"' and point_id ='"+bean.get("point_id")+"' and dyna_obj_type ='"+bean.get("dyna_obj_type")+"' and dyna_obj ='"+bean.get("dyna_obj")+"'");
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
				RecordVo vo = new RecordVo("per_dyna_rank");
				vo.setString("point_id",bean.get("point_id").toString());
				vo.setInt("plan_id",Integer.parseInt(planid));
				vo.setInt("dyna_obj_type",Integer.parseInt(bean.get("dyna_obj_type").toString()));
				vo.setString("dyna_obj",codeitemid);
				vo.setDouble("rank",((Double)pointRankMap_default.get(bean.get("point_id").toString())).doubleValue());
				dao.addValueObject(vo);
			}
			
			for(int i=0;i<uplist.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)uplist.get(i);
				RecordVo vo = new RecordVo("per_dyna_rank");
				vo.setString("point_id",bean.get("point_id").toString());
				vo.setInt("plan_id",Integer.parseInt(planid));
				vo.setInt("dyna_obj_type",Integer.parseInt(bean.get("dyna_obj_type").toString()));
				vo.setString("dyna_obj",codeitemid);
				vo.setDouble("rank",((Double)pointRankMap_default.get(bean.get("point_id").toString())).doubleValue());
				dao.updateValueObject(vo);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
