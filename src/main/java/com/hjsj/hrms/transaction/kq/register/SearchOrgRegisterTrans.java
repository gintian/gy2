package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SearchOrgRegisterTrans extends IBusiness{
    public void execute() throws GeneralException{
    	String kq_duration=(String)this.getFormHM().get("kq_duration");	
		ArrayList courselist= (ArrayList)this.getFormHM().get("courselist");
		ArrayList datelist=(ArrayList)this.getFormHM().get("datelist");
		String kq_period=(String)this.getFormHM().get("kq_period");
		String coursedate=(String)this.getFormHM().get("coursedate");
		String cur_date=coursedate;
		if(courselist==null||courselist.size()<=0)
		{			
			courselist=RegisterDate.sessionDate(this.frameconn);
		}
		if(kq_duration==null||kq_duration.length()<=0)
		{
			CommonData vo = (CommonData) courselist.get(0);
			cur_date=vo.getDataValue();
		}		
		if(datelist==null||datelist.size()<=0)
		{
			 datelist =RegisterDate.getKqDurationList(this.getFrameconn());
		}
		String start_date=datelist.get(0).toString();
    	String end_date = datelist.get(datelist.size()-1).toString();
    	ArrayList fielditemlist = DataDictionary.getFieldList("Q03",
				Constant.USED_FIELD_SET);
		ArrayList list= OrgRegister.newFieldItemList(fielditemlist);
		 String codesetid="UN";
		 if(!userView.isSuper_admin()) 
         {
		 if("UM".equals(RegisterInitInfoData.getKqPrivCode(userView)))
			codesetid="UM";	
         }
		 list=OrgRegister.newFieldItemListQ09(list,codesetid);
		 if(kq_period==null||kq_period.length()<=0)
		 {
			 kq_period=OrgRegister.getMonthRegisterDate(start_date,end_date);	 
		 }		 
		 
		
		 String code=(String) this.getFormHM().get("code");
		 String kind=(String) this.getFormHM().get("kind");
		 String b0110=code;
		 if(b0110==null||b0110.length()<=0)
		 {
			 b0110=RegisterInitInfoData.getKqPrivCodeValue(userView);
		 }
		 if(kind==null||kind.length()<=0)
		 {
			 kind="2";
		 }		 
		 if("2".equals(kind))
		 {
			    /*ArrayList a0100whereIN= new ArrayList();
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
					b0110s.append(", '");
					b0110s.append(orgide0122List.get(i).toString());
					b0110s.append("'");
				}		
				for(int i=0;i<orgidb0110List.size();i++)
				{
					b0110s.append(", '");
					b0110s.append(orgidb0110List.get(i).toString());
					b0110s.append("'");
				}
				b0110=b0110s.toString();*/
		 }else
		 {
			 b0110=code;
		 }
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
		 this.getFormHM().put("sqlstr", sqllist.get(0).toString());	 
		 this.getFormHM().put("strwhere", sqllist.get(1).toString());		  
		 this.getFormHM().put("columns", sqllist.get(2).toString()); 
		 this.getFormHM().put("orderby"," order by b0110");
		 this.getFormHM().put("kq_duration",kq_duration);
		 this.getFormHM().put("coursedate",coursedate!=null&&coursedate.length()>0?coursedate:cur_date);
		 this.getFormHM().put("fielditemlist", list);
		 this.getFormHM().put("courselist", courselist);		 
		 this.getFormHM().put("code",code);		 
		 this.getFormHM().put("orgsumvali","");
		 this.getFormHM().put("kq_period",kq_period);
		 
		// 高级花名册条件 月汇总条件
		 String strSQLWhere = sqllist.get(1).toString();
		 strSQLWhere = strSQLWhere.substring(" from Q09  where".length());
		 this.getFormHM().put("condition",SafeCode.encode("9`"+strSQLWhere));
		 this.getFormHM().put("returnURL","/kq/register/daily_registerdata.do?b_query=link");
			this.getFormHM().put("nprint","9");
    }
    
}
