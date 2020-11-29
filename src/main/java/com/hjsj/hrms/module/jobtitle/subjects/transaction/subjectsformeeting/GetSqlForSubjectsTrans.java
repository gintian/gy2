package com.hjsj.hrms.module.jobtitle.subjects.transaction.subjectsformeeting;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 获取查询语句_学科组
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class GetSqlForSubjectsTrans extends IBusiness {

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		
		String w0301 = (String)this.getFormHM().get("w0301");//评审会议编号
		w0301 = PubFunc.decrypt(w0301);
		String group_id = (String)this.getFormHM().get("group_id");//组编号
		group_id = PubFunc.decrypt(group_id);
		
		try {
			StringBuilder sql =  new StringBuilder();//查询sql
			ArrayList<String> searchItems = new ArrayList<String>();//检索项目
			
			sql.append("select ");
			ArrayList fieldList = DataDictionary.getFieldList("w01",Constant.USED_FIELD_SET);
			for(int i=0; i<fieldList.size(); i++){
				FieldItem item = (FieldItem)fieldList.get(i);		
				String itemid = item.getItemid();//字段id
				if(i != fieldList.size()-1){
					sql.append(" w01."+itemid+",");
				}else {
					sql.append(" w01."+itemid+" ");
				}
			}
			sql.append(",w01.b0110 ");
			sql.append("from zc_subjectgroup_experts zc ");
			sql.append("right join w01 ");//left--》应该以专家表为基表 改为右外连接
			sql.append("on w01.w0101=zc.expertid ");
			sql.append("where group_id='"+group_id+"' and expertid not in(select w0101 from zc_expert_user where type=2 and w0301='"+w0301+"' and group_id='"+group_id+"' )");
			sql.append(" and ("+ Sql_switcher.today() +" between start_date and end_date or end_date is null)");
			sql.append("and flag='1'");
			
			searchItems.add(PubFunc.encrypt("w0103"));
			searchItems.add(PubFunc.encrypt("w0105"));
			searchItems.add(PubFunc.encrypt("w0107"));
			this.getFormHM().put("sql", PubFunc.encrypt(sql.toString()));
			this.getFormHM().put("orderBy", PubFunc.encrypt("order by w0101"));
			this.getFormHM().put("searchItems", searchItems);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
