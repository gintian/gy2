package com.hjsj.hrms.transaction.performance.batchGrade;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeSinglePointBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 多人打分——单指标显示
 * @author 邓灿
 *
 */
public class ExcecuteBatchGrade_SinglePoint_Trans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String plan_id="";
			String point_index=(String)this.getFormHM().get("point_index");
			if(!"query".equals((String)hm.get("b_Desc")))
			{
				plan_id=(String)hm.get("b_Desc");	
				hm.remove("b_Desc");
				point_index="0";
			}
			else
			{
				plan_id=(String)this.getFormHM().get("plan_id");	
			}
			
			BatchGradeSinglePointBo bo=new BatchGradeSinglePointBo(this.getFrameconn(),plan_id,this.userView);
			if(!"0".equals(point_index))
			{
				if(this.getFormHM().get("objectList")!=null&&((ArrayList)this.getFormHM().get("objectList")).size()>0)
					bo.setObject_List((ArrayList)this.getFormHM().get("objectList"));
				
				if(this.getFormHM().get("object_priv_map")!=null&&((HashMap)this.getFormHM().get("object_priv_map")).size()>0)
					bo.setObject_priv_map((HashMap)this.getFormHM().get("object_priv_map"));
				
			}
			String html=bo.getGradeHtml(Integer.parseInt(point_index));
			
			this.getFormHM().put("objectList",bo.getObject_List());
			this.getFormHM().put("object_priv_map",bo.getObject_priv_map());
			this.getFormHM().put("isAllSub",bo.getIsAllSub(bo.getObject_List()));
			this.getFormHM().put("plan_id",plan_id);
			this.getFormHM().put("point_id",bo.get_point_id());
			this.getFormHM().put("objects_str", bo.getobjects_str(bo.getObject_List()));
			this.getFormHM().put("totalNumber",String.valueOf(bo.getPointList().size()));
			this.getFormHM().put("point_index", point_index);
			this.getFormHM().put("tableWidth", bo.getTableWidth());
			this.getFormHM().put("batchGradeHtml",html);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
