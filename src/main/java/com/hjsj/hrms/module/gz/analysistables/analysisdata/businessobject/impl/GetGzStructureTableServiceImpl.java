package com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.impl;

import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.GetGzStructureTableService;
import com.hjsj.hrms.module.gz.analysistables.util.GzAnalysisUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class GetGzStructureTableServiceImpl implements GetGzStructureTableService{
	private UserView userView;
	private Connection conn;
	private GzAnalysisUtil gasUtil;
	
	public GetGzStructureTableServiceImpl(UserView userView, Connection frameconn,GzAnalysisUtil gzAnalysisUtil) {
		this.userView = userView;
		this.conn = frameconn;
		this.gasUtil = gzAnalysisUtil;
	}

	@Override
    public HashMap<String, Object> findAllSalaryItem(String rsdtld, String salaryids, String fieldid, String codeitemid, String nbases, String verifying,
                                                     String year, int lay) throws GeneralException {
		HashMap<String, Object> result= new HashMap<String, Object>();
		StringBuffer sql_salaryid = new StringBuffer(" 1=2 ");//salaryid拼接sql
		ArrayList<String> list_salaryid = new ArrayList<String>();//salaryid集合
		
		String notShow = "'NBASE','A0100','A0000','A00Z2','A00Z3','A00Z0','A00Z1'";
		ArrayList<HashMap<String, String>> fieldid_list = new ArrayList<HashMap<String, String>>();//分析项目（数值）
		ArrayList<HashMap<String, String>> codeitemid_list = new ArrayList<HashMap<String, String>>();//分类项目（代码）
		ArrayList<HashMap<String, String>> levelSum_list = new ArrayList<HashMap<String, String>>();//层级汇总
		StringBuffer fieldid_s = new StringBuffer(",");//分析项目（数值）
		StringBuffer codeitemid_s = new StringBuffer(",");//分类项目（代码）
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		String itemtype = "";
		HashMap<String, String> map_temp = new HashMap<String, String>();
		StringBuffer sql_ = new StringBuffer();
		boolean lay_exist = false;
		try {
			salaryids = salaryids + ",";
			String[] salaryid_ = salaryids.split(",");
			for(int i = 0; i < salaryid_.length; i++) {
				if(StringUtils.isNotBlank(salaryid_[i])) {
					sql_salaryid.append(" or salaryid = ?");
					list_salaryid.add(salaryid_[i]);
				}
			}

			String sql = "select itemid,itemtype,itemdesc,codesetid from salaryset where (" + sql_salaryid + ") and upper(itemid) not in (" + 
							notShow + ")";
			rs = dao.search(sql, list_salaryid);
			while(rs.next()) {
				map_temp = new HashMap<String, String>();
				String itemid = rs.getString("itemid");
				if("0".equals(userView.analyseFieldPriv(itemid)))
					continue;
				map_temp.put("id", itemid.toLowerCase());
				map_temp.put("name", rs.getString("itemdesc"));
				
				itemtype = rs.getString("itemtype");
				//分类项
				if("N".equalsIgnoreCase(itemtype)) {
					fieldid_list.add(map_temp);
					fieldid_s.append(itemid.toLowerCase() + ",");
				//分析项
				}else if("A".equalsIgnoreCase(itemtype) && !"0".equals(rs.getString("codesetid")) && codeitemid_s.indexOf("," + itemid.toLowerCase() + ",") == -1) {
					codeitemid_list.add(map_temp);
					codeitemid_s.append(itemid.toLowerCase() + ",");
				}
			}
			if(fieldid_list.size() == 0 || codeitemid_list.size() == 0) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.analysistable.noFieldAuthority")));
			}
			String fieldid_temp = "";
			String codeitemid_temp = "";
			//获取应该显示的值
			if(fieldid_list.size() > 0) {
				fieldid_temp = (fieldid_s.toString().indexOf("," + fieldid + ",") > -1)?fieldid : (String)((HashMap<String, String>)fieldid_list.get(0)).get("id");
			}
			if(StringUtils.isBlank(codeitemid)) {
				//默认按照部门显示
				codeitemid_temp = "e0122";
			}else if(codeitemid_list.size() > 0) {
				codeitemid_temp = (codeitemid_s.toString().indexOf("," + codeitemid + ",") > -1)?codeitemid : (String)((HashMap<String, String>)codeitemid_list.get(0)).get("id");
			}
			result.put("fieldList", fieldid_list);//分析项集合 
			result.put("fieldid", fieldid_temp);
			result.put("codeItemList", codeitemid_list);//分类项集合
			result.put("codeitemid", codeitemid_temp);
			
			//计算级别
			String b_units=this.userView.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
			String privSql = "";
			if(StringUtils.isNotBlank(salaryids)) {
				//按照权限控制
				privSql=gasUtil.getPrivSQL("salaryhistory", nbases, salaryids, b_units, "");
			}
			FieldItem item_codeitem = DataDictionary.getFieldItem(codeitemid_temp);
			String codesetid = item_codeitem.getCodesetid();
			
			String table = "codeitem";
			if("UM".equalsIgnoreCase(codesetid) || "UN".equalsIgnoreCase(codesetid) || "@K".equalsIgnoreCase(codesetid)) {
				table = "organization";
			}
			sql_.append("select layer as layer from salaryhistory left join "+table+" org on salaryhistory." + codeitemid_temp + " = org.codeitemid where ");
			sql_.append(" org.codesetid = ? and org.layer <= 5");	
			//5.拼接最后的年份和group by
			sql_.append(" and " + Sql_switcher.year("A00Z0") + " = ?");
			
			list_salaryid.clear();
			list_salaryid.add(item_codeitem.getCodesetid());
			list_salaryid.add(year);
			if(!"1".equals(verifying)) {//不包含审批过程的
				sql_.append(" and sp_flag = ? ");
				list_salaryid.add("06");
			}
			sql_.append(privSql + " group by layer");
			//6.同时查salaryarchive
			sql_.append(" union all " + sql_.toString().replace("salaryhistory", "salaryarchive"));
			list_salaryid.addAll(list_salaryid);
			sql = "select layer from (" + sql_ + ") tem group by layer order by layer";
			rs = dao.search(sql, list_salaryid);
			while(rs.next()) {
				
				map_temp = new HashMap<String, String>();
				String layer = rs.getString("layer");
				map_temp.put("id", layer);
				map_temp.put("name", layer + ResourceFactory.getProperty("gz.analysistable.ji"));
				levelSum_list.add(map_temp);
				//只显示数据中有的层级
				if(Integer.parseInt(layer) == lay) {
					lay_exist = true;
				}
			}
			if(levelSum_list.size() > 0 && (lay == 0 || !lay_exist)) {
				result.put("lay", Integer.parseInt(((HashMap<String, String>)levelSum_list.get(0)).get("id")));
			}
			result.put("levelSum_list", levelSum_list);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return result;
	}
	
	@Override
    public HashMap getAllData(HashMap<String, Object> map_, boolean showNumberOfPeople, boolean collect, int lay, String salaryids,
                              String nbases, String verifying, String filterSql, String orderSql) throws GeneralException {
		HashMap result = new HashMap();
		ArrayList<LazyDynaBean> list_data= new ArrayList<LazyDynaBean>();
		ArrayList<ColumnsInfo> list_column = new ArrayList<ColumnsInfo>();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		String fieldid = (String) map_.get("fieldid");//分析项
		String codeitemid = (String) map_.get("codeitemid");//分类项
		String year = (String) map_.get("year");//年份
		String privSql = "";
		
		String order  = "asc";//标识
		String order_item = codeitemid;
		
		fieldid = StringUtils.isNotBlank(fieldid)?fieldid:"0";
		codeitemid = StringUtils.isNotBlank(codeitemid)?codeitemid:"0";
		ArrayList<String> sql_list_value = new ArrayList<String>();
		LazyDynaBean dataBean=new LazyDynaBean();//最后结果的
		LazyDynaBean dataBean_total=new LazyDynaBean();//总计用数据相加了，不查了，查询排序容易出错
		String b_units=this.userView.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
		
		//排序
		if(StringUtils.isNotBlank(orderSql)) {
			String temp_ = orderSql.split(" ")[0];
			if(!"rownum_".equalsIgnoreCase(temp_))
				order_item = orderSql.split(" ")[0];
			
			if(orderSql.toLowerCase().indexOf("desc") > -1) {
				order = "desc";
			}
			
		}
		
		//这个都兼容的，如果用rownum，和rollup一起用的时候可能有问题
		String order_ = order_item + " " + order;
		
		//拼接最外层的sql，算出每月的数据，还有人数
		String temp_sum_person="(sum(mc_1)+sum(mc_2)+sum(mc_3)+sum(mc_4)+sum(mc_5)+sum(mc_6)+sum(mc_7)+\r\n" + 
				"sum(mc_8)+sum(mc_9)+sum(mc_10)+sum(mc_11)+sum(mc_12))";
		//md:monthData（每月数据）
        //mc:monthCount（每月人数）
		StringBuffer sql_all = new StringBuffer("select nullif(" + codeitemid + ",'') " + codeitemid.toLowerCase() +","
					+ "sum(md_1) md_1,sum(md_2) md_2,sum(md_3) md_3,sum(md_4) md_4,sum(md_5) md_5,sum(md_6) md_6,sum(md_7) md_7,sum(md_8) md_8,sum(md_9) md_9,sum(md_10) md_10,"
					+ "sum(md_11) md_11,sum(md_12) md_12,"
					+ "sum(mc_1) mc_1,sum(mc_2) mc_2,sum(mc_3) mc_3,sum(mc_4) mc_4,sum(mc_5) mc_5,sum(mc_6) mc_6,sum(mc_7) mc_7,sum(mc_8) mc_8,sum(mc_9) mc_9,sum(mc_10) mc_10,"
					+ "sum(mc_11) mc_11,sum(mc_12) mc_12,"
					+ temp_sum_person + "/max(a00Z0) avg_person," + Sql_switcher.isnull("sum(" + fieldid + ")/sum(a00Z0)","0") + " avg_person_data,sum(sum_data) sum_data from ("
					+ "select sum(" + fieldid + ") " + fieldid + ",a0100,count(a0100) a0100_count,count(distinct A00Z0) a00Z0,"
					+ "(case when nullif(" + codeitemid + ",'') is null then '000000000' else nullif(" + codeitemid + ",'') end) " + codeitemid.toLowerCase());//因为归档和历史表，最外面加上select group by
		
		try {
			//序号
			//list_column.add(getColumnInfo("rownum_", ResourceFactory.getProperty("gz_new.gz_sort"), "A"));
			
			if(StringUtils.isNotBlank(salaryids)) {
				//按照权限控制
				privSql=gasUtil.getPrivSQL("salaryhistory", nbases, salaryids, b_units, "");
				list_column.add(getColumnInfo(codeitemid.toLowerCase(), "", "A"));
			}
			//1.先拼接处每月的总数据
			for(int i = 1; i < 13; i++) {
				sql_all.append("," + Sql_switcher.isnull("sum( case when " + Sql_switcher.month("A00Z0") + "=" + i + " then " + fieldid + " else 0 end)", "0") +
						" md_" + i);
				
				list_column.add(getColumnInfo("md_" + i, gasUtil.getUpperMonth(i), "N"));
				//2.是否显示人数，拼接出人数的列sql
					sql_all.append("," + Sql_switcher.isnull("max( case when " + Sql_switcher.month("A00Z0") + "=" + i + " then 1 else 0 end)", "0") + 
							"mc_" + i);
					
				ColumnsInfo col = getColumnInfo("mc_" + i, gasUtil.getUpperMonth(i) + ResourceFactory.getProperty("jx.param.empCount"), "N");
				if(showNumberOfPeople) {
					col.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
				}else {
					col.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
				}
				list_column.add(col);
			}
			
			//3.查出平均人数，人均值，合计
			sql_all.append("," + Sql_switcher.isnull("sum(" + fieldid + ")", "0") + " sum_data ");
			//平均人数
			list_column.add(getColumnInfo("avg_person", ResourceFactory.getProperty("jx.evaluation.jspZavg") + 
								ResourceFactory.getProperty("train.job.manNumber"), "N"));
			//人均值
			list_column.add(getColumnInfo("avg_person_data", ResourceFactory.getProperty("gz.analysistable.perCapita") + 
								ResourceFactory.getProperty("jx.param.value"), "N"));
			list_column.add(getColumnInfo("sum_data", ResourceFactory.getProperty("gz.gz_acounting.total"), "N"));
			
			//第一次进来的时候没有salaryids直接return
			if(StringUtils.isBlank(salaryids)) {
				result.put("list_data", list_data);
				result.put("list_column", list_column);
				return result;
			}
			
			String codesetid = DataDictionary.getFieldItem(codeitemid.toLowerCase()).getCodesetid();
			String codeitemid_tem = codeitemid;
			if(collect) {
				codeitemid_tem = getLayOrgSql(codesetid, lay).replace("XXXXXX", codeitemid);
			}
			StringBuffer sql = new StringBuffer("select nullif(" + codeitemid_tem + ",'') " + codeitemid.toLowerCase() + ",a0100,A00Z0," + (StringUtils.isNotBlank(fieldid)?fieldid:"'' temp") + " from salaryhistory");
			//4.如果按照层级显示。需要关联查询
			if(collect) {
				if("UM".equalsIgnoreCase(codesetid) || "UN".equalsIgnoreCase(codesetid) || "@K".equalsIgnoreCase(codesetid)) {
					sql.append(" left join organization org on salaryhistory." + codeitemid + " = org.codeitemid where ");
					sql.append(" org.codesetid = ? and org.layer >= ?");
				}else {
					sql.append(" left join codeitem cod on salaryhistory." + codeitemid + " = cod.codeitemid where ((cod.codesetid = ? and "
							+ "(cod.layer <= ? or cod.layer is null)) or " + codeitemid + " is null)");
				}
				sql_list_value.add(codesetid);
				sql_list_value.add(String.valueOf(lay));
				
				sql.append(" and ");
			}else {
				sql.append(" where ");
			}
			
			//5.拼接最后的年份和group by
			sql.append(Sql_switcher.year("A00Z0") + " = ?");
			sql_list_value.add(year);
			if(!"1".equals(verifying)) {//不包含审批过程的
				sql.append(" and sp_flag = ? ");
				sql_list_value.add("06");
			}
			sql.append(privSql);
			//6.同时查salaryarchive
			sql.append(" union all " + sql.toString().replace("salaryhistory", "salaryarchive"));
			sql_list_value.addAll(sql_list_value);
			
			sql_all.append(" from (" + sql.toString() + ") sa group by nullif(" + codeitemid + ",''),a0100)");
			
			sql_all.append(" sear where 1=1 " + filterSql + " group by  nullif(" + codeitemid + ",'')");
			sql_all.append(" order by " + order_);
			rs = dao.search(sql_all.toString(), sql_list_value);
			while(rs.next()) {
				dataBean = new LazyDynaBean();
				codeitemid = codeitemid.toLowerCase();
				//处理分类指标为单位或者部门的时候，显示按照系统管理设置的层级显示
				String value = rs.getString(codeitemid);
				//序号列暂时不要了
				//dataBean.set("rownum_", String.valueOf(++row));
				
				dataBean.set(codeitemid, value+"`"+gasUtil.getCodeName(codeitemid, value));
				
				for(int i = 1; i < 13; i++) {
					dataBean.set("md_" + i, rs.getString("md_" + i));
					dataBean_total.set("md_" + i, getTotal("md_" + i, rs.getString("md_" + i), dataBean_total));
				}
				//2.是否显示人数，拼接出人数的列sql
				if(showNumberOfPeople) {
					for(int i = 1; i < 13; i++) {
						dataBean.set("mc_" + i, rs.getString("mc_" + i));
						dataBean_total.set("mc_" + i, getTotal("mc_" + i, rs.getString("mc_" + i), dataBean_total));
					}
				}
				
				dataBean.set("avg_person", rs.getString("avg_person"));
				dataBean.set("avg_person_data", rs.getString("avg_person_data"));
				dataBean.set("sum_data", rs.getString("sum_data"));
				
				dataBean_total.set("avg_person", getTotal("avg_person", rs.getString("avg_person"), dataBean_total));
				dataBean_total.set("sum_data", getTotal("sum_data", rs.getString("sum_data"), dataBean_total));
				list_data.add(dataBean);
			}
			rs = dao.search("select count(*) count_ from (select a0100 from (" + sql.toString() + ") a group by a00z0,a0100) b", sql_list_value);
			int rows = 0;
			if(rs.next()) {
				rows = rs.getInt("count_");
			}
			// 平均数单独计算，通过总数/group by a00z0,a0100
			if(dataBean_total.get("sum_data") != null) {
				dataBean_total.set("avg_person_data", GzAnalysisUtil.div(String.valueOf(dataBean_total.get("sum_data")), rows + "", 2));
			}
			//总计
			//dataBean_total.set("rownum_", ResourceFactory.getProperty("label.gz.datasum"));
			dataBean_total.set(codeitemid, "`"+ResourceFactory.getProperty("label.gz.datasum"));
			list_data.add(dataBean_total);
			result.put("list_data", list_data);
			result.put("list_column", list_column);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return result;
	}
	
	/**
	 * 如果按照层级，获取该层级下的所有单位的值进行汇总
	 * @param codesetid
	 * @param layer
	 * @return
	 */
	private String getLayOrgSql(String codesetid, int layer) {
		StringBuffer result = new StringBuffer("(case");
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		String sql = "";
		try {
			if("UM".equalsIgnoreCase(codesetid) || "UN".equalsIgnoreCase(codesetid) || "@K".equalsIgnoreCase(codesetid)) {
				sql = "select codeitemid from organization where codesetid = ? and layer = ?";
			}else {
				sql = "select codeitemid from codeitem where codesetid = ? and layer = ?";
			}
			
			list.add(codesetid);
			list.add(layer);
			rs = dao.search(sql, list);
			while(rs.next()) {
				String codeitemid = rs.getString("codeitemid");
				result.append(" when XXXXXX like '" + codeitemid + "%' then '" + codeitemid + "'");
			}
			result.append(" else '' end)");
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return result.toString();
	}
	/**
	 * 获取表格控件的config
	 * @param dataList
	 * @param columns
	 * @return
	 * @throws GeneralException
	 */
	@Override
    public String getTableConfig(ArrayList<LazyDynaBean> dataList, ArrayList<ColumnsInfo> columnList)
			throws GeneralException {
		String result = "";
		try {
			TableConfigBuilder builder = new TableConfigBuilder("gzStructure", columnList, "GZ00000711", "gzStructure1", userView, conn);
			builder.setDataList(dataList);//数据查询sql语句
			builder.setAutoRender(false);//是否自动渲染表格到页面
			builder.setSelectable(false);//选框
			builder.setEditable(false);//表格编辑
			builder.setPageSize(20);//每页条数
			builder.setSetScheme(false);
			builder.setScheme(true);
			builder.setColumnFilter(true);
			result = builder.createExtTableConfig();
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return result;
	}
	
	private ColumnsInfo getColumnInfo(String col_id, String col_desc, String column_type) {
		ColumnsInfo info = new ColumnsInfo();
		info = new ColumnsInfo();
		info.setColumnId(col_id);
		info.setColumnDesc(col_desc);
		//info.setDefaultValue("0");
		info.setColumnType(column_type);
		info.setEditableValidFunc("false");//不可编辑
		info.setQueryable(true);
		if("N".equalsIgnoreCase(column_type)) {
			info.setTextAlign("right");
		}else {
			info.setTextAlign("left");
		}
		if("rownum_".equalsIgnoreCase(col_id)) {
			info.setColumnWidth(50);
			info.setLocked(true);
		}
		if("rownum_".equalsIgnoreCase(col_id) || col_id.startsWith("mc") || "avg_person".equalsIgnoreCase(col_id)) {
			info.setDecimalWidth(0);
		}else {
			info.setDecimalWidth(2);
		}
		FieldItem item = DataDictionary.getFieldItem(col_id);
		if(item != null) {
			info.setCodesetId(item.getCodesetid());
			info.setColumnDesc(item.getItemdesc());
			info.setColumnWidth(160);
			info.setLocked(true);
			if("UM".equalsIgnoreCase(item.getCodesetid()) || "UM".equalsIgnoreCase(item.getCodesetid()) || "@K".equalsIgnoreCase(item.getCodesetid())) {
				info.setCtrltype("3");
				info.setNmodule("1");
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
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fielname;
	}

	@Override
    public ArrayList<HashMap> getSalarySetList(String queryText) throws GeneralException {

		ArrayList<HashMap> list=new ArrayList<HashMap> ();
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet salarytemplateRs = null; 
		try {
			StringBuffer buf = new StringBuffer();
			ArrayList<String> sqlList = new ArrayList<String>();
		 	buf.append("select salaryid,cname,cbase,seq,cond from salarytemplate where (cstate is null or cstate='')"); 
		 	String[] values=queryText.split(",");
			// 快速查询
		 	StringBuffer strbuf = new StringBuffer();
			for(int i = 0; i < values.length; i++){
				String queryVal = values[i];
				if(i == 0){
					strbuf.append(" and ( ");
				}else{
					strbuf.append(" or ");
				}
				strbuf.append("(salaryid=?)");
				sqlList.add(queryVal);
			}
			if(strbuf.length() > 0){
				strbuf.append(")");
			}
			buf.append(strbuf.toString());
			buf.append(" order by seq");
			salarytemplateRs = dao.search(buf.toString(), sqlList);
			HashMap map=null;
		    while(salarytemplateRs.next()){
				// 加上权限过滤
				if (!userView.isHaveResource(IResourceConstant.GZ_SET, salarytemplateRs.getString("salaryid")))
					continue;

				map=new HashMap();
				map.put("salaryid", salarytemplateRs.getString("salaryid"));
				map.put("cname", salarytemplateRs.getString("cname"));
				list.add(map);
		   }
		}catch (Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			PubFunc.closeDbObj(salarytemplateRs);
		}
		return list;
	}
	
	private String getTotal(String key, String value, LazyDynaBean ldb) throws GeneralException {
		String val = "";
		String key_value = "";
		try {
			if(ldb != null) {
				if(ldb.get(key) == null) {
					key_value = "0";
				}else {
					key_value = (String) ldb.get(key);
				}
				BigDecimal ori_v = new BigDecimal(key_value);
		        BigDecimal now_v = new BigDecimal(value);
		        val = String.valueOf(ori_v.add(now_v));//
			}
		}catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return val;
	}
}
