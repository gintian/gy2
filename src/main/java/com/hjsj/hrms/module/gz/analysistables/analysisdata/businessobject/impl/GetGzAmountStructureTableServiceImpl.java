package com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.impl;

import com.hjsj.hrms.businessobject.gz.gz_analyse.GzAnalyseBo;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.GetGzAmountStructureTableService;
import com.hjsj.hrms.module.gz.analysistables.util.GzAnalysisUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class GetGzAmountStructureTableServiceImpl implements GetGzAmountStructureTableService{
	private UserView userView;
	private Connection conn;
	
	public GetGzAmountStructureTableServiceImpl(UserView userView, Connection frameconn) {
		this.userView = userView;
		this.conn = frameconn;
	}

	@Override
    public HashMap<String, Object> findAllSalaryItem(String rsdtlid, String salaryids, String fieldid, String codevalue) throws GeneralException {
		HashMap<String, Object> result= new HashMap<String, Object>();
		StringBuffer sql_salaryid = new StringBuffer(" 1=2 ");//salaryid拼接sql
		ArrayList<String> list_salaryid = new ArrayList<String>();//salaryid集合
		
		ArrayList<HashMap<String, String>> fieldListId = new ArrayList<HashMap<String, String>>();//薪资项（代码类）
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		String sql = "";
		HashMap<String, String> map_temp = new HashMap<String, String>();
		String needShowFront = ",b0110,e0122,";
		try {
			salaryids = salaryids + ",";
			String[] salaryid_ = salaryids.split(",");
			for(int i = 0; i < salaryid_.length; i++) {
				if(StringUtils.isNotBlank(salaryid_[i])) {
					sql_salaryid.append(" or salaryid = ?");
					list_salaryid.add(salaryid_[i]);
				}
			}
			//薪资项
			boolean isExists = false;
			sql = "select itemid,itemdesc,codesetid from salaryset where (" + sql_salaryid + ")  "
						+ "and codesetid is not null and codesetid <> '0' group by itemid,itemdesc,codesetid";
			rs = dao.search(sql, list_salaryid);
			while(rs.next()) {
				map_temp = new HashMap<String, String>();
				String itemid_tem = rs.getString("itemid");
				if("0".equals(userView.analyseFieldPriv(itemid_tem)))
					continue;
				String codesetid = rs.getString("codesetid");
				map_temp.put("id", itemid_tem + "`" + codesetid);
				map_temp.put("name", rs.getString("itemdesc"));
				//如果是b0110或者e0122就直接置頂
				if(needShowFront.indexOf(itemid_tem.toLowerCase()) > -1)
					fieldListId.add(0, map_temp);
				else 
					fieldListId.add(map_temp);
				if(fieldid.equalsIgnoreCase(itemid_tem + "`" + codesetid)) {
					isExists = true;
				}
			}
				
			if(StringUtils.isBlank(fieldid) || !isExists) {
				fieldid = fieldListId.size()>0?(String)((HashMap<String, String>)fieldListId.get(0)).get("id"):"";
			}
			
			String codesetid = "";
			if(StringUtils.isNotBlank(fieldid)) {
				codesetid = DataDictionary.getFieldItem(fieldid.split("`")[0]).getCodesetid();
			}
			result.put("fieldListId", fieldListId);//薪资项
			result.put("fieldid", fieldid);//值
			result.put("codesetid", codesetid);//代码
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return result;
	}
	
	@Override
    public HashMap getAllData(boolean selectAll, String rsdtld, int year, int endmonth, String fieldid, String codevalue, String salaryids,
                              String nbases, String verifying) throws GeneralException {
		HashMap result = new HashMap();
		ArrayList<LazyDynaBean> list_data= new ArrayList<LazyDynaBean>();
		ArrayList<ColumnsInfo> list_column = new ArrayList<ColumnsInfo>();
		GzAnalysisUtil gzAnalysisUtil = new GzAnalysisUtil(this.conn, this.userView);
		try {
			ArrayList itemlist = getHeadItemList(rsdtld);
			String b_units=this.userView.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
			String privSql=gzAnalysisUtil.getPrivSQL("salaryhistory", nbases, salaryids, b_units, "");
			StringBuffer sum_sql = new StringBuffer();
			StringBuffer filed_sql = new StringBuffer();
			for (int i = 0; i < itemlist.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean) itemlist.get(i);
				if (((String) bean.get("itemid")).startsWith("avg"))
					continue;
				
				String itemid = (String) bean.get("itemid");
				sum_sql.append(", sum("+Sql_switcher.isnull(itemid, "0")+") ");
				sum_sql.append(itemid);
				filed_sql.append("," + itemid);
			}
			
			//可以用1个sql写，不过速度不行
			HashMap beforemap = getBeforeYearDataList(selectAll, year - 1, itemlist, nbases, salaryids, endmonth, fieldid, codevalue, 
					sum_sql.toString(), privSql, filed_sql.toString(), verifying);
			HashMap nowData = getBeforeYearDataList(selectAll, year, itemlist, nbases, salaryids, endmonth, fieldid, codevalue, 
					sum_sql.toString(), privSql, filed_sql.toString(), verifying);
			
			LazyDynaBean itembean = null;
			for (int i = 1; i < 14; i++) {
				LazyDynaBean bean = new LazyDynaBean();
				String month = "";
				if(i == 13) {
					month = ResourceFactory.getProperty("label.gz.datasum");//总计
					bean.set("month", ResourceFactory.getProperty("label.gz.datasum"));
				}else {
					month = String.valueOf(i);
					bean.set("month", gzAnalysisUtil.getUpperMonth(i));
				}
				//设置列头
				if(i == 1) {
					list_column.add(getColumnInfo("month", ResourceFactory.getProperty("gz.acount.month"), "A"));
					list_column.add(getColumnInfo("avgperson", ResourceFactory.getProperty("jx.evaluation.jspZavg") + 
										ResourceFactory.getProperty("train.job.manNumber"), "N"));//平均人数
				}
				for (int j = 0; j < itemlist.size(); j++) {
					itembean = (LazyDynaBean) itemlist.get(j);
					String item = ((String) itembean.get("itemid")).toLowerCase();
					if (item.startsWith("avg"))
						continue;
					
					String before ="0.00";
					if(beforemap.get(month.toLowerCase()+ item)!=null)
						before=(String)beforemap.get(month.toLowerCase()+ item);
					if("".equals(before)|| "0".equals(before))
						before="0.00";
					bean.set(item + "before", GzAnalysisUtil.div(before, "1", 2));
					
					String ayear ="0.00";
					if(nowData.get(month.toLowerCase()+ item)!=null)
						ayear=(String)nowData.get(month.toLowerCase()+ item);
					bean.set(item + "year", ayear == null ? "0.00" : GzAnalysisUtil.div(ayear, "1", 2));
					String add = GzAnalyseBo.sub(ayear,before, 2);
					bean.set(item + "adde", add == null ? "0.00" : GzAnalysisUtil.div(add, "1", 2));
					if((before==null|| "".equalsIgnoreCase(before)||Float.parseFloat(before)==0))
					{
						bean.set(item + "addl","0.00");
					}
					else {
		    			bean.set(item + "addl", GzAnalysisUtil.div(""+(Double.parseDouble(GzAnalysisUtil.div(add, before, 4)) * 100), "1", 2));
					}
					String beforeavg = GzAnalysisUtil.div(before,(String) beforemap.get(month.toLowerCase()), 2);
					bean.set("avg" + item + "before", beforeavg == null ? "0.00": beforeavg);
					String yearavg = GzAnalysisUtil.div(ayear, (String) nowData.get(month.toLowerCase()), 2);
					bean.set("avg" + item + "year", yearavg == null ? "0.00": yearavg);
					String addavg = GzAnalyseBo.sub(yearavg == null ? "0.00": yearavg, beforeavg == null ? "00.00" : beforeavg, 2);
					bean.set("avg" + item + "adde", Double.parseDouble(addavg));
					
					if((beforeavg==null|| "".equalsIgnoreCase(beforeavg)||Float.parseFloat(beforeavg)==0)){
						bean.set("avg" + item + "addl","0.00");
					}
					else
					{
				    	String addeavg = ""+ Double.parseDouble(GzAnalysisUtil.div(addavg,beforeavg, 4)) * 100;
				    	bean.set("avg" + item + "addl", GzAnalysisUtil.div(addeavg, "1", 2));
					}
					//设置列头
					if(i == 1) {
						ArrayList<ColumnsInfo> list_child_column = new ArrayList<ColumnsInfo>();
						//上年
						list_child_column.add(getColumnInfo(item + "before", ResourceFactory.getProperty("gz.analysistable.lastYear"), "N"));
						//半年
						list_child_column.add(getColumnInfo(item + "year", ResourceFactory.getProperty("jx.khplan.currentyear"), "N"));
						//增长额
						list_child_column.add(getColumnInfo(item + "adde", ResourceFactory.getProperty("gz.analysistable.increase"), "N"));
						//增长率
						list_child_column.add(getColumnInfo(item + "addl", ResourceFactory.getProperty("gz.analysistable.growthRate"), "N"));
						ColumnsInfo par_col = getColumnInfo(item, (String) itembean.get("itemdesc"), "A");
						par_col.setChildColumns(list_child_column);
						list_column.add(par_col);
						
						ArrayList<ColumnsInfo> list_child_column_avg = new ArrayList<ColumnsInfo>();
						list_child_column_avg.add(getColumnInfo("avg" + item + "before", ResourceFactory.getProperty("gz.analysistable.lastYear"), "N"));
						list_child_column_avg.add(getColumnInfo("avg" + item + "year", ResourceFactory.getProperty("jx.khplan.currentyear"), "N"));
						list_child_column_avg.add(getColumnInfo("avg" + item + "adde", ResourceFactory.getProperty("gz.analysistable.increase"), "N"));
						list_child_column_avg.add(getColumnInfo("avg" + item + "addl", ResourceFactory.getProperty("gz.analysistable.growthRate"), "N"));
						//人均XXXX
						ColumnsInfo par_col_ = getColumnInfo("avg" + item, ResourceFactory.getProperty("gz.analysistable.perCapita") + (String) itembean.get("itemdesc"), "A");
						par_col_.setChildColumns(list_child_column_avg);
						list_column.add(par_col_);
					}
				}
				String item_sum = (String)nowData.get(month.toLowerCase() + "item_sum");
				String avg_item_sum = GzAnalysisUtil.div((String)nowData.get(month.toLowerCase() + "item_sum"),(String)nowData.get(month.toLowerCase()),2);
				//汇总合计
				bean.set("item_sum", StringUtils.isBlank(item_sum)?"0.00":item_sum);
				//人均汇总合计
				bean.set("avg_item_sum", StringUtils.isBlank(avg_item_sum)?"0.00":avg_item_sum);
				if(i == 1) {
					list_column.add(getColumnInfo("item_sum", ResourceFactory.getProperty("report_collect.collect") + 
										ResourceFactory.getProperty("gz.gz_acounting.total"), "N"));
					list_column.add(getColumnInfo("avg_item_sum", ResourceFactory.getProperty("gz.analysistable.perSum"), "N"));//平均人数
				}
				
				bean.set("avgperson", (String)(nowData.get(month.toLowerCase()) == null ? "0" : GzAnalysisUtil.div((String)nowData.get(month.toLowerCase()), "1", 0)));
				list_data.add(bean);
			}
			result.put("list_data", list_data);
			result.put("list_column", list_column);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return result;
	}
	
	/**
	 * 获取表格控件的config
	 * @param dataList
	 * @param columns
	 * @return
	 * @throws GeneralException
	 */
	@Override
    public String getTableConfig(ArrayList<LazyDynaBean> dataList, ArrayList<ColumnsInfo> columnList, String rsdtlid)
			throws GeneralException {
		String result = "";
		try {
			TableConfigBuilder builder = new TableConfigBuilder("gzAmountStructure_" + rsdtlid, columnList, "gzAmountStructure1_" + rsdtlid, this.userView, this.conn);
			builder.setDataList(dataList);//数据查询sql语句
			builder.setAutoRender(false);//是否自动渲染表格到页面
			builder.setPageSize(20);//每页条数
			builder.setSetScheme(false);
			builder.setSortable(false);
			builder.setScheme(true);
			builder.setSelectable(false);
			result = builder.createExtTableConfig();
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return result;
	}
	
	private ColumnsInfo getColumnInfo(String col_id, String col_desc, String column_type) {
		ColumnsInfo info = new ColumnsInfo();
		//平均人数不需要显示小数位
		int decimal = 0;
		if(!"avgperson".equalsIgnoreCase(col_id)) {
			decimal = 2;
		}else {
			info.setLocked(true);
			info.setColumnWidth(70);
		}
		if("month".equalsIgnoreCase(col_id)) {
			info.setColumnWidth(50);
			info.setLocked(true);
		}
		info.setColumnId(col_id);
		info.setColumnDesc(col_desc);
		info.setColumnType(column_type);
		info.setEditableValidFunc("false");//不可编辑
		info.setQueryable(true);
		if("N".equalsIgnoreCase(column_type)) {
			info.setDefaultValue("0");
			info.setDecimalWidth(decimal);
			info.setTextAlign("right");
		}
		FieldItem item = DataDictionary.getFieldItem(col_id);
		if(item != null) {
			info.setColumnDesc(item.getItemdesc());
			info.setCodesetId(item.getCodesetid());
			if("UM".equalsIgnoreCase(item.getCodesetid()) || "UM".equalsIgnoreCase(item.getCodesetid()) || "@K".equalsIgnoreCase(item.getCodesetid())) {
				info.setColumnWidth(160);
			}
		}
		return info;
	}
	
	@Override
    public String export_data(String rsid, String rsdtlid, ArrayList<ColumnsInfo> columns_list, ArrayList<LazyDynaBean> dataList, String tableName) {
		String fielname = "";
		try {
			GzAnalysisUtil gzAnalysisUtil = new GzAnalysisUtil(this.conn, this.userView);
			ReportParseVo reportVo = gzAnalysisUtil.analysePageSettingXml(rsid,rsdtlid);
			fielname = gzAnalysisUtil.exportExcel(tableName, reportVo, dataList, columns_list, false,false,null,"");
		}catch (Exception e) {
			e.printStackTrace();
		}
		return fielname;
	}
	
	/**
	 * 
	 * @param beforeYear
	 * @param itemlist
	 * @param nbases
	 * @param salaryids
	 * @param endmonth
	 * @param itemid
	 * @param itemvalue
	 * @param dbSql
	 * @param salarySql
	 * @return
	 */
	private HashMap getBeforeYearDataList(boolean selectAll, int year, ArrayList itemlist, String nbases, String salaryids, int endmonth, String fieldid,
			String itemvalue, String sum_sql, String privSql, String filed_sql, String verifying) {
		HashMap map = new HashMap();
		RowSet rs = null;
		GzAnalysisUtil gzAnalysisUtil = new GzAnalysisUtil(this.conn, this.userView);
		try {
			HashMap map_ = this.getSql(selectAll, year, endmonth, fieldid, privSql, itemvalue, sum_sql, filed_sql, verifying);
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search((String)map_.get("sql"), (ArrayList)map_.get("list"));
			int amonth = 0;
			while (rs.next()) {
				String month = rs.getString("amonth");
				String sum_all = "";
				for (int i = 0; i < itemlist.size(); i++) {
					LazyDynaBean bean = (LazyDynaBean) itemlist.get(i);
					String item = (String) bean.get("itemid");
					if (item.startsWith("avg"))
						continue;
					String sum=rs.getString(item);
					if(sum==null)
						sum="0.00";
					else
						sum = GzAnalysisUtil.div(sum,"1",2);
					
					sum_all = GzAnalyseBo.add(sum, sum_all, 2);
					map.put(month.toLowerCase() + item.toLowerCase(),sum);
				}
				map.put(month.toLowerCase() + "item_sum", sum_all);
				map.put(month, rs.getString("personNum") == null ? "" : rs.getString("personNum"));
				amonth++;
			}
			map.put("monthcount", String.valueOf(amonth));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return map;
	}
	
	private HashMap getSql(boolean selectAll, int year, int month, String fieldid, String privSql, String codevalue, String buf, 
							String filed_sql, String verifying) {
		HashMap map = new HashMap();
		StringBuffer sql_all = new StringBuffer();
		StringBuffer sql = new StringBuffer();
		ArrayList list = new ArrayList();
		sql_all.append("select count(distinct nbase"+Sql_switcher.concat()+"A0100) personNum,");
		sql_all.append("(case when " + Sql_switcher.month("a00z0") + " is null then '总计' else " + Sql_switcher.numberToChar(Sql_switcher.month("a00z0")) + " end)");
		sql_all.append(" amonth ");
		sql_all.append(buf);
		sql_all.append(" from (");
		sql.append("select nbase,A0100,a00z0" + filed_sql + " FROM salaryhistory WHERE ");
		sql.append(Sql_switcher.year("a00z0"));
		sql.append("=?");
		list.add(year);
		if (!selectAll && fieldid != null && !"".equals(fieldid) && codevalue != null&& !"".equals(codevalue)) {
			sql.append(" and ");
			sql.append(fieldid);
			sql.append(" like ?");
			list.add(codevalue+"%");
		}
		if (month > 0) {
			sql.append(" and ");
			sql.append(Sql_switcher.month("a00z0"));
			sql.append(" <= ?");
			list.add(month);
		}
		if(!"1".equals(verifying)) {
			sql.append(" and sp_flag=?");
			list.add("06");
		}
		if(StringUtils.isNotBlank(privSql)){
    		sql.append(privSql);
		}
		sql.append(" union all " + sql.toString().replace("salaryhistory", "salaryarchive"));
		list.addAll(list);
		sql_all.append(sql.toString() + ") sa ");
		sql_all.append(" group by ");
		if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
			sql_all.append(" rollup (" + Sql_switcher.month("a00z0") + ")");
		}else if(Sql_switcher.searchDbServer()==Constant.MSSQL) {			
			sql_all.append(Sql_switcher.month("a00z0") + " with rollup");
		}
		map.put("sql", sql_all.toString());
		map.put("list", list);
		return map;
	}
	
	private ArrayList getHeadItemList(String rsdtld) {
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		RowSet rs = null;
		LazyDynaBean bean = null;
		try {
			String sql = " select a.nwidth,a.align,a.itemdesc,a.itemid,a.itemfmt,b.itemtype,b.codesetid from reportitem a,fielditem b,"
					+ "(select distinct itemid from salaryset) s where upper(a.itemid)=upper(b.itemid) and upper(s.itemid)=upper(a.itemid) and "
					+ "upper(s.itemid)=upper(b.itemid) and a.rsdtlid=" + rsdtld + " order by a.sortid";
			rs = dao.search(sql);
			while (rs.next()) {
				bean = new LazyDynaBean();
				String itemtype = "A";
				String codesetid = "0";
				String itemfmt = "";
				if (rs.getString("itemtype") != null) {
					itemtype = rs.getString("itemtype");
					codesetid = rs.getString("codesetid");
				}
				
				if((this.userView != null && "0".equals(this.userView.analyseFieldPriv(rs.getString("itemid")))) || !"N".equals(itemtype))
					continue;
				
				bean.set("itemid", rs.getString("itemid"));
				bean.set("itemdesc", rs.getString("itemdesc"));
				bean.set("align", rs.getString("align"));
				bean.set("nwidth", rs.getString("nwidth"));
				if (rs.getString("itemfmt") != null)
					itemfmt = rs.getString("itemfmt");

				bean.set("itemtype", itemtype);
				bean.set("codesetid", codesetid);
				bean.set("itemfmt", itemfmt);
				list.add(bean);
				//加上平均值
				bean = new LazyDynaBean();
				bean.set("itemid", "avg" + rs.getString("itemid"));
				//人均XXX
				bean.set("itemdesc", ResourceFactory.getProperty("gz.analysistable.perCapita") + rs.getString("itemdesc"));
				bean.set("align", rs.getString("align"));
				bean.set("nwidth", rs.getString("nwidth"));
				String avgitemtype = "A";
				String avgcodesetid = "0";
				String avgitemfmt = "";
				if (rs.getString("itemfmt") != null)
					avgitemfmt = rs.getString("itemfmt");
				if (rs.getString("itemtype") != null) {
					avgitemtype = rs.getString("itemtype");
					avgcodesetid = rs.getString("codesetid");
				}

				if ("a00z0".equals(rs.getString("itemid").toLowerCase()))
					bean.set("itemtype", "D");
				else
					bean.set("itemtype", avgitemtype);
				bean.set("codesetid", avgcodesetid);
				bean.set("itemfmt", avgitemfmt);
				list.add(bean);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return list;
	}
}
