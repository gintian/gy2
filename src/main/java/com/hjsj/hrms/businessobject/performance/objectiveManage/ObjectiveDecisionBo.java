package com.hjsj.hrms.businessobject.performance.objectiveManage;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.struts.upload.FormFile;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:ObjectiveDecisionBo.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 01, 2010 09:15:57 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class ObjectiveDecisionBo {
	
	private Connection con=null;
	private UserView userview=null;	
	private RecordVo planVo = null;
	private String onlyFild2 = "";
	private String onlyFild2Cn = "";
	private String plan_id="";
	private RecordVo template_vo=null;
	HashMap keyMap = new HashMap();
	
	public ObjectiveDecisionBo(Connection a_con,UserView userView)
	{
		this.con=a_con;
		this.userview=userView;
	}
	
	public ObjectiveDecisionBo(Connection a_con,UserView userView,String plan_id)
	{
		this.con=a_con;
		this.userview=userView;
		this.planVo=this.getPerPlanVo(plan_id);
		this.plan_id=plan_id;
		this.template_vo=get_TemplateVo();
		init();
	}
	private void init()
	{	
		//考核对象唯一性指标

		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.con);
		if(this.planVo.getInt("object_type")==2)
		{
			onlyFild2 = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
//			String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
//			if(uniquenessvalid.equals("0"))
//				onlyFild2 ="";
			if(onlyFild2!=null&&onlyFild2.length()!=0)
				onlyFild2Cn=DataDictionary.getFieldItem(onlyFild2).getItemdesc();
		}else
		{
			RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD",this.con);
			if(unit_code_field_constant_vo!=null)
			{
				onlyFild2=unit_code_field_constant_vo.getString("str_value");
				if(onlyFild2!=null&&onlyFild2.length()!=0)
				{
					if(DataDictionary.getFieldItem(onlyFild2)!=null)
			    		onlyFild2Cn=DataDictionary.getFieldItem(onlyFild2).getItemdesc();
				}
			}
			if(this.onlyFild2==null || this.onlyFild2.trim().length()<=0 || "#".equals(this.onlyFild2))
				this.onlyFild2 = "b0110";
			if(this.onlyFild2Cn==null || this.onlyFild2Cn.trim().length()<=0 || "#".equals(this.onlyFild2Cn))
				this.onlyFild2Cn = "组织机构编码";
		}
	}
	public RecordVo get_TemplateVo()
	{
		RecordVo vo=new RecordVo("per_template");
		try
		{
			vo.setString("template_id",this.planVo.getString("template_id"));
			ContentDAO dao = new ContentDAO(this.con);
			vo=dao.findByPrimaryKey(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
	
	public RecordVo getPerPlanVo(String planid)
	{

		RecordVo vo = new RecordVo("per_plan");
		try
		{
			ContentDAO dao = new ContentDAO(this.con);
			vo.setInt("plan_id", Integer.parseInt(planid));
			vo = dao.findByPrimaryKey(vo);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
	/**
     * 取考核计划下的考核对象
     * @param getInPlanObjectDec
     * @return
     */	
	public ArrayList getInPlanObjectDec(String plan_id,ArrayList dbname,String sp_flag,String object_type,UserView userView)
	{
		ArrayList list = new ArrayList();
		ArrayList list1 = new ArrayList();
		RowSet rowSet = null;
		RowSet rs = null;
		try
		{
			if(plan_id!=null && plan_id.length()>0)
			{
				ContentDAO dao = new ContentDAO(this.con);
				String strSql="select DISTINCT per_template_item.kind,p04.b0110,p04.A0100 from p04,per_template_item where p04.item_id=per_template_item.item_id and plan_id="+plan_id+" and per_template_item.kind=2";
				rowSet = dao.search(strSql);							
				while(rowSet.next())
				{								
					String B0110=rowSet.getString("A0100")!=null?rowSet.getString("A0100"):rowSet.getString("b0110");
					String kind=rowSet.getString("kind")!=null?rowSet.getString("kind"):"";
					list1.add(B0110);												
				}
				
				// 获得每一个考核对象的所有考核主体的打分状态
				HashMap obj_mainMap = this.getObject_mainbodyType(plan_id);
				
				StringBuffer sql = new StringBuffer();					
				//登录用户权限范围内考核对象或者是登录用户的考核对象
				StringBuffer objWhl = new StringBuffer(" and (");
				PerformanceImplementBo pb = new PerformanceImplementBo(this.con);
				
				sql.append("select b0110,e0122,e01a1,a0101,object_id,sp_flag,plan_id from per_object where 1=1 ");
				sql.append(" and plan_id ="+plan_id+" "+pb.getPrivWhere(userView)+" ");
				if("-1".equals(sp_flag) || sp_flag.length()<=0)
					sql.append(" and 1=1");				
				else if("01".equals(sp_flag))
		    		sql.append(" and (sp_flag='"+sp_flag+"' or sp_flag is null)");
				else
					sql.append(" and sp_flag='"+sp_flag+"'");
				
				sql.append(" order by A0000");									
					
				rs = dao.search(sql.toString());
				ArrayList dbnameList = new ArrayList();
				dbnameList.add("Usr");				
				while(rs.next())
				{
					String planid=rs.getString("plan_id");
					String object_id = rs.getString("object_id");
					for(int i=0;i<dbname.size();i++)
					{				
						LazyDynaBean bean = new LazyDynaBean();
						if("2".equals(object_type)){
							bean.set("b0110",AdminCode.getCodeName("UN",rs.getString("b0110")));
							
						}else{
							String b0110 = AdminCode.getCodeName("UN",rs.getString("object_id"));
		                    if(b0110==null|| "".equals(b0110))
		                    	b0110=AdminCode.getCodeName("UM",rs.getString("object_id"));
		        	        bean.set("b0110",b0110);
						}
						bean.set("e0122",AdminCode.getCodeName("UM",rs.getString("e0122")));
						bean.set("e01a1",AdminCode.getCodeName("@K",rs.getString("e01a1")));
						String spf=rs.getString("sp_flag");
							
						if(spf==null)
							spf="01";
						String spFlagDesc=MyObjectiveBo.getSpflagDesc(spf);//AdminCode.getCodeName("23",spf);
	    					
						bean.set("sp_flag", spFlagDesc);
						bean.set("sp", spf);						                      
						bean.set("a0101",rs.getString("a0101"));
						bean.set("planid",rs.getString("plan_id"));
						bean.set("a0100",rs.getString("object_id"));
						bean.set("mda0100", PubFunc.encryption(rs.getString("object_id")));
						bean.set("mdplanid",PubFunc.encryption(rs.getString("plan_id")));
						bean.set("object_type",object_type);
						
						String editCard = (String)obj_mainMap.get(object_id);
						if(!"03".equals(spf)&&(editCard==null || (editCard!=null && "0".equals(editCard))))
							bean.set("editCard","true");
						else
							bean.set("editCard","false");
						
						if(list1.size()<=0){						
							bean.set("object_id","0");
						}else{
							for(int j=0;j<list1.size();j++){
								String value = (String)list1.get(j);
					       		if(value!=null && value.length()>0)
					       		{
					       			if(value.equalsIgnoreCase(object_id))
					       			{
						       			bean.set("object_id","1");
						       			break;
					       			}
					       			else{
						       			bean.set("object_id","0");
						       			continue;
					       			}
					       		}
							}
						}
						list.add(bean);					
					}
				}
			}
			if(rs!=null)
				rs.close();
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} 
		return list;
	}
	
	// 获得每一个考核对象的所有考核主体的打分状态
	public HashMap getObject_mainbodyType(String plan_id)
	{
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.con);
		RowSet rowSet = null;
		try{
			
			StringBuffer sql = new StringBuffer();						
			sql.append("select object_id,status from per_mainbody where plan_id='"+plan_id+"' GROUP BY object_id,status order by object_id,status ");															
			rowSet = dao.search(sql.toString());
			while(rowSet.next())
			{	
				String object_id = rowSet.getString("object_id");
				String status = rowSet.getString("status");
				if(status==null || status.trim().length()<=0)
					status = "0";
				map.put(object_id,status);				
			}
			if(rowSet!=null)    		
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}	
	
	private String getCellValue(Cell cell) 
	{
		String result = "";
		switch (cell.getCellType()) 
		{
			case HSSFCell.CELL_TYPE_BLANK:
			result = "";
			break;
			case HSSFCell.CELL_TYPE_STRING:
			result = cell.getRichStringCellValue().getString();
			break;
			case HSSFCell.CELL_TYPE_NUMERIC:
			result = String.valueOf(cell.getNumericCellValue());
			break;
			case HSSFCell.CELL_TYPE_FORMULA:
			result = "";
			break;
			case HSSFCell.CELL_TYPE_BOOLEAN:
			result = String.valueOf(cell.getBooleanCellValue());
			break;
			case HSSFCell.CELL_TYPE_ERROR:
			result = "";
			break;
			default:
			result = cell.getRichStringCellValue().getString();
		}
		return result;
	}
	
	
	public void importData(FormFile form_file,ArrayList conctorList) throws GeneralException
	{
		if(this.onlyFild2!=null && this.onlyFild2.trim().length()>0 && !"#".equals(this.onlyFild2))
		{
			FieldItem fielditem = DataDictionary.getFieldItem(this.onlyFild2);
			String useFlag = fielditem.getUseflag(); 
			if("0".equalsIgnoreCase(useFlag))
				throw new GeneralException("定义的唯一性指标未构库,请构库后再进行此操作！");	
		}
		
		Workbook wb = null;
		Sheet sheet = null;
				
		/**获取模板的所有项目编号*/
		this.keyMap=getItem_idList();
		LazyDynaBean abean=null;
		InputStream in = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.con);	
			RowSet rs = null;
			String sql ="";			
			HashMap pointsMap = new HashMap();
			HashMap pointMapIgnoreCase = new HashMap(); // 将upper(point_id)作为key，point_id作为value
			sql = "select point_id,pointname from per_point ";
			rs = dao.search(sql);
			while(rs.next()) {	
				pointsMap.put(rs.getString(1).toUpperCase(), rs.getString(2));
				
				String point_id = rs.getString("point_id");
				point_id = point_id == null ? "" : point_id;
				pointMapIgnoreCase.put(point_id.toUpperCase(), point_id);
			}
			in = form_file.getInputStream();
			wb = WorkbookFactory.create(in);
			for(int i=0;i<wb.getNumberOfSheets();i++)
			{
				HashMap key_Map=new HashMap();
				sheet = wb.getSheetAt(i);	
				String sheetname = sheet.getSheetName();
				Row row = sheet.getRow(0);
				if(row==null)
					throw new GeneralException("请用下载的模板导入考核对象["+sheetname+"]的目标数据！");
				Cell cell = row.getCell(0);
				boolean errorFlag=false;
				if(cell==null || (cell.getCellComment())==null)
					throw new GeneralException("请用下载的模板导入考核对象["+sheetname+"]的目标数据！");
				String onlyFild=cell.getCellComment().getString().getString().trim();
				String onlyFildCn=getCellValue(cell);
				if(!onlyFild.equalsIgnoreCase(onlyFild2))
					errorFlag=true;				
					
				cell = row.getCell(1);
				String object_id="";
				String onlyFildValue=getCellValue(cell);		
				
				for(int k=0;k<conctorList.size();k++)
				{
					abean=(LazyDynaBean)conctorList.get(k);
					String only_id=(String)abean.get(onlyFild2);
					if(only_id.equalsIgnoreCase(onlyFildValue))					
						break;								
					if(k==(conctorList.size()-1))
					{
						onlyFildValue="";						
					}
				}
				if(onlyFildValue==null || onlyFildValue.length()<=0){
					//continue;导入模板时，唯一性指标为空就跳过了，前台还提示导入成功，造成误导。现直接抛出提示。chent 20151224
					throw new GeneralException("【"+sheetname+"】的唯一性指标为空，导入不成功，请维护唯一性指标【"+onlyFild2Cn+"】！");
				}
				row = sheet.getRow(1);
				cell = row.getCell(0);
				if(cell==null || (cell.getCellComment())==null)
					throw new GeneralException("请用下载的模板导入考核对象["+sheetname+"]的目标数据！");
				String x=cell.getCellComment().getString().getString().trim();
				cell = row.getCell(1);
				String object_name=getCellValue(cell);
				if(this.planVo.getInt("object_type")==2)
				{
					if(!"a0101".equalsIgnoreCase(x))
						errorFlag=true;	
				}else
				{
					if(!"CODEITEMDESC".equalsIgnoreCase(x))
						errorFlag=true;	
				}				
				
				String b0110 = "";
				String e0122 = "";
				String e01a1 = "";
				String a0101 = "";
				
				//zxj 20141112 根据唯一性指标取对象id并校验唯一性指标重复情况
				String objectId = getObjectIdByOnlyField(dao, onlyFild, onlyFildValue, this.planVo.getInt("object_type"));
				
				//只是导入起草和驳回状态的目标卡
				String sp_flag = "";
				sql = "select * from per_object where plan_id="+this.planVo.getString("plan_id")+" and object_id='" + objectId + "'";
                
				/*zxj 20141112 唯一性指标重复会报错
				if(this.planVo.getInt("object_type")==2)
					sql+="(select a0100 from usra01 where "+onlyFild+"='"+onlyFildValue+"')";
				else
					sql+="(select b0110 from b01 where "+onlyFild+"='"+onlyFildValue+"')";
                */
				
				rs = dao.search(sql);
				String noHave="no";
				if(rs.next())
				{
					sp_flag = rs.getString("sp_flag")==null?"01":rs.getString("sp_flag");
					object_id = rs.getString("object_id")==null?"":rs.getString("object_id");
					b0110 = rs.getString("b0110") == null ? "" : rs.getString("b0110");
					e0122 = rs.getString("e0122") == null ? "" : rs.getString("e0122");
					e01a1 = rs.getString("e01a1") == null ? "" : rs.getString("e01a1");
					a0101 = rs.getString("a0101") == null ? "" : rs.getString("a0101");
					noHave="have";
				}
				
				if("no".equalsIgnoreCase(noHave))
					throw new GeneralException("考核对象("+object_name+")的("+onlyFildCn+")输入错误！");
				
				if(!"01".equals(sp_flag)&&!"07".equals(sp_flag))
					continue;
				
				//找到数据列名
				row = sheet.getRow(2);
				int cols = row.getPhysicalNumberOfCells();
				HashMap colMap = new HashMap();
				HashMap codeColMap = new HashMap();
				int p0407Col=0;
				StringBuffer codeBuf = new StringBuffer();
				for (int j = 0; j < cols; j++)
				{
					String colname = "";
				    cell = row.getCell((short) j);
					if (cell != null)
					{
						switch (cell.getCellType())
						{
							case Cell.CELL_TYPE_STRING:
								if (cell.getCellComment() == null)
									throw new GeneralException("请设置列[" + cell.getStringCellValue() + "]的批注！");
								else
									colname = cell.getCellComment().getString().getString().trim();
								break;
							default:
								colname = "";
						}					
						if("p0407".equalsIgnoreCase(colname))
							p0407Col=j;
						if("".equals(colname)||p0407Col==0)
							continue;
						if(DataDictionary.getFieldItem(colname)==null)
						{
							errorFlag=true;	
							break;
						}
													
						String codesetid = DataDictionary.getFieldItem(colname).getCodesetid();
						if (!"0".equals(codesetid))
						{
							if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equals(codesetid))
							{
								codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='" + codesetid + "'  and codeitemid=childid  union all ");
							} else
							{
								codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='" + codesetid+ "' union all ");
							}
						}
						
						colMap.put(new Integer(j), colname+":"+cell.getStringCellValue());
					}
				}
				if (codeBuf.length() > 0)
				{
					codeBuf.setLength(codeBuf.length() - " union all ".length());

					rs = dao.search(codeBuf.toString());
					while (rs.next())
					{
						if (!"UM".equalsIgnoreCase(rs.getString("codesetid")) && !"UN".equalsIgnoreCase(rs.getString("codesetid")) && !"@K".equalsIgnoreCase(rs.getString("codesetid")))
							codeColMap.put(rs.getString("codesetid") + "a04v2u" + rs.getString("codeitemid") + ":" + rs.getString("codeitemdesc"), rs.getString("codeitemid"));
						else
							codeColMap.put(rs.getString("codesetid") + "a04v2u" + rs.getString("codeitemid") + ":" + rs.getString("codeitemdesc"), rs.getString("codeitemid"));
					}
				}
				if(errorFlag)
					throw new GeneralException("请用下载的模板导入考核对象["+sheetname+"]的目标数据！");
				
				//先删除原来的目标卡
			    sql = "delete from p04 where plan_id="+this.planVo.getString("plan_id")+" and ";
				if(this.planVo.getInt("object_type")==2)
					sql+="a0100='";
				else
					sql+="b0110='";
				sql+=object_id+"' ";
				dao.delete(sql, new ArrayList());
				//数据行
				int rows = sheet.getPhysicalNumberOfRows();
				for (int j = 3; j < rows; j++)
				{
					row = sheet.getRow(j);
					if(row==null)
						continue;
					
					RecordVo vo = new RecordVo("P04");
					IDGenerator idg = new IDGenerator(2, this.con);
					String id = idg.getId("P04.P0400");
					vo.setInt("p0400", Integer.parseInt(id));
					vo.setInt("seq", this.getSeq(object_id));
					if("07".equals(sp_flag))
						vo.setInt("processing_state", 1);
					vo.setInt("plan_id", this.planVo.getInt("plan_id"));
					if (this.planVo.getInt("object_type") == 2)
					{
						vo.setString("b0110", b0110);
						vo.setString("e0122", e0122);
						vo.setString("e01a1", e01a1);
						vo.setString("nbase", "USR");
						vo.setString("a0100", object_id);
					} else
					{
						vo.setString("b0110", object_id);
					}
					vo.setString("a0101", a0101);					
					
					boolean hasTheContent = false;
					for (int k = p0407Col; k < cols; k++)
					{
						cell = row.getCell((short) k);
						String fieldItems = (String) colMap.get(new Integer(k));
						if (fieldItems == null)
							continue;
						String[] fieldItem = fieldItems.split(":");
						String field = fieldItem[0];
						String fieldName = fieldItem[1];
						String itemtype = DataDictionary.getFieldItem(field).getItemtype();
						String codesetid = DataDictionary.getFieldItem(field).getCodesetid();
						int decwidth = DataDictionary.getFieldItem(field).getDecimalwidth();
						if("A".equalsIgnoreCase(itemtype) || "M".equalsIgnoreCase(itemtype)){//郭峰增加  因为如果KPI指标输入123等数字型的值，程序就会当做数字来处理，那么KPI指标就会导入不进去。
							int liying = HSSFCell.CELL_TYPE_STRING;
							if(cell!=null)
								cell.setCellType(liying);
						}
						String value = "";
						if (cell != null)
						{
							switch (cell.getCellType())
							{
							case Cell.CELL_TYPE_FORMULA:
								break;
							case Cell.CELL_TYPE_NUMERIC:
								double y2 = cell.getNumericCellValue();
								value = Double.toString(y2);
								if (value.indexOf("E") > -1)
								{
									String x1 = value.substring(0, value.indexOf("E"));
									String y1 = value.substring(value.indexOf("E") + 1);

									value = (new BigDecimal(Math.pow(10, Integer.parseInt(y1.trim()))).multiply(new BigDecimal(x1))).toString();
								}
							    if ("N".equals(itemtype))
							    {	
							    	int num=(value.length()-1)-value.indexOf(".");
							    	if(num>decwidth)
							    	{
							    		value=PubFunc.round(value, decwidth);
							    	}							    								    	
							    	if(decwidth==0)
							    	{							    		
							    		vo.setInt(field, new Integer(value).intValue());
							    	}									
									else
										vo.setDouble(field, new Double(value).doubleValue());
							    }
								else if ("D".equals(itemtype))
								{
									value = changeNumToDate(value);
									vo.setDate(field, java.sql.Date.valueOf(value));
								}
								break;
							case Cell.CELL_TYPE_STRING:
								value = cell.getRichStringCellValue().toString();
								String point_id="";
								if(value.trim().length()>0 && (cell.getCellComment())!=null)
			   					{
									point_id=cell.getCellComment().getString().getString().trim();
			   					}								
								if("p0407".equalsIgnoreCase(field))
								{
									//取得所属的项目编号
									String itemid = getItemCell(sheet,j,k,a0101);
									if(itemid!=null && itemid.trim().length()>0)
									{
										if("emptyItem".equalsIgnoreCase(itemid))
										{
											
										}
										else
											vo.setInt("item_id", Integer.parseInt(itemid));
									}
									
									if(value.trim().length()>0)
										hasTheContent=true;
									int fromflag=2;
									if(point_id!=null && point_id.length()>0)
									{
										String pointid = point_id;
										if(pointsMap.get(pointid.toUpperCase())!=null)
										{
											if(key_Map.get(pointid)==null)
											{
												fromflag=2;
												vo.setString("p0401", (String) pointMapIgnoreCase.get(pointid.toUpperCase()));
												vo.setString("p0407", value);
												key_Map.put(pointid, "");
											}else{
												throw new GeneralException("考核对象["+a0101+"]的Excel模板中存在批注相同的考核指标，<br>不予导入，请重新整理后，再进行导入！");
											}											
										}else
											fromflag=1;
									}else{//如果考核指标不存在
										//先判断项目是共性的还是个性的。如果是共性的，那么模板错误。
										StringBuffer sb = new StringBuffer("");
										if(StringUtils.isEmpty(itemid))
											itemid = "-100";
										sb.append("select * from per_template_item where item_id="+itemid+" and kind=1");//kind=1说明是共性的
										RowSet temprs = null;
										temprs = dao.search(sb.toString());
										if(temprs.next()){//说明是共性项目
											throw new GeneralException("考核对象["+a0101+"]的Excel模板中共性项目增加了考核指标("+value+")，<br>不予导入，请重新整理后，再进行导入！");
										}else{
											fromflag=1;
										}
										if(temprs!=null)
											temprs.close();
									}
									vo.setInt("fromflag", fromflag);
									if(fromflag==1)
									{
										vo.setString("p0407", value);
										vo.setString("p0401", id);
									}									
									
								}else//如果不是p0407
								{
									if("A".equals(itemtype)|| "M".equalsIgnoreCase(itemtype))
									{
										if (!"0".equals(codesetid) && !"".equals(codesetid))
										{
											if (!"0".equals(codesetid) && !"".equals(codesetid))
												if (codeColMap.get(codesetid + "a04v2u" + value.trim()) != null)
													value = (String) codeColMap.get(codesetid + "a04v2u" + value.trim());
										}
										vo.setString(field, value);	
									}else if("D".equals(itemtype) && value.length()>0)
									{
										if (!this.isDataType(decwidth, itemtype, value))
										{
											String msg = "源数据(" + fieldName + ")中数据:" + value + " 不符合格式!";
											throw new GeneralException(msg);
										}	
										value = PubFunc.replace(value, ".", "-");
										vo.setDate(field, java.sql.Date.valueOf(value));
									}else  if ("N".equals(itemtype))
								    {
										if (!this.isDataType(decwidth, itemtype, value))
										{
											String msg = "源数据(" + fieldName + ")中数据:" + value + " 不符合格式!";
											throw new GeneralException(msg);
										}
										value=value.length()==0?"0":value;
								    	if(decwidth==0)
											vo.setInt(field, new Integer(PubFunc.round(value, 0)).intValue());
										else
											vo.setDouble(field, new Double(value).doubleValue());
								    }										
								}															
								break;
							case Cell.CELL_TYPE_BLANK:// 如果什么也不填的话数值就默认更新为0
								if ("N".equals(itemtype))
								{
									if(decwidth==0)
										vo.setInt(field, 0);
									else
										vo.setDouble(field, 0);
								} 
								break;
							default:
								
							}

							String msg = "";
							if ("N".equals(itemtype) || "D".equals(itemtype))
							{
								if (!this.isDataType(decwidth, itemtype, value))
								{
									msg = "源数据(" + fieldName + ")中数据:" + value + " 不符合格式!";
									throw new GeneralException(msg);
								}
							}
						}
					}
					
					if("0".equals(this.template_vo.getString("status")))  //分值
					{
						vo.setDouble("p0415",1);
					}
					else if("1".equals(this.template_vo.getString("status"))) //权重
					{
						vo.setDouble("p0413",this.template_vo.getDouble("topscore")); 
					}
						
					if(hasTheContent)
						dao.addValueObject(vo);
				}
			}			
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeIoResource(in);
			PubFunc.closeResource(wb);
		}
	}
	/**
	 * 判断 值类型是否与 要求的类型一致
	 * 
	 * @param columnBean
	 * @param itemid
	 * @param value
	 * @return
	 */
	public boolean isDataType(int decwidth, String itemtype, String value)
	{

		boolean flag = true;
		if(value.trim().length()==0)
			return flag;
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
			flag = value.matches("[0-9]{4}[#-.][0-9]{2}[#-.][0-9]{2}");
		}
		return flag;
	}
	public int getSeq(String object_id)
	{
		int seq = 1;
		ContentDAO dao = new ContentDAO(this.con);
		String sql = "select " + Sql_switcher.isnull("max(seq)", "0") + "+1 from p04 where a0100='" + object_id + "' and plan_id=" + planVo.getString("plan_id");
		if (planVo.getInt("object_type") == 1 || planVo.getInt("object_type") == 3 || planVo.getInt("object_type") == 4)
			sql = "select " + Sql_switcher.isnull("max(seq)", "0") + "+1 from p04 where b0110='" + object_id + "' and plan_id=" + planVo.getString("plan_id");
		try
		{
			RowSet rowSet = dao.search(sql);
			if (rowSet.next())
			{
				seq = rowSet.getInt(1);
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return seq;
	}
    public static String changeNumToDate(String s)
    {

	String rtn = "1900-01-01";
	try
	{
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    java.util.Date date1 = new java.util.Date();
	    date1 = format.parse("1900-01-01");
	    long i1 = date1.getTime();

	    // 这里要减去2，(Long.parseLong(s)-2) 不然日期会提前2天，具体原因不清楚，

	    // 估计和java计时是从1970-01-01开始有关
	    // 而Excel里面的计算是从1900-01-01开始
	    i1 = i1 / 1000 + ((Long.parseLong(s) - 2) * 24 * 3600);
	    date1.setTime(i1 * 1000);
	    rtn = format.format(date1);
	} catch (Exception e)
	{
	    rtn = "1900-01-01";
	}
	return rtn;

    }
    public String  getItemCell(Sheet sheet,int pointRow,int pointCol,String a0101) throws GeneralException
    {
    	String item_id="";
    	int countqq = sheet.getNumMergedRegions();//找到当前sheet单元格中共有多少个合并区域   
    	pointCol--;
        for(;pointCol>=0;pointCol--)
        {
        	//如果单元格是合并区域，则取合并区域的值
   		 	for(int n = 0; n<countqq; n++)
   		 	{   	         
   		 		CellRangeAddress range = sheet.getMergedRegion(n);//一个合并单元格代表 CellRangeAddress   
   		 		int row1=range.getFirstRow();
   		 		int col1=range.getFirstColumn();
   		 		int row2=range.getLastRow();
   		 		int col2=range.getLastColumn();
   		 		if(row1<=pointRow && col1==pointCol && row2>=pointRow)
   		 		{	//合并区域
   					Row row = sheet.getRow(row1);
   					Cell cell = row.getCell(col1);
   					item_id = getCellItemId(cell,a0101);
   					if(!StringUtils.isEmpty(item_id)
   						 && !"emptyItem".equals(item_id)) {
   		    			return item_id;
   		    		}
   		 		}
   		 	}
   		 	//单元格不是合并区域，则直接取单元格的值，如果这个单元格的itemid没有值，则继续取前一个单元格的值
	 		Row row = sheet.getRow(pointRow);
        	Cell cell = row.getCell(pointCol);
        	if(cell !=null){
				item_id = getCellItemId(cell,a0101);
				if(!StringUtils.isEmpty(item_id)
						&& !"emptyItem".equals(item_id)) {
					return item_id;
				}
			}
        }
	 	return item_id;
    }
    private String getCellItemId(Cell cell,String a0101) throws GeneralException {
    	String value="";
		String point_id="";
		String item_id="";
    	switch (cell.getCellType())
		{
			case Cell.CELL_TYPE_FORMULA:
				break;					
			case Cell.CELL_TYPE_STRING:
				value = cell.getRichStringCellValue().toString();
				String comment = "";
				if(value.trim().length()>0 && (cell.getCellComment())==null){
					int tempIndex=value.lastIndexOf("itemid:")+6;
					if(tempIndex==-1){
						throw new GeneralException("请设置项目[" + cell.getStringCellValue() + "]的批注！");
					}else{
						comment = value.substring(tempIndex+1, value.length()-1);
						if("".equals(comment))
							throw new GeneralException("请设置项目[" + cell.getStringCellValue() + "]的批注！");
					}
				}
					
					
				if((value.trim().length()>0 && (cell.getCellComment())!=null) || comment.length()>0)
				{
					if(cell.getCellComment()!=null){
						comment = cell.getCellComment().getString().getString().trim();
					}
					point_id=comment;
					if(this.keyMap.get(point_id)==null)
					{
						throw new GeneralException("考核对象["+a0101+"]的Excel模板中[" + cell.getStringCellValue() + "]的项目编号（即项目批注）不对，请检查！");
					}
				}
				
				if(value.trim().length()<=0 && (cell.getCellComment())==null && comment.length()<=0)
					item_id="emptyItem";
				break;
			default:						
		}
    	if(point_id!=null && point_id.trim().length()>0)
		{
			item_id = point_id;
		}   
    	return item_id;
    }
    public HashMap getItem_idList()
	{
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.con);
		RowSet rowSet = null;
		try{
			
			StringBuffer sql = new StringBuffer();						
			sql.append("select item_id from per_template_item where template_id='"+this.planVo.getString("template_id")+"'");															
			rowSet = dao.search(sql.toString());
			while(rowSet.next())
			{	
				if(map.get(rowSet.getString("item_id"))==null)
				{
					map.put(rowSet.getString("item_id"),"");
				}
			}
			if(rowSet!=null)    		
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
    
    //获取对象目标卡信息
	public ArrayList getKh_objectList(ArrayList personList)
	{
		ArrayList list = new ArrayList();
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			LazyDynaBean bean=null;
			
			for(int k=0;k<personList.size();k++)
			{
				bean=(LazyDynaBean)personList.get(k);
				String object_id=(String)bean.get("a0100");
				
				if((onlyFild2!=null) && (onlyFild2.trim().length()>0) && (!"#".equalsIgnoreCase(onlyFild2)))
				{
					if(this.planVo.getInt("object_type")==2)			
						rowSet=dao.search("select "+onlyFild2+" from usrA01 where A0100='"+object_id+"'");
					else										
						rowSet=dao.search("select "+onlyFild2+" from b01 where b0110='"+object_id+"'");				
							
					LazyDynaBean abean=null;
					while(rowSet.next())
				    {				
						abean=new LazyDynaBean();									
					    abean.set(onlyFild2,rowSet.getString(onlyFild2)!=null?rowSet.getString(onlyFild2):"");
				    	list.add(abean);		    	
				    }
				}
			}
			if(rowSet!=null)    		
				rowSet.close();			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}				
		return list;
	}
	
	// 出现"评分细则"按钮的限制条件  JinChunhai  2011.03.16
	public String ScoreManual(Connection con,String planid,String object_id,UserView userView,String model,String body_id,String opt)
	{
		String flag="false";
		RowSet rs = null;
		try
		{
			ContentDAO dao  = new ContentDAO(this.con);
			
			// 判断业务字典"目标卡任务表"中的 score_org (考核机构)字段 是否设置为"显示"状态
			String showState="no";
			ArrayList list = DataDictionary.getFieldList("P04", Constant.USED_FIELD_SET);
			for (int i = 0; i < list.size(); i++)
			{
			    FieldItem item = (FieldItem) list.get(i);
			    String itemid = item.getItemid();
			    if("score_org".equalsIgnoreCase(itemid))
			    {
			    	String state=item.getState();
			    	if("1".equalsIgnoreCase(state))
			    	{
			    		showState="yes";
			    	}
			    	break;
			    }
			}
			
			// 判断"计划参数"中的 "允许查看下级对考核对象评分"参数 是否选中
			LoadXml loadxml = new LoadXml(this.con, planid);
			Hashtable params = loadxml.getDegreeWhole();			
			String allowSeeLowerGrade = (String)params.get("allowSeeLowerGrade");
			
			// 判断"计划参数"中的 "目标卡指标"参数 是否选中，若选中就按此参数走；没选中就按"参数设置"中的"目标卡指标"走
			String check="noCheck";
			String targetDefineItem = "";
			String targetTraceEnabled = (String)params.get("TargetTraceEnabled");
			if("true".equalsIgnoreCase(targetTraceEnabled))
			{
				targetDefineItem = (String) params.get("TargetDefineItem");
				if(targetDefineItem!=null && targetDefineItem.trim().length()>0)
				{
					String[] items = targetDefineItem.split(",");
					for (int i = 0; i < items.length; i++)
					{
						if("score_org".equalsIgnoreCase(items[i]))
						{
							check="checked";
							break;
						}					
					}
				}				
			}else
			{
				rs = dao.search("select str_value from constant where constant='PER_PARAMETERS'");
				while( rs.next())
			    {
			    	String str_value = rs.getString("str_value");
					if (str_value == null || (str_value != null && "".equals(str_value)))
					{
	
					}else
					{
					    Document doc = PubFunc.generateDom(str_value);
					    String xpath = "//Per_Parameters";
					    XPath xpath_ = XPath.newInstance(xpath);
					    Element ele = (Element) xpath_.selectSingleNode(doc);
					    Element child;
					    
					    child = ele.getChild("TargetDefineItem");
						if (child != null)			
							targetDefineItem = child.getTextTrim();	
					    
						if(targetDefineItem!=null && targetDefineItem.trim().length()>0)
						{
							String[] items = targetDefineItem.split(",");
							for (int i = 0; i < items.length; i++)
							{
								if("score_org".equalsIgnoreCase(items[i]))
								{
									check="checked";
									break;
								}					
							}
						}
					}
			    }
			}
			
			// 判断当前登录用户是否有下级人员	
			String subordinate = "noHave";
			ObjectCardBo obo=new ObjectCardBo(con,planid,object_id,userView,model,body_id,opt);
			ArrayList alist=obo.getLowerGradeList(Integer.parseInt(body_id),object_id,Integer.parseInt(planid));
			if(alist.size()>0)	
				subordinate = "have";
			
			
			if(("yes".equalsIgnoreCase(showState)) && ("true".equalsIgnoreCase(allowSeeLowerGrade)) && ("checked".equalsIgnoreCase(check)) &&("have".equalsIgnoreCase(subordinate)))
			{
				flag="true";
				return flag;		       			 							
			}
			
			if(rs!=null)							
				rs.close();							
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return flag;
	}
	
	/**
	 * 根据唯一性指标从库中查考核对象的编号（人员A0100或机构b0110)
	 * @Title: getObjectIdByOnlyField   
	 * @Description: 唯一性指标重复将抛出异常信息，前台提示明确的重复信息。   
	 * @param dao
	 * @param onlyField
	 * @param onlyFieldValue
	 * @param objectType
	 * @return
	 * @throws GeneralException
	 * @author zhaoxj 20141112
	 */
	private String getObjectIdByOnlyField(ContentDAO dao, String onlyField, 
	        String onlyFieldValue, int objectType) throws GeneralException {
	    String objectId = "";
	    
	    int i = 0;
	    String objNames = "";
	    RowSet rs = null;
	    try {
	        StringBuilder sql = new StringBuilder();
	        sql.append("SELECT");
	        if (2 == objectType)
	            sql.append(" A0100 objid,A0101 objname FROM UsrA01");
	        else {
	            sql.append(" b0110 objid,codeitemdesc objname FROM B01 Left join organization");
	            sql.append(" ON B01.B0110=organization.codeitemid");
	        }
	        
	        sql.append(" WHERE " + onlyField + "=?");
	        
	        ArrayList params = new ArrayList();
	        params.add(onlyFieldValue);
	        
	        rs = dao.search(sql.toString(), params);
	        while (rs.next()) {
	            i++;
	            objectId = rs.getString("objid");
	            
	            //取前10个重复项，其它的不取，客户根据提示的唯一性指标值能够查出全部重复人员或机构
	            if (i <= 10)
	                objNames = objNames + "，" + rs.getString("objname");
            }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (null != rs)
	                rs.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    
	    if (i > 1) {
	        StringBuilder errorInfo = new StringBuilder("以下");
	        if (2 == objectType)
	            errorInfo.append("人员");
	        else
	            errorInfo.append("组织");
	        
	        FieldItem onlyItem = DataDictionary.getFieldItem(onlyField);
	        if (null != onlyItem)
	            errorInfo.append(onlyItem.getItemdesc());
	        errorInfo.append("").append(onlyFieldValue).append("重复，请处理：<br>");
	        errorInfo.append(objNames.substring(1));
	        if (i > 10)
	            errorInfo.append("......等。");
	        
	        throw new GeneralException(errorInfo.toString());
	    }
	    
	    return objectId;
	}
	
}
