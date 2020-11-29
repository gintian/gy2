/*
 * Created on 2006-4-27
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.businessobject.ykcard;


import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.performance.statistic.StatisticPlan;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.constant.FontFamilyType;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.ykcard.RGridView;
import com.hjsj.hrms.valueobject.ykcard.RPageView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class YkcardPdf {
	private Connection conn=null;
	private float lt=0;  //表格整体往左靠，需减的边距像素
	private float tp=0;  //顶边距象素	
	private int tabid;
	private String plan_id;
	private String ykcard_auto;
	private int tmargin=0;
	private int bmargin=0;
	private int lmargin=0;
	private int rmargin=0;
	private float h_base=1.002f;
	private float w_base=1.002f;
	private String display_zero="";
	private String platform;
	private static float mMtoItext=297f/842;//毫米转itext 需要的比例  
	private static float converPxTomm=1f/96*25.4f; //像素转毫米 
	private  HashMap gridPropty=null;//登记表参数内容信息
	private static int  isZipFile=0;//0 生成压缩文件  1 不生成压缩文件
	private static HashMap fileNameMap=new HashMap();////回传文件名称集合
	
	public static HashMap getFileNameMap() {
        return fileNameMap;
    }

    public static void setFileNameMap(HashMap fileNameMap) {
        YkcardPdf.fileNameMap = fileNameMap;
    }

    public static int getIsZipFile() {
        return isZipFile;
    }

    public static void setIsZipFile(int isZipFile) {
        YkcardPdf.isZipFile = isZipFile;
    }

    public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	public YkcardPdf(Connection conn) {
		this.conn=conn;
		this.fileNameMap.clear();
	}

	public YkcardPdf() {	
	    this.fileNameMap.clear();
	}
	public String executePdfS(int tabid,ArrayList nid,String dbname,UserView userview,String cyear,String querytype,String cmonth,String userpriv,String istype,String season,String ctimes,String cdatestart,String cdateend,String infokind,String fieldpurv) throws Exception
	{
         /*数据查询方式0条件1月2时间段3季4年*/
	     /*年*/               
	     /*月*/
	     /*用户权限*/
         /*0表示薪酬表。2表示机构1表示职位表3登记表*/
		this.tabid=tabid;
		if(this.tabid==-1) {
            throw GeneralExceptionHandler.Handle(new GeneralException("","没有选择登记表,请选择!","",""));
        }
	    String havepriv="1";            /*是否有权限*/
		String disting_pt="1024";	
		String url="";
		int pageid=0;
		FileOutputStream out = null;
		try{		
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
			this.ykcard_auto=sysbo.getValue(Sys_Oth_Parameter.YKCARD_AUTO);
			CardConstantSet cardConstantSet=new CardConstantSet();
			LazyDynaBean rnameExtendAttrBean=cardConstantSet.getRnameExtendAttrBean(conn,tabid+"");
			if(rnameExtendAttrBean!=null)
			{
				if(ykcard_auto==null||ykcard_auto.length()<=0|| "0".equals(ykcard_auto))
				{
					ykcard_auto=(String)rnameExtendAttrBean.get("auto_size");
				}
				this.display_zero=(String)rnameExtendAttrBean.get("display_zero");
			}
			DataEncapsulation encap=new DataEncapsulation();
			encap.setUserview(userview);
			/*获得页面的大小*/
            float[] papersize=getPaperSize(encap, tabid,conn);
            Rectangle pageSize = new Rectangle(papersize[0], papersize[1]);			//自定义纸张大小
          
    		Document document = null;
    		document=new Document(pageSize);
    		url="card_"+PubFunc.getStrg()+"_"+tabid+".pdf";
    		out = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")
					+  url);
    		PdfWriter writer = PdfWriter.getInstance(document,out);

			document.open();
			
			if(nid!=null){
			  for(int i=0;i<nid.size();i++){
				 /*生成页表头*/
			
				 String id=nid.get(i).toString();
				 
				 /*生成表体*/		
			     createPageBody(document,infokind,tabid, id, dbname, userview, Integer.parseInt(querytype), Integer.parseInt(cyear), Integer.parseInt(cmonth), userpriv, havepriv, Integer.parseInt(season), Integer.parseInt(ctimes), cdatestart, cdateend, disting_pt, pageid, encap, papersize, writer,fieldpurv);
			   }
			}else
		    {
				   /*生成页表头*/
					//createPageTitle(tabid,pageid,"", dbname, encap, papersize, writer,infokind);
					/*生成表体*/	
				   createPageBody(document,infokind,tabid, "", dbname, userview, Integer.parseInt(querytype), Integer.parseInt(cyear), Integer.parseInt(cmonth), userpriv, havepriv, Integer.parseInt(season), Integer.parseInt(ctimes), cdatestart, cdateend, disting_pt, pageid, encap, papersize, writer,fieldpurv);
 
			}
			document.close();		
		}catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}finally{
			PubFunc.closeIoResource(out);
		}		
		return url;
	}
	/***
	 * 查询需要导出的人员或单位或岗位名称
	 * */
	public String  searchExportName(String nid,String dbname,String infokind,UserView userView){
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs=null;
		String sql="";
		String exportName="";
		if(nid==null||"".equals(nid)) {
            return null;
        }
		try {
			if("1".equals(infokind)){//人员
	    		sql="select A0101 from "+dbname+"A01 where A0100='"+nid+"'";
	    	}else if("2".equals(infokind)){//单位 UN   UM部门
	    		if(userView.getStatus()==4)
	            {
		    		sql="select organization.codeitemdesc from t_sys_result,organization where organization.codeitemid=t_sys_result.obj_id and t_sys_result.flag=1" +
		    				"  and UPPER(t_sys_result.username)='"+userView.getUserName().toUpperCase()+"' and t_sys_result.obj_id='"+nid+"'";
	            }else
	            {
	            	sql="select organization.codeitemdesc from "+userView.getUserName()+"BResult," +
	            			"organization where organization.codeitemid="+userView.getUserName()+"BResult.b0110 and "+userView.getUserName()+"BResult.b0110='"+nid+"'";
	            }        			
	    	}else if("4".equals(infokind)){//岗位 @K
	    		if(userView.getStatus()==4)
	            {
		    		sql="select organization.codeitemdesc from " +
		    				"t_sys_result,organization where organization.codeitemid=t_sys_result.obj_id and " +
		    				"t_sys_result.flag=2  and UPPER(t_sys_result.username)='"+userView.getUserName().toUpperCase()+"' and t_sys_result.obj_id='"+nid+"'";
	            }else
	            {
	            	sql="select organization.codeitemdesc " +
	            			"from "+userView.getUserName()+"KResult,organization" +
	            			" where organization.codeitemid="+userView.getUserName()+"KResult.E01A1 and "+userView.getUserName()+"KResult.E01A1='"+nid+"'";
	            }
	    	}else if("6".equals(infokind)){//基准岗位
	    		String codeset=new CardConstantSet().getStdPosCodeSetId();
	    		sql="select codeitemdesc from t_sys_result,CodeItem "+
	    				   " where CodeItem.codeitemid=t_sys_result.obj_id and codesetid='"+codeset+"'"+
	    				        " and t_sys_result.flag=5"+// 基准岗位
	    				        " and UPPER(t_sys_result.username)='"+userView.getUserName().toUpperCase()+"' and t_sys_result.obj_id= '"+nid+"'";
	    	}
			rs=dao.search(sql);
			while(rs.next()){
				exportName=rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return exportName;
	}
	public String executePdf(int tabid,String nid,String dbname,UserView userview,String cyear,String querytype,String cmonth,String userpriv,String istype,String season,String ctimes,String cdatestart,String cdateend,String infokind,String fieldpurv,String platform)throws GeneralException  
	{
         /*数据查询方式0条件1月2时间段3季4年*/
	     /*年*/               
	     /*月*/
	     /*用户权限*/
         /*0表示薪酬表。2表示机构1表示职位表3登记表*/
	    String havepriv="1";            /*是否有权限*/
		String disting_pt="1024";
		this.platform=platform;
		String url="";
		this.tabid=tabid;
		if(this.tabid==-1) {
            throw GeneralExceptionHandler.Handle(new GeneralException("","没有选择登记表,请选择!","",""));
        }
		int pageid=0;
		PdfWriter writer = null;
		FileOutputStream outputStream=null;
		try{			
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
			this.ykcard_auto=sysbo.getValue(Sys_Oth_Parameter.YKCARD_AUTO);
			CardConstantSet cardConstantSet=new CardConstantSet();
			LazyDynaBean rnameExtendAttrBean=cardConstantSet.getRnameExtendAttrBean(conn,tabid+"");
			if(rnameExtendAttrBean!=null)
			{
				if(ykcard_auto==null||ykcard_auto.length()<=0|| "0".equals(ykcard_auto))
				{
					ykcard_auto=(String)rnameExtendAttrBean.get("auto_size");
				}
				this.display_zero=(String)rnameExtendAttrBean.get("display_zero");
			}
			DataEncapsulation encap=new DataEncapsulation();
			encap.setUserview(userview);
			/*获得页面的大小*/
            float[] papersize=getPaperSize(encap, tabid,conn);
            Rectangle pageSize = new Rectangle(papersize[0], papersize[1]);			//自定义纸张大小
           
    		Document document = null;
    		document=new Document(pageSize);
    		//liuy 2015-3-20 8179：自助服务/员工信息/我的薪酬，生成PDF，每次点击生成PDF都会生成不同的文件名，期望按统一规则各模块只生成一个，避免在临时目录造成太多的垃圾数据 begin
    		//url="card_"+PubFunc.getStrg()+"_"+tabid+".pdf";
    		//url=userview.getUserName()+"_card.pdf";
    		if("1".equals(infokind)|| "2".equals(infokind)|| "4".equals(infokind)|| "6".equals(infokind)){
        		String exportname=searchExportName(nid,dbname,infokind,userview);
        		if(exportname!=null&&!"".equals(exportname)) {
                    url=exportname+"_"+userview.getUserName()+"_card.pdf";
                } else {
                    url=userview.getUserName()+"_card.pdf";
                }
        	}else{
        		url=userview.getUserName()+"_card.pdf";
        	}
    		//liuy 2015-3-20 end
    		outputStream=new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")
                    +  url);
    		writer = PdfWriter.getInstance(document,outputStream
					);


			document.open();
			/*生成页表头*/
   			//createPageTitle(tabid,pageid, nid, dbname, encap, papersize, writer,infokind);
			/*生成表体*/
   			int cyearint=0;
   			int cmonthint=0;
   			int seasonint=0;
   			int ctimesint=0;
   			if(ctimes!=null) {
                ctimesint=Integer.parseInt(ctimes);
            }
   			if(season!=null) {
                seasonint=Integer.parseInt(season);
            }
   			
   			if(cyear!=null) {
                cyearint=Integer.parseInt(cyear);
            }
   			if(cmonth!=null) {
                cmonthint=Integer.parseInt(cmonth);
            }

		    createPageBody(document,infokind,tabid, nid, dbname, userview, Integer.parseInt(querytype), cyearint, cmonthint, userpriv, havepriv, seasonint, ctimesint, cdatestart, cdateend, disting_pt, pageid, encap, papersize, writer,fieldpurv);
		    document.close();		
		}catch(Exception e){
			throw GeneralExceptionHandler.Handle(e);
		}finally{
		    PubFunc.closeResource(outputStream);
		    PubFunc.closeResource(writer);
		}
		return url;
	}
	
	/**
	 * 删除子文件夹及文件夹内文件
	 * **/
	public void deleteDirOrFile(String path){
		File file=new File(path);
		if(file.exists()){
			if(file.isDirectory()){
				File[] listFile=file.listFiles();
				for (int i = 0; i < listFile.length; i++) {
					String childpath=listFile[i].getAbsolutePath();
					if(listFile[i].isFile()){
						File childFile=new File(childpath);
						childFile.delete();
					}
				}
				file.delete();
			}else{
				file.delete();
			}
		}
	}
	
	public HashMap<String, String> getFileName(ArrayList nidList,String dbper,String tableName){
		HashMap<String, String> map=new HashMap<String, String>();
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
        String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name");//身份证
        StringBuffer sbf=new StringBuffer();
        for (int i = 0; i < nidList.size(); i++) {
			sbf.append("?");
			if(i<nidList.size()-1) {
                sbf.append(",");
            }
		}
        StringBuffer sql = new StringBuffer("select A0100,A0101 ");
        if(StringUtils.isNotBlank(chk)) {
            sql.append(","+chk);
        }
        sql.append(" from "+dbper+"A01 where a0100 in (");
        sql.append(sbf+")");
        RowSet rs=null;
        ContentDAO dao=new ContentDAO(this.conn);
        HashMap<String,Integer> namesmap=new HashMap<String,Integer>();//当唯一性标识为空时 判断是否有重名 有则+1
        try {
        	rs=dao.search(sql.toString(), nidList);	
        	while(rs.next()){
        		String chkValue=StringUtils.isNotBlank(chk)?rs.getString(chk):"";
        		String a0101=rs.getString("A0101");
        		String tempName="";
        		if(StringUtils.isEmpty(chkValue)) {
        			
        			if(namesmap.get(a0101)==null) {
        				namesmap.put(a0101, 0);
        				
        			}else {
        				namesmap.put(a0101,namesmap.get(a0101)+1);
        				a0101=a0101+"("+(namesmap.get(a0101))+")";
        			}
        			tempName+=a0101;
        		}else {
        			tempName+=a0101+"_"+chkValue;
        		}
        		map.put(rs.getString("A0100"), tempName+"_"+tableName);
        		fileNameMap.put(rs.getString("A0100"), tempName+"_"+tableName);
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		return map;
	}
	
	/***
	 * 文件夹压缩
	 * */
	public String createZipFile(UserView userview,String sourceFilePath)throws GeneralException{
		String tmpFileName=userview.getUserName()+"_tempCard.zip";
        File sourceFile = new File(sourceFilePath);  
        FileInputStream fis = null;  
        BufferedInputStream bis = null;  
        FileOutputStream fos = null;  
        ZipOutputStream zos = null;  
          
        try {  //System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+userview.getUserName()+"_tempCard"
	        if(sourceFile.exists() == false){  
	            throw GeneralExceptionHandler.Handle(new Exception("压缩文件夹不存在！"));
	        }else{  
                File zipFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator") + tmpFileName);  
                if(zipFile.exists()){  
                    zipFile.delete();
                }
                
                File[] sourceFiles = sourceFile.listFiles();  
                if(null == sourceFiles || sourceFiles.length<1){ 
                	throw GeneralExceptionHandler.Handle(new Exception("待压缩的文件目录：" + sourceFilePath + "里面不存在文件，无需压缩."));
                }else{  
                    fos = new FileOutputStream(zipFile);  
                    zos = new ZipOutputStream(new BufferedOutputStream(fos));  
                    byte[] bufs = new byte[1024*10];  
                    for(int i=0;i<sourceFiles.length;i++){  
                        //创建ZIP实体，并添加进压缩包  
                        ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());  
                        zos.putNextEntry(zipEntry);  
                        //读取待压缩的文件并写进压缩包里  
                        fis = new FileInputStream(sourceFiles[i]);  
                        bis = new BufferedInputStream(fis, 1024*10);  
                        int read = 0;  
                        while((read=bis.read(bufs, 0, 1024*10)) != -1){  
                            zos.write(bufs,0,read);  
                        }
                        fis.close();
                        bis.close();
                    }
                    zos.closeEntry();
                }  
            }  
        }catch (IOException e) {  
            e.printStackTrace();  
            throw GeneralExceptionHandler.Handle(e);
        } finally{  
            //关闭流  
            	PubFunc.closeIoResource(bis);
            	PubFunc.closeIoResource(zos);
                PubFunc.closeIoResource(fis);
                PubFunc.closeIoResource(fos);
          
        } 
		return tmpFileName;
	}
	
	/***
	 * 批量生成单个pdf文件，生成压缩文件
	 * nidList 人员id集合
	 * nameList 文件命名规则集合 m
	 * */
	public String executePdf(Map<String,ArrayList<String>> map,String dbname,UserView userview,String cyear,String querytype,String cmonth,String userpriv,String istype,String season,String ctimes,String cdatestart,String cdateend,String infokind,String fieldpurv,String platform)throws GeneralException  
	{
		String dirPath=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+ userview.getUserName()+"_tempCard";
		PdfWriter writer = null;
		FileOutputStream outputStream=null;
		try {

	         /*数据查询方式0条件1月2时间段3季4年*/
		     /*年*/               
		     /*月*/
		     /*用户权限*/
	         /*0表示薪酬表。2表示机构1表示职位表3登记表*/
			
		    String havepriv="1";            /*是否有权限*/
			String disting_pt="1024";
			this.platform=platform;
			String url="";
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
			this.ykcard_auto=sysbo.getValue(Sys_Oth_Parameter.YKCARD_AUTO);
			CardConstantSet cardConstantSet=new CardConstantSet();
			
			//临时文件夹内创建批量导出pdf文件夹 su_tempCard
			File file=new File(dirPath);
			if(file.exists()&&file.isDirectory()){
				deleteDirOrFile(dirPath);
			}
			file.mkdir();
			for(String id : map.keySet()) {//map("tabid",nidList);
			    if("#".equals(id)) {
			        continue;
			    }
				int tabid=Integer.parseInt(id);
				ArrayList nidList=(ArrayList)map.get(id);
				this.tabid=tabid;
				if(this.tabid==-1) {
                    throw GeneralExceptionHandler.Handle(new GeneralException("","没有选择登记表,请选择!","",""));
                }

				LazyDynaBean rnameExtendAttrBean=cardConstantSet.getRnameExtendAttrBean(conn,tabid+"");
				if(rnameExtendAttrBean!=null)
				{
					if(ykcard_auto==null||ykcard_auto.length()<=0|| "0".equals(ykcard_auto))
					{
						ykcard_auto=(String)rnameExtendAttrBean.get("auto_size");
					}
					this.display_zero=(String)rnameExtendAttrBean.get("display_zero");
				}
				DataEncapsulation encap=new DataEncapsulation();
				encap.setUserview(userview);
				/*获得页面的大小*/
	            float[] papersize=getPaperSize(encap, tabid,conn);
	            Rectangle pageSize = new Rectangle(papersize[0], papersize[1]);			//自定义纸张大小
	            String tableName=((LazyDynaBean)encap.getRname(tabid, conn).get(0)).get("name").toString();
				HashMap<String, String> filenameList=getFileName(nidList, dbname, tableName);//获取用户名 姓名+唯一性标识+模板名称
	            
				HashMap gridPropty=new HashMap();
				this.gridPropty=gridPropty;
				List pageData=new DataEncapsulation().getPagecount(tabid,conn);
				gridPropty.put("pageData", pageData);
				
				List setList=encap.GetSetsPDF(tabid,conn); 
				gridPropty.put("setList", setList);
				
				/*List rpageList=encap.getRpage(tabid,0,conn);
				gridPropty.put("rpageList", rpageList);*/
				if(!pageData.isEmpty()){
					for (int i = 0; i < pageData.size(); i++) {
						 DynaBean rec=(DynaBean)pageData.get(i);//Integer.parseInt(rec.get("pageid").toString())
						 List rgrids=encap.getRgrid(tabid,Integer.parseInt(rec.get("pageid").toString()),conn);             //获得Grid各个cell的List的对象
						 List rgridsBL=encap.getRgridIsbrokenline(tabid,Integer.parseInt(rec.get("pageid").toString()),conn);             //获得Grid各个cell的List的对象
						 gridPropty.put("rgrids"+rec.get("pageid").toString(), rgrids);
						 gridPropty.put("rgridsBL"+rec.get("pageid").toString(), rgridsBL);
						 gridPropty.put("rpageList"+Integer.parseInt(rec.get("pageid").toString()), encap.getRpage(tabid,Integer.parseInt(rec.get("pageid").toString()),conn));
					}
				}
				for (int i = 0; i < nidList.size(); i++) {
					String nid=nidList.get(i).toString();
					int pageid=0;
			    		Document document = null;
			    		document=new Document(pageSize);
			    		outputStream=new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")
			                    + userview.getUserName()+"_tempCard" +System.getProperty("file.separator")+filenameList.get(nid)+".pdf");
			    		writer = PdfWriter.getInstance(document,outputStream
								);
						document.open();
						/*生成页表头*/
			   			//createPageTitle(tabid,pageid, nid, dbname, encap, papersize, writer,infokind);
						/*生成表体*/
			   			int cyearint=0;
			   			int cmonthint=0;
			   			int seasonint=0;
			   			int ctimesint=0;
			   			if(ctimes!=null) {
                            ctimesint=Integer.parseInt(ctimes);
                        }
			   			if(season!=null) {
                            seasonint=Integer.parseInt(season);
                        }
			   			
			   			if(cyear!=null) {
                            cyearint=Integer.parseInt(cyear);
                        }
			   			if(cmonth!=null) {
                            cmonthint=Integer.parseInt(cmonth);
                        }

					    createPageBody(document,infokind,tabid, nid, dbname, userview, Integer.parseInt(querytype), 
					    		cyearint, cmonthint, userpriv, havepriv, seasonint, ctimesint, cdatestart, cdateend, 
					    		disting_pt, pageid, encap, papersize, writer,fieldpurv);
					    
					    document.close();		
					    outputStream.close();
					
					}
				
				this.gridPropty.clear();//循环第二个表时 map内容清除
			}
			//循环完成后压缩文件 返回压缩文件夹的文件
			
			if(file.exists()&&file.isDirectory()){
				File[] fils=file.listFiles();
				if(fils.length>1&&0==isZipFile){//导出多个文件 生成压缩文件
					url=createZipFile(userview,dirPath);
					
				}else if(fils.length==1){//单个文件不需要压缩
					File oldF=new File(fils[0].getAbsolutePath());
					url=oldF.getName();
					File newF=new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+url);
					if(newF.exists()) {
                        newF.delete();
                    }
					oldF.renameTo(newF);
				}
				
			}
				
			this.gridPropty=null;
			return url;
		
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(outputStream);
			PubFunc.closeResource(writer);
			if(0==isZipFile) {
                deleteDirOrFile(dirPath);//文件压缩完成后删除文件夹
            }
		}
	}
	
	/**
	 * @param tabid
	 * @param nid
	 * @param dbname
	 * @param userview
	 * @param querytype
	 * @param cyear
	 * @param cmonth
	 * @param userpriv
	 * @param havepriv
	 * @param season
	 * @param ctimes
	 * @param cdatestart
	 * @param cdateend
	 * @param disting_pt
	 * @param pageid
	 * @param encap
	 * @param papersize
	 * @param writer
	 * @throws Exception 
	 */
	private void createPageBody(Document document,String infokind,int tabid, String nid, 
			String dbname, UserView userview, int querytype, int cyear, int cmonth, String userpriv, String havepriv,
			int season, int ctimes, String cdatestart, String cdateend, String disting_pt, int pageid, 
			DataEncapsulation encap, float[] papersize, PdfWriter writer,String fieldpurv) throws Exception {
		
		 MadeFontsizeToCell mc=new MadeFontsizeToCell();              //创建的字体适应cell大小的对象
		 mc.setAuto(this.ykcard_auto);
		 mc.setOutType("pdf");
		 GetCardCellValue card=new GetCardCellValue();                //创建获得单元个cell值的对象
		 card.setDisplay_zero(display_zero);//创建获得单元个cell值的对象
	     getMargin(tabid,conn);//页边据
		 try{		
			 List pageData=null;
			 List rpageList=null;
			 List setList=null;
			 if(this.gridPropty!=null){
				 pageData=(List)gridPropty.get("pageData");
				 //rpageList=(List)gridPropty.get("rpageList");
				 setList=(List)gridPropty.get("setList");
			 }else{
				 pageData=new DataEncapsulation().getPagecount(tabid,conn);
				 //rpageList=encap.getRpage(tabid,pageid,conn);
				 setList=encap.GetSetsPDF(tabid,conn);
			 }
			 if(!pageData.isEmpty()){
			 	for(int pages=0;pages<pageData.size();pages++)
			 	{
			 		DynaBean rec=(DynaBean)pageData.get(pages);
			 		 List rgrids=null;//encap.getRgrid(tabid,Integer.parseInt(rec.get("pageid").toString()),conn);             //获得Grid各个cell的List的对象
			 		 List rgridsBL=null;//encap.getRgridIsbrokenline(tabid,Integer.parseInt(rec.get("pageid").toString()),conn); 
			 		 if(this.gridPropty!=null){
			 			rgrids=(List)gridPropty.get("rgrids"+rec.get("pageid").toString());
			 			rgridsBL=(List)gridPropty.get("rgridsBL"+rec.get("pageid").toString());
			 			rpageList=(List)gridPropty.get("rpageList"+Integer.parseInt(rec.get("pageid").toString()));
			 		 }else{
			 			rgridsBL=encap.getRgridIsbrokenline(tabid,Integer.parseInt(rec.get("pageid").toString()),conn); 
			 			rpageList=encap.getRpage(tabid,Integer.parseInt(rec.get("pageid").toString()),conn);
			 			rgrids=encap.getRgrid(tabid,Integer.parseInt(rec.get("pageid").toString()),conn);             //获得Grid各个cell的List的对象
			 		 }			 	 
					 printGrids(infokind,tabid, nid, dbname, userview, querytype, cyear, cmonth, userpriv,
							 havepriv, season, ctimes, cdatestart, cdateend, disting_pt, pageid, encap, papersize, writer, mc, card, rec,fieldpurv,rpageList,setList,rgrids,rgridsBL);
					 document.newPage();				     
			    }
		    }
		 }catch(Exception e){
		 	e.printStackTrace();
		 	throw e;
		 }
	}

	private void printGrids(String infokind,int tabid, String nid, String dbname, UserView userview, int querytype, int cyear, 
			int cmonth, String userpriv, String havepriv, int season, int ctimes, String cdatestart, String cdateend, 
			String disting_pt, int pageid, DataEncapsulation encap, float[] papersize, PdfWriter writer, MadeFontsizeToCell mc, 
			GetCardCellValue card, DynaBean rec,String fieldpurv,List rpageList,List setList,List rgrids,List rgridsBL) throws Exception, BadElementException, MalformedURLException, IOException {
		createPageTitle(tabid,Integer.parseInt(rec.get("pageid").toString()), nid, dbname, encap, papersize, writer,infokind,rpageList);
		printGridsIsNotBrokenLine(infokind,tabid, nid, dbname, userview, querytype, cyear, cmonth, userpriv, havepriv, season, ctimes, cdatestart, cdateend, disting_pt, pageid, encap, papersize, writer, mc, card, rec,fieldpurv,rgrids,setList);
		printGridsIsNotBrokenLineC(infokind,tabid, nid, dbname, userview, querytype, cyear, cmonth, userpriv, havepriv, season, ctimes, cdatestart, cdateend, disting_pt, pageid, encap, papersize, writer, mc, card, rec,rgrids,setList);
		printGridsIsBrokenLine(tabid, nid, dbname, userview, querytype, cyear, cmonth, userpriv, havepriv, season, ctimes, cdatestart, cdateend, disting_pt, pageid, encap, papersize, writer, mc, card, rec,rgridsBL);
	}	
	/**
	 * @param tabid
	 * @param nid
	 * @param dbname
	 * @param userview
	 * @param querytype
	 * @param cyear
	 * @param cmonth
	 * @param userpriv
	 * @param havepriv
	 * @param season
	 * @param ctimes
	 * @param cdatestart
	 * @param cdateend
	 * @param disting_pt
	 * @param pageid
	 * @param encap
	 * @param papersize
	 * @param writer
	 * @param mc
	 * @param card
	 * @param rec
	 * @throws Exception
	 * @throws BadElementException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private void printGridsIsBrokenLine(int tabid, String nid, String dbname, UserView userview,
			int querytype, int cyear, int cmonth, String userpriv, String havepriv, 
			int season, int ctimes, String cdatestart, String cdateend, String disting_pt,
			int pageid, DataEncapsulation encap, float[] papersize, 
			PdfWriter writer, MadeFontsizeToCell mc, GetCardCellValue card, DynaBean rec,List rgrids) throws Exception, BadElementException, MalformedURLException, IOException {
		//显示各个单元个得开始
		//List rgrids=encap.getRgridIsbrokenline(tabid,Integer.parseInt(rec.get("pageid").toString()),conn);             //获得Grid各个cell的List的对象
		 RGridView rgrid;
		 float topn;                                                  //单元格的上边位置
		 float leftn;                                                 //单元格的左边位置
		 float heights;                                               //单元格的高
		 float widthn;                                                //单元格的宽
		if(!rgrids.isEmpty())
		{
			for(int i=0;i<rgrids.size();i++)
		    {
		    	 rgrid=(RGridView)rgrids.get(i);
		    	 leftn=Float.parseFloat(rgrid.getRleft())*w_base*converPxTomm/mMtoItext+this.lmargin*w_base;
		         topn=papersize[1]-Float.parseFloat(rgrid.getRtop())*h_base*converPxTomm/mMtoItext-this.tmargin*h_base;;
		         widthn=Float.parseFloat(rgrid.getRwidth())*w_base*converPxTomm/mMtoItext;
		         heights=Float.parseFloat(rgrid.getRheight())*h_base*converPxTomm/mMtoItext;				  			    
			     if("0".equals(rgrid.getB()))
			     {
			    	   PdfContentByte cb = writer.getDirectContent();
					   cb.setLineWidth(1f);
					   cb.moveTo(leftn,topn - heights);
					   cb.lineTo(leftn + widthn,topn - heights);
					   cb.setColorStroke(new Color(255,255,255));
					   cb.stroke();	
			     }			    	
			     if("0".equals(rgrid.getT()))
			     {
			    	   PdfContentByte cb = writer.getDirectContent();
					   cb.setLineWidth(1f);
					   cb.moveTo(leftn,topn);
					   cb.lineTo(leftn + widthn,topn);
					   cb.setColorStroke(new Color(255,255,255));
					   cb.stroke();	
			     }			    	
			     if("0".equals(rgrid.getL()))
			     {
			    	   PdfContentByte cb = writer.getDirectContent();
					   cb.setLineWidth(1f);
					   cb.moveTo(leftn,topn);
					   cb.lineTo(leftn,topn-heights);
					   cb.setColorStroke(new Color(255,255,255));
					   cb.stroke();	
			     } 
			     if("0".equals(rgrid.getR()))
			     {
			    	   printBrokenLine(writer, topn, leftn, heights, widthn);	
			     } 			 
			}
		}
	}

	/**
	 * @param writer
	 * @param topn
	 * @param leftn
	 * @param heights
	 * @param widthn
	 */
	private void printBrokenLine(PdfWriter writer, float topn, float leftn, float heights, float widthn) {
		PdfContentByte cb = writer.getDirectContent();
		   cb.setLineWidth(1f);
		   cb.moveTo(leftn + widthn,topn);
		   cb.lineTo(leftn + widthn,topn-heights);
		   cb.setColorStroke(new Color(255,255,255));
		   cb.stroke();
	}
	/**
	 * @param tabid
	 * @param nid
	 * @param dbname
	 * @param userview
	 * @param querytype
	 * @param cyear
	 * @param cmonth
	 * @param userpriv
	 * @param havepriv
	 * @param season
	 * @param ctimes
	 * @param cdatestart
	 * @param cdateend
	 * @param disting_pt
	 * @param pageid
	 * @param encap
	 * @param papersize
	 * @param writer
	 * @param mc
	 * @param card
	 * @param rec
	 * @throws Exception
	 * @throws BadElementException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private void printGridsIsNotBrokenLineC(String infokind,int tabid, String nid, String dbname, 
			UserView userview, int querytype, int cyear, int cmonth, String userpriv, String havepriv, 
			int season, int ctimes, String cdatestart, String cdateend, String disting_pt, 
			int pageid, DataEncapsulation encap, float[] papersize, PdfWriter writer, 
			MadeFontsizeToCell mc, GetCardCellValue card, DynaBean rec,List rgrids,List setList) throws Exception, BadElementException, MalformedURLException, IOException {
		//显示各个单元个得开始
		//List rgrids=encap.getRgrid(tabid,Integer.parseInt(rec.get("pageid").toString()),conn);             //获得Grid各个cell的List的对象
		 RGridView rgrid;
		 float topn;                                                  //单元格的上边位置
		 float leftn;                                                 //单元格的左边位置
		 float heights;                                               //单元格的高
		 float widthn;                                                //单元格的宽
		 int fontsize;
		 String hz="";                                                //单元格的内容是说明信息
		 //List setList=encap.GetSetsPDF(tabid,conn);               //获得到整个Grid所有的子集名称
		if(!rgrids.isEmpty())
		{
			for(int i=0;i<rgrids.size();i++)
		    {
		    	 rgrid=(RGridView)rgrids.get(i);
		    	  if("C".equals(rgrid.getFlag())){
		    	 leftn=Float.parseFloat(rgrid.getRleft())*w_base*converPxTomm/mMtoItext+this.lmargin*w_base;
		         topn=papersize[1]-Float.parseFloat(rgrid.getRtop())*h_base*converPxTomm/mMtoItext-this.tmargin*h_base;;
		         widthn=Float.parseFloat(rgrid.getRwidth())*w_base*converPxTomm/mMtoItext;
		         heights=Float.parseFloat(rgrid.getRheight())*h_base*converPxTomm/mMtoItext;	    	
		         Paragraph paragraph=new Paragraph();
		         PdfPCell cell = new PdfPCell(paragraph);
		         setCellAlign(rgrid,cell); 
			     cell.setFixedHeight(heights);
			   
			                //获得适应单元格大小的字体大小
		                  fontsize=mc.ReDrawLitterRect((int)widthn,(int)heights,rgrid.getCHz(),Integer.parseInt(rgrid.getFontsize()),hz,disting_pt,rgrid.getField_type(),rgrid.getSlope());
		                  fontsize=fontsize-4;
		                  //fontsize=mc.getFitFontSize(Integer.parseInt(rgrid.getFontsize()), widthn,heights,rgrid.getCHz());
		                  //getFormulaValue()函数是格式化显示数据的函数
		                 // System.out.println("sss" + card.getFormulaValue(rgrid));
		                  Paragraph smallparagraph=new Paragraph(card.getFormulaValue(rgrid), FontFamilyType.getFont(rgrid.getFontName(),rgrid.getFonteffect(),fontsize,this.platform));
		                  paragraph.add(smallparagraph);	                   	
		    		
			     
		   		      PdfPTable table = new PdfPTable(1);	
		   		      table.setTotalWidth(widthn);
		   		      table.setLockedWidth(true);
		   		      table.addCell(cell);		          
		   		      table.writeSelectedRows(0, -1,leftn,topn,writer.getDirectContent());	
   	                    
		    	 }
		      }
			}
	}
	/**
	 * @param tabid
	 * @param nid
	 * @param dbname
	 * @param userview
	 * @param querytype
	 * @param cyear
	 * @param cmonth
	 * @param userpriv
	 * @param havepriv
	 * @param season
	 * @param ctimes
	 * @param cdatestart
	 * @param cdateend
	 * @param disting_pt
	 * @param pageid
	 * @param encap
	 * @param papersize
	 * @param writer
	 * @param mc
	 * @param card
	 * @param rec
	 * @throws Exception
	 * @throws BadElementException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private String fenlei_type="";
	private void printGridsIsNotBrokenLine(String infokind,int tabid, String nid, String dbname, UserView userview, 
			int querytype, int cyear, int cmonth, String userpriv, String havepriv, int season, int ctimes, 
			String cdatestart, String cdateend, String disting_pt, int pageid, DataEncapsulation encap,
			float[] papersize, PdfWriter writer, MadeFontsizeToCell mc, GetCardCellValue card,
			DynaBean rec,String fieldpurv,List rgrids,List setList) throws Exception, BadElementException, MalformedURLException, IOException {
		//显示各个单元个得开始
		 //List rgrids=encap.getRgrid(tabid,Integer.parseInt(rec.get("pageid").toString()),conn);             //获得Grid各个cell的List的对象
		 RGridView rgrid;
		 float topn;                                                  //单元格的上边位置
		 float leftn;                                                 //单元格的左边位置
		 float heights;                                               //单元格的高
		 float widthn;                                                //单元格的宽
		 int fontsize;
		 String hz="";                                                //单元格的内容是说明信息
		 //List setList=encap.GetSetsPDF(tabid,conn);               //获得到整个Grid所有的子集名称
		 ArrayList valueList=new ArrayList();
		 ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
		 if(infokind!=null&& "5".equals(infokind)&&this.plan_id!=null&&this.plan_id.length()>0)
	     {
	    	 StatisticPlan statisticPlan=new StatisticPlan(userview,conn);
	    	 alUsedFields=statisticPlan.khResultField(alUsedFields,this.plan_id);
	     }
		 this.fenlei_type=card.getOneFenleiYype(userview,dbname, nid, conn);
		if(!rgrids.isEmpty())
		{
			for(int i=0;i<rgrids.size();i++)
		    {
		    	 rgrid=(RGridView)rgrids.get(i);
		    	 leftn=Float.parseFloat(rgrid.getRleft())*w_base*converPxTomm/mMtoItext+this.lmargin*w_base;
		         topn=papersize[1]-Float.parseFloat(rgrid.getRtop())*h_base*converPxTomm/mMtoItext-this.tmargin*h_base;;
		         widthn=Float.parseFloat(rgrid.getRwidth())*w_base*converPxTomm/mMtoItext;
		         heights=Float.parseFloat(rgrid.getRheight())*h_base*converPxTomm/mMtoItext;  
		         Paragraph paragraph=new Paragraph();
		         valueList=new ArrayList();
		         //System.out.println(rgrid.getB()+"--"+rgrid.getT()+"---"+rgrid.getL()+"---"+rgrid.getR());
			     /*if("0".equals(rgrid.getB())||"0".equals(rgrid.getT())||"0".equals(rgrid.getL())||"0".equals(rgrid.getB()))
			      cell.cloneNonPositionParameters(encap.getRectangle(rgrid));*/
			     boolean subflag_b=false;
			     PdfPTable subTbale=null;			     
			     if("A".equals(rgrid.getFlag())&&!"1".equals(rgrid.getSubflag())){                  //A人员库
		                byte nFlag=0;                              //0表示人员库
		                subflag_b=false;
		                paragraph=createTableGridcell(infokind,nid, dbname, userview, querytype, cyear, cmonth, userpriv, havepriv, season, ctimes, cdatestart, cdateend, disting_pt, encap,  rgrid, topn, leftn, heights, widthn, mc, card, setList, paragraph, nFlag,fieldpurv);    	
		         }else if("A".equals(rgrid.getFlag())&& "1".equals(rgrid.getSubflag()))
		         {
		        	    byte nFlag=0; 
		        	    subTbale=viewSubclass(infokind, nid, dbname, userview, querytype, cyear,cmonth,userpriv, havepriv,season,  ctimes, disting_pt,  rgrid,  nFlag);
		        	    subflag_b=true;
		         }else if("B".equals(rgrid.getFlag())&&!"1".equals(rgrid.getSubflag())){         //B单位库
		            	byte nFlag=2;                              //2表示单位库
		            	paragraph=createTableGridcell(infokind,nid, dbname, userview, querytype, cyear, cmonth, userpriv, havepriv, season, ctimes, cdatestart, cdateend, disting_pt, encap,  rgrid, topn, leftn, heights, widthn, mc, card, setList, paragraph, nFlag,fieldpurv);    //固定坐标
		         }else if("B".equals(rgrid.getFlag())&& "1".equals(rgrid.getSubflag()))
		         {
		        	 byte nFlag=2; 
		        	 subTbale=viewSubclass(infokind, nid, dbname, userview, querytype, cyear,cmonth,userpriv, havepriv,season,  ctimes, disting_pt,  rgrid,  nFlag);
		        	 subflag_b=true;
		         }else if("K".equals(rgrid.getFlag())&&!"1".equals(rgrid.getSubflag())){         //岗位库
		            	byte nFlag=4;                              //2表示岗位库
		            	paragraph=createTableGridcell(infokind,nid, dbname, userview, querytype, cyear, cmonth, userpriv, havepriv, season, ctimes, cdatestart, cdateend, disting_pt, encap,  rgrid, topn, leftn, heights, widthn, mc, card, setList, paragraph, nFlag,fieldpurv);    //固定坐标
		         }else if("K".equals(rgrid.getFlag())&& "1".equals(rgrid.getSubflag()))
		         {
		        	 byte nFlag=4; 
		        	 subTbale=viewSubclass(infokind, nid, dbname, userview, querytype, cyear,cmonth,userpriv, havepriv,season,  ctimes, disting_pt,  rgrid,  nFlag);
		        	 subflag_b=true;
		         }if("Z".equals(rgrid.getFlag())&&!"1".equals(rgrid.getSubflag())){                  //A人员库
		                byte nFlag=6;                              //0表示人员库
		                subflag_b=false;
		                paragraph=createTableGridcell(infokind,nid, dbname, userview, querytype, cyear, cmonth, userpriv, havepriv, season, ctimes, cdatestart, cdateend, disting_pt, encap,  rgrid, topn, leftn, heights, widthn, mc, card, setList, paragraph, nFlag,fieldpurv);    	
		         }else if("Z".equals(rgrid.getFlag())&& "1".equals(rgrid.getSubflag()))
		         {
		        	    byte nFlag=6; 
		        	    subTbale=viewSubclass(infokind, nid, dbname, userview, querytype, cyear,cmonth,userpriv, havepriv,season,  ctimes, disting_pt,  rgrid,  nFlag);
		        	    subflag_b=true;
		         }else if("J".equals(rgrid.getFlag())){         //计划库
		            	byte nFlag=5;                              //5表示计划库
		            	if(!"1".equals(rgrid.getSubflag())) {
                            paragraph=createTableGridcell(infokind,nid, dbname, userview, querytype, cyear, cmonth, userpriv, havepriv, season, ctimes, cdatestart, cdateend, disting_pt, encap,  rgrid, topn, leftn, heights, widthn, mc, card, setList, paragraph, nFlag,fieldpurv);    //固定坐标
                        } else
		            	{
		            		rgrid.setPlan_id(this.plan_id);
		            		subTbale=viewSubclass(infokind, nid, dbname, userview, querytype, cyear,cmonth,userpriv, havepriv,season,  ctimes, disting_pt,  rgrid,  nFlag);
				        	subflag_b=true;
		            	}
		         }else if("E".equals(rgrid.getFlag())){  // 基准岗位
		            	byte nFlag=7;                             
		            	if(!"1".equals(rgrid.getSubflag())) {
                            paragraph=createTableGridcell(infokind,nid, dbname, userview, querytype, cyear, cmonth, userpriv, havepriv, season, ctimes, cdatestart, cdateend, disting_pt, encap,  rgrid, topn, leftn, heights, widthn, mc, card, setList, paragraph, nFlag,fieldpurv);    //固定坐标
                        } else
		            	{
		            		rgrid.setPlan_id(this.plan_id);
		            		subTbale=viewSubclass(infokind, nid, dbname, userview, querytype, cyear,cmonth,userpriv, havepriv,season,  ctimes, disting_pt,  rgrid,  nFlag);
				        	subflag_b=true;
		            	}
		         }
		         else if("P".equals(rgrid.getFlag())){         //p表示照片
		        	 	String tempName=encap.createPhotoFile(dbname+"A00",nid,"P",conn);
		            	String lowtemp=userview.getUserName()+"_"+tempName;
		            	if(tempName!=null && tempName.length()>0)
		            	{
		            		Image image = null;
		            		File imageFile=new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+tempName);
		            		File lowImFile=new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+lowtemp);
		            		if(imageFile.exists()&&(imageFile.length()>100*1024)){
		            			PhotoImgBo imageBo=new PhotoImgBo(this.conn);
		            			imageBo.compressJpegFile(imageFile, lowImFile,0.9995f);
		            			tempName=lowtemp;
		            		}
							if(tempName.toUpperCase().indexOf(".PNG")>-1||tempName.toUpperCase().indexOf(".JPEG")>-1||tempName.toUpperCase().indexOf(".JPG")>-1||tempName.toUpperCase().indexOf(".GIF")>-1/*||tempName.toUpperCase().indexOf(".BMP")>-1*/){
		            			java.awt.Image awtImage=Toolkit.getDefaultToolkit().createImage(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+tempName);
		            			image= Image.getInstance(awtImage,null);									
		            		}else {
                                image=Image.getInstance(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+tempName);
                            }
							image.scaleAbsolute(widthn-2,heights);
							//图片使用后删除图片
							if(imageFile.exists()) {
                                imageFile.delete();
                            }
							if(lowImFile.exists()) {
                                lowImFile.delete();
                            }
							PdfPCell pdfCell=new PdfPCell(image, false);
							pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
							pdfCell.setVerticalAlignment(Element.ALIGN_BOTTOM);									
							PdfPTable table = new PdfPTable(1);	
		               		table.setTotalWidth(widthn);
		               		table.setLockedWidth(true);
		               		table.addCell(pdfCell);		          
		               		table.writeSelectedRows(0, -1,leftn,topn,writer.getDirectContent());
		               		PdfContentByte cb = writer.getDirectContent();
		         		    cb.setLineWidth(1f);
		         		    cb.moveTo(leftn,topn);
		         		    cb.lineTo(leftn + widthn,topn);
		         		    cb.setColorStroke(new Color(0,0,0));
		         		    cb.stroke();
		           		}else
		           		{
		           			//加一个没有照片的提示照片
		           			//Image image = Image.getInstance(System.getProperty("java.io.tmpdir")+"\\"+tempName);									
							//image.scaleAbsolute(widthn,heights);
							PdfPCell pdfCell=new PdfPCell();							
							pdfCell.setFixedHeight(heights);
							pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
							pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);									
							PdfPTable table = new PdfPTable(1);	
		               		table.setTotalWidth(widthn);
		               		table.setLockedWidth(true);
		               		table.addCell(pdfCell);		          
		               		table.writeSelectedRows(0, -1,leftn,topn,writer.getDirectContent());	
		           		}
		            }else if("H".equals(rgrid.getFlag())){         //H表示文字说明
		       	             hz=rgrid.getCHz();
		       	             //leftn=Float.parseFloat(rgrid.getRleft());
		                     if(hz !=null && hz.length()>0){
		                    	// fontsize=mc.getFitFontSize(Integer.parseInt(rgrid.getFontsize()), widthn,heights,rgrid.getCHz());
		                        //fontsize=mc.ReDrawLitterRect((int)widthn,(int)heights ,rgrid.getCHz(),Integer.parseInt(rgrid.getFontsize()),hz,disting_pt,rgrid.getField_type(),rgrid.getSlope());
		                    	valueList.add(rgrid.getCHz());
		                    	fontsize=mc.RePDFDrawLitterRect(widthn,heights, valueList, Integer.parseInt(rgrid.getFontsize()));
		                    	fontsize=fontsize-4;
		                    	/*if(fontsize>4)
		                    	   fontsize=fontsize-2;*/
		                        int last_s=hz.lastIndexOf("`");
		                        if(last_s==(hz.length()-1)) {
                                    hz=hz.substring(0,hz.length()-1);
                                }
		                        String[] a_stok=hz.split("`");		                        
		                        if(a_stok!=null&&a_stok.length>0)
		                        {
		                        	StringBuffer str=new StringBuffer();
		                        	for(int s=0;s<a_stok.length;s++)
		                        	{
		                        		str=new StringBuffer();
		                        		String tt=a_stok[s].replaceAll(" ", "\u0020\u0020");
		                        		tt=tt.replaceAll("　", "\u0020\u0020");
		                        		//System.out.println(tt);                		
		                        		str.append(tt);
		                        		Paragraph smallparagraph=new Paragraph(str.toString(), FontFamilyType.getFont(rgrid.getFontName(),rgrid.getFonteffect(),fontsize,this.platform));
		                        		
		                        		paragraph.add(smallparagraph);
		                        	}
		                        }else
		                        {
		                        	Paragraph smallparagraph=new Paragraph("", FontFamilyType.getFont(rgrid.getFontName(),rgrid.getFonteffect(),fontsize,this.platform));
		                        	paragraph.add(smallparagraph);
		                        }
		                        /*StringTokenizer Stok=new StringTokenizer(hz,"`");		                        
		                        for(;Stok.hasMoreTokens();)
		                        {
		                        	String str=Stok.nextToken();
		                        	System.out.println(str);
		                        	Paragraph smallparagraph=new Paragraph(str, FontFamilyType.getFont(rgrid.getFontName(),rgrid.getFonteffect(),fontsize));
		                        	paragraph.add(smallparagraph);	                       
		                        }*/		  
		                        
		                    }
		              
			        }else if("C".equals(rgrid.getFlag())){
		                /*  //获得适应单元格大小的字体大小
		                  fontsize=mc.ReDrawLitterRect((int)widthn,(int)heights,rgrid.getCHz(),Integer.parseInt(rgrid.getFontsize()),hz,disting_pt);
		   	              //getFormulaValue()函数是格式化显示数据的函数
		                  //System.out.println("sss" + card.getFormulaValue(rgrid));
		                  Paragraph smallparagraph=new Paragraph(card.getFormulaValue(rgrid), FontFamilyType.getFont(rgrid.getFontName(),rgrid.getFonteffect(),fontsize));
		                  paragraph.add(smallparagraph);	 */                  	
		    		} if("D".equals(rgrid.getFlag())){
		    			byte nFlag=0;   
		    			valueList=card.getTextValueForCexpress(dbname, conn, card, rgrid, userview,alUsedFields,infokind,nid,this.plan_id);
	                	paragraph=createTableGridcellForValueList(valueList,infokind, rgrid, topn, leftn, heights, widthn, mc, card, setList, paragraph, nFlag,fieldpurv);
		    		}
			        else{	
			        	if(widthn<1) {
                            continue;
                        }
		            }
			       PdfPCell cell = new PdfPCell(paragraph);			       
		           setCellAlign(rgrid,cell); 		          
			       cell.setFixedHeight(heights);
			       if(!"P".equals(rgrid.getFlag()))
			       {
			    	  if("H".equals(rgrid.getFlag()))
			    	  {
			    		  PdfPTable table = new PdfPTable(1);	
			   		      table.setTotalWidth(widthn);
			   		      table.setLockedWidth(true);
			   		      table.addCell(cell);	
			   		      table.getDefaultCell().setPadding(0);
			   		     //table.getDefaultCell().setBorder(0);
			 	          table.setHorizontalAlignment(0);
			 	          table.setSpacingBefore(0);
			   		      table.writeSelectedRows(0, -1,leftn,topn,writer.getDirectContent());	
			    	  }else if(!subflag_b)
			    	  {
			    		  PdfPTable table = new PdfPTable(1);	
			   		      table.setTotalWidth(widthn);
			   		      table.setLockedWidth(true);
			   		      table.addCell(cell);	
			   		      table.getDefaultCell().setPadding(0);
			   		     //table.getDefaultCell().setBorder(0);
			 	          table.setHorizontalAlignment(0);
			 	          table.setSpacingBefore(0);
			   		      table.writeSelectedRows(0, -1,leftn,topn,writer.getDirectContent());	 
			    	  }else
			    	  {
			    		  if(subTbale!=null)
			   			   {
			   				 PdfPTable table = new PdfPTable(1);	
				   		     table.setTotalWidth(widthn);
				   		     table.setLockedWidth(true);
				   		     table.getDefaultCell().setPadding(0);
				   		     //table.getDefaultCell().setBorder(0);
				 	         table.setHorizontalAlignment(0);
				 	         table.setSpacingBefore(0);		 	         
				   		     table.addCell(subTbale);
				   		     table.writeSelectedRows(0, -1,leftn,topn,writer.getDirectContent());
			   			   }else
			   			   {
			   				  PdfPTable table = new PdfPTable(1);	
				   		      table.setTotalWidth(widthn);
				   		      table.setLockedWidth(true);
				   		      table.addCell(cell);		   		   
				   		      table.writeSelectedRows(0, -1,leftn,topn,writer.getDirectContent());	
			   			   } 
			    	  }		   		      
		   		   }   	                    
		    	 }
			}
	}

	/**
	 * @param nid
	 * @param dbname
	 * @param userview
	 * @param querytype
	 * @param cyear
	 * @param cmonth
	 * @param userpriv
	 * @param havepriv
	 * @param season
	 * @param ctimes
	 * @param cdatestart
	 * @param cdateend
	 * @param disting_pt
	 * @param encap
	 * @param writer
	 * @param rgrid
	 * @param topn
	 * @param leftn
	 * @param heights
	 * @param widthn
	 * @param mc
	 * @param card
	 * @param setList
	 * @param paragraph
	 * @param nFlag
	 * @throws Exception
	 */
	private Paragraph createTableGridcell(String infokind,String nid, String dbname, UserView userview, int querytype, int cyear, int cmonth, String userpriv, String havepriv, int season, int ctimes, String cdatestart, String cdateend, String disting_pt, DataEncapsulation encap,  RGridView rgrid, float topn, float leftn, float heights, float widthn, MadeFontsizeToCell mc, GetCardCellValue card, List setList, Paragraph paragraph, byte nFlag,String fieldpurv) throws Exception {
		int fontsize;
		//String fontStr;
		ArrayList valueList=null;			
		if(!"5".equals(infokind))
		{
			if("Z03".equalsIgnoreCase(rgrid.getCSetName()))
			{
				valueList = getGridCellValueList(infokind,nid, dbname, userview, querytype, cyear, cmonth, userpriv, havepriv, season, ctimes, cdatestart, cdateend, rgrid, card, nFlag, valueList, null,fieldpurv);
			}else if("1".equals(rgrid.getIsView()))
			{
				valueList = getGridCellValueList(infokind,nid, dbname, userview, querytype, cyear, cmonth, userpriv, havepriv, season, ctimes, cdatestart, cdateend, rgrid, card, nFlag, valueList, null,fieldpurv);
			}else if(!setList.isEmpty()){
			    for(int j=0;j<setList.size();j++)
			    {
			       LazyDynaBean fieldset=(LazyDynaBean)setList.get(j);
			       if(fieldset.get("fieldsetid").equals(rgrid.getCSetName())){
			         valueList = getGridCellValueList(infokind,nid, dbname, userview, querytype, cyear, cmonth, userpriv, havepriv, season, ctimes, cdatestart, cdateend, rgrid, card, nFlag, valueList, fieldset,fieldpurv);
			   	     break;
			       }
			    } 
			 }	
		}else
		{
			valueList = getGridCellValueList(infokind,nid, dbname, userview, querytype, cyear, cmonth, userpriv, havepriv, season, ctimes, cdatestart, cdateend, rgrid, card, nFlag, valueList, null,fieldpurv);
		}	
	
		 if(valueList !=null &&!valueList.isEmpty()){		    
		    String fontsizStr="";
            /*for(int j=0;j<valueList.size();j++)
            {
            	   fontsizStr+=valueList.get(j)!=null?valueList.get(j).toString():"";
            	   fontsizStr=fontsizStr+"`";
            }
           fontsizStr=fontsizStr.replaceAll("\r\n", "`") ;  
		   fontsize=mc.ReDrawLitterRect((int)widthn,(int)heights,fontsizStr,Integer.parseInt(rgrid.getFontsize())-1,rgrid.getFontName(),disting_pt,rgrid.getField_type(),rgrid.getSlope());//获得显示字体的大小
		   */
		   if(valueList.size()==1)
		   {
			   fontsize=mc.RePDFDrawLitterRect(widthn,heights, valueList, Integer.parseInt(rgrid.getFontsize()));		   
			   fontsize=fontsize-4;
			   /*if(fontsize>4)
	        	   fontsize=fontsize-3;*/
			   String value=valueList.get(0)!=null?valueList.get(0).toString():"";
			   value=com.hjsj.hrms.utils.HtmlRegexpUtil.filterHtml(value);
			   value=value.replaceAll("&nbsp;", "  ");
		       if("5".equals(infokind))
		       {
		    		  value=value.replaceAll("@#@","\r\n");     
               	  value=value.replaceAll("#@#","\r\n");   
		       }		    	
		       Paragraph smallparagraph=new Paragraph(value, FontFamilyType.getFont(rgrid.getFontName(),rgrid.getFonteffect(),fontsize,this.platform));
		       paragraph.add(smallparagraph);
		   }else
		   {
			   for(int j=0;j<valueList.size();j++)
			   {
				  int heigh=Integer.parseInt(rgrid.getFontsize())+Integer.parseInt(PubFunc.multiple(rgrid.getFontsize(),"0.72", 0));
				  String value = valueList.get(j)==null?"":valueList.get(j).toString();
				  if(value.length() == 0) {
                      value = " ";
                  }
				  //if(valueList.get(j)==null||valueList.get(j).toString().length()<=0)
				  //	  continue;
				  fontsize=mc.ReOneRowDrawLitterRect((int)widthn,heigh, value, Integer.parseInt(rgrid.getFontsize()));
				  fontsize=fontsize-4;
				  /*if(fontsize>4)
		        	   fontsize=fontsize-3;*/
//			      if(valueList.get(j)!=null && valueList.get(j).toString() !=null){	
//			    	  String value=valueList.get(j)!=null?valueList.get(j).toString():"";		    	  
			    	  value=com.hjsj.hrms.utils.HtmlRegexpUtil.filterHtml(value);
			    	  value=value.replaceAll("&nbsp;", " ");
			    	  if("5".equals(infokind))
			    	  {
			    		  value=value.replaceAll("@#@","\r\n");     
	                  	  value=value.replaceAll("#@#","\r\n");   
			    	  }		    	
			      	 Paragraph smallparagraph=new Paragraph(value, FontFamilyType.getFont(rgrid.getFontName(),rgrid.getFonteffect(),fontsize,this.platform));
//			      	 if(rgrid.getField_name().equals("A1915"))
//			      	     smallparagraph.setSpacingBefore(5);
			      	 paragraph.add(smallparagraph);
//			      }
			   }
		   }
		   
		  			                           
		} 
		 return paragraph;
	}
	
	
	private  Paragraph createTableGridcellForValueList(ArrayList valueList,String infokind,RGridView rgrid, float topn, float leftn, float heights, float widthn, MadeFontsizeToCell mc, GetCardCellValue card, List setList, Paragraph paragraph, byte nFlag,String fieldpurv )
	{
		int fontsize;
		if(valueList !=null &&!valueList.isEmpty()){		    
		    String fontsizStr="";          
		   if(valueList.size()==1)
		   {
			   fontsize=mc.RePDFDrawLitterRect(widthn,heights, valueList, Integer.parseInt(rgrid.getFontsize()));		   
			   /*if(fontsize>4)
	        	   fontsize=fontsize-3;*/
			   fontsize=fontsize-4;
			   String value=valueList.get(0)!=null?valueList.get(0).toString():"";		    	  
			   value=value.replace("<br>", "\r\n");//插入计算公式时 设置</br> 导出换行
			   value=value.replace("</br>", "\r\n");
		       if("5".equals(infokind))
		       {
		    		  value=value.replaceAll("@#@","\r\n");     
               	  value=value.replaceAll("#@#","\r\n");   
		       }		    	
		       Paragraph smallparagraph=new Paragraph(value, FontFamilyType.getFont(rgrid.getFontName(),rgrid.getFonteffect(),fontsize,this.platform));
		       paragraph.add(smallparagraph);
		   }else
		   {
			   for(int j=0;j<valueList.size();j++)
			   {
				  int heigh=Integer.parseInt(rgrid.getFontsize())+Integer.parseInt(PubFunc.multiple(rgrid.getFontsize(),"0.72", 0));;
				  if(valueList.get(j)==null||valueList.get(j).toString().length()<=0) {
                      continue;
                  }
				  fontsize=mc.ReOneRowDrawLitterRect((int)widthn,heigh, valueList.get(j).toString(), Integer.parseInt(rgrid.getFontsize()));		   
				  /*if(fontsize>4)
		        	   fontsize=fontsize-3;*/
				  fontsize=fontsize-4;
			      if(valueList.get(j)!=null && valueList.get(j).toString() !=null){	
			    	  String value=valueList.get(j)!=null?valueList.get(j).toString():"";		    	  
			    	  value=value.replace("<br>", "\r\n");
					   value=value.replace("</br>", "\r\n");
			    	  if("5".equals(infokind))
			    	  {
			    		  value=value.replaceAll("@#@","\r\n");     
	                  	  value=value.replaceAll("#@#","\r\n");   
			    	  }		    	
			      	 Paragraph smallparagraph=new Paragraph(value, FontFamilyType.getFont(rgrid.getFontName(),rgrid.getFonteffect(),fontsize,this.platform));
			      	 paragraph.add(smallparagraph);
			      }
			   }
		   }	                           
		} 
		 return paragraph;
	}
	/**
	 * @param rgrid
	 * @param cell
	 */
	private void setCellAlign(RGridView rgrid, PdfPCell cell) {
		int align=Integer.parseInt(rgrid.getAlign());
		/*  单元格内容的排列方式
		 * =0上左 =1上中  =2上右  =3下左  =4下中  =5下右 =6中左  =7中中 =8中右
		 */
		if(align==0)   
		{
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);    //基于最合适的
		}
		else if(align==1)
		{
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		}
		else if(align==2)
		{
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		}
		else if(align==3)
		{
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		}
		else if(align==4)
		{
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		}
		else if(align==5)
		{
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		}
		else if(align==6)
		{
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		}
		else if(align==7)
		{
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);   //居中
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		}
		else if(align==8)
		{
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);   //居右
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		}else
		{
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);   //居右
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		}
	}
	/**
	 * @param nid
	 * @param dbname
	 * @param userview
	 * @param querytype
	 * @param cyear
	 * @param cmonth
	 * @param userpriv
	 * @param havepriv
	 * @param season
	 * @param ctimes
	 * @param cdatestart
	 * @param cdateend
	 * @param rgrid
	 * @param card
	 * @param nFlag
	 * @param valueList
	 * @param fieldset
	 * @return
	 * @throws Exception
	 */
	private ArrayList getGridCellValueList(String infokind,String nid, String dbname, UserView userview, int querytype, int cyear, int cmonth, String userpriv, String havepriv, int season, int ctimes, String cdatestart, String cdateend, RGridView rgrid, GetCardCellValue card, byte nFlag, ArrayList valueList, LazyDynaBean fieldset,String fieldpurv) throws Exception {
		//获得单元格的内容值
		String changeflag="0";		
		if(fieldset!=null) {
            changeflag=fieldset.get("changeflag").toString();
        } else if ("1".equalsIgnoreCase(rgrid.getIsView())) {
		      changeflag = card.viewIsChangeflag(rgrid, this.conn);
		    }
		   if(querytype==0) {
               valueList=card.GetFldValuePDF(infokind,rgrid.getCSetName(),rgrid.getField_name(),nFlag,dbname,rgrid,querytype,Integer.parseInt(changeflag),cyear,cmonth,ctimes,nid,userview,userpriv,havepriv,cdatestart,cdateend,season,conn,fieldpurv);
           } else if(querytype==1)
		   {
			  if(infokind!=null&& "5".equals(infokind))
			  {
				   StatisticPlan statisticPlan=new StatisticPlan(userview,conn);
				   String table_name=statisticPlan.getPER_RESULT_TableName(this.plan_id);
				   rgrid.setCSetName(table_name);	
				   rgrid.setPlan_id(this.plan_id);				 
				   valueList=card.GetFldValuePDF(infokind,rgrid.getCSetName(),rgrid.getField_name(),nFlag,dbname,rgrid,querytype,0,cyear,cmonth,ctimes,nid,userview,userpriv,havepriv,cdatestart,cdateend,season,conn,fieldpurv);
			  }else
			  {
				  valueList=card.GetFldValuePDF(infokind,rgrid.getCSetName(),rgrid.getField_name(),nFlag,dbname,rgrid,querytype,Integer.parseInt(changeflag),cyear,cmonth,ctimes,nid,userview,userpriv,havepriv,cdatestart,cdateend,season,conn,fieldpurv); 
			  }   
		   }		     
		   else if(querytype==2) {
               valueList=card.GetFldValuePDF(infokind,rgrid.getCSetName(),rgrid.getField_name(),nFlag,dbname,rgrid,querytype,Integer.parseInt(changeflag),cyear,cmonth,ctimes,nid,userview,userpriv,havepriv,cdatestart,cdateend,season,conn,fieldpurv);
           } else if(querytype==3) {
               valueList=card.GetFldValuePDF(infokind,rgrid.getCSetName(),rgrid.getField_name(),nFlag,dbname,rgrid,querytype,Integer.parseInt(changeflag),cyear,cmonth,ctimes,nid,userview,userpriv,havepriv,cdatestart,cdateend,season,conn,fieldpurv);
           } else if(querytype==4) {
               valueList=card.GetFldValuePDF(infokind,rgrid.getCSetName(),rgrid.getField_name(),nFlag,dbname,rgrid,querytype,Integer.parseInt(changeflag),cyear,cmonth,ctimes,nid,userview,userpriv,havepriv,cdatestart,cdateend,season,conn,fieldpurv);
           }
		   return valueList;
	}

	/**
	 * @param tabid      登记表id
	 * @param pageid     页数
	 * @param nid        人员ID
	 * @param dbname     人员库
	 * @param encap      
	 * @param papersize  页面大小
	 * @param writer     写对象
	 */
	private void createPageTitle(int tabid,int pageid,String nid, String dbname, DataEncapsulation encap, float[] papersize, PdfWriter writer,String infokind,List rpageList) {
		int fontsize;
		String fontEffect;
		//List rpageList=encap.getRpage(tabid,pageid,conn); 
		//System.out.println("fasdfs"+ tabid + " ddd " + pageid);
		try{
		   //生成页面标头等信息的开始
		   if(!rpageList.isEmpty()){
		     for(int i=0;i<rpageList.size();i++){
				RPageView rpage=(RPageView)rpageList.get(i);
		     	fontsize=Integer.parseInt(rpage.getFontsize());
		     	fontsize=fontsize-4;
		     	fontEffect=rpage.getFonteffect();	
		     	if(rpage.getFlag()!=6)
          	    {
//		     		Paragraph paragraph=new Paragraph("fdsfs",FontFamilyType.getFont(rpage.getFontname(),fontEffect,fontsize));
			     	//System.out.println("fasdfs"+ rpage.getFlag());
			     	//System.out.println(pageid+"-"+rpage.getFlag()+"-"+rpage.getHz()+"-"+nid+"-"+dbname+"-"+tabid+"-"+infokind+"-"+rpage.getExtendAttr()+"--"+fontEffect+"-"+fontsize);
			     	Paragraph paragraph=new Paragraph(encap.getPageTitle(pageid,rpage.getFlag(),rpage.getHz(),nid,dbname,tabid,infokind,rpage.getExtendAttr()),FontFamilyType.getFont(rpage.getFontname(),fontEffect,fontsize,this.platform));
			    	PdfPCell cell = new PdfPCell(paragraph);
			        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			        //cell.setFixedHeight(Float.parseFloat(rpage.getRheight()));
			        cell.setBorder(0);
			        cell.setNoWrap(true);
			       
			        PdfPTable table = new PdfPTable(1);	
			        table.setTotalWidth(Float.parseFloat(rpage.getRwidth())*converPxTomm/mMtoItext );
			        table.setLockedWidth(false);
			        table.addCell(cell);		          
			        table.writeSelectedRows(0, -1,Float.parseFloat(rpage.getRleft())*w_base*converPxTomm/mMtoItext+this.lmargin*w_base,papersize[1]-Float.parseFloat(rpage.getRtop())*h_base*converPxTomm/mMtoItext-this.tmargin*h_base,writer.getDirectContent());    //固定坐标	
          	    }else
          	    {
          	    	String extendattr=rpage.getExtendAttr();
        			if(extendattr!=null&&extendattr.length()>0)
        			{
        				String ext="";        				
        				float leftn=Float.parseFloat(rpage.getRleft("1"))*converPxTomm/mMtoItext+this.lmargin*w_base;
   		                float topn=Float.parseFloat(rpage.getRtop("1"))*h_base*converPxTomm/mMtoItext-this.tmargin*h_base;
//   		                float widthn=Float.parseFloat(rpage.getRwidth("1"))*w_base;
//   		                float heights=Float.parseFloat(rpage.getRheight("1")) *h_base;  
   		                float widthn=Float.parseFloat(rpage.getRwidth()/*rpage.getRwidth("1")*/)*w_base*converPxTomm/mMtoItext;//生成图片宽度直接取设置的宽度 不计算 20170105
   		                float heights=Float.parseFloat(rpage.getRheight()) *h_base*converPxTomm/mMtoItext;//生成图片高度直接取设置的宽度 不计算 changxy 20170105
        				if(extendattr.indexOf("<format>")!=-1&&extendattr.indexOf("</format>")!=-1)
        				{
        					ext=extendattr.substring(extendattr.indexOf("<ext>")+5,extendattr.indexOf("</ext>"));
        				}        				
        				String tempName=encap.createTitlePhotoFile(tabid,pageid,rpage.getGridno(),ext,conn);        				
              	    	if(tempName!=null && tempName.length()>0)
    	            	{
              	    		Image image = null;
							if(tempName.toUpperCase().indexOf(".JPG")>-1||tempName.toUpperCase().indexOf(".GIF")>-1){
		            			java.awt.Image awtImage=Toolkit.getDefaultToolkit().createImage(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+tempName);
		            			image= Image.getInstance(awtImage,null);									
		            		}else {
                                image= Image.getInstance(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+tempName);
                            }
    						image.scaleAbsolute(widthn-2,heights);
    						PdfPCell pdfCell=new PdfPCell(image, false);
    						pdfCell.setBorder(0); 				    	
    						pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    						pdfCell.setVerticalAlignment(Element.ALIGN_BOTTOM);									
    						PdfPTable table = new PdfPTable(1);	
    	               		table.setTotalWidth(widthn);
    	               		table.setLockedWidth(true);
    	               		table.addCell(pdfCell);	
    	               		table.writeSelectedRows(0, -1,Float.parseFloat(rpage.getRleft())*w_base*converPxTomm/mMtoItext+this.lmargin*w_base,papersize[1]-Float.parseFloat(rpage.getRtop())*h_base*converPxTomm/mMtoItext-this.tmargin*h_base,writer.getDirectContent());    	               		
    	               		PdfContentByte cb = writer.getDirectContent();
    	         		    cb.setLineWidth(1f);
    	         		    cb.moveTo(leftn,topn);
    	         		    cb.lineTo(leftn,topn);//leftn + widthn 导出图片下划线出现错位
    	         		    cb.setColorStroke(new Color(0,0,0));
    	         		    cb.stroke();    	         		   
    	           		}
        		     }       	    	
          	    }
		     	
		     }
		  } 
		 }catch(Exception e)
		 {
		    e.printStackTrace();	
		 }
	}

	/**
	 * @param encap
	 * @param tabid    登记表id
	 */
	private float[] getPaperSize(DataEncapsulation encap, int tabid,Connection conn) {
		List rnameList=encap.getRname(tabid,conn);
		double PaperH=0f;
		double PaperW=0f;
		float[] papersize=new float[2];
		if(!rnameList.isEmpty())
		{
			for(int i=0;i<rnameList.size();i++)
			{
				LazyDynaBean rec=(LazyDynaBean)rnameList.get(i);
				if("1".equals(rec.get("paperori")))
				{
					PaperH=Double.parseDouble(rec.get("paperh").toString())/mMtoItext+this.tmargin+this.bmargin;
					PaperW=Double.parseDouble(rec.get("paperw").toString())/mMtoItext+this.lmargin+this.rmargin;
				}else
				{
					PaperH=Double.parseDouble(rec.get("paperw").toString())/mMtoItext+this.tmargin+this.bmargin;
					PaperW=Double.parseDouble(rec.get("paperh").toString())/mMtoItext+this.lmargin+this.rmargin;
				}
				
			}			
		}
		papersize[1]=Integer.parseInt(encap.round(String.valueOf(PaperH),0));
		papersize[0]=Integer.parseInt(encap.round(String.valueOf(PaperW),0));
		return papersize;
	}
	public PdfPTable viewSubclass(String infokind,String nid, String dbname, UserView userview, int querytype, int cyear, int cmonth, String userpriv, String havepriv, int season, int ctimes,  String disting_pt, RGridView rgrid,  byte nFlag)
	{
		PdfPTable table = null;
		String sub_domain=rgrid.getSub_domain();
		if(sub_domain==null||sub_domain.length()<=0) {
            return null;
        }
		YkcardViewSubclass ykcardViewSubclass=new YkcardViewSubclass(conn,cyear,cmonth,ctimes,dbname,nid,userview,false);
		int fact_width=(int)(Float.parseFloat(rgrid.getRwidth())*converPxTomm/mMtoItext) + 1;
	    int fact_height=(int)(Float.parseFloat(rgrid.getRheight())*converPxTomm/mMtoItext) +1;	 
	    ykcardViewSubclass.setFenlei_type(this.fenlei_type);
		ykcardViewSubclass.setFact_width(fact_width);
		ykcardViewSubclass.setFact_height(fact_height);
		ykcardViewSubclass.setUserpriv(userpriv);
		ykcardViewSubclass.setNFlag(nFlag);
		ykcardViewSubclass.getXmlSubdomain(rgrid.getSub_domain(),rgrid);
		ykcardViewSubclass.setDisplay_zero(this.display_zero);
		table=ykcardViewSubclass.viewSubClassPdf(infokind,dbname,conn,userview,rgrid,disting_pt,nFlag,this.platform);
		return table;
	}
	/**
	 * 页边据
	 * @param conn
	 */
	private void getMargin(int tabid,Connection conn)
	{
		StringBuffer sql=new StringBuffer();
		sql.append("select Tmargin,bmargin,lmargin,rmargin  from rname where tabid='"+tabid+"'");		
		ContentDAO dao=new ContentDAO(conn);
		RowSet rs=null;
		double tm_f=0;
		double bm_f=0;
		double lm_f=0;
		double rm_f=0;
		try
		{
			
			rs=dao.search(sql.toString());
			if(rs.next())
			{
				tm_f=rs.getDouble("Tmargin");
				bm_f=rs.getDouble("bmargin");
				lm_f=rs.getDouble("lmargin");
				rm_f=rs.getDouble("rmargin");
				tm_f=tm_f/mMtoItext;
				bm_f=bm_f/mMtoItext;
				lm_f=lm_f/mMtoItext;
				rm_f=rm_f/mMtoItext;
				this.tmargin=(int)Math.round(tm_f);
				this.bmargin=(int)Math.round(bm_f);
				this.lmargin=(int)Math.round(lm_f);
				this.rmargin=(int)Math.round(rm_f);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
