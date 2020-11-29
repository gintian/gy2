package com.hjsj.hrms.utils.components.selectfield.transaction;

import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hjsj.hrms.businessobject.ykcard.TSyntax;
import com.hjsj.hrms.module.jobtitle.experts.businessobject.ExpertsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Factor;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.util.DateUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

/**
 * 显示查询结果-人员
 * @author chent
 *
 */
public class ShowResultTrans extends IBusiness{

	
	/**是否跨库查询*/
	private boolean bm_dbase=false;	
	/**历史记录查询标识
	 * =0不对历史记录
	 * =1历史记录
	 * */
	// 是否需要权限限制
    String personPriv = "";
    String isFilterSelectedExpert="1";//职称评审 是否过滤掉已选专家 0不过滤 1过滤
	private String history="0";
	private String onlyname="";
	private String onlynameValid="";
	public void execute() throws GeneralException {
		
		try
		{
			
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			this.onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
			this.onlynameValid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
			
			String expr = (String)this.getFormHM().get("expr");//条件表达式1|E0122=010105`  1*2|E0122=010105`A0107=1`
			expr=expr!=null&&expr.trim().length()>0?expr:"";
			expr=SafeCode.decode(expr);
			
			/**模块号；0:薪资类别,1:人事异动,2:所得税管理结构设置...9 职称评审*/
			String imodule=(String)this.getFormHM().get("imodule");
			
			String query_type=(String)this.getFormHM().get("query_type");//1通用，2简单查询
			 /**查询类型，简单查询或通用查询*/
		        if(query_type==null|| "".equals(query_type))
		            query_type="2";
			
			String tabid=(String)this.getFormHM().get("tabid");
			tabid=tabid!=null&&tabid.length()>0?tabid:"";
			
			/** type: 1为人员库(default), 2单位, 3岗位, 9基准岗位*/        
	        String type=(String)this.getFormHM().get("type");
	        if(type==null|| "".equals(type))
	        	type="1";

	        if("9".equalsIgnoreCase(imodule)&&StringUtils.isNotBlank((String)this.getFormHM().get("isFilterSelectedExpert")))
				isFilterSelectedExpert=(String)this.getFormHM().get("isFilterSelectedExpert");

			ArrayList dataList=(ArrayList)this.getFormHM().get("dataList");
			
			String checkValues = (String)this.getFormHM().get("checkValues");//选择的人员库和查询类型字符串，如：dbName=Usr&dbName=Ret&like=like&history=history&second=second
			checkValues=checkValues!=null&&checkValues.trim().length()>0?checkValues:"";
			String checkArray[]=checkValues.split("&");
			String[] dbpre=new String[checkArray.length];
			String like="0";
			String result="0";
			for(int i=0;i<checkArray.length;i++){
				if(checkArray[i].indexOf("dbName")!=-1){
					int a=checkArray[i].indexOf("=");
					dbpre[i]=checkArray[i].substring(a+1);
				}else if(checkArray[i].indexOf("like")!=-1){
					like="1";
				}else if(checkArray[i].indexOf("history")!=-1){
					history="1";
				}else if(checkArray[i].indexOf("search_result")!=-1){
					result="1";
				}
			}
			cat.debug("history="+history);
			String filter_factor=(String)this.getFormHM().get("filter_factor");//通用查询才有
			filter_factor=SafeCode.decode(filter_factor);
			
			
			//ArrayList factorlist=(ArrayList)this.getFormHM().get("factorlist");////[{fieldname=e0122,hz=部门,codeid=UM,oper=＝,log=*,value=010105,hzvalue=战略发展部}]
			ArrayList factorlist=MorphdynabeanToFactor(dataList,type,expr);//[{fieldname=e0122,hz=部门,codeid=UM,oper==,log=*,value=010105,hzvalue=部门}]
	        if(factorlist==null)
	            return;
	        
//	        history=(String)this.getFormHM().get("history");
//	        String like=(String)this.getFormHM().get("like");
//	        String result=(String)this.getFormHM().get("result");
	        // 是否需要权限限制
	        personPriv = (String) this.getFormHM().get("personPriv");
	        this.getFormHM().remove("personPriv");
	        personPriv = personPriv == null ? "1" : personPriv;
	        
	        String chpriv=(String)this.getFormHM().get("chpriv");  //前台传参为priv=0,用于不控制人员管理范围  ----人事异动
	        if(chpriv!=null&& "0".equals(chpriv))
	        	this.personPriv="0";
	        
	        if((dbpre==null||dbpre.length==0)&& "1".equals(type))
	        	throw new GeneralException(ResourceFactory.getProperty("errors.static.notdbname")); 
	        /**跨库标识*/
	        if(dbpre!=null&&dbpre.length>1)
	        	this.bm_dbase=true;        
	        StringBuffer sfactor=new StringBuffer();
	        StringBuffer sexpr=new StringBuffer();
	        /**合成通用的表达式*/
	        combineFactor(factorlist, like, sfactor, sexpr);
	        String sfactortemp = PubFunc.keyWord_reback(sfactor.toString());
	        
	        //解决岗位说明书查询不能按照单位查询，按照单位查询是传入b0110需替换为e0122
	        if("3".equals(type) && sfactortemp.indexOf("B0110")!=-1){
	        	sfactortemp = sfactortemp.replaceAll("B0110", "E0122");
	        	sfactortemp = sfactortemp.replaceAll("b0110", "E0122");
	        }
	        
	        sfactor.setLength(0);
	        sfactor.append(sfactortemp);
	        String sexprtemp = PubFunc.keyWord_reback(sexpr.toString());
	        sexpr.setLength(0);
	        sexpr.append(sexprtemp);
	        /**通用查询时，表达式因子按用户填写进行分析处理*/
	        if("1".equals(query_type))
	        {
	        	String expression=expr.substring(0,expr.indexOf("|"));
	            sexpr.setLength(0);
	            if(expression==null|| "".equals(expression))
	               throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistexpr"),"",""));
	            /**为了分析用*/
	            if(!isHaveExpression(expression,factorlist.size()))
	                throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistfactor"),"",""));
	            expression=expression.replaceAll("!","-");
	            TSyntax syntax=new TSyntax();
	            if(!syntax.Lexical(expression))
	                throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.expression"),"",""));
	            if(!syntax.DoWithProgram())
	                throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.expression"),"",""));
	            sexpr.append(expression);
	        }
	        cat.debug("expr="+sexpr.toString());
	        cat.debug("factor="+sfactor.toString());
	        boolean bhis=false;
	        boolean bresult=true;
	        boolean blike=false;
	        if("1".equals(history))
	        	bhis=true;
	        if("1".equals(result))
	        	bresult=false;
	        if("1".equals(like))
	        	blike=false;
	        
	        
	    	String sql = combine_SQL(type, dbpre, sfactor, sexpr, bhis, bresult,blike,filter_factor,imodule);
	    	sql = sql.replaceAll("\"","\\\\\"");
	    	
	    	ArrayList<ColumnsInfo> columnList = this.getColumnList();
			
			TableConfigBuilder builder = new TableConfigBuilder( "experts_picker_00001", columnList, "experts_picker", userView, this.getFrameconn());
			builder.setDataSql(sql);
			builder.setOrderBy("order by A0000");
			builder.setTitle("");
			builder.setAutoRender(false);
			builder.setSetScheme(false);
			builder.setLockable(true);
			builder.setTableTools(null);//xiegh  职称评审  条件引入  查询结果  有tollbar  导致界面不太美观 bug : 28893 date : 20170710 
			builder.setSelectable(true);
			builder.setEditable(false);
			builder.setPageSize(20);
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());
	        
	        
	        
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 根据条件组成sql
	 * @param type
	 * @param dbpre
	 * @param sfactor
	 * @param sexpr
	 * @param bhis
	 * @param bresult
	 * @param blike
	 * @param filter_factor
	 * @param imodule
	 * @return
	 * @throws GeneralException
	 */
	private String combine_SQL(String type, String[] dbpre, StringBuffer sfactor, StringBuffer sexpr, boolean bhis, boolean bresult,boolean blike,String filter_factor,String imodule) throws GeneralException {
		
		StringBuffer strSql = new StringBuffer();  
		
		ContentDAO dao = null;
		RowSet rs = null;
		try{
			String tabId=(String)this.getFormHM().get("tabId");
	        if(tabId==null)
	        	tabId="";
	        boolean kqFlag=false;
	        if (!"".equals(tabId)){
	        	TemplateTableParamBo parambo=new TemplateTableParamBo(this.frameconn);
	        	kqFlag= parambo.isKqTempalte(Integer.parseInt(tabId));//判断当前模板是否定义了考勤参数
	        }
			String strWhere=null;
	    	String strSelect=null;
	    	ArrayList fieldlist=new ArrayList();
	    	/**加权限过滤*/
			ArrayList alUsedFields=null;
			if(filter_factor!=null && !"null".equalsIgnoreCase(filter_factor) &&  filter_factor.trim().length()>0)
		    { 
			  alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			  /**
			   *保持和以前的程序兼容，因为先前单位编码和职位编码、
			   *单位名称和职位名称未统一起来 
			   */
			  FieldItem item=new FieldItem();
			  item.setItemid("b0110");
			  item.setCodesetid("UN");
			  item.setItemdesc("单位编码");
			  item.setItemtype("A");
			  item.setFieldsetid("A01");
			  item.setUseflag("2");
			  item.setItemlength(30);
			  item.setItemlength(30);
			  alUsedFields.add(item);
	
			  item=new FieldItem();
			  item.setItemid("e01a1");
			  item.setCodesetid("@K");
			  item.setItemdesc("职位编码");
			  item.setItemtype("A");
			  item.setFieldsetid("A01");
			  item.setUseflag("2");
			  item.setItemlength(30);
			  item.setItemlength(30);
			  alUsedFields.add(item);		  
		    }
			//职称单独处理sql  haosl 2017-07-11
			if ("9".equals(imodule) && "1".equals(type)) {
				StringBuilder tableSql = new StringBuilder();
				for (int i = 0; i < dbpre.length; i++) {
					if (StringUtils.isBlank(dbpre[i]))
						continue;
					FactorList factorlist = new FactorList(sexpr.toString(), PubFunc.getStr(sfactor.toString()), dbpre[i], false, false, true, 1, userView.getUserName(), "");
					strWhere = factorlist.getSqlExpression();
					String b0110 = userView.getUnitIdByBusi(imodule);// 取得所属单位
					if (b0110.split("`")[0].length() > 2) {// 组织机构去除UN、UM后不为空：取本级，下级。为空：最高权限
						strWhere += this.getB0110Sql_down(b0110, dbpre[i]);
					}
					strSelect = getSelectString(fieldlist, type, dbpre[i]);
					tableSql.append(strSelect);
					tableSql.append(strWhere);
					if (filter_factor != null && !"undefined".equalsIgnoreCase(filter_factor) && !"null".equalsIgnoreCase(filter_factor) && filter_factor.trim().length() > 0) {
						tableSql.append(" and " + getFilterSQL(userView, dbpre[i], alUsedFields, this.getFrameconn(), filter_factor));
					}
					tableSql.append(" UNION ");
				}
				tableSql.setLength(tableSql.length() - 7);

				StringBuilder exceptStr = new StringBuilder();
				if ("1".equalsIgnoreCase(isFilterSelectedExpert)) {
					// 排除现有已选专家

					ExpertsBo bo = new ExpertsBo(this.frameconn, this.userView);
					StringBuilder sql = new StringBuilder("select * from ");
					sql.append(bo.getSelectSql());
					dao = new ContentDAO(this.frameconn);
					rs = dao.search(sql.toString());
					while (rs.next()) {
						String nbase = rs.getString("nbase");
						String a0100 = rs.getString("a0100");
						exceptStr.append("'" + nbase + a0100 + "',");
					}
					if (exceptStr.length() > 0) {
						exceptStr.deleteCharAt(exceptStr.length() - 1);
					}
				}
				strSql.append("select * From (");
				strSql.append(tableSql);
				strSql.append(") A ");
				if (exceptStr.length() > 0) {
					strSql.append("where nbasea0100 not in (");
					strSql.append(exceptStr);
					strSql.append(")");
				}

        	    
			}else{//其他模块走这里(处职称外)  haosl 
	    	   if((!userView.isSuper_admin())&& "1".equals(type))
	           {
		           	for(int i=0;i<dbpre.length;i++)
		           	{   
		           		if(StringUtils.isBlank(dbpre[i]))
		           			continue;
		           		fieldlist=new ArrayList();
		           		FactorList factorlist = new FactorList(sexpr.toString(), PubFunc.getStr(sfactor.toString()), dbpre[i],
		   						false, false, true, 1, userView.getUserName(),"");	
		           		if ("0".equalsIgnoreCase(personPriv)&&dbpre[i]!=null) {
		           			strWhere = factorlist.getSqlExpression();
		           		} else if(dbpre[i]!=null){
		                       if (kqFlag&& userView.getKqManageValue() != null && !"".equals(userView.getKqManageValue())){
		                       	strWhere = factorlist.getSqlExpression();
		                       	String kqManageValue = userView.getKqManageValue();
		                       	String codeType=kqManageValue.substring(0,2).toUpperCase();
		                       	String codeId=kqManageValue.substring(2);
		                       	String fld="";
		                       	if ("UN".equals(codeType)){
		                       		fld="b0110";
		                       	}
		                       	else if ("UM".equals(codeType)){
		                       		fld="e0122";
		                       	}
		                       	else if ("@K".equals(codeType)){
		                       		fld="e01a1";
		                       	}
		                       	strWhere=strWhere+" and "+fld+" like '"+codeId+"%'";
		                       }	
		           			else {
		       	     	    	strWhere=userView.getPrivSQLExpression(sexpr.toString()+"|"+PubFunc.getStr(sfactor.toString()),dbpre[i],bhis,bresult,fieldlist);
		           			}
		           		}
		             	    strSelect=getSelectString(fieldlist,type,dbpre[i]);
		           	    strSql.append(strSelect);
		           	    strSql.append(strWhere);  
		           	    //wlh修改添加检索过滤
		           	    if(filter_factor!=null && !"undefined".equalsIgnoreCase(filter_factor)&&!"null".equalsIgnoreCase(filter_factor) &&  filter_factor.trim().length()>0)
		   			    { 
		   			 	    strSql.append(" and " + getFilterSQL(userView,dbpre[i],alUsedFields,this.getFrameconn(),filter_factor));
		   			    }
		           	    strSql.append(" UNION ");         		
		           	}
	        	    strSql.setLength(strSql.length()-7);
	        	    
	           }
	    	   else
	    	   {
	               if("1".equals(type))
	               {
	            	   if(dbpre==null||dbpre.length==0)
	            		   throw new GeneralException(ResourceFactory.getProperty("errors.static.notdbname"));
	            	   for(int i=0;i<dbpre.length;i++)
	            	   {
	            		   if(dbpre[i]!=null){
	   	             	   FactorList factor_bo=new FactorList(sexpr.toString(),PubFunc.getStr(sfactor.toString()),dbpre[i],bhis,blike,bresult,Integer.parseInt(type),userView.getUserId());
	     	     	    	   strWhere=factor_bo.getSqlExpression();
	   	             	   fieldlist=factor_bo.getFieldList();
	   	             	   strSelect=getSelectString(fieldlist,type,dbpre[i]);
	   	             	   strSql.append(strSelect);
	   	             	   strSql.append(strWhere); 
	   	             	  //wlh修改添加检索过滤
	   	           	      if(filter_factor!=null && !"undefined".equalsIgnoreCase(filter_factor)&&!"null".equalsIgnoreCase(filter_factor) &&  filter_factor.trim().length()>0)
	   	   			      { 
	   	           	    	//  strSql.append(" and "+dbpre[i]+"A01."+dbnamebo.getComplexCond(this.userView, dbpre[i], alUsedFields, filter_factor, IParserConstant.forPerson).trim() /*getFilterSQL(userView,dbpre[i],alUsedFields,this.getFrameconn(),filter_factor)*/);
	   	   			        //邓灿修改 人事异动中 上述程序会将考核结果表里的数据冲掉，造成考核结果表与检索条件不能同时用
	   	           	    	 strSql.append(" and " + getFilterSQL(userView,dbpre[i],alUsedFields,this.getFrameconn(),filter_factor));
	   	   			      }
	   	             	   strSql.append(" UNION ");
	            		   }
	            	   }
	            	   strSql.setLength(strSql.length()-7);
	               }
	               else
	               {
	                  String backdate=DateUtils.format(new Date(), "yyyy-MM-dd");
	            	   FactorList factor_bo=new FactorList(sexpr.toString(),PubFunc.getStr(sfactor.toString()),"",bhis,blike,bresult,Integer.parseInt(type),userView.getUserId());
	            	   strWhere=factor_bo.getSqlExpression();
	            	   fieldlist=factor_bo.getFieldList();         	   
	            	   strSelect = getSelectString(fieldlist, type, "");
	            	   //添加控制权限
	            	   StringBuffer privStr=new StringBuffer("");
	   				if (!userView.isSuper_admin()) 
	   				{					
	   					/**
	   					 * cmq changed at 20121002 单位和岗位权限范围规则
	   					 * 业务范围-操作单位-人员范围优先级
	   					 */
	   					String codeValue=userView.getUnitPosWhereByPriv("codeitemid");//userView.getManagePrivCodeValue();					
	   					if ("3".equals(type))//职位
	   					{
	   						String conditionSql = " select codeitemid from organization  where codesetid='@K' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date and  "+ codeValue;
	   						privStr.append(" and K01.e01a1 in (" + conditionSql + " )");
	   					} 
	   					else if ("2".equals(type))//单位
	   					{
	
	   						String conditionSql = " select codeitemid from organization  where ( codesetid='UN' or codesetid='UM') and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date and  "
	   							+ codeValue;
	   						privStr.append(" and B01.b0110 in (" + conditionSql + " )");
	   						
	   					}
	   				}
	   				else
	   				{
	   					
	   					if ("3".equals(type)) {
	   						String conditionSql = " select codeitemid from organization  where codesetid='@K' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date";
	   						privStr.append(" and K01.e01a1 in (" + conditionSql + " )");
	   					} 
	   					else if ("2".equals(type)) {
	                       //单位
	   						String conditionSql = " select codeitemid from organization  where ( codesetid='UN' or codesetid='UM') and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ";
	   						privStr.append(" and B01.b0110 in (" + conditionSql + " )");
	   						
	   					}
	   				}
	
	   				// 基准岗位
	   				if("9".equals(type)) {
	   				    String codesetid="";
	   	                RecordVo constantuser_vo = ConstantParamter.getRealConstantVo("PS_C_CODE");
	   	                if (constantuser_vo != null)
	   	                    codesetid = constantuser_vo.getString("str_value");
	                       String conditionSql = " select codeitemid from codeitem  where ( codesetid='"+codesetid+"') and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ";
	                       privStr.append(" and H01.H0100 in (" + conditionSql + " )");
	   				}
	   				//System.out.println(strSelect + "  " + strWhere);
	   				strSql.append(strSelect);
	            	    strSql.append(strWhere);  
	            	    strSql.append(privStr);
	            	   
	               }
	           }
	       }
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			
		}
       
        return strSql.toString();
	}
	/*
	 *  //wlh修改添加检索过滤
	 */
	private String getFilterSQL(UserView uv,String BasePre,ArrayList alUsedFields,Connection conn1,String filter_factor)
	{
		String sql=" (1=1) ";
		try{
			filter_factor=filter_factor.replaceAll("@","\"");
			ContentDAO dao=new ContentDAO(conn1);
			//this.filterfactor="性别 <> '1'";
			int infoGroup = 0; // forPerson 人员
			int varType = 8; // logic								
			String whereIN=InfoUtils.getWhereINSql(uv,BasePre);
			whereIN="select a0100 "+whereIN;							
			YksjParser yp = new YksjParser( uv ,alUsedFields,
					YksjParser.forSearch, varType, infoGroup, "Ht", BasePre);
			YearMonthCount ymc=null;							
			yp.run_Where(filter_factor, ymc,"","", dao, whereIN,conn1,"A", null);
			
			sql=BasePre+"A01.a0100 in (select  a0100 from "+yp.getTempTableName()+" where "+yp.getSQL()+" )";
		//	sql=yp.getSQL();
		}catch(Exception e)
		{
			e.printStackTrace();
		}	
		return sql;
	}
	private String getSelectString(ArrayList list,String strInfkind,String dbpre)
    {
        ArrayList resultlist=privFieldList(list,strInfkind);
        /**同样根据是否填定查询值，取得查询指标*/
        String fields=query_Field(resultlist);
        
        StringBuffer strsql=new StringBuffer();
        if("2".equals(strInfkind))
        {
	        strsql.append("select distinct b01.b0110 ");
        }
        else if("3".equals(strInfkind))
        {
	        strsql.append("select distinct k01.e01a1 ");        	
        }
        else if("9".equals(strInfkind))  // 基准岗位
        {
	        strsql.append("select distinct h01.h0100 ");        	
        }
        else
        {
	        strsql.append("select distinct a0000,");
	        strsql.append(dbpre);
	        strsql.append("a01.a0100 ,'");
	        strsql.append(dbpre);
	        strsql.append("' as dbase,");
	        strsql.append(dbpre);        
	        strsql.append("a01.b0110 b0110,");
	        strsql.append(dbpre);  
	        strsql.append("a01.e0122 e0122,");
	        strsql.append(dbpre);
	        strsql.append("a01.e01a1 e01a1, "); 
	        strsql.append(dbpre);
	        strsql.append("a01.a0101 a0101, "); 
	        strsql.append("'" + dbpre + "'"+Sql_switcher.concat());
	        strsql.append(dbpre);
	        strsql.append("a01.a0100 as nbasea0100 ");
	        if(StringUtils.isNotEmpty(this.onlyname) 
	        		&& !"b0110".equalsIgnoreCase(this.onlyname)
	        		&& !"e0122".equalsIgnoreCase(this.onlyname)
	        		&& !"e01a1".equalsIgnoreCase(this.onlyname)
	        		&& !"a0101".equalsIgnoreCase(this.onlyname)){
	        	strsql.append(",");  
	        	strsql.append(dbpre);  
	        	strsql.append("a01."+this.onlyname+" ");
	        }
        }
        if(fields==null|| "".equals(fields))
        {
            strsql.append(" ");
        }
        else
        {
        	strsql.append(",");
        	strsql.append(fields);        	
        }
        strsql.append(" ");
        return strsql.toString();
}	
	private void combineFactor(ArrayList factorlist, String like, StringBuffer sfactor, StringBuffer sexpr) {
		for(int i=0;i<factorlist.size();i++)
        {
            Factor factor=(Factor)factorlist.get(i);
            if(i!=0)
            {
            	factor.setLog(PubFunc.keyWord_reback(factor.getLog()));
                sexpr.append(factor.getLog());
            }
            sexpr.append(i+1);
            sfactor.append(factor.getFieldname().toUpperCase());
            String oper = PubFunc.hireKeyWord_filter_reback(factor.getOper());
            factor.setOper(oper);
            sfactor.append(oper);
            String q_value=factor.getValue().trim();
            if("1".equals(like)&&("A".equals(factor.getFieldtype())|| "M".equals(factor.getFieldtype())))
            {
            	//liuy 2014-11-4 嘉佑医疗集团：岗位说明书界面查询操作有误(代码型指标前面不能模糊，字符串型指标前面可以模糊) start
            	if("0".equals(factor.getCodeid())||factor.getCodeid()==null||"".equals(factor.getCodeid())){
            		if(!("".equals(q_value)))
            			sfactor.append("*");
            	}
            }
            
            sfactor.append(q_value);  
            /**对字符型指标有模糊*/
            if("1".equals(like)&&("A".equals(factor.getFieldtype())|| "M".equals(factor.getFieldtype())))
            {
            	if(!("".equals(q_value)))
                    sfactor.append("*");
            }
            sfactor.append("`");            
        }
	}
	 
    /**
     * 取得查询指标项目
     * @param querylist
     * @param strInfkind
     * @return
     */
    private ArrayList getFieldList(ArrayList querylist ,String strInfkind)
    {
    	ArrayList list=new ArrayList();
    	Field temp=null;
		if("1".equals(strInfkind))
		{
				temp=new Field("dbase",ResourceFactory.getProperty("label.dbase"));
				temp.setDatatype(DataType.STRING);
				temp.setKeyable(true);
			    temp.setVisible(this.bm_dbase);
			    temp.setNullable(false);
			    temp.setSortable(false);	
				temp.setLength(30);
				temp.setCodesetid("@@");
				list.add(temp);		
				
				temp=new Field("a0100",ResourceFactory.getProperty("a0100.label"));
				temp.setDatatype(DataType.STRING);
				temp.setKeyable(true);
			    temp.setVisible(false);
			    temp.setNullable(false);
			    temp.setSortable(false);	
				temp.setLength(30);
				temp.setCodesetid("0");
				list.add(temp);
				temp=new Field("a0000",ResourceFactory.getProperty("a0000.label"));
				temp.setDatatype(DataType.INT);
				temp.setKeyable(false);
			    temp.setVisible(false);
			    temp.setNullable(false);
			    temp.setSortable(false);
			    temp.setCodesetid("0");
				list.add(temp);	
				
				temp=new Field("b0110",ResourceFactory.getProperty("b0110.label"));
				temp.setDatatype(DataType.STRING);
				temp.setKeyable(false);
			    temp.setVisible(true);
			    temp.setNullable(false);
			    temp.setSortable(false);
			    temp.setLength(50);
			    temp.setCodesetid("UN");
				list.add(temp);	
				
				FieldItem item=DataDictionary.getFieldItem("e0122");
				temp=item.cloneField();
				temp.setVisible(true);
			    temp.setSortable(true);	
				temp.setKeyable(false);
			    temp.setNullable(false);
			    temp.setLength(50);
			    temp.setCodesetid("UM");
				list.add(temp);	
				
				temp=new Field("e01a1",ResourceFactory.getProperty("e01a1.label"));
				temp.setDatatype(DataType.STRING);
				temp.setKeyable(false);
			    temp.setVisible(true);
			    temp.setNullable(false);
			    temp.setSortable(true);
			    temp.setLength(50);
			    temp.setCodesetid("@K");
				list.add(temp);	
				
				item=DataDictionary.getFieldItem("a0101");
				temp=item.cloneField();
				temp.setVisible(true);
			    temp.setSortable(true);	
				temp.setKeyable(false);	
			    temp.setNullable(false);	
			    temp.setCodesetid("0");
				list.add(temp);	
		}
		else if("2".equals(strInfkind))
		{
			temp=new Field("b0110",ResourceFactory.getProperty("b0110.label"));
			temp.setDatatype(DataType.STRING);
			temp.setKeyable(true);
		    temp.setVisible(true);
		    temp.setNullable(false);
		    temp.setSortable(false);
		    temp.setLength(50);
		    temp.setCodesetid("UN");
			list.add(temp);			
		}
		else if("9".equals(strInfkind))
		{
			temp=new Field("h0100",ResourceFactory.getProperty("h0100.label"));
			temp.setDatatype(DataType.STRING);
			temp.setKeyable(true);
		    temp.setVisible(true);
		    temp.setNullable(false);
		    temp.setSortable(false);
		    temp.setLength(50);
		    CardConstantSet cardconst=new CardConstantSet(userView, getFrameconn());
		    temp.setCodesetid(cardconst.getStdPosCodeSetId());
			list.add(temp);			
		}
		else
		{
			temp=new Field("e01a1",ResourceFactory.getProperty("e01a1.label"));
			temp.setDatatype(DataType.STRING);
			temp.setKeyable(true);
		    temp.setVisible(true);
		    temp.setNullable(false);
		    temp.setSortable(true);
		    temp.setLength(50);
		    temp.setCodesetid("@K");
			list.add(temp);				
		}
        ArrayList resultlist=privFieldList(querylist,strInfkind);
        /**同样根据是否填定查询值，取得查询指标*/
        String fields=query_Field(resultlist);
        cat.debug("sss_fields="+fields.toString());
    	for(int i=0;i<resultlist.size();i++)
    	{
    		FieldItem item=(FieldItem)resultlist.get(i);
    		if("1".equals(history)&&(!item.isMainSet()))
    			continue;	     		
//    		if(fields.indexOf(item.getItemid())==-1)
//    			continue;   		
    		cat.debug("item name="+item.getItemid());
    		list.add(item.cloneField());
    	}
    	return list;
    }
    /**
     * @param fieldlist
     * @param flag 1:人员2：单位3：职位
     * @return
     */
	private ArrayList privFieldList(ArrayList fieldlist,String flag) {
		ArrayList list=new ArrayList();
		/**权限分析*/
		for(int j=0;j<fieldlist.size();j++)
		{
			FieldItem fielditem=(FieldItem)fieldlist.get(j);
			String fieldname=fielditem.getItemid();
			if("e01a1".equals(fieldname)&& "3".equals(flag))
				continue;
			else if("b0110".equals(fieldname)&& "2".equals(flag))
				continue;
			else if("h0100".equals(fieldname)&& "9".equals(flag))  // 基准岗位
				continue;
			else 
			{
				if("b0110,e0122,e01a1,a0101".indexOf(fieldname)!=-1)
					continue;
			}
			/**
			    * cmq changed at 20120427 因为多表（数据量大）关联查询速度比较慢，
			    * 查询引擎做了优化,子集指标不能直接从返回的SQL取得  
			    */
			   if(("1".equalsIgnoreCase(flag)&&!fielditem.isMainSet())||("1".equalsIgnoreCase(flag)&&!fielditem.getFieldsetid().toUpperCase().startsWith("A")))
				   continue;
			cat.debug("priv_field="+fieldname);
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname.toUpperCase())));
			list.add(fielditem);
		}
		return list;
	} 
	/**
     * 根据输入的查询条件取得查询值
     * @param list
     * @return
     */
    private String query_Field(ArrayList list)
    {
        StringBuffer strfields=new StringBuffer();
        int j=0;
        for(int i=0;i<list.size();i++)
        {
            FieldItem item=(FieldItem)list.get(i);
    		if("1".equals(history)&&(!item.isMainSet()))
    			continue;	         
            if(j!=0)
                strfields.append(",");
            ++j;
            strfields.append(item.getItemid());
        }
        return strfields.toString();
    }
    /**
     * 分析表达式的合法式
     * @param expression
     * @param nmax　最大表达式因子号
     * @return
     */
    private boolean isHaveExpression(String expression,int nmax)
    {
        boolean bflag=true;
        String strlastno="";
        int ncurr=0;
        for(int i=0;i<expression.length();i++)
        {
          char v =expression.charAt(i);
          if(((i+1)!=expression.length())&&(v>='0'&&v<='9'))
          {
            strlastno=strlastno+v;
          }
          else
          {
            if(v>='0'&&v<='9')
            {
              strlastno=strlastno+v;
            }
            if(!"".equals(strlastno))
            {
              ncurr=Integer.parseInt(strlastno);
              if(ncurr>nmax)
              {
                  bflag=false;
                  break;
              }
            }
            strlastno="";
          }
        }        
        return bflag;
    }
    /**
     * 
     * @param dataList
     * @param type
     * @param expr
     * @return
     * @Description: 将前台传来的bean转换成需要的bean
     * @author gaohy 
     * @editor lis 20160406
     * @date Feb 19, 2016 4:54:34 PM 
     * @version V7x
     * @throws GeneralException 
     */
    private ArrayList MorphdynabeanToFactor(ArrayList dataList,String type,String expr) throws GeneralException{
    	ArrayList list = new ArrayList();
    	int nInform=Integer.parseInt(type);
    	try {
    		for (int i = 0; i < dataList.size(); i++) {
    			MorphDynaBean md=(MorphDynaBean)dataList.get(i);
    			String fieldName = md.get("fieldname")+"";
    			FieldItem item = DataDictionary.getFieldItem(fieldName);
        		Factor factor = new Factor(nInform);
        		factor.setCodeid(md.get("codeid")+"");
        		factor.setFieldname(fieldName);
        		factor.setHz(md.get("hz")+"");
        		factor.setFieldtype(item.getItemtype());
        		factor.setItemlen(item.getItemlength());
        		factor.setItemdecimal(item.getDecimalwidth());
        		factor.setHzvalue(item.getItemdesc());
        		
        		String value = md.get("value") + "";
        		if(StringUtils.isNotBlank(value)){
        			String[] arr = value.split("`");
        			if(arr.length > 0)
        				factor.setValue(arr[0]);
        			else
        				factor.setValue("");
        		}
        		else
        			factor.setValue(value);
        			
        		factor.setOper(md.get("oper")+"");// default
        		factor.setLog(md.get("log")+"");// default
        		list.add(factor);
    		}	
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    	return list;
    }
    public ArrayList<ColumnsInfo> getColumnList(){
    	ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();
    	try{
    		ColumnsInfo a0101 = getColumnsInfo("a0101", "姓名", 150, "A");
			columnTmp.add(a0101);
			
			ColumnsInfo b0110 = getColumnsInfo("b0110", "单位", 150, "A");
			b0110.setCodesetId("UN");
			columnTmp.add(b0110);
			
			ColumnsInfo e0122 = getColumnsInfo("e0122", "部门", 150, "A");
			e0122.setCodesetId("UM");
			columnTmp.add(e0122);
			
			ColumnsInfo e01a1 = getColumnsInfo("e01a1", "岗位", 150, "A");
			e01a1.setCodesetId("@K");
			columnTmp.add(e01a1);

			if(StringUtils.isNotEmpty(this.onlynameValid) && "1".equals(this.onlynameValid) && StringUtils.isNotEmpty(this.onlyname)){//唯一性指标启用 && 唯一性指标不为空时，增加【唯一性指标】列
				String onlynameText = DataDictionary.getFieldItem(this.onlyname, "a01").getItemdesc();
				ColumnsInfo guidkey = getColumnsInfo(this.onlyname, onlynameText+"(唯一标识)", 150, "A");
				columnTmp.add(guidkey);
			}
			
			ColumnsInfo a0100 = getColumnsInfo("a0100", "", 150, "A");
			a0100.setEncrypted(true);
			a0100.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(a0100);

			ColumnsInfo nbase = getColumnsInfo("nbase", "", 150, "A");
			nbase.setEncrypted(true);
			nbase.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(nbase);

			ColumnsInfo nbasea0100 = getColumnsInfo("nbasea0100", "", 150, "A");
			nbasea0100.setEncrypted(true);
			nbasea0100.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(nbasea0100);
			
    	} catch (Exception e) {
            e.printStackTrace();
            GeneralExceptionHandler.Handle(e);
        }
    	return columnTmp;
    }
    private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth, String type) {

        ColumnsInfo columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId(columnId);
        columnsInfo.setColumnDesc(columnDesc);
        //columnsInfo.setCodesetId("");// 指标集
        columnsInfo.setColumnType(type);// 类型N|M|A|D
        columnsInfo.setColumnWidth(columnWidth);// 显示列宽
        columnsInfo.setSortable(true);// 是否排序
        if ("A".equals(type)) {
            columnsInfo.setCodesetId("0");
        }
        columnsInfo.setDecimalWidth(0);// 小数位

        // 数值和日期默认居右
        if ("D".equals(type) || "N".equals(type))
            columnsInfo.setTextAlign("right");

        return columnsInfo;
    }
    /**
	 * 获取本级、下级
	 * @param unitIdByBusi
	 * @return sql 权限过滤sql
	 */
    private String getB0110Sql_down(String unitIdByBusi,String nbase) {
    	StringBuilder sql = new StringBuilder();
    	
    	try{
    		String[] tmp = unitIdByBusi.split("`");
    		sql.append(" AND (");
    		//可能选择的是部门，如果仅查b0110查不出值
    		for(int i=0;i<tmp.length;i++)
			{
				if(tmp[i].trim().length()>0)
				{
					if(i > 0) {
						sql.append(" or ");
					}
					sql.append(nbase+"A01.E0122 like '"+tmp[i].substring(2)+"%' ");
					 
					if("UN".equals(tmp[i].substring(0,2)))
					{
						sql.append(" or "+nbase+"A01.B0110 like '"+tmp[i].substring(2)+"%' ");
					}
				}
			}
    		sql.append(") ");
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	return sql.toString();
	}
}
