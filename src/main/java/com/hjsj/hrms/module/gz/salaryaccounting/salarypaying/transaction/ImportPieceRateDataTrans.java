package com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.transaction;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
/**
 * @ClassName: ImportPieceRateDataTrans 
 * @Description: TODO(引入计件薪资) 
 * @author lis 
 * @date 2015-8-25 上午11:18:24
 */
public class ImportPieceRateDataTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
    	try
		{ 
		String salaryid = (String) this.getFormHM().get("salaryid");
		String appdate = (String) this.getFormHM().get("appdate");
		
		salaryid=SafeCode.decode(salaryid); //解码
		salaryid =PubFunc.decrypt(salaryid); //解密
		appdate=SafeCode.decode(appdate); //解码
		appdate =PubFunc.decrypt(appdate); //解密
		
		if(StringUtils.isBlank(appdate)) //没有发放薪资数据
			throw new GeneralException(ResourceFactory.getProperty("gz_new.gz_accounting.noGzData"));
		
		SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(), Integer.parseInt(salaryid), this.userView);
		
		String tablename = gzbo.getGz_tablename();
    	String TmpPieceTab="t#_"+this.userView.getUserName()+"_S05";
		
	    String strWhere="  A00Z1= (select min(b.A00Z1) from "+tablename+" b where "+tablename+".a0100=b.a0100 and UPPER("+tablename+".NBASE)=UPPER(b.NBASE)) ";
		
	   //获得薪资发放前台过滤条件和当前用户的可操作范围SQL 
		String whl_str =gzbo.getFilterAndPrivSql_ff(); 
		if(StringUtils.isNotBlank(whl_str))
		{
			strWhere = strWhere + whl_str;
		}
		
		String querySql = gzbo.getfilter(tablename);//得到前台过滤条件
		if(StringUtils.isNotBlank(querySql)) 
			strWhere = strWhere + querySql;
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		DbWizard dbw = new DbWizard(this.frameconn);

    	String strExpression=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"strExpression"); //公式表达式
    	String rela_fld=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"relation_field"); //对应指标
    	rela_fld = rela_fld.replaceAll("＝", "=");
    	
        if (StringUtils.isBlank(rela_fld)){             
                throw new GeneralException(ResourceFactory.getProperty("gz_new.gz_accounting.noPiecefield"));
        }
        
        //公式表达式转化为sql
        String sqlExpression=this.getSqlExpression(strExpression); 
		
		String strBeginDate=""; //开始日期
		String strEndDate=""; //结束日期
		
		//得到计件薪资计件归属日期的开始和结束日期
		HashMap<String, String> dateMap = this.getBeginAndEndDate(tablename,appdate,gzbo);
		strBeginDate = dateMap.get("strBeginDate");
		strEndDate = dateMap.get("strEndDate");
		String strDateScope=" and s0104 between "+Sql_switcher.dateValue(strBeginDate)+" and "+Sql_switcher.dateValue(strEndDate)+"";
		
    	String updateFields=""; //要更新指标
    	String sumFields=""; //要取指标值相加取和的指标
    	String[] strfieldlist=rela_fld.split(",");
    	for (int i=0 ;i<strfieldlist.length;i++)
    	{
    		String s=strfieldlist[i];
    		int k= s.indexOf("=");
    		if (k>0){
    			String relafld1=s.substring(0,k);
    			String relafld2=s.substring(k+1,s.length()); 			
    			updateFields = updateFields+"`"+tablename+"."+ relafld2+"="+TmpPieceTab+"."+	relafld1;
    			sumFields = sumFields +", sum("+relafld1+") as "+relafld1;
    		}
    	}

		if(dbw.isExistTable(TmpPieceTab, false))	
			dbw.dropTable(TmpPieceTab);
		
		String strSql="";
		if (DbWizard.dbflag==1){
		    strSql="select Nbase,A0100"+sumFields+" into "+TmpPieceTab; 
		}
		else {
		    strSql=" create table "+TmpPieceTab +" as select Nbase,A0100"+sumFields;    
		}

		strSql=strSql
    	     +" from (select S05.*,s01.S0102 from S05, "
    		 +"(select S0100,S0104,S0102 from S01 where  SP_FLAG ='03'";
    	if (StringUtils.isNotBlank(strDateScope))
    	{
    		strSql=strSql+strDateScope;	
    	}
    	strSql=strSql +") S01"
    		 +" where S01.S0100= S05.S0100 ) S05 ";
    	if (StringUtils.isNotBlank(sqlExpression))
    	{
    		strSql=strSql+" where " + sqlExpression;	
    	}	 
    		 
    	strSql=strSql +"group by  Nbase,A0100 ";	    	
    	dao.update(strSql);
	    String strJoin="UPPER("+tablename+".Nbase)=UPPER("+TmpPieceTab+".Nbase) and "+tablename+".A0100="+	TmpPieceTab+".A0100";
	    updateFields=updateFields.substring(1);
        
	   dbw.updateRecord(tablename, TmpPieceTab, strJoin, updateFields, strWhere, "");
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
    }
    
    /**
     * @Title: getSql 
     * @Description: TODO(将公式转化为sql) 
     * @param strExpression 公式表达式
     * @return String
     * @throws GeneralException
     * @author lis  
     * @date 2015-8-24 下午05:18:59
     */
    private String getSqlExpression(String strExpression) throws GeneralException{
    	try {
    		String sqlExpression=""; //公式表达式
    		if (StringUtils.isNotBlank(strExpression)){    
    			ArrayList fieldlist = DataDictionary.getFieldList("S05", Constant.USED_FIELD_SET);
				FieldItem item = new FieldItem();
				item.setItemid("S0102");
				//计件作业类别
				item.setItemdesc(ResourceFactory.getProperty("gz_new.gz_accounting.pieceType"));
				item.setItemtype("A");
				item.setItemlength(30);
				item.setDecimalwidth(4);
				item.setCodesetid("0");
				fieldlist.add(item);
    			YksjParser yp = new YksjParser(getUserView(), fieldlist, YksjParser.forNormal, YksjParser.LOGIC
    					, YksjParser.forPerson,"Ht", "");
    			yp.run(strExpression);
    			sqlExpression = yp.getSQL();//得到公式的结果
    		}
    		return sqlExpression;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    }
    
    /**
     * @Title: getBeginAndEndDate 
     * @Description: TODO(这里用一句话描述这个方法的作用) 
     * @param period 业务周期类型
     * @param currym 当前业务日期
     * @param firstday 周期类型是月时开始的天
     * @return HashMap<String, String>
     * @throws GeneralException
     * @author lis  
     * @date 2015-8-24 下午05:17:29
     */
    private HashMap<String, String> getBeginAndEndDate(String tablename,String currym,SalaryTemplateBo gzbo) throws GeneralException{
    	try {
    		
    		String period=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"period"); //业务周期类别,年、月、半年、季
    		//取得当前发放日期
        	
    		HashMap<String, String> dateMap = new  HashMap<String, String>();
    		Calendar cal = Calendar.getInstance();   
    		String strBeginDate=""; //开始日期
    		String strEndDate=""; //结束日期
    		
        	//格式化当前业务日期
    		Date date = stringToDate(currym,"yyyy-MM");
    		cal.setTime(date);   		    		
    		int year = cal.get(Calendar.YEAR);
    		int month = cal.get(Calendar.MONTH);
    		month = month+1;
        	
        	int b_month= month; //开始月份
    		int e_month= month; //结束月份
    		int b_year= year; //开始年
    		int e_year= year; //结束年
    		int b_day= 1; //开始天，默认从第一天开始
    		int e_day= 31; //结束天，默认31
    		
        	if ("1".equals(period))//月
        	{
        		String firstday=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"firstday"); //业务日期是月的时候月份的开始第一天
        		b_day = Integer.valueOf(firstday);
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
        	}
        	else if ("2".equals(period))//季度
        	{
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
    			b_month= 1;	
    			e_month=12;
        		
        	}
        	
        	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
        	cal.set(b_year, b_month-1, b_day);
    		strBeginDate= formatter.format(cal.getTime());			
    		cal.set(e_year, e_month-1, e_day);
    		strEndDate= formatter.format(cal.getTime());  
    		
    		dateMap.put("strBeginDate", strBeginDate);
    		dateMap.put("strEndDate", strEndDate);
    		
    		return dateMap;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    	
    }
    /**
     * @Title: stringToDate 
     * @Description: TODO(字符型日期转换为日期类型) 
     * @param _date 日期值
     * @param format 初始化
     * @return Date
     * @author lis  
     * @date 2015-8-21 下午05:44:03
     */
    private Date stringToDate(String _date,String format){
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
}
