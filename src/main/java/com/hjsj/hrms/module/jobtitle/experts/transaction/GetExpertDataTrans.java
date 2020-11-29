package com.hjsj.hrms.module.jobtitle.experts.transaction;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.module.jobtitle.experts.businessobject.ExpertsBo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
* <p>Title: GetExpertDataTrans</p>
* <p>Description: 专家列表显示</p>
* <p>Company: hjsj</p> 
* @author hej
* @date Nov 25, 2015 9:07:03 AM
 */
public class GetExpertDataTrans extends IBusiness{
	
	@Override
    public void execute() throws GeneralException {
		try {
			ExpertsBo bo = new ExpertsBo(this.frameconn,this.userView);
			/** 获取列头 */
			ArrayList<ColumnsInfo> columnList = bo.getColumnList();
			StringBuffer datasql =  new StringBuffer();
			datasql.append("select *");
			/** 获得需要查询的字段拼接sql */
			String selectsql = bo.getSelectSql();
			/** 获得sql*/
			datasql.append(" from ");
			datasql.append(selectsql+" ");
			

			/** 获取操作按钮*/
    		ArrayList buttonList = bo.getButtonList();
    		/** 获得列表操作的列id*/
    		ArrayList idlist = bo.getIdlist();
    		/**获得专家图片保存跟路径**/
    		ConstantXml constantXml = new ConstantXml(this.getFrameconn(),"FILEPATH_PARAM");
            String fileRootPath = constantXml.getNodeAttributeValue("/filepath", "rootpath");
            fileRootPath = fileRootPath.replace("\\", "/");
            /**获得登陆人的职称管理业务范围   **/
            String unit = this.userView.getUnitIdByBusi("9");
            String orgid = "";
            String func = "";
            if(unit!=null&&!"".equals(unit)){
            	if("UN`".equals(unit)){//全部范围
                	orgid = unit;
                }
                else{
                	String [] unitarr = unit.split("`");
                	for(String arr:unitarr){
                		arr = arr.substring(2,arr.length());
                		orgid+=arr+",";
                	}
                	orgid = orgid.substring(0,orgid.length()-1); 
                	String itemid = "";
                	if(orgid.indexOf(",")!=-1){
                		int index = orgid.indexOf(",");
                    	itemid = orgid.substring(0,index);
                	}else{
                		itemid = orgid;
                	}
                	String codeitemdesc = bo.getItemDesc(itemid);
                	func = itemid+"`"+codeitemdesc;
                }
            }
            /** 取得复杂查询下拉中的字段**/
            ArrayList fieldsArray = bo.getFieldsArray();
            
			TableConfigBuilder builder = new TableConfigBuilder("zc_reviewmeeting_experts_00001", columnList, "experts", userView, this.getFrameconn());
			builder.setDataSql(datasql.toString());
			builder.setOrderBy("order by w0111 desc ,A0000 asc  ");//根据是否是内外专家，a0000排序
			builder.setTitle("专家库");
			builder.setAutoRender(true);
			builder.setColumnFilter(true);
			if (this.userView.isSuper_admin() || this.userView.hasTheFunction("380020107")){
				builder.setScheme(true);
				builder.setSetScheme(true);
				builder.setShowPublicPlan(false);
				builder.setShowPublicPlan(this.userView.hasTheFunction("38002010701"));
			}
			builder.setLockable(true);
			builder.setSelectable(true);
			builder.setEditable(true);
			builder.setAnalyse(false);
//			if (this.userView.isSuper_admin() || this.userView.hasTheFunction("380020108")){//统计分析功能授权
//				builder.setAnalyse(true);
//			}
			builder.setConstantName("jobtitle/experts");
			builder.setTableTools(buttonList);
			builder.setPageSize(20);	//haosl 20160818 选人界面同意修改为显示15人
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());
			
			this.getFormHM().put("idlist", idlist.toString());
			this.getFormHM().put("fileRootPath", fileRootPath);
			this.getFormHM().put("orgid", orgid);
			this.getFormHM().put("func", func);
			this.getFormHM().put("fieldsArray", fieldsArray);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
