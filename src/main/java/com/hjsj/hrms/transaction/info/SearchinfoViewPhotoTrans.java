/*
 * Created on 2005-6-22
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.info;

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
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchinfoViewPhotoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {	
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		hm.remove("b_view_photo");
		String userbase=(String)this.getFormHM().get("userbase");
		String code=(String)this.getFormHM().get("code"); 
		String kind=(String)this.getFormHM().get("kind");
		String orgtype = (String)hm.get("orgtype");
		//显示照片是否按查询结果显示，目前只有员工管理信息浏览和自助服务员工信息-信息浏览页面使用
		String isResult = (String)hm.get("isResult");
		String select_name=(String)this.getFormHM().get("select_name");		
		//当获取页面显示的查询姓名为空时，获取“p_select_name”参数的值
		if(StringUtils.isEmpty(select_name))
			select_name = (String)this.getFormHM().get("p_select_name");
		
		String querylike=(String)this.getFormHM().get("querylike");	//模糊查询
		if(orgtype==null||orgtype.length()<=0)
			orgtype="org";
		
		String orglike=(String)this.getFormHM().get("orglike");
		this.getFormHM().put("orgtype", orgtype);
		String part_unit=(String)this.getFormHM().get("part_unit");
		String part_setid=(String)this.getFormHM().get("part_setid");
		String part_appoint=(String)this.getFormHM().get("part_appoint");
		String stock_cond=(String)this.getFormHM().get("stock_cond");//人员分类
		stock_cond=stock_cond==null|| "-1".equals(stock_cond)?"":stock_cond;
        String strwhere="";
		StringBuffer wheresql=new StringBuffer();
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
		/*if("infoself".equalsIgnoreCase(flag))
		{
			infoFieldList=userView.getPrivFieldList("A01",0);   //获得当前子集的所有属性
		}
	    else
		{	
			infoFieldList=userView.getPrivFieldList("A01");      //获得当前子集的所有属性
		}*/
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
		if(roster.trim().length()>0&&!"#".equals(roster))
		{
			if(!this.isHaveMuster(roster))
			{
				roster="no";
			}
		}
		else
		{
			roster="no";
		}
		if(!this.userView.isSuper_admin()){
			String temp=this.userView.getResourceString(4);
			if(temp.indexOf(roster)<0){
				roster="no";
			}
		}
		//System.out.println(union_Sql.toString());
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
				sfactor=PubFunc.keyWord_reback(sfactor);
				sfactor=PubFunc.reBackWord(sfactor);
				sexpr=PubFunc.keyWord_reback(sexpr);
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
		String whereTrem = "";
		if(query!=null&& "1".equals(query)) {
		    if("1".equalsIgnoreCase(isResult)) {
		        if(this.userView.getStatus() == 4)
		            querycheckSql = "(exists(select 1 from t_sys_result Q where " + userbase +"A01.a0100=Q.obj_id))";
		        else 
		            querycheckSql = "(exists(select 1 from " + this.userView.getUserName() + userbase
		            + "result Q where " + userbase +"A01.a0100=Q.a0100))";
		        
		    } else {
		        ArrayList factorlist=(ArrayList)this.getFormHM().get("queryfieldlist");							
		        whereTrem=infoUtils.combine_ExistsSQL(this.userView,factorlist,querylike,userbase,"1");
		        if(whereTrem!=null&&whereTrem.length()>0) {
		            querycheckSql="(exists(select 1 "+whereTrem+" and "+userbase+"A01.a0100=Q.a0100))";		
		        } else if(StringUtils.isEmpty(check) || "no".equals(check)) {
		            String tabldName = userView.getUserName() + userbase + "result";
		            if(userView.getStatus()==4)
		                tabldName = "t_sys_result";
		            
		            querycheckSql = "(exists(select 1 from " + tabldName + " Q where " + userbase +"A01.a0100=Q.a0100))";
		        }	
		    }
		    
			this.getFormHM().put("isShowCondition", "none");	
	    }else {
	    	this.getFormHM().put("query", "");
	    	this.getFormHM().put("isShowCondition", "none");	
	    }	
		
		StringBuffer buf=new StringBuffer();
		StringBuffer wherebuf=new StringBuffer();
		/**2009.12.27解决人员信息浏览中，，导出excel总是导出所有人问题lzw*/
		buf.append(strsql.toString());
		buf.append(" from "+userbase+"A01");					
		buf.append(" where exists(select a0100 from ("+term_Sql.toString()+")A where  "+userbase+"A01.a0100=A.a0100)");				
		wherebuf.append(" where exists(select a0100 from ("+term_Sql.toString()+")A where  "+userbase+"A01.a0100=A.a0100)");
		if(where_n!=null&&where_n.length()>=0)
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
		
		orderby.append(" order by a0000");
					
		//System.out.println("===================");
		//System.out.println(buf.toString());
		if(query!=null && "1".equals(query) && (StringUtils.isNotEmpty(whereTrem) || "ok".equals(check))) {
			infoUtils.saveQueryResult("1",userbase,buf.toString(),this.getFrameconn(),this.userView);
		}
		
		this.getFormHM().put("strsql",buf.toString());
	    this.getFormHM().put("ensql",SafeCode.encode(wherebuf.toString()));
		this.getFormHM().put("cond_str",""); 
		this.getFormHM().put("code",code);
		this.getFormHM().put("kind",kind);
		this.getFormHM().put("order_by",orderby.toString());
		this.getFormHM().put("columns",columns.toString());	
		this.getFormHM().put("roster",roster);
		this.getFormHM().put("check", check);
		//登记表
		String cardid=searchCard("1");
		this.getFormHM().put("cardid",cardid);				
		if(isOrgLike)
			this.getFormHM().put("orglike", "1");
		else
			this.getFormHM().put("orglike", "0");		
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
                strwhere=userView.getPrivSQLExpression(expr+"|"+factor,userbase,false,blike,true,fieldlist);
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
