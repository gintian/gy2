package com.hjsj.hrms.module.gz.salaryaccounting.batch.businessobject;

import com.hjsj.hrms.businessobject.gz.SalaryLProgramBo;
import com.hjsj.hrms.businessobject.gz.templateset.GzItemVo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 
 * 项目名称：hcm7.x 
 * 类名称：BatchImportBo 类 
 * 描述：执行批量引入Bo类 
 * 创建人：sunming 
 * 创建时间：2015-8-20
 * 
 * @version
 */
public class BatchImportBo {
	private Connection conn = null;
	/** 薪资表名称 */
	private String gz_tablename;
	/** 薪资类别号 */
	private int salaryid = -1;
	/** 登录用户 */
	private UserView userview;
	private SalaryAccountBo salaryAccountBo = null;
	private SalaryTemplateBo salaryTemplateBo = null;
	/** 薪资控制参数 */
	private SalaryCtrlParamBo ctrlparam = null;
	/** 工资管理员，对共享类别有效* */
	private String manager = "";
	/** 薪资类别数据对象 */
	private RecordVo templatevo = null;

	public BatchImportBo(Connection conn, int salaryid, UserView userview) {
		this.conn = conn;
		this.userview = userview;
		this.salaryid = salaryid;
		this.salaryAccountBo = new SalaryAccountBo(conn, this.userview,
				salaryid);
		this.salaryTemplateBo = this.salaryAccountBo.getSalaryTemplateBo();
		this.templatevo = this.salaryTemplateBo.getTemplatevo();
		this.manager = this.salaryTemplateBo.getManager();
		this.ctrlparam = this.salaryTemplateBo.getCtrlparam();
		this.gz_tablename = this.salaryTemplateBo.getGz_tablename();
	}

	/**
	 * 获得可执行引入操作的人员记录条件
	 * 
	 * @return
	 */
	private String getConditionSql() {
		StringBuffer strwhere = new StringBuffer();
		String payitem = this.ctrlparam.getValue(SalaryCtrlParamBo.PAY_FLAG);
		String manager = this.salaryTemplateBo.getManager();
		/** 仅导入正常发薪记录 */
		if (payitem.length() != 0) {
			strwhere.append(" (" + gz_tablename + "." + payitem + "='0' ");
			strwhere.append(" or " + gz_tablename + "." + payitem + "='' or "
					+ gz_tablename + "." + payitem + " is null) ");
		}
		/** 需要审批||不允许提交后更改数据 ,仅导入起草和驳回记录 */
		String flow_flag = this.ctrlparam.getValue(SalaryCtrlParamBo.FLOW_CTRL,
				"flag"); // 1:需要审批
		SalaryLProgramBo lpbo = new SalaryLProgramBo(this.salaryTemplateBo
				.getTemplatevo().getString("lprogram"));
		String allowEditSubdata = lpbo.getValue(SalaryLProgramBo.CONFIRM_TYPE,
				"allow_edit_subdata"); // 薪资发放 是否允许提交后更改数据；具有 “允许提交后更改数据”
		if ("1".equals(flow_flag) || !"1".equals(allowEditSubdata)) {
			if (strwhere.length() != 0)
				strwhere.append(" and ");
			strwhere.append(gz_tablename);
			strwhere.append(".");
			strwhere.append("sp_flag in('01','07') ");
		}
		// 共享薪资类别，其他操作人员引入数据
		if (manager.length() > 0
				&& !this.userview.getUserName().equalsIgnoreCase(manager)) {
			if (strwhere.length() != 0)
				strwhere.append(" and ");
			strwhere.append(gz_tablename);
			strwhere.append(".");
			strwhere.append(" sp_flag2 in ('01','07') ");

		}

		if (strwhere.length() == 0)
			strwhere.append(" 1=1 ");
		strwhere.append(this.salaryTemplateBo.getfilter(gz_tablename)); // 获取表格工具过滤以及页面模糊查询返回的sql片段
		if (manager != null && manager.length() > 0
				&& !this.userview.getUserName().equalsIgnoreCase(manager))
			strwhere.append(this.salaryTemplateBo.getWhlByUnits(gz_tablename,
					true));
		return strwhere.toString();
	}

	/**
	 * 获得引入指标列表中当前记录值和非当前记录值的指标集合
	 * 
	 * @param isE01A1
	 *            引入指标是否包含岗位
	 * @param itemList
	 *            非当前记录的引入指标集合
	 * @param setMap
	 *            引入当前记录指标稽核
	 * @param items
	 *            前台选中的引入指标id
	 * @param allFieldMap
	 */
	private void getImportItemList(Boolean isE01A1, ArrayList itemList,
			HashMap setMap, String items, HashMap allFieldMap) {
		ArrayList gzItemList = this.salaryTemplateBo.getSalaryItemList("",
				this.salaryid + "", 1);
		LazyDynaBean abean = null;
		for (int i = 0; i < gzItemList.size(); i++) {
			abean = (LazyDynaBean) gzItemList.get(i);
			String fieldsetid = (String) abean.get("fieldsetid");
			String itemid = (String) abean.get("itemid");
			if ("E01A1".equalsIgnoreCase(itemid))
				isE01A1 = true;
			/** 过滤未选中的薪资项目 */
			if (items.indexOf(itemid.toUpperCase()) == -1)
				continue;
			/** 单位指标或职位指标 */
			if (fieldsetid.charAt(0) == 'A') {
				int nlock = Integer.parseInt((String) abean.get("nlock"));
				int ainit = Integer.parseInt((String) abean.get("initflag"));
				int nheap = Integer.parseInt((String) abean.get("heapflag"));
				String formula = (String) abean.get("formula");
				/** nlock=1,对系统项 2:导入项 */
				if (nlock == 0 && nheap == 0 && (ainit == 1 || ainit == 2)) {
					if (allFieldMap.get(formula.trim().toLowerCase()) != null
							|| DataDictionary.getFieldItem(formula.trim()) != null) {
						FieldItem field = null;
						if (allFieldMap.get(formula.trim().toLowerCase()) != null) // 20141209
							// dengcan
							field = (FieldItem) ((FieldItem) allFieldMap
									.get(formula.trim().toLowerCase())).clone();
						if (field == null)
							field = (FieldItem) DataDictionary.getFieldItem(
									formula.trim()).clone();
						// 公式定义成部门、岗位时，得到的fielditem的setid为K01 DENGCAN 20141025
						if ("e0122".equalsIgnoreCase(field.getItemid())
								|| "e01a1".equalsIgnoreCase(field.getItemid())|| "b0110".equalsIgnoreCase(field.getItemid())) {
							field.setFieldsetid("A01");
						}

						String a_setid = field.getFieldsetid();
						if (setMap.get(a_setid.toUpperCase()) != null) {
							ArrayList tempList = (ArrayList) setMap.get(a_setid
									.toUpperCase());
							tempList.add(itemid + "`" + field.getItemid());
							setMap.put(a_setid.toUpperCase(), tempList);
						} else {
							ArrayList tempList = new ArrayList();
							tempList.add(itemid + "`" + field.getItemid());
							setMap.put(a_setid.toUpperCase(), tempList);
						}
					} else
						itemList.add(abean);
				} else {
					itemList.add(abean);
				}
			} else
				itemList.add(abean);
		}
	}

	/**
	 * 从档案库中引入数据
	 * 
	 * @param itemlist
	 *            引入的薪资项目列表
	 * @param busiDate
	 *            业务日期、次数 date:2010-03-01 count:1
	 * @return
	 */
	private boolean batchImportFromArchive(ArrayList itemlist,
			LazyDynaBean busiDate) throws GeneralException {
		boolean bflag = true;
		int ninit = 0;
		RowSet rowSet = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer strwhere = new StringBuffer();
			String items = Arrays.toString(itemlist.toArray());
			items = items.toUpperCase();
			strwhere.append(getConditionSql()); // 获得可执行引入操作的人员记录条件
			ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
					Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			FieldItem Field = null;
			HashMap allFieldMap = new HashMap();
			for (int i = 0; i < allUsedFields.size(); i++) {
				Field = (FieldItem) allUsedFields.get(i);
				allFieldMap.put(Field.getItemdesc().toLowerCase(), Field);
			}

			ArrayList itemList = new ArrayList(); // 引入非当前记录值的指标集合
			HashMap setMap = new HashMap(); // 引入当前记录值的指标集合
			Boolean isE01A1 = new Boolean(false); // //是否有岗位名称
			getImportItemList(isE01A1, itemList, setMap, items, allFieldMap); // 获得引入指标列表中当前记录值和非当前记录值得指标集合
			String dbpres = this.salaryTemplateBo.getTemplatevo().getString(
					"cbase"); // 薪资帐套指定的人员库前缀
			String strpre = null;
			/** 应用库前缀 */
			String[] dbarr = StringUtils.split(dbpres, ",");
			String tempWhere = strwhere.toString();
			for (int j = 0; j < dbarr.length; j++) {
				strpre = dbarr[j];
				strwhere.setLength(0);
				strwhere.append(tempWhere);
				strwhere.append(" and lower(nbase)='" + strpre.toLowerCase()
						+ "'");

				String importMenSql_where = ""; // 算法分析器计算时需限定的人员范围，减少计算人数，提高性能
				importMenSql_where += " select A0100 from ";
				importMenSql_where += gz_tablename;
				importMenSql_where += " where  1=1 ";
				if (strwhere != null&& strwhere.toString().trim().length() > 0)
					importMenSql_where +=" and "+ strwhere;

				// 将导入当前记录数据的项目先批量处理
				this.salaryAccountBo.batchImportGzItems(setMap, strwhere
						.toString(), strpre, gz_tablename); // 批量引入当前记录值
				if (isE01A1.booleanValue()) // 更新人员岗位信息
				{
					String sql = "update " + gz_tablename
							+ " set E01A1=(select E01A1 from " + strpre
							+ "A01 where " + strpre + "A01.a0100="
							+ gz_tablename + ".a0100  ) where exists ";
					sql += " (select null from " + strpre + "A01 where "
							+ strpre + "A01.a0100=" + gz_tablename
							+ ".a0100  ) and lower(nbase)='"
							+ strpre.toLowerCase() + "' ";
					if (strwhere != null
							&& strwhere.toString().trim().length() > 0)
						sql += " and " + strwhere.toString();
					dao.update(sql);
				}

				/** 所有项目 */
				LazyDynaBean abean = null;
				for (int i = 0; i < itemList.size(); i++) {
					abean = (LazyDynaBean) itemList.get(i);
					String fieldsetid = (String) abean.get("fieldsetid");
					String itemid = (String) abean.get("itemid");

					int nlock = Integer.parseInt((String) abean.get("nlock")); // nlock=1,对系统项
					int ainit = Integer
							.parseInt((String) abean.get("initflag"));
					int nheap = Integer
							.parseInt((String) abean.get("heapflag"));
					String formula = (String) abean.get("formula");

					/** 单位指标或职位指标 */
					if (fieldsetid.charAt(0) != 'A') {
						this.salaryAccountBo.computingImportUnitItem(abean,
								strwhere.toString(), strpre, isE01A1, busiDate);
					} else {

						if (nlock == 0 || "A01z0".equalsIgnoreCase(itemid)) {
							rowSet = dao.search("select count(nbase) from "
									+ gz_tablename + " where lower(nbase)=? ",
									Arrays.asList(new Object[] { strpre
											.toLowerCase() }));
							if (rowSet.next()) {
								if (rowSet.getInt(1) > 0) {
									switch (ainit) {
									case 0:// 清零项，不管它
										break;
									case 1: // 累积项
										this.salaryAccountBo
												.computingImportItem(abean,
														strwhere.toString(),
														strpre,
														importMenSql_where
																.toString(),
														busiDate);
										break;
									case 2: // 导入项
										this.salaryAccountBo
												.computingImportItem(abean,
														strwhere.toString(),
														strpre,
														importMenSql_where
																.toString(),
														busiDate);
										break;
									case 3: // 系统项
										this.salaryAccountBo
												.computingImportItem(abean,
														strwhere.toString(),
														strpre,
														importMenSql_where
																.toString(),
														busiDate);
										break;
									}
								}
							}
						}// nlock end.
					}

				}// for i loop end.
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			bflag = false;
			throw GeneralExceptionHandler.Handle(ex);
		} finally {
			PubFunc.closeDbObj(rowSet);
		}
		return bflag;
	}

	/**
	 * 判读用户是否有此薪资类别|保险类别的权限
	 * 
	 * @param gz_module
	 *            1:保险 0：薪资
	 * @return
	 * @throws GeneralException
	 */
	public boolean isSalarySetResource(String gz_module)
			throws GeneralException {
		String salaryid = String.valueOf(this.salaryid);
		boolean isPriv = true;
		if (!this.userview.isHaveResource(IResourceConstant.GZ_SET, salaryid)
				&& !this.userview.isHaveResource(IResourceConstant.INS_SET,
						salaryid)) {
			isPriv = false;

			if (gz_module == null)
				throw GeneralExceptionHandler.Handle(new Exception(
						ResourceFactory
								.getProperty("gz.acount.noClassesAuthority")
								+ "!"));
			else if ("1".equals(gz_module))
				throw GeneralExceptionHandler.Handle(new Exception(
						ResourceFactory.getProperty("gz.acount.noInsAuthority")
								+ "!"));
			else
				throw GeneralExceptionHandler.Handle(new Exception(
						ResourceFactory.getProperty("gz.acount.noGzAuthority")
								+ "!"));
		}

		return isPriv;
	}

	/**
	 * 批量引入数据
	 * 
	 * @param type
	 *            =1同月上次 =2上月同次数据 =3档案库数据 =4某年某月某次
	 * @param itemlist引入的薪资项目列表
	 * @param appdate
	 *            业务日期
	 * @param busiDateSome
	 *            type=4时，年月次的集合
	 * @return
	 * @throws GeneralException
	 */
	public boolean batchImport(String type, ArrayList itemlist,
			ArrayList busiDateSome, String appdate, String count)
			throws GeneralException {
		String royalty_valid = ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,
				"valid");
		// 拼接业务日期次数bean
		LazyDynaBean busiDate = new LazyDynaBean();
		busiDate.set("date", appdate);
		busiDate.set("count", count);
		// 提成工资
		if (royalty_valid != null && "1".equals(royalty_valid)) {
			// 提成工资批量引入
			reImportRoyaltyData(itemlist, busiDate);
			// 删除不在范围的人员
			delNoConditionData();
			return true;
		} else {
			if ("3".equalsIgnoreCase(type)) {
				return batchImportFromArchive(itemlist, busiDate);
			} else {
				return batchImportFromHisGzTable(type, itemlist, busiDateSome,
						busiDate);
			}
		}
	}

	/**
	 * 批量从薪资历史数据表取得已发放的数据
	 * 
	 * @param type
	 *            =1同月上次 =2上月同次数据 =4某年某月某次
	 * @param itemlist
	 *            引入的薪资项目列表
	 * @param year
	 * @param month
	 * @param count
	 * @return
	 * @throws GeneralException
	 */
	private boolean batchImportFromHisGzTable(String type, ArrayList itemlist,
			ArrayList busiDateSome, LazyDynaBean busiDate)
			throws GeneralException {
		boolean bflag = true;
		try {
			String tablename = "salaryhistory";
			try {
				// 发放次数
				String strym = null;
				// nc发放日期 ny年 nm月
				int nc = 0, ny = 0, nm = 0;
				/** 求得当前处理年月和次数 */
				String currym = (String) busiDate.get("date");
				String currcount = (String) busiDate.get("count");
				if ("1".equals(type))// =1同月上次
				{
					nc = Integer.parseInt(currcount) - 1;
					strym = currym;
					if(strym.length()==7)
						strym=strym+"-01";
						
				} else if ("2".equals(type)) {
					nc = Integer.parseInt(currcount);
					String[] tmp = StringUtils.split(currym, "-");
					ny = Integer.parseInt(tmp[0]);
					nm = Integer.parseInt(tmp[1]);
					if (nm == 1) {
						ny = ny - 1;
						nm = 12;
					} else {
						nm = nm - 1;
					}
					strym = String.valueOf(ny) + "-" + String.valueOf(nm) + "-"
							+ "01";
				} else if ("4".equals(type)) {
					nc = Integer.parseInt((String) busiDateSome.get(2));
					ny = Integer.parseInt((String) busiDateSome.get(0));
					nm = Integer.parseInt((String) busiDateSome.get(1));
					strym = String.valueOf(ny) + "-" + String.valueOf(nm) + "-"
							+ "01";
				}
				String strwhere1 = " salaryhistory.A00Z2=" + Sql_switcher.dateValue(strym)
						+ " and salaryhistory.A00Z3=" + nc
						+ " and salaryhistory.salaryid=" + salaryid + "";
				// 判断历史表中是否有历史数据
				boolean temp = this.salaryTemplateBo.hasRecordByTable(
						"salaryhistory", "salaryid", " where " + strwhere1);
				if (!temp) {
					// 归档表
					tablename = "salaryarchive";
				}
				/** 更新串 */
				StringBuffer strupdate = new StringBuffer();
				StringBuffer field_str = new StringBuffer("");
				// 读写权限允许重新导入
				String read_field = ctrlparam
						.getValue(SalaryCtrlParamBo.READ_FIELD);
				if (read_field == null || "".equals(read_field))
					read_field = "0";
				for (int i = 0; i < itemlist.size(); i++) {
					String _itemid = ((String) itemlist.get(i)).toLowerCase()
							.trim();
					if (DataDictionary.getFieldItem(_itemid) != null) {
						if ("0".equals(read_field)) {
							if (!"2"
									.equals(this.userview.analyseFieldPriv(_itemid)))
								continue;
						} else {
							if (!"2"
									.equals(this.userview.analyseFieldPriv(_itemid))
									&& !"1"
											.equals(userview.analyseFieldPriv(_itemid)))
								continue;
						}
					}

					strupdate.append(this.gz_tablename);
					strupdate.append(".");
					strupdate.append(itemlist.get(i));
					strupdate.append("=");
					strupdate.append(tablename);
					strupdate.append(".");
					strupdate.append(itemlist.get(i));
					strupdate.append("`");

					field_str.append("," + itemlist.get(i));
				}// for loop end.

				if (strupdate.length() > 0) {
					strupdate.setLength(strupdate.length() - 1);
				} else {
					return bflag;
				}

				DbWizard dbw = new DbWizard(conn);

				// 共享薪资类别，其他操作人员引入数据
				if (manager.length() > 0
						&& !userview.getUserName().equalsIgnoreCase(manager)) {
					String dbpres = templatevo.getString("cbase");
					// 应用库前缀
					String[] dbarr = StringUtils.split(dbpres, ",");
					for (int i = 0; i < dbarr.length; i++) {
						String pre = dbarr[i];
						// 连接串
						StringBuffer strjoin = new StringBuffer();
						strjoin.append(gz_tablename);
						strjoin.append(".A0100=");
						strjoin.append(tablename);
						strjoin.append(".A0100 and upper(");
						strjoin.append(gz_tablename);
						strjoin.append(".NBASE)='" + pre.toUpperCase() + "'");

						// 条件串
						StringBuffer strwhere = new StringBuffer(" 1=1 and ");
						strwhere.append("" + tablename + ".A00Z2=");
						strwhere.append(strym);
						strwhere.append(" and " + tablename + ".A00Z3=");
						strwhere.append(nc);
						strwhere.append(" and " + tablename + ".salaryid=");
						strwhere.append(this.salaryid);
						
						strwhere.append(" and " + tablename + ".A00Z1=(select MAX(aa.A00Z1) from "  + tablename + " aa where "+gz_tablename+".A0100=aa.A0100 and upper("+gz_tablename+".NBASE)=upper(aa.NBASE) and "+strwhere.toString().replaceAll(tablename, "aa")+" GROUP BY aa.A0100 ,upper(aa.NBASE))");

						// 获取可执行引入操作的人员记录条件 共享、权限、是否需要审批等
						strwhere.append(" and " + getConditionSql() + " ");

						StringBuffer stdwhere = new StringBuffer(
								" exists ( select null from " + tablename
										+ " where  " + strjoin.toString());
						stdwhere.append(" and " + strwhere.toString() + " )");

						dbw.updateRecord(gz_tablename, tablename, strjoin
								.toString(), strupdate.toString(), stdwhere
								.toString(), strwhere.toString());

					}
				} else {
					/** 连接串 */
					StringBuffer strjoin = new StringBuffer();
					strjoin.append(gz_tablename);
					strjoin.append(".A0100=");
					strjoin.append(tablename);
					strjoin.append(".A0100 and upper(");
					strjoin.append(gz_tablename);
					strjoin.append(".NBASE)=upper(");
					strjoin.append(tablename);
					strjoin.append(".NBASE) ");
					/** 条件串 */
					StringBuffer strwhere = new StringBuffer();
					strwhere.append(" 1=1 and ");
					strwhere.append("" + tablename + ".A00Z2=");
					strwhere.append(Sql_switcher.dateValue(strym));
					strwhere.append(" and " + tablename + ".A00Z3=");
					strwhere.append(nc);
					strwhere.append(" and " + tablename + ".salaryid=");
					strwhere.append(this.salaryid);
					strwhere.append(" and " + tablename + ".A00Z1=(select MAX(aa.A00Z1) from "  + tablename + " aa where "+gz_tablename+".A0100=aa.A0100 and upper("+gz_tablename+".NBASE)=upper(aa.NBASE) and "+strwhere.toString().replaceAll(tablename, "aa")+" GROUP BY aa.A0100 ,upper(aa.NBASE))");

					// 获取可执行引入操作的人员记录条件 共享、权限、是否需要审批等
					strwhere.append(" and " + getConditionSql() + " ");

					StringBuffer stdwhere = new StringBuffer(
							" exists ( select null from " + tablename
									+ " where  " + strjoin.toString());
					stdwhere.append(" and " + strwhere.toString() + " )");

					dbw.updateRecord(gz_tablename, tablename, strjoin
							.toString(), strupdate.toString(), stdwhere
							.toString(), strwhere.toString());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				bflag = false;
				throw GeneralExceptionHandler.Handle(ex);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			bflag = false;
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}

	/**
	 * 提成工资重新导入（无则新增；有则修改；多则删除）
	 * 
	 * @param itemlist
	 * @param busiDate
	 *            业务日期次数
	 * @throws GeneralException
	 */
	private void reImportRoyaltyData(ArrayList itemlist, LazyDynaBean busiDate)
			throws GeneralException {
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			HashMap map = this.salaryAccountBo
					.getMaxYearMonthCount(null, false);
			// 当前月
			String currym = (String) map.get("ym");
			// 当前次数
			String currcount = (String) map.get("count");
			String[] temps = currym.split("-");
			String year = temps[0];
			String month = temps[1];
			// 提成工资子集
			String royalty_setid = ctrlparam.getValue(
					SalaryCtrlParamBo.ROYALTIES, "setid");
			// 计划日期
			String royalty_date = ctrlparam.getValue(
					SalaryCtrlParamBo.ROYALTIES, "date");
			// 周期
			String royalty_period = ctrlparam.getValue(
					SalaryCtrlParamBo.ROYALTIES, "period");
			// 关联指标
			String royalty_relation_fields = ctrlparam.getValue(
					SalaryCtrlParamBo.ROYALTIES, "relation_fields");
			String strExpression = ctrlparam.getValue(
					SalaryCtrlParamBo.ROYALTIES, "strExpression");
			String whl = "";// 查询条件
			String whl2 = "";// 条件串
			String strpre = null;
			String strset = null;
			String strc = null;
			// 获取查询条件
			if (strExpression != null && strExpression.length() > 0) {
				YksjParser yp = new YksjParser(this.userview, DataDictionary
						.getFieldList(royalty_setid, 1), YksjParser.forNormal,
						YksjParser.LOGIC, YksjParser.forPerson, "Ht", "");
				yp.run_where(strExpression);
				whl = yp.getSQL();
			}
			/*
			 * 如果需要审批，则仅导入起草和驳回的记录
			 */
			String flow_flag = this.ctrlparam.getValue(
					SalaryCtrlParamBo.FLOW_CTRL, "flag"); // 1:需要审批
			if ("1".equals(flow_flag)) {
				whl2 += " and " + gz_tablename + "." + "sp_flag in('01','07')";
			}
			String dbpres = templatevo.getString("cbase");
			// 应用库前缀
			String[] dbarr = StringUtils.split(dbpres, ",");
			// 关联的指标
			ArrayList relationfieldList = new ArrayList();
			String relation_str = "";
			if (royalty_relation_fields.length() > 0) {
				temps = royalty_relation_fields.toLowerCase().split(",");
				for (int i = 0; i < temps.length; i++) {
					if (temps[i].trim().length() > 0
							&& DataDictionary.getFieldItem(temps[i].trim()) != null) {
						relationfieldList.add(DataDictionary
								.getFieldItem(temps[i].trim().toLowerCase()));
					}
				}
			}

			// 删除多余人员
			FieldItem item = null;
			StringBuffer sql = new StringBuffer("");
			for (int i = 0; i < dbarr.length; i++) {
				sql.setLength(0);
				strpre = dbarr[i];
				strc = strpre + royalty_setid;
				sql.append("delete from " + this.gz_tablename
						+ " where lower(nbase)='" + strpre.toLowerCase()
						+ "' and  not exists(select null from " + strc
						+ " where ");
				sql.append(" " + this.gz_tablename + ".a0100=" + strc
						+ ".a0100 ");
				for (int j = 0; j < relationfieldList.size(); j++) {
					item = (FieldItem) relationfieldList.get(j);
					sql.append(" and  " + this.gz_tablename + "."
							+ item.getItemid() + "=" + strc + "."
							+ item.getItemid());
				}

				int[] months = getMonth(month, royalty_period);// 获取月份
				if ("1".equals(royalty_period)) // 周期 (1|2|3|4)=( 月|季|半年|年)
					sql.append(" and  " + Sql_switcher.year(royalty_date) + "="
							+ year + " and " + Sql_switcher.month(royalty_date)
							+ "=" + month);
				if ("2".equals(royalty_period) || "3".equals(royalty_period)) // 周期(1|2|3|4)=(月|季|半年|年)
				{
					sql.append(" and  " + Sql_switcher.year(royalty_date) + "="
							+ year + " and " + Sql_switcher.month(royalty_date)
							+ " in ( 100");
					for (int n = 0; n < months.length; n++) {
						sql.append("," + months[n]);
					}
					sql.append(" )");
				}
				if ("4".equals(royalty_period)) // 周期 (1|2|3|4)=( 月|季|半年|年)
					sql.append(" and  " + Sql_switcher.year(royalty_date) + "="
							+ year);

				if (whl != null && whl.length() > 0)
					sql.append(" and ( " + whl + " )");
				// 获得薪资发放前台过滤条件和当前用户的可操作范围SQL
				sql.append("  ) " + whl2
						+ this.salaryTemplateBo.getFilterAndPrivSql_ff());
				dao.update(sql.toString());
			}

			// 新增人员
			StringBuffer buf = new StringBuffer("");
			String pay_flag = ctrlparam.getValue(SalaryCtrlParamBo.PAY_FLAG);
			/** 年月和次数 */
			String ym = (String) busiDate.get("date");
			String szC = (String) busiDate.get("count");
			szC = szC != null && szC.trim().length() > 0 ? szC : "1";
			boolean payFlag_isExist = false;

			// 获取薪资项目列表,GzItemVo类对象
			ArrayList gzitemlist = this.salaryTemplateBo.getSalaryItemList("",
					this.salaryid + "", 3);

			for (int e = 0; e < gzitemlist.size(); e++) {
				GzItemVo itemvo = (GzItemVo) gzitemlist.get(e);
				if (itemvo.getInitflag() == 0)
					continue;
				if (pay_flag != null
						&& pay_flag.equalsIgnoreCase(itemvo.getFldname()))
					payFlag_isExist = true;
			}
			// 当前薪资类别涉及到的子集列表
			ArrayList setlist = this.salaryTemplateBo.searchSetList(salaryid);

			if (!(this.manager.length() > 0 && !this.userview.getUserName()
					.equalsIgnoreCase(this.manager))) {
				for (int i = 0; i < dbarr.length; i++) {
					strpre = dbarr[i];
					buf.setLength(0);
					String strlst = "";
					for (int j = 0; j < setlist.size(); j++) {
						strset = (String) setlist.get(j);
						if ("A01".equalsIgnoreCase(strset)) {
							strlst += "," + getInsFieldSQL(strset, pay_flag);
						}
						if (strset.equalsIgnoreCase(royalty_setid)) {
							strlst += "," + getInsFieldSQL(strset, pay_flag);
						}
					}
					if (strlst.length() > 0)
						strlst = strlst.substring(1);

					buf.append("insert into ");
					buf.append(this.gz_tablename);
					buf.append(" (userflag,nbase,A00Z2,A00Z3,A00Z0,A00Z1,sp_flag,");
					if (this.manager.length() > 0)
						buf.append("sp_flag2,");
					if (payFlag_isExist && pay_flag.length() != 0) {

						buf.append(pay_flag);
						buf.append(",");
					}
					buf.append(strlst);
					buf.append(") select '");
					buf.append(this.userview.getUserName());
					buf.append("','");
					buf.append(strpre.toUpperCase());
					buf.append("',");
					buf.append(Sql_switcher.dateValue(ym));
					buf.append(",");
					buf.append(szC);
					buf.append(",");
					buf.append(Sql_switcher.dateValue(ym));
					buf.append(",");
					buf.append(strpre + royalty_setid + ".i9999");
					buf.append(",'");
					buf.append("01");// 起草
					buf.append("',");
					if (this.manager.length() > 0)
						buf.append("'01',");
					if (payFlag_isExist && pay_flag.length() != 0) {
						buf.append("'");
						buf.append("0");// 正常发薪
						buf.append("',");
					}
					strlst = strlst.replaceAll("A0100", strpre + "A01.A0100");
					buf.append(strlst);

					strc = strpre + royalty_setid;

					buf.append(" from ");
					buf.append(strc + "," + strpre + "A01");
					buf.append(" where " + strc + ".a0100=" + strpre
							+ "A01.a0100 ");
					int[] months = getMonth(month, royalty_period);
					if ("1".equals(royalty_period)) // 周期 (1|2|3|4)=( 月|季|半年|年)
						buf.append(" and  " + Sql_switcher.year(royalty_date)
								+ "=" + year + " and "
								+ Sql_switcher.month(royalty_date) + "="
								+ month);
					if ("2".equals(royalty_period)
							|| "3".equals(royalty_period)) // 周期 (1|2|3|4)=(
					// 月|季|半年|年)
					{
						buf.append(" and  " + Sql_switcher.year(royalty_date)
								+ "=" + year + " and "
								+ Sql_switcher.month(royalty_date)
								+ " in ( 100");
						for (int n = 0; n < months.length; n++) {
							buf.append("," + months[n]);
						}
						buf.append(" )");
					}
					if ("4".equals(royalty_period)) // 周期 (1|2|3|4)=( 月|季|半年|年)
						buf.append(" and  " + Sql_switcher.year(royalty_date)
								+ "=" + year);

					if (whl != null && whl.length() > 0)
						buf.append(" and ( " + whl + " )");

					buf.append("and  not exists ( select null from "
							+ this.gz_tablename + " where lower(nbase)='"
							+ strpre.toLowerCase() + "' ");
					buf.append(" and " + this.gz_tablename + ".a0100=" + strc
							+ ".a0100 ");
					for (int j = 0; j < relationfieldList.size(); j++) {
						item = (FieldItem) relationfieldList.get(j);
						buf.append(" and  " + this.gz_tablename + "."
								+ item.getItemid() + "=" + strc + "."
								+ item.getItemid());
					}
					buf.append(" ) ");
					dao.update(buf.toString());
				}
			}

			// 修改
			String read_field = this.ctrlparam
					.getValue(SalaryCtrlParamBo.READ_FIELD);
			if (read_field == null || "".equals(read_field))
				read_field = "0";
			for (int i = 0; i < dbarr.length; i++) {

				strpre = dbarr[i];
				for (int j = 0; j < itemlist.size(); j++) {
					sql.setLength(0);
					String _itemid = ((String) itemlist.get(j)).toLowerCase()
							.trim();
					item = DataDictionary.getFieldItem(_itemid);
					if (item != null) {
						if ("0".equals(read_field)) {
							if (!"2".equals(userview.analyseFieldPriv(_itemid)))
								continue;
						} else {
							if (!"2".equals(userview.analyseFieldPriv(_itemid))
									&& !"1"
											.equals(userview.analyseFieldPriv(_itemid)))
								continue;
						}

						if ("A01".equalsIgnoreCase(item.getFieldsetid())
								|| item.getFieldsetid().equalsIgnoreCase(
										royalty_setid)) {
							sql.append("update " + this.gz_tablename + " set "
									+ _itemid + "=(select " + _itemid
									+ " from " + strpre + item.getFieldsetid()
									+ " where  ");
							if ("A01".equalsIgnoreCase(item.getFieldsetid())) {

								sql.append(" " + this.gz_tablename + ".a0100="
										+ strpre + "A01.a0100 ");

							} else {

								// 通过a0100和i9999来筛选
								// "+this.gz_tablename+".a00z1="+strpre+item.getFieldsetid()+".i9999
								sql.append(" " + this.gz_tablename + ".a0100="
										+ strpre + item.getFieldsetid()
										+ ".a0100  and " + this.gz_tablename
										+ ".nbase='" + strpre.toUpperCase()
										+ "'");
								for (int e = 0; e < relationfieldList.size(); e++) {
									item = (FieldItem) relationfieldList.get(e);
									sql.append(" and  " + this.gz_tablename
											+ "." + item.getItemid() + "="
											+ strpre + royalty_setid + "."
											+ item.getItemid());
								}

							}
							sql.append(" ) where lower(nbase)='"
									+ strpre.toLowerCase() + "' " + whl2
									+ " and exists (select null from " + strpre
									+ item.getFieldsetid() + " where  ");
							sql
									.append(" " + this.gz_tablename + ".a0100="
											+ strpre + item.getFieldsetid()
											+ ".a0100 ");
							if (item.getFieldsetid().equalsIgnoreCase(
									royalty_setid)) {
								for (int e = 0; e < relationfieldList.size(); e++) {
									item = (FieldItem) relationfieldList.get(e);
									sql.append(" and  " + this.gz_tablename
											+ "." + item.getItemid() + "="
											+ strpre + royalty_setid + "."
											+ item.getItemid());
								}
							}
							sql.append("  ) ");
							dao.update(sql.toString());

						}
					} else
						continue;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 删除不在条件范围中的人员
	 */
	private void delNoConditionData() {
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			/** 导入数据 */
			String dbpres = this.templatevo.getString("cbase");
			/** 应用库前缀 */
			String[] dbarr = StringUtils.split(dbpres, ",");
			String flag = this.ctrlparam.getValue(SalaryCtrlParamBo.COND_MODE,
					"flag"); // "":没条件 0：简单条件 1：复杂条件
			String aflag = this.ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,
					"flag"); // 人员范围权限过滤标志 1：有

			String cond = this.templatevo.getString("cond");
			String cexpr = this.templatevo.getString("cexpr");
			String sql = "";
			ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
					Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			/** 需要审批,仅导入起草和驳回记录 */
			String flow_flag = this.ctrlparam.getValue(
					SalaryCtrlParamBo.FLOW_CTRL, "flag"); // 1:需要审批
			String whl2 = "";
			if ("1".equals(flow_flag)) {
				whl2 += " and " + gz_tablename + "." + "sp_flag in('01','07')";
			}

			for (int i = 0; i < dbarr.length; i++) {
				String pre = dbarr[i];

				if (aflag != null && "1".equals(aflag)) {
					String asql = "delete from " + gz_tablename
							+ " where upper(nbase)='" + pre.toUpperCase()
							+ "' and a0100 not in (select a0100 "
							+ this.userview.getPrivSQLExpression(pre, false)
							+ " )" + whl2;
					dao.delete(asql, new ArrayList());
				}

				if (flag != null && "0".equals(flag) && cond.length() > 0) // 0：简单条件
				{
					FactorList factor = new FactorList(cexpr, cond, pre, false,
							false, true, 1, "su");
					String strSql = "";
					if (factor.size() > 0) {
						strSql = factor.getSqlExpression();
						sql = "delete from " + gz_tablename
								+ " where upper(nbase)='" + pre.toUpperCase()
								+ "' and a0100 ";
						sql += "not in (select " + pre + "a01.a0100 " + strSql
								+ " )" + whl2;
						dao.delete(sql, new ArrayList());
					}
				}

				if (flag != null && "1".equals(flag) && cond.length() > 0) // 1：复杂条件
				{

					int infoGroup = 0; // forPerson 人员
					int varType = 8; // logic

					String whereIN = "select a0100 from " + pre + "A01";
					alUsedFields.addAll(this.salaryTemplateBo
							.getMidVarItemList(String.valueOf(salaryid)));
					YksjParser yp = new YksjParser(this.userview, alUsedFields,
							YksjParser.forSearch, varType, infoGroup, "Ht", pre
									.toString());
					YearMonthCount ymc = null;
					yp.run_Where(cond, ymc, "", "hrpwarn_result", dao, whereIN,
							this.conn, "A", null);
					String tempTableName = yp.getTempTableName();
					String w = yp.getSQL();
					if (w != null && w.trim().length() > 0) {
						sql = "delete from " + gz_tablename
								+ " where upper(nbase)='" + pre.toUpperCase()
								+ "' and a0100 ";
						sql += "not ";
						sql += " in (select a0100 from " + tempTableName
								+ " where " + w + " )" + whl2;
						dao.delete(sql, new ArrayList());
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 求插入的更新串
	 * 
	 * @param strset
	 * @param pay_flag
	 * @return
	 */
	private String getInsFieldSQL(String strset, String pay_flag) {
		ArrayList gzitemlist = this.salaryTemplateBo.getSalaryItemList("",
				this.salaryid + "", 3);
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < gzitemlist.size(); i++) {
			GzItemVo itemvo = (GzItemVo) gzitemlist.get(i);
			if (!(strset.equalsIgnoreCase(itemvo.getSetname())))
				continue;
			if (itemvo.getInitflag() == 0)
				continue;
			if (pay_flag != null
					&& pay_flag.equalsIgnoreCase(itemvo.getFldname()))
				continue;
			buf.append(itemvo.getFldname());
			buf.append(",");
		}
		if (buf.length() > 0)
			buf.setLength(buf.length() - 1);
		return buf.toString();
	}

	/**
	 * 求薪资数据各子集提交方式,数据提交至档案库USRAXX
	 * 
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getSubmitTypeList() throws GeneralException {
		ArrayList list = new ArrayList();
		ArrayList gzitemlist = this.salaryTemplateBo.getSalaryItemList("",
				this.salaryid + "", 3);
		try {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < gzitemlist.size(); i++) {
				GzItemVo itemvo = (GzItemVo) gzitemlist.get(i);
				String setid = itemvo.getSetname();
				if (setid.charAt(0) != 'A')
					continue;
				if ("A00".equalsIgnoreCase(setid))
					continue;
				if (buf.indexOf(setid) == -1) {
					buf.append(setid);
					buf.append(",");
				}
			}// for i loop end.
			SalaryLProgramBo lpbo = new SalaryLProgramBo(this.templatevo
					.getString("lprogram"));
			HashMap map = lpbo.getSubmitMap();
			String[] seta = StringUtils.split(buf.toString(), ",");

			for (int i = 0; i < seta.length; i++) {
				String setid = seta[i];
				FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
				if (fieldset == null)
					continue;
				if ("0".equalsIgnoreCase(fieldset.getUseflag()))
					continue;
				LazyDynaBean dynabean = new LazyDynaBean();
				dynabean.set("setid", setid);
				dynabean.set("name", fieldset.getCustomdesc());
				String type = (String) map.get(setid);
				if (type == null || type.length() == 0) {
					if ("0".equals(fieldset.getChangeflag())) {
						dynabean.set("type", "2");
					} else
						dynabean.set("type", "1");
				} else
					dynabean.set("type", type);
				list.add(dynabean);
			}// for i loop end.
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}

	/**
	 * 获取月份数组
	 * 
	 * @param month
	 * @param royalty_period
	 * @return
	 */
	private int[] getMonth(String month, String royalty_period) {
		int[] months = null;
		int a_month = Integer.parseInt(month);
		if ("2".equals(royalty_period)) // 季
		{
			months = new int[3];
			if (a_month <= 3) {
				months[0] = 1;
				months[1] = 2;
				months[2] = 3;
			} else if (a_month > 3 && a_month <= 6) {
				months[0] = 4;
				months[1] = 5;
				months[2] = 6;
			} else if (a_month > 6 && a_month <= 9) {
				months[0] = 7;
				months[1] = 8;
				months[2] = 9;
			} else if (a_month > 9 && a_month <= 12) {
				months[0] = 10;
				months[1] = 11;
				months[2] = 12;
			}
		} else if ("3".equals(royalty_period)) // 周期 (1|2|3|4)=( 月|季|半年|年)
		{
			months = new int[6];
			if (a_month <= 6) {
				months[0] = 1;
				months[1] = 2;
				months[2] = 3;
				months[3] = 4;
				months[4] = 5;
				months[5] = 6;
			} else {
				months[0] = 7;
				months[1] = 8;
				months[2] = 9;
				months[3] = 10;
				months[4] = 11;
				months[5] = 12;
			}
		}
		return months;
	}

	/**
	 * 薪资审核中批量引入数据
	 * 
	 * @param map
	 * @param importtype
	 *            =1同月上次 =2上月同次数据 =3档案库数据 =4某年某月某次
	 * @param items
	 *            引入的薪资项目列表
	 * @param busiDate
	 *            业务日期次数
	 * @param busiDateSome
	 *            importtype=4 年月 次的集合
	 * @return
	 * @throws GeneralException
	 */
	public boolean batchImport_history(LazyDynaBean busiDate,
			String importtype, ArrayList items, ArrayList busiDateSome)
			throws GeneralException {
		if ("3".equalsIgnoreCase(importtype))
			return batchImportFromArchive_history(busiDate, importtype, items);
		else
			return batchImportFromHisGzTable_history(busiDate, importtype,
					items, busiDateSome);

	}

	/**
	 * 批量从档案库取得已发放的数据
	 * 
	 * @param busiDate
	 *            业务日期
	 * @param importtype
	 *            =3档案库数据
	 * @param itemlist
	 *            引入的薪资项目列表
	 * @return
	 * @throws GeneralException
	 */
	private boolean batchImportFromArchive_history(LazyDynaBean busiDate,
			String importtype, ArrayList itemlist) throws GeneralException {
		boolean bflag = true;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			/** 求得当前处理年月和次数 */
			String currym = (String) busiDate.get("date");
			String currcount = (String) busiDate.get("count");
			// 拼接条件语句
			StringBuffer strwhere = new StringBuffer(
					" ( salaryhistory.curr_user='"
							+ this.userview.getUserId()
							+ "' and ( salaryhistory.sp_flag='02' or salaryhistory.sp_flag='07' ) )");
			strwhere.append(" and salaryhistory.a00z2="
					+ Sql_switcher.dateValue(currym)
					+ " and salaryhistory.a00z3=" + currcount
					+ " and salaryhistory.salaryid=" + this.salaryid);

			String items = Arrays.toString(itemlist.toArray());
			items = items.toUpperCase();
			ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
					Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			FieldItem Field = null;
			HashMap allFieldMap = new HashMap();
			for (int i = 0; i < allUsedFields.size(); i++) {
				Field = (FieldItem) allUsedFields.get(i);
				allFieldMap.put(Field.getItemdesc().toLowerCase(), Field);
			}
			// 将导入当前记录数据的项目先批量处理
			ArrayList itemList = new ArrayList();// 引入非当前记录值的指标集合
			HashMap setMap = new HashMap();// 引入当前记录值的指标集合
			this.setGz_tablename("salaryhistory");//薪资审批进来把临时表改成历史表 zhaoxg add 2016-9-18
			this.salaryTemplateBo.setGz_tablename("salaryhistory");//薪资审批进来把临时表改成历史表 zhaoxg add 2016-9-18
			// 薪资集合
//			ArrayList gzitemlist = this.salaryTemplateBo.getSalaryItemList("", this.salaryid + "", 1);
			// 是否有岗位名称
			Boolean isE01A1 = new Boolean(false);
			// 获得引入指标列表中当前记录值和非当前记录值的指标集合
			this.getImportItemList(isE01A1, itemList, setMap, items,
					allFieldMap);
			String dbpres = this.templatevo.getString("cbase");
			String strpre = null;
			/** 应用库前缀 */
			String[] dbarr = StringUtils.split(dbpres, ",");
			String tempWhere = strwhere.toString();
			for (int j = 0; j < dbarr.length; j++) {
				strpre = dbarr[j];

				strwhere.setLength(0);
				strwhere.append(tempWhere);
				strwhere.append(" and lower(nbase)='" + strpre.toLowerCase()
						+ "'");

				String importMenSql_where = ""; // 算法分析器计算时需限定的人员范围，减少计算人数，提高性能
				importMenSql_where += " select A0100 from ";
				importMenSql_where += gz_tablename;
				importMenSql_where += " where  1=1 and ";
				importMenSql_where += strwhere;

				// 批量导入当前记录数据的项目
				this.salaryAccountBo.batchImportGzItems(setMap, strwhere
						.toString(), strpre, "salaryhistory");

				if (isE01A1.booleanValue())// 更新人员岗位信息
				{
					String sql = "update " + gz_tablename
							+ " set E01A1=(select E01A1 from " + strpre
							+ "A01 where " + strpre + "A01.a0100="
							+ gz_tablename + ".a0100  ) where exists ";
					sql += " (select null from " + strpre + "A01 where "
							+ strpre + "A01.a0100=" + gz_tablename
							+ ".a0100  ) and lower(nbase)='"
							+ strpre.toLowerCase() + "' ";
					if (strwhere != null
							&& strwhere.toString().trim().length() > 0)
						sql += " and " + strwhere.toString();
					dao.update(sql);
				}

				/** 所有项目 */
				LazyDynaBean bean = null;
				for (int i = 0; i < itemList.size(); i++) {
					bean = (LazyDynaBean) itemList.get(i);
					String fieldsetid = (String) bean.get("fieldsetid");
					String itemid = (String) bean.get("itemid");

					int nlock = Integer.parseInt((String) bean.get("nlock")); // nlock=1,对系统项
					int ainit = Integer.parseInt((String) bean.get("initflag"));
					int nheap = Integer.parseInt((String) bean.get("heapflag"));
					String formula = (String) bean.get("formula");
					/** 单位指标或职位指标 */
					if (fieldsetid.charAt(0) != 'A') {
						this.salaryAccountBo.computingImportUnitItem(bean,
								strwhere.toString(), strpre, isE01A1, busiDate);
					} else {

						/** =0不锁,=1锁住 */
						if (nlock == 0 || "A01z0".equalsIgnoreCase(itemid)) {
							// 有记录才进行下一步计算
							RowSet rowSet = dao
									.search("select count(nbase) from salaryhistory where lower(nbase)='"
											+ strpre.toLowerCase()
											+ "' and "
											+ strwhere.toString());
							if (rowSet.next()) {
								if (rowSet.getInt(1) > 0) {
									switch (ainit) {
									case 0:// 清零项，不管它
										break;
									case 1: // 累积项
										this.salaryAccountBo
												.computingImportItem(bean,
														strwhere.toString(),
														strpre,
														importMenSql_where
																.toString(),
														busiDate);
										break;
									case 2: // 导入项
										this.salaryAccountBo
												.computingImportItem(bean,
														strwhere.toString(),
														strpre,
														importMenSql_where
																.toString(),
														busiDate);
										break;
									case 3: // 系统项
										this.salaryAccountBo
												.computingImportItem(bean,
														strwhere.toString(),
														strpre,
														importMenSql_where
																.toString(),
														busiDate);
										break;
									}
								}
							}
							if (rowSet != null)
								rowSet.close();

						}// nlock end.
					}

				}// for i loop end.
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			bflag = false;
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}

	/**
	 * 批量从薪资历史数据表取得已发放的数据(审批引入功能)
	 * 
	 * @param map
	 * @param importtype
	 *            =1同月上次 =2上月同次数据 =4某年某月某次
	 * @param items
	 *            引入的薪资项目列表
	 * @param year
	 * @param month
	 * @param count
	 * @return
	 * @throws GeneralException
	 */
	private boolean batchImportFromHisGzTable_history(LazyDynaBean busiDate,
			String importtype, ArrayList items, ArrayList busiDateSome)
			throws GeneralException {
		boolean bflag = true;
		try {
			String strym = null;
			int nc = 0, ny = 0, nm = 0;
			// 取得当前处理的年月、次数
			String currym = (String) busiDate.get("date");
			String currcount = (String) busiDate.get("count");
			if ("1".equals(importtype))// =1同月上次
			{
				nc = Integer.parseInt(currcount) - 1;
				strym = currym;
			} else if ("2".equals(importtype))// 上月同次
			{
				nc = Integer.parseInt(currcount);
				String[] tmp = StringUtils.split(currym, "-");
				ny = Integer.parseInt(tmp[0]);
				nm = Integer.parseInt(tmp[1]);
				if (nm == 1) {
					ny = ny - 1;
					nm = 12;
				} else {
					nm = nm - 1;
				}
				strym = String.valueOf(ny) + "-" + String.valueOf(nm) + "-"
						+ "01";
			} else if ("4".equals(importtype))// 指定年月、次
			{
				nc = Integer.parseInt((String) busiDateSome.get(2));
				ny = Integer.parseInt((String) busiDateSome.get(0));
				nm = Integer.parseInt((String) busiDateSome.get(1));
				strym = String.valueOf(ny) + "-" + String.valueOf(nm) + "-"
						+ nc;
			}
			/** 更新串 */
			StringBuffer strupdate = new StringBuffer();
			String read_field = this.ctrlparam
					.getValue(SalaryCtrlParamBo.READ_FIELD);
			if (read_field == null || "".equals(read_field))
				read_field = "0";
			for (int i = 0; i < items.size(); i++) {
				String _itemid = ((String) items.get(i)).toLowerCase().trim();
				if (DataDictionary.getFieldItem(_itemid) != null) {
					if ("0".equals(read_field)) {
						if (!"2"
								.equals(this.userview.analyseFieldPriv(_itemid)))
							continue;
					} else {
						if (!"2"
								.equals(this.userview.analyseFieldPriv(_itemid))
								&& !"1"
										.equals(this.userview.analyseFieldPriv(_itemid)))
							continue;
					}
				}

				strupdate.append("salaryhistory");
				strupdate.append(".");
				strupdate.append(items.get(i));
				strupdate.append("=");
				strupdate.append("a");
				strupdate.append(".");
				strupdate.append(items.get(i));
				strupdate.append("`");
			}
			if (strupdate.length() > 0)
				strupdate.setLength(strupdate.length() - 1);
			else
				return bflag;

			{
				/** 连接串 */
				StringBuffer strjoin = new StringBuffer();
				strjoin.append("salaryhistory.A0100=");
				strjoin.append("a");
				strjoin.append(".A0100 and upper(");
				strjoin.append("salaryhistory.NBASE)=upper(");
				strjoin.append("a");
				strjoin.append(".NBASE) ");

				/** 条件串 */
				StringBuffer strwhere = new StringBuffer();
				strwhere.append("a.A00Z2=");
				strwhere.append(Sql_switcher.dateValue(strym));
				strwhere.append(" and a.A00Z3=");
				strwhere.append(nc);
				strwhere.append(" and a.salaryid=");
				strwhere.append(this.salaryid);

				DbWizard dbw = new DbWizard(this.conn);
				StringBuffer stdwhere = new StringBuffer(
						" salaryhistory.a00z2="
								+ Sql_switcher.dateValue(currym)
								+ " and salaryhistory.a00z3=" + currcount
								+ " and salaryhistory.salaryid="
								+ this.salaryid);
				stdwhere
						.append(" and ( salaryhistory.curr_user='"
								+ this.userview.getUserId()
								+ "' and ( salaryhistory.sp_flag='02' or salaryhistory.sp_flag='07' ) )");

				dbw.updateRecord("salaryhistory", "salaryhistory a", strjoin
						.toString(), strupdate.toString(), stdwhere.toString(),
						strwhere.toString());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			bflag = false;
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}

	/**
	 * 审批中批量引入数据后需同步临时表中的相应记录数据
	 * 
	 * @param itemlist
	 * @throws GeneralException
	 */
	public void batchUpdateTempData(ArrayList itemlist, LazyDynaBean busiDate)
			throws GeneralException {
		try {
			if (itemlist.size() == 0)
				return;
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer strwhere = new StringBuffer(
					" and ( salaryhistory.curr_user='"
							+ this.userview.getUserId()
							+ "' and ( salaryhistory.sp_flag='02' or salaryhistory.sp_flag='07' ) )");
			/** 求得当前处理年月和次数 */
			String currym = (String) busiDate.get("date");
			String currcount = (String) busiDate.get("count");
			strwhere.append(" and salaryhistory.a00z2="
					+ Sql_switcher.dateValue(currym)
					+ " and salaryhistory.a00z3=" + currcount
					+ " and salaryhistory.salaryid=" + this.salaryid);

			ArrayList userFlagList = new ArrayList();
			RowSet rowSet = null;
			if (this.manager != null && this.manager.length() > 0) {
				rowSet = dao
						.search("select distinct userflag from salaryhistory where 1=1 "
								+ strwhere.toString());
				if (rowSet.next())
					userFlagList.add(this.manager);
			} else {
				rowSet = dao
						.search("select distinct userflag from salaryhistory where 1=1 "
								+ strwhere.toString());
				while (rowSet.next())
					userFlagList.add(rowSet.getString(1));
			}

			StringBuffer up_str = new StringBuffer("");
			String read_field = this.ctrlparam
					.getValue(SalaryCtrlParamBo.READ_FIELD);
			if (read_field == null || "".equals(read_field))
				read_field = "0";
			for (int j = 0; j < userFlagList.size(); j++) {
				String userFlag = (String) userFlagList.get(j);
				String tableName = userFlag + "_salary_" + this.salaryid;
				up_str.setLength(0);
				StringBuffer strupdate = new StringBuffer();
				StringBuffer strupdate2 = new StringBuffer();
				for (int i = 0; i < itemlist.size(); i++) {
					String _itemid = ((String) itemlist.get(i)).toLowerCase()
							.trim();
					if (DataDictionary.getFieldItem(_itemid) != null) {
						if ("0".equals(read_field)) {
							if (!"2"
									.equals(this.userview.analyseFieldPriv(_itemid)))
								continue;
						} else {
							if (!"2"
									.equals(this.userview.analyseFieldPriv(_itemid))
									&& !"1"
											.equals(this.userview.analyseFieldPriv(_itemid)))
								continue;
						}
					}

					if (Sql_switcher.searchDbServer() != 2) // 不为oracle
					{
						strupdate.append("," + tableName);
						strupdate.append(".");
						strupdate.append(itemlist.get(i));
						strupdate.append("=");
						strupdate.append("salaryhistory");
						strupdate.append(".");
						strupdate.append(itemlist.get(i));
					} else {
						strupdate.append("," + tableName + "."
								+ itemlist.get(i));
						strupdate2.append(",salaryhistory." + itemlist.get(i));
					}
				}
				if (strupdate.length() == 0)
					continue;

				if (Sql_switcher.searchDbServer() != 2) // 不为oracle
				{
					up_str.append("update " + tableName + " set "
							+ strupdate.substring(1) + " from " + tableName
							+ ",salaryhistory");
					up_str
							.append(" where " + tableName
									+ ".a0100=salaryhistory.a0100 and upper("
									+ tableName
									+ ".nbase)=upper(salaryhistory.nbase )");
					up_str.append(" and " + tableName
							+ ".a00z0=salaryhistory.a00z0 and " + tableName
							+ ".a00z1=salaryhistory.a00z1 ");
					up_str.append(strwhere.toString());
				} else {
					up_str.append("update " + tableName + " set ("
							+ strupdate.substring(1) + ")=");
					up_str.append("(select " + strupdate2.substring(1)
							+ " from salaryhistory where " + tableName
							+ ".a0100=salaryhistory.a0100 and upper("
							+ tableName
							+ ".nbase)=upper(salaryhistory.nbase ) ");
					up_str.append(" and " + tableName
							+ ".a00z0=salaryhistory.a00z0 and " + tableName
							+ ".a00z1=salaryhistory.a00z1 ");
					up_str.append(strwhere.toString() + " ) where exists ");
					up_str.append(" ( select null from salaryhistory where "
							+ tableName
							+ ".a0100=salaryhistory.a0100 and upper("
							+ tableName
							+ ".nbase)=upper(salaryhistory.nbase ) ");
					up_str.append(" and " + tableName
							+ ".a00z0=salaryhistory.a00z0 and " + tableName
							+ ".a00z1=salaryhistory.a00z1 ");
					up_str.append(strwhere.toString() + " ) ");

				}
				dao.update(up_str.toString());

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public String getGz_tablename() {
		return gz_tablename;
	}

	public void setGz_tablename(String gz_tablename) {
		this.gz_tablename = gz_tablename;
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

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public RecordVo getTemplatevo() {
		return templatevo;
	}

	public void setTemplatevo(RecordVo templatevo) {
		this.templatevo = templatevo;
	}

}
