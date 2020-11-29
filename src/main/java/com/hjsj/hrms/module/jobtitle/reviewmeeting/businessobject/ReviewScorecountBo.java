package com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ReviewFileBo;
import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.StartReviewBo;
import com.hjsj.hrms.module.jobtitle.utils.JobtitleUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * 
 * <p>Title:ReviewMeetingBo </p>
 * <p>Description: 评审会议操作Bo类</p>
 * <p>Company: hjsj</p> 
 * <p>create time: 2015-12-31</p>
 * @author liuy
 * @version 1.0
 */
public class ReviewScorecountBo {
	Connection conn;
    UserView userview;
    
    public ReviewScorecountBo() {

    }
    public ReviewScorecountBo(Connection conn) {
        this.conn = conn;
    }
    public ReviewScorecountBo(Connection conn, UserView userview) {
        this.conn = conn;
        this.userview = userview;
    }
    public ReviewScorecountBo(Connection conn, UserView userview, String w0301, String reviewLinks) {
        this.conn = conn;
        this.userview = userview;
        this.w0301 = w0301;
        this.reviewLinks = reviewLinks;
    }
    // 统计 锁列
 	public static String islock = ",w0507,w0509,w0511,";
 	// 分数统计 排除指标 : 
 	public static String exceptScoreFields = ",group_id,w0517,w0519,w0521,w0523,w0525,w0527,w0529,w0531,w0533,w0536,w0537,w0539,w0541,w0543,w0545,w0547,w0549,w0551,w0553,w0555,w0557,w0559,w0561,w0563,w0565,w0567,w0569,w0571,w0573,w0575,";
 	// 票数统计 排除指标 : 
 	public static String exceptPollFields = ",w0536,w0537,w0555,w0573,w0575,w0539,w0541,";
    /**
     * 会议id 
     */
    private String w0301; 
    /**
     * 评审环节 1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段 
     */
    private String reviewLinks; 
    
    private boolean isFinished;
    
    public boolean isFinished() {
		return isFinished;
	}
	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}
	/**
     * 获取会议都进行了哪些阶段，即当前阶段之前的所有阶段（包括当前阶段）
     * @return	,1,2,
     * @throws GeneralException
     */
    public String getReviewMeetingMent() throws GeneralException {
    	
    	String ments = ",";
    	try {
			ContentDAO dao = new ContentDAO(this.conn);
	    	RecordVo w03Vo = new RecordVo("w03");
			w03Vo.setString("w0301", w0301);
			w03Vo = dao.findByPrimaryKey(w03Vo);
			String xmlDoc = w03Vo.getString("extend_param");
			ReviewMeetingPortalBo reviewMeetingPortalBo = new ReviewMeetingPortalBo(this.userview, this.conn);
			List<LazyDynaBean> segments = reviewMeetingPortalBo.getXmlParamByW03(xmlDoc);
			LazyDynaBean ldb = new LazyDynaBean();
			for(int i=0;i<segments.size();i++) {
				ldb = segments.get(i);
				// 1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段 
				String flag = (String) ldb.get("flag");
				ments += flag + ",";
				if(StringUtils.isNotEmpty(reviewLinks) && reviewLinks.equals(flag))
					break;
			}
    	
    	} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}
    	return ments;
    }
    /**
     * 获取该会议当前阶段的测评表配置
     * @return
     * @throws GeneralException
     */
    public String getReviewMeetingTemplateids() throws GeneralException {
    	
    	String ments = ",";
    	try {
			ContentDAO dao = new ContentDAO(this.conn);
	    	RecordVo w03Vo = new RecordVo("w03");
			w03Vo.setString("w0301", w0301);
			w03Vo = dao.findByPrimaryKey(w03Vo);
			String xmlDoc = w03Vo.getString("extend_param");
			ReviewMeetingPortalBo reviewMeetingPortalBo = new ReviewMeetingPortalBo(this.userview, this.conn);
			List<LazyDynaBean> segments = reviewMeetingPortalBo.getXmlParamByW03(xmlDoc);
			LazyDynaBean ldb = new LazyDynaBean();
			for(int i=0;i<segments.size();i++) {
				ldb = segments.get(i);
				// 1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段 
				String flag = (String) ldb.get("flag");
				if(StringUtils.isNotEmpty(reviewLinks) && reviewLinks.equals(flag)) {
					ments = (String) ldb.get("template");
					break;
				}
			}
    	
    	} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}
    	return ments;
    }
    
 public String getReviewMeetingTemplateids(String segment) throws GeneralException {
    	
    	String ments = ",";
    	try {
			ContentDAO dao = new ContentDAO(this.conn);
	    	RecordVo w03Vo = new RecordVo("w03");
			w03Vo.setString("w0301", w0301);
			w03Vo = dao.findByPrimaryKey(w03Vo);
			String xmlDoc = w03Vo.getString("extend_param");
			ReviewMeetingPortalBo reviewMeetingPortalBo = new ReviewMeetingPortalBo(this.userview, this.conn);
			List<LazyDynaBean> segments = reviewMeetingPortalBo.getXmlParamByW03(xmlDoc);
			LazyDynaBean ldb = new LazyDynaBean();
			for(int i=0;i<segments.size();i++) {
				ldb = segments.get(i);
				// 1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段 
				String flag = (String) ldb.get("flag");
				if(StringUtils.isNotEmpty(segment) && segment.equals(flag)) {
					ments = (String) ldb.get("template");
					break;
				}
			}
    	
    	} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}
    	return ments;
    }
    /**
	 * 获取表格控件配置
	 * @param subModuleId
	 * @param title
	 * @param evaluationType：1:投票  2：评分
	 * @return
	 * @throws GeneralException
	 */
	public String getTableConfigForDiff(String subModuleId, String evaluationType, String groupids) throws GeneralException {
		
		String config = "";
		try {
			// 获取列头集合
			ArrayList<ColumnsInfo> columnsList = new ArrayList<ColumnsInfo>();
			// 获取sql
			StringBuffer datasql =  new StringBuffer();
			// 获取分组id的SQL查询条件
			String groupidSql = this.getGroupidSql(groupids);
			columnsList = this.getPollColumnList(ReviewScorecountBo.exceptPollFields, ReviewScorecountBo.islock, groupidSql);
			datasql.append(this.getPollcountSql(groupidSql));
			TableConfigBuilder builder = new TableConfigBuilder(subModuleId, columnsList, subModuleId, userview, this.conn);
			
			builder.setDataSql(datasql.toString());
			builder.setOrderBy(" order by categories_id,queue,w0501 ");
			builder.setColumnFilter(true);//统计过滤
			if(this.userview.hasTheFunction("380050509")){//栏目设置权限
				builder.setScheme(true);//栏目设置
				builder.setSetScheme(true);
				builder.setShowPublicPlan(this.userview.hasTheFunction("38005050901"));//公有
				builder.setSchemePosition("ReviewScorecount_schemeSetting");
			}
			builder.setSelectable(true);//选框
			builder.setEditable(true);//表格编辑
			builder.setPageSize(20);//每页条数
			builder.setLockable(true);
			builder.setSchemeSaveCallback("reviewScorecount_me.schemeSaveCallback");
			ArrayList buttonList = this.getButtonList(evaluationType);//得到操作按钮
			builder.setTableTools(buttonList);//表格工具栏功能
			config = builder.createExtTableConfig();
			
		}catch(Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}
		
		return config;
	}
	
	/**
	 * 获取表格控件快速查询条件语句
	 * @param type			1为输入查询，2为方案查询
	 * @param valuesList	快速输入多个条件
	 * @param exp			方案查询 参数
	 * @param cond			方案查询 参数
	 * @return
	 * @throws GeneralException
	 */
	public String getTableConfigSeachSql(String type, ArrayList<String> valuesList, String exp, String cond) throws GeneralException {
		// 拼接sql
        StringBuffer sql = new StringBuffer("");
		try {
			if ("1".equals(type)) {
            	// 输入的内容
                if (valuesList.size() > 0) {
                    String where = this.getWhereSql(valuesList);
                    if (!StringUtils.isEmpty(where))
                        sql.append(" and (" + where + ")");
                }
            } else {
            	if (cond.length() < 1 || exp.length() < 1) 
                    return "";
            	
                sql.append(" and ");
                FactorList parser = new FactorList(exp, cond, userview.getUserName());
                sql.append(parser.getSingleTableSqlExpression("myGridData"));
            }
			
		}catch(Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}
		return sql.toString();
	}
	
    /**
     * 获取分组名称id对象集合
     * @return	
     */
    public ArrayList<CommonData> getGroupsList(String groupidSql) {
    	
    	ArrayList<CommonData> operationData = new ArrayList<CommonData>();
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer groupSql = new StringBuffer("");
			groupSql.append("select DISTINCT Z.name dataName,Z.categories_id dataValue ");
			groupSql.append(" from W05 W ");
			groupSql.append(" left join (zc_categories_relations C left join zc_personnel_categories Z on C.categories_id=Z.categories_id ");
			// 增加分组id条件
			if(StringUtils.isNotEmpty(groupidSql)) 
	    		groupSql.append(groupidSql);
			groupSql.append(" ) ");
			
			groupSql.append(" on W.W0501=C.W0501");
			groupSql.append(" where Z.W0301=? and Z.Review_links=? ");
			
			ArrayList list = new ArrayList();
			list.add(w0301);
			list.add(reviewLinks);
			
			ArrayList valueList = new ArrayList();
			CommonData cd = new CommonData();
			rs = dao.search(groupSql.toString(), list);
			while (rs.next()) {
				cd = new CommonData();
				cd.setDataName(rs.getString("dataName"));
				cd.setDataValue(rs.getString("dataValue"));
				operationData.add(cd);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
    	
    	return operationData;
    }
    /**
     * 拼接分组id的SQL查询条件语句
     * 注意Z.categories_id 
     * @param groupids
     * @return
     */
	public String getGroupidSql(String groupids) {
    	StringBuffer groupSql = new StringBuffer("");
    	// 增加分组id条件
		if(StringUtils.isNotEmpty(groupids)) {
			String groupidsSql = "";
			String[] tmps = groupids.split(",");
			for(int i=0; i<tmps.length; i++){
				if(StringUtils.isEmpty(tmps[i])) 
					continue;
				groupidsSql += "'" + tmps[i] + "'"; 
				if(i < tmps.length-1)
					groupidsSql += ",";
			}
			groupSql.append(" and Z.categories_id in (").append(groupidsSql).append(")");
		}
    	return groupSql.toString();
    }
    /**
	 * 获取票数统计列头
	 * 
	 * 赞成人数（A），反对人数（B），弃权人数（	C），状态（D）
	 * ps:外部鉴定专家
	 * 1、ABCD一列都不显示，就不显示整个专家块，并且在启动评审阶段不能选择
	 * 2、只要有ABCD其一，就显示复合表头、【鉴定专家】和有的列
	 * 3、只要有A，就显示复合表头、【鉴定专家】和【赞成人数占比】列
	 * 
	 * @param exceptFields
	 *            不需要指标
	 * @param islock
	 *            需要锁列
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList getPollColumnList(String exceptFields, String islock, String groupidSql)throws GeneralException {
		
		ArrayList columnsList = new ArrayList();
		ColumnsInfo columnsInfo = new ColumnsInfo();
		// 业务字典指标
		ArrayList fieldList = DataDictionary.getFieldList("W05", 1);
		// 获取当前进行的所有阶段	1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段
		String ments = this.getReviewMeetingMent();
		ReviewMeetingPortalBo rmpb = new ReviewMeetingPortalBo(userview,conn);
		LazyDynaBean meetingBean = rmpb.getMeetingData(w0301);
		boolean expertFlg = false, subjectsFlg = false, committeeFlg = false,collegeFlg = false;
		//各阶段投票状态可编辑，填充数据
		ArrayList<CommonData> statusData = new ArrayList<CommonData>();
		CommonData cd = new CommonData();
		cd.setDataName("通过");
		cd.setDataValue("01");
		statusData.add(cd);
		cd = new CommonData();
		cd.setDataName("未通过");
		cd.setDataValue("02");
		statusData.add(cd);
		for (int i = 0; i < fieldList.size(); i++) {
			FieldItem fi = (FieldItem) fieldList.get(i);
			// 去除没有启用的指标
			if (!"1".equals(fi.getUseflag()))
				continue;
			// 去除隐藏的指标
			if (!"1".equals(fi.getState()))
				continue;
			
			String itemid = fi.getItemid();
			// 去除不需要的指标
			if (exceptFields.indexOf("," + itemid.toLowerCase() + ",") != -1)
				continue;
			// 测评表符合列头单独处理 
			if(itemid.toLowerCase().startsWith("c_") || itemid.toLowerCase().endsWith("_seq"))
				continue ;
			
			columnsInfo = getColumnsInfoByFi(fi, 80);
			columnsInfo.setEditableValidFunc("false");
			// 外部专家
			if("W0527".equalsIgnoreCase(itemid)//反对人数
					||"W0523".equalsIgnoreCase(itemid)
					|| "W0529".equalsIgnoreCase(itemid)//弃权人数
					|| "W0531".equalsIgnoreCase(itemid) //赞成人数
					|| "W0533".equalsIgnoreCase(itemid)) {//状态
				// 会议是否进行到该阶段(!"3".equals(reviewLinks))
				if( !ments.contains(",3,"))
					continue;
				// 已加载过
				if(expertFlg)
					continue;
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
				expertInfo.setRendererFunc("reviewScorecount_me.checkProficient");
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
							columnsInfo.setRendererFunc("reviewScorecount_me.w0531");
						}else if("W0527".equalsIgnoreCase(itemId)){//反对
							columnsInfo.setRendererFunc("reviewScorecount_me.w0527");
						}else if("W0529".equalsIgnoreCase(itemId)){//弃权
							columnsInfo.setRendererFunc("reviewScorecount_me.w0529");
						}else if("W0533".equalsIgnoreCase(itemId)){
							columnsInfo.setRendererFunc("reviewScorecount_me.status");
							w0533 = columnsInfo;
							w0533.setOperationData(statusData);
							if(!"3".equals(reviewLinks) || isFinished)
								w0533.setEditableValidFunc("false");
							continue;
						}
						columnsInfo.setEditableValidFunc("false");
						list.add(columnsInfo);
					}
				}
				if(tempFlag){					
					columnsInfo = getColumnsInfo("proficientagree", "赞成人数占比", 90, "0", "A", 3, 0);
					columnsInfo.setEditableValidFunc("false");
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
			else if("W0521".equalsIgnoreCase(itemid)//专家人数
					|| "W0543".equalsIgnoreCase(itemid)//反对人数
					|| "W0545".equalsIgnoreCase(itemid)//弃权人数
					|| "W0547".equalsIgnoreCase(itemid)//赞成人数
					|| "group_id".equalsIgnoreCase(itemid)// 专家鉴定问卷计划号
					|| "W0557".equalsIgnoreCase(itemid)) {//状态
				// 会议是否进行到该阶段(!"2".equals(reviewLinks))
				if(!ments.contains(",2,"))
					continue;
				String evaluationType = (String)meetingBean.get("evaluationType_2");
				if("2".equals(evaluationType) && !subjectsFlg) {//打分
					//学科组阶段下的打分列
					columnsList.addAll(this.getScoreColumnList("2"));
					subjectsFlg = true;
				}
				// 已加载过
				if(subjectsFlg)
					continue;
				ColumnsInfo info = new ColumnsInfo();
				info.setQueryable(false);
				info.setColumnDesc(JobtitleUtil.ZC_REVIEWFILE_STEP2SHOWTEXT);
				info.setColumnId("subject_hb");//为合并列设置id
				ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
				//当前环节增加赞成名额列
				FieldItem  agree  = DataDictionary.getFieldItem("W0547", 1);
				String itemdesc = agree.getItemdesc().replace("人数", "");
				ColumnsInfo agreeColumn = new ColumnsInfo();
				agreeColumn.setQueryable(false);
				agreeColumn.setColumnDesc(itemdesc+ResourceFactory.getProperty("zc_new.zc_reviewcConsole.personCount"));
				agreeColumn.setColumnId("agrre_number_2");
				agreeColumn.setColumnWidth(70);
				agreeColumn.setEditableValidFunc("false");
				list.add(agreeColumn);
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
							columnsInfo.setRendererFunc("reviewScorecount_me.w0547");
						}else if("W0543".equalsIgnoreCase(itemId)){//反对
							columnsInfo.setRendererFunc("reviewScorecount_me.w0543");
						}else if("W0545".equalsIgnoreCase(itemId)){//弃权
							columnsInfo.setRendererFunc("reviewScorecount_me.w0545");
						}else if("W0557".equalsIgnoreCase(itemId)){
							columnsInfo.setRendererFunc("reviewScorecount_me.status");
							w0557 = columnsInfo;
							w0557.setOperationData(statusData);
							if(!"2".equals(reviewLinks) || isFinished)
								w0557.setEditableValidFunc("false");
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
						columnsInfo.setRendererFunc("reviewScorecount_me.w0521");
						columnsInfo.setEditableValidFunc("false");
						list.add(columnsInfo);
						
					} else if("group_id".equalsIgnoreCase(itemId)){
						// 37600 经讨论暂时不显示 学科组阶段/评委会阶段/二级单位
//						columnsInfo.setQueryable(false);
//						columnsInfo.setColumnDesc(JobtitleUtil.ZC_REVIEWFILE_STEP2SHOWTEXT);
//						columnsInfo.setCodesetId("0");// 指标集
//						columnsInfo.setColumnType("A");// 类型N|M|A|D
////						columnsInfo.setOperationData("group_id");
//						columnsInfo.setColumnWidth(100);
////						columnsInfo.setEditableValidFunc("reviewfile_me.createGroup");
//						list.add(0, columnsInfo);
					}
				}
				if(tempFlag){					
					columnsInfo = getColumnsInfo("subjectsagree", "赞成人数占比", 90, "0", "A", 3, 0);
					columnsInfo.setEditableValidFunc("false");
//					columnsInfo.setRendererFunc("reviewfile_me.subjectsagree");
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
			// 评委会
			else if("W0517".equalsIgnoreCase(itemid)//评委人数
					|| "W0519".equalsIgnoreCase(itemid)//参会评委人数
					|| "W0549".equalsIgnoreCase(itemid)//反对人数
					|| "W0551".equalsIgnoreCase(itemid)//弃权人数
					|| "W0553".equalsIgnoreCase(itemid)//赞成人数
					|| "W0559".equalsIgnoreCase(itemid)) {//状态
				// 会议是否进行到该阶段(!"1".equals(reviewLinks))
				if(!ments.contains(",1,"))
					continue;
				String evaluationType = (String)meetingBean.get("evaluationType_1");
				if("2".equals(evaluationType) && !committeeFlg) {//打分
					//评委会阶段下的打分列
					columnsList.addAll(this.getScoreColumnList("1"));
					committeeFlg = true;
				}
				// 已加载过
				if(committeeFlg)
					continue;
				ColumnsInfo info = new ColumnsInfo();
				info.setQueryable(false);
				info.setColumnDesc(JobtitleUtil.ZC_REVIEWFILE_STEP1SHOWTEXT);
				info.setColumnId("committee_hb");//为合并列设置id
				ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
				
				//当前环节增加赞成名额列
//				if("1".equals(reviewLinks)) {
					FieldItem  agree  = DataDictionary.getFieldItem("W0553", 1);
					String itemdesc = agree.getItemdesc().replace("人数", "");
					ColumnsInfo agreeColumn = new ColumnsInfo();
					agreeColumn.setQueryable(false);
					agreeColumn.setColumnDesc(itemdesc+ResourceFactory.getProperty("zc_new.zc_reviewcConsole.personCount"));
					agreeColumn.setColumnId("agrre_number_1");
					agreeColumn.setColumnWidth(70);
					agreeColumn.setEditableValidFunc("false");
					list.add(agreeColumn);
//				}
				
				ColumnsInfo w0559 = null;
				boolean tempFlag = false;//是否显示赞成人数占比
				for (int j = 0; j < fieldList.size(); j++) {
					FieldItem item = (FieldItem) fieldList.get(j);
					String itemId = item.getItemid();
					if (("W0517".equalsIgnoreCase(itemId)//评委人数
							|| "W0519".equalsIgnoreCase(itemId)//参会评委人数
							|| "W0549".equalsIgnoreCase(itemId)//反对人数
							|| "W0551".equalsIgnoreCase(itemId)//弃权人数
							|| "W0553".equalsIgnoreCase(itemId)//赞成人数
							|| "W0559".equalsIgnoreCase(itemId))&&item.isVisible()==true) {//状态
						if("W0519".equalsIgnoreCase(itemId))
							continue;
						if("W0553".equalsIgnoreCase(itemId))
							tempFlag = true;
						columnsInfo = getColumnsInfoByFi(item, 70);
						columnsInfo.setQueryable(false);
						
						if("W0517".equalsIgnoreCase(itemId)){//评委人数
							columnsInfo.setColumnDesc("已评数/总数");
							columnsInfo.setColumnType("A");
							columnsInfo.setCodesetId("0");// 指标集
							columnsInfo.setColumnWidth(80);
							columnsInfo.setTextAlign("center");
							columnsInfo.setRendererFunc("reviewScorecount_me.w0517");
						}
						
						if("W0553".equalsIgnoreCase(itemId)){//赞成
							columnsInfo.setRendererFunc("reviewScorecount_me.w0553");
						}else if("W0549".equalsIgnoreCase(itemId)){//反对
							columnsInfo.setRendererFunc("reviewScorecount_me.w0549");
						}else if("W0551".equalsIgnoreCase(itemId)){//弃权
							columnsInfo.setRendererFunc("reviewScorecount_me.w0551");
						}else if("W0559".equalsIgnoreCase(itemId)){
							columnsInfo.setRendererFunc("reviewScorecount_me.status");
							w0559 = columnsInfo;
							w0559.setOperationData(statusData);
							if(!"1".equals(reviewLinks) || isFinished)
								w0559.setEditableValidFunc("false");
							continue;
						}
						columnsInfo.setEditableValidFunc("false");
						list.add(columnsInfo);
					}
				}
				if(tempFlag){					
					columnsInfo = getColumnsInfo("committeeagree", "赞成人数占比", 90, "0", "A", 3, 0);
					columnsInfo.setEditableValidFunc("false");
//					columnsInfo.setRendererFunc("reviewfile_me.committeeagree");
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
			if("W0563".equalsIgnoreCase(itemid)//反对人数
					|| "W0565".equalsIgnoreCase(itemid)//弃权人数
					|| "W0567".equalsIgnoreCase(itemid)//赞成人数
					|| "W0569".equalsIgnoreCase(itemid)//状态
					|| "W0561".equalsIgnoreCase(itemid)//学院聘任组
					|| "W0571".equalsIgnoreCase(itemid)){//聘任组人数
				// 会议是否进行到该阶段(!"4".equals(reviewLinks))
				if(!ments.contains(",4,"))
					continue;
				String evaluationType = (String)meetingBean.get("evaluationType_4");
				if("2".equals(evaluationType) && !collegeFlg) {//打分
					//评委会阶段下的打分列
					columnsList.addAll(this.getScoreColumnList("4"));
					collegeFlg = true;
				}
				// 已加载过   
				if(collegeFlg)
					continue;
				ColumnsInfo info = new ColumnsInfo();
				info.setQueryable(false);
				info.setColumnDesc(JobtitleUtil.ZC_REVIEWFILE_STEP4SHOWTEXT);
				info.setColumnId("college_hb");	//为合并列设置id
				ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
				
				//当前环节增加赞成名额列
//				if("4".equals(reviewLinks)) {
					FieldItem  agree  = DataDictionary.getFieldItem("W0553", 1);
					String itemdesc = agree.getItemdesc().replace("人数", "");
					ColumnsInfo agreeColumn = new ColumnsInfo();
					agreeColumn.setQueryable(false);
					agreeColumn.setColumnDesc(itemdesc+ResourceFactory.getProperty("zc_new.zc_reviewcConsole.personCount"));
					agreeColumn.setColumnId("agrre_number_4");
					agreeColumn.setColumnWidth(70);
					agreeColumn.setEditableValidFunc("false");
					list.add(agreeColumn);
//				}
				
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
							columnsInfo.setRendererFunc("reviewScorecount_me.w0567");
						}else if("W0563".equalsIgnoreCase(itemId)){//反对
							columnsInfo.setRendererFunc("reviewScorecount_me.w0563");
						}else if("W0565".equalsIgnoreCase(itemId)){//弃权
							columnsInfo.setRendererFunc("reviewScorecount_me.w0565");
						}else if("W0569".equalsIgnoreCase(itemId)){
							columnsInfo.setRendererFunc("reviewScorecount_me.status");
							W0569 = columnsInfo;
							W0569.setOperationData(statusData);
							if(!"4".equals(reviewLinks) || isFinished)
								W0569.setEditableValidFunc("false");
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
						columnsInfo.setColumnType("A");
						columnsInfo.setCodesetId("0");// 指标集
						W0571.setColumnWidth(80);
						W0571.setTextAlign("center");
						W0571.setRendererFunc("reviewScorecount_me.W0571");
						W0571.setEditableValidFunc("false");
						list.add(W0571);
					}
					else if("W0561".equalsIgnoreCase(itemId)){//学院聘任组
						// 37600 经讨论暂时不显示 学科组阶段/评委会阶段/二级单位
//						ColumnsInfo W0561 = new ColumnsInfo();
//						W0561.setQueryable(false);
//						W0561.setColumnId("committeeName");
//						W0561.setColumnDesc(JobtitleUtil.ZC_REVIEWFILE_STEP4SHOWTEXT);
//						W0561.setColumnWidth(100);
//						W0561.setEditableValidFunc("false");
//						list.add(0,W0561);
					}
					
				}
				if(tempFlag){					
					columnsInfo = getColumnsInfo("collegeagree", "赞成人数占比", 90, "0", "A", 3, 0);
					columnsInfo.setEditableValidFunc("false");
//					columnsInfo.setRendererFunc("reviewfile_me.collegeagree");
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
			// 现聘职称、申报职称  现库中存的是字符型  实际上是AJ代码项下对应指标
			else if ("w0513".equalsIgnoreCase(itemid) || "w0515".equalsIgnoreCase(itemid)) {
				columnsInfo.setColumnType("A");
				columnsInfo.setCodesetId("AJ");
				columnsInfo.setCtrltype("3");
				columnsInfo.setNmodule("9");
			}else if ("W0535".equalsIgnoreCase(itemid)) {// 评审材料
				columnsInfo.setColumnId("w0535");
                columnsInfo.setColumnWidth(100);// 显示列宽
				columnsInfo.setColumnDesc(fi.getItemdesc());
				columnsInfo.setEditableValidFunc("false");
				columnsInfo.setRendererFunc("reviewScorecount_me.w0535");
				columnsInfo.setTextAlign("center");
				columnsInfo.setQueryable(false);
				columnsInfo.setBeExport(false);
			} 
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
				columnsInfo.setTextAlign("right");
			}

			// 需要锁列
			if (!StringUtils.isEmpty(islock)) {
				if (islock.indexOf("," + itemid + ",") != -1) {
					columnsInfo.setLocked(true);
					columnsList.add(0, columnsInfo);// 需要锁列的放到第一列，不然导出时顺序会错。
					continue;
				}
			}
			
			columnsList.add(columnsInfo);
		}

		// 自定义列
		columnsInfo = getColumnsInfo("categories_id", "组名", 180, "0", "A", 100, 0);
		columnsInfo.setQueryable(false);
		columnsInfo.setLocked(true);
		columnsInfo.setEditableValidFunc("false");
		
		ArrayList<CommonData> operationData = getGroupsList(groupidSql);
		columnsInfo.setOperationData(operationData);
		// 按组名过滤
		columnsInfo.setDoFilterOnLoad(true);
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
		columnsInfo.setColumnId("w0523");
		columnsInfo.setColumnDesc("外部鉴定专家人数");
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
        columnsInfo.setColumnId("w0536");
        columnsInfo.setColumnDesc("评审材料word模板");
        columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
        columnsList.add(columnsInfo);

		return columnsList;
	}
	
    /**
     * 获取分数统计列头
     * @param exceptFields	需要排除的指标
     * @param islock		需要锁列的指标
     * @param perTemplates	测评表配置
     * @return
     * @throws GeneralException
     */
	@SuppressWarnings("unchecked")
	public List<ColumnsInfo> getScoreColumnList(String segment)throws GeneralException {
		// 获取该会议当前阶段的测评表配置
		ArrayList<ColumnsInfo> columns = new ArrayList<ColumnsInfo>();
		String templates = this.getReviewMeetingTemplateids(segment);
		String[] perTemps = templates.split(",");
		String segmentName = "";
		if("1".equals(segment)) {
			segmentName = JobtitleUtil.ZC_REVIEWFILE_STEP1SHOWTEXT;
		}
		else if("2".equals(segment)) {
			segmentName = JobtitleUtil.ZC_REVIEWFILE_STEP2SHOWTEXT;
		}
		else if("3".equals(segment)) {
			segmentName = JobtitleUtil.ZC_REVIEWFILE_STEP3SHOWTEXT;
		}else {
			segmentName = JobtitleUtil.ZC_REVIEWFILE_STEP4SHOWTEXT;
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
			cosu = getColumnsInfo(per+"_"+segment+"_tml", desc+"【"+segmentName+"】", 220, "0", "A", 100, 0);
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
			columnsInfo.setColumnId("C_"+per+"_seq_"+segment);
			columnsInfo.setColumnDesc("排名");
			columnsInfo.setColumnType("A");
			columnsInfo.setQueryable(false);
			columnsInfo.setColumnWidth(80);
			columnsInfo.setEditableValidFunc("false");
			columnsInfo.setTextAlign("right");
			cosu.addChildColumn(columnsInfo);
			
			columns.add(cosu);
			
		}
	//	segmentCol.setChildColumns(columns);
		return columns;
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
		int columnLength = fi.getItemlength();// 显示长度
		int decimalWidth = fi.getDecimalwidth();// 小数位
		co = getColumnsInfo(itemid, itemdesc, columnWidth, codesetId,
				columnType, columnLength, decimalWidth);
		
		return co;
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
//		columnsInfo.setFieldsetid(columnId);
		columnsInfo.setCodesetId(codesetId);// 指标集
		columnsInfo.setColumnType(columnType);// 类型N|M|A|D
		columnsInfo.setColumnLength(columnLength);// 显示长度
		columnsInfo.setDecimalWidth(decimalWidth);// 小数位
//		columnsInfo.setReadOnly(true);// 是否只读
//		columnsInfo.setEditableValidFunc("reviewfile_me.checkCell");

		return columnsInfo;
	}
	
    /**
     * 表格工具栏功能按钮
	 * @param evaluationType：1:投票  2：评分
     * @return
     */
	@SuppressWarnings("unchecked")
	public ArrayList getButtonList(String evaluationType) {
		ArrayList buttonList  = new ArrayList();
		// 导出 按钮
		ButtonInfo buttonInfo = new ButtonInfo();
		buttonInfo.setText("导出");
		buttonInfo.setFunctype("excel");
		buttonList.add(buttonInfo);
		if("2".equals(evaluationType)) {
			buttonInfo = new ButtonInfo("导出明细", "reviewScorecount_me.exportScoreDetails");
			buttonList.add(buttonInfo);
		}
				
		// 返回 按钮
		buttonInfo = new ButtonInfo("返回", "reviewScorecount_me.callback");
		buttonList.add(buttonInfo);
		
		ButtonInfo queryBox = new ButtonInfo();
		queryBox = new ButtonInfo();
		queryBox.setFunctionId("ZC00002316");
		queryBox.setType(ButtonInfo.TYPE_QUERYBOX);
		queryBox.setText("请输入单位名称、部门、姓名...");
		buttonList.add(queryBox);
		
		return buttonList;
	}
    
    /**
     * 获取分数统计查询语句
     * @param W0301			会议ID
     * @param reviewLinks	评审环节1:论文送审 2：学科组 3：聘委会 
     * @return
     * @throws GeneralException
     */
	public String getScorecountSql(String groupidSql)throws GeneralException {
		
		StringBuffer datasql =  new StringBuffer();
		datasql.append("select Z.name groupName,Z.categories_id categories_id");
		datasql.append(",W.w0501 as w0501_safe,W.w0503 as w0503_safe,W.w0505 as w0505_safe ");
		datasql.append(",W.w0503"+Sql_switcher.concat()+"W.w0505 as nbasea0100,W.w0503"+Sql_switcher.concat()+"'`'"+Sql_switcher.concat()+"W.w0505 as nbasea0100_1");
		datasql.append(",W.* from W05 W ");
		datasql.append(" left join (zc_categories_relations C left join zc_personnel_categories Z on C.categories_id=Z.categories_id ");
		// 增加分组id条件
		if(StringUtils.isNotEmpty(groupidSql)) 
    		datasql.append(groupidSql);
		datasql.append(" ) ");
		
		datasql.append(" on W.W0501=C.W0501	");
		datasql.append(" where Z.W0301='").append(w0301).append("' and Z.Review_links=").append(reviewLinks).append(" ");
		
		return datasql.toString();
	}
	
    /**
	 * 获取票数统计查询语句
	 * @param W0301			会议ID
     * @param reviewLinks	评审环节1:论文送审 2：学科组 3：聘委会 
     * @return
     * @throws GeneralException
	 */
	public String getPollcountSql(String groupidSql)throws GeneralException {
		
		StringBuilder sql = new StringBuilder();
		StringBuilder selectSql = new StringBuilder();
		sql.append("select Z.name groupName,Z.categories_id categories_id,");
		ArrayList<FieldItem> fieldList = DataDictionary.getFieldList("W05", Constant.USED_FIELD_SET);
		for (int i = 0; i < fieldList.size(); i++) {
			FieldItem fi = (FieldItem) fieldList.get(i);
			String itemid = fi.getItemid();
			if(itemid.toLowerCase().startsWith("c_") || itemid.toLowerCase().endsWith("_seq")) {
				continue;
			}
			if("w0571".equalsIgnoreCase(itemid)) {//二级单位人数
				selectSql.append(Sql_switcher.sqlToChar(Sql_switcher.isnull("w0563","0")+"+"+Sql_switcher.isnull("w0565","0")+"+"+Sql_switcher.isnull("w0567","0"))+Sql_switcher.concat()+"'/'"+Sql_switcher.concat()+Sql_switcher.sqlToChar(Sql_switcher.isnull("w0571","0"))+" as w0571,");
				selectSql.append("(select Z1.c_number from zc_personnel_categories z1,zc_categories_relations z2 WHERE W0301="+w0301+" and Review_links=4 and z1.categories_id=z2.categories_id and z2.w0501=w05.w0501) as agrre_number_4");
			}else if("w0523".equalsIgnoreCase(itemid)){//外部鉴定专家人数
				selectSql.append(Sql_switcher.sqlToChar(Sql_switcher.isnull("w0527","0")+"+"+Sql_switcher.isnull("w0529","0")+"+"+Sql_switcher.isnull("w0531","0"))+Sql_switcher.concat()+"'/'"+Sql_switcher.concat()+Sql_switcher.sqlToChar(Sql_switcher.isnull("w0523","0"))+" as checkProficient");
			}else if("w0521".equalsIgnoreCase(itemid)){//专业（学科）组
				selectSql.append(Sql_switcher.sqlToChar(Sql_switcher.isnull("w0543","0")+"+"+Sql_switcher.isnull("w0545","0")+"+"+Sql_switcher.isnull("w0547","0"))+Sql_switcher.concat()+"'/'"+Sql_switcher.concat()+Sql_switcher.sqlToChar(Sql_switcher.isnull("w0521","0"))+" as w0521,");
				selectSql.append("(select Z1.c_number from zc_personnel_categories z1,zc_categories_relations z2 where W0301="+w0301+" and Review_links=2 and z1.categories_id=z2.categories_id and z2.w0501=w05.w0501) as agrre_number_2");
			}else if("w0517".equalsIgnoreCase(itemid)){//评委会人数
				selectSql.append(Sql_switcher.sqlToChar(Sql_switcher.isnull("w0549","0")+"+"+Sql_switcher.isnull("w0551","0")+"+"+Sql_switcher.isnull("w0553","0"))+Sql_switcher.concat()+"'/'"+Sql_switcher.concat()+Sql_switcher.sqlToChar(Sql_switcher.isnull("w0517","0"))+" as w0517,");
				selectSql.append("(select Z1.c_number from zc_personnel_categories z1,zc_categories_relations z2 where W0301="+w0301+" and Review_links=1 and z1.categories_id=z2.categories_id and z2.w0501=w05.w0501) as agrre_number_1");
			}else {
				selectSql.append("w05."+itemid);
			}
			selectSql.append(",");
		}
		sql.append(selectSql);
		
		//打分阶段数据列  haosl  start
		String ments = this.getReviewMeetingMent();
		ReviewMeetingPortalBo rmpb = new ReviewMeetingPortalBo(userview,conn);
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
				String templates = this.getReviewMeetingTemplateids(ment);
				String[] perTemps = templates.split(",");
				for(int j = 0; j < perTemps.length; j++) {
					String per = perTemps[j];
					sql.append("(select score from kh_object where Relation_id='1_"+w0301+"_"+ment+"' and Object_id=w05.W0505 and template_id='"+per+"') as C_"+per+"_"+ment);
					sql.append(",(select case when "+Sql_switcher.isnull("score", "-1000")+"=-1000 then null else seq end as seq from kh_object where Relation_id='1_"+w0301+"_"+ment+"' and Object_id=w05.W0505 and template_id='"+per+"') as C_"+per+"_seq_"+ment+",");
				}
				
			}
		}
		//打分阶段数据列  haosl  end
		sql.append("w05.w0301,w05.w0501 as w0501_safe");
		sql.append(",w05.w0503 as w0503_safe");
		sql.append(",w03.w0303 as meetingname ");
		sql.append(",w05.w0301 as w0301_safe");
		sql.append(",W0321,w03.b0110");
		sql.append(",w05.w0505 as w0505_safe ");
		sql.append(",zc.committee_name as committeeName");
		sql.append(",w05.w0503"+Sql_switcher.concat()+"w05.w0505 as nbasea0100,w05.w0503"+Sql_switcher.concat()+"'`'"+Sql_switcher.concat()+"w05.w0505 as nbasea0100_1 ");
		sql.append(",w0303");
		sql.append(",C.queue");
		// 赞成占比 start
		sql.append(",case when ("+Sql_switcher.isnull("w0549","0")+"+"+Sql_switcher.isnull("w0551","0")+"+"+Sql_switcher.isnull("w0553","0")+")=0 then '0' else cast(cast(cast((w0553)as float)/("+Sql_switcher.isnull("w0549","0")+"+"+Sql_switcher.isnull("w0551","0")+"+"+Sql_switcher.isnull("w0553","0")+")*100 as int)as varchar(10)) end "+Sql_switcher.concat()+"'%'  as committeeagree");
		sql.append(",case when ("+Sql_switcher.isnull("w0543","0")+"+"+Sql_switcher.isnull("w0545","0")+"+"+Sql_switcher.isnull("w0547","0")+")=0 then '0' else cast(cast(cast((w0547)as float)/("+Sql_switcher.isnull("w0543","0")+"+"+Sql_switcher.isnull("w0545","0")+"+"+Sql_switcher.isnull("w0547","0")+")*100 as int)as varchar(10)) end "+Sql_switcher.concat()+"'%'  as subjectsagree");
		sql.append(",case when ("+Sql_switcher.isnull("w0527","0")+"+"+Sql_switcher.isnull("w0529","0")+"+"+Sql_switcher.isnull("w0531","0")+")=0 then '0' else cast(cast(cast((w0531)as float)/("+Sql_switcher.isnull("w0527","0")+"+"+Sql_switcher.isnull("w0529","0")+"+"+Sql_switcher.isnull("w0531","0")+")*100 as int)as varchar(10)) end "+Sql_switcher.concat()+"'%'  as proficientagree");
		sql.append(",case when ("+Sql_switcher.isnull("w0563","0")+"+"+Sql_switcher.isnull("w0565","0")+"+"+Sql_switcher.isnull("w0567","0")+")=0 then '0' else cast(cast(cast((w0567)as float)/("+Sql_switcher.isnull("w0563","0")+"+"+Sql_switcher.isnull("w0565","0")+"+"+Sql_switcher.isnull("w0567","0")+")*100 as int)as varchar(10)) end "+Sql_switcher.concat()+"'%'  as collegeagree");
		// 赞成占比 end
		sql.append(" from w05 ");
		sql.append(" left join zc_committee zc on w05.W0561=zc.committee_id ");
		sql.append(" left join w03 on w05.w0301=w03.w0301 ");
		
		sql.append(" left join (zc_categories_relations C left join zc_personnel_categories Z on C.categories_id=Z.categories_id ");
		// 增加分组id条件
		if(StringUtils.isNotEmpty(groupidSql)) 
    		sql.append(groupidSql);
		sql.append(" ) ");
		
		sql.append(" on w05.W0501=C.W0501	");
		// 传入参数 会议ID W0301 	评审环节1:论文送审 2：学科组 3：聘委会 Review_links
		sql.append(" where Z.W0301='").append(w0301).append("' and Z.Review_links=").append(reviewLinks).append(" ");
		
		sql.append(" and w05.w0301=w03.w0301 ");
		
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
    
    public String getOrgIds(String name) {
        String orgids = "";
        RowSet rs = null;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("select codeitemid from organization O ");
            sql.append(" left join w05 on O.codesetid=w05.W0509 or O.codesetid=w05.W0507");
            sql.append(" where O.codeitemdesc like '%" + name + "%'");
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            while (rs.next()) {
                String codeitemid = rs.getString("codeitemid");
                orgids += ",'" + codeitemid + "'";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return orgids;
    }
    
    /**
     * 分数统计 | 票数统计 	拼接 查询条件
     * 
     * @param valuelist
     *            输入框中的输入的值
     * @return
     */
    public String getWhereSql(ArrayList<String> valuelist) {
        if (valuelist == null || valuelist.size() < 1)
            return "";
        ReviewScorecountBo bo = new ReviewScorecountBo(this.conn, this.userview);
        StringBuffer where = new StringBuffer();
        for (int i = 0; i < valuelist.size(); i++) {
            String value = valuelist.get(i);
            if (StringUtils.isEmpty(value))
                continue;

            value = SafeCode.decode(value);
            if (StringUtils.isEmpty(value))
                continue;

            // 34148 快速查询框内多个条件拼接or时 需补充右括号
            if (i > 0)
                where.append(" or ");
            
            where.append("(");
            where.append(" W0511 like '%" + value + "%'");
            // 获取部门编号
            String orgids = bo.getOrgIds(value);
            where.append(" or W0507 in ('abc'" + orgids + ")");
            where.append(" or W0509 in ('abc'" + orgids + ")");
            where.append(")");
        }

        return where.toString();
    }
    
	public String getReviewLinks() {
		return reviewLinks;
	}
	public void setReviewLinks(String reviewLinks) {
		this.reviewLinks = reviewLinks;
	}
	public String getW0301() {
		return w0301;
	}
	public void setW0301(String w0301) {
		this.w0301 = w0301;
	}
    
	/**
	 * 归档方法
	 */
	
	/**
	 * 分数|票数 归档
	 * @param evaluationType	=1 票数统计 ；	=2 分数统计
	 * @param type				=1未全部完成评审，直接归档 ；=0||空需要判断
	 * @return
	 */
	public String countResultsArchiving(String evaluationType, String type) throws GeneralException {
		String msg = "";

		ReviewFileBo reviewFileBo = new ReviewFileBo(this.conn, userview);
		// 读取评审结果归档方案内容
		HashMap hashMap = reviewFileBo.getResultsArchivingConfig(JobtitleUtil.ZC_REVIEWARCHIVE_STR);	
		if(null==hashMap || hashMap.size()==0){
			msg = "未配置评审结果归档方案，请及时联系管理员！";
			return msg;
		}
		
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			// type非1的时候需要判断
			//归档前需要统计票数和投票状态是否通过
			ReviewScorecountBo scoreBo = new ReviewScorecountBo(this.conn);
			if("1".equals(evaluationType)) {
				scoreBo.asyncPersonNum(this.w0301, Integer.parseInt(reviewLinks));
				scoreBo.asyncStatus(this.w0301);
			}
			msg = checkIfArchiving(evaluationType, type, dao);
			// 若校验后返回信息不为空时 直接返回
			if(StringUtils.isNotEmpty(msg))
				return msg;
			
			// 进行归档操作
			msg = countArchiving(evaluationType, hashMap, dao);
			if(StringUtils.isNotEmpty(msg))
				return msg;
			
			msg = "归档成功！";
		} catch (Exception e) {
			msg = "归档失败！";
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
		
		return msg;
	}
	
	/**
	 * 分数|票数 归档
	 * @param evaluationType	=1 票数统计 ；	=2 分数统计
	 * @param type				=1未全部完成评审，直接归档 ；=0||空需要判断
	 * @param hashMap			归档子集对应字段
	 * @return
	 */
	public String countArchiving(String evaluationType, HashMap hashMap, ContentDAO dao) throws GeneralException {
		
		String msg = "";
		try {
			// 归档到子集中
			msg = fileSubsetMain(hashMap, dao);
			// 若校验后返回信息不为空时 直接返回
			if(StringUtils.isNotEmpty(msg))
				return msg;
			// 对会议的阶段进行归档 等操作
			msg = reviewLinkFileSetMain(evaluationType, dao);
			if(StringUtils.isNotEmpty(msg))
				return msg;
						
			msg = "归档成功！";
		} catch (Exception e) {
			msg = "归档失败！";
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return msg;
	}
	/**
	 * 对会议的阶段进行归档 等操作
	 * @param evaluationType	=1 票数统计  =2 分数统计
	 * @param dao				
	 * @return
	 */
	public String reviewLinkFileSetMain(String evaluationType, ContentDAO dao) throws GeneralException {
		
		String msg = "";
		try {
			/**
			 * 将w03中参数extend_param--
			 * state置为2结束状态 ;archived置为2归档状态
			 * evaluation_type 1:投票  2：评分
			 */
			RecordVo w03Vo = new RecordVo("w03");
			w03Vo.setString("w0301", w0301);
			w03Vo = dao.findByPrimaryKey(w03Vo);
			String xmlDoc = w03Vo.getString("extend_param");
			ReviewMeetingPortalBo reviewMeetingPortalBo = new ReviewMeetingPortalBo(this.userview, this.conn);
			List<LazyDynaBean> segments = reviewMeetingPortalBo.getXmlParamByW03(xmlDoc);
			// 考核计划标识	Relation_id  模块ID_评审会议ID_环节ID	
			String relationids = "";
			// 全部阶段是否全部结束，是否全部归档
			boolean relationFlag = false;
			// 需要更新的阶段对象集合
			List<HashMap<String,String>> maplist = new ArrayList<HashMap<String,String>>();
			// 需要更新的阶段对象
			HashMap<String,String> map = new HashMap<String,String>();
			LazyDynaBean ldb = new LazyDynaBean();
			// 需要更改当前阶段的状态为结束，如果有下一阶段并置为开始
			for(int i=0;i<segments.size();i++) {
				ldb = segments.get(i);
				// 1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段 
				String flag = (String) ldb.get("flag");
				if(reviewLinks.equals(flag)) {
					map = new HashMap<String,String>();
					map.put("segemntIndex", flag);
					// 状态 0：未开始 1：进行中 2：结束
					map.put("state", "2");
					// 是否归档 =1 否 =2 是
					map.put("archived", "2");
					maplist.add(map);
					
					// 如果有下一阶段并置为进行中
					if(i+1 < segments.size()) {
						
						ldb = segments.get(i+1);
						flag = (String) ldb.get("flag");
						
						map = new HashMap<String,String>();
						map.put("segemntIndex", flag);
						map.put("state", "1");
						map.put("archived", "1");
						maplist.add(map);
						// 若有下一阶段则代表还存在未结束的阶段
						relationFlag = true;
					}
					
					relationids += ",'1_"+w0301+"_"+flag+"'";
					break;
				}
				relationids += ",'1_"+w0301+"_"+flag+"'";
			}
			//更新阶段下分组的投票|打分状态
			updateCategorieStatus();
			// 更新阶段状态
			reviewMeetingPortalBo.batchUpdateSegmentsAttr(w0301, maplist);
			// 最终归档
			msg = allFinalFileSetMain(evaluationType, relationFlag, relationids, dao);
			
		} catch (Exception e) {
			msg = "归档失败！";
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return msg;
	}
	/**
	 * 在所有阶段都结束时 对整个会议进行归档
	 * 若是分数统计归档则需要归档到相应考核表中
	 * @param evaluationType	=1 票数统计  =2 分数统计
	 * @param relationFlag		全部阶段是否全部结束，是否全部归档
	 * @param relationids		考核计划标识Relation_id  sql in查询条件 如 '模块ID_评审会议ID_环节ID','1_w0301_2',''		
	 * @param dao
	 * @return
	 * @throws GeneralException
	 */
	public String allFinalFileSetMain(String evaluationType, boolean relationFlag
			, String relationids, ContentDAO dao) throws GeneralException {
		
		String msg = "";
		try {
			// 若存在未结束的阶段 则不允许归档
			if(!relationFlag && StringUtils.isNotEmpty(relationids)) {
				StringBuffer sql = new StringBuffer("");
				sql.append("update W03 set W0321='06' where W0301=?");
				ArrayList valuelist = new ArrayList();
				valuelist.add(w0301);
				// 在全部阶段结束 再置为结束
				dao.update(sql.toString(), valuelist);
				
				// =2 分数统计归档 特殊处理
				if("2".equals(evaluationType)) {
					/**
					 * kh_mainbody  
					 *		考核计划标识	Relation_id  模块ID_评审会议ID_环节ID		
					 *		===>获得  Id(kh_mainbody.id)
					 *	kh_detail	
					 *		Kh_mainbody_id  ==>获得id(kh_detail.id)  或全部数据  
					 *		然后 移到归档表kh_detail_archive
					 */
					// 只有该会议全部阶段都为结束状态时才归档到这一步
					// 归档到kh_detail_archive评分明细归档表
					sql.setLength(0);
					sql.append("insert into kh_detail_archive select * from kh_detail where kh_mainbody_id in(");
					sql.append("select id from kh_mainbody where relation_id in(").append(relationids.substring(1)).append(")");
					sql.append(") ");
					dao.update(sql.toString());
					StartReviewBo bo=new StartReviewBo(this.conn,this.userview);
					//zhanghua 删除会议涉及到的考核对象和考核主体 不删除打分明细 2018年5月19日 10:10:56
					bo.cleanAllKHTableByW0301(w0301,false);
//					// 最后 将kh_mainbody 中Archive_flag 归档标记改为 1（已归档）
//					sql.setLength(0);
//					sql.append("update kh_mainbody set archive_flag=1 where relation_id in(").append(relationids.substring(1)).append(") ");
//					dao.update(sql.toString());
				}
			}
			
		} catch (Exception e) {
			msg = "归档失败！";
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return msg;
	}
	/**
	 * 分数|票数 归档到配置的子集中
	 * @param hashMap	归档子集对应字段
	 * @param dao				
	 * @return
	 */
	public String fileSubsetMain(HashMap hashMap, ContentDAO dao) throws GeneralException {
		
		String msg = "";
		RowSet rs = null;
		try {
			ReviewFileBo reviewFileBo = new ReviewFileBo(this.conn, this.userview);
			StringBuffer sql = new StringBuffer("");
			// 结果归档所有属性名称
			List<String> list = reviewFileBo.getResultsArchivingList(JobtitleUtil.ZC_REVIEWARCHIVE_STR);
			// 存放查询的字段
			List<String> list2 = new ArrayList<String>();	
			for(int i=1;i<list.size();i++){
				String name = (String)hashMap.get(list.get(i));
				// 判断是否设置对应目的指标
				if(StringUtils.isNotBlank(name)){
					String itemid = list.get(i);
					list2.add(itemid);
				}
			}
			
			// 查询人员库
			sql.setLength(0);
			sql.append("select distinct W0503 from W05 left join W03 on W05.W0301=W03.W0301 ");
			sql.append(" and W05.W0555=").append(reviewLinks).append(" ");
			rs = dao.search(sql.toString());
			ArrayList<String> dbList = new ArrayList<String>();
			while(rs.next())
				dbList.add(rs.getString("W0503"));
			if (dbList.size() < 1) 
				return msg;

			// 循环人员库 归档到对应子集
			for(int i=0; i<dbList.size(); i++){
				String tableName = dbList.get(i)+ (String)hashMap.get("fieldset");
				// 置空sql对象
				sql.setLength(0);
				sql.append(" select * ");
				sql.append(" from W05");
				sql.append(" left join W03 on W05.W0301 = W03.W0301 ");
				sql.append(" where W0503=? and W03.W0301=?");
				sql.append(" and W05.W0555=?");
				
				ArrayList<String> valuelist = new ArrayList<String>();
				valuelist.add(dbList.get(i));
				valuelist.add(w0301);
				valuelist.add(reviewLinks);
				
				rs = dao.search(sql.toString(), valuelist);
				// 循环人员
				while(rs.next()){
					RecordVo vo = new RecordVo(tableName);
					// start 如果是有本年的会议名称下有归档信息就更新，没有才插入  
					boolean insertOrUpdate = true;
					int i9999 = 0;
					String meetingNameCol = (String) hashMap.get("W0303");
					String startDateCol = (String) hashMap.get("W0309");
					if(StringUtils.isBlank(meetingNameCol) || StringUtils.isBlank(startDateCol)) {
						throw GeneralExceptionHandler.Handle(new Throwable(ResourceFactory.getProperty("zc_new.archivedata.error.msg")));
					}
					String now_year_whereStr = "where a0100='"+ rs.getString("w0505") +"' and ";
					now_year_whereStr+=meetingNameCol+"='"+rs.getString("W0303")+"' and ";
					now_year_whereStr+=Sql_switcher.diffYears(startDateCol, Sql_switcher.sqlNow())+"=0";
					// 子集中本年的最大编号
					int max_now_year_i9999 = DbNameBo.getPrimaryKey(tableName, "i9999", now_year_whereStr, this.conn);
					// 本年的最大编号是1，说明还没有，需要插入。
					if(max_now_year_i9999 == 1){
						String whereStr = "where a0100='"+ rs.getString("w0505") +"'";
						// 子集中最大编号
						i9999 = DbNameBo.getPrimaryKey(tableName, "i9999", whereStr, this.conn);
					} 
					// 本年的最大编号大于1,说存在记录。要更新
					else {
						insertOrUpdate = false;
						i9999 = max_now_year_i9999-1;
					}
					// end 如果是有本年的归档信息就更新，没有才插入  
					vo.setString("a0100", rs.getString("w0505"));
					vo.setInt("i9999", i9999);
					vo.setDate("createtime", new Date());
					vo.setString("createusername", userview.getUserName());
					// 循环目的指标  	0是fieldset 1开始是子集的指标项 
					LazyDynaBean bean = null;
					if("1".equals(reviewLinks) || "2".equals(reviewLinks)) {
						bean = isHaveSubComiteeData(tableName,rs.getString("w0505"),rs.getString("w0503"),hashMap);
					}
					String ments = this.getReviewMeetingMent();
					boolean updateFlag1 = false;
					boolean updateFlag2 = false;
					for(int j=0; j<list2.size() ;j++){
						String field = (String)hashMap.get(list2.get(j)).toString().toLowerCase();
						// 有可能不配置目的指标
						if(!StringUtils.isEmpty(field)){
							if("W0309".equalsIgnoreCase(list2.get(j))||"W0311".equalsIgnoreCase(list2.get(j)))
								vo.setDate(field, rs.getDate(list2.get(j)));
							else if("W0513".equalsIgnoreCase(list2.get(j)) || "W0515".equalsIgnoreCase(list2.get(j))) {
								String codeitemid = rs.getString(list2.get(j));
								if(!StringUtils.isEmpty(codeitemid))
									vo.setString(field, AdminCode.getCodeName("AJ", codeitemid));
							}else{
								String value = "";
								// attendance_1:参会人数（评委会）//只更新当前阶段的数据
								if("attendance_1".equalsIgnoreCase(list2.get(j))){
									if( !ments.contains(",1,"))
										continue;
									int w0549 = rs.getInt("w0549");
									int w0551 = rs.getInt("w0551");
									int w0553 = rs.getInt("w0553");
									value = String.valueOf(w0549+w0551+w0553);
									//判断
									
									if(!updateFlag1 && bean!=null) {
										String evaluationType4 = (String) bean.get("evaluationType4");
										if("1".equals(evaluationType4)) {
											String col = hashMap.get("attendance_4")==null?null:(String)hashMap.get("attendance_4");
											updateFlag1 = true;
											if(StringUtils.isNotBlank(col)) {
												int count = (Integer) bean.get("count");
												vo.setInt(col.toLowerCase(), count);
											}
										}
									}
								} 
								//评委会
								else if("w0549".equalsIgnoreCase(list2.get(j))
											||"w0551".equalsIgnoreCase(list2.get(j))
											||"w0553".equalsIgnoreCase(list2.get(j))){
									if( !ments.contains(",1,"))
										continue;
									value = rs.getString(list2.get(j));
									//判断
									if(!updateFlag2 && bean!=null) {
										String evaluationType4 = (String) bean.get("evaluationType4");
										if("1".equals(evaluationType4)) {
											updateFlag2 = true;
											String col1 = hashMap.get("W0563")==null?null:(String)hashMap.get("W0563");
											String col2 = hashMap.get("W0565")==null?null:(String)hashMap.get("W0565");
											String col3 = hashMap.get("W0567")==null?null:(String)hashMap.get("W0567");
											String col4 = hashMap.get("W0569")==null?null:(String)hashMap.get("W0569");
											String col5 = hashMap.get("W0571")==null?null:(String)hashMap.get("W0571");
											if(StringUtils.isNotBlank(col1)) {
												vo.setInt(col1.toLowerCase(), (Integer)bean.get("w0563"));
											}
											if(StringUtils.isNotBlank(col2)) {
												vo.setInt(col2.toLowerCase(), (Integer)bean.get("w0565"));
											}
											if(StringUtils.isNotBlank(col3)) {
												vo.setInt(col3.toLowerCase(),(Integer)bean.get("w0567"));
											}
											if(StringUtils.isNotBlank(col4)) {
												vo.setString(col4.toLowerCase(),(String)bean.get("w0569"));
											}
											if(StringUtils.isNotBlank(col5)) {
												vo.setInt(col5.toLowerCase(),(Integer)bean.get("w0571"));
											}
										}
											
									}
								}
								// attendance_2：:参会人数（学科组）//只更新当前阶段的数据
								else if("attendance_2".equalsIgnoreCase(list2.get(j))) {
									if( !ments.contains(",2,"))
										continue;
									int w0543 = rs.getInt("w0543");
									int w0545 = rs.getInt("w0545");
									int w0547 = rs.getInt("w0547");
									value = String.valueOf(w0543+w0545+w0547);
									
									//判断
									
									if(!updateFlag1 && bean!=null) {
										String evaluationType4 = (String) bean.get("evaluationType4");
										if("1".equals(evaluationType4)) {
											updateFlag1 = true;
											String col = hashMap.get("attendance_4")==null?null:(String)hashMap.get("attendance_4");
											if(StringUtils.isNotBlank(col)) {
												int count = (Integer)bean.get("count");
												vo.setInt(col.toLowerCase(), count);
											}
										}
									}
								}
								//学科组
								else if("w0543".equalsIgnoreCase(list2.get(j))
										||"w0545".equalsIgnoreCase(list2.get(j))
										||"w0547".equalsIgnoreCase(list2.get(j))) {
									if( !ments.contains(",2,"))
										continue;
									value = rs.getString(list2.get(j));
									//判断
									if(!updateFlag2 && bean!=null) {
										String evaluationType4 = (String) bean.get("evaluationType4");
										if("1".equals(evaluationType4)) {
											updateFlag2 = true;
											String col1 = hashMap.get("W0563")==null?null:(String)hashMap.get("W0563");
											String col2 = hashMap.get("W0565")==null?null:(String)hashMap.get("W0565");
											String col3 = hashMap.get("W0567")==null?null:(String)hashMap.get("W0567");
											String col4 = hashMap.get("W0569")==null?null:(String)hashMap.get("W0569");
											String col5 = hashMap.get("W0571")==null?null:(String)hashMap.get("W0571");
											if(StringUtils.isNotBlank(col1)) {
												vo.setInt(col1.toLowerCase(), (Integer)bean.get("w0563"));
											}
											if(StringUtils.isNotBlank(col2)) {
												vo.setInt(col2.toLowerCase(), (Integer)bean.get("w0565"));
											}
											if(StringUtils.isNotBlank(col3)) {
												vo.setInt(col3.toLowerCase(),(Integer)bean.get("w0567"));
											}
											if(StringUtils.isNotBlank(col4)) {
												vo.setString(col4.toLowerCase(),(String)bean.get("w0569"));
											}
											if(StringUtils.isNotBlank(col5)) {
												vo.setInt(col5.toLowerCase(),(Integer)bean.get("w0571"));
											}
										}
											
									}
									
								}
								// attendance_3：:参会人数（同行专家）//只更新当前阶段的数据
								else if("attendance_3".equalsIgnoreCase(list2.get(j))) {
									if( !ments.contains(",3,"))
										continue;
									int w0527 = rs.getInt("w0527");
									int w0529 = rs.getInt("w0529");
									int w0531 = rs.getInt("w0531");
									value = String.valueOf(w0527+w0529+w0531);
								}//同行专家
								else if("w0527".equalsIgnoreCase(list2.get(j))
										||"w0529".equalsIgnoreCase(list2.get(j))
										||"w0531".equalsIgnoreCase(list2.get(j))) {
									if( !ments.contains(",3,"))
										continue;
									value = rs.getString(list2.get(j));
								}
								// attendance_4：:参会人数（二级单位）//只更新当前阶段的数据
								else if("attendance_4".equalsIgnoreCase(list2.get(j))) {
									if( !ments.contains(",4,"))
										continue;
									int w0563 = rs.getInt("w0563");
									int w0565 = rs.getInt("w0565");
									int w0567 = rs.getInt("w0567");
									value = String.valueOf(w0563+w0565+w0567);
								}//二级单位
								else if("w0563".equalsIgnoreCase(list2.get(j))
											||"w0565".equalsIgnoreCase(list2.get(j))
											||"w0567".equalsIgnoreCase(list2.get(j))
											||"w0569".equalsIgnoreCase(list2.get(j))
											||"w0571".equalsIgnoreCase(list2.get(j))) {
									if( !ments.contains(",4,"))
										continue;
									value = rs.getString(list2.get(j));
								}else {
									if(list2.get(j).startsWith("C_") || list2.get(j).toLowerCase().endsWith("_SEQ")) {
										if(bean!=null) {
											String evaluationType4 = (String) bean.get("evaluationType4");
											Object obj = bean.get(list2.get(j));
											if("2".equals(evaluationType4) && obj!=null) {
												if(list2.get(j).toLowerCase().endsWith("_SEQ"))
													vo.setInt(field,(Integer)obj);
												else
													vo.setDouble(field,(Double)obj);
											}
										}
										if(list2.get(j).toLowerCase().endsWith("_SEQ")) {
											vo.setInt(field, rs.getInt(list2.get(j)));
										}else {
											vo.setDouble(field, rs.getDouble(list2.get(j)));
										}
										continue;	
									}
									value = rs.getString(list2.get(j));
								}
								
								vo.setString(field, value);
							}
						}
					}
					if(insertOrUpdate)
						dao.addValueObject(vo);
					 else 
						dao.updateValueObject(vo);
				}
			}
			
		} catch (Exception e) {
			msg = "归档失败！";
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
		
		return msg;
	}
	
	/**
	 * 归档之前校验 是否存在未评审的数据
	 * @param evaluationType	=1 票数统计 ；	=2 分数统计
	 * @param type				=1未全部完成评审，直接归档 ；=0||空需要判断
	 * @return
	 */
	public String checkIfArchiving(String evaluationType, String type, ContentDAO dao) throws GeneralException {
		String msg = "";
		
		RowSet rs = null;
		try {
			StringBuffer sql = new StringBuffer("");
			// 非1的时候需要判断
			if(!"1".equals(type)){
				// =1 票数统计 归档
				List<String> values = new ArrayList<String>();
				if("1".equals(evaluationType)) {
					sql.append("select W0511,W0303 from W05 left join W03 on w05.W0301 = w03.W0301");
					sql.append(" inner join zc_categories_relations zcr on zcr.w0501=W05.w0501 inner join zc_personnel_categories zpc on zcr.categories_id=zpc.categories_id");
					sql.append(" where W03.W0301=?");
					sql.append(" and W05.W0555=?");
					sql.append(" and zpc.approval_state<>'2'");//结束状态的分组不提示
					sql.append(" and zpc.review_links=? and ");
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
				}
				// =2 分数统计归档
				else if("2".equals(evaluationType)) {
					sql.append("select * from (");
					sql.append("select w05.w0511 ,w03.w0303,w05.w0501 from ");
					sql.append("kh_object ko,w03 left join w05 on w03.w0301=w05.w0301");
					sql.append(" where id in ");
					sql.append("(select distinct(kh_object_id) from kh_mainbody where Mainbody_id in ");
					//usetype=3是指打分，2投票，1审核
					sql.append("(select username from zc_expert_user where w0301 =? and usetype = 3 group by username)");
					sql.append(" and Relation_id=? and Status<>2) ");
					sql.append(" and w03.w0301=?");
					sql.append(" AND OBJECT_ID=w0505");
					
					sql.append(" UNION");
					
					sql.append(" select w0511,w0303,w05.w0501 from w05,w03 where w05.W0555=? and w05.w0301=?");
					sql.append(" and w05.w0301=w03.w0301 and w05.W0505 NOT IN(select object_id from kh_object where Relation_id=?)) T");
					sql.append(" inner join zc_categories_relations zcr on zcr.w0501=T.w0501 inner join zc_personnel_categories zpc on zcr.categories_id=zpc.categories_id");
					sql.append(" where zpc.approval_state<>'2' and zpc.review_links=?");//结束状态的分组不提示
					values.add(w0301);
					values.add("1_"+w0301+"_"+reviewLinks);
					values.add(w0301);
					values.add(reviewLinks);
					values.add(w0301);
					values.add("1_"+w0301+"_"+reviewLinks);
					values.add(reviewLinks);
				}
				rs = dao.search(sql.toString(),values);
				if(rs.next()){
					int i=1;
					msg=rs.getString("W0303") +"："+rs.getString("W0511");
					while(rs.next()){
						i++;
						if(i<=5)
							msg +="、"+ rs.getString("W0511");
					}
					msg+=(i>5?"等"+i+"人":"")+"的职称申报未完成评审！是否继续归档？";
					return msg;
				}
			}
			// 37700 由于前台复写的alert方法在IE浏览器下显示不全，故这里如果有提示信息在外层套一个div用于显示
			if(StringUtils.isNotEmpty(msg))
				msg = "<div>" + msg + "</div>";
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
		return msg;
	}
	
	/**
	 * 票数统计
	 */
	
	/**
	 * 同步各个阶段票数
	 * @param type:1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段
	 */
	public void asyncPersonNum(String w0301,int type){
		
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			/** 同步总人数 */ 
			String sql = "";
			StringBuffer querySql = new StringBuffer("");
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
			
			querySql.append("select z.group_id,a.*,z.expertnum from(");
			querySql.append(" select w05.w0501,w05.w0539,w05.w0541,w05.w0525,w0315,w0323 ");
			querySql.append(" from w05,w03 ");
			querySql.append(" where w03.w0301=w05.w0301 and w03.W0321 in ('05') ");
			querySql.append(" and w05.w0301=? and w05.w0555=? ");
			querySql.append(" ) a,zc_personnel_categories z ");
			querySql.append(" where z.categories_id=(select categories_id from zc_categories_relations c where  c.w0501=a.w0501 and z.categories_id = c.categories_id) and z.review_links = ?");
			
			ArrayList valuelist = new ArrayList();
			valuelist.add(w0301);
			valuelist.add(type);
			valuelist.add(type);
			rs = dao.search(querySql.toString(), valuelist);
			List<String> sqls = new ArrayList<String>();
			while(rs.next()){
				
				String w0501 = rs.getString("w0501");
				int expertnum = rs.getInt("expertnum");
				StringBuffer upSql = new StringBuffer();
				upSql.append("update W05 set ");
				upSql.append(totalItem+"="+expertnum+",");
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
	
	/***
	 * 
	 * @Title: asyncstatus   
	 * @Description: 计算是否通过   01 通过 02 未通过 
	 * @param  
	 * @return void    
	 * @throws
	 */
	public void asyncStatus(String w0301){
		
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet row=null;
		RowSet row_w0552=null;
		HashMap map_w0552 = new HashMap();
		/*外部鉴定专家      学科组     评委会     学院聘任组*/
		String sql="select * from w05,w03 where w05.w0301 = ? "
		  			+ "and w03.w0301=w05.w0301"
		  			+" and w03.W0321 in ('05')";//只同步进行中的会议
		try {
			//w0552，各阶段状态是否修改过
			//W0533同行评议//W0557专业组//w0569二级单位//W0559评委会
			//0000:0未修改，1修改 
			DbWizard dbw = new DbWizard(this.conn);
			if(!dbw.isExistField("w05","w0552", false)) {
				Table table=new Table("w05");
				Field field=new Field("w0552","状态修改标识");
				field.setDatatype(DataType.STRING);
				field.setLength(10);
				table.addField(field);	
				dbw.addColumns(table);
			}
			row_w0552 = dao.search("select w0501,w0552 from w05 where w0301=?", Arrays.asList(new String[] {w0301}));
			while(row_w0552.next()) {
				String w0552 = row_w0552.getString("w0552");
				map_w0552.put(row_w0552.getString("w0501"), StringUtils.isNotBlank(w0552)?w0552:"0000");
			}
			
			row=dao.search(sql, Arrays.asList(new String[] {w0301}));
			ArrayList<Map<String,String>> list=new ArrayList<Map<String,String>>();
			while (row.next()) {
				String W0533=row.getString("W0533");//外部鉴定专家状态
				String W0557=row.getString("W0557");//学科组状态
				String W0559=row.getString("W0559");//评委会状态
				String W0569=row.getString("W0569");//学院任聘组状态
				Map<String,String> map=new HashMap<String,String>();
				int W0517=row.getInt("W0517"); //评委人数 （总）
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
				String w0552 = (String) map_w0552.get(W0501);
				String W0533_edit = "";
				String W0557_edit = "";
				String W0569_edit = "";
				String W0559_edit = "";
				//W0533同行评议//W0557专业组//w0569二级单位//W0559评委会
				if(StringUtils.isNotBlank(w0552)) {
					for(int i=0;i<w0552.length();i++){
						if(i == 0) {
							W0533_edit = String.valueOf(w0552.charAt(i));
						}else if(i == 1) {
							W0557_edit = String.valueOf(w0552.charAt(i));
						}else if(i == 2) {
							W0569_edit = String.valueOf(w0552.charAt(i));
						}else if(i == 3) {
							W0559_edit = String.valueOf(w0552.charAt(i));
						}
					}
				}
				//已评人数不为0时计算是否通过 为0时直接置为"" 
				//已评人数的2/3+1 < 赞成人数 通过 否则 不通过
				if(W0523!=0 && (expertsNum!=0 || expertsFlg) && "0".equalsIgnoreCase(W0533_edit)){//外部专家
					if(W0531 >= Math.ceil((double)((double)(expertsNum*2)/3)))
						W0533="01";
					else
						W0533="02";
				}
				
				if(W0521!=0 && (subjectsNum!=0 || subjectsFlg) && "0".equalsIgnoreCase(W0557_edit)){//学科组
					if(W0547 >= Math.ceil((double)((double)(subjectsNum*2)/3)))
						W0557="01";
					else
						W0557="02";
				}
				
				if(W0517!=0 && (committeeNum!=0 || committeeFlg) && "0".equalsIgnoreCase(W0559_edit)){//评委会
					if(W0553 >= Math.ceil((double)((double)(committeeNum*2)/3)))
						W0559="01";
					else
						W0559="02";
				}
				
				if(W0571!=0 && (collegeNum!=0 || collegeFlg) && "0".equalsIgnoreCase(W0569_edit)){//学院任聘组(二级单位)
					if(W0567 >= Math.ceil((double)((double)(collegeNum*2)/3)))
						W0569="01";
					else
						W0569="02";
				}
				map.put("W0501", W0501);
				map.put("W0533", W0533);
				map.put("W0557", W0557);
				map.put("W0559", W0559);
				map.put("W0569", W0569);
				list.add(map);
			}
		  
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
			PubFunc.closeDbObj(row_w0552);
		}
	}
	/**
	 * 更新阶段下的分组状态
	 * @throws GeneralException 
	 */
	private void updateCategorieStatus() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			List values = new ArrayList();
			String sql = "update zc_personnel_categories set approval_state = '4' where W0301=? and Review_links=? and "+Sql_switcher.isnull("approval_state", "''")+"<>''";
			values.add(this.w0301);
			values.add(Integer.parseInt(this.reviewLinks));
			dao.update(sql,values);
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {

		}
	}
	/**
	 * 查询当前年度下指定申报人有没有二级单位的数据，有的话则更新
	 * @return
	 * @throws GeneralException 
	 */
	private LazyDynaBean isHaveSubComiteeData(String fieldset,String a0100,String nbase,HashMap hashMap) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		LazyDynaBean bean = null;
		try {
			String sql = "select fd.*,w05.w0301 from "+fieldset+" fd,W05,W03 where fd.a0100=? and W03.W0321='06' and w05.w0505=fd.a0100 and W0503=? and w03.w0301=w05.w0301 and w0555=4 and "+Sql_switcher.diffYears("W0309", Sql_switcher.sqlNow())+"=0 order by createtime desc";
			ReviewMeetingPortalBo bo = new ReviewMeetingPortalBo(userview, conn);
			List values = new ArrayList();
			values.add(a0100);
			values.add(nbase);
			rs = dao.search(sql, values);
			while(rs.next()) {
				LazyDynaBean meetingData = bo.getMeetingData(rs.getString("w0301"));
				String evaluationType4 = (String) meetingData.get("evaluationType_4");
				if("1".equals(evaluationType4)) {//投票
					String W0563Col = (String) hashMap.get("W0563");
					String W0565Col = (String) hashMap.get("W0565");
					String W0567Col = (String) hashMap.get("W0567");
					String W0569Col = (String) hashMap.get("W0569");
					String W0571Col = (String) hashMap.get("W0571");
					int w0563 = 0;
					int w0565 = 0;
					int w0567 = 0;
					String w0569 = null;//评审状态
					int w0571 = 0;//专家人数
					if(StringUtils.isNotBlank(W0563Col)) {
						w0563 = rs.getInt(W0563Col);
					}
					if(StringUtils.isNotBlank(W0565Col)) {
						w0565 = rs.getInt(W0565Col);
					}
					if(StringUtils.isNotBlank(W0567Col)) {
						w0567 = rs.getInt(W0567Col);
					}
					if(StringUtils.isNotBlank(W0571Col)) {
						w0571 = rs.getInt(W0571Col);
					}
					if(StringUtils.isNotBlank(W0569Col)) {
						w0569 = rs.getString(W0569Col);
					}
					int count = w0563+w0565+w0567;//参会人数
					if(count==0) {
						continue;
					}else {
						bean = new LazyDynaBean();
						bean.set("evaluationType4", "1");
						bean.set("w0563",w0563);
						bean.set("w0565",w0565);
						bean.set("w0567",w0567);
						bean.set("w0571",w0571);
						bean.set("w0569", StringUtils.isEmpty(w0569)?"":w0569);
						bean.set("count",count);
						return bean;
					}
				}else {//打分
					String w0301_temp = this.w0301;
					this.w0301 = rs.getString("w0301");
					String[] perTables = this.getReviewMeetingTemplateids("4").split(",");
					this.w0301 = w0301_temp;
					boolean isHaveData = false;
					bean = new LazyDynaBean();
					bean.set("evaluationType4", "2");
					for(int i=0;i<perTables.length;i++) {
						String table = perTables[i];
						String tableCol = (String) hashMap.get("C_"+table);
						String tableSeqCol = (String) hashMap.get("C_"+table+"_SEQ");
						
						if(StringUtils.isNotBlank(tableCol)) {
							Double score = rs.getDouble(tableCol);
							if(score!=null && score>0) {
								isHaveData = true;
								bean.set("C_"+table, score);
								if(StringUtils.isNotBlank(tableSeqCol)) {
									Integer seq = rs.getInt(tableSeqCol);
									if(seq!=null && seq>0) {
										bean.set("C_"+table+"_SEQ", seq);
									}
								}
							}
							
							
						}
					}
					if(isHaveData) {
						return bean;
					}
				}
			}
			return null;
		}catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
	}
	
}
