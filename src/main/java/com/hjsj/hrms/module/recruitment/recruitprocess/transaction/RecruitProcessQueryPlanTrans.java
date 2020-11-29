package com.hjsj.hrms.module.recruitment.recruitprocess.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.List;

/****
 * 招聘流程状态中人员列表查询分析功能交易类
 * <p>Title: RecruitProcessQueryPlanTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-7-30 下午01:24:54</p>
 * @author xiexd
 * @version 1.0
 */
public class RecruitProcessQueryPlanTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		//如果有type参数说明是查询组件进入的
		String type = (String)this.getFormHM().get("type");
		String subModuleId = (String)this.getFormHM().get("subModuleId");
		TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(subModuleId);
		StringBuffer condsql = new StringBuffer();
		if(type!=null){
			//快速查询
			if("1".equals(type)){
				 List values = (ArrayList) this.getFormHM().get("inputValues");
				 if(values==null || values.isEmpty()){
					 //刷新userView中的sql参数
					 tableCache.setQuerySql(condsql.toString());
					 userView.getHm().put(subModuleId, tableCache);
					 return;
				 }
				 for(int i=0;i<values.size();i++){
						condsql.append(" and (");
						String value = SafeCode.decode(values.get(i).toString());		
						value = value.toLowerCase();
						RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
						String nbase="";  //应聘人员库
						if(vo!=null)
							nbase=vo.getString("str_value"); 
						condsql.append(" lower(myGridData.A0101) like '%"+value+"%' ");
			            FieldItem emailItem = DataDictionary.getFieldItem(this.getEmailItemId());
			            if(emailItem!=null&&!"".equals(emailItem))
						{
			            	String email = emailItem.getItemid();
			            	condsql.append(" or lower(myGridData."+email+") like '%"+value+"%'  ");
						}
						FieldItem A0435 = DataDictionary.getFieldItem("A0435");
			            if(A0435!=null&&!"".equals(A0435))
						{
			            	condsql.append(" or lower(myGridData.A0435) like '%"+value+"%' ");
						}
			            condsql.append(" )");
				 }
				 tableCache.setQuerySql(condsql.toString());
			}else if("2".equals(type)){//方案查询
				 condsql.append(" and ");
				 String exp = SafeCode.decode(this.getFormHM().get("exp").toString());
				 exp = PubFunc.keyWord_reback(exp);
				 String cond = PubFunc.keyWord_reback(SafeCode.decode(this.getFormHM().get("cond").toString()));
		         if(cond.length()<1 || exp.length()<1){
		        	//刷新userView中的sql参数
		        	tableCache.setQuerySql("");
					userView.getHm().put(subModuleId, tableCache);
		        	return;
		         }
		         FactorList parser = new FactorList(exp ,cond, userView.getUserName());
		         condsql.append(parser.getSingleTableSqlExpression("myGridData"));
			}
			tableCache.setQuerySql(condsql.toString());
			userView.getHm().put(subModuleId, tableCache);
			return;
		}
	}
	 /**
	 * 获取邮件地址指标
	* @Title:getEmailItemId
	* @Description：
	* @author xiexd
	* @return
	 */
	public String getEmailItemId()
	{
		String emailId = "";
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			StringBuffer sql = new StringBuffer("select Str_Value from constant where constant='SS_EMAIL'");
			RowSet rs = dao.search(sql.toString());
			if(rs.next())
			{
				emailId = rs.getString("Str_Value");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return emailId;
	}
}
