package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.businessobject.gz.piecerate.PieceRateBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;


public class PieceRateDetailTrans extends IBusiness {
	ArrayList list=new ArrayList();
	String S0402Desc="货名";
	String S0401Desc="操作过程";
	boolean bCanEdit=true;
	public void execute() throws GeneralException {
		try{
			String sql=""; 
			String tableName="";
            Sys_Oth_Parameter sysparam=new Sys_Oth_Parameter(this.frameconn);
            String topOrgDesc=sysparam.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
            if(topOrgDesc==null|| "".equals(topOrgDesc))
                topOrgDesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
            this.getFormHM().put("topOrgDesc", topOrgDesc);
            
			String canEdit="true";
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");	
			String s0100=(String) hm.get("s0100");
			if ((s0100==null)|| ("".equals(s0100))) { s0100="0";}
			String model=(String) hm.get("model");
			
			String personname=(String) hm.get("personname");
			hm.remove("personname");
			if ((personname==null)) { personname="";}
			personname = SafeCode.decode(personname);
			personname = PubFunc.keyWord_reback(personname);
			
			PieceRateBo TaskBo=new PieceRateBo(this.getFrameconn(),s0100,this.userView);
			String infoStr=TaskBo.getInfostr();
			if (!TaskBo.CanEdit()){
				canEdit="false";	
				bCanEdit=false;
			}
		
			initS0402Desc();
			if ("people".equalsIgnoreCase(model)){
				tableName="S05";	
				setFieldList("S05");
				sql ="select Dbname.Dbid,Dbname.dbname,S05.* from S05,dbname "
			        +"where upper(S05.nbase) = upper(dbname.pre)"
			        +" and S05.s0100=" + s0100;
				String s=TaskBo.getPrivPre();
				if (!"".equals(s)){
					sql=sql +" and "+s;	
				}
				if (!"".equals(personname)){
				    sql=sql +" and S05.A0101 like '%"+personname.trim()+"%'";   
				}
			    sql=sql +" order by Dbid,B0110,E0122,E01A1";
			}
			else
			{
				tableName="S04";	
				setFieldList("S02");
				setFieldList("S03");
				setFieldList("S04");
				sql ="select S0203,S0303,S04.* from S04,S02,S03 "
					        +"where S04.S0401 = S02.S0200 and S04.S0402= S03.S0300"
					        +" and S04.s0100=" + s0100
					        +" order by I9999";
			}

			this.getFormHM().put("fieldlist",list);
			this.getFormHM().put("sql", sql);
			this.getFormHM().put("s0100", s0100);
			this.getFormHM().put("busiid", TaskBo.getBusiId());
			this.getFormHM().put("tableName",tableName);
			this.getFormHM().put("infoStr", infoStr);
			this.getFormHM().put("canEdit",canEdit);			
			this.getFormHM().put("dbname",getNbases());
			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}	

	public void setFieldList(String fieldSet){	
		StringBuffer format=new StringBuffer();	
		String ReadStr=",NBASE,I9999,A0100,A0101,S0100,B0110,E0122,E01A1,";
		format.append("###################");	
		boolean bReadOnly=false;
		ArrayList fieldItemList = DataDictionary.getFieldList(fieldSet.toUpperCase(),Constant.USED_FIELD_SET);
		for(int i=0;i<fieldItemList.size();i++)
		{   bReadOnly=false;
			FieldItem  fielditem = (FieldItem)fieldItemList.get(i);
		    String desc=fielditem.getItemdesc();
			if ("S04".equalsIgnoreCase(fieldSet)){
				if ("S0402".equalsIgnoreCase(fielditem.getItemid())
					       || "S0401".equalsIgnoreCase(fielditem.getItemid()))
				{
					continue; 
					}						
			}
			else if ("S02".equalsIgnoreCase(fieldSet)){
				if 	(!"S0203".equalsIgnoreCase(fielditem.getItemid()) )
				{
					continue;	
				}	
				desc=this.S0401Desc;
				bReadOnly=true;
			}
			else if ("S03".equalsIgnoreCase(fieldSet)){
				if 	(!"S0303".equalsIgnoreCase(fielditem.getItemid()) )
				{
					continue;						
				}	
				bReadOnly=true;
				desc=this.S0402Desc;
			}
			else if ("S05".equalsIgnoreCase(fieldSet)){
				if 	("nbase".equalsIgnoreCase(fielditem.getItemid()) )
				{
					if (fielditem.isVisible())
					{
						Field field = new Field("dbname",desc);
						field.setCodesetid("0");	
						field.setDatatype(DataType.STRING);
						field.setLength(50);
						field.setAlign("left");
						field.setVisible(true);
						field.setReadonly(true);	
						field.setSortable(false);
						list.add(field);	
						
						Field field1 = new Field("Dbid",desc);
						field1.setCodesetid("0");	
						field1.setDatatype(DataType.INT);
						field1.setLength(50);
						field1.setAlign("left");
						field1.setVisible(false);
						field1.setReadonly(true);	
						field1.setSortable(false);
						list.add(field1);	
						
						
						Field field3 = new Field("Nbase",desc);
						field3.setCodesetid("0");	
						field3.setDatatype(DataType.STRING);
						field3.setLength(50);
						field3.setAlign("left");
						field3.setVisible(false);
						field3.setReadonly(true);	
						field3.setSortable(false);
						list.add(field3);	
						continue;					
					}
				}

				if(ReadStr.indexOf(fielditem.getItemid().toUpperCase())>-1)
				{
					bReadOnly=true;	
				} 			
				
			}

			desc="&nbsp;&nbsp;&nbsp;"+desc+"&nbsp;&nbsp;&nbsp;";
			Field field = new Field(fielditem.getItemid(),desc);
			field.setCodesetid(fielditem.getCodesetid());
			if("N".equalsIgnoreCase(fielditem.getItemtype()))
			{
				field.setLength(fielditem.getItemlength());
				field.setDecimalDigits(fielditem.getDecimalwidth());
				if(fielditem.getDecimalwidth()==0){
					field.setDatatype(DataType.INT);
					field.setFormat("####");
				}else{
					field.setDatatype(DataType.FLOAT);
					field.setFormat("####."+format.toString().substring(0,fielditem.getDecimalwidth()));
				}
				field.setAlign("right");
			}else if("D".equalsIgnoreCase(fielditem.getItemtype())){
				field.setLength(20);
				field.setDatatype(DataType.DATE);
				field.setFormat("yyyy.MM.dd");
				field.setAlign("right");	
			}else if("M".equalsIgnoreCase(fielditem.getItemtype())){
				field.setDatatype(DataType.CLOB);
				field.setAlign("left");		
			}else if("A".equalsIgnoreCase(fielditem.getItemtype())){
				field.setDatatype(DataType.STRING);
				if(fielditem.getCodesetid()==null|| "0".equals(fielditem.getCodesetid())|| "".equals(fielditem.getCodesetid()))
					field.setLength(fielditem.getItemlength());						
				else
					field.setLength(50);
				field.setAlign("left");
			}else{
				field.setDatatype(DataType.STRING);
				field.setLength(fielditem.getItemlength());
				field.setAlign("left");			
			}
			field.setVisible(fielditem.isVisible());
			if ("S0100".equalsIgnoreCase(fielditem.getItemid())
					   || "I9999".equalsIgnoreCase(fielditem.getItemid()))
			{
				field.setVisible(false);	
			}	
			
			field.setReadonly(bReadOnly||!bCanEdit);	
			field.setSortable(false);
			list.add(field);	
	   }

	}	
	
	public void initS0402Desc(){	
		ArrayList fieldItemList = DataDictionary.getFieldList("S04",Constant.USED_FIELD_SET);
		for(int i=0;i<fieldItemList.size();i++)		{  
			FieldItem  fielditem = (FieldItem)fieldItemList.get(i);
			if ("S0402".equalsIgnoreCase(fielditem.getItemid()))
			{
				this.S0402Desc=fielditem.getItemdesc();
			}
			if( "S0401".equalsIgnoreCase(fielditem.getItemid()))
			{
				this.S0401Desc=fielditem.getItemdesc(); 	
			 }						

		}	
	}

	private String getNbases()
	{   String s="";
		ArrayList db= this.userView.getPrivDbList();
		for (int i=0;i<db.size();i++)
		{
			if ("".equals(s)){
				s= (String)db.get(i);
			}
			else {
				s= s+"`"+(String)db.get(i);	
			}
			
		}
	  if ("".equals(s)){
		  s="Usr";
	  }	
	  return s;	
	}

}
