package com.hjsj.hrms.module.jobtitle.reviewmeeting.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * <p>Title:GetSqlForChooseExpertTrans </p>
 * <p>Description: 打开选择执行评委界面</p>
 * <p>Company: hjsj</p> 
 * <p>create time: 2015-12-31</p>
 * @author liuy
 * @version 1.0
 */
@SuppressWarnings("serial")
public class GetSqlForChooseExpertTrans extends IBusiness{

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		try {
			StringBuilder sql =  new StringBuilder();//查询sql
			ArrayList<String> searchItems = new ArrayList<String>();//检索项目
			String committee_id = (String)this.getFormHM().get("committee_id");//评委会编号
			int typeCommittee = (Integer)this.getFormHM().get("typeCommittee"); //=1 评委会   =4 二级单位
			String w0301 = (String)this.getFormHM().get("w0301");//会议编号
			if(StringUtils.isNotEmpty(w0301))
				w0301 = PubFunc.decrypt(w0301);
			
			sql.append("select ");
			ArrayList fieldList = DataDictionary.getFieldList("w01",Constant.USED_FIELD_SET);
			for(int i=0; i<fieldList.size(); i++){
				FieldItem item = (FieldItem)fieldList.get(i);		
				String itemid = item.getItemid();//字段id
				if(i != fieldList.size()-1)
					sql.append(" W01."+itemid+",");
				else 
					sql.append(" W01."+itemid+" ");
			}
			
			sql.append(",w01.b0110 from zc_judgingpanel_experts zc");
			sql.append(" left join w01 on zc.W0101 = W01.W0101");
			sql.append(" where committee_id='"+ committee_id +"'");
			sql.append(" and w01.w0101 not in ");
			sql.append(" (select w0101 from zc_expert_user where w0301='"+ w0301 +"' and type="+typeCommittee+" and w0501='xxxxxx')");
			sql.append(" and ("+ Sql_switcher.today() +" between start_date and end_date or end_date is null)");
			sql.append(" and flag=1");

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
