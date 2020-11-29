package com.hjsj.hrms.transaction.dutyinfo;

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

public class NullifyDutyInfoTrans  extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		 String pre="";
	     try{
			ArrayList selectedlist=(ArrayList)this.getFormHM().get("selectedinfolist");
			OrgInfoUtils orgInfoUtils=new OrgInfoUtils(this.getFrameconn());
			ArrayList msge01a1=new ArrayList();
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
		     	  String codeitemid=rec.get("e01a1").toString();
		     	  msge01a1.add(codeitemid);
		     	  CodeItem item=orgInfoUtils.getCodeItem(codeitemid, "org");
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
			this.getFormHM().put("msge01a1", msge01a1);
			String POST_HISTORY_SET = SystemConfig.getPropertyValue("POST_HISTORY_SET");//职位子集
			if (POST_HISTORY_SET != null&& POST_HISTORY_SET.trim().length() > 1&&DataDictionary.getFieldSetVo(POST_HISTORY_SET)!=null) 
			{
				ArrayList childfielditemlist = DataDictionary.getFieldList(POST_HISTORY_SET.toUpperCase(),Constant.USED_FIELD_SET);
				if(childfielditemlist==null)
				{
					this.getFormHM().put("changemsg", "no");
				}else
				{
					this.getFormHM().put("childfielditemlist", childfielditemlist);
					this.getFormHM().put("changemsg", "yes");
				}
				
			} else {
				this.getFormHM().put("changemsg", "no");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}

