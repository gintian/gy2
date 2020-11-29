package com.hjsj.hrms.module.template.templatelist.transaction;

import com.hjsj.hrms.module.template.templatecard.businessobject.TempletChgLogBo;
import com.hjsj.hrms.module.template.templatelist.businessobject.TemplateListShowBo;
import com.hjsj.hrms.module.template.utils.TemplateDataBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:TemplateListTrans.java</p>
 * <p>Description>:人事异动列表 主界面初始化</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2016-5-10 下午03:09:59</p>
 * <p>@version: 7.x</p>
 */
public class TemplateListTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
            TemplateFrontProperty frontProperty = new TemplateFrontProperty(this.getFormHM());
            String sysType = frontProperty.getSysType();
            String moduleId = frontProperty.getModuleId();
            String approveFlag = frontProperty.getApproveFlag();
            String returnFlag = frontProperty.getReturnFlag();
            String tabId = frontProperty.getTabId();
            String taskId = frontProperty.getTaskId();
            String object_id = frontProperty.getOtherParam("object_id");
            String noShowPageNo = frontProperty.getOtherParam("noshow_pageno");//设定的不显示的页签
    //      boolean sp_batch = taskId.contains(",");	//20160905 dengcan 无用变量 
            String filterStr = frontProperty.getOtherParam("search_sql");
            String subModuleId = "templet_"+tabId;
            TemplateParam paramBo=new TemplateParam(this.getFrameconn(), this.userView,Integer.valueOf(tabId));
            paramBo.setReturnFlag(returnFlag);//liuyz 我的申请不需要去获取节点权限
            TemplateListShowBo listBo = new TemplateListShowBo(this.getFrameconn(),this.userView,Integer.valueOf(tabId),paramBo);
            listBo.setApproveFlag(frontProperty.getApproveFlag());
            listBo.setNoshow_pageno(noShowPageNo);
            TemplateDataBo dataBo= listBo.getDataBo();
            paramBo = listBo.getParamBo();
            TableDataConfigCache tableCacheList = (TableDataConfigCache) userView.getHm().get(subModuleId);
            String property = "";
            String direction = "";
            String filterStr_catch="";
			if(tableCacheList!=null)
			{
				HashMap customParamHM = tableCacheList.getCustomParamHM()==null?new HashMap():tableCacheList.getCustomParamHM();
				property = customParamHM.get("property")==null?"":(String)customParamHM.get("property");
				direction = customParamHM.get("direction")==null?"":(String)customParamHM.get("direction");
				filterStr_catch = tableCacheList.getFilterSql();//获取列表过滤条件的sql
            	/*if(StringUtils.isNotBlank(filterStr_catch)) //bug 35036 过滤条件sql放到了赋给filterStr会导致取消过滤后查询语句还带有条件。
            		filterStr+=filterStr_catch;*/
			}
			
            /** 获取列头 */
            ArrayList tableHeadSetList = listBo.getTableHeadSetList(taskId); 
            ArrayList pageList=new ArrayList();
            ArrayList pagePrilist = dataBo.getParamBo().getOutPriPageList();
            for(int i=0;i<pagePrilist.size();i++){
            	int pageid = (Integer)pagePrilist.get(i);
            	if(!dataBo.isHaveReadFieldPriv(pageid+"", taskId))
            		continue;
            	pageList.add(pageid);
            }
            this.getFormHM().put("pageList", pageList);
            HashMap nodePrivMap = new HashMap(); 
            if (paramBo.getSp_mode()==0&&!"3".equals(returnFlag)) {//liuyz 我的申请不需要去获取节点权限
                nodePrivMap = dataBo.getUtilBo().getFieldPrivByNode(taskId,Integer.valueOf(tabId));
            }
            
            ArrayList<ColumnsInfo> columnsInfo = listBo.getColumnList(tableHeadSetList);

            /** 获取数据sql */
            String dataTabName = dataBo.getUtilBo().getTableName(moduleId, Integer.valueOf(tabId), taskId);
            String sql = dataBo.getTemplateListSql(moduleId, returnFlag, approveFlag, dataTabName, taskId, object_id, filterStr,tableHeadSetList);
            this.getFormHM().put("tablename", dataTabName);
            //String opinion_field = paramBo.getOpinion_field();//liuyz bug31563
            //this.getFormHM().put("opinion_field", opinion_field==null||opinion_field.trim().length()==0?"":opinion_field+"_2");
            StringBuffer orderBy = new StringBuffer();
            if ((paramBo.getInfor_type() == 2 ||paramBo.getInfor_type() == 3) 
                    && (paramBo.getOperationType() == 8 || paramBo.getOperationType() == 9)) {
                sql = this.editSql(sql, columnsInfo);
                String key = "b0110";
                if (paramBo.getInfor_type() == 3)
                    key = "e01a1";
                orderBy.append("  order by " + Sql_switcher.isnull("to_id", "100000000") + ",case when " + key + "=to_id then 100000000 else a0000 end asc ");
            } else
                orderBy.append(" order by a0000");

            //29235 linbz 增加选人控件不显示的人员参数
            if(dataBo.getParamBo().getInfor_type() == 1){
	            ArrayList objectslist = dataBo.getNbaseA0100List(sql);
	            this.getFormHM().put("deprecate", objectslist);
	            String deprecateFlag = frontProperty.getOtherParam("deprecate_flag");
	            if("1".equals(deprecateFlag)){
	            	return;
	            }
            }
            
            /** 加载表格 */
            TableConfigBuilder builder = new TableConfigBuilder(subModuleId, columnsInfo, "templatelist", userView, this.getFrameconn());
            // builder.setDataList(dataList);
            builder.setDataSql(sql);
            builder.setOrderBy(orderBy.toString());
            builder.setAutoRender(false);
            builder.setFieldAnalyse(true);//启用列的统计 lis 20160822
            // builder.setTitle(title);
            builder.setLockable(true);
            builder.setEditable(true);
            builder.setScheme(true);
            builder.setSetScheme(true);
            builder.setSchemePosition(TableConfigBuilder.SCHEME_POSITION_TITLE);
            builder.setSchemeSaveCallback("templateList_me.schemeSave");
            builder.setSelectable(true);
            // builder.setSearchConfig("","按名称、单位、部门查询...");
            builder.setPageSize(20);
            builder.setColumnFilter(true);// lis 20160426
          //栏目设置权限
			boolean bComSet=false;
			if (this.userView.hasTheFunction("010735")&&"9".equals(moduleId)||userView.hasTheFunction("32034")&&!"9".equals(moduleId)){
				bComSet=true;
			}
            builder.setShowPublicPlan(bComSet);
            //linbz 优化过滤条件后默认设置选中
            builder.setBeforeLoadFunctionId("MB00003009");
            HashMap map = new HashMap();
            map.put("taskId", taskId);
            map.put("tableName", dataTabName);
            map.put("search_sql", filterStr);
            builder.setCustomParamHM(map);
            String config = builder.createExtTableConfig();
            this.getFormHM().put("tableConfig", config.toString());

            /** 取得复杂查询下拉中的字段* */
            // HashMap fieldsMap=new HashMap();
            ArrayList fieldsMap = new ArrayList();
            JSONObject tableConfig =  JSONObject.fromObject(config); 
            JSONArray tablecolumnsList = (JSONArray)(JSONObject.fromObject(config).get("tablecolumns"));
            ArrayList fieldsArray =listBo.getFieldsArray(tablecolumnsList, fieldsMap);
            this.getFormHM().put("fieldsArray", fieldsArray);
            this.getFormHM().put("fieldsMap", fieldsMap);
            
            this.getFormHM().put("sub_Map", listBo.getSub_Map());
            
            if(nodePrivMap.size()>0)
            	this.getFormHM().put("nodePriv","1"); //是否受节点权限控制
            else
            	this.getFormHM().put("nodePriv","0");
            
            // 方案查询
            TableDataConfigCache tableCache = (TableDataConfigCache) userView.getHm().get(subModuleId);
            if (StringUtils.isNotBlank(filterStr)) {
                tableCache.setTableSql(sql);
            }
            if(tableCache!=null)
            {
            	String sortSql = tableCache.getSortSql();
            	if(sortSql!=null&&sortSql.trim().length()>0)
            	{
            		String muster_sql=(String)this.userView.getHm().get("template_sql");
            		//bug 33668 人事异动按单位排序后和组织机构模块显示一致
            		ArrayList columns = tableCache.getDisplayColumns();
            		rebuildSearchSql(columns,paramBo,sortSql,tableCache,sql);
             		this.userView.getHm().put("template_sql",muster_sql+sortSql);
            	}
            	HashMap customParamHM = tableCache.getCustomParamHM()==null?new HashMap():tableCache.getCustomParamHM();
            	if(StringUtils.isNotBlank(property))
            		customParamHM.put("property", property);
            	if(StringUtils.isNotBlank(direction))
            		customParamHM.put("direction", direction);
            	if(StringUtils.isNotBlank(filterStr_catch))
            		customParamHM.put("filterStr_catch", filterStr_catch);
				tableCache.setCustomParamHM(customParamHM);
				
            }
            this.getFormHM().put("@eventlog","表单号:"+tabId+",任务号:"+taskId);
            ArrayList changeInfoList = new ArrayList();
            if(paramBo.getIsAotuLog()||paramBo.getIsRejectAotuLog()){//如果记录变动日志，获取变动的数据
			   TempletChgLogBo chglogBo=new TempletChgLogBo(this.frameconn,this.userView,paramBo);
			   changeInfoList=chglogBo.getFieldChangeInfo(taskId,tabId);
			}
            this.getFormHM().put("changeInfoList",changeInfoList);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

    /**
     * @author lis
     * @Description: 编辑sql
     * @date 2016-4-27
     * @param sql
     * @param tableHeadSetList
     * @return
     * @throws GeneralException
     */
    private String editSql(String sql, ArrayList<ColumnsInfo> columnsInfos) throws GeneralException {
        try {
            StringBuffer selectField = new StringBuffer();
            for (int i = 0; i < columnsInfos.size(); i++) {
                ColumnsInfo info = (ColumnsInfo) columnsInfos.get(i);
                String columnId = info.getColumnId();
                if ("parentid".equalsIgnoreCase(columnId.split("_")[0])) {
                    sql = sql.replace(",T.seqnum seqnum2", ",(select codeitemdesc from organization where codeitemid=T." + columnId + ") parentid");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return sql;
    }
	/**
	 * 栏目设置设置按单位部门岗位排序与机构表顺序一致，重组查询sql和排序语句。
	 * columns:栏目设置中的全部指标
	 * sortSql:原排序语句
	 * sql:原查询sql
	 * **/
    private void rebuildSearchSql(ArrayList columns,TemplateParam paramBo,String sortSql,TableDataConfigCache tableCache,String sql){
    	ArrayList orderlist=new ArrayList();//记录与机构表关联查询排序的指标
      	ArrayList orderSortlist=new ArrayList();//记录与机构表关联查询排序指标的排序规则
    	
    	if(sortSql.toLowerCase().indexOf("b0110")!=-1||sortSql.toLowerCase().indexOf("e0122")!=-1||sortSql.toLowerCase().indexOf("e01a1")!=-1){
	 		for(int num=0;num<columns.size();num++)
	 		{
	 			String orderByColumnid=null;//记录按照哪个字段与机构表关联查询排序
	 			ColumnsInfo column = (ColumnsInfo)columns.get(num);
	 			String columnId = column.getColumnId();
	 			String ordertype = column.getOrdertype();
	 			String orderrule = "1".equals(ordertype)?" asc ":" desc ";
	 			if(columnId!=null&&paramBo.getInfor_type() == 1&&(columnId.toLowerCase().indexOf("b0110")!=-1||columnId.toLowerCase().indexOf("e0122")!=-1||columnId.toLowerCase().indexOf("e01a1")!=-1)&&sortSql.indexOf(columnId)!=-1)
	 			{
	 				if(columnId.toLowerCase().indexOf("b0110")!=-1){
	 					orderByColumnid=columnId;
	 				}else if(columnId.toLowerCase().toLowerCase().indexOf("e0122")!=-1){
	 					orderByColumnid=columnId;
	 				}else if(columnId.toLowerCase().toLowerCase().indexOf("e01a1")!=-1){
	 					orderByColumnid=columnId;
	 				}
	 			}else if((paramBo.getInfor_type() == 2||paramBo.getInfor_type() == 3)&&columnId.toLowerCase().indexOf("codeitemdesc")!=-1&&sortSql.indexOf(columnId)!=-1){
	 				orderByColumnid = "b0110";
	                if (paramBo.getInfor_type() == 3)
	                	orderByColumnid = "e01a1";
	 			}
	 			if(orderByColumnid!=null){
	 				orderlist.add(orderByColumnid);
	 				orderSortlist.add(orderrule);
	 			}
	 		}
	 		//重组查询sql语句
	 		if(orderlist.size()>0){
				String tableSql=" select temp.* from ("+sql+") temp ";
					String fieldStr="";
					String joinStr="";
					String orderSortSql=" order by ";
	     		for(int num=0;num<orderlist.size();num++){
	     			fieldStr+=",org"+num+".a0000 org"+num+"a0000";
	     			joinStr+=" left join organization org"+num+" on temp."+orderlist.get(num)+"=org"+num+".codeitemid ";
	     			orderSortSql+="org"+num+"a0000 "+orderSortlist.get(num)+",";
	     		}
	     		sortSql=orderSortSql+sortSql.replace(" order by ", "");//将新组的order by放到原来的前面。
	     		tableSql=tableSql.replace("temp.*","temp.*"+fieldStr)+joinStr+" " ;
	     		tableCache.setTableSql(tableSql);
				tableCache.setSortSql(sortSql);
			}
    	}
    }


}
