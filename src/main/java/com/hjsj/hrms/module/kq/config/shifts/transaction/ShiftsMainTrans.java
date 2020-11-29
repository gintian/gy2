package com.hjsj.hrms.module.kq.config.shifts.transaction;

import com.hjsj.hrms.module.kq.config.shifts.businessobject.ShiftsService;
import com.hjsj.hrms.module.kq.config.shifts.businessobject.impl.ShiftsServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 	班次交易类
 * @author haosl
 * 
 * 2018-9-25
 *
 */
public class ShiftsMainTrans extends IBusiness {
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 * 数据返回格式统一是json 格式
	 * 参数说明：
	 *type:
	 *	=pclist  获得班次信息列表
	 * 	=delete  删除班次
	 *  =validate  启用|停用班次
	 *  =adjust_seq  调整班次顺序
	 * 	=get_info  获得班次详细信息（编辑班次）
	 *  =save  保存班次信息
	 *  =getPriv 获得登陆用户的业务范围
	 */
	@Override
	public void execute() throws GeneralException {
		String subModuleId = (String) this.getFormHM().get("subModuleId");// 为空：初次进入页面 ；不为空：快速查询
		//输入查询
		if("shifts_list_subModuleId".equals(subModuleId)) {
			TableDataConfigCache catche = (TableDataConfigCache)this.userView.getHm().get(subModuleId);
			String type = (String)this.getFormHM().get("type");
			if("1".equals(type)) {// 1:输入查询
				StringBuilder querySql = new StringBuilder();
				ArrayList<String> valuesList = new ArrayList<String>();
				valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");// 输入的内容
				// 快速查询
				if (valuesList != null && valuesList.size() > 0) {
					querySql.append(" and (");
				}
				for (int i = 0; valuesList != null && i < valuesList.size(); i++) {
					String queryVal = valuesList.get(i);
					//queryVal = SafeCode.decode(queryVal);// 解码
					
					//替换特殊字符
					if (i != 0) {
						querySql.append("or ");
					}
					querySql.append("(name like '%" + queryVal+"%' or abbreviation like '%"+queryVal+"%')");
				}
				if (valuesList != null && valuesList.size() > 0) {
					querySql.append(" ) ");
				}
				catche.setQuerySql(querySql.toString());
			}else if("2".equals(type)){//方案查询
				StringBuilder querySql = new StringBuilder();
				HashMap queryFields = catche.getQueryFields();//haosl 20161014方案查询可以查询自定义指标
				String exp = (String) this.getFormHM().get("exp");
				exp = SafeCode.decode(exp);
				exp=PubFunc.keyWord_reback(exp);
				String cond = (String) this.getFormHM().get("cond");
				cond = SafeCode.decode(cond);
				cond = cond.replaceAll("＜", "<");
				cond = cond.replaceAll("＞", ">");
				if(cond.length()<1 || exp.length()<1){
					catche.setQuerySql(querySql.toString());
					return;
				}
				querySql.append(" and ");
				FactorList parser = new FactorList(exp,cond,userView.getUserName(),queryFields);//haosl 20161014方案查询可以查询自定义指标
				querySql.append(parser.getSingleTableSqlExpression("myGridData"));
				catche.setQuerySql(querySql.toString());
			}
			return;
		}
		String jsonStr = (String)this.formHM.get("jsonStr");
		//获得班次信息列表
		JSONObject jsonObj = JSONObject.fromObject(jsonStr);
		String type = jsonObj.getString("type");
		ShiftsService shiftsService = new ShiftsServiceImpl(this.userView,this.frameconn);
		String returnStr = "";
		if("pclist".equals(type)){
			this.getFormHM().put("tableConfig", shiftsService.getShiftsTableConfig());
		}
		//保存班次
		else if("save".equals(type)) {
			JSONObject jsonInfo = jsonObj.getJSONObject("info");
			returnStr = shiftsService.saveShift(jsonInfo);
		}
		//删除班次
		else if("delete".equals(type)) {
			String ids =  jsonObj.getString("ids");
			String[] idArr = ids.split(",");
			for(int i=0;i<idArr.length;i++) {
				idArr[i]=PubFunc.decrypt(idArr[i]);
			}
			if(idArr.length>0) {
				returnStr = shiftsService.delShit(idArr);
			}
		}
		//编辑前回显数据
		else if("get_info".equals(type)) {
			String classId =  jsonObj.getString("id");
			classId = PubFunc.decrypt(classId);
			String return_code = "success";
			String return_msg = "";
			JSONObject json = new JSONObject();
			try {
				LazyDynaBean classBean = shiftsService.getClassInfo(classId);
				json.put("return_data", JSONObject.fromObject(classBean));
			} catch (Exception e) {
				return_code="fail";
				return_msg=e.getMessage();
				throw GeneralExceptionHandler.Handle(e);
			}finally {
				json.put("return_code", return_code);
				json.put("return_msg", return_msg);
				returnStr =  json.toString();
			}
		}
		//启用|停用班次
		else if("validate".equals(type)) {
			String classId =  jsonObj.getString("id");
			classId = PubFunc.decrypt(classId);
			String validate = jsonObj.getString("validate");
			returnStr = shiftsService.editValidate(classId,validate);
			
		}
		//验证班次是否使用
		else if("checkValidate".equals(type)) {
			String classId =  jsonObj.getString("id");
			classId = PubFunc.decrypt(classId);
			returnStr = shiftsService.checkValidate(classId);
		}
		//调整班次顺序
		else if("adjust_seq".equals(type)) {
			String from_id = PubFunc.decrypt(jsonObj.getString("from_id"));
			String to_id = PubFunc.decrypt(jsonObj.getString("to_id"));
			returnStr = shiftsService.adjustClassSeq(from_id,to_id);
		}else if("getPriv".equals(type)) {
			returnStr = shiftsService.getPriveCode();
		}
		this.getFormHM().put("returnStr", returnStr);
		

	}
}
