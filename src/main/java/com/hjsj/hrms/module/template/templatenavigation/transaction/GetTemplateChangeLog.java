package com.hjsj.hrms.module.template.templatenavigation.transaction;

import com.hjsj.hrms.module.template.templatecard.businessobject.TempletChgLogBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GetTemplateChangeLog extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String searchType = (String) this.getFormHM().get("optype");// 按人员还是单位
		String searchPersonName = (String) this.getFormHM().get("personname");// 人员姓名或者单位名称
		String searchOrgName = (String) this.getFormHM().get("orgName");// 人员姓名或者单位名称
		String searchFieldName = (String) this.getFormHM().get("fieldName");// 指标名称
		String searchFieldSetName = (String) this.getFormHM().get("fieldsetName");// 指标名称
		String searchTable = (String) this.getFormHM().get("tabid");// 模版名称
		String searchYear = (String) this.getFormHM().get("year");// 查询年度
		Boolean isSearchTempLog=false;
		Date date = new Date();
		TempletChgLogBo changLogBo = new TempletChgLogBo(this.frameconn, this.userView);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		DbWizard dbwizard = new DbWizard(this.frameconn);
		String currentYear = sdf.format(date);
		if (StringUtils.isBlank(searchYear)) {
			searchYear = currentYear;
		}
		if(currentYear.equalsIgnoreCase(searchYear)){
			isSearchTempLog=true;
			if (!dbwizard.isExistTable("templet_chg_log", false)) {
				changLogBo.createTemplateChgLogTable("templet_chg_log");
			}
		}
		String searchLogSql = "";
		String searchTabNameSql = "";
		String searchFieldSql = "";
		String whereSql = " where ";
		String tableNamewhereSql = " where ";
		String fieldNamewhereSql = " where ";
		String orderby = "order by createtime desc";
		if (!dbwizard.isExistTable("templet_chg_log" + searchYear, false)) {
			changLogBo.createTemplateChgLogTable("templet_chg_log" + searchYear);
		}
		if(isSearchTempLog){
			searchLogSql = "select a.log_id,a.objectid,a.a0101,a.only_value,a.info_type,a.opt_type,a.ins_id,a.task_id,a.tabid,a.pageid,a.subflag,a.setname,a.field_id,a.field_name,a.sub_content,a.content_1,a.content_2,a.record_key_id,"+Sql_switcher.dateToChar("a.createtime", "yyyy-mm-dd hh24:mi:ss")+" createtime,a.createuser,t.name tablename,f.fieldsetdesc from ( select *  from templet_chg_log" + searchYear +" union all  select * from templet_chg_log"
					+ " ) a left join template_table t on a.tabid=t.tabid left join fieldset f on lower("+Sql_switcher.substr("a.setname", "3", "3")+")=lower(f.fieldSetId)";
			searchTabNameSql = "select a.tabid from ( select * from templet_chg_log" + searchYear+" union all  select * from templet_chg_log ) a ";
			searchFieldSql = "select a.field_id,a.field_name from ( select * from templet_chg_log" + searchYear+"  union all  select * from templet_chg_log ) a ";
		}else{
			searchLogSql = "select a.log_id,a.objectid,a.a0101,a.only_value,a.info_type,a.opt_type,a.ins_id,a.task_id,a.tabid,a.pageid,a.subflag,a.setname,a.field_id,a.field_name,a.sub_content,a.content_1,a.content_2,a.record_key_id,"+Sql_switcher.dateToChar("a.createtime", "yyyy-mm-dd hh24:mi:ss")+" createtime,a.createuser,t.name tablename,f.fieldsetdesc  from templet_chg_log" + searchYear
					+ " a left join template_table t on a.tabid=t.tabid left join fieldset f on lower("+Sql_switcher.substr("a.setname", "3", "3")+")=lower(f.fieldSetId)";
			searchTabNameSql = "select tabid from templet_chg_log" + searchYear+" a ";
			searchFieldSql = "select field_id,field_name from templet_chg_log" + searchYear+" a ";
		}

		ArrayList paramList = new ArrayList();
		Boolean isHaveWhere = false;
		ArrayList tableNameParamList = new ArrayList();
		Boolean tableNameisHaveWhere = false;
		ArrayList fieldNameParamList = new ArrayList();
		Boolean fieldNameisHaveWhere = false;

		if (StringUtils.isNotBlank(searchType)) {
			if (isHaveWhere) {
				whereSql += " and ";
			}
			whereSql += " a.info_type='" + searchType + "' ";
			isHaveWhere = true;
			
			if (fieldNameisHaveWhere) {
				fieldNamewhereSql += " and ";
			}
			fieldNamewhereSql += " a.info_type='" + searchType + "' ";
			fieldNameisHaveWhere = true;

			if (tableNameisHaveWhere) {
				tableNamewhereSql += " and ";
			}
			tableNamewhereSql += " a.info_type='" + searchType + "' ";
			tableNameisHaveWhere = true;
			
		}
		if ("1".equalsIgnoreCase(searchType)) {
			if (StringUtils.isNotBlank(searchPersonName)) {
				if (isHaveWhere) {
					whereSql += " and ";
				}
				whereSql += " (a.A0101 like '%" + searchPersonName + "%' or a.only_value='" + searchPersonName + "' ) ";
				isHaveWhere = true;
				
				if (fieldNameisHaveWhere) {
					fieldNamewhereSql += " and ";
				}
				fieldNamewhereSql += " (a.A0101 like '%" + searchPersonName + "%' or a.only_value='" + searchPersonName + "' ) ";
				fieldNameisHaveWhere = true;

				if (tableNameisHaveWhere) {
					tableNamewhereSql += " and ";
				}
				tableNamewhereSql+= " (a.A0101 like '%" + searchPersonName + "%' or a.only_value='" + searchPersonName + "' ) ";
				tableNameisHaveWhere = true;
			}
		}
		if ("2".equalsIgnoreCase(searchType)) {
			if (StringUtils.isNotBlank(searchOrgName)) {
				if (isHaveWhere) {
					whereSql += " and ";
				}
				whereSql += " a.objectid='" + searchOrgName + "' ";
				isHaveWhere = true;
				
				if (fieldNameisHaveWhere) {
					fieldNamewhereSql += " and ";
				}
				fieldNamewhereSql += " a.objectid='" + searchOrgName + "' ";
				fieldNameisHaveWhere = true;

				if (tableNameisHaveWhere) {
					tableNamewhereSql += " and ";
				}
				tableNamewhereSql+= " a.objectid='" + searchOrgName + "' ";
				tableNameisHaveWhere = true;
			}
		}
		if (StringUtils.isNotBlank(searchFieldName)&&!"-1".equalsIgnoreCase(searchFieldName)) {
			if (isHaveWhere) {
				whereSql += " and ";
			}
			whereSql += " a.field_id='" + searchFieldName + "' ";
			isHaveWhere = true;
		}
		if (StringUtils.isNotBlank(searchTable)&&!"-1".equalsIgnoreCase(searchTable)) {
			if (isHaveWhere) {
				whereSql += " and ";
			}
			whereSql += " a.tabid='" + searchTable + "' ";
			isHaveWhere = true;
		}
		String nflag="0";
		if("-1".equalsIgnoreCase(searchFieldSetName)){
			if("1".equalsIgnoreCase(searchType)){
				searchFieldSetName="A01";
			}else{
				searchFieldSetName="B01";
			}
		}
		if("2".equalsIgnoreCase(searchType)){
			nflag="4";
		}
		ArrayList fieldSetTemp = fieldListTemp(nflag, searchType);
		ArrayList itemList = getItemList(searchFieldSetName,this.userView);
		if(fieldSetTemp.size()>0){
			StringBuffer newFieldPriv=new StringBuffer();
			if (StringUtils.isNotBlank(searchFieldSetName)&&!"-1".equalsIgnoreCase(searchFieldSetName)) {
				newFieldPriv.append(" (a.opt_type=3 and upper("+Sql_switcher.substr("a.setname", "3", "3")+")=upper('"+searchFieldSetName+"'))");
			}else{
				HashMap map = (HashMap) fieldSetTemp.get(0);
				newFieldPriv.append(" (a.opt_type=3 and upper("+Sql_switcher.substr("a.setname", "3", "3")+")=upper('"+map.get("id")+"'))");
			}
			if (itemList.size()>0) {
				newFieldPriv.append(" or ( a.opt_type<>3 and upper(a.field_id) in ( ");
				for(int i=0;i<itemList.size();i++){
					HashMap map = (HashMap) itemList.get(i);
					newFieldPriv.append("'"+map.get("id")+"'");
					if(i!=itemList.size()-1){
						newFieldPriv.append(",");
					}
				}
				newFieldPriv.append(" )) ");
				if (isHaveWhere) {
					whereSql += " and ";
				}
				whereSql += " ( "
						+ newFieldPriv.toString() + ") ";
				isHaveWhere = true;
				
				if (fieldNameisHaveWhere) {
					fieldNamewhereSql += " and ";
				}
				fieldNamewhereSql += "( "
						+ newFieldPriv.toString() + ") ";
				fieldNameisHaveWhere = true;

				if (tableNameisHaveWhere) {
					tableNamewhereSql += " and ";
				}
				tableNamewhereSql+= " ( "
						+ newFieldPriv.toString() +" ) ";
				tableNameisHaveWhere = true;
			}
		}
			
		
		if (!this.userView.isAdmin()) {
			if ("1".equalsIgnoreCase(searchType)) {
				ArrayList privDbList = userView.getPrivDbList();
				String privDbStr = "";
				String operOrg = userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理
																// 6：培训管理 7：招聘管理
																// 8:业务模板
				if ((operOrg != null) && (!"UN`".equalsIgnoreCase(operOrg))) {
					String strB0110Where = "";
					if (operOrg.length() > 3) {
						String[] temp = operOrg.split("`");
						for (int j = 0; j < temp.length; j++) {
							if (temp[j] != null && temp[j].length() > 0) {
								if("UN".equalsIgnoreCase(temp[j].substring(0,2))){
									strB0110Where = strB0110Where + " or  b0110 like '" + temp[j].substring(2) + "%'";
								}
								if("UM".equalsIgnoreCase(temp[j].substring(0,2))){
									strB0110Where = strB0110Where + " or  e0122 like '" + temp[j].substring(2) + "%'";
								}
								if("@K".equalsIgnoreCase(temp[j].substring(0,2))){
									strB0110Where = strB0110Where + " or  e01a1 like '" + temp[j].substring(2) + "%'";
								}
							}
						}
					}
					strB0110Where = strB0110Where + " or " + Sql_switcher.sqlNull("b0110", "##") + "='##'";
					if (StringUtils.isNotBlank(strB0110Where)) {
						strB0110Where = strB0110Where.substring(3);
						strB0110Where = "(" + strB0110Where + ")";
						for (int i = 0; i < privDbList.size(); i++) {
							String dbname = (String) privDbList.get(i);
							if (i > 0)
								privDbStr += " UNION ";
							privDbStr += " select lower('" + dbname.toLowerCase() + "'" + Sql_switcher.concat()
									+ "a0100) from " + dbname + "a01  where " + strB0110Where;
						}
						if (StringUtils.isNotBlank(privDbStr)) {
							if (isHaveWhere) {
								whereSql += " and ";
							}
							whereSql += " lower(a.objectid) in (" + privDbStr + ") ";
							isHaveWhere = true;
							
							if (fieldNameisHaveWhere) {
								fieldNamewhereSql += " and ";
							}
							fieldNamewhereSql += " lower(a.objectid) in (" + privDbStr + ") ";
							fieldNameisHaveWhere = true;

							if (tableNameisHaveWhere) {
								tableNamewhereSql += " and ";
							}
							tableNamewhereSql+= " lower(a.objectid) in (" + privDbStr + ") ";
							tableNameisHaveWhere = true;
							
						} else {
							if (isHaveWhere) {
								whereSql += " and ";
							}
							whereSql += " 1=2 ";
							isHaveWhere = true;
							
							if (fieldNameisHaveWhere) {
								fieldNamewhereSql += " and ";
							}
							fieldNamewhereSql += " 1=2 ";
							fieldNameisHaveWhere = true;

							if (tableNameisHaveWhere) {
								tableNamewhereSql += " and ";
							}
							tableNamewhereSql += " 1=2 ";
							tableNameisHaveWhere = true;
						}

					}
				}
			}
			//过滤表
			String tmp = getTemplates();
			if(tmp.length()==0)
			{
				if (fieldNameisHaveWhere) {
					fieldNamewhereSql += " and ";
				}
				fieldNamewhereSql += " 1=2 ";
				fieldNameisHaveWhere = true;

				if (tableNameisHaveWhere) {
					tableNamewhereSql += " and ";
				}
				tableNamewhereSql+= " 1=2 ";
				tableNameisHaveWhere = true;
				
				if (isHaveWhere) {
					whereSql += " and ";
				}
				whereSql += " 1=2 ";
				isHaveWhere = true;
			}
			else
			{
				if (fieldNameisHaveWhere) {
					fieldNamewhereSql += " and ";
				}
				fieldNamewhereSql+=" a.tabid in (";
				fieldNamewhereSql+=tmp;
				fieldNamewhereSql+=")";
				fieldNameisHaveWhere = true;

				if (tableNameisHaveWhere) {
					tableNamewhereSql += " and ";
				}
				tableNamewhereSql+=" a.tabid in (";
				tableNamewhereSql+=tmp;
				tableNamewhereSql+=")";
				tableNameisHaveWhere = true;
				
				if (isHaveWhere) {
					whereSql += " and ";
				}
				whereSql+=" a.tabid in (";
				whereSql+=tmp;
				whereSql+=")";
				isHaveWhere = true;
			}
		}
		if (isHaveWhere) {
			searchLogSql += whereSql;
			searchTabNameSql += tableNamewhereSql;
			searchFieldSql += fieldNamewhereSql;
		}
		ArrayList<ColumnsInfo> column = new ArrayList<ColumnsInfo>();
		column = changLogBo.getChangeLogColumns();
		TableConfigBuilder builder = new TableConfigBuilder(SafeCode.encode(PubFunc.encrypt("changeInfo")), column,
				"changeInfo", userView, this.getFrameconn());
		builder.setDataSql(searchLogSql);
		builder.setOrderBy(orderby);
		builder.setSelectable(false);
		// builder.setColumnFilter(true);
		builder.setPageSize(20);
		builder.setTableTools(new ArrayList());
		String config = builder.createExtTableConfig();
		ArrayList templateList = changLogBo.getTemplateTableList(searchTabNameSql);
		ArrayList templateFieldList = changLogBo.getTemplateFieldList(searchFieldSql);
		this.getFormHM().put("templatejson", templateList);
		this.getFormHM().put("templateFieldjson", templateFieldList);
		this.getFormHM().put("templateFieldSetNameJson", fieldSetTemp);
		this.getFormHM().put("tableConfig", config.toString());
	}
	
	/**
	 * 求权限范围下的模板串
	 * @return
	 */
	private String getTemplates() {
		StringBuffer mb=new StringBuffer();
		String rsbd=this.userView.getResourceString(IResourceConstant.RSBD);
		mb.append(rsbd);
		mb.append(",");
		
		String orgbd=this.userView.getResourceString(IResourceConstant.ORG_BD);
		mb.append(orgbd);
		mb.append(",");	
		String posbd=this.userView.getResourceString(IResourceConstant.POS_BD);
		mb.append(posbd);
		mb.append(",");	
		
		String gzbd=this.userView.getResourceString(IResourceConstant.GZBD);
		mb.append(gzbd);
		mb.append(",");					
		String bybd=this.userView.getResourceString(IResourceConstant.INS_BD);
		mb.append(bybd);
		mb.append(",");	
		String pso=this.userView.getResourceString(IResourceConstant.PSORGANS);
		mb.append(pso);
		mb.append(",");	
		String fg=this.userView.getResourceString(IResourceConstant.PSORGANS_FG);
		mb.append(fg);
		mb.append(",");	
		String gx=this.userView.getResourceString(IResourceConstant.PSORGANS_GX);
		mb.append(gx);
		mb.append(",");	
		String jcg=this.userView.getResourceString(IResourceConstant.PSORGANS_JCG);
		mb.append(jcg);
		mb.append(",");	
		String[] bdarr=StringUtils.split(mb.toString(),",");
		if(bdarr==null || bdarr.length==0)
			return "";
		
		String tmp=StringUtils.join(bdarr, ',');
		tmp = tmp.replace("r", "");
		tmp = tmp.replace("R", "");
		tmp = tmp.replace(" ", "");
		tmp = tmp.replace(",,", ",");
		return tmp;
	}
	

	public ArrayList fieldListTemp(String nflag, String infor_type){//当nflag为4时，只需要单位信息集   
		ArrayList fieldsetlist = new ArrayList();
		try {
			ArrayList listset = new ArrayList();
			if(!"4".equals(nflag))
				listset.addAll(DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET));
			listset.addAll(DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET)); 
			/***bug52775 组织机构：机构变动日志，信息集选不到岗位子集*/ 
			if("4".equals(nflag)||"0".equals(nflag))
				listset.addAll(DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET));
				
			for(int i=0;i<listset.size();i++){
				 Map<String, String> map = new HashMap<String, String>();
				 FieldSet fieldset = (FieldSet)listset.get(i);
				 if(fieldset==null)
					 continue;
				 if("A00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }else  if("B00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }else  if("K00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }
				 if("2".equals(infor_type)){//单位
					 if(fieldset.getFieldsetid().toUpperCase().startsWith("A"))
						 continue; 
				 }
				 /***bug52389 V76人事异动 变动日志选择人员信息时还会显示单位岗位的信息集 此不是bug*/
//				 if("1".equals(infor_type)){//人员
//					 if(fieldset.getFieldsetid().toUpperCase().startsWith("B")||fieldset.getFieldsetid().toUpperCase().startsWith("K"))
//						 continue; 
//				 }
				 if(this.userView.analyseTablePriv(fieldset.getFieldsetid())==null)
					 continue;
				 if("0".equals(this.userView.analyseTablePriv(fieldset.getFieldsetid())))
					 continue;
				 map.put("id", fieldset.getFieldsetid());
				 map.put("name", fieldset.getFieldsetid()+"-"+fieldset.getCustomdesc());
				 fieldsetlist.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
		return fieldsetlist;
	}
	
	public ArrayList getItemList(String fieldsetid,UserView uv){
		ArrayList list = new ArrayList();
		FieldSet fieldset=DataDictionary.getFieldSetVo(fieldsetid);
		ArrayList dylist = new ArrayList();
		
		if(fieldsetid!=null&&fieldsetid.length()>0){
			dylist = DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);
		}
		Map<String, String> map = new HashMap<String, String>();
//		map.put("", "");
//		list.add(map);
		try {
			for(int i=0;i<dylist.size();i++){
				map = new HashMap<String, String>();
				FieldItem fielditem = (FieldItem)dylist.get(i);
				String itemid = fielditem.getItemid();
				String itemdesc = fielditem.getItemdesc();
				if("M".equalsIgnoreCase(fielditem.getItemtype()))
					continue;
				/*
				 * 【6127】自助服务/员工信息维护，计算，公式，选择指标，指标没有经过权限控制，不对。
				 * 修改为有读或写权限的指标才能显示     jingq add 2014.12.18
				 * r45 培训费用表培训 模块的指标不需要权限控制        chenxg  add 2014-01-05
				 */
				if(!"r45".equalsIgnoreCase(fieldsetid) && "0".equalsIgnoreCase(uv.analyseFieldPriv(itemid))){
					continue;
				}
				
				if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))
						||!"0".equals(fieldset.getChangeflag())){
					 map.put("id", itemid.toUpperCase());
					 map.put("name", itemid.toUpperCase()+":"+itemdesc);
					list.add(map);
				}
			}
			if("B01".equalsIgnoreCase(fieldsetid)){//bug 48754 特殊字段从数据字典中取不到需特殊处理
				//,start_date,codesetid,parentid,codeitemdesc,corcode,to_id,
				map = new HashMap<String, String>();
				map.put("id", "start_date".toUpperCase());
				map.put("name", "start_date".toUpperCase()+":"+"");
				list.add(map);
				map = new HashMap<String, String>();
				map.put("id", "codesetid".toUpperCase());
				map.put("name", "codesetid".toUpperCase()+":"+"");
				list.add(map);
				map = new HashMap<String, String>();
				map.put("id", "parentid".toUpperCase());
				map.put("name", "parentid".toUpperCase()+":"+"");
				list.add(map);
				map = new HashMap<String, String>();
				map.put("id", "codeitemdesc".toUpperCase());
				map.put("name", "codeitemdesc".toUpperCase()+":"+"");
				list.add(map);
				map = new HashMap<String, String>();
				map.put("id", "corcode".toUpperCase());
				map.put("name", "corcode".toUpperCase()+":"+"");
				list.add(map);
				map = new HashMap<String, String>();
				map.put("id", "to_id".toUpperCase());
				map.put("name", "to_id".toUpperCase()+":"+"");
				list.add(map);
			}
		} catch(Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
		return list;
	}

}
