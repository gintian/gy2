package com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.impl;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.GetGzItemSummaryTableService;
import com.hjsj.hrms.module.gz.analysistables.util.GzAnalysisUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.DateStyle;
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

public class GetGzItemSummaryTableServiceImpl implements GetGzItemSummaryTableService{
	private UserView userView;
	private Connection conn;
	
	public GetGzItemSummaryTableServiceImpl(UserView userView, Connection frameconn) {
		this.userView = userView;
		this.conn = frameconn;
	}

	/**
	 * 获取所有的数据集合
	 * @param result 年份，薪资类别，分析项，分类项
	 * @param showNumberOfPeople 是否显示每月人数
	 * @param collect 是否按层级汇总 
	 * @param lay 层级值
	 * @return
	 * @throws GeneralException
	 */
	@Override
    public HashMap getAllData(HashMap map_data, String salaryids, String nbases, String verifying, int limit, int page, String filterSql, String orderSql) throws GeneralException {
		
		String year = (String) map_data.get("year"); 
		String month = (String) map_data.get("month");  
		String fromYear = (String) map_data.get("fromYear");  
		String endYear = (String) map_data.get("endYear");  
		boolean appointtime = (Boolean) map_data.get("appointtime"); 
		
		String rsdtld = (String) map_data.get("not_enc_rsdtlid"); 
		HashMap result = new HashMap();
		StringBuffer sql_all = new StringBuffer("select b0110," + Sql_switcher.isnull("e0122", "''") + " e0122,count(distinct a0100) count_person");
		StringBuffer sql = new StringBuffer("select a0100,b0110,e0122");
		GzAnalysisUtil gzAnalysisUtil = new GzAnalysisUtil(this.conn, this.userView);
		ArrayList<LazyDynaBean> list_data= new ArrayList<LazyDynaBean>();
		ArrayList<ColumnsInfo> list_column = new ArrayList<ColumnsInfo>();
		ArrayList<String> list_tem = new ArrayList<String>();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String b_units=this.userView.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
			String privSql=gzAnalysisUtil.getPrivSQL("salaryhistory", nbases, salaryids, b_units, "");
			
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			//序号
			list_column.add(getColumnInfo("rownum_", ResourceFactory.getProperty("gz.bankdisk.sequencenumber"), "N", 0));
			//单位
			ColumnsInfo b0110_ = getColumnInfo("b0110", ResourceFactory.getProperty("columns.archive.unit"), "A", 0);
			b0110_.setDoFilterOnLoad(true);//显示出单位树
			list_column.add(b0110_);
			//部门
			list_column.add(getColumnInfo("e0122", ResourceFactory.getProperty("columns.archive.um"), "A", 0));
			//人数
			list_column.add(getColumnInfo("count_person", ResourceFactory.getProperty("jx.param.empCount"), "N", 0));
			//获取应该显示的字段，拼接
			ArrayList list = getHeadItemList(rsdtld);
			for(int i = 0; i < list.size(); i++) {
				LazyDynaBean map = (LazyDynaBean) list.get(i);
				String itemid = (String) map.get("itemid");
				String col_desc = (String) map.get("itemdesc");
				String column_type = (String) map.get("itemtype");
				int decimalwidth = (Integer) map.get("decimalwidth");
				sql_all.append(",sum(" + Sql_switcher.isnull(itemid, "0") + ") " + itemid);
				sql.append("," + itemid);
				list_column.add(getColumnInfo(itemid, col_desc, column_type, decimalwidth));
			}
			
			sql.append(" from salaryhistory ");
			sql.append(" where 1=1 ");
			//是否包含审批过程
			if(!"1".equals(verifying)) {
				sql.append(" and sp_flag = ?");
				list_tem.add("06");
			}
			//如果是勾选统计区间，按照开始和截止日期判断
			if(appointtime) {
				if(StringUtils.isNotBlank(year)) {
					sql.append(" and " + Sql_switcher.year("A00Z0") + " = ?");
					list_tem.add(year);
				}
				if(StringUtils.isNotBlank(month)) {
					sql.append(" and " + Sql_switcher.month("A00Z0") + " = ?");
					list_tem.add(month);
				}
			}else if(!appointtime) {
				
				if(StringUtils.isNotBlank(fromYear) && StringUtils.isNotBlank(endYear) && DateStyle.parseDate(fromYear) != null && DateStyle.parseDate(endYear) != null) {
					sql.append(" and A00Z0 between " + Sql_switcher.dateValue(fromYear) + " and " + Sql_switcher.dateValue(endYear));
				}else if(StringUtils.isNotBlank(fromYear) && DateStyle.parseDate(fromYear) != null) {
					sql.append(" and A00Z0 > " + Sql_switcher.dateValue(fromYear));
				}else if(StringUtils.isNotBlank(endYear) && DateStyle.parseDate(endYear) != null) {
					sql.append(" and A00Z0 < " + Sql_switcher.dateValue(endYear));
				}
			}
			sql.append(privSql+filterSql);
			sql.append(" union all " + sql.toString().replace("salaryhistory", "salaryarchive"));
			list_tem.addAll(list_tem);
			
			sql_all.append(" from (" + sql.toString() + ") dat ");
			sql_all.append(" group by b0110," + Sql_switcher.isnull("e0122", "''"));
			String sql_excute = sql_all.toString();
			if(StringUtils.isBlank(orderSql)) {
				orderSql = "b0110,e0122";
			}
			sql_excute = "select * from (" + sql_all + ") aa order by " + orderSql;
			rs = dao.search(sql_excute, list_tem);
			while(rs.next()) {
				LazyDynaBean dataBean = new LazyDynaBean();
				String b0110 = rs.getString("b0110");
				String e0122 = rs.getString("e0122");
				dataBean.set("rownum_", rs.getRow());
				
				dataBean.set("b0110", b0110+"`"+gzAnalysisUtil.getCodeName("b0110", b0110));
				dataBean.set("e0122", e0122+"`"+gzAnalysisUtil.getCodeName("e0122", e0122));
				dataBean.set("count_person", rs.getString("count_person"));
				for(int i = 0; i < list.size(); i++) {
					LazyDynaBean map = (LazyDynaBean) list.get(i);
					String itemid = (String) map.get("itemid");
					dataBean.set(itemid.toLowerCase(), rs.getString(itemid));
				}
				list_data.add(dataBean);
			}
			
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
			TableConfigBuilder builder = new TableConfigBuilder("GzItemSummary_" + rsdtlid, columnList, "GZ00000713", 
					"GzItemSummary1_" + rsdtlid, userView, conn);
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
	
	private ColumnsInfo getColumnInfo(String col_id, String col_desc, String column_type, int decimalWidth) {
		ColumnsInfo info = new ColumnsInfo();
		info = new ColumnsInfo();
		info.setColumnId(col_id);
		info.setColumnDesc(col_desc);
		info.setDefaultValue("0");
		info.setColumnType(column_type);
		info.setEditableValidFunc("false");//不可编辑
		info.setDecimalWidth(decimalWidth);
		info.setQueryable(true);
		info.setOrdertype("0");
		info.setTextAlign("right");
		FieldItem item = DataDictionary.getFieldItem(col_id);
		if("rownum_".equalsIgnoreCase(col_id)) {
			info.setColumnWidth(50);
			info.setLocked(true);
		}
		if("count_person".equalsIgnoreCase(col_id)) {
			info.setLocked(true);
			info.setColumnWidth(60);
		}
		if(item != null) {
			info.setColumnDesc(item.getItemdesc());
			info.setCodesetId(item.getCodesetid());
			if("UN".equalsIgnoreCase(item.getCodesetid()) || "UM".equalsIgnoreCase(item.getCodesetid()) || "@K".equalsIgnoreCase(item.getCodesetid())) {
				info.setColumnWidth(160);
				info.setLocked(true);
				info.setCtrltype("3");
				info.setNmodule("1");
			}
			if("N".equalsIgnoreCase(item.getItemtype()))
				info.setTextAlign("right");
			else
				info.setTextAlign("left");
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
	 * 根据reportitem获取选择的统计指标
	 * @param rsdtld
	 * @return
	 */
	private ArrayList getHeadItemList(String rsdtld) {
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		RowSet rs = null;
		LazyDynaBean bean = null;
		try {
			String sql = " select a.align,a.itemdesc,a.itemid,a.itemfmt,b.itemtype,b.codesetid,b.decimalwidth from reportitem a,fielditem b,"
					+ "(select distinct itemid from salaryset) s where upper(a.itemid)=upper(b.itemid) and upper(s.itemid)=upper(a.itemid) and "
					+ "upper(s.itemid)=upper(b.itemid) and a.rsdtlid="
					+ rsdtld + " and b.itemtype = 'N' order by a.sortid";
			rs = dao.search(sql);
			while (rs.next()) {
				bean = new LazyDynaBean();
				String itemtype = "A";
				String codesetid = "0";
				if (rs.getString("itemtype") != null) {
					itemtype = rs.getString("itemtype");
					codesetid = rs.getString("codesetid");
				}
				
				if((this.userView != null && "0".equals(this.userView.analyseFieldPriv(rs.getString("itemid")))))
					continue;
				
				bean.set("itemid", rs.getString("itemid"));
				bean.set("itemdesc", rs.getString("itemdesc"));
				bean.set("align", rs.getString("align"));
				bean.set("decimalwidth", rs.getInt("decimalwidth"));
				bean.set("itemtype", itemtype);
				bean.set("codesetid", codesetid);
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
