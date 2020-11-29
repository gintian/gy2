/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:StartTaskTrans</p>
 * <p>Description:启动任务</p> 
 * <p>Company:hjsj</p> 
 * create time at:Oct 26, 2011 17:13:23 PM
 * @author xieguiquan
 * @version 4.0
 */
public class OverBzTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		
		try
		{
			/**员工自助申请标识
			 *＝1员工
			 *＝0业务员 
			 */
			String selfapply=(String)hm.get("selfapply");
			if(selfapply==null||selfapply.length()==0)
				selfapply="0";
			
			String tabid=(String)hm.get("tabid");
			
			
		
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
		
			String srcTab=this.userView.getUserName()+"templet_"+Integer.parseInt(tabid);
			if("1".equalsIgnoreCase(selfapply))
				srcTab="g_templet_"+tabid;
			this.getFormHM().put("msg","yes");
			if(tablebo.getInfor_type()==1)
			{
				//编制控制
				RecordVo vo=new RecordVo(srcTab);
				if(vo!=null&&(vo.hasAttribute("e01a1_2")||vo.hasAttribute("b0110_2")||vo.hasAttribute("e0122_2")))
				{
					DbNameBo bo=new DbNameBo(this.getFrameconn(),this.userView);
					if(bo.checkOrgMaint2())
					{
						ContentDAO dao=new ContentDAO(this.getFrameconn());
						StringBuffer buf=new StringBuffer("select * from "+srcTab); 
					    buf.append(" where ");
					    if("1".equalsIgnoreCase(selfapply))//员工通过自助平台发动申请
							 buf.append(" a0100='"+this.userView.getA0100()+"' and lower(basepre)='"+this.userView.getDbname().toLowerCase()+"'");
						else
							 buf.append(" submitflag=1");
						RowSet rset=dao.search(buf.toString()); 
						ArrayList recordlist=new ArrayList();	
						LazyDynaBean abean=null; 
						while(rset.next())
						{ 
							abean=new LazyDynaBean();
							String _ca0100=rset.getString("a0100");
							String _srcbase=rset.getString("basepre");
							abean.set("dbpre", _srcbase);abean.set("a0100", _ca0100);
						 
							if(vo!=null&&vo.hasAttribute("b0110_2"))
								abean.set("UN_code", rset.getString("b0110_2")!=null? rset.getString("b0110_2"):"");
							else
								abean.set("UN_code","");
							if(vo!=null&&vo.hasAttribute("e0122_2"))
								abean.set("UM_code", rset.getString("e0122_2")!=null? rset.getString("e0122_2"):"");
							else
								abean.set("UM_code","");
							
							if(vo!=null&&vo.hasAttribute("e01a1_2"))
								abean.set("K_code", rset.getString("e01a1_2")!=null? rset.getString("e01a1_2"):"");
							else
								abean.set("K_code","");
							
							recordlist.add(abean);
						}
						if(rset!=null)
							rset.close();
						String msg=bo.validateOverBz(recordlist);
						if(!"yes".equals(msg))
						{ 
							Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
							String mode=sysoth.getValue(Sys_Oth_Parameter.WORKOUT,"mode");
							if(mode!=null&& "warn".equalsIgnoreCase(mode)){
								this.getFormHM().put("msg",msg);
							}
						}
					}
					
				}  
			}
			
         
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
