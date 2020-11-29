package com.hjsj.hrms.transaction.sys.options.param.operation;

import com.hjsj.hrms.businessobject.sys.options.param.SubsysOperation;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 *<p>Title:SortInfoTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 15, 2008:10:45:25 AM</p> 
 *@author huaitao
 *修改：郭峰2013-7-15
 *@version 1.0
 */
public class SortInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try {
			SubsysOperation so = new SubsysOperation(this.frameconn,this.userView);
			String id = (String)this.getFormHM().get("operationid");
			ArrayList sortlist = so.getView_tag(id);
			ArrayList rolelist = new ArrayList();
			for(int i=0;i<sortlist.size();i++){//循环每一个业务分类
				LazyDynaBean bean = new LazyDynaBean();
				String sortname = sortlist.get(i).toString();
				String select_id = so.getView_value(id,sortname);
				String[] select_ids = select_id.split(",");
				String valid = so.getView_value(id,"valid",sortname);//得到单位性质的选中状态
				ContentDAO dao = new ContentDAO(this.frameconn);
				StringBuffer sql = new StringBuffer();
				sql.append("select TabId,Name from template_table  ");
				if(select_id!=null&&select_id.trim().length()>0){
					sql.append(" where TabId in (");
					for(int j=0;j<select_ids.length;j++){
						sql.append("'"+select_ids[j]+"',");
					}
					sql.setLength(sql.length()-1);
					sql.append(")");
				}else{
				    sql.append(" where 1=2");
				}
				sql.append(" order by tabid");
				StringBuffer sortvalue = new StringBuffer();
				this.frowset = dao.search(sql.toString());
				while(this.frowset.next()){
					sortvalue.append(this.frowset.getString(1)+":"+this.frowset.getString(2)+",");
				}
				if(sortvalue.length()>0)
					sortvalue = sortvalue.delete(sortvalue.length()-1,sortvalue.length());
				bean.set("sortname",sortname);
				bean.set("select_id",select_id+",");
				bean.set("sortvalue",sortvalue.toString());
				LazyDynaBean bean1 = new LazyDynaBean();
				ArrayList list = new ArrayList();
				for(int x=1;x<4;x++)//获得单位性质  把军队和其它去掉
				{
					//vo = new CommonData();
					bean1 = new LazyDynaBean();
					bean1.set("name",ResourceFactory.getProperty("sys.options.param.descript"+x));
					bean1.set("value",sortname+"-"+x);
					if(valid!=null)
					if(valid.indexOf(x+"")!=-1)
						 bean1.set("check","checked");
					else
						 bean1.set("check","");
					
					list.add(bean1);
				}
				bean.set("type_list",list);
				rolelist.add(bean);
				
			}
			this.getFormHM().put("rolelist",rolelist);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
