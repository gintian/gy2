package com.hjsj.hrms.transaction.general.deci.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 分析
 *<p>Title:ShowStatAnalyseLeaderTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 29, 2007</p> 
 *@author sunxin
 *@version 4.0
 */
public class InitStatAnalyseCandiTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		LeadarParamXML leadarParamXML=new LeadarParamXML(this.getFrameconn());
		String gcond=leadarParamXML.getTextValue(LeadarParamXML.GCOND);	
		String leader_type=(String)this.getFormHM().get("leader_type");
		if(leader_type==null||leader_type.length()<=0)
			leader_type="team";
		if(gcond==null||gcond.length()<=0)
			gcond="";
		ArrayList statlist=loadstatlist(gcond);
		this.getFormHM().put("statlist",statlist);
		loadprivdb(leader_type,leadarParamXML);
		String analyse_setid="";//数据集编号	
	    String analyse_codesetid="";//标示字段名
	    String analyse_value="";//标示值		    
		if("team".equals(leader_type))
		{
			analyse_setid=leadarParamXML.getValue(LeadarParamXML.TEAM_LEADER,"setid");//数据集编号	
		    analyse_codesetid=leadarParamXML.getValue(LeadarParamXML.TEAM_LEADER,"fielditem");//标示字段名
		    analyse_value=leadarParamXML.getValue(LeadarParamXML.TEAM_LEADER,"value");//标示值	
		}else if("candi".equals(leader_type))
		{
			analyse_setid=leadarParamXML.getValue(LeadarParamXML.CANDID_LEADER,"setid");//数据集编号	
		    analyse_codesetid=leadarParamXML.getValue(LeadarParamXML.CANDID_LEADER,"fielditem");//标示字段名
		    analyse_value=leadarParamXML.getValue(LeadarParamXML.CANDID_LEADER,"value");//标示值	
		}	    
	    boolean isCorrect=true;
	    if(analyse_setid==null||analyse_setid.length()<=0)
	    	isCorrect=false;;
	    if(analyse_codesetid==null||analyse_codesetid.length()<=0)
	    	isCorrect=false;
	    if(analyse_value==null||analyse_value.length()<=0)
	    	isCorrect=false;
	    if(!isCorrect)
	    {
	    	throw GeneralExceptionHandler.Handle(new GeneralException("","没有完整定义标识！","",""));
	    }

	    this.getFormHM().put("analyse_setid",analyse_setid);
	    this.getFormHM().put("analyse_codesetid",analyse_codesetid);
	    this.getFormHM().put("analyse_value",analyse_value);
	}
	/*加载应前缀库过滤条件*/
	private void loadprivdb(String leader_type,LeadarParamXML leadarParamXML)
	{
		 /**应用库过滤前缀符号*/
		String db_field="";
		leadarParamXML=new LeadarParamXML(this.getFrameconn());
		if("team".equals(leader_type))
			db_field=leadarParamXML.getTextValue(LeadarParamXML.BZDBPRE);
		else if("candi".equals(leader_type))
			db_field=leadarParamXML.getTextValue(LeadarParamXML.HBDBPRE);
        ArrayList dblist=new ArrayList();
        if(db_field.length()<=0)
        	dblist.add("Usr");
		else{
			String[] ss = db_field.split(",");
			for(int i =0;i<ss.length;i++){
				dblist.add(ss[i]);
			}
		}
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
			int i=0;
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
	/*加载统计条件项 */
	private ArrayList loadstatlist(String gcond) throws GeneralException 
	{
		ArrayList statlist=new ArrayList();	
		HashMap statHM = new HashMap();
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
		    	    /*显示的顺序是通过设置显示，不是从小到大排序显示 bug 46055 wangb 20190528*/
		    	    statHM.put(this.getFrowset().getString("id"), data);
//		    	   	sstatlist.add(data);
		    	   	if(i==0)
			    	{
			    		this.getFormHM().put("statid",this.getFrowset().getString("id"));
			    		if("1".equals(this.getFrowset().getString("type")))
			    			this.getFormHM().put("isonetwostat","1");
			    		else
			    			this.getFormHM().put("isonetwostat","2");
			    	}
			    	i++;
	    		}		    	
		    }
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		for(int i=0;i<gconds.length;i++)
    	{
    		statlist.add(statHM.get(gconds[i]));
    	}
		return statlist;
	}
}
