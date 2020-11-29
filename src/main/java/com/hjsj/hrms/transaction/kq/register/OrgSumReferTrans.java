package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class OrgSumReferTrans extends IBusiness {
   public void execute()throws GeneralException{
	   String coursedate = (String) this.getFormHM().get("coursedate");
		String code=(String) this.getFormHM().get("code");
		
		String b0110=code;
		String codesetid=(String)this.getFormHM().get("codesetid");
		String orgsumvali="false";
		String kind = (String)this.getFormHM().get("kind");
		if(!userView.isSuper_admin()){				
			b0110=RegisterInitInfoData.getKqPrivCodeValue(userView); 
		}
		ArrayList kq_dbase_list=(ArrayList)this.getFormHM().get("kq_dbase_list");
		String whereIN=RegisterInitInfoData.getWhereINSql(this.userView,kq_dbase_list.get(0).toString());
		boolean if_emsumprefer=if_EmpSumRefer(coursedate,code,kind,whereIN);
		if(if_emsumprefer){
			//判断该考勤期间部门日汇总是否已经提交
			boolean if_orgrefer=if_OrgSumRefer(coursedate,b0110,codesetid);
			if(if_orgrefer){
				orgsumvali=update_OrgSum(coursedate,b0110,codesetid);
			}else{
				throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.collect.nosave"),"",""));
			}
		}else{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.emp.norefer"),"",""));
		}
		this.getFormHM().put("orgsumvali",orgsumvali);
	   
   }
   /**
    * 修改审核标志
    * @param coursedate 考勤期间
    * @param org_id 部门代码
    * @param codesetid UM:部门，UN:单位
    * retuen true 修改成功
    * */
   public String  update_OrgSum(String coursedate,String org_id,String codesetid)throws GeneralException{
   	String orgsumvali="false";
   	String wheresql=OrgRegister.whereSumSQL(org_id,coursedate,codesetid);
   	StringBuffer updateSql=new StringBuffer();
   	updateSql.append("update Q09 set ");
   	updateSql.append(" Q03Z5=? ");
   	updateSql.append(wheresql);
   	ArrayList updatelist=new ArrayList();
   	updatelist.add("02");
   	ArrayList list= new ArrayList();
	    list.add(updatelist);
	    ContentDAO dao = new ContentDAO(this.getFrameconn()); 	    
	    try{ 	    	
	       dao.batchUpdate(updateSql.toString(),list);
	       orgsumvali="true";
	    }catch(Exception e){
	    	e.printStackTrace();
	    	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.refer.lost"),"",""));
	    }
	    return orgsumvali;
	    
   }
   /**
    * 判断该操作考勤期间是否已经提交
    * @param coursedate 考勤期间
    * @param org_id 部门代码
    * @param codetype UM:部门，UN:单位
    * @return true 可以提交,false 不可以提交 
    * */
   public boolean if_OrgSumRefer(String coursedate,String org_id,String codesetid){
   	boolean isCorrect=false;
   	String wheresql=OrgRegister.whereSumSQL(org_id,coursedate,codesetid);
 	    StringBuffer sql=new StringBuffer();          
 	    sql.append("select Q03Z5 from Q09 ");   	     
 	    sql.append(wheresql);
	    ContentDAO dao = new ContentDAO(this.getFrameconn());
       try{
          this.frowset = dao.search(sql.toString());
          if(this.frowset.next()){
    	       String checkflag= (String)this.frowset.getString("Q03Z5");
    	       if("01".equals(checkflag)){
    		     isCorrect=true;
    	       }
          }
       }catch(Exception e){
    	  e.printStackTrace();
       }
   	return isCorrect;
   }
   /**
    * 判断员工日考勤是否提交，如果没有提交，则部门日考勤不能提交
    * @param coursedate  考勤期间
    * @param code 单位编号
    * @param whereIN 查询子句
    * @return  false 不能提交，true 可以提交
    * */
   public boolean if_EmpSumRefer(String coursedate,String code,String kind,String whereIN){
   	boolean isCorrect=true;
   	StringBuffer sql=new StringBuffer();
   	sql.append("select Q03Z5 ");
	    sql.append(" from Q05");
	    sql.append(" where Q03Z0 = '"+coursedate+"'");
	    if("1".equals(kind))
        {
 	    	sql.append(" and e0122 like '"+code+"%'"); 
        }else
        {
 	      sql.append(" and b0110 like '"+code+"%'");
        } 
	    sql.append(" and Q03Z5='01' ");
	    sql.append(" and a0100 in(select a0100 "+whereIN+")");
	    
	   ContentDAO dao = new ContentDAO(this.getFrameconn());
      try{
        this.frowset = dao.search(sql.toString());
        if(this.frowset.next()){
    	    isCorrect=false;
         }
      }catch(Exception e){
    	 e.printStackTrace();
      }
   	return isCorrect;
   }
  
}
