package com.hjsj.hrms.module.jobtitle.committee.businessobject;

import com.hjsj.hrms.module.jobtitle.utils.JobtitleUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.util.Strings;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 资格评审_聘委会
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 */
public class CommitteeBo {
	
	// 基本属性
	private Connection conn = null;
	private UserView userview;
	
	/**
	 * 构造函数
	 * @param conn
	 * @param userview
	 */
	public CommitteeBo(Connection conn, UserView userview){
		this.conn = conn;
		this.userview = userview;
	}
	
	
	/**
	 * 显示权限范围内的聘委会
	 * @param b0110：所属单位
	 * @return 聘委会信息
	 */
	public ArrayList<HashMap<String, String>> getCommittee(String b0110) throws GeneralException {
		
		ArrayList<HashMap<String, String>> committeeList = new ArrayList<HashMap<String, String>>();
		
		ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	
    	try {
    		StringBuilder sql = new StringBuilder();
    		
    		sql.append("select committee_id,committee_name,type,b0110 ");
    		sql.append("From zc_committee ");
    		sql.append("where 1=1 ");
    		
    		if(b0110.split("`")[0].length() > 2){//组织机构去除UN、UM后不为空：取本级，本级，下级。为空：最高权限
    			String whereSql = new JobtitleUtil(this.conn, this.userview).getB0110Sql_down(b0110);
    			sql.append(whereSql);
    		}
    		sql.append("order by committee_id desc");
			rs = dao.search(sql.toString());
			
			while (rs.next()) {
				String committee_id = rs.getString("committee_id");
				committee_id = PubFunc.encrypt(committee_id);
				String committee_name = rs.getString("committee_name");
				String _b0110 = rs.getString("b0110");
				String b0110Name = "";
				b0110Name = !StringUtils.isEmpty(AdminCode.getCodeName("UN", _b0110))?AdminCode.getCodeName("UN", _b0110):AdminCode.getCodeName("UM", _b0110);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("committee_id", committee_id);
				map.put("committee_name", committee_name);
				map.put("b0110name", b0110Name);
				
				committeeList.add(map);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return committeeList;
	}
	
	/**
	 * 获取人员信息查询语句
	 * @param committee_id 聘委会编号
	 * @param isHistory 聘委会名称
	 * @return
	 * @throws GeneralException
	 */
	public String getCommitteePersonSql(ArrayList fieldList, String committee_id, String isHistory) throws GeneralException {
		
		StringBuilder sql =  new StringBuilder();//查询sql
		
		try {
			sql.append("select ");
			for(int i=0; i<fieldList.size(); i++){
				FieldItem item = (FieldItem)fieldList.get(i);		
				String itemid = item.getItemid();//字段id
				if("fixed_member".equalsIgnoreCase(itemid)){
					continue;
				}
				sql.append(" "+"w."+itemid+",");
			}
			sql = new StringBuilder(sql.substring(0, sql.length()-1));
			sql.append(" ,z.start_date, z.end_date, z.flag,z.role,z.committee_id,z.fixed_member,w.b0110 ");
			
			sql.append(" from w01 w,zc_judgingpanel_experts z ");
			sql.append(" where 1=1 ");
			sql.append(" and w.W0101=z.W0101 and z.committee_id='"+committee_id+"' ");
			if("0".equals(isHistory)){//不显示历史时,只显示任聘人员
				sql.append(" and flag=1 ");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return sql.toString();
	}
	
	/**
	 * 新增聘委会
	 * @param subjectsName:聘委会名称
	 * @return 
	 * @throws GeneralException
	 */
	public String createCommittee(String committee_name, String committee_type, String description, String b0110, String create_fullname, String create_time) throws GeneralException{
		
		String id = "";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			IDFactoryBean idf = new IDFactoryBean();
			RecordVo vo = new RecordVo("zc_committee");
			//聘委会编号、聘委会名称、所属单位、创建时间、创建者用户名、创建者姓名、修改时间、历史记录标识、有效标识
			id = idf.getId("zc_committee.committee_id", "", conn);
			vo.setString("committee_id", id);
			vo.setString("committee_name", committee_name);
			vo.setString("type", committee_type);
			vo.setInt("committee_number", 0);//不启用
			vo.setString("description", description);
			vo.setString("b0110", b0110);
			vo.setDate("create_time", create_time);
			vo.setString("create_user", this.userview.getUserName());
			vo.setString("create_fullname", create_fullname);
			vo.setDate("modify_time", new java.sql.Date(new java.util.Date().getTime()));
			dao.addValueObject(vo);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
		
		return id;
	}
	/**
	 * 修改聘委会名称
	 * @param group_id：聘委会编号
	 * @param list:可变长的HashMap（key:目标字段  value：值）
	 * @return
	 * @throws GeneralException
	 */
	public String modifyCommittee(String committee_id, ArrayList<HashMap<String, String>> list) throws GeneralException{
		
		String msg = "";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			ArrayList<String> sqllist = new ArrayList<String>();
			StringBuilder sql = new StringBuilder();
			sql.append("update zc_committee ");
			if(list.size() > 0){
				sql.append("set ");
			}
			for(int i=0; i<list.size(); i++){
				sql.append(list.get(i).get("key")+"=? ");
				sqllist.add(list.get(i).get("value"));
				if(i != list.size()-1){
					sql.append(", ");
				}
			}
    		sql.append("where committee_id=? ");
    		sqllist.add(committee_id);
			dao.update(sql.toString(), sqllist);
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return msg;
	}
	
	/**
	 * 删除聘委会
	 * @param group_id：聘委会编号
	 * @return msg
	 * @throws GeneralException
	 */
	public String deleteCommittee(String committee_id) throws GeneralException{
			
		String msg = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		
		try {
			StringBuilder sql = new StringBuilder();
			ArrayList<String> list = new ArrayList<String>();
			
			// 查看该聘委会是否关联会议
			sql.append("select COUNT(W0301) as count ");
			sql.append("From w03 ");
			sql.append("where committee_id=? ");
			list.add(committee_id);
			rs = dao.search(sql.toString(), list);
			while(rs.next()){
				if(rs.getInt("count") > 0){//存在关联
					msg = "该"+JobtitleUtil.ZC_MENU_COMMITTEESHOWTEXT+"已关联会议，不能删除！";
					return msg;
				} else {
					sql.setLength(0);
					list.clear();
					sql.append("delete from zc_committee ");
		    		sql.append("where committee_id=? ");
		    		list.add(committee_id);
					dao.delete(sql.toString(), list);
					//msg = "已把该聘委会置为不启用状态";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeDbObj(rs);
		}
		
		return msg;
	}
	/**
	 * 新增聘委会组内成员
	 * @param group_id：聘委会编号
	 * @param personidList：专家编号
	 * @return msg
	 * @throws GeneralException
	 */
	public String createCommitteePerson(String committee_id, ArrayList<String> personidList) throws GeneralException{
		
		String msg = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			for(int i=0; i<personidList.size(); i++){
				String expertid = personidList.get(i);
				expertid = PubFunc.decrypt(expertid);
				StringBuilder sql = new StringBuilder();
				ArrayList<String> list = new ArrayList<String>();
				// 需要新增的专家是否已经存在
				sql.append("select count(committee_id) as count ");
				sql.append("From zc_judgingpanel_experts ");
				sql.append("where committee_id=? ");
				sql.append("and W0101=? ");
				list.add(committee_id);
				list.add(expertid);
				rs = dao.search(sql.toString(), list);
				while(rs.next()){
					if(rs.getInt("count") > 0){//存在该专家
						sql.setLength(0);
						list.clear();
						sql.append("update zc_judgingpanel_experts ");
						sql.append("set flag='1' ");
						sql.append("where committee_id=? ");
						sql.append("and W0101=? ");
						list.add(committee_id);
						list.add(expertid);
						dao.update(sql.toString(), list);
					}else {
						RecordVo vo = new RecordVo("zc_judgingpanel_experts");
						//聘委会编号、专家编号、聘任标识
						vo.setString("committee_id", committee_id);
						vo.setString("w0101", expertid);
						vo.setDate("start_date", new java.sql.Date(new java.util.Date().getTime()));
						vo.setString("flag", "1");
						dao.addValueObject(vo);
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeDbObj(rs);
		}
		
		return msg;
	}
	/**
	 * 删除聘委会组内成员
	 * @param group_id：聘委会编号
	 * @param personidList：专家编号
	 * @return msg
	 * @throws GeneralException
	 */
	public String deleteCommitteePerson(String cid, ArrayList<String> personidList) throws GeneralException{
		
		String msg = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		
		try {
			boolean delFlag = true;
			String inStr = "";
			List expertidList = new ArrayList();
			int cursor = 0;
			StringBuffer noDelPerson = new StringBuffer();
			for(int i=0; i<personidList.size(); i++){
				String expertid = personidList.get(i);
				expertid = PubFunc.decrypt(expertid);
				StringBuilder sql = new StringBuilder();
				ArrayList<String> list = new ArrayList<String>();
				// 查看该专家是否关联会议
				sql.append("select w0107 ");
				sql.append("from zc_expert_user zc,w01 ");
				sql.append("where w0301 in(  ");
				sql.append("select distinct(w0301) from w03 where zc.w0101=? ");
				sql.append("and zc.w0101 = w01.w0101 ");
				sql.append(" and (sub_committee_id=? and type=4 or committee_id=? and type=1) )");
				list.add(expertid);
				list.add(cid);
				list.add(cid);
				rs = dao.search(sql.toString(), list);
				if(rs.next()){{//存在关联
						delFlag = false;
						if(cursor<2)
							noDelPerson.append(rs.getString("w0107")+",");
						cursor++;
					}
				}
				 else {
						inStr+="?,";
						expertidList.add(expertid);
					}
				//删除专家
				if(delFlag && expertidList.size()>0 && inStr.length()>0){
					sql.setLength(0);
					list.clear();
					sql.append("delete from zc_judgingpanel_experts ");
					sql.append("where committee_id=? ");
					sql.append("and W0101 in ( ");
					sql.append(inStr.substring(0, inStr.length()-1)+")");
					list.add(cid);
					list.addAll(expertidList);
					dao.delete(sql.toString(), list);
					msg = "撤销成功 ！";
				}
			}
			if(!delFlag){
				return "【"+noDelPerson.substring(0,noDelPerson.length()-1)+"】"+(cursor>2?"等"+cursor+"名专家":"")+"已关联评审会议，不允许撤销！";
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeDbObj(rs);
		}
		
		return msg;
	}
	
	/**
	 * 获取更新用列表
	 * @param committee_name 聘委会名称
	 * @param committee_type 聘委会类型
	 * @param description 描述
	 * @param b0110 所属单位
	 * @return
	 */
	public ArrayList<HashMap<String, String>> getModifyList(String committee_name, String type, String description, String b0110){
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("key", "committee_name");//聘委会名称
		map.put("value", committee_name);
		list.add(map);
		
		HashMap<String, String> map2 = new HashMap<String, String>();
		map2.put("key", "type");//聘委会类型
		map2.put("value", type);
		list.add(map2);
		
		HashMap<String, String> map3 = new HashMap<String, String>();
		map3.put("key", "description");//描述
		map3.put("value", description);
		list.add(map3);
		
		HashMap<String, String> map4 = new HashMap<String, String>();
		map4.put("key", "b0110");//所属单位
		map4.put("value", b0110);
		list.add(map4);
		
		return list;
	}
    /**
     * 获取列头，表格渲染
     * @param fieldList：数据字典列表
     * @return
     */
    public ArrayList<ColumnsInfo> getColumnList(ArrayList fieldList){
    	ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();
    	try{
	    	//取得数据字典中设置的w01的构库的所有字段
			for(int i=0; i<fieldList.size(); i++){
				FieldItem item = (FieldItem)fieldList.get(i);
				
				String itemid = item.getItemid();//字段id
				String itemtype = item.getItemtype();//字段类型
				String codesetid = item.getCodesetid();//关联的代码			
				String columndesc = item.getItemdesc();//字段描述
				int itemlength = item.getItemlength();//字段长度
				String state = item.getState();//0隐藏  1显示
				String fieldsetid = item.getFieldsetid();
				
				if("fixed_member".equalsIgnoreCase(itemid)){
					continue;
				}
				
				//列类型特殊处理
				//if("w0103".equals(itemid) || "w0105".equals(itemid) || "w0107".equals(itemid)){//注释掉，虽然在评委会表中是文本型，也不能修改成M。如果改成M，则在复杂查询中的备选项就不会出来了。chent
					//itemtype = "M";
				//}
				
				ColumnsInfo columnsInfo = getColumnsInfo(itemid, columndesc, 100, itemtype, fieldsetid);
				if("A".equals(itemtype)){//A:字符型  D:日期型 N:数值型  M:备注型
					if("0".equals(codesetid) || codesetid == null){//非代码字符型
						//获得字段描述
						if("w0101".equals(itemid)){
							columnsInfo.setColumnLength(itemlength);
							columnsInfo.setCodesetId("0");
							columnsInfo.setEncrypted(true);
							columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
						}else{
							columnsInfo.setColumnLength(itemlength);
							columnsInfo.setCodesetId("0");
							if("0".equals(state)){
								continue;
								//columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD );
							}
						}
					}else{//代码型字符
						columnsInfo.setColumnLength(itemlength);
						columnsInfo.setCodesetId(codesetid);
						if("0".equals(state)){
							continue;
							//columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD  );
						}
					}
				} else if("D".equals(itemtype)||"N".equals(itemtype)||"M".equals(itemtype)){//日期型。数值。备注
					columnsInfo.setColumnLength(itemlength);
					columnsInfo.setCodesetId("0");
					if("0".equals(state)){
						continue;
						//columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD  );
					}
				}
				if("w0103".equals(itemid) || "w0105".equals(itemid) || "w0107".equals(itemid)){
					columnsInfo.setLocked(true);
				}
				if("w0111".equalsIgnoreCase(itemid)){
					columnsInfo.setEditableValidFunc("false");
					columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
				}else
					columnsInfo.setEditableValidFunc("committee_me.w01EditableValid");
				if("w0109".equals(itemid)){
					continue;
					//columnsInfo.setColumnDesc("聘任标识(专家库)");
					//columnsInfo.setColumnWidth(120);
				}
				columnTmp.add(columnsInfo);
			}
			
			ColumnsInfo columnsInfo = getColumnsInfo("fixed_member", "固定成员", 100, "A", "");
			columnsInfo.setCodesetId("45");
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
			columnTmp.add(columnsInfo);
			// 组长
			if(this.userview.hasTheFunction("380020211")) {
				columnsInfo = getColumnsInfo("role", "组长", 100, "A", "");
				columnsInfo.setRendererFunc("committee_me.setRole");
				columnsInfo.setEditableValidFunc("false");
				columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
				columnsInfo.setOperationData("leader");
				columnTmp.add(columnsInfo);
			}
			columnsInfo = getColumnsInfo("flag", "聘任标识", 100, "A", "");
			columnsInfo.setCodesetId("45");
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
			columnTmp.add(columnsInfo);
			
			columnsInfo = getColumnsInfo("start_date", "起始日期", 100, "D", "");
			columnsInfo.setColumnLength(10);
			columnsInfo.setRendererFunc("committee_me.dateFormat");
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
			columnTmp.add(columnsInfo);
			
			columnsInfo = getColumnsInfo("end_date", "终止日期", 100, "D", "");
			columnsInfo.setColumnLength(10);
			columnsInfo.setRendererFunc("committee_me.dateFormat");
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
			columnTmp.add(columnsInfo);
			
			columnsInfo = getColumnsInfo("b0110", "所属机构", 100, "A", "");
			columnsInfo.setEditableValidFunc("committee_me.b0110EditableValid");
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
			columnsInfo.setCodesetId("UM");
			columnsInfo.setCtrltype("3");
			columnsInfo.setNmodule("9");
			columnsInfo.setCodeSetValid(false);
			columnTmp.add(columnsInfo);
			
			/** 隐藏 */
			// 编号
			columnsInfo = new ColumnsInfo();
			columnsInfo.setColumnId("committee_id");
			columnsInfo.setColumnDesc("committee_id");
			columnsInfo.setEncrypted(true);
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(columnsInfo);
    	} catch (Exception e) {
            e.printStackTrace();
            GeneralExceptionHandler.Handle(e);
        }
    	return columnTmp;
    }
    
    /**
     * 初始化控件列对象
     * @param columnId
     * @param columnDesc：名称
     * @param columnWidth：显示列宽
     * @param type：类型
     * @return
     */
    private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth, String type, String fieldsetid) {

        ColumnsInfo columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId(columnId);
        columnsInfo.setColumnDesc(columnDesc);
        //columnsInfo.setCodesetId("");// 指标集
        columnsInfo.setColumnType(type);// 类型N|M|A|D
        columnsInfo.setColumnWidth(columnWidth);// 显示列宽
        if ("A".equals(type)) {
            columnsInfo.setCodesetId("0");
        }
        columnsInfo.setDecimalWidth(0);// 小数位
        columnsInfo.setFieldsetid(fieldsetid);
        	
        // 数值和日期默认居右
        if ("D".equals(type) || "N".equals(type))
            columnsInfo.setTextAlign("right");

        return columnsInfo;
    }
    /**
	 * 获取功能按钮
	 * @return
	 */
	public ArrayList<Object> getButtonList(){
		
		ArrayList<Object> buttonList = new ArrayList<Object>();
		try{
			ButtonInfo buttonInfo = new ButtonInfo();
//			if (this.userview.hasTheFunction("380020201")) {
//				buttonInfo = new ButtonInfo("导出", ButtonInfo.FNTYPE_EXPORT, "");
//				buttonInfo.setId("committee_export");
//				buttonList.add(buttonInfo);
//			}
			if (this.userview.hasTheFunction("380020212")) {
				buttonInfo = new ButtonInfo("新增外部专家", "committee_me.addExpert");
				buttonInfo.setId("committee_newOutPerson");
				buttonList.add(buttonInfo);
			}
			if (this.userview.hasTheFunction("380020203")) {
				buttonInfo = new ButtonInfo("引入专家", "committee_me.openExpertPicker");
				buttonInfo.setId("committee_newPerson");
				buttonList.add(buttonInfo);
			}

			if (this.userview.hasTheFunction("380020204")) {
				buttonInfo = new ButtonInfo("专家抽取", "committee_me.randomSelectionr");
				buttonInfo.setId("committee_randomChoose");
				buttonList.add(buttonInfo);
			}
			
			if (this.userview.hasTheFunction("380020205")) {
				buttonInfo = new ButtonInfo("撤销", "committee_me.deletePerson");
				buttonInfo.setId("committee_deletePerson");
				buttonList.add(buttonInfo);
			}
			
			if (this.userview.hasTheFunction("380020206")) {
				buttonInfo = new ButtonInfo(ResourceFactory.getProperty("button.save"),ButtonInfo.FNTYPE_SAVE, "ZC00002108");
				buttonInfo.setId("committee_save");
				buttonList.add(buttonInfo);
			}
			
			ButtonInfo queryBox = new ButtonInfo();
			queryBox.setType(ButtonInfo.TYPE_QUERYBOX);
			queryBox.setText("请输入单位名称、部门、姓名");
			queryBox.setFunctionId("ZC00002109");
			buttonList.add(queryBox);
			
//			// 加搜索条
//	        buttonList.add(new ButtonInfo("<div id='fastsearch11'> </div>"));
//	        // 显示历史
//	        buttonList.add("->");
//	        buttonList.add(new ButtonInfo("<div id='committee_history_checkBox'></div>"));
//	        buttonList.add(new ButtonInfo("<div id='committee_history_label'></div>"));
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return buttonList;
	}
	/**
	 * 获取聘委会信息 编辑、新建时用
	 * @param type：1新建 2编辑
	 * @param committee_id ：聘委会编号
	 * @return
	 * @throws GeneralException
	 */
	public HashMap<String, String> getCommitteeInfo(String type, String committee_id) throws GeneralException {
    	
		HashMap<String, String> map = new HashMap<String, String>();
		
		
		if("1".equals(type)){//新建时
			String data = DateUtils.format(new java.sql.Date(new java.util.Date().getTime()),"yyyy-MM-dd");
			//String b0110 = this.userview.getUserDeptId();//取得所属单位
			String b0110 = this.userview.getUnitIdByBusi("9");//取得所属单位
			if(b0110.split("`")[0].length() > 2){//组织机构去除UN、UM后不为空：取本级，本级，下级。为空：最高权限
				b0110 = b0110.split("`")[0].substring(2);
    		}
			String unitName = "";
			unitName = !StringUtils.isEmpty(AdminCode.getCodeName("UN", b0110))?AdminCode.getCodeName("UN", b0110):AdminCode.getCodeName("UM", b0110);
			map.put("b0110", b0110+"`"+unitName);
			map.put("create_time", data);
			map.put("create_fullname", StringUtils.isEmpty(this.userview.getUserFullName()) ? this.userview.getUserName() : this.userview.getUserFullName());
			return map;
		}
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
		try {
			StringBuilder sql = new StringBuilder(); 
	    	sql.append("select committee_name,type,description,b0110,create_time,create_fullname ");
	    	sql.append(" from zc_committee ");
	    	sql.append(" where  committee_id=?");
	    	
	    	ArrayList<String> list = new ArrayList<String>();
	    	list.add(committee_id);
			rs = dao.search(sql.toString(), list);
			
			while (rs.next()) {
				map.put("comsubName", rs.getString("committee_name"));
				map.put("type1", rs.getString("type"));
				map.put("description", rs.getString("description"));
				String unitName = "";
				unitName = !StringUtils.isEmpty(AdminCode.getCodeName("UN", rs.getString("b0110")))?AdminCode.getCodeName("UN", rs.getString("b0110")):AdminCode.getCodeName("UM", rs.getString("b0110"));
				map.put("select_b0110", rs.getString("b0110")+"`"+unitName);
				map.put("create_fullname", rs.getString("create_fullname"));
				map.put("create_time", String.valueOf(rs.getDate("create_time")));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return map;
    }
	/**
	 * 获取全部人员
	 * @param sql :控件sql
	 * @param personidList 需要排出的人员列表
	 * @return 所有人列表
	 * @throws GeneralException
	 */
	public ArrayList<String> getAllPerson(String sql, ArrayList<String> personidList) throws GeneralException {
		
		ArrayList<String> personList = new ArrayList<String>();
		ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	
    	try {
    		StringBuilder newsql = new StringBuilder(sql);
    		if(personidList.size() != 0){
    			newsql.append(" and w.W0101 not in ( ");
    		}
    		for(int i=0; i<personidList.size(); i++){
				String expertid = personidList.get(i);
				expertid = PubFunc.decrypt(expertid);
				if(i != personidList.size()-1){
					newsql.append("'" + expertid + "', ");
				}else{
					newsql.append("'" + expertid + "' ");
				}
    		}
    		if(personidList.size() != 0){
    			newsql.append(" ) ");
    		}
    		rs = dao.search(newsql.toString());
    		while(rs.next()){
    			String w0101 = rs.getString("W0101");
    			w0101 = PubFunc.encrypt(w0101);
    			personList.add(w0101);
    		}
    		
    	}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeDbObj(rs);
		}
		
		
		
		return personList;
	}
	/**
	 * 获取列头   导出excel用
	 * @param columnList
	 * @return
	 */
	public ArrayList<LazyDynaBean> getExpHeadList(ArrayList<ColumnsInfo> columnList, String exceptStr){
		
		/** 获取类型名称 */
		ArrayList<LazyDynaBean> columnTmp = new ArrayList<LazyDynaBean>();
		
		for (ColumnsInfo info : columnList) {
			String columnId = info.getColumnId();
			if (exceptStr.indexOf("," + Strings.toLowerCase(columnId) + ",") != -1) {
				continue;
			}
			String columnDesc = info.getColumnDesc();
			String columnType = info.getColumnType();// 类型N|M|A|D
			int columnWidth = info.getColumnWidth();// 显示列宽
			String codesetId = info.getCodesetId();
			String decimalWidth = String.valueOf(info.getDecimalWidth());// 小数位
			
				
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("content", columnDesc);// 列头名称
			bean.set("itemid", columnId);// 列头代码
			bean.set("codesetid", codesetId);// 列头代码
			bean.set("decwidth", decimalWidth);// 列小数点后面位数
			bean.set("colType", columnType);// 该列数据类型
			columnTmp.add(bean);
		}
		return columnTmp;
	}
	/**
     * 获取列头，表格渲染
     * @return
     */
    public ArrayList<ColumnsInfo> getColumnListRandom(){
    	ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();
    	try{
	    	//取得数据字典中设置的w01的构库的所有字段
	    	ArrayList fieldList = DataDictionary.getFieldList("w01",Constant.USED_FIELD_SET);
			for(int i=0; i<fieldList.size(); i++){
				FieldItem item = (FieldItem)fieldList.get(i);		
				String itemid = item.getItemid();//字段id
				String itemtype = item.getItemtype();//字段类型
				String codesetid = item.getCodesetid();//关联的代码			
				String columndesc = item.getItemdesc();//字段描述
				int itemlength = item.getItemlength();//字段长度
				String state = item.getState();//0隐藏  1显示
				String fieldsetid = item.getFieldsetid();
				//列类型特殊处理
				if("w0103".equals(itemid) || "w0105".equals(itemid) || "w0107".equals(itemid)){
					itemtype = "M";
				}
				
				ColumnsInfo columnsInfo = getColumnsInfo(itemid, columndesc, 100, itemtype, fieldsetid);
				if("A".equals(itemtype)){//A:字符型  D:日期型 N:数值型  M:备注型
					if("0".equals(codesetid) || codesetid == null){//非代码字符型
						//获得字段描述
						if("w0101".equals(itemid)){
							columnsInfo.setColumnLength(itemlength);
							columnsInfo.setCodesetId("0");
							columnsInfo.setEncrypted(true);
							columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
						}else{
							columnsInfo.setColumnLength(itemlength);
							columnsInfo.setCodesetId("0");
							if("0".equals(state)){
								columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
							}
						}
					}else{//代码型字符
						columnsInfo.setColumnLength(itemlength);
						columnsInfo.setCodesetId(codesetid);
						if("0".equals(state)){
							columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
						}
					}
				} else if("D".equals(itemtype)||"N".equals(itemtype)||"M".equals(itemtype)){//日期型。数值。备注
					columnsInfo.setColumnLength(itemlength);
					columnsInfo.setCodesetId("0");
					if("0".equals(state)){
						columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
					}
				}
				if("w0103".equals(itemid) || "w0105".equals(itemid) || "w0107".equals(itemid)){
					columnsInfo.setLocked(true);
				}
				columnTmp.add(columnsInfo);
			}
			
			ColumnsInfo columnsInfo = new ColumnsInfo();
			columnsInfo.setColumnId("schemeid");
			columnsInfo.setColumnDesc("归属方案id");
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(columnsInfo);

			ColumnsInfo seq = new ColumnsInfo();
			seq.setColumnId("seq");
			seq.setColumnDesc("序号");
			seq.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(seq);
    	} catch (Exception e) {
            e.printStackTrace();
            GeneralExceptionHandler.Handle(e);
        }
    	return columnTmp;
    }
	/**
	 * 获得需要查询的sql字段
	 * @param committeeId：评委会编号，为了排除已有的人员
	 * @param moduleType：模块区分  1：评委会 2：学科组
	 * @return
	 */
	public String getSelectSqlRandom(String committeeId ,String moduleType) {
		StringBuilder sql =  new StringBuilder();//查询sql
		
		String notInSql = "";
		if("1".equals(moduleType)){
			notInSql = "select zc_judgingpanel_experts.w0101 from zc_judgingpanel_experts where committee_id='"+committeeId+"'";
		} else if ("2".equals(moduleType)){
			notInSql = "select expertid as w0101 from zc_subjectgroup_experts where group_id='"+committeeId+"'";
		}
		
		sql.append("select distinct ");
		ArrayList fieldList = DataDictionary.getFieldList("w01",Constant.USED_FIELD_SET);
		for(int i=0; i<fieldList.size(); i++){
			FieldItem item = (FieldItem)fieldList.get(i);		
			String itemid = item.getItemid();//字段id
			if(i != fieldList.size()-1){
				sql.append(" w01."+itemid+",");
			}else {
				sql.append(" w01."+itemid+" ");
			}
		}
		if("1".equals(moduleType)){
			sql.append(" from w01 left join zc_judgingpanel_experts on w01.W0101 = zc_judgingpanel_experts.w0101 and zc_judgingpanel_experts.committee_id='"+committeeId+"' where 1=1 and W0109=1 and w01.w0101 not in ("+notInSql+") ");
		} else if ("2".equals(moduleType)){
			sql.append(" from w01 left join zc_judgingpanel_experts on w01.W0101 = zc_judgingpanel_experts.w0101 where 1=1 and W0109=1 and w01.w0101 not in ("+notInSql+") ");
			
		}
		
		String b0110 = this.userview.getUnitIdByBusi("9");//取得所属单位
		if(b0110.split("`")[0].length() > 2){//组织机构不为空：取上级、本级，下级。为空：最高权限
			
			JobtitleUtil jobtitleUtil = new JobtitleUtil(this.conn, this.userview);// 工具类
			sql.append(jobtitleUtil.getB0110Sql_upToDown(b0110));
		}
		
		return sql.toString();
	}
	
	/**
	 * 获取随机选择页面中查询方案
	 * @param subModuleId:唯一标识
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList<HashMap<String, String>> getRandomScheme(String subModuleId) throws GeneralException {
		
		ArrayList<HashMap<String, String>> schemeList = new ArrayList<HashMap<String, String>>();
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("key", "0");
		map.put("value", "专家库");
		schemeList.add(map);
		
		ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	
    	try {
    		StringBuilder sql = new StringBuilder();
    		
    		sql.append("select Query_plan_id,plan_name,conditem ");
    		sql.append(" From t_sys_table_query_plan ");
    		sql.append(" where ");
    		sql.append(" submoduleid='"+subModuleId+"' ");
    		
    		rs = dao.search(sql.toString());
    		while(rs.next()){
    			String Query_plan_id = rs.getString("Query_plan_id");
    			String plan_name = rs.getString("plan_name");
    			map = new HashMap<String, String>();
    			map.put("key", Query_plan_id);
    			map.put("value", plan_name);
    			schemeList.add(map);
    		}
    		
    	}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeDbObj(rs);
		}
		
		return schemeList ;
	}
	
	/**
	 * 随机获取专家
	 * @param type：1内部专家  2外部专家
	 * @param insideNum ：人数
	 * @return
	 */
	public ArrayList<String> getPersonForRandomSelection(String type, int num) throws GeneralException {
		
		ArrayList<String> list = new ArrayList<String>(); 
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			TableDataConfigCache catche = (TableDataConfigCache) this.userview.getHm().get("random_selection_00001");
			String beforeSql = "";//自定义的前缀sql
			String tableSql = catche.getTableSql();//控件tableSql
			String querySql = catche.getQuerySql();//控件QuerySql
			String sortSql = "";//控件sortSql
			String afterSql = "";//自定义的后缀sql
			
			int db_type = Sql_switcher.searchDbServer();//数据库类型
			if(db_type == 1){//sql server
				beforeSql = "  select top "+num+" w0101 from ( ";
				sortSql = "";
				if("1".equals(type)) {//内部专家
					afterSql = " and w0111=2) s  order by NEWID() ";
				}else if("2".equals(type)){//外部专家
					afterSql = " and w0111=1) s  order by NEWID() ";
				}
			}else if(db_type == 2){//oracle
				beforeSql = " select w0101 from ( ";
				if("1".equals(type)) {//内部专家
					sortSql = " and w0111=2 order by dbms_random.value ";
				} else if("2".equals(type)){//外部专家
					sortSql = " and w0111=1 order by dbms_random.value ";
				}
				num++;
				afterSql = " ) where rownum < "+num+" ";
			}
			
			StringBuilder sql = new StringBuilder();
			sql.append(beforeSql);
			sql.append(tableSql);
			sql.append(querySql);
			sql.append(sortSql);
			sql.append(afterSql);
	
			rs = dao.search(sql.toString());
			while (rs.next()) {
				String w0101 = rs.getString("w0101");
				w0101 = PubFunc.encrypt(w0101);
				list.add(w0101);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeDbObj(rs);
		}
		
		return list;
	}
	
	/**
	 * 会议是否启用学院聘任组
	 * @param w0301：会议编号
	 * @return
	 */
	public boolean isCollegeGrounp(String w0301){
		boolean flg = false;
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String sql = "select w0323 from w03 where w0301=?";
			ArrayList<String> list = new ArrayList<String>();
			list.add(w0301);
			rs = dao.search(sql, list);
			while (rs.next()) {
				int w0323 = rs.getInt("w0323");
				if(w0323 == 1){//启用
					flg = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			PubFunc.closeDbObj(rs);
		}
		
		return flg;
	}

	/**
	 * 设置评委会组长
	 * @param committee_id
	 * @param personidList
	 * @param role
	 * @author haosl
	 * @return
	 * @throws GeneralException 
	 */
	public String updateCommitteePerson(String committee_id, ArrayList<String> personidList, String role) throws GeneralException {
		String msg = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			ArrayList list = new ArrayList();
			if(personidList.size()>0){
				String expertid = personidList.get(0);
				expertid = PubFunc.decrypt(expertid);
				//将所有专家置为组员
				StringBuilder sql = new StringBuilder();
				sql.append("update zc_judgingpanel_experts");
				sql.append(" set role='0'");
				sql.append(" where committee_id=?");
				list.add(committee_id);
				dao.update(sql.toString(), list);
				
				//将指定专家置为组长
				if("1".equals(role)){
					list.clear();
					sql.setLength(0);
					sql.append("update zc_judgingpanel_experts");
					sql.append(" set role=?");
					sql.append(" where committee_id=?");
					sql.append(" and w0101=?");
					list.add(role);
					list.add(committee_id);
					list.add(expertid);
					dao.update(sql.toString(), list);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = "设置组长失败！";
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeDbObj(rs);
		}
		return msg;
	}

}
