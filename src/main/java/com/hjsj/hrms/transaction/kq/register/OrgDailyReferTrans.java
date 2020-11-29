package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class OrgDailyReferTrans extends IBusiness{
    public void execute()throws GeneralException{
    	   	
    	ArrayList datelist = (ArrayList)this.getFormHM().get("datelist");
    	String start_date=datelist.get(0).toString();
    	String end_date=datelist.get(datelist.size()-1).toString();
		String code=(String) this.getFormHM().get("code");
		ArrayList kq_dbase_list=(ArrayList)this.getFormHM().get("kq_dbase_list");
		String whereIN=RegisterInitInfoData.getWhereINSql(this.userView,kq_dbase_list.get(0).toString());
		String b0100=code;
		String codesetid=(String)this.getFormHM().get("codesetid");
		String orgvali="false";
		String kind = (String)this.getFormHM().get("kind");
		if(!userView.isSuper_admin()){				
			b0100=RegisterInitInfoData.getKqPrivCodeValue(userView); 
		}
		//判断是否有该考勤期间的数据		
		//判断员工日考勤是否提交，如果没有提交，则部门日考勤不能提交
		boolean if_emprefer=if_EmpRefer(start_date,end_date,code,whereIN,kind);
		if(if_emprefer){
			//判断该考勤期间部门日汇总是否已经提交
			boolean if_orgrefer=if_OrgRefer(start_date,end_date,b0100,codesetid);
			if(if_orgrefer){
				orgvali=update_Org(start_date,end_date,b0100,codesetid);
			}else{
				throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.collect.nosave"),"",""));
			}
		}else{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.emp.norefer"),"",""));
		}
		this.getFormHM().put("orgvali",orgvali);
    }
    /**
     * 修改审核标志
     * @param start_date, end_date 考勤期间
     * @param org_id 部门代码
     * @param codesetid UM:部门，UN:单位
     * retuen true 修改成功
     * */
    public String  update_Org(String start_date,String end_date,String b0100,String codesetid)throws GeneralException{
    	String orgvali="false";
    	String wheresql=OrgRegister.whereSQL(b0100,start_date,end_date,codesetid);
    	StringBuffer updateSql=new StringBuffer();
    	updateSql.append("update Q07 set ");
    	updateSql.append(" q03z5=? ");
    	updateSql.append(wheresql);
    	ArrayList updatelist=new ArrayList();
    	updatelist.add("02");
    	ArrayList list= new ArrayList();
 	    list.add(updatelist);
 	    ContentDAO dao = new ContentDAO(this.getFrameconn()); 	    
 	    try{ 	    	
 	       dao.batchUpdate(updateSql.toString(),list);
 	      orgvali="true";
 	    }catch(Exception e){
 	    	e.printStackTrace();
 	    	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.refer.lost"),"",""));
 	    }
 	    return orgvali;
 	   
    }   
    /**
     * 判断该操作考勤期间是否已经提交
     * @param coursedate 考勤期间
     * @param org_id 部门代码
     * @param codetype UM:部门，UN:单位
     * @return true 可以提交,false 不可以提交 
     * */
    public boolean if_OrgRefer(String start_date,String end_date,String b0100,String codesetid){
    	boolean isCorrect=false;
    	String wheresql=OrgRegister.whereSQL(b0100,start_date,end_date,codesetid);
  	    StringBuffer sql=new StringBuffer();          
  	    sql.append("select q03z5 from Q07 ");   	     
  	    sql.append(wheresql);
 	    ContentDAO dao = new ContentDAO(this.getFrameconn());
        try{
           this.frowset = dao.search(sql.toString());
           if(this.frowset.next()){
     	       String checkflag= (String)this.frowset.getString("q03z5");
     	       if("01".equals(checkflag)){
     		     isCorrect=true;
     	       }
           }else{
        	  isCorrect=true;  
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
    public boolean if_EmpRefer(String start_date,String end_date,String code,String whereIN,String kind){
    	boolean isCorrect=true;
    	StringBuffer sql=new StringBuffer();
    	sql.append("select q03z5 ");
 	    sql.append(" from Q03");
 	    sql.append(" where 1=1 ");
	    sql.append(OrgRegister.where_Date(start_date,end_date));
 	    if("1".equals(kind))
        {
 	    	sql.append(" and e0122 like '"+code+"%'"); 
        }else
        {
 	      sql.append(" and b0110 like '"+code+"%'");
        } 
 	    sql.append(" and q03z5='01' ");
 	     String orgtype=OrgRegister.getOrgType(this.userView);
 	    if(!userView.isSuper_admin()){
 	    	sql.append("and "+orgtype+" in(select "+orgtype+" "+whereIN+")");
 	    }else{
 	    	sql.append("and a0100 in(select a0100 "+whereIN+")");
 	    }
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
