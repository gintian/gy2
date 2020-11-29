package com.hjsj.hrms.module.muster.mustermanage.transaction;

import com.hjsj.hrms.module.muster.mustermanage.businessobject.MusterManageService;
import com.hjsj.hrms.module.muster.mustermanage.businessobject.impl.MusterManageServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 项目名称：hcm7.x
 * 类名称：MusterSpDetail.java
 * 类描述：简单花名册优化之querybox返回数据处理
 * 创建人：PanCS
 * 创建时间：2019年2月28日
 * 备注： 
 * @version
 */
public class MusterQueryDelTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try{
			String flag =  (String) this.getFormHM().get("flag");// flag = del 为删除 否则为查询
			if ("del".equals(flag)) {
				MusterManageService mustermanageservice =new MusterManageServiceImpl(frameconn,userView);
				String tabid=(String) this.getFormHM().get("tabid").toString(); 
				tabid = SafeCode.decode(tabid);
				mustermanageservice.deleteMuster(tabid);
			}else {
				// 用于存放处理后的查询条件
				String condSql = "";
				String type = (String) this.getFormHM().get("type");// 查询类型，1为输入查询，2为方案查询
				ArrayList<String> valuesList = (ArrayList) this.getFormHM().get("inputValues");//页面查询框返回的内容
				String subModuleId=(String)this.getFormHM().get("subModuleId"); // 判断是哪个界面的查询数据
				MorphDynaBean bean = (MorphDynaBean)this.getFormHM().get("customParams");//查询组件返回条件集合
				TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(subModuleId);
				String exp = (String) this.getFormHM().get("exp"); // ????
				String cond = (String) this.getFormHM().get("cond"); // ???
				HashMap queryFields = tableCache.getQueryFields();
				if("1".equals(type)) {
					condSql = this.getSql(type, valuesList, subModuleId, bean);
				} else if ("2".equals(type)) {// 处理querybox选择的复杂查询
					condSql = this.handleQueryBox(exp, cond, queryFields);
				}
				if(condSql.length()>0){//页面模糊查询
					tableCache.setQuerySql(" and ( "+condSql.replaceAll("data.", "")+" ) ");//去掉表名，防止表格工具追加后报错
				}else {
					tableCache.setQuerySql("");//去掉表名，防止表格工具追加后报错
				}
				this.userView.getHm().put(SafeCode.encode(PubFunc.encrypt("musterManage")), tableCache);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 处理输入框内输入的查询条件
	 * @param type
	 * @param valuesList
	 * @param subModuleId
	 * @param bean
	 * @return String
	 */
	public String getSql(String type,ArrayList<String> valuesList,String subModuleId,MorphDynaBean bean) {
		// 用于存放处理后的查询条件
		String condSql = "";
		try {
			StringBuffer str = new StringBuffer();
			if("musterManage".equals(subModuleId)) {
				// 输入的内容
				for(int i=0;i<valuesList.size();i++){
					String queryValue = SafeCode.decode((String) valuesList.get(i));
						if(i==0){
						str.append("hzname like '%"+queryValue+"%'");
						str.append(" or create_name like '%"+queryValue+"%'");
					}else{
						str.append(" or hzname like '%"+queryValue+"%'");
						str.append(" or create_name like '%"+queryValue+"%'");
					}
				}
			}
			if(subModuleId.indexOf("showMuster")!=-1) {
				String  tabid = (String) bean.get("tabid"); // 获取当前表的id
				String sql = new String(" SELECT field_name  FROM  Lbase  where tabid = ? and Field_type = 'A'  ");
				ContentDAO dao = new ContentDAO(this.frameconn);
				
				ArrayList params = new ArrayList();
				params.add(tabid);
				
				RowSet fieldNameList = dao.search(sql, params);
				int judgeFirst = 0;
				while(fieldNameList.next()) {
					String fieldName = fieldNameList.getString("field_name");
					fieldName = fieldName.substring(4);
					FieldItem fieldItem = DataDictionary.getFieldItem(fieldName);
					if(fieldItem != null) {
						if ("0".equals(fieldItem.getCodesetid())) {
							if(judgeFirst == 0) {
								for(int i=0;i<valuesList.size();i++){
									String queryValue = SafeCode.decode((String) valuesList.get(i));
									if(i == 0)
										str.append(fieldName + " like '%"+queryValue+"%'");
									else 
										str.append("  or  " + fieldName + " like '%"+queryValue+"%'");
								}
							}else {
								for(int i=0;i<valuesList.size();i++){
									String queryValue = SafeCode.decode((String) valuesList.get(i));
									str.append("  or  " + fieldName + " like '%"+queryValue+"%'");
								}
							}
							judgeFirst ++;
						}
					}
				}
			}
			if(valuesList.size()>0)
				condSql += str.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return condSql;
	}
	/**
	 * 处理下拉框中的复杂查询
	 * @param exp
	 * @param cond
	 * @param queryFields
	 * @return String
	 */
	public String handleQueryBox(String exp,String cond,HashMap queryFields) {
		// 用于存放处理后的查询条件
		String condSql = "";
		try {
			// 解析表达式并获得sql语句
			FactorList parser = new FactorList(PubFunc.keyWord_reback(SafeCode.decode(exp)) ,PubFunc.keyWord_reback(SafeCode.decode(cond)), userView.getUserName(),queryFields);
			condSql += parser.getSingleTableSqlExpression("data");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return condSql;
	}
}