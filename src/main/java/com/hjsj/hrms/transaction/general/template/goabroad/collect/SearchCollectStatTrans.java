package com.hjsj.hrms.transaction.general.template.goabroad.collect;

import com.hjsj.hrms.businessobject.general.template.collect.CollectStat;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/**
 * 浏览汇总统计
 * <p>Title:SearchCollectStatTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Nov 24, 2006 10:18:04 AM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class SearchCollectStatTrans  extends IBusiness {


	public void execute() throws GeneralException 
	{
		String subset=(String)this.getFormHM().get("subset");
		String nbase=(String)this.getFormHM().get("nbase");
		String code = (String) this.getFormHM().get("code");
		String kind = (String) this.getFormHM().get("kind");  
		ArrayList nbaselist=(ArrayList)this.getFormHM().get("nbaselist");
		String fileset=(String)this.getFormHM().get("fileset");
		String select_stat=(String)this.getFormHM().get("select_stat");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String sort_field = (String)hm.get("sort_field");
		String sort_flag = (String)hm.get("sort_flag");
		if(select_stat==null||select_stat.length()<=0)
			select_stat="";
		if(fileset==null||fileset.length()<=0)
		{
			fileset="-1";
		}
		if(subset==null||subset.length()<=0)
		  throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("goabroad.collect.no.subset"),"",""));
		nbaselist=getNbaseList(nbaselist);
		nbase=getNbase(nbase,nbaselist);
		ArrayList columnlist = DataDictionary.getFieldList(subset,Constant.USED_FIELD_SET);
		String Privtable = this.userView.analyseTablePriv(subset);
		if(!"0".equals(Privtable)){
			for(Iterator it = columnlist.iterator();it.hasNext();){
				FieldItem fielditem = (FieldItem)it.next();
				String itemid = fielditem.getItemid();
				String privField = this.userView.analyseFieldPriv(itemid);
				if("0".equals(privField)){
					it.remove();
				}
			}
		}else{
			columnlist = new ArrayList();
		}
		CollectStat collectStat=new CollectStat(this.getFrameconn());
		columnlist=collectStat.getColumnlist(columnlist);
		ArrayList filelist=collectStat.getFieldList(columnlist);
		String columns=collectStat.getFieldString(columnlist,nbase);
		String columns_sql=collectStat.getSQLFieldString(columnlist,nbase);
		StringBuffer sqlstr=new StringBuffer();
		sqlstr.append("select "+columns_sql);
		String where="from "+nbase+subset+","+nbase+"A01";		
		where =where+collectStat.getWhere(code,kind,nbase,this.userView,subset);	
		if("1".equals(select_stat))
		{
			String isWhere=(String)this.getFormHM().get("isWhere");
			where=where+" "+isWhere;
		}
		//System.out.println(sqlstr.toString()+" "+where);
		
		String orderby="";
		if(sort_field != null && sort_field.length() > 0){
			if("1".equals(sort_flag)){
				orderby = "order by " +  sort_field + " DESC";
				this.getFormHM().put("sort_sign", "▼");
			} else if("2".equals(sort_flag)){
				orderby = "order by " +  sort_field + " ASC";
				this.getFormHM().put("sort_sign", "▲");
			}
			hm.remove("sort_field");
			this.getFormHM().put("sort_field", sort_field);
			String ordwhere = (String)this.getFormHM().get("where");
			ordwhere = ordwhere == null? "":ordwhere;
			where = ordwhere == ""?where:ordwhere;
		}else{
//			orderby="order by "+nbase+subset+".CreateTime desc";
			orderby = "order by A0000,i9999 ASC";
			this.getFormHM().remove("sort_field");
			String whereIn = (String)hm.get("whereIN");
			if(where.indexOf("where") != -1 && whereIn != null){
				where +=  " AND (" +  whereIn + ")";
			}else if(whereIn != null){
				where += " where " + whereIn;
			}
			hm.remove("whereIN");
		}
		
		// 获得部门层级
		String uplevel = "";
		Sys_Oth_Parameter sys = new Sys_Oth_Parameter(this.frameconn);
		uplevel = sys.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
		uplevel=uplevel!=null&&uplevel.trim().length()>0?uplevel:"0";
		
//		String whereIn = (String)this.getFormHM().get("whereIN");
		
		this.getFormHM().put("uplevel",uplevel);
		this.getFormHM().put("strsql",sqlstr.toString());
		this.getFormHM().put("where",where);
		
		this.getFormHM().put("columnlist",columnlist);
		this.getFormHM().put("columns",columns);
		this.getFormHM().put("orderby",orderby);
		this.getFormHM().put("nbaselist",nbaselist);
		this.getFormHM().put("nbase",nbase);
		this.getFormHM().put("subset",subset);
		
		this.getFormHM().put("filelist",filelist);
		this.getFormHM().put("fileset",fileset);
		this.getFormHM().put("code",code);
		this.getFormHM().put("kind",kind);
		
		this.userView.getHm().put("goboard_where", where);
		this.userView.getHm().put("goboard_orderby", orderby);
	}
	/**
	 * 返回人员库List
	 * @param nbaselist
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getNbaseList(ArrayList nbaselist)throws GeneralException 
	{
		if(nbaselist==null||nbaselist.size()<=0)
    	{
    		ArrayList dbaselist=this.userView.getPrivDbList(); 
    		if(dbaselist==null||dbaselist.size()<=0)
    		{
    			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.static.notdbname"),"",""));
    		}else
    		{
    			CollectStat collectStat=new CollectStat(this.getFrameconn());
    			nbaselist=collectStat.getBaseList(dbaselist);
       		}    		
    	}
		return nbaselist;
	}
	/**
	 * 返回人员库
	 * @param nbase
	 * @param nbaselist
	 * @return
	 * @throws GeneralException
	 */
    public String getNbase(String nbase,ArrayList nbaselist) throws GeneralException 
    {
    	if(nbase==null||nbase.length()<=0)
	    {
	    	if(nbaselist==null||nbaselist.size()<=0)
	    	{
	    		ArrayList dbaselist=this.userView.getPrivDbList(); 
	    		if(dbaselist==null||dbaselist.size()<=0)
	    		{
	    			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.static.notdbname"),"",""));
	    		}else
	    		{
	    			CollectStat collectStat=new CollectStat(this.getFrameconn());
	    			nbaselist=collectStat.getBaseList(dbaselist);
	    			CommonData comm=(CommonData)nbaselist.get(0);
	    	    	nbase=comm.getDataValue();	
	    		}
	    		
	    	}else
	    	{
	    		CommonData comm=(CommonData)nbaselist.get(0);
		    	nbase=comm.getDataValue();	
	    	}
	    	    	
	    }
    	return nbase;
    }  
}
