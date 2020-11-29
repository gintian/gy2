package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.team.BaseClassShift;
import com.hjsj.hrms.businessobject.kq.team.KqClassArrayConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 选择基本班次排列
 * <p>Title:BaseKqShiftClass.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Nov 2, 2006 11:13:38 AM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class BaseKqShiftClassTrans extends IBusiness{

	public void execute() throws GeneralException
	{
	   String a_code=(String)this.getFormHM().get("a_code");
	   String nbase=(String)this.getFormHM().get("nbase");
	   String start_date=(String)this.getFormHM().get("start_date");
	   String end_date=(String)this.getFormHM().get("end_date");
	   String selected_class=(String)this.getFormHM().get("selected_class");
	   String rest_postpone=(String)this.getFormHM().get("rest_postpone");
	   String feast_postpone=(String)this.getFormHM().get("feast_postpone");
	   if(selected_class==null||selected_class.length()<=0)
		   return;	  
	   
	   if("-1".equals(selected_class))
		   selected_class=null;
	   
	   String codesetid="";
	   String codeitemid="";
	   String kq_type="02";
	   if(a_code!=null&&!"UN".equalsIgnoreCase(a_code))
	   {
       	    codesetid=a_code.substring(0,2); //UN,UM,@K
    		codeitemid=a_code.substring(2);  //编号
    		
    		if("UN".equalsIgnoreCase(codesetid)
    		        || "UM".equalsIgnoreCase(codesetid)
    		        || "@K".equalsIgnoreCase(codesetid))
    		{
    			orgShift(codesetid,codeitemid,start_date,end_date,selected_class,rest_postpone,feast_postpone,kq_type);
    		}
    		else if("EP".equalsIgnoreCase(codesetid))
    		{
    			shift_employee(nbase,codeitemid,start_date,end_date,selected_class,rest_postpone,feast_postpone,kq_type);
    		}
    		else if("GP".equalsIgnoreCase(codesetid))
    		{
    			if(codeitemid==null||codeitemid.length()<=0)
    				throw GeneralExceptionHandler.Handle(new GeneralException("","请选择具体班组！","",""));
    			shift_group(nbase,codeitemid,start_date,end_date,selected_class,rest_postpone,feast_postpone,kq_type);
    		}
	   }else
	   {
		   ManagePrivCode managePrivCode =new ManagePrivCode(this.userView,this.getFrameconn());
		   codeitemid=managePrivCode.getPrivOrgId();
		   codesetid="UN";
		   if(codeitemid!=null&&codeitemid.length()>0)
		   {
			   orgShift(codesetid,codeitemid,start_date,end_date,selected_class,rest_postpone,feast_postpone,kq_type);
		   }else
		   {
			   for(int i=0;i<userView.getPrivDbList().size();i++)
			   {
				  String userbase= userView.getPrivDbList().get(i).toString();
			      String whereIN=RegisterInitInfoData.getWhereINSql(userView,userbase);			    
				  String whereB0110=RegisterInitInfoData.selcet_OrgId(userbase,"b0110",whereIN);
				  ArrayList orgidb0110List=OrgRegister.getQrgE0122List(this.getFrameconn(),whereB0110,"b0110");
				  for(int t=0;t<orgidb0110List.size();t++)
				  {
					  String b0110_one=orgidb0110List.get(t).toString();								 
					  nbase=RegisterInitInfoData.getOneB0110Dase(this.getFormHM(),this.userView,userbase,b0110_one,this.getFrameconn());
					  if(nbase!=null&&nbase.length()>0)
					  {
						  orgShift(codesetid,b0110_one,start_date,end_date,selected_class,rest_postpone,feast_postpone,kq_type);
					  }
				  }
			   }
		   }
	   }	   
	}

   private void shift_group(String codesetid,String codeitemid,String start_date,String end_date,String class_id,String rest_postpone,String feast_postpone,String kq_type) throws GeneralException
   {
	   ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
	   String b0110=managePrivCode.getPrivOrgId();  
	   BaseClassShift baseClassShift=new BaseClassShift(this.userView,this.getFrameconn());
   	   String t_table=baseClassShift.tempClassTable();
   	   String date_Table=baseClassShift.creat_KqTmp_Table(this.userView.getUserId());
   	   ArrayList date_list=baseClassShift.getDatelist(start_date,end_date);  
       ArrayList restList=IfRestDate.search_RestOfWeek(b0110,userView,this.getFrameconn());
	   String rest_date=restList.get(0).toString();
	   String rest_b0110=restList.get(1).toString();	
	   baseClassShift.initializtion_date_Table(date_list,rest_date,date_Table,rest_b0110,codeitemid);
	   //以下代码注释原因：首钢 防止把其它人员的数据改错，班组下的人员要排班的话，使用“同步班组排班”功能菜单
//	   String group_syn=(String)this.getFormHM().get("group_syn");
//	   if(group_syn!=null&&group_syn.equalsIgnoreCase("1"))
//   	   {
//		   baseClassShift.synchronizationInitGtoupEmployee_Table(codeitemid);//初始化
//		   baseClassShift.insrtGroupTempData(t_table,date_Table,codeitemid);//插入临时表
//		   baseClassShift.insertClassToTemp(class_id,t_table,rest_postpone,feast_postpone);//修改临时表	   
//		   baseClassShift.insertClassToShift(t_table);
//   	   }	  
	    //将排班信息记录到部门排班表
	   getka_org_dept_shift("@G",codeitemid,start_date,end_date,class_id,date_Table,t_table,rest_postpone,feast_postpone);
	   baseClassShift.deleteTable(t_table);	   
   	   
   }
    /**
	 * 组织机构排班
	 * @param codesetid
	 * @param codeitemid
	 * @param start_date
	 * @param end_date
	 * @param class_id
	 * @param rest_postpone
	 * @param feast_postpone
	 */
    private void orgShift(String codesetid,String codeitemid,String start_date,String end_date,
            String class_id,String rest_postpone,String feast_postpone,String kq_type)
    throws GeneralException
    {
        String org_str = "";   
        String kind = "";
        if("UN".equalsIgnoreCase(codesetid)){
            org_str = "b0110";
        }
        else if("UM".equalsIgnoreCase(codesetid)){
            org_str = "e0122";
            kind = "1";
        }
        else if("@K".equalsIgnoreCase(codesetid)){
            org_str = "e01a1";
            kind = "0";
        }
        
    	start_date = start_date.replaceAll("-","\\.");
    	end_date = end_date.replaceAll("-","\\.");
    	
    	BaseClassShift baseClassShift = new BaseClassShift(this.userView,this.getFrameconn());
    	String t_table = baseClassShift.tempClassTable();
    	String date_Table = baseClassShift.creat_KqTmp_Table(this.userView.getUserId());
    	ArrayList date_list = baseClassShift.getDatelist(start_date,end_date);    	
    	ArrayList org_list = baseClassShift.getOrgid_listFrom(codeitemid, codesetid);
    	
    	String sWhere = "";
    	String b0110 = RegisterInitInfoData.getDbB0100(codeitemid, kind, this.getFormHM(),this.userView,this.getFrameconn());
    	ArrayList restList = IfRestDate.search_RestOfWeek(b0110,userView,this.getFrameconn());
		String rest_date = restList.get(0).toString();
		String rest_b0110 = restList.get(1).toString();		
		
    	for(int i=0;i<org_list.size();i++)
    	{
    		String org_id=org_list.get(i).toString();
    		/***********建立日期************/
    		sWhere="and "+org_str+"='"+org_id+"' and  DT.orgid='"+org_id+"'";
    		baseClassShift.initializtion_date_Table(date_list,rest_date,date_Table,rest_b0110,org_id);			
    		ArrayList nbaselist=RegisterInitInfoData.getB0110Dase(this.getFormHM(),this.userView,this.getFrameconn(),org_id);
    		for(int j=0;j<nbaselist.size();j++)
    		{
    			String nbase=nbaselist.get(j).toString();
    			String whereOrg = KqClassArrayConstant.kq_employ_shift_table+"."+org_str+"='"+org_id+"'";
    			String whereD = KqClassArrayConstant.kq_employ_shift_table+".q03z0>='"+start_date+"' and "+KqClassArrayConstant.kq_employ_shift_table+".q03z0<='"+end_date+"'";
    			String whereS = nbase+"A01."+org_str+"='"+org_id+"'";
    			String whereIN = RegisterInitInfoData.getWhereINSql(this.userView,nbase);
    			
    			//同步需排班人员组织机构等信息
    			baseClassShift.synchronizationInitEmployee_Table(nbase,whereIN,whereD,whereS);
    			
    			//插入排班数据到临时表
    			baseClassShift.insrtTempData(t_table,date_Table,nbase,whereIN,sWhere);
    			
    			//修改临时表班次
    			baseClassShift.insertClassToTemp(class_id,t_table,rest_postpone,feast_postpone);
    			
    			whereS=t_table+"."+org_str+"='"+org_id+"'";
    			whereD = whereOrg + " AND " + whereD;
    			baseClassShift.insertClassToShift(t_table,whereIN,nbase,whereD,whereS);
    			
    			baseClassShift.deleteTable(t_table);
    		}
    	}
    	getka_org_dept_shift(codesetid,codeitemid,start_date,end_date,class_id,date_Table,t_table,rest_postpone,feast_postpone);
    	baseClassShift.dropTable(t_table);
    	baseClassShift.dropTable(date_Table); 
    }

    /**
     * 将排班信息记录到部门排班表
     * @param codesetid :UN,UM,@K,@G
     * @param codeitemid :部门编号
     * @param start_date ：开始时间
     * @param end_date ：结束时间
     * @param selected_class ：排班信息
     * @param date_Table 时间表
     * @throws GeneralException
     */
    private void getka_org_dept_shift(String codesetid,String codeitemid,String start_date,String end_date,String selected_class,String date_Table,String t_table,String rest_postpone,String feast_postpone)throws GeneralException
    {
    	insrtOrg_DeptTempData(t_table,date_Table,codesetid,codeitemid);
    	insertClassToTemp(selected_class,t_table,rest_postpone,feast_postpone);
    	StringBuffer deleteSQL =new StringBuffer();
    	StringBuffer insertSQL = new StringBuffer();
    	ArrayList deletelist= new ArrayList();

		deleteSQL.append("delete from kq_org_dept_shift where kq_org_dept_shift.q03z0 in ");
    	deleteSQL.append("(select "+date_Table+".Sdate from "+date_Table+")");
    	deleteSQL.append(" and kq_org_dept_shift.org_dept_id='"+codeitemid+"' and kq_org_dept_shift.codesetid='"+codesetid+"'");

    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	try
    	{
    		dao.delete(deleteSQL.toString(), deletelist);
    		insertSQL.append("INSERT INTO kq_org_dept_shift(org_dept_id,q03z0,class_id,codesetid) ");
    		insertSQL.append("SELECT a0100,q03z0,class_id,'"+codesetid+"' FROM "+t_table+"");
    		ArrayList list=new ArrayList();
    		dao.insert(insertSQL.toString(),list); 
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
   
   private void shift_employee(String nbase,String codeitemid,String start_date,String end_date,String class_id,String rest_postpone,String feast_postpone,String kq_type)
   throws GeneralException
   {
	   if(nbase==null||nbase.length()<=0)
		   return;
	   BaseClassShift baseClassShift=new BaseClassShift(this.userView,this.getFrameconn());
	   String b0110=baseClassShift.getEMpData(codeitemid,nbase,"b0110");
	   ArrayList restList=IfRestDate.search_RestOfWeek(b0110,userView,this.getFrameconn());
	   String rest_date=restList.get(0).toString();
	   String rest_b0110=restList.get(1).toString();
	   String t_table=baseClassShift.tempClassTable();
   	   String date_Table=baseClassShift.creat_KqTmp_Table(this.userView.getUserId());
   	   ArrayList date_list=baseClassShift.getDatelist(start_date,end_date);    	
       baseClassShift.initializtion_date_Table(date_list,rest_date,date_Table,rest_b0110,b0110);	
       String sWhere="and a0100='"+codeitemid+"'";
       String whereIN=RegisterInitInfoData.getWhereINSql(this.userView,nbase);
       /**解决调转部门人员不能更改班次问题开始 wangy**/
       String pdindex = KqParam.getInstance().getKqDepartment(); //如果考勤参数设置了调换班组走这部
       if(pdindex!=null&&pdindex.length()>0)
       {
    	   String factor="";
    	   if(!userView.isSuper_admin())
    	   {
    		   if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
    		   {
    			   factor+=userView.getManagePrivCodeValue();
    			   whereIN+=" or "+pdindex+" like '"+factor+"%'";
    		   }else{
    			   whereIN+=" or "+pdindex+" like '"+factor+"%'";
    		   }
    	   }
       }
       /**结束**/
       String whereD = KqClassArrayConstant.kq_employ_shift_table+".a0100='"+codeitemid+"' and " + KqClassArrayConstant.kq_employ_shift_table+".q03z0 between '"+start_date.replaceAll("-", "\\.")+"' and '"+end_date.replaceAll("-", "\\.")+"'";
       baseClassShift.insrtTempData(t_table,date_Table,nbase,whereIN,sWhere);//插入临时表
       baseClassShift.insertClassToTemp(class_id,t_table,rest_postpone,feast_postpone);//修改临时表
       baseClassShift.insertClassToShift(t_table,whereIN,nbase,whereD,"");
       //baseClassShift.insertClassToShift(t_table,whereIN);
       baseClassShift.dropTable(t_table);
   	   baseClassShift.dropTable(date_Table); 
   }
   
    /**
	 * 选择了单位,部门,职位
	 * @param t_table
	 */
    private void insrtOrg_DeptTempData(String t_table,String date_Table,String codesetid,String org_dept_id)throws GeneralException
	{
		StringBuffer insertSql=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			 ArrayList list=new ArrayList();
		     dao.delete("delete from "+t_table, new ArrayList());
		     insertSql.append("INSERT INTO "+t_table+"(nbase,A0100,Q03Z0,class_id,flag) ");
			 insertSql.append("SELECT '"+codesetid+"','"+org_dept_id+"', sDate ,0,dkind ");
			 insertSql.append(" FROM "+date_Table+" where orgid='"+org_dept_id+"'");
			 
		     dao.insert(insertSql.toString(),list); 
		}catch(Exception e)
		{
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		}		
	}
	
	/**
	 * 给班次临时表里插入班次
	 * @param class_id
	 * @param t_table
	 * @param rest_postpone
	 * @param feast_postpone
	 */
    private void insertClassToTemp(String class_id,String t_table,String rest_postpone,String feast_postpone)throws GeneralException
	{
	    String update="";
	    if(feast_postpone==null||feast_postpone.length()<=0)
	    	feast_postpone="0";
	    
	    if(rest_postpone==null||rest_postpone.length()<=0)
	    	rest_postpone="0";
	    
		if(class_id!=null&&class_id.length()>0)
	    {
	    	update="update "+t_table+" set class_id='"+class_id+"'";
	    }else
	    {
	    	update="update "+t_table+" set class_id=null";
	    }	
		
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		 try
		 {
			 dao.update(update);
			 if("1".equals(feast_postpone))
			 {
				 update="update "+t_table+" set class_id='0' where flag='3'";
				 dao.update(update);	 
			 }
			 if("1".equals(rest_postpone))
			 {
				 update="update "+t_table+" set class_id='0' where flag='2'";
				 dao.update(update);
			 }
		 }catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
	}
}
