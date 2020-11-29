package com.hjsj.hrms.transaction.general.deci.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.LeadberOperation;
import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.businessobject.sys.AnychartBo;
import com.hjsj.hrms.transaction.stat.SformulaXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;
/**
 * 分析
 *<p>Title:ShowStatAnalyseLeaderTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 29, 2007</p> 
 *@author sunxin
 *@version 4.0
 */
public class ShowStatAnalyseCandiTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String statid=(String)this.getFormHM().get("statid");		
		String a_code=(String)this.getFormHM().get("a_code");	
		String dbpre=(String)this.getFormHM().get("dbpre");
		String analyse_setid=(String)this.getFormHM().get("analyse_setid");
		String  analyse_codesetid=(String)this.getFormHM().get("analyse_codesetid");
		String analyse_value=(String)this.getFormHM().get("analyse_value");
		LeadberOperation leadberOperation=new LeadberOperation(this.getFrameconn(),this.userView);
		String candi_sql_in=leadberOperation.getLeaderWhereIn(dbpre,analyse_setid,analyse_codesetid,analyse_value);
	    boolean isresult=true;
		String type=null;  
		try{
			String sql="select id,type from sname where id=" + statid;
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql.toString());
         	if(this.frowset.next())
         	{
   			  type=this.frowset.getString("type");
      	    }	
         	if(type!=null && "1".equals(type))
         		this.getFormHM().put("isonetwostat","1");
         	else if(type!=null && "2".equals(type))
         		this.getFormHM().put("isonetwostat","2");
		}catch(Exception e){
			e.printStackTrace();
		}
		SformulaXml xml = new SformulaXml(this.frameconn,statid);
		String sformula=null;
		Element element=xml.getFirstElement();
		String decimal="0";
		if(element!=null){
			sformula=element.getAttributeValue("id");
			decimal=element.getAttributeValue("decimalwidth");
			decimal=decimal==null||decimal.length()==0?"0":decimal;
		}
		try{
		if(type!=null && "1".equals(type))
		{
			int[] statvalues=null;
			double[] statvaluess=null;
			String[] fieldDisplay; 
			String SNameDisplay;
			ArrayList datalist=new ArrayList();	
		    StatDataEncapsulation simplestat=new StatDataEncapsulation();
		    simplestat.setWhereIN(candi_sql_in);
		    String exprfactor="";
		    String exprlexpr="";
		    if(a_code!=null && a_code.length()>=2)
		    {
		    	String codeid=a_code.substring(0,2);
		    	if("UN".equalsIgnoreCase(codeid))
				{
		    		exprlexpr="1";				
		    		exprfactor="B0110=";
				}
				else if("UM".equalsIgnoreCase(codeid))
				{
					exprlexpr="1";			
					exprfactor="E0122=";
				}
				else
				{
					exprlexpr="1";				
					exprfactor="E01A1=";
				}
		    	exprfactor+=a_code.substring(2)+"*`";
		    	if(sformula==null)
		    		statvalues =simplestat.getLexprData(dbpre, Integer.parseInt(statid), null, userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,exprlexpr,exprfactor,"2","");
		    	else
		    		statvaluess =simplestat.getLexprDataSformula(dbpre, Integer.parseInt(statid), null, userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,exprlexpr,exprfactor,"2","",sformula,this.frameconn);
		    }else{
		    	if(sformula==null)
		    		statvalues =simplestat.getLexprData(dbpre, Integer.parseInt(statid), null, userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,null,null,null,"");
		    	else
		    		statvaluess =simplestat.getLexprDataSformula(dbpre, Integer.parseInt(statid), null, userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,exprlexpr,exprfactor,"2","",sformula,this.frameconn);
		    }
		    SNameDisplay = simplestat.getSNameDisplay();
			if (statvalues != null && statvalues.length > 0) {
				fieldDisplay = simplestat.getDisplay();
				int statTotal = 0;
				for (int i = 0; i < statvalues.length; i++) {
					 CommonData vo=new CommonData();
					 vo.setDataName(fieldDisplay[i]);
					 vo.setDataValue(String.valueOf(statvalues[i]));
					 datalist.add(vo);
				     statTotal += statvalues[i];
				}
			  this.getFormHM().put("snamedisplay",SNameDisplay);
		      this.getFormHM().put("datalist",datalist);
		      this.getFormHM().put("totalvalue",String.valueOf(statTotal));
		      this.getFormHM().put("varraylist",new ArrayList());
		      this.getFormHM().put("harraylist",new ArrayList());
			}
			if (statvaluess != null && statvaluess.length > 0) {
				fieldDisplay = simplestat.getDisplay();
				double statTotal = 0;
				for (int i = 0; i < statvaluess.length; i++) {
					 CommonData vo=new CommonData();
					 vo.setDataName(fieldDisplay[i]);
					 vo.setDataValue(String.valueOf(statvaluess[i]));
					 datalist.add(vo);
				     statTotal += statvaluess[i];
				}
			  this.getFormHM().put("snamedisplay",SNameDisplay);
		      this.getFormHM().put("datalist",datalist);
		      this.getFormHM().put("totalvalue",String.valueOf(statTotal));
		      this.getFormHM().put("varraylist",new ArrayList());
		      this.getFormHM().put("harraylist",new ArrayList());
			}
			String xangle=AnychartBo.computeXangle(datalist);
		    this.getFormHM().put("xangle", xangle);
		}else if(type!=null && "2".equals(type))
		{
			 int[][] statValues=null;
			 double[][] statValuess=null;
			 String exprlexpr;
			 String exprfactor;
			 StatDataEncapsulation simplestat=new StatDataEncapsulation();
			 simplestat.setWhereIN(candi_sql_in);
			   if(a_code!=null && a_code.length()>=2)
			    {
			    	String codeid=a_code.substring(0,2);
			    	if("UN".equalsIgnoreCase(codeid))
					{
			    		exprlexpr="1";				
			    		exprfactor="B0110=";
					}
					else if("UM".equalsIgnoreCase(codeid))
					{
						exprlexpr="1";			
						exprfactor="E0122=";
					}
					else
					{
						exprlexpr="1";				
						exprfactor="E01A1=";
					}
			    	exprfactor+=a_code.substring(2)+"*`";
			    	if(sformula==null)
			    		statValues=simplestat.getDoubleLexprData(Integer.parseInt(statid),dbpre,null,userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,exprlexpr,exprfactor,"2",null);
			    	else
			    		statValuess=simplestat.getDoubleLexprDataSformula(Integer.parseInt(statid),dbpre,null,userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,exprlexpr,exprfactor,"2",null,sformula,this.frameconn);
			    }else{
			    	if(sformula==null)
			    		statValues=simplestat.getDoubleLexprData(Integer.parseInt(statid),dbpre,null,userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,null,null,"",null);
			    	else
			    		statValuess=simplestat.getDoubleLexprDataSformula(Integer.parseInt(statid),dbpre,null,userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,null,null,"",null,sformula,this.frameconn);
			    }	
		    List varraylist=simplestat.getVerticalArray();
			List harraylist=simplestat.getHorizonArray();
			String snameplay=simplestat.getSNameDisplay();
			int totalvalue=simplestat.getTotalValue();
			this.getFormHM().put("statdoublevalues",statValues);
			this.getFormHM().put("varraylist",varraylist);
			this.getFormHM().put("harraylist",harraylist);
			this.getFormHM().put("snamedisplay",snameplay);
			this.getFormHM().put("totalvalue",String.valueOf(totalvalue));
			this.getFormHM().put("sformula", sformula);
			this.getFormHM().put("statdoublevaluess", statValuess);
		}
		this.getFormHM().put("decimal", decimal);
		} catch (Exception e) {
	    	e.printStackTrace();
	    	throw new GeneralException("", e.toString(),"", "");
		}
	}
}
