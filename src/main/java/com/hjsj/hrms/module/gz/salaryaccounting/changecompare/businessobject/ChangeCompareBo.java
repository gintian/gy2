package com.hjsj.hrms.module.gz.salaryaccounting.changecompare.businessobject;

import com.hjsj.hrms.businessobject.gz.GzContant;
import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hjsj.hrms.businessobject.gz.SalaryTotalBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.bouncycastle.util.Strings;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * 薪资管理_薪资发放_变动比对 工具类
 * @createtime July 02, 2015 9:07:55 PM
 * @author chent
 */
public class ChangeCompareBo implements GzContant {
	
	/** 新增人员 */
	public final String ADD_PERSON = "0";
	/** 减少人员 */
	public final String REDUCE_PERSON = "1";
	/** 信息变动人员 */
	public final String CHANGE_PERSON = "2";
	/** 停发人员 */
	public final String STOP_PERSON = "3";
	/** 人员库 */
	private final static String  DBASE = ResourceFactory.getProperty("gz_new.gz_nbase");
	
	// 基本属性
	private Connection conn = null;
	private UserView userview;
	
	// 薪资项目列表,GzItemVo类对象
	private ArrayList gzitemlist=new ArrayList();
	
	/**
	 * 构造函数
	 * @param conn
	 * @param userview
	 */
	public ChangeCompareBo(Connection conn, UserView userview){
		this.conn = conn;
		this.userview = userview;
	}
	
	/**
	 * 判断薪资类别属性中设置的人员范围是否有效
	 * 
	 * @param salaryid
	 */
	public void checkPersonScope(String salaryid) {
		try {
			/** 获取<<薪资类别表>>中的人员范围 */
			ContentDAO dao = new ContentDAO(this.conn);
			RecordVo recordVo = new RecordVo("salarytemplate");
			recordVo.setInt("salaryid", Integer.parseInt(salaryid));
			recordVo = dao.findByPrimaryKey(recordVo);
			String dbpres = recordVo.getString("cbase");

			/** 应用库前缀 */
			DbWizard dbw = new DbWizard(this.conn);
			String[] dbarr = StringUtils.split(dbpres, ",");
			String newDbpres = "";
			boolean flag = false;
			for (int i = 0; i < dbarr.length; i++) {
				String pre = dbarr[i];
				if (dbw.isExistTable(pre + "a01", false)) {
					newDbpres += pre + ",";
				} else {
					flag = true;
				}
			}
			if (!newDbpres.equalsIgnoreCase(dbpres) && flag) {
				recordVo.setString("cbase", newDbpres);
				dbw.execute("update salarytemplate set cbase='" + newDbpres
						+ "' where salaryid=" + salaryid);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	/**
	 * 获取列头   新增人员、减少人员、停发人员
	 * @param viewtype 0:新增人员  1:减少人员 3:停发人员
	 * @param tableName <<新增人员临时表>>
	 * @param exceptStr 排除字段
	 * @return
	 */
	public ArrayList<ColumnsInfo> getColumnList(String viewtype, String tableName, String exceptStr){
		
		/** 获取类型名称 */
		ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();
		
		/** 新增人员、减少人员、停发人员 */
		if (ADD_PERSON.equals(viewtype) || REDUCE_PERSON.equals(viewtype) || STOP_PERSON.equals(viewtype)){
			
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			try {
				String sql = "select * from " + tableName + " where 1=2";
				rs = dao.search(sql);
				ResultSetMetaData metaData = rs.getMetaData();
				// 列宽
				int width = 180;
				if(metaData.getColumnCount() > 10){
					width = 120;
				}
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					String columnItem = metaData.getColumnName(i).toLowerCase();
					if (exceptStr.indexOf("," + Strings.toLowerCase(columnItem) + ",") != -1) {
						continue;
					}
					if("dbname".equalsIgnoreCase(columnItem)){
						ColumnsInfo dBase = getColumnsInfo(columnItem, DBASE, width, "0", "M", 0, 0);
						dBase.setRendererFunc("change_me.dbname");
						dBase.setKey(true);
						columnTmp.add(dBase);
						continue;
					}
					String columnName = "";
					String codesetId = "";
					String columnType = "";
					int columnLength = 0;
					int decimalWidth = 0;
					if(DataDictionary.getFieldItem(columnItem) != null){
						columnName = DataDictionary.getFieldItem(columnItem).getItemdesc();
						codesetId = DataDictionary.getFieldItem(columnItem).getCodesetid();
						columnType = DataDictionary.getFieldItem(columnItem).getItemtype();
						columnLength = DataDictionary.getFieldItem(columnItem).getDisplaywidth();// 显示长度 
						decimalWidth = DataDictionary.getFieldItem(columnItem).getDecimalwidth();// 小数位
						columnTmp.add(getColumnsInfo(columnItem, columnName, width, codesetId, columnType, columnLength, decimalWidth));
						continue;
					}else{
						//columnTmp.add(getColumnsInfo(columnItem, columnName, width, codesetId, columnType, columnLength, decimalWidth));
						continue;
					}
				}
				/** 隐藏 */
				// 员工号
				ColumnsInfo id = new ColumnsInfo();
				id.setColumnId("A0100");
				id.setColumnDesc(ResourceFactory.getProperty("gz_new.gz_accounting.employeeid"));
				id.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
				id.setKey(true);
				columnTmp.add(id);
				// 表区分 add/reduce/info/stop
				ColumnsInfo tabletype = new ColumnsInfo();
				tabletype.setColumnId("tabletype");
				tabletype.setColumnDesc(ResourceFactory.getProperty("gz_new.gz_accounting.tabletype"));
				tabletype.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
				columnTmp.add(tabletype);
				// 状态
				ColumnsInfo state = new ColumnsInfo();
				state.setColumnId("state");
				state.setColumnDesc(ResourceFactory.getProperty("gz_new.gz_accounting.status"));
				state.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
				columnTmp.add(state);
			} catch (Exception ex) {
				ex.printStackTrace();
			}finally{
				PubFunc.closeDbObj(rs);
			}
		}
		
		return columnTmp;
	}
	/**
	 * 
	 * @Title: getColumn   
	 * @Description:   获取列头 
	 * @param @param viewtype  0:新增人员  1:减少人员 3:停发人员
	 * @param @param tableName 临时表
	 * @param @param exceptStr 排除的字段
	 * @param @return 
	 * @return HashMap 
	 * @author:zhaoxg   
	 * @throws
	 */
	public HashMap getColumn(String viewtype, String tableName, String exceptStr){
		
		HashMap map = new HashMap();
		StringBuffer fields = new StringBuffer();//显示字段
		fields.append("[");
		StringBuffer str = new StringBuffer();//column
		str.append("[");
		str.append("{");
		str.append("xtype: 'templatecolumn',");
		str.append("text:'<input name="+viewtype+" type=checkbox id=selall onclick=\"change_me.updateState(this.id,this.checked,this.name);\" checked />',");//默认进来全选，所以这写死选中
		str.append("menuDisabled:true,");
		str.append("width:35,");
		str.append("align:'center',");
		str.append("hideable:false,");
		str.append("tpl:'<input name={TABLETYPE} type=checkbox id={DBNAME1}{A0100} class=\"options\" onclick=\"change_me.updateState(this.id,this.checked,this.name);\" {STATE} />' ");//读取state的状态 取值的时候把1和0分别转化为checked和空 用来确定是否勾选
		str.append("}");
		fields.append("'state'");
		/** 新增人员、减少人员、停发人员 */
		if (ADD_PERSON.equals(viewtype) || REDUCE_PERSON.equals(viewtype) || STOP_PERSON.equals(viewtype)){
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			try {
				String sql = "select * from " + tableName + " where 1=2";
				rs = dao.search(sql);
				ResultSetMetaData metaData = rs.getMetaData();
				// 列宽
				int width = 180;
				if(metaData.getColumnCount() > 10){
					width = 120;
				}
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					String columnItem = metaData.getColumnName(i).toUpperCase();
					if (exceptStr.indexOf("," + Strings.toLowerCase(columnItem) + ",") != -1) {
						continue;
					}
					if("dbname".equalsIgnoreCase(columnItem)){
						fields.append(",'"+columnItem+"'");
						str.append(",{");
						str.append("text: '"+DBASE+"',");
						str.append("width:"+width+",");
						str.append("remoteSort: true,");
						str.append("align:'left',");
						str.append("dataIndex: '"+columnItem+"' ");
						str.append("}");
						continue;
					}
					String columnName = "";
					String codesetId = "";
					String columnType = "";
					int columnLength = 0;
					int decimalWidth = 0;
					if(DataDictionary.getFieldItem(columnItem) != null){
						columnName = DataDictionary.getFieldItem(columnItem).getItemdesc();
						codesetId = DataDictionary.getFieldItem(columnItem).getCodesetid();
						columnType = DataDictionary.getFieldItem(columnItem).getItemtype();
						columnLength = DataDictionary.getFieldItem(columnItem).getDisplaywidth();// 显示长度 
						decimalWidth = DataDictionary.getFieldItem(columnItem).getDecimalwidth();// 小数位
						fields.append(",'"+columnItem+"'");
						str.append(",{");
						str.append("text: '"+columnName+"',");
						str.append("width:"+width+",");
						str.append("remoteSort: true,");
						String align = "left";
						if("D".equals(columnType) || "N".equals(columnType))
							align = "right";
						str.append("align:'"+align+"',");
						str.append("dataIndex: '"+columnItem+"' ");
						str.append("}");
						continue;
					}else{
						continue;
					}
				}
				str.append("]");			
				fields.append(",'TABLETYPE'");
				fields.append("]");
				map.put("column", str.toString());
				map.put("fields", fields.toString());
			} catch (Exception ex) {
				ex.printStackTrace();
			}finally{
				PubFunc.closeDbObj(rs);
			}
		}
		return map;
	}
	/**
	 * 
	 * @Title: getOrderby   
	 * @Description:根据前台正倒序获取order by
	 * @param @param sort
	 * @param @return 
	 * @return String 
	 * @author:zhaoxg   
	 * @throws
	 */
	public String getOrderby(String sort,String tablename){
		String order = " order by dbname,a0000,b0110,e0122";
		try{
			String str = ",B0110,E0122,E01A1,";//单位 部门 岗位 排序按照a0000
			if(sort!=null){
				sort=PubFunc.hireKeyWord_filter_reback(sort);
				JSONArray arry = JSONArray.fromObject(sort);
				JSONObject jsonObject = arry.getJSONObject(0);
				HashMap<String, String> sortmap = new HashMap<String, String>();
				for (Iterator<?> iter = jsonObject.keys(); iter.hasNext();)
				{
					String key = (String) iter.next();
					String value = jsonObject.get(key).toString();
					sortmap.put(key, value);
				}
				if(str.indexOf(","+sortmap.get("property").toUpperCase()+",")!=-1){
					order = " left join organization on organization.codeitemid="+tablename+"."+sortmap.get("property")+" order by organization.A0000 "+sortmap.get("direction");
				}else{
					order = " order by "+ sortmap.get("property")+" "+sortmap.get("direction");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return order;
	}
	/**
	 * @author lis
	 * @Description: 获取当前表格字段
	 * @date 2016-2-20
	 * @param tableName
	 * @return
	 */
	public String getColumnStr(String tableName){
		RowSet rs = null;
		StringBuffer columnStr = new StringBuffer();
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			String sql = "select * from " + tableName + " where 1=2";
			rs = dao.search(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String columnItem = metaData.getColumnName(i).toLowerCase();
				columnStr.append("," + columnItem);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(columnStr.length() > 0)
			return columnStr.substring(1);
		else 
			return "";
	}
	/**
	 * 
	 * @Title: getChangeInfoColumn   
	 * @Description:  信息变动列头  
	 * @param @param tableName 临时表名
	 * @param @param exceptStr 排除字段
	 * @param @return 
	 * @return HashMap 
	 * @author:zhaoxg   
	 * @throws
	 */
	public HashMap getChangeInfoColumn(String tableName, String exceptStr){
		
		/** 获取类型名称 */
		ArrayList columnTmp = new ArrayList();
		HashMap map = new HashMap();
		StringBuffer fields = new StringBuffer();//显示字段
		fields.append("[");
		StringBuffer str = new StringBuffer();//column
		StringBuffer tipItem = new StringBuffer();//需要提示的列名
		str.append("[");
		str.append("{");
		str.append("xtype: 'templatecolumn',");
		str.append("text:'<input name=2 type=checkbox id=selall onclick=\"change_me.updateState(this.id,this.checked,this.name);\" checked />',");//默认进来全选，所以这写死选中
		str.append("menuDisabled:true,");
		str.append("width:35,");
		str.append("align:'center',");
		str.append("hideable:false,");
		str.append("tpl:'<input name={TABLETYPE} type=checkbox id={DBNAME1}{A0100}  onclick=\"change_me.updateState(this.id,this.checked,this.name);\" {STATE} />' ");//读取state的状态 取值的时候把1和0分别转化为checked和空 用来确定是否勾选
		str.append("}");
		fields.append("'state'");
		/** 显示 */
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String sql = "select * from " + tableName + " where 1=2";
			rs = dao.search(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			// 列宽
			int width = 120;
			String org = ",b0110,e0122,a0101,";//单位，部门，姓名  现是子集 原是薪资表&&其他的原子集 现是薪资表
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String columnItem = metaData.getColumnName(i).toUpperCase();
				if (exceptStr.indexOf("," + Strings.toLowerCase(columnItem) + ",") != -1) {
					continue;
				} 
				if("dbname".equalsIgnoreCase(columnItem)){
					fields.append(",'"+columnItem+"'");
					str.append(",{");
					str.append("text: '"+DBASE+"',");
					str.append("width:"+width+",");
					str.append("remoteSort: true,");
					str.append("align:'left',");
					str.append("dataIndex: '"+columnItem+"' ");
					str.append("}");
					continue;
				}
				String columnName = "";
				String codesetId = "";
				String columnType = "";
				int columnLength = 0;
				int decimalWidth = 0;
				if(!StringUtils.isEmpty(columnItem)){
					columnName = columnItem;
					if(DataDictionary.getFieldItem(columnItem) != null){
						columnName = DataDictionary.getFieldItem(columnItem).getItemdesc();
						codesetId = DataDictionary.getFieldItem(columnItem).getCodesetid();
						columnType = DataDictionary.getFieldItem(columnItem).getItemtype();
						columnLength = DataDictionary.getFieldItem(columnItem).getDisplaywidth();// 显示长度 
						decimalWidth = DataDictionary.getFieldItem(columnItem).getDecimalwidth();// 小数位
					} 
					if(i <= (metaData.getColumnCount()-1) && (columnItem.toLowerCase()+"1").equalsIgnoreCase(metaData.getColumnName(i+1).toLowerCase())){
						str.append(",{");
						str.append("text: '"+columnName+"',");
						str.append("remoteSort: true,");
						str.append("dataIndex: '',");
						str.append("columns : [");
//原子集 现是薪资表
						fields.append(",'" + metaData.getColumnName(i + 1).toUpperCase() + "'");
						fields.append(",'" + metaData.getColumnName(i).toUpperCase() + "'");
						tipItem.append(tipItem.length() > 35?tipItem.lastIndexOf(".") == -1?"...":"":"," + columnName);
						// 原+列名
						str.append("{");
						str.append("text: '" + ResourceFactory.getProperty("gz_new.gz_accounting.before") + columnName + "',");
						str.append("width:" + width + ",");
						str.append("remoteSort: true,");
						
						String align = "left";
						if("D".equals(columnType) || "N".equals(columnType))
							align = "right";
						str.append("align:'" + align + "',");
						str.append("dataIndex: '" + metaData.getColumnName(i + 1).toUpperCase() + "', ");
						str.append("renderer:change_me.nowInfo");
						str.append("}");
						// 现+列名
						str.append(",{");
						str.append("text: '" + ResourceFactory.getProperty("inform.muster.now") + columnName + "',");
						str.append("width:" + width + ",");
						str.append("remoteSort: true,");
						str.append("align:'" + align + "',");
						str.append("dataIndex: '" + metaData.getColumnName(i).toUpperCase() + "', ");
						str.append("renderer:change_me.baseInfo");
						str.append("}");

						str.append("]");
						str.append("}");
						
						i++;
					}else{
						if(DataDictionary.getFieldItem(columnItem) != null){
							fields.append(",'"+columnItem+"'");
							str.append(",{");
							str.append("text: '"+columnName+"',");
							str.append("width:"+width+",");
							str.append("remoteSort: true,");
							String align = "left";
							if("D".equals(columnType) || "N".equals(columnType))
								align = "right";
							str.append("align:'"+align+"',");
							str.append("dataIndex: '"+columnItem+"' ");
							str.append("}");
						}else{
							continue;
						}
					}
				}
			}
			str.append("]");			
			fields.append(",'TABLETYPE'");
			fields.append("]");
			map.put("column", str.toString());
			map.put("fields", fields.toString());
			map.put("tipItem", tipItem.substring(1));
		} catch (Exception ex) {
			ex.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return map;
	}
	/**
	 * 获取列头,信息变动人员
	 * @param baseList 列头源数据
	 * @return
	 */
	public ArrayList<ColumnsInfo> editColumnsList(ArrayList baseList) {

		ArrayList<ColumnsInfo> columnList = new ArrayList<ColumnsInfo>();

		for (int i = 0; i < baseList.size(); i++) {
			Object obj = baseList.get(i);
			if (obj instanceof ColumnsInfo) {
				columnList.add((ColumnsInfo) obj);
			} else {// 复合表头
				HashMap columnBox = (HashMap) obj;
				ArrayList list = (ArrayList) columnBox.get("items");
				for (int j = 0; j < list.size(); j++) {
					Object childObj = list.get(j);
					columnList.add((ColumnsInfo) childObj);
				}
			}
		}

		return columnList;
	}
	/**
	 * 列表 列表中存放是的LazyBean
	 * @param viewtype 0:新增人员  1:减少人员 2:信息变动人员 3:停发人员
	 * @param tableName <<新增人员临时表>>
	 * @param exceptStr 排除字段
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList<LazyDynaBean> getDataList(String viewtype, String tableName, ArrayList<ColumnsInfo> columnsInfoList, String salaryid) throws GeneralException{ 
		
		ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		RowSet rs1 = null;
		try {
			String sql = "select * from " + tableName + " order by dbname,a0000,b0110,e0122";
			rs = dao.search(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			ArrayList<String> list1 = new ArrayList<String>();
			
			while(rs.next()){
				LazyDynaBean lazyvo = new LazyDynaBean();
				for(ColumnsInfo columnsInfo : columnsInfoList){
					// 列头itemId
					String items = columnsInfo.getColumnId();
					/** 人员库 */ 
					if("dbname".equalsIgnoreCase(items)){
						rs1 = null;
						String sql1 = "select * from DBName where Pre = ?";
						list1.clear();
						list1.add(rs.getString(items));
						if(StringUtils.isEmpty(rs.getString(items))){
							list1.clear();
							list1.add("");
						}
						rs1 = dao.search(sql1, list1);
						if (rs1.next()) {
							lazyvo.set(items, rs1.getString("DBName"));
						} else {
							lazyvo.set(items, "");
						}
						continue;
					}
					/** 单位名称 */ 
					if("b0110".equalsIgnoreCase(items)){
						rs1 = null;
						String sql1 = "select * from organization where codeitemid = ?";
						list1.clear();
						list1.add(rs.getString(items));
						if(StringUtils.isEmpty(rs.getString(items))){
							list1.clear();
							list1.add("");
						}
						rs1 = dao.search(sql1, list1);
						if (rs1.next()) {
							lazyvo.set(items, rs1.getString("codeitemdesc"));
						} else {
							lazyvo.set(items, "");
						}
						continue;
					}
					/** 岗位名称 */ 
					if("e0122".equalsIgnoreCase(items)){
						String display_e0122 = "";
						Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
						display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
						if (display_e0122 == null || "00".equals(display_e0122) || "".equals(display_e0122)) {
							display_e0122 = "0";
						}
						String department = AdminCode.getCode("UM",rs.getString(items),Integer.parseInt(display_e0122))!=null?AdminCode.getCode("UM",rs.getString(items),Integer.parseInt(display_e0122)).getCodename():AdminCode.getCodeName("UM",display_e0122);
						lazyvo.set(items, department);
						continue;
					}
					/** 其他项 */ 
					// 唯一性指标
					String onlyname = "";
					// 如果唯一性指标启用,或者表中不存在
					Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
					String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
					if (!StringUtils.isEmpty(uniquenessvalid) && !"0".equals(uniquenessvalid) && this.isAddColumn(onlyname, "dbname,b0110,e0122,a0101,state,a0100")) {
						onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
					}
					
					// 编辑新增/减少人员设置的比对指标
					String rightvalue = "";
					if(ADD_PERSON.equals(viewtype) || REDUCE_PERSON.equals(viewtype)){
						SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(this.conn, Integer.parseInt(salaryid));
						if(ADD_PERSON.equals(viewtype)){
							rightvalue = ctrl_par.getValue(SalaryCtrlParamBo.ADD_MAN_FIELD);
						}else if(REDUCE_PERSON.equals(viewtype)){
							rightvalue = ctrl_par.getValue(SalaryCtrlParamBo.DEL_MAN_FIELD);
						}
					}
					// 指标转化成对应的汉字
					ArrayList<FieldItem> fieldItemList = this.getadd_delList(rightvalue, salaryid, onlyname);
					
					// 数据库中数据与新增人员比对指标进行匹配      如果有:输出汉字;没有:原样输出
					if(!StringUtils.isEmpty(rs.getString(items))){
						for (FieldItem fieldItem : fieldItemList) {
							if (items.equalsIgnoreCase(fieldItem.getItemid()) && "a".equalsIgnoreCase(fieldItem.getItemtype()) && !"0".equals(fieldItem.getCodesetid())) {
								lazyvo.set(items, AdminCode.getCodeName(fieldItem.getCodesetid(), items));
								break;
							} 
						}
						lazyvo.set(items, rs.getString(items));
					} else{
						lazyvo.set(items, "");
					}
				}
			list.add(lazyvo);
			}
		}catch (Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(rs1);
		}
		
		return list;
	}
	
	
	
	public ArrayList<FieldItem> getadd_delList(String rightvalue, String salaryid, String onlyname) {
		
		ArrayList<FieldItem> list = new ArrayList<FieldItem>();
		
		String cloumnStr = "DBNAME,A0100,A0000,B0110,E0122,A0101,STATE";
		rightvalue = rightvalue != null ? rightvalue.replaceAll(",", "','") : "";
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer sql = new StringBuffer();
		sql.append("select itemid,itemdesc,sortid from salaryset where initflag<>3 and salaryid=");
		sql.append(salaryid);
		sql.append(" and itemid in ('");
		sql.append(rightvalue);
		sql.append("') group by itemid,itemdesc,sortid order by sortid");
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sql.toString());
			for (Iterator it = dylist.iterator(); it.hasNext();) {
				DynaBean dynabean = (DynaBean) it.next();
				FieldItem item = DataDictionary.getFieldItem(dynabean.get("itemid").toString());
				if ((item != null && cloumnStr.indexOf(item.getItemid()) == -1) && !(onlyname.equalsIgnoreCase(item.getItemid()))) {// 过滤重复字段
					list.add(item);
				}
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		
		return list;
	}

	/**
	 * 判断是否用加唯一性指标字段
	 * 
	 * @param field
	 * @param fields
	 * @return
	 */
	public boolean isAddColumn(String field, String fields) {
		
		boolean flag = true;
		
		if (field == null || "".equals(field))
			flag = false;
		else {
			if (("," + fields + ",").toUpperCase().indexOf("," + field.toUpperCase() + ",") != -1)
				flag = false;
		}
		
		return flag;
	}

	/**
	 * 获取FieldItem
	 * @param type
	 * @param salaryid
	 * @return
	 */
	public ArrayList<FieldItem> getField(int type, String salaryid) {
		
		ArrayList<FieldItem> list = new ArrayList<FieldItem>();
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String rightvalue = getvalue(salaryid, type);
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("select itemid,itemdesc,itemlength,decwidth,codesetid,itemtype");
			sqlstr.append(" from salaryset where UPPER(itemid) in('");
			sqlstr.append(rightvalue.toUpperCase().replaceAll(",", "','"));
			sqlstr.append("') and salaryid =" + salaryid);
			
			rs = dao.search(sqlstr.toString());
			while (rs.next()) {
				FieldItem fielditem = new FieldItem();
				fielditem.setItemid(rs.getString("itemid"));
				fielditem.setItemdesc(rs.getString("itemdesc"));
				fielditem.setItemlength(rs.getInt("itemlength"));
				fielditem.setDecimalwidth(rs.getInt("decwidth"));
				fielditem.setCodesetid(rs.getString("codesetid"));
				fielditem.setItemtype(rs.getString("itemtype"));
				list.add(fielditem);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}

		return list;
	}

	/**
	 * 获取参数值
	 * @param salaryid
	 * @param type
	 * @return
	 */
	public String getvalue(String salaryid, int type) {
		
		SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(this.conn, Integer.parseInt(salaryid));
		return ctrl_par.getValue(type);
	}
	/**
	 * 列头ColumnsInfo对象初始化
	 * @param columnId id
	 * @param columnDesc 名称
	 * @param columnDesc 显示列宽
	 * @return
	 */
	private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth, String codesetId, String columnType, int columnLength, int decimalWidth){
		
		ColumnsInfo columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId(columnId);
		columnsInfo.setColumnDesc(columnDesc);
		columnsInfo.setColumnWidth(columnWidth);//显示列宽
		columnsInfo.setCodesetId(codesetId);// 指标集
		columnsInfo.setColumnType(columnType);// 类型N|M|A|D
		columnsInfo.setColumnLength(columnLength);// 显示长度 
		columnsInfo.setDecimalWidth(decimalWidth);// 小数位
		columnsInfo.setAllowBlank(true);// 编辑时是否可以为空
		columnsInfo.setReadOnly(true);// 是否只读
		columnsInfo.setFromDict(false);// 是否从数据字典里来
		columnsInfo.setLocked(false);//是否锁列
		if("D".equals(columnType) || "N".equals(columnType))//数值和日期型的默认居右 zhaoxg add 2016-5-4
			columnsInfo.setTextAlign("right");
		return columnsInfo;
	}
	/**
	 * 获取功能按钮
	 * @return
	 */
	public ArrayList<ButtonInfo> getButtonList(){
		
		ArrayList<ButtonInfo> buttonList = new ArrayList<ButtonInfo>();
		try{
			ButtonInfo export = new ButtonInfo(ResourceFactory.getProperty("button.export"), "");
			buttonList.add(export);
			ButtonInfo enter = new ButtonInfo(ResourceFactory.getProperty("button.ok"), "");
			buttonList.add(enter);
			ButtonInfo returnButton = new ButtonInfo(ResourceFactory.getProperty("button.return"), "");
			buttonList.add(returnButton);

		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return buttonList;
	}
	/**
	 * 获取人员库转化map
	 * @return
	 */
	public HashMap<String, String> getDbname(){
		
		HashMap<String, String> map = new HashMap<String, String>();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String sql = "select * from DBName";
			rs = dao.search(sql.toString());
			while (rs.next()) {
 				map.put(rs.getString("Pre"), rs.getString("DBName"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		
		return map;
	}
	/**
	 * 获取临时表名称
	 * @param tableType：表区分 add:新增 reduce:减少 info:信息变动 stop:停发
	 * @return
	 */
	public String getTableName(String tableType){
		String tableName = "";
		if("add".equals(tableType)){
			tableName = "t#" + this.userview.getUserName() + "_gz_Ins";
		}else if("reduce".equals(tableType)){
			tableName = "t#" + this.userview.getUserName() + "_gz_Dec";
		}else if("info".equals(tableType)){
			tableName = "t#" + this.userview.getUserName() + "_gz_Bd";
		}else if("stop".equals(tableType)){
			tableName = "t#" + this.userview.getUserName() + "_gz_Tf";
		}
		
		return tableName; 
	}
	/**
	 * 从薪资表中删除薪资停发的人员
	 * @param salaryid 薪资类别号
	 * @param gz_tablename 薪资数据表
	 * @param manager 管理员
	 * @throws GeneralException
	 */
	public void removeA01Z0ManData(String salaryid, String gz_tablename, String manager) throws GeneralException {
		DbSecurityImpl dbS = new DbSecurityImpl();
		String tablename = "t#" + this.userview.getUserName() + "_gz_Tf";
		ContentDAO dao = new ContentDAO(this.conn);
		PreparedStatement ps = null;
		try {
			SalaryCtrlParamBo ctrlparam = new SalaryCtrlParamBo(this.conn, Integer.parseInt(salaryid));
			String a01z0Flag = ctrlparam.getValue(SalaryCtrlParamBo.A01Z0, "flag"); // 是否显示停发标识 1：有
			if (a01z0Flag != null && "1".equals(a01z0Flag)) {//停发
				DbWizard dbw = new DbWizard(this.conn);
				if (!dbw.isExistTable(tablename, false)){//<<停发人员>>表不存在则返回
					return;
				}
				int rows = getRows(tablename);
				if (rows == 0){//<<停发人员>>表选中条数为0则返回
					return;
				}
				
				// 删除税率明细
				StringBuffer buf1 = new StringBuffer("");
				buf1.append("delete from gz_tax_mx where salaryid=" + salaryid + " and lower(nbase)=? and a0100=? and "
						+ Sql_switcher.year("a00z0") + "=? and " + Sql_switcher.month("a00z0") + "=? and a00z1=?");
				if (manager.length() > 0 && !this.userview.getUserName().equalsIgnoreCase(manager)){
					buf1.append(" and ( lower(userflag)='" + manager.toLowerCase() + "' or userflag is null )");
				} else {
					buf1.append(" and ( lower(userflag)='" + this.userview.getUserName().toLowerCase() + "' or userflag is null )");
				}

				String sub_str = "select * from " + gz_tablename + " where exists(select * from " + tablename
						+ " where state='1' and upper(" + gz_tablename + ".nbase)=upper(";
				sub_str += tablename + ".dbname) and " + gz_tablename + ".A0100=" + tablename + ".A0100)";
				ps = this.conn.prepareStatement(buf1.toString());
				RowSet rowSet = dao.search(sub_str);
				while (rowSet.next()) {
					Calendar d = Calendar.getInstance();
					d.setTime(rowSet.getDate("a00z0"));
					ps.setString(1, rowSet.getString("nbase").toLowerCase());
					ps.setString(2, rowSet.getString("a0100"));
					ps.setInt(3, d.get(Calendar.YEAR));
					ps.setInt(4, (d.get(Calendar.MONTH) + 1));
					ps.setInt(5, rowSet.getInt("a00z1"));
					ps.addBatch();
				}

				// 打开Wallet
				dbS.open(conn, buf1.toString());
				ps.executeBatch();

				
				// 删除<<薪资数据表>>记录
				StringBuffer buf = new StringBuffer();
				buf.append("delete from ");
				buf.append(gz_tablename);
				buf.append(" where exists(select * from ");
				buf.append(tablename);
				buf.append(" where state='1' and upper(");
				buf.append(gz_tablename);
				buf.append(".nbase)=upper(");
				buf.append(tablename);
				buf.append(".dbname) and ");
				buf.append(gz_tablename);
				buf.append(".A0100=");
				buf.append(tablename);
				buf.append(".A0100)");
				dao.update(buf.toString());

				// 同步薪资发放数据的映射表
				buf.setLength(0);
				String username = this.userview.getUserName().toLowerCase();
				if (manager.length() > 0){
					username = manager.toLowerCase();
				}
				buf.append("delete from salary_mapping where salaryid=" + salaryid + " and lower(userflag)='" + username + "' and  exists(select * from ");
				buf.append(tablename);
				buf.append(" where state='1' and upper(salary_mapping.nbase)=upper(");
				buf.append(tablename);
				buf.append(".dbname) and salary_mapping.A0100=");
				buf.append(tablename);
				buf.append(".A0100)");
				dao.update(buf.toString());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		} finally {
			try {
				// 关闭Wallet
				dbS.close(this.conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			PubFunc.closeDbObj(ps);
		}
	}
	/**
	 * 从薪资表中删除档案库中不存在的人员
	 * @param salaryid 薪资类别号
	 * @param gz_tablename 薪资数据表
	 * @param manager 管理员
	 * @throws GeneralException 
	 */
	public void removeDelManData(String salaryid, String gz_tablename, String manager) throws GeneralException {
		
		StringBuffer buf = new StringBuffer();
		String tablename = "t#" + this.userview.getUserName() + "_gz_Dec";
		DbSecurityImpl dbS = new DbSecurityImpl();
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			int rows = getRows(tablename);
			if (rows == 0){//<<减少人员>>表选中条数为0则返回
				return;
			}
			
			/** 总额计算 */
			ArrayList dateList = new ArrayList();
			SalaryTotalBo bo = new SalaryTotalBo(this.conn, this.userview, salaryid);
			SalaryCtrlParamBo ctrlparam = new SalaryCtrlParamBo(this.conn, Integer.parseInt(salaryid));
			String isControl = ctrlparam.getValue(SalaryCtrlParamBo.AMOUNT_CTRL, "flag"); // 该工资类别是否进行总额控制
			if ("1".equals(isControl)) {
				StringBuffer sql = new StringBuffer("");
				sql.append(" select distinct " + gz_tablename + ".a00z0," + gz_tablename + ".a00z1 from " + tablename + ","
						+ gz_tablename + " where upper(" + gz_tablename + ".nbase)=upper(" + tablename + ".dbname) ");
				sql.append(" and " + gz_tablename + ".A0100=" + tablename + ".A0100 ");
				sql.append(" and " + tablename + ".state='1'  and " + gz_tablename + ".sp_flag='07'  ");
				RowSet rowSet = dao.search(sql.toString());
				Calendar d = Calendar.getInstance();
				HashSet dateSet = new HashSet();
				while (rowSet.next()) {
					d.setTime(rowSet.getDate(1));
					int a00z1 = rowSet.getInt(2);
					dateSet.add(d.get(Calendar.YEAR) + "-" + (d.get(Calendar.MONTH) + 1) + "-" + a00z1);
				}

				StringBuffer buf2 = new StringBuffer("");
				buf2.append(" and  exists(select * from ");
				buf2.append(tablename);
				buf2.append(" where state='1' and upper(");
				buf2.append(gz_tablename);
				buf2.append(".nbase)=upper(");
				buf2.append(tablename);
				buf2.append(".dbname) and ");
				buf2.append(gz_tablename);
				buf2.append(".A0100=");
				buf2.append(tablename);
				buf2.append(".A0100 and " + gz_tablename + ".sp_flag='07'   )");

				dateList = bo.getDateList(buf2.toString(), dateSet, false);
			}

			// 删除税率明细
			StringBuffer buf1 = new StringBuffer("");
			buf1.append("delete from gz_tax_mx where salaryid=" + salaryid);
			if (manager.length() > 0 && !this.userview.getUserName().equalsIgnoreCase(manager)) {
				buf1.append(" and ( lower(userflag)='" + manager.toLowerCase() + "' or userflag is null )");
			} else {
				buf1.append(" and ( lower(userflag)='" + this.userview.getUserName().toLowerCase() + "' or userflag is null )");
			}
			buf1.append(" and exists( select null from (select * from "+gz_tablename+" where exists  (select null from " + tablename
					+ " where state='1' and upper(" + gz_tablename + ".nbase)=upper(");
			buf1.append(tablename + ".dbname) and " + gz_tablename + ".A0100=" + tablename + ".A0100) )");
		 
			buf1.append(" aa where  upper(gz_tax_mx.nbase)=upper(aa.nbase) and gz_tax_mx.A0100=aa.A0100 and gz_tax_mx.a00z1=aa.a00z1 ");
			buf1.append("and "+Sql_switcher.year("gz_tax_mx.a00z0")+"="+Sql_switcher.year("aa.a00z0"));
			buf1.append("and "+Sql_switcher.month("gz_tax_mx.a00z0")+"="+Sql_switcher.month("aa.a00z0")+" ) ");
			dao.update(buf1.toString());
			/*
			StringBuffer buf1 = new StringBuffer("");
			buf1.append("delete from gz_tax_mx where salaryid=" + salaryid + " and lower(nbase)=? and a0100=? and "
					+ Sql_switcher.year("a00z0") + "=? and " + Sql_switcher.month("a00z0") + "=? and a00z1=?");
			if (manager.length() > 0 && !this.userview.getUserName().equalsIgnoreCase(manager)) {
				buf1.append(" and ( lower(userflag)='" + manager.toLowerCase() + "' or userflag is null )");
			} else {
				buf1.append(" and ( lower(userflag)='" + this.userview.getUserName().toLowerCase() + "' or userflag is null )");
			}
			String sub_str = "select * from " + gz_tablename + " where exists(select * from " + tablename
					+ " where state='1' and upper(" + gz_tablename + ".nbase)=upper(";
			sub_str += tablename + ".dbname) and " + gz_tablename + ".A0100=" + tablename + ".A0100)";
			PreparedStatement ps = this.conn.prepareStatement(buf1.toString());
			RowSet rowSet = dao.search(sub_str);
			while (rowSet.next()) {
				Calendar d = Calendar.getInstance();
				d.setTime(rowSet.getDate("a00z0"));
				ps.setString(1, rowSet.getString("nbase").toLowerCase());
				ps.setString(2, rowSet.getString("a0100"));
				ps.setInt(3, d.get(Calendar.YEAR));
				ps.setInt(4, (d.get(Calendar.MONTH) + 1));
				ps.setInt(5, rowSet.getInt("a00z1"));
				ps.addBatch();
			}

			// 打开Wallet
			dbS.open(conn, buf1.toString());
			ps.executeBatch();
			 */
			
			// 删除<<薪资数据表>>记录
			buf.append("delete from ");
			buf.append(gz_tablename);
			buf.append(" where exists(select * from ");
			buf.append(tablename);
			buf.append(" where state='1' and upper(");
			buf.append(gz_tablename);
			buf.append(".nbase)=upper(");
			buf.append(tablename);
			buf.append(".dbname) and ");
			buf.append(gz_tablename);
			buf.append(".A0100=");
			buf.append(tablename);
			buf.append(".A0100)");
			dao.update(buf.toString());

			// 同步薪资发放数据的映射表
			buf.setLength(0);
			String username = this.userview.getUserName().toLowerCase();
			if (manager.length() > 0){
				username = manager.toLowerCase();
			}
			buf.append("delete from salary_mapping where salaryid=" + salaryid + " and lower(userflag)='" + username + "' and  exists (select * from ");
			buf.append(tablename);
			buf.append(" where state='1' and upper(salary_mapping.nbase)=upper(");
			buf.append(tablename);
			buf.append(".dbname) and salary_mapping.A0100=");
			buf.append(tablename);
			buf.append(".A0100)");
			dao.update(buf.toString());

			/** 总额计算 */
			bo.calculateTotalSum(dateList);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		} finally {
			try {
				// 关闭Wallet
				dbS.close(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 更新信息发生变化的人员信息至薪资表中
	 * @param salaryid 薪资类别号
	 * @param gz_tablename 薪资数据表
	 * @param manager 管理员
	 * @throws GeneralException
	 */
	public void updateChgInfoManData(String salaryid, String gz_tablename, String manager, String compareItem) throws GeneralException {
		String tablename = "t#" + this.userview.getUserName() + "_gz_bd";
		int rows = getRows(tablename);
		if (rows == 0){
			return;
		}
		/** 更新串 */
		StringBuffer strupdate = new StringBuffer();
		strupdate.append(gz_tablename);
		strupdate.append(".B0110=");
		strupdate.append(tablename);
		strupdate.append(".B0110`");
		strupdate.append(gz_tablename);
		strupdate.append(".E0122=");
		strupdate.append(tablename);
		strupdate.append(".E0122`");
		strupdate.append(gz_tablename);
		strupdate.append(".A0101=");
		strupdate.append(tablename);
		strupdate.append(".A0101 ");
		if(StringUtils.isNotBlank(compareItem)) {
			String[] compareArray = compareItem.split(",");
			for(int i = 0; i < compareArray.length; i++) {
				strupdate.append("`" + gz_tablename);
				strupdate.append("." + compareArray[i] + "=");
				strupdate.append(tablename);
				strupdate.append("." + compareArray[i] );
			}
		}
		StringBuffer strwhere = new StringBuffer();
		strwhere.append(" exists(select * from ");
		strwhere.append(tablename);
		strwhere.append(" where ");
		strwhere.append(gz_tablename);
		strwhere.append(".A0100=");
		strwhere.append(tablename);
		strwhere.append(".A0100");
		strwhere.append(" and upper(");
		strwhere.append(gz_tablename);
		strwhere.append(".nbase)=upper(");
		strwhere.append(tablename);
		strwhere.append(".dbname)");
		strwhere.append(" and state='1')");

		try {
			DbWizard dbw = new DbWizard(this.conn);
			/** 更新薪资表 */
			dbw.updateRecord(gz_tablename, tablename, gz_tablename + ".A0100=" + tablename + ".A0100 and UPPER(" + gz_tablename 
					+ ".nbase)=UPPER(" + tablename + ".dbname)", strupdate.toString(), strwhere.toString(), strwhere.toString());
			/** 更新薪资历史数据表,A0101 */
			strupdate.setLength(0);
			strupdate.append("salaryhistory");
			strupdate.append(".B0110=");
			strupdate.append(tablename);
			strupdate.append(".B0110`");
			strupdate.append("salaryhistory");
			strupdate.append(".E0122=");
			strupdate.append(tablename);
			strupdate.append(".E0122`");
			strupdate.append("salaryhistory");
			strupdate.append(".A0101=");
			strupdate.append(tablename);
			strupdate.append(".A0101 ");
			if(StringUtils.isNotBlank(compareItem)) {
				String[] compareArray = compareItem.split(",");
				for(int i = 0; i < compareArray.length; i++) {
					strupdate.append("`salaryhistory");
					strupdate.append("." + compareArray[i] + "=");
					strupdate.append(tablename);
					strupdate.append("." + compareArray[i] );
				}
			}
			strwhere.setLength(0);
			strwhere.append(" exists(select * from ");
			strwhere.append(tablename);
			strwhere.append(" where ");
			strwhere.append("salaryhistory");
			strwhere.append(".A0100=");
			strwhere.append(tablename);
			strwhere.append(".A0100");
			strwhere.append(" and ");
			strwhere.append("upper(salaryhistory");
			strwhere.append(".nbase)=upper(");
			strwhere.append(tablename);
			strwhere.append(".dbname)");
			strwhere.append(" and state='1')");

			SalaryCtrlParamBo ctrlparam = new SalaryCtrlParamBo(this.conn, Integer.parseInt(salaryid));
			String username = this.userview.getUserName().toLowerCase();
			if (manager != null && manager.trim().length() > 0){
				username = manager;
			}
			String table_name = username + "_salary_" + salaryid;

			strwhere.append("  and salaryhistory.salaryid=" + salaryid + "  and exists (select null from ");
			strwhere.append(table_name);
			strwhere.append(" where ");
			strwhere.append("salaryhistory");
			strwhere.append(".A0100=");
			strwhere.append(table_name);
			strwhere.append(".A0100");
			strwhere.append(" and ");
			strwhere.append("upper(salaryhistory");
			strwhere.append(".nbase)=upper(");
			strwhere.append(table_name);
			strwhere.append(".nbase)");
			strwhere.append(" and  salaryhistory.a00z0=" + table_name + ".a00z0  and  salaryhistory.a00z1=" + table_name + ".a00z1   )");
			dbw.updateRecord("salaryhistory", tablename, "salaryhistory" + ".A0100=" + tablename  + ".A0100 and upper(salaryhistory.nbase)=upper("
					+ tablename + ".dbname)", strupdate.toString(), strwhere.toString(), strwhere.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 求当前表的记录数
	 * @param tablename
	 * @return
	 */
	private int getRows(String tablename) {
		int maxrows = 0;
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("select count(*) as nrow from ");
			buf.append(tablename);
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rset = dao.search(buf.toString());
			if (rset.next()){
				maxrows = rset.getInt("nrow");
			}
			rset.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return maxrows;
	}
	/**
	 * 取得当前薪资表处理的发放日期和次数
	 * 如果初次使用,历史记录表为空，当前处到的日期为系统日期
	 * 当前处理次数为1.
	 * 从返回HashMap 取得ym(业务日期),yyyy-MM-dd
	 *              取得count(发放次数),
	 * @param  
	 * @return
	 */
	public HashMap getYearMonthCount(String salaryid)
	{
		HashMap mp = new HashMap();
		
		try {
			SalaryPkgBo pgkbo = new SalaryPkgBo(this.conn, this.userview, 0);
			String username = this.userview.getUserName();
			SalaryCtrlParamBo ctrlparam = new SalaryCtrlParamBo(this.conn, Integer.parseInt(salaryid));
			String manager = ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			if (manager != null && manager.length() > 0){
				username = manager;
			}
			LazyDynaBean abean = pgkbo.searchCurrentDate2(salaryid, username);
			/** 当前处理的到的年月标识和次数 */
			String strYm = (String) abean.get("strYm");
			String strC = (String) abean.get("strC");
			if ("".equalsIgnoreCase(strYm)) {
				String appdate = ConstantParamter.getAppdate(this.userview.getUserName());
				if (appdate == null || appdate.trim().length() == 0) {
					strYm = DateUtils.format(new Date(), "yyyy-MM-dd");
				} else {
					strYm = appdate.replaceAll("\\.", "-");
				}
				String[] tmp = StringUtils.split(strYm, "-");
				strYm = tmp[0] + "-" + tmp[1] + "-01";
				strC = "1";
				DbNameBo.appendExtendLog(this.userview.getUserName(), Integer.parseInt(salaryid), strYm, strC, this.conn);
			}
			mp.put("ym", strYm);
			mp.put("count", strC);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return mp;
	}
	/**
	 * 获取列头   导出excel用
	 * @param viewtype 0:新增人员  1:减少人员 3:停发人员
	 * @param tableName <<新增人员临时表>>
	 * @param exceptStr 排除字段
	 * @return
	 */
	public ArrayList<LazyDynaBean> getExpHeadList(String tableName, String exceptStr){
		
		/** 获取类型名称 */
		ArrayList<LazyDynaBean> columnTmp = new ArrayList<LazyDynaBean>();
		
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			try {
				String sql = "select * from " + tableName + " where 1=2";
				rs = dao.search(sql);
				ResultSetMetaData metaData = rs.getMetaData();
				// 列宽
				int width = 180;
				if(metaData.getColumnCount() > 10){
					width = 120;
				}
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					String columnItem = metaData.getColumnName(i).toLowerCase();
					if (exceptStr.indexOf("," + Strings.toLowerCase(columnItem) + ",") != -1) {
						continue;
					}
					if("dbname".equalsIgnoreCase(columnItem)){
						FieldItem  fieldItem = DataDictionary.getFieldItem(columnItem);
						LazyDynaBean bean = new LazyDynaBean();
						bean.set("content", DBASE);// 列头名称
						bean.set("itemid", columnItem);// 列头代码
						bean.set("codesetid", "0");// 列头代码
						bean.set("decwidth", "0");// 列小数点后面位数
						bean.set("colType", "A");// 该列数据类型
						columnTmp.add(bean);
						continue;
					}
					String columnName = "";
					String codesetId = "";
					String columnType = "";
					String decimalWidth = "0";
					if(DataDictionary.getFieldItem(columnItem) != null){
						columnName = DataDictionary.getFieldItem(columnItem).getItemdesc();
						codesetId = DataDictionary.getFieldItem(columnItem).getCodesetid();
						columnType = DataDictionary.getFieldItem(columnItem).getItemtype();
						decimalWidth = String.valueOf(DataDictionary.getFieldItem(columnItem).getDecimalwidth());// 小数位
						
						LazyDynaBean bean = new LazyDynaBean();
						bean.set("content", columnName);// 列头名称
						bean.set("itemid", columnItem);// 列头代码
						bean.set("codesetid", codesetId);// 列头代码
						bean.set("decwidth", decimalWidth);// 列小数点后面位数
						bean.set("colType", columnType);// 该列数据类型
						columnTmp.add(bean);
						continue;
					}else{
						continue;
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}finally{
				PubFunc.closeDbObj(rs);
			}
		
		return columnTmp;
	}
	/**
	 * 获取列头   导出excel用
	 * @param viewtype 0:新增人员  1:减少人员 3:停发人员
	 * @param tableName <<新增人员临时表>>
	 * @param exceptStr 排除字段
	 * @return
	 */
	public ArrayList<LazyDynaBean> getExpInfoHeadList(String tableName, String exceptStr){
		
		/** 获取类型名称 */
		ArrayList<LazyDynaBean> columnTmp = new ArrayList<LazyDynaBean>();
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			/* fromRowNum：合并单元格从那行开始
			 * toRowNum：合并单元格到哪行结束
			 * fromColNum：合并单元格从哪列开始
			 * toColNum：合并单元格从哪列结束*/
			int rowNum = 0;
			int colNum = 0;
			String sql = "select * from " + tableName + " where 1=2";
			rs = dao.search(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String columnItem = metaData.getColumnName(i).toLowerCase();
				if (exceptStr.indexOf("," + Strings.toLowerCase(columnItem) + ",") != -1) {
					continue;
				}
				if("dbname".equalsIgnoreCase(columnItem)){
					FieldItem  fieldItem = DataDictionary.getFieldItem(columnItem);
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("content", DBASE);// 列头名称
					bean.set("itemid", columnItem);// 列头代码
					bean.set("codesetid", "0");// 列头代码
					bean.set("decwidth", "0");// 列小数点后面位数
					bean.set("colType", "A");// 该列数据类型
					bean.set("fromRowNum", rowNum);// 合并单元格从那行开始
					bean.set("toRowNum", ++rowNum);// 合并单元格到哪行结束
					bean.set("fromColNum", colNum);// 合并单元格从哪列开始
					bean.set("toColNum", colNum);// 合并单元格从哪列结束
					columnTmp.add(bean);
					rowNum = 0;
					colNum += 1;//定位下次初始列
					continue;
				}
				String columnName = "";
				String codesetId = "";
				String columnType = "";
				String decimalWidth = "0";// 小数位
				if(!StringUtils.isEmpty(columnItem)){
					columnName = columnItem;
					if(DataDictionary.getFieldItem(columnItem) != null){
						columnName = DataDictionary.getFieldItem(columnItem).getItemdesc();
						codesetId = DataDictionary.getFieldItem(columnItem).getCodesetid();
						columnType = DataDictionary.getFieldItem(columnItem).getItemtype();
						decimalWidth = String.valueOf(DataDictionary.getFieldItem(columnItem).getDecimalwidth());
					}
					if(i <= (metaData.getColumnCount()-1) && (columnItem+"1").equalsIgnoreCase(metaData.getColumnName(i+1).toLowerCase())){//复合列
						LazyDynaBean bean = new LazyDynaBean();
						// 原+列名
						bean.set("content", ResourceFactory.getProperty("gz_new.gz_accounting.before")+columnName);// 列头名称
						bean.set("itemid", metaData.getColumnName(i+1).toLowerCase());// 列头代码
						bean.set("codesetid", codesetId);// 列头代码
						bean.set("decwidth", decimalWidth);// 列小数点后面位数
						bean.set("colType", columnType);// 该列数据类型
						columnTmp.add(bean);
						colNum += 1;//定位下次初始列
						
						bean = new LazyDynaBean();
						// 现+列名
						bean.set("content", ResourceFactory.getProperty("inform.muster.now")+columnName);// 列头名称
						bean.set("itemid", metaData.getColumnName(i).toLowerCase());// 列头代码
						bean.set("codesetid", codesetId);// 列头代码
						bean.set("decwidth", decimalWidth);// 列小数点后面位数
						bean.set("colType", columnType);// 该列数据类型
						columnTmp.add(bean);
						colNum += 1;//定位下次初始列
						i++;
						continue;
					}else{//跨行普通列
						LazyDynaBean bean = new LazyDynaBean();
						bean.set("content", columnName);// 列头名称
						bean.set("itemid", columnItem);// 列头代码
						bean.set("codesetid", codesetId);// 列头代码
						bean.set("decwidth", decimalWidth);// 列小数点后面位数
						bean.set("colType", columnType);// 该列数据类型
						bean.set("fromRowNum", rowNum);// 合并单元格从那行开始
						bean.set("toRowNum", ++rowNum);// 合并单元格到哪行结束
						bean.set("fromColNum", colNum);// 合并单元格从哪列开始
						bean.set("toColNum", colNum);// 合并单元格从哪列结束
						columnTmp.add(bean);
						rowNum = 0;
						colNum += 1;//定位下次初始列
						continue;
					}
				}else{
					continue;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		
		return columnTmp;
	}
	/**
	 * 获取文件名   导出excel用
	  * @param tableType：表区分 add:新增 reduce:减少 info:信息变动 stop:停发
	 * @return
	 */
	public String getExpFileName(String tableType){
			String fileName = "";
			if("add".equals(tableType)){
				fileName = "gz_"+this.userview.getUserName()+"_addmen.xls";//新增人员
			}else if("reduce".equals(tableType)){
				fileName = "gz_"+this.userview.getUserName()+"_delmen.xls";//减少人员
			}else if("info".equals(tableType)){
				fileName = "gz_"+this.userview.getUserName()+"_changeInfo.xls";//信息变动人员
			}else if("stop".equals(tableType)){
				fileName = "gz_"+this.userview.getUserName()+"_stop.xls";//停发人员
			}
			
			return fileName; 
	}
	/**
	 * 获取sheet名   导出excel用
	 * @param tableType：表区分 add:新增 reduce:减少 info:信息变动 stop:停发
	 * @return
	 */
	public String getExpSheetName(String tableType){
		String sheetName = "";
		if("add".equals(tableType)){
			sheetName = ResourceFactory.getProperty("gz.info.addmen");//新增人员
		}else if("reduce".equals(tableType)){
			sheetName = ResourceFactory.getProperty("gz.info.delmen");//减少人员
		}else if("info".equals(tableType)){
			sheetName = ResourceFactory.getProperty("gz.info.changeInfo");//信息变动人员
		}else if("stop".equals(tableType)){
			sheetName = ResourceFactory.getProperty("gz.info.a01z0");//停发人员
		}
		
		return sheetName; 
	}
	/**
	 * 获取导出用sql语句
	 * @param tableName：表名
	 * @return
	 */
	public String getExpSql(String tableName){
		StringBuilder str = new StringBuilder();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String sql = "select * from " + tableName + " where 1=2";
			rs = dao.search(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			
			str.append("select ");
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String columnItem = metaData.getColumnName(i).toLowerCase();
				if("dbname".equalsIgnoreCase(columnItem)){
					str.append("d.DBName");
					continue;
				}else{
					str.append(","+columnItem);
					
				}
			}
			str.append(" from ");
			str.append(tableName);
			str.append(" tmp ");
			str.append("left join DBName d ");
			str.append("on lower(tmp.DBNAME)=lower(d.Pre) ");
			str.append("order by tmp.dbname,a0000,b0110,e0122");
		} catch (Exception ex) {
			ex.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		
		return str.toString(); 
	}
	/**
	 * 获取复合表头的上半部分
	 * @param tableName <<新增人员临时表>>
	 * @param exceptStr 排除字段
	 */
	public ArrayList<LazyDynaBean> getExpMergedCellList(String tableName, String exceptStr){
		ArrayList<LazyDynaBean> columnTmp = new ArrayList<LazyDynaBean>();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			int colNum = 0;
			String sql = "select * from " + tableName + " where 1=2";
			rs = dao.search(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String columnItem = metaData.getColumnName(i).toLowerCase();
				if (exceptStr.indexOf("," + Strings.toLowerCase(columnItem) + ",") != -1) {
					continue;
				}
				String columnName = "";
				String codesetId = "";
				String columnType = "";
				String decimalWidth = "0";// 小数位
				if(!StringUtils.isEmpty(columnItem)){
					columnName = columnItem;
					if(DataDictionary.getFieldItem(columnItem) != null){
						columnName = DataDictionary.getFieldItem(columnItem).getItemdesc();
						codesetId = DataDictionary.getFieldItem(columnItem).getCodesetid();
						columnType = DataDictionary.getFieldItem(columnItem).getItemtype();
						decimalWidth = String.valueOf(DataDictionary.getFieldItem(columnItem).getDecimalwidth());
					}
					if(i <= (metaData.getColumnCount()-1) && (columnItem+"1").equalsIgnoreCase(metaData.getColumnName(i+1).toLowerCase())){//复合列
						
						LazyDynaBean bean = new LazyDynaBean();
						bean.set("content", columnName);// 列头名称
						bean.set("itemid", metaData.getColumnName(i+1).toLowerCase());// 列头代码
						bean.set("codesetid", codesetId);// 列头代码
						bean.set("decwidth", decimalWidth);// 列小数点后面位数
						bean.set("colType", columnType);// 该列数据类型
						HashMap<String, Object> styleMap = new HashMap<String, Object>();// 样式
						styleMap.put("fontSize", 10);// 字号
						styleMap.put("fillForegroundColor", HSSFColor.GREY_25_PERCENT.index);// 背景色
						bean.set("mergedCellStyleMap", styleMap);
						bean.set("fromRowNum", 0);// 合并单元格从那行开始
						bean.set("toRowNum", 0);// 合并单元格到哪行结束
						bean.set("fromColNum", colNum);// 合并单元格从哪列开始
						bean.set("toColNum", ++colNum);// 合并单元格从哪列结束
						columnTmp.add(bean);
						colNum += 1;//定位下次初始列
						
						i++;
						continue;
					}else{//跨行普通列
						colNum += 1;//定位下次初始列
						continue;
					}
				}else{
					continue;
				}
			}
		
		} catch (Exception ex) {
			ex.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		
		return columnTmp; 
	}
	
	/**
	 * 根据系统管理中设置的部门显示多少级，取出对应的name
	 * @param value
	 * @return
	 */
	public String getDepartName(String value) {
		String department = "";
		try {
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if (display_e0122 == null || "00".equals(display_e0122) || "".equals(display_e0122)) {
				display_e0122 = "0";
			}
			department = AdminCode.getCode("UM",value,Integer.parseInt(display_e0122))!=null?AdminCode.getCode("UM",value,Integer.parseInt(display_e0122)).getCodename():AdminCode.getCodeName("UM",display_e0122);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return department;
	}
}
