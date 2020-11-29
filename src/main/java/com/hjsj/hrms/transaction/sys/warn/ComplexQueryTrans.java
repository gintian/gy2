/**
 * 
 */
package com.hjsj.hrms.transaction.sys.warn;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author Owner
 */
public class ComplexQueryTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		ArrayList setList = new ArrayList();
		ArrayList itemList = new ArrayList();	
		String fieldSetId = (String)this.getFormHM().get("setid");		
		String warntype=(String)this.getFormHM().get("warntype");
		if(warntype==null||warntype.length()<=0)
			warntype="0";
		
		this.getFormHM().put("warntype", warntype);
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		
		if(fieldSetId == null || "".equals(fieldSetId)){//首次进入
			String setsql = "select "+Sql_switcher.substr("fieldsetid", "1", "1")+" classid,fieldsetid , fieldsetdesc,customdesc from fieldset where useflag <> 0";
			if("1".equals(warntype))
			  setsql=setsql+" and fieldsetid like 'B%'";
			else if("2".equals(warntype))
			  setsql=setsql+" and fieldsetid like 'K%'";
			setsql=setsql+" order by classid,displayorder";
			;
			String firstSetId = "";
			boolean b = false;
			try {
				this.frowset = dao.search(setsql);
				while(this.frowset.next()){
					if(!b){
						firstSetId = this.frowset.getString("fieldsetid");
						b=true;
					}
					CommonData dataobj = new CommonData();
					String setid = this.getFrowset().getString("fieldsetid");
					String setdesc = this.getFrowset().getString("customdesc");
					dataobj = new CommonData(setid,"("+setid+")"+setdesc);
					setList.add(dataobj);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			/*String itemsql = "select itemid,itemdesc  from fielditem where useflag <> 0 and fieldsetid ='"+firstSetId+"'";
			CommonData top = new CommonData();
			top = new CommonData("","");
			itemList.add(top);
			try {
				this.frowset = dao.search(itemsql);
				while(this.frowset.next()){
					String itemid = this.frowset.getString("itemid");
					String itemdesc = this.frowset.getString("itemdesc");
					CommonData dataobj = new CommonData();
					//dataobj = new CommonData(itemid,itemdesc);
					dataobj = new CommonData(itemdesc,"("+itemid+")"+itemdesc);
					itemList.add(dataobj);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
			fieldSetId=firstSetId;
			ArrayList fielditemlist=DataDictionary.getFieldList(tranQ05ToQ03(firstSetId),Constant.USED_FIELD_SET);
			CommonData top = new CommonData();
			top = new CommonData("","");
			itemList.add(top);
			try
			{
				if(fielditemlist!=null)
			    {
					for(int i=0;i<fielditemlist.size();i++)
				    {
				      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
				      if("M".equals(fielditem.getItemtype()))
				    	continue;
				      
				      if(!"3".equals(warntype) && "0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
				        continue;
				      
				      CommonData dataobj = new CommonData();
				      dataobj = new CommonData(fielditem.getItemdesc(),fielditem.getItemdesc());				      
				      itemList.add(dataobj);
				    }
			    }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}else if("midvariable".equalsIgnoreCase(fieldSetId))
		{
			String templetid="0";
			String nflag="3";	
			
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("select cname,chz,nid,cValue");			

			sqlstr.append(" from midvariable where templetid="+templetid+" and nflag="+nflag);
			CommonData dataobj = new CommonData();
			dataobj = new CommonData("","");
			itemList.add(dataobj);
			try {
				this.frowset = dao.search(sqlstr.toString());
				while(this.frowset.next()){
					dataobj = new CommonData(this.frowset.getString("chz"),this.frowset.getString("chz"));
					itemList.add(dataobj);
				}
			}catch(Exception e)
		    {
					e.printStackTrace();
		    }
			dataobj = new CommonData("newcreate","新建临时变量");
			itemList.add(dataobj);
		}else{//改变指标集
			/*String itemsql = "select itemid,itemdesc  from fielditem where useflag <> 0 and fieldsetid ='"+fieldSetId+"'";
			CommonData top = new CommonData();
			top = new CommonData("","");
			itemList.add(top);
			try {
				this.frowset = dao.search(itemsql);
				while(this.frowset.next()){
					//String itemid = this.frowset.getString("itemid");
					String itemdesc = this.frowset.getString("itemdesc");
					CommonData dataobj = new CommonData();
					//dataobj = new CommonData(itemid,itemdesc);
					dataobj = new CommonData(itemdesc,itemdesc);
					itemList.add(dataobj);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
			ArrayList fielditemlist=DataDictionary.getFieldList(tranQ05ToQ03(fieldSetId),Constant.USED_FIELD_SET);
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
			      
			      if(!"3".equals(warntype) && "0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
			        continue;
			      
			      CommonData dataobj = new CommonData();
			      dataobj = new CommonData(fielditem.getItemdesc(),fielditem.getItemdesc());
			      
			      itemList.add(dataobj);
			    }
		    }
		}	
		
		StringBuffer fieldItems = new StringBuffer(); 
		/*String sql="select itemdesc ,itemid , itemtype from fielditem where useflag='1' ";
		try {
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				//String itemid = this.frowset.getString("itemid");
				String itemdesc = this.frowset.getString("itemdesc");
				String itemtype = this.frowset.getString("itemtype");
				fieldItems.append(itemtype);
				fieldItems.append(" ");
				fieldItems.append(itemdesc);
				fieldItems.append(",");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
		ArrayList fielditemlist=DataDictionary.getFieldList(tranQ05ToQ03(fieldSetId),Constant.USED_FIELD_SET);
		if(fielditemlist!=null)
	    {
			for(int i=0;i<fielditemlist.size();i++)
		    {
		        FieldItem fielditem=(FieldItem)fielditemlist.get(i);
		        if("M".equals(fielditem.getItemtype()))
			       continue;
		        
			    if(!"3".equals(warntype) && "0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
			       continue;
			    
			    String itemdesc = fielditem.getItemdesc();
				String itemtype = fielditem.getItemtype();
				fieldItems.append(itemtype);
				fieldItems.append(" ");
				fieldItems.append(itemdesc);
				fieldItems.append(",");
		    }
	    }
		this.getFormHM().put("fieldItems",fieldItems.toString());
		this.getFormHM().put("setlist",setList);
		this.getFormHM().put("itemlist",itemList);
		this.getFormHM().put("setid", fieldSetId);
	}
	
	   
    /*
     * Q05与Q03用的是同一套数据字典
     * 如果取Q05字典信息需转为Q03
     */
    private String tranQ05ToQ03(String setId) {
        if ("Q05".equalsIgnoreCase(setId))
            setId = "Q03";
        
        return setId;
        
    }
}
