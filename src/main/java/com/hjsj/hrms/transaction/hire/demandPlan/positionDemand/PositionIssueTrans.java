package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
public class PositionIssueTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("position_set_table");
		ArrayList list=(ArrayList)hm.get("position_set_record");		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		VersionControl vc = new VersionControl();
		try
		{
			if(!(list==null||list.size()==0))
			{	
				StringBuffer info=new StringBuffer("");
				for(int i=0;i<list.size();i++)
				{
					RecordVo vo=(RecordVo)list.get(i);
					
					if(!"03".equals(vo.getString("z0319"))&&!"06".equals(vo.getString("z0319"))&&!"09".equals(vo.getString("z0319")))
					{	info.append("注意：只能发布已批、结束、暂停状态的需求！");
						
					}
					if(info.length()>1)
						throw GeneralExceptionHandler.Handle(new Exception(info.toString()));
					
				}	
				PositionDemand bo = new PositionDemand(this.getFrameconn());
				ArrayList z03list=DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
				ArrayList z04list=DataDictionary.getFieldList("Z04",Constant.USED_FIELD_SET);
				for(int i=0;i<list.size();i++)
				{
					RecordVo vo=(RecordVo)list.get(i);	
					//if(!vo.getString("z0319").equals("03"))
						//continue;
					String sql="update z03 set z0319='04'";
					String shrs="0";
					if(vo.getString("z0315")!=null&&vo.getInt("z0315")!=0)
					{
						sql+=",z0315="+vo.getString("z0315");
						shrs=vo.getString("z0315");
					}
					else
					{
						shrs=vo.getString("z0313");
						sql+=",z0315="+vo.getString("z0313");
					}
					sql+=" where z0301='"+vo.getString("z0301")+"'";
					dao.update(sql);
					/**如果招聘订单功能发开的话，才往招聘订单表中增加记录*/
					if(vc.searchFunctionId("31015"))
					{
			    		if(!"09".equals(vo.getString("z0319")))
				        	bo.addHireOrder(z03list, z04list, Integer.parseInt(shrs), vo.getString("z0301"),this.getUserView());
					}
				}					
			}
	    }
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		
	}

}
