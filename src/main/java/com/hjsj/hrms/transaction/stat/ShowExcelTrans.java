package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.businessobject.general.statics.ShowExcel;
import com.hjsj.hrms.businessobject.stat.GeneralQueryStat;
import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Element;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * 
 *<p>Title:ShowExcelTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 29, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class ShowExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		RowSet rs = null;
		try
		{
			String userbases=(String)this.getFormHM().get("userbases");
			String userbase=(String)this.getFormHM().get("userbase");
			String statId=(String)this.getFormHM().get("statid");
			String querycond=(String)this.getFormHM().get("querycond");
			String infokind=(String)this.getFormHM().get("infokind");
			String sformula = (String)this.getFormHM().get("sformula");
		    sformula = sformula ==null?"":sformula; 
		    String vtotal=(String)this.getFormHM().get("vtotal");
		    String htotal=(String)this.getFormHM().get("htotal");
		    String decimalwidth="0";
		    if(sformula.length()>0){
			    SformulaXml xml = new SformulaXml(this.frameconn,statId);
				Element element = xml.getElement(sformula);
				if(element==null){
					sformula="";
				}else{
					decimalwidth = element.getAttributeValue("decimalwidth");
					decimalwidth=(decimalwidth==null||decimalwidth.length()==0)?"2":decimalwidth;
				}
		    }
		    ContentDAO dao = new ContentDAO(this.frameconn);
		    
		    String name = "";
		    rs = dao.search("select name from sname where id=?",Arrays.asList(statId));
		    if(rs.next()) {
		    	name = rs.getString("name");
		    }
		    ArrayList curr_idlist=(ArrayList)this.getFormHM().get("curr_id");
		    String[] curr_id = null;
		    if(curr_idlist!=null&&curr_idlist.size()>=1){
		    	curr_id = new String[curr_idlist.size()];
		    	for(int i=0;i<curr_idlist.size();i++){
		    		curr_id[i] = curr_idlist.get(i).toString();
		    	}
		    }
		    String preresult=(String)this.getFormHM().get("preresult");
		    if(preresult==null||preresult.length()<=0)
		    	preresult="2";
		    if("1".equals(preresult))
		    	curr_id=null;
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
		    String history=(String)this.getFormHM().get("history");
			    boolean isresult=true;
			    String result=(String)this.getFormHM().get("result");
			    if(result==null|| "".equals(result)|| "0".equals(result))
			    	isresult=true; 
			    else
			    	isresult =false;
			    
			    //加上常用查询进行的统计
			    String commlexr=null;
			    String commfacor=null;
				GeneralQueryStat generalstat=new GeneralQueryStat();
				generalstat.getGeneralQueryLexrfacor(curr_id,userbase,history,this.getFrameconn());
			    
			    if(curr_id!=null)
			    {
			    	commlexr=generalstat.getLexpr();
			    	commfacor=generalstat.getLfactor();
			    }
			    //组织机构筛选过滤start   wangb 20190822
		   		String filterId = (String)this.formHM.get("filterId");
		   		String org_filter = (String)this.formHM.get("org_filter");
		   		if(org_filter != null && "1".equalsIgnoreCase(org_filter)) {
		   			querycond = "";
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
			StatDataEncapsulation simplestat=new StatDataEncapsulation();
			int[][] getValues = null;
			double[][] getValuess = null; 
			if(userbases==null||userbases.length()==0){
				if(sformula.length()==0)
					getValues =simplestat.getDoubleLexprData(Integer.parseInt(statId),userbase,querycond,userView.getUserName(),userView.getManagePrivCode(),userView,infokind,isresult,commlexr,commfacor,preresult,history,vtotal,htotal);
				else
					getValuess =simplestat.getDoubleLexprDataSformula(Integer.parseInt(statId),userbase.toUpperCase(),querycond,userView.getUserName(),userView.getManagePrivCode(),userView,infokind,isresult,commlexr,commfacor,preresult,history,sformula,this.frameconn,vtotal,htotal);
			}else{
				if(sformula.length()==0)
					getValues =simplestat.getDoubleLexprData(Integer.parseInt(statId),userbase.toUpperCase(),querycond,userView.getUserName(),userView.getManagePrivCode(),userView,infokind,isresult,commlexr,commfacor,preresult,history,userbases,vtotal,htotal);
				else
					getValuess =simplestat.getDoubleLexprDataSformula(Integer.parseInt(statId),userbase.toUpperCase(),querycond,userView.getUserName(),userView.getManagePrivCode(),userView,infokind,isresult,commlexr,commfacor,preresult,history,userbases,sformula,this.frameconn,vtotal,htotal);
			}
			getValues = getValues==null?new int[][]{}:getValues;
			getValuess = getValuess==null?new double[][]{}:getValuess;
		    List dlist=simplestat.getVerticalArray();
			List hlist=simplestat.getHorizonArray();
			String snameplay=simplestat.getSNameDisplay();
			int tolvalue=simplestat.getTotalValue();
			double tolvalues=simplestat.getTotalValues();
			ShowExcel show= new ShowExcel(this.getFrameconn());
			String excelfile="";
			String filename = this.userView.getUserName() +"_"+name+".xls";
			if(sformula.length()==0)
				excelfile=show.creatExcel(getValues,dlist,hlist,snameplay,tolvalue,filename);
			else
				excelfile=show.creatExcel(getValuess,dlist,hlist,snameplay,tolvalues,Integer.parseInt(decimalwidth),filename);
			excelfile = SafeCode.encode(PubFunc.encrypt(excelfile));
			this.getFormHM().put("excelfile",excelfile);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally {
			PubFunc.closeDbObj(rs);
		}

	}

}
