package com.hjsj.hrms.transaction.general.query.general;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



public class GetGeneralFieldTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			int flag=Integer.parseInt((String)this.getFormHM().get("flag"));  //  1:  子标集  2: 子标  3: 代码  0:累计方式
			
			ArrayList list=new ArrayList();
			
			switch(flag){
				case 0:
					list= getAddUpList();
				    break;
				case 1:
					list= getfieldSetList();
				    break;
				case 2:
					String itemSet=(String)this.getFormHM().get("value");
					list=getfieldItemList(itemSet);
				    break;
				case 3:
					String itemid=(String)this.getFormHM().get("value");
					list=getcodeItemList(itemid);
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
	
	private ArrayList getfieldSetList()
	{
		ArrayList list=new ArrayList();
		try
		{
			list.add(new CommonData("",""));
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			/**工资只要人员，单位，职位信息群（不知道别地方是否调用此处且需要别的信息群，lizw 2011-09-28）*/
			this.frowset=dao.search("select fieldsetid,customdesc from fieldset where useflag='1' and fieldsetid like 'A%' order by displayorder");
			while(this.frowset.next())
			{
				list.add(new CommonData(this.frowset.getString(1),this.frowset.getString(2)));
			}
			this.frowset=dao.search("select fieldsetid,customdesc from fieldset where useflag='1' and fieldsetid like 'B%' order by displayorder");
			while(this.frowset.next())
			{
				list.add(new CommonData(this.frowset.getString(1),this.frowset.getString(2)));
			}
			this.frowset=dao.search("select fieldsetid,customdesc from fieldset where useflag='1' and fieldsetid like 'K%' order by displayorder");
			while(this.frowset.next())
			{
				list.add(new CommonData(this.frowset.getString(1),this.frowset.getString(2)));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
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
			this.frowset=dao.search("select itemid,itemdesc from fielditem where fieldsetid='"+itemSet+"' and useflag='1' order by displayid");
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
				templist.add(new CommonData(this.frowset.getString(1),this.frowset.getString(2)));
			}
			
			if("A01".equalsIgnoreCase(itemSet))
			{
				if(!isB0110)
					templist2.add(new CommonData("B0110",ResourceFactory.getProperty("column.sys.org")));
				if(!isE0122)
					templist2.add(new CommonData("E0122",ResourceFactory.getProperty("hrms.e0122")));
				if(!isE01A1)
					templist2.add(new CommonData("E01A1",ResourceFactory.getProperty("column.sys.pos")));
				
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
					sql="select codeitemid,codeitemdesc from organization where (codesetid='"+codesetid+"'";
				}
				else
				{
					sql="select codeitemid,codeitemdesc from codeitem where (codesetid='"+codesetid+"'";
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String backdate = sdf.format(new Date());
				if("UM".equals(codesetid)){//支持关联部门的指标也可以选择单位
					sql+= " or codesetid ='UN'";
				}
				sql+=") and " + Sql_switcher.dateValue(backdate)
     			+ " between start_date and end_date";
				if ("UN".equalsIgnoreCase(codesetid) || "UM".equalsIgnoreCase(codesetid) || "@K".equalsIgnoreCase(codesetid))
				{
					sql=sql+(" ORDER BY a0000,codeitemid ");
				}else if(!"@@".equalsIgnoreCase(codesetid))
				{
					sql=sql+(" ORDER BY codeitemid ");
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
