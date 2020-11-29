/*
 * Created on 2006-2-7
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.general.card;

import com.hjsj.hrms.businessobject.common.commonfunction;
import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hjsj.hrms.businessobject.ykcard.DataEncapsulation;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchCardSortTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		//this.getFormHM().clear();
		// TODO Auto-generated method stub 
		TreeItemView treeItem=new TreeItemView();
		treeItem.setName("root");		
		treeItem.setIcon("/images/add_all.gif");	
		treeItem.setTarget("mil_body");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		//获得等级表的类型A人员B机构K职位
	    String strInfkind=(String)hm.get("inforkind");
		if(strInfkind==null||strInfkind.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("没有得到登记表类型！请确认类型"));
		//登记表rname.flagA：('A','B','K','R','P','H') = (人员,单位,职位,培训,绩效,基准岗位)
        String rootdesc=ResourceFactory.getProperty("general.card.allcard");
	    treeItem.setRootdesc(rootdesc);
	    String flag="";
        if("1,2,4".equals(strInfkind))  // 人员+单位+岗位
            flag="A,B,K";
        else
            flag=getCardFlag(strInfkind);
        String temp_id=(String)hm.get("temp_id");
	    treeItem.setLoadChieldAction("/general/card/searchcardstree?flag=1&moduleflag=&flaga=" + flag+"&temp_id="+temp_id);
	    treeItem.setAction("javascript:void(0)");	
//	    System.out.println(treeItem.toJS());
	    this.getFormHM().put("treeCode",treeItem.toJS());	  
	    ArrayList dblist=userView.getPrivDbList();
		 String dbcond=commonfunction.getDbcondString(dblist);
		this.getFormHM().put("dbcond",dbcond);
		
		String dbname=(String)hm.get("dbname");
		dbname=dbname!=null&&dbname.trim().length()>0?dbname:"";
		hm.remove("dbname");
		
		String userbase="";
		if(dblist.size()>0){
			for(int i=0;i<dblist.size();i++){
				if(dbname.toUpperCase().indexOf(dblist.get(i).toString().toUpperCase())!=-1){
					userbase=dblist.get(i).toString(); 
					break;
				}
			}  
        }else{
        	userbase="Usr";
        }
		
		userbase=userbase!=null&&userbase.trim().length()>0?userbase:"Usr";
		this.getFormHM().put("userbase",userbase);
		DataEncapsulation de = new DataEncapsulation(userView, getFrameconn());
		int firstPrivTabId = de.getFirstPrivCardTabId(flag, temp_id); 
        if("1,2,4".equals(strInfkind)) {
            flag = de.getCardFlag(firstPrivTabId);
            strInfkind = getInfoKind(flag);
        }
        this.getFormHM().put("inforkind",strInfkind);
        this.getFormHM().put("tabid", String.valueOf(firstPrivTabId));
        this.getFormHM().put("pageWidth", getPageWidth(String.valueOf(firstPrivTabId)));
		ContentDAO dao=new ContentDAO(this.getFrameconn());
        StringBuffer sql=new StringBuffer();
        /**自助平台的用户*/
        /*if(this.userView.getStatus()==4)
        {
        	UserObjectBo userbo=new UserObjectBo(this.getFrameconn());
        	userbo.createResultTable(userbase, strInfkind, this.userView.getUserName());
        }*/
        String cardtype="no";
        String dataFlag="";//
        /* <CARDSTYLE>A人员,B单位,K职位,R培训,P绩效</CARDSTYLE>
        <TEMPLATEID>考核模板号</TEMPLATEID><PLANID>考核计划号</PLANID>
        */  
        String istype="1";/*0代表薪酬1登记表2绩效*/	
        if("A".equals(flag))
        {
        	if(this.userView.getStatus()==4)
            {
        		sql.append("select ");
            	sql.append(userbase);
            	sql.append("A01.a0100,");
            	sql.append(userbase);
            	sql.append("A01.a0101 from t_sys_result ");            	
            	sql.append(",");
            	sql.append(userbase);
            	sql.append("A01 where UPPER(t_sys_result.nbase)='"+userbase.toUpperCase()+"' and UPPER(t_sys_result.username)='"+userView.getUserName().toUpperCase()+"' ");            	
            	sql.append("and t_sys_result.flag=0 and t_sys_result.obj_id=");
            	sql.append(userbase);
            	sql.append("A01.a0100 order by ");
            	sql.append(userbase);
            	sql.append("a01.a0000");	
            }else
            {
            	sql.append("select ");
            	sql.append(userbase);
            	sql.append("A01.a0100,");
            	sql.append(userbase);
            	sql.append("A01.a0101 from "+this.userView.getUserName());
            	sql.append(userbase);
            	sql.append("Result,");
            	sql.append(userbase);
            	sql.append("A01 where "+this.userView.getUserName());
            	sql.append(userbase);
            	sql.append("Result.a0100=");
            	sql.append(userbase);
            	sql.append("A01.a0100 order by ");
            	sql.append(userbase);
            	sql.append("a01.a0000");	
            }       	
        	dataFlag="<CARDSTYLE>A</CARDSTYLE>";
        }    	
	    else if("B".equals(flag)) {
	    	if(this.userView.getStatus()==4)
            {
	    		sql.append("select t_sys_result.obj_id as b0110,organization.codeitemdesc from t_sys_result,organization where organization.codeitemid=t_sys_result.obj_id and t_sys_result.flag=1  and UPPER(t_sys_result.username)='"+userView.getUserName().toUpperCase()+"'");
            }else
            {
            	sql.append("select "+this.userView.getUserName()+"BResult.b0110,organization.codeitemdesc from "+this.userView.getUserName()+"BResult,organization where organization.codeitemid="+this.userView.getUserName()+"BResult.b0110");
            }        	
        	dataFlag="<CARDSTYLE>B</CARDSTYLE>";
	    }    	
	    else if("K".equals(flag)){
	    	if(this.userView.getStatus()==4)
            {
	    		sql.append("select t_sys_result.obj_id as e01a1,organization.codeitemdesc from t_sys_result,organization where organization.codeitemid=t_sys_result.obj_id and t_sys_result.flag=2  and UPPER(t_sys_result.username)='"+userView.getUserName().toUpperCase()+"'");
            }else
            {
            	sql.append("select "+this.userView.getUserName()+"KResult.E01A1,organization.codeitemdesc from "+this.userView.getUserName()+"KResult,organization where organization.codeitemid="+this.userView.getUserName()+"KResult.E01A1");
            }
	    	       
	    	dataFlag="<CARDSTYLE>K</CARDSTYLE>";
	    }else if("P".equals(flag))
	    {
	    	String plan_id=(String)hm.get("plan_id");
	    	sql.append("select object_id,a0101 from per_result_"+plan_id);
	    	this.getFormHM().put("plan_id", plan_id);
	    	cardtype="plan";
	        String sql2="select template_id from per_plan where plan_id="+plan_id;
	        
	        String template_id="";
	        try {
				this.frowset=dao.search(sql2.toString());
				if(this.frowset.next())
					template_id=this.frowset.getString("template_id");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			istype="2";
	    	dataFlag="<CARDSTYLE>P</CARDSTYLE><TEMPLATEID>"+template_id+"</TEMPLATEID><PLANID>"+plan_id+"</PLANID>";
	    	this.getFormHM().put("userbase","Usr");
	    }
	    else if("H".equals(flag))
	    {
	    	String codeset=new CardConstantSet().getStdPosCodeSetId();
    		sql.append("select t_sys_result.obj_id as h0100,codeitemdesc from t_sys_result,CodeItem "+
    				   " where CodeItem.codeitemid=t_sys_result.obj_id and codesetid='"+codeset+"'"+
    				        " and t_sys_result.flag=5"+// 基准岗位
    				        " and UPPER(t_sys_result.username)='"+userView.getUserName().toUpperCase()+"'");
    		dataFlag="<CARDSTYLE>H</CARDSTYLE>";
	    }
        this.getFormHM().put("cardtype", cardtype);
        this.getFormHM().put("dataFlag", dataFlag);
        this.getFormHM().put("istype", istype);
        try
		{

        	/***
        	 * 业务用户不做做查询没有生成结果表 用户名+库前缀+Result则下面程序不需要执行
        	 * */
        	DbWizard db=new DbWizard(this.frameconn);
        	boolean tableFlag=true;
        	if("A".equals(flag)&&this.userView.getStatus()!=4){//业务用户是否存在查询结果表
        		tableFlag=db.isExistTable(this.userView.getUserName()+userbase+"Result",false);
//        		db.i
        	}
        	
        	if("B".equals(flag)&&this.userView.getStatus()!=4){
        		tableFlag=db.isExistTable(this.userView.getUserName()+"BResult",false);
        	}
        	
        	if("K".equals(flag)&&this.userView.getStatus()!=4){
        		tableFlag=db.isExistTable(this.userView.getUserName()+"KResult",false);
        	}
        	if(tableFlag){
        		
        		this.frowset=dao.search(sql.toString());
   	    	 String a0100=null;
               if("A".equals(flag))
               {
           	   if(this.frowset.next())
           	  {
           		if(a0100==null)
           			a0100=this.frowset.getString("a0100");
           	  } 
               }else if("B".equals(flag)) {
               	 if(this.frowset.next())
              	  {
              		if(a0100==null)
              			a0100=this.frowset.getString("b0110");
              	  } 
              }    	
       	    else if("K".equals(flag)){
       	      if(this.frowset.next())
              	  {
              		if(a0100==null)
              			a0100=this.frowset.getString("e01a1");
              	  } 
       	    }else if("p".equalsIgnoreCase(flag)){
       	      if(this.frowset.next())
              	  {
              		if(a0100==null)
              			a0100=this.frowset.getString("object_id");
              	  } 
       	    }else {
       	      if(this.frowset.next())
              	  {
              		if(a0100==null)
              			a0100=this.frowset.getString(1);
              	  } 
       	    }             	
           	this.getFormHM().put("a0100",a0100);
        	}
	    	
        	//end
            String dbType="1";
            switch(Sql_switcher.searchDbServer())
            {
                  case Constant.MSSQL:
                  {
                      dbType="1";
                      break;
                  }
                  case Constant.ORACEL:
                  { 
                      dbType="2";
                      break;
                  }
                  case Constant.DB2:
                  {
                      dbType="3";
                      break;
                  }
            }
            this.getFormHM().put("dbType", dbType);
        }catch(Exception e)
		{
        	e.printStackTrace();
        	throw GeneralExceptionHandler.Handle(e);
        }
	}

	private String getCardFlag(String infoKind) {
	    String flag="";
        if("1".equals(infoKind))
            flag="A";
        else if("2".equals(infoKind))
            flag="B";
        else if("4".equals(infoKind))
            flag="K";     
        else if("5".equals(infoKind))
            flag="P";          
        else if("6".equals(infoKind))  // 基准岗位
            flag="H";
        return flag;
	}
	
    private String getInfoKind(String cardFlag) {
        String infoKind="";
        if("A".equals(cardFlag))
            infoKind="1";
        else if("B".equals(cardFlag))
            infoKind="2";
        else if("K".equals(cardFlag))
            infoKind="4";     
        else if("P".equals(cardFlag))
            infoKind="5";          
        else if("H".equals(cardFlag))  // 基准岗位
            infoKind="6";
        return infoKind;
    }
    
    private int getPageWidth(String tabid){
    	ContentDAO dao=new ContentDAO(this.frameconn);
    	RowSet rs=null;
    	int width=0;
		 String sql="select paperH,paperori,paperW,lMargin,rmargin from rname where tabid='"+tabid+"'";
	     	try
	     	{
	     		rs=dao.search(sql);
	     		float w=0;
	     		if(rs.next())
	     		{
	     			String ori=rs.getString("paperori");
	     			if(ori==null||ori.length()<=0)
	     				ori="1";
	     			if("2".equals(ori))
	     				w=rs.getFloat("paperH")+rs.getFloat("lMargin")+rs.getFloat("rmargin");
	     			else
	     				w=rs.getFloat("paperW")+rs.getFloat("lMargin")+rs.getFloat("rmargin");
	     		}
	     		w=w*0.0393701f;
	     		w=w*96f;
	     		width=(int)w;
	     	}catch(Exception e)
	     	{
	     		e.printStackTrace();
	     	}
			return width;
	
	
	}
    
}
