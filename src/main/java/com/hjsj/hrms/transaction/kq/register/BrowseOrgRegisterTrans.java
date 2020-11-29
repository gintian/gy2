package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;


public class BrowseOrgRegisterTrans extends IBusiness{
    public void execute() throws GeneralException{
    	String kq_duration =RegisterDate.getKqDuration(this.getFrameconn());		
    	ArrayList courselist=RegisterDate.sessionDate(this.frameconn);
    	ArrayList datelist =RegisterDate.getKqDurationList(this.frameconn);
    	String start_date=datelist.get(0).toString();
    	String end_date = datelist.get(datelist.size()-1).toString();
    	ArrayList fielditemlist = DataDictionary.getFieldList("Q03",
				Constant.USED_FIELD_SET);
		ArrayList list= OrgRegister.newFieldItemList(fielditemlist);
		 String kq_period=OrgRegister.getMonthRegisterDate(start_date,end_date); 	
		 String codesetid="UN";
		 if(!userView.isSuper_admin()) 
         {
		 if("UM".equals(RegisterInitInfoData.getKqPrivCode(userView)))
			codesetid="UM";	
         }
		 list=OrgRegister.newFieldItemListQ09(list,codesetid);
		 String code=(String) this.getFormHM().get("code");
		 String b0110=code;
		 if(b0110==null||b0110.length()<=0)
		 {
			 //ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
	 		 b0110=RegisterInitInfoData.getKqPrivCodeValue(this.userView);
		 }
		 /*String kind=(String) this.getFormHM().get("kind");
		 if(kind==null||kind.length()<=0)
		 {
			 kind="2";
		 }		 
		 if(kind.equals("2"))
		 {
			    ArrayList a0100whereIN= new ArrayList();
				for(int i=0;i<userView.getPrivDbList().size();i++)
				{
					String dbase=userView.getPrivDbList().get(i).toString();
					String whereA0100In=RegisterInitInfoData.getWhereINSql(this.userView,dbase);
					a0100whereIN.add(whereA0100In);
				}
					*//************得到部门权限**********//*

				String whereE0122=OrgRegister.selcet_kq_OrgId(start_date,end_date,"e0122",a0100whereIN,b0110);
				ArrayList orgide0122List=OrgRegister.getQrgE0122List(this.frameconn,whereE0122,"e0122");
				whereE0122=OrgRegister.selcet_kq_OrgId(start_date,end_date,"b0110",a0100whereIN,b0110);
				ArrayList orgidb0110List=OrgRegister.getQrgE0122List(this.frameconn,whereE0122,"b0110");
				StringBuffer b0110s=new StringBuffer();
				b0110s.append("'"+b0110+"'");
				for(int i=0;i<orgide0122List.size();i++)
				{
					b0110s.append(",'");
					b0110s.append(orgide0122List.get(i).toString());
					b0110s.append("'");
				}				
				for(int i=0;i<orgidb0110List.size();i++)
				{
					b0110s.append(", '");
					b0110s.append(orgidb0110List.get(i).toString());
					b0110s.append("'");
				}
				b0110=b0110s.toString();
		 }		 */
		  KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn(),this.userView);
			ArrayList kq_dbase_list=kqUtilsClass.getKqPreList();	
			ArrayList a0100whereIN = new ArrayList();
			for(int i=0;i<kq_dbase_list.size();i++)
			{
				String dbase=kq_dbase_list.get(i).toString();
				String whereA0100In=RegisterInitInfoData.getWhereINSql(this.userView,dbase);
				
				a0100whereIN.add(whereA0100In);
			}
		 ArrayList sqllist=OrgRegister.getSumSqlstrLike(list,kq_duration, b0110, "Q09",a0100whereIN);
		 //System.out.println(sqllist.get(0).toString());
		 //System.out.println(sqllist.get(1).toString());
		 
		// 显示部门层数
			Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
			String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if (uplevel == null || uplevel.length() == 0)
				uplevel = "0";
		 // 将导出模板的sql语句保存至服务器
         String kq_sql_unit = sqllist.get(0).toString()+sqllist.get(1).toString()+" order by b0110";
         this.userView.getHm().put("kq_sql_unit",kq_sql_unit);
         
		 this.getFormHM().put("sqlstr", sqllist.get(0).toString());	 
		 this.getFormHM().put("strwhere", sqllist.get(1).toString());		  
		 this.getFormHM().put("columns", sqllist.get(2).toString()); 
		 this.getFormHM().put("orderby"," order by b0110");
		 this.getFormHM().put("kq_duration",kq_duration);
		 this.getFormHM().put("coursedate",start_date);
		 this.getFormHM().put("fielditemlist", list);
		 this.getFormHM().put("courselist", courselist);
		 this.getFormHM().put("datelist",datelist);
		 this.getFormHM().put("code",code);		 
		 this.getFormHM().put("orgsumvali","");
		 this.getFormHM().put("kq_period",kq_period);
		 this.getFormHM().put("start_date",start_date);
		 this.getFormHM().put("end_date",end_date);
		 this.getFormHM().put("uplevel",uplevel);
		 
		 // 高级花名册条件 月汇总条件
		 String strSQLWhere = sqllist.get(1).toString();
		 strSQLWhere = strSQLWhere.substring(" from Q09  where".length());
		 // 涉及SQL注入直接放进userView里
		 this.userView.getHm().put("kq_condition", "9`"+strSQLWhere);
		 this.getFormHM().put("returnURL","/kq/register/daily_registerdata.do?b_query=link");
		 this.getFormHM().put("nprint","9");
    }
   
}
