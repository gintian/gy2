package com.hjsj.hrms.transaction.performance.data_collect;

import com.hjsj.hrms.businessobject.performance.data_collect.DataCollectBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
* 
* 类名称：ExportExcelTrans   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 21, 2013 11:59:10 AM   
* 修改人：zhaoxg   
* 修改时间：Aug 21, 2013 11:59:10 AM   
* 修改备注：   导入excel
* @version    
*
 */
public class ExportExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		InputStream _in = null;
		Workbook workbook = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			FormFile file = (FormFile)this.getFormHM().get("formfile");
			String dbname=(String)this.getFormHM().get("dbname");//数据库
			int info = 0;
			String retInfo="";
			if(file==null||file.getFileData().length==0)
			{
				info=2;
				this.getFormHM().put("info",String.valueOf(info));
				this.getFormHM().put("retInfo","您选择的文件是个空文件");
				return;
			}
			_in = file.getInputStream();
			workbook = WorkbookFactory.create(_in);
			Sheet sheet1=workbook.getSheetAt(0);
			exportInTaxData(sheet1,dao);
			this.getFormHM().put("info",String.valueOf(info));
			this.getFormHM().put("returnInfo",retInfo);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(_in);
			PubFunc.closeResource(workbook);
		}
	}
	public HashMap exportInTaxData(Sheet sheet,ContentDAO dao) throws GeneralException
	{
		HashMap map = new HashMap();
		try
		{
			Row row = null;
			Cell cell=null;	
			String fieldsetid=(String)this.getFormHM().get("fieldsetid");	
			fieldsetid = PubFunc.hireKeyWord_filter(fieldsetid); // 刘蒙
			DataCollectBo bo = new DataCollectBo(this.frameconn,"Params");
			DataCollectBo databo = new DataCollectBo(this.frameconn,this.userView);
			String state_id  = bo.getXmlValue1("state_id",fieldsetid);
			String set_id  = bo.getXmlValue1("set_id",fieldsetid);
			StringBuffer insertSql = new StringBuffer();
			StringBuffer Sql = new StringBuffer();
			int rows=sheet.getPhysicalNumberOfRows();
			String id = "";
			String code = "";
			String name = "";
			String type = "";
			String value = "";
			String codesetid = "";
			ArrayList accountlist = databo.getExcelList(set_id, state_id);
			for(int i=1;i<rows;i++)
			{
				LazyDynaBean bean = new LazyDynaBean();
				row=sheet.getRow(i);
				int cells=accountlist.size();
				ArrayList list = new ArrayList();
				if(row.getCell(cells-1)==null|| "".equals(row.getCell(cells-1))){
					break;
				}
				String status=row.getCell(cells-1).toString().split(":")[0];
				if("01".equals(status)|| "07".equals(status)){
					cell=row.getCell(0);
					String[] _cell = cell.toString().split(":");
					insertSql.append("update "+_cell[0]+set_id+" set ");


					for(short j=5;j<cells;j++)
					{
						bean = (LazyDynaBean) accountlist.get(j);
						id = (String) bean.get("id");
						code = (String) bean.get("code");
						name = (String) bean.get("name");
						type = (String) bean.get("type");
						codesetid = (String) bean.get("codesetid");
						cell=row.getCell(j);
						if(!code.equals(state_id)){
							if("A".equals(type)){
								insertSql.append(code+"="+"'"+cell.getStringCellValue().split(":")[0]+"'"+"");
							}else if ("N".equals(type)) {
								String desc = (String) bean.get("decwidth");
								insertSql.append(code+"="+"'"+PubFunc.round(Double.toString(cell.getNumericCellValue()),Integer.parseInt(desc))+"'"+"");
							}else if("D".equals(type)){
								insertSql.append(code+"="+getDate(cell.getStringCellValue())+"");
							}else if("M".equals(type)){
								insertSql.append(code+"="+"'"+cell.getStringCellValue()+"'"+"");
							}else{
								insertSql.append(code+"="+"'"+cell.getStringCellValue()+"'"+"");
							}
			    				insertSql.append(",");	
						}


					}
					Sql.append(insertSql.substring(0, insertSql.length()-1)+"  where a0100='"+_cell[1]+"' and i9999='"+_cell[2]+"' and "+state_id+" in ('01','07')");
					dao.update(Sql.toString(), list);
					insertSql.setLength(0);
					Sql.setLength(0);
					list.clear();
				}
				
			}
		}catch(IllegalStateException ex){
			throw GeneralExceptionHandler.Handle(new Exception("导入数据格式有误！"));
		}catch(Exception e)
		{
			e.printStackTrace();
		}

		return map;
	}
public String getDate(String _date){
	String date = "";
	try{
		if(_date!=null&&!"".equals(_date)){
			_date = _date.replaceAll("\\.", "-");
			String[] tempdate = _date.split("-");
			if(_date.length()<8){
				date = tempdate[0]+"-"+tempdate[1]+"-"+"01";
			}else{
				StringBuffer ss = new StringBuffer();
				for(int i=0;i<tempdate.length;i++){
					ss.append(tempdate[i]);
					if(i<2){
						ss.append("-");
					}else{
						ss.append(":");
					}
					
				}
				date=ss.toString().substring(0, ss.length()-1);
			}
			
			
		}
		date = Sql_switcher.dateValue(date);
	}catch(Exception e)
	{
		e.printStackTrace();
	}
	return date;
}
	public String getMaxid(ContentDAO dao){
		String maxid = "1";
		try
		{
			String sql = "select max("+Sql_switcher.sqlToInt("i_id")+") as max from GZ_code";
			this.frowset=dao.search(sql);
			if(this.frowset.next()){
				maxid=Integer.parseInt(this.frowset.getString("max"))+1+"";
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return maxid;
	}

}
