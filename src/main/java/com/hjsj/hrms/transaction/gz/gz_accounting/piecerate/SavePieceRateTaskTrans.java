package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SavePieceRateTaskTrans extends IBusiness {
	String model ="";
	String s0100 = "";
	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList fieldlist=(ArrayList)this.getFormHM().get("fielditemlist");	         
		model = (String) hm.get("model");
		s0100 = (String) hm.get("s0100");	
		if ("add".equals(model)){
			s0100 = "";	
		}
		
		try{
			StringBuffer fields=new StringBuffer();
			StringBuffer fieldvalues=new StringBuffer();
			String[] fieldsname=new String[fieldlist.size()];
			String[] fieldcode=new String[fieldlist.size()];
			String value="";
			
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem fieldItem=(FieldItem)fieldlist.get(i);
				fields.append(fieldItem.getItemid());
				fieldsname[i]=fieldItem.getItemid();
				fieldItem.setValue(PubFunc.getStr(fieldItem.getValue()));
				value = fieldItem.getValue();
	            if("D".equals(fieldItem.getItemtype()))				{
	                if (value.length()>13){//有小时
	                    String hour = value.substring(11,13);
	                    int ihour=1;
	                    try{
	                        ihour = Integer.parseInt(hour);
	                    }
	                    catch(Exception e){
	       	            }
	                    if (ihour >23){
	                        throw new GeneralException('['+fieldItem.getItemdesc()+"]是日期型指标,小时值必须介于 0 和 23 之间"); 
	                    }	                        
	                }
					fieldvalues.append(PubFunc.DateStringChange(fieldItem.getValue()));
					fieldcode[i]=PubFunc.DateStringChange(fieldItem.getValue());				 		  
				}else if("M".equals(fieldItem.getItemtype()))				{
					if (fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue()))
					{	fieldcode[i]="null";
						fieldvalues.append("null");
					}
					else
					{
						fieldcode[i]="'" + fieldItem.getValue() + "'";					
						fieldvalues.append("'" + fieldItem.getValue() + "'");
					}
				}else if("N".equals(fieldItem.getItemtype()))
				{
					if (fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue()))
					{	
						fieldcode[i]="null";
						fieldvalues.append("null");
					}
					else
					{
						fieldcode[i]=fieldItem.getValue();
						fieldvalues.append(fieldItem.getValue());
					}
				}
				else
				{
					if (fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue()))
					{
						fieldcode[i]="null";
						fieldvalues.append("null");
					}
					else
					{
						if(fieldItem.isCode())
						{
							//value=AdminCode.getCodeName(fieldItem.getCodesetid(), fieldItem.getValue());
							value = fieldItem.getValue();
							if(value==null||value.length()<=0)
							{
								fieldItem.setValue("");
							}
						}
	
						fieldvalues.append("'" + PubFunc.splitString(fieldItem.getValue(),fieldItem.getItemlength()) + "'");
					}
				}			
				fields.append(",");
				fieldvalues.append("`");
			}	
			
			String Strsql="";
		    String strfield="";
		    String strValue="";
			String[] arrfield = fields.toString().split(",");
			String[] arrfieldvalue = fieldvalues.toString().split("`");
	
			if("add".equals(model))
			{	
				for (int i=0;i<arrfield.length;i++)
				{
				  if (!"null".equalsIgnoreCase(arrfieldvalue[i])){
					  strfield =strfield+","+ arrfield[i];
					  strValue =strValue+","+ arrfieldvalue[i];					  
				  }				  
					
				}
			   if (strfield.length()>1){
				     s0100=getMaxNumber();
					 Strsql ="insert into s01 (s0100,sp_flag"+strfield
					         +") values("+s0100+",'01'"+strValue
					         +")";
					dao.update(Strsql); 
				  }			   
			 }	
		     else
		    {
				for (int i=0;i<arrfield.length;i++)
				{
				  strfield = strfield+","+arrfield[i]+"="+arrfieldvalue[i];
					
				} 
			   if (strfield.length()>1){
					 strfield= strfield.substring(1); 
					 Strsql ="update s01 set "+strfield
			         +" where s0100="+s0100 ;
		    	dao.update(Strsql); 
				  }				   
			}
	
			this.getFormHM().put("infofieldlist",fieldlist);
			this.getFormHM().put("actiontype","update");
			this.getFormHM().put("s0100",s0100);
	        
    	}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
    	finally 
		{

		}

	}
	
	public String getMaxNumber(){
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		 RowSet rs=null;
		 String Maxid="1";
		 try {
			String sql="select max(s0100) as s0100 from S01";
			try {
				rs=dao.search(sql);
				if (rs.next()){
				  Maxid=String.valueOf(rs.getInt("s0100")+1);				
							
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
         
		} catch (GeneralException e) {
			e.printStackTrace();			
		}//顶级单位		
		return Maxid;
	}

}
