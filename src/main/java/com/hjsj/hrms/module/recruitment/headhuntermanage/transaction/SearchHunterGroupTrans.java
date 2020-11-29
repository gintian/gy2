package com.hjsj.hrms.module.recruitment.headhuntermanage.transaction;

import com.hjsj.hrms.businessobject.hire.HireTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchHunterGroupTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try{
			
			
			//如果有type参数说明是查询组件进入的
			String type = (String)this.getFormHM().get("type");
			if(type!=null){
				TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("zp_headhunter_00001");
				StringBuffer condsql = new StringBuffer(" and ");
				//快速查询
				if("1".equals(type)){
					 List values = (ArrayList) this.getFormHM().get("inputValues");
					 if(values==null || values.isEmpty()){
						 tableCache.setQuerySql("");
						 userView.getHm().put("zp_headhunter_00001", tableCache);
						 return;
					 }
					 condsql.append("(");
					 for(int i=0;i<values.size();i++){
						 String value =SafeCode.decode(values.get(i).toString());
						 value = value.replace('\'', '‘');
						 condsql.append(" z6003 like '%"+value+"%' or ");
					 }
					 condsql.append(" 1=2 )");
				}else if("2".equals(type)){//方案查询
					HashMap queryFields = tableCache.getQueryFields();
					 String exp = (String) this.getFormHM().get("exp");
					 exp = SafeCode.decode(exp);
					 exp = PubFunc.keyWord_reback(exp);
			         String cond = (String) this.getFormHM().get("cond");
			         cond = SafeCode.decode(cond);
			         cond = PubFunc.keyWord_reback(cond);
			         if(cond.length()<1 || exp.length()<1){
			        	 	 tableCache.setQuerySql("");
						 userView.getHm().put("zp_headhunter_00001", tableCache);
			        	 	return;
			         }
			         FactorList parser = new FactorList(exp ,cond, userView.getUserName(),queryFields);
			       StringBuffer aa = parser.getErrorInfo();
			         condsql.append(parser.getSingleTableSqlExpression("myGridData"));
				}
				tableCache.setQuerySql(condsql.toString());
				userView.getHm().put("zp_headhunter_00001", tableCache);
				return;
			}
			
			HireTemplateBo bo = new HireTemplateBo(this.frameconn);
			String b0110 = bo.getB0110(this.userView);
			// 查询字段
			StringBuffer fields = new StringBuffer();
			//获取猎头渠道（公司）表指标
			ArrayList fielditems = DataDictionary.getFieldList("z60", Constant.USED_FIELD_SET);
			// 列头 
			ArrayList columns = new ArrayList();
			//循环指标，生成数据
			for(int i=0;i<fielditems.size();i++){
				FieldItem fi = (FieldItem) fielditems.get(i);
				if(!"0".equals(fi.getState())||"z6000".equals(fi.getItemid())){
				    fields.append(fi.getItemid()+",");
				    ColumnsInfo c = new ColumnsInfo(fi);
				    if("z6000".equals(fi.getItemid())){
				        c.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
				        c.setEncrypted(true);
				    }
				    if("z6003".equals(fi.getItemid())){
				        c.setRendererFunc("hunternamerender");
				        c.setLocked(true);
				    }
				    //if(fi.getItemid().equals("z6016")){
				    //	c.setRendererFunc("companyrender");
				    //}
				    if(!"HJSJ".equalsIgnoreCase(b0110)&& "z6005".equals(fi.getItemid())){
				    	c.setCtrltype("3");
				    	c.setNmodule("7");
				    }
				    columns.add(c);
				    
				}
			}
			
			//添加hunters列
			ColumnsInfo hunterscol = new ColumnsInfo();
			hunterscol.setColumnId("hunters");
			hunterscol.setColumnType("N");
			hunterscol.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columns.add(hunterscol);
			
			//添加自定义显示列
			ColumnsInfo diy = new ColumnsInfo();
			diy.setColumnDesc("操作");
			diy.setRendererFunc("searchhunters");//自定义渲染方法
			columns.add(diy);
			
			//权限 sql
			String privorg = userView.getUnitIdByBusi("7");
			String orgin = "or "+Sql_switcher.isnull("z6005", "'000'")+"='000' ";
			//当前用户没有任何权限时不显示列表记录
			if(!"".equals(privorg)&&privorg!=null)
			{
				//当 不是 UN` 开头  、中间没有UN`
				if(privorg.indexOf("UN`")==-1){
					String[] orgids = privorg.split("`");
					for(int i=0;i<orgids.length;i++){
						orgin+=" or Z6005 like '"+orgids[i].substring(2)+"%' ";
					}
				}else{
					orgin = " or 1=1 ";
				}
			}else{
				orgin = " and 1 = 2";
			}
			
			StringBuffer sqlstr = new StringBuffer(" select "+fields+" (select count(1) from zp_headhunter_login where z6000=z60.z6000) hunters from z60 ");
			sqlstr.append(" where  ( Z6017='1' ");
			sqlstr.append(orgin+") ");
			
			this.getFormHM().put("groupcolumns", columns);
			this.getFormHM().put("sqlstr", sqlstr.toString());
			this.getFormHM().put("constantxml", "recruitment/headhuntergroup");
			
			if(userView.hasTheFunction("3110409"))
				this.getFormHM().put("showPublicPlan", "true");
			else
				this.getFormHM().put("showPublicPlan", "false");
			
			if(userView.hasTheFunction("3110410"))
				this.getFormHM().put("isAnalyse", "true");
			else
				this.getFormHM().put("isAnalyse", "false");
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
