package com.hjsj.hrms.transaction.gz.premium.param;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.*;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class GetGeneralFieldTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			int flag=Integer.parseInt((String)this.getFormHM().get("flag"));  //  1:  子标集  2: 子标  3: 代码  0:累计方式
			
			ArrayList list=new ArrayList();
			
			switch(flag){
				case 0:
					list= getAddUpList();
				    break;
				case 1:
					String fmode =(String)this.getFormHM().get("fmode");
					fmode=fmode!=null&&fmode.trim().length()>0?fmode:"";
					
					list= getfieldSetList(fmode);
					
				    break;
				case 2:
					String itemSet=(String)this.getFormHM().get("value");
					list=getfieldItemList(itemSet);
				    break;
				case 3:
					String itemid=(String)this.getFormHM().get("value");
					list=getcodeItemList(itemid);
					break;
				case 4:
					list= getfieldSetListA();
					break;
			}
			
			
			this.getFormHM().put("flag",String.valueOf(flag));
			this.getFormHM().put("list",list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	
	private ArrayList getAddUpList()
	{
		ArrayList list=new ArrayList();
		list.add(new CommonData("0","不累积"));
		list.add(new CommonData("1","月内累积"));
		list.add(new CommonData("2","季度内累积"));
		list.add(new CommonData("3","年内累积"));
		list.add(new CommonData("4","无条件累积"));
		list.add(new CommonData("5","季度内同次累积"));
		list.add(new CommonData("6","年内同次累积"));
		list.add(new CommonData("7","同次累积"));
		list.add(new CommonData("8","小于本次的月内累积"));
		return list;
	}
	
	private ArrayList getfieldSetList(String fmode)
	{
		ArrayList setidList=new ArrayList();
		try
		{
			 CommonData noItem = new CommonData("", "");
			 setidList.add(noItem);
			  ArrayList list = this.userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
			    for (int i = 0; i < list.size(); i++)
			    {
				FieldSet fieldset = (FieldSet) list.get(i);

				if ("0".equalsIgnoreCase(fieldset.getUseflag()))
				    continue;
				if ("B00".equalsIgnoreCase(fieldset.getFieldsetid()))
				    continue;
				if ("B01".equalsIgnoreCase(fieldset.getFieldsetid()))
				    continue;
				if("0".equals(fmode)){
				if (!"1".equalsIgnoreCase(fieldset.getChangeflag()))
				    continue;
				}
				ArrayList checklist = this.userView.getPrivFieldList(fieldset.getFieldsetid(), Constant.USED_FIELD_SET);
				if (checklist.size() < 1)
				    continue;

				String fieldsetid = fieldset.getFieldsetid();
				
				    CommonData temp = new CommonData(fieldset.getFieldsetid(),fieldset.getFieldsetid()+":"+ fieldset.getCustomdesc());
				    setidList.add(temp);
			   
			    }
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return setidList;
	}
	
	private ArrayList getfieldItemList(String itemSet)
	{
		ArrayList list=new ArrayList();
		ArrayList templist=new ArrayList();
		ArrayList templist2=new ArrayList();
		try
		{
			
			list.add(new CommonData("",""));
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select itemid,itemdesc from fielditem where fieldsetid='"+itemSet+"' and useflag='1' order by itemid");
			boolean isB0110=false;
			boolean isE0122=false;
			boolean isE01A1=false;
			
			while(this.frowset.next())
			{
				if("A01".equalsIgnoreCase(itemSet))
				{
					if("B0110".equals(this.frowset.getString(1)))
						isB0110=true;
					if("E0122".equals(this.frowset.getString(1)))
						isE0122=true;
					if("E01A1".equals(this.frowset.getString(1)))
						isE01A1=true;
				}
				templist.add(new CommonData(this.frowset.getString(1),this.frowset.getString(1)+":"+this.frowset.getString(2)));
			}
			
			if("A01".equalsIgnoreCase(itemSet))
			{
				if(!isB0110)
					templist2.add(new CommonData("B0110","B0110"+":"+ResourceFactory.getProperty("column.sys.org")));
				if(!isE0122)
					templist2.add(new CommonData("E0122","E0122"+":"+ResourceFactory.getProperty("hrms.e0122")));
				if(!isE01A1)
					templist2.add(new CommonData("E01A1","E01A1"+":"+ResourceFactory.getProperty("column.sys.pos")));
				
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if("A01".equalsIgnoreCase(itemSet))
			list.addAll(templist2);
		list.addAll(templist);
		return list;
	}

	private ArrayList getfieldSetListA()
	{
		ArrayList list=new ArrayList();
		try
		{
			list.add(new CommonData("",""));
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select fieldsetid,customdesc from fieldset where useflag='1' order by fieldsetid");
			while(this.frowset.next())
			{
				list.add(new CommonData(this.frowset.getString(1),this.frowset.getString(1)+":"+this.frowset.getString(2)));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	private ArrayList getcodeItemList(String itemid)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			FieldItem item=DataDictionary.getFieldItem(itemid.toLowerCase());
			String codesetid=item.getCodesetid();
			if(!"0".equals(codesetid))
			{
				list.add(new CommonData("",""));
				String sql="";
				if("UN".equals(codesetid)|| "UM".equals(codesetid)|| "@K".equals(codesetid))
				{
					sql="select codeitemid,codeitemdesc from organization where codesetid='"+codesetid+"'";
				}
				else
				{
					sql="select codeitemid,codeitemdesc from codeitem where codesetid='"+codesetid+"'";
				}
				this.frowset=dao.search(sql);
				while(this.frowset.next())
				{
					list.add(new CommonData(this.frowset.getString(1),this.frowset.getString(2)));
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
}
