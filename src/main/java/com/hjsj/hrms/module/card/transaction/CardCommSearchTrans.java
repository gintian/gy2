package com.hjsj.hrms.module.card.transaction;

import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.ykcard.TSyntax;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

/**
 * 登记表通用查询
 * @author Administrator
 *
 */
public class CardCommSearchTrans extends IBusiness{
	/**是否跨库查询*/
	private boolean bm_dbase=false;	
	/**历史记录查询标识
	 * =0不对历史记录
	 * =1历史记录
	 * */
	private String history="0";
	
    String personPriv = "";
	@Override
    public void execute() throws GeneralException {
		try {
			
			String expr = (String)this.getFormHM().get("expr");//条件表达式1|E0122=010105`  1*2|E0122=010105`A0107=1`
			expr=expr!=null&&expr.trim().length()>0?expr:"";
			expr=SafeCode.decode(expr);
			
			 String query_type=(String)this.getFormHM().get("query_type");//1通用，2简单查询
			 /**查询类型，简单查询或通用查询*/
		        if(query_type==null|| "".equals(query_type))
		            query_type="2";
			
			String tabid=(String)this.getFormHM().get("tabid");
			tabid=tabid!=null&&tabid.length()>0?tabid:"";
			
			/** type: 1为人员库(default), 2单位, 3岗位, 9基准岗位*/        
	        String type=(String)this.getFormHM().get("inforkind");
	        if(type==null|| "".equals(type))
	        	type="1";
	        
			ArrayList dataList=(ArrayList)this.getFormHM().get("dataList");
			String second="";
			String checkValues = (String)this.getFormHM().get("checkValues");//选择的人员库和查询类型字符串，如：dbName=Usr&dbName=Ret&like=like&history=history&second=second
			checkValues=checkValues!=null&&checkValues.trim().length()>0?checkValues:"";
			String[] checkArray =checkValues.split("&");
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
				}else if(checkArray[i].indexOf("second")!=-1) {
					second="1";
				}
			}
			String filter_factor=(String)this.getFormHM().get("filter_factor");//通用查询才有
			filter_factor=SafeCode.decode(filter_factor);
		
			ArrayList factorlist=MorphdynabeanToFactor(dataList,type,expr);//[{fieldname=e0122,hz=部门,codeid=UM,oper==,log=*,value=010105,hzvalue=部门}]
	        if(factorlist==null)
	            return;
	     // 是否需要权限限制
	        personPriv = (String) this.getFormHM().get("personPriv");
	        this.getFormHM().remove("personPriv");
	        personPriv = personPriv == null ? "1" : personPriv;
	        
	        String chpriv=(String)this.getFormHM().get("chpriv");  //前台传参为priv=0,用于不控制人员管理范围 
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
	        if("4".equals(type) && sfactortemp.indexOf("B0110")!=-1){
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
	         //
	        //type 人员库类型, dbpre 库, sfactor 表达式, sexpr 因式表达式, fieldlist, bhis, bresult,blike,filter_factor
	     String sql=combine_SQL(type, dbpre, sfactor, sexpr, fieldlist, bhis, bresult,blike,filter_factor,second);
	     this.getFormHM().put("flagType", true);
	     //sql 数据插入临时表
		} catch (Exception e) {
			this.getFormHM().put("flagType",false);
			this.getFormHM().put("eMsg", e.getMessage());
			e.printStackTrace();
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
	private String combine_SQL(String type, String[] dbpre, StringBuffer sfactor, StringBuffer sexpr, 
								ArrayList list, boolean bhis, boolean bresult,
								boolean blike,String filter_factor,String second) throws Exception {
        String tabId=(String)this.getFormHM().get("tabId");
        DbWizard db=new DbWizard(this.frameconn);
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
        		if(StringUtils.isBlank(dbpre[i]))
        			continue;
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
        	    updateResult(strSql.toString(), type, dbpre[i], db,second);
         	    strSql.setLength(0);    		
        	}
     	   
        }
        else
        {
            if("1".equals(type))
            {
         	   if(dbpre==null||dbpre.length==0)
         		   throw new GeneralException(ResourceFactory.getProperty("errors.static.notdbname"));
         	   DbNameBo dbnamebo=new DbNameBo(this.getFrameconn());
         	   ArrayList list_dbname=this.userView.getPrivDbList();
         	   for(int i=0;i<dbpre.length;i++)
         	   {
         		   if(StringUtils.isBlank(dbpre[i])) {
         			   continue;
         		   }
         		   if(list_dbname.contains(dbpre[i])) {
         			  list_dbname.remove(dbpre[i]);
         		   }
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
             	   //strSql.append(" UNION ");
           	      updateResult(strSql.toString(), type, dbpre[i], db,second);
           	      strSql.setLength(0);
         	   }
         	   //筛选未选中的人员库 删除对应临时表数据
				if (list_dbname != null && list_dbname.size() > 0) {
					ContentDAO dao = new ContentDAO(this.frameconn);
					StringBuffer delsql = new StringBuffer();
					for (int i = 0; i < list_dbname.size(); i++) {
						delsql.append("delete from t_card_result where flag=1 and nbase='" + list_dbname.get(i)
								+ "' and username='" + this.userView.getUserName() + "' and status="
								+ this.userView.getStatus());
						dao.update(delsql.toString());
						delsql.setLength(0);
					}
				}
            }
            else
            {
            	/**
            	 * type 1 人员库
            	 * 		2 单位
            	 * 		3岗位
            	 * 		5培训
            	 * 		9职务库
            	 * 		6党组织
            	 * 		7团组织
            	 * 		8工会组织
            	 * */
               String backdate=DateUtils.format(new Date(), "yyyy-MM-dd");
         	   FactorList factor_bo=new FactorList(sexpr.toString(),PubFunc.getStr(sfactor.toString()),"",bhis,blike,bresult,Integer.parseInt("4".equals(type)?"3":("6".equals(type)?"9":type)),userView.getUserId());
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

						String conditionSql = " select codeitemid from organization  where  ( codesetid='UN' or codesetid='UM') and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date and  "
							+ codeValue;
						privStr.append(" and B01.b0110 in (" + conditionSql + " )");
						
					}
				}
				else
				{
					
					if ("4".equals(type)) {
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
				if("6".equals(type)) {
				    String codesetid="";
	                RecordVo constantuser_vo = ConstantParamter.getRealConstantVo("PS_C_CODE");
	                if (constantuser_vo != null)
	                    codesetid = constantuser_vo.getString("str_value");
                    String conditionSql = " select codeitemid from codeitem  where ( codesetid='"+codesetid+"') and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ";
                    privStr.append(" and H01.H0100 in (" + conditionSql + " )");
				}
				strSql.append(strSelect);
         	    strSql.append(strWhere);  
         	    strSql.append(privStr);
         	    updateResult(strSql.toString(), type,"", db,second);
        	      strSql.setLength(0);
            }
        }

        for(int i=0;i<fieldlist.size();i++)
        {
        	FieldItem item=(FieldItem)fieldlist.get(i);      	
        	list.add(item.clone());
        }
        return strSql.toString();
	}
	/**
	 * 登记表临时表 增删数据
	 * @param sql
	 * @param inforkind
	 * @param dbper
	 * @param db
	 */
	private void updateResult(String sql,String inforkind,String dbper,DbWizard db,String second) throws Exception {
		ContentDAO dao=new ContentDAO(this.frameconn);
//		RowSet rs=null;
//		登记表临时表  t_card_result  
//		表结构  username nbase  objid  flag  status 
			Boolean dbflag=false;
			dbflag=db.isExistTable("t_card_result", false);
			if(!dbflag) {//业务用户临时表 无此表时 创建查询结果表   //自助与业务用户共用同一张临时表 
				Table table=new Table("t_card_result");
				//table.setName(this.userView.getUserName()+dbper+"Result");
				Field username=new Field("username","username");
				username.setDatatype(DataType.STRING);
				username.setLength(50);
				
				Field nbase=new Field("nbase","nbase");
				nbase.setDatatype(DataType.STRING);
				nbase.setLength(3);
				
				Field objid=new Field("objid","objid");
				objid.setDatatype(DataType.STRING);
				objid.setLength(50);
				
				Field flag=new Field("flag","flag");
				flag.setDatatype(DataType.INT);
				
				Field status=new Field("status","status");//自助用户业务用户区分 0 业务用户  4 自助用户
				status.setDatatype(DataType.INT);
				
				table.addField(username);
				table.addField(nbase);
				table.addField(objid);
				table.addField(flag);
				table.addField(status);
				db.createTable(table);
				
			}
		
			StringBuffer sbf=new StringBuffer();
			StringBuffer delsql=new StringBuffer();
			StringBuffer insertSql=new StringBuffer();
			insertSql.append("insert into t_card_result(username,nbase,objid,flag,status)");
			delsql.append("delete from ");
			delsql.append("t_card_result");	
			//if(this.userView.getStatus()!=4) {//业务   插入数据前先判断是否有临时表存在 不存在创建  插入数据前 先清除数据
				if("1".equals(inforkind)) {
					delsql.append(" where flag=1 and nbase='"+dbper+"' and username='"+this.userView.getUserName()+"' and status="+this.userView.getStatus());
					sbf.append("select '"+this.userView.getUserName()+"' as username,'"+dbper+"' as nbase ,A.A0100 as objid,1 as flag,"+this.userView.getStatus()+" as status from");
					sbf.append("("+sql+") A");
					
				}else if("2".equals(inforkind)) {//单位登记表
					delsql.append(" where flag=2 and username='"+this.userView.getUserName()+"' and status="+this.userView.getStatus());
					sbf.append("select '"+this.userView.getUserName()+"' as username,'B' as nbase ,A.b0110 as objid,2 as flag,"+this.userView.getStatus()+" as status from");
					sbf.append("("+sql+")A");
					
				}else if("6".equals(inforkind)) {//基准岗位 inforkind=6 
					delsql.append(" where flag=6 and username='"+this.userView.getUserName()+"' and status="+this.userView.getStatus());
					sbf.append("select '"+this.userView.getUserName()+"' as username,'H' as nbase ,A.h0100 as objid,6 as flag ,"+this.userView.getStatus()+" as status from ");
					sbf.append("("+sql+")A");
					
				}else if("4".equals(inforkind)) {//岗位说明书 4
					delsql.append(" where flag=4 and username='"+this.userView.getUserName()+"' and status="+this.userView.getStatus());
					sbf.append("select '"+this.userView.getUserName()+"' as username,'K' as nbase ,A.e01a1 as objid,4 as flag ,"+this.userView.getStatus()+" as status from");
					sbf.append("("+sql+")A");
					
				}
			if("1".equals(second)) {//启用二次查询
				dao.update(delsql.toString()+" and objid not in (select objid from ("+sbf.toString()+")AA)");
			}else {
				dao.update(delsql.toString());
				dao.update(insertSql.toString()+sbf.toString());
			}
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
			else if("h0100".equals(fieldname)&& "6".equals(flag))  // 基准岗位
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
	
	private String getSelectString(ArrayList list,String strInfkind,String dbpre)
    {
        ArrayList resultlist=privFieldList(list,strInfkind);
        /**同样根据是否填定查询值，取得查询指标*/
        String fields=query_Field(resultlist);
        
        StringBuffer strsql=new StringBuffer();
        if("1".equals(strInfkind)) {//人员
        	strsql.append("select distinct A0100");
        }else if("2".equals(strInfkind))//单位
        {
	        strsql.append("select distinct b01.b0110 ");
        }
        else if("4".equals(strInfkind))//岗位说明书
        {
	        strsql.append("select distinct k01.e01a1 ");        	
        }
        else if("6".equals(strInfkind))  // 基准岗位
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
	
	/*
	 *  //修改添加检索过滤
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
			String whereIN=InfoUtils.getWhereINSql(uv,BasePre,"0".equals(personPriv)?"1":"0");//按检索条件
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
     * 
     * @param dataList
     * @param type
     * @param expr
     * @return
     * @Description: 将前台传来的bean转换成需要的bean
     * @throws GeneralException 
     */
	private ArrayList MorphdynabeanToFactor(ArrayList dataList,String type,String expr) throws Exception{
    	ArrayList list = new ArrayList();
    	int nInform=Integer.parseInt(type);
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
    	return list;
    }
	
}
