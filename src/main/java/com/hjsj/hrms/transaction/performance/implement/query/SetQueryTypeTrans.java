package com.hjsj.hrms.transaction.performance.implement.query;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SetQueryTypeTrans extends IBusiness {


	
	public SetQueryTypeTrans() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void execute() throws GeneralException {
		 /**查询类型*/
	
		
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
        if(hm!=null)
        {
       
        	String plan_id=(String)hm.get("plan_id");
            String query_type="2";
            String a_flag=(String)hm.get("a_flag");
            String a_id=(String)hm.get("a_roleid");
            String type=(String)hm.get("a_inforkind");
//            String objectType=(String)hm.get("objectType");
            if(type==null|| "".equals(type))
            	type="1";
            String object_id=(String)hm.get("object_id");
            String body_id=(String)hm.get("body_id");
            String flag=(String)hm.get("flag");
            if(flag==null|| "".equals(flag))
            	flag="1";
            
            this.getFormHM().put("plan_id",plan_id);
            this.getFormHM().put("query_type",query_type);
            this.getFormHM().put("type",type);
            this.getFormHM().put("object_id",object_id);
            this.getFormHM().put("body_id",body_id);
            this.getFormHM().put("flag",flag);
            
            PerformanceImplementBo pb=new PerformanceImplementBo(this.getFrameconn());            
            this.getFormHM().put("objectType",pb.getPlanVo(plan_id).getString("object_type"));
            this.getFormHM().put("accordByDepartment","0"); 
        }
        this.getFormHM().put("isSelectAll","0"); 
        this.getFormHM().put("succeedinfo","");
    
	}

}
