package com.hjsj.hrms.businessobject.sys.rolemanagement;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.jdom.Document;
import org.jdom.Element;

import javax.sql.RowSet;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * 
 * <p>Title:导出选中人员的权限</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Mar 4, 2009:4:19:41 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class IndividualManagementBo {
	private Connection conn = null;
	private RowSet frowset;
	private EncryptLockClient lock;
	/**
     * 　人员库串
     */
    private String db_str;
    /**
     * 功能串
     */
    private String func_str;
    /**
     * 管理范围串
     */
    private String manage_str;
    /**
     * 子集串
     */
    private String table_str;
    /**
     * 指标串
     */
    private String field_str;
    private UserView userView;
	public IndividualManagementBo(Connection a_con)
	{
		this.conn=a_con;
	}
	/*
	 * 头信息t_sys_role
	 */
	public ArrayList toprole(String [] roleid)
	{
		ArrayList list = new ArrayList();
		StringBuffer role_id = new StringBuffer();
		StringBuffer sql = new StringBuffer();
		try
		{
			for(int i=0;i<roleid.length;i++)
			{
				role_id.append("'");
				role_id.append(roleid[i]);
				role_id.append("',");
			}
			role_id.setLength(role_id.length()-1);
			sql.append("select role_name,role_id,role_property from t_sys_role");
			sql.append(" where role_id in ");
			sql.append("("+role_id.toString()+")");
			sql.append(" order by norder");
			ContentDAO da = new ContentDAO(this.conn);
			this.frowset = da.search(sql.toString());
			while(this.frowset.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("role_name", this.frowset.getString("role_name"));
				bean.set("role_id", this.frowset.getString("role_id"));
				bean.set("role_property",this.frowset.getString("role_property"));
				list.add(bean);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}finally{
			if(this.frowset!=null) {
                try {
                    this.frowset.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		return list;
	}
	//人员库左边信息
	public ArrayList getdbname()
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer buf = new StringBuffer();
			StringBuffer sql = new StringBuffer("select DBName,pre from DBNAME");
			 sql.append(" order by DbId");
			 ContentDAO da = new ContentDAO(this.conn);
			 this.frowset = da.search(sql.toString());
			 while(this.frowset.next()){
				 LazyDynaBean bean = new LazyDynaBean();
				 bean.set("DBName", this.frowset.getString("DBName"));
				 bean.set("dbid", this.frowset.getString("pre"));
				 list.add(bean);
			 }
		}
		catch(Exception e )
		{
			e.printStackTrace();
		}finally{
			if(this.frowset!=null) {
                try {
                    this.frowset.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		return list;
	}
	/*
	 * 写入人员库信息
	 */
	public void setdbname(ArrayList toplist,ArrayList dbnamelist,HSSFWorkbook workbook,HSSFRow row,HSSFCell cell) throws Exception
	{

        ResultSet rset=null;
		try
		{
			ContentDAO dao = new ContentDAO(conn);
			String flag=null; //表t_sys_function_priv 中的status字段值
			if(flag==null|| "".equals(flag)) {
                flag=GeneralConstant.ROLE;
            }
			
			HSSFSheet  sheet = null;
			sheet = workbook.createSheet(ResourceFactory.getProperty("infor.menu.base")); //生成一张表;人员库
			// 创建HSSFPatriarch对象,HSSFPatriarch是所有注释的容器. 
			HSSFPatriarch patr = sheet.createDrawingPatriarch();
			short h=0;//top
			short h1=0;//左边
			short h11=0;
			short k=2;
			short t=1;//序号
			short y=0;
//			 设置列宽,参数一，9列
			sheet.setColumnWidth((short)0,(short)2000);
			sheet.setColumnWidth((short)1,(short)3000);
			//row = sheet.createRow(h+0); // 定义是那一页的row
			row = sheet.getRow(h+0);
			if(row==null) {
                row = sheet.createRow(h+0);
            }

//			 合并单元格，参数，从第几行，该行的第几个单元格，到第几行，第几个单元格
			ExportExcelUtil.mergeCell(sheet, h+0,(short)0,h+1,(short)8); 
			cell=row.createCell((short)(0));  //写入的单元各位置;
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    		cell.setCellValue(ResourceFactory.getProperty("kjg.title.namshouquan")); //人员库授权
    		cell.setCellStyle(this.setDateStyle(workbook));
    		//写入top
    		//row= sheet.createRow(h+2);
    		row = sheet.getRow(h+2);
			if(row==null) {
                row = sheet.createRow(h+2);
            }
    		cell=row.createCell((short)(0));  //写入的单元各位置;
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    		cell.setCellValue(ResourceFactory.getProperty("kjg.gather.xuhao"));              //序号
    		cell.setCellStyle(this.settopStyle(workbook));
    		cell=row.createCell((short)(1));
    		//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    		cell.setCellValue(ResourceFactory.getProperty("kq.emp.change.nbase"));  //人员库
    		cell.setCellStyle(this.settopStyle(workbook));
    		for(int i=0;i<toplist.size();i++){  //top
    			LazyDynaBean bean = (LazyDynaBean)toplist.get(i);
    			//row= sheet.createRow(h+2);
    			row = sheet.getRow(h+2);
    			if(row==null) {
                    row = sheet.createRow(h+2);
                }
    			cell=row.createCell((short)(k));
    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
        		cell.setCellValue((String)bean.get("role_name"));
        		cell.setCellStyle(this.settopStyle(workbook));
        		//定义位置
        		HSSFComment comment = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) k , (h+2) , ( short ) (k) , (h+2) ));
        		//找到值
        		comment.setString( new HSSFRichTextString((String)bean.get("role_id")));
        		//comment.setAuthor("A001"); 作者
        		//写入
        		cell.setCellComment(comment);
        		
        		short list=row.getLastCellNum();                       //获得某一行的列数；
        		list--; //兼容excel2007
        		sheet.setColumnWidth((short)list,(short)3000);
        		StringBuffer strsql=new StringBuffer();
        		strsql.append("select dbpriv ");
                strsql.append(" from t_sys_function_priv where id='");
                strsql.append((String)bean.get("role_id"));
                strsql.append("' and status=");
                strsql.append(flag);
	            	rset=dao.search(strsql.toString());
	            	if(rset.next()){
	            		this.db_str=Sql_switcher.readMemo(rset,"dbpriv");//rset.getString("dbpriv");
	            	}
	            	else
	            	{
	            		this.db_str="";
	            	}
              
            	for(int n=0;n<dbnamelist.size();n++){
            		if(h1<dbnamelist.size())
            		{
            			LazyDynaBean beans = (LazyDynaBean)dbnamelist.get(n);
            			//row= sheet.createRow(h1+3);
            			row = sheet.getRow(h1+3);
            			if(row==null) {
                            row = sheet.createRow(h1+3);
                        }
            			cell=row.createCell((short)(1));
            			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                		cell.setCellValue((String)beans.get("DBName"));
                		//注释
                		HSSFComment comments = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) 1 , (h+3) , ( short ) 1 , (h+3) ));
                		comments.setString( new HSSFRichTextString((String)beans.get("dbid")));
                		cell.setCellComment(comments);
                		if(haveTheFunc(this.db_str,(String)beans.get("dbid")))
                		{
                			//row= sheet.createRow(h1+3);
                			row = sheet.getRow(h1+3);
                			if(row==null) {
                                row = sheet.createRow(h1+3);
                            }
                			cell=row.createCell((short)(k));
                			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                    		cell.setCellValue("有");
                		}
                		else
                		{
                			//row= sheet.createRow(h1+3);
                			row = sheet.getRow(h1+3);
                			if(row==null) {
                                row = sheet.createRow(h1+3);
                            }
                			cell=row.createCell((short)(k));
                			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                    		cell.setCellValue("无");
                		}
            		//全部写无的部分
//            		for(int r=2;r<k;r++){
//            			cell=row.createCell((short)(r));
//            			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
//                		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
//                		cell.setCellValue("无");
//            		}
            		//        		cell.setCellStyle(this.setbodyStyle(workbook));
            		cell=row.createCell((short)(0));
            		//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
            		cell.setCellValue(t);

            		t++;
            		h1++;
            		}
            		else
            		{
            			LazyDynaBean beans = (LazyDynaBean)dbnamelist.get(n);
                		if(haveTheFunc(this.db_str,(String)beans.get("dbid")))
                		{
                			//row= sheet.createRow(h11+3);
                			row = sheet.getRow(h11+3);
                			if(row==null) {
                                row = sheet.createRow(h11+3);
                            }
                			cell=row.createCell((short)(k));
                			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                    		cell.setCellValue("有");
                		}
                		else
                		{
                			//row= sheet.createRow(h11+3);
                			row = sheet.getRow(h11+3);
                			if(row==null) {
                                row = sheet.createRow(h11+3);
                            }
                			cell=row.createCell((short)(k));
                			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                    		cell.setCellValue("无");
                		}
                		h11++;
            		}
            		if(h11==dbnamelist.size()) {
                        h11=0;
                    }
            			
        		}
            	k++;
    		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} finally{
        	if(rset!=null){
        		rset.close();
        	}
        	
        }
	}
	/**
     * 当前对象是否有
     * @param func_str ，用户已授权的功能串列如 ,2020,30,
     * @param func_id
     * @return
     */
    private boolean haveTheFunc(String func_str,String func_id)
    {
    	if(func_str.indexOf(","+func_id+",")==-1) {
            return false;
        } else {
            return true;
        }
    }
    /*
	 * 查询xml
	 */
    private ArrayList AllList =new ArrayList();
    private int lay=0;
	public ArrayList searchFunctionXmlHtml(){
		ArrayList lists = new ArrayList();
		InputStream in=this.getClass().getResourceAsStream("/com/hjsj/hrms/constant/function.xml");
		try{
			Document doc = PubFunc.generateDom(in);; //得到xml
			Element root = doc.getRootElement(); //得到root元素
			/**版本之间的差异控制，市场考滤*/
	        VersionControl ver_ctrl=new VersionControl();
			List list = root.getChildren("function");  //得到根部的孩子返回所有子节点的数组
			for (int i = 0; i < list.size(); i++){
				 Element node = (Element) list.get(i);
				 LazyDynaBean bean = new LazyDynaBean();
				 String func_id=node.getAttributeValue("id");
				 String func_name=node.getAttributeValue("name");
				 if(ver_ctrl.searchFunctionId(func_id)){
					 bean.set("id",func_id);
					 bean.set("name", func_name);
					 bean.set("level","1");
					 AllList.add(bean);
					 doMethod(node,1);
				 }
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		    if(in!=null){
		        PubFunc.closeIoResource(in);
		    }
		}
		return AllList;
	}
	public void doMethod(Element element,int level)
	{
		/**有几个指标固定的 **/
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);//判断
		String inputchinfor=sysbo.getValue(Sys_Oth_Parameter.INPUTCHINFOR);
		String approveflag=sysbo.getValue(Sys_Oth_Parameter.APPROVE_FLAG);
		HashMap filtrateMap=filtrateId(inputchinfor,approveflag);
		
		/**版本之间的差异控制，市场考滤*/
        VersionControl ver_ctrl=new VersionControl();
		List list= element.getChildren();
		level++;

			if(lay<level) {
                lay=level;
            }
		for(int i=0;i<list.size();i++)
		{
			 Element childElement=(Element)list.get(i);    //得到底下的孩子
			 LazyDynaBean bean=new LazyDynaBean();
			 String func_id=childElement.getAttributeValue("id");
			 String func_name=childElement.getAttributeValue("name");
			 if(!ver_ctrl.searchFunctionId(func_id)) {
                 continue;
             }
			 if(isFiltrate(func_id,filtrateMap)) {
                 continue;
             }
			 bean.set("id",func_id);
			 bean.set("name", func_name);
			 bean.set("level", level+"");
			 AllList.add(bean);
			 getParentLinkMap(childElement,func_id);
			 doMethod(childElement,level);
		}
	}
	public void getParentLinkMap(Element parentElement,String id)
	{
		HashMap map= new HashMap();
		if(!parentElement.getParentElement().isRootElement()&&parentElement.getParentElement()!=null)
		{
			if(map.get(id.toUpperCase())!=null)
			{
				ArrayList list=(ArrayList)map.get(id.toUpperCase());
				list.add(parentElement.getParentElement());
				map.put(id.toUpperCase(), list);
			}
			else
			{
				ArrayList list= new ArrayList();
				list.add(parentElement.getParentElement());
				map.put(id.toUpperCase(), list);
			}
			getParentLinkMap(parentElement.getParentElement(),id);
		}
	}
	  /**
     * 返回需要过滤的idMap
     * @param inputchinfor
     * @param approveflag
     * @return
     */
    private HashMap filtrateId(String inputchinfor,String approveflag)
    {
    	HashMap hashMap=new HashMap();
    	if("1".equals(inputchinfor)&& "1".equals(approveflag))
    	{
    		hashMap.put("01030115", "01030115");//整体报批
    		hashMap.put("03084", "03084");//整体批准
    		hashMap.put("03083", "03083");//整体驳回
    		hashMap.put("260633", "260633");//批准
    		hashMap.put("260634", "260634");//整体驳回
    	}else
    	{
    		hashMap.put("01030106", "01030106");//我的变动信息明细
    		hashMap.put("03085", "03085");//删除    		
    		hashMap.put("260635", "260635");//删除 
    	}
    	return hashMap;
    }
    /**
     * 过滤功能号 
     * @param id
     * @param map
     * @return
     */
    private boolean isFiltrate(String id,HashMap map)
    {
    	boolean isCorrect=false;
    	if(map!=null)
    	{
    		String filtrateid=(String)map.get(id);
    		if(filtrateid!=null&&filtrateid.length()>0)
    		{
    			isCorrect=true;
    		}	
    	}
    	return isCorrect;    
    }
    /*
	 * 写入功能权限excel
	 */
	public void setxmlexcel(ArrayList AllList,HSSFWorkbook workbook, HSSFRow row, HSSFCell cell,ArrayList toplist) throws Exception
	{
		
		ResultSet rset=null;
		try
		{
			ContentDAO dao = new ContentDAO(conn);
			short h=0;
			short h1=0;
			short k=(short) lay;
			short f=1; //序号
			short lists=0;
			String flag=null; //表t_sys_function_priv 中的status字段值
			if(flag==null|| "".equals(flag)) {
                flag=GeneralConstant.ROLE;
            }
			CellRangeAddress region = null;
			HSSFSheet sheet= workbook.createSheet(ResourceFactory.getProperty("kjg.title.functionshouquan")); //功能授权
			   // 创建HSSFPatriarch对象,HSSFPatriarch是所有注释的容器. 
	          HSSFPatriarch patr = sheet.createDrawingPatriarch();	
	          row = sheet.getRow(0);
	          if(row==null) {
                  row = sheet.createRow(0); // 定义是那一页的row
              }
//				 合并单元格，参数，从第几行，该行的第几个单元格，到第几行，第几个单元格
				ExportExcelUtil.mergeCell(sheet, 0,(short)0,1,(short)10);
				cell=row.createCell((short)(0));  //写入的单元各位置;
				//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
	    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
	    		cell.setCellValue(ResourceFactory.getProperty("kjg.title.functionshouquan"));  //功能授权
	    		cell.setCellStyle(this.setDateStyle(workbook));
	    		//写入top
	    		row = sheet.getRow(2);
		          if(row==null) {
                      row= sheet.createRow(2);
                  }
	    		cell=row.createCell((short)(0));  //写入的单元各位置;
				//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
	    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
	    		cell.setCellValue(ResourceFactory.getProperty("kh.field.seq")); //序号
	    		cell.setCellStyle(this.settopStyle(workbook));
	    		if(lay > 2) {
	    			region = new CellRangeAddress(2,2,(short)1,(short)(lay-1));
	    			sheet.addMergedRegion(region);
	    			HSSFCellStyle cs=this.settopStyle(workbook);
	    			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
	    				//row = sheet.createRow(p);
	    				row = sheet.getRow(p);
	    				if(row==null) {
                            row = sheet.createRow(p);
                        }
	    				for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
	    					cell = row.createCell((short)o);
	    					cell.setCellStyle(cs);
	    				}
	    			}
	    		}
	    		
	    		cell=row.createCell((short)(1));  //写入的单元各位置;
	    		//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
	    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
	    		cell.setCellValue(ResourceFactory.getProperty("kjg.title.module"));  //模 块
	    		cell.setCellStyle(this.settopStyle(workbook));
	    		for(int t=0;t<toplist.size();t++){  //top
	    			LazyDynaBean bean = (LazyDynaBean)toplist.get(t);
	    			row = sheet.getRow(2);
	  	          if(row==null) {
                      row= sheet.createRow(2);
                  }
	    			cell=row.createCell((short)(k));
	    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
	        		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
	        		cell.setCellValue((String)bean.get("role_name"));
	        		cell.setCellStyle(this.settopStyle(workbook));
	        		//注释
					HSSFComment comm = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) k , 2 , ( short ) (k+1) , 3 ));
	        		comm.setString( new HSSFRichTextString((String)bean.get("role_id")));
	        		cell.setCellComment(comm);
	        		cell.setCellStyle(this.settopStyle(workbook));
	        		lists=row.getLastCellNum();                       //获得某一行的列数；
	        		lists--; //兼容excel2007
	        		sheet.setColumnWidth((short)lists,(short)3000);
	        		StringBuffer strsql=new StringBuffer();
	        		strsql.append("select functionpriv ");
	                strsql.append(" from t_sys_function_priv where id='");
	                strsql.append((String)bean.get("role_id"));
	                strsql.append("' and status=");
	                strsql.append(flag);
		            	rset=dao.search(strsql.toString());
		            	if(rset.next()){
		            		this.func_str=Sql_switcher.readMemo(rset,"functionpriv");//rset.getString("dbpriv");
		            	}
		            	else
		            	{
		            		this.func_str="";
		            	}
	               
	            	for(int n=0;n<AllList.size();n++){
	            		if(h<AllList.size())
	            		{
	            			LazyDynaBean beans = (LazyDynaBean)AllList.get(n);
							String level = (String)beans.get("level");
							int lece = Integer.parseInt(level);
								//row= sheet.createRow((short)h+3); //行
							row = sheet.getRow(h+3);
                			if(row==null) {
                                row = sheet.createRow(h+3);
                            }
								cell=row.createCell((short)lece);
								//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				    			cell.setCellValue((String)beans.get("name"));
				    			//注释
		        				HSSFComment comment = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) (lece+1) , (h+2) , ( short ) (lece+2) , (h+4) ));
		        				comment.setString( new HSSFRichTextString((String)beans.get("id")));
		                		cell.setCellComment(comment);
		                		if(haveTheFunc2(this.func_str,(String)beans.get("id")))
		                		{
		                			//row= sheet.createRow((short)h+3); //行
		                			row = sheet.getRow(h+3);
		                			if(row==null) {
                                        row = sheet.createRow(h+3);
                                    }
		                			cell=row.createCell((short)k);
		                			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					    			cell.setCellValue("有");
		                		}
		                		else
		                		{
		                			//row= sheet.createRow((short)h+3); //行
		                			row = sheet.getRow(h+3);
		                			if(row==null) {
                                        row = sheet.createRow(h+3);
                                    }
		                			cell=row.createCell((short)k);
		                			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					    			cell.setCellValue("无");
		                		}
				    			cell = row.createCell((short)0);
			        			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			        			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			        			cell.setCellValue(f);
			        			f++;
				    			h++;
	            		}
	            		else
	            		{
	            			LazyDynaBean beans = (LazyDynaBean)AllList.get(n);
							String level = (String)beans.get("level");
							int lece = Integer.parseInt(level);
		                		if(haveTheFunc2(this.func_str,(String)beans.get("id")))
		                		{
		                			//row= sheet.createRow((short)h1+3); //行
		                			row = sheet.getRow(h1+3);
		                			if(row==null) {
                                        row = sheet.createRow(h1+3);
                                    }
		                			cell=row.createCell((short)k);
		                			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					    			cell.setCellValue("有");
		                		}
		                		else
		                		{
		                			//row= sheet.createRow((short)h1+3); //行
		                			row = sheet.getRow(h1+3);
		                			if(row==null) {
                                        row = sheet.createRow(h1+3);
                                    }
		                			cell=row.createCell((short)k);
		                			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					    			cell.setCellValue("无");
		                		}
				    			h1++;
				    			if(h1==AllList.size()) {
                                    h1=0;
                                }
	            		}
	            		
					}
	        		k++;
	    		}
	    		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
        	if(rset!=null){
        		rset.close();
        	}
        	
        }
	}
	/**
     * 当前对象是否有
     * @param func_str ，用户已授权的功能串列如 ,2020,30,
     * @param func_id
     * @return
     */
    private boolean haveTheFunc2(String func_str,String func_id)
    {
    	if(func_str.indexOf(","+func_id+",")==-1) {
            return false;
        } else {
            return true;
        }
    }
    /*
	 *管理范围查询 
	 */
	private HashMap map =null;
	private HashMap codesetmap;
	private ArrayList itemlist;
	private ArrayList parentlist;
	private ArrayList lll;
	private HashMap mm;
	ArrayList listvalue = new ArrayList();
	ArrayList list =new ArrayList();
	public ArrayList uuu(){
		RowSet rs = null;
		try{
		/***组织机构表*/
		map = new HashMap();
		rs = null;
		itemlist=new ArrayList();
		parentlist = new ArrayList();
		codesetmap=new HashMap();
		lll = new ArrayList();
		mm=new HashMap();
		String sql = " select * from organization order by codesetid,codeitemid";
		ContentDAO das = new ContentDAO(this.conn);
		rs = das.search(sql);
	    while(rs.next())
	    {
	    	LazyDynaBean bean = new LazyDynaBean();
	    	bean.set("codesetid",rs.getString("codesetid"));
	    	bean.set("codeitemid",rs.getString("codeitemid"));
	    	bean.set("parentid",rs.getString("parentid"));
	    	//进行一下null值转换，否则下面去的使用用(String)bean.get("codeitemdesc")时会报 转换失败 guodd 2019-06-25
	    	String codedesc = rs.getString("codeitemdesc");
	    	if(codedesc==null) {
                codedesc = "";
            }
	    	bean.set("codeitemdesc",codedesc);
	    	if(codesetmap.get(rs.getString("parentid").toUpperCase())==null)
	    	{
	    		ArrayList lllll=new ArrayList();
		    	lllll.add(bean);
		    	codesetmap.put(rs.getString("parentid").toUpperCase(), lllll);
	    	}
	    	else
	    	{
	    		ArrayList lllll=(ArrayList)codesetmap.get(rs.getString("parentid").toUpperCase());
		    	lllll.add(bean);
		    	codesetmap.put(rs.getString("parentid").toUpperCase(), lllll);
	    	}
	    	itemlist.add(bean);
	    	if(rs.getString("parentid").equals(rs.getString("codeitemid")))
	    	{
	    		parentlist.add(bean);  //9个
	    	}
	    	else
	    	{
	    		lll.add(bean);
	    	}
	    }
	    mm=this.getLeafItemLinkMap(itemlist,"organization");
	    LazyDynaBean tbean=new LazyDynaBean();
	    tbean.set("codeitemdesc", "组织机构");
		tbean.set("codeitem_id", "");
		tbean.set("codeset_id", "UN");
		tbean.set("lang","1");
		listvalue.add(tbean);
		for(int j=0;j<parentlist.size();j++)
		{
			LazyDynaBean pbean=(LazyDynaBean)parentlist.get(j);
			String codesetid=(String)pbean.get("codesetid");
			String codeitemid=(String)pbean.get("codeitemid");
			String codeitemdesc=(String)pbean.get("codeitemdesc");
			String parentid=(String)pbean.get("parentid");
			
			if(this.map.get((codesetid+codeitemid).toUpperCase())==null)
			{ 
				pbean.set("codeitemdesc", codeitemdesc);
				pbean.set("codeitem_id", codeitemid);
				pbean.set("codeset_id", codesetid);
				pbean.set("lang","2");
				listvalue.add(pbean);
	        	map.put((codesetid+codeitemid).toUpperCase(), "1");
			}
	    	this.ffff(codesetid, codeitemid, parentid, listvalue,2,conn,das);
		}
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return listvalue;
	}
	
	public  HashMap getLeafItemLinkMap(ArrayList list,String tablename)
	{
		HashMap map=new HashMap();
		try
		{
			LazyDynaBean abean=null;
			for(int i=0;i<list.size();i++)
			{
				abean=(LazyDynaBean)list.get(i);
				String item_id=(String)abean.get("codeitemid");
				String parent_id=(String)abean.get("parentid");
				String codesetid=(String)abean.get("codesetid");
				ArrayList linkList=new ArrayList();
				getParentItem(linkList,abean,tablename);
				map.put((codesetid+item_id).toUpperCase(),linkList);  //@k+自己代号 9
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public void getParentItem(ArrayList list,LazyDynaBean abean,String tablename)
	{
		String item_id=(String)abean.get("codeitemid");
		String parent_id=(String)abean.get("parentid");
		String codeset_id=(String)abean.get("codesetid");
		LazyDynaBean a_bean=null;
		String sstr="";
		if("organization".equalsIgnoreCase(tablename)) {
            sstr=item_id;
        } else {
            sstr=codeset_id;
        }
		ArrayList llll=(ArrayList)codesetmap.get(sstr.toUpperCase());
		if(llll==null||llll.size()==0) {
            return;
        }
		/***原先循环的事itemlist*/
		for(int i=0;i<llll.size();i++)
		{
			a_bean=(LazyDynaBean)llll.get(i);
			String itemid=(String)a_bean.get("codeitemid");
			String parentid=(String)a_bean.get("parentid");
			String codesetid=(String)a_bean.get("codesetid");
			if("organization".equalsIgnoreCase(tablename))
			{
				if(item_id.equals(parentid)&&!itemid.equals(parentid))
	    		{
	    			list.add(a_bean);
	    			getParentItem(list,a_bean,tablename);
	    		}
			}
			else
			{
	    		if(item_id.equals(parentid)&&!itemid.equals(parentid)&&codeset_id.equalsIgnoreCase(codesetid))
	    		{
	    			list.add(a_bean);
	    			getParentItem(list,a_bean,tablename);
	    		}
			}
		}				
	}
	private int gong=0;
	public void ffff(String codesetid,String codeitemid,String parentid,ArrayList listvalue,int lay,Connection conn,ContentDAO das)
	{
		try
		{
			ArrayList list = (ArrayList)mm.get((codesetid+codeitemid).toUpperCase());
			if(list==null||list.size()==0) {
                return;
            }
			lay++;
			if(gong<lay) {
                gong=lay;
            }
			for(int i=0;i<list.size();i++)
			{
				
				LazyDynaBean pbean=(LazyDynaBean)list.get(i);
				String codeset_id=(String)pbean.get("codesetid");
				String codeitem_id=(String)pbean.get("codeitemid");
				String codeitemdesc=(String)pbean.get("codeitemdesc");
				if(this.map.get((codeset_id+codeitem_id).toUpperCase())==null)
				{
					pbean.set("codeitem_id",codeitem_id);
					pbean.set("codeitemdesc",codeitemdesc);
					pbean.set("codeset_id",codeset_id);
					pbean.set("lang",lay+"");
					listvalue.add(pbean);
    	        	map.put((codeset_id+codeitem_id).toUpperCase(), "1");
				}
				if(mm.get((codeset_id+codeitem_id).toUpperCase())==null||((ArrayList)mm.get((codeset_id+codeitem_id).toUpperCase())).size()==0)
				{
					continue;
					
				}else{
	    			this.ffff(codeset_id, codeitem_id, parentid, listvalue, lay,conn,das);
				}
    	    	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	//生成管理范围ecxel
	public void setmanagementexcel(ArrayList toplist,ArrayList listvalue,HSSFWorkbook workbook, HSSFRow row, HSSFCell cell) throws Exception 
	{

        ResultSet rset=null;
		try
		{
			ContentDAO dao = new ContentDAO(conn);
			String flag=null; //表t_sys_function_priv 中的status字段值
			if(flag==null|| "".equals(flag)) {
                flag=GeneralConstant.ROLE;
            }
			short h = 0;
			short h1 = 0;
			short h11=0;
			short k = (short)gong;
			short t = 1;
			short lt=0;//top
			short f=1;//序号
			CellRangeAddress region = null;
			HSSFSheet sheet = workbook.createSheet(ResourceFactory.getProperty("menu.manage")); //生成一张表; 管理范围
			 // 创建HSSFPatriarch对象,HSSFPatriarch是所有注释的容器. 
	        HSSFPatriarch patr = sheet.createDrawingPatriarch();
//			 设置列宽,参数一，10列
	        row = sheet.getRow(0);
	          if(row==null) {
                  row = sheet.createRow(0);
              }
			ExportExcelUtil.mergeCell(sheet, 0,(short)0,1,(short)10);
			cell=row.createCell((short)(0));
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    		cell.setCellValue(ResourceFactory.getProperty("menu.manage"));  //管理范围
    		cell.setCellStyle(this.setDateStyle(workbook));
    		//写入top
    		row = sheet.getRow(2);
	          if(row==null) {
                  row = sheet.createRow(2);  //row位置
              }
    		cell=row.createCell((short)(0));  //cell位置
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    		cell.setCellValue(ResourceFactory.getProperty("recidx.label"));   //序号
    		cell.setCellStyle(this.settopStyle(workbook));
    		if(gong>0){
    			HSSFCellStyle cs=this.settopStyle(workbook);
    			if(gong > 1) {
    				region = new CellRangeAddress(2,2,(short)1,(short)gong);
    				sheet.addMergedRegion(region);
    				for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
    					//row = sheet.createRow(p);
    					row = sheet.getRow(p);
    					if(row==null) {
                            row = sheet.createRow(p);
                        }
    					for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
    						cell = row.createCell((short)o);
    						cell.setCellStyle(cs);
    					}
    				}
    			} else {
    				row = sheet.getRow(2);
    				if(row==null) {
                        row = sheet.createRow(2);
                    }
    				
    				cell = row.createCell((short) 1);
    				cell.setCellStyle(cs);
    			}
        		cell=row.createCell((short)(1));  //cell位置
    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        		cell.setCellValue(ResourceFactory.getProperty("tree.unroot.undesc")+"    "+ResourceFactory.getProperty("tree.umroot.umdesc"));   //单位
        		cell.setCellStyle(this.settopStyle(workbook));
        		cell.setCellStyle(this.settopStyle(workbook));
    		}else if(gong==0){
    			row = sheet.getRow(2);
  	          if(row==null) {
                  row = sheet.createRow(2);  //row位置
              }
    			cell=row.createCell((short)(1));  //cell位置
    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        		cell.setCellValue(ResourceFactory.getProperty("tree.unroot.undesc")+"    "+ResourceFactory.getProperty("tree.umroot.umdesc"));   //单位
        		cell.setCellStyle(this.settopStyle(workbook));
        		cell.setCellStyle(this.settopStyle(workbook));
    		}
    		for(int i=0;i<toplist.size();i++){        //top
    			LazyDynaBean bean = (LazyDynaBean)toplist.get(i);
    			row = sheet.getRow(2);
  	          if(row==null) {
                  row= sheet.createRow(2);
              }
    			if(gong==0){
//    				String role_property =(String)bean.get("role_property");
//    				if(role_property.equalsIgnoreCase("1")||role_property.equalsIgnoreCase("5")||role_property.equalsIgnoreCase("6")||role_property.equalsIgnoreCase("7"))
//    				{
////    					break;
//    					continue;
//    				}
    				cell=row.createCell((short)(k+2));
        			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        			cell.setCellValue((String)bean.get("role_name"));
        			//注释
    				HSSFComment comm = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) k , 2 , ( short ) (k+1) , 3 ));
            		comm.setString( new HSSFRichTextString((String)bean.get("role_id")));
            		cell.setCellComment(comm);
        			cell.setCellStyle(this.settopStyle(workbook));
        			lt = row.getLastCellNum();
        			lt--; //兼容excel2007
        			sheet.setColumnWidth((short)lt,(short)3000);
        			StringBuffer strsql=new StringBuffer();
            		strsql.append("select managepriv ");
                    strsql.append(" from t_sys_function_priv where id='");
                    strsql.append((String)bean.get("role_id"));
                    strsql.append("' and status=");
                    strsql.append(flag);
                    	rset=dao.search(strsql.toString());
	                	if(rset.next()){
	                		this.manage_str=Sql_switcher.readMemo(rset,"managepriv");//rset.getString("dbpriv");
	                	}
	                	else
	                	{
	                		this.manage_str="";
	                	}
                	for(int n=0;n<listvalue.size();n++){
                		if(h<listvalue.size())
                		{
                			LazyDynaBean beans = (LazyDynaBean)listvalue.get(n);
            	        	  String lang = (String)beans.get("lang");
            	        	  int lece = Integer.parseInt(lang);
            	        	  //row= sheet.createRow((short)h+3); //行
            	        	  row = sheet.getRow(h+3);
	                			if(row==null) {
                                    row = sheet.createRow(h+3);
                                }
            	        	  cell=row.createCell((short)lece);
            	        	 // cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            	        	  cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            	    		  cell.setCellValue((String)beans.get("codeitemdesc"));
            	    		//注释
              				HSSFComment comment = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) (lece-1) , (h+3) , ( short ) lece , (h+5) ));
              				comment.setString( new HSSFRichTextString((String)beans.get("codeset_id")+(String)beans.get("codeitem_id")));
                      		cell.setCellComment(comment);
                      		if(haveTheFunc3(this.manage_str,((String)beans.get("codeset_id")+(String)beans.get("codeitem_id"))))
                      		{
                      			//row= sheet.createRow((short)h+3); //行
                      			row = sheet.getRow(h+3);
	                			if(row==null) {
                                    row = sheet.createRow(h+3);
                                }
                      			cell=row.createCell((short)(k+2));
                      			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				    			cell.setCellValue("有");
                      		}
                      		else
                      		{
                      			//row= sheet.createRow((short)h+3); //行
                      			row = sheet.getRow(h+3);
	                			if(row==null) {
                                    row = sheet.createRow(h+3);
                                }
                      			cell=row.createCell((short)(k+2));
                      			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				    			cell.setCellValue("无");
                      		}
                			cell = row.createCell((short)0);
                			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                			cell.setCellValue(f);
                			f++;
                			h++;
                		}
                		else
                		{
	                		  LazyDynaBean beans = (LazyDynaBean)listvalue.get(n);
	                    		if(haveTheFunc3(this.manage_str,((String)beans.get("codeset_id")+(String)beans.get("codeitem_id"))))
	                    		{
	                    			//row= sheet.createRow((short)h1+3); //行
	                    			row = sheet.getRow(h1+3);
		                			if(row==null) {
                                        row = sheet.createRow(h1+3);
                                    }
	                    			cell=row.createCell((short)(k+2));
	                    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					    			cell.setCellValue("有");
	                    		}
	                    		else
	                    		{
	                    			//row= sheet.createRow((short)h1+3); //行
	                    			row = sheet.getRow(h1+3);
		                			if(row==null) {
                                        row = sheet.createRow(h1+3);
                                    }
	                    			cell=row.createCell((short)(k+2));
	                    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					    			cell.setCellValue("无");
	                    		}
	              			h1++;
	              			if(h1==listvalue.size()) {
                                h1=0;
                            }
                		}
      	        	  
      	          }
    			}else if(gong>0){
//    				String role_property =(String)bean.get("role_property");
//    				if(role_property.equalsIgnoreCase("1")||role_property.equalsIgnoreCase("5")||role_property.equalsIgnoreCase("6")||role_property.equalsIgnoreCase("7"))
//    				{
////    					break;
//    					continue;
//    				}
    				cell=row.createCell((short)(k+1));
        			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        			cell.setCellValue((String)bean.get("role_name"));
        			//注释
    				HSSFComment comm = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) k , 2 , ( short ) (k+1) , 3 ));
            		comm.setString( new HSSFRichTextString((String)bean.get("role_id")));
            		cell.setCellComment(comm);
        			cell.setCellStyle(this.settopStyle(workbook));
        			lt = row.getLastCellNum();
        			lt--; //兼容excel2007
        			sheet.setColumnWidth((short)lt,(short)3000);
        			StringBuffer strsql=new StringBuffer();
            		strsql.append("select managepriv ");
                    strsql.append(" from t_sys_function_priv where id='");
                    strsql.append((String)bean.get("role_id"));
                    strsql.append("' and status=");
                    strsql.append(flag);
	                	rset=dao.search(strsql.toString());
	                	if(rset.next()){
	                		this.manage_str=Sql_switcher.readMemo(rset,"managepriv");//rset.getString("dbpriv");
	                	}
	                	else
	                	{
	                		this.manage_str="";
	                	}
                	for(int n=0;n<listvalue.size();n++){
                		if(h<listvalue.size())
                		{
                			LazyDynaBean beans = (LazyDynaBean)listvalue.get(n);
            	        	  String lang = (String)beans.get("lang");
            	        	  int lece = Integer.parseInt(lang);
            	        	  //row= sheet.createRow((short)h+3); //行
            	        	  row = sheet.getRow(h+3);
	                			if(row==null) {
                                    row = sheet.createRow(h+3);
                                }
            	        	  cell=row.createCell((short)lece);
            	        	  //cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            	        	  cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            	    		  cell.setCellValue((String)beans.get("codeitemdesc"));
            	    		//注释
              				HSSFComment comment = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) (lece-1) , (h+3) , ( short ) lece , (h+5) ));
              				comment.setString( new HSSFRichTextString((String)beans.get("codeset_id")+(String)beans.get("codeitem_id")));
                      		cell.setCellComment(comment);
                      		if(haveTheFunc3(this.manage_str,((String)beans.get("codeset_id")+(String)beans.get("codeitem_id"))))
                      		{
                      			//row= sheet.createRow((short)h+3); //行
                      			row = sheet.getRow(h+3);
	                			if(row==null) {
                                    row = sheet.createRow(h+3);
                                }
                      			cell=row.createCell((short)(k+1));
                      			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				    			cell.setCellValue("有");
                      		}
                      		else
                      		{
                      			//row= sheet.createRow((short)h+3); //行
                      			row = sheet.getRow(h+3);
	                			if(row==null) {
                                    row = sheet.createRow(h+3);
                                }
                      			cell=row.createCell((short)(k+1));
                      			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				    			cell.setCellValue("无");
                      		}
                			cell = row.createCell((short)0);
                			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                			cell.setCellValue(f);
                			f++;
                			h++;
                		}
                		else
                		{
                			
	                		  LazyDynaBean beans = (LazyDynaBean)listvalue.get(n);
	          	        	  String lang = (String)beans.get("lang");
	          	        	  int lece = Integer.parseInt(lang);
	                    		if(haveTheFunc3(this.manage_str,((String)beans.get("codeset_id")+(String)beans.get("codeitem_id"))))
	                    		{
	                    			//row= sheet.createRow((short)h11+3); //行
	                    			row = sheet.getRow(h11+3);
		                			if(row==null) {
                                        row = sheet.createRow(h11+3);
                                    }
	                    			cell=row.createCell((short)(k+1));
	                    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					    			cell.setCellValue("有");
	                    		}
	                    		else
	                    		{
	                    			//row= sheet.createRow((short)h11+3); //行
	                    			row = sheet.getRow(h11+3);
		                			if(row==null) {
                                        row = sheet.createRow(h11+3);
                                    }
	                    			cell=row.createCell((short)(k+1));
	                    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					    			cell.setCellValue("无");
	                    		}
	              			h11++;
	              			if(h11==listvalue.size()) {
                                h11=0;
                            }
                		}
      	        	  
      	          }
    			}
    			k++;
    		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			if(rset!=null){
        		rset.close();
        	}
		}
	}
	/**
     * 当前对象是否有
     * @param func_str ，用户已授权的功能串列如 ,2020,30,
     * @param func_id
     * @return
     */
    private boolean haveTheFunc3(String func_str,String func_id)
    {
    	if(func_str.equalsIgnoreCase(func_id)) {
            return true;
        } else {
            return false;
        }
    }
    /*
	 * 指标子集授权查询
	 */
	
	public HashMap infomap(){
		HashMap map = new HashMap();
		try{
			ArrayList list = new ArrayList();
//			ArrayList subsetlist= new ArrayList();
			StringBuffer buf = new StringBuffer("select classname,classpre from informationclass  order by  inforid");
			ContentDAO da = new ContentDAO(this.conn);
			this.frowset = da.search(buf.toString());
			while(this.frowset.next()){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("classname", this.frowset.getString("classname")+ResourceFactory.getProperty("system.param.sysinfosort.subset"));//子集
				bean.set("classpre", this.frowset.getString("classpre"));
				list.add(bean);
			}
			HashMap amap= new HashMap();
			for(int i=0;i<list.size();i++){
				LazyDynaBean prebean = (LazyDynaBean)list.get(i);
				String classpre = (String)prebean.get("classpre");
				StringBuffer subset= new StringBuffer("select fieldsetid,customdesc from fieldset  where fieldsetid like '");
					subset.append(classpre+"%' and useflag='1' order by displayorder ");
					ContentDAO das = new ContentDAO(this.conn);
					
					this.frowset = das.search(subset.toString());
					while(this.frowset.next()){
						LazyDynaBean infobean = new LazyDynaBean();
						infobean.set("fieldsetid", this.frowset.getString("fieldsetid"));
						infobean.set("customdesc", this.frowset.getString("customdesc"));
//						infobean.set("classpre", classpre);
						if(amap.get(classpre.toUpperCase())==null)
						{
							ArrayList setlist = new ArrayList();
							setlist.add(infobean);
							amap.put(classpre.toUpperCase(), setlist); //把list放到map里
						}
						else
						{
							ArrayList setlist=(ArrayList)amap.get(classpre.toUpperCase());
							setlist.add(infobean);
							amap.put(classpre.toUpperCase(), setlist);
						}
					}
			}
			ArrayList sublist = new ArrayList();
			LazyDynaBean mubean = new LazyDynaBean();
			mubean.set("ryid", "A00");
			mubean.set("ryname", "人员多媒体子集");
			mubean.set("dwid","B00");
			mubean.set("dwname","单位多媒体子集");
			mubean.set("id","K00");
			mubean.set("subsetname","职位多媒体子集");
			sublist.add(mubean);
			//指标
			HashMap zbmap= new HashMap();
			
			for(int p=0;p<list.size();p++){
				LazyDynaBean indexbean = (LazyDynaBean)list.get(p);
				ArrayList indexlist = (ArrayList)amap.get((String)indexbean.get("classpre"));
				if(indexlist!=null){
					for(int u=0;u<indexlist.size();u++){
						
						LazyDynaBean zbbean = (LazyDynaBean)indexlist.get(u);
						String fieldsetid = (String)zbbean.get("fieldsetid");
						if("A01".equalsIgnoreCase(fieldsetid)){
							StringBuffer subbean=new StringBuffer("select fieldsetid,itemid,itemdesc from fielditem where fieldsetid='");
							subbean.append(fieldsetid+"' and useflag='1' order by displayid");
							ContentDAO das = new ContentDAO(this.conn);
							this.frowset = das.search(subbean.toString());
							ArrayList setlist = new ArrayList();
							LazyDynaBean itembean1 = new LazyDynaBean();
							itembean1.set("fieldsetid","A01");
							itembean1.set("itemid", "B0110");
							itembean1.set("itemdesc", "单位名称");
							setlist.add(itembean1);
							zbmap.put("A01", setlist);
							LazyDynaBean itembean2 = new LazyDynaBean();
							itembean2.set("fieldsetid","A01");
							itembean2.set("itemid", "E01A1");
							itembean2.set("itemdesc", "职位名称");
							setlist.add(itembean2);
							zbmap.put("A01", setlist);
							while(this.frowset.next()){
								LazyDynaBean itembean = new LazyDynaBean();
								itembean.set("fieldsetid", this.frowset.getString("fieldsetid"));
								itembean.set("itemid", this.frowset.getString("itemid"));
								itembean.set("itemdesc", this.frowset.getString("itemdesc"));
								if(zbmap.get(frowset.getString("fieldsetid").toUpperCase())==null)
								{
									
									setlist.add(itembean);
									zbmap.put(frowset.getString("fieldsetid").toUpperCase(), setlist); //把list放到map里
								}
								else
								{
									setlist=(ArrayList)zbmap.get(frowset.getString("fieldsetid").toUpperCase());
									setlist.add(itembean);
									zbmap.put(frowset.getString("fieldsetid").toUpperCase(), setlist);
								}
							}
							
						}else{
							StringBuffer subbean=new StringBuffer("select fieldsetid,itemid,itemdesc from fielditem where fieldsetid='");
							subbean.append(fieldsetid+"' and useflag='1' order by displayid");
							ContentDAO das = new ContentDAO(this.conn);
							this.frowset = das.search(subbean.toString());
							while(this.frowset.next()){
								LazyDynaBean itembean = new LazyDynaBean();
								itembean.set("fieldsetid", this.frowset.getString("fieldsetid"));
								itembean.set("itemid", this.frowset.getString("itemid"));
								itembean.set("itemdesc", this.frowset.getString("itemdesc"));
								if(zbmap.get(frowset.getString("fieldsetid").toUpperCase())==null)
								{
									ArrayList setlist = new ArrayList();
									setlist.add(itembean);
									zbmap.put(frowset.getString("fieldsetid").toUpperCase(), setlist); //把list放到map里
								}
								else
								{
									ArrayList setlist=(ArrayList)zbmap.get(frowset.getString("fieldsetid").toUpperCase());
									setlist.add(itembean);
									zbmap.put(frowset.getString("fieldsetid").toUpperCase(), setlist);
								}
							}
						}	
					
				}
				}
			}
			
			map.put("1", list);
			map.put("2", amap);
			map.put("3", zbmap);
			map.put("4", sublist);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(this.frowset!=null) {
                try {
                    this.frowset.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		return map;
	}
	/*
	 * 指标子集授权写入excel
	 */
	public void setinfoexcel(ArrayList classlist,HashMap fielsetidmap,HashMap itemdescmap,HSSFWorkbook workbook,HSSFRow row,HSSFCell cell,ArrayList toplist,ArrayList sublist)
	{
		
		ResultSet rset=null;
		try
		{
			ContentDAO dao  = new ContentDAO(conn);
			HSSFSheet sheet = workbook.createSheet(ResourceFactory.getProperty("kjg.title.zjshouquan")); //生成一张表; 子集指标授权
			// 创建HSSFPatriarch对象,HSSFPatriarch是所有注释的容器. 
			HSSFPatriarch patr = sheet.createDrawingPatriarch();
			CellRangeAddress region=null;
			short h=0;
			short xia=0;
			short k=3;
			short t=1;
			String flagl="0";
			String havePriv="0";
			String flag=null; //表t_sys_function_priv 中的status字段值
			if(flag==null|| "".equals(flag)) {
                flag=GeneralConstant.ROLE;
            }
//			 设置列宽,参数一，10列
			sheet.setColumnWidth((short)0,(short)2000);
			sheet.setColumnWidth((short)1,(short)5000);
			sheet.setColumnWidth((short)2,(short)8000);
			row = sheet.getRow(0);
	          if(row==null) {
                  row = sheet.createRow(0);
              }
			//合并单元格
	      

			ExportExcelUtil.mergeCell(sheet, 0,(short)0,2,(short)10);
			cell=row.createCell((short)(0));
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    		cell.setCellValue(ResourceFactory.getProperty("kjg.title.zjshouquan"));
    		cell.setCellStyle(this.setDateStyle(workbook));
    		//写入top
    		row = sheet.getRow(3);
	          if(row==null) {
                  row = sheet.createRow(3);  //row位置
              }
    		cell=row.createCell((short)(0));  //cell位置

    		//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    		cell.setCellValue(ResourceFactory.getProperty("kjg.gather.xuhao"));    //序号
    		cell.setCellStyle(this.settopStyle(workbook));
    		cell=row.createCell((short)(1));
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    		cell.setCellValue(ResourceFactory.getProperty("menu.table"));     //子集
    		cell.setCellStyle(this.settopStyle(workbook));
    		cell=row.createCell((short)(2));
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    		cell.setCellValue(ResourceFactory.getProperty("menu.field"));   //指标
    		cell.setCellStyle(this.settopStyle(workbook));
    		short list=0;
    		for(int i=0;i<toplist.size();i++){        //top
    			LazyDynaBean bean = (LazyDynaBean)toplist.get(i);
    			row = sheet.getRow(3);
  	          if(row==null) {
                  row= sheet.createRow(3);
              }
    			cell=row.createCell((short)k);
    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    			cell.setCellValue((String)bean.get("role_name"));
    			cell.setCellStyle(this.settopStyle(workbook));
    			//定义位置
        		HSSFComment comment = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) k , 2 , ( short ) (k+1) , 3 ));
        		//找到值
        		comment.setString( new HSSFRichTextString((String)bean.get("role_id")));
        		//comment.setAuthor("A001"); 作者
        		//写入
        		cell.setCellComment(comment);
    			list = row.getLastCellNum();
    			list--; //兼容excel2007
    			sheet.setColumnWidth((short)list,(short)3000);
    			h=0;
    			for(int n=0;n<classlist.size();n++){   //ABK 浅绿色 
    				if(i==0){
    	    			LazyDynaBean beans = (LazyDynaBean)classlist.get(n);
            			//row=sheet.createRow(h+4);
    	    			row = sheet.getRow(h+4);
            			if(row==null) {
                            row = sheet.createRow(h+4);
                        }
            			//合并单元格
            			if(list > 0) {
            				/*项目bug 【45301】 数据是一列一列写进去的，每次合并都是0~最大列，导致有重复合并报错。改成直接合并到正确位置 guodd 2019-03-08*/
            				region = new CellRangeAddress(h+4,h+4,(short)0,(short)2+toplist.size());
            				sheet.addMergedRegion(region);
            				HSSFCellStyle cs=this.setsubStyle(workbook);
//            			ExportExcelUtil.mergeCell(sheet, h+4,(short)0,h+4,(short)list));
            				for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
            					//row = sheet.createRow(p);
            					row = sheet.getRow(p);
            					if(row==null) {
                                    row = sheet.createRow(p);
                                }
            					for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
            						cell = row.createCell((short)o);
            						cell.setCellStyle(cs);
            					}
            				}
            			}
            			
            			cell = row.createCell((short)0);
            			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            			cell.setCellValue((String)beans.get("classname"));
            			cell.setCellStyle(this.setsubStyle(workbook));
            			h++;
            			StringBuffer strsql=new StringBuffer();
                		strsql.append("select tablepriv ");   //查找对应子集
                        strsql.append(" from t_sys_function_priv where id='");
                        strsql.append((String)bean.get("role_id"));
                        strsql.append("' and status=");
                        strsql.append(flag);
	                    	rset=dao.search(strsql.toString());
	                    	if(rset.next()){
	                    		this.table_str=Sql_switcher.readMemo(rset,"tablepriv");//rset.getString("dbpriv");
	                    	}
	                    	else
	                    	{
	                    		this.table_str="";
	                    	}
                        
            			ArrayList subsetlist = (ArrayList)fielsetidmap.get((String)beans.get("classpre"));
            			if(subsetlist!=null){
            				for(int e=0;e<subsetlist.size();e++){  //子集
                				LazyDynaBean subsetbean = (LazyDynaBean)subsetlist.get(e);
                				//row = sheet.createRow(h+4);
                				row = sheet.getRow(h+4);
	                			if(row==null) {
                                    row = sheet.createRow(h+4);
                                }
                				cell=row.createCell((short)1);
                				//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    			cell.setCellValue((String)subsetbean.get("customdesc"));
                    			//增加批注;定义位置
                        		HSSFComment comments = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) 1 , (h+4) , ( short ) 2 , (h+5) ));
                        		comments.setString( new HSSFRichTextString((String)subsetbean.get("fieldsetid")));
                        		cell.setCellComment(comments);
                        		/**
                  	           * 支持分布式授权机制
                  	           */
//                  	          	flagl=userView.analyseTablePriv((String)subsetbean.get("fieldsetid")); //子集
//                  	          	if(flagl.equals("0"))
//                	              continue;
                  	          /**
                  	           * 现拥有的权限
                  	           */
                  	          havePriv=this.analyseTablePriv((String)subsetbean.get("fieldsetid"));
                  	          if("0".equalsIgnoreCase(havePriv))
                  	          {
                  	        	//row= sheet.createRow(h+4);
                  	        	row = sheet.getRow(h+4);
	                			if(row==null) {
                                    row = sheet.createRow(h+4);
                                }
                    			cell=row.createCell((short)(k));
                    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                        		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                        		cell.setCellValue("无");
                  	          }else if("1".equalsIgnoreCase(havePriv))
                  	          {
                  	        	//row= sheet.createRow(h+4);
                  	        	row = sheet.getRow(h+4);
	                			if(row==null) {
                                    row = sheet.createRow(h+4);
                                }
                    			cell=row.createCell((short)(k));
                    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                        		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                        		cell.setCellValue("读");
                  	          }else if("2".equalsIgnoreCase(havePriv))
                  	          {
                  	        	//row= sheet.createRow(h+4);
                  	        	row = sheet.getRow(h+4);
	                			if(row==null) {
                                    row = sheet.createRow(h+4);
                                }
                    			cell=row.createCell((short)(k));
                    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                        		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                        		cell.setCellValue("写");
                  	          }
                        		
                    			cell=row.createCell((short)2);
                    			//序号
                    			cell=row.createCell((short)0);
                				//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    			cell.setCellValue(t);                               
//                    			cell.setCellStyle(this.setbodyStyle(workbook));
                    			t++;
                    			h++;
                    			StringBuffer strs=new StringBuffer();
                    			strs.append("select fieldpriv ");   
                    			strs.append(" from t_sys_function_priv where id='");
                    			strs.append((String)bean.get("role_id"));
                    			strs.append("' and status=");
                    			strs.append(flag);
                                	rset=dao.search(strs.toString());
	                            	if(rset.next()){
	                            		this.field_str=Sql_switcher.readMemo(rset,"fieldpriv");//rset.getString("dbpriv");
	                            	}
	                            	else
	                            	{
	                            		this.field_str="";  //指标
	                            	}
                    			ArrayList indexlist  = (ArrayList)itemdescmap.get((String)subsetbean.get("fieldsetid"));
                    			//有为null的时候，这里判断一下，如果为null 给一个空的arrayList guodd 2016-12-30
                    			indexlist = indexlist==null?new ArrayList():indexlist;
                    			for(int r=0;r<indexlist.size();r++){
                    				LazyDynaBean indexbean = (LazyDynaBean)indexlist.get(r);
                    				//row = sheet.createRow(h+4);
                    				row = sheet.getRow(h+4);
    	                			if(row==null) {
                                        row = sheet.createRow(h+4);
                                    }
                    				cell=row.createCell((short)2);
                    				//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    				cell.setCellValue((String)indexbean.get("itemdesc"));
//                    				//注释
                    				HSSFComment comm = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) 2 , (h+4) , ( short ) 3 , (h+6) ));
                            		comm.setString( new HSSFRichTextString((String)indexbean.get("itemid")));
                            		cell.setCellComment(comm);
                            		havePriv=this.analyseFieldPriv((String)indexbean.get("itemid"));
                            		if("0".equalsIgnoreCase(havePriv))
                            		{
                            			//row= sheet.createRow((short)h+4); //行
                            			row = sheet.getRow((short)h+4);
        	                			if(row==null) {
                                            row = sheet.createRow((short)h+4);
                                        }
    		                			cell=row.createCell((short)k);
    		                			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    					    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    					    			cell.setCellValue("无");
                            		}else if("1".equalsIgnoreCase(havePriv))
                            		{
                            			//row= sheet.createRow((short)h+4); //行
                            			row = sheet.getRow((short)h+4);
        	                			if(row==null) {
                                            row = sheet.createRow((short)h+4);
                                        }
    		                			cell=row.createCell((short)k);
    		                			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    					    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    					    			cell.setCellValue("读");
                            		}else if("2".equalsIgnoreCase(havePriv))
                            		{
                            			//row= sheet.createRow((short)h+4); //行
                            			row = sheet.getRow((short)h+4);
        	                			if(row==null) {
                                            row = sheet.createRow((short)h+4);
                                        }
    		                			cell=row.createCell((short)k);
    		                			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    					    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    					    			cell.setCellValue("写");
                            		}
                    				//序号
                    				cell=row.createCell((short)0);
                    				//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        			cell.setCellValue(t);
                        			t++;
                    				h++;
                    			}
                			}
            			}
            			if("职位子集".equalsIgnoreCase((String)beans.get("classname")))
            			{
            				for(int ic=0;ic<sublist.size();ic++){
                				LazyDynaBean mediabean = (LazyDynaBean)sublist.get(ic);
                    			//row = sheet.createRow(h+4);
                				row = sheet.getRow(h+4);
	                			if(row==null) {
                                    row = sheet.createRow(h+4);
                                }
                    			cell=row.createCell((short)1);
                    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    			cell.setCellValue((String)mediabean.get("ryname"));
                    			HSSFComment comm = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) 2 , (h+4) , ( short ) 3 , (h+6) ));
                        		comm.setString( new HSSFRichTextString((String)mediabean.get("ryid")));
                        		cell.setCellComment(comm);
//                    			序号
                				cell=row.createCell((short)0);
                				//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    			cell.setCellValue(t);
                    			/**
                    	           * 现拥有的权限
                    	           */
                    	          havePriv=this.analyseTablePriv((String)mediabean.get("ryid"));
                    	          if("0".equalsIgnoreCase(havePriv))
                    	          {
                    	        	//row= sheet.createRow(h+4);
                    	        	  row = sheet.getRow(h+4);
      	                			if(row==null) {
                                        row = sheet.createRow(h+4);
                                    }
                      			cell=row.createCell((short)(k));
                      			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                          		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                          		cell.setCellValue("无");
                    	          }else if("1".equalsIgnoreCase(havePriv))
                    	          {
                    	        	//row= sheet.createRow(h+4);
                    	        	  row = sheet.getRow(h+4);
      	                			if(row==null) {
                                        row = sheet.createRow(h+4);
                                    }
                      			cell=row.createCell((short)(k));
                      			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                          		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                          		cell.setCellValue("读");
                    	          }else if("2".equalsIgnoreCase(havePriv))
                    	          {
                    	        	//row= sheet.createRow(h+4);
                    	        	  row = sheet.getRow(h+4);
      	                			if(row==null) {
                                        row = sheet.createRow(h+4);
                                    }
                      			cell=row.createCell((short)(k));
                      			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                          		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                          		cell.setCellValue("写");
                    	          }
                    	          t++;
                      			  h++;
                    	         // row = sheet.createRow(h+4);
                      			row = sheet.getRow(h+4);
	                			if(row==null) {
                                    row = sheet.createRow(h+4);
                                }
                      			  cell=row.createCell((short)1);
                      			  //cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                  				  cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                      			  cell.setCellValue((String)mediabean.get("dwname"));
                      			  HSSFComment comms = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) 2 , (h+4) , ( short ) 3 , (h+6) ));
                          		  comms.setString( new HSSFRichTextString((String)mediabean.get("dwid")));
                          		  cell.setCellComment(comms);
//                      			序号
                  				cell=row.createCell((short)0);
                  				//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                  				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                      			cell.setCellValue(t);
                      			/**
                  	           * 现拥有的权限
                  	           */
                  	          havePriv=this.analyseTablePriv((String)mediabean.get("dwid"));
                  	        if("0".equalsIgnoreCase(havePriv))
              	          {
              	        	//row= sheet.createRow(h+4);
                  	        	row = sheet.getRow(h+4);
	                			if(row==null) {
                                    row = sheet.createRow(h+4);
                                }
                			cell=row.createCell((short)(k));
                			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                    		cell.setCellValue("无");
              	          }else if("1".equalsIgnoreCase(havePriv))
              	          {
              	        	//row= sheet.createRow(h+4);
              	        	row = sheet.getRow(h+4);
                			if(row==null) {
                                row = sheet.createRow(h+4);
                            }
                			cell=row.createCell((short)(k));
                			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                    		cell.setCellValue("读");
              	          }else if("2".equalsIgnoreCase(havePriv))
              	          {
              	        	//row= sheet.createRow(h+4);
              	        	row = sheet.getRow(h+4);
                			if(row==null) {
                                row = sheet.createRow(h+4);
                            }
                			cell=row.createCell((short)(k));
                			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                    		cell.setCellValue("写");
              	          }
                  	        	t++;
                			    h++;
                			    //row = sheet.createRow(h+4);
                			    row = sheet.getRow(h+4);
	                			if(row==null) {
                                    row = sheet.createRow(h+4);
                                }
                    			cell=row.createCell((short)1);
                    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    			cell.setCellValue((String)mediabean.get("subsetname"));
                    			HSSFComment com = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) 2 , (h+4) , ( short ) 3 , (h+6) ));
                        		com.setString( new HSSFRichTextString((String)mediabean.get("id")));
                        		cell.setCellComment(com);
//                    			序号
                				cell=row.createCell((short)0);
                				//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    			cell.setCellValue(t);
                    			/**
                    	           * 现拥有的权限
                    	           */
                    	          havePriv=this.analyseTablePriv((String)mediabean.get("id"));
                    	        if("0".equalsIgnoreCase(havePriv))
                	          {
                	        	//row= sheet.createRow(h+4);
                    	        	row = sheet.getRow(h+4);
    	                			if(row==null) {
                                        row = sheet.createRow(h+4);
                                    }
                  			cell=row.createCell((short)(k));
                  			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                      		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                      		cell.setCellValue("无");
                	          }else if("1".equalsIgnoreCase(havePriv))
                	          {
                	        	//row= sheet.createRow(h+4);
                	        	  row = sheet.getRow(h+4);
  	                			if(row==null) {
                                    row = sheet.createRow(h+4);
                                }
                  			cell=row.createCell((short)(k));
                  			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                      		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                      		cell.setCellValue("读");
                	          }else if("2".equalsIgnoreCase(havePriv))
                	          {
                	        	//row= sheet.createRow(h+4);
                	        	  row = sheet.getRow(h+4);
  	                			if(row==null) {
                                    row = sheet.createRow(h+4);
                                }
                  			cell=row.createCell((short)(k));
                  			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                      		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                      		cell.setCellValue("写");
                	          }
                    	        t++;
                    	        h++;
                    	        xia=h;
                    	        
                    	        /**h=0,
                    	         * 导出excel后
                    	         * 再打开excle时显示“文件错误，
                    	         * 数据可能丢失”，
                    	         * wangzhongjun
                    	         * 2010-2-27
                    	         */
//                    	        //h=0;
                			}
            			}
            		
    				}else{
    					
    	    			LazyDynaBean beans = (LazyDynaBean)classlist.get(n);
    	    			/*项目bug 【45301】 上面代码（搜索【45301】查看上个修改）已经改为一次性合并正确了，这里不需要再次合并了 guodd 2019-03-08*/
    	    			//region = new CellRangeAddress(h+4,h+4,(short)0,(short)list);
            			//sheet.addMergedRegion(region);
            			HSSFCellStyle cs=this.setsubStyle(workbook);
//            			ExportExcelUtil.mergeCell(sheet, h+4,(short)0,h+4,(short)list));
            			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
                			//row = sheet.createRow(p);
            				row = sheet.getRow(p);
                			if(row==null) {
                                row = sheet.createRow(p);
                            }
            	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
            	            	cell = row.createCell((short)o);
            	                cell.setCellStyle(cs);
            	            }
            			}
            			cell = row.createCell((short)0);
            			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            			cell.setCellValue((String)beans.get("classname"));
            			cell.setCellStyle(this.setsubStyle(workbook));
            			h++;
            			StringBuffer strsql=new StringBuffer();
                		strsql.append("select tablepriv ");   //查找对应子集
                        strsql.append(" from t_sys_function_priv where id='");
                        strsql.append((String)bean.get("role_id"));
                        strsql.append("' and status=");
                        strsql.append(flag);
                        	rset=dao.search(strsql.toString());
	                    	if(rset.next()){
	                    		this.table_str=Sql_switcher.readMemo(rset,"tablepriv");//rset.getString("dbpriv");
	                    	}
	                    	else
	                    	{
	                    		this.table_str="";
	                    	}
            			ArrayList subsetlist = (ArrayList)fielsetidmap.get((String)beans.get("classpre"));
            			if(subsetlist!=null){
            				for(int e=0;e<subsetlist.size();e++){  //子集
                				LazyDynaBean subsetbean = (LazyDynaBean)subsetlist.get(e);
                  	          /**
                  	           * 现拥有的权限
                  	           */
                  	          havePriv=this.analyseTablePriv((String)subsetbean.get("fieldsetid"));
                  	          if("0".equalsIgnoreCase(havePriv))
                  	          {
                  	        	//row= sheet.createRow(h+4);
                  	        	row = sheet.getRow(h+4);
	                			if(row==null) {
                                    row = sheet.createRow(h+4);
                                }
                    			cell=row.createCell((short)(k));
                    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                        		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                        		cell.setCellValue("无");
                  	          }else if("1".equalsIgnoreCase(havePriv))
                  	          {
                  	        	//row= sheet.createRow(h+4);
                  	        	row = sheet.getRow(h+4);
	                			if(row==null) {
                                    row = sheet.createRow(h+4);
                                }
                    			cell=row.createCell((short)(k));
                    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                        		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                        		cell.setCellValue("读");
                  	          }else if("2".equalsIgnoreCase(havePriv))
                  	          {
                  	        	//row= sheet.createRow(h+4);
                  	        	row = sheet.getRow(h+4);
	                			if(row==null) {
                                    row = sheet.createRow(h+4);
                                }
                    			cell=row.createCell((short)(k));
                    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                        		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                        		cell.setCellValue("写");
                  	          }
                        		
                    			cell=row.createCell((short)2);
                    			h++;
                    			StringBuffer strs=new StringBuffer();
                    			strs.append("select fieldpriv ");   
                    			strs.append(" from t_sys_function_priv where id='");
                    			strs.append((String)bean.get("role_id"));
                    			strs.append("' and status=");
                    			strs.append(flag);
                    			rset=dao.search(strs.toString());
	                            	if(rset.next()){
	                            		this.field_str=Sql_switcher.readMemo(rset,"fieldpriv");//rset.getString("dbpriv");
	                            	}
	                            	else
	                            	{
	                            		this.field_str="";  //指标
	                            	}
                               
                    			ArrayList indexlist  = (ArrayList)itemdescmap.get((String)subsetbean.get("fieldsetid"));
                    			//有为null的时候，这里判断一下，如果为null 给一个空的arrayList guodd 2016-12-30
                    			indexlist = indexlist==null?new ArrayList():indexlist;
                    			for(int r=0;r<indexlist.size();r++){
                    				LazyDynaBean indexbean = (LazyDynaBean)indexlist.get(r);
                    				
                            		havePriv=this.analyseFieldPriv((String)indexbean.get("itemid"));
                            		if("0".equalsIgnoreCase(havePriv))
                            		{
                            			//row= sheet.createRow((short)h+4); //行
                            			row = sheet.getRow((short)h+4);
        	                			if(row==null) {
                                            row = sheet.createRow((short)h+4);
                                        }
    		                			cell=row.createCell((short)k);
    		                			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    					    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    					    			cell.setCellValue("无");
                            		}else if("1".equalsIgnoreCase(havePriv))
                            		{
                            			//row= sheet.createRow((short)h+4); //行
                            			row = sheet.getRow((short)h+4);
        	                			if(row==null) {
                                            row = sheet.createRow((short)h+4);
                                        }
    		                			cell=row.createCell((short)k);
    		                			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    					    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    					    			cell.setCellValue("读");
                            		}else if("2".equalsIgnoreCase(havePriv))
                            		{
                            			//row= sheet.createRow((short)h+4); //行
                            			row = sheet.getRow((short)h+4);
        	                			if(row==null) {
                                            row = sheet.createRow((short)h+4);
                                        }
    		                			cell=row.createCell((short)k);
    		                			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    					    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    					    			cell.setCellValue("写");
                            		}
                    				
                    				h++;
                    			}
                			}
            			}
            			if("职位子集".equalsIgnoreCase((String)beans.get("classname")))
            			{
            				for(int ic=0;ic<sublist.size();ic++){
                				LazyDynaBean mediabean = (LazyDynaBean)sublist.get(ic);
                    			/**
                    	           * 现拥有的权限
                    	           */
                    	          havePriv=this.analyseTablePriv((String)mediabean.get("ryid"));
                    	          if("0".equalsIgnoreCase(havePriv))
                    	          {
                    	        	//row= sheet.createRow(h+4);
                    	        	  row = sheet.getRow(h+4);
      	                			if(row==null) {
                                        row = sheet.createRow(h+4);
                                    }
                      			cell=row.createCell((short)(k));
                      			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                          		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                          		cell.setCellValue("无");
                    	          }else if("1".equalsIgnoreCase(havePriv))
                    	          {
                    	        	//row= sheet.createRow(h+4);
                    	        	  row = sheet.getRow(h+4);
      	                			if(row==null) {
                                        row = sheet.createRow(h+4);
                                    }
                      			cell=row.createCell((short)(k));
                      			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                          		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                          		cell.setCellValue("读");
                    	          }else if("2".equalsIgnoreCase(havePriv))
                    	          {
                    	        	//row= sheet.createRow(h+4);
                    	        	  row = sheet.getRow(h+4);
      	                			if(row==null) {
                                        row = sheet.createRow(h+4);
                                    }
                      			cell=row.createCell((short)(k));
                      			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                          		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                          		cell.setCellValue("写");
                    	          }
                      			  h++;
                    	          
                      			/**
                  	           * 现拥有的权限
                  	           */
                  	          havePriv=this.analyseTablePriv((String)mediabean.get("dwid"));
                  	        if("0".equalsIgnoreCase(havePriv))
              	          {
              	        	//row= sheet.createRow(h+4);
                  	        	row = sheet.getRow(h+4);
	                			if(row==null) {
                                    row = sheet.createRow(h+4);
                                }
                			cell=row.createCell((short)(k));
                			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                    		cell.setCellValue("无");
              	          }else if("1".equalsIgnoreCase(havePriv))
              	          {
              	        	//row= sheet.createRow(h+4);
              	        	row = sheet.getRow(h+4);
                			if(row==null) {
                                row = sheet.createRow(h+4);
                            }
                			cell=row.createCell((short)(k));
                			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                    		cell.setCellValue("读");
              	          }else if("2".equalsIgnoreCase(havePriv))
              	          {
              	        	//row= sheet.createRow(h+4);
              	        	row = sheet.getRow(h+4);
                			if(row==null) {
                                row = sheet.createRow(h+4);
                            }
                			cell=row.createCell((short)(k));
                			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                    		cell.setCellValue("写");
              	          }
                			    h++;
                			    
                    			/**
                    	           * 现拥有的权限
                    	           */
                    	          havePriv=this.analyseTablePriv((String)mediabean.get("id"));
                    	        if("0".equalsIgnoreCase(havePriv))
                	          {
                	        	//row= sheet.createRow(h+4);
                    	        	row = sheet.getRow(h+4);
    	                			if(row==null) {
                                        row = sheet.createRow(h+4);
                                    }
                  			cell=row.createCell((short)(k));
                  			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                      		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                      		cell.setCellValue("无");
                	          }else if("1".equalsIgnoreCase(havePriv))
                	          {
                	        	//row= sheet.createRow(h+4);
                	        	  row = sheet.getRow(h+4);
  	                			if(row==null) {
                                    row = sheet.createRow(h+4);
                                }
                  			cell=row.createCell((short)(k));
                  			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                      		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                      		cell.setCellValue("读");
                	          }else if("2".equalsIgnoreCase(havePriv))
                	          {
                	        	//row= sheet.createRow(h+4);
                	        	  row = sheet.getRow(h+4);
  	                			if(row==null) {
                                    row = sheet.createRow(h+4);
                                }
                  			cell=row.createCell((short)(k));
                  			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                      		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                      		cell.setCellValue("写");
                	          }
                    	        h++;
                    	        xia=h;
                    	        /**h=0,
                    	         * 导出excel后
                    	         * 再打开excle时显示“文件错误，
                    	         * 数据可能丢失”，
                    	         * wangzhongjun
                    	         * 2010-2-27
                    	         */
//                    	        //h=0;
                			}
            			}
            		}
    				}
        			
    			
    			k++;
    		}
    		///excel上生成下拉筐
    		short m=0;
    		//row = sheet.createRow(m+0);
    		row = sheet.getRow(m+0);
			if(row==null) {
                row = sheet.createRow(m+0);
            }
    		cell=row.createCell((short)26);
    		//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellValue(ResourceFactory.getProperty("write.label"));//写
    		m++;
    		//row = sheet.createRow(m+0);
    		row = sheet.getRow(m+0);
			if(row==null) {
                row = sheet.createRow(m+0);
            }
    		cell=row.createCell((short)26);
    		//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellValue(ResourceFactory.getProperty("read.label"));   //读
    		m++;
    		//row = sheet.createRow(m+0);
    		row = sheet.getRow(m+0);
			if(row==null) {
                row = sheet.createRow(m+0);
            }
    		cell=row.createCell((short)26);
    		//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellValue(ResourceFactory.getProperty("null.label"));     //无
    		m++;
    		String strFormula = "$AA$1:$AA$3";  //表示AA列1-2行作为下拉列表来源数据 
    		/*HSSFDataValidation data_validation = new HSSFDataValidation((short)  
    				(5),(short)3,(short)(xia+3),(short)list);
    		  data_validation.setDataValidationType(HSSFDataValidation.DATA_TYPE_LIST); 
    		  data_validation.setFirstFormula(strFormula);  
              data_validation.setSecondFormula(null);  
              data_validation.setExplicitListFormula(true);  
              data_validation.setSurppressDropDownArrow(false);  
              data_validation.setEmptyCellAllowed(false);  
              data_validation.setShowPromptBox(false);
//              sheet.a
    		 sheet.addValidationData(data_validation);*/
    		/*下拉区域坐标坐标不对。原因：升级POI后 旧版POI传值格式和新版POI传值格式不太一样，这里代码需要更新一下 guodd 2019-06-25*/
    		CellRangeAddressList addressList = new CellRangeAddressList(4,4+xia, 3,k-1);
    		DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
    		HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
    		dataValidation.setSuppressDropDownArrow(false);		
    		sheet.addValidationData(dataValidation);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
        	if(rset!=null){
        		try {
					rset.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
	}
	public String analyseTablePriv(String table_id)
    {
        if(table_str==null|| "".equals(table_str)) {
            return "0";
        }
        int len=table_id.length();
        int idx=table_str.indexOf(table_id);
        if(idx==-1) {
            return "0";
        }
        return table_str.substring(idx+len,idx+len+1);
    }
	/**
     * 分析指标的权限
     * ,xxxxxX{,xxxxX},（X=0,1,2,3,4,5,6）
     * @param field_id　for examples field_id=A0101;
     * @return
     */
    public String analyseFieldPriv(String field_id)
    {
        if(field_str==null|| "".equals(field_str)) {
            return "0";
        }
        int len=field_id.length();
        int idx=field_str.indexOf(field_id);
        if(idx==-1) {
            return "0";
        }
        return field_str.substring(idx+len,idx+len+1);        
    }
	/*
	 * top大体字
	 */
	public HSSFCellStyle setDateStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 20); // 字体大小
        font.setBold(true);//粗体字
        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font); // 单元格字体
        style.setAlignment(HorizontalAlignment.CENTER); // 居中对齐方式 左右
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐方式 上下
        return style;

	}
	//top黄颜色部分
	public HSSFCellStyle settopStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 12); // 字体大小
        font.setBold(true);//粗体字
        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();//创建单元各风格
        style.setAlignment(HorizontalAlignment.CENTER); // 居中对齐方式 左右
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐方式 上下
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFillForegroundColor(HSSFColor.YELLOW.index);//颜色黄色
		style.setWrapText(true);  //换行
        style.setFont(font); // 单元格字体
        style.setBorderBottom(BorderStyle.THIN); //下边
        style.setBorderLeft(BorderStyle.THIN); //左边
        style.setBorderRight(BorderStyle.THIN); //右边
        style.setBorderTop(BorderStyle.THIN); //上边

        return style;

	}
	//A/K/B样式
	public HSSFCellStyle setsubStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 12); // 字体大小
        font.setBold(true);//粗体字
        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();//创建单元各风格
        style.setAlignment(HorizontalAlignment.LEFT); // 居中对齐方式 左右
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐方式 上下
       
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);//浅绿色颜色
        style.setFont(font); // 单元格字体
        style.setBorderBottom(BorderStyle.THIN); //下边
        style.setBorderLeft(BorderStyle.THIN); //左边
        style.setBorderRight(BorderStyle.THIN); //右边
        style.setBorderTop(BorderStyle.THIN); //上边

        return style;

	}
	
	/**
	 * 	导出角色的人员列表
	 * @param roleid
	 * @return
	 * @throws GeneralException
	 */
	public HashMap getRoleDetail(String [] roleid) throws GeneralException{
		HashMap map=new HashMap();
		ResultSet rs = null;
		try{
			ArrayList orgList = new ArrayList();
			ContentDAO dao = new ContentDAO(conn);
			for(int i=0;i<roleid.length;i++){
				String role_id=roleid[i];
				ArrayList detailList = new ArrayList();
				
					String sql="select pre from dbname";
					rs = dao.search(sql);
					StringBuffer dbpre=new StringBuffer();
					while(rs.next()){
						dbpre.append(","+rs.getString("pre"));
					}
					sql = "select staff_id,status from t_sys_staff_in_role t left join operuser o on t.staff_id=o.username where role_id='"+role_id+"' order by groupid,staff_id,status";
					rs = dao.search(sql);
					HashMap status1= new HashMap();//自助用户staff_id
					StringBuffer status2= new StringBuffer();//单位、部门、岗位staff_id
					String detailtype="";
					String detailname="";
					ArrayList status1List = new ArrayList();
					int n = 0;
					while(rs.next()){
						int status = rs.getInt("status");
						if(status==0){//业务用户
							LazyDynaBean ldb = new LazyDynaBean();
							ldb.set("staff_id", rs.getString("staff_id"));
							ldb.set("role_id", role_id);
							detailtype=ResourceFactory.getProperty("label.role.detail.name.0");
							detailname=rs.getString("staff_id");
							ldb.set("detailtype", detailtype);
							ldb.set("detailname", getOperUserGroupName(detailname));
							ldb.set("status", "0");
							detailList.add(ldb);
						}else if(status==1){
							String staff_id = rs.getString("staff_id");
							if(staff_id.length()>8){
								String pre=staff_id.substring(0, 3);
								if(dbpre.indexOf(pre)!=-1){//验证数据是否未自助用户相应人员库+a0100,然后分组收集
									if(status1.containsKey(pre)){
										StringBuffer status1pre=(StringBuffer)status1.get(pre);
										status1pre.append(",'"+staff_id.substring(3)+"'");
										n++;
										if(n>500){
											status1List.add(status1);
											status1= new HashMap();
											n=0;
										}
									}else{
										StringBuffer status1pre= new StringBuffer();
										status1pre.append(",'"+staff_id.substring(3)+"'");
										status1.put(pre, status1pre);
									}
								}
							}
							//detailtype=ResourceFactory.getProperty("label.role.detail.name.1");
							//detailname = getA0101(this.frecset.getString("staff_id"),dao);
						}else if(status==2){
							orgList.add(rs.getString("staff_id"));
						}
					}
					status1List.add(status1);
					for(int m=status1List.size()-1;m>=0;m--){
						status1 = (HashMap)status1List.get(m);
						getA0101(status1,detailList,role_id);
					}
					getOrgInfo(orgList,detailList,role_id);
					map.put(role_id, detailList);
			
		}
		}catch(Exception e){
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		return map;
	}
	
	/**
	 * 查询自助用户的
	 * @param status1
	 * @param detailList
	 * @param role_id
	 * @param dao
	 * @throws Exception
	 */
	private void getA0101(HashMap status1,ArrayList detailList,String role_id) throws Exception{
		ResultSet rs=null;
		String detailtype=ResourceFactory.getProperty("label.role.detail.name.1");
		try{
			ContentDAO dao = new ContentDAO(conn);
			Sys_Oth_Parameter sysOth = new Sys_Oth_Parameter(conn);
			String dept_seq = sysOth.getValue(32, "sep");
			for(Iterator i=status1.keySet().iterator();i.hasNext();){
				String dbname=(String)i.next();
				String sql=null;
				sql = "select dbname from dbname where pre='"+dbname+"'";
				rs = dao.search(sql);
				String dbdesc=null;
				if(rs.next()){
					dbdesc=rs.getString("dbname");
				}
				StringBuffer status1pre=(StringBuffer)status1.get(dbname);
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
				String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name");
				if(chk==null) {
                    chk="";
                }
				
				if(chk.length()>0){
					sql = "select a0100,a0101,"+chk+",b0110,e0122,e01a1 from "+dbname+"A01 where a0100 in ('##'"+status1pre.toString()+") order by b0110,e0122,e01a1";
				}else{
					sql = "select a0100,a0101,b0110,e0122,e01a1 from "+dbname+"A01 where a0100 in ('##'"+status1pre.toString()+") order by b0110,e0122,e01a1";
				}
				rs= dao.search(sql);
				while(rs.next()){
					LazyDynaBean ldb = new LazyDynaBean();
					ldb.set("staff_id", dbname+rs.getString("a0100"));
					ldb.set("role_id", role_id);
					String b0110=rs.getString("b0110");
					String e0122=rs.getString("e0122");
					String e01a1=rs.getString("e01a1");
					String a0101=rs.getString("a0101");
					String a0177 = null;
					if(chk.length()>0){
						a0177=rs.getString(chk);
					}
					StringBuffer detailname = new StringBuffer();
					/*if(b0110!=null&&b0110.length()>0){
						if(e0122!=null&&e0122.length()>0){
							if(e01a1!=null&&e01a1.length()>0){
								//detailname.append(getCodeToNameLevel(e01a1)+"  ");
								detailname.append(AdminCode.getCodeName("@K", e01a1));
							}else{
								//detailname.append(getCodeToNameLevel(e0122)+"  ");
							}
						}else{
							//detailname.append(getCodeToNameLevel(b0110)+"  ");
						}
					}*/
					CodeItem codeitem = AdminCode.getCode("UN", b0110, 5);
					if(codeitem!=null) {
                        detailname.append(codeitem.getCodename());
                    }
					codeitem = AdminCode.getCode("UM", e0122, 5);
					if(codeitem!=null) {
                        detailname.append(dept_seq+codeitem.getCodename());
                    }
					detailname.append(dept_seq+AdminCode.getCodeName("@K", e01a1));
					
					detailname.append("  "+(dbdesc!=null?dbdesc:"")+dept_seq);
					detailname.append((a0101!=null?a0101:"")+"  ");
					detailname.append((a0177!=null?a0177:""));
					ldb.set("detailtype", detailtype);
					ldb.set("detailname", detailname.toString());
					ldb.set("status", "1");
					detailList.add(ldb);
				}
			
		
		}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(rs!=null){
				rs.close();
			}
		}
	}
	/**
	 * 查询单位、部门、岗位的
	 * @param orgList
	 * @param detailList
	 * @param role_id
	 * @throws Exception
	 */
	private void getOrgInfo(ArrayList orgList,ArrayList detailList,String role_id) throws Exception{
		//String sql = "select codesetid,codeitemid,codeitemdesc from organization where codeitemid in ('##'"+status2.toString()+") order by codeitemid";
		/*【62010】 in 个数超过1000个报错，此处处理一下  guodd 2020-06-15*/
		StringBuffer sql = new StringBuffer();
		sql.append("select codesetid,codeitemid,codeitemdesc from organization where codeitemid in (");
		for(int i=1;i<=orgList.size();i++){
			if(i%1000==0){
				sql.append("'##') or codeitemid in (");
			}
			sql.append("'").append(orgList.get(i-1)).append("',");
		}

		sql.append("'##')");
		ResultSet rs=null;
		try{
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql.toString());
			while(rs.next()){
				LazyDynaBean ldb = new LazyDynaBean();
				ldb.set("staff_id", rs.getString("codeitemid"));
				ldb.set("role_id", role_id);
				String detailtype=rs.getString("codesetid");
				String codeitemid = rs.getString("codeitemid");
				//String detailname=getCodeToNameLevel(rs.getString("codeitemid"));
				CodeItem codeitem =  AdminCode.getCode(detailtype, codeitemid, 5);
				String detailname = "";
				if(codeitem!=null) {
                    detailname=codeitem.getCodename();
                }
				if("UN".equalsIgnoreCase(detailtype)){
					detailtype=ResourceFactory.getProperty("tree.unroot.undesc");
				}else if("UM".equalsIgnoreCase(detailtype)){//部门添加单位前缀 wang 20170730 bug 39260
					String parentid = codeitem.getPcodeitem();
					codeitem = AdminCode.getCode("UN", parentid, 5);
					detailname = codeitem.getCodename()+"/"+detailname;
					detailtype=ResourceFactory.getProperty("tree.umroot.umdesc");
				}else{//岗位添加单位前缀   wang 20170730 bug 39260
					if(codeitem !=null){
						String UNParentid = codeitem.getPcodeitem();
						codeitem = AdminCode.getCode("UN", UNParentid, 5);
						detailname = codeitem.getCodename()+"/"+detailname;
					}
					detailtype=ResourceFactory.getProperty("hmuster.label.post");
				}
				ldb.set("detailtype", detailtype);
				ldb.set("detailname", detailname);
				ldb.set("status", "2");
				detailList.add(ldb);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(rs!=null){
				rs.close();
			}
		}
	}
	/**
	 * 某单位/某部门/某岗位
	 * @param codeitemid
	 * @return
	 * @throws Exception
	 */
	private String getCodeToNameLevel(String codeitemid) throws Exception{
		String sql = "select codeitemid,parentid,codeitemdesc from organization where codeitemid='"+codeitemid+"'";
		ResultSet rs=null;
		StringBuffer sb= new StringBuffer();
		try{
			ContentDAO dao = new ContentDAO(conn);
			rs= dao.search(sql);
			if(rs.next()){
				String parentid=rs.getString("parentid");
				if(codeitemid.equals(parentid)){
					sb.append(rs.getString("codeitemdesc"));
				}else{
					sb.append(rs.getString("codeitemdesc"));
					getParentDesc(parentid,sb,1);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(rs!=null){
				rs.close();
			}
			
		}
		return sb.toString();
	}
	private void getParentDesc(String codeitemid,StringBuffer sb,int flag)throws Exception{
		if(flag>10) {
            return;//防止死锁
        }
		String sql = "select codeitemid,parentid,codeitemdesc from organization where codeitemid='"+codeitemid+"'";
		ResultSet rs=null;
		boolean f=false;

		String parentid=null;
		try{
			ContentDAO dao = new ContentDAO(conn);
			rs= dao.search(sql);
			if(rs.next()){
				parentid=rs.getString("parentid");
				if(codeitemid.equals(parentid)){
					sb.insert(0, rs.getString("codeitemdesc")+"/");
				}else{
					sb.insert(0, rs.getString("codeitemdesc")+"/");
					flag++;
					f=true;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(rs!=null){
				rs.close();
			}

		}
		if(f){
			getParentDesc(parentid,sb,flag);
		}
	}
	
	private String getOperUserGroupName(String username)throws Exception{
		String sql="select o.username,o.groupid groupid,u.groupname from operuser o,usergroup u where o.groupid=u.groupid and roleid<>1 and o.username='"+username+"'";
		ResultSet rs=null;
		StringBuffer sb= new StringBuffer();
		sb.append("  "+username);

		try{
			ContentDAO dao = new ContentDAO(conn);
			rs= dao.search(sql);
			if(rs.next()){
				int groupid=rs.getInt("groupid");
				if(1==groupid){
					sb.insert(0,rs.getString("groupname"));
				}else{
					//sb.insert(0,rs.getString("groupname"));
					getGroupParentDesc(groupid,sb,1);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(rs!=null){
				rs.close();
			}
		}
		return sb.toString();
	}
	
	private void getGroupParentDesc(int groupid,StringBuffer sb,int flag)throws Exception{
		if(flag>10) {
            return;//防止死锁
        }
		ResultSet rs=null;
		boolean f=false;
		int parentid=0;
		String sql="select o.username,o.groupid groupid,u.groupname groupname from operuser o,usergroup u where o.username=u.groupname and roleid=1 and u.groupid='"+groupid+"'";
		try{
			ContentDAO dao = new ContentDAO(conn);
			rs= dao.search(sql);
			if(rs.next()){
				parentid=rs.getInt("groupid");
				if(1==parentid){
					if(flag!=1){
						sb.insert(0, rs.getString("groupname")+"/");
					}else{
						sb.insert(0, rs.getString("groupname"));
					}
				}else{
					if(flag!=1){
						sb.insert(0, rs.getString("groupname")+"/");
					}else{
						sb.insert(0, rs.getString("groupname"));
					}
					flag++;
					f=true;
					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(rs!=null){
				rs.close();
			}
		}
		if(f){
			getGroupParentDesc(parentid,sb,flag);
		}
	}
	/*
	 * 导出角色的人员列表
	 */
	public void creatRoleDetailSheet(ArrayList toplist,HashMap map,HSSFWorkbook workbook,HSSFRow row,HSSFCell cell)
	{
		try
		{
			HSSFSheet  sheet = null;
			sheet = workbook.createSheet(ResourceFactory.getProperty("label.role.detail.sheet.name")); //生成一张表;角色人员列表
			// 创建HSSFPatriarch对象,HSSFPatriarch是所有注释的容器. 
			HSSFPatriarch patr = sheet.createDrawingPatriarch();
			int h=0,y=0;//top
			
//			 设置列宽,参数一，9列
			sheet.setColumnWidth((short)0,(short)5000);
			sheet.setColumnWidth((short)1,(short)5000);
			sheet.setColumnWidth((short)2,(short)20000);
			//row = sheet.createRow(h+0); // 定义是那一页的row
			row = sheet.getRow(h+0);
			if(row==null) {
                row = sheet.createRow(h+0);
            }

//			 合并单元格，参数，从第几行，该行的第几个单元格，到第几行，第几个单元格
			ExportExcelUtil.mergeCell(sheet, h+0,(short)0,h+1,(short)2); 
			cell=row.createCell((short)(0));  //写入的单元各位置;
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    		cell.setCellValue(ResourceFactory.getProperty("label.role.detail.sheet.name")); 
    		cell.setCellStyle(this.setDateStyle(workbook));
    		//写入top
    		//row= sheet.createRow(h+2);
    		row = sheet.getRow(h+2);
			if(row==null) {
                row = sheet.createRow(h+2);
            }
    		cell=row.createCell((short)(0));  //写入的单元各位置;
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    		cell.setCellValue(ResourceFactory.getProperty("label.sys.warn.domain.role"));//角色
    		cell.setCellStyle(this.settopStyle(workbook));
    		cell=row.createCell((short)(1));
    		//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    		cell.setCellValue(ResourceFactory.getProperty("label.role.detail.type"));  //关联对象类型
    		cell.setCellStyle(this.settopStyle(workbook));
    		cell=row.createCell((short)(2));
    		//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    		cell.setCellValue(ResourceFactory.getProperty("label.role.detail.name"));  //关联对象名称
    		cell.setCellStyle(this.settopStyle(workbook));
    		
    		h=3;
    		for(int i=0;i<toplist.size();i++){
    			LazyDynaBean bean = (LazyDynaBean)toplist.get(i);
    			String role_id=(String)bean.get("role_id");
    			String role_name=(String)bean.get("role_name");
    			row = sheet.getRow(h);
    			if(row==null) {
                    row = sheet.createRow(h);
                }
    			cell=row.createCell((short)(0));
    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
        		cell.setCellValue(role_name);
        		HSSFComment comments = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) 0 , (h) , ( short ) 1, (h+2) ));
        		comments.setString( new HSSFRichTextString(role_id));
        		cell.setCellComment(comments);
        		ArrayList roledetailList = (ArrayList)map.get(role_id);
        		for(y=0;y<roledetailList.size();y++){
        			bean = (LazyDynaBean)roledetailList.get(y);
        			String staff_id=(String)bean.get("staff_id");
        			String detailtype=(String)bean.get("detailtype");
        			String detailname=(String)bean.get("detailname");
        			String status = (String)bean.get("status");
        			row = sheet.getRow(h+y+1);
        			if(row==null) {
                        row = sheet.createRow(h+y+1);
                    }
        			cell=row.createCell((short)(1));
        			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
            		cell.setCellValue(detailtype);
            		comments = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) 1 , (h+y+1) , ( short ) 2, (h+y+3) ));
            		comments.setString( new HSSFRichTextString(status));
            		cell.setCellComment(comments);
        			cell=row.createCell((short)(2));
        			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
            		cell.setCellValue(detailname);
            		comments = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) 2 , (h+y+1) , ( short ) 3, (h+y+3) ));
            		comments.setString( new HSSFRichTextString(staff_id));
            		cell.setCellComment(comments);
        		}
        		h=h+y+1;
    		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}