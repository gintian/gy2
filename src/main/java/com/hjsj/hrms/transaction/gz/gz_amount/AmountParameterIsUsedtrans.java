package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GrossPayManagement;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;

public class AmountParameterIsUsedtrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String opt=(String)this.getFormHM().get("opt");
			if("1".equals(opt))
			{
	    		String msg="no";
	    		String zeitemid=(String)this.getFormHM().get("zeitemid");
		    	String seitemid=(String)this.getFormHM().get("seitemid");
		    	String sfitemid=(String)this.getFormHM().get("sfitemid");
		    	String setname=(String)this.getFormHM().get("setname");
		    	if((zeitemid==null|| "".equals(zeitemid.trim()))&&(seitemid==null|| "".equals(seitemid.trim()))&&(sfitemid==null|| "".equals(sfitemid.trim())))
		    	{
		    		msg="yes";
		    	}else
		    	{
	    	    	boolean flag = this.isZero(setname, zeitemid, seitemid, sfitemid);
	    	    	if(!flag)
	    	    	{
	    		    	msg="yes";
	    	    	}
		    	}
		    	this.getFormHM().put("msg",msg);
			}
			else
			{
				String setid=(String)this.getFormHM().get("setid");
				GrossPayManagement gross = new GrossPayManagement(this.getFrameconn());
				ArrayList list=new ArrayList();
				if(setid!=null&&setid.trim().length()>0)
					list=gross.getAmountPlanitemDescFieldList(setid);
				this.getFormHM().put("list", list);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	public boolean isZero(String setname,String itemid,String itemid2,String itemid3)
	{
		boolean flag=true;
		try
		{
			StringBuffer sql =new StringBuffer();
			sql.append("select ");
			if(itemid!=null&&!"".equals(itemid.trim()))
			{
				sql.append("SUM("+itemid+") as "+itemid+",");
			}
			if(itemid2!=null&&!"".equals(itemid2.trim()))
			{
				sql.append(" SUM("+itemid2+") as "+itemid2+" ,");
			}
			if(itemid3!=null&&!"".equals(itemid3.trim()))
			{
				sql.append(" SUM("+itemid3+") as "+itemid3+",");
			}
			sql.setLength(sql.length()-1);
			sql.append(" from "+setname);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql.toString());
			ResultSetMetaData rsmd=this.frowset.getMetaData();
			while(this.frowset.next())
			{
				String item="0";
				if(itemid!=null&&!"".equals(itemid.trim()))
					item=PubFunc.NullToZero(this.frowset.getString(itemid));
				String item2="0";
				if(itemid2!=null&&!"".equals(itemid2.trim()))
					item2=PubFunc.NullToZero(this.frowset.getString(itemid2));
				String item3="0";
				if(itemid3!=null&&!"".equals(itemid3.trim()))
					item3=PubFunc.NullToZero(this.frowset.getString(itemid3));
				if("0".equals(item)&& "0".equals(item2)&& "0".equals(item3))
				{
					flag=false;
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}

}
