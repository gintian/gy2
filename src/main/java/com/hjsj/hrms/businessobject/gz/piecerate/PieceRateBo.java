package com.hjsj.hrms.businessobject.gz.piecerate;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

public class PieceRateBo {
	private Connection conn = null;
	private String S0100="";
	private UserView userView=null;
	private RecordVo TaskRecordVo=null;
	private ArrayList gzitemlist=null;
	private String TabS05="S05";
	private String Peopletabname="";
	private ArrayList setlist=null;	
	
	public PieceRateBo(Connection _con,String s0100,UserView _userView)
	{
		conn=_con;
		S0100=s0100;
		userView=_userView;
		init();		
	}
	
	private void init()
	{
	   if(this.S0100!=null&&this.S0100.length()>0)
		   TaskRecordVo=getRecordVo("S01","s0100",Integer.parseInt(S0100)); 
	}
	
	
	private int getDataType(String type)
	{
		int datatype=0;
		switch(type.charAt(0))
		{
		case 'A':  
			datatype=YksjParser.STRVALUE;
			break;
		case 'D':
			datatype=YksjParser.DATEVALUE;
			break;
		case 'N':
			datatype=YksjParser.FLOAT;
			break;
		}
		return datatype;
	}
	
	private RecordVo getRecordVo(String tabname,String primary_key,int key) 
	{
		RecordVo vo=new RecordVo(tabname); 
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			vo.setInt(primary_key, key);
			vo=dao.findByPrimaryKey(vo);
		}
		catch(Exception e)
		{
			//e.printStackTrace();			 
		}
		return vo;
	}
	
	public String getBusiId() 
	{   String s0102="";
		try{
			s0102=this.TaskRecordVo.getString("s0102");
			}
		catch (Exception e)
		{		
			e.printStackTrace();		
		}
		return s0102;	
		
	}
	
	public String getInfostr() 
	{   String infostr="";
		try{
			String s0108=this.TaskRecordVo.getString("s0108");
			String sp_flag=this.TaskRecordVo.getString("sp_flag");
			if ((sp_flag==null)||("".equals(sp_flag))) {sp_flag="01";}
			CodeItem item=AdminCode.getCode("23", sp_flag);	
			sp_flag=item.getCodename();			
			infostr="作业单："+s0108 +"      "   +"状态："+sp_flag;
			}
		catch (Exception e)
		{		
			e.printStackTrace();		
		}
		return infostr;	
		
	}
	public boolean CanEdit() 
	{   boolean b=false;
		try{
			String sp_flag=this.TaskRecordVo.getString("sp_flag");
			if ((sp_flag==null)||("".equals(sp_flag))) {sp_flag="01";}
            if (("01".equals(sp_flag))||("07".equals(sp_flag)))//起草、驳回
            {            	
               b=true;	
            }
            	
			}
		catch (Exception e)
		{		
			e.printStackTrace();		
		}
		return b;	
		
	}
	
	private boolean IsHaveThisProduct(String productid,ContentDAO dao) 
	{   boolean b=false;
		try{
			String strsql="select * from s04 where s0100=" + this.S0100
			             +" and S0402 =" + productid;
			RowSet rset=dao.search(strsql);
			if (rset.next())
			{
			 b= true;            	
			}
		}
		catch (Exception e)
		{		
			e.printStackTrace();		
		}
		return b;	
		
	}
	
	
	public boolean HandAddProduct(String productlist) 
	{   boolean b=false;
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			String sp_flag=this.TaskRecordVo.getString("sp_flag");
			if ((sp_flag==null)||("".equals(sp_flag))) {sp_flag="01";}
            if (!(("01".equals(sp_flag))||("07".equals(sp_flag))))//起草、驳回
            {            	
              return b ;
            }
			String[] strlist=productlist.split("/");  
			for (int i= strlist.length-1;i>=0;i--){
				String ProductId  = strlist[i];
				if ("/".equals(ProductId)|| "".equals(ProductId)){continue;}
				if (!IsHaveThisProduct(ProductId,dao)){
					String sql="insert into S04(S0100,I9999,S0401,S0402)"
                           +" select "+this.S0100+" as S0100,(B.I9999+1) as I9999,S03.S0311 as S0401,S03.S0300 as S0402 from S03,"
                           +"(select "+Sql_switcher.sqlToInt(Sql_switcher.sqlNull("MAX(I9999)", 0))+" as I9999  from s04 where S0100="+this.S0100+") B"
                           +"  where s03.S0300="+ProductId ;
					
					dao.update(sql);					
				}		

			}

		}
		catch (Exception e)
		{		
			e.printStackTrace();		
		}
		return b;			
	}
	
	private boolean IsHaveThisPeople(String nbase,String A0100,ContentDAO dao) 
	{   boolean b=false;
		try{
			String strsql="select * from s05 where s0100=" + this.S0100
			             +" and nbase ='" + nbase+"' and A0100='"+A0100
			             +"'";
			RowSet rset=dao.search(strsql);
			if (rset.next())
			{
			 b= true;            	
			}
		}
		catch (Exception e)
		{		
			e.printStackTrace();		
		}
		return b;	
		
	}
	
	private ArrayList GetItemList()
	{
		String Itemid="";
		ArrayList rlist =new ArrayList();

	    ArrayList list = DataDictionary.getFieldList("s05",Constant.USED_FIELD_SET);
	    for (int i=0;i<list.size();i++)
	    {
	    	FieldItem fielditem = (FieldItem) list.get(i);
	    	Itemid= fielditem.getItemid();
	    	String excludeStr=",Nbase,A0100,I9999,S0100,B0110,E01A1,E0122".toUpperCase();
	    	if ("0".equals(fielditem.getState())) continue;
			if (excludeStr.indexOf(","+fielditem.getItemid().toUpperCase()+",")>-1) {continue;}
	    	FieldItem fld =DataDictionary.getFieldItem(Itemid);
	    	if (!"s05".equalsIgnoreCase(fld.getFieldsetid())&& (!"0".equals(fld.getUseflag()))) {
	    		rlist.add(fld);		    		
	    	}   	
	    }	
	    return rlist;
	}
	
	public void createTmpTable()
	{
		try		
		{
			DbWizard dbw = new DbWizard(this.conn);
			this.Peopletabname = "t#"+this.userView.getUserName()+"_piece_1";
			if (dbw.isExistTable(this.Peopletabname)) 
			  dbw.dropTable(this.Peopletabname);
			Table table=new Table(this.Peopletabname);		
			
			Field field=new Field("NBASE","NBASE");
			field.setDatatype(DataType.STRING); 
			field.setLength(10);
			field.setKeyable(true);
			field.setNullable(false);
			table.addField(field);
			
			field=new Field("A0100","A0100");
			field.setDatatype(DataType.STRING); 
			field.setKeyable(true);
			field.setLength(30);
			field.setNullable(false);
			table.addField(field);				
			
			dbw.createTable(table);		
		}
		catch(Exception e)
		{
		//	e.printStackTrace(); 
		}
	}
	
	public boolean HandAddPeople(String peoplelist) 
	{   boolean b=false;
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			String sp_flag=this.TaskRecordVo.getString("sp_flag");
			if ((sp_flag==null)||("".equals(sp_flag))) {sp_flag="01";}
            if (!(("01".equals(sp_flag))||("07".equals(sp_flag))))//起草、驳回
            {            	
              return b ;
            }
            this.gzitemlist=GetItemList();
            createTmpTable();
            String dblist="`";
            String strSql="";
			String[] strlist=peoplelist.split("/");  			
			for (int i= strlist.length-1;i>=0;i--){
				String str  = strlist[i];
				if ("/".equals(str)|| "".equals(str)){continue;}
				int j= str.indexOf(".");
				String Nbase=str.substring(0,j).toUpperCase();				
				String A0100=str.substring(j+1);
				if (dblist.toUpperCase().indexOf("'"+Nbase.toUpperCase()+"'")==-1)
				{
					dblist=dblist+	Nbase+"`";
					
				}				

				strSql="insert into "+this.Peopletabname+"(NBASE,A0100) values ("
		           + "'"+Nbase+"','"+A0100+"')";			                           
	    		dao.update(strSql);						
	
			}
			AddPeople(dblist,dao);	
		}
		catch (Exception e)
		{		
			e.printStackTrace();		
		}
		return b;			
	}
	
	public boolean CondAddPeople(String dblist,String strwhere) 
	{   boolean b=false;        
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			String sp_flag=this.TaskRecordVo.getString("sp_flag");
			if ((sp_flag==null)||("".equals(sp_flag))) {sp_flag="01";}
            if (!(("01".equals(sp_flag))||("07".equals(sp_flag))))//起草、驳回
            {            	
              return b ;
            }
            this.gzitemlist=GetItemList();
            createTmpTable();
			String strSql="";
			RowSet rset=null;
			String Nbase="";
			String A0100="";
			
			
			DbWizard dbWizard = new DbWizard(this.conn);
			dblist=dblist.toUpperCase();
			int k=strwhere.indexOf("|");
			String sexpr= strwhere.substring(0,k);
			strwhere= strwhere.substring(k+1,strwhere.length());			
			k=strwhere.indexOf("|");
			String sfactor= strwhere.substring(0,k);
			String slike= strwhere.substring(k+1,strwhere.length());
			
//			String[] arrstr=strwhere.split("|");  不起作用
			String strmanger=getPrivB0110();
			
			String[] strlist=dblist.split("`");  
			try{
				for (int i=0;i< strlist.length;i++){
					Nbase  = strlist[i];
					if ("/".equals(Nbase)|| "".equals(Nbase)){continue;}
					String tabname="t#_"+this.userView.getUserName()+"_piece_2";
	                String strfrom="";
			    	try{
				    	 	FactorList factorslist=new FactorList(sexpr,PubFunc.getStr(sfactor),Nbase,false, "1".equals(slike),true,1,this.userView.getUserId());
				    	 	factorslist.setSuper_admin(this.userView.isSuper_admin());
				    	 	strfrom= factorslist.getSqlExpression();
				    	 	if (!"".equals(strmanger)){
				    	 		strfrom=strfrom+" and ("+strmanger+")";
				    	 	}
							if(dbWizard.isExistTable(tabname,false))
							{
								strSql="delete from "+ tabname;
								dao.update(strSql);
								strSql="insert into "+tabname +" (B0110,A0100) select B0110,A0100 "+ strfrom;
								dao.update(strSql);							
								
							}
							else
							{
							    if (1==dbWizard.dbflag){							        
							        strSql="select B0110,A0100 into "+tabname +" "+ strfrom;
							    }
							    else {							        
	                                 strSql="Create Table "+ tabname+ " as  select B0110,A0100 "+" "+ strfrom;    
							    }

								dao.update(strSql);			
								
							}
			    	 }catch(Exception e){
			    		 throw GeneralExceptionHandler.Handle(e);
			    	 }
			    	 
		    	 
					if(dbWizard.isExistTable(tabname,false))
					{
						strSql="DELETE FROM "+ tabname + " WHERE A0100 in (select  A0100 from "
                                   + " S05 where  Nbase ='"+ Nbase+"' and S0100="+this.S0100+")";
						dao.update(strSql);
						strSql="select * from " + tabname;
					    rset = dao.search(strSql);
					    while (rset.next())
					    {
					    	A0100=rset.getString("A0100");
					    	strSql="insert into "+this.Peopletabname+"(NBASE,A0100) values ("
							           + "'"+Nbase+"','"+A0100+"')";			                           
							dao.update(strSql);					

					    }					
					}							    
				}
				AddPeople(dblist,dao);	
			 }
			catch(Exception e){
			}


		}
		catch (Exception e)
		{		
			e.printStackTrace();		
		}
		return b;			
	}
	
	private void AddPeople(String dblist,ContentDAO dao)
	{
		try{
			String A01Flds=GetA01Items();
			String strSql="select * from " + this.Peopletabname;
			RowSet rset = dao.search(strSql);
		    while (rset.next())
		    {
		    	String A0100=rset.getString("A0100");
		    	String Nbase=rset.getString("NBASE");
				if (!IsHaveThisPeople(Nbase,A0100,dao)){
					strSql="insert into S05(NBASE,A0100,I9999 "+A01Flds+"S0100)"
	                       +" select '"+Nbase+"' as NBase,A0100,(B.I9999+1) as I9999 "+A01Flds+this.S0100+" as S0100 from "+Nbase+"A01,"
	                       +"(select "+Sql_switcher.sqlToInt(Sql_switcher.sqlNull("MAX(I9999)", 0))+" as I9999 from s05 "
	                       + "where Nbase='"+Nbase+"' and A0100 ='"+ A0100+"') B"
	                       +"  where A0100='"+A0100 +"'";
					dao.update(strSql);	
				}	
		    }
		    
		    importAddManData(dblist);   
		    
		}
		catch (Exception e)
		{		
			e.printStackTrace();		
		}
	}
	
	private String GetA01Items()
	{
		String strItems=",B0110,E0122,E01A1,";
		for(int i=0;i<gzitemlist.size();i++)
		{
			FieldItem itemvo=(FieldItem)gzitemlist.get(i);
			if(!("A01".equalsIgnoreCase(itemvo.getFieldsetid()))) continue;
			String strfld=itemvo.getItemid().toUpperCase();
			if (strItems.indexOf(","+strfld+",")==-1 )
			{
				strItems=strItems+strfld+",";
			}
		}
		return strItems;
	}
	
	private String getUpdateFieldSQL(String strSrc,String strDest,String setid)
	{
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<gzitemlist.size();i++)
		{
			FieldItem itemvo=(FieldItem)gzitemlist.get(i);
			if(!(setid.equalsIgnoreCase(itemvo.getFieldsetid()))) continue;
			String strfld=itemvo.getItemid();

			buf.append(strDest);
			buf.append(".");
			buf.append(strfld);
			buf.append("=");
			buf.append(strSrc);
			buf.append(".");					
			buf.append(strfld);
			buf.append("`");

		}
		if(buf.length()>0)
			buf.setLength(buf.length()-1);
		return buf.toString();
	}
	
	private ArrayList GetSetList()
	{
		  ArrayList list=new ArrayList();
		  StringBuffer buf=new StringBuffer();
		  buf.append("select fieldsetid from fielditem where itemid in( "); 
		  buf.append("select itemid from t_hr_busifield where Fieldsetid ='S05')");
		  buf.append(" group by fieldsetid order by fieldsetid");
		  RowSet rset=null;
		  try
		  {
			  ContentDAO dao=new ContentDAO(this.conn);
			  rset=dao.search(buf.toString());
			  while(rset.next())
			  {
				  list.add(rset.getString("fieldsetid").toUpperCase());
			  }
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
		  }		  
		  finally
		  {
			try
			{
				if(rset!=null)
					rset.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		  }			  
		  return list;
	}

	
	private void importAddManData(String dblist)throws GeneralException
	{		
		try
		{		
			String Itemid="";
			String strpre="";
			String strset=null;
			String strc=null;
			StringBuffer buf=new StringBuffer();
			StringBuffer strWhere=new StringBuffer();
			
			ContentDAO dao=new ContentDAO(this.conn);
			DbWizard dbw=new DbWizard(this.conn);
		    this.setlist =GetSetList();	

			String[] strlist=dblist.split("`");  
			for (int i=0;i< strlist.length;i++){
				strpre  = strlist[i];
				if ("/".equals(strpre)|| "".equals(strpre)){continue;}
				
				buf.setLength(0);
				for(int j=0;j<setlist.size();j++)
				{
					strset=(String)setlist.get(j);
					if ("A01".equalsIgnoreCase(strset)){continue;}
					strc=strpre+strset;
					if(strset.charAt(0)=='A')
					{			
						RowSet rowSet=dao.search("select count(nbase) from "+ this.Peopletabname+" where upper(nbase)='"+strpre.toUpperCase()+"'");
						if(rowSet.next())
						{
							if(rowSet.getInt(1)>0)
							{
								
							    String strupdate=getUpdateFieldSQL(strc, "S05", strset);
								
								if(strupdate.length()==0) continue;
								String temp1="(select * from "+strc+" a where a.i9999=(select max(b.i9999) from "+strc+" b where a.a0100=b.a0100  ) ) "+strc;
									
								strWhere.setLength(0);
								strWhere.append(" exists (select 1 from ");						
								strWhere.append(this.Peopletabname);
								strWhere.append(" where S05.a0100=");
								strWhere.append(this.Peopletabname);
								strWhere.append(".a0100  and upper(");
								strWhere.append(this.Peopletabname);
								strWhere.append(".nbase) = upper(");
								strWhere.append("S05");
								strWhere.append(".nbase)");
								strWhere.append(" and upper(S05.nbase)='");
								strWhere.append(strpre.toUpperCase());
								strWhere.append("'");
								strWhere.append(" and  S05.S0100="+this.S0100);
								strWhere.append(")");
								
								dbw.updateRecord("S05",temp1,"S05.A0100="+strc+".A0100", 
										   strupdate, strWhere.toString(),"");			
								
							}
						}

					
					}

				}


			}

			
	


			RowSet rowSet=dao.search("select count(nbase) from "+ this.Peopletabname);
			if(rowSet.next())
			{
				if(rowSet.getInt(1)>0)
				{
					try
					{
						FieldItem Field=null;
						for(int i=0;i<gzitemlist.size();i++)
						{
							FieldItem itemvo=(FieldItem)gzitemlist.get(i);
							String setid=itemvo.getFieldsetid();

							/**单位指标或职位指标*/
							if((setid.charAt(0)!='A')&&(!"S05".equalsIgnoreCase(setid)))
							{
								computingImportUnitItem(itemvo,strWhere.toString(),"",true);
							}
							
						}
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
						throw GeneralExceptionHandler.Handle(ex);
					}	
					
				}

			}
			rowSet.close();			

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		
	}
	
	
	private boolean createMidTable(ArrayList fieldlist,String tablename,String keyfield)
	{
		boolean bflag=true;
		try
		{
			DbWizard dbw=new DbWizard(this.conn);
			if(dbw.isExistTable(tablename, false))
				dbw.dropTable(tablename);
			Table table=new Table(tablename);
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem fielditem=(FieldItem)fieldlist.get(i);
				Field field=fielditem.cloneField();
				if(field.getName().equalsIgnoreCase(keyfield))
				{
					field.setNullable(false);
					field.setKeyable(true);
				}
				table.addField(field);
			}//for i loop end.
			Field field=new Field("userflag","userflag");
			field.setLength(50);
			field.setDatatype(DataType.STRING);
			table.addField(field);
			dbw.createTable(table);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
		}
		return bflag;
	}
	
	private boolean computingImportUnitItem(FieldItem itemvo,String strWhere,String strPre,boolean isE01A1)throws GeneralException
	{
		boolean bflag=true;
		YksjParser yp =null;
		try
		{			
			String setid=itemvo.getFieldsetid();
			/**单位指标或职位指标*/
			if(setid.charAt(0)=='K'&&!isE01A1)
				return bflag;
			StringBuffer buf=new StringBuffer();
			DbWizard dbw=new DbWizard(this.conn);
			ContentDAO dao=new ContentDAO(this.conn);
			String formulaTabName="t#"+this.userView.getUserName()+"_gz"; //this.userview.getUserName()+"midtable";
			String fldname=itemvo.getItemid();
			String fldtype=itemvo.getItemtype();

			/**公式计算*/
			ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
					Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);

			if(setid.charAt(0)=='K')
				yp=new YksjParser(this.userView, allUsedFields,
						YksjParser.forSearch, getDataType(fldtype), YksjParser.forPosition, "Ht", strPre);
			else
				yp=new YksjParser(this.userView, allUsedFields,
					YksjParser.forSearch, getDataType(fldtype), YksjParser.forUnit, "Ht", strPre);
			yp.setCon(this.conn);
			ArrayList fieldlist=null;
			try
			{
				fieldlist=yp.getFormulaFieldList(fldname);
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
				return bflag;
			}
			
			/**增加一个计算公式用的临时字段*/
			ArrayList usedlist=new ArrayList();
			FieldItem fielditem=new FieldItem("A01","AAAAA");
			fielditem.setItemdesc("AAAAA");
			fielditem.setCodesetid("0");
			fielditem.setItemtype(fldtype);
			fielditem.setItemlength(itemvo.getItemlength());
			fielditem.setDecimalwidth(itemvo.getDecimalwidth()); 
			yp.setTargetFieldDecimal(fielditem.getDecimalwidth());     
			usedlist.add(fielditem);
			/**创建计算用临时表*/
			if(setid.charAt(0)=='K')
			{
				fielditem=new FieldItem("A01","E01A1");
				fielditem.setItemdesc("职位名称");
				fielditem.setCodesetid("@K");
				fielditem.setItemtype("A");
				fielditem.setItemlength(30);
				fielditem.setDecimalwidth(0);
				usedlist.add(fielditem);
				if(createMidTable(usedlist,formulaTabName,"E01A1"))
				{
					/**导入单位主集数据B0110*/
					buf.setLength(0);
					buf.append("insert into ");
					buf.append(formulaTabName);
					buf.append("(E01A1) select E01A1 FROM K01");
					dao.update(buf.toString());
				}// 创建临时表结束.
			}
			else
			{
				fielditem=new FieldItem("A01","B0110");
				fielditem.setItemdesc("单位名称");
				fielditem.setCodesetid("UN");
				fielditem.setItemtype("A");
				fielditem.setItemlength(30);
				fielditem.setDecimalwidth(0);
				usedlist.add(fielditem);
				if(createMidTable(usedlist,formulaTabName,"B0110"))
				{
					/**导入单位主集数据B0110*/
					buf.setLength(0);
					buf.append("insert into ");
					buf.append(formulaTabName);
					buf.append("(B0110) select B0110 FROM B01");
					dao.update(buf.toString());
				}// 创建临时表结束.
			}
			
			yp.run(fldname,null,"AAAAA",formulaTabName,dao,"",this.conn,fldtype,fielditem.getItemlength(),1,itemvo.getCodesetid());
			if(setid.charAt(0)=='K')
			{
				StringBuffer sql=new StringBuffer("update "+TabS05+" set "+fldname+"=(select  "+formulaTabName+".AAAAA from "+formulaTabName);
				sql.append(" where "+TabS05+".E01A1="+formulaTabName+".E01A1 ) where exists (select null from "+formulaTabName);
				sql.append(" where "+TabS05+".E01A1="+formulaTabName+".E01A1 ) ");
				if(strWhere!=null&&strWhere.length()>0)
					sql.append(" and "+strWhere);
				dbw.execute(sql.toString());
			}
			else
			{
				StringBuffer sql=new StringBuffer("update "+TabS05+" set "+fldname+"=NULL");
				if(strWhere!=null&&strWhere.length()>0)
					sql.append(" where "+strWhere);
				dbw.execute(sql.toString());
				// 部门
				String cond=null;
				if("N".equalsIgnoreCase(fldtype))
					cond="AAAAA<>0";
				else if("D".equalsIgnoreCase(fldtype))
					cond="NOT AAAAA IS NULL";
				else
				{
					if(Sql_switcher.searchDbServer() < Constant.ORACEL)
						cond="AAAAA<>''";
					else
						cond="NOT AAAAA IS NULL";
				}
				sql.setLength(0);
				sql.append("update "+TabS05+" set "+fldname+"="+
						     "(select  "+formulaTabName+".AAAAA from "+formulaTabName+
				             " where "+TabS05+".E0122="+formulaTabName+".B0110 and "+cond+")"+
				           " where exists (select null from "+formulaTabName+
				                          " where "+TabS05+".E0122="+formulaTabName+".B0110)");
				if(strWhere!=null&&strWhere.length()>0)
					sql.append(" and "+strWhere);
				dbw.execute(sql.toString());

				// 单位
				cond=TabS05+"."+fldname+" IS NULL";
				sql.setLength(0);
				sql.append("update "+TabS05+" set "+fldname+"="+
						        "(select  "+formulaTabName+".AAAAA from "+formulaTabName+
				                " where "+TabS05+".B0110="+formulaTabName+".B0110)"+
				           " where "+cond+" and exists (select null from "+formulaTabName+
				                          " where "+TabS05+".B0110="+formulaTabName+".B0110)");
				if(strWhere!=null&&strWhere.length()>0)
					sql.append(" and "+strWhere);
				dbw.execute(sql.toString());			
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);
		}finally{ 
			yp=null;
		} 
		return bflag;		
	}
	
	
	
	public boolean DelPeople(String peoplelist) 
	{   boolean b=false;
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			String sp_flag=this.TaskRecordVo.getString("sp_flag");
			if ((sp_flag==null)||("".equals(sp_flag))) {sp_flag="01";}
            if (!(("01".equals(sp_flag))||("07".equals(sp_flag))))//起草、驳回
            {            	
              return b ;
            }
			String strSql="";
			String[] strlist=peoplelist.split("/");  
			for (int i=0;i< strlist.length;i++){
				String peopleinfo = strlist[i];
				if ("/".equals(peopleinfo)|| "".equals(peopleinfo)){continue;}

				try{
					int j= peopleinfo.indexOf("`");
					if (j<0){continue;}
					String[] peopleinfolist=peopleinfo.split("`");  	
					String Nbase=peopleinfolist[0];
					String A0100=peopleinfolist[1];
					String I9999=peopleinfolist[2];

					strSql="DELETE FROM S05 WHERE Nbase ='"+ Nbase+"' " 
							+" and  A0100 ='"+ A0100+"' " 
							+" and I9999="+I9999
							+ " and S0100="+this.S0100;
					dao.update(strSql);
					
				}catch(Exception e){
					e.printStackTrace();
				}
			}

		}
		catch (Exception e)
		{		
			e.printStackTrace();		
		}
		return b;			
	}
	public boolean DelProduct(String productlist) 
	{   boolean b=false;
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			String sp_flag=this.TaskRecordVo.getString("sp_flag");
			if ((sp_flag==null)||("".equals(sp_flag))) {sp_flag="01";}
            if (!(("01".equals(sp_flag))||("07".equals(sp_flag))))//起草、驳回
            {            	
              return b ;
            }
			String strSql="";
			String[] strlist=productlist.split("/");  
			for (int i=0;i< strlist.length;i++){
				String I9999 = strlist[i];
				if ("/".equals(I9999)|| "".equals(I9999)){continue;}

				try{
					strSql="DELETE FROM S04 WHERE  S0100="+this.S0100
							+" and I9999="+I9999;
					dao.update(strSql);
					
				}catch(Exception e){
					e.printStackTrace();		
				}
			}

		}
		catch (Exception e)
		{		
			e.printStackTrace();		
		}
		return b;			
	}
	
	public String getPrivPre()
	{		
		StringBuffer pre=new StringBuffer("");
		try
		{
			ArrayList list = this.userView.getPrivDbList();		
			if(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId()))
			{
				pre.append(" 1=1" );
			}
			else if(list==null||list.size()<=0)
			{
				pre.append(" 1=2");
			}
			else
			{
				String b_units=this.userView.getUnitIdByBusi("1");//薪资操作单位
				if(b_units!=null&&b_units.length()>0&&!"UN".equalsIgnoreCase(b_units)) //模块操作单位
				{
					String unitarr[] =b_units.split("`");
    				for(int i=0;i<unitarr.length;i++)
    				{
	    				String codeid=unitarr[i];
	    				if(codeid==null|| "".equals(codeid))
	    					continue;
		    			if(codeid!=null&&codeid.trim().length()>2)
	    				{
		    				if("UN".equalsIgnoreCase(codeid.substring(0,2)))
		    				{
	                 		   pre.append(" or b0110 like '"+codeid.substring(2)+"%' ");
		    				}
		    				else if("UM".equalsIgnoreCase(codeid.substring(0,2)))
		    				{
		    					pre.append(" or e0122 like '"+codeid.substring(2)+"%'");
		    				}
	                 	}
		    			else if(codeid!=null&& "UN".equalsIgnoreCase(codeid))
		    			{
		    				pre.append(" or 1=1 ");
	                 	}	
		    		}    				
		    		if(pre.toString().length()>0)
	    			{
	     				String str="(" +pre.toString().substring(3)+")";
	    				pre.setLength(0);
	    				pre.append(str);
	    			}
		    		String nbase="";
		    		for (int i=0;i<list.size();i++){
		    			if ("".equals(nbase)){
		    				nbase="'"+((String)list.get(i)).toUpperCase()+"'";
		    			}
		    			else{
		    				nbase=nbase+","+"'"+((String)list.get(i)).toUpperCase()+"'";	
		    				
		    			}	    				
		    		}
		    		nbase=" Nbase in ("+nbase+")";
		    		pre.append(" and "+nbase );
				}				

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		String s=pre.toString();
		return s;
	}
	public String getPrivB0110()
	{		
		StringBuffer pre=new StringBuffer("");
		try
		{
			if(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId()))
			{
				pre.append(" 1=1" );
			}
			else
			{
				String b_units=this.userView.getUnitIdByBusi("1");//薪资操作单位
				if(b_units!=null&&b_units.length()>0&&!"UN".equalsIgnoreCase(b_units)) //模块操作单位
				{
					String unitarr[] =b_units.split("`");
    				for(int i=0;i<unitarr.length;i++)
    				{
	    				String codeid=unitarr[i];
	    				if(codeid==null|| "".equals(codeid))
	    					continue;
		    			if(codeid!=null&&codeid.trim().length()>2)
	    				{
		    				if("UN".equalsIgnoreCase(codeid.substring(0,2)))
		    				{
	                 		   pre.append(" or b0110 like '"+codeid.substring(2)+"%' ");
		    				}
		    				else if("UM".equalsIgnoreCase(codeid.substring(0,2)))
		    				{
		    					pre.append(" or e0122 like '"+codeid.substring(2)+"%'");
		    				}
	                 	}
		    			else if(codeid!=null&& "UN".equalsIgnoreCase(codeid))
		    			{
		    				pre.append(" or 1=1 ");
	                 	}	
		    		}    				
		    		if(pre.toString().length()>0)
	    			{
	     				String str="(" +pre.toString().substring(3)+")";
	    				pre.setLength(0);
	    				pre.append(str);
	    			}

				}				

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		String s=pre.toString();
		return s;
	}
	//返回格式:UM010101`UN0102
	public String getMangerPriv()
	{		
		String s="";
		try
		{
			if(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId()))
			{
				s="";
			}
			else
			{
				String b_units=this.userView.getUnitIdByBusi("1");//薪资操作单位
				if(b_units!=null&&b_units.length()>0&&!"UN".equalsIgnoreCase(b_units)) //模块操作单位
				{
					String unitarr[] =b_units.split("`");
    				for(int i=0;i<unitarr.length;i++)
    				{
	    				String codeid=unitarr[i];
	    				if(codeid==null|| "".equals(codeid))	continue;
		    			if(codeid!=null&&codeid.trim().length()>2)
	    				{
		    				if ("".equals(s)){
		    					s=codeid;
		    				}
		    				else{
		    					s=s+'`'+codeid;			    					
		    				} 
	                 	}
		    		}  				
				}				

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return s;
	}
	
	//如果当前操作单位就一个 且是部门 则返回此部门 返回 :部门编码`部门名称
	public String getE0122Value()
	{		
		String s="";
		try
		{
			if(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId()))
			{
				s="";
			}
			else
			{
				String b_units=this.userView.getUnitIdByBusi("1");//薪资操作单位
				if(b_units!=null&&b_units.length()>0&&!"UN".equalsIgnoreCase(b_units)) //模块操作单位
				{
					String unitarr[] =b_units.split("`");
					for(int i=0;i<unitarr.length;i++)
					{
						String codeid=unitarr[i];
						if(codeid==null|| "".equals(codeid))	continue;
						if(codeid!=null&&codeid.trim().length()>2)
						{
							if ("".equals(s)){
								if ("UM".equalsIgnoreCase(codeid.substring(0, 2))){
									s=codeid.substring(2);
									
								}
								else{
									break; 
								}
								
							}
							else{
								s="";
								break;    					
							} 
						}
					}  				
				}				
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return s;
	}
	
	
}
