package com.hjsj.hrms.module.questionnaire.template.transaction;

import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

public class SearchTemplateLibraryTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try {
				//区分是查看问卷模板还是创建问卷 changxy 20160808
				//SeeTemplate rpc 传递的参数，map("SeeTemplate","SeeTemplate")
				String seeTemplate=(String)this.getFormHM().get("SeeTemplate")==null?"":(String)this.getFormHM().get("SeeTemplate");
			    ArrayList columns = new ArrayList();
			    //模板id列
			    ColumnsInfo column = new ColumnsInfo();
			    column.setColumnId("qnid");
			    column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			    columns.add(column);
			    
			    //模板id列
			    column = new ColumnsInfo();
			    column.setColumnId("qnname");
			    column.setColumnDesc("问卷名称");
			    column.setColumnWidth(250);
			    if(!"SeeTemplate".equals(seeTemplate)){
			    	column.setRendererFunc("QN_global.templateNameRenderFn");
			    }
				column.setEditableValidFunc("QN_global.validLibraryEdit");
			    columns.add(column);
			    
			    //qnid 模板id列
			    column = new ColumnsInfo();
			    column.setColumnId("isshare");
			    column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			    columns.add(column);
			    
			  //qnid 模板id列
			    column = new ColumnsInfo();
			    column.setColumnId("qntype");
			    column.setColumnDesc("问卷分类");
			    column.setColumnWidth(150);
			    ArrayList typeList = new ArrayList();
			    CommonData examen = new CommonData("1", "调查");
			    typeList.add(examen);
			    CommonData vote = new CommonData("2", "投票");
			    typeList.add(vote);
			    CommonData evaluation = new CommonData("3", "测评");
			    typeList.add(evaluation);
			    CommonData form = new CommonData("4", "表单");
			    typeList.add(form);
			    column.setOperationData(typeList);
			    column.setEditableValidFunc("QN_global.validLibraryEdit");
			    columns.add(column);
			    
			  //qnid 模板id列
			    FieldItem b0110 = DataDictionary.getFieldItem("b0110");
			    column = new ColumnsInfo(b0110);
				column.setColumnDesc("所属机构");
				column.setColumnRealDesc("所属机构");
			    column.setColumnWidth(150);
				column.setEditableValidFunc("QN_global.b0110ValidLibraryEdit");
				//权限控制方式 0：不控制 1：人员范围 2：操作单位 3：业务范围
				column.setCtrltype("3");
				//业务模块号
				column.setNmodule("4");
			    columns.add(column);
			    
			    column = new ColumnsInfo();
			    column.setColumnId("qnum");
			    column.setColumnDesc("题数");
			    column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			    columns.add(column);
			    
			    column = new ColumnsInfo();
			    column.setColumnId("page");
			    column.setColumnDesc("页数");
			    column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			    columns.add(column);
			    
			    if("SeeTemplate".equals(seeTemplate)){
			    	column=new ColumnsInfo();
			    	column.setColumnId("isshare");
			    	column.setColumnDesc("是否共享");
			    	ArrayList isShareList=new ArrayList();
			    	CommonData share=new CommonData("1","公有");
			    	CommonData disshare=new CommonData("0","私有");
			    	isShareList.add(share);
			    	isShareList.add(disshare);
			    	column.setOperationData(isShareList);
					column.setEditableValidFunc("QN_global.validLibraryEdit");
			    	columns.add(column);
			    }
			   /* column = new ColumnsInfo();
			    column.setColumnId("linknum");
			    column.setColumnDesc("引用次数");
			    column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			    columns.add(column);
			    
			    column = new ColumnsInfo();
			    column.setColumnId("recount");
			    column.setColumnDesc("收集份数");
			    column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			    columns.add(column);*/
			    
			  //qnid 模板id列
			    column = new ColumnsInfo();
			    //column.setColumnId("qndesc");
			    column.setColumnDesc("问卷情况");
			    column.setRendererFunc("QN_global.questionDescRender");//function(value,meta,record){return record.get('qnum')+'题，'+record.get('page')+'页';}");//record.get('qnum')+'题，'+record.get('page')+'页'
			    column.setColumnWidth(300);
			    columns.add(column);
			    
			    if("SeeTemplate".equals(seeTemplate)){
				    column=new ColumnsInfo();
				    column.setColumnDesc("操作");
				    column.setRendererFunc("QN_global.seeTmplateNameRenderFn");
				    column.setColumnWidth(70);
				    columns.add(column);			    	
			    }

				column = new ColumnsInfo();
				column.setColumnId("canedit");
				column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
				columns.add(column);

			  //qnid 模板id列
			  /*  column = new ColumnsInfo();
			    column.setColumnDesc("使用情况");
			    column.setRendererFunc("QN_global.questionInfoRender");//function(value,meta,record){return '引用'+record.get('linknum')+'次，收集'+record.get('recount')+'份';}");
			    column.setColumnWidth(300);
			    columns.add(column);*/
			
			    TableConfigBuilder builder = new TableConfigBuilder("qn_template_00002", columns, "qnLib", userView, this.frameconn);
			    if(!"SeeTemplate".equals(seeTemplate)){
			    	ArrayList buttons = new ArrayList();
			    	ButtonInfo b = new ButtonInfo("<span>问卷分类&nbsp;&nbsp;:&nbsp;&nbsp;<a id='template_all' href='javascript:QN_global.searchTemplateLib(\"all\");'>全部</a>&nbsp;&nbsp;"+
			    			"<a id='template_1' href='javascript:QN_global.searchTemplateLib(\"1\");'>调查</a>&nbsp;&nbsp;"+
			    			"<a id='template_2' href='javascript:QN_global.searchTemplateLib(\"2\");'>投票</a>&nbsp;&nbsp;"+
			    			"<a id='template_3' href='javascript:QN_global.searchTemplateLib(\"3\");'>评估</a>&nbsp;&nbsp;"+
			    	"<a id='template_4' href='javascript:QN_global.searchTemplateLib(\"4\");'>表单</a></span>&nbsp;");
			    	buttons.add(b);
			    	builder.setTableTools(buttons);
			    }
			   /* if("SeeTemplate".equals(SeeTemplate)){
			    	ButtonInfo editBtn=new ButtonInfo("编辑","");
			    	buttons.add(editBtn);
			    	ButtonInfo delBtn=new ButtonInfo("删除","QN_global.deleteTemplatePlan");
			    	delBtn.setParameter("action", "delete");
			    	buttons.add(delBtn);
			    }*/
			    if("SeeTemplate".equals(seeTemplate)){
			    	builder.setTitle("查看问卷模板");
			    }else{
			    	builder.setTitle("创建问卷");
			    }
			   
			    if("SeeTemplate".equals(seeTemplate)){
			    	ArrayList buttonlist=new ArrayList();
			    	ButtonInfo savebtn=new ButtonInfo("保存",""); 
			    	savebtn.setId("savebtn");
			    	buttonlist.add(savebtn);
			    	
			    	/*ButtonInfo backbtn=new ButtonInfo("返回",""); 
			    	backbtn.setId("backbtn");
			    	buttonlist.add(backbtn);取消返回按钮改为在标题栏显示关闭图片*/  
			    	
			    	ButtonInfo delbtn=new ButtonInfo("删除",""); 
			    	delbtn.setId("delbtn");
			    	buttonlist.add(delbtn);
			    	
			    	
			    	builder.setTableTools(buttonlist);
			    }

			    String funcStr = "";
				//选人控件优先级是业务范围>操作单位>人员范围，查询模板的时候，也按同样的权限控制
				String orgId = userView.getUnitIdByBusi("4");
				String[] orgArr = null;
				//如果登录的是自助用户，或者业务用户关联了自助用户，则只能看到自助用户所在单位的模板
				if(StringUtils.isNotBlank(userView.getA0100())){
					String userOrgId = userView.getUserOrgId();
					funcStr += " or " +  Sql_switcher.substr("'" +userOrgId + "'","1", Sql_switcher.length("b0110"))+ "=b0110 ";
				}else{
					//如果orgId的值是UN`，说明拥有整个组织机构的权限
					if("UN`".equalsIgnoreCase(orgId)){
						funcStr = " or 1=1 ";
					} else{
						orgArr = orgId.split("`");
						for(int x =0;x < orgArr.length;x++){
							funcStr += " or " + Sql_switcher.substr("'" + orgArr[x].substring(2,orgArr[x].length()) + "'", "1", Sql_switcher.length("b0110")) + "=b0110  ";
						}

					}
				}

			    //zhangh 2020-1-15 【56150】V77问卷调查：创建问卷，从问卷模板开始，选择问卷模板页面的查询框中提示应为请输入问卷名称
			    builder.setSearchConfig("QN20000002", "请输入问卷名称...",false);
			    StringBuffer sb = new StringBuffer();
			    sb.append("select TE.qnid,qnname,qntype,isshare,b0110,");
			    sb.append("(select count(1) from qn_question_item where qnid=TE.qnid and (typeid<9 or typeid>11) ) qnum,");
			    sb.append("(select count(1)+1 from qn_question_item where qnid=TE.qnid and typeid=10 ) page,");
			    if(this.userView.isSuper_admin()){
					sb.append(" 1 as canedit ");
				}else{
			    	sb.append(" case lower(createUser) when '"+userView.getUserName().toLowerCase()+"' then 1 else 0 end as canedit ");
				}
			   // sb.append("(select count(planid) from qn_plan where qnid=TE.qnid) linknum,");
			   // sb.append("(select sum(recoveryCount) from qn_plan where qnid=TE.qnid) recount ");
			    sb.append(" from qn_template TE,qn_template_library LI where  TE.qnid = LI.qnid ");
			    if(!this.userView.isSuper_admin()) {
					sb.append(" and (");
						sb.append(" (LI.isShare=0 and createUser='" + userView.getUserName() + "')");
						sb.append(" or (");
							sb.append(" LI.isShare=1 and (b0110 is null or " + Sql_switcher.isnull("b0110", "'-'")).append("='-' ");
							if (!StringUtils.isEmpty(funcStr)) {
								sb.append(funcStr);
							}
							sb.append(")");
						sb.append(")");
					sb.append(")");
				}
			    builder.setDataSql(sb.toString());
			    builder.setPageSize(20);
			    if("SeeTemplate".equals(seeTemplate))//取消创建问卷时 模板编辑功能
			    builder.setEditable(true);
			    if("SeeTemplate".equals(seeTemplate))//添加查看问卷模板选中框 changxy 
			    	builder.setSelectable(true);
			    String configStr = builder.createExtTableConfig();
			    
			    this.formHM.clear();
			    this.formHM.put("configStr", configStr);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
