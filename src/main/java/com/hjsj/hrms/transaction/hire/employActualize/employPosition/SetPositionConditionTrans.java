package com.hjsj.hrms.transaction.hire.employActualize.employPosition;

import com.hjsj.hrms.businessobject.hire.DemandCtrlParamXmlBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;



public class SetPositionConditionTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String type=(String)hm.get("type");
			/**招聘岗位,岗位一系列的参数都进行了加密,解密回来**/
			String z0301=PubFunc.decrypt((String)hm.get("z0301"));
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String sql="";
			PositionDemand bo=new PositionDemand(this.getFrameconn());
			if("0".equals(type))
			{
		    	ArrayList posConditionList=(ArrayList)this.getFormHM().get("posConditionList");
		    
			    ArrayList list=bo.getParamConditionList(posConditionList);
		    	DemandCtrlParamXmlBo xmlBo=new DemandCtrlParamXmlBo(this.getFrameconn(),z0301);
		    	HashMap map=new HashMap();
		    	map.put("simple",list);
		    	xmlBo.updateNode("simple",map,z0301);
		    	String upValue=(String)this.getFormHM().get("upValue");  //子集代码型 以上
		    	String vague=(String)this.getFormHM().get("vague");  //1 ：模糊查询 
			    sql=bo.getSqlByCondition(posConditionList,vo.getString("str_value"),upValue,vague);
			}
			else
			{
				String templateid=(String)hm.get("templateid");
				sql=bo.getComplexTemplateSQL(templateid, vo.getString("str_value"), this.userView);
			}
			String sql_str="update zp_pos_tache set resume_flag='13'  where zp_pos_id='"+z0301+"' and a0100 not in ("+sql+")";
			
			dao.update(sql_str);
			
			
			ParameterXMLBo xmlBo2=new ParameterXMLBo(this.getFrameconn());
			HashMap map2=xmlBo2.getAttributeValues();
			String resume_state="";
			if(map2.get("resume_state")!=null)
				resume_state=(String)map2.get("resume_state");
			if("".equals(resume_state))
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置简历状态指标！"));
			
			
			sql_str="update "+vo.getString("str_value")+"A01 set "+resume_state+"='13' where a0100 in (select a0100 from zp_pos_tache where zp_pos_id='"+z0301+"') and a0100 not in ("+sql+")";
			dao.update(sql_str);
			//dao.delete(sql_str,new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
