package com.hjsj.hrms.transaction.hire.employActualize.employResume;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ValidateIsSetStateTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ArrayList recordIDs=(ArrayList)this.getFormHM().get("recordIDs");
			String    state=(String)this.getFormHM().get("state");
			String a0100=(String)this.getFormHM().get("a0100");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname=vo.getString("str_value");
			
			ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn());
			HashMap map=xmlBo.getAttributeValues();
			String resume_state="";
			if(map.get("resume_state")!=null)
				resume_state=(String)map.get("resume_state");
			if(resume_state.trim().length()<=0){
				throw new GeneralException("简历状态指标未设置,请到配置参数中设置简历状态指标!");
			}
			StringBuffer whl=new StringBuffer("");
			StringBuffer a0100s=new StringBuffer("");
			for(Iterator t=recordIDs.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
				String[] temps=temp.split("/");
				if(temps[0]!=null && temps[0].length() > 0 && !temps[0].matches("^\\d+$"))
                    temps[0] = PubFunc.decrypt(temps[0]);
                
                if(temps[1]!=null && temps[1].length() > 0 && !temps[1].matches("^\\d+$"))
                    temps[1] = PubFunc.decrypt(temps[1]);
                
				whl.append(" or ( a0100='"+temps[0]+"' and zp_pos_id='"+temps[1]+"' ) ");
				a0100s.append(",'"+temps[0]+"'");
			}
			
			String flag="0";   
			StringBuffer info=new StringBuffer("");
			StringBuffer sql=new StringBuffer("select distinct "+dbname+"a01.a0100,"+dbname+"a01.a0101 from "+dbname+"a01,");
			sql.append(" (select * from zp_pos_tache where  "+whl.substring(3)+"  ) zpt ");
		//	sql.append(" zp_pos_tache zpt");
			sql.append(" where "+dbname+"a01.a0100=zpt.a0100  and "+dbname+"a01."+resume_state+"<>zpt.resume_flag and  "+dbname+"a01."+resume_state+"<>'10'  and "+dbname+"a01."+resume_state+"<>'13' ");     //='11' or "+dbname+"a01."+resume_state+"='12' ) ");
		//	if(state.equals("10"))
		//	   sql.append("  and ( zpt.resume_flag='11' or zpt.resume_flag='12' )  ");
			
			sql.append("  and "+dbname+"a01.a0100 in ("+a0100s.substring(1)+") ");
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next())
			{
				flag="1";
				
				info.append("<br>[");
				info.append(this.frowset.getString("a0101")+"]已为初试状态，不能进行状态设置");
			}
			
			if("1".equals(flag)&&recordIDs.size()>1)
				info.append("<br>您是否执行其他人的简历状态设置？");
			this.getFormHM().put("info",info.toString());
			this.getFormHM().put("flag",flag);
			this.getFormHM().put("state",state);
			this.getFormHM().put("a0100", a0100);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
