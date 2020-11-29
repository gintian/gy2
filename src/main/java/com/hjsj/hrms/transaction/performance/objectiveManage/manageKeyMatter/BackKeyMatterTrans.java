package com.hjsj.hrms.transaction.performance.objectiveManage.manageKeyMatter;

import com.hjsj.hrms.businessobject.performance.objectiveManage.manageKeyMatter.KeyMatterBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:BackKeyMatterTrans.java</p>
 * <p>Description:退回关健事件交易类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-05-03 15:45:28</p>
 * @author JinChunhai
 * @version 1.0
 */

public class BackKeyMatterTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String spBackstr = (String) hm.get("spBackstr");
		spBackstr = spBackstr.substring(0, spBackstr.length() - 1);
		String[] matters = spBackstr.replaceAll("／", "/").split("/");
		
		KeyMatterBo bo = new KeyMatterBo(this.getFrameconn());
		bo.spBackKeyMatters(matters);
    }
}
