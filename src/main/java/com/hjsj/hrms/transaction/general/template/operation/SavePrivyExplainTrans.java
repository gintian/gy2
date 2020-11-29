package com.hjsj.hrms.transaction.general.template.operation;

import com.hjsj.hrms.businessobject.general.template.privy_explain.PrivyExplain;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 因私出国(境) 业务说明
 * <p>Title:SavePrivyExplainTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Dec 7, 2006 9:07:48 AM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class SavePrivyExplainTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		String content=(String)this.getFormHM().get("content");
		/*fckeditor 提交内容过滤注入js代码  guodd 2019-05-06 */
		content = PubFunc.stripScriptXss(content);
		PrivyExplain privyExplain=new PrivyExplain(this.getFrameconn());
		String  constantXML=privyExplain.creatSysContentXml(content);
		privyExplain.addSysContentXML(constantXML);
	}

}
