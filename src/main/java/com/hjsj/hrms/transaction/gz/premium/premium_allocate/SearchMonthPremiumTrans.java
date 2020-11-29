package com.hjsj.hrms.transaction.gz.premium.premium_allocate;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.gz.premium.PremiumBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:SearchMonthPremiumTrans.java
 * </p>
 * <p>
 * Description:部门月奖金管理
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-11-26 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SearchMonthPremiumTrans extends IBusiness
{
	private String childOrgSqlStr = "";// 当前组织单元的直接子机构

	private String busiDateSqlStr = "";// 当前业务日期的条件

	public void execute() throws GeneralException
	{

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String currentOrg = (String) hm.get("orgcode");
		hm.remove("orgcode");
		currentOrg = currentOrg == null ? "" : currentOrg;

		String theYear = (String) hm.get("theYear");
		hm.remove("theYear");
		theYear = theYear == null ? "" : theYear;
		theYear = "undefined".equalsIgnoreCase(theYear)?"":theYear;
		
		String theMonth = (String) hm.get("theMonth");
		hm.remove("theMonth");
		theMonth = theMonth == null ? "" : theMonth;
		theMonth = "undefined".equalsIgnoreCase(theMonth)?"":theMonth;
		
		// 用于判断由叶子机构进入人员奖金然后点击返回的情况
		String returnFlag = (String) hm.get("returnFlag");
		hm.remove("returnFlag");
		returnFlag = returnFlag == null ? "" : returnFlag;

		String isLeafOrg = (String) hm.get("isLeafOrg");
		hm.remove("isLeafOrg");
		isLeafOrg = isLeafOrg == null ? "" : isLeafOrg;

		// 用于判断由核算单位标志为否的机构进入人员奖金然后点击返回的情况
		String isOrgCheckNo2 = (String) hm.get("isOrgCheckNo");
		hm.remove("isOrgCheckNo");
		isOrgCheckNo2 = isOrgCheckNo2 == null ? "" : isOrgCheckNo2;

		ConstantXml xml = new ConstantXml(this.frameconn, "GZ_BONUS", "Params");
		String setid = xml.getNodeAttributeValue("/Params/BONUS_SET", "setid");// 奖金子集
		String dist_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "dist_field");// 下发标识指标
		String rep_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "rep_field");// 上报标识指标
		String cardid = xml.getNodeAttributeValue("/Params/BONUS_SET", "cardid"); // 单位登记表号
		String keep_save_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "keep_save_field");// 封存字段
		String salaryid = xml.getNodeAttributeValue("/Params/BONUS_SET", "salaryid");// 共享工资类别
		String checkUn_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "checkUn_field");// 奖金核算单位标识指标

		if (setid.trim().length() == 0)
			throw new GeneralException(ResourceFactory.getProperty("error.notdefine.premiumset"));

		String busiField = setid + "z0";// 业务日期字段
		String operOrg = this.userView.getUnit_id();// 操作单位

		if (operOrg.trim().length() == 0|| "UN".equalsIgnoreCase(operOrg.trim()))
			throw new GeneralException(ResourceFactory.getProperty("error.notdefine.operOrg"));
		if ("UN`".equalsIgnoreCase(operOrg))
			throw new GeneralException("操作单位不可以设为全部，您必须指定一个组织单元！");

		StringBuffer buf = new StringBuffer();
		buf.append("select * from organization where 1=1 ");
		StringBuffer tempSql = new StringBuffer("");
		String[] temp = operOrg.split("`");
		for (int i = 0; i < temp.length; i++)
		{
			tempSql.append(" or  codeitemid = '" + temp[i].substring(2) + "'");
		}

		buf.append(" and ( " + tempSql.substring(3) + " ) order by codeitemid");

		String topOrg = ""; // 顶层机构
		operOrg = "";
		ArrayList operOrgList = new ArrayList();
		HashMap formulaFields = new HashMap();// 统计项、导入项和计算项

		String isKeepSave = "0";// 是否已经封存
		String isCanReport = "0";// 是否可以上报
		String isCanDistri = "0"; // 是否可以下发
		String isCanKeepSave = "0"; // 顶层机构是否可以执行封存操作
		String isOnlyLeafOrgs = "1";// 是否所有操作单位都是叶子机构
		String isAllDistri = "0";// 是否登录用户的所有操作单位都处于下发状态（顶层除外）
		String isGzManager = "0";// 是否工资管理员
		String isOrgCheckNo = "0";// 是否当前机构不往下核算了 此时当前机构直接进入月奖金的发放 按叶子机构处理
		if (salaryid != null && salaryid.trim().length() > 0)
		{
			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(), Integer.parseInt(salaryid), this.userView);
			String manager = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			if (this.userView.getUserName().equals(manager))
				isGzManager = "1";
		} else
			salaryid = "nodefine";

		ContentDAO dao = new ContentDAO(this.frameconn);
		String sqlStr = "select * from organization where codeitemid=parentid";
		try
		{
			this.frowset = dao.search(sqlStr);
			if (this.frowset.next())
				topOrg = this.frowset.getString("codeitemid") == null ? "" : this.frowset.getString("codeitemid");

			sqlStr = "select * from bonusformula where Upper(setid)='" + setid.toUpperCase() + "'";
			this.frowset = dao.search(sqlStr);
			while (this.frowset.next())
				formulaFields.put(this.frowset.getString("itemname").toLowerCase(), "");

			this.frowset = dao.search(buf.toString());
			while (this.frowset.next())
			{
				CommonData data = new CommonData(this.frowset.getString("codeitemid"), this.frowset.getString("codeitemdesc"));
				operOrgList.add(data);
			}
			if ("".equals(currentOrg))
			{
				if (operOrgList.size() > 0)
				{
					CommonData data = (CommonData) operOrgList.get(0);
					currentOrg = data.getDataValue();
				}
			}
			if (!"".equals(theYear) && !"".equals(theMonth))
			{
				this.busiDateSqlStr = Sql_switcher.year(busiField) + "=" + theYear + " and " + Sql_switcher.month(busiField) + "=" + theMonth;
				// 如果是非叶子机构的返回还返回非叶子机构，叶子机构就不可以返回叶子机构了要返回第一个非叶子子构,同理
				// 非核算机构也要返回第一个核算机构
				if ("1".equals(returnFlag) && ("1".equals(isLeafOrg) || "1".equals(isOrgCheckNo2)))
				{
					for (int i = 0; i < operOrgList.size(); i++)
					{
						CommonData data = (CommonData) operOrgList.get(i);
						String org = data.getDataValue();
						if (!this.isLeafOrg(org) && !this.isNotCheckNoOrg(org, setid, checkUn_field))
						{
							currentOrg = data.getDataValue();
							break;
						}
					}
				}

//				for (int i = 0; i < operOrgList.size(); i++)
//				{
//					CommonData data = (CommonData) operOrgList.get(i);
//					String org = data.getDataValue();
//					if (!this.isLeafOrg(org) && !this.isNotCheckNoOrg(org, setid, checkUn_field))
//					{
//						isOnlyLeafOrgs = "0";// 存在非叶子且可以核算的单位
//						break;
//					}
//				}
			}else
			{
//				for (int i = 0; i < operOrgList.size(); i++)
//				{
//					CommonData data = (CommonData) operOrgList.get(i);
//					String org = data.getDataValue();
//					if (!this.isLeafOrg(org))
//					{
//						isOnlyLeafOrgs = "0";
//						break;
//					}
//				}
			}

			String isTopOrg = "0";// 是否顶层机构
			String isLowerMost = "0";// 是否叶子机构
			if (currentOrg.equals(topOrg))
				isTopOrg = "1";

			sqlStr = "select * from organization where  codesetid in ('UM','UN') and parentid='" + currentOrg + "'";
			this.frowset = dao.search(sqlStr);
			if (this.frowset.next())
			{

			} else
				isLowerMost = "1";

			String errorinfo = "由于您没有设置人员月奖表参数，所以不能进行人员奖金的发放！";
			if ("1".equals(isLowerMost) && "nodefine".equals(salaryid))// 叶子结构节点
				// 直接进入人员月奖金的发放
				throw new GeneralException(errorinfo);

			// 直接子机构的字符串
			this.childOrgSqlStr = "select codeitemid from organization where  codesetid in ('UM','UN') and parentid='" + currentOrg + "'";

			if ("".equals(theYear) || "".equals(theMonth))
			{
				PremiumBo bo = new PremiumBo(this.frameconn, this.userView);
				String maxYearMonth = bo.getMaxYearMonth(setid, currentOrg);
				String[] ym = StringUtils.split(maxYearMonth, "-");
				theYear = ym[0];
				theMonth = ym[1];				
			}
			this.busiDateSqlStr = Sql_switcher.year(busiField) + "=" + theYear + " and " + Sql_switcher.month(busiField) + "=" + theMonth;

			for (int i = 0; i < operOrgList.size(); i++)
			{
				CommonData data = (CommonData) operOrgList.get(i);
				String org = data.getDataValue();
				if (!this.isLeafOrg(org) && !this.isNotCheckNoOrg(org, setid, checkUn_field))// 存在非叶子且可以核算的单位
				{
					isOnlyLeafOrgs = "0";
					break;
				}
			}			
			
			buf.setLength(0);
			buf.append("select * from " + setid + " where " + checkUn_field + "='2' and b0110='" + currentOrg + "' ");
			buf.append(" and " + this.busiDateSqlStr);
			this.frowset = dao.search(buf.toString());
			if (this.frowset.next())
				isOrgCheckNo = "1";

			if ("1".equals(isOrgCheckNo) && "nodefine".equals(salaryid))// 当前机构不往下核算了
				// 直接进入人员月奖金的发放
				throw new GeneralException(errorinfo);

			if (isCanDistrbute(isTopOrg, currentOrg, isLowerMost, setid, dist_field))
				isCanDistri = "1";
			if (isCanReport(isTopOrg, currentOrg, isLowerMost, setid, dist_field, rep_field))
				isCanReport = "1";

			isAllDistri = this.testDistributeAll(temp, topOrg, setid, dist_field, checkUn_field);
			this.getFormHM().put("isAllDistri", isAllDistri);

			boolean isData = false; // 表中是否有数据
			sqlStr = "select count(*) from " + setid;
			this.frowset = dao.search(sqlStr);
			if (this.frowset.next())
				if (this.frowset.getInt(1) > 0)
					isData = true;
			// 封存是一次性的 只要顶层机构做一次封存 所有子机构就都变为封存状态
			// sqlStr = "select * from " + setid + " where " +
			// keep_save_field + "='1' ";
			// sqlStr += " and " + this.busiDateSqlStr;
			// sqlStr += "and b0110 = '" + currentOrg + "'";
			sqlStr = "select * from " + setid + " where " + keep_save_field + "='2' or " + keep_save_field + " is null ";
			sqlStr += "and b0110 = '" + currentOrg + "'";
			this.frowset = dao.search(sqlStr);
			if (this.frowset.next())// 是否存在没有封存的记录
				isKeepSave = "0";
			else
				isKeepSave = "1";
			if (!isData) // 如果表中无数据，默认封存状态
				isKeepSave = "1";

			// 如果顶层机构的直接子机构处于已上报状态 顶层机构就可以封存了
			if ("1".equals(isTopOrg))
			{
				isCanKeepSave = "1";
				sqlStr = "select * from " + setid;
				sqlStr += " where " + this.busiDateSqlStr;
				sqlStr += "and b0110 in (" + this.childOrgSqlStr + ")";
				this.frowset = dao.search(sqlStr);
				while (this.frowset.next())
				{
					String repFlag = this.frowset.getString(rep_field) == null ? "2" : this.frowset.getString(rep_field);
					if ("2".equals(repFlag))// 存在未上报的就不能封存
					{
						isCanKeepSave = "0";
						break;
					}
				}
			}

			StringBuffer sql = new StringBuffer("select ");
			StringBuffer sumSql = new StringBuffer("select ");
			ArrayList fieldlist = new ArrayList();
			ArrayList list = DataDictionary.getFieldList(setid, Constant.USED_FIELD_SET);

			Field field = DataDictionary.getFieldItem("b0110").cloneField();
			field.setVisible(false);
			fieldlist.add(field);
			field = new Field("i9999");
			field.setLabel("序号");
			field.setVisible(false);
			fieldlist.add(field);
			sql.append(" b0110,i9999,");
			sumSql.append("'sum' as b0110,0 as i9999,");

			field = new Field("orgCode");
			field.setLabel("单位|部门");
			field.setReadonly(true);
			fieldlist.add(field);
			sumSql.append("'合计' as orgCode,");
			sql.append(" (select codeitemdesc from organization where codeitemid=" + setid + ".b0110) as orgCode,");

			for (int i = 0; i < list.size(); i++)
			{
				FieldItem fielditem = (FieldItem) list.get(i);
				field = fielditem.cloneField();
				String itemid = field.getName();
				String itemtype = fielditem.getItemtype();
				if ("0".equals(this.userView.analyseFieldPriv(itemid, 0)) && "0".equals(this.userView.analyseFieldPriv(itemid, 1)))
					field.setVisible(false);
				if ("1".equals(this.userView.analyseFieldPriv(itemid, 0)) && "1".equals(this.userView.analyseFieldPriv(itemid, 1)))
					field.setReadonly(true);
				if (!"2".equals(this.userView.analyseTablePriv(setid)))
					field.setReadonly(true);
				if (formulaFields.get(itemid.toLowerCase()) != null)
					field.setReadonly(true);

				if (itemid.equalsIgnoreCase(dist_field))
					field.setReadonly(true);
				if (itemid.equalsIgnoreCase(rep_field))
					field.setReadonly(true);
				if (itemid.equalsIgnoreCase(keep_save_field))
					field.setReadonly(true);
				fieldlist.add(field);
				sql.append(itemid + ",");
				if ("N".equals(itemtype))
				{
					if (itemid.equalsIgnoreCase(setid + "z1"))
						sumSql.append("null as " + itemid + ",");
					else
						sumSql.append("sum(" + itemid + ") as " + itemid + ",");
				} else 
					sumSql.append("null as " + itemid + ",");

			}
			sql.setLength(sql.length() - 1);
			sumSql.setLength(sumSql.length() - 1);

			String whlSql = " from " + setid + " where b0110 in (" + this.childOrgSqlStr + ") ";
			whlSql += " and " + this.busiDateSqlStr;

			// sql.append(whlSql); 为了让直接子机构展示的时候能按照 organization 中的 a0000排序
			// 在此换一种写法
			if ("0".equals(isLowerMost))
			{
				this.frowset = dao.search(this.childOrgSqlStr + " order by a0000");
				StringBuffer sqlRepeat2 = new StringBuffer();
				while (this.frowset.next())
				{
					StringBuffer sqlRepeat = new StringBuffer(sql.toString());
					sqlRepeat.append(" from " + setid + " where b0110 ='" + this.frowset.getString("codeitemid") + "' ");
					sqlRepeat.append(" and " + this.busiDateSqlStr);
					sqlRepeat.append(" union all ");
					sqlRepeat2.append(sqlRepeat);
				}
				if (sqlRepeat2.length() > 0)
					sqlRepeat2.setLength(sqlRepeat2.length() - " union all ".length());
				sql.setLength(0);
				sql.append(sqlRepeat2.toString());
			} else
			{
				sql.append(whlSql);
			}
			sumSql.append(whlSql);

			if ("0".equals(isLowerMost))
			{
				this.frowset = dao.search(sql.toString());
				if (this.frowset.next())
				{
					sql.append(" union all ");
					sql.append(sumSql);
				}
			}

			this.getFormHM().put("sql", sql.toString());
			this.getFormHM().put("fieldlist", fieldlist);
			this.getFormHM().put("dist_field", dist_field);
			this.getFormHM().put("orgsubset", setid);
			this.getFormHM().put("operOrgList", operOrgList);
			this.getFormHM().put("operOrg", currentOrg);
			this.getFormHM().put("year", theYear);
			this.getFormHM().put("month", theMonth);
			this.getFormHM().put("isDistribute", isCanDistri);
			this.getFormHM().put("isTopOrg", isTopOrg);
			this.getFormHM().put("isKeepSave", isKeepSave);
			this.getFormHM().put("isCanReport", isCanReport);
			this.getFormHM().put("cardid", cardid);
			this.getFormHM().put("isCanKeepSave", isCanKeepSave);
			this.getFormHM().put("salaryid", salaryid);
			this.getFormHM().put("isLeafOrg", isLowerMost);
			this.getFormHM().put("isOnlyLeafOrgs", isOnlyLeafOrgs);
			this.getFormHM().put("isGzManager", isGzManager);
			this.getFormHM().put("isOrgCheckNo", isOrgCheckNo);
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

	public boolean isLeafOrg(String orgCode) throws GeneralException
	{

		boolean flag = true;
		ContentDAO dao = new ContentDAO(this.frameconn);
		String sqlStr = "select * from organization where  codesetid in ('UM','UN') and parentid='" + orgCode + "'";
		try
		{
			this.frowset = dao.search(sqlStr);
			if (this.frowset.next())
				flag = false;
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return flag;
	}

	/** 判断是否为奖金核算单位 */
	public boolean isNotCheckNoOrg(String orgCode, String setid, String checkUn_field) throws GeneralException
	{
		boolean flag = true;

		ContentDAO dao = new ContentDAO(this.frameconn);
		String sqlStr = "select b0110 from " + setid + " where  b0110='" + orgCode + "' and " + this.busiDateSqlStr ;
		try
		{
			this.frowset = dao.search(sqlStr);
			if (this.frowset.next())
			{
				
			}
			else
			{
				flag = false; 
				return flag;
			}		
		
		 sqlStr = "select b0110 from " + setid + " where  b0110='" + orgCode + "' and " + this.busiDateSqlStr + " and (" + checkUn_field + "='1' or "
				+ checkUn_field + " is null)";
		
			this.frowset = dao.search(sqlStr);
			if (this.frowset.next())
				flag = false;
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return flag;
	}

	/**
	 * 下发：将子机构改为下发状态 是否可以下发考虑情况： 1.如果是顶层，看子机构是否为下发状态，如果不是可以下发。
	 * 2.如果当前机构不是顶层，当自己为下发，子机构没有下发才能下发 3.最后一层不下发
	 * 
	 * @throws GeneralException
	 */
	public boolean isCanDistrbute(String isTopOrg, String currentOrg, String isLowerMost, String setid, String dist_field) throws GeneralException
	{

		ContentDAO dao = new ContentDAO(this.frameconn);
		String sqlStr = "";
		boolean isCanDistri = false;
		// 最后一层不下发
		if ("1".equals(isLowerMost))
			return false;
		try
		{
			// 如果是顶层，看子机构是否为下发状态，如果不是可以下发。
			if ("1".equals(isTopOrg))
			{
				sqlStr = "select * from " + setid + " where (" + dist_field + "='2' or " + dist_field + " is null ) and b0110 in (" + this.childOrgSqlStr
						+ ") and " + this.busiDateSqlStr;
				this.frowset = dao.search(sqlStr);
				if (this.frowset.next())
					isCanDistri = true;
			} else
			// 如果当前机构不是顶层，当自己为下发，子机构没有下发才能下发
			{
				boolean selfDistri = false;
				boolean childNoDistri = false;
				sqlStr = "select * from " + setid + " where " + dist_field + "='1'  and b0110 = '" + currentOrg + "' and " + this.busiDateSqlStr;
				this.frowset = dao.search(sqlStr);
				if (this.frowset.next())
					selfDistri = true;

				sqlStr = "select * from " + setid + " where (" + dist_field + "='2' or " + dist_field + " is null ) and b0110 in (" + this.childOrgSqlStr
						+ ") and " + this.busiDateSqlStr;
				this.frowset = dao.search(sqlStr);
				if (this.frowset.next())
					childNoDistri = true;

				if (selfDistri && childNoDistri)
					isCanDistri = true;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return isCanDistri;
	}

	/**
	 * 上报：将自己和子机构改为上报状态 是否可以上报考虑情况： 1.最后一层和第一层不上报
	 * 2.如果当前机构味为倒数第二层，当前孩子为已下发，当前孩子和自己都为未上报可以上报 3.非倒数第一层和倒数第二层，当孩子为上报自己为未上报可以上报
	 * 
	 * @throws GeneralException
	 */
	public boolean isCanReport(String isTopOrg, String currentOrg, String isLowerMost, String setid, String dist_field, String rep_field)
			throws GeneralException
	{

		ContentDAO dao = new ContentDAO(this.frameconn);
		String sqlStr = "";
		boolean isCanReport = false;
		// 顶层机构不执行不上报，叶子机构的上报在人员奖金分配界面执行，所以这两种当前机构都不执行上报功能
		if ("1".equals(isLowerMost) || "1".equals(isTopOrg))
			return false;
		try
		{
			// boolean isLastTwoLayerOrg = false;// 是否为倒数第二层
			// StringBuffer orgBuf = new StringBuffer();
			// orgBuf.append("select * from organization where parentid in
			// ");
			// orgBuf.append("(select childid from organization where
			// codeitemid='");
			// orgBuf.append(currentOrg);
			// orgBuf.append("' and codesetid in ('UM','UN')) and codesetid
			// in ('UM','UN')");
			//
			// orgBuf.append(" select * from organization where parentid='"
			// + currentOrg + "' and codesetid in ('UM','UN') ");
			// orgBuf.append(" and codeitemid in (select parentid from
			// organization where upper(codesetid)='UM' or
			// upper(codesetid)='UN' ) ");
			// this.frowset = dao.search(orgBuf.toString());
			// if (this.frowset.next())// 判断有没有孙子机构
			// {
			//
			// } else
			// isLastTwoLayerOrg = true;
			// 如果是倒数第二层，当前孩子为已下发状态，当前孩子和自己为没有上报状态可以上报
			// if (isLastTwoLayerOrg)
			// {
			// boolean isChildDistri = false;
			// boolean isSelfNoReport = false;
			// boolean isChildNoReport = false;
			//
			// sqlStr = "select * from " + setid + " where " + dist_field +
			// "='1' and b0110 in (" + this.childOrgSqlStr + ") and " +
			// this.busiDateSqlStr;
			// this.frowset = dao.search(sqlStr);
			// if (this.frowset.next())
			// isChildDistri = true;
			//
			// sqlStr = "select * from " + setid + " where (" + rep_field +
			// "='2' or " + rep_field + " is null ) and b0110 in (" +
			// this.childOrgSqlStr + ") and " + this.busiDateSqlStr;
			// this.frowset = dao.search(sqlStr);
			// if (this.frowset.next())
			// isChildNoReport = true;
			//
			// sqlStr = "select * from " + setid + " where (" + rep_field +
			// "='2' or " + rep_field + " is null ) and b0110='" +
			// currentOrg + "' and " + this.busiDateSqlStr;
			// this.frowset = dao.search(sqlStr);
			// if (this.frowset.next())
			// isSelfNoReport = true;
			//
			// if (isChildDistri && isChildNoReport && isSelfNoReport)
			// isCanReport = true;
			// } else
			// 如果当前机构不是倒数第二层，当自己为没有上报，孩子为上报才能上报
			{
				boolean isSelfNoReport = false;
				boolean isChildReport = true;

				sqlStr = "select * from " + setid + " where (" + rep_field + "='2' or " + rep_field + " is null ) and b0110 in (" + this.childOrgSqlStr
						+ ") and " + this.busiDateSqlStr;
				this.frowset = dao.search(sqlStr);
				if (this.frowset.next())
					isChildReport = false;

				sqlStr = "select * from " + setid + " where (" + rep_field + "='2' or " + rep_field + " is null )  and b0110='" + currentOrg + "' and "
						+ this.busiDateSqlStr;
				this.frowset = dao.search(sqlStr);
				if (this.frowset.next())
					isSelfNoReport = true;

				if (isSelfNoReport && isChildReport)
					isCanReport = true;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return isCanReport;
	}

	// 判断所有操作单位是否处于下发状态,如果操作单位中存在核算单位标志为否的机构，只要这种单位之上的组织机构处于下发状态就算都下发
	public String testDistributeAll(String[] temp, String topOrg, String setid, String dist_field, String checkUn_field) throws GeneralException
	{
		ContentDAO dao = new ContentDAO(this.frameconn);
		String isAllDistri = "0";

		StringBuffer buf = new StringBuffer();
		boolean isHaveTopOrg = false;
		int maxWidth = 0;
		StringBuffer tempSql = new StringBuffer("");
		for (int i = 0; i < temp.length; i++)
		{
			if (temp[i].substring(2).equals(topOrg))
			{
				isHaveTopOrg = true;
				continue;
			}
			tempSql.append(" or  b0110 = '" + temp[i].substring(2) + "'");
		}
//		buf.setLength(0);

//		buf.append("select b0110  from " + setid + " where " + checkUn_field + "='2' ");
//		buf.append(" and " + this.busiDateSqlStr);
//		if (tempSql.length() > 0)
//			buf.append(" and ( " + tempSql.substring(3) + ")");
		try
		{
//			this.frowset = dao.search(buf.toString());
//			while (this.frowset.next())// 操作单位中有核算单位为否的机构
//			{
//				int x = this.frowset.getString("b0110").length();
//				if (maxWidth < x)
//					maxWidth = x;
//			}

			buf.setLength(0);
			buf.append("select count(*)  from " + setid + " where " + dist_field + "='1' ");
			buf.append(" and " + this.busiDateSqlStr);

			// 没有设置核算单位为否的操作单位
//			if (maxWidth == 0)
//			{
				// 判断是否登录用户的所有操作单位都处于下发状态（顶层除外）
				if (tempSql.length() > 0)
					buf.append(" and ( " + tempSql.substring(3) + ")");
				this.frowset = dao.search(buf.toString());
				if (this.frowset.next())
				{
					int count = this.frowset.getInt(1);
					if (count > 0 && isHaveTopOrg)
						count++;
					if (count == temp.length)
						isAllDistri = "1";
				}
//			} else
//			{
//				tempSql.setLength(0);
//				int y = 0;
//				for (int i = 0; i < temp.length; i++)
//				{
//					if (temp[i].substring(2).equals(topOrg))
//					{
//						isHaveTopOrg = true;
//						continue;
//					}
//					if (temp[i].substring(2).length() >= maxWidth)
//						continue;
//					tempSql.append(" or  b0110 = '" + temp[i].substring(2) + "'");
//					y++;
//				}
//
//				if (tempSql.length() > 0)
//					buf.append(" and ( " + tempSql.substring(3) + ")");
//				buf.append(" and b0110 !='" + topOrg + "'");
//				this.frowset = dao.search(buf.toString());
//				if (this.frowset.next())
//				{
//					int count = this.frowset.getInt(1);
//					if (count == y)
//						isAllDistri = "1";
//				}
//			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		return isAllDistri;
	}
}
