package com.hjsj.hrms.module.jobtitle.committee.transaction;

import com.hjsj.hrms.module.jobtitle.utils.JobtitleUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 获取查询语句_评委会
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class GetSqlForCommitteeTrans extends IBusiness {

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		
		try {
			StringBuilder sql =  new StringBuilder();//查询sql
			ArrayList<String> searchItems = new ArrayList<String>();//检索项目
			
			sql.append("select ");
			ArrayList fieldList = DataDictionary.getFieldList("w01",Constant.USED_FIELD_SET);
			for(int i=0; i<fieldList.size(); i++){
				FieldItem item = (FieldItem)fieldList.get(i);		
				String itemid = item.getItemid();//字段id
				if(i != fieldList.size()-1){
					sql.append(" "+itemid+",");
				}else {
					sql.append(" "+itemid+" ");
				}
			}
			sql.append(",w01.b0110 ");
			sql.append(" from w01 where W0109=1 ");
			
			String b0110 = this.userView.getUnitIdByBusi("9");//取得所属单位
			if(b0110.split("`")[0].length() > 2){//组织机构不为空：取本级，下级。为空：最高权限
    			JobtitleUtil jobtitleUtil = new JobtitleUtil(this.getFrameconn(), this.userView);// 工具类
    			sql.append(jobtitleUtil.getB0110Sql_upToDown(b0110));
    		}
			
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
