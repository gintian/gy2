package com.hjsj.hrms.transaction.hire.interviewEvaluating.interviewExamine;

import com.hjsj.hrms.businessobject.hire.EmployActualize;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class ValidateOverNumTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		ArrayList list=(ArrayList)this.getFormHM().get("select");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer info=new StringBuffer("");
		try
		{
			EmployActualize employActualize=new EmployActualize(this.getFrameconn());
			String dbname=employActualize.getZP_DB_NAME();
			HashMap map=new HashMap();
			String resume_state_field="";
			ParameterXMLBo bo2=new ParameterXMLBo(this.frameconn,"1");
			HashMap xmlmap=bo2.getAttributeValues();
			if(xmlmap!=null&&xmlmap.get("resume_state")!=null)
				resume_state_field=(String)xmlmap.get("resume_state");//简历状态标识
			for(int i=0;i<list.size();i++)
			{
				
				//  z0301/z0315/z0311/a_state
				String temp=(String)list.get(i);
				String[] temps=temp.split("/");
				String z0301=temps[0];
				int z0315=Integer.parseInt(temps[1]);
				String z0311=temps[2];
				String a_state=temps[3];
				
				if(map.get(z0301)==null)
				{
					
					String sql="select count("+dbname+"a01.a0100) a from zp_pos_tache zp,"+dbname+"a01"
							 +" where zp.a0100="+dbname+"a01.a0100 and zp.zp_pos_id='"+z0301+"' and ( "+dbname+"a01."+resume_state_field+"='41' or "+dbname+"a01."+resume_state_field+"='43' )";
					this.frowset=dao.search(sql);
					int count=0;
					if(this.frowset.next())
					{
						count=this.frowset.getInt("a");
					}
					for(int j=0;j<list.size();j++)
					{
						String atemp=(String)list.get(j);
						String[] atemps=atemp.split("/");
						String az0301=atemps[0];
						int az0315=Integer.parseInt(atemps[1]);
						String aa0100=atemps[2];
						String aa_state=atemps[3];
						
						if(az0301.equals(z0301))
							count++;
					}
					if(count>z0315)  //如果 拟录用的人数 超过了该职位审批的人数，则报警。
					{
						String desc=AdminCode.getCodeName("@K",z0311); //职位描述
						info.append("#岗位： "+desc+" 录用人数超过了用工需求(审核人数:"+z0315+")! ");
					}
					map.put(z0301,"1");
				}
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(info.length()>2)
			info.append("#是否继续录用?");
		this.getFormHM().put("info",info.toString());
		

	}

}
