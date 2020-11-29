package com.hjsj.hrms.businessobject.gz.templateset.moneystyle;

import com.hrms.frame.dao.ContentDAO;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;

public class MoneyStyleSetBo {
	private Connection conn;
	
	public MoneyStyleSetBo(Connection conn){
		this.conn=conn;
	}
	public MoneyStyleSetBo(){
		
	}
	/**
	 * 取得币种信息列表
	 * @return
	 */
	public ArrayList getMoneyStyleList()
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select * from moneystyle order by nstyleid");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("nstyleid",rs.getString("nstyleid"));
				bean.set("cname",rs.getString("cname")==null|| "null".equals(rs.getString("cname"))?"":rs.getString("cname"));
				bean.set("ctoken",rs.getString("ctoken")==null|| "null".equals(rs.getString("ctoken"))?"":rs.getString("ctoken"));
				bean.set("cunit",rs.getString("cunit")==null|| "null".equals(rs.getString("cunit"))?"":rs.getString("cunit"));
				bean.set("nratio",getXS(rs.getString("nratio"),2));
				/*
				 * 状态标识 ，未用
				 * bean.set("cstate",rs.getString("cstate"));
				*/
				list.add(bean);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
		
	}
	/**
	 * 根据货币类别取得货币明细列表
	 * @param nstyleid
	 * @return
	 */
	public ArrayList getMoneyStyleDetailList(String nstyleid)
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select * from moneyitem where nstyleid='");
			sql.append(nstyleid+"'");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("nstyleid",rs.getString("nstyleid"));
				bean.set("nitemid",rs.getString("nitemid"));
				bean.set("cname",rs.getString("cname"));
				bean.set("nflag",rs.getString("nflag"));
				/*
				 * 状态标识 ，未用
				 * bean.set("cstate",rs.getString("cstate"));
				*/
				list.add(bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 删除货币种类同时删除货币明细信息
	 * @param nstyleid
	 * @param tabType
	 */
	public void deteleMoneyStyleInfo(String nstyleid,int tabType){
		StringBuffer sql = new StringBuffer();
		sql.append("delete from ");
		if(tabType==1)
			sql.append(" moneystyle ");
		if(tabType==2)
			sql.append(" moneyitem ");
		sql.append("where nstyleid");
		if(nstyleid.indexOf(",")==-1)
		{
			sql.append("='"+nstyleid+"'");
		}
		else
		{
			sql.append(" in (");
			sql.append(nstyleid.substring(1));
			sql.append(")");
		}
	//	sql.append(nstyleid+"'");
		try
		{
		     ContentDAO dao = new ContentDAO(this.conn);
		     dao.delete(sql.toString(),new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	/**
	 * 取得币种明细信息
	 * @param nstyleid
	 */
	public ArrayList getMoneyStyleDetailInfo(String nstyleid){
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select * from moneyitem where nstyleid='"+nstyleid+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql);
			while(rs.next())
			{
				LazyDynaBean bean= new LazyDynaBean();
				bean.set("nstyleid",nstyleid);
				bean.set("nitemid",String.valueOf(rs.getFloat("nitemid")));
				bean.set("cname",rs.getString("cname"));
				bean.set("nflag",rs.getString("nflag"));
				/*
				 * bean.set("cstate",rs.getString("cstate"));
				 */
				list.add(bean);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 小数点后保留两位
	 * @param str
	 * @param scale
	 * @return
	 */
	 public String getXS(String str,int scale){
	    	if(str==null)
	    		str="0.00";
	    	BigDecimal m=new BigDecimal(str);
	    	BigDecimal one = new BigDecimal("1");
	    	return m.divide(one, scale, BigDecimal.ROUND_HALF_UP).toString();
	    }
	 /**
	  * 删除币种明细信息
	  * @param itemids
	  * @param nstyleid
	  */
     public void deleteMoneyStyleDetail(String itemids,String nstyleid){
    	 try
    	 {
    		 ContentDAO dao = new ContentDAO(this.conn);
    		 StringBuffer sql = new StringBuffer("delete from moneyitem where ");
    		 sql.append("nstyleid='");
    		 sql.append(nstyleid+"'");
    		 if(itemids.indexOf("#")==-1)
    		 {
    			 sql.append(" and nitemid='");
    			 sql.append(itemids+"'");
    		 }
    		 else
    		 {
    			 StringBuffer where_sql = new StringBuffer();
    			 String[] str = itemids.split("#");
    			 for(int i=0;i<str.length;i++)
    			 {
    				 where_sql.append(",'");
    				 where_sql.append(str[i]);
    				 where_sql.append("'");
    			 }
    			 sql.append(" and nitemid in(");
    			 sql.append(where_sql.toString().substring(1));
    			 sql.append(")");
    			 
    		 }
    		 dao.delete(sql.toString(),new ArrayList());
    	 }
    	 catch(Exception e)
    	 {
    		 e.printStackTrace();
    	 }
     }
     /**
      * 币种id的取值
      * @return
      */
     public int getMoneyStyleId()
     {
    	 int n=0;
    	 try
    	 {
    		 String sql ="select max(nstyleid) id from moneystyle";
    		 ContentDAO dao = new ContentDAO(this.conn);
    		 RowSet rs= null;
    		 rs=dao.search(sql);
    		 while(rs.next())
    		 {
    			 n=rs.getInt("id");
    		 }
    		 
    	 }
    	 catch(Exception e)
    	 {
    		 e.printStackTrace();
    	 }
    	 return n==0?1:n+1;
  
     }
}
