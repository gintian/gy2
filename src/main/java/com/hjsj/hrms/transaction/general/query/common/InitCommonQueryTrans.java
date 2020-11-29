/**
 * 
 */
package com.hjsj.hrms.transaction.general.query.common;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:InitCommonQueryTrans</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-28:13:39:51</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class InitCommonQueryTrans extends IBusiness {

	/**
	 * 取得权限范围的人员库列表
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList getDbList(String show_dbpre) throws GeneralException {
		ArrayList dblist=this.userView.getPrivDbList();
		DbNameBo dbvo=new DbNameBo(this.getFrameconn());
		dblist=dbvo.getDbNameVoList(dblist);
		ArrayList list=new ArrayList();
		for(int i=0;i<dblist.size();i++)
		{
			CommonData vo=new CommonData();
			RecordVo dbname=(RecordVo)dblist.get(i);
			if(show_dbpre.indexOf(dbname.getString("pre"))==-1)
				continue;
			vo.setDataName(dbname.getString("dbname"));
			vo.setDataValue(dbname.getString("pre"));
			list.add(vo);
		}
		return list;
	}
	
	public void execute() throws GeneralException {
		  try
		  {
			  HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String show_dbpre=(String)this.getFormHM().get("show_dbpre");	
			this.getFormHM().remove("setidpiv");
			this.getFormHM().remove("setdescpiv");
			
			String setid=(String)hm.get("setid");
			setid=setid!=null?setid:"";
			hm.remove("setid");
			String setdesc=(String)hm.get("setdesc");
			setdesc=setdesc!=null?setdesc:"";
			hm.remove("setdesc");
			String priv=(String)hm.get("priv");
			priv=priv!=null?priv:"1";
			hm.remove("priv");
			//人事异动用tabid
			String tabId=(String)hm.get("tabId");
			tabId=tabId!=null?tabId:"";
			hm.remove("tabId");
			/**未定义信息类别,默认为人员信息*/
			String infor_kind=(String)this.getFormHM().get("type");
			if(infor_kind==null|| "".equals(infor_kind))
				infor_kind="1";
			this.getFormHM().put("type",infor_kind);
			/**
			 * 权限范围内的人员库列表
			 */
			ArrayList list = getDbList(show_dbpre);
			
			if(hm.get("isGetSql")!=null)
			{
				this.getFormHM().put("isGetSql","1");
				hm.remove("isGetSql");
			}
			else
				this.getFormHM().put("isGetSql","0");
			
			this.getFormHM().put("dblist",list);
			this.getFormHM().put("chpriv",priv);
			this.getFormHM().put("selectedlist",new ArrayList());
			this.getFormHM().put("expression","");
			this.getFormHM().put("setidpiv",setid);
			this.getFormHM().put("setdescpiv",setdesc);
			this.getFormHM().put("factorlist",  new ArrayList());
			this.getFormHM().put("tabId",  tabId);
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
			  throw GeneralExceptionHandler.Handle(ex);
		  }

	}

}
