/**
 * 
 */
package com.hjsj.hrms.businessobject.gz.templateset.tax_table;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:CalcTaxBo</p> 
 *<p>Description:税表计算业务类</p>
  *<p>税率表计算算法说明
  *计税方式：
  *1.工资薪金，自动合并计税
  *2.全年一次性奖金计税方式：总额/12，再套用税率表,不用减基数，同一计税时间的全年一次性奖金，合并计税
  *3.劳务报酬，合并计税
  *合并计税计算过程：一个人有多条记录时，按归属日期和次数升序排序，依次计算所得税，计算完一条记录后，把结果保存
  *到个税明细表，再计算一下条记录。
  *同一个计税时间、同一个计税方式，才合并计税，相同归属日期，应纳税所得额汇总
  *
  *
  *
  *残疾人算税规则
  *1月残疾人减免 = 1月份个税/2             说明：1月份个税四舍五入。1月份个税/2   截取小数点后两位，不做四舍五入
1月份残疾人缴纳个税 = 1月份个税  -  1月残疾人减免


2月份残疾人减免 = 2月份累计个税/2 - 1月份残疾人减免       说明：2月份累计个税四舍五入。2月份累计个税/2  截取小数点后两位，不做四舍五入
2月份残疾人缴纳个税 = 2月份个税  -  2月份残疾人减免


3月份残疾人减免 = 3月份累计个税/2  -  1月份残疾人减免 - 2月份残疾人减免   说明：3月份累计个税四舍五入。3月份累计个税/2  截取小数点后两位，不做四舍五入
3月份残疾人缴纳个税 = 3月份个税  -  3月份残疾人减免
  *
  *
  *
  *
  *</p>
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-9-5:下午02:02:33</p> 
 *@author cmq
 *@version 4.0
 */
public class CalcTaxBo {
	/**税表号*/
	private int taxid;
	/**薪资类别号*/
	private int salaryid;
	/**数据库连接*/
	private Connection conn;
	/**计税时间指标*/
	private String tax_date_item;
	/**报税时间指标*/
	private String declare_date_item;
	/**计算方式指标*/
	private String tax_mode_item;
	/**发薪标识指标**/
	private String pay_flag_item;
	/**应纳税额指标*/
	private String ynse_item;
	/** 0正算,1反算*/
	private String model="0";
	/**纳税项目说明*/
	private String desc_item;
	/**
	 * 包含如下属性
	 * hzName,itemname,useflag,itemid,rexpr,cond,standid,itemtype,runflag
	 */
	private DynaBean dbean;
	/**税率表扣减基数值*/
	private double k_base;
	/**登录用户*/
	private UserView userview;	
	/**计税方式
	 *1:工资薪金
	 *2：全年一次性奖金
	 *3:企业年金
	 *4：劳务报酬
	 *5:累计预扣法 
	 */
	private String tax_mode;
	
	/** 计税单位 */
	private String tax_unit;
	
	

	/** 入职时间  */
	private String hiredate;
	/** 是否残疾人 */
	private String disability;
	//残疾生效时间 
	private String disability_date="";
	
	/** 减征比例 */
	private double minus_percent=1.0;
	
	
	
	/**计算过程中的临时表1，临时表2*/
	private String tmp1_name;
	private String tmp2_name;
	/**处理的薪资表*/
	private String gz_tablename;
	/**薪资类别对象*/
	SalaryCtrlParamBo ctrlbo;
	/**个税表定义的字段*/
	private ArrayList taxfldlist;
	/** 算税时是否清空项目 */
	private String isClearItem="1";
	private String errorInfo="";
	/**薪资类别数据对象*/
	private RecordVo templatevo=null; 
	private String   manager="";
	private SalaryCtrlParamBo ctrlparam=null;
	
	/**所得税管理是否根据指定的dept_id字段进行权限控制*/
	private boolean isDeptControl=false;
	
	private String controlByUnitcod="0";
	
	private String calculation="M"; //计税方式默认按月 ，Y：按年累计
	
	
	/**
	 * @param salaryid   薪资类别号
	 */
	public CalcTaxBo(int salaryid, Connection conn,UserView userview) throws GeneralException{
		super();
		try
		{
			
			this.conn = conn;
			this.salaryid=salaryid;
			this.userview=userview;
			tmp1_name="t#"+userview.getUserName()+"_gz_3"; //userview.getUserName()+"tax_temp1";
			tmp2_name="t#"+userview.getUserName()+"_gz_4"; //userview.getUserName()+"tax_temp2";	
			
			templatevo=new RecordVo("salarytemplate");
			templatevo.setInt("salaryid", this.salaryid);
			ContentDAO dao=new ContentDAO(this.conn);
			templatevo=dao.findByPrimaryKey(templatevo);
			ctrlparam=new SalaryCtrlParamBo(this.conn,this.salaryid);
			this.manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			if(this.manager.length()==0)
				this.gz_tablename=this.userview.getUserName()+"_salary_"+this.salaryid;
			else
				this.gz_tablename=this.manager+"_salary_"+this.salaryid;
			
		//	this.gz_tablename=this.userview.getUserName()+"_salary_"+this.salaryid;
			ctrlbo=new SalaryCtrlParamBo(this.conn,this.salaryid);
			tax_date_item=ctrlbo.getValue(SalaryCtrlParamBo.TAX_DATE_FIELD);
			declare_date_item=ctrlbo.getValue(SalaryCtrlParamBo.DECLARE_TAX);
			pay_flag_item=ctrlbo.getValue(SalaryCtrlParamBo.PAY_FLAG);
			
			if(declare_date_item==null||declare_date_item.length()==0)
				errorInfo="该类别属性中报税时间指标未指定!";
			
			if(tax_date_item==null||tax_date_item.length()==0)
				errorInfo="该类别属性中计税时间指标未指定!";
			
			tax_mode_item=ctrlbo.getValue(SalaryCtrlParamBo.TAX_MODE);
			desc_item=ctrlbo.getValue(SalaryCtrlParamBo.TAX_DESC);
			tax_unit=ctrlbo.getValue(SalaryCtrlParamBo.TAX_UNIT);
			
			hiredate=ctrlbo.getValue(SalaryCtrlParamBo.HIRE_DATE); 
			disability=ctrlbo.getValue(SalaryCtrlParamBo.DISABILITY); 
			if(disability!=null&&disability.trim().length()>0)
			{
				minus_percent=Double.parseDouble(((String)ctrlbo.getValue(SalaryCtrlParamBo.DISABILITY,"percent")).trim())/100.0;
				if(SystemConfig.getPropertyValue("disability_date")!=null)
					disability_date=SystemConfig.getPropertyValue("disability_date"); //残疾生效时间
			
			}
			
			
			
			/**自动创建个税明细表*/
			createTaxDetails();
			/**个税明细表动态指标*/
			TaxMxBo mxbo=new TaxMxBo(this.conn,this.userview);
			taxfldlist=mxbo.searchDynaItemList();
			
			if("true".equalsIgnoreCase(mxbo.getDeptID()))
				this.isDeptControl=true;
			
			String _controlByUnitcode="0";
			String orgid = this.ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");
			orgid = orgid != null ? orgid : "";
			String deptid = this.ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
			deptid = deptid != null ? deptid : "";
			if(this.manager!=null&&this.manager.trim().length()>0)
			{
				if(!this.userview.getUserName().equalsIgnoreCase(this.manager))
				{
					if(orgid.length()>0||deptid.length()>0)
						_controlByUnitcode="1";
				}
			}
			this.controlByUnitcod=_controlByUnitcode;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 
	 */
	public CalcTaxBo(Connection conn,UserView userview)
	{
		super();
		this.conn = conn;
		this.userview=userview;		
	}
	
	
	/**
	 * 取得计税参数
	 * @param dbean      计算公式对象	 
	 */
	private void init(DynaBean dbean)
	{
		try
		{
			this.dbean = dbean;
			this.taxid=Integer.parseInt((String)dbean.get("standid"));	
			ynse_item=ctrlbo.getValue(SalaryCtrlParamBo.YS_FIELDITEM,"id",(String)dbean.get("itemid"));
			
			if(ynse_item==null||ynse_item.trim().length()==0)
				errorInfo="应纳税额指标没指定!";
			
			this.model=ctrlbo.getValue(SalaryCtrlParamBo.YS_FIELDITEM,"id",(String)dbean.get("itemid"),"mode");
			 
		//	this.calculation="Y";
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	/**
	 * 更新序号指标I9999
	 */
	private void updateSortField()
	{
		try
		{
			StringBuffer buf=new StringBuffer();
			ContentDAO dao=new ContentDAO(this.conn);
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
			{
				switch(Sql_switcher.searchDbServer())
				{
					case 1://MSSQL
						buf.append("alter table ");
						buf.append(this.tmp1_name);
						buf.append(" add xxx int identity(1,1)");
						break;
					case 3://DB2
						buf.append("create sequence xxx increment by 1 start with 1");
						break;
				}
				dao.update(buf.toString());
				buf.setLength(0);
				switch(Sql_switcher.searchDbServer())
				{
					case 1://MSSQL
						buf.append("update ");
						buf.append(this.tmp1_name);
						buf.append(" set I9999=xxx");
						break;
					case 3://DB2
						buf.append("update ");
						buf.append(this.tmp1_name);
						buf.append(" set I9999=nextval for xxx");
						break;
				}
				dao.update(buf.toString());
				buf.setLength(0);
				switch(Sql_switcher.searchDbServer())
				{
					case 1://MSSQL
						buf.append("alter table ");
						buf.append(this.tmp1_name);
						buf.append(" drop column xxx");
						break;
					case 3://DB2
						buf.append("drop sequence xxx ");
						break;
				}				
				dao.update(buf.toString());				
			}
		}
		catch(Exception ex)
		{
			
		}
	}
	
	
	/**
	 * 创建临时表，获得每个人当年非当月的算税记录当作为所在单位的月数    20181221
	 * @param strwhere
	 * @throws GeneralException
	 */
	private void createMonthCount(String strwhere) throws GeneralException
	{
		try
		{
			String tmpname="t#"+userview.getUserName()+"_gz_5"; 
			DbWizard dbw=new DbWizard(this.conn);
			dbw.dropTable(tmpname);
			
			StringBuffer buf=new StringBuffer();
			if(Sql_switcher.searchDbServer()==2)
				buf.append("create table "+tmpname+" as "); 
			buf.append(" select nbase,a0100,count(a0100) as month_count  ");
			if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
				buf.append(", taxunit"); 
			if(Sql_switcher.searchDbServer()!=2)
				buf.append(" into "+tmpname); 
			
			buf.append(" from ( select distinct nbase,a0100, " +Sql_switcher.month("tax_date")+"  as amonth");
			if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
				buf.append(","+Sql_switcher.sqlNull("taxunit","-1")+" as taxunit"); 
			
		//	buf.append(" from gz_tax_mx ");
			String select_str=" upper(nbase) as nbase,a0100,a00z0,a00z1,tax_date,taxmode,sds,ynse,salaryid,YNSE_FIELD";
			if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
				select_str+=",taxunit"; 
			buf.append(" from  ( select "+select_str+" from gz_tax_mx  union all select "+select_str+" from taxarchive ) gz_tax_mx  ");
			
			buf.append(" where exists ( select null from (select nbase,a0100,"+this.tax_date_item+" from  ");
			buf.append(this.gz_tablename+" where "+strwhere+" )   ");
			buf.append(" A where upper(gz_tax_mx.nbase)=upper(A.nbase) and gz_tax_mx.a0100=A.a0100 ");
			buf.append(" and "+Sql_switcher.year("gz_tax_mx.tax_date")+"="+Sql_switcher.year("A."+this.tax_date_item));
			buf.append(" and "+Sql_switcher.month("gz_tax_mx.tax_date")+"<>"+Sql_switcher.month("A."+this.tax_date_item));
			buf.append(" ) and taxmode='");
			buf.append(tax_mode);
			buf.append("'");
			 
			buf.append(" ) aa  group by nbase,a0100 ");
			if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
				buf.append(",taxunit");
			
			 
			dbw.execute(buf.toString());  
			
			 
			 
			buf.setLength(0);
			tmpname="t#"+userview.getUserName()+"_gz_6"; 
			dbw.dropTable(tmpname);
			if(Sql_switcher.searchDbServer()==2)
				buf.append("create table "+tmpname+" as "); 
			
			buf.append(" select nbase,a0100  ");	
			if(Sql_switcher.searchDbServer()!=2)
				buf.append(" into "+tmpname); 
			
			buf.append(" from (select nbase,a0100 from (");
			buf.append(getSql_insert_gz_6(strwhere, "gz_tax_mx"));
			buf.append(" union all ");
			buf.append(getSql_insert_gz_6(strwhere, "taxarchive"));
			buf.append(") tab group by nbase,a0100) aa  ");
			dbw.execute(buf.toString());  
			
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}	
			
	}
	
	/**
	 * 个税表和个税归档表都需要查出来人，否则应纳税所得额等有问题
	 * @param strwhere
	 * @param tablename_param
	 * @return
	 */
	private String getSql_insert_gz_6(String strwhere, String tablename_param) {
		StringBuffer buf = new StringBuffer();
		buf.append(" (select distinct nbase,a0100 ");
		if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
			buf.append(","+Sql_switcher.sqlNull("taxunit","-1")+" as taxunit");  
		
		buf.append(" from " + tablename_param + " where exists ( select null from  ");
		if(strwhere!=null)
			strwhere=strwhere.replaceAll(this.gz_tablename+".","A.");
		buf.append(this.gz_tablename+" A  where "+strwhere+"    ");
		buf.append(" and " + tablename_param + ".nbase=A.nbase and " + tablename_param + ".a0100=A.a0100 ");
		buf.append(" and "+Sql_switcher.year(tablename_param + ".tax_date")+"="+Sql_switcher.year("A."+this.tax_date_item));
		buf.append(" and "+Sql_switcher.month(tablename_param + ".tax_date")+"="+Sql_switcher.month("A."+this.tax_date_item));
		buf.append(" and ( " + tablename_param + ".a00z0<>A.a00z0 or " + tablename_param + ".a00z1<>A.a00z1 or " + tablename_param + ".salaryid<>"+this.salaryid+"  ) "); 
		buf.append(" ) and taxmode='");
		buf.append(tax_mode);
		buf.append("')");
		
		return buf.toString();
	}
	
	/**
	 * 创建临时表1 表名：用户名 + Tax_Temp1
	 * 临时表1，包括所有要计算的人员；
	 * 从工资表将相同计税方式的、应纳税所得额大于0的记录导入临时表1；
	 * 相同归属日期，应纳税所得额需要按人汇总；
  	 * 以A00Z0, A00Z1升序进行排序，将顺序号保存在I9999；
  	 * 表结构包括：NBASE,A0100,A00Z0,A00Z1,I9999,tax_date,ynse
  	 * I9999：记录顺序号
  	 * tax_date:计税时间
  	 * ynse:应纳税额
	 */
	private void createTemp1(String strWhere)throws GeneralException
	{
		try
		{
			 RecordVo vo=new RecordVo(this.gz_tablename);
			
			
			DbWizard dbw=new DbWizard(this.conn);
			dbw.dropTable(tmp1_name);
			/**创建临时表1*/
			String flds="NBASE,A0100,A00Z0,A00Z1";
			dbw.createTempTable(gz_tablename,tmp1_name, flds, "1=2", "");
			Table table=new Table(tmp1_name);
			Field field=new Field("I9999","I9999");
			field.setDatatype(DataType.INT);
			table.addField(field);
			field=new Field("tax_date","tax_date");
			field.setDatatype(DataType.DATE);
			table.addField(field);
			field=new Field("ynse","ynse");
			field.setDatatype(DataType.FLOAT);
			field.setLength(12);
			field.setDecimalDigits(4);
			table.addField(field);		
			
			if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
			{
				field=new Field("taxunit","taxunit"); //计税单位
				field.setDatatype(DataType.STRING);
				field.setLength(50); 
				table.addField(field);
			}
			if(this.hiredate!=null&&this.hiredate.trim().length()>0)
			{
				field=new Field("hiredate","hiredate"); //入职时间
				field.setDatatype(DataType.DATE); 
				table.addField(field);
			}
			if(this.disability!=null&&this.disability.trim().length()>0)
			{
				field=new Field("disability","disability"); //是否残疾人
				field.setDatatype(DataType.STRING);
				field.setLength(2);
				table.addField(field);
				
				field=new Field("disability_date","disability_date"); //是否残疾人
				field.setDatatype(DataType.DATE); 
				table.addField(field);
			} 
			dbw.addColumns(table);
			/**应纳税额>0*/
			String cond=strWhere; //Sql_switcher.sqlNull(this.ynse_item, 0)+">0";
		//	if(strWhere.length()!=0)
		//		cond=strWhere +" and "+cond;
			
			
			/**归属日期和归属次数*/
			/**导入计税时间，应纳税所得额
			 *相同归属日期，应纳税所得额汇总 
			 */
			String orderby=" order by A00Z0,A00Z1";
			StringBuffer destflds=new StringBuffer();
			destflds.append("NBASE,A0100,A00Z0,A00Z1,tax_date,ynse");
			if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
				destflds.append(",taxunit");
			if(this.hiredate!=null&&this.hiredate.trim().length()>0)
				destflds.append(",hiredate");
			if(this.disability!=null&&this.disability.trim().length()>0)
				destflds.append(",disability");
			if(!StringUtils.isEmpty(this.disability_date)&&vo.hasAttribute(this.disability_date.toLowerCase()))
			{
				destflds.append(",disability_date"); 
			}
			
			StringBuffer srcflds=new StringBuffer();
			srcflds.append("NBASE,A0100,A00Z0,MIN(A00Z1) as A00Z1,");
			srcflds.append("MIN(");
			srcflds.append(tax_date_item);
			srcflds.append(") as tax_date,");
			srcflds.append("SUM(");
			srcflds.append(ynse_item);
			srcflds.append(") as ynse ");
			if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
				srcflds.append(","+Sql_switcher.sqlNull(this.tax_unit,"-1")+" as taxunit");  
			if(this.hiredate!=null&&this.hiredate.trim().length()>0)
			{ 
		        srcflds.append(","+Sql_switcher.isnull("MIN("+hiredate+")",Sql_switcher.dateValue("2019-01-01"))+" as hiredate"); 
				//srcflds.append(",MIN("+hiredate+") as hiredate");
		        
			}
			if(this.disability!=null&&this.disability.trim().length()>0)
				srcflds.append(",MIN("+disability+") as disability");
			if(!StringUtils.isEmpty(this.disability_date)&&vo.hasAttribute(this.disability_date.toLowerCase()))
			{
				srcflds.append(",MIN("+this.disability_date+") as disability_date");
			}
			
			
			String groupby=" group by nbase,a0100,a00z0";
			if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
				groupby+=","+this.tax_unit;
			StringBuffer strsql=new StringBuffer();
			
			switch(Sql_switcher.searchDbServer())
			{
			case 1://mssql
			case 3://db2
				strsql.append("insert into ");
				strsql.append(this.tmp1_name);
				strsql.append(" (");
				strsql.append(destflds.toString());
				strsql.append(") ");
				strsql.append(" select ");
				strsql.append(destflds.toString());
				strsql.append(" from (");
				strsql.append(" select ");
				strsql.append(srcflds.toString());
				strsql.append(" from ");
				strsql.append(this.gz_tablename);
				strsql.append(" where ");
				strsql.append(cond);
				strsql.append(" ");
				strsql.append(groupby);
				strsql.append(") ");
				strsql.append(this.gz_tablename);
				strsql.append(orderby);
				break;
			case 2://oracle
				strsql.append("insert into ");
				strsql.append(this.tmp1_name);
				strsql.append(" (");
				strsql.append(destflds.toString());
				strsql.append(",I9999)");
				strsql.append(" select a.*,RowNum from (");
				strsql.append(" select ");
				strsql.append(srcflds.toString());
				strsql.append(" from ");
				strsql.append(this.gz_tablename);
				strsql.append(" where ");
				strsql.append(cond);
				strsql.append(groupby);
				strsql.append(orderby);
				strsql.append(") a");
				break;
			}
			dbw.execute(strsql.toString());
			if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
				dbw.execute("create index "+this.tmp1_name+"_index on "+this.tmp1_name+" (NBASE,A0100,A00Z0,A00Z1,taxunit)");
			else
				dbw.execute("create index "+this.tmp1_name+"_index on "+this.tmp1_name+" (NBASE,A0100,A00Z0,A00Z1)");
			
			if(!StringUtils.isEmpty(this.disability_date)&&!vo.hasAttribute(this.disability_date.toLowerCase()))
			{
				String dbpres=this.templatevo.getString("cbase");
				//应用库前缀
				String[] dbarr=StringUtils.split(dbpres, ",");
				for(int i=0;i<dbarr.length;i++)
				{
					String pre=dbarr[i];
					String update_str="update "+this.tmp1_name+" set disability_date=(select "+this.disability_date+" from "+pre+"A01 where "+this.tmp1_name+".a0100="+pre+"A01.a0100 )";
					update_str+=" where upper(NBASE)='"+pre.toUpperCase()+"' and exists (select null from "+pre+"A01 where "+this.tmp1_name+".a0100="+pre+"A01.a0100 ) ";
					dbw.execute(update_str);
					
				}
			}
			
			
			/**生成mssql,db2,I9999*/
			updateSortField();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}
	/**
	 * 创建计算临时表2
	 * @throws GeneralException
	 */
	private void createTemp2()throws GeneralException
	{
		try
		{
			DbWizard dbw=new DbWizard(this.conn);
			dbw.dropTable(tmp2_name);
			String flds="NBASE,A0100,A00Z0,A00Z1";
			dbw.createTempTable(this.gz_tablename,this.tmp2_name, flds, "1=2", "");
			Table table=new Table(tmp2_name);
			Field field=new Field("TaxableSum","TaxableSum");
			field.setDatatype(DataType.FLOAT);
			field.setLength(12);
			field.setDecimalDigits(4);
			table.addField(field);

			field=new Field("SDSSum","SDSSum");
			field.setDatatype(DataType.FLOAT);
			field.setLength(12);
			field.setDecimalDigits(4);
			table.addField(field);			
			
			field=new Field("tax_date","tax_date");
			field.setDatatype(DataType.DATE);
			table.addField(field);
			
			field=new Field("ynse","ynse");
			field.setDatatype(DataType.FLOAT);
			field.setLength(12);
			field.setDecimalDigits(4);
			table.addField(field);	
			
			field=new Field("sds","sds");
			field.setDatatype(DataType.FLOAT);
			field.setLength(12);
			field.setDecimalDigits(4);
			table.addField(field);	
			
			field=new Field("taxitem","taxitem");
			field.setDatatype(DataType.INT);
			field.setLength(12);
			table.addField(field);
			
			//当月工资所得额
			field=new Field("gz_sde","gz_sde");
			field.setDatatype(DataType.FLOAT);
			field.setLength(12);
			field.setDecimalDigits(4);
			table.addField(field);
			
			//是否有当月工资所得额 1：有  0：没有
			field=new Field("is_sde","is_sde");
			field.setDatatype(DataType.INT);
			field.setLength(12);
			table.addField(field);
			
			//纳税人当年截至本月在本单位的任职受雇月份数 20181221
			field=new Field("kcbase_num","kcbase_num");
			field.setDatatype(DataType.INT);
			field.setLength(12);
			table.addField(field);
			
			field=new Field("yyjl","yyjl"); //当月是否已有记录
			field.setDatatype(DataType.INT);
			field.setLength(12);
			table.addField(field);
			
			
			if("3".equalsIgnoreCase(tax_mode)) //企业年金
			{
				
				field=new Field("gzxj_current","gzxj_current"); //当月工资薪金
				field.setDatatype(DataType.FLOAT);
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
			}
			
			if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
			{
				field=new Field("taxunit","taxunit"); //计税单位
				field.setDatatype(DataType.STRING);
				field.setLength(50); 
				table.addField(field);
			}

			
			if(this.hiredate!=null&&this.hiredate.trim().length()>0)
			{
				field=new Field("hiredate","hiredate"); //入职时间
				field.setDatatype(DataType.DATE); 
				table.addField(field);
			}
			if(this.disability!=null&&this.disability.trim().length()>0)
			{
				field=new Field("disability","disability"); //是否残疾人
				field.setDatatype(DataType.STRING);
				field.setLength(2); 
				table.addField(field);
				
				field=new Field("disability_date","disability_date"); //是否残疾人
				field.setDatatype(DataType.DATE); 
				table.addField(field);
				
				field=new Field("disability_SDSSum","disability_SDSSum");
				field.setDatatype(DataType.FLOAT);
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);		
			} 
			
			
		 
			field=new Field("allowance","allowance"); //残疾人累计个税减免
			field.setDatatype(DataType.FLOAT);
			field.setLength(12);
			field.setDecimalDigits(4);
			table.addField(field);
				 
			field=new Field("allowance_sum","allowance_sum"); //残疾人累计个税减免
			field.setDatatype(DataType.FLOAT);
			field.setLength(12);
			field.setDecimalDigits(4);
			table.addField(field);
			
			
			field=new Field("ljse","ljse"); // 2019/11/05  累计预扣缴税费
			field.setDatatype(DataType.FLOAT);
			field.setLength(12);
			field.setDecimalDigits(4);
			table.addField(field); 
			
			dbw.addColumns(table);
			if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
				dbw.execute("create index "+this.tmp2_name+"_index on "+this.tmp2_name+" (NBASE,A0100,A00Z0,A00Z1,taxunit)");
			else
				dbw.execute("create index "+this.tmp2_name+"_index on "+this.tmp2_name+" (NBASE,A0100,A00Z0,A00Z1)");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}		
	}
	
	/**
	 * 如果个税明细表不存在，则自动创建个税明细表
	 * @throws GeneralException
	 */
	public void createTaxDetails()throws GeneralException
	{
		try
		{
			DbWizard dbw=new DbWizard(this.conn);
			if(!dbw.isExistTable("gz_tax_mx", false))
			{
				Table table=new Table("gz_tax_mx");
				com.hjsj.hrms.module.gz.tax.businessobject.TaxMxBo taxMxBo=new com.hjsj.hrms.module.gz.tax.businessobject.TaxMxBo();
				ArrayList<Field> list=taxMxBo.searchAllCommonItemList();
				for (Field field : list) {
					table.addField(field);
				}
				dbw.createTable(table);
			}
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from gz_tax_mx where 1=2");
			ResultSetMetaData mt=rowSet.getMetaData();
			int columns=mt.getColumnCount();
			HashMap map=new HashMap();
			for(int i=0;i<columns;i++)
			{
				map.put(mt.getColumnName(i+1).toLowerCase(),"1");
			}

			if(map.get("a00z2")==null)
			{
				Table tbl=new Table("gz_tax_mx");
				Field field=new Field("A00Z2","A00Z2");
				field.setDatatype(DataType.DATE);
				tbl.addField(field);
				dbw.addColumns(tbl);
			}
			if(map.get("a00z3")==null)
			{
				Table tbl=new Table("gz_tax_mx");
				Field field=new Field("A00Z3","A00Z3");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				tbl.addField(field);
				dbw.addColumns(tbl);
			}

			if(map.get("ynse_field")==null)
			{
				Table tbl=new Table("gz_tax_mx");
				Field field=new Field("ynse_field","ynse_field");
				field.setDatatype(DataType.STRING);
				field.setLength(5);
				tbl.addField(field);
				dbw.addColumns(tbl);
			}

			if(map.get("deptid")==null)
			{
				Table tbl=new Table("gz_tax_mx");
				Field field=new Field("deptid","deptid");
				field.setDatatype(DataType.STRING);
				field.setLength(30);
				tbl.addField(field);
				dbw.addColumns(tbl);
			}


			if(map.get("ynse")==null)
			{
				Table tbl=new Table("gz_tax_mx");
				Field field=new Field("ynse","ynse");
				field.setDatatype(DataType.FLOAT);
				field.setLength(12);
				field.setDecimalDigits(4);
				tbl.addField(field);
				dbw.addColumns(tbl);
			}

			if(map.get("userflag")==null)
			{
				Table tbl=new Table("gz_tax_mx");
				Field field=new Field("UserFlag","UserFlag");
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				tbl.addField(field);
				dbw.addColumns(tbl);
			}


			if(map.get("declare_tax")==null)
			{
				Table tbl=new Table("gz_tax_mx");
				Field 	field=new Field("declare_date","declare_date");
				field.setDatatype(DataType.DATE);
				tbl.addField(field);
				dbw.addColumns(tbl);
			}
			if(map.get("salaryid")==null)
			{
				Table tbl=new Table("gz_tax_mx");
				Field 	field=new Field("salaryid","salaryid");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				tbl.addField(field);
				dbw.addColumns(tbl);
			}
			if(map.get("taxmode")==null)
			{
				Table tbl=new Table("gz_tax_mx");
				Field 	field=new Field("taxmode","taxmode");
				field.setDatatype(DataType.STRING);
				field.setLength(10);
				tbl.addField(field);
				dbw.addColumns(tbl);
			}
			if(map.get("description")==null)
			{
				Table tbl=new Table("gz_tax_mx");
				Field 	field=new Field("description","description");
				field.setDatatype(DataType.STRING);
				field.setLength(200);
				tbl.addField(field);
				dbw.addColumns(tbl);
			}
			if(map.get("flag")==null)
			{
				Table tbl=new Table("gz_tax_mx");
				Field field=new Field("flag","flag");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				tbl.addField(field);
				dbw.addColumns(tbl);
				dbw.updateRecord("gz_tax_mx", "flag=1", "");
			}
			if (map.get("a0000") == null) {
				Table tbl = new Table("gz_tax_mx");
				Field field=new Field("A0000","A0000");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				field.setVisible(false);
				tbl.addField(field);
				dbw.addColumns(tbl);
			}

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 求临时表1最大子集记录数
	 * @return
	 */
	private int getSubMaxRows()
	{
		int nmaxs=0;
		StringBuffer buf=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			buf.append("select max(a) from (select count(*) as a from ");
			buf.append(this.tmp1_name);
			buf.append(" group by nbase,a0100) b");
			RowSet rset=dao.search(buf.toString());
			if(rset.next())
				nmaxs=rset.getInt(1);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return nmaxs;
	}
	
	
	
	/**
	 * 当发现个税表里有非自己建的当月当次数据，需z1自动加1
	 * @throws GeneralException
	 */
	public void autoAddZ1(String cond,String fieldname)throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String table="t#"+userview.getUserName()+"_gz_1"; //this.userview.getUserName()+"_tempZ1Table";
			StringBuffer strsql=new StringBuffer("");
			if(Sql_switcher.searchDbServer()==2)
				strsql.append("create table "+table+" as select distinct ss.nbase,ss.a0100,ss.a00z1,ss.a00z0,ss.a00z3 ");
			else 
				strsql.append("select distinct ss.nbase,ss.a0100,ss.a00z1,ss.a00z0,ss.a00z3  into "+table);
		
			if(cond.trim().length()>0)
				strsql.append("  from  (select * from "+this.gz_tablename+" where "+cond.trim()+" )  ss");
			else
				strsql.append("  from  "+this.gz_tablename+" ss");
		
			
			strsql.append(",gz_tax_mx gm where ");
			strsql.append(" gm.salaryid="+this.salaryid+" and lower(ss.nbase)=lower(gm.nbase) and ss.a0100=gm.a0100 and ss.a00z0=gm.a00z0 and ss.a00z1=gm.a00z1 ");
			if(this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager))
			{
				strsql.append(" and ( lower(gm.userflag)<>'"+this.manager.toLowerCase()+"' ");
				strsql.append(" or (  lower(gm.userflag)='"+this.manager.toLowerCase()+"' and ( gm.a00z2<>ss.a00z2 or gm.a00z3<>ss.a00z3 )  ) ) ");
			}
			else
			{
				strsql.append(" and ( lower(gm.userflag)<>'"+this.userview.getUserName().toLowerCase()+"' ");
				strsql.append(" or (  lower(gm.userflag)='"+this.userview.getUserName().toLowerCase()+"' and ( gm.a00z2<>ss.a00z2 or gm.a00z3<>ss.a00z3 )  ) ) ");
			}
			strsql.append(" and lower(gm.ynse_field)='"+fieldname.toLowerCase()+"' and gm.userflag is not null");
		
	//		if(cond.trim().length()>0)
	//			strsql.append(" and "+cond.replaceAll(this.gz_tablename, "ss"));
			
			DbWizard dbw=new DbWizard(this.conn);
		//	if(dbw.isExistTable(table,false))
		//	{
				dbw.dropTable(table);
			/*	dbw.execute("delete from "+table);
				strsql.setLength(0);
				strsql.append("insert into "+table+" (nbase,a0100,a00z1,a00z0,a00z3) ");
				strsql.append("select distinct ss.nbase,ss.a0100,ss.a00z1,ss.a00z0,ss.a00z3  ");
				
				if(cond.trim().length()>0)
					strsql.append("  from  (select * from "+this.gz_tablename+" where "+cond.trim()+" )  ss");
				else
					strsql.append("  from  "+this.gz_tablename+" ss");
				strsql.append(",gz_tax_mx gm where ");
				strsql.append(" gm.salaryid="+this.salaryid+" and lower(ss.nbase)=lower(gm.nbase) and ss.a0100=gm.a0100 and ss.a00z0=gm.a00z0 and ss.a00z1=gm.a00z1 ");
				if(this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager))
					strsql.append(" and lower(gm.userflag)<>'"+this.manager.toLowerCase()+"' ");
				else
					strsql.append(" and lower(gm.userflag)<>'"+this.userview.getUserName().toLowerCase()+"' ");
				strsql.append(" and gm.userflag is not null");
		//		if(cond.trim().length()>0)
		//			strsql.append(" and "+cond.replaceAll(this.gz_tablename, "ss"));
				dbw.execute(strsql.toString());*/
		//	}
		//	else
			{
				dao.update(strsql.toString());
				dao.update("create index "+this.userview.getUserName()+"_gz_1_idx on "+table+" (a0100,a00z1,a00z0,nbase)");
			}
			
			strsql.setLength(0);
			strsql.append("update "+table+" set a00z3=(select max(gz_tax_mx.a00z1) from gz_tax_mx where salaryid="+this.salaryid+" and  "+table+".a0100=gz_tax_mx.a0100 "); 
			strsql.append(" and "+table+".a00z0=gz_tax_mx.a00z0 and lower("+table+".nbase)=lower(gz_tax_mx.nbase) group by gz_tax_mx.a0100	) ");
			dao.update(strsql.toString());
			dao.update("update "+table+" set a00z3=a00z3+1");
			
			
			strsql.setLength(0);
			strsql.append("update "+table+" set a00z3=(select max("+gz_tablename+".a00z1)+1 from "+gz_tablename+" where  "+table+".a0100="+gz_tablename+".a0100 "); 
			strsql.append(" and "+table+".a00z0="+gz_tablename+".a00z0 and lower("+table+".nbase)=lower("+gz_tablename+".nbase) group by "+gz_tablename+".a0100 having max("+gz_tablename+".a00z1)+1>"+table+".a00z3	) ");
			strsql.append(" where exists ( select null from "+gz_tablename+" where  "+table+".a0100="+gz_tablename+".a0100 ");
			strsql.append(" and "+table+".a00z0="+gz_tablename+".a00z0 and lower("+table+".nbase)=lower("+gz_tablename+".nbase) group by "+gz_tablename+".a0100 having max("+gz_tablename+".a00z1)+1>"+table+".a00z3	 )");
			dao.update(strsql.toString());
			
			
			strsql.setLength(0);
			strsql.append("update "+this.gz_tablename+" set a00z1=(select a00z3 from "+table+" where   "+table+".a0100="+this.gz_tablename+".a0100 "); 
			strsql.append(" and "+table+".a00z1="+this.gz_tablename+".a00z1 and "+table+".a00z0="+this.gz_tablename+".a00z0 and "+table+".nbase="+this.gz_tablename+".nbase and "+table+".a00z3 is not null ) ");
			strsql.append(" where exists (select null from "+table+" where   "+table+".a0100="+this.gz_tablename+".a0100 "); 
			strsql.append(" and "+table+".a00z1="+this.gz_tablename+".a00z1 and "+table+".a00z0="+this.gz_tablename+".a00z0 and "+table+".nbase="+this.gz_tablename+".nbase and "+table+".a00z3 is not null ) ");
			dao.update(strsql.toString());
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	
	
	
	public void delTaxMx(String strWhere,DynaBean dbean)throws GeneralException
	{
		try
		{
			init(dbean);
			String fieldname=(String)dbean.get("itemname");
			String aflag=this.ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  				
			String atableName="t#"+this.userview.getUserName()+"_gzsp"; //this.userview.getUserName()+"_sp_data";
			StringBuffer buf=new StringBuffer("");
			ContentDAO dao=new ContentDAO(this.conn);
			 if(!this.gz_tablename.equalsIgnoreCase(atableName)&&this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager)&&aflag!=null&& "1".equals(aflag))
			 {
									String dbpres=this.templatevo.getString("cbase");
									//应用库前缀
									String[] dbarr=StringUtils.split(dbpres, ",");
									for(int i=0;i<dbarr.length;i++)
									{
										String pre=dbarr[i];
										
										StringBuffer tempSql=new StringBuffer(" and upper(nbase)='"+pre.toUpperCase()+"'" );
										//					权限过滤
										if("1".equals(this.controlByUnitcod))
										{
											String whl_str=getWhlByUnits();
											if(whl_str.length()>0)
											{
												tempSql.append(" and  a0100 in ( select a0100 from "+this.gz_tablename+" where 1=1 "+whl_str+" and  upper(nbase)='"+pre.toUpperCase()+"' ) ");
											}
										}
										else
										{
											String whereIN=InfoUtils.getWhereINSql(this.userview,pre);
											whereIN="select a0100 "+whereIN;	
											tempSql.append(" and a0100 in ( "+whereIN+" )");
										}
										 buf.setLength(0);
										 buf.append("delete from gz_tax_mx");
										 buf.append(" where exists(select null from ");
										 buf.append(" "+this.gz_tablename+"  ");
										 buf.append(" where gz_tax_mx.nbase="+this.gz_tablename+".nbase and gz_tax_mx.A0100="+this.gz_tablename+".A0100  ");
										 if(strWhere.trim().length()>0)
											 buf.append(" and ( "+strWhere+" )");
										 buf.append(" and gz_tax_mx.A00Z2="+this.gz_tablename+".a00z2 and gz_tax_mx.A00Z3="+this.gz_tablename+".a00z3 ");
										 buf.append(" and gz_tax_mx.A00Z0="+this.gz_tablename+".a00z0 and gz_tax_mx.A00Z1="+this.gz_tablename+".a00z1)");							
										 buf.append(" and salaryid=");
										 buf.append(this.salaryid);
										 if(this.gz_tablename.indexOf("_gzsp")==-1)
										 {
											 buf.append(" and lower(userflag)='"+this.manager.toLowerCase()+"'");  //2010/10/15
										 }
										 
										 buf.append(" and ( upper(YNSE_FIELD)='"+this.ynse_item.toUpperCase()+"' or upper(YNSE_FIELD)='"+fieldname.toUpperCase()+"'   or YNSE_FIELD is null )") ;  //解决同一工资套多个算税公式 
										 dao.update(buf.toString()+tempSql.toString());
										 buf.setLength(0);
										 
									}
				}
				else
				{
							 
								 buf.setLength(0);
								 buf.append("delete from gz_tax_mx");
								 buf.append(" where exists(select * from ");
								 buf.append(" "+this.gz_tablename+" ");
								 buf.append(" where gz_tax_mx.nbase="+this.gz_tablename+".nbase and gz_tax_mx.A0100="+this.gz_tablename+".A0100  ");
								 if(strWhere.trim().length()>0)
									 buf.append(" and ( "+strWhere+" )");	
								 buf.append(" and gz_tax_mx.A00Z2="+this.gz_tablename+".a00z2 and gz_tax_mx.A00Z3="+this.gz_tablename+".a00z3 ");
								 buf.append(" and gz_tax_mx.A00Z0="+this.gz_tablename+".a00z0 and gz_tax_mx.A00Z1="+this.gz_tablename+".a00z1)");
								 buf.append(" and salaryid=");
								 buf.append(this.salaryid);
								 if(this.gz_tablename.indexOf("_gzsp")==-1)
								 {
									 buf.append(" and lower(userflag)='"+this.userview.getUserName().toLowerCase()+"'");  //2010/10/15
									
								 }
								 buf.append(" and ( upper(YNSE_FIELD)='"+this.ynse_item.toUpperCase()+"' or upper(YNSE_FIELD)='"+fieldname.toUpperCase()+"'  or YNSE_FIELD is null )") ;  //解决同一工资套多个算税公式
								 int num=dao.update(buf.toString());
								 buf.setLength(0);
							
				}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 根据职位找直属部门
	 * @param codeid
	 * @return
	 */
	public String getUnByPosition(String codeid)
	{
		String str="";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from organization where codeitemid=(select parentid from organization where codeitemid='"+codeid+"')");
			if(rowSet.next())
			{
				str=rowSet.getString("codesetid")+rowSet.getString("codeitemid");
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	
	public String getWhlByUnits()
	{
		StringBuffer whl=new StringBuffer(""); 
		String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");
		orgid = orgid != null ? orgid : "";
		String deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
		deptid = deptid != null ? deptid : "";
		
		String unitcodes=this.userview.getUnit_id();  //UM010101`UM010105`
		if(unitcodes!=null&& "UN`".equalsIgnoreCase(unitcodes))
		{
			
		}
		else
		{
			if(unitcodes==null||unitcodes.length()==0|| "UN".equalsIgnoreCase(unitcodes.trim()))
			{
				String a_code=""; 
				if(!this.userview.isSuper_admin())
				{
					if(this.userview.getManagePrivCode().length()==0)
						a_code="1=2";
					else if("@K".equals(this.userview.getManagePrivCode()))
						a_code=getUnByPosition(this.userview.getManagePrivCodeValue());
					else
						a_code=this.userview.getManagePrivCode()+this.userview.getManagePrivCodeValue();
					
				}
				
				if("".equals(a_code))
					return "";
				else if("1=2".equals(a_code))
					return " and 1=2 ";
				else
					unitcodes=a_code+"`";
			}
			
			
			String[] temps=unitcodes.split("`");
			if(orgid.trim().length()>0&&deptid.trim().length()>0)
			{
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						 whl.append(" or "+this.gz_tablename+"."+deptid+" like '"+temps[i].substring(2)+"%' ");
					}
				}
				
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						if("UN".equals(temps[i].substring(0,2)))
						{
							 whl.append(" or "+this.gz_tablename+"."+orgid+" like '"+temps[i].substring(2)+"%' ");
						}
					}
				} 
			}
			else if(orgid.trim().length()>0)
			{
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						if("UN".equals(temps[i].substring(0,2)))
						{
							 whl.append(" or "+this.gz_tablename+"."+orgid+" like '"+temps[i].substring(2)+"%' ");
						}
					}
				} 
			}
			else if(deptid.trim().length()>0)
			{
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						 whl.append(" or "+this.gz_tablename+"."+deptid+" like '"+temps[i].substring(2)+"%' ");
					}
				}
			}
			
			if(whl.length()==0)
				return " and 1=2 ";
		}
		String whl_str="";
		if(whl.length()>0)
			whl_str=" and ( "+whl.substring(3)+" ) ";
		return whl_str;
	}
	
	
	
	/**
	 * 个税明细表新增“累计预扣缴所得额”、“累计预扣预缴税额”、“已预扣预缴税额”信息列
	 * @throws GeneralException
	 */
	private void upgradeTaxStruct()throws GeneralException
	{
		StringBuffer buf=new StringBuffer();
		RowSet rowSet=null;
		try
		{ 
			
			DbWizard dbw=new DbWizard(this.conn);
			 
			ContentDAO dao=new ContentDAO(this.conn);
			rowSet=dao.search("select * from gz_tax_mx where 1=2");
			ResultSetMetaData metaData=rowSet.getMetaData();			
			HashMap voMap=new HashMap();
			for(int i=1;i<=metaData.getColumnCount();i++)
			{				
				voMap.put(metaData.getColumnName(i).toUpperCase(),"1");			
			}
			metaData=null;  
			Table table=new Table("gz_tax_mx");
			Field field=null;
			 
			if(voMap.get("LJSDE")==null)
			{
				
				field=new Field("ynssde","应纳税所得额");
				field.setDatatype(DataType.FLOAT);	 
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
				field=new Field("ljsde","累计应纳税所得额");
				field.setDatatype(DataType.FLOAT);	 
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
				field=new Field("ljse","累计预扣税额");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
			}
			//	----------------------------  20181221
			if(voMap.get("ZNJY")==null)
			{
				
				field=new Field("lj_basedata","累计基本减除费用");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
				field=new Field("znjy","子女教育");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
				field=new Field("sylr","赡养老人");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
				
				field=new Field("zfdklx","住房贷款利息");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
				field=new Field("zfzj","住房租金");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
				field=new Field("jxjy","继续教育");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field); 
				
			}//
			
			Table table2=new Table("taxarchive");
			if(voMap.get("ALLOWANCE")==null)
			{ 
				field=new Field("allowance","税收减免"); //残疾人个税减免
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				table2.addField(field);
			} 
			if(field!=null)
			{
				dbw.addColumns(table); 
				if(voMap.get("ALLOWANCE")==null)
				{
					dbw.addColumns(table2); 
				}
				
			}
			 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
		}
		
	}
	
	
	
	
	
	
	/**
	 * 计算税表公式
	 * @param strWhere 计算过滤条件
	 * @param dbean    计算公式对象 包括如下属性hzName,itemname,useflag,itemid,rexpr,cond,standid,itemtype,runflag
	 * @throws GeneralException
	 */
	public void calc(String strWhere,DynaBean dbean)throws GeneralException
	{
		try
		{
			String fieldname=(String)dbean.get("itemname");
			StringBuffer buf=new StringBuffer();
			ContentDAO dao=new ContentDAO(this.conn);
			DbWizard dbw=new DbWizard(this.conn);
			/**扣减基数*/
			init(dbean);
			if(this.errorInfo.length()>0)
				return;
			
			String strStdId=(String)dbean.get("standid");  //税率表id
			if(!getK_baseOrMode())
				return;//throw new GeneralException("税率表无效,不能计算！");
			
			if("5".equals(this.tax_mode)) //综合所得
				calculation="Y";
			
			
			if(tax_mode_item==null||tax_mode_item.trim().length()==0)
				return;//未定义计税方式指标
			StringBuffer cond=new StringBuffer("");
			StringBuffer cond_update=new StringBuffer(""); 
			if(strWhere.length()>0)
			{
				cond.append(strWhere);
				cond.append(" and ");
			} 
			
			//20181128
			upgradeTaxStruct();
			
			
			if("2".equalsIgnoreCase(tax_mode)|| "3".equalsIgnoreCase(tax_mode)|| "4".equalsIgnoreCase(tax_mode)|| "5".equalsIgnoreCase(tax_mode))
			{
				
					if(SystemConfig.getPropertyValue("compute_nulltaxmode")!=null&& "false".equalsIgnoreCase(SystemConfig.getPropertyValue("compute_nulltaxmode")))
					{
						cond.append(Sql_switcher.isnull("nullif("+tax_mode_item+",'')","'x'")); 
						cond.append("='");
						cond.append(tax_mode);
						cond.append("'");
						cond_update.append(cond.toString());
					}
					else
					{
						cond.append(Sql_switcher.isnull("nullif("+tax_mode_item+",'')","'5'")); 
						cond.append("='");
						cond.append(tax_mode);
						cond.append("'");
						cond_update.append(cond.toString());
					}
			}
			else
			{
				if(SystemConfig.getPropertyValue("compute_nulltaxmode")!=null&& "false".equalsIgnoreCase(SystemConfig.getPropertyValue("compute_nulltaxmode")))
				{
					
					cond.append(" ((not (");
					cond.append(tax_mode_item);
					cond.append(" in ('2','4','3'))) and ");
					cond.append(tax_mode_item);
					cond.append(" is not null ");
					if(Sql_switcher.searchDbServer()==1)
					{
						cond.append(" and "+tax_mode_item);
						cond.append("<>''");
					}
					cond.append("  )");
					
				}
				else
				{
					cond.append(" ((not (");
					cond.append(tax_mode_item);
					cond.append(" in ('2','4','3'))) or ");
					cond.append(tax_mode_item);
					cond.append(" is null)");
				}
				cond_update.append(" ((not (");
				cond_update.append(tax_mode_item);
				cond_update.append(" in ('2','4','3'))) or ");
				cond_update.append(tax_mode_item);
				cond_update.append(" is null)");
				if(strWhere.length()>0)
				{
					cond_update.append(" and "+strWhere); 
				}
				
			}
			
			
			String aflag=this.ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  				
			String atableName="t#"+this.userview.getUserName()+"_gzsp"; //this.userview.getUserName()+"_sp_data";
			
			 
			
			if(this.gz_tablename.indexOf("_gzsp")==-1)
				autoAddZ1(strWhere,fieldname);
			
			// 20181221
			if("Y".equalsIgnoreCase(calculation))//累计预扣法
			{
				createMonthCount(cond.toString()); 
			}
			
			if(this.model==null|| "0".equals(this.model))   //正算
			{	
				/**创建临时表1*/
				createTemp1(cond.toString());
				/**创建临时表2*/
				createTemp2();
				
				/**根据归属日期A00Z0和A00Z1删除明细表记录,避免重复生成*/
				//	共享薪资类别，其他操作人员引入数据 (dengcan 2008/6/26 )
				if(!this.gz_tablename.equalsIgnoreCase(atableName)&&this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager)&&aflag!=null&& "1".equals(aflag))
				{
					String dbpres=this.templatevo.getString("cbase");
					//应用库前缀
					String[] dbarr=StringUtils.split(dbpres, ",");
					for(int i=0;i<dbarr.length;i++)
					{
						String pre=dbarr[i];
						buf.setLength(0);
						
						buf.append("delete from gz_tax_mx");
						buf.append(" where exists(select * from ");
						buf.append(this.tmp1_name);
						buf.append(" A");
						buf.append(" where gz_tax_mx.nbase=A.nbase and gz_tax_mx.A0100=A.A0100 and ");
						buf.append(" gz_tax_mx.A00Z0=A.A00Z0 and gz_tax_mx.A00Z1=A.A00Z1)");
						buf.append(" and salaryid=");
						buf.append(this.salaryid);
						buf.append(" and upper(nbase)='"+pre.toUpperCase()+"'" );
						
						buf.append(" and ( upper(YNSE_FIELD)='"+this.ynse_item.toUpperCase()+"' or upper(YNSE_FIELD)='"+fieldname.toUpperCase()+"'  or YNSE_FIELD is null )") ;  //解决同一工资套多个算税公式
						
						//					权限过滤
						if("1".equals(this.controlByUnitcod))
						{
							String whl_str=getWhlByUnits();
							if(whl_str.length()>0)
							{
								buf.append(" and  a0100 in ( select a0100 from "+this.gz_tablename+" where 1=1 "+whl_str+" and  upper(nbase)='"+pre.toUpperCase()+"' ) ");
							}
						}
						else
						{
							String whereIN=InfoUtils.getWhereINSql(this.userview,pre);
							whereIN="select a0100 "+whereIN;	
							buf.append(" and a0100 in ( "+whereIN+" )");
						}
						
						
						dao.update(buf.toString());
						
					}
				}
				else
				{
					buf.append("delete from gz_tax_mx");
					buf.append(" where exists ( select * from ");
					buf.append(this.tmp1_name);
					buf.append(" A");
					buf.append(" where gz_tax_mx.nbase=A.nbase and gz_tax_mx.A0100=A.A0100 and ");
					buf.append(" gz_tax_mx.A00Z0=A.A00Z0 and gz_tax_mx.A00Z1=A.A00Z1)");
					buf.append(" and salaryid=");
					buf.append(this.salaryid);
					buf.append(" and ( upper(YNSE_FIELD)='"+this.ynse_item.toUpperCase()+"' or upper(YNSE_FIELD)='"+fieldname.toUpperCase()+"'  or YNSE_FIELD is null )") ;  //解决同一工资套多个算税公式			
					dao.update(buf.toString());
				}
				
				/**计算项,清空薪资表的项目值*/
				String itemid=(String)dbean.get("itemname");
				if("1".equals(isClearItem))
				{
					if(strWhere.trim().length()>0)
						strWhere=" where "+strWhere;
					dbw.updateRecord(this.gz_tablename,itemid+"=NULL"," where "+cond_update.toString());//strWhere); //2013-11-15
				}
				/**求临时表1最大行数,其中某人最多记录数*/
				int nmaxs=getSubMaxRows();
				for (int i=0;i<nmaxs;i++)
				{
					buf.setLength(0);
					/**将临时表1中每个人员的第i记录（也即I9999最小的记录），导入临时表2*/
					buf.append("insert into ");
					buf.append(this.tmp2_name);
					buf.append("(NBASE,A0100,A00Z0,A00Z1,tax_date,ynse,is_sde");
					if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
						buf.append(",taxunit"); 
					if(this.hiredate!=null&&this.hiredate.trim().length()>0)
						buf.append(",hiredate");
					if(this.disability!=null&&this.disability.trim().length()>0)
						buf.append(",disability");
					
					if(!StringUtils.isEmpty(this.disability_date))
					{
						buf.append(",disability_date");
					}
					
					buf.append(")");
					buf.append(" select NBASE,A0100,A00Z0,A00Z1,tax_date,ynse,0");
					if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
						buf.append(",taxunit"); 
					if(this.hiredate!=null&&this.hiredate.trim().length()>0)
						buf.append(",hiredate");
					if(this.disability!=null&&this.disability.trim().length()>0)
						buf.append(",disability");
					if(!StringUtils.isEmpty(this.disability_date))
					{
						buf.append(",disability_date");
					}
					buf.append(" from ");
					buf.append(this.tmp1_name);
					buf.append(" where I9999=(select min(I9999) from ");
					buf.append(tmp1_name);
					buf.append(" A where ");
					buf.append(tmp1_name);
					buf.append(".NBASE");
					buf.append("=A.NBASE and ");
					buf.append(tmp1_name);
					buf.append(".A0100=");
					buf.append("A.A0100)");
					dao.update(buf.toString());
					
					// 20181221
					if("Y".equalsIgnoreCase(calculation))//累计预扣法
					{
						buf.setLength(0);;
						buf.append("update "+this.tmp2_name+" set kcbase_num=(select month_count from t#"+userview.getUserName()+"_gz_5 where "+this.tmp2_name+".a0100=t#"+userview.getUserName()+"_gz_5.a0100 and  upper("+this.tmp2_name+".nbase)=upper(t#"+userview.getUserName()+"_gz_5.nbase)   ");
						if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
						       buf.append(" and "+this.tmp2_name+".taxunit=t#"+userview.getUserName()+"_gz_5.taxunit ");      
					    buf.append("  )");
						dao.update(buf.toString());
						dao.update("update "+this.tmp2_name+" set kcbase_num="+Sql_switcher.isnull("kcbase_num","0")+"+1");
						
						if(this.hiredate!=null&&this.hiredate.trim().length()>0)
						{
							dao.update("update "+this.tmp2_name+" set kcbase_num="+Sql_switcher.month("tax_date")+"   where "+Sql_switcher.year("tax_date")+">"+Sql_switcher.year("hiredate"));
							dao.update("update "+this.tmp2_name+" set kcbase_num="+Sql_switcher.month("tax_date")+"-"+Sql_switcher.month("hiredate")+"+1   where "+Sql_switcher.year("tax_date")+"="+Sql_switcher.year("hiredate")+" and "+Sql_switcher.month("tax_date")+">="+Sql_switcher.month("hiredate")); 
							dao.update("update "+this.tmp2_name+" set kcbase_num=0   where "+Sql_switcher.year("tax_date")+"="+Sql_switcher.year("hiredate")+" and "+Sql_switcher.month("tax_date")+"<"+Sql_switcher.month("hiredate"));
						}
						
						buf.setLength(0);;
						buf.append("update "+this.tmp2_name+" set yyjl=1 where exists (select null from t#"+userview.getUserName()+"_gz_6 where "+this.tmp2_name+".a0100=t#"+userview.getUserName()+"_gz_6.a0100 and  upper("+this.tmp2_name+".nbase)=upper(t#"+userview.getUserName()+"_gz_6.nbase)   )");
						dao.update(buf.toString());
						dbw.dropTable("t#"+userview.getUserName()+"_gz_6");
					}	
					
					
					sumSdsOrYnse();
					/**全年一性次奖金=2*/
					if("2".equalsIgnoreCase(tax_mode))
					{
						runTaxTableOneBonus();					
					}
					/**劳务报酬=4*/
					else if("4".equalsIgnoreCase(tax_mode))
					{
						runTaxTable();					
					}
					/**=1工资薪金 =3企业年金*/
					else //(tax_mode.equalsIgnoreCase("1"))
					{  
						if("Y".equalsIgnoreCase(calculation)&&this.disability!=null&&this.disability.trim().length()>0)//累计预扣法
						{ 
								dao.update("update "+this.tmp2_name+" set  sdssum=sdssum+"+Sql_switcher.sqlNull("allowance_sum",0)+"   where "+Sql_switcher.isnull("disability","'0'")+"='1'");  //残疾人个税 和 累计应纳税额减半
							
						}  
						runTaxTable();
						 
					}		
					
					
					if(this.hiredate!=null&&this.hiredate.trim().length()>0)
					{ 
						dao.update("update "+this.tmp2_name+" set sds=0   where "+Sql_switcher.year("tax_date")+"="+Sql_switcher.year("hiredate")+" and "+Sql_switcher.month("tax_date")+"<"+Sql_switcher.month("hiredate"));
					}
					
					
					//2019/11/05计算累计预扣缴税费
					if("Y".equalsIgnoreCase(calculation))
					{
						dao.update("update "+this.tmp2_name+" set ljse=sds+"+Sql_switcher.sqlNull("sdssum",0)+" where  sds+"+Sql_switcher.sqlNull("sdssum",0)+">=0");
						dao.update("update "+this.tmp2_name+" set ljse=0 where sds is null or sds+"+Sql_switcher.sqlNull("sdssum",0)+"<0");
						if(this.disability!=null&&this.disability.trim().length()>0)
						{ 
							dao.update("update "+this.tmp2_name+" set ljse=sds+"+Sql_switcher.sqlNull("sdssum",0)+"-"+Sql_switcher.sqlNull("allowance_sum",0)+"  where  (sds+"+Sql_switcher.sqlNull("sdssum",0)+"-"+Sql_switcher.sqlNull("allowance_sum",0)+")>=0 and "+Sql_switcher.isnull("disability","'0'")+"='1'");
							dao.update("update "+this.tmp2_name+" set ljse=0  where ( sds is null  or ( sds+"+Sql_switcher.sqlNull("sdssum",0)+"-"+Sql_switcher.sqlNull("allowance_sum",0)+")<0  ) and "+Sql_switcher.isnull("disability","'0'")+"='1'");
							
						}
					}
					
					/**将计算结果更新至薪资表*/
					updateSalaryTable(itemid);
					/**追加记录至个税明细表*/
			//		appendRecordTaxMx();
					LazyDynaBean paramBean=new LazyDynaBean();  
					paramBean.set("tmp2_name",this.tmp2_name); 
					paramBean.set("dbean",this.dbean);  
					paramBean.set("desc_item",this.desc_item);  
					paramBean.set("isDeptControl",new Boolean(this.isDeptControl));  
					paramBean.set("gz_tablename",this.gz_tablename);  
					paramBean.set("manager",this.manager);  
					paramBean.set("templateVo",this.templatevo);  
					paramBean.set("ctrlparam",this.ctrlparam);  
					paramBean.set("controlByUnitcod",this.controlByUnitcod);  
					String whl_str=getWhlByUnits(); 
					paramBean.set("whlByUnits",whl_str);  
					paramBean.set("k_base",String.valueOf(this.k_base));  
					paramBean.set("declare_date_item",this.declare_date_item);  
					paramBean.set("salaryid",String.valueOf(this.salaryid));  
					paramBean.set("tax_mode",this.tax_mode);  
					paramBean.set("calculation",this.calculation); 
					paramBean.set("taxfldlist",this.taxfldlist);   
					if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
						paramBean.set("tax_unit","1");   //有计税单位
					else
						paramBean.set("tax_unit","0");   //无计税单位
					
					DbNameBo.appendRecordTaxMx(paramBean ,this.conn,this.userview);
					
					/**删除临时表1中第i条记录*/
					buf.setLength(0);
					buf.append("delete from ");
					buf.append(this.tmp1_name);
					buf.append(" where I9999=(select MIN(I9999) from ");
					buf.append(this.tmp1_name);
					buf.append(" A Where ");
					buf.append(this.tmp1_name);
					buf.append(".NBASE=A.NBASE and ");
					buf.append(this.tmp1_name);
					buf.append(".A0100=A.A0100)");
					dbw.execute(buf.toString());
					dbw.execute("delete from "+this.tmp2_name);
				}//for i loop end.
				dbw.execute("delete from "+this.tmp1_name);
				dbw.execute("delete from "+this.tmp2_name);
			}
			else  //反算
			{
				/**计算项,清空薪资表的项目值*/
				 String itemid=(String)dbean.get("itemname");
				 
				 if("1".equals(isClearItem))
				 {
					/*    String aa=strWhere;
						if(aa.trim().length()>0)
							aa=" where "+aa;
						dbw.updateRecord(this.gz_tablename,itemid+"=NULL",aa);
					*/	
						if(strWhere.trim().length()>0)
							strWhere=" where "+strWhere;
						dbw.updateRecord(this.gz_tablename,itemid+"=NULL"," where "+cond_update.toString());//strWhere); //2013-11-15 dengc
				 }
				 
				 create_gz_taxTable();
				 String tabName="t#"+this.userview.getUserName();//"gz_taxTable_"+this.userview.getUserName();
				 execute_gs_taxData(strStdId,cond.toString());  //生成个税税率表数据

		        //计算出应纳税所得额
				 StringBuffer sql=new StringBuffer(" update "+this.gz_tablename+" set "+itemid+"=(");
				 sql.append("select  "+Sql_switcher.round(Sql_switcher.isnull("gs","0"),2)+" from "+tabName+" where  "+this.gz_tablename+".a0100="+tabName+".a0100 ");
				 	
				 sql.append(" and  "+this.gz_tablename+".nbase="+tabName+".nbase ");
				 sql.append(" and  "+this.gz_tablename+".a00z0="+tabName+".tax_z0 ");
				 sql.append(" and  "+this.gz_tablename+".a00z1="+tabName+".tax_z1  ");
				 if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
					 sql.append(" and  "+Sql_switcher.sqlNull(this.gz_tablename+"."+this.tax_unit,"-1")+"="+tabName+".taxunit   ");
				 
				 
				 sql.append(" ) ");
				 sql.append(" where exists( ");
			     sql.append(" select null from "+tabName+" where  "+this.gz_tablename+".a0100="+tabName+".a0100 ");
				 sql.append(" and  "+this.gz_tablename+".nbase="+tabName+".nbase ");
				 sql.append(" and  "+this.gz_tablename+".a00z0="+tabName+".tax_z0 ");
				 sql.append(" and  "+this.gz_tablename+".a00z1="+tabName+".tax_z1 ");
				 if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
					 sql.append(" and  "+Sql_switcher.sqlNull(this.gz_tablename+"."+this.tax_unit,"-1")+"="+tabName+".taxunit   ");
				 sql.append(")"); 
				 
				 dao.update(sql.toString());
				 /**根据归属日期A00Z0和A00Z1删除明细表记录,避免重复生成*/
				
              		
				
				if(!this.gz_tablename.equalsIgnoreCase(atableName)&&this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager)&&aflag!=null&& "1".equals(aflag))
				{
						String dbpres=this.templatevo.getString("cbase");
						//应用库前缀
						String[] dbarr=StringUtils.split(dbpres, ",");
						for(int i=0;i<dbarr.length;i++)
						{
							String pre=dbarr[i];
							
							StringBuffer tempSql=new StringBuffer(" and upper(nbase)='"+pre.toUpperCase()+"'" );
							//					权限过滤
							
							if("1".equals(this.controlByUnitcod))
							{
								String whl_str=getWhlByUnits();
								if(whl_str.length()>0)
								{
									tempSql.append(" and  a0100 in ( select a0100 from "+this.gz_tablename+" where 1=1 "+whl_str+" and  upper(nbase)='"+pre.toUpperCase()+"' ) ");
								}
							}
							else
							{
								String whereIN=InfoUtils.getWhereINSql(this.userview,pre);
								whereIN="select a0100 "+whereIN;	
								tempSql.append(" and a0100 in ( "+whereIN+" )");
							}
							 buf.setLength(0);
							 buf.append("delete from gz_tax_mx");
							 buf.append(" where exists(select * from ");
							 buf.append(" "+tabName+" A");
							 buf.append(" where gz_tax_mx.nbase=A.nbase and gz_tax_mx.A0100=A.A0100 and ");
							 buf.append(" gz_tax_mx.A00Z0=A.tax_z0 and gz_tax_mx.A00Z1=A.tax_z1)");							
							 buf.append(" and salaryid=");
							 buf.append(this.salaryid);
							 buf.append(" and ( upper(YNSE_FIELD)='"+fieldname.toUpperCase()/*this.ynse_item.toUpperCase()*/+"' or YNSE_FIELD is null )") ;  //解决同一工资套多个算税公式
							 dao.update(buf.toString()+tempSql.toString());
						}
				}
				else
				{
				 
					 buf.setLength(0);
					 buf.append("delete from gz_tax_mx");
					 buf.append(" where exists(select * from ");
					 buf.append(" "+tabName+" A");
					 buf.append(" where gz_tax_mx.nbase=A.nbase and gz_tax_mx.A0100=A.A0100 and ");
					 buf.append(" gz_tax_mx.A00Z0=A.tax_z0 and gz_tax_mx.A00Z1=A.tax_z1)");
					 buf.append(" and salaryid=");
					 buf.append(this.salaryid);
					 buf.append(" and ( upper(YNSE_FIELD)='"+fieldname.toUpperCase()/*this.ynse_item.toUpperCase()*/+"' or YNSE_FIELD is null )") ;  //解决同一工资套多个算税公式
					 dao.update(buf.toString());
				
				} 
				
				 /**追加记录至个税明细表*/
			  //   appendRecordTaxMx2();
			     LazyDynaBean paramBean=new LazyDynaBean(); 
			     HashMap salarySetMap=getSalarySetMap();
			     paramBean.set("salarySetMap",salarySetMap);  
			     paramBean.set("dbean",this.dbean);  
			     paramBean.set("gz_tablename",this.gz_tablename);  
			     paramBean.set("salaryid",String.valueOf(this.salaryid));  
			     paramBean.set("tax_date_item",this.tax_date_item);  
			     paramBean.set("declare_date_item",this.declare_date_item);  
			     paramBean.set("tax_mode_item",this.tax_mode_item);  
			     paramBean.set("tax_mode",this.tax_mode);  
			     paramBean.set("desc_item",this.desc_item);  
			     paramBean.set("taxfldlist",this.taxfldlist);  
			     paramBean.set("isDeptControl",new Boolean(this.isDeptControl));  
			     paramBean.set("ctrlparam",this.ctrlparam);  
			     paramBean.set("k_base", String.valueOf(this.k_base));//基数
			     if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
						paramBean.set("tax_unit",this.tax_unit);   //有计税单位
					else
						paramBean.set("tax_unit","");   //无计税单位
			     paramBean.set("calculation",this.calculation); 
				 DbNameBo.appendRecordTaxMx2(paramBean ,this.conn,this.userview);
			     
				 dbw.execute("delete from "+tabName);
			}
		}
		
		catch(Exception ex)
		{
			if(this.errorInfo.length()==0)
				errorInfo="算税公式出错，请重新计算!";
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	
	public boolean isSequence(int dbflag,String name)
	{
		boolean flag=false;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			if(dbflag==Constant.ORACEL){
				RowSet rowSet=dao.search("select sequence_name from user_sequences where lower(sequence_name)='"+name+"'");
				if(rowSet.next())
					flag=true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 更新gz_taxTable序号
	 * @param name
	 */
	public void updateMusterRecidx()// throws GeneralException
	{
		StringBuffer strsql=new StringBuffer();
		try
		{
			DbWizard db=new DbWizard(this.conn);
			ContentDAO dao = new ContentDAO(this.conn);
			String tablename="t#"+this.userview.getUserName(); //"gz_taxTable_"+this.userview.getUserName();
			switch(Sql_switcher.searchDbServer())
			{
				case Constant.MSSQL:
					strsql.append("alter table "+tablename+" ");
					strsql.append(" add xxx int identity(1,1)");
					break;
				default:	
					    if(isSequence(Sql_switcher.searchDbServer(),this.userview.getUserName()+"xxx"))
					    {
					    	db.execute("drop sequence "+this.userview.getUserName()+"xxx");	
					    }
					    strsql.append("create sequence "+this.userview.getUserName()+"xxx increment by 1 start with 1");
					break;
			}
	        
			if(Sql_switcher.searchDbServer()==Constant.MSSQL)
			{
				RowSet rowSet=dao.search("select * from "+tablename+" where 1=2");
				ResultSetMetaData mt=rowSet.getMetaData();
				boolean isExist=false;
				for(int i=0;i<mt.getColumnCount();i++)
				{
					if("xxx".equalsIgnoreCase(mt.getColumnName(i+1)))
						isExist=true;
				}
				if(!isExist)
					db.execute(strsql.toString());	
			}
			else
				db.execute(strsql.toString());	
			strsql.setLength(0);
			switch(Sql_switcher.searchDbServer())
			{
				case Constant.MSSQL:
					strsql.append("update "+tablename+" set id=xxx");
					break;
				case Constant.DB2:
					strsql.append("update "+tablename+" set id=nextval for "+this.userview.getUserName()+"xxx");			
					break;
				case Constant.ORACEL:
					strsql.append("update "+tablename+" set id="+this.userview.getUserName()+"xxx.nextval");					
					break;
				default:
					strsql.append("update "+tablename+" set id=xxx");
					break;
			}	
			db.execute(strsql.toString());	
			strsql.setLength(0);			
			switch(Sql_switcher.searchDbServer())
			{
				case Constant.MSSQL:
					strsql.append("alter table "+tablename+" drop column xxx");
					break;
				default:
					strsql.append(" drop sequence "+this.userview.getUserName()+"xxx");
					break;
			}		
			db.execute(strsql.toString());	
		}
		catch(Exception ex)
		{
				
		}
	
	}
	
	//取得类别项目
	public HashMap getSalarySetMap()
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from salaryset where salaryid="+this.salaryid);
			while(rowSet.next())
			{
				map.put(rowSet.getString("itemid").toLowerCase(),"1");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
		
	}
	
	
	
	
	
	
	
	//追加记录至个税明细表
	public void  appendRecordTaxMx2()
	{
		
		try
		{
			updateMusterRecidx();
			HashMap salarySetMap=getSalarySetMap();
			ContentDAO dao = new ContentDAO(this.conn);
			int maxid=getMaxTax_maxid();
			
			String fieldname=(String)this.dbean.get("itemname");
			
			StringBuffer insert_sql=new StringBuffer("insert into gz_tax_mx (tax_max_id,salaryid,nbase,a0100,a00z2,a00z3,a00z0,a00z1,a0000,b0110,e0122,a0101,userflag");
			String tableName=this.gz_tablename;
			StringBuffer sql=new StringBuffer("select gtt.id+"+maxid+","+this.salaryid+",gtt.nbase,gtt.a0100,s.a00z2,s.a00z3,s.a00z0,s.a00z1,s.a0000,s.b0110,s.e0122,s.a0101");
			
			sql.append(",s.userflag");
		/*	if(this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager))
			        sql.append(",'"+this.manager+"'");
			else
					sql.append(",'"+this.userview.getUserName()+"'");
			*/
			if(tax_date_item!=null&&tax_date_item.length()>0)
			{
				sql.append(",s."+tax_date_item);      /**计税时间指标*/
				insert_sql.append(",tax_date");
			}
			if(declare_date_item!=null&&declare_date_item.length()>0)
			{
				sql.append(",s."+declare_date_item);  /**报税时间指标*/
				insert_sql.append(",declare_tax");
			}
			if(tax_mode_item!=null&&tax_mode_item.length()>0)
			{
				sql.append(",s."+tax_mode_item);      /**计算方式指标*/
				insert_sql.append(",taxmode");
			}
			if(desc_item!=null&&desc_item.length()>0)
			{
				sql.append(",s."+desc_item);          /**纳税项目说明*/
				insert_sql.append(",description");
			}
			
			sql.append(",gtt.taxitem,"+Sql_switcher.isnull("gtt.ynsd","0")+"+"+Sql_switcher.round(Sql_switcher.isnull("gtt.gs","0"),2)+",gtt.sskcs,"+this.k_base+",gtt.sl,"+Sql_switcher.round(Sql_switcher.isnull("gtt.gs","0"),2)+"");
			if(!this.gz_tablename.equalsIgnoreCase("t#"+this.userview.getUserName()+"_gzsp"))
				sql.append(",0");
			else
				sql.append(",1");
		//	sql.append(",0");
			insert_sql.append(",taxitem,ynse,sskcs,basedata,Sl,Sds,flag");
			for(int i=0;i<this.taxfldlist.size();i++)
			{
				Field field=(Field)this.taxfldlist.get(i);
				if(salarySetMap.get(field.getName().toLowerCase())!=null)
				{
					insert_sql.append(","+field.getName());
					sql.append(",s."+field.getName());
				}
			}
			
			//按业务划分操作单位 所得税管理根据指定的dept_id字段进行权限控制
			if(this.isDeptControl)
			{
				if(ctrlparam.getValue(SalaryCtrlParamBo.LS_DEPT)!=null&&ctrlparam.getValue(SalaryCtrlParamBo.LS_DEPT).trim().length()>0)
				{
					insert_sql.append(",deptid");
					sql.append(",S.");
					sql.append(ctrlparam.getValue(SalaryCtrlParamBo.LS_DEPT).trim());
				}
			}
			
			insert_sql.append(",ynse_field");
			sql.append(",'"+fieldname/*this.ynse_item*/+"'");
			
			sql.append(" from t#"+this.userview.getUserName()+" gtt,"+tableName+" s ");
			sql.append(" where gtt.a0100=s.a0100 and gtt.nbase=s.nbase and gtt.tax_z0=s.a00z0 and gtt.tax_z1=s.a00z1 ");
			dao.update(insert_sql.toString()+") "+sql.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
    
	
	
	/**
	 * 生成个税税率表数据
	 * 
	 * 
	 *全年一次性奖金 反算公式：
	    1、当月工资薪金所得>=基数
	     扣除基数 < 1:
	     应交个人所得税=(税率*(1-扣除基数)*(实发工资+已纳税所得额)-速算扣除数-已交个人所得税)/(1-税率+扣除基数*税率)
	
	     扣除基数 >= 1:
	     应交个人所得税=((已纳税所得额+实发工资-扣除基数)*税率-速算扣除数-已交个人所得税)/(1-税率)
	    2、当月工资薪金所得<基数
	     扣除基数 < 1:
	     应交个人所得税=(税率*(1-扣除基数)*(实发工资+已纳税所得额-当月工资薪金所得与基数的差额)-速算扣除数-已交个人所得税)/(1-税率+扣除基数*税率)
	
	     扣除基数 >= 1:
	     应交个人所得税=((已纳税所得额+实发工资-扣除基数-当月工资薪金所得与基数的差额)*税率-速算扣除数-已交个人所得税)/(1-税率)
	
	     当月工资薪金所得与基数的差额=相应基数-工资薪金所得
	
	    税率表中应纳税所得上限=(已纳税所得+应发工资上限)/12
	    税率表中应纳税所得下限=(已纳税所得+应发工资下限)/12
	    实发工资上限=应发工资上限-个税上限=应纳税所得上限*12-已纳税所得-个税上限
	    实发工资下限=应发工资下限-个税下限=应纳税所得下限*12-已纳税所得-个税下限
	 * 
	 * @param strStdId  税率表id
	 */
	private void execute_gs_taxData(String strStdId,String str_where)
	{
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			String fieldname=(String)this.dbean.get("itemname");
			
			String tableName=this.gz_tablename;
			String gzTaxTabName="t#"+this.userview.getUserName(); //"gz_taxTable_"+this.userview.getUserName();
			StringBuffer sql=new StringBuffer("");
			String aflag=this.ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
			//共享薪资类别，其他操作人员引入数据 (dengcan 2008/6/26 )
			String atableName="t#"+this.userview.getUserName()+"_gzsp"; //this.userview.getUserName()+"_sp_data";
			int count=1;
		//	if(this.calculation.equalsIgnoreCase("Y"))
		//		count=12;
			if(!this.gz_tablename.equalsIgnoreCase(atableName)&&this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager)&&aflag!=null&& "1".equals(aflag))
			{
				String dbpres=this.templatevo.getString("cbase");
				//应用库前缀
				String[] dbarr=StringUtils.split(dbpres, ",");
				for(int i=0;i<dbarr.length;i++)
				{
					String pre=dbarr[i];
					
					StringBuffer strsql=new StringBuffer(" and upper("+tableName+".nbase)='"+pre.toUpperCase()+"'" );
					//权限过滤
					if("1".equals(this.controlByUnitcod))
					{
						String whl_str=getWhlByUnits();
						if(whl_str.length()>0)
						{
							strsql.append(whl_str);
						}
					}
					else
					{
						String whereIN=InfoUtils.getWhereINSql(this.userview,pre);
						whereIN="select a0100 "+whereIN;	
						strsql.append(" and "+tableName+".a0100 in ( "+whereIN+" )");
					}
					
					sql.setLength(0);
					sql.append("insert into "+gzTaxTabName+" (taxitem,taxid,ynse_down,ynse_up,sl,sskcs,flag,kc_base,description,a0100,nbase,tax_z0,tax_z1,ynsd,is_sde,tax_date");
					if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
						sql.append(",taxunit");
					if(this.hiredate!=null&&this.hiredate.trim().length()>0)
						sql.append(",hiredate");
					if(this.disability!=null&&this.disability.trim().length()>0)
						sql.append(",disability");
					
					sql.append(" ) ");   //20141222 dengcan  依据计税时间来算
					
					
					sql.append("select taxitem,taxid,ynse_down*"+count+",ynse_up*"+count+",sl,sskcs*"+count+",flag,"+Sql_switcher.isnull("kc_base","0")+",description,a0100,nbase,a00z0,a00z1,"+Sql_switcher.isnull(this.ynse_item,"0")+",0,"+tableName+"."+this.tax_date_item+" as tax_date");  //20151106  dengcan  防止tax_date_item指标定义为a00z0,SQL出错
					if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
						sql.append(","+Sql_switcher.sqlNull(this.tax_unit,"-1"));
					if(this.hiredate!=null&&this.hiredate.trim().length()>0)
						sql.append(","+tableName+"."+hiredate);
					if(this.disability!=null&&this.disability.trim().length()>0)
						sql.append(","+tableName+"."+disability);
					sql.append(" from gz_taxrate_item,"+tableName+" where gz_taxrate_item.taxid="+strStdId);
					if(str_where.trim().length()>0)
						 sql.append(" and ( "+str_where+" )");
			//		sql.append(" and "+tableName+"."+this.ynse_item+" is not null and "+tableName+"."+this.ynse_item+"<>0 ");
					sql.append(" "+strsql.toString()+" order by a0100,nbase,a00z0,a00z1,taxitem");
					dao.update(sql.toString());
				}
			}
			else
			{
				sql.setLength(0);
				sql.append("insert into "+gzTaxTabName+" (taxitem,taxid,ynse_down,ynse_up,sl,sskcs,flag,kc_base,description,a0100,nbase,tax_z0,tax_z1,ynsd,is_sde,tax_date");
				if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
					sql.append(",taxunit");
				if(this.hiredate!=null&&this.hiredate.trim().length()>0)
					sql.append(",hiredate");
				if(this.disability!=null&&this.disability.trim().length()>0)
					sql.append(",disability");
				sql.append(" ) ");  //20141222 dengcan  依据计税时间来算
				sql.append("select taxitem,taxid,ynse_down*"+count+",ynse_up*"+count+",sl,sskcs*"+count+",flag,"+Sql_switcher.isnull("kc_base","0")+",description,a0100,nbase,a00z0,a00z1,"+Sql_switcher.isnull(this.ynse_item,"0")+",0,"+tableName+"."+this.tax_date_item+" as tax_date");//20151106  dengcan  防止tax_date_item指标定义为a00z0,SQL出错
				if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
					sql.append(","+Sql_switcher.sqlNull(this.tax_unit,"-1"));
				if(this.hiredate!=null&&this.hiredate.trim().length()>0)
					sql.append(","+tableName+"."+hiredate);
				if(this.disability!=null&&this.disability.trim().length()>0)
					sql.append(","+tableName+"."+disability);
				
				sql.append(" from gz_taxrate_item,"+tableName+" where gz_taxrate_item.taxid="+strStdId);
				if(str_where.trim().length()>0)
					 sql.append(" and ( "+str_where+" )");
		//		sql.append(" and "+tableName+"."+this.ynse_item+" is not null and "+tableName+"."+this.ynse_item+"<>0 ");
				sql.append(" order by a0100,nbase,a00z0,a00z1,taxitem"); 
				dao.update(sql.toString());
			}
			
			if("3".equals(this.tax_mode)) //企业年金
			{ 
				sql.setLength(0);
				sql.append("update "+gzTaxTabName+" set gzxj_current=(select sum("+Sql_switcher.sqlNull("ynse",0)+") from gz_tax_mx where taxmode='1' ");
				sql.append(" and nbase="+gzTaxTabName+".nbase and  a0100="+gzTaxTabName+".a0100 and tax_date="+gzTaxTabName+".tax_z0 ");
				if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
					sql.append(" and "+Sql_switcher.sqlNull("taxunit","-1")+"="+gzTaxTabName+".taxunit");
				sql.append("  group by nbase,a0100,tax_date ");	
				if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
					sql.append(","+Sql_switcher.sqlNull("taxunit","-1")+" as taxunit");
				
				sql.append(" ) where exists (select null from gz_tax_mx where taxmode='1' ");
				sql.append(" and nbase="+gzTaxTabName+".nbase and  a0100="+gzTaxTabName+".a0100 and tax_date="+gzTaxTabName+".tax_z0   group by nbase,a0100,tax_date ");	
				if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
					sql.append(","+Sql_switcher.sqlNull("taxunit","-1"));
				sql.append(" ) ");
				dao.update(sql.toString());
			}
			
			
//			计算已纳税所得 和 已交个税
			computeYNSD_YJGS();
			
			
			if("5".equals(this.tax_mode)&& "Y".equalsIgnoreCase(this.calculation))
			{ 
				
				sql.setLength(0);
				sql.append("update "+gzTaxTabName+" set kcbase_num=(select month_count from t#"+userview.getUserName()+"_gz_5 where "+gzTaxTabName+".a0100=t#"+userview.getUserName()+"_gz_5.a0100 and  upper("+gzTaxTabName+".nbase)=upper(t#"+userview.getUserName()+"_gz_5.nbase)   ");
				if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
					  sql.append(" and "+gzTaxTabName+".taxunit=t#"+userview.getUserName()+"_gz_5.taxunit ");      
				sql.append("  )");
				dao.update(sql.toString());
				dao.update("update "+gzTaxTabName+" set kcbase_num="+Sql_switcher.isnull("kcbase_num","0")+"+1");

				if(this.hiredate!=null&&this.hiredate.trim().length()>0)
				{
					dao.update("update "+gzTaxTabName+" set kcbase_num="+Sql_switcher.month("tax_date")+"   where "+Sql_switcher.year("tax_date")+">"+Sql_switcher.year("hiredate"));
					dao.update("update "+gzTaxTabName+" set kcbase_num="+Sql_switcher.month("tax_date")+"-"+Sql_switcher.month("hiredate")+"+1   where "+Sql_switcher.year("tax_date")+"="+Sql_switcher.year("hiredate")+" and "+Sql_switcher.month("tax_date")+">="+Sql_switcher.month("hiredate"));
				} 
				
				sql.setLength(0);
				sql.append("update "+gzTaxTabName+" set gs=(");
				sql.append(" (("+Sql_switcher.isnull("ynse_sd","0")+"+"+Sql_switcher.isnull("ynsd","0")+"-"+this.k_base+"*kcbase_num-"+Sql_switcher.isnull("yjgs","0")+")*sl-Sskcs)/(1-sl)-"+Sql_switcher.isnull("yjgs","0")+" ");
				sql.append(" ) ");
				dao.update(sql.toString());
				
				sql.setLength(0);
				String gzTaxTabName2=gzTaxTabName+"_2";
				DbWizard dbw=new DbWizard(this.conn);
				dbw.dropTable(gzTaxTabName2);
				if(Sql_switcher.searchDbServer()==2)
					sql.append("create table "+gzTaxTabName2+" as ");
				sql.append(" select *"); 
				if(Sql_switcher.searchDbServer()!=2)
					sql.append(" into "+gzTaxTabName2); 
				sql.append(" from "+gzTaxTabName+" where sskcs=0");
				dao.update(sql.toString());
				dao.update("update "+gzTaxTabName2+" set sl=0,gs=0");
				
				sql.setLength(0);
				sql.append("delete "+gzTaxTabName+" where ("+Sql_switcher.isnull("ynsd","0")+"+"+Sql_switcher.isnull("gs","0")+"+"+Sql_switcher.isnull("ynse_sd","0")+"-"+this.k_base+"*kcbase_num )<ynse_down or ("+Sql_switcher.isnull("ynsd","0")+"+"+Sql_switcher.isnull("gs","0")+"+"+Sql_switcher.isnull("ynse_sd","0")+"-"+this.k_base+"*kcbase_num)>ynse_up ");
				dao.update(sql.toString());
				//[49221] 删掉  个税恰在2个税档中间会有2条记录，需删掉最大的那条记录
				ArrayList delList=new ArrayList();
				RowSet rowSet=dao.search(" select a0100,nbase,"+Sql_switcher.year("tax_z0")+" a_year,"+Sql_switcher.month("tax_z0")+" a_month,tax_z1 ,max(taxitem)  taxitem from "+gzTaxTabName+" group by a0100,nbase,tax_z0,tax_z1 having count(a0100)>1");
				while(rowSet.next())
				{
					ArrayList objList=new ArrayList();
					objList.add(rowSet.getString("a0100"));
					objList.add(rowSet.getString("nbase"));
					objList.add(rowSet.getInt("a_year"));
					objList.add(rowSet.getInt("a_month"));
					objList.add(rowSet.getInt("tax_z1"));
					objList.add(rowSet.getInt("taxitem"));
					delList.add(objList); 
				}
				if(delList.size()>0)
					dao.batchUpdate("delete from "+gzTaxTabName+" where a0100=? and nbase=? and "+Sql_switcher.year("tax_z0")+"=? and "+Sql_switcher.month("tax_z0")+"=? and tax_z1=? and taxitem=? ", delList);
				if(rowSet!=null)
					rowSet.close();
				
				
				//2019/11/05 反算累计预扣缴税费
			 	if("Y".equalsIgnoreCase(calculation))
				{
					if(this.disability!=null&&this.disability.trim().length()>0)
						dao.update("update "+gzTaxTabName+" set ljse=gs+"+Sql_switcher.sqlNull("yjgs",0)+" where  (gs+"+Sql_switcher.sqlNull("yjgs",0)+")>=0 and "+Sql_switcher.isnull("disability","'0'")+"!='1'" );
					else
						dao.update("update "+gzTaxTabName+" set ljse=gs+"+Sql_switcher.sqlNull("yjgs",0)+" where  (gs+"+Sql_switcher.sqlNull("yjgs",0)+")>=0");
					if(this.disability!=null&&this.disability.trim().length()>0)
						dao.update("update "+gzTaxTabName+" set ljse=0 where gs is null or (gs+"+Sql_switcher.sqlNull("yjgs",0)+")<0  and "+Sql_switcher.isnull("disability","'0'")+"!='1' ");
					else
						dao.update("update "+gzTaxTabName+" set ljse=0 where gs is null or (gs+"+Sql_switcher.sqlNull("yjgs",0)+")<0");
					 
				}
				 
				
				
				sql.setLength(0);
				sql.append("update "+gzTaxTabName+" set gs=0 where "+Sql_switcher.isnull("gs","0")+"<0 ");
				dao.update(sql.toString());
				//写入个税为0的记录
				sql.setLength(0);
				String taxunit_str="";
				if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
					taxunit_str=",taxunit";
				sql.append("insert into "+gzTaxTabName+" (taxitem,taxid,ynse_down,ynse_up,sl,sskcs,flag,kc_base,kcbase_num,description,tax_z0,nbase,tax_z1,yjgs,gz_sde,sf_down,ynsd,is_sde,gs_up,ynse_sd,tax_date,a0100,gs_down,sf_up,gs"+taxunit_str+") ");
				sql.append(" select taxitem,taxid,ynse_down,ynse_up,sl,sskcs,flag,kc_base,kcbase_num,description,tax_z0,nbase,tax_z1,yjgs,gz_sde,sf_down,ynsd,is_sde,gs_up,ynse_sd,tax_date,a0100,gs_down,sf_up,gs"+taxunit_str+" from "+gzTaxTabName2);
				sql.append(" where not exists ( select null from "+gzTaxTabName+" where "+gzTaxTabName+".a0100="+gzTaxTabName2+".a0100 and "+gzTaxTabName+".nbase="+gzTaxTabName2+".nbase )");
				dao.update(sql.toString());
				
				sql.setLength(0);
				sql.append("update "+gzTaxTabName+" set yyjl=1 where exists (select null from t#"+userview.getUserName()+"_gz_6 where "+gzTaxTabName+".a0100=t#"+userview.getUserName()+"_gz_6.a0100 and  "+gzTaxTabName+".nbase=t#"+userview.getUserName()+"_gz_6.nbase   )");
				dao.update(sql.toString());
				dbw.dropTable("t#"+userview.getUserName()+"_gz_6"); 
				dbw.dropTable(gzTaxTabName2);
			}
			else
			{ 
				//个税下限
				sql.setLength(0);
				sql.append("update "+gzTaxTabName+" set gs_down=(");
				sql.append("ynse_down*(1-kc_base)*sl-"+Sql_switcher.isnull("sskcs","0")+"-"+Sql_switcher.isnull("yjgs","0"));
				sql.append(") where kc_base<1");
				dao.update(sql.toString());
				sql.setLength(0);
				sql.append("update "+gzTaxTabName+" set gs_down=(");
				sql.append("(ynse_down-kc_base)*sl-"+Sql_switcher.isnull("sskcs","0")+"-"+Sql_switcher.isnull("yjgs","0"));
				sql.append(") where kc_base>=1");
				
				//个税上限
				sql.setLength(0);
				sql.append("update "+gzTaxTabName+" set gs_up=(");
				sql.append(" ynse_up*(1-kc_base)*sl-"+Sql_switcher.isnull("sskcs","0")+"-"+Sql_switcher.isnull("yjgs","0"));
				sql.append(") where kc_base<1");
				dao.update(sql.toString());			
				sql.setLength(0);
				sql.append("update "+gzTaxTabName+" set gs_up=(");
				sql.append("(ynse_up-kc_base)*sl-"+Sql_switcher.isnull("sskcs","0")+"-"+Sql_switcher.isnull("yjgs","0"));
				sql.append(") where kc_base>=1");
				dao.update(sql.toString());
				
				if("1".equals(this.tax_mode)|| "5".equals(this.tax_mode)|| "4".equals(this.tax_mode)|| "3".equals(this.tax_mode))
				{
					//实发工资下限  sf_down
					sql.setLength(0);
					sql.append("update "+gzTaxTabName+" set sf_down=(");
					sql.append(" ynse_down+"+k_base+"-"+Sql_switcher.isnull("ynse_sd","0")+"-"+Sql_switcher.isnull("gs_down","0")+")"); 
					dao.update(sql.toString());
					//实发工资上限  sf_up
					sql.setLength(0);
					sql.append("update "+gzTaxTabName+" set sf_up=(");
					sql.append(" ynse_up+"+k_base+"-"+Sql_switcher.isnull("ynse_sd","0")+"-"+Sql_switcher.isnull("gs_up","0")+")"); 
					dao.update(sql.toString());
				}
				else if("2".equals(this.tax_mode))  //全年一性次奖金
				{
	//				实发工资下限  sf_down
					sql.setLength(0);
					sql.append("update "+gzTaxTabName+" set sf_down=(");
					sql.append(" ynse_down*12-"+Sql_switcher.isnull("ynse_sd","0")+"-"+Sql_switcher.isnull("gs_down","0")+")"); 
					dao.update(sql.toString());
					//实发工资上限  sf_up
					sql.setLength(0);
					sql.append("update "+gzTaxTabName+" set sf_up=(");
					sql.append(" ynse_up*12-"+Sql_switcher.isnull("ynse_sd","0")+"-"+Sql_switcher.isnull("gs_up","0")+")"); 
					dao.update(sql.toString());
				}
				//根据实发工资上下限，可以确定实发工资对应税率  flag=0:上限封闭  =1：下限封闭
				sql.setLength(0);
				
				if("3".equals(this.tax_mode))  //企业年金
				{
					String str="case when "+Sql_switcher.sqlNull("gzxj_current",0)+">="+this.k_base+" then ynsd  when "+Sql_switcher.sqlNull("gzxj_current",0)+"<"+this.k_base+" then  ynsd-("+this.k_base+"-"+Sql_switcher.sqlNull("gzxj_current",0)+") end";
					   
					sql.append("delete from "+gzTaxTabName+" where not exists ( select * from  "+gzTaxTabName+" a  where ((flag=0 and "+str+">sf_down and "+str+"<=sf_up  ) or (flag=1  and "+str+">=sf_down and "+str+"<sf_up)) ");
					sql.append(" and "+gzTaxTabName+".a0100=a0100 and "+gzTaxTabName+".nbase=nbase and "+gzTaxTabName+".tax_z0=tax_z0 and "+gzTaxTabName+".tax_z1=tax_z1 and "+gzTaxTabName+".taxitem=taxitem ");
					sql.append(" ) ");
					
				}
				else
				{
					sql.append("delete from "+gzTaxTabName+" where not exists ( select * from  "+gzTaxTabName+" a  where ((flag=0 and ynsd>sf_down and ynsd<=sf_up  ) or (flag=1  and ynsd>=sf_down and ynsd<sf_up)) ");
					sql.append(" and "+gzTaxTabName+".a0100=a0100 and "+gzTaxTabName+".nbase=nbase and "+gzTaxTabName+".tax_z0=tax_z0 and "+gzTaxTabName+".tax_z1=tax_z1 and "+gzTaxTabName+".taxitem=taxitem ");
					sql.append(" ) ");
				}
				
				dao.update(sql.toString());
				
				//共享薪资类别，其他操作人员引入数据 (dengcan 2008/6/26 )
				if(!this.gz_tablename.equalsIgnoreCase(atableName)&&this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager)&&aflag!=null&& "1".equals(aflag))
				{
					String dbpres=this.templatevo.getString("cbase");
					//应用库前缀
					String[] dbarr=StringUtils.split(dbpres, ",");
					for(int i=0;i<dbarr.length;i++)
					{
						String pre=dbarr[i];
						
						StringBuffer strsql=new StringBuffer(" and upper("+tableName+".nbase)='"+pre.toUpperCase()+"'" );
						//权限过滤
						if("1".equals(this.controlByUnitcod))
						{
							String whl_str=getWhlByUnits();
							if(whl_str.length()>0)
							{
								strsql.append(whl_str);
							}
						}
						else
						{
							String whereIN=InfoUtils.getWhereINSql(this.userview,pre);
							whereIN="select a0100 "+whereIN;	
							strsql.append(" and "+tableName+".a0100 in ( "+whereIN+" )");
						}
						sql.setLength(0);
						sql.append("insert into "+gzTaxTabName+" (a0100,nbase,tax_z0,tax_z1,ynsd) ");
						sql.append("select a0100,nbase,a00z0,a00z1,"+this.ynse_item);
						sql.append(" from "+tableName+" where 1=1 ");
						if(str_where.trim().length()>0)
							 sql.append(" and ( "+str_where+" )");
						sql.append("  and not exists (select null from "+gzTaxTabName+"  where "+gzTaxTabName+".a0100="+tableName+".a0100 and  upper("+gzTaxTabName+".nbase)=upper("+tableName+".nbase)  )   and "+tableName+"."+this.ynse_item+" is not null and "+tableName+"."+this.ynse_item+"<>0  "+strsql.toString()+" order by a0100,nbase,a00z0,a00z1");
						dao.update(sql.toString());
					}
				}
				else
				{
					sql.setLength(0);
					sql.append("insert into "+gzTaxTabName+" (a0100,nbase,tax_z0,tax_z1,ynsd) ");
					sql.append("select a0100,nbase,a00z0,a00z1,"+this.ynse_item);
					sql.append(" from "+tableName+" where 1=1 ");
					if(str_where.trim().length()>0)
						 sql.append(" and ( "+str_where+" )");
					sql.append("   and not exists (select null from "+gzTaxTabName+"  where "+gzTaxTabName+".a0100="+tableName+".a0100 and  upper("+gzTaxTabName+".nbase)=upper("+tableName+".nbase)  )    and "+tableName+"."+this.ynse_item+" is not null and "+tableName+"."+this.ynse_item+"<>0 order by a0100,nbase,a00z0,a00z1");
					dao.update(sql.toString());
				}
				
				
				
				
				//计算--应交个人所得税
				sql.setLength(0);
				if("3".equals(this.tax_mode))  //企业年金
				{
					sql.append("update "+gzTaxTabName+" set gs=(");
					sql.append(" (("+Sql_switcher.isnull("ynse_sd","0")+"+"+Sql_switcher.isnull("ynsd","0")+")*sl-Sskcs-"+Sql_switcher.isnull("yjgs","0")+")/(1-sl) ");
					sql.append(" ) where   "+Sql_switcher.sqlNull("gzxj_current",0)+">="+this.k_base+"");
					dao.update(sql.toString());
					sql.setLength(0);
					sql.append("update "+gzTaxTabName+" set gs=(");
					sql.append(" (("+Sql_switcher.isnull("ynse_sd","0")+"+"+Sql_switcher.isnull("ynsd","0")+"-("+this.k_base+"-"+Sql_switcher.sqlNull("gzxj_current",0)+"))*sl-Sskcs-"+Sql_switcher.isnull("yjgs","0")+")/(1-sl) ");
					sql.append(" ) where   "+Sql_switcher.sqlNull("gzxj_current",0)+"<"+this.k_base+"");
					dao.update(sql.toString());
				}
				else if("1".equals(this.tax_mode)|| "4".equals(this.tax_mode)|| "5".equals(this.tax_mode))  //劳务报酬 /工资薪金
				{
					sql.append("update "+gzTaxTabName+" set gs=(");
					sql.append(" (((1-kc_base)*("+Sql_switcher.isnull("ynse_sd","0")+"+"+Sql_switcher.isnull("ynsd","0")+")-"+k_base+")*sl-Sskcs-"+Sql_switcher.isnull("yjgs","0")+")/(1-sl+kc_base*sl) ");
					sql.append(" ) where  kc_base<1");
					dao.update(sql.toString());
					sql.setLength(0);
					sql.append("update "+gzTaxTabName+" set gs=(");
					sql.append(" (("+Sql_switcher.isnull("ynse_sd","0")+"+"+Sql_switcher.isnull("ynsd","0")+"-"+k_base+"-kc_base)*sl-Sskcs-"+Sql_switcher.isnull("yjgs","0")+")/(1-sl) ");
					sql.append(" ) where  kc_base>=1");
					dao.update(sql.toString());
				}
				else if("2".equals(this.tax_mode))  //全年一性次奖金
				{
					//写入当月工资所得额
					setGz_sdeValue(gzTaxTabName);
					float k_base=getKBase();
					
					for(int z=0;z<2;z++)
					{ 
						sql.setLength(0);
						StringBuffer taxable=new StringBuffer();
						StringBuffer where_str=new StringBuffer("");
						if(z==0) //工资薪金所得>=基数
						{
							
						//	where_str.append(" ( "+Sql_switcher.isnull(gzTaxTabName+".gz_sde","0")+">="+k_base+" ) or "+gzTaxTabName+".gz_sde is  null");
							where_str.append(" ( ( "+Sql_switcher.isnull(gzTaxTabName+".gz_sde","0")+">="+k_base+" ) or "+gzTaxTabName+".is_sde=0 ) ");
						}
						if(z==1) //工资薪金所得<基数
						{
							//where_str.append("("+Sql_switcher.isnull(gzTaxTabName+".gz_sde","0")+"+"+Sql_switcher.isnull(gzTaxTabName+".ynsd","0")+")<"+k_base);
							//where_str.append(Sql_switcher.isnull(gzTaxTabName+".gz_sde","0")+"<"+k_base+" and "+gzTaxTabName+".gz_sde is not null");
							where_str.append(" ( "+Sql_switcher.isnull(gzTaxTabName+".gz_sde","0")+"<"+k_base+" and "+gzTaxTabName+".is_sde=1 ) ");
						}
						String sub_sql="";
				//		if(z==1) //工资薪金所得<基数
				//			sub_sql="-("+k_base+"-"+Sql_switcher.isnull(gzTaxTabName+".gz_sde","0")+")";
						
						
						sql.append("update "+gzTaxTabName+" set gs=(");
						sql.append("(sl*(1-kc_base)*("+Sql_switcher.isnull("ynsd","0")+"+"+Sql_switcher.isnull("ynse_sd","0")+sub_sql+")-Sskcs-"+Sql_switcher.isnull("yjgs","0")+")/(1-sl+kc_base*sl) ");
						sql.append(" ) where  kc_base<1 and "+where_str);
						dao.update(sql.toString());
						sql.setLength(0);
						
						
						sql.append("update "+gzTaxTabName+" set gs=(");
						sql.append(" (("+Sql_switcher.isnull("ynse_sd","0")+"+"+Sql_switcher.isnull("ynsd","0")+"-kc_base"+sub_sql+")*sl-Sskcs-"+Sql_switcher.isnull("yjgs","0")+")/(1-sl)");
						sql.append(" ) where kc_base>=1 and "+where_str);
						dao.update(sql.toString());
					}
				}
			
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	//计算已纳税所得 和 已交个税
	public void computeYNSD_YJGS()
	{
		try
		{
			String fieldname=(String)dbean.get("itemname");
			
			ContentDAO dao=new ContentDAO(this.conn);
			String tableName="t#"+this.userview.getUserName(); //"gz_taxTable_"+this.userview.getUserName();
			
			String tempTableName=tableName+"_temp";
			DbWizard dbw=new DbWizard(this.conn);
			dbw.dropTable(tempTableName);
			
			StringBuffer _buf=new StringBuffer("");
			if(Sql_switcher.searchDbServer()==2)
				_buf.append("create table "+tempTableName+" as ");
			_buf.append(" select gz_tax_mx.a0100,gz_tax_mx.nbase,"+Sql_switcher.isnull("sum(sds)", "0")+" sds,"+Sql_switcher.isnull("sum(ynse)", "0")+" ynse ");
			if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
				_buf.append(","+tableName+".taxunit");
			if(Sql_switcher.searchDbServer()!=2)
				_buf.append(" into "+tempTableName); 
		//	_buf.append(" from gz_tax_mx ");
			String select_str="upper(nbase) nbase,a0100,a00z0,a00z1,tax_date,taxmode,sds,ynse,salaryid,YNSE_FIELD";
			if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
				select_str+=",taxunit"; 
   		    _buf.append(" from  ( select "+select_str+" from gz_tax_mx  union all select "+select_str+" from taxarchive ) gz_tax_mx  ");
			_buf.append(", (select distinct a0100,nbase,tax_z0,tax_z1,tax_date");
			if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
				_buf.append(",taxunit");
			//2019/11/01
			if("Y".equalsIgnoreCase(calculation)&&this.hiredate!=null&&this.hiredate.trim().length()>0)
				_buf.append(",hiredate");
			_buf.append("  from "+tableName+" ) "+tableName+" "); // ,gz_taxrate_item");   //20141222 dengcan  依据计税时间来算
			_buf.append(" where  gz_tax_mx.a0100="+tableName+".a0100  and upper(gz_tax_mx.nbase)=upper("+tableName+".nbase) ");
			_buf.append(" and gz_tax_mx.taxmode='"+this.tax_mode+"' "); //计税方式必须一致
			if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
				_buf.append(" and "+Sql_switcher.sqlNull("gz_tax_mx.taxunit","-1")+"="+tableName+".taxunit "); 
			_buf.append(" and  "+Sql_switcher.year(tableName+".tax_date")+"="+Sql_switcher.year("gz_tax_mx.tax_date")+" "); //20141222 dengcan  依据计税时间来算
			
			
			//2019/11/01
			if("Y".equalsIgnoreCase(calculation)&&this.hiredate!=null&&this.hiredate.trim().length()>0)
			{
				_buf.append(" and  ( "+Sql_switcher.year("gz_tax_mx.tax_date")+">"+Sql_switcher.year(tableName+".hiredate")+" or (  "+Sql_switcher.year("gz_tax_mx.tax_date")+"="+Sql_switcher.year(tableName+".hiredate")+"  and "+Sql_switcher.month("gz_tax_mx.tax_date")+">="+Sql_switcher.month(tableName+".hiredate")+"   )    ) ");
			
			}
			
			
			
			if(!"Y".equalsIgnoreCase(calculation))//累计预扣法
				_buf.append(" and  "+Sql_switcher.month(tableName+".tax_date")+"="+Sql_switcher.month("gz_tax_mx.tax_date")+"  ");
			
			_buf.append(" and ( ( gz_tax_mx.salaryid="+this.salaryid+"  and (  "+tableName+".tax_z1<>gz_tax_mx.a00z1 ");
			if("Y".equalsIgnoreCase(calculation))//累计预扣法
				_buf.append(" or "+tableName+".tax_z0<>gz_tax_mx.a00z0");
			_buf.append("  or ("+tableName+".tax_z1=gz_tax_mx.a00z1  and   upper(YNSE_FIELD)!='"+fieldname.toUpperCase()/*this.ynse_item.toUpperCase()*/+"'))   ) or (gz_tax_mx.salaryid<>"+this.salaryid+"  ) ) ");
			_buf.append(" group by gz_tax_mx.nbase,gz_tax_mx.a0100 ");
			if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
				_buf.append(","+tableName+".taxunit");
			dbw.execute(_buf.toString());
			dbw.execute("create index gtt_"+this.userview.getUserName()+"_i on "+tempTableName+" (a0100,nbase)");
			
			 
			StringBuffer sql=new StringBuffer("");
			
			sql.append("update "+tableName+" set ynse_sd=(select ynse from  "); 
			sql.append(tempTableName);
			sql.append(" a where "+tableName+".a0100=a.a0100 and upper("+tableName+".nbase)=upper(a.nbase) ");
			if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
				sql.append(" and "+tableName+".taxunit=a.taxunit ");
			sql.append(" ) where exists ( ");
			sql.append(" select null from ");
			
			sql.append(tempTableName);
	 
			sql.append(" a where "+tableName+".a0100=a.a0100 and upper("+tableName+".nbase)=upper(a.nbase) ");
			if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
				sql.append(" and "+tableName+".taxunit=a.taxunit ");
			sql.append(" )");
			dao.update(sql.toString());
			
			sql.setLength(0);
			sql.append("update "+tableName+" set yjgs=(select sds from ");
			
			
			sql.append(tempTableName);
			
	 
			sql.append(" a where "+tableName+".a0100=a.a0100 and upper("+tableName+".nbase)=upper(a.nbase) ");
			if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
				sql.append(" and "+tableName+".taxunit=a.taxunit ");
			sql.append(" ) where exists( ");
			sql.append(" select null from ");
			
			sql.append(tempTableName);
        
			sql.append(" a where "+tableName+".a0100=a.a0100 and upper("+tableName+".nbase)=upper(a.nbase)  ");
			if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
				sql.append(" and "+tableName+".taxunit=a.taxunit ");
			sql.append(" )");
			dao.update(sql.toString()); 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
	
	
	
	
	
	
	
	
	
	/**
	 * 创建工资税率表
	 * @throws GeneralException
	 */
	private void create_gz_taxTable()throws GeneralException
	{
		DbWizard dbWizard=new DbWizard(this.conn);	
		ContentDAO dao = new ContentDAO(this.conn);
		String tableName="t#"+this.userview.getUserName(); //"gz_taxTable_"+this.userview.getUserName();
		String sql="";
		try
		{
		
				dbWizard.dropTable(tableName);
				if(Sql_switcher.searchDbServer()==2)
					sql="create table "+tableName+" as select * from gz_taxrate_item where 1=2 ";
				else 
					sql="select *  into "+tableName+"  from gz_taxrate_item where 1=2 ";
				dao.update(sql);
				DBMetaModel dbmodel=new DBMetaModel(this.conn);
				dbmodel.reloadTableModel(tableName);
				
				Table table=new Table(tableName);
				Field field=new Field("tax_z0","tax_z0");
				field.setDatatype(DataType.DATE);
				table.addField(field);
				field=new Field("tax_z1","tax_z1");
				field.setDatatype(DataType.INT);
				table.addField(field);
				field=new Field("a0100","a0100");
				field.setDatatype(DataType.STRING);
				field.setLength(8);
				table.addField(field);	
				field=new Field("nbase","nbase");
				field.setDatatype(DataType.STRING);
				field.setLength(3);
				table.addField(field);	
				field=new Field("gs_down","gs_down");  //个税上限
				field.setDatatype(DataType.FLOAT);
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);	
				field=new Field("gs_up","gs_up");      //个税下限
				field.setDatatype(DataType.FLOAT);
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);	
				field=new Field("sf_down","sf_down");  //实发工资上限
				field.setDatatype(DataType.FLOAT);
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);	
				field=new Field("sf_up","sf_up");      //实发工资下限
				field.setDatatype(DataType.FLOAT);
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);	
				field=new Field("ynsd","ynsd");        //已纳税所得
				field.setDatatype(DataType.FLOAT);
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);	
				
				field=new Field("gs","gs");        //个税
				field.setDatatype(DataType.FLOAT);
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);	
				
				field=new Field("ynse_sd","ynse_sd");        //gz_tax_mx 表 前几次已纳税所得额de总计
				field.setDatatype(DataType.FLOAT);
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);	
				
				field=new Field("yjgs","yjgs");        //gz_tax_mx 表 前几次已交个税总计
				field.setDatatype(DataType.FLOAT);
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
				field=new Field("id","id");        
				field.setDatatype(DataType.INT);
				field.setLength(8);
				table.addField(field);
				
				field=new Field("gz_sde","gz_sde");        //工资薪金所得
				field.setDatatype(DataType.FLOAT);
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
				field=new Field("is_sde","is_sde");        //是否发过工资薪金
				field.setDatatype(DataType.INT);
				field.setLength(8);
				table.addField(field);
				
				if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
				{
					field=new Field("taxunit","taxunit"); //计税单位
					field.setDatatype(DataType.STRING);
					field.setLength(50); 
					table.addField(field);
				}
				if(this.hiredate!=null&&this.hiredate.trim().length()>0)
				{
					field=new Field("hiredate","hiredate"); //入职时间
					field.setDatatype(DataType.DATE); 
					table.addField(field);
				}
				if(this.disability!=null&&this.disability.trim().length()>0)
				{
					field=new Field("disability","disability"); //是否残疾人
					field.setDatatype(DataType.STRING);
					field.setLength(2); 
					table.addField(field);
				} 
				if("3".equals(this.tax_mode))
				{
					field=new Field("gzxj_current","gzxj_current");      
					field.setDatatype(DataType.FLOAT);
					field.setLength(12);
					field.setDecimalDigits(4);
					table.addField(field);
				}
				 //20141222 dengcan  依据计税时间来算
				field=new Field("tax_date","tax_date");
				field.setDatatype(DataType.DATE);
				table.addField(field);
				
				//纳税人当年截至本月在本单位的任职受雇月份数 20181221
				field=new Field("kcbase_num","kcbase_num");
				field.setDatatype(DataType.INT);
				field.setLength(12);
				table.addField(field);
				
				field=new Field("yyjl","yyjl");//本月已有记录
				field.setDatatype(DataType.INT);
				field.setLength(12);
				table.addField(field);
				
				//2019/11/05 反算累计预扣缴税费
				field=new Field("ljse","ljse");      
				field.setDatatype(DataType.FLOAT);
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
				dbWizard.addColumns(table);
			  
				table.clear();
				field=new Field("taxitem","taxitem");        
				field.setDatatype(DataType.INT);
				field.setLength(10);
				field.setNullable(true); 
				table.addField(field);
				field=new Field("taxid","taxid");        
				field.setDatatype(DataType.INT);
				field.setLength(10);
				field.setNullable(true); 
				table.addField(field);
				
				
				
				dbWizard.alterColumns(table);
			//	dao.update("alter table GZ_TAXTABLE_SU modify(taxitem null)");
			//	dao.update("alter table GZ_TAXTABLE_SU modify(taxid null)");
				dbmodel.reloadTableModel(tableName);
				dbWizard.execute("create index "+tableName+"_index on "+tableName+" (nbase,a0100,tax_z0,tax_z1)"); 
		/*	}
			else
			{
				dao.delete("delete from "+tableName,new ArrayList());
			}
			
			*/
			
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}	
	}
	
	
	public boolean isColumn(String tableName,String columnname)
	{
		boolean flag=false;
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			RowSet rowSet=dao.search("select * from "+tableName+" where 1=2");
			ResultSetMetaData data=rowSet.getMetaData();
			for(int i=0;i<data.getColumnCount();i++)
			{
				if(data.getColumnName(i+1).equalsIgnoreCase(columnname))
				{
					flag=true;
					break;
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return flag;
	}
	
	
	/**
	 * 设置临时表的主键字段，自动增长类型
	 */
	private void setTmp2PrimaryKey()throws GeneralException
	{
		StringBuffer buf=new StringBuffer();
		DbWizard dbw=new DbWizard(this.conn);			
		try
		{
			if(!isColumn(this.tmp2_name,"tax_max_id"))
			{
				switch(Sql_switcher.searchDbServer())
				{
				case 1://MSSQL
					buf.append("alter table ");
					buf.append(this.tmp2_name);
					buf.append(" add tax_max_id int identity(1,1)");
					dbw.execute(buf.toString());
					break;
				case 2://ORACLE
				case 3://DB2
					Table table=new Table(this.tmp2_name);
					Field field=new Field("tax_max_id","tax_max_id");
					field.setDatatype(DataType.INT);
					field.setLength(10);
					table.addField(field);
					dbw.addColumns(table);

					if(isSequence(Sql_switcher.searchDbServer(),userview.getUserName()+"s_tax_max_id"))
					{
						 dbw.execute("drop sequence "+userview.getUserName()+"s_tax_max_id");	
					}
					buf.append("create sequence "+userview.getUserName()+"s_tax_max_id increment by 1 start with 1");
					dbw.execute(buf.toString());
					
					buf.setLength(0);
					buf.append("update ");
					buf.append(this.tmp2_name);
					buf.append(" set tax_max_id=");
					buf.append(Sql_switcher.sql_NextVal(userview.getUserName()+"s_tax_max_id"));
					dbw.execute(buf.toString());
					buf.setLength(0);				
					buf.append("drop sequence "+userview.getUserName()+"s_tax_max_id");
					dbw.execute(buf.toString());
					break;
				}//switch end.
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}		
	}
	/**
	 * 取得动态字段更新语句
	 * @param extflds
	 * @param extvalues
	 */
	private void getExtFlds(StringBuffer extflds,StringBuffer extvalues)
	{
		try
		{
			
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from gz_tax_mx where 1=2");
			ResultSetMetaData mt=rowSet.getMetaData();
			int columns=mt.getColumnCount();
			HashMap map=new HashMap();
			for(int i=0;i<columns;i++)
			{
				map.put(mt.getColumnName(i+1).toLowerCase(),"1");
			}
			
		//	RecordVo vo=new RecordVo("gz_tax_mx");
			RecordVo vo2=new RecordVo(this.gz_tablename.toLowerCase());
			for(int i=0;i<taxfldlist.size();i++)
			{
				Field field=(Field)taxfldlist.get(i);
				String fieldname=field.getName();
			//	if(vo.hasAttribute(fieldname.toLowerCase())&&vo2.hasAttribute(fieldname.toLowerCase()))
				if(map.get(fieldname.toLowerCase())!=null&&vo2.hasAttribute(fieldname.toLowerCase()))
				{
					extflds.append(",");
					extflds.append(fieldname);
					extvalues.append(",");				
					extvalues.append("S.");
					extvalues.append(fieldname);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	/*	if(extflds.length()>0)
		{
			extflds.setLength(extflds.length()-1);
			extvalues.setLength(extvalues.length()-1);
		}*/
	}
	/**
	 * 求个税明细表最大号
	 * @return
	 */
	private int getMaxTax_maxid()
	{
		int nmax=0;
		try
		{
			
				nmax=DbNameBo.getPrimaryMaxKey("gz_tax_mx","tax_max_id",this.conn);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return nmax;
	}
	/***
	 * 追加记录至个税明细表
	 * @throws GeneralException
	 */
	private void appendRecordTaxMx()throws GeneralException
	{
		try
		{
			DbWizard dbw=new DbWizard(this.conn);	
			String fieldname=(String)this.dbean.get("itemname");
			/**设置临时表主键*/
			setTmp2PrimaryKey();
			/**取得个税明细表额外定义的字段*/
			StringBuffer extFlds=new StringBuffer();
			StringBuffer extValues=new StringBuffer();
			getExtFlds(extFlds,extValues);
			/**项目描述*/
			if(!(desc_item==null||desc_item.length()==0))
			{
				extFlds.append(",description");
				extValues.append(",S.");
				extValues.append(desc_item);
			}
			
			//按业务划分操作单位 所得税管理根据指定的dept_id字段进行权限控制
			if(this.isDeptControl)
			{
				if(ctrlparam.getValue(SalaryCtrlParamBo.LS_DEPT)!=null&&ctrlparam.getValue(SalaryCtrlParamBo.LS_DEPT).trim().length()>0)
				{
					extFlds.append(",deptid");
					extValues.append(",S.");
					extValues.append(ctrlparam.getValue(SalaryCtrlParamBo.LS_DEPT).trim());
				}
			}
			
			
			/**求个税明细表中tax_max_id字段最大值*/
			
			String aflag=this.ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
			//共享薪资类别，其他操作人员引入数据 (dengcan 2008/6/26 )
			String atableName="t#"+this.userview.getUserName()+"_gzsp"; //this.userview.getUserName()+"_sp_data";
			if(!this.gz_tablename.equalsIgnoreCase(atableName)&&this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager)&&aflag!=null&& "1".equals(aflag))
			{
				String dbpres=this.templatevo.getString("cbase");
				//应用库前缀
				String[] dbarr=StringUtils.split(dbpres, ",");
				for(int i=0;i<dbarr.length;i++)
				{
					String pre=dbarr[i];
					StringBuffer tempSql=new StringBuffer(" and upper(temp2.nbase)='"+pre.toUpperCase()+"'" );
					//权限过滤
					if("1".equals(this.controlByUnitcod))
					{
						String whl_str=getWhlByUnits();
						if(whl_str.length()>0)
						{
							tempSql.append(" and  temp2.a0100 in ( select a0100 from "+this.gz_tablename+" where 1=1 "+whl_str+" and  upper(nbase)='"+pre.toUpperCase()+"' ) ");
						}
					}
					else
					{
						String whereIN=InfoUtils.getWhereINSql(this.userview,pre);
						whereIN="select a0100 "+whereIN;	
						tempSql.append(" and temp2.a0100 in ( "+whereIN+" )");
					}
			  //	int nmax=getMaxTax_maxid();
					StringBuffer buf=new StringBuffer();
					buf.append("insert into gz_tax_mx(tax_max_id,NBASE,A0100,A00Z2,A00Z3,A00Z0,A00Z1,A0000,B0110,E0122,A0101,userflag,");
					buf.append("tax_date,taxitem,sskcs,ynse,basedata,sl,sds,");
					buf.append("declare_tax,salaryid,taxmode,flag,ynse_field");
					buf.append(extFlds.toString());
					buf.append(")");
					buf.append(" select tax_max_id+X~X");
				//	buf.append(nmax);
					buf.append(",S.NBASE,S.A0100,S.A00Z2,S.A00Z3,S.A00Z0,S.A00Z1,S.A0000,");
					buf.append("S.B0110,S.E0122,S.A0101,'"+this.manager+"',");
					buf.append("temp2.tax_date,temp2.taxitem,");
			/*		if(calculation.equalsIgnoreCase("Y"))
						buf.append("gz_taxrate_item.sskcs*12,temp2.ynse,");
					else*/
						buf.append("gz_taxrate_item.sskcs,temp2.ynse,");
					buf.append(this.k_base);
					buf.append(",gz_taxrate_item.sl,");
					buf.append(Sql_switcher.round("temp2.sds", 2));
					buf.append(",S.");
					buf.append(this.declare_date_item);
					buf.append(",");
					buf.append(salaryid);
					buf.append(",'");
					buf.append(tax_mode);
					buf.append("',0,'"+fieldname+"'"); //    this.ynse_item+"'");
					buf.append(extValues.toString());
					buf.append(" from (");
					buf.append(this.tmp2_name);
					buf.append(" temp2 left join gz_taxrate_item ");
					buf.append(" on temp2.taxitem=gz_taxrate_item.taxitem)");
					buf.append(" left join ");
					buf.append(gz_tablename);
					buf.append(" S ");
					buf.append(" on temp2.NBASE=S.NBASE and temp2.A0100=S.A0100 and ");
					buf.append(" temp2.A00Z0=S.A00Z0 and temp2.A00Z1=S.A00Z1 ");
					buf.append(tempSql.toString());
					DbNameBo.appendRecordTaxMx(buf.toString(),"gz_tax_mx","tax_max_id",this.conn);
				//	dbw.execute(buf.toString());
					
				}
			}
			else
			{
			
			//	int nmax=getMaxTax_maxid();
				StringBuffer buf=new StringBuffer();
				buf.append("insert into gz_tax_mx(tax_max_id,NBASE,A0100,A00Z2,A00Z3,A00Z0,A00Z1,A0000,B0110,E0122,A0101,userflag,");
				buf.append("tax_date,taxitem,sskcs,ynse,basedata,sl,sds,");
				buf.append("declare_tax,salaryid,taxmode,flag,ynse_field");
				buf.append(extFlds.toString());
				buf.append(")");
				buf.append(" select tax_max_id+X~X");
			//	buf.append(nmax);
				buf.append(",S.NBASE,S.A0100,S.A00Z2,S.A00Z3,S.A00Z0,S.A00Z1,S.A0000,");
				buf.append("S.B0110,S.E0122,S.A0101,S.userflag,"); //          '"+this.userview.getUserName()+"',");
				buf.append("temp2.tax_date,temp2.taxitem,");
				
		/*		if(calculation.equalsIgnoreCase("Y"))
					buf.append("gz_taxrate_item.sskcs*12,temp2.ynse,");
				else*/
					buf.append("gz_taxrate_item.sskcs,temp2.ynse,");
				buf.append(this.k_base);
				buf.append(",gz_taxrate_item.sl,");
				buf.append(Sql_switcher.round("temp2.sds", 2));
				buf.append(",S.");
				buf.append(this.declare_date_item);
				buf.append(",");
				buf.append(salaryid);
				buf.append(",'");
				buf.append(tax_mode);
				
				
				if(!this.gz_tablename.equalsIgnoreCase(atableName))
					buf.append("',0");
				else
					buf.append("',1");
			//	buf.append(",'"+this.ynse_item+"'");
				buf.append(",'"+fieldname+"'");
				buf.append(extValues.toString());
				buf.append(" from (");
				buf.append(this.tmp2_name);
				buf.append(" temp2 left join gz_taxrate_item ");
				buf.append(" on temp2.taxitem=gz_taxrate_item.taxitem)");
				buf.append(" left join ");
				buf.append(gz_tablename);
				buf.append(" S ");
				buf.append(" on temp2.NBASE=S.NBASE and temp2.A0100=S.A0100 and ");
				buf.append(" temp2.A00Z0=S.A00Z0 and temp2.A00Z1=S.A00Z1 ");
				DbNameBo.appendRecordTaxMx(buf.toString(),"gz_tax_mx","tax_max_id",this.conn);
				//dbw.execute(buf.toString());
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}
	}
	/**
	 * 将计算结果更新到结果表
	 * @param itemname 更新薪资表中的指标
	 * @throws GeneralException
	 */
	private void updateSalaryTable(String itemname)throws GeneralException
	{
		try
		{
			DbWizard dbw=new DbWizard(this.conn);			
			StringBuffer strjoin=new StringBuffer();
			strjoin.append(tmp2_name);
			strjoin.append(".NBASE=");
			strjoin.append(gz_tablename);
			strjoin.append(".NBASE and ");
			strjoin.append(tmp2_name);
			strjoin.append(".A0100=");
			strjoin.append(gz_tablename);
			strjoin.append(".A0100 and ");
						
			strjoin.append(tmp2_name);
			strjoin.append(".A00Z0=");
			strjoin.append(gz_tablename);
			strjoin.append(".A00Z0 and ");

			strjoin.append(tmp2_name);
			strjoin.append(".A00Z1=");
			strjoin.append(gz_tablename);
			strjoin.append(".A00Z1 ");
			
			StringBuffer strset=new StringBuffer();
			strset.append(gz_tablename);
			strset.append(".");
			strset.append(itemname);
			strset.append("=");
			FieldItem item=DataDictionary.getFieldItem(itemname);
			int len = 4;
			if(item!=null){
				len = item.getDecimalwidth()>4?4:item.getDecimalwidth();//因为gz_tax_mx中sds长度固定是4
			}
		//	strset.append(Sql_switcher.round(tmp2_name+".sds", len));//如果业务字段存在则取该指标实际小数位，否则默认4位 zhaoxg add 2016-12-19
			strset.append("case when "+Sql_switcher.round(tmp2_name+".sds", len)+"<0 then 0 else "+Sql_switcher.round(tmp2_name+".sds", len)+" end ");
			
			StringBuffer strDest=new StringBuffer();
			strDest.append(" exists( select * from ");
			strDest.append(tmp2_name);
			strDest.append(" A");
			strDest.append(" where ");
			strDest.append(gz_tablename);
			strDest.append(".NBASE=A.NBASE and ");
			strDest.append(gz_tablename);
			strDest.append(".A0100=A.A0100 and ");
			strDest.append(gz_tablename);
			strDest.append(".A00Z0=A.A00Z0 and ");
			strDest.append(gz_tablename);
			strDest.append(".A00Z1=A.A00Z1 )");
		//	strDest.append(" and ( "+gz_tablename+"."+pay_flag_item+"='0' or "+gz_tablename+"."+pay_flag_item+" is null )");
			
			String aflag=this.ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
			//共享薪资类别，其他操作人员引入数据 (dengcan 2008/6/26 )
			String atableName="t#"+this.userview.getUserName()+"_gzsp"; //this.userview.getUserName()+"_sp_data";
			if(!this.gz_tablename.equalsIgnoreCase(atableName)&&this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager)&&aflag!=null&& "1".equals(aflag))
			{
				String dbpres=this.templatevo.getString("cbase");
				//应用库前缀
				String[] dbarr=StringUtils.split(dbpres, ",");
				for(int i=0;i<dbarr.length;i++)
				{
					String pre=dbarr[i];
					StringBuffer tempSql=new StringBuffer(" and upper("+gz_tablename+".nbase)='"+pre.toUpperCase()+"'" );
					//权限过滤
					if("1".equals(this.controlByUnitcod))
					{
						String whl_str=getWhlByUnits();
						if(whl_str.length()>0)
						{
							tempSql.append(whl_str);
						}
					}
					else
					{
						String whereIN=InfoUtils.getWhereINSql(this.userview,pre);
						whereIN="select a0100 "+whereIN;	
						tempSql.append(" and "+gz_tablename+".a0100 in ( "+whereIN+" )");
					}
					dbw.updateRecord(gz_tablename, tmp2_name, strjoin.toString(), strset.toString(), strDest.toString()+tempSql.toString(), strDest.toString()+tempSql.toString());
				}
			}
			else			
				dbw.updateRecord(gz_tablename, tmp2_name, strjoin.toString(), strset.toString(), strDest.toString(), strDest.toString());
			
			ContentDAO dao = new ContentDAO(this.conn);
		//	System.out.println("update "+gz_tablename+" set "+itemname+"=0 where   "+gz_tablename+"."+pay_flag_item+" is not null and  "+"+gz_tablename+"+"."+pay_flag_item+"<>'0'  and  "+gz_tablename+"."+pay_flag_item+"<>''");
		//	dao.update("update "+gz_tablename+" set "+itemname+"=0 where   "+gz_tablename+"."+pay_flag_item+" is not null and  "+gz_tablename+"."+pay_flag_item+"<>'0'  and  "+gz_tablename+"."+pay_flag_item+"<>''");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}
	}
	/**
	 * 执行税率表 (非全年一次性奖金)
	 * 应交个人所得税=(∑应纳税所得额-基数-扣除基数)*税率-速算扣除数-∑已交个人所得税
	 * 其中：
  	 *		扣除基数 < 1时，扣除基数为比例：
     *			扣除基数 = ∑应纳税所得额*扣除基数
  	 *		扣除基数 >= 1时，扣除基数为数值：
	 *			扣除基数 = 扣除基数
	 * @throws GeneralException
	 */
	private void runTaxTable()throws GeneralException
	{
		try
		{	
			DbWizard dbw=new DbWizard(this.conn);	
			StringBuffer taxable=new StringBuffer();
			taxable.append(Sql_switcher.sqlNull(tmp2_name+".taxableSum",0));
			taxable.append("+");
			taxable.append(Sql_switcher.sqlNull(tmp2_name+".ynse",0));
			taxable.append("-");
			if("Y".equalsIgnoreCase(this.calculation))
				taxable.append(this.k_base+"*kcbase_num"); //+Sql_switcher.month(tmp2_name+".tax_date"));
			else
			{
				if("4".equalsIgnoreCase(tax_mode))
				{
					taxable.append(" case when "+Sql_switcher.sqlNull(tmp2_name+".ynse",0)+"<=4000 then 800 ");
					taxable.append(" else  "+Sql_switcher.sqlNull(tmp2_name+".ynse",0)+"*0.2 end ");
				}
				else
					taxable.append(this.k_base);
			}
			/**税率表明细*/
			String taxitem="gz_taxrate_item";
			
			StringBuffer strjoin=new StringBuffer();
			
			if("3".equals(this.tax_mode))  //企业年金
			{
				
				String str=taxable.toString();
				taxable.setLength(0);
				taxable.append(" case when "+Sql_switcher.sqlNull("gzxj_current",0)+">="+this.k_base+" then "+str);
				taxable.append(" when "+Sql_switcher.sqlNull("gzxj_current",0)+"<"+this.k_base+" then  ("+str+")-("+this.k_base+"-"+Sql_switcher.sqlNull("gzxj_current",0)+") end ");
			}
			 
			 
				strjoin.append("(((");
				strjoin.append(taxitem);
		/*		if(calculation.equalsIgnoreCase("Y"))
					strjoin.append(".ynse_down*12<");
				else */
					strjoin.append(".ynse_down<");
				strjoin.append(taxable.toString());
				strjoin.append(") and (");
				strjoin.append(taxable.toString());
				strjoin.append("<=");
				strjoin.append(taxitem);
		/*		if(calculation.equalsIgnoreCase("Y"))
					strjoin.append(".ynse_up*12) and ");
				else */
					strjoin.append(".ynse_up) and ");
				strjoin.append(taxitem);
				strjoin.append(".flag=0) or ");
				strjoin.append("((");			
				strjoin.append(taxitem);
			/*	if(calculation.equalsIgnoreCase("Y"))
					strjoin.append(".ynse_down*12<=");
				else */
					strjoin.append(".ynse_down<=");
				strjoin.append(taxable.toString());
				strjoin.append(") and (");
				strjoin.append(taxable.toString());
				strjoin.append("<");
				strjoin.append(taxitem);
		/*		if(calculation.equalsIgnoreCase("Y"))
					strjoin.append(".ynse_up*12) and ");
				else */
					strjoin.append(".ynse_up) and ");
				strjoin.append(taxitem);
				strjoin.append(".flag=1)) ");
		 
			
			
			
			StringBuffer strset=new StringBuffer();
			strset.append(tmp2_name);
			strset.append(".taxitem=");
			strset.append(taxitem);
			strset.append(".taxitem");
			
			String strWhere="taxid="+this.taxid;
			
			dbw.updateRecord(tmp2_name,taxitem, strjoin.toString(),strset.toString(),"",strWhere);
			/**计算个人所得税*/
			String[][] cases=new String[1][2];
			StringBuffer strWhen=new StringBuffer();
			strWhen.append(Sql_switcher.sqlNull("kc_base", 0));
			strWhen.append(">=1");
			
			StringBuffer strThen=new StringBuffer();
			strThen.append("(");
			strThen.append(Sql_switcher.sqlNull(tmp2_name+".taxablesum",0));
			strThen.append("+");
			strThen.append(Sql_switcher.sqlNull(tmp2_name+".ynse",0));
			strThen.append("-");
			
			if("Y".equalsIgnoreCase(this.calculation))
				strThen.append(this.k_base+"*kcbase_num"); //+Sql_switcher.month(tmp2_name+".tax_date"));
			else
				strThen.append(this.k_base); 
			
			strThen.append("-");
			strThen.append(Sql_switcher.sqlNull("kc_base",0));
			
			if("3".equals(this.tax_mode))  //企业年金
			{
				strThen.append("-(case when "+Sql_switcher.sqlNull("gzxj_current",0)+">="+this.k_base+" then 0");
				strThen.append(" when "+Sql_switcher.sqlNull("gzxj_current",0)+"<"+this.k_base+" then  ("+this.k_base+"-"+Sql_switcher.sqlNull("gzxj_current",0)+") end) ");
			}
			
			
	/*		if(calculation.equalsIgnoreCase("Y"))
				strThen.append(")*sl-sskcs*12-");
			else */
				strThen.append(")*sl-sskcs-");
			strThen.append(Sql_switcher.sqlNull("sdssum",0));
			
			cases[0][0]=strWhen.toString();
			cases[0][1]=strThen.toString();
			
			StringBuffer strElse=new StringBuffer();
			String strElse_t="";
			strElse.append("(");
			strElse.append(Sql_switcher.sqlNull(tmp2_name+".taxablesum",0));
			strElse.append("+");
			strElse.append(Sql_switcher.sqlNull(tmp2_name+".ynse",0));
			strElse.append("-"); 
			if("Y".equalsIgnoreCase(this.calculation))
				strElse.append(this.k_base+"*kcbase_num"); //+Sql_switcher.month(tmp2_name+".tax_date"));
			else
				strElse.append(this.k_base); 
			
			if("3".equals(this.tax_mode))  //企业年金
			{
				strElse.append("-(case when "+Sql_switcher.sqlNull("gzxj_current",0)+">="+this.k_base+" then 0");
				strElse.append(" when "+Sql_switcher.sqlNull("gzxj_current",0)+"<"+this.k_base+" then  ("+this.k_base+"-"+Sql_switcher.sqlNull("gzxj_current",0)+") end) ");
			}
			
			
			
			
			strElse.append("-(");
			strElse.append(Sql_switcher.sqlNull(tmp2_name+".taxablesum",0));
			strElse.append("+");
			strElse.append(Sql_switcher.sqlNull(tmp2_name+".ynse",0));
			strElse.append(")*");
			strElse.append(Sql_switcher.sqlNull("kc_base",0));
		/*	if(calculation.equalsIgnoreCase("Y"))
				strElse.append(")*sl-sskcs*12-");
			else */
				strElse.append(")*sl-sskcs");
			strElse_t=strElse.toString();
			strElse.append("-"+Sql_switcher.sqlNull("sdssum",0));
			
			strjoin.setLength(0);
			strjoin.append(tmp2_name);
			strjoin.append(".taxitem=gz_taxrate_item.taxitem");
			
			
			
			
			//先算残疾人当月减免  3月份残疾人减免 = 3月份累计个税/2  -  1月份残疾人减免 - 2月份残疾人减免   说明：3月份累计个税四舍五入。3月份累计个税/2  截取小数点后两位，不做四舍五入
			if("Y".equalsIgnoreCase(calculation)&&this.disability!=null&&this.disability.trim().length()>0)//累计预扣法
			{
			 
				StringBuffer strThen_t=new StringBuffer();
				strThen_t.append("(");
				strThen_t.append(Sql_switcher.sqlNull(tmp2_name+".taxablesum",0));
				strThen_t.append("+");
				strThen_t.append(Sql_switcher.sqlNull(tmp2_name+".ynse",0));
				strThen_t.append("-"); 
				if("Y".equalsIgnoreCase(this.calculation))
					strThen_t.append(this.k_base+"*kcbase_num"); //+Sql_switcher.month(tmp2_name+".tax_date"));
				else
					strThen_t.append(this.k_base);  
				strThen_t.append("-");
				strThen_t.append(Sql_switcher.sqlNull("kc_base",0));
				
				if("3".equals(this.tax_mode))  //企业年金
				{
					strThen_t.append("-(case when "+Sql_switcher.sqlNull("gzxj_current",0)+">="+this.k_base+" then 0");
					strThen_t.append(" when "+Sql_switcher.sqlNull("gzxj_current",0)+"<"+this.k_base+" then  ("+this.k_base+"-"+Sql_switcher.sqlNull("gzxj_current",0)+") end) ");
				} 
				strThen_t.append(")*sl-sskcs"); 
				
				String[][] cases_t=new String[1][2];
				cases_t[0][0]=strWhen.toString();
				cases_t[0][1]=strThen_t.toString();
				
				 
				strset.setLength(0);
				strset.append("allowance=(");
				strset.append(Sql_switcher.sql_Case("", strElse_t.toString(), cases_t)+")");
				dbw.updateRecord(tmp2_name,"gz_taxrate_item", strjoin.toString(), strset.toString(),"",""); 
				
				//2019/11/01
				if(!StringUtils.isEmpty(this.disability_date))
				{ 
					StringBuffer buf=new StringBuffer("update "+this.tmp2_name+" set  disability_SDSSum=(select  ");
					String select_str="lower(nbase) nbase,a0100,tax_date,taxmode,sds";
					buf.append(" sum (sds) from  ( select "+select_str+" from gz_tax_mx  union all select "+select_str+" from taxarchive ) gz_tax_mx  ") ;
					buf.append("  where  upper(gz_tax_mx.nbase)=upper("+this.tmp2_name+".nbase) and gz_tax_mx.a0100="+this.tmp2_name+".a0100  and "+this.tmp2_name+".disability_date is not null ");
					buf.append(" and "+Sql_switcher.isnull("disability","'0'")+"='1'    ");
					buf.append(" and "+Sql_switcher.year("gz_tax_mx.tax_date")+"="+Sql_switcher.year(""+this.tmp2_name+".tax_date"));  
					if(this.hiredate!=null&&this.hiredate.trim().length()>0)
					{
						buf.append(" and  ( "+Sql_switcher.year("gz_tax_mx.tax_date")+">"+Sql_switcher.year(""+this.tmp2_name+".hiredate")+" or (  "+Sql_switcher.year("gz_tax_mx.tax_date")+"="+Sql_switcher.year(""+this.tmp2_name+".hiredate")+"  and "+Sql_switcher.month("gz_tax_mx.tax_date")+">="+Sql_switcher.month(""+this.tmp2_name+".hiredate")+"   )    ) ");
					} 
					buf.append(" and  (   "+Sql_switcher.year("gz_tax_mx.tax_date")+"="+Sql_switcher.year(this.tmp2_name+".disability_date")+"  and "+Sql_switcher.month("gz_tax_mx.tax_date")+"<"+Sql_switcher.month(this.tmp2_name+".disability_date")+"     ) ");
					buf.append(" ) "); 
					
					buf.append(" where exists ( select null from ");
					buf.append(" ( select "+select_str+" from gz_tax_mx  union all select "+select_str+" from taxarchive ) gz_tax_mx  ");
					buf.append("  where upper(gz_tax_mx.nbase)=upper("+this.tmp2_name+".nbase) and gz_tax_mx.a0100="+this.tmp2_name+".a0100  and "+this.tmp2_name+".disability_date is not null  ");
					buf.append("  and "+Sql_switcher.isnull("disability","'0'")+"='1'   and "+Sql_switcher.year("gz_tax_mx.tax_date")+"="+Sql_switcher.year(""+this.tmp2_name+".tax_date")); 
					//2019/11/01
					if(this.hiredate!=null&&this.hiredate.trim().length()>0)
					{
						buf.append(" and  ( "+Sql_switcher.year("gz_tax_mx.tax_date")+">"+Sql_switcher.year(""+this.tmp2_name+".hiredate")+" or (  "+Sql_switcher.year("gz_tax_mx.tax_date")+"="+Sql_switcher.year(""+this.tmp2_name+".hiredate")+"  and "+Sql_switcher.month("gz_tax_mx.tax_date")+">="+Sql_switcher.month(""+this.tmp2_name+".hiredate")+"   )    ) ");
					} 
					buf.append(" and  (   "+Sql_switcher.year("gz_tax_mx.tax_date")+"="+Sql_switcher.year(this.tmp2_name+".disability_date")+"  and "+Sql_switcher.month("gz_tax_mx.tax_date")+"<"+Sql_switcher.month(this.tmp2_name+".disability_date")+"     ) ");
					buf.append(" ) ");
					dbw.execute(buf.toString()); 
					dbw.execute("update "+this.tmp2_name+" set  allowance=allowance-"+Sql_switcher.isnull("disability_SDSSum","'0'")+"   where "+Sql_switcher.isnull("disability","'0'")+"='1'");
				} 
				
				dbw.execute("update "+this.tmp2_name+" set  allowance=0   where "+Sql_switcher.isnull("disability","'0'")+"<>'1'");
				if(Sql_switcher.searchDbServer()==1) //MSSQL
				{  
					dbw.execute("update "+this.tmp2_name+" set  allowance=round(round(allowance,2)*(1-"+minus_percent+"),2,1)-"+Sql_switcher.isnull("allowance_sum","0")+"   where "+Sql_switcher.isnull("disability","'0'")+"='1'");
				}
				else // oracle
				{
					 
					dbw.execute("update "+this.tmp2_name+" set  allowance=trunc(round(allowance,2)*(1-"+minus_percent+"),2)-"+Sql_switcher.isnull("allowance_sum","0")+"  where "+Sql_switcher.isnull("disability","'0'")+"='1'");
				}
			}	
			//
			
			
			
			strset.setLength(0);
			strset.append("sds=");
			strset.append(Sql_switcher.sql_Case("", strElse.toString(), cases));
			dbw.updateRecord(tmp2_name,"gz_taxrate_item", strjoin.toString(), strset.toString(),"","");
			
			 
			if("Y".equalsIgnoreCase(calculation)&&this.disability!=null&&this.disability.trim().length()>0)//
			{ 
						dbw.execute("update "+this.tmp2_name+" set sds=sds-allowance   where "+Sql_switcher.isnull("disability","'0'")+"='1'");  //残疾人个税 和 累计应纳税额减半
					 
			} 
			 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}		
	}
	
	
	/**
	 * 取得工资薪金的基数
	 * @return
	 */
	public float getKBase()
	{
		float k_base=0;
		try
		{
			
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search("select k_base,param from gz_tax_rate  order by taxid desc ");// where taxid="+this.taxid+" order by taxid desc"); 全年一次性奖金基数是0，要取工资薪金基数
			while(rset.next())
			{
				float kbase=rset.getFloat("k_base");
				String tmp=Sql_switcher.readMemo(rset, "param");
				TaxTableXMLBo xmlbo=new TaxTableXMLBo(tmp);
				String taxmode=xmlbo.getValue("TaxModeCode");
				if("1".equals(taxmode)|| "5".equals(taxmode))
				{
					k_base=kbase;
					break;
				}
			}//if end.
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return k_base;
	}
	
	/**
	 * 取得税率表的基数
	 * @return
	 */
	public float getKBase2()
	{
		float k_base=0;
		try
		{
			
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search("select k_base,param from gz_tax_rate  where taxid="+this.taxid+" order by taxid desc");
			if(rset.next())
			{
				float kbase=rset.getFloat("k_base");
				String tmp=Sql_switcher.readMemo(rset, "param");
				TaxTableXMLBo xmlbo=new TaxTableXMLBo(tmp);
				String taxmode=xmlbo.getValue("TaxModeCode"); 
			    k_base=kbase;  
			}//if end.
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return k_base;
	}
	
	
	/**
	 * XXXtax_temp2 写入当月工资所得额
	 *
	 */
	public void setGz_sdeValue(String goalTable)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer("update "+goalTable+" set gz_sde=( select sum("+Sql_switcher.isnull("ynse","0")+") ynse  from gz_tax_mx  where  taxmode='1' ");
	/*		
			sql.append(" and ( salaryid<>"+this.salaryid+" or (salaryid="+this.salaryid);//  and flag=1 ");   //20141222 dengcan  依据计税时间来算
			if(goalTable.equalsIgnoreCase("t#"+this.userview.getUserName()))
				sql.append(" and gz_tax_mx.a00z0="+goalTable+".tax_z0  and gz_tax_mx.a00z1<>"+goalTable+".tax_z1  ");
			else
				sql.append(" and gz_tax_mx.a00z0="+goalTable+".a00z0  and gz_tax_mx.a00z1<>"+goalTable+".a00z1  ");
			sql.append("   ) ) "); // and flag=1 ");
	*/		
			
			
			
			 /*
			if(goalTable.equalsIgnoreCase("t#"+this.userview.getUserName()))
			{
				sql.append(" and gz_tax_mx.a00z0="+goalTable+".tax_z0 ");
			}
			else
			{ 
			 	sql.append(" and gz_tax_mx.a00z0="+goalTable+".a00z0 ");
			}
			 */
			sql.append(" and "+Sql_switcher.year("gz_tax_mx.tax_date")+"="+Sql_switcher.year(goalTable+".tax_date"));  //20141222 dengcan  依据计税时间来算
			sql.append(" and "+Sql_switcher.month("gz_tax_mx.tax_date")+"="+Sql_switcher.month(goalTable+".tax_date"));
			
			sql.append(" and gz_tax_mx.nbase="+goalTable+".nbase  and gz_tax_mx.a0100="+goalTable+".a0100 ),is_sde=1");
			sql.append(" where exists (select null  from gz_tax_mx  where  taxmode='1' ");
			/*
			sql.append(" and ( salaryid<>"+this.salaryid+" or (salaryid="+this.salaryid);
			if(goalTable.equalsIgnoreCase("t#"+this.userview.getUserName()))
				sql.append(" and gz_tax_mx.a00z0="+goalTable+".tax_z0  and gz_tax_mx.a00z1<>"+goalTable+".tax_z1  ");
			else
				sql.append(" and gz_tax_mx.a00z0="+goalTable+".a00z0  and gz_tax_mx.a00z1<>"+goalTable+".a00z1  ");
			sql.append("   ) ) "); // and flag=1 ");
			*/
			/*
			if(goalTable.equalsIgnoreCase("t#"+this.userview.getUserName()))
				sql.append(" and gz_tax_mx.a00z0="+goalTable+".tax_z0 ");
			else
			{
				 
			    sql.append(" and gz_tax_mx.a00z0="+goalTable+".a00z0  ");
			}*/
			sql.append(" and "+Sql_switcher.year("gz_tax_mx.tax_date")+"="+Sql_switcher.year(goalTable+".tax_date"));   //20141222 dengcan  依据计税时间来算
			sql.append(" and "+Sql_switcher.month("gz_tax_mx.tax_date")+"="+Sql_switcher.month(goalTable+".tax_date"));
			
			sql.append(" and gz_tax_mx.nbase="+goalTable+".nbase  and gz_tax_mx.a0100="+goalTable+".a0100  )");
		//	System.out.println(sql.toString());
			dao.update(sql.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 执行税率表 (全年一次性奖金)
	 * 应交个人所得税=(∑应纳税所得额-扣除基数)*税率-速算扣除数-∑已交个人所得税
	 * 其中：
  	 *		扣除基数 < 1时，扣除基数为比例：
     *			扣除基数 = ∑应纳税所得额*扣除基数
  	 *		扣除基数 >= 1时，扣除基数为数值：
	 *			扣除基数 = 扣除基数
	 * 税率由(∑应纳税额)/12,查税率表
	 * @throws GeneralException
	 */
	private void runTaxTableOneBonus()throws GeneralException
	{
		try
		{	
			DbWizard dbw=new DbWizard(this.conn);	
			
			
			//写入当月工资所得额
			setGz_sdeValue(tmp2_name);
			float k_base=getKBase(); 
			for(int z=0;z<2;z++)
			{ 
				StringBuffer taxable=new StringBuffer();
				StringBuffer where_str=new StringBuffer("");
				//2014-7-1  dengcan 解决全年一次性奖金当月不发薪，不扣减3500问题
				if(z==0) //工资薪金所得>=基数
				{
				//	where_str.append(" ( ("+Sql_switcher.isnull(tmp2_name+".gz_sde","0")+">="+k_base+" ) or is_sde=0 )");
					where_str.append(" (  "+Sql_switcher.isnull(tmp2_name+".gz_sde","0")+">="+k_base+" and is_sde=1 )");
				}
				if(z==1) //工资薪金所得<基数
				{
				//	where_str.append(" ( ("+Sql_switcher.isnull(tmp2_name+".gz_sde","0")+"<"+k_base+" ) and is_sde=1 )");
					where_str.append(" ( ( ("+Sql_switcher.isnull(tmp2_name+".gz_sde","0")+"<"+k_base+" ) and is_sde=1 ) or is_sde=0 )");
					
				}
				taxable.append("(");
				taxable.append(Sql_switcher.sqlNull(tmp2_name+".taxableSum",0));
				taxable.append("+");
				taxable.append(Sql_switcher.sqlNull(tmp2_name+".ynse",0));
			//	if(z==1) //工资薪金所得<基数
			//		taxable.append("-("+k_base+"-"+Sql_switcher.isnull(tmp2_name+".gz_sde","0")+")");
				taxable.append(")/12");
				
				/**税率表明细*/
				String taxitem="gz_taxrate_item";
				
				StringBuffer strjoin=new StringBuffer();
				strjoin.append("(((");
				strjoin.append(taxitem);
				strjoin.append(".ynse_down<");
				strjoin.append(taxable.toString());
				strjoin.append(") and (");
				strjoin.append(taxable.toString());
				strjoin.append("<=");
				strjoin.append(taxitem);
				strjoin.append(".ynse_up) and ");
				strjoin.append(taxitem);
				strjoin.append(".flag=0) or ");
				strjoin.append("((");			
				strjoin.append(taxitem);
				strjoin.append(".ynse_down<=");
				strjoin.append(taxable.toString());
				strjoin.append(") and (");
				strjoin.append(taxable.toString());
				strjoin.append("<");
				strjoin.append(taxitem);
				strjoin.append(".ynse_up) and ");
				strjoin.append(taxitem);
				strjoin.append(".flag=1)) ");
				
				StringBuffer strset=new StringBuffer();
				strset.append(tmp2_name);
				strset.append(".taxitem=");
				strset.append(taxitem);
				strset.append(".taxitem");
				
				String strWhere="taxid="+this.taxid;
				strWhere+=" and "+where_str.toString();
				
				dbw.updateRecord(tmp2_name,taxitem, strjoin.toString(),strset.toString(),where_str.toString(),strWhere);
				/**计算个人所得税*/
				String[][] cases=new String[1][2];
				StringBuffer strWhen=new StringBuffer();
				strWhen.append(Sql_switcher.sqlNull("kc_base", 0));
				strWhen.append(">=1");
				
				StringBuffer strThen=new StringBuffer();
				strThen.append("(");
				strThen.append(Sql_switcher.sqlNull(tmp2_name+".taxablesum",0));
				strThen.append("+");
				strThen.append(Sql_switcher.sqlNull(tmp2_name+".ynse",0));
	
				strThen.append("-");
				strThen.append(Sql_switcher.sqlNull("kc_base",0));
			//	if(z==1) //工资薪金所得<基数
			//		strThen.append("-("+k_base+"-"+Sql_switcher.isnull(tmp2_name+".gz_sde","0")+")");
				strThen.append(")*sl-sskcs-");
				strThen.append(Sql_switcher.sqlNull("sdssum",0));
				
				cases[0][0]=strWhen.toString();
				cases[0][1]=strThen.toString();
				
				StringBuffer strElse=new StringBuffer();
				strElse.append("(");
				strElse.append(Sql_switcher.sqlNull(tmp2_name+".taxablesum",0));
				strElse.append("+");
				strElse.append(Sql_switcher.sqlNull(tmp2_name+".ynse",0));
				strElse.append("-(");
				strElse.append(Sql_switcher.sqlNull(tmp2_name+".taxablesum",0));
				strElse.append("+");
				strElse.append(Sql_switcher.sqlNull(tmp2_name+".ynse",0));
				strElse.append(")*");
				strElse.append(Sql_switcher.sqlNull("kc_base",0));
			//	if(z==1) //工资薪金所得<基数
			//		strElse.append("-("+k_base+"-"+Sql_switcher.isnull(tmp2_name+".gz_sde","0")+")");
				strElse.append(")*sl-sskcs-");
				strElse.append(Sql_switcher.sqlNull("sdssum",0));
				
				strjoin.setLength(0);
				strjoin.append(tmp2_name);
				strjoin.append(".taxitem=gz_taxrate_item.taxitem");
				
				strset.setLength(0);
				strset.append("sds=");
				strset.append(Sql_switcher.sql_Case("", strElse.toString(), cases));
				dbw.updateRecord(tmp2_name,"gz_taxrate_item", strjoin.toString(), strset.toString(),where_str.toString(),where_str.toString());
				
			
			}
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}		
	}
	
	/**
	 * 同一个计税时间、同一个计税方式，才汇总,汇总所得税和应纳税所得额
	 * @throws GeneralException
	 */
	private void sumSdsOrYnse()throws GeneralException
	{
		try
		{
			/**创建临时表*/
			String tmp=this.userview.getUserName()+"tax_mx";
			DbWizard dbw=new DbWizard(this.conn);
			dbw.dropTable(tmp);
			//20190626
			String flds="NBASE,A0100,ynse,sds,a00z1,a00z3,taxunit";//tax_date";  z1:年  z3:月
			if("Y".equalsIgnoreCase(calculation))//累计预扣法
				flds+=",allowance";
			dbw.createTempTable("gz_tax_mx", tmp, flds, "1=2","");
			
			
			if("Y".equalsIgnoreCase(calculation))//累计预扣法
			{
				flds="NBASE,A0100,ynse,sds,a00z1,taxunit";//tax_date";  z1:年  z3:月
				
				StringBuffer buf=new StringBuffer();
				buf.append("insert into ");
				buf.append(tmp);
				buf.append("(");
				buf.append(flds);
				//20190626
				buf.append(",allowance)");
				buf.append(" select nbase,a0100,sum(");
				buf.append(Sql_switcher.sqlNull("ynse",0));
				buf.append("),");
				buf.append("sum(");
				buf.append(Sql_switcher.sqlNull("sds",0)); 
				buf.append(" ),"+Sql_switcher.year("tax_date"));
				if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
					buf.append(","+Sql_switcher.sqlNull("taxunit","-1")+" as taxunit");
				else
					buf.append(",''");
				//20190626
				buf.append(",sum(");
				buf.append(Sql_switcher.sqlNull("allowance",0)); 
				buf.append(" )");
				
				
				String select_str="lower(nbase) nbase,a0100,tax_date,taxmode,sds,ynse";
				if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
					select_str+=",taxunit"; 
				//20190626
				select_str+=",allowance";
				
				buf.append(" from  ( select "+select_str+" from gz_tax_mx  union all select "+select_str+" from taxarchive ) gz_tax_mx  ");
				buf.append("  where exists(select null from ");
				buf.append(tmp1_name);
				buf.append(" A where upper(gz_tax_mx.nbase)=upper(A.nbase) and gz_tax_mx.a0100=A.a0100 ");
				buf.append(" and "+Sql_switcher.year("gz_tax_mx.tax_date")+"="+Sql_switcher.year("A.tax_date"));
				
				//2019/11/01
				if(this.hiredate!=null&&this.hiredate.trim().length()>0)
				{
					buf.append(" and  ( "+Sql_switcher.year("gz_tax_mx.tax_date")+">"+Sql_switcher.year("A.hiredate")+" or (  "+Sql_switcher.year("gz_tax_mx.tax_date")+"="+Sql_switcher.year("A.hiredate")+"  and "+Sql_switcher.month("gz_tax_mx.tax_date")+">="+Sql_switcher.month("A.hiredate")+"   )    ) ");
				
				}
				
				
				
				buf.append(" ) and taxmode='");
				buf.append(tax_mode);
				buf.append("'");
				buf.append(" group by nbase,a0100,"+Sql_switcher.year("tax_date"));   //,tax_date" );
				if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
					buf.append(",taxunit");
				dbw.execute(buf.toString());
				dbw.execute("create index "+tmp+"_index on "+tmp+" (nbase,a0100,a00z1,a00z3)");
				
				/**将所得税、应纳税所得额汇总更新到临时表2*/
				StringBuffer strjoin=new StringBuffer();
				strjoin.append("upper("+tmp2_name);
				strjoin.append(".nbase)=upper(");
				strjoin.append(tmp);
				strjoin.append(".nbase) and ");
				strjoin.append(tmp2_name);
				strjoin.append(".a0100=");
				strjoin.append(tmp);
				strjoin.append(".a0100 and ");			 
				strjoin.append(Sql_switcher.year(tmp2_name+".tax_date")+"=");
				strjoin.append(tmp+".a00z1");
				
				if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
				{
					strjoin.append(" and "+tmp2_name);
					strjoin.append(".taxunit=");
					strjoin.append(tmp+".taxunit ");	
				}
				
				StringBuffer strset=new StringBuffer();
				strset.append(tmp2_name);
				strset.append(".sdssum=");
				strset.append(Sql_switcher.sqlNull(tmp+".sds",0));
				strset.append("`");
				strset.append(tmp2_name);
				strset.append(".taxablesum=");
				strset.append(Sql_switcher.sqlNull(tmp+".ynse",0));
				
				//20190626
				strset.append("`");
				strset.append(tmp2_name);
				strset.append(".allowance_sum=");
				strset.append(Sql_switcher.sqlNull(tmp+".allowance",0));
				
				
				
				
				
				
				dbw.updateRecord(tmp2_name, tmp,strjoin.toString(),strset.toString(),"", "");
				
			}
			else
			{
				
				
				StringBuffer buf=new StringBuffer();
				buf.append("insert into ");
				buf.append(tmp);
				buf.append("(");
				buf.append(flds);
				buf.append(")");
				buf.append(" select nbase,a0100,sum(");
				buf.append(Sql_switcher.sqlNull("ynse",0));
				buf.append("),");
				buf.append("sum(");
				buf.append(Sql_switcher.sqlNull("sds",0)); 
				buf.append(" ),"+Sql_switcher.year("tax_date")+","+Sql_switcher.month("tax_date"));
				if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
					buf.append(","+Sql_switcher.sqlNull("taxunit","-1")+" as taxunit");
				else
					buf.append(",''");
				buf.append(" from gz_tax_mx where exists(select null from ");
				buf.append(tmp1_name);
				buf.append(" A where gz_tax_mx.nbase=A.nbase and gz_tax_mx.a0100=A.a0100 ");
				buf.append(" and "+Sql_switcher.year("gz_tax_mx.tax_date")+"="+Sql_switcher.year("A.tax_date"));
				buf.append(" ) and taxmode='");
				buf.append(tax_mode);
				buf.append("'");
				buf.append(" group by nbase,a0100,"+Sql_switcher.year("tax_date")+","+Sql_switcher.month("tax_date"));   //,tax_date" );
				if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
					buf.append(",taxunit");
				dbw.execute(buf.toString());
				dbw.execute("create index "+tmp+"_index on "+tmp+" (nbase,a0100,a00z1,a00z3)");
				
				/**将所得税、应纳税所得额汇总更新到临时表2*/
				StringBuffer strjoin=new StringBuffer();
				strjoin.append(tmp2_name);
				strjoin.append(".nbase=");
				strjoin.append(tmp);
				strjoin.append(".nbase and ");
				strjoin.append(tmp2_name);
				strjoin.append(".a0100=");
				strjoin.append(tmp);
				strjoin.append(".a0100 and ");			 
				//---------2014-06-23 dengcan 合并计税按月合并
				strjoin.append(Sql_switcher.year(tmp2_name+".tax_date")+"=");
				strjoin.append(tmp+".a00z1");
				strjoin.append(" and "+Sql_switcher.month(tmp2_name+".tax_date")+"=");
				strjoin.append(tmp+".a00z3");
				if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
				{
					strjoin.append(" and "+tmp2_name);
					strjoin.append(".taxunit=");
					strjoin.append(tmp+".taxunit ");	
				}
				
				StringBuffer strset=new StringBuffer();
				strset.append(tmp2_name);
				strset.append(".sdssum=");
				strset.append(Sql_switcher.sqlNull(tmp+".sds",0));
				strset.append("`");
				strset.append(tmp2_name);
				strset.append(".taxablesum=");
				strset.append(Sql_switcher.sqlNull(tmp+".ynse",0));
				
				dbw.updateRecord(tmp2_name, tmp,strjoin.toString(),strset.toString(),"", "");
				
				if("3".equalsIgnoreCase(tax_mode)) //企业年金
				{
					 //gzxj_current 写入当月工资薪金
					dbw.dropTable(tmp);
					flds="NBASE,A0100,ynse,sds,a00z1,a00z3,taxunit";//tax_date";  z1:年  z3:月
					dbw.createTempTable("gz_tax_mx", tmp, flds, "1=2","");
					buf.setLength(0);
					
					buf.append("insert into ");
					buf.append(tmp);
					buf.append("(");
					buf.append(flds);
					buf.append(")");
					buf.append(" select nbase,a0100,sum(");
					buf.append(Sql_switcher.sqlNull("ynse",0));
					buf.append("),");
					buf.append("sum(");
					buf.append(Sql_switcher.sqlNull("sds",0));
			//		buf.append("),tax_date ");
					buf.append(" ),"+Sql_switcher.year("tax_date")+","+Sql_switcher.month("tax_date"));
					if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
						buf.append(","+Sql_switcher.sqlNull("taxunit","''")+"");
					else
						buf.append(",''");
					buf.append(" from gz_tax_mx where exists(select * from ");
					buf.append(tmp1_name);
					buf.append(" A where gz_tax_mx.nbase=A.nbase and gz_tax_mx.a0100=A.a0100  ");
					buf.append(" and "+Sql_switcher.year("gz_tax_mx.tax_date")+"="+Sql_switcher.year("A.tax_date"));
					buf.append(" ) and taxmode='3'"); 
					buf.append(" group by nbase,a0100,"+Sql_switcher.year("tax_date")+","+Sql_switcher.month("tax_date"));   //,tax_date" );
					if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
						buf.append(","+Sql_switcher.sqlNull("taxunit","''"));
					dbw.execute(buf.toString());
					dbw.execute("create index "+tmp+"_index on "+tmp+" (nbase,a0100,a00z1,a00z3)");
					
					 
					strjoin.setLength(0);
					strjoin.append(tmp2_name);
					strjoin.append(".nbase=");
					strjoin.append(tmp);
					strjoin.append(".nbase and ");
					strjoin.append(tmp2_name);
					strjoin.append(".a0100=");
					strjoin.append(tmp);
					strjoin.append(".a0100 and ");			 
					//---------2014-06-23 dengcan 合并计税按月合并		
					strjoin.append(Sql_switcher.year(tmp2_name+".tax_date")+"=");
					strjoin.append(tmp+".a00z1");
					strjoin.append(" and "+Sql_switcher.month(tmp2_name+".tax_date")+"=");
					strjoin.append(tmp+".a00z3");
					if(this.tax_unit!=null&&this.tax_unit.trim().length()>0)
					{
						strjoin.append(" and "+tmp2_name);
						strjoin.append(".taxunit=");
						strjoin.append(tmp+".taxunit ");	
					} 
					strset.setLength(0); 
					strset.append(tmp2_name);
					strset.append(".gzxj_current=");
					strset.append(Sql_switcher.sqlNull(tmp+".ynse",0)); 
					dbw.updateRecord(tmp2_name, tmp,strjoin.toString(),strset.toString(),"", "");
				}
			}
			 
		
		
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}
	/**
	 * 求当前税率表的扣减基数
	 * @return
	 */
	private boolean getK_baseOrMode()
	{
		StringBuffer buf=new StringBuffer();
		boolean bflag=false;
		try
		{
			buf.append("select k_base,param from gz_tax_rate where taxid=");
			buf.append(this.taxid);
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			if(rset.next())
			{
				k_base=rset.getFloat("k_base");
				String tmp=Sql_switcher.readMemo(rset, "param");
				TaxTableXMLBo xmlbo=new TaxTableXMLBo(tmp);
				tax_mode=xmlbo.getValue("TaxModeCode");
				 
				bflag=true;
			}//if end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return bflag;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getIsClearItem() {
		return isClearItem;
	}
	public void setIsClearItem(String isClearItem) {
		this.isClearItem = isClearItem;
	}
	public String getErrorInfo() {
		return errorInfo;
	}
	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}
	public String getGz_tablename() {
		return gz_tablename;
	}
	public void setGz_tablename(String gz_tablename) {
		this.gz_tablename = gz_tablename;
	}
}
