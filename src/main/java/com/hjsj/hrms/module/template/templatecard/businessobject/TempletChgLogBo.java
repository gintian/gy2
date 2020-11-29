package com.hjsj.hrms.module.template.templatecard.businessobject;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.SubField;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.module.template.utils.javabean.TemplateSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class TempletChgLogBo {
	private String log_id;
	private String objetid;
	private String a0101;
	private String only_value;
	private Integer info_type;
	private Integer opt_type;
	private Integer ins_id;
	private Integer task_id;
	private Integer tabid;
	private Integer pageid;
	private String subflag;
	private String setname;
	private String field_id;
	private String field_name;
	private String sub_content;
	private String content_1;
	private String content_2;
	private String record_key_id;
	private Date createtime;
	private String createuser;
	private UserView userView = null;
	private Connection conn = null;
	private TemplateParam param = null;
	private TemplateUtilBo utilBo = null;

	public TempletChgLogBo(Connection conn, UserView userView) {
		this.conn = conn;
		this.userView = userView;
		this.utilBo = new TemplateUtilBo(conn, userView);
		initIdFactiory();
	}

	public TempletChgLogBo(Connection conn, UserView userView, TemplateParam param) {
		this.conn = conn;
		this.userView = userView;
		this.param = param;
		this.utilBo = new TemplateUtilBo(conn, userView);
		//58006 V771包：审批人sx登陆，首页待办705模板更改变化后指标信息之后，内容未变色显示
		//原因：表号为空 以致审批人对 ins_id 都设置成0了。
		this.tabid=param==null?null:param.getTabId();
		initIdFactiory();
	}

	/**
	 * 判断序号生成器中是否已添加，没有就插入。
	 */
	public void initIdFactiory() {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			rowSet = dao.search("select 1 from id_factory where sequence_name='templet_chg_log.log_id'");
			if (!rowSet.next()) {
				StringBuffer insertSQL = new StringBuffer();
				insertSQL.append(
						"insert into id_factory  (sequence_name, sequence_desc, minvalue, maxvalue, auto_increase, increase_order, prefix, suffix, currentid, id_length, increment_O)");
				insertSQL
						.append(" values ('templet_chg_log.log_id', '日志记录序号', 1, 99999999, 1, 1, Null, Null, 0, 8, 1)");
				ArrayList list = new ArrayList();
				dao.insert(insertSQL.toString(), list);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rowSet);
		}
	}

	public String getLog_id() {
		return log_id;
	}

	public void setLog_id(String log_id) {
		this.log_id = log_id;
	}

	public String getObjetid() {
		return objetid;
	}

	public void setObjetid(String objetid) {
		this.objetid = objetid;
	}

	public String getA0101() {
		return a0101;
	}

	public void setA0101(String a0101) {
		this.a0101 = a0101;
	}

	public String getOnly_value() {
		return only_value;
	}

	public void setOnly_value(String only_value) {
		this.only_value = only_value;
	}

	public Integer getInfo_type() {
		return info_type;
	}

	public void setInfo_type(Integer info_type) {
		this.info_type = info_type;
	}

	public Integer getOpt_type() {
		return opt_type;
	}

	public void setOpt_type(Integer opt_type) {
		this.opt_type = opt_type;
	}

	public Integer getIns_id() {
		return ins_id;
	}

	public void setIns_id(Integer ins_id) {
		this.ins_id = ins_id;
	}

	public Integer getTask_id() {
		return task_id;
	}

	public void setTask_id(Integer task_id) {
		this.task_id = task_id;
	}

	public Integer getTabid() {
		return tabid;
	}

	public void setTabid(Integer tabid) {
		this.tabid = tabid;
	}

	public Integer getPageid() {
		return pageid;
	}

	public void setPageid(Integer pageid) {
		this.pageid = pageid;
	}

	public String getSubflag() {
		return subflag;
	}

	public void setSubflag(String subflag) {
		this.subflag = subflag;
	}

	public String getSetname() {
		return setname;
	}

	public void setSetname(String setname) {
		this.setname = setname;
	}

	public String getField_id() {
		return field_id;
	}

	public void setField_id(String field_id) {
		this.field_id = field_id;
	}

	public String getField_name() {
		return field_name;
	}

	public void setField_name(String field_name) {
		this.field_name = field_name;
	}

	public String getSub_content() {
		return sub_content;
	}

	public void setSub_content(String sub_content) {
		this.sub_content = sub_content;
	}

	public String getContent_1() {
		return content_1;
	}

	public void setContent_1(String content_1) {
		this.content_1 = content_1;
	}

	public String getContent_2() {
		return content_2;
	}

	public void setContent_2(String content_2) {
		this.content_2 = content_2;
	}

	public String getRecord_key_id() {
		return record_key_id;
	}

	public void setRecord_key_id(String record_key_id) {
		this.record_key_id = record_key_id;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public String getCreateuser() {
		return createuser;
	}

	public void setCreateuser(String createuser) {
		this.createuser = createuser;
	}

	/**
	 * 把对应任务的变动日志归档到年度表。
	 * 
	 * @param ins_id
	 *            实例id
	 */
	public void insertChangeInfoToYearTable(String ins_id) {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			this.createTemplateChgLogTable("templet_chg_log");// 判断临时表是否存在
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("YYYY");
			String currentYear = sdf.format(date);
			createTemplateChgLogTable("templet_chg_log" + currentYear);// 判断年度表是否存在
			String sql = "insert into templet_chg_log" + currentYear
					+ "  select  *  from  templet_chg_log where ins_id=" + ins_id;// 把临时表中实例数据插入到年度表中
			dao.update(sql);
			ArrayList paramList = new ArrayList();
			paramList.add(ins_id);
			sql = "delete  from  templet_chg_log where ins_id=?";// 删除临时表中的数据
			dao.delete(sql, paramList);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * 把起草插入的变动日志ins_id从0更新为流程中的ins_id
	 * 
	 * @param personList
	 *            人员信息 nbase`a0100,机构 B0110或者e01a1
	 * @param task_id
	 *            0
	 * @param tabid
	 *            表单号
	 * @param new_ins_id
	 *            进入流程后得到的ins_id
	 * @param info_type
	 *            类型 1人员 2、3机构
	 */
	public void updateChangeInfoAddIns_id(ArrayList personList, String task_id, String tabid, String new_ins_id,
			int info_type) {
		this.createTemplateChgLogTable("templet_chg_log");
		String ins_id = "0";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			ArrayList allParamList = new ArrayList();
			String sql = "update templet_chg_log set ins_id=? where tabid=? and ins_id=? and lower(objectid)=? and createUser=?";
			for (int i = 0; i < personList.size(); i++) {
				ArrayList list = (ArrayList) personList.get(i);
				String objectid = "";
				if (info_type == 1) {
					String basepre = (String) list.get(0);
					String a0100 = (String) list.get(1);
					objectid = basepre.toLowerCase() + a0100.toLowerCase();
				} else {
					objectid = (String) list.get(0);
				}
				ArrayList paramList = new ArrayList();
				paramList.add(new_ins_id);
				paramList.add(tabid);
				paramList.add(ins_id);
				paramList.add(objectid);
				paramList.add((StringUtils.isBlank(this.userView.getUserFullName()) ? this.userView.getUserName()
						: this.userView.getUserFullName()).toLowerCase());
				allParamList.add(paramList);
			}
			if (allParamList.size() > 0)
				dao.batchUpdate(sql, allParamList);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	/**
	 * 或得子集指标或者指标的变动信息
	 * @param object_id 人员信息 nbase`a0100,机构 B0110或者e01a1
	 * @param task_id 
	 * @param tabid
	 * @return 指标的变动信息
	 */
	public HashMap getFieldChangeInfoList(String object_id, String task_id, String tabid) {
		this.createTemplateChgLogTable("templet_chg_log");
		HashMap map = new HashMap();
		RowSet rowset = null;
		String ins_id = "0";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			String tablename = "templet_chg_log";
			if (!"0".equalsIgnoreCase(task_id)) {
				String year = "";
				String sql = "select twt.ins_id,"+Sql_switcher.year("twi.end_date")+" year from t_wf_task twt,t_wf_instance twi where twt.ins_id=twi.ins_id and twt.task_id='" + task_id + "'";
				rowset = dao.search(sql);
				if (rowset.next()) {
					ins_id = rowset.getString("ins_id");
					year = rowset.getString("year")==null?"":rowset.getString("year");
				}
				TemplateBo templateBo= new TemplateBo(this.conn,this.userView,Integer.parseInt(tabid));
				boolean isFinishTask= templateBo.isFinishedTask(task_id);
				if(isFinishTask) {
					tablename +=year;
				}
			}
			String sql = "select pageid,field_id from "+tablename+" where tabid=? and ins_id=? and objectid=? and subflag='0'";
			ArrayList paramList = new ArrayList();
			if ("0".equalsIgnoreCase(task_id)) {
				sql = "select pageid,field_id from "+tablename+" where lower(createuser)=? and tabid=? and ins_id=? and objectid=? and subflag='0'";
				paramList.add(StringUtils.isBlank(this.userView.getUserFullName()) ? this.userView.getUserName()
						: this.userView.getUserFullName().toLowerCase());
			}
			paramList.add(tabid);
			paramList.add(ins_id);
			paramList.add(object_id.replace("`", ""));
			rowset = dao.search(sql, paramList);
			while (rowset.next()) {
				map.put(rowset.getString("field_id"), rowset.getString("field_id"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rowset);
		}

		return map;
	}
	
	/**
	 * 或得子集指标或者指标的变动信息
	 * @param taskids 
	 * @param tabid
	 * @return 指标的变动信息
	 */
	public ArrayList getFieldChangeInfo(String taskids, String tabid) {
		this.createTemplateChgLogTable("templet_chg_log");
		ArrayList list = new ArrayList();
		RowSet rowset = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			String taskid = "";
			if("0".equals(taskids)) taskid="0";
			String[] taskarr=StringUtils.split(taskids,",");
			for(int i=0;i<taskarr.length;i++)
			{
				taskid+=taskarr[i]+",";
			}
			if(taskid.length()>0) {
				taskid = taskid.substring(0,taskid.length()-1);
			}
			String sql = "select objectid,field_id,ins_id,task_id,pageid from templet_chg_log where tabid=? and task_id in ("+taskid+") and subflag='0'";
			ArrayList paramList = new ArrayList();
			if ("0".equals(taskid)) {
				sql = "select objectid,field_id,ins_id,task_id,pageid from templet_chg_log where lower(createuser)=? and tabid=? and task_id in ("+taskid+") and subflag='0'";
				paramList.add(StringUtils.isBlank(this.userView.getUserFullName()) ? this.userView.getUserName()
						: this.userView.getUserFullName().toLowerCase());
			}
			paramList.add(tabid);
			rowset = dao.search(sql, paramList);
			while (rowset.next()) {
				String field_id = rowset.getString("field_id");
				String pageid = rowset.getString("pageid");
				String objectid = rowset.getString("objectid");
				String ins_id = rowset.getString("ins_id");
				String task_id = rowset.getString("task_id");
				list.add(objectid+":"+ins_id+":"+task_id+":"+field_id+"_2");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rowset);
		}
		return list;
	}
	/**
	 * 获取子集的变动信息
	 * @param object_id 人员信息 nbase`a0100,机构 B0110或者e01a1
	 * @param ins_id  
	 * @param tabid   表单号
	 * @param setname 子集字段名，t_a04_2\t_a04_2_2
	 * @return
	 */
	public String getSubsetChgLogInfo(String object_id, String ins_id, String tabid, String setname) {
		this.createTemplateChgLogTable("templet_chg_log");
		StringBuffer str = new StringBuffer();
		RowSet rowset = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			ArrayList paramList = new ArrayList();
			String tablename = "templet_chg_log";
			if (StringUtils.isBlank(ins_id)||"0".equals(ins_id)) {
				ins_id = "0";
			}else{
				//syl 58127 人事异动表单22号，设置自动记录变动日志，点击计算，学历学位子集中学制变红，提交表单，在任务监控已结束中查看表单，学制没有变红
				String year = "";
				String task_id="";
				String sql = "select twt.task_id,"+Sql_switcher.year("twi.end_date")+" year from t_wf_task twt,t_wf_instance twi where twt.ins_id=twi.ins_id and twi.ins_id=? order by twt.task_id desc";
				paramList.add(ins_id);
				rowset = dao.search(sql, paramList);
				if (rowset.next()) {
					task_id = rowset.getString("task_id");
					year = rowset.getString("year")==null?"":rowset.getString("year");
					TemplateBo templateBo= new TemplateBo(this.conn,this.userView,Integer.parseInt(tabid));
					boolean isFinishTask= templateBo.isFinishedTask(task_id);
					if(isFinishTask) {
						tablename +=year;
					}
				}
			}
			
			String sql = "select record_key_id,field_id from "+tablename+" where tabid=? and ins_id=? and objectid=? and subflag='1' and setname=?";
			paramList.clear();
			paramList.add(tabid);
			paramList.add(ins_id);
			paramList.add(object_id.replace("`", ""));
			paramList.add(setname);
			rowset = dao.search(sql, paramList);
			while (rowset.next()) {
				str.append(rowset.getString("record_key_id") + ":" + rowset.getString("field_id") + ";");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rowset);
		}

		return str.toString();
	}
	/**
	 * 获取变动日志列信息
	 * @return
	 */
	public ArrayList<ColumnsInfo> getChangeLogColumns() {
		ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
		try {
			FieldItem item = new FieldItem();
			item.setItemid("opt_type");
			item.setItemdesc(ResourceFactory.getProperty("template.changeLog.state"));// 状态
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(10);
			item.setCodesetid("0");
			ColumnsInfo info = new ColumnsInfo(item);
			info.setRendererFunc("ChangeLogScope.optTypeRenered");
			info.setColumnWidth(60);// 显示列宽
			list.add(info);

			item = new FieldItem();
			item.setItemid("a0101");
			item.setItemdesc(ResourceFactory.getProperty("template.changeLog.personName"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setColumnWidth(100);// 显示列宽
			list.add(info);

			item = new FieldItem();
			item.setItemid("tablename");
			item.setItemdesc(ResourceFactory.getProperty("template.changeLog.tableName"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setColumnWidth(200);// 显示列宽
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("setname");
			item.setItemdesc(ResourceFactory.getProperty("template.changeLog.area"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setColumnWidth(50);// 显示列宽
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			list.add(info);

			item = new FieldItem();
			item.setItemid("subflag");
			item.setItemdesc(ResourceFactory.getProperty("template.changeLog.area"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setColumnWidth(50);// 显示列宽
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			list.add(info);

			item = new FieldItem();
			item.setItemid("sub_content");
			item.setItemdesc(ResourceFactory.getProperty("template.changeLog.area"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(200);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setColumnWidth(50);// 显示列宽
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			list.add(info);

			item = new FieldItem();
			item.setItemid("fieldsetdesc");
			item.setItemdesc(ResourceFactory.getProperty("template.changeLog.area"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setColumnWidth(150);// 显示列宽
			info.setRendererFunc("ChangeLogScope.setNameRenered");
			list.add(info);

			item = new FieldItem();
			item.setItemid("field_name");
			item.setItemdesc(ResourceFactory.getProperty("template.changeLog.fieldName"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setColumnWidth(150);// 显示列宽
			list.add(info);

			item = new FieldItem();
			item.setItemid("content_1");
			item.setItemdesc(ResourceFactory.getProperty("template.changeLog.beforChange"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setColumnWidth(100);// 显示列宽
			list.add(info);

			item = new FieldItem();
			item.setItemid("content_2");
			item.setItemdesc(ResourceFactory.getProperty("template.changeLog.afterChange"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(100);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setColumnWidth(100);// 显示列宽
			list.add(info);

			item = new FieldItem();
			item.setItemid("createtime");
			item.setItemdesc(ResourceFactory.getProperty("template.changeLog.time"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setCodesetid("0");
			item.setItemlength(50);
			info = new ColumnsInfo(item);
			info.setColumnWidth(150);// 显示列宽
			list.add(info);

			item = new FieldItem();
			item.setItemid("createuser");
			item.setItemdesc(ResourceFactory.getProperty("template.changeLog.operator"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setColumnWidth(100);// 显示列宽
			list.add(info);

			item = new FieldItem();
			item.setItemid("log_id");
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setColumnWidth(50);// 显示列宽
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 只加载数据

			list.add(info);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}
	/**
	 * 获取全部有权限的字段，用于下拉查询
	 * @param searchSql 查询sql语句
	 * @return
	 */
	public ArrayList getTemplateFieldList(String searchSql) {
		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("id", "-1");
			bean.set("name", "全部");
			list.add(bean);
			rowSet = dao.search(searchSql);
			StringBuffer tabidStr = new StringBuffer();
			while (rowSet.next()) {
				String name = rowSet.getString("field_name");
				if (StringUtils.isNotBlank(name)) {
					bean = new LazyDynaBean();
					bean.set("id", rowSet.getString("field_id"));
					bean.set("name", rowSet.getString("field_name"));
					list.add(bean);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rowSet);
		}
		return list;
	}

	/**
	 * 获取全部有权限的表名，用于下拉查询
	 * 
	 * @param searchSql
	 * @return
	 */
	public ArrayList getTemplateTableList(String searchSql) {
		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("id", "-1");
			bean.set("name", "全部");
			list.add(bean);
			rowSet = dao.search(searchSql);
			StringBuffer tabidStr = new StringBuffer();
			while (rowSet.next()) {
				tabidStr.append(rowSet.getString("tabid")).append(",");
			}
			if (tabidStr.length() > 0) {
				StringBuffer sql = new StringBuffer();
				sql.append("select   tabid,  name  from   Template_table   ");
				sql.append(" where tabid in (" + tabidStr.substring(0, tabidStr.length() - 1) + ")  order by tabid");
				rowSet = dao.search(sql.toString());
				while (rowSet.next()) {
					bean = new LazyDynaBean();
					bean.set("id", rowSet.getString("tabid"));
					bean.set("name", rowSet.getString("name"));
					list.add(bean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rowSet);
		}

		return list;
	}
	/**
	 * 插入或者更新全部字段和子集的变动日志。
	 * @param fileNamelist        修改了值的字段列表
	 * @param setBoList			  
	 * @param upDateValueList     更新的字段值
	 * @param ins_id 
	 * @param task_id
	 * @param objectId            nbase`a0100或者b0110或者e01a1
	 * @param tableName			      表明 templet_tabid或者g_templet_tabid或者用户名templet_tabid
	 * @param info_type			     人员或者机构
	 */
	public void insertOrUpdateAllLogger(ArrayList fileNamelist, ArrayList setBoList, ArrayList upDateValueList,
			String ins_id, String task_id, String objectId, String tableName, int info_type) {
		String sql = "";
		String a0101 = "";
		String onlyValue = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		HashMap fieldMap = new HashMap();
		HashMap valueMap = new HashMap();
		try {
			this.createTemplateChgLogTable("templet_chg_log");
			IDGenerator idg = new IDGenerator(2, this.conn);
			if ("0".equalsIgnoreCase(ins_id)) {
				sql = "select log_id,pageid,subflag,field_id,record_key_id,setname,opt_type,content_1,content_2,ins_id,task_id  from templet_chg_log where ins_id='"
						+ ins_id + "' and task_id in (" + task_id + ") and lower(objectid)='"
						+ objectId.replace("`", "").toLowerCase() + "' and lower(Createuser)='"
						+ (StringUtils.isBlank(this.userView.getUserFullName()) ? this.userView.getUserName()
								: this.userView.getUserFullName()).toLowerCase()
						+ "'  ";
			} else {
				sql = "select log_id,pageid,subflag,field_id,record_key_id,setname,opt_type,content_1,content_2,ins_id,task_id  from templet_chg_log where ( ins_id in ("
						+ ins_id + ") and task_id in(" + task_id + ") and lower(objectid)='"
						+ objectId.replace("`", "").toLowerCase() + "' and opt_type<>3 ) or( ins_id in (" + ins_id
						+ ") and opt_type=3 )";
			}
			String sqlFieldsin = "";
			String sqlSubsetin = "";
			String sqlin = "";
			for (int i = 0; i < fileNamelist.size(); i++) {
				String fieldname = (String) fileNamelist.get(i);
				if (fieldname.toLowerCase().startsWith("t")) {
					sqlSubsetin += fieldname + ",";
				} else {
					if (fieldname.indexOf("start_date") != -1) {
						sqlFieldsin += "start_date,";
					} else if (fieldname.indexOf("codesetid") != -1) {
						sqlFieldsin += "codesetid,";
					} else if (fieldname.indexOf("codeitemdesc") != -1) {
						sqlFieldsin += "codeitemdesc,";
					} else if (fieldname.indexOf("corcode") != -1) {
						sqlFieldsin += "corcode,";
					} else if (fieldname.indexOf("parentid") != -1) {
						sqlFieldsin += "parentid,";
					} else if (fieldname.indexOf("_") != -1) {
						sqlFieldsin += fieldname.substring(0, fieldname.indexOf("_")) + ",";
					} else {
						sqlFieldsin += fieldname + ",";

					}
				}
				sqlin += fieldname + ",";
			}
			sql += " and (";
			if (sqlFieldsin.trim().length() > 0) {
				sql += " ( field_id in ( '" + sqlFieldsin.substring(0, sqlFieldsin.length() - 1).replace(",", "','")
						+ "' ) and subflag=0 )";
			}
			if (sqlSubsetin.trim().length() > 0) {
				if (StringUtils.isNotBlank(sqlFieldsin)) {
					sql += " or ";
				}
				sql += " (subflag=1 and setname in ('"
						+ sqlSubsetin.substring(0, sqlSubsetin.length() - 1).replace(",", "','") + "')  ) ";
			}
			sql += ")";

			rowSet = dao.search(sql);
			while (rowSet.next()) {
				String log_id = rowSet.getString("log_id");
				String pageid = rowSet.getString("pageid");
				String subflag = rowSet.getString("subflag");
				String field_id = rowSet.getString("field_id");
				String record_key_id = rowSet.getString("record_key_id");
				String setname = rowSet.getString("setname");
				String opt_type = rowSet.getString("opt_type");
				String content_1 =Sql_switcher.readMemo(rowSet, "content_1");
				String content_2 =Sql_switcher.readMemo(rowSet, "content_2");
				HashMap map = new HashMap();
				map.put("log_id", log_id);
				map.put("pageid", pageid);
				map.put("subflag", subflag);
				map.put("record_key_id", record_key_id);
				map.put("field_id", field_id);
				map.put("setname", setname);
				map.put("opt_type", opt_type);
				map.put("content_1", content_1);
				map.put("content_2", content_2);

				if ("1".equalsIgnoreCase(subflag)) {
					if ("3".equalsIgnoreCase(opt_type)) {
						fieldMap.put(record_key_id + ":" + field_id + ":" + opt_type, map);
					} else {
						fieldMap.put(record_key_id + ":" + field_id, map);
					}
				} else {
					fieldMap.put(field_id.toLowerCase() + "_2", map);
				}
			}
			if (sqlin.toLowerCase().indexOf("a0101_1") == -1 && info_type == 1) {
				sqlin += "a0101_1,";
			}
			if(!tableName.equalsIgnoreCase("templet_"+this.tabid)) {
				sql = " select " + sqlin.substring(0, sqlin.length() - 1) + ",0 as ins_id,0 as task_id from " + tableName + " where ";
			}else {
				sql = " select " + sqlin.substring(0, sqlin.length() - 1) + ",ins_id,task_id from " + tableName + " where ";
			}
			String sqlObjectName = "";
			if (info_type == 1) {
				String[] objectids = objectId.split("`");
				String basepre = objectids[0];
				String a0100 = objectids[1];
				Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
				String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
				String valid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");
				if (this.param.getOperationType() == 0) {
					DbWizard dbwizard = new DbWizard(this.conn);
					sqlObjectName = "select a0101_1 a0101 from " + tableName + " where A0100='" + a0100
							+ "' and BasePre='" + basepre + "' ";
					if (!"0".equals(valid)) {
						if (dbwizard.isExistField(tableName, onlyname + "_2", false)) {
							sqlObjectName = "select a0101_1 a0101," + onlyname + "_2 " + onlyname + " from " + tableName
									+ " where A0100='" + a0100 + "' and BasePre='" + basepre + "' ";
						} else {
							sqlObjectName = "select a0101_1 a0101,'' " + onlyname + " from " + tableName
									+ " where A0100='" + a0100 + "' and BasePre='" + basepre + "' ";
						}
					}
					if (!"0".equalsIgnoreCase(ins_id)) {
						sqlObjectName += " and ins_id in (" + ins_id + ")";
					}
					sql += " A0100='" + a0100 + "'  and BasePre='" + basepre + "'";
				} else {
					sqlObjectName = "select a0101 from " + basepre + "A01 " + " where A0100='" + a0100 + "' ";
					if (!"0".equals(valid)) {
						sqlObjectName = "select a0101," + onlyname + " from " + basepre + "A01 " + " where A0100='"
								+ a0100 + "'";
					}
					sql += " A0100='" + a0100 + "' and BasePre='" + basepre + "'";
				}
				rowSet = dao.search(sqlObjectName);
				if (rowSet.next()) {
					a0101 = rowSet.getString("a0101");
					if (!"0".equals(valid)) {
						onlyValue = rowSet.getString(onlyname);
					}
				}
			} else if (info_type == 2) {
				if (this.param.getOperationType() == 5||this.param.getOperationType() == 8) {
					DbWizard dbwizard = new DbWizard(this.conn);
					sqlObjectName = "select codeitemdesc_1 a0101 from " + tableName + " where b0110='" + objectId
							+ "' ";
					if (!"0".equalsIgnoreCase(ins_id)) {
						sqlObjectName += " and ins_id='" + ins_id + "'";
					}
					rowSet = dao.search(sqlObjectName);
					if (rowSet.next()) {
						a0101 = rowSet.getString("a0101");
					}
				} else {
					a0101 = AdminCode.getCode("UN", objectId) == null
							? (AdminCode.getCode("UM", objectId) == null ? ""
									: AdminCode.getCode("UM", objectId).getCodename())
							: AdminCode.getCode("UN", objectId).getCodename();

				}
				sql += " b0110='" + objectId + "'";
			} else if (info_type == 3) {
				sql += " e01a1='" + objectId + "'";
				if (this.param.getOperationType() == 5||this.param.getOperationType() == 8) {
					DbWizard dbwizard = new DbWizard(this.conn);
					sqlObjectName = "select codeitemdesc_1 a0101 from " + tableName + " where e01a1='" + objectId
							+ "' ";
					if (!"0".equalsIgnoreCase(ins_id)) {
						sqlObjectName += " and ins_id in (" + ins_id + ")";
					}
					rowSet = dao.search(sqlObjectName);
					if (rowSet.next()) {
						a0101 = rowSet.getString("a0101");
					}
				} else {
					a0101 = AdminCode.getCode("@K", objectId) == null ? ""
							: AdminCode.getCode("@K", objectId).getCodename();
				}
			}
			if (!"0".equals(ins_id)) {
				sql += " and ins_id in (" + ins_id + ")";
			}

			rowSet = dao.search(sql);
			if (rowSet.next()) {
				int ins_id_tem=rowSet.getInt("ins_id");
				int task_id_tem=rowSet.getInt("task_id");
				ArrayList insertParamList = new ArrayList();//插入新日志记录
				ArrayList updateParamList = new ArrayList();//更新日志记录
				ArrayList deleteEditParamList = new ArrayList();//删除"编辑"和"新增"类型的日志记录
				ArrayList deleteDelParamList = new ArrayList();//删除"删除"类型日志的记录
				ArrayList deleteByLogIdParamList = new ArrayList();
				String insertSql = "insert into templet_chg_log (LOG_ID,OBJECTID,A0101,ONLY_VALUE,INFO_TYPE,OPT_TYPE,INS_ID,TASK_ID,TABID,PAGEID,SUBFLAG,SETNAME,FIELD_ID,FIELD_NAME,SUB_CONTENT,CONTENT_1,CONTENT_2,RECORD_KEY_ID,CREATETIME,CREATEUSER)  values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				String updateSql = "update templet_chg_log set content_2=?,createtime=? where log_id=?";
				String deleteEditSql = "delete from  templet_chg_log where record_key_id=?  and ins_id=? and opt_type<>3 ";
				String deleteDelSql = "delete from  templet_chg_log where record_key_id=?  and ins_id=? and  (opt_type=3 and createuser=?)";
				String deleteByLogIdSql = "delete from  templet_chg_log where log_id=?";
				for (int i = 0; i < fileNamelist.size(); i++) {
					TemplateSet setBo = (TemplateSet) setBoList.get(i);
					if("S".equalsIgnoreCase(setBo.getFlag())||"M".equalsIgnoreCase(setBo.getOld_fieldType())){
						continue;
					}
					String fieldName = (String) fileNamelist.get(i);
					String fieldNameSave = fieldName;
					if ("a0101_1".equalsIgnoreCase(fieldName) || "codeitemdesc_1".equalsIgnoreCase(fieldName)) {
						continue;
					}
					if (!setBo.isSubflag()) {
						fieldNameSave = fieldName;
						if (fieldName.indexOf("start_date") != -1) {
							fieldNameSave = "start_date";
						} else if (fieldName.indexOf("codesetid") != -1) {
							fieldNameSave = "codesetid";
						} else if (fieldName.indexOf("codeitemdesc") != -1) {
							fieldNameSave = "codeitemdesc";
						} else if (fieldName.indexOf("corcode") != -1) {
							fieldNameSave = "corcode";
						} else if (fieldName.indexOf("parentid") != -1) {
							fieldNameSave = "parentid";
						} else if (fieldName.indexOf("_") != -1) {
							fieldNameSave = fieldName.substring(0, fieldName.indexOf("_"));
						}
					}
					String oldValue = "";
					String value = "";
					if ("D".equalsIgnoreCase(setBo.getField_type())) {
						Date date = rowSet.getDate(fieldName);
						if (date == null) {
							oldValue = "";
						} else {
							oldValue = utilBo.getFormatDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date),
									setBo.getDisformat());
						}
						Date newDate = (Date) upDateValueList.get(i);
						if (newDate == null) {
							value = "";
						} else {
							value = utilBo.getFormatDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(newDate),
									setBo.getDisformat());
						}
					} else if (setBo.isSubflag()) {
						oldValue = Sql_switcher.readMemo(rowSet, fieldName);
						value = String.valueOf(upDateValueList.get(i)==null?"":upDateValueList.get(i));
					} else {
						oldValue = rowSet.getString(fieldName);
						value = String.valueOf(upDateValueList.get(i)==null?"":upDateValueList.get(i));
					}
					if ("codesetid".equalsIgnoreCase(fieldNameSave)) {
						oldValue = "UM".equalsIgnoreCase(oldValue) ? "部门"
								: "UN".equalsIgnoreCase(oldValue) ? "单位" : oldValue;
						value = "UM".equalsIgnoreCase(value) ? "部门" : "UN".equalsIgnoreCase(value) ? "单位" : value;
					}
					Boolean isInsert = true;
					if (setBo.isSubflag()) {
						ArrayList list = compareSubsetValues(oldValue, value,setBo);
						for (int j = 0; j < list.size(); j++) {
							HashMap map = (HashMap) list.get(j);
							String opt_type = (String) map.get("opt_type");
							if ("1".equalsIgnoreCase(opt_type)) {
								ArrayList isertList = new ArrayList();
								isertList.add(idg.getId("templet_chg_log.log_id"));
								isertList.add(objectId.replace("`", ""));
								isertList.add(a0101);
								isertList.add(onlyValue);
								isertList.add(info_type == 3 ? 2 : info_type);
								isertList.add(1);
								isertList.add(ins_id_tem);
								isertList.add(task_id_tem);
								isertList.add(setBo.getTabId());
								isertList.add(setBo.getPageId());
								isertList.add("1");
								isertList.add(setBo.getTableFieldName().toLowerCase());
								isertList.add((String) map.get("field_id"));
								isertList.add((String) map.get("field_name"));
								isertList.add((String) map.get("sub_content"));
								isertList.add((String) map.get("content_1"));
								isertList.add((String) map.get("content_2"));
								isertList.add((String) map.get("record_key_id"));
								isertList.add(new java.sql.Timestamp(new Date().getTime()));
								isertList.add((StringUtils.isBlank(this.userView.getUserFullName())
										? this.userView.getUserName() : this.userView.getUserFullName()).toLowerCase());
								insertParamList.add(isertList);
							} else if ("2".equalsIgnoreCase(opt_type)) {
								String record_key_id = (String) map.get("record_key_id");
								String field_id = (String) map.get("field_id");
								String content_2 = (String) map.get("content_2");
								Boolean isNew = true;
								if (fieldMap.containsKey(record_key_id + ":" + field_id)) {
									HashMap submap = (HashMap) fieldMap.get(record_key_id + ":" + field_id);
									if (field_id.equalsIgnoreCase((String) submap.get("field_id"))) {
										String log_id = (String) submap.get("log_id");
										String content_1 = (String) submap.get("content_1");
										String opt_type2 = (String) submap.get("opt_type");
										if(StringUtils.isBlank(content_1)){
											content_1="";
										}
										if (content_1.equalsIgnoreCase(content_2) && "2".equalsIgnoreCase(opt_type2)) {
											ArrayList deleteList = new ArrayList();
											deleteList.add(log_id);
											deleteByLogIdParamList.add(deleteList);
											isNew = false;
										} else {
											ArrayList updateList = new ArrayList();
											updateList.add(content_2);
											updateList.add(new java.sql.Timestamp(new Date().getTime()));
											updateList.add(log_id);
											updateParamList.add(updateList);
											isNew = false;
										}
									}
								}
								if (isNew) {
									ArrayList isertList = new ArrayList();
									isertList.add(idg.getId("templet_chg_log.log_id"));
									isertList.add(objectId.replace("`", ""));
									isertList.add(a0101);
									isertList.add(onlyValue);
									isertList.add(info_type == 3 ? 2 : info_type);
									isertList.add(2);
									isertList.add(ins_id_tem);
									isertList.add(task_id_tem);
									isertList.add(setBo.getTabId());
									isertList.add(setBo.getPageId());
									isertList.add("1");
									isertList.add(setBo.getTableFieldName().toLowerCase());
									isertList.add((String) map.get("field_id"));
									isertList.add((String) map.get("field_name"));
									isertList.add((String) map.get("sub_content"));
									isertList.add((String) map.get("content_1"));
									isertList.add((String) map.get("content_2"));
									isertList.add((String) map.get("record_key_id"));
									isertList.add(new java.sql.Timestamp(new Date().getTime()));
									isertList.add((StringUtils.isBlank(this.userView.getUserFullName())
											? this.userView.getUserName() : this.userView.getUserFullName())
													.toLowerCase());
									insertParamList.add(isertList);
								}
							} else if ("3".equalsIgnoreCase(opt_type)) {
								// 新增日志记录
								String record_key_id = (String) map.get("record_key_id");
								String field_id = (String) map.get("field_id");
								if (StringUtils.isBlank(field_id)) {
									field_id = "";
								}
								if (!fieldMap.containsKey(record_key_id + ":" + field_id + ":3")) {
									Boolean isNewInsert = (Boolean) map.get("isNewInsert");
									if(!isNewInsert){
										ArrayList isertList = new ArrayList();
										isertList.add(idg.getId("templet_chg_log.log_id"));
										isertList.add(objectId.replace("`", ""));
										isertList.add(a0101);
										isertList.add(onlyValue);
										isertList.add(info_type == 3 ? 2 : info_type);
										isertList.add(3);
										isertList.add(ins_id_tem);
										isertList.add(task_id_tem);
										isertList.add(setBo.getTabId());
										isertList.add(setBo.getPageId());
										isertList.add("1");
										isertList.add(setBo.getTableFieldName().toLowerCase());
										isertList.add("");
										isertList.add("");
										isertList.add((String) map.get("sub_content"));
										isertList.add("");
										isertList.add("");
										isertList.add((String) map.get("record_key_id"));
										isertList.add(new java.sql.Timestamp(new Date().getTime()));
										isertList.add((StringUtils.isBlank(this.userView.getUserFullName())
												? this.userView.getUserName() : this.userView.getUserFullName())
														.toLowerCase());
										insertParamList.add(isertList);
									}
									ArrayList delEditList = new ArrayList();
									delEditList.add((String) map.get("record_key_id"));
									delEditList.add(Integer.parseInt(ins_id));
									deleteEditParamList.add(delEditList);
									if(isNewInsert){//新增的记录才需要删除"删除"类型的日志
										ArrayList delDelList = new ArrayList();
										delDelList.add((String) map.get("record_key_id"));
										delDelList.add(Integer.parseInt(ins_id));
										delDelList.add((StringUtils.isBlank(this.userView.getUserFullName())
												? this.userView.getUserName() : this.userView.getUserFullName())
														.toLowerCase());
										deleteDelParamList.add(delDelList);
									}
								}
							}
						}
					} else {
						if (fieldMap.containsKey(fieldName.toLowerCase())) {
							HashMap map = (HashMap) fieldMap.get(fieldName.toLowerCase());
							String pageid = (String) map.get("pageid");
							// 更新日志记录
							if (setBo.isBcode() && !"codesetid".equalsIgnoreCase(fieldNameSave)) {
								String valueTemp = "";//bug 48051
								valueTemp = AdminCode.getCodeName(setBo.getCodeid(), value);
								if (StringUtils.isBlank(valueTemp) && "UM".equalsIgnoreCase(setBo.getCodeid())) {
									value = AdminCode.getCodeName("UN", value);
								} else {
									value = valueTemp;
								}
							}
							String log_id = (String) map.get("log_id");
							String content_1 = (String) map.get("content_1");
							String content_2 = (String) map.get("content_2");
							String opt_type2 = (String) map.get("opt_type");
							/*if ("D".equalsIgnoreCase(setBo.getField_type()) && StringUtils.isNotBlank(content_1)) {
								content_1 = content_1.replace(".", "-").replace("/", "-").replace("年", "-").replace("月", "-").replace("日"," ").replace("时", ":").replace("分", ":").replace("秒", "");
								content_1 = utilBo.getFormatDate(content_1, setBo.getDisformat());
								content_2 = content_2.replace(".", "-").replace("/", "-").replace("年", "-").replace("月", "-").replace("日"," ").replace("时", ":").replace("分", ":").replace("秒", "");
								content_2 = utilBo.getFormatDate(content_2, setBo.getDisformat());
							}*/
							if (StringUtils.isBlank(content_1)) {
								content_1 = "";
							}
							if (StringUtils.isBlank(content_2)) {
								content_2 = "";
							}
							if (content_1.equalsIgnoreCase(value) && "2".equalsIgnoreCase(opt_type2)) {
								ArrayList deleteList = new ArrayList();
								deleteList.add(log_id);
								deleteByLogIdParamList.add(deleteList);
							} else {
								if(!content_2.equalsIgnoreCase(value)){
									ArrayList updateList = new ArrayList();
									updateList.add(value);
									updateList.add(new java.sql.Timestamp(new Date().getTime()));
									updateList.add(log_id);
									updateParamList.add(updateList);
								}
							}
						} else {
							isInsert = false;
						}
						if (!isInsert && !((StringUtils.isBlank(value) || "null".equalsIgnoreCase(value))
								&& StringUtils.isBlank(oldValue))) {
							// 新增日志记录
							if (setBo.isBcode()) {
								if (!"codesetid".equalsIgnoreCase(fieldNameSave)) {
									String valueTemp = "";
									valueTemp = AdminCode.getCodeName(setBo.getCodeid(), value);
									if (StringUtils.isBlank(valueTemp) && "UM".equalsIgnoreCase(setBo.getCodeid())) {
										value = AdminCode.getCodeName("UN", value);
									} else {
										value = valueTemp;
									}
									valueTemp = AdminCode.getCodeName(setBo.getCodeid(), oldValue);
									if (StringUtils.isBlank(valueTemp) && "UM".equalsIgnoreCase(setBo.getCodeid())) {
										oldValue = AdminCode.getCodeName("UN", oldValue);
									} else {
										oldValue = valueTemp;
									}
								}
							}
							if ("D".equalsIgnoreCase(setBo.getField_type())) {
								try {
									if (StringUtils.isBlank(oldValue)) {
										oldValue = "";
									}
									//oldValue=oldValue.replace(".", "-").replace("/", "-").replace("年", "-").replace("月", "-").replace("日", " ").replace("时", ":").replace("分", ":").replace("秒", "");
									//oldValue=utilBo.getFormatDate(oldValue, setBo.getDisformat());
								}catch(Exception ex){
									ex.printStackTrace();
								}
								try {
									if (StringUtils.isBlank(value)) {
										value = "";
									}
									//value=value.replace(".", "-").replace("/", "-").replace("年", "-").replace("月", "-").replace("日", " ").replace("时", ":").replace("分", ":").replace("秒", "");
									//value=utilBo.getFormatDate(value, setBo.getDisformat());
								}catch(Exception ex){
									ex.printStackTrace();
								}
							}
							if (!value.equalsIgnoreCase(oldValue)) {
								ArrayList isertList = new ArrayList();
								isertList.add(idg.getId("templet_chg_log.log_id"));
								isertList.add(objectId.replace("`", ""));
								isertList.add(a0101);
								isertList.add(onlyValue);
								isertList.add(info_type == 3 ? 2 : info_type);
								isertList.add(2);
								isertList.add(ins_id_tem);
								isertList.add(task_id_tem);
								isertList.add(setBo.getTabId());
								isertList.add(setBo.getPageId());
								isertList.add("0");
								isertList.add("");
								isertList.add(fieldNameSave.toLowerCase());
								isertList.add(setBo.getField_hz());
								isertList.add("");
								isertList.add(oldValue);
								isertList.add(value);
								isertList.add("");
								isertList.add(new java.sql.Timestamp(new Date().getTime()));
								isertList.add((StringUtils.isBlank(this.userView.getUserFullName())
										? this.userView.getUserName() : this.userView.getUserFullName()).toLowerCase());
								insertParamList.add(isertList);
							}
						}
					}
				}
				if (updateParamList.size() > 0)
					dao.batchUpdate(updateSql, updateParamList);
				if (insertParamList.size() > 0)
					dao.batchInsert(insertSql, insertParamList);
				if (deleteEditParamList.size() > 0)
					dao.batchUpdate(deleteEditSql, deleteEditParamList);
				if(deleteDelParamList.size()>0){
					dao.batchUpdate(deleteDelSql, deleteDelParamList);
				}
				if (deleteByLogIdParamList.size() > 0)
					dao.batchUpdate(deleteByLogIdSql, deleteByLogIdParamList);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rowSet);
		}
	}

	// 插入或者更新某一个子集的变动信息
	public void insertOrUpdateOneSubsetLogger(String setname, String oldXml, String newXml, String ins_id,
			String task_id, String objectId, String tableName, int info_type, TemplateSet setBo) {
		String sql = "";
		String a0101 = "";
		String onlyValue = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		HashMap fieldMap = new HashMap();
		HashMap valueMap = new HashMap();
		try {
			this.createTemplateChgLogTable("templet_chg_log");
			sql = " select " + setname + " from " + tableName + " where ";
			String sqlObjectName = "";
			if (info_type == 1) {
				String[] objectids = objectId.split("`");
				String basepre = objectids[0];
				String a0100 = objectids[1];
				Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
				String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
				String valid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");
				if (this.param.getOperationType() == 0) {
					DbWizard dbwizard = new DbWizard(this.conn);
					sqlObjectName = "select a0101_1 a0101 from " + tableName + " where A0100='" + a0100
							+ "' and BasePre='" + basepre + "' ";
					if (!"0".equals(valid)) {
						if (dbwizard.isExistField(tableName, onlyname + "_2", false)) {
							sqlObjectName = "select a0101_1 a0101," + onlyname + "_2 " + onlyname + " from " + tableName
									+ " where A0100='" + a0100 + "' and BasePre='" + basepre + "' ";
						} else {
							sqlObjectName = "select a0101_1 a0101,'' " + onlyname + " from " + tableName
									+ " where A0100='" + a0100 + "' and BasePre='" + basepre + "' ";
						}
					}
					if (!"0".equalsIgnoreCase(ins_id)) {
						sqlObjectName += " and ins_id='" + ins_id + "'";
					}
					sql += " A0100='" + a0100 + "'  and BasePre='" + basepre + "'";
				} else {
					sqlObjectName = "select a0101 from " + basepre + "A01 " + " where A0100='" + a0100 + "' ";
					if (!"0".equals(valid)) {
						sqlObjectName = "select a0101," + onlyname + " from " + basepre + "A01 " + " where A0100='"
								+ a0100 + "'";
					}
					sql += " A0100='" + a0100 + "' and BasePre='" + basepre + "'";
				}
				rowSet = dao.search(sqlObjectName);
				if (rowSet.next()) {
					a0101 = rowSet.getString("a0101");
					if (!"0".equals(valid)) {
						onlyValue = rowSet.getString(onlyname);
					}
				}
			} else if (info_type == 2) {
				if (this.param.getOperationType() == 5||this.param.getOperationType() == 8) {
					DbWizard dbwizard = new DbWizard(this.conn);
					sqlObjectName = "select codeitemdesc_1 a0101 from " + tableName + " where b0110='" + objectId
							+ "' ";
					if (!"0".equalsIgnoreCase(ins_id)) {
						sqlObjectName += " and ins_id='" + ins_id + "'";
					}
					rowSet = dao.search(sqlObjectName);
					if (rowSet.next()) {
						a0101 = rowSet.getString("a0101");
					}
				} else {
					a0101 = AdminCode.getCode("UN", objectId) == null
							? (AdminCode.getCode("UM", objectId) == null ? ""
									: AdminCode.getCode("UM", objectId).getCodename())
							: AdminCode.getCode("UN", objectId).getCodename();

				}
				sql += " b0110='" + objectId + "'";
			} else if (info_type == 3) {
				sql += " e01a1='" + objectId + "'";
				if (this.param.getOperationType() == 5||this.param.getOperationType() == 8) {
					DbWizard dbwizard = new DbWizard(this.conn);
					sqlObjectName = "select codeitemdesc_1 a0101 from " + tableName + " where e01a1='" + objectId
							+ "' ";
					if (!"0".equalsIgnoreCase(ins_id)) {
						sqlObjectName += " and ins_id='" + ins_id + "'";
					}
					rowSet = dao.search(sqlObjectName);
					if (rowSet.next()) {
						a0101 = rowSet.getString("a0101");
					}
				} else {
					a0101 = AdminCode.getCode("@K", objectId) == null ? ""
							: AdminCode.getCode("@K", objectId).getCodename();
				}
			}
			IDGenerator idg = new IDGenerator(2, this.conn);
			if ("0".equalsIgnoreCase(ins_id)) {
				sql = "select log_id,pageid,subflag,field_id,record_key_id,setname,opt_type  from templet_chg_log where ins_id='"
						+ ins_id + "' and task_id='" + task_id + "' and lower(objectid)='"
						+ objectId.replace("`", "").toLowerCase() + "' and lower(Createuser)='"
						+ (StringUtils.isBlank(this.userView.getUserFullName()) ? this.userView.getUserName()
								: this.userView.getUserFullName()).toLowerCase()
						+ "' and lower(setname)='" + setname.toLowerCase() + "'";
			} else {
				sql = "select log_id,pageid,subflag,field_id,record_key_id,setname,opt_type  from templet_chg_log where ( ins_id='"
						+ ins_id + "' and task_id='" + task_id + "' and opt_type<>3  and lower(objectid)='"
						+ objectId.replace("`", "").toLowerCase() + "'  and lower(setname)='" + setname.toLowerCase()
						+ "' )  or( ins_id='" + ins_id + "' and opt_type=3  and lower(setname)='"
						+ setname.toLowerCase() + "')";
			}
			rowSet = dao.search(sql);
			while (rowSet.next()) {
				String log_id = rowSet.getString("log_id");
				String pageid = rowSet.getString("pageid");
				String subflag = rowSet.getString("subflag");
				String field_id = rowSet.getString("field_id");
				String record_key_id = rowSet.getString("record_key_id");
				String opt_type = rowSet.getString("opt_type");
				HashMap map = new HashMap();
				map.put("log_id", log_id);
				map.put("pageid", pageid);
				map.put("subflag", subflag);
				map.put("record_key_id", record_key_id);
				map.put("field_id", field_id);
				map.put("setname", setname);
				map.put("opt_type", opt_type);
				if ("3".equalsIgnoreCase(opt_type)) {
					fieldMap.put(record_key_id + ":" + field_id + ":" + opt_type, map);
				} else {
					fieldMap.put(record_key_id + ":" + field_id, map);
				}
			}
			ArrayList insertParamList = new ArrayList();
			ArrayList updateParamList = new ArrayList();
			ArrayList deleteEditParamList = new ArrayList();//删除"编辑"和"新增"类型的日志记录
			ArrayList deleteDelParamList = new ArrayList();//删除"删除"类型日志的记录
			String insertSql = "insert into templet_chg_log (LOG_ID,OBJECTID,A0101,ONLY_VALUE,INFO_TYPE,OPT_TYPE,INS_ID,TASK_ID,TABID,PAGEID,SUBFLAG,SETNAME,FIELD_ID,FIELD_NAME,SUB_CONTENT,CONTENT_1,CONTENT_2,RECORD_KEY_ID,CREATETIME,CREATEUSER) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			String updateSql = "update templet_chg_log set content_2=?,createtime=? where log_id=?";
			String deleteEditSql = "delete from  templet_chg_log where record_key_id=?  and ins_id=? and opt_type<>3 ";
			String deleteDelSql = "delete from  templet_chg_log where record_key_id=?  and ins_id=? and  (opt_type=3 and createuser=?)";
			Boolean isInsert = true;
			ArrayList list = compareSubsetValues(oldXml, newXml,setBo);
			for (int j = 0; j < list.size(); j++) {
				HashMap map = (HashMap) list.get(j);
				String opt_type = (String) map.get("opt_type");
				if ("1".equalsIgnoreCase(opt_type)) {
					ArrayList isertList = new ArrayList();
					isertList.add(idg.getId("templet_chg_log.log_id"));
					isertList.add(objectId.replace("`", ""));
					isertList.add(a0101);
					isertList.add(onlyValue);
					isertList.add(info_type == 3 ? 2 : info_type);
					isertList.add(1);
					isertList.add(Integer.parseInt(ins_id));
					isertList.add(Integer.parseInt(task_id));
					isertList.add(setBo.getTabId());
					isertList.add(setBo.getPageId());
					isertList.add("1");
					isertList.add(setBo.getTableFieldName());
					isertList.add((String) map.get("field_id"));
					isertList.add((String) map.get("field_name"));
					isertList.add((String) map.get("sub_content"));
					isertList.add((String) map.get("content_1"));
					isertList.add((String) map.get("content_2"));
					isertList.add((String) map.get("record_key_id"));
					isertList.add(new java.sql.Timestamp(new Date().getTime()));
					isertList.add((StringUtils.isBlank(this.userView.getUserFullName()) ? this.userView.getUserName()
							: this.userView.getUserFullName()).toLowerCase());
					insertParamList.add(isertList);
				} else if ("2".equalsIgnoreCase(opt_type)) {
					String record_key_id = (String) map.get("record_key_id");
					String field_id = (String) map.get("field_id");
					if (StringUtils.isBlank(field_id)) {
						field_id = "";
					}
					String content_2 = (String) map.get("content_2");
					Boolean isNew = true;
					if (fieldMap.containsKey(record_key_id + ":" + field_id)) {
						HashMap submap = (HashMap) fieldMap.get(record_key_id + ":" + field_id);
						if (field_id.equalsIgnoreCase((String) submap.get("field_id"))) {
							String log_id = (String) submap.get("log_id");
							ArrayList updateList = new ArrayList();
							updateList.add(content_2);
							updateList.add(new java.sql.Timestamp(new Date().getTime()));
							updateList.add(log_id);
							updateParamList.add(updateList);
							isNew = false;
						}
					}
					if (isNew) {
						ArrayList isertList = new ArrayList();
						isertList.add(idg.getId("templet_chg_log.log_id"));
						isertList.add(objectId.replace("`", ""));
						isertList.add(a0101);
						isertList.add(onlyValue);
						isertList.add(info_type == 3 ? 2 : info_type);
						isertList.add(2);
						isertList.add(Integer.parseInt(ins_id));
						isertList.add(Integer.parseInt(task_id));
						isertList.add(setBo.getTabId());
						isertList.add(setBo.getPageId());
						isertList.add("1");
						isertList.add(setBo.getTableFieldName());
						isertList.add((String) map.get("field_id"));
						isertList.add((String) map.get("field_name"));
						isertList.add((String) map.get("sub_content"));
						isertList.add((String) map.get("content_1"));
						isertList.add((String) map.get("content_2"));
						isertList.add((String) map.get("record_key_id"));
						isertList.add(new java.sql.Timestamp(new Date().getTime()));
						isertList.add((StringUtils.isBlank(this.userView.getUserFullName())
								? this.userView.getUserName() : this.userView.getUserFullName()).toLowerCase());
						insertParamList.add(isertList);
					}
				} else if ("3".equalsIgnoreCase(opt_type)) {
					// 新增日志记录
					String record_key_id = (String) map.get("record_key_id");
					String field_id = (String) map.get("field_id");
					if (StringUtils.isBlank(field_id)) {
						field_id = "";
					}
					if (!fieldMap.containsKey(record_key_id + ":" + field_id + ":3")) {
						Boolean isNewInsert = (Boolean) map.get("isNewInsert");
						if(!isNewInsert){
							ArrayList isertList = new ArrayList();
							isertList.add(idg.getId("templet_chg_log.log_id"));
							isertList.add(objectId.replace("`", ""));
							isertList.add(a0101);
							isertList.add(onlyValue);
							isertList.add(info_type == 3 ? 2 : info_type);
							isertList.add(3);
							isertList.add(Integer.parseInt(ins_id));
							isertList.add(Integer.parseInt(task_id));
							isertList.add(setBo.getTabId());
							isertList.add(setBo.getPageId());
							isertList.add("1");
							isertList.add(setBo.getTableFieldName());
							isertList.add("");
							isertList.add("");
							isertList.add((String) map.get("sub_content"));
							isertList.add("");
							isertList.add("");
							isertList.add((String) map.get("record_key_id"));
							isertList.add(new java.sql.Timestamp(new Date().getTime()));
							isertList.add((StringUtils.isBlank(this.userView.getUserFullName())
									? this.userView.getUserName() : this.userView.getUserFullName()).toLowerCase());
							insertParamList.add(isertList);
						}

						ArrayList delEditList = new ArrayList();
						delEditList.add((String) map.get("record_key_id"));
						delEditList.add(Integer.parseInt(ins_id));
						deleteEditParamList.add(delEditList);
						if(isNewInsert){//新增的记录才需要删除"删除"类型的日志
							ArrayList delDelList = new ArrayList();
							delDelList.add((String) map.get("record_key_id"));
							delDelList.add(Integer.parseInt(ins_id));
							delDelList.add((StringUtils.isBlank(this.userView.getUserFullName())
									? this.userView.getUserName() : this.userView.getUserFullName())
											.toLowerCase());
							deleteDelParamList.add(delDelList);
						}

					}
				}
			}
			if (updateParamList.size() > 0)
				dao.batchUpdate(updateSql, updateParamList);
			if (insertParamList.size() > 0)
				dao.batchInsert(insertSql, insertParamList);
			if (deleteEditParamList.size() > 0)
				dao.batchUpdate(deleteEditSql, deleteEditParamList);
			if(deleteDelParamList.size()>0){
				dao.batchUpdate(deleteDelSql, deleteDelParamList);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rowSet);
		}
	}

	// 根据新旧子集xml数据，对比出新增、修改和删除的记录信息。
	public ArrayList compareSubsetValues(String oldXml, String newXml,TemplateSet setBo) {
		ArrayList list = new ArrayList();
		HashMap oldMap = new HashMap();
		HashMap newMap = new HashMap();
		try {
			if (StringUtils.isNotBlank(oldXml)) {
				Document doc = PubFunc.generateDom(oldXml);
				Element eleRoot = null; // xml解析得到/records对象
				Element element = null; // xml解析得到/record对象
				XMLOutputter outputter = new XMLOutputter();
				Format format = Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				String xpath = "/records";
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				eleRoot = (Element) findPath.selectSingleNode(doc);
				boolean isCdata = false;
				if (eleRoot != null) {
					String columns = eleRoot.getAttributeValue("columns").toUpperCase(); // 得到xml中columns中对应的值
					String[] columnarr = columns.split("`", -1);
					List recordList = eleRoot.getChildren("record");
					if (recordList != null && recordList.size() > 0) {
						for (int i = 0; i < recordList.size(); i++) {
							HashMap childMap = new HashMap();
							element = (Element) recordList.get(i);
							String contentValue = element.getValue();
							String i9999 = element.getAttributeValue("I9999");
							childMap.put("i9999", i9999);
							// 存储column和其对应的值，lineNum为当前record的条数
							if (contentValue != null && contentValue.length() > 0) {
								String[] valueArr = contentValue.split("`", -1);
								String[] columnArr = columns.split("`", -1);
								LinkedHashMap columnMap = new LinkedHashMap();
								LinkedHashMap columnNameMap = new LinkedHashMap();
								for (int j = 0; j < columnArr.length&&j<valueArr.length; j++) {//bug 49243
									ArrayList subFieldList = setBo.getSubFieldList();
									String value = valueArr[j];
									if (columnArr[j].toLowerCase().indexOf("attach") != -1)// 不支持附件
										continue;
									Boolean isReadOnly=false;
									for(int num=0;num<subFieldList.size();num++){
										SubField subField = (SubField) subFieldList.get(num);
										if(subField.getFieldname().equalsIgnoreCase(columnArr[j])&&"true".equalsIgnoreCase(subField.getHis_readonly())&&!"-1".equalsIgnoreCase(i9999)){
											isReadOnly=true;
											break;
										}
									}
									if(isReadOnly){
										continue;
									}
									FieldItem item = DataDictionary.getFieldItem(columnarr[j]);
									if (item != null ) {
										if (item.isCode()) {
											String valueTemp = "";//bug 48051 代码型指标没雨被翻译
											valueTemp =AdminCode.getCode(item.getCodesetid(), value) == null ? ""
													: AdminCode.getCode(item.getCodesetid(), value).getCodename();
											if (StringUtils.isBlank(valueTemp) && "UM".equalsIgnoreCase(item.getCodesetid())) {
												value = AdminCode.getCodeName("UN", value);
											} else {
												value = valueTemp;
											}
										}
										if(j < 6){
											columnNameMap.put(item.getItemdesc(), value);
										}
									}
									columnMap.put(columnArr[j].toLowerCase(), valueArr[j]);
								}
								childMap.put("columnMap", columnMap);
								childMap.put("columnDescMap", columnNameMap);
							}
							String state = element.getAttributeValue("state");
							childMap.put("state", state);
							String record_key_id = element.getAttributeValue("record_key_id");
							oldMap.put(record_key_id, childMap);
						}
					}
				}
			}
			if (StringUtils.isNotBlank(newXml)) {
				Document doc = PubFunc.generateDom(newXml);
				Element eleRoot = null; // xml解析得到/records对象
				Element element = null; // xml解析得到/record对象
				XMLOutputter outputter = new XMLOutputter();
				Format format = Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				String xpath = "/records";
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				eleRoot = (Element) findPath.selectSingleNode(doc);
				boolean isCdata = false;
				if (eleRoot != null) {
					String columns = eleRoot.getAttributeValue("columns").toUpperCase(); // 得到xml中columns中对应的值
					String[] columnarr = columns.split("`");
					List recordList = eleRoot.getChildren("record");
					if (recordList != null && recordList.size() > 0) {
						for (int i = 0; i < recordList.size(); i++) {
							HashMap childMap = new HashMap();
							element = (Element) recordList.get(i);
							String state = element.getAttributeValue("state");
							childMap.put("state", state);
							String i9999 = element.getAttributeValue("I9999");
							childMap.put("i9999", i9999);
							String record_key_id = element.getAttributeValue("record_key_id");
							if ("D".equalsIgnoreCase(state)) {
								HashMap oldChildMap = (HashMap) oldMap.get(record_key_id);
								LinkedHashMap oldColumnMap = (LinkedHashMap) oldChildMap.get("columnMap");
								LinkedHashMap columnDescMap = (LinkedHashMap) oldChildMap.get("columnDescMap");
								if ("D".equalsIgnoreCase(state)) {
									HashMap returnmap = new HashMap();
									returnmap.put("record_key_id", record_key_id);
									returnmap.put("opt_type", "3");
									JSONObject subDatajson = JSONObject.fromObject(columnDescMap);
									String value = subDatajson.toString();
									returnmap.put("sub_content", value);
									returnmap.put("isNewInsert", false);
									list.add(returnmap);
								}
							} else {
								HashMap oldChildMap = (HashMap) oldMap.get(record_key_id);
								if (oldChildMap != null) {// 修改之前的数据
									LinkedHashMap oldColumnMap = (LinkedHashMap) oldChildMap.get("columnMap");
									LinkedHashMap columnDescMap = (LinkedHashMap) oldChildMap.get("columnDescMap");
									String contentValue = element.getValue();
									// 存储column和其对应的值，lineNum为当前record的条数
									if (contentValue != null && contentValue.length() > 0) {
										String[] valueArr = contentValue.split("`", -1);
										String[] columnArr = columns.split("`", -1);
										for (int j = 0; j < columnArr.length; j++) {
											if (columnArr[j].toLowerCase().indexOf("attach") != -1)// 不支持附件
												continue;
											ArrayList subFieldList = setBo.getSubFieldList();
											Boolean isReadOnly=false;
											for(int num=0;num<subFieldList.size();num++){
												SubField subField = (SubField) subFieldList.get(num);
												if(subField.getFieldname().equalsIgnoreCase(columnArr[j])&&"true".equalsIgnoreCase(subField.getHis_readonly())&&!"-1".equalsIgnoreCase(i9999)){
													isReadOnly=true;
													break;
												}
											}
											if(isReadOnly){
												continue;
											}
											String newValue = valueArr[j];
											FieldItem item1 = DataDictionary.getFieldItem(columnArr[j]);
											//syl日期型字段转换成标准日期 58053 人事异动表单22号，设置自动记录变动日志，子集中起始时间和截止时间并没有变动，也变红了
											if(item1 != null&&"D".equalsIgnoreCase(item1.getItemtype())&&newValue.indexOf(".")!=-1){
												newValue = PubFunc.FormatDate(newValue);
											}
											String oldValue = (String) oldColumnMap
													.get(columnArr[j].toLowerCase()) == null ? ""
															: (String) oldColumnMap.get(columnArr[j].toLowerCase());
											//syl日期型字段转换成标准日期 58053 人事异动表单22号，设置自动记录变动日志，子集中起始时间和截止时间并没有变动，也变红了
											if(item1 != null&&"D".equalsIgnoreCase(item1.getItemtype())&&oldValue.indexOf(".")!=-1){
												oldValue = PubFunc.FormatDate(oldValue);
											}
											if (!newValue.equalsIgnoreCase(oldValue) && !(StringUtils.isBlank(oldValue)
													&& StringUtils.isBlank(newValue))) {
												HashMap returnmap = new HashMap();
												returnmap.put("record_key_id", record_key_id);
												returnmap.put("opt_type", "2");
												JSONObject subDatajson = JSONObject.fromObject(columnDescMap);
												String value = subDatajson.toString();
												returnmap.put("sub_content", value);
												returnmap.put("field_id", columnArr[j]);
												FieldItem item = DataDictionary.getFieldItem(columnarr[j]);
												String field_name = "";
												if (item != null) {
													field_name = item.getItemdesc();
													if (item.isCode()) {
														String valueTemp = "";//bug 48051 代码型指标没雨被翻译
														valueTemp =AdminCode.getCode(item.getCodesetid(), newValue) == null ? ""
																: AdminCode.getCode(item.getCodesetid(), newValue).getCodename();
														if (StringUtils.isBlank(valueTemp) && "UM".equalsIgnoreCase(item.getCodesetid())) {
															newValue = AdminCode.getCodeName("UN", newValue);
														} else {
															newValue = valueTemp;
														}
														valueTemp = "";
														valueTemp =AdminCode.getCode(item.getCodesetid(), oldValue) == null ? ""
																: AdminCode.getCode(item.getCodesetid(), oldValue).getCodename();
														if (StringUtils.isBlank(valueTemp) && "UM".equalsIgnoreCase(item.getCodesetid())) {
															oldValue = AdminCode.getCodeName("UN", oldValue);
														} else {
															oldValue = valueTemp;
														}
													}
												}
												returnmap.put("field_name", field_name);
												returnmap.put("content_1", oldValue);
												returnmap.put("content_2", newValue);
												returnmap.put("record_key_id", record_key_id);
												list.add(returnmap);
											}
										}
									}
								} else {// 新增子集数据
									LinkedHashMap columnchildMap = new LinkedHashMap();
									LinkedHashMap columnDescMap = new LinkedHashMap();
									String contentValue = element.getValue();
									// 存储column和其对应的值，lineNum为当前record的条数
									if (contentValue != null && contentValue.length() > 0) {
										String[] valueArr = contentValue.split("`", -1);
										String[] columnArr = columns.split("`", -1);
										for (int j = 0; j < columnArr.length; j++) {
											if (columnArr[j].toLowerCase().indexOf("attach") != -1)// 不支持附件
												continue;
											String newValue = valueArr[j];
											FieldItem item = DataDictionary.getFieldItem(columnarr[j]);
											String itemDesc = "";
											if (item != null) {
												itemDesc = item.getItemdesc();
												if (item.isCode()) {
														String valueTemp = "";//bug 48051 代码型指标没雨被翻译
														valueTemp =AdminCode.getCode(item.getCodesetid(), newValue) == null ? ""
																: AdminCode.getCode(item.getCodesetid(), newValue).getCodename();
														if (StringUtils.isBlank(valueTemp) && "UM".equalsIgnoreCase(item.getCodesetid())) {
															newValue = AdminCode.getCodeName("UN", newValue);
														} else {
															newValue = valueTemp;
														}
												}
												
												if(j < 6){
													columnDescMap.put(itemDesc, newValue);
												}
											}
											columnchildMap.put(columnArr[j], newValue);
										}

										for (int j = 0; j < columnArr.length; j++) {
											if (columnArr[j].toLowerCase().indexOf("attach") != -1)// 不支持附件
												continue;
											String content_2 = (String) columnchildMap.get(columnArr[j]);
											if (StringUtils.isBlank(content_2)) {
												continue;
											}
											HashMap returnmap = new HashMap();
											returnmap.put("record_key_id", record_key_id);
											returnmap.put("opt_type", "1");
											JSONObject subDatajson = JSONObject.fromObject(columnDescMap);
											String value = subDatajson.toString();
											returnmap.put("sub_content", value);
											returnmap.put("field_id", columnArr[j]);
											FieldItem item = DataDictionary.getFieldItem(columnarr[j]);
											String field_name = "";
											if (item != null) {
												field_name = item.getItemdesc();
											}
											returnmap.put("field_name", field_name);
											returnmap.put("content_1", "");
											returnmap.put("content_2", columnchildMap.get(columnArr[j]));
											list.add(returnmap);
										}
									}
								}
							}
							oldMap.remove(record_key_id);
						}
					}
				}
				if (oldMap.size() > 0) {
					Iterator iterator = oldMap.entrySet().iterator();
					while (iterator.hasNext()) {
						Entry next = (Entry) iterator.next();
						String record_key_id = (String) next.getKey();
						HashMap map = (HashMap) next.getValue();
						if (map.size() > 0) {
							HashMap columnDescMap = (HashMap) map.get("columnDescMap");
							HashMap returnmap = new HashMap();
							returnmap.put("record_key_id", record_key_id);
							returnmap.put("opt_type", "3");
							JSONObject subDatajson = JSONObject.fromObject(columnDescMap);
							String value = subDatajson.toString();
							returnmap.put("sub_content", value);
							returnmap.put("isNewInsert", true);
							list.add(returnmap);
						}
					}
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	// 判断临时表是否存在，不存在就创建。
	public void createTemplateChgLogTable(String tablename) {
		DbWizard dbwizard = new DbWizard(this.conn);
		Table table = new Table(tablename);
		try {
			if (!dbwizard.isExistTable(tablename, false)) {
				addFieldItem(table, 0);
				dbwizard.createTable(table);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	// 临时表中字段设置。
	private void addFieldItem(Table table, int flag) throws GeneralException {
		Field temp = null;

		temp = new Field("log_id", "log_id");
		temp.setDatatype(DataType.STRING);
		temp.setLength(10);
		temp.setKeyable(true);
		temp.setVisible(false);
		temp.setNullable(false);
		temp.setSortable(false);
		table.addField(temp);

		temp = new Field("objectid", "objectid");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);

		temp = new Field("a0101", "a0101");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);

		temp = new Field("only_value", "only_value");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);

		temp = new Field("info_type", "info_type");
		temp.setDatatype(DataType.INT);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);

		temp = new Field("opt_type", "opt_type");
		temp.setDatatype(DataType.INT);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);

		temp = new Field("ins_id", "ins_id");
		temp.setDatatype(DataType.INT);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);

		temp = new Field("task_id", "task_id");
		temp.setDatatype(DataType.INT);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);

		temp = new Field("tabid", "tabid");
		temp.setDatatype(DataType.INT);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);

		temp = new Field("pageid", "pageid");
		temp.setDatatype(DataType.INT);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);

		temp = new Field("subflag", "subflag");
		temp.setDatatype(DataType.STRING);
		temp.setLength(1);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);

		temp = new Field("setname", "setname");
		temp.setDatatype(DataType.STRING);
		temp.setLength(10);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);

		temp = new Field("field_id", "field_id");
		temp.setDatatype(DataType.STRING);
		temp.setLength(30);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);

		temp = new Field("field_name", "field_name");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);

		temp = new Field("sub_content", "sub_content");
		temp.setDatatype(DataType.CLOB);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);

		temp = new Field("content_1", "content_1");
		temp.setDatatype(DataType.CLOB);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);

		temp = new Field("content_2", "content_2");
		temp.setDatatype(DataType.CLOB);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);

		temp = new Field("record_key_id", "record_key_id");
		temp.setDatatype(DataType.STRING);
		temp.setLength(100);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);

		temp = new Field("createtime", "createtime");
		temp.setDatatype(DataType.DATETIME);
		temp.setFormat("YYYY-mm-DD HH:MM:SS");
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);

		temp = new Field("createuser", "createuser");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);
	}

	// 删除未进入流程单据的变动日志记录
	public void deleteChangeInfoNoInProcess(ArrayList personList, String task_id, String tabid, String ins_id,
			int info_type) {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			this.createTemplateChgLogTable("templet_chg_log");
			String sql = "";
			ArrayList delParamlist = new ArrayList();
			for (int i = 0; i < personList.size(); i++) {
				ArrayList list = (ArrayList) personList.get(i);
				String objectid = "";
				if (info_type == 1) {
					String basepre = (String) list.get(0);
					String a0100 = (String) list.get(1);
					objectid = basepre.toLowerCase() + a0100.toLowerCase();
				} else {
					objectid = (String) list.get(0);
				}
				sql = "delete from  templet_chg_log  where tabid=? and ins_id=? and lower(objectid)=? and createUser=? ";
				ArrayList paramList = new ArrayList();
				paramList.add(tabid);
				paramList.add(ins_id);
				paramList.add(objectid);
				paramList.add((StringUtils.isBlank(this.userView.getUserFullName()) ? this.userView.getUserName()
						: this.userView.getUserFullName()).toLowerCase());
				delParamlist.add(paramList);
			}
			if (StringUtils.isNotBlank(sql) && delParamlist.size() > 0) {
				dao.batchUpdate(sql, delParamlist);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 删除已进入流程的变动日志记录批量删除
	 * @param sql
	 * @param tab_id
	 * @param info_type
	 */
	public void deleteChangeInfoInProcess(String sql, String tab_id, int info_type) {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			this.createTemplateChgLogTable("templet_chg_log");
			String delSql = "delete from  templet_chg_log  where tabid=? and ins_id=? and lower(objectid)=?";
			ArrayList delParamList = new ArrayList();
			rowSet = dao.search(sql);
			while (rowSet.next()) {
				ArrayList paramList = new ArrayList();
				paramList.add(tab_id);
				String ins_id = rowSet.getString("ins_id");
				paramList.add(ins_id);
				if (info_type == 1) {
					String nbase = rowSet.getString("basePre");
					String a0100 = rowSet.getString("a0100");
					paramList.add(nbase.toLowerCase() + a0100.toLowerCase());
				} else if (info_type == 2) {
					String b0110 = rowSet.getString("b0110");
					paramList.add(b0110.toLowerCase());
				} else if (info_type == 3) {
					String e01a1 = rowSet.getString("e01a1");
					paramList.add(e01a1.toLowerCase());
				}
				delParamList.add(paramList);
			}
			if (delParamList.size() > 0) {
				dao.batchUpdate(delSql, delParamList);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rowSet);
		}
	}

	/**
	 * 删除已进入流程的变动日志记录单个删除
	 * @param task_id
	 * @param tab_id
	 */
	public void deleteChangeInfoInProcess(String task_id, String tab_id) {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			this.createTemplateChgLogTable("templet_chg_log");
			RecordVo vo = new RecordVo("t_wf_task");
			vo.setInt("task_id", Integer.parseInt(task_id));
			vo = dao.findByPrimaryKey(vo);
			String ins_id = vo.getString("ins_id");
			String delSql = "delete from  templet_chg_log  where tabid=? and ins_id=? ";
			ArrayList delParamList = new ArrayList();
			delParamList.add(tab_id);
			delParamList.add(ins_id);
			dao.delete(delSql, delParamList);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rowSet);
		}
	}
	/**
	 * 新增机构提交入库后需要更新成新的编码
	 * @param newObjectid新的objetid
	 * @param oldObjectid原来的objectid
	 */
	public void updateOrganizationNewCodeitemId(String newObjectid,String oldObjectid){
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			this.createTemplateChgLogTable("templet_chg_log");
			String sql="update templet_chg_log set objectid=? where objectid=?";
			ArrayList paramList=new ArrayList();
			paramList.add(newObjectid);
			paramList.add(oldObjectid);
			dao.update(sql, paramList);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rowSet);
		}
	}
	/**
	 * 撤回任务把ins_id更改回0
	 * @param ins_id
	 */
	public void recallTaskUpdateInsidToZero(String ins_id){
		this.createTemplateChgLogTable("templet_chg_log");
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			ArrayList allParamList = new ArrayList();
			String sql = "update templet_chg_log set ins_id=?,task_id=? where  ins_id=? ";
			ArrayList paramList = new ArrayList();
			paramList.add("0");
			paramList.add("0");
			paramList.add(ins_id);
			allParamList.add(paramList);
			if (allParamList.size() > 0)
				dao.batchUpdate(sql, allParamList);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
