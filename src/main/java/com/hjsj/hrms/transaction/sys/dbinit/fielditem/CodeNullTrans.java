package com.hjsj.hrms.transaction.sys.dbinit.fielditem;

import com.hjsj.hrms.businessobject.sys.fieldsubset.IndexBo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * <p>Title:验证代码类是否为空(长度)</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 5, 2008:11:09:16 AM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class CodeNullTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
		String msgs = "1";
		String obj = (String)this.getFormHM().get("obj");
		IndexBo subset = new IndexBo(this.getFrameconn());
		
		boolean flag=false;
		if(subset.codelength(obj)){
			flag=true;
			msgs=ResourceFactory.getProperty("kjg.error.clew");
		}
		this.getFormHM().put("msgs", msgs);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}
