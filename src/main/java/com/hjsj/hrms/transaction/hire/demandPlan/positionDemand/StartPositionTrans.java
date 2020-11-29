package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class StartPositionTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
	    	String z0301 = (String)this.getFormHM().get("z0301");
	    	String[] arr = z0301.split("`");
	    	PositionDemand bo = new PositionDemand(this.getFrameconn());
	    	ArrayList z03list=DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
	    	ArrayList z04list=DataDictionary.getFieldList("Z04",Constant.USED_FIELD_SET);
	    	ContentDAO dao = new ContentDAO(this.getFrameconn());
	    	for(int i=0;i<arr.length;i++)
	    	{
	    		String id=arr[i];
	    		RecordVo vo = new RecordVo("z03");
		    	vo.setString("z0301",id);
		    	vo = dao.findByPrimaryKey(vo);
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
				if(!"09".equals(vo.getString("z0319")))
			    	bo.addHireOrder(z03list, z04list, Integer.parseInt(shrs), vo.getString("z0301"),this.getUserView());
	    	}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
