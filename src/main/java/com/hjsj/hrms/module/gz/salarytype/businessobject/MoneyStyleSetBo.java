package com.hjsj.hrms.module.gz.salarytype.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
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
	 * @throws GeneralException 
	 */
	public ArrayList getMoneyStyleList() throws GeneralException
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select * from moneystyle order by nstyleid");
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("nstyleid",rs.getString("nstyleid"));
				bean.set("cname",rs.getString("cname")==null|| "null".equals(rs.getString("cname"))?"":rs.getString("cname"));
				bean.set("ctoken",rs.getString("ctoken")==null|| "null".equals(rs.getString("ctoken"))?"":rs.getString("ctoken"));
				bean.set("cunit",rs.getString("cunit")==null|| "null".equals(rs.getString("cunit"))?"":rs.getString("cunit"));
				bean.set("nratio",getXS(rs.getString("nratio"),2));
				list.add(bean);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
		return list;
		
	}
	/**
	 * 根据币种id取得币种信息
	 * @param nstyleid
	 * @return
	 * @throws GeneralException
	 * @author zhanghua
	 * @date 2017年6月5日 下午4:29:33
	 */
	public ArrayList getMoneyStyleList(String nstyleid) throws GeneralException
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select * from moneystyle where nstyleid in ("+nstyleid+") order by nstyleid");
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("nstyleid",rs.getString("nstyleid"));
				bean.set("cname",rs.getString("cname")==null|| "null".equals(rs.getString("cname"))?"":rs.getString("cname"));
				bean.set("ctoken",rs.getString("ctoken")==null|| "null".equals(rs.getString("ctoken"))?"":rs.getString("ctoken"));
				bean.set("cunit",rs.getString("cunit")==null|| "null".equals(rs.getString("cunit"))?"":rs.getString("cunit"));
				bean.set("nratio",getXS(rs.getString("nratio"),2));
				list.add(bean);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
		return list;
		
	}
	/**
	 * 根据货币类别取得货币明细列表
	 * @param nstyleid
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getMoneyStyleDetailList(String nstyleid) throws GeneralException
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select nstyleid,nitemid,cname,nflag from moneyitem where nstyleid in (");
			sql.append(nstyleid+")");
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
				list.add(bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	/**
	 * 删除货币种类同时删除货币明细信息
	 * @param nstyleid
	 * @param tabType
	 * @throws GeneralException 
	 */
	public void deteleMoneyStyleInfo(String nstyleid,int tabType) throws GeneralException{
		StringBuffer sql = new StringBuffer();
		sql.append("delete from ");
		if(tabType==1)
			sql.append(" moneystyle ");
//		if(tabType==2)
//			sql.append(" moneyitem ");
		sql.append("where nstyleid");
		if(nstyleid.indexOf(",")==-1)
		{
			sql.append("='"+nstyleid+"'");
		}
		else
		{
			sql.append(" in (");
			sql.append(nstyleid);
			sql.append(")");
		}
		try
		{
		     ContentDAO dao = new ContentDAO(this.conn);
		     dao.delete(sql.toString(),new ArrayList());//删除列表
		     dao.delete(sql.toString().replace("moneystyle", "moneyitem"),new ArrayList());//删除明细
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	/**
	 * 取得币种明细信息
	 * @param nstyleid
	 * @throws GeneralException 
	 */
	public ArrayList getMoneyStyleDetailInfo(String nstyleid) throws GeneralException{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try
		{
			String sql = "select nstyleid,nitemid,cname,nitemid from moneyitem where nstyleid in ("+nstyleid+")";
			ContentDAO dao = new ContentDAO(this.conn);
			rs=dao.search(sql);
			while(rs.next())
			{
				LazyDynaBean bean= new LazyDynaBean();
				bean.set("nstyleid",nstyleid);
				bean.set("nitemid",String.format("%.2f",rs.getFloat("nitemid")));
				bean.set("cname",rs.getString("cname"));
				bean.set("beforenitemid",rs.getString("nitemid"));
				list.add(bean);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
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
	 * @throws GeneralException 
	  */
     public void deleteMoneyStyleDetail(String itemids,String nstyleid) throws GeneralException{
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
    		 throw GeneralExceptionHandler.Handle(e);
    	 }
     }
     /**
      * 币种id的取值
      * @return
     * @throws GeneralException 
      */
     public int getMoneyStyleId() throws GeneralException
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
    		 throw GeneralExceptionHandler.Handle(e);
    	 }
    	 return n==0?1:n+1;
  
     }
     

     /**
      * 检查币种类别是否处于占用状态
      * @param nstyleid 币种id
      * @return [salaryid,cname]占用薪资类别
      * @throws GeneralException
      * @author zhanghua
      * @date 2017年6月5日 下午3:31:27
      */
     public ArrayList<String []> checkMoneyStyleIsUse(String nstyleid) throws GeneralException{
    	 
    	 ArrayList<String []> list=new ArrayList<String []>();
    	 try{
    		 String strSql=" select salaryid,cname from SALARYTEMPLATE where nmoneyid=? ";
    		 ContentDAO dao = new ContentDAO(this.conn);
    		 ArrayList idList=new ArrayList();
    		 idList.add(nstyleid);
    		 RowSet rs=dao.search(strSql,idList);
    		 while(rs.next()){
    			 String [] strList=new String[2];
    			 strList[0]=rs.getString("salaryid");
    			 strList[1]=rs.getString("cname");
    			 list.add(strList);
    		 }
    		 
    	 }catch(Exception e){
    		 e.printStackTrace();
    		 throw GeneralExceptionHandler.Handle(e);
    	 }
    	 return list;
     }
}
