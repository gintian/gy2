package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hjsj.hrms.businessobject.general.template.TemplateListBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>
 * Title:ExportExcelTrans.java
 * </p>
 * <p>
 * Description:人事异动导入模板数据
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-04-08 13:00:00
 * </p>
 * 
 * @author xiegq
 * @version 1.0
 * 
 */
public class GetTemplDataTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String tabid = (String) hm.get("tabid");

	FormFile form_file = (FormFile) getFormHM().get("file");
	
	int num =0;
	String table_name=(String)this.getFormHM().get("table_name");
	String operationtype = (String)this.getFormHM().get("operationtype");
	TemplateListBo bo=new TemplateListBo(this.getFormHM().get("tabid").toString(),this.getFrameconn(),this.userView);
	bo.setIsMobile(0);//liuyz 导入过滤手机页
	ArrayList allCellList=bo.getAllCells();
	ArrayList templateSetList=(ArrayList) allCellList.get(0);
	ArrayList noPrintList=(ArrayList) allCellList.get(1);
	String existtarget =",";
	for(int i =0;i<templateSetList.size();i++){
		LazyDynaBean bean =	(LazyDynaBean)templateSetList.get(i);
		if("0".equals(bean.get("isvar")))
		existtarget+=bean.get("field_name")+"_"+bean.get("chgstate")+",";
		else
			existtarget+=bean.get("field_name")+",";	
	}
	
	int infor_type = bo.getBo().getInfor_type();
	String onlyname = "";
	String valid = "";
	if(infor_type==1){//对人员处理的业务模板
		Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
    	 onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
    	 valid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
    	 if("0".equals(valid)){
    		 onlyname="no";
    	 }
	}else if(infor_type==2){
		RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD",this.getFrameconn());
		if(unit_code_field_constant_vo!=null)
		{
		    onlyname=unit_code_field_constant_vo.getString("str_value");
		}	
	}else if(infor_type==3){
		RecordVo pos_code_field_constant_vo=ConstantParamter.getRealConstantVo("POS_CODE_FIELD",this.getFrameconn());
		if(pos_code_field_constant_vo!=null)
		{
		    onlyname=pos_code_field_constant_vo.getString("str_value");
		}
	}
	//循环不打印页上的指标，看是否存在唯一性指标。
    for (int i = 0; i < noPrintList.size(); i++) {
        LazyDynaBean bean = (LazyDynaBean) noPrintList.get(i);
        if(onlyname.equalsIgnoreCase(String.valueOf(bean.get("field_name")))){
            if ("0".equals(bean.get("isvar")))
                existtarget += bean.get("field_name") + "_" + bean.get("chgstate") + ",";
            else
                existtarget += bean.get("field_name") + ",";
        }
    }
	StringBuffer sql = new StringBuffer();
	sql.append("update " + table_name + " set ");
	StringBuffer insertsql = new StringBuffer();
	StringBuffer tempinsertstr = new StringBuffer();
	insertsql.append("insert into " + table_name + "(  ");
	int updateFidsCount = 0;// 将要更新的字段数目
//	HSSFWorkbook wb = null;
//	HSSFSheet sheet = null;
	Workbook wb = null;
	Sheet sheet = null;
	StringBuffer errorStr=new StringBuffer();
	TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
	 HashMap cell_param_map=tablebo.getModeCell4();
	InputStream inputStream = null;
	try
	{
		boolean isFileTypeEqual=FileTypeUtil.isFileTypeEqual(form_file);
		if(!isFileTypeEqual){
			throw new GeneralException(ResourceFactory.getProperty("error.fileuploaderror"));
		}
    	String onlyflag = "0";//0表示原有逻辑，1表示唯一指标
    	String fieldset_2 ="";//变化后子集
    	HashMap fieldsetmap = new HashMap();
    	String cname ="";//模板名称
    	String excelfields = "";
    	ContentDAO dao = new ContentDAO(this.frameconn);
    	this.frowset =dao.search("select name from template_table where tabid="+tabid);
    	if(this.frowset.next())
    	 cname =this.frowset.getString("name");
    	
    	
    	inputStream = form_file.getInputStream();
		 wb = WorkbookFactory.create(inputStream);
		 int sheetsnum = wb.getNumberOfSheets();
		 HashMap sheetnameMap = new HashMap();//key:sheet名字，value：sheet的位置
		 ArrayList sheetlist = new ArrayList();
		 sheetlist.add(cname);
		 String tasklist_str=(String)this.getFormHM().get("tasklist_str");
		 ArrayList tasklist=new ArrayList();
			if(tasklist_str.length()>0)
			{
				String[] temp=tasklist_str.split(",");
				for(int i=0;i<temp.length;i++)
				{
					if(temp[i]==null||temp[i].length()==0)
						continue;
					tasklist.add(temp[i]);
					
				}
			}
		 HashMap fieldPrivByNode=new HashMap();
			if(tasklist.size()>0)
			{
				fieldPrivByNode=tablebo.getFieldPriv((String)tasklist.get(0),this.getFrameconn());
			}
			
		 for(int i=0;i<sheetsnum;i++){
			 String sheetname = wb.getSheetName(i);
			 if(sheetname.indexOf("(t_")!=-1&&sheetname.indexOf("_2)")!=-1){
				if(sheetname.indexOf("_2)")>sheetname.indexOf("(t_")&&sheetname.indexOf("_2)")==sheetname.indexOf("(t_")+6){
				 sheetname = sheetname.substring(sheetname.indexOf("(t_")+3,sheetname.indexOf("_2)"));
				 sheetnameMap.put(sheetname, ""+i);
				 sheetlist.add(sheetname);
				}
			 }else{
				 if(sheetname.equals(cname)){
			     sheetnameMap.put(sheetname, ""+i);
				 }
			 }
			 
		 }
		int  onlynamesit =0;
		int  onlynamesit1 =0;
		int  onlynamesit2 =0;
		boolean select = false;
	//	 wb.getSheetName(arg0);//通过位置获得sheet名称
	//	 wb.getSheet(arg0);//通过sheet名称获得sheet
		 if(sheetnameMap==null||sheetnameMap.get(cname)==null){
			 throw new GeneralException("导入的excel模板找不到对应的"+cname+"数据页！");
		 }else{
			 sheet = wb.getSheetAt(Integer.parseInt(""+sheetnameMap.get(cname)));
			  Row row = sheet.getRow(0);
			    if (row == null)
				throw new GeneralException("请用导出的模板Excel来导入数据！");
			    int cols = row.getPhysicalNumberOfCells();
			    int rows = sheet.getPhysicalNumberOfRows();
			    if (cols < 1 || rows < 1)
			    	throw new GeneralException("请用导出的模板Excel来导入数据！");
				else
				{
				    for (int i = 0; i < cols; i++)
				    {
					Cell cell = row.getCell((short) i);
					if (cell != null)
					{
						String field = cell.getCellComment().getString().toString();
						if(field!=null&&field.trim().length()>0){
							excelfields += field+",";
							if(field.equalsIgnoreCase(onlyname+"_1")){
								onlynamesit1 = i;
								select =true;
							}
							if(field.equalsIgnoreCase(onlyname+"_2")){
								onlynamesit2 = i;
							}
						}
					}
				 }
				}
			    
		 }
		 if(select){
			 onlynamesit =onlynamesit1;
		 }else
			 onlynamesit =onlynamesit2; 
		 if(onlyname!=null&&onlyname.trim().length()>1&&existtarget.toString().toLowerCase().indexOf(onlyname.toLowerCase())!=-1){//模板中存在唯一标识
		    	if(excelfields.toLowerCase().indexOf(onlyname.toLowerCase()+"_1")!=-1){
		    		onlyflag = "1";
		    	}else if(excelfields.toLowerCase().indexOf(onlyname.toLowerCase()+"_2")!=-1){
		    		onlyflag = "2";
		    	}else{
		    		onlyflag = "0";
		    	}
			 }
		
			
		HashMap onlynameMap = new HashMap(); 

		String sheetname = ""+sheetlist.get(0);
		
			sheet = wb.getSheetAt(Integer.parseInt(""+sheetnameMap.get(sheetname)));
		
		 
	    HashMap map = new HashMap();
	    HashMap name2map = new HashMap();
	    Row row = sheet.getRow(0);
	    if (row == null)
		throw new GeneralException("请用导出的模板Excel来导入数据！");
	    int cols = row.getPhysicalNumberOfCells();
	    int rows = sheet.getPhysicalNumberOfRows();
	    StringBuffer a0100s = new StringBuffer();
	    StringBuffer codeBuf = new StringBuffer();
	    int x = 0;
	    String codeSetStr ="";
	    HashMap codeLeafItefColMap = new HashMap();//存放控制只选择叶子节点代码类的叶子节点代码
        ArrayList codeLeafSetColList = new ArrayList();//存放控制只选择叶子节点的代码类
	    HashMap codeColMap = new HashMap();
	    HashMap nameMap = new HashMap();
	    if (row != null)
	    {
		boolean errorflag = false;
		if (cols < 1 || rows < 1)
		    errorflag = true;
		else
		{
		    for (int i = 0; i < 1; i++)
		    {
			String value = "";
			Cell cell = row.getCell((short) i);
			if (cell != null)
			{
			    switch (cell.getCellType())
			    {
			    case Cell.CELL_TYPE_FORMULA:
				break;
			    case Cell.CELL_TYPE_NUMERIC:
				double y = cell.getNumericCellValue();
				value = Double.toString(y);
				break;
			    case Cell.CELL_TYPE_STRING:
				value = cell.getStringCellValue();
				break;
			    default:
				value = "";
			    }
			} else
			{
				 if(!table_name.startsWith("templet")&&("0".equals(operationtype)|| "5".equals(operationtype))){
					 
				 }else{
			    errorflag = true;
			    break;
				 }
			}

			if (i == 0 && !"主键标识串".equalsIgnoreCase(value))
			    errorflag = true;
			if (errorflag)
			    break;
		    }
		}
		if (errorflag&& "0".equals(onlyflag))
		    throw new GeneralException("请用导出的模板Excel来导入数据！");

		for (short c = 0; c < cols; c++)
		{
			if("0".equals(onlyflag)&&c==0)
				continue;
		    Cell cell = row.getCell(c);
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
			String field = cell.getCellComment().getString().toString();
			if("".equals(field.trim()))
				throw new GeneralException("标题行存在空批注！请用导出的模板Excel来导入数据！");
			if("i9999".equalsIgnoreCase(field))//i9999跟排序有关
				continue;
			if("主键标识串".equalsIgnoreCase(field))
				continue;
			if ("".equals(title.trim()))
			    throw new GeneralException("标题行存在空标题！请用导出的模板Excel来导入数据！");
			
			String tempfield="";
			tempfield = field;
			
			if(field.indexOf("_")!=-1){
				tempfield = field.substring(0,field.lastIndexOf("_"));
			}
			
			String itemtype="";
			String codesetid ="";
			if(DataDictionary.getFieldItem(tempfield)!=null&&DataDictionary.getFieldItem(tempfield).getCodesetid().length()>0){
				codesetid = DataDictionary.getFieldItem(tempfield).getCodesetid();
				itemtype = DataDictionary.getFieldItem(tempfield).getItemtype();
			}else{
				codesetid="0";
			}

//			String pri = this.userView.analyseFieldPriv(tempfield);
//			if (pri.equals("1") || pri.equals("0")) // 只读或者是没有权限
//			    continue;

			if (!"0".equals(codesetid))
			{
			    if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid))
			    {
			    	if(codeSetStr.trim().length()==0){
                		codeSetStr="'"+codesetid+"'";
                	}else
                		codeSetStr=codeSetStr+",'"+codesetid+"'";
			    	codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where upper(codesetid)='" + codesetid + "'   union all ");
			    } else
			    {
			    	if("UN".equalsIgnoreCase(codesetid))
			    		codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where upper(codesetid)='" + codesetid+  "' union all ");
			    	else if("UM".equalsIgnoreCase(codesetid))
			    		codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where upper(codesetid)<>'@K' union all ");
			    	else 
			    		codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization   union all ");
//						因为导入的时候有可能更新为非叶子机构所以在此放开限制为叶子部门的代码	+ "' and  codeitemid not in (select parentid from organization where codesetid='" + codesetid + "') union all ");
						
			    }
			}else{
			 if("parentid".equals(tempfield)&&infor_type!=1){
					codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where upper(codesetid)<>'@K' union all ");
			 }
			}
//			if ("a0100,a0101".indexOf(tempfield.toLowerCase()) == -1)//单位 部门 姓名字段不更新b0110,e01a1,e0122,
//			{
			name2map.put(new Short(c), field + ":" + cell.getStringCellValue());
			  if(field.indexOf("_1")!=-1)
				  continue;
			  if("codesetid".equalsIgnoreCase(tempfield)|| "codeitemdesc".equalsIgnoreCase(tempfield)|| "corcode".equalsIgnoreCase(tempfield)|| "parentid".equalsIgnoreCase(tempfield)|| "start_date".equalsIgnoreCase(tempfield))
				{
					
				}else{
					String state = this.userView.analyseFieldPriv(tempfield);
					String editable=null;
					if(fieldPrivByNode!=null&&fieldPrivByNode.get(field.toLowerCase())!=null)
					 editable=(String)fieldPrivByNode.get(field.toLowerCase()); //	//0|1|2(无|读|写)
					if(editable!=null)
		    			state=editable; 
					
					
						if(!this.getUserView().isSuper_admin()&&!"2".equalsIgnoreCase(state)&& "0".equals(tablebo.getUnrestrictedMenuPriv_Input()))
							 continue;	//无权限的去掉
					
					if(tablebo.getOpinion_field()!=null&&tablebo.getOpinion_field().length()>0&&tablebo.getOpinion_field().equalsIgnoreCase(tempfield))
						continue;
				}
			  if(existtarget.toUpperCase().indexOf(","+field.toUpperCase().trim()+",")==-1)
				  throw new GeneralException("指标"+field+"不存在！请检查模板数据指标！");
//			  if(itemtype.equals("M"))
//				  continue;//大字段类型单独处理
			  	insertsql.append(field+",");
			  	tempinsertstr.append("?,");
			    sql.append(field + "=?,");
			    map.put(new Short(c), field + ":" + cell.getStringCellValue());
			    updateFidsCount++;
//			}
		    } else
			break;
		}
		if (codeBuf.length() > 0)
		{
		    codeBuf.setLength(codeBuf.length() - " union all ".length());
		    RowSet rs=null;
		    try
		    {
			rs = dao.search(codeBuf.toString());
			while (rs.next()){
			    codeColMap.put(rs.getString("codesetid") + "a04v2u" + rs.getString("codeitemid")+":"+rs.getString("codeitemdesc").trim(), rs.getString("codeitemid"));//liuyz 代码项中前后有空格，在导入数据会去掉空格时造成匹配失败，不能导入。
			    codeColMap.put(rs.getString("codesetid") + "a04v2u" +rs.getString("codeitemdesc").trim(), rs.getString("codeitemid"));
			 	codeColMap.put(rs.getString("codesetid") + "a04v2u" + rs.getString("codeitemid"), rs.getString("codeitemid"));//考虑手工写入指标代码
			}
		    } catch (SQLException e)
		    {
			e.printStackTrace();
		    }finally {
				PubFunc.closeDbObj(rs);
			}

		}
		if(codeSetStr.length()>0)
        {
			String searchCodeset="SELECT  cm.codesetid,cm.codeitemid ,cm.codeitemdesc FROM codeitem cm LEFT JOIN ( SELECT  COUNT(1) AS num ,codesetid,parentid FROM    codeitem WHERE codeitemid<>parentid and  "+Sql_switcher.sqlNow()+" BETWEEN start_date AND end_date   GROUP BY parentid ,codesetid) AS cnum ON cm.codesetid = cnum.codesetid AND cm.codeitemid = cnum.parentid left join codeset c on cm.codesetid=c.codesetid WHERE "+Sql_switcher.isnull("cnum.num", "0")+"= 0 and upper(cm.codesetid) in("+ codeSetStr + ") and "+Sql_switcher.isnull("c.leaf_node","0")+"='1'  ";
        	RowSet rs=null;
            try {
                rs = dao.search(searchCodeset.toString());
                while (rs.next()) {
                	if(!codeLeafSetColList.contains(rs.getString("codesetid")))
                		codeLeafSetColList.add(rs.getString("codesetid"));
                	codeLeafItefColMap.put(rs.getString("codesetid") + "a04v2u" + rs.getString("codeitemid") + ":" + rs.getString("codeitemdesc").trim(), rs.getString("codeitemid"));//liuyz 代码项中前后有空格，在导入数据会去掉空格时造成匹配失败，不能导入。
                	codeLeafItefColMap.put(rs.getString("codesetid") + "a04v2u" + rs.getString("codeitemdesc").trim(), rs.getString("codeitemid"));
                	codeLeafItefColMap.put(rs.getString("codesetid") + "a04v2u" + rs.getString("codeitemid"), rs.getString("codeitemid"));// 考虑手工写入指标代码
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
				PubFunc.closeDbObj(rs);
			}
        }
		sql.setLength(sql.length() - 1);
		if("0".equals(onlyflag)){
		if(table_name.startsWith("templet")){//审批状态
			if(bo.getBo()!=null&&bo.getBo().getInfor_type()==1){
				insertsql.append("BasePre,A0100,ins_id,task_id");
				tempinsertstr.append("?,?,?,?");
				sql.append(" where BasePre=? and A0100=? and ins_id=? and "+Sql_switcher.isnull("task_id", "0")+"=?  ");
			}
			else if(bo.getBo()!=null&&bo.getBo().getInfor_type()==2){
				insertsql.append("b0110,ins_id,task_id");
				tempinsertstr.append("?,?,?");
				sql.append(" where  b0110=? and ins_id=? and task_id=?  ");
			}
			else if(bo.getBo()!=null&&bo.getBo().getInfor_type()==3){
				insertsql.append("e01a1,ins_id,task_id");
				tempinsertstr.append("?,?,?");
				sql.append(" where  e01a1=? and ins_id=? and task_id=?  ");
			}else{
				insertsql.append("BasePre,A0100,ins_id,task_id");
				tempinsertstr.append("?,?,?,?");
				sql.append(" where BasePre=? and A0100=? and ins_id=? and task_id=?  ");
			}
		
		}else{
			if(bo.getBo()!=null&&bo.getBo().getInfor_type()==1){
				tempinsertstr.append("?,?,?");
				insertsql.append("BasePre,A0100,A0000");	
				sql.append(" where BasePre=? and A0100=? ");
			}
			else if(bo.getBo()!=null&&bo.getBo().getInfor_type()==2){
				tempinsertstr.append("?,?");
				insertsql.append("b0110,A0000");	
				sql.append(" where  b0110=? ");
			}
			else if(bo.getBo()!=null&&bo.getBo().getInfor_type()==3){
				tempinsertstr.append("?,?");
				insertsql.append("e01a1,A0000");	
				sql.append(" where  e01a1=? ");
			}else{
				tempinsertstr.append("?,?,?");
				insertsql.append("BasePre,A0100,A0000");	
				sql.append(" where BasePre=? and A0100=? ");
			}
			
		}
		}else{

			if(table_name.startsWith("templet")){//审批状态
				if(bo.getBo()!=null&&bo.getBo().getInfor_type()==1){//对人员处理的业务模板
					insertsql.append("BasePre,A0100,ins_id,task_id");
					tempinsertstr.append("?,?,?,?");
				}
				else if(bo.getBo()!=null&&bo.getBo().getInfor_type()==2){
					insertsql.append("b0110,ins_id,task_id");
					tempinsertstr.append("?,?,?");
				}
				else if(bo.getBo()!=null&&bo.getBo().getInfor_type()==3){
					insertsql.append("e01a1,ins_id,task_id");
					tempinsertstr.append("?,?,?");
				}else{
					insertsql.append("BasePre,A0100,ins_id,task_id");
					tempinsertstr.append("?,?,?,?");
				}
			
			}else{
				if(bo.getBo()!=null&&bo.getBo().getInfor_type()==1){
					tempinsertstr.append("?,?,?");
					insertsql.append("BasePre,A0100,A0000");	
				}
				else if(bo.getBo()!=null&&bo.getBo().getInfor_type()==2){
					tempinsertstr.append("?,?");
					insertsql.append("b0110,A0000");	
				}
				else if(bo.getBo()!=null&&bo.getBo().getInfor_type()==3){
					tempinsertstr.append("?,?");
					insertsql.append("e01a1,A0000");	
				}else{
					tempinsertstr.append("?,?,?");
					insertsql.append("BasePre,A0100,A0000");	
				}
				
			}
					sql.append(" where "+onlyname+"_"+onlyflag+"=?  ");
		}
		
//		insertsql.setLength(sql.length() - 1);
		insertsql.append(",seqnum)values("+tempinsertstr.toString()+",?)");
	    }
	    ArrayList list2 = new ArrayList();
	    ArrayList listvo = new ArrayList();
	    ArrayList insertlist = new ArrayList();
	    int num2=1;
	    HashMap tablemap = getTableMap(table_name,infor_type,onlyflag,onlyname);
	    ArrayList listxuhao = new ArrayList();//新增时更新自动生成的序号
	    String errorFileName = "";//生成提示excel
	    String updateCount="0";//更新的数据条数
	    if("0".equals(onlyflag)){
	    	String reutnStr[] =bo.importMainExcel(sheet, table_name, nameMap, name2map, map, codeColMap, listxuhao, insertlist, tablemap, errorStr, list2, updateFidsCount, sql, insertsql,onlyflag,onlyname,onlynamesit,onlynameMap,form_file,codeLeafItefColMap,codeLeafSetColList);
	    	errorFileName=reutnStr[0];
            updateCount=reutnStr[1];
	    }else{//只有在系统中含有唯一性标识且模版中含有唯一性标识字段时 才会有子集工作表 才需要导入子集信息
	    	String reutnStr[] =bo.importMainExcel(sheet, table_name, nameMap, name2map, map, codeColMap, listxuhao, insertlist, tablemap, errorStr, list2, updateFidsCount, sql, insertsql,onlyflag,onlyname,onlynamesit,onlynameMap, form_file,codeLeafItefColMap,codeLeafSetColList);
	    	errorFileName=reutnStr[0];
            updateCount=reutnStr[1];
	    	tablemap = getTableMap(table_name,infor_type,onlyflag,onlyname);
			for(int i=1;i<sheetlist.size();i++){
				String setname = ""+sheetlist.get(i);
				sheet = wb.getSheetAt(Integer.parseInt(""+sheetnameMap.get(setname)));
			bo.importSubExcel(sheet, table_name, nameMap, map, listxuhao, insertlist, tablemap, errorStr, list2, updateFidsCount, onlyflag, onlyname, onlynameMap,setname);
			}
			
		}
	    	this.getFormHM().put("errorFileName", errorFileName);//导入数据失败提示的excel
	    	this.getFormHM().put("onlyname", onlyname);//是否有唯一性标识
	    	this.getFormHM().put("updateCount", updateCount);//是否有唯一性标识
	   
	    String hintInfo =errorStr.toString();
	    if(hintInfo.length()>0){
	        if (hintInfo.length() >2000){
	            hintInfo=hintInfo.substring(0,2000)+"……";//字符串太长，前台加载慢 10几分钟，提示信息多对用户也没太大用处，不会全看。wangrd 2015-02-07
	        }
			throw new GeneralException(hintInfo);
		}
	    
	    
	 //   11111111111111111111
//	    for (int j = 1; j < rows; j++)
//	    {
//	    	int num3 =0;
//			int num4=0;	
//		ArrayList list = new ArrayList();
//		row = sheet.getRow(j);
//		if(row==null)
//			row = sheet.createRow(j);
//
//		Cell flagCol = row.getCell((short) 0);
//		//遇到整个行都是空的截止
//		boolean flag = false;//判断是否有单元格不为null
//		if (flagCol != null)
//		{
//			
//		if(table_name.startsWith("templet")){//审批状态
//			if(!operationtype.equals("0")&&!operationtype.equals("5")){//==0是调入模板 需要特别处理 如没主键，就新增
//				   switch (flagCol.getCellType())
//				    {
//				    case Cell.CELL_TYPE_BLANK:
//					throw new GeneralException("主键标识串列存在空数据，导入数据失败！");
//				    case Cell.CELL_TYPE_STRING:
//					if (flagCol.getRichStringCellValue().toString().trim().length() == 0)
//					    throw new GeneralException("主键标识串列存在空数据，导入数据失败！");
//				    }
//				   String[] temp = flagCol.getStringCellValue().split("\\|");
//				if ((infor_type==1&&temp.length !=4)||(infor_type!=1&&temp.length !=3))
//				    continue;
//				if ((infor_type==1&&(temp[0].trim().length() == 0 || temp[1].trim().length() == 0|| temp[2].trim().length() == 0|| temp[3].trim().length() == 0 ))||
//						(infor_type!=1&&(temp[0].trim().length() == 0 || temp[1].trim().length() == 0|| temp[2].trim().length() == 0 ) )	)
//				    continue;
//			}else{
//				
//			}
//		
//		}else{
//			
//			if(!operationtype.equals("0")&&!operationtype.equals("5")){//==0是调入模板 需要特别处理 如没主键，就新增
//				   switch (flagCol.getCellType())
//				    {
//				    case Cell.CELL_TYPE_BLANK:
//					throw new GeneralException("主键标识串列存在空数据，导入数据失败！");
//				    case Cell.CELL_TYPE_STRING:
//					if (flagCol.getRichStringCellValue().toString().trim().length() == 0)
//					    throw new GeneralException("主键标识串列存在空数据，导入数据失败！");
//				    }
//				   String[] temp = flagCol.getStringCellValue().split("\\|");
//				if ((infor_type==1&&temp.length !=2)||(infor_type!=1&&temp.length !=1))
//				    continue;
//
//				if ((infor_type==1&&(temp[0].trim().length() == 0 || temp[1].trim().length() == 0))||
//						(infor_type!=1&&temp[0].trim().length() == 0 )	)
//				    continue;
//			}else{
//				
//			}
//			
//			}
//	   
//		    flag =true;
//		}
//		else{
//			
//		}
////		else
////		    continue;
//
//		StringBuffer priverror = new StringBuffer();
//	//	RecordVo  vo = new RecordVo(table_name);
//		for (short c = 1; c < cols; c++)
//		{
//		    Cell cell1 = row.getCell(c);
//		    if (cell1 != null)
//		    {
//		    flag =true;
//			String fieldItems = (String) map.get(new Short(c));
//			String fieldtemp = (String)name2map.get(new Short(c));
////			if(fieldtemp!=null){//为了抛出提示信息出现名字
////				String[] fieldItem2 = fieldtemp.split(":");
////				String field2 = fieldItem2[0];
////			  if(field2.equalsIgnoreCase("a0101_1")){
////				 String value2 = cell1.getRichStringCellValue().toString();
////			    	nameMap.put(j+1+"", value2);
////			    }
////			    else if(field2.equalsIgnoreCase("a0101_2")){
////			    	 String value2 = cell1.getRichStringCellValue().toString();
////			    	nameMap.put(j+1+"", value2);
////			    }
////			}
//			if (fieldItems == null)// 过滤掉只读的列
//			    continue;
//			
//			String[] fieldItem = fieldItems.split(":");
//			String field = fieldItem[0];
//			String fieldName = fieldItem[1];
//			String tempfield ="";
//			if(field.indexOf("_")!=-1)
//				tempfield = field.substring(0,field.lastIndexOf("_"));
//			String itemtype = "";
//			String codesetid = "";
//			int decwidth = 0;
//			int itemlength =0;
//			if( DataDictionary.getFieldItem(tempfield)!=null){
//			 itemtype = DataDictionary.getFieldItem(tempfield).getItemtype();
//			 codesetid = DataDictionary.getFieldItem(tempfield).getCodesetid();
//			 decwidth = DataDictionary.getFieldItem(tempfield).getDecimalwidth();
//			 itemlength =DataDictionary.getFieldItem(tempfield).getItemlength();
//			}
////			 if(itemtype.equals("M")){
////				   vo.setString(field, cell1.getRichStringCellValue().toString()) ;
////				  continue;//大字段类型单独处理
////			 }
//			if(tempfield.equalsIgnoreCase("codesetid")||tempfield.equalsIgnoreCase("codeitemdesc")||tempfield.equalsIgnoreCase("corcode")||tempfield.equalsIgnoreCase("parentid")||tempfield.equalsIgnoreCase("start_date"))
//			{
//				if(tempfield.equalsIgnoreCase("start_date")){
//					 itemtype ="D" ;	
//					 itemlength=10;
//				}else{
//					 itemtype ="A" ;
//					 itemlength=50;
//				}
//				
//			}
//			// String pri =
//			// this.userView.analyseFieldPriv(fieldName);
//			// if (pri.equals("1") || pri.equals("0")) //只读或者是没有权限
//			// continue;
//
//			String value = "";
//
//			switch (cell1.getCellType())
//			{
//			case Cell.CELL_TYPE_FORMULA:
//			    break;
//			case Cell.CELL_TYPE_NUMERIC:
//				 if(itemtype.equals("D")){
////					 SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd"); 
////					 list.add(Date.valueOf(sformat.format(cell1.getDateCellValue()).trim()));
//					 value = ""+cell1.getNumericCellValue();
//					 if(value.endsWith(".0"))
//					value = value.substring(0,value.lastIndexOf("."));
//					   if (value.length()>0&&this.isDataType(decwidth, itemtype, value)){	
//						   value= returnDataType(value);
//						   list.add(Date.valueOf(value.trim()));
//					   }
//					   else
//					   list.add(null);
//					    
//				 }else{
//
//			    double y = cell1.getNumericCellValue();
//
//			    value = Double.toString(y);
//			    value = PubFunc.round(value, decwidth);
//			    list.add(new Double((PubFunc.round(value, decwidth))));
//					   }
//				 if(fieldtemp!=null){//为了抛出提示信息出现名字
//						String[] fieldItem2 = fieldtemp.split(":");
//						String field2 = fieldItem2[0];
//					 if(infor_type==1){
//						  if(field2.equalsIgnoreCase("a0101_1")){
//						    	nameMap.put(j+1+"", value);
//						    }
//						    else if(field2.equalsIgnoreCase("a0101_2")){
//						    	nameMap.put(j+1+"", value);
//						    }
//						 }else{
//							  if(field2.equalsIgnoreCase("codeitemdesc_1")){
//								  if(value.length()>0){
//								    	nameMap.put(j+1+"", value);
//								    	}
//							    }
//							    else if(field2.equalsIgnoreCase("codeitemdesc_2")){
//							    	if(value.length()>0){
//							    	nameMap.put(j+1+"", value);
//							    	}
//							    }
//						 }
//					
//					}
//			    break;
//			case Cell.CELL_TYPE_STRING:
//			    value = cell1.getRichStringCellValue().toString();
//			    if (!codesetid.equals("0") && !codesetid.equals("")){
//				if (codeColMap.get(codesetid + "a04v2u" + value.trim()) != null)
//				    value = (String) codeColMap.get(codesetid + "a04v2u" + value.trim());
//				else
//				    value = null;
//			    }else{
//			    	if(bo.getBo()!=null&&bo.getBo().getInfor_type()!=1&&field.startsWith("codesetid_2")){
//			    		  if(value.equals("部门"))
//			    			  value="UM";
//			    		  if(value.equals("单位"))
//			    			  value="UN";
//			    	  }
//			    	if(bo.getBo()!=null&&bo.getBo().getInfor_type()!=1&&field.startsWith("parentid")){
//			    		if (codeColMap.get("UN"+ "a04v2u" + value.trim()) != null)
//						    value = (String) codeColMap.get("UN" + "a04v2u" + value.trim());
//			    		else if (codeColMap.get("UM"+ "a04v2u" + value.trim()) != null)
//						    value = (String) codeColMap.get("UM" + "a04v2u" + value.trim());
//						else
//						    value = null;
//			    	  }
//			    }
//			  //单位，部门，职位是否进行了权限控制
////			    if(codesetid.equals("UN")||codesetid.equals("UM")||codesetid.equals("@K")){
////			    	String error =isPriv_ctrl(cell_param_map,field,value,j,fieldName);
////			    	if(error.length()>0)
////						priverror.append(error);
////			    }
//			    if(itemtype.equals("D")){
//			   if (value.length()>0&&this.isDataType(decwidth, itemtype, value)){	
//				   value= returnDataType(value);
//				   list.add(Date.valueOf(value.trim()));
//			   }
//			   else
//			   list.add(null);
//			    }else{
//			    list.add(value);
//			    }
//			    if(fieldtemp!=null){//为了抛出提示信息出现名字
//					String[] fieldItem2 = fieldtemp.split(":");
//					String field2 = fieldItem2[0];
//					 if(infor_type==1){
//				  if(field2.equalsIgnoreCase("a0101_1")){
//				    	nameMap.put(j+1+"", value);
//				    }
//				    else if(field2.equalsIgnoreCase("a0101_2")){
//				    	nameMap.put(j+1+"", value);
//				    }
//					}else{
//						  if(field2.equalsIgnoreCase("codeitemdesc_1")){
//							  if(value.length()>0){
//							    	nameMap.put(j+1+"", value);
//							    	}
//						    }
//						    else if(field2.equalsIgnoreCase("codeitemdesc_2")){
//						    	if(value.length()>0){
//						    	nameMap.put(j+1+"", value);
//						    	}
//						    }
//					 }
//				}
//			    break;
//			case Cell.CELL_TYPE_BLANK:// 如果什么也不填的话数值就默认更新为0
//			    if (itemtype.equals("N"))
//			    {
//				value = PubFunc.round(value, decwidth);
//				list.add(new Double(value));
//			    }
//			    else if(itemtype.equals("D"))
//			    {
//					   if (value.length()>0&&this.isDataType(decwidth, itemtype, value)){	
//						   value= returnDataType(value);
//						   list.add(Date.valueOf(value.trim()));
//					   }
//					   else
//					   list.add(null);
//					    
//				    }
//			    else
//				list.add(null);
//			    break;
//			default:
//			    list.add(null);
//			}
//			String msg = "";
//			if (itemtype.equals("N") || itemtype.equals("D"))
//			{
//				if(value!=null)
//				value =value.trim();
//			    if (value.length()>0&&!this.isDataType(decwidth, itemtype, value))
//			    {
//			    if(itemtype.equals("D"))
//			    msg = "源数据(" + fieldName + ")第"+(j+1)+"行中数据:" + value + " 不符合格式<br>格式为yyyy-MM-dd或者yyyy.MM.dd或者yyyy/MM/dd!";
//			    else
//				msg = "源数据(" + fieldName + ")第"+(j+1)+"行中数据:" + value + " 不符合格式!";
//			    throw new GeneralException(msg);
//			    }
//			}
//			if(value!=null&&itemtype.equals("D")&&value.length()>10)
//			throw new GeneralException("源数据(" + fieldName + ")第"+(j+1)+"行中数据:长度超过数据库定义的长度，导入失败!");
//			if(value!=null&&itemtype.equals("N")&&value.length()>itemlength){
//				if(decwidth>0&&value.length()>itemlength+decwidth+1)
//				throw new GeneralException("源数据(" + fieldName + ")第"+(j+1)+"行中数据:长度超过数据库定义的长度，导入失败!");
//			}
//			if(value!=null&&itemtype.equals("A")){
//				byte [] valuelength =value.getBytes();
//				if(valuelength.length>itemlength)
//				throw new GeneralException("源数据(" + fieldName + ")第"+(j+1)+"行中数据:长度超过数据库定义的长度，导入失败!");
//			}
//			   
//		    }
//		    if(cell1==null&&c!=cols-1)
//			 {
//				 if(map.get(new Short(c))!=null)
//				 {
//					 list.add(null);
//				 }
//			 } 
//		    num3++;
//			 if(cell1==null||list.get(list.size()-1)==null||"".equals(list.get(list.size()-1)))
//			 {
//				
//					 num4++;
//				
//			 } else{
//				// System.out.println(list.get(list.size()-1));
//			 }
//		}
////		if(priverror.length()>0)
////			throw new Exception(priverror.toString());
//		if(num3==num4)
//			 continue;		//跳过该行
//		if(!flag)//整行为空过滤掉
//			continue;
//		ArrayList insert = (ArrayList)list.clone();
////		a0100s.append("'" + temp[1] + "',");
//		if(flagCol==null){
//			if(table_name.startsWith("templet")){//审批状态 审批模式下没有新增
//				continue;
//		
//				}else{
//					if(!operationtype.equals("0")&&!operationtype.equals("5")){//==0是调入模板 需要特别处理 如没主键，就新增
//						continue;
//					}else{//判断新增或者更新
//						//新增
//							 IDGenerator idg=new IDGenerator(2,this.getFrameconn());
//							
//							 if(tablebo.getDest_base()==null||tablebo.getDest_base().length()==0){
//								 if(infor_type==1){
//									throw new GeneralException("人员调入业务模板未定义目标库!");
//								 }else{
//									// throw new GeneralException("人员调入业务模板未定义目标库!");
//								 }
//							 }
//							String id = idg.getId("rsbd.a0100");
//							 if(infor_type==1){
//								 listxuhao.add(id);
//							 insert.add(tablebo.getDest_base().toUpperCase());
//							 insert.add(id);
//							 this.frowset=dao.search("select "+Sql_switcher.isnull("max(a0000)","0")+"+1 from "+table_name);
//								if(this.frowset.next())
//									 insert.add(""+(this.frowset.getInt(1)+num++));
//								
//								
//						    insertlist.add(insert);
////						    vo.setString("BasePre", tablebo.getDest_base().toUpperCase()) ;
////						    vo.setString("A0100", id) ;
////						    vo.setString("A0000", this.frowset.getInt(1)+num+"") ;
//							 }else{
//								 listxuhao.add("B"+id);
//								 insert.add("B"+id);
//								 this.frowset=dao.search("select "+Sql_switcher.isnull("max(a0000)","0")+"+1 from "+table_name);
//									if(this.frowset.next())
//										 insert.add(""+(this.frowset.getInt(1)+num++));
//									
//									
//							    insertlist.add(insert);
////							    if(infor_type==2){
////							    vo.setString("B0110", id) ;
////							    vo.setString("A0000", this.frowset.getInt(1)+num+"") ;
////							    }else{
////							    	 vo.setString("E01a1", id) ;
////									 vo.setString("A0000", this.frowset.getInt(1)+num+"") ;	
////							    }
//							 }
//						    continue;
//						
//
//					}
//				}	
//		}else{
//		String[] temp = flagCol.getStringCellValue().split("\\|");
//		if(temp!=null&&temp.length>1&&temp[1]!=null){
//			if(tablemap==null){
//				 if(infor_type==1){
//					 throw new GeneralException("当前模板库中不存在人员!");
//					 }else{
//					 throw new GeneralException("当前模板库中不存在记录!");
//					 }
//					
//			}
//			if(tablemap!=null&&((infor_type==1&&tablemap.get(temp[1])==null)||(infor_type!=1&&tablemap.get(temp[0])==null))){
//				 if(infor_type==1){
//					 		errorStr.append(" "+num2+"、第"+(j+1)+"行     姓名："+nameMap.get(j+1+"")+"<br>");
//						 }else{
//							 errorStr.append(" "+num2+"、第"+(j+1)+"行  ："+nameMap.get(j+1+"")+"<br>");
//						 }
//				
//			num2++;
//			}
//			//throw new GeneralException("第"+(j+1)+"行中主键标识下的人员id:"+temp[1]+"不在当前模板库中!");
//		}
//		if(table_name.startsWith("templet")){//审批状态 审批模式下没有新增
//			if(temp.length==4){
//				list.add(temp[0]);
//				list.add(temp[1]);
//				list.add(temp[2]);
//				list.add(temp[3]);
////				 vo.setString("BasePre", temp[0]) ;
////				    vo.setString("A0100", temp[1]) ;
////				    vo.setString("ins_id", temp[2]) ;
////				    vo.setString("task_id", temp[3]) ;
//			}else if(temp.length==3){
//				list.add(temp[0]);
//				list.add(temp[1]);
//				list.add(temp[2]);
////				if(infor_type==2){
////			    vo.setString("B0110", temp[0]) ;
////				}else
////					vo.setString("e01a1", temp[0]) ;
////			    vo.setString("ins_id", temp[1]) ;
////			    vo.setString("task_id", temp[2]) ;
//			}
//			else{
//				continue;
//			}
//	
//			}else{
//				if(!operationtype.equals("0")&&!operationtype.equals("5")){//==0是调入模板 需要特别处理 如没主键，就新增
//					if(temp.length==2){
//					list.add(temp[0]);
//					list.add(temp[1]);
////					vo.setString("BasePre", temp[0]) ;
////				    vo.setString("A0100", temp[1]) ;
//					}else if(temp.length==1){
//						list.add(temp[0]);
////						if(infor_type==2){
////						    vo.setString("B0110", temp[0]) ;
////							}else
////								vo.setString("e01a1", temp[0]) ;
//						}
//					else{
//						continue;
//					}
//				}else{//判断新增或者更新
//					if (temp.length==0||(temp.length==1&&temp[0].equals(""))){//新增
//						 IDGenerator idg=new IDGenerator(2,this.getFrameconn());
//						
//						 if(tablebo.getDest_base()==null||tablebo.getDest_base().length()==0){
//							 if(infor_type==1){
//								 throw new GeneralException("人员调入业务模板未定义目标库!");
//								 }else{
//								// throw new GeneralException("当前模板库中不存在记录!");
//								 }
//								
//						}
//							String id = idg.getId("rsbd.a0100");
//							 if(infor_type==1){
//								 listxuhao.add(id);
//							 insert.add(tablebo.getDest_base().toUpperCase());
//							 insert.add(id);
//							 this.frowset=dao.search("select "+Sql_switcher.isnull("max(a0000)","0")+"+1 from "+table_name);
//								if(this.frowset.next())
//									 insert.add(""+(this.frowset.getInt(1)+num++));
//								
//								
//						    insertlist.add(insert);
////						    vo.setString("BasePre", tablebo.getDest_base().toUpperCase()) ;
////						    vo.setString("A0100", id) ;
////						    vo.setString("A0000", this.frowset.getInt(1)+num+"") ;
//							 }else{
//								 listxuhao.add("B"+id);
//								 insert.add("B"+id);
//								 this.frowset=dao.search("select "+Sql_switcher.isnull("max(a0000)","0")+"+1 from "+table_name);
//									if(this.frowset.next())
//										 insert.add(""+(this.frowset.getInt(1)+num++));
//									
//									
//							    insertlist.add(insert);
////							    if(infor_type==2){
////							    vo.setString("B0110", id) ;
////							    vo.setString("A0000", this.frowset.getInt(1)+num+"") ;
////							    }else{
////							    	 vo.setString("E01a1", id) ;
////									 vo.setString("A0000", this.frowset.getInt(1)+num+"") ;	
////							    }
//							 }
//					    continue;
//					}else
//					{
//					if(temp.length==2){
//						list.add(temp[0]);
//						list.add(temp[1]);
////						vo.setString("BasePre", temp[0]) ;
////					    vo.setString("A0100", temp[1]) ;
//						}else if(temp.length==1){
//							list.add(temp[0]);
////							if(infor_type==2){
////							    vo.setString("B0110", temp[0]) ;
////								}else
////									vo.setString("e01a1", temp[0]) ;
//							}
//						else{
//							if(infor_type==1){
//								list.add(null);
//								list.add(null);
//							}else{
//								list.add(null);
//							}
//							
//						}
//					}
//
//				}
//			}
//		}	
//		
//	
//		list2.add(list);
//	//	listvo.add(vo);
//	    }
//	    if(errorStr.toString().length()>2){
//	   	 if(infor_type==1){
//	   		throw new GeneralException("EXCEL模板隐藏的第一列主键标识串中的人员：<br>"+errorStr.toString()+"在业务表单中不存在!");	
//			 }else{
//			throw new GeneralException("EXCEL模板隐藏的第一列主键标识串中的记录：<br>"+errorStr.toString()+"在业务表单中不存在!");	
//			 }
//	    	
//	    }
//	    if (updateFidsCount == 0)
//		return;
//	    if(list2.size()>0)
//	    dao.batchUpdate(sql.toString(), list2);
//	    if(!table_name.startsWith("templet")&&(operationtype.equals("0")||operationtype.equals("5"))&&insertlist.size()>0){
//	    dao.batchUpdate(insertsql.toString(), insertlist);
//	    }
//	    //更新自动生成序号
//	    if(listxuhao.size()>0){
//	    	 if("1".equals(tablebo.getId_gen_manual())){
//	            	
//	            }else{
//	            	for(int i=0;i<listxuhao.size();i++){
//	   	    		 tablebo.filloutSequence(""+listxuhao.get(i), tablebo.getDest_base(), table_name); 
//	   	    	} 
//	            }
//	    
//	    }
//		if(operationtype.equals("0"))
//		{
//			if(sql.toString().indexOf("a0101_2")!=-1||insertsql.toString().indexOf("a0101_2")!=-1)
//			{
//				 dao.update(" update "+table_name+" set a0101_1= a0101_2 ");
//			}
//		}
	
//	    a0100s.setLength(a0100s.length() - 1);
//	    StringBuffer buf = new StringBuffer("select * from " + table_name + " where a0100 in (" + a0100s.toString() + ") ");
	
//		buf.append(" and tabid=" + tabid);
//       if (sql.length() > x)
//	    {
////		buf.append(" and  not (" + sql.substring(x, sql.length()) + ")");
//		RowSet rs = dao.search(buf.toString());
//		String errorInfo = "下列人员的数据不允许更新：\n";
//		int count = 0;
//		if(dbWizard.isExistField(table_name,"a0101_1"))
//		while (rs.next())
//		{
//		    errorInfo += rs.getString("a0101_1") + "  ";
//		    count++;
//		    if (count % 10 == 0)
//			errorInfo += "\n";
//		}
//		if (count > 0)
//		    throw new GeneralException(errorInfo);
//	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	} finally {
		PubFunc.closeResource(wb);
		PubFunc.closeResource(inputStream);//资源释放 jingq 2014.12.29
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
 //   public boolean isDataType(int decwidth, String itemtype, String value)
//    {
//
//	boolean flag = true;
//	if (itemtype.equals("N"))
//	{
//	    if (decwidth == 0)
//	    {
//		flag = value.matches("^[+-]?[\\d]+$");
//	    } else
//	    {
//		flag = value.matches("^[+-]?[\\d]*[.]?[\\d]+");
//	    }
//
//	} else if (itemtype.equals("D"))
//	{
//		value = value.replace(".", "-");
//		value = value.replace("/", "-");
//		if(value.matches("[0-9]{4}")&&value.length()==4){//2010
//		    flag = true;
//		    value= value+"-01-01";
//			}
//		else if(value.matches("[0-9]{4}[-]")&&value.length()==5){//2010-
//		    flag = true;
//		    value= value+"01-01";
//			}
//		else if(value.matches("[0-9]{4}[-][0-9]{1}")&&value.length()==6){//2010-5
//		    flag = true;
//		    String str = value.substring(5,value.length());
//		    value= value.substring(0,5)+"0"+str+"-01";
//			}
//		else if(value.matches("[0-9]{4}[-][0-9]{2}")&&value.length()==7){//2010-05
//		    flag = true;
//		    	 value= value+"-01";
//			}
//		else if(value.matches("[0-9]{4}[-][0-9]{1}[-]")&&value.length()==7){//2010-5- 
//		    flag = true;
//		    	 String str = value.substring(5,value.length());
//		    	 value= value.substring(0,5)+"0"+str+"-01";
//			}
//		else if(value.matches("[0-9]{4}[-][0-9]{2}[-]")&&value.length()==8){//2010-05-
//		    flag = true;
//		    value= value+"01";
//			}
//		else if(value.matches("[0-9]{4}[-][0-9]{1}[-][0-9]{1}")&&value.length()==8){//2010-5-5
//			  flag = true;
//		    String str1 = value.substring(5,6);
//		    String str2 = value.substring(7,8);
//	    	 value= value.substring(0,5)+"0"+str1+"-0"+str2;
//			}
//		else if(value.matches("[0-9]{4}[-][0-9]{2}[-][0-9]{1}")&&value.length()==9){//2010-05-5
//		    flag = true;
//		    String str2 = value.substring(value.length()-1,value.length());
//		    value= value.substring(0,value.length()-1)+"0"+str2;
//			}
//		else if(value.matches("[0-9]{4}[-][0-9]{1}[-][0-9]{2}")&&value.length()==9){//2010-5-15
//		    flag = true;
//		    String str = value.substring(5,value.length());
//		    value= value.substring(0,5)+"0"+str;
//			}
//		
//		else if(value.matches("[0-9]{4}[-][0-9]{2}[-][0-9]{2}")&&value.length()==10){
//		    flag = true;
//			}
//		else{
//			flag=false;
//		}
//		if(value.matches("[0-9]{4}")&&value.length()>4){
//			String str = value.substring(0,4);
//			if(Integer.parseInt(str)<1800)
//				flag = false;
//		}
//			
//		 String eL= "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-9]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";   
//	        Pattern p = Pattern.compile(eL);    
//	        Matcher m = p.matcher(value);    
//	        boolean b = m.matches();   
//	        if(!b)   
//	        {   
//	        	flag=false;
//	        }   
//	       
//		
//	}
//	return flag;
//    }
    /**
     * 返回日期类型
     * 
     * @param columnBean
     * @param itemid
     * @param value
     * @return
     */
//public String returnDataType( String value)
//{
//
//
//	value = value.replace(".", "-");
//	value = value.replace("/", "-");
//	if(value.matches("[0-9]{4}")&&value.length()==4){//2010
//	    value= value+"-01-01";
//		}
//	else if(value.matches("[0-9]{4}[-]")&&value.length()==5){//2010-
//	    value= value+"01-01";
//		}
//	else if(value.matches("[0-9]{4}[-][0-9]{1}")&&value.length()==6){//2010-5
//	    String str = value.substring(5,value.length());
//	    value= value.substring(0,5)+"0"+str+"-01";
//		}
//	else if(value.matches("[0-9]{4}[-][0-9]{2}")&&value.length()==7){//2010-05
//	    	 value= value+"-01";
//		}
//	else if(value.matches("[0-9]{4}[-][0-9]{1}[-]")&&value.length()==7){//2010-5- 
//	    	 String str = value.substring(5,value.length());
//	    	 value= value.substring(0,5)+"0"+str+"01";
//		}
//	else if(value.matches("[0-9]{4}[-][0-9]{2}[-]")&&value.length()==8){//2010-05-
//	    value= value+"01";
//		}
//	else if(value.matches("[0-9]{4}[-][0-9]{1}[-][0-9]{1}")&&value.length()==8){//2010-5-5
//	    String str1 = value.substring(5,6);
//	    String str2 = value.substring(7,8);
//    	 value= value.substring(0,5)+"0"+str1+"-0"+str2;
//		}
//	else if(value.matches("[0-9]{4}[-][0-9]{2}[-][0-9]{1}")&&value.length()==9){//2010-05-5
//	    String str2 = value.substring(value.length()-1,value.length());
//	    value= value.substring(0,value.length()-1)+"0"+str2;
//		}
//	else if(value.matches("[0-9]{4}[-][0-9]{1}[-][0-9]{2}")&&value.length()==9){//2010-5-15
//	    String str = value.substring(5,value.length());
//	    value= value.substring(0,5)+"0"+str;
//		}
//	
//return value;
//}
   /**
    * 获得库中所有的数据
    * @param table_name
    * @return
    */
    public HashMap getTableMap(String table_name,int infor_type,String onlyflag,String onlyname)
	{
		HashMap map=new HashMap();
		try
		{
			String sql="select A0100  from "+table_name+"  "; 	
			if("0".equals(onlyflag)){
			if(infor_type==1){
				sql="select A0100  from "+table_name+"  "; 	
			}else if(infor_type==2){
				sql="select b0110  from "+table_name+"  "; 	
			}else if(infor_type==3){
				sql="select e01a1  from "+table_name+"  "; 	
			}
			}else{
				sql="select "+onlyname+"_"+onlyflag+"  from "+table_name+"  "; 
			}
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql);
			while(this.frowset.next())
			{
				map.put(this.frowset.getString(1),this.frowset.getString(1));
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
//	private String  isPriv_ctrl(HashMap cell_param_map,String field,String value ,int j,String fieldname){
//		String sub_domain="";
//		Document doc = null;
//		Element element=null;
//		StringBuffer sb = new StringBuffer();
//		LazyDynaBean bean = (LazyDynaBean)cell_param_map.get(field);
//		if(bean!=null&&bean.get("sub_domain")!=null)
//			sub_domain=(String)bean.get("sub_domain");
//		sub_domain = SafeCode.decode(sub_domain);
//		if(sub_domain!=null&&sub_domain.length()>0)
//		{
//			StringReader reader=new StringReader(sub_domain);
//			try {
//				doc=saxbuilder.build(reader);
//			
//				String xpath="/sub_para/para";
//				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
//				List childlist=findPath.selectNodes(doc);	
//				if(childlist!=null&&childlist.size()>0)
//				{
//					element=(Element)childlist.get(0);
//					String priv =(String)element.getAttributeValue("limit_manage_priv");
//					if("1".equals(priv)){
//						if(!this.userView.isSuper_admin()){
//						if(value!=null&&!"".equals(value)&&!value.startsWith(this.userView.getManagePrivCodeValue())){
//							
//							if(field.equalsIgnoreCase("b0110_2"))
//								sb.append("源数据第"+(j+1)+"行中数据:指标"+fieldname+"设置了按管理范围控制,请选择管理范围下的单位！\r\n");
//							if(field.equalsIgnoreCase("e0122_2"))
//								sb.append("源数据第"+(j+1)+"行中数据:指标"+fieldname+"设置了按管理范围控制,请选择管理范围下的部门！\r\n");
//							if(field.equalsIgnoreCase("E01A1_2"))
//								sb.append("源数据第"+(j+1)+"行中数据:指标"+fieldname+"设置了按管理范围控制,请选择管理范围下的职位！\r\n");
//						
//						}
//						}
//					}
//				}
//			} catch (JDOMException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		return sb.toString();
//		}
}
