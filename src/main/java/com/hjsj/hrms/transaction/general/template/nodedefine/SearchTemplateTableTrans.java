/**
 * 
 */
package com.hjsj.hrms.transaction.general.template.nodedefine;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

/**
 * @author Owner
 *
 */
public class SearchTemplateTableTrans extends IBusiness{

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		String unit_type=sysbo.getValue(Sys_Oth_Parameter.UNITTYPE,"type");
		if(unit_type==null|| "".equals(unit_type))
			unit_type="3";
		StringBuffer strsql=new StringBuffer();
		strsql.append("select tabid,name from template_table where sp_flag='1' ");
		//strsql.append("and flag="+Integer.parseInt(unit_type));	
		strsql.append(" and (");
		String units[]=unit_type.split(",");
		for(int i=0;i<units.length;i++)
		{
			strsql.append("flag ="+Integer.parseInt(units[i]));
			if(i<units.length-1)
				strsql.append(" or ");
		}			
		strsql.append(")");
		if(!this.userView.isSuper_admin())
		{
			String tmp=getTemplates();
			if(tmp.length()==0)
			{
				strsql.append(" and 1=2");
			}
			else
			{
				strsql.append(" and tabid in (");
				strsql.append(tmp);
				strsql.append(")");
			}			
		}
		StringBuffer columns=new StringBuffer();
		columns.append("tabid,name");
		this.getFormHM().put("strsql",strsql.toString());
		this.getFormHM().put("columns",columns.toString());
	}

	/**
	 * 求权限范围下的模板串
	 * @return
	 */
	private String getTemplates() {
		StringBuffer mb=new StringBuffer();
		String rsbd=this.userView.getResourceString(IResourceConstant.RSBD);
		mb.append(rsbd);
		mb.append(",");
		String gzbd=this.userView.getResourceString(IResourceConstant.GZBD);
		mb.append(gzbd);
		mb.append(",");					
		String bybd=this.userView.getResourceString(IResourceConstant.INS_BD);
		mb.append(bybd);
		mb.append(",");	
		String pso=this.userView.getResourceString(IResourceConstant.PSORGANS);
		mb.append(pso);
		mb.append(",");	
		String fg=this.userView.getResourceString(IResourceConstant.PSORGANS_FG);
		mb.append(fg);
		mb.append(",");	
		String gx=this.userView.getResourceString(IResourceConstant.PSORGANS_GX);
		mb.append(gx);
		mb.append(",");	
		String jcg=this.userView.getResourceString(IResourceConstant.PSORGANS_JCG);
		mb.append(jcg);
		mb.append(",");	
		String orbd=this.userView.getResourceString(IResourceConstant.ORG_BD);
		orbd =orbd.replace("R","");
		mb.append(orbd);
		mb.append(",");	
		String pobd=this.userView.getResourceString(IResourceConstant.POS_BD);
		pobd =pobd.replace("R","");
		mb.append(pobd);
		mb.append(",");	
		String[] bdarr=StringUtils.split(mb.toString(),",");
		if(bdarr==null || bdarr.length==0)
			return "";
		
		//String tmp=Arrays.toString(bdarr);
		String tmp=StringUtils.join(bdarr, ',');
		//tmp=tmp.substring(1,tmp.length()-1);
		tmp = tmp.replace("r", "");
		tmp = tmp.replace("R", "");
		tmp = tmp.replace(" ", "");
		tmp = tmp.replace(",,", ",");
		return tmp;
	}	
}
