package com.hjsj.hrms.businessobject.gz.templateset.tax_table;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class TaxTableSetBo {
	private Connection conn;
	public TaxTableSetBo(Connection conn){
		this.conn=conn;
	}
	/**
	 * 税率表信息列表
	 * @return
	 */
    public ArrayList getTaxTableList(){
    	ArrayList list = new ArrayList();
    	try{
    		String sql="select * from  gz_tax_rate";
    		ContentDAO da= new ContentDAO(this.conn);
    		RowSet rs=null;
    		rs=da.search(sql);
    		while(rs.next()){
    			LazyDynaBean bean= new LazyDynaBean();
    			bean.set("description",rs.getString("description"));
    			bean.set("k_base",rs.getString("k_base"));
    			
    			
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return list;
    }
    /**
     * 计税方式列表
     */
    public ArrayList getTaxTypeList(){
    	ArrayList list = new ArrayList();
    	try{
    		String sql="select codeitemid,codeitemdesc from codeitem where codesetid='46'";
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs = null;
    		rs=dao.search(sql);
    		while(rs.next()){
    			list.add(new CommonData(rs.getString("codeitemid"),rs.getString("codeitemdesc")));
    		}
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return list;
    }
    /**
     * 应纳所得税列表
     */
    public ArrayList getIncomeList(String salaryid){
    	ArrayList list = new ArrayList();
    	try{
    		String sql="select * from salaryset where itemtype='N' and salaryid='"+salaryid+"' order by sortid ";
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs = null;
    		rs=dao.search(sql);
    		list.add(new CommonData("",""));
    		while(rs.next()){
    			list.add(new CommonData(rs.getString("itemid"),rs.getString("itemdesc")));
    		}
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return list;
    }
    /**
     * 删除税率表
     * @param ids
     */
    public void deleteTaxTable(String ids){
    	String sql = "delete from gz_tax_rate where taxid in ("+ids+")";
    	ContentDAO dao = new ContentDAO(this.conn);
    	String detailSql= " delete from gz_taxrate_item where taxid in("+ids+")";
    	try{
    		dao.delete(sql,new ArrayList());
    		dao.delete(detailSql,new ArrayList());
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    }
    /**
     * 重命名税率表
     * @param id
     * @param description
     */
    public void renameTaxTable(String id,String description){
    	String sql = "update gz_tax_rate set description=? where taxid="+id;
    	ArrayList list = new ArrayList();
    	list.add(description);
    	ContentDAO dao = new ContentDAO(this.conn);
    	try{
    		dao.update(sql, list);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    /**
     * 得到税率表基数
     * @param id
     * @return
     */
    
    public String getK_base(String id){
		String k_base="";
		String sql = "select k_base from gz_tax_rate where taxid="+id;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs=null;
		try{
			rs=dao.search(sql);
			while(rs.next()){
				k_base=rs.getString("k_base");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return k_base;
	}
    /**
     * 税率明细表列表
     * @param id
     * @return
     */
    public ArrayList getTaxDetailTableList(String taxid){
    	ArrayList list = new ArrayList();
    	try{
    		StringBuffer buf = new StringBuffer();
			StringBuffer sql= new StringBuffer("select * from  gz_taxrate_item where taxid in(");
			if(taxid.indexOf(",")==-1)
			{
				buf.append(taxid);
				sql.append(buf.toString());
			}else
			{
				String[] arr= taxid.split(",");
				for(int i=0;i<arr.length;i++)
				{
					buf.append(",");
					buf.append(arr[i]);
				}
				sql.append(buf.toString().substring(1));
			}
			sql.append(") order by taxid,taxitem");
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs= null;
    		rs=dao.search(sql.toString());
    		DecimalFormat myformat1 = new DecimalFormat("########.###");//
    		while(rs.next()){
    			LazyDynaBean bean = new LazyDynaBean();
    			bean.set("taxitem",rs.getString("taxitem"));
    			bean.set("ynse_down",getXS(String.valueOf(rs.getFloat("ynse_down")),2));
    			bean.set("ynse_up",getXS(String.valueOf(rs.getFloat("ynse_up")),2));
    			bean.set("sl",rs.getString("sl")==null?"0":myformat1.format(rs.getDouble("sl")));
    			bean.set("sskcs",getXS(String.valueOf(rs.getFloat("sskcs")),2));
    			bean.set("flag",rs.getString("flag"));
    			bean.set("description",rs.getString("description")==null?"":rs.getString("description"));
    			bean.set("kc_base",getXS(String.valueOf(rs.getFloat("kc_base")),2));
    			bean.set("taxid",String.valueOf(rs.getInt("taxid")));
    			list.add(bean);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return list;
    }
    public  ArrayList getFlagList(){
    	ArrayList list = new ArrayList();
    	list.add(new CommonData("1","上限封顶"));
    	list.add(new CommonData("2","下限封顶"));
    	return list;
    }
    /**
     * 取得某一税率表的信息
     * @param taxid
     * @return String
     */
    public String getTaxTableInfo(String taxid){
    	StringBuffer sb = new StringBuffer();
    	try
    	{
    		String sql = "select * from gz_tax_rate where taxid ="+taxid;
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs = null;
    		rs= dao.search(sql);
    		while(rs.next()){
    			if(rs.getString("k_base")!=null&&!(rs.getString("k_base").trim().length()<=0))
    			{
    				 sb.append(rs.getString("k_base"));
    			}
    			else
    			{
    				sb.append("^");
    			}
    			sb.append("#");
    			if(rs.getString("param")!=null&&!(rs.getString("param").trim().length()<=0))
    			{
    				sb.append(rs.getString("param"));
    			}
    			else
    			{
    				sb.append("^");
    			}	  
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return sb.toString();
    }
    /**
     * 将当前税率表另存为另一个税率表
     * @param taxid
     * @param description
     * @param k_p
     */
    
    public void saveAsTax(int taxid,String description,String k_p){
    	try
    	{
    		String k_base=k_p.split("#")[0];
    		StringBuffer sql = new StringBuffer();
    		sql.append(" insert into gz_tax_rate (taxid,description");
    		if(!"^".equals(k_base))
    			sql.append(",k_base");
    		sql.append(") values ");
    		sql.append("(");
    		sql.append(taxid);
    		sql.append(",'");
    		sql.append(description);
    		sql.append("'");
    		if(!"^".equals(k_base))
    		         sql.append(","+k_base);
    		sql.append(")");
    		ContentDAO dao = new ContentDAO(this.conn);
    		dao.insert(sql.toString(),new ArrayList());
    		sql.setLength(0);
    		sql.append("update gz_tax_rate set param=");
    		if("^".equals(k_p.split("#")[1]))
    			sql.append("''");
    		else
    			sql.append("'"+k_p.split("#")[1]+"'");
    		sql.append(" where taxid="+taxid);
    		String s=sql.toString();
    		dao.update(sql.toString());
    		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
	}
    public void saveAsTaxDetail(int taxitem,int taxid,String values)
    {
    	try
    	{
    		StringBuffer sql = new StringBuffer();
    		sql.append(" insert into gz_taxrate_item (taxitem,taxid,ynse_down,ynse_up,sl,sskcs,flag,description,kc_base) values (");
    		sql.append(taxitem+",");
    		sql.append(taxid+",");
    		sql.append(values);
    		sql.append(")");
    		ContentDAO dao = new ContentDAO(this.conn);
    		dao.insert(sql.toString(),new ArrayList());
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    public String getXS(String str,int scale){
    	if(str==null|| "null".equalsIgnoreCase(str)|| "".equals(str))
    		str="0.00";
    	BigDecimal m=new BigDecimal(str);
    	BigDecimal one = new BigDecimal("1");
    	return m.divide(one, scale, BigDecimal.ROUND_HALF_UP).toString();
    }
    
    public int getTaxId(String tableName,String idColumnName)
    {
    	int i=0;
    	try
    	{
    		StringBuffer sql = new StringBuffer();
    		sql.append("select max(");
    		sql.append(idColumnName);
    		sql.append(") id  from ");
    		sql.append(tableName);
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs = null;
    		rs=dao.search(sql.toString());
    		while(rs.next())
    		{
    			
    			i=rs.getInt("id");
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return i==0?1:i+1;
 
    }
    //导入导出EXCEL的方法
    /**
     * 设置税率表的excel表头
     */
    public HashMap getTaxExcelHead()
    {
    	HashMap map = new HashMap();
    	try
    	{
    		map.put("taxid",ResourceFactory.getProperty("gz.columns.type"));
    		map.put("description",ResourceFactory.getProperty("gz.columns.taxname"));
    		map.put("k_base",ResourceFactory.getProperty("gz.columns.basedata"));
    		map.put("param",ResourceFactory.getProperty("gz.columns.taxmode"));
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return map;
    }
    /**
     * 设置税率明细表的excel表头
     * @return
     */
    public HashMap getTaxDetailExcelHead()
    {
    	HashMap map = new HashMap();
    	try
    	{
    		map.put("taxid",ResourceFactory.getProperty("gz.columns.type"));
    		map.put("taxitem",ResourceFactory.getProperty("gz.columns.slbh"));
    		map.put("ynse_down",ResourceFactory.getProperty("gz.columns.ynsd_dowm"));
    		map.put("ynse_up",ResourceFactory.getProperty("gz.columns.ynsd_up"));
    		map.put("sl",ResourceFactory.getProperty("gz.columns.sl"));
    		map.put("sskcs",ResourceFactory.getProperty("gz.columns.sskcs"));
    		map.put("flag",ResourceFactory.getProperty("gz.columns.taxflag"));
    		map.put("description",ResourceFactory.getProperty("label.description"));
    		map.put("kc_base",ResourceFactory.getProperty("gz.columns.kc_base"));
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return map;
    }

}
