package com.hjsj.hrms.module.talentmarkets.competition.businessobject.impl;

import com.hjsj.hrms.module.talentmarkets.competition.businessobject.CompetitionApplyService;
import com.hjsj.hrms.module.talentmarkets.competition.businessobject.CompetitionJobsService;
import com.hjsj.hrms.module.talentmarkets.utils.TalentMarketsUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class CompetitionApplyServiceImpl implements CompetitionApplyService{

	private static Category log = Category.getInstance(CompetitionApplyServiceImpl.class.getName());
	
	private UserView userView;
	private Connection conn;
	private String subModuleId = "competitionApplysTable";
	
	public CompetitionApplyServiceImpl(UserView userView , Connection conn){
		this.userView = userView;
		this.conn = conn;
	}
	
	@Override
	public String getTableConfig(String state,String orgId) throws GeneralException {
		//获取列头
        ArrayList<ColumnsInfo> columns = getColumnList(state);
        TableConfigBuilder builder = new TableConfigBuilder(subModuleId, (ArrayList) columns, subModuleId, this.userView, this.conn);
        builder.setDataList(this.listTableData(state, this.getTableSql(state,orgId)));
       // builder.setSelectable(false);
        //builder.setLockable(true);
        builder.setTitle(ResourceFactory.getProperty("talentmarkets.competitionJobs.competitiveApply"));
        builder.setColumnFilter(true);
    //    builder.setOrderBy(" order by Z81.create_time desc");
        String gridConfig = builder.createExtTableConfig();
        return gridConfig;
	}
	
	@Override
	public int getCompetitionApplyPostNum(String state, String orgId) throws GeneralException {
		int count = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("select "+ Sql_switcher.isnull("count(1)", "0") +" count ");
		sql.append(this.getCompetitonApplyWhereSql(state, orgId));
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search(sql.toString());
			if(rs.next()){
				count = rs.getInt("count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("getCompetitionPostError");
			//获取竞聘岗位数出错
			throw new GeneralException("getCompetitionPostError");
		} finally{
			PubFunc.closeDbObj(rs);
		}
		return count;
	}
	
	@Override
	public Map getCompetitionApplyOtherParam() throws GeneralException {
		Map dataHM = new HashMap();
		CompetitionJobsService competitionJobsService = new CompetitionJobsServiceImpl(this.conn, this.userView);
		boolean isHaveThePosTab = competitionJobsService.isHaveThePosTab();
		String postDetailRanemId = "";
		if(isHaveThePosTab){
			postDetailRanemId = TalentMarketsUtils.getCompetitionPostDetailRname();
		}
		dataHM.put("postDetailRnameId", postDetailRanemId);
		int maxJobsNum = TalentMarketsUtils.getMaxCompetitionPost();
		int applyPostNum = this.getCompetitionApplyPostNum("executing", "");
		int laveJobNum = maxJobsNum - applyPostNum;
		dataHM.put("laveJobNum", laveJobNum);
		
		Map recordHM = new HashMap();
		recordHM.put("a0100", this.userView.getA0100());
		recordHM.put("guidkey", this.userView.getGuidkey());
		recordHM.put("nbase", this.userView.getDbname());
		List recordList = new ArrayList();
		recordList.add(recordHM);
		dataHM.put("records", recordList);
		dataHM.put("templateType", "applyTemplate");
		return dataHM;
	}
	
	/**
	 * 获取表格组件所需的数据 
	 * @param state 类型
	 * @param sql  查询数据sql语句
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList listTableData(String state,String sql) throws GeneralException{
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet  rs = null;
		try {
			rs = dao.search(sql);
			while(rs.next()){
				LazyDynaBean bean = new  LazyDynaBean(); 
				if(!"competitive_jobs".equalsIgnoreCase(state)){
					bean.set("z8303", rs.getString("Z8303"));
				}
				bean.set("create_time", rs.getDate("create_time"));
				bean.set("z8101", rs.getString("Z8101"));
				bean.set("e01a1", rs.getString("E01A1")==null? "":rs.getString("E01A1"));
				bean.set("b0110", rs.getString("B0110")==null? "":rs.getString("B0110"));
				bean.set("e0122", rs.getString("E0122")==null? "":rs.getString("E0122"));
				bean.set("z8113", rs.getString("Z8113")==null? "":rs.getString("Z8113"));
				List<String> postFieldsList = TalentMarketsUtils.listPostFields();
				for (int i = 0; i < postFieldsList.size(); i++) {
					String field = postFieldsList.get(i);
					if(",Z8101,E01A1,Z8105,B0110,E0122,Z8113,".indexOf(","+field.toUpperCase()+",") !=-1) {
						continue;
					}
					FieldItem fieldItem = DataDictionary.getFieldItem(field,"z81");
					if("A".equalsIgnoreCase(fieldItem.getItemtype())){
						bean.set(field.toLowerCase(), rs.getString(field)==null? "":rs.getString(field));
					}
					if("D".equalsIgnoreCase(fieldItem.getItemtype())){
						bean.set(field.toLowerCase(), rs.getString(field)==null? "":rs.getString(field));
					}
					if("N".equalsIgnoreCase(fieldItem.getItemtype())){
						bean.set(field.toLowerCase(), rs.getInt(field));
					}
				}
				list.add(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//获取竞聘报名数据出错
			log.error("getCompetitionApplyDataError");
			throw new GeneralException("getCompetitionApplyDataError");
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return list;
	}
	
	/**
	 * 获取竞聘报名sql语句
	 * @param state
	 * @return
	 * @throws GeneralException 
	 */
	private String getTableSql(String state, String orgId) throws GeneralException{
		StringBuffer sql = new StringBuffer();
		StringBuffer fieldStr = new StringBuffer();
		String format_str="yyyy-MM-dd HH:mm:ss";
		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			format_str="yyyy-MM-dd hh24:mi:ss"; 
		List<String> postFieldsList = TalentMarketsUtils.listPostFields();
		for (int i = 0; i < postFieldsList.size(); i++) {
			String field = postFieldsList.get(i);
			if(",Z8101,E01A1,Z8105,B0110,E0122,Z8113,".indexOf(","+field.toUpperCase()+",") !=-1) {
				continue;
			}
			FieldItem fieldItem = DataDictionary.getFieldItem(field, "z81");
			if("D".equalsIgnoreCase(fieldItem.getItemtype())){
				fieldStr.append(","+Sql_switcher.dateToChar("Z81."+field, (format_str))+" " + field);
			}else{
				fieldStr.append(",Z81." + field);
			}
		}
		if("competitive_jobs".equalsIgnoreCase(state)){//竞聘岗位
			sql.append("select Z81.create_time,Z81.Z8101,Z81.E01A1,Z81.B0110,Z81.E0122,"+Sql_switcher.dateToChar("Z81.Z8113", format_str)+" Z8113"+fieldStr);
		}else if("executing".equalsIgnoreCase(state)){//我的竞聘
			sql.append("select Z83.create_time,Z83.Z8303,Z81.Z8101,Z81.E01A1,Z81.B0110,Z81.E0122,"+Sql_switcher.dateToChar("Z81.Z8113", format_str)+" Z8113"+fieldStr);
		}else if("end".equalsIgnoreCase(state)){//历史竞聘
			sql.append("select Z83.create_time,Z83.Z8303,Z81.Z8101,Z81.E01A1,Z81.B0110,Z81.E0122,"+Sql_switcher.dateToChar("Z81.Z8113", format_str)+" Z8113"+fieldStr);
		}
		sql.append(this.getCompetitonApplyWhereSql(state, orgId));
		if("competitive_jobs".equalsIgnoreCase(state)){//竞聘岗位
			sql.append(" order by Z81.create_time desc ");
		}else{
			sql.append(" order by Z83.create_time desc ");
		}
		return sql.toString();
	}
	
	/**
	 * 获取岗位报名sql查询条件
	 * @param state 竞聘类型
	 * @param orgId 机构id号
	 * @return
	 */
	private String getCompetitonApplyWhereSql(String state, String orgId){
		StringBuffer sql = new StringBuffer();
		if("competitive_jobs".equalsIgnoreCase(state)){//竞聘岗位
			sql.append(" from Z81 ");
			sql.append(" where z8103 = '04' and not EXISTS(select Z8101 from Z83 where Z83.Z8101=Z81.Z8101 and Z8301='"+this.userView.getGuidkey()+"')");
			if (Sql_switcher.searchDbServer() == 1) { //发布时间未过的岗位
	            sql.append(" and Z8113 > GETDATE() and Z8111 <= GETDATE() ");
	        } else if (Sql_switcher.searchDbServer() == 2) {
	            sql.append(" and Z8113 > sysdate and Z8111 <= sysdate ");
	        }
			String b0110 = this.userView.getUserOrgId();//单位
	        String e0122 = this.userView.getUserDeptId();//部门
	        List orgList = new ArrayList();
	        if (e0122 != null && e0122.trim().length() > 0) {
	        	orgList.add("%," + e0122 + ",%");
	        	orgList = this.getOrgIds("UM", e0122, orgList);
	        } else {
	            if (b0110 != null && b0110.trim().length() > 0) {
	            	orgList.add("%," + b0110 + ",%");
	            	orgList = this.getOrgIds("UN", b0110, orgList);
	            }
	        }
			/**过滤竞聘范围*/
	        sql.append(" and (Z8115 is null or Z8115 = '' or Z8115 = 'UN`' or ");
	        for (int i = 0; i < orgList.size(); i++) {
	            sql.append(" Z8115 like '"+ orgList.get(i) +"' or ");
	        }
	        sql.setLength(sql.length() - 3);
	        sql.append(") ");
	        if (orgId != null && orgId.trim().length() > 0 && AdminCode.getCode("UN", orgId) != null) {
	            sql.append(" and B0110 like '"+ orgId +"%'");
	        }
	        if (orgId != null && orgId.trim().length() > 0 && AdminCode.getCode("UM", orgId) != null) {
	            sql.append(" and E0122 like '"+ orgId +"%'");
	        }
	        if (orgId != null && orgId.trim().length() > 0 && AdminCode.getCode("@K", orgId) != null) {
	            sql.append(" and E01A1 like '"+ orgId +"%'");
	        }
		}else if("executing".equalsIgnoreCase(state)){//我的竞聘
			sql.append(" from Z81,Z83 ");
			sql.append(" where Z81.Z8101=Z83.Z8101 and Z83.Z8301='"+this.userView.getGuidkey() +"' ");
			sql.append("  and Z83.Z8303 not in ('03','06','09','12','13') ");
		}else if("end".equalsIgnoreCase(state)){//历史竞聘
			sql.append(" from Z81,Z83 ");
			sql.append(" where Z81.Z8101=Z83.Z8101 and Z83.Z8301='"+this.userView.getGuidkey() +"' ");
			sql.append("  and Z83.Z8303 in ('03','06','09','12','13') ");
		}
		return sql.toString();
	}
	
	
	/**
     * 获取当前用户的机构和上级机构
     *
     * @param codeitemid
     * @return
     */
    private List getOrgIds(String codesetid, String codeitemid, List list) {
        CodeItem codeitem = AdminCode.getCode(codesetid, codeitemid);
        if (codeitem == null) {
            codeitem = AdminCode.getCode("UN", codeitemid);
        }
        String parentId = codeitem.getPcodeitem();
        if (!codeitemid.equalsIgnoreCase(parentId)) {
            list.add("%," + parentId + ",%");
            return getOrgIds(codeitem.getCodeid(), parentId, list);
        }
        return list;
    }
	
	
	/**
	 * 获取竞聘报名列信息
	 * @param state
	 * @return
	 * @throws GeneralException 
	 */
	private ArrayList<ColumnsInfo> getColumnList(String state) throws GeneralException{
		ArrayList<ColumnsInfo> columnsInfoList= new ArrayList<ColumnsInfo>();
		Map<String, Object> exparam = new HashMap<String, Object>();
		 try {
			// 竞聘岗位编号
			ColumnsInfo columnInfo = TalentMarketsUtils.getColumnsInfo((FieldItem) DataDictionary.getFieldItem("z8101","z81").clone(), ColumnsInfo.LOADTYPE_ONLYLOAD, 0, null);
			columnsInfoList.add(columnInfo);
			if(!"competitive_jobs".equalsIgnoreCase(state)){
				// 状态
				exparam.put("editableValidFunc", "false");
	            exparam.put("fieldsetid", "z81");
	            columnInfo = TalentMarketsUtils.getColumnsInfo("A", "87", "Z8303", DataDictionary.getFieldItem("Z8303", "z83").getItemdesc(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 100, exparam);
				columnsInfoList.add(columnInfo);
				exparam.clear();
			}
			//竞聘岗位
			exparam.put("rendererFunc", "CompetitionApply.renderJobsColumnFunc");
			exparam.put("filterable", false);
            columnInfo = TalentMarketsUtils.getColumnsInfo("A", "@K", "E01A1", DataDictionary.getFieldItem("E01A1", "z81").getItemdesc(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 200, exparam);
			columnInfo.setColumnDesc(ResourceFactory.getProperty("talentmarkets.competitionJobs.competitivePosition"));
			columnsInfoList.add(columnInfo);
			exparam.clear();
			
			// 单位
			exparam.put("filterable", false);
            columnInfo = TalentMarketsUtils.getColumnsInfo((FieldItem) DataDictionary.getFieldItem("b0110").clone(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 200, exparam);
            columnsInfoList.add(columnInfo);
            exparam.clear();

            // 部门
            exparam.put("filterable", false);
            columnInfo = TalentMarketsUtils.getColumnsInfo((FieldItem) DataDictionary.getFieldItem("e0122").clone(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 200, exparam);
            columnsInfoList.add(columnInfo);
            exparam.clear();
            
            // 结束时间
            exparam.put("textAlign", "left");
            exparam.put("filterable", false);
            exparam.put("columnLength", 16);
            columnInfo = TalentMarketsUtils.getColumnsInfo((FieldItem) DataDictionary.getFieldItem("Z8113", "z81").clone(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 140, exparam);
            columnsInfoList.add(columnInfo);
            exparam.clear();
            
            List<String> postFieldsList = TalentMarketsUtils.listPostFields();
            for (int i = 0; i < postFieldsList.size(); i++) {
				String field = postFieldsList.get(i);
				if(",Z8101,E01A1,Z8105,B0110,E0122,Z8113,".indexOf(","+field.toUpperCase()+",") !=-1) {
					continue;
				}
				exparam.put("filterable", false);
				columnInfo = TalentMarketsUtils.getColumnsInfo((FieldItem) DataDictionary.getFieldItem(field,"z81").clone(), ColumnsInfo.LOADTYPE_ALWAYSLOAD, 80, exparam);
	            columnsInfoList.add(columnInfo);
	            exparam.clear();
			}
        	// 操作
            exparam.put("editableValidFunc", "false");
            exparam.put("textAlign", "left");
            exparam.put("rendererFunc", "CompetitionApply.operateRenderFunc");
            exparam.put("beExport", false);
            exparam.put("sortable", false);
            exparam.put("filterable", false);
            exparam.put("queryable", false);
            columnInfo = TalentMarketsUtils.getColumnsInfo("", "0", "interviewArrangement", ResourceFactory.getProperty("column.operation"), ColumnsInfo.LOADTYPE_BLOCK, 80, exparam);
            columnsInfoList.add(columnInfo);
		} catch (GeneralException e) {
			e.printStackTrace();
			//获取竞聘报名指标参数出错
			log.error("getCompetitionApplyColumnError");
			throw new GeneralException("getCompetitionApplyColumnError");
		}
		return columnsInfoList;
	}

	@Override
	public Map getCompetitionApplyTemplateData(String state,String z8101) throws GeneralException {
		Map dataHM = new HashMap();
		/**获取当前竞聘岗位对应流程中状态*/
		int ins_id = this.getCompetitionApplyTemplateIns_id(z8101);
		String sql = this.getApplyTemplateSql(state,ins_id);
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		String tabid = "";
		String task_id = "";
		String recallflag = "0";
		String actor_type = "";
		try {
			rs = dao.search(sql);
			while(rs.next()){
				tabid = rs.getString("tabid");
				task_id = rs.getString("task_id");
				actor_type = rs.getString("actor_type");
				recallflag = rs.getString("recallflag");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//获取报名单子信息出错
			log.error("getCompetitionApplyTemplateInfoError");
			throw new  GeneralException("getCompetitionApplyTemplateInfoError");
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		Map templateHM = new HashMap();
		templateHM.put("tabid", tabid);
		templateHM.put("ins_id", String.valueOf(ins_id));
		templateHM.put("task_id_e", PubFunc.encrypt(task_id));
		templateHM.put("task_id", task_id);
		templateHM.put("recallflag", recallflag);//撤回参数
		templateHM.put("cancelflag", this.getCompetitionApplyTemplateCancel(z8101));//撤销参数
		templateHM.put("actor_type", actor_type);
		
		dataHM.put("searchTemplate", templateHM);
		return dataHM;
	}
	/**
	 * 获取当前竞聘岗位单子是否可以撤销
	 * @param z8101 竞聘岗位编号
	 * @return
	 * @throws GeneralException 
	 */
	private String getCompetitionApplyTemplateCancel(String z8101) throws GeneralException{
		String cancel = "0";
		StringBuffer sql = new StringBuffer();
		Map map = TalentMarketsUtils.getApplyTemplateRelation();
		String tabid = TalentMarketsUtils.getApplyTemplate();
		sql.append("select tt.task_id,tt.state,tt.node_id,t.ins_id from templet_" + tabid + " t,t_wf_task tt where t.ins_id=tt.ins_id ");
        sql.append(" and t.a0100=? ");
        sql.append(" and t.basepre=? ");
        sql.append(" and t." + (String) map.get("z8101") + "=? ");
        sql.append(" order by tt.task_id desc");
        int taskid = 0;
        int ins_id = 0;
        String state = "";
        String node_id = "";
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            rs = dao.search(sql.toString(), Arrays.asList(this.userView.getA0100(), this.userView.getDbname(), z8101));
            if (rs.next()) {
                taskid = rs.getInt("task_id");
                ins_id = rs.getInt("ins_id");
                state = rs.getString("state");
                node_id = rs.getString("node_id");
            }
            if (StringUtils.equalsIgnoreCase("07", state)) {
        		rs = dao.search("select nodetype from t_wf_node where node_id = ? and tabid = ? ", Arrays.asList(node_id, tabid));
        		if (rs.next()) {
        			//驳回到发起人状态
        			if (StringUtils.equalsIgnoreCase("1", rs.getString("nodetype"))) {
        				cancel = "1";
        			}
        		}
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //获取报名中得竞聘岗位模板信息出错
            log.error("getApplyTemplateTaskidAndInsidError");
            throw new GeneralException("getApplyTemplateTaskidAndInsidError");
        } finally {
    		PubFunc.closeDbObj(rs);
    	}
        
		return cancel;
	}

	/**
	 * 获取我的申请表单sql语句
	 * @return
	 * @throws GeneralException 
	 */
	private String getApplyTemplateSql(String state,int ins_id) throws GeneralException{
		String _static="static";
		if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
			_static="static_o";
		}
		String module_id = "9";
		String tabid = TalentMarketsUtils.getApplyTemplate();
		String insWhereSql = "";
		if("executing".equalsIgnoreCase(state)){
			insWhereSql = "task_type='2' and finished='2' and ( task_state='3'  or task_state='6' )";
		}else{//历史竞聘查询 结束和终止的单子
			insWhereSql = "( T.task_type='9' and  T.task_state='5' or T.task_state='4' )";
		}
		StringBuffer sql = new StringBuffer();
		sql.append("select U.ins_id,U.tabid,T.actor_type,T.task_id,U.actor_type actortype,")
			.append("case when (select count(1) from t_wf_task t1  where  t1.task_type='2' and T1.ins_id=u.ins_id and t1.bread=1)>0 then 0  else 1 end  recallflag ")
			.append(" from ")
			.append("t_wf_task T  ,")
			.append("t_wf_instance U ,")
			.append("template_table tt ")
			.append(" where ")
			.append("T.ins_id=U.ins_id and U.tabid= "+tabid+" and  task_topic not like '%共0人%' and  task_topic not like '%共0条%'  and ")
			.append("U.tabid=tt.tabid and tt."+_static+"!=10 and tt."+_static+"!=11   and "+ insWhereSql +" and ");
		String dbpre=this.userView.getDbname(); //库前缀
		String userid=dbpre+this.userView.getA0100();//人员编号
		/**如果为空，设置一个不可能出现的用户，为了少写几行代码*/
		if(userid==null||userid.length()==0)
			userid="-1";
		 
		/**人员列表*/
		sql.append( " ( upper(U.actorid) in ('");
		sql.append(userid.toUpperCase());
		if(("9".equals(module_id)&&this.userView.getStatus()==4)||!"9".equals(module_id)) {
			sql.append("','");
			sql.append(this.userView.getUserName().toUpperCase());
		}
		sql.append("'))"); 
		sql.append(" and "+Sql_switcher.isnull("T.bs_flag","'1'")+"='1'  and U.ins_id="+ins_id);
		return sql.toString();
	}
	
	/**
	 * 获取当前竞聘岗位对应的ins_id 
	 * @param z8101 竞聘岗位编号
	 * @return
	 * @throws GeneralException
	 */
	private int getCompetitionApplyTemplateIns_id(String z8101) throws GeneralException{
		int ins_id = 0;
		RowSet rs = null;
		StringBuffer sql = new StringBuffer();
		JSONObject applyTemplateRelation = null;
		try {
			applyTemplateRelation = TalentMarketsUtils.getApplyTemplateRelation();
		} catch (Exception e1) {
			e1.printStackTrace();
			//获取报名模板指标对应关系出错
			log.error("notSetApplyPostTemplatePlan");
			throw new GeneralException("notSetApplyPostTemplatePlan");
		}
		if (applyTemplateRelation == null) {
			//获取报名模板指标对应关系出错
			log.error("notSetApplyPostTemplatePlan");
			throw new GeneralException("notSetApplyPostTemplatePlan");
		} else {
			if (applyTemplateRelation.isEmpty()) {
				//获取报名模板指标对应关系出错
				log.error("notSetApplyPostTemplatePlan");
				throw new GeneralException("notSetApplyPostTemplatePlan");
			}
		}
		try {
			String primaryKeyField = applyTemplateRelation.getString("z8101");
			String tabid = TalentMarketsUtils.getApplyTemplate();
			sql.append("select ins_id from templet_"+tabid+" ")
				.append("where ")
				.append(" a0100=? and ")
				.append(" basepre=? and ")
				.append(" "+ primaryKeyField +"=? ")
				.append("order by ins_id desc ");
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString(),Arrays.asList(this.userView.getA0100(),this.userView.getDbname(),z8101));
			if(rs.next()){
				ins_id = rs.getInt("ins_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//获取单子所在实例号出错
			log.error("getTemplateInsidError");
			throw new GeneralException("getTemplateInsidError");
		} catch (Exception e) {
			e.printStackTrace();
			//获取单子所在实例号出错
			log.error("getTemplateInsidError");
			throw new GeneralException("getTemplateInsidError");
		} finally{
			PubFunc.closeDbObj(rs);
		}
		return ins_id;
	}

}
