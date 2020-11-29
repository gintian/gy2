package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.edit_report.EditReport;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ImportTemplateU02Trans extends IBusiness {

	public void execute() throws GeneralException {
		Workbook wb = null;
		try
		{
		Sheet  sheet = null;
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String addother =(String)this.getFormHM().get("addother");
		String updatehistory = (String)hm.get("updatehistory");
		hm.remove("updatehistory");
		String idstr="";//表示生成新的原精确报表id
		String idstr2="";
		String idstr3="";
		String report_id=(String)hm.get("report_id");
		this.getFormHM().put("report_id", report_id);
		String escope="";	
		escope=report_id.split("_")[1];
		String reportname ="";
		if("1".equals(escope))
			reportname="离休人员";
		else if("2".equals(escope))
			reportname="退休人员";
		else if("3".equals(escope))
			reportname="离休人员";
		else if("4".equals(escope))
			reportname="遗嘱";
		EditReport editReport=new EditReport();
    	ArrayList fieldlist=editReport.getU02FieldList(this.getFrameconn(),report_id,false);

    	LazyDynaBean updownRuleBeans=editReport.getUpdownRuleBeans(this.getFrameconn(),fieldlist,report_id);
    	
		String unitcode=(String)this.getFormHM().get("unitcode");
    	String id=(String)this.getFormHM().get("id");
    	String stateflag = (String)this.getFormHM().get("flag");
    	
    	RecordVo cycle_vo = new RecordVo("tt_cycle");
    	cycle_vo.setInt("id", Integer.parseInt(id));
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		cycle_vo = dao.findByPrimaryKey(cycle_vo);
		HashMap isexistMap = new HashMap();	//判断excel中姓名和身份证
		HashMap isexisU0201U0203tMap = new HashMap();	//判断库中姓名和身份证
		HashMap isdoubleU0200Map =  new HashMap();
    	HashMap allCollectUnitMap=getCollectUntiMap();
    	HashMap allUnitMap=getCollectUntiMap2();
    	
    	String kmethod=(String)this.getFormHM().get("kmethod");
    	//初始化数据,引入旧的精确编号
    	boolean oldiditemflag =false;
    	int oldiditemid=-1;
    	String oldstr="";
    	
    	if(SystemConfig.getPropertyValue("oldiditem")!=null&&SystemConfig.getPropertyValue("oldiditem").startsWith("U02")){
    		oldiditemflag = true;
    		oldstr = SystemConfig.getPropertyValue("oldiditem");
    	}
//    	else{
//    		throw new GeneralException("请在相应的文件中配置oldiditem参数！");
//    	}
    	
    	String  _str="";
    	 if(this.getFormHM().get("cycleparm")!=null&&!"".equals(this.getFormHM().get("cycleparm")))
    	 {
    		 _str=oldstr;
    		 
    		 if(addother!=null&&!"1".equals(addother)) {
    			 String	delete_sql="delete from U02 where  escope="+escope+" and id="+id+"  ";
    			 dao.delete(delete_sql, new ArrayList());
    		 }
    		 
    		 
    	 }
    	 else
    		 _str="u0200";
    	 
    	String pre_id=getPre_cycleID(cycle_vo,dao); 
    	HashMap u0207Map=getU0207Map(pre_id,_str,dao,escope,"");
    	HashMap u0200Map=null;
    	 if(this.getFormHM().get("cycleparm")!=null&&!"".equals(this.getFormHM().get("cycleparm")))
    	 {
    	u0200Map=getU0200Map(pre_id,_str,dao,escope);
    	 }
    	HashMap _u0207Map=null;
    	if(addother!=null&& "1".equals(addother)) //表示追加判断是否存在)
    		_u0207Map=getU0207Map(id,_str,dao,escope,"");
    	HashMap ori_u0207Map=getU0207Map(id,"u0200",dao,escope,stateflag);
    
    	isexisU0201U0203tMap = getU0201U0203Map(id,dao,escope,stateflag,updatehistory);
    	
    	
    	
    	String U0207="";
    	String U0207hisid="";
		FormFile form_file = (FormFile) getFormHM().get("file");
		String filename=form_file.getFileName();
		int indexInt = filename.lastIndexOf(".");            
		String ext = filename.substring(indexInt + 1, filename.length());
		if(ext==null||ext.length()<=0||(!"xls".equals(ext)&&!"xlsx".equals(ext)))
			throw GeneralExceptionHandler.Handle(new GeneralException("","上传文件类型出错！","",""));
		InputStream stream = null;
		try
		{
			stream = form_file.getInputStream();
			wb =  WorkbookFactory.create(stream);
			sheet = wb.getSheetAt(0);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeIoResource(stream);
		}
		Row row = sheet.getRow(0);
		HashMap map = new HashMap();
		if (row == null)
		    throw new GeneralException("请用导出的模板Excel来导入数据！");
		Cell cell2 =row.getCell((short)0);
		if (cell2 == null)
		    throw new GeneralException("请用导出的模板Excel来导入数据！");
		//System.out.println("左上端单元是： " + cell2.getStringCellValue()); 
		int cols = row.getPhysicalNumberOfCells();
		int rowsd = sheet.getPhysicalNumberOfRows();
		int rows = sheet.getPhysicalNumberOfRows();
		int unitcodeid = -1;//代表部门或单位所在的列
		String unitcodes="";//存放单位id
		int u0239id = -1;//代表备注信息
		int u0207id=-1;
		int u0201id =-1;//身份证所在的列
		int u0203id = -1;//姓名
		int u0204id= -1;//出生日期
		StringBuffer codeBuf = new StringBuffer();		
		//ArrayList list=editReport.getU02FieldList(this.getFrameconn(),report_id);
		StringBuffer sql = new StringBuffer();
		sql.append("update U02 set ");
		StringBuffer insert_sql=new StringBuffer();
		StringBuffer insert_sql_temp=new StringBuffer();
		StringBuffer insert_value=new StringBuffer();		
		StringBuffer insert_value_temp=new StringBuffer();		
		insert_sql.append("insert into U02(");
		insert_sql_temp.append("insert into t#_u02(");
		insert_sql_temp.append("username"+",");
		insert_value_temp.append("?,");
		int factCols=0;
		boolean isU0207=false;
		String fields="";
		for(short i = 0; i < cols; i++)
		{
			
			Cell cell = row.getCell((short) (i));
			 if (cell != null)
			 {
				 String title = "";
				 switch (cell.getCellType())
				 {
				    case Cell.CELL_TYPE_FORMULA:
					  break;
				    case Cell.CELL_TYPE_NUMERIC:
					  double y = cell.getNumericCellValue();
					  title = Double.toString(y);
					  break;
				    case Cell.CELL_TYPE_STRING:
					  title = cell.getStringCellValue();
					  break;
				    default:
					  title = "";
				}
				//System.out.println(title);
				if ("".equals(title.trim()))
						throw new GeneralException("标题行存在空标题！请用导出的模板Excel来导入数据！");
				if(cell.getCellComment()==null)
					throw new GeneralException("标题行存在空批注！请用导出的模板Excel来导入数据！");
			     String field = cell.getCellComment().getString().toString();
			     field = field.trim();
			     fields+=field+",";
			    // System.out.println("field： " + field);
			     if("unitcode".equalsIgnoreCase(field)){
			    	 unitcodeid =i;
			    //	 sql.append("unitcode=?,");
			    	 continue;
			     }
			  
			     if("u0243".equalsIgnoreCase(field)){
			    	 continue;
			     }
			     if("u0201".equalsIgnoreCase(field)){
			    	 u0201id =i;
			     }
			     if("u0203".equalsIgnoreCase(field)){
			    	 u0203id =i;
			     }
			     if("u0239".equalsIgnoreCase(field)){
			    	 u0239id =i;
			     }
			     if("u0204".equalsIgnoreCase(field)){
			    	 u0204id =i;
			     }
			     if(this.getFormHM().get("cycleparm")!=null&&!"".equals(this.getFormHM().get("cycleparm"))){
			    	 if(field.equalsIgnoreCase(oldstr)){
				    	 oldiditemid = i;
				   	 continue;
				     } 
			     }else{
			    	 if(field.equalsIgnoreCase(oldstr)){
				    	 oldiditemid = i;
				    	// continue;
				     } 
			     }
			     
			     FieldItem fielditem= DataDictionary.getFieldItem(field);	
			     if(fielditem==null)
			    	 throw new GeneralException("指标不存在！请检查模板数据指标！！");
			     String codesetid = fielditem.getCodesetid(); 			    
			     if (!"0".equals(codesetid))
				 {
					if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equals(codesetid))
					{
					    codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='" + codesetid + "'  and codeitemid=childid  union all ");
					} else
					{
					    codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='" + codesetid
						    + "' and  codeitemid not in (select parentid from organization where codesetid='" + codesetid + "') union all ");
					}
				}
				map.put(new Short(i), field + ":" + cell.getStringCellValue());
				   if("u0207".equalsIgnoreCase(field)){
				    	 u0207id =i;
				    	 continue;
				     }
				if(!"u0200".equalsIgnoreCase(fielditem.getItemid()))
				{
					sql.append(field + "=?,");
					if("62".equals(codesetid))
				    {	 
				    	 isU0207=true;
				    	 
				    }else
				    {
				    	insert_sql.append(field+",");
						insert_value.append("?,");
						insert_sql_temp.append(field+",");
						insert_value_temp.append("?,");
				    }
				}
				factCols++;
				  
			 }
		}
		if(this.getFormHM().get("cycleparm")!=null&&!"".equals(this.getFormHM().get("cycleparm"))){
			if(fields.indexOf("unitcode")==-1)
				throw new GeneralException("标题行不存在unitcode批注！请设置单位或部门来导入数据！");
			if(fields.toUpperCase().indexOf(oldstr.toUpperCase())==-1)
				throw new GeneralException("标题行不存在原精确编号或者原精确编号批注不对！");
			
		}else{
			if(fields.indexOf("unitcode")!=-1)
				throw new GeneralException("标题行不需要unitcode批注！请用导出的模板Excel来导入数据！");
		}
		
		//System.out.println(codeBuf.toString());
		sql.setLength(sql.length() - 1);
			
			if(this.getFormHM().get("cycleparm")!=null&&!"".equals(this.getFormHM().get("cycleparm"))){
				insert_sql.append("editflag,escope,u0200,id,unitcode,u0207,"+oldstr+") values (");
				insert_sql_temp.append("editflag,escope,u0200,id,unitcode,u0207,"+oldstr+") values (");
				sql.append(",u0207=? where "+oldstr+"=? and id='"+id+"' and escope='"+escope+"' and unitcode='"+unitcode+"'");
				insert_sql.append(insert_value.toString());
				insert_sql.append("?,?,?,?,?,?,?)");
				insert_sql_temp.append(insert_value_temp.toString());
				insert_sql_temp.append("?,?,?,?,?,?,?)");
			}else{
				insert_sql.append("editflag,escope,u0200,id,unitcode,u0207) values (");	
				insert_sql_temp.append("editflag,escope,u0200,id,unitcode,u0207) values (");	
				sql.append(",u0207=? where u0200=? and id='"+id+"' and escope='"+escope+"' and unitcode='"+unitcode+"'");
				insert_sql.append(insert_value.toString());
				insert_sql.append("?,?,?,?,?,?)");
				insert_sql_temp.append(insert_value_temp.toString());
				insert_sql_temp.append("?,?,?,?,?,?)");
			}
		
		HashMap codeColMap = new HashMap();
		try
		{
		    if(codeBuf.length()>0)
		    {
			    codeBuf.setLength(codeBuf.length() - " union all ".length());			   
				RowSet rs = dao.search(codeBuf.toString());
				while (rs.next())
				  codeColMap.put(rs.getString("codesetid") + "a04v2u" + rs.getString("codeitemdesc"), rs.getString("codeitemid"));
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
	    }
		ArrayList list_insert = new ArrayList();
		ArrayList list_update = new ArrayList();
		ArrayList list_insert2 = new ArrayList();//统计插入数据
		ArrayList list_temp = new ArrayList();//向临时表中插入的数据
		//System.out.println(cols);
		LazyDynaBean bean=new LazyDynaBean();
		for (int j = 1; j <= rows; j++)
		{
			int num =0;
			int num2=0;
			int mnum = j+1;
			String u0203value ="";
			String u0201value ="";
		    ArrayList list = new ArrayList();
		    ArrayList list0 = new ArrayList();
		    list0.add(this.getUserView().getUserName());
		    row = sheet.getRow(j);	   
		 //   System.out.println(row);
		    bean=new LazyDynaBean();
		    for (short c = 1; c < cols; c++)
		    {
		    	Cell cell1=null;
		    	Cell _Insertcell=null;
			  if(row!=null){
		     cell1 = row.getCell(c);
			    _Insertcell = row.getCell((short)0);
			  }
			   if(_Insertcell!=null)
				switch (_Insertcell.getCellType())
			    {
				     case Cell.CELL_TYPE_BLANK:						        
					   _Insertcell=null;
					 break;
				}
			   boolean isInsertcell=false;
			   if(_Insertcell==null)
			   {
				   isInsertcell=true;
			   }
			   String fieldItems = (String) map.get(new Short(c));
		       if(fieldItems==null)//过滤掉只读的列
			      continue;
		       
		       if(this.getFormHM().get("cycleparm")!=null&&!"".equals(this.getFormHM().get("cycleparm"))){
		    	   if(oldiditemid!=-1&&oldiditemid==c)//过滤旧的精确编号
			    	   continue;
		       }
//		       else{
//		    	   if(oldiditemid!=-1&&oldiditemid==c)//过滤旧的精确编号
//			    	   continue;
//		       }
		       
		       String[] fieldItem = fieldItems.split(":");
		       String field = fieldItem[0];
		       String fieldName = fieldItem[1];
		       FieldItem fielditem= DataDictionary.getFieldItem(field);
		       String itemtype = fielditem.getItemtype();
		       String codesetid = fielditem.getCodesetid();		      
		       if(isU0207&&isInsertcell&& "62".equals(codesetid))
			   {	 
			      continue;
			   }
		       //获得u0207
		       if(updatehistory!=null&& "updatehistory".equals(updatehistory)&&"u0207".equalsIgnoreCase(field)){
		        if (!"0".equals(codesetid) && !"".equals(codesetid))
		        {
		        	if (cell1!=null&&cell1.getRichStringCellValue()!=null&&codeColMap.get(codesetid + "a04v2u" + cell1.getRichStringCellValue().toString().trim()) != null)
		        		U0207hisid = (String) codeColMap.get(codesetid + "a04v2u" + cell1.getRichStringCellValue().toString().trim());
		        }
		       }
		       if(u0207id!=-1&&u0207id==c)//过滤掉人员分类
		    	   continue;
		       if("u0207".equalsIgnoreCase(field)){
		    	   continue;
		       }
			   if (cell1 != null)
			   {
			       int decwidth = fielditem.getDecimalwidth();
                   String value = "";                   
			       switch (cell1.getCellType())
			       {
			          case Cell.CELL_TYPE_FORMULA:
			        	     double y = cell1.getNumericCellValue();
						        value = Double.toString(y);
						        value = PubFunc.round(value, decwidth);
						        if("D".equalsIgnoreCase(itemtype)&&value.trim().length()==6)
						        {
						        		list.add(value.substring(0,4)+"-"+value.substring(4)+"-"+"01");
						        		list0.add(value.substring(0,4)+"-"+value.substring(4)+"-"+"01");
						        }
						        else{
						        	if(u0239id!=-1&&u0239id==c){
						        		list.add(new String(value));
						        		list0.add(new String(value));
						        	}else{
						        		if(u0201id!=-1&&u0201id==c){
						        			if(this.getFormHM().get("cycleparm")!=null&&!"".equals(this.getFormHM().get("cycleparm"))){
											if(value.trim().length()>18)
											{
												value =value.trim().substring(0,17);
											}
						        			}else{
						        				if(value.trim().length()!=18&&value.trim().length()!=15)
												{
						        					if(updatehistory==null||!"updatehistory".equals(updatehistory))
						      				       throw new GeneralException("第"+mnum+"行(" + fieldName + ")中数据:" + value + " 不符合身份证格式!");
												}
						        			}
							        		}   
						        	list.add(new Double((PubFunc.round(value, decwidth))));
						        	list0.add(new Double((PubFunc.round(value, decwidth))));
							        	 
						        	}
						        	
						        }
						        
				        break;
			          case Cell.CELL_TYPE_NUMERIC:
				         y = cell1.getNumericCellValue();
				        value = Double.toString(y);
				        value = PubFunc.round(value, decwidth);
				        if("D".equalsIgnoreCase(itemtype)&&value.trim().length()==6)
				        {
				        	
				        		list.add(value.substring(0,4)+"-"+value.substring(4)+"-"+"01");
				        		list0.add(value.substring(0,4)+"-"+value.substring(4)+"-"+"01");
				        }
				        else{
				        	if(u0239id!=-1&&u0239id==c){
				        		list.add(new String(value));
				        		list0.add(new String(value));
				        	}else{
				        		if(u0201id!=-1&&u0201id==c){
				        			if(this.getFormHM().get("cycleparm")!=null&&!"".equals(this.getFormHM().get("cycleparm"))){
										if(value.trim().length()>18)
										{
											value =value.trim().substring(0,17);
										}
					        			}else{
					        				if(value.trim().length()!=18&&value.trim().length()!=15)
											{
					        					if(updatehistory==null||!"updatehistory".equals(updatehistory))
					      				       throw new GeneralException("第"+mnum+"行(" + fieldName + ")中数据:" + value + " 不符合身份证格式!");
											}
					        			}
					        		}   
				        	list.add(new Double((PubFunc.round(value, decwidth))));
				        	list0.add(new Double((PubFunc.round(value, decwidth))));
					        	 
				        	}
				        	
				        }
				        
				        
				        break;
			         case Cell.CELL_TYPE_STRING:
				        value = cell1.getRichStringCellValue().toString();
				        if (!"0".equals(codesetid) && !"".equals(codesetid))
				        {
				        	if (codeColMap.get(codesetid + "a04v2u" + value.trim()) != null)
						        value = (String) codeColMap.get(codesetid + "a04v2u" + value.trim());
					        else
						         value = null;
				        }
				        
				        if("D".equalsIgnoreCase(itemtype))
				        {
				        	if(value==null||value.length()<=0)
				        	{
				        		 list.add(null);
				        		 list0.add(null);
				        	}else if(value.matches("^[+-]?[\\d]+$")&&value.trim().length()==6)
				        	{
				        		
				        		 list.add(value.substring(0,4)+"-"+value.substring(4)+"-"+"01");
				        		 list0.add(value.substring(0,4)+"-"+value.substring(4)+"-"+"01");
				        	}
				        }else
				        {
				        	if(u0201id!=-1&&u0201id==c){
				        		if(this.getFormHM().get("cycleparm")!=null&&!"".equals(this.getFormHM().get("cycleparm"))){
									if(value.trim().length()>18)
									{
										value =value.trim().substring(0,17);
									}
				        			}else{
				        				if(value.trim().length()!=18&&value.trim().length()!=15)
										{
				        					if(updatehistory==null||!"updatehistory".equals(updatehistory))
				      				       throw new GeneralException("第"+mnum+"行(" + fieldName + ")中数据:" + value + " 不符合身份证格式!");
										}
				        			}
				        		}   
				        	list.add(value);
				        	list0.add(value);
				        }
				        
				       
				        break;
			        case Cell.CELL_TYPE_BLANK:// 如果什么也不填的话数值就默认更新为0
				       if ("N".equals(itemtype))
				       {
				          value = PubFunc.round(value, decwidth);
				          list.add(new Double(value));
				          list0.add(new Double(value));
				       } else{
				    	   list.add(null);
				    	   list0.add(null);
				       }
				         
				       break;
			        default:
				      list.add(null);
			          list0.add(null);
			    }	
			 	  
			       
			    String msg = "";
			    if (("N".equals(itemtype) || "D".equals(itemtype))&&value.trim().length()>0)
			    {
				    if (!this.isDataType(decwidth, itemtype, value))
				    {
				       msg = "第"+mnum+"行(" + fieldName + ")中数据:" + value + " 不符合格式!";
				       throw new GeneralException(msg);
				    }
				    if(u0204id!=-1&&u0204id==c){
				    	value = value.substring(0,4)+"-"+value.substring(4)+"-"+"01";
				    }
			    }
			    bean.set(field, value); 
			    if(u0201id!=-1&&u0201id==c){
			    	u0201value = value;
			    }
			    if(u0203id!=-1&&u0203id==c){
			    	u0203value = value;
			    }
			 }
			 
			 /*Cell _cell = row.getCell((short)0);
			 if(_cell!=null)
			 switch (_cell.getCellType())
			 {
				      case Cell.CELL_TYPE_BLANK:
				         //throw new GeneralException("主键标识串列存在空数据，导入数据失败！");
				    	  _cell=null;
				      break;
				      
			 }*/
			 if(cell1==null)
			 {
				 if(map.get(new Short(c))!=null)
				 {
					 list.add(null);
					 list0.add(null);
				 }
			 } 
			 if(u0201id!=-1&&u0201id==c&&cell1!=null&&cell1.getCellType()!=1){
				 
				// throw new GeneralException("第"+mnum+"行的身份证号格式不是文本!");	
			 }
//			 if(cell1!=null&&cell1.getCellType()==3&&cell1.getRichStringCellValue()!=null&&cell1.getRichStringCellValue().equals("")){
//				num++;
//			 }
			 num2++;
			 if(cell1==null||list.get(list.size()-1)==null||"".equals(list.get(list.size()-1)))
			 {
				
					 num++;
				
			 } 
		   }
		  if(num==num2)
			 continue;		//跳过该行
		 
		    //判断原精确编号是否存在
		    boolean isInsertold = false;
		    String oldvalue="";
		    String cellu0200="";
		    boolean isaddother = false;
		    if(this.getFormHM().get("cycleparm")!=null&&!"".equals(this.getFormHM().get("cycleparm"))){
		    if(oldiditemflag&&oldiditemid!=-1){
		    	
		    	
		    	Cell cell = row.getCell((short)oldiditemid);
		    	if(cell==null)
		    		throw new GeneralException("第"+mnum+"行原有精确编号不能为空!");	
		    	if(cell!=null){
		    		switch (cell.getCellType()){
		    		case Cell.CELL_TYPE_FORMULA:
		    			throw new GeneralException("第"+mnum+"行原有精确编号不能为空!");
				      //  break;
		    		case Cell.CELL_TYPE_BLANK:
		    			throw new GeneralException("第"+mnum+"行原有精确编号不能为空!");
		    		//	break;
			          case Cell.CELL_TYPE_NUMERIC:
				        double y = cell.getNumericCellValue();
				        oldvalue = Double.toString(y);
				        oldvalue = PubFunc.round(oldvalue, 0);
				        break;
			          case Cell.CELL_TYPE_STRING:
			        	  oldvalue = cell.getRichStringCellValue().toString();
			        	  if("".equals(oldvalue.trim())){
			        		  throw new GeneralException("第"+mnum+"行原有精确编号不能为空!");
			        	  }
					        break;
		    		}
		    	
		    		U0207 ="-1";
		    		U0207 =editReport.isExistData2(this.getFrameconn(), oldvalue,id,oldstr,escope,cycle_vo,u0207Map);
		    		if(!"-1".equals(U0207)){
		    			if("-3".equals(U0207)){
		    				U0207 ="1";
		    			}else{
		    			isInsertold =true;
		    			}
		    		//	if("-2".equals(U0207))
		    		//		 throw new GeneralException("上一次的精算编号中的"+reportname+"不存在!");
		    		}else{
		    			U0207="3";
		    		}
		    }
		    }
		    
		    //获取精确编号
		    if(u0200Map!=null&&u0200Map.get(oldvalue)!=null)
		    cellu0200 =(String)u0200Map.get(oldvalue);
		    idstr3 = idstr;
		    if(addother!=null&& "1".equals(addother)){//表示追加判断是否存在
		    //	isaddother = editReport.isExistDataOther(this.getFrameconn(), oldvalue,id,oldstr,escope);
		    	if(_u0207Map!=null&&oldvalue!=null&&oldvalue.trim().length()>0&&_u0207Map.get(oldvalue)!=null)
		    		isaddother=true;
		    	else
		    		isaddother=false;
		    }
		  //判断单位是否存在.
		    if(unitcodeid!=-1){
		    	Cell cell = row.getCell((short)unitcodeid);
		    	if(cell==null)
		    		throw new GeneralException("导入的excel中的第"+mnum+"行数据,单位不能为空!");	
		    	
		    	String unitname = cell.getStringCellValue();
		    //	unitname = editReport.getUnitcode(unitname,this.getFrameconn());
		    	if(allUnitMap.get(unitname.trim())==null)
		    		unitname="";
		    	else
		    	{
		    		unitcode=(String)allUnitMap.get(unitname.trim());
		    		if(unitcodes.indexOf(unitcode)==-1)
		    			unitcodes+=unitcode+",";
		    	}
		    	if(!"".equalsIgnoreCase(unitname)){
		    	/*	unitcode = unitname;
		    		if(unitcodes.indexOf(unitcode)==-1)
		    		unitcodes+=unitcode+",";*/
		    	}else{
		    		throw new GeneralException("库中不存在第"+mnum+"行的单位:"+cell.getStringCellValue()+"!");	
		    	}
		    //	ActuarialReportBo ab=new ActuarialReportBo(this.getFrameconn(),this.getUserView());
		    	
		    	String flag ="0"; //ab.isCollectUnit(unitcode);
		    	if(allCollectUnitMap.get(unitcode)!=null)
		    		flag="1";
		    	if("1".equals(flag))
		    		throw new GeneralException("第"+mnum+"行存在汇总单位,不能导入汇总单位的数据!");	
		    }
		    
		    }
		    else{
		    	 //判断姓名和身份证重复
				  String key = u0203value+u0201value;
				  if(!"".equals(key.trim())){
				if(isexistMap==null||isexistMap.get(key)==null|| "".equals(isexistMap.get(key))) {
					isexistMap.put(key, ""+mnum);
				}else{
					String per_mnum =(String)isexistMap.get(key);
					if(updatehistory==null||!"updatehistory".equals(updatehistory))
						throw new GeneralException("第"+mnum+"行与第"+per_mnum+"行姓名和身份证重复,不能导入数据!");	
				}
				  }
		    	//判断人员分类
		    	oldvalue ="";
		    
			    	Cell cell = row.getCell((short)0);
			    	 if(cell!=null){
			    			
			    		switch (cell.getCellType()){
			    		case Cell.CELL_TYPE_FORMULA:
					       break;
			    		case Cell.CELL_TYPE_BLANK:
			    		break;
				          case Cell.CELL_TYPE_NUMERIC:
					        double y = cell.getNumericCellValue();
					        oldvalue = Double.toString(y);
					        oldvalue = PubFunc.round(oldvalue, 0);
					        break;
				          case Cell.CELL_TYPE_STRING:
				        	  oldvalue = cell.getRichStringCellValue().toString();
						        break;
			    		}
		    }
			    	 
			    		U0207 ="-1";
			    	//	U0207 =editReport.isExistData2(this.getFrameconn(), oldvalue,id,"u0200",escope,cycle_vo,u0207Map);
			    		if(ori_u0207Map!=null&&oldvalue!=null&&oldvalue.trim().length()>0&&ori_u0207Map.get(oldvalue)!=null)
			    		{
			    			U0207=(String)ori_u0207Map.get(oldvalue);
			    		}
			    		if("-1".equals(U0207))
			    			U0207="3";
			    		/*
			    		if(!"-1".equals(U0207)){//true有这个精确编号
			    			if("-3".equals(U0207))
			    				U0207 ="1";
			    		}else{
			    			U0207="3";
			    		}*/
		    }
		    if(U0207hisid!=null&&U0207hisid.length()>0)
		    	U0207=U0207hisid;
		    //精算编号 
		    Cell cell0 = row.getCell((short)0);
		    String cell0id = "";
		    String cell0str="";
		    boolean flag0 =false;
		    boolean isexist =false;
		    String key2 = u0203value+u0201value;
		    if(isexisU0201U0203tMap!=null&&isexisU0201U0203tMap.get(key2)!=null){
		    String per_mnum2 =(String)isexisU0201U0203tMap.get(key2);
			if(per_mnum2!=null&&per_mnum2.startsWith("N")){
				 cell0id=per_mnum2;
				isexist=true;
			}
			}else{
		    if(cell0!=null){
		    	switch (cell0.getCellType())
				{
				      case Cell.CELL_TYPE_BLANK:
				    	  flag0 =true;
				      break;
				      case Cell.CELL_TYPE_FORMULA:
				    	  flag0 =true;
					      break;
				      case Cell.CELL_TYPE_NUMERIC:
					        double y = cell0.getNumericCellValue();
					        cell0str = Double.toString(y);
					       if("".equals(cell0str)|| "0".equals(cell0str))
					    	   flag0=true;
					        break;
				          case Cell.CELL_TYPE_STRING:
				        	  cell0str = cell0.getRichStringCellValue().toString();
						       if("".equals(cell0str)|| "0".equals(cell0str))
						    	   flag0=true;
						        break;
				} 
		    	// isexist = editReport.isExistDataOther(this.getFrameconn(), cell0str,id,"u0200",escope);
		    	if(ori_u0207Map!=null&&cell0str!=null&&cell0str.trim().length()>0&&ori_u0207Map.get(cell0str)!=null)
	    		{
		    		isexist=true;
	    		}
		    	else
		    		isexist=false;
		    	if(this.getFormHM().get("cycleparm")!=null&&!"".equals(this.getFormHM().get("cycleparm"))&&!"".equals(cellu0200)){
		    		  cell0id=cellu0200;
		    	}else{
		    	if(flag0){
		    		  IDGenerator idg=new IDGenerator(2,this.getFrameconn());
		       		    cell0id=idg.getId(("U02.U0200").toUpperCase());
				    }else{
				    	if(isexist){
				   	cell0id = cell0str;
				    	}else{
				    		 IDGenerator idg=new IDGenerator(2,this.getFrameconn());
				       		   cell0id=idg.getId(("U02.U0200").toUpperCase());
				    	}
				    }
		    	}
		    }else{
		    	if(this.getFormHM().get("cycleparm")!=null&&!"".equals(this.getFormHM().get("cycleparm"))&&!"".equals(cellu0200)){
		    		  cell0id=cellu0200;
		    	}else{
		    	IDGenerator idg=new IDGenerator(2,this.getFrameconn());
       		    cell0id=idg.getId(("U02.U0200").toUpperCase());
		    	flag0 =true;
		    	}
		    }
			}
		    //判断是否重复的精算编号
		    if(isexist&&(this.getFormHM().get("cycleparm")==null|| "".equals(this.getFormHM().get("cycleparm")))){
		    	String key =cell0str;
		    	if(isdoubleU0200Map==null||isdoubleU0200Map.get(key)==null|| "".equals(isdoubleU0200Map.get(key))) {
		    		isdoubleU0200Map.put(cell0str,""+mnum);
		    	}else{
		    		String per_mnum =(String)isdoubleU0200Map.get(key);
		    		if(!"".equals(key))
					throw new GeneralException("第"+mnum+"行与第"+per_mnum+"行精算编号重复,不能导入数据!");	
		    	}
		    	
		    }
//		    else{
//		    	  IDGenerator idg=new IDGenerator(2,this.getFrameconn());
//	       		    cell0id=idg.getId(("U02.U0200").toUpperCase());
//		    }
		    //改动说明按原精确编号
		//   Cell cell = row.getCell((short)oldiditemid);
		   if(!flag0)
		   {
			    boolean isInsert=false;
			    String error=editReport.estimateRule(updownRuleBeans,bean,U0207);
				if(error!=null&&error.length()>0)
				{
					if(updatehistory==null||!"updatehistory".equals(updatehistory))
					   throw new GeneralException(error);
				}
				
				if(this.getFormHM().get("cycleparm")!=null&&!"".equals(this.getFormHM().get("cycleparm"))){
					if(addother!=null&& "1".equals(addother)){
						
						if(!isaddother){
							    list.add("1");
					            list.add(escope);
//					            IDGenerator idg=new IDGenerator(2,this.getFrameconn());
//					       		String insertid=idg.getId(("U02.U0200").toUpperCase());	
					       		list.add(cell0id);       		   
					       		list.add(new Integer(id));
					       		list.add(unitcode);
					       		list.add(U0207);
					       		list.add(oldvalue);
					       		
					       		//System.out.println("INSERT======="+list);
					        	list_insert.add(list);  
					        	list_insert2.add(list);
					        	list0.add("1");
					        	list0.add(escope);
//					            IDGenerator idg=new IDGenerator(2,this.getFrameconn());
//					       		String insertid=idg.getId(("U02.U0200").toUpperCase());	
					        	list0.add(cell0id);       		   
					        	list0.add(new Integer(id));
					        	list0.add(unitcode);
					        	list0.add(U0207);
					        	list0.add(oldvalue);
					        	
					        	//System.out.println("INSERT======="+list);
					        	list_temp.add(list0);  
						}else{
							  
				       		   list.add(U0207);
				       		   list.add(oldvalue);
				       		//list_insert.add(list);
					         list_update.add(list);	
					         list0.add("1");
					        	list0.add(escope);
//					            IDGenerator idg=new IDGenerator(2,this.getFrameconn());
//					       		String insertid=idg.getId(("U02.U0200").toUpperCase());	
					        	list0.add(cell0id);       		   
					        	list0.add(new Integer(id));
					        	list0.add(unitcode);
					        	list0.add(U0207);
					        	list0.add(oldvalue);
					        	
					        	//System.out.println("INSERT======="+list);
					        	list_temp.add(list0); 
						}
					}else{
						
						
					if(!isInsertold)
					{
				        list.add("1");
			            list.add(escope);
//			            IDGenerator idg=new IDGenerator(2,this.getFrameconn());
//			       		String insertid=idg.getId(("U02.U0200").toUpperCase());	
			       		list.add(cell0id);       		   
			       		list.add(new Integer(id));
			       		list.add(unitcode);
			       		list.add(U0207);
			       		list.add(oldvalue);
			       		//System.out.println("INSERT======="+list);
			        	list_insert.add(list);  
			        	list_insert2.add(list);
			        	
			        	list0.add("1");
			        	list0.add(escope);
//			            IDGenerator idg=new IDGenerator(2,this.getFrameconn());
//			       		String insertid=idg.getId(("U02.U0200").toUpperCase());	
			        	list0.add(cell0id);       		   
			        	list0.add(new Integer(id));
			        	list0.add(unitcode);
			        	list0.add(U0207);
			        	list0.add(oldvalue);
			       		//System.out.println("INSERT======="+list);
			        	list_temp.add(list0); 
					}  else
					{
//						 IDGenerator idg=new IDGenerator(2,this.getFrameconn());
//				       		String insertid=idg.getId(("U02.U0200").toUpperCase());	
						list.add("1");
			            list.add(escope);
			       		list.add(cell0id);       		   
			       		list.add(new Integer(id));
			       		list.add(unitcode);
			       		list.add(U0207);
			       		list.add(oldvalue);
			       		list_insert.add(list);
				         list_update.add(list);	
				        
				         list0.add("1");
				         list0.add(escope);
				         list0.add(cell0id);       		   
				         list0.add(new Integer(id));
				         list0.add(unitcode);
				         list0.add(U0207);
				         list0.add(oldvalue);
				       		list_temp.add(list0);
					}
					}
					
				}else{
					
					if(isexist){
						list.add(U0207);
						 list.add(cell0id);
				         list_update.add(list);	
				         
				         if(stateflag!=null&& "2".equals(stateflag))
							 list0.add("2");
							else
								list0.add("1");
						         list0.add(escope);
						         list0.add(cell0id);       		   
						         list0.add(new Integer(id));
						         list0.add(unitcode);
						         list0.add(U0207);
				        	    list_temp.add(list0);
					}else{
						if(stateflag!=null&& "2".equals(stateflag))
						 list.add("2");
						else
							list.add("1");
			               list.add(escope);
			       		   list.add(cell0id);       		   
			       		   list.add(new Integer(id));
			       		   list.add(unitcode);
			       		   list.add(U0207);
			        	   list_insert.add(list);
			        	   
			        	   if(stateflag!=null&& "2".equals(stateflag))
								 list0.add("2");
								else
									list0.add("1");
							         list0.add(escope);
							         list0.add(cell0id);       		   
							         list0.add(new Integer(id));
							         list0.add(unitcode);
							         list0.add(U0207);
					        	    list_temp.add(list0);
					}
					  
				
				}
			   
			   //判断上下限
			  
		   }else
           {
				if(this.getFormHM().get("cycleparm")!=null&&!"".equals(this.getFormHM().get("cycleparm"))){

					   String error=editReport.estimateRule(updownRuleBeans,bean,U0207);
					   if(error!=null&&error.length()>0)
					   {
						   if(updatehistory==null||!"updatehistory".equals(updatehistory))
						   throw new GeneralException(error);
					   }
					   if(addother!=null&& "1".equals(addother)){
						  
							if(!isaddother){
								   list.add("1");
						            list.add(escope);
//						            IDGenerator idg=new IDGenerator(2,this.getFrameconn());
//						       		String insertid=idg.getId(("U02.U0200").toUpperCase());	
						       		list.add(cell0id);       		   
						       		list.add(new Integer(id));
						       		list.add(unitcode);
						       		list.add(U0207);
						       		list.add(oldvalue);
						       		
						       		//System.out.println("INSERT======="+list);
						        	list_insert.add(list);  
						        	list_insert2.add(list);
						        	
						        	list0.add("1");
						        	list0.add(escope);
//						            IDGenerator idg=new IDGenerator(2,this.getFrameconn());
//						       		String insertid=idg.getId(("U02.U0200").toUpperCase());	
						        	list0.add(cell0id);       		   
						        	list0.add(new Integer(id));
						        	list0.add(unitcode);
						        	list0.add(U0207);
						        	list0.add(oldvalue);
						       		
						       		//System.out.println("INSERT======="+list);
						        	list_temp.add(list0); 
							}else{
								  
					       		   list.add(U0207);
					       		list.add(oldvalue);
					       		//list_insert.add(list);
						         list_update.add(list);	
						         
						            list0.add("1");
						        	list0.add(escope);
//						            IDGenerator idg=new IDGenerator(2,this.getFrameconn());
//						       		String insertid=idg.getId(("U02.U0200").toUpperCase());	
						        	list0.add(cell0id);       		   
						        	list0.add(new Integer(id));
						        	list0.add(unitcode);
						        	list0.add(U0207);
						        	list0.add(oldvalue);
						       		
						       		//System.out.println("INSERT======="+list);
						        	list_temp.add(list0);
							}
						}else{
							
					   if(!isInsertold){
						   list.add("1");
			               list.add(escope);
//			               IDGenerator idg=new IDGenerator(2,this.getFrameconn());
//			       		   String insertid=idg.getId(("U02.U0200").toUpperCase());	
			       		   list.add(cell0id);       		   
			       		   list.add(new Integer(id));
			       		   list.add(unitcode);
			       		   list.add(U0207);
			       		   list.add(oldvalue);
			       		   //System.out.println("INSERT======="+list);
			        	   list_insert.add(list);
			        	   list_insert2.add(list);
			        	   
			        	   list0.add("1");
			        	   list0.add(escope);
//			               IDGenerator idg=new IDGenerator(2,this.getFrameconn());
//			       		   String insertid=idg.getId(("U02.U0200").toUpperCase());	
			        	   list0.add(cell0id);       		   
			        	   list0.add(new Integer(id));
			        	   list0.add(unitcode);
			        	   list0.add(U0207);
			        	   list0.add(oldvalue);
			       		   //System.out.println("INSERT======="+list);
			        	   list_temp.add(list0);
					   }else{
//						   IDGenerator idg=new IDGenerator(2,this.getFrameconn());
//				       		String insertid=idg.getId(("U02.U0200").toUpperCase());	
						   list.add("1");
			               list.add(escope);
			       		   list.add(cell0id);       		   
			       		   list.add(new Integer(id));
			       		   list.add(unitcode);
			       		   list.add(U0207);
			       		   list.add(oldvalue);
			       		list_insert.add(list);
				         list_update.add(list);
				         
				           list0.add("1");
				           list0.add(escope);
				           list0.add(cell0id);       		   
				           list0.add(new Integer(id));
				           list0.add(unitcode);
				           list0.add(U0207);
				           list0.add(oldvalue);
			       		list_temp.add(list0);
					   }
					}		
				}else{
			   String error=editReport.estimateRule(updownRuleBeans,bean,U0207);
			   if(error!=null&&error.length()>0)
			   {
				   if(updatehistory==null||!"updatehistory".equals(updatehistory))
				   throw new GeneralException(error);
			   }
				if(stateflag!=null&& "2".equals(stateflag))
					 list.add("2");
					else
						list.add("1");
               list.add(escope);
       		   list.add(cell0id);       		   
       		   list.add(new Integer(id));
       		   list.add(unitcode);
       		   list.add(U0207);
        	   list_insert.add(list);
        	   
        	   
        	   
        	   if(stateflag!=null&& "2".equals(stateflag))
        		   list0.add("2");
        	   else
        		   list0.add("1");
        	   list0.add(escope);
        	   list0.add(cell0id);       		   
        	   list0.add(new Integer(id));
        	   list0.add(unitcode);
        	   list0.add(U0207);
        	   list_temp.add(list0);
        	   
        	  
			   
				}
           } 
		 //  if(list.size()!=17)
		//   System.out.println(insert_sql.toString()+"list.size:"+list.size());
//		   System.out.println(sql.toString()+"list.size:"+list.size()+"listupdate:"+list_update.size());
		}
	//System.out.print(new Date().getTime()-start.getTime()+"/s");
				    
			//dao.batchUpdate(sql.toString(), list_update);
			//dao.batchInsert(insert_sql.toString(), list_insert);
			//针对报表周期初试化数据导入只有插入，修改人员只是显示作用后台调用该精确编号
		    StringBuffer info=new StringBuffer();
		    if(updatehistory!=null&& "updatehistory".equals(updatehistory)){
		    	list_insert2.clear();
		    	list_insert.clear();
		    }
		    info.append("导入模板统计信息：<br>");
		    info.append("&nbsp;&nbsp;修改人员："+list_update.size()+" 人；<br>");
		    if(this.getFormHM().get("cycleparm")!=null&&!"".equals(this.getFormHM().get("cycleparm"))){
		    info.append("&nbsp;&nbsp;新增人员："+list_insert2.size()+" 人；");
		    }else{
		    	 info.append("&nbsp;&nbsp;新增人员："+list_insert.size()+" 人；");
		    }
		    DbWizard dbWizard = new DbWizard(this.getFrameconn());
		    if(!dbWizard.isExistTable("t#_u02",false))
			{
		    	if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    	dao.update("create table t#_u02 as select * from u02 where 1=2");
		    	else
		    	dao.update("select * into t#_u02 from u02 where 1=2");
		    	
		    	Table table = new Table("t#_u02");
		    	table.addField(getField("username", "A", 30, false));	
		    	if(table.size()>0)
		    		dbWizard.addColumns(table);
			}else{
				String delsql = "delete from t#_u02 where username="+"'"+this.userView.getUserName()+"'";
				dao.update(delsql);
			}
		    dao.batchInsert(insert_sql_temp.toString(), list_temp);
		    //通过公式校验
			String  infoStr = getShInfo(" username="+"'"+this.getUserView().getUserName()+"'");
			if(infoStr.length()>0){
				throw new GeneralException(infoStr);
			}
		    this.getFormHM().put("import_insertList", list_insert);
		    this.getFormHM().put("import_updateList", list_update);
		    this.getFormHM().put("import_insertSql", insert_sql.toString());
		    this.getFormHM().put("import_updateSql", sql.toString());
		    this.getFormHM().put("importInfo", info.toString());
		    this.getFormHM().put("updatehistory", updatehistory);
		    if(this.getFormHM().get("cycleparm")!=null&&!"".equals(this.getFormHM().get("cycleparm"))){
		   // String deleteinfo = " delete from U02 where escope='"+escope+"' and id="+new Integer(id)+"";
		    //this.getFormHM().put("import_deleteinfo", deleteinfo);
		    	if(!"".equals(unitcodes))
		    		unitcodes = unitcodes.substring(0, unitcodes.length()-1);
		    this.getFormHM().put("unitcodes", unitcodes);
		    }
		    form_file=null;
		    this.getFormHM().put("file", form_file);
		
		
	} catch (Exception e) {
		throw GeneralExceptionHandler.Handle(e); 
	}finally {
			PubFunc.closeResource(wb);
		}
		
	}
	
	public String getPre_cycleID(RecordVo cycle_vo,ContentDAO dao)
	{
		String pre_cycle_id="0";
		try
		{
			String kmethod = cycle_vo.getString("kmethod");
			Date bos_date=new Date();
			bos_date = cycle_vo.getDate("bos_date");
			if(bos_date!=null){	
				if(kmethod!=null&& "0".equals(kmethod)){
					String date_str=DateUtils.format(bos_date, "yyyy-MM-dd");
					String sqlstr="select id from tt_cycle  where bos_date<"+Sql_switcher.dateValue(date_str)+" and  kmethod=0 order by bos_date desc";
				   this.frowset=dao.search(sqlstr);			  
					if(frowset.next())
					{
						pre_cycle_id=this.frowset.getString("id");
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return pre_cycle_id;
	}
	
	public HashMap getU0207Map(String cycle_id,String str,ContentDAO dao,String escope,String stateflag)
	{
		HashMap map=new HashMap();
		try
		{
			String sql ="";
			 if(this.getFormHM().get("cycleparm")!=null&&!"".equals(this.getFormHM().get("cycleparm"))){
					 sql = "select U0207,"+str+"  from u02 where id="+cycle_id+"  and escope='"+escope+"'"; 
			 }else{
				 if("2".equals(stateflag)){
					 sql = "select U0207,"+str+"  from u02 where id="+cycle_id+"  and escope='"+escope+"' and unitcode like'"+(String)this.getFormHM().get("unitcode")+"%' and editflag="+stateflag+"  ";
				 }else{
					 sql = "select U0207,"+str+"  from u02 where id="+cycle_id+"  and escope='"+escope+"' and unitcode like '"+(String)this.getFormHM().get("unitcode")+"%'  "; 
				 }
					
			 }
			
			// dao = new ContentDAO(this.getFrameconn());
			if(this.frowset!=null)
			this.frowset.close();
			this.frowset = dao.search(sql);
			while(this.frowset.next())
			{
				map.put(this.frowset.getString(str),this.frowset.getString("U0207"));
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if("0".equals(cycle_id))
			return null;
		return map;
	}
	public HashMap getU0201U0203Map(String cycle_id,ContentDAO dao,String escope,String stateflag,String updatehistory)
	{
		HashMap map=new HashMap();
		try
		{
			String sql ="";
			 if(this.getFormHM().get("cycleparm")!=null&&!"".equals(this.getFormHM().get("cycleparm"))){
			 }else{
				 if(updatehistory!=null&& "updatehistory".equals(updatehistory)){
				    }else{
				    	 if("2".equals(stateflag)){
						 }else{
							 sql = "select U0200,U0201,U0203  from u02 where id="+cycle_id+"  and escope='"+escope+"' and unitcode like '"+(String)this.getFormHM().get("unitcode")+"%'  "; 
						 }
				    }
				
					
			 }
			
			// dao = new ContentDAO(this.getFrameconn());
			if(this.frowset!=null)
			this.frowset.close();
			if(sql.length()>0){
			this.frowset = dao.search(sql);
			while(this.frowset.next())
			{
				if(this.frowset.getString("U0201")!=null&&this.frowset.getString("U0201").length()>0&&this.frowset.getString("U0203")!=null&&this.frowset.getString("U0203").length()>0)
				map.put(this.frowset.getString("U0203")+this.frowset.getString("U0201"),this.frowset.getString("U0200"));
				
			}
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if("0".equals(cycle_id))
			return null;
		return map;
	}
	public HashMap getU0200Map(String cycle_id,String str,ContentDAO dao,String escope)
	{
		HashMap map=new HashMap();
		try
		{
			String sql ="";
			
					 sql = "select U0200,"+str+"  from u02 where id="+cycle_id+"  and escope='"+escope+"'"; 
			
			
			// dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql);
			while(this.frowset.next())
			{
				map.put(this.frowset.getString(str),this.frowset.getString("U0200"));
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if("0".equals(cycle_id))
			return null;
		return map;
	}
	
	public HashMap getCollectUntiMap2()
	{
		HashMap map=new HashMap();
		try
		{
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rowSet=dao.search("select unitcode,unitname from tt_organization  ");
			while(rowSet.next())
				map.put(rowSet.getString("unitname").trim(),rowSet.getString("unitcode"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	public HashMap getCollectUntiMap()
	{
		HashMap map=new HashMap();
		try
		{
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rowSet=dao.search("select unitcode from tt_organization where unitcode in (select parentid from tt_organization)");
			while(rowSet.next())
				map.put(rowSet.getString("unitcode"),"1");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	public boolean isDataType(int decwidth, String itemtype, String value)
	{

		boolean flag = true;
		if ("N".equals(itemtype))
		{
		    if (decwidth == 0)
		    {
			flag = value.matches("^[+-]?[\\d]+$");
		    } else
		    {
			flag = value.matches("^[+-]?[\\d]*[.]?[\\d]+");
		    }

		} else if ("D".equals(itemtype))
		{
			if(value.matches("^[+-]?[\\d]+$")&&value.trim().length()==6){
				if(value.matches("[0-9]{4}[0][1-9]{1}")||value.matches("[0-9]{4}[1][0-2]{1}"))
				flag=true;
				else
					flag =false;
			}
			else
				flag = false;
		}
		return flag;
    }
	/**
     * Description: 公式验证 返回错误提示信息（无则返回""）
     * @Version1.0 
     * Nov 26, 2012 8:46:23 PM Jianghe created
     * @param list
     * @return
     * @throws GeneralException
     */
    public String getShInfo(String conWhere) throws GeneralException
	{
    	String message="";
		HashMap returnMap = new HashMap();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ResultSet rs = null;
		try
		{
			ArrayList formulaList = this.getSpFormulaList();
			ArrayList varlist =this.getUItemList();
			YksjParser yp=null;
			yp = new YksjParser(this.userView ,varlist,YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
			yp.setCon(this.getFrameconn());
			for(int i=0;i<formulaList.size();i++)
			{
				String message1 = "";
				String strName = "";
				String information = "";
				StringBuffer sql = new StringBuffer();
				LazyDynaBean bean = (LazyDynaBean)formulaList.get(i);
				String formula=(String)bean.get("formula");
				String formulaname=(String)bean.get("name");
                information=(String)bean.get("information");
				if(formula==null|| "".equals(formula))
					continue;
				yp.run(formula.trim());
				String wherestr = yp.getSQL();//公式的结果
				sql.append(" select * from t#_u02 where ");
				if(wherestr.trim().length()>0)
					sql.append("("+wherestr+")");
				if(wherestr.trim().length()>0)
					sql.append(" and ");
				if(conWhere.trim().length()>0)
				sql.append(conWhere);
				rs=dao.search(sql.toString());
				while(rs.next())
				{
					strName += rs.getString("u0203")+",";
					
				}	
				if(strName.length()>0){
					strName = strName.substring(0,strName.length()-1);
					message1= strName+"\n"+information+"\n";
					//return message;
					if(message.length()>0)
					    message+="\n"+message1;
					else
						message+=message1;
			    }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return message;
	}
    public ArrayList getSpFormulaList()
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select chkid,name,validflag,formula,information  from hrpchkformula where 1=1  ");
				sql.append("and flag=3  ");
			sql.append(" order by seq");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rs = null;
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("chkid",rs.getString("chkid"));
				bean.set("name",rs.getString("name"));
				bean.set("validflag", rs.getString("validflag"));
				bean.set("information", rs.getString("information"));
				bean.set("formula", Sql_switcher.readMemo(rs,"formula"));
				//bean.set("formula", )
				list.add(bean);
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
    public ArrayList getUItemList()
	{
		ArrayList list = new ArrayList();
		try
		{
			ArrayList fielditemlist = new ArrayList();

			fielditemlist = DataDictionary.getFieldList("u02",
					Constant.USED_FIELD_SET);
			for (int i = 0; i < fielditemlist.size(); i++) {
				if (fielditemlist.get(i) == null)
					continue;
				FieldItem fielditem = (FieldItem) fielditemlist.get(i);
				list.add(fielditem);
			}
			FieldItem item = new FieldItem();
			item.setItemid("escope");
			item.setItemdesc("人员范围");
			item.setItemtype("A");
			item.setCodesetid("61");
			list.add(item);
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
		
	}
    /**
	 * 新建指标计算公式临时表字段
	 */
	public Field getField(String fieldname, String a_type, int length, boolean key)
    {
		Field obj = new Field(fieldname, fieldname);
		if ("A".equals(a_type))
		{
		    obj.setDatatype(DataType.STRING);
		    obj.setLength(length);
		} else if ("M".equals(a_type))
		{
		    obj.setDatatype(DataType.CLOB);
		} else if ("I".equals(a_type))
		{
		    obj.setDatatype(DataType.INT);
		    obj.setLength(length);
		} else if ("F".equals(a_type))
		{
		    obj.setDatatype(DataType.FLOAT);
		    obj.setLength(length);
		    obj.setDecimalDigits(5);
		} else if ("D".equals(a_type))
		{
		    obj.setDatatype(DataType.DATE);
		} else
		{
		    obj.setDatatype(DataType.STRING);
		    obj.setLength(length);
		}
		if(key)
		    obj.setNullable(false);
		obj.setKeyable(key);	
		return obj;
    }
    
}
