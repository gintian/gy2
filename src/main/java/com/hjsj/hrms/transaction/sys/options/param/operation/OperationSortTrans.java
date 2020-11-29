package com.hjsj.hrms.transaction.sys.options.param.operation;

import com.hjsj.hrms.businessobject.sys.options.param.SubsysOperation;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:OperationSortTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 12, 2008:5:41:18 PM</p> 
 *@author huaitao  
 *修改：郭峰  2013-7-15
 *@version 1.0
 */
public class OperationSortTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		StringBuffer sql = new StringBuffer();
		sql.append("select id,name from t_hr_subsys where id in ('34','37','38','39','40','55','56','57' ");//56:机构管理 57：岗位管理
		if(SystemConfig.getPropertyValue("unit_property")!=null)
		{
			if("psorgans_jcg".equalsIgnoreCase(SystemConfig.getPropertyValue("unit_property"))) //警衔管理、检察官管理
			{
				sql.append(",'51','52'");
			}
			else if("psorgans_fg".equalsIgnoreCase(SystemConfig.getPropertyValue("unit_property"))) //警衔管理、法官等级
			{
				sql.append(",'51','53'");
			}
			else if("psorgans_gx".equalsIgnoreCase(SystemConfig.getPropertyValue("unit_property"))) //警衔管理、关衔管理
			{
				sql.append(",'51','54'");
			}
		}
		
		//zxj 20161108 增加考勤模板分类
		//zxj 20170612 71封版正式启用考勤管理业务分类
		//if("1".equals(SystemConfig.getPropertyValue("show_kq_template_sort")))
		    sql.append(",'30'");
		    
		//zxj 20180515 增加证照管理业务分类
		sql.append(",'61'");
		
		sql.append(" ) order by id");
		ContentDAO dao = new ContentDAO(this.frameconn);
		SubsysOperation so = new SubsysOperation(this.frameconn,this.userView);
		ArrayList rolelist = new ArrayList();
		try {
			this.frowset = dao.search(sql.toString());
			while(this.frowset.next()){
				LazyDynaBean bean = new LazyDynaBean();
				String id = this.frowset.getString("id");
				bean.set("id",id);
				bean.set("name",this.frowset.getString("name"));
				ArrayList list = so.getView_tag(id);//得到所有业务分类的名字
				if(list==null||list.size()<=0)//如果该业务模板下没有业务分类
					bean.set("flag","0");
				else{
					bean.set("flag","1");
					HashMap map = so.getMap();//是否启用
					String check = (String)map.get(id);
					bean.set("check",check);
				}
				//ArrayList sortlist = so.getView_tag(id);//郭峰注释 没有必要重查一遍
				String text = "";
				for(int i=0;i<list.size();i++){
					String sortname = list.get(i).toString();
					String select_id = so.getView_value(id,sortname);
					String[] select_ids = select_id.split(",");
					StringBuffer sortvalue = new StringBuffer();//存储模板Id和模板名字。
					sql.delete(0,sql.length());
					sql.append("select TabId,Name from template_table  ");
					if(select_id!=null&&select_id.trim().length()>0){
						sql.append(" where TabId in (");
						for(int j=0;j<select_ids.length;j++){
							sql.append("'"+select_ids[j]+"',");
						}
						sql.setLength(sql.length()-1);
						sql.append(")");
					}else{
					    sql.append(" where 1=2 ");
					}
					sql.append(" order by tabid");
					RowSet rs = dao.search(sql.toString());
					while(rs.next()){
						sortvalue.append(rs.getString(1)+":"+rs.getString(2)+",");
					}
					if(sortvalue.length()>0)
						sortvalue = sortvalue.delete(sortvalue.length()-1,sortvalue.length());//删除最后一个字符
					text += "<STRONG>"+sortname+"</STRONG>：<br>&nbsp;&nbsp;&nbsp;"+ sortvalue +"<br>";
				}
				if(text.length()>0)
					text = text.substring(0,text.length()-4);//把最后一个<br>删除。
				bean.set("text",text);
				rolelist.add(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.getFormHM().put("rolelist",rolelist);
		this.getFormHM().put("errmes","");
	}

}
