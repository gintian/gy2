package com.hjsj.hrms.transaction.browse;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.info.SortFilter;
import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class ScanInfoDataTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {	
		
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String userbase=(String)this.getFormHM().get("userbase");
		String orglike=(String)this.getFormHM().get("orglike");
		String code=(String)this.getFormHM().get("code"); 
		String kind=(String)this.getFormHM().get("kind");
		String orgtype=(String)this.getFormHM().get("orgtype");
		String querylike=(String)this.getFormHM().get("querylike");	//模糊查询
		String select_name=(String)this.getFormHM().get("select_name");		
		select_name = PubFunc.hireKeyWord_filter_reback(select_name);
		if(orgtype==null||orgtype.length()<=0)
			orgtype="org";
		this.getFormHM().put("orgtype", orgtype);
		String part_unit=(String)this.getFormHM().get("part_unit");
		String part_setid=(String)this.getFormHM().get("part_setid");
		String part_appoint=(String)this.getFormHM().get("part_appoint");
		StringBuffer orderby=new StringBuffer();	
		
		String personsort=(String)this.getFormHM().get("personsort");
		String query=(String)this.getFormHM().get("query");
		if(StringUtils.isEmpty(query) || !"1".equals(query)) {
			select_name = "";
			this.getFormHM().put("select_name", "");
		}
		
		String personsortfield=new SortFilter().getSortPersonField(this.getFrameconn());
		if(personsortfield!=null&&personsortfield.length()>0)
		{
			if((personsort==null||personsort.length()<=0))
			{
				ArrayList personsortlist=(ArrayList)this.getFormHM().get("personsortlist");
				if(personsortlist!=null&&personsortlist.size()>0)
				{
					CodeItem codeitem=(CodeItem)personsortlist.get(0);
					personsort=codeitem.getCcodeitem();
				}
			}
		}
		StringBuffer strsql=new StringBuffer();
		StringBuffer columns=new StringBuffer();

		strsql.append("select "+userbase+"A01.a0000 a0000,");//chenmengqing added 20070910
		strsql.append(userbase);
		strsql.append("A01.A0100 a0100");	
		columns.append("A0100");
		
		String fieldstr=new SaveInfo_paramXml(this.getFrameconn()).getInfo_paramNode("browser");
		this.getFormHM().put("browsefields","");
		if(fieldstr!=null&&fieldstr.length()>0){
			fieldstr=new SortFilter().getBrowseFields(fieldstr,userbase,this.getFrameconn(),this.userView);
			if(fieldstr==null||fieldstr.length()<=0){				
				fieldstr=",B0110,E0122,E01A1,A0101,UserName";				
			}
			if(fieldstr.toLowerCase().indexOf("b0110")!=-1)
			{
				String newfieldstr=fieldstr.toLowerCase().replaceAll("b0110",userbase+"A01.b0110");
				strsql.append(newfieldstr);
			}else
			{
				strsql.append(fieldstr);
			}			 
			columns.append(fieldstr);
		}else{
			fieldstr=",B0110,E0122,E01A1,A0101,UserName";
			strsql.append(","+userbase+"A01.B0110,"+userbase+"A01.E0122,"+userbase+"A01.E01A1,"+userbase+"A01.A0101,UserName");
			columns.append(",B0110,E0122,E01A1,A0101,UserName");
		}	
		InfoUtils infoUtils=new InfoUtils();
		String where_n="";		
		if(select_name!=null&&select_name.length()>0&&!"null".equals(select_name))
		{
			String whereA0101=infoUtils.whereA0101(this.userView,this.getFrameconn(), userbase, select_name,querylike);
			if(whereA0101!=null&&whereA0101.length()>0)
				  where_n=" and "+whereA0101;
			this.formHM.put("p_select_name", select_name.trim());
		}
		else
		{
			this.formHM.put("p_select_name", "");
		}
		this.getFormHM().put("where_n", where_n);		
		
		boolean isOrgLike=false;
		if(orglike!=null&& "1".equals(orglike))
			isOrgLike=true;
		String term_Sql=infoUtils.getWhereSQLExists(this.getFrameconn(),this.userView,userbase,code,isOrgLike,kind,orgtype,personsortfield,personsort);
		
		
		String setprv=userView.analyseTablePriv("A01");
		this.getFormHM().put("setprv",setprv);	
    	String flag=(String)hm.get("flag");
    	ArrayList infoFieldList=new ArrayList();		
    	FieldSet fieldset=DataDictionary.getFieldSetVo("A01");
    	GzDataMaintBo gzDataMaintBo=new GzDataMaintBo(this.getFrameconn());
    	infoFieldList=gzDataMaintBo.fieldItemList(fieldset);
		ArrayList fields=new ArrayList();
		
		
		//
		String[] f=fieldstr.split(",");
		for(int i=0;i<f.length;i++){
			for(int j=0;j<infoFieldList.size();j++){
				FieldItem fieldItem_O=(FieldItem)infoFieldList.get(j);
				FieldItem fieldItem=(FieldItem)fieldItem_O.clone();				
				//if(fieldItem.getPriv_status() !=0)                //只加在有读写权限的指标
				if(!"0".equals(this.userView.analyseFieldPriv(fieldItem.getItemid())))
				{
					if(f[i].equalsIgnoreCase(fieldItem.getItemid()))
					{
						fields.add(fieldItem);
					}
				}
			}
		}

		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		String roster=sysbo.getValue(Sys_Oth_Parameter.COMMON_ROSTER);
		roster=roster!=null&&roster.trim().length()>0&&!"#".equals(roster)?roster:"no";
		if(!this.userView.isSuper_admin()){
			String temp=this.userView.getResourceString(4);
			if(temp.indexOf(roster)<0){
				roster="no";
			}
		}		
		this.getFormHM().put("browsefields",fields);	
		
		
		//System.out.println(buf.toString());
		String checkSql="";
		if(query!=null&& "1".equals(query))
		{
			//String querylike=(String)this.getFormHM().get("querylike");
			ArrayList factorlist=(ArrayList)this.getFormHM().get("scanfieldlist");
			String whereTrem=infoUtils.combine_ExistsSQL(this.userView,factorlist,querylike,userbase,"1");
			if(whereTrem!=null&&whereTrem.length()>0)
			{
				
				checkSql="(exists(select 1 "+whereTrem+" and "+userbase+"A01.a0100=Q.a0100))";		
			}	
			//this.getFormHM().put("queryfieldlist",infoUtils.clearQueryList(factorlist));
			
			this.getFormHM().put("isShowCondition", "none");	
	    }else
	    {
	    	if(query!=null&& "0".equals(query))
	    	{
	    		ArrayList factorlist=(ArrayList)this.getFormHM().get("scanfieldlist");
	    		this.getFormHM().put("scanfieldlist",infoUtils.clearFieldValueList(factorlist));
	    	}
	    	this.getFormHM().put("query", "");
	    	this.getFormHM().put("isShowCondition", "none");	
	    }
		StringBuffer buf=new StringBuffer();
		StringBuffer wherebuf=new StringBuffer();
		buf.append(strsql.toString());
		buf.append(" from "+userbase+"A01");
		buf.append(" where exists(select a0100 from ("+term_Sql.toString()+")A where  "+userbase+"A01.a0100=A.a0100)");
		wherebuf.append(" where exists(select a0100 from ("+term_Sql.toString()+")A where  "+userbase+"A01.a0100=A.a0100)");
		if(checkSql!=null&&checkSql.length()>0)
		{
			wherebuf.append(" and "+checkSql);
			buf.append(" and "+checkSql);
		} 
		if(where_n!=null&&where_n.length()>=0)
    	{
			buf.append(" "+where_n);
			wherebuf.append(" "+where_n);
    	}
		orderby.append(" order by a0000");
			
		
		
		//System.out.println(buf.toString());
	    this.getFormHM().put("strsql",buf.toString());
	    //this.getFormHM().put("ensql",SafeCode.encode(wherebuf.toString()));
		this.getFormHM().put("cond_str",""); 
		this.getFormHM().put("code",code);
		this.getFormHM().put("kind",kind);
		this.getFormHM().put("order_by",orderby.toString());
		this.getFormHM().put("columns",columns.toString());	
		this.getFormHM().put("roster",roster);
		
	}

	
	
}
