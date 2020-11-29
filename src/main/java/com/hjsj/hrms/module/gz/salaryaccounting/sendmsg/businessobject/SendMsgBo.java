package com.hjsj.hrms.module.gz.salaryaccounting.sendmsg.businessobject;

import com.hjsj.hrms.businessobject.attestation.AttestationUtils;
import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.interfaces.analyse.IParserConstant;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.module.gz.utils.SalaryPageLayoutBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * Title:SendMsgBo.java
 * </p>
 * <p>
 * Decsription:定义薪资发放邮件模板
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:2015-7-3 10:24:08
 * </p>
 * 
 * @author sunming
 * @version 1.0
 */
/**
 * @author lucky
 *
 */
public class SendMsgBo {

	/** 库链接 */
	private Connection conn = null;
	/** 登录用户 */
	private UserView userview;

	public SendMsgBo() {

	}

	public SendMsgBo(Connection conn,  UserView userview) {
		this.conn = conn;
		this.userview = userview;
	}
	/**
	 * 获取发放功能按钮
	 * 
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getSalaryAccountingButtonList() throws GeneralException {
		SalaryPageLayoutBo spbo  = new SalaryPageLayoutBo(conn,userview);
		ArrayList buttonList = new ArrayList();
		String mobile_field=this.getMobileField();//判断短信参数是否设置
		String corpid = (String) ConstantParamter.getAttribute("wx","corpid");//判断微信参数是否设置
		String dd_corpid = (String) ConstantParamter.getAttribute("DINGTALK","corpid");//判断钉钉参数是否设置
		try {
			buttonList.add(new ButtonInfo(ResourceFactory.getProperty("gz_new.gz_accounting.getMsgcontent"),"sendMsgScope.getMsgcontent"));//发送通知按钮
			//buttonList.add("-");
			ButtonInfo binfo=new ButtonInfo(ResourceFactory.getProperty("menu.gz.sendmessage"),"sendMsgScope.showSendMode");
			if(corpid!=null&&corpid.length()>0)
				binfo.setParameter("corpid", "1");
			else
				binfo.setParameter("corpid", "0");
			if(mobile_field!=null&&mobile_field.trim().length()>0)
				binfo.setParameter("mobile", "1");
			else
				binfo.setParameter("mobile", "0");
			if(dd_corpid!=null&&dd_corpid.length()>0)
				binfo.setParameter("dd_corpid", "1");
			else
				binfo.setParameter("dd_corpid", "0");
			buttonList.add(binfo);
			buttonList.add("-");
			buttonList.add(new ButtonInfo(ResourceFactory.getProperty("gz_new.gz_accounting.deleteMsg"), "sendMsgScope.deleteMsg"));
			
			
			ButtonInfo button = new ButtonInfo("请输入姓名",ButtonInfo.TYPE_QUERYBOX,"GZ00000141");
			button.setType(ButtonInfo.TYPE_QUERYBOX);
			buttonList.add(button);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return buttonList;
	}

	/**
	 * @Title: getColumnList
	 * @Description: 查询列表表头信息
	 * @param
	 * @return
	 * @return ArrayList
	 * @throws GeneralException
	 */
	public ArrayList getColumnList() throws GeneralException {
		ArrayList list = new ArrayList();
		ArrayList columnList = new ArrayList();
		list.add("personid"); // 人员id
		list.add("aid"); // id,因tablefactory控件中的id具有唯一性，所以这里将id处理为aid
		list.add("send_ok"); // 状态
		list.add("b0110"); // 单位
		list.add("e0122"); // 部门
		list.add("a0101");// 姓名
		list.add("send_time"); //发放日期
		list.add("a00z3"); //发放次数
		list.add("address"); // 发送到
		list.add("subject");// 主题
		list.add("a0100");// 人员编号
		list.add("nbase");// 人员库
		list.add("I9999");
		list.add("wid");

		try {

			for (int i = 0; i < list.size(); i++) {
				FieldItem item = DataDictionary.getFieldItem((String) list.get(i));
				FieldItem info = new FieldItem();
				if (item == null&& "send_ok".equalsIgnoreCase((String) list.get(i))) {
					info.setItemid("send_ok");
					info.setItemtype("M");
					info.setItemdesc(ResourceFactory.getProperty("gz_new.gz_accounting.mailStatus"));
					info.setItemlength(80);
					info.setCodesetid("0");
					info.setAlign("center");
				} else if ("b0110".equalsIgnoreCase((String) list.get(i))) {
					info = (FieldItem) item.cloneItem();
					info.setAlign("left");
				} else if ("e0122".equalsIgnoreCase((String) list.get(i))) {
					info = (FieldItem) item.cloneItem();
					info.setAlign("left");
				} else if ("a0101".equalsIgnoreCase((String) list.get(i))) {
					info = (FieldItem) item.cloneItem();
					info.setAlign("left");
				} else if ("send_time".equalsIgnoreCase((String) list.get(i))) {
					info.setItemid("send_time");
					info.setItemtype("D");
					info.setItemdesc("业务日期");
					info.setFormat("yyyy-MM-dd HH:mm");
					info.setCodesetid("0");
					info.setAlign("left");
				}else if ("a00z3".equalsIgnoreCase((String) list.get(i))) {
					info.setItemid("a00z3");
					info.setItemtype("A");
					info.setItemdesc("发放次数"); 
					info.setCodesetid("0");
					info.setAlign("right");
				}else if ("address".equalsIgnoreCase((String) list.get(i))) {
					info.setItemid("address");
					info.setItemtype("M");
					info.setItemdesc(ResourceFactory.getProperty("gz_new.gz_accounting.address"));
					info.setItemlength(200);
					info.setCodesetid("0");
					info.setAlign("left");
				} else if ("subject".equalsIgnoreCase((String) list.get(i))) {
					info.setItemid("subject");
					info.setItemtype("M");
					info.setItemlength(150);
					info.setItemdesc(ResourceFactory.getProperty("gz_new.gz_accounting.subject"));
					info.setCodesetid("0");
					info.setAlign("left");
				} else if ("aid".equalsIgnoreCase((String) list.get(i))) {
					info.setItemid("aid");
					info.setItemtype("M");
					info.setItemdesc("aid");
					info.setCodesetid("0");
					info.setAlign("left");
				} else if ("personid".equalsIgnoreCase((String) list.get(i))) {
					info.setItemid("personid");
					info.setItemtype("A");
					info.setItemdesc("personid");
					info.setCodesetid("0");
					info.setAlign("left");
				} else if ("nbase".equalsIgnoreCase((String) list.get(i))) {
					info.setItemid("nbase");
					info.setItemtype("M");
					info.setItemdesc("nbase");
					info.setCodesetid("0");
					info.setAlign("left");
				} else if ("I9999".equalsIgnoreCase((String) list.get(i))) {
					info.setItemid("I9999");
					info.setItemtype("M");
					info.setItemdesc("I9999");
					info.setCodesetid("0");
					info.setAlign("left");
				} else if ("a0100".equalsIgnoreCase((String) list.get(i))) {
					info.setItemid("a0100");
					info.setItemtype("M");
					info.setItemdesc("a0100");
					info.setCodesetid("0");
					info.setAlign("left");
				} else if ("wid".equalsIgnoreCase((String) list.get(i))) {
					info.setItemid("wid");
					info.setItemtype("M");
					info.setItemdesc("wid");
					info.setCodesetid("0");
					info.setAlign("left");
				} else {
					throw new GeneralException(ResourceFactory.getProperty("gz_new.gz_accounting.columnerror"));//列表头定义错误
				}

				columnList.add(info);
			}// for end

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		return columnList;
	}

	/**
	 * 取得显示页面字段
	 * 
	 * @param fieldList 列表字段
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList<ColumnsInfo> toColumnsInfo(ArrayList<FieldItem> fieldList) throws GeneralException {
		ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
		try {
			for (int i = 0; i < fieldList.size(); i++) {
				FieldItem item = (FieldItem) fieldList.get(i);
				ColumnsInfo info = new ColumnsInfo(item);
				info.setColumnWidth(200);
				if ("send_ok".equalsIgnoreCase(item.getItemid())) {
					//定义前台过滤条件的函数，sendMsg.js
					info.setOperationData("sendMsgScope.showFilterColumn()");
					//渲染函数，将状态替换为图片
					info.setRendererFunc("sendMsgScope.showSendOkImg");
					info.setColumnWidth(100);
					info.setTextAlign("center");
				}
				if ("subject".equalsIgnoreCase(item.getItemid())) {
					//渲染函数，预览通知内容
					info.setRendererFunc("sendMsgScope.showMsgTemplate");
				}
				if ("a0101".equalsIgnoreCase(item.getItemid())) {
					info.setColumnWidth(100);
				}
				if ("send_ok".equalsIgnoreCase(item.getItemid())) {
					info.setColumnWidth(65);
				}
				if ("b0110".equalsIgnoreCase(item.getItemid())|| "e0122".equalsIgnoreCase(item.getItemid())) {
					info.setCtrltype("3");
					info.setNmodule("1");
				}
				if ("send_time".equalsIgnoreCase(item.getItemid())) {
					info.setColumnLength(7);
					info.setTextAlign("center");
					info.setColumnWidth(100);
				}
				if ("a00z3".equalsIgnoreCase(item.getItemid())) {
					info.setTextAlign("center");
					info.setColumnWidth(80);
				}
				if ("aid".equalsIgnoreCase(item.getItemid())
						|| "personid".equalsIgnoreCase(item.getItemid())
						|| "nbase".equalsIgnoreCase(item.getItemid())
						|| "I9999".equalsIgnoreCase(item.getItemid())
						|| "a0100".equalsIgnoreCase(item.getItemid())
						|| "wid".equalsIgnoreCase(item.getItemid())) {
					info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
				}
				list.add(info);

			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}

	/**
	 * 动态创建邮件发送标志列,并设置初始值
	 * @throws GeneralException 
	 * 
	 */
	public void updateTableCloumns() throws GeneralException {
		try {
			String tablename = "email_content";
			RecordVo vo = new RecordVo(tablename);
			Table table = new Table(tablename);
			int num = 0;
			if (!vo.hasAttribute("send_ok"))// 发送标识
			{
				Field obj = new Field("send_ok", "send_ok");
				obj.setDatatype(DataType.INT);
				obj.setKeyable(false);
				obj.setVisible(false);
				obj.setAlign("left");
				table.addField(obj);
				num++;
			}
			if (num > 0) {
				DbWizard dbWizard = new DbWizard(this.conn);
				dbWizard.addColumns(table);
				DBMetaModel dbmodel = new DBMetaModel(this.conn);
				dbmodel.reloadTableModel(tablename);
				/** 新建send-ok列.将值赋为0 */
				StringBuffer sql = new StringBuffer();
				sql.append("update email_content set send_ok=0");
				ContentDAO dao = new ContentDAO(this.conn);
				dao.update(sql.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * 取得按时间查询记录的sql语句
	 * 
	 * @return
	 * @throws GeneralException 
	 */
	public String queryRecordByTime(String value) throws GeneralException {
		StringBuffer buf = new StringBuffer("");
		try {
			if (value == null || "".equals(value) || "0".equals(value)) {
				return buf.toString();
			}
			buf.append(Sql_switcher.year("send_time"));
			buf.append("=");
			buf.append(value.split("-")[0]);
			buf.append(" and ");
			buf.append(Sql_switcher.month("send_time"));
			buf.append("=");
			buf.append(value.split("-")[1]);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return buf.toString();
	}

	/**
	 * 根据条件查找人员
	 * 
	 * @param column
	 *            加条件的列
	 * @param value
	 *            条件值
	 * @param templateId
	 *            模板号
	 * @return
	 * @throws GeneralException 
	 */
	public String getSearchPersonByCondSql(String column, String value,
			String templateId,  String tableName,
			String timesql, String priv_mode, String privSql, 
			String order_by, String orderBy,String salaryid) throws GeneralException {
		StringBuffer sql = new StringBuffer();
		try {
			//-------------------------------------新增了四个字段，判断下 zhaoxg add 2016-9-2--------------------------
			DbWizard dbw = new DbWizard(this.conn);
			if(!dbw.isExistField("email_content","guidkey",false))
			{
				Table table=new Table("email_content");
				Field field=new Field("guidkey","guidkey");
				field.setDatatype(DataType.STRING);
				field.setLength(38);
				table.addField(field);	
				dbw.addColumns(table);
			}
			if(!dbw.isExistField("email_content","module_type",false))
			{
				Table table=new Table("email_content");
				Field field=new Field("module_type","module_type");
				field.setDatatype(DataType.INT);
				table.addField(field);	
				dbw.addColumns(table);
			}
			if(!dbw.isExistField("email_content","module_id",false))
			{
				Table table=new Table("email_content");
				Field field=new Field("module_id","module_id");
				field.setDatatype(DataType.INT);
				table.addField(field);	
				dbw.addColumns(table);
			}
			if(!dbw.isExistField("email_content","create_time",false))
			{
				Table table=new Table("email_content");
				Field field=new Field("create_time","create_time");
				field.setDatatype(DataType.DATE);
				table.addField(field);	
				dbw.addColumns(table);
			}
			if(!dbw.isExistField("email_content","a00z3",false))
			{
				Table table=new Table("email_content");
				Field field=new Field("a00z3","a00z3");
				field.setDatatype(DataType.INT);
				table.addField(field);	
				dbw.addColumns(table);
			}
			//-----------------------------------------------end--------------------------------------------------
			if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
				sql.append("select s.a0100,s.nbase,s.nbase||s.a0100||'~'||"+Sql_switcher.isnull(Sql_switcher.dateToChar("send_time", "YYYY-MM-DD"),"''")+"||'~'||"+Sql_switcher.isnull(Sql_switcher.numberToChar("a.a00z3"),"''")+"  as personid,s.b0110,s.e0122,s.a0101,a.id as aid,a.send_ok,a.send_time,a.a00z3,a.subject,I9999,address,a.wid");
			}else {
				sql.append("select s.a0100,s.nbase,s.nbase+s.a0100+'~'+"+Sql_switcher.isnull(Sql_switcher.dateToChar("send_time", "YYYY-MM-DD"),"''")+"+'~'+"+Sql_switcher.isnull(Sql_switcher.numberToChar("a.a00z3"),"''")+"  as personid,s.b0110,s.e0122,s.a0101,a.id as aid,a.send_ok,a.send_time,a.a00z3,a.subject,I9999,address,a.wid");
			}

			sql.append(" from ( select  distinct a0100,upper(nbase) nbase,b0110,e0122,a0101  from " + tableName);
			String salaryFilterSql=this.getSalaryTableFilterSql(salaryid);
			if(StringUtils.isNotBlank(salaryFilterSql)){
				sql.append(" where 1=1 ").append(salaryFilterSql);
			}
			sql.append(" ) s ");
			if ("3".equals(value))
				sql.append(" left join ");
			else
				sql.append(" , ");
			sql.append(" (select e.id,e.a0100,e.pre,e.send_ok,e.subject,e.I9999,case when address is null then '' else address end as address ,e.send_time,e.wid,e.a00z3 from email_content e where e.id=");
			sql.append(templateId);
			
			if (!"3".equals(value))
				sql.append(" and e." + column + "='" + value + "' ");
			if (!(timesql == null || "".equals(timesql))) {
				sql.append(" and ");
				sql.append(timesql);
			}
			String username=tableName.split("_salary_")[0];
			sql.append(" and e.module_type=34 and e.module_id="+salaryid); //薪资模块 + 某个薪资账套的邮件
			sql.append(" and e.username='"+username+"' ");
			sql.append(" ) a ");

			if ("3".equals(value)) {
				sql.append(" on s.a0100=a.a0100 and lower(a.pre)=lower(s.nbase) ");
				sql.append(" where 1=1 ");
			} else {
				sql.append(" where 1=1 and s.a0100=a.a0100 and lower(a.pre)=lower(s.nbase)  ");
			}

			if ("1".equals(priv_mode)) {
				if (privSql != null && privSql.trim().length() > 0) {
					sql.append(" " + privSql);
				}
			}
		/*
			sql.append("  and a00z1=(select max(a00z1) from "
							+ tableName
							+ " X where X.a0100=s.a0100 and X.nbase=s.nbase and X.a00z0=s.a00z0) ");
*/
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return sql.toString();
	}

	/**
	 * 取得所有薪资发放的邮件模板
	 * 
	 * @return ArrayList
	 * @throws GeneralException 
	 */
	public ArrayList getEmailTemplateList(int type) throws GeneralException {
		ArrayList list = new ArrayList();
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("select id,name from email_name where nmodule=2 order by id");
			ContentDAO dao = new ContentDAO(this.conn);
			list = dao.searchDynaList(buf.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}

	/**
	 * 首次进入邮件发送页面,默认为id最小的模板
	 * 
	 * @return
	 * @throws GeneralException 
	 */
	public int getMinTemplateId(String nmodule) throws GeneralException {
		int n = 0;
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("select min(id) from email_name where nmodule="+nmodule);
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(buf.toString());
			while (rs.next()) {
				n = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return n;

	}

	/**
	 * 取得邮件模板信息
	 * @param templateid 邮件模板id
	 * @return
	 * @throws GeneralException
	 */
	public LazyDynaBean getTemplateInfo(String templateid) throws GeneralException {
		LazyDynaBean bean = new LazyDynaBean();
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer buf = new StringBuffer();
			buf.append("select address,subject,content from email_name where id="
					+ templateid);
			rs = dao.search(buf.toString());
			while (rs.next()) {
				String emailField = rs.getString("address");
				if (emailField != null && emailField.trim().length() > 0) {
					emailField = emailField.substring(0,
							emailField.indexOf(":")).trim();
				}
				bean.set("address", emailField);
				bean.set("subject", rs.getString("subject") == null ? "" : rs
						.getString("subject"));
				bean.set("content", rs.getString("content") == null ? "" : rs
						.getString("content"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return bean;
	}


	/**
	 * 取得模板指标或公式的信息
	 * 
	 * @param templateId
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList<LazyDynaBean> getTemplateFieldInfo(int templateId, int type) throws GeneralException {
		ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
		RowSet rs = null;
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("select * from email_field where id=?" );
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList templist=new ArrayList();
            templist.add(templateId);
			rs = dao.search(buf.toString(),templist);
			while (rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("id", String.valueOf(templateId));
				bean.set("fieldid", rs.getString("fieldid") == null ? "" : rs
						.getString("fieldid").trim());
				bean.set("fieldtitle", rs.getString("fieldtitle") == null ? ""
						: rs.getString("fieldtitle").trim());
				bean.set("fieldtype", rs.getString("fieldtype"));
				if (type == 1) {
					bean.set("fieldcontent", rs.getString("fieldcontent"));
				} else {
					bean.set("fieldcontent", rs.getString("fieldcontent"));
				}
				bean.set("dateformat", rs.getString("dateformat"));
				bean.set("fieldlen", rs.getString("fieldlen"));
				bean.set("ndec", rs.getString("ndec"));
				bean.set("codeset", rs.getString("codeset") == null ? "" : rs
						.getString("codeset").trim());
				bean.set("nflag", rs.getString("nflag"));
				list.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return list;
	}

	/**
	 * 取得模板的人员所在的库和a0100
	 *
	 * @param templateId
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getEmailContentA0100(String templateId, String tableName,
			String type, String selectedid) throws GeneralException {
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select s.nbase,s.a0100 from ");
			sql.append(tableName + " s where 1=1 ");
			if ("1".equals(type) && selectedid != null
					&& !"".equals(selectedid)) {
				String sqlstr = this.getSql(selectedid, "s");
				sql.append(" and " + sqlstr);
			}
			sql.append(" and ");
			sql.append(" a00z1=(select max(a00z1) from "
							+ tableName
							+ " X where X.a0100=s.a0100 and lower(X.nbase)=lower(s.nbase) and X.a00z0=s.a00z0) ");
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString());
			while (rs.next()) {
				list.add(rs.getString("nbase") + rs.getString("a0100"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return list;
	}

	private ArrayList midVList = new ArrayList();
	private ArrayList allFielditemList = new ArrayList();

	/**
	 * 生成邮件方法
	 * @param templateFieldList 模板项目列表
	 * @param templateId 模板id
	 * @param emailContentA0100List 模板人员
	 * @param content 模板
	 * @param userview
	 * @param salaryid
	 * @param tableN 临时表名
	 * @param appdate 业务日期
	 * @param count 模板内容
     * @param selectType 人员范围 0 全部，1所选
     * @param selectedid 人员范围过滤条件
	 * @throws GeneralException
	 */
	public void updateEmailContent( ArrayList<LazyDynaBean> templateFieldList,
			String templateId, ArrayList<HashMap<String,String>> emailContentA0100List, String content,
			UserView userview, String salaryid, String tableN,String appdate,String count,String subject,String selectType,String selectedid) throws GeneralException {
		Connection _con = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			String cont = "";
			HashMap salaryItemMap = null;
			this.allFielditemList = DataDictionary.getAllFieldItemList(
					Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);

			salaryItemMap = this.getSalarySetMap(salaryid);
			midVList = this.getMidVList(salaryid);
			this.allFielditemList.addAll(midVList);
			ArrayList valueList = new ArrayList();
			//xiegh 20170508 bug27457
			StringBuffer sqlbuffer = new StringBuffer();

            String username=tableN.split("_salary_")[0];
            String[] date = appdate.split("-");


            //-----------------------------------生成之前先删除原有的邮件 zhanghua---------------------------
            StringBuffer str = new StringBuffer();
            str.append("delete from email_content where ");
            ArrayList<Object> datalist=new ArrayList<Object>();
            if ("1".equals(selectType) && selectedid != null&& !"".equals(selectedid)){//是按选择的人
                str.append(" module_type=34 and module_id=?");
                datalist.add(salaryid);
                //	str.append(" and send_time ="+Sql_switcher.dateValue(appdate));
                str.append(" and ").append(Sql_switcher.dateToChar("send_time", "YYYY-MM-DD")).append("=?");
                datalist.add(appdate);
                str.append(" and id=? ");
                datalist.add(templateId);
                str.append(" and (");
                StringBuffer _str = new StringBuffer();
                String[] arr = selectedid.split(",");
                for (int i = 0; i < arr.length; i++) {
                    if (arr[i] == null || "".equals(arr[i]))
                        continue;
                    String[] column = arr[i].split("~");
                    _str.append(" or  ( UPPER(pre)=? and a0100=? )");
                    datalist.add(column[0].substring(0, 3).toUpperCase());
                    datalist.add(column[0].substring(3).toUpperCase());
                }
                str.append(_str.substring(4));
                str.append(")");
            }else{//所有
                str.append(" module_type=34 and module_id=?");
                datalist.add(salaryid);
                str.append(" and ").append(Sql_switcher.dateToChar("send_time", "YYYY-MM-DD")).append("=? " );
                datalist.add(appdate);
                str.append(" and id=? ");
                datalist.add(templateId);

				String salaryFilterSql=this.getSalaryTableFilterSql(salaryid);
				if(StringUtils.isNotBlank(salaryFilterSql)){
					str.append(" and EXISTS (select * from ").append(tableN).append(" where 1=1 ").append(salaryFilterSql);
					str.append(" and UPPER(email_content.pre)=UPPER(").append(tableN).append(".nbase) and ");
					str.append(" email_content.A0100=").append(tableN).append(".A0100 )");
				}


            }
            str.append(" and a00z3=").append(count).append("  and username='").append(username).append("'");
            dao.delete(str.toString(),datalist);
            //-----------------------------------------------------------删除 end--------------------------------------




            sqlbuffer.append(" INSERT  INTO email_content ( id ,UserName ,Subject ,Pre ,A0000 ,A0100 ,B0110 ,E01A1 ,guidkey ,Address,content,send_ok,module_type ,module_id ,create_time ,I9999 ,a00z3,send_time )  VALUES ");
            sqlbuffer.append(" (?,?,?,?,?,?,?,?,?,?,?,0,34,?,").append(Sql_switcher.today()).append(",0,?,").append(Sql_switcher.dateValue(appdate)).append(") ");

            //区分公式和指标，指标批量更新
			ArrayList<LazyDynaBean> fieldList=new ArrayList<LazyDynaBean>();//模板指标
            ArrayList<LazyDynaBean> formulaFieldList=new ArrayList<LazyDynaBean>();//公式
            for (LazyDynaBean bean : templateFieldList) {
                String nflag = (String) bean.get("nflag");
                if("0".equalsIgnoreCase(nflag))
                    fieldList.add(bean);
                else
                    formulaFieldList.add(bean);
            }
            String strSql=this.getContentFieldSql(fieldList);//获取更新列名
            HashMap<String,String>contentMap=new HashMap<String, String>();
            //遍历所有人 生成并插入邮件模板
            for (int i = 0, j=0; i < emailContentA0100List.size(); i++) {
                String prea0100 = emailContentA0100List.get(i).get("nbase")+emailContentA0100List.get(i).get("a0100");
                String t_content=content;
                if(formulaFieldList.size()>0)//计算模板包含的公式
                    t_content= this.getFactContentFromSalaryTempTable(content,
                            prea0100, formulaFieldList, userview, tableN,
                            salaryItemMap);
                contentMap.put(prea0100,t_content);
                if((i%200==0&&i!=0)||i==emailContentA0100List.size()-1){//每200人 批量插入一次模板
                    if(fieldList.size()>0)//批量获取模板中包含的指标
                        contentMap=this.replaceFieldContent(fieldList, strSql, contentMap,tableN);
                    while(j<i+1){
                        HashMap<String,String> map=emailContentA0100List.get(j);
                        String a0100 = map.get("nbase")+map.get("a0100");
                        String pcontent=contentMap.containsKey(a0100)?contentMap.get(a0100):"";
                        ArrayList list = new ArrayList();
                        list.add(templateId);
                        list.add(username);
                        list.add(subject);
                        list.add(map.get("nbase"));
                        list.add(map.get("a0000"));
                        list.add(map.get("a0100"));
                        list.add(map.get("b0110"));
                        list.add(map.get("e01a1"));
                        list.add(map.get("guidkey"));
                        String address=map.get("address");
                        if(StringUtils.isBlank(address))
                            address="";
                        list.add(address);
                        list.add(pcontent);
                        list.add(salaryid);
                        list.add(count);
                        valueList.add(list);
                        j++;
                    }
                    dao.batchUpdate(sqlbuffer.toString(), valueList);
                    valueList.clear();
                    contentMap.clear();
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

    /**
     * 批量生成邮件模板中的指标项
     * @param templateFieldList
     * @param fieldSql
     * @param contentMap
     * @param tableN
     * @return
     * @author ZhangHua
     * @date 10:53 2017/12/25
     * @throws GeneralException
     */
	private HashMap<String,String> replaceFieldContent(ArrayList<LazyDynaBean> templateFieldList,String fieldSql,
                                                       HashMap<String,String> contentMap,String tableN) throws GeneralException {
	    try{
	        ContentDAO dao = new ContentDAO(this.conn);
            RowSet rs=null;
	        HashMap<String,ArrayList<String>> nbaseMap =new HashMap<String, ArrayList<String>>();
	        StringBuffer strSql=new StringBuffer();
	        for(Map.Entry<String, String> map:contentMap.entrySet()){//区分人员库
                String prea0100 = map.getKey();
                String nbase=prea0100.substring(0, 3).toUpperCase();
                if(nbaseMap.containsKey(nbase)){
                    nbaseMap.get(nbase).add(prea0100);
                }else{
                    ArrayList<String> list=new ArrayList<String>();
                    list.add(prea0100);
                    nbaseMap.put(nbase,list);
                }
	        }

            for(Map.Entry<String,ArrayList<String>> map:nbaseMap.entrySet()) {
                String nbase=map.getKey();
                ArrayList<String> list= map.getValue();
                StringBuffer strA0100=new StringBuffer();
                ArrayList<String> a0100List=new ArrayList<String>();
                for (String s : list) {
                    a0100List.add(s.substring(3));
                    strA0100.append("?,");
                }
                if(strA0100.length()>0)
                    strA0100.deleteCharAt(strA0100.length()-1);
                strSql.setLength(0);
                strSql.append("select ").append(fieldSql).append(" ,a0100,nbase from ").append(tableN);
                strSql.append(" where a0100 in(").append(strA0100).append(") ");
                strSql.append(" and upper(nbase)=?");
                strSql.append(" group by a0100,nbase ");
                a0100List.add(nbase.toUpperCase());
                rs=dao.search(strSql.toString(),a0100List);

                while(rs.next()){

                    String prea0100=rs.getString("nbase")+rs.getString("a0100");
                    String fact_content=contentMap.get(prea0100);
                    for (LazyDynaBean bean : templateFieldList) {
                        String fieldtitle = (String) bean.get("fieldtitle");
                        String fieldtype = (String) bean.get("fieldtype");
                        String fieldcontent = (String) bean.get("fieldcontent");
                        String fieldid = (String) bean.get("fieldid");
                        String dateformat = (String) bean.get("dateformat");
                        String fieldlen = (String) bean.get("fieldlen");
                        String ndec = (String) bean.get("ndec");
                        String codeset = (String) (bean.get("codeset") == null ? ""
                                : bean.get("codeset"));
                        String nflag = (String) bean.get("nflag");
                        String replace = "";// 要被替换的内容
                        String factcontent = "";
                        String setid = "";
                        if ("0".equals(nflag)) {
                            replace = "\\$" + fieldid + ":" + fieldtitle.trim() + "\\$";
                        }
                        if ("1".equals(nflag)) {
                            replace = "\\#" + fieldid + ":" + fieldtitle.trim() + "\\#";
                        }

                        if ("D".equalsIgnoreCase(fieldtype.trim())
                                && Sql_switcher.searchDbServer() == Constant.ORACEL) {
                            Date dd = rs.getDate(fieldcontent.trim());
                            if (dd != null) {
                                factcontent = dd.toString();
                            }
                        } else {
                            factcontent = rs.getString(fieldcontent.trim());

                        }
                        if (codeset != null && !"0".equalsIgnoreCase(codeset)
                                && "A".equalsIgnoreCase(fieldtype.trim()))// 代码型
                        {
                            factcontent = AdminCode.getCodeName(codeset,
                                    factcontent);
                        }

                        /** 日期型按格式显示 */
                        if ("D".equalsIgnoreCase(fieldtype.trim())
                                && !(dateformat == null || ""
                                .equals(dateformat)))
                            factcontent = this.getYMDFormat(Integer
                                    .parseInt(dateformat), factcontent);
                        /** 数值型按格式显示 */
                        if ("N".equalsIgnoreCase(fieldtype.trim()))
                            factcontent = this.getNumberFormat(Integer
                                            .parseInt(fieldlen),
                                    Integer.parseInt(ndec), factcontent);
                        if (factcontent == null
                                || factcontent.trim().length() == 0) {
                            if ("N".equalsIgnoreCase(fieldtype.trim()))
                                factcontent = "0.0";
                            else
                                factcontent = " ";
                        }
                        fact_content = fact_content.replaceAll(replace,
                                factcontent);
                    }
                    contentMap.put(prea0100,fact_content);
                }
            }

	    }catch (Exception e){
	        e.printStackTrace();
            if(e.toString().indexOf("标识符无效")!=-1||e.toString().indexOf("无效")!=-1||e.toString().indexOf("invalid identifier")!=-1){
                String strField="";
                String strErr=e.getMessage();
                if(strErr.indexOf("'")>0) {
                    strField = strErr.substring(strErr.indexOf("'") + 1, strErr.indexOf("'") + 6);
                    if(DataDictionary.getFieldItem(strField)!=null){
                        strField=DataDictionary.getFieldItem(strField).getItemdesc();
                    }
                }
                if(StringUtils.isNotBlank(strField))
                    throw GeneralExceptionHandler.Handle(new Exception("模板中【"+strField+"】定义有误（请检查当前薪资类别是否缺少相应的薪资项目）!"));
                else
                    throw GeneralExceptionHandler.Handle(e);
            }else{
                throw GeneralExceptionHandler.Handle(e);
            }
	    }
        return contentMap;
    }

    /**
     * 获取所有邮件模板涉及到的指标的查询sql
     * @param  templateFieldList 涉及到的列
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 13:20 2017/12/21
     * */
    private String getContentFieldSql(ArrayList<LazyDynaBean> templateFieldList) throws GeneralException {
	    StringBuffer fieldSql=new StringBuffer();
        try{
        	String varcharLength="MAX";
        	//【60443】VFS+UTF-8+达梦：薪资管理/薪资发放/发送通知/生成通知报错
			if(Sql_switcher.searchDbServer()!=2){
				DatabaseMetaData dbMeta = this.conn.getMetaData();
				if(dbMeta.getDatabaseMajorVersion()==8) {// sql2000=8 sql2005=9 sql2008=10 sql2012=11
					varcharLength="4000";
				}
			}

            for(LazyDynaBean bean:templateFieldList){
                String fieldtype = (String) bean.get("fieldtype");
                String fieldcontent = (String) bean.get("fieldcontent");
                if("N".equalsIgnoreCase(fieldtype))
                    fieldSql.append("sum(").append(fieldcontent).append(") as ").append(fieldcontent).append(",");
                else if("M".equalsIgnoreCase(fieldtype)){
					fieldSql.append("max(CAST(").append(fieldcontent).append(" AS NVARCHAR("+varcharLength+"))) as ").append(fieldcontent).append(",");
				}else
                    fieldSql.append("max(").append(fieldcontent).append(") as ").append(fieldcontent).append(",");
            }
            if(fieldSql.length()>0)
                fieldSql.deleteCharAt(fieldSql.length()-1);
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return fieldSql.toString();
    }

    /**
     * 取得不在a01子集中的邮件地址
     * @param emailField
     * @param emailFieldSet
     * @param list
     * @author ZhangHua
     * @date 17:53 2017/12/23
     * @throws GeneralException
     */
	public void getEmailValue(String emailField, String emailFieldSet, ArrayList<HashMap<String,String>> list) throws GeneralException {

		HashMap<String,String> addressMap=new HashMap<String, String>();
        HashMap<String,ArrayList<String>> nbaseMap =new HashMap<String, ArrayList<String>>();
        RowSet rs=null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            for (HashMap<String, String> stringHashMap : list) {
                String prea0100 = stringHashMap.get("a0100");
                String nbase=stringHashMap.get("nbase");

                if(nbaseMap.containsKey(nbase)){
                    nbaseMap.get(nbase).add(prea0100);
                }else{
                    ArrayList<String> templist=new ArrayList<String>();
                    templist.add(prea0100);
                    nbaseMap.put(nbase,templist);
                }
            }


            for(Map.Entry<String,ArrayList<String>> map:nbaseMap.entrySet()){
                StringBuffer strSql = new StringBuffer();
                String nbase=map.getKey();
                ArrayList<String> tempList=map.getValue();
                strSql.append(" select a.a0100,a."+emailField+" address from ");
                strSql.append(nbase + emailFieldSet+" a ");
                strSql.append(" INNER JOIN ( SELECT a0100,MAX(i9999) AS i9999  FROM "+nbase + emailFieldSet+" where a0100 in(");
                ArrayList<String> a0100List=new ArrayList<String>();
                StringBuffer strA0100=new StringBuffer();
                for (int i = 0; i <tempList.size() ; i++) {
                    strA0100.append("?,");
                    a0100List.add(tempList.get(i));
                    if((i!=0&&i%500==0)||i==tempList.size()-1){
                        String str=strSql.toString()+strA0100.deleteCharAt(strA0100.length()-1)+" ) GROUP BY a0100) t ON t.A0100=a.A0100 AND  t.i9999=a.i9999";
                        rs=dao.search(str,a0100List);
                        while (rs.next()){
                            addressMap.put(nbase+rs.getString("a0100"),rs.getString("address"));
                        }
                        strA0100.setLength(0);
                        a0100List.clear();
                    }
                }
            }

            for (HashMap<String, String> map : list) {
                String prea0100 = map.get("a0100");
                String nbase=map.get("nbase");
                String strAddress=addressMap.get(nbase+prea0100);
                if(StringUtils.isBlank(strAddress))
                    strAddress="";
                map.put("address",strAddress);
            }

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

	private String inserttime = "";

	public String getInserttime() {
		return inserttime;
	}

	public void setInserttime(String inserttime) {
		this.inserttime = inserttime;
	}

	/**
	 * 将基本信息导入邮件内容表
	 * 
	 * @param templateId
	 *            邮件模板id
	 * @param subject
	 *            邮件标题
	 * @param tableName
	 *            薪资表名
	 *            单位或部门过滤
	 * @param email_field
	 *            邮件指标
	 * @param userView
	 *            username
	 * @throws GeneralException 
	 */
	public ArrayList<HashMap<String,String>> exportPersonBaseIntoContent(String templateId, String subject,
			String tableName,String email_field,
			UserView userView, String type, String selectedid,String appdate,String module_id,String count) throws GeneralException {
        ArrayList<HashMap<String,String>> dataList=new ArrayList<HashMap<String, String>>();
		try {
			updateTableCloumns();
			StringBuffer buf = new StringBuffer();
			boolean ismain = false;
			FieldItem item = DataDictionary.getFieldItem(email_field.toLowerCase());
			if(item==null||item.getFieldsetid()==null){
				throw GeneralExceptionHandler.Handle(new Exception("邮件地址指标"+email_field+"不存在，请联系管理员！"));
			}
			String username=tableName.split("_salary_")[0];
			ContentDAO dao = new ContentDAO(this.conn);

			String[] date = appdate.split("-");
            String sql = "";
            if ("1".equals(type) && selectedid != null&& !"".equals(selectedid)) {//是按选择的人
                sql = this.getSql(selectedid, "a");
            }

			if ("A01".equalsIgnoreCase(item.getFieldsetid()))
				ismain = true;
			ArrayList preList = getNbaseList();
			for (int i = 0; i < preList.size(); i++) {
				CommonData data = (CommonData) preList.get(i);
				if ("#".equals(data.getDataValue()))
					continue;
				String pre = data.getDataValue();
				buf.append(" select");
				buf.append(" a.nbase,u.a0000,u.a0100,u.b0110,u.e01a1,u.guidkey ");
				if (ismain)
					buf.append(",u."+email_field+" as address ");
				else
					buf.append(",'' as address ");

				buf.append(" from (select distinct nbase ,a0100 from ").append(tableName);

				String salaryFilterSql=this.getSalaryTableFilterSql(tableName.split("_salary_")[1]);
				if(StringUtils.isNotBlank(salaryFilterSql)){
					buf.append(" where 1=1 ").append(salaryFilterSql);
				}

				buf.append(" ) a,").append(pre).append("a01 u where u.a0100=");
				buf.append("a.a0100 and UPPER(a.nbase)='"+pre.toUpperCase()+"' ");
				if ("1".equals(type) && selectedid != null&& !"".equals(selectedid)){
					buf.append(" and ");
					buf.append(sql);
				} 
				if(preList.size()-1!=i)
					buf.append(" union ");
			}

			RowSet rs=dao.search(buf.toString());

			while(rs.next()){
                HashMap<String,String> map=new HashMap<String, String>();
                map.put("nbase",rs.getString("nbase"));
                map.put("a0000",rs.getString("a0000"));
                map.put("a0100",rs.getString("a0100"));
                map.put("b0110",rs.getString("b0110"));
                map.put("e01a1",rs.getString("e01a1"));
                map.put("guidkey",rs.getString("guidkey"));
                map.put("address",rs.getString("address"));
                dataList.add(map);
            }

			//buf.append(") temp");
		//	dao.insert(buf.toString(), new ArrayList());
		//	configI9999(null, templateId, appdate, dao);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return dataList;
	}

	/**
	 * 更新I9999的方法
	 * @param wid
	 * @param templateId
	 * @param time
	 * @param dao
	 * @throws GeneralException
	 */
	public void configI9999(String wid, String templateId, String time,ContentDAO dao) throws GeneralException {
		try {
			int year=Integer.parseInt(time.substring(0,4)); 
			int month = Integer.parseInt(time.substring(5, 7));
			String yearMonth = time.substring(0,7);
			StringBuffer sql = new StringBuffer();
			sql.append("update email_content set I9999=");
			sql.append("( select aa.i9999 from ");
			sql.append("(select max(i9999)+1 as I9999 ,a0100,pre from email_content where 1=1");
			if (Sql_switcher.searchDbServer() == Constant.ORACEL){
				sql.append(" and send_time=to_date('"+time+"','yyyy-mm-dd hh24:mi:ss')");
			}
			else{
				sql.append(" and year(send_time)="+year);
				sql.append(" and month(send_time)="+month);
			}
			sql.append(" group by  pre, a0100) aa where email_content.a0100=aa.a0100 and email_content.pre=aa.pre  ) ");
			sql.append("where exists (");
			sql.append("select null from ");
			sql.append("(select max(i9999)+1 as I9999,a0100,pre  from email_content   where 1=1 ");
			if (Sql_switcher.searchDbServer() == Constant.ORACEL)
				sql.append(" and send_time=to_date('"+time+"','yyyy-mm-dd hh24:mi:ss')");
			else{
				sql.append(" and year(send_time)="+year);
				sql.append(" and month(send_time)="+month);
			}
			sql.append(" group by pre, a0100 ) aa where email_content.a0100=aa.a0100 and email_content.pre=aa.pre )");
			if (Sql_switcher.searchDbServer() == Constant.ORACEL)
				sql.append(" and send_time=to_date('"+time+"','yyyy-mm-dd hh24:mi:ss')");
			else{
				sql.append(" and year(send_time)="+year);
				sql.append(" and month(send_time)="+month);
			}
			if (wid != null && !"".equals(wid)) {
				sql.append(" and email_content.wid=" + wid);
			} else {
				sql.append(" and email_content.wid is null ");
			}
			sql.append(" and email_content.id="+templateId);
			sql.append(" and i9999=0");
			dao.update(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	private RecordVo salaryTemplateVo;

	public void setSalaryTemplateVo(RecordVo vo) {
		this.salaryTemplateVo = vo;
	}

	/**
	 * 取得人员库列表
	 * 
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getNbaseList() throws GeneralException {
		ArrayList list = new ArrayList();
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("select pre,dbname from dbname order by dbid");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(buf.toString());
			String cbase = "";
			if (this.salaryTemplateVo != null) {
				cbase = "," + salaryTemplateVo.getString("cbase");
			}
			while (rs.next()) {
				if (!"".equals(cbase)
						&& (cbase.indexOf(","
								+ rs.getString("pre") + ",") == -1))
					continue;
				list.add(new CommonData(rs.getString("pre"), rs
						.getString("dbname")));
			}
			list.add(0, new CommonData("#", "      "));
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}

	/**
	 * 取得指标的主集id
	 * 
	 * @param itemid
	 * @return
	 * @throws GeneralException 
	 */
	public String getFieldSetId(String itemid) throws GeneralException {
		StringBuffer sql = new StringBuffer();
		sql.append("select fieldsetid from fielditem where UPPER(itemid)=?");
		PreparedStatement pstmt = null;
		ContentDAO dao = new ContentDAO(this.conn);
		ResultSet rs = null;
		String fieldSet = "";
		try {
			ArrayList paramList = new ArrayList();
			paramList.add(itemid.toUpperCase());
			rs = dao.search(sql.toString(), paramList);
			while (rs.next()) {
				fieldSet = rs.getString("fieldsetid");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(pstmt);
		}
		return fieldSet;
	}

	public ArrayList getMidVList(String salaryid) throws GeneralException {
		ArrayList fieldlist = new ArrayList();
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec from ");
			buf.append(" midvariable where nflag=0 and templetid=0 ");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rset = dao.search(buf.toString());
			while (rset.next()) {
				FieldItem item = new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid("A01");// 没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				switch (rset.getInt("ntype")) {
				case 1://
					item.setItemtype("N");
					break;
				case 2:
					item.setItemtype("A");
					break;
				case 4:
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return fieldlist;
	}
	/**
	 * 工资发放中直接从工资表里取数据,如果定义的公式和指标不在改工资套的项目中，内容不被替换
	 * 
	 * @param content 邮件内容
	 * @param prea0100 人员库前缀
	 * @param fieldList 
	 * @param uv
	 * @param tableN
	 * @return
	 * @throws GeneralException 
	 */
	public String getFactContentFromSalaryTempTable(String content,
			String prea0100, ArrayList fieldList, UserView uv, String tableN,
			HashMap salaryItemMap) throws GeneralException {
		String fact_content = content;
		try {
			String pre = prea0100.substring(0, 3);
			String a0100 = prea0100.substring(3);
			StringBuffer buf = new StringBuffer();
			StringBuffer table_name = new StringBuffer();
			HashSet name_set = new HashSet();
			StringBuffer where_sql = new StringBuffer();
			StringBuffer where_sql2 = new StringBuffer();
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			for (int i = 0; i < fieldList.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean) fieldList.get(i);
				String fieldtitle = (String) bean.get("fieldtitle");
				String fieldtype = (String) bean.get("fieldtype");
				String fieldcontent = (String) bean.get("fieldcontent");
				String fieldid = (String) bean.get("fieldid");
				String dateformat = (String) bean.get("dateformat");
				String fieldlen = (String) bean.get("fieldlen");
				String ndec = (String) bean.get("ndec");
				String nflag = (String) bean.get("nflag");
				String replace = "";// 要被替换的内容
				String factcontent = "";
				if ("0".equals(nflag)) {
					replace = "\\$" + fieldid + ":" + fieldtitle.trim() + "\\$";
				}
				if ("1".equals(nflag)) {
					replace = "\\#" + fieldid + ":" + fieldtitle.trim() + "\\#";
				}
				if ("1".equals(nflag))// 公式
				{

					try {
						YksjParser yp = new YksjParser(uv,
								this.allFielditemList, YksjParser.forNormal,
								getColumType(fieldtype.trim()), 3, pre, "");
						yp.run(fieldcontent);
						String temp = yp.getSQL();// 公式的结果
						buf.append("select ");
						if ("N".equalsIgnoreCase(fieldtype.trim()))
							buf.append(" sum");
						else
							buf.append(" max");
						buf.append("(" + temp + ") as T");
						buf.append(" from " + tableN);
						buf.append(" where a0100='" + a0100
								+ "' and UPPER(nbase)='" + pre.toUpperCase()
								+ "'");
						rs = dao.search(buf.toString());
						while (rs.next()) {
							String str = "";
							if ("D".equalsIgnoreCase(fieldtype.trim()) && Sql_switcher.searchDbServer() == Constant.ORACEL) {// 对于sqlserver用getString()
								Date dd = rs.getDate("T");
								if (dd != null) {
									str = dd.toString();
								}
							} else {
								str = rs.getString(1);
							}
							if (str != null) {
								factcontent = str;
							} else {
								factcontent = "";
							}
						}
					} catch (Exception e) {
						if(e.toString().indexOf("标识符无效")!=-1||e.toString().indexOf("无效")!=-1||e.toString().indexOf("invalid identifier")!=-1){
							throw GeneralExceptionHandler.Handle(new Exception("模板中【"+replace+"】所涉及到的内容在当前薪资类别中无效（请检查是否缺少相应的薪资项目）!"));
						}else{
							throw GeneralExceptionHandler.Handle(e);
						}
					}
					if (factcontent != null && !"".equals(factcontent)) {
						/** 日期型按格式显示 */
						if ("D".equalsIgnoreCase(fieldtype.trim())
								&& !(dateformat == null || ""
										.equals(dateformat)))
							factcontent = this.getYMDFormat(Integer
									.parseInt(dateformat), factcontent);
						/** 数值型按格式显示 */
						if ("N".equalsIgnoreCase(fieldtype.trim()))
							factcontent = this.getNumberFormat(Integer
									.parseInt(fieldlen),
									Integer.parseInt(ndec), factcontent);
					}
					//if (factcontent != null && !factcontent.equals(""))如果为没有值，将其值置为空，如果加了这个判断，会显出该公式名称，如#9:ddd#这种
					if (StringUtils.isBlank(factcontent))
						factcontent = "";
					fact_content = fact_content.replaceAll(replace,factcontent);
					
					buf.setLength(0);
					table_name.setLength(0);
					name_set.clear();
					where_sql.setLength(0);
					where_sql2.setLength(0);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return fact_content;
	}

	/**
	 * 获取指定id下薪资类别中定义的薪资项目，及临时变量中的变量名
	 * @param salaryid
	 * @return
	 * @throws GeneralException
	 */
	private HashMap getSalarySetMap(String salaryid) throws GeneralException {
		HashMap map = new HashMap();
		try {
			StringBuffer buf = new StringBuffer("");
			buf.append("select itemid from salaryset where salaryid="
					+ salaryid);
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(buf.toString());
			while (rs.next()) {
				map.put(rs.getString("itemid").toUpperCase(), "1");
			}
			StringBuffer sql = new StringBuffer(); 
			sql.append("select cname,chz from midvariable where templetid=0 and(cstate="
					+ salaryid + " or cstate is null) and nflag=0");
			rs = dao.search(sql.toString());
			while (rs.next()) {
				map.put(rs.getString("cname").toUpperCase(), "1");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}

	public String getSql(String c_expr, String varType, UserView uv,
			ArrayList listset) throws GeneralException {
		String temp = "";
		try {
			if (c_expr.trim().length() > 0) {
				ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
						Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
				YksjParser yp = new YksjParser(uv, alUsedFields,
						YksjParser.forNormal, getColumType(varType.trim()), 3,
						"usr", "");
				yp.run(c_expr);
				temp = yp.getSQL();// 公式的结果
				listset.addAll(yp.getUsedSets());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return temp;
	}

	/**
	 * 得到数据类型
	 * 
	 * @param type
	 * @return
	 */
	public int getColumType(String type) {
		int temp = 1;
		if ("A".equals(type)) {
			temp = IParserConstant.STRVALUE;
		} else if ("D".equals(type)) {
			temp = IParserConstant.DATEVALUE;
		} else if ("N".equals(type)) {
			temp = IParserConstant.FLOAT;
		} else if ("L".equals(type)) {
			temp = IParserConstant.LOGIC;
		} else {
			temp = IParserConstant.STRVALUE;
		}
		return temp;
	}

	/**
	 * 将时间指标或公式按指定格式显示
	 * 
	 * @param type
	 * @param time
	 * @return
	 * @throws GeneralException 
	 */
	public String getYMDFormat(int type, String time) throws GeneralException {
		String ret = "";
		String year = "";
		String month = "";
		String day = "";
		if (time == null || time.trim().length() <= 0) {
			return ret;
		}
		if (time.length() > 10)// 是带时分秒的格式
		{
			int index = time.indexOf(" ");
			time = time.substring(0, index);
		}
		String separapor = "";
		if (time.indexOf("-") != -1)
			separapor = "-";
		else if (time.indexOf(".") != -1)
			separapor = ".";
		year = time.substring(0, time.indexOf(separapor));
		month = time.substring(time.indexOf(separapor) + 1, time
				.lastIndexOf(separapor));
		day = time.substring(time.lastIndexOf(separapor) + 1);
		try {
			switch (type) {
			case 1: {
				ret = year + "." + month + "." + day;
				break;
			}
			case 2: {
				if (Integer.parseInt(month) < 10)
					month = month.substring(1);
				if (Integer.parseInt(day) < 10)
					day = day.substring(1);
				ret = year + "." + month + "." + day;
				break;
			}
			case 3: {
				if (Integer.parseInt(month) < 10)
					month = month.substring(1);
				if (Integer.parseInt(day) < 10)
					day = day.substring(1);
				year = year.substring(2);
				ret = year + "." + month + "." + day;
				break;
			}
			case 4: {
				ret = year + "." + month;
				break;
			}
			case 5: {
				if (Integer.parseInt(month) < 10)
					month = month.substring(1);
				ret = year + "." + month;
				break;
			}
			case 6: {
				year = year.substring(2);
				ret = year + "." + month;
				break;
			}
			case 7: {
				year = year.substring(2);
				if (Integer.parseInt(month) < 10)
					month = month.substring(1);
				ret = year + "." + month;
				break;
			}
			case 8: {
				ret = year + ResourceFactory.getProperty("datestyle.year")
						+ month
						+ ResourceFactory.getProperty("datestyle.month") + day
						+ ResourceFactory.getProperty("datestyle.day");
				break;
			}
			case 9: {
				if (Integer.parseInt(month) < 10)
					month = month.substring(1);
				if (Integer.parseInt(day) < 10)
					day = day.substring(1);
				ret = year + ResourceFactory.getProperty("datestyle.year")
						+ month
						+ ResourceFactory.getProperty("datestyle.month") + day
						+ ResourceFactory.getProperty("datestyle.day");
				break;
			}
			case 10: {
				ret = year + ResourceFactory.getProperty("datestyle.year")
						+ month
						+ ResourceFactory.getProperty("datestyle.month");
				break;
			}
			case 11: {
				if (Integer.parseInt(month) < 10)
					month = month.substring(1);
				ret = year + ResourceFactory.getProperty("datestyle.year")
						+ month
						+ ResourceFactory.getProperty("datestyle.month");
				break;
			}
			case 12: {
				year = year.substring(2);
				ret = year + ResourceFactory.getProperty("datestyle.year")
						+ month
						+ ResourceFactory.getProperty("datestyle.month") + day
						+ ResourceFactory.getProperty("datestyle.day");
				break;
			}
			case 13: {
				if (Integer.parseInt(month) < 10)
					month = month.substring(1);
				if (Integer.parseInt(day) < 10)
					day = day.substring(1);
				year = year.substring(2);
				ret = year + ResourceFactory.getProperty("datestyle.year")
						+ month
						+ ResourceFactory.getProperty("datestyle.month") + day
						+ ResourceFactory.getProperty("datestyle.day");
				break;
			}
			case 14: {
				if (Integer.parseInt(month) < 10)
					month = month.substring(1);
				year = year.substring(2);
				ret = year + ResourceFactory.getProperty("datestyle.year")
						+ month
						+ ResourceFactory.getProperty("datestyle.month");
				break;
			}
			case 15: {// 年限
				int cYear = Calendar.getInstance().get(Calendar.YEAR);// 当前年
				int cMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;// 当前月
				int cDay = Calendar.getInstance().get(Calendar.DATE);// 当前日
				ret = cYear - Integer.parseInt(year) + "";
				break;
			}
			case 16: {// 年份
				ret = year;
				break;
			}
			case 17: {// 月份
				if (month.length() > 2 && Integer.parseInt(month) < 10)
					month = month.substring(1);
				ret = month;
				break;
			}
			case 18: {// 日份
				if (day.length() > 2 && Integer.parseInt(day) < 10)
					day = day.substring(1);
				ret = day;
				break;
			}

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return ret;
	}

	/**
	 * 将数值型按指定的整数长度和小数长度显示
	 * 
	 * @param integerlength
	 *            整数长度
	 * @param ndeclen
	 *            小数长度
	 * @param number
	 *            数值
	 * @return
	 * @throws GeneralException 
	 */
	public String getNumberFormat(int integerlength, int ndeclen, String number) throws GeneralException {
		String ret = "";
		try {
			if (number == null || number.trim().length() <= 0)
				return ret;
			/** 为了不在数字前面补0，整数位只要1位 */
			String temp = "0";
			if (ndeclen > 0) {
				String temp2 = this.getFormat(ndeclen);
				temp = temp + "." + temp2;
			}
			if (temp.length() > 0) {
				DecimalFormat dcom = new DecimalFormat(temp);
				ret = dcom.format(Double.parseDouble(number));
			} else
				ret = number;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return ret;
	}

	/**
	 * get format String
	 * 
	 * @param len
	 * @return
	 * @throws GeneralException 
	 */
	public String getFormat(int len) throws GeneralException {
		String temp = "";
		try {
			for (int i = 0; i < len; i++) {
				temp += "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return temp;
	}


	/**
	 * 删除人员的方法(可多删)
	 * @throws GeneralException 
	 */
	public void deletePersonFromEmail_content(String a0100s, String id,
			String username,String salaryid) throws GeneralException {
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			String[] temp_arr = a0100s.split(",");
			for (int i = 0; i < temp_arr.length; i++) {
				if (temp_arr[i] == null || "".equals(temp_arr[i]))
					continue;
				String nbasea0100 = temp_arr[i].substring(0, temp_arr[i]
						.indexOf("~"));
				String send_time ="";
				if(temp_arr[i].split("~").length==3)
					send_time=temp_arr[i].split("~")[1];
				String count="";
				if(temp_arr[i].split("~").length==3)	
					count=temp_arr[i].split("~")[2];
				if (send_time == null || "".equals(send_time) || "".equals(count))
					continue;
				String sql = "delete from email_content where a0100 ='"
						+ nbasea0100.substring(3) + "' and id=" + id
						+ " and lower(pre)='" + nbasea0100.substring(0, 3).toLowerCase()+"'"; 
				sql+=" and module_type=34 and module_id="+salaryid; //薪资模块 + 某个薪资账套的邮件
				sql+=" and "+Sql_switcher.dateToChar("send_time", "YYYY-MM-DD")+"='"+send_time+"'";	
				sql+=" and a00z3="+count+"  and username='"+username+"'";
				
				dao.delete(sql, new ArrayList());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * 根据选中id和表名生成sql的方法
	 * @param selectedid 选中id
	 * @param tablename 表名
	 * @return
	 * @throws GeneralException
	 */
	private String getSql(String selectedid, String tablename) throws GeneralException {
		String sqlstr = "";

		try {
			StringBuffer s = new StringBuffer();
			StringBuffer sql = new StringBuffer("");
			String[] arr = selectedid.split(",");
			String column = "";
			if ("e".equalsIgnoreCase(tablename))
				column = "pre";
			if ("s".equalsIgnoreCase(tablename)
					|| "a".equalsIgnoreCase(tablename))
				column = "nbase";
			for (int i = 0; i < arr.length; i++) {
				if (arr[i] == null || "".equals(arr[i]))
					continue;
				String[] str = arr[i].split("~");
				sql.append(" or  (UPPER(" + tablename + "." + column + ")='"
						+ str[0].substring(0, 3).toUpperCase() + "' and " + tablename
						+ ".a0100='" + str[0].substring(3) + "')");
			}

			if (sql.toString().length() > 0) {
				sqlstr = sql.toString().substring(4);
				s.append("(");
				s.append(sqlstr);
				s.append(")");
				sqlstr = s.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return sqlstr;
	}

	/**
	 * 取得浏览邮件的内容
	 * 
	 * @param templateId 模板id
	 * @param a0100 //人员
	 * @param nbase 
	 * @return
	 * @throws GeneralException 
	 */
	public HashMap getBrowseEmailContent(String templateId, String a0100,
			String nbase, String tableName, String send_time,String salaryid,String count) throws GeneralException {
		HashMap map = new HashMap();
		try {
			StringBuffer sql = new StringBuffer();
			sql
					.append(" select s.a0101,address,subject,content from email_content e,");
			sql.append(tableName
					+ " s where e.a0100=s.a0100 and upper(e.pre)=upper(s.nbase) and e.id=");
			sql.append(templateId);
			sql.append(" and e.a0100='");
			sql.append(a0100);
			sql.append("' and lower(e.pre)='");
			sql.append(nbase.toLowerCase() + "'");
			sql.append(" and e.module_type=34 and e.module_id="+salaryid); //薪资模块 + 某个薪资账套的邮件
			sql.append(" and "+Sql_switcher.dateToChar("e.send_time", "YYYY-MM-DD")+"='"+send_time+"'");	
			sql.append(" and e.a00z3="+count+" and e.username='"+tableName.split("_salary_")[0]+"'");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql.toString());
			while (rs.next()) {
				map.put("address", rs.getString("address"));
				map.put("subject", rs.getString("subject"));
				map.put("content", rs.getString("content"));
				map.put("a0101", rs.getString("a0101"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}

	/**
	 * 取浏览邮件的附件列表
	 * 
	 * @param templateId
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getBrowseEmailAttach(String templateId) throws GeneralException {
		ArrayList list = new ArrayList();
		try {
			
			StringBuffer sql = new StringBuffer();
			sql.append("select filename from email_attach where id="
					+ templateId);
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql.toString());
			while (rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("filename", rs.getString("filename"));
				list.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;

	}
	/**
	 * 取得待发送邮件的人员
	 * 
	 * @param templateId
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getEmailContentList(String type, String selectA0100,
			String templateId, UserView userview, 
			String tableName, String privSql, String mobile_field,
			String e_m_type,String Send_ok,String condSql,String timeSql,String salaryid) throws GeneralException {
		ArrayList list = new ArrayList();
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			StringBuffer sql = new StringBuffer();
			//查询人员库
			StringBuffer sqlUsr = new StringBuffer();
			/** 查询所有的自助用户指标 start**/
			AttestationUtils utils = new AttestationUtils();
			LazyDynaBean fieldbean = utils.getUserNamePassField();
			String username_field = (String)fieldbean.get("name");

			StringBuffer strWhere=new StringBuffer(" (");
			ArrayList<String> templist=new ArrayList<String>();
			/** 查询所有的自助用户指标 end**/
			if ("1".equalsIgnoreCase(type)) {
				String[] temp = selectA0100.split(",");
				for (int i = 0; i < temp.length; i++) {
					if(temp[i].split("~").length!=3)
						continue;

					strWhere.append("(a0100=?");
					strWhere.append(" and upper(pre)=?");
					strWhere.append(" and a00z3=?");
					strWhere.append(" and ").append(Sql_switcher.dateToChar("send_time", "YYYY-MM-DD")).append("=? ) or");
					templist.add(temp[i].substring(3, temp[i].indexOf("~")));
					templist.add(temp[i].substring(0, 3));
					templist.add(temp[i].split("~")[2]);
					templist.add(temp[i].split("~")[1]);


					sql.append("select e.id,e.pre,e.subject,e.address,e.content,e.a0100,e.username,e.I9999, s.a0101,"+Sql_switcher.dateToChar("e.send_time", "YYYY-MM-DD")+" send_time,e.a00z3,C." + username_field + " usernameZiZhu");
					if ("1".equals(e_m_type))
						sql.append(",C." + mobile_field);
					sql.append(" from email_content e,(select distinct a0100,upper(nbase) nbase,a0101 from " + tableName + "  ) s");

					sql.append("," + temp[i].substring(0, 3) + "A01 C ");
					sql.append(" where e.id=" + templateId);
					sql.append(" and e.a0100='");
					sql.append(temp[i].substring(3, temp[i].indexOf("~")));
					sql.append("' and lower(e.pre)='");
					sql.append(temp[i].substring(0, 3).toLowerCase());
					sql.append("'  and e.module_type=34 and e.module_id="+salaryid); //薪资模块 + 某个薪资账套的邮件 
					sql.append(" and "+Sql_switcher.dateToChar("e.send_time", "YYYY-MM-DD")+"='"+ temp[i].split("~")[1]+"'");
					sql.append(" and e.a00z3="+temp[i].split("~")[2]+" and e.username='"+tableName.split("_salary_")[0]+"' ");//存在共享帐套的原因，这里取XX_salary_YY的XX为username，存的时候就这样存的 sunjian
					sql.append(" and e.a0100=s.a0100 and lower(e.pre)=lower(s.nbase)");
					sql.append(" and e.a0100=C.a0100");
					
					rs = dao.search(sql.toString());
					while (rs.next()) {
						LazyDynaBean bean = new LazyDynaBean();
						bean.set("subject", rs.getString("subject"));
						/** 邮件地址中如果有空格，将其去掉 */
						bean.set("toAddr", rs.getString("address") == null ? ""
								: rs.getString("address").replaceAll(" ", ""));
						bean.set("returnAddress",
								rs.getString("address") == null ? "" : rs
										.getString("address").replaceAll(" ",
												""));
						String bodyText=  rs.getString("content") == null ? "" : rs.getString("content");
						bodyText=bodyText.replaceAll("\r\n","<br>");
						bodyText=bodyText.replace("\n","<br>");
						bodyText=bodyText.replace("\r","<br>");
						bodyText=bodyText.replace("><br>",">");
						bodyText=bodyText.replace("> <br>",">");
						bodyText=bodyText.replace(">  <br>",">");
						if(bodyText.indexOf("<div")==-1&&bodyText.indexOf("<table")==-1) {
							bodyText = bodyText.replace(" ", "&nbsp;");
						}
						bean.set("module_type","34");
				 		bean.set("module_id",salaryid);
						bean.set("a00z3",rs.getString("a00z3"));
						bean.set("send_time",rs.getString("send_time")!=null?rs.getString("send_time"):"");
						bean.set("bodyText",bodyText);
						bean.set("a0100", rs.getString("a0100"));
						bean.set("pre", rs.getString("pre"));
						bean.set("id", rs.getString("id"));
						bean.set("username",
								rs.getString("username") == null ? "" : rs
										.getString("username"));
						bean.set("I9999", rs.getString("I9999") == null ? ""
								: rs.getString("I9999"));
						bean.set("a0101", rs.getString("a0101") == null ? ""
								: rs.getString("a0101"));
						bean.set("usernameZiZhu", rs.getString("usernameZiZhu"));
						if ("1".equals(e_m_type))
							bean.set(mobile_field.toLowerCase(), rs
									.getString(mobile_field) == null ? "" : rs
									.getString(mobile_field));
						list.add(bean);
					}
					sql.setLength(0);
				}
				//将所有待发送数据置为 发送中状态 send_ok=4
				strWhere.delete(strWhere.length()-2,strWhere.length());
				strWhere.append(")");
				sql.setLength(0);
				sql.append( "update email_content  set send_ok=4 where ").append(strWhere);
				sql.append(" and id=?");
				sql.append(" and module_type=34 and module_id=?"); //薪资模块 + 某个薪资账套的邮件
				sql.append(" and username=? ");
				templist.add(templateId);
				templist.add(salaryid);
				templist.add(tableName.split("_salary_")[0]);
				dao.update(sql.toString(),templist);

			} else {

				String salaryFilterSql=this.getSalaryTableFilterSql(tableName.split("_salary_")[1]);

				privSql=privSql==null?"":privSql;

				ArrayList listPre = new ArrayList();
				sql.append("select pre from email_content where module_type=34 and module_id="+salaryid+" group by pre ");
				rs = dao.search(sql.toString());
				while(rs.next()) {
					String pre = rs.getString("pre");
					listPre.add(pre);
				}

				for(int i = 0; i < listPre.size(); i++) {
					String tview = "";
					if ("1".equals(e_m_type)) {
						tview = this.getTableView(tableName, mobile_field);
					}
					sql.setLength(0);
					sql.append("select e.id,e.pre,e.subject,e.address,e.content,e.a0100,e.username,e.I9999,s.a0101 ,"+Sql_switcher.dateToChar("e.send_time", "YYYY-MM-DD")+" send_time,e.a00z3,u." + username_field + " usernameZiZhu");
					if (tview != null && tview.trim().length() > 0)
						sql.append(",T." + mobile_field);
					sql.append(" from email_content e,(select distinct a0100,upper(nbase) nbase,a0101 ").append(getPriField(salaryid)).append(" from ").append(tableName);

					if (!"".equals(privSql)||StringUtils.isNotBlank(salaryFilterSql)) {
						sql.append(" where 1=1 ").append(privSql).append(" ").append(salaryFilterSql);
					}
					sql.append( "  ) s ," );

                    if(condSql.length()>0){//添加搜索框中的查询条件
                        sql.append("(select * from "+ listPre.get(i).toString().toLowerCase() +"A01 where "+condSql+" ) u ");
                    }else
                        sql.append(listPre.get(i).toString().toLowerCase() + "A01 u");

					if (tview != null && tview.trim().length() > 0)
						sql.append(",(" + tview + ") T");
					sql.append(" where e.id=");
					sql.append(templateId);
					sql.append(" and e.module_type=34 and e.module_id="+salaryid); //薪资模块 + 某个薪资账套的邮件

					sql.append(" and e.a0100=u.a0100 and e.a0100=s.a0100 and lower(e.pre) = lower(s.nbase) ");
					if (tview != null && tview.trim().length() > 0) {
						sql.append(" and T.a0100=e.a0100 and lower(T.nbase)=lower(e.pre) ");
					}
					if(timeSql.length()>0){//添加时间条件
						sql.append(" and "+timeSql);
					}
					if(Send_ok.length()>0&&!"3".equals(Send_ok.trim())){//添加发送状态
						sql.append(" and Send_ok='"+Send_ok+"' ");
					}

					sql.append(" and lower(e.pre)='");
					sql.append(listPre.get(i).toString().toLowerCase());
					sql.append("'");
					rs = dao.search(sql.toString());
					while (rs.next()) {
						LazyDynaBean bean = new LazyDynaBean();
						bean.set("subject", rs.getString("subject"));
						bean.set("toAddr", rs.getString("address") == null ? ""
								: rs.getString("address"));
						bean.set("returnAddress",
								rs.getString("address") == null ? "" : rs
										.getString("address"));
						String bodyText=  rs.getString("content")==null?"":rs.getString("content");
						bodyText=bodyText.replaceAll("\r\n","<br>");
						bodyText=bodyText.replace("\n","<br>");
						bodyText=bodyText.replace("\r","<br>");
						bodyText=bodyText.replace("><br>",">");
						bodyText=bodyText.replace("> <br>",">");
						bodyText=bodyText.replace(">  <br>",">");
						bean.set("module_type","34");
						bean.set("module_id",salaryid);
						bean.set("a00z3",rs.getString("a00z3"));
						bean.set("send_time",rs.getString("send_time")!=null?rs.getString("send_time"):"");
						bean.set("bodyText",bodyText);
						bean.set("a0100", rs.getString("a0100"));
						bean.set("pre", rs.getString("pre"));
						bean.set("id", rs.getString("id"));
						bean.set("username", rs.getString("username"));
						bean.set("I9999", rs.getString("I9999"));
						bean.set("a0101", rs.getString("a0101") == null ? "" : rs
								.getString("a0101"));
						bean.set("usernameZiZhu", rs.getString("usernameZiZhu"));
						if (tview != null && tview.trim().length() > 0) {
							bean.set(mobile_field.toLowerCase(), rs
									.getString(mobile_field) == null ? "" : rs
									.getString(mobile_field));
						}
						list.add(bean);
					}
					//将所有待发送数据置为 发送中状态 send_ok=4
					sql.setLength(0);
					sql.append( "update email_content  set send_ok=4 where ");
					sql.append(" id=?");
					sql.append(" and module_type=34 and module_id=?"); //薪资模块 + 某个薪资账套的邮件
					sql.append(" and username=? ");
					sql.append(" and exists (select 1 from " + tableName+" where 1=1 ");
					if (!"".equals(privSql)||StringUtils.isNotBlank(salaryFilterSql)) {
						sql.append(privSql).append(" ").append(salaryFilterSql);
					}
					sql.append( "  and  email_content.a0100=" + tableName+".a0100 and lower(email_content.pre) = lower(" + tableName+".nbase)) " );
					if(condSql.length()>0)//添加搜索框中的查询条件
						sql.append(" and exists (select 1 from "+ listPre.get(i).toString().toLowerCase() +"A01 a where "+condSql+" and  a.a0100=email_content.a0100 ) ");
					if(timeSql.length()>0){//添加时间条件
						sql.append(" and "+timeSql);
					}
					if(Send_ok.length()>0&&!"3".equals(Send_ok.trim())){//添加发送状态
						sql.append(" and Send_ok=?");
					}
					sql.append(" and lower(email_content.pre)=?");
					templist.clear();
					templist.add(templateId);
					templist.add(salaryid);
					templist.add(tableName.split("_salary_")[0]);
					if(Send_ok.length()>0&&!"3".equals(Send_ok.trim())){//添加发送状态
						templist.add(Send_ok);
					}
					templist.add(listPre.get(i).toString().toLowerCase());
					dao.update(sql.toString(),templist);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}

	private String getTableView(String tableName, String mobile_field) throws GeneralException {
		String sql = "";
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search("select distinct upper(nbase) from " + tableName);
			StringBuffer buf = new StringBuffer("");
			while (rs.next()) {
				String pre = rs.getString(1);
				buf.append(" union All ");
				buf.append(" (select a0100,'" + pre + "' as nbase,"
						+ mobile_field + " from " + pre + "A01)");

			}
			if (buf.toString().length() > 0) {
				String str = buf.toString().substring(10);
				sql = "select * from (" + str + ") T ";
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return sql;
	}

	/**
	 * 将邮件模板的附件从数据库中取出，还原成文件，返回的是由文件名组成的列表
	 * 
	 * @param templateId
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getAttachFileName(String templateId) throws GeneralException {
		ArrayList list = new ArrayList();
		InputStream is = null;
		FileOutputStream fileOut = null;
		try {
			StringBuffer sql =new StringBuffer();
			sql.append("select filename,attach from email_attach where id="
					+ templateId);
			ContentDAO dao = new ContentDAO(this.conn);
			byte[] buf = new byte[1024];
			RowSet rs = null;
			rs = dao.search(sql.toString());
			while (rs.next()) {
				try {
					is = rs.getBinaryStream("attach");
					if (is == null) {
						continue;
					}
					String filename = System.getProperty("java.io.tmpdir")
							+ System.getProperty("file.separator")
							+ rs.getString("filename");
					File file = new File(filename);
					if (file.exists())
						if (file.delete())
							file.createNewFile();
						else {
						}
					if (!file.exists())
						file.createNewFile();
					fileOut = new FileOutputStream(filename);
					int length;
					while ((length = is.read(buf)) != -1) {
						fileOut.write(buf, 0, length);
						fileOut.flush();
					}
					list.add(filename);
				} finally {
					PubFunc.closeResource(fileOut);
					PubFunc.closeIoResource(is);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}

	/**
	 * 取得系统邮件服务器设置的发送邮件的地址
	 * 
	 * @return
	 * @throws GeneralException
	 */
	public String getFromAddr() throws GeneralException {
		String str = "";
		RecordVo stmp_vo = ConstantParamter.getConstantVo("SS_STMP_SERVER");
		if (stmp_vo == null)
			return "";
		String param = stmp_vo.getString("str_value");
		if (param == null || "".equals(param))
			return "";
		try {
			Document doc = PubFunc.generateDom(param);
			Element root = doc.getRootElement();
			Element stmp = root.getChild("stmp");
			str = stmp.getAttributeValue("from_addr");
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return str;
	}

	/**
	 * 发送信息
	 * @param type 类型
	 * @param emailContentList 通知列表
	 * @param templateId 模板id
	 * @param attachList 附件list
	 * @param from 邮件地址
	 * @return
	 * @throws Exception
	 */
	public HashMap sendMessage(int type, ArrayList emailContentList,
			String templateId, ArrayList attachList, String from,
			UserView userView, int flagtype, String mobile_field)
			throws Exception {
		HashMap map = new HashMap();
		int isSucess = 0;
		StringBuffer buf = new StringBuffer("");
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
			String currenttime = String.valueOf(calendar.get(Calendar.YEAR))
					+ "-" + String.valueOf(calendar.get(Calendar.MONTH) + 1)
					+ "-" + String.valueOf(calendar.get(Calendar.DATE)) + " "
					+ String.valueOf(calendar.get(Calendar.HOUR)) + ":"
					+ String.valueOf(calendar.get(Calendar.MINUTE)) + ":"
					+ String.valueOf(calendar.get(Calendar.SECOND));
			updateTableCloumns();
			ArrayList destlist = new ArrayList();
			ArrayList alist = new ArrayList();
			for (int i = 0; i < emailContentList.size(); i++) {
				int flag = 1;
				LazyDynaBean bean = (LazyDynaBean) emailContentList.get(i);
				if (bean.get(mobile_field.toLowerCase()) == null
						|| ""
								.equals((String) bean.get(mobile_field.toLowerCase()))) {
					if (buf != null && buf.length() > 0) {
						buf.append("\r\n");
					}
					buf.append("------------------------");
					buf.append((String) (bean.get("a0101") == null ? "" : bean
							.get("a0101")));
					buf.append("------------------------");
					buf.append("\r\n");
					buf.append("电话号码为空");
					flag = 2;
					this.configSend_ok(flag, templateId, (String) bean
							.get("a0100"), (String) bean.get("pre"),
							(String) bean.get("username"), (String) bean
							.get("module_type"), (String) bean
							.get("module_id"), (String) bean
							.get("send_time"), (String) bean
							.get("a00z3"));
					continue;
				}
				if (buf != null && buf.length() > 0) {
					buf.append("\r\n");
				}
				String bodyText = (String) bean.get("bodyText");
				LazyDynaBean dyvo = new LazyDynaBean();
				dyvo.set("sender", userView.getUserFullName());
				dyvo.set("receiver", (String) bean.get("a0101"));
				dyvo.set("phone_num", (String) bean.get(mobile_field
						.toLowerCase()));
				bodyText = bodyText.replaceAll(" ", "");
				bodyText = bodyText.replaceAll("\\r", "");
				bodyText = bodyText.replaceAll("\\n", "");
				bodyText = bodyText.replaceAll("\\r\\n", "");
				dyvo.set("msg", bodyText);
				destlist.add(dyvo);
				alist.add(bean);
			}
			try {
				if (destlist != null && destlist.size() > 0) {
					SmsBo smsbo = new SmsBo(this.conn);
					smsbo.batchSendMessage(destlist);
				}
			} catch (Exception e) {
				isSucess = 2;
				throw GeneralExceptionHandler
						.Handle(new Exception(
								ResourceFactory
										.getProperty("hire.employResume.sendmessagefaild")));
			}
			if (isSucess == 0 && alist.size() > 0) {
				for (int i = 0; i < alist.size(); i++) {
					int flag = 1;
					LazyDynaBean bean = (LazyDynaBean) alist.get(i);
					this.configSend_ok(flag, templateId, (String) bean
							.get("a0100"), (String) bean.get("pre"),
							(String) bean.get("username"), (String) bean
									.get("module_type"), (String) bean
									.get("module_id"), (String) bean
									.get("send_time"), (String) bean
									.get("a00z3"));
				}
			}
			if (buf != null && buf.toString().length() > 0) {
				buf.append("\r\n");
				buf.append("-----------------------------------------");
				buf.append("发送时间:" + currenttime);
				buf.append("-----------------------------------------");
			}
			String outName = "EmailError.txt";
			if (type == 0 && buf.toString().length() > 0) {
				FileOutputStream fileOut = null;
				try {
					fileOut = new FileOutputStream(System
							.getProperty("java.io.tmpdir")
							+ System.getProperty("file.separator") + outName);
					fileOut.write(buf.toString().getBytes());
				} finally {
					PubFunc.closeIoResource(fileOut);
				}
			} else {
				outName = "1";
			}
			map.put("flag", isSucess + "");
			map.put("file", SafeCode.encode(PubFunc.encrypt(outName)));
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}

	/**
	 * 设置邮件发送成功或失败的标志位
	 * 
	 * @param templateId
	 * @param a0100
	 * @param pre
	 * @param username
	 * @throws GeneralException 
	 */
	private void configSend_ok(int flag, String templateId, String a0100,
			String pre, String username, String module_type,String module_id,String send_time,String a00z3) throws GeneralException {
		try {
			updateTableCloumns();
			StringBuffer buf = new StringBuffer();
			buf.append("update email_content set send_ok=");
			buf.append(flag);
			buf.append(" where id=");
			buf.append(templateId);
			buf.append(" and a0100='");
			buf.append(a0100 + "' and lower(pre)='");
			buf.append(pre.toLowerCase()+"' ");
			buf.append(" and module_type="+module_type);
			buf.append(" and module_id="+module_id);
			buf.append(" and "+Sql_switcher.dateToChar("send_time", "YYYY-MM-DD")+"='"+send_time+"'");		
			buf.append(" and a00z3="+a00z3+" and username='"+username+"'");
			ContentDAO dao = new ContentDAO(this.conn);
			dao.update(buf.toString());

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * 发送邮件
	 * 
	 * @param emailContentList
	 *            对应选中模板的人员列表
	 * @param templateId
	 *            选中的模板
	 * @param attachList
	 *            模板的附件列表
	 * @param from
	 *            发送地址
	 * @throws Exception
	 * @throws GeneralException
	 */
	public HashMap sendEmail(int type, ArrayList emailContentList,
			String templateId, ArrayList attachList, String from,
			UserView userView, int flagtype) throws Exception {

		/*邮件bean添加附件信息 guodd 2016-08-01*/
		for (int i = 0; i < emailContentList.size(); i++) {
			LazyDynaBean bean = (LazyDynaBean) emailContentList.get(i);
			bean.set("attachList", attachList);
			bean.set("templateId", templateId);//添加模板信息 zhanghua 2016-08-06
		}
		
		HashMap map = new HashMap();
		StringBuffer buf = new StringBuffer("");
		try {
			// send_no=0; //邮件末发
			// send_ok=1; //邮件已发成功
			// send_err=2; //邮件发送失败
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
			String currenttime = String.valueOf(calendar.get(Calendar.YEAR))
					+ "-" + String.valueOf(calendar.get(Calendar.MONTH) + 1)
					+ "-" + String.valueOf(calendar.get(Calendar.DATE)) + " "
					+ String.valueOf(calendar.get(Calendar.HOUR)) + ":"
					+ String.valueOf(calendar.get(Calendar.MINUTE)) + ":"
					+ String.valueOf(calendar.get(Calendar.SECOND));
			updateTableCloumns();
			// EMailBo bo = null;
			AsyncEmailBo bo = null;
			// bo = new EMailBo(this.conn, true);
			SendMsgIsSuccess sendMsgIsSuccess=new SendMsgIsSuccess();
			bo = new AsyncEmailBo(this.conn, this.userview,sendMsgIsSuccess);
			int flag=emailContentList.size();
			bo.send(emailContentList);
		
//			for (int i = 0; i < emailContentList.size(); i++) {
//				flag = 1;
//				LazyDynaBean bean = (LazyDynaBean) emailContentList.get(i);
//				if (bean.get("toAddr") == null
//						|| ((String) bean.get("toAddr")).equals("")
//						|| !isMail((String) bean.get("toAddr"))) {
//					flag = 2;
//					this.configSend_ok(flag, templateId, (String) bean
//							.get("a0100"), (String) bean.get("pre"), "",
//							(String) bean.get("I9999"));
//					continue;
//				}
//				if (buf != null && buf.length() > 0) {
//					buf.append("\r\n");
//				}
//				 this.configSend_ok(flag, templateId, (String) bean
//				 .get("a0100"), (String) bean.get("pre"),
//				 (String) bean.get("username"), (String) bean
//				 .get("I9999"));
//			}
			//map.put("ListCount", emailContentList.size());
			map.put("flag", flag + "");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;

	}

	/**
	 * 根据业务用户名取得关联的自助用户名
	 * 
	 * @param name
	 * @return
	 * @throws GeneralException 
	 */
	public String getZizhuUsername(String pre,String name) throws GeneralException {
		StringBuffer str = new StringBuffer();
		try {
			ContentDAO dao = new ContentDAO(conn);
			/** 查询所有的自助用户指标 start**/
			AttestationUtils utils = new AttestationUtils();
			LazyDynaBean fieldbean = utils.getUserNamePassField();
			String username_field = (String)fieldbean.get("name");
			/** 查询所有的自助用户指标 end**/
			String sql = "select " + username_field + " username from "+ pre +"A01 where A0100 = '"
					+ name + "'";
			RowSet rs = dao.search(sql);
			if (rs.next()) {
				str.append(rs.getString("UserName"));
			}
			if (str.length() == 0) {
				str.append(name);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return str.toString();
	}

	/**
	 * 验证邮件地址是否合法
	 * 
	 * @param email
	 * @return
	 */
	private boolean isMail(String email) {
		String emailPattern = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		return email.matches(emailPattern);
	}
	/**
	 * 取系统的电话指标
	 * @return
	 * @throws GeneralException 
	 */
	public String getMobileField() throws GeneralException
	{
		try
		{
			  RecordVo vo=ConstantParamter.getConstantVo("SS_MOBILE_PHONE");
		        if(vo==null)
		        	return "";
		        String field_name=vo.getString("str_value");
		        if(field_name==null|| "".equals(field_name))
		        	return "";
		        FieldItem item=DataDictionary.getFieldItem(field_name);
		        if(item==null)
		        	return "";
		        /**分析是否构库*/
		        if("0".equals(item.getUseflag()))
		        	return "";
		        return field_name; 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 获取发送通知的时候如果设置了归属单位或者部门指标，以及设置了权限最后pri的指标
	 * @return
	 */
	private String getPriField(String salaryid) {
		StringBuffer whl=new StringBuffer(""); 
		SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.conn,Integer.parseInt(salaryid));
		String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid"); //归属单位
		orgid = StringUtils.isBlank(orgid)?"b0110":orgid;
		whl.append("," + orgid);
		String deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid"); //归属部门
		deptid = StringUtils.isBlank(deptid)?"e0122":deptid;
		whl.append("," + deptid);
		
		return whl.toString();
	}

	/**
	 * 获取邮件是否发送完毕
	 * @param type 1 发送选中，0 全部发送
	 * @param selectA0100 所选行
	 * @param templateId 邮件模板号
	 * @param tableName 薪资临时表名
	 * @param privSql 权限sql
	 * @param condSql 过滤sql
	 * @param timeSql 时间过滤sql
	 * @param salaryid
	 * @author ZhangHua
	 * @date 14:36 2018/2/11
	 * @return true 发送完毕 false 没有完毕
	 * @throws GeneralException
	 */
	public boolean getSendStatus(String type, String selectA0100,
								 String templateId, String tableName, String privSql,String condSql,
								 String timeSql,String salaryid) throws GeneralException {
		RowSet rs=null;
		boolean isOk=false;
	    try{
	        ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer();
			int num=0;
			if ("1".equalsIgnoreCase(type)) {//发送选中
				String[] temp = selectA0100.split(",");
				StringBuffer strWhere=new StringBuffer(" (");
				ArrayList<String> list=new ArrayList<String>();
				for(String str :temp){
					if(str.split("~").length!=3)
						continue;
					strWhere.append("(e.a0100=?");
					strWhere.append(" and upper(pre)=?");
					strWhere.append(" and a00z3=?");
					strWhere.append(" and ").append(Sql_switcher.dateToChar("e.send_time", "YYYY-MM-DD")).append("=? ) or");
					list.add(str.substring(3, str.indexOf("~")));
					list.add(str.substring(0, 3));
					list.add(str.split("~")[2]);
					list.add(str.split("~")[1]);
				}
				strWhere.delete(strWhere.length()-2,strWhere.length());
				strWhere.append(")");
				sql.append(" select count(1) as num from  email_content e inner join (select  a0100,nbase from " + tableName + " group by nbase,a0100  ) s on e.a0100=s.a0100 and lower(e.pre)=lower(s.nbase) ");
				sql.append(" where ").append(strWhere.toString());
				sql.append("  and e.id=?");
				sql.append("  and e.module_type=34 and e.module_id=?"); //薪资模块 + 某个薪资账套的邮件
				sql.append(" and e.username=? ");
				sql.append(" and ").append(Sql_switcher.isnull("send_ok", "0")).append("=4");
				list.add(templateId);
				list.add(salaryid);
				list.add(tableName.split("_salary_")[0]);
				rs=dao.search(sql.toString(),list);
				if(rs.next()){
					num+=rs.getInt("num");
				}
				if(num>0)//查询所有处于发送中的数据，如果没有了 证明发送完成
					isOk= false;
				else
					isOk= true;
			}else{//发送全部
				ArrayList<String> listPre = new ArrayList<String>();
				sql.append("select pre from email_content where module_type=34 and module_id="+salaryid+" group by pre ");
				rs = dao.search(sql.toString());
				while(rs.next()) {
					String pre = rs.getString("pre");
					listPre.add(pre);
				}

				for(String pre:listPre) {
					sql.setLength(0);
					sql.append("select count(1) as num  from email_content e inner join (select distinct a0100,nbase,a0101 " + getPriField(salaryid) + " from " + tableName);
					if (privSql != null && !"".equals(privSql))
						sql.append(" where 1=1 ").append(privSql);
					sql.append( "  ) s on e.a0100=s.a0100 and lower(e.pre) = lower(s.nbase) " );
					if(condSql.length()>0)//添加搜索框中的查询条件
						sql.append(" inner join (select * from ").append(pre).append("A01 where ").append(condSql).append(" ) u ");
					else
						sql.append(" inner join ").append(pre).append("A01 u ");
					sql.append(" on e.a0100=u.a0100 ");
					sql.append(" where e.id=");
					sql.append(templateId);
					sql.append(" AND e.send_ok=4 and e.module_type=34 and e.module_id="+salaryid); //薪资模块 + 某个薪资账套的邮件
					if(timeSql.length()>0)//添加时间条件
						sql.append(" and "+timeSql);
					sql.append(" and lower(e.pre)='");
					sql.append(pre.toLowerCase());
					sql.append("'");
					rs = dao.search(sql.toString());
					if(rs.next())
						num+=rs.getInt("num");
					if(num>0)//查询所有处于发送中的数据，如果没有了 证明发送完成
						isOk= false;
					else
						isOk= true;
				}
			}

	    }catch (Exception e){
	        e.printStackTrace();
	        throw GeneralExceptionHandler.Handle(e);
	    } finally
	    {
	        PubFunc.closeDbObj(rs);
	    }
	    return isOk;
	}


	/**
	 * 获取薪资发放页面过滤sql
	 * @param salaryid
	 * @author ZhangHua
	 * @date 13:22 2018/8/16
	 * @return
	 */
	private String getSalaryTableFilterSql(String salaryid){
		String salaryCondSql="";
		TableDataConfigCache salaryTableCache = (TableDataConfigCache)this.userview.getHm().get( "salary_"+salaryid);
		if(salaryTableCache!=null){
			salaryCondSql+= StringUtils.isNotBlank(salaryTableCache.getQuerySql())?salaryTableCache.getQuerySql():"";
			salaryCondSql+= StringUtils.isNotBlank(salaryTableCache.getFilterSql())?salaryTableCache.getFilterSql():"";
		}

		return salaryCondSql;
	}
}