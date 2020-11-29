package com.hjsj.hrms.module.gz.salaryaccounting.changesmore.businessobject;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：ChangesmoreBo
 * 类描述：上期数据比对Bo类
 * 创建人：zhanghua
 * 创建时间：2016-11-09
 * @version
 */
public class ChangesmoreBo {
	private Connection conn = null;
	/** 薪资类别号 */
	private int salaryid = -1;
	/** 登录用户 */
	private UserView userview;
	private SalaryAccountBo salaryAccountBo = null;
	private SalaryTemplateBo salaryTemplateBo = null;
	private String tableOld;//上期数据所在的表名 在getBeforeYWDate中 由薪资审批页面进入时赋值
	private  double lastdataSum=0,nowdataSum=0,marginSum=0;//总计数值
	public ChangesmoreBo(Connection conn, int salaryid, UserView userview) {
		super();
		this.conn = conn;
		this.salaryid = salaryid;
		this.userview = userview;
		this.salaryAccountBo = new SalaryAccountBo(conn, this.userview,
				salaryid);
		this.salaryTemplateBo = this.salaryAccountBo.getSalaryTemplateBo();
		this.tableOld="";
	}
	/**
	 * 取得变动比对主表格数据
	 * @param tableName 表名 发放时为 user_salary_id 审批时为salaryhistory
	 * @param tableNameOld 历史表/归档表
	 * @param type type 1为薪资发放，2为薪资审批
	 * @param fieldItems 待比对指标
	 * @param deptSql 部门过滤条件
	 * @param ispriv true需要限制用户管理范围 
	 * @param strwhere 上期数据范围条件
	 * @param strWhereNow 本期数据范围条件
	 * @return
	 */
	public ArrayList getChangeMainDataList(String  tableName,String tableNameOld,String type,ArrayList<LazyDynaBean> fieldItems,String deptSql,String strwhere,String strWhereNow)throws GeneralException{
		ArrayList dataList=new ArrayList();
		String strSql="";
		try{
			
			ArrayList<LazyDynaBean> fieldItem=new ArrayList<LazyDynaBean>();
			//每20个字段 执行一次对比
			for(int i=0;i<fieldItems.size();i++){
				fieldItem.add(fieldItems.get(i));
				if(i!=0&&i%20==0||i==fieldItems.size()-1){
					strSql=this.getDataSql(fieldItem, tableName,tableNameOld, type, deptSql, strwhere, strWhereNow);//取得比对sql
					dataList.addAll((this.getDataList(strSql, fieldItem)));//取得数据
					fieldItem.clear();
				}
			}
		    LazyDynaBean dataBean=new LazyDynaBean();//最后插入总计行
		    dataBean.set("itemid", "rootSum");
		    dataBean.set("itemdesc", ResourceFactory.getProperty("label.gz.datasum"));//总计
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(2);
			nf.setMinimumFractionDigits(2);
			nf.setRoundingMode(RoundingMode.UP);

		    dataBean.set("lastdata", nf.format(this.lastdataSum));
		    dataBean.set("nowdata", nf.format(this.nowdataSum));
		    dataBean.set("peoplenum", "");
		    dataBean.set("margin", nf.format(this.marginSum));
		    dataList.add(dataBean);
		}catch(Exception e){
			throw GeneralExceptionHandler.Handle(e);
		}
		
		
		return dataList;
	}
	/***
	 * 拼接上期数据对比主表格数据查询sql
	 * @param fieldItems 待查询字段
	 * @param tableName 表名 发放时为 user_salary_id 审批时为salaryhistory
	 * @param tableNameOld 历史表/归档表 
	 * @param type 1为薪资发放，2为薪资审批
	 * @param deptSql 部门查询条件
	 * @param strwhere 本次发放日期
	 * @param strWhereNow 上次发放日期
	 * @return
	 */
	public String getDataSql(ArrayList<LazyDynaBean> fieldItems,String tableName,String tableNameOld,String type,String deptSql,String strwhere,String strWhereNow){
		
		StringBuffer mainSql=new StringBuffer();
		StringBuffer lastSql=new StringBuffer();//上次汇总数据
		StringBuffer nowSql=new StringBuffer();//本次汇总数据
		StringBuffer peopleSql=new StringBuffer();//差异人数
		//分别取得 上期数据集，本期数据集，以及差异人员通过union拼接到一起。
		
		lastSql.append("select ");
		nowSql.append("select ");
		peopleSql.append("select ");
		StringBuffer strSumField=new StringBuffer();//需要求和的字段
		for(LazyDynaBean bean:fieldItems){
			String strField=bean.get("itemid").toString();
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				strSumField.append(" sum( cast ("+strField+" as NUMBER(20,4))) as "+strField+",");
			else
				strSumField.append(" sum( convert (money,"+strField+" )) as "+strField+",");
			peopleSql.append(" count(case when "+Sql_switcher.isnull("t1."+strField, "0")+"<>"+Sql_switcher.isnull("t2."+strField, "0")+" then '1' end) as "+strField+",");//按列统计差异人数 
		}
		strSumField.deleteCharAt(strSumField.length()-1);//去除最后的逗号
		lastSql.append(strSumField);
		nowSql.append(strSumField);
		peopleSql.deleteCharAt(peopleSql.length()-1);//去除最后的逗号
		
		lastSql.append(" from "+tableNameOld+" where 1=1 "+strwhere+" "+deptSql);
		nowSql.append(" from "+tableName+" where 1=1 "+deptSql);
		
		if(!"1".equals(type))//如果是审批 需要增加strWhereNow添加
			nowSql.append(strWhereNow);
		
		peopleSql.append(" from (select A0100,upper(nbase) as nbase,"+strSumField.toString()+" from "+tableName+" where 1=1 "+strWhereNow+"  "+deptSql+" group by A0100,upper(nbase)) t1 full join ");
		peopleSql.append(" (select A0100,upper(nbase) as nbase,"+strSumField.toString()+" from "+tableNameOld+" where 1=1 " +strwhere+"  "+deptSql+" group by A0100,upper(nbase)) t2 on t1.A0100=t2.A0100 and t1.nbase=t2.nbase ");
		
		//差异人数 通过 case when 按列统计
		mainSql.append(lastSql);
		mainSql.append(" union all ");
		mainSql.append(nowSql);
		mainSql.append(" union all ");
		mainSql.append(peopleSql);
		return mainSql.toString();
	}
	
	/***
	 * 取得比对主表格数据
	 * @param strSql
	 * @param fieldItems 待比对指标
	 * @return
	 */
	private ArrayList getDataList(String strSql,ArrayList<LazyDynaBean> fieldItems){
		ArrayList dataList=new ArrayList();
		RowSet rs=null;
		ContentDAO dao = new ContentDAO(this.conn);
		try{
			for(LazyDynaBean bean:fieldItems){//首先插入带比对字段名列
				LazyDynaBean dataBean=new LazyDynaBean();
	    		dataBean.set("itemid",bean.get("itemid").toString());
	    		dataBean.set("itemdesc", bean.get("itemdesc").toString());
				dataBean.set("decwidth",StringUtils.isNotBlank((String)bean.get("decwidth"))?Integer.parseInt((String)bean.get("decwidth")):0);
	    		dataList.add(dataBean);
	    	}
			
		  rs = dao.search(strSql);
		  String [] fieldName={"lastdata","nowdata","peoplenum","margin"};// 先写入itemid itemdesc 后加入这4个(上期数据，本期数据，差异人数，差值)
		  int rowNum=0;
		    while(rs.next())
		    {
		    	for(int i=0;i<dataList.size();i++){//将原本横排显示的数据转换为竖排显示
		    		LazyDynaBean dataBean=(LazyDynaBean)dataList.get(i);
					NumberFormat nf = NumberFormat.getNumberInstance();
		    		int decwidth=(Integer) dataBean.get("decwidth");
		    		if(rowNum==2)//人数保留整数
		    			decwidth=0;
					nf.setMaximumFractionDigits(decwidth);
					nf.setMinimumFractionDigits(decwidth);
					nf.setRoundingMode(RoundingMode.UP);
					
					Double data = convertDouble(rs.getDouble(dataBean.get("itemid").toString()), decwidth);
					dataBean.set(fieldName[rowNum],  nf.format(data));
		    		if(rowNum==0)
		    			this.lastdataSum+=data;//统计总计数据
		    		else if(rowNum==1){
		    			double data_this = convertDouble(Double.parseDouble(dataBean.get(fieldName[0]).toString().replace(",","")), decwidth);
		    			this.nowdataSum+=data;
		    			
		    			BigDecimal bd1 = new BigDecimal(Double.toString(data)); 
		    	        BigDecimal bd2 = new BigDecimal(Double.toString(data_this)); 
		    	        
		    			this.marginSum+=bd1.subtract(bd2).doubleValue();
		    			dataBean.set(fieldName[3], nf.format(bd1.subtract(bd2).doubleValue()));
		    		}
		    	}
		    	rowNum++;
		    }

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null)
					rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		
		
		return dataList;
		
	}
	
	private double convertDouble(double val, int decwidth) {
        BigDecimal bg = new BigDecimal(val);
        return bg.setScale(decwidth, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	/***
	 * 取得主页面工具栏中 差异统计
	 * @param tableName 表名 发放时为 user_salary_id 审批时为salaryhistory
	 * @param appdate
	 * @param count
	 * @param type
	 * @param deptSql 组织机构判断条件
	 * @param ispriv true需要限制用户管理范围 
	 * @return
	 */
	public String titleAmountText(String tableName,String tableNameOld,String type,String deptSql,String strwhere,String strWhereNow){
		
		//关联本次数据和上次数据两表的a0100和nbase字段。取a0100为null即无法关联的行 即为差异人数，没有考虑同a0100不同部门的因素。
		String str="";
		StringBuffer peopleSql=new StringBuffer();//差异人数

		peopleSql.append("select ");
		peopleSql.append(" count(case when t1.A0100 is null then '1' end) as delpeop, count(case when t2.A0100 is null then '1' end) as addpeop ");		
		peopleSql.append(" from (select A0100,upper(nbase) as nbase from "+tableName+" where 1=1 "+strWhereNow+" "+deptSql+" group by A0100,upper(nbase)) t1 full join ");
		peopleSql.append(" (select A0100,upper(nbase) as nbase from "+tableNameOld+" where 1=1 " +strwhere+"  "+deptSql+" group by A0100,upper(nbase)) t2 on t1.A0100=t2.A0100 and t1.nbase=t2.nbase ");
		
		ArrayList dataList=new ArrayList();
		RowSet rs=null;
		ContentDAO dao = new ContentDAO(this.conn);
		try{
		  rs = dao.search(peopleSql.toString());

		    if(rs.next())
		    {
		    		String delpeop=rs.getString("delpeop")==null?"0":rs.getString("delpeop");
		    		String addpeop=rs.getString("addpeop")==null?"0":rs.getString("addpeop");
		    		str=ResourceFactory.getProperty("label.gz.addpeople")+addpeop+"，"+ResourceFactory.getProperty("label.gz.delpeople")+delpeop;//本月新增人员：xx，减少人员：xx
		    }
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null)
					rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return str;
	}
	
	/***
	 * 获取发放日期 次数和权限条件 
	 * @param appdate 发放日期
	 * @param count 发放次数
	 * @param type 1为薪资发放，2为薪资审批
	 * @param UserName
	 * @param ispriv true需要限制用户管理范围 
	 * @return 下标为0 上次发放 为1 本次发放
	 */
	public ArrayList<String> getSqlWhere(String tableName,String appdate,String count,String type,boolean ispriv){

		ArrayList<String> array=new ArrayList<String>();
		String UserName=this.userview.getUserName();
		if(appdate.trim().length()==7)
			appdate=appdate.trim()+"-01";
		appdate=appdate.replace(".", "-");
		
		//拼接where条件
		StringBuffer strWhereNow=new StringBuffer();
		StringBuffer strwhere=new StringBuffer();
		String strDate="";
		strDate=this.getBeforeYWDate(appdate,type);
		
		strwhere.append(" and "+Sql_switcher.dateToChar("A00Z2","YYYY-MM-DD")+"= '"+strDate+"' ");//拼接发放日期 发放次数
		strwhere.append(" and A00Z3='"+count+"' ");
		String privWhlStr = this.salaryTemplateBo.getWhlByUnits("salaryhistory", true);//权限范围内的人员筛选条件
		if(!"1".equals(type)){//薪资审批 若在薪资发放中 当前数据从薪资发放表中取得，故无需下列条件
			
			strWhereNow.append(" and "+Sql_switcher.dateToChar("A00Z2","YYYY-MM-DD")+"= '"+appdate+"'");//拼接发放日期 发放次数
			strWhereNow.append(" and A00Z3='"+count+"' ");
			strWhereNow.append(" and ((((AppUser is null  "+privWhlStr+" ) or AppUser Like '%;"+UserName+";%' )) or curr_user='"+UserName+"') and salaryid="+salaryid);//取得操作人为当前用户
			
		}
		else if(ispriv)
			strWhereNow.append(" "+this.salaryTemplateBo.getWhlByUnits(tableName, true));
		//若从薪资发放进入 账套不共享或是共享管理员的时候，若不走审批。则根据userflag判定数据范围 zhanghua2017-7-10
		if("1".equals(type)){
			if(this.salaryTemplateBo.getManager()!=null&&this.salaryTemplateBo.getManager().length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.salaryTemplateBo.getManager()))//共享非管理员
				strwhere.append(" and ((((AppUser is null  "+privWhlStr+" ) or AppUser Like '%;"+UserName+";%' )) or curr_user='"+UserName+"') and salaryid="+salaryid);
			else
				strwhere.append(" and ((((AppUser is null  and upper(userflag)='"+this.userview.getUserName().toUpperCase()+"' ) or AppUser Like '%;"+UserName+";%' )) or curr_user='"+UserName+"') and salaryid="+salaryid);
		}else
			strwhere.append(" and ((((AppUser is null  "+privWhlStr+" ) or AppUser Like '%;"+UserName+";%' )) or curr_user='"+UserName+"') and salaryid="+salaryid);
		
		array.add(strwhere.toString());
		array.add(strWhereNow.toString());
		return array;
	}
	
	/***
	 * 拼接组织机构的查询条件
	 * @param selectID 所选机构
	 * @param deptid 
	 * @param orgid
	 * @return
	 */
	public String getDeptSqlWhere(String selectID,String deptid,String orgid){
		String [] strOrgId=selectID.split("#");
		
		ArrayList<String> strList=new ArrayList<String>();
		if(strOrgId.length==0)
			return "";
		
		//如果父节点被选中，不再考虑子节点选中状态。
		String str1="null";//当前节点
		String strTemp="";//游标节点
		int i=0;
		while(i<strOrgId.length){
			strTemp=strOrgId[i];
			if("".equals(strTemp.trim())|| "root".equalsIgnoreCase(strTemp.trim())){
				i++;
				continue;
			}
				
			if(strTemp.indexOf(str1)!=0){
				strList.add(strTemp);
				str1=strTemp;
			}
			i++;
		}
		if(strList.size()==0)
			return "";
		
		StringBuffer StrSql=new StringBuffer();
		
		StrSql.append("and (");
		if(deptid.trim().length()==0)
			deptid="e0122";
		if(orgid.trim().length()==0)
			orgid="b0110";
		HashMap orgMap=getOrgMap();
		String field="";
		for(String str:strList){
			if(!"".equals(str.trim())&&!"root".equals(str.trim())){
				if(orgMap.get(str)!=null)
					field=orgid;
				else
					field=deptid;
				 if(Sql_switcher.searchDbServer()==2)
					StrSql.append(" instr("+field+",'"+str+"')=1 or");
				 else 
					StrSql.append(" CHARINDEX('"+str+"',"+field+")=1 or");
			}
				
		}
		
		StrSql=StrSql.delete(StrSql.length()-2, StrSql.length());
		StrSql.append(")");
		return StrSql.toString();
	}
	
	/***
	 * 数据是否存在于归档表
	 * @param strwhere
	 * @return 若历史表有数据 返回历史表，否则如果归档表有数据 返回归档表，都没数据返回历史表
	 */
	public String isArchive(String strwhere){
		RowSet rs=null;
		ContentDAO dao = new ContentDAO(this.conn);
		String tableNameOld="";
		try{
			if(!"".equals(this.tableOld))//若tableOld已经存在值 则取这个值
				return this.tableOld;
			String strSql="select count(1) as num from salaryhistory where 1=1 "+strwhere;
			rs = dao.search(strSql);
			if(rs.next()){
				if(Double.parseDouble(rs.getString("num"))>0)
					tableNameOld="salaryhistory";
				else{
					strSql="select count(1) as num from salaryarchive where 1=1 "+strwhere.replaceAll("salaryhistory", "salaryarchive");
					rs = dao.search(strSql);
					if(rs.next()){
						if(Double.parseDouble(rs.getString("num"))>0)
							tableNameOld="salaryarchive";
						else
							tableNameOld="salaryhistory";
					}
				}
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null)
					rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return tableNameOld;
	}
	/***
	 * 取得单位编码表，判断是否是单位使用
	 * @return
	 */
	private HashMap getOrgMap(){
		HashMap dataMap=new HashMap();
		
		RowSet rs=null;
		ContentDAO dao = new ContentDAO(this.conn);
		try{
			
			rs = dao.search("select CODEITEMID from organization where CODESETID='UN'");
		    while(rs.next())
		    {
		    	dataMap.put(rs.getString("CODEITEMID"), "");

		    }
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null)
					rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return dataMap;
	}
	
	
	/**
	 * 获取上期发放的发放日期
	 * @param ywdate 发放日期
	 * @param type 1薪资发放 2薪资审批
	 * @return
	 */
	public String getBeforeYWDate(String ywdate,String type)
	{
		String date = "";
		RowSet rs=null;
		ContentDAO dao = new ContentDAO(this.conn);
		try{
			ywdate = ywdate.replaceAll("\\.", "-");
			String[] date_arr = ywdate.split("-");

			//若为薪资发放 则上期应为gz_extend_log表中的上次发放日期
			//若为薪资审批 则上期应为salaryhistory或 salaryarchive表中有当前用户参与的最近发放日期。哪个表的日期最近 则取哪个表的数据
			if("1".equals(type)){//薪资发放
				String userName="";
		    	if(this.salaryTemplateBo.getManager().length()==0)
	    		userName=this.userview.getUserName();
		    	else
	    		userName=this.salaryTemplateBo.getManager();
				String sql="select "+Sql_switcher.dateToChar("max(a00z2)","YYYY-MM-DD")+" as a00z2 from gz_extend_log where salaryid="+salaryid+" and "+Sql_switcher.dateToChar("a00z2","YYYY-MM-DD")+"<'"+ywdate+"' and username ='"+userName+"'";
		    	rs=dao.search(sql.toString());
		    	if(rs.next())
			    {
		    		 date=rs.getString("a00z2");
		    	}
			}else{//薪资审批
		    	String UserName=this.userview.getUserName();
		    	String historyDate="",archiveDate="";
		    	String privWhlStr = this.salaryTemplateBo.getWhlByUnits("salaryhistory", true);//权限范围内的人员筛选条件
		    	String str="select "+Sql_switcher.dateToChar("max(a00z2)","YYYY-MM-DD")+" as a00z2 from salaryhistory "
		    			+ "where salaryid="+salaryid+" and "+Sql_switcher.dateToChar("a00z2","YYYY-MM-DD")+"<'"+ywdate+"' "
		    			+ "and ((((AppUser is  null  "+privWhlStr+" ) or AppUser Like '%;"+UserName+";%' )) or curr_user='"+UserName+"')";
		    	rs=dao.search(str);
		    	if(rs.next())
			    {
		    		historyDate=rs.getString("a00z2");
		    	}
		    	rs=dao.search("select "+Sql_switcher.dateToChar("max(a00z2)","YYYY-MM-DD")+" as a00z2 from salaryarchive "
		    			+ "where salaryid="+salaryid+" and "+Sql_switcher.dateToChar("a00z2","YYYY-MM-DD")+"<'"+ywdate+"' "
		    			+ "and ((((AppUser is  null  "+privWhlStr.replaceAll("salaryhistory", "salaryarchive")+" ) or AppUser Like '%;"+UserName+";%' )) or curr_user='"+UserName+"')");
		    	if(rs.next())
			    {
		    		archiveDate=rs.getString("a00z2");
		    	}
		    	if(historyDate==null){
		    		if(archiveDate==null){
		    			this.tableOld="salaryhistory";
		    			date="null";
		    		}else{
		    			this.tableOld="salaryarchive";
		    			date=archiveDate;
		    		}
		    	}else if(archiveDate==null){
		    		this.tableOld="salaryhistory";
	    			date=historyDate;
		    	}else{
			    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
			    	if(sdf.parse(historyDate).getTime()>=sdf.parse(archiveDate).getTime()){
			    		date=historyDate;
			    		this.tableOld="salaryhistory";
			    	}else{
			    		date=archiveDate;
			    		this.tableOld="salaryarchive";
			    	}
		    	}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null)
					rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return date;
	}
	/**
	 * 判断数据库是否存在归属单位，归属部门
	 * @param tableName
	 * @param deptid
	 * @param orgid
	 * @return
	 */
	public String checkOrgField(String tableName ,String deptid,String orgid){
		HashMap dataMap=new HashMap();
		String errorStr="";
		RowSet rowField=null;
		ContentDAO dao = new ContentDAO(this.conn);
		try{
			Boolean isHaveDeptid=false,isHaveOrgid=false;
			if(orgid.trim().length()==0)
				isHaveOrgid=true;
			if(deptid.trim().length()==0)
				isHaveDeptid=true;
			ArrayList strSql=new ArrayList();
			DbWizard dbWizard = new DbWizard(this.conn);
			if (dbWizard.isExistTable(tableName, false)) {
				strSql.add("select * from  "+tableName+" where 1=2");
			}
			if (dbWizard.isExistTable("salaryarchive", false)) {
				strSql.add("select * from  salaryarchive  where 1=2");
			}
			if (dbWizard.isExistTable("salaryhistory", false)) {
				strSql.add("select * from  salaryhistory where 1=2");
			}
			
			int sqlNum=0;
			while((!isHaveDeptid||!isHaveOrgid)&&sqlNum<strSql.size()){
				rowField=dao.search((String)strSql.get(sqlNum));
				ResultSetMetaData metaData=rowField.getMetaData();
				for(int i=1;i<=metaData.getColumnCount();i++){
					String ColumnName=metaData.getColumnName(i).toUpperCase();
					if(deptid.trim().length()>0&&ColumnName.equalsIgnoreCase(deptid))
						isHaveDeptid=true;
					if(orgid.trim().length()>0&&ColumnName.equalsIgnoreCase(orgid))
						isHaveOrgid=true;
				}
				sqlNum++;
				if(!isHaveDeptid||!isHaveOrgid){
					if(!isHaveOrgid)
						errorStr+="归属单位、";
					if(!isHaveDeptid)
						errorStr+="归属部门、";
					break;
				}
					
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rowField!=null)
					rowField.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return "".equals(errorStr)?"":errorStr.substring(0,errorStr.length()-1)+"设置有误，请检查！";
	}
	
	
//	/***
//	 * 主页面显示字段()
//	 * @return
//	 */
//	public  ArrayList<ColumnsInfo> getColumnList(){
//		ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();
//
//		/** 显示 */
//		// 薪资类别(有点击事件)
//		ColumnsInfo cname = getColumnsInfo("itemdesc", "项目名称", 140,0,"M");
//		cname.setRendererFunc("gz_changesmore.renderName");
//		columnTmp.add(cname);
//		columnTmp.add(getColumnsInfo("nowdata", "本期合计", 110,2,"N"));
//		columnTmp.add(getColumnsInfo("lastdata", "上期合计", 110,2,"N"));
//		
//		columnTmp.add(getColumnsInfo("margin", "差异值", 110,2,"N"));
//		columnTmp.add(getColumnsInfo("peoplenum", "差异人数", 100,0,"N"));
//		/** 隐藏 */
//		// 编号
//		ColumnsInfo salaryid_safe = getColumnsInfo("itemid", "id", 0,0,"M");
//		salaryid_safe.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
//		columnTmp.add(salaryid_safe);
//		return columnTmp;
//	}
	/***
	 * 主页面导出excel显示字段
	 * @return
	 */
	public  ArrayList<ColumnsInfo> getColumnListForExc(){
		ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();

		/** 显示 */
		// 薪资类别(有点击事件)
		ColumnsInfo cname = getColumnsInfo("itemdesc", ResourceFactory.getProperty("gz.columns.itemdesc"), 200,0,"M");//项目名称
		columnTmp.add(cname);
		columnTmp.add(getColumnsInfo("nowdata", ResourceFactory.getProperty("gz.columns.nowdataSum"), 160,2,"N"));//本期值
		columnTmp.add(getColumnsInfo("lastdata", ResourceFactory.getProperty("gz.columns.lastdataSum"), 160,2,"N"));//上期值
		columnTmp.add(getColumnsInfo("margin", ResourceFactory.getProperty("gz.columns.marginSum"), 160,2,"N"));//差异值
		columnTmp.add(getColumnsInfo("peoplenum", ResourceFactory.getProperty("gz.columns.peoplenumSum"), 160,0,"N"));//差异人数

		return columnTmp;
	}
	/***
	 * 导出excel字段
	 * @param deptid
	 * @param orgid
	 * @return
	 */
	public  ArrayList<ColumnsInfo> getExcelColumnListDetail(String deptid,String orgid,String onlyname){
		ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();
		deptid=deptid.trim().length()>0?deptid:"e0122";
		orgid=orgid.trim().length()>0?orgid:"b0110";
		/** 显示 */
		// 薪资类别(有点击事件)
		ColumnsInfo orgidCol = new ColumnsInfo();
		orgidCol.setColumnId(orgid);
		orgidCol.setColumnDesc(DataDictionary.getFieldItem(orgid).getItemdesc());//单位名称
		orgidCol.setCodesetId("UN");// 指标集
		orgidCol.setColumnType("A");// 类型N|M|A|D
		orgidCol.setColumnWidth(160);//显示列宽
		orgidCol.setColumnLength(100);// 显示长度 
		orgidCol.setDecimalWidth(0);// 小数位
		orgidCol.setAllowBlank(true);// 编辑时是否可以为空
		orgidCol.setReadOnly(true);// 是否只读
		orgidCol.setFieldsetid(orgid);// 是否从数据字典里来
		orgidCol.setLocked(false);//是否锁列
		columnTmp.add(orgidCol);
		
		
		ColumnsInfo deptidCol = new ColumnsInfo();
		deptidCol.setColumnId(deptid);
		deptidCol.setColumnDesc("e0122".equalsIgnoreCase(deptid)?ResourceFactory.getProperty("gz.columns.e0122Name"):DataDictionary.getFieldItem(deptid).getItemdesc());//部门名称
		deptidCol.setCodesetId("UM");// 指标集
		deptidCol.setColumnType("A");// 类型N|M|A|D
		deptidCol.setColumnWidth(140);//显示列宽
		deptidCol.setColumnLength(100);// 显示长度 
		deptidCol.setDecimalWidth(0);// 小数位
		deptidCol.setAllowBlank(true);// 编辑时是否可以为空
		deptidCol.setReadOnly(true);// 是否只读
		deptidCol.setFieldsetid(deptid);// 是否从数据字典里来
		deptidCol.setLocked(false);//是否锁列
		columnTmp.add(deptidCol);
		if(!"".equals(onlyname.trim())){
			ColumnsInfo Col1 = new ColumnsInfo();
			FieldItem field=DataDictionary.getFieldItem(onlyname);
			Col1.setColumnId(onlyname);
			Col1.setColumnDesc(field.getItemdesc());//工号
			Col1.setCodesetId(field.getCodesetid());// 指标集
			Col1.setColumnType(field.getItemtype());// 类型N|M|A|D
			Col1.setColumnWidth(110);//显示列宽
			Col1.setColumnLength(100);// 显示长度
			Col1.setDecimalWidth(field.getDecimalwidth());// 小数位
			Col1.setAllowBlank(true);// 编辑时是否可以为空
			Col1.setReadOnly(true);// 是否只读
			Col1.setFieldsetid(onlyname);// 是否从数据字典里来
			Col1.setLocked(false);//是否锁列
			columnTmp.add(Col1);
		}
		ColumnsInfo col2 = new ColumnsInfo();
		col2.setColumnId("a0101");
		col2.setColumnDesc(DataDictionary.getFieldItem("a0101").getItemdesc());//姓名
		col2.setCodesetId("");// 指标集
		col2.setColumnType("A");// 类型N|M|A|D
		col2.setColumnWidth(110);//显示列宽
		col2.setColumnLength(100);// 显示长度 
		col2.setDecimalWidth(0);// 小数位
		col2.setAllowBlank(true);// 编辑时是否可以为空
		col2.setReadOnly(true);// 是否只读
		col2.setFieldsetid("a0101");// 是否从数据字典里来
		col2.setLocked(false);//是否锁列
		columnTmp.add(col2);
		columnTmp.add(getColumnsInfo("nowdata", ResourceFactory.getProperty("gz.columns.nowdata"), 110,2,"N"));//本期值
		columnTmp.add(getColumnsInfo("lastdata", ResourceFactory.getProperty("gz.columns.lastdata"), 110,2,"N"));//上期值
		
		columnTmp.add(getColumnsInfo("difference", ResourceFactory.getProperty("gz.columns.margin"), 110,2,"N"));//差异值
		return columnTmp;
	}
	/***
	 * 明细表头字段
	 * @param deptid
	 * @param orgid
	 * @return
	 */
	public  HashMap getColumnListDetail(String deptid,String orgid,String onlyname){
		/**
		 * xiegh
		 * bug 26875
		 * name与value 区分大小写不一，导致前台数据显示 不全
		 * deptid.toLowerCase()
		 * orgid.toLowerCase()
		 * 2017/04/05
		 */
		deptid=deptid.trim().length()>0?deptid.toLowerCase():"e0122";
		orgid=orgid.trim().length()>0?orgid.toLowerCase():"b0110";
		HashMap map = new HashMap();
		try{
			StringBuffer fields = new StringBuffer();//显示字段
			fields.append("[");
			StringBuffer str = new StringBuffer();//column
			str.append("[");

			fields.append("'"+orgid+"'");
			str.append("{");
			str.append("text: '"+DataDictionary.getFieldItem(orgid).getItemdesc()+"',");
			str.append("width:160,");
			str.append("remoteSort: true,");
			str.append("align:'left',");
			str.append("dataIndex: '"+orgid+"' ");
			str.append("}");
			
			String deptname = "e0122".equalsIgnoreCase(deptid)?ResourceFactory.getProperty("gz.columns.e0122Name"):DataDictionary.getFieldItem(deptid).getItemdesc();
			fields.append(",'"+deptid+"'");
			str.append(",{");
			str.append("text: '"+deptname+"',");
			str.append("width:140,");
			str.append("remoteSort: true,");
			str.append("align:'left',");
			str.append("dataIndex: '"+deptid+"' ");
			str.append("}");
			
			if(!"".equals(onlyname.trim())){
				FieldItem field=DataDictionary.getFieldItem(onlyname);
				fields.append(",'"+onlyname+"'");
				str.append(",{");
				str.append("text: '"+field.getItemdesc()+"',");
				str.append("width:110,");
				str.append("remoteSort: true,");
				str.append("align:'left',");
				str.append("dataIndex: '"+onlyname+"' ");
				str.append("}");
			}
			fields.append(",'a0101'");
			str.append(",{");
			str.append("text: '"+DataDictionary.getFieldItem("a0101").getItemdesc()+"',");
			str.append("width:110,");
			str.append("remoteSort: true,");
			str.append("align:'left',");
			str.append("dataIndex: 'a0101' ");
			str.append("}");
			
//			fields.append(",'nowdata'");
//			str.append(",{");
//			str.append("text: '"+ResourceFactory.getProperty("gz.columns.nowdata")+"',");
//			str.append("width:110,");
//			str.append("remoteSort: true,");
//			str.append("align:'left',");
//			str.append("dataIndex: 'nowdata' ");
//			str.append("}");
			
			fields.append(",'nowdata'");
			str.append(",{");
			str.append("text: '"+ResourceFactory.getProperty("gz.columns.nowdata")+"',");
			str.append("width:110,");
			str.append("remoteSort: true,");
			str.append("align:'right',");
			str.append("dataIndex: 'nowdata' ");
			str.append("}");
			
			fields.append(",'lastdata'");
			str.append(",{");
			str.append("text: '"+ResourceFactory.getProperty("gz.columns.lastdata")+"',");
			str.append("width:110,");
			str.append("remoteSort: true,");
			str.append("align:'right',");
			str.append("dataIndex: 'lastdata' ");
			str.append("}");
			
			fields.append(",'difference'");
			str.append(",{");
			str.append("text: '"+ResourceFactory.getProperty("gz.columns.margin")+"',");
			str.append("width:110,");
			str.append("remoteSort: true,");
			str.append("align:'right',");
			str.append("dataIndex: 'difference' ");
			str.append("}");

			str.append("]");
			fields.append(",'TABLETYPE'");
			fields.append("]");
			
			map.put("column", str.toString());
			map.put("fields", fields.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 
	 * @Title: getOrderby   
	 * @Description:根据前台正倒序获取order by
	 * @param @param sort
	 * @param @return 
	 * @return String 
	 * @author:zhaoxg   
	 * @throws
	 */
	public String getOrderby(String sort,String tablename){
		String order = " order by dbid ,a0000,b0110,e0122 ";
		try{
			String str = ",B0110,E0122,E01A1,";//单位 部门 岗位 排序按照a0000
			if(sort!=null){
				sort=PubFunc.hireKeyWord_filter_reback(sort);
				JSONArray arry = JSONArray.fromObject(sort);
				JSONObject jsonObject = arry.getJSONObject(0);
				HashMap<String, String> sortmap = new HashMap<String, String>();
				for (Iterator<?> iter = jsonObject.keys(); iter.hasNext();)
				{
					String key = (String) iter.next();
					String value = jsonObject.get(key).toString();
					sortmap.put(key, value);
				}
				if(str.indexOf(","+sortmap.get("property").toUpperCase()+",")!=-1){
					order = " left join organization on organization.codeitemid="+tablename+"."+sortmap.get("property")+" order by organization.A0000 "+sortmap.get("direction");
				}else{
					order = " order by "+ sortmap.get("property")+" "+sortmap.get("direction");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return order;
	}
	/**
	 * 列头ColumnsInfo对象初始化
	 * @param columnId id
	 * @param columnDesc 名称
	 * @param columnWidth 显示列宽
	 * @param DecimalWidth 小数位
	 * @return
	 */
	private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth, int DecimalWidth,String ColumnType){
		
		ColumnsInfo columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId(columnId);
		columnsInfo.setColumnDesc(columnDesc);
		columnsInfo.setCodesetId("");// 指标集
		columnsInfo.setColumnType(ColumnType);// 类型N|M|A|D
		columnsInfo.setColumnWidth(columnWidth);//显示列宽
		columnsInfo.setColumnLength(100);// 显示长度 
		columnsInfo.setDecimalWidth(DecimalWidth);// 小数位
		columnsInfo.setAllowBlank(true);// 编辑时是否可以为空
		columnsInfo.setReadOnly(true);// 是否只读
		columnsInfo.setTextAlign("right");
		columnsInfo.setFromDict(false);// 是否从数据字典里来
		columnsInfo.setLocked(false);//是否锁列
		
		return columnsInfo;
	}
	/***
	 * 构建机构树sql 机构树所取范围为在比对数据中存在人员的所属机构
	 * @param tableName 审批时为salaryhistory
	 * @param type 1薪资发放 2薪资审批
	 * @param appdate 发放日期
	 * @param count 发放次数
	 * @param UserName
	 * @param parentid 父节点id 根节点为root
	 * @param ispriv true需要限制用户管理范围 
	 * @return
	 */
	public String getMainTreeDataSql(String tableName,String type,String appdate,String count,String parentid,boolean ispriv,String deptid,String orgid){
		String strDate="";
		String UserName=this.userview.getUserName();
		//取得发放日期 次数和权限条件
		ArrayList<String> arrStr=this.getSqlWhere(tableName,appdate, count, type,ispriv);
		String strwhere=arrStr.get(0);//本次发放日期
		String strWhereNow=arrStr.get(1);//上次发放日期
		String tableNameOld=this.isArchive(strwhere);
		if("salaryarchive".equalsIgnoreCase(tableNameOld))
			strwhere=strwhere.replaceAll("salaryhistory", "salaryarchive");
		String addStr="";
		if("root".equals(parentid.trim()))//root时 为寻找根节点
			addStr="parentid=codeitemid";
		else
			addStr=" parentid='"+parentid+"' and codeitemid<>'"+parentid+"'";//寻找子节点

		StringBuffer strSql=new StringBuffer();
		strSql.append("select  distinct org1.codeitemid,org1.codeitemdesc,org1.codesetid from (select codeitemid,codeitemdesc,codesetid from organization where "+addStr+" )org1 INNER join (");
		
		//取需比对数据集中存在的人员的所属机构
		strSql.append("select (case when "+Sql_switcher.isnull(deptid, "' '")+"<>' ' then "+deptid+" else "+orgid+" end) as codeitemid   from "+tableName+" where 1=1 "+strWhereNow.toString()+" GROUP BY "+deptid+","+orgid+" ");
		strSql.append(" UNION select (case when "+Sql_switcher.isnull(deptid, "' '")+"<>' ' then "+deptid+" else "+orgid+" end) as codeitemid from "+tableNameOld+" where 1=1 "+strwhere+" GROUP BY "+deptid+" ,"+orgid+" ");
		
		if(Sql_switcher.searchDbServer()==2)//是oracle
			strSql.append(") a  on instr(a.codeitemid,org1.codeitemid)=1 ");
		else 
			strSql.append(") a  on CHARINDEX(org1.codeitemid,a.codeitemid)=1 ");

		return strSql.toString();
		
	}
	/***
	 * 获取机构树数据集
	 * @param strSql
	 * @return
	 */
	public ArrayList getTreeData(String strSql,String node){
		ArrayList dataList=new ArrayList();
		RowSet rs=null;
		ContentDAO dao = new ContentDAO(this.conn);
		try{
		  rs = dao.search(strSql);

		    while(rs.next())
		    {
		    		HashMap map=new HashMap();
		    		map.put("id", rs.getString("codeitemid"));
		    		map.put("text", rs.getString("codeitemdesc"));
		    		map.put("checked", false);//第一次加载的时候为非选中状态
		    		if("un".equals(rs.getString("codesetid").toString().toLowerCase()))
		    			map.put("icon", "/images/unit.gif");
		    		else if("um".equals(rs.getString("codesetid").toString().toLowerCase()))
		    			map.put("icon", "/images/dept.gif");
		    		dataList.add(map);
		    }
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null)
					rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		
		
		return dataList;
	}
	
	/**
	 * 获取薪资发放界面功能按钮
	 * @param type 0 主页面 1 明细页面
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getButtons(int type) throws GeneralException{
		ArrayList buttonList = new ArrayList();
		try{
//			if(type==0){
//				buttonList.add(new ButtonInfo("导出Excel","gz_changesmore.exportData"));//导出()
//			}else
				if(type==1){
				buttonList.add(new ButtonInfo(ResourceFactory.getProperty("menu.gz.export")+"Excel","gz_changesmore.exportDetailData"));//导出
			}
			//buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.return"),"gz_changesmore.returnBack"));//返回
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return buttonList;
	}
	

	
	/**
	 * 取得变动比对明细表数据
	 * @param UserName
	 * @param salaryid
	 * @param appdate 发放日期
	 * @param count 发放次数
	 * @param type type 1为薪资发放，2为薪资审批
	 * @param fieldItem 待比对指标
	 * @param deptSql 部门过滤条件
	 * @param deptid 所属部门
	 * @param orgid 所属单位
	 * @param ispriv true需要限制用户管理范围 
	 * @return
	 */
	public HashMap getChangeDetailsDataList(String tableName,String appdate,String count,String type,String fieldItem,
			String deptSql,String deptid,String orgid,boolean ispriv,String onlyname,int page,int limit,String sort){
		HashMap map = new HashMap();
		ArrayList dataList=new ArrayList();
		String onlynameStr="";
		//appdate需要格式化
		if(appdate.trim().length()==0)
			return null;
		//取得发放日期 次数和userName条件
		ArrayList<String> arrStr=this.getSqlWhere(tableName,appdate, count, type,ispriv);
		String strwhere=arrStr.get(0);//上次发放日期
		String strWhereNow=arrStr.get(1);//本次发放日期
		StringBuffer mainSql=new StringBuffer();
		deptid=deptid.trim().length()>0?deptid:"e0122";
		orgid=orgid.trim().length()>0?orgid:"b0110";
		String tableNameOld=this.isArchive(strwhere);
		int decwidth=2;
		NumberFormat nf = NumberFormat.getNumberInstance();
		FieldItem item= DataDictionary.getFieldItem(fieldItem);
		if(item!=null){
			decwidth=item.getDecimalwidth();
		}

		if("salaryarchive".equalsIgnoreCase(tableNameOld))
			strwhere=strwhere.replaceAll("salaryhistory", "salaryarchive");
		
		mainSql.append("select ");
		mainSql.append(" case when t1.dbid is null then "+Sql_switcher.isnull("t2.dbid", "0")+" else t1.dbid end as dbid, ");//默认排序使用 zhanghua 2017-4-13
		mainSql.append(" case when t1.a0000 is null then "+Sql_switcher.isnull("t2.a0000", "0")+" else t1.a0000 end as a0000, ");//默认排序使用
		if(!"".equals(onlyname.trim())){
			mainSql.append(" case when t1."+onlyname+" is null then "+Sql_switcher.isnull("t2."+onlyname, "' '")+" else t1."+onlyname+" end as "+onlyname+", ");
			onlynameStr="max("+onlyname+") as "+onlyname+",";
		}
		mainSql.append(" case when t1."+deptid+" is null then "+Sql_switcher.isnull("t2."+deptid, "' '")+" else t1."+deptid+" end as "+deptid+", ");
		mainSql.append(" case when t1."+orgid+" is null then  "+Sql_switcher.isnull("t2."+orgid, "' '")+" else t1."+orgid+" end as "+orgid+", ");
		mainSql.append(" case when t1.a0101 is null then "+Sql_switcher.isnull("t2.a0101", "' '")+" else t1.a0101 end as a0101, ");
		mainSql.append(" t2."+fieldItem+" as lastdata,t1."+fieldItem+" as nowdata,"+Sql_switcher.isnull("t1."+fieldItem, "0")+"-"+Sql_switcher.isnull("t2."+fieldItem, "0")+" as difference ");
		mainSql.append(" from (select max(dbid) as dbid,max(a0000) as a0000,"+onlynameStr+" A0100,upper(nbase) as nbase,max("+deptid+") as "+deptid+",max("+orgid+") as "+orgid+",max(a0101) as a0101,sum("+fieldItem+") as "+fieldItem+"  from "+tableName);
		mainSql.append(" where "+fieldItem+"<>0 "+strWhereNow+" "+deptSql+" group by A0100,upper(nbase)) t1"); 
		mainSql.append(" full join ");
		mainSql.append(" (select max(dbid) as dbid,max(a0000) as a0000,"+onlynameStr+" A0100,upper(nbase) as nbase,max("+deptid+") as "+deptid+",max("+orgid+") as "+orgid+",max(a0101) as a0101,sum("+fieldItem+") as "+fieldItem+" from "+tableNameOld+"  ");
		mainSql.append(" where "+fieldItem+"<>0  "+strwhere+" "+deptSql+" group by A0100,upper(nbase)) t2 ");
		mainSql.append(" on t1.A0100=t2.A0100 and t1.nbase=t2.nbase where "+Sql_switcher.isnull("t1."+fieldItem, "0")+"<>"+Sql_switcher.isnull("t2."+fieldItem, "0"));
		RowSet rs=null;
		ContentDAO dao = new ContentDAO(this.conn);
		try{
			String order = this.getOrderby(sort,"cgtable");
			order = order.replace("b0110", orgid).replace("e0122", deptid);
			rs = dao.search("select * from ("+mainSql.toString()+") cgtable "+order,limit,page);
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(StringUtils.isBlank(display_e0122)|| "00".equals(display_e0122))
				display_e0122="0";	
		    while(rs.next())
		    {
	    		LazyDynaBean dataBean=new LazyDynaBean();	
	    		dataBean.set(deptid.toLowerCase(), AdminCode.getCodeName("UM", rs.getString(deptid)));
	    		dataBean.set(orgid.toLowerCase(), AdminCode.getCodeName("UN", rs.getString(orgid)));
	    		if(!"".equals(onlyname.trim())){
	    			FieldItem field=DataDictionary.getFieldItem(onlyname);
	    			if("0".equalsIgnoreCase(field.getCodesetid()))
	    				dataBean.set(onlyname,rs.getString(onlyname));
	    			else
	    				dataBean.set(onlyname, AdminCode.getCodeName(field.getCodesetid(), rs.getString(onlyname)));
	    		}

				nf.setMaximumFractionDigits(decwidth);
				nf.setMinimumFractionDigits(decwidth);
				nf.setRoundingMode(RoundingMode.UP);
	    		dataBean.set("a0101", rs.getString("a0101"));
	    		dataBean.set("lastdata", nf.format(rs.getDouble("lastdata")));
	    		dataBean.set("nowdata", nf.format(rs.getDouble("nowdata")));
	    		dataBean.set("difference", nf.format(rs.getDouble("difference")));
	    		dataList.add(dataBean);
		    }
			int totalCount = 0;//总条数
			RowSet ros = dao.search("select count(*) as ncount from ("+mainSql+") t");
			if(ros.next()){
				totalCount = ros.getInt("ncount");
			}
			map.put("totalCount", totalCount);
		    map.put("dataList", dataList);
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null)
					rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return map;
	}
	/**
	 * 取得变动比对明细表数据
	 * @param UserName
	 * @param salaryid
	 * @param appdate 发放日期
	 * @param count 发放次数
	 * @param type type 1为薪资发放，2为薪资审批
	 * @param fieldItems 待比对指标
	 * @param deptSql 部门过滤条件
	 * @param deptid 所属部门
	 * @param orgid 所属单位
	 * @param ispriv true需要限制用户管理范围 
	 * @return
	 */
	public ArrayList getChangeDetailsDataList(String tableName,String appdate,String count,String type,String fieldItem,
			String deptSql,String deptid,String orgid,boolean ispriv,String onlyname){
		ArrayList dataList=new ArrayList();
		String onlynameStr="";
		//appdate需要格式化
		if(appdate.trim().length()==0)
			return null;
		//取得发放日期 次数和userName条件
		ArrayList<String> arrStr=this.getSqlWhere(tableName,appdate, count, type,ispriv);
		String strwhere=arrStr.get(0);//上次发放日期
		String strWhereNow=arrStr.get(1);//本次发放日期
		StringBuffer mainSql=new StringBuffer();
		deptid=deptid.trim().length()>0?deptid:"e0122";
		orgid=orgid.trim().length()>0?orgid:"b0110";
		String tableNameOld=this.isArchive(strwhere);
		mainSql.append("select ");
		mainSql.append(" case when t1.dbid is null then "+Sql_switcher.isnull("t2.dbid", "0")+" else t1.dbid end as dbid, ");//默认排序使用 zhanghua 2017-4-13
		mainSql.append(" case when t1.a0000 is null then "+Sql_switcher.isnull("t2.a0000", "0")+" else t1.a0000 end as a0000, ");//默认排序使用
		if(!"".equals(onlyname.trim())){
			mainSql.append(" case when t1."+onlyname+" is null then "+Sql_switcher.isnull("t2."+onlyname, "' '")+" else t1."+onlyname+" end as "+onlyname+", ");
			onlynameStr="max("+onlyname+") as "+onlyname+",";
		}
		mainSql.append(" case when t1."+deptid+" is null then "+Sql_switcher.isnull("t2."+deptid, "' '")+" else t1."+deptid+" end as "+deptid+", ");
		mainSql.append(" case when t1."+orgid+" is null then  "+Sql_switcher.isnull("t2."+orgid, "' '")+" else t1."+orgid+" end as "+orgid+", ");
		mainSql.append(" case when t1.a0101 is null then "+Sql_switcher.isnull("t2.a0101", "' '")+" else t1.a0101 end as a0101, ");
		mainSql.append(" t2."+fieldItem+" as lastdata,t1."+fieldItem+" as nowdata,"+Sql_switcher.isnull("t1."+fieldItem, "0")+"-"+Sql_switcher.isnull("t2."+fieldItem, "0")+" as difference ");
		mainSql.append(" from (select  max(dbid) as dbid,max(a0000) as a0000 ,"+onlynameStr+" A0100,upper(nbase) as nbase,max("+deptid+") as "+deptid+",max("+orgid+") as "+orgid+",max(a0101) as a0101,sum("+fieldItem+") as "+fieldItem+"  from "+tableName);
		mainSql.append(" where "+fieldItem+"<>0 "+strWhereNow+" "+deptSql+" group by A0100,upper(nbase)) t1"); 
		mainSql.append(" full join ");
		mainSql.append(" (select max(dbid) as dbid,max(a0000) as a0000,"+onlynameStr+" A0100,upper(nbase) as nbase,max("+deptid+") as "+deptid+",max("+orgid+") as "+orgid+",max(a0101) as a0101,sum("+fieldItem+") as "+fieldItem+" from "+tableNameOld+"  ");
		mainSql.append(" where "+fieldItem+"<>0  "+strwhere+" "+deptSql+" group by A0100,upper(nbase)) t2 ");
		mainSql.append(" on t1.A0100=t2.A0100 and t1.nbase=t2.nbase where "+Sql_switcher.isnull("t1."+fieldItem, "0")+"<>"+Sql_switcher.isnull("t2."+fieldItem, "0"));
		RowSet rs=null;
		ContentDAO dao = new ContentDAO(this.conn);
		try{
			rs = dao.search("select * from ("+mainSql.toString()+" ) cgtable order by dbid ,a0000,"+orgid+","+deptid);
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(StringUtils.isBlank(display_e0122)|| "00".equals(display_e0122))
				display_e0122="0";	
		    while(rs.next())
		    {
	    		LazyDynaBean dataBean=new LazyDynaBean();	
	    		dataBean.set(deptid.toLowerCase(), AdminCode.getCodeName("UM", rs.getString(deptid)));
	    		dataBean.set(orgid.toLowerCase(), AdminCode.getCodeName("UN", rs.getString(orgid)));
	    		if(!"".equals(onlyname.trim())){
	    			FieldItem field=DataDictionary.getFieldItem(onlyname);
	    			if(!"0".equals(field.getCodesetid())){//xiegh 20170411  26805
	    				dataBean.set(onlyname, AdminCode.getCodeName(field.getCodesetid(), rs.getString(onlyname)));
	    			}else{
	    				dataBean.set(onlyname,rs.getString(onlyname));
	    			}
	    		}
	    		dataBean.set("a0101", rs.getString("a0101"));
	    		dataBean.set("lastdata", rs.getDouble("lastdata"));
	    		dataBean.set("nowdata", rs.getDouble("nowdata"));
	    		dataBean.set("difference", rs.getDouble("difference"));
	    		dataList.add(dataBean);
		    }
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null)
					rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return dataList;
	}
	/**
	 * 判断唯一性指标是否包含在薪资项目里
	 * @param onlyname 唯一性指标
	 * @param tableName 
	 * @return 存在则返回唯一性指标，不存在则返回空字符
	 */
	public String getOnlyName(String onlyname){
		RowSet rs=null;
		ContentDAO dao = new ContentDAO(this.conn);
		try{
			String strSql="select 1 from salaryset  where salaryid="+this.salaryid +" and itemid='"+onlyname.toUpperCase()+"'";
			rs = dao.search(strSql);
			if(!rs.next())
				onlyname="";

		}catch(Exception e)
		{
			onlyname="";
		}finally{
			try
			{
				if(rs!=null)
					rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return onlyname;
	}
	
	/**
	 * 和上期数据比对，按照栏目设置顺序显示
	 * @param itemSetList
	 * @param schemeId
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList<LazyDynaBean> getSchemedHeadItemList_simpleSort(ArrayList<LazyDynaBean> itemSetList, int schemeId) throws GeneralException {
		ArrayList<LazyDynaBean> itemList = new ArrayList<LazyDynaBean>();
		HashMap<Integer, LazyDynaBean> map_lazy=new HashMap<Integer, LazyDynaBean>();
		RowSet rowSet = null;
		try {
			
			HashMap<String, Integer> map=new HashMap<String, Integer>();
			ArrayList<HashMap> filedList = new ArrayList<HashMap>();
			ContentDAO dao = new ContentDAO(this.conn);
			//查出栏目设置的锁的顺序，用Map
			StringBuffer sql = new StringBuffer("select itemid,displayorder from t_sys_table_scheme_item  where  scheme_id =? order by displayorder");
			ArrayList list = new ArrayList();
			list.add(schemeId);
			// 从表t_sys_table_scheme_item中查询itemid
			rowSet = dao.search(sql.toString(), list);
			while (rowSet.next()) {
				map.put((String) rowSet.getString("itemid").toLowerCase(), rowSet.getInt("displayorder"));
			}
			
			//排去所有非数值型的字段，并将对应的bean按照（顺序号，bean）塞入map
			ArrayList<LazyDynaBean> itemSetListClone = (ArrayList<LazyDynaBean>) itemSetList.clone();
			for(int i = 0; i<itemSetList.size(); i++){
				LazyDynaBean bean=itemSetList.get(i);
				String itemid = (String)bean.get("itemid");
				if(map.get(itemid.toLowerCase()) != null && !"3".equals(bean.get("initflag").toString())&& "N".equals(bean.get("itemtype").toString())) {
					map_lazy.put((int)map.get(itemid.toLowerCase()), bean);
				}
			}
			//根据顺序，将bean塞入list中
			for(int i = 0; i < itemSetList.size(); i++) {
				if(map_lazy.get(i) != null) {
					itemList.add((LazyDynaBean)map_lazy.get(i));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return itemList;
	}
	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public int getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(int salaryid) {
		this.salaryid = salaryid;
	}

	public UserView getUserview() {
		return userview;
	}

	public void setUserview(UserView userview) {
		this.userview = userview;
	}

	public SalaryAccountBo getSalaryAccountBo() {
		return salaryAccountBo;
	}

	public void setSalaryAccountBo(SalaryAccountBo salaryAccountBo) {
		this.salaryAccountBo = salaryAccountBo;
	}

	public SalaryTemplateBo getSalaryTemplateBo() {
		return salaryTemplateBo;
	}

	public void setSalaryTemplateBo(SalaryTemplateBo salaryTemplateBo) {
		this.salaryTemplateBo = salaryTemplateBo;
	}


}
