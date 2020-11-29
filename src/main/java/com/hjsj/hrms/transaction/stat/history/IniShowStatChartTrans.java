/*
 * Created on 2005-6-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.stat.history;

import com.hjsj.hrms.businessobject.stat.GeneralQueryStat;
import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jfree.data.general.DefaultPieDataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IniShowStatChartTrans extends IBusiness {

	 /* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		int[] statvalues;		
       	String[] fieldDisplay;        
		String SNameDisplay="";
		String userbase=(String)this.getFormHM().get("userbase");
		String querycond=(String)this.getFormHM().get("querycond");
		String infokind=(String)this.getFormHM().get("infokind");
	    HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
 	    String isshowstatcond=(String)this.getFormHM().get("isshowstatcond");	   
	    this.getFormHM().put("isshowstatcond",isshowstatcond);
	    String[] curr_id=(String[])this.getFormHM().get("curr_id");	 
	    String preresult=(String)this.getFormHM().get("preresult");
	    String history=(String)this.getFormHM().get("history");	    
	    if(preresult==null||preresult.length()<=0)
	    	preresult="2";
	    if("1".equals(preresult))
	    	curr_id=null;
	    if("0".equals(preresult))
	    	preresult="2";    
	    
	    if(curr_id==null&&infokind!=null&& "1".equals(infokind))
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
	    boolean isresult=true;
	    String result=(String)this.getFormHM().get("result");
	    /*if(result==null||result.equals("")||result.equals("0"))
	    	isresult=true; 
	    else
	    	isresult =false;*/
	   
	    
        //加上常用查询进行的统计
		String statId="-1";
		String type="1";
    	boolean istabid=false;
    	 //加上常用查询进行的统计
	    String commlexr=null;
	    String commfacor=null;
		try{
			
			GeneralQueryStat generalstat=new GeneralQueryStat();
			generalstat.getGeneralQueryLexrfacor(curr_id,userbase,history,this.getFrameconn());
		    
		    if(curr_id!=null)
		    {
		    	commlexr=generalstat.getLexpr();
		    	commfacor=generalstat.getLfactor();
		    }
			String sql="select id,type from sname where infokind=" + infokind + " order by snorder";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql.toString());
            int i=1;
        	while(!istabid && this.frowset.next())
        	{
        		
        		  if((userView.isHaveResource(IResourceConstant.STATICS,this.frowset.getString("id"))))
        		  {
        			  statId=this.frowset.getString("id");
        			  type=this.frowset.getString("type");
       		          istabid=true;
       		          break;
        		  }
        		  
        	}		
        	this.getFormHM().put("statid", statId);
        	this.getFormHM().put("istwostat","1");
		}catch(Exception e){
			e.printStackTrace();
		}
		if(!istabid)
		{
			if(userView.getVersion()<50)//版本号大于等于50才显示这些功能
		      throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("workbench.stat.nostatitem"),"",""));
		}
		else
		{
			if("2".equals(type))
				this.getFormHM().put("istwostat","2");
    	}
		ArrayList list=new ArrayList();	
	    StatDataEncapsulation simplestat=new StatDataEncapsulation();
	    
	    if((result==null|| "".equals(result))&&statId!=null&&statId.length()>0)
	    {
	    	StringBuffer sql =new StringBuffer();
			sql.append("select flag from SName where id=");
			sql.append(statId);
			List rs =ExecuteSQL.executeMyQuery(sql.toString());
			if (!rs.isEmpty()) {
				LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				String flag = rec.get("flag")!=null?rec.get("flag").toString():"";
				if(flag!=null&& "1".equals(flag))
					isresult=false; 
			}
	    }else if(result!=null&& "1".equals(result))
	    	isresult=false; 	    
	    //System.out.println(userbase+"---"+Integer.parseInt(statId)+"---"+querycond+"---"+ userView.getUserName()+"---"+userView.getManagePrivCode()+"---userView---"+infokind+"---"+isresult+"---"+commlexr+"---"+commfacor+"---"+preresult+"---"+history);
	    try {
			statvalues =simplestat.getLexprData(userbase, Integer.parseInt(statId), querycond, userView.getUserName(),userView.getManagePrivCode(),userView,infokind,isresult,commlexr,commfacor,preresult,history);
		    SNameDisplay = simplestat.getSNameDisplay();
		   if (statvalues != null && statvalues.length > 0) 
		   {
			    fieldDisplay = simplestat.getDisplay();
				DefaultPieDataset dataset = new DefaultPieDataset();
				int statTotal = 0;
				for (int i = 0; i < statvalues.length; i++) {
					CommonData vo = new CommonData();
					vo.setDataName(fieldDisplay[i]);
					vo.setDataValue(String.valueOf(statvalues[i]));
					list.add(vo);
					statTotal += statvalues[i];
				}
				// System.out.println("wwwwwwww");
				this.getFormHM().put("snamedisplay", SNameDisplay);
				this.getFormHM().put("list", list);
			}else {
				StringBuffer sql = new StringBuffer();
				sql.append("select * from SName where id=");
				sql.append(statId);
				List rs = ExecuteSQL.executeMyQuery(sql.toString());
				if (!rs.isEmpty()) {
					LazyDynaBean rec = (LazyDynaBean) rs.get(0);
					SNameDisplay = rec.get("name") != null ? rec.get("name")
							.toString() : "";
				}
				CommonData vo = new CommonData();
				vo.setDataName("");
				vo.setDataValue("0");
				list.add(vo);
				this.getFormHM().put("snamedisplay", SNameDisplay);
				this.getFormHM().put("list", list);
			}
		     HashMap jfreemap=new HashMap();
			 jfreemap.put(SNameDisplay, list);
			 this.getFormHM().put("jfreemap" ,jfreemap);
	    } catch (Exception e) {
			e.printStackTrace();
			StringBuffer sql = new StringBuffer();
			sql.append("select * from SName where id=");
			sql.append(statId);
			List rs = ExecuteSQL.executeMyQuery(sql.toString());			
			if (!rs.isEmpty()) {
				LazyDynaBean rec = (LazyDynaBean) rs.get(0);
				SNameDisplay = rec.get("name") != null ? rec.get("name")
						.toString() : "";
			}
			CommonData vo = new CommonData();
			vo.setDataName("");
			vo.setDataValue("0");
			list.add(vo);
			this.getFormHM().put("snamedisplay", SNameDisplay);
			this.getFormHM().put("list", list);
			HashMap jfreemap=new HashMap();
			jfreemap.put(SNameDisplay, list);
			this.getFormHM().put("jfreemap" ,jfreemap);
		}

	}
	
}
