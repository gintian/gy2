package com.hjsj.hrms.module.jobtitle.reviewfile.businessobject;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.module.jobtitle.reviewfile.transaction.ReviewFileTrans;
import com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject.ReviewMeetingBo;
import com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject.ReviewMeetingPortalBo;
import com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject.ReviewScorecountBo;
import com.hjsj.hrms.module.jobtitle.utils.JobtitleUtil;
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
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 资格评审_职称评审_上会材料
 * 
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 */
public class ReviewFileBo {

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
	@SuppressWarnings("unused")
	private UserView userview;

	/**
	 * 构造函数
	 * 
	 * @param conn
	 * @param userview
	 */
	public ReviewFileBo(Connection conn, UserView userview) {
		this.setConn(conn);
		this.setUserview(userview);
	}
	public ReviewFileBo(Connection conn) {
		this.setConn(conn);
	}

	/**
	 * 获取列头、表格渲染
	 * 
	 * 赞成人数（A），反对人数（B），弃权人数（	C），状态（D）
	 * ps:外部鉴定专家
	 * 1、ABCD一列都不显示，就不显示整个专家块，并且在启动评审阶段不能选择
	 * 2、只要有ABCD其一，就显示复合表头、【鉴定专家】和有的列
	 * 3、只要有A，就显示复合表头、【鉴定专家】和【赞成人数占比】列
	 * 
	 * @param exceptFields
	 *            不需要指标
	 * @param notEditFields
	 *            不可编辑指标
	 * @param isAddWidth
	 *            需要增加列宽的指标
	 * @param isAddWidth
	 *            需要锁列
	 * @param w0301
	 * 		  会议编号
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList getColumnList(String exceptFields, String notEditFields, String islock,String w0301)throws GeneralException {
		ArrayList columnsList = new ArrayList();
		ColumnsInfo columnsInfo = new ColumnsInfo();
		// 业务字典指标
		ArrayList fieldList = DataDictionary.getFieldList("W05", 1);
		boolean expertFlg = false, subjectsFlg = false, committeeFlg = false,collegeFlg = false;
		LazyDynaBean bean = isOldW03Data(w0301);//为空代表是老数据
		String enableSteps = "";
		if(bean == null ) {
			enableSteps = getEnableSteps(w0301);
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
			if (exceptFields.indexOf("," + fi.getItemid().toLowerCase() + ",") != -1) {
				continue;
			}
			
			if("w0573".equals(fi.getItemid().toLowerCase())
                || "w0536".equals(fi.getItemid().toLowerCase())){//为了把【评审状态】列直接加在【评审环节】后，此处把【评审状态】跳过
				continue ;
			}
			// 测评表符合列头单独处理 
			if(fi.getItemid().toLowerCase().startsWith("c_") || fi.getItemid().toLowerCase().endsWith("_seq")) 
				continue ;
			
			columnsInfo = getColumnsInfoByFi(fi, 80);
			// 外部专家
			if("W0527".equalsIgnoreCase(fi.getItemid())//反对人数
					||"W0523".equalsIgnoreCase(fi.getItemid())
					|| "W0529".equalsIgnoreCase(fi.getItemid())//弃权人数
					|| "W0531".equalsIgnoreCase(fi.getItemid()) //赞成人数
					|| "W0533".equalsIgnoreCase(fi.getItemid())) {//状态
				if(expertFlg){//已加载过
					continue;
				}
				if(bean!=null) {
					if(!bean.getMap().containsKey("evaluationType_3")) {//同行阶段未启用  不显示
						expertFlg = true;
						continue;
					}
				}else {
					if(!enableSteps.contains(",3,")){
						expertFlg = true;
						continue;
					}
				}
				ColumnsInfo info = new ColumnsInfo();
				info.setQueryable(false);
				info.setColumnDesc(JobtitleUtil.ZC_REVIEWFILE_STEP3SHOWTEXT);
				info.setColumnId("checkproficient_hb");//为合并列设置id
				ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
				// 外部鉴定专家
				ColumnsInfo expertInfo = getColumnsInfo("checkproficient", JobtitleUtil.ZC_REVIEWFILE_STEP3SHOWTEXT, 80, "0", "A", 100, 0);
				expertInfo.setQueryable(false);
				expertInfo.setTextAlign("center");
				expertInfo.setEditableValidFunc("false");
				//expertInfo.setRendererFunc("reviewfile_me.checkProficient");
				//分组
				ColumnsInfo group_3 = getColumnsInfo("group_3", ResourceFactory.getProperty("zc_new.zc_reviewcfile.columnname.group"), 80, "0", "A", 100, 0);
				group_3.setQueryable(false);
				group_3.setTextAlign("center");
				group_3.setEditableValidFunc("false");
				list.add(group_3);
				list.add(expertInfo);
				ColumnsInfo w0533 = null;
				boolean tempFlag = false;//是否显示赞成人数占比
				
				for (int j = 0; j < fieldList.size(); j++) {
					FieldItem item = (FieldItem) fieldList.get(j);
					String itemId = item.getItemid();
					if (("W0527".equalsIgnoreCase(itemId)//反对人数
							|| "W0529".equalsIgnoreCase(itemId)//弃权人数
							|| "W0531".equalsIgnoreCase(itemId)//赞成人数
							|| "W0533".equalsIgnoreCase(itemId))&&item.isVisible()==true) {//状态

						if("W0531".equalsIgnoreCase(itemId))
							tempFlag = true;
						columnsInfo = getColumnsInfoByFi(item, 70);
						columnsInfo.setQueryable(false);
						if("W0531".equalsIgnoreCase(itemId)){//赞成
							columnsInfo.setRendererFunc("reviewfile_me.w0531");
						}else if("W0527".equalsIgnoreCase(itemId)){//反对
							columnsInfo.setRendererFunc("reviewfile_me.w0527");
						}else if("W0529".equalsIgnoreCase(itemId)){//弃权
							columnsInfo.setRendererFunc("reviewfile_me.w0529");
						}else if("W0533".equalsIgnoreCase(itemId)){
							columnsInfo.setRendererFunc("reviewfile_me.status");
							columnsInfo.setEditableValidFunc("false");
							w0533 = columnsInfo;
							continue;
						}
						columnsInfo.setEditableValidFunc("false");
						list.add(columnsInfo);
					}
				}
				if(tempFlag){					
					columnsInfo = getColumnsInfo("proficientagree", "赞成人数占比", 90, "0", "A", 3, 0);
					columnsInfo.setEditableValidFunc("false");
					columnsInfo.setRendererFunc("reviewfile_me.expertagree");
					columnsInfo.setQueryable(false);
					list.add(columnsInfo);
				}
				if(w0533 != null){
					list.add(w0533);
				}
				info.setChildColumns(list);
				columnsList.add(info);
				
				expertFlg = true;
				continue;
			}
			// 专业（学科）组
			else if("W0521".equalsIgnoreCase(fi.getItemid())//专家人数
					|| "W0543".equalsIgnoreCase(fi.getItemid())//反对人数
					|| "W0545".equalsIgnoreCase(fi.getItemid())//弃权人数
					|| "W0547".equalsIgnoreCase(fi.getItemid())//赞成人数
					|| "group_id".equalsIgnoreCase(fi.getItemid())// 专家鉴定问卷计划号
					|| "W0557".equalsIgnoreCase(fi.getItemid())) {//状态
				if(subjectsFlg){//已加载过
					continue;
				}
				if(bean!=null) {
					if(!bean.getMap().containsKey("evaluationType_2")) {////专业组阶段未启用  不显示
						subjectsFlg = true;
						continue;
					}else {
						String evaluationType = (String)bean.get("evaluationType_2");
						if("2".equals(evaluationType)) {
							ColumnsInfo scoreCol = this.getScoreColumnList("2",w0301);
							if(scoreCol!=null)
								columnsList.add(scoreCol);
							subjectsFlg = true;
							continue;
						}
					}
				}else {
					if(!enableSteps.contains(",2,")){
						subjectsFlg = true;
						continue;
					}
				}
				ColumnsInfo info = new ColumnsInfo();
				info.setQueryable(false);
				info.setColumnDesc(JobtitleUtil.ZC_REVIEWFILE_STEP2SHOWTEXT);
				info.setColumnId("subject_hb");//为合并列设置id
				ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
				
				ColumnsInfo group_2 = getColumnsInfo("group_2", ResourceFactory.getProperty("zc_new.zc_reviewcfile.columnname.group"), 80, "0", "A", 100, 0);
				group_2.setQueryable(false);
				group_2.setTextAlign("center");
				group_2.setEditableValidFunc("false");
				list.add(group_2);
				ColumnsInfo w0557 = null;
				boolean tempFlag = false;//是否显示赞成人数占比
				for (int j = 0; j < fieldList.size(); j++) {
					FieldItem item = (FieldItem) fieldList.get(j);
					String itemId = item.getItemid();
					columnsInfo = getColumnsInfoByFi(item, 70);
					if (("W0543".equalsIgnoreCase(itemId)
							|| "W0545".equalsIgnoreCase(itemId)
							|| "W0547".equalsIgnoreCase(itemId)
							|| "W0557".equalsIgnoreCase(itemId))&&item.isVisible()==true) {//状态
						
						columnsInfo.setQueryable(false);
						
						if("W0547".equalsIgnoreCase(itemId))
							tempFlag = true;
						
						if("W0547".equalsIgnoreCase(itemId)){//赞成
							columnsInfo.setRendererFunc("reviewfile_me.w0547");
						}else if("W0543".equalsIgnoreCase(itemId)){//反对
							columnsInfo.setRendererFunc("reviewfile_me.w0543");
						}else if("W0545".equalsIgnoreCase(itemId)){//弃权
							columnsInfo.setRendererFunc("reviewfile_me.w0545");
						}else if("W0557".equalsIgnoreCase(itemId)){
							columnsInfo.setRendererFunc("reviewfile_me.status");
							columnsInfo.setEditableValidFunc("false");
							w0557 = columnsInfo;
							continue;
						}
						columnsInfo.setEditableValidFunc("false");
						list.add(columnsInfo);
						
					}else if("W0521".equalsIgnoreCase(itemId)){//专家人数
						columnsInfo.setQueryable(false);
						columnsInfo.setColumnType("A");
						columnsInfo.setCodesetId("0");// 指标集
						columnsInfo.setColumnDesc("已评数/总数");
						columnsInfo.setColumnWidth(80);
						columnsInfo.setTextAlign("center");
						columnsInfo.setRendererFunc("reviewfile_me.w0521");
						columnsInfo.setEditableValidFunc("false");
						list.add(columnsInfo);
						
					} else if("group_id".equalsIgnoreCase(itemId)){
						/*columnsInfo.setQueryable(false);
						columnsInfo.setColumnDesc(JobtitleUtil.ZC_REVIEWFILE_STEP2SHOWTEXT);
						columnsInfo.setCodesetId("0");// 指标集
						columnsInfo.setColumnType("A");// 类型N|M|A|D
						columnsInfo.setOperationData("group_id");
						columnsInfo.setColumnWidth(100);
						columnsInfo.setEditableValidFunc("reviewfile_me.createGroup");
						list.add(0, columnsInfo);*/
					}
				}
				if(tempFlag){					
					columnsInfo = getColumnsInfo("subjectsagree", "赞成人数占比", 90, "0", "A", 3, 0);
					columnsInfo.setEditableValidFunc("false");
					columnsInfo.setRendererFunc("reviewfile_me.subjectsagree");
					columnsInfo.setQueryable(false);
					list.add(columnsInfo);
				}
				if(w0557 != null){
					list.add(w0557);
				}
				info.setChildColumns(list);
				columnsList.add(info);
				
				subjectsFlg = true;
				continue;
			}
			// 聘委会
			else if("W0517".equalsIgnoreCase(fi.getItemid())//评委人数
					|| "W0519".equalsIgnoreCase(fi.getItemid())//参会评委人数
					|| "W0549".equalsIgnoreCase(fi.getItemid())//反对人数
					|| "W0551".equalsIgnoreCase(fi.getItemid())//弃权人数
					|| "W0553".equalsIgnoreCase(fi.getItemid())//赞成人数
					|| "W0559".equalsIgnoreCase(fi.getItemid())) {//状态
				if(committeeFlg){//已加载过
					continue;
				}
				if(bean!=null) {
					if(!bean.getMap().containsKey("evaluationType_1")) {////专业组阶段未启用  不显示
						committeeFlg = true;
						continue;
					}else {
						String evaluationType = (String)bean.get("evaluationType_1");
						if("2".equals(evaluationType)) {
							ColumnsInfo scoreCol = this.getScoreColumnList("1",w0301);
							if(scoreCol!=null)
								columnsList.add(scoreCol);
							committeeFlg = true;
							continue;
						}
					}
				}else {
					if(!enableSteps.contains(",1,")){
						committeeFlg = true;
						continue;
					}
				}
				ColumnsInfo info = new ColumnsInfo();
				info.setQueryable(false);
				info.setColumnDesc(JobtitleUtil.ZC_REVIEWFILE_STEP1SHOWTEXT);
				info.setColumnId("committee_hb");//为合并列设置id
				ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
				
				ColumnsInfo group_1 = getColumnsInfo("group_1", ResourceFactory.getProperty("zc_new.zc_reviewcfile.columnname.group"), 80, "0", "A", 100, 0);
				group_1.setQueryable(false);
				group_1.setTextAlign("center");
				group_1.setEditableValidFunc("false");
				list.add(group_1);
				ColumnsInfo w0559 = null;
				boolean tempFlag = false;//是否显示赞成人数占比
				for (int j = 0; j < fieldList.size(); j++) {
					FieldItem item = (FieldItem) fieldList.get(j);
					String itemId = item.getItemid();
					if("W0519".equalsIgnoreCase(itemId))//参会评委人数
						continue;
					if (("W0517".equalsIgnoreCase(itemId)//评委人数
							|| "W0549".equalsIgnoreCase(itemId)//反对人数
							|| "W0551".equalsIgnoreCase(itemId)//弃权人数
							|| "W0553".equalsIgnoreCase(itemId)//赞成人数
							|| "W0559".equalsIgnoreCase(itemId))&&item.isVisible()==true) {//状态

						if("W0553".equalsIgnoreCase(itemId))
							tempFlag = true;
						columnsInfo = getColumnsInfoByFi(item, 70);
						columnsInfo.setQueryable(false);
						columnsInfo.setEditableValidFunc("false");
						
						if("W0517".equalsIgnoreCase(itemId)){//评委人数
							columnsInfo.setColumnDesc("已评数/总数");
							columnsInfo.setColumnType("A");
							columnsInfo.setCodesetId("0");// 指标集
							columnsInfo.setColumnWidth(80);
							columnsInfo.setTextAlign("center");
							columnsInfo.setRendererFunc("reviewfile_me.w0517");
						}
						
						if("W0553".equalsIgnoreCase(itemId)){//赞成
							columnsInfo.setRendererFunc("reviewfile_me.w0553");
						}else if("W0549".equalsIgnoreCase(itemId)){//反对
							columnsInfo.setRendererFunc("reviewfile_me.w0549");
						}else if("W0551".equalsIgnoreCase(itemId)){//弃权
							columnsInfo.setRendererFunc("reviewfile_me.w0551");
						}else if("W0559".equalsIgnoreCase(itemId)){
							columnsInfo.setRendererFunc("reviewfile_me.status");
							columnsInfo.setEditableValidFunc("false");
							w0559 = columnsInfo;
							continue;
						}
						list.add(columnsInfo);
					}
				}
				if(tempFlag){					
					columnsInfo = getColumnsInfo("committeeagree", "赞成人数占比", 90, "0", "A", 3, 0);
					columnsInfo.setEditableValidFunc("false");
					columnsInfo.setRendererFunc("reviewfile_me.committeeagree");
					columnsInfo.setQueryable(false);
					list.add(columnsInfo);
				}
				if(w0559 != null){
					list.add(w0559);
				}
				info.setChildColumns(list);
				columnsList.add(info);
				
				committeeFlg = true;
				continue;
			}
			// 二级单位
			if("W0563".equalsIgnoreCase(fi.getItemid())//反对人数
					|| "W0565".equalsIgnoreCase(fi.getItemid())//弃权人数
					|| "W0567".equalsIgnoreCase(fi.getItemid())//赞成人数
					|| "W0569".equalsIgnoreCase(fi.getItemid())//状态
					|| "W0561".equalsIgnoreCase(fi.getItemid())//学院聘任组
					|| "W0571".equalsIgnoreCase(fi.getItemid())){//聘任组人数
				if(collegeFlg){//已加载过
					continue;
				}
				if(bean!=null) {
					if(!bean.getMap().containsKey("evaluationType_4")) {////专业组阶段未启用  不显示
						collegeFlg = true;
						continue;
					}else {
						String evaluationType = (String)bean.get("evaluationType_4");
						if("2".equals(evaluationType)) {
							ColumnsInfo scoreCol = this.getScoreColumnList("4",w0301);
							if(scoreCol!=null)
								columnsList.add(scoreCol);
							collegeFlg = true;
							continue;
						}
					}
				}else {
					if(!enableSteps.contains(",4,")){
						collegeFlg = true;
						continue;
					}
				}
				ColumnsInfo info = new ColumnsInfo();
				info.setQueryable(false);
				info.setColumnDesc(JobtitleUtil.ZC_REVIEWFILE_STEP4SHOWTEXT);
				info.setColumnId("college_hb");	//为合并列设置id
				ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
				ColumnsInfo group_4 = getColumnsInfo("group_4", ResourceFactory.getProperty("zc_new.zc_reviewcfile.columnname.group"), 80, "0", "A", 100, 0);
				group_4.setQueryable(false);
				group_4.setTextAlign("center");
				group_4.setEditableValidFunc("false");
				list.add(group_4);
				ColumnsInfo W0569 = null;
				boolean tempFlag = false;//是否显示赞成人数占比
				for (int j = 0; j < fieldList.size(); j++) {
					FieldItem item = (FieldItem) fieldList.get(j);
					String itemId = item.getItemid();
					if (("W0563".equalsIgnoreCase(itemId)//反对人数
							|| "W0565".equalsIgnoreCase(itemId)//弃权人数
							|| "W0567".equalsIgnoreCase(itemId)//赞成人数
							|| "W0569".equalsIgnoreCase(itemId)&&item.isVisible()==true)) {//状态

						if("W0567".equalsIgnoreCase(itemId))
							tempFlag = true;
						columnsInfo = getColumnsInfoByFi(item, 70);
						columnsInfo.setQueryable(false);
						if("W0567".equalsIgnoreCase(itemId)){//赞成
							columnsInfo.setRendererFunc("reviewfile_me.w0567");
						}else if("W0563".equalsIgnoreCase(itemId)){//反对
							columnsInfo.setRendererFunc("reviewfile_me.w0563");
						}else if("W0565".equalsIgnoreCase(itemId)){//弃权
							columnsInfo.setRendererFunc("reviewfile_me.w0565");
						}else if("W0569".equalsIgnoreCase(itemId)){
							columnsInfo.setRendererFunc("reviewfile_me.status");
							columnsInfo.setEditableValidFunc("false");
							W0569 = columnsInfo;
							continue;
						}
						columnsInfo.setEditableValidFunc("false");
						
						list.add(columnsInfo);
					}
					else if("W0571".equalsIgnoreCase(itemId)){
						ColumnsInfo W0571 = new ColumnsInfo();
						W0571.setQueryable(false);
						W0571.setColumnId("W0571");
						W0571.setColumnDesc("已评数/总数");
						W0571.setColumnType("A");
						W0571.setCodesetId("0");// 指标集
						W0571.setColumnWidth(80);
						W0571.setTextAlign("center");
						W0571.setRendererFunc("reviewfile_me.W0571");
						W0571.setEditableValidFunc("false");
						list.add(W0571);
					}
					else if("W0561".equalsIgnoreCase(itemId)){//学院聘任组
						if(bean !=null) {
							String usertype = (String)bean.get("usertype_4");
							if("1".equals(usertype)) {
								continue;
							}
						}
						ColumnsInfo W0561 = new ColumnsInfo();
						W0561.setQueryable(false);
						W0561.setColumnId("committeeName");
						W0561.setColumnDesc(JobtitleUtil.ZC_REVIEWFILE_STEP4SHOWTEXT);
						W0561.setColumnWidth(100);
						W0561.setEditableValidFunc("false");
						list.add(0,W0561);
					}
					
				}
				if(tempFlag){					
					columnsInfo = getColumnsInfo("collegeagree", "赞成人数占比", 90, "0", "A", 3, 0);
					columnsInfo.setEditableValidFunc("false");
					columnsInfo.setRendererFunc("reviewfile_me.collegeagree");
					columnsInfo.setQueryable(false);
					list.add(columnsInfo);
				}
				if(W0569 != null){
					list.add(W0569);
				}
				info.setChildColumns(list);
				columnsList.add(info);
				
				collegeFlg = true;
				continue;
			}
			else if ("W0535".equalsIgnoreCase(fi.getItemid())) {// 评审材料访问地址
				columnsInfo.setColumnId("w0535_");
				columnsInfo.setColumnDesc(fi.getItemdesc());
				columnsInfo.setEditableValidFunc("false");
				columnsInfo.setRendererFunc("reviewfile_me.w0535");
				columnsInfo.setTextAlign("center");
				columnsInfo.setQueryable(false);
			} 
			else if ("W0537".equalsIgnoreCase(fi.getItemid())) {// 送审论文材料访问地址
				columnsInfo.setColumnId("w0537_");
				columnsInfo.setEditableValidFunc("false");
				columnsInfo.setColumnDesc(fi.getItemdesc());
				columnsInfo.setRendererFunc("reviewfile_me.w0537");
				columnsInfo.setTextAlign("center");
				columnsInfo.setQueryable(false);
			} 
			else if ("W0539".equalsIgnoreCase(fi.getItemid())) {// 内部评审问卷模板号
//				columnsInfo.setCodesetId("0");// 指标集
//				columnsInfo.setColumnType("A");// 类型N|M|A|D
				columnsInfo.setOperationData("qnPlan");
				columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
				// columnsInfo.setRendererFunc("reviewfile_me.W0539");
			} 
			else if ("W0541".equalsIgnoreCase(fi.getItemid())) {// 专家鉴定问卷模板号
//				columnsInfo.setCodesetId("0");// 指标集
//				columnsInfo.setColumnType("A");// 类型N|M|A|D
				columnsInfo.setOperationData("qnPlan");
				columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
				// columnsInfo.setRendererFunc("reviewfile_me.W0541");
			} 
			else if ("w0555".equalsIgnoreCase(fi.getItemid())) {// 评审环节
				//columnsInfo.setRendererFunc("reviewfile_me.w0555");
//				columnsInfo.setColumnType("A");
//				columnsInfo.setCodesetId("0");// 指标集
				columnsInfo.setOperationData("reviewsteplist");
			} else if ("w0513".equalsIgnoreCase(fi.getItemid()) || "w0515".equalsIgnoreCase(fi.getItemid())) {// 现聘职称、申报职称改为字符型 chent 20170413
				columnsInfo.setColumnType("A");
				columnsInfo.setCodesetId("AJ");
				columnsInfo.setCodeRealValue(true);
				columnsInfo.setEditableValidFunc("false");
			}/*else if ("W0536".equalsIgnoreCase(fi.getItemid())) {// 评审材料word模板
				columnsInfo.setColumnId("w0536_");
				columnsInfo.setColumnDesc(fi.getItemdesc());
				columnsInfo.setEditableValidFunc("false");
				columnsInfo.setRendererFunc("reviewfile_me.w0536");
				columnsInfo.setTextAlign("center");
				columnsInfo.setQueryable(false);
			} */
			// 单位、部门等，过滤时按照业务范围走
			String columnType = fi.getItemtype();
			String codesetid = fi.getCodesetid();
			if("A".equals(columnType) && ("UM".equalsIgnoreCase(codesetid) || "UN".equalsIgnoreCase(codesetid))) {
				columnsInfo.setCodesetId(codesetid);
				columnsInfo.setCtrltype("3");
				columnsInfo.setNmodule("9");
			}

			// 日期型、数值型右对齐
			if ("D".equals(columnType) || "N".equals(columnType)) {
				if ("W0539".equalsIgnoreCase(fi.getItemid())
						|| "W0541".equalsIgnoreCase(fi.getItemid()) 
						|| "W0555".equalsIgnoreCase(fi.getItemid())) {
				} else {
					columnsInfo.setTextAlign("right");
				}
			}
			// 不允许编辑的列
			if (!StringUtils.isEmpty(notEditFields)) {
				if (notEditFields.indexOf("," + fi.getItemid() + ",") != -1) {
					columnsInfo.setEditableValidFunc("false");
				}
			}
			// 导入的字段不允许修改。不是w05开头的就不让修改，特殊的：group_Id让修改。
			if(!"W05".equalsIgnoreCase(fi.getItemid().substring(0, 3)) && !"group_Id".equalsIgnoreCase(fi.getItemid())) {
				columnsInfo.setEditableValidFunc("false");
			}

			// 需要锁列
			if (!StringUtils.isEmpty(islock)) {
				if (islock.indexOf("," + fi.getItemid() + ",") != -1) {
					columnsInfo.setLocked(true);
					columnsList.add(0, columnsInfo);// 需要锁列的放到第一列，不然导出时顺序会错。
					continue;
				}
			}
				
			
			columnsList.add(columnsInfo);
			
			/*if("w0555".equals(fi.getItemid().toLowerCase())){//评审环节后，直接加入【评审状态】
				FieldItem Item_w0573 = DataDictionary.getFieldItem("W0573");
				if(Item_w0573 != null && "1".equals(Item_w0573.getState()) && "1".equals(Item_w0573.getUseflag())){
					ColumnsInfo w0573 = getColumnsInfoByFi(Item_w0573, 80);
					//w0573.setRendererFunc("reviewfile_me.w0573");
					w0573.setEditableValidFunc("false");
					//w0573.setColumnType("A");
					w0573.setOperationData("w0573");
					columnsList.add(w0573);
				}
			}*/
		}

		// 自定义列
		columnsInfo = getColumnsInfo("meetingname", "会议名称", 180, "0", "A", 100, 0);
		columnsInfo.setLocked(true);
		columnsInfo.setEditableValidFunc("false");
		columnsList.add(0, columnsInfo);
		
		
		/** 隐藏 */
		// 编号
		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("w0503_safe");
		columnsInfo.setColumnDesc("应用库前缀");
		columnsInfo.setEncrypted(true);
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		columnsList.add(columnsInfo);

		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("w0501");
		columnsInfo.setColumnDesc("申报人主键序号");
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		columnsList.add(columnsInfo);

		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("w0501_safe");
		columnsInfo.setColumnDesc("申报人主键序号加密");
		columnsInfo.setEncrypted(true);
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		columnsList.add(columnsInfo);

		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("w0505");
		columnsInfo.setColumnDesc("人员编号");
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		columnsList.add(columnsInfo);

		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("w0505_safe");
		columnsInfo.setColumnDesc("人员编号加密");
		columnsInfo.setEncrypted(true);
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		columnsList.add(columnsInfo);

		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("W0301");
		columnsInfo.setColumnDesc("会议ID");
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		columnsList.add(columnsInfo);
		
		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("W0321");
		columnsInfo.setColumnDesc("会议状态");
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		columnsList.add(columnsInfo);

		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("W0301_safe");
		columnsInfo.setColumnDesc("会议ID加密");
		columnsInfo.setEncrypted(true);
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		columnsList.add(columnsInfo);

		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("w0523");
		columnsInfo.setColumnDesc("外部鉴定专家人数");
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		columnsList.add(columnsInfo);

		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("w0525");
		columnsInfo.setColumnDesc("是否导入数据");
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		columnsList.add(columnsInfo);

		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("nbasea0100");
		columnsInfo.setColumnDesc("人员库加人员编号");
		columnsInfo.setEncrypted(true);
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		columnsList.add(columnsInfo);

		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("nbasea0100_1");
		columnsInfo.setColumnDesc("人员库加人员编号中间加`");
		columnsInfo.setEncrypted(true);
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		columnsList.add(columnsInfo);
		
		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("w0535");
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		columnsList.add(columnsInfo);

		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("w0536");
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		columnsList.add(columnsInfo);
		
		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("w0537");
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		columnsList.add(columnsInfo);

		return columnsList;
	}

	/**
	 * 获取功能按钮
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Object> getButtonList(boolean returnButton) {

		ArrayList<Object> buttonList = new ArrayList<Object>();
		try {
			ArrayList<Object> menuList = new ArrayList<Object>();
			/*
			//-----------------审核账号 start---------------------------
			ArrayList<Object> key1List = new ArrayList<Object>();
			if (this.userview.hasTheFunction("380050603")){
				LazyDynaBean bean2 = getMenuBean("生成"+JobtitleUtil.ZC_REVIEWFILE_STEP4SHOWTEXT+"账号密码","JobTitleReviewFile.createExamineVoteKey('examine',4)","",new ArrayList());
				bean2.set("id", "create14");
				key1List.add(bean2);
			}
			if (this.userview.hasTheFunction("380050602")){
				LazyDynaBean bean2 = getMenuBean("生成"+JobtitleUtil.ZC_REVIEWFILE_STEP2SHOWTEXT+"账号密码","JobTitleReviewFile.createExamineVoteKey('examine',2)","",new ArrayList());
				bean2.set("id", "create12");
				key1List.add(bean2);
			}
			if (this.userview.hasTheFunction("380050601")){
				LazyDynaBean bean2 = getMenuBean("生成"+JobtitleUtil.ZC_REVIEWFILE_STEP1SHOWTEXT+"账号密码","JobTitleReviewFile.createExamineVoteKey('examine',1)","",new ArrayList());
				bean2.set("id", "create11");
				key1List.add(bean2);
			}
			if (this.userview.hasTheFunction("380050604")) {
				key1List.add(getMenuBean("导出审核账号密码","JobTitleReviewFile.expKey('1')","",new ArrayList<Object>()));
			}
			if(key1List.size() > 0){
				LazyDynaBean oneBean1 = new LazyDynaBean();
				oneBean1.set("text", "审核账号");
				oneBean1.set("menu", key1List);
				oneBean1.set("id", "key1");
				menuList.add(oneBean1);
			}*/
			//-----------------审核账号 end---------------------------
			
			//-----------------投票账号 start---------------------------
			/*ArrayList<Object> key2List = new ArrayList<Object>();
			if (this.userview.hasTheFunction("380050608")){
				LazyDynaBean bean2 = getMenuBean("生成"+JobtitleUtil.ZC_REVIEWFILE_STEP4SHOWTEXT+"账号密码","JobTitleReviewFile.createExamineVoteKey('vote',4)","",new ArrayList());
				bean2.set("id", "create24");
				key2List.add(bean2);
			}
			if (this.userview.hasTheFunction("380050607")){
				LazyDynaBean bean2 = getMenuBean("生成"+JobtitleUtil.ZC_REVIEWFILE_STEP3SHOWTEXT+"账号密码","JobTitleReviewFile.randomCreateKey()","",new ArrayList());
				bean2.set("id", "create23");
				key2List.add(bean2);
			}
			if (this.userview.hasTheFunction("380050606")){
				LazyDynaBean bean2 = getMenuBean("生成"+JobtitleUtil.ZC_REVIEWFILE_STEP2SHOWTEXT+"账号密码","JobTitleReviewFile.createExamineVoteKey('vote',2)","",new ArrayList());
				bean2.set("id", "create22");
				key2List.add(bean2);
			}
			if (this.userview.hasTheFunction("380050605")){
				LazyDynaBean bean2 = getMenuBean("生成"+JobtitleUtil.ZC_REVIEWFILE_STEP1SHOWTEXT+"账号密码","JobTitleReviewFile.createExamineVoteKey('vote',1)","",new ArrayList());
				bean2.set("id", "create21");
				key2List.add(bean2);
			}
			
			if (this.userview.hasTheFunction("380050609")) {
				key2List.add(getMenuBean("导出投票账号密码","JobTitleReviewFile.expKey('2')","",new ArrayList<Object>()));
			}
			
			if(key2List.size() > 0){
				LazyDynaBean oneBean2 = new LazyDynaBean();
				oneBean2.set("text", "投票账号");
				oneBean2.set("menu", key2List);
				oneBean2.set("id", "key2");
				menuList.add(oneBean2);
			}
			//-----------------投票账号 end---------------------------
			
			if (this.userview.hasTheFunction("380050610")) {
    			LazyDynaBean buttonInfo = new LazyDynaBean();
    			buttonInfo.set("text", ResourceFactory.getProperty("生成公示材料"));
    			buttonInfo.set("handler", "JobTitleReviewFile.notice()");
    			menuList.add(buttonInfo);
			}
			if (this.userview.hasTheFunction("380050611")) {
				LazyDynaBean buttonInfo = new LazyDynaBean();
				buttonInfo.set("id", "archiving");
    			buttonInfo.set("text", ResourceFactory.getProperty("结果归档"));
    			buttonInfo.set("handler", "JobTitleReviewFile.resultsArchiving()");
				menuList.add(buttonInfo);
			}
			*/
			// 是否在version.xml中启用“导出外审材料”功能，如果没有启用，则在参数设置中将不显示
//			VersionControl vc = new VersionControl();
//			if(vc.searchFunctionId("380050620", this.userview.hasTheFunction("380050620"))){
			if(this.userview.hasTheFunction("380050620")){
				LazyDynaBean buttonInfo = new LazyDynaBean();
				buttonInfo.set("id", "material");
				buttonInfo.set("text", ResourceFactory.getProperty("zc_new.zc_reviewcConsole.masterpiece"));
				buttonInfo.set("handler", "JobTitleReviewFile.materialWindow()");
				menuList.add(buttonInfo);
			}		
			
			if(menuList.size()>0) {
				String menu = getMenuStr("功能导航","navbar",menuList);
				buttonList.add(menu);
			}
			if (this.userview.hasTheFunction("380050613")) {
				ButtonInfo buttonInfo = new ButtonInfo(ResourceFactory.getProperty("button.save"),"JobTitleReviewFile.saveData");
				buttonInfo.setId("reviewfile_save");
				buttonList.add(buttonInfo);
			}
			if (this.userview.hasTheFunction("380050619")) {//导出
				ButtonInfo buttonInfo = new ButtonInfo("导出",ButtonInfo.FNTYPE_EXPORT,"");
				buttonInfo.setId("reviewfile_outputData");
				buttonList.add(buttonInfo);
			}
			/*if (this.userview.hasTheFunction("380050612")) {
				ButtonInfo buttonInfo = new ButtonInfo("导入", "JobTitleReviewFile.importData");
				buttonInfo.setId("reviewfile_importData");
				buttonList.add(buttonInfo);
			}
			if (this.userview.hasTheFunction("380050614")) {
				ButtonInfo buttonInfo = new ButtonInfo("撤销", "JobTitleReviewFile.deletePerson");
				buttonInfo.setId("reviewfile_revoke");
				buttonList.add(buttonInfo);
			}
			ArrayList<Object> startVoteList = new ArrayList<Object>();//启动投票菜单
			if (this.userview.hasTheFunction("380050615")) {
				LazyDynaBean bean = getMenuBean("材料审核","JobTitleReviewFile.examineAndStart('examine')","",new ArrayList<Object>());
				bean.set("id", "examine");
				startVoteList.add(bean);
			}
			if (this.userview.hasTheFunction("380050616")) {
				LazyDynaBean bean = getMenuBean("投票","JobTitleReviewFile.examineAndStart('vote')","",new ArrayList<Object>());
				bean.set("id", "vote");
				startVoteList.add(bean);
			}
			if(startVoteList.size()>0){
				String startVote = getMenuStr("启动","startVote",startVoteList);			
				buttonList.add(startVote);
			}
			//统计票数
			if(this.userview.hasTheFunction("380050618")){
				ButtonInfo buttonInfo = new ButtonInfo("统计票数","JobTitleReviewFile.syncVotes");
				buttonInfo.setId("reviewfile_syncVotes");
				buttonList.add(buttonInfo);
			}*/
			if(returnButton){
				ButtonInfo buttonInfo = new ButtonInfo("返回", "JobTitleReviewFile.returnPage");
				buttonList.add(buttonInfo);
			}
			
			ButtonInfo queryBox = new ButtonInfo();
			queryBox.setType(ButtonInfo.TYPE_QUERYBOX);
			queryBox.setText("请输入单位名称、部门、姓名");
			queryBox.setFunctionId("ZC00003001");
			buttonList.add(queryBox);
			// 卡片/列表切换
			buttonList.add(new ButtonInfo("<div id='cardcut'></div>"));
			buttonList.add(new ButtonInfo("<div id='listcut'></div>"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return buttonList;
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
	public String getMenuStr(String name,String id,ArrayList list){
		StringBuffer str = new StringBuffer();
		try{
			if(name.length()>0){
				str.append("<jsfn>{xtype:'button',text:'"+name+"'");
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
					str.append(getMenuStr("",menuId, menulist));
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
	 * 获取url中指定字符串
	 * 
	 * @param url
	 * @param str
	 * @return
	 */
	public String getValByStr(String url, String str) {

		String val = "";
		try {
			url = url.substring(1);
			url = url.replaceAll("b_query=link&encryptParam=", "");
			url = PubFunc.decrypt(url);
			int index = url.indexOf(str + "=");
			int startIndex = index + str.length() + 1;
			int endIndex = index + str.length() + 2;
			val = url.substring(startIndex, endIndex);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return val;
	}

	/**
	 * 整型判断
	 * 
	 * @param str
	 * @return
	 */
	public boolean isInteger(String str) {
		if (str == null)
			return false;
		Pattern pattern = Pattern.compile("[0-9]+");
		return pattern.matcher(str).matches();
	}
	/**
	 * 取得查询语句：取得评审会议列表
	 * 
	 * @param valuesList 快速查询检索条件
	 * @param schemeType 查询方案 in:进行中 stop:暂停 finish:已结束 all:全部
	 * @return
	 */
	public String getSql(String w0301)throws GeneralException {
		StringBuilder sql = new StringBuilder();
		StringBuilder selectSql = new StringBuilder();
		sql.append("select ");
		ArrayList<FieldItem> fieldList = DataDictionary.getFieldList("W05", Constant.USED_FIELD_SET);
		for (int i = 0; i < fieldList.size(); i++) {
			FieldItem fi = (FieldItem) fieldList.get(i);
			String itemid = fi.getItemid();
			if(itemid.toLowerCase().startsWith("c_") || itemid.toLowerCase().endsWith("_seq")) {
				continue;
			}
			if("w0571".equalsIgnoreCase(itemid)) {
				selectSql.append(Sql_switcher.sqlToChar(Sql_switcher.isnull("w0563","0")+"+"+Sql_switcher.isnull("w0565","0")+"+"+Sql_switcher.isnull("w0567","0"))+Sql_switcher.concat()+"'/'"+Sql_switcher.concat()+Sql_switcher.sqlToChar(Sql_switcher.isnull("w0323", "0"))+" as w0571");
			}else if("w0523".equalsIgnoreCase(itemid)){
				selectSql.append(Sql_switcher.sqlToChar(Sql_switcher.isnull("w0527","0")+"+"+Sql_switcher.isnull("w0529","0")+"+"+Sql_switcher.isnull("w0531","0"))+Sql_switcher.concat()+"'/'"+Sql_switcher.concat()+Sql_switcher.sqlToChar(Sql_switcher.isnull("w0523","0"))+" as checkProficient");
			}else if("w0521".equalsIgnoreCase(itemid)){
				selectSql.append(Sql_switcher.sqlToChar(Sql_switcher.isnull("w0543","0")+"+"+Sql_switcher.isnull("w0545","0")+"+"+Sql_switcher.isnull("w0547","0"))+Sql_switcher.concat()+"'/'"+Sql_switcher.concat()+Sql_switcher.sqlToChar(Sql_switcher.isnull("w0521","0"))+" as w0521");
			}else if("w0517".equalsIgnoreCase(itemid)){
				selectSql.append(Sql_switcher.sqlToChar(Sql_switcher.isnull("w0549","0")+"+"+Sql_switcher.isnull("w0551","0")+"+"+Sql_switcher.isnull("w0553","0"))+Sql_switcher.concat()+"'/'"+Sql_switcher.concat()+Sql_switcher.sqlToChar(Sql_switcher.isnull("w0315","0"))+" as w0517");
			}else {
				selectSql.append("w05."+itemid);
			}
			selectSql.append(",");
		}
		sql.append(selectSql);
		
		//打分阶段数据列（与分数统计sql一致）
		if(StringUtils.isNotBlank(w0301)) {
			ReviewMeetingPortalBo rmpb = new ReviewMeetingPortalBo(userview,conn);
			ReviewScorecountBo rscb = new ReviewScorecountBo(conn, userview, w0301, "");
			//先获取到这个会议有多少个阶段
			String ments = rscb.getReviewMeetingMent();
			//再获取当前阶段的状态，比如是否是评分阶段
			LazyDynaBean meetingBean = rmpb.getMeetingData(w0301);
			String[] mentArr = ments.split(",");
			for(int i=0;i<mentArr.length;i++) {
				String ment = mentArr[i];
				if(StringUtils.isBlank(ment)) {
					continue;
				}
				String evaluationType = (String)meetingBean.get("evaluationType_"+ment);
				//评分列数据
				if("2".equals(evaluationType)) {
					//获取模板组sql
					String templates = rscb.getReviewMeetingTemplateids(ment);
					String[] perTemps = templates.split(",");
					for(int j = 0; j < perTemps.length; j++) {
						String per = perTemps[j];
						sql.append("(select score from kh_object where Relation_id='1_"+w0301+"_"+ment+"' and Object_id=w05.W0505 and template_id='"+per+"') as C_"+per+"_"+ment);
						sql.append(",(select case when "+Sql_switcher.isnull("score", "-1000")+"=-1000 then null else seq end as seq from kh_object where Relation_id='1_"+w0301+"_"+ment+"' and Object_id=w05.W0505 and template_id='"+per+"') as C_"+per+"_seq_"+ment+",");
					}
					
				}
			}
		}
		
		sql.append("w05.w0301,w05.w0501 as w0501_safe");
		sql.append(",w05.w0503 as w0503_safe");
		sql.append(",w03.w0303 as meetingname ");
		sql.append(",w05.w0301 as w0301_safe");
		sql.append(",W0321,w03.b0110");
		sql.append(",w05.w0505 as w0505_safe ");
		sql.append(",zc.committee_name as committeeName");
		sql.append(",w05.w0503"+Sql_switcher.concat()+"w05.w0505 as nbasea0100,w05.w0503"+Sql_switcher.concat()+"'`'"+Sql_switcher.concat()+"w05.w0505 as nbasea0100_1 ,'' as w0535_, '' as w0537_");
		sql.append(",w0303");
		sql.append(",(SELECT Name FROM zc_personnel_categories zpc WHERE Review_links=1 AND W0301=W05.w0301 AND EXISTS(SELECT categories_id FROM zc_categories_relations zcr WHERE zpc.categories_id=zcr.categories_id AND zcr.w0501=w05.w0501)) AS group_1");
		sql.append(",(SELECT Name FROM zc_personnel_categories zpc WHERE Review_links=2 AND W0301=W05.w0301 AND EXISTS(SELECT categories_id FROM zc_categories_relations zcr WHERE zpc.categories_id=zcr.categories_id AND zcr.w0501=w05.w0501))  AS group_2");
		sql.append(",(SELECT Name FROM zc_personnel_categories zpc WHERE Review_links=3 AND W0301=W05.w0301 AND EXISTS(SELECT categories_id FROM zc_categories_relations zcr WHERE zpc.categories_id=zcr.categories_id AND zcr.w0501=w05.w0501))  AS group_3");
		sql.append(",(SELECT Name FROM zc_personnel_categories zpc WHERE Review_links=4 AND W0301=W05.w0301 AND EXISTS(SELECT categories_id FROM zc_categories_relations zcr WHERE zpc.categories_id=zcr.categories_id AND zcr.w0501=w05.w0501))  AS group_4");
		// 赞成占比 start
		sql.append(",case when ("+Sql_switcher.isnull("w0549","0")+"+"+Sql_switcher.isnull("w0551","0")+"+"+Sql_switcher.isnull("w0553","0")+")=0 then '0' else cast(cast(cast((w0553)as float)/("+Sql_switcher.isnull("w0549","0")+"+"+Sql_switcher.isnull("w0551","0")+"+"+Sql_switcher.isnull("w0553","0")+")*100 as int)as varchar(10)) end "+Sql_switcher.concat()+"'%'  as committeeagree");
		sql.append(",case when ("+Sql_switcher.isnull("w0543","0")+"+"+Sql_switcher.isnull("w0545","0")+"+"+Sql_switcher.isnull("w0547","0")+")=0 then '0' else cast(cast(cast((w0547)as float)/("+Sql_switcher.isnull("w0543","0")+"+"+Sql_switcher.isnull("w0545","0")+"+"+Sql_switcher.isnull("w0547","0")+")*100 as int)as varchar(10)) end "+Sql_switcher.concat()+"'%'  as subjectsagree");
		sql.append(",case when ("+Sql_switcher.isnull("w0527","0")+"+"+Sql_switcher.isnull("w0529","0")+"+"+Sql_switcher.isnull("w0531","0")+")=0 then '0' else cast(cast(cast((w0531)as float)/("+Sql_switcher.isnull("w0527","0")+"+"+Sql_switcher.isnull("w0529","0")+"+"+Sql_switcher.isnull("w0531","0")+")*100 as int)as varchar(10)) end "+Sql_switcher.concat()+"'%'  as proficientagree");
		sql.append(",case when ("+Sql_switcher.isnull("w0563","0")+"+"+Sql_switcher.isnull("w0565","0")+"+"+Sql_switcher.isnull("w0567","0")+")=0 then '0' else cast(cast(cast((w0567)as float)/("+Sql_switcher.isnull("w0563","0")+"+"+Sql_switcher.isnull("w0565","0")+"+"+Sql_switcher.isnull("w0567","0")+")*100 as int)as varchar(10)) end "+Sql_switcher.concat()+"'%'  as collegeagree");
		// 赞成占比 end
		sql.append(" from w05 ");
		sql.append(" left join w03 on w05.w0301=w03.w0301 ");
		sql.append(" left join zc_committee zc on w03.sub_committee_id=zc.committee_id ");
		sql.append(" where w05.w0301=w03.w0301 ");
		
		// 评审会议限制业务范围
		//haosl 20160805 修改上会材料可以看到所有直属上级的开启学院聘任组的评审会议的上会 
		String b0110 = this.userview.getUnitIdByBusi("9");//取得所属单位
		if(b0110.split("`")[0].length() > 2 && !userview.isSuper_admin()){//组织机构去除UN、UM后不为空：取本级，下级。为空：最高权限
			String whereSql = this.getB0110Sql_down(b0110);
			sql.append(whereSql);
		}
		
		return sql.toString();
	}
	/**
	 * 获取本级、下级
	 * @param unitIdByBusi
	 * @return sql 权限过滤sql
	 */
    public String getB0110Sql_down(String unitIdByBusi) {
    	StringBuilder sql = new StringBuilder();
    	
    	try{
    		String[] tmp = unitIdByBusi.split("`");
    		sql.append(" and (");
    		for(int i=0; i<tmp.length; i++){
    			String b = tmp[i].substring(2);
    			sql.append("w03.b0110 like '"+b+"%' or ");//本级、下级
    		}
    		sql.append("NULLIF(w03.b0110,'') is NULL");
    		sql.append(") ");
    		
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	return sql.toString();
	}
	/**
	 * 获取人员库转化map
	 * 
	 * @return
	 */
	public HashMap<String, String> getDbname() {

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
		} finally {
			PubFunc.closeDbObj(rs);
		}

		return map;
	}

	/**
	 * 学科专业组
	 * 
	 * @return
	 */
	public ArrayList<HashMap> getProfessionGroup() {

		ArrayList<HashMap> list = new ArrayList<HashMap>();

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String sql = "select group_id,group_name from zc_subjectgroup where group_id in (select group_id from zc_expert_user)";
			rs = dao.search(sql.toString());
			while (rs.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("dataValue", rs.getString("group_id"));
				map.put("dataName", rs.getString("group_name"));
				list.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}

		return list;
	}
	
	/**
	 * 学科专业组
	 * 
	 * @return
	 */
	public ArrayList<HashMap> getProfessionGroupByW0301(String w0301) {

		ArrayList<HashMap> list = new ArrayList<HashMap>();

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String sql = "select A.group_id,A.group_name from zc_subjectgroup A where group_id in(select group_id from zc_expert_user where W0301=?)";
			ArrayList<String> sqlList = new ArrayList<String>();
			sqlList.add(w0301);
			rs = dao.search(sql.toString(), sqlList);
			while (rs.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("myId", rs.getString("group_id"));
				map.put("displayText", rs.getString("group_name"));
				list.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}

		return list;
	}

	/**
	 * 问卷模板取得
	 * 
	 * @return
	 */
	public ArrayList<HashMap> getQnPlan() {

		ArrayList<HashMap> list = new ArrayList<HashMap>();

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String sql = "select * from qn_plan where status=1";// 问卷表
			rs = dao.search(sql.toString());
			while (rs.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("dataValue", rs.getString("planId"));// 问卷号
				map.put("dataName", rs.getString("planName"));// 问卷名称
				list.add(map);
			}
			if(list.size()>0){//评价表添加空选项用于取消选择评价表 haosl 20160907
				HashMap map = new HashMap();
				map.put("dataValue", "0");
				map.put("dataName", "　");
				list.add(0, map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}

		return list;
	}

	/**
	 * 文件是否存在
	 * 
	 * @param sPath
	 *            文件路径
	 * @return 存在返回true，否则返回false
	 */
	public boolean isExistsFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 存在
		if (file.isFile() && file.exists()) {
			flag = true;
		}

		return flag;
	}
	/**初始化列对象ColumnsInfo
	 * @param fi
	 * @param columnWidth
	 * @return
	 */
	private ColumnsInfo getColumnsInfoByFi(FieldItem fi, int columnWidth){
		ColumnsInfo co = new ColumnsInfo();
		
		String itemid = fi.getItemid();
		String itemdesc = fi.getItemdesc();
		String codesetId = fi.getCodesetid();
		String columnType = fi.getItemtype();
		int columnLength = fi.getDisplaywidth();// 显示长度
		int decimalWidth = fi.getDecimalwidth();// 小数位
		co = getColumnsInfo(itemid, itemdesc, columnWidth, codesetId,
				columnType, columnLength, decimalWidth);
		
		return co;
	}
	/***
	 * 
	 * @Title: asyncstatus   
	 * @Description: 计算是否通过   01 通过 02 未通过 
	 * @param  
	 * @return void    
	 * @throws
	 * changxy 
	 */
	public void asyncStatus(String w0301){
      ContentDAO dao=new ContentDAO(this.conn);
      RowSet row=null;
	  /*外部鉴定专家      学科组     评委会     学院聘任组*/
	  String sql="select * from w05,w03 where w05.w0301 = '"+w0301+"' "
	  			+ "and w03.w0301=w05.w0301"
	  			+" and w03.W0321 in ('05')";//只同步进行中的会议 haosl 20161124
	  try {
		  row=dao.search(sql);
		  ArrayList<Map<String,String>> list=new ArrayList<Map<String,String>>();
		  while (row.next()) {
			  String W0533=null;//外部鉴定专家状态
			  String W0557=null;//学科组状态
			  String W0559=null;//评委会状态
			  String W0569=null;//学院任聘组状态
			Map<String,String> map=new HashMap<String,String>();
			// 高评委总人数改用w0315。因为业务修改把生成密码逻辑移到了上会材料页面，导致如果没有生成高评委账号密码时w0517是空的 chent 20180131 modify
			int W0315=row.getInt("W0315"); //评委人数 （总）
			//int W0517=row.getInt("W0517"); //评委人数 （总）
			int W0521=row.getInt("W0521"); //学科组人数（总）
			int W0523=row.getInt("W0523"); //外部专家人数（总）
			int W0571=row.getInt("W0571"); //学院任聘组人数（总）
		
			int W0531=row.getInt("W0531");//外部专家赞成人数
			int W0527=row.getInt("W0527");//外部专家反对人数
			int W0529=row.getInt("W0529");//外部专家弃权人数
			int expertsNum = W0531+W0527+W0529;//外部专家已评人数
			
			int W0547=row.getInt("W0547");//学科组赞成人数
			int W0543=row.getInt("W0543");//学科组反对人数
			int W0545=row.getInt("W0545");//学科组赞成人数
			int subjectsNum = W0547+W0543+W0545;//学科组已评人数
			
			int W0553=row.getInt("W0553");//评委会赞成人数
			int W0549=row.getInt("W0549");//评委会反对人数
			int W0551=row.getInt("W0551");//评委会弃权人数
			int committeeNum = W0553+W0549+W0551;//评委会已评人数
			
			int W0567=row.getInt("W0567");//学院任聘组赞成人数
			int W0563=row.getInt("W0563");//学院任聘组反对人数
			int W0565=row.getInt("W0565");//学院任聘组弃权人数
			int collegeNum = W0567+W0563+W0565;//学院任聘组已评人数
			
			String w0525=row.getString("w0525");//导入标识
			boolean expertsFlg = "1".equals(w0525.substring(0, 1))?true:false;//外部专家、是否是导入的数据
			boolean subjectsFlg = "1".equals(w0525.substring(1, 2))?true:false;//专业学科组
			boolean committeeFlg = "1".equals(w0525.substring(2, 3))?true:false;//评委会
			boolean collegeFlg = "1".equals(w0525.substring(3, 4))?true:false;//学院评委会
			
			String W0501=row.getString("W0501");//主键
			//已评人数不为0时计算是否通过 为0时直接置为"" 
			//已评人数的2/3+1 < 赞成人数 通过 否则 不通过
			if(W0523!=0 && (expertsNum!=0 || expertsFlg)){//外部专家
				if(W0531 >= Math.ceil((double)((double)(expertsNum*2)/3)))
					W0533="01";
				else
					W0533="02";
			}else{
				W0533="";
			}
			
			if(W0521!=0 && (subjectsNum!=0 || subjectsFlg)){//学科组
				if(W0547 >= Math.ceil((double)((double)(subjectsNum*2)/3)))
					W0557="01";
				else
					W0557="02";
			}else{
				W0557="";
			}
			
			if(W0315!=0 && (committeeNum!=0 || committeeFlg)){//评委会
				if(W0553 >= Math.ceil((double)((double)(committeeNum*2)/3)))
					W0559="01";
				else
					W0559="02";
			}else{
				W0559="";
			}
			
			if(W0571!=0 && (collegeNum!=0 || collegeFlg)){//学院任聘组(二级单位)
				if(W0567 >= Math.ceil((double)((double)(collegeNum*2)/3)))
					W0569="01";
				else
					W0569="02";
			}else{
				W0569="";
			}
			map.put("W0501", W0501);
			map.put("W0533", W0533);
			map.put("W0557", W0557);
			map.put("W0559", W0559);
			map.put("W0569", W0569);
			list.add(map);
		  }
		  
		//haosl 修改 20161124
	    List<List<String>> vlist = new ArrayList<List<String>>();
		String upSql = "update w05 set w0533=?,w0557=?,w0559=?,w0569=? where w0501=?";
		for (int i = 0; i < list.size(); i++) {
			Map<String, String> map=list.get(i);
			//修改各个评审阶段的状态 
			List<String> values = new ArrayList<String>();
			values.add(map.get("W0533"));
			values.add(map.get("W0557"));
			values.add(map.get("W0559"));
			values.add(map.get("W0569"));
			values.add(map.get("W0501"));
	    	vlist.add(values);
		}
		if(vlist.size()>0)
			dao.batchUpdate(upSql, vlist);
		  
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		PubFunc.closeDbObj(row);
	}
		
	}
	
	/**
	 * 同步鉴定专家人数、学科组人数、聘委会人数,学院聘任组人数
	 */
	public void asyncPersonNum(String w0301){
		this.asyncPersonNum(w0301,1);
		this.asyncPersonNum(w0301,2);
		this.asyncPersonNum(w0301,3);
		this.asyncPersonNum(w0301,4);
	}
	/**
	 * 同步鉴定专家人数、学科组人数、聘委会人数
	 * @param type:1:聘委会 2:学科组 3:鉴定专家 4:学院聘任组
	 */
	public void asyncPersonNum(String w0301,int type){
		
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			/** 同步总人数 */ 
			String sql = "";
			String totalItem = "";//总人数
			String agreeItem = "";//赞成人数
			String disagreeItem = "";//反对人数
			String giveupItem = "";//弃权人数
			if(type == 1) {
				totalItem = "W0517";
				agreeItem = "W0553";
				disagreeItem = "W0549";
				giveupItem = "W0551";
				
			} else if(type == 2) { 
				totalItem = "W0521";
				agreeItem = "W0547";
				disagreeItem = "W0543";
				giveupItem = "W0545";
				
			} else if(type == 3) { 
				totalItem = "W0523";
				agreeItem = "W0531";
				disagreeItem = "W0527";
				giveupItem = "W0529";
			}else if(type == 4){
				totalItem = "W0571";
				agreeItem = "W0567";
				disagreeItem = "W0563";
				giveupItem = "W0565";
			}
			//只同步进行中的会议 haosl 20161124
			sql = "select w05.w0501,w05.w0539,w05.w0541,w05.w0525,w05.group_id,w0315,w0323 from w05,w03 where w05.w0301 = '"+w0301+"' "
	  			+ "and w03.w0301=w05.w0301"
	  			+" and w03.W0321 in ('05')";
			rs = dao.search(sql);
			List<String> sqls = new ArrayList<String>();
			while(rs.next()){
				
				String w0501 = rs.getString("w0501");
				
				// 查看导入标识，导入的数据不校验人数 chent 20160823 start
				String w0525 = rs.getString("w0525");//导入标识
				if(StringUtils.isNotEmpty(w0525)){
					boolean expertsFlg = "1".equals(w0525.substring(0, 1))?true:false;//外部专家、是否是导入的数据
					boolean subjectsFlg = "1".equals(w0525.substring(1, 2))?true:false;//专业学科组
					boolean committeeFlg = "1".equals(w0525.substring(2, 3))?true:false;//评委会
					boolean collegeFlg = "1".equals(w0525.substring(3, 4))?true:false;//学院评委会
					if((type==1 && committeeFlg) || (type==2 && subjectsFlg) || (type==3 && expertsFlg) || (type==4 && collegeFlg)){//导入数据
						continue ;
					}else{
						sql = "update W05 set "+totalItem+"=(select count(user_id) From zc_expert_user where W0301=w05.w0301 and type="+type;
						if(type==2){
							String group_id = rs.getString("group_id");
							if(StringUtils.isEmpty(group_id)) {
								group_id = "";
							}
							sql +=" and group_id='"+group_id+"'";
						}
						if(type==3){//同行专家需要根据w0501统计专家数
							sql +=" and  W0501='"+w0501+"'";
						}else {
							sql +=" and  W0501='xxxxxx'";
							
						}
						sql+= ") where w0301='"+w0301+"' and W0501='"+w0501+"'";
						if(type==2)//学科组阶段
						   sql+=" and group_id='"+rs.getString("group_id")+"'";
						dao.update(sql.toString());
					}
				}
				// 查看导入标识，导入的数据不校验人数 chent 20160823end
				StringBuffer upSql = new StringBuffer();
				upSql.append("update W05 set ");
				/** 同步二级单位人数和评委会人数*/
				if(type==1)
					upSql.append(totalItem+"="+rs.getInt("w0315")+",");
				else if(type==4)
					upSql.append(totalItem+"="+rs.getInt("w0323")+",");
				/** 同步赞成人数 */
				upSql.append(agreeItem+" = (select COUNT(*) count from zc_data_evaluation a "); 
				upSql.append("left join zc_expert_user b on a.W0501=b.W0501 and a.W0301=b.W0301 and a.username=b.username "); 
				upSql.append("where expert_state='3' and approval_state='1' and type="+type+" and a.w0501=w05.w0501), "); 
			
				/** 同步反对人数 */ 
				upSql.append(disagreeItem+" = (select COUNT(*) count from zc_data_evaluation a "); 
				upSql.append("left join zc_expert_user b on a.W0501=b.W0501 and a.W0301=b.W0301 and a.username=b.username "); 
				upSql.append("where expert_state='3' and approval_state='2' and type="+type+" and a.w0501=w05.w0501), "); 
				
				/** 同步弃权人数 */ 
				upSql.append(giveupItem+" = (select COUNT(*) count from zc_data_evaluation a ");
				upSql.append("left join zc_expert_user b on a.W0501=b.W0501 and a.W0301=b.W0301 and a.username=b.username "); 
				upSql.append("where expert_state='3' and approval_state='3' and type="+type+" and a.w0501=w05.w0501) "); 
				upSql.append("where w05.W0501 = '"+w0501+"' and w05.w0301='"+w0301+"'");
				
				sqls.add(upSql.toString());
			}
			if(sqls.size()>0)
				dao.batchUpdate(sqls);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
	}
	/**
	 * 生成聘委会账号密码(人事异动模块上会时调用)
	 * @param w0501：申报人主键序号ID 
	 * @param w0301：会议Id
	 * @return
	 */
	public String createUserPwd(String w0501, String w0301) throws GeneralException {
		String msg = "";
		
		this.createUserPwd("1", w0501, w0301, "");
		
		return msg;
	}
	/**
	 * 生成用户名、密码
	 * @param type
	 * @param w0501
	 * @param w0301
	 * @param group_id
	 * @param useType 
	 * 			帐号区分
	 * @return
	 * @throws GeneralException
	 */
	public String createUserPwd(String type, String w0501, String w0301, String group_id) throws GeneralException {
		String msg = "";
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			/** 删除曾经数据 */
			String dSql = "delete from zc_expert_user where w0301=? and w0501=? and type=? and "+Sql_switcher.isnull("usetype", "1")+"=1";
			ArrayList<String> dList = new ArrayList<String>();
			dList.add(w0301);
			dList.add(w0501);
			dList.add(type);
			dao.delete(dSql, dList);
			
			/** 插入生成的数据 */
			StringBuilder insertSql = new StringBuilder();
			insertSql.append("insert into zc_expert_user ");
			insertSql.append("(user_id,group_id,W0301,W0501,username,password,state,type,W0101,description,role,usetype) ");
			insertSql.append("values ");
			insertSql.append("(?,?,?,?,?,?,?,?,?,?,?,?)");
			
			String valueSql = "";
			if("1".equals(type)) {//聘委会
				valueSql = "select group_id,W0301,username,password,state,type,W0101,description,role from zc_expert_user where "+Sql_switcher.isnull("usetype", "1")+"=1 and w0301='"+w0301+"' and type="+type+" and w0501='xxxxxx' and w0101 is not NULL";
			}else if("2".equals(type)){//学科组
				valueSql = "select group_id,W0301,username,password,state,type,W0101,description,role from zc_expert_user where "+Sql_switcher.isnull("usetype", "1")+"=1 and group_id='"+group_id+"' and w0301='"+w0301+"' and type=2 and w0501='xxxxxx' and w0101 is not NULL";
			}
			rs = dao.search(valueSql);
			ArrayList<ArrayList<String>> iList = new ArrayList<ArrayList<String>>();
			while(rs.next()){
				ArrayList vList = new ArrayList();
				IDFactoryBean idf = new IDFactoryBean();
				String user_id = idf.getId("zc_expert_user.user_id", "", this.conn);
				vList.add(user_id);
				vList.add(rs.getString("group_id"));
				vList.add(rs.getString("W0301"));
				vList.add(w0501);
				vList.add(rs.getString("username"));
				vList.add(rs.getString("password"));
				vList.add(rs.getString("state"));
				vList.add(rs.getString("type"));
				vList.add(rs.getString("W0101"));
				vList.add(rs.getString("description"));
				vList.add(rs.getString("role"));
				vList.add(1);
				iList.add(vList);
			}
			dao.batchInsert(insertSql.toString(), iList);
		} catch(SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return msg;
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
	 * 获取归档子集数据源
	 * @return archivedata 数据源
	 * @throws GeneralException
	 */
	public ArrayList<HashMap<String, String>> getArchivedata() throws GeneralException {

    	
    	ArrayList<HashMap<String, String>> archivedata = new ArrayList<HashMap<String, String>>();
    	
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
		try {
			String sql = "select fieldsetid,fieldsetdesc From fieldSet where fieldsetid like 'A%' and fieldsetid not in ('A01') order by Displayorder ";
	    	
			rs = dao.search(sql);
			
			while (rs.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
		    	map.put("fieldsetid", rs.getString("fieldsetid"));//指标集编号
		    	map.put("fieldsetdesc", rs.getString("fieldsetdesc"));//名称
				
		    	archivedata.add(map);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return archivedata;
    
	}
	/**
	 * 获取目的指标数据源
	 * @return objectivedata 数据源
	 * @throws GeneralException
	 */
	public ArrayList<HashMap<String, String>> getObjectivedata(String fieldsetid) throws GeneralException {
		
		
		ArrayList<HashMap<String, String>> objectivedata = new ArrayList<HashMap<String, String>>();
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String sql = "select itemid,itemdesc,itemtype,codesetid From fielditem where fieldsetid=? and useflag = 1";
			ArrayList<String> list = new ArrayList<String>();
			list.add(fieldsetid);
			
			rs = dao.search(sql, list);
			//目标指标加空选项，一遍清楚已选指标  haosl  2017-9-29
			HashMap<String, String> nullmap = new HashMap<String, String>();
			nullmap.put("itemid", "　");//指标编号
			nullmap.put("itemdesc", "　");//名称
			objectivedata.add(nullmap);
			while (rs.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("itemid", rs.getString("itemid"));//指标编号
				map.put("itemdesc", rs.getString("itemdesc"));//名称
				map.put("itemtype", rs.getString("itemtype"));//类型
				map.put("codesetid", rs.getString("codesetid"));//代码型对应代码类
				
				objectivedata.add(map);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return objectivedata;
		
	}
	/**
	 * 从静态变量里读取评审结果归档方案内容
	 * @param reviewArchiveStr
	 * 		归档方案的源指标串
	 * @return
	 */
	public HashMap getResultsArchivingConfig(String reviewArchiveStr){
		HashMap hashMap = new HashMap();
		ConstantXml constantXml = new ConstantXml(conn, "JOBTITLE_CONFIG");
		List listEl = constantXml.getAllChildren("//params");
		List<String> list = getResultsArchivingList(reviewArchiveStr);
		List srcList = new ArrayList();	//存放归档源指标
		for(int i=1; i<list.size();i++){
			String value = list.get(i);
			FieldItem item = new FieldItem();
			item.setItemid(value);
			item.setItemtype("N");
			item.setFieldsetid("0");
			//【48372 】V76职称评审：评审会议归档方案中应将“单位、部门、姓名”三列进行隐藏，因为此项配置是针对评审人得评审会议环节以及状态太进行归档，不需要再对人员信息进行匹配
			//w0535:评审材料 |w0536:word模板|w0537:论文送审材料|w0539:内部评审问卷计划号|w0541:专家鉴定问卷计划号
			if("w0507".equalsIgnoreCase(item.getItemid()) || "w0509".equalsIgnoreCase(item.getItemid()) || "w0511".equalsIgnoreCase(item.getItemid()) ||
					"w0535".equalsIgnoreCase(item.getItemid()) || "w0536".equalsIgnoreCase(item.getItemid()) || "w0537".equalsIgnoreCase(item.getItemid()) ||
					"w0539".equalsIgnoreCase(item.getItemid()) || "w0541".equalsIgnoreCase(item.getItemid())) {
				continue;
			}
			
			if("attendance_1".equalsIgnoreCase(value)){//attendance_1:参会人数（评委会）
				item.setItemdesc("参会人数");
				srcList.add(item);
				
			} else if("attendance_2".equalsIgnoreCase(value)) {// attendance_2：:参会人数（学科组）
				item.setItemdesc("参会人数");
				srcList.add(item);
			} else if("attendance_3".equalsIgnoreCase(value)) {// attendance_3：:参会人数（同行专家）
				item.setItemdesc("参会人数");
				srcList.add(item);
			} else if("attendance_4".equalsIgnoreCase(value)) {// attendance_4：:参会人数（二级单位）
				item.setItemdesc("参会人数");
				srcList.add(item);
			} else {
				srcList.add(DataDictionary.getFieldItem(value, 1));
			}
		}
		//haosl 20161117
		Element archiving = constantXml.getElement("/params/archiving");
		if(archiving!=null){
			for (int i = 0; i < list.size(); i++) {//获得配置的archiving的所有属性的值
				hashMap.put(list.get(i), archiving.getAttributeValue(list.get(i)));
			}
			HashMap<String, String> map = new HashMap<String, String>();
			
			String fieldset = archiving.getAttributeValue("fieldset");//归档子集
			for(int i=1; i<list.size()&&fieldset!=null; i++){
				String value = list.get(i);	//归档源指标的id
				String name = "";
				String item = archiving.getAttributeValue(value);
				DbWizard dbWizard = new DbWizard(this.getConn());
				if(StringUtils.isNotBlank(item) && dbWizard.isExistTable("usr"+fieldset, false) && DataDictionary.getFieldItem(item, fieldset) != null){
					name = DataDictionary.getFieldItem(item, fieldset).getItemdesc();
					map.put(item, name);	//haosl 20160820
				}
			}
			hashMap.put("descinfo", map);
 	    }
		hashMap.put("configArrayName",list);
		hashMap.put("srcList",srcList);
		return hashMap;
	}
	/**
	 * 根据规则串获取评审结果归档所有属性名称
	 * @return List<String>
	 */
	public List<String> getResultsArchivingList(String reviewArchiveStr){
		List<String> list = new ArrayList<String>();
		try {
			list.add("fieldset");
			// 获取W05 业务字典指标 	add linbz
			ArrayList fieldList = DataDictionary.getFieldList("W05", 1);
			for (int i = 0; fieldList!=null && i < fieldList.size(); i++) {
				FieldItem fi = (FieldItem) fieldList.get(i);
				// 去除没有启用的指标
				if (!"1".equals(fi.getUseflag()))
					continue;
				// 去除隐藏的指标
				if (!"1".equals(fi.getState()))
					continue;
				String itemid = fi.getItemid();
				// 这里的参会人数过滤掉，下面有对应各个阶段的参会人数 || 过滤参数配置的id
				if("w0519".equalsIgnoreCase(itemid)
						|| (","+reviewArchiveStr.toLowerCase()+",").contains(","+itemid.toLowerCase()+",")
                        || "w0536".equalsIgnoreCase(itemid))
					continue;
				// 添加id时需大写，兼容之前的配置格式
				list.add(itemid.toUpperCase());
			}
			
			if(reviewArchiveStr!=null && !"".equals(reviewArchiveStr.trim())){
				// 所有属性名称
				String[] items = reviewArchiveStr.split(",");
				list.addAll(Arrays.asList(items));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 通过业务模板号获取模板名称
	 * @param tabId 业务模板号
	 * @return tebName
	 */
	public String getTabNameByTabId(String tabId) throws GeneralException {
		
		String name = "";
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String sql = "select name From template_table where tabid=?";
			ArrayList<String> list = new ArrayList<String>();
			list.add(tabId);
			
			rs = dao.search(sql, list);
			
			while (rs.next()) {
				name = rs.getString("name");//模板名称
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		
		return name;
	}
	/**
	 * 获取鉴定专家页面列头
	 * @param columnfield：列头字段
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList<ColumnsInfo> getCheckProficientColumnList(String columnfield)  throws GeneralException {
		
		ArrayList<ColumnsInfo> columnsList = new ArrayList<ColumnsInfo>();
		
		String[] fieldArray = columnfield.split(",");
		for(int i=0; i<fieldArray.length; i++){
			if(StringUtils.isEmpty(fieldArray[i])) {
				continue ;
			}
			String fielditem = fieldArray[i].split(":")[0];
			String fieldiName = fieldArray[i].split(":")[1];
			
			ColumnsInfo columnsInfo = new ColumnsInfo();
			columnsInfo = getColumnsInfo(fielditem, fieldiName, 100, "0", "A", 6, 0);

			// 账号启用和禁用状态
			if("state".equalsIgnoreCase(fielditem)) {
				columnsInfo.setColumnType("N");
				columnsInfo.setOperationData("outproficientstate");
				columnsInfo.setColumnWidth(60);
			}
			// 是否已提交状态
			if("subflag".equalsIgnoreCase(fielditem)) {
				columnsInfo.setColumnType("N");
				columnsInfo.setOperationData("subflag");
				columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//隐藏是否提交列 haosl 20161013
				columnsInfo.setColumnWidth(80);
			}
			
			// 描述列为大文本
			if("description".equalsIgnoreCase(fielditem)) {
				columnsInfo.setColumnType("M");
			}
			
			// 除了【账号】、【密码】、【描述】列。其他都不可编辑
			if(!"username".equalsIgnoreCase(fielditem) && !"password".equalsIgnoreCase(fielditem) && !"description".equalsIgnoreCase(fielditem)) {
				columnsInfo.setEditableValidFunc("false");
			}else{
				columnsInfo.setEditableValidFunc("outProficient_me.checkCell");//【账号】、【密码】、【描述】添加校验列
				if(!"description".equalsIgnoreCase(fielditem))
					columnsInfo.setValidFunc("outProficient_me.validfunc");
			}
			
			// 加密列
			if("w0501".equalsIgnoreCase(fielditem) || "w0301".equalsIgnoreCase(fielditem) || "user_id".equalsIgnoreCase(fielditem)){
				columnsInfo.setEncrypted(true);
				columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			}
			columnsList.add(columnsInfo);
		}
		
		return columnsList;
	}
	/**
	 * 获取鉴定专家页面sql
	 * @param columnfield:需要显示的列头，暂时没用
	 * @param w0501：申请人编号
	 * @param w0301：评审会议编号
	 * @return：sql
	 */
	public String getCheckProficientSql(String columnfield, String w0501, String w0301){
		String sql = "";
		
		sql = "select user_id,A.username,password,state,description,w01.w0103,w01.w0105,w01.w0107,A.w0501,A.w0301";
		sql += " ,case when B.expert_state=3 then 3 else 1 end as subflag ";//除3之外的值都是1
		sql += " from zc_expert_user A";
		sql += " left join w01 on A.w0101=w01.w0101 ";
		sql += " left join zc_data_evaluation B on A.w0501=B.w0501 and A.username=B.username";
		sql += " where ";
		sql += " A.W0301='"+w0301+"'";
		sql += " and A.W0501='"+w0501+"'";
        sql += " and A.type=3";//外部专家
		return sql;
	}
	/**
	 * 获取鉴定专家页面按钮
	 * @return
	 */
	public ArrayList<Object> getCheckProficientButtonList() {

		ArrayList<Object> buttonList = new ArrayList<Object>();
		
		ButtonInfo buttonInfo = new ButtonInfo("新增", "outProficient_me.addComputeCond");
		buttonList.add(buttonInfo);
	
		buttonInfo = new ButtonInfo("删除", ButtonInfo.FNTYPE_DELETE, "ZC00003004");
		buttonList.add(buttonInfo);
		
		buttonInfo = new ButtonInfo("保存", ButtonInfo.FNTYPE_SAVE, "ZC00003003");
		buttonList.add(buttonInfo);
		
		buttonInfo = new ButtonInfo("引入专家", "outProficient_me.importExpert");
		buttonList.add(buttonInfo);
		
		return buttonList;
	}

	
	/**
     * 上会材料--导入数据，浏览上传
     * 
     * @param wb  获取的excel
     * @return importMsgList 存放返回信息记录
     */
	public ArrayList<String> importTemplate(Workbook wb) throws GeneralException{
	    ArrayList<String> importMsgList = new ArrayList<String>();//存放返回信息记录
	    String importMsg = "";
	    Sheet sheet = null;
	    RowSet rs = null;
    	try{
    	    sheet = wb.getSheetAt(0);
            
            Row row = sheet.getRow(0);//得到第一行
        
    //        int cols = row.getPhysicalNumberOfCells();//总列数
    //        int rows = sheet.getPhysicalNumberOfRows();//总行数
            //需要从第三行计算列数，前两行有合并列
            int cellNum = sheet.getRow(2).getPhysicalNumberOfCells();//有合并列，从第三行取记录的所有列数
            //取得所有指标名称
            HashMap indexOfFieldMap = new HashMap();
            for (int j = 0; j < cellNum; j++) {
                String itemid = "";
                Cell cell = row.getCell(j);
                if(cell != null&&cell.getCellComment()==null){
                    Row rowTwo = sheet.getRow(1);//得到第2行,其中有合并行
                    cell = rowTwo.getCell(j);
                }else if(cell == null)
                	continue;
                if(cell.getCellComment()==null){
                    continue;
                }
                String cellComment = cell.getCellComment().getString().toString();// 得到comment
                String[] commentValueArr = cellComment.split("`");
                itemid = commentValueArr[0];
                
    //            System.out.println(j+"-------------"+itemid);
                indexOfFieldMap.put("" + j, itemid);
            }
            //先判断记录是否包含主键w0501,来判断Excel模板是否正确
            ArrayList<String> colNameList = new ArrayList<String>();
            for (int j = 0; j < cellNum; j++) {// 列循环
                String colName = (String) indexOfFieldMap.get("" + j);
                colNameList.add(colName);
            }
            if(!colNameList.contains("w0501")){
                importMsg = "请使用该页面下载的Excel模板来导入数据！";
                importMsgList.add(importMsg);
                return importMsgList;
            }
            
            /** 开始处理每一个记录begin **/
            ArrayList recordList = new ArrayList();
            ArrayList<String> indexList = new ArrayList<String>();//取得的指标名称(如果有多条记录，只取一次)
            HashMap recordListMap = new HashMap();//(指标名称.指标类型)
            ArrayList<HashMap> indexitemtypeList = new ArrayList<HashMap>();//取得的（指标名，类型）list
            int realNum = 0;//等于1，执行成功
            int num = 0;//总共导入多少条记录
            boolean flag = true;
            RecordVo w05Vo = new RecordVo("w05");
            HashMap recordMap = new HashMap();
            for (int i = 2; i < sheet.getPhysicalNumberOfRows(); i++) {// 页中的每一行(除了表头行2行)
            	String w0501 = "";//申请人主键
                //每次循环一行记录，新建一个集合
                recordList = new ArrayList();
                row = sheet.getRow(i);
                Cell isnull = row.getCell(0);
                if(isnull == null){
                    continue;
                }
                
                /** 检查导入哪些阶段：学院聘任组、外部专家、学科组、评委会 chent 20160823 start */
                boolean collegeFlg = false;// 学院
                boolean expertsFlg = false;// 外部专家
                boolean subjectsFlg = false;// 学科组
                boolean committeeFlg = false;// 评委会
                
                for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {// 列循环
                	 Cell colCell = row.getCell(j);
                	 String colName = (String) indexOfFieldMap.get("" + j);
            	 	if(!collegeFlg && ("w0567".equalsIgnoreCase(colName) || "w0563".equalsIgnoreCase(colName) || "w0565".equalsIgnoreCase(colName))){// 学院
            	 		if(colCell.getCellType() != Cell.CELL_TYPE_BLANK){
            	 			collegeFlg = true;
            	 		}
        	 		}
            	 	if(!expertsFlg && ("w0531".equalsIgnoreCase(colName) || "w0527".equalsIgnoreCase(colName) || "w0529".equalsIgnoreCase(colName))){// 外部专家
            	 		if(colCell.getCellType() != Cell.CELL_TYPE_BLANK){
            	 			expertsFlg = true;
            	 		}
                	}
            	 	if(!subjectsFlg && ("w0547".equalsIgnoreCase(colName) || "w0543".equalsIgnoreCase(colName) || "w0545".equalsIgnoreCase(colName))){// 学科组
            	 		if(colCell.getCellType() != Cell.CELL_TYPE_BLANK){
            	 			subjectsFlg = true;
            	 		}
            	 	}         		 
					                	 
					if(!committeeFlg && ("w0553".equalsIgnoreCase(colName) || "w0549".equalsIgnoreCase(colName) || "w0551".equalsIgnoreCase(colName))){// 评委会
						if(colCell.getCellType() != Cell.CELL_TYPE_BLANK){
							committeeFlg = true;
            	 		}
					}
                }
                
                
                
                /** 检查导入哪些阶段：学院聘任组、外部专家、学科组、评委会 chent 20160823 end */
                
                
                for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {// 列循环
                    importMsg = "";
                    Cell colCell = row.getCell(j);
                    //获取的每列 指标名称
                    String colName = (String) indexOfFieldMap.get("" + j);
                    if("w0301".equalsIgnoreCase(colName)){
                    	String value = colCell.getStringCellValue();//单元格内容
                    	if(StringUtils.isNotEmpty(value)){
                    		ContentDAO dao = new ContentDAO(conn);
                    		rs = dao.search("select W0321 from W03 where W0301='"+ value +"'");
                    		if(rs.next()){
                    			String w0321 = rs.getString("W0321");//会议状态
                    			if("06".equals(w0321)){
                    				importMsg = "结束的评审会议不允许导入！";
                                    importMsgList.add(importMsg);
                                    return importMsgList;
                    			}
                    		}
                    	}
                    }
                    if("w0501".equalsIgnoreCase(colName)){
                        w0501 = colCell.getStringCellValue();
                        w05Vo.setString(colName.toLowerCase(), w0501);
                        if(i == 2){
                            indexList.add(colName);
                        }
                    }
                    // 锁定的列  部分隐藏的列 直接跳过，不用update
                    if (ReviewFileTrans.exportIslock.indexOf("," + colName + ",") != -1 || ",proficientagree,subjectsagree,committeeagree,".indexOf("," + colName + ",") != -1) {
                        continue;
                    }
                    if (colName == null) 
                        continue;
                    
                    FieldItem item = DataDictionary.getFieldItem(colName);
                    if(item == null)
                        continue;
                    
                    // 评审环节中，数据全部为空的，视为不导入该环节，跳过 chent 20160823 start
                    if(!collegeFlg && ("W0567".equalsIgnoreCase(colName) || "W0563".equalsIgnoreCase(colName) || "W0565".equalsIgnoreCase(colName))){
                    	continue ;
                    }
                    if(!expertsFlg && ("W0531".equalsIgnoreCase(colName) || "W0527".equalsIgnoreCase(colName) || "W0529".equalsIgnoreCase(colName))){
                    	continue ;
                    }
                    if(!subjectsFlg && ("W0547".equalsIgnoreCase(colName) || "W0543".equalsIgnoreCase(colName) || "W0545".equalsIgnoreCase(colName))){
                    	continue ;
                    }
                    if(!committeeFlg && ("W0553".equalsIgnoreCase(colName) || "W0549".equalsIgnoreCase(colName) || "W0551".equalsIgnoreCase(colName))){
                    	continue ;
                    }
                    // 评审环节中，数据全部为空的，视为不导入该环节，跳过 chent 20160823 end
                    
                    //数据类型
                    String itemtype = item.getItemtype();
                    String itemdesc = item.getItemdesc();
                    String codesetid = item.getCodesetid();
                    
                    if (",w0539,w0541,".indexOf("," + colName + ",") != -1) {
                        String value1 = colCell.getStringCellValue();
                        String[] commentValueArr = value1.split(":");
                        String value = commentValueArr[0];
                        w05Vo.setInt(colName.toLowerCase(), StringUtils.isEmpty(value)?0:Integer.parseInt(value));
                        if(i == 2){
                            indexList.add(colName);
                        }
                        recordListMap.put(colName, itemtype);
                        continue;
                    }
                    if ("A".equalsIgnoreCase(itemtype)) {
                        if(!("0".equalsIgnoreCase(codesetid))&&codesetid!=null&&!("".equalsIgnoreCase(codesetid))){//如果关联代码类
                            String value = colCell.getStringCellValue();
                            String codeitemid = getCodeByDesc(value, codesetid);//取得对应codeitemid
                            w05Vo.setString(colName.toLowerCase(), codeitemid);
                        }else{
                            String value = colCell.getStringCellValue();
                            w05Vo.setString(colName.toLowerCase(), value);
                        }
                    } else if ("D".equalsIgnoreCase(itemtype)) {
                        String value = colCell.getStringCellValue();
                    	try{
                    		w05Vo.setDate(colName.toLowerCase(), value);
                    	}catch (Exception e) {
                    		importMsg = "第"+(i-1)+"条记录的【"+itemdesc+"】列，日期格式错误！";
						}
                    } else if ("N".equalsIgnoreCase(itemtype)) {
                        int decimalwidth = item.getDecimalwidth();
                        int itemlength = item.getItemlength();//整数长度
                        if (decimalwidth == 0) {
                            try {
                                    int value = (int) colCell.getNumericCellValue();//直接取得就是整数
                                    if(String.valueOf(value).length() > itemlength){
                                        importMsg = "第"+(i-1)+"条记录的【"+itemdesc+"】列，最大长度为"+itemlength+"位！";
                                    }else{
                                        w05Vo.setInt(colName.toLowerCase(), value);
                                    }
                                } catch (Exception e) {
                                    importMsg = "第"+(i-1)+"条记录的【"+itemdesc+"】列，请输入数值型！";
    //                                return;
                                }
                        } else {
                            try {
                                double value = colCell.getNumericCellValue();
                                w05Vo.setDouble(colName.toLowerCase(), value);
                            } catch (Exception e) {
                                importMsg = "第"+(i-1)+"条记录的【"+itemdesc+"】列，请输入数值型！";
                            }
                        }
                    } else {
                        String value = colCell.getStringCellValue();
                        w05Vo.setString(colName.toLowerCase(), value);
                    }
                    if(i == 2){
                        indexList.add(colName);
                    }
                    recordListMap.put(colName, itemtype);
                    
                    if(!"".equalsIgnoreCase(importMsg)){
                        importMsgList.add(importMsg);
                    }
                    
                }
                ArrayList voList = new ArrayList();
                voList.add(w05Vo);
                recordMap.put(sheet.getSheetName(), voList);
                indexitemtypeList.add(recordListMap);
                recordList.add(recordMap);
                // 向数据库中导入数据
                if(importMsgList.size() < 1){
                    realNum = resloveData(recordList, wb, indexList , indexitemtypeList);
                }
                
                if(realNum != 1){
                    flag = false;
                }
                if(flag){
                    num++;
                    // collegeFlg expertsFlg subjectsFlg committeeFlg
                    String w0525 = "0000";
                    String college = collegeFlg?"1":"0";
                    String experts = expertsFlg?"1":"0";
                    String subjects = subjectsFlg?"1":"0";
                    String committee = committeeFlg?"1":"0";
                    w0525 = experts+subjects+committee+college;
                    
                    this.updateW0525(w0501, w0525);// 该条【导入标识】置为“是”
                }
            }
            if(importMsgList.size() < 1){
                if(num == 0){
                    importMsg = "导入失败！";
                }else{
                    importMsg = "成功导入" + num + "条记录！";
                }
                importMsgList.add(importMsg);
            }
    	} catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } 
    	    return importMsgList;
//        }finally {
	}
	/**
     * 获得一条记录，导入数据库
     * 
     * @param recordList 一条记录的集合
     * @param wb  获取的excel
     * @param indexList  需要更新的指标名称集合
     * @param indexitemtypeList （指标名称，指标类型）的集合
     * @return realRecordNumber 录入库中的条数
     */
    private int resloveData(ArrayList recordList, Workbook wb, ArrayList<String> indexList, ArrayList<HashMap> indexitemtypeList) {
        ContentDAO dao = new ContentDAO(this.conn);
        int realRecordNumber = 0;
        Sheet sheet = null;
        try{
            for (int i = 0; i < recordList.size(); i++) {
                // sheet名字，即指标集的名字
                String sheetName = wb.getSheetName(0);
                // 挨着得到每一个sheet
                sheet = wb.getSheet(sheetName);
                
                HashMap recordMap = (HashMap) recordList.get(i);
                
                ArrayList w05list  = (ArrayList) recordMap.get(sheetName);
                RecordVo w05vo = (RecordVo) w05list.get(0);
                
                String w0501 = w05vo.getString("w0501");
                if("".equalsIgnoreCase(w0501) && w0501==null){
                    return 2;
                }
                StringBuffer sql = new StringBuffer();
                ArrayList valuelist = new ArrayList();
                sql.append("update w05 set ");
                
                for(int j = 0; j < indexList.size(); j++){
                    String index = indexList.get(j);
                    if("w0501".equalsIgnoreCase(index.toLowerCase()))
                        continue;
                    String value =  w05vo.getString(index);
                    sql.append(" ");
                    sql.append(index + "=?,");
                    HashMap indexitemtype = indexitemtypeList.get(0);
                    String itemtype = (String) indexitemtype.get(index);
                    if("A".equalsIgnoreCase(itemtype)
                    		||"M".equals(itemtype)
                    		||"D".equals(itemtype)){
                        valuelist.add(value);
                    }else if("N".equalsIgnoreCase(itemtype)){
                    	//len=0位整形，len>0位小数类型  haosl 2017-07-14
                    	int len = DataDictionary.getFieldItem(index).getDecimalwidth();
                    	if(len > 0){
                    		double dvalue = Double.parseDouble(value);
                    		valuelist.add(dvalue);
                    	}else {
                    		int intvalue = Integer.parseInt(value);
                    		valuelist.add(intvalue);
                    	}
                    }
                }
                sql.setLength(sql.length() -1);
                sql.append(" where w0501=?");//根据主键w0501 update
                valuelist.add(w0501);
                
                realRecordNumber  = dao.update(sql.toString(), valuelist);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realRecordNumber;
    }

    /**
     * 获取代码型 数据  codeitemid
     * 
     * @param codeDesc 代码型描述 
     * @param fieldCodeSetId 代码型codesetid
     * @return codeitemid 数据  codeitemid
     */
    private String getCodeByDesc(String codeDesc, String fieldCodeSetId){
    	String tableName = "";
    	//组织机构类型的代码应该查organization表 haosl 2017-08-02 add
    	if("UN".equalsIgnoreCase(fieldCodeSetId) 
    			|| "UM".equalsIgnoreCase(fieldCodeSetId)
    			||"@K".equalsIgnoreCase(fieldCodeSetId))
    		tableName = "organization";
    	else
    		tableName = "codeitem";
    	String	sql="select codeitemid from "+tableName+" where codeitemdesc='"+codeDesc+"' and codesetid='"+fieldCodeSetId+"'";
        RowSet rs = null;
        String msg = "";
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            rs=dao.search(sql);
            if(rs.next()){
                String codeitemid=rs.getString("codeitemid");
                return codeitemid;
            }
        }catch (Exception e) {
            e.printStackTrace();
            return msg;
        }finally{
        	PubFunc.closeDbObj(rs);
        }
        return null;
    }
    /**
     * 通过单位或部门名称模糊查询
     * @param codeDesc
     * @return
     * @throws GeneralException
     */
    public List<String> getCodeByLikeDesc(String codeDesc) throws GeneralException{
    	List<String> itemidList = new ArrayList<String>();
    	//组织机构类型的代码应该查organization表 haosl 2017-08-02 add
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
	 * 评审环节list
	 * @return
	 */
	public ArrayList<HashMap> getReviewStepList(){
		
		ArrayList<HashMap> list = new ArrayList<HashMap>();
		// 阶段一
		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("dataValue", "1");
		map1.put("dataName", JobtitleUtil.ZC_REVIEWFILE_STEP1SHOWTEXT);
		// 阶段二
		HashMap<String, String> map2 = new HashMap<String, String>();
		map2.put("dataValue", "2");
		map2.put("dataName", JobtitleUtil.ZC_REVIEWFILE_STEP2SHOWTEXT);
		// 阶段三
		HashMap<String, String> map3 = new HashMap<String, String>();
		map3.put("dataValue", "3");
		map3.put("dataName", JobtitleUtil.ZC_REVIEWFILE_STEP3SHOWTEXT);
		// 阶段三
		HashMap<String, String> map4 = new HashMap<String, String>();
		map4.put("dataValue", "4");
		map4.put("dataName", JobtitleUtil.ZC_REVIEWFILE_STEP4SHOWTEXT);
				
		list.add(map1);
		list.add(map2);
		list.add(map3);
		list.add(map4);
		
		return list;
	}
	
	/**
	 * 更新【导入标识】 说明:【第0位】：同行专家阶段 【1】：学科组阶段  【2】：聘委会阶段 【3】：学院聘任组 1000：仅导入了同行专家阶段数据
	 * @param w0501：申报人主键序号ID 
	 * @param value：
	 */
	public void updateW0525(String w0501, String value) {
		
		ContentDAO dao = new ContentDAO(this.conn);
        try{
        	String sql = "update w05 set w0525=? where w0501=?";
        	ArrayList list = new ArrayList();
        	list.add(value);
        	list.add(w0501);
            dao.update(sql, list);
            
        }catch (Exception e) {
            e.printStackTrace();
        }
	}
	/**
	 * 更新【导入标识】 只更新其中一位  【0】：同行专家阶段 【1】：学科组阶段  【2】：聘委会阶段 【3】：学院聘任组
	 * @param w0501：申报人主键序号ID 
	 * @param type：帐号类型 1：内部评委 2：学科组成员 3：外部鉴定专家 4：学院聘任组
	 * @param value：
	 */
	public void updateW0525(String w0501, int type, String value) {
		
		ContentDAO dao = new ContentDAO(this.conn);
		try{
			RecordVo w05 = new RecordVo("w05");
			w05.setString("w0501", w0501);
			w05 = dao.findByPrimaryKey(w05);
			
			String newW0525 = "0000";
			String oldW0525 = w05.getString("w0525");
			if(StringUtils.isNotEmpty(oldW0525)){
				int startIndex = 0;
				if(type == 1){//评委会
					startIndex = 2;
				} else if(type == 2){//学科组
					startIndex = 1;
				} else if(type == 3){//外部专家
					startIndex = 0;
				} else if(type == 4){//学院聘任组
					startIndex = 3;
				}
				StringBuffer buffer = new StringBuffer(oldW0525);
				buffer.replace(startIndex, startIndex+1, value);
				newW0525 = buffer.toString();
			}
			
			w05.setString("w0525", newW0525);
    		dao.updateValueObject(w05);
		}catch (Exception e) {
			e.printStackTrace();
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
	 * 获得当前业务范围的所有上级机构的id
	 */
	public List<String> getAllIdByBs(){
		String unit = this.userview.getUnitIdByBusi("9");
		List<String> parenIdList = new ArrayList<String>();	//存放所有上级机构的id 
        if(StringUtils.isNotEmpty(unit) && !"UN`".equals(unit)){
        	String [] unitarr = unit.split("`");
        	for(int m=0;m<unitarr.length;m++){
        		String arr = unitarr[m];
        		arr = arr.substring(2,arr.length());
        		getParentId(arr,parenIdList);	//查找到所有上级机构的id 并存放到parenIdList集合中
        	}
        }
        return parenIdList;
	}
	  /**
	 * 通过下级部门(单位)id查到上级部门（单位）id,并且是启用了学校聘任组评审环节评审会议
	 * @param codeItemId
	 * 				机构id
	 * @param list	
	 * 				存放parentid的容器
	 */
    private void getParentId(String codeItemId,List<String> list){
		for(int n=codeItemId.length()-1; n>0; n--){
			//从后往前每次截取1位
 			String temp = codeItemId.substring(0, n);	
 			CodeItem item1 = AdminCode.getCode("UM", temp);
 			CodeItem item2 = AdminCode.getCode("UN", temp);
 			//查询是否有这个代码项，有则是父级id,没有则不是
 			if(item1 != null || item2 != null){
				if(!list.contains(temp)) {
					list.add(temp);
				}
 			}
 		}
	}
   /**
    * 查询指定会议id是否启用学院聘任组阶段
    * @param
    * 		w0301 会议id
    */
   public Boolean isOpenCollegeGroup(String w0301) throws GeneralException {
	   ContentDAO dao = new ContentDAO(this.conn);
	   RowSet rs = null;
	   try{
		   String sql = "select w0323 from w03 where w0301 = ?";
		   ArrayList list = new ArrayList();
		   list.add(w0301);
		   rs = dao.search(sql,list);
		   if(rs.next()){
			   if(rs.getInt("w0323")==1){
				   return true;
			   }else{
				   return false; 
			   }
		   }
		   return false;
	   }catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	  
   }
	/**
	 * 获取当前会议下的所有投票结果集合
	 * @param w0501
	 * @param itemId
	 * @return
	 * chent 
	 */
	public Map<String, String> getPersonSet(String w0301) {

		Map<String, String> map = new HashMap<String, String>();

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			List list = new ArrayList();
			String sql = "select b.W0501,b.username,approval_state,b.type from zc_data_evaluation a " 
					+ "left join zc_expert_user b on a.W0501=b.W0501 and a.W0301=b.W0301 and a.username=b.username " 
					+ "where a.w0501=b.w0501 and expert_state='3' ";
			if(StringUtils.isNotEmpty(w0301)){
				sql += " and a.w0301=?";
				list.add(w0301);
			}

			rs = dao.search(sql, list);

			while (rs.next()) {
				String w0501 = rs.getString("w0501");
				String username = rs.getString("username");
				String approval_state = rs.getString("approval_state");
				String type = rs.getString("type");

				String key = PubFunc.encrypt(w0501) + "_" + type + "_" + approval_state;

				String oldValue = map.get(key);
				if (StringUtils.isEmpty(oldValue)) {
					String value = username;
					map.put(key, value);
				} else {
					String value = oldValue += ("、" + username);
					map.put(key, value);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}

		return map;
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
	 * 
	 * @Title: changeConstant   
	 * @Description: 查询或改变Str_value值的操作
	 * @param @param list：noticefielditems节点的值
	 * @param @param type：type为1表示新增或者修改Str_value的值，type为2时表示查询Str_value的节点的值
	 * @param @return 
	 * @return String    
	 * @throws
	 */
	public String changeConstant(String list, String type) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		//传回前台的信息提示
		String msg = null;
		String xmlValue = null;
		try {
			rs = dao.search("select Str_Value from Constant where constant='JOBTITLE_CONFIG'");
			if("1".equals(type) || type == "1") {
				xmlValue = "<?xml version=\"1.0\" encoding=\"GB2312\"?>" +
						"  <params><templates></templates><noticefielditems value='"+list+"'/></params>";
				
				if(!(rs.next())){//没有则插入空
					String sql="insert into Constant(Constant,Type,Describe,str_value)" +
							" values('JOBTITLE_CONFIG','A','职称评审配置参数','')";
					dao.insert(sql, new ArrayList());
				}else{
					String str_value = rs.getString("Str_Value");
					if(StringUtils.isNotEmpty(str_value))
						xmlValue = rs.getString("Str_Value");
				}
		        Document doc = PubFunc.generateDom(xmlValue);
		        //取的根元素
	            Element root = doc.getRootElement();
	            root.removeChild("noticefielditems");
	            Element noticefielditems = new Element("noticefielditems");
	            noticefielditems.setAttribute("value", list);
				root.addContent(noticefielditems);
				  //设置xml字体编码，然后输出为字符串
	            Format format=Format.getRawFormat();
	        	format.setEncoding("UTF-8");
	            XMLOutputter output=new XMLOutputter(format);
	        	String xml=output.outputString(doc);//最终处理后xml
				int row = dao.update("update constant  set Str_Value=? where Constant='JOBTITLE_CONFIG'", Arrays.asList(new String[] {xml}));
				if(row==1){//是否成功
					RecordVo paramsVo=ConstantParamter.getConstantVo("JOBTITLE_CONFIG");
					if(paramsVo==null){
						paramsVo = new RecordVo("Constant");
						paramsVo.setString("constant", "JOBTITLE_CONFIG");
						paramsVo.setString("describe", "职称评审配置参数");
						paramsVo.setString("type", "A");
					}
					paramsVo.setString("str_value", xml);
					ConstantParamter.putConstantVo(paramsVo, "JOBTITLE_CONFIG");
				}
			}else if("2".equals(type) || type == "2"){
				if((rs.next())){//没有则插入空
					
					String str_value = rs.getString("Str_Value");
					if(StringUtils.isNotEmpty(str_value))
						xmlValue = rs.getString("Str_Value");
					if (xmlValue!=null && xmlValue.length()>0){
					   	//创建一个新的字符串
				        String upxml="";
				        List listArray=new ArrayList();
				        try {
				            //通过输入源构造一个Document
				            Document doc = PubFunc.generateDom(xmlValue);
				            //取的根元素
				            Element root = doc.getRootElement();
				            //得到根元素所有子元素的集合
				            List jiedian = root.getChildren();
				            //子元素对象
				            Element et = null;
				            for(int i = 0; i < jiedian.size(); i++){
				                et = (Element) jiedian.get(i);//循环依次得到子元素
				                if(et.getAttributeValue("value") != null && !"".equals(et.getAttributeValue("value"))) 
				                	msg =  (String)et.getAttributeValue("value");
				            }
				            
				        } catch (JDOMException e) {
				            e.printStackTrace();
				        } catch (IOException e) {
				            e.printStackTrace();
				        }
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return msg;
	}
	/**
	 * 获取会议数据源
	 * @return archivedata 数据源
	 * @throws GeneralException
	 */
	public ArrayList<HashMap> getMeetingdata(ArrayList schemeTypeArray) throws GeneralException {

    	
    	ArrayList<HashMap> dataList = new ArrayList<HashMap>();
    	
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
		try {
			StringBuilder querySql = new StringBuilder();
			for(int i=0; schemeTypeArray!=null && i<schemeTypeArray.size(); i++){
				String scheme = (String)schemeTypeArray.get(i);
				if("0".equals(scheme) || "all".equals(scheme) || StringUtils.isEmpty(scheme)){//all：全部、0：没有选
					continue;
				}
				querySql.append("and ");
				if ("in".equals(scheme)) {// 进行中
					querySql.append("w0321='05' ");
				} else if ("stop".equals(scheme)) {// 暂停
					querySql.append("w0321='09' ");
				} else if ("finish".equals(scheme)) {// 已结束
					querySql.append("w0321='06' ");
				}
			}
			String sql = "select * from ("+getSql("")+") myGridData where 1=1 ";
			if(querySql.length()>0){
				sql += querySql.toString();
			}
			
			rs = dao.search(sql);
			
			String str = ",";
			while(rs.next()){
				HashMap map = new HashMap();
				String w0301 = rs.getString("W0301_safe");
				String w0505 = rs.getString("w0505");
				String w0503 = rs.getString("w0503");
				if(str.indexOf(","+w0301+",") > -1){
					continue ;
				}
				map.put("w0301", "w0301_"+PubFunc.encrypt(w0301));
				map.put("w0303", rs.getString("meetingname"));
				map.put("w0321", rs.getString("w0321"));
//				// 启用的阶段
//				ReviewMeetingBo reviewMeetingBo = new ReviewMeetingBo(this.conn, this.userview);
//				HashMap<String, Boolean> enableSteps = (HashMap<String, Boolean>)reviewMeetingBo.getEnableSteps(w0301);
//				map.put("enableSteps", enableSteps);
//				
				dataList.add(map);
				
				str += (w0301+",");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return dataList;
    
	}
	/**
	 * 获取表格控件列头
	 * @param w0301
	 * @param review_links
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList getColumnListForDiff(String w0301, String review_links)throws GeneralException {
		ArrayList list = new ArrayList();

		try {
			ColumnsInfo columnsInfo = new ColumnsInfo();

			// 分组名
			columnsInfo = this.getColumnsInfo("name", ResourceFactory.getProperty("zc_new.zc_reviewfile.groupName"), 180, "0", "A", 100, 0);
			columnsInfo.setTextAlign("center");
			columnsInfo.setEditableValidFunc("jobtitle_reviewdiff.checkCell");
			list.add(columnsInfo);
			
			// w03中配置的职级列
			ReviewMeetingBo bo = new ReviewMeetingBo(this.getConn(), this.getUserview());
			String value = bo.getW03Ctrl_param(w0301).get(review_links);
			if(StringUtils.isNotEmpty(value)) {// 配置职级分类时，显示配置的职级
				HashMap<String, String> w0575CodeItemMap = this.getW0575CodeItemMap();
				String[] array = value.split(",");
				for(String str : array) {
					
					ArrayList<ColumnsInfo> childList = new ArrayList<ColumnsInfo>();
					String num_id = "c_" + str;
					ColumnsInfo personNum = this.getColumnsInfo(num_id, "应选人数", 70, "0", "N", 100, 0);
					personNum.setTextAlign("center");
					personNum.setEditableValidFunc("jobtitle_reviewdiff.checkCell");
					childList.add(personNum);

					String person_id = "p_" + str;
					ColumnsInfo childColumn = this.getColumnsInfo(person_id, "申报人", 600, "0", "A", 100, 0);
					childColumn.setRendererFunc("jobtitle_reviewdiff.renderperson");
					childColumn.setEditableValidFunc("false");
					childList.add(childColumn);
					
					ColumnsInfo info = new ColumnsInfo();
					info.setColumnDesc(w0575CodeItemMap.get(str));
					info.setChildColumns(childList);
					list.add(info);
				}
				
			} else {//没有配置时，默认显示一个职级分类
				columnsInfo = this.getColumnsInfo("c_number", "应选人数", 70, "0", "N", 100, 0);
				columnsInfo.setTextAlign("center");
				columnsInfo.setEditableValidFunc("jobtitle_reviewdiff.checkCell");
				list.add(columnsInfo);
				
				columnsInfo = this.getColumnsInfo("p_person", "申报人", 600, "0", "A", 100, 0);
				columnsInfo.setRendererFunc("jobtitle_reviewdiff.renderperson");
				columnsInfo.setEditableValidFunc("false");
				list.add(columnsInfo);
			}
			// 进度
			columnsInfo = this.getColumnsInfo("progress", "进度", 70, "0", "N", 100, 0);
			columnsInfo.setRendererFunc("jobtitle_reviewdiff.progress");
			columnsInfo.setTextAlign("center");
			columnsInfo.setEditableValidFunc("false");
			list.add(columnsInfo);
			
			// 状态
			columnsInfo = getColumnsInfo("approval_state", "状态", 70, "0", "A", 100, 0);
			columnsInfo.setRendererFunc("jobtitle_reviewdiff.approval_state");
			columnsInfo.setTextAlign("center");
			columnsInfo.setEditableValidFunc("false");
			list.add(columnsInfo);

			// 操作
			columnsInfo = this.getColumnsInfo("operation", "操作", 100, "0", "A", 100, 0);
			columnsInfo.setRendererFunc("jobtitle_reviewdiff.renderoperation");
			columnsInfo.setTextAlign("center");
			columnsInfo.setEditableValidFunc("false");
			list.add(columnsInfo);
			
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
		} catch(Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	/**
	 * 差额投票-按钮
	 * @return
	 */
	public ArrayList<Object> getButtonListForDiff() {

		ArrayList<Object> buttonList = new ArrayList<Object>();
		try {
			
			// 功能导航
			ArrayList<Object> menuList = new ArrayList<Object>();
			ArrayList list = new ArrayList();
			list.add(getCustomBean("","","codeselectpicker",""));
			LazyDynaBean bean = getMenuBean("应选人数设置","jobtitle_reviewdiff.setPersonNum()", "", list);
			menuList.add(bean);
			String menu = getMenuStr("功能导航","reviewdiffmenu",menuList);			
			buttonList.add(menu);
			
			// 新建分组
			ButtonInfo buttonInfo = new ButtonInfo("新建分组", "jobtitle_reviewdiff.createCategorie");
			buttonList.add(buttonInfo);

			// 保存
			buttonInfo = new ButtonInfo(ResourceFactory.getProperty("button.save"),"jobtitle_reviewdiff.saveInfo");
			buttonList.add(buttonInfo);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return buttonList;
	}
	/**
	 * 获取表格控件tablesql
	 * @param w0301
	 * @param review_links
	 * @return
	 */
	private String getSqlForDiff(String w0301, String review_links) {
		
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
	 * 获取表格控件配置
	 * @param w0301
	 * @param review_links
	 * @return
	 * @throws GeneralException
	 */
	public String getTableConfigForDiff(String w0301, String review_links) throws GeneralException {
		
		String config = "";
		try {
			ArrayList<ColumnsInfo> columnList = this.getColumnListForDiff(w0301, review_links);
			String sql = this.getSqlForDiff(w0301, review_links);
			
			String key = "jobtitle_reviewfile_diff_" + review_links;
			TableConfigBuilder builder = new TableConfigBuilder(key, columnList, key, this.getUserview(), this.getConn());
			builder.setDataSql(sql);
			builder.setOrderBy("order by categories_id");
			builder.setTableTools(this.getButtonListForDiff());
			//builder.setTitle(JobtitleUtil.ZC_MENU_COMMITTEESHOWTEXT + "成员");
			builder.setEditable(true);
			builder.setAutoRender(false);
			builder.setColumnFilter(false);
			builder.setScheme(true);
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
	public String getTableConfigForDiffSelPerson(String w0301, String review_links) throws GeneralException {
		
		String config = "";
		try {
			ArrayList list = new ArrayList();
			ColumnsInfo columnsInfo = new ColumnsInfo();
			columnsInfo = this.getColumnsInfo("w0511", DataDictionary.getFieldItem("w0511").getItemdesc(), 120, "0", "A", 100, 0);
			columnsInfo.setTextAlign("center");
			columnsInfo.setEditableValidFunc("false");
			list.add(columnsInfo);
			
			columnsInfo = this.getColumnsInfo("w0507", DataDictionary.getFieldItem("w0507").getItemdesc(), 120, "UN", "A", 100, 0);
			columnsInfo.setTextAlign("center");
			columnsInfo.setEditableValidFunc("false");
			list.add(columnsInfo);

			columnsInfo = this.getColumnsInfo("w0509", DataDictionary.getFieldItem("w0509").getItemdesc(), 120, "UM", "A", 100, 0);
			columnsInfo.setTextAlign("center");
			columnsInfo.setEditableValidFunc("false");
			list.add(columnsInfo);
			
			columnsInfo = this.getColumnsInfo("w0513", DataDictionary.getFieldItem("w0513").getItemdesc(), 120, "0", "A", 100, 0);
			columnsInfo.setTextAlign("center");
			columnsInfo.setEditableValidFunc("false");
			list.add(columnsInfo);
			
			columnsInfo = this.getColumnsInfo("w0515", DataDictionary.getFieldItem("w0515").getItemdesc(), 120, "0", "A", 100, 0);
			columnsInfo.setTextAlign("center");
			columnsInfo.setEditableValidFunc("false");
			list.add(columnsInfo);
			
			columnsInfo = this.getColumnsInfo("w0501", "申报人id", 100, "0", "A", 100, 0);
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnsInfo.setEncrypted(true);;
			list.add(columnsInfo);
				
			String sql = "select * From W05 where W0301='"+w0301+"' and W0501 not in(select W0501 from zc_categories_relations where categories_id in (select categories_id from zc_personnel_categories where w0301='"+w0301+"' and Review_links='"+review_links+"'))";
			
			TableConfigBuilder builder = new TableConfigBuilder("jobtitle_reviewfile_diff_selperson", list, "jobtitle_reviewfile_diff_selperson", this.getUserview(), this.getConn());
			builder.setDataSql(sql);
			
			StringBuilder orderBy = new StringBuilder();
			orderBy.append(" order by ");
	    	ArrayList<String> sortItemList = this.getSchemeSettingSortItemList();//栏目设置的指标排序，如果栏目设置没有设置公有方案则默认按w0501排序。
	    	if(sortItemList.size() > 0){
	    		for(String sortItem : sortItemList){
	    			String[] array = sortItem.split(":");
					String itemid = array[0];
					String type = array[1];
					FieldItem item = DataDictionary.getFieldItem(itemid, "w05");
					if(item == null){
						continue ;
					}
					orderBy.append(" "+itemid+" "+(Integer.parseInt(type)==1?"asc":"desc")+",");
	    		}
	    	}else {
	    		orderBy.append(" w0501 DESC ");
	    	}
	    	orderBy.deleteCharAt(orderBy.length()-1);
			builder.setOrderBy(orderBy.toString());
			
			ArrayList btnList = new ArrayList();
			ButtonInfo queryBox = new ButtonInfo();
			queryBox.setType(ButtonInfo.TYPE_QUERYBOX);
			queryBox.setText("请输入单位名称、部门、姓名");
			queryBox.setFunctionId("ZC00003023");
			queryBox.setShowPlanBox(false);
			btnList.add(queryBox);
			
			builder.setTableTools(btnList);
			builder.setEditable(true);
			builder.setSelectable(true);
			builder.setAutoRender(false);
			builder.setColumnFilter(false);
			builder.setScheme(false);
			builder.setLockable(false);
			builder.setAnalyse(false);
			builder.setPageSize(20);
			config = builder.createExtTableConfig();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return config;
	}
	public void diffSelPersonFastSearch(ArrayList<String> valuesList) {
		try {
			StringBuilder querySql = new StringBuilder();
			TableDataConfigCache catche = (TableDataConfigCache)this.getUserview().getHm().get("jobtitle_reviewfile_diff_selperson");
			
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
				querySql.append("(w0511 like '%" + queryVal+"%'");
				List<String> itemids = this.getCodeByLikeDesc(queryVal);
				if(itemids.size()>0) {
					StringBuffer itemBuf = new StringBuffer();
					for(String itemid : itemids) {
						itemBuf.append("'"+itemid+"',");
					}
					itemBuf.setLength(itemBuf.length()-1);
					querySql.append(" or w0507 in ("+itemBuf+") or w0509 in ("+itemBuf+")");
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
	 * @return
	 * @throws GeneralException
	 */
	public int addCategorie(String w0301, String review_links, String categories_name) throws GeneralException{
		
		int errorcode = 1;
		
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			IDFactoryBean idf = new IDFactoryBean();
			RecordVo vo = new RecordVo("zc_personnel_categories");
			
			String id = idf.getId("zc_personnel_categories.categories_id", "", conn);
			vo.setString("categories_id", id);
			vo.setString("w0301", w0301);
			vo.setString("review_links", review_links);
			vo.setString("name", categories_name);
			vo.setString("approval_state", "0");
			vo.setInt("expertnum", 0);
			vo.setInt("submitnum", 0);
			
			int result = dao.addValueObject(vo);
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
					if(StringUtils.isNotEmpty(key) && (key.startsWith("c_") || "name".equals(key))) {
						String val = String.valueOf(entry.getValue());
						if(key.startsWith("c_") && "null".equalsIgnoreCase(val)) {
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
	public int deleteCategorie(String categories_id) throws GeneralException{
		
		int errorcode = 1;
		
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			IDFactoryBean idf = new IDFactoryBean();
			RecordVo vo = new RecordVo("zc_personnel_categories");
			vo.setString("categories_id", categories_id);
			
			vo = dao.findByPrimaryKey(vo);
			
			int result = dao.deleteValueObject(vo);
			if(result == 1) {
				errorcode = 0;
				// 删除人员的投票环节和账号等需要重置
				this.stopCategories(categories_id);
				
				// 同时删除关联的人员
				String sql = "delete from zc_categories_relations where categories_id=?";
				result = dao.delete(sql, Arrays.asList(new String[] {categories_id}));
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
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
			String upSql = "update W05 set W0573=? , W0555=? where W0501 in (select w0501 from zc_categories_relations where categories_id=?)";
			List values = new ArrayList();
			values.add(null);
			values.add(null);
			values.add(categories_id);
			dao.update(upSql, values);
			
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
	public int addCategories_relations(String categories_id, ArrayList<String> w0501_eList, String c_level) throws GeneralException{
		
		int errorcode = 1;
		
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			
			for(String w0501_e : w0501_eList) {
				String w0501 = PubFunc.decrypt(w0501_e);
				
				RecordVo vo = new RecordVo("zc_categories_relations");
				vo.setString("categories_id", categories_id);
				vo.setString("w0501", w0501);
				vo.setString("c_level", c_level);
				
				int result = dao.addValueObject(vo);
				if(result == 1) {
					errorcode = 0;
					// 同步人数
					this.asyncCategoriesPersonNum(categories_id, c_level);
				}else {
					errorcode = 1;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
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
	public int deleteCategories_relations(String categories_id, String w0501, String c_level) throws GeneralException {
		
		int errorcode = 1;
		
		ContentDAO dao = new ContentDAO(this.conn);
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
				// 同步人数
				this.asyncCategoriesPersonNum(categories_id, c_level);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
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
	public HashMap<String, String> getCategoriesMap(String w0301, String review_links) throws GeneralException {
		HashMap<String, String> map = new HashMap<String, String>();
		
		RowSet rs = null;
		RowSet rs1 = null;
		try {
			ContentDAO dao = new ContentDAO(this.getConn());
			rs = dao.search("select A.categories_id,A.w0501,A.c_level,B.W0503,B.W0505,B.W0511,B.W0507,B.W0509,B.W0513,B.W0515 from zc_categories_relations A,W05 B where A.w0501=B.W0501 and B.w0301=?", Arrays.asList(new String[] {w0301}));
			while(rs.next()) {
				
				String _categories_id = rs.getString("categories_id");
				RecordVo vo = new RecordVo("zc_personnel_categories");
				StringBuilder usernameStr = new StringBuilder();
				int submitnum = 0;
		    	rs1 = dao.search("select distinct username  From zc_data_evaluation where expert_state=3 and username in (select username from zc_expert_user where W0301=? and type=?) and w0501 in (select W0501 from zc_categories_relations where categories_id=? )", Arrays.asList(new String[] {w0301, review_links, _categories_id}));
		    	while(rs1.next()){
		    		submitnum++;
		    		String username = rs1.getString("username");
		    		usernameStr.append(username+",");
		    	}

		    	vo.setString("categories_id", _categories_id);
		    	vo = new ContentDAO(this.getConn()).findByPrimaryKey(vo);
		    	int expertnum = vo.getInt("expertnum");
				if(expertnum>0 && submitnum>0 && expertnum==submitnum) {// 应投==已投：已结束
					vo.setString("approval_state", "2");
				}
				vo.setInt("submitnum", submitnum);
				dao.updateValueObject(vo);
				
				if(usernameStr.length() > 0) {
					usernameStr.deleteCharAt(usernameStr.length()-1);
				}
				map.put(PubFunc.encrypt(_categories_id), usernameStr.toString());
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(rs1);
		}
		return map;
	}
	public HashMap<String, ArrayList<HashMap<String, String>>> getCategories_relations(String w0301, String review_links) throws GeneralException {
		HashMap<String, ArrayList<HashMap<String, String>>> map = new HashMap<String, ArrayList<HashMap<String, String>>>();
		
		RowSet rs = null;
		RowSet rs1 = null;
		RowSet rs2 = null;
		try {
			ContentDAO dao = new ContentDAO(this.getConn());
			rs = dao.search("select A.categories_id,A.w0501,A.c_level,B.W0503,B.W0505,B.W0511,B.W0507,B.W0509,B.W0513,B.W0515 from zc_categories_relations A,W05 B where A.w0501=B.W0501 and B.w0301='"+w0301+"'");
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
				person.put("w0501", PubFunc.encrypt(rs.getString("w0501")));
				person.put("w0503", PubFunc.encrypt(rs.getString("W0503")));
				person.put("w0505", PubFunc.encrypt(rs.getString("W0505")));
				person.put("w0511", rs.getString("W0511"));// 姓名
				person.put("w0507", AdminCode.getCodeName("UN", rs.getString("W0507")));// 单位
				person.put("w0509", AdminCode.getCodeName("UM", rs.getString("W0509")));// 部门
				String w0513 = rs.getString("W0513");
				if(StringUtils.isEmpty(w0513)) {
					w0513 = "";
				}
				person.put("w0513", w0513);//现聘名称
				String w0515 = rs.getString("W0515");
				if(StringUtils.isEmpty(w0515)) {
					w0515 = "";
				}
				person.put("w0515", w0515);//申报职位
				PhotoImgBo photoImgBo = new PhotoImgBo(this.conn);
				String imgpath = photoImgBo.getPhotoPathLowQuality(rs.getString("w0503"), rs.getString("w0505"));
				person.put("imgpath", imgpath);
				
				// 申报人一共有几个人给他投票
				int expert_count = 0;
				String sql2 = "select count(user_id) as count from zc_expert_user where w0501=? and type=? and state=1 and usetype=2";
				rs1 = new ContentDAO(this.getConn()).search(sql2, Arrays.asList(new String[] {rs.getString("w0501"), review_links}));
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
	 * @return
	 * @throws GeneralException
	 */
	public int updateApproval_state(String categories_id, String approval_state) throws GeneralException{
		
		int errorcode = 1;
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			RecordVo vo = new RecordVo("zc_personnel_categories");
			vo.setString("categories_id", categories_id);
			vo = dao.findByPrimaryKey(vo);
			
			if("1".equals(approval_state)) {
				int expertnum=0;
				rs = dao.search("select distinct username from zc_expert_user where w0501 in(select w0501 From zc_categories_relations where categories_id=?) and W0301 in (select W0301 from zc_personnel_categories where categories_id=?) and type in (select review_links from zc_personnel_categories where categories_id=?) and usetype=2", Arrays.asList(new String[] {categories_id, categories_id, categories_id}));
				while(rs.next()) {
					expertnum++;
				}
				vo.setInt("expertnum", expertnum);
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
			
			RecordVo vo1 = new RecordVo("zc_personnel_categories");
			vo1.setString("categories_id", categories_id);
			vo1 = dao.findByPrimaryKey(vo1);
			String key = "c_"+c_level;
			if("person".equalsIgnoreCase(c_level)) {
				key = "c_number";
			}
			vo1.setInt(key, num);
			dao.updateValueObject(vo1);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
	}
	private ArrayList getSchemeSettingSortItemList() {
		
		ArrayList<String> sortItemList = new ArrayList<String>();
		RowSet rs = null;
		ContentDAO dao = null;
		try{
			// 是否有私有方案
			boolean isSelfScheme = false;
			String sql = "select count(*) as count from t_sys_table_scheme_item where scheme_id = (select scheme_id from t_sys_table_scheme where submoduleid = 'reviewFile' and is_share = '0' and username='"+this.getUserview().getUserName()+"') and is_display = '1'";;
			rs = new ContentDAO(conn).search(sql);
			if (rs.next() && rs.getInt("count")>0){
				isSelfScheme = true;
			}
			
			
			String sql1 = "";
			if(isSelfScheme) {// 私有方案
				sql1 = "select * from t_sys_table_scheme_item where scheme_id = (select scheme_id from t_sys_table_scheme where submoduleid = 'reviewFile' and is_share = '0' and username='"+this.getUserview().getUserName()+"') and is_display = '1' order by displayorder";;
				
			}else {// 公有方案
				sql1 = "select * from t_sys_table_scheme_item where scheme_id = (select scheme_id from t_sys_table_scheme where submoduleid = 'reviewFile' and is_share = '1') and is_display = '1' order by displayorder";;
			}
			rs = new ContentDAO(conn).search(sql1);
			while (rs.next()){
				String itemid = rs.getString("itemid");
				String is_order = rs.getString("is_order");
            	if(StringUtils.isNotBlank(is_order) && !"0".equals(is_order)){
            		sortItemList.add(itemid+":"+is_order);
            	}
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			PubFunc.closeDbObj(rs);
		}
		
		return sortItemList;
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
	 * 判断是否为程序改版前的老数据 老数据返回null  新数据不为null
	 * @throws GeneralException 
	 */
	public LazyDynaBean isOldW03Data(String w0301) throws GeneralException {
		ReviewMeetingPortalBo rmpb = new ReviewMeetingPortalBo(userview, conn);
		LazyDynaBean bean = rmpb.getMeetingData(w0301);
		String[] segments = (String[])bean.get("segments");
		if(segments.length==0) {
			return null;
		}
		return bean;
	}
	
	@SuppressWarnings("unchecked")
	public ColumnsInfo getScoreColumnList(String segment,String w0301)throws GeneralException {
		// 获取该会议当前阶段的测评表配置
		ArrayList<ColumnsInfo> columns = new ArrayList<ColumnsInfo>();
		ReviewScorecountBo rsbo = new ReviewScorecountBo(conn, userview);
		rsbo.setW0301(w0301);
		String templates = rsbo.getReviewMeetingTemplateids(segment);
		String[] perTemps = templates.split(",");
		ColumnsInfo segmentCol = null;
		String segmentName = "";
		String id = "";
		if("1".equals(segment)) {
			segmentName = JobtitleUtil.ZC_REVIEWFILE_STEP1SHOWTEXT;
			id="committee_hb";
		}
		else if("2".equals(segment)) {
			segmentName = JobtitleUtil.ZC_REVIEWFILE_STEP2SHOWTEXT;
			id="subject_hb";
		}
		else if("3".equals(segment)) {
			segmentName = JobtitleUtil.ZC_REVIEWFILE_STEP3SHOWTEXT;
			id="checkproficient_hb";
		}else {
			segmentName = JobtitleUtil.ZC_REVIEWFILE_STEP4SHOWTEXT;
			id="college_hb";
		}
		// 复合列头  z001,z002
		for(int j = 0; j < perTemps.length; j++) {
			String per = perTemps[j];
			FieldItem item = DataDictionary.getFieldItem("C_"+per, "W05");
			if(null == item)
				continue;
			// 去除没有启用的指标
			if (!"1".equals(item.getUseflag())) 
				continue;
			// 去除隐藏的指标
			if (!"1".equals(item.getState())) 
				continue;
			
			String desc = item.getItemdesc();
			// 复合列头
			ColumnsInfo cosu = new ColumnsInfo();
			cosu = getColumnsInfo(per, desc, 220, "0", "A", 100, 0);
			cosu.setQueryable(false);
			// 分数子列头
			ColumnsInfo columnsInfo = new ColumnsInfo();
			columnsInfo.setColumnId("C_"+per+"_"+segment);
			columnsInfo.setColumnType("N");
			columnsInfo.setDecimalWidth(2);
			columnsInfo.setColumnDesc("分数");
			columnsInfo.setQueryable(false);
			columnsInfo.setColumnWidth(80);
			columnsInfo.setEditableValidFunc("false");
			columnsInfo.setTextAlign("right");
			cosu.addChildColumn(columnsInfo);
			
			columnsInfo = new ColumnsInfo();
			columnsInfo.setColumnId("C_"+per+"_seq"+"_"+segment);
			columnsInfo.setColumnDesc("排名");
			columnsInfo.setColumnType("N");
			columnsInfo.setQueryable(false);
			columnsInfo.setColumnWidth(80);
			columnsInfo.setEditableValidFunc("false");
			columnsInfo.setTextAlign("right");
			cosu.addChildColumn(columnsInfo);
			
			columns.add(cosu);
			
		}
		if(columns.size()>0) {
			ColumnsInfo groupCol = new ColumnsInfo();
			groupCol.setColumnId("group_"+segment);
			groupCol.setColumnDesc(ResourceFactory.getProperty("zc_new.zc_reviewcfile.columnname.group"));
			groupCol.setColumnType("A");
			groupCol.setQueryable(false);
			groupCol.setColumnWidth(80);
			
			groupCol.setEditableValidFunc("false");
			groupCol.setTextAlign("center");
			columns.add(0,groupCol);
			
			segmentCol = new ColumnsInfo();
			segmentCol.setQueryable(false);
			segmentCol.setColumnDesc(segmentName);
			segmentCol.setColumnId(id);//为合并列设置id
			segmentCol.setChildColumns(columns);
		}
		return segmentCol;
	}
	
	/**
	 * 获取启用的阶段
	 * @param w0301
	 * @return
	 * @throws GeneralException
	 */
	public String getEnableSteps(String w0301) throws GeneralException{
		HashMap<String, Boolean> steps = new HashMap<String, Boolean>();
		//评审会议(包括同年度相同申报人结束的会议)启用的阶段
		steps.put("step1", false);
		steps.put("step2", false);
		steps.put("step3", false);
		steps.put("step4", false);
		if(StringUtils.isEmpty(w0301)){
			return "";
		}
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select sum(W0517) step1,sum(W0521) step2,sum(W0523) step3,sum(W0571) step4 from w05 where w0301=? ");
			List values = new ArrayList();
			values.add(w0301);
			rs = dao.search(sql.toString(),values);
			if(rs.next()) {
				int step1 = rs.getInt("step1");
				int step2 = rs.getInt("step2");
				int step3 = rs.getInt("step3");
				int step4 = rs.getInt("step4");
				if(step1>0) {
					steps.put("step1", true);
				}
				if(step3>0) {
					steps.put("step3", true);
				}
				if(step4>0) {
					steps.put("step4", true);
				}
			}
			
			RecordVo vo = new RecordVo("w03");
			vo.setString("w0301", w0301);
			
			dao = new ContentDAO(this.conn);
			vo = dao.findByPrimaryKey(vo);//本次上申报数据
			
			int w0315 = vo.getInt("w0315");
			String w0325 = vo.getString("w0325");
			int w0323 = vo.getInt("w0323");
			
			
			
			//学科组
			sql.setLength(0);
			sql.append("select distinct(w0301) from zc_expert_user where w0301 =? and type=2");
			rs = dao.search(sql.toString(),values);
			
			while(rs.next()){//没有数据 证明没有启用学科组评议
				steps.put("step2", true);
			}
			if(w0315 > 0){//参会人数>0:启用高评委
				steps.put("step1", true);
			}
			if("1".equals(w0325)){//启用同行评议组
				steps.put("step3", true);
			}
			if(w0323 > 0 ){//参会人数>0:启用二级单位
				steps.put("step4", true);
			}
			
			StringBuffer segments = new StringBuffer();
			segments.append(",");
			if(steps.get("step1"))
				segments.append("1,");
			if(steps.get("step2"))
				segments.append("2,");
			if(steps.get("step3"))
				segments.append("3,");
			if(steps.get("step4"))
				segments.append("4,");
			return segments.toString();
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
	}
}
