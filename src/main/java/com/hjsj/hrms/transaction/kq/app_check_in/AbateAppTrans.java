package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
/**
 * 废除操作
 * <p>Title:AbateAppTrans.java</p>
 * <p>Description>:AbateAppTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jul 8, 2010 2:25:01 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class AbateAppTrans extends IBusiness {

	public void execute() throws GeneralException {
		 ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("selectedinfolist");
		 String table=(String)this.getFormHM().get("table");
		 String ta=table.toLowerCase();
		 if(ta==null||ta.length()<=0|| "q15".equalsIgnoreCase(ta))
			 return;
		 ContentDAO dao=new ContentDAO(this.getFrameconn()); 
    	 StringBuffer buf=new StringBuffer();
    	 buf.append("update ");
    	 buf.append(table+" set state='1'");
    	 buf.append(" where ");
    	 buf.append( ta+"01=?");
    	 buf.append(" and "+ ta+"z5='03'");
    	 ArrayList paralist=new ArrayList();
    	 AnnualApply annualApply=new AnnualApply(this.userView,this.getFrameconn()); 
    	 try{
    		 for(int i=0;i<selectedinfolist.size();i++){
    			 LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i); 
    			 // 58769 如果有不是已批的 抛异常
    			 if(!"03".equalsIgnoreCase(rec.get(ta+"z5").toString())) {
    				 throw new GeneralException("所选记录中包含非已批的记录，请重新选择！");
    			 }
    			 ArrayList list=new ArrayList();
    			 list.add(rec.get(ta+"01").toString());
    			 paralist.add(list);         		   
             }
        	 dao.batchUpdate(buf.toString(),paralist);
        	 
    		 ArrayList dblist=this.userView.getPrivDbList();	   
          	 StringBuffer sql=new StringBuffer();
          	 for(int i=0;i<dblist.size();i++)         	
          	 {
          		sql.delete(0, sql.length());
          		String nbase=dblist.get(i).toString();
          		sql.append("select a0100,a0101,nbase,"+table+"z1 z1,"+table+"z3 z3 from "+table+" where nbase='"+nbase+"' and state='1' and "+table+"z5='03'");
          		this.frowset=dao.search(sql.toString());
          		while(this.frowset.next())
          		{
          			 String a0100=this.frowset.getString("a0100");
          			 String a0101=this.frowset.getString("a0101");
          			 Date z1=this.frowset.getTimestamp("z1");
          			 Date z3=this.frowset.getTimestamp("z1");
          			 if(!annualApply.getKqDataState(nbase,a0100,z1,z3))
          				throw GeneralExceptionHandler.Handle(new GeneralException("",a0101+"申请的业务日期包含的日明细数据已经提交，不可再编辑，不能做作废操作，请与考勤管理员联系！","",""));
          		}
          		sql.delete(0, sql.length());
          		String delete_sql="update "+table+" set "+ta+"z5='10' where nbase='"+nbase+"'  and state='1' and "+table+"z5='03'";         		
          		String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);
          		delete_sql=delete_sql+" and a0100 in(select a0100 "+whereIN+")";
          		dao.update(delete_sql);
          	}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
