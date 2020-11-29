package com.hjsj.hrms.module.kq.config.scheme.businessobject.impl;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.module.kq.config.scheme.businessobject.SchemeMainService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.util.KqDataUtil;
import com.hjsj.hrms.module.kq.util.KqCtrlParamUtil;
import com.hjsj.hrms.module.kq.util.KqPrivForHospitalUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

public class SchemeMainServiceImpl implements SchemeMainService{

	// 基本属性
	private Connection conn = null;
	private UserView userview;

	public SchemeMainServiceImpl(Connection conn, UserView userview) {
		this.conn = conn;
		this.userview = userview;
	}
	/**
	 * 获取考勤方案列表
	 * @return
	 * @throws GeneralException
	 */
	@Override
    public HashMap getSchemeDataList(int currentPage, int pageSize, ArrayList inputValue) throws GeneralException {
		HashMap mapsFinally = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		//最后数据集合
		ArrayList<HashMap> list = new ArrayList<HashMap>();
		//查询时候的sql
		ArrayList<String> listSqlParam = new ArrayList<String>();
		int totalCount = 0;
		try {
			StringBuffer sql = new StringBuffer();
			//考勤业务范围start
			StringBuffer sbfWhere = new StringBuffer();
			StringBuffer unitSql = new StringBuffer();
			String unitcodes=userview.getUnitIdByBusi("11");
			String[] temps=unitcodes.split("`");
			if(!this.userview.isSuper_admin()) {//超级管理员可以看到全部
				for(int i=0;i<temps.length;i++) {
					if(temps[i].trim().length()>0) {
						unitSql.append(" or b0110 like ? ");
						listSqlParam.add(temps[i].substring(2) + "%");
					}
				}
			}
			sql.append("select scheme_id,b0110,name,clerk_username,clerk_fullname,reviewer_id,reviewer_fullname,org_scope,is_validate,create_time,create_user,create_fullname " +
						"from kq_scheme where 1=1");
			if(StringUtils.isNotBlank(unitSql.toString())) {
				sql.append(" and (" + unitSql.substring(3) + ")");
				sbfWhere.append(" and (" + unitSql.substring(3) + ")");
			}
			//考勤业务范围end
			//考勤方案搜索框start
			StringBuffer sbf = new StringBuffer();
			if(inputValue != null && inputValue.size() > 0) {
				for(int i = 0; i < inputValue.size(); i++) {
					sbf.append(" or name like ?");
					listSqlParam.add("%" + inputValue.get(i) + "%");
				}
				sql.append(" and (" + sbf.substring(3) + ")");
				sbfWhere.append(" and (" + sbf.substring(3) + ")");
			}
			
			sql.append(" order by scheme_id desc");
			//考勤方案搜索框end
			ArrayList<String> userNames = new ArrayList<String>();
			rs = dao.search(sql.toString(), listSqlParam, pageSize, currentPage+1);
			while(rs.next()) {
				HashMap map = new HashMap();

				StringBuffer org_scope = new StringBuffer();
				String org_scopes = rs.getString("org_scope");
				String[] org_scopeArray = org_scopes.split(",");
				for(int i = 0; i < org_scopeArray.length; i++) {
					String org_scope_on = org_scopeArray[i];
					org_scope.append("," + (AdminCode.getCodeName("UN", org_scope_on).length()==0?AdminCode.getCodeName("UM", org_scope_on):AdminCode.getCodeName("UN", org_scope_on)));
				}
				String b0110 = rs.getString("b0110");
				String scheme_id = rs.getString("scheme_id");
				String reviewer_id_ = rs.getString("reviewer_id");
				map.put("encrypt_scheme_id", PubFunc.encrypt(scheme_id));//id
				map.put("name", rs.getString("name"));//名称
				map.put("org_scope", org_scope.substring(1));//应用范围
				String clerk_username = rs.getString("clerk_username");
				if(!userNames.contains(clerk_username)) {
					userNames.add(clerk_username);
				}
				map.put("clerk_username_one", clerk_username);
				String clerk_fullname = rs.getString("clerk_fullname");
				map.put("clerk_username", clerk_username + (StringUtils.isBlank(clerk_fullname)?"":" (" + clerk_fullname + ")"));//考勤人
				map.put("reviewer", KqDataUtil.nullif(rs.getString("reviewer_fullname")));//审核人
				map.put("reviewer_id_",  StringUtils.isBlank(reviewer_id_)?"" : PubFunc.encrypt(reviewer_id_));//审核人
				map.put("reviewer_imgPath", getReviewImgPath(rs.getString("reviewer_id")));
				map.put("b0110", AdminCode.getCodeName("UN", b0110).length()==0?AdminCode.getCodeName("UM", b0110):AdminCode.getCodeName("UN", b0110));//所属机构
				map.put("is_validate", rs.getString("is_validate"));//是否生效
				map.put("create_time", PubFunc.FormatDate(rs.getTimestamp("create_time"), "yyyy-MM-dd HH:mm"));//创建时间
				map.put("expanded", true);
				map.put("create_user", StringUtils.isBlank(rs.getString("create_fullname"))?rs.getString("create_user"):rs.getString("create_fullname"));//创建人
				ArrayList listFillingAgencys = this.getFillingAgencysTree(scheme_id);
				map.put("children", listFillingAgencys);//数据上报机构
				for(int i=0;i<listFillingAgencys.size();i++) {
					HashMap orgmap = (HashMap)listFillingAgencys.get(i);
					String orgUserName = (String)orgmap.get("y_clerk_username");
					if(!userNames.contains(orgUserName)) {
						userNames.add(orgUserName);
					}
				}
				list.add(map);
			}
			//*更换关联用户问题  查的时候反查start*//
			if(userNames.size() > 0) {
				// 54651 业务用户关联自助用户的姓名
				HashMap<String, String> userFullMap = getUserFullMap(rs, dao, userNames);
				for(int i=0;i<list.size();i++) {
					HashMap map = list.get(i);
					String clerk_username = (String)map.get("clerk_username_one");
					String clerk_fullname = userFullMap.get(clerk_username);
					map.put("clerk_username", clerk_username + (StringUtils.isBlank(clerk_fullname)?"":" (" + clerk_fullname + ")"));//考勤人
					ArrayList listFillingAgencys = (ArrayList)map.get("children");
					for(int j=0;j<listFillingAgencys.size();j++) {
						HashMap orgmap = (HashMap)listFillingAgencys.get(j);
						String orgUserName = (String)orgmap.get("y_clerk_username");
						String y_clerk_fullname = userFullMap.get(orgUserName);
						orgmap.put("clerk_username", orgUserName + " ("+y_clerk_fullname+")");
						orgmap.put("y_clerk_fullname", y_clerk_fullname);
					}
				}
			}
			//*更换关联用户问题end*//
			//获取总的数量
			rs = dao.search("select count(*) as count from kq_scheme where 1=1 " + (sbfWhere.length() == 0?"":sbfWhere),listSqlParam);
			if(rs.next()) {
				totalCount = rs.getInt("count");
			}

			mapsFinally.put("children", list);
			mapsFinally.put("totalCount", totalCount);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return mapsFinally;
	}

	/**
	 * 获取数据上报机构
	 * @param dbnames 选中的人员库
	 * @return
	 * @throws GeneralException
	 */
	@Override
    public ArrayList getFillingAgencysTree(String scheme_id) {
		ArrayList listFillingAgencys = new ArrayList();
		try {
			KqCtrlParamUtil kqCtrlParam = new KqCtrlParamUtil(this.conn,this.userview, scheme_id);
			List elementList = kqCtrlParam.getValue(KqCtrlParamUtil.FILLING_AGENCY, KqCtrlParamUtil.FILLING_AGENCYS);
			for (int i = 0; i < elementList.size(); i++) {
				Element element = (Element) elementList.get(i);
				String reviewId = element.getAttributeValue("reviewer_id");//审核人id
				HashMap map = new HashMap();
				String org_id = element.getAttributeValue("org_id").substring(2);
				map.put("name", AdminCode.getCodeName("UN", org_id).length()==0?AdminCode.getCodeName("UM", org_id):AdminCode.getCodeName("UN", org_id));
				map.put("clerk_username", element.getAttributeValue("clerk_username") + " (" + element.getAttributeValue("clerk_fullname") + ")");
				map.put("y_clerk_username", element.getAttributeValue("clerk_username"));
				map.put("y_clerk_fullname", element.getAttributeValue("clerk_fullname"));
				map.put("reviewer", element.getAttributeValue("reviewer_fullname"));
				map.put("reviewer_id",  StringUtils.isBlank(reviewId)?"" : PubFunc.encrypt(reviewId));
				map.put("reviewer_imgPath", getReviewImgPath(reviewId));
				map.put("org_id",org_id);
				map.put("y_org_id",element.getAttributeValue("org_id"));
				// 55945  删除审核人之前校验是否存在待办需要用加密后的org_id
				map.put("org_id_e", PubFunc.encrypt(org_id));
				map.put("leaf", true);
				listFillingAgencys.add(map);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return listFillingAgencys;
	}

	/**
	 * 获取获得方案详细信息
	 * scheme_id: 加密的
	 * @return
	 * @throws GeneralException
	 */
	@Override
    public HashMap getSchemeDetailDataList(String scheme_id) throws GeneralException {
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			if(StringUtils.isNotBlank(scheme_id)) {
				rs = dao.search("select * from kq_scheme where scheme_id=?",Arrays.asList(new String[] {PubFunc.decrypt(scheme_id)}));
				if(rs.next()) {
					HashMap mapItemClass = getClassItemData(rs.getString("class_ids"));//获取考勤班次和考勤项目的数据
					//管理范围
					StringBuffer org_scope_fullname = new StringBuffer();
					StringBuffer org_scope = new StringBuffer();
					ArrayList<String> org_scope_encrypt = new ArrayList<String>();
					String org_scopes = rs.getString("org_scope");
					String[] org_scopeArray = org_scopes.split(",");
					for(int i = 0; i < org_scopeArray.length; i++) {
						String org_scope_on = org_scopeArray[i];
						org_scope.append("," + org_scope_on);
						org_scope_encrypt.add(AdminCode.getCodeName("UN", org_scope_on).length()==0?PubFunc.encrypt("UM"+org_scope_on):PubFunc.encrypt("UN"+org_scope_on));
						org_scope_fullname.append("," + (AdminCode.getCodeName("UN", org_scope_on).length()==0?AdminCode.getCodeName("UM", org_scope_on):AdminCode.getCodeName("UN", org_scope_on)));
					}
					String b0100 = rs.getString("b0110");
					if(StringUtils.isNotBlank(b0100))
						b0100 = b0100 + "`" + (AdminCode.getCodeName("UN", b0100).length()==0?AdminCode.getCodeName("UM", b0100):AdminCode.getCodeName("UN", b0100));
					String reviewer_id = rs.getString("reviewer_id");
					map.put("encrypt_scheme_id", scheme_id);
					map.put("name", KqDataUtil.nullif(rs.getString("name")));//名称
					map.put("clerk_username", KqDataUtil.nullif(rs.getString("clerk_username")));//考勤员用户名
					map.put("clerk_fullname", KqDataUtil.nullif(rs.getString("clerk_fullname")));//考勤员全称
					map.put("clerk_imgPath", getZiZhuImg(rs.getString("clerk_username")));//考勤员如果关联自主用户展示图片
					map.put("reviewer_id", StringUtils.isNotBlank(reviewer_id)?PubFunc.encrypt(reviewer_id):"");//审核人ID
					map.put("reviewer_fullname", KqDataUtil.nullif(rs.getString("reviewer_fullname")));//审核人全称
					map.put("reviewer_imgPath", getReviewImgPath(reviewer_id));//审核人图片src
					map.put("b0110", b0100);//所属单位
					map.put("remark", KqDataUtil.nullif(rs.getString("remark")));//备注
					map.put("org_scope_fullname", org_scope_fullname.substring(1));//应用范围
					map.put("org_scope", org_scope.substring(1));//应用范围
					map.put("org_scope_enc", org_scope_encrypt);//应用范围
					map.put("cond", SafeCode.encode(rs.getString("cond")));//条件项
					map.put("item_ids_All", mapItemClass.get("itemIds"));//考勤项目ID
					map.put("item_id_list", mapItemClass.get("itemId_list"));//考勤项目ID顺序
					map.put("item_ids", StringUtils.isNotBlank(rs.getString("item_ids"))?rs.getString("item_ids"):"");//考勤项目ID选中
					map.put("class_ids_All", mapItemClass.get("classIds"));//班次ID
					map.put("class_id_list", mapItemClass.get("classId_list"));//考勤班次id顺序
					map.put("class_ids", StringUtils.isNotBlank(rs.getString("class_ids"))?rs.getString("class_ids"):"");//班次ID选中
					map.put("is_validate", rs.getInt("is_validate"));//是否生效
					map.put("confirm_flag", rs.getInt("confirm_flag"));//员工确认考勤结果 1:生效
					map.put("secondary_admin", rs.getInt("secondary_admin"));//考勤数据提交上级单位审批 1:生效
					map.put("day_detail_enabled", rs.getInt("day_detail_enabled"));//显示日明细数据 默认值为1（显示） ；0||null 不显示

					String dbnames = rs.getString("cbase");
					map.put("dbname", this.getDbName("," + dbnames +","));//人员库

					HashMap mapList = this.getFillingAgencys(PubFunc.decrypt(scheme_id));
					HashMap<String, String> map1 = new HashMap<String, String>();
					ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) mapList.get("list");
					if(list.size() == 0) {
						map1.put("org_id", "-1");
						map1.put("org_name", "");
						map1.put("clerk_username", "");
						map1.put("clerk_imgPath", "/images/photo.jpg");
						map1.put("reviewer_imgPath", "");
						map1.put("reviewer_id", "");
						map1.put("reviewer_fullname", "");
						map1.put("reviewer_imgPath", "/images/photo.jpg");
						list.add(map1);
					}
					map.put("filling_agencys", list);//数据上报机构
					map.put("filling_agencys_select_org", mapList.get("select_org"));//数据上报机构
					map.put("map_oldOrg", mapList.get("map_oldOrg"));//记录下数据上报的机构，这样在最后确定的时候可以直接修改相关的待办和流程kq_extend_log
				}
			}else {
				HashMap mapItemClass = getClassItemData("");//获取考勤班次和考勤项目的数据
				ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> map1 = new HashMap<String, String>();
				map1.put("org_id", "-1");
				map1.put("org_name", "");
				map1.put("clerk_username", "");
				map1.put("clerk_fullname", "");
				map1.put("clerk_imgPath", "/images/photo.jpg");
				map1.put("reviewer_id", "");
				map1.put("reviewer_fullname", "");
				map1.put("reviewer_imgPath", "/images/photo.jpg");
				list.add(map1);

				map.put("encrypt_scheme_id", "");
				map.put("name", "");//名称
				map.put("clerk_username", "");//考勤员用户名
				map.put("clerk_fullname", "");//考勤员全称
				map.put("clerk_imgPath", "/images/photo.jpg");
				map.put("reviewer_id", "");//审核人ID
				map.put("reviewer_fullname", "");//审核人全称
				map.put("reviewer_imgPath", "/images/photo.jpg");//审核人图片src
				map.put("day_detail_enabled", 1);//显示日明细数据 默认值为1（显示） ；0||null 不显示
				String userB0100 = this.userview.getUnitIdByBusi("11");//业务范围
				if(StringUtils.isNotBlank(userB0100)) {
					if("UN`".equalsIgnoreCase(userB0100)) {
						userB0100 = PubFunc.getTopOrgDept(userB0100);
					}
					String userB0100Id = StringUtils.isNotBlank(userB0100.split("`")[0])?userB0100.split("`")[0].substring(2):userB0100.split("`")[1].substring(2);
					userB0100 = userB0100Id + "`" + (AdminCode.getCodeName("UN", userB0100Id).length()==0?AdminCode.getCodeName("UM", userB0100Id):AdminCode.getCodeName("UN", userB0100Id));
				}
				
				map.put("b0110", userB0100);//所属单位
				map.put("remark", "");//备注
				map.put("org_scope_fullname", "");//应用范围
				map.put("org_scope", "");//应用范围
				map.put("cond", "");//条件项
				map.put("item_ids_All", mapItemClass.get("itemIds"));//考勤项目ID
				map.put("item_id_list", mapItemClass.get("itemId_list"));//考勤项目ID顺序
				map.put("item_ids", "");//考勤项目ID选中
				map.put("class_ids_All", mapItemClass.get("classIds"));//班次ID
				map.put("class_id_list", mapItemClass.get("classId_list"));//考勤班次id顺序
				map.put("class_ids", "");//班次ID选中
				map.put("is_validate", "");//是否生效
				map.put("filling_agencys", list);//数据上报机构
				map.put("filling_agencys_select_org", "");//数据上报机构选中的
				map.put("dbname", this.getDbName(""));//人员库
				map.put("confirm_flag", 1);//员工确认考勤结果 1:生效 默认勾选
				map.put("secondary_admin", 1);//考勤数据提交上级单位审批 1:生效 默认勾选
			}
			//选择应用范围，需要根据人员的权限进行显示，选人控件无法知道考勤的业务范围，这里先把值传过去
			StringBuffer unitcodes = new StringBuffer();
			String unitcode = this.userview.getUnitIdByBusi("11");
			if(StringUtils.isNotBlank(unitcode) && !"UN`".equalsIgnoreCase(unitcode)) {
				String[] temps=unitcode.split("`");
				for(int i=0;i<temps.length;i++) {
					if(temps[i].trim().length()>0) {
						unitcodes.append(","+temps[i].substring(2));
					}
				}
			}
			map.put("org_unit", StringUtils.isNotBlank(unitcodes.toString())?unitcodes.substring(1):"");
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return map;
	}

	/**
	 * 获取考勤班次和考勤项目的数据
	 * class_id:如果班次被选中，但是班次被禁止了，这时候这个班次的也要查出来
	 * @return
	 * @throws GeneralException 
	 */
	private HashMap getClassItemData(String class_id) throws GeneralException {
		HashMap map = new HashMap();
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		LinkedHashMap<Integer,String> classIds = new LinkedHashMap<Integer,String>();//班次对应的详细信息
		LinkedHashMap itemIds = new LinkedHashMap();
		ArrayList listSqlParam = new ArrayList();
		RowSet rs = null;
		try {
			// 50016 考勤权限
	    	String whereInOrg = " 1=1 ";
	    	try {
				whereInOrg = KqPrivForHospitalUtil.getPrivB0110Whr(this.userview, "org_id", KqPrivForHospitalUtil.LEVEL_GLOBAL_PARENT_SELF);
			}catch (Exception e) {
				e.printStackTrace();
			}
			String ids = StringUtils.isNotBlank(class_id)?" or class_id in (" + class_id + ") ":"";
			rs = dao.search("select class_id,name,abbreviation,onduty_1,offduty_1,onduty_2,offduty_2,onduty_3,offduty_3 from kq_class where "
					+ "is_validate = 1 " + " and (" + whereInOrg + ")" + ids + " order by seq asc",listSqlParam);
			while(rs.next()) {
				StringBuffer itemSf = new StringBuffer();
				if(StringUtils.isNotBlank(rs.getString("onduty_1"))) {
					itemSf.append("," + rs.getString("onduty_1") + "~" + (rs.getString("offduty_1") == null?"":rs.getString("offduty_1")));
				}
				if(StringUtils.isNotBlank(rs.getString("onduty_2"))) {
					itemSf.append("," + rs.getString("onduty_2") + "~" + (rs.getString("offduty_2") == null?"":rs.getString("offduty_2")));
				}
				if(StringUtils.isNotBlank(rs.getString("onduty_3"))) {
					itemSf.append("," + rs.getString("onduty_3") + "~" + (rs.getString("offduty_3") == null?"":rs.getString("offduty_3")));
				}
				String abbreviation = rs.getString("abbreviation");
				classIds.put(rs.getInt("class_id"), (StringUtils.isBlank(abbreviation)?rs.getString("name"):abbreviation) + itemSf.toString());
				list.add(rs.getInt("class_id"));
			}
			map.put("classId_list", list);
			map.put("classIds", classIds);
			list = new ArrayList();
			//应出勤和实出勤不需要查出来
			rs = dao.search("select item_id,item_name,fielditemid from kq_item order by displayorder,item_id");
			while(rs.next()) {
				if(!"q3533".equalsIgnoreCase(rs.getString("fielditemid")) && !"q3535".equalsIgnoreCase(rs.getString("fielditemid"))) {
					itemIds.put(rs.getString("item_id"), rs.getString("item_name"));
					list.add(rs.getString("item_id"));
				}
			}
			map.put("itemId_list", list);
			map.put("itemIds", itemIds);
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return map;
	}
	/**
	 * 获取人员库信息
	 * @param dbnames 选中的人员库
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList<HashMap<String,String>> getDbName(String dbnames){
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs=null;
		ArrayList<HashMap<String,String>> listDbname = new ArrayList<HashMap<String,String>>();
		try
		{
			//取认证应用库
			ArrayList<String> kq_dbase_list = KqPrivForHospitalUtil.getB0110Dase(this.userview, conn);
            
            if(kq_dbase_list.size() > 0) {
				rs=dao.search("select dbid,dbname,pre from dbname order by dbid");
				while(rs.next())
				{
					String pre = rs.getString("pre");
					if(!kq_dbase_list.contains(pre))
						continue;
					HashMap<String, String> mapDbName = new HashMap<String, String>();
					mapDbName.put("id", pre);
					mapDbName.put("name", rs.getString("dbname"));
					if(dbnames.toLowerCase().indexOf("," + pre.toLowerCase() + ",") != -1) {//选中的
						mapDbName.put("checked", "true");
					}else {
						mapDbName.put("checked", "false");
					}
					listDbname.add(mapDbName);
				}
            }
		}catch(Exception ex) {
			ex.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return listDbname;
	}

	/**
	 * 获取数据上报机构
	 * @param dbnames 选中的人员库
	 * @return
	 * @throws GeneralException
	 */
	private HashMap getFillingAgencys(String scheme_id) {
		HashMap mapF = new HashMap();
		ArrayList<HashMap<String,String>> listFillingAgencys = new ArrayList();
		HashMap map_oldOrg = new HashMap();
		try {
			KqCtrlParamUtil kqCtrlParam = new KqCtrlParamUtil(this.conn,this.userview, scheme_id);
			List elementList = kqCtrlParam.getValue(KqCtrlParamUtil.FILLING_AGENCY, KqCtrlParamUtil.FILLING_AGENCYS);
			ArrayList select_org = new ArrayList();
			for (int i = 0; i < elementList.size(); i++) {
				Element element = (Element) elementList.get(i);
				String reviewId = element.getAttributeValue("reviewer_id");//审核人id
				HashMap<String,String> map = new HashMap<String,String>();
				HashMap map_ = new HashMap();
				String org_id = element.getAttributeValue("org_id");
				map.put("org_id", org_id.substring(0, 2) + PubFunc.encrypt(org_id.substring(2)));
				select_org.add(PubFunc.encrypt(org_id));
				map.put("org_name", AdminCode.getCodeName("UN", org_id.substring(2)).length()==0?AdminCode.getCodeName("UM", org_id.substring(2)):AdminCode.getCodeName("UN", org_id.substring(2)));
				map.put("clerk_username", element.getAttributeValue("clerk_username"));
				map.put("clerk_fullname", element.getAttributeValue("clerk_fullname"));
				map.put("clerk_imgPath", getZiZhuImg(element.getAttributeValue("clerk_username")));
				map.put("reviewer_id", StringUtils.isBlank(reviewId)?"" : PubFunc.encrypt(reviewId));
				map.put("reviewer_fullname", StringUtils.isBlank(element.getAttributeValue("reviewer_fullname")) ? "" : element.getAttributeValue("reviewer_fullname"));
				map.put("reviewer_imgPath", getReviewImgPath(reviewId));
				listFillingAgencys.add(map);
				map_.put("clerk_name_old", element.getAttributeValue("clerk_username"));
				map_.put("review_name_old", StringUtils.isBlank(reviewId)?"" : PubFunc.encrypt(reviewId));
				map_.put("org_id_detail", PubFunc.encrypt(org_id.substring(2)));
				map_oldOrg.put(org_id.substring(0, 2) + PubFunc.encrypt(org_id.substring(2)), map_);
			}
			mapF.put("list", listFillingAgencys);
			mapF.put("select_org", select_org);
			mapF.put("map_oldOrg", map_oldOrg);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return mapF;
	}

	/**
	 * 获取图片路径
	 * @param reviewId
	 * @return
	 */
	private String getReviewImgPath(String reviewId) {
		String imgpath = "";
		try{
			if(StringUtils.isNotBlank(reviewId)) {
				String imgDbName =reviewId.substring(0, 3);//人员库前缀
				String imgA0100 = reviewId.substring(3, reviewId.length());
				PhotoImgBo photoImgBo = new PhotoImgBo(this.conn);
				imgpath = photoImgBo.getPhotoPathLowQuality(imgDbName, imgA0100);
			}else {
				imgpath = "/images/photo.jpg";
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return imgpath;
	}

	/**
	 * 保存属性
	 * @throws GeneralException
	 */
	@Override
    public int saveData(JSONObject jsonObject) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		int count = 0;
		KqDataUtil kqDataUtil = new KqDataUtil(this.userview);
		try {
			//数据上报
			String ctrlXml = getCtrl(jsonObject, dao);
			
			//审核人ID
			String reviewer_id = jsonObject.get("reviewer_id") == null?"":PubFunc.decrypt(jsonObject.getString("reviewer_id"));
			//条件项
			String cond = "";
			if(jsonObject.get("cond") != null) {
				cond = SafeCode.decode(jsonObject.getString("cond"));
				cond = cond.replaceAll("!", "\r");
				cond = cond.replaceAll("`", "\n");
				cond = PubFunc.keyWord_reback(cond);
			}

			//应用范围,这里不需要判断是否为空，因为是必填，这里判断多余，前台判断就行
			StringBuffer org_scope = new StringBuffer();
			String[] org_scopeArray = jsonObject.getString("org_scope").split(",");
			for(int i = 0; i < org_scopeArray.length; i++) {
				org_scope.append("," + org_scopeArray[i]);
			}

			RecordVo vo=new RecordVo("kq_scheme");
			String name = jsonObject.getString("name");
			vo.setString("name", name);//名称(必填)
			vo.setString("clerk_username", jsonObject.getString("clerk_username"));//考勤员用户名(必填)
			vo.setString("clerk_fullname", jsonObject.getString("clerk_fullname"));//考勤员全称(必填)
			vo.setString("reviewer_id", reviewer_id);//审核人ID
			vo.setString("reviewer_fullname", jsonObject.get("reviewer_fullname")==null?"":jsonObject.getString("reviewer_fullname"));//审核人全称
			vo.setString("b0110", jsonObject.get("b0110")==null?"":jsonObject.getString("b0110").split("`")[0]);//所属单位
			vo.setString("remark", jsonObject.get("remark")==null?"":jsonObject.getString("remark"));//备注
			vo.setString("cbase", (String)jsonObject.get("cbase"));//应用库标识
			vo.setString("org_scope", org_scope.substring(1));//应用范围(必填)
			vo.setString("cond", cond);//条件项
			vo.setString("item_ids", jsonObject.get("item_ids")==null?"":jsonObject.getString("item_ids"));//考勤项目ID
			vo.setString("class_ids", jsonObject.get("class_ids")==null?"":jsonObject.getString("class_ids"));//班次ID
			vo.setString("ctrl_param", ctrlXml);//数据上报机构 （xml格式）
			vo.setInt("confirm_flag", jsonObject.get("confirm_flag")==null?1:jsonObject.getInt("confirm_flag"));//员工确认考勤结果 1:生效
			vo.setInt("secondary_admin", jsonObject.get("secondary_admin")==null?1:jsonObject.getInt("secondary_admin"));//考勤数据提交上级单位审批 1:生效
            vo.setInt("day_detail_enabled",jsonObject.get("dayDetailEnabled")==null?0:jsonObject.getInt("dayDetailEnabled"));
			
			if(jsonObject.get("scheme_id") == null) {
				IDFactoryBean idf = new IDFactoryBean();
				String id = idf.getId("kq_scheme.scheme_id", "", conn);
				vo.setInt("scheme_id", Integer.parseInt(id));
				vo.setInt("is_validate", 1);//是否生效1生效
				vo.setDate("create_time", new Date());//创建时间
				vo.setString("create_user", this.userview.getUserName());//创建者用户名
				vo.setString("create_fullname", this.userview.getUserFullName());//创建者姓名
				count = dao.addValueObject(vo);
			}else {
				int scheme_id = Integer.parseInt(PubFunc.decrypt(jsonObject.getString("scheme_id")));
				vo.setInt("scheme_id", scheme_id);
				count = dao.updateValueObject(vo);
				if(count > 0) {
					String old_name_ = (String) jsonObject.get("old_name");
					if(!old_name_.equals(name)) {
						kqDataUtil.updateKqPengdingName(String.valueOf(scheme_id), this.conn, old_name_, name);
					}
					
					String map_old = jsonObject.get("map_oldOrg") == null?"":jsonObject.getString("map_oldOrg");
					JSONObject  myJson = JSONObject.fromObject(map_old);
					Map map_oldOrg = myJson; 
					
					Iterator iter = map_oldOrg.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						Object key = entry.getKey();
						Map val = (Map)entry.getValue();
						Iterator iter_ = val.entrySet().iterator();
						String old_name_clerk = val.get("clerk_name_old")==null?"":(String)val.get("clerk_name_old");
						String old_name_review = val.get("review_name_old")==null?"":(String)val.get("review_name_old");
						String new_name_clerk = val.get("clerk_name_new")==null?"":(String)val.get("clerk_name_new");
						String new_name_review = val.get("review_name_new")==null?"":(String)val.get("review_name_new");
						String org_id_detail = val.get("org_id_detail")==null?"":(String)val.get("org_id_detail");
						// 45448 org_id_detail参数未解密 导致更换审核人失败
						if(StringUtils.isNotBlank(new_name_clerk) && StringUtils.isNotBlank(old_name_clerk) && !new_name_clerk.equalsIgnoreCase(old_name_clerk))
							this.updatePendingTaskAndCurrentUser(new_name_clerk, old_name_clerk, String.valueOf(scheme_id),PubFunc.decrypt(org_id_detail));
						if(StringUtils.isNotBlank(new_name_review) && StringUtils.isNotBlank(old_name_review) && !new_name_review.equalsIgnoreCase(old_name_review))
							this.updatePendingTaskAndCurrentUser(PubFunc.decrypt(new_name_review), PubFunc.decrypt(old_name_review), String.valueOf(scheme_id),PubFunc.decrypt(org_id_detail));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return count;
	}

	/**
	 * 组成xml
	 * @param jsonObject
	 * @param kqCtrlParam
	 * @return
	 */
	private String getCtrl(JSONObject jsonObject, ContentDAO dao) {
		StringBuffer buf=new StringBuffer();
		try {
			String scheme_id = jsonObject.get("scheme_id")==null?"":PubFunc.decrypt(jsonObject.getString("scheme_id"));
			KqCtrlParamUtil kqCtrlParam = new KqCtrlParamUtil(this.conn,this.userview, scheme_id);
			ArrayList<String> list = new ArrayList<String>();
			JSONArray filling_agencys = jsonObject.getJSONArray("filling_agencys");
			ArrayList<HashMap> orglist = kqCtrlParam.getFillingAgencysMap("", "");

			kqCtrlParam.removeNode(KqCtrlParamUtil.FILLING_AGENCYS);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
			String dateNow=df.format(new Date());// new Date()为获取当前系统时间
			String newOrgids = ",";
			for(int i = 0; i < filling_agencys.size(); i++) {
				JSONObject beanJson = (JSONObject) filling_agencys.get(i);
				Map bean = beanJson;
				String org_id_encry = bean.get("org_id")==null?"":(String)bean.get("org_id");
				if(StringUtils.isNotBlank(org_id_encry)) {
					org_id_encry = org_id_encry.substring(0,2) + PubFunc.decrypt(org_id_encry.substring(2));
				}
				kqCtrlParam.setValue(KqCtrlParamUtil.FILLING_AGENCY, KqCtrlParamUtil.FILLING_AGENCYS, "org_id", org_id_encry,-1);
				kqCtrlParam.setValue(KqCtrlParamUtil.FILLING_AGENCY, KqCtrlParamUtil.FILLING_AGENCYS, "clerk_username", bean.get("clerk_username")==null?"":(String)bean.get("clerk_username"),i);
				kqCtrlParam.setValue(KqCtrlParamUtil.FILLING_AGENCY, KqCtrlParamUtil.FILLING_AGENCYS, "clerk_fullname", bean.get("clerk_fullname")==null?"":(String)bean.get("clerk_fullname"),i);
				kqCtrlParam.setValue(KqCtrlParamUtil.FILLING_AGENCY, KqCtrlParamUtil.FILLING_AGENCYS, "reviewer_id", bean.get("reviewer_id")==null?"":PubFunc.decrypt((String)bean.get("reviewer_id")),i);
				kqCtrlParam.setValue(KqCtrlParamUtil.FILLING_AGENCY, KqCtrlParamUtil.FILLING_AGENCYS, "reviewer_fullname", bean.get("reviewer_fullname")==null?"":(String)bean.get("reviewer_fullname"),i);
				newOrgids += org_id_encry + ",";
				for (HashMap map : orglist) {
					if(org_id_encry.equalsIgnoreCase((String) map.get("y_org_id"))){
						dateNow=(String) map.get("create_time");
						break;
					}
				}

				kqCtrlParam.setValue(KqCtrlParamUtil.FILLING_AGENCY, KqCtrlParamUtil.FILLING_AGENCYS, "create_time", dateNow,i);
			}
			// 45588 linbz 删除机构时清空该机构的填报记录
			deleteSchemeOrgid(dao, scheme_id, newOrgids, orglist);
			
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(kqCtrlParam.getDoc()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buf.toString();
	}
	/**
	 * 对于有父节点时，其子节点不用保存
	 * @param org_ids
	 * @param org_name
	 * @param org_id_appear 数据上报中的机构，为了解决上报的时候人员和应用范围不匹配问题bug 【42968】
	 * @return
	 * @throws GeneralException
	 */
	@Override
    public HashMap getSortRangeMap(JSONArray org_ids, JSONArray org_name, JSONArray org_id_appear) throws GeneralException {
		StringBuffer org_name_finally = new StringBuffer();
		List org_scope_enc = new ArrayList();
		StringBuffer org_ids_notEncrypt_finally = new StringBuffer();
		HashMap map = new HashMap();
		try {
			// 53578 校验是否为空
			if(null==org_ids || org_ids.size()==0) {
				map.put("needDeleteIndex", "");
				map.put("org_name", "");
				map.put("org_scope_enc", "");
				map.put("org_id", "");
				return map;
			}
			for(int i = 0; i < org_ids.size(); i++) {
				boolean flag = true;
				String org_id = PubFunc.decrypt(org_ids.getString(i).split(",")[1]);
				for(int j = 0; j < org_ids.size(); j++) {
					String org_id_o = PubFunc.decrypt(org_ids.getString(j).split(",")[1]);
					if(!org_id_o.equalsIgnoreCase(org_id) && org_id.startsWith(org_id_o)) {//如果第一个已第二个开头，则说明第一个是第二个的子节点，第一个没必要加了
						flag = false;
						break;
					}
				}
				if(flag) {
					org_scope_enc.add(PubFunc.encrypt(org_ids.getString(i).split(",")[0] + org_id));
					org_name_finally.append("," + org_name.getString(i));
					org_ids_notEncrypt_finally.append("," + org_id);
				}
			}
			//找出换了应用范围之后的不在该范围内的所有需要删除的机构
			ArrayList<Integer> needDeleteIndex = new ArrayList<Integer>();
			String org_ids_finally = org_ids_notEncrypt_finally.substring(1);
			String[] org_ids_finally_arr = org_ids_notEncrypt_finally.substring(1).split(",");
			//循环数据上报的机构，然后再判断是否在应用范围选择的机构范围内
			for(int i = 0; i < org_id_appear.size(); i++) {
				String org_id = PubFunc.decrypt(((String) org_id_appear.get(i)).substring(2));
				boolean flag = false;
				for(int j = 0; j < org_ids_finally_arr.length; j++) {
					String org_ids_finally_ = org_ids_finally_arr[j];
					if(org_id.length() >= org_ids_finally_.length()) {
						if(org_id.substring(0, org_ids_finally_.length()).equalsIgnoreCase(org_ids_finally_)) {
							break;
						}else if(j == (org_ids_finally_arr.length-1)) {
							needDeleteIndex.add(i);
						}
					}
				}
			}
			//记下需要删除的rowIndex
			map.put("needDeleteIndex", needDeleteIndex);
			map.put("org_name", org_name_finally.substring(1));
			map.put("org_scope_enc", org_scope_enc);//选人控件，需要加密的才能回显default..
			map.put("org_id", org_ids_finally);//选人控件，需要传不加密的才能显示org_id
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}

	/**
	 * 列表界面是否启用
	 * @param jsonObject
	 */
	@Override
    public int changeState(JSONObject jsonObject) {
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		int count = 0;
		try {
			String scheme_id = jsonObject.getString("scheme_id");
			int state = jsonObject.getInt("state");
			list.add(state);
			list.add(Integer.parseInt(PubFunc.decrypt(scheme_id)));
			count = dao.update("update kq_scheme set is_validate = ? where scheme_id = ?",list);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 删除
	 * @param jsonObject
	 */
	@Override
    public int deleteScheme(JSONObject jsonObject) {
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		int count = 0;
		try {
			KqDataUtil kqDataUtil = new KqDataUtil(this.userview);
			StringBuffer sf = new StringBuffer();
			String[] scheme_ids = jsonObject.getString("ids").split(",");
			for(int i = 0; i < scheme_ids.length; i++) {
				String scheme_id = PubFunc.decrypt(scheme_ids[i]);
				sf.append(" or scheme_id=?");
				list.add(Integer.parseInt(scheme_id));
				//删除对应的考勤代办
				kqDataUtil.cleanKqPengdingTaskBySchemeId(conn, scheme_id, "", "", "");
			}
			count = dao.update("delete from  kq_scheme where " + sf.substring(3),list);
			
			dao.update("delete from  kq_extend_log where " + sf.substring(3),list);
			
			dao.update("delete from  q35 where " + sf.substring(3),list);
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	@Override
    public ArrayList<LazyDynaBean> listKq_scheme(String sqlWhere, ArrayList parameterList, String sqlSort) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList<LazyDynaBean> dataList = new ArrayList<LazyDynaBean>();
		RowSet rs = null;
		StringBuffer strSql = new StringBuffer();
		try {
			strSql.append("SELECT * FROM KQ_SCHEME where 1=1 ");
			if (StringUtils.isNotBlank(sqlWhere)) {
				strSql.append(sqlWhere);
			}
			if (StringUtils.isNotBlank(sqlSort)) {
				strSql.append(" ORDER BY ").append(sqlSort);
			} else {
				strSql.append(" ORDER BY scheme_id ");
			}
			ArrayList pList = new ArrayList();
			if (parameterList != null) {
				pList.addAll(parameterList);
			}
			ArrayList<String> userNames = new ArrayList<String>();
			rs = dao.search(strSql.toString(), pList);
			while (rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("name", KqDataUtil.nullif(rs.getString("name")));
				String clerk_username = KqDataUtil.nullif(rs.getString("clerk_username"));
				bean.set("clerk_username", clerk_username);
				userNames.add(clerk_username);
				bean.set("clerk_fullname", KqDataUtil.nullif(rs.getString("clerk_fullname")));
				bean.set("reviewer_id",KqDataUtil.nullif( rs.getString("reviewer_id")));
				bean.set("reviewer_fullname", KqDataUtil.nullif(rs.getString("reviewer_fullname")));
				bean.set("b0110", KqDataUtil.nullif(rs.getString("b0110")));
				bean.set("cbase",KqDataUtil.nullif( rs.getString("cbase")));
				bean.set("org_scope",KqDataUtil.nullif( rs.getString("org_scope")));
				bean.set("item_ids", KqDataUtil.nullif(rs.getString("item_ids")));
				bean.set("class_ids", KqDataUtil.nullif(rs.getString("class_ids")));
				bean.set("is_validate",KqDataUtil.nullif( rs.getString("is_validate")));
				bean.set("create_fullname", KqDataUtil.nullif(rs.getString("create_fullname")));
				bean.set("ctrl_param", KqDataUtil.nullif(rs.getString("ctrl_param")));
				bean.set("create_user", KqDataUtil.nullif(rs.getString("create_user")));
				bean.set("remark",KqDataUtil.nullif( rs.getString("remark")));
				bean.set("cond", KqDataUtil.nullif(rs.getString("cond")));
				bean.set("scheme_id",KqDataUtil.nullif( rs.getString("scheme_id")));
				bean.set("secondary_admin",KqDataUtil.nullif(rs.getString("secondary_admin")));
				bean.set("confirm_flag",KqDataUtil.nullif(rs.getString("confirm_flag")));
				bean.set("day_detail_enabled",rs.getInt("day_detail_enabled"));
				KqCtrlParamUtil kqCtrlParamUtil=new KqCtrlParamUtil(this.conn,this.userview,rs.getString("scheme_id"),rs.getString("ctrl_param"));
				ArrayList<HashMap> listFillingAgencys = kqCtrlParamUtil.getFillingAgencysMap("","");
				bean.set("org_map",listFillingAgencys);
				for(int j=0;j<listFillingAgencys.size();j++) {
					HashMap orgmap = (HashMap)listFillingAgencys.get(j);
					userNames.add((String)orgmap.get("y_clerk_username"));
				}
				dataList.add(bean);
			}
			// 54643 校验方案详细信息中考勤员姓名
			checkListKq_scheme(rs, dao, userNames, dataList);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return dataList;
	}

	/**
	 * 根据各种条件查询kq_scheme表中的内容 根据年月获取有效机构
	 * @param sqlWhere sql条件
	 * @param parameterList 参数
	 * @param sqlSort 排序字段：无需order by,直接写字段即可
	 * @param kq_year 考勤年份
	 * @param kq_duration 考勤月份
	 * @return ArrayList<LazyDynaBean>
	 * @throws GeneralException
	 */
	@Override
    public ArrayList<LazyDynaBean> listKq_scheme(String sqlWhere, ArrayList parameterList, String sqlSort, String kq_year, String kq_duration) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList<LazyDynaBean> dataList = new ArrayList<LazyDynaBean>();
		RowSet rs = null;
		StringBuffer strSql = new StringBuffer();
		try {
			strSql.append("SELECT * FROM KQ_SCHEME where 1=1 ");
			if (StringUtils.isNotBlank(sqlWhere)) {
				strSql.append(sqlWhere);
			}
			if (StringUtils.isNotBlank(sqlSort)) {
				strSql.append(" ORDER BY ").append(sqlSort);
			} else {
				strSql.append(" ORDER BY scheme_id ");
			}
			ArrayList pList = new ArrayList();
			if (parameterList != null) {
				pList.addAll(parameterList);
			}
			ArrayList<String> userNames = new ArrayList<String>();
			rs = dao.search(strSql.toString(), pList);
			while (rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("name", KqDataUtil.nullif(rs.getString("name")));
				String clerk_username = KqDataUtil.nullif(rs.getString("clerk_username"));
				bean.set("clerk_username", clerk_username);
				userNames.add(clerk_username);
				bean.set("clerk_fullname", KqDataUtil.nullif(rs.getString("clerk_fullname")));
				bean.set("reviewer_id",KqDataUtil.nullif( rs.getString("reviewer_id")));
				bean.set("reviewer_fullname", KqDataUtil.nullif(rs.getString("reviewer_fullname")));
				bean.set("b0110", KqDataUtil.nullif(rs.getString("b0110")));
				bean.set("cbase",KqDataUtil.nullif( rs.getString("cbase")));
				bean.set("org_scope",KqDataUtil.nullif( rs.getString("org_scope")));
				bean.set("item_ids", KqDataUtil.nullif(rs.getString("item_ids")));
				bean.set("class_ids", KqDataUtil.nullif(rs.getString("class_ids")));
				bean.set("is_validate",KqDataUtil.nullif( rs.getString("is_validate")));
				bean.set("create_fullname", KqDataUtil.nullif(rs.getString("create_fullname")));
				bean.set("ctrl_param", KqDataUtil.nullif(rs.getString("ctrl_param")));
				bean.set("create_user", KqDataUtil.nullif(rs.getString("create_user")));
				bean.set("remark",KqDataUtil.nullif( rs.getString("remark")));
				bean.set("cond", KqDataUtil.nullif(rs.getString("cond")));
				bean.set("scheme_id",KqDataUtil.nullif( rs.getString("scheme_id")));
				bean.set("secondary_admin",KqDataUtil.nullif( rs.getString("secondary_admin")));
				bean.set("confirm_flag",KqDataUtil.nullif( rs.getString("confirm_flag")));
				bean.set("day_detail_enabled",rs.getInt("day_detail_enabled"));
				KqCtrlParamUtil kqCtrlParamUtil=new KqCtrlParamUtil(this.conn,this.userview,rs.getString("scheme_id"),rs.getString("ctrl_param"));
				ArrayList<HashMap> listFillingAgencys = kqCtrlParamUtil.getFillingAgencysMap(kq_year,kq_duration);
				bean.set("org_map",listFillingAgencys);
				for(int j=0;j<listFillingAgencys.size();j++) {
					HashMap orgmap = (HashMap)listFillingAgencys.get(j);
					userNames.add((String)orgmap.get("y_clerk_username"));
				}
				dataList.add(bean);
			}
			// 54643 校验方案详细信息中考勤员姓名
			checkListKq_scheme(rs, dao, userNames, dataList);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return dataList;
	}

	/**
	 * 获取业务用户的图片
	 * @param busiName
	 * @return
	 * @throws GeneralException 
	 */
	@Override
    public String getZiZhuImg(String busiName){
		RowSet rs = null;
		String imgpath = "";
		String imgA0100 = "";
		String imgDbName = "";
		ContentDAO dao = new ContentDAO(this.conn);		
		try {
			rs = dao.search("select a0100,nbase from operuser where username = ?",Arrays.asList(new String[] {busiName}));
			if(rs.next()) {
				imgDbName = rs.getString("nbase");
				imgA0100 = rs.getString("a0100");
			}
			if(StringUtils.isNotBlank(imgDbName) && StringUtils.isNotBlank(imgA0100)) {
				PhotoImgBo photoImgBo = new PhotoImgBo(this.conn);
				imgpath = photoImgBo.getPhotoPathLowQuality(imgDbName, imgA0100);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return StringUtils.isNotBlank(imgpath)?imgpath:"/images/photo.jpg";
	}
	
	/**
	 * flag: true: 审核员，false:考勤员
	 * @return
	 * @throws GeneralException 
	 */
	@Override
    public int changeClerkOrReviewFromList(String username, String fullname, boolean flag, String org_id, String scheme_id, String old_name) throws GeneralException {
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);	
		ArrayList listParam = new ArrayList();
		int num = 0;
		StringBuffer sql = new StringBuffer("update kq_scheme ");
		try {
			if(StringUtils.isBlank(org_id)) {//更新人事处的审核人或者考勤员
				if(flag) {//修改审核员
					sql.append(" set reviewer_id=?,reviewer_fullname=? ");
					listParam.add(PubFunc.decrypt(username));
					listParam.add(fullname);
				}else {
					sql.append(" set clerk_username=?,clerk_fullname=? ");
					listParam.add(username);
					listParam.add(fullname);
				}
			}else {
				KqCtrlParamUtil kqCtrlParamUtil=new KqCtrlParamUtil(this.conn,this.userview,PubFunc.decrypt(scheme_id));
				List elementList = kqCtrlParamUtil.getValue(KqCtrlParamUtil.FILLING_AGENCY, KqCtrlParamUtil.FILLING_AGENCYS);
				for(int i = 0; i < elementList.size(); i++) {
					Element element = (Element) elementList.get(i);
					String org_id_ele = element.getAttributeValue("org_id");
					if(org_id_ele.equalsIgnoreCase(org_id)) {
						
						if(flag) {//修改审核员
							kqCtrlParamUtil.setValue(KqCtrlParamUtil.FILLING_AGENCY, KqCtrlParamUtil.FILLING_AGENCYS, "reviewer_id", PubFunc.decrypt(username),i);
							kqCtrlParamUtil.setValue(KqCtrlParamUtil.FILLING_AGENCY, KqCtrlParamUtil.FILLING_AGENCYS, "reviewer_fullname", fullname,i);
						}else {
							
							kqCtrlParamUtil.setValue(KqCtrlParamUtil.FILLING_AGENCY, KqCtrlParamUtil.FILLING_AGENCYS, "clerk_username", username,i);
							kqCtrlParamUtil.setValue(KqCtrlParamUtil.FILLING_AGENCY, KqCtrlParamUtil.FILLING_AGENCYS, "clerk_fullname", fullname,i);
						}
						break;
					}
				}
				
				sql.append(" set ctrl_param=? ");
				XMLOutputter outputter=new XMLOutputter();
				Format format=Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				listParam.add(outputter.outputString(kqCtrlParamUtil.getDoc()));
			}
			sql.append(" where scheme_id=?");
			listParam.add(PubFunc.decrypt(scheme_id));
			num = dao.update(sql.toString(), listParam);
			//如果以前没有则不需要发待办
			if(StringUtils.isNotBlank(old_name) && !username.equalsIgnoreCase(old_name))
				updatePendingTaskAndCurrentUser(flag?PubFunc.decrypt(username):username, flag?PubFunc.decrypt(old_name):old_name
						, PubFunc.decrypt(scheme_id), StringUtils.isBlank(org_id)?"":org_id.substring(2));
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return num;
	}
	
	/**
	 * 在替换人员的时候修改代办和kq_extend_log表中的curr_user和appuser
	 */
	private void updatePendingTaskAndCurrentUser(String new_username, String old_username, String scheme_id, String org_id) {
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);	
		KqDataUtil kqDataUtil = new KqDataUtil(this.userview);
		ArrayList list = new ArrayList();
		try {
			list.add(old_username);
			list.add(new_username);
			list.add(scheme_id);
			String org_id_ = "";
			String ext_flag = "KQ%_"+scheme_id+"%";
			if(StringUtils.isNotBlank(org_id)) {
				ext_flag = "KQ%_"+scheme_id+"_"+PubFunc.encrypt(org_id)+"%";
				org_id_ = " and org_id=?";
				list.add(org_id);
				// 修改kq_extend_log 
				// 46012 如果不是改下级机构的考勤员或审批人 则不需要修改 ,由于流程已优化 故curr_user不需要更改   
				int count = dao.update("update kq_extend_log set appuser = replace(appuser, ?, ?) where scheme_id = ?"+org_id_,list);
				if(count > 0) {
					rs = dao.search("select kq_year,kq_duration,curr_user from kq_extend_log where scheme_id=? and org_id=?"
							, Arrays.asList(new String[] {scheme_id, org_id}));
					while(rs.next()) {
						String curr_user = rs.getString("curr_user");
						// 兼容老的流程问题
						if(!",1,2,3,4,".contains(","+curr_user+",")) {
							ArrayList listp = new ArrayList();
							listp.addAll(list);
							listp.add(rs.getString("kq_year"));
							listp.add(rs.getString("kq_duration"));
							dao.update("update kq_extend_log set curr_user = replace(curr_user, ?, ?) where scheme_id = ?"+org_id_
									+" and kq_year=? and kq_duration=?", listp);
						}
					}
				}
			}else {
				dao.update("update kq_extend_log set appuser = replace(appuser, ?, ?) where scheme_id = ?",list);
			}
			
			//先查出代办，然后发送
			rs = dao.search("select pending_url,receiver,sender,pending_title from t_hr_pendingtask "
					+ "where (receiver=? or sender=?) and ext_flag like ? and pending_type=30 and pending_status = 0", 
					Arrays.asList(new String[] {old_username, old_username,ext_flag}));
			
			while(rs.next()) {
				
				String title = rs.getString("pending_title");
				String sender = rs.getString("sender");
				if(sender.equalsIgnoreCase(old_username)) {
					sender = new_username;
				}
				String receiver = rs.getString("receiver");
				if(receiver.equalsIgnoreCase(old_username)) {
					receiver = new_username;
				}
				//填报-审批-驳回-确认
				String str = title.substring(title.lastIndexOf("_") + 1, title.length());
				int taskType = 0;
				if(str.indexOf(ResourceFactory.getProperty("kq.data.sp.text.report")) != -1) {
					taskType = KqDataUtil.TASKTYPE_FILL;
				}else if(str.indexOf(ResourceFactory.getProperty("kq.data.sp.text.pindingapproval")) != -1) {
					taskType = KqDataUtil.TASKTYPE_SP;
				}else if(str.indexOf(ResourceFactory.getProperty("kq.data.sp.text.back")) != -1) {
					taskType = KqDataUtil.TASKTYPE_SP_BACK;
				}
				// 51293 校验下发本人确认待办错误
				else if(ResourceFactory.getProperty("kq.archive.scheme.kqconfirmpendingtask").indexOf(str) != -1) {
					taskType = KqDataUtil.TASKTYPE_CONFIRM;
				}
				
				String pending_url = rs.getString("pending_url");
				pending_url = SafeCode.decode(pending_url.substring(pending_url.indexOf("param=") + 6));
				String kqYear = pending_url.substring(pending_url.indexOf("kqYear=")+7, pending_url.length());
				kqYear = kqYear.substring(0, kqYear.indexOf("&"));
				
				String kqDuration = pending_url.substring(pending_url.indexOf("kqDuration=")+11, pending_url.length());
				kqDuration = kqDuration.substring(0, kqDuration.indexOf("&"));
		    	// 51293 兼容 orgId 为 org_id 的情况（由于之前wb确认待办参数写错导致）
		    	String orgId = "";
		    	if(pending_url.indexOf("orgId=") != -1) {
		    		orgId = pending_url.substring(pending_url.indexOf("orgId=")+6, pending_url.length());
		    	}else if(pending_url.indexOf("org_id=") != -1) {
		    		orgId = pending_url.substring(pending_url.indexOf("org_id=")+7, pending_url.length());
		    	}
		    	orgId = PubFunc.decrypt(orgId.substring(0, orgId.indexOf("&")));
		    	
		    	String optRole = "-1";
		    	if(pending_url.indexOf("optRole=") != -1) {
		    		optRole = pending_url.substring(pending_url.indexOf("optRole=")+8, pending_url.length());
		    		optRole = optRole.substring(0, optRole.indexOf("&"));
		    	}
		    	
				ArrayList parameterList = new ArrayList();
			    parameterList.add(scheme_id);
			    ArrayList<LazyDynaBean> schemeList = this.listKq_scheme(" And scheme_id=? ", parameterList, "");
			    //对于人事处的审核员和考勤员，org_id是没有值的，但是在切换的时候，只有url中可以分别，先查出来，判断optRole是否是人事处的考勤员和考勤员，才能重新发送
			    if(StringUtils.isNotBlank(org_id) || (StringUtils.isBlank(org_id) && (Integer.parseInt(optRole)==KqDataUtil.role_Clerk ||
			    		Integer.parseInt(optRole)==KqDataUtil.role_Reviewer))) {
			    	
			    	if(StringUtils.isNotBlank(new_username)) {//执行删除操作
				    	//发送代办
				    	kqDataUtil.kqSendPengdingTask_changeForScheme(this.conn, title, Integer.parseInt(scheme_id), 
				    			kqYear, kqDuration, orgId, taskType, sender, receiver, schemeList.get(0));
			    	}
			    	//删除以前的代办
			    	kqDataUtil.cleanKqPengdingTaskByUserName(scheme_id, this.conn, old_username, org_id);
			    }
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 根据方案id删除某个机构
	 * deleteSchemeOrgid
	 * @param dao
	 * @param scheme_id		方案id
	 * @param newOrgids		新的机构id
	 * @param oldOrglist	方案中老的机构详细信息
	 * @return
	 * @date 2019年3月18日 下午3:39:21
	 * @author linbz
	 */
	public int deleteSchemeOrgid(ContentDAO dao, String scheme_id, String newOrgids, ArrayList<HashMap> oldOrglist) {
		ArrayList list = new ArrayList();
		int count = 0;
		try {
			if(null == oldOrglist || oldOrglist.size() == 0 || StringUtils.isBlank(scheme_id)) 
				return 0;
			KqDataUtil kqDataUtil = new KqDataUtil(this.userview);
			StringBuffer sqlWhere = new StringBuffer("");
			StringBuffer sqllike = new StringBuffer("");
			ArrayList delParam = new ArrayList();
			for(HashMap map : oldOrglist) {
				if(!newOrgids.contains(","+(String) map.get("y_org_id")+",")){
					String orgid = (String) map.get("org_id");
					delParam.add(orgid);
					sqlWhere.append(",?");
					sqllike.append("or org_id like '").append(orgid).append("%' ");
					// 45588 删除对应的考勤代办
					kqDataUtil.cleanKqPengdingTaskBySchemeId(conn, scheme_id, "", "", orgid);
				}
			}
			if(delParam.size() > 0) {
				delParam.add(scheme_id);
				// 删流程记录
				String sql = "delete from kq_extend_log where org_id in("+sqlWhere.toString().substring(1)+") and scheme_id=? ";
				dao.update(sql.toString(), delParam);
				// 删q35
				delParam.clear();
				delParam.add(scheme_id);
				sql = "delete from q35 where ("+sqllike.toString().substring(2)+") and scheme_id=? ";
				dao.update(sql.toString(), delParam);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	/**
	 * 获取业务用户关联自助用户的姓名
	 * @param rs
	 * @param dao
	 * @param userNames
	 * @return
	 */
	private HashMap<String, String> getUserFullMap(RowSet rs, ContentDAO dao, ArrayList<String> userNames) {
		HashMap<String, String> userFullMap = new HashMap<String, String>();
		try {
			StringBuffer clerk_usernameStrs = new StringBuffer("");
			for(int i=0;i<userNames.size();i++) {
				clerk_usernameStrs.append(",'"+userNames.get(i)+"'");
			}
			ArrayList list = new ArrayList();
			ArrayList listA0100 = new ArrayList();
			ArrayList<String> listNBase = new ArrayList<String>();
			String whereSql = "";
			String sqlStr = "SELECT UserName,FullName,A0100,NBase FROM OperUser where UserName in ("+clerk_usernameStrs.toString().substring(1)+")";
			rs = dao.search(sqlStr);
			while (rs.next()) {
				String fullName = KqDataUtil.nullif(rs.getString("FullName"));
				String userName = rs.getString("UserName");
				userFullMap.put(userName, StringUtils.isBlank(fullName) ? userName : fullName);
				
				String a0100 = KqDataUtil.nullif(rs.getString("A0100"));
				String nBase = KqDataUtil.nullif(rs.getString("NBase")).toUpperCase();
				if(StringUtils.isNotBlank(nBase) && StringUtils.isNotBlank(a0100)) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("UserName", userName);
					map.put("value", nBase+a0100);
					list.add(map);
					listA0100.add(a0100);
					if(!listNBase.contains(nBase))
						listNBase.add(nBase);
					whereSql += ",?";
				}
			}
			
			if(list.size() > 0) {
				StringBuffer sql = new StringBuffer();
				ArrayList valueList = new ArrayList();
				valueList.addAll(listA0100);
				for(int i=0;i<listNBase.size();i++) {
					if(i > 0) {
						sql.append(" UNION ");
						valueList.addAll(listA0100);
					}
					String nbase = listNBase.get(i);
					sql.append("select '"+nbase+"' nbase,A0100,A0101 from ").append(nbase).append("A01");
					sql.append(" where A0100 in (").append(whereSql.substring(1)).append(")");
				}
				HashMap<String, String> A0101Map = new HashMap<String, String>();
				rs = dao.search(sql.toString(), valueList);
				while (rs.next()) {
					A0101Map.put(rs.getString("nbase")+rs.getString("A0100"), rs.getString("A0101"));
				}
				
				for(int i=0;i<list.size();i++) {
					HashMap<String, String> map = (HashMap<String, String>)list.get(i);
					String userName = map.get("UserName");
					String value = map.get("value");
					userFullMap.put(userName, A0101Map.get(value));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userFullMap;
	}
	/**
	 * 校验方案详细信息中考勤员姓名
	 * 支持该方法获取的机构信息kqCtrlParamUtil.getFillingAgencysMap("","")
	 * @param rs
	 * @param dao
	 * @param userNames
	 * @param dataList
	 * @return
	 */
	private ArrayList<LazyDynaBean> checkListKq_scheme(RowSet rs, ContentDAO dao, ArrayList<String> userNames
			, ArrayList<LazyDynaBean> dataList) {
		try {
			//*更换关联用户问题  查的时候反查start*//
			if(userNames.size() > 0) {
				// 54651 业务用户关联自助用户的姓名
				HashMap<String, String> userFullMap = getUserFullMap(rs, dao, userNames);
				for(int i=0;i<dataList.size();i++) {
					LazyDynaBean bean = dataList.get(i);
					String clerk_username = (String)bean.get("clerk_username");
					String clerk_fullname = userFullMap.get(clerk_username);
					bean.set("clerk_fullname", (StringUtils.isBlank(clerk_fullname) ? clerk_username : clerk_fullname));//考勤人
					ArrayList<HashMap> listFillingAgencys = (ArrayList<HashMap>)bean.get("org_map");
					for(int j=0;j<listFillingAgencys.size();j++) {
						HashMap orgmap = (HashMap)listFillingAgencys.get(j);
						String orgUserName = (String)orgmap.get("y_clerk_username");
						String y_clerk_fullname = userFullMap.get(orgUserName);
						orgmap.put("clerk_username", orgUserName + " ("+y_clerk_fullname+")");
						orgmap.put("clerk_fullname", y_clerk_fullname);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//*更换关联用户问题end*//
		return dataList;
	}
	
	@Override
    public boolean checkReviewPersonDealt(String org_id, String scheme_id, String old_name) throws GeneralException{
		boolean bool = false;
		RowSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select scheme_id from kq_extend_log");
			sql.append(" where (sp_flag='02' or sp_flag='07') and scheme_id=? and appuser like ? and curr_user=?");
			
			ArrayList parameterList = new ArrayList();
	        parameterList.add(PubFunc.decrypt(scheme_id));
	        parameterList.add("%;"+PubFunc.decrypt(old_name)+";");
	        // 如果org_id为空 则为方案审核人；否则为机构审核人
	        if(StringUtils.isNotBlank(org_id)) {
	        	sql.append(" and org_id=?");
	        	parameterList.add("4");
	        	parameterList.add(PubFunc.decrypt(org_id));
	        }else {
	        	parameterList.add("2");
	        }
	        ContentDAO dao = new ContentDAO(this.conn);	
	        rs = dao.search(sql.toString(), parameterList);
			if (rs.next()) {
				bool = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return bool;
	}
}
