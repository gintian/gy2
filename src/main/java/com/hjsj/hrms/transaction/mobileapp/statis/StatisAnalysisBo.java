package com.hjsj.hrms.transaction.mobileapp.statis;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.CombineFactor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * 
 * <p> Title: SetScopeBo </p>
 * <p> Description:设置范围BO </p>
 * <p> Company: hjsj </p>
 * <p> create time: 2013-11-28 上午10:56:40 </p>
 * 
 * @author yangj
 * @version 1.0
 */
public class StatisAnalysisBo {

	private UserView userView;
	private Connection conn;

	/** 选择的人员库 Usr,OTh, */
	private String mBase = "";
	/** 常用条件 */
	private String mCondID = "";
	/** 所选的组织机构 0101,101,0101 */
	private String mOrg = "";
	/** 上次查询结果标示 */
	private String lastResult = "";

	public void setmBase(String mBase) {
		this.mBase = mBase;
	}

	public void setmCondID(String mCondID) {
		this.mCondID = mCondID;
	}

	public void setmOrg(String mOrg) {
		this.mOrg = mOrg;
	}

	public void setLastResult(String lastResult) {
		this.lastResult = lastResult;
	}

	/**
	 * 默认构造对象
	 */
	public StatisAnalysisBo() {
	}

	/**
	 * 构造全属性对象
	 * 
	 * @param userView
	 * @param conn
	 */
	public StatisAnalysisBo(UserView userView, Connection conn) {
		this.userView = userView;
		this.conn = conn;
	}

	/**
	 * 
	 * @Title: getCondStatList
	 * @Description: 通过不同的参数获得统计
	 * @param type
	 * @throws GeneralException
	 * @return ArrayList
	 * @throws
	 */
	protected ArrayList getCondStatList(int type) throws GeneralException {
		StringBuffer strsql = new StringBuffer();
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(conn);
		ArrayList list = new ArrayList();
		try {
			strsql.append("select id,name,type from lexpr where type='");
			strsql.append(type);
			strsql.append("' order by norder");
			/** 常用查询条件列表 */
			rs = dao.search(strsql.toString());
			// 增加请选择选项
			HashMap map = new HashMap();
			map.put("id", "");
			map.put("name", ResourceFactory.getProperty("label.select.dot"));
			list.add(map);
			while (rs.next()) {
				// 判断该ID是否可以显示
				if (userView.isHaveResource(IResourceConstant.LEXPR, rs.getString("id"))) {
					map = new HashMap();
					map.put("id", rs.getString("id"));
					map.put("name", rs.getString("name"));
					list.add(map);
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return list;
	}

	/**
	 * 
	 * @Title: GetQuickQueryField
	 * @Description: 获得人员库
	 * @throws GeneralException
	 * @return List
	 * @throws
	 */
	protected List getPersonnel() throws GeneralException {
		List libraryList = new ArrayList();
		try {
			// 获得库前缀
			List prelist = userView.getPrivDbList();
			if (prelist != null) {
				String libraryName = "";
				StringBuffer sBuffer = new StringBuffer();
				Map libraryMap = new HashMap();
				for (int i = 0, n = prelist.size(); i < n; i++) {
					// 根据库前缀查询库名
					libraryName = AdminCode.getCodeName("@@", (String) prelist.get(i));
					libraryMap.put("pre", (String) prelist.get(i));
					libraryMap.put("name", libraryName);
					libraryList.add(libraryMap);
					libraryMap = new HashMap();
					// 追加一个全部人员库
					sBuffer.append(prelist.get(i));
					sBuffer.append("`");
				}
				String pre = sBuffer.toString();
				libraryMap.put("pre", pre.substring(0, pre.length() - 1));
				// 全部人员库
				libraryMap.put("name", ResourceFactory.getProperty("mobileapp.emp.allPersonnel"));
				libraryList.add(libraryMap);
			}
		} catch (Exception ex) {
			throw GeneralExceptionHandler.Handle(ex);
		}
		return libraryList;

	}

	/**
	 * 
	 * @Title: getOneDirectory
	 * @Description:根据传入的 infokind 查询一级目录
	 * @param infokind
	 * @return
	 * @return List
	 * @throws GeneralException
	 */
	public List getOneDirectory(int infokind) throws GeneralException {
		StringBuffer strsql = new StringBuffer();
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(conn);
		ArrayList list = new ArrayList();
		try {
			strsql.append("select distinct categories from sname where infokind='");
			strsql.append(infokind);
			strsql.append("' order by categories");
			/** 一级目录列表 */
			rs = dao.search(strsql.toString());
			// 标示符，预防数据库中的字段为""和"null"出行两个默认分类
			boolean flag = true;
			while (rs.next()) {
				// 判断一级目录下的二级目录是否有值，没有值则不显示
				if (this.getTwoDirectory(rs.getString("categories"), infokind) != null) {
					HashMap map = new HashMap();
					// 判断是否需要显示默认分类
					if (flag && (rs.getString("categories") == null || "".equals(rs.getString("categories")))) {
						map.put("categories", ResourceFactory.getProperty("mobileapp.emp.categories.null"));
						flag = false;
						list.add(map);
					} else if (!(rs.getString("categories") == null || "".equals(rs.getString("categories")))) {
						map.put("categories", rs.getString("categories"));
						list.add(map);
					}
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return list;
	}

	/**
	 * 
	 * @Title: getOneDirectory
	 * @Description:查询一级目录(包含人员1，机构2，岗位3)
	 * @param infokind
	 * @return
	 * @return List
	 * @throws GeneralException
	 */
	public List getOneDirectory() throws GeneralException {
		StringBuffer strsql = new StringBuffer();
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(conn);
		ArrayList list = new ArrayList();
		// 标示map，记录是否已加载目录
		Map flagMap = new HashMap();
		// 描述
		String categories;
		// infokind：人员1，机构2，岗位3
		String infoKind;
		try {
			// 人员
			String employ = ResourceFactory.getProperty("mobileapp.statis.employ");
			// 机构（单位、部门）
			String institutions = ResourceFactory.getProperty("mobileapp.statis.institutions");
			// 岗位
			String post = ResourceFactory.getProperty("mobileapp.statis.post");
			// 查询SQL语句
			strsql.append("select s.categories,s.InfoKind from SName s order by s.InfoKind, s.snorder");
			/** 一级目录列表 */
			rs = dao.search(strsql.toString());	
			while (rs.next()) {
				// 判断一级目录下的二级目录是否有值，没有值则不显示
				categories = rs.getString("categories");
				HashMap map = new HashMap();
				if (categories == null || "".equals(categories)) {// 判断是否需要显示默认分类
					categories = ResourceFactory.getProperty("mobileapp.statis.categories.null");
				}
				infoKind = rs.getString("InfoKind");
				if ("1".equals(infoKind)) {// 人员
					categories = categories + "[" + employ + "]";
				} else if ("2".equals(infoKind)) {// 机构
					categories = categories + "[" + institutions + "]";
				} else if ("3".equals(infoKind)) {// 岗位
					categories = categories + "[" + post + "]";
				} else {
					continue;
				}
				if (!flagMap.containsKey(categories)) {// 判断是否已加载
					List towList = this.getTwoDirectory(categories);
					if ( towList!= null && towList.size() > 0) {
						flagMap.put(categories, "true");
						map.put("categories", categories);
						list.add(map);
					}
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return list;
	}

	/**
	 * 
	 * @Title: getTwoDirectory
	 * @Description: 二级目录(包含infokind：人员1，机构2，岗位3)
	 * @param categories
	 * @return List
	 * @throws GeneralException
	 */
	public List getTwoDirectory(String categories) throws GeneralException {
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(conn);
		ArrayList list = new ArrayList();
		StringBuffer strsql = new StringBuffer();
		try {
			// 人员
			String employ = ResourceFactory.getProperty("mobileapp.statis.employ");
			// 机构（单位、部门）
			String institutions = ResourceFactory.getProperty("mobileapp.statis.institutions");
			// 岗位
//			 String post = ResourceFactory.getProperty("mobileapp.statis.post");
			// 获得InfoKind
			categories = categories.substring(0, categories.length() - 1);
			categories = categories.replace("[", "`");
			String[] temporaryList = categories.split("`");
			categories = temporaryList[0];
			// infokind：人员1，机构2，岗位3
			String infoKind = temporaryList[1];
			if (employ.equals(infoKind)) {// 人员
				infoKind = "1";
			} else if (institutions.equals(infoKind)) {// 机构
				infoKind = "2";
			} else {// 岗位
				infoKind = "3";
			}
			// 查询语句
			strsql.append("select id,Name,type,InfoKind ");
			strsql.append("from sname ");
			strsql.append("where type<>'3' and InfoKind='" + infoKind + "' and ( categories = '");
			// 判断一级目录是否为默认目录或空
			if (categories == null || "".equals(categories) || categories.equals(ResourceFactory.getProperty("mobileapp.statis.categories.null")))
				strsql.append("' OR categories IS NULL");
			else
				strsql.append(categories + "' ");
			strsql.append(") order by snorder");
			/** 二级目录列表 */
			rs = dao.search(strsql.toString());
			while (rs.next()) {
				//验证权限
				if (userView.isHaveResource(IResourceConstant.STATICS, rs.getString("id"))) {
					if (rs.getString("Name") != null && rs.getString("Name").length() > 0) {
						HashMap map = new HashMap();
						map.put("id", rs.getString("id"));
						map.put("name", rs.getString("Name"));
						map.put("dim", rs.getString("type"));
						map.put("infokind", rs.getString("InfoKind"));
						list.add(map);
					}
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return list;
	}

	/**
	 * 
	 * @Title: getTwoDirectory
	 * @Description: 二级目录
	 * @param categories
	 * @param infokind
	 * @return List
	 * @throws GeneralException
	 */
	public List getTwoDirectory(String categories, int infokind) throws GeneralException {
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(conn);
		ArrayList list = new ArrayList();
		try {
			StringBuffer strsql = new StringBuffer();
			strsql.append("select id,Name,type from sname where infokind = '");
			strsql.append(infokind);
			strsql.append("' and categories ='");
			if (!(categories == null || "".equals(categories) 
					|| categories.equals(ResourceFactory.getProperty("mobileapp.emp.categories.null"))))
				strsql.append(categories);
			strsql.append("' order by snorder");
			/** 二级目录列表 */
			rs = dao.search(strsql.toString());
			while (rs.next()) {
				if (userView.isHaveResource(IResourceConstant.STATICS, rs.getString("id"))) {
					if (rs.getString("Name") != null && rs.getString("Name").length() > 0) {
						HashMap map = new HashMap();
						map.put("id", rs.getString("id"));
						map.put("name", rs.getString("Name"));
						map.put("dim", rs.getString("type"));
						list.add(map);
					}
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return list;
	}

	/**
	 * 
	 * 
	 * @Title: getOneDimStatisChart
	 * @Description: 一维图
	 * @param condid  查询条件ID
	 * @param infokind 1人员，2单位，3岗位
	 * @throws GeneralException
	 * @return List
	 * @throws
	 */
	public List getOneDimStatisChart(String condid, String infokind) throws GeneralException {
		List list = new ArrayList();
		boolean isresult = true;
		try {
			String commlexr = "";
			String commfacor = "";
//			String history = "";
			// 统计查询准备工作
			HashMap map = prepareHandle(condid, infokind);
			commlexr = (String) map.get("commlexr");
			commfacor = (String) map.get("commfacor");
//			history = (String) map.get("history");
			isresult = Boolean.parseBoolean((String) map.get("isresult"));
			list = this.getLexprData(mBase, condid, mOrg, infokind, isresult, commlexr, commfacor);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}

	/**
	 * 
	 * @Title: disposeOrganizationID
	 * @Description: 处理组织ID
	 * @param
	 * @return void
	 * @throws
	 */
	private void disposeOrganizationID() {
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(conn);
		try {
			if (mOrg.length() > 0) {
				String[] str = mOrg.split("`");
				StringBuffer strBuf = new StringBuffer();
				for (int i = 0, n = str.length; i < n; i++)
					strBuf.append("'" + str[i] + "'" + ",");
				String strSql = strBuf.toString();
				strSql = strSql.substring(0, strSql.length() - 1);
				strBuf.setLength(0);
				strBuf.append("select codesetid,codeitemid from organization where codeitemid in (");
				strBuf.append(strSql);
				strBuf.append(")");
				rs = dao.search(strBuf.toString());
				strBuf.setLength(0);
				while (rs.next()) {
					strBuf.append(rs.getString("codesetid"));
					strBuf.append(rs.getString("codeitemid"));
					strBuf.append(",");
				}
				mOrg = strBuf.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}

	/** 
	 * 
	 * @Title: getLexprData
	 * @Description: 一维图例
	 * @param userbase 人员库
	 * @param queryId 选择的统计项ID
	 * @param sqlSelect 组织ID
	 * @param infokind 1人员，2单位，3岗位
	 * @param bresult 是否查看历史记录，false是查看
	 * @param commlexpr 常用条件1
	 * @param commfactor 常用条件2
	 * @param @return
	 * @return List
	 * @throws GeneralException
	 */
	private List getLexprData(String userbase, String queryId, String sqlSelect, String infokind,
			boolean bresult, String commlexpr, String commfactor) throws GeneralException {
		String strFactor = "";
		String strLexpr = "";
		String strQuery = "";
		RowSet rs = null;
		RowSet rsValue = null;
		StringBuffer strsql = new StringBuffer();
		ContentDAO dao = new ContentDAO(conn);
		StatDataEncapsulation SDE = new StatDataEncapsulation();
		List list = new ArrayList();
		try {
			// 获得统计项
			strsql.append("select * from SLegend where id=");
			strsql.append(queryId);
			strsql.append(" order by norder");
			rs = dao.search(strsql.toString());
			int sum = 0;
			while (rs.next()) {
				strLexpr = rs.getString("lexpr");
				strFactor = rs.getString("factor") + "`";
				if (commlexpr != null && commfactor != null) {
					String[] style = getCombinLexprFactor(strLexpr, strFactor, commlexpr, commfactor);
					if (style != null && style.length == 2) {
						strLexpr = style[0];
						strFactor = style[1];
					}
				}
				boolean ishavehistory = false;
				if ("1".equals(rs.getString("flag")))
					ishavehistory = true;
				// 获取查询WHERE条件语句
				strQuery = SDE.getCondQueryString(strLexpr, strFactor, userbase, ishavehistory, userView.getUserName(), 
						sqlSelect, userView, infokind, bresult);
				if ("1".equals(infokind)) {
					StringBuffer sb = new StringBuffer();
					String tmpsql = ("select distinct " + userbase + "a01.a0100 as a0100" + strQuery);
					if (userbase.indexOf("`") == -1) {
						sb.append(tmpsql.replaceAll(userbase, userbase));
					} else {
						String[] tmpdbpres = userbase.split("`");
						for (int n = tmpdbpres.length - 1; n >= 0; n--) {
							String tmpdbpre = tmpdbpres[n];
							if (tmpdbpre.length() == 3) {
								if (sb.length() > 0) {
									sb.append(" union all " + tmpsql.replaceAll(userbase, tmpdbpre));
								} else {
									sb.append(tmpsql.replaceAll(userbase, tmpdbpre));
								}
							}
						}
					}
					strQuery = "select count(a0100) as lexprData from (" + sb.toString() + ") tt";
				} else if ("2".equals(infokind))
					strQuery = "select count(distinct b01.b0110) as lexprData " + strQuery;
				else if ("3".equals(infokind))
					strQuery = "select count(distinct k01.e01a1) as lexprData " + strQuery;
				rsValue = dao.search(strQuery.toString());
				// 保存该图例的统计数
				if (rsValue.next()) {
					HashMap map = new HashMap();
					map.put("id", rs.getString("Id") + "`" + rs.getString("nOrder") + "," + queryId);
					map.put("name", rs.getString("legend"));
					map.put("value", rsValue.getString("lexprdata"));
					list.add(map);
					sum += Integer.parseInt(rsValue.getString("lexprdata"));
				}
			}
			HashMap map = new HashMap();
			map.put("sum", String.valueOf(sum));
			list.add(map);
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
			PubFunc.closeResource(rsValue);
		}
		return list;
	}

	/**
	 * 
	 * @Title: getCombinLexprFactor
	 * @Description: 合并表达式
	 * @param lexpr
	 * @param factor
	 * @param seclexpr
	 * @param secfactor
	 * @return String[]
	 * @throws
	 */
	public String[] getCombinLexprFactor(String lexpr, String factor, String seclexpr, String secfactor) {
		String[] style = new String[2];
		ArrayList lexprFactor = new ArrayList();
		factor = PubFunc.keyWord_reback(factor);
		lexprFactor.add(lexpr + "|" + factor);
		lexprFactor.add(seclexpr + "|" + secfactor);
		CombineFactor combinefactor = new CombineFactor();
		String lexprFactorStr = combinefactor.getCombineFactorExpr(lexprFactor, 0);
		StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
		if (Stok.hasMoreTokens()) {
			style[0] = Stok.nextToken();
			style[1] = Stok.nextToken();
		}
		return style;
	}

	/**
	 * 
	 * @Title: getTwoDimStatisTable
	 * @Description: tiany add 二维表格统计查询
	 * @param @return
	 * @return List
	 * @throws GeneralException
	 * @throws
	 */
	public void getTwoDimStatisTable(String condid, String infokind, HashMap hm) throws GeneralException {
		boolean isresult = true;
		try {
			String commlexr = "";
			String commfacor = "";
			String history = null;
			String preresult = "2";
			HashMap map = prepareHandle(condid, infokind);
			String vtotal = (String) hm.get("vtotal");
			vtotal = vtotal == null || vtotal.length() == 0 ? "0" : vtotal;
			String htotal = (String) hm.get("htotal");
			htotal = htotal == null || htotal.length() == 0 ? "0" : htotal;
			commlexr = (String) map.get("commlexr");
			commfacor = (String) map.get("commfacor");
			history = (String) map.get("history");
			isresult = Boolean.parseBoolean((String) map.get("isresult"));

			StatDataEncapsulation simplestat = new StatDataEncapsulation();
			ArrayList privdblist = this.userView.getPrivDbList();
	        if(privdblist.size()==0)
	            throw new GeneralException("",ResourceFactory.getProperty("muster.label.dbname.size"),"","");
	        String userbase = (String)privdblist.get(0);
			int[][] statValues = null;
			statValues = simplestat.getDoubleLexprData(Integer.parseInt(condid), userbase.toUpperCase(), mOrg, userView.getUserName(),
					userView.getManagePrivCode(), userView, infokind, isresult,commlexr, commfacor, preresult, history, mBase.toUpperCase(), vtotal, htotal);
			List varraylist = simplestat.getVerticalArray();
			List rowList = new ArrayList();
			List rowParameterList = new ArrayList();// 数据穿透行参数
			List colList = new ArrayList();
			List colParameterList = new ArrayList();// 数据列透行参数
			for (int i = 0; i < varraylist.size(); i++) {// 获得行标题数据
				LazyDynaBean bean = (LazyDynaBean) varraylist.get(i);
				String legend = (String) bean.get("legend");
				String id = (String) bean.get("id");
				String norder = (String) bean.get("norder");
				String parameter = id + "`" + norder;
				colParameterList.add(parameter);
				colList.add(legend);
			}
			List harraylist = simplestat.getHorizonArray();
			for (int i = 0; i < harraylist.size(); i++) {// 获得列标题数据
				LazyDynaBean bean = (LazyDynaBean) harraylist.get(i);
				String legend = (String) bean.get("legend");
				String id = (String) bean.get("id");
				String norder = (String) bean.get("norder");
				String parameter = id + "`" + norder;
				rowParameterList.add(parameter);
				rowList.add(legend);
			}
//			String snameplay = simplestat.getSNameDisplay();
//			int totalvalue = simplestat.getTotalValue();

			hm.put("rowList", rowList);
			hm.put("rowParameterList", rowParameterList);
			hm.put("colList", colList);
			hm.put("colParameterList", colParameterList);
			hm.put("values", statValues);
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * 
	 * @Title: prepareHandle
	 * @Description: 统计查询准备工作
	 * @param @return
	 * @return HashMap
	 * @throws GeneralException
	 * @throws
	 */
	public HashMap prepareHandle(String condid, String infokind) throws GeneralException {
		HashMap map = new HashMap();
		try {
			// 判断是否需要查询上次结果
			boolean isresult = true;
			if (lastResult == null || "".equals(lastResult)) {
				StringBuffer sql = new StringBuffer();
				sql.append("select flag from SName where id=");
				sql.append(condid);
				List rs = ExecuteSQL.executeMyQuery(sql.toString());
				if (!rs.isEmpty()) {
					LazyDynaBean rec = (LazyDynaBean) rs.get(0);
					String flag = rec.get("flag") != null ? rec.get("flag").toString() : "";
					if (flag != null && "1".equals(flag))
						isresult = false; // false时才查询，查询结果表
				}
			} else if ("true".equals(lastResult))
				isresult = false;
			// 如果没有选择人员库，默认用户的第一个人员库
			if (mBase == null || mBase.length() == 0)
				mBase = userView.getPrivDbList().get(0).toString();
			// mOrg="0101`01`";
			// 处理组织ID
			this.disposeOrganizationID();
			// 加上常用查询进行的统计
			String commlexr = "";
			String commfacor = "";
			String history = "";
			// 保护机制
			if (mCondID != null && mCondID.length() > 0) {
				if ("#".equals(mCondID))
					mCondID = "";
			}
			// 当没有选择时，从系统选择查询条件
			if (mCondID.length() == 0 && infokind != null && "1".equals(infokind)) {
				Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
				String stat_id = sysbo.getValue(Sys_Oth_Parameter.STAT_ID);
				if (stat_id != null && stat_id.length() > 0 && !"#".equals(stat_id)) {
					mCondID = stat_id;
				}
			}
			if (mCondID.length() > 0) {
				GeneralQueryStat generalstat = new GeneralQueryStat();
				String[] curr_id = { mCondID };
				generalstat.getGeneralQueryLexrfacor(curr_id, mBase, history, conn);
				if (curr_id != null && curr_id.length > 0) {
					commlexr = generalstat.getLexpr();
					commfacor = generalstat.getLfactor();
					history = generalstat.getHistory();
				}
			}
			map.put("commlexr", commlexr);
			map.put("commfacor", commfacor);
			map.put("history", history);
			map.put("isresult", String.valueOf(isresult));
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}

	/**
	 * 
	 * @Title: getStatisChartListSQL
	 * @Description:
	 * @param statisSLegendID 点击的ID
	 * @param statisDim 维度
	 * @param infokind 1人员、2单位、3岗位
	 * @return String
	 * @throws GeneralException
	 */
	public String getStatisChartListSQL(String statisSLegendID, String statisDim, String infokind) throws GeneralException {
		String strSql = "";
		try {
			String SLegendID = "";
			String nOrder = "";
			String[] dim;
			// 维度分割，statisSLegendID格式： 一维 id`norder，id3,二维 id1`norder,id2`norder，id3（备注：id3为统计项ID）
			String[] segment = statisSLegendID.split(",");
			// 获得ID3
			String condid = segment[segment.length - 1];
			//准备工作
			HashMap map = prepareHandle(condid, infokind);
			String commlexr = (String) map.get("commlexr");
			String commfacor = (String) map.get("commfacor");
			boolean isresult = Boolean.parseBoolean((String) map.get("isresult"));
			StringBuffer separate = new StringBuffer();
			// 是否总合计标示符
			boolean flag = true;
			// 分解，获得where中的条件
			for (int i = 0, length = segment.length - 1; i < length; i++) {
				dim = segment[i].split("`");
				SLegendID = dim[0];
				nOrder = dim[1];
				if (!(nOrder == null || "null".equals(nOrder) || "".equals(nOrder))) {
					if ("1".equals(infokind))
						separate.append(" and ");
					separate.append(this.getSeparateSql(mBase, SLegendID, nOrder, mOrg, infokind, isresult, commlexr, commfacor));
					flag = false;
				}
			}
			// 多维总合计,暂不支持单位岗位查询
			if ("1".equals(infokind)) {
				if (flag) {
					strSql = this.totalAmountSql(mBase, SLegendID, mOrg, infokind, isresult, commlexr, commfacor);
				} else
					strSql = separate.toString().substring(4);
			}
			StringBuffer sb = new StringBuffer();
			// 分解人员库
			String[] tmpdbpres = mBase.split("`");
			// 合并
			if ("1".equals(infokind)) {
				for (int n = 0, length = tmpdbpres.length; n < length; n++) {
					String tmpdbpre = tmpdbpres[n];
					sb.append(" union all select distinct " + tmpdbpre + "a01.a0100,'" + (n + 1) + "' ord,'" + tmpdbpre
							+ "' dbpre," + tmpdbpre + "a01.b0110," + tmpdbpre + "a01.e01a1," + tmpdbpre + "a01.e0122,a0101,a0000 ");
					sb.append(" from " + tmpdbpre + "A01 ");
					sb.append(" where (" + strSql.replace(mBase, tmpdbpre)+")");
				}
				strSql = sb.toString().substring(10);
			} else if ("2".equals(infokind) || "3".equals(infokind)) {
				
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
				String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
				FieldItem item = DataDictionary.getFieldItem(onlyname);
				if (item != null&&!"a0101".equalsIgnoreCase(onlyname)) {
					onlyname = onlyname;
				}else{
					onlyname ="";
				}
				String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
				item  = DataDictionary.getFieldItem(pinyin_field.toLowerCase());
				if (!(pinyin_field == null|| "".equals(pinyin_field) || "#".equals(pinyin_field)||item==null|| "0".equals(item.getUseflag()))&&!"a0101".equalsIgnoreCase(pinyin_field)){
					pinyin_field = pinyin_field;
				} else{
					pinyin_field = "";
				}
				// 　UN单位名称b0110 UM部门e0122@K岗位名称（职位）e01a1
				// 单位2，岗位3
				// 获得要查询的数据
				for (int n = 0, length = tmpdbpres.length; n < length; n++) {
					String tmpdbpre = tmpdbpres[n];
					sb.append(" union all select distinct " + tmpdbpre + "a01.a0100,'" + (n + 1) + "' ord,'" + tmpdbpre
							+ "' dbpre," + tmpdbpre + "a01.b0110," + tmpdbpre + "a01.e01a1," + tmpdbpre + "a01.e0122,a0101,a0000 ");
					if(onlyname.length()>0)
						sb.append(","+onlyname);
					if(pinyin_field.length()>0)
						sb.append(","+pinyin_field);
					sb.append(" from " + tmpdbpre + "A01 ");
				}
				strSql = sb.toString().substring(10);
				sb.setLength(0);
				// 获得要查询的范围Where
				String[] org = separate.toString().split("`");
				for (int i = 0, length = org.length; i < length; i++) {
					sb.append(" OR # like '" + org[i] + "%'");
				}
				String whereORG = sb.substring(4);
				sb.setLength(0);
				sb.append("select distinct a0100,ord,dbpre,b0110,e01a1,e0122,a0101,a0000 ");
				if(onlyname.length()>0)
					sb.append(","+onlyname);
				if(pinyin_field.length()>0)
					sb.append(","+pinyin_field);
				sb.append(" from (" + strSql + ") a ");
				// 根据单位还是岗位替换
				if ("2".equals(infokind)) {
					sb.append("where (" + whereORG.replace("#", "b0110") + " OR " + whereORG.replace("#", "e0122")+")");
				} else {
					sb.append("where (" + whereORG.replace("#", "e01a1")+")");
				}
				strSql = sb.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return strSql;

	}

	/**
	 * 
	 * @Title: totalAmountSql
	 * @Description: 查询总合计的sql
	 * @param userbase 选择的人员库
	 * @param SLegendID SLegend表中ID
	 * @param sqlSelect 组织ID
	 * @param infokind 1人员、2单位、3岗位
	 * @param bresult 是否查看历史记录，false是查看
	 * @param commlexpr 常用条件1
	 * @param commfactor 常用条件2
	 * @param @return
	 * @return String
	 * @throws GeneralException
	 */
	private String totalAmountSql(String userbase, String SLegendID, String sqlSelect, String infokind, boolean bresult,
			String commlexpr, String commfactor) throws GeneralException {
		String strFactor = "";
		String strLexpr = "";
		String strQuery = "";
		RowSet rs = null;
		StringBuffer strsql = new StringBuffer();
		StringBuffer totalAmountSql = new StringBuffer();
		ContentDAO dao = new ContentDAO(conn);
		StatDataEncapsulation SDE = new StatDataEncapsulation();
		try {
			String[] SLegend = SLegendID.split(",");
			for (int i = 0; i < SLegend.length; i++) {
				SLegendID = SLegend[i];
				// 获得统计项
				strsql.append("select * from SLegend where id='");
				strsql.append(SLegendID);
				strsql.append("' order by norder");
				rs = dao.search(strsql.toString());
				while (rs.next()) {
					strLexpr = rs.getString("lexpr");
					strFactor = rs.getString("factor") + "`";
					if (commlexpr != null && commfactor != null) {
						String[] style = getCombinLexprFactor(strLexpr, strFactor, commlexpr, commfactor);
						if (style != null && style.length == 2) {
							strLexpr = style[0];
							strFactor = style[1];
						}
					}
					boolean ishavehistory = false;
					if ("1".equals(rs.getString("flag")))
						ishavehistory = true;
					strQuery = SDE.getCondQueryString(strLexpr, strFactor, userbase, ishavehistory, userView.getUserName(),
							sqlSelect, userView, infokind, bresult);
					strQuery = strQuery.substring(16 + userbase.length());
					// 所有的条件语句用OR合并
					totalAmountSql.append(" OR ");
					totalAmountSql.append(strQuery);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return totalAmountSql.toString().substring(4);
	}

	/**
	 * 
	 * @Title: getSeparateSql
	 * @Description: 人员显示的条件sql
	 * @param userbase 选择的人员库
	 * @param SLegendID SLegend表中ID
	 * @param nOrder SLegend表中字段
	 * @param sqlSelect 组织ID
	 * @param infokind 1人员、2单位、3岗位
	 * @param bresult  是否查看历史记录，false是查看
	 * @param commlexpr 常用条件1
	 * @param commfactor常用条件2
	 * @return String
	 * @throws GeneralException
	 */
	private String getSeparateSql(String userbase, String SLegendID, String nOrder, String sqlSelect, String infokind, 
			boolean bresult, String commlexpr, String commfactor) throws GeneralException {
		String strFactor = "";
		String strLexpr = "";
		String strQuery = "";
		RowSet rs = null;
		StringBuffer strsql = new StringBuffer();
		ContentDAO dao = new ContentDAO(conn);
		StatDataEncapsulation SDE = new StatDataEncapsulation();
		try {
			// 获得统计项
			strsql.append("select * from SLegend where id='");
			strsql.append(SLegendID);
			strsql.append("' and norder = '");
			strsql.append(nOrder);
			strsql.append("' order by norder");
			rs = dao.search(strsql.toString());
			if (rs.next()) {
				strLexpr = rs.getString("lexpr");
				strFactor = rs.getString("factor") + "`";
				if (commlexpr != null && commfactor != null) {
					String[] style = getCombinLexprFactor(strLexpr, strFactor, commlexpr, commfactor);
					if (style != null && style.length == 2) {
						strLexpr = style[0];
						strFactor = style[1];
					}
				}
				boolean ishavehistory = false;
				if ("1".equals(rs.getString("flag")))
					ishavehistory = true;
				strQuery = SDE.getCondQueryString(strLexpr, strFactor, userbase, ishavehistory, userView.getUserName(), 
						sqlSelect, userView, infokind, bresult);
				if ("1".equals(infokind)) {
					strQuery = strQuery.substring(16 + userbase.length());
				} else {
					strQuery = this.getBKSeparateSql(infokind, strQuery);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return strQuery;
	}

	/**
	 * 
	 * @Title: getBKSeparateSql
	 * @Description:遇到单位或岗位时的处理。获取的格式为id1`id2`
	 * @param infokind 单位2，岗位3
	 * @param strQuery
	 * @return String
	 * @throws GeneralException
	 */
	private String getBKSeparateSql(String infokind, String strQuery)
			throws GeneralException {
		RowSet rs = null;
		StringBuffer strsql = new StringBuffer();
		ContentDAO dao = new ContentDAO(conn);
		try {
			// 　UN单位名称b0110 UM部门e0122@K岗位名称（职位）e01a1
			// 单位2，岗位3
			if ("2".equals(infokind))
				strQuery = "select distinct b01.b0110 as lexprData " + strQuery;
			else if ("3".equals(infokind))
				strQuery = "select distinct k01.e01a1 as lexprData " + strQuery;
			rs = dao.search(strQuery);
			while (rs.next()) {
				strsql.append(rs.getString("lexprData") + "`");
			}
			strQuery = PubFunc.getTopOrgDept(strsql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return strQuery;
	}
}
