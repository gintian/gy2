package com.hjsj.hrms.transaction.performance.evaluation.dealWithBusiness;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchKhObjsTrans.java</p>
 * <p>Description:生成评语模板/查找考核对象</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-08-25 10:56:23</p>
 * @author JinChunhai
 * @version 5.0
 * 
 */

public class SearchKhObjsTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
    	try
    	{
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String planId = (String) hm.get("planid");
			CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
			boolean _flag = _bo.isHavePriv(this.userView, planId);
			if(!_flag){
				return;
			}
			String code = (String)this.getFormHM().get("code");
			code=code==null?"":code;
			String objStr=(String)this.getFormHM().get("objStr");
			String order_str=(String)this.getFormHM().get("order_str");
			
			PerEvaluationBo bo = new PerEvaluationBo(this.getFrameconn());	
			
			PerformanceImplementBo pb=new PerformanceImplementBo(this.getFrameconn());	
			String whl = pb.getPrivWhere(this.userView);//根据用户权限先得到一个考核对象的范围
			if(code!=null && !"-1".equals(code))
			{
				if(AdminCode.getCodeName("UN",code)!=null&&AdminCode.getCodeName("UN",code).length()>0)
					whl+=" and b0110 like '"+code+"%'";
				else if(AdminCode.getCodeName("UM",code)!=null&&AdminCode.getCodeName("UM",code).length()>0)
					whl+=" and e0122 like '"+code+"%'";
				
			}
			if(order_str!=null && objStr.length()>0)
				whl+=" and object_id in ("+PubFunc.keyWord_reback(objStr)+") ";
			
			ArrayList list = bo.getKhObjList(planId,whl,order_str);	
			this.getFormHM().put("CurrentObjList", list);
			this.getFormHM().put("object_type", bo.getPerPlanVo(planId).getString("object_type"));
			list = bo.getRemarkTemplates();
			this.getFormHM().put("RemarkTemplates", list);
			this.getFormHM().put("remark", "");
    	} catch (Exception e)
    	{
    	    e.printStackTrace();
    	    throw GeneralExceptionHandler.Handle(e);
    	}	
    }
}
