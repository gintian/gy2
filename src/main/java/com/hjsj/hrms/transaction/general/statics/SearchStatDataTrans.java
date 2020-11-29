package com.hjsj.hrms.transaction.general.statics;

import com.hjsj.hrms.businessobject.stat.StatCondAnalyse;
import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import java.util.*;
/**
 * 统计查看结果
 * <p>Title:SearchStatDataTrans.java</p>
 * <p>Description>:SearchStatDataTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jul 10, 2010 4:20:01 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class SearchStatDataTrans extends IBusiness {
	public void execute() throws GeneralException {
		String showLegend=(String)this.getFormHM().get("showLegend");
		String history=(String)this.getFormHM().get("history");//历史记录
		String userbase=(String)this.getFormHM().get("userbase");//人员库
		String userbases=(String)this.getFormHM().get("userbases");//人员库
        String infokind = (String)this.getFormHM().get("infor_Flag");//统计类型 1：人员；2：单位；3：职位
        String find=(String)this.getFormHM().get("find");//模糊统计
        String result=(String)this.getFormHM().get("result");//查询结果
        String stat_type=(String)this.getFormHM().get("stat_type");
        ArrayList flist=(ArrayList)this.getFormHM().get("factorlist");
        String querycond = (String) this.getFormHM().get("querycond");
		if(showLegend==null||showLegend.length()<=0)
			showLegend="";
		showLegend=SafeCode.decode(showLegend);
		showLegend=showLegend.replaceAll("\\n", "").replaceAll("\\r", "");
		showLegend=com.hjsj.hrms.utils.PubFunc.keyWord_reback(showLegend);
		
		try{
			String columns="";
			ArrayList mainlist=new ArrayList();
			String wheresql="";
			StringBuffer strsql=new StringBuffer();
	        boolean isresult=true;			
			if(result==null|| "".equals(result)|| "0".equals(result))
			    	isresult=true; 
			else
			    	isresult =false;
		    //strlexpr=strlexpr.replaceAll("a","+");
			StatCondAnalyse cond = new StatCondAnalyse();
			StringBuffer orderby=new StringBuffer();
			String distinct="";
			String strlexpr="";
			String tem=null;
	    	String name="";	
	    	String strfactor="";
	    	rebackKeyword(flist);
			if(stat_type!=null&& "simple".equals(stat_type))
			{
				strlexpr="1";
				
				 for(int i=0; i<flist.size();i++)
				 {
						String t_Hz="";    	
						Factor fc=(Factor)flist.get(i);
						String opt=com.hjsj.hrms.utils.PubFunc.keyWord_reback(fc.getOper());
						fc.setOper(opt);
					    //opt = getOperator(opt);
					    if("0".equalsIgnoreCase(fc.getCodeid())) //非代码型
					    {
					    	if(fc.getValue()==null|| "".equals(fc.getValue()))
					    	{
					    		tem="`";
					    		name=ResourceFactory.getProperty("label.null");
					    	}
					    	else
					    	{
					    		/**like仅对字符型指标有作用*/
					    		if("1".equals(find)&& "A".equalsIgnoreCase(fc.getFieldtype()))
					    		{
					    			if(fc.getCodeid()==null || "0".equalsIgnoreCase(fc.getCodeid()))	
					    				tem="%"+fc.getValue()+"%`";
					    			else
					    				tem=fc.getValue()+"%`";
					    		}
					    		else
					    		{
					    			tem=fc.getValue()+"`";
					    		}
					    		name=fc.getValue();
					    	}
					    	t_Hz=fc.getHz()+opt+name;
					    	if(StringUtils.isEmpty(fc.getValue()))
                                name = t_Hz;
					    	
					    	if(showLegend.equals(name)){
					    	    strfactor=fc.getFieldname()+fc.getOper()+tem;					    	
					    	    break;
					    	}
					    }
					    else
					    {
					    	name=fc.getHzvalue();
					    	tem=fc.getValue();
					    	if(fc.getValue()==null|| "".equals(fc.getValue()))
					    	{
					    		tem="`";
					    		name=ResourceFactory.getProperty("label.null");
					    	}
					    	else
					    	{
					    		if("1".equals(find))
					    			tem=fc.getValue()+"%`";
					    		else
					    			tem=tem+"`";
					    	}
					    	t_Hz=fc.getHz()+opt+name;
					    	if(StringUtils.isEmpty(fc.getValue()))
                                name = t_Hz;
					    	
					    	if(showLegend.equals(name)) {
					    		strfactor=fc.getFieldname()+fc.getOper()+tem;
					    		break;
					    	}
					    }
				    }
			}else if(stat_type!=null&& "general".equals(stat_type))
			{
				String[] sel=(String[])this.getFormHM().get("selects");
				for(int i=0;i<sel.length;i++){
					sel[i]=PubFunc.hireKeyWord_filter_reback(sel[i]);
				}
				String selx=(String)this.getFormHM().get("mess");
				ArrayList alist=new ArrayList();
				StringTokenizer st=new StringTokenizer(selx,"," );
			    while(st.hasMoreTokens())
			       alist.add(st.nextToken(","));
			    for(int i=0; i<sel.length;i++)
				{
			    	if(!showLegend.equals(sel[i]))
			    	   continue;
			    	strlexpr=getExpr(alist.get(i).toString());
			    	strfactor=getStrss(alist.get(i).toString(),flist);
			    	//System.out.println(exp);
				}
			}
		    try
		    {
		    	if(infokind!=null && "1".equals(infokind))
				{
					 
					boolean ishavehistory=false;
					if(history!=null&& "1".equals(history))
						ishavehistory=true;
					
					mainlist= getStatItemList(strfactor,infokind);
					strsql.append("select distinct ");
			        strsql.append(userbase);
			        strsql.append("a01.a0100 as a0100,");
			        strsql.append("## as db,");
			        strsql.append("a0000 as a0000,");
			        strsql.append(userbase);        
			        strsql.append("a01.b0110 as b0110,");
		            strsql.append(userbase);
		            strsql.append("a01.e0122 as e0122,");
		            strsql.append(userbase);
		            strsql.append("a01.e01a1 as e01a1");
			        columns=getMainQueryFields(mainlist,infokind);
			        columns=(","+columns.toLowerCase()).replaceAll(",e01a1", "").replaceAll(",e0122", "").replaceAll(",b0110", "").replaceAll(",a0100", "");
			        //strsql.append(",");
			        strsql.append(columns);
			        strsql.append(",UserName ");
			        	//【8665】员工管理-统计分析-通用统计，设置统计范围后，反查进去，人数不对  jingq upd 2015.04.14
					  wheresql=cond.getCondQueryString(strlexpr,strfactor,userbase,ishavehistory,userView.getUserName(),querycond,userView,infokind,isresult,false);
					  userbase=userbase.toUpperCase();
				        String tmpsql =(strsql.toString()+wheresql).toUpperCase();
				        StringBuffer sb = new StringBuffer();
				        if(userbases!=null&&userbases.length()>0){
					        if(userbases.indexOf("`")==-1){
								sb.append(" from ("+tmpsql.replaceAll(userbase, userbases).replaceAll("##", "'"+getStart(0)+userbases+"'")+"");
							}else{
								String[] tmpdbpres=userbases.split("`");
								for(int n=0;n<tmpdbpres.length;n++){
									String tmpdbpre=tmpdbpres[n];
									if(tmpdbpre.length()==3){
										if(sb.length()>0){
											sb.append(" union all "+tmpsql.replaceAll(userbase, tmpdbpre).replaceAll("##", "'"+getStart(n)+tmpdbpre+"'"));
										}else{
											sb.append(" from ("+tmpsql.replaceAll(userbase, tmpdbpre).replaceAll("##", "'"+getStart(n)+tmpdbpre+"'"));
										}
									}
								}
							}
				        }else{
				        	sb.append(" from ("+tmpsql.replaceAll("##", "'"+getStart(0)+userbase+"'")+"");
				        }
				        wheresql=sb.toString()+") tt";
				        strsql.setLength(0);
				        strsql.append("select a0000,");
				        strsql.append("a0100,");
				        strsql.append("b0110,");
			            strsql.append("e0122,");
			            strsql.append("e01a1");
				        //strsql.append(",");
				        strsql.append(columns);
				        strsql.append(",UserName,db ");
					  //System.out.println(wheresql);
		        	//mainlist=getMainFieldList(infokind);
					
			       /* strsql.append("select distinct a0000,");
			        strsql.append(userbase);
			        strsql.append("a01.a0100 ,");
			        strsql.append(userbase);        
			        strsql.append("a01.b0110 b0110,");
		            strsql.append(userbase);
		            strsql.append("a01.e0122 e0122,");
		            strsql.append(userbase);
		            strsql.append("a01.e01a1 e01a1");
			        columns=     
			        strsql.append(",");
			        strsql.append(columns);
			        strsql.append(",UserName ");*/
			        /*
				    strsql.append("select ");
				    strsql.append(userbase);
				    strsql.append("A01.A0100,B0110,E0122,E01A1,A0101,UserName ");
				    */
					orderby.append(" order by ");
					//orderby.append("B0110,E0122,");
					//orderby.append(userbase);
					//orderby.append("a01.a0000");
					orderby.append("db,a0000");
						this.getFormHM().put("distinct", "");
					this.getFormHM().put("columns", columns.toUpperCase()+",UserName,B0110,E0122,E01A1,A0100,DB");			
			    }else if(infokind!=null && "2".equals(infokind)){
			    	//System.out.println(strlexpr + strfactor + querycond);
			    	wheresql=cond.getCondQueryString(strlexpr,strfactor,"B",false,userView.getUserName(),"",userView,infokind,isresult,false);
			    	mainlist= getStatItemList(strfactor,infokind);
					columns=getMainQueryFields(mainlist,infokind); 
				    strsql.append("select B01.B0110 ");
				    if(columns!=null&&columns.length()>0)
				    {
				    	strsql.append(",");
				    	strsql.append(columns);
				    }
			       
					orderby.append(" order by b01.");
					orderby.append("b0110");
					this.getFormHM().put("columns", columns.toUpperCase()+",B0110");
					this.getFormHM().put("distinct", "B01.B0110");
				}else if(infokind!=null && "3".equals(infokind)){
					wheresql=cond.getCondQueryString(strlexpr,strfactor,"K",false,userView.getUserName(),"",userView,infokind,isresult,false);
					mainlist= getStatItemList(strfactor,infokind);
					columns=getMainQueryFields(mainlist,infokind); 
				    strsql.append("select K01.e01a1,K01.e0122 ");
				    if(columns!=null&&columns.length()>0)
				    {
				    	strsql.append(",");
				    	strsql.append(columns);
				    }
				    orderby.append(" order by K01.");
					orderby.append("e01a1");
					this.getFormHM().put("distinct", "K01.e01a1");
					this.getFormHM().put("columns", columns.toUpperCase()+",E0122,E01A1");
					//columns=getStatItemList(strfactor,infokind);  
					//this.getFormHM().put("columns",);
				}
		    }catch(Exception e)
		    {
		    	e.printStackTrace();
		    	throw GeneralExceptionHandler.Handle(e);
		    }
			
			
		    
	        this.getFormHM().put("strsql",strsql.toString());
	       
		    this.getFormHM().put("cond_str",wheresql); 
		    this.getFormHM().put("fieldlist",mainlist);
	        /**浏览信息用的卡片*/
	        this.getFormHM().put("tabid", searchCard(infokind)); 	    
		    this.getFormHM().put("order_by",orderby.toString());
			}catch(Exception e){}
		
	}
	/**
	 * 取得定义的主集指标
	 * @return
	 */
	private ArrayList getMainFieldList(String flag)
	{
		ArrayList mainset=new ArrayList();		
		/**取得人员主集已定义的指标*/
		if("1".equals(flag))
		{
			SaveInfo_paramXml infoxml=new SaveInfo_paramXml(this.getFrameconn());
			mainset=infoxml.getMainSetFieldList();
			/**如果未定义，则固定四项指标，单位、部门、职位以及姓名*/
			if(mainset.size()==0)
			{
				mainset.add(DataDictionary.getFieldItem("b0110"));
				mainset.add(DataDictionary.getFieldItem("e0122"));
				mainset.add(DataDictionary.getFieldItem("e01a1"));
				mainset.add(DataDictionary.getFieldItem("a0101"));
			}			
			for(int i=0;i<mainset.size();i++)
			{
				FieldItem fielditem=(FieldItem)mainset.get(i);
				String fieldname=fielditem.getItemid();
				fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			}

		}
		return mainset;
	}
	
    private String getMainQueryFields(ArrayList list,String infokind)
    {
        StringBuffer strfields=new StringBuffer();
        int j=0;
        if("1".equals(infokind))
        {
        	for(int i=0;i<list.size();i++)
            {
                FieldItem item=(FieldItem)list.get(i);
    			if("b0110,e0122".indexOf(item.getItemid().toLowerCase())!=-1)
    				continue;  
                if(j!=0)
                    strfields.append(",");
                ++j;
              
                strfields.append(item.getItemid());
            }
        }else if("2".equals(infokind))
        {
        	for(int i=0;i<list.size();i++)
            {
                FieldItem item=(FieldItem)list.get(i);
    			if("b0110,".indexOf(item.getItemid().toLowerCase())!=-1)
    				continue;  
                if(j!=0)
                    strfields.append(",");
                ++j;
              
                strfields.append(item.getItemid());
            }
        }else if("3".equals(infokind))
        {
        	for(int i=0;i<list.size();i++)
            {
                FieldItem item=(FieldItem)list.get(i);
    			if("e01a1,e0122".indexOf(item.getItemid().toLowerCase())!=-1)
    				continue;  
                if(j!=0)
                    strfields.append(",");
                ++j;
              
                strfields.append(item.getItemid());
            }
        }
        
        return strfields.toString();    	
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
    /**
	 * 取得定义的主集指标
	 * @return
	 */
	private ArrayList getStatItemList(String factors,String infokind)
	{
		ArrayList statitemlist=new ArrayList();	
		FieldItem fielditem=new FieldItem();
		String fieldname="";
		if("1".equals(infokind))
		{
			fielditem=DataDictionary.getFieldItem("b0110");
			fieldname=fielditem.getItemid();
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			statitemlist.add(fielditem);
			fielditem=DataDictionary.getFieldItem("e0122");
			fieldname=fielditem.getItemid();
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			statitemlist.add(fielditem);
			fielditem=DataDictionary.getFieldItem("e01a1");
			fieldname=fielditem.getItemid();
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			statitemlist.add(fielditem);
			fielditem=DataDictionary.getFieldItem("a0101");
			fieldname=fielditem.getItemid();
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			statitemlist.add(fielditem);	
		}else if("2".equals(infokind))
		{
			fielditem=DataDictionary.getFieldItem("b0110");
			fieldname=fielditem.getItemid();
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			statitemlist.add(fielditem);
		}else if("3".equals(infokind))
		{
			fielditem=DataDictionary.getFieldItem("e0122");
			fieldname=fielditem.getItemid();
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			statitemlist.add(fielditem);
			fielditem=DataDictionary.getFieldItem("e01a1");
			fieldname=fielditem.getItemid();
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			statitemlist.add(fielditem);
		}
			
		HashSet fieldItemSet=getStatFieldItem(factors,this.userView);
		Iterator it = fieldItemSet.iterator();		
		while(it.hasNext())
		{
			   String item=(String)it.next();
			   if("2".equals(infokind))
			   {
				   if("b0110".equalsIgnoreCase(item))
					   continue;
			   }else if("3".equals(infokind))
			   {
				   if("e0122".equalsIgnoreCase(item)|| "e01a1".equalsIgnoreCase(item))
					   continue;
			   }if("1".equals(infokind)){
				   if("a0101".equalsIgnoreCase(item)|| "a0101".equalsIgnoreCase(item))
					   continue;
			   }
			   fielditem=DataDictionary.getFieldItem(item);
			   fieldname=fielditem.getItemid();
			   fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			   /**
			    * cmq changed at 20120427 因为多表（数据量大）关联查询速度比较慢，
			    * 查询引擎做了优化,子集指标不能直接从返回的SQL取得  
			    */
			   if(("1".equalsIgnoreCase(infokind)&&!fielditem.isMainSet())||("1".equalsIgnoreCase(infokind)&&!fielditem.getFieldsetid().toUpperCase().startsWith("A")))
				   continue;
			   statitemlist.add(fielditem);
		}		
		return statitemlist;
	}
	/**
	 * 得到统计项
	 * @param dao
	 * @param userView
	 * @param id
	 * @param norder
	 * @return
	 */
	public HashSet getStatFieldItem(String factors,UserView userView)
	{
		HashSet fieldItemSet = new HashSet();
		if(factors==null||factors.length()<=0)
			return fieldItemSet;		
		if(factors!=null&&factors.length()>0)
		{
			String factorArr[]=factors.split("`");
			String factorstr=""; 
			for(int i=0;i<factorArr.length;i++)
			{
				factorstr=factorArr[i];
				factorstr=factorstr.toUpperCase();
				com.hjsj.hrms.businessobject.parse.parsebusiness.Factor factor = new com.hjsj.hrms.businessobject.parse.parsebusiness.Factor(userView.getDbname(), factorstr);
				String item=factor.getItem();
				if(item!=null&&item.length()>0)
				{
					fieldItemSet.add(item);
				}						
			}
		}	
		return fieldItemSet;
	}
	private String getExpr(String expr)
    {
		int n=0;
        String stre="";
        String tem="";
        int inge=0;
        expr=PubFunc.keyWord_reback(expr);
       for(int i=0;i<expr.length();i++)
	        {
	          char ch =expr.charAt(i);
	          if(((i+1)!=expr.length())&&(ch>='0'&&ch<='9'))
	          {
	        	 stre=stre+ch;
	        	 inge=(int)(stre.length());
	        	 if(!(inge>1))
	        	 {
	        	   n++;
	               tem=tem+String.valueOf(n);
	        	 }
	          }
	          else
	          {
	      	  
		        if(ch>='0'&&ch<='9')
		        {
		        	stre=stre+ch;
		        	inge=(int)(stre.length());
		        	 if(!(inge>1))
		        	 {
		        	   n++;
		               tem=tem+String.valueOf(n);
		            
		             }
		        }
      
		        if(ch=='*'||ch=='+'||ch=='('||ch==')')
		        {
		        	stre="";
		        	tem=tem+ch;
		        }
		        
	          }
	        }    
        return tem;
    }
	private String getStrss(String expre,List list)throws GeneralException
    {
	  String find=(String)this.getFormHM().get("find");
       String strl="";
       int ncurr=0;
       StringBuffer str=new StringBuffer();
       expre=PubFunc.keyWord_reback(expre);
       try
       {
          for(int i=0;i<expre.length();i++)
          {
             char v =expre.charAt(i);
             if(((i+1)!=expre.length())&&(v>='0'&&v<='9'))
             {
        	    strl=strl+v;
              }
              else
              {
	            if(v>='0'&&v<='9')
	            {
	        	   strl=strl+v;
	             }
                if(!"".equals(strl))
                {
                   ncurr=Integer.parseInt(strl);
    	            Factor fc=(Factor)list.get(ncurr-1);
    	            str.append(fc.getFieldname().toUpperCase());
    	            fc.setOper(com.hjsj.hrms.utils.PubFunc.keyWord_reback(fc.getOper()));
    	            str.append(fc.getOper());
    	            String fieldname = fc.getFieldname();
    	            FieldItem field =DataDictionary.getFieldItem(fieldname);
    	            if("1".equals(find) && (fc.getCodeid()==null || "0".equalsIgnoreCase(fc.getCodeid())) &&fc.getValue().length()>0 &&("A".equalsIgnoreCase(field.getItemtype()) || "M".equalsIgnoreCase(field.getItemtype()) ))
    	            {
    	            	str.append("%");
    	            }
    	            str.append(fc.getValue());
    	            if("1".equals(find)&&fc.getValue().length()>0 && ("A".equalsIgnoreCase(field.getItemtype()) || "M".equalsIgnoreCase(field.getItemtype()) ))
    	            {
    	            	str.append("%");
    	            }
    	            str.append("`");
                }	        
	        strl="";
          }
        }      
     }
     catch(Exception ex)
     {
    	 ex.printStackTrace();
		 throw GeneralExceptionHandler.Handle(ex);    	 
     }
     return str.toString();
    }
	
	private String getStart(int i){
		String [] str={"A","B","C","D","E","F","G","H","I","J","K","O","P","Q","R","S","T","U","V","X","Y","Z"};
		return str[i%str.length];
	}
	
	private void rebackKeyword(ArrayList list){
		for(int i=0;i<list.size();i++){
			Factor factor = (Factor)list.get(i);
			String hz = factor.getHz();
			String oper = factor.getOper();
			String log = factor.getLog();
			String value = factor.getValue();
			String hzvalue = factor.getHzvalue();
			hz = PubFunc.hireKeyWord_filter_reback(hz);
			oper = PubFunc.hireKeyWord_filter_reback(oper);
			log = PubFunc.hireKeyWord_filter_reback(log);
			value = PubFunc.hireKeyWord_filter_reback(value);
			hzvalue = PubFunc.hireKeyWord_filter_reback(hzvalue);
			factor.setHz(hz);
			factor.setOper(oper);
			factor.setLog(log);
			factor.setValue(value);
			factor.setHzvalue(hzvalue);
		}
	}
}
