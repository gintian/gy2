package com.hjsj.hrms.module.talentmarkets.portaldashboard.businessobject.impl;

import com.hjsj.hrms.module.talentmarkets.portaldashboard.businessobject.PortalDashboardService;
import com.hjsj.hrms.module.talentmarkets.utils.TalentMarketsUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title PortalDashboardServiceImpl
 * @Description 人才市场门户页面接口实现类
 * @Company hjsj
 * @Author wangbs、hanqh
 * @Date 2019/7/30
 * @Version 1.0.0
 */
public class PortalDashboardServiceImpl implements PortalDashboardService {

	private UserView userView;
	private ContentDAO dao;

	public PortalDashboardServiceImpl(UserView userView, Connection conn) {
		this.userView = userView;
		this.dao = new ContentDAO(conn);
	}

	/**
	 * 初始化查询所有数据
	 * @author wangbs
	 * @return HashMap
	 * @throws GeneralException 抛出异常
	 */
	@Override
	public Map getAllData() throws GeneralException {
		Map returnMap = new HashMap();
		try {
			//获取组织机构权限sql
			String conditionSql = TalentMarketsUtils.getConditionsSql(this.userView, "4", "e01a1", "");
			String privOrgIdStr = TalentMarketsUtils.getAllPrivOrgIdStr(this.userView);
			Map psnOrPosPrivMap = TalentMarketsUtils.getPsnOrPosPriv(this.userView);

			Map staticPsnCountMap = getPsnStaticCountInfo(conditionSql);
			Map compePosChartOption = getCompePosChartOption("", conditionSql);

			returnMap.put("privOrgIdStr", privOrgIdStr);
			returnMap.put("psnOrPosPrivMap", psnOrPosPrivMap);
			returnMap.put("staticPsnCountMap", staticPsnCountMap);
			returnMap.put("compePosChartOption", compePosChartOption);
		} catch (GeneralException e) {
			e.printStackTrace();
			throw new GeneralException(e.getErrorDescription());
		}
		return returnMap;
	}

	/**
	 * 获取人数统计信息
	 * @author wangbs
	 * @param conditionsSql 机构权限sql
	 * @return Map
	 * @throws GeneralException 抛出异常
	 */
	private Map getPsnStaticCountInfo(String conditionsSql) throws GeneralException {
		int currentUserCount = 0;
		int totalUserCount = 0;
		int currentPosCount = 0;
		int totalPosCount = 0;
		Map staticPsnCountMap = new HashMap();
		RowSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select a.currentPsnCount,b.currentPosCount,c.hisPsnCount,d.hisPosCount from ");
			//获取当前竞聘人次
			sql.append("(select sum(z8109) currentPsnCount from z81 where z8103 in ('04','05') and (" + conditionsSql + ")) a,");
			//获取当前竞聘岗位数
			sql.append("(select count(z8101) currentPosCount from z81 where z8103 in ('04','05') and (" + conditionsSql + ")) b,");
			//获取历史竞聘人次
			sql.append("(select sum(z8109) hisPsnCount from z81 where z8103 in" + TalentMarketsUtils.END_STATUS + " and (" + conditionsSql + ")) c,");
			//获取历史竞聘岗位数
			sql.append("(select count(z8101) hisPosCount from z81 where z8103 in" + TalentMarketsUtils.END_STATUS + " and (" + conditionsSql + ")) d");

			rs = this.dao.search(sql.toString());
			if (rs.next()) {
				currentUserCount = rs.getInt("currentPsnCount");
				currentPosCount = rs.getInt("currentPosCount");
				totalUserCount = rs.getInt("hisPsnCount");
				totalPosCount = rs.getInt("hisPosCount");
			}
			staticPsnCountMap.put("currentUserCount", currentUserCount);
			staticPsnCountMap.put("currentPositionCount", currentPosCount);
			staticPsnCountMap.put("totalUserCount", totalUserCount);
			staticPsnCountMap.put("totalPositionCount", totalPosCount);
		} catch (Exception e) {
			e.printStackTrace();
			//查询数据失败
			throw new GeneralException("tm.staticPsnCountError");
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return staticPsnCountMap;
	}

	/**
	 * 根据机构编码拼接过滤语句
	 * @author wangbs
	 * @param orgIds 机构编码
	 * @return String
	 */
	private String getOrgFilterSql(String orgIds) {
		StringBuffer orgFilterSql = new StringBuffer();
		StringBuffer tempSql = new StringBuffer();
		String[] orgIdsArr = orgIds.split(",");
		for (String orgId : orgIdsArr) {
			tempSql.append(" or e01a1 like '" + orgId + "%'");
		}
		orgFilterSql.append(tempSql.substring(4));
		return orgFilterSql.toString();
	}

	/**
	 * 获取竞聘岗位chart的option
	 * @author wangbs
	 * @param orgIds 机构id
	 * @param conditionsSql 机构权限sql
	 * @return Map
	 * @throws GeneralException 抛出异常
	 */
	@Override
	public Map getCompePosChartOption(String orgIds, String conditionsSql) throws GeneralException {
		RowSet rs = null;
		StringBuffer sql = new StringBuffer();

		//chart配置数据
		Map chartOption = new HashMap();
		//需求人数数据
		List needPsnCountData = new ArrayList();
		//申报人数数据
		List applyPsnCountData = new ArrayList();
		//横轴数据
		List xAxisData = new ArrayList();
		//柱子对象纵轴信息
		List seriesData = new ArrayList();
		//图例对象配置
		Map legendMap = new HashMap();
		//图例列表
		List legendDataList = new ArrayList();
		try {
			if (StringUtils.isNotBlank(orgIds)) {
				conditionsSql = getOrgFilterSql(orgIds);
			}
			if (StringUtils.isBlank(conditionsSql)) {
				conditionsSql = "1=1";
			}
			legendDataList.add(ResourceFactory.getProperty("talentmarkets.needPsnCount"));
			legendDataList.add(ResourceFactory.getProperty("talentmarkets.applyPsnCount"));
			legendMap.put("data", legendDataList);

			//图例定位及排布方式
			legendMap.put("x", "right");
			legendMap.put("y", "center");
			legendMap.put("orient", "vertical");

			sql.append("select z8101,z8107,z8109,e01a1 from z81 where z8103 in('04','05') and (");
			sql.append(conditionsSql + ") order by z8109 desc");
			//oracle中认为null最大，实际业务中按应聘人数降序时需放在最后面
			if (Sql_switcher.searchDbServer() == 2) {
				sql.append(" nulls last");
			}
			rs = this.dao.search(sql.toString());

			List z8101List = new ArrayList();
			while (rs.next()){
				String z8101 = PubFunc.encrypt(rs.getString("z8101"));
				z8101List.add(z8101);
				//拟招聘人数 （需求人数）
				int z8107 = rs.getInt("z8107");
				//应聘人数 （申报人数）
				int z8109 =rs.getInt("z8109");

				//岗位信息
				String e01a1 = rs.getString("e01a1");
				String posDesc = AdminCode.getCodeName("@K", e01a1);

				xAxisData.add(posDesc);
				needPsnCountData.add(z8107);
				applyPsnCountData.add(z8109);
			}

			//需求人数柱子对象map
			Map needDataMap = new HashMap();
			needDataMap.put("type", "line");
			needDataMap.put("smooth", true);
			needDataMap.put("data", needPsnCountData);
			needDataMap.put("name", ResourceFactory.getProperty("talentmarkets.needPsnCount"));

			//申报人数柱子对象map
			Map applyDataMap = new HashMap();
			applyDataMap.put("type", "line");
			applyDataMap.put("smooth", true);
			applyDataMap.put("data", applyPsnCountData);
			applyDataMap.put("z8101List", z8101List);
			applyDataMap.put("name", ResourceFactory.getProperty("talentmarkets.applyPsnCount"));

			seriesData.add(needDataMap);
			seriesData.add(applyDataMap);

			chartOption.put("xAxisData", xAxisData);
			chartOption.put("seriesData", seriesData);
			chartOption.put("legend", legendMap);
		}catch (Exception e) {
			e.printStackTrace();
			//获取统计图配置数据失败
			throw new GeneralException("tm.chartOptionError");
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return chartOption;
	}
}
