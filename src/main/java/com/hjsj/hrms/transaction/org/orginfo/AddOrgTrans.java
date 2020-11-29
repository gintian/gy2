/*
 * Created on 2005-12-12
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.org.orginfo;

import com.hjsj.hrms.businessobject.org.AddOrgInfo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.common.LabelValueView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddOrgTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList codesetlist=new ArrayList();
		String code=(String)this.getFormHM().get("code");
		if(code==null||code.length()<1){
			if(!userView.isSuper_admin()){
				code=getBusi_org_dept(this.userView);
				if(code.length()>2&&code.indexOf("`")!=-1)
					code=code.substring(2,code.indexOf("`"));
				this.getFormHM().put("code", code);
			}
		}
		String kind=(String)this.getFormHM().get("kind");
		String vflag = (String)this.getFormHM().get("vflag");
		//String codesetid=(String)this.getFormHM().get("codesetid");	
 		String codesetid="UN";
		//System.out.println(kind);
		//if(code==null || code.trim().length()==0)
			if("0".equals(kind))
			{
				if(this.userView.hasTheFunction("23055"))//判断是否有新增岗位权限 sunjian 2017-8-18
					codesetid="@K";
			}else if("2".equals(kind))
			{
				if(this.userView.hasTheFunction("23053"))
					codesetid="UN";
				else if(this.userView.hasTheFunction("23054"))
					codesetid="UM";
				else if(this.userView.hasTheFunction("23055"))
					codesetid="@K";
			}else if("1".equals(kind))
			{
				if(this.userView.hasTheFunction("23054"))
					codesetid="UM";
				else if(this.userView.hasTheFunction("23055"))
					codesetid="@K";
			}else
			{
				codesetid="UN";
			}
		String labelmessage="";
		String first="1";
		int len=30;
		StringBuffer strsql=new StringBuffer();
		strsql.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,corcode from organization where parentid='");
		strsql.append(code);
		//strsql.append("' and codeitemid<>parentid and codesetid='");
		//strsql.append(codesetid+"'");
		strsql.append("' and codeitemid<>parentid ");
		//strsql.append(" and codesetid='"+codesetid+"'");
		if("1".equals(vflag)){
			strsql.append("union select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,corcode from vorganization where parentid='");
			strsql.append(code);
			strsql.append("' and codeitemid<>parentid ");
		}
		strsql.append(" order by codeitemid desc");
		//System.out.println(strsql);
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{	
			this.frowset=dao.search(strsql.toString());
			boolean b = false;
			while(this.frowset.next())
			{		
				b = true;
				first="0";
				this.getFormHM().put("first",first);
			    String chilecode=this.frowset.getString("codesetid");
			    String codeitemid=this.frowset.getString("codeitemid");
			    String corcode=this.frowset.getString("corcode");
			    int grade=this.frowset.getInt("grade");
			    this.getFormHM().put("grade",String.valueOf(grade));
			    if(chilecode!=null)
			    {
			    	//if(codesetid.equalsIgnoreCase(chilecode)){
				    	if(code!=null)
				    	{
				    		len=codeitemid.trim().length()-code.trim().length();
				    	}
				    	else
				    	{
				    		len=codeitemid.trim().length();
				    	}
				    	labelmessage=ResourceFactory.getProperty("label.org.childmessage") + len;
				    	AddOrgInfo addOrgInfo=new AddOrgInfo();
					    codeitemid=addOrgInfo.GetNext(codeitemid,code);
					    if(corcode!=null&&corcode.length()>0)
					    	corcode=addOrgInfo.GetNext(corcode,code);
					    this.getFormHM().put("codeitemid",codeitemid);
					    this.getFormHM().put("corcode","");
					    break;
			    	/*}
			    	else{
			    		if(code!=null)
					      {
					        len=30-code.trim().length();
					      }
			    		labelmessage=ResourceFactory.getProperty("label.org.firstchildmessage") + len;
			    		AddOrgInfo addOrgInfo=new AddOrgInfo();
					    codeitemid=addOrgInfo.GetNext(codeitemid,code);
					    this.getFormHM().put("codeitemid",codeitemid);
					    this.getFormHM().put("first","1");
			    	}*/
			    }
				/*first="0";
			    String chilecode=this.frowset.getString("codeitemid");
			    String codeitemid=this.frowset.getString("codeitemid");
			    //System.out.println(codesetid + code + chilecode);
			    int grade=this.frowset.getInt("grade");
			    if(chilecode!=null)
			    {
			    	if(code!=null)
			    	{
			    		len=chilecode.trim().length()-code.trim().length();
			    	}
			    	else
			    	{
			    		len=chilecode.trim().length();
			    	}
			    }
			    this.getFormHM().put("grade",String.valueOf(grade));
			    this.getFormHM().put("first",first);
			    labelmessage=ResourceFactory.getProperty("label.org.childmessage") + len;
			    AddOrgInfo addOrgInfo=new AddOrgInfo();
			    codeitemid=addOrgInfo.getChildCodeitemid(codeitemid,code,len);
			    this.getFormHM().put("codeitemid",codeitemid);*/
		    }
			if(b){
		    }else
		    {
		    	String codeitemid="";
		    	String corcode="";
		    	first="1";
		    	strsql.delete(0,strsql.length());
				strsql.append("select grade from organization where codeitemid='");
		    	strsql.append(code);
		    	strsql.append("' and codesetid='");
		        strsql.append(codesetid);
		        strsql.append("'");
		    	this.frowset=dao.search(strsql.toString());
		    	int grade=1;
		    	if(this.frowset.next())
		    	{
		    	  grade=this.frowset.getInt("grade");
		    	  grade=grade + 1;
		    	}		    	
		    	this.getFormHM().put("grade",String.valueOf(grade));
		    
		      if(code!=null && code.trim().length()>0)
		      {
		        len=30-code.trim().length();
		    	this.getFormHM().put("first",first);
		      }
		      else
		      {
		    	  
		    	  strsql.delete(0,strsql.length());
				  strsql.append("select ");
				  strsql.append(Sql_switcher.length("codeitemid"));
				  strsql.append(" as codeitemidlen from organization where parentid=codeitemid and codesetid='");
		          strsql.append(codesetid);
		          strsql.append("'");	
		          //System.out.println(strsql.toString());
			      this.frowset=dao.search(strsql.toString()); 
			      if(this.frowset.next())
			      {
			    	  //System.out.println("sss");
			    	  len=this.frowset.getInt("codeitemidlen");
			    	  this.getFormHM().put("first","0");
			    	  //String sql="select * from organization where parentid=codeitemid and codesetid='"+codesetid+"'";
			    	  String sql="select * from organization where parentid=codeitemid ";
			    	  sql=sql+" order by codeitemid desc";
			    	  this.frowset=dao.search(sql);
			    	  if(this.frowset.next())
			    	  {
			    		  codeitemid=this.frowset.getString("codeitemid");
			    		  corcode=this.frowset.getString("corcode");
			    	  }
			      }
			      else
			      {
			    	  this.getFormHM().put("first","1");
			      }
		      }
		      labelmessage=ResourceFactory.getProperty("label.org.firstchildmessage") + len;
		      AddOrgInfo addOrgInfo=new AddOrgInfo();
		      //codeitemid=addOrgInfo.getChildCodeitemid(codeitemid,code,len);
		      codeitemid=addOrgInfo.GetNext(codeitemid,code);
		      this.getFormHM().put("codeitemid",codeitemid);
		      this.getFormHM().put("corcode","");
		    }
			
			String posfillable="0",unitfillable="0";
				RecordVo pos_code_field_constant_vo=ConstantParamter.getRealConstantVo("POS_CODE_FIELD",this.getFrameconn());
				if(pos_code_field_constant_vo!=null)
				{
				  String  pos_code_field=pos_code_field_constant_vo.getString("str_value");
				  if(pos_code_field!=null&&pos_code_field.length()>1){
					  FieldItem item = DataDictionary.getFieldItem(pos_code_field);
					  if(item!=null){
						  if(item.isFillable()){
							  posfillable="1";
						  }
					  }
				  }
				}
				RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD",this.getFrameconn());
				if(unit_code_field_constant_vo!=null)
				{
				  String  unit_code_field=unit_code_field_constant_vo.getString("str_value");
				  if(unit_code_field!=null&&unit_code_field.length()>1){
					  FieldItem item = DataDictionary.getFieldItem(unit_code_field);
					  if(item!=null){
						  if(item.isFillable()){
							  unitfillable="1";
						  }
					  }
				  }
				}
				this.getFormHM().put("posfillable", posfillable);
				this.getFormHM().put("unitfillable", unitfillable);
		}catch(Exception e){
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}
		
		if("0".equals(kind))
		{
		}else if("2".equals(kind))
		{
			LabelValueView labelValue1=new LabelValueView("UM",ResourceFactory.getProperty("label.codeitemid.um"));
			LabelValueView labelValue2=new LabelValueView("KK",ResourceFactory.getProperty("label.codeitemid.kk"));
			codesetlist.add(labelValue1);
			codesetlist.add(labelValue2);
		}else if("1".equals(kind))
		{
			LabelValueView labelValue1=new LabelValueView("UN",ResourceFactory.getProperty("label.codeitemid.un"));
			LabelValueView labelValue2=new LabelValueView("UM",ResourceFactory.getProperty("label.codeitemid.um"));
			LabelValueView labelValue3=new LabelValueView("KK",ResourceFactory.getProperty("label.codeitemid.kk"));
			codesetlist.add(labelValue1);
			codesetlist.add(labelValue2);
			codesetlist.add(labelValue3);
		}		
		this.getFormHM().put("codesetlist",codesetlist);
		this.getFormHM().put("len",String.valueOf(len));
		this.getFormHM().put("labelmessage",labelmessage);
		//this.getFormHM().put("codeitemid","");
		this.getFormHM().put("codesetid",codesetid);
		this.getFormHM().put("codeitemdesc","");
		this.getFormHM().put("isrefresh","no");
		
	}    
   
	private String getBusi_org_dept(UserView userView) {
		String busi = "";
				String busi_org_dept = "";
				Connection conn = null;
				RowSet rs = null;
				try {
					
					busi_org_dept = userView.getUnitIdByBusi("4");
					if (busi_org_dept.length() > 0) {
						busi = com.hjsj.hrms.utils.PubFunc.getTopOrgDept(busi_org_dept);
					}else{
						busi=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {// 1,UNxxx`UM9191`|2,UNxxx`UM9191`
					if (rs != null)
						try {
							rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					if (conn != null)
						try {
							conn.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
				}
		return busi;
	}
}
