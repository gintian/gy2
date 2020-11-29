package com.hjsj.hrms.utils.components.definetempvar.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.definetempvar.businessobject.DefineTempVarBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 项目名称 ：ehr7.x
 * 类名称：DelVariablesTrans
 * 类描述：删除临时变量
 * 创建人： lis
 * 创建时间：2015-10-31
 */
public class DelVariablesTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.frameconn);
		String nids = (String)hm.get("nids");//id串
		String cstate = (String)hm.get("cstate");//薪资类别id
		cstate = cstate!=null&&cstate.length()>0?cstate:"";
		String type = (String)hm.get("type");
		type = type!=null&&type.length()>0?type:"";
		if(!"3".equals(type)){
			cstate = PubFunc.decrypt(SafeCode.decode(cstate));
		}
		nids=nids!=null&&nids.trim().length()>0?nids:"";
		
		ArrayList idsList = new ArrayList();
		RowSet rs = null;
		try {
			if(StringUtils.isNotBlank(nids)){
				String[] arr = nids.split(",");
				StringBuffer context = new StringBuffer();
				DefineTempVarBo bo = new DefineTempVarBo(this.frameconn,this.userView);
				String name="";
				if("1".equals(type)){
					name = bo.getSalaryName(cstate);
				}else if("3".equals(type)){
					name = bo.getTempName(cstate);//获得人事异动模版名称
				}
				StringBuffer whl=new StringBuffer("");
				for(int i=0;i<arr.length;i++){
					if(StringUtils.isBlank(arr[i]))
						continue;
					whl.append(","+arr[i]);
					ArrayList list = new ArrayList();
					list.add(arr[i]);
					idsList.add(list);
				}
				
				if(StringUtils.isNotBlank(whl.toString())){
					rs = dao.search("select cname,chz from  midvariable where nid in ("+whl.substring(1)+")");
					while(rs.next())
					{
						if(context.length()==0){
							String delete = ResourceFactory.getProperty("menu.gz.delete");//删除
							String var = ResourceFactory.getProperty("label.gz.variable");//临时变量
							context.append(delete+":"+name+"("+cstate+")"+delete+var+":<br>");
							context.append(rs.getString("chz")+"("+rs.getString("cname")+")");
						}else
							context.append(","+rs.getString("chz")+"("+rs.getString("cname")+")");
					}
					this.getFormHM().put("@eventlog", context.toString());
					
					String sqlstr = "delete from midvariable where nid=?";						
					dao.batchUpdate(sqlstr,idsList);	
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
	}
}
