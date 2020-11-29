package com.hjsj.hrms.transaction.performance.objectiveManage.manageKeyMatter;

import com.hjsj.hrms.businessobject.performance.objectiveManage.manageKeyMatter.KeyMatterBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:CompareKeyMatterTrans.java</p>
 * <p>Description:生效关健事件交易类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-05-03 15:45:28</p>
 * @author JinChunhai
 * @version 1.0
 */

public class CompareKeyMatterTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String comparestr = (String) hm.get("comparestr");
		comparestr = comparestr.substring(0, comparestr.length() - 1);
		String[] matters = comparestr.replaceAll("／", "/").split("/");
		
		KeyMatterBo bo = new KeyMatterBo(this.getFrameconn());
		bo.compareKeyMatters(matters);
    }
}
