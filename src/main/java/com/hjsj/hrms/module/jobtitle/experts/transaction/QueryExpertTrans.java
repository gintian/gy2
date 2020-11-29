package com.hjsj.hrms.module.jobtitle.experts.transaction;

import com.hjsj.hrms.module.jobtitle.experts.businessobject.ExpertsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * 
* <p>Title:QueryExpertTrans </p>
* <p>Description: 普通或方案查询</p>
* <p>Company: hjsj</p> 
* @author hej
* @date Nov 27, 2015 9:19:50 AM
 */
public class QueryExpertTrans extends IBusiness{
	@Override
    public void execute() throws GeneralException {
		
		String type = (String)this.getFormHM().get("type");
		
		String subModuleId = (String)this.getFormHM().get("subModuleId");
		TableDataConfigCache cache = (TableDataConfigCache)this.userView.getHm().get(subModuleId);
		if("1".equals(type) || "2".equals(type)){//快速查询
			
				
				StringBuffer condsql = new StringBuffer("");
				if("1".equals(type)){//查询栏查询
					List values = (ArrayList) this.getFormHM().get("inputValues");
					if(values==null || values.isEmpty()){
						cache.setQuerySql("");
						return;
					}
					//condsql.append("(");
					for(int i=0;i<values.size();i++){
						String value =SafeCode.decode(values.get(i).toString());
						if (i == 0) {
							condsql.append(" and (");
						}else {
							condsql.append(" or ");
						}
						FieldItem w0107 = DataDictionary.getFieldItem("w0107");
						if(w0107!=null&&!"".equals(w0107))
						{
							condsql.append("( w0107 like '%"+value+"%' or ");
						}
						FieldItem w0103 = DataDictionary.getFieldItem("w0103");
						if(w0103!=null&&!"".equals(w0103))
						{
							condsql.append(" w0103 like '%"+value+"%' or ");
						}
						FieldItem w0105 = DataDictionary.getFieldItem("w0105");
						if(w0105!=null&&!"".equals(w0105))
						{
							condsql.append(" w0105 like '%"+value+"%') ");
						}
						
						if(i == values.size()-1){
							condsql.append(" ) ");
							
						}
					}
				}else if("2".equals(type)){//方案查询
					HashMap queryFields = cache.getQueryFields();//haosl 20161014方案查询可以查询自定义指标
					condsql.append(" and ");
					String exp = (String) this.getFormHM().get("exp");
					exp = SafeCode.decode(exp);
					exp=PubFunc.keyWord_reback(exp);
					String cond = (String) this.getFormHM().get("cond");
					cond = SafeCode.decode(cond);
					cond = cond.replaceAll("＜", "<");
					cond = cond.replaceAll("＞", ">");
					if(cond.length()<1 || exp.length()<1){
						cache.setQuerySql("");
						return;
					}
					FactorList parser = new FactorList(exp,cond,userView.getUserName(),queryFields);//haosl 20161014方案查询可以查询自定义指标
					condsql.append(parser.getSingleTableSqlExpression("myGridData"));
				}
				cache.setQuerySql(condsql.toString());
		} else if("3".equals(type)){//获取全部专家
			ArrayList<String> experts = new ArrayList<String>();
			
			ContentDAO dao = null;
			RowSet rs = null;
			try{
				ExpertsBo bo = new ExpertsBo(this.frameconn,this.userView);
				StringBuilder sql = new StringBuilder("select nbase,a0100 from ");
				sql.append(bo.getSelectSql());

				dao = new ContentDAO(this.frameconn);
				rs = dao.search(sql.toString());
				while(rs.next()){
					String nbase = rs.getString("nbase");
					String a0100 = rs.getString("a0100");
					experts.add(PubFunc.encrypt(nbase+a0100));
				}
			} catch(Exception e){
				e.printStackTrace();
			} finally {
				PubFunc.closeDbObj(rs);
			}
			
			formHM.put("experts", experts);
		}
	}

}
