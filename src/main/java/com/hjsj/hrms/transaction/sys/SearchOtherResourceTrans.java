/**
 * 
 */
package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * <p>Title:</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Jul 24, 20062:35:58 PM
 * @author chenmengqing
 * @version 4.0
 */
public class SearchOtherResourceTrans extends IBusiness implements IResourceConstant {

	
	private String getResourceSql(int res_type,String str_content,Map parammap)
	{
		StringBuffer strsql=new StringBuffer();
		if("".equals(str_content))
			str_content="-1";
		String searchparam = "";
		String searchwhere = "";
		if(!parammap.isEmpty()){
			searchparam = (String)parammap.get("searchparam");
		}
		/**
		 * 超级用户组下的用户,如果未关联三员角色时
		 * 才为超级管理员,
		 * 针对这种用户仅功能和管理范围进行控制，其它资源还是按超级用户进行管理 ，at 20111123
		 */
		boolean bSuperAdmin=false;
		bSuperAdmin=userView.isSuper_admin()/*&&(!userView.isBThreeUser())*/;
		switch(res_type)
		{
		case GZ_REPORT_STYLE:
			if(!bSuperAdmin)
			{
				strsql.append(" select rsid tabid,rsname name from reportstyle where rsid in(");
				strsql.append(str_content);
				strsql.append(") and ((rsid>=5 and rsid<12) or rsid>=14) order by rsid");
			}
			else
			{
				strsql.append(" select rsid tabid,rsname name from reportstyle ");
				strsql.append(" where ((rsid>=5 and rsid<12) or rsid>=14) order by rsid");
			}
			break;
		
		case REPORT:
			if(!bSuperAdmin)
			{
				strsql.append("select tabid,name from tname where tabid in (");
				strsql.append(str_content);
				strsql.append(") order by tsortid,tabid");
			}
			else
			{
				strsql.append("select tabid,name from tname ");
				strsql.append(" order by tsortid,tabid");				
			}
			break;
		case CARD:
			if(!bSuperAdmin)
			{
				strsql.append("select tabid,name from rname where tabid in (");
				strsql.append(str_content);
				strsql.append(") order by tabid");
			}
			else
			{
				strsql.append("select tabid,name from rname ");
				strsql.append(" order by tabid");				
			}
			break;
		case MUSTER:
			if(!bSuperAdmin)
			{
				strsql.append("select tabid,hzname name from lname where tabid in (");
				strsql.append(str_content);
				strsql.append(") order by flag, tabid");
			}
			else
			{
				strsql.append("select tabid,hzname name from lname ");
				strsql.append(" order by flag, tabid");				
			}			
			break;
		case HIGHMUSTER:
			if(!bSuperAdmin)
			{
				strsql.append("select tabid,cname name from muster_name where tabid in (");
				strsql.append(str_content);
				strsql.append(") order by nmodule, tabid");
			}
			else
			{
				strsql.append("select tabid,cname name from muster_name ");
				strsql.append(" order by nmodule, tabid");				
			}				
			break;
		case LEXPR:
			if(!bSuperAdmin)
			{
				strsql.append("select id tabid,name  from lexpr where id in (");
				strsql.append(str_content);
				strsql.append(") order by type, tabid");
			}
			else
			{
				strsql.append("select id tabid,name from lexpr ");
				strsql.append(" order by type, tabid");				
			}				
			break;
		case STATICS:
			if(!bSuperAdmin)
			{
				strsql.append("select id tabid,name  from sname where id in (");
				strsql.append(str_content);
				strsql.append(") order by type, tabid");
			}
			else
			{
				strsql.append("select id tabid,name from sname ");
				strsql.append(" order by type, tabid");				
			}			
			break;
		case GZ_CHART:
			if(!bSuperAdmin)
			{
				strsql.append("select  tbid tabid,tablename name  from stattable where tbid in (");
				strsql.append(str_content);
				strsql.append(") and statkind=1 order by norder");
			}
			else
			{
				strsql.append("select  tbid tabid,tablename name  from stattable ");
				strsql.append("where statkind=1 order by norder");		
			}			
			break;			
		case INVEST: //问卷调查表
			if(!(bSuperAdmin||userView.haveTheRoleProperty("2")))
			{
				String temp=exchg_ResStr(str_content);
				strsql.append("select id tabid,content name  from investigate where flag=1 and status=1 and id in (");
				strsql.append(temp);
				strsql.append(") ");
			}
			else
			{
				strsql.append("select id tabid,content name from investigate  where flag=1 and status=1");
			}			
			break;
		case TRAINJOB:
			if(!(bSuperAdmin||userView.haveTheRoleProperty("2")))
			{
				String temp=exchg_ResStr(str_content);
				strsql.append("select r3101 tabid,r3130 name  from r31 where r3127='04' and r3101 in (");
				strsql.append(temp);
				strsql.append(") ");
			}
			else
			{
				strsql.append("select r3101 tabid,r3130 name from r31  where r3127='04'");
			}			
			break;	
		case ANNOUNCE:
			if(!bSuperAdmin)
			{
				strsql.append("select id tabid,topic name  from announce where approve=1 and id in (");
				strsql.append(str_content);
				strsql.append(") order by createtime desc");
			}
			else
			{
				strsql.append("select id tabid,topic name from announce where approve=1 ");
				strsql.append(" order by createtime desc");				
			}			
			break;	
		case ARCH_TYPE:
			String archivetype=SystemConfig.getPropertyValue("archivetype");
			if(archivetype==null||archivetype.length()==0)
				archivetype="XB";
			if(!bSuperAdmin)
			{
				strsql.append("select codeitemid tabid,codeitemdesc name from codeitem where codesetid='"+archivetype);
				strsql.append("' and codeitemid in(");
				strsql.append(str_content);
				strsql.append(")");
			}
			else
			{
				strsql.append("select codeitemid tabid,codeitemdesc name from codeitem where codesetid='"+archivetype);
				strsql.append("'");
			}
			break;
		case GZ_SET:
			if(!bSuperAdmin)
			{
				strsql.append("select salaryid tabid,cname name from SALARYTEMPLATE where (cstate is null or cstate='') and ");
				strsql.append(" salaryid in(");
				strsql.append(str_content);
				strsql.append(")");
				searchwhere = queryWhere(GZ_SET,searchparam);
				strsql.append(searchwhere);
			}
			else
			{
				strsql.append("select salaryid tabid,cname name from SALARYTEMPLATE where (cstate is null or cstate='')");
				searchwhere = queryWhere(GZ_SET,searchparam);
				strsql.append(searchwhere);
			}
			strsql.append(" order by seq");
			break;
		case INS_SET:
			if(!bSuperAdmin)
			{
				strsql.append("select salaryid tabid,cname name from SALARYTEMPLATE where cstate ='1' and ");
				strsql.append(" salaryid in(");
				strsql.append(str_content);
				strsql.append(")");
				searchwhere = queryWhere(INS_SET,searchparam);
				strsql.append(searchwhere);
			}
			else
			{
				strsql.append("select salaryid tabid,cname name from SALARYTEMPLATE where cstate ='1' ");
				searchwhere = queryWhere(INS_SET,searchparam);
				strsql.append(searchwhere);
			}			
			break;			
		case KQ_MACH:
			if(!bSuperAdmin)
			{
				strsql.append("select location_id tabid,name name from kq_machine_location where ");
				strsql.append(" location_id in(");
				strsql.append(str_content);
				strsql.append(")");
			}
			else
			{
				strsql.append("select location_id tabid,name name from kq_machine_location");
			}
			break;
		case MEDIA_EMP:
			if(!bSuperAdmin)
			{
				strsql.append("select id tabid,sortname name from mediasort where ");
				strsql.append(" id in(");
				strsql.append(str_content);
				strsql.append(")");
			}
			else
			{
				strsql.append("select id tabid,sortname name from mediasort");
			}
			break;	
		case KEY_EVENT:
			if(!bSuperAdmin)
			{
				strsql.append("select codeitemid tabid,codeitemdesc name from codeitem where codesetid='67' and");
				strsql.append(" codeitemid in('");
				strsql.append(str_content.replaceAll(",", "','"));
				strsql.append("')");
			}
			else
			{
				strsql.append("select codeitemid tabid,codeitemdesc name from codeitem where codesetid='67'");
			}
			break;		
		}

		return strsql.toString();
	}
	/**
	 * 转换资源串
	 * @param res_str
	 * @return
	 */
    private String exchg_ResStr(String res_str)
    {
    	String[] strs=StringUtils.split(res_str,",");
    	int i=0;
    	StringBuffer buf=new StringBuffer();
    	for(i=0;i<strs.length;i++)
    	{
    		buf.append("'");
    		buf.append(strs[i]);
    		buf.append("'");
    		buf.append(",");
    	}
    	buf.setLength(buf.length()-1);
    	return buf.toString();
    }
    /**
     * 生成查询条件
     * @param searchparam
     * @return
     */
    public String queryWhere(int resid,String searchparam){
    	StringBuffer buf=new StringBuffer();
    	if(resid==12 ||resid==18){
    		if(!"".equals(searchparam) && searchparam!=null){
    			String reg ="\\d+\\.{0,1}\\d*";
    			boolean isDigits = searchparam.matches(reg);
    			if(isDigits){
    				buf.append(" and ( cname like '%"+searchparam+"%'");
            		buf.append(" or salaryid ="+searchparam);
            		buf.append(")");
    			}else{
    				if("_".equals(searchparam)){//暂时先这样处理
    					buf.append(" and cname like '%$"+searchparam+"%' escape '$' ");
    				}
    				buf.append(" and cname like '%"+searchparam+"%'");
    			}
        		
    		}else{
    			buf.append(" and 1=1");
    		}
    	}
    	return buf.toString();
    }     
	public void execute() throws GeneralException {
		try
		{
			// 添加了薪资类别和保险类别的查询功能 add hej 2015.7.15
			Map parammap = new HashMap();
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String searchparam = "";
			String buttonflag = (String)hm.get("buttonflag");
			hm.put("buttonflag", "");
			if("12".equals(hm.get("res_flag"))){
			  searchparam = (String)this.getFormHM().get("searchparamgz");
			}
			if("18".equals(hm.get("res_flag"))){
			  searchparam = (String)this.getFormHM().get("searchparambx");
			}
			//this.getFormHM().put("searchparam", "");
			if("请输入编号或薪资类别名称".equals(searchparam) || "请输入编号或保险类别名称".equals(searchparam)|| searchparam==null)
				searchparam = "";
			if(buttonflag==null||"".equals(buttonflag)){
				parammap.put("searchparam", "");
			}else{
				parammap.put("searchparam", searchparam);
			}
			String flag=(String)this.getFormHM().get("flag");
			String roleid=(String)this.getFormHM().get("roleid");
			String res_flag=(String)this.getFormHM().get("res_flag");
			if(flag==null|| "".equals(flag))
	            flag=GeneralConstant.ROLE;
			if(res_flag==null|| "".equals(res_flag))
				res_flag="0";
			/**资源类型*/
			int res_type=Integer.parseInt(res_flag);
			/**采用预警字段作为其资源控制字段*/
			/**当前被授权用户拥有的资源*/
			SysPrivBo privbo=new SysPrivBo(roleid,flag,this.getFrameconn(),"warnpriv");
			String res_str=privbo.getWarn_str();
			ResourceParser parser=new ResourceParser(res_str,res_type);
			
			/**1,2,3*/
			String str_content=","+parser.getContent()+",";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String temp=this.userView.getResourceString(res_type);
			this.frowset=dao.search(getResourceSql(res_type,temp,parammap));
			ArrayList list=new ArrayList();
			int idx=0;
			while(this.frowset.next())
			{
                DynaBean dbean=new LazyDynaBean();
                String name = this.frowset.getString("name");
                name=name!=null?name:"";
                String tabid=this.frowset.getString("tabid");
                if(!(this.userView.isHaveResource(res_type,tabid)))
        			continue; 
                name=name!=null?name:"";
                dbean.set("name",tabid+"."+name);
                dbean.set("tabid",tabid);
                idx=str_content.indexOf(","+tabid+",");
                if(idx==-1)
                	dbean.set("c0","0");
                else
                	dbean.set("c0","1");                	
                list.add(dbean);
			}
			this.getFormHM().put("list",list);
            this.getFormHM().put("res_flag",res_flag);
			if(buttonflag==null || "".equals(buttonflag))
			{
				if("12".equals(hm.get("res_flag"))){
				    this.getFormHM().put("searchparamgz","");
				}
				if("18".equals(hm.get("res_flag"))){
					this.getFormHM().put("searchparambx","");
				}
			}else{
				if("12".equals(hm.get("res_flag"))){
				    this.getFormHM().put("searchparamgz",searchparam);
				}
				if("18".equals(hm.get("res_flag"))){
					this.getFormHM().put("searchparambx",searchparam);
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
