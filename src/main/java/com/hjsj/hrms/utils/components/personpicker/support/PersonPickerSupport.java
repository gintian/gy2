package com.hjsj.hrms.utils.components.personpicker.support;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.recruitment.exammanage.examhall.businessobject.ExamHallBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.*;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * <p>Title: PersonPickerSupport.java</p>
 * <p>Description: 选人控件工具集</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-2-9 15:46:11</p>
 * @author 刘蒙
 * @version 1.0
 */
public class PersonPickerSupport {

	private Connection conn = null;
	private ContentDAO dao = null;
	private UserView userview;
	private String pyField; // 拼音字段名称
	private String emailField;
	private String onlyField; //人员唯一指标
	private String phoneField;
	private String sexField;//性别指标
	private String loginNameField;//用户名指标
	public PersonPickerSupport(Connection frameconn) {
		super();
		this.conn = frameconn;
		this.dao = new ContentDAO(conn);

		pyField = getPinYinField(conn);
		if(StringUtils.isNotBlank(pyField)) {
			pyField = pyField.toLowerCase();
		}
		phoneField = getPhoneField(conn);
		if(StringUtils.isNotBlank(phoneField)) {
			phoneField = phoneField.toLowerCase();
		}
		emailField = getEmailField(conn);
		if(StringUtils.isNotBlank(emailField)) {
			emailField = emailField.toLowerCase();
		}
		sexField = getSexField(conn);
		if(StringUtils.isNotBlank(sexField)) {
			sexField = sexField.toLowerCase();
		}
		onlyField = getOnlyField(conn);
		if(StringUtils.isNotBlank(onlyField)) {
			onlyField = onlyField.toLowerCase();
		}
		DbNameBo db = new DbNameBo(frameconn);
		loginNameField = db.getLogonUserNameField();
		if(StringUtils.isNotBlank(loginNameField)) {
			loginNameField = loginNameField.toLowerCase();
		}
	}
	public PersonPickerSupport(Connection frameconn,UserView uv) {
		this(frameconn);
		userview = uv;
	}

	/** 查询下级单位 */
	public List subordinateUnit(String attachTo, String ancester, String level, String orgid, boolean isPrivExpression, boolean recruitmentSpecial, boolean addpost, boolean selectByNbase) {

		List subunit = new ArrayList();
		List params = new ArrayList();
		RowSet rs = null;
		try {
			String orgSql = "";
			if(!StringUtils.isEmpty(orgid)){//机构范围,为空默认查询全部
				orgSql = this.getOrgSql(orgid);
			}

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT * FROM organization WHERE ");
			if(!addpost){// 默认不添加岗位。要选择岗位时，就不能排除岗位了。chent 20161102
				sql.append(" codesetid<>'@K' AND ");
			}
			sql.append(" ? BETWEEN start_date AND end_date");
			params.add(new Date(System.currentTimeMillis()));

			if (isEmpty(attachTo) || (selectByNbase && attachTo.startsWith("nbs_"))) {
				if(StringUtils.isNotBlank(orgid)){
					//不管是否走高级，如果穿了orgId就都需要走  haosl
					sql.append(orgSql);// 组织机构范围查询
				} else if (isPrivExpression && !userview.isSuper_admin()) {
					String unit_id = this.userview.getUnit_id();
					String privcode = userview.getManagePrivCode();
					String privcodeValue = userview.getManagePrivCodeValue();
					// 业务用户:操作单位 > 管理范围
					if("UN`".equalsIgnoreCase(unit_id)){
						sql.append(" AND parentid=codeitemid");
					} else if (StringUtils.isNotEmpty(unit_id) && unit_id.length()>2 || StringUtils.isNotEmpty(privcode)) {
						if(StringUtils.isNotEmpty(unit_id) && unit_id.length() > 2) {
							String[] unitArr = unit_id.split("`");
							String unitStr = "";
							for(int j=0;j<unitArr.length;j++) {
								String unit = unitArr[j].substring(2);
								unitStr+="'"+unit+"',";
							}
							sql.append(" AND codeitemid in (" + unitStr.substring(0, unitStr.length()-1) + ")");
						}else {
							if (privcodeValue == null || "".equals(privcodeValue.trim())) {//全权
								if (StringUtils.isEmpty(orgid)) {
									sql.append(" AND parentid=codeitemid");
								}
							} else {//非全权
								sql.append(" AND codeitemid in ('" + privcodeValue + "')");
							}
						}
					}else {//没有授权人员范围的话，则不显示机构和人员
						sql.append(" AND 1=2");
					}
				} else {
					sql.append(" AND parentid=codeitemid");
				}
			} else {
				sql.append(" AND parentid=? AND parentid<>codeitemid");
				params.add(attachTo);
			}

			sql.append(" ORDER BY a0000,codeitemid");

			rs = dao.search(sql.toString(), params);
			while (rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();

				String setId = nvl(rs.getString("codesetid"), "");
				String id = nvl(rs.getString("codeitemid"), "");
				String name = nvl(rs.getString("codeitemdesc"), "");

				String _id = PubFunc.encrypt(id);
				bean.set("id", _id);
				bean.set("name", name);
				bean.set("shortName", truncate(name, 18));

				bean.set("rawType", setId);
				bean.set("type", UNIT);
				bean.set("ancester", nvl(ancester, _id));
				bean.set("attachTo", nvl(attachTo, ""));

				int _level = Integer.parseInt(nvl(level, "0"))+1;
				if(selectByNbase && _level ==1 ){ // 区分人员库时，要把第一层级的机构往后错开一位 chent 20180205 modify
					++_level;
				}
				bean.set("level",  String.valueOf(_level));
				bean.set("display", FOLD);
				if(recruitmentSpecial){
					ExamHallBo examHallBo = new ExamHallBo(this.conn, this.userview);
					HashMap<String, Integer> map = examHallBo.getExamNum(id);
					int has_exam = 0, no_exam = 0;
					if(map.get("has_exam") != null){
						has_exam = map.get("has_exam");
					}
					if(map.get("no_exam") != null){
						no_exam = map.get("no_exam");
					}
					bean.set("has_exam", has_exam);
					bean.set("no_exam", no_exam);
				}
				subunit.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}

		return subunit;
	}

	/** 下属人员 */
	public List subordinatePerson(String attachTo, String ancester, String level, String nbases, String extend_str, Boolean isPrivExpression, List deprecate, boolean validateSsLOGIN, boolean selectByNbase, String contentNbs) {
		List person = new ArrayList();

		StringBuffer sql = new StringBuffer();
		List params = new ArrayList();
		RowSet rs = null;
		try {
			String[] nbase = {};
			if(selectByNbase){//区分人员库时，直接拿当前的人员库
				nbase = new String[]{contentNbs};
			}else {
				nbase = getNbases(nbases, validateSsLOGIN);
				if(nbase==null){
					return person;
				}
			}

			for (int i = 0; i < nbase.length; i++) {
				String n = nbase[i];
				if (isEmpty(n)) {
					continue;
				}
				StringBuffer dbperSql = new StringBuffer();
				String tblName = n + "A01";
				if(sql.length() == 0){
					sql.append("");
				}else{
					sql.append(" UNION ");
				}
				// sexField性别指标
				dbperSql.append("SELECT guidkey,a0000,a0100,a0101,b0110,e0122,e01a1,(CASE WHEN  nullif (e0122,'')  IS NOT NULL  THEN e0122 ELSE b0110 END) as atta,? AS nbase");// 添加所有时，要把下下级人员也同时添加 chent 20161010
				if(isNotEmpty(sexField) && dbperSql.indexOf(sexField)==-1) {
					dbperSql.append(",").append(sexField);
				}
				if (isNotEmpty(phoneField) && dbperSql.indexOf(phoneField)==-1) {
					dbperSql.append(",").append(phoneField);
				}
				params.add(n);
				if (isNotEmpty(emailField)  && dbperSql.indexOf(emailField)==-1) {
					dbperSql.append(",").append(emailField);
				}
				if(isNotEmpty(onlyField) && dbperSql.indexOf(onlyField)==-1) {
					dbperSql.append(",").append(onlyField);
				}
				if(isNotEmpty(loginNameField) && dbperSql.indexOf(loginNameField)==-1) {
					dbperSql.append(",").append(loginNameField);
				}
				dbperSql.append(" FROM ").append(tblName).append(" WHERE");
				//sql.append(" (CASE WHEN  nullif (e0122,'')  IS NOT NULL  THEN e0122 ELSE b0110 END) like '"+PubFunc.decrypt(attachTo)+"%'");// 添加所有时，要把下下级人员也同时添加 chent 20161010
				dbperSql.append(" (CASE WHEN  nullif (e0122,'')  IS NOT NULL  THEN e0122 ELSE b0110 END) = '"+attachTo+"'");// 添加所有时，要把下下级人员也同时添加 chent 20161010


				if(StringUtils.isNotBlank(extend_str)&&extend_str.toLowerCase().indexOf("salaryid")!=-1){
					//薪资发放个性化内容 添加薪资类别人员范围的限制 extend_str串内容为 salaryid=xxx;appdate=xxx zhanghua 2019-05-15
					String sql_salary = getSql_for_salary(tblName);
					dbperSql.append(sql_salary);
					if(StringUtils.isNotBlank(sql_salary)) {
						params.add(n.toUpperCase());
					}
				}else{

					if(!StringUtils.isEmpty(extend_str)){
						// 按检索条件和人员范围
						if(extend_str.startsWith("template/"))
						{
							String searchSql=getTemplateSearchSql(extend_str,isPrivExpression,n);
							if(!StringUtils.isEmpty(searchSql))
								dbperSql.append(searchSql);
						}
						else
						{
							String extend_sql="";
							extend_sql= extend_str.replace("${nbase}", n);
							dbperSql.append(" and A0100 in("+ extend_sql +")");
						}
					}

				}
                //权限控制sql haosl update 2017-10-27
                if(isPrivExpression) {
                    String privSql = getPrivSQL(n);
                    dbperSql.append(privSql);
                }
				sql.append(dbperSql);
			}
			if(sql.length() > 0){
				sql.append(" ORDER BY a0000");
				rs = dao.search(sql.toString(), params);
				while (rs.next()) {
					String pre = nvl(rs.getString("nbase"), "");
					String a0100 = nvl(rs.getString("a0100"), "");
					String atta = nvl(rs.getString("atta"), "");
					if (deprecate.contains(pre + a0100)) {
						continue;
					}

					LazyDynaBean bean = _pack(rs, emailField);
					if (isNotEmpty(bean)) {
						bean.set("ancester", nvl(ancester, ""));
						bean.set("attachTo", nvl(PubFunc.encrypt(attachTo), ""));
						bean.set("realattach", PubFunc.encrypt(atta));
						bean.set("level", Integer.parseInt(nvl(level, "0")) + 1 + "");
						person.add(bean);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}

		return person;
	}
	/**
	 * 默认已选
	 * @param defaultSelected
	 * @param addunit
	 * @param adddepartment
	 * @param addpost
	 * @return
	 * chent
	 */
	public List getDefaultSelected(List defaultSelected, boolean addunit, boolean adddepartment, boolean addpost,boolean isSelfUser) {
		List list = new ArrayList();
		RowSet rs = null;
		try {
			boolean isPerson = true;//默认的是人员 or 机构
			if(addunit || adddepartment || addpost){
				isPerson = false;
			}

			StringBuilder sql = new StringBuilder();
			List params = new ArrayList();
			for (int i = 0; i < defaultSelected.size(); i++) {
				StringBuilder childSql = new StringBuilder();
				if(sql.length() == 0){
					sql.append("");
				}else{
					sql.append(" UNION ");
				}

				String obj = (String)defaultSelected.get(i);
				if(StringUtils.isEmpty(obj)){
					continue;
				}
				obj = PubFunc.decrypt(obj);
				if(isPerson){
					if(isSelfUser) {//自主用戶
						String nbs = obj.substring(0, 3);
						String a0100 = obj.substring(3);

						String tblName = nbs + "A01";
						childSql.append("SELECT guidkey,a0000,a0100,a0101,b0110,e0122,e01a1,(CASE WHEN  nullif (e0122,'')  IS NOT NULL  THEN e0122 ELSE b0110 END) as atta,? AS nbase");
						if (isNotEmpty(sexField) && childSql.indexOf(sexField)==-1) {
							childSql.append(",").append(sexField);
						}
						if (isNotEmpty(phoneField) && childSql.indexOf(phoneField)==-1) {
							childSql.append(",").append(phoneField);
						}
						if (isNotEmpty(emailField) && childSql.indexOf(emailField)==-1) {
							childSql.append(",").append(emailField);
						}
						if(isNotEmpty(loginNameField) && childSql.indexOf(loginNameField)==-1) {
							childSql.append(",").append(loginNameField);
						}
						if(isNotEmpty(onlyField) && childSql.indexOf(onlyField)==-1) {
							childSql.append(",").append(onlyField);
						}
						childSql.append(" FROM ").append(tblName);
						childSql.append(" where a0100=? ");
						params.add(nbs);
						params.add(a0100);
					}else {//业务用户
						childSql.append("select username,FullName, nbase, a0100 from operuser where username=?");
						childSql.append(" and roleid=0  ");
						params.add(obj);
					}
				} else {

					String pre = obj.substring(0, 2);
					String codeitemid = obj.substring(2);

					childSql.append("SELECT codesetid,codeitemid,codeitemdesc FROM organization WHERE ");
					childSql.append(" ? BETWEEN start_date AND end_date");
					childSql.append(" and codeitemid=?");
					params.add(new Date(System.currentTimeMillis()));
					params.add(codeitemid);
				}
				sql.append(childSql.toString());
			}
			if(sql.length() > 0){
				rs = dao.search(sql.toString(), params);
				while (rs.next()) {
					LazyDynaBean bean = null;
					if(isPerson){
						if(isSelfUser) {//自助用户
							bean = _pack(rs, emailField);
							if (isNotEmpty(bean)) {
								list.add(bean);
							}
						}else {//业务用户
							String userName = rs.getString("username");
					        String FullName = rs.getString("FullName");
							bean = new LazyDynaBean();
							String id = PubFunc.encrypt(userName).replaceAll("@", "＠");

							// 业务用户显示规则：显示全称，如果没有全称则显示账号名。
							String nametext = userName;
							if(StringUtils.isNotEmpty(FullName)){
								nametext = FullName;
							}
							bean.set("id", id);
							bean.set("name", nametext);
							bean.set("userName", userName);
//							bean.set("ancester", nvl(ancester, id));
//							bean.set("attachTo", nvl(groupid, ""));
							bean.set("shortName", truncate(nametext, 18));
							bean.set("type", PERSON);
//							bean.set("level", Integer.parseInt(nvl(level, "0"))+1+"");
							bean.set("display", FOLD);
							bean.set("photo", "/components/personPicker/image/male.png");
							list.add(bean);
						}

					}else {
						LazyDynaBean unitbean = new LazyDynaBean();
						String setId = nvl(rs.getString("codesetid"), "");
						String id = nvl(rs.getString("codeitemid"), "");
						String name = nvl(rs.getString("codeitemdesc"), "");

						String _id = PubFunc.encrypt(id);
						unitbean.set("id", _id);
						unitbean.set("name", name);
						unitbean.set("shortName", truncate(name, 18));
						unitbean.set("rawType", setId);
						unitbean.set("type", UNIT);

						bean = new LazyDynaBean();
						bean.set("data", unitbean);//兼容前台需要的格式，机构时前台需要用data封装一层
						list.add(bean);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}

		return list;
	}
	/**
	 * 取非超级管理员认证应用库和人员库的交集
	 * @param nbase 认证应用库字符串数组
	 * @return
	 */
	private String[] getNbase(String[] nbase){
		String nbases = userview.getDbpriv().toString();//取人员库
		if(",".equals(nbases)){//没任何人员库权限
			nbase = null;
		}else{//有人员库权限，取交集
			String[] tempNbase = nbases.split(",");
			StringBuffer tempNbases = new StringBuffer();
			for (int i = 0; i < tempNbase.length; i++) {
				if (isEmpty(tempNbase[i]))
					continue;
				for (int j = 0; j < nbase.length; j++) {
					if (tempNbase[i].equals(nbase[j])) {
						tempNbases.append(tempNbase[i]+",");
						break;
					}
				}
			}
			if(tempNbases.length()==0)
				nbase = null;
			else
				nbase = tempNbases.toString().split(",");
		}
		return nbase;
	}

	/**
     * 根据关键字从姓名拼音,姓名汉字和邮箱中查找相似的项
     * @param keyword 关键字,可以包含中文
     * @param recommend 默认显示的人
     * @param deprecate 默认排除的人
     * @return 符合条件的LazyDyncBean集合,最多20个元素
     */
	public List getCandidateByKeyword(String keyword, List recommend, List deprecate, String orllgid, Boolean isPrivExpression, String nbases, boolean validateSsLOGIN, boolean selectByNbase,String extend_str) {
		List candidates = new ArrayList(); // 符合条件的候选名单

		RowSet rs = null;
		try {
			String[] nbase = getNbases(nbases, validateSsLOGIN);
			if(nbase==null){
				return candidates;
			}

			// 推荐显示的人员
			StringBuffer rSql = new StringBuffer();
			if (isNotEmpty(recommend)) {
				rSql.append(" AND a0100 IN(''");
				for (int i = 0, len = recommend.size(); i < len; i++) {
					String _id = (String) recommend.get(i);
					_id = _id.substring(3);
					rSql.append(",'").append(_id).append("'");
				}
				rSql.append(")");
			}
			DbWizard dbWizard = new DbWizard(this.conn);
			int personnum = 0;
			for (int i = 0; i < nbase.length; i++) {
				StringBuffer sql = new StringBuffer();
				List params = new ArrayList();
				String n = nbase[i];
				if (isEmpty(n)) {
					continue;
				}
				StringBuffer dbpreSql = new StringBuffer();
				String tblName = n + "A01";
				dbpreSql.append(sql.length() > 0 ? " UNION " : "");
				// sexField性别指标
				StringBuffer inxsql = new StringBuffer();
				inxsql.append("create index "+tblName+"_keyword_idx on "+tblName+"(");
				dbpreSql.append("SELECT guidkey,a0000,a0100,a0101,b0110,e0122,e01a1,? AS nbase,(select dbname from dbname where pre='"+n+"') as dbname,'"+i+"' as dbpre ");
				if(isNotEmpty(sexField) && dbpreSql.indexOf(sexField)<0) {
					dbpreSql.append(",").append(sexField);
				}
				//haosl  优化sql拼接，指标设置重复的话后台sql执行的时候会报列重复  2018年3月21日  start
                boolean isCreateIdx = false;
				if (isNotEmpty(phoneField)) {
					if(dbpreSql.indexOf(phoneField)==-1)
						dbpreSql.append(",").append(phoneField);
                    if(inxsql.indexOf(phoneField)<0) {
                        inxsql.append(phoneField + ",");
                        isCreateIdx = true;
                    }
				}
				params.add(n);
				if (isNotEmpty(pyField)) {
					if(dbpreSql.indexOf(pyField)==-1)
						dbpreSql.append(",").append(pyField);
                    if(inxsql.indexOf(pyField)<0) {
                        inxsql.append(pyField + ",");
                        isCreateIdx = true;
                    }
				}
				if (isNotEmpty(emailField)) {
					if(dbpreSql.indexOf(emailField)==-1)
						dbpreSql.append(",").append(emailField);
                    if(inxsql.indexOf(emailField)<0){
                        inxsql.append(emailField+",");
                        isCreateIdx = true;
                    }
				}
				if (isNotEmpty(onlyField)) {
					if(dbpreSql.indexOf(onlyField)==-1)
						dbpreSql.append(",").append(onlyField);
                    if(inxsql.indexOf(onlyField)<0){
                        inxsql.append(onlyField+",");
                        isCreateIdx = true;
                    }
				}
				if (isNotEmpty(loginNameField) && dbpreSql.indexOf(loginNameField)==-1) {
					dbpreSql.append(",").append(loginNameField);
				}
				//haosl  优化sql拼接，指标设置重复的话后台sql执行的时候会报列重复  2018年3月21日  end
				if(isCreateIdx && !dbWizard.isExistIndex(tblName,tblName+"_keyword_idx"))//加索引
					dao.update(inxsql.substring(0, inxsql.length()-1)+")");
				dbpreSql.append(" FROM ").append(tblName).append(" WHERE (");

				dbpreSql.append(" lower(a0101) LIKE ?");

				keyword = keyword.toLowerCase();//支持大小写模糊查询
				params.add("%" + keyword + "%");
				if (pyField.length() > 0) {
					//【39501 】中信泰富特钢集团 :当唯一性指标关联工号时，在人事异动--手工选人时，可以根据工号来查人，但是当工号有大写英文字母时就查不出来，在主页查询中可以。
					dbpreSql.append(" OR ").append("lower("+pyField+")").append(" LIKE ?");
					params.add(keyword + "%");
				}
				if (emailField.length() > 0) {
					//【39501 】中信泰富特钢集团 :当唯一性指标关联工号时，在人事异动--手工选人时，可以根据工号来查人，但是当工号有大写英文字母时就查不出来，在主页查询中可以。
					dbpreSql.append(" OR ").append("lower("+emailField+")").append(" LIKE ?");
					params.add("%" + keyword + "%");
				}
				if(onlyField.length() > 0) {
					//【39501 】中信泰富特钢集团 :当唯一性指标关联工号时，在人事异动--手工选人时，可以根据工号来查人，但是当工号有大写英文字母时就查不出来，在主页查询中可以。
					dbpreSql.append(" OR ").append("lower("+onlyField+")").append(" LIKE ?");
					params.add("%" + keyword + "%");
				}

				dbpreSql.append(")").append(rSql);

				if(StringUtils.isNotBlank(extend_str)&&extend_str.toLowerCase().indexOf("salaryid")!=-1){
					//薪资发放个性化内容 添加薪资类别人员范围的限制 extend_str串内容为 salaryid=xxx;appdate=xxx zhanghua 2019-05-15
					String sql_salary = getSql_for_salary(tblName);
					dbpreSql.append(sql_salary);
 					if(StringUtils.isNotBlank(sql_salary)) {
						params.add(n.toUpperCase());
					}
				}else{
					if(!StringUtils.isEmpty(extend_str)){
						if(extend_str.startsWith("template/"))
						{
							String searchSql=getTemplateSearchSql(extend_str,isPrivExpression,n);
							if(!StringUtils.isEmpty(searchSql))
								dbpreSql.append(searchSql);
						}
					}
				}
				//权限控制sql haosl update 2017-10-27
				if(isPrivExpression) {
					String privSql = getPrivSQL(n);
					dbpreSql.append(privSql);
				}

				//通过orgid进行权限控制   haosl add 2018-6-5 start
				if(!StringUtils.isEmpty(orllgid)){
					String[] orgArray = orllgid.split(",");
					dbpreSql.append(" AND (");
					for(int a=0; a<orgArray.length; a++){
						String org = orgArray[a];
						dbpreSql.append(" (b0110 like ? or e0122 like ? or e01a1 like ?) ");
						if(a<orgArray.length-1) {
							dbpreSql.append(" or ");
						}
						params.add(org+"%");
						params.add(org+"%");
						params.add(org+"%");
					}
					dbpreSql.append(")");
				}
				//通过orgid进行权限控制   haosl add 2018-6-5 end
				sql.append(dbpreSql);

				StringBuilder _sql = new StringBuilder();//按照人员库的顺序排序 chent 20170919
				if(DbWizard.dbflag == Constant.MSSQL) {
					_sql.append("select top 20 * from ( ");
					_sql.append(sql);
					_sql.append(" ) T");
				}else {//oracle
					_sql.append("select * from ( ");
					_sql.append(sql);
					_sql.append(" ) T where rownum<=20");
				}

				rs = dao.search(_sql.toString(), params);
				while (rs.next()) {
					String nbs = nvl(rs.getString("nbase"), "");
					String a0100 = nvl(rs.getString("a0100"), "");
					if (deprecate.contains(nbs + a0100)) {
						continue;
					}
					LazyDynaBean bean = _pack(rs, emailField);
					if(selectByNbase){//区分人员库时，查出人员库的名称
						bean.set("dbname", nvl(rs.getString("dbname"), ""));
					}
					if (isNotEmpty(bean)) {
						if(personnum==20) {
							break;
						}
						candidates.add(bean);
						personnum++;
					}
				}
				if(personnum==20) {
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}

		return candidates;
	}
	/**
	 * 根据关键字从姓名拼音,姓名汉字和邮箱中查找相似的项
	 * @param keyword 关键字,可以包含中文
	 * @param addunit 是否可添加单位
	 * @param adddepartment 是否可添加部门
	 * @param orgid 指定的机构范围
	 * @return
	 */
	public List getCandidateByKeywordForUnit(String keyword,List deprecate, String addunit, String adddepartment, String orgid, boolean addpost) {
		List candidates = new ArrayList(); // 符合条件的候选机构

		StringBuffer buf = new StringBuffer();
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try {

			buf.append("select * from organization where 1=1 ");
//			if(!addpost){// 默认不添加岗位。要选择岗位时，就不能排除岗位了。chent 20170216
//				buf.append(" and codesetid<>'@K'  ");
//			}
			buf.append(" AND ? BETWEEN start_date AND end_date");
			buf.append(" AND  codeitemdesc like ?");
			list.add(new Date(System.currentTimeMillis()));
			list.add("%"+keyword+"%");
			String temp = "";
			for(int i =0 ; i<deprecate.size();i++) {
				temp+="?";
				if(i<deprecate.size()-1)
					temp+=",";
				list.add(deprecate.get(i));
			}
			if(StringUtils.isNotBlank(temp)) {
				buf.append(" AND codeitemid NOT IN ("+temp+")");
			}
//			if("1".equals(addunit)){
//				buf.append(" AND (codesetid='UN'");
//			} else {
//				buf.append(" AND (codesetid<>'UN'");
//			}
//			if("1".equals(adddepartment)){
//				if("1".equals(addunit)){
//					buf.append(" OR codesetid='UM'");
//				}else{
//					buf.append(" AND codesetid='UM'");
//				}
//			}
//			if("1".equals(addunit)){
//				buf.append(") ");
//			}
			buf.append(" ORDER BY a0000,codeitemid");
			rs = dao.search(buf.toString(), list);
			while (rs.next()) {

				String codeitemid = rs.getString("codeitemid");

				if(!StringUtils.isEmpty(orgid)){
					boolean flg = true;

					String[] orgarray = orgid.split(",");
					for(int i=0; i<orgarray.length; i++){
						String org = orgarray[i];
						if(codeitemid.startsWith(org)){
							flg = false;
						}
					}
					if(flg){
						continue;
					}
				}


				LazyDynaBean bean = new LazyDynaBean();

				String setId = nvl(rs.getString("codesetid"), "");
				if(!"1".equals(addunit) && "UN".equalsIgnoreCase(setId)){
					continue;
				}
				if(!"1".equals(adddepartment) && "UM".equalsIgnoreCase(setId)){
					continue;
				}
				if(!addpost && "@K".equalsIgnoreCase(setId)){
					continue;
				}

				String id = nvl(codeitemid, "");
				String name = nvl(rs.getString("codeitemdesc"), "");

				String _id = PubFunc.encrypt(id);
				bean.set("id", _id);
				bean.set("name", name);
				bean.set("shortName", truncate(name, 18));

				bean.set("rawType", setId);
				bean.set("type", UNIT);
				bean.set("isquery", "1");

				candidates.add(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}

		return candidates;
	}

	/** 新增一个bean，设置人员的基本信息 */
	public LazyDynaBean _pack(RowSet rs, String emailField) throws SQLException {
		String nbase = nvl(rs.getString("nbase"), "");
		String a0100 = nvl(rs.getString("a0100"), "");
		String a0101 = nvl(rs.getString("a0101"), "");
		String b0110 = nvl(rs.getString("b0110"), "");
		String e0122 = nvl(rs.getString("e0122"), "");
		String e01a1 = nvl(rs.getString("e01a1"), "");
		String guidkey = nvl(rs.getString("guidkey"), "");
		String sex = "";
		if(isNotEmpty(sexField))
			sex = nvl(rs.getString(sexField), "");

		LazyDynaBean bean = new LazyDynaBean();
		bean.set("id", PubFunc.encrypt(nbase + a0100).replaceAll("@", "＠"));
		bean.set("name", a0101);
		bean.set("type", PERSON);
		if(isNotEmpty(loginNameField)) {
			bean.set("userName",rs.getString(loginNameField));
		}
		if(isNotEmpty(onlyField)) {
			bean.set("onlyName",nvl(rs.getString(onlyField), ""));
		}
		bean.set("b0110", PubFunc.encrypt(b0110));
		bean.set("e0122", PubFunc.encrypt(e0122));
		bean.set("e01a1", PubFunc.encrypt(e01a1));
		bean.set("gender", "1".equals(nvl(sex, "1")) ? MALE : FEMALE);
		//误提交，还原成加密guidkey 串  haosl 2020019
        bean.set("guidkey", PubFunc.encrypt(guidkey));
        // vfs改造用非加密的guidkey
        bean.set("guidkey_str", guidkey);
		//添加人员库标识，防止分库时加入了别的人员库的人。为了解决 bug 29072   2017-08-07 haosl
		bean.set("nbase", PubFunc.encrypt("nbs_"+nbase));
		if (isNotEmpty(phoneField)) {
			bean.set("c0104", nvl(rs.getString(phoneField), ""));
		}
		
		String unit = AdminCode.getCodeName("UN", b0110);
		String shortUnit = truncate(unit, 18);
		String dept = AdminCode.getCodeName("UM", e0122);
		String shortDept = truncate(dept, 18);
		String post = AdminCode.getCodeName("@K", e01a1);
		String shortPost = truncate(post, 12);
		bean.set("shortName", truncate(a0101, 8));
		bean.set("unit", unit);
		bean.set("shortUnit", shortUnit);
		bean.set("dept", dept);
		bean.set("shortDept", shortDept);
		bean.set("post", post);
		bean.set("shortPost", shortPost);
		
		String photo = getPhotoPath(nbase, a0100);
		bean.set("photo", photo);
		
		String email = "";
		if (isNotEmpty(emailField)) {
			email = nvl(rs.getString(emailField), "");
		}
		bean.set("email", email);
		
		return bean;
	}
	
	/** ###################################### 业务功能函数 ############################################### */
	 /** 得到电子邮箱指标 */
	public static String getEmailField(Connection conn) {
		String emailId = "";
		try {
			/*
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sql = new StringBuffer("select Str_Value from constant where constant='SS_EMAIL'");
			RowSet rs = dao.search(sql.toString());
			if(rs.next()){
				if(!rs.getString("Str_Value").toString().equalsIgnoreCase("#"))
				{					
					emailId = rs.getString("Str_Value");
				}
			}*/
			emailId=ConstantParamter.getEmailField().toLowerCase(); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return emailId;
	}
	
	/** 移动电话指标 */
	public static String getPhoneField(Connection conn) {
		String phone = "";
		try {
			/*
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sql = new StringBuffer("select Str_Value from constant where constant='SS_MOBILE_PHONE'");
			RowSet rs = dao.search(sql.toString());
			if(rs.next()){
				if(!rs.getString("Str_Value").toString().equalsIgnoreCase("#")){					
					phone = rs.getString("Str_Value");
				}
			}*/
			phone=ConstantParamter.getMobilePhoneField().toLowerCase(); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return phone;
	}
	/** 移动性别指标 */
	public static String getSexField(Connection conn) {
		String sex = "";
		try {
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sql = new StringBuffer("select itemid from fielditem where fieldsetid='A01' and codesetid='AX' and useflag='1' order by itemid");
			RowSet rs = dao.search(sql.toString());
			boolean flg = false;
			while(rs.next()){//如果有
				String itemid = rs.getString("itemid");
				if("A0107".equalsIgnoreCase(itemid)){
					flg = true;
					break;
				}
			}
			if(!flg){//如果没有"A0107"，则取第一个
				rs.beforeFirst();
				if(rs.next()){
					sex = rs.getString("itemid");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sex;
	}
	
	/**
	 * 获得唯一性指标
	 * @param conn
	 * @return
	 */
	public static String getOnlyField(Connection conn) {
		String onlyFiled = "";
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
		String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");//是否定义唯一性指标 0：没定义
		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一性指标值
		if(!"0".equals(uniquenessvalid)&&StringUtils.isNotBlank(onlyname) && fieldInA01(onlyname)){
			onlyFiled = onlyname;
		}
		return onlyFiled;
	}
	
	/** 取得拼音指标 */
	public static String getPinYinField(Connection conn) {
		// 获取拼音简码的字段
		Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
		String pinyinFld = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
		if (null == pinyinFld || "".equals(pinyinFld.trim())) {
			pinyinFld = "";
		}

		if (!fieldInA01(pinyinFld)) {
			pinyinFld = "";
		}

		return pinyinFld;
	}
	
	/** 是否是主集指标并已构库 */
	public static boolean fieldInA01(String field) {
		boolean inA01 = false;
		if (null == field || "".equals(field.trim())) {
			return inA01;
		}

		FieldItem fieldItem = DataDictionary.getFieldItem(field, "a01");
		inA01 = null != fieldItem && "1".equals(fieldItem.getUseflag());

		return inA01;
	}
	
	/** 获取人员图像所在路径 */
	public String getPhotoPath(String nbase, String a0100) {
	    PhotoImgBo imgBo = new PhotoImgBo(conn);
	    //xus 20/5/21 选人控件照片改为证件照 【60599】VFS+UTF-8：人事异动，404表单，选人/手工选人，选择到右侧已选框中的照片与员工管理中信息浏览的照片不一致，见附件
	    imgBo.setIdPhoto(true);
        return imgBo.getPhotoPathLowQuality(nbase, a0100);
	}
	
	/** 应用库 */
	public String[] getNbase(boolean validateSsLOGIN) {
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			if(validateSsLOGIN){//启用认证库校验，则取认证库
				RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN");
				if (login_vo != null) {
					String strpres = login_vo.getString("str_value");
					String[] split = strpres.split(",");
					StringBuffer sql = new StringBuffer("select Pre from DBName ");
					sql.append("where Pre in(");
					for (String string : split) {
						sql.append("'"+string+"',");
					}
					sql.setLength(sql.length()-1);
					sql.append(") order by dbid");
					rs = dao.search(sql.toString());
					strpres = "";
					while (rs.next()) {
						strpres+=(rs.getString("Pre")+",");
					}
					
					if(StringUtils.isNotEmpty(strpres)){
					    strpres = strpres.substring(0, strpres.length()-1);
					    return strpres.split(",");					    
					} else
					    return new String[0];
				}
			}else{// 不启用，返回全部人员库
				StringBuilder strpres = new StringBuilder();
				rs = dao.search("select Pre from DBName order by dbid");
				while (rs.next()) {
					strpres.append(rs.getString("Pre")+",");
				}
				strpres.deleteCharAt(strpres.length()-1);
				
				return strpres.toString().split(",");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return new String[0];
	}
	
	/** 单位 */
	public static final String UNIT = "unit";
	/** 人员 */
	public static final String PERSON = "person";
	/** 单位处于收起状态 */
	public static final String FOLD = "fold";
	/** 单位处于展开状态 */
	public static final String UNFOLD = "unfold";
	/** 男 */
	public static final String MALE = "male";
	/** 女 */
	public static final String FEMALE = "female";
	
	/** ###################################### 其它功能函数 ############################################### */
	
	/** 如果value为空时返回defaultValue，否则返回value */
	public static String nvl(String value, String defaultValue) {
		return value == null || value.trim().length() == 0 ? defaultValue : value;
	}
	
	/** 判断一个字符串或者集合是否为空 */
	public static boolean isEmpty(Object val) {
		if (val == null) {
			return true;
		} else if (val instanceof String) {
			String obj = (String) val;
			return "".equals(obj.trim());
		} else if (val instanceof Collection) {
			Collection obj = (Collection) val;
			return obj.size() == 0;
		} else if (val instanceof Map) {
			Map obj = (Map) val;
			return obj.size() == 0;
		}

		return false;
	}
	
	/** 判断一个字符串或者集合是否不为空 */
	public static boolean isNotEmpty(Object val) {
		return !isEmpty(val);
	}
	
	/** 判断是否是中文字符 */
	public static boolean isChineseChar(char c) {
		return Pattern.matches("[\u4e00-\u9fa5]", String.valueOf(c));
	}
	
	/** 截取字符串的前8个字节，汉字保证完整 */
	public static String truncate(String s, int max) {
		if (isEmpty(s)) {
			return "";
		}
		
		int len = s.length();
		int iByte = 0;
		int index = 0;
		
		for (index = 0; index < len; index++) {
			char c = s.charAt(index);
			if (isChineseChar(c)) {
				iByte += 2;
			} else {
				iByte += 1;
			}
			
			if (iByte >= max) {
				break;
			}
		}
		
		index = index >= len ? len : index + 1;
		return s.substring(0, index);
	}
	
	/**
	 * 获取机构范围sql
	 * @param orgid：指定的组织机构
	 * @return sql
	 */
	private String getOrgSql(String orgid){
		String orgSql = "";
		String sortAfterStr = "";
		/** 重新整理机构:只留顶层机构。如'01,0101'=>'01' */
		String[] orgStr = orgid.split(",");
		for(int i = 0; i < orgStr.length; i++){
			boolean flag = true;
			for(int j = 0; j < orgStr.length; j++){
				int Alength = orgStr[i].length();
				int Blength = orgStr[j].length();
				if(Alength > Blength){
					String str = orgStr[i].substring(0, Blength);
					if(str.equalsIgnoreCase(orgStr[j])){
						flag = false;
					}
				}
			}
			if(flag == true){
				sortAfterStr+=(","+orgStr[i].trim());
			}
		}
		/** 整合查询语句 */
		orgSql = " and codeitemid in(";
		String[] AfterStr = sortAfterStr.split(",");
		for(int i = 0; i < AfterStr.length; i++){
			if(i == 0){
				orgSql = orgSql +"'"+AfterStr[i]+"'";
			}else {
				orgSql = orgSql +",'"+AfterStr[i]+"'";
			}
			if(i == AfterStr.length - 1){
				orgSql = orgSql + ")";
			}
		}
		return orgSql;
	}
	/**
	 * 加载业务用户人员信息  zhaoxg add
	 * @param groupid
	 * @param level 层级
	 * @param str 个性条件  根据自己模块随便定义  不互相影响即可
	 * @param selfUserIsExceptMe 是否排除自己
	 * @return
	 */
	public List loadUserNodes(String groupid,String level,String ancester,String str, boolean selfUserIsExceptMe) {
		
		List subunit = new ArrayList();
		StringBuffer strsql=new StringBuffer();
		RowSet rset=null;
		groupid = groupid==null|| "".equals(groupid)?"1":groupid;

		/**对超级用户组特殊处理*/
		if("1".equals(groupid))
		{
			strsql.append("select username,FullName,b.groupid as sss, nbase, a0100 from operuser a left join usergroup b ");
			strsql.append(" on a.username=b.groupname ");
			strsql.append( "where a.groupid=? ");
			strsql.append(" and userflag='10' and roleid=0 ");
			if(selfUserIsExceptMe){
				strsql.append(" and username<>?");
			}
			strsql.append(" order by sss,InGrpOrder");				
		}
		else
		{
			strsql.append("select username,FullName,b.groupid as sss, nbase, a0100 from operuser a left join usergroup b ");
			strsql.append(" on a.username=b.groupname ");
			strsql.append( "where a.groupid=? ");
			strsql.append(" and roleid=0  ");
			if(selfUserIsExceptMe){
				strsql.append(" and username<>? ");
			}
			strsql.append(" order by sss,InGrpOrder");
		}

		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList list = new ArrayList();
			list.add(groupid);
			if(selfUserIsExceptMe){
				list.add(this.userview.getUserName());
			}
			rset=dao.search(strsql.toString(),list);
			while(rset.next())
			{
	            String userName = rset.getString("username");
	            String FullName = rset.getString("FullName");
				LazyDynaBean bean = new LazyDynaBean();
				String id = PubFunc.encrypt(userName).replaceAll("@", "＠");
				
				// 业务用户显示规则：显示全称，如果没有全称则显示账号名。
				String nametext = userName;
				if(StringUtils.isNotEmpty(FullName)){
					nametext = FullName;
				}
				bean.set("id", id);
				bean.set("name", nametext);
				bean.set("userName", userName);
				bean.set("ancester", nvl(ancester, id));
				bean.set("attachTo", nvl(groupid, ""));
				bean.set("shortName", truncate(nametext, 18));
				bean.set("type", PERSON);
				bean.set("level", Integer.parseInt(nvl(level, "0"))+1+"");
				bean.set("display", FOLD);
				bean.set("photo", "/components/personPicker/image/male.png");
	    		boolean temp=true;
	    		//薪资报批中调用 只显示有该薪资类别 的用户。
	    		if(str!=null&&str.length()>0&&str.indexOf("salary")!=-1)
	    		{
	    			String salaryid = str.split("/")[1];
	    			String cstate="0";  //薪资
	    			RowSet rs=dao.search("select cstate from salarytemplate where salaryid="+PubFunc.decrypt(SafeCode.decode(salaryid)));
	    			if(rs.next())
	    			{
	    				if(rs.getString("cstate")!=null&& "1".equals(rs.getString("cstate")))
	    					cstate="1";
	    			}
	    			UserView userView=new UserView(rset.getString("username"),conn);
	    			userView.canLogin(false);
	    			if("0".equals(cstate))
	    			{
	    				if(!userView.isHaveResource(IResourceConstant.GZ_SET, PubFunc.decrypt(SafeCode.decode(salaryid))))
	    					temp=false;
	    			}
	    			else
	    			{
	    				if(!userView.isHaveResource(IResourceConstant.INS_SET, PubFunc.decrypt(SafeCode.decode(salaryid))))
	    					temp=false;
	    			}
	    		}
	    		if(temp)
	    			subunit.add(bean);
			}

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return subunit;
	}
	/**
	 * 获取用户组信息  zhaoxg add
	 * @param groupid
	 * @param level
	 * @param str
	 * @return
	 */
	public List loadUsergrops(String groupid,String level,String ancester,String str) {
		
		List subunit = new ArrayList();
		StringBuffer strsql=new StringBuffer();
		RowSet rset=null;
		groupid = groupid==null|| "".equals(groupid)?"1":groupid;
		strsql.append("select username,b.groupid as sss from operuser a left join usergroup b ");
		strsql.append(" on a.username=b.groupname ");		
		strsql.append(" where roleid=1 and a.groupid=? order by sss,InGrpOrder");
		ArrayList list = new ArrayList();
		list.add(groupid);
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			rset=dao.search(strsql.toString(),list);
			while(rset.next())
			{
	            String userName = rset.getString("username");
				LazyDynaBean bean = new LazyDynaBean();
				String id = PubFunc.encrypt(rset.getString("sss")).replaceAll("@", "＠");
				bean.set("id", id);
				bean.set("ancester", nvl(ancester, id));
				bean.set("attachTo", nvl(groupid, ""));
				bean.set("name", userName);
				bean.set("shortName", truncate(userName, 18));
				bean.set("rawType", "UN");
				bean.set("type", UNIT);
				bean.set("level", Integer.parseInt(nvl(level, "0"))+1+"");
				bean.set("display", FOLD);
				subunit.add(bean);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return subunit;
	}
	/**
	 * 加载人员库
	 * @param nbases：配置的人员库
	 * @param validateSsLOGIN：是否启用认证库校验
	 * @return
	 */
	public List loadNbases(String nbases,boolean validateSsLOGIN) {
		List list = new ArrayList();
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try
		{
			String[] nbase = getNbases(nbases, validateSsLOGIN);
			if(nbase==null){
				return null;
			}
			
			StringBuffer sqlIn = new StringBuffer("''");
			for(int i = 0; i < nbase.length; i++){
				sqlIn. append(",'");
				sqlIn. append(nbase[i]);
				sqlIn. append("'");
			}
			String sql = "select * from dbname where Pre in (" + sqlIn.toString() + ") order by dbid";
			rs = dao.search(sql);
			
			while(rs.next()){
				String pre = rs.getString("pre");
				String dbname = rs.getString("dbname");

				LazyDynaBean bean = new LazyDynaBean();
				bean.set("id", PubFunc.encrypt("nbs_" + pre));
				bean.set("ancester", "");
				bean.set("attachTo", "");
				bean.set("name", dbname);
				bean.set("shortName", truncate(dbname, 18));
				bean.set("rawType", "UN");
				bean.set("type", UNIT);
				bean.set("level", "0");
				bean.set("display", FOLD);
				list.add(bean);
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}
	/**
	 * 业务用户查询框模糊查询
	 * @param keyword  模糊内容
	 * @param str 自定义限制条件
	 * @param selfUserIsExceptMe 是否排除自己
	 * @return
	 */
	public List queryPerson(String keyword,String str,boolean selfUserIsExceptMe){
		List subunit = new ArrayList();
		RowSet rset=null;
		try
		{
			StringBuffer mainSql = new StringBuffer();
			mainSql.append("select a0100,nbase,username,fullname,a.groupid as groupid, b.groupid as sss,userflag,roleid,a.email,b.GroupName");
			mainSql.append(" from OperUser a, usergroup b");	
			mainSql.append(" where a.GroupId = b.GroupID ");
			mainSql.append(" and roleid=0  and (");
			mainSql.append(" username like ? escape '\\'");
			mainSql.append(" or fullname like ? escape '\\'");	
			mainSql.append(" or a.email like ? escape '\\'");	
			mainSql.append(" ) ");
			if(selfUserIsExceptMe){
				mainSql.append(" and username<>?");
			}
			ArrayList list = new ArrayList();
			list.add("%"+keyword+"%");
			list.add("%"+keyword+"%");
			list.add("%"+keyword+"%");
			if(selfUserIsExceptMe){
				list.add(this.userview.getUserName());
			}
			ContentDAO dao=new ContentDAO(this.conn);
			rset=dao.search(mainSql.toString(),list);
			while(rset.next())
			{
				String FullName = rset.getString("FullName");
	            String userName = rset.getString("username");
	            
	            // 业务用户显示规则：显示全称，如果没有全称则显示账号名。
				String nametext = userName;
				if(StringUtils.isNotEmpty(FullName)){
					nametext = FullName;
				}
	            
				LazyDynaBean bean = new LazyDynaBean();
				String id = PubFunc.encrypt(userName).replaceAll("@", "＠");
				bean.set("id", id);
				bean.set("name", nametext);
				bean.set("userName", userName);
				bean.set("dept", rset.getString("GroupName"));
				bean.set("shortName", truncate(nametext, 18));
				bean.set("type", PERSON);
				bean.set("display", FOLD);
				bean.set("email", nvl(rset.getString("email"),""));
				bean.set("shortDept", truncate(nvl(rset.getString("GroupName"),""),18));
				bean.set("photo", "/components/personPicker/image/male.png");
	    		boolean temp=true;
	    		//薪资报批中调用 只显示有该薪资类别 的用户。
	    		if(str!=null&&str.length()>0&&str.indexOf("salary")!=-1)
	    		{
	    			String salaryid = str.split("/")[1];
	    			String cstate="0";  //薪资
	    			RowSet rs=dao.search("select cstate from salarytemplate where salaryid="+PubFunc.decrypt(SafeCode.decode(salaryid)));
	    			if(rs.next())
	    			{
	    				if(rs.getString("cstate")!=null&& "1".equals(rs.getString("cstate")))
	    					cstate="1";
	    			}
	    			UserView userView=new UserView(rset.getString("username"),conn);
	    			userView.canLogin(false);
	    			if("0".equals(cstate))
	    			{
	    				if(!userView.isHaveResource(IResourceConstant.GZ_SET, PubFunc.decrypt(SafeCode.decode(salaryid))))
	    					temp=false;
	    			}
	    			else
	    			{
	    				if(!userView.isHaveResource(IResourceConstant.INS_SET, PubFunc.decrypt(SafeCode.decode(salaryid))))
	    					temp=false;
	    			}
	    		}
	    		if(temp)
	    			subunit.add(bean);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return subunit;
	}
	public List _decrypt(List src) {
		List result = new ArrayList(); // 解密后id的集合
		if (PersonPickerSupport.isNotEmpty(src)) {
			for (int i = 0, len = src.size(); i < len; i++) {
				String id = PubFunc.decrypt(((String) src.get(i)).trim());
				if (PersonPickerSupport.isNotEmpty(id)) {
					result.add(id);
				}
			}
		}
		
		return result;
	}
	public String getNameByNbsA0100(String nbase, String a0100) {
		String name = null;
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			String tableName = nbase + "A01";
			String sql = "select a0101 from "+tableName+" where A0100=?";
			rs = dao.search(sql, Arrays.asList(new Object[] {a0100}));
			if (rs.next()) {
				name = rs.getString("a0101");
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return name;
	}
	private String[] getNbases(String nbases, boolean validateSsLOGIN){
		String[] nbase = {};
		
		if(!StringUtils.isEmpty(nbases)){// 设定的人员库
			nbase = nbases.split(",");
		}else {
			nbase = getNbase(validateSsLOGIN);
			if(!userview.isSuper_admin()){//非超级管理员取认证应用库和人员库的交集
				nbase = getNbase(nbase);
			}
		}
		
		return nbase;
	}
	
	/*
	 *  //wlh修改添加检索过滤
	 */
	private String getFilterSQL(UserView uv,String BasePre,ArrayList alUsedFields,Connection conn1,String filter_factor)
	{
		String sql=" (1=1) ";
		try{
			filter_factor=filter_factor.replaceAll("@","\"");
			ContentDAO dao=new ContentDAO(conn1);
			//this.filterfactor="性别 <> '1'";
			int infoGroup = 0; // forPerson 人员
			int varType = 8; // logic								
			String whereIN=InfoUtils.getWhereINSql(uv,BasePre);
			whereIN="select a0100 "+whereIN;							
			YksjParser yp = new YksjParser( uv ,alUsedFields,
					YksjParser.forSearch, varType, infoGroup, "Ht", BasePre);
			YearMonthCount ymc=null;							
			yp.run_Where(filter_factor, ymc,"","", dao, whereIN,conn1,"A", null);
			
			sql=BasePre+"A01.a0100 in (select  a0100 from "+yp.getTempTableName()+" where "+yp.getSQL()+" )";
		//	sql=yp.getSQL();
		}catch(Exception e)
		{
			e.printStackTrace();
		}	
		return sql;
	}
	
	/*
	 * 人事异动按检索条件查询。
	 */
	public String getTemplateSearchSql(String extend_str,boolean isPrivExpression,String n)
	{
		String sql="";
		ArrayList alUsedFields=null;
		String template_extend_str=null;
		if(!StringUtils.isEmpty(extend_str)){
			if(extend_str.startsWith("template/"))
			{
				TemplateParam param=new TemplateParam(this.conn,this.userview,Integer.valueOf(extend_str.split("/")[1]));
				if("1".equalsIgnoreCase(param.getFilter_by_factor()))
				{
					template_extend_str=param.getFactor();
				}
			}
		}
		if(!StringUtils.isEmpty(template_extend_str))
	    { 
		  alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
		  /**
		   *保持和以前的程序兼容，因为先前单位编码和职位编码、
		   *单位名称和职位名称未统一起来 
		   */
		  FieldItem item=new FieldItem();
		  item.setItemid("b0110");
		  item.setCodesetid("UN");
		  item.setItemdesc("单位编码");
		  item.setItemtype("A");
		  item.setFieldsetid("A01");
		  item.setUseflag("2");
		  item.setItemlength(30);
		  item.setItemlength(30);
		  alUsedFields.add(item);

		  item=new FieldItem();
		  item.setItemid("e01a1");
		  item.setCodesetid("@K");
		  item.setItemdesc("职位编码");
		  item.setItemtype("A");
		  item.setFieldsetid("A01");
		  item.setUseflag("2");
		  item.setItemlength(30);
		  item.setItemlength(30);
		  alUsedFields.add(item);	

		  int infoGroup = 0; // forPerson 人员
		  int varType = 8; // logic								
		  String whereIN=InfoUtils.getWhereINSql(userview,n,isPrivExpression?"0":"1");
		  whereIN="select a0100 "+whereIN;							
		  YksjParser yp = new YksjParser( userview ,alUsedFields,YksjParser.forSearch, varType, infoGroup, "Ht", n);
		  YearMonthCount ymc=null;							
		  yp.run_Where(template_extend_str, ymc,"","", dao, whereIN,conn,"A", null);
		 
		   sql=" and A0100 in (select  "+n+"A01.a0100 from "+yp.getTempTableName()+" where "+yp.getSQL()+" and "+n+"A01.a0100="+yp.getTempTableName()+".a0100  )";
	    }
		return sql;
	}
	
	/**
	 * 得到权限范围sql
	 * 
	 * haosl
	 * 
	 * 优先级：操作范围-》人员范围（因业务范围涉及模块，选人控件暂不支持业务范围控制）
	 * @return
	 * @throws GeneralException 
	 */
	public String getPrivSQL(String nbase) throws GeneralException {
			// 业务用户:操作单位 > 管理范围
		StringBuffer sql = new StringBuffer();
		try {
			String unit_id = this.userview.getUnit_id();
			if("UN`".equalsIgnoreCase(unit_id)){
					sql.append(" AND 1=1 ");
			}else if(StringUtils.isNotEmpty(unit_id) && unit_id.length()>2) {
				String[] unitArr = unit_id.split("`");
				sql.append(" AND (");
				StringBuffer sql1 = new StringBuffer();
				for(int j=0;j<unitArr.length;j++) {
					String em = unitArr[j].substring(2);
					String unit = AdminCode.getCodeName("UN",em);
					String dept = AdminCode.getCodeName("UM", em);
					String field = "";
					if(StringUtils.isNotEmpty(unit)) {
						field = "b0110";
					} else if (StringUtils.isNotEmpty(dept)) {
						field = "e0122";
						
					}
					if(StringUtils.isNotBlank(field)){//设置的操作单位或者管理范围的单位被删除，导致field为空串，组装sql不正确。
						if(StringUtils.isNotBlank(sql1.toString())) {
							sql1.append(" or ");
						}
						sql1.append(field+" like '"+em+"%'");
					}
				}
				if(StringUtils.isBlank(sql1.toString())){
					sql1.append(" 1=1 ");
				}
				sql.append(sql1.toString());
				sql.append(")");
			} else {
				//改成默认莫支持历史记录查询，后期考虑增加是否支持历史记录查询的配置 haosl 20200221
				String sql_where="select "+nbase+"A01.a0100 "+ userview.getPrivSQLExpression(nbase, false);
				sql.append(" AND A0100 in("+ sql_where +")");
			}
			return sql.toString();
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	public String getSql_for_salary(String tblName) {
		String sql = "";
		String tablename="t#"+this.userview.getUserName()+"_gz_Ins";
		
		DbWizard dbw=new DbWizard(this.conn);
		if(dbw.isExistTable(tablename, false)) {
			sql = " and exists (select null from "+tablename+" where "+tablename+".a0100="+tblName+".a0100 and upper("+tablename+".DBNAME)=?)";
		}
		return sql;
	}
}
