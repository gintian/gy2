package com.hjsj.hrms.module.recruitment.parameter.transaction;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SelectFieldItemTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		ArrayList list=new ArrayList();
		
		String setname=(String)this.getFormHM().get("tablename");
		String flag=(String)this.getFormHM().get("flag");
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			if("1".equals(flag))
			{
				String fieldItemIDs="";
				this.frowset=dao.search("select * from constant where constant='ZP_FIELD_LIST'");
				if(this.frowset.next())
				{
					fieldItemIDs=Sql_switcher.readMemo(this.frowset,"str_value");
				}
				if(fieldItemIDs.trim().length()>0)
				{
					if(fieldItemIDs.indexOf(setname.trim()) !=-1){
					String temp_str=fieldItemIDs.substring(fieldItemIDs.indexOf(setname.trim()));
				
					temp_str=temp_str.substring(temp_str.indexOf("{")+1);
					temp_str=temp_str.substring(0,temp_str.indexOf("}"));
				
					String[] fieldItems=temp_str.split(",");
					StringBuffer whl=new StringBuffer("");
					for(int i=0;i<fieldItems.length;i++)
					{
						whl.append(",'"+fieldItems[i].substring(0,fieldItems[i].indexOf("["))+"'");
					}
					//String b=whl.toString();
					if("A01".equalsIgnoreCase(setname))
					{
						if(whl.indexOf("B0110")!=-1)
						{
							CommonData dataobj = new CommonData("B0110",ResourceFactory.getProperty("tree.unroot.undesc"));
							 list.add(dataobj);
						}
					/*	if(whl.indexOf("E01A1")!=-1)
						{
							CommonData dataobj = new CommonData("E01A1","职位");
							 list.add(dataobj);
						}*/
						
					}
					if("Z03".equals(setname)){
					this.frowset=dao.search("select * from t_hr_busifield where fieldsetid='Z03' and useflag='1' and state= '1'");	
					}else{
					this.frowset=dao.search("select itemid,itemdesc,itemtype from fielditem where useflag=1 and itemtype<>'M' and itemid in ("+whl.substring(1)+")");
					}
					while(this.frowset.next())
					{
						 //if(this.frowset.getString("itemid").equalsIgnoreCase("A0101"))
							 //continue;
						 CommonData dataobj = new CommonData(this.frowset.getString("itemid"), this.frowset.getString("itemdesc"));
						 list.add(dataobj);
					}
				  }else{
					  if("Z03".equals(setname)){
							this.frowset=dao.search("select * from t_hr_busifield where fieldsetid='Z03' and useflag='1' and state= '1'");	
							}
					  while(this.frowset.next())
						{
							 //if(this.frowset.getString("itemid").equalsIgnoreCase("A0101"))
								 //continue;
							 CommonData dataobj = new CommonData(this.frowset.getString("itemid"), this.frowset.getString("itemdesc"));
							 list.add(dataobj);
						}
					  
				  }
					
				}
							
			}
			else 
			{
				if("2".equals(flag)|| "4".equals(flag)|| "5".equals(flag)){//dml 2011-6-22 10:53:42
					this.frowset=dao.search("select * from t_hr_busiField where fieldsetid='"+setname+"' and itemtype<>'M' and useflag=1");
					
				}else if("3".equals(flag)){
					this.frowset=dao.search("select * from t_hr_busiField where fieldsetid='"+setname+"' and useflag=1");
					
				}
				while(this.frowset.next())
				{
					if("0".equals(this.frowset.getString("state")))
						continue;
					if("Z0301".equalsIgnoreCase(this.frowset.getString("itemid")))
						continue;
					 CommonData dataobj = new CommonData(this.frowset.getString("itemid"), this.frowset.getString("itemdesc"));
					 list.add(dataobj);
				}
				if("4".equals(flag))
				{
					CommonData cd = new CommonData("yprsl","应聘人数(推荐人数)");
					list.add(cd);
					cd=new CommonData("ypljl","应聘(推荐)");
					list.add(cd);
					
					cd=new CommonData("opentime","发布时间");
					list.add(cd);
				}
				
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		this.getFormHM().clear();
	    this.getFormHM().put("fieldlist",list);
	    

	}
	
	
	
	public static void main(String[] arg)
	{
		
		String fieldItemIDs="A01{B0110,E0122,E01A1,A0101,},A04{A0420,},A03{B0110,A0101,},";
		String fieldSet="A03";
		String temp_str=fieldItemIDs.substring(fieldItemIDs.indexOf(fieldSet));
		temp_str=temp_str.substring(temp_str.indexOf("{")+1);
		temp_str=temp_str.substring(0,temp_str.indexOf("}"));
	
		
	}

}
