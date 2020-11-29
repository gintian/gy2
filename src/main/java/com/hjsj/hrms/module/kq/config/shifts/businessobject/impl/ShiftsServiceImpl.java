package com.hjsj.hrms.module.kq.config.shifts.businessobject.impl;

import com.hjsj.hrms.module.kq.config.shifts.businessobject.ShiftsService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.util.KqDataUtil;
import com.hjsj.hrms.module.kq.util.KqPrivForHospitalUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class ShiftsServiceImpl implements ShiftsService {
	private UserView userView;
	private Connection conn;
	
	public ShiftsServiceImpl(UserView userView,Connection conn) {
		this.userView = userView;
		this.conn = conn;
	}
	
	@Override
    public String getShiftsTableConfig() throws GeneralException {
		String config = "";
		try {
			String datasql = this.getTableSql();
			ArrayList columnList = this.getColumnList();
			TableConfigBuilder builder = new TableConfigBuilder("shifts_list_subModuleId", columnList, "shifts", userView, conn);
			builder.setDataSql(datasql);//数据查询sql语句
			builder.setOrderBy("order by seq asc");//排序语句
			builder.setAutoRender(true);//是否自动渲染表格到页面
			builder.setTitle(ResourceFactory.getProperty("kq.shift.name"));// 标题
			builder.setSelectable(true);//选框
			builder.setEditable(false);//表格编辑
			builder.setPageSize(20);//每页条数
			ArrayList buttonList = this.getButtonList();//得到操作按钮
			builder.setTableTools(buttonList);//表格工具栏功能
			config = builder.createExtTableConfig();
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return config;
	}
	/**
	 * 表格数据sql
	 * @return
	 * @throws GeneralException 
	 */
	private String getTableSql() throws GeneralException {
		StringBuffer sql = new StringBuffer();
		try {
			//查询上级班次
			sql.append("select class_id,class_id as encrypt_class_id,");
			sql.append("name,abbreviation,domain_count,work_hours,");
			sql.append("color,symbol,statistics_type,org_id,remarks,is_validate,onduty_1,onduty_2,onduty_3,offduty_1,offduty_2,offduty_3");
			//超级用户可以看到所有的班次
			if(!this.userView.isSuper_admin()) {
				sql.append(",0 as seq,'true' as higLevel from kq_class where 1=2");
				/**
				 * 'true' as higLevel 公共+上级	不可编辑
				 * 'false' as higLevel 本级		  可编辑
				 */
				// 50016 取考勤管理范围条件
				String whereInOrg = KqPrivForHospitalUtil.getPrivB0110Whr(userView, "org_id", KqPrivForHospitalUtil.LEVEL_GLOBAL_PARENT);
				sql.append(" or "+ whereInOrg);
				// 55978 非超级用户只能编辑本部门的权限
				sql.append(" UNION ALL ");
				sql.append(" select class_id,class_id as encrypt_class_id,");
				sql.append("name,abbreviation,domain_count,work_hours,");
				sql.append("color,symbol,statistics_type,org_id,remarks,is_validate,onduty_1,onduty_2,onduty_3,offduty_1,offduty_2,offduty_3");
				sql.append(",seq,'false' as higLevel from kq_class where 1=2");
				// 获取本级条件
				whereInOrg = KqPrivForHospitalUtil.getPrivB0110Whr(userView, "org_id", KqPrivForHospitalUtil.LEVEL_SELF);
				sql.append(" or "+ whereInOrg);
				
			}else {
				sql.append(",seq,'false' as higLevel from kq_class");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return sql.toString();
	}
	
	private void getSuperB0110(String b0110,Set<String> set) {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search("select parentid from organization where codeitemid=? and codeitemid<>parentid",Arrays.asList(b0110));
			if(rs.next()) {
				String b0110_ = rs.getString("parentid");
				set.add(b0110_);
				getSuperB0110(b0110_,set);
			}else {
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
	}

	private ArrayList getColumnList() {
		ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
		
		//class_id
		ColumnsInfo clazzId = getColumnInfo("", 80,"class_id");
		clazzId.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		list.add(clazzId);
		//加密后class_id
		ColumnsInfo clazzId_e = getColumnInfo("", 80,"encrypt_class_id");
		clazzId_e.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		clazzId_e.setEncrypted(true);
		list.add(clazzId_e);
		
		ColumnsInfo name = getColumnInfo(ResourceFactory.getProperty("kq.shift.relief.name"), 180,"name");
		name.setRendererFunc("config_shifts_me.click2editShift");
		name.setLocked(true);
		name.setTextAlign("left");
		list.add(name);
		ColumnsInfo abbreviation = getColumnInfo(ResourceFactory.getProperty("kq.shift.relief.sname"), 80,"abbreviation");
		abbreviation.setTextAlign("left");
		list.add(abbreviation);
		ColumnsInfo domainCount = getColumnInfo(ResourceFactory.getProperty("kq.shift.tiems.count"), 60,"domain_count");
		domainCount.setTextAlign("right");
		domainCount.setColumnType("N");
		list.add(domainCount);
		ColumnsInfo timeRange = getColumnInfo(ResourceFactory.getProperty("kq.shift.tiems.range"), 180,"domain_scope");
		timeRange.setSortable(false);
		timeRange.setQueryable(false);
		timeRange.setTextAlign("left");
		timeRange.setRendererFunc("config_shifts_me.domainScopeRender");
		list.add(timeRange);
		ColumnsInfo workHours = getColumnInfo(ResourceFactory.getProperty("kq.shift.tiems.length"), 55,"work_hours");
		workHours.setColumnType("N");
		workHours.setTextAlign("right");
		workHours.setDecimalWidth(2);
		list.add(workHours);
		ColumnsInfo color = getColumnInfo(ResourceFactory.getProperty("kq.item.color"), 50,"color");
		color.setQueryable(false);
		color.setRendererFunc("config_shifts_me.renderColor");
		list.add(color);
		ColumnsInfo symbol = getColumnInfo(ResourceFactory.getProperty("kq.item.sign"), 50,"symbol");
		symbol.setTextAlign("left");
		list.add(symbol);
		ColumnsInfo statisticsType = getColumnInfo(ResourceFactory.getProperty("kq.item.statisticstype"),80,"statistics_type");
		statisticsType.setTextAlign("left");
		statisticsType.setCodesetId("85");
		list.add(statisticsType);
		ColumnsInfo b0110 = getColumnInfo(ResourceFactory.getProperty("kq_shift.group.org"), 150,"org_id");
		b0110.setTextAlign("left");
		b0110.setCodesetId("UM");
		list.add(b0110);
		
		//序号
		ColumnsInfo seq = getColumnInfo("", 80,"seq");
		seq.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		list.add(seq);
		//是否是上级
		ColumnsInfo higLevel = getColumnInfo("", 80,"higLevel");
		higLevel.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		list.add(higLevel);
		
		ColumnsInfo onduty_1 = getColumnInfo("", 80,"onduty_1");
		onduty_1.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		list.add(onduty_1);
		ColumnsInfo onduty_2 = getColumnInfo("", 80,"onduty_2");
		onduty_2.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		list.add(onduty_2);
		ColumnsInfo onduty_3 = getColumnInfo("", 80,"onduty_3");
		onduty_3.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		list.add(onduty_3);
		ColumnsInfo offduty_1 = getColumnInfo("", 80,"offduty_1");
		offduty_1.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		list.add(offduty_1);
		ColumnsInfo offduty_2 = getColumnInfo("", 80,"offduty_2");
		offduty_2.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		list.add(offduty_2);
		ColumnsInfo offduty_3 = getColumnInfo("", 80,"offduty_3");
		offduty_3.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		list.add(offduty_3);
		
		ColumnsInfo remarks = getColumnInfo(ResourceFactory.getProperty("kq.shift.text"), 200,"remarks");
		remarks.setTextAlign("left");
		remarks.setColumnType("M");
        remarks.setInputType(1);
		remarks.setSortable(false);
		remarks.setQueryable(false);
		list.add(remarks);
		ColumnsInfo validate = getColumnInfo(ResourceFactory.getProperty("kq.shift.on"), 70,"is_validate");
		validate.setRendererFunc("config_shifts_me.renderValidate");
		list.add(validate);
		return list;
	}

	private ArrayList getButtonList() {
		ArrayList buttonList = new ArrayList();
		if (this.userView.hasTheFunction("272020101")) {
			ButtonInfo newBtn = new ButtonInfo(ResourceFactory.getProperty("kq.search_feast.new"),"config_shifts_me.addShitView");
			newBtn.setId("shits_new");
			buttonList.add(newBtn);
		}
		if (this.userView.hasTheFunction("272020102")) {
			ButtonInfo modifyBtn = new ButtonInfo(ResourceFactory.getProperty("kq.feast_type_list.modify"),"config_shifts_me.showClassInfo(\"-1\")");
			modifyBtn.setId("shits_modify");
			buttonList.add(modifyBtn);
		}
		if (this.userView.hasTheFunction("272020103")) {
			ButtonInfo delBtn = new ButtonInfo(ResourceFactory.getProperty("kq.search_feast.delete"),"config_shifts_me.deleteShifts");
			delBtn.setId("shits_delete");
			buttonList.add(delBtn);
		}
		ButtonInfo queryBox = new ButtonInfo();
		queryBox.setType(ButtonInfo.TYPE_QUERYBOX);
		queryBox.setText(ResourceFactory.getProperty("kq.shift.fastquery.text"));
		queryBox.setFunctionId("KQ00020101");
		buttonList.add(queryBox);
		return buttonList;
	}

	private ColumnsInfo getColumnInfo(String desc,int colWidth,String columnId) {
		ColumnsInfo column = new ColumnsInfo();
		column.setColumnDesc(desc);
		column.setColumnId(columnId);
		column.setTextAlign("center");
		column.setColumnWidth(colWidth);
		return column;
	}

	@Override
    public String saveShift(JSONObject jsonObj) {
		ContentDAO dao = new ContentDAO(this.conn);
		JSONObject obj = new JSONObject();
		String return_code = "success";
		String return_msg = "";
		RowSet rs = null;
		try {
			String classId = jsonObj.getString("encrypt_class_id");
			String name = jsonObj.getString("name");//名称
			String abbreviation = jsonObj.getString("abbreviation");//简称
			String color = jsonObj.getString("color");
			String symbol = jsonObj.getString("symbol");//考勤符号
			symbol = "null".equals(symbol)?"":symbol;
			String statistics_type = jsonObj.getString("statistics_type");//统计属性 "0`大夜"
			String domain_count = jsonObj.getString("domain_count");
			Integer domain_count_ = null;
			if(StringUtils.isNotEmpty(domain_count) && !"null".equals(domain_count)) {
				domain_count_ = Integer.parseInt(domain_count);
			}
			
			String work_hours = jsonObj.getString("work_hours");
			Double work_hours_ = null;
			if(StringUtils.isNotEmpty(work_hours) && !"null".equals(work_hours)) {
				work_hours_ = Double.parseDouble(work_hours);
			}
			String ownorg = jsonObj.getString("org_id");//机构
			String remarks = jsonObj.getString("remarks");//机构 "01`某集团公司"
			String validate = jsonObj.getString("is_validate");//启用|禁用; =1 启用，  0|null 不启用
			
			RecordVo classVo = new RecordVo("kq_class");
			boolean forUpdate = false;
			StringBuffer checkSql = new StringBuffer();
			checkSql.append("select name,abbreviation from kq_class where (name=? or abbreviation=?)");
			List values = new ArrayList();
			values.add(name);
			values.add(abbreviation);
			if(StringUtils.isNotBlank(classId)) {
				forUpdate = true;
				classId = PubFunc.decrypt(classId);
				checkSql.append(" and class_id <> ?");
				values.add(classId);
				classVo.setString("class_id", classId);
				classVo = dao.findByPrimaryKey(classVo);
			}else {
				IDGenerator idg = new IDGenerator(2, this.conn);
				classId = idg.getId("kq_class.class_id");
				classVo.setString("class_id", classId);
			}
			rs = dao.search(checkSql.toString(),values);
			if(rs.next()) {
				String name_ = rs.getString("name");
				String abbreviation_ = rs.getString("abbreviation");
				String msg = "";
				//校验是否有重复的名称或者简称
				if(name.equals(name_)) {
					msg = ResourceFactory.getProperty("kq.shift.prompt.repeat");
					msg = msg.replace("{0}",ResourceFactory.getProperty("kq.shift.relief.name"));
					msg = msg.replace("{1}","【"+name+"】");
				}else if(abbreviation.equals(abbreviation_)) {
					msg = ResourceFactory.getProperty("kq.shift.prompt.repeat");
					msg = msg.replace("{0}",ResourceFactory.getProperty("kq.shift.relief.sname"));
					msg = msg.replace("{1}","【"+abbreviation+"】");
				}
				if(StringUtils.isNotEmpty(msg)) {
					throw new Exception(msg);
				}
			}
            if(jsonObj.containsKey("onduty_1")) {
                classVo.setString("onduty_1", jsonObj.getString("onduty_1"));
            }else{
                classVo.setString("onduty_1",null);
            }
            if(jsonObj.containsKey("offduty_1")) {
                classVo.setString("offduty_1", jsonObj.getString("offduty_1"));
            }else{
                classVo.setString("offduty_1",null);
            }
			if(jsonObj.containsKey("onduty_2")) {
				classVo.setString("onduty_2", jsonObj.getString("onduty_2"));
			}else{
                classVo.setString("onduty_2",null);
            }
			if(jsonObj.containsKey("offduty_2")) {
				classVo.setString("offduty_2", jsonObj.getString("offduty_2"));
			}else{
                classVo.setString("offduty_2",null);
            }
			if(jsonObj.containsKey("onduty_3")) {
				classVo.setString("onduty_3", jsonObj.getString("onduty_3"));
			}else{
                classVo.setString("onduty_3",null);
            }
			if(jsonObj.containsKey("offduty_3")) {
				classVo.setString("offduty_3", jsonObj.getString("offduty_3"));
			}else{
                classVo.setString("offduty_3",null);
            }
			classVo.setString("name", name);
			classVo.setString("abbreviation", abbreviation);
			classVo.setString("color", color);
			classVo.setString("symbol", symbol);
			classVo.setString("statistics_type", statistics_type.length()>0?statistics_type.split("`")[0]:"");
			if(domain_count_!= null)
				classVo.setInt("domain_count", domain_count_);
			if(work_hours_!= null)
				classVo.setDouble("work_hours", work_hours_);
			classVo.setString("org_id", ownorg.length()>0?ownorg.split("`")[0]:"");
			classVo.setString("remarks", remarks);
			classVo.setString("is_validate", validate);
			if(forUpdate) {
				dao.updateValueObject(classVo);
			}else {
				int seq = 1;
				rs = dao.search("select max(seq) maxSeq from kq_class");
				if(rs.next()) {
					seq = rs.getInt("maxSeq")+1;
				}
				classVo.setInt("seq", seq);
				classVo.setDate("create_time",new Date());
				classVo.setString("create_user",this.userView.getUserName());
				classVo.setString("create_fullname",this.userView.getUserFullName());
				dao.addValueObject(classVo);
			}
		} catch (Exception e) {
			return_code = "fail";
			return_msg = e.getMessage();
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
			obj.put("return_code", return_code);
			obj.put("return_msg", return_msg);
		}
		return obj.toString();
	}

	@Override
    public String delShit(String[] idArr) {
		String return_code = "success";
		String return_msg = "";
		JSONObject jsonObj = new JSONObject();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			StringBuffer pholder = new StringBuffer();
			List values = new ArrayList();
			for(String id : idArr) {
				pholder.append("?,");
				values.add(id);
			}
			String pholderStr = pholder.substring(0,pholder.length()-1);
			boolean delFlag = true;
			// 54559 删除之前查询该班次是否被使用
			StringBuffer searchSql = new StringBuffer();
			searchSql.append("select count(1) num from kq_employ_shift_v2 ");
			searchSql.append(" where Class_id_1 in (").append(pholderStr).append(")");
			searchSql.append(" or Class_id_2 in (").append(pholderStr).append(")");
			searchSql.append(" or Class_id_3 in (").append(pholderStr).append(")");
			List searchValues = new ArrayList();
			searchValues.addAll(values);
			searchValues.addAll(values);
			searchValues.addAll(values);
			rs = dao.search(searchSql.toString(), searchValues);
			if(rs.next()) {
				if(rs.getInt("num") > 0) {
					delFlag = false;
					return_code = "fail";
					return_msg = ResourceFactory.getProperty("kq.class.delete.error");
				}
			}
			//55735 删除之前查询该班次是否被固定班次使用
			for(String id : idArr) {
				searchSql.setLength(0);
				searchSql.append("select count(1) num from kq_shift_group ");
				searchSql.append(" where Shift_data like '%;").append(id).append(";%'");
				searchSql.append(" or Shift_data like '%;").append(id).append("%'");
				searchSql.append(" or Shift_data like '%").append(id).append(";%'");
				searchSql.append(" or Shift_data like '%,").append(id).append(";%'");
				searchSql.append(" or Shift_data like '%;").append(id).append(",%'");
				searchSql.append(" or Shift_data like '%,").append(id).append(",%'");
				searchSql.append(" or Shift_data like '%").append(id).append("%'");
				rs = dao.search(searchSql.toString());
				if(rs.next()) {
					if(rs.getInt("num") > 0) {
						delFlag = false;
						return_code = "fail";
						return_msg = ResourceFactory.getProperty("kq.class.delete.error");
					}
				}
			}
			// 所删除班次没有被使用
			if(delFlag) {
				StringBuffer delSql = new StringBuffer();
				delSql.append("delete from kq_class where class_id in (");
				delSql.append(pholderStr).append(")");
				
				dao.delete(delSql.toString(), values);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return_code="fail";
			return_msg=ResourceFactory.getProperty("kq.shift.mess.del.fail");
		}finally {
			PubFunc.closeDbObj(rs);
			jsonObj.put("return_code", return_code);
			jsonObj.put("return_msg", return_msg);
		}
		return jsonObj.toString();
	}

	@Override
    public LazyDynaBean getClassInfo(String classId) throws Exception {
		ContentDAO dao = new  ContentDAO(this.conn);
		RowSet rs = null;
		LazyDynaBean jsonObj = new LazyDynaBean();
		try {
			String sql = "select * from kq_class where class_id=?";
			List values = new ArrayList();
			values.add(classId);
			rs = dao.search(sql, values);
			if(rs.next()) {
				jsonObj.set("encrypt_class_id", PubFunc.encrypt(classId));
				jsonObj.set("name", rs.getString("name")==null?"":rs.getString("name"));
				jsonObj.set("abbreviation",rs.getString("abbreviation")==null?"":rs.getString("abbreviation"));
				jsonObj.set("domain_count",rs.getInt("domain_count"));
				jsonObj.set("symbol",rs.getString("symbol")==null?"":rs.getString("symbol"));
				jsonObj.set("work_hours",rs.getFloat("work_hours"));
				jsonObj.set("color",KqDataUtil.nullif(rs.getString("color")));
				jsonObj.set("onduty_1",rs.getString("onduty_1")==null?"":rs.getString("onduty_1"));
				jsonObj.set("onduty_2",rs.getString("onduty_2")==null?"":rs.getString("onduty_2"));
				jsonObj.set("onduty_3",rs.getString("onduty_3")==null?"":rs.getString("onduty_3"));
				jsonObj.set("offduty_1",rs.getString("offduty_1")==null?"":rs.getString("offduty_1"));
				jsonObj.set("offduty_2",rs.getString("offduty_2")==null?"":rs.getString("offduty_2"));
				jsonObj.set("offduty_3",rs.getString("offduty_3")==null?"":rs.getString("offduty_3"));
				if(!StringUtils.isEmpty(rs.getString("statistics_type"))) {
					String statistics_type = AdminCode.getCodeName("85",rs.getString("statistics_type"));
					jsonObj.set("statistics_type",rs.getString("statistics_type")+"`"+statistics_type);
				}
				if(!StringUtils.isEmpty(rs.getString("org_id"))) {
					String codeName = AdminCode.getCodeName("UN",rs.getString("org_id"));
					if(StringUtils.isEmpty(codeName)) {
						codeName = AdminCode.getCodeName("UM",rs.getString("org_id"));
					}
					if(!StringUtils.isEmpty(codeName)) {
						jsonObj.set("org_id",rs.getString("org_id")+"`"+codeName);
					}
				}
				jsonObj.set("remarks",rs.getString("remarks")==null?"":rs.getString("remarks"));
				jsonObj.set("is_validate",rs.getInt("is_validate"));
			}
		} finally{
			PubFunc.closeDbObj(rs);
		}
		return jsonObj;
	}

	@Override
    public String editValidate(String classId, String validate) {
		String return_code = "success";
		String return_msg = "";
		ContentDAO dao = new  ContentDAO(this.conn);
		JSONObject json = new JSONObject();
		try {
			String sql = "update kq_class set is_validate=? where class_id=?";
			List values = new ArrayList();
			values.add(validate);
			values.add(classId);
			dao.update(sql, values);
		} catch (Exception e) {
			return_code = "fail";
			return_msg = e.getMessage();
		}
		json.put("return_code", return_code);
		json.put("return_msg", return_msg);
		return json.toString();
	}
	@Override
    public String checkValidate(String classId) {
		String return_code = "success";
		ContentDAO dao = new  ContentDAO(this.conn);
		RowSet rs = null;
		JSONObject json = new JSONObject();
		try {
			String sql = "select guidkey from kq_employ_shift_v2  where Class_id_1=? or Class_id_2=? or Class_id_3=?";
			List values = new ArrayList();
			values.add(classId);
			values.add(classId);
			values.add(classId);
			rs = dao.search(sql, values);
			if (rs.next()) {
				return_code = "fail";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		json.put("return_code", return_code);
		return json.toString();
	}
	@Override
    public String adjustClassSeq(String from_id, String to_id) {
		String return_code = "success";
		String return_msg = "";
		ContentDAO dao = new  ContentDAO(this.conn);
		JSONObject json = new JSONObject();
		RowSet rs = null;
		try {
			StringBuffer str = new StringBuffer();
			List list = new ArrayList();
			String dropPosition = "";
			list.add(from_id);
			list.add(to_id);
			rs = dao.search("select seq,class_id from kq_class where class_id in (?,?)",list);
			int ori_seq=0,to_seq=0;
			while(rs.next()) {
				int seq = rs.getInt("seq");
				String class_id = rs.getString("class_id");
				if(class_id.equals(from_id))
					ori_seq = seq;
				if(class_id.equals(to_id))
					to_seq = seq;
			}
				
			if(Integer.valueOf(ori_seq) > Integer.valueOf(to_seq))
				dropPosition = "before";
			else if(Integer.valueOf(ori_seq) < Integer.valueOf(to_seq))
				dropPosition = "after";
			//获得考勤业务范围
			String privCode = this.userView.getUnitIdByBusi("11");
			StringBuffer self_b0110Sql = new StringBuffer();
			if(!this.userView.isSuper_admin()) {
				self_b0110Sql.append(" org_id in (");
				String[] b0110s = privCode.split("`");
				for(int i=0;i<b0110s.length;i++) {
					if(b0110s[i].length()>2) {
						self_b0110Sql.append("'"+b0110s[i].substring(2)+"'");
					}
					if(i<b0110s.length-1) {
						self_b0110Sql.append(",");
					}
				}
				self_b0110Sql.append(")");
			}
			if("before".equals(dropPosition)){//上移
				//在移动对象和目标对象之间的对象seq都加1.
				str.append("update kq_class set seq = seq+1 where");
				if(self_b0110Sql.length()>0)
					str.append(self_b0110Sql+" and ");
				str.append(" seq>=? and seq<?");
				list.clear();
				list.add(to_seq);
				list.add(ori_seq);
				dao.update(str.toString(),list);
				
				str.setLength(0);
				list.clear();
				
				//将上移对象的seq替换成目标对象的
				str.append("update kq_class set seq=? where class_id=?");
				list.add(to_seq);
				list.add(from_id);
				dao.update(str.toString(),list);
				
			}else if("after".equals(dropPosition)){//下移
				
				//在移动对象和目标对象之间的对象seq都减1.
				str.append("update kq_class set seq = seq-1 where ");
				if(self_b0110Sql.length()>0)
					str.append(self_b0110Sql+" and ");
				str.append("seq>? and seq<=?");
				list.clear();
				list.add(ori_seq);
				list.add(to_seq);
				dao.update(str.toString(),list);
				
				str.setLength(0);
				list.clear();
				
				//将下移对象的seq替换成目标对象的
				str.append("update kq_class set seq=? where class_id=?");
				list.add(to_seq);
				list.add(from_id);
				dao.update(str.toString(),list);
				
			}
		} catch (Exception e) {
			return_code = "fail";
			return_msg = ResourceFactory.getProperty("kq.shift.mess.sort.fail");
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
			json.put("return_code", return_code);
			json.put("return_msg", return_msg);
		}
		return json.toString();
	}

	private String getTopOrgs() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		String orgs = "";
		try {
			String sql = "SELECT CODEITEMID FROM ORGANIZATION WHERE PARENTID=CODEITEMID";
			rs = dao.search(sql);
			while(rs.next()) {
				orgs+=rs.getString("CODEITEMID")+",";
			}
			if(StringUtils.isNotBlank(orgs)) {
				orgs = orgs.substring(0,orgs.length());
			}
			return orgs;
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
	}

	@Override
    public String getPriveCode() {
		String return_code = "success";
		String return_msg = "";
		JSONObject jsonO = new JSONObject();
		try {
			String org_id = "";
			String b0110 = userView.getUnitIdByBusi("11");
			//超级用户或者默认最大业务范围  多个则取第一个
			if(userView.isSuper_admin() || "UN`".equals(b0110)) {
				String orgs = this.getTopOrgs();
				if(StringUtils.isNotBlank(orgs)) {
					org_id = orgs.split(",")[0];
				}
			}else {
				String[] units = ArrayUtils.EMPTY_STRING_ARRAY; 
				units = StringUtils.split(b0110, "`");
				if(units.length>0) {
					org_id = units[0].substring(2);
				}
			}
			String orgName = "";
			if(StringUtils.isNotBlank(org_id)) {
				String UNName = AdminCode.getCodeName("UN", org_id);
				String UMName = AdminCode.getCodeName("UM", org_id);
				orgName = StringUtils.isNotEmpty(UNName)?UNName:UMName;
				jsonO.put("org_id", org_id+"`"+orgName);
			}
		}catch(Exception e){
			e.printStackTrace();
			return_code = "fail";
			return_msg="";
		} finally {
			jsonO.put("return_code", return_code);
			jsonO.put("return_msg", return_msg);
		}
		return jsonO.toString();
	}

	@Override
    public ArrayList<LazyDynaBean> listKq_class(String sqlWhere,
                                                ArrayList parameterList, String sqlSort) throws GeneralException {
	  	ContentDAO dao = new ContentDAO(this.conn);
        ArrayList<LazyDynaBean> dataList = new ArrayList<LazyDynaBean>();
        RowSet rs = null;
        StringBuffer strSql = new StringBuffer();
        try {
            strSql.append("SELECT * FROM kq_class where 1=1 ");
            if (sqlWhere != null) {
                strSql.append(sqlWhere);
            }
            if (StringUtils.isNotBlank(sqlSort)) {
                strSql.append(" ORDER BY ").append(sqlSort);
            } else {
                strSql.append(" ORDER BY class_id ");
            }
            ArrayList pList = new ArrayList();
            if (parameterList != null) {
                pList.addAll(parameterList);
            }
            rs = dao.search(strSql.toString(), pList);
            while (rs.next()) {
            	 //查询结果 bean 中的key为全小写字段名
                 LazyDynaBean bean = new LazyDynaBean(); 
            	 bean.set("class_id", nullif(rs.getString("class_id")));
	    		 bean.set("onduty_1", nullif(rs.getString("onduty_1")));
	    		 bean.set("offduty_1", nullif(rs.getString("offduty_1")));
	    		 bean.set("onduty_2",nullif( rs.getString("onduty_2")));
	    		 bean.set("offduty_2", nullif(rs.getString("offduty_2")));
	    		 bean.set("onduty_3", nullif(rs.getString("onduty_3")));
	    		 bean.set("offduty_3", nullif(rs.getString("offduty_3")));
    			 bean.set("name", nullif(rs.getString("name")));
    			 bean.set("abbreviation", nullif(rs.getString("abbreviation")));
    			 bean.set("color", nullif(rs.getString("color")));
    			 bean.set("symbol", nullif(rs.getString("symbol")));
    			 bean.set("statistics_type", nullif(rs.getString("statistics_type")));
    			 bean.set("domain_count", rs.getInt("domain_count"));
    			 bean.set("work_hours", rs.getFloat("work_hours"));
    			 bean.set("org_id", nullif(rs.getString("org_id")));
    			 bean.set("remarks", nullif(rs.getString("remarks")));
    			 bean.set("is_validate", nullif(rs.getString("is_validate")));
    			 bean.set("seq", rs.getInt("seq"));
                 dataList.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new Exception("查询数据出错！"));
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return dataList;
	}
	
	private String nullif(String str) {
		if(str==null)
			return "";
		else
			return str;
	}
	
}
