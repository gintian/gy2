package com.hjsj.hrms.transaction.kq.app_check_in.redeploy_rest;

import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * 审批驳回
 * <p>Title:ChangeAppRedeployTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Mar 23, 2007 8:57:18 AM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class ChangeAppRedeployTrans extends IBusiness {
    private String table_name="q25";
	public void execute() throws GeneralException
	{
		// TODO Auto-generated method stub
		ArrayList infolist=(ArrayList)this.getFormHM().get("infolist");
		if(infolist==null||infolist.size()==0)
			  return;
		String audit_flag=(String)this.getFormHM().get("audit_flag");
		if(audit_flag==null||audit_flag.length()<=0)
	          return;
		String radio=(String)this.getFormHM().get("radio");
		if(radio==null||radio.length()<=0)
		{
			radio="";
		}
		String result=(String)this.getFormHM().get("result");	
		SearchAllApp searchAllApp=new SearchAllApp(this.getFrameconn(),this.userView);
		if("1".equals(audit_flag))
		{
			/**审批**/
			searchAllApp.upData_audit(this.table_name,radio,result,infolist);
			if("01".equals(radio))//审批意见为同意
			{
				up_kq_employ_shift(this.table_name,infolist);
			}			
		}else if("2".equals(audit_flag))
		{
			/**驳回**/
			searchAllApp.upData_overrule(this.table_name,result,infolist);
		}
	}
	public void up_kq_employ_shift(String table,ArrayList  infolist)throws GeneralException
	{
		ArrayList up_list=new ArrayList();		
		StringBuffer sql=new StringBuffer();
		sql.append("update kq_employ_shift set");
		sql.append(" class_id=? ");
		sql.append(" where a0100=? and nbase=? and q03z0=?");
		 for(int i=0;i<infolist.size();i++)
    	 {
			  ArrayList cur_list=new ArrayList();
			  ArrayList ex_list=new ArrayList();
    	      LazyDynaBean rec=(LazyDynaBean)infolist.get(i); 
    	      String q19z7=getFieldValue(table,table+"z7",rec.get(table+"01").toString());
    	      cur_list.add("0");    	      
    	      cur_list.add(rec.get("a0100").toString());
    	      cur_list.add(rec.get("nbase").toString());
    	      cur_list.add(rec.get(table+"z1").toString());
    	      up_list.add(cur_list);    	      
    	      ex_list.add(q19z7);
    	      ex_list.add(rec.get("a0100").toString());
    	      ex_list.add(rec.get("nbase").toString());
    	      ex_list.add(rec.get(table+"z3").toString());
    	      up_list.add(ex_list);    	     
    	 }
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		 try
		 {
			 dao.batchUpdate(sql.toString(),up_list);
		 }catch(Exception e)
		 {
			 e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		 }
	}	
	public String getFieldValue(String table,String field,String id)
	{
		StringBuffer sql=new StringBuffer();
		sql.append("select "+field+" from "+table);
		sql.append(" where "+table+"01='"+id+"'");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String value="";
		try
		{
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
			{
				value=this.frowset.getString(field);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return value;
	}
}

