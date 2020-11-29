package com.hjsj.hrms.module.kq.config.shiftgroup.businessobject.impl;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.module.kq.config.shiftgroup.businessobject.ShiftGroupService;
import com.hjsj.hrms.module.kq.util.KqPrivForHospitalUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**  
 * <p>Title: ShiftGroupServiceImpl</p>  
 * <p>Description: 班组管理接口实现类</p>  
 * <p>Company: hjsj</p>
 * @date 2018年11月1日 下午1:24:09
 * @author linbz  
 * @version 7.5
 */  
public class ShiftGroupServiceImpl implements ShiftGroupService {
	private UserView userView;
	private Connection conn;
	
	public ShiftGroupServiceImpl(UserView userView,Connection conn) {
		this.userView = userView;
		this.conn = conn;
	}

	/**
	 * 获取班组信息
	 * getShiftGroup
	 * @param groupId
	 * @return
	 * @throws GeneralException
	 * @date 2018年11月1日 下午4:00:41
	 * @author linbz
	 */
	@Override
    public String getShiftGroup(String groupId) throws GeneralException {
		JSONObject obj = new JSONObject();
		String return_code = "success";
		String return_msg = "";
		RowSet rs = null;
		try {
			ArrayList<HashMap<Integer, String>> classIds = this.listClassData();
			String adminId = "";
			ContentDAO dao = new ContentDAO(this.conn);
			RecordVo classVo = new RecordVo("kq_shift_group");
			HashMap<Integer, ArrayList> classIdDescs = new HashMap<Integer, ArrayList>();
			// 若没有班组id则视为新建
			if(StringUtils.isNotBlank(groupId)) {
				
				groupId = PubFunc.decrypt(groupId);
				classVo.setString("group_id", groupId);
				classVo = dao.findByPrimaryKey(classVo);
				
				obj.put("group_id", PubFunc.encrypt(groupId));
				obj.put("name", classVo.getString("name"));
				String orgid = classVo.getString("org_id");
				// 兼容老的所属机构 UN0101
				if(orgid.startsWith("U"))
					orgid = orgid.substring(2);
				String unStr = AdminCode.getCodeName("UN", orgid);
				String orgDesc = StringUtils.isBlank(unStr) ? AdminCode.getCodeName("UM", orgid) : unStr;
				obj.put("org_id", orgid+"`"+orgDesc);
//				obj.put("member_count", String.valueOf(classVo.getInt("member_count")));
				// 排班类型 0：固定班制 1：排班制
				int shiftType = classVo.getInt("shift_type");
				obj.put("shift_type", String.valueOf(shiftType));
				obj.put("rest_type", String.valueOf(classVo.getInt("rest_type")));
				obj.put("shift_cycle", (1==shiftType) ? String.valueOf(classVo.getInt("shift_cycle")) : "");
				String shiftData = classVo.getString("shift_data");
				String[] shiftDatas = shiftData.split(";");//StringUtils.split(shiftData, ";");
				// 班次保存格式7天的班次数据以分号隔开，每天的班次以逗号隔开1,2,3;1;2,3;..
				for(int i=0;i<shiftDatas.length;i++) {
					String[] classids = StringUtils.split(shiftDatas[i], ",");
					ArrayList list = new ArrayList();
					for(int j=0;j<classids.length;j++) {
						String classid = classids[j];
						for (int k = 0; k < classIds.size(); k++) {
							HashMap<Integer, String> classMap=(HashMap<Integer, String>) classIds.get(k);
							if (classMap.get(Integer.parseInt(classid))!=null) {
								String classdesc = classMap.get(Integer.parseInt(classid));
								// 52936 已删除的不显示
								if(StringUtils.isBlank(classdesc)) 
									continue;
								list.add(classid + "`" + classdesc);
								break;
							}
						}
					}
					classIdDescs.put(i+1, list);
				}
				obj.put("shift_data", shiftData);
				adminId = classVo.getString("admin_id");
				obj.put("admin_id", adminId);
				obj.put("admin_name", classVo.getString("admin_name"));
				Date strdate = classVo.getDate("start_date");
				obj.put("start_date", DateUtils.format((null==strdate?new Date():strdate), "yyyy-MM-dd"));
				Date enddate = classVo.getDate("end_date");
				obj.put("end_date", null==enddate ? "" : DateUtils.format(enddate, "yyyy-MM-dd"));
			}else{
				
				obj.put("group_id", "");
				obj.put("name", "");
				KqPrivForHospitalUtil kp = new KqPrivForHospitalUtil(userView, conn);
				String orgid = kp.getPrivB0110();
				if(StringUtils.isEmpty(orgid) && this.userView.isSuper_admin())
					orgid = kp.getTopUNCodeitemid();
				String unStr = AdminCode.getCodeName("UN", orgid);
				String orgDesc = StringUtils.isBlank(unStr) ? AdminCode.getCodeName("UM", orgid) : unStr;
				obj.put("org_id", orgid+"`"+orgDesc);
				obj.put("member_count", "");
				// 默认排班类型 0：固定班制 
				obj.put("shift_type", "0");
				// 默认节假日自动排休
				obj.put("rest_type", "1");
				obj.put("shift_cycle", "7");
				obj.put("shift_data", "");
				obj.put("admin_id", "");
				obj.put("admin_name", "");
				obj.put("start_date", DateUtils.format(new Date(), "yyyy-MM-dd"));
				obj.put("end_date", "9999-12-31");
			}
			String userId = this.getUserIdByGuidkey(adminId);
			obj.put("user_id", StringUtils.isBlank(userId) ? "" : PubFunc.encrypt(userId));
			String photoUrl = this.getPerPhotoUrl(userId);
			obj.put("photourl", photoUrl);
			obj.put("nbases", this.getNbases());
			obj.put("class_ids_All", classIds);
			obj.put("classid_descs", classIdDescs);
			
		} catch (Exception e) {
			return_code = "fail";
			return_msg = e.getMessage();
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
			obj.put("return_code", return_code);
			obj.put("return_msg", return_msg);
		}
		return obj.toString();
	}
	/**  新增或修改班组信息
	 * @param jsonObj	班组信息json串
	 * @return
	 * @throws GeneralException
	 * @date 2018年11月1日 下午4:28:21
	 * @author linbz  
	 */ 
	@Override
    public String saveShiftGroup(JSONObject jsonObj) throws GeneralException {
	
		JSONObject obj = new JSONObject();
		String return_code = "success";
		String return_msg = "";
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			
			String groupId = jsonObj.getString("group_id");
			String name = jsonObj.getString("name"); 
			String orgId = jsonObj.getString("org_id");
			orgId = StringUtils.split(orgId, "`")[0];
			// 人数
//			String memberCountStr = jsonObj.getString("member_count");
			// 排班类型 0：固定班制 1：排班制
			String shiftType = jsonObj.getString("shift_type");
			// 与排班制同用  每周期天数 
			String shiftCycle = jsonObj.getString("shift_cycle");
			// 与固定班制同用  节假日自动排休 0：不排休；1：排休
			String restType = jsonObj.getString("rest_type"); 
			String shiftData = jsonObj.getString("shift_data"); 
			String userid = jsonObj.getString("user_id"); 
			String adminId = this.getGuidkeyByA0100(PubFunc.decrypt(userid));
			String adminName = jsonObj.getString("admin_name"); 
			String startDateStr = jsonObj.getString("start_date");
			String endDateStr = jsonObj.getString("end_date");
			
			RecordVo classVo = new RecordVo("kq_shift_group");
			boolean forUpdate = false;
			if(StringUtils.isNotBlank(groupId)) {
				forUpdate = true;
				groupId = PubFunc.decrypt(groupId);
				classVo.setString("group_id", groupId);
				classVo = dao.findByPrimaryKey(classVo);
			}else {
				// 校验班组名称是否重复
				if(this.isCheckGroupName(name)) {
					obj.put("return_code", "fail");
					obj.put("return_msg", ResourceFactory.getProperty("kq.group.namerepeat"));
					return obj.toString();
				}
				IDGenerator idg = new IDGenerator(2, this.conn);
				groupId = idg.getId("kq_shift_group.group_id");
				classVo.setString("group_id", groupId);
			}
			
			classVo.setString("name", name);
			classVo.setString("org_id", orgId);
			classVo.setInt("shift_type", Integer.parseInt(shiftType));
			classVo.setInt("rest_type", Integer.parseInt(restType));
			if("1".equals(shiftType))
				classVo.setInt("shift_cycle", Integer.parseInt(shiftCycle));
			
			classVo.setString("shift_data", shiftData);
			classVo.setString("admin_id", adminId);
			classVo.setString("admin_name", adminName);
			
			classVo.setDate("start_date", DateUtils.getDate(startDateStr, "yyyy-MM-dd"));
			classVo.setDate("end_date", DateUtils.getDate(endDateStr, "yyyy-MM-dd"));
			
			if(forUpdate) {
				dao.updateValueObject(classVo);
			}else {
				classVo.setDate("create_time",new Date());
				classVo.setString("create_user",this.userView.getUserFullName());
				dao.addValueObject(classVo);
			}
		} catch (Exception e) {
			return_code = "fail";
			return_msg = e.getMessage();
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
			obj.put("return_code", return_code);
			obj.put("return_msg", return_msg);
		}
		
		return obj.toString();
	}
	/**
	 * 校验班组名称是否重复
	 * isCheckGroupName
	 * @param groupName
	 * @return
	 * @date 2018年12月21日 下午5:58:36
	 * @author linbz
	 */
	private boolean isCheckGroupName(String groupName) {
		boolean boolflag = false;
		RowSet rs = null;
		try {
			ArrayList values = new ArrayList();
			values.add(groupName);
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search("select group_id from kq_shift_group where name=? ", values);
			if(rs.next()) 
				boolflag = true;
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return boolflag;
	}
	/**
	 * 删除班组信息
	 * delShiftGroup
	 * @param groupId
	 * @return
	 * @throws GeneralException
	 * @date 2018年11月1日 下午5:00:41
	 * @author linbz
	 */
	@Override
    public String delShiftGroup(String groupId) throws GeneralException {
		String return_code = "success";
		String return_msg = "";
		JSONObject jsonObj = new JSONObject();
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			groupId = PubFunc.decrypt(groupId);
			StringBuffer delSql = new StringBuffer();
			ArrayList value = new ArrayList();
			value.add(groupId);
			StringBuffer sql = new StringBuffer();
			/**
			 * 获取班组 设置的方案
			 * scheme_id	方案id
			 * scope		时间范围	2018.09.24-2018.09.30
			 */
			sql.append("select scheme_id,scope ");
			sql.append(" from kq_shift_scheme ");
			sql.append(" where group_id=? ");
			String schemeidValue = "";
			rs = dao.search(sql.toString(), value);
			while (rs.next()) {
				String schemeid = rs.getString("scheme_id");
				schemeidValue += schemeid + ",";
				String scope = rs.getString("scope");
				String stratDate = scope.split("-")[0];
				String endDate = scope.split("-")[1];
				// 1、删除kq_employ_shift_v2员工排班表 
				delSql.setLength(0);
				delSql.append("delete from kq_employ_shift_v2 "
						+ "where "+Sql_switcher.dateToChar("q03Z0", "yyyy-MM-dd")+">=? and "+Sql_switcher.dateToChar("q03Z0", "yyyy-MM-dd")+"<=?"
						+ " and guidkey in (select guidkey from kq_shift_scheme_emp where scheme_id=? ) ");
				value = new ArrayList();
				value.add(stratDate);
				value.add(endDate);
				value.add(schemeid);
				dao.update(delSql.toString(), value);
			}
			// 2、删除kq_shift_scheme_emp排班方案人员表
			if(!"".equals(schemeidValue)) {
				schemeidValue = schemeidValue.substring(0, schemeidValue.length()-1);
				delSql.setLength(0);
				delSql.append("delete from kq_shift_scheme_emp where scheme_id in(?) ");
				value = new ArrayList();
				value.add(groupId);
				dao.update(delSql.toString(), value);
			}
			
			value = new ArrayList();
			value.add(groupId);
			// 3、删除kq_shift_scheme排班方案表
			delSql.setLength(0);
			delSql.append("delete from kq_shift_scheme where group_id=? ");
			dao.update(delSql.toString(), value);
			// 4、删除kq_group_emp_v2人员班组对应表
			delSql.setLength(0);
			delSql.append("delete from kq_group_emp_v2 where group_id=? ");
			dao.update(delSql.toString(), value);
			// 5、删除kq_shift_group班组信息表
			delSql.setLength(0);
			delSql.append("delete from kq_shift_group where group_id=? ");
			dao.update(delSql.toString(), value);
			
		} catch (Exception e) {
			e.printStackTrace();
			return_code = "fail";
			return_msg = e.toString();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
			jsonObj.put("return_code", return_code);
			jsonObj.put("return_msg", return_msg);
		}
		return jsonObj.toString();
	}
	/**
	 * 获取班组信息表格
	 * getShiftGroupTableConfig
	 * @return
	 * @throws GeneralException
	 * @date 2018年11月1日 下午5:24:00
	 * @author linbz
	 */
	@Override
    public String getShiftGroupTableConfig(String validityflag) throws GeneralException {
		
		String config = "";
		try {
			ArrayList columnList = this.listShiftGroupColumns();
			
			TableConfigBuilder builder = new TableConfigBuilder("kqshiftgroup_01", columnList
        			, "kqshiftgroup_01", this.userView, this.conn);
			builder.setTitle(ResourceFactory.getProperty("kq.kq_rest.group"));//"班组管理"
			builder.setSelectable(false);// 选框
        	builder.setDataSql(this.getShiftGroupSql(validityflag));
            builder.setOrderBy(" order by group_id ");
            builder.setColumnFilter(true);
            builder.setTableTools(this.listShiftGroupButtons());
            config = builder.createExtTableConfig();
            
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return config;
	}
	/**
	 * 获取班组信息SQL
	 * getShiftGroupSql
	 * @return
	 * @date 2018年11月1日 下午5:24:30
	 * @author linbz
	 */
	private String getShiftGroupSql(String validityflag) throws GeneralException {
    	
		StringBuffer sql = new StringBuffer("");
		try {
	    	String selectStr = "select '' borrowing_id,name,shift_type,member_count,admin_id,admin_name,start_date,end_date,create_user,create_time,group_id";
	    	String fromStr = " from kq_shift_group ";
	    	String whereStr = " and (("+ Sql_switcher.dateToChar("end_date", "yyyy-mm-dd") + ">'" 
	    			+ DateUtils.format(new Date(), "yyyy-MM-dd") + "') or end_date is null ";
		    if (Constant.MSSQL == Sql_switcher.searchDbServer()) 
		    	whereStr += " or end_date='' ";
		    whereStr += ")";
		    
	    	sql.append(selectStr);
	    	// 兼容老的班组所属机构  去除前两位 字符UN 或 UM
	    	String orgsub = "";
	    	if (Constant.ORACEL == Sql_switcher.searchDbServer()) 
	    		orgsub = "substr(org_id, 3)";
	    	else 
	    		orgsub = "right(org_id,(len(org_id)-2))";
	    	sql.append(","+ orgsub +" org_id");
	    	
	    	sql.append(fromStr);
	    	sql.append(" where (org_id like 'UN%' or org_id like 'UM%') ");
	    	if("1".equals(validityflag))
	    		sql.append(whereStr);
	    	// 班组所属机构权限
	    	String whereInOrg = KqPrivForHospitalUtil.getPrivB0110Whr(userView, orgsub, KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
	    	sql.append(" and "+ whereInOrg);
	    	sql.append(" UNION ALL ");
	    	sql.append(selectStr);
	    	// 新的班组所属机构  前两位没有 字符UN 或 UM
			sql.append(",org_id");
	    	sql.append(fromStr);
	    	sql.append(" where org_id not like 'UN%' and org_id not like 'UM%'");
	    	if("1".equals(validityflag))
	    		sql.append(whereStr);
	    	// 班组所属机构权限
	    	whereInOrg = KqPrivForHospitalUtil.getPrivB0110Whr(userView, "org_id", KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
	    	sql.append(" and ("+ whereInOrg);
	    	String guidkey = this.getGuidkeyByA0100(this.userView.getDbname()+this.userView.getA0100());
	    	// 增加登录用户是负责人的或是创建人的条件  50895 该条件应与权限是或的关系  但是与终止日期是是并且的关系
	    	sql.append(" or( admin_id='"+ guidkey +"' or create_user='"+ this.userView.getUserFullName() +"') )");
	    	
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    	return sql.toString();
    }
	/**
	 * 获取班组表格列集合
	 * listShiftGroupColumns
	 * @return
	 * @date 2018年11月1日 下午5:24:42
	 * @author linbz
	 */
	private ArrayList<ColumnsInfo> listShiftGroupColumns() {
		
        ArrayList<ColumnsInfo> columnList = new ArrayList<ColumnsInfo>();
        try{
        	/**
        	 * kq_shift_group
        	 * Group_id 班组序号 Name 名称 Org_id 所属机构号 Member_count 人数 Shift_type 排班类型 Shift_cycle 每周期天数 Shift_data 排班 rest_type 节假日自动排休
        	 * Admin_id 负责人id Admin_name 负责人名称 Start_date 起始日期 End_date 终止日期 Create_time 创建日期 Create_user 创建用户
        	 */
        	// "班组名称"
        	ColumnsInfo columnsInfo = getColumnsInfo("name", ResourceFactory.getProperty("kq.kq_rest.group.name"), 200, "0", "A", 100, 0, "kq_shift_group");
        	columnsInfo.setLocked(true);
    		columnsInfo.setRendererFunc("shiftGroup.groupNameFunc");
    		columnList.add(columnsInfo);
    		// "排班类型"
    		columnsInfo = getColumnsInfo("shift_type", ResourceFactory.getProperty("kq.kq_rest.group.type"), 100, "0", "A", 100, 0, "kq_shift_group");
    		// 字符串类型模拟代码类
    		ArrayList<CommonData> list = new ArrayList<CommonData>();
    		// "固定班制"
    		CommonData data = new CommonData("0", ResourceFactory.getProperty("kq.kq_rest.group.fixed"));
    		list.add(data);
    		// "排班制"
    		data = new CommonData("1", ResourceFactory.getProperty("kq.kq_rest.group.cycle"));
    		list.add(data);
    		columnsInfo.setOperationData(list);
    		columnList.add(columnsInfo);
    		// "人数"
    		columnsInfo = getColumnsInfo("member_count", ResourceFactory.getProperty("menu.gz.personnum"), 70, "0", "N", 100, 0, "kq_shift_group");
    		columnsInfo.setRendererFunc("shiftGroup.renderPerNum");
    		columnList.add(columnsInfo);
    		// "负责人"
    		columnsInfo = getColumnsInfo("admin_name", ResourceFactory.getProperty("lable.zp_plan.staff_id"), 80, "0", "A", 100, 0, "kq_shift_group");
    		columnList.add(columnsInfo);
    		// "起始日期"
    		columnsInfo = getColumnsInfo("start_date", ResourceFactory.getProperty("kq.deration_details.start"), 100, "0", "D", 10, 0, "kq_shift_group");
    		columnList.add(columnsInfo);
    		// "终止日期"
    		columnsInfo = getColumnsInfo("end_date", ResourceFactory.getProperty("kq.deration_details.end"), 100, "0", "D", 10, 0, "kq_shift_group");
    		columnList.add(columnsInfo);
    		// "所属机构"
    		columnsInfo = getColumnsInfo("org_id", ResourceFactory.getProperty("kq_shift.group.org"), 150, "UM", "A", 100, 0, "kq_shift_group");
//    		columnsInfo.setCtrltype("1");
//    		columnsInfo.setNmodule("10");
    		columnList.add(columnsInfo);
    		// "创建人"
    		columnsInfo = getColumnsInfo("create_user", ResourceFactory.getProperty("jx.khplan.creator"), 80, "0", "A", 100, 0, "kq_shift_group");
    		columnList.add(columnsInfo);
    		// "创建日期"
    		columnsInfo = getColumnsInfo("create_time", ResourceFactory.getProperty("jx.khplan.createdate"), 100, "0", "D", 10, 0, "kq_shift_group");
    		columnList.add(columnsInfo);
    		// "操作"
            columnsInfo = getColumnsInfo("borrowing_id", ResourceFactory.getProperty("kh.field.opt"), 170, "0", "A", 100, 0, "");
    		columnsInfo.setRendererFunc("shiftGroup.groupHandleFunc");
    		columnsInfo.setSortable(false);
    		columnList.add(columnsInfo);
            // 隐藏 "班组序号"
    		columnsInfo = getColumnsInfo("group_id", ResourceFactory.getProperty("kq.kq_rest.group.num"), 0, "0", "A", 100, 0, "");
    		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
    		columnsInfo.setEncrypted(true);
    		columnList.add(columnsInfo);
    		// 隐藏 admin_id
    		columnsInfo = getColumnsInfo("admin_id", "admin_id", 0, "0", "A", 100, 0, "");
    		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
    		columnsInfo.setEncrypted(true);
    		columnList.add(columnsInfo);
    		
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return columnList;
    }
	/**
	 * 获取单个列对象
	 * getColumnsInfo
	 * @param columnId
	 * @param columnDesc
	 * @param columnWidth
	 * @param codesetId
	 * @param columnType
	 * @param columnLength
	 * @param decimalWidth
	 * @param fieldsetid
	 * @return
	 * @date 2018年11月1日 下午5:25:04
	 * @author linbz
	 */
	private ColumnsInfo getColumnsInfo(String columnId, String columnDesc,
			int columnWidth, String codesetId, String columnType,
			int columnLength, int decimalWidth, String fieldsetid) {

		ColumnsInfo columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId(columnId);
		columnsInfo.setColumnDesc(columnDesc);
		columnsInfo.setColumnWidth(columnWidth);// 显示列宽
		columnsInfo.setCodesetId(codesetId);// 指标集
		columnsInfo.setColumnType(columnType);// 类型N|M|A|D
		columnsInfo.setColumnLength(columnLength);// 显示长度
		columnsInfo.setDecimalWidth(decimalWidth);// 小数位
		columnsInfo.setFieldsetid(fieldsetid);
//		columnsInfo.setReadOnly(true);// 是否只读

		return columnsInfo;
	}
	/**
	 * 获取班组表格对象集合
	 * listShiftGroupButtons
	 * @return
	 * @date 2018年11月1日 下午5:26:58
	 * @author linbz
	 */
	private ArrayList<ButtonInfo> listShiftGroupButtons(){
    	ArrayList<ButtonInfo> buttonList = new ArrayList<ButtonInfo>();
    	
    	ButtonInfo querybox = new ButtonInfo();
    	querybox.setText(ResourceFactory.getProperty("kq.kq_rest.group.add"));//"创建班组"
    	querybox.setId("addGroupid");
    	querybox.setHandler("shiftGroup.showShiftGroup('')");
    	
    	if(this.userView.hasTheFunction("272020201"))
    		buttonList.add(querybox);
    	
    	querybox = new ButtonInfo();
        querybox.setFunctionId("KQ00021301");
        querybox.setType(ButtonInfo.TYPE_QUERYBOX);
        querybox.setText(ResourceFactory.getProperty("kq.kq_rest.group.inputname"));//"请输入班组名称..."
        buttonList.add(querybox);
        
    	return buttonList;
    }
	/**
	 * 获取人员头像路径
	 * getPerPhotoUrl
	 * @param userId	库前缀+a0100	Usr00000009
	 * @return
	 * @date 2018年11月6日 下午9:09:25
	 * @author linbz
	 */
	private String getPerPhotoUrl(String userId) {
		String photoUrl = "";
		try{
			if(StringUtils.isNotBlank(userId)) {
				String perDbName =userId.substring(0, 3);//人员库前缀
				String perA0100 = userId.substring(3, userId.length());
				PhotoImgBo photoImgBo = new PhotoImgBo(this.conn);
				photoUrl = photoImgBo.getPhotoPathLowQuality(perDbName, perA0100);
			}else {
				photoUrl = "/images/photo.jpg";
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return photoUrl;
	}
	/**
	 * 获取班次列表 班次id 班次名称
	 * listClassData
	 * @return
	 * @date 2018年11月7日 下午1:41:07
	 * @author linbz
	 */
	private ArrayList<HashMap<Integer,String>> listClassData() {
		ArrayList<HashMap<Integer,String>> classMapList=new ArrayList<HashMap<Integer,String>>();
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search("select class_id,name,abbreviation from kq_class where is_validate=1 order by seq");
			while(rs.next()) {
				HashMap<Integer,String> classIds = new HashMap<Integer,String>();
				String abbreviation = rs.getString("abbreviation");
				classIds.put(rs.getInt("class_id"), (StringUtils.isBlank(abbreviation)?rs.getString("name"):abbreviation));
				classMapList.add(classIds);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return classMapList;
	}
	/**
	 * 获取库集合
	 * getNbases
	 * @return
	 * @date 2018年11月7日 上午11:28:34
	 * @author linbz
	 */
	private String getNbases() {
		String nbases = "";
		try {
			ArrayList<String> kq_dbase_list = KqPrivForHospitalUtil.getB0110Dase(userView, conn);
			for(int i=0;i<kq_dbase_list.size();i++) {
				String nbase = kq_dbase_list.get(i);
				if(StringUtils.isBlank(nbase))
					continue;
				nbases += nbase + ",";
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return nbases;
	}
	/**
	 * 通过guidkey获取a0100
	 * getUserIdByGuidkey
	 * @param guidkey
	 * @return
	 * @date 2018年11月7日 上午11:28:04
	 * @author linbz
	 */
	private String getUserIdByGuidkey(String guidkey) {
		String userid = "";
		RowSet rs = null;
		try {
			String nbases = this.getNbases();
			String[] nbaseList = nbases.split(",");
			StringBuffer sql = new StringBuffer("");
			ArrayList values = new ArrayList();
			for(int i=0;i<nbaseList.length;i++) {
				String dbname = nbaseList[i];
				if(StringUtils.isBlank(dbname))
					continue;
				sql.append("select '").append(dbname).append("' dbname,A0100 from ").append(dbname).append("A01 where GUIDKEY=? ");
				if(i < nbaseList.length-1)
					sql.append(" UNION ALL ");
				values.add(guidkey);
			}
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString(), values);
			while(rs.next()) {
				userid = rs.getString("dbname") + rs.getString("A0100");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return userid;
	}
	/**
	 * 通过库+a0100获取guidkey
	 * getGuidkeyByA0100
	 * @param userId
	 * @return
	 * @date 2018年11月7日 上午11:27:26
	 * @author linbz
	 */
	private String getGuidkeyByA0100(String userId) {
		String guidkey = "";
		RowSet rs = null;
		try {
			// 45167  若userid为空或长度不够 可能为业务用户或其他 则直接返回空
			if(StringUtils.isBlank(userId) || userId.length()<3) 
				return guidkey;
			String perDbName =userId.substring(0, 3);//人员库前缀
			String perA0100 = userId.substring(3, userId.length());
			ArrayList values = new ArrayList();
			values.add(perA0100);
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search("select guidkey from "+perDbName+"A01 where a0100=? ", values);
			while(rs.next()) {
				guidkey = rs.getString("guidkey");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return guidkey;
	}
	/**
	 * 获取班组需要的其他参数信息等
	 * getShiftGroupInfo
	 * @return
	 * @throws GeneralException
	 * @date 2019年2月28日 上午11:39:32
	 * @author linbz
	 */
	@Override
    public HashMap getShiftGroupInfo() throws GeneralException {
		
		HashMap map = new HashMap();
		try {
			String guidkey = this.getGuidkeyByA0100(this.userView.getDbname()+this.userView.getA0100());
			map.put("user_guidkey", PubFunc.encrypt(guidkey));
			map.put("user_name", this.userView.getUserFullName());
			// 45191 选人控件需要传入权限部门id 如：010102,020202
			String orgids = "";
			KqPrivForHospitalUtil kp = new KqPrivForHospitalUtil(userView, conn);
			String privB0110Str = KqPrivForHospitalUtil.getB0110(userView);
			String[] privB0110s = privB0110Str.split("`");
			for (int i = 0; i < privB0110s.length; i++) {
	            String privB0110 = privB0110s[i].trim();
	            if (StringUtils.isEmpty(privB0110))
	                continue;
	            else if ("HJSJ".equalsIgnoreCase(privB0110))
	                continue;
	            else {
	            	orgids += privB0110 + ",";
	            }
			}
			map.put("orgid", (orgids.length() > 0)?orgids.substring(0, orgids.length()-1):"");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}
}
