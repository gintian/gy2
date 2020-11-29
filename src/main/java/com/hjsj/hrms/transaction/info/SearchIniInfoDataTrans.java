/*
 * Created on 2005-6-23
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.info;

import com.hjsj.hrms.businessobject.info.SortFilter;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hjsj.hrms.businessobject.sys.options.otherparam.OtherParam;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchIniInfoDataTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		//HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String userbase=(String)this.getFormHM().get("userbase");
		String personsort=(String) this.getFormHM().get("personsort");
		String part_unit=(String)this.getFormHM().get("part_unit");
		String dept_field=(String)this.getFormHM().get("part_dept");
		String part_pos=(String)this.getFormHM().get("part_pos");
		String part_setid=(String)this.getFormHM().get("part_setid");
		String part_appoint=(String)this.getFormHM().get("part_appoint");
		String personsortfield=new SortFilter().getSortPersonField(this.getFrameconn());
//		SortFilter sf=new SortFilter();
		String Sortfield="";	
		String strwhere="";	
		String kind="";
		StringBuffer orderby=new StringBuffer();
		orderby.append(" order by ");
		orderby.append("a0000");
		if(!userView.isSuper_admin())
		{
	           String expr="1";
	           String factor="";
			if("UN".equals(userView.getManagePrivCode()))
			{
				factor="B0110=";
			    kind="2";
				if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
				{
					  factor+=userView.getManagePrivCodeValue();
					  factor+="%`";
				}
				else
				{
				  factor+="%`B0110=`";
				  expr="1+2";
				}
			}
			else if("UM".equals(userView.getManagePrivCode()))
			{
				factor="E0122="; 
			    kind="1";
				if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
				{
					  factor+=userView.getManagePrivCodeValue();
					  factor+="%`";
				}
				else
				{
				  factor+="%`E0122=`";
				  expr="1+2";
				}
			}
			else if("@K".equals(userView.getManagePrivCode()))
			{
				factor="E01A1=";
				kind="0";
				if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
				{
					  factor+=userView.getManagePrivCodeValue();
					  factor+="%`";
				}
				else
				{
				  factor+="%`E01A1=`";
				  expr="1+2";
				}
			}
			else
			{
				 expr="1+2";
				factor="B0110=";
			    kind="2";
				if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					factor+=userView.getManagePrivCodeValue();
				factor+="%`B0110=`";
			}			
			 ArrayList fieldlist=new ArrayList();
		        try
		        {  
		            /**表过式分析*/
		            /**非超级用户且对人员库进行查询*/
		            strwhere=userView.getPrivSQLExpression(expr+"|"+factor,userbase,false,fieldlist);
		            if(personsortfield!=null && !"null".equalsIgnoreCase(personsortfield) && personsortfield.length()>0&&!"ALL".equalsIgnoreCase(personsort))
			    		strwhere=strwhere + " and " + personsortfield + "='" + personsort + "'";
		           
				    //union_Sql.append(" UNION ");
				    DbNameBo dbbo=new DbNameBo(this.getFrameconn());
				    FieldItem pos_fielditem=DataDictionary.getFieldItem(part_pos);
			    	if(part_unit!=null&&part_unit.length()>0&&part_setid!=null&&part_setid.length()>0)
			    	{
			    		if(pos_fielditem!=null&&!"0".equalsIgnoreCase(pos_fielditem.getUseflag())&&"@K".equalsIgnoreCase(pos_fielditem.getCodesetid())){
			    			strwhere=strwhere+" or ( "+userbase+"A01.a0100 in(select a0100 from "+userbase+""+part_setid+" where ("+part_unit+" like '"+this.userView.getManagePrivCodeValue()+"%'";
			    			if(StringUtils.isNotEmpty(dept_field))
			    			    strwhere += " or "+dept_field+" like '"+this.userView.getManagePrivCodeValue()+"%'";
			    			
			    			if(StringUtils.isNotEmpty(dept_field))
			    			    strwhere += " or "+part_pos+" like '"+this.userView.getManagePrivCodeValue()+"%'";
			    			
			    			strwhere += ")";
			    			strwhere +=" and a0100 in (select a0100 "+this.userView.getPrivSQLExpression(userbase, false);
			    			String tmp = this.condpriv2part(userView, part_unit, dept_field,part_pos, userbase);
			    			if(tmp.length()>0)
			    				strwhere +="union select a0100 " +tmp + ")";
			    			else
			    				strwhere +=")";
			    			if(part_appoint!=null&&part_appoint.length()>0)
			    				strwhere=strwhere+" and "+part_appoint+"='0' ";
			    			//union_Sql.append(" and "+userbase+""+part_setid+".a0100 in (select a0100 "+priv_stwhere+")");//韩俊华提，只要管理人，对改部门有权限，就能对该部门兼职人员进行操作
			    			strwhere=strwhere+"))";
			    		}else{			    			
			    			strwhere=strwhere+" or ( "+userbase+"A01.a0100 in(select a0100 from "+userbase+""+part_setid+" where ("+part_unit+" like '"+this.userView.getManagePrivCodeValue()+"%'";
			    			if(StringUtils.isNotEmpty(dept_field))
		                         strwhere += " or "+dept_field+" like '"+this.userView.getManagePrivCodeValue()+"%'";
		                            
			    			strwhere += ")";
			    			strwhere +=" and a0100 in (select a0100 "+this.userView.getPrivSQLExpression(userbase, false);
			    			String tmp=this.condpriv2part(userView, part_unit, dept_field, userbase);
			    			if(tmp.length()>0)
			    				strwhere +="union select a0100 " +tmp + ")";
			    			else
			    				strwhere +=")";
			    			if(part_appoint!=null&&part_appoint.length()>0)
			    				strwhere=strwhere+" and "+part_appoint+"='0' ";
			    			//union_Sql.append(" and "+userbase+""+part_setid+".a0100 in (select a0100 "+priv_stwhere+")");//韩俊华提，只要管理人，对改部门有权限，就能对该部门兼职人员进行操作
			    			strwhere=strwhere+"))";
			    		}
			    	}
		        }catch(Exception e){
		          e.printStackTrace();	
		        }
	
		}else{
			StringBuffer wheresql=new StringBuffer();
			wheresql.append(" from ");
			wheresql.append(userbase);
			wheresql.append("A01 ");			
			kind="2";
			strwhere=wheresql.toString();
		}
		
		String setprv=getEditSetPriv("A01");
		this.getFormHM().put("setprv",setprv);
		StringBuffer strsql=new StringBuffer();
		StringBuffer columns=new StringBuffer();
		strsql.append("select "+userbase+"A01.a0000 a0000,");
	    strsql.append(userbase);
		strsql.append("A01.A0100");
		columns.append("A0100,a0000");
	    String fieldstr=new SaveInfo_paramXml(this.getFrameconn()).getInfo_paramNode("browser");
	    if(fieldstr!=null&&fieldstr.length()>0)
	    {
	       fieldstr=new SortFilter().getBrowseFields(fieldstr,userbase,this.getFrameconn(),this.userView);
		   if(fieldstr==null||fieldstr.length()<=0){				
				fieldstr=",B0110,E0122,E01A1,A0101,UserName";				
		   }
	       if(fieldstr.indexOf("state")!=-1)
	       {
	    	   fieldstr=fieldstr+",state";
	       }		
	       strsql.append(fieldstr);
		   columns.append(fieldstr);
	   }else{
				fieldstr=",B0110,E0122,E01A1,A0101,UserName,state";
				strsql.append(",B0110,E0122,E01A1,A0101,UserName,state");
				columns.append(",B0110,E0122,E01A1,A0101,UserName,state");
	   }
//	    在这里显示指标
		//strsql.append("A01.A0100,B0110,E0122,E01A1,A0101,UserName,state");
//		yuxiaochun add program
		try{
			if(personsort!=null&&personsort.length()>0&&!"All".equals(personsort)){
				OtherParam param=new OtherParam(this.getFrameconn());
			    Map  atMap=param.serachAtrr("/param/employ_type");
			    Sortfield=(String) atMap.get("field");
			    if(!userView.isSuper_admin()){
			    	strwhere=strwhere+" and "+Sortfield+"='"+personsort+"'";
			    }else{
			    strwhere=strwhere+" where "+Sortfield+"='"+personsort+"'";
			    }
			}
			}catch(Exception ex){
				ex.printStackTrace();
			}
//		yuxiaochun add program
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
	    String flag=(String)hm.get("flag");
		ArrayList infoFieldList=new ArrayList();
		String fenlei_priv=(String)this.getFormHM().get("fenlei_priv");		
		if("infoself".equalsIgnoreCase(flag))
		{
			infoFieldList=userView.getPrivFieldList("A01",0);   //获得当前子集的所有属性
		}
	    else
		{	
			infoFieldList=userView.getPrivFieldList("A01");      //获得当前子集的所有属性
		}
		ArrayList fields=new ArrayList();
		//
		String[] f=fieldstr.split(",");
		for(int i=0;i<f.length;i++){
					for(int j=0;j<infoFieldList.size();j++){
						FieldItem fieldItem=(FieldItem)infoFieldList.get(j);
						if(fieldItem.getPriv_status() !=0)                //只加在有读写权限的指标
						{
							if(f[i].equalsIgnoreCase(fieldItem.getItemid()))
							{
								fields.add(fieldItem);
							}
						}
					}
		}
		//System.out.println(strwhere);
		this.getFormHM().put("browsefields",fields);
	    this.getFormHM().put("strsql",strsql.toString());
		this.getFormHM().put("cond_str",strwhere); 
		this.getFormHM().put("columns",columns.toString());	
		this.getFormHM().put("code",userView.getManagePrivCodeValue());
		this.getFormHM().put("kind",kind);
		this.getFormHM().put("order_by",orderby.toString());        
	
		//人员分类条件
		List condlist=getCondList();
		this.getFormHM().put("condlist", condlist);
		this.getFormHM().put("stock_cond", "");
	}
	/**
	 * 求对子集修改权限，具体算法根据子集权限和指标权限进行分析．
	 * @param infoSetList
	 * @param infoFieldSetList
	 * @param setname
	 * @return
	 */
	private String getEditSetPriv(String setname)
	{
		String setpriv=userView.analyseTablePriv(setname);
		return setpriv;
	}
	 /**人员分类 常用查询*/
    private List getCondList(){
    	List condlist=new ArrayList();
    	try {
    		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
	    	ContentDAO dao=new ContentDAO(this.getFrameconn());
			String gquery_cond = sysbo.getValue(Sys_Oth_Parameter.GQUERY_COND,"value");
			if(gquery_cond==null)
				gquery_cond="";
			if(gquery_cond.trim().length()<=0)
				return condlist;
			String[] gquery_conds=gquery_cond.split(",");
			if(gquery_conds.length<=0||gquery_conds[0].trim().length()<1)
				return condlist;
			StringBuffer sql=new StringBuffer();
			StringBuffer wheresql=new StringBuffer();
			sql.append("select id,name from lexpr where id in(");
			for (int i = 0; i < gquery_conds.length; i++) {
				if(gquery_conds[i]!=null&&gquery_conds[i].length()>0){
					if(!(this.userView.isHaveResource(IResourceConstant.LEXPR,gquery_conds[i])))
	                	continue;
					wheresql.append(gquery_conds[i]+",");
				}
			}
			if(wheresql.length()>0)
			   wheresql.setLength(wheresql.length()-1);
			else
			   return condlist;
			sql.append(wheresql.toString()+")");
			sql.append(" order by norder");
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next())
			{	
				CommonData cd = new CommonData(this.frowset.getString("id"),this.frowset.getString("name"));
				condlist.add(cd);
			}
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
		return condlist;
    }
    
    private String condpriv2part(UserView userView,String unit_field,String dept_field,String dbpre){
		RowSet rs = null;
		String condpriv2part="";
    	try
    	{
    		if(!userView.isSuper_admin()){
    			String sql = "";
    			/*if(userView.getStatus()!=0){
    				sql = " select condpriv from t_sys_function_priv where upper(id)='"+(userView.getDbname()+userView.getUserId()).toUpperCase()+"'";
    			}else{
	    			sql = " select condpriv from t_sys_function_priv where id='"+userView.getUserId()+"'";
    			}
    			ContentDAO dao = new ContentDAO(conn);
	    		rs=dao.search(sql);
	    		if(rs.next())*/
	    		{
	    			//String condpriv=rs.getString("condpriv");
	    			String condpriv=userView.getPrivExpression();
	    			if(condpriv!=null&&condpriv.length()>0){
	    				String tmps[]=condpriv.split("\\|");
	    				if(tmps.length==2){
	    					String expr=tmps[1].toUpperCase();
	    					expr = expr.replaceAll("B0110", unit_field).replaceAll("E0122", dept_field);
	    		            String factor=tmps[0];
	    					FactorList factorslist=new FactorList(factor,expr,dbpre,false,false,true,1,userView.getUserId());
	    					//System.out.println(factorslist.getSqlExpression());
	    					sql = factorslist.getSqlExpression();
	    					//condpriv2part = sql.substring(0,(sql.indexOf(".I9999")-11));
	    					condpriv2part=sql;
	    				}
	    			}
	    		}
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return condpriv2part;
    }
    private String condpriv2part(UserView userView,String unit_field,String dept_field,String pos_field,String dbpre){
		RowSet rs = null;
		String condpriv2part="";
    	try
    	{
    		if(!userView.isSuper_admin()){
    			String sql = "";
    			/*if(userView.getStatus()!=0){
    				sql = " select condpriv from t_sys_function_priv where upper(id)='"+(userView.getDbname()+userView.getUserId()).toUpperCase()+"'";
    			}else{
	    			sql = " select condpriv from t_sys_function_priv where id='"+userView.getUserId()+"'";
    			}
    			ContentDAO dao = new ContentDAO(conn);
	    		rs=dao.search(sql);
	    		if(rs.next())*/
	    		{
	    			//String condpriv=rs.getString("condpriv");
	    			String condpriv=userView.getPrivExpression();
	    			if(condpriv!=null&&condpriv.length()>0){
	    				String tmps[]=condpriv.split("\\|");
	    				if(tmps.length==2){
	    					String expr=tmps[1].toUpperCase();
	    					expr = expr.replaceAll("B0110", unit_field).replaceAll("E0122", dept_field).replace("E01A1",pos_field);
	    		            String factor=tmps[0];
	    					FactorList factorslist=new FactorList(factor,expr,dbpre,false,false,true,1,userView.getUserId());
	    					//System.out.println(factorslist.getSqlExpression());
	    					sql = factorslist.getSqlExpression();
	    					//condpriv2part = sql.substring(0,(sql.indexOf(".I9999")-11));
	    					condpriv2part=sql;
	    				}
	    			}
	    		}
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return condpriv2part;
    }
}
