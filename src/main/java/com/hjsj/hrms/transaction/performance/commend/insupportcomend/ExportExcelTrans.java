package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hjsj.hrms.businessobject.performance.commend.CommendSetBo;
import com.hjsj.hrms.businessobject.performance.commend.CommendXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ExportExcelTrans extends IBusiness{

	public void execute() throws GeneralException {
		FileOutputStream fileOut = null;
		HSSFWorkbook workbook = null;
		try{
			String p0201=(String)this.getFormHM().get("p0201");
			String p0209=(String)this.getFormHM().get("p0209");
			ArrayList list=DataDictionary.getFieldList("p03",Constant.USED_FIELD_SET);
			CommendSetBo bo = new CommendSetBo(this.getFrameconn());
			CommendXMLBo xmlBo=new CommendXMLBo(this.getFrameconn());
			String commend_field="";
			String commend_field_codesetid="";
			commend_field=xmlBo.getCtrl_paraValue(p0201,CommendXMLBo.commend_field);
			commend_field_codesetid=bo.getCommendFieldCodesetid(list,commend_field);
			String outname="commend123.xls";
			workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet();
			HSSFRow row=null;
			HSSFCell csCell=null;
            HashMap map =this.getExcelTitle("p02",p0201,"p0203","p0201");
			HashMap mp = this.getExcelColumn(list);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			short n=setTitle((String)map.get("title"),workbook,sheet);
			ArrayList p0307List = getP0307List(p0201);
			for(short i=0;i<p0307List.size();i++){
				String p0307=(String)p0307List.get(i);
				String name=getP0307Name(p0307);/**单位名称*/
				HashMap hm = getCountByP0307(p0201,p0307,commend_field,commend_field_codesetid);
				int counts=0;
				if(hm.get("1")!=null)
				     counts=Integer.parseInt((String)hm.get("1"));/**该单位的总票数*/
				ArrayList dataList = (ArrayList)hm.get("2");/**该单位的个人得票数明细*/
				String[][] data=getExcelData(dataList);
				row = sheet.createRow((short)(n));
				csCell =row.createCell((short)0);
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue("单位:"+name+"("+counts+"票)");
				csCell =row.createCell((short)6);
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue(format.format(new Date()));
				n=setHead((short)(n+1),mp,workbook,sheet);
                for(short k=0;k<data.length;k++){
				   row = sheet.createRow((short)(n));
				   for(short j=0;j<data[k].length;j++)
				   {
					   csCell =row.createCell((short)(2*j));
//					   csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
					   csCell.setCellValue(data[k][j]);
				  }
				   n++;
			   }
			}
			
			
			
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outname);
			workbook.write(fileOut);
			sheet=null;
			//outname=outname.replaceAll(".xls","#");
			//xus 20/4/30 vfs改造
			this.getFormHM().put("outName",PubFunc.encrypt(outname));
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}
	}
	/**
	 * 得到excel文件的标题
	 * @param tableName
	 * @param keyValue
	 * @param titleColumn
	 * @param keyColumn
	 * @return
	 */
	public HashMap getExcelTitle(String tableName,String keyValue,String titleColumn,String keyColumn){
		HashMap map=new HashMap();
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("select ");
			sql.append(titleColumn);
			sql.append(" from ");
			sql.append(tableName);
			sql.append(" where ");
			sql.append(keyColumn+" = "+keyValue);
			ContentDAO dao =new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next()){
				map.put("title",this.frowset.getString(titleColumn));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
		
	}
	/**
	 * 得到excel文件的列名
	 * @param fieldList
	 * @return
	 */
	public HashMap getExcelColumn(ArrayList fieldList){
		//ArrayList list = new ArrayList();
		HashMap map = new HashMap();
		try{
			FieldItem newItem=new FieldItem();
			newItem.setItemdesc("序号");
			map.put("1",newItem.getItemdesc());
			for(int i=0;i<fieldList.size();i++){
				FieldItem item=(FieldItem)fieldList.get(i);
				if("p0304".equalsIgnoreCase(item.getItemid()) ){
					map.put("4",item.getItemdesc());
				}
				if("a0101".equalsIgnoreCase(item.getItemid())){
					map.put("2",item.getItemdesc());
				}
			}
			map.put("3","推荐职务");
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
		
	}
	/**
	 * 得到数据列表
	 * @param p0201
	 * @param p0209
	 * @return
	 */
	public ArrayList getData(String p0201,String p0209){
		ArrayList list = new ArrayList();
		StringBuffer sql=new StringBuffer();
		try{
			if("06".equals(p0209)){
				sql.append("select a0101,p0304 from p03 where p0201=");
				sql.append(p0201);
			 }else{
				sql.append("select p03.a0100,p03.a0101,b.num from p03 ");
				sql.append(" left join ( select count(logon_id) num,ptv.p0300 from p03,");
				sql.append("per_talent_vote ptv where ptv.p0300=ptv.p0300");
				sql.append(" group by ptv.p0300) b");
				sql.append(" on p03.p0300=b.p0300 where ");
				sql.append("p03.p0201="+p0201);
				
			}
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql.toString());
			int i=1;
			while(this.frowset.next()){
				LazyDynaBean bean=new LazyDynaBean();
				bean.set("id",String.valueOf(i));
				bean.set("a0101",this.frowset.getString("a0101"));
				String p0304 = this.frowset.getString("num");
				if(p0304 ==null || "".equals(p0304))
					bean.set("p0304","0");
				else
				    bean.set("p0304",p0304);
				i++;
				list.add(bean);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	public String[][] getExcelData(ArrayList list){
		int i=list.size();
		String[][] data=new String[i][4];
		for(int n=0;n<i;n++){
			LazyDynaBean bean=(LazyDynaBean)list.get(n);
			data[n][0]=(String)bean.get("id");
			data[n][1]=(String)bean.get("a0101");
			data[n][2]=(String)bean.get("commend_field");
			data[n][3]=(String)bean.get("p0304");
		}
		return data;
	}
	/**
	 * 得到某一推荐记录下的所有推荐范围
	 * @param p0201
	 * @return
	 */
	public ArrayList getP0307List(String p0201){
		ArrayList list = new ArrayList();
		String sql = "select distinct p0307 from p03 where p0201="+p0201+" order by p0307";
		ContentDAO dao =new ContentDAO(this.getFrameconn());
		try{
		  this.frowset=dao.search(sql);
		  while(this.frowset.next()){
			  list.add(this.frowset.getString("p0307"));
		  }
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 得到某一推荐范围下的所有票数和候选人信息
	 * @param p0201
	 * @param p0307
	 * @return
	 */
	public HashMap getCountByP0307(String p0201,String p0307,String commendField,String commendFieldCodesetid){
		HashMap map=new HashMap();
		StringBuffer sql = new StringBuffer();
		sql.append("select p03.*,b.num from p03 ");
		sql.append(" left join ( select count(logon_id) num,ptv.p0300 from ");
		sql.append("per_talent_vote ptv,p03 where ptv.p0300=ptv.p0300 and p03.p0300=ptv.p0300");
		sql.append(" group by ptv.p0300) b");
		sql.append(" on p03.p0300=b.p0300 where ");
		sql.append("p03.p0201="+p0201+" and p03.p0307='"+p0307+"' order by p03.p0304 desc");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		int counts=0;
		ArrayList list = new ArrayList();
		try{
		   this.frowset=dao.search(sql.toString());
		   int i=1;
		   while(this.frowset.next()){
			   LazyDynaBean bean=new LazyDynaBean();
				bean.set("id",String.valueOf(i));
				bean.set("a0101",this.frowset.getString("a0101"));
				String p0304 = this.frowset.getString("num");
				if(p0304 ==null || "".equals(p0304))
					bean.set("p0304","0");
				else
				    bean.set("p0304",p0304);
				if(commendField.trim().length()>0)
					bean.set("commend_field",AdminCode.getCodeName(commendFieldCodesetid,this.frowset.getString(commendField)));
				else
					bean.set("commend_field","");
				i++;
				list.add(bean);
			 counts+=this.frowset.getInt("num");  
		   }
		   map.put("1",String.valueOf(counts));
		   map.put("2",list);
		   
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
		
	}
	/**
	 * 得到推荐范围的名字
	 * @param p0307
	 * @return
	 */
	public String getP0307Name(String p0307){
		StringBuffer sql = new StringBuffer();
		sql.append("select o.codeitemdesc from b01,organization o where b0110=codeitemid and b0110='"+p0307+"'");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String name="";
		try{
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next()){
				name=this.frowset.getString("codeitemdesc");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return name;
		
	}
	public short setTitle(String titleName,HSSFWorkbook workbook,HSSFSheet sheet){
		short n=0;
		HSSFRow row=null;
		HSSFCell csCell=null;
		try{
			HSSFFont font = workbook.createFont();			
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			HSSFCellStyle cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			row=sheet.createRow(n);
			csCell=row.createCell((short)(n+3));
			csCell.setCellStyle(cellStyle);
//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
			csCell.setCellValue(titleName);
			n++;
			n++;
		}catch(Exception e){
			e.printStackTrace();
		}
		return n;
	}
	public short setHead(short n,HashMap map,HSSFWorkbook workbook,HSSFSheet sheet){
		short w=n;
		try{
        HSSFRow row=null;
		HSSFCell csCell=null;
		row=sheet.createRow(w);
		for(short i=0;i<map.size();i++){
		   csCell=row.createCell((short)(i+i));
//		   csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
		   csCell.setCellValue((String)map.get(String.valueOf(i+1)));
 	    }
		
		}catch(Exception e){
			e.printStackTrace();
		}
		w++;
		return w;
	}
	

}
