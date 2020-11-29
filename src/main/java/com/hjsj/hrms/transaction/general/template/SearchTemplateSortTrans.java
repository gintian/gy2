package com.hjsj.hrms.transaction.general.template;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchTemplateSortTrans extends IBusiness {


	public void execute() throws GeneralException 
	{
		String type=(String)this.getFormHM().get("type");
		if(type==null|| "".equals(type))
			type="1";
		String openseal="0";
		if("1".equals(type))
			openseal = "37";     //人事异动
		else if("2".equals(type))
			openseal = "34";     //薪资变动
		else if("8".equals(type))
			openseal = "39";      //保险变动
		else if("21".equals(type))//合同管理处理的业务模板，采用“人事异动”的模板相同的分类及授权
			openseal = "38";		
		else if("12".equals(type))//出国管理处理的业务模板，采用“人事异动”的模板相同的分类及授权
			openseal = "40";
		else if("10".equals(type))//组织机构
			openseal = "43";	
		else if("11".equals(type))//岗位变动
			openseal = "44";
		 else if("3".equals(type))
			 openseal="51";//警衔管理
		    else if("4".equals(type))
		    	openseal="53";//法官等级
		    else if("5".equals(type))
		    	openseal="54";//关衔管理
		    else if("6".equals(type))
		    	openseal="52";//检察官管理
		this.getFormHM().put("openseal", openseal);
	}

}
