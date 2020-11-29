package com.hjsj.hrms.utils.components.defineformula.transaction.standard;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/**
 * 项目名称 ：ehr
 * 类名称：SelectStandardTrans
 * 类描述：标准表数据集合
 * 创建人： lis
 * 创建时间：2016-2-1
 */
public class SelectStandardTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = this.getFormHM();
		String itemname = (String)hm.get("itemname");
		itemname=itemname!=null&&itemname.trim().length()>0?itemname:"";
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		ArrayList list = new ArrayList();
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select id,name from gz_stand where item='");
		sqlstr.append(itemname);
		sqlstr.append("' and id in(select id from gz_stand_history");
		sqlstr.append(" where pkg_id in(select pkg_id from gz_stand_pkg where status='1')");
		String unitid = "XXXX";
		StringBuffer tt = new StringBuffer();
		if(this.userView.isSuper_admin())
		{
			unitid="";
			tt.append(" or 1=1 ");
		}
		else
		{
			if(this.userView.getUnit_id()!=null&&this.userView.getUnit_id().trim().length()>2)
			{
				if(this.userView.getUnit_id().length()==3)
				{
					unitid="";
					tt.append(" or 1=1 ");
				}
				else
				{
			    	unitid=this.userView.getUnit_id();
			    	String[] unit_arr = unitid.split("`");
			    	for(int i=0;i<unit_arr.length;i++)
			    	{
			    		if(unit_arr[i]==null|| "".equals(unit_arr[i]))
			    			continue;
			    		tt.append(" or b0110 like '%,"+unit_arr[i].substring(2)+"%' ");
			    	}
				}
			}
			else{
				if(this.userView.getManagePrivCode()!=null&&this.userView.getManagePrivCode().trim().length()>0)
				{
					if(this.userView.getManagePrivCodeValue()==null|| "".equals(this.userView.getManagePrivCodeValue().trim()))
					{
						unitid="";
						tt.append(" or 1=1 ");
					}
					else{
				    	unitid=this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
				    	tt.append(" or b0110 like '%,"+this.userView.getManagePrivCodeValue()+"%'");
					}
				}
				else//没有范围
				{
					
				}
			}
		}
		if(tt.toString().length()>0)
		{
			if(this.userView.isSuper_admin()|| "".equals(unitid))
			{
				
			}else
			{
				sqlstr.append(" and (");
				sqlstr.append("("+tt.toString().substring(3)+")");
				sqlstr.append(" or UPPER(b0110)='UN' or "+Sql_switcher.isnull("b0110", "''")+"=''");
				sqlstr.append(")");
			}
		}
		if("XXXX".equals(unitid))
		{
			sqlstr.append(" and "+Sql_switcher.isnull("b0110", "''")+"=''");
		}
		sqlstr.append(") ORDER BY id desc");
		ArrayList dylist = null;
		try {
			CommonData dataobj1 = new CommonData("",ResourceFactory.getProperty("label.select")+"...");//请选择
			list.add(dataobj1);
			dylist = dao.searchDynaList(sqlstr.toString());
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				String id = dynabean.get("id").toString();
				String name = dynabean.get("name").toString();
				CommonData dataobj = new CommonData(id,name);
				list.add(dataobj);
			}
		} catch(GeneralException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		hm.put("standardlist",list);
	}

}
