package com.hjsj.hrms.businessobject.gz.piecerate;

import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class PieceRateFormulaBo {		
	
	private Connection conn = null;
	private String FormulaId="";
	private String itemid="";
	private String Cond="";
	private String RExpr="";
	private UserView userView=null;
	
	public PieceRateFormulaBo(Connection _con,String formulaid,UserView _userView)
	{
		conn=_con;
		this.FormulaId=formulaid;
		userView=_userView;
		init();		
	}
	
	private void init()
	{
	   if(this.FormulaId!=null&&this.FormulaId.length()>0)
	   {
		    ContentDAO dao=new ContentDAO(this.conn);
			String str = "select * from hr_formula  where formulaid="+ FormulaId + "";	
			try{
				
				ArrayList dylist = dao.searchDynaList(str);
			    for (Iterator it = dylist.iterator(); it.hasNext();) 
			    {
				  DynaBean dynabean = (DynaBean) it.next();
				  this.itemid = dynabean.get("itemid").toString(); 
				  this.RExpr = dynabean.get("rexpr").toString(); 
				  this.Cond = dynabean.get("cond").toString(); 				  
			     }					   	
			}
			catch (Exception e)
			{		
				e.printStackTrace();		
			}
	   }
	}
	
	public String getFormulaId() 
	{   
		return this.FormulaId;		
	}
	public String getItemId() 
	{   
		return this.itemid;		
	}
	public String getRExpr() 
	{   
		return this.RExpr;		
	}
	public String getCond() 
	{   
		return this.Cond;		
	}
	
	public String GetFirstFormula(ContentDAO dao,String busiid)
	{
        String formulaid="";
		String str = "select *  from hr_formula where modulecode='SAL_JJ' and  busiId='"+busiid+"' order by sortid,itemid desc";	
		try{			
			RowSet rset=dao.search(str);
			if (rset.next())
			{
				this.FormulaId = Integer.toString(rset.getInt("formulaid")) ; 
				this.itemid = rset.getString("itemid"); 
				this.RExpr = rset.getString("rexpr"); 
				this.Cond = rset.getString("cond"); 	   	
			}
		}
		catch (Exception e)
		{		
			e.printStackTrace();		
		}
      return formulaid;
	}
	
	public String AddFormula(ContentDAO dao,String formulaitemid,String busiid){
		String strResult="no";
		String[] standid = {"1","1"};
		String sqlstr = "select max(formulaid) as formulaid,max(sortid) as sortid from hr_formula ";
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				if(dynabean.get("formulaid")!=null&&dynabean.get("formulaid").toString().trim().length()>0){
					standid[0] = Integer.parseInt(dynabean.get("formulaid").toString())+1+"";
				}
				if(dynabean.get("sortid")!=null&&dynabean.get("sortid").toString().trim().length()>0){
					standid[1] = Integer.parseInt(dynabean.get("sortid").toString())+1+"";
				}
			}
		} catch(GeneralException e) {
			e.printStackTrace();
		}
		String[] arr = formulaitemid.split(":");	
		FieldItem fielditem = null;		
		StringBuffer strsql = new StringBuffer();
/*		strsql.setLength(0);
		strsql.append("select * from hr_formula where modulecode ='SAL_JJ' and  busiid ='"+busiid+"'");
		strsql.append(" and  setid ='S05'");
		strsql.append(" and  itemid ='"+arr[0]+"'");
		try{
		    RowSet rset = dao.search(strsql.toString());
		    if ((rset !=null) && (rset.next()))
		    {
		    	strResult="exists";	
		    	return strResult;
		    }
		}catch(Exception e){
			e.printStackTrace();
		}	*/	
		
		strsql.setLength(0);
		strsql.append("insert into hr_formula(formulaid, sortid,modulecode,busiid,setid,itemid,");
		strsql.append("itemname,itemtype) values(");
		if(standid.length==2){
			strsql.append(standid[0]+","+standid[1]+",");
		}else{
			strsql.append("0,0,");
		}
		strsql.append("'SAL_JJ',"+ "'"+busiid+"',"+"'S05',");
		if(arr.length==2){
			strsql.append("'"+arr[0]+"','"+arr[1]+"',");
			fielditem = DataDictionary.getFieldItem(arr[0]);
		}else{
			strsql.append("0,0,");
		}
		if(fielditem!=null){
			strsql.append("'"+fielditem.getItemtype()+"')");
		}else{
			strsql.append("'N')");
		}
		
		try {
			dao.update(strsql.toString());
			strResult = "ok";
		} catch(SQLException e) {
			e.printStackTrace();
		}			
		
		return strResult;
	}
	
	public String GetMaxFormulaId(ContentDAO dao,String busiid){
		String strResult="1";
		String sqlstr = "select max(formulaid) as formulaid,max(sortid) as sortid from hr_formula ";
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				if(dynabean.get("formulaid")!=null&&dynabean.get("formulaid").toString().trim().length()>0){
					strResult = Integer.parseInt(dynabean.get("formulaid").toString())+"";
				}

			}
		} catch(GeneralException e) {
			e.printStackTrace();
		}		
		return strResult;
	}	
	
	
	public ArrayList sortList(Connection conn,String busiid){
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "select * from hr_formula where modulecode ='SAL_JJ' and  busiid ='"+busiid+"'"+" order by sortid,itemid desc";
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				CommonData dataobj = new CommonData(dynabean.get("itemid").toString()+":"+dynabean.get("formulaid").toString(),
						dynabean.get("itemname").toString());
				list.add(dataobj);
			}
			if(dylist.size()<1){
				CommonData dataobj = new CommonData("","");
				list.add(dataobj);
			}
			
		} catch(GeneralException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public String AdjustSeq(ContentDAO dao,String sortlist,String busiid){		
		String strResult = "ok";
		String[] fitem = sortlist.split(",");	
		if(busiid.length()>0){
			for(int i=0;i<fitem.length;i++){
				String arr[] = fitem[i].split(":");
				if(arr.length==2){
					StringBuffer sqlstr = new StringBuffer();
					sqlstr.append("update hr_formula set sortid="+i+" where formulaid=");
					sqlstr.append(arr[1]);
					try {
						dao.update(sqlstr.toString());
						
					} catch (SQLException e) {
						strResult="no";	
						e.printStackTrace();
					}
				}
			}
		}	
		
		return strResult;
	}
	
	public String DelFormula(ContentDAO dao,String FormulaIds,String busiid){		
		String strResult = "ok";
		String ids = FormulaIds.substring(1);
		String sqlstr = "delete from  hr_formula where formulaid in ("+ids+")";
		try {
			dao.update(sqlstr);			
		} catch (SQLException e) {
			strResult="no";	
			e.printStackTrace();
		}	
		return strResult;
	}
	
	public ArrayList conditionsList(Connection conn,String setid)
	{
		ArrayList list = new ArrayList();
		ArrayList fieldList = new ArrayList();
		fieldList = DataDictionary.getFieldList(setid, Constant.USED_FIELD_SET);
		String excludeStr=",Nbase,A0100,I9999,S0100,S0522,".toUpperCase();
		try {
			CommonData dataobj = new CommonData(":","");
			list.add(dataobj);
			for(int i=0;i<fieldList.size();i++){
				FieldItem item = (FieldItem)fieldList.get(i);
				if ("0".equals(item.getState())) continue;
				String itemid = item.getItemid().toUpperCase();
				if(itemid==null || "".equals(itemid))	continue;
				if (excludeStr.indexOf(","+itemid.toUpperCase()+",")>-1) {continue;}
				String itemdesc = item.getItemdesc();
				dataobj = new CommonData(itemid+":"+itemdesc,itemid+":"+itemdesc);
				list.add(dataobj);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public String SaveFormulaCond(ContentDAO dao,String formulaId,String StrValue){		
		String strResult = "ok";
		try {
			String sqlstr = "update hr_formula set cond='"+StrValue+"'  where formulaid="+ formulaId + "";
			dao.update(sqlstr);
		
		} catch(Exception e) {
			strResult="no";
			e.printStackTrace();
		 }			
		
		return strResult;
	}
	
	public String SaveFormulaContent(ContentDAO dao,String formulaId,String StrValue){		
		String strResult = "ok";
		try {
			String sqlstr = "update  hr_formula set rexpr='"+ StrValue+"' where formulaid ="+formulaId;
			dao.update(sqlstr);			
		} catch (SQLException e) {
			strResult="no";	
			e.printStackTrace();
		}	
		return strResult;
	}

	public ArrayList getFormulaList(String busiid,String forumulaids)throws GeneralException
	{
		ArrayList list=new ArrayList();
		StringBuffer buf=new StringBuffer();
		try
		{
			buf.append( "select * from hr_formula where modulecode ='SAL_JJ' and  busiid ='"+busiid+"'");
			if (!"".equals(forumulaids))
				buf.append(" and formulaid in ("+forumulaids.substring(1)+")" );
			buf.append(" order by sortid,itemid desc");
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			list=dao.getDynaBeanList(rset);
			rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}
	
	
	private ArrayList getComputeFormulaList(ArrayList itemids, String busiid)throws GeneralException
	{
		ArrayList list=new ArrayList();
		StringBuffer strformulaid=new StringBuffer();
		try
		{
			if(itemids.size()>0)
			{
				for(int i=0;i<itemids.size();i++)
				{
					strformulaid.append(","+(String)itemids.get(i));
				}
				list =this.getFormulaList(busiid,strformulaid.toString());					

			}
			return list;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		

	}
	
	
	public boolean computing(String strWhere,ArrayList itemids,String busiid,String s0100)throws GeneralException
	{
		boolean bflag=false;
		ArrayList formulalist=this.getComputeFormulaList(itemids,busiid);  //this.getFormulaList(1);
		if(formulalist.size()==0)
			return true;
		try
		{
			for(int i=0;i<formulalist.size();i++)
			{
                DynaBean dbean=(LazyDynaBean)formulalist.get(i);
                String formula=(String)dbean.get("rexpr");
                if ("".equals(formula.trim())) continue;
               //处理有存储过程的情况
                if ((formula.indexOf("执行存储过程(")>-1) && (formula.indexOf("作业单序号")>-1) ){
                	
                	formula =formula.replace("作业单序号", s0100);
                	
                }

                String cond=(String)dbean.get("cond");
                String fieldname=(String)dbean.get("itemid");
                calcFormula(formula,cond,fieldname,strWhere);

			}

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}
		
	
	private int getDataType(String type)
	{
		int datatype=0;
		switch(type.charAt(0))
		{
		case 'A':  
			datatype=YksjParser.STRVALUE;
			break;
		case 'D':
			datatype=YksjParser.DATEVALUE;
			break;
		case 'N':
			datatype=YksjParser.FLOAT;
			break;
		}
		return datatype;
	}
	
	private void calcFormula(String formula,String cond,String fieldname,String strWhere)
	{
		YksjParser yp=null;
		try
		{
			StringBuffer strsql=new StringBuffer();
			String strfilter="";	        
			ContentDAO dao=new ContentDAO(this.conn);
			/**先对计算公式的条件进行分析*/
			if(!(cond==null|| "".equalsIgnoreCase(cond)))
			{
				yp = new YksjParser( this.userView ,DataDictionary.getFieldList("S05", Constant.USED_FIELD_SET),
						YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
				yp.run_where(cond);
				strfilter=yp.getSQL();
			}
			StringBuffer strcond=new StringBuffer();
			if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
				strcond.append(strWhere);
			if(!("".equalsIgnoreCase(strfilter)))
			{
				if(strcond.length()>0)
					strcond.append(" and ");
				strcond.append(strfilter);
			}		
			FieldItem item=DataDictionary.getFieldItem(fieldname);		
			yp=new YksjParser( this.userView ,DataDictionary.getFieldList("S05", Constant.USED_FIELD_SET),
					YksjParser.forNormal, getDataType(item.getItemtype()),YksjParser.forPerson , "Ht", "");
			//如果是分组汇总，使用临时表
			String tmpTabName="t#"+this.userView.getUserId()+"_s05";
			if ((formula.indexOf("分组汇总")>=0)|| (formula.indexOf("执行标准")>=0)) {
				DbWizard dbWizard = new DbWizard(this.conn);				
				if (dbWizard.isExistTable(tmpTabName,false)){
					dbWizard.dropTable(tmpTabName);			
				}
				if(Sql_switcher.searchDbServer()==2)
					strsql.append("create table "+tmpTabName+" as select * from S05 where "+strcond.toString());
				else 
					strsql.append("select * into "+tmpTabName+" from S05 where "+strcond.toString()) ;
				dao.update(strsql.toString());
				yp.run(formula,this.conn,"",tmpTabName);
			}	
			else{
				yp.run(formula,this.conn,strcond.toString(),"S05");	
			}

			String strexpr="";
			strexpr=yp.getSQL();
			if ("已执行存储过程".equals(strexpr)) return;

			strsql.setLength(0);
			if ((formula.indexOf("分组汇总")>=0)|| (formula.indexOf("执行标准")>=0)) {
				strsql.setLength(0);
				strsql.append("update ");
				strsql.append(tmpTabName);
				strsql.append(" set ");
				strsql.append(fieldname);
				strsql.append("=");
				strsql.append(strexpr);
				strsql.append(" where 1=1 ");
				dao.update(strsql.toString());	
				
				DbWizard dbWizard = new DbWizard(this.conn);
				dbWizard.updateRecord("s05", tmpTabName, 						
						"S05.Nbase="+tmpTabName+".nbase and s05.A0100="+tmpTabName+".A0100" ,
						"S05."+fieldname+"="+tmpTabName+"."+fieldname, 
						"S05."+strWhere,"");
				dbWizard.dropTable(tmpTabName);		
			}
			else{				
				strsql.append("update ");
				strsql.append("S05");
				strsql.append(" set ");
				strsql.append(fieldname);
				strsql.append("=");
				strsql.append(strexpr);
				strsql.append(" where 1=1 ");
				if(strcond.length()>0){
					strsql.append(" and ");
					strsql.append(strcond.toString());
				}
				dao.update(strsql.toString());				
			}

		}
		catch(Exception ex)
		{

		}finally{ 
			yp=null;
		} 
	}
	
}

