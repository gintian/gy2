package com.hjsj.hrms.businessobject.sys.rolemanagement;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/*import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;*/

/**
 * 
 * <p>Title:</p>
 * <p>Description:导入权限excel</p>
 * <p>Company:hjsj</p>
 * <p>create time:Nov 24, 2008:10:22:14 AM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class GuidePurviewExcelBo {
	private Connection conn = null;
	private UserView userView = null;
	
	public GuidePurviewExcelBo(Connection a_con,UserView userView){
		this.conn=a_con;
		this.userView=userView;
	}
	 /**
     * 用户标识
     */
    private String userflag=GeneralConstant.ROLE;
    private RowSet frowset;
	/**
	 * 取得人员库权限
	 */
	String cname[] = null;   //列.
	String cname1[] = null;  //指标叠加的数组
	String index[] = null;  //子集指标叠加数据
	String ername[]=null;   //行数
	int cells;
	String nullarray[]=null; //都为无的权限的;
	
	ArrayList rolesVo = new ArrayList();
	//public HashMap getusepurview(HSSFWorkbook workbook,HSSFSheet sheet2){
	public HashMap getusepurview(Workbook workbook,Sheet sheet2){
		HashMap map = new HashMap();
		try{
			//HSSFRow row = null;
			//HSSFCell cell=null;
			Row row = null;
			Cell cell=null;
			//得到总行数
			int rows=sheet2.getPhysicalNumberOfRows();
			ername=new String[rows];  //行
			for(int i=2;i<rows;i++){  //从第二行开始
				if(i==2){
					row=sheet2.getRow(i);
					cells=row.getPhysicalNumberOfCells(); //全局
					
					cname=new String[cells];
					cname1 = new String[cells];
					nullarray =  new String[cells];
					for(short j=2;j<cells;j++){  //列数
						cell=row.getCell(j);
						//得到comment值
						//HSSFComment comment = cell.getCellComment();
						//HSSFRichTextString commname = comment.getString();
						Comment comment = cell.getCellComment();
						RichTextString commname = comment.getString();
						cname[j]=commname.toString();  //列,top所有的批注值
						nullarray[j]="0";
						RecordVo vo = new RecordVo("t_sys_role");
						vo.setString("role_id", cname[j]);
						vo.setString("role_name", cell.getStringCellValue());
						vo.setString("valid","1");
				        vo.setString("status","1");
				        vo.setString("role_property", "-1");
				        rolesVo.add(vo);
					}
					
					
				}else{
					row=sheet2.getRow(i);
					int cells=row.getPhysicalNumberOfCells();
					for(short y=1;y<cells;y++){
						if(y==1){   //取列值
							cell=row.getCell(y);
//							得到comment值
							//HSSFComment comment = cell.getCellComment();
							//HSSFRichTextString commname = comment.getString();
							Comment comment = cell.getCellComment();
							RichTextString commname = comment.getString();
							ername[i]=commname.toString();
						}else {
							cell=row.getCell(y); //第二列以后
							String sanvalue = cell.getStringCellValue();
							if(sanvalue.equalsIgnoreCase(ResourceFactory.getProperty("kjg.title.have"))){
								String zongxiang =ername[i];  //纵向
								if(!this.userView.hasTheDbName(zongxiang)){
									continue;
								}
								int ee = (int)y;
								nullarray[ee]="1";
								String hangxiang = cname[ee];   //黄色  id号
								if(cname1[ee] == null) {
                                    cname1[ee]=","+zongxiang;  //所有"是"值
                                } else {
                                    cname1[ee]+=","+zongxiang;  //所有"是"值
                                }
							}
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	//写入人员库
	public void setvalue(){
		for(int i =2;i<cells;i++){
//			if(cname1[i]!=null&&cname1[i] != "")
//			{
//			String db_str =cname1[i]+",";   //叠加值
//			String role_id=cname[i];      //ID
//			saveDbPriv(role_id,db_str);
//			}
			
			if("0".equalsIgnoreCase(nullarray[i]))
			{
				String db_str=",,";	
				String role_id=cname[i];      //ID
				saveDbPriv(role_id,db_str);
			}
			else
			{
				if(cname1[i]==null||cname1[i]==""){
					String db_str=",,";	
					String role_id=cname[i];      //ID
					saveDbPriv(role_id,db_str);
				}else{
					String db_str =cname1[i]+",";   //叠加值
					String role_id=cname[i];      //ID
					saveDbPriv(role_id,db_str);
				}
			}
			
		}
		
		//保存角色
		ContentDAO dao = new ContentDAO(conn);
		for(int i=0;i<rolesVo.size();i++){
			RecordVo vo = (RecordVo)rolesVo.get(i);
			if(!this.isExistRecordVo(vo, dao)){
				try {
					dao.addValueObject(vo);
				} catch (GeneralException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private boolean isExistRecordVo(RecordVo vo,ContentDAO dao){
		RecordVo tmpvo =null;
		try{
			tmpvo = new RecordVo(vo.getModelName());
			ArrayList keylist=vo.getKeylist();
			for(int i=0;i<keylist.size();i++){
				String name= (String)keylist.get(i);
				tmpvo.setString(name, vo.getString(name));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		}
		return dao.isExistRecordVo(tmpvo);
		
	}
	//走陈总保存的方法;
	  private void saveDbPriv(String role_id,String dbstr) {
	        RecordVo vo=new RecordVo("t_sys_function_priv");
	        vo.setString("id",role_id);
	        vo.setString("status",this.userflag/*GeneralConstant.ROLE*/);        
	        vo.setString("dbpriv",dbstr);

	        SysPrivBo sysbo=new SysPrivBo(vo,conn);
	        sysbo.save();
	    }
	  //管理权限 sheet3
	  public void getcaretaker(Workbook workbook,Sheet sheet3){
		 // public void getcaretaker(HSSFWorkbook workbook,HSSFSheet sheet3){
		  try{
			  //HSSFRow row = null;
			  //HSSFCell cell=null;
			  Row row = null;
			  Cell cell=null;
			  //得到总行数
			  int rows=sheet3.getPhysicalNumberOfRows();
			  ername=new String[rows];  //行
			  for(int i=2;i<rows;i++){
				  if(i==2){
					  row=sheet3.getRow(i);
					  cells=row.getPhysicalNumberOfCells();
					  cname=new String[cells];
					  cname1 = new String[cells];
					  nullarray=new String[cells];
					  for(short j=0;j<cells;j++){ //不知道从列开始有批注,从0列开始遍历
						  cell=row.getCell(j);
						  //得到comment值
						  //HSSFComment comment = cell.getCellComment();
						  Comment comment = cell.getCellComment();
						  if(comment!=null){ //只有当批注不为空的时候才记录他的列数
							  //HSSFRichTextString commname = comment.getString();
							  RichTextString commname = comment.getString();
							  cname[j]=commname.toString();  //列,top所有的批注值
							  nullarray[j]="0";
						  }
						  nullarray[j]="0";
					  }
				  }else{
					  	row=sheet3.getRow(i);
						for(short y=1;y<cells;y++){
							
							cell=row.getCell(y);
							
							if(cell!=null){
								String sanvalue = cell.getStringCellValue();  //是或否
								//HSSFComment comment = cell.getCellComment();  //左边di
								Comment comment = cell.getCellComment();
								if(comment!=null){
									//HSSFRichTextString commname = comment.getString();
									RichTextString commname = comment.getString();
									ername[i]=commname.toString();  //行数
								}else if(sanvalue.equalsIgnoreCase(ResourceFactory.getProperty("kjg.title.have"))){
									String zongxiang =ername[i];  //纵向
									String priv = this.userView.getManagePrivCodeValue();
									if(!this.userView.isSuper_admin()){
										if(this.userView.getManagePrivCode().length()==0) {
                                            continue;
                                        }
										if(zongxiang.substring(2).indexOf(priv)==-1){
											continue;
										}
									}
									int ee = (int)y;
									nullarray[ee]="1";
									String hangxiang = cname[ee];   //黄色  id号 横向
									if(cname1[ee]==null){         //管理范围是单选，只记录第一个为是的值；
										if(cname1[ee] == null) {
                                            cname1[ee]=","+zongxiang;  //所有"是"值
                                        } else {
                                            cname1[ee]+=","+zongxiang;  //所有"是"值
                                        }
									}
								}
							}
						}
				  }
			  }
		  }catch(Exception e){
			  e.printStackTrace();
		  }
	  }
	  //写入管理范围
	  public void setcaretaker(){
			for(int i =2;i<cells;i++){
//				if(cname1[i]!=null&&cname1[i] != "")
//				{
//				String selstr =cname1[i]+",";   //叠加值
//				String role_id=cname[i];      //ID
//				saveManagePriv(role_id,selstr);
//				}
				if(cname[i]!=null&&!"".equals(cname[i])){
					if("0".equalsIgnoreCase(nullarray[i])){
						String selstr=",,";
						String[] arr=StringUtils.split(selstr,",");
						selstr=StringUtils.join(arr, ",");
						String role_id=cname[i];      //ID
						saveManagePriv(role_id,selstr);
					}else{
						if(cname1[i]==null||cname1[i]==""){
							String selstr=",,";
							String[] arr=StringUtils.split(selstr,",");
							selstr=StringUtils.join(arr, ",");
							String role_id=cname[i];      //ID
							saveManagePriv(role_id,selstr);
						}else{
							String selstrs =cname1[i]+",";   //叠加值
							String selstr  =selstrs.replaceAll(",", ""); //管理范围只能选择一个,所以不要,号
							String role_id=cname[i];      //ID
							saveManagePriv(role_id,selstr);
						}
					}
				}
				
			}
		}
	  private void saveManagePriv(String role_id,String manage_str)
	    {
	        if(manage_str==null) {
                manage_str= "";
            }
	        RecordVo vo=new RecordVo("t_sys_function_priv");
	        vo.setString("id",role_id);
	        vo.setString("status",this.userflag/*GeneralConstant.ROLE*/);
	        vo.setString("managepriv",manage_str);
//	        cat.debug("role_vo="+vo.toString());	
	        
	        SysPrivBo sysbo=new SysPrivBo(vo,conn);
	        sysbo.save();         
	    }
	  //查询子集指标授权
	  public void getsubset(Workbook workbook,Sheet sheet4){
		 // public void getsubset(HSSFWorkbook workbook,HSSFSheet sheet4){
		  try{
			  //HSSFRow row = null;
			  //HSSFCell cell=null;
			  Row row = null;
			  Cell cell=null;
			  //得到总行数
			  int rows=sheet4.getPhysicalNumberOfRows();
			  ername=new String[rows];  //行 动态分配数组
			  for(int i=3;i<rows;i++){
				  if(i==3){
					  row=sheet4.getRow(i);
					  cells=row.getPhysicalNumberOfCells();
					  cname=new String[cells];  //列 动态分配列
					  cname1 = new String[cells];
					  index = new String[cells]; // 指标叠加数据
					  nullarray = new String[cells];
					  for(short j=0;j<cells;j++){
						  cell=row.getCell(j);
						  Comment comment = cell.getCellComment();
						  //HSSFComment comment = cell.getCellComment();
						  if(comment!=null){ //只有当批注不为空的时候才记录他的列数
							  //HSSFRichTextString commname = comment.getString();
							  RichTextString commname = comment.getString();
							  cname[j]=commname.toString();  //列,top所有的批注值
							  nullarray[j]="0";
						  }
						  nullarray[j]="0";
					  }
				  }else{
					  row=sheet4.getRow(i);
					  cell=row.getCell((short)1);
					  if(cell!=null){
						  for(short y=1;y<cells;y++){
							  cell=row.getCell(y);
							  if(cell!=null){
								  String sanvalue = cell.getStringCellValue();  //是或否
								  if(!sanvalue.equalsIgnoreCase(ResourceFactory.getProperty("kjg.title.usesubset"))||!sanvalue.equalsIgnoreCase(ResourceFactory.getProperty("kjg.title.dwsubset"))||!sanvalue.equalsIgnoreCase(ResourceFactory.getProperty("kjg.title.zwsubset"))){
									 // HSSFComment comment = cell.getCellComment();  //左边di
									  Comment comment = cell.getCellComment();  //左边di
									  if(comment!=null){
										  RichTextString commname = comment.getString();
											//HSSFRichTextString commname = comment.getString();
											ername[i]=commname.toString();  //行数
										}else if(sanvalue.equalsIgnoreCase(ResourceFactory.getProperty("read.label"))){ //读
											String zongxiang =ername[i];  //纵向
											if(zongxiang.length()==3){//子集
												if("0".equalsIgnoreCase(this.userView.analyseTablePriv(zongxiang))) {
                                                    continue;
                                                }
											}else{//指标
												if("0".equalsIgnoreCase(this.userView.analyseFieldPriv(zongxiang))) {
                                                    continue;
                                                }
											}
											int ee = (int)y;
											nullarray[ee]="1";
											String hangxiang = cname[ee];   //黄色  id号 横向
											if(cname1[ee] == null) {
                                                cname1[ee]=","+zongxiang+"1";  //所有"是"值
                                            } else {
                                                cname1[ee]+=","+zongxiang+"1";  //所有"是"值
                                            }
										}else if(sanvalue.equalsIgnoreCase(ResourceFactory.getProperty("write.label"))){  //写
											String zongxiang =ername[i];  //纵向
											if(zongxiang.length()==3){//子集
												if(!"2".equalsIgnoreCase(this.userView.analyseTablePriv(zongxiang))) {
                                                    continue;
                                                }
											}else{//指标
												if(!"2".equalsIgnoreCase(this.userView.analyseFieldPriv(zongxiang))) {
                                                    continue;
                                                }
											}
											int ee = (int)y;
											nullarray[ee]="1";
											String hangxiang = cname[ee];   //黄色  id号 横向
											if(cname1[ee] == null) {
                                                cname1[ee]=","+zongxiang+"2";  //所有"是"值
                                            } else {
                                                cname1[ee]+=","+zongxiang+"2";  //所有"是"值
                                            }
										}
								  }
							  }
						  }
					  }
					  cell=row.getCell((short)2);
					  if(cell!=null){
						  for(short y=1;y<cells;y++){
							  cell=row.getCell(y);
							  if(cell!=null){
								  String sanvalue = cell.getStringCellValue();  //是或否
								  if(!sanvalue.equalsIgnoreCase(ResourceFactory.getProperty("kjg.title.usesubset"))||!sanvalue.equalsIgnoreCase(ResourceFactory.getProperty("kjg.title.dwsubset"))||!sanvalue.equalsIgnoreCase(ResourceFactory.getProperty("kjg.title.zwsubset"))){
									  Comment comment = cell.getCellComment();  //左边di
									  //HSSFComment comment = cell.getCellComment();
									  if(comment!=null){
											//HSSFRichTextString commname = comment.getString();
											RichTextString commname = comment.getString();
											ername[i]=commname.toString();  //行数
										}else if(sanvalue.equalsIgnoreCase(ResourceFactory.getProperty("read.label"))){ //读
											String zongxiang =ername[i];  //纵向
											if(zongxiang.length()==3){//子集
												if("0".equalsIgnoreCase(this.userView.analyseTablePriv(zongxiang))) {
                                                    continue;
                                                }
											}else{//指标
												if("0".equalsIgnoreCase(this.userView.analyseFieldPriv(zongxiang))) {
                                                    continue;
                                                }
											}
											int ee = (int)y;
											nullarray[ee]="1";
											String hangxiang = cname[ee];   //黄色  id号 横向
											if(index[ee] == null) {
                                                index[ee]=","+zongxiang+"1";  //所有"是"值
                                            } else {
                                                index[ee]+=","+zongxiang+"1";  //所有"是"值
                                            }
										}else if(sanvalue.equalsIgnoreCase(ResourceFactory.getProperty("write.label"))){  //写
											String zongxiang =ername[i];  //纵向
											if(zongxiang.length()==3){//子集
												if(!"2".equalsIgnoreCase(this.userView.analyseTablePriv(zongxiang))) {
                                                    continue;
                                                }
											}else{//指标
												if(!"2".equalsIgnoreCase(this.userView.analyseFieldPriv(zongxiang))) {
                                                    continue;
                                                }
											}
											int ee = (int)y;
											nullarray[ee]="1";
											String hangxiang = cname[ee];   //黄色  id号 横向
											if(index[ee] == null) {
                                                index[ee]=","+zongxiang+"2";  //所有"是"值
                                            } else {
                                                index[ee]+=","+zongxiang+"2";  //所有"是"值
                                            }
										}
								  }
							  }
						  }
					  }
				  }
			  }
		  }catch(Exception e){
			  e.printStackTrace();
		  }
	  }
	  //写入子集授权
	  public void setsubset(){
		  for(int i =0;i<cells;i++){
//				if(cname1[i]!=null&&cname1[i] != "")
//				{
//				String media_str =cname1[i]+",";   //叠加值
//				String role_id=cname[i];      //ID
//				saveTablePriv(role_id,media_str);
//				}
				if(cname[i]!=null&&!"".equals(cname[i])){
					if("0".equalsIgnoreCase(nullarray[i])){
						  String media_str = ",";
						  String role_id=cname[i];      //ID
						  saveTablePriv(role_id,media_str);
					  }else{
						  if(cname1[i]==null||cname1[i]==""){
							  String media_str = ",";
							  String role_id=cname[i];      //ID
							  saveTablePriv(role_id,media_str);
						  }else{
							  String media_str =cname1[i]+",";   //叠加值
							  String role_id=cname[i];      //ID
							  saveTablePriv(role_id,media_str); 
						  }
						  	
					  }
				}
			}
	  }
	  private void saveTablePriv(String role_id,String table_str)
	    {
	        if(table_str==null) {
                table_str="";
            }
	        RecordVo vo=new RecordVo("t_sys_function_priv");
	        vo.setString("id",role_id);
	        vo.setString("status",this.userflag/*GeneralConstant.ROLE*/);
	        vo.setString("tablepriv",table_str);
	        
	        SysPrivBo sysbo=new SysPrivBo(vo,conn);
	        sysbo.save();        
	    }
	//写入指标授权
	  public void setindex(){
		  for(int i =2;i<cells;i++){
//				if(index[i]!=null&&index[i] != "")
//				{
//				String media_str =index[i]+",";   //叠加值
//				String role_id=cname[i];      //ID
//				saveFieldPriv(role_id,media_str);
//				}
				if(cname[i]!=null&&!"".equals(cname[i])){
					if("0".equalsIgnoreCase(nullarray[i])){
						String media_str=",";
						String role_id=cname[i];      //ID
						saveFieldPriv(role_id,media_str);
					}else{
						if(index[i]==null||index[i]==""){
							String media_str=",";
							String role_id=cname[i];      //ID
							saveFieldPriv(role_id,media_str);
						}else{
							String media_str =index[i]+",";   //叠加值
							String role_id=cname[i];      //ID
							saveFieldPriv(role_id,media_str);
						}
						
					}
				}
			}
	  }
	  private void saveFieldPriv(String role_id,String field_str) //id;field_strs所选则的值
	  {
	      if(field_str==null) {
              field_str="";
          }
	      StringBuffer strsql=new StringBuffer();
	      strsql.append("select id from t_sys_function_priv where id='");
	      strsql.append(role_id);
	      strsql.append("' and status=");
	      strsql.append(this.userflag);
	      try
	      {
	    	ArrayList paralist=new ArrayList();
	    	ContentDAO dao=new ContentDAO(this.conn);
	    	this.frowset=dao.search(strsql.toString());
//	    	cat.debug("select sql="+strsql.toString());	

	    	if(this.frowset.next())
	    	{
		    	paralist.add(field_str);	    		
	    		strsql.setLength(0);
	    		strsql.append("update t_sys_function_priv set fieldpriv=?");
	    		//strsql.append(field_str);
	    		strsql.append(" where id='");
	    		strsql.append(role_id);
	    		strsql.append("' and status=");
	    		strsql.append(this.userflag);
	    	}
	    	else
	    	{
		    	paralist.add(role_id);	    		
		    	paralist.add(field_str);	    		
	    		strsql.setLength(0);
	    		strsql.append("insert into t_sys_function_priv (id,fieldpriv,status) values(?,?,");
	    		/*
	    		strsql.append(role_id);
	    		strsql.append("',");
	    		strsql.append(field_str);
	    		strsql.append("',");
	    		*/
	    		strsql.append(this.userflag);
	    		strsql.append(")");
	    	}
//	    	cat.debug("updat field_priv sql="+strsql.toString());
	    	dao.update(strsql.toString(),paralist);
	      }
	      catch(SQLException sqle)
	      {
	    	  sqle.printStackTrace();
	      }
//	      RecordVo vo=new RecordVo("t_sys_function_priv");
//	      vo.setString("id",role_id);
//	      vo.setString("fieldpriv",field_str);
//	      vo.setString("status",this.userflag/*GeneralConstant.ROLE*/);
//	      cat.debug("role_vo="+vo.toString());	
//	      
//	      SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
//	      sysbo.save();         
	  }  
	  // 功能授权
	  public void getfunction(Workbook workbook,Sheet sheet1){
		  //public void getfunction(HSSFWorkbook workbook,HSSFSheet sheet1){
		  try{
			  //HSSFRow row = null;
			 // HSSFCell cell=null;
			  Row row = null;
			  Cell cell=null;
			  //得到总行数
			  int rows=sheet1.getPhysicalNumberOfRows();
			  ername=new String[rows];  //行
			  for(int i=2;i<rows;i++){
				  if(i==2){
					  row=sheet1.getRow(i);
					  cells=row.getPhysicalNumberOfCells();
					  cname=new String[cells];
					  cname1 = new String[cells];
					  nullarray = new String[cells];
					  for(short j=0;j<cells;j++){ //不知道从列开始有批注,从0列开始遍历
						  cell=row.getCell(j);
						  //得到comment值
						  Comment comment = cell.getCellComment();
						  //HSSFComment comment = cell.getCellComment();
						  
						  if(comment!=null){ //只有当批注不为空的时候才记录他的列数
							  //HSSFRichTextString commname = comment.getString();
							  RichTextString commname = comment.getString();
							  cname[j]=commname.toString();  //列,top所有的批注值
							  nullarray[j]="0";
						  }
						  nullarray[j]="0";
					  }
				  }else{
					  	row=sheet1.getRow(i);
						for(short y=1;y<cells;y++){
							
							cell=row.getCell(y);
							
							if(cell!=null){
								String sanvalue = cell.getStringCellValue();  //是或否
								Comment comment = cell.getCellComment();  //左边di
								//HSSFComment comment = cell.getCellComment(); 
								if(comment!=null){
									//HSSFRichTextString commname = comment.getString();
									RichTextString commname = comment.getString();
									ername[i]=commname.toString();  //行数
								}else if(sanvalue.equalsIgnoreCase(ResourceFactory.getProperty("kjg.title.have"))){
									String zongxiang =ername[i];  //纵向
									if(!this.userView.hasTheFunction(zongxiang)){
										continue;
									}
									int ee = (int)y;
									nullarray[ee]="1";
									String hangxiang = cname[ee];   //黄色  id号 横向
									if(cname1[ee] == null) {
                                        cname1[ee]=","+zongxiang;  //所有"是"值
                                    } else {
                                        cname1[ee]+=","+zongxiang;  //所有"是"值
                                    }
								}
							}
						}
				  }
			  }
		  }catch(Exception e){
			  e.printStackTrace();
		  }
	  }
	  //写入功能授权
	  public void setfunction(){
		  for(int i =2;i<cells;i++){
//				if(cname1[i]!=null&&cname1[i] != "")
//				{
//				String func_str =cname1[i]+",";   //叠加值
//				String role_id=cname[i];      //ID
//				saveFunctionPriv(role_id,func_str);
//				}
			  if(cname[i]!=null&&!"".equals(cname[i])){
				  if("0".equalsIgnoreCase(nullarray[i])){
						String func_str=",,";
						String role_id=cname[i];      //ID
						saveFunctionPriv(role_id,func_str);
					}else{
						if(cname1[i]==null||cname1[i]==""){
							String func_str=",,";
							String role_id=cname[i];      //ID
							saveFunctionPriv(role_id,func_str);
						}else{
							String func_str =cname1[i]+",";   //叠加值
							String role_id=cname[i];      //ID
							saveFunctionPriv(role_id,func_str);
						}
					}
			  }
				
			}
	  }
	  private void saveFunctionPriv(String role_id,String func_str) {
	        RecordVo vo=new RecordVo("t_sys_function_priv",1);
	        vo.setString("id",role_id);
	        vo.setString("status",this.userflag/*GeneralConstant.ROLE*/);
	        vo.setString("functionpriv",func_str);
//	        cat.debug("role_vo="+vo.toString());	
	        
	        SysPrivBo sysbo=new SysPrivBo(vo,conn);
	        sysbo.save();

	    }
}
