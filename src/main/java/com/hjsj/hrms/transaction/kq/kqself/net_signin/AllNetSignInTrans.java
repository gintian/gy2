package com.hjsj.hrms.transaction.kq.kqself.net_signin;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.kqself.NetSignIn;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * 审阅网上签到
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 2, 2007:3:52:01 AM</p> 
 *@author dengcan
 *@version 4.0
 */
public class AllNetSignInTrans  extends IBusiness{
	
	   
	   public void execute()throws GeneralException
	   {
		   String start_date=(String)this.getFormHM().get("start_date");
		   String end_date=(String)this.getFormHM().get("end_date");
		   String location=(String)this.getFormHM().get("location");
		   String select_flag=(String)this.getFormHM().get("select_flag");
		   String select_name=(String)this.getFormHM().get("select_name");
		   if(select_flag!=null&& "0".equals(select_flag))
		   {
			   location="all";
			   select_name="";
		   }			   
		   this.getFormHM().put("location",location);
		   this.getFormHM().put("select_flag",select_flag);
		   this.getFormHM().put("select_name",select_name);
		   NetSignIn netSignIn=new NetSignIn(this.userView,this.getFrameconn());
		   if(start_date==null||start_date.length()<=0)		  
			   start_date=netSignIn.getWork_date();	
		   else
			   start_date=start_date.replaceAll("-","\\.");
		   if(end_date==null||end_date.length()<=0)		   
			   end_date=netSignIn.getWork_date();
		   else 
			   end_date=end_date.replaceAll("-","\\.");
		   String code=(String)this.getFormHM().get("code");
		   String kind=(String)this.getFormHM().get("kind");
		   if(kind==null||kind.length()<=0)
              kind="";
		   StringBuffer condition=new StringBuffer();
		   if(code==null||code.length()<=0)
			{
			   LazyDynaBean bean=RegisterInitInfoData.getKqPrivCodeAndKind(userView);
			   code=(String)bean.get("code");
			   kind=(String)bean.get("kind");
			}		   
			if("1".equals(kind))
			{
				condition.append(" and e0122 like '"+code+"%'");
			}else if("0".equals(kind))
			{
				condition.append(" and e01a1 like '"+code+"%'");	
			}else
			{
				condition.append(" and b0110 like '"+code+"%'");	
			}
		   StringBuffer column=new StringBuffer();
		   column.append("nbase,a0100,a0101,b0110,e0122,e01a1,card_no,work_date,work_time,location,sp_flag,oper_cause");
		   String sql="select "+column;
		   StringBuffer where_str=new StringBuffer();
		   where_str.append("from kq_originality_data ");
		   where_str.append(" where 1=1");		
		   where_str.append(" and work_date>='"+start_date+"'");
		   where_str.append(" and work_date<='"+end_date+"'");
		   where_str.append(" "+condition.toString());
		   if(location!=null&&location.length()>0&&!"all".equals(location))
			   where_str.append(" and location='"+location+"'");
		   KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
		   String where_c=kqUtilsClass.getWhere_C(select_flag,"a0101",select_name);
		   where_str.append(where_c);
		   where_str.append(" and sp_flag <> '01'");
		   this.getFormHM().put("sql_str",sql);
		   this.getFormHM().put("column_str",column.toString());
		   this.getFormHM().put("where_str",where_str.toString());
		   this.getFormHM().put("order_str","order by b0110,e0122,a0100,work_date,work_time");		 
		   this.getFormHM().put("fieldlist",getViewlist());
	   }
        private ArrayList getViewlist()
        {
        	ArrayList list=new ArrayList();
        	FieldItem fielditem=new FieldItem();
        	fielditem.setItemid("nbase");
        	fielditem.setCodesetid("@@");
        	fielditem.setItemdesc("人员库");
        	fielditem.setVisible(true);
        	list.add(fielditem);
        	fielditem=new FieldItem();
        	fielditem.setItemid("b0110");
        	fielditem.setCodesetid("UN");
        	fielditem.setItemdesc("单位");
        	fielditem.setVisible(true);
        	list.add(fielditem);
        	fielditem=new FieldItem();
        	fielditem.setItemid("e0122");
        	fielditem.setCodesetid("UM");
        	fielditem.setItemdesc("部门");
        	fielditem.setVisible(true);
        	list.add(fielditem);
        	fielditem=new FieldItem();
        	fielditem.setItemid("e01a1");
        	fielditem.setCodesetid("@K");
        	fielditem.setItemdesc("职位");
        	fielditem.setVisible(true);
        	list.add(fielditem);
        	fielditem=new FieldItem();
        	fielditem.setItemid("a0100");
        	fielditem.setCodesetid("0");
        	fielditem.setItemdesc("人员编号");
        	fielditem.setVisible(false);
        	list.add(fielditem);
        	fielditem=new FieldItem();
        	fielditem.setItemid("a0101");
        	fielditem.setCodesetid("0");
        	fielditem.setItemdesc("姓名");
        	fielditem.setVisible(true);
        	list.add(fielditem);
        	fielditem=new FieldItem();
        	fielditem.setItemid("card_no");
        	fielditem.setCodesetid("0");
        	fielditem.setItemdesc("卡号");
        	fielditem.setVisible(true);
        	list.add(fielditem);
        	fielditem=new FieldItem();
        	fielditem.setItemid("work_date");
        	fielditem.setCodesetid("0");
        	fielditem.setItemdesc("日期");
        	fielditem.setVisible(true);
        	list.add(fielditem);
        	fielditem=new FieldItem();
        	fielditem.setItemid("work_time");
        	fielditem.setCodesetid("0");
        	fielditem.setItemdesc("时间");
        	fielditem.setVisible(true);
        	list.add(fielditem);
        	fielditem=new FieldItem();
        	fielditem.setItemid("location");
        	fielditem.setCodesetid("0");
        	fielditem.setItemdesc("说明");
        	fielditem.setVisible(true);
        	list.add(fielditem);
        	fielditem=new FieldItem();
        	fielditem.setItemid("sp_flag");
        	fielditem.setCodesetid("23");
        	fielditem.setItemdesc("审批标志");
        	fielditem.setVisible(true);
        	list.add(fielditem);
        	fielditem=new FieldItem();
        	fielditem.setItemid("oper_cause");
        	fielditem.setCodesetid("0");
        	fielditem.setItemdesc("补刷卡原因");
        	fielditem.setVisible(true);
        	list.add(fielditem);
        	return list;
        }
}
