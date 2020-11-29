package com.hjsj.hrms.transaction.gz.templateset.tax_table;

import com.hjsj.hrms.businessobject.gz.templateset.tax_table.TaxTableSetBo;
import com.hjsj.hrms.businessobject.gz.templateset.tax_table.TaxTableXMLBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class InitTaxTableTrans extends IBusiness {

	public void execute() throws GeneralException {
	   try{
		  ArrayList taxList = new ArrayList();
		  taxList=getTaxTableList();
		  HashMap pamaHm=(HashMap)this.getFormHM().get("requestPamaHM");
		  String returnflag=(String)pamaHm.get("returnflag"); 
		  this.getFormHM().put("returnflag",returnflag);
		  this.getFormHM().put("taxList",taxList);
		  this.getFormHM().put("size",String.valueOf(taxList.size()));
		  ContentDAO dao= new ContentDAO(this.getFrameconn());
		  StringBuffer buf=new StringBuffer("select codesetid from codeitem where codesetid='46' and codeitemid='5'");
		  this.frowset=dao.search(buf.toString());
		  if(!this.frowset.next())
		  {
			  buf.setLength(0);	
			  buf.append("insert into  codeItem  (codesetid, codeitemid, codeitemdesc, parentid, childid, flag,invalid,layer,start_date,end_date) values ('46', '5', '综合所得', '5', '5', 1,1,1,");
			  buf.append(Sql_switcher.dateValue("1949-10-01"));
			  buf.append(",");
			  buf.append(Sql_switcher.dateValue("9999-12-31"));
			  buf.append(")");
			  dao.update(buf.toString());
			  
			  CodeItem item = new CodeItem();
		      item.setCodeid("46");
		      item.setCodeitem("5");
		      item.setCodename("综合所得");
		      item.setPcodeitem("5");
		      item.setCcodeitem("5"); 
			  AdminCode.addCodeItem(item);
		  }
		  
		  
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		
	}
	
	/**
	 * 税率表信息列表
	 * @return
	 */
    public ArrayList getTaxTableList(){
    	ArrayList list = new ArrayList();
    	TaxTableSetBo setBo = new TaxTableSetBo(this.getFrameconn());
    	TaxTableXMLBo bo=new TaxTableXMLBo(this.getFrameconn());
    	HashMap map=bo.getAllValues(getTaxid());
    	try{
    		String sql="select * from  gz_tax_rate";
    		ContentDAO da= new ContentDAO(this.getFrameconn());
    		this.frowset=da.search(sql);
    		while(this.frowset.next()){
    			LazyDynaBean bean= new LazyDynaBean();
    			bean.set("description",this.frowset.getString("description"));
    		    bean.set("k_base",setBo.getXS(String.valueOf(this.frowset.getFloat("k_base")),2));
    			if(map !=null && map.size()!=0)
    			     bean.set("param",AdminCode.getCodeName("46",(String)map.get(this.frowset.getString("taxid"))));
    			else
    				bean.set("param","");
    			bean.set("taxid",this.frowset.getString("taxid"));
    			list.add(bean);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return list;
    }
    public String getTaxid(){
    	StringBuffer sb=new StringBuffer();
    	try{
    		ContentDAO dao = new ContentDAO(this.getFrameconn());
    		String sql = "select taxid from gz_tax_rate";
    		this.frowset=dao.search(sql);
    		while(this.frowset.next()){
    			sb.append(",");
    			sb.append(this.frowset.getString("taxid"));
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	if(sb.toString().trim().length()>0)
    		return sb.toString().substring(1);
    	else
    		return "";
    }

}
