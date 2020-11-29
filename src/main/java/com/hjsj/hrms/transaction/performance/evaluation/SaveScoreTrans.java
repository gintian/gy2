package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:保存录入分值</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 20, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class SaveScoreTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String value=(String)this.getFormHM().get("value");
			String nameDesc=(String)this.getFormHM().get("nameDesc");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String[] temps=nameDesc.replaceAll("／", "/").split("/");
			
			// trmps[1]是加密的object_id,但是不知是不是所有用到此交易的地方都被加密
			// 为将影响降到最低,先尝试解密 lium
			String object_id = temps[1];
			String _tmp = PubFunc.decrypt(temps[1]);
			if(StringUtils.isNotEmpty(_tmp)){
				object_id = _tmp;
			}
			
			String sql="update per_result_"+temps[0]+" set C_"+temps[2]+"="+value+" where object_id='"+object_id+"'";
			dao.update(sql);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
