package com.hjsj.hrms.transaction.gz.templateset.salaryItem;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;

public class GetFieldSetOrItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String opt=(String)this.getFormHM().get("opt");   // 1: fieldset   2:fielditem
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			ArrayList list=new ArrayList();
			if("1".equals(opt))
			{
				list.add(new CommonData("",""));

				this.frowset=dao.search("select fieldsetid,customdesc from fieldset where fieldsetid like 'A%' and useflag='1' order by displayorder ");
				while(this.frowset.next())
				{
					if(!"0".equals(this.userView.analyseTablePriv(this.frowset.getString(1))))
						list.add(new CommonData(this.frowset.getString(1),this.frowset.getString(2)));
				}
				this.frowset=dao.search("select fieldsetid,customdesc from fieldset where fieldsetid like 'B%' and useflag='1' order by displayorder ");
				while(this.frowset.next())
				{
					if(!"0".equals(this.userView.analyseTablePriv(this.frowset.getString(1))))
						list.add(new CommonData(this.frowset.getString(1),this.frowset.getString(2)));
				}
				this.frowset=dao.search("select fieldsetid,customdesc from fieldset where fieldsetid like 'K%' and useflag='1' order by displayorder ");
				while(this.frowset.next())
				{
					if(!"0".equals(this.userView.analyseTablePriv(this.frowset.getString(1))))
						list.add(new CommonData(this.frowset.getString(1),this.frowset.getString(2)));
				}
				this.frowset=dao.search("select fieldsetid,customdesc from fieldset where fieldsetid not like 'A%' and fieldsetid not like 'B%' and fieldsetid not like 'K%' and fieldsetid not like 'Y%' and fieldsetid not like 'V%' and fieldsetid not like 'W%' and useflag='1' order by displayorder ");//Y:党组织，V：团组织，W：工会组织 过滤掉，赵旭光 2013-4-15 想过滤基准岗位加个H即可
				while(this.frowset.next())
				{
					if(!"0".equals(this.userView.analyseTablePriv(this.frowset.getString(1))))
						list.add(new CommonData(this.frowset.getString(1),this.frowset.getString(2)));
				}
//				this.frowset=dao.search("select fieldsetid,customdesc from fieldset where useflag='1' and (fieldsetid not like 'A%' and fieldsetid not like 'B%' and fieldsetid not like 'K%') order by displayorder ");
//				while(this.frowset.next())
//				{
//					if(!this.userView.analyseTablePriv(this.frowset.getString(1)).equals("0"))
//						list.add(new CommonData(this.frowset.getString(1),this.frowset.getString(2)));
//				}
			}
			else if("2".equals(opt))
			{
				String feildSetid=(String)this.getFormHM().get("fieldSetid");
				String salaryid=(String)this.getFormHM().get("salaryid");
			    StringBuffer sql=new StringBuffer("select itemid,itemdesc from fielditem where fieldsetid='"+feildSetid+"' ");
			    sql.append(" and itemid not in (select itemid from salaryset where salaryid="+salaryid+")   and useflag='1'");
			    sql.append(" and itemid<>'"+feildSetid+"Z0' and itemid<>'"+feildSetid+"Z1'  order by displayid  ");
				this.frowset=dao.search(sql.toString());
				FieldItem item =DataDictionary.getFieldItem("e01a1");
				boolean addflag=this.hasE01a1Field(salaryid);
				boolean addflag2=true;
				while(this.frowset.next())
				{
					
					if(DataDictionary.getFieldItem(this.frowset.getString(1).toLowerCase())!=null)
					{
						if("e01a1".equalsIgnoreCase(this.frowset.getString(1)))
							addflag2=false;
						//System.out.println(this.userView.analyseFieldPriv(this.frowset.getString(1))+"  "+this.frowset.getString(2));
						if(!"0".equals(this.userView.analyseFieldPriv(this.frowset.getString(1))))
							list.add(new CommonData(this.frowset.getString(1),this.frowset.getString(2).replaceAll("\r\n", "")));
					}
				}
				if("a01".equalsIgnoreCase(feildSetid)&&addflag2)
				{
			    	if(addflag&&item!=null)
			    	{
			    		if(!"0".equals(this.userView.analyseFieldPriv("E01A1")))
			         		list.add(0,new CommonData(item.getItemid(),item.getItemdesc().replaceAll("\r\n", "")));
			    	}
				}
				
			}
			this.getFormHM().put("list",list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	private boolean hasE01a1Field(String salaryid)
	{
		boolean flag=true;
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rs=null;
			rs=dao.search("select * from salaryset where salaryid="+salaryid+" and UPPER(itemid)='E01A1'");
			while(rs.next())
			{
				flag=false;
				break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}

}
