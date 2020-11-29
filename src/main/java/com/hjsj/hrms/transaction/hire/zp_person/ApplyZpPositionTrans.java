/*
 * Created on 2005-11-22
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_person;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template or this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ApplyZpPositionTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String loginFlag = "0";
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String  edition=(String)hm.get("edition");
		hm.remove("edition");
		String	zp_pos_id = (String)hm.get("zp_pos_id_value");
        if(this.userView !=null && this.userView.getUserId() != null &&  this.userView.getUserId().length()>0 && !"".equals(this.userView.getUserId()) && !"su".equals(this.userView.getUserId())){
        	loginFlag = "1";
        	PreparedStatement pstmt=null;
        	try{
        	   String ssql = "select a0100 from zp_pos_tache where a0100 = '"+this.userView.getUserId()+"' and zp_pos_id='" + zp_pos_id + "'";
        	   this.frowset = dao.search(ssql);
        	   while(this.frowset.next()){
       	   	      loginFlag="2";
        	   	  this.getFormHM().put("loginflag",loginFlag);       
        	   	  return;
        	   }
        	   String sql ="";
        	   if(edition==null)
        		   sql="insert into zp_pos_tache (a0100,zp_pos_id,tache_id,thenumber,apply_date,status) values (?,?,1,1,?,0)";
        	   else
        	   {
        		    this.frowset=dao.search("select * from constant where constant='ZP_DBNAME'");
	   				String dbname="";
	   				if(this.frowset.next())
	   				{
	   					dbname=this.frowset.getString("str_value");
	   				}
	   				dao.delete("delete from zp_pos_tache where a0100 in(select "+dbname+"a01.a0100 from "+dbname+"a01 where a0100='"+this.userView.getUserId()+"' and state='33')",new ArrayList());
	   				dao.update("update "+dbname+"A01 set state='10' where a0100='"+this.userView.getUserId()+"'");
	   				dao.delete("delete from z05 where  a0100='"+this.userView.getUserId()+"'",new ArrayList());
	   				dao.delete("delete from zp_test_template where  a0100='"+this.userView.getUserId()+"'",new ArrayList());
	   				sql="insert into zp_pos_tache (a0100,zp_pos_id,thenumber,apply_date,status) values (?,?,1,?,0)";
        	   }
        	   ArrayList values = new ArrayList();
        	   values.add(this.userView.getUserId());
        	   values.add(zp_pos_id);			   
        	   values.add(DateUtils.getSqlDate(new Date()));
        	   dao.insert(sql, values);
        	   /*pstmt=this.getFrameconn().prepareStatement(sql);
			   pstmt.setString(1,this.userView.getUserId());
			   pstmt.setString(2,zp_pos_id);			   
			   pstmt.setDate(3,DateUtils.getSqlDate(new Date()));
			   pstmt.executeUpdate();*/	
			   
			   //职位申请表 z03 里的简历数量加1
			   if(edition!=null)
			   {
				   this.frowset=dao.search("select z0323 from z03 where z0301='"+zp_pos_id+"'");
				   if(this.frowset.next())
				   {
					   int num=0;
					   if(this.frowset.getString("z0323")!=null)
					   {
						   num=this.frowset.getInt("z0323");
					   }
					   num++;
					   dao.update("update z03 set z0323="+num+" where z0301='"+zp_pos_id+"'");
				   }
				   
			   }
			   
			   //第2版不需要以下步骤
			   if(edition==null)
			   {
					RecordVo rv= ConstantParamter.getRealConstantVo("ZP_DBNAME");
					String dbpre = "";
			        if(rv!=null)
			        {
			            dbpre=rv.getString("str_value");
			            if(dbpre==null || dbpre!=null &&dbpre.length()==0)
			            	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.zp_exam.notsetdbname"),"",""));
			        }else
			        {
			        	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.zp_exam.notsetdbname"),"",""));
			        }
			        StringBuffer strsql=new StringBuffer();
			        //strsql.append("INSERT INTO zp_exam_report(a0100) select a0100 from ");
				    //strsql.append(dbpre);
				    //strsql.append("A01 where (a0100 not in (select a0100 from zp_exam_report))"); 
				    if(this.userView.getUserId()!=null && !"su".equals(this.userView.getUserId()))
				    {
				    	this.frowset=dao.search("select a0100 from zp_exam_report");
				    	if(!this.frowset.next())
				    	{
					        strsql.append("INSERT INTO zp_exam_report(a0100) values('");
						    strsql.append(this.userView.getUserId());
						    strsql.append("')");		    
						    cat.debug("SynchronizationDataExamTrans" + strsql);
						    dao.update(strsql.toString(),new ArrayList());
					    }
				    }
			   }
        	}catch(Exception e){
            	e.printStackTrace();
            	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("label.zp_person.applysuccess"),"",""));
            }finally{  	

    			try
    			{
    				if(pstmt!=null)
    					pstmt.close();
    			}
    			catch(SQLException ee)
    			{
    				ee.printStackTrace();
    			}
    		}
        }
        this.getFormHM().put("loginflag",loginFlag);
 	}
}
