package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.DemandCtrlParamXmlBo;
import com.hjsj.hrms.businessobject.hire.ParameterSetBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class EvaluationAl extends IBusiness {

	public void execute() throws GeneralException {
		ParameterSetBo parameterSetBo=new ParameterSetBo(this.getFrameconn());
		String z0301=(String)this.getFormHM().get("z0301");
		/**安全改造,判断是否要设置的z0301是否存在后台begin**/
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String sql = (String) this.userView.getHm().get("hire_sql");	
			int index = sql.indexOf("order by");
			if(index!=-1){
				sql = sql.substring(0, index);
			}	
			sql = sql+" and z0301='"+z0301+"'";
			this.frowset = dao.search(sql);
			if(!this.frowset.next()){
				throw new GeneralException(ResourceFactory.getProperty("label.hireemploye.no.contorl"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		/**安全改造,判断是否要设置的z0301是否存在后台end**/
		DemandCtrlParamXmlBo DemandCtrlParamXmlBo = new DemandCtrlParamXmlBo(this.getFrameconn(),z0301);
		String type =DemandCtrlParamXmlBo.getNodeAttributeValue("/content/template", "type");
		String id = DemandCtrlParamXmlBo.getNodeAttributeValue("/content/template", "id");
		if("".equals(type))
			 type="1";	 
		ArrayList testTemplateList=parameterSetBo.getPerTemplateList();	
		this.getFormHM().put("testTemplateList",testTemplateList);
		this.getFormHM().put("testid",id);
		this.getFormHM().put("valid",type);
		
		
		
	}
	
}
