package com.hjsj.hrms.module.org.virtualorg.trans;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.org.virtualorg.bo.VirturalRoleTransBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchViewerVirtualMemberTrans extends IBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(){
		try {
			String condSql = " ";
			TableDataConfigCache cache = (TableDataConfigCache)this.userView.getHm().get("virtual_members_00002");
			VirturalRoleTransBo bo=new VirturalRoleTransBo(this.frameconn,this.userView);
			String sql = cache.getTableSql();
			StringBuilder newsql = new StringBuilder();
			ArrayList<String> valuesList = new ArrayList<String>();
			valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");
			String flag=(String)this.getFormHM().get("checkflag");
			String code=(String)this.getFormHM().get("code");
			if(code!=null&&flag!=null){
				code=PubFunc.decrypt(code.split("=")[1]);
				code = code.split("=")[1];
				if("1".equals(flag))
				 cache.setTableSql(bo.getVirturalColumsql(code, true));
				else
				 cache.setTableSql(bo.getVirturalColumsql(code, false));	
			}
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name","1");
			if(valuesList!=null&&valuesList.size() > 0){
				for(int i = 0;i<valuesList.size();i++){
					String queryVal = valuesList.get(i);
					queryVal = SafeCode.decode(queryVal);
					if(i == 0){
						newsql.append(" and (");
					}else{
						newsql.append(" or ");
					}
					if(onlyname!=null&&!"".equals(onlyname)&&!"A0101".equalsIgnoreCase(onlyname))
						newsql.append(" A0101 LIKE '%"+queryVal+"%' OR onlyname LIKE '%"+queryVal+"%' ");
					else
						newsql.append(" A0101 LIKE '%"+queryVal+"%' ");
				}
				if(valuesList.size() > 0){
					newsql.append(" ) ");
					condSql += newsql.toString();
				}
			}
			if(flag==null&&code==null)
				cache.setQuerySql(condSql.toString());
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}

}
