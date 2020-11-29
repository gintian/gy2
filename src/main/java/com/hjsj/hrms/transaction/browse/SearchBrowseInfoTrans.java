/**
 * 
 */
package com.hjsj.hrms.transaction.browse;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.info.SortFilter;
import com.hjsj.hrms.businessobject.kq.machine.ReconstructionKqField;
import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * @author Owner
 *
 */
public class SearchBrowseInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {	
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String userbase=(String)this.getFormHM().get("userbase");
		String code=(String)this.getFormHM().get("code"); 
		String kind=(String)this.getFormHM().get("kind");
		String orgtype=(String)hm.get("orgtype");
		String select_name=(String)this.getFormHM().get("select_name");		
		String querylike=(String)this.getFormHM().get("querylike");	//模糊查询
		//二次查询
		String querySecond=(String)this.getFormHM().get("querySecond");	
		if(orgtype==null||orgtype.length()<=0)
			orgtype="org";
		String orglike=(String)this.getFormHM().get("orglike");
		this.getFormHM().put("orgtype", orgtype);
		String part_unit=(String)this.getFormHM().get("part_unit");
		String part_setid=(String)this.getFormHM().get("part_setid");
		String stock_cond=(String)this.getFormHM().get("stock_cond");//人员分类
		String browse_search_state=(String)this.getFormHM().get("browse_search_state");//员工信息浏览查询项 0：隐藏，1：显示
		browse_search_state=browse_search_state!=null&&browse_search_state.length()>0?browse_search_state:"0";
		stock_cond=stock_cond==null|| "-1".equals(stock_cond)?"":stock_cond;
		StringBuffer orderby=new StringBuffer();		
		//String userbase=(String)this.getFormHM().get("userbase");
		String personsort=(String)this.getFormHM().get("personsort");
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
		strsql.append("A01.A0100");	
		columns.append("A0100");
		
		String fieldstr=new SaveInfo_paramXml(this.getFrameconn()).getInfo_paramNode("browser");
		this.getFormHM().put("browsefields","");
		if(fieldstr!=null&&fieldstr.length()>0){
			fieldstr=new SortFilter().getBrowseFields(fieldstr,userbase,this.getFrameconn(),this.userView);
			if(fieldstr==null||fieldstr.length()<=0){				
				fieldstr=",B0110,E0122,E01A1,A0101,UserName";				
			}
			if (fieldstr.toLowerCase().indexOf("a0101") == -1) {//人员库指标未选择姓名
				fieldstr = ",a0101" + fieldstr;
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
		String where_n="";		
		String query=(String)this.getFormHM().get("query");
		if(!(query!=null&& "1".equals(query)))
			select_name="";
		InfoUtils infoUtils=new InfoUtils();
		select_name = PubFunc.hireKeyWord_filter_reback(select_name);
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
		boolean isPart=false;
		if(part_unit!=null&&part_unit.length()>0&&part_setid!=null&&part_setid.length()>0)
		{
			ReconstructionKqField reconstructionKqField=new ReconstructionKqField(this.getFrameconn());
			if(reconstructionKqField.checkFieldSave(userbase+part_setid,"i0000"))
			{
				isPart=true;
			}
		}
		String term_Sql="";
		/**2009.12.27注释掉，lzw*/
		//isPart=false;
		boolean isOrgLike=true;
		if(orglike!=null&& "0".equals(orglike))
			isOrgLike=false;
		
		term_Sql=infoUtils.getWhereSQLExists(this.getFrameconn(),this.userView,userbase,code,isOrgLike,kind,orgtype,personsortfield,personsort);
		
		
		String setprv=getEditSetPriv("A01");
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
				//System.out.println(this.userView.analyseFieldPriv(fieldItem.getItemid()));
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
		if(StringUtils.isNotEmpty(roster.trim()) && !"#".equals(roster)) {
			if(!this.isHaveMuster(roster)) {
				roster="no";
			}
		} else
		{
			roster="no";
		}
		
		this.getFormHM().put("browsefields",fields);	
		String check=(String)this.getFormHM().get("check");
		String checkSql="";
		if(check!=null&& "ok".equals(check))
		{
			String sexpr=(String)this.getFormHM().get("expr");
			String sfactor=(String)this.getFormHM().get("factor");
			String history=(String)this.getFormHM().get("history");
			if(sexpr!=null&&sexpr.length()>0&&sfactor!=null&&sfactor.length()>0&&history!=null&&history.length()>0)
			{
				boolean bhis=false;
				if(history!=null&& "1".equals(history))
		        	bhis=true;
				if(sfactor!=null&&sfactor.length()>0)
				sfactor=SafeCode.decode(sfactor);
				if(sexpr!=null&&sexpr.length()>0)
					sexpr=SafeCode.decode(sexpr);
				sexpr=PubFunc.keyWord_reback(sexpr);
				sfactor=PubFunc.keyWord_reback(sfactor);
				sfactor=PubFunc.reBackWord(sfactor);
				ArrayList fieldlist=new ArrayList();
				String chwhere="";
				/*if((!userView.isSuper_admin())){
					chwhere=userView.getPrivSQLExpression(sexpr+"|"+PubFunc.getStr(sfactor),userbase,false,false,true,new ArrayList()) ;
	        		0017957信息浏览，非su，高级查询，姓名等于兼职人员，查询不出人来，不对。
	        	}else{    */  
				boolean likeflag = false;
				if("1".equals((String)this.getFormHM().get("likeflag"))){
					likeflag=true;
				}
	        	FactorList factorslist=new FactorList(sexpr,PubFunc.getStr(sfactor),userbase,bhis,likeflag,true,1,userView.getUserId());
	        	fieldlist=factorslist.getFieldList();
	        	factorslist.setSuper_admin(userView.isSuper_admin());
	            chwhere=factorslist.getSqlExpression();
	            chwhere=infoUtils.getPrivSqlExists(chwhere, userbase+"A01", "C");
				checkSql="exists(select 1  "+chwhere+" and "+userbase+"A01.a0100=C.a0100)";
			}
		}
		String querycheckSql="";
		if(query!=null&& "1".equals(query))
		{
			ArrayList factorlist=(ArrayList)this.getFormHM().get("queryfieldlist");	
			String whereTrem=infoUtils.combine_ExistsSQL(this.userView,factorlist,querylike,userbase,"1");
			if(whereTrem!=null&&whereTrem.length()>0)
			{
				
				querycheckSql="(exists(select 1 "+whereTrem+" and "+userbase+"A01.a0100=Q.a0100))";		
			}	
			if("0".equals(browse_search_state))
			   this.getFormHM().put("isShowCondition", "none");	
			
			this.getFormHM().put("queryfieldlist", factorlist);
	    }else
	    {
	    	this.getFormHM().put("query", "");
	    	if("0".equals(browse_search_state))
	    	  this.getFormHM().put("isShowCondition", "none");		    	
	    }	
		
		StringBuffer buf=new StringBuffer();
		StringBuffer wherebuf=new StringBuffer();
		/**2009.12.27解决人员信息浏览中，，导出excel总是导出所有人问题lzw*/		
        String partSql = infoUtils.getPartSql(this.frameconn, userbase, kind, isOrgLike, code);
        if (StringUtils.isNotEmpty(partSql)) {
            StringBuffer I0000 = new StringBuffer();
            /** 兼职单位字段 */
            String unitField = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "unit");
            I0000.append("Case  When " + unitField + " is not null and I0000 is not null");
            I0000.append(" then I0000 else " + userbase + "A01.A0000 end AS a0000");
            buf.append(strsql.toString().replace(userbase + "A01.a0000 a0000", I0000));
            buf.append(" from (" + partSql + ") " + userbase + "A01");
        } else {
            buf.append(strsql.toString());
            buf.append(" from " + userbase + "A01");
        }			
        
		buf.append(" where exists(select a0100 from ("+term_Sql.toString().replace("A.*", "A.a0100")+")A where  "+userbase+"A01.a0100=A.a0100)");		
		wherebuf.append(" where exists(select a0100 from ("+term_Sql.toString()+")A where  "+userbase+"A01.a0100=A.a0100)");
		if(where_n!=null&&where_n.length()>0)
    	{
			buf.append(" "+where_n);
			wherebuf.append(" "+where_n);
    	}
		if(checkSql!=null&&checkSql.length()>0)
		{
			buf.append(" and "+checkSql);
			wherebuf.append(" and "+checkSql);
		} 
		if(querycheckSql!=null&&querycheckSql.length()>0)			 
		{
			 buf.append(" and "+querycheckSql);		
			 wherebuf.append(" and "+querycheckSql);
		}
		if(stock_cond!=null&&stock_cond.length()>0){
			buf.append(" and"+getCondWhere(userbase,stock_cond,querylike));//人员分类 liwc
			wherebuf.append(" and"+getCondWhere(userbase,stock_cond,querylike));//人员分类 liwc
		}
		//进行二次查询
		if("1".equalsIgnoreCase(querySecond)) {
		    
		    String tabldName = "t_sys_result";
		    if(userView.getStatus() == 4) {
		        buf.append(" and a0100 in (");
	            buf.append(" select obj_id from ");
	            buf.append(tabldName);
	            buf.append(" where UPPER(nbase)='");
	            buf.append(userbase.toUpperCase());
	            buf.append("' and UPPER(username)='"+userView.getUserName().toUpperCase());
	            buf.append("' and flag ='0')");
	            
	            wherebuf.append(" and a0100 in (");
	            wherebuf.append(" select obj_id from t_sys_result");
	            wherebuf.append(" where nbase='");
	            wherebuf.append(userbase);
	            wherebuf.append("' and UPPER(username)='"+userView.getUserName().toUpperCase());
	            wherebuf.append("' and flag ='0')");
		    } else {
		        tabldName = this.userView.getUserName() + userbase + "result";
		        buf.append(" and a0100 in (");
		        buf.append(" select a0100 from ");
		        buf.append(tabldName);
		        buf.append(")");
		        
		        wherebuf.append(" and a0100 in (");
		        wherebuf.append(" select a0100 from ");
		        wherebuf.append(tabldName);
		        wherebuf.append(")");
		        
		    }
		    
		    
		}
		
		orderby.append(" order by a0000");		
		//System.out.println("===================");
		//System.out.println(buf.toString());
		if(query!=null&& "1".equals(query)) {
		    String strSql = "select " + userbase + "a01.a0100" + buf.toString().substring(buf.indexOf(" from"));
		    if("1".equalsIgnoreCase(querySecond)) 
		        infoUtils.filterQueryResultsaveQueryResult("1",userbase,strSql,this.getFrameconn(),this.userView);
		    else
		        infoUtils.saveQueryResult("1",userbase,strSql,this.getFrameconn(),this.userView);
		}
		this.getFormHM().put("strsql",buf.toString());
	    this.getFormHM().put("ensql",SafeCode.encode(wherebuf.toString()));
	    this.userView.getHm().put("muster_excel_sql",SafeCode.encode(wherebuf.toString()));//liuy 2014-10-21 员工管理查询sql
		this.getFormHM().put("cond_str",""); 
		this.getFormHM().put("code",code);
		this.getFormHM().put("kind",kind);
		this.getFormHM().put("order_by",orderby.toString());
		this.getFormHM().put("columns",columns.toString());	
		this.getFormHM().put("roster",roster);
		this.getFormHM().put("check", "no");
		this.getFormHM().put("querySecond", querySecond);
		this.getFormHM().put("select_name", select_name);
		//登记表
		String cardid=searchCard("1");
		this.getFormHM().put("cardid",cardid);				
		if(isOrgLike)
			this.getFormHM().put("orglike", "1");
		else
			this.getFormHM().put("orglike", "0");
		/*FactorList factorslist=new FactorList("((1+2)*3*4)","E0902=010109`E0902=010111`A0101<>王光艳`AZ401=`",userbase,false,false,true,1,userView.getUserId());
		System.out.println(factorslist.getSqlExpression());*/
	}
	
	/**添加人员分类条件 liwc*/
	private String getCondWhere(String userbase,String stock_cond,String like) {
		String strwhere="";
		boolean blike=like.length()<1|| "0".equals(like)?false:true;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RecordVo vo=new RecordVo("lexpr");
        vo.setString("id",stock_cond);
        ArrayList fieldlist=new ArrayList();
        try {
        	vo=dao.findByPrimaryKey(vo);
        	String expr=vo.getString("lexpr");
            String factor=vo.getString("factor");
            factor=factor.replaceAll("\\$THISMONTH\\[\\]","当月");  /*兼容报表管理、常用查询*/
            String history=vo.getString("history");
            if(history==null|| "".equals(history))
                history="0";
            boolean bhis=false;
            if("1".equals(history))
            	bhis=true;    
            if((!userView.isSuper_admin()))
            {
                strwhere=userView.getPrivSQLExpression(expr+"|"+factor,userbase,bhis,blike,true,fieldlist);
            }
            else
            {
                FactorList factorlist=new FactorList(expr,factor,userbase,bhis ,blike,true,1,userView.getUserId());
                strwhere=factorlist.getSqlExpression();
            } 
		} catch (Exception e) {
			// TODO: handle exception
		}
		strwhere=" "+userbase+"A01.a0100 in (select "+userbase+"A01.A0100 "+strwhere+")";
		return strwhere;
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
	 /**
     * 根据信息群类别，查询定义的登记表格号
     * @param infortype =1人员 =2单位 3=职位 
     * @return
     */
    private String searchCard(String infortype)
    {
		 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		 String cardid="-1";
		 try
		 {
			 if("1".equalsIgnoreCase(infortype))
			 {
				 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"emp");
			 }
			 if("2".equalsIgnoreCase(infortype))
			 {
				 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"org");
			 }
			 if("3".equalsIgnoreCase(infortype))
			 {
				 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"pos");
			 }
			 if(cardid==null|| "".equalsIgnoreCase(cardid)|| "#".equalsIgnoreCase(cardid))
				 cardid="-1";
		 }
		 catch(Exception ex)
		 {
			 ex.printStackTrace();
		 }
		 return cardid;
    }    
    
    public boolean isHaveMuster(String tabid)
    {
    	boolean flag=true;
    	try
    	{
    		String sql = " select * from lname where tabid="+tabid;
    		String ManagePrivCodeValue = this.userView.getManagePrivCodeValue();
    		if(!this.userView.isSuper_admin()) {
    		    sql += " and (b0110=" + Sql_switcher.substr("'"+ManagePrivCodeValue+"'", "0", Sql_switcher.datalength("b0110")); 
    		    sql += " or b0110 like '" + ManagePrivCodeValue + "%'";
    		    sql += " or " + Sql_switcher.isnull("b0110", "'#'") + "='#')";
    		}
    		
    		ContentDAO dao = new ContentDAO(this.getFrameconn());
    		this.frowset=dao.search(sql);
    		if(this.frowset.next())
    		{
    			flag=true;
    		}
    		else
    		{
    			flag=false;
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return flag;
    }
}
