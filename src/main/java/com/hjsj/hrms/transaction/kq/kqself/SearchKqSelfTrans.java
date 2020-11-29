package com.hjsj.hrms.transaction.kq.kqself;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SearchKqSelfTrans extends IBusiness {
	
	private void getTit(String table)
	{
		String ta =table.toString();
		ArrayList fieldlist = DataDictionary.getFieldList(table,Constant.USED_FIELD_SET);// 字段名
		ArrayList list=new ArrayList();
		boolean hasFlagField = false;
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem field=(FieldItem)fieldlist.get(i);
			field.setValue("");
			field.setViewvalue("");
			if("b0110".equals(field.getItemid())||field.getItemid().equals(ta+"01")|| "a0101".equals(field.getItemid())|| "e0122".equals(field.getItemid())|| "nbase".equals(field.getItemid())|| "a0100".equals(field.getItemid())|| "e01a1".equals(field.getItemid()))
		      field.setVisible(false);
			else if("q1517".equals(field.getItemid())|| "q1519".equals(field.getItemid()))
				field.setVisible(false);
			else
			{
				if("1".equalsIgnoreCase(field.getState()))
			          field.setVisible(true);
					else 
						field.setVisible(false);
				
				
			}
			if ("flag".equals(field.getItemid())) 
			{
				hasFlagField = true;
			}
			
			FieldItem field_n=(FieldItem)field.cloneItem();
			list.add(field_n);
		} 
		
		if (!hasFlagField && "Q11".equalsIgnoreCase(table)) {
			FieldItem item = new FieldItem();
			item.setItemid("flag");
			item.setFieldsetid("Q11");
			item.setUseflag("1");
			item.setCodesetid("0");
			item.setItemtype("A");
			item.setItemdesc("申请标识");
			item.setVisible(false);
			item.setItemlength(1);
			FieldItem field_n=(FieldItem)item.cloneItem();
			list.add(field_n);
		}
		this.getFormHM().put("flist", list);
	}

	/**
	 * 校验日期是否正确
	 * @return
	 */
	private boolean validateDate(String datestr)
	{
		boolean bflag=true;
		if(datestr==null|| "".equals(datestr))
			return false;
		try
		{
			Date date=DateStyle.parseDate(datestr);
			if(date==null)
				bflag=false;
		}
		catch(Exception ex)
		{
			bflag=false;
		}
		return bflag;
	}
	
	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String table = (String) hm.get("table");
		String kq_year=(String)this.getFormHM().get("kq_year");
		String kq_duration = (String) this.getFormHM().get("kq_duration");
		String select_flag = (String) this.getFormHM().get("select_flag");
		boolean ball=false;
		String start_date=(String)this.getFormHM().get("start_date");
		String end_date=(String)this.getFormHM().get("end_date");	
		String seal_date=(String)this.getFormHM().get("seal_date");
		KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
		start_date=kqUtilsClass.getSafeCode(start_date);
		end_date=kqUtilsClass.getSafeCode(end_date);
		if(seal_date==null||seal_date.length()<=0)
		{//封存的最后一天
			ArrayList list=RegisterDate.getKqDayList(this.getFrameconn());
			if(list!=null&&list.size()>0)
				seal_date=list.get(0).toString();
		}
		if(!(validateDate(start_date.replaceAll("\\.", "-"))&&validateDate(end_date.replaceAll("\\.", "-"))))
		{
			//start_date=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
			//end_date=start_date;
			ball=true;
		}
		else
			end_date=end_date+" 23:59:59"; //oracle ,db2解决办法?,查询包括时分秒处理
	    ArrayList fieldlist = DataDictionary.getFieldList(table,Constant.USED_FIELD_SET);// 字段名
		StringBuffer sql_str = new StringBuffer();
		StringBuffer cond_str = new StringBuffer();
	    String columns = "";
	    
		sql_str.append("select ");
		for (int i = 0; i < fieldlist.size(); i++)
		{
			FieldItem field = (FieldItem) fieldlist.get(i);
			columns=columns+field.getItemid().toString()+",";
			if("D".equalsIgnoreCase(field.getItemtype()) && Sql_switcher.searchDbServer() == Constant.ORACEL){
				sql_str.append("TO_CHAR("+field.getItemid().toString()+",'YYYY-MM-DD HH24:MI:SS') "+field.getItemid().toString());
			}else{
				sql_str.append(field.getItemid().toString());
			}
		
			if (i != fieldlist.size() - 1)
				sql_str.append(",");
		}
		DbWizard dbw = new DbWizard(frameconn);
		if ("Q11".equalsIgnoreCase(table) && dbw.isExistField("Q11", "flag", false) && sql_str.indexOf("flag") == -1) {
			sql_str.append(",flag");
			columns = columns + "flag" + ",";
		}
		/*columns=columns+"name";
		sql_str.append(",name");*/
		  cond_str.append(" from ");
		  cond_str.append(table+"");
		  cond_str.append(" where a0100 ='");

		  cond_str.append(userView.getA0100()/*userView.getUserId()*/);
		  cond_str.append("' and UPPER(nbase)='");
		  cond_str.append(userView.getDbname().toUpperCase());
		  cond_str.append("'");
		 // cond_str.append(" and "+table+"."+table+"04=kq_class.class_id");
		  if(!ball)
		  {
			  cond_str.append(" and (");
			  cond_str.append(table);
			  cond_str.append("z3 ");
			  cond_str.append(" >=");
			  cond_str.append(Sql_switcher.dateValue(start_date));
			  //cond_str.append(start_date);
			  cond_str.append(" and ");		  
			  cond_str.append(table);
			  cond_str.append("z1 ");		  
			  cond_str.append(" <=");
			  cond_str.append(Sql_switcher.dateValue(end_date));			  
			  //cond_str.append(end_date);
			  cond_str.append(")");	
		  }else
		  {
			  if(seal_date!=null&&seal_date.length()>0)
			  {
				  cond_str.append(" and ");
				  cond_str.append(table);
				  cond_str.append("z3 ");
				  cond_str.append(" >= ");
				  cond_str.append(Sql_switcher.dateValue(seal_date));
				
			  }
				  
		  }
			 
		  cond_str.append(" and "+Sql_switcher.isnull(table + "17","0")+"=0");		  
		  
		  if (select_flag != null && select_flag.length() > 0 && !"all".equals(select_flag)) 
		  {
			  cond_str.append(" and " + table.toLowerCase() + "z5='" + select_flag + "'");
		  }
		  this.getTit(table);
		  this.getFormHM().put("order","order by "+table+ "Z1");
		  this.getFormHM().put("sql",sql_str.toString());
		  this.getFormHM().put("com",columns);
		  this.getFormHM().put("table",table);
		  this.getFormHM().put("where",cond_str.toString());
		  this.getFormHM().put("kq_duration", kq_duration);
		  this.getFormHM().put("kq_year", kq_year);
		  this.getFormHM().put("seal_date",seal_date);
		  if ("q11".equalsIgnoreCase(table)) 
		  {
			  SearchAllApp searchAllApp=new SearchAllApp(this.getFrameconn(),this.userView);
			  ArrayList appStatusList = searchAllApp.getSplist();
			  appStatusList.add(1, new CommonData("01", "起草"));
			  this.getFormHM().put("appStatusList",appStatusList);
		  }
		  String field = KqUtilsClass.getFieldByDesc("Q11", ResourceFactory.getProperty("kq.self.app.workingdaysoff.yesorno"));
		  this.getFormHM().put("field", field);
	}
}
