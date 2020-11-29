package com.hjsj.hrms.transaction.general.inform.emp.view;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 *<p>Title:MultimediaTree</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-10-18:上午09:25:26</p> 
 *@author FengXiBin
 *@version 4.0
 */

public class MultimediaTree extends IBusiness {

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
			String editType = (String) hm.get("editType");
			String kind = (String)hm.get("kind");
			if("add".equals(editType)) {
				if("9".equals(kind))
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("dtgh.party.H.query")));
				else
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workbench.doty.unit.tipMsg")));
			} else if("new".equalsIgnoreCase(editType))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workbench.doty.pos.tipMsg")));
			
			String isvisible=(String)hm.get("isvisible");
			if(kind!=null)
			{
				String A0100 = (String)hm.get("a0100");
				String dbname = (String)this.getFormHM().get("dbname");
				if("6".equals(kind)){
					CheckPrivSafeBo checkPrivSafeBo=new CheckPrivSafeBo(this.frameconn,this.userView);
					dbname=checkPrivSafeBo.checkDb(dbname);
					A0100=checkPrivSafeBo.checkA0100("", dbname, A0100, "");
				}
				this.getFormHM().put("dbname",dbname);
				this.getFormHM().put("a0100",A0100);
				this.getFormHM().put("kind",kind);
				//add by wangchaoqun on 2014-9-11 begin参数加密
				String encryptParam = PubFunc.encrypt("dbpre="+dbname+"&a0100="+A0100+"&kind="+kind);
				this.getFormHM().put("encryptParam",encryptParam);
				//add by wangchaoqun on 2014-9-11 end
			}
			else
				this.getFormHM().put("newFilePriv","no");	
			
			if("hcm".equals(this.userView.getBosflag()) || "hl".equals(this.userView.getBosflag())){
				this.getFormHM().put("is_yewu","all");
			}else{
				/*判断是业务还是自助平台*/
				if(this.userView.isBbos())
					this.getFormHM().put("is_yewu","yes");	
				else
					this.getFormHM().put("is_yewu","no");
			}
			this.getFormHM().put("isvisible",isvisible==null?"0":isvisible);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	public String newFilePriv(ContentDAO dao,String kind)
	{
		StringBuffer sb = new StringBuffer();
		int i = 0;
		String retstr = "";
		String flag = "";
		if("6".equals(kind)) // 人员
		{
			sb.append(" select flag  from mediasort ");
			sb.append(" where dbflag = 1 ");
		}else if("0".equals(kind)) // 职位
		{
			sb.append(" select flag  from mediasort ");
			sb.append(" where dbflag = 3 ");
		}else	
		{
			sb.append(" select flag  from mediasort ");
			sb.append(" where dbflag = 2 ");
		}
		try
		{
			this.frowset = dao.search(sb.toString());
			while(this.frowset.next())
			{
				flag = this.frowset.getString("flag");
				if(this.userView.hasTheMediaSet(flag))
					i++;
			}
			if(i>0)
				retstr = "yes";
			else
				retstr = "no";
		}catch(Exception e){
			e.printStackTrace();
		}
		return retstr;
	}
}
