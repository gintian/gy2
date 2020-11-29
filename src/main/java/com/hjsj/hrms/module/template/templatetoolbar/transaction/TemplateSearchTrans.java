package com.hjsj.hrms.module.template.templatetoolbar.transaction;

import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * 
* <p>Title:TemplateSearchTrans </p>
* <p>Description: 普通或方案查询</p>
* <p>Company: hjsj</p> 
* @author liuzy
* @date 
 */
public class TemplateSearchTrans extends IBusiness{
	@Override
    public void execute() throws GeneralException {
		
		String type = (String)this.getFormHM().get("type");
		String subModuleId = (String)this.getFormHM().get("subModuleId");
		MorphDynaBean md=(MorphDynaBean)this.getFormHM().get("customParams");
		ArrayList fieldsMap=(ArrayList)md.get("fieldsMap");
		String tab_id=(String)md.get("tab_id");//1:模板id
		HashMap map=new HashMap();
		for(int i=0;i<fieldsMap.size();i++){
			MorphDynaBean fieldMd=(MorphDynaBean)fieldsMap.get(i);
			String itemid=fieldMd.get("itemid")+"";
			String itemdesc=fieldMd.get("itemdesc")+"";
			String itemtype=fieldMd.get("itemtype")+"";
			String codesetid=fieldMd.get("codesetid")+"";
			String useflag=fieldMd.get("useflag")+"";
			FieldItem item = new FieldItem();
			item.setCodesetid(codesetid);
			item.setUseflag(useflag);
			item.setItemtype(itemtype);
			item.setItemid(itemid);
			item.setItemdesc(itemdesc);
			map.put(itemid,item);
		}
		TableDataConfigCache cache = (TableDataConfigCache)this.userView.getHm().get(subModuleId);
		
		StringBuffer condsql = new StringBuffer(" and ");
		String valuesql = "";
		if("1".equals(type)){//查询栏查询
			 TemplateParam param = new TemplateParam(this.getFrameconn(),this.userView,Integer.valueOf(tab_id));
			 List values = (ArrayList) this.getFormHM().get("inputValues");
			 if(values==null || values.isEmpty()){
				 //28382  linbz 现由于增加卡片下的快速查询，默认进入为卡片状态下时，subModuleId列表下的表格对象为空，故增加校验cache对象是否为空
				 if(null != cache)
					 cache.setQuerySql("");
				 this.getFormHM().put("condsql", " and 1=1 ");//bug 32799 用户点击全部，自动全部人都打上勾。
				 return;
			 }
			 //查询时判断模板 lis 201609-2
			 String key = "A0101";
			 String valueCol = "a0101_1";
			 if(param.getInfor_type() == 1){//人员模板
				 if (param.getOperationType() == 0) {//人员调入型
					 valueCol = "a0101_2";
				 } else {
					 valueCol = "a0101_1";
				 }
			 }else  if(param.getInfor_type() == 2){//单位模板
				 key = "B0110";
				 if (param.getOperationType() == 5) {//创建
					 valueCol = "codeitemdesc_2";
					} else {
						valueCol = "codeitemdesc_1";
				 }
			 }else if(param.getInfor_type() == 3){//岗位模板
				 key = "E01A1";
				 if (param.getOperationType() == 5) {//创建
					 valueCol = "codeitemdesc_2";
					} else {
						valueCol = "codeitemdesc_1";
				 }
			 }
			 FieldItem ketItem = DataDictionary.getFieldItem(key);
			 if(ketItem != null){
				 for(int i=0;i<values.size();i++){
					 String value =SafeCode.decode(values.get(i).toString());
					 if (i == 0) {
						 {
							 condsql.append(" (" + valueCol + " like '%"+value.replace("'", "")+"%'");
						 }
					 }else {
						 condsql.append(" or " + valueCol + " like '%"+value.replace("'", "")+"%'");
					 }
				 }
				 condsql.append(")");
			 }
		}else if("2".equals(type)){//方案查询
			 String exp = (String) this.getFormHM().get("exp");
			 exp = SafeCode.decode(exp);
	         String cond = (String) this.getFormHM().get("cond");
	         cond = SafeCode.decode(cond);
	         if(cond.length()<1 || exp.length()<1){
	        	 if(null != cache)
	        		 cache.setQuerySql("");
	        	 this.getFormHM().put("condsql", " and 1=1");
	        	 return;
	         }
	         //这里的cond经过decode解码之后，会出现原本半角的情况，现在变成全角，所以需要PubFunc.keyWord_reback()进行特殊字符的转换，liuzy 20150225
	         FactorList factor_bo=new FactorList(PubFunc.keyWord_reback(exp),PubFunc.keyWord_reback(cond.toUpperCase()),userView.getUserId(),map);
	         condsql.append(factor_bo.getSingleTableSqlExpression("T"));
		}
		this.getFormHM().put("condsql", condsql.toString());
		//cache.setQuerySql(condsql.toString());
	}

}
