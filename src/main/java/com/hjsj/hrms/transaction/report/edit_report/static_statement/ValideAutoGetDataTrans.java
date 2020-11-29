package com.hjsj.hrms.transaction.report.edit_report.static_statement;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
public class ValideAutoGetDataTrans extends IBusiness {

	public void execute() throws GeneralException {
	//权限范围下的统计口径 
	//用户根据自己的操作单位是否与统计口径的所属机构相匹配来展现范围内的可选口径。
	//如果用户没有定义操作单位则按管理范围来匹配。
	ContentDAO dao=new ContentDAO(this.getFrameconn());
	ArrayList list = new ArrayList();
	String flag ="";		//该参数可以控制按操作单位或管理范围进行展现，1为操作单位，0为管理范围  xgq20101108
	StringBuffer  scopeidstr = new StringBuffer();
	try {
		this.frowset = dao.search("select * from tscope ");
		while(this.frowset.next()){
			list.add(this.frowset.getString("scopeid"));
	
		}
		for(int a=0;a<list.size();a++){
			String scopeid2 = (String)list.get(a);
			StringBuffer str = new StringBuffer(" select * from tscope where scopeid ="+scopeid2+" ");
			String temps="";
			
			if (!userView.isSuper_admin())
			{
				String operOrg = userView.getUnit_id();// 操作单位
				StringBuffer tempstr = new StringBuffer();
				if (operOrg.length() > 2)
				{
					String[] temp = operOrg.split("`");
					for (int i = 0; i < temp.length; i++)
					{
						if ("UN".equalsIgnoreCase(temp[i].substring(0, 2))|| "UM".equalsIgnoreCase(temp[i].substring(0, 2))){
							tempstr.append(" or  owner_unit like 'UM" + temp[i].substring(2) + "%'");
							tempstr.append(" or  owner_unit like 'UN" + temp[i].substring(2) + "%'");
						}
					}
					if(tempstr.length()>3){
						temps+=tempstr.toString().substring(3);
					}
					flag="1";
				} else
				{	//走管理范围
					
					String code = "-1";
					if (userView.getManagePrivCodeValue() != null && userView.getManagePrivCodeValue().length() > 0)// 管理范围
					{
						code = userView.getManagePrivCodeValue();
						if (code!=null)
							{
							if(code.indexOf("UN")!=-1||code.indexOf("UM")!=-1){
								tempstr.append(" or  owner_unit like 'UM" + code.substring(2) + "%'");
								tempstr.append(" or  owner_unit like 'UN" + code.substring(2) + "%'");
							}else{
								tempstr.append(" or  owner_unit like 'UN" + code + "%'");
								tempstr.append(" or  owner_unit like 'UM" + code + "%'");
							}
							}else{
								tempstr.append("and  1=2");
							}
							}else{
								tempstr.append("and  1=2");
							}
					if(tempstr.length()>3){
						temps+=tempstr.toString().substring(3);
					}
					flag="0";
					}
			
			if(temps.length()>0){
				str.append(" and ("+temps+")");
			}
			this.frowset = dao.search(str.toString());
			if(this.frowset.next()){
				scopeidstr.append(","+scopeid2);
			}
			}else{
				scopeidstr.append(","+scopeid2);
			}
		}
	
	//所属机构
	String sql = "";
	if(scopeidstr.toString().length()>0){
		sql = "select * from tscope  where scopeid in("+scopeidstr.substring(1)+") order by displayid";
		this.getFormHM().put("info", "success");
		this.getFormHM().put("flag", flag);
	}else{
		sql = "select * from tscope where 1=2 order by displayid";
		this.getFormHM().put("info", "fail");
		this.getFormHM().put("flag", flag);
		//throw GeneralExceptionHandler.Handle(new GeneralException("","权限范围下的统计口径不存在!","",""));
	}
		
	//统计口径
	} catch (SQLException e) {
		e.printStackTrace();
	}
	}

}
