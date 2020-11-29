package com.hjsj.hrms.module.system.sdparameter.businessobject.impl;

import com.hjsj.hrms.module.system.sdparameter.businessobject.SDParameterService;
import com.hjsj.hrms.module.system.sdparameter.utils.SDParameterUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 二开参数设置业务接口实现类
 * @author wangbo 2019-09-19
 * @category hjsj
 * @version 1.0
 */
public class SDParameterServiceImpl implements SDParameterService {
	UserView userView;
	Connection conn;

	public SDParameterServiceImpl(UserView userView, Connection conn) {
		this.userView = userView;
		this.conn = conn;
	}

	@Override
	public void saveParameter(List parameter) throws GeneralException {
		try {
			ArrayList recordVoList = new ArrayList();
			ArrayList paramList = new ArrayList();
			for (int i = 0; i < parameter.size(); i++) {
				HashMap map  = PubFunc.DynaBean2Map((MorphDynaBean)parameter.get(i));
				RecordVo vo = new RecordVo("t_sys_sd_param");
				vo.setInt("id", (Integer) map.get("id"));
				vo.setString("constant", (String)map.get("constant"));
				vo.setString("describe", (String)map.get("describe"));
				vo.setString("str_value", (String)map.get("str_value"));
				recordVoList.add(vo);
				paramList.add(map);
			}
			ContentDAO dao = new ContentDAO(this.conn);
			dao.updateValueObject(recordVoList);
			SDParameterUtils.updateSDParameterHM(conn, "update", paramList);
		} catch (Exception e) {
			e.printStackTrace();
			//保存数据出错
			throw new GeneralException("saveError");
		}
	}

	@Override
	public void deleteParameter(String constants) throws GeneralException {
		try {
			// 截取字符串到字符串数组，不会出现空的情况。不需判断
			String[] paramArr = constants.split(",");
			List<String> paramList = Arrays.asList(paramArr);
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("delete from t_sys_sd_param where constant in(");
			for (int i = 0; i < paramList.size(); i++) {
				sqlBuffer.append("?,");
			}
			sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
			sqlBuffer.append(")");
			ContentDAO dao = new ContentDAO(this.conn);
			dao.delete(sqlBuffer.toString(), paramList);
			SDParameterUtils.updateSDParameterHM(conn, "delete", paramList);
		} catch (Exception e) {
			e.printStackTrace();
			//删除数据出错
			throw new GeneralException("deleteError");
		}
	}
	/**
	 * page:显示的页数
	 * pageSize:每页的数据个数
	 * */
	@Override
	public String getTableConfig(int page,int pageSize) throws GeneralException {
		String gridconfig = "";
		HashMap map = new HashMap();
		try {
			ArrayList<ButtonInfo> buttonList = new ArrayList<ButtonInfo>();
			buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.insert"), "SDParameter.addFunc"));
			buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.delete"), "SDParameter.deletFunc"));
			buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.temporary.save"), "SDParameter.saveFunc"));

			ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();
			//序号
			ColumnsInfo idColumns = getColumnsInfo("id",ResourceFactory.getProperty("system.sdparameter.id"),20,"A");
			idColumns.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(idColumns);
			// 名称列
			ColumnsInfo nameColumns = getColumnsInfo("constant", ResourceFactory.getProperty("system.sdparameter.nameCol"), 200, "A");
			nameColumns.setColumnLength(50);
			nameColumns.setValidFunc("SDParameter.validNameFunc");
			columnTmp.add(nameColumns);
			// 值列
			ColumnsInfo str_valueColumns = getColumnsInfo("str_value", ResourceFactory.getProperty("system.sdparameter.valueCol"), 700,"M");
			columnTmp.add(str_valueColumns);
			// 描述列
			ColumnsInfo describeColumns = getColumnsInfo("describe", ResourceFactory.getProperty("system.sdparameter.descCol"), 500,"M");
			columnTmp.add(describeColumns);

			TableConfigBuilder builder = new TableConfigBuilder("mainGrid", columnTmp, "mainGrid", this.userView, this.conn);

			builder.setTitle(ResourceFactory.getProperty("system.sdparameter.title"));
			String dataSql = "select id,constant,str_value,describe from t_sys_sd_param";
			builder.setDataSql(dataSql);
			builder.setSelectable(true);
			builder.setEditable(true);
			builder.setPageSize(pageSize);
			builder.setCurrentPage(page);
			builder.setOrderBy("order by id");
			builder.setSortable(false);
			builder.setTableTools(buttonList);
			gridconfig = builder.createExtTableConfig();
		} catch (Exception e) {
			e.printStackTrace();
			//初始化数据出错！
			throw new GeneralException("renderError");
		}
		return gridconfig;
	}
	/**
	 * 获取列头
	 * @param columnId 列id
	 * @param columnDesc 列描述
	 * @param columnWidth 列宽
	 * @param columnType 列类型
	 * @return 列主要属性
	 */
	private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth, String columnType) {
		ColumnsInfo columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId(columnId);
		columnsInfo.setColumnDesc(columnDesc);
		columnsInfo.setColumnWidth(columnWidth);
		columnsInfo.setColumnType(columnType);
		if ("A".equals(columnType)) {
			columnsInfo.setCodesetId("0");
		} else if ("D".equals(columnType)) {
			columnsInfo.setTextAlign("right");
		}
		return columnsInfo;
	}

	@Override
	public HashMap getSDParameter() throws GeneralException {
		return SDParameterUtils.getSDParameterHM(conn);
	}

	/**
	 * 获取数据库最大的序号
	 * */
	private int getMaxParamId() {
		int count = SDParameterUtils.getCount(conn);
		return count;
	}

	/**
	 * 获取数据库中数据总数
	 * */
	private int getCountData() throws GeneralException{
		int countData = SDParameterUtils.getDataCount(conn);
		return countData;
	}

	/**
	 * 插入数据，返回数据所在页数
	 * */
	@Override
	public int insertParamData(int pageSize) throws GeneralException {
		/*获取当前系统时间*/
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String createTime = sdf.format(new Date());
		String sql = "";
		int showPage = 0;
		int maxParamId = getMaxParamId();
		maxParamId+=1;
		ArrayList insertList = new ArrayList();//要插入的数据
		String name;
		try {
			name = ResourceFactory.getProperty("system.sdparameter.name")+maxParamId;
			insertList.add(maxParamId);
			insertList.add(name);
			sql = "insert into t_sys_sd_param (id,constant) values(?,?)";
			ContentDAO dao = new ContentDAO(conn);
			dao.insert(sql,insertList);
			int countData = getCountData();
			showPage = countData%pageSize==0?countData/pageSize:countData/pageSize+1;
			insertList.clear();
			HashMap map = new HashMap();
			map.put("id", maxParamId);
			map.put("constant", name);
			insertList.add(map);
			SDParameterUtils.updateSDParameterHM(conn, "add", insertList);
		}catch (Exception e){
			e.printStackTrace();
		}
		return showPage;
	}

}