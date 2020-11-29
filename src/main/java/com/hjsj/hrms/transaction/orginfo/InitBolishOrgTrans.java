package com.hjsj.hrms.transaction.orginfo;

import com.hjsj.hrms.businessobject.info.OrgInfoUtils;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class InitBolishOrgTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
			String pre="";
			ArrayList selectedlist=(ArrayList)this.getFormHM().get("selectedinfolist");
			OrgInfoUtils orgInfoUtils=new OrgInfoUtils(this.getFrameconn());
			if(selectedlist==null || selectedlist.size()==0)
			{
				this.getFormHM().put("bolishlist",new ArrayList()); 
				this.getFormHM().put("bolishorgname","");
				/**权限范围内的人员库列表*/
				ArrayList dblist=DataDictionary.getDbpreList();
				DbNameBo dbvo=new DbNameBo(this.getFrameconn());
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
			    for(int i=0;i<selectedlist.size();i++)
			    {
			      LazyDynaBean rec=(LazyDynaBean)selectedlist.get(i); 
		     	  String codeitemid=rec.get("code").toString();
		     	  CodeItem item=orgInfoUtils.getCodeItem1(codeitemid, "org");
		     	  if(item==null)
		     		  continue;
	    		  CommonData dataobj =  new CommonData(item.getCodeid()+item.getCodeitem(),item.getCodename());
	              bolishlist.add(dataobj);
	    	   }
			   this.getFormHM().put("bolishlist",bolishlist); 
			   /**权限范围内的人员库列表*/
			   ArrayList dblist=DataDictionary.getDbpreList();
			   DbNameBo dbvo=new DbNameBo(this.getFrameconn());
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
			String UNIT_HISTORY_SET = SystemConfig.getPropertyValue("UNIT_HISTORY_SET");
			if (UNIT_HISTORY_SET != null&& UNIT_HISTORY_SET.trim().length() > 1&&DataDictionary.getFieldSetVo(UNIT_HISTORY_SET)!=null) 
			{
				ArrayList childfielditemlist = DataDictionary.getFieldList(UNIT_HISTORY_SET.toUpperCase(),Constant.USED_FIELD_SET);
				childfielditemlist=childfielditemlist!=null?childfielditemlist:new ArrayList();
				this.getFormHM().put("childfielditemlist", childfielditemlist);
				this.getFormHM().put("changemsg", "yes");
			} else {
				//add by wangchaoqun on 2014-9-30 begin 配置文件未配置‘UNIT_HISTORY_SET’时，给childfielditemlist赋予一个空值
				ArrayList childfielditemlist = new ArrayList();
				this.getFormHM().put("childfielditemlist", childfielditemlist);
				//add by wangchaoqun on 2014-9-30 end
				this.getFormHM().put("changemsg", "no");
			}
		}catch(GeneralException e)
		{
			e.printStackTrace();
			throw e;
		}

	}

}
