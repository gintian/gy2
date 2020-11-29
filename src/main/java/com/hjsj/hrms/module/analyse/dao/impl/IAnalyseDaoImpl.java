package com.hjsj.hrms.module.analyse.dao.impl;

import com.hjsj.hrms.module.analyse.dao.IAnalyseDao;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计分析数据库接口实现类
 * @author wangbo
 * @category hjsj 2109-12-16
 * @version 1.0
 */
public class IAnalyseDaoImpl implements IAnalyseDao{

	private ContentDAO dao;
	private Connection conn;
	private static Category log = Category.getInstance(IAnalyseDaoImpl.class.getName());
	
	public IAnalyseDaoImpl(Connection conn){
		this.conn = conn;
		dao = new ContentDAO(conn);
	}
	
	@Override
	public List<Map> listZhanBiViewData(String viewTable, String items, String b0110, String year)
			throws GeneralException {
		List<Map> dataList = new ArrayList<Map>();
		StringBuffer sql = new StringBuffer();
		ArrayList list = new ArrayList();
		RowSet rs = null;
		String[] valueFields = items.split(","); 
		sql.append("select b0110,name,sum("+ valueFields[0] +") "+valueFields[0]+",sum("+ valueFields[1] +")/12 "+valueFields[1] +" from "+ viewTable +" ");
		sql.append(" where 1=1 ");
		sql.append(" and "+ Sql_switcher.dateToChar("yearmark", "yyyy") +"=? ");
		
		String new_year = "";
		if(StringUtils.isNotBlank(year)){
			new_year =  year;
		}else{
			new_year = (String) this.listViewYear(viewTable, b0110).get(0);
		}
		list.add(new_year);
		
		sql.append(" and ( "+ Sql_switcher.dateToChar("yearmark", "mm") +">=? and "+ Sql_switcher.dateToChar("yearmark", "mm") +"<=? ) ");
		String month = this.getMinAndMaxMonth(viewTable, b0110, new_year);
		list.add(month.split(",")[0]);
		list.add(month.split(",")[1]);
		
		if(StringUtils.isNotBlank(b0110)){
			sql.append(" and b0110 in (");
			String[] b0110s = b0110.split(",");
			for (int i = 0; i < b0110s.length; i++) {
				sql.append(" ?,");
				list.add(b0110s[i]);
			}
			sql.setLength(sql.length()-1);
			sql.append(") ");
		}
		sql.append(" GROUP BY b0110,name ");
		sql.append(" order by b0110 ");
		try {
			rs = dao.search(sql.toString(), list);
			while(rs.next()){
				HashMap map = new HashMap();
				map.put("b0110", PubFunc.encrypt(rs.getString("b0110")));
				map.put("itemname", rs.getString("name"));
				BigDecimal value_zx = new BigDecimal(rs.getDouble(valueFields[0])).setScale(2, BigDecimal.ROUND_HALF_UP);
				map.put(valueFields[0],value_zx);
				BigDecimal value_pf = new BigDecimal(rs.getDouble(valueFields[1])).setScale(2, BigDecimal.ROUND_HALF_UP);
				map.put(valueFields[1],value_pf);
				dataList.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//获取占比统计数据出错
			log.error("/module/selfservice/resource_zh_CN.js--->analyse.error.getZhanBiDataError");
			log.error("-----zhanbi--view---->"+viewTable);
			log.error("-----------sql------->"+sql);
			throw new GeneralException("analyse.error.getZhanBiDataError");
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return dataList;
	}


	@Override
	public List listViewYear(String viewTable, String b0110) throws GeneralException {
		StringBuffer sql = new StringBuffer();
		ArrayList list = new ArrayList();
		ArrayList yearList = new ArrayList();
		RowSet rs = null;
		sql.append(this.getYearSql(viewTable, b0110));
		if(StringUtils.isNotBlank(b0110)){
			String[] b0110s = b0110.split(",");
			for (int i = 0; i < b0110s.length; i++) {
				list.add(b0110s[i]);
			}
		}
		try {
			rs = dao.search(sql.toString(), list);
			while(rs.next()){
				yearList.add(rs.getString("year"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//获取统计年份出错
			log.error("/module/selfservice/resource_zh_CN.js--->analyse.error.getViewYearError");
			log.error("-------view---->"+viewTable);
			log.error("-------sql------->"+sql);
			throw new GeneralException("analyse.error.getViewYearError");
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return yearList;
	}
	
	@Override
	public List<Map> listTongBiViewData(String viewTable, String items, String b0110, String year)
			throws GeneralException {
		List dataList = new ArrayList();
		List<String> orgList = new ArrayList<String>();
		Map dataHM = new HashMap();
		StringBuffer sql = new StringBuffer();
		ArrayList list = new ArrayList();
		RowSet rs = null;
		
		sql.append("select b0110,name,"+ Sql_switcher.dateToChar("yearmark", "yyyy")+" year,sum("+items+") "+ items +" from "+ viewTable +" ");
		sql.append(" where 1=1 ");
		sql.append(" and ("+ Sql_switcher.dateToChar("yearmark","yyyy") +"=? or "+ Sql_switcher.dateToChar("yearmark","yyyy") +"=?) ");
		
		String new_year = "";
		if(StringUtils.isNotBlank(year)){
			new_year = year;
		}else{
			new_year = (String) this.listViewYear(viewTable, b0110).get(0);
		}
		list.add(new_year);
		list.add(""+(Integer.parseInt(new_year)-1));
		
		sql.append(" and ( "+ Sql_switcher.dateToChar("yearmark", "mm") +">=? and "+ Sql_switcher.dateToChar("yearmark", "mm") +"<=? ) ");
		String month = this.getMinAndMaxMonth(viewTable, b0110, new_year);
		list.add(month.split(",")[0]);
		list.add(month.split(",")[1]);
		
		
		if(StringUtils.isNotBlank(b0110)){
			sql.append(" and b0110 in (");
			String[] b0110s = b0110.split(",");
			for (int i = 0; i < b0110s.length; i++) {
				sql.append("?,");
				list.add(b0110s[i]);
			}
			sql.setLength(sql.length()-1);
			sql.append(") ");
		}
		sql.append(" GROUP BY b0110,name,to_char(yearmark,'yyyy') ");
		sql.append(" order by year ,b0110 ");
		try {
			rs = dao.search(sql.toString(), list);
			while(rs.next()){
				HashMap map = null;
				ArrayList chartDataList = null;
				HashMap chartDataHM = null;
				String b0110_e = PubFunc.encrypt(rs.getString("b0110"));
				if(dataHM.containsKey(b0110_e)){
					map = (HashMap) dataHM.get(b0110_e);
					chartDataList = (ArrayList) map.get("dataList");
					chartDataHM = new HashMap();
					chartDataHM.put("year", rs.getString("year"));
					chartDataHM.put("value", rs.getDouble(items));
					chartDataList.add(chartDataHM);
					
				}else{
					map = new HashMap();
					map.put("b0110", b0110_e);
					chartDataList = new ArrayList();
					chartDataHM = new HashMap();
					map.put("itemname", rs.getString("name"));
					
					chartDataHM.put("year", rs.getString("year"));
					chartDataHM.put("value", rs.getDouble(items));
					chartDataList.add(chartDataHM);
					
					map.put("dataList",chartDataList);
					dataHM.put(b0110_e, map);
					orgList.add(b0110_e);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//获取同比统计图数据出错
			log.error("/module/selfservice/resource_zh_CN.js--->analyse.error.getTongBiViewDataError");
			log.error("----tongbi---view---->"+viewTable);
			log.error("-----------sql------->"+sql);
			throw new GeneralException("analyse.error.getTongBiViewDataError");
		}finally{
			PubFunc.closeDbObj(rs);
		}
		for (int i = 0; i < orgList.size(); i++) {
			dataList.add(dataHM.get(orgList.get(i)));
		}
		
		return dataList;
	}
	
	/**
	 * 获取年份集合sql语句
	 * @param viewTable 数据视图表名
	 * @param b0110  xxx,xxx 机构id编号
	 * @return
	 */
	private String getYearSql(String viewTable,String b0110){
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct "+ Sql_switcher.dateToChar("yearmark", "yyyy") +" year from "+ viewTable +" ");
		sql.append(" where 1=1 ");
		if(StringUtils.isNotBlank(b0110)){
			sql.append(" and b0110 in (");
			String[] b0110s = b0110.split(",");
			for (int i = 0; i < b0110s.length; i++) {
				sql.append(" ?,");
			}
			sql.setLength(sql.length()-1);
			sql.append(") ");
		}
		sql.append(" order by year desc ");
		return sql.toString();
	}
	
	/**
	 * 最近年度是今年，且没有达到12个月
	 * @param viewTable 视图表名
	 * @param b0110  机构编号id xxxx,xxxx
	 * @param year  年度
	 * @return
	 * @throws GeneralException 
	 */
	private String getMinAndMaxMonth(String viewTable,String b0110,String year) throws GeneralException{
		StringBuffer sql = new StringBuffer();
		ArrayList list = new ArrayList();
		RowSet rs = null;
		String month = "";
		sql.append("select min(").append(Sql_switcher.dateToChar("yearmark", "mm"));
		sql.append(") minyear,max(").append(Sql_switcher.dateToChar("yearmark", "mm")).append(") maxyear from ").append(viewTable).append(" ");
		sql.append(" where 1=1 ");
		sql.append(" and "+ Sql_switcher.dateToChar("yearmark", "yyyy") +"=? ");
		list.add(year);
		if(StringUtils.isNotBlank(b0110)){
			sql.append(" and b0110 in (");
			String[] b0110s = b0110.split(",");
			for (int i = 0; i < b0110s.length; i++) {
				sql.append("?,");
				list.add(b0110s[i]);
			}
			sql.setLength(sql.length()-1);
			sql.append(" ) ");
		}
		try {
			rs = dao.search(sql.toString(), list);
			if(rs.next()){
				String min = rs.getString("minyear");
				String max = rs.getString("maxyear");
				month = min+","+max;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//获取年份对应最小最大月份出错
			e.printStackTrace();
			log.error("/module/selfservice/resource_zh_CN.js--->analyse.error.getViewMonthError");
			log.error("-------view---->"+viewTable);
			log.error("-----sql------->"+sql);
			throw new GeneralException("analyse.error.getViewMonthError");
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return month;
	}
	


	@Override
	public List<Map> listPingJunViewData(String viewTable, String b0110, String year) throws GeneralException {
		HashMap dataHM = new HashMap();
		List<Map> dataList = new ArrayList<Map>();
		ArrayList itemList = new ArrayList();
		RowSet rs = null;
		try {
			rs = this.getPingJunSql(viewTable, b0110, year);
			while (rs.next()){
				HashMap map = new HashMap();
				HashMap itemHM = new HashMap();
				ArrayList list = new ArrayList();
				String itemid = rs.getString("itemid");
				if(dataHM.containsKey(itemid)){
					map = (HashMap) dataHM.get(itemid);
					list = (ArrayList) map.get("dataList");
					itemHM.put("name", rs.getString("year"));
					int num = rs.getInt("num");
					if(num > 0){
						double value = Double.parseDouble(String.valueOf(rs.getObject("totalValue")))/num;
						itemHM.put("value", new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP));
					}else{
						itemHM.put("value", 0);
					}
					list.add(itemHM);
				}else{
					map.put("itemid", itemid);
					map.put("itemname",rs.getString("itemname"));
					
					itemHM.put("name", rs.getString("year"));
					int num = rs.getInt("num");
					if(num > 0){
						double value = Double.parseDouble(String.valueOf(rs.getObject("totalValue")))/num;
						itemHM.put("value", new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP));
					}else{
						itemHM.put("value", 0);
					}
					list.add(itemHM);
					map.put("dataList", list);
					
					dataHM.put(itemid, map);
					itemList.add(itemid);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//获取平均or累计统计数据出错
			log.error("/module/selfservice/resource_zh_CN.js--->analyse.error.getPingJunViewDataError");
			throw new GeneralException("analyse.error.getPingJunViewDataError");
		} finally {
			PubFunc.closeDbObj(rs);
		}
		for (int i = 0; i < itemList.size(); i++) {
			dataList.add((Map)dataHM.get(itemList.get(i)));
		}
		return dataList;
	}

	@Override
	public Map listLeiJiViewData(String viewTable, String b0110, String year) throws GeneralException {
		Map map = new HashMap();
		List<Map> dataList = new ArrayList<Map>();
		RowSet rs = null;
		double sum = 0;
		try {
			rs = this.getLeiJiSql(viewTable, b0110, year);
			while (rs.next()){
				HashMap itemHM = new HashMap();
				String itemid = rs.getString("itemid");

				itemHM.put("itemid", itemid);
				itemHM.put("name",rs.getString("itemname"));
				BigDecimal bigDecimalValue = new BigDecimal(Double.parseDouble(String.valueOf(rs.getObject("totalValue")))).setScale(2, BigDecimal.ROUND_HALF_UP);
				itemHM.put("value", bigDecimalValue);

				dataList.add(itemHM);
				sum = sum + bigDecimalValue.doubleValue();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//获取平均or累计统计数据出错
			log.error("/module/selfservice/resource_zh_CN.js--->analyse.error.getLeiJiViewDataError");
			throw new GeneralException("analyse.error.getLeiJiViewDataError");
		} finally {
			PubFunc.closeDbObj(rs);
		}
		map.put("sum", sum);
		map.put("data", dataList);
		return map;
	}
	
	@Override
	public List<Map> listMoreItemAndMoreTypeViewData(String viewTable, String b0110, String year)
			throws GeneralException {
		StringBuffer sql = new StringBuffer();
		Map dataHM = new HashMap();
		List<Map> dataList = new ArrayList<Map>();
		ArrayList list = new ArrayList();
		RowSet rs = null;
		
		List fieldList = this.getMoreItemAndMoreTypeFieldItem(viewTable);
		
		sql.append("select vflag");
		for (int i = 0; i < fieldList.size(); i++) {
			Map map = new HashMap();
			map.put("itemname", fieldList.get(i));
			map.put("dataList", new ArrayList());
			dataHM.put(fieldList.get(i), map);
			sql.append(",sum("+ fieldList.get(i) +") "+ fieldList.get(i)+" ");
		}
		
		sql.append(" from "+ viewTable + " ");
		sql.append(" where 1=1 ");
		sql.append(" and "+ Sql_switcher.dateToChar("yearmark","yyyy") +"=? ");
		String new_year = "";
		if(StringUtils.isNotBlank(year)){
			new_year = year;
		}else{
			new_year = (String) this.listViewYear(viewTable, b0110).get(0);
		}
		list.add(new_year);
//		sql.append(" and ( "+ Sql_switcher.dateToChar("yearmark", "mm") +">=? and "+ Sql_switcher.dateToChar("yearmark", "mm") +"<=? ) ");
//		String month = this.getMinAndMaxMonth(viewTable, b0110, new_year);
//		list.add(month.split(",")[0]);
//		list.add(month.split(",")[1]);
		
		if(StringUtils.isNotBlank(b0110)){
			sql.append(" and b0110 in (");
			String[] b0110s = b0110.split(",");
			for (int i = 0; i < b0110s.length; i++) {
				sql.append("?,");
				list.add(b0110s[i]);
			}
			sql.setLength(sql.length()-1);
			sql.append(") ");
		}
		sql.append(" group by vflag order by vflag");
		try {
			rs = dao.search(sql.toString(), list);
			while(rs.next()){
				for (int i = 0; i < fieldList.size(); i++) {
					HashMap map = new HashMap();
					map.put("name", rs.getString("vflag"));
					map.put("value",  new BigDecimal(Double.parseDouble(String.valueOf(rs.getObject((String)fieldList.get(i))))).setScale(2, BigDecimal.ROUND_HALF_UP));
					HashMap itemHM = (HashMap) dataHM.get(fieldList.get(i));
					((ArrayList)itemHM.get("dataList")).add(map);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//获取多项多分类统计出错
			log.error("/module/selfservice/resource_zh_CN.js--->analyse.error.getMoreItemAndMoreTypeViewDataError");
			log.error("----item and type---view---->"+viewTable);
			log.error("-----------sql-------------->"+sql);
			throw new GeneralException("analyse.error.getMoreItemAndMoreTypeViewDataError");
		} finally {
			PubFunc.closeDbObj(rs);
		}
		for (int i = 0; i <fieldList.size(); i++) {
			dataList.add((Map)dataHM.get((String)fieldList.get(i)));
		}
		return dataList;
	}
		
	/**
	 * 获取平均的sql语句
	 * @param viewTable
	 * @param b0110
	 * @param year
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private RowSet getPingJunSql(String viewTable, String b0110, String year) throws GeneralException, SQLException{
		List dataList = new ArrayList();
		List<String> orgList = new ArrayList<String>();
		Map dataHM = new HashMap();
		StringBuffer sql = new StringBuffer();
		ArrayList list = new ArrayList();
		RowSet rs = null;

		sql.append("select "+ Sql_switcher.dateToChar("yearmark", "yyyy")+" year,itemid,itemname,sum(value) totalValue,sum(num) num from "+ viewTable +" ");
		sql.append(" where 1=1 ");
		sql.append(" and ("+ Sql_switcher.dateToChar("yearmark","yyyy") +"=? or "+ Sql_switcher.dateToChar("yearmark","yyyy") +"=?) ");
		
		String new_year = "";
		if(StringUtils.isNotBlank(year)){
			new_year = year;
		}else{
			new_year = (String) this.listViewYear(viewTable, b0110).get(0);
		}
		list.add(new_year);
		list.add(""+(Integer.parseInt(new_year)-1));
		
		sql.append(" and ( "+ Sql_switcher.dateToChar("yearmark", "mm") +">=? and "+ Sql_switcher.dateToChar("yearmark", "mm") +"<=? ) ");
		String month = this.getMinAndMaxMonth(viewTable, b0110, new_year);
		list.add(month.split(",")[0]);
		list.add(month.split(",")[1]);

		
		if(StringUtils.isNotBlank(b0110)){
			sql.append(" and b0110 in (");
			String[] b0110s = b0110.split(",");
			for (int i = 0; i < b0110s.length; i++) {
				sql.append("?,");
				list.add(b0110s[i]);
			}
			sql.setLength(sql.length()-1);
			sql.append(") ");
		}
		sql.append("group by itemid,to_char(yearmark, 'yyyy'),itemname order by year ,itemid ");
		
		log.debug("----getPingJunSql---->"+sql);
		rs = dao.search(sql.toString(), list);
		return rs;
	}
	
	/**
	 * 获取累计的sql语句
	 * @param viewTable
	 * @param b0110
	 * @param year
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private RowSet getLeiJiSql(String viewTable, String b0110, String year) throws GeneralException, SQLException{
		List dataList = new ArrayList();
		List<String> orgList = new ArrayList<String>();
		Map dataHM = new HashMap();
		StringBuffer sql = new StringBuffer();
		ArrayList list = new ArrayList();
		RowSet rs = null;

		sql.append("select itemid,itemname,sum(value) totalValue from "+ viewTable +" ");
		sql.append(" where 1=1 ");
		sql.append(" and "+ Sql_switcher.dateToChar("yearmark","yyyy") +"=? ");
		
		String new_year = "";
		if(StringUtils.isNotBlank(year)){
			new_year = year;
		}else{
			new_year = (String) this.listViewYear(viewTable, b0110).get(0);
		}
		list.add(new_year);

		sql.append(" and ( "+ Sql_switcher.dateToChar("yearmark", "mm") +">=? and "+ Sql_switcher.dateToChar("yearmark", "mm") +"<=? ) ");
		String month = this.getMinAndMaxMonth(viewTable, b0110, new_year);
		list.add(month.split(",")[0]);
		list.add(month.split(",")[1]);

		
		if(StringUtils.isNotBlank(b0110)){
			sql.append(" and b0110 in (");
			String[] b0110s = b0110.split(",");
			for (int i = 0; i < b0110s.length; i++) {
				sql.append("?,");
				list.add(b0110s[i]);
			}
			sql.setLength(sql.length()-1);
			sql.append(") ");
		}
		sql.append("group by itemid,itemname order by itemid ");
		log.debug("-----getLeiJiSql---->"+sql);
		rs = dao.search(sql.toString(), list);
		return rs;
	}
	
	/**
	 * 取得多项目指标集合
	 * @param viewTable
	 * @return
	 * @throws GeneralException 
	 */
	private List getMoreItemAndMoreTypeFieldItem(String viewTable) throws GeneralException{
		List fieldList = new ArrayList();
		String sql = "select * from "+viewTable + " where 1=2";
		RowSet rs = null;
		ResultSetMetaData data = null;
		Map filterFieldHM = this.getFilterField();
		try {
			rs = dao.search(sql);
			data = rs.getMetaData();
			for (int i = 0; i < data.getColumnCount(); i++) {
				if (filterFieldHM.containsKey(data.getColumnName(i + 1).toLowerCase())) {
					continue;
				}
				fieldList.add(data.getColumnName(i + 1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//获取视图机构信息出错
			log.error("/module/selfservice/resource_zh_CN.js--->analyse.error.getMoreItemAndMoreTypeViewFieldError");
			log.error("-------view---->"+viewTable);
			throw new GeneralException("analyse.error.getMoreItemAndMoreTypeViewFieldError");
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return fieldList;
	}

	/**
	 * 过滤的指标集合
	 * @return
	 */
	private Map getFilterField(){
		Map map = new HashMap();
		map.put("b0110", 1);
		map.put("name", 1);
		map.put("yearmark", 1);
		map.put("vflag", 1);
		return map;
	}

	@Override
	public List<Map> listOrgData(String viewTable, String b0110) throws GeneralException {
		// TODO Auto-generated method stub
		StringBuffer sql = new StringBuffer();
		ArrayList list = new ArrayList();
		ArrayList orgList = new ArrayList();
		RowSet rs = null;
		sql.append("select distinct b0110,name from "+ viewTable +" ");
		sql.append(" where 1=1 ");
		if(StringUtils.isNotBlank(b0110)){
			sql.append(" and b0110 in (");
			String[] b0110s = b0110.split(",");
			for (int i = 0; i < b0110s.length; i++) {
				sql.append("?,");
				list.add(b0110s[i]);
			}
			sql.setLength(sql.length()-1);
			sql.append(") ");
		}
		sql.append(" order by b0110 ");
		try {
			rs = dao.search(sql.toString(), list);
			while(rs.next()){
				HashMap map = new HashMap();
				map.put("value", PubFunc.encrypt(rs.getString("b0110")));
				map.put("text", rs.getString("name"));
				orgList.add(map);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//获取视图机构信息出错
			log.error("/module/selfservice/resource_zh_CN.js--->analyse.error.getViewOrgError");
			log.error("-------view---->"+viewTable);
			log.error("-----sql------->"+sql);
			throw new GeneralException("analyse.error.getViewOrgError");
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return orgList;
	}
}
