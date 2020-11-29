/*
 * Created on 2005-6-22
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.info;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.info.SortFilter;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hjsj.hrms.businessobject.sys.options.otherparam.OtherParam;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchInfoDataTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		Map map = (Map) this.getFormHM().get("requestPamaHM");
		String isAdvance = (String) map.get("isAdvance");
		map.remove("isAdvance");
		if (isAdvance == null ||isAdvance.length()<= 0) {
			isAdvance = "";
		}
		this.getFormHM().put("isAdvance", isAdvance);
		HashMap thm=this.getFormHM();	
		String userbase=(String)this.getFormHM().get("userbase");
		userbase = userbase==null||userbase.length()==0?"Usr":userbase;
		String code=(String)this.getFormHM().get("code"); 
		CheckPrivSafeBo checkPrivSafeBo=new CheckPrivSafeBo(this.frameconn,this.userView);
		userbase=checkPrivSafeBo.checkDb(userbase);
		code=checkPrivSafeBo.checkOrg(code, "");
		
//		yuxiaochun add program		
		String select_name=(String)this.getFormHM().get("select_name");
		select_name = PubFunc.hireKeyWord_filter_reback(select_name);
		String querylike=(String)this.getFormHM().get("querylike");	//模糊查询
		String orglike=(String)this.getFormHM().get("orglike");
		String personsort=(String) thm.get("personsort");
		String Sortfield="";
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
		String part_unit=(String)this.getFormHM().get("part_unit");
		String part_setid=(String)this.getFormHM().get("part_setid");
		String part_appoint=(String)this.getFormHM().get("part_appoint");
		String stock_cond=(String)this.getFormHM().get("stock_cond");//人员分类
		stock_cond=stock_cond==null|| "-1".equals(stock_cond)?"":stock_cond;
		try{
		if(personsort!=null&&personsort.length()>0&&!"All".equals(personsort)){
			OtherParam param=new OtherParam(this.getFrameconn());
		    Map  atMap=param.serachAtrr("/param/employ_type");
		    Sortfield=(String) atMap.get("field");			
		}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		String where_n="";
		String query=(String)this.getFormHM().get("query");
	
		if(!(query!=null&& "1".equals(query))) {
		    select_name="";
		    ArrayList queryfieldlist=(ArrayList)this.getFormHM().get("queryfieldlist");
		    String b_view_photo = (String) map.get("b_view_photo");
		    for(int i = 0; i < queryfieldlist.size(); i++){
	            FieldItem fi = (FieldItem) queryfieldlist.get(i);
	            if(fi == null)
	                continue;
	            
	            if(StringUtils.isEmpty(b_view_photo)){
	                fi.setValue("");
	                fi.setViewvalue("");
	            }
	        }
		}
		
		InfoUtils infoUtils=new InfoUtils();
		if(select_name!=null&&select_name.length()>0&&!"null".equals(select_name))
		{			
			String whereA0101=infoUtils.whereA0101(this.userView,this.getFrameconn(), userbase, select_name,querylike);
			if(whereA0101!=null&&whereA0101.length()>0)
				  where_n=" and "+whereA0101;
			this.formHM.put("p_select_name", select_name.trim());
			this.formHM.put("select_name", select_name.trim());
		}
		else
		{
			this.formHM.put("p_select_name", "");
			this.formHM.put("select_name", "");
		}
		
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String orgtype=(String)hm.get("orgtype");
		if(orgtype==null||orgtype.length()<=0)
			orgtype="org";

		String kind=(String)this.getFormHM().get("kind");
		if(kind==null||kind.length()<=0)
		if(code == null && !userView.isSuper_admin())
        { 
           code=userView.getManagePrivCodeValue();
         }
	
		if (kind == null) {
			kind = "2";
		}
		String manaprivCode = this.userView.getManagePrivCode();
		if("UM".equals(manaprivCode) && "2".equals(kind)){
			kind="1";
		}else if("@K".equals(manaprivCode)){
			kind="0";
		}
		StringBuffer orderby=new StringBuffer();
		orderby.append(" order by ");
		orderby.append("a0000");
		
		boolean isOrgLike=false;
		if(orglike!=null&& "1".equals(orglike))
			isOrgLike=true;
		String term_Sql=infoUtils.getWhereSQLExists(this.getFrameconn(),this.userView,userbase,code,isOrgLike,kind,orgtype,personsortfield,personsort);
		StringBuffer strsql=new StringBuffer();
		StringBuffer columns=new StringBuffer();
		strsql.append("select "+userbase+"A01.a0000 a0000,");
	    strsql.append(userbase);
		strsql.append("A01.A0100 a0100");
		columns.append("A0100,a0000");
		String fieldstr=new SaveInfo_paramXml(this.getFrameconn()).getInfo_paramNode("browser");
		if(fieldstr!=null&&fieldstr.length()>0)
		{
			 fieldstr=new SortFilter().getBrowseFields(fieldstr,userbase,this.getFrameconn(),this.userView);
			 if(fieldstr==null||fieldstr.length()<=0){				
					fieldstr=",B0110,E0122,E01A1,A0101,UserName";				
			 }
			 if (fieldstr.toLowerCase().indexOf("a0101") == -1) {//人员库指标未选择姓名
					fieldstr = ",a0101" + fieldstr;
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
        String flag=(String)hm.get("flag");
    	ArrayList infoFieldList=new ArrayList();
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
		String check=(String)this.getFormHM().get("check");		
		String checkSql="";
		if(check!=null&& "ok".equals(check)) {
			//String tablename=this.userView.getUserName()+userbase+"result";
			//strwhere=strwhere+" and "+userbase+"A01.a0100 in(select a0100 from "+tablename+")";
			String sexpr=(String)this.getFormHM().get("expr");
			String sfactor=(String)this.getFormHM().get("factor");
			String history=(String)this.getFormHM().get("history");
			if(sexpr!=null&&sexpr.length()>0&&sfactor!=null&&sfactor.length()>0&&history!=null&&history.length()>0)
			{
				if(sfactor!=null&&sfactor.length()>0)
					   sfactor=SafeCode.decode(sfactor);
					if(sexpr!=null&&sexpr.length()>0)
						sexpr=SafeCode.decode(sexpr);	
					sfactor=PubFunc.keyWord_reback(sfactor);
					sfactor=PubFunc.reBackWord(sfactor);
					sexpr=PubFunc.keyWord_reback(sexpr);
					boolean bhis=false;
					if(history!=null&& "1".equals(history))
			        	bhis=true;
					
					String chwhere="";
					/*if((!userView.isSuper_admin())){
						chwhere=userView.getPrivSQLExpression(sexpr+"|"+PubFunc.getStr(sfactor),userbase,false,false,true,new ArrayList()) ;
		        		0017957信息浏览，非su，高级查询，姓名等于兼职人员，查询不出人来，不对。
		        	}else{*/
					boolean likeflag = false;
					if("1".equals((String)this.getFormHM().get("likeflag"))){
						likeflag=true;
					}
		        	FactorList factorslist=new FactorList(sexpr,PubFunc.getStr(sfactor),userbase,bhis,likeflag,true,1,userView.getUserId());
		        	factorslist.setSuper_admin(userView.isSuper_admin());
		        	chwhere=factorslist.getSqlExpression();
		            chwhere=factorslist.getSqlExpression();
		            chwhere=infoUtils.getPrivSqlExists(chwhere, userbase+"A01", "C");
		     	    checkSql="exists(select 1  "+chwhere+" and "+userbase+"A01.a0100=C.a0100)";
			}
			this.getFormHM().put("generalsearch","1");//zgd 2014-7-1 信息浏览界面中的高级查询。1是，0否
		} else {
			if(query!=null&& "1".equals(query))
			{
				ArrayList factorlist=(ArrayList)this.getFormHM().get("queryfieldlist");
				String whereTrem=infoUtils.combine_ExistsSQL(this.userView,factorlist,querylike,userbase,"1");
				if(whereTrem!=null&&whereTrem.length()>0)
					checkSql="(exists(select 1 "+whereTrem+" and "+userbase+"A01.a0100=Q.a0100))";		
				
		    } else
		    	this.getFormHM().put("query", "");
		    	
			this.getFormHM().put("generalsearch","0");// zgd 2014-7-1 信息浏览界面中的高级查询
		}
		
		StringBuffer buf=new StringBuffer();
		StringBuffer wherebuf=new StringBuffer();
		String partSql = infoUtils.getPartSql(this.frameconn, userbase, kind, isOrgLike, code);
        if(StringUtils.isNotEmpty(partSql)) {
            StringBuffer I0000 = new StringBuffer();
            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
            /**兼职单位字段*/
            String unitField=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit"); 
            I0000.append("Case  When " + unitField + " is not null and I0000 is not null");
            I0000.append(" then I0000 else " + userbase + "A01.A0000 end AS a0000");
            buf.append(strsql.toString().replace(userbase+"A01.a0000 a0000", I0000));
            buf.append(" from (" + partSql + ") " + userbase + "A01");
        } else {
            buf.append(strsql.toString());
            buf.append(" from "+userbase+"A01");
        }
		
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
		if(stock_cond!=null&&stock_cond.length()>0){
			buf.append(" and"+getCondWhere(userbase,stock_cond,querylike));//人员分类 liwc
			wherebuf.append(" and"+getCondWhere(userbase,stock_cond,querylike));//人员分类 liwc
		}
		if("1".equals(query)){
			this.inforquery_exten(buf, querylike, userbase);
		}
		//System.out.println(strsql.toString()+" "+strwhere+" "+orderby.toString());
		//System.out.println(buf.toString());
		if(query!=null&& "1".equals(query))
		{
			infoUtils.saveQueryResult("1",userbase,buf.toString(),this.getFrameconn(),this.userView);
		}
		this.getFormHM().put("browsefields",fields);
		this.getFormHM().put("columns",columns.toString());			
	    this.getFormHM().put("strsql",buf.toString());
		this.getFormHM().put("cond_str","");
		if (kind == null) {
			kind = "2";
		}
		this.getFormHM().put("kind",kind);
		this.getFormHM().put("code",code);
		
		if("1".equals(kind))
		{
			this.getFormHM().put("deptparentcode", code);
			this.getFormHM().put("posparentcode",code);
		}else if("2".equals(kind))
		{
			this.getFormHM().put("orgparentcode", userView.getManagePrivCodeValue());
			this.getFormHM().put("deptparentcode",code);
			this.getFormHM().put("posparentcode",code);
		}else if("0".equals(kind))
		{
			this.getFormHM().put("posparentcode", code);
		}
		this.getFormHM().put("order_by",orderby.toString());
		this.getFormHM().put("Sortfield",Sortfield);
		this.getFormHM().put("check", "no");
		if(isOrgLike)
			this.getFormHM().put("orglike", "1");
		else
			this.getFormHM().put("orglike", "0");
		String setprv=userView.analyseTablePriv("A01");
		this.getFormHM().put("setprv",setprv);
		
		String fromwhere = buf.substring(buf.indexOf("from"));
		userView.getHm().put("staff_sql",fromwhere);
		
		String multimedia_file_flag = "0";
		List infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);//获得所有权限的子集
		for(int p=0;p<infoSetList.size();p++) {
			FieldSet fieldset=(FieldSet)infoSetList.get(p);
			//是否有A01权限
			if("A01".equalsIgnoreCase(fieldset.getFieldsetid())) {
				multimedia_file_flag = fieldset.getMultimedia_file_flag();
				break;
			}
		}
		this.getFormHM().put("multimedia_file_flag",multimedia_file_flag);
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
	
	private void inforquery_exten(StringBuffer buf,String querylike,String userbase) throws GeneralException{
		String createtimestart = (String)this.getFormHM().get("createtimestart");
		String createtimeend = (String)this.getFormHM().get("createtimeend");
		String createusername = (String)this.getFormHM().get("createusername");
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		if(createtimestart!=null&&createtimestart.length()>0&&createtimeend!=null&&createtimeend.length()>0){
			String s_str_date=createtimestart.replaceAll("\\.","-");
	        String e_str_date=createtimeend.replaceAll("\\.","-");
	        try{
	        	Date s_date=sdf.parse(s_str_date);
	        	Date e_date=sdf.parse(e_str_date); 
	        }catch(Exception e){
	        	throw new GeneralException("输入的日期格式错误或范围不完整，请重新输入！");
	        }
	        buf.append(" and "+Sql_switcher.charToDate(Sql_switcher.dateToChar(userbase+"A01.createtime","yyyy-MM-dd"))+" between "+Sql_switcher.dateValue(s_str_date)+" and "+Sql_switcher.dateValue(e_str_date));
		}else if(createtimestart!=null&&createtimestart.length()>0){
			String s_str_date=createtimestart.replaceAll("\\.","-");
	        try{
	        	Date s_date=sdf.parse(s_str_date);
	        }catch(Exception e){
	        	throw new GeneralException("输入的日期格式错误或范围不完整，请重新输入！");
	        }
	        buf.append(" and "+Sql_switcher.charToDate(Sql_switcher.dateToChar(userbase+"A01.createtime","yyyy-MM-dd"))+">="+Sql_switcher.dateValue(s_str_date));
		}else if(createtimeend!=null&&createtimeend.length()>0){
	        String e_str_date=createtimeend.replaceAll("\\.","-");
	        try{
	        	Date e_date=sdf.parse(e_str_date);
	        }catch(Exception e){
	        	throw new GeneralException("输入的日期格式错误或范围不完整，请重新输入！");
	        }
	        buf.append(" and "+Sql_switcher.charToDate(Sql_switcher.dateToChar(userbase+"A01.createtime","yyyy-MM-dd"))+"<="+Sql_switcher.dateValue(e_str_date));
		}
		if(createusername!=null&&createusername.length()>0){
			boolean blike=querylike.length()<1|| "0".equals(querylike)?false:true;
			if(blike){
				buf.append(" and "+userbase+"A01.createusername like '%"+createusername+"%'");
			}else{
				buf.append(" and "+userbase+"A01.createusername='"+createusername+"'");
			}
		}
	}
	
	private String getPartSql(String nbase, String kind, boolean isLike, String code) {
	    if(StringUtils.isEmpty(code))
	        return "";
	    
	    StringBuffer partSql = new StringBuffer();
        Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
        String flag = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");
        /**兼职单位字段*/
        String unitField=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit"); 
        //兼职部门
        String deptField=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"dept");
        //兼职职位
        String posField=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos");
        /**任免标识字段*/
        String appointField=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");
        if(StringUtils.isEmpty(flag) || "false".equalsIgnoreCase(flag))
            return "";
        
        String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
        if(StringUtils.isEmpty(setid) || DataDictionary.getFieldSetVo(setid) == null 
                ||  "0".equalsIgnoreCase(DataDictionary.getFieldSetVo(setid).getUseflag()))
            return "";
            
        FieldItem appointFieldItem = DataDictionary.getFieldItem(appointField);
        if(appointFieldItem==null || "0".equalsIgnoreCase(appointFieldItem.getUseflag()))
            return "";
        
        String field = "B0110";
        if("2".equals(kind))
            field = "B0110";
        else if("1".equals(kind))
            field = "e0122";  
        else if("0".equals(kind))
            field = "e01a1";
        
        partSql.append("select " + nbase + "A01.*,I0000," + unitField);
        partSql.append(" from " + nbase + "A01");
        partSql.append(" left join ");
        partSql.append("(select I0000,a0100," + unitField + " from " + nbase + setid + " A  where I9999 = (");
        partSql.append("select max(I9999) from " + nbase + setid + " B");
        partSql.append(" where A.A0100 = B.A0100 and (");
        if(isLike)
            partSql.append(unitField + " like '" + code + "%' or " + deptField + " like '" + code + "%' or " + posField + " like '" + code + "%')");
        else
            partSql.append(unitField + "='" + code + "' or " + deptField + "='" + code + "' or " + posField + "='" + code + "')");
        
        partSql.append(" and " + appointField + " = '0'");
        partSql.append("and not A0100 in (select  A0100 from USRA01 where " + field + " LIKE '" + code + "%'))) P");
        partSql.append(" on " + nbase + "a01.a0100=P.a0100");
        
        return partSql.toString();
    }
}
