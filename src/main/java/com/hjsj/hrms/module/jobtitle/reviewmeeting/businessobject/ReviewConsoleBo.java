package com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.TemplatePendingTaskBo;
import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.module.jobtitle.configfile.businessobject.JobtitleConfigBo;
import com.hjsj.hrms.module.jobtitle.configfile.transaction.DomXml;
import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.StartReviewBo;
import com.hjsj.hrms.module.template.templatetoolbar.printout.businessobject.OutPutModelBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
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
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 资格评审_职称评审_上会材料
 * 
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 */
public class ReviewConsoleBo {

	// 基本属性
	private Connection conn = null;
	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	public UserView getUserview() {
		return userview;
	}
	public void setUserview(UserView userview) {
		this.userview = userview;
	}
	private UserView userview;
	/** 当前评审环节是否已经结束，如结束则只允许查看 */
	private boolean isFinished;
	
	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}
	/**
	 * 构造函数
	 * 
	 * @param conn
	 * @param userview
	 */
	public ReviewConsoleBo(Connection conn, UserView userview) {
		this.setConn(conn);
		this.setUserview(userview);
	}
	public ReviewConsoleBo(Connection conn) {
		this.setConn(conn);
	}


	
	/**
	 * 生成菜单的bean
	 * @param text    文本内容
	 * @param handler 触发事件
	 * @param icon    图标
	 * @param list    按钮集合
	 * @return
	 */
	public LazyDynaBean getMenuBean(String text,String handler,String icon,ArrayList list){
		LazyDynaBean bean = new LazyDynaBean();
		try{
			if(text!=null&&text.length()>0)
				bean.set("text", text);
			if(icon!=null&&icon.length()>0)
				bean.set("icon", icon);
			if(handler!=null&&handler.length()>0){
				if(list!=null&&list.size()>0){
					bean.set("menu", list);
				}else{
					bean.set("handler", handler);
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
	}
	
	/**
	 * 生成功能导航菜单的json串
	 * @param name 菜单名
	 * @param id   菜单id
	 * @param list 菜单功能集合
	 * @return
	 */
	private String getMenuStr(String name,String id,ArrayList list,String otherStyle){
		StringBuffer str = new StringBuffer();
		try{
			if(name.length()>0){
				str.append("<jsfn>{xtype:'button',"+otherStyle+"text:'"+name+"'");
			}
			if(StringUtils.isNotBlank(id)){
				str.append(",id:'");
				str.append(id);
				str.append("'");
			}
			str.append(",menu:{items:[");
			for(int i=0;i<list.size();i++){
				LazyDynaBean bean = (LazyDynaBean) list.get(i);
				if(i!=0)
					str.append(",");
				str.append("{");
				if(bean.get("xtype")!=null&&bean.get("xtype").toString().length()>0)
					str.append("xtype:'"+bean.get("xtype")+"'");
				if(bean.get("text")!=null&&bean.get("text").toString().length()>0)
					str.append("text:'"+bean.get("text")+"'");
				if(bean.get("handler")!=null&&bean.get("handler").toString().length()>0){
					if(bean.get("xtype")!=null&& "datepicker".equalsIgnoreCase(bean.get("xtype").toString())){//时间控件单独处理一下 方法GzGlobal.aaa(picker, date)这样写
						str.append(",handler:function(picker, date){"+bean.get("handler")+";}");
					}else{
						str.append(",handler:function(){"+bean.get("handler")+";}");
					}				
				}
				String menuId = (String)bean.get("id");
				
				if(menuId!=null&&menuId.length()>0)//人事异动-手工选择按钮需要id（gaohy）
					str.append(",id:'"+menuId+"'");
				else
					menuId = "";
				if(bean.get("icon")!=null&&bean.get("icon").toString().length()>0)
					str.append(",icon:'"+bean.get("icon")+"'");
				if(bean.get("value")!=null&&bean.get("value").toString().length()>0)
					str.append(",value:"+bean.get("value")+"");
				ArrayList menulist = (ArrayList)bean.get("menu");
				if(menulist!=null&&menulist.size()>0){
					str.append(getMenuStr("",menuId, menulist,""));
				}
				str.append("}");
			}
			str.append("]}");
			if(name.length()>0){				
				str.append("}</jsfn>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str.toString();
	}
	
	/**
	 * 生成功能导航菜单的json串
	 * @param name 菜单名
	 * @param id   菜单id
	 * @param list 菜单功能集合
	 * @return
	 */
	private String getButtonStr(String name,String id,String otherStyle,HashMap map){
		StringBuffer str = new StringBuffer();
		try{
			str.append("<jsfn>{xtype:'button',"+otherStyle+"text:'"+name+"'");
			if(StringUtils.isNotBlank(id)){
				str.append(",id:'");
				str.append(id);
				str.append("'");
			}
			str.append(",handler:function(){"+map.get("handler")+";}}</jsfn>");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str.toString();
	}
	
	/**
	 * 列头ColumnsInfo对象初始化
	 * 
	 * @param columnId
	 *            id
	 * @param columnDesc
	 *            名称
	 * @param columnDesc
	 *            显示列宽
	 * @return
	 */
	private ColumnsInfo getColumnsInfo(String columnId, String columnDesc,
			int columnWidth, String codesetId, String columnType,
			int columnLength, int decimalWidth) {

		ColumnsInfo columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId(columnId);
		columnsInfo.setColumnDesc(columnDesc);
		columnsInfo.setColumnWidth(columnWidth);// 显示列宽
		columnsInfo.setCodesetId(codesetId);// 指标集
		columnsInfo.setColumnType(columnType);// 类型N|M|A|D
		columnsInfo.setColumnLength(columnLength);// 显示长度
		columnsInfo.setDecimalWidth(decimalWidth);// 小数位
		columnsInfo.setAllowBlank(true);// 编辑时是否可以为空
		columnsInfo.setReadOnly(true);// 是否只读
		columnsInfo.setLocked(false);// 是否锁列
		columnsInfo.setEditableValidFunc("reviewfile_me.checkCell");

		return columnsInfo;
	}

	
	/**
	 * 获取评审会议筛选数据源
	 * @param sql:当前页面的查询语句
	 * @return list 会议数据源
	 * @throws GeneralException
	 */
	public ArrayList<HashMap> getMeetingList(String sql) throws GeneralException {
		ArrayList<HashMap> list = new ArrayList<HashMap>();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			StringBuilder sqlStr = new StringBuilder(sql);
			sqlStr.append(" order by w03.W0301 desc ");
			rs = dao.search(sqlStr.toString());
			
			String str = ",";
			while(rs.next()){
				HashMap map = new HashMap();
				String w0301 = rs.getString("W0301_safe");
				if(str.indexOf(","+w0301+",") > -1){
					continue ;
				}
				map.put("w0301", PubFunc.encrypt(w0301));
				map.put("w0303", rs.getString("meetingname"));
				map.put("w0323", rs.getInt("w0323"));
				list.add(map);
				str += (w0301+",");
			}
			
			//list  = new ArrayList<HashMap>(new HashSet<HashMap>(list));//去重    //不去重了，set无序，orderby就没有效果了。 chent 20161025
			
		} catch(SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return list;
	}

    /**
     * 通过单位或部门名称模糊查询
     * @param codeDesc
     * @return
     * @throws GeneralException
     */
    public List<String> getCodeByLikeDesc(String codeDesc) throws GeneralException{
    	List<String> itemidList = new ArrayList<String>();
    	//组织机构类型的代码应该查organization表 
    	String	sql="select codeitemid from organization where codeitemdesc like '%"+codeDesc+"%' and codesetid in ('UN','UM')";
    	RowSet rs = null;
    	try{
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs=dao.search(sql);
    		while(rs.next()){
    			String codeitemid=rs.getString("codeitemid");
    			itemidList.add(codeitemid);
    		}
    		return itemidList;
    	}catch (Exception e) {
    		throw GeneralExceptionHandler.Handle(e);
    	}finally{
    		PubFunc.closeDbObj(rs);
    	}
    }
	/**
	 * 通过会议id查询b0110(组织机构id)
	 * @param w0301
	 * @return b0110
	 */
	public String getb0110Byw0301(String w0301){
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String sql = "select b0110 from W03 where W0301=?";
			List<String> values = new ArrayList<String>();
			values.add(w0301);
			rs = dao.search(sql, values);
			if(rs.next()){
				String b0110 = rs.getString("b0110");
				return b0110;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			PubFunc.closeDbObj(rs);
		}
	}
	/**
	 * 获取只有第一个会议的sql
	 * @param initSql
	 * @return
	 */
	public String getFirstMeetingSql(String initSql){
		StringBuilder sql = new StringBuilder(initSql);
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			ArrayList<HashMap> list = getMeetingList(initSql);
			if(list.size() > 0){
				String firstW0301 =  PubFunc.decrypt((String)list.get(0).get("w0301"));
				sql.append(" and w03.w0301='"+firstW0301+"'");
			}
			
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return sql.toString();
	}
	
	/**
	 * 获取表格控件列头
	 * @param w0301
	 * @param review_links 1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段 
	 * @param entryType：1：创建修改评审会议条件，2：上会界面
	 * @param evaluationType：1:投票  2：评分
	 * @param userType：1：随机账号，2：非随机，选人的
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList getColumnListForDiff(String w0301, String review_links,String entryType,String evaluationType,String userType, int screenWidth)throws GeneralException {
		ArrayList list = new ArrayList();

		try {
			ColumnsInfo columnsInfo = new ColumnsInfo();
			boolean isHaveOptionColumn = isHaveOptionColumn(w0301,review_links);
			
			if(!isHaveOptionColumn) {//如果没有操作这一列，对应的其他列增加长度
				screenWidth = screenWidth + 100;
			}
			if("2".equals(evaluationType)) {//如果是评分，因为没有
				screenWidth = screenWidth + 100;
			}
			int applicantWidth = 0;//申报人宽度
			if(screenWidth < 1280) {//1024做特殊处理
				if("2".equals(review_links)) {
					if("1".equals(userType)) {//1：随机账号申报人列长点，2：非随机，选人的
						applicantWidth = (int) (screenWidth * 0.42);
					}else {
						applicantWidth = (int) (screenWidth * 0.32);
					}
				}else if("3".equals(review_links)){
					applicantWidth = (int) (screenWidth * 0.50);
				}else {
					applicantWidth = (int) (screenWidth * 0.45);
				}
			}else {
				if("2".equals(review_links)) {
					if("1".equals(userType)) {//1：随机账号，2：非随机，选人的
						applicantWidth = (int) (screenWidth * 0.45); 
					}else {
						applicantWidth = (int) (screenWidth * 0.40); 
					}
				}else if("3".equals(review_links)){
					applicantWidth = (int) (screenWidth * 0.58);
				}else {
					applicantWidth = (int) (screenWidth * 0.52);
				}
			}
			// 分组名
			columnsInfo = this.getColumnsInfo("name", ResourceFactory.getProperty("zc_new.zc_reviewcConsole.groupName"), (int) (screenWidth * 0.10), "0", "A", 100, 0);
			columnsInfo.setTextAlign("center");
			columnsInfo.setEditableValidFunc("jobtitle_reviewconsole.checkCell");
			columnsInfo.setAllowBlank(false);
			columnsInfo.setColumnLength(100);
			list.add(columnsInfo);
			//同行没有达标名额
			if("1".equals(evaluationType) && !"3".equals(review_links)) {//投票显示多少人可投赞成票
				String showItem = "";
				if("1".equals(review_links)) {//评委会
					showItem = "W0553";
					
				} else if("2".equals(review_links)) {//学科组
					showItem = "W0547";
					
				} else if("3".equals(review_links)) {//外部专家
					showItem = "W0531";
				} else if("4".equals(review_links)) {//学院任聘组
					showItem = "W0567";
				}
				FieldItem  agree  = DataDictionary.getFieldItem(showItem, 1);
				String itemdesc = agree.getItemdesc().replace("人数", "");
				
				//"名额"
				columnsInfo = this.getColumnsInfo("c_number", itemdesc+ResourceFactory.getProperty("zc_new.zc_reviewcConsole.personCount"), (int) (screenWidth * 0.06), "0", "N", 100, 0);
				columnsInfo.setTextAlign("center");
				columnsInfo.setEditableValidFunc("jobtitle_reviewconsole.checkCell");
				columnsInfo.setValidFunc("jobtitle_reviewconsole.validfuncOther");
				list.add(columnsInfo);
			}
			//"申报人"
			columnsInfo = this.getColumnsInfo("p_person", ResourceFactory.getProperty("zc_new.zc_reviewcConsole.applicant"), applicantWidth, "0", "A", 100, 0);
			columnsInfo.setRendererFunc("jobtitle_reviewconsole.renderperson");
			columnsInfo.setEditableValidFunc("false");
			list.add(columnsInfo);
			
			if("2".equals(review_links)) {//
				
				if("1".equals(userType)) {
					//"评审人"
					columnsInfo = this.getColumnsInfo("expertnum", ResourceFactory.getProperty("hire.remark.person"), (int) (screenWidth * 0.08), "0", "N", 100, 0);
					//columnsInfo.setRendererFunc("jobtitle_reviewconsole.reviewRandomPerson");
					columnsInfo.setTextAlign("center");
					columnsInfo.setEditableValidFunc("jobtitle_reviewconsole.checkCell");
					columnsInfo.setValidFunc("jobtitle_reviewconsole.validfunc");
				}else { 
					columnsInfo = this.getColumnsInfo("review_person", ResourceFactory.getProperty("hire.remark.person"), (int) (screenWidth * 0.18), "0", "A", 100, 0);
					columnsInfo.setRendererFunc("jobtitle_reviewconsole.reviewPerson");
					columnsInfo.setEditableValidFunc("false");
				}
				list.add(columnsInfo);
				
			}
			
			if(!"1".equals(entryType)) {
				// 进度
				columnsInfo = this.getColumnsInfo("progress", ResourceFactory.getProperty("train.resource.mylessons.courseprogress"), (int) (screenWidth * 0.05), "0", "N", 100, 0);
				columnsInfo.setRendererFunc("jobtitle_reviewconsole.progress");
				columnsInfo.setTextAlign("center");
				columnsInfo.setEditableValidFunc("false");
				list.add(columnsInfo);
				
				// 状态
				columnsInfo = getColumnsInfo("approval_state", ResourceFactory.getProperty("zc_new.zc_reviewfile.state"), (int) (screenWidth * 0.06), "0", "A", 100, 0);
				columnsInfo.setRendererFunc("jobtitle_reviewconsole.approval_state");
				columnsInfo.setTextAlign("center");
				columnsInfo.setEditableValidFunc("false");
				list.add(columnsInfo);
			}
			
			if(isHaveOptionColumn) {
				// 操作
				columnsInfo = this.getColumnsInfo("operation", ResourceFactory.getProperty("column.operation"), (int) (screenWidth * 0.09), "0", "A", 100, 0);
				columnsInfo.setRendererFunc("jobtitle_reviewconsole.renderoperation");
				columnsInfo.setTextAlign("center");
				columnsInfo.setEditableValidFunc("false");
				list.add(columnsInfo);
			}
			
			/** 隐藏 */
			columnsInfo = new ColumnsInfo();
			columnsInfo.setColumnId("categories_id");
			columnsInfo.setEncrypted(true);
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			list.add(columnsInfo);

			columnsInfo = new ColumnsInfo();
			columnsInfo.setColumnId("expertnum");
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			list.add(columnsInfo);

			columnsInfo = new ColumnsInfo();
			columnsInfo.setColumnId("submitnum");
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			list.add(columnsInfo);
			
			columnsInfo = new ColumnsInfo();
			columnsInfo.setColumnId("seq");
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			list.add(columnsInfo);
		} catch(Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	
	/**
	 * 如果是已经归档了不应该有操作列
	 * @return
	 */
	private boolean isHaveOptionColumn(String w0301, String review_links) {
		boolean isHaveOptionColumn = true;
		ArrayList<String> list = new ArrayList<String>();
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.getConn());
			String sql = "select count(*) as count from zc_personnel_categories where w0301=? and review_links=? and approval_state=?";
			list.add(w0301);
			list.add(review_links);
			list.add("4");
			rs = dao.search(sql,list);
			if(rs.next()) {
				int count = rs.getInt("count");
				if(count > 0) {
					isHaveOptionColumn = false;
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return isHaveOptionColumn;
	}
	
	/**
	 * 差额投票-按钮
	 * @param review_links 1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段 
	 * @param entryType：1：创建修改评审会议条件，2：上会界面
	 * @param evaluationType：1:投票  2：评分
	 * @param userType：1：随机账号，2：非随机，选人的
	 * @return
	 */
	public ArrayList<Object> getButtonListForDiff(String w0301,String review_links,String entryType,String evaluationType,String userType) {

		ArrayList<Object> buttonList = new ArrayList<Object>();
		try {
			//创建修改评审会议是不显示功能导航按钮
			if("2".equals(entryType)) {
				// 功能导航
				ArrayList<Object> menuList = new ArrayList<Object>();
				ArrayList list = new ArrayList();
				if("1".equals(evaluationType)) {
					//材料公示
					LazyDynaBean bean = null;
					//同行和专业组都不应该显示材料公示
					if(!"3".equals(review_links) && !"2".equals(review_links) && !this.isFinished && this.userview.hasTheFunction("38005051501")) {
						bean = getMenuBean(ResourceFactory.getProperty("zc_new.zc_reviewcConsole.publicity"),"jobtitle_reviewconsole.notice()", "", list);
						menuList.add(bean);
					}
					ArrayList newList=new ArrayList();
					if(!this.isFinished) {
						if("3".equals(review_links) && this.userview.hasTheFunction("38005051502")){
							bean = getMenuBean(ResourceFactory.getProperty("zc_new.zc_reviewfile.voteAccountPwd"),"jobtitle_reviewconsole.showExportExcelColumns(2)", "", newList);//导出账号、密码
							menuList.add(bean);
						}else{
							JobtitleConfigBo jobtitleConfigBo = new JobtitleConfigBo(this.conn,this.userview);
							boolean support_checking = jobtitleConfigBo.getParamConfig("support_checking");
							//支持审核时显示 审核账号列 
							if(support_checking){
								newList.add(getMenuBean(ResourceFactory.getProperty("zc_new.zc_reviewfile.verifyAccountPwd"),"jobtitle_reviewconsole.showExportExcelColumns(1)", "", list));//导出账号、密码
							}
							newList.add(getMenuBean(ResourceFactory.getProperty("zc_new.zc_reviewfile.voteAccountPwd"),"jobtitle_reviewconsole.showExportExcelColumns(2)", "", list));//导出账号、密码
							//导出账号密码
							if(this.userview.hasTheFunction("38005051502")) {
								bean = getMenuBean(ResourceFactory.getProperty("zc_new.zc_reviewcConsole.exportPassword"),"jobtitle_reviewconsole.showExportExcelColumns(1)", "", newList);//导出账号、密码
								menuList.add(bean);
							}
						}
						
					}
					if(this.userview.hasTheFunction("38005051503")) {
						bean = getMenuBean(ResourceFactory.getProperty("label.commend.analyse"), "jobtitle_reviewconsole.scoreCount()", "", list);//"票数统计"
						menuList.add(bean);
					}
					if(!this.isFinished && this.userview.hasTheFunction("38005051504")) {
						bean = getMenuBean(ResourceFactory.getProperty("zc_new.zc_reviewcConsole.archiveData"), "jobtitle_reviewconsole.countResultsArchiving()", "", list);//"归档投票结果数据"
						menuList.add(bean);
					}
					if(this.userview.hasTheFunction("380050620")||this.userview.hasTheFunction("38005051506")) {//用户在使用时可能并不会建同行评议环节，所以为提供功能的灵活性 在各个环节都提供此项功能
						bean = getMenuBean(ResourceFactory.getProperty("zc_new.zc_reviewcConsole.masterpiece"), "jobtitle_reviewconsole.scoreCount(1)", "", list);//"导出代表作"
						menuList.add(bean);
					}
				}else if("2".equals(evaluationType)) {
					/*LazyDynaBean bean = getMenuBean(ResourceFactory.getProperty("zc_new.zc_reviewcConsole.startScore"),"jobtitle_reviewconsole.setPersonNum()", "", list);
					menuList.add(bean);*/
					LazyDynaBean bean = null;
					if(!"2".equals(review_links) && !this.isFinished && this.userview.hasTheFunction("38005051501")) {
						bean = getMenuBean(ResourceFactory.getProperty("zc_new.zc_reviewcConsole.publicity"),"jobtitle_reviewconsole.notice()", "", list);
						menuList.add(bean);
					}
					ArrayList newList=new ArrayList();
					if(!this.isFinished && this.userview.hasTheFunction("38005051502")) {
						bean = getMenuBean(ResourceFactory.getProperty("zc_new.zc_reviewcConsole.exportPassword"),"jobtitle_reviewconsole.showExportExcelColumns(3)", "", newList);//导出账号、密码
						menuList.add(bean);
					}
					if(this.userview.hasTheFunction("38005051503")) {
						bean = getMenuBean(ResourceFactory.getProperty("zc_new.zc_reviewcConsole.countScore"), "jobtitle_reviewconsole.scoreCount()", "", list);//"分数统计"
						menuList.add(bean);
					}
					if(!this.isFinished && this.userview.hasTheFunction("38005051504")) {
						bean = getMenuBean(ResourceFactory.getProperty("zc_new.zc_reviewcConsole.archiveScore"), "jobtitle_reviewconsole.countResultsArchiving()", "", list);//"归档评分结果数据"
						menuList.add(bean);
					}
				}
				if(menuList.size() > 0) {
					String menu = getMenuStr(ResourceFactory.getProperty("gz_new.gz_accounting.FunctionNavigation"),"reviewdiffmenu_"+review_links,menuList,"");	//"功能导航"		
					buttonList.add(menu);
				}
			}
			// 新建分组 
			if(!this.isFinished && this.userview.hasTheFunction("38005051505")) {
				HashMap<String,String> map = new HashMap<String,String>();
				map.put("handler", "jobtitle_reviewconsole.createCategorie()");
				String button = getButtonStr(ResourceFactory.getProperty("zc_new.zc_reviewcConsole.createGroup"),"newGroup_"+review_links,"",map);
				buttonList.add(button);
			}
			//申报人分组页面不需要显示保存按钮//页面实时保存，这里去掉
			/*if("2".equals(entryType)) {
				// 保存
				map.clear();
				map.put("handler", "jobtitle_reviewconsole.saveInfo()");
				button = getButtonStr(ResourceFactory.getProperty("button.save"),"saveGroup_"+review_links,"style:'top:15px !impotrant;',",map);
				buttonList.add(button);
			}*/
			buttonList.add("->");
			//右侧显示申报人等
			String html = "<div style='text-align:right;margin-top: 2px;'><div style='margin:0 8px 0px 0px;display:inline;'>" + ResourceFactory.getProperty("zc.label.applicant")
					+"<span id='personCount_"+review_links+"'>"+getCount(w0301,review_links,"0")+"</span>"+ResourceFactory.getProperty("zc.label.person");
			
			//评委会显示 评委会专家数
			if(!"2".equals(review_links)) {
				html += "<span style='padding-left:12px;'>"+ResourceFactory.getProperty("zc.label.reviewer")+getExpertNum(w0301, review_links, userType, "")+ResourceFactory.getProperty("zc.label.person")+"</span>";
			}
			html += "</div>";
			if("1".equals(evaluationType) && !"3".equals(review_links)) {//打分不显示投票赞成数超2/3自动计算通过(同行也不显示)
				//查询配置信息
				ReviewMeetingPortalBo rmpb = new ReviewMeetingPortalBo(userview, conn);
				LazyDynaBean extendParam = rmpb.getMeetingData(w0301);
				String rate_control = (String) extendParam.get("rate_control_"+review_links);
				String checkedStr = "";
				if("1".equals(rate_control)) {
					checkedStr = "checked='checked'";
				}
				String showItem = "";
				if("1".equals(review_links)) {//评委会
					showItem = "W0553";
					
				} else if("2".equals(review_links)) {//学科组
					showItem = "W0547";
					
				} else if("3".equals(review_links)) {//外部专家
					showItem = "W0531";
				} else if("4".equals(review_links)) {//学院任聘组
					showItem = "W0567";
				}
				String disabled = "";
				if(this.isFinished)
					disabled = "disabled='disabled'";
				FieldItem  agree  = DataDictionary.getFieldItem(showItem, 1);
				String itemdesc = agree.getItemdesc().replace("人数", "");
				html += "<div style='cursor:pointer;display:inline;' onclick='jobtitle_reviewconsole.saveRateControl();'><input "+disabled+"  style='position:relative;top:2px;' type='checkbox' "+checkedStr+" id='approve_"+review_links+"'/>"
					+"<span>"+ResourceFactory.getProperty("zc.label.vote")+itemdesc+ResourceFactory.getProperty("zc.label.pass")+"</span></div>";
			}
			html += "</div>";
			buttonList.add(html);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return buttonList;
	}
	
	/**
	 * 获取申报和评委会等对应的人数
	 * type:0:申报人， 1：评委会
	 * @return
	 */
	public int getCount(String w0301,String review_links,String type) {
		RowSet rs = null;
		int count = 0;
		String sql = "";
		ArrayList<String> list = new ArrayList<String>();
		try {
			ContentDAO dao = new ContentDAO(this.getConn());
			if("0".equals(type)) {
				sql = "select count(1) as count from zc_categories_relations where categories_id in (select categories_id from zc_personnel_categories where review_links=? and w0301=?)";
				list.add(review_links);
			}else if("1".equals(type)) {
				sql = "select count(1) as count from zc_expert_user where w0301=?";
			}
			list.add(w0301);
			rs = dao.search(sql,list);
			if(rs.next()) {
				count = rs.getInt("count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * 获取表格控件tablesql
	 * @param w0301
	 * @param review_links
	 * @return
	 */
	public String getSqlForDiff(String w0301, String review_links) {
		
		StringBuilder sql = new StringBuilder();
		try {
			sql.append("select categories_id,name,c_number,expertnum,submitnum,approval_state  ");

			HashMap<String, String> w0575CodeItemMap = this.getW0575CodeItemMap();
			Iterator iter = w0575CodeItemMap.keySet().iterator();
			while (iter.hasNext()) {
				String codeitemid = (String)iter.next();
				sql.append(",c_" + codeitemid);
			}
			
			sql.append(" from zc_personnel_categories ");
			sql.append(" where w0301='"+w0301+"' and review_links='"+review_links+"'");
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return sql.toString();
	}
	
	/**
	 * 获取表格控件tablesql
	 * @param w0301
	 * @param review_links//1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段
	 * @param userType：1：随机账号，2：非随机，选人的
	 * @return
	 */
	private String getSqlForDiff(String w0301, String review_links, String userType) {
		
		StringBuilder sql = new StringBuilder();
		try {
			sql.append("select seq,categories_id,name,c_number,expertnum,submitnum,approval_state");

			/*HashMap<String, String> w0575CodeItemMap = this.getW0575CodeItemMap();
			Iterator iter = w0575CodeItemMap.keySet().iterator();
			while (iter.hasNext()) {
				String codeitemid = (String)iter.next();
				sql.append(",c_" + codeitemid);
			}*/
			
			sql.append(" from zc_personnel_categories ");
			//approval_state数据库中是varchar(1)的,如果写成nvl(XXX,'')<>''，oracle会什么都查不出来sqlserver没问题
			sql.append(" where w0301='"+w0301+"' and review_links='"+review_links+"' and "+Sql_switcher.isnull("approval_state", "'a'")+" <> 'a'");
			
			if("2".equals(review_links)) {//学科组的时候得加这个判断
				if("1".equals(userType)) {
					sql.append(" and group_id is null");//因为新建分组的时候会将其置为''所以这里只进行is null的判断
				}else {
					sql.append(" and group_id is not null");
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return sql.toString();
	}
	
	/**
	 * 获取表格控件配置
	 * @param w0301
	 * @param review_links
	 * @param evaluationType：1:投票  2：评分
	 * @param entryType：1：创建修改评审会议条件，2：上会界面
	 * @param userType：1：随机账号，2：非随机，选人的
	 * @param screenWidth:浏览器宽度
	 * @return
	 * @throws GeneralException
	 */
		
	public String getTableConfigForDiff(String w0301, String review_links,String entryType,String evaluationType,String userType, int screenWidth) throws GeneralException {
		String config = "";
		try {
			this.asyncTableCategories();
			ArrayList<ColumnsInfo> columnList = this.getColumnListForDiff(w0301, review_links, entryType, evaluationType, userType, screenWidth);
			String sql = this.getSqlForDiff(w0301, review_links, userType);
			
			String key = "jobtitle_reviewfile_console_" + review_links;
			TableConfigBuilder builder = new TableConfigBuilder(key, columnList, key, this.getUserview(), this.getConn());
			builder.setDataSql(sql);
			builder.setOrderBy("order by seq");
			builder.setTableTools(this.getButtonListForDiff(w0301,review_links, entryType, evaluationType,userType));
			//builder.setTitle(JobtitleUtil.ZC_MENU_COMMITTEESHOWTEXT + "成员");
			builder.setEditable(true);
			builder.setAutoRender(false);
			builder.setColumnFilter(false);
			builder.setScheme(false);
			builder.setLockable(false);
			builder.setSelectable(false);
			builder.setAnalyse(false);
			builder.setPageSize(100);
			builder.setSortable(false);;
			config = builder.createExtTableConfig();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return config;
	}
	/**
	 * 
	 * @param w0301
	 * @param review_links//1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段
	 * @param userType//1:投票  2：评分
	 * @return
	 * @throws GeneralException
	 */
	public String getTableConfigForDiffSelPerson(String w0301, String review_links, String evaluationType) throws GeneralException {
		
		String config = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		ArrayList<ColumnsInfo> listShow = new ArrayList<ColumnsInfo>();//列的集合
		ArrayList<ColumnsInfo> listLoadNotShow = new ArrayList<ColumnsInfo>();//列的集合
		ArrayList<ColumnsInfo> listAll = new ArrayList<ColumnsInfo>();//列的集合
		String flag = "";//sql需要查询字段的标识
		String forUsedId = "";//页面需要查询的指标
		String codeid = "";
		StringBuffer sqlOther = new StringBuffer();//多个模板表拼接成的数据
		StringBuffer strTemplateId = new StringBuffer();//有多少个模板
		StringBuffer strId = new StringBuffer();//页面需要查询的指标
		ArrayList<Integer> listArray = new ArrayList<Integer>();
		try {
			//拿出有多少审核材料模版
			DomXml domXml = new DomXml();
			HashMap map = domXml.getJobtitleTemplates(this.conn);
			Iterator it = map.entrySet().iterator();
			StringBuffer sql = new StringBuffer();
			String oldId = "";
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String key = (String)entry.getKey();
				//同行评议的可以选择论文送审模板和材料评审，其他的只能选择材料评审
				if((!"3".equals(review_links) && "6".equals(key)) || ("3".equals(review_links) && ("6".equals(key) || "5".equals(key)))) {
					String value = (String)entry.getValue();
					if(StringUtils.isNotBlank(value)) {
						String[] templateIdArr = value.split(",");
						for(String id : templateIdArr){
							if(StringUtils.isBlank(strTemplateId.toString()) || !listArray.contains(Integer.parseInt(id))) {//不存在的模板添加
								sql.append(" inner join (select Field_name,max(Field_hz) as Field_hz,max(Field_type) as Field_type,max(codeid) as codeid,chgstate from Template_Set where tabID=? "
										+ "and Field_name is not null and Field_name <> '' group by Field_name,chgstate ) a_"+id );
								if(StringUtils.isNotBlank(oldId)) {
									sql.append(" on a_"+id+".Field_name="+oldId+".Field_name and a_"+id+".ChgState="+oldId+".ChgState and a_"+id+".Field_type="+oldId+".Field_type ");
								}
								listArray.add(Integer.parseInt(id));
								oldId = "a_"+id;
								strTemplateId.append(","+id);
							}
						}
					}
				}
			}
			
			if(StringUtils.isBlank(sql.toString())) {//如果模板中什么都没有配置，提示出来
				//请在配置中选择模板后再添加
				throw GeneralExceptionHandler.Handle(new Throwable(ResourceFactory.getProperty("zc_new.zc_reviewfile.addFail")));
			}
			//找出这些模版中共有的指标 group by count()>查找，添加到表格控件columnsInfo中
			String completeSql = "select "+oldId+".Field_name,max("+oldId+".Field_hz) as Field_hz,max("+oldId+".Field_type) as Field_type,max("+oldId+".codeid) as codeid,"+oldId+".chgstate "
					+ "from " + sql.substring(12) + " group by "+oldId+".Field_name,"+oldId+".chgstate order by "+oldId+".Field_name";
			
			
			rs = dao.search(completeSql,listArray);
			//固定要显示的列（姓名，单位，部门，现聘职务，申报职务）现聘职务，申报职务由于指标不确定，在共有指标中查询添加
			ColumnsInfo columnsInfo = new ColumnsInfo();
			columnsInfo = this.getColumnsInfo("A0101", DataDictionary.getFieldItem("A0101").getItemdesc(), 120, "0", "A", 100, 0);
			columnsInfo.setTextAlign("center");
			columnsInfo.setEditableValidFunc("false");
			listShow.add(columnsInfo);
			
			columnsInfo = this.getColumnsInfo("B0110", DataDictionary.getFieldItem("B0110").getItemdesc(), 120, "UN", "A", 100, 0);
			columnsInfo.setTextAlign("center");
			columnsInfo.setEditableValidFunc("false");
			columnsInfo.setCtrltype("3");
			columnsInfo.setNmodule("9");//按照职称业务范围控制显示
			listShow.add(columnsInfo);

			columnsInfo = this.getColumnsInfo("E0122", DataDictionary.getFieldItem("E0122").getItemdesc(), 120, "UM", "A", 100, 0);
			columnsInfo.setTextAlign("center");
			columnsInfo.setEditableValidFunc("false");
			columnsInfo.setCtrltype("3");
			columnsInfo.setNmodule("9");//按照职称业务范围控制显示
			listShow.add(columnsInfo);
			
			/*columnsInfo = this.getColumnsInfo("A5602_1", DataDictionary.getFieldItem("w0513").getItemdesc(), 120, "AJ", "A", 100, 0);
			columnsInfo.setTextAlign("center");
			columnsInfo.setEditableValidFunc("false");
			listShow.add(columnsInfo);

			columnsInfo = this.getColumnsInfo("A5601_2", DataDictionary.getFieldItem("w0515").getItemdesc(), 120, "AJ", "A", 100, 0);
			columnsInfo.setTextAlign("center");
			columnsInfo.setEditableValidFunc("false");
			columnsInfo.setDoFilterOnLoad(true);
			listShow.add(columnsInfo);*/
			
			columnsInfo = this.getColumnsInfo("A0100", ResourceFactory.getProperty("zc_new.zc_reviewcConsole.applicant"), 100, "0", "A", 100, 0);//申报人"
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnsInfo.setEncrypted(true);
			listShow.add(columnsInfo);
			
			columnsInfo = this.getColumnsInfo("ins_id", "", 100, "0", "N", 100, 0);
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnsInfo.setEncrypted(true);
			listShow.add(columnsInfo);
			
			columnsInfo = this.getColumnsInfo("task_id", "", 100, "0", "N", 100, 0);
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnsInfo.setEncrypted(true);
			listShow.add(columnsInfo);
			
			columnsInfo = this.getColumnsInfo("basePre", "", 100, "0", "A", 100, 0);
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnsInfo.setEncrypted(true);
			listShow.add(columnsInfo);
			
			columnsInfo = this.getColumnsInfo("tabid", "", 100, "0", "A", 100, 0);
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnsInfo.setEncrypted(true);
			listShow.add(columnsInfo);
			String conditionItemId = "";
			int nowPost = 0;
			int futurePost = 0;
			//从模版表中查出所有流程没有结束的数据
			HashMap<String,String> maps = getAllUnFinishAndCurrentUser(strTemplateId+",");
			//拼出所有的列
			while (rs.next()) {
				forUsedId = rs.getString("field_name");
				//因为可能有模板不存子啊单位和部门，这里按照usrA01表中查询
				String chgstate = rs.getString("chgstate");
				codeid = rs.getString("codeid");//代码类型
				if(!"e0122".equalsIgnoreCase(forUsedId) && !"b0110".equalsIgnoreCase(forUsedId) && !"a0101".equalsIgnoreCase(forUsedId)) {
					strId.append(",t."+rs.getString("field_name"));//页面查询sql字段
					if(!"0".equals(chgstate)) {
						strId.append("_"+rs.getString("chgstate"));
						forUsedId += "_"+rs.getString("chgstate");
					}
					columnsInfo = this.getColumnsInfo(forUsedId, rs.getString("field_hz"), 100, codeid, rs.getString("Field_type"), 100, 0);
					columnsInfo.setTextAlign("center");
					columnsInfo.setEditableValidFunc("false");
					if(!"AJ".equalsIgnoreCase(codeid)) {
						columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD_HIDE);
					}else {
						if("2".equals(rs.getString("chgstate")) && nowPost == 0) {
							nowPost = nowPost+1;
							columnsInfo = this.getColumnsInfo(forUsedId, DataDictionary.getFieldItem("w0515").getItemdesc(), 100, codeid, rs.getString("Field_type"), 100, 0);
							columnsInfo.setTextAlign("center");
							columnsInfo.setEditableValidFunc("false");
							if(maps.size() > 0)
								columnsInfo.setDoFilterOnLoad(true);
						}else if("1".equals(rs.getString("chgstate")) && futurePost == 0){
							futurePost = futurePost+1;
							columnsInfo = this.getColumnsInfo(forUsedId, DataDictionary.getFieldItem("w0513").getItemdesc(), 100, codeid, rs.getString("Field_type"), 100, 0);
							columnsInfo.setTextAlign("center");
							columnsInfo.setEditableValidFunc("false");
						}else {
							columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD_HIDE);
						}
					}
					listLoadNotShow.add(columnsInfo);
				}
			}
			boolean hasSubjectGroup = false;
			String userType = "";//，学科组阶段1.投票2.评分
			if("1".equals(review_links)) {
				ReviewMeetingPortalBo reviewMeetingPortalBo = new ReviewMeetingPortalBo(this.userview,this.conn);
				LazyDynaBean bean = reviewMeetingPortalBo.getMeetingData(w0301);
				userType = bean.get("evaluationType_2")==null?"":(String)bean.get("evaluationType_2");
			}
			//maps:是模版表中查出所有流程没有结束的数据，放在上面的原因是为了在没有数据的时候不显示申报职务树，否则点击报错
			Iterator iter = maps.entrySet().iterator();
			String item = "";
			//现在换成从模板表中拿，因为选择申报人的时候还没上会
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object temId = entry.getKey();
				Object taskid = entry.getValue();
				if(StringUtils.isNotBlank(sqlOther.toString())) {
					sqlOther.append(" union all ");
				}
				if(StringUtils.isBlank(strId.toString())) {//可能模板里面没有共有的指标，str为空
					item = "";
				}else {
					item = strId.substring(1) + ",";
				}
				//这里要用到上面查出来的指标，
				sqlOther.append("select " + item + (String)temId + " as tabid,u.A0101,u.B0110,u.E0122,u.A0100,t.ins_id,twt.task_id,t.basePre from templet_" + temId + " t "
						+ " left join UsrA01 u on t.A0100 = u.A0100 left join t_wf_task_objlink twt on t.ins_id=twt.ins_id and t.seqnum=twt.seqnum where twt.task_id in (" + taskid + ") ");//and w0555='"+review_links+"'
				//评委会选取学科组阶段通过的人(投票)//评分只能选则学科组阶段选择的人
				if("1".equals(review_links) && StringUtils.isNotBlank(userType)) {
					if("1".equals(userType)) {//
					//hasSubjectGroup(w0301);
						sqlOther.append(" and t.A0100 in  (select w0505 from w05 where W0301='"+w0301+"' and w0505 not in (select w0505 from w05 where w0501 in (select W0501 from zc_categories_relations where categories_id in "
								+ "(select categories_id from zc_personnel_categories where w0301='"+w0301+"' and Review_links='"+review_links+"'))) "
										+ "and (w0555 = '1' or W0555='2') and w0557='01')");
					}else {
						sqlOther.append(" and t.A0100 in  (select w0505 from w05 where w0501 in (select w0501 from zc_categories_relations zcr left join zc_personnel_categories zpc on zcr.categories_id = zpc.categories_id  where zpc.W0301='"+w0301+"' and zpc.review_links=2 and zcr.w0501 not in (select W0501 from zc_categories_relations where categories_id in " 
								+ "(select categories_id from zc_personnel_categories where w0301='"+w0301+"' and Review_links='"+review_links+"'))))");
					}
						
				}else {
					sqlOther.append(" and t.A0100 not in  (select w0505 from w05 where w0501 in (select W0501 from zc_categories_relations where categories_id in (select categories_id from zc_personnel_categories where w0301='"+w0301+"' and Review_links='"+review_links+"')))");
				}
			}
			
			//String sqlOther = "select " + strId.substring(1) + " From W05 where W0301='"+w0301+"' and W0501 not in(select W0501 from zc_categories_relations where categories_id in (select categories_id from zc_personnel_categories where w0301='"+w0301+"' and Review_links='"+review_links+"'))";
			listAll.addAll(listShow);
			listAll.addAll(listLoadNotShow);
			TableConfigBuilder builder = new TableConfigBuilder("jobtitle_reviewfile_console_selperson", listAll, "jobtitle_reviewfile_console_selperson", this.getUserview(), this.getConn());
			if(StringUtils.isNotBlank(sqlOther.toString()))
				builder.setDataSql(sqlOther.toString());
			else
				builder.setDataSql("select * from zc_personnel_categories where 1=2");
			
			StringBuilder orderBy = new StringBuilder();
			orderBy.append(" order by ");
			
			ArrayList btnList = new ArrayList();
			
			ButtonInfo queryBox = new ButtonInfo();
			queryBox.setType(ButtonInfo.TYPE_QUERYBOX);
			queryBox.setText(ResourceFactory.getProperty("zc_new.zc_reviewcConsole.enterB0100E0122Name"));//请输入单位名称、部门、姓名
			queryBox.setFunctionId("ZC00002316");
			queryBox.setShowPlanBox(false);
			btnList.add(queryBox);
			/*btnList.add("->");
			//右侧显示申报人等
			String html = "<div style='float:right;color:red;'>选人规则：仅能选择待您审批的申报材料人员</div>";
			btnList.add(html);*/
			
			builder.setTableTools(btnList);
			builder.setSchemeSaveCallback("jobtitle_reviewconsole.closeSettingWin");//栏目设置关闭回调
			builder.setEditable(true);
			builder.setSelectable(true);
			builder.setAutoRender(false);
			builder.setColumnFilter(true);
			builder.setScheme(true);
			builder.setLockable(false);
			builder.setAnalyse(false);
			builder.setPageSize(20);
			config = builder.createExtTableConfig();
		}catch(Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return config;
	}
	

	public void diffSelPersonFastSearch(ArrayList<String> valuesList) {
		try {
			StringBuilder querySql = new StringBuilder();
			TableDataConfigCache catche = (TableDataConfigCache)this.getUserview().getHm().get("jobtitle_reviewfile_console_selperson");
			
			// 快速查询
			if (valuesList != null && valuesList.size() > 0) {
				querySql.append(" and ( ");
			}
			for (int i = 0; valuesList != null && i < valuesList.size(); i++) {
				String queryVal = valuesList.get(i);
				queryVal = SafeCode.decode(queryVal);// 解码
				if (i != 0) {
					querySql.append("or ");
				}
				querySql.append("(A0101 like '%" + queryVal+"%'");
				List<String> itemids = this.getCodeByLikeDesc(queryVal);
				if(itemids.size()>0) {
					StringBuffer itemBuf = new StringBuffer();
					for(String itemid : itemids) {
						itemBuf.append("'"+itemid+"',");
					}
					itemBuf.setLength(itemBuf.length()-1);
					querySql.append(" or B0110 in ("+itemBuf+") or E0122 in ("+itemBuf+")");
				}
				querySql.append(")");
			}
			if (valuesList != null && valuesList.size() > 0) {
				querySql.append(")");
			}
			catche.setQuerySql(querySql.toString());
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 获取申报职级(w0575)关联代码的代码指标集
	 * @return
	 */
	private HashMap<String, String> getW0575CodeItemMap() {
		
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			String w0575_codesetid = DataDictionary.getFieldItem("W0575").getCodesetid();
			
			if("0".equals(w0575_codesetid)) {
				return map;
			}
			
			ArrayList<CodeItem> codeItemList = AdminCode.getCodeItemList(w0575_codesetid);
			for(CodeItem codeItem : codeItemList) {
				String codeitemid = codeItem.getCodeitem();
				String codeitemname = codeItem.getCodename();
				
				map.put(codeitemid, codeitemname);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return map;
	}
	/**
	 * 新增申报人员分类
	 * @param w0301
	 * @param review_links
	 * @param categories_name
	 * @param userType//1：随机账号，2：非随机，选人的
	 * @return
	 * @throws GeneralException
	 */
	public int addCategorie(String w0301, String review_links, String categories_name, String userType) throws GeneralException{
		
		int errorcode = 1;
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			TableDataConfigCache tableCache = (TableDataConfigCache)userview.getHm().get("jobtitle_reviewfile_console_" + review_links);
			String sql = tableCache.getTableSql();
			String sql_ = "select max(seq) as seq from " + sql.split("from")[1];
			rs = dao.search(sql_);
			int maxSeq = 0;
			if(rs.next()) {
				maxSeq = rs.getInt("seq");
			}
			
			IDFactoryBean idf = new IDFactoryBean();
			RecordVo vo = new RecordVo("zc_personnel_categories");
			
			String id = idf.getId("zc_personnel_categories.categories_id", "", conn);
			vo.setString("categories_id", id);
			vo.setString("w0301", w0301);
			vo.setString("review_links", review_links);
			vo.setString("name", categories_name);
			vo.setString("approval_state", "0");
			int expertNum = getExpertNum(w0301, review_links, userType, id);
			vo.setInt("expertnum", expertNum);
			vo.setInt("submitnum", 0);
			if("2".equals(userType) && "2".equals(review_links)) {//非选人的时候将其置为空，这样展示的时候就能区别是什么类型的is null即可
				vo.setString("group_id", getGroupId(w0301, review_links));
			}
			vo.setInt("seq", maxSeq + 1);
			int result = dao.addValueObject(vo);
			if(result == 1) {
				errorcode = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return errorcode;
	}
	
	/**
	 * 更新申报人员分类名称
	 * @param w0301
	 * @param review_links
	 * @param categories_name
	 * @return
	 * @throws GeneralException
	 */
	public int updateCategorie(ArrayList<DynaBean> savelist) throws GeneralException{
		
		int errorcode = 1;
		
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			for(int i=0; i<savelist.size(); i++) {
				HashMap map = PubFunc.DynaBean2Map(savelist.get(i));
				String categories_id = PubFunc.decrypt((String)map.get("categories_id_e"));
				
				RecordVo vo = new RecordVo("zc_personnel_categories");
				vo.setString("categories_id", categories_id);
				vo = dao.findByPrimaryKey(vo);
				
				Iterator iter = map.entrySet().iterator();
				ArrayList<Object> list = new ArrayList();
				ArrayList wherelist = new ArrayList();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = (String)entry.getKey();
					if(StringUtils.isNotEmpty(key) && (key.startsWith("c_") || "name".equals(key) || "expertnum".equalsIgnoreCase(key))) {
						String val = String.valueOf(entry.getValue());
						if("c_number".equalsIgnoreCase(key) && "null".equalsIgnoreCase(val)) 
							val = null;
							
						if(key.startsWith("c_") && "null".equalsIgnoreCase(val)) {
							val = "0";
						}
						//如果是expertnum并且是null，该情况下直接可以置为0，对于投票状态下的可以置为空这里的值是''，不会置为0
						if("expertnum".equalsIgnoreCase(key) && "null".equalsIgnoreCase(val)) {
							val = "0";
						}
						vo.setString(key, val);
						
					}else {
						continue ;
					}
				
				}
				int result = dao.updateValueObject(vo);
				if(result == 1) {
					errorcode = 0;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
		
		return errorcode;
	}
	/**
	 * 删除申报人员分类名称
	 * @param w0301
	 * @param review_links
	 * @param categories_name
	 * @return
	 * @throws GeneralException
	 */
	public int deleteCategorie(String categories_id,String w0301,String review_links) throws GeneralException{
		
		int errorcode = 1;
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			IDFactoryBean idf = new IDFactoryBean();
			RecordVo vo = new RecordVo("zc_personnel_categories");
			vo.setString("categories_id", categories_id);
			
			vo = dao.findByPrimaryKey(vo);
			
			int result = dao.deleteValueObject(vo);
			if(result > 0) {
				errorcode = 0;
				// 删除人员的投票环节和账号等需要重置
				//this.stopCategories(categories_id);
				String sqls = "select w0505 from W05 where W0501 in (select W0501 from zc_categories_relations where categories_id = ?)";
				ArrayList<String> a0100List = new ArrayList<String>();
				rs = dao.search(sqls, Arrays.asList(new String[] {categories_id}));
				while(rs.next()) {
					a0100List.add(rs.getString("w0505"));
				}
				StartReviewBo StartReviewBo = new StartReviewBo(this.conn,this.userview);
				StartReviewBo.cleanKh_ObjectByA0100(a0100List, w0301, review_links);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return errorcode;
	}
	/**
	 * 重置投票环节和账号
	 * @param categories_id
	 * @throws GeneralException
	 */
	public void stopCategories(String categories_id) throws GeneralException{
		
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			// 把删除的人员的投票环节置为空
			/*String upSql = "update W05 set W0573=? , W0555=? where W0501 in (select w0501 from zc_categories_relations where categories_id=?)";
			List values = new ArrayList();
			values.add(null);
			values.add(null);
			values.add(categories_id);
			dao.update(upSql, values);*/
			
			// 把删除的人员账号置为禁用
			String upSql1 = "update zc_expert_user set state=0 where w0501 in (select w0501 from zc_categories_relations where categories_id=?)";
			dao.update(upSql1, Arrays.asList(new String[] {categories_id}));
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
	}
	/**
	 * 新增人员分类关联
	 * @param w0301
	 * @param review_links
	 * @param categories_name
	 * @return
	 * @throws GeneralException
	 */
	public int addCategories_relations(String categories_id, ArrayList<String> w0501_eList, String c_level, int queue, String w0301, String review_links) throws GeneralException{
		
		int errorcode = 1;
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			String sql_ = "select max(seq) as seq from zc_categories_relations where categories_id=?";
			rs = dao.search(sql_, Arrays.asList(new String[] {categories_id}));
			int maxSeq = 0;
			if(rs.next()) {
				maxSeq = rs.getInt("seq");
			}
			
			for(String w0501_e : w0501_eList) {
				
				RecordVo vo = new RecordVo("zc_categories_relations");
				vo.setString("categories_id", categories_id);
				vo.setString("w0501", w0501_e);
				vo.setString("c_level", c_level);
				vo.setInt("queue", queue);
				vo.setInt("seq", ++maxSeq);
				int result = dao.addValueObject(vo);
				if(result == 1) {
					errorcode = 0;
					// 同步人数
					//this.asyncCategoriesPersonNum(categories_id, c_level);
				}else {
					errorcode = 1;
					break;
				}
			}
			
			sql_ = "select approval_state,expertnum from zc_personnel_categories where categories_id=?";
			rs = dao.search(sql_, Arrays.asList(new String[] {categories_id}));
			String approval_state = "";
			int expertnum = 0;
			if(rs.next()) {
				approval_state = rs.getString("approval_state");
				expertnum = rs.getInt("expertnum");
			}
			//只有在未启动的时候直接生成审核账号，否则在导出审核账号后，再添加人，这样如果不重新导出审核账号会导致新增的没有进行账号的表中，页面永远刷新不出来人
			if(StringUtils.isNotBlank(approval_state) && "0".equals(approval_state) && expertnum > 0) {
				//生成审核账号密码，在审核账号的时候
				OutPositionalStaffBo opsb=new OutPositionalStaffBo(this.conn, this.userview,w0301,Integer.parseInt(review_links), 1);
				opsb.getOutExcelList(w0301,Integer.parseInt(review_links), 1, PubFunc.encrypt(categories_id));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
		
		return errorcode;
	}
	
	/**
	 * 删除人员分类关联
	 * @param w0301
	 * @param review_links
	 * @param categories_name
	 * @return
	 * @throws GeneralException
	 */
	public int deleteCategories_relations(String categories_id, String w0501, String c_level,String queue) throws GeneralException {
		
		int errorcode = 1;
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			IDFactoryBean idf = new IDFactoryBean();
			RecordVo vo = new RecordVo("zc_categories_relations");
			vo.setString("categories_id", categories_id);
			vo.setString("w0501", w0501);
			vo.setString("c_level", c_level);
			
			vo = dao.findByPrimaryKey(vo);
			int result = dao.deleteValueObject(vo);
			if(result == 1) {
				errorcode = 0;
				String sql = "select count(1) as count from zc_categories_relations where categories_id=? and queue=?";
				ArrayList<String> list = new ArrayList<String>();
				list.add(categories_id);
				list.add(queue);
				rs = dao.search(sql, list);
				if(rs.next()) {
					int count = rs.getInt("count");
					if(count == 0) {
						sql = "update zc_categories_relations set queue = queue-1 where categories_id=? and queue>?";
						dao.update(sql, list);
					}
				}
				// 同步人数
				//this.asyncCategoriesPersonNum(categories_id, c_level);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return errorcode;
	}
	/**
	 * 同步申报人员分类表（zc_personnel_categories）表结构
	 * 同步规则：评审会议初审材料汇总信息表（w05）的申报职级（W0575）关联的所有代码项，以“C_代码项”的方式作为表的列。
	 * @return
	 */
	public int asyncTableCategories() throws GeneralException {
		
		int errorcode = 1;
		try {
			// 查看目前表结构是否是w0575关联的代码结构
			boolean isNeedToAsync = false;
			DbWizard dbWizard = new DbWizard(this.getConn());
			HashMap<String, String> w0575CodeItemMap = this.getW0575CodeItemMap();
			Iterator iter = w0575CodeItemMap.keySet().iterator();
			while (iter.hasNext()) {
				String codeitemid = (String)iter.next();
				if(!dbWizard.isExistField("zc_personnel_categories", "c_" + codeitemid, false)) {
					isNeedToAsync = true;
					break;
				}
			}
			
			// 同步表结构
			if(isNeedToAsync) {
				errorcode = this.createTableCategories();
			} else {
				errorcode = 0;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return errorcode;
	}
	/**
	 * 创建申报人员分类表（zc_personnel_categories）
	 * @return
	 * @throws GeneralException
	 */
	private int createTableCategories() throws GeneralException {
		
		int errorcode = 1;
		try {
			DbWizard dbWizard = new DbWizard(this.getConn());
			/** 先删表 */ 
			if (dbWizard.isExistTable("zc_personnel_categories", false)) {
				dbWizard.dropTable("zc_personnel_categories");
			}
			
			/** 组装表结构 */ 
			// 固定列
			Table table = new Table("zc_personnel_categories");
			table.addField(getField("categories_id", "A", 10, true));
			table.addField(getField("w0301", "A", 10, false));
			table.addField(getField("review_links", "I", 10, false));
			table.addField(getField("name", "A", 100, false));
			table.addField(getField("c_number", "I", 10, false));
			table.addField(getField("approval_state", "A", 1, false));
			table.addField(getField("expertnum", "I", 10, false));
			table.addField(getField("submitnum", "I", 10, false));
			// w0575关联的代码型列
			HashMap<String, String> w0575CodeItemMap = this.getW0575CodeItemMap();
			Iterator iter = w0575CodeItemMap.keySet().iterator();
			while (iter.hasNext()) {
				String codeitemid = (String)iter.next();
				table.addField(getField("c_" + codeitemid, "I", 10, false));
			}
			
			/** 创建表 */
			if(dbWizard.createTable(table)) {
				DBMetaModel dbmodel = new DBMetaModel(this.getConn());
				dbmodel.reloadTableModel("zc_personnel_categories");
				errorcode = 0;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return errorcode;
	}
	/**
	 * 获取表的列
	 * @param fieldname
	 * @param a_type
	 * @param length
	 * @param key
	 * @return
	 */
	private Field getField(String fieldname, String a_type, int length, boolean key) {

		Field obj = new Field(fieldname, fieldname);
		if ("A".equals(a_type)) {
			obj.setDatatype(DataType.STRING);
			obj.setLength(length);
		} else if ("M".equals(a_type)) {
			obj.setDatatype(DataType.CLOB);
		} else if ("I".equals(a_type)) {
			obj.setDatatype(DataType.INT);
			obj.setLength(length);
		} else if ("N".equals(a_type)) {
			obj.setDatatype(DataType.FLOAT);
			obj.setLength(length);
			obj.setDecimalDigits(5);
		} else if ("D".equals(a_type)) {
			obj.setDatatype(DataType.DATE);
		} else {
			obj.setDatatype(DataType.STRING);
			obj.setLength(length);
		}
		if (key)
			obj.setNullable(false);
		obj.setKeyable(key);
		return obj;
	}
	/**
	 * 该方法是为了在进度一栏中显示出已经评价的人
	 * @param w0301
	 * @param review_links
	 * @param evaluationType 1:投票  2：评分
	 * @return
	 * @throws GeneralException
	 */
	public HashMap<String, String> getCategoriesMap(String w0301, String review_links,String evaluationType) throws GeneralException {
		HashMap<String, String> map = new HashMap<String, String>();
		
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.getConn());
			//1:投票	  2：评分
			if("1".equals(evaluationType)) {
		    	rs = dao.search("select distinct username,categories_id  From zc_data_evaluation where expert_state=3 and username in "
		    			+ "(select username from zc_expert_user where W0301=? and type=?) order by categories_id", Arrays.asList(new String[] {w0301, review_links}));
		    	
		    	while(rs.next()){
		    		String username = rs.getString("username");
		    		String categories_id = rs.getString("categories_id");
		    		map.put(PubFunc.encrypt(categories_id), map.get(PubFunc.encrypt(categories_id))==null?username:map.get(PubFunc.encrypt(categories_id))+","+username);
		    	}
			}else {
				ArrayList<String> list = new ArrayList<String>();
				String sql = "select categories_id,Mainbody_id FROM kh_mainbody km left join kh_object ko on km.kh_object_id=ko.id "
		    			+ "left join W05 on ko.Object_id=W05.W0505 left join zc_categories_relations zcr on w05.W0501=zcr.w0501 "
		    			+ "WHERE  km.Status = ? AND km.Relation_id=? and w05.W0301=? and w05.W0555= ? "
		    			+ "group by categories_id,Mainbody_id";
				list.add("2");
				list.add("1_"+w0301+"_"+review_links);
				list.add(w0301);
				list.add(review_links);
				rs = dao.search(sql,list);
		    	
		    	while(rs.next()){
		    		String username = rs.getString("Mainbody_id");
		    		String categories_id = rs.getString("categories_id");
		    		map.put(PubFunc.encrypt(categories_id), map.get(PubFunc.encrypt(categories_id))==null?username:map.get(PubFunc.encrypt(categories_id))+","+username);
		    	}
		    	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeDbObj(rs);
		}
		return map;
	}
	/**
	 * 获取申报人对应的数据信息
	 * @param w0301
	 * @param review_links
	 * @return
	 * @throws GeneralException
	 */
	public HashMap<String, ArrayList<HashMap<String, String>>> getCategories_relations(String w0301, String review_links) throws GeneralException {
		HashMap<String, ArrayList<HashMap<String, String>>> map = new HashMap<String, ArrayList<HashMap<String, String>>>();
		
		RowSet rs = null;
		RowSet rs1 = null;
		RowSet rs2 = null;
		try {
			HashMap w05ItemMap = getW05ItemString();
			ArrayList<String> listItem = (ArrayList<String>) w05ItemMap.get("arrayItem");
			ContentDAO dao = new ContentDAO(this.getConn());
			rs = dao.search("select A.categories_id,A.w0501,A.c_level,A.queue" + w05ItemMap.get("stringItem") + " from zc_categories_relations A,W05 B where A.w0501=B.W0501 and B.w0301='"+w0301+"' order by A.seq");
			while(rs.next()) {
				
				String categories_id = PubFunc.encrypt(rs.getString("categories_id"));
				String c_level = rs.getString("c_level");
				
				String key = categories_id + "_" + c_level;
				ArrayList<HashMap<String, String>> list = map.get(key);
				if(list == null) {
					list = new ArrayList<HashMap<String, String>>();
				}
				
				HashMap<String, String> person = new HashMap<String, String>();
				person.put("categories_id", categories_id);
				person.put("c_level", rs.getString("c_level"));
				person.put("queue", rs.getString("queue"));
				person.put("w0501", PubFunc.encrypt(rs.getString("w0501")));
				person.put("w0503", PubFunc.encrypt(rs.getString("W0503")));
				person.put("w0505", PubFunc.encrypt(rs.getString("W0505")));
				person.put("w0511", rs.getString("W0511"));// 姓名
				person.put("w0507", AdminCode.getCodeName("UN", rs.getString("W0507")));// 单位
				person.put("w0509", AdminCode.getCodeName("UM", rs.getString("W0509")));// 部门
				person.put("nbasea0100_e", PubFunc.encrypt(rs.getString("w0503")+rs.getString("w0505")));// 人员库+a0100
				String w0513 = rs.getString("W0513");
				if(StringUtils.isEmpty(w0513)) {
					w0513 = "";
				}
				person.put("w0513", AdminCode.getCodeName("AJ",w0513));//现聘名称
				String w0515 = rs.getString("W0515");
				if(StringUtils.isEmpty(w0515)) {
					w0515 = "";
				}
				person.put("w0515", AdminCode.getCodeName("AJ",w0515));//申报职位
				PhotoImgBo photoImgBo = new PhotoImgBo(this.conn);
				String imgpath = photoImgBo.getPhotoPathLowQuality(rs.getString("w0503"), rs.getString("w0505"));
				person.put("imgpath", imgpath);
				
				// 申报人一共有几个人给他投票
				int expert_count = 0;
				String sql2 = "select expertnum from zc_personnel_categories where categories_id=? and approval_state = 1";
				rs1 = new ContentDAO(this.getConn()).search(sql2, Arrays.asList(new String[] {categories_id}));
				if(rs1.next()) {
					expert_count = rs1.getInt("count");
					person.put("expert_count", String.valueOf(expert_count));
				}
				
				// 给他投票的人有几个是赞成
				int expert_already_count = 0;
				String sql3 = "select count(username) as count from zc_data_evaluation where w0501=? and username in (select username from zc_expert_user where w0501=? and type=? and state=1) and approval_state=1 and expert_state=3";
				rs2 = new ContentDAO(this.getConn()).search(sql3, Arrays.asList(new String[] {rs.getString("w0501"), rs.getString("w0501"), review_links}));
				if(rs2.next()) {
					expert_already_count = rs2.getInt("count");
					person.put("expert_already_count", String.valueOf(expert_already_count));
				}
				
				String isPass = "02";
				if(expert_count > 0 && expert_already_count >= Math.ceil((double)((double)(expert_count*2)/3)))
					isPass="01";
				else
					isPass="02";
				person.put("ispass", isPass);
				
				
				for(int i = 0; i < listItem.size(); i++) {
					String value = "";
					FieldItem fieldList = DataDictionary.getFieldItem(listItem.get(i), "w05");
					if(!"0".equals(fieldList.getCodesetid())) {
						//update haosl 2019-12-17
						value = AdminCode.getCodeName(fieldList.getCodesetid(),rs.getString(listItem.get(i)));
					}else {
						value = rs.getString(listItem.get(i));
					}
					person.put(listItem.get(i), value);
				}
				list.add(person);
				
				map.put(key, list);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(rs1);
			PubFunc.closeDbObj(rs2);
		}
		return map;
	}
	public LazyDynaBean getCustomBean(String handler,String icon,String xtype,String value){
		LazyDynaBean bean = new LazyDynaBean();
		try{
			if(xtype!=null&&xtype.length()>0)
				bean.set("xtype", xtype);
			if(value!=null&&value.length()>0)
				bean.set("value", value);
			if(handler!=null&&handler.length()>0)
				bean.set("handler", handler);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
	}
	/**
	 * 更新投票状态
	 * @param categories_id
	 * @param approval_state
	 * @param review_links//1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段 
	 * @param userType//1：随机账号，2：非随机，选人的
	 * @return
	 * @throws GeneralException
	 */
	public int updateApproval_state(String w0301,String review_links,String userType,String categories_id, String approval_state) throws GeneralException{
		
		int errorcode = 1;
		String sql = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			RecordVo vo = new RecordVo("zc_personnel_categories");
			vo.setString("categories_id", categories_id);
			vo = dao.findByPrimaryKey(vo);
			//重新统计人数，保证专家数是对的
			if("1".equals(approval_state)) {
				int expertnum=0;
				//对于二级单位和评委会的需要从w03表中去找人数
				if("1".equals(review_links)) {
					if("1".equals(userType)) {
						sql = "select W0315 as expertnum from w03 where w0301=?";
					}else {
						sql = "select count(1) as expertnum from zc_judgingpanel_experts where committee_id = (select committee_id from w03 where w0301=?) "
								+ "and ("+ Sql_switcher.today() +" between start_date and end_date or end_date is null)  and flag=1";
					}
					rs = dao.search(sql, Arrays.asList(new String[] {w0301}));
					while(rs.next()) {
						expertnum = rs.getInt("expertnum");
					}
					vo.setInt("expertnum", expertnum);
				}else if("4".equals(review_links)) {
					if("1".equals(userType)) {
						sql = "select W0323 as expertnum from w03 where w0301=?";
					}else {
						sql = "select count(1) as expertnum from zc_judgingpanel_experts where committee_id = (select sub_committee_id from w03 where w0301=?) "
								+ "and ("+ Sql_switcher.today() +" between start_date and end_date or end_date is null)  and flag=1";
					}
					rs = dao.search(sql, Arrays.asList(new String[] {w0301}));
					while(rs.next()) {
						expertnum = rs.getInt("expertnum");
					}
					vo.setInt("expertnum", expertnum);
				}else if("2".equals(review_links)) {
					if("2".equals(userType)) {
						sql = "select count(1) as expertnum from zc_expert_user where W0301=? and type=? and group_id = (select group_id from zc_personnel_categories where categories_id=?)";
						rs = dao.search(sql, Arrays.asList(new String[] {w0301,review_links,categories_id}));
						while(rs.next()) {
							expertnum = rs.getInt("expertnum");
						}
						vo.setInt("expertnum", expertnum);
					}
				}
			}
			String _approval_state = vo.getString("approval_state");
			if(!"3".equals(_approval_state) && !"3".equals(approval_state)) {
				vo.setInt("submitnum", 0);
			}
			vo.setString("approval_state", approval_state);
			int result = dao.updateValueObject(vo);
			if(result == 1) {
				errorcode = 0;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return errorcode;
	}
	public HashMap<String, String> getProgressAndApprovalState(String w0301, String review_links) throws GeneralException{
		HashMap<String, String> map = new HashMap<String, String>();
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String sql = "select categories_id,progress,approval_state from zc_personnel_categories where w0301=? and review_links=?";
			rs = dao.search(sql, Arrays.asList(new String[] {w0301, review_links}));
			while(rs.next()) {
				String categories_id = rs.getString("categories_id");
				int progress = rs.getInt("progress");
				String approval_state = rs.getString("approval_state");
				
				String key = PubFunc.encrypt(categories_id);
				map.put(key, progress+"_"+approval_state);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return map;
	}
	public void asyncCategoriesPersonNum(String categories_id, String c_level) throws GeneralException{
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			
			int num = 0;
			String sql = "select count(w0501) as count from zc_categories_relations where categories_id=? and c_level=?";
			rs = dao.search(sql, Arrays.asList(new String[] {categories_id, c_level}));
			if(rs.next()) {
				num = rs.getInt("count");
			}
			if(num > 0) {
				RecordVo vo1 = new RecordVo("zc_personnel_categories");
				vo1.setString("categories_id", categories_id);
				vo1 = dao.findByPrimaryKey(vo1);
				String key = "c_"+c_level;
				if("person".equalsIgnoreCase(c_level)) {
					key = "c_number";
				}
				vo1.setInt(key, num);
				dao.updateValueObject(vo1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
	}
	public static String createTaskidValidCode(String taskid) {
		String key = "";
		try {
			key = PubFunc.encrypt(taskid+"_"+new SimpleDateFormat("HHmmssSSS").format(new Date()));
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return key;
	}
	/**
	 * 组成<group_name, PubFunc.encrypt(group_id)+"_"+count>
	 * @return
	 * @throws GeneralException
	 */
	public HashMap<String, String> getGroupMap(String w0301,String reviewLinks) throws GeneralException {
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		HashMap<String, String> map = new HashMap<String, String>();
		ArrayList<String> list = new ArrayList<String>();
		String group_name = "";
		String group_id = "";
		String count = "";
    	try {
    		String sql = "select zs.group_id,max(zs.group_name) as group_name,count(zpc.group_id) as count from zc_subjectgroup zs,zc_expert_user zpc "
    				+ "where zs.group_id=zpc.group_id and W0301=? and type=? and w0501=? group by zs.group_id";
    		list.add(w0301);
    		list.add(reviewLinks);
    		list.add("xxxxxx");
    		rs = dao.search(sql,list);
    		while(rs.next()){
    			group_name = rs.getString("group_name");
    			group_id = rs.getString("group_id");
    			count = rs.getString("count");
    			map.put(group_name, PubFunc.encrypt(group_id)+"_"+count);
    		}
    	}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeDbObj(rs);
		}
    	return map;
	}
	
	/**
	 * 组成<categories_id,group_id>当前的每个分组对应的group_id
	 * @return
	 * @throws GeneralException
	 */
	public HashMap<String, String> getCountMap(String w0301,String review_links) throws GeneralException {
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		HashMap<String, String> map = new HashMap<String, String>();
		ArrayList<String> list = new ArrayList<String>();
		String group_id = "";
		String categories_id = "";
    	try {
    		//根据会议号找到所有分组对应的分组名和数量，这样在审批人列进行显示
    		String sql = "select categories_id,group_id FROM zc_personnel_categories where w0301=? and review_links=?";
    		list.add(w0301);
    		list.add(review_links);
    		rs = dao.search(sql,list);
    		while(rs.next()){
    			categories_id = rs.getString("categories_id");
    			group_id = rs.getString("group_id");
    			map.put(PubFunc.encrypt(categories_id), PubFunc.encrypt(group_id));
    		}
    	}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeDbObj(rs);
		}
    	return map;
	}
	
	/**
	 * 获取所有的来自上报的评审材料流程未结束且报给当前操作用户insid
	 * @return
	 */
	private HashMap<String,String> getAllUnFinishAndCurrentUser(String strTemplateId) {
		HashMap<String,String> map = new HashMap<String,String>();
		LazyDynaBean paramBean=new LazyDynaBean();
		String tabid = "";
		paramBean.set("start_date", "");
		paramBean.set("end_date","");
		paramBean.set("days","");
		paramBean.set("query_type","");
		paramBean.set("tabid","");
		paramBean.set("module_id","9");
		paramBean.set("bs_flag","1");
		    
		TemplatePendingTaskBo templatePendingTaskBo=new TemplatePendingTaskBo(this.conn,this.userview);
		ArrayList dataList=templatePendingTaskBo.getDBList(paramBean,this.userview);
		for(int i = 0; i < dataList.size(); i++) {
			paramBean = (LazyDynaBean)dataList.get(i);
			tabid = (String)paramBean.get("tabid");
			if(strTemplateId.indexOf(","+tabid+",") != -1) {
				//这里可能返回相同的tabid，不同的ins_is,map如果是相同的key会覆盖
				if(StringUtils.isNotBlank((String)paramBean.get("taskid_noEncrypt")) && !"0".equals((String)paramBean.get("taskid_noEncrypt")))
					map.put(tabid, (map.get(tabid)==null?"":map.get(tabid)+",")+(String)paramBean.get("taskid_noEncrypt"));
			}
		}
		return map;
	}
	
	/**
	 * 判断当前会议中是否已存在，如果已经存在了，就更新状态W0555
	 * @param w0301
	 * @param a0100_eList
	 * @param insId_eList
	 * @param tabId_eList
	 * @param taskId_eList
	 * @param basePre_eList
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList<String> isExist(String w0301, ArrayList<MorphDynaBean> infoList,String review_link,String categories_id) throws GeneralException {
		RowSet rs = null;
		String nbase = "";
		String a0100 = "";
		String sql = "";
		ContentDAO dao=new ContentDAO(this.conn);
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> listW0501 = new ArrayList<String>();//返回新生成的w0501的集合
		try {
			for(int i=0;i<infoList.size();i++)
			{
				a0100 = PubFunc.decrypt((String)infoList.get(i).get("a0100"));
				nbase = PubFunc.decrypt((String)infoList.get(i).get("basePre"));
				sql = "select count(1) as count,max(w0501) as w0501 from w05 where w0301=? and w0505=? and w0503=?";
				list.clear();
				list.add(w0301);
				list.add(a0100);
				list.add(nbase);
				rs = dao.search(sql, list);
				if(rs.next()) {
					if(rs.getInt("count") > 0) {//说明已经上会了
						listW0501.add(rs.getString("w0501"));
						sql = "update w05 set w0555=?,create_time=" + Sql_switcher.today() + " where w0301=? and w0505=? and w0503=?";
						list = new ArrayList<String>();
						list.add(review_link);
						list.add(w0301);
						list.add(a0100);
						list.add(nbase);
						dao.update(sql, list);
						
						//在已经上过会的时候，可能模板会有数据改变了，这样再上会重新更新下数据
						String tabid = PubFunc.decrypt((String)infoList.get(i).get("tabid"));
						String ins_id = PubFunc.decrypt((String)infoList.get(i).get("ins_id"));
						String taskid = PubFunc.decrypt((String)infoList.get(i).get("task_id"));
						
						String addr ="/general/template/edit_form.do?b_query=link"
								+"&tabid="+tabid+"&ins_id="+ins_id+"&taskid="+PubFunc.encrypt(taskid)
								+"&sp_flag=2&returnflag=noback"
								+"&taskid_validate="+ReviewConsoleBo.createTaskidValidCode(taskid);
						DomXml	domXml = new DomXml();
						//比如张三同时填报了论文送审和材料审查，这样上一个阶段是同行选择了张三，当前阶段有要选择张三，需要同时更新下w0535addr，w05表中材料送审地址
						String w0535addr="";
						String w0537addr="";
						String templateId= ","+domXml.getJobtitleTemplateByType(this.conn, "5")+",";
						//论文送审
						if (templateId.contains(","+tabid+","))
							w0537addr=addr;
						//材料报审
						templateId= ","+domXml.getJobtitleTemplateByType(this.conn, "6")+",";
						if (templateId.contains(","+tabid+","))
							w0535addr=addr;
						
						TemplateTableBo tablebo=new TemplateTableBo(this.conn,Integer.parseInt(tabid),this.userview);
						ArrayList fieldlist=tablebo.getAllFieldItem();	
						HashMap updateMap=getUpdateFieldsMap(fieldlist);
						String updateSql =getChangeUpdateSQL("templet_"+Integer.parseInt(tabid),ins_id,w0301,updateMap,w0535addr,w0537addr,nbase,a0100);
						dao.update(updateSql);	
					}else {//不存在的人直接上会
						listW0501.addAll(subMeeting(w0301, infoList.get(i),review_link, categories_id));
					}
				}
			} 
		} catch(Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return listW0501;
	}
	
	/**
	 * 创建评审会议的时候添加申报人，需要将这个申报人直接上会
	 * @param w0301
	 * @param a0100_eList
	 * @param insId_eList
	 * @param tabId_eList
	 * @param taskId_eList
	 * @param basePre_eList
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList<String> subMeeting(String w0301, MorphDynaBean infoList,String review_link, String categories_id) throws GeneralException {
		RowSet rSet = null;
		IDGenerator idg = new IDGenerator(2, this.conn);
		ContentDAO dao=new ContentDAO(this.conn);
		DbWizard dbw=new DbWizard(this.conn);
		DomXml	domXml = new DomXml();
		ArrayList<String> list = new ArrayList<String>();//返回新生成的w0501的集合
		try {
			String a0100 = PubFunc.decrypt((String)infoList.get("a0100"));
			String ins_id = PubFunc.decrypt((String)infoList.get("ins_id"));
			String tabid = PubFunc.decrypt((String)infoList.get("tabid"));
			String taskid = PubFunc.decrypt((String)infoList.get("task_id"));
			String nbase = PubFunc.decrypt((String)infoList.get("basePre"));
			
			String srcTab="templet_"+Integer.parseInt(tabid);
			boolean bW0535=false;//评审材料模板
			boolean bW0537=false;//送审论文模板
			String templateId= ","+domXml.getJobtitleTemplateByType(this.conn, "5")+",";
			if (templateId.contains(","+tabid+",")){
				bW0537=true;
			}
			templateId= ","+domXml.getJobtitleTemplateByType(this.conn, "6")+",";
			if (templateId.contains(","+tabid+",")){
				bW0535=true;
			}
			
			String addr ="/general/template/edit_form.do?b_query=link"
				+"&tabid="+tabid+"&ins_id="+ins_id+"&taskid="+PubFunc.encrypt(taskid)
				+"&sp_flag=2&returnflag=noback"
				+"&taskid_validate="+ReviewConsoleBo.createTaskidValidCode(taskid);
			RecordVo w05Vo=new RecordVo("w05");
			String  w0501 = idg.getId("W05.W0501");
			list.add(w0501);
			w05Vo.setString("w0501", w0501);
			w05Vo.setString("w0505",a0100);
			//if(StringUtils.isNotBlank(sub_committee_id))
			//	w05Vo.setString("w0561",sub_committee_id);
			w05Vo.setString("w0503",nbase);						
			w05Vo.setString("w0301",w0301);//评审会议ID
			w05Vo.setString("w0525","0000");//导入标识
			w05Vo.setInt("w0517",0);//专家人数(评委会)
			w05Vo.setInt("w0519",0);//参会人数(评委会)
			w05Vo.setInt("w0549",0);//反对人数(评委会)
			w05Vo.setInt("w0551",0);//弃权人数(评委会)
			w05Vo.setInt("w0553",0);//赞成人数(评委会)
			w05Vo.setInt("w0521",0);//专家人数(学科组)
			w05Vo.setInt("w0543",0);//反对人数(学科组)
			w05Vo.setInt("w0545",0);//弃权人数(学科组)
			w05Vo.setInt("w0547",0);//赞成人数(学科组)
			w05Vo.setInt("w0523",0);//专家人数(同行专家)
			w05Vo.setInt("w0527",0);//反对人数(同行专家)
			w05Vo.setInt("w0529",0);//弃权人数(同行专家)
			w05Vo.setInt("w0531",0);//赞成人数(同行专家)
			w05Vo.setInt("w0571",0);//专家人数(二级单位)
			w05Vo.setInt("w0563",0);//反对人数(二级单位)
			w05Vo.setInt("w0565",0);//弃权人数(二级单位)
			w05Vo.setInt("w0567",0);//赞成人数(二级单位)
			w05Vo.setInt("w0555",Integer.parseInt(review_link));//赞成人数(二级单位)
			w05Vo.setDate("create_time",PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"));
			w05Vo.setString("create_user",this.userview.getS_userName());                
			w05Vo.setString("create_fullname",this.userview.getUserFullName());  
			if (dao.addValueObject(w05Vo)>=0){
				//addRecordList.add(nbase+a0100);//新添加的记录，加入list chent 20170801
				//更新 b0110 e0122 a0101
				String usrTab=nbase+"A01";
				String strJoin = "w05.w0505="+usrTab+".a0100";
				String strSet = "w05.w0507="+usrTab+".b0110"+"`"
				               +"w05.w0509="+usrTab+".e0122"+"`"
				               +"w05.w0511="+usrTab+".a0101";
				dbw.updateRecord("w05", usrTab,strJoin,strSet ,
				        "w05.w0505='"+a0100+"' and w05.w0503='"+nbase+"'",
				        usrTab+".a0100='"+a0100+"'"
				        
				        );
			}
			TemplateTableBo tablebo=new TemplateTableBo(this.conn,Integer.parseInt(tabid),this.userview);
			ArrayList fieldlist=tablebo.getAllFieldItem();	
			HashMap updateMap=getUpdateFieldsMap(fieldlist);
			
			//更新 
			List<Map<String,String>> w0505List = new ArrayList<Map<String,String>>();
			HashMap<String,String> map = null;
			
			//if(addRecordList.contains(nbase+a0100)) {//是新添加的，才更新.曾经上会过的人就不更新 chent 20170801
				String w0535addr="";
				String w0537addr="";
				if (bW0535){
					w0535addr=addr;
				}
				if (bW0537){
					w0537addr=addr;
				}
				//更新已存在的记录
				String updateSql =getChangeUpdateSQL(srcTab,ins_id,w0301,updateMap,w0535addr,w0537addr,nbase,a0100);
				dao.update(updateSql);		
				map = new HashMap<String,String>();
				map.put("w0505", a0100);
				map.put("w0503",nbase);
				w0505List.add(map);
				//}
				
                
			//同步投票数据对于最新的程序这里不需要同步
			//this.syncW05Data(w0301,w0505List);
			//获得公示、投票环节显示申报材料表单上传的word模板内容参数设置 将文件路径存到W0536中
			this.saveTemplateDataToW0536(tabid,ins_id,"11","1",taskid,w0301);
		} catch(Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		} finally {
			PubFunc.closeDbObj(rSet);
		}
		return list;
	}
	
	/** 
	* @Title: getUpdateFieldsMap 
	* @Description: 获取需要更新的字段对应更新  放在map里面，目标字段=源字段
	* @param @param fieldlist
	* @param @return
	* @param @throws GeneralException
	* @return HashMap
	*/ 
	private HashMap getUpdateFieldsMap(ArrayList fieldlist)throws GeneralException
	{
		String w0513Fld="";//现聘职称
		String w0515Fld="";//申报职称名称

		HashMap map=new HashMap();
		try
		{
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem item=(FieldItem)fieldlist.get(i);
				String fieldname=item.getItemid();
				String codesetid = item.getCodesetid();
				//if ("AJ".equalsIgnoreCase(codesetid) || "YM".equalsIgnoreCase(codesetid) || "YL".equalsIgnoreCase(codesetid) || "YN".equalsIgnoreCase(codesetid)){//不同职位模板的代码型不同，有哪个取哪个。chent 20170413
				if ("AJ".equalsIgnoreCase(codesetid)) {	
					String jobtitleCodeSetid = item.getCodesetid();
					if (item.isChangeBefore()){
						w0513Fld=fieldname+"_1";
					}
					else if (item.isChangeAfter()){
						w0515Fld=fieldname+"_2";
					}
				}// 申报职级
				else if("DL".equalsIgnoreCase(codesetid)){

					if (item.isChangeAfter()){// 变化后指标优先
						map.put("w0575", fieldname+"_2");
					}
					else {
						//如果有变化后指标，则不使用变化前指标w05 先查一下
						boolean bExists=false;
						Iterator it = map.entrySet().iterator();
						while (it.hasNext()) {
							Map.Entry entry = (Map.Entry) it.next();
							Object key = entry.getKey();
							if ("w0575".equals((String)key)){
								bExists=true;
								break;
							}
						}
						if (!bExists){//不存在变化后指标
							map.put("w0575", fieldname+"_1");
						}
					}
				}else {
					FieldItem w05Item = DataDictionary.getFieldItem(fieldname, "w05");
					if ("b0110".equalsIgnoreCase(fieldname)){
						w05Item = DataDictionary.getFieldItem("w0507", "w05");
					} 
					else if ("e0122".equalsIgnoreCase(fieldname)){
						w05Item = DataDictionary.getFieldItem("w0509", "w05");
					}  
					else if ("a0101".equalsIgnoreCase(fieldname)){
						w05Item = DataDictionary.getFieldItem("w0511", "w05");
					}  
					if (w05Item!=null && "1".equals(w05Item.getUseflag())){
						String w05Itemid=w05Item.getItemid();
						if (item.isChangeAfter()){// 变化后指标优先 
							map.put(w05Itemid, fieldname+"_2");
						}
						else {
							//如果有变化后指标，则不使用变化前指标w05 先查一下
							boolean bExists=false;
							Iterator it = map.entrySet().iterator();
							while (it.hasNext()) {
								Map.Entry entry = (Map.Entry) it.next();
								Object key = entry.getKey();
								if (w05Itemid.equals((String)key)){
									bExists=true;
									break;
								}
							}
							if (!bExists){//不存在变化后指标
								map.put(w05Itemid, fieldname+"_1");
							}
						}
					}
				}			
			}
			if (w0513Fld.length()>0){
				map.put("w0513", w0513Fld);
			}
			if (w0515Fld.length()>0){
				map.put("w0515", w0515Fld);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return map;
	}
	
	/** 
	* @Title: getChangeUpdateSQL 
	* @Description: 获取更新sql
	* @param @param srcTab 源表
	* @param @param w0513Fld  现聘职称
	* @param @param w0515Fld 申报职称
	* @param @param ins_id
	* @param @param w0301 会议id
	* @param @return
	* @return String
	*/ 
	private String getChangeUpdateSQL(String srcTab,String ins_id,String w0301,
			HashMap updateMap,String w0535addr, String w0537addr,String w0503,String w0505)
	{
		int db_type=Sql_switcher.searchDbServer();//数据库类型		
		String destTab="w05";
		StringBuffer strsql=new StringBuffer();
		if(db_type==2||db_type==3)
		{
		
			String srcUpdateFlds="";
			String destUpdateFlds="";
			Iterator it = updateMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String destFld = (String)entry.getKey();
				String srcFld = (String)entry.getValue();
				if (srcUpdateFlds.length()>0){
					srcUpdateFlds=srcUpdateFlds+",";
					destUpdateFlds=destUpdateFlds+",";
				}
				srcFld = srcTab+"."+srcFld;
				//srcFld=getSrcFld(destFld,srcFld,srcTab);
				srcUpdateFlds=srcUpdateFlds+srcFld;
				destUpdateFlds=destUpdateFlds + destTab+"."+destFld;
			}
			if (w0535addr.length()>0){
				if (srcUpdateFlds.length()>0){
					srcUpdateFlds=srcUpdateFlds+",";
					destUpdateFlds=destUpdateFlds+",";
				}
				destUpdateFlds=destUpdateFlds+ destTab+".w0535";
				srcUpdateFlds=srcUpdateFlds+"'"+w0535addr+"'";
			}
			if (w0537addr.length()>0){
				if (srcUpdateFlds.length()>0){
					srcUpdateFlds=srcUpdateFlds+",";
					destUpdateFlds=destUpdateFlds+",";
				}
				destUpdateFlds=destUpdateFlds+ destTab+".w0537";
				srcUpdateFlds=srcUpdateFlds+"'"+w0537addr+"'";
			}
			
			strsql.append("update w05 set  (");
			strsql.append(destUpdateFlds+")=(select "+srcUpdateFlds);
			strsql.append(" from ");
			strsql.append(srcTab);
			strsql.append(" where ");
			strsql.append("upper(basepre)=upper("+destTab+".w0503)");
			strsql.append(" and a0100="+destTab+".w0505");
			strsql.append(" and  ins_id="+ins_id);
			strsql.append(") where w0301 ='"+w0301+"'");;
			strsql.append(" and  w0503 ='"+w0503+"'");
			strsql.append(" and  w0505 ='"+w0505+"'");
		}
		else
		{
			String strupdate="";
			Iterator it = updateMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String destFld = (String)entry.getKey();
				String srcFld = (String)entry.getValue();
				//srcFld=getSrcFld(destFld,srcFld,srcTab);
				srcFld = srcTab+"."+srcFld;
				if (strupdate.length()>0){
					strupdate=strupdate+",";
				}
				strupdate =strupdate+destTab+"."+destFld+"="+srcFld;
			}			
			if (w0535addr.length()>0){
				if (strupdate.length()>0){
					strupdate=strupdate+",";
				}
				strupdate =strupdate+destTab+".w0535"+"="+"'"+w0535addr+"'";
			}
			if (w0537addr.length()>0){
				if (strupdate.length()>0){
					strupdate=strupdate+",";
				}
				strupdate =strupdate+destTab+".w0537"+"="+"'"+w0537addr+"'";
			}
			
			strsql.append("update w05 set ");
			strsql.append(strupdate);
			strsql.append(" from w05 ");
			strsql.append(" left join ");
			strsql.append(srcTab);
			strsql.append(" on ");
			strsql.append("w05.w0503="+srcTab+".basepre");
			strsql.append(" and w05.w0505="+srcTab+".a0100");
			strsql.append(" where ");
			strsql.append("w05.w0301 ='"+w0301+"'");
            strsql.append(" and  w05.w0503 ='"+w0503+"'");
            strsql.append(" and  w05.w0505 ='"+w0505+"'");
			strsql.append(" and "+srcTab+".ins_id=");
			strsql.append(ins_id);
			
		} 
		return strsql.toString();
	}
	
	/**
	 * 基于最新需求的优化：
	 * 校级评审会议在创建时仅需考虑启用学科组、高评委评审两个阶段，
	 * 但在上会材料处要参考申报人在院级评审会议中产生的二级单位评议组、同行专家投票结果数据，
	 * 程序在此处特殊处理，当会议启用了高评委即使没有选择“二级单位评议”、“同行专家”投票阶段，
	 * 在上会材料处仍显示相关环节的投票情况，此处的投票数据在上会时引自申报人同年
	 * 其它已结束的评审会议启用了该阶段的数据。
	 * @throws GeneralException 
	 */
	private void syncW05Data(String newW0301,List<Map<String,String>> personList) throws GeneralException {
		//获得当前年度
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		RowSet rs2 = null;
		try {
			if(personList.size()==0)
				return;
			//得到要更新的列，list的最后一列为导入标识（将同步标识用导入标识表示）
			List<String> fields = this.needSyncModifyFields(newW0301);
			StringBuffer sql = new StringBuffer();
			//查询 最新上会 的人员编号
			List<String> values = new ArrayList<String>();	
			List<List> updateList = new ArrayList<List>();
			for(Map<String,String> map:personList){
				String w0505 = map.get("w0505");
				String w0503 = map.get("w0503");
				
				values.clear();
				sql.setLength(0);
				//查询需要更新的申报人的w0501
				sql.append("select w0501 from w05 where w0505 = ? and w0503= ? and w0301=?");
				values.add(w0505);
				values.add(w0503);
				values.add(newW0301);
				rs = dao.search(sql.toString(),values);
				
				//根据人员编号 查询当前年度上会记录  用以同步最新上会数据
				if(rs.next()){
					String w0501 = rs.getString("w0501");
					sql.setLength(0);
					sql.append("select W05.* from W05,W03 where w0505 = ? and w0503= ? and ");
					sql.append(Sql_switcher.diffYears("w05.create_time",Sql_switcher.today())+"=0 ");//本年度
					sql.append("and w05.W0301=w03.W0301 ");
					sql.append("and W05.w0301<>? and W03.W0321='06' ");
					sql.append("order by W05.create_time desc");
					
					values.clear();
					values.add(w0505);
					values.add(w0503);
					values.add(newW0301);
					rs2 = dao.search(sql.toString(),values);
					
					List list = new ArrayList();
					if(rs2.next()){
						for(int i=0;i<fields.size();i++){
							if(i==fields.size()-1){
								list.add(fields.get(i));
							}else{
								String cName = fields.get(i);//列名
								if("w0561".equalsIgnoreCase(cName)
										|| "group_id".equals(cName))
									list.add(rs2.getString(cName));
								else
									list.add(rs2.getInt(cName));
							}
						}
						list.add(w0501);
						updateList.add(list);
					}
						
				}
			}
			//更新数据
			if(fields.size()>0){
				StringBuffer upSql = new StringBuffer("update W05 set ");
				for(int i=0;i<fields.size();i++){
					if(i==fields.size()-1){
						upSql.append("W0525=?");//导入标识
					}else{
						String cName = fields.get(i);//列名
						upSql.append(cName+"=?,");
					}
					
				}
				upSql.append(" where w0501=?");
				dao.batchUpdate(upSql.toString(), updateList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs2);
			PubFunc.closeDbObj(rs);
		}
	}
	/**
	 * 获得需要同步的字段
	 *  	W0517：评委人数 （总）<br />
	 *		W0521：学科组人数（总）<br />
	 *		W0523：外部专家人数（总）<br />
	 *		W0571：学院任聘组人数（总）<br />
     *
 	 *		W0531：外部专家赞成人数<br />
	 *		W0527：外部专家反对人数<br />
	 *		W0529：外部专家弃权人数<br />
	 *		
	 *		W0547：学科组赞成人数<br />
	 *		W0543：学科组反对人数<br />
	 *		W0545：学科组赞成人数<br />
	 *		
	 *		W0553：评委会赞成人数<br />
	 *		W0549：评委会反对人数<br />
	 *		W0551：评委会弃权人数<br />
	 *		
	 *		W0567：二级单位赞成人数<br />
	 *		W0563：二级单位反对人数<br />
	 *		W0565：二级单位弃权人数<br />
	 * @author haosl
	 * @throws GeneralException 
	 */
	private List<String> needSyncModifyFields(String w0301) throws GeneralException{
		List<String> fields = new ArrayList<String>();
		ContentDAO dao = new ContentDAO(this.conn);//
		RowSet rs = null;
		String[] sync = {"0","0","0","0"};
		List<Integer> list = new ArrayList<Integer>();//保存没有启用的评议组
		try {
			//二级单位 和高评委
			StringBuffer sql = new StringBuffer();
			sql.append("select w0315,w0323,w0325 from w03 where w0301 ='"+w0301+"'");
			rs = dao.search(sql.toString());
			if(rs.next()){
				int w0315 = rs.getInt("w0315");//高评委
				int w0323 = rs.getInt("w0323");//二级单位
				String w0325 = rs.getString("w0325");//w0325是否启用同行阶段 1 启用  ;null| 2不启用
				if(w0315==0){//没有启用高评委
					fields.add("W0517");
					fields.add("W0553");
					fields.add("W0549");
					fields.add("W0551");
					sync[2]="1";
				}if(w0323==0){//没有启用二级单位
					fields.add("W0561");
					fields.add("W0571");
					fields.add("W0567");
					fields.add("W0563");
					fields.add("W0565");
					sync[3]="1";
				}if(!StringUtils.equals(w0325, "1")){//没有启用同行评议组
					fields.add("W0523");
					fields.add("W0531");
					fields.add("W0527");
					fields.add("W0529");
					sync[0]="1";
				}
			}
			//学科组
			sql.setLength(0);
			sql.append("select distinct(group_id) from zc_expert_user where w0301 ='"+w0301+"' and type=2");
			rs.close();
			rs = dao.search(sql.toString());
			if(!rs.next()){//没有数据 证明没有启用学科组评议
				fields.add("group_id");
				fields.add("W0521");
				fields.add("W0547");
				fields.add("W0543");
				fields.add("W0545");
				sync[1]="1";
			}
			//将导入标识作为同步标识，该标识放到list的最后一列
			String syncTemp = StringUtils.join(sync);
			if(!"0000".equals(syncTemp))
				fields.add(StringUtils.join(sync));
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return fields;
	}
	
	/**
	 * 获得公示、投票环节显示申报材料表单上传的word模板内容参数设置 将文件路径存到W0536中
	 * @param tabid
	 * @param tmpTasklist
	 * @param module_id
	 * @param infor_type
	 * @param taskid
	 * @param w0301
	 * @throws GeneralException
	 */
	private void saveTemplateDataToW0536(String tabid, String insid, String module_id, String infor_type, String taskid, String w0301) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rst = null;
		try{
			ConstantXml constantXml = new ConstantXml(this.conn,"JOBTITLE_CONFIG");
	        String support_word= constantXml.getTextValue("/params/support_word");
	        constantXml = new ConstantXml(this.conn,"FILEPATH_PARAM");
	        String rootpath = constantXml.getNodeAttributeValue("/filepath", "rootpath");
	        rootpath=rootpath.replace("\\",File.separator);          
	    	if (!rootpath.endsWith(File.separator)) 
	    		rootpath =rootpath+File.separator;
	        if("true".equalsIgnoreCase(support_word)){
	        	ArrayList paramList = new ArrayList();
	        	RecordVo vo = new RecordVo("template_table");
				vo.setInt("tabid", Integer.parseInt(tabid));
				vo=dao.findByPrimaryKey(vo);
				String tabname=vo.getString("name");
	        	OutPutModelBo opmBo = new OutPutModelBo(this.conn,this.userview);
	        	String selfapply = "0";
	        	if("9".equals(module_id))
	        		selfapply = "1";
	        	
	        	ArrayList fileList = opmBo.outPutModel(insid, taskid, null, null, selfapply, infor_type, tabid, "1");
	        	for(int k=0;k<fileList.size();k++){
	        		ArrayList param = new ArrayList();
	        		HashMap filemap = (HashMap)fileList.get(k);
	        		Set keySet = filemap.keySet();
					Iterator iterator = keySet.iterator();
					String filepath_ = "";
	        		String objectid = "";
					while (iterator.hasNext()) {
						objectid = iterator.next().toString();
						filepath_ = filemap.get(objectid).toString();
					}
					//查找人员对应的W0501
					ArrayList searchparam = new ArrayList();
					StringBuffer sb = new StringBuffer("");
					String W0501 = "";
					sb.append("select w0501 from w05 where lower(w0503"+Sql_switcher.concat()+"w0505)=? and w0301=?");
					searchparam.add(objectid.toLowerCase());
					searchparam.add(w0301);
					rst = dao.search(sb.toString(), searchparam);
					if(rst.next()){
						W0501 = rst.getString("w0501");
					}
	        		//\ reviewMaterial \ meeting_会议ID\ 模板名称_W0501.pdf
	        		String filepath = rootpath+"reviewMaterial"+File.separator+"meeting_"+w0301+File.separator+tabname+"_"+W0501+".pdf";
	        		FileUtils.copyFile(new File(filepath_), new File(filepath));
	        		File oldfile = new File(filepath_);
		      		  if(oldfile.exists())
		      			oldfile.delete();
	        		RecordVo recordVo=new RecordVo("w05");
		        	recordVo.setString("w0501", W0501);
		        	recordVo.setString("w0536", SafeCode.encode(PubFunc.encrypt(filepath)));
		        	dao.updateValueObject(recordVo);
	        	}
	        }
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rst);
		}
	}
	
	/**
	 * 删除非启动中的,肯呢个是删除分组，或者删除个人，通过有误categories_id区别
	 * @param w0501
	 * @param w0301
	 * @return
	 * @throws GeneralException
	 */
	public int deleteMeetingPerson(String w0501, String w0301, String categories_id, String review_links) throws GeneralException {
		int flag = 0;
        ContentDAO dao=new ContentDAO(this.conn);
        String w0501Sql = "";
        boolean isHaveEva = false;
        ArrayList<String> w0501List = new ArrayList<String>();
        String sql = "";
		ArrayList<String> list = new ArrayList<String>();
        RowSet rs = null;
		try
		{
			/*if(StringUtils.isNotBlank(categories_id)) {
				if(!isCanRevokeCate(w0301,categories_id)) {
					return 0;
				}
				//w0501 = getAllW0501List(categories_id);//删除分组
			}else {
				if(!isCanRevoke(w0301, w0501)) {
					return 0;
				}
			}*/
			w0501List.add(w0301);
			//判断是否在上一个阶段还存在的人，如果存在就不能删除
			if(StringUtils.isNotBlank(categories_id)) {
				sql = "select w0501 from zc_categories_relations zcr where w0501 not in (select w0501 from zc_personnel_categories zpc "
						+ "where zpc.categories_id = zcr.categories_id and zpc.w0301=? and zpc.review_links <> ?) and zcr.categories_id=?";
				list.add(w0301);
				list.add(review_links);
				list.add(categories_id);
				rs = dao.search(sql, list);
				while(rs.next()) {
					isHaveEva = true;
					if(StringUtils.isBlank(w0501Sql)) {
						w0501Sql += " and (w0501 =? ";
					}else {
						w0501Sql += " or w0501 =? ";
					}
					w0501List.add(rs.getString("w0501"));
				}
				w0501Sql += ")";
				
				// 同时删除关联的人员
				sql = "delete from zc_categories_relations where categories_id=?";
				dao.delete(sql, Arrays.asList(new String[] {categories_id}));
			}else {
				w0501Sql += " and w0501 =? ";
				w0501List.add(w0501);
				//找出这个人在其他阶段还存在的记录
				sql ="select w0501 from zc_categories_relations zcr left join zc_personnel_categories zpc on zpc.categories_id = zcr.categories_id where zpc.w0301=? and zpc.review_links <> ? and zcr.w0501=?";
				list.add(w0301);
				list.add(review_links);
				list.add(w0501);
				rs = dao.search(sql, list);
				if(rs.next()) {
					isHaveEva = true;
				}
			}
			if(w0501List.size() > 1) {
				if(!isHaveEva) {//如果其他阶段有该人，则不删除w05，投票结果表对应的数据应该删除，否则数据冗余
					// 删除申请记录
					sql = "delete from w05 where w0301=? ";
					sql += w0501Sql;
					dao.delete(sql, w0501List);
				}
				
				ArrayList<String> list_temp = (ArrayList<String>) w0501List.clone();
				// 删除评审账号、密码
				sql = "delete from zc_expert_user where w0301=?";
				sql += w0501Sql + " and type=?";
				list_temp.add(review_links);
				dao.delete(sql, list_temp);
				
				//删除投票记录
				sql = "delete from zc_data_evaluation where w0301=?";
				sql += w0501Sql;
				if(StringUtils.isNotBlank(categories_id)) {//不用这些条件判断会导致删除多了
					sql += " and categories_id=?";
					w0501List.add(categories_id);
				}else {
					sql += " and categories_id in (select categories_id from zc_expert_user where w0301=? and type=?)";
					w0501List.add(w0301);
					w0501List.add(review_links);
				}
				dao.delete(sql,w0501List);
			}
			
		} catch(Exception e) {
			throw GeneralExceptionHandler.Handle(e);
			
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return 1;
	}
	
	/**
     * 判断申报人是否可以被撤销，评审中的不允许撤销
     * @param w0301
     * @param w0501
     * @return
     */
    private boolean isCanRevoke(String w0301,String w0501) throws GeneralException{
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	try {
    		StringBuffer sql = new StringBuffer();
        	sql.append("select * from zc_personnel_categories where categories_id in (select categories_id from zc_categories_relations where w0501=? ) and w0301=? and approval_state='1'");
        	List<String> values = new ArrayList<String>();
        	values.add(w0501);
        	values.add(w0301);
        	
        	rs = dao.search(sql.toString(),values);
        	if(rs.next())
        		return false;
        	return true;
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
    }
    
    /**
     * 判断申报人分组是否可以被撤销，评审中的不允许撤销
     * @param w0301
     * @param w0501
     * @return
     */
    private boolean isCanRevokeCate(String w0301,String categories_id) throws GeneralException{
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	try {
    		StringBuffer sql = new StringBuffer();
        	sql.append("select * from zc_personnel_categories where categories_id = ? and w0301=? and approval_state='1'");
        	List<String> values = new ArrayList<String>();
        	values.add(categories_id);
        	values.add(w0301);
        	
        	rs = dao.search(sql.toString(),values);
        	if(rs.next())
        		return false;
        	return true;
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
    }
    
    /**
     * 获取w0501的集合
     * @param categories_id
     * @return
     * @throws GeneralException 
     */
    private String getAllW0501List(String categories_id) throws GeneralException {
    	String w0501Concant = "";
    	ArrayList<String> listArray = new ArrayList<String>();
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	try {
    		String sql = "select w0501 from zc_categories_relations where categories_id=?";
    		listArray.add(categories_id);
    		rs = dao.search(sql,listArray);
    		while(rs.next()) {
    			if(StringUtils.isNotBlank(w0501Concant)) {
    				w0501Concant += ",";
    			}
    			w0501Concant += rs.getString("w0501");
    		}
    	}catch (Exception e) {
    		throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
    	return w0501Concant;
    }
    
    /**
     * 评委会和二级单位在w03中查询，学科组和同行专家在zc_personnel_categories的查询
     * @param w0301
     * @param review_links//1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段
     * @return
     * @throws GeneralException 
     */
   public HashMap<String,Integer> getCountPerson(String w0301, String review_links) throws GeneralException {
    	ArrayList<String> list = new ArrayList<String>();
    	HashMap<String,Integer> map = new HashMap<String,Integer>();
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	String categories_id = "";
    	String sql = "";
    	try {
    		if("1".equals(review_links)) {//评委会
    			sql = "select W0315 as expertnum,categories_id from w03 w,zc_personnel_categories zc where w.w0301 = zc.w0301 and w.w0301 = ? and zc.review_links = ?";
    		}else if("4".equals(review_links)) {//二级单位
    			sql = "select W0323 as expertnum,categories_id from w03 w,zc_personnel_categories zc where w.w0301 = zc.w0301 and w.w0301 = ? and zc.review_links = ?";
    		}else {
    			sql = "select expertnum,categories_id from zc_personnel_categories where w0301 = ? and review_links = ? and group_id is null";
    		}
	    	list.add(w0301);
	    	list.add(review_links);
	    	rs = dao.search(sql,list);
	    	while(rs.next()) {
	    		categories_id = rs.getString("categories_id");
	    		map.put(PubFunc.encrypt(categories_id), rs.getInt("expertnum"));
	    	}
    	}catch (Exception e) {
    		throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
    	return map;
    }
   	
   /**
    * 
    * @param evaluationType//1:投票  2：评分
    * @param srbo
    * @param idlist
    * @param w0301
    * @param review_links//1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段 5、论文送审
    * @param userType//1：随机账号，2：非随机，选人的
    * @param categories_id_e
    * @param jobtitleConfigBo
    * @param operateType //1：启动，2：重启
    * @return
    */
    public String startReview(String evaluationType,StartReviewBo srbo,ArrayList<MorphDynaBean> idlist,String w0301, String review_links,String categories_id_e,
    		JobtitleConfigBo jobtitleConfigBo,String userType, int operateType) {
    	String msg="";//消息
	    try {
	    	ContentDAO dao = new ContentDAO(this.conn);
	        ArrayList<HashMap<String, String>> allList = srbo.getSelectList(idlist, this.userview);//实际选中的数据
	    	if(allList.size()>0){
	    		w0301 = allList.get(0).get("w0301");
	    	} 
        	/*if(!StringUtils.isEmpty(w0301)){
	    		//评委会、同行专家、二级单位 校验
    			for(int i=0;i<allList.size();i++){
    				String w0501 = allList.get(i).get("w0501");
    				String group_id = allList.get(i).get("group_id");
	    			int experNum = srbo.getExperAccountNum(w0301,Integer.parseInt(review_links),2,group_id,w0501);
	    			if(experNum==0){//提示未生成帐号不能审查材料
	    				if("1".equals(review_links))//评委会
	    					msg="请为选中申报人的 【"+JobtitleUtil.ZC_REVIEWFILE_STEP1SHOWTEXT+"】生成投票帐号！";
	    				else if("2".equals(review_links))//学科组 校验
	    					msg="请为选中申报人的【"+JobtitleUtil.ZC_REVIEWFILE_STEP2SHOWTEXT+"】生成投票帐号！";
	    				else if("3".equals(review_links))//同行专家
	    					msg="请为选中申报人的【"+JobtitleUtil.ZC_REVIEWFILE_STEP3SHOWTEXT+"】生成投票帐号！";
	    				else //二级单位 
	    					msg="请为选中申报人的【"+JobtitleUtil.ZC_REVIEWFILE_STEP4SHOWTEXT+"】生成投票帐号！";
        				return msg;
	    			}
    			}
        	}*/
	    	//清除投票结果
	    	srbo.clearData(allList,review_links, operateType);
	    	//更新w0555和w0573=2,清除评审结果（zc_data_evaluation）
	    	srbo.updateW0555W0573(allList, Integer.parseInt(review_links),2,categories_id_e);
	    	if(operateType == 2) {
	        	//重启清空问卷调查表对应的数据
        		srbo.clearQndata(allList, w0301, PubFunc.decrypt(categories_id_e), review_links);
        	}
        	
        	if(StringUtils.isNotEmpty(categories_id_e)) {//差额投票时，更新申报人员分类表（zc_personnel_categories）中投票状态（approval_state）为已启动（1）
        		String categories_id = PubFunc.decrypt(categories_id_e);
				this.updateApproval_state(w0301,review_links,userType,categories_id, "1");
        		//启动打分
				if("2".equals(evaluationType)) {
					ArrayList<String> templates = srbo.getKh_Template_Ids(w0301,review_links);
					String Relation_id = "1_" + w0301 +"_"+ review_links;//考核计划标识职称评审格式设置为模块ID_评审会议ID_环节ID
					if(operateType == 2) {//重启重置打分状态
						srbo.reStartScore(w0301,review_links,categories_id,null);
					}
					//添加的规则是1.考核对象表：一个考核对象对应一个模板2.考核主题表：一个考核对象对应一个考核主体对应一个考核模板
					ArrayList<String> list=new ArrayList<String>();
					list.add(categories_id);
					srbo.doInsertKhObjectTable(templates,list,Relation_id);//申报人添加到kh_object考核对象表
					srbo.doInsertKhMainbodyTable(list,Relation_id);//评审人添加到kh_mainbody考核主体表
				}
        	}
        	int usetype = Integer.parseInt(evaluationType) +1;
        	//生成账号密码
			OutPositionalStaffBo opsb=new OutPositionalStaffBo(this.conn, this.userview,w0301,Integer.parseInt(review_links),usetype);
			ArrayList outExcelList=opsb.getOutExcelList(w0301,Integer.parseInt(review_links),usetype,categories_id_e);	
	    	msg="启动成功！";
	    	
	    	ReviewMeetingPortalBo rmpbo = new ReviewMeetingPortalBo(this.userview, this.conn);
			RecordVo vo = new RecordVo("w03");
			vo.setString("w0301", w0301);
			vo = dao.findByPrimaryKey(vo);
			List<LazyDynaBean> beans = rmpbo.getXmlParamByW03(vo.getString("extend_param"));
			String vote_default = "";//1 通过率按2/3控制 =2 则不控制
			for(LazyDynaBean bean : beans) {
				//1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段 (review_links)
				if(StringUtils.equalsIgnoreCase((String) bean.get("flag"), review_links) && 
						StringUtils.equalsIgnoreCase((String) bean.get("evaluation_type"), "1")) {//=1 投票 =2 评分
					vote_default = (String) bean.get("vote_default");
				}
			}
			//if(StringUtils.isNotBlank(vote_default) && !"0".equals(vote_default))
				putPersonToEvaluation(w0301, vote_default, PubFunc.decrypt(categories_id_e));
	    }catch (Exception e) {
	    	e.printStackTrace();
		}
	    return msg;
    }
    
    /**
     * 启动的时候，更新默认状态
     * @param w0301
     * @param vote_default 默认赞成反对弃权
     * @param categories_id
     * @throws GeneralException
     */
    public void putPersonToEvaluation(String w0301, String vote_default, String categories_id) throws GeneralException {
		StringBuffer sql = new StringBuffer();
		ArrayList list = new ArrayList();
		try {
			//opinion:xxxxxx，标识默认赞成，反对，弃权这种，如果修改了opinion：''，在重启，暂时修改了默认状态后就能正确修改没改动过的数据
			ContentDAO dao=new ContentDAO(this.conn);
			if(StringUtils.isBlank(vote_default) || "0".equals(vote_default)) {
				sql.append("delete from zc_data_evaluation where w0301=? and categories_id=? and ");
				if(Sql_switcher.searchDbServer() == 2) {//oracle
					sql.append(" to_char(zc_data_evaluation.opinion)=?");
				}else {
					sql.append(" convert(varchar(1000),zc_data_evaluation.opinion)=?");
				}
				list.add(w0301);
				list.add(categories_id);//W0301
				list.add("xxxxxx");
				dao.update(sql.toString(), list);
			}else {
				sql.append("update zc_data_evaluation set approval_state = ? where w0301=? and categories_id=? and ");
				if(Sql_switcher.searchDbServer() == 2) {//oracle
					sql.append(" to_char(zc_data_evaluation.opinion)=?");
				}else {
					sql.append(" convert(varchar(1000),zc_data_evaluation.opinion)=?");
				}
				list.add(vote_default);//W0301
				list.add(w0301);
				list.add(categories_id);//W0301
				list.add("xxxxxx");
				dao.update(sql.toString(), list);
				
				sql.setLength(0);
				list = new ArrayList();
				sql.append("insert into  zc_data_evaluation(W0501, W0301, username, W0101, expert_state, approval_state, opinion, categories_id) ");
				sql.append(" select w0501,w0301,username,null,'','"+vote_default+"','xxxxxx',categories_id from zc_expert_user where w0301=? and categories_id=? and w0501<>'xxxxxx' and usetype=2 ");
				sql.append(" and w0501 not in (select w0501 from zc_data_evaluation where w0301=? and categories_id=? and zc_expert_user.username = zc_data_evaluation.username) ");
				//approval_state1：同意  2：不同意  3：弃权
				list.add(w0301);//W0301
				list.add(categories_id);
				list.add(w0301);//W0301
				list.add(categories_id);
				dao.update(sql.toString(), list);
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
    /**
     * 保存通过率参数
     * @param w0301
     * @param review_links
     * @throws GeneralException 
     */
	public void saveRateControl(String w0301, String review_links,String rate_control) throws GeneralException {
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			ReviewMeetingPortalBo rmpb = new ReviewMeetingPortalBo(userview, conn);
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("rate_control", rate_control);
			rmpb.updateSegmentAttr(w0301, review_links, map);
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			
		}
	}
    /**
     * 获取会议名称
     * @param w0301
     * @return
     */
    public String meettingName(String w0301) {
    	String name = "";
    	RowSet rs = null;
    	ArrayList<String> list = new ArrayList<String>();
    	ContentDAO dao = new ContentDAO(this.conn);
    	try {
    		list.add(w0301);
    		String sql = "select w0303 from w03 where w0301=?";
    		rs = dao.search(sql,list);
    		if(rs.next()) {
    			name = rs.getString("w0303");
    		}
    	}catch (Exception e) {
    		e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
    	return name;
    }
    
    /**
     * 获取不包含结束状态的 分组ID-name map
     * @param w0301
     * @param review_links
     * @return
     */
    public ArrayList<String> getCateIdNameNotEnd(String w0301,String review_links,String exportType,String userType) {
    	ArrayList<String> listFinally = new ArrayList<String>();
    	ArrayList<String> list = new ArrayList<String>();
    	RowSet rs = null;
    	ContentDAO dao = new ContentDAO(this.conn);
    	StringBuffer sql = new StringBuffer();
    	try {
    		String andSql="";
    		if("1".equals(exportType)){
    			andSql=" and approval_state=0 ";
    		}else {
    			andSql=" and nullif(approval_state, '') is not null";
    		}
    		list.add(w0301);
    		list.add(review_links);
    		list.add("2");
    		sql.append("select categories_id,name from zc_personnel_categories where w0301=? and review_links=? and "+Sql_switcher.isnull("approval_state", "''")+" <> ? and nullif(name, '') is not null "+andSql);
    		if("2".equals(review_links)) {//学科组的时候得加这个判断
				if("1".equals(userType)) {
					sql.append(" and group_id is null");//因为新建分组的时候会将其置为''所以这里只进行is null的判断
				}else {
					sql.append(" and group_id is not null");
				}
			}
    		//增加分组项排序
    		sql.append(" order by seq");
    		rs = dao.search(sql.toString(),list);
    		while(rs.next()) {
    			listFinally.add(PubFunc.encrypt(rs.getString("categories_id"))+"_"+rs.getString("name"));
    		}
    	}catch (Exception e) {
    		e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
    	return listFinally;
    }
    
    /**
     * 获取分组id和分组名称的map集合，材料公示需要
     * @param w0301
     * @return
     */
    public ArrayList<String> getCateIdName(String w0301,String review_links,String userType) {
    	ArrayList<String> listFinally = new ArrayList<String>();
    	ArrayList<String> list = new ArrayList<String>();
    	RowSet rs = null;
    	ContentDAO dao = new ContentDAO(this.conn);
    	StringBuffer sql = new StringBuffer();
    	try {
    		list.add(w0301);
    		list.add(review_links);
    		sql.append("select categories_id,name from zc_personnel_categories where w0301=? and review_links=? and nullif(approval_state, '') is not null");
    		if("2".equals(review_links)) {//学科组的时候得加这个判断
				if("1".equals(userType)) {
					sql.append(" and group_id is null");//因为新建分组的时候会将其置为''所以这里只进行is null的判断
				}else {
					sql.append(" and group_id is not null");
				}
			}
    		sql.append(" order by seq");
    		rs = dao.search(sql.toString(),list);
    		while(rs.next()) {
    			listFinally.add(PubFunc.encrypt(rs.getString("categories_id"))+"_"+(rs.getString("name")==null?"":rs.getString("name")));
    		}
    	}catch (Exception e) {
    		e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
    	return listFinally;
    }
    
    /**
     * 获取材料公式需要显示的指标
     * @param w0301
     * @return
     */
    public LinkedHashMap<String,String> getW05Item(String w0301,String reviewLinks,String evaluationType) {
    	LinkedHashMap<String,String> map = new LinkedHashMap<String,String>(); 
    	//排除指标：评审环节、评审状态、评审材料、送审材料、二级单位评议组、已评数/总数、赞成人数、反对人数、弃权人数、赞成人数占比、状态、
  		//同行专家、基本达到人数、未达到人数、已达到人数、赞成人数占比、状态、学科组已评数/总数、赞成人数、反对人数、弃权人数、赞成人数占比、状态、参会人数、
  		//已评数/总数、赞成人数、反对人数、弃权人数、赞成人数占比、状态、评价表、同行专家评价表,评审材料访问地址、送审论文材料访问地址
    	String exceptItems = ",w0555,w0573,w0535_,w0537_,committeename,w0571,w0567,w0563,w0565,collegeagree,w0569" + 
    			",checkproficient,w0527,w0529,w0531,w0533,proficientagree,group_id,w0521,w0523,w0547,w0543,w0545,subjectsagree" + 
    			",w0557,w0519,w0517,w0553,w0549,w0551,committeeagree,w0559,w0539,w0541,w0535,w0537,w0561,w0536,";
    	//如果勾选了公示、投票环节显示申报材料表单上传的word模板内容，详情就是word模板，这里就不需要再次显示word模板指标了
    	try {
    		// 业务字典指标
    		ArrayList fieldList = DataDictionary.getFieldList("W05", 1);
    		ReviewScorecountBo bo = new ReviewScorecountBo(conn, userview, w0301, reviewLinks);
    		String template = "";
    		if("2".equals(evaluationType)) {
    			template = bo.getReviewMeetingTemplateids();
    		}
    		for (int i = 0; i < fieldList.size(); i++) {
    			FieldItem fi = (FieldItem) fieldList.get(i);
    			// 去除没有启用的指标
    			if (!"1".equals(fi.getUseflag())) {
    				continue;
    			}
    			// 去除隐藏的指标
    			if (!"1".equals(fi.getState())) {
    				continue;
    			}
    			// 去除不需要的指标
    			if (exceptItems.indexOf("," + fi.getItemid().toLowerCase() + ",") != -1) {
    				continue;
    			}
    			//只显示当前阶段相关的评分列和排名
    			if(fi.getItemid().toLowerCase().startsWith("c_") || fi.getItemid().toLowerCase().endsWith("_seq")) {
    				if(!"2".equals(evaluationType)) {
        				continue;
        			}else {
        				if(StringUtils.isEmpty(template) || !template.contains(fi.getItemid().toUpperCase().replaceFirst("C_", "").replace("_SEQ", ""))){
        					continue;
        				}
        			}
    			}
    			map.put(fi.getItemid().toLowerCase(), fi.getItemdesc());
    		}
    	}catch (Exception e) {
    		e.printStackTrace();
		}
    	return map;
    }
    
    /**
     * 获取材料公式需要显示的指标
     * @param w0301
     * @return
     */
    private HashMap getW05ItemString() {
    	StringBuffer item = new StringBuffer();
    	HashMap map = new HashMap();
    	ArrayList<String> list = new ArrayList<String>();
    	//排除指标：评审环节、评审状态、评审材料、送审材料、二级单位评议组、已评数/总数、赞成人数、反对人数、弃权人数、赞成人数占比、状态、
  		//同行专家、基本达到人数、未达到人数、已达到人数、赞成人数占比、状态、学科组已评数/总数、赞成人数、反对人数、弃权人数、赞成人数占比、状态、参会人数、
  		//已评数/总数、赞成人数、反对人数、弃权人数、赞成人数占比、状态、评价表、同行专家评价表
    	String exceptItems = ",w0555,w0573,w0535_,w0537_,committeename,w0571,w0567,w0563,w0565,collegeagree,w0569" + 
    			",checkproficient,w0527,w0529,w0531,w0533,proficientagree,group_id,w0521,w0523,w0547,w0543,w0545,subjectsagree" + 
    			",w0557,w0519,w0517,w0553,w0549,w0551,committeeagree,w0559,w0539,w0541,";
    	String exceptItem = ",w0501,w0503,w0505,w0511,,w0507,w0509,w0513,w0515,";
    	try {
    		// 业务字典指标
    		ArrayList fieldList = DataDictionary.getFieldList("W05", 1);
    		
    		for (int i = 0; i < fieldList.size(); i++) {
    			FieldItem fi = (FieldItem) fieldList.get(i);
    			// 去除没有启用的指标
    			if (!"1".equals(fi.getUseflag())) {
    				continue;
    			}
    			// 去除不需要的指标
    			if (exceptItems.indexOf("," + fi.getItemid().toLowerCase() + ",") != -1) {
    				continue;
    			}
    			// 去除不需要的指标
    			if (exceptItems.indexOf("," + fi.getItemid().toLowerCase() + ",") != -1) {
    				continue;
    			}
    			// 去除不需要的指标
    			if (exceptItem.indexOf("," + fi.getItemid().toLowerCase() + ",") == -1) {
    				list.add(fi.getItemid().toLowerCase());
    			}
    			item.append(",B.").append(fi.getItemid().toLowerCase());
    		}
    		map.put("stringItem", item);
    		map.put("arrayItem", list);
    	}catch (Exception e) {
    		e.printStackTrace();
		}
    	return map;
    }
    
    /**
     * 更新随机数
     * @param categories_id
     * @param value
     * @return
     * @throws GeneralException
     */
    public int updateCategorie(String categories_id,String value) throws GeneralException{
		
		int errorcode = 1;
		
		ContentDAO dao = new ContentDAO(this.conn);
		try {
				
			RecordVo vo = new RecordVo("zc_personnel_categories");
			vo.setString("categories_id", categories_id);
			vo = dao.findByPrimaryKey(vo);
			
			vo.setInt("expertnum", Integer.parseInt(value));
			int result = dao.updateValueObject(vo);
			if(result == 1) {
				errorcode = 0;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
		
		return errorcode;
	}
    /**
	 * 更新投票状态
	 * @param categories_id
	 * @param approval_state
	 * @param review_links//1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段 
	 * @param userType//1：随机账号，2：非随机，选人的
	 * @return
	 * @throws GeneralException
	 */
	private int getExpertNum(String w0301,String review_links,String userType,String categories_id) throws GeneralException{
		
		String sql = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		int expertnum=0;
		try {
			
			//对于二级单位和评委会的需要从w03表中去找人数
			if("1".equals(review_links)) {
				if("1".equals(userType)) {
					sql = "select W0315 as expertnum from w03 where w0301=?";
				}else {
					sql = "select count(1) as expertnum from zc_judgingpanel_experts where committee_id = (select committee_id from w03 where w0301=?) "
							+ "and ("+ Sql_switcher.today() +" between start_date and end_date or end_date is null)  and flag=1";
				}
				rs = dao.search(sql, Arrays.asList(new String[] {w0301}));
				while(rs.next()) {
					expertnum = rs.getInt("expertnum");
				}
			}else if("4".equals(review_links)) {
				if("1".equals(userType)) {
					sql = "select W0323 as expertnum from w03 where w0301=?";
				}else {
					sql = "select count(1) as expertnum from zc_judgingpanel_experts where committee_id = (select sub_committee_id from w03 where w0301=?) "
							+ "and ("+ Sql_switcher.today() +" between start_date and end_date or end_date is null)  and flag=1";
				}
				rs = dao.search(sql, Arrays.asList(new String[] {w0301}));
				while(rs.next()) {
					expertnum = rs.getInt("expertnum");
				}
			}else if("3".equals(review_links)) {
				sql = "select  expertnum from zc_personnel_categories where w0301=? and review_links=?";
				rs = dao.search(sql, Arrays.asList(new String[] {w0301,review_links}));
				if(rs.next()) {
					expertnum = rs.getInt("expertnum");
				}
			}else {
				if("2".equals(userType)) {
					sql = "select count(1) as expertnum from zc_expert_user where categories_id=?";
					rs = dao.search(sql, Arrays.asList(new String[] {categories_id}));
					while(rs.next()) {
						expertnum = rs.getInt("expertnum");
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return expertnum;
	}
	
	/**
	 * 每选择一次评审人的时候更新一下账号表的group_id
	 * @param group_id
	 * @param categories_id
	 * @throws GeneralException
	 */
	public void updateZcExpertUser(String count,String group_id, String categories_id) throws GeneralException {
		String sql = "";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			sql = "update zc_personnel_categories set group_id=?,expertnum=? where categories_id=?";
			dao.update(sql, Arrays.asList(new String[] {StringUtils.isNotBlank(group_id)?group_id:" ",count,categories_id}));
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
		
	}
	
	/**
	 * 定时获取已投票的，每30秒。更新人员的投票数，进度
	 * <w0501,categoriesid_submitnum_expertnum_expertAlreadyCount>
	 * @param w0301
	 * @param review_links
	 * @param evaluationType//1:投票  2：评分
	 * @return
	 * @throws GeneralException
	 */
	public HashMap<String,String> getTimeData(String w0301,String review_links,String evaluationType) throws GeneralException {
		String sql = "";
		String submitnum = "";
		String expertnum = "";
		String expert_already_count = "";
		ArrayList<String> list = new ArrayList<String>();
		HashMap<String,String> map = new HashMap<String,String>();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			if("1".equals(evaluationType)) {
				//只有赞成的，分组是启动状态的，并且是结束的进行定时获取
				sql = "select max(zpc.categories_id) as categories_id,zcr.w0501,MAX(zpc.submitnum) as submitnum,MAX(zpc.expertnum) as expertnum,"
						+ "sum(case when zde.approval_state='1' then 1 else 0 end) as expert_already_count from"
						+ " zc_categories_relations zcr left join zc_personnel_categories zpc on zcr.categories_id = zpc.categories_id left join  zc_data_evaluation zde on zde.w0501 = zcr.w0501 "
						+ "where zpc.w0301 = ? and zpc.review_links = ? and zde.username in (select username from zc_expert_user where w0301=?  and type=? and state=? "
						+ "group by username)  and (zde.approval_state=? or zde.approval_state=? or zde.approval_state=?) and zpc.approval_state=?"
						+ " and zde.expert_state=? group by zcr.w0501";
				list.add(w0301);
				list.add(review_links);
				list.add(w0301);
				list.add(review_links);
				list.add("1");
				list.add("1");
				list.add("2");
				list.add("3");
				list.add("1");
				list.add("3");
			}else {
				sql = "select categories_id as w0501,categories_id,max(submitnum) as submitnum,max(expertnum) as expertnum,0 as expert_already_count from zc_personnel_categories where w0301=? and review_links=? group by categories_id";
				list.add(w0301);
				list.add(review_links);
			}
			rs = dao.search(sql,list);
			
			while(rs.next()) {
				submitnum = StringUtils.isBlank(rs.getString("submitnum"))?"0":rs.getString("submitnum");
				expertnum = StringUtils.isBlank(rs.getString("expertnum"))?"0":rs.getString("expertnum");
				expert_already_count = StringUtils.isBlank(rs.getString("expert_already_count"))?"0":rs.getString("expert_already_count");
				map.put(PubFunc.encrypt(rs.getString("w0501")), PubFunc.encrypt(rs.getString("categories_id"))+"_"+submitnum+"_"+expertnum+"_"+expert_already_count);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return map;
	}
	
	/**
	 * @return
	 * @throws GeneralException
	 */
	public void setExpertNum(String w0301,String reviewLinks) throws GeneralException {
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		HashMap<String, String> map = new HashMap<String, String>();
		ArrayList<String> list = new ArrayList<String>();
		String group_id = "";
    	try {
    		String sql = "select group_id from zc_personnel_categories where w0301=? and review_links=?";
    		list.add(w0301);
    		list.add(reviewLinks);
    		rs = dao.search(sql,list);
    		while(rs.next()){
    			group_id = rs.getString("group_id");
    			dao.update("update zc_personnel_categories set expertnum=(select count(1) as expertnum from zc_expert_user where w0301=? and review_links=? and group_id=? and w0501=?) where review_links=? and w0301=? and group_id=?",Arrays.asList(new String[] {w0301,reviewLinks,group_id,"xxxxxx",reviewLinks,w0301,group_id}));
    		}
    	}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeDbObj(rs);
		}
	}
	
	/**
	 * 将状态置为已结束
	 * @param categories_id
	 */
	public void setUpToEnd(String categories_id) {
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList<String> list = new ArrayList<String>();
		try {
			String sql = "update zc_personnel_categories set approval_state=? where categories_id=?";
			list.add("2");
			list.add(categories_id);
			dao.update(sql,list);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 结束前校验是否存在未评审的数据，存在则提示
	 * @param evaluationType	=1 票数统计 ；	=2 分数统计
	 * @return
	 */
	public String checkToEnd(String evaluationType,String w0301,String categories_id,String reviewLinks) throws GeneralException {
		String msg = "";
		
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(conn);
		try {
			StringBuffer sql = new StringBuffer("");
			//同步一下w05表的票数
			ReviewScorecountBo reviewScorecountBo = new ReviewScorecountBo(this.conn, this.userview, w0301, reviewLinks);
			reviewScorecountBo.asyncPersonNum(w0301, Integer.parseInt(reviewLinks));
			// =1 票数统计 归档
			
			List<String> values = new ArrayList<String>();
			if("1".equals(evaluationType)) {
				sql.append("select W0511,W0303 from W05 left join W03 on w05.W0301 = w03.W0301");
				sql.append(" inner join zc_categories_relations zcr on zcr.w0501=W05.w0501 inner join zc_personnel_categories zpc on zcr.categories_id=zpc.categories_id");
				sql.append(" where W03.W0301=?");
				sql.append(" and W05.W0555=?");
				sql.append(" and zpc.review_links=?");
				sql.append(" and zpc.categories_id=? and ");
				// 评委会
				if("1".equals(reviewLinks))
					sql.append("(W0549+W0551+W0553)<zpc.expertnum"); 
				// 学科组
				else if("2".equals(reviewLinks))
					sql.append("(W0543+W0545+W0547)<zpc.expertnum"); 
				// 同行专家
				else if("3".equals(reviewLinks))
					sql.append("(W0527+W0529+W0531)<zpc.expertnum"); 
				// 二级单位
				else if("4".equals(reviewLinks))
					sql.append("(W0563+W0565+W0567)<zpc.expertnum");
				values.add(w0301);
				values.add(reviewLinks);
				values.add(reviewLinks);
				values.add(categories_id);
			}
			// =2 分数统计归档
			else if("2".equals(evaluationType)) {
				sql.append("select Objectname as W0511 from kh_object where id in (select kh_object_id from kh_mainbody where Mainbody_id in ");
				sql.append("(select username from zc_expert_user where w0301 =? and usetype = 3 and categories_id=? group by username)");
				sql.append(" and status <> 2 group by kh_object_id)");
				sql.append(" and Object_id in (select w0505 from W05 where W0501 in (select W0501 from zc_categories_relations where categories_id = ?)) group by Objectname");
				values.add(w0301);
				values.add(categories_id);
				values.add(categories_id);
			}
			rs = dao.search(sql.toString(),values);
			int i=0;
			while(rs.next()){
				i++;
				if(i<=5)
					msg +=rs.getString("W0511")+"、";
			}
			if(i>0) {
				msg = msg.substring(0, msg.length()-1);
				msg+=(i>5?"等"+i+"人":"")+"未完成评审，是否结束？";
			}
			// 37700 由于前台复写的alert方法在IE浏览器下显示不全，故这里如果有提示信息在外层套一个div用于显示
			if(StringUtils.isNotEmpty(msg))
				msg = "<div>" + msg + "</div>";
			return msg;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
	}
	/**
	 * 保存投票结果
	 * @param w0501
	 * @param updateColumns
	 * @param formHM
	 * @throws GeneralException
	 */
	public void saveVoteResult(String w0501, String updateColumns, HashMap formHM) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		String w0552 = "";
		try {
			DbWizard dbw = new DbWizard(this.conn);
			//w0552，各阶段状态是否修改过
			//W0533同行评议//W0557专业组//w0569二级单位//W0559评委会
			//0000:0未修改，1修改 
			if(!dbw.isExistField("w05","w0552", false)) {
				Table table=new Table("w05");
				Field field=new Field("w0552","状态修改标识");
				field.setDatatype(DataType.STRING);
				field.setLength(10);
				table.addField(field);	
				dbw.addColumns(table);
			}
			rs = dao.search("select w0552 from w05 where w0501 = ?", Arrays.asList(new String[] {w0501}));
			if(rs.next()) {
				w0552 = rs.getString("w0552");
				if(StringUtils.isBlank(w0552)) {
					w0552 = "0000";
				}
			}
			StringBuilder w0552_bf = new StringBuilder(w0552);
			List<String> values = new ArrayList<String>();
			StringBuffer sql = new StringBuffer();
			sql.append("update w05 set ");
			String[] columns = updateColumns.split(",");
			//W0533同行评议//W0557专业组//w0569二级单位//W0559评委会
			for(String c : columns) {
				String value = (String)formHM.get(c);
				sql.append(c+"=?,");
				values.add(value);//01：通过  02：未通过
				if(StringUtils.equalsIgnoreCase(c, "w0533")) {
					w0552_bf = w0552_bf.replace(0, 1, "1");
				}else if(StringUtils.equalsIgnoreCase(c, "W0557")) {
					w0552_bf = w0552_bf.replace(1, 2, "1");
				}else if(StringUtils.equalsIgnoreCase(c, "w0569")) {
					w0552_bf = w0552_bf.replace(2, 3, "1");
				}else if(StringUtils.equalsIgnoreCase(c, "W0559")) {
					w0552_bf = w0552_bf.replace(3, 4, "1");
				}
			}
			//更新状态
			sql.append("w0552=?,");
			values.add(w0552_bf.toString());
			
			sql.setLength(sql.length()-1);
			sql.append(" where w0501=?");
			values.add(w0501);
			dao.update(sql.toString(), values);
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
	}
	
	/**
	 * 如果没有排序，自动用过索引排序
	 * @param categories_id
	 * @throws GeneralException 
	 */
	public void sortSeq(String w0301, String review_links, String userType) throws GeneralException {
		RowSet rs = null;
		int seq = 0;
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer sqlWhere = new StringBuffer();
		ArrayList list = new ArrayList();
		try {
			sqlWhere.append(" where w0301=? and review_links=? and "+Sql_switcher.isnull("approval_state", "'a'")+" <> 'a'");
			list.add(w0301);
			list.add(review_links);
			if("2".equals(review_links)) {//学科组的时候得加这个判断
				if("1".equals(userType)) {
					sqlWhere.append(" and group_id is null");//因为新建分组的时候会将其置为''所以这里只进行is null的判断
				}else {
					sqlWhere.append(" and group_id is not null");
				}
			}
			rs = dao.search(Sql_switcher.sqlTop("select seq from zc_personnel_categories " + sqlWhere.toString() + " and seq is null",1), list);	
			if(rs.next()) {
				seq = rs.getInt("seq");
			}
			if(seq == 0) {
				if(Sql_switcher.searchDbServer() == 1) {//sqlserver
					dao.update("update zc_personnel_categories set seq =  row.rownum from zc_personnel_categories zc inner join (select ROW_NUMBER() over(order by seq,categories_id) rownum,"
							+ "categories_id from zc_personnel_categories b " + sqlWhere.toString() + ") row on zc.categories_id=row.categories_id", list);
				}else if(Sql_switcher.searchDbServer() == 2) {//oracle
					list.add(w0301);
					list.add(review_links);
					dao.update("update zc_personnel_categories zc set seq =  (select row1.rw from (select ROW_NUMBER() over(order by seq,categories_id) rw,"
							+ "categories_id from zc_personnel_categories b " + sqlWhere.toString() + ") row1 where zc.categories_id=row1.categories_id) " + sqlWhere.toString(), list);
				}
			}
			
			//组内人员如果没有排序，添加排序
			seq = 0;
			list = new ArrayList();
			list.add(w0301);
			list.add(review_links);
			ArrayList listCate_id = new ArrayList();
			String sql = "select categories_id from zc_personnel_categories zpc " + sqlWhere.toString();
			rs = dao.search("select seq,categories_id from zc_categories_relations where categories_id in (" + sql + ")", list);
			while(rs.next()) {
				seq = rs.getInt("seq");
				listCate_id.add(rs.getString("categories_id"));
			}
			if(seq == 0) {
				for(int i = 0; i < listCate_id.size(); i++) {
					list = new ArrayList();
					list.add(listCate_id.get(i));
					if(Sql_switcher.searchDbServer() == 1) {//sqlserver
						dao.update("update zc_categories_relations set seq = row.rownum from zc_categories_relations zcr inner join (select ROW_NUMBER() over(order by categories_id,queue,w0501) rownum,"
								+ "categories_id,w0501 from zc_categories_relations b where categories_id = ?) row on zcr.categories_id=row.categories_id and zcr.w0501 = row.w0501", list);
					}else if(Sql_switcher.searchDbServer() == 2) {//oracle
						list.add(listCate_id.get(i));
						dao.update("update zc_categories_relations zcr set seq =  (select row1.rw from (select ROW_NUMBER() over(order by categories_id,queue,w0501) rw,"
								+ "categories_id,w0501 from zc_categories_relations b where categories_id = ?) row1 where zcr.categories_id=row1.categories_id and zcr.w0501 = row.w0501) where categories_id = ?", list);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
	}
	
	/**
	 * 组之间的拖拽排序
	 * @param w0301
	 * @param review_links
	 * @param userType 
	 * @param ori_cateId 原始id
	 * @param ori_seq 最初的位置
	 * @param to_seq 移动到的位置
	 * @throws GeneralException
	 */
	public void sortCategories(String w0301, String review_links, String userType, String ori_cateId, int ori_seq, int to_seq) throws GeneralException {
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer sqlWhere = new StringBuffer();
		ArrayList list = new ArrayList();
		try {
			sqlWhere.append(" where w0301=? and review_links=?  and "+Sql_switcher.isnull("approval_state", "'a'")+" <> 'a'");
			list.add(w0301);
			list.add(review_links);
			if("2".equals(review_links)) {//学科组的时候得加这个判断
				if("1".equals(userType)) {
					sqlWhere.append(" and group_id is null");//因为新建分组的时候会将其置为''所以这里只进行is null的判断
				}else {
					sqlWhere.append(" and group_id is not null");
				}
			}
			if(ori_seq > to_seq) {//上移
				sqlWhere.append(" and seq < ? and seq >= ?");
				list.add(ori_seq);
				list.add(to_seq);
				dao.update("update zc_personnel_categories set seq = seq+1 " + sqlWhere.toString(), list);
				
			}else {//下移
				sqlWhere.append(" and seq > ? and seq <= ?");
				list.add(ori_seq);
				list.add(to_seq);
				dao.update("update zc_personnel_categories set seq = seq-1 " + sqlWhere.toString(), list);
			}
			list = new ArrayList();
			list.add(to_seq);
			list.add(PubFunc.decrypt(ori_cateId));
			dao.update("update zc_personnel_categories set seq = ? where categories_id=? ", list);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 人员排序
	 * @param ori_cateId
	 * @param w0501
	 * @param to_seq
	 */
	public void sortPerson(String ori_cateId, String w0501, int to_seq) {
		int ori_seq = 0;
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer sqlWhere = new StringBuffer();
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try {
			sqlWhere.append(" where categories_id=? and w0501=?");
			list.add(ori_cateId);
			list.add(w0501);
			rs = dao.search("select seq from zc_categories_relations " + sqlWhere.toString(), list);
			if(rs.next()) {
				ori_seq = rs.getInt("seq");
			}
			if(to_seq < ori_seq) {
				list = new ArrayList();
				list.add(ori_cateId);
				list.add(ori_seq);
				list.add(to_seq);
				dao.update("update zc_categories_relations set seq = seq+1 where categories_id=? and seq < ? and seq >= ?", list);
			}else {
				list = new ArrayList();
				list.add(ori_cateId);
				list.add(ori_seq);
				list.add(to_seq);
				dao.update("update zc_categories_relations set seq = seq-1 where categories_id=? and seq > ? and seq <= ?", list);
			}
			list = new ArrayList();
			list.add(to_seq);
			list.add(ori_cateId);
			list.add(w0501);
			dao.update("update zc_categories_relations set seq = ? where categories_id=? and w0501=?", list);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
	}
	
	/**
	 * 获取专业组，新增默认选择第一个，oracle添加，空的没法添加，一直添加为null，
	 * 随机账号和选择专业组就是根据null进行区别的
	 * @param w0301
	 * @param reviewLinks
	 * @return
	 * @throws GeneralException
	 */
	private String getGroupId(String w0301, String reviewLinks) throws GeneralException {
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		HashMap<String, String> map = new HashMap<String, String>();
		ArrayList<String> list = new ArrayList<String>();
		String group_name = "";
		String group_id = "";
		String count = "";
    	try {
    		String sql = Sql_switcher.sqlTop("select group_id from zc_expert_user where  W0301=? and type=? and w0501=? group by group_id",1);
    		list.add(w0301);
    		list.add(reviewLinks);
    		list.add("xxxxxx");
    		rs = dao.search(sql,list);
    		if(rs.next()){
    			group_id = rs.getString("group_id");
    		}
    	}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeDbObj(rs);
		}
    	return group_id;
	}
}
