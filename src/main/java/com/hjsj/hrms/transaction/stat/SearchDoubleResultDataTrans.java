/*
 * Created on 2005-6-30
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.businessobject.stat.GeneralQueryStat;
import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchDoubleResultDataTrans extends IBusiness {

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

		}else if("2".equals(flag)){
			mainset.add(DataDictionary.getFieldItem("b0110"));
		}else{
			mainset.add(DataDictionary.getFieldItem("e01a1"));
		}
		return mainset;
	}
	
    private String getMainQueryFields(ArrayList list)
    {
        StringBuffer strfields=new StringBuffer();
        int j=0;
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
		try{
			String userbases=(String)this.getFormHM().get("userbases");
			userbases=userbases==null?"":userbases;
			userbases=userbases.toUpperCase();
			String userbase=(String)this.getFormHM().get("userbase");
			String statId=(String)this.getFormHM().get("statid");
			String querycond=(String)this.getFormHM().get("querycond");
			String infokind=(String)this.getFormHM().get("infokind");
			int v=(int)Integer.parseInt((String)this.getFormHM().get("v"));
			int h=(int)Integer.parseInt((String)this.getFormHM().get("h"));
			String statid = (String) ((HashMap)this.formHM.get("requestPamaHM")).get("statid");
			String sql1="select * from sname where id=?";
			ArrayList sqlList = new ArrayList();
			String isnotie = (String) ((HashMap)this.formHM.get("requestPamaHM")).get("isnotie");
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String type = (String) hm.get("type");
			if((!"true".equalsIgnoreCase(isnotie) && statid != null && statid.trim().length() > 0 && !"null".equalsIgnoreCase(statid)) ||("cross".equalsIgnoreCase(type)&& !"null".equalsIgnoreCase(statid))){
				sqlList.add(((HashMap)this.formHM.get("requestPamaHM")).get("statid"));//获取 传入的statid 不拿form  wangb 20190515 bug 47471
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				try {
					this.frowset=dao.search(sql1,sqlList);
					if(this.frowset.next())
					{
						userbases=this.frowset.getString("nbase");
						userbases = userbases == null?"":userbases.replaceAll(",", "`");
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				this.getFormHM().put("userbases",userbases);
			}
			//前台显示组织机构过滤信息加密，后台解密
			String e_querycond = (String) ((HashMap)this.formHM.get("requestPamaHM")).get("e_querycond");
			((HashMap)this.formHM.get("requestPamaHM")).remove("e_querycond");
			if(e_querycond != null && "1".equalsIgnoreCase(e_querycond)) {
				querycond = PubFunc.decrypt(querycond);
				this.getFormHM().put("querycond", querycond);
			}
			
		    String[] curr_id=(String[])this.getFormHM().get("curr_id");
		    String preresult=(String)this.getFormHM().get("preresult");
		    String history=(String)this.getFormHM().get("history");
		    String lexprId=(String)hm.get("lexprId");//常用查询条件
		    
			if(lexprId==null){
				lexprId=(String)this.getFormHM().get("lexprId");
				this.getFormHM().remove("lexprId");
			}
			String[] lengthwayslist = null;
			String[] crosswiselist = null;
			String vtotal = "";
			String htotal = "";
			String vnull = "";
			String hnull = "";
			//zgd 2014-8-27 二维交叉统计表点击事件传入type=cross
			if("cross".equalsIgnoreCase(type)){
				querycond = "";
				curr_id = null;
				//userbases = (String) hm.get("dbname");
				userbases=userbases==null?"":userbases;
				userbases = userbases.replaceAll(",", "`");
				//liuy 2014-12-5 5737：员工管理：多维统计，导出excel时显示空白页面，后台报错 start
				this.getFormHM().put("userbases",userbases);
				//liuy 2014-12-5 end
				infokind = "1";
				userbase = "Usr";
				String lengthways = (String)hm.get("lengthways");
				String crosswise = (String)hm.get("crosswise");
				String[] lengthlist = lengthways.split(",");
				String[] crosslist = crosswise.split(",");
				lengthwayslist = new String[lengthlist.length];
				crosswiselist = new String[crosslist.length];
				for(int i = 0;i < lengthlist.length;i++){
					lengthwayslist[lengthlist.length-1-i] = lengthlist[i];
				}
				for(int i = 0;i < crosslist.length;i++){
					crosswiselist[crosslist.length-1-i] = crosslist[i];
				}
				vtotal = (String) hm.get("vtotal");
				vtotal=vtotal==null||vtotal.length()==0?"0":vtotal;
				htotal = (String) hm.get("htotal");
				htotal=htotal==null||htotal.length()==0?"0":htotal;
				vnull = (String) hm.get("vnull");
				vnull=vnull==null||vnull.length()==0?"0":vnull;
				hnull = (String) hm.get("hnull");
				hnull=hnull==null||hnull.length()==0?"0":hnull;
			}
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
		    //组织机构筛选过滤start   wangb 20190822
	   		String filterId = (String)this.formHM.get("filterId");
	   		String org_filter = (String)this.formHM.get("org_filter");
	   		String req_org_filter = (String) ((HashMap)this.formHM.get("requestPamaHM")).get("org_filter");
	   		if(org_filter != null && ("1".equalsIgnoreCase(org_filter) || "1".equalsIgnoreCase(req_org_filter))) {
	   			querycond = "";
	   			curr_id = null;
	   		}
	   		hm.clear();
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
		    if("0".equals(preresult))
		    	preresult="2";
			  boolean isresult=true;
			    String result=(String)this.getFormHM().get("result");
			    if(result==null|| "".equals(result))
			    {
			    	StringBuffer sql =new StringBuffer();
					sql.append("select flag from SName where id=");
					sql.append(statId == null|| statId.trim().length()==0? "''":statId);
					List rs =ExecuteSQL.executeMyQuery(sql.toString());
					if (!rs.isEmpty()) {
						LazyDynaBean rec=(LazyDynaBean)rs.get(0);
						String flag = rec.get("flag")!=null?rec.get("flag").toString():"";
						if(flag!=null&& "1".equals(flag))
							isresult=false; //false时才查询，查询结果表
					}
			    }else if("1".equals(result))
			    	isresult=false; 
			StringBuffer orderby=new StringBuffer();
			orderby.append(" order by ");
			//orderby.append("a0000");
			orderby.append("db,a0000");
			
			 //加上常用查询进行的统计
		    String commlexr=null;
		    String commfacor=null;
			GeneralQueryStat generalstat=new GeneralQueryStat();
			generalstat.setInfokind(infokind);
			generalstat.getGeneralQueryLexrfacor(curr_id,userbase,history,this.getFrameconn());
			/**人员主集指标*/
			ArrayList mainlist=new ArrayList();
			String columns="";
			
		    if(curr_id!=null)
		    {
		    	commlexr=generalstat.getLexpr();
		    	commfacor=generalstat.getLfactor();
		    }
		    /* zgd 2014-8-13 领导桌面，二维统计，修改人员范围（分类）时，将常用统计条件传入到sql语句当中 start*/
		    //二维交叉统计人员范围
		    //【7517】员工管理-常用统计-点击二维统计-反查不出来（后台报错，关键字“NULL”附近有语法错误）  jingq upd 2015.02.11
		    //if((commlexr==null||commfacor==null) || "cross".equalsIgnoreCase(type)){
		    if((commlexr==null||commfacor==null) && "cross".equalsIgnoreCase(type)){
				commlexr = (String)this.getFormHM().get("commlexr");
				commlexr = PubFunc.keyWord_reback(commlexr);
				commfacor = (String)this.getFormHM().get("commfacor");
				commfacor = PubFunc.keyWord_reback(commfacor);
		    }
		    
		    
	   		 if(filterId != null && filterId.trim().length()>0 && !"UN".equalsIgnoreCase(filterId)){
	   			
	   			if(AdminCode.getCode("UN", filterId)!= null){
	   				filterId = "b0110="+filterId+"*";
	   			}
	   			if(AdminCode.getCode("UM", filterId)!= null){
	   				filterId = "e0122="+filterId+"*";
	   			}
	   			if((commlexr == null || commlexr.trim().length()==0) || (commfacor == null || commfacor.trim().length()==0)){
	   				commlexr = "1";
		   			commfacor = filterId;
	   			}else{
	   				String[] style=new StatDataEncapsulation().getCombinLexprFactor(commlexr,commfacor,"1",filterId);
	   				commlexr = style[0];
	   				commfacor = style[1];
	   			}
	   		}
	   		 
		    /* zgd 2014-8-13 领导桌面，二维统计，修改人员范围（分类）时，将常用统计条件传入到sql语句当中 end*/
		    String sql = "";
		    if("cross".equalsIgnoreCase(type)){
		    	sql=new StatDataEncapsulation().getDataSQL(lengthwayslist,crosswiselist,userbase,querycond,v,h,userView.getUserName(),userView.getManagePrivCode(),userView,infokind,true,commlexr,commfacor,preresult,history,userbases,vtotal,htotal,vnull,hnull);
		    }else{
		    	sql=new StatDataEncapsulation().getDataSQL(Integer.parseInt(statId),userbase,querycond,v,h,userView.getUserName(),userView.getManagePrivCode(),userView,infokind,isresult,commlexr,commfacor,preresult,history);
		    }
		    StringBuffer strsql=new StringBuffer();
	    	mainlist=getMainFieldList(infokind);
		    columns=getMainQueryFields(mainlist);
		    if("1".equals(infokind))
		    {
		        /*strsql.append("select a0000,");
		        strsql.append(userbase);
		        strsql.append("a01.a0100 ,");
		        strsql.append(userbase);        
		        strsql.append("a01.b0110 b0110,");
	            strsql.append(userbase);
	            strsql.append("a01.e0122 e0122");
		        strsql.append(",");
		        strsql.append(columns);
		        strsql.append(",UserName ");*/
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
	            
		        columns=(","+columns.toLowerCase()).replaceAll(",e01a1", "").replaceAll(",e0122", "").replaceAll(",b0110", "").replaceAll(",a0100", "");
		        //strsql.append(",");
		        strsql.append(columns);
		        strsql.append(",UserName ");
		        
		        userbase=userbase.toUpperCase();
		        String tmpsql =(strsql.toString()+sql).toUpperCase();
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
		        sql=sb.toString()+") tt";
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
		        this.getFormHM().put("columns", columns.toUpperCase()+",UserName,B0110,E0122,E01A1,A0100,DB");
		        this.getFormHM().put("distinct", "");
		       this.getFormHM().put("order_by",orderby.toString());
		    }else if("2".equals(infokind))
		    {
		    	 strsql.append("select ");
			     strsql.append("b01.B0110 ");
			     this.getFormHM().put("order_by","");
			     this.getFormHM().put("distinct", "B01.B0110");
			     this.getFormHM().put("columns", columns.toUpperCase()+",UserName,B0110,E0122,A0100");
		    }else if("3".equals(infokind))
		    {
		    	 strsql.append("select ");
			     strsql.append("k01.e01a1 ");
			     this.getFormHM().put("order_by","");
			     this.getFormHM().put("distinct", "K01.e01a1");
			     this.getFormHM().put("columns", columns.toUpperCase()+",UserName,B0110,E0122,A0100");
		    }
		    this.getFormHM().put("fieldlist",mainlist);
	        /**浏览信息用的卡片*/
	    	this.getFormHM().put("tabid", searchCard(infokind)); 			    
		    this.getFormHM().put("strsql",strsql.toString());
		    this.getFormHM().put("cond_str",sql);
		    this.userView.getHm().put("staff_sql", sql);//liuy 2014-11-17 修改多维交叉统计导出excel不对
		    //zgd 2014-8-27 二维交叉统计点击二维表之前未将infokind传入后台，前台页面需要判断
	        this.getFormHM().put("infokind",infokind);
	    }catch(Exception e)
		{
	      	e.printStackTrace();
	    	throw GeneralExceptionHandler.Handle(e);
	    }
	}

	private String getStart(int i){
		String [] str={"A","B","C","D","E","F","G","H","I","J","K","O","P","Q","R","S","T","U","V","X","Y","Z"};
		return str[i];
	}
}
