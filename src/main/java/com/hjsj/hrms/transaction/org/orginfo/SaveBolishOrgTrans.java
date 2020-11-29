/*
 * Created on 2006-1-13
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.org.orginfo;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SaveBolishOrgTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		  String pre="";
		  DbNameBo dbvo=new DbNameBo(this.getFrameconn());
		try{
			ArrayList selectedlist=(ArrayList)this.getFormHM().get("selectedlist");
			if(selectedlist==null || selectedlist.size()==0)
			{
				this.getFormHM().put("bolishlist",new ArrayList()); 
				this.getFormHM().put("bolishorgname","");
				/**权限范围内的人员库列表*/
				ArrayList dblist=DataDictionary.getDbpreList();
				dblist=dbvo.getDbNameVoList(dblist);
				ArrayList list=new ArrayList();
				for(int i=0;i<dblist.size();i++)
				{
					CommonData vo=new CommonData();
					RecordVo dbname=(RecordVo)dblist.get(i);
					vo.setDataName(dbname.getString("dbname"));
					vo.setDataValue(dbname.getString("pre"));
					if(i==0)
						pre=dbname.getString("pre");
					list.add(vo);
				}
				this.getFormHM().put("dbprelist",list);
				this.getFormHM().put("ishavepersonmessage","");
			}else{
			ArrayList bolishlist=new ArrayList();
			for(int i=0;i<selectedlist.size();i++){
	    		RecordVo vo=(RecordVo)selectedlist.get(i);
	    		CommonData dataobj =  new CommonData(vo.getString("codesetid")+vo.getString("codeitemid"),vo.getString("codeitemdesc"));
	             bolishlist.add(dataobj);
	    	}
			this.getFormHM().put("bolishlist",bolishlist); 
			/**权限范围内的人员库列表*/
			ArrayList dblist=DataDictionary.getDbpreList();
			dblist=dbvo.getDbNameVoList(dblist);
			ArrayList list=new ArrayList();
			for(int i=0;i<dblist.size();i++)
			{
				CommonData vo=new CommonData();
				RecordVo dbname=(RecordVo)dblist.get(i);
				vo.setDataName(dbname.getString("dbname"));
				vo.setDataValue(dbname.getString("pre"));
				if(i==0)
					pre=dbname.getString("pre");
				list.add(vo);
			}
			this.getFormHM().put("dbprelist",list);
			this.getFormHM().put("ishavepersonmessage","");
			}
			this.getFormHM().put("movepersons",new ArrayList());
			this.getFormHM().put("isrefresh","bolish");
			String value = "UNIT_HISTORY_SET";
			RecordVo vo=(RecordVo)selectedlist.get(0);
			if("UN".equalsIgnoreCase(vo.getString("codesetid")) || "UM".equalsIgnoreCase(vo.getString("codesetid")))
				value = "UNIT_HISTORY_SET";
			else if("@K".equalsIgnoreCase(vo.getString("codesetid")))
				value = "POST_HISTORY_SET";
			String HISTORY_SET = SystemConfig
			.getPropertyValue(value);
			if (HISTORY_SET != null
					&& HISTORY_SET.trim().length() > 1&&DataDictionary.getFieldSetVo(HISTORY_SET)!=null) {
				ArrayList childfielditemlist = DataDictionary
						.getFieldList(HISTORY_SET.toUpperCase(),
								Constant.USED_FIELD_SET);
				childfielditemlist = childfielditemlist!=null?childfielditemlist:new ArrayList();
				this.getFormHM().put("childfielditemlist", childfielditemlist);
				this.getFormHM().put("HISTORY_SET", HISTORY_SET);
				this.getFormHM().put("changemsg", "yes");
			} else {
				this.getFormHM().put("changemsg", "no");
				this.getFormHM().put("childfielditemlist", new ArrayList());
			}
			 
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
