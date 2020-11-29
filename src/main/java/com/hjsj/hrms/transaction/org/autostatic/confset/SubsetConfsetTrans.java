package com.hjsj.hrms.transaction.org.autostatic.confset;

import com.hjsj.hrms.businessobject.org.autostatic.confset.SubsetConfsetBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.List;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author ${FengXiBin}
 *@version 4.0
  */
public class SubsetConfsetTrans extends IBusiness{

	public void execute() throws GeneralException
	{
		
		SubsetConfsetBo scb = new SubsetConfsetBo();
//		ContentDAO dao = new ContentDAO(this.getFrameconn());
		HashMap hm=(HashMap)this.getFormHM();
		HashMap reqhm = (HashMap) hm.get("requestPamaHM");
		String init =(String)reqhm.get("init");
		reqhm.remove("init");
		if(init==null || "".equals(init))
		{
			String str=(String) hm.get("changeflagstr");
			str = PubFunc.keyWord_reback(str);//add by wangchaoqun on 2014-9-13 对于转换为全角的数据进行还原
			try{
				scb.updatesubset(str,this.getFrameconn());
				DataDictionary.refresh();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		List subsetlist = (List)scb.getsubsetlist();
		for(int i=0;i<subsetlist.size();i++){
			FieldSet fs=(FieldSet)subsetlist.get(i);
			String fid=fs.getFieldsetid();
			if("0".equals(this.userView.analyseTablePriv(fid))|| "0".equals(this.userView.analyseTablePriv(fid,1))){
				subsetlist.remove(i);
				i--;
			}			
		}
		
		hm.put("subsetlist", subsetlist);
		
	}
}
