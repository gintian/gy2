package com.hjsj.hrms.businessobject.info.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.BuildLeaderStuff;
import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.transaction.stat.SformulaXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.jdom.Element;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * 领导班子
 * @author Guodd
 *2014-01-14
 */

public class LeaderUtils {


	/**
	 * 生成领导班子人员查询sql语句
	 * @param leaderMainSet  领导班子主集 Bxx
	 * @param leaderPersonSet 领导班子人员信息集 Axx
	 * @param linkField 关联指标
	 * @param b0110Field 所属单位
	 * @param i9999 领导班子的序号值
	 * @param orderByField 排序指标
	 * @param displayFields 查询的指标
	 * @param b0110 单位id
	 * @param nextOrg 是否包含下级机构班子成员 1：是/ 0:否
	 * @param leaderType 班子类型
	 * @param leaderSession 班子届次
	 * @param browsefields 将“查询的指标”转换成FieldItem 放入此list
	 * @return
	 */
	public String createLeaderInfoSql(String leaderMainSet,String leaderPersonSet,String linkField,
			                          String b0110Field,String i9999,
			                          String orderByField,String displayFields,String b0110,String nextOrg,String leaderTypeItem,
			                          String leaderTypeValue,String sessionItem,String sessionValue,ArrayList browsefields,ArrayList dbpreList,UserView userView){
		
		String[] itemids = displayFields.split(",");
		
		HashSet setmap = new HashSet(); 
		String setName = "Usr"+leaderPersonSet;
		StringBuffer ssql = new StringBuffer("select "+setName+".A0100,"+setName+".I9999,'Usr' dbpre, ");
		StringBuffer fsql = new StringBuffer(" from (select TT.*");
		if(leaderTypeItem!=null && leaderTypeItem.trim().length()>0) {
            fsql.append(","+leaderMainSet+"."+leaderTypeItem);
        }
	    if(sessionItem!=null && sessionItem.trim().length()>0) {
            fsql.append(","+leaderMainSet+"."+sessionItem);
        }
		fsql.append(" from "+setName+" TT left join "+leaderMainSet);
		fsql.append(" on TT."+b0110Field+"="+leaderMainSet+".b0110 and TT."+linkField+"="+leaderMainSet+".i9999 ");
		if("1".equals(nextOrg)){
			fsql.append(" where "+b0110Field+"=(select min("+b0110Field+") from "+setName+" where a0100=TT.a0100  and "+b0110Field+" like '"+b0110+"%') ");
			fsql.append(" and  "+linkField+"=(select min("+linkField+") from "+setName+" where a0100=TT.a0100 and "+b0110Field+" like '"+b0110+"%')");
			//fsql.append(" and "+b0110Field+" like '"+b0110+"%' ");
		}else {
            fsql.append("where "+b0110Field+"="+b0110+" and "+linkField+"="+i9999);
        }
		fsql.append(") "+setName);
		StringBuffer column = new StringBuffer();
		
		for(int i=0;i<itemids.length;i++){
			FieldItem fi = DataDictionary.getFieldItem(itemids[i]);
			if(fi==null || "0".equals(fi.getUseflag())) {
                continue;
            }
			
			ssql.append("Usr"+fi.getFieldsetid()+"."+fi.getItemid()+",");
			
			if(!fi.getFieldsetid().equalsIgnoreCase(leaderPersonSet) && !setmap.contains(fi.getFieldsetid())){
				 if("A01".equalsIgnoreCase(fi.getFieldsetid())) {
                     fsql.append(" left join Usr"+fi.getFieldsetid());
                 } else {
                     fsql.append(" left join (select * from Usr"+fi.getFieldsetid()+" POXY where i9999=(select max(i9999) from Usr"+fi.getFieldsetid()+" where a0100=POXY.a0100)) Usr"+fi.getFieldsetid());
                 }
				   
			     fsql.append(" on "+setName+".A0100 = Usr"+fi.getFieldsetid()+".A0100 ");
			     //if(!fi.getFieldsetid().equalsIgnoreCase("A01"))
			    //	 fsql.append(" and Usr"+fi.getFieldsetid()+".i9999 = (select max(i9999) from  Usr"+fi.getFieldsetid()+" where a0100="+setName+".A0100 ) ");
			     setmap.add(fi.getFieldsetid());
			}
			
			column.append(fi.getItemid()+",");
			
			browsefields.add(fi);
		}
		
		if(ssql.indexOf(orderByField)==-1) {
            ssql.append(setName+"."+orderByField+",");
        }
		if(ssql.indexOf(b0110Field)==-1) {
            ssql.append(setName+"."+b0110Field+",");
        }
		if(ssql.indexOf(linkField)==-1) {
            ssql.append(setName+"."+linkField+",");
        }
		
		if(!"all".equals(leaderTypeValue) && leaderTypeItem.length()>0){
			ssql.append(setName+"."+leaderTypeItem+",");
		}
		if(!"all".equals(sessionValue) && !"max".equals(sessionValue) && sessionItem.length()>0) {
            ssql.append(setName+"."+sessionItem+",");
        }
		
		ssql.append(" '1' s ");
		
		
		
		StringBuffer wsql = new StringBuffer(" where 1=1 ");
		
		if(!"all".equals(leaderTypeValue) && leaderTypeItem.length()>0){
			wsql.append(" and "+setName+"."+leaderTypeItem+" = '"+leaderTypeValue+"' ");
		}
		if(!"all".equals(sessionValue) && !"max".equals(sessionValue) && sessionItem.length()>0) {
            wsql.append(" and "+setName+"."+sessionItem+" = '"+sessionValue+"' ");
        }
		
		
		
		
		String combineSql = ssql.toString()+fsql.toString()+wsql.toString();
		
		StringBuffer dbuSql = new StringBuffer();
		//ArrayList dblist = userView.getPrivDbList();
		for(int i=0;i<dbpreList.size();i++){
			String dbname = dbpreList.get(i).toString();
			String dbSql = combineSql.replaceAll("Usr", dbname);
			dbuSql.append(dbSql+" union all ");
		}
		dbuSql.delete(dbuSql.lastIndexOf("union all"), dbuSql.length());
		
		return dbuSql.toString();
	}
	
	/**
	 * 查询兼职岗位的信息
	 * @param a0100
	 * @param nbase
	 * @param code
	 * @param kind
	 * @param b0110
	 * @param e0122
	 * @return
	 */
	public String getPartJob(String a0100,String nbase, String code, String kind, String b0110, String e0122)
    {
		String part_desc="";
		String part_setid="";
		String part_unit="";
		String appoint="";
		String flag="";
		String part_pos="";
		String part_dept="";
		String part_order="";
		String part_format="";
		RowSet rs = null;
		Connection conn=null;
		try {
			conn = AdminDb.getConnection();
			Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(conn);
			ArrayList list = new ArrayList();
			list.add("flag");// 启用标识
			list.add("unit");// 兼职单位标识
			list.add("setid");// 兼职子集
			list.add("appoint");// 任免标识
			list.add("pos");// 任免职务
			list.add("dept");// 兼职部门
			list.add("order");// 排序
			list.add("format");// 兼职内容显示格式
			list.add("takeup_quota");// 兼职占用岗位编制：1占用，0或null 则不占用
			list.add("occupy_quota");// 兼职占用单位部门编制：1占用，0或null 则不占用
			String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
			HashMap part_map = sysoth.getAttributeValues(Sys_Oth_Parameter.PART_TIME, list);
			if(part_map==null)
			{
				return part_desc;
			}	
		if(part_map.get("flag")!=null && ((String)part_map.get("flag")).trim().length()>0) {
            flag=(String)part_map.get("flag");
        }
		if(part_map.get("unit")!=null && ((String)part_map.get("unit")).trim().length()>0) {
            part_unit=(String)part_map.get("unit");
        }
		if(part_map.get("setid")!=null && ((String)part_map.get("setid")).trim().length()>0) {
            part_setid=(String)part_map.get("setid");
        }
		if(part_map.get("appoint")!=null && ((String)part_map.get("appoint")).trim().length()>0) {
            appoint=(String)part_map.get("appoint");
        }
		if(part_map.get("pos")!=null && ((String)part_map.get("pos")).trim().length()>0) {
            part_pos=(String)part_map.get("pos");
        }
		if(part_map.get("dept")!=null && ((String)part_map.get("dept")).trim().length()>0) {
            part_dept=(String)part_map.get("dept");
        }
		if(part_map.get("order")!=null && ((String)part_map.get("order")).trim().length()>0) {
            part_order=(String)part_map.get("order");
        }
		if(part_map.get("format")!=null && ((String)part_map.get("format")).trim().length()>0) {
            part_format=(String)part_map.get("format");
        }
		boolean isreturn=false;
		
		if(!"true".equalsIgnoreCase(flag)) {
            isreturn=true;
        }
		if(part_setid==null||part_setid.length()<=0) {
            isreturn=true;
        }
    	if(a0100==null||a0100.length()<=0) {
            isreturn=true;
        }
    	if(nbase==null||nbase.length()<=0) {
            isreturn=true;
        }
    	if(part_pos==null||part_pos.length()<=0) {
            isreturn=true;
        }
    	if(appoint==null||appoint.length()<=0) {
            isreturn=true;
        }
    	FieldItem fielitem=DataDictionary.getFieldItem(part_pos);
    	if(fielitem==null) {
            isreturn=true;
        }
    	if(isreturn)
    	{
			return part_desc;
    	}
    	String codesetid=fielitem.getCodesetid();
    	
    	StringBuffer sql=new StringBuffer();
    	boolean isview=false;
    	if(code!=null)
    	{
    		if("2".equals(kind))
    		{
    			if(code.indexOf(b0110)==-1&&b0110.indexOf(code)==-1)
    			{
    				isview=true;
    			}
    		}else if("1".equals(kind))
    		{
    			if(code.indexOf(e0122)==-1&&e0122.indexOf(code)==-1)
    			{
    				isview=true;
    			}
    		}
    	}
			   ContentDAO dao=new ContentDAO(conn);
			   sql.append("select "+part_pos+" as part_pos  ");
		       if(part_unit!=null&&part_unit.length()>0) {
                   sql.append(","+part_unit+" part_unit ");
               }
		       if(part_dept!=null&&part_dept.length()>0) {
                   sql.append(","+part_dept+" part_dept ");
               }
		       if(part_format!=null&&part_format.length()>0) {
                   sql.append(","+part_format+" part_format ");
               }
		       sql.append(" from "+nbase+part_setid+" where a0100='"+a0100+"'");
		       if(appoint!=null&&appoint.length()>0) {
                   sql.append(" and "+appoint+"='0' ");
               }
		       if(part_order!=null&&part_order.length()>0) {
                   sql.append(" order by "+part_order);
               }
			   rs=dao.search(sql.toString());
			   String pos="";
			   String unit="";
			   String dept="";
			   String un_value="";		
			   String um_value="";	
			   String format="";
			   String unit_code="";
			   String dept_code="";
			   int unit_len=0;
			   int dept_len=0;
			   StringBuffer buf=new StringBuffer();
			   StringBuffer cufbuf=new StringBuffer();
			   while(rs.next())
			   {
				   
				   if(part_unit!=null&&part_unit.length()>0)//兼职单位
				   {
					   unit=rs.getString("part_unit");
					   if(unit!=null&&unit.length()>0)
					   {
						   un_value=AdminCode.getCodeName("UN",unit);
						   if(un_value==null||un_value.length()<=0) {
                               un_value=AdminCode.getCodeName("UM",unit);
                           }
						   if(isview)
						   {
							   if(unit.indexOf(code)==0||code.indexOf(unit)==0)
							   {								   
								   if(unit_len<unit.length())
								   {
									   if("2".equals(kind)&&unit.length()<=code.length())
									   {
										   unit_len=unit.length();
										   unit_code=unit;
									   }else
									   {
										   unit_len=unit.length();
										   unit_code=unit;
									   }
									   
								   }
							   }
						   }
					   }					   
				   }
				   if(part_dept!=null&&part_dept.length()>0)//兼职单位
				   {
					   dept=rs.getString("part_dept");
					   if(dept!=null&&dept.length()>0)
					   {
						   um_value=AdminCode.getCodeName("UM",dept);	
						   if(isview)
						   {
							   if(dept.indexOf(code)==0||code.indexOf(dept)==0)
							   {								   
								   if(dept_len<dept.length())
								   {
									   if("1".equals(kind)&&dept.length()<=code.length())
									   {
										   dept_len=dept.length();
										   dept_code=dept;
									   }else
									   {
										   dept_len=dept.length();
										   dept_code=dept;
									   }
									   
								   }
							   }
						   }
					   }
				   }	
				   pos=rs.getString("part_pos");
				   if(codesetid!=null&&codesetid.length()>0&&!"0".equals(codesetid))
				   {
					   pos=AdminCode.getCodeName(codesetid,pos);
				   }				   
				   if(pos!=null&&pos.length()>0)
				   {
					   cufbuf.setLength(0);
					   if(un_value!=null&&un_value.length()>0) {
                           cufbuf.append(un_value+"/");
                       }
					   if(um_value!=null&&um_value.length()>0) {
                           cufbuf.append(um_value+"/");
                       }
					   cufbuf.append(pos);	
					   buf.append(PubFunc.reLineString(cufbuf.toString(), 30, "<br/>"));
					   if(part_format!=null&&part_format.length()>0)
					   {
						   format=rs.getString("part_format");
						   if(format!=null&&format.length()>=0)
						   {
							   buf.append(format=format.replaceAll("\\\\n", "<br>&nbsp;"));
						   }					  
					   }
				   }
					
			   }			   
			   CodeItem item = null;
			   //单位显示
			   if(unit_code!=null&&unit_code.length()>0)
			   {
				   item=AdminCode.getCode("UN",unit_code);
				   if(item==null)
				   {
					   item=AdminCode.getCode("UM",unit_code);
				   }
			   }
			   if(item==null)
			   {
				   item=AdminCode.getCode("UN",b0110);
			   }
			   if(item!=null) {
			}
			   //部门显示
			   item = null;
			   if(dept_code!=null&&dept_code.length()>0)
			   {
				   if(uplevel!=null&&uplevel.length()>0)
				   {
					   item=AdminCode.getCode("UM",dept_code,Integer.parseInt(uplevel));
				   }else
				   {
					   item=AdminCode.getCode("UM",dept_code);
				   }	
			   }
			   if(item==null)
			   {
					   item=AdminCode.getCode("UM",e0122);
			   }
			   if(item!=null) {
			}
			   
			   if(buf!=null&&buf.length()>0)
			   {
				  part_desc="<br>&nbsp;"+buf.toString()+"&nbsp;";		
			   }else
			   {
				   part_desc="";
			   }
			}catch(Exception e)
			{
				e.printStackTrace();				
			}
			finally
			{
				try{
				 if(rs!=null) {
                     rs.close();
                 }
				 if (conn != null) {
                     conn.close();
                 }
				 
				}catch(Exception e)
				{
					e.printStackTrace();
				}
		          
			}
		return part_desc;
    }
	
	
	/**
	 * 生成excel
	 * @param conn
	 * @param sql  数据查询语句
	 * @param titleItems  显示的字段      例：[FieldItem,FieldItem,FieldItem]
	 * @return String fileName 文件名称 加密
	 */
	public String createExcelFile(Connection conn,String sql,ArrayList titleItems,UserView userView){
		
		String fileName = userView.getUserName()+"_LeadPersons"+ ".xls";
		HSSFWorkbook workbook = null;
		try{
			
			workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("领导班子成员信息表");
			HSSFRow row = null;
			HSSFCell cell = null;
			int rowNum = 0;
			
			HSSFFont titlefont = workbook.createFont();
			titlefont.setFontHeight((short)300);
			
			HSSFFont itemfont = workbook.createFont();
			itemfont.setFontHeight((short)280);
			
			HSSFCellStyle titleStyle  = workbook.createCellStyle();
			titleStyle.setAlignment(HorizontalAlignment.CENTER);
			titleStyle.setFont(titlefont);
			
			HSSFCellStyle itemStyle  = workbook.createCellStyle();
			itemStyle.setAlignment(HorizontalAlignment.CENTER);
			titleStyle.setFont(itemfont);
			
			HSSFCellStyle charStyle  = workbook.createCellStyle();
			charStyle.setAlignment(HorizontalAlignment.LEFT);
			
			HSSFCellStyle numStyle  = workbook.createCellStyle();
			numStyle.setAlignment(HorizontalAlignment.RIGHT);
			
			//sheet title
			row = sheet.createRow(rowNum);
			row.setHeight((short)350);
			cell = row.createCell(0);
			cell.setCellValue("领导班子成员信息表");
			cell.setCellStyle(titleStyle);
			ExportExcelUtil.mergeCell(sheet, rowNum, (short)0, rowNum, (short)(titleItems.size()-1));
			rowNum++;
			
			row = sheet.createRow(rowNum);
			for(int i=0;i<titleItems.size();i++){
				FieldItem fi = (FieldItem)titleItems.get(i); 
				cell = row.createCell(i);
				cell.setCellValue(fi.getItemdesc());
				cell.setCellStyle(itemStyle);
				sheet.setColumnWidth(i, 4000);
				
			}
			rowNum++;
			
			List dataList = ExecuteSQL.executeMyQuery(sql, conn);
			
			for(int k = 0;k<dataList.size();k++){
				LazyDynaBean ldb = (LazyDynaBean)dataList.get(k);
				row = sheet.createRow(rowNum);
				for(int l = 0;l<titleItems.size();l++){
					FieldItem fi = (FieldItem)titleItems.get(l);
					cell = row.createCell(l);
					String value = ldb.get(fi.getItemid()).toString();
					
					if(fi.getCodesetid()!=null && !"0".equals(fi.getCodesetid())) {
                        value = getCodeValue(fi.getCodesetid(),value,conn);
                    }
					
					if("N".equals(fi.getItemtype())) {
                        cell.setCellStyle(numStyle);
                    } else if("D".equals(fi.getItemtype())){
						int len = fi.getItemlength();
						value = value.length()>len?value.substring(0, len):value;
					}else {
                        cell.setCellStyle(charStyle);
                    }
					
					cell.setCellValue(value);
				}
				
				rowNum++;
			}
			
			FileOutputStream fout = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName);
			workbook.write(fout);
			fout.close();
			
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(workbook);
		}
		
		//zxj 20141120  文件名加密
		return PubFunc.encrypt(fileName);
	}
	
	private String getCodeValue(String codesetid,String codevalue,Connection conn){
	
		String codedesc = "";
		try{
			
			StringBuffer sql = new StringBuffer();
			
			if("@K".equalsIgnoreCase(codesetid) || "UM".equalsIgnoreCase(codesetid) || "UN".equalsIgnoreCase(codesetid)) {
                sql.append("select codeitemdesc from organization where codeitemid = '"+codevalue+"'");
            } else {
                sql.append(" select codeitemdesc from codeitem where codesetid='"+codesetid+"' and codeitemid='"+codevalue+"'");
            }
			
			List codeList = ExecuteSQL.executeMyQuery(sql.toString());
			 
			if(codeList!=null && codeList.size()>0){
				LazyDynaBean ldb = (LazyDynaBean)codeList.get(0);
				codedesc = ldb.get("codeitemdesc").toString();
			}
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return codedesc;
	}
	
	private String statid ="";
	
	public String analyserLeader(String wsql,String a_code,Connection conn,UserView userView,String replaceDbStr) throws SQLException{
		
		RowSet rs = null;
		StringBuffer txtfile = new StringBuffer("<ul>");
		try{
		
		LeadarParamXML leadarParamXML=new LeadarParamXML(conn);
		
	    String gcond=leadarParamXML.getTextValue(LeadarParamXML.GCOND);	
	    ArrayList statlist=loadstatlist(gcond,conn,userView);
	    
	    ArrayList dblist = new ArrayList();
	    dblist = userView.getPrivDbList();
	    
	    String dbpre="";
	    String ttype=null;
	    BuildLeaderStuff bls = new BuildLeaderStuff(conn,userView);
	    
	    ArrayList datalist=new ArrayList();
	    ArrayList varraydatalist = new ArrayList();
	    ArrayList harraydatalist = new ArrayList();
	    
	    
	    for(int y=0;y<statlist.size();y++){
	    	String type=null;
	    	statid = ((CommonData)statlist.get(y)).getDataValue();
	    	
	    	for(int x=0;x<dblist.size();x++){
	    		dbpre = dblist.get(x).toString();
	    		String A0100In = wsql.replaceAll(replaceDbStr, dbpre);
	    		String sqlwhere = dbpre+"A01.A0100 in ( select a0100 from ("+A0100In+") a)";
	    		boolean isresult=true;
	    		
	    		try{
					String sql="select id,type from sname where id=" + statid;
					ContentDAO dao=new ContentDAO(conn);
					rs=dao.search(sql.toString());
		         	if(rs.next())
		         	{
		   			  type=rs.getString("type");
		      	    }	
				}catch(Exception e){
					e.printStackTrace();
				}
				
				SformulaXml xml = new SformulaXml(conn,statid);
				String sformula=null;
				String decimal="2";
				Element element=xml.getFirstElement();
				if(element!=null){
					sformula=element.getAttributeValue("id");
					ttype=element.getAttributeValue("type");
					decimal=element.getAttributeValue("decimalwidth");
					decimal=decimal==null||decimal.length()==0?"2":decimal;
				}else{
					ttype=null;
				}
				
				if(type!=null && "1".equals(type))
				{
					int[] statvalues=null;
					double[] statvaluess=null;
					String[] fieldDisplay; 
					String SNameDisplay;
						
				    StatDataEncapsulation simplestat=new StatDataEncapsulation();
				    simplestat.setWhereIN(sqlwhere);
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
				    	if(sformula==null) {
                            statvalues =simplestat.getLexprData(dbpre, Integer.parseInt(statid), null, userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,exprlexpr,exprfactor,"2","");
                        } else {
                            statvaluess =simplestat.getLexprDataSformula(dbpre, Integer.parseInt(statid), null, userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,exprlexpr,exprfactor,"2","",sformula,conn);
                        }
				    }else{
				    	if(sformula==null) {
                            statvalues =simplestat.getLexprData(dbpre, Integer.parseInt(statid), null, userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,null,null,null,"");
                        } else {
                            statvaluess =simplestat.getLexprDataSformula(dbpre, Integer.parseInt(statid), null, userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,exprlexpr,exprfactor,"2","",sformula,conn);
                        }
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
								 vo.setDataValue(PubFunc.formatDecimals(statvaluess[i],Integer.parseInt(decimal)));
								 datalist.add(vo);
							     statTotal += statvaluess[i];
							}else{
								CommonData vo = new CommonData();
								vo = (CommonData)datalist.get(i);
								vo.setDataName(fieldDisplay[i]);
								double sum = new Double(vo.getDataValue()).intValue();
								vo.setDataValue(PubFunc.formatDecimals(sum+statvaluess[i],Integer.parseInt(decimal)));
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
					 simplestat.setWhereIN(sqlwhere);
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
					    	if(sformula==null) {
                                statValues=simplestat.getDoubleLexprData(Integer.parseInt(statid),dbpre,null,userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,exprlexpr,exprfactor,"2",null);
                            } else {
                                statValuess=simplestat.getDoubleLexprDataSformula(Integer.parseInt(statid),dbpre,null,userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,exprlexpr,exprfactor,"2",null,sformula,conn);
                            }
					    }else{
					    	if(sformula==null) {
                                statValues=simplestat.getDoubleLexprData(Integer.parseInt(statid),dbpre,null,userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,null,null,"",null);
                            } else {
                                statValuess=simplestat.getDoubleLexprDataSformula(Integer.parseInt(statid),dbpre,null,userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,null,null,"",null,sformula,conn);
                            }
					    }
					 if(sformula==null&&statValues==null) {
                         continue;
                     }
					 if(sformula!=null&&statValuess==null) {
                         continue;
                     }
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
										 hvo.setDataValue(PubFunc.formatDecimals(hsum,Integer.parseInt(decimal)));
										 harraydatalist.remove(j);
										 harraydatalist.add(j,hvo);
									 }
									 num+=statValuess[i][j];
								 }
								 vo.setDataValue(PubFunc.formatDecimals(num,Integer.parseInt(decimal)));
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
									 hvo.setDataValue(PubFunc.formatDecimals(hsum,Integer.parseInt(decimal)));
									 harraydatalist.remove(j);
									 harraydatalist.add(j,hvo);
								 }
								 vo = (CommonData)varraydatalist.get(i);
								 double sum = num+(Double.parseDouble(vo.getDataValue()));
								 vo.setDataValue(PubFunc.formatDecimals(sum,Integer.parseInt(decimal)));
								 varraydatalist.remove(i);
								 varraydatalist.add(i,vo);
							 }
						}
					 }
				
				}
	    	}
	    	
	    	
	    	String unit = "人";
		    if(ttype!=null&&!"count".equalsIgnoreCase(ttype)){
		    	unit="";
		    }
		    if(type!=null && "1".equals(type)) {
                bls.buildDataList(datalist,txtfile,unit);
            } else if(type!=null && "2".equals(type)) {
                bls.buildDoubleDataList(varraydatalist,harraydatalist,txtfile,unit);
            }
		    varraydatalist.clear();
		    harraydatalist.clear();
		    datalist.clear();
	    }
	    
	    txtfile.append("</ul>");
	    
	    
	    
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		return txtfile.toString();
	}

	/*加载统计条件项*/
	private ArrayList loadstatlist(String gcond,Connection conn,UserView userView) throws GeneralException 
	{
		ArrayList<CommonData> statlist=new ArrayList<CommonData>();		
		if(gcond==null||gcond.length()<=0) {
            gcond="";
        }
    	String gconds[]=gcond.split(",");
    	ArrayList list =new ArrayList();
    	if(gconds==null||gconds.length<=0) {
            return list;
        }
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
		ContentDAO dao=new ContentDAO(conn);
		RowSet rs = null;
		try{
			int i=0;
		    rs=dao.search(sql.toString());
		    while(rs.next()) {
		    	if((userView.isHaveResource(IResourceConstant.STATICS,rs.getString("id")))) {
		    	    CommonData data=new CommonData();
		    	    data.setDataName(rs.getString("name"));
		    	    data.setDataValue(rs.getString("id"));
		    	   	statlist.add(data);
		    	   	if(i==0) {
                        statid = rs.getString("id");
                    }
			    		
			    	i++;
	    		}		    	
		    }
		    //将统计条件按显示顺序排序
		    ArrayList<CommonData> tempList = new ArrayList<CommonData>();
		    for (int m = 0; m < gconds.length; m++) {
		    	String id = gconds[m];
		    	if(StringUtils.isEmpty(id.trim())) {
                    continue;
                }
		    	
		    	for(int n = 0; n < statlist.size(); n++) {
		    		CommonData data = statlist.get(n);
		    		if(id.equals(data.getDataValue())) {
		    			tempList.add(data);
		    			break;
		    		}
		    	}
		    }
		    
		    statlist = tempList;
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return statlist;
	}
}
