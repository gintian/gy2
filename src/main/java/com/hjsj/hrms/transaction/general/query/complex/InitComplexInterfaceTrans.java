package com.hjsj.hrms.transaction.general.query.complex;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 复杂查询初始化
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jun 25, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class InitComplexInterfaceTrans extends IBusiness {
	
	
	public void execute() throws GeneralException 
	{
		//ArrayList  complexList=getComplexList();
		//this.getFormHM().put("complexList", complexList);
		ArrayList setList = new ArrayList();
		ArrayList itemList = new ArrayList();
	
		String fieldSetId = (String)this.getFormHM().get("setid");
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String fromFlag="0";
		if(fieldSetId == null || "".equals(fieldSetId)){//首次进入
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			if(map.get("fromFlag")!=null)
			{
				fromFlag=(String)map.get("fromFlag");
				map.remove("fromFlag");
			}
			String setsql = "select fieldsetid , fieldsetdesc,customdesc from fieldset where useflag <> 0 and (fieldsetid like 'A%' or fieldsetid like 'K%' or fieldsetid like 'B%') order by "+com.hrms.hjsj.utils.Sql_switcher.substr("fieldsetid", "1", "1")+",Displayorder";
			String firstSetId = "";
			boolean b = false;
			try {
				this.frowset = dao.search(setsql);
				while(this.frowset.next()){
					
					if("0".equals(this.userView.analyseTablePriv(this.frowset.getString("fieldsetid"))))
				        continue;
					if(!b){
						firstSetId = this.frowset.getString("fieldsetid");
						b=true;
					}
					CommonData dataobj = new CommonData();
					String setid = this.getFrowset().getString("fieldsetid");
					String setdesc ="";
					if(this.getFrowset().getString("customdesc")!=null&&this.getFrowset().getString("customdesc").length()>0)
						setdesc= this.getFrowset().getString("customdesc");
					else
						setdesc= this.getFrowset().getString("fieldsetdesc");
					dataobj = new CommonData(setid,/*"("+setid+")"+*/setdesc);
					setList.add(dataobj);
				}
				 CommonData dataobj = new CommonData();
				 dataobj = new CommonData("midvariable","临时变量表");//MidVariadle:
				 setList.add(dataobj);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			//String itemsql = "select itemid,itemdesc,itemtype  from fielditem where useflag <> 0 and fieldsetid ='"+firstSetId+"'";
			ArrayList fielditemlist=DataDictionary.getFieldList(firstSetId,Constant.USED_FIELD_SET);
			CommonData top = new CommonData();
			top = new CommonData("","");
			itemList.add(top);
			if(fielditemlist!=null)
		    {
				for(int i=0;i<fielditemlist.size();i++)
			    {
			      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
			      if("M".equals(fielditem.getItemtype()))
				    	continue;
				  if("0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
				        continue;
				  CommonData dataobj = new CommonData();
				  dataobj = new CommonData(fielditem.getItemdesc(),fielditem.getItemdesc());
				  itemList.add(dataobj);
			   }
		   }		  
		}else{//改变指标集
			String itemsql = "select itemid,itemdesc  from fielditem where useflag <> 0 and fieldsetid ='"+fieldSetId+"'";
			CommonData top = new CommonData();
			top = new CommonData("","");
			itemList.add(top);
			try {
				this.frowset = dao.search(itemsql);
				while(this.frowset.next()){
					String itemdesc = this.frowset.getString("itemdesc");
					itemdesc = itemdesc.replace("\r\n", "");
					CommonData dataobj = new CommonData();
					dataobj = new CommonData(itemdesc,itemdesc);
					itemList.add(dataobj);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}	
		
		StringBuffer fieldItems = new StringBuffer(); 
		String sql="select itemdesc ,itemid , itemtype from fielditem where useflag='1' ";
		try {
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				String itemdesc = this.frowset.getString("itemdesc");
				itemdesc = itemdesc.replace("\r\n", "");
				String itemtype = this.frowset.getString("itemtype");
				fieldItems.append(itemtype);
				fieldItems.append(" ");
				fieldItems.append(itemdesc);
				fieldItems.append(",");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ArrayList dbaselist=userView.getPrivDbList();  
		DbNameBo dbvo=new DbNameBo(this.getFrameconn());
		dbaselist=dbvo.getDbNameVoList(dbaselist);		
		ArrayList dbList=new ArrayList();
		for(int i=0;i<dbaselist.size();i++)
		{
			CommonData vo=new CommonData();
			RecordVo dbname=(RecordVo)dbaselist.get(i);
			vo.setDataName(dbname.getString("dbname"));
			vo.setDataValue(dbname.getString("pre"));
			dbList.add(vo);
		}
		this.getFormHM().put("compledblist", dbList);
		this.getFormHM().put("comple_db", "ALL");
		this.getFormHM().put("fieldItems",fieldItems.toString());
		this.getFormHM().put("setlist",setList);
		this.getFormHM().put("itemlist",itemList);
		String cardid=searchCard("1");
		this.getFormHM().put("tabid",cardid);
		this.getFormHM().put("fromFlag", fromFlag);
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
        String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
    	if(uplevel==null||uplevel.length()==0)
    		uplevel="0";
    	this.getFormHM().put("uplevel", uplevel);
	}
    private ArrayList getComplexList()
    {
    	ArrayList list=new ArrayList();
    	String sql="select id,name from gwhere order by id";
    	CommonData da=new CommonData();
    	/*da.setDataName("请选择...");
    	da.setDataValue("");
    	list.add(da);*/
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	try
    	{
    		this.frowset=dao.search(sql);
    		while(this.frowset.next())
    		{
    			da=new CommonData();
    	    	da.setDataName(this.frowset.getString("name"));
    	    	da.setDataValue(this.frowset.getString("id"));
    	    	list.add(da);
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    }
    private String searchCard(String infortype)
    {
		 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		 String cardid="-1";
		 try
		 {
			 if("1".equalsIgnoreCase(infortype))
			 {
				 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"emp");
			 }
			 if("2".equalsIgnoreCase(infortype))
			 {
				 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"org");
			 }
			 if("3".equalsIgnoreCase(infortype))
			 {
				 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"pos");
			 }
			 if(cardid==null|| "".equalsIgnoreCase(cardid)|| "#".equalsIgnoreCase(cardid))
				 cardid="-1";
		 }
		 catch(Exception ex)
		 {
			 ex.printStackTrace();
		 }
		 return cardid;
    }
}
