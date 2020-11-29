 /*
 * Created on 2005-6-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.businessobject.parse.parsebusiness.Factor;
import com.hjsj.hrms.businessobject.stat.GeneralQueryStat;
import com.hjsj.hrms.businessobject.stat.StatCondAnalyse;
import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.CombineFactor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.*;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchStatDataTrans extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5827243513097033002L;
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
    			if("b0110,e01a1,e0122".indexOf(item.getItemid().toLowerCase())!=-1)
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
    
	public void execute() throws GeneralException {
	 	// TODO Auto-generated method stub
		String home = (String) this.getFormHM().get("home");//直接冲配置的统计条件配置，穿透统计图 访问  需添加 home 参数
		if(home == null || home.trim().length()==0)
			this.getFormHM().put("home", "0");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		StatCondAnalyse cond = new StatCondAnalyse();
		StringBuffer orderby=new StringBuffer();
		//liuy 2014-10-24  4717:组织机构-岗位管理-岗位设置-统计分析（统计项与反查结果不符的问题） 将form取值改为了hm取值
		String showflag=(String)hm.get("showflag");
		hm.remove("showflag");
		String lexprId=(String)hm.get("lexprId");//常用查询条件
		if(lexprId==null){
			lexprId=(String)this.getFormHM().get("lexprId");
			this.getFormHM().remove("lexprId");
		}
		String strlexpr="";
		String history="";
		String strfactor="";
		String statid=(String)hm.get("statid");
		if(showflag!=null&& "1".equals(showflag))//点击图例触发，传入的是汉字
		{
			String showLegend=(String)hm.get("showLegend");
			if(showLegend==null||showLegend.length()<=0)
				showLegend="";
			showLegend=SafeCode.decode(showLegend);
			showLegend=showLegend.replaceAll("\n", "");
			showLegend=showLegend.replaceAll("\r", "");
			LazyDynaBean bean=getStatDataForName(statid ,showLegend);
			history=(String)bean.get("history");
			strlexpr=(String)bean.get("lexpr");
			strfactor=(String)bean.get("factor");
			strfactor=strfactor+"`";
			//【9397】员工管理-统计分析-常用统计中的123的可以统计出来是3个，但是反查进去就是空的 jingq add 2015.05.06
			strfactor = PubFunc.keyWord_reback(strfactor);
		}else
		{
			strlexpr=(String)hm.get("strlexpr");
			hm.remove("strlexpr");
			if(strlexpr!=null)
			  strlexpr=strlexpr.replaceAll("a","+");
			strlexpr=PubFunc.keyWord_reback(strlexpr);
			history=(String)hm.get("history");
			strfactor=(String)hm.get("strfactor");	
			strfactor=SafeCode.decode(strfactor)+ "`";
			strfactor=PubFunc.keyWord_reback(strfactor);
		}
		String userbases=(String)this.getFormHM().get("userbases"); 
		userbases=userbases==null?"":userbases;
		//userbases=userbases.toUpperCase();
		String userbase=(String)this.getFormHM().get("userbase");
		String querycond=(String)this.getFormHM().get("querycond");
		String infokind=(String)hm.get("infokind");
		//liuy end
		/* 标识：1785 员工管理/统计分析/常用统计，选择具体统计项后，不显示数据 xiaoyun 2014-6-10 start */
		if(StringUtils.isEmpty(infokind)) {
			infokind = "1";
			this.getFormHM().put("flag", "12");
			this.getFormHM().put("infokind", infokind);
		}
		/* 标识：1785 员工管理/统计分析/常用统计，选择具体统计项后，不显示数据 xiaoyun 2014-6-10 end */
		/* 标识：2249 主页常用统计点更多进去和单位管理/信息维护/统计分析，“单位编制情况”统计出来的结果不同 xiaoyun 2014-7-3 start */
		List temp = ExecuteSQL.executeMyQuery("select * from SName where id="+statid);
		if (!temp.isEmpty()) {
			LazyDynaBean rec=(LazyDynaBean)temp.get(0);
			infokind = rec.get("infokind") != null?rec.get("infokind").toString():"";
			this.getFormHM().put("infokind", infokind);
			temp = null;
		}
		/* 标识：2249 主页常用统计点更多进去和单位管理/信息维护/统计分析，“单位编制情况”统计出来的结果不同 xiaoyun 2014-7-3 end */
	    String[] curr_id=(String[])this.getFormHM().get("curr_id");
	    String preresult=(String)this.getFormHM().get("preresult");	 
	    if(preresult==null||preresult.length()<=0)
	    	preresult="2";
	    if("1".equals(preresult))
	    	curr_id=null;
	    if(lexprId!=null&&lexprId.length()>0)
		{
			//加上常用查询进行的统计
			curr_id=new String[1];
			curr_id[0]=lexprId;			
		}
		cat.debug("----strlexpr----->" + strlexpr);
		cat.debug("----strfactor----->" + strfactor);
		try{
		String columns="";
		ArrayList mainlist=new ArrayList();
		String wheresql="";
		StringBuffer strsql=new StringBuffer();
		if("0".equals(preresult))
	    	preresult="2";
		
        /*boolean isresult=true;
		String result=(String)this.getFormHM().get("result");
		if(result==null||result.equals("")||result.equals("0"))
		{
			StringBuffer sql =new StringBuffer();
			sql.append("select flag from SName where id=");
			sql.append(statid);
			List rs =ExecuteSQL.executeMyQuery(sql.toString());
			if (!rs.isEmpty()) {
				LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				String flag = rec.get("flag")!=null?rec.get("flag").toString():"";
				if(flag!=null&&flag.equals("1"))
					isresult=false; //false时才查询，查询结果表							
			}
		}
		else
		    	isresult =false;*/
		
		boolean isresult=true;	  
	    String result=(String)this.getFormHM().get("result");
	    if(result==null|| "".equals(result))
	    {
	    	StringBuffer sql =new StringBuffer();
			sql.append("select flag from SName where id=");
			sql.append(statid);
			List rs =ExecuteSQL.executeMyQuery(sql.toString());
			if (!rs.isEmpty()) {
				LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				String flag = rec.get("flag")!=null?rec.get("flag").toString():"";
				if(flag!=null&& "1".equals(flag))
					isresult=false; //false时才查询，查询结果表
			}
	    }else if("1".equals(result))
	    	isresult=false; 	    
	    /*if(this.userView.getStatus()!=0)
	    	isresult=true; */
	    //strlexpr=strlexpr.replaceAll("a","+");
	    String type = (String) hm.get("type");
	    try
	    {
	    	boolean ishavehistory=false;
	    	
	        //zxj 20150604 不论人员、单位还是岗位(infokind 1 2 3)都有“历史记录”选项
            if(history!=null&& "1".equals(history))
                ishavehistory=true;
				
            //组织机构筛选过滤start  wangb 20190822 
            String filterId = (String)this.formHM.get("filterId");
            String org_filter = (String)this.formHM.get("org_filter");
            if("1".equalsIgnoreCase(org_filter) || "cross".equalsIgnoreCase(type)) {//走按组织机构筛选， 不走 按统计范围机构 or 从多维统计 图穿透展现一维数据 去掉统计范围
            	querycond = "";
            	curr_id = null;
            }
            if(filterId != null && filterId.trim().length()>0 && !"UN".equalsIgnoreCase(filterId)){
            	if(AdminCode.getCode("UN", filterId)!= null){
            		filterId = "b0110="+filterId+"*";
            	}
            	if(AdminCode.getCode("UM", filterId)!= null){
            		filterId = "e0122="+filterId+"*";
            	}
            	String[] style=new StatDataEncapsulation().getCombinLexprFactor(strlexpr,strfactor,"1",filterId);
            	strlexpr = style[0];
            	strfactor = style[1];
            }
	    	if(infokind!=null && "1".equals(infokind))
			{
	    		
	    		 //组织机构筛选过滤end
	    		
	    		 //liuy 2014-11-3 固定多层常用统计图穿透，最后穿人员列表时的人员库 start
	    		 ArrayList dblist=new ArrayList();
	    		 String firstStatId = (String)hm.get("firstStatId");
	    		 hm.remove("firstStatId");
	    		 if(firstStatId!=null){
	    			 dblist=getDBListBean(firstStatId);
	    		 }else{
	    			 dblist=getDBListBean(statid);	    			 
	    		 }
	    		 //liuy end
	    		 if(userbase==null||userbase.trim().length()<=0|| "ALL".equalsIgnoreCase(userbase))
	    		 {
	    		    	if(dblist!=null&&dblist.size()>0)
	    		    	{
	    		    		CommonData da=(CommonData)dblist.get(0);
	    		    		if(da.getDataValue()==null || da.getDataValue().trim().length()==0){
	    		    			if(userView.getPrivDbList().size() == 0)
	    		    				throw new GeneralException(ResourceFactory.getProperty("errors.static.notdbname"));
	    		    			userbase=(String) userView.getPrivDbList().get(0);
	    		    		}else{
	    		    			userbase=da.getDataValue();
	    		    		}
	    		    		userbase=da.getDataValue()==null || da.getDataValue().trim().length()==0? "Usr":da.getDataValue();
	    		    	}else
	    		    		userbase="Usr";   		    		
	    		    	  
	    		 }
				 if(curr_id==null)
				 {
				    	Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
				    	String stat_id=sysbo.getValue(Sys_Oth_Parameter.STAT_ID);
				    	if(stat_id!=null&&stat_id.length()>0&&!"#".equals(stat_id))
				    	{
				    		String[] stat_ids=new String[1];
				    		stat_ids[0]=stat_id;
				    		curr_id=stat_ids;
				    	}
				 }
				 
					
			    //加上常用查询进行的统计
				GeneralQueryStat generalstat=new GeneralQueryStat();
				generalstat.getGeneralQueryLexrfacor(curr_id,userbase,history,this.getFrameconn());
					/**人员主集指标*/
				String commlexr=null;
			    String commfacor=null;
				if(curr_id!=null)
			    {
			    	commlexr=generalstat.getLexpr();
			    	commfacor=generalstat.getLfactor();
			    }
				/* zgd 2014-8-27 二维交叉统计 start*/
				if("cross".equalsIgnoreCase(type)){
					userbases = (String) hm.get("dbname");
					userbases=userbases==null?"":userbases;
					userbases = userbases.replaceAll(",", "`");
					commlexr = (String)hm.get("commlexr");
					commlexr = SafeCode.decode(commlexr);
					commlexr = PubFunc.keyWord_reback(commlexr);
					commfacor = (String)hm.get("commfacor");
					commfacor = SafeCode.decode(commfacor);
					commfacor = PubFunc.keyWord_reback(commfacor);
					//commfacor = PubFunc.keyWord_reback(commfacor);
					hm.clear();
				}
				/* zgd 2014-8-27 二维交叉统计 end*/
				if((curr_id!=null&&curr_id.length>0&&curr_id[0].length()>0) || ("cross".equalsIgnoreCase(type) && !"".equals(commlexr) && !"".equals(commfacor)))
				{
						//add by xiegh ondate 20180111 bug34861
						// CombineFactor把多个因子表达式组合成一个表达式，也是用|作为分割符，所以对于字符型多选，如要用到多个因子表达式合并，开发人员需将|替换成~ 
						commfacor = commfacor.replaceAll("\\|", "~");
						String[] style=new StatDataEncapsulation().getCombinLexprFactor(strlexpr,strfactor,commlexr,commfacor);
					    if(style!=null && style.length==2)
					    {
					    	strlexpr=style[0];
					    	strfactor=style[1];
					    	
					    	
					    }
				}
				//liuy 2014-10-10 4553 一维常用统计支持钻取，即穿透到另一个一维常用统计 start
		    	String subIndex=(String) hm.get("subIndex");
		    	hm.remove("subIndex");
				if("next".equals(subIndex)){
		    		ArrayList lexprFactor=new ArrayList();
		    		commlexr = (String)hm.get("commlexr");
					commlexr = SafeCode.decode(commlexr);
					commlexr = PubFunc.keyWord_reback(commlexr);
					commfacor = (String)hm.get("commfacor");
					commfacor = SafeCode.decode(commfacor);
					commfacor = PubFunc.keyWord_reback(commfacor);
		    		
					// add by xiegh ondate 20180111 bug34861
					// CombineFactor把多个因子表达式组合成一个表达式，也是用|作为分割符，所以对于字符型多选，如要用到多个因子表达式合并，开发人员需将|替换成~ 
					commfacor = commfacor.replaceAll("\\|", "~");
					
					lexprFactor.add(commlexr + "|" + commfacor);
					lexprFactor.add(strlexpr + "|" + strfactor);
					CombineFactor combinefactor=new CombineFactor();
					String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
					StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
					if(Stok.hasMoreTokens())
					{
						strlexpr=Stok.nextToken();
						strfactor=Stok.nextToken();
					}
					this.getFormHM().put("subIndex", "");
					//liuy 2014-10-10 end
		    	}
				
				if("1".equalsIgnoreCase(generalstat.getHistory()))
					ishavehistory=true;
				
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				
				wheresql=cond.getCondQueryString(strlexpr,strfactor,userbase,ishavehistory,userView.getUserName(),querycond,userView,infokind,isresult,false);
				if(wheresql.contains("本单位")){
			    	String sss = userView.getUnit_id();
			    	sss=sss.replace("UN","");
			    	if(sss.length()==1||sss.length()==0){
			    		wheresql=wheresql.replace("='本单位'", "<>null");
			    		wheresql=wheresql.replace("<>'本单位'", "=null");
			    	}else{
			    		sss=sss.substring(0,sss.length()-1);
			    		sss=sss.replace("`", ",");
			    		wheresql=wheresql.replace("='本单位'", " in("+sss+")");
			    		wheresql=wheresql.replace("<>'本单位'", " not in("+sss+")");
			    	}
			    }
	        	//mainlist=getMainFieldList(infokind);
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
		        
		        //userbase=userbase.toUpperCase();
		        String tmpsql =(strsql.toString()+wheresql)/*.toUpperCase()*/;
		        StringBuffer sb = new StringBuffer();
		        //tiany add 支持全部人员库统计图穿透
		        if(userbases!=null&& "ALL".equalsIgnoreCase(userbases)&&dblist!=null){
                    userbases="";
                    for(int i = 0;i<dblist.size();i++)
                    {
                        CommonData da=(CommonData)dblist.get(i);
                        if(i==0){
                            userbases+=da.getDataValue();  
                        }else{
                            userbases+="`"+da.getDataValue(); 
                        }
                    }
                }
		        //tiany and end
		        if(userbases!=null&&userbases.length()>0){
			        if(userbases.indexOf("`")==-1){
			        	userbases = ("".equals(userbases.trim()))? userbase:userbases;//没有设置人员库条件处理 wangb 20180822
						sb.append(" from ("+tmpsql.replaceAll(userbase, userbases).replaceAll("##", "'"+getStart(0)+userbases+"'")+"");
					}else{
						String[] tmpdbpres=userbases.split("`");
						ArrayList dbPrilist = userView.getPrivDbList();
						String dbPri = ","+StringUtils.join(dbPrilist.toArray(new String[dbPrilist.size()]),",")+",";
						for(int n=0;n<tmpdbpres.length;n++){
							String tmpdbpre=tmpdbpres[n];
							if(dbPri.toLowerCase().indexOf(","+tmpdbpre.toLowerCase()+",") == -1/*!dbPrilist.contains(tmpdbpre)*/)//过滤 人员库不存在情况  wangb 20180717 bug 38893
								continue;
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
		        /*
			    strsql.append("select ");
			    strsql.append(userbase);
			    strsql.append("A01.A0100,B0110,E0122,E01A1,A0101,UserName ");
			    */
				orderby.append(" order by ");
				//orderby.append("B0110,E0122,");
				orderby.append("db,a0000");
				this.getFormHM().put("columns", columns.toUpperCase()+",UserName,B0110,E0122,E01A1,A0100,DB");
				String flag=(String)this.getFormHM().get("flag");
				if(flag!=null&& "13".equals(flag))
				{
					this.getFormHM().put("nbaselist", dblist);
					
				}
				this.getFormHM().put("distinct","");
		    }else if(infokind!=null && "2".equals(infokind)){
		    	//System.out.println(strlexpr + strfactor + querycond);
		    	wheresql=cond.getCondQueryString(strlexpr,strfactor,"B",ishavehistory,userView.getUserName(),querycond,userView,infokind,isresult,false);
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
				wheresql=cond.getCondQueryString(strlexpr,strfactor,"K",ishavehistory,userView.getUserName(),querycond,userView,infokind,isresult,false);
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
				this.getFormHM().put("columns", columns.toUpperCase()+",E0122,E01A1");
				this.getFormHM().put("distinct", "K01.e01a1");
				//columns=getStatItemList(strfactor,infokind);  
				//this.getFormHM().put("columns",);
				
			}
	    	String tmp=(String)this.getFormHM().get("uplevel");
	    	if(tmp==null||tmp.length()==0){
	    		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
	            String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
	        	if(uplevel==null||uplevel.length()==0)
	        		uplevel="0";
	        	this.getFormHM().put("uplevel", uplevel);	
	    	}
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    	throw GeneralExceptionHandler.Handle(e);
	    }
		
		
	    
        this.getFormHM().put("strsql",strsql.toString());
       
	    this.getFormHM().put("cond_str",wheresql); 
	    this.userView.getHm().put("staff_sql", wheresql);
	    //System.out.println(strsql.toString()+wheresql);
	    this.getFormHM().put("fieldlist",mainlist);
	    
        /**浏览信息用的卡片*/
        this.getFormHM().put("tabid", searchCard(infokind)); 	    
	    this.getFormHM().put("order_by",orderby.toString());
		}catch(Exception e){}
		//userbase=userbase.substring(0,1)+userbase.substring(1).toLowerCase();
		this.getFormHM().put("userbase", userbase);
		this.getFormHM().put("userbases", userbases);
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
			   if("1".equals(infokind)){//LiWeichao
				   if("b0110".equalsIgnoreCase(item)|| "e0122".equalsIgnoreCase(item)|| "e01a1".equalsIgnoreCase(item)|| "a0101".equalsIgnoreCase(item))
					   continue;
			   }
			   else if("2".equals(infokind))
			   {
				   if("b0110".equalsIgnoreCase(item))
					   continue;
			   }else if("3".equals(infokind))
			   {
				   if("b0110".equalsIgnoreCase(item)|| "e0122".equalsIgnoreCase(item)|| "e01a1".equalsIgnoreCase(item))
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
					Factor factor = new Factor(userView.getDbname(), factorstr);
					String item=factor.getItem();
					if(item!=null&&item.length()>0)
					{
						fieldItemSet.add(item);
					}			
			}
		}	
		return fieldItemSet;
	}
	/**
	 * 
	 * @param statida
	 * @param name
	 * @return
	 */
	private LazyDynaBean getStatDataForName(String statid ,String name)
	{
		String sql="select * from slegend where id="+statid+" and legend='"+name+"'";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		LazyDynaBean bean=new LazyDynaBean();
		try {
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				bean.set("lexpr", this.frowset.getString("lexpr")!=null?this.frowset.getString("lexpr"):"");
				bean.set("factor", this.frowset.getString("factor")!=null?this.frowset.getString("factor"):"");
				bean.set("norder", this.frowset.getString("norder")!=null?this.frowset.getString("norder"):"");
				bean.set("history", this.frowset.getString("flag")!=null?this.frowset.getString("flag"):"");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bean;
	}
	
	private ArrayList getDBListBean(String statid)
	{
		ArrayList dblist=(ArrayList)this.getFormHM().get("dblist");
		ArrayList list=new ArrayList();
		if(dblist==null||dblist.size()<=0)
		{
			dblist=new ArrayList();
			String sql1="select * from sname where id='"+statid+"'";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String condid="";
			try {
				this.frowset=dao.search(sql1);
				if(this.frowset.next())
				{
					String userbase=this.frowset.getString("nbase");
					if(userbase!=null&&userbase.length()>0)
					{
						String [] baseS=userbase.split(",");
						for(int i=0;i<baseS.length;i++)
						{
							if(baseS[i]!=null&&baseS[i].length()>0){
								if(this.userView.hasTheDbName(baseS[i]))
									dblist.add(baseS[i]);
							}
						}
					}
					if(dblist==null||dblist.size()<=0)
						dblist=this.userView.getPrivDbList();					
				}				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(dblist!=null&&dblist.size()>0)
		{
			CommonData da=null;
			for(int i=0;i<dblist.size();i++)
			{
				String name=AdminCode.getCodeName("@@",dblist.get(i).toString());
				da=new CommonData();
				da.setDataValue(dblist.get(i).toString());
				da.setDataName(name);
				list.add(da);
			}			
		}
		return list;
	}
	private String getStart(int i){
		String [] str={"A","B","C","D","E","F","G","H","I","J","K","O","P","Q","R","S","T","U","V","X","Y","Z"};
		return str[i];
	}
}
