package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ImportPieceRateDataTrans extends IBusiness {
    String currym="";
    public void execute() throws GeneralException {
		String salaryid = (String) this.getFormHM().get("salaryid");
		SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(), Integer.parseInt(salaryid), this.userView);

		String tablename = gzbo.getGz_tablename();
    	String TmpPieceTab="t#_"+this.userView.getUserName()+"_S05";
		String manager = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
		getCurrentGzTableYearMonthCount(tablename);
		if("".equals(currym)) return;
		
		boolean isShare = true;
		boolean isGZmanager = false;
		if (manager == null || (manager != null && manager.length() == 0))// 不共享
		    isShare = false;
		if (this.userView.getUserName().equals(manager))
		    isGZmanager = true;
	
	    String strWhere="  A00Z1= (select min(b.A00Z1) from "+tablename+" b where "+tablename+".a0100=b.a0100 and "+tablename+".NBASE=b.NBASE  ) ";
		if (isShare && !isGZmanager)// 共享的工资类别且非管理员有人员范围限制
		{			
			String showUnitCodeTree=gzbo.getControlByUnitcode();
			if("1".equals(showUnitCodeTree)) ////是否按操作单位来控制
			{				 
				String whl_str=gzbo.getWhlByUnits();
				if(whl_str.length()>0)
				{
					strWhere=strWhere + whl_str;
				}
			}
			else 
			{
			    String a_code = this.userView.getManagePrivCode() + this.userView.getManagePrivCodeValue();
			    String codesetid = a_code.substring(0, 2);
			    String value = a_code.substring(2);
			    if ("UN".equalsIgnoreCase(codesetid))
			    {
				strWhere=strWhere + " and (B0110 like '"+ value+"%' ";
				if ("".equalsIgnoreCase(value)){
					strWhere=strWhere+" or B0110 is null";
				}
				
				strWhere=strWhere+" )";
			    } else if ("UM".equalsIgnoreCase(codesetid))
			    {
				strWhere=strWhere+ " and E0122 like '"+ value+"'% ";				
			    }
			}	    
		    
		}

		ContentDAO dao = new ContentDAO(this.frameconn);
		DbWizard dbw=new DbWizard(this.frameconn);

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
		try
		{          
	    	String period=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"period");
	    	String firstday=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"firstday");
	    	String strExpression=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"strExpression");
	    	String rela_fld=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"relation_field");
	    	rela_fld = rela_fld.replaceAll("＝", "=");
	    	if ("".equals(period)){	    	    
	    	    period="1";
	    	    firstday="1";
	    	}
	        if (rela_fld==null || "".equals(rela_fld)){             
	                throw new GeneralException("请设置需要引入的指标");
	        }
	    	Calendar cal = Calendar.getInstance();   
    		Date date1 = stringToDate(currym,"yyyy-MM-dd");
    		cal.setTime(date1);   		    		
    		int year = cal.get(Calendar.YEAR);
    		int month = cal.get(Calendar.MONTH);
    		month = month+1;
    		
    		
    		int b_month= month;
    		int e_month= month;
    		int b_year= year;
    		int e_year= year;
    		int b_day= Integer.parseInt(firstday);
    		int e_day= 2;
    		
    		String strBeginDate="";
    		String strEndDate="";
    		String sqlExpression="";
    	//	strExpression="计件作业类别="+"\""+ "01"+"\""+" 且 单位="+"\""+"0101"+"\"";
    		if (!"".equals(strExpression)){
    			ArrayList fieldlist = DataDictionary.getFieldList("S05", Constant.USED_FIELD_SET);
				FieldItem item = new FieldItem();
				item.setItemid("S0102");
				item.setItemdesc("计件作业类别");
				item.setItemtype("A");
				item.setItemlength(30);
				item.setDecimalwidth(4);
				item.setCodesetid("0");
				fieldlist.add(item);
    			YksjParser yp = new YksjParser(getUserView(), fieldlist, YksjParser.forNormal, YksjParser.LOGIC
    					, YksjParser.forPerson,"Ht", "");
    			yp.run(strExpression);
    			sqlExpression = yp.getSQL();//公式的结果
    		}
	    	if ("1".equals(period))//月
	    	{
	    		if (!"1".equals(firstday)){
	    			if (month==1){
	    				b_month= 12;	
	    				b_year= year-1;	
	    			}
	    			else {
	    				b_month =month-1;
	    			}    			
	    			e_day = Integer.parseInt(firstday)-1;
	    		}
	    		else{
	    			e_day = 31;		
	    		}
	    		
	    	}
	    	else if ("2".equals(period))//季度
	    	{
  				b_day=1;
    			e_day = 31;
    			if ((month>=1)&&(month<=3))
    			{
    				b_month= 1;	   
    				e_month=3;
	    		}
	    		else if ((month>=4)&&(month<=6))
	    		{
	    			b_month= 4;	   
	    			e_month=6;
	    		}
	    		else if ((month>=7)&&(month<=9))
	    		{

	    			b_month= 7;	   
	    			e_month=9;
	    		}
	    		else if ((month>=10)&&(month<=12))
	    		{
    				b_month= 9;	
    				e_month=12;
	    		}
	    	}
	    	else if ("3".equals(period))//半年
	    	{
	    		b_day=1;
	    		e_day = 31;
	    		if ((month>=1)&&(month<=6))
	    		{
	    			b_month= 1;	   
	    			e_month=6;

	    		}
	    		else 
	    		{
	    			b_month= 7;	
	    			e_month=12;
	    		}
	    	}
	    	else if ("4".equals(period))//年
	    	{
	    		b_day=1;
	    		e_day = 31;
    			b_month= 1;	
    			e_month=12;
	    		
	    	}
	
			cal.set(b_year, b_month-1, b_day);
			strBeginDate= formatter.format(cal.getTime());			
			cal.set(e_year, e_month-1, e_day);
			strEndDate= formatter.format(cal.getTime());   	
			
	    	String strDateScope=" and s0104 between "+Sql_switcher.dateValue(strBeginDate)+" and "+Sql_switcher.dateValue(strEndDate)+"";
	    	String updFlds="";
	    	String sumFlds="";
	    	String[] strfieldlist=rela_fld.split(",");
	    	for (int i=0 ;i<strfieldlist.length;i++)
	    	{
	    		String s=strfieldlist[i];
	    		int k= s.indexOf("=");
	    		if (k>0){
	    			String relafld1=s.substring(0,k);
	    			String relafld2=s.substring(k+1,s.length()); 			
	    			updFlds=updFlds+"`"+tablename+"."+ relafld2+"="+TmpPieceTab+"."+	relafld1;
	    			sumFlds= sumFlds +", sum("+relafld1+") as "+relafld1;
	    		}
	    		
	    	}

			if(dbw.isExistTable(TmpPieceTab, false))	dbw.dropTable(TmpPieceTab);
			String strSql="";
			if (dbw.dbflag==1){
			    strSql="select Nbase,A0100"+sumFlds+" into "+TmpPieceTab; 
			}
			else {
			    strSql=" create table "+TmpPieceTab +" as select Nbase,A0100"+sumFlds;    
			}

			strSql=strSql
	    	     +" from (select S05.*,s01.S0102 from S05, "
	    		 +"(select S0100,S0104,S0102 from S01 where  SP_FLAG ='03'";
	    	if (!"".equals(strDateScope))
	    	{
	    		strSql=strSql+strDateScope;	
	    	}
	    	strSql=strSql +") S01"
	    		 +" where S01.S0100= S05.S0100 ) S05 ";
	    	if (!"".equals(sqlExpression))
	    	{
	    		strSql=strSql+" where " + sqlExpression;	
	    	}	 
	    		 
	    	strSql=strSql +"group by  Nbase,A0100 ";	    	
	    	dao.update(strSql);
		    String strJoin=tablename+".Nbase="+	TmpPieceTab+".Nbase and "+tablename+".A0100="+	TmpPieceTab+".A0100";
		    updFlds=updFlds.substring(1);
		   
	        
			dbw.updateRecord(tablename, TmpPieceTab, strJoin, updFlds, strWhere, "");
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
    }
    
    
	
    private static Date stringToDate(String _date,String format){
    	  if(null == format || "".equals(format))
    	        {
    	            format = "yyyy-MM-dd HH:mm:ss";
    	        }
    	  SimpleDateFormat sdf = new SimpleDateFormat(format);
    	  Date date=null;
    	  try {
    	   date=sdf.parse(_date);
    	  } catch (ParseException e) {
    	   e.printStackTrace();
    	  }
    	  return date;
    	 }

    public String getDatePart(String mydate, String datepart)
    {
    	
    	String str = "";
    	if ("y".equalsIgnoreCase(datepart))
    		str = mydate.substring(0, 4);
    	else if ("m".equalsIgnoreCase(datepart))
    	{
    		if ("0".equals(mydate.substring(5, 6)))
    			str = mydate.substring(6, 7);
    		else
    			str = mydate.substring(5, 7);
    	} else if ("d".equalsIgnoreCase(datepart))
    	{
    		if ("0".equals(mydate.substring(8, 9)))
    			str = mydate.substring(9, 10);
    		else
    			str = mydate.substring(8, 10);
    	}
    	return str;
    }
	private HashMap getCurrentGzTableYearMonthCount(String tablename)
	{
		HashMap mp=new HashMap();
		String strYm="";
		String strC="";
		StringBuffer buf=new StringBuffer();
		buf.append("select A00Z2, A00Z3 from ");
		buf.append(tablename);
		try
		{
			ContentDAO dao=new ContentDAO(this.frameconn);
			RowSet rset=dao.search(buf.toString());
			if(rset.next())
			{
				strYm=PubFunc.FormatDate(rset.getDate("A00Z2"), "yyyy-MM-dd");
				strC=rset.getString("A00Z3");
			}
			if("".equalsIgnoreCase(strYm))
			{
				strYm=DateUtils.format(new Date(), "yyyy-MM-dd");
				strC="1";
			}
			rset.close();
			/**当前处理的到的年月标识和次数*/
			currym=strYm;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return mp;
	}

}
