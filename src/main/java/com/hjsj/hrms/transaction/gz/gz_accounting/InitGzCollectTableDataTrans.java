package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
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
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 初始化汇总数据
 * 
 * @author xujian
 * @version 1.0 2009-9-24
 */
public class InitGzCollectTableDataTrans extends IBusiness {

	private HashMap orgtree = null;
	private int currentlayer = 1;
	private ArrayList topcodeitemid = new ArrayList();

	
	private HashMap   getSalarySetMap(String salaryid,ContentDAO dao)
	{
		HashMap map=new HashMap();
		try
		{
			RowSet rowSet=dao.search("select itemlength,decwidth,itemdesc,itemid from salaryset where salaryid="+salaryid+" and upper(itemtype)='N'");
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				abean.set("itemlength",rowSet.getString("itemlength")!=null?rowSet.getString("itemlength"):"0");
				abean.set("decwidth",rowSet.getString("decwidth")!=null?rowSet.getString("decwidth"):"0");
				abean.set("itemdesc",rowSet.getString("itemdesc"));
				abean.set("itemid",rowSet.getString("itemid"));
				map.put(rowSet.getString("itemid").toUpperCase(), abean);
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return map;
	}
	
	
	
	public void execute() throws GeneralException {
		ArrayList fieldlist = new ArrayList();
		String[] sum_fields = null;
		String sum_fields_str = "";
		try {
			String salaryid = (String) this.getFormHM().get("salaryid");
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			
			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(),
					Integer.parseInt(salaryid), this.userView);
			SalaryCtrlParamBo ctrlparam = gzbo.getCtrlparam();
			String layer = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,
					"layer");
			layer = layer != null && !"".equals(layer) ? layer : "0";
			String sum_type = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,
					"sum_type");
			
			String collect_field = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,
					"collect_field");
			FieldItem collectfield=DataDictionary.getFieldItem(collect_field);
			
			sum_type = sum_type != null ? sum_type : "0";
//			if(!collectfieldcode.equalsIgnoreCase("um")&&!collectfieldcode.equalsIgnoreCase("un"))
//				sum_type="2";


			sum_type="1";
			String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,
					"orgid");
			orgid = orgid != null ? orgid : "";
			String deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,
					"deptid");
			deptid = deptid != null ? deptid : "";
			// 同步gz_sp_report表与设置汇总指标的字段
			sum_fields_str = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD);
			sum_fields_str = sum_fields_str != null ? sum_fields_str : "";
			if (sum_fields_str.trim().length() < 1) {
				throw GeneralExceptionHandler.Handle(new Exception(
						"请您先设置此工资套下需要汇总的指标！"));
			}
			sum_fields = sum_fields_str.split(",");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			HashMap setMap=getSalarySetMap(salaryid,dao);
			
			RecordVo vo_gz_sp_report = new RecordVo("gz_sp_report");
			DbWizard dbw = new DbWizard(this.getFrameconn());
			DBMetaModel dbmodel = new DBMetaModel(this.getFrameconn());
			dbmodel.reloadTableModel("gz_sp_report");
			// 添加jsp页面的<hrms:dataset 标签将要显示的字段
			FieldItem b0110=DataDictionary.getFieldItem("b0110");
			Field field=null;
			if("UNUM".equalsIgnoreCase(collect_field)||collectfield==null){
				field = new Field("b0110", "单位&部门");
				field.setCodesetid("UM");
				//field.setCodesetid("0");
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				sum_type="1";
			}
			else if("um".equalsIgnoreCase(collectfield.getCodesetid())){
				field = new Field("b0110",collectfield.getItemdesc());
				field.setCodesetid("UM");
				//field.setCodesetid("0");
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				sum_type="1";
			}else
			{
				field = new Field("b0110", b0110.getItemdesc());
				field.setCodesetid("UN");
				//field.setCodesetid("0");
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				sum_type="0";
			}
			fieldlist.add(field);
			this.getFormHM().put("layer", layer);
			this.getFormHM().put("sum_type", sum_type);
			FieldItem item=null;
			for (int i = 0; i < sum_fields.length; i++) {
				String sum_field = sum_fields[i];
				if(setMap.get(sum_field.toUpperCase())==null)
					continue;
				LazyDynaBean _bean=(LazyDynaBean)setMap.get(sum_field.toUpperCase());
				int itemlength=Integer.parseInt((String)_bean.get("itemlength"));
				int decwidth=Integer.parseInt((String)_bean.get("decwidth"));
				String itemdesc=(String)_bean.get("itemdesc");
				field = new Field(sum_field.toLowerCase(), itemdesc);
				item=DataDictionary.getFieldItem(sum_field.toLowerCase());
				if(item==null|| "0".equals(item.getUseflag()))
				{
					throw GeneralExceptionHandler.Handle(new Exception("汇总指标在薪资类别中已经不存在，请到薪资类别—属性界面，重新指定汇总指标！"));
				//	continue;
				}
				String format = "############################";
				if (decwidth > 0) {
					field.setDatatype(DataType.FLOAT);
					field.setLength(itemlength);
					field.setDecimalDigits(decwidth);
					field.setFormat(format.substring(0, itemlength)
							+ "." + format.substring(0, decwidth));
				} else {
					field.setDatatype(DataType.INT);
					field.setFormat("####");
				}
				field.setAlign("right");
				fieldlist.add(field);
			}

			Table table = new Table("gz_sp_report");
			boolean flag = false;// 判断有没有在gz_sp_report表中新增字段
			for (int i = 0; i < sum_fields.length; i++) {
				String sum_field = sum_fields[i];
				if (!vo_gz_sp_report.hasAttribute(sum_field.toLowerCase())) {
					flag = true;
					
					if(setMap.get(sum_field.toUpperCase())==null)
						continue;
					LazyDynaBean _bean=(LazyDynaBean)setMap.get(sum_field.toUpperCase());
					int itemlength=12; //Integer.parseInt((String)_bean.get("itemlength"));
					int decwidth=Integer.parseInt((String)_bean.get("decwidth"));
					String itemdesc=(String)_bean.get("itemdesc");
					
					field = new Field(sum_field.toLowerCase(), itemdesc);
					String format = "############################";
					if (decwidth > 0) {
						field.setDatatype(DataType.FLOAT);
						field.setLength(itemlength);
						field.setDecimalDigits(decwidth);
						field.setFormat(format.substring(0, itemlength)
								+ "." + format.substring(0, decwidth));
					} else {
						field.setDatatype(DataType.INT);
						field.setFormat("####");
					}
					field.setAlign("right");
					table.addField(field);
				}
			}
			if (flag) {
				dbw.addColumns(table);
				dbmodel.reloadTableModel("gz_sp_report");
			}
			syncGzField(dao,sum_fields,setMap,gzbo);
			
			// 汇总数据
			// 确定当前需汇总记录的所有发放日期和发放次数
			/*
			 * String sql = "select a00z2 from salaryhistory where
			 * salaryid="+salaryid+" and
			 * curr_user='"+this.userView.getUserName()+"' and sp_flag
			 * in('02','07')"; ArrayList a00z2List = new ArrayList();
			 * this.frecset = dao.search(sql); SimpleDateFormat sdf = new
			 * SimpleDateFormat("yyyy.MM.dd"); while(this.frecset.next()){ Date
			 * date = this.frecset.getDate("a00z2"); String d =
			 * sdf.format(date); a00z2List.add(d); }
			 */

			/** 发放日期和发放次数 */
			String bosdate = (String) this.getFormHM().get("bosdate");
			String count = (String) this.getFormHM().get("count");
			/** 求发放日期及发放次数列表 */
			ArrayList datelist =getCoolectDateList(salaryid);// gzbo.getCoolectDateList();
			this.getFormHM().put("datelist", datelist);
			if ((bosdate == null || "".equalsIgnoreCase(bosdate))
					&& datelist.size() > 0) {
				bosdate = ((CommonData) datelist.get(datelist.size() - 1))
						.getDataValue();
			} else if (bosdate == null || "".equalsIgnoreCase(bosdate))
				bosdate = PubFunc.FormatDate(new Date(), "yyyy.MM.dd");
			else {
				boolean isExist = false;
				for (int i = 0; i < datelist.size(); i++) {
					CommonData data = (CommonData) datelist.get(i);
					if (data.getDataValue().equalsIgnoreCase(bosdate))
						isExist = true;
				}
				if (!isExist && datelist.size() > 0)
					bosdate = ((CommonData) datelist.get(0))
							.getDataValue();
				if (!isExist && datelist.size() == 0)
					bosdate = PubFunc.FormatDate(new Date(), "yyyy.MM.dd");
			}

			ArrayList countlist = gzbo.getCollectCountList(bosdate);
			if ((count == null || "".equalsIgnoreCase(count))
					&& countlist.size() > 0)
				count = ((CommonData) countlist.get(countlist.size() - 1))
						.getDataValue();
			else if (count == null || "".equalsIgnoreCase(count))
				count = "1";
			else {
				boolean isExist = false;
				for (int i = 0; i < countlist.size(); i++) {
					CommonData data = (CommonData) countlist.get(i);
					if (data.getDataValue().equalsIgnoreCase(count))
						isExist = true;
				}
				if (!isExist && countlist.size() > 0)
					count = ((CommonData) countlist.get(countlist.size() - 1))
							.getDataValue();
				else if (!isExist && countlist.size() == 0)
					count = "1";

			}
			String count1 = count;
			String bosdate1 = bosdate;
			this.getFormHM().put("datelist", datelist);
			this.getFormHM().put("countlist", countlist);
			this.getFormHM().put("bosdate", bosdate);
			this.getFormHM().put("count", count);

			String sql = "";
			sql = "delete from gz_sp_report where salaryid=" + salaryid
			+ " and userid='" + this.userView.getUserId() + "'";
			dao.update(sql);
			RecordVo vo = new RecordVo("gz_sp_report");
			int num=0;
			for (int m = 0; m < datelist.size(); m++) {
				
				
				bosdate = ((CommonData) datelist.get(m)).getDataValue();
				countlist = gzbo.getCollectCountList(bosdate);
				{
					for (int z = 0; z < countlist.size(); z++) {
						num++;
						count = ((CommonData) countlist.get(z)).getDataValue();
						switch (Sql_switcher.searchDbServer()) {
						case Constant.MSSQL: {
							if (dbw.isExistTable("##gz_collect_"
									+ this.userView.getUserName(),false)&&num==1) {
								dbw.dropTable("##gz_collect_"
										+ this.userView.getUserName());
							}
							break;
						}
						case Constant.DB2: {
							break;
						}
						case Constant.ORACEL: {
							if (dbw.isExistTable("gz_collect_"
									+ this.userView.getUserName(),false)&&num==1) {
								dbw.dropTable("gz_collect_"
										+ this.userView.getUserName());
							}
							break;
						}
						}
						// 将当前用户、当前发放日期、当前发放次数需要汇总的记录写入临时表，表名：mssql:##gz_collect_当前用户名
						// oracle:gz_collect_当前用户名
						if ("0".equals(sum_type)) {// 当汇总类型为单位 0
							// 得到组织机构树
							if (this.orgtree == null)
								getOrgTree(dao, false, 0);// 只有单位节点
							if ("".equals(orgid.trim())) {// 当未设置汇总单位时，按所属单位b0110汇总
								switch (Sql_switcher.searchDbServer()) {
								case Constant.MSSQL: {
									sql = "select a00z2,a00z3,nbase,a0100,a00z0,a00z1,b0110,"
											+ sum_fields_str
											+ ",sp_flag,(b0110) as org into ##gz_collect_"
											+ this.userView.getUserName()
											+ " from salaryhistory where salaryid="
											+ salaryid + " and ( curr_user='"
											+ this.userView.getUserName()
											+ "' or AppUser Like '%;"
											+ this.userView.getUserName()
											+ ";%' ) and a00z2="
											+ Sql_switcher.dateValue(bosdate)
											+ " and a00z3=" + count;
									if(num>1)
									{
										sql ="insert into ##gz_collect_"+ this.userView.getUserName()
											+" (a00z2,a00z3,nbase,a0100,a00z0,a00z1,b0110,"+ sum_fields_str+ ",sp_flag,org)"
											+" select a00z2,a00z3,nbase,a0100,a00z0,a00z1,b0110,"
											+ sum_fields_str
											+ ",sp_flag,b0110 " 
											+ " from salaryhistory where salaryid="
											+ salaryid + " and ( curr_user='"
											+ this.userView.getUserName()
											+ "' or AppUser Like '%;"
											+ this.userView.getUserName()
											+ ";%' ) and a00z2="
											+ Sql_switcher.dateValue(bosdate)
											+ " and a00z3=" + count;
									}
									
									dao.update(sql);
									sql = "update ##gz_collect_"+this.userView.getUserName()+" set org='-1' where  nullif(org,'') is null ";//+ Sql_switcher.sqlNull("org", "-1")+ "='-1'";
									dao.update(sql);
									break;
								}
								case Constant.DB2: {
									break;
								}
								case Constant.ORACEL: {
									sql = "create table gz_collect_"
											+ this.userView.getUserName()
											+ " as select a00z2,a00z3,nbase,a0100,a00z0,a00z1,b0110,"
											+ sum_fields_str
											+ ",sp_flag,(b0110) as org from salaryhistory where salaryid="
											+ salaryid + " and ( curr_user='"
											+ this.userView.getUserName()
											+ "' or AppUser Like '%;"
											+ this.userView.getUserName()
											+ ";%' ) and a00z2="
											+ Sql_switcher.dateValue(bosdate)
											+ " and a00z3=" + count;
									if(num>1)
									{
										sql = "insert into gz_collect_"
											+ this.userView.getUserName()
											+" (a00z2,a00z3,nbase,a0100,a00z0,a00z1,b0110,"+sum_fields_str+",sp_flag,org) "
											+ "  select a00z2,a00z3,nbase,a0100,a00z0,a00z1,b0110,"
											+ sum_fields_str
											+ ",sp_flag,b0110 from salaryhistory where salaryid="
											+ salaryid + " and ( curr_user='"
											+ this.userView.getUserName()
											+ "' or AppUser Like '%;"
											+ this.userView.getUserName()
											+ ";%' ) and a00z2="
											+ Sql_switcher.dateValue(bosdate)
											+ " and a00z3=" + count;
									}
									
									dao.update(sql);
									sql = "update gz_collect_"+this.userView.getUserName()+" set org='-1' where  nullif(org,'') is null ";//+ Sql_switcher.sqlNull("org", "-1")+ "='-1'";
									dao.update(sql);
									break;
								}
								}

							} else {// 按设置的汇总单位汇总

								switch (Sql_switcher.searchDbServer()) {
								case Constant.MSSQL: {
									sql = "select a00z2,a00z3,nbase,a0100,a00z0,a00z1,"
											+ orgid
											+ ","
											+ sum_fields_str
											+ ",sp_flag,("
											+ orgid
											+ ") as org into ##gz_collect_"
											+ this.userView.getUserName()
											+ " from salaryhistory where salaryid="
											+ salaryid + " and ( curr_user='"
											+ this.userView.getUserName()
											+ "' or AppUser Like '%;"
											+ this.userView.getUserName()
											+ ";%' ) and a00z2="
											+ Sql_switcher.dateValue(bosdate)
											+ " and a00z3=" + count;
									
									if(num>1)
									{
										sql ="insert into ##gz_collect_"+ this.userView.getUserName()
										+" (a00z2,a00z3,nbase,a0100,a00z0,a00z1,"+orgid+","+ sum_fields_str+ ",sp_flag,org)"
										+"select a00z2,a00z3,nbase,a0100,a00z0,a00z1,"
										+ orgid
										+ ","
										+ sum_fields_str
										+ ",sp_flag,"
										+ orgid
										+ " from salaryhistory where salaryid="
										+ salaryid + " and ( curr_user='"
										+ this.userView.getUserName()
										+ "' or AppUser Like '%;"
										+ this.userView.getUserName()
										+ ";%' ) and a00z2="
										+ Sql_switcher.dateValue(bosdate)
										+ " and a00z3=" + count;
										
										
										
									}
									
									
									
									dao.update(sql);
									// sql = "update
									// ##gz_collect_"+this.userView.getUserName()+"
									// set org="+orgid;
									// dao.update(sql);
									sql = "update ##gz_collect_"+this.userView.getUserName()+" set org='-1' where  nullif(org,'') is null "; //+ Sql_switcher.sqlNull("org", "-1")+ "='-1'";
									dao.update(sql);
									break;
								}
								case Constant.DB2: {
									break;
								}
								case Constant.ORACEL: {
									sql = "create table gz_collect_"
											+ this.userView.getUserName()
											+ " as select a00z2,a00z3,nbase,a0100,a00z0,a00z1,"
											+ orgid
											+ ","
											+ sum_fields_str
											+ ",sp_flag,("
											+ orgid
											+ ") as org from salaryhistory where salaryid="
											+ salaryid + " and ( curr_user='"
											+ this.userView.getUserName()
											+ "' or AppUser Like '%;"
											+ this.userView.getUserName()
											+ ";%' ) and a00z2="
											+ Sql_switcher.dateValue(bosdate)
											+ " and a00z3=" + count;
									if(num>1)
									{
										sql = "insert into gz_collect_"
											+ this.userView.getUserName()
											+" ( a00z2,a00z3,nbase,a0100,a00z0,a00z1,"+ orgid+","+sum_fields_str+ ",sp_flag,org )"
											+ "   select a00z2,a00z3,nbase,a0100,a00z0,a00z1,"
											+ orgid
											+ ","
											+ sum_fields_str
											+ ",sp_flag,"
											+ orgid
											+ " from salaryhistory where salaryid="
											+ salaryid + " and ( curr_user='"
											+ this.userView.getUserName()
											+ "' or AppUser Like '%;"
											+ this.userView.getUserName()
											+ ";%' ) and a00z2="
											+ Sql_switcher.dateValue(bosdate)
											+ " and a00z3=" + count;
									}
									
									
									dao.update(sql);
									sql = "update gz_collect_"+this.userView.getUserName()+" set org='-1' where  nullif(org,'') is null  ";//+ Sql_switcher.sqlNull("org", "-1")+ "='-1'";
									dao.update(sql);
									break;
								}
								}

							}
						} else if ("1".equals(sum_type)) {// 当汇总类型是部门 1

							// 得到组织机构树
							if (this.orgtree == null)
								getOrgTree(dao, true, Integer.parseInt(layer));// 单位和部门节点

							if ("".equals(deptid)) {// 当未设置汇总部门时，按所属部门e0122汇总

								switch (Sql_switcher.searchDbServer()) {
								case Constant.MSSQL: {
									sql = "select a00z2,a00z3,nbase,a0100,a00z0,a00z1,b0110,e0122,"
											+ sum_fields_str
											+ ",sp_flag,(e0122) as org into ##gz_collect_"
											+ this.userView.getUserName()
											+ " from salaryhistory where salaryid="
											+ salaryid + " and ( curr_user='"
											+ this.userView.getUserName()
											+ "' or AppUser Like '%;"
											+ this.userView.getUserName()
											+ ";%' ) and a00z2="
											+ Sql_switcher.dateValue(bosdate)
											+ " and a00z3=" + count;
									
									if(num>1)
									{
										sql ="insert into ##gz_collect_"+ this.userView.getUserName() 
											+" (a00z2,a00z3,nbase,a0100,a00z0,a00z1,b0110,e0122,"+sum_fields_str+",sp_flag,org)"
											+"select a00z2,a00z3,nbase,a0100,a00z0,a00z1,b0110,e0122,"
											+ sum_fields_str
											+ ",sp_flag, e0122  from salaryhistory where salaryid="
											+ salaryid + " and ( curr_user='"
											+ this.userView.getUserName()
											+ "' or AppUser Like '%;"
											+ this.userView.getUserName()
											+ ";%' ) and a00z2="
											+ Sql_switcher.dateValue(bosdate)
											+ " and a00z3=" + count;
									
									}
									
									
									dao.update(sql);
									// sql = "update
									// ##gz_collect_"+this.userView.getUserName()+"
									// set org=b0110";
									// dao.update(sql);
									sql = "update ##gz_collect_"
											+ this.userView.getUserName()
											+ " set org=b0110 where  nullif(org,'') is null   ";
											// + Sql_switcher.sqlNull("org", "-1")+ "='-1'";
									dao.update(sql);
									sql = "update ##gz_collect_"+this.userView.getUserName()+" set org='-1' where  nullif(org,'') is null  "; //+ Sql_switcher.sqlNull("org", "-1")+ "='-1'";
									dao.update(sql);
									break;
								}
								case Constant.DB2: {
									break;
								}
								case Constant.ORACEL: {
									sql = "create table gz_collect_"
											+ this.userView.getUserName()
											+ " as select a00z2,a00z3,nbase,a0100,a00z0,a00z1,b0110,e0122,"
											+ sum_fields_str
											+ ",sp_flag,(e0122) as org from salaryhistory where salaryid="
											+ salaryid + " and ( curr_user='"
											+ this.userView.getUserName()
											+ "' or AppUser Like '%;"
											+ this.userView.getUserName()
											+ ";%' ) and a00z2="
											+ Sql_switcher.dateValue(bosdate)
											+ " and a00z3=" + count;
									if(num>1)
									{
										sql = "insert into gz_collect_"+ this.userView.getUserName()
											+ " (a00z2,a00z3,nbase,a0100,a00z0,a00z1,b0110,e0122,"+sum_fields_str+",sp_flag,org) "
											+ "   select a00z2,a00z3,nbase,a0100,a00z0,a00z1,b0110,e0122,"
											+ sum_fields_str
											+ ",sp_flag, e0122  from salaryhistory where salaryid="
											+ salaryid + " and ( curr_user='"
											+ this.userView.getUserName()
											+ "' or AppUser Like '%;"
											+ this.userView.getUserName()
											+ ";%' ) and a00z2="
											+ Sql_switcher.dateValue(bosdate)
											+ " and a00z3=" + count;
									}
									
									dao.update(sql);
									// sql = "update
									// ##gz_collect_"+this.userView.getUserName()+"
									// set org=b0110";
									// dao.update(sql);
									sql = "update gz_collect_"
											+ this.userView.getUserName()
											+ " set org=b0110 where  nullif(org,'') is null  ";
										//	+ Sql_switcher.sqlNull("org", "-1")+ "='-1'";
									dao.update(sql);
									sql = "update gz_collect_"+this.userView.getUserName()+" set org='-1' where   nullif(org,'') is null "; //+ Sql_switcher.sqlNull("org", "-1")+ "='-1'";
									dao.update(sql);
									break;
								}
								}

							} else {// 按设置的汇总部门汇总
								switch (Sql_switcher.searchDbServer()) {
								case Constant.MSSQL: {
									sql = "select a00z2,a00z3,nbase,a0100,a00z0,a00z1,"
											+ orgid
											+ ","
											+ deptid
											+ ","
											+ sum_fields_str
											+ ",sp_flag,("
											+ deptid
											+ ") as org into ##gz_collect_"
											+ this.userView.getUserName()
											+ " from salaryhistory where salaryid="
											+ salaryid + " and ( curr_user='"
											+ this.userView.getUserName()
											+ "' or AppUser Like '%;"
											+ this.userView.getUserName()
											+ ";%' ) and a00z2="
											+ Sql_switcher.dateValue(bosdate)
											+ " and a00z3=" + count;
									if(num>1)
									{
										sql ="insert into ##gz_collect_"+ this.userView.getUserName()
											+" (a00z2,a00z3,nbase,a0100,a00z0,a00z1,"+orgid+","+deptid+","+sum_fields_str+",sp_flag,org) "
											+"select a00z2,a00z3,nbase,a0100,a00z0,a00z1,"
											+ orgid
											+ ","
											+ deptid
											+ ","
											+ sum_fields_str
											+ ",sp_flag,"
											+ deptid
											+ " from salaryhistory where salaryid="
											+ salaryid + " and ( curr_user='"
											+ this.userView.getUserName()
											+ "' or AppUser Like '%;"
											+ this.userView.getUserName()
											+ ";%' ) and a00z2="
											+ Sql_switcher.dateValue(bosdate)
											+ " and a00z3=" + count;
										
									}
									dao.update(sql);
									// sql = "update
									// ##gz_collect_"+this.userView.getUserName()+"
									// set org="+deptid;
									// dao.update(sql);
									sql = "update ##gz_collect_"
											+ this.userView.getUserName()
											+ " set org=" + orgid + " where  nullif(org,'') is null  ";
										//	+ Sql_switcher.sqlNull("org", "-1")+ "='-1'";
									dao.update(sql);
									sql = "update ##gz_collect_"+this.userView.getUserName()+" set org='-1' where  nullif(org,'') is null "; //+ Sql_switcher.sqlNull("org", "-1")+ "='-1'";
									dao.update(sql);
									break;
								}
								case Constant.DB2: {
									break;
								}
								case Constant.ORACEL: {
									sql = "create table gz_collect_"
											+ this.userView.getUserName()
											+ " as select a00z2,a00z3,nbase,a0100,a00z0,a00z1,"
											+ orgid
											+ ","
											+ deptid
											+ ","
											+ sum_fields_str
											+ ",sp_flag,("
											+ deptid
											+ ") as org from salaryhistory where salaryid="
											+ salaryid + " and ( curr_user='"
											+ this.userView.getUserName()
											+ "' or AppUser Like '%;"
											+ this.userView.getUserName()
											+ ";%' ) and a00z2="
											+ Sql_switcher.dateValue(bosdate)
											+ " and a00z3=" + count;
									if(num>1)
									{
										sql =" insert into gz_collect_"+ this.userView.getUserName()
											+" (a00z2,a00z3,nbase,a0100,a00z0,a00z1,"+orgid+","+deptid+","+sum_fields_str+",sp_flag,org )"
											 
											+ "  select a00z2,a00z3,nbase,a0100,a00z0,a00z1,"
											+ orgid
											+ ","
											+ deptid
											+ ","
											+ sum_fields_str
											+ ",sp_flag, "
											+ deptid
											+ "  from salaryhistory where salaryid="
											+ salaryid + " and ( curr_user='"
											+ this.userView.getUserName()
											+ "' or AppUser Like '%;"
											+ this.userView.getUserName()
											+ ";%' ) and a00z2="
											+ Sql_switcher.dateValue(bosdate)
											+ " and a00z3=" + count;
									}
									
									dao.update(sql);
									// sql = "update
									// ##gz_collect_"+this.userView.getUserName()+"
									// set org="+deptid;
									// dao.update(sql);
									sql = "update gz_collect_"
											+ this.userView.getUserName()
											+ " set org=" + orgid + " where  nullif(org,'') is null  ";
										//	+ Sql_switcher.sqlNull("org", "-1")+ "='-1'";
									dao.update(sql);
									sql = "update gz_collect_"+this.userView.getUserName()+" set org='-1' where  nullif(org,'') is null "; //+ Sql_switcher.sqlNull("org", "-1")+ "='-1'";
									dao.update(sql);
									break;
								}
								}
							}
						}
						for (Iterator i = this.orgtree.keySet().iterator(); i
								.hasNext();) {
							String key = (String) i.next();
							// System.out.print(key+":");
							if (topcodeitemid.contains(key)) {
								switch (Sql_switcher.searchDbServer()) {
								case Constant.MSSQL: {
									sql = "select " + sum_fields_str
											+ ",sp_flag from ##gz_collect_"
											+ this.userView.getUserName()
											+ " where org like '" + key
											+ "%' and nullif(org,'') is not null ";
											//+ Sql_switcher.sqlNull("org", "-1")
											//+ "<>'-1'";
									break;
								}
								case Constant.ORACEL: {
									sql = "select " + sum_fields_str
											+ ",sp_flag from gz_collect_"
											+ this.userView.getUserName()
											+ " where org like '" + key
											+ "%' and nullif(org,'') is not null  ";
										//	+ Sql_switcher.sqlNull("org", "-1")
										//	+ "<>'-1'";
									break;
								}
								}
								this.doCollect(dao, sql, sum_fields, vo,
										bosdate, count, key, salaryid,setMap);
							}
							ArrayList al = (ArrayList) this.orgtree.get(key);
							for (int n = 0; n < al.size(); n++) {
								// System.out.print((String)al.get(n)+",");
								switch (Sql_switcher.searchDbServer()) {
								case Constant.MSSQL: {
									sql = "select " + sum_fields_str
											+ ",sp_flag from ##gz_collect_"
											+ this.userView.getUserName()
											+ " where org like '"
											+ (String) al.get(n) + "%' and  nullif(org,'') is not null  ";
										//	+ Sql_switcher.sqlNull("org", "-1")
										//	+ "<>'-1'";
									break;
								}
								case Constant.ORACEL: {
									sql = "select " + sum_fields_str
											+ ",sp_flag from gz_collect_"
											+ this.userView.getUserName()
											+ " where org like '"
											+ (String) al.get(n) + "%' and  nullif(org,'') is not null  ";
									//		+ Sql_switcher.sqlNull("org", "-1")
									//		+ "<>'-1'";
									break;
								}
								}
								this.doCollect(dao, sql, sum_fields, vo,
										bosdate, count, (String) al.get(n),
										salaryid,setMap);
							}
							// System.out.println();

						}
						// 汇总汇总部门为空的所有记录 暂时写为0
					 
						switch (Sql_switcher.searchDbServer()) {
						case Constant.MSSQL: {
							sql = "select " + sum_fields_str
									+ ",sp_flag from ##gz_collect_"
									+ this.userView.getUserName() + " where org='-1'";
							break;
						}
						case Constant.ORACEL: {
							sql = "select " + sum_fields_str
									+ ",sp_flag from gz_collect_"
									+ this.userView.getUserName() + " where org='-1'";
							break;
						}
						}
						this.doCollect(dao, sql, sum_fields, vo, bosdate,
								count, "-1", salaryid,setMap); 
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			Field field = new Field("sp_flag", ResourceFactory
					.getProperty("label.gz.sp"));
			field.setLength(50);
			field.setCodesetid("23");
			field.setDatatype(DataType.STRING);
			// field.setReadonly(true);
			fieldlist.add(field);
			this.getFormHM().put("fieldlist", fieldlist);
			this.getFormHM().put("sum_fields_str", sum_fields_str);
			String tempTableName = "";
			switch (Sql_switcher.searchDbServer()) {
			case Constant.MSSQL: {
				tempTableName = "##gz_collect_"+ this.userView.getUserName();
				break;
			}
			case Constant.ORACEL: {
				tempTableName = "gz_collect_"+ this.userView.getUserName();
				break;
			}
			}
			this.getFormHM().put("tempTableName", tempTableName);
		}
	}

	/**
	 * 加载组织结构树
	 * 
	 * @param loadtype是否加载部门
	 *            true 加载 false 不加载
	 * @param deptlayer
	 *            加载部门层级 1，2.....
	 */
	public void getOrgTree(ContentDAO dao, boolean loadtype, int deptlayer)
			throws Exception {
		String sql = "select codeitemid from organization where codesetid='UN' and codeitemid=parentid";
		this.frecset = dao.search(sql);
		this.orgtree = new HashMap();
		while (this.frecset.next()) {
			topcodeitemid.add(this.frecset.getString("codeitemid"));
			this.getOrgTree(dao, loadtype, deptlayer, this.frecset
					.getString("codeitemid"));
		}
		/*
		 * for(Iterator i=this.orgtree.keySet().iterator();i.hasNext();){ String
		 * key = (String)i.next(); System.out.print(key+":"); ArrayList al =
		 * (ArrayList)this.orgtree.get(key); for(int n=0;n<al.size();n++){
		 * System.out.print((String)al.get(n)+","); } System.out.println(); }
		 */
	}

	private void getOrgTree(ContentDAO dao, boolean loadtype, int deptlayer,
			String codeitemid) throws Exception {
		String sql = "";
		if (loadtype)
			sql = "select codeitemid,codesetid from organization where codesetid<>'@K' and parentid='"
					+ codeitemid + "' and codeitemid<>parentid";
		else
			sql = "select codeitemid,codesetid from organization where codesetid='UN' and parentid='"
					+ codeitemid + "' and codeitemid<>parentid";
		ResultSet rs = null;
		try {
			rs = dao.search(sql);
			ArrayList childOrg = null;
			while (rs.next()) {
				if (orgtree.containsKey(codeitemid)) {
					childOrg = (ArrayList) orgtree.get(codeitemid);
					childOrg.add(rs.getString("codeitemid"));
					orgtree.put(codeitemid, childOrg);
				} else {
					childOrg = new ArrayList();
					childOrg.add(rs.getString("codeitemid"));
					orgtree.put(codeitemid, childOrg);
				}
				if (loadtype && "UM".equals(rs.getString("codesetid"))) {
					this.currentlayer = 1;
					if (currentlayer(rs.getString("codeitemid")) == deptlayer) {
					} else {
						getOrgTree(dao, loadtype, deptlayer, rs
								.getString("codeitemid"));
					}
				} else {
					getOrgTree(dao, loadtype, deptlayer, rs
							.getString("codeitemid"));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			rs.close();
		}

	}

	/**
	 * 判断部门是第几层
	 * 
	 * @param codeitemid
	 * @return
	 */
	private int currentlayer(String codeitemid) throws Exception {
		String sql = "select * from organization where codesetid='UM' and codeitemid=(select parentid from organization where codesetid='UM' and codeitemid='"
				+ codeitemid + "')";
		ResultSet rset = null;
		try {
			Connection conn = this.getFrameconn();
			ContentDAO dao = new ContentDAO(conn);
			rset = dao.search(sql);
			while (rset.next()) {
				++currentlayer;
				currentlayer(rset.getString("codeitemid"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			rset.close();
		}

		return currentlayer;
	}

	/**
	 * 汇总数据
	 * 
	 * @param dao
	 * @param sql
	 * @param sum_fields
	 *            汇总指标
	 * @param vo
	 *            gz_sp_report表RecordVo
	 * @param bosdate
	 *            发放日期
	 * @param count
	 *            发放次数
	 * @param key
	 *            汇总单位或部门
	 * @param salaryid
	 * @throws Exception
	 */
	private void doCollect(ContentDAO dao, String sql, String[] sum_fields,
			RecordVo vo, String bosdate, String count, String key,
			String salaryid,HashMap setMap) throws Exception {
	//	System.out.println("sql="+sql);
		
		  
	 	sql+=" and a00z2="+ Sql_switcher.dateValue(bosdate)+ " and a00z3=" + count;
		this.frecset = dao.search(sql);
		HashMap temp = new HashMap();// 用于保存个汇总字段的各个总和
		String sp_flag_temp = "";
		String sp_flag = "";
		boolean hasRecord = false;// 通过查询看看是否有满足汇总的记录
		while (this.frecset.next()) {
			hasRecord = true;
			for (int n = 0; n < sum_fields.length; n++) {
				String sum_field = sum_fields[n];
				try {
					temp
							.put(
									sum_field,
									(temp.get(sum_field) != null ? ((BigDecimal) temp
											.get(sum_field))
											: new BigDecimal(0))
											.add((this.frecset
													.getBigDecimal(sum_field))));
				} catch (Exception e) {
					temp.put(sum_field,
							(temp.get(sum_field) != null ? ((BigDecimal) temp
									.get(sum_field)) : new BigDecimal(0))
									.add(new BigDecimal(0)));
				}
			}
			if ("".equals(sp_flag)) {
				sp_flag = sp_flag_temp = this.frecset.getString("sp_flag");
			}
			sp_flag = this.frecset.getString("sp_flag");
		}
		if (hasRecord) {
			if (!sp_flag.equalsIgnoreCase(sp_flag_temp)) {
				sp_flag = "02";// 选择单位或部门的人员薪资数据如果全部为已批状态，则汇总单位或部门记录为“已批”状态。如果全部为已驳回状态，则汇总单位或部门记录为“驳回”状态，否则汇总单位或部门记录为“已报批”状态。
			}

			vo.setDate("a00z2", bosdate.replaceAll("\\.", "-"));
			vo.setInt("a00z3", Integer.parseInt(count));
			vo.setString("b0110", key);
			
	//		String name=AdminCode.getCodeName("UN",key);
	//		if(name==null||name.trim().length()==0)
	//			name=AdminCode.getCodeName("UM",key);
	//		vo.setString("b0110",name);
			
			vo.setString("userid", this.userView.getUserId());
			vo.setInt("salaryid", Integer.parseInt(salaryid));
//			try {
//				dao.findByPrimaryKey(vo);
//				vo.setString("sp_flag", sp_flag);
//				for (int n = 0; n < sum_fields.length; n++) {
//					String sum_field = sum_fields[n];
//					vo.setString(sum_field.toLowerCase(), String.valueOf(temp
//							.get(sum_field)));
//				}
//				dao.updateValueObject(vo);
//			} catch (Exception e) {
				vo.setString("sp_flag", sp_flag);
				for (int n = 0; n < sum_fields.length; n++) {
					String sum_field = sum_fields[n];
					
					String value=((BigDecimal)temp.get(sum_field)).toString();
					if(setMap.get(sum_field.toUpperCase())!=null)
					{	 
						LazyDynaBean _bean=(LazyDynaBean)setMap.get(sum_field.toUpperCase()); 
						int decwidth=Integer.parseInt((String)_bean.get("decwidth"));
						value=PubFunc.round(value,decwidth);
					} 
					vo.setDouble(sum_field.toLowerCase(), Double.parseDouble(value));
				}
	//			System.out.println(vo.getString("a00z2")+"  "+vo.getInt("a00z3")+"   "+vo.getString("b0110")+"  "+vo.getInt("salaryid"));
				dao.addValueObject(vo);
			//}

		}
	}
	
	
	private void  syncGzField(ContentDAO dao,String[] sum_fields,HashMap setMap,SalaryTemplateBo gzbo)
	{
		try
		{
			
			 HashMap fieldMap=new HashMap();
			 for(int i=0;i<sum_fields.length;i++)
				 fieldMap.put(sum_fields[i].toUpperCase(),"1");
			 DbWizard dbw=new DbWizard(this.frameconn);
			 
			 HashMap dMap=new HashMap();
			 if(Sql_switcher.searchDbServer()==2) //ORACLE
			 {
				 String sql = "select column_name,data_type,data_length,data_precision,data_scale from user_tab_columns where Table_Name='GZ_SP_REPORT'";
				 RowSet rs = dao.search(sql);
				 while(rs.next())
				 {
					 String columnName=rs.getString("column_name");
					 int size=rs.getInt("data_length");
					 if("NUMBER".equalsIgnoreCase(rs.getString("data_type"))){//数值型的 data_precision代表字段总长度
						 size=rs.getInt("data_precision");
					 }else if("VARCHAR2".equalsIgnoreCase(rs.getString("data_type"))){//字符型的 data_length代表字段总长度
						 size=rs.getInt("data_length");
					 }
					int scale=rs.getInt("data_scale");
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("size",size+"");
					abean.set("scale",scale+"");
					dMap.put(columnName.toUpperCase(),abean);
				 }
			 }
			 
			 RowSet rowSet=dao.search("select * from gz_sp_report where 1=2");
			 ResultSetMetaData data=rowSet.getMetaData();
			 ArrayList alterList=new ArrayList();
			 ArrayList resetList=new ArrayList();
			 LazyDynaBean abean=null;
			 for(int i=1;i<=data.getColumnCount();i++)
			 {
					String columnName=data.getColumnName(i).toUpperCase();
					if(fieldMap.get(columnName)!=null&&setMap.get(columnName)!=null)
					{
						int columnType=data.getColumnType(i);	 
						int size=data.getColumnDisplaySize(i);
						int scale=data.getScale(i); 
						
						if(Sql_switcher.searchDbServer()==2)
						{
							abean=(LazyDynaBean)dMap.get(columnName);
							size=Integer.parseInt((String)abean.get("size"));
							scale=Integer.parseInt((String)abean.get("scale"));
						}
						 
						FieldItem item=DataDictionary.getFieldItem(columnName.toLowerCase());
				    	if(item==null|| "0".equals(item.getUseflag()))
							continue;
						
						switch(columnType)
						{
							case java.sql.Types.INTEGER: 
								if("N".equals(item.getItemtype()))
								{
									if(item.getDecimalwidth()!=scale){
										if(Sql_switcher.searchDbServer()==2)
											gzbo.alertColumn("gz_sp_report",item,dbw,dao);
										else
											alterList.add(item.cloneField());
									}else if(size<item.getItemlength()&&item.getItemlength()<=10) //2013-11-23  如果指标长度改大了，需同步结构
									{
										if(Sql_switcher.searchDbServer()==2)
											gzbo.alertColumn("gz_sp_report",item,dbw,dao);
										else
											alterList.add(item.cloneField());
									} 	
								}
								if(!"N".equals(item.getItemtype()))
								{
									if("A".equals(item.getItemtype()))
									{
										if(Sql_switcher.searchDbServer()==2)
											gzbo.alertColumn("gz_sp_report",item,dbw,dao);
										else
											alterList.add(item.cloneField());
									}
									else		
										resetList.add(item.cloneField());
								} 
								break;
							case java.sql.Types.DOUBLE: 
								if("N".equals(item.getItemtype()))
								{
									if(item.getDecimalwidth()!=scale)
									{
										if(Sql_switcher.searchDbServer()==2)
											gzbo.alertColumn("gz_sp_report",item,dbw,dao);
										else
											alterList.add(item.cloneField());
									}
									else if((size-scale)<item.getItemlength()) //2013-11-23  如果指标长度改大了，需同步结构
									{
										if(Sql_switcher.searchDbServer()==2)
											gzbo.alertColumn("gz_sp_report",item,dbw,dao);
										else
											alterList.add(item.cloneField());
									} 
								}
								if(!"N".equals(item.getItemtype()))
								{
									if("A".equals(item.getItemtype()))
									{
										if(Sql_switcher.searchDbServer()==2)
											gzbo.alertColumn("gz_sp_report",item,dbw,dao);
										else
											alterList.add(item.cloneField());
									}
									else		
										resetList.add(item.cloneField());
								} 
								
								break;
							case java.sql.Types.NUMERIC:
								if("N".equals(item.getItemtype()))
								{
									if(item.getDecimalwidth()!=scale)
									{
										if(Sql_switcher.searchDbServer()==2)
											gzbo.alertColumn("gz_sp_report",item,dbw,dao);
										else
											alterList.add(item.cloneField());
									}
									else if((size-scale)<item.getItemlength()) //2013-11-23  如果指标长度改大了，需同步结构
									{
										if(Sql_switcher.searchDbServer()==2)
											gzbo.alertColumn("gz_sp_report",item,dbw,dao);
										else
											alterList.add(item.cloneField());
									} 
								}
								if(!"N".equals(item.getItemtype()))
								{
									if("A".equals(item.getItemtype()))
									{
										if(Sql_switcher.searchDbServer()==2)
											gzbo.alertColumn("gz_sp_report",item,dbw,dao);
										else
											alterList.add(item.cloneField());
									}
									else		
										resetList.add(item.cloneField());
								}
								break;	
						}
					}
			 }
			
			 Table table=new Table("gz_sp_report");
			 if(Sql_switcher.searchDbServer()!=2)  //不为oracle
			 { 
				    for(int i=0;i<alterList.size();i++)
				    {
				    	
				    	table.addField((Field)alterList.get(i)); 
				    }
					if(alterList.size()>0)
						dbw.alterColumns(table);
					 table.clear();
			 } 
			 
			 for(int i=0;i<resetList.size();i++)
					table.addField((Field)resetList.get(i));
			 if(resetList.size()>0)
			 {
				 dbw.dropColumns(table);
				 dbw.addColumns(table);
			 }
			 
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
	/**
	 * 获取汇总日期
	 * @return
	 */
	public ArrayList getCoolectDateList(String salaryid)
	{
		ArrayList list=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select distinct A00Z2 from salaryhistory where salaryid=");
			buf.append(salaryid);
			buf.append(" and ( sp_flag='07' or sp_flag='02' ) and curr_user='"+this.userView.getUserName()+"'   order by A00Z2 desc");
	//		buf.append(" and (( curr_user='"+this.userview.getUserId()+"' and ( sp_flag='02' or sp_flag='07' ) ) or ( ( (AppUser is null  "+this.getPrivWhlStr("")+"  )  or AppUser Like '%;"+this.userview.getUserName()+";%' ) and  ( sp_flag='06' or  sp_flag='03' ) ) )  order by A00Z2 desc");
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rset=dao.search(buf.toString());
			CommonData temp=null;
			while(rset.next())
			{
				String strdate=PubFunc.FormatDate(rset.getDate("A00Z2"), "yyyy.MM.dd");
				temp=new CommonData(strdate,strdate);
				list.add(temp);
			}
			rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	
	
	
}
