package com.hjsj.hrms.transaction.performance.options;

import com.hjsj.hrms.businessobject.performance.options.PerDegreeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchDegreeHighSetTrans.java</p>
 * <p>Description:参数设置/等级分类/高级设置</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-07-15 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SearchDegreeHighSetTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
    	
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
		String degreeID=(String)hm.get("degreeID");
		String b_query=(String)hm.get("b_query");
		String plan_id = (String) this.getFormHM().get("plan_id");
		if("search".equalsIgnoreCase(b_query))
			plan_id="";
		
		PerDegreeBo bo = new PerDegreeBo(this.frameconn,degreeID,plan_id);
		
		// 得到变动的等级项目
		ArrayList list = bo.getDegrees();
		this.getFormHM().put("itemCount", new Integer(list.size()));
		this.getFormHM().put("degrees",list);
		
		// 创建临时表并插入数据	        
		bo.createTable();
	    list = bo.getData();
	    this.getFormHM().put("extpro",list);
	    
	    // 启用高级设置
		this.getFormHM().put("qy",bo.getUsed());
		this.getFormHM().put("toRoundOff",(bo.getToRoundOff()==null|| "".equals(bo.getToRoundOff()))?"0":bo.getToRoundOff());
		
		// 等级设置/高级设置 将原有按部门分组列改为:可以选择 部门、对象类别、以及评估表结构中引入的代码型指标 进行分组控制		
		ArrayList groupList = bo.getGroupList(plan_id);
		this.getFormHM().put("groupList",groupList);
		this.getFormHM().put("plan_id",plan_id);
		
    }

}
