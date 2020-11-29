package com.hjsj.hrms.transaction.sys.dbinit.fielditem;

import com.hjsj.hrms.businessobject.sys.fieldsubset.IndexBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 
 * <p>Title:指标排序</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 20, 2008:1:38:29 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class SortingIndexTrans extends IBusiness{

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String fieldsetid=(String)reqhm.get("fieldsetid");
		
		fieldsetid=fieldsetid!=null&&fieldsetid.trim().length()>0?fieldsetid:"";
		reqhm.remove("fieldsetid");
		if(fieldsetid.length()>0){
			IndexBo subset = new IndexBo(this.getFrameconn());
			if(fieldsetid.length()==1){
				hm.put("sortlist", subset.sortSet(fieldsetid));
			}else{
				hm.put("sortlist", subset.sortList(fieldsetid));
			}
		}
		hm.put("setid", fieldsetid);
	}

}
