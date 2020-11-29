package com.hjsj.hrms.transaction.performance.objectiveManage.setUnderlingObjective;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class UnderlingObjectivePlanTreeTrans extends IBusiness{


	public void execute() throws GeneralException {
		try
		{
			if(this.userView.getA0100()==null|| "".equals(this.userView.getA0100()))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("employ.no.use.model")));
			//非在职人员不允许使用改功能
			if(!"USR".equalsIgnoreCase(userView.getDbname())) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("notUsr.no.use.func")));
			}
			String fieldItem = getPS_SUPERIOR_value();
			if(fieldItem.length()>0){
				DbWizard dbw = new DbWizard(this.frameconn);
				if(!dbw.isExistField("K01", fieldItem, false)){
					throw GeneralExceptionHandler.Handle(new Exception("直接上级岗位指标"+fieldItem+"已被删除，请重新设置！"));
				}
			}

			
			String a0100 = this.userView.getA0100();
			String posid=this.userView.getUserPosId();
			TreeItemView treeItem=new TreeItemView();
			treeItem.setName("root");
			treeItem.setRootdesc("root");
			treeItem.setTitle("root");
			treeItem.setIcon("/images/add_all.gif");	
			treeItem.setTarget("il_body");
			String rootdesc= "bjpt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))?"KPI表格":ResourceFactory.getProperty("org.performance.card");
				
		    treeItem.setRootdesc(rootdesc);
			treeItem.setText(rootdesc); 
		    treeItem.setLoadChieldAction("/servlet/performance/UnderlingObjectiveServlet?year=-1&posid="+posid+"&a0100="+a0100);
		    treeItem.setAction("javascript:void(0)");
		    treeItem.setTarget("mil_body");
		    this.getFormHM().put("tree",treeItem.toJS());
		    
		    HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		    String returnflag=(String)hm.get("returnflag");
		    this.getFormHM().put("returnflag",returnflag);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
		
		
		/**
		 * 获得 汇报关系中 直接上级指标
		 * @return
		 */
		public String getPS_SUPERIOR_value()
		{
			String fieldItem="";
			RecordVo vo=ConstantParamter.getRealConstantVo("PS_SUPERIOR",this.getFrameconn());
	        if(vo==null)
	        	return fieldItem;
	        String param=vo.getString("str_value");
	        if(param==null|| "".equals(param)|| "#".equals(param))
	        	return fieldItem;
			fieldItem=param;
			return fieldItem;
		}

}
