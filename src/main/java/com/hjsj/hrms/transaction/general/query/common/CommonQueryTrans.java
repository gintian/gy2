package com.hjsj.hrms.transaction.general.query.common;

import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hjsj.hrms.businessobject.ykcard.TSyntax;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
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
import org.apache.tools.ant.util.DateUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

/**
 * <p>Title:CommonQueryTrans</p>
 * <p>Description:简单及通用查询交易</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-28:14:07:04</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class CommonQueryTrans extends IBusiness {

	/**是否跨库查询*/
	private boolean bm_dbase=false;	
	/**历史记录查询标识
	 * =0不对历史记录
	 * =1历史记录
	 * */
	// 是否需要权限限制
    String personPriv = "";
	private String history="0";
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
     * 取得查询条件中的指标和固定指标不同的项目串
     * @param list
     * @return
     */
    private String getFilterFields(ArrayList list)
    {
    	StringBuffer strfield=new StringBuffer();
    	for(int i=0;i<list.size();i++)
    	{
    		FieldItem item=(FieldItem)list.get(i);
    		strfield.append(item.getItemid().toLowerCase());
    		strfield.append(",");
    	}
    	if(strfield.length()>0)
    		strfield.setLength(strfield.length()-1);
    	return strfield.toString();
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
	
	
	public void execute() throws GeneralException {
	try
	{
		String filter_factor=(String)this.getFormHM().get("filter_factor");
		
		filter_factor=SafeCode.decode(filter_factor);
		ArrayList factorlist=(ArrayList)this.getFormHM().get("factorlist");
        String query_type=(String)this.getFormHM().get("query_type");
        String expression=(String)this.getFormHM().get("expression");
         expression = PubFunc.keyWord_reback(expression);
        /**查询类型，简单查询或通用查询*/
        if(query_type==null|| "".equals(query_type))
            query_type="1";
        if(factorlist==null)
            return;
        /** type: 1为人员库(default), 2单位, 3岗位, 9基准岗位*/        
        String type=(String)this.getFormHM().get("type");
        if(type==null|| "".equals(type))
        	type="1";
        
        String[] dbpre=(String[])this.getFormHM().get("dbpre");
        history=(String)this.getFormHM().get("history");
        String like=(String)this.getFormHM().get("like");
        String result=(String)this.getFormHM().get("result");
        // 是否需要权限限制
        personPriv = (String) this.getFormHM().get("personPriv");
        this.getFormHM().remove("personPriv");
        personPriv = personPriv == null ? "1" : personPriv;
        
        String chpriv=(String)this.getFormHM().get("chpriv");  //前台传参为priv=0,用于不控制人员管理范围  ----人事异动
        if(chpriv!=null&& "0".equals(chpriv))
        	this.personPriv="0";
        
        if((dbpre==null||dbpre.length==0)&& "1".equals(type))
        	throw new GeneralException(ResourceFactory.getProperty("errors.static.notdbname"));        
        if(history==null|| "".equals(history))
            history="0";
        if(like==null|| "".equals(like))
            like="0";
        if(result==null|| "".equals(result))
        	result="0";
        cat.debug("history="+history);
        /**跨库标识*/
        if(dbpre!=null&&dbpre.length>1)
        	this.bm_dbase=true;        
        StringBuffer sfactor=new StringBuffer();
        StringBuffer sexpr=new StringBuffer();
        /**合成通用的表达式*/
        combineFactor(factorlist, like, sfactor, sexpr);
        String sfactortemp = PubFunc.keyWord_reback(sfactor.toString());
        
        //解决岗位说明书查询不能按照单位查询，按照单位查询是传入b0110需替换为e0122
        if("3".equals(type) && sfactortemp.indexOf("B0110")!=-1 && "1".equals(query_type)){
        	sfactortemp = sfactortemp.replaceAll("B0110", "E0122");
        	sfactortemp = sfactortemp.replaceAll("b0110", "E0122");
        }
        
        sfactor.setLength(0);
        sfactor.append(sfactortemp);
        String sexprtemp = PubFunc.keyWord_reback(sexpr.toString());
        sexpr.setLength(0);
        sexpr.append(sexprtemp);
        /**通用查询时，表达式因子按用户填写进行分析处理*/
        if("2".equals(query_type))
        {
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
        /**通用查询结束**/
        cat.debug("expr="+sexpr.toString());
        cat.debug("factor="+sfactor.toString());
        ArrayList fieldlist=new ArrayList();
        boolean bhis=false;
        boolean bresult=true;
        boolean blike=false;
        if("1".equals(history))
        	bhis=true;
        if("1".equals(result))
        	bresult=false;
        if("1".equals(like))
        	blike=false;
        
        
    	String sql=combine_SQL(type, dbpre, sfactor, sexpr, fieldlist, bhis, bresult,blike,filter_factor);
    	sql=sql.replaceAll("\"","\\\\\"");
  	    cat.debug("after field list size="+fieldlist.size());
        cat.debug("Common query's sql="+sql);
     //   System.out.println("sql="+sql);
        
        
        String columns = "";
        String orderby="";
        if(type!=null&& "1".equals(type))
        	orderby="order by A0000";
        ArrayList columlist = getFieldList(fieldlist,type);
        for(int i=0;i<columlist.size();i++){
        	Field field = (Field)columlist.get(i);
        	if(field!=null)
        		columns+=field.getName()+",";
        }
        
        this.getFormHM().put("sql",sql);
        /**结果集名称*/
		this.getFormHM().put("setname","result");
		/**把查询结果指标-->显示的数据格式*/
		this.getFormHM().put("showlist",getFieldList(fieldlist,type));
		this.getFormHM().put("columns",columns.substring(0,columns.length()-1));
		this.getFormHM().put("orderby",orderby);
		
	}
	catch(Exception ex)
	{
		ex.printStackTrace();
		throw GeneralExceptionHandler.Handle(ex);
	}
	}
	/**
	 * @param type
	 * @param dbpre
	 * @param sfactor
	 * @param sexpr
	 * @param fieldlist
	 * @param bhis
	 * @param bresult
	 * @throws GeneralException
	 */
	private String combine_SQL(String type, String[] dbpre, StringBuffer sfactor, StringBuffer sexpr, ArrayList list, boolean bhis, boolean bresult,boolean blike,String filter_factor) throws GeneralException {
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
    	StringBuffer strSql=new StringBuffer();  
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
        if((!userView.isSuper_admin())&& "1".equals(type))
        {
        	for(int i=0;i<dbpre.length;i++)
        	{   
        		fieldlist=new ArrayList();
        		if ("0".equalsIgnoreCase(personPriv)) {
        			FactorList factorlist = new FactorList(sexpr.toString(), PubFunc.getStr(sfactor.toString()), dbpre[i],
        													false, false, true, 1, userView.getUserName(),"");	
        			strWhere = factorlist.getSqlExpression();
        		} else {
                    if (kqFlag&& userView.getKqManageValue() != null && !"".equals(userView.getKqManageValue())){
                    	FactorList factorlist = new FactorList(sexpr.toString(), PubFunc.getStr(sfactor.toString()), dbpre[i],
                    			false, false, true, 1, userView.getUserName(),"");	
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
//     	    strSql.append(" order by dbase desc,b0110,e0122,a0000");        	
        }
        else
        {
            if("1".equals(type))
            {
         	   if(dbpre==null||dbpre.length==0)
         		   throw new GeneralException(ResourceFactory.getProperty("errors.static.notdbname"));
         	   DbNameBo dbnamebo=new DbNameBo(this.getFrameconn());
         	   for(int i=0;i<dbpre.length;i++)
         	   {
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
         	   strSql.setLength(strSql.length()-7);
         	   //System.out.println(strSql.toString());
//         	   strSql.append(" order by dbase desc ,b0110,e0122,a0000");
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
  	    cat.debug("before field list size="+fieldlist.size());    

        for(int i=0;i<fieldlist.size();i++)
        {
        	FieldItem item=(FieldItem)fieldlist.get(i);      	
        	list.add(item.clone());
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
		        strsql.append("a01.e01a1 e01a1,a0101 ");           	
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
	/**
	 * @param factorlist
	 * @param like
	 * @param sfactor
	 * @param sexpr
	 */
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
            	//liuy end
            }
            sfactor.append(factor.getValue());  
            /**对字符型指标有模糊*/
            if("1".equals(like)&&("A".equals(factor.getFieldtype())|| "M".equals(factor.getFieldtype())))
            {
            	if(!("".equals(q_value)))
                    sfactor.append("*");
            }
            sfactor.append("`");            
        }
	}

}
