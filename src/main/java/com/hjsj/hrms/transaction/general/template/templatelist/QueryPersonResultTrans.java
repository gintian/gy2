package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hjsj.hrms.businessobject.general.template.TemplateListBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.ykcard.TSyntax;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Factor;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class QueryPersonResultTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			  String tabid=(String)this.getFormHM().get("tabid");
			  ArrayList factorlist = (ArrayList)this.getFormHM().get("personFilterList");
			  String table_name=(String)this.getFormHM().get("table_name");
			  if(table_name==null|| "".equals(table_name)){
				  table_name="templet_"+tabid;
			  }else{
				  if(table_name.startsWith("templet_")){
					  String tableid = table_name.substring(table_name.indexOf("templet_")+8,table_name.length());
					  if(tabid!=null&&tabid.length()>0&&!tabid.equals(tableid))
						  table_name="templet_"+tabid;
				  }
			  }
			  HashMap hm =(HashMap)this.getFormHM().get("requestPamaHM");
			  TemplateListBo bo=new TemplateListBo(tabid,this.getFrameconn(),this.userView);
			  String sql ="";
			  String sexpr=(String)this.getFormHM().get("expr");
			  sexpr = PubFunc.keyWord_reback(sexpr);
				HashMap map = new HashMap();
				
				for(int i=0;i<factorlist.size();i++)
				{
					 Factor factor=(Factor)factorlist.get(i);
					FieldItem item = new FieldItem();
					String s = factor.getFieldname();
					item.setCodesetid(factor.getCodeid());
					item.setUseflag("1");
					
						item.setItemtype(factor.getFieldtype());
					
					
					item.setItemid(factor.getFieldname().toUpperCase());
					//item.setAlign(factor.get);
					item.setItemdesc(factor.getHz());

					map.put(factor.getFieldname().toUpperCase(),item);
		
				}
			  if(hm.get("query")!=null&& "1".equals(hm.get("query"))){
				   factorlist = (ArrayList)this.getFormHM().get("queryfieldlist");
				   map = new HashMap();
				   for(int i=0;i<factorlist.size();i++)
					{
					   FieldItem item=(FieldItem)factorlist.get(i);
					   if((item.getValue()==null|| "".equals(item.getValue()))&&(!"D".equals(item.getItemtype())))
			                continue;
			            if(((item.getValue()==null|| "".equals(item.getValue()))&&(item.getViewvalue()==null|| "".equals(item.getViewvalue())))&&("D".equals(item.getItemtype())))
			                continue;
			            if(!"1".equals(item.getUseflag()))
			            	continue;
			        	item.setCodesetid(item.getCodesetid());
//						item.setUseflag("1");
//						
//							item.setItemtype(item.getItemtype());
//						
//						
//						item.setItemid(item.getFieldsetid().toUpperCase());
//						//item.setAlign(factor.get);
//						item.setItemdesc(item.getItemdesc());
						map.put(item.getItemid().toUpperCase(),item);
			
					}
				  String like=(String)this.getFormHM().get("querylike");	//模糊查询
				  int j=1;
			    	boolean bresult=true;
			    	boolean blike=false; 
			    	
			    	if("1".equals(like))
			    		blike=true;
				  StringBuffer strexpr=new StringBuffer();
			        StringBuffer strfactor=new StringBuffer();
			        ArrayList checklist=new ArrayList();
			        InfoUtils infoUtils=new InfoUtils();
				   for(int i=0;i<factorlist.size();i++)
			        {
			            FieldItem item=(FieldItem)factorlist.get(i);
			            //System.out.println(item.getItemdesc());
			            /**如果值未填的话，default是否为不查*/
			            if((item.getValue()==null|| "".equals(item.getValue()))&&(!"D".equals(item.getItemtype())))
			                continue;
			            if(((item.getValue()==null|| "".equals(item.getValue()))&&(item.getViewvalue()==null|| "".equals(item.getViewvalue())))&&("D".equals(item.getItemtype())))
			                continue; 
			         
			    			if("D".equals(item.getItemtype()))
			                {
			                    int sf=bo.analyFieldDate(item,strexpr,strfactor,j);
			                    if(sf==1)
			                    {
			                    	throw new GeneralException("输入的日期格式错误或范围不完整，请重新输入！");
			                    }
			                    j=j+sf;
			                }
			                else
			                {
			    	            String tempexpr="";
			    	            if("A".equals(item.getItemtype())|| "M".equals(item.getItemtype()))
			    	            {
			    	            		String q_v=item.getValue().trim();
			    	            		String temp []=q_v.split("`");
			    	            		
			    	            		if(temp.length>2){
			    	            			 tempexpr="(";
			    	            			for(int a=1;a<temp.length;a++){
			    	            				if("1".equals(like)&&(!(q_v==null|| "".equals(q_v))))
					    	                    {
					    			                
					    	                    	strfactor.append(item.getItemid().toUpperCase());
					    			                if("0".equals(item.getCodesetid()))
					    			                	strfactor.append("=*");
					    			                else
					    			                	strfactor.append("=");			                	
					    			                strfactor.append(temp[a]);
					    			                strfactor.append("*`");	
					    			                tempexpr+=j++;
					    	                    }
					    	                    else
					    	                    {
					    	                    	
					    			                strfactor.append(item.getItemid().toUpperCase());
					    			                strfactor.append("=*");
					    			                strfactor.append(temp[a]);
					    			                strfactor.append("*`");
					    			                tempexpr+=j++;
					    	                    }
			    	            				tempexpr+="+";
			    	            			}
			    	            			tempexpr=tempexpr.substring(0,tempexpr.length()-1);
			    	            			tempexpr+=")";
			    	            		}else{
			    	            		if("1".equals(like)&&(!(q_v==null|| "".equals(q_v))))
			    	                    {
			    			                
			    	                    	strfactor.append(item.getItemid().toUpperCase());
			    			                if("0".equals(item.getCodesetid()))
			    			                	strfactor.append("=*");
			    			                else
			    			                	strfactor.append("=");	
			    			                //特殊处理 单位，部门，职位类型
			    			                if("codesetid_1".equals(item.getItemid().toLowerCase())){
			    			                	if("部门".indexOf(item.getValue().trim())!=-1)
			    			                		 strfactor.append("UM");
			    			                	else if("单位".indexOf(item.getValue().trim())!=-1)
			    			                		 strfactor.append("UN");
			    			                	else if("职位".indexOf(item.getValue().trim())!=-1)
			    			                		 strfactor.append("@K");
			    			                	else
			    			                		strfactor.append(item.getValue().trim().replace("`", ""));
			    			                }else
			    			                strfactor.append(item.getValue().trim().replace("`", ""));
			    			                strfactor.append("*`");	   			                	  
			    	                    }
			    	                    else
			    	                    {
			    	                    	
			    			                strfactor.append(item.getItemid().toUpperCase());
			    			                strfactor.append("=");
			    			                //特殊处理 单位，部门，职位类型
			    			                if("codesetid_1".equals(item.getItemid().toLowerCase())){
			    			                	if("部门".indexOf(item.getValue().trim())!=-1)
			    			                		 strfactor.append("UM");
			    			                	else if("单位".indexOf(item.getValue().trim())!=-1)
			    			                		 strfactor.append("UN");
			    			                	else if("职位".indexOf(item.getValue().trim())!=-1)
			    			                		 strfactor.append("@K");
			    			                	else
			    			                		strfactor.append(item.getValue().trim().replace("`", ""));
			    			                }else
			    			                strfactor.append(item.getValue().trim().replace("`", ""));
			    			                strfactor.append("`");	  	                        
			    	                    }
			    	            		}
			    	            }
			    	            else
			    	            {
			    	                strfactor.append(item.getItemid().toUpperCase());
			    	                strfactor.append("=");
			    	                strfactor.append(item.getValue().trim().replace("`", ""));
			    	                strfactor.append("`");
			    	            }
			    	            /**组合表达式串*/
			    	            if("".equals(tempexpr))
			    	            {
			    	               if(j==1){
			    	            	strexpr.append(j);
			    	               }else{
			    	            	   strexpr.append("*");
				    	                strexpr.append(j);    
			    	               }
			    	               ++j;	 
			    	            }
			    	            else
			    	            {
			    	            	if(j==1){
			    	            		strexpr.append(tempexpr);
			    	            	}else{
			    	            		strexpr.append("*");
			    	            		strexpr.append(tempexpr);
			    	            	}
			    	            }
			    	                      
			                }
			    		}
			            String strexpr2 =strexpr.toString();
			            if(strexpr2.startsWith("*"))
			            	strexpr2=strexpr2.substring(1,strexpr2.length());	
				   hm.remove("query");
				    
				   String strfactor_str=strfactor.toString().replaceAll("＊", "*");
				   strfactor_str=strfactor_str.replaceAll("？", "?");
					FactorList factor_bo=new FactorList(strexpr2,strfactor_str.toUpperCase(),this.userView.getUserId(),map);
					   sql=factor_bo.getSingleTableSqlExpression(table_name);
	//			   sql = bo.combine_SQL(this.getUserView(), factorlist, like,this.userView.getUserId(),map,table_name);
				 
			  }else{
			 
			  //筛选条件
			  StringBuffer sfactor = new StringBuffer();
				for(int i=0;i<factorlist.size();i++)
		        {
		            Factor factor=(Factor)factorlist.get(i);
		          /*  if(i!=0)
		            {
		                sexpr.append(factor.getLog());
		            }
		            sexpr.append(i+1);*/
//		            if(factor.getFieldname().indexOf("_")!=-1)
//		            sfactor.append(factor.getFieldname().substring(0, factor.getFieldname().indexOf("_")).toUpperCase());
//		            else
		            	 sfactor.append(factor.getFieldname().toUpperCase());
		            sfactor.append( PubFunc.keyWord_reback(factor.getOper()));
		            String q_value=factor.getValue().trim();
		            if("M".equals(factor.getFieldtype()))
		            {
		            	if(!("".equals(q_value)))
		            		sfactor.append("*");
		            }
		            if(factor.getFieldname().toLowerCase().startsWith("codesetid_")){
	                	if("部门".indexOf(factor.getValue().trim())!=-1)
	                		sfactor.append("UM");
	                	else if("单位".indexOf(factor.getValue().trim())!=-1)
	                		sfactor.append("UN");
	                	else if("职位".indexOf(factor.getValue().trim())!=-1)
	                		sfactor.append("@K");
	                	else
	                		sfactor.append(factor.getValue());  
	                }else
		            sfactor.append(factor.getValue());  
		            /**对字符型指标有模糊*/
		            if("M".equals(factor.getFieldtype()))
		            {
		            	if(!("".equals(q_value)))
		                    sfactor.append("*");
		            }
		            sfactor.append("`");            
		        }
				 if("".equals(sexpr.toString()))
			           throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistexpr"),"",""));
			        /**为了分析用*/
			        if(!isHaveExpression(sexpr.toString(),factorlist.size()))
			            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistfactor"),"",""));
			        String expression =sexpr.toString().replace('!','-');
			        TSyntax syntax=new TSyntax();
			        if(!syntax.Lexical(expression))
			            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.expression"),"",""));
			        if(!syntax.DoWithProgram())
			            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.expression"),"",""));
			      
				FactorList factor_bo=new FactorList(expression,sfactor.toString().toUpperCase(),this.userView.getUserId(),map);
				   sql=factor_bo.getSingleTableSqlExpression(table_name);
			  }
			  String tempsql="";
			  if(sql!=null&&sql.length()>0)
			   tempsql = " select * from  "+table_name+" where "+sql ;
			  else
				  tempsql = " select * from  "+table_name ;
			  ContentDAO dao=new ContentDAO(this.getFrameconn());
			  boolean flag2 = false;
			  try{
			  dao.search(tempsql);
			  }catch(Exception e){
				  flag2=true;
				  this.getFormHM().put("issave","2");
				  this.getFormHM().put("description","表达式因子有误！"); 
				 // throw GeneralExceptionHandler.Handle(new GeneralException("","表达式因子有误！","",""));
			  }
			  String codeid=(String)this.getFormHM().get("codeid");
			  String tasklist_str=(String)this.getFormHM().get("tasklist_str");
			  
			  removeNoConditionSelectedRecords(tasklist_str,tabid,table_name);
			  
			  
			  String filterCondId = (String)this.getFormHM().get("filterCondId");
			  if(filterCondId==null|| "".equals(filterCondId))
				  filterCondId="new";
			  ArrayList filterList = (ArrayList)this.getFormHM().get("personFilterList");
	          this.getFormHM().put("personFilterList",filterList);
	          /**将sql传到前台时要加密,防止越权，以及sql注入**/
	          //this.getFormHM().put("filterStr",sql);
	          this.getFormHM().put("filterStr", PubFunc.encrypt(sql));
	          this.userView.getHm().put("filterStr", sql); //当sql太长通过链接传递会出错
			  this.getFormHM().put("tabid",tabid);
//			  this.getFormHM().put("tableName",tableName);
			  if(flag2){
				  this.getFormHM().put("issave","2");  
			  }else
			  this.getFormHM().put("issave","1");
//			  this.getFormHM().put("filterCondId",filterCondId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			 GeneralException eGenenal= GeneralExceptionHandler.Handle(e);
			 eGenenal.getErrorDescription();
			 this.getFormHM().put("issave","2"); 
			 this.getFormHM().put("description",eGenenal.getErrorDescription()); 
		}
		
	}
	
	
	/**
	 * 将非查询结果的选择标记去掉。
	 * @param _codeid
	 * @param tasklist_str
	 * @param tabid
	 * @param operationtype
	 * @param condSql
	 */
	public void removeNoConditionSelectedRecords(String tasklist_str,String tabid,String table_name)
	{
		
//		try
//		{
//			ContentDAO dao=new ContentDAO(this.getFrameconn());
//			
//			ArrayList tasklist=new ArrayList();
//			if(tasklist_str!=null&&tasklist_str.length()>0)
//			{
//				String[] temp=tasklist_str.split(",");
//				for(int i=0;i<temp.length;i++)
//				{
//					if(temp[i]==null||temp[i].length()==0)
//						continue;
//					tasklist.add(temp[i]);
//					
//				}
//			}
//			StringBuffer sql=new StringBuffer();
//			sql.append("update "+table_name+" set submitflag=0  where 1=1 ");
//			if(tasklist!=null&&tasklist.size()>0)
//			{
//				StringBuffer strins=new StringBuffer();
//				for(int i=0;i<tasklist.size();i++)//按任务号查询需要审批的对象20080418
//				{
//									if(i!=0)
//									  strins.append(",");
//									strins.append((String)tasklist.get(i));
//				}
//				sql.append(" and ( task_id in(");
//				sql.append(strins.toString());
//				sql.append(")");				
//				//角色属性是否为汇报关系 “直接领导”、“主管领导”，“第三级领导”、“第四级领导”、“全部领导”，
//				sql.append(" or exists (select null from t_wf_task_datalink where templet_"+tabid+".seqnum=t_wf_task_datalink.seqnum ");
//				sql.append("  and task_id in ("+strins.toString()+") and state=0 ) ) ");
//								
//			}
//			String strsql=sql.toString();
//			dao.update(strsql);
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
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

}
