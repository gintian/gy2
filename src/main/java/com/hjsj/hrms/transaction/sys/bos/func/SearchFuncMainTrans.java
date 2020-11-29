/**
 * 
 */
package com.hjsj.hrms.transaction.sys.bos.func;

import com.hjsj.hrms.businessobject.sys.bos.func.FuncMainBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:查询功能列表</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 29, 2008:3:15:01 PM</p>
 * @author xgq
 * @version 1.0
 * 
 */
public class SearchFuncMainTrans extends IBusiness {
    /**
	 */
	
	public void execute() throws GeneralException {
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
        String function_id = (String)hm.get("parentid");
        String ctrl_ver = (String)hm.get("ctrl_ver");
        String lockVersion = (String)this.formHM.get("lockVersion");
        Document doc =null;
        if(this.getFormHM().get("function_dom")!=null){
			 doc = (Document)this.getFormHM().get("function_dom");
		}else{
		FuncMainBo fbo = new FuncMainBo();
		 doc =fbo.getDocument();
		}
        FuncMainBo funcbo = new FuncMainBo(this.getFrameconn());
        funcbo.setLockVersion(lockVersion);
        if(ctrl_ver!=null && ctrl_ver.length()>0)
        	funcbo.setCtrl_ver(ctrl_ver);
        
        ArrayList list =funcbo.getFunctionContent(function_id,doc);
        this.getFormHM().put("functionMainlist", list);
        this.getFormHM().remove("parentid");
        this.getFormHM().put("parentid", function_id);
//        this.getFormHM().put("precodeitemid", function_id);
//        this.getFormHM().put("codeitemid", function_id);
//        this.getFormHM().put("codeitemdesc", function_id);

	}
	
}
