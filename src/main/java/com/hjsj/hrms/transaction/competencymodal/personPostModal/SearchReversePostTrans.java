package com.hjsj.hrms.transaction.competencymodal.personPostModal;

import com.hjsj.hrms.businessobject.competencymodal.personPostModal.PersonPostModalBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchReversePostTrans.java</p> 
 * <p>Description:人岗匹配反查结果</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-01-15 11:11:11</p> 
 * @author JinChunhai
 * @version 5.0
 */

public class SearchReversePostTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		ArrayList list = new ArrayList();
		try
		{
			String onlyFild = (String)this.getFormHM().get("onlyFild");    // 人员唯一性指标
			String plan_id = (String)this.getFormHM().get("plan_id");    // 考核计划Id
			String orgCode = (String)this.getFormHM().get("orgCode");    // 组织机构树id
			String subSetMenu = (String)this.getFormHM().get("subSetMenu");	// 代码类指标
						
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			String flag = (String)hm.get("flag");     // 标识参数
			String degreeName = (String)hm.get("degreeName");     // 等级名称or分类名称
			String greeName = (String)hm.get("greeName");     // 等级名称
			
			PersonPostModalBo bo = new PersonPostModalBo(this.getFrameconn(),this.userView);
			// 获得反查结果集
			list = bo.getReverseResultList(plan_id,orgCode,degreeName,flag,onlyFild,subSetMenu,greeName);			
						
		//	this.getFormHM().put("type",type);
			this.getFormHM().put("setlist",list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
}