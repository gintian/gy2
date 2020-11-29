package com.hjsj.hrms.transaction.dtgh.party;

import com.hjsj.hrms.businessobject.org.AddOrgInfo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class AddorEditPartyMainInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		//HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String fieldsetid = (String)this.getFormHM().get("fieldsetid");
		if(fieldsetid==null||fieldsetid.length()<=0)
			return;
		String type=(String)this.getFormHM().get("type");
		String codeitemid = (String)this.getFormHM().get("codeitemid");		
		ArrayList infolist = DataDictionary.getFieldList(fieldsetid, 1);
		ArrayList infofieldlist = new ArrayList();
		String [] msg= null;
		if("edit".equals(type)){
			msg= getCodeitem(codeitemid);
		}else{
			msg= getNewCodeitem((String)this.getFormHM().get("a_code"));
		}
		FieldItemView fieldItemV=new FieldItemView();
		 fieldItemV.setItemdesc(ResourceFactory.getProperty("conlumn.codeitemid.caption"));
		 fieldItemV.setItemtype("A");
		 fieldItemV.setItemid("codeitemid");
		 fieldItemV.setPriv_status(2);	
		 if("edit".equals(type))
		 fieldItemV.setReadonly(true);
		 fieldItemV.setViewvalue(msg[0]);
		 fieldItemV.setValue(msg[0]);	
		 fieldItemV.setItemlength(Integer.parseInt(msg[1]));
		 fieldItemV.setCodesetid("0");
		 fieldItemV.setFillable(true);
		 infofieldlist.add(fieldItemV);
		 
		 fieldItemV=new FieldItemView();
		 String columnName = "";
		 if("H01".equals(fieldsetid.toUpperCase()))
			 columnName=ResourceFactory.getProperty("h0100.label");
		 else
			 columnName=ResourceFactory.getProperty("column.name");
		 
		 fieldItemV.setItemdesc(columnName);
		 fieldItemV.setItemtype("A");
		 fieldItemV.setPriv_status(2);
		 fieldItemV.setItemid("codeitemdesc");
		 if("edit".equals(type)){
			 fieldItemV.setValue(msg[2]);
			 fieldItemV.setViewvalue(msg[2]);
		 }else{
			 fieldItemV.setValue("");
			 fieldItemV.setViewvalue("");
		 }
		 fieldItemV.setCodesetid("0");
		 fieldItemV.setItemlength(50);
		 fieldItemV.setFillable(true);
		 if("edit".equals(type) && "H01".equals(fieldsetid.toUpperCase()))
		    fieldItemV.setReadonly(true);
		 infofieldlist.add(fieldItemV);
		 
		 RecordVo vo = new RecordVo(fieldsetid.toLowerCase());
		 if("edit".equals(type)){
			 ContentDAO dao = new ContentDAO(this.frameconn);
			 checkExist(fieldsetid.toLowerCase(), codeitemid,dao);
			 vo.setString((fieldsetid+"00").toLowerCase(), codeitemid);
			 try {
				vo = dao.findByPrimaryKey(vo);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		 }
		for(int i=0;i<infolist.size();i++){
			 FieldItem fieldItem=(FieldItem)infolist.get(i);
			 if("h0100".equals(fieldItem.getItemid()))
				 continue;
			 FieldItemView fieldItemView=new FieldItemView();
				fieldItemView.setAuditingFormula(fieldItem.getAuditingFormula());
				fieldItemView.setAuditingInformation(fieldItem.getAuditingInformation());
				fieldItemView.setCodesetid(fieldItem.getCodesetid());
				fieldItemView.setDecimalwidth(fieldItem.getDecimalwidth());
				fieldItemView.setDisplayid(fieldItem.getDisplayid());
				fieldItemView.setDisplaywidth(fieldItem.getDisplaywidth());
				fieldItemView.setExplain(fieldItem.getExplain());
				fieldItemView.setFieldsetid(fieldItem.getFieldsetid());
				fieldItemView.setItemdesc(fieldItem.getItemdesc());
				fieldItemView.setItemid(fieldItem.getItemid().toUpperCase());
				fieldItemView.setItemlength(fieldItem.getItemlength());
				fieldItemView.setItemtype(fieldItem.getItemtype());
				fieldItemView.setModuleflag(fieldItem.getModuleflag());
				fieldItemView.setState(fieldItem.getState());
				fieldItemView.setUseflag(fieldItem.getUseflag());
				fieldItemView.setPriv_status(fieldItem.getPriv_status());
				fieldItemView.setFillable(fieldItem.isFillable());
				fieldItemView.setInputtype(fieldItem.getInputtype());
             //在struts用来表示换行的变量
			    fieldItemView.setRowflag(String.valueOf(infolist.size()+1));
			    if("edit".equals(type)){
			    	if("D".equals(fieldItemView.getItemtype())){
			    		fieldItemView.setValue(vo.getString((fieldItemView.getItemid()).toLowerCase()).replaceAll("-", "\\."));
			    	}else{
				    	fieldItemView.setValue(vo.getString((fieldItemView.getItemid()).toLowerCase()));
				    	if(!"0".equals(fieldItemView.getCodesetid())){
				    		fieldItemView.setViewvalue(AdminCode.getCodeName(fieldItemView.getCodesetid(), vo.getString((fieldItemView.getItemid()).toLowerCase())));
				    	}
			    	}
			    }
			    infofieldlist.add(fieldItemView);
		}
		this.getFormHM().put("infofieldlist", infofieldlist);
	}

	private String[] getNewCodeitem(String a_code) throws GeneralException{
		
		String first="1";
		int len=30;
		String codeitemid = "";
		StringBuffer strsql=new StringBuffer();
		if(a_code!=null && a_code.length()==2)
		{
			strsql.append("select max(codeitemid) as codeitemid from codeitem where codeitemid=parentid and codesetid='");
			strsql.append(a_code.substring(0, 2));
			strsql.append("'");
		}
		else if(a_code!=null && a_code.length()>2)
		{
			strsql.append("select max(codeitemid) as codeitemid from codeitem where parentid='");
			strsql.append(a_code!=null && a_code.length()>=2?a_code.substring(2):"");
			strsql.append("' and codeitemid<>parentid and codesetid='");
			strsql.append(a_code.substring(0, 2));
			strsql.append("'");
		}
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{			
			cat.debug("------strsql----->" + strsql);
			if(strsql!=null && strsql.toString().length()>0)
			   this.frowset=dao.search(strsql.toString());
			if(this.frowset !=null && this.frowset.next())
			{		
				
			    String chilecode=this.frowset.getString("codeitemid");
			    if(chilecode!=null)
			    {
			    	first="0";
			    	if(a_code!=null)
			    	{
			    		len=chilecode.trim().length()-a_code.trim().length()+2;
			    	}
			    	else
			    	{
			    		len=chilecode.trim().length();
			    	}
			    	AddOrgInfo addOrgInfo=new AddOrgInfo();
				    codeitemid=addOrgInfo.GetNext(chilecode,a_code.substring(2));
				    this.getFormHM().put("first",first);
			    }else
			    {
			    	first="1";		    	
			    	this.getFormHM().put("first",first);
			      if(a_code!=null)
			        len=30-a_code.trim().length();
			    }
			    
		    }else
		    {
		    	first="1";		    	
		    	this.getFormHM().put("first",first);
		      if(a_code!=null)
		        len=30-a_code.trim().length();
		    }
		}catch(Exception e){
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}
		String [] mgs={codeitemid.length()>0?codeitemid:"001",String.valueOf(len)};
		return mgs;
	}
	
private String[] getCodeitem(String codeitemid) throws GeneralException{
		String param = (String)this.getFormHM().get("param");
		String codesetid = "64";
		if("Y".equals(param)){
			codesetid = "64";
		}else if("V".equals(param)){
			codesetid = "65";
		}else if("W".equals(param)){
			codesetid = "66";
		}else if("H".equals(param)){
			RecordVo constantuser_vo = ConstantParamter
			.getRealConstantVo("PS_C_CODE");
			codesetid = constantuser_vo.getString("str_value");
		}
		int len=30;
		StringBuffer strsql=new StringBuffer();
		strsql.append("select codeitemdesc from codeitem where codeitemid='"+codeitemid+"' and codesetid='"+codesetid+"'");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String codeitemdesc="";
		try{			
			if(strsql!=null && strsql.toString().length()>0)
			this.frowset=dao.search(strsql.toString());
			if(this.frowset.next())
				codeitemdesc=this.frowset.getString("codeitemdesc");
			this.getFormHM().put("first","0");

		}catch(Exception e){
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}
		String [] mgs={codeitemid,String.valueOf(len),codeitemdesc};
		return mgs;
	}

    public void checkExist(String table,String codeid,ContentDAO dao){
    	 String sql = "select '1' from "+table+" where "+table+"00='"+codeid+"'";
    	 try{
    	    this.frowset = dao.search(sql);
    	    if(!frowset.next()){
    	    	RecordVo vo = new RecordVo(table);
    	    	vo.setString(table+"00", codeid);
    	    	dao.addValueObject(vo);
    	    }
    	 }catch(Exception e){
    		 e.printStackTrace();
    	 }
    	 
    }
}
