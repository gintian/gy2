package com.hjsj.hrms.transaction.general.deci.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.BuildLeaderStuff;
import com.hjsj.hrms.businessobject.general.deci.leader.GroupOperation;
import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hjsj.hrms.businessobject.general.deci.leader.LeaderParam;
import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.transaction.stat.SformulaXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 提供班子分析的材料生成，以txt格式下载
 *<p>Title:BuildLeaderStuffTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Oct 6, 2007</p> 
 *@author huaitao
 *@version 4.0
 */


public class BuildLeaderStuffTrans extends IBusiness {
	String statid ="";
	
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String dbpre ="";
		String txtfile = "";
		//dblist.add("Usr");dblist.add(" Ret");dblist.add("Trs");dblist.add("Oth");
		
		String code=(String)this.getFormHM().get("code");
	    String kind=(String)this.getFormHM().get("kind");
	    String a_code = (String)this.getFormHM().get("a_code");
	    
		LeadarParamXML leadarParamXML=new LeadarParamXML(this.getFrameconn());
	    String groupid_setid=leadarParamXML.getValue(LeadarParamXML.TEAM_LEADER,"setid");//数据集编号	
	    String groupid_codesetid=leadarParamXML.getValue(LeadarParamXML.TEAM_LEADER,"fielditem");//标示字段名
	    String groupid_value=leadarParamXML.getValue(LeadarParamXML.TEAM_LEADER,"value");//标示值
	    
	    String repertory_setid=leadarParamXML.getValue(LeadarParamXML.CANDID_LEADER,"setid");//数据集编号	
	    String repertory_codesetid=leadarParamXML.getValue(LeadarParamXML.CANDID_LEADER,"fielditem");//标示字段名
	    String repertory_value=leadarParamXML.getValue(LeadarParamXML.CANDID_LEADER,"value");//标示值
	    
	    String display_field=leadarParamXML.getTextValue(LeadarParamXML.OUTPUT);//显示指标
	    String bz_field=leadarParamXML.getTextValue(LeadarParamXML.BZDBPRE);//显示班子库
	    
	    String hb_field=leadarParamXML.getTextValue(LeadarParamXML.HBDBPRE);//显示后备库
	    LeaderParam leaderParam=new LeaderParam(this.getFrameconn(),this.userView);	
		ArrayList display_list=leaderParam.getFields(display_field);
		GroupOperation leadberOperation=new GroupOperation(this.getFrameconn(),this.userView);
		
		/*统计图表信息*/
		String gcond=leadarParamXML.getTextValue(LeadarParamXML.GCOND);	
		if(gcond==null||gcond.length()<=0)
			gcond="";
		ArrayList statlist=loadstatlist(gcond);
		//this.getFormHM().put("statlist",statlist);
		loadprivdb();	    
	    boolean isCorrect=true;
	    if(groupid_setid==null||groupid_setid.length()<=0)
	    	isCorrect=false;;
	    if(groupid_codesetid==null||groupid_codesetid.length()<=0)
	    	isCorrect=false;
	    if(groupid_value==null||groupid_value.length()<=0)
	    	isCorrect=false;
	    if(!isCorrect)
	    {
	    	this.getFormHM().put("display","no");
	    	return;
	    	//throw GeneralExceptionHandler.Handle(new GeneralException("","请到参数设置中设置基本情况指标！","",""));
	    }
	    int tt=2;
	    if(repertory_setid==null||repertory_setid.length()<=0)
	    	isCorrect=false;;
	    if(repertory_codesetid==null||repertory_codesetid.length()<=0)
	    	isCorrect=false;
	    if(repertory_value==null||repertory_value.length()<=0)
	    	isCorrect=false;
	    if(!isCorrect)
	    {
	    	tt=1;
	    	//this.getFormHM().put("display","no");
	    	//return;
	    	//throw GeneralExceptionHandler.Handle(new GeneralException("","请到参数设置中设置基本情况指标！","",""));
	    }
	    if(display_field==null||display_field.length()<=0){
	    	this.getFormHM().put("display","no");
	    	return;
	    	//throw GeneralExceptionHandler.Handle(new GeneralException("","请到参数设置中设置基本情况指标！","",""));
	    }
	    for(int h=0;h<tt;h++){
	    	ArrayList dblist = new ArrayList();
	    	HashMap group_map = new HashMap();
	    	if(h==0)
	    		group_map=leadberOperation.getLeadberMap(bz_field,groupid_setid,groupid_codesetid,groupid_value,display_field,display_list,code,kind);
	    	else if(h==1)
	    		group_map=leadberOperation.getLeadberMap(hb_field,repertory_setid,repertory_codesetid,repertory_value,display_field,display_list,code,kind);
			String strsql = (String)group_map.get("select_str");
			ArrayList fieldlist = (ArrayList)group_map.get("fieldlist");
			ArrayList beanlist=leadberOperation.beanList(fieldlist,strsql);
		    BuildLeaderStuff bls = new BuildLeaderStuff(this.getFrameconn(),this.getUserView());
			txtfile = bls.buildStuff(h,fieldlist,beanlist);
			
		    /*班子信息统计*/
		    ArrayList datalist=new ArrayList();
		    ArrayList varraydatalist = new ArrayList();
		    ArrayList harraydatalist = new ArrayList();
		    if(h==0){
		    	if(bz_field.length()<=0)
			    	dblist=this.userView.getPrivDbList();
				else{
					String[] ss = bz_field.split(",");
					for(int i =0;i<ss.length;i++){
						dblist.add(ss[i]);
					}
				}
		    }
		    else if(h==1){
		    	if(hb_field.length()<=0)
			    	dblist=this.userView.getPrivDbList();
				else{
					String[] ss = hb_field.split(",");
					for(int i =0;i<ss.length;i++){
						dblist.add(ss[i]);
					}
				}
		    }
		    try {
		    for(int y=0;y<statlist.size();y++){
		    	String type=null;
		    	statid = ((CommonData)statlist.get(y)).getDataValue();
		    	if(beanlist.size()==0)
		    		continue;
			    for(int x=0;x<dblist.size();x++){
			    	dbpre = dblist.get(x).toString();
			    	String candi_sql_in = "";
			    	if(h==0)
			    		candi_sql_in=leadberOperation.getLeaderWhereIn(dbpre,groupid_setid,groupid_codesetid,groupid_value);
			    	else if(h==1)
			    		candi_sql_in=leadberOperation.getLeaderWhereIn(dbpre,repertory_setid,repertory_codesetid,repertory_value);
				    boolean isresult=true;
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
					if(element!=null)
						sformula=element.getAttributeValue("id");
					if(type!=null && "1".equals(type))
					{
						int[] statvalues=null;
						double[] statvaluess=null;
						String[] fieldDisplay; 
						String SNameDisplay;
							
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
						bls.setSNameDisplay(SNameDisplay);
						if (statvalues != null && statvalues.length > 0) {
							fieldDisplay = simplestat.getDisplay();
							int statTotal = 0;
							for (int i = 0; i < statvalues.length; i++) {
								if(x==0){
									CommonData vo=new CommonData();
									 vo.setDataName(fieldDisplay[i]);
									 vo.setDataValue(String.valueOf(statvalues[i]));
									 datalist.add(vo);
								     statTotal += statvalues[i];
								}else{
									CommonData vo = new CommonData();
									vo = (CommonData)datalist.get(i);
									vo.setDataName(fieldDisplay[i]);
									int sum = new Integer(vo.getDataValue()).intValue();
									vo.setDataValue(String.valueOf(sum+statvalues[i]));
									datalist.remove(i);
									datalist.add(i,vo);
								}
							}
						}
						if (statvaluess != null && statvaluess.length > 0) {
							fieldDisplay = simplestat.getDisplay();
							double statTotal = 0;
							for (int i = 0; i < statvaluess.length; i++) {
								if(x==0){
									CommonData vo=new CommonData();
									 vo.setDataName(fieldDisplay[i]);
									 vo.setDataValue(String.valueOf(statvaluess[i]));
									 datalist.add(vo);
								     statTotal += statvaluess[i];
								}else{
									CommonData vo = new CommonData();
									vo = (CommonData)datalist.get(i);
									vo.setDataName(fieldDisplay[i]);
									double sum = new Double(vo.getDataValue()).intValue();
									vo.setDataValue(String.valueOf(sum+statvaluess[i]));
									datalist.remove(i);
									datalist.add(i,vo);
								}
							}
						}
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
						 if(sformula==null&&statValues==null)
							 continue;
						 if(sformula!=null&&statValuess==null)
							 continue;
						 List varraylist=simplestat.getVerticalArray();
						 List harraylist=simplestat.getHorizonArray();
						 String snameplay=simplestat.getSNameDisplay();
						 bls.setSNameDisplay(snameplay);
						 LazyDynaBean lb = new LazyDynaBean();
						 if (statValues != null && statValues.length > 0) {
							 for(int i=0;i<statValues.length;i++){
								 if(x==0){
									 int num = 0;
									 CommonData vo = new CommonData();
									 lb = (LazyDynaBean)varraylist.get(i);
									 vo.setDataName(lb.get("legend").toString());
									 for(int j=0;j<statValues[i].length;j++){
										 CommonData hvo = new CommonData();
										 int hsum = 0;
										 if(i==0){
											 lb = (LazyDynaBean)harraylist.get(j);
											 hvo.setDataName(lb.get("legend").toString());
											 hvo.setDataValue(""+statValues[i][j]);
											 harraydatalist.add(hvo);
										 }else{
											 hvo = (CommonData)harraydatalist.get(j);
											 hsum += statValues[i][j]+Integer.parseInt(hvo.getDataValue());
											 hvo.setDataValue(""+hsum);
											 harraydatalist.remove(j);
											 harraydatalist.add(j,hvo);
										 }
										 num+=statValues[i][j];
									 }
									 vo.setDataValue(""+num);
									 varraydatalist.add(vo);
								 }else{
									 int num = 0;
									 CommonData vo = new CommonData();
									 for(int j=0;j<statValues[i].length;j++){
										 num+=statValues[i][j];
										 CommonData hvo = new CommonData();
										 int hsum = 0;
										 hvo = (CommonData)harraydatalist.get(j);
										 hsum += statValues[i][j]+Integer.parseInt(hvo.getDataValue());
										 hvo.setDataValue(""+hsum);
										 harraydatalist.remove(j);
										 harraydatalist.add(j,hvo);
									 }
									 vo = (CommonData)varraydatalist.get(i);
									 int sum = num+(Integer.parseInt(vo.getDataValue()));
									 vo.setDataValue(""+sum);
									 varraydatalist.remove(i);
									 varraydatalist.add(i,vo);
								 }
							}
						 }
						 if (statValuess != null && statValuess.length > 0) {
							 for(int i=0;i<statValuess.length;i++){
								 if(x==0){
									 double num = 0;
									 CommonData vo = new CommonData();
									 lb = (LazyDynaBean)varraylist.get(i);
									 vo.setDataName(lb.get("legend").toString());
									 for(int j=0;j<statValuess[i].length;j++){
										 CommonData hvo = new CommonData();
										 double hsum = 0;
										 if(i==0){
											 lb = (LazyDynaBean)harraylist.get(j);
											 hvo.setDataName(lb.get("legend").toString());
											 hvo.setDataValue(""+statValuess[i][j]);
											 harraydatalist.add(hvo);
										 }else{
											 hvo = (CommonData)harraydatalist.get(j);
											 hsum += statValuess[i][j]+Double.parseDouble(hvo.getDataValue());
											 hvo.setDataValue(""+hsum);
											 harraydatalist.remove(j);
											 harraydatalist.add(j,hvo);
										 }
										 num+=statValuess[i][j];
									 }
									 vo.setDataValue(""+num);
									 varraydatalist.add(vo);
								 }else{
									 double num = 0;
									 CommonData vo = new CommonData();
									 for(int j=0;j<statValuess[i].length;j++){
										 num+=statValuess[i][j];
										 CommonData hvo = new CommonData();
										 double hsum = 0;
										 hvo = (CommonData)harraydatalist.get(j);
										 hsum += statValuess[i][j]+Double.parseDouble(hvo.getDataValue());
										 hvo.setDataValue(""+hsum);
										 harraydatalist.remove(j);
										 harraydatalist.add(j,hvo);
									 }
									 vo = (CommonData)varraydatalist.get(i);
									 double sum = num+(Double.parseDouble(vo.getDataValue()));
									 vo.setDataValue(""+sum);
									 varraydatalist.remove(i);
									 varraydatalist.add(i,vo);
								 }
							}
						 }
					}
			    }
			    if(type!=null && "1".equals(type))
			    	bls.buildDataList(datalist);
			    else if(type!=null && "2".equals(type))
			    	bls.buildDoubleDataList(varraydatalist,harraydatalist);
			    varraydatalist.clear();
			    harraydatalist.clear();
			    datalist.clear();
		    }
		    } catch (Exception e) {
		    	e.printStackTrace();
		    	throw new GeneralException("", e.toString(),"", "");
			}
	    }
	    txtfile=PubFunc.encrypt(txtfile);//liuy 领导桌面安全改造
	    this.getFormHM().put("display","yes");
		this.getFormHM().put("txtfile",txtfile);
		
	    
	}
	
	/*加载应前缀库过滤条件*/
	private void loadprivdb()
	{
		 /**应用库过滤前缀符号*/
        ArrayList dblist=userView.getPrivDbList();
        StringBuffer cond=new StringBuffer();
        ArrayList dbprelist=new ArrayList();
        cond.append("select pre,dbname from dbname where pre in (");
        for(int i=0;i<dblist.size();i++)
        {
            if(i!=0)
                cond.append(",");
            cond.append("'");
            cond.append((String)dblist.get(i));
            cond.append("'");
            if(i==0)
            	this.getFormHM().put("dbpre",dblist.get(0));
        }
        if(dblist.size()==0)
            cond.append("''");
        cond.append(")");
        /**应用库前缀过滤条件*/
        ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{
		    this.frowset=dao.search(cond.toString());
		    while(this.frowset.next())
		    {  
		    	CommonData data=new CommonData();
	    	    data.setDataName(this.getFrowset().getString("dbname"));
	    	    data.setDataValue(this.getFrowset().getString("pre"));
	    	    dbprelist.add(data);
		    }
		}catch(Exception e)
		{
			e.printStackTrace();			
		}
        this.getFormHM().put("dbprelist",dbprelist);
	}
	/*加载统计条件项*/
	private ArrayList loadstatlist(String gcond) throws GeneralException 
	{
		ArrayList statlist=new ArrayList();		
		if(gcond==null||gcond.length()<=0)
			gcond="";
    	String gconds[]=gcond.split(",");
    	ArrayList list =new ArrayList();
    	if(gconds==null||gconds.length<=0)
    		return list;
    	StringBuffer sql=new StringBuffer();
    	sql.append("select * from sname where ");
    	sql.append(" id in(");
    	for(int i=0;i<gconds.length;i++)
    	{
    		sql.append("'"+gconds[i]+"',");
    	}
    	sql.setLength(sql.length()-1);
    	sql.append(") and infokind=1 ");
    	sql.append(" order by id");		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{
			int i=0;
		    this.frowset=dao.search(sql.toString());
		    while(this.frowset.next())
		    {
		    	if((userView.isHaveResource(IResourceConstant.STATICS,this.frowset.getString("id"))))
	    		{
		    	    CommonData data=new CommonData();
		    	    data.setDataName(this.getFrowset().getString("name"));
		    	    data.setDataValue(this.getFrowset().getString("id"));
		    	   	statlist.add(data);
		    	   	if(i==0)
			    	{
			    		this.getFormHM().put("statid",this.getFrowset().getString("id"));
			    		statid = this.getFrowset().getString("id");
			    	}
			    	i++;
	    		}		    	
		    }
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return statlist;
	}
}
