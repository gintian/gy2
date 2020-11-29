package com.hjsj.hrms.module.officermanage;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.officermanage.businessobject.CardViewService;
import com.hjsj.hrms.module.officermanage.businessobject.impl.CardViewServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SyncOfficerDataTrans extends IBusiness {
	@Override
	public void execute() throws GeneralException {
		try {
			String flag = (String) this.getFormHM().get("flag");
			boolean loadFlag = false;
			CardViewService bo=new CardViewServiceImpl(this.userView, this.frameconn);
			if ("all".equals(flag)) {// 加载全部
				TableDataConfigCache cache = (TableDataConfigCache) this.userView.getHm()
						.get("OfficerManage_OfficerView");
				StringBuffer sql = new StringBuffer();
				if(cache!=null) {
					sql.append( " select * from ("+cache.getTableSql()+" ) A where 1=1");
					if (StringUtils.isNotEmpty(cache.getFilterSql())) {
						sql.append(cache.getFilterSql());
					}
					if (StringUtils.isNotEmpty(cache.getQuerySql())) {
						sql.append(cache.getQuerySql());
					}
				}else {
					String sql_=bo.getOfficerSql("",false).toString();
					sql.append( " select * from ( "+sql_.toString().substring(0, sql_.length()-10)+" ) A where 1=1");
				}
				loadFlag = updateData(sql.toString(), null);
			} else {// 加载选中人员
				ArrayList<String> list = (ArrayList) this.getFormHM().get("data"); // Usr`A0100
				ArrayList sqlList = new ArrayList();
				for (String id : list) {
					ArrayList dataList = new ArrayList();
					dataList.add(this.userView.getUserName());
					dataList.add(id.substring(0, 3).toUpperCase());
					dataList.add(id.substring(3));
					dataList.add(0);
					sqlList.add(dataList);
				}
				loadFlag = updateData("", sqlList);

			}
			if (loadFlag) {// 结果表更新成功
				ArrayList<HashMap> maplist = getParamMap();
				updateDate(maplist.get(0), maplist.get(1));
			}
			this.getFormHM().put("typeFlag", true);
		} catch (Exception e) {
			e.printStackTrace();
			this.getFormHM().put("typeFlag", false);
			this.getFormHM().put("errorMsg", e.getMessage());
		}

	}

	/***
	 * 更新om_officer_muster数据
	 */
	private void updateDate(HashMap<String, String> itemMap, HashMap<String, String> ccMap) throws Exception {
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		ArrayList dbList = new ArrayList();
		try {
			rs = dao.search("select distinct nbase from t_sys_result where username='" + this.userView.getUserName()
					+ "' and flag=0 ");
			while (rs.next()) {
				dbList.add(rs.getString(1));
			}
		} finally {
			PubFunc.closeDbObj(rs);
		}
		if (dbList.size() > 0) {
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
			String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "1", "name");// 身份证
			//新增A0101列 同步前先判断是否存在A0101数据列 存在则同步A0101数据
			DbWizard dbWizard = new DbWizard(this.frameconn);
			boolean isExistA0101=false;
			if(dbWizard.isExistField("om_officer_muster", "a0101",false)) {
				isExistA0101=true;
			}
			boolean isExist_A0100=false;
			if(dbWizard.isExistField("om_officer_muster", "a0100",false)) {
				isExist_A0100=true;
			}
			if(StringUtils.isEmpty(chk))
				chk=" null ";
			String sql = "";
			StringBuffer sbf = new StringBuffer("");
			String colfield = "";
			for (int i = 0; i < dbList.size(); i++) {
				String nbase = dbList.get(i).toString();
				String itemfield = "";
				ArrayList setList = new ArrayList();
				sbf.append(" select "+(isExistA0101?nbase+"A01.A0101,":"")+(isExist_A0100?nbase+"A01.A0100,":"")+"'" + nbase + "' as nbase," + nbase + "A01.guidkey," + chk + " as id_number  ");
				for (String colid : itemMap.keySet()) {
					if (i == 0)
						colfield += colid + ",";
					String value = itemMap.get(colid);
					boolean customFlag = false;
					if (value.indexOf("`") > -1)
						customFlag = true;
					FieldItem item = DataDictionary.getFieldItem(customFlag ? value.split("`")[0] : value);
					if (item == null)
						throw new Exception(colid + ResourceFactory.getProperty("officer.notFindField"));
					if (!"A01".equalsIgnoreCase(item.getFieldsetid())) {
						if(!setList.contains(item.getFieldsetid()))
							setList.add(item.getFieldsetid());
					}
					if ("D".equals(item.getItemtype())) {
						String str = nbase + item.getFieldsetid() + "." + item.getItemid();
						//日期类型格式以"-"间隔 不按照系统指标类型设置
						String format="YYYY-MM";
						switch (item.getItemlength()) {
						case 4:
							format="YYYY";
							break;
						case 7:
							format="YYYY-MM";
							break;
						case 10:
							format="YYYY-MM-DD";
							break;
						case 16:
							format="YYYY-MM-DD HH:MM";
							break;
						default:
							format="YYYY-MM-DD HH:MM:SS";
							break;
						}
						itemfield += " " + Sql_switcher.dateToChar(str,format) + " as " + colid + " ,";							
					} else
						itemfield += " " + nbase + item.getFieldsetid() + "." + item.getItemid() + " as " + colid
								+ " ,";
				}
				if(itemfield.length()>0)
					sbf.append(" , "+itemfield.substring(0, itemfield.length() - 1));
				
				sbf.append(" from " + nbase + "A01");
				String wheresql = "";
				for (int j = 0; j < setList.size(); j++) {
					//select UsrA04.* from UsrA04,(select max(i9999) as i9999,A0100 from UsrA04 group by A0100) T where T.i9999=UsrA04.i9999 and UsrA04.a0100=t.A0100
					sbf.append(",(");
					sbf.append(" select "+nbase + setList.get(j).toString()+".*");
					sbf.append(" from "+nbase + setList.get(j).toString());
					sbf.append(" ,(select max(i9999) as i9999,A0100 from "+nbase + setList.get(j).toString()+" group by A0100) T where T.i9999="+nbase + setList.get(j).toString()+".i9999 and "+nbase + setList.get(j).toString()+".a0100=t.A0100");
					sbf.append(" )  "+ nbase + setList.get(j).toString());
					if (j != 0)
						wheresql += " And ";
					wheresql += " " + nbase + "A01.A0100=" + nbase + setList.get(j).toString() + ".A0100";
				}
				if (StringUtils.isNotEmpty(wheresql))
					sbf.append(" where " + wheresql);
				if (i < dbList.size() - 1)
					sbf.append(" union all ");
			}
			sql += "select guidkey, id_number, "+(isExistA0101?"A.A0101,":"")+(isExist_A0100?"A.A0100,":"")+" A.nbase as nbase,'" + this.userView.getUserName() + "' as create_user, "
					+ "'" + this.userView.getUserFullName() + "' as create_fullName," + Sql_switcher.today()
					+ " as create_time" + (colfield.length()>0?","+colfield.substring(0, colfield.length() - 1):"") + " " + "from t_sys_result left join ("
					+ sbf.toString() + ")A  on  A.A0100=t_sys_result.obj_id where t_sys_result.username='" + this.userView.getUserName() + "' "
					+ " and t_sys_result.flag=0 " + "and  A.A0100=t_sys_result.obj_id"
					+ " and t_sys_result.nbase=A.nbase";
			// 插入前先删除数据
			dao.delete("delete om_officer_muster where guidkey in (select guidkey from (" + sql + ")B )",
					new ArrayList());
			String insertSql = "insert into om_officer_muster (guidkey,id_number,"+(isExistA0101?"A0101,":"")+(isExist_A0100?"A0100,":"")+"nbase,create_user,create_fullName,"
					+ "create_time " + (colfield.length()>0?","+colfield.substring(0, colfield.length() - 1):"") + ") " + sql;
			dao.insert(insertSql, new ArrayList());

			String whereSql="where guidkey in ( select guidkey from ("+sql+")A)";
			// 代码类翻译
			for (String key : itemMap.keySet()) {
				FieldItem item = DataDictionary.getFieldItem(itemMap.get(key));
				if (item != null) {
					String codesetId = item.getCodesetid();
					if (StringUtils.isNotEmpty(codesetId) && !"0".equals(codesetId)) {
						if (!("UM".equals(codesetId) || "UN".equals(codesetId) || "@K".equals(codesetId))) {
							if (Sql_switcher.searchDbServer() == Constant.ORACEL)
								dao.update("update om_officer_muster  set (om_officer_muster." + key
										+ ")=(select codeitemdesc from codeitem where codeitem.codesetid='"
										+ item.getCodesetid() + "' and codeitem.codeitemid=om_officer_muster." + key
										+ " ) "+whereSql+" ");
							else
								dao.update("update om_officer_muster  set om_officer_muster." + key
										+ "=(select codeitemdesc from codeitem where codeitem.codesetid='"
										+ item.getCodesetid() + "' and codeitem.codeitemid=om_officer_muster." + key
										+ " ) "+whereSql+" ");
						} else {
							if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
								dao.update("update om_officer_muster  set " + "(om_officer_muster." + key + ")="
										+ "(select codeitemdesc from organization " + " where organization.codesetid='"
										+ item.getCodesetid() + "' " + "and organization.codeitemid=om_officer_muster."
										+ key + " and end_date>" + Sql_switcher.today() + " )"+whereSql);
							} else {
								dao.update("update om_officer_muster  set " + "om_officer_muster." + key + "="
										+ "(select codeitemdesc from organization " + " where organization.codesetid='"
										+ item.getCodesetid() + "' " + "and organization.codeitemid=om_officer_muster."
										+ key + " and end_date>" + Sql_switcher.today() + " )"+whereSql);

							}
						}
					}
				}
			}

		}
		/*//处理入党时间
		if(dbList.size()>0&&itemMap.containsKey("joinpartydate")) {
			StringBuffer sbf=null;
			String partyField=itemMap.get("joinpartydate");//入党时间特殊处理
			if(StringUtils.isNotEmpty(partyField)) {
				for(int i=0;i<dbList.size();i++) {
					sbf=new StringBuffer();
					String nbase=(String)dbList.get(i);
					sbf.append("update  om_officer_muster  set joinpartydate=A.joinpart");
					sbf.append(" from om_officer_muster,"
							+ "(select guidkey,case  "
							+ " when ( isnull(A01AM,'') = '01' or isnull(A01AM,'') = '02') "
							+ " then  CONVERT(varchar(20),year("+partyField+")) +right(100+month("+partyField+"),2)  "
							+ " when (isnull(A01AM,'')<> '' and isnull(A01AM,'') <>'01' and isnull(A01AM,'')='12' and isnull(A01AM,'') <>'02' and isnull("+partyField+",'') <> '') "
							+ " then CONVERT(varchar(20),year("+partyField+")) +right(100+month("+partyField+"),2)+'；'+codeitemdesc "
							+" when (isnull(A01AM,'')<> '' and isnull(A01AM,'') <>'01' and isnull(A01AM,'') <>'02' and isnull(A01AM,'')<>'12' and isnull("+partyField+",'') <> '') "
							+" then CONVERT(varchar(20),year("+partyField+")) +right(100+month("+partyField+"),2)+'；'+left(isnull(codeitemdesc,''),2)	"
							+" when (isnull(A01AM,'')='12' and isnull("+partyField+",'')='') then codeitemdesc "
							+ " when (isnull(A01AM,'')= '' and isnull("+partyField+",'')='') "
							+ " then '无党派' "
							+ " when (isnull(A01AM,'')<>'' and isnull("+partyField+",'')='') "
							+ " then left(isnull(codeitemdesc,''),2) "
							+ " when (isnull(A01AM,'')='' and isnull("+partyField+",'')<>'') "
							+ " then CONVERT(varchar(20),year("+partyField+")) +right(100+month("+partyField+"),2)" 
							+" end  as joinpart "
							+ " from "+nbase+"A01 left join codeitem on  codesetid='AT' and A01AM=codeitemid"
							+" and a0100 in (select obj_id from t_sys_result where username='"+this.userView.getUserName()+"' and lower(nbase)='"+nbase.toLowerCase()+"' and flag=0 )" 
							+ ") A "
							+ " where om_officer_muster.guidkey=A.guidkey" + 
							"");
					
					dao.update(sbf.toString());
				}
			}
		}*/
		
		
		for (String key : ccMap.keySet()) {
			String cc = ccMap.get(key);// 存储过程
			if(Sql_switcher.searchDbServer()==1)
				dao.update("exec  " + cc + " @username= '" + this.userView.getUserName() + "'");
			else
				dao.update("begin " + cc + "('"+this.userView.getUserName()+"'); end;");
		}
	}

	/***
	 * 获取配置表中自定义指标与固定指标
	 */
	private ArrayList<HashMap> getParamMap() throws Exception {
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		String strPara = "";
		try {
			rs = dao.search("select * from constant where constant='OFFICER_PARAM'");
			if (rs.next()) {
				strPara = rs.getString("str_value");
			}
		} finally {
			PubFunc.closeDbObj(rs);
		}
		if (StringUtils.isNotEmpty(strPara)) {
			ArrayList list = new ArrayList();
			Document doc = null;
			doc = PubFunc.generateDom(strPara);
			Element root = doc.getRootElement();
			List<Element> customEL = root.getChild("customfields").getChildren();// 自定义
			List<Element> mainEL = root.getChild("mainfields").getChildren();// 固定指标
			HashMap<String, String> itemMap = new HashMap();// 指标集合 字段名：指标名
			HashMap<String, String> cCMap = new HashMap();//// 指标集合 字段名：指标名
			for (Element el : mainEL) {
				String name = el.getAttributeValue("name");
				String type = el.getAttributeValue("type");
				String value = el.getAttributeValue("value");// 存储/指标id
				if (StringUtils.isEmpty(value)) {// value为空不更新
					continue;
				}
				if ("1".equals(type)) {// 使用指标
					itemMap.put(name, value);
				} else {// 使用存储过程
					cCMap.put(name, value);
				}
			}
			for (Element el : customEL) {
				String key = el.getAttributeValue("columnid");
				String value = el.getAttributeValue("value");
				String type = el.getAttributeValue("type");
				if (StringUtils.isEmpty(value)) {// value为空不更新
					continue;
				}
				if ("1".equals(type)) {
					itemMap.put(key, value + "`customFlag");// 自定义标记
				} else {
					cCMap.put(key, value);
				}
			}
			list.add(itemMap);
			list.add(cCMap);
			return list;
		}
		return null;
	}

	/***
	 * 更新查询结果表数据
	 */
	private boolean updateData(String sql, ArrayList list) throws Exception {
		ContentDAO dao = new ContentDAO(this.frameconn);
		dao.delete("delete t_sys_result where username='"+this.userView.getUserName()+"' and flag=0 ", new ArrayList());
		if (StringUtils.isNotEmpty(sql)) {
			dao.insert(" insert into t_sys_result select '" + this.userView.getUserName()
					+ "',UPPER(AA.dbType),AA.A0100,'0' from (" + sql + ")AA ", new ArrayList());
			return true;
		} else {
			for (int i = 0; i < list.size(); i++) {
				ArrayList dataList = (ArrayList) list.get(i);
				dao.insert(" insert into t_sys_result(username,nbase,obj_id,flag)values(?,?,?,?)", dataList);
			}
			return true;
		}
	}
}
