package com.hjsj.hrms.transaction.kq.options.init;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;

public class KqFileCodeTrans extends IBusiness {
	
	private String out = "";//公出
	private String otime = "";//加班
	private String rest = "";//休息
	private String staffl = "";//员工日明细
	private String staffy = "";//员工月汇总
	private String deptl = "";//部门日明细
	private String depty = "";//部门月汇总	
	private String shift = "";//员工排班信息表
	private String ypsk = "";//员工刷卡信息表
	
	private boolean isNull(String str)
	{
		boolean boo=false;
		if(!(str==null|| "".equals(str)))
		{
			boo=true;
		}
		
		return boo;
	}
	public void execute() throws GeneralException {
		out = (String)this.getFormHM().get("out");//公出
		otime = (String)this.getFormHM().get("outime");//加班
		rest = (String)this.getFormHM().get("rest");//休息
		staffl = (String)this.getFormHM().get("staffl");//员工日明细
		staffy = (String)this.getFormHM().get("staffy");//员工月汇总
		deptl = (String)this.getFormHM().get("deptl");//部门日明细
		depty = (String)this.getFormHM().get("depty");//部门月汇总	
		shift = (String)this.getFormHM().get("shift");//员工排班信息表
		ypsk = (String)this.getFormHM().get("ypsk");//员工刷卡信息表
		String scope = (String)this.getFormHM().get("scope");//时间范围标记
		String tstart = (String)this.getFormHM().get("count_start");// 开始时间
		String tend = (String)this.getFormHM().get("count_end"); // 结束时间
		
		
		KqUtilsClass kqClass = new KqUtilsClass(this.frameconn);
		// 允许操作的时间点
		LazyDynaBean bean= kqClass.getMaxDuration();
		String kq_start = (String) bean.get("kq_start");
		
		
		if (kq_start != null && kq_start.length() > 0) {
			this.getFormHM().put("scope","1");
			if (tend != null && tend.length() > 0) {
				if (tend.compareTo(kq_start) >= 0 && !"1".equalsIgnoreCase(scope)) {
					this.getFormHM().put("mess","3");
					this.getFormHM().put("erro",kq_start+"及"+kq_start+"以后的数据不允许归档！归档失败！");
					return;
				}
			}
			// 归档全部数据时，获取全部的开始结束日期 
			if("1".equalsIgnoreCase(scope)){
				//获取考勤期间最小开始日期
				ArrayList minSessionlist = RegisterDate.getMinKqDayList(this.frameconn);
				if(minSessionlist.size()==0 || null==minSessionlist){
					this.getFormHM().put("mess","3");
					this.getFormHM().put("erro","没有需要归档的数据！");
					return;
				}
				String startValue = (String) minSessionlist.get(0);
				startValue = startValue.replaceAll("\\.", "-");
				if(StringUtils.isEmpty(startValue)){
					this.getFormHM().put("mess","3");
					this.getFormHM().put("erro","没有需要归档的数据！");
					return;
				}
		        
		        //减一天的日期为全部的结束日期
		        String endValue = DateUtils.format(DateUtils.addDays(DateUtils.getDate(kq_start, "yyyy-MM-dd"), -1), "yyyy-MM-dd");
		        //如果结束日期不在开始日期之后直接返回
		        if (!DateUtils.getDate(endValue, "yyyy-MM-dd").after(DateUtils.getDate(startValue, "yyyy-MM-dd")))
		        {
		        	this.getFormHM().put("mess","3");
					this.getFormHM().put("erro","没有需要归档的数据！");
					return;
		        }
		        tstart = startValue;
		        tend = endValue;
			}
			
			if(StringUtils.isNotEmpty(tstart) && StringUtils.isNotEmpty(tend)){
				Date fromdt = DateUtils.getDate(tstart, "yyyy-MM-dd");
		    	Date todt = DateUtils.getDate(tend, "yyyy-MM-dd");
		    	int days = DateUtils.dayDiff(fromdt, todt);
		    	
		    	//先归档不需要按天记录的数据（数据量相对少）
		    	doArch(kqClass, tstart, tend);
		    	
		    	//按天记录的数据（数据量大），每7天一批进行归档
		    	if(days<8 && days>0){
		    		doDailyArch(kqClass, tstart, tend);
		    	}else if(days>7){
		    		//归档数据时现优化为7天一处理
		    		int num = days/7;
		    		int remainder = days%7;
		    		if(remainder > 0)
		    			num = num + 1;
		    		
		    		String fromClockstr = tstart;
		    		String toClockstr = "";
		    		for(int i=0;i<num;i++){
		    			if(i==(num-1) && remainder > 0){
		    			    doDailyArch(kqClass, fromClockstr, tend);
		    			}else{
			    			toClockstr = DateUtils.format(DateUtils.addDays(DateUtils.getDate(fromClockstr, "yyyy-MM-dd"), (7-1)), "yyyy-MM-dd");
			    			doDailyArch(kqClass, fromClockstr, toClockstr);
			    			fromClockstr = DateUtils.format(DateUtils.addDays(DateUtils.getDate(toClockstr, "yyyy-MM-dd"), 1), "yyyy-MM-dd");
		    			}
		    		}
		    	}
			}
				
			this.getFormHM().put("mess","2");
		} else {
			this.getFormHM().put("mess","3");
			this.getFormHM().put("erro","操作失败！");
			throw GeneralExceptionHandler.Handle(new Exception("考勤数据没有封存，归档失败！"));
		}
	}
	
	/**
	 * 归档员工月汇总、部门月汇总数据
	 * @param kqClass	
	 * @param tstart	开始日期
	 * @param tend	          结束日期
	 * @throws GeneralException
	 */
	private void doArch(KqUtilsClass kqClass, String tstart, String tend) throws GeneralException{
		StringBuffer table=null;
		
		if(this.isNull(staffy)){
			table=new StringBuffer("Q05");
			String where = kqClass.dealTime(tstart, "Q03Z0", tend, "Q03Z0", "2", "3");
            this.doFile(table.toString(), where);		   	
		}
		
		if(this.isNull(depty)){
			table=new StringBuffer("Q09");
			String where = kqClass.dealTime(tstart, "Q03Z0", tend, "Q03Z0", "2", "3");
            this.doFile(table.toString(), where+"`"+Sql_switcher.substr("Q03Z0", "6", "2")+"<>'PT'");	   	
		}
		
	}
	
	/**
	 * 按日归档的数据
	 * @param kqClass
	 * @param tstart
	 * @param tend
	 * @throws GeneralException
	 */
	private void doDailyArch(KqUtilsClass kqClass, String tstart, String tend) throws GeneralException {
	    StringBuffer table=null;
        if(this.isNull(out)){
            table=new StringBuffer("Q13");
            String where = kqClass.dealTime(tstart, "Q13Z1", tend, "Q13Z3", "2", "1");
            this.doFile(table.toString(), where);
        }
        
        if(this.isNull(otime)){
            table=new StringBuffer("Q11");
            String where = kqClass.dealTime(tstart, "Q11Z1", tend, "Q11Z3", "2", "1");
            this.doFile(table.toString(), where);
        }
        
        if(this.isNull(rest)){
            table=new StringBuffer("Q15");
            String where = kqClass.dealTime(tstart, "Q15Z1", tend, "Q15Z3", "2", "1");
            this.doFile(table.toString(), where);           
        }
        
        if(this.isNull(staffl)){
            table=new StringBuffer("Q03");
            String where = kqClass.dealTime(tstart, "Q03Z0", tend, "Q03Z0", "2", "2");
            this.doFile(table.toString(), where);                   
        }
        
        if(this.isNull(deptl)){
            table=new StringBuffer("Q07");
            String where = kqClass.dealTime(tstart, "Q03Z0", tend, "Q03Z0", "2", "2");
            this.doFile(table.toString(), where);                   
        }
        
        if(this.isNull(shift)){
            table=new StringBuffer("kq_employ_shift");//人员排班表
            String where = kqClass.dealTime(tstart, "Q03Z0", tend, "Q03Z0", "2", "2");
            this.doFile(table.toString(), where);
            
            table = new StringBuffer("kq_org_dept_shift");//班组、组织机构排班表
            where = kqClass.dealTime(tstart, "Q03Z0", tend, "Q03Z0", "2", "2");
            this.doFile(table.toString(), where);
        }
        
        if(this.isNull(ypsk)){
            table=new StringBuffer("kq_originality_data");
            String where = kqClass.dealTime(tstart, "Work_date", tend, "Work_date", "2", "2");
            this.doFile(table.toString(), where);           
        }
	}
	
	private void doFile(String table, String where) throws GeneralException{
		KqUtilsClass k=new KqUtilsClass(this.frameconn);
		
		// 数据归档
		String erro=k.copyContent(table, table+"_arc", "*", where, "");
		if (erro != null && erro.length() > 0) {
			this.getFormHM().put("mess","3");
			throw GeneralExceptionHandler.Handle(new Exception(erro));
		} else {
			// 删除源表的数据
			deleteFromStr(table, where);
		}
	}
	
	/**
	 * 删除源表中的数据
	 * @param tableName String 表名称
	 * @param where String 条件
	 */
	private void deleteFromStr(String tableName, String strWhere) {
		StringBuffer strSql = new StringBuffer();
		strSql.append("delete from ");
		strSql.append(tableName);
		strSql.append(" where 1=1 ");
		if (strWhere != null && strWhere.length() > 0){
			String[] str = strWhere.split("`");
			for (int i = 0; i < str.length; i++) {
				if (str[i].trim().length() > 0) {
					strSql.append(" and ");
					strSql.append(str[i]);
				}
				
			}
		}
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			dao.update(strSql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
