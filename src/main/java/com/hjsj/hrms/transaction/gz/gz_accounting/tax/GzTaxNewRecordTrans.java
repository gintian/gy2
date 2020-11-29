package com.hjsj.hrms.transaction.gz.gz_accounting.tax;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.HashMap;
/**
 * 
 *<p>Title:GzTaxNewRecordTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 1, 2007</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class GzTaxNewRecordTrans extends IBusiness{
	
	private String tax_max_id;
	
	private String declare_tax;
	
	private String tax_date;
	
	private String a_code;
	private String e0122;
	private String b0100;
	
	public void execute() throws GeneralException {
		
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			HashMap hm=this.getFormHM();
			String tablename=(String)hm.get("tablename");  
			cat.debug("table name="+tablename);
			this.a_code=(String)hm.get("a_code");
			int tax_mx_id = this.getTaxMaxId(tablename,dao);
			if(tax_mx_id>0)
			{
				tax_mx_id=tax_mx_id+1;
				this.insert(tax_mx_id+"",dao,tablename);
				this.getCreateTimeDate(tax_mx_id+"",dao,tablename);
				if(this.declare_tax!=null || this.tax_date!=null)
				{
					this.getFormHM().put("declare_tax",this.declare_tax);
					this.getFormHM().put("tax_date",this.tax_date);
					this.getFormHM().put("tax_max_id",this.tax_max_id);
					this.getFormHM().put("b0110",this.b0100);
					this.getFormHM().put("e0122",this.e0122);
				}
			}else
			{
				this.insert("1",dao,tablename);
				this.getCreateTimeDate("1",dao,tablename);
				if(this.declare_tax!=null || this.tax_date!=null)
				{
					this.getFormHM().put("declare_tax",this.declare_tax);
					this.getFormHM().put("tax_date",this.tax_date);
					this.getFormHM().put("tax_max_id",this.tax_max_id);
					this.getFormHM().put("b0110",this.b0100);
					this.getFormHM().put("e0122",this.e0122);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 取最大的主键
	 * @param table
	 * @param dao
	 * @return
	 */
	public int getTaxMaxId(String table,ContentDAO dao)
	{
		RowSet rs;
		StringBuffer sb = new StringBuffer();
		int retint = 0;
		String retstr = "";
		sb.append(" select max(tax_max_id) as tax_max_id from  "+table);
		//System.out.println(sb.toString());
		try
		{
			rs = dao.search(sb.toString());
			if(rs.next())
			{
				retint = rs.getInt("tax_max_id");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return retint;
	}
	
	public void getCreateTimeDate(String maxid,ContentDAO dao,String tablename)
	{
		RowSet rs;
		StringBuffer sb = new StringBuffer();
//		String declare_tax = "";
//		String tax_date = "";
		sb.append(" select tax_max_id,declare_tax,tax_date,b0110,e0122 from "+tablename+" ");
		sb.append(" where tax_max_id="+maxid);
		//System.out.println(sb.toString());
		try
		{
			rs = dao.search(sb.toString());
			if(rs.next())
			{
				this.declare_tax = rs.getDate("declare_tax").toString();
				this.tax_date =  rs.getDate("tax_date").toString();
				this.tax_max_id =  rs.getInt("tax_max_id")+"";
				this.b0100=rs.getString("b0110")==null?"":rs.getString("b0110");
				this.e0122=rs.getString("e0122")==null?"":rs.getString("e0122");
			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	public void insert(String tax_max_id_value,ContentDAO dao,String tablename)
	{
		
		StringBuffer sb = new StringBuffer();
		sb.append("insert into "+tablename+" ");
		sb.append("(tax_max_id,declare_tax,tax_date");
		if(this.a_code!=null)
		{
			String code=this.a_code.substring(0,2);
			String value=this.a_code.substring(2);
			if("UN".equalsIgnoreCase(code))
			{
				if(value!=null&&!"".equals(value))
	    			sb.append(", b0110");
			}
			if("UM".equalsIgnoreCase(code))
			{
				String b0110=getB0110(value,dao);
				sb.append(",b0110,e0122");
			}
		}
		sb.append(")");
		sb.append(" values ");
		sb.append("("+tax_max_id_value+",");
		sb.append(Sql_switcher.sqlNow()+",");
		sb.append(Sql_switcher.sqlNow());
		if(this.a_code!=null)
		{
			String code=this.a_code.substring(0,2);
			String value=this.a_code.substring(2);
			if("UN".equalsIgnoreCase(code))
			{
				if(value!=null&&!"".equals(value))
	    			sb.append(",'"+value+"'");
 			}
			if("UM".equalsIgnoreCase(code))
			{
				String b0110=getB0110(value,dao);
				sb.append(",'"+b0110+"','"+value+"'");
			}
		}
		sb.append(")");
//		System.out.println(sb.toString());
		try
		{

			dao.update(sb.toString());
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public String getB0110(String e0122,ContentDAO dao)
	{
		String b0110="";
		try
		{
			String sql = " select * from organization  where codeitemid='"+e0122+"'";
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				sql="select * from organization where codeitemid='"+rs.getString("parentid")+"'";
				RowSet rs1 = dao.search(sql);
				while(rs1.next())
				{
					String codesetid=rs1.getString("codesetid");
		    		if("UN".equalsIgnoreCase(codesetid))
		    		{
		     			b0110=rs1.getString("codeitemid");
    	    			return b0110;		
		    		}
			    	else
			     	{
			    		return getB0110(rs1.getString("parentid"),dao);
		    		}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return b0110;
	}
}
