package com.hjsj.hrms.businessobject.performance;

import com.hjsj.hrms.businessobject.competencymodal.PersonPostMatchingBo;
import com.hjsj.hrms.businessobject.competencymodal.personPostModal.PersonPostModalBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleGradeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;

import javax.imageio.ImageIO;
import javax.sql.RowSet;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *<p>Title:PerformanceAnalyseBo.java</p> 
 *<p>Description:结果分析</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 28, 2008</p> 
 *@author dengcan
 *@version 4.0
 */

public class PerformanceAnalyseBo 
{
	 private Connection conn = null;
	 private UserView userView = null;
	//以下两个变量是为了解决  按岗位素质模型测评 郭峰
	 private boolean isByModel = false;
	 private String object_id = "";
	 
	 public PerformanceAnalyseBo(Connection con)
	 {
		 this.conn=con;
	 }
	
	 public PerformanceAnalyseBo(Connection con,UserView _userView)
	 {
		 this.conn=con;
		 this.userView=_userView;
	 }
	 public PerformanceAnalyseBo(Connection con,UserView _userView,String plan_id,String object_id)
	 {
		 this.conn=con;
		 this.userView=_userView;
		 this.object_id = object_id;
		 initData(plan_id,object_id);
	 }
	 /**为isByModel赋值*/
	    public void initData(String plan_id,String object_id){
			try{
				RowSet rs = null;
			    ContentDAO dao = new ContentDAO(this.conn);
			    
			    boolean isModel = SingleGradeBo.getByModel(plan_id,this.conn);
		    	if(!isModel){
		    		this.isByModel = false;
		    		return;
		    	}
		    	boolean isHasPoint = SingleGradeBo.isHaveMatchByModel(object_id,this.conn);
				if(isModel && isHasPoint){
					this.isByModel = true;
				}
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			
	    }
	 //生成excel图表
	 public String getExcelName(String picName,ArrayList pointToNameList)
	 {
		 String fileName="perAnalyseChart_"+PubFunc.getStrg()+".xls";
		 FileOutputStream fileOut = null;
		 HSSFWorkbook workbook = new HSSFWorkbook();
		 HSSFSheet sheet = null;
		 try
		 {
			sheet = workbook.createSheet("Sheet1");
			
			
//			先把读进来的图片放到一个ByteArrayOutputStream中，以便产生ByteArray 
            BufferedImage bufferImg =null; 
	        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream(); 
	        String s = System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+picName;
	        bufferImg = ImageIO.read(new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+picName)); 
	        ImageIO.write(bufferImg,"jpg",byteArrayOut); 
	         
	       HSSFPatriarch patriarch = sheet.createDrawingPatriarch(); 
	       HSSFClientAnchor anchor = new HSSFClientAnchor(0,0,0,255,(short)1,1,(short)12,24);
	       anchor.setAnchorType(AnchorType.DONT_MOVE_DO_RESIZE);
	       patriarch.createPicture(anchor , workbook.addPicture(byteArrayOut.toByteArray(),HSSFWorkbook.PICTURE_TYPE_JPEG)); 		
		   
	       HSSFRow row=null;
	       HSSFCell csCell=null;HSSFCell csCell1=null;
	       int j=0;
	       short n=0;
	       for(int i=0;i<pointToNameList.size();i++)
	       {
	    	   CommonData data=(CommonData)pointToNameList.get(i);
	    	   if(i%4==0)
	    	   {
	    		   row = sheet.createRow(27+j);
	    		   j++;
	    		   n=0;
	    	   }
	    	   csCell =row.createCell(Short.parseShort(String.valueOf(i%4*2+1+n)));
	    	   csCell1 =row.createCell(Short.parseShort(String.valueOf(i%4*2+2+n)));
			   csCell.setCellValue(data.getDataValue());
			   csCell1.setCellValue(data.getDataName());
			   n++;
	       }
	       fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName);
		   workbook.write(fileOut);
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }finally{
			 PubFunc.closeResource(fileOut);
			 PubFunc.closeResource(workbook);
			}
		 return fileName;
	 }
	 	 
	 /**
	  * 根据考核对象取得考核计划
	  * @param objectid
	  * @return
	  */
	 public String getPlanByObject(String objectid,String busitype)
	 {
		String planid="";
		try
		{
			String sql="select per_plan.* from per_plan ,per_object where "
					  +" per_plan.plan_id=per_object.plan_id and per_plan.status=7 and per_object.object_id='"+objectid+"' ";
			
			if(busitype==null || busitype.trim().length()<=0) {
                busitype = "0";
            }
			 if(busitype!=null && busitype.trim().length()>0 && "0".equalsIgnoreCase(busitype)) {
                 sql += " and ( busitype is null or busitype='' or busitype = '" + busitype + "') ";
             } else if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype)) {
                 sql += " and busitype = '" + busitype + "' ";
             }
			
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet=dao.search(sql);
			if(rowSet.next())
			{
				planid=rowSet.getString("plan_id");
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return planid;
	 }
	 	 
	 /**
	  * 取得多指标趋势分析数据
	  * @param planIds
	  * @param a0100
	  * @return
	  */
	 public HashMap getMultiplePointAnalyse(String planIds,String a0100,ArrayList pointToNameList)
	 {
		 HashMap dataMap=new HashMap();
		 HashMap pointToNameMap2=new HashMap();
		 HashMap pointToNameMap=new HashMap();
		 if(a0100==null || planIds.length()==0) {
             return dataMap;
         }
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 RowSet rowSet2=null;
			 RowSet rowSet3=null;
			 RowSet rowSet=null;
			 String sql ="";
			 StringBuffer planBuf = new StringBuffer();
			 //求出不包括选中考核对象的计划 这些计划在图例中显示 但是在图标表不显示图
			 HashMap planidsMap = new HashMap();			 
			 sql="select * from per_object where plan_id in ("+planIds+") and object_id='"+a0100+"'";
			 rowSet=dao.search(sql);
			 while(rowSet.next())
			 {
				 String planid = rowSet.getString("plan_id");
				 planidsMap.put(planid, planid);
				 planBuf.append(","+planid);
			 }			 
			 
			 planIds=planIds.replaceAll(",","','");
//			 求所选计划(包含该考核对象的计划)对应的不同模板中的相同指标
			 if(planBuf.length()==0) {
                 return dataMap;
             }
			 sql = "select distinct template_id from per_plan where plan_id in ("+planBuf.substring(1)+")  ";
			 rowSet=dao.search(sql);			
			 int index=0;
			 HashMap commonPoints = new HashMap();
			 HashMap commonItems = new HashMap();
			 while(rowSet.next())
			 {
				 index++;
				 String template_id=rowSet.getString("template_id");
				 sql="select point_id from per_template_point where item_id in (select item_id from per_template_item where UPPER(template_id)='"+template_id.toUpperCase()+"')";
				 rowSet2=dao.search(sql);	
				 HashMap tempMap = new HashMap();
				 while(rowSet2.next())
				 {
					 String point_id = (String)rowSet2.getString(1).toUpperCase();
					 if(index==1) {
                         commonPoints.put(point_id, point_id);
                     } else
					 {
						 if(commonPoints.get(point_id)!=null) {
                             tempMap.put(point_id, point_id);
                         }
					 }
				 }
				 if(tempMap.size()>0) {
                     commonPoints=tempMap;
                 }
				 
				 sql="select item_id from per_template_item where UPPER(template_id)='"+template_id.toUpperCase()+"' and kind=2";
				 rowSet2=dao.search(sql);	
				 tempMap = new HashMap();
				 while(rowSet2.next())
				 {
					 String item_id = (String)rowSet2.getString(1).toUpperCase();
					 if(index==1) {
                         commonItems.put(item_id, item_id);
                     } else
					 {
						 if(commonItems.get(item_id)!=null) {
                             tempMap.put(item_id, item_id);
                         }
					 }
				 }
				 if(tempMap.size()>0) {
                     commonItems=tempMap;
                 }
			 }			 
			 
			 sql = "select plan_id,name,template_id from per_plan where plan_id in ('"+planIds+"')  order by plan_id";
			 rowSet=dao.search(sql);
			 while(rowSet.next())
			 {
				 ArrayList list=new ArrayList();
				 String plan_id=rowSet.getString("plan_id");
				 String name=rowSet.getString("name");
				 
				 if(planidsMap.get(plan_id)==null)
				 {					 
					 dataMap.put(name,list);
					 continue;
				 }
				 
				 String template_id=rowSet.getString("template_id");
				
				 sql="select phr.*,pp.pointname from  per_history_result phr,per_point pp "
					 	   +" where UPPER(phr.point_id)=UPPER(pp.point_id) and  phr.object_id='"+a0100+"' and phr.plan_id="+plan_id+" and phr.status=0 order by pp.point_id";
				 rowSet2=dao.search(sql);
				 int i=0;
				 while(rowSet2.next())
				 {
					 	  i++;
						 String pointID = (String)rowSet2.getString("point_id").toUpperCase();
						 //分析图横坐标只出现多个模板公共的指标
						 if(commonPoints.get(pointID)==null) {
                             continue;
                         }
						 
						 String pointname=rowSet2.getString("pointname");
						 pointToNameMap.put(pointID, pointname);
						 String score=rowSet2.getString("score")==null?"0":rowSet2.getString("score");
						 String temp="";
						 if(pointToNameMap2.get(pointID)!=null)
						 {
							 temp=(String)pointToNameMap2.get(pointID);
						 }
						 else
						 {
							 temp=pointID;
							 pointToNameMap2.put(pointID,temp);
							 pointToNameList.add(new CommonData(temp,pointname));
						 }
						 String categoryName = temp;
						 //5.0以上版本
						if(this.userView.getVersion()>=50)
						{
								categoryName =(String)pointToNameMap.get(categoryName);
								categoryName=this.warpRowStr(categoryName,6);
						}
						 list.add(new CommonData(score,categoryName));

				}

				 sql="select phr.*,pti.itemdesc from  per_history_result phr,per_template_item pti where phr.point_id=pti.item_id "
					+" and  phr.object_id='"+a0100+"'  and phr.plan_id="+plan_id+" and phr.status=1 and pti.kind=2";
				 rowSet2=dao.search(sql);
				 while(rowSet2.next())
				 {
					 	 i++;
						 String pointID = (String)rowSet2.getString("point_id").toUpperCase();
						 if(commonItems.get(pointID)==null) {
                             continue;
                         }
						 
						 String pointname=rowSet2.getString("itemdesc");
						 pointToNameMap.put(pointID, pointname);
						 String score=rowSet2.getString("score")==null?"0":rowSet2.getString("score");
						 String temp="";
						 if(pointToNameMap2.get(pointID)!=null)
						 {
							 temp=(String)pointToNameMap2.get(pointID);
						 }
						 else
						 {
							 temp=pointID;
							 pointToNameMap2.put(pointID,temp);
							 pointToNameList.add(new CommonData(temp,pointname));
						 }
						 String categoryName = temp;
						 //5.0以上版本
						if(this.userView.getVersion()>=50)
						{
								categoryName =(String)pointToNameMap.get(categoryName);
								categoryName=this.warpRowStr(categoryName,6);
						}
						 list.add(new CommonData(score,categoryName));

				} 
				 
				 
				 
				 if(i==0)
				 {
					 sql="select po.point_id,po.pointname from per_template_item pi,per_template_point pp,per_point po "
						 +" where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id='"+template_id+"' ";
					 rowSet3=dao.search(sql);
					 while(rowSet3.next())
					 {
						 String pointID = (String)rowSet3.getString("point_id").toUpperCase();
						 //分析图横坐标只出现多个模板公共的指标
						 if(commonPoints.get(pointID)==null) {
                             continue;
                         }
						 String pointname=rowSet3.getString("pointname");
						 pointToNameMap.put(pointID, pointname);
						 String temp="";
						 if(pointToNameMap2.get(pointID)!=null)
						 {
							 temp=(String)pointToNameMap2.get(pointID);
						 }
						 else
						 {
							 temp=pointID;
							 pointToNameMap2.put(pointID,temp);
							 pointToNameList.add(new CommonData(temp,pointname));
						 }
						 String categoryName = temp;
						 //5.0以上版本
						if(this.userView.getVersion()>=50)
						{
								categoryName =(String)pointToNameMap.get(categoryName);
								categoryName=this.warpRowStr(categoryName,6);
						}
						 list.add(new CommonData("0",categoryName));
					 }
					 
					 
					 sql="select item_id,itemdesc from per_template_item  where "
							+" template_id='"+template_id+"' and kind=2";
					 rowSet2=dao.search(sql);
						 while(rowSet2.next())
						 {  
							 	 i++;
								 String pointID = (String)rowSet2.getString("item_id").toUpperCase();
								 if(commonItems.get(pointID)==null) {
                                     continue;
                                 }
								 String pointname=rowSet2.getString("itemdesc");
								 pointToNameMap.put(pointID, pointname);
								 String temp="";
								 if(pointToNameMap2.get(pointID)!=null)
								 {
									 temp=(String)pointToNameMap2.get(pointID);
								 }
								 else
								 {
									 temp=pointID;
									 pointToNameMap2.put(pointID,temp);
									 pointToNameList.add(new CommonData(temp,pointname));
								 }
								 String categoryName = temp;
								 //5.0以上版本
								if(this.userView.getVersion()>=50)
								{
										categoryName =(String)pointToNameMap.get(categoryName);
										categoryName=this.warpRowStr(categoryName,6);
								}
								 list.add(new CommonData("0",categoryName));
						} 
					 
					 
					 
				 }
				dataMap.put(name,list);
			 }
			 if(rowSet2!=null) {
                 rowSet2.close();
             }
			 if(rowSet3!=null) {
                 rowSet3.close();
             }
			 if(rowSet!=null) {
                 rowSet.close();
             }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 
		 return dataMap; 
	 }
	 /**
	  * 取得多指标趋势分析数据  加入按岗位素质模型测评修改版
	  * @param planIds
	  * @param a0100
	  * @return
	  */
	 public HashMap getMultiplePointAnalyseModify(String planIds,String a0100,ArrayList pointToNameList)
	 {
		 HashMap dataMap=new HashMap();
		 HashMap pointToNameMap2=new HashMap();
		 HashMap pointToNameMap=new HashMap();
		 if(a0100==null || planIds.length()==0) {
             return dataMap;
         }
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 RowSet rowSet2=null;
			 RowSet rowSet3=null;
			 RowSet rowSet=null;
			 String sql ="";
			 String planidArray="";
			 StringBuffer planBuf = new StringBuffer();
			 HashMap planidsMap = new HashMap();			 
			 sql="select * from per_object where plan_id in ("+planIds+") and object_id='"+a0100+"'";
			 rowSet=dao.search(sql);
			 while(rowSet.next())
			 {
				 String planid = rowSet.getString("plan_id");
				 planidsMap.put(planid, planid);
				 planBuf.append(","+planid);
				 planidArray+=planid+",";
			 }			 
			 
			 planIds=planIds.replaceAll(",","','");
//			 求所选计划(包含该考核对象的计划)对应的不同模板中的相同指标
			 if(planBuf.length()==0) {
                 return dataMap;
             }
			 sql="";
			 String[] planid=planidArray.substring(0, planidArray.length()-1).split(",");
			 if(planid.length>0){//intersect交集
				 for(int i=0;i<planid.length;i++){
					 if(i==0){
						 sql="select point_id from per_history_result where plan_id='"+planid[i]+"' and object_id='"+a0100+"' and status='0'  ";
					 }else{
						 sql+="intersect select point_id from per_history_result where plan_id='"+planid[i]+"' and object_id='"+a0100+"' and status='0'  ";
					 }
					
				 }
			 }
			 rowSet=dao.search(sql);			
			 HashMap commonPoints = new HashMap();
			 String point_id="";
			 while(rowSet.next())
			 {
				 point_id=rowSet.getString("point_id");
				 commonPoints.put(point_id, point_id);
			 }			 
			 
			 sql = "select plan_id,name,template_id from per_plan where plan_id in ('"+planIds+"')  order by plan_id";
			 rowSet=dao.search(sql);
			 while(rowSet.next())
			 {
				 ArrayList list=new ArrayList();
				 String plan_id=rowSet.getString("plan_id");
				 String name=rowSet.getString("name");
				 
				 if(planidsMap.get(plan_id)==null)
				 {					 
					 dataMap.put(name,list);
					 continue;
				 }
				 
				 String template_id=rowSet.getString("template_id");
				
				 sql="select phr.*,pp.pointname from  per_history_result phr,per_point pp "
					 	   +" where UPPER(phr.point_id)=UPPER(pp.point_id) and  phr.object_id='"+a0100+"' and phr.plan_id="+plan_id+" and phr.status=0 order by pp.point_id";
				 rowSet2=dao.search(sql);
				 int i=0;
				 while(rowSet2.next())
				 {
					 	  i++;
						 String pointID = (String)rowSet2.getString("point_id").toUpperCase();
						 //分析图横坐标只出现多个模板公共的指标
						 if(commonPoints.get(pointID)==null) {
                             continue;
                         }
						 
						 String pointname=rowSet2.getString("pointname");
						 pointToNameMap.put(pointID, pointname);
						 String score=rowSet2.getString("score")==null?"0":rowSet2.getString("score");
						 String temp="";
						 if(pointToNameMap2.get(pointID)!=null)
						 {
							 temp=(String)pointToNameMap2.get(pointID);
						 }
						 else
						 {
							 temp=pointID;
							 pointToNameMap2.put(pointID,temp);
							 pointToNameList.add(new CommonData(temp,pointname));
						 }
						 String categoryName = temp;
						 //5.0以上版本
						if(this.userView.getVersion()>=50)
						{
								categoryName =(String)pointToNameMap.get(categoryName);
								categoryName=this.warpRowStr(categoryName,6);
						}
						 list.add(new CommonData(score,categoryName));

				}

				dataMap.put(name,list);
			 }
			 if(rowSet2!=null) {
                 rowSet2.close();
             }
			 if(rowSet3!=null) {
                 rowSet3.close();
             }
			 if(rowSet!=null) {
                 rowSet.close();
             }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 
		 return dataMap; 
	 }
	 
	 /**
      * 取得了解程度列表
      * 
      * @return
      */
	 public ArrayList getKnowList()
	 {
	
		ArrayList list = new ArrayList();
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    RowSet rowSet = dao.search("select * from per_know order by seq");
		    while (rowSet.next())
		    {
			LazyDynaBean abean = new LazyDynaBean();
			abean.set("know_id", rowSet.getString("know_id"));
			abean.set("name", rowSet.getString("name"));
			list.add(abean);
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return list;
	 }
	 
	 
	 /**
	  * 取得考核计划的主体类别列表
	  */
	 public ArrayList getMainbodySetList(String planid)
	 {
		 ArrayList list=new ArrayList();
		 if(planid.trim().length()==0) {
             return list;
         }
		 try
		 {
			 ContentDAO dao=new ContentDAO(this.conn);
			 
			 // 过滤掉只有确认权限(per_plan_body.opt=1)的主体 lium
			 StringBuffer sql = new StringBuffer();
			 sql.append(" SELECT pmb.name, pmb.body_id ");
			 sql.append(" FROM per_plan_body ppb, per_mainbodyset pmb ");
			 sql.append(" WHERE ppb.body_id=pmb.body_id AND "+Sql_switcher.isnull("ppb.opt", "0")+"<>1 AND ppb.plan_id=? ");
			 sql.append(" ORDER BY pmb.seq ");
			 
			 RowSet rowSet=dao.search(sql.toString(),Arrays.asList(new Object[] {
					 Integer.valueOf(planid)
			 }));
			 while (rowSet.next())
			 {
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("body_id", rowSet.getString("body_id"));
				abean.set("name", rowSet.getString("name"));
				list.add(abean);
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return list;
	 }
	 
	 // 取得选中考核对象名称
	 public HashMap getObjectNameMap(String plan_id,String str)
	 {		 
		 HashMap map = new HashMap();
		 try
		 {
			 if(str==null || str.trim().length()<=0) {
                 return map;
             }
			 ContentDAO dao = new ContentDAO(this.conn);
			 StringBuffer sql = new StringBuffer("");
			 sql.append("select object_id,a0101 from per_object where plan_id="+plan_id+" and object_id in ("+str.substring(1)+") ");			 
			 RowSet rowSet=dao.search(sql.toString());
			 while(rowSet.next())
			 {
				 map.put(rowSet.getString("object_id"), rowSet.getString("a0101"));
			 }
			 
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return map;
	 }
	 
	 /**
	  * 取得多考核对象 主体分类对比分析数据
	  * @param planid
	  * @param object_ids
	  * @return
	  */
	 public HashMap getMultipleMainbodyContrastData(String planid,String object_ids,String selectids,String isShowPercentVal,String chart_type)
	 {
		 HashMap dataMap=new HashMap();
		 if(planid.length()==0) {
             return dataMap;
         }
		 try
		 {
			 ArrayList mainbodySetList=getMainbodySetList(planid);
			 DbWizard dbWizard=new DbWizard(this.conn); 
			 ContentDAO dao=new ContentDAO(this.conn);
			 LazyDynaBean point_bean=null;
			 LazyDynaBean mainbody_bean=null;
			 StringBuffer str=new StringBuffer("");
			 String[] temps=object_ids.split(",");
			 for(int i=0;i<temps.length;i++)
			 {
				 if(temps[i].length()>0) {
                     str.append(",'"+temps[i].substring(3)+"'");
                 }
			 }
			// 取得选中考核对象名称
			 HashMap objectNameMap = getObjectNameMap(planid,str.toString());
			 
			 String status = "0";//0-分值模板	  1-权重模板
			 String sql = "select * from per_template where template_id = (select template_id from per_plan where plan_id="+planid+")";
			 RowSet rowSet=dao.search(sql);
			 if(rowSet.next()) {
                 status = rowSet.getString("status");
             }
			 
			 //取得指标的总分
			 HashMap map = new HashMap();
			 sql = "select * from per_template_point where item_id in (select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+"))";
			 rowSet=dao.search(sql);
			 while(rowSet.next())
			 {
				 String pointid = rowSet.getString("point_id");
				 float score = rowSet.getFloat("score");
				 map.put(pointid.toLowerCase(), new Float(score));
			 }
			 
			 float minRadarScore = 0;  //  所有线的最低值
			 float maxRadarScore = 0;  //  所有线的最高值
			 boolean flag = false;  //  0分值标识
			 
			 if(dbWizard.isExistTable("Per_ScoreDetail",false)&&str.length()>0)
			 {
				// RowSet rowSet=dao.search("select pos.*,po.a0101  from 	per_objectbody_score pos,per_object po  where pos.object_id=po.object_id and pos.plan_id="+planid+" and po.plan_id="+planid+" and pos.object_id in ("+str.substring(1)+") order by pos.body_id");
				  sql="select pos.*,po.a0101,"+planid+" plan_id  from (select object_id,body_id,sum(score*point_rank) score  from Per_ScoreDetail "
					       +" where plan_id="+planid+"  and object_id in ("+str.substring(1)+")  group by object_id,body_id ) pos,per_object po "
					       +" where pos.object_id=po.object_id  and po.plan_id="+planid+"  order by pos.body_id ";
				  rowSet=dao.search(sql);
				 HashMap existMap=new HashMap();
				 String abody_id="";
				 ArrayList tempList=new ArrayList();
				 ArrayList a0101List=new ArrayList();
				 
				 HashMap objBodyMap = new HashMap();
				 float minScore = 0;  //  单条线的最低值
				 float maxScore = 0;  //  单条线的最高值
				 int z=0;
				 while(rowSet.next())
				 {
					 String object_id=rowSet.getString("object_id");
					 String body_id=rowSet.getString("body_id");
					 String score = rowSet.getString("score")!=null?rowSet.getString("score"):"0";
					 if(z==0)
					 {
						 minScore = Float.parseFloat(score);
						 maxScore = Float.parseFloat(score);	
					 }						 								 	
					 if(minScore > Float.parseFloat(score)) {
                         minScore = Float.parseFloat(score);
                     }
					 if(maxScore < Float.parseFloat(score)) {
                         maxScore = Float.parseFloat(score);
                     }
					 z++;
					 
					 if(abody_id.length()==0) {
                         abody_id=body_id;
                     }
					 
					 if(!abody_id.equals(body_id))
					 {
						 for(int i=0;i<temps.length;i++)
						 {
							 if(temps[i].length()>0)
							 {
								 if(objBodyMap.get(temps[i].substring(3))==null) {
                                     tempList.add(new CommonData("0",(String)objectNameMap.get(temps[i].substring(3))));
                                 }
							 }
						 }
						 for(int i=0;i<mainbodySetList.size();i++)
						 {
							 mainbody_bean=(LazyDynaBean)mainbodySetList.get(i);
							 String bodyid=(String)mainbody_bean.get("body_id");
							 String name=(String)mainbody_bean.get("name");
							 if(bodyid.equalsIgnoreCase(abody_id))
							 {
								 if("null".equals(selectids)||(","+selectids+",").indexOf(","+bodyid+",")!=-1) {
                                     dataMap.put(name,tempList);
                                 }
								 existMap.put(bodyid,"1");
								 break;
							 }
						 }
						 abody_id=body_id;
						 tempList=new ArrayList();
						 objBodyMap = new HashMap();
					 }
					 String categoryName = rowSet.getString("a0101");
					 //5.0以上版本
					 if(this.userView.getVersion()>=50) {
                         categoryName = categoryName;//让横坐标竖写
                     }
					 tempList.add(new CommonData(score,categoryName));
					 objBodyMap.put(object_id, categoryName);
				 }
				 if(minRadarScore==0 || minRadarScore==0.0) {
                     minRadarScore = minScore;
                 } else
				 {
					 if(minRadarScore > minScore) {
                         minRadarScore = minScore;
                     }
				 }
				 if(maxRadarScore==0 || maxRadarScore==0.0) {
                     maxRadarScore = maxScore;
                 } else
				 {													 
					 if(maxRadarScore < maxScore) {
                         maxRadarScore = maxScore;
                     }
				 }
				 
				 for(int i=0;i<temps.length;i++)
				 {
					 if(temps[i].length()>0)
					 {
						 if(objBodyMap.get(temps[i].substring(3))==null) {
                             tempList.add(new CommonData("0",(String)objectNameMap.get(temps[i].substring(3))));
                         }
					 }
				 }
				 for(int i=0;i<mainbodySetList.size();i++)
				 {
					 mainbody_bean=(LazyDynaBean)mainbodySetList.get(i);
					 String bodyid=(String)mainbody_bean.get("body_id");
					 String name=(String)mainbody_bean.get("name");
					 if(bodyid.equalsIgnoreCase(abody_id))
					 {
						 if("null".equals(selectids)||(","+selectids+",").indexOf(","+bodyid+",")!=-1)
						 {
							 dataMap.put(name,tempList);
							 if(a0101List.size()==0)
							 {
								 for(int m=0;m<tempList.size();m++)
								 {
									 CommonData temp = (CommonData)tempList.get(m);
									 a0101List.add(temp.getDataName());
								 }
							 }
						 }
							 
						 existMap.put(bodyid,"1");
						 break;
					 }
				 }
				 //////////////////////////////
				 ArrayList objectList=new ArrayList();
				 rowSet=dao.search("select a0101 from per_result_"+planid+" where object_id in ("+str.substring(1)+")");
				 while(rowSet.next()) {
                     objectList.add(rowSet.getString("a0101"));
                 }
				 for(int i=0;i<mainbodySetList.size();i++)
				 {
					 mainbody_bean=(LazyDynaBean)mainbodySetList.get(i);
					 String body_id=(String)mainbody_bean.get("body_id");
					 String name=(String)mainbody_bean.get("name"); 
					 tempList=new ArrayList();
					 if(existMap.get(body_id)==null)
					 {						 
						 for(int j=0;j<objectList.size();j++)
						 {
							 String categoryName = (String)objectList.get(j);
							 //5.0以上版本
							 if(this.userView.getVersion()>=50) {
                                 categoryName = categoryName;//让横坐标竖写
                             }
							 tempList.add(new CommonData("0",categoryName));
						 }
							
						 if("null".equals(selectids)||(","+selectids+",").indexOf(","+body_id+",")!=-1)
						 {
							 flag = true; 
							 dataMap.put(name,tempList); 
						 }
					 }
				 }
				 ///////////////////////////////
				 //调整数据
				 HashMap tempMap = new HashMap();
				 Set keyset = dataMap.keySet();
				 for (Iterator iter = keyset.iterator(); iter.hasNext();)
				 {
					String bodyname = (String) iter.next();
					ArrayList list = (ArrayList)dataMap.get(bodyname);
					HashMap tempMap2 = new HashMap();
					for(int k=0;k<list.size();k++)
					{
						CommonData data = (CommonData)list.get(k);
						tempMap2.put(data.getDataName(), data);						
					}
				    list = new ArrayList();
					for(int m=0;m<objectList.size();m++)
					{
						String a0101 = (String)objectList.get(m);
						if(tempMap2.get(a0101)!=null) {
                            list.add(tempMap2.get(a0101));
                        }
					}
					tempMap.put(bodyname, list);
				}
				dataMap=tempMap;
				 
				 
				 ///////////////////////////////
				 rowSet=dao.search("select * from per_result_"+planid+" where  object_id in ("+str.substring(1)+")  ");
				 tempList=new ArrayList();
				 tempMap = new HashMap();
				 int y=0;
				 while(rowSet.next())
				 {
					 String categoryName = rowSet.getString("a0101");
					 String score = rowSet.getString("score")!=null?rowSet.getString("score"):"0";
					 if(y==0)
					 {
						 minScore = Float.parseFloat(score);
						 maxScore = Float.parseFloat(score);	
					 }						 								 	
					 if(minScore > Float.parseFloat(score)) {
                         minScore = Float.parseFloat(score);
                     }
					 if(maxScore < Float.parseFloat(score)) {
                         maxScore = Float.parseFloat(score);
                     }
					 y++;
					 
					 //5.0以上版本
					 if(this.userView.getVersion()>=50) {
                         categoryName = categoryName;//让横坐标竖写
                     }
//					tempList.add(new CommonData(rowSet.getString("score")!=null?PubFunc.round(rowSet.getString("score"),1):"0",categoryName));
					tempMap.put(categoryName, new CommonData(score,categoryName));
				 }
				 if(minRadarScore==0 || minRadarScore==0.0) {
                     minRadarScore = minScore;
                 } else
				 {
					 if(minRadarScore > minScore) {
                         minRadarScore = minScore;
                     }
				 }
				 if(maxRadarScore==0 || maxRadarScore==0.0) {
                     maxRadarScore = maxScore;
                 } else
				 {													 
					 if(maxRadarScore < maxScore) {
                         maxRadarScore = maxScore;
                     }
				 }
				 
				 if(tempMap.size()>0)
				 {
//					 if((selectids.equals("null")||(","+selectids+",").indexOf(",-1,")!=-1)&&a0101List.size()==0)
						 a0101List=objectList;//综合线显示所有人的值出来
					 for(int i=0;i<a0101List.size();i++)
					 {
						 String a0101 = (String)a0101List.get(i);
						 tempList.add(tempMap.get(a0101));
					 }
				 }
				 if("null".equals(selectids)||(","+selectids+",").indexOf(",-1,")!=-1) {
                     dataMap.put(ResourceFactory.getProperty("jx.param.zh"),tempList);
                 }
			 }
			 if(minRadarScore>=maxRadarScore || flag) {
                 minRadarScore=0;
             }
			 if(maxRadarScore==0 || maxRadarScore==0.0) {
                 maxRadarScore=5;
             }
			 if("41".equals(chart_type))
			 {
//				 dataMap.put("minmax",minRadarScore+","+maxRadarScore);
				 dataMap.put("minmax",0+","+maxRadarScore);
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return dataMap;
	 }
	 /**
	  * 取得多考核对象 主体分类对比分析数据  包含按岗位素质模型测评 但未定义岗位素质指标
	  * @param planid
	  * @param object_ids
	  * @return
	  */
	 public HashMap getMultipleMainbodyContrastDataModify(String planid,String object_ids,String selectids,String isShowPercentVal,String chart_type,String byModel)
	 {
		 HashMap dataMap=new HashMap();
		 if(planid.length()==0) {
             return dataMap;
         }
		 try
		 {
			 ArrayList mainbodySetList=getMainbodySetList(planid);
			 DbWizard dbWizard=new DbWizard(this.conn); 
			 ContentDAO dao=new ContentDAO(this.conn);
			 LazyDynaBean point_bean=null;
			 LazyDynaBean mainbody_bean=null;
			 StringBuffer str=new StringBuffer("");
			 String[] temps=object_ids.split(",");
			 for(int i=0;i<temps.length;i++)
			 {
				 if(temps[i].length()>0) {
                     str.append(",'"+temps[i].substring(3)+"'");
                 }
			 }
			// 取得选中考核对象名称
			 HashMap objectNameMap = getObjectNameMap(planid,str.toString());
			 
			 String status = "0";//0-分值模板	  1-权重模板
			 String sql = "select * from per_template where template_id = (select template_id from per_plan where plan_id="+planid+")";
			 RowSet rowSet=dao.search(sql);
			 if(rowSet.next()) {
                 status = rowSet.getString("status");
             }
			 
			 //取得指标的总分
			 HashMap map = new HashMap();
			 sql = "select * from per_template_point where item_id in (select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+"))";
			 rowSet=dao.search(sql);
			 while(rowSet.next())
			 {
				 String pointid = rowSet.getString("point_id");
				 float score = rowSet.getFloat("score");
				 map.put(pointid.toLowerCase(), new Float(score));
			 }
			 
			 float minRadarScore = 0;  //  所有线的最低值
			 float maxRadarScore = 0;  //  所有线的最高值
			 boolean flag = false;  //  0分值标识
			 
			 if(dbWizard.isExistTable("Per_ScoreDetail",false)&&str.length()>0)
			 {
				// RowSet rowSet=dao.search("select pos.*,po.a0101  from 	per_objectbody_score pos,per_object po  where pos.object_id=po.object_id and pos.plan_id="+planid+" and po.plan_id="+planid+" and pos.object_id in ("+str.substring(1)+") order by pos.body_id");
				  sql="select pos.*,po.a0101,"+planid+" plan_id  from (select object_id,body_id,sum(score*point_rank) score  from Per_ScoreDetail "
					       +" where plan_id="+planid+"  and object_id in ("+str.substring(1)+")  group by object_id,body_id ) pos,per_object po "
					       +" where pos.object_id=po.object_id  and po.plan_id="+planid+"  order by pos.body_id ";
				  rowSet=dao.search(sql);
				 HashMap existMap=new HashMap();
				 String abody_id="";
				 ArrayList tempList=new ArrayList();
				 ArrayList a0101List=new ArrayList();
				 
				 HashMap objBodyMap = new HashMap();
				 float minScore = 0;  //  单条线的最低值
				 float maxScore = 0;  //  单条线的最高值
				 int z=0;
				 while(rowSet.next())
				 {
					 String object_id=rowSet.getString("object_id");
					 String body_id=rowSet.getString("body_id");
					 String score = rowSet.getString("score")!=null?rowSet.getString("score"):"0";
					 if(z==0)
					 {
						 minScore = Float.parseFloat(score);
						 maxScore = Float.parseFloat(score);	
					 }						 								 	
					 if(minScore > Float.parseFloat(score)) {
                         minScore = Float.parseFloat(score);
                     }
					 if(maxScore < Float.parseFloat(score)) {
                         maxScore = Float.parseFloat(score);
                     }
					 z++;
					 
					 if(abody_id.length()==0) {
                         abody_id=body_id;
                     }
					 
					 if(!abody_id.equals(body_id))
					 {
						 for(int i=0;i<temps.length;i++)
						 {
							 if(temps[i].length()>0)
							 {
								 if(objBodyMap.get(temps[i].substring(3))==null) {
                                     tempList.add(new CommonData("0",(String)objectNameMap.get(temps[i].substring(3))));
                                 }
							 }
						 }
						 for(int i=0;i<mainbodySetList.size();i++)
						 {
							 mainbody_bean=(LazyDynaBean)mainbodySetList.get(i);
							 String bodyid=(String)mainbody_bean.get("body_id");
							 String name=(String)mainbody_bean.get("name");
							 if(bodyid.equalsIgnoreCase(abody_id))
							 {
								 if("null".equals(selectids)||(","+selectids+",").indexOf(","+bodyid+",")!=-1) {
                                     dataMap.put(name,tempList);
                                 }
								 existMap.put(bodyid,"1");
								 break;
							 }
						 }
						 abody_id=body_id;
						 tempList=new ArrayList();
						 objBodyMap = new HashMap();
					 }
					 String categoryName = rowSet.getString("a0101");
					 //5.0以上版本
					 if(this.userView.getVersion()>=50) {
                         categoryName = categoryName;//让横坐标竖写
                     }
					 tempList.add(new CommonData(score,categoryName));
					 objBodyMap.put(object_id, categoryName);
				 }
				 if(minRadarScore==0 || minRadarScore==0.0) {
                     minRadarScore = minScore;
                 } else
				 {
					 if(minRadarScore > minScore) {
                         minRadarScore = minScore;
                     }
				 }
				 if(maxRadarScore==0 || maxRadarScore==0.0) {
                     maxRadarScore = maxScore;
                 } else
				 {													 
					 if(maxRadarScore < maxScore) {
                         maxRadarScore = maxScore;
                     }
				 }
				 
				 for(int i=0;i<temps.length;i++)
				 {
					 if(temps[i].length()>0)
					 {
						 if(objBodyMap.get(temps[i].substring(3))==null) {
                             tempList.add(new CommonData("0",(String)objectNameMap.get(temps[i].substring(3))));
                         }
					 }
				 }
				 for(int i=0;i<mainbodySetList.size();i++)
				 {
					 mainbody_bean=(LazyDynaBean)mainbodySetList.get(i);
					 String bodyid=(String)mainbody_bean.get("body_id");
					 String name=(String)mainbody_bean.get("name");
					 if(bodyid.equalsIgnoreCase(abody_id))
					 {
						 if("null".equals(selectids)||(","+selectids+",").indexOf(","+bodyid+",")!=-1)
						 {
							 dataMap.put(name,tempList);
							 if(a0101List.size()==0)
							 {
								 for(int m=0;m<tempList.size();m++)
								 {
									 CommonData temp = (CommonData)tempList.get(m);
									 a0101List.add(temp.getDataName());
								 }
							 }
						 }
							 
						 existMap.put(bodyid,"1");
						 break;
					 }
				 }
				 //////////////////////////////
				 ArrayList objectList=new ArrayList();
				 rowSet=dao.search("select a0101,e01a1 from per_result_"+planid+" where object_id in ("+str.substring(1)+") order by e01a1");//zzk 是分析数据与组织架构人员顺序一致
				 while(rowSet.next()) {
                     objectList.add(rowSet.getString("a0101"));
                 }
				 for(int i=0;i<mainbodySetList.size();i++)
				 {
					 mainbody_bean=(LazyDynaBean)mainbodySetList.get(i);
					 String body_id=(String)mainbody_bean.get("body_id");
					 String name=(String)mainbody_bean.get("name"); 
					 tempList=new ArrayList();
					 if(existMap.get(body_id)==null)
					 {						 
						 for(int j=0;j<objectList.size();j++)
						 {
							 String categoryName = (String)objectList.get(j);
							 //5.0以上版本
							 if(this.userView.getVersion()>=50) {
                                 categoryName = categoryName;//让横坐标竖写
                             }
							 tempList.add(new CommonData("0",categoryName));
						 }
							
						 if("null".equals(selectids)||(","+selectids+",").indexOf(","+body_id+",")!=-1)
						 {
							 flag = true; 
							 dataMap.put(name,tempList); 
						 }
					 }
				 }
				 ///////////////////////////////
				 //调整数据
				 HashMap tempMap = new HashMap();
				 Set keyset = dataMap.keySet();
				 for (Iterator iter = keyset.iterator(); iter.hasNext();)
				 {
					String bodyname = (String) iter.next();
					ArrayList list = (ArrayList)dataMap.get(bodyname);
					HashMap tempMap2 = new HashMap();
					for(int k=0;k<list.size();k++)
					{
						CommonData data = (CommonData)list.get(k);
						tempMap2.put(data.getDataName(), data);						
					}
				    list = new ArrayList();
					for(int m=0;m<objectList.size();m++)
					{
						String a0101 = (String)objectList.get(m);
						if(tempMap2.get(a0101)!=null) {
                            list.add(tempMap2.get(a0101));
                        }
					}
					tempMap.put(bodyname, list);
				}
				dataMap=tempMap;
				 
				 
				 ///////////////////////////////
				 tempList=new ArrayList();
				 tempMap = new HashMap();
				 int y=0;
				if(byModel!=null&& "1".equals(byModel)){
					 rowSet=dao.search("select * from  per_history_result where plan_id='"+planid+"' and object_id in ("+str.substring(1)+")  and status='2' ");
				}else{
					 rowSet=dao.search("select * from per_result_"+planid+" where  object_id in ("+str.substring(1)+")  ");
				}
				 while(rowSet.next())
				 {
					 String categoryName = rowSet.getString("a0101");
					 String score = rowSet.getString("score")!=null?rowSet.getString("score"):"0";
					 if(y==0)
					 {
						 minScore = Float.parseFloat(score);
						 maxScore = Float.parseFloat(score);	
					 }						 								 	
					 if(minScore > Float.parseFloat(score)) {
                         minScore = Float.parseFloat(score);
                     }
					 if(maxScore < Float.parseFloat(score)) {
                         maxScore = Float.parseFloat(score);
                     }
					 y++;
					 
					 //5.0以上版本
					 if(this.userView.getVersion()>=50) {
                         categoryName = categoryName;//让横坐标竖写
                     }
//					tempList.add(new CommonData(rowSet.getString("score")!=null?PubFunc.round(rowSet.getString("score"),1):"0",categoryName));
					tempMap.put(categoryName, new CommonData(score,categoryName));
				 }
				 if(minRadarScore==0 || minRadarScore==0.0) {
                     minRadarScore = minScore;
                 } else
				 {
					 if(minRadarScore > minScore) {
                         minRadarScore = minScore;
                     }
				 }
				 if(maxRadarScore==0 || maxRadarScore==0.0) {
                     maxRadarScore = maxScore;
                 } else
				 {													 
					 if(maxRadarScore < maxScore) {
                         maxRadarScore = maxScore;
                     }
				 }
				 
				 if(tempMap.size()>0)
				 {
//					 if((selectids.equals("null")||(","+selectids+",").indexOf(",-1,")!=-1)&&a0101List.size()==0)
						 a0101List=objectList;//综合线显示所有人的值出来
					 for(int i=0;i<a0101List.size();i++)
					 {
						 String a0101 = (String)a0101List.get(i);
						 tempList.add(tempMap.get(a0101));
					 }
				 }
				 if("null".equals(selectids)||(","+selectids+",").indexOf(",-1,")!=-1) {
                     dataMap.put(ResourceFactory.getProperty("jx.param.zh"),tempList);
                 }
			 }
			 if(minRadarScore>=maxRadarScore || flag) {
                 minRadarScore=0;
             }
			 if(maxRadarScore==0 || maxRadarScore==0.0) {
                 maxRadarScore=5;
             }
			 if("41".equals(chart_type))
			 {
//				 dataMap.put("minmax",minRadarScore+","+maxRadarScore);
				 dataMap.put("minmax",0+","+maxRadarScore);
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return dataMap;
	 }
	 /**
	  * 按岗位素质模型测评 取得多考核对象 主体分类对比分析数据
	  * @param planid
	  * @param object_ids
	  * @return
	  */
	 public HashMap getMultipleMainbodyContrastDataByModel(String planid,String object_ids,String selectids,String isShowPercentVal,String chart_type)
	 {
		 HashMap dataMap=new HashMap();
		 if(planid.length()==0) {
             return dataMap;
         }
		 try
		 {
			 ArrayList mainbodySetList=getMainbodySetList(planid);
			 DbWizard dbWizard=new DbWizard(this.conn); 
			 ContentDAO dao=new ContentDAO(this.conn);
			 LazyDynaBean point_bean=null;
			 LazyDynaBean mainbody_bean=null;
			 StringBuffer str=new StringBuffer("");
			 String[] temps=object_ids.split(",");
			 for(int i=0;i<temps.length;i++)
			 {
				 if(temps[i].length()>0) {
                     str.append(",'"+temps[i].substring(3)+"'");
                 }
			 }
			// 取得选中考核对象名称
			 HashMap objectNameMap = getObjectNameMap(planid,str.toString());
			 
			// String status = "1";//0-分值模板	  1-权重模板
			 RowSet rowSet=null;
			 String sql="";
			 float minRadarScore = 0;  //  所有线的最低值
			 float maxRadarScore = 0;  //  所有线的最高值
			 boolean flag = false;  //  0分值标识
			 
			 if(dbWizard.isExistTable("Per_ScoreDetail",false)&&str.length()>0)
			 {
				// RowSet rowSet=dao.search("select pos.*,po.a0101  from 	per_objectbody_score pos,per_object po  where pos.object_id=po.object_id and pos.plan_id="+planid+" and po.plan_id="+planid+" and pos.object_id in ("+str.substring(1)+") order by pos.body_id");
				  sql="select pos.*,po.a0101,plan_id  from (select object_id,body_id,sum(score*point_rank) score  from Per_ScoreDetail "
					       +" where plan_id="+planid+"  and object_id in ("+str.substring(1)+")  group by object_id,body_id ) pos,per_object po "
					       +" where pos.object_id=po.object_id  and po.plan_id="+planid+"  order by pos.body_id ";
				 rowSet=dao.search(sql);
				 HashMap existMap=new HashMap();
				 String abody_id="";
				 ArrayList tempList=new ArrayList();
				 ArrayList a0101List=new ArrayList();
				 
				 HashMap objBodyMap = new HashMap();
				 float minScore = 0;  //  单条线的最低值
				 float maxScore = 0;  //  单条线的最高值
				 int z=0;
				 while(rowSet.next())
				 {
					 String object_id=rowSet.getString("object_id");
					 String body_id=rowSet.getString("body_id");
					 String score = rowSet.getString("score")!=null?rowSet.getString("score"):"0";
					 if(z==0)
					 {
						 minScore = Float.parseFloat(score);
						 maxScore = Float.parseFloat(score);	
					 }						 								 	
					 if(minScore > Float.parseFloat(score)) {
                         minScore = Float.parseFloat(score);
                     }
					 if(maxScore < Float.parseFloat(score)) {
                         maxScore = Float.parseFloat(score);
                     }
					 z++;
					 
					 if(abody_id.length()==0) {
                         abody_id=body_id;
                     }
					 
					 if(!abody_id.equals(body_id))
					 {
						 for(int i=0;i<temps.length;i++)
						 {
							 if(temps[i].length()>0)
							 {
								 if(objBodyMap.get(temps[i].substring(3))==null) {
                                     tempList.add(new CommonData("0",(String)objectNameMap.get(temps[i].substring(3))));
                                 }
							 }
						 }
						 for(int i=0;i<mainbodySetList.size();i++)
						 {
							 mainbody_bean=(LazyDynaBean)mainbodySetList.get(i);
							 String bodyid=(String)mainbody_bean.get("body_id");
							 String name=(String)mainbody_bean.get("name");
							 if(bodyid.equalsIgnoreCase(abody_id))
							 {
								 if("null".equals(selectids)||(","+selectids+",").indexOf(","+bodyid+",")!=-1) {
                                     dataMap.put(name,tempList);
                                 }
								 existMap.put(bodyid,"1");
								 break;
							 }
						 }
						 abody_id=body_id;
						 tempList=new ArrayList();
						 objBodyMap = new HashMap();
					 }
					 String categoryName = rowSet.getString("a0101");
					 //5.0以上版本
					 if(this.userView.getVersion()>=50) {
                         categoryName = categoryName;//让横坐标竖写
                     }
					 tempList.add(new CommonData(score,categoryName));
					 objBodyMap.put(object_id, categoryName);
				 }
				 if(minRadarScore==0 || minRadarScore==0.0) {
                     minRadarScore = minScore;
                 } else
				 {
					 if(minRadarScore > minScore) {
                         minRadarScore = minScore;
                     }
				 }
				 if(maxRadarScore==0 || maxRadarScore==0.0) {
                     maxRadarScore = maxScore;
                 } else
				 {													 
					 if(maxRadarScore < maxScore) {
                         maxRadarScore = maxScore;
                     }
				 }
				 
				 for(int i=0;i<temps.length;i++)
				 {
					 if(temps[i].length()>0)
					 {
						 if(objBodyMap.get(temps[i].substring(3))==null) {
                             tempList.add(new CommonData("0",(String)objectNameMap.get(temps[i].substring(3))));
                         }
					 }
				 }
				 for(int i=0;i<mainbodySetList.size();i++)
				 {
					 mainbody_bean=(LazyDynaBean)mainbodySetList.get(i);
					 String bodyid=(String)mainbody_bean.get("body_id");
					 String name=(String)mainbody_bean.get("name");
					 if(bodyid.equalsIgnoreCase(abody_id))
					 {
						 if("null".equals(selectids)||(","+selectids+",").indexOf(","+bodyid+",")!=-1)
						 {
							 dataMap.put(name,tempList);
							 if(a0101List.size()==0)
							 {
								 for(int m=0;m<tempList.size();m++)
								 {
									 CommonData temp = (CommonData)tempList.get(m);
									 a0101List.add(temp.getDataName());
								 }
							 }
						 }
							 
						 existMap.put(bodyid,"1");
						 break;
					 }
				 }
				 //////////////////////////////
				 ArrayList objectList=new ArrayList();
				 rowSet=dao.search("select distinct a0101,e01a1 from per_history_result  where plan_id='"+planid+"' and  object_id in ("+str.substring(1)+") order by e01a1");//zzk 是分析数据与组织架构人员顺序一致
				 while(rowSet.next()) {
                     objectList.add(rowSet.getString("a0101"));
                 }
				 for(int i=0;i<mainbodySetList.size();i++)
				 {
					 mainbody_bean=(LazyDynaBean)mainbodySetList.get(i);
					 String body_id=(String)mainbody_bean.get("body_id");
					 String name=(String)mainbody_bean.get("name"); 
					 tempList=new ArrayList();
					 if(existMap.get(body_id)==null)
					 {						 
						 for(int j=0;j<objectList.size();j++)
						 {
							 String categoryName = (String)objectList.get(j);
							 //5.0以上版本
							 if(this.userView.getVersion()>=50) {
                                 categoryName = categoryName;//让横坐标竖写
                             }
							 tempList.add(new CommonData("0",categoryName));
						 }
							
						 if("null".equals(selectids)||(","+selectids+",").indexOf(","+body_id+",")!=-1)
						 {
							 flag = true; 
							 dataMap.put(name,tempList); 
						 }
					 }
				 }
				 ///////////////////////////////
				 //调整数据
				 HashMap tempMap = new HashMap();
				 Set keyset = dataMap.keySet();
				 for (Iterator iter = keyset.iterator(); iter.hasNext();)
				 {
					String bodyname = (String) iter.next();
					ArrayList list = (ArrayList)dataMap.get(bodyname);
					HashMap tempMap2 = new HashMap();
					for(int k=0;k<list.size();k++)
					{
						CommonData data = (CommonData)list.get(k);
						tempMap2.put(data.getDataName(), data);						
					}
				    list = new ArrayList();
					for(int m=0;m<objectList.size();m++)
					{
						String a0101 = (String)objectList.get(m);
						if(tempMap2.get(a0101)!=null) {
                            list.add(tempMap2.get(a0101));
                        }
					}
					tempMap.put(bodyname, list);
				}
				dataMap=tempMap;
				 
				 
				 ///////////////////////////////
				 rowSet=dao.search("select * from  per_history_result where plan_id='"+planid+"' and object_id in ("+str.substring(1)+")  and status='2' ");
				 tempList=new ArrayList();
				 tempMap = new HashMap();
				 int y=0;
				 while(rowSet.next())
				 {
					 String categoryName = rowSet.getString("a0101");
					 String score = rowSet.getString("score")!=null?rowSet.getString("score"):"0";
					 if(y==0)
					 {
						 minScore = Float.parseFloat(score);
						 maxScore = Float.parseFloat(score);	
					 }						 								 	
					 if(minScore > Float.parseFloat(score)) {
                         minScore = Float.parseFloat(score);
                     }
					 if(maxScore < Float.parseFloat(score)) {
                         maxScore = Float.parseFloat(score);
                     }
					 y++;
					 
					 //5.0以上版本
					 if(this.userView.getVersion()>=50) {
                         categoryName = categoryName;//让横坐标竖写
                     }
//					tempList.add(new CommonData(rowSet.getString("score")!=null?PubFunc.round(rowSet.getString("score"),1):"0",categoryName));
					tempMap.put(categoryName, new CommonData(score,categoryName));
				 }
				 if(minRadarScore==0 || minRadarScore==0.0) {
                     minRadarScore = minScore;
                 } else
				 {
					 if(minRadarScore > minScore) {
                         minRadarScore = minScore;
                     }
				 }
				 if(maxRadarScore==0 || maxRadarScore==0.0) {
                     maxRadarScore = maxScore;
                 } else
				 {													 
					 if(maxRadarScore < maxScore) {
                         maxRadarScore = maxScore;
                     }
				 }
				 
				 if(tempMap.size()>0)
				 {
//					 if((selectids.equals("null")||(","+selectids+",").indexOf(",-1,")!=-1)&&a0101List.size()==0)
						 a0101List=objectList;//综合线显示所有人的值出来
					 for(int i=0;i<a0101List.size();i++)
					 {
						 String a0101 = (String)a0101List.get(i);
						 tempList.add(tempMap.get(a0101));
					 }
				 }
				 if("null".equals(selectids)||(","+selectids+",").indexOf(",-1,")!=-1) {
                     dataMap.put(ResourceFactory.getProperty("jx.param.zh"),tempList);
                 }
			 }
			 if(minRadarScore>=maxRadarScore || flag) {
                 minRadarScore=0;
             }
			 if(maxRadarScore==0 || maxRadarScore==0.0) {
                 maxRadarScore=5;
             }
			 if("41".equals(chart_type))
			 {
//				 dataMap.put("minmax",minRadarScore+","+maxRadarScore);
				 dataMap.put("minmax",0+","+maxRadarScore);
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return dataMap;
	 }
	 /**
	  * 取得单考核对象 主体分类对比分析数据  郭峰
	  * @param planid
	  * @param object_ids
	  * @return
	  */
	 public HashMap getSingleMainbodyContrastData(String planid,String object_id,String selectids,String isShowPercentVal,String chart_type)
	 {
		 HashMap dataMap=new HashMap();//body_name和list数据的映射
		 if(planid.length()==0) {
             return dataMap;
         }
		 try
		 {
			 ArrayList mainbodySetList=getMainbodySetList(planid);//所有的主体类别
			 DbWizard dbWizard=new DbWizard(this.conn);
			 ContentDAO dao=new ContentDAO(this.conn);
			 RowSet rowSet = null;
			 LazyDynaBean mainbody_bean=null;
			 
			 String sql ="";
			 String status = "0";//0-分值模板	  1-权重模板  分值模板可能要按百分比显示
			 if(!isByModel){//如果不是按岗位素质模型测评
				 sql = "select * from per_template where template_id = (select template_id from per_plan where plan_id="+planid+")";
				 rowSet=dao.search(sql);
				 if(rowSet.next()) {
                     status = rowSet.getString("status");//分值模板还是权重模板
                 }
			 }
			 
			 //取得指标和标准分值的对应关系     分值模板可能要按百分比显示
			 HashMap map = new HashMap();
			 if(!isByModel){//如果不是按岗位素质模型测评
				 sql = "select * from per_template_point where item_id in (select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+"))";
				 rowSet=dao.search(sql);
				 while(rowSet.next())
				 {
					 String pointid = rowSet.getString("point_id");
					 float score = rowSet.getFloat("score");
					 map.put(pointid.toLowerCase(), new Float(score));
				 }
			 }
			 
			 //查出所有的类别（综合除外。因为综合要在后面单独处理）
			 HashMap bodyMap = new HashMap();
			 for(int i=0;i<mainbodySetList.size();i++)
			 {
				 mainbody_bean=(LazyDynaBean)mainbodySetList.get(i);
				 String bodyid=(String)mainbody_bean.get("body_id");
				 String name=(String)mainbody_bean.get("name");				
			
				 if("null".equals(selectids)||(","+selectids+",").indexOf(","+bodyid+",")!=-1) {
                     bodyMap.put(bodyid, name);
                 }
			 }
			 
			 float minRadarScore = 0;  //  所有线的最低值  用于雷达图
			 float maxRadarScore = 0;  //  所有线的最高值
			 boolean flagScore = false;  //  0分值标识
			 
			 //开始处理数据
			 if(dbWizard.isExistTable("Per_ScoreDetail",false)&&object_id.length()>0)
			 {
				 //个性项目
				 String abody_id="";
				 ArrayList tempList=new ArrayList();//分数和项目名称的List
				 ArrayList itemList=new ArrayList();//存放map,map存放item的id和desc
				 HashMap itemDataMap = new HashMap();//body_id和templist的映射关系
				 HashMap itemDescMap = new HashMap();//body_name和templist的映射关系
				 if(!isByModel){//按岗位素质模型测评 全部当做共性指标
					 sql="select a.*,b.itemdesc from (select item_id,body_id,(sum(score*point_rank)/sum(point_rank)) score from Per_ScoreDetail where plan_id="+planid+" and object_id ='"+object_id;
					 sql+="' and item_id in (select item_id from per_template_item where template_id in (select template_id from per_plan where plan_id="+planid+") and kind=2) group by item_id,body_id ) a";
					 sql+=",per_template_item b where a.item_id=b.item_id order by a.body_id";
					 rowSet=dao.search(sql);
					 while(rowSet.next())
					 {
						 String item_id=rowSet.getString("item_id");
						 String itemdesc = rowSet.getString("itemdesc");
						 
						 String body_id=rowSet.getString("body_id");
						 if(bodyMap.get(body_id)==null)//过滤无效数据
                         {
                             continue;
                         }
						 
						 if(abody_id.length()==0) {
                             abody_id=body_id;
                         }
						 if(abody_id.equals(body_id))//如果这个body_id已经有了
                         {
                             tempList.add(new CommonData(rowSet.getString("score")!=null?PubFunc.round(rowSet.getString("score"),1):"0",itemdesc));
                         } else//某个具体的body_id遍历结束，开始向itemDataMap塞入数据
						 {
							 if(bodyMap.get(abody_id)!=null&&tempList.size()>0)
							 {
								 itemDataMap.put(abody_id, tempList);
								 itemDescMap.put((String)bodyMap.get(abody_id), tempList);
								 abody_id=body_id;
								 tempList=new ArrayList();
								 tempList.add(new CommonData(rowSet.getString("score")!=null?PubFunc.round(rowSet.getString("score"),1):"0",itemdesc));
							 }
						 }
					 } //while结束
					 //把最后那一项加上
					 if(bodyMap.get(abody_id)!=null&&tempList.size()>0)
					 {
						 itemDataMap.put(abody_id, tempList);
						 itemDescMap.put((String)bodyMap.get(abody_id), tempList);
					 }
				 }
				 
				 //共性项目对应的指标
				 if(!isByModel){//如果不是按岗位素质模型测评
					sql="select pos.*,pp.pointname from (select point_id,body_id,sum(score) score from Per_ScoreDetail ";
					sql+=" where plan_id="+planid+" and object_id ='"+object_id+"' and point_id in ";
					sql+=" (select point_id from per_template_point where item_id in (select item_id from per_template_item where template_id in (select template_id from per_plan where plan_id="+planid+") and (kind=1 or kind is null)))" ;
					sql+=" group by point_id,body_id ) pos,per_point pp  ";
					sql+=" where pos.point_id=pp.point_id order by pos.body_id ";
				 }else{
					 SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
					 Calendar calendar = Calendar.getInstance();				
					 String historyDate = sdf.format(calendar.getTime());
					 StringBuffer sb = new StringBuffer("");//按岗位素质模型测评的指标
					 sb.append("select point_id from per_competency_modal where object_type='3' and object_id = (select "+Sql_switcher.isnull("e01a1", "null")+" from usra01 where a0100='"+object_id+"')");
					 sb.append(" and "+Sql_switcher.dateValue(historyDate)+" between start_date and end_date");
					 sql="select pos.*,pp.pointname from (select point_id,body_id,sum(score) score from Per_ScoreDetail ";
					 sql+=" where plan_id="+planid+" and object_id ='"+object_id+"' and point_id in ";
					 sql+=" ("+sb.toString()+")" ;
					 sql+=" group by point_id,body_id ) pos,per_point pp  ";
					 sql+=" where pos.point_id=pp.point_id order by pos.body_id ";
				 }
				 rowSet=dao.search(sql);
				 ArrayList pointList = new ArrayList();
				 abody_id="";
				 tempList=new ArrayList();
				 while(rowSet.next())
				 {
					 String point_id=rowSet.getString("point_id");
					 String point_name = rowSet.getString("pointname");
					 String score = rowSet.getString("score")!=null?rowSet.getString("score"):"0";
					 Float theScore = (Float)map.get(point_id.toLowerCase());//标准分值
					 if("1".equals(isShowPercentVal) && "0".equals(status)&& theScore!=null && theScore.floatValue()>0 && !isByModel)//如果是分值模板，并且按百分比显示
					 {
						 float x = Float.parseFloat(score)*100/theScore.floatValue();
						 score = Float.toString(x);//百分比
					 }	
					 
					 
					 String body_id=rowSet.getString("body_id");
					 if(bodyMap.get(body_id)==null) {
                         continue;
                     }
							 
					 if(abody_id.length()==0) {
                         abody_id=body_id;
                     }
					 
					 if(abody_id.equals(body_id)) {
                         tempList.add(new CommonData(score,point_name));
                     } else
					 {
						 if(bodyMap.get(abody_id)!=null&&tempList.size()>0)
						 {
							 if(itemDataMap.get(abody_id)!=null)//把个性项目数据放到指标后面
							 {
								 ArrayList list  = (ArrayList)itemDataMap.get(abody_id);
								 for(int j=0;j<list.size();j++) {
                                     tempList.add(list.get(j));
                                 }
							 }
							 dataMap.put((String)bodyMap.get(abody_id), tempList);							 
							 abody_id=body_id;
							 tempList=new ArrayList();
							 tempList.add(new CommonData(score,point_name));
						 }
					 }
				 } //while结束
				 if(bodyMap.get(abody_id)!=null&&tempList.size()>0)
				 {
					 if(itemDataMap.get(abody_id)!=null)//把个性项目数据放到指标后面
					 {
						 ArrayList list  = (ArrayList)itemDataMap.get(abody_id);
						 for(int j=0;j<list.size();j++) {
                             tempList.add(list.get(j));
                         }
					 }
					 dataMap.put((String)bodyMap.get(abody_id), tempList);
				 }
				 
				 /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				//显示所有指标和个性项目 
				//所有指标
				pointList = new ArrayList();
				if(!isByModel){
					ArrayList pointToNameList=this.getPlanPointList(planid);
					for(int i=0;i<pointToNameList.size();i++)
					{
						 LazyDynaBean abean=(LazyDynaBean)pointToNameList.get(i);
						 String point_id = (String)abean.get("point_id");
						 String pointname = (String)abean.get("pointname");
						 
						 HashMap pointMap = new HashMap();
						 pointMap.put("point_id", point_id);
						 pointMap.put("point_name", pointname);
						 pointList.add(pointMap); 
					}
				}else{
					 SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
					 Calendar calendar = Calendar.getInstance();				
					 String historyDate = sdf.format(calendar.getTime());
					 StringBuffer sb = new StringBuffer("");//按岗位素质模型测评的指标
					 sb.append("select pcm.point_id,pp.pointname from per_competency_modal pcm left join per_point pp on pcm.point_id=pp.point_id where object_type='3' and object_id = (select "+Sql_switcher.isnull("e01a1", "null")+" from usra01 where a0100='"+object_id+"')");
					 sb.append(" and "+Sql_switcher.dateValue(historyDate)+" between start_date and end_date");
					 rowSet = dao.search(sb.toString());
					 while(rowSet.next()){
						 String point_id = rowSet.getString("point_id")==null?"":rowSet.getString("point_id");
						 String pointname = rowSet.getString("pointname")==null?"":rowSet.getString("pointname");
						 HashMap pointMap = new HashMap();
						 pointMap.put("point_id", point_id);
						 pointMap.put("point_name", pointname);
						 pointList.add(pointMap); 
					 }
				}
				
				//所有个性项目
				itemList = new ArrayList();
				if(!isByModel){
					sql = "select item_id,itemdesc from per_template_item where template_id in (select template_id from per_plan where plan_id="+planid+") and kind=2";
					rowSet=dao.search(sql);
					while(rowSet.next())
					{
						 String item_id = (String)rowSet.getString(1);
						 String itemdesc = (String)rowSet.getString(2);
						 HashMap itemMap = new HashMap();
						 itemMap.put("item_id", item_id);
						 itemMap.put("itemdesc", itemdesc);
						 itemList.add(itemMap); 
					}
				}
				
				
				//统一所有数据  没有数据的显示0 在此统一下数据
//			    Set bodyids = bodyMap.keySet();
			    HashMap tempMap = new HashMap();
//				for (Iterator iter = bodyids.iterator(); iter.hasNext();)
//				{
//					String bodyid = (String) iter.next();
//					String bodyname = (String) bodyMap.get(bodyid);
				
			    // 考虑模板只有个性项目  JinChunhai 2011.08.18 
			    if((dataMap==null || dataMap.size()<=0) && (itemDescMap!=null && itemDescMap.size()>0)) {
                    dataMap=itemDescMap;
                }
			    
			    //开始统一所有数据
			    int z=0;
				for(int m=0;m<mainbodySetList.size();m++)//按次序显示主体类别
			    {
					mainbody_bean=(LazyDynaBean)mainbodySetList.get(m);
					String bodyid=(String)mainbody_bean.get("body_id");
					if(bodyMap.get(bodyid)==null) {
                        continue;
                    }
					String bodyname=(String)mainbody_bean.get("name");					
					
					tempList=new ArrayList();
					if(dataMap.get(bodyname)!=null)//比如说有三个主体类别：公司领导，直管领导，同级，但是只分配了一个考核主体。所以dataMap里的body_id数目会少一些
					{
						ArrayList list = (ArrayList)dataMap.get(bodyname);
						HashMap dataValue = new HashMap();
						for(int j=0;j<list.size();j++)
						{
							CommonData data = (CommonData)list.get(j);
							dataValue.put(data.getDataName(),data.getDataValue() );
						}
						 float minScore = 0;  //  单条线的最低值
						 float maxScore = 0;  //  单条线的最高值
						
						//按顺序放数据 没有的放0
						 for(int i=0;i<pointList.size();i++)
						 {
							 HashMap pointMap = (HashMap)pointList.get(i);
							 String point_name =(String)pointMap.get("point_name");
							 String score = dataValue.get(point_name)!=null?(String)dataValue.get(point_name):"0";	
							 
							 if(i==0)
							 {
								 minScore = Float.parseFloat(score);
								 maxScore = Float.parseFloat(score);	
							 }						 								 	
							 if(minScore > Float.parseFloat(score)) {
                                 minScore = Float.parseFloat(score);
                             }
							 if(maxScore < Float.parseFloat(score)) {
                                 maxScore = Float.parseFloat(score);
                             }
							 
							 if("41".equals(chart_type)) {
                                 point_name=this.warpRowStr(point_name,25);
                             } else {
                                 point_name=this.warpRowStr(point_name,6);
                             }
							 
							 tempList.add(new CommonData(score,point_name));
						 }
						 
						 if(pointList!=null && pointList.size()>0)
						 {							 						 
							 if(z==0)
							 {
								 if(minRadarScore==0) {
                                     minRadarScore = minScore;
                                 } else
								 {
									 if(minRadarScore > minScore) {
                                         minRadarScore = minScore;
                                     }
								 }
								 if(maxRadarScore==0 || maxRadarScore==0.0) {
                                     maxRadarScore = maxScore;
                                 } else
								 {													 
									 if(maxRadarScore < maxScore) {
                                         maxRadarScore = maxScore;
                                     }
								 }
							 }else
							 {
								 if(minRadarScore > minScore) {
                                     minRadarScore = minScore;
                                 }
								 if(maxRadarScore < maxScore) {
                                     maxRadarScore = maxScore;
                                 }
							 }
						}
						 
						 for(int i=0;i<itemList.size();i++)
						 {
							 HashMap itemMap = (HashMap)itemList.get(i);
							 String item_name =(String)itemMap.get("itemdesc");
							 String score = dataValue.get(item_name)!=null?(String)dataValue.get(item_name):"0";
							 
							 if(i==0)
							 {
								 minScore = Float.parseFloat(score);
								 maxScore = Float.parseFloat(score);	
							 }						 								 	
							 if(minScore > Float.parseFloat(score)) {
                                 minScore = Float.parseFloat(score);
                             }
							 if(maxScore < Float.parseFloat(score)) {
                                 maxScore = Float.parseFloat(score);
                             }
							 
							 if("41".equals(chart_type)) {
                                 item_name=this.warpRowStr(item_name,25);
                             } else {
                                 item_name=this.warpRowStr(item_name,6);
                             }
							 
							 tempList.add(new CommonData(score,item_name));
						 }
						 if(z==0)
						 {
							 if(minRadarScore==0) {
                                 minRadarScore = minScore;
                             } else
							 {
								 if(minRadarScore > minScore) {
                                     minRadarScore = minScore;
                                 }
							 }
							 if(maxRadarScore==0 || maxRadarScore==0.0) {
                                 maxRadarScore = maxScore;
                             } else
							 {													 
								 if(maxRadarScore < maxScore) {
                                     maxRadarScore = maxScore;
                                 }
							 }
						 }else
						 {
							 if(minRadarScore > minScore) {
                                 minRadarScore = minScore;
                             }
							 if(maxRadarScore < maxScore) {
                                 maxRadarScore = maxScore;
                             }
						 }
						 z++;
						 
					}else
					{
						//按顺序放数据 没有的放0
						 for(int i=0;i<pointList.size();i++)
						 {
							 HashMap pointMap = (HashMap)pointList.get(i);
							 String point_name =(String)pointMap.get("point_name");		
							 if("41".equals(chart_type)) {
                                 point_name=this.warpRowStr(point_name,25);
                             } else {
                                 point_name=this.warpRowStr(point_name,6);
                             }
							 tempList.add(new CommonData("0",point_name));
							 flagScore = true;
						 }
						 for(int i=0;i<itemList.size();i++)
						 {
							 HashMap itemMap = (HashMap)itemList.get(i);
							 String item_name =(String)itemMap.get("itemdesc");		
							 if("41".equals(chart_type)) {
                                 item_name=this.warpRowStr(item_name,25);
                             } else {
                                 item_name=this.warpRowStr(item_name,6);
                             }
							 tempList.add(new CommonData("0",item_name));
							 flagScore = true;
						 }
					}
					tempMap.put(bodyname, tempList);
				}				 
				dataMap=tempMap;
				 
				 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				 //综合
				 if("null".equals(selectids)||(","+selectids+",").indexOf(",-1,")!=-1)
				 {
					 tempList=new ArrayList();
					 if(!isByModel){//如果不是按岗位素质模型测评
						 rowSet=dao.search("select * from per_result_"+planid+" where object_id  ='"+object_id+"'");
						 int y=0;
						 if(rowSet.next())
						 {
							 float minScore = 0;  //  单条线的最低值
							 float maxScore = 0;  //  单条线的最高值
							 //先处理共性项目
							 for(int i=0;i<pointList.size();i++)
							 {
								 HashMap pointMap = (HashMap)pointList.get(i);
								 String point_id=(String)pointMap.get("point_id");
								 String point_name =(String)pointMap.get("point_name");
								 String score = rowSet.getString("C_"+point_id)!=null?rowSet.getString("C_"+point_id):"0";
								 
								 if(i==0)
								 {
									 minScore = Float.parseFloat(score);
									 maxScore = Float.parseFloat(score);	
								 }						 								 								 							 
								 Float theScore = (Float)map.get(point_id.toLowerCase());
								 if("1".equals(isShowPercentVal) && "0".equals(status)&& theScore!=null && theScore.floatValue()>0)
								 {
									 float x = Float.parseFloat(score)*100/theScore.floatValue();
									 score = Float.toString(x);
								 }
								 if(minScore > Float.parseFloat(score)) {
                                     minScore = Float.parseFloat(score);
                                 }
								 if(maxScore < Float.parseFloat(score)) {
                                     maxScore = Float.parseFloat(score);
                                 }
								 
								 if("41".equals(chart_type)) {
                                     point_name=this.warpRowStr(point_name,25);
                                 } else {
                                     point_name=this.warpRowStr(point_name,6);
                                 }
								 
								 tempList.add(new CommonData(score,point_name));
							 }
	/*						 
							 if(y==0)
							 {
								 if(minRadarScore==0)
									 minRadarScore = minScore; 
								 else
								 {
									 if(minRadarScore > minScore)
										 minRadarScore = minScore;							 							 
								 }
								 if(maxRadarScore==0 || maxRadarScore==0.0)
									 maxRadarScore = maxScore;
								 else
								 {													 
									 if(maxRadarScore < maxScore)
										 maxRadarScore = maxScore;
								 }
							 }else
							 {
								 if(minRadarScore > minScore)
									 minRadarScore = minScore;							 
								 if(maxRadarScore < maxScore)
									 maxRadarScore = maxScore;
							 }
	*/						 
							 for(int i=0;i<itemList.size();i++)
							 {
								 HashMap itemMap = (HashMap)itemList.get(i);
								 String item_id=(String)itemMap.get("item_id");
								 String item_name =(String)itemMap.get("itemdesc");
								 String score = rowSet.getString("T_"+item_id)!=null?rowSet.getString("T_"+item_id):"0";
								 
								 if(i==0)
								 {
									 minScore = Float.parseFloat(score);
									 maxScore = Float.parseFloat(score);	
								 }
								 if(minScore > Float.parseFloat(score)) {
                                     minScore = Float.parseFloat(score);
                                 }
								 if(maxScore < Float.parseFloat(score)) {
                                     maxScore = Float.parseFloat(score);
                                 }
								 
								 if("41".equals(chart_type)) {
                                     item_name=this.warpRowStr(item_name,25);
                                 } else {
                                     item_name=this.warpRowStr(item_name,6);
                                 }
								 
								 tempList.add(new CommonData(score,item_name));
							 }
							 if(y==0)
							 {
								 if(minRadarScore==0) {
                                     minRadarScore = minScore;
                                 } else
								 {
									 if(minRadarScore > minScore) {
                                         minRadarScore = minScore;
                                     }
								 }
								 if(maxRadarScore==0 || maxRadarScore==0.0) {
                                     maxRadarScore = maxScore;
                                 } else
								 {													 
									 if(maxRadarScore < maxScore) {
                                         maxRadarScore = maxScore;
                                     }
								 }
							 }else
							 {
								 if(minRadarScore > minScore) {
                                     minRadarScore = minScore;
                                 }
								 if(maxRadarScore < maxScore) {
                                     maxRadarScore = maxScore;
                                 }
							 }
							 y++;
						 }
					 }else{//如果是按岗位素质模型测评
						 String sqlquery = "select phr.*,pp.pointname from per_history_result phr left join per_point pp on phr.point_id=pp.point_id where plan_id="+planid+" and object_id  ='"+object_id+"' and phr.status=0";
						 rowSet=dao.search(sqlquery);
						 int y=0;
						 while(rowSet.next())
						 {
							 float minScore = 0;  //  单条线的最低值
							 float maxScore = 0;  //  单条线的最高值
							 //先处理共性项目
							 String score = rowSet.getString("score")!=null?rowSet.getString("score"):"0";
							 String point_name = rowSet.getString("pointname")!=null?rowSet.getString("pointname"):"";
							 if(y==0)
							 {
								 minScore = Float.parseFloat(score);
								 maxScore = Float.parseFloat(score);
							 }						 								 								 							 
							 if(minScore > Float.parseFloat(score)) {
                                 minScore = Float.parseFloat(score);
                             }
							 if(maxScore < Float.parseFloat(score)) {
                                 maxScore = Float.parseFloat(score);
                             }
							 
							 if("41".equals(chart_type)) {
                                 point_name=this.warpRowStr(point_name,25);
                             } else {
                                 point_name=this.warpRowStr(point_name,6);
                             }
							 
							 tempList.add(new CommonData(score,point_name));	

							 if(y==0)
							 {
								 if(minRadarScore==0) {
                                     minRadarScore = minScore;
                                 } else
								 {
									 if(minRadarScore > minScore) {
                                         minRadarScore = minScore;
                                     }
								 }
								 if(maxRadarScore==0 || maxRadarScore==0.0) {
                                     maxRadarScore = maxScore;
                                 } else
								 {													 
									 if(maxRadarScore < maxScore) {
                                         maxRadarScore = maxScore;
                                     }
								 }
							 }else
							 {
								 if(minRadarScore > minScore) {
                                     minRadarScore = minScore;
                                 }
								 if(maxRadarScore < maxScore) {
                                     maxRadarScore = maxScore;
                                 }
							 }
							 y++;
						 }
					 }
					 
					 if(tempList.size()>0) {
                         dataMap.put(ResourceFactory.getProperty("jx.param.zh"),tempList);
                     }
				 } //综合 处理完毕	
			 } //数据处理完毕
			 if(minRadarScore>=maxRadarScore || flagScore) {
                 minRadarScore=0;
             }
			 if(maxRadarScore==0 || maxRadarScore==0.0) {
                 maxRadarScore=5;
             }
			 if("41".equals(chart_type))
			 {
//				 dataMap.put("minmax",minRadarScore+","+maxRadarScore);
				 dataMap.put("minmax",0+","+maxRadarScore);
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return dataMap;
	 }
	 /**
	  * 取得单考核对象 主体分类对比分析数据  含按岗位素质模型测评 但未定义岗位素质模型指标情况
	  * @param planid
	  * @param object_ids
	  * @return
	  */
	 public HashMap getSingleMainbodyContrastDataModify(String planid,String object_id,String selectids,String isShowPercentVal,String chart_type,String byModel)
	 {
		 HashMap dataMap=new HashMap();
		 if(planid.length()==0) {
             return dataMap;
         }
		 try
		 {
			 ArrayList mainbodySetList=getMainbodySetList(planid);
			 DbWizard dbWizard=new DbWizard(this.conn); 
			 ContentDAO dao=new ContentDAO(this.conn);
			 LazyDynaBean mainbody_bean=null;			 
			 String status = "0";//0-分值模板	  1-权重模板
			 String sql = "select * from per_template where template_id = (select template_id from per_plan where plan_id="+planid+")";
			 RowSet rowSet=dao.search(sql);
			 if(rowSet.next()) {
                 status = rowSet.getString("status");
             }
			 	
			 //取得指标的总分
			 HashMap map = new HashMap();
			 sql = "select * from per_template_point where item_id in (select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+"))";
			 rowSet=dao.search(sql);
			 while(rowSet.next())
			 {
				 String pointid = rowSet.getString("point_id");
				 float score = rowSet.getFloat("score");
				 map.put(pointid.toLowerCase(), new Float(score));
			 }			 
			 
			 HashMap bodyMap = new HashMap();			 
			 for(int i=0;i<mainbodySetList.size();i++)
			 {
				 mainbody_bean=(LazyDynaBean)mainbodySetList.get(i);
				 String bodyid=(String)mainbody_bean.get("body_id");
				 String name=(String)mainbody_bean.get("name");				
			
				 if("null".equals(selectids)||(","+selectids+",").indexOf(","+bodyid+",")!=-1) {
                     bodyMap.put(bodyid, name);
                 }
			 }			 
			 
			 float minRadarScore = 0;  //  所有线的最低值
			 float maxRadarScore = 0;  //  所有线的最高值
			 boolean flagScore = false;  //  0分值标识
			 
			 if(dbWizard.isExistTable("Per_ScoreDetail",false)&&object_id.length()>0)
			 {
				 //个性项目
				 sql="select a.*,b.itemdesc from (select item_id,body_id,(sum(score*point_rank)/sum(point_rank)) score from Per_ScoreDetail where plan_id="+planid+" and object_id ='"+object_id;
				 sql+="' and item_id in (select item_id from per_template_item where template_id in (select template_id from per_plan where plan_id="+planid+") and kind=2) group by item_id,body_id ) a";
				 sql+=",per_template_item b where a.item_id=b.item_id order by a.body_id";
				 rowSet=dao.search(sql);
				 String abody_id="";
				 ArrayList tempList=new ArrayList();
				 ArrayList itemList=new ArrayList();
				 HashMap itemDataMap = new HashMap();
				 HashMap itemDescMap = new HashMap();
				 boolean flag = false;
				 HashMap itemidMap = new HashMap();
				 while(rowSet.next())
				 {
					 String item_id=rowSet.getString("item_id");
					 String itemdesc = rowSet.getString("itemdesc");
					 
					 String body_id=rowSet.getString("body_id");
					 if(bodyMap.get(body_id)==null)//过滤无效数据
                     {
                         continue;
                     }
					 
					 if(abody_id.length()==0) {
                         abody_id=body_id;
                     }
					 if(abody_id.equals(body_id)) {
                         tempList.add(new CommonData(rowSet.getString("score")!=null?PubFunc.round(rowSet.getString("score"),1):"0",itemdesc));
                     } else
					 {
						 if(bodyMap.get(abody_id)!=null&&tempList.size()>0)
						 {
							 itemDataMap.put(abody_id, tempList);
							 itemDescMap.put((String)bodyMap.get(abody_id), tempList);
							 flag=true;
							 abody_id=body_id;
							 tempList=new ArrayList();
							 tempList.add(new CommonData(rowSet.getString("score")!=null?PubFunc.round(rowSet.getString("score"),1):"0",itemdesc));
						 }
					 }
					 if(!flag)
					 {
						 if(itemidMap.get(item_id)!=null) {
                             continue;
                         }
						 
						 itemidMap.put(item_id, item_id);						 
						 HashMap itemMap = new HashMap();
						 itemMap.put("item_id", item_id);
						 itemMap.put("itemdesc", itemdesc);
						 itemList.add(itemMap); 
					 }
				 }
				 if(bodyMap.get(abody_id)!=null&&tempList.size()>0)	
				 {
					 itemDataMap.put(abody_id, tempList);
					 itemDescMap.put((String)bodyMap.get(abody_id), tempList);
				 }
				 
				 
				 
				 
				 //共性项目对应的指标
				 sql="select pos.*,pp.pointname from (select point_id,body_id,sum(score) score from Per_ScoreDetail ";
				 sql+=" where plan_id="+planid+" and object_id ='"+object_id+"' and point_id in ";
				 sql+=" (select point_id from per_template_point where item_id in (select item_id from per_template_item where template_id in (select template_id from per_plan where plan_id="+planid+") and (kind=1 or kind is null)))" ;
				 sql+=" group by point_id,body_id ) pos,per_point pp  ";
				 sql+=" where pos.point_id=pp.point_id order by pos.body_id ";
				 rowSet=dao.search(sql);
				 ArrayList pointList = new ArrayList();
				 flag = false;
				 abody_id="";
				 tempList=new ArrayList();
				 while(rowSet.next())
				 {
					 String point_id=rowSet.getString("point_id");
					 String point_name = rowSet.getString("pointname");
					 String score = rowSet.getString("score")!=null?rowSet.getString("score"):"0";
					 Float theScore = (Float)map.get(point_id.toLowerCase());
					 if("1".equals(isShowPercentVal) && "0".equals(status)&& theScore!=null && theScore.floatValue()>0)
					 {
						 float x = Float.parseFloat(score)*100/theScore.floatValue();
						 score = Float.toString(x);
					 }	
					 
					 
					 String body_id=rowSet.getString("body_id");
					 if(bodyMap.get(body_id)==null) {
                         continue;
                     }
							 
					 if(abody_id.length()==0) {
                         abody_id=body_id;
                     }
					 
					 if(abody_id.equals(body_id)) {
                         tempList.add(new CommonData(score,point_name));
                     } else
					 {
						 if(bodyMap.get(abody_id)!=null&&tempList.size()>0)
						 {
							 if(itemDataMap.get(abody_id)!=null)//把个性项目数据放到指标后面
							 {
								 ArrayList list  = (ArrayList)itemDataMap.get(abody_id);
								 for(int j=0;j<list.size();j++) {
                                     tempList.add(list.get(j));
                                 }
							 }
							 dataMap.put((String)bodyMap.get(abody_id), tempList);							 
							 flag=true;
							 abody_id=body_id;
							 tempList=new ArrayList();
							 tempList.add(new CommonData(score,point_name));
						 }
					 }
					 if(!flag)
					 {
						 HashMap pointMap = new HashMap();
						 pointMap.put("point_id", point_id);
						 pointMap.put("point_name", point_name);
						 pointList.add(pointMap); 
					 }					 
				 }
				 if(bodyMap.get(abody_id)!=null&&tempList.size()>0)
				 {
					 if(itemDataMap.get(abody_id)!=null)//把个性项目数据放到指标后面
					 {
						 ArrayList list  = (ArrayList)itemDataMap.get(abody_id);
						 for(int j=0;j<list.size();j++) {
                             tempList.add(list.get(j));
                         }
					 }
					 dataMap.put((String)bodyMap.get(abody_id), tempList);
				 }		
				 /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				//显示所有指标和个性项目 没有数据的显示0 在此统一下数据
				//所有指标
				pointList = new ArrayList();
				ArrayList pointToNameList=this.getPlanPointList(planid);
				for(int i=0;i<pointToNameList.size();i++)
				{
					 LazyDynaBean abean=(LazyDynaBean)pointToNameList.get(i);
					 String point_id = (String)abean.get("point_id");
					 String pointname = (String)abean.get("pointname");
					 
					 HashMap pointMap = new HashMap();
					 pointMap.put("point_id", point_id);
					 pointMap.put("point_name", pointname);
					 pointList.add(pointMap); 
				}
				//所有个性项目
				itemList = new ArrayList();
				sql = "select item_id,itemdesc from per_template_item where template_id in (select template_id from per_plan where plan_id="+planid+") and kind=2";
				rowSet=dao.search(sql);
				while(rowSet.next())
				{
					 String item_id = (String)rowSet.getString(1);
					 String itemdesc = (String)rowSet.getString(2);
					 HashMap itemMap = new HashMap();
					 itemMap.put("item_id", item_id);
					 itemMap.put("itemdesc", itemdesc);
					 itemList.add(itemMap); 
				}
				//统一所有数据
//			    Set bodyids = bodyMap.keySet();
			    HashMap tempMap = new HashMap();
//				for (Iterator iter = bodyids.iterator(); iter.hasNext();)
//				{
//					String bodyid = (String) iter.next();
//					String bodyname = (String) bodyMap.get(bodyid);
				
			    // 考虑模板只有个性项目  JinChunhai 2011.08.18 
			    if((dataMap==null || dataMap.size()<=0) && (itemDescMap!=null && itemDescMap.size()>0)) {
                    dataMap=itemDescMap;
                }
			    
			    int z=0;
				for(int m=0;m<mainbodySetList.size();m++)//按次序显示主体类别
			    {
					mainbody_bean=(LazyDynaBean)mainbodySetList.get(m);
					String bodyid=(String)mainbody_bean.get("body_id");
					if(bodyMap.get(bodyid)==null) {
                        continue;
                    }
					String bodyname=(String)mainbody_bean.get("name");					
					
					tempList=new ArrayList();
					if(dataMap.get(bodyname)!=null)
					{
						ArrayList list = (ArrayList)dataMap.get(bodyname);
						HashMap dataValue = new HashMap();
						for(int j=0;j<list.size();j++)
						{
							CommonData data = (CommonData)list.get(j);
							dataValue.put(data.getDataName(),data.getDataValue() );
						}
						 float minScore = 0;  //  单条线的最低值
						 float maxScore = 0;  //  单条线的最高值
						
						//按顺序放数据 没有的放0
						 for(int i=0;i<pointList.size();i++)
						 {
							 HashMap pointMap = (HashMap)pointList.get(i);
							 String point_name =(String)pointMap.get("point_name");
							 String score = dataValue.get(point_name)!=null?(String)dataValue.get(point_name):"0";	
							 
							 if(i==0)
							 {
								 minScore = Float.parseFloat(score);
								 maxScore = Float.parseFloat(score);	
							 }						 								 	
							 if(minScore > Float.parseFloat(score)) {
                                 minScore = Float.parseFloat(score);
                             }
							 if(maxScore < Float.parseFloat(score)) {
                                 maxScore = Float.parseFloat(score);
                             }
							 
							 if("41".equals(chart_type)) {
                                 point_name=this.warpRowStr(point_name,25);
                             } else {
                                 point_name=this.warpRowStr(point_name,6);
                             }
							 
							 tempList.add(new CommonData(score,point_name));
						 }
						 
						 if(pointList!=null && pointList.size()>0)
						 {							 						 
							 if(z==0)
							 {
								 if(minRadarScore==0) {
                                     minRadarScore = minScore;
                                 } else
								 {
									 if(minRadarScore > minScore) {
                                         minRadarScore = minScore;
                                     }
								 }
								 if(maxRadarScore==0 || maxRadarScore==0.0) {
                                     maxRadarScore = maxScore;
                                 } else
								 {													 
									 if(maxRadarScore < maxScore) {
                                         maxRadarScore = maxScore;
                                     }
								 }
							 }else
							 {
								 if(minRadarScore > minScore) {
                                     minRadarScore = minScore;
                                 }
								 if(maxRadarScore < maxScore) {
                                     maxRadarScore = maxScore;
                                 }
							 }
						}
						 
						 for(int i=0;i<itemList.size();i++)
						 {
							 HashMap itemMap = (HashMap)itemList.get(i);
							 String item_name =(String)itemMap.get("itemdesc");
							 String score = dataValue.get(item_name)!=null?(String)dataValue.get(item_name):"0";
							 
							 if(i==0)
							 {
								 minScore = Float.parseFloat(score);
								 maxScore = Float.parseFloat(score);	
							 }						 								 	
							 if(minScore > Float.parseFloat(score)) {
                                 minScore = Float.parseFloat(score);
                             }
							 if(maxScore < Float.parseFloat(score)) {
                                 maxScore = Float.parseFloat(score);
                             }
							 
							 if("41".equals(chart_type)) {
                                 item_name=this.warpRowStr(item_name,25);
                             } else {
                                 item_name=this.warpRowStr(item_name,6);
                             }
							 
							 tempList.add(new CommonData(score,item_name));
						 }
						 if(z==0)
						 {
							 if(minRadarScore==0) {
                                 minRadarScore = minScore;
                             } else
							 {
								 if(minRadarScore > minScore) {
                                     minRadarScore = minScore;
                                 }
							 }
							 if(maxRadarScore==0 || maxRadarScore==0.0) {
                                 maxRadarScore = maxScore;
                             } else
							 {													 
								 if(maxRadarScore < maxScore) {
                                     maxRadarScore = maxScore;
                                 }
							 }
						 }else
						 {
							 if(minRadarScore > minScore) {
                                 minRadarScore = minScore;
                             }
							 if(maxRadarScore < maxScore) {
                                 maxRadarScore = maxScore;
                             }
						 }
						 z++;
						 
					}else
					{
						//按顺序放数据 没有的放0
						 for(int i=0;i<pointList.size();i++)
						 {
							 HashMap pointMap = (HashMap)pointList.get(i);
							 String point_name =(String)pointMap.get("point_name");		
							 if("41".equals(chart_type)) {
                                 point_name=this.warpRowStr(point_name,25);
                             } else {
                                 point_name=this.warpRowStr(point_name,6);
                             }
							 tempList.add(new CommonData("0",point_name));
							 flagScore = true;
						 }
						 for(int i=0;i<itemList.size();i++)
						 {
							 HashMap itemMap = (HashMap)itemList.get(i);
							 String item_name =(String)itemMap.get("itemdesc");		
							 if("41".equals(chart_type)) {
                                 item_name=this.warpRowStr(item_name,25);
                             } else {
                                 item_name=this.warpRowStr(item_name,6);
                             }
							 tempList.add(new CommonData("0",item_name));
							 flagScore = true;
						 }
					}
					tempMap.put(bodyname, tempList);
				}				 
				dataMap=tempMap;
				 
				 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				 //综合
				 if("null".equals(selectids)||(","+selectids+",").indexOf(",-1,")!=-1)
				 {	 
					 tempList=new ArrayList();
					 HashMap scoreMap=new HashMap();
					 if(byModel!=null&& "1".equals(byModel)){
						 rowSet=dao.search("select * from per_history_result where plan_id='"+planid+"' and object_id='"+object_id+"' and status='0'");
						 int y=0;
						 while(rowSet.next()){
							 String point_id=rowSet.getString("point_id");
							 String score=rowSet.getString("score");;
							 scoreMap.put(point_id, score);
						 }
						 if(scoreMap.size()>0)
						 {
							 float minScore = 0;  //  单条线的最低值
							 float maxScore = 0;  //  单条线的最高值
							 for(int i=0;i<pointList.size();i++)
							 {
								 HashMap pointMap = (HashMap)pointList.get(i);
								 String point_id=(String)pointMap.get("point_id");
								 String point_name =(String)pointMap.get("point_name");
								 String score =(String) scoreMap.get(point_id);
								 if(score==null|| "".equalsIgnoreCase(score)){
									 score="0";
								 }
								 
								 if(i==0)
								 {
									 minScore = Float.parseFloat(score);
									 maxScore = Float.parseFloat(score);	
								 }						 								 								 							 
								 Float theScore = (Float)map.get(point_id.toLowerCase());
								 if("1".equals(isShowPercentVal) && "0".equals(status)&& theScore!=null && theScore.floatValue()>0)
								 {
									 float x = Float.parseFloat(score)*100/theScore.floatValue();
									 score = Float.toString(x);
								 }
								 if(minScore > Float.parseFloat(score)) {
                                     minScore = Float.parseFloat(score);
                                 }
								 if(maxScore < Float.parseFloat(score)) {
                                     maxScore = Float.parseFloat(score);
                                 }
								 
								 if("41".equals(chart_type)) {
                                     point_name=this.warpRowStr(point_name,25);
                                 } else {
                                     point_name=this.warpRowStr(point_name,6);
                                 }
								 
								 tempList.add(new CommonData(score,point_name));
							 }

							 if(y==0)
							 {
								 if(minRadarScore==0) {
                                     minRadarScore = minScore;
                                 } else
								 {
									 if(minRadarScore > minScore) {
                                         minRadarScore = minScore;
                                     }
								 }
								 if(maxRadarScore==0 || maxRadarScore==0.0) {
                                     maxRadarScore = maxScore;
                                 } else
								 {													 
									 if(maxRadarScore < maxScore) {
                                         maxRadarScore = maxScore;
                                     }
								 }
							 }else
							 {
								 if(minRadarScore > minScore) {
                                     minRadarScore = minScore;
                                 }
								 if(maxRadarScore < maxScore) {
                                     maxRadarScore = maxScore;
                                 }
							 }
							 y++;
						 }
					 }else{
						 rowSet=dao.search("select * from per_result_"+planid+" where object_id  ='"+object_id+"'");
						 int y=0;
						 if(rowSet.next())
						 {
							 float minScore = 0;  //  单条线的最低值
							 float maxScore = 0;  //  单条线的最高值
							 for(int i=0;i<pointList.size();i++)
							 {
								 HashMap pointMap = (HashMap)pointList.get(i);
								 String point_id=(String)pointMap.get("point_id");
								 String point_name =(String)pointMap.get("point_name");
								 String score = rowSet.getString("C_"+point_id)!=null?rowSet.getString("C_"+point_id):"0";
								 
								 if(i==0)
								 {
									 minScore = Float.parseFloat(score);
									 maxScore = Float.parseFloat(score);	
								 }						 								 								 							 
								 Float theScore = (Float)map.get(point_id.toLowerCase());
								 if("1".equals(isShowPercentVal) && "0".equals(status)&& theScore!=null && theScore.floatValue()>0)
								 {
									 float x = Float.parseFloat(score)*100/theScore.floatValue();
									 score = Float.toString(x);
								 }
								 if(minScore > Float.parseFloat(score)) {
                                     minScore = Float.parseFloat(score);
                                 }
								 if(maxScore < Float.parseFloat(score)) {
                                     maxScore = Float.parseFloat(score);
                                 }
								 
								 if("41".equals(chart_type)) {
                                     point_name=this.warpRowStr(point_name,25);
                                 } else {
                                     point_name=this.warpRowStr(point_name,6);
                                 }
								 
								 tempList.add(new CommonData(score,point_name));
							 }
	/*						 
							 if(y==0)
							 {
								 if(minRadarScore==0)
									 minRadarScore = minScore; 
								 else
								 {
									 if(minRadarScore > minScore)
										 minRadarScore = minScore;							 							 
								 }
								 if(maxRadarScore==0 || maxRadarScore==0.0)
									 maxRadarScore = maxScore;
								 else
								 {													 
									 if(maxRadarScore < maxScore)
										 maxRadarScore = maxScore;
								 }
							 }else
							 {
								 if(minRadarScore > minScore)
									 minRadarScore = minScore;							 
								 if(maxRadarScore < maxScore)
									 maxRadarScore = maxScore;
							 }
	*/						 
							 for(int i=0;i<itemList.size();i++)
							 {
								 HashMap itemMap = (HashMap)itemList.get(i);
								 String item_id=(String)itemMap.get("item_id");
								 String item_name =(String)itemMap.get("itemdesc");
								 String score = rowSet.getString("T_"+item_id)!=null?rowSet.getString("T_"+item_id):"0";
								 
								 if(i==0)
								 {
									 minScore = Float.parseFloat(score);
									 maxScore = Float.parseFloat(score);	
								 }
								 if(minScore > Float.parseFloat(score)) {
                                     minScore = Float.parseFloat(score);
                                 }
								 if(maxScore < Float.parseFloat(score)) {
                                     maxScore = Float.parseFloat(score);
                                 }
								 
								 if("41".equals(chart_type)) {
                                     item_name=this.warpRowStr(item_name,25);
                                 } else {
                                     item_name=this.warpRowStr(item_name,6);
                                 }
								 
								 tempList.add(new CommonData(score,item_name));
							 }
							 if(y==0)
							 {
								 if(minRadarScore==0) {
                                     minRadarScore = minScore;
                                 } else
								 {
									 if(minRadarScore > minScore) {
                                         minRadarScore = minScore;
                                     }
								 }
								 if(maxRadarScore==0 || maxRadarScore==0.0) {
                                     maxRadarScore = maxScore;
                                 } else
								 {													 
									 if(maxRadarScore < maxScore) {
                                         maxRadarScore = maxScore;
                                     }
								 }
							 }else
							 {
								 if(minRadarScore > minScore) {
                                     minRadarScore = minScore;
                                 }
								 if(maxRadarScore < maxScore) {
                                     maxRadarScore = maxScore;
                                 }
							 }
							 y++;
						 }
					 }
					 if(tempList.size()>0) {
                         dataMap.put(ResourceFactory.getProperty("jx.param.zh"),tempList);
                     }
				 }			
			 }
			 if(minRadarScore>=maxRadarScore || flagScore) {
                 minRadarScore=0;
             }
			 if(maxRadarScore==0 || maxRadarScore==0.0) {
                 maxRadarScore=5;
             }
			 if("41".equals(chart_type))
			 {
//				 dataMap.put("minmax",minRadarScore+","+maxRadarScore);
				 dataMap.put("minmax",0+","+maxRadarScore);
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return dataMap;
	 }
	 /**
	  * 按岗位素质模型测评 取得单考核对象 主体分类对比分析数据
	  * @param planid
	  * @param object_ids
	  * @return
	  */
	 public HashMap getSingleMainbodyContrastDataByModel(String planid,String object_id,String selectids,String isShowPercentVal,String chart_type)
	 {
		 HashMap dataMap=new HashMap();
		 if(planid.length()==0) {
             return dataMap;
         }
		 try
		 {
			 ArrayList mainbodySetList=getMainbodySetList(planid);
			 DbWizard dbWizard=new DbWizard(this.conn); 
			 ContentDAO dao=new ContentDAO(this.conn);
			 LazyDynaBean mainbody_bean=null;			 
			 String status = "1";//0-分值模板	  1-权重模板
			 String sql = "";
			 RowSet rowSet=null;

			 //取得指标的总分
			 HashMap map = new HashMap();
			 
			 sql = "select * from per_competency_modal where object_type='3' and object_id='"+this.getE01A1ByA0100(object_id)+"'";
			 rowSet=dao.search(sql);
			 while(rowSet.next())
			 {
				 String pointid = rowSet.getString("point_id");
				 float score = rowSet.getFloat("score");
				 map.put(pointid.toLowerCase(), new Float(score));
			 }			 
			 
			 HashMap bodyMap = new HashMap();			 
			 for(int i=0;i<mainbodySetList.size();i++)
			 {
				 mainbody_bean=(LazyDynaBean)mainbodySetList.get(i);
				 String bodyid=(String)mainbody_bean.get("body_id");
				 String name=(String)mainbody_bean.get("name");				
			
				 if("null".equals(selectids)||(","+selectids+",").indexOf(","+bodyid+",")!=-1) {
                     bodyMap.put(bodyid, name);
                 }
			 }			 
			 
			 float minRadarScore = 0;  //  所有线的最低值
			 float maxRadarScore = 0;  //  所有线的最高值
			 boolean flagScore = false;  //  0分值标识
			 
			 if(dbWizard.isExistTable("Per_ScoreDetail",false)&&object_id.length()>0)
			 {
				 String abody_id="";
				 ArrayList tempList=new ArrayList();
				 boolean flag = false;
				 sql="select pos.*,pp.pointname from (select point_id,body_id,sum(score) score from Per_ScoreDetail ";
				 sql+=" where plan_id="+planid+" and object_id ='"+object_id+"' and point_id in ";
				 sql+=" (select point_id from per_history_result where plan_id='"+planid+"' and object_id='"+object_id+"' and status='0' )" ;
				 sql+=" group by point_id,body_id ) pos,per_point pp  ";
				 sql+=" where pos.point_id=pp.point_id order by pos.body_id ";
				 rowSet=dao.search(sql);
				 ArrayList pointList = new ArrayList();
				 flag = false;
				 abody_id="";
				 tempList=new ArrayList();
				 while(rowSet.next())
				 {
					 String point_id=rowSet.getString("point_id");
					 String point_name = rowSet.getString("pointname");
					 String score = rowSet.getString("score")!=null?rowSet.getString("score"):"0";
					 Float theScore = (Float)map.get(point_id.toLowerCase());
					 if("1".equals(isShowPercentVal) && "0".equals(status)&& theScore!=null && theScore.floatValue()>0)
					 {
						 float x = Float.parseFloat(score)*100/theScore.floatValue();
						 score = Float.toString(x);
					 }	
					 
					 
					 String body_id=rowSet.getString("body_id");
					 if(bodyMap.get(body_id)==null) {
                         continue;
                     }
							 
					 if(abody_id.length()==0) {
                         abody_id=body_id;
                     }
					 
					 if(abody_id.equals(body_id)) {
                         tempList.add(new CommonData(score,point_name));
                     } else
					 {
						 if(bodyMap.get(abody_id)!=null&&tempList.size()>0)
						 {
							 dataMap.put((String)bodyMap.get(abody_id), tempList);							 
							 flag=true;
							 abody_id=body_id;
							 tempList=new ArrayList();
							 tempList.add(new CommonData(score,point_name));
						 }
					 }
					 if(!flag)
					 {
						 HashMap pointMap = new HashMap();
						 pointMap.put("point_id", point_id);
						 pointMap.put("point_name", point_name);
						 pointList.add(pointMap); 
					 }					 
				 }
				 if(bodyMap.get(abody_id)!=null&&tempList.size()>0)
				 {

					 dataMap.put((String)bodyMap.get(abody_id), tempList);
				 }		
		
				HashMap tempMap = new HashMap();
			    
			    int z=0;
				for(int m=0;m<mainbodySetList.size();m++)//按次序显示主体类别
			    {
					mainbody_bean=(LazyDynaBean)mainbodySetList.get(m);
					String bodyid=(String)mainbody_bean.get("body_id");
					if(bodyMap.get(bodyid)==null) {
                        continue;
                    }
					String bodyname=(String)mainbody_bean.get("name");					
					
					tempList=new ArrayList();
					if(dataMap.get(bodyname)!=null)
					{
						ArrayList list = (ArrayList)dataMap.get(bodyname);
						HashMap dataValue = new HashMap();
						for(int j=0;j<list.size();j++)
						{
							CommonData data = (CommonData)list.get(j);
							dataValue.put(data.getDataName(),data.getDataValue() );
						}
						 float minScore = 0;  //  单条线的最低值
						 float maxScore = 0;  //  单条线的最高值
						
						//按顺序放数据 没有的放0
						 for(int i=0;i<pointList.size();i++)
						 {
							 HashMap pointMap = (HashMap)pointList.get(i);
							 String point_name =(String)pointMap.get("point_name");
							 String score = dataValue.get(point_name)!=null?(String)dataValue.get(point_name):"0";	
							 
							 if(i==0)
							 {
								 minScore = Float.parseFloat(score);
								 maxScore = Float.parseFloat(score);	
							 }						 								 	
							 if(minScore > Float.parseFloat(score)) {
                                 minScore = Float.parseFloat(score);
                             }
							 if(maxScore < Float.parseFloat(score)) {
                                 maxScore = Float.parseFloat(score);
                             }
							 
							 if("41".equals(chart_type)) {
                                 point_name=this.warpRowStr(point_name,25);
                             } else {
                                 point_name=this.warpRowStr(point_name,6);
                             }
							 
							 tempList.add(new CommonData(score,point_name));
						 }
						 
						 if(pointList!=null && pointList.size()>0)
						 {							 						 
							 if(z==0)
							 {
								 if(minRadarScore==0) {
                                     minRadarScore = minScore;
                                 } else
								 {
									 if(minRadarScore > minScore) {
                                         minRadarScore = minScore;
                                     }
								 }
								 if(maxRadarScore==0 || maxRadarScore==0.0) {
                                     maxRadarScore = maxScore;
                                 } else
								 {													 
									 if(maxRadarScore < maxScore) {
                                         maxRadarScore = maxScore;
                                     }
								 }
							 }else
							 {
								 if(minRadarScore > minScore) {
                                     minRadarScore = minScore;
                                 }
								 if(maxRadarScore < maxScore) {
                                     maxRadarScore = maxScore;
                                 }
							 }
						}

						 if(z==0)
						 {
							 if(minRadarScore==0) {
                                 minRadarScore = minScore;
                             } else
							 {
								 if(minRadarScore > minScore) {
                                     minRadarScore = minScore;
                                 }
							 }
							 if(maxRadarScore==0 || maxRadarScore==0.0) {
                                 maxRadarScore = maxScore;
                             } else
							 {													 
								 if(maxRadarScore < maxScore) {
                                     maxRadarScore = maxScore;
                                 }
							 }
						 }else
						 {
							 if(minRadarScore > minScore) {
                                 minRadarScore = minScore;
                             }
							 if(maxRadarScore < maxScore) {
                                 maxRadarScore = maxScore;
                             }
						 }
						 z++;
						 
					}else
					{
						//按顺序放数据 没有的放0
						 for(int i=0;i<pointList.size();i++)
						 {
							 HashMap pointMap = (HashMap)pointList.get(i);
							 String point_name =(String)pointMap.get("point_name");		
							 if("41".equals(chart_type)) {
                                 point_name=this.warpRowStr(point_name,25);
                             } else {
                                 point_name=this.warpRowStr(point_name,6);
                             }
							 tempList.add(new CommonData("0",point_name));
							 flagScore = true;
						 }

					}
					tempMap.put(bodyname, tempList);
				}				 
				dataMap=tempMap;
				 
				 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				 //综合
				HashMap scoreMap=new HashMap();
				 if("null".equals(selectids)||(","+selectids+",").indexOf(",-1,")!=-1)
				 {	 
					 tempList=new ArrayList();
					 rowSet=dao.search("select * from per_history_result where plan_id='"+planid+"' and object_id='"+object_id+"' and status='0'");
					 int y=0;
					 while(rowSet.next()){
						 String point_id=rowSet.getString("point_id");
						 String score=rowSet.getString("score");;
						 scoreMap.put(point_id, score);
					 }
					 if(scoreMap.size()>0)
					 {
						 float minScore = 0;  //  单条线的最低值
						 float maxScore = 0;  //  单条线的最高值
						 for(int i=0;i<pointList.size();i++)
						 {
							 HashMap pointMap = (HashMap)pointList.get(i);
							 String point_id=(String)pointMap.get("point_id");
							 String point_name =(String)pointMap.get("point_name");
							 String score =(String) scoreMap.get(point_id);
							 if(score==null|| "".equalsIgnoreCase(score)){
								 score="0";
							 }
							 
							 if(i==0)
							 {
								 minScore = Float.parseFloat(score);
								 maxScore = Float.parseFloat(score);	
							 }						 								 								 							 
							 Float theScore = (Float)map.get(point_id.toLowerCase());
							 if("1".equals(isShowPercentVal) && "0".equals(status)&& theScore!=null && theScore.floatValue()>0)
							 {
								 float x = Float.parseFloat(score)*100/theScore.floatValue();
								 score = Float.toString(x);
							 }
							 if(minScore > Float.parseFloat(score)) {
                                 minScore = Float.parseFloat(score);
                             }
							 if(maxScore < Float.parseFloat(score)) {
                                 maxScore = Float.parseFloat(score);
                             }
							 
							 if("41".equals(chart_type)) {
                                 point_name=this.warpRowStr(point_name,25);
                             } else {
                                 point_name=this.warpRowStr(point_name,6);
                             }
							 
							 tempList.add(new CommonData(score,point_name));
						 }

						 if(y==0)
						 {
							 if(minRadarScore==0) {
                                 minRadarScore = minScore;
                             } else
							 {
								 if(minRadarScore > minScore) {
                                     minRadarScore = minScore;
                                 }
							 }
							 if(maxRadarScore==0 || maxRadarScore==0.0) {
                                 maxRadarScore = maxScore;
                             } else
							 {													 
								 if(maxRadarScore < maxScore) {
                                     maxRadarScore = maxScore;
                                 }
							 }
						 }else
						 {
							 if(minRadarScore > minScore) {
                                 minRadarScore = minScore;
                             }
							 if(maxRadarScore < maxScore) {
                                 maxRadarScore = maxScore;
                             }
						 }
						 y++;
					 }
					 if(tempList.size()>0) {
                         dataMap.put(ResourceFactory.getProperty("jx.param.zh"),tempList);
                     }
				 }			
			 }
			 if(minRadarScore>=maxRadarScore || flagScore) {
                 minRadarScore=0;
             }
			 if(maxRadarScore==0 || maxRadarScore==0.0) {
                 maxRadarScore=5;
             }
			 if("41".equals(chart_type))
			 {
//				 dataMap.put("minmax",minRadarScore+","+maxRadarScore);
				 dataMap.put("minmax",0+","+maxRadarScore);
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return dataMap;
	 }
	 /**
	  * 取得单考核对象 主体分类对比分析数据的柱状图数据
	  * @param planid
	  * @param object_ids
	  * @author Guo Feng
	  */
	 public ArrayList getSingleMainbodyContrastList(HashMap dataMap)
	 {
		 ArrayList dataList = new ArrayList();
		 ArrayList totalList = new ArrayList();
		 int count = dataMap.size();//主体类别的个数
		 if(dataMap.get("minmax")!=null) {
             count--;
         }
		 if(count==0) {
             return dataList;
         }
		 ArrayList[] tempList = new ArrayList[count];
		 int index = 0;
		 ArrayList nameList = new ArrayList();
		 
		 Set key = dataMap.keySet();
		 for (Iterator it = key.iterator(); it.hasNext();) {
		      String s = (String) it.next();
			  if("minmax".equalsIgnoreCase(s)){
				  continue;
			  }
			  nameList.add(s);
			  tempList[index] = (ArrayList)dataMap.get(s);
			  index++;
		 }
		 
		 int fieldCount = tempList[0].size();
		 for(int i=0;i<fieldCount;i++){
			 totalList = new ArrayList();
			 LazyDynaBean abean=new LazyDynaBean();
			 String pointname = "";
			 for(int j=0;j<count;j++){
				 CommonData cd = (CommonData)tempList[j].get(i);
				 if(j==0) {
                     pointname = cd.getDataName();
                 }
				 CommonData commondata = new CommonData(cd.getDataValue(),(String)nameList.get(j));
				 totalList.add(commondata);
			 }
			 abean.set("categoryName", pointname);
		     abean.set("dataList",totalList);
		     dataList.add(abean); 
		 }
		 return dataList;
	 }
	 /**
	  * 取得 主体分类对比分析  分值序列数据
	  * @param selectids
	  * @return
	  */
	 public String getMainbodySetGradeStr(String selected,String planid)
	 {
		 ArrayList mainbodySetList=getMainbodySetList(planid);
		 StringBuffer str=new StringBuffer(",-1~"+ResourceFactory.getProperty("jx.param.zh")+"~");
		 if("null".equals(selected)||(","+selected+",").indexOf(",-1,")!=-1) {
             str.append("1");
         } else {
             str.append("0");
         }
		 for(int i=0;i<mainbodySetList.size();i++)
		 {
			 LazyDynaBean know_abean=(LazyDynaBean)mainbodySetList.get(i);
			 String body_id=(String)know_abean.get("body_id");
			 String name=(String)know_abean.get("name");
			 
			 str.append(","+body_id+"~"+name+"~");
			 if("null".equals(selected)||((","+selected+",").indexOf(","+body_id+",")!=-1)) {
                 str.append("1");
             } else {
                 str.append("0");
             }
		 }
		
		return str.substring(1);
	 }
	 
	 
	 /**
	  * 取得单考核对象 主体分类对比分析数据
	  * @param planid
	  * @param object_id
	  * @param pointList
	  * @return
	  */
	 public HashMap getSingleMainbodyContrastData(String planid,String object_id,ArrayList pointList,String selectids,String isShowPercentVal,String chart_type)
	 {
		 HashMap dataMap=new HashMap();
		 if(planid.trim().length()==0) {
             return dataMap;
         }
		 try
		 {
			 ArrayList mainbodySetList=getMainbodySetList(planid);
			 DbWizard dbWizard=new DbWizard(this.conn); 
			 ContentDAO dao=new ContentDAO(this.conn);
			 LazyDynaBean point_bean=null;
			 LazyDynaBean mainbody_bean=null;
			 
			 String status = "0";//0-分值模板	  1-权重模板
			 String sql = "select * from per_template where template_id = (select template_id from per_plan where plan_id="+planid+")";
			 RowSet rowSet=dao.search(sql);
			 if(rowSet.next()) {
                 status = rowSet.getString("status");
             }
			 
			 //取得指标的总分
			 HashMap map = new HashMap();
			 sql = "select * from per_template_point where item_id in (select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+"))";
			 rowSet=dao.search(sql);
			 while(rowSet.next())
			 {
				 String pointid = rowSet.getString("point_id");
				 float score = rowSet.getFloat("score");
				 map.put(pointid.toLowerCase(), new Float(score));
			 }
			 	
			 float minRadarScore = 0;  //  所有线的最低值
			 float maxRadarScore = 0;  //  所有线的最高值
			 boolean flagScore = false;  //  0分值标识
			 
			 if(dbWizard.isExistTable("per_bodyscore_"+planid,false))
			 {
				 rowSet=dao.search("select * from per_bodyscore_"+planid+" where object_id='"+object_id+"' and PK_Flag=1");
				 HashMap existKnowMap=new HashMap();
				 int z=0;
				 while(rowSet.next())
				 {
					 String class_id=rowSet.getString("class_id");
					 ArrayList tempList=new ArrayList();
					 
					 float minScore = 0;  //  单条线的最低值
					 float maxScore = 0;  //  单条线的最高值
					 
					 for(int i=0;i<pointList.size();i++)
					 {
						 point_bean=(LazyDynaBean)pointList.get(i);
						 String point_id=(String)point_bean.get("point_id");
						 String pointname=(String)point_bean.get("pointname");
						 
						 if("41".equals(chart_type)) {
                             pointname=this.warpRowStr(pointname,25);
                         } else {
                             pointname=this.warpRowStr(pointname,6);
                         }
						 
						 String score = rowSet.getString("C_"+point_id)!=null?rowSet.getString("C_"+point_id):"0";
						 
						 if(i==0)
						 {
							 minScore = Float.parseFloat(score);
							 maxScore = Float.parseFloat(score);	
						 }						 	
						 Float theScore = (Float)map.get(point_id.toLowerCase());
						 if("1".equals(isShowPercentVal) && "0".equals(status)&& theScore!=null && theScore.floatValue()>0)
						 {
							 float x = Float.parseFloat(score)*100/theScore.floatValue();
							 score = Float.toString(x);
						 }	
						 if(minScore > Float.parseFloat(score)) {
                             minScore = Float.parseFloat(score);
                         }
						 if(maxScore < Float.parseFloat(score)) {
                             maxScore = Float.parseFloat(score);
                         }
						 
						 //5.0以上版本
							if(this.userView.getVersion()>=50) {
                                tempList.add(new CommonData(score,pointname));
                            } else {
                                tempList.add(new CommonData(score,point_id));
                            }
//						 tempList.add(new CommonData(rowSet.getString("C_"+point_id)!=null?rowSet.getString("C_"+point_id):"0",point_id));
					 }
					 if(z==0)
					 {
						 if(minRadarScore==0) {
                             minRadarScore = minScore;
                         } else
						 {
							 if(minRadarScore > minScore) {
                                 minRadarScore = minScore;
                             }
						 }
						 if(maxRadarScore==0 || maxRadarScore==0.0) {
                             maxRadarScore = maxScore;
                         } else
						 {													 
							 if(maxRadarScore < maxScore) {
                                 maxRadarScore = maxScore;
                             }
						 }
					 }else
					 {
						 if(minRadarScore > minScore) {
                             minRadarScore = minScore;
                         }
						 if(maxRadarScore < maxScore) {
                             maxRadarScore = maxScore;
                         }
					 }
					 z++;
					 
					 for(int i=0;i<mainbodySetList.size();i++)
					 {
						 mainbody_bean=(LazyDynaBean)mainbodySetList.get(i);
						 String body_id=(String)mainbody_bean.get("body_id");
						 String name=(String)mainbody_bean.get("name");
						 if(body_id.equalsIgnoreCase(class_id))
						 {
							 if("null".equals(selectids)||(","+selectids+",").indexOf(","+body_id+",")!=-1) {
                                 dataMap.put(name,tempList);
                             }
							 existKnowMap.put(body_id,"1");
							 break;
						 }
					 }
				 }
				 for(int i=0;i<mainbodySetList.size();i++)
				 {
					 mainbody_bean=(LazyDynaBean)mainbodySetList.get(i);
					 String body_id=(String)mainbody_bean.get("body_id");
					 String name=(String)mainbody_bean.get("name"); 
					 ArrayList tempList=new ArrayList();
					 if(existKnowMap.get(body_id)==null)
					 {
						 for(int j=0;j<pointList.size();j++)
						 {
							 point_bean=(LazyDynaBean)pointList.get(j);
							 String point_id=(String)point_bean.get("point_id");
							 String pointname=(String)point_bean.get("pointname");							 
							 //5.0以上版本
							if(this.userView.getVersion()>=50) {
                                tempList.add(new CommonData("0",pointname));
                            } else {
                                tempList.add(new CommonData("0",point_id));
                            }
						 }
						 if("null".equals(selectids)||(","+selectids+",").indexOf(","+body_id+",")!=-1)
						 {
							 flagScore = true;  
							 dataMap.put(name,tempList);
						 }
					 }
				 }
				 rowSet=dao.search("select * from per_result_"+planid+" where  object_id='"+object_id+"' ");
				 int y=0;
				 if(rowSet.next())
				 {
					 ArrayList tempList=new ArrayList();
					 
					 float minScore = 0;  //  单条线的最低值
					 float maxScore = 0;  //  单条线的最高值
					 
					 for(int i=0;i<pointList.size();i++)
					 {
						 point_bean=(LazyDynaBean)pointList.get(i);
						 String point_id=(String)point_bean.get("point_id");
						 String pointname=(String)point_bean.get("pointname");
						 
						 if("41".equals(chart_type)) {
                             pointname=this.warpRowStr(pointname,25);
                         } else {
                             pointname=this.warpRowStr(pointname,6);
                         }
//						 tempList.add(new CommonData(rowSet.getString("C_"+point_id)!=null?rowSet.getString("C_"+point_id):"0",point_id));
						 
						 String score = rowSet.getString("C_"+point_id)!=null?rowSet.getString("C_"+point_id):"0";
						 if(i==0)
						 {
							 minScore = Float.parseFloat(score);
							 maxScore = Float.parseFloat(score);	
						 }						 	
						 
						 Float theScore = (Float)map.get(point_id.toLowerCase());
						 if("1".equals(isShowPercentVal) && theScore!=null && theScore.floatValue()>0)
						 {
							 float x = Float.parseFloat(score)*100/theScore.floatValue();
							 score = Float.toString(x);
						 }
						 if(minScore > Float.parseFloat(score)) {
                             minScore = Float.parseFloat(score);
                         }
						 if(maxScore < Float.parseFloat(score)) {
                             maxScore = Float.parseFloat(score);
                         }
						 
						 //5.0以上版本
						 if(this.userView.getVersion()>=50) {
                             tempList.add(new CommonData(score,pointname));
                         } else {
                             tempList.add(new CommonData(score,point_id));
                         }
					 }
					 if(y==0)
					 {
						 if(minRadarScore==0) {
                             minRadarScore = minScore;
                         } else
						 {
							 if(minRadarScore > minScore) {
                                 minRadarScore = minScore;
                             }
						 }
						 if(maxRadarScore==0 || maxRadarScore==0.0) {
                             maxRadarScore = maxScore;
                         } else
						 {													 
							 if(maxRadarScore < maxScore) {
                                 maxRadarScore = maxScore;
                             }
						 }
						 
					 }else
					 {
						 if(minRadarScore > minScore) {
                             minRadarScore = minScore;
                         }
						 if(maxRadarScore < maxScore) {
                             maxRadarScore = maxScore;
                         }
					 }
					 y++;
					 
					 if("null".equals(selectids)||(","+selectids+",").indexOf(",-1,")!=-1) {
                         dataMap.put(ResourceFactory.getProperty("jx.param.zh"),tempList);
                     }
				 }
			 }	
			 if(minRadarScore>=maxRadarScore || flagScore) {
                 minRadarScore=0;
             }
			 if(maxRadarScore==0 || maxRadarScore==0.0) {
                 maxRadarScore=5;
             }
			 if("41".equals(chart_type))
			 {
//				 dataMap.put("minmax",minRadarScore+","+maxRadarScore);
				 dataMap.put("minmax",0+","+maxRadarScore);
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return dataMap;
	 }
	 
	 /**
	  * 取得了解程度对比分析的分值序列 值 
	  * @param knowGradeList
	  * @param selected
	  * @return
	  */
	 public String getKnowScoreGradeStr(ArrayList knowGradeList ,String selected)
	 {
		 StringBuffer str=new StringBuffer(",-1~"+ResourceFactory.getProperty("jx.param.zh")+"~");
		 if("null".equals(selected)||(","+selected+",").indexOf(",-1,")!=-1) {
             str.append("1");
         } else {
             str.append("0");
         }
		 for(int i=0;i<knowGradeList.size();i++)
		 {
			 LazyDynaBean know_abean=(LazyDynaBean)knowGradeList.get(i);
			 String know_id=(String)know_abean.get("know_id");
			 String name=(String)know_abean.get("name");
			 
			 str.append(","+know_id+"~"+name+"~");
			 if("null".equals(selected)||((","+selected+",").indexOf(","+know_id+",")!=-1)) {
                 str.append("1");
             } else {
                 str.append("0");
             }
		 }
		
		return str.substring(1);
	 }
	 
	 /**
	  * 取得了解程度对比分析数据
	  * @param planid
	  * @param object_id
	  * @param knowGradeList
	  * @param controlStr  分值序列
	  * @return
	  */
	 public HashMap getKnowContrastData(String planid,String object_id,ArrayList knowGradeList,ArrayList pointList,String controlStr,String isShowPercentVal,String chart_type)
	 {
		 HashMap dataMap=new HashMap();
		 if(planid.trim().length()==0) {
             return dataMap;
         }
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 LazyDynaBean know_abean=null;
			 LazyDynaBean point_bean=null;
			 
			 String signLogo = "false";
			 int n = 0;
			 int m = 0;					 
			 for(int i=0;i<pointList.size();i++)
			 {
				 point_bean=(LazyDynaBean)pointList.get(i);				 
				 String pointname=(String)point_bean.get("pointname");
				 n = pointname.length();
				 if(i==0) {
                     m = pointname.length();
                 }
				 if(n>m) {
                     m = n;
                 }
				 if(i==pointList.size()-1)
				 {
					 if(n==m) {
                         signLogo = "true";
                     }
				 }
			 }
			 
			 float minRadarScore = 0;  //  所有线的最低值
			 float maxRadarScore = 0;  //  所有线的最高值
			 boolean flagScore = false;  //  0分值标识
			 
			 DbWizard dbWizard=new DbWizard(this.conn); 
			 
			 String status = "0";//0-分值模板	  1-权重模板
			 String sql = "select * from per_template where template_id = (select template_id from per_plan where plan_id="+planid+")";
			 RowSet rowSet=dao.search(sql);
			 if(rowSet.next()) {
                 status = rowSet.getString("status");
             }
			 
			 //取得指标的总分
			 HashMap map = new HashMap();
			 sql = "select * from per_template_point where item_id in (select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+"))";
			 rowSet=dao.search(sql);
			 while(rowSet.next())
			 {
				 String pointid = rowSet.getString("point_id");
				 float score = rowSet.getFloat("score");
				 map.put(pointid.toLowerCase(), new Float(score));
			 }
			 
			 if(dbWizard.isExistTable("per_bodyscore_"+planid,false))
			 {
				 rowSet=dao.search("select * from per_bodyscore_"+planid+" where object_id='"+object_id+"' and PK_Flag=2");
				 HashMap existKnowMap=new HashMap();
				 int z=0;
				 while(rowSet.next())
				 {
					 String class_id=rowSet.getString("class_id");
					 ArrayList tempList=new ArrayList();
					 
					 float minScore = 0;  //  单条线的最低值
					 float maxScore = 0;  //  单条线的最高值
					 
					 for(int i=0;i<pointList.size();i++)
					 {
						 point_bean=(LazyDynaBean)pointList.get(i);
						 String point_id=(String)point_bean.get("point_id");
						 String pointname=(String)point_bean.get("pointname");
						 
						 if("true".equalsIgnoreCase(signLogo))
						 {
							 if(pointname.length()<m)
							 {
								 for(int k=pointname.length();k<=m;k++)
								 {
									 pointname = pointname+" ";
								 }
							 }
						 }
						 if("41".equals(chart_type)) {
                             pointname=this.warpRowStr(pointname,25);
                         } else {
                             pointname=this.warpRowStr(pointname,6);
                         }
						 
//						 tempList.add(new CommonData(rowSet.getString("C_"+point_id)!=null?rowSet.getString("C_"+point_id):"0",point_id));
						 String score = rowSet.getString("C_"+point_id)!=null?rowSet.getString("C_"+point_id):"0";
						 
						 if(i==0)
						 {
							 minScore = Float.parseFloat(score);
							 maxScore = Float.parseFloat(score);	
						 }
						 
						 Float theScore = (Float)map.get(point_id.toLowerCase());
						 if("1".equals(isShowPercentVal) && "0".equals(status)&& theScore!=null && theScore.floatValue()>0)
						 {
							 float x = Float.parseFloat(score)*100/theScore.floatValue();
							 score = Float.toString(x);
						 }	
						 
						 if(minScore > Float.parseFloat(score)) {
                             minScore = Float.parseFloat(score);
                         }
						 if(maxScore < Float.parseFloat(score)) {
                             maxScore = Float.parseFloat(score);
                         }
						 
						 //5.0以上版本
						 if(this.userView.getVersion()>=50) {
                             tempList.add(new CommonData(score,pointname));
                         } else {
                             tempList.add(new CommonData(score,point_id));
                         }
					 }
					 
					 if(z==0)
					 {
						 if(minRadarScore==0) {
                             minRadarScore = minScore;
                         } else
						 {
							 if(minRadarScore > minScore) {
                                 minRadarScore = minScore;
                             }
						 }
						 if(maxRadarScore==0 || maxRadarScore==0.0) {
                             maxRadarScore = maxScore;
                         } else
						 {													 
							 if(maxRadarScore < maxScore) {
                                 maxRadarScore = maxScore;
                             }
						 }
					 }else
					 {
						 if(minRadarScore > minScore) {
                             minRadarScore = minScore;
                         }
						 if(maxRadarScore < maxScore) {
                             maxRadarScore = maxScore;
                         }
					 }
					 z++;
					 
					 for(int i=0;i<knowGradeList.size();i++)
					 {
						 know_abean=(LazyDynaBean)knowGradeList.get(i);
						 String know_id=(String)know_abean.get("know_id");
						 String name=(String)know_abean.get("name");
						 if(know_id.equalsIgnoreCase(class_id))
						 {
							 if("null".equals(controlStr)||(","+controlStr+",").indexOf(","+know_id+",")!=-1)
							 {
								 dataMap.put(name,tempList);
							 }
							 existKnowMap.put(know_id,"1");
							 break;
						 }
					 }
				 }
				 for(int i=0;i<knowGradeList.size();i++)
				 {
					 know_abean=(LazyDynaBean)knowGradeList.get(i);
					 String know_id=(String)know_abean.get("know_id");
					 String name=(String)know_abean.get("name"); 
					 ArrayList tempList=new ArrayList();
					 if(existKnowMap.get(know_id)==null)
					 {
						 for(int j=0;j<pointList.size();j++)
						 {
							 point_bean=(LazyDynaBean)pointList.get(j);
							 String point_id=(String)point_bean.get("point_id");
							 String pointname=(String)point_bean.get("pointname");
							 
							 if("true".equalsIgnoreCase(signLogo))
							 {
								 if(pointname.length()<m)
								 {
									 for(int k=pointname.length();k<=m;k++)
									 {
										 pointname = pointname+" ";
									 }
								 }
							 }
							 if("41".equals(chart_type)) {
                                 pointname=this.warpRowStr(pointname,25);
                             } else {
                                 pointname=this.warpRowStr(pointname,6);
                             }
							 
							 //5.0以上版本
							 if(this.userView.getVersion()>=50) {
                                 tempList.add(new CommonData("0",pointname));
                             } else {
                                 tempList.add(new CommonData("0",point_id));
                             }
						 }
						 if("null".equals(controlStr)||(","+controlStr+",").indexOf(","+know_id+",")!=-1)
						 {
							 flagScore = true;
							 dataMap.put(name,tempList);
						 }
					 }
				 }
				 rowSet=dao.search("select * from per_result_"+planid+" where  object_id='"+object_id+"' ");
				 int y=0;
				 if(rowSet.next())
				 {
					 ArrayList tempList=new ArrayList();
					 
					 float minScore = 0;  //  单条线的最低值
					 float maxScore = 0;  //  单条线的最高值
					 
					 for(int i=0;i<pointList.size();i++)
					 {
						 point_bean=(LazyDynaBean)pointList.get(i);
						 String point_id=(String)point_bean.get("point_id");
						 String pointname=(String)point_bean.get("pointname");
						 
						 if("true".equalsIgnoreCase(signLogo))
						 {
							 if(pointname.length()<m)
							 {
								 for(int k=pointname.length();k<=m;k++)
								 {
									 pointname = pointname+" ";
								 }
							 }
						 }
						 if("41".equals(chart_type)) {
                             pointname=this.warpRowStr(pointname,25);
                         } else {
                             pointname=this.warpRowStr(pointname,6);
                         }
						 
//						 tempList.add(new CommonData(rowSet.getString("C_"+point_id)!=null?rowSet.getString("C_"+point_id):"0",point_id));
						 
						 String score = rowSet.getString("C_"+point_id)!=null?rowSet.getString("C_"+point_id):"0";
						 if(i==0)
						 {
							 minScore = Float.parseFloat(score);
							 maxScore = Float.parseFloat(score);	
						 }
						 
						 Float theScore = (Float)map.get(point_id.toLowerCase());
						 if("1".equals(isShowPercentVal) && theScore!=null && theScore.floatValue()>0)
						 {
							 float x = Float.parseFloat(score)*100/theScore.floatValue();
							 score = Float.toString(x);
						 }	
						 
						 if(minScore > Float.parseFloat(score)) {
                             minScore = Float.parseFloat(score);
                         }
						 if(maxScore < Float.parseFloat(score)) {
                             maxScore = Float.parseFloat(score);
                         }
						 
						 //5.0以上版本
						 if(this.userView.getVersion()>=50) {
                             tempList.add(new CommonData(score,pointname));
                         } else {
                             tempList.add(new CommonData(score,point_id));
                         }
					 }
					 if(y==0)
					 {
						 if(minRadarScore==0) {
                             minRadarScore = minScore;
                         } else
						 {
							 if(minRadarScore > minScore) {
                                 minRadarScore = minScore;
                             }
						 }
						 if(maxRadarScore==0 || maxRadarScore==0.0) {
                             maxRadarScore = maxScore;
                         } else
						 {													 
							 if(maxRadarScore < maxScore) {
                                 maxRadarScore = maxScore;
                             }
						 }
						 
					 }else
					 {
						 if(minRadarScore > minScore) {
                             minRadarScore = minScore;
                         }
						 if(maxRadarScore < maxScore) {
                             maxRadarScore = maxScore;
                         }
					 }
					 y++;
					 
					 if("null".equals(controlStr)||(","+controlStr+",").indexOf(",-1,")!=-1) {
                         dataMap.put(ResourceFactory.getProperty("jx.param.zh"),tempList);
                     }
				 }
			 }
			 if(minRadarScore>=maxRadarScore || flagScore) {
                 minRadarScore=0;
             }
			 if(maxRadarScore==0 || maxRadarScore==0.0) {
                 maxRadarScore=5;
             }
			 if("41".equals(chart_type))
			 {
//				 dataMap.put("minmax",minRadarScore+","+maxRadarScore);
				 dataMap.put("minmax",0+","+maxRadarScore);
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return dataMap;
	 }
	 
	 
	 public String getMultipleContrastStr(String planid,String objects,String selectids)
	 {
		 StringBuffer str=new StringBuffer("");
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 RowSet rowSet=null;
			 StringBuffer object_ids=new StringBuffer("");
			 String[] temps=objects.split(",");
			 for(int i=0;i<temps.length;i++)
			 {
				if(temps[i].length()>0) {
                    object_ids.append(",'"+temps[i].substring(3)+"'");
                }
			 }
			 String sql="select a0101 from per_result_"+planid+" where object_id  in ("+object_ids.substring(1)+") ";
			 rowSet=dao.search(sql);
			 while(rowSet.next())
			 {
				 str.append(","+rowSet.getString(1)+"~"+rowSet.getString(1));
				 if("null".equals(selectids)||(","+selectids+",").indexOf(","+rowSet.getString(1)+",")!=-1) {
                     str.append("~1");
                 } else {
                     str.append("~0");
                 }
			 }
			 
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 
		return  str.substring(1);
	 }
	 
	 /**
	  * 取得多人对比分析数据
	  * @param planid
	  * @param a0100
	  * @return
	  */
	 public ArrayList getMultipleContrastAnalyseList(String planid,String objects,ArrayList pointToNameList,String selectids,String isShowPercentVal)
	 {
		 ArrayList dataList=new ArrayList();
		 HashMap dataMap=new HashMap();
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 
			 String status = "0";//0-分值模板	  1-权重模板
			 String sql = "select * from per_template where template_id = (select template_id from per_plan where plan_id="+planid+")";
			 RowSet rowSet=dao.search(sql);
			 if(rowSet.next()) {
                 status = rowSet.getString("status");
             }
			 
			 //取得指标的总分
			 HashMap map = new HashMap();
			 sql = "select * from per_template_point where item_id in (select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+"))";
			 rowSet=dao.search(sql);
			 while(rowSet.next())
			 {
				 String pointid = rowSet.getString("point_id");
				 float score = rowSet.getFloat("score");
				 map.put(pointid.toLowerCase(), new Float(score));
			 }
			 
			 			 
			 ///////////////--------- start ----------/////////////////
			  sql="select po.point_id,po.pointname from per_template_item pi,per_template_point pp,per_point po,per_plan "
				 	   +" where pi.item_id=pp.item_id and pp.point_id=po.point_id  and pi.template_id=per_plan.template_id and per_plan.plan_id="+planid+" order by pi.seq,pp.seq";
			 rowSet=dao.search(sql);
			 ArrayList list0=new ArrayList(); 
			 while(rowSet.next())
			 {
				 String point_id=rowSet.getString("point_id");
				 list0.add("C_"+point_id);
				
			 }
			 ///共享项目
			 rowSet=dao.search("select pi.* from per_template_item pi,per_plan where per_plan.template_id=pi.template_id and pi.kind=2 and per_plan.plan_id="+planid);
			 ArrayList itemList=new ArrayList();
			 while(rowSet.next())
			 {
				 list0.add("T_"+rowSet.getString("item_id"));
				 
				 String item_id = rowSet.getString("item_id");
				 float score = rowSet.getFloat("score");
				 map.put(item_id, new Float(score));	
			 }
			 
			 
			 StringBuffer object_ids=new StringBuffer("");
			 String[] temps=objects.split(",");
			 for(int i=0;i<temps.length;i++)
			 {
				if(temps[i].length()>0) {
                    object_ids.append(",'"+temps[i].substring(3)+"'");
                }
			 }
			 sql="select * from per_result_"+planid+" where object_id  in ("+object_ids.substring(1)+") ";
			 rowSet=dao.search(sql);
			 ArrayList objList=new ArrayList();
			 ArrayList perGradeCompetenceList = getPerGradeCompetenceList();
			 while(rowSet.next())
			 {
				 ArrayList list=new ArrayList();
				 LazyDynaBean abean=new LazyDynaBean();
				 for(int j=0;j<list0.size();j++)
				 {
//					 String id=(String)list0.get(j);
//					 abean.set(id.substring(2),rowSet.getString(id)!=null?rowSet.getString(id):"0");					 
					 
					 String a_value=(String)list0.get(j);
					 String pointid = a_value.substring(2);
					 String score = rowSet.getString(a_value)!=null?rowSet.getString(a_value):"0";
					 Float theScore = (Float)map.get(pointid.toLowerCase());
					 //liuy 2015-12-04
					 int level = 0;
					 if("2".equals(isShowPercentVal)){
						 float tempScore = Float.parseFloat(score);
						 level = getLevel(tempScore, theScore, perGradeCompetenceList, getDomainFlag(planid));
						 abean.set(pointid, level+"");
					 }else {
						 if("1".equals(isShowPercentVal) && theScore!=null && theScore.floatValue()>0)
						 {
							 float x = Float.parseFloat(score)*100/theScore.floatValue();
							 score = Float.toString(x);
						 }						 
						 abean.set(pointid,score);
					 }//liuy 2015-12-04
				 }
				 dataMap.put(rowSet.getString("a0101"),abean);
				 objList.add(rowSet.getString("a0101"));
			 }
			 
			 String signLogo = "false";
			 int n = 0;
			 int m = 0;					 
			 for(int k=0;k<pointToNameList.size();k++)
			 {
				 CommonData data=(CommonData)pointToNameList.get(k);
				 String pointname=data.getDataValue();			 				 
				 n = pointname.length();
				 if(k==0) {
                     m = pointname.length();
                 }
				 if(n>m) {
                     m = n;
                 }
				 if(k==pointToNameList.size()-1)
				 {
					 if(n==m) {
                         signLogo = "true";
                     }
				 }
			 }			 
			 
			 ArrayList tempList=null;
			 for(int i=0;i<pointToNameList.size();i++)
			 {
				 CommonData data=(CommonData)pointToNameList.get(i);				
				 String categoryName=data.getDataValue();
				 //5.0以上版本
					if(this.userView.getVersion()>=50)
					{
						categoryName =data.getDataName();
						
						if("true".equalsIgnoreCase(signLogo))
						{
							if(categoryName.length()<m)
							{
								for(int k=categoryName.length();k<=m;k++)
								{
									categoryName = categoryName+" ";
								}
							}
						}
						
						categoryName = this.warpRowStr(categoryName,6);
					}
				 tempList=new ArrayList();
				 for(int j=0;j<objList.size();j++)
				 {
					 String obj=(String)objList.get(j);
					 LazyDynaBean abean=(LazyDynaBean)dataMap.get(obj);
					 if(selectids.trim().length()==0 || "null".equals(selectids)||(","+selectids+",").indexOf(","+obj+",")!=-1) {
                         tempList.add(new CommonData((String)abean.get(data.getDataValue()),obj));
                     }
				 }
				 LazyDynaBean abean=new LazyDynaBean();
				 abean.set("categoryName", categoryName);
				 abean.set("dataList",tempList);
				 dataList.add(abean);
				 
			 }
			 
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return dataList;
	 }
	 
	 /**
	  * 取得多人对比分析数据 按素质模型  用于柱状图
	  * @param planid
	  * @param a0100 pointToNameList共同指标
	  * @return
	  */
	 public ArrayList getMultipleContrastAnalyseListByModel(String planid,String objects,ArrayList pointToNameList,String selectids,String isShowPercentVal)
	 {
		 ArrayList dataList=new ArrayList();
		 HashMap dataMap=new HashMap();
		 ArrayList objList=new ArrayList();
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 String sql = "";
			 RowSet rowSet=null;
			 String[] temps=objects.split(",");
			 String objectid="";
			 String a0101="";
			 //liuy 2015-12-04//取得指标的总分
			 HashMap map = new HashMap();
			 sql = "select point_id,score from per_competency_modal where object_id=(select e01a1 from "+ temps[0].substring(0,3) +"a01 where a0100 ='"+ temps[0].substring(3) +"')";
			 rowSet=dao.search(sql);
			 while(rowSet.next())
			 {
				 String pointid = rowSet.getString("point_id");
				 float score = rowSet.getFloat("score");
				 map.put(pointid.toLowerCase(), new Float(score));
			 }//liuy 2015-12-04
			 for(int i=0;i<temps.length;i++){
				 if(temps[i].length()>3){
					 objectid+="'"+temps[i].substring(3)+"',";
				 }

			 }
			 objectid=objectid.substring(0,objectid.length()-1);
			 sql="select * from per_history_result where object_id in("+objectid+")  and plan_id='"+planid+"' and status='0'";
			 rowSet=dao.search(sql);
			 LazyDynaBean bean=new LazyDynaBean();
			 ArrayList perGradeCompetenceList = getPerGradeCompetenceList();
			 while(rowSet.next())
			 {  
				 String pointid=rowSet.getString("point_id");
				 String score=rowSet.getString("score");
				 Float theScore = (Float)map.get(pointid.toLowerCase());
				 if(!"".equals(a0101)&&!a0101.equalsIgnoreCase(rowSet.getString("a0101"))){
					 dataMap.put(a0101,bean);
					 objList.add(a0101);
					 bean=new LazyDynaBean();
				 }
				 a0101=rowSet.getString("a0101");
				 //liuy 2015-12-04
				 int level = 0;
				 if("2".equals(isShowPercentVal)){
					 float tempScore = Float.parseFloat(score);
					 level = getLevel(tempScore, theScore, perGradeCompetenceList, getDomainFlag(planid));
					 bean.set(pointid, level+"");
				 }else {
                     bean.set(pointid,score);
                 }
				 //liuy 2015-12-04
				
			 }
			 if(dataMap.get(a0101)==null){
				 dataMap.put(a0101,bean);
				 objList.add(a0101);
				 
			 }
			 String signLogo = "false";
			 int n = 0;
			 int m = 0;					 
			 for(int k=0;k<pointToNameList.size();k++)
			 {
				 CommonData data=(CommonData)pointToNameList.get(k);
				 String pointname=data.getDataValue();			 				 
				 n = pointname.length();
				 if(k==0) {
                     m = pointname.length();
                 }
				 if(n>m) {
                     m = n;
                 }
				 if(k==pointToNameList.size()-1)
				 {
					 if(n==m) {
                         signLogo = "true";
                     }
				 }
			 }			 
			 
			 ArrayList tempList=null;
			 for(int i=0;i<pointToNameList.size();i++)
			 {
				 CommonData data=(CommonData)pointToNameList.get(i);				
				 String categoryName=data.getDataValue();
				 //5.0以上版本
					if(this.userView.getVersion()>=50)
					{
						categoryName =data.getDataName();
						
						if("true".equalsIgnoreCase(signLogo))
						{
							if(categoryName.length()<m)
							{
								for(int k=categoryName.length();k<=m;k++)
								{
									categoryName = categoryName+" ";
								}
							}
						}
						
						categoryName = this.warpRowStr(categoryName,6);
					}
				 tempList=new ArrayList();
				 for(int j=0;j<objList.size();j++)
				 {
					 String obj=(String)objList.get(j);
					 LazyDynaBean abean=(LazyDynaBean)dataMap.get(obj);
					 if(selectids.trim().length()==0 || "null".equals(selectids)||(","+selectids+",").indexOf(","+obj+",")!=-1) {
                         tempList.add(new CommonData((String)abean.get(data.getDataValue()),obj));
                     }
				 }
				 LazyDynaBean abean=new LazyDynaBean();
				 abean.set("categoryName", categoryName);
				 abean.set("dataList",tempList);
				 dataList.add(abean);
				 
			 }
			 
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return dataList;
	 }
	 
	 /**
	  * 取得多人对比分析数据
	  * @param planid
	  * @param a0100
	  * @return
	  */
	 public HashMap getMultipleContrastAnalyse(String planid,String objects,ArrayList pointToNameList,String selectids,String isShowPercentVal,String chart_type)
	 {
		 HashMap dataMap=new HashMap();
		 HashMap pointToNameMap=new HashMap();
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 
			 String status = "0";//0-分值模板	  1-权重模板
			 String sql = "select * from per_template where template_id = (select template_id from per_plan where plan_id="+planid+")";
			 RowSet rowSet=dao.search(sql);
			 if(rowSet.next()) {
                 status = rowSet.getString("status");
             }
			 
			 //取得指标的总分
			 HashMap map = new HashMap();
			 sql = "select * from per_template_point where item_id in (select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+"))";
			 rowSet=dao.search(sql);
			 while(rowSet.next())
			 {
				 String pointid = rowSet.getString("point_id");
				 float score = rowSet.getFloat("score");
				 map.put(pointid.toLowerCase(), new Float(score));
			 }			 
			 
			 sql="select po.point_id,po.pointname from per_template_item pi,per_template_point pp,per_point po,per_plan "
				 	   +" where pi.item_id=pp.item_id and pp.point_id=po.point_id  and pi.template_id=per_plan.template_id and per_plan.plan_id="+planid+" order by pi.seq,pp.seq";
			 rowSet=dao.search(sql);
			 ArrayList list0=new ArrayList(); 
			 while(rowSet.next())
			 {
				 String point_id=rowSet.getString("point_id");
				 list0.add("C_"+point_id);
				 pointToNameList.add(new CommonData(point_id,rowSet.getString("pointname")));
				 pointToNameMap.put("C_"+point_id,rowSet.getString("pointname"));
			 }
			 ///共享项目
			 rowSet=dao.search("select pi.* from per_template_item pi,per_plan where per_plan.template_id=pi.template_id and pi.kind=2 and per_plan.plan_id="+planid);
			 ArrayList itemList=new ArrayList();
			 while(rowSet.next())
			 {
				 LazyDynaBean abean=new LazyDynaBean();
				 abean.set("item_id", rowSet.getString("item_id"));
				 abean.set("itemdesc",rowSet.getString("itemdesc"));
				 abean.set("score",rowSet.getString("score")!=null?rowSet.getString("score"):"0");
				 abean.set("rank", rowSet.getString("rank")!=null?rowSet.getString("rank"):"0");
				 itemList.add(abean);
				 
				 String item_id = rowSet.getString("item_id");
				 float score = rowSet.getFloat("score");
				 map.put(item_id, new Float(score));		
			 }
			 for(int i=0;i<itemList.size();i++)
			 {
				 LazyDynaBean abean=(LazyDynaBean)itemList.get(i);
				 String item_id=(String)abean.get("item_id");
				 String itemdesc=(String)abean.get("itemdesc");
				 pointToNameList.add(new CommonData(item_id,itemdesc));
				 list0.add("T_"+item_id);
				 pointToNameMap.put("T_"+item_id,itemdesc);
			 }
			 
			 String signLogo = "false";
			 int n = 0;
			 int m = 0;					 
			 for(int i=0;i<list0.size();i++)
			 {
				 String a_value=(String)list0.get(i);	
				 String pointname = (String)pointToNameMap.get(a_value);				 
				 n = pointname.length();
				 if(i==0) {
                     m = pointname.length();
                 }
				 if(n>m) {
                     m = n;
                 }
				 if(i==list0.size()-1)
				 {
					 if(n==m) {
                         signLogo = "true";
                     }
				 }
			 }
			 
			 StringBuffer object_ids=new StringBuffer("");
			 String[] temps=objects.split(",");
			 for(int i=0;i<temps.length;i++)
			 {
				if(temps[i].length()>0) {
                    object_ids.append(",'"+temps[i].substring(3)+"'");
                }
			 }
			 sql="select * from per_result_"+planid+" where object_id  in ("+object_ids.substring(1)+") ";
			 rowSet=dao.search(sql);
			 int i=0;
			 float minRadarScore = 0;  //  所有线的最低值
			 float maxRadarScore = 0;  //  所有线的最高值	
			 //liuy 2015-12-04
			 int maxLevel = 0;//最大级别
			 ArrayList perGradeCompetenceList = getPerGradeCompetenceList();
			 maxLevel = perGradeCompetenceList.size();
			 //liuy 2015-12-04
			 while(rowSet.next())
			 {
				 ArrayList list=new ArrayList();
				 float minScore = 0;  //  单条线的最低值
				 float maxScore = 0;  //  单条线的最高值
				 
				 for(int j=0;j<list0.size();j++)
				 {
//					 String a_value=(String)list0.get(j);
//					 list.add(new CommonData(rowSet.getString(a_value)!=null?rowSet.getString(a_value):"0",a_value.substring(2)));
					 
					 String a_value=(String)list0.get(j);
					 String pointid = a_value.substring(2);
					 String score = rowSet.getString(a_value)!=null?rowSet.getString(a_value):"0";
					 int level = 0;//级别
					 
					 if(j==0)
					 {
						 minScore = Float.parseFloat(score);
						 maxScore = Float.parseFloat(score);	
					 }
					 Float theScore = (Float)map.get(pointid.toLowerCase());
//					 if(isShowPercentVal.equals("1") && status.equals("0")&& theScore!=null && theScore.floatValue()>0)
					 if("1".equals(isShowPercentVal) && theScore!=null && theScore.floatValue()>0)//不管模板类型了
					 {
						 float x = Float.parseFloat(score)*100/theScore.floatValue();
						 score = Float.toString(x);
					 //liuy 2015-12-04
					 }else if("2".equals(isShowPercentVal)){
						 float tempScore = Float.parseFloat(score);
						 level = getLevel(tempScore, theScore, perGradeCompetenceList, getDomainFlag(planid));
					 }//liuy 2015-12-04
					 if(minScore > Float.parseFloat(score)) {
                         minScore = Float.parseFloat(score);
                     }
					 if(maxScore < Float.parseFloat(score)) {
                         maxScore = Float.parseFloat(score);
                     }
					 
						//5.0以上版本
						if(this.userView.getVersion()>=50)
						{
							String name = (String)pointToNameMap.get(a_value);
							
							if("true".equalsIgnoreCase(signLogo))
							{
								if(name.length()<m)
								{
									for(int k=name.length();k<=m;k++)
									{
										name = name+" ";
									}
								}
							}
							if("41".equals(chart_type)) {
                                name=this.warpRowStr(name,25);
                            } else {
                                name=this.warpRowStr(name,6);
                            }
							
							//liuy 2015-12-04
							if("2".equals(isShowPercentVal)) {
                                list.add(new CommonData(Integer.toString(level),name));
                            } else {
                                list.add(new CommonData(score,name));
                            }
						}else
							if("2".equals(isShowPercentVal)) {
                                list.add(new CommonData(Integer.toString(level),pointid));
                            } else {
                                list.add(new CommonData(score,pointid));
                            }
							//liuy 2015-12-04
				 }
				 if(i==0)
				 {
					 if(minRadarScore==0) {
                         minRadarScore = minScore;
                     } else
					 {
						 if(minRadarScore > minScore) {
                             minRadarScore = minScore;
                         }
					 }
					 if(maxRadarScore==0 || maxRadarScore==0.0) {
                         maxRadarScore = maxScore;
                     } else
					 {													 
						 if(maxRadarScore < maxScore) {
                             maxRadarScore = maxScore;
                         }
					 }					 
				 }else
				 {
					 if(minRadarScore > minScore) {
                         minRadarScore = minScore;
                     }
						 
					 if(maxRadarScore < maxScore) {
                         maxRadarScore = maxScore;
                     }
				 }
				 i++;
				 
				if(selectids.trim().length()==0 || "null".equalsIgnoreCase(selectids)||(","+selectids+",").indexOf(","+rowSet.getString("a0101")+",")!=-1) {
                    dataMap.put(rowSet.getString("a0101"),list);
                }
			 }
			 if(minRadarScore>=maxRadarScore) {
                 minRadarScore=0;
             }
			 if(maxRadarScore==0 || maxRadarScore==0.0) {
                 maxRadarScore=5;
             }
			 //liuy 2015-12-04
			 if(maxLevel<4 || maxLevel<4.0) {
                 maxLevel=4;
             }
			 if("41".equals(chart_type))
			 {
				 if("2".equals(isShowPercentVal)) {
                     dataMap.put("minmax",0+","+maxLevel);
                 } else{
					 //dataMap.put("minmax",minRadarScore+","+maxRadarScore);
					 dataMap.put("minmax",0+","+maxRadarScore);
				 }
			 }//liuy 2015-12-04
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return dataMap;
	 }
	 
	 /**
	  * 取得多人对比分析数据 按岗位素质模型测评
	  * @param planid
	  * @param a0100
	  * @return
	  */
	 public HashMap getMultipleContrastAnalyseByModel(String planid,String objects,ArrayList pointToNameList,String selectids,String isShowPercentVal,String chart_type)throws GeneralException
	 {
		 HashMap dataMap=new HashMap();
		 HashMap pointToNameMap=new HashMap();
		 String[] temps=objects.split(",");
		 String  objectid="";
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 String sql = "";
			 /**取得多人对比分析指标   取共同指标**/
			 for(int i=0;i<temps.length;i++)
			 {
				if(temps[i].length()>0){
					objectid=temps[i].substring(3);
				}
				if(i==0){
					sql="select  per_history_result"+i+".point_id from per_history_result per_history_result"+i+" where ";
					sql+="  per_history_result"+i+".plan_id='"+planid+"' and per_history_result"+i+".object_id='"+temps[0].substring(3)+"'  and status='0' ";
				}else{
					sql+=" and EXISTS(select  per_history_result"+i+".point_id from per_history_result  per_history_result"+i+" where plan_id='"+planid+"' and object_id='"+objectid+"' and status='0' "+
					"and per_history_result"+i+".point_id= per_history_result0.point_id )";
				}
			 }
			 sql="select point_id ,pointname from per_point where point_id in ( "+sql+" )";
			 RowSet rowSet=dao.search(sql);
			 String condition="";
			 String orderCondition="";
			 ArrayList list0=new ArrayList(); 
			 while(rowSet.next())
			 {
				 String point_id = rowSet.getString("point_id");
				 String pointname = rowSet.getString("pointname");
				 list0.add(point_id);
				 pointToNameList.add(new CommonData(point_id,pointname));
				 pointToNameMap.put(point_id,pointname);
				 condition+="'"+point_id+"',";
				 orderCondition+=point_id+",";
			 }
			 //liuy 2015-12-04//取得指标的总分
			 HashMap map = new HashMap();
			 sql = "select point_id,score from per_competency_modal where object_id=(select e01a1 from "+ temps[0].substring(0,3) +"a01 where a0100 ='"+ temps[0].substring(3) +"')";
			 rowSet=dao.search(sql);
			 while(rowSet.next())
			 {
				 String pointid = rowSet.getString("point_id");
				 float score = rowSet.getFloat("score");
				 map.put(pointid.toLowerCase(), new Float(score));
			 }//liuy 2015-12-04	
			 if(condition.length()>0) {
                 condition=condition.substring(0, condition.length()-1);
             } else {
                 throw GeneralExceptionHandler.Handle(new Exception("所选对象没有共同的岗位素质模型指标!"));
             }
			 String signLogo = "false";
			 int n = 0;
			 int m = 0;					 
			 for(int i=0;i<list0.size();i++)
			 {
				 String a_value=(String)list0.get(i);	
				 String pointname = (String)pointToNameMap.get(a_value);				 
				 n = pointname.length();
				 if(i==0) {
                     m = pointname.length();
                 }
				 if(n>m) {
                     m = n;
                 }
				 if(i==list0.size()-1)
				 {
					 if(n==m) {
                         signLogo = "true";
                     }
				 }
			 }
			 
			 float minRadarScore = 0;  //  所有线的最低值
			 float maxRadarScore = 0;  //  所有线的最高值	
			 float minScore = 0;  //  单条线的最低值
			 float maxScore = 0;  //  单条线的最高值
			 //liuy 2015-12-04
			 int maxLevel = 0;//最大级别
			 ArrayList perGradeCompetenceList = getPerGradeCompetenceList();
			 maxLevel = perGradeCompetenceList.size();
			 //liuy 2015-12-04
			 for(int p=0;p<temps.length;p++)
			 {
				if(temps[p].length()>0){
					objectid=temps[p].substring(3);
				}
				 /**********不同数据库 in排序***********/
				 switch (Sql_switcher.searchDbServer()) {
					case Constant.MSSQL:
						 sql="select * from per_history_result where object_id='"+objectid+"'  and plan_id='"+planid+"' and point_id in("+condition+")   order by charindex(','+point_id+',',',"+orderCondition+",') ";//本人得分
						 break;
					case Constant.DB2:
						 sql="select * from per_history_result where object_id='"+objectid+"'  and plan_id='"+planid+"' and point_id in("+condition+")   order by  instr('"+orderCondition.substring(0, orderCondition.length()-1)+"',point_id) ";//本人得分
						 break;
					case Constant.ORACEL:
						sql="select * from per_history_result where object_id='"+objectid+"'  and plan_id='"+planid+"' and point_id in("+condition+")    order by instr('"+orderCondition.substring(0, orderCondition.length()-1)+"',point_id) ";//本人得分
						 break;
					default://mysql
						sql="select * from per_history_result where object_id='"+objectid+"'  and plan_id='"+planid+"' and point_id in("+condition+")    order by  instr(',"+orderCondition+"',CONCAT(',',point_id,',')) ";//本人得分
						 break;
				 }
				 int i=0;
				 rowSet=dao.search(sql);
				 ArrayList list=new ArrayList(); 
				 String a0101="";
				 while(rowSet.next())
				 {
					 String pointid = rowSet.getString("point_id")!=null?rowSet.getString("point_id"):"0";
					 String score = rowSet.getString("score")!=null?rowSet.getString("score"):"0";
					 a0101= rowSet.getString("a0101")!=null?rowSet.getString("a0101"):"";
					 //liuy 2015-12-04
					 int level = 0;//级别
					 Float theScore = (Float)map.get(pointid.toLowerCase());
					 if("2".equals(isShowPercentVal)){
						 float tempScore = Float.parseFloat(score);
						 level = getLevel(tempScore, theScore, perGradeCompetenceList, getDomainFlag(planid));
					 }//liuy 2015-12-04
					 if(condition.indexOf(pointid)==-1){
						 continue;
					 }
					 if(pointid==null|| "".equals(pointid)){
						 continue;
					 }
					 if(condition.indexOf(pointid)==-1){
						 continue;
					 }
					 if(pointid==null|| "".equals(pointid)){
						 continue;
					 }
						 minScore = Float.parseFloat(score);
						 maxScore = Float.parseFloat(score);	

					 if(minScore > Float.parseFloat(score)) {
                         minScore = Float.parseFloat(score);
                     }
					 if(maxScore < Float.parseFloat(score)) {
                         maxScore = Float.parseFloat(score);
                     }
					 
						//5.0以上版本
						if(this.userView.getVersion()>=50)
						{
							String name = (String)pointToNameMap.get(pointid);
							
							if("true".equalsIgnoreCase(signLogo))
							{
								if(name.length()<m)
								{
									for(int k=name.length();k<=m;k++)
									{
										name = name+" ";
									}
								}
							}
							if("41".equals(chart_type)) {
                                name=this.warpRowStr(name,25);
                            } else {
                                name=this.warpRowStr(name,6);
                            }
							
							//liuy 2015-12-04
							if("2".equals(isShowPercentVal)) {
                                list.add(new CommonData(Integer.toString(level),name));
                            } else {
                                list.add(new CommonData(score,name));
                            }
						}else
							if("2".equals(isShowPercentVal)) {
                                list.add(new CommonData(Integer.toString(level),pointid));
                            } else {
                                list.add(new CommonData(score,pointid));
                            }
							//liuy 2015-12-04
				 if(i==0)
				 {
					 if(minRadarScore==0) {
                         minRadarScore = minScore;
                     } else
					 {
						 if(minRadarScore > minScore) {
                             minRadarScore = minScore;
                         }
					 }
					 if(maxRadarScore==0 || maxRadarScore==0.0) {
                         maxRadarScore = maxScore;
                     } else
					 {													 
						 if(maxRadarScore < maxScore) {
                             maxRadarScore = maxScore;
                         }
					 }					 
				 }else
				 {
					 if(minRadarScore > minScore) {
                         minRadarScore = minScore;
                     }
						 
					 if(maxRadarScore < maxScore) {
                         maxRadarScore = maxScore;
                     }
				 }
				 i++;
			 	}
				if(selectids.trim().length()==0 || "null".equalsIgnoreCase(selectids)||(","+selectids+",").indexOf(","+rowSet.getString("a0101")+",")!=-1) {
                    dataMap.put(a0101,list);
                }
			 }
			
			 if(minRadarScore>=maxRadarScore) {
                 minRadarScore=0;
             }
			 if(maxRadarScore==0 || maxRadarScore==0.0) {
                 maxRadarScore=5;
             }
			 if("41".equals(chart_type))
			 {
				 //liuy 2015-12-04
				 if("2".equals(isShowPercentVal)) {
                     dataMap.put("minmax",0+","+maxLevel);
                 } else{
					 //dataMap.put("minmax",minRadarScore+","+maxRadarScore);
					 dataMap.put("minmax",0+","+maxRadarScore);
				 }//liuy 2015-12-04
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
		 return dataMap;
	 }
	 	 
	 /**
	  * 取得 单人对比分析 分值序列str
	  * @param planIds
	  * @param a0100
	  * @param selectids
	  * @return
	  */
	 public String getSingleContrastStr(String planIds,String a0100,String selectids,String busitype)
	 {
		 StringBuffer str=new StringBuffer(",");
		 if(planIds.length()==0) {
             return "";
         }
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 
			 str.append(ResourceFactory.getProperty("lable.examine.hightestScore")+"~"+ResourceFactory.getProperty("lable.examine.hightestScore"));
			 if(selectids.trim().length()==0 || "null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.hightestScore")+",")!=-1) {
                 str.append("~1");
             } else {
                 str.append("~0");
             }
			 str.append(","+ResourceFactory.getProperty("lable.examine.avgScore")+"~"+ResourceFactory.getProperty("lable.examine.avgScore"));
			 if(selectids.trim().length()==0 || "null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.avgScore")+",")!=-1) {
                 str.append("~1");
             } else {
                 str.append("~0");
             }
		
			 RowSet rowSet=dao.search("select * from per_object where plan_id="+planIds+" and object_id='"+a0100+"'");
			 if(rowSet.next())
			 {	str.append(","+ResourceFactory.getProperty("lable.examine.selfscore")+"~"+ResourceFactory.getProperty("lable.examine.selfscore"));
//			 	str.append(rowSet.getString("a0101")+"~"+rowSet.getString("a0101"));
//			 	if(selectids.equals("null")||(","+selectids+",").indexOf(","+rowSet.getString("a0101")+",")!=-1)
			 	if(selectids.trim().length()==0 || "null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.selfscore")+",")!=-1) {
                    str.append("~1");
                } else {
                    str.append("~0");
                }
			 }
			 
			 //增加最低分
			 str.append(","+ResourceFactory.getProperty("lable.examine.lowestscore")+"~"+ResourceFactory.getProperty("lable.examine.lowestscore"));
//			 if(selectids.equals("null")||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.lowestscore")+",")!=-1)
			 if((","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.lowestscore")+",")!=-1) {
                 str.append("~1");
             } else {
                 str.append("~0");
             }
			 
			 // 增加岗位要求
			 if(busitype!=null && busitype.trim().length()>0 && "1".equals(busitype))
			 {
				 str.append(","+ResourceFactory.getProperty("lable.examine.e01a1Score")+"~"+ResourceFactory.getProperty("lable.examine.e01a1Score"));
				 if(selectids.trim().length()==0 || "null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.e01a1Score")+",")!=-1) {
                     str.append("~1");
                 } else {
                     str.append("~0");
                 }
			 }
			 
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 
		 return str.substring(1);
	 }
	 
	 
	 /**
	  * 取得单人对比分析数据(用于分组柱状图)
	  * @param planid
	  * @param a0100
	  * @return
	  */
	 public ArrayList getSingleContrastAnalyseList(String planid,String a0100,ArrayList pointToNameList,String selectids,String isShowPercentVal,String busitype)
	 {
		 ArrayList dataList=new ArrayList();
		 if(planid.length()==0) {
             return dataList;
         }
		 HashMap dataMap=new HashMap();
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 
			 String status = "0";//0-分值模板	  1-权重模板
			 String sql = "select * from per_template where template_id = (select template_id from per_plan where plan_id="+planid+")";
			 RowSet rowSet=dao.search(sql);
			 if(rowSet.next()) {
                 status = rowSet.getString("status");
             }
			 
			 //取得指标的总分
			 HashMap map = new HashMap();
			 sql = "select * from per_template_point where item_id in (select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+"))";
			 rowSet=dao.search(sql);
			 while(rowSet.next())
			 {
				 String pointid = rowSet.getString("point_id");
				 float score = rowSet.getFloat("score");
				 map.put(pointid.toLowerCase(), new Float(score));
			 }			 
			 
			 sql="select po.point_id,po.pointname from per_template_item pi,per_template_point pp,per_point po,per_plan "
				 	   +" where pi.item_id=pp.item_id and pp.point_id=po.point_id  and pi.template_id=per_plan.template_id and per_plan.plan_id="+planid+" order by pi.seq,pp.seq";
			 rowSet=dao.search(sql);
			 StringBuffer sql0=new StringBuffer("");
			 StringBuffer sql1=new StringBuffer("");
			 StringBuffer sql2=new StringBuffer("");
			 StringBuffer sql3=new StringBuffer("");
			 ArrayList list0=new ArrayList();
			 while(rowSet.next())
			 {
				 String point_id=rowSet.getString("point_id");
				 sql0.append(",C_"+point_id);
				 sql2.append(",Max(C_"+point_id+") C_"+point_id);
				 sql1.append(",avg(C_"+point_id+") C_"+point_id);
				 sql3.append(",Min(C_"+point_id+") C_"+point_id);
				 list0.add("C_"+point_id);
			 }
			 
			 ///共享项目
			 rowSet=dao.search("select pi.* from per_template_item pi,per_plan where per_plan.template_id=pi.template_id and pi.kind=2 and per_plan.plan_id="+planid);
			 ArrayList itemList=new ArrayList();
			 while(rowSet.next())
			 {
				 LazyDynaBean abean=new LazyDynaBean();
				 abean.set("item_id", rowSet.getString("item_id"));
				 abean.set("itemdesc",rowSet.getString("itemdesc"));
				 abean.set("score",rowSet.getString("score")!=null?rowSet.getString("score"):"0");
				 abean.set("rank", rowSet.getString("rank")!=null?rowSet.getString("rank"):"0");
				 itemList.add(abean);
				 
				 String item_id = rowSet.getString("item_id");
				 float score = rowSet.getFloat("score");
				 map.put(item_id, new Float(score));	
			 }
			 for(int i=0;i<itemList.size();i++)
			 {
				 LazyDynaBean abean=(LazyDynaBean)itemList.get(i);
				 String item_id=(String)abean.get("item_id");
				 String itemdesc=(String)abean.get("itemdesc");
				 sql0.append(",T_"+item_id);
				 sql2.append(",Max(T_"+item_id+") T_"+item_id);
				 sql1.append(",avg(T_"+item_id+") T_"+item_id);
				 sql3.append(",Min(T_"+item_id+") T_"+item_id);
				 list0.add("T_"+item_id);
			 }
			 			 			 
			 
			 if(sql0.length()>0) {
				 sql="select "+sql0.substring(1)+" from per_result_"+planid+" where object_id='"+a0100+"' ";
			 }
			 if(sql1.length()>0) {
				 if(sql.length() > 0) {
					 sql+=" union all ";
				 }
				 sql+="select "+sql1.substring(1)+" from per_result_"+planid;
			 }
			 if(sql2.length()>0) {
				 if(sql.length() > 0) {
					 sql+=" union all ";
				 }
				 sql+="select "+sql2.substring(1)+" from per_result_"+planid;
			 }
			 if(sql3.length()>0) {
				 if(sql.length() > 0) {
					 sql+=" union all ";
				 }
				 sql+="select "+sql3.substring(1)+" from per_result_"+planid;
			 }
			 
			 String e01a1="";
			 String a0101="";
			 boolean isHasValue=false;
			 rowSet=dao.search("select a0101,e01a1 from per_result_"+planid+" where object_id='"+a0100+"'");
			 if(rowSet.next())
			 {
				 isHasValue=true;
				 a0101=rowSet.getString("a0101");
				 e01a1=rowSet.getString("e01a1");
			 }
			 
			 if(!isHasValue)
			 {
				 rowSet=dao.search("select a0101,e01a1 from per_object where plan_id="+planid+" and object_id='"+a0100+"'");
				 if(rowSet.next())
				 {
					 a0101=rowSet.getString("a0101");
					 e01a1=rowSet.getString("e01a1");
				 }
				 LazyDynaBean abean=new LazyDynaBean();
				 for(int j=0;j<list0.size();j++)
				 {
					 String id=(String)list0.get(j);
					 abean.set(id.substring(2),"0");
				 }
				 dataMap.put(a0101,abean);
			 }
			 
			 ArrayList perGradeCompetenceList = getPerGradeCompetenceList();
			 /**获得能力素质当前岗位各指标的标准分  JinChunhai 2011.12.21 */			
			 if(busitype!=null && busitype.trim().length()>0 && "1".equals(busitype))
			 {
				 HashMap pointScoreMap = getE01a1PointScore(e01a1,status,planid);
				 LazyDynaBean zbean=new LazyDynaBean();
				 for(int j=0;j<list0.size();j++)
				 {				 
					 String a_value=(String)list0.get(j);
					 String pointid = a_value.substring(2);				 
					 String score = (String)pointScoreMap.get(pointid);
					 Float theScore = (Float)map.get(pointid.toLowerCase());
					 int level = 0;//级别
					 
					 if(score==null || score.trim().length()<=0) {
                         score = String.valueOf(theScore);
                     }
					 //liuy 2015-12-04
					 if("2".equals(isShowPercentVal) && theScore!=null && theScore.floatValue()>0){
						 float tempScore = Float.parseFloat(score);
						 level = getLevel(tempScore, theScore, perGradeCompetenceList, getDomainFlag(planid));
						 zbean.set(pointid, level+"");
					 }else {
						 if("1".equals(isShowPercentVal) && theScore!=null && theScore.floatValue()>0)
						 {
							 float x = Float.parseFloat(score)*100/theScore.floatValue();
							 score = Float.toString(x);
						 }
						 zbean.set(pointid, score);				 
					 }//liuy 2015-12-04					 
				 }
				 dataMap.put(ResourceFactory.getProperty("lable.examine.e01a1Score"),zbean);
			 }
			 
			 rowSet=dao.search(sql);
			 int i=0;
			 while(rowSet.next())
			 {
				 LazyDynaBean abean=new LazyDynaBean();
				 for(int j=0;j<list0.size();j++)
				 {
//					 String id=(String)list0.get(j);
//					 abean.set(id.substring(2), rowSet.getString(id)!=null?rowSet.getString(id):"0");
					 
					 String a_value=(String)list0.get(j);
					 String pointid = a_value.substring(2);
					 String score = rowSet.getString(a_value)!=null?rowSet.getString(a_value):"0";
					 Float theScore = (Float)map.get(pointid.toLowerCase());
					 //liuy 2015-12-04
					 int level = 0;//级别
					 if("2".equals(isShowPercentVal) && theScore!=null && theScore.floatValue()>0){
						 float tempScore = Float.parseFloat(score);
						 level = getLevel(tempScore, theScore, perGradeCompetenceList, getDomainFlag(planid));
						 abean.set(pointid, level+"");
					 }else {
						 if("1".equals(isShowPercentVal) && theScore!=null && theScore.floatValue()>0)
						 {
							 float x = Float.parseFloat(score)*100/theScore.floatValue();
							 score = Float.toString(x);
						 }
						 abean.set(pointid, score);
					 }//liuy 2015-12-04					 
					 
				 }
				 if(isHasValue)
				 {
					 if(i==0)
					 {
						 dataMap.put(a0101,abean);
					 }
					 else if(i==1)
					 {
						 dataMap.put(ResourceFactory.getProperty("lable.examine.avgScore"),abean);
					 }
					 else if(i==2)
					 {
						 dataMap.put(ResourceFactory.getProperty("lable.examine.hightestScore"),abean);
					 }
					 else if(i==3)
					 {
						 dataMap.put(ResourceFactory.getProperty("lable.examine.lowestscore"),abean);
					 }
				 }
				 else
				 {
					 if(i==0)
					 {
						 dataMap.put(ResourceFactory.getProperty("lable.examine.avgScore"),abean);
					 }
					 else if(i==1)
					 {
						 dataMap.put(ResourceFactory.getProperty("lable.examine.hightestScore"),abean);
					 }
					 else if(i==2)
					 {
						 dataMap.put(ResourceFactory.getProperty("lable.examine.lowestscore"),abean);
					 }
				 }
				 i++;
			 }
			 			 
			 String signLogo = "false";
			 int n = 0;
			 int m = 0;					 
			 for(int k=0;k<pointToNameList.size();k++)
			 {
				 CommonData data=(CommonData)pointToNameList.get(k);
				 String pointname=data.getDataValue();			 				 
				 n = pointname.length();
				 if(k==0) {
                     m = pointname.length();
                 }
				 if(n>m) {
                     m = n;
                 }
				 if(k==pointToNameList.size()-1)
				 {
					 if(n==m) {
                         signLogo = "true";
                     }
				 }
			 }
			 
			 ArrayList tempList=new ArrayList();
			 for(int j=0;j<pointToNameList.size();j++)
			 {
				 CommonData data=(CommonData)pointToNameList.get(j);
				 String categoryName=data.getDataValue();
				 //5.0以上版本
				 if(this.userView.getVersion()>=50)
				 {
					categoryName =data.getDataName();
					
					if("true".equalsIgnoreCase(signLogo))
					{
						if(categoryName.length()<m)
						{
							for(int k=categoryName.length();k<=m;k++)
							{
								categoryName = categoryName+" ";
							}
						}
					}
					
					categoryName = this.warpRowStr(categoryName,6);
					
				 }
				 LazyDynaBean abean1=(LazyDynaBean)dataMap.get(a0101);
				 LazyDynaBean abean2=(LazyDynaBean)dataMap.get(ResourceFactory.getProperty("lable.examine.avgScore"));
				 LazyDynaBean abean3=(LazyDynaBean)dataMap.get(ResourceFactory.getProperty("lable.examine.hightestScore"));
				 LazyDynaBean abean4=(LazyDynaBean)dataMap.get(ResourceFactory.getProperty("lable.examine.lowestscore"));
				 
				 LazyDynaBean abean5 = new LazyDynaBean();
				 if(busitype!=null && busitype.trim().length()>0 && "1".equals(busitype)) {
                     abean5=(LazyDynaBean)dataMap.get(ResourceFactory.getProperty("lable.examine.e01a1Score"));
                 }
				 
				 tempList=new ArrayList();
//				 if(selectids.equals("null")||(","+selectids+",").indexOf(","+a0101+",")!=-1)
				 if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.selfscore")+",")!=-1) {
                     tempList.add(new CommonData((String)abean1.get(data.getDataValue()),a0101));
                 }
				 if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.avgScore")+",")!=-1) {
                     tempList.add(new CommonData((String)abean2.get(data.getDataValue()),ResourceFactory.getProperty("lable.examine.avgScore")));
                 }
				 if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.hightestScore")+",")!=-1) {
                     tempList.add(new CommonData((String)abean3.get(data.getDataValue()),ResourceFactory.getProperty("lable.examine.hightestScore")));
                 }
				 
//				 if(selectids.equals("null")||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.lowestscore")+",")!=-1)
				 if((","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.lowestscore")+",")!=-1) {
                     tempList.add(new CommonData((String)abean4.get(data.getDataValue()),ResourceFactory.getProperty("lable.examine.lowestscore")));
                 }
				 
				 if(busitype!=null && busitype.trim().length()>0 && "1".equals(busitype))
				 {
					 if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.e01a1Score")+",")!=-1) {
                         tempList.add(new CommonData((String)abean5.get(data.getDataValue()),ResourceFactory.getProperty("lable.examine.e01a1Score")));
                     }
				 }
				 
				 LazyDynaBean abean=new LazyDynaBean();
				 abean.set("categoryName", categoryName);
				 abean.set("dataList",tempList);
				 dataList.add(abean);
				 
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return dataList;
	 }
	 
	 /**
	  * 取得单人对比分析数据(用于分组柱状图) 按岗位素质模型
	  * @param planid
	  * @param a0100
	  * @return
	  */
	 public ArrayList getSingleContrastAnalyseListByModel(String planid,String a0100,ArrayList pointToNameList,String selectids,String isShowPercentVal,String busitype)
	 {
		 ArrayList dataList=new ArrayList();
		 if(planid.length()==0) {
             return dataList;
         }
		 HashMap dataMap=new HashMap();
		 HashMap pointNameMap=new HashMap();
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 
			 /****按岗位素质模型 指标均为按权重打分****/
			 String status = "1";//0-分值模板	  1-权重模板
			 String sql ="";
			 RowSet rowSet=null;
			 /**从历史表中找出要分析的指标**/
			 sql="select point_id ,pointname from per_point where point_id in(select point_id from per_history_result where plan_id='"+planid+"' and  object_id='"+a0100+"' and status='0') ";
//			 sql="select point_id ,pointname from per_point where point_id in (select point_id from per_competency_modal where object_type=3 and '"+sdf.format(date)+"' between start_date and end_date and object_id=(select e01a1 from usra01 where a0100='"+a0100+"'))";
			 rowSet=dao.search(sql);
			 ArrayList list0=new ArrayList(); 
			 String condition="";
			 String orderCondition="";
			 while(rowSet.next())
			 {
				 String point_id=rowSet.getString("point_id");
				 pointToNameList.add(new CommonData(point_id,rowSet.getString("pointname")));
				 pointNameMap.put(point_id,rowSet.getString("pointname"));
				 list0.add(point_id);
				 condition+="'"+point_id+"',";
				 orderCondition+=point_id+",";
			 }
			 condition=condition.substring(0, condition.length()-1);
	 
			 String sql_max="";
			 String sql_avg="";
			 String sql_min="";
			 /**********不同数据库 in排序***********/
			 switch (Sql_switcher.searchDbServer()) {
				case Constant.MSSQL:
					 sql_max="select point_id,max(score) score from per_history_result where point_id in("+condition+") and plan_id='"+planid+"' group by point_id  order by charindex(','+point_id+',',',"+orderCondition+",') ";//最大值
					 sql_avg="select point_id,avg(score) score from per_history_result where point_id in("+condition+")  and plan_id='"+planid+"'  group by point_id  order by charindex(','+point_id+',',',"+orderCondition+",') ";//平均值
					 sql="select point_id, score from per_history_result where object_id='"+a0100+"'  and plan_id='"+planid+"' and point_id in("+condition+")  and plan_id='"+planid+"'   order by charindex(','+point_id+',',',"+orderCondition+",') ";//本人得分
					 sql_min="select point_id,min(score) score from per_history_result where point_id in("+condition+") and plan_id='"+planid+"' group by point_id  order by charindex(','+point_id+',',',"+orderCondition+",') ";//最小值
					break;
				case Constant.DB2:
					 sql_max="select point_id,max(score) score from per_history_result where point_id in("+condition+") and plan_id='"+planid+"' group by point_id  order by  instr('"+orderCondition.substring(0, orderCondition.length()-1)+"',point_id) ";//最大值
					 sql_avg="select point_id,avg(score) score from per_history_result where point_id in("+condition+")  and plan_id='"+planid+"'  group by point_id  order by  instr('"+orderCondition.substring(0, orderCondition.length()-1)+"',point_id) ";//平均值
					 sql="select point_id, score from per_history_result where object_id='"+a0100+"'  and plan_id='"+planid+"' and point_id in("+condition+")  and plan_id='"+planid+"'   order by  instr('"+orderCondition.substring(0, orderCondition.length()-1)+"',point_id) ";//本人得分
					 sql_min="select point_id,min(score) score from per_history_result where point_id in("+condition+") and plan_id='"+planid+"' group by point_id  order by  instr('"+orderCondition.substring(0, orderCondition.length()-1)+"',point_id) ";//最小值
					break;
				case Constant.ORACEL:
					 sql_max="select point_id,max(score) score from per_history_result where point_id in("+condition+") and plan_id='"+planid+"' group by point_id  order by instr('"+orderCondition.substring(0, orderCondition.length()-1)+"',point_id) ";//最大值
					 sql_avg="select point_id,avg(score) score from per_history_result where point_id in("+condition+")  and plan_id='"+planid+"'  group by point_id  order by  instr('"+orderCondition.substring(0, orderCondition.length()-1)+"',point_id) ";//平均值
					 sql="select point_id, score from per_history_result where object_id='"+a0100+"'  and plan_id='"+planid+"' and point_id in("+condition+")  and plan_id='"+planid+"'   order by instr('"+orderCondition.substring(0, orderCondition.length()-1)+"',point_id) ";//本人得分
					 sql_min="select point_id,min(score) score from per_history_result where point_id in("+condition+") and plan_id='"+planid+"' group by point_id  order by  instr('"+orderCondition.substring(0, orderCondition.length()-1)+"',point_id) ";//最小值
					break;
				default://mysql
					 sql_max="select point_id,max(score) score from per_history_result where point_id in("+condition+") and plan_id='"+planid+"' group by point_id  order by  instr(',"+orderCondition+"',CONCAT(',',point_id,',')) ";//最大值
					 sql_avg="select point_id,avg(score) score from per_history_result where point_id in("+condition+")  and plan_id='"+planid+"'  group by point_id  order by  instr(',"+orderCondition+"',CONCAT(',',point_id,',')) ";//平均值
					 sql="select point_id, score from per_history_result where object_id='"+a0100+"'  and plan_id='"+planid+"' and point_id in("+condition+")  and plan_id='"+planid+"'   order by  instr(',"+orderCondition+"',CONCAT(',',point_id,',')) ";//本人得分
					 sql_min="select point_id,min(score) score from per_history_result where point_id in("+condition+") and plan_id='"+planid+"' group by point_id  order by  instr(',"+orderCondition+"',CONCAT(',',point_id,',')) ";//最小值
					break;
			 }
			
			 String e01a1="";
			 String a0101="";
			 boolean isHasValue=false;
			 rowSet=dao.search("select a0101,e01a1 from per_history_result where object_id='"+a0100+"'  and plan_id='"+planid+"'");
			 if(rowSet.next())
			 {
				 isHasValue=true;
				 a0101=rowSet.getString("a0101");
				 e01a1=rowSet.getString("e01a1");
			 }
			 /**获得能力素质当前岗位各指标的标准分  JinChunhai 2011.12.21 */	
			 HashMap pointScoreMap = getE01a1PointScore(e01a1,status,planid);
			 ArrayList perGradeCompetenceList = getPerGradeCompetenceList();
			 if(busitype!=null && busitype.trim().length()>0 && "1".equals(busitype))
			 {
			
				 LazyDynaBean zbean=new LazyDynaBean();
				 for(int j=0;j<list0.size();j++)
				 {				 
					 String pointid = (String)list0.get(j);				 
					 String score = (String)pointScoreMap.get(pointid)==null?"0":(String)pointScoreMap.get(pointid);	
					 float theScore=Float.parseFloat((String)pointScoreMap.get(pointid)==null?"0":(String)pointScoreMap.get(pointid));
					 if("1".equals(isShowPercentVal) && theScore>0)
					 {
						 float x = Float.parseFloat(score)*100/theScore;
						 score = Float.toString(x);
					 }		
					 zbean.set(pointid, score);				 
				 }
				 dataMap.put(ResourceFactory.getProperty("lable.examine.e01a1Score"),zbean);
			 }
			 
			 LazyDynaBean abean=new LazyDynaBean();
			 /**********本人得分**********/
			 if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.selfscore")+",")!=-1)
			 {
				 rowSet=dao.search(sql);//本人得分
				 abean=new LazyDynaBean();
				 while(rowSet.next())
				 {
	
					 String pointid = rowSet.getString("point_id")!=null?rowSet.getString("point_id"):"0";
					 String score = rowSet.getString("score")!=null?rowSet.getString("score"):"0";
					 int level =0;
					 if(condition.indexOf(pointid)==-1){
						 continue;
					 }
					 if(pointid==null|| "".equals(pointid)){
						 continue;
					 }
					 float theScore=Float.parseFloat((String)pointScoreMap.get(pointid)==null?"0":(String)pointScoreMap.get(pointid));
					 //liuy 2015-12-04
					 if("2".equals(isShowPercentVal) && theScore>0){
						 float tempScore = Float.parseFloat(score);
						 level = getLevel(tempScore, theScore, perGradeCompetenceList, getDomainFlag(planid));
						 abean.set(pointid, level+"");
					 }else {
					 	if("1".equals(isShowPercentVal) && theScore>0)
					 	{
						 	float x = Float.parseFloat(score)*100/theScore;
						 	score = Float.toString(x);
					 	}
					 	abean.set(pointid,score);
					 }//liuy 2015-12-04	
					 }
				 dataMap.put(a0101,abean);			 
			}
			 /**********最高得分**********/
			 if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.hightestScore")+",")!=-1)
			 {
				 rowSet=dao.search(sql_max);//最高得分
				 abean=new LazyDynaBean();
				 while(rowSet.next())
				 {
					 String pointid = rowSet.getString("point_id")!=null?rowSet.getString("point_id"):"0";
					 String score = rowSet.getString("score")!=null?rowSet.getString("score"):"0";
					 int level =0;
					 if(condition.indexOf(pointid)==-1){
						 continue;
					 }
					 if(pointid==null|| "".equals(pointid)){
						 continue;
					 }
					 float theScore=Float.parseFloat((String)pointScoreMap.get(pointid)==null?"0":(String)pointScoreMap.get(pointid));
					 //liuy 2015-12-04
					 if("2".equals(isShowPercentVal)){
						 float tempScore = Float.parseFloat(score);
						 level = getLevel(tempScore, theScore, perGradeCompetenceList, getDomainFlag(planid));
						 abean.set(pointid, level+"");
					 }else {						
						 if("1".equals(isShowPercentVal) && theScore>0)
						 {
							 float x = Float.parseFloat(score)*100/theScore;
							 score = Float.toString(x);
						 }
						 abean.set(pointid,score);
					 }//liuy 2015-12-04	
				}
				 
				 dataMap.put(ResourceFactory.getProperty("lable.examine.hightestScore"),abean);
			 }	 
			 /**********平均得分**********/
			 if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.avgScore")+",")!=-1)
			 {
				 rowSet=dao.search(sql_avg);//平均得分
				 abean=new LazyDynaBean();
				 while(rowSet.next())
				 {
					 String pointid = rowSet.getString("point_id")!=null?rowSet.getString("point_id"):"0";
					 String score = rowSet.getString("score")!=null?rowSet.getString("score"):"0";
					 if(condition.indexOf(pointid)==-1){
						 continue;
					 }
					 if(pointid==null|| "".equals(pointid)){
						 continue;
					 }
					 float theScore=Float.parseFloat((String)pointScoreMap.get(pointid)==null?"0":(String)pointScoreMap.get(pointid));
					 if("1".equals(isShowPercentVal) && theScore>0)
					 {
						 float x = Float.parseFloat(score)*100/theScore;
						 score = Float.toString(x);
					 }	
					 abean.set(pointid,score);
				 }

				 dataMap.put(ResourceFactory.getProperty("lable.examine.avgScore"),abean);	 
			 }
			 /**********最低得分**********/
			if((","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.lowestscore")+",")!=-1)
	     	{
				 rowSet=dao.search(sql_min);//最低得分
				 abean=new LazyDynaBean();
				 while(rowSet.next())
				 {
					 String pointid = rowSet.getString("point_id")!=null?rowSet.getString("point_id"):"0";
					 String score = rowSet.getString("score")!=null?rowSet.getString("score"):"0";
					 if(condition.indexOf(pointid)==-1){
						 continue;
					 }
					 if(pointid==null|| "".equals(pointid)){
						 continue;
					 }
					 float theScore=Float.parseFloat((String)pointScoreMap.get(pointid)==null?"0":(String)pointScoreMap.get(pointid));
					 if("1".equals(isShowPercentVal) && theScore>0)
					 {
						 float x = Float.parseFloat(score)*100/theScore;
						 score = Float.toString(x);
					 }	
					 abean.set(pointid,score);
				 }
		     		dataMap.put(ResourceFactory.getProperty("lable.examine.lowestscore"),abean);
			   }		
			 String signLogo = "false";
			 int n = 0;
			 int m = 0;					 
			 for(int i=0;i<list0.size();i++)
			 {
				 String a_value=(String)list0.get(i);	
				 String pointname = (String)pointNameMap.get(a_value);				 
				 n = pointname.length();
				 if(i==0) {
                     m = pointname.length();
                 }
				 if(n>m) {
                     m = n;
                 }
				 if(i==list0.size()-1)
				 {
					 if(n==m) {
                         signLogo = "true";
                     }
				 }
			 }		 			 
		
			 ArrayList tempList=new ArrayList();
			 for(int j=0;j<pointToNameList.size();j++)
			 {
				 CommonData data=(CommonData)pointToNameList.get(j);
				 String categoryName=data.getDataValue();
				 //5.0以上版本
				 if(this.userView.getVersion()>=50)
				 {
					categoryName =data.getDataName();
					
					if("true".equalsIgnoreCase(signLogo))
					{
						if(categoryName.length()<m)
						{
							for(int k=categoryName.length();k<=m;k++)
							{
								categoryName = categoryName+" ";
							}
						}
					}
					
					categoryName = this.warpRowStr(categoryName,6);
					
				 }
				 LazyDynaBean abean1=(LazyDynaBean)dataMap.get(a0101);
				 LazyDynaBean abean2=(LazyDynaBean)dataMap.get(ResourceFactory.getProperty("lable.examine.avgScore"));
				 LazyDynaBean abean3=(LazyDynaBean)dataMap.get(ResourceFactory.getProperty("lable.examine.hightestScore"));
				 LazyDynaBean abean4=(LazyDynaBean)dataMap.get(ResourceFactory.getProperty("lable.examine.lowestscore"));
				 
				 LazyDynaBean abean5 = new LazyDynaBean();
				 if(busitype!=null && busitype.trim().length()>0 && "1".equals(busitype)) {
                     abean5=(LazyDynaBean)dataMap.get(ResourceFactory.getProperty("lable.examine.e01a1Score"));
                 }
				 
				 tempList=new ArrayList();
//				 if(selectids.equals("null")||(","+selectids+",").indexOf(","+a0101+",")!=-1)
				 if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.selfscore")+",")!=-1) {
                     tempList.add(new CommonData((String)abean1.get(data.getDataValue()),a0101));
                 }
				 if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.avgScore")+",")!=-1) {
                     tempList.add(new CommonData((String)abean2.get(data.getDataValue()),ResourceFactory.getProperty("lable.examine.avgScore")));
                 }
				 if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.hightestScore")+",")!=-1) {
                     tempList.add(new CommonData((String)abean3.get(data.getDataValue()),ResourceFactory.getProperty("lable.examine.hightestScore")));
                 }
				 
//				 if(selectids.equals("null")||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.lowestscore")+",")!=-1)
				 if((","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.lowestscore")+",")!=-1) {
                     tempList.add(new CommonData((String)abean4.get(data.getDataValue()),ResourceFactory.getProperty("lable.examine.lowestscore")));
                 }
				 
				 if(busitype!=null && busitype.trim().length()>0 && "1".equals(busitype))
				 {
					 if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.e01a1Score")+",")!=-1) {
                         tempList.add(new CommonData((String)abean5.get(data.getDataValue()),ResourceFactory.getProperty("lable.examine.e01a1Score")));
                     }
				 }
				 
				 LazyDynaBean abean11=new LazyDynaBean();
				 abean11.set("categoryName", categoryName);
				 abean11.set("dataList",tempList);
				 dataList.add(abean11);
				 
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return dataList;
	 }
	 /**
	  * 获得能力素质当前岗位各指标的要求分
	  * @param e01a1
	  * @param status
	  * @return
	  */
	public HashMap getE01a1PointScore(String e01a1,String status,String plan_id)
	{
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		String creatDate = PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"); // 获取当前时间
		try
		{
			StringBuffer sql = new StringBuffer("");
			
			//0-分值模板	1-权重模板
//			if(status.equals("0"))
//				sql.append("select pc.object_id,pc.point_id,(pc.score*pg.gradevalue*pc.rank) postScore ");
//			else
//				sql.append("select pc.object_id,pc.point_id,(pc.score*pg.gradevalue) postScore ");
		    String startDate=PersonPostMatchingBo.getPlanStartDate(dao, plan_id);
		    if(!"".equals(startDate)){
		    	creatDate=startDate;
		    }	
			sql.append("select pc.object_id,pc.point_id,(pc.score*pg.gradevalue) postScore ");//无论分值、权重模板 计算岗位素质指标要求分都无需乘以权重(标准分*等级系数)  算岗位要求总分时再乘以权重
			sql.append(" from per_competency_modal pc,per_grade pg ");
			sql.append(" where pc.object_type = 3 ");
			sql.append(" and pc.point_id=pg.point_id and pc.gradecode=pg.gradecode ");
			sql.append(" and "+Sql_switcher.dateValue(creatDate)+" between start_date and end_date");
			sql.append(" and pc.object_id='" + e01a1 + "' ");
			
			rowSet = dao.search(sql.toString());
			while(rowSet.next())
			{		    	
			    String point_id = rowSet.getString("point_id")==null?"111":rowSet.getString("point_id");
			    String postScore = rowSet.getString("postScore")==null?"0.0":rowSet.getString("postScore");
			    	
			    map.put(point_id,postScore);
			}
			if(rowSet!=null) {
                rowSet.close();
            }
			    
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}	 
	
	/**
	  * 获得能力素质当前岗位各指标的总分
	  * @param e01a1
	  * @param status
	  * @return
	  */
	public HashMap getE01a1Score(String e01a1,String status,String plan_id)
	{
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		String creatDate = PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"); // 获取当前时间
		try
		{
			StringBuffer sql = new StringBuffer("");
			
			//0-分值模板	1-权重模板
//			if(status.equals("0"))
//				sql.append("select pc.object_id,pc.point_id,(pc.score*pg.gradevalue*pc.rank) postScore ");
//			else
//				sql.append("select pc.object_id,pc.point_id,(pc.score*pg.gradevalue) postScore ");
		    String startDate=PersonPostMatchingBo.getPlanStartDate(dao, plan_id);
		    if(!"".equals(startDate)){
		    	creatDate=startDate;
		    }	
			sql.append("select pc.object_id,pc.point_id,pc.score ");//无论分值、权重模板 计算岗位素质指标要求分都无需乘以权重(标准分*等级系数)  算岗位要求总分时再乘以权重
			sql.append(" from per_competency_modal pc,per_grade pg ");
			sql.append(" where pc.object_type = 3 ");
			sql.append(" and pc.point_id=pg.point_id and pc.gradecode=pg.gradecode ");
			sql.append(" and "+Sql_switcher.dateValue(creatDate)+" between start_date and end_date");
			sql.append(" and pc.object_id='" + e01a1 + "' ");
			
			rowSet = dao.search(sql.toString());
			while(rowSet.next())
			{		    	
			    String point_id = rowSet.getString("point_id")==null?"111":rowSet.getString("point_id");
			    String score = rowSet.getString("score")==null?"0.0":rowSet.getString("score");
			    	
			    map.put(point_id,score);
			}
			if(rowSet!=null) {
                rowSet.close();
            }
			    
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 获得能力素质当前岗位各指标的级别
	 * @param e01a1
	 * @param status
	 * @param plan_id
	 * @return
	 */
	public HashMap getE01a1PointGradecode(String e01a1,String status,String plan_id)
	{
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		String creatDate = PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"); // 获取当前时间
		try{
			StringBuffer sql = new StringBuffer("");
		    String startDate=PersonPostMatchingBo.getPlanStartDate(dao, plan_id);
		    if(!"".equals(startDate)) {
                creatDate=startDate;
            }
			sql.append("select pc.object_id,pc.point_id,pc.gradecode ");
			sql.append(" from per_competency_modal pc,per_grade pg ");
			sql.append(" where pc.object_type = 3 ");
			sql.append(" and pc.point_id=pg.point_id and pc.gradecode=pg.gradecode ");
			sql.append(" and "+Sql_switcher.dateValue(creatDate)+" between start_date and end_date");
			sql.append(" and pc.object_id='" + e01a1 + "' ");
			
			rowSet = dao.search(sql.toString());
			while(rowSet.next()){		    	
			    String point_id = rowSet.getString("point_id")==null?"111":rowSet.getString("point_id");
			    String gradeCode = rowSet.getString("gradecode")==null?"0.0":rowSet.getString("gradecode");
			    map.put(point_id,gradeCode);
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		} catch (Exception e){
			e.printStackTrace();
		}
		return map;
	}
	
	 /**
	  * 取得单人对比分析数据
	  * @param planid
	  * @param a0100
	  * @return
	  */
	 public HashMap getSingleContrastAnalyse(String planid,String a0100,ArrayList pointToNameList,String selectids,String isShowPercentVal,String chart_type,String busitype,String logo)
	 {
		 HashMap dataMap=new HashMap();
		 if(planid.trim().length()==0 || a0100.trim().length()==0) {
             return dataMap;
         }
		 HashMap pointNameMap=new HashMap();
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 
			 String status = "0";//0-分值模板	  1-权重模板
			 String sql = "select * from per_template where template_id = (select template_id from per_plan where plan_id="+planid+")";
			 RowSet rowSet=dao.search(sql);
			 if(rowSet.next()) {
                 status = rowSet.getString("status");
             }
			 
			 //取得指标的总分
			 HashMap map = new HashMap();
			 sql = "select * from per_template_point where item_id in (select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+"))";
			 rowSet=dao.search(sql);
			 while(rowSet.next())
			 {
				 String pointid = rowSet.getString("point_id");
				 float score = rowSet.getFloat("score");
				 map.put(pointid.toLowerCase(), new Float(score));
			 }
			 
//			 sql="select po.point_id,po.pointname from per_template_item pi,per_template_point pp,per_point po,per_plan "
//				 	   +" where pi.item_id=pp.item_id and pp.point_id=po.point_id  and pi.template_id=per_plan.template_id and per_plan.plan_id="+planid+" order by pi.seq,pp.seq";
			 sql="select po.point_id,po.pointname from per_template_item pi,per_template_point pp,per_point po,per_plan "
			 	   +" where pi.item_id=pp.item_id and pp.point_id=po.point_id  and pi.template_id=per_plan.template_id and per_plan.plan_id="+planid+" order by po.point_id";
			 rowSet=dao.search(sql);
			 StringBuffer sql0=new StringBuffer("");
			 StringBuffer sql1=new StringBuffer("");
			 StringBuffer sql2=new StringBuffer("");
			 StringBuffer sql3=new StringBuffer("");
			 ArrayList list0=new ArrayList(); 
			 while(rowSet.next())
			 {
				 String point_id=rowSet.getString("point_id");
				 pointToNameList.add(new CommonData(point_id,rowSet.getString("pointname")));
				 pointNameMap.put("C_"+point_id,rowSet.getString("pointname"));
				 sql0.append(",C_"+point_id);
				 sql2.append(",Max(C_"+point_id+") C_"+point_id);
				 sql1.append(",avg(C_"+point_id+") C_"+point_id);
				 sql3.append(",Min(C_"+point_id+") C_"+point_id);
				 list0.add("C_"+point_id);
			 }
			 ///共享项目
			 rowSet=dao.search("select pi.* from per_template_item pi,per_plan where per_plan.template_id=pi.template_id and pi.kind=2 and per_plan.plan_id="+planid);
			 ArrayList itemList=new ArrayList();
			 while(rowSet.next())
			 {
				 LazyDynaBean abean=new LazyDynaBean();
				 abean.set("item_id", rowSet.getString("item_id"));
				 abean.set("itemdesc",rowSet.getString("itemdesc"));
				 abean.set("score",rowSet.getString("score")!=null?rowSet.getString("score"):"0");
				 abean.set("rank", rowSet.getString("rank")!=null?rowSet.getString("rank"):"0");
				 itemList.add(abean);				 
				 
				 String item_id = rowSet.getString("item_id");
				 float score = rowSet.getFloat("score");
				 map.put(item_id, new Float(score));				 
			 }			 			 
			 
			 for(int i=0;i<itemList.size();i++)
			 {
				 LazyDynaBean abean=(LazyDynaBean)itemList.get(i);
				 String item_id=(String)abean.get("item_id");
				 String itemdesc=(String)abean.get("itemdesc");
				 pointToNameList.add(new CommonData(item_id,itemdesc));
				 pointNameMap.put("T_"+item_id,itemdesc);
				 sql0.append(",T_"+item_id);
				 sql2.append(",Max(T_"+item_id+") T_"+item_id);
				 sql1.append(",avg(T_"+item_id+") T_"+item_id);
				 sql3.append(",Min(T_"+item_id+") T_"+item_id);
				 list0.add("T_"+item_id);
			 }			 
			 			 
			 String signLogo = "false";
			 int n = 0;
			 int m = 0;					 
			 for(int i=0;i<list0.size();i++)
			 {
				 String a_value=(String)list0.get(i);	
				 String pointname = (String)pointNameMap.get(a_value);				 
				 n = pointname.length();
				 if(i==0) {
                     m = pointname.length();
                 }
				 if(n>m) {
                     m = n;
                 }
				 if(i==list0.size()-1)
				 {
					 if(n==m) {
                         signLogo = "true";
                     }
				 }
			 }			 
			 
			 if(sql2.length() > 0) {
				 sql="select "+sql2.substring(1)+" from per_result_"+planid;
			 }
			 if(sql1.length() > 0) {
				 if(sql.length() > 0) {
					 sql+=" union all ";
				 }
				 sql+="select "+sql1.substring(1)+" from per_result_"+planid;
			 }
			 if(sql0.length() > 0) {
				 if(sql.length() > 0) {
					 sql+=" union all ";
				 }
				 sql+="select "+sql0.substring(1)+" from per_result_"+planid+" where object_id='"+a0100+"' ";
			 }
			 if(sql3.length() > 0) {
				 if(sql.length() > 0) {
					 sql+=" union all ";
				 }
				 sql+="select "+sql3.substring(1)+" from per_result_"+planid;
			 }
			 
			 
			 String a0101="";
			 String e01a1="";
			 rowSet=dao.search("select a0101,e01a1 from per_result_"+planid+" where object_id='"+a0100+"'");
			 if(rowSet.next())
			 {
				 a0101=rowSet.getString("a0101");
				 e01a1=rowSet.getString("e01a1");
			 }
			 
			 rowSet=dao.search(sql);
			 int i=0;
			 
			 float minRadarScore = 0;  //  所有线的最低值
			 float maxRadarScore = 0;  //  所有线的最高值
			 //liuy 2015-12-04
			 int maxLevel = 0;
			 ArrayList perGradeCompetenceList = getPerGradeCompetenceList();
			 maxLevel = perGradeCompetenceList.size();
			 //liuy 2015-12-04
			 /**获得能力素质当前岗位各指标的标准分  JinChunhai 2011.12.21 */			
			 if(busitype!=null && busitype.trim().length()>0 && "1".equals(busitype))
			 {
				 HashMap pointScoreMap = null;
				 //haosl delete 根据下面代码可以看出等级的计算也是根据分值算的，所以这里的处理便没有意义了 20170415
				 /*if("2".equals(isShowPercentVal))
					 pointScoreMap = getE01a1PointGradecode(e01a1, status, planid);
				 else*/
				 pointScoreMap = getE01a1PointScore(e01a1,status,planid);
				 ArrayList list=new ArrayList();
				 for(int j=0;j<list0.size();j++)
				 {				 
					 String a_value=(String)list0.get(j);
					 String pointid = a_value.substring(2);				 
					 String score = (String)pointScoreMap.get(pointid);
					 Float theScore = (Float)map.get(pointid.toLowerCase());
					 int level = 0;//级别
					 
					 if(score==null || score.trim().length()<=0) {
                         score = String.valueOf(theScore);
                     }
					 
					 if(j==0)
					 {
						 minRadarScore = Float.parseFloat(score);
						 maxRadarScore = Float.parseFloat(score);	
					 }
					 
					 if("1".equals(isShowPercentVal) && theScore!=null && theScore.floatValue()>0)
					 {
						 float x = Float.parseFloat(score)*100/theScore.floatValue();
						 score = Float.toString(x);
					 //liuy 2015-12-04
					 }else if("2".equals(isShowPercentVal)){
						 float tempScore = Float.parseFloat(score);
						 level = getLevel(tempScore, theScore, perGradeCompetenceList, getDomainFlag(planid));
					 }//liuy 2015-12-04
					 if(minRadarScore > Float.parseFloat(score)) {
                         minRadarScore = Float.parseFloat(score);
                     }
					 if(maxRadarScore < Float.parseFloat(score)) {
                         maxRadarScore = Float.parseFloat(score);
                     }
					 
					//5.0以上版本
					 if(this.userView.getVersion()>=50)
					 {
						 String name = (String)pointNameMap.get(a_value);
							
						 if("true".equalsIgnoreCase(signLogo))
						 {
							 if(name.length()<m)
							 {
								 for(int k=name.length();k<=m;k++)
								 {
									 name = name+" ";
								 }
							 }
						 }
						 
						 if("41".equals(chart_type))
						 {
							 if(logo!=null && logo.trim().length()>0 && "logo".equalsIgnoreCase(logo)) {
                                 name=this.warpRowStr(name,13);
                             } else {
                                 name=this.warpRowStr(name,25);
                             }
						 }else {
                             name=this.warpRowStr(name,6);
                         }
						 
						 //liuy 2015-12-04
						 if("2".equals(isShowPercentVal)) {
                             list.add(new CommonData(Integer.toString(level),name));
                         } else {
                             list.add(new CommonData(score,name));
                         }
					 }else
						 if("2".equals(isShowPercentVal)) {
                             list.add(new CommonData(Integer.toString(level),pointid));
                         } else {
                             list.add(new CommonData(score,pointid));
                         }
						 //liuy 2015-12-04
				 }
				 
				 if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.e01a1Score")+",")!=-1)
				 {
					 dataMap.put(ResourceFactory.getProperty("lable.examine.e01a1Score"),list);//岗位要求
				 }
			 }			 			 
			 
			 while(rowSet.next())
			 {
				 ArrayList list=new ArrayList();
				 float minScore = 0;  //  单条线的最低值
				 float maxScore = 0;  //  单条线的最高值
				 int level = 0;//级别
				 for(int j=0;j<list0.size();j++)
				 {					
					 String a_value=(String)list0.get(j);
					 String pointid = a_value.substring(2);
					 String score = rowSet.getString(a_value)!=null?rowSet.getString(a_value):"0";
					 
					 if(j==0)
					 {
						 minScore = Float.parseFloat(score);
						 maxScore = Float.parseFloat(score);	
					 }
					 
					 Float theScore = (Float)map.get(pointid.toLowerCase());
					 if("1".equals(isShowPercentVal) && theScore!=null && theScore.floatValue()>0)
					 {
						 float x = Float.parseFloat(score)*100/theScore.floatValue();
						 score = Float.toString(x);			 
					 //liuy 2015-12-04
					 }else if("2".equals(isShowPercentVal)){
						 float tempScore = Float.parseFloat(score);
						 level = getLevel(tempScore, theScore, perGradeCompetenceList, getDomainFlag(planid));
					 }//liuy 2015-12-04
					 
					 if(minScore > Float.parseFloat(score)) {
                         minScore = Float.parseFloat(score);
                     }
					 if(maxScore < Float.parseFloat(score)) {
                         maxScore = Float.parseFloat(score);
                     }
					 
					 	//5.0以上版本
						if(this.userView.getVersion()>=50)
						{
							String name = (String)pointNameMap.get(a_value);
							
							if("true".equalsIgnoreCase(signLogo))
							{
								if(name.length()<m)
								{
									for(int k=name.length();k<=m;k++)
									{
										name = name+" ";
									}
								}
							}
							
							if("41".equals(chart_type))
							 {
								 if(logo!=null && logo.trim().length()>0 && "logo".equalsIgnoreCase(logo)) {
                                     name=this.warpRowStr(name,13);
                                 } else {
                                     name=this.warpRowStr(name,25);
                                 }
							 }else {
                                name=this.warpRowStr(name,6);
                            }
							
							//liuy 2015-12-04
							if("2".equals(isShowPercentVal)) {
                                list.add(new CommonData(Integer.toString(level),name));
                            } else {
                                list.add(new CommonData(score,name));
                            }
							
						 }else
							 if("2".equals(isShowPercentVal)) {
                                 list.add(new CommonData(Integer.toString(level),pointid));
                             } else {
                                 list.add(new CommonData(score,pointid));
                             }
							 //liuy 2015-12-04
				 }
				 
				 if(i==0)
				 {
					 if(minRadarScore==0) {
                         minRadarScore = minScore;
                     } else
					 {
						 if(minRadarScore > minScore) {
                             minRadarScore = minScore;
                         }
					 }
					 if(maxRadarScore==0 || maxRadarScore==0.0) {
                         maxRadarScore = maxScore;
                     } else
					 {													 
						 if(maxRadarScore < maxScore) {
                             maxRadarScore = maxScore;
                         }
					 }					 
				 }else
				 {
					 if(minRadarScore > minScore) {
                         minRadarScore = minScore;
                     }
						 
					 if(maxRadarScore < maxScore) {
                         maxRadarScore = maxScore;
                     }
				 }
				 
				 if(i==0)
				 {
//					 if(selectids.equals("null")||(","+selectids+",").indexOf(","+a0101+",")!=-1)
					
					 
					 if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.hightestScore")+",")!=-1)
					 {
						 dataMap.put(ResourceFactory.getProperty("lable.examine.hightestScore"),list);
					 }
				 }
				 else if(i==1)
				 {
					 if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.avgScore")+",")!=-1)
					 {
						 dataMap.put(ResourceFactory.getProperty("lable.examine.avgScore"),list);
					 }
				 }
				 else if(i==2)
				 {
					 if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.selfscore")+",")!=-1)
					 {
						  dataMap.put(a0101,list);
					 }
				 }
				 else if(i==3)
				 {
//					if(selectids.equals("null")||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.lowestscore")+",")!=-1)
				     	if((","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.lowestscore")+",")!=-1)
				     	{
				     		dataMap.put(ResourceFactory.getProperty("lable.examine.lowestscore"),list);
				     	}
				 }
				 i++;				 
			 }
			 if(minRadarScore>=maxRadarScore) {
                 minRadarScore=0;
             }
			 if(maxRadarScore==0 || maxRadarScore==0.0) {
                 maxRadarScore=5;
             }
			 //liuy 2015-12-04
			 if(maxLevel<4 || maxLevel<4.0) {
                 maxLevel=4;
             }
			 if("41".equals(chart_type))
			 {
				 if("2".equals(isShowPercentVal)) {
                     dataMap.put("minmax",0+","+maxLevel);
                 } else{
					 //dataMap.put("minmax",minRadarScore+","+maxRadarScore);
					 dataMap.put("minmax",0+","+maxRadarScore);
				 }
			 }//liuy 2015-12-04
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return dataMap;
	 }
	 
	/**
     * 得到标准模板所有级别
     * @author liuy
     * @return
     */ 
	private ArrayList getPerGradeCompetenceList(){
		String startLevel = SystemConfig.getPropertyValue("startLevel");
		ArrayList perGradeCompetenceList=new ArrayList();
		String sql = "select grade_template_id,top_value,bottom_value from per_grade_competence";
		if(StringUtils.isNotEmpty(startLevel)) {
            sql += " where gradevalue >= (select gradevalue from per_grade_competence where grade_template_id = '"+ startLevel +"') order by gradevalue";
        } else {
            sql += " order by gradevalue";
        }
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs =null;
		try {
			rs = dao.search(sql);
			while(rs.next()){
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("grade_template_id", rs.getString("grade_template_id"));
				abean.set("top_value",rs.getString("top_value"));
				abean.set("bottom_value",rs.getString("bottom_value"));
				perGradeCompetenceList.add(abean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if(rs!=null) {
                PubFunc.closeResource(rs);
            }
		}
		return perGradeCompetenceList;
	}
	 /**
     * 得到当前指标级别
     * @param tempScore 当前分数
     * @param theScore 指标总分
     * @param perGradeCompetenceList 指标等级集合
     * @param domainflag 0:上限封闭 	1:下限封闭
     * @author liuy
     * @return
     */ 
	private int getLevel(float tempScore, float theScore, ArrayList perGradeCompetenceList, int domainflag){
		int level = 0;
		for(int k = 0;k < perGradeCompetenceList.size();k++){
			LazyDynaBean abean = (LazyDynaBean)perGradeCompetenceList.get(k);
			String grade_template_id = (String)abean.get("grade_template_id");
			float top_value = Float.parseFloat(abean.get("top_value").toString());
			float bottom_value = Float.parseFloat(abean.get("bottom_value").toString());
			if(domainflag==0){//上限封闭				
				if(k==0&&tempScore < bottom_value*theScore) {
                    level = 0;
                } else if(k==perGradeCompetenceList.size()-1&&tempScore >= top_value*theScore) {
                    level = perGradeCompetenceList.size();
                } else if(tempScore <= top_value*theScore && tempScore > bottom_value*theScore){
					level = k+1;
					break;
				}
			}else if(domainflag==1){//下限封闭
				if(k==0&&tempScore < bottom_value*theScore) {
                    level = 0;
                } else if(k==perGradeCompetenceList.size()-1&&tempScore >= top_value*theScore) {
                    level = perGradeCompetenceList.size();
                } else if(tempScore < top_value*theScore && tempScore >= bottom_value*theScore){
					level = k+1;
					break;
				}
			}
		}
		return level;
	}
	
	/**
	 * 得到等级分类的封闭标识
	 * @param plan_id
	 * @return
	 */
	private int getDomainFlag(String plan_id){
		int domainflag=0;
		ContentDAO dao = new ContentDAO(this.conn);
		String ssql="select domainflag from per_degree where degree_id='"+getGradeClass(plan_id)+"'";
		ResultSet res = null;
		try {
			res = dao.search(ssql);
			if(res.next()){//取得匹配度等级分类的封闭标识
				domainflag=res.getInt("domainflag");//0:上限封闭 	1:下限封闭
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(res!=null) {
                PubFunc.closeResource(res);
            }
		}
		return domainflag;
	}
	
	/**
     * 得到考核计划等级分类参数
     * @param plan_id
     * @return
     */
    public String getGradeClass(String plan_id){
    	String gradeClass="";
    	try{
    		LoadXml loadxml=null;
    		if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null){
    			loadxml=new LoadXml(this.conn,plan_id);
    			BatchGradeBo.getPlanLoadXmlMap().put(plan_id,loadxml);
    		}else {
                loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);
            }
    		Hashtable planParam=loadxml.getDegreeWhole(); 
    		gradeClass = (String)planParam.get("GradeClass");
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	return gradeClass;
    }
	
	private HashMap getPerGradeCompetenceMap(){
		String startLevel = SystemConfig.getPropertyValue("startLevel");
		HashMap perGradeCompetenceMap=new HashMap();
		String sql = "select grade_template_id,top_value,bottom_value from per_grade_competence";
		if(StringUtils.isNotEmpty(startLevel)) {
            sql += " where gradevalue >= (select gradevalue from per_grade_competence where grade_template_id = '"+ startLevel +"') order by gradevalue";
        } else {
            sql += " order by gradevalue";
        }
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs =null;
		try {
			rs = dao.search(sql);
			int i = 1;
			while(rs.next()){
				perGradeCompetenceMap.put(rs.getString("grade_template_id"), i);
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if(rs!=null) {
                PubFunc.closeResource(rs);
            }
		}
		return perGradeCompetenceMap;
	}
	
	 /**
	  * 取得单人对比分析数据  按岗位素质模型
	  * @param planid
	  * @param a0100
	  * @return
	  */
	 public HashMap getSingleContrastAnalyseByModel(String planid,String a0100,ArrayList pointToNameList,String selectids,String isShowPercentVal,String chart_type,String busitype,String logo)
	 {
		 HashMap dataMap=new HashMap();
		 if(planid.trim().length()==0 || a0100.trim().length()==0) {
             return dataMap;
         }
		 HashMap pointNameMap=new HashMap();
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 /****按岗位素质模型 指标均为按权重打分****/
			 String status = "1";//0-分值模板	  1-权重模板
			 String sql ="";
			 RowSet rowSet=null;
			 /**从历史表中找出要分析的指标**/
			 sql="select point_id ,pointname from per_point where point_id in(select point_id from per_history_result where plan_id='"+planid+"' and  object_id='"+a0100+"' and status='0') ";
			 //sql="select point_id ,pointname from per_point where point_id in (select point_id from per_competency_modal where object_type=3 and '"+sdf.format(date)+"' between start_date and end_date and object_id=(select e01a1 from usra01 where a0100='"+a0100+"'))";
			 rowSet=dao.search(sql);
			 ArrayList list0=new ArrayList(); 
			 String condition="";
			 String orderCondition="";
			 while(rowSet.next())
			 {
				 String point_id=rowSet.getString("point_id");
				 pointToNameList.add(new CommonData(point_id,rowSet.getString("pointname")));
				 pointNameMap.put(point_id,rowSet.getString("pointname"));
				 list0.add(point_id);
				 condition+="'"+point_id+"',";
				 orderCondition+=point_id+",";
			 }
			 if(condition.length()>0) {
                 condition=condition.substring(0, condition.length()-1);
             }
			 String signLogo = "false";
			 int n = 0;
			 int m = 0;					 
			 for(int i=0;i<list0.size();i++)
			 {
				 String a_value=(String)list0.get(i);	
				 String pointname = (String)pointNameMap.get(a_value);				 
				 n = pointname.length();
				 if(i==0) {
                     m = pointname.length();
                 }
				 if(n>m) {
                     m = n;
                 }
				 if(i==list0.size()-1)
				 {
					 if(n==m) {
                         signLogo = "true";
                     }
				 }
			 }			 
			 String sql_max="";
			 String sql_avg="";
			 String sql_min="";
			 /**********不同数据库 in排序***********/
			 switch (Sql_switcher.searchDbServer()) {
				case Constant.MSSQL:
					 sql_max="select point_id,max(score) score from per_history_result where point_id in("+condition+") and plan_id='"+planid+"' group by point_id  order by charindex(','+point_id+',',',"+orderCondition+",') ";//最大值
					 sql_avg="select point_id,avg(score) score from per_history_result where point_id in("+condition+")  and plan_id='"+planid+"'  group by point_id  order by charindex(','+point_id+',',',"+orderCondition+",') ";//平均值
					 sql="select point_id, score from per_history_result where object_id='"+a0100+"'  and plan_id='"+planid+"' and point_id in("+condition+")   order by charindex(','+point_id+',',',"+orderCondition+",') ";//本人得分
					 sql_min="select point_id,min(score) score from per_history_result where point_id in("+condition+") and plan_id='"+planid+"' group by point_id  order by charindex(','+point_id+',',',"+orderCondition+",') ";//最小值
					break;
				case Constant.DB2:
					 sql_max="select point_id,max(score) score from per_history_result where point_id in("+condition+") and plan_id='"+planid+"' group by point_id  order by  instr('"+orderCondition.substring(0, orderCondition.length()-1)+"',point_id) ";//最大值
					 sql_avg="select point_id,avg(score) score from per_history_result where point_id in("+condition+")  and plan_id='"+planid+"'  group by point_id  order by  instr('"+orderCondition.substring(0, orderCondition.length()-1)+"',point_id) ";//平均值
					 sql="select point_id, score from per_history_result where object_id='"+a0100+"'  and plan_id='"+planid+"' and point_id in("+condition+")    order by  instr('"+orderCondition.substring(0, orderCondition.length()-1)+"',point_id) ";//本人得分
					 sql_min="select point_id,min(score) score from per_history_result where point_id in("+condition+") and plan_id='"+planid+"' group by point_id  order by  instr('"+orderCondition.substring(0, orderCondition.length()-1)+"',point_id) ";//最小值
					break;
				case Constant.ORACEL:
					 sql_max="select point_id,max(score) score from per_history_result where point_id in("+condition+") and plan_id='"+planid+"' group by point_id  order by instr('"+orderCondition.substring(0, orderCondition.length()-1)+"',point_id) ";//最大值
					 sql_avg="select point_id,avg(score) score from per_history_result where point_id in("+condition+")  and plan_id='"+planid+"'  group by point_id  order by  instr('"+orderCondition.substring(0, orderCondition.length()-1)+"',point_id) ";//平均值
					 sql="select point_id, score from per_history_result where object_id='"+a0100+"'  and plan_id='"+planid+"' and point_id in("+condition+")    order by instr('"+orderCondition.substring(0, orderCondition.length()-1)+"',point_id) ";//本人得分
					 sql_min="select point_id,min(score) score from per_history_result where point_id in("+condition+") and plan_id='"+planid+"' group by point_id  order by  instr('"+orderCondition.substring(0, orderCondition.length()-1)+"',point_id) ";//最小值
					break;
				default://mysql
					 sql_max="select point_id,max(score) score from per_history_result where point_id in("+condition+") and plan_id='"+planid+"' group by point_id  order by  instr(',"+orderCondition+"',CONCAT(',',point_id,',')) ";//最大值
					 sql_avg="select point_id,avg(score) score from per_history_result where point_id in("+condition+")  and plan_id='"+planid+"'  group by point_id  order by  instr(',"+orderCondition+"',CONCAT(',',point_id,',')) ";//平均值
					 sql="select point_id, score from per_history_result where object_id='"+a0100+"'  and plan_id='"+planid+"' and point_id in("+condition+")   order by  instr(',"+orderCondition+"',CONCAT(',',point_id,',')) ";//本人得分
					 sql_min="select point_id,min(score) score from per_history_result where point_id in("+condition+") and plan_id='"+planid+"' group by point_id  order by  instr(',"+orderCondition+"',CONCAT(',',point_id,',')) ";//最小值
					break;
			 }
			
			 
			 String a0101="";
			 String e01a1="";
			 rowSet=dao.search("select a0101,e01a1 from per_history_result where object_id='"+a0100+"'  and plan_id='"+planid+"'");
			 if(rowSet.next())
			 {
				 a0101=rowSet.getString("a0101");
				 e01a1=rowSet.getString("e01a1");
			 }
			 
			 rowSet=dao.search(sql);
			 float minRadarScore = 0;  //  所有线的最低值
			 float maxRadarScore = 0;  //  所有线的最高值
			 //liuy 2015-12-04
			 int maxLevel = 0;
			 ArrayList perGradeCompetenceList = getPerGradeCompetenceList();
			 HashMap perGradeCompetenceMap = getPerGradeCompetenceMap();
			 maxLevel = perGradeCompetenceMap.size();
			 //liuy 2015-12-04
			 /**获得能力素质当前岗位各指标的标准分 */	
			 HashMap pointScoreMap = null;
			 if("2".equals(isShowPercentVal)) {
                 pointScoreMap = getE01a1PointGradecode(e01a1, status, planid);
             } else {
                 pointScoreMap = getE01a1PointScore(e01a1,status,planid);
             }
			 if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.e01a1Score")+",")!=-1)
			 {
				 if(busitype!=null && busitype.trim().length()>0 && "1".equals(busitype))
				 {
					 ArrayList list=new ArrayList();
					 for(int j=0;j<list0.size();j++)
					 {				 
						 String pointid = (String)list0.get(j);			 
						 String score = (String)pointScoreMap.get(pointid)==null?"0":(String)pointScoreMap.get(pointid);
						 int level = 0;//级别
						 if("1".equals(isShowPercentVal))
						 {
							 float theScore=Float.parseFloat(score);
							 if(theScore>0){
								 float x = Float.parseFloat(score)*100/theScore;
								 score = Float.toString(x);
							 }
						 //liuy 2015-12-04
						 }else if("2".equals(isShowPercentVal)){
							 String gradecode = (String)pointScoreMap.get(pointid)==null?"":(String)pointScoreMap.get(pointid);
							 if(perGradeCompetenceMap.get(gradecode)!=null) {
                                 level = Integer.parseInt(perGradeCompetenceMap.get(gradecode).toString());
                             } else {
                                 level = 0;
                             }
							 score = "0";
						 }//liuy 2015-12-04
						 if(j==0)
						 {
							 minRadarScore = Float.parseFloat(score);
							 maxRadarScore = Float.parseFloat(score);	
						 }
	
						 if(minRadarScore > Float.parseFloat(score)) {
                             minRadarScore = Float.parseFloat(score);
                         }
						 if(maxRadarScore < Float.parseFloat(score)) {
                             maxRadarScore = Float.parseFloat(score);
                         }
						 
						//5.0以上版本
						 if(this.userView.getVersion()>=50)
						 {
							 String name = (String)pointNameMap.get(pointid);
								
							 if("true".equalsIgnoreCase(signLogo))
							 {
								 if(name.length()<m)
								 {
									 for(int k=name.length();k<=m;k++)
									 {
										 name = name+" ";
									 }
								 }
							 }
							 
							 if("41".equals(chart_type))
							 {
								 if(logo!=null && logo.trim().length()>0 && "logo".equalsIgnoreCase(logo)) {
                                     name=this.warpRowStr(name,13);
                                 } else {
                                     name=this.warpRowStr(name,25);
                                 }
							 }else {
                                 name=this.warpRowStr(name,6);
                             }
							 
							 //liuy 2015-12-04
							 if("2".equals(isShowPercentVal)) {
                                 list.add(new CommonData(Integer.toString(level),name));
                             } else {
                                 list.add(new CommonData(score,name));
                             }
						 }else
							 if("2".equals(isShowPercentVal)) {
                                 list.add(new CommonData(Integer.toString(level),pointid));
                             } else {
                                 list.add(new CommonData(score,pointid));
                             }
							 //liuy 2015-12-04
					 }
	
						 dataMap.put(ResourceFactory.getProperty("lable.examine.e01a1Score"),list);//岗位要求
					 }
			 }
			 float minScore = 0;  //  单条线的最低值
			 float maxScore = 0;  //  单条线的最高值
			 int level = 0;//级别
			 ArrayList list=new ArrayList();
			 pointScoreMap = getE01a1Score(e01a1,status,planid);
			 /**********本人得分**********/
			 if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.selfscore")+",")!=-1)
			 {
				 rowSet=dao.search(sql);//本人得分
				 list=new ArrayList();
	
					 while(rowSet.next())
					 {
		
						 String pointid = rowSet.getString("point_id")!=null?rowSet.getString("point_id"):"0";
						 String score = rowSet.getString("score")!=null?rowSet.getString("score"):"0";
						 if(condition.indexOf(pointid)==-1){
							 continue;
						 }
						 if(pointid==null|| "".equals(pointid)){
							 continue;
						 }
						 float theScore=Float.parseFloat((String)pointScoreMap.get(pointid)==null?"0":(String)pointScoreMap.get(pointid));
						 if("1".equals(isShowPercentVal) && theScore>0)
						 {
							 float x = Float.parseFloat(score)*100/theScore;
							 score = Float.toString(x);
						 //liuy 2015-12-04
						 }else if("2".equals(isShowPercentVal)){
							 float tempScore = Float.parseFloat(score);
							 level = getLevel(tempScore, theScore, perGradeCompetenceList, getDomainFlag(planid));
						 }//liuy 2015-12-04
						 minScore = Float.parseFloat(score);
						 maxScore = Float.parseFloat(score);
						 
							 if(minScore > Float.parseFloat(score)) {
                                 minScore = Float.parseFloat(score);
                             }
							 if(maxScore < Float.parseFloat(score)) {
                                 maxScore = Float.parseFloat(score);
                             }
							 
							 	//5.0以上版本
								if(this.userView.getVersion()>=50)
								{
									String name = (String)pointNameMap.get(pointid);
									
									if("true".equalsIgnoreCase(signLogo))
									{
										if(name.length()<m)
										{
											for(int k=name.length();k<=m;k++)
											{
												name = name+" ";
											}
										}
									}
									
									if("41".equals(chart_type))
									 {
										 if(logo!=null && logo.trim().length()>0 && "logo".equalsIgnoreCase(logo)) {
                                             name=this.warpRowStr(name,13);
                                         } else {
                                             name=this.warpRowStr(name,25);
                                         }
									 }else {
                                        name=this.warpRowStr(name,6);
                                    }
									
									//liuy 2015-12-04
									if("2".equals(isShowPercentVal)) {
                                        list.add(new CommonData(Integer.toString(level),name));
                                    } else {
                                        list.add(new CommonData(score,name));
                                    }
								}else
									if("2".equals(isShowPercentVal)) {
                                        list.add(new CommonData(Integer.toString(level),pointid));
                                    } else {
                                        list.add(new CommonData(score,pointid));
                                    }
									//liuy 2015-12-04
		
							 if(minRadarScore > minScore) {
                                 minRadarScore = minScore;
                             }
								 
							 if(maxRadarScore < maxScore) {
                                 maxRadarScore = maxScore;
                             }
		
		
		
							  dataMap.put(a0101,list);
						 }
						 
				 }
			 /**********最高得分**********/
			 if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.hightestScore")+",")!=-1)
			 {
				 rowSet=dao.search(sql_max);//最高得分
				 list=new ArrayList();
				 while(rowSet.next())
				 {			
						 String pointid = rowSet.getString("point_id")!=null?rowSet.getString("point_id"):"";
						 String score = rowSet.getString("score")!=null?rowSet.getString("score"):"0";
						 if(condition.indexOf(pointid)==-1){
							 continue;
						 }
						 if(pointid==null|| "".equals(pointid)){
							 continue;
						 }
						 float theScore=Float.parseFloat((String)pointScoreMap.get(pointid)==null?"0":(String)pointScoreMap.get(pointid));
						 if("1".equals(isShowPercentVal) && theScore>0)
						 {
							 float x = Float.parseFloat(score)*100/theScore;
							 score = Float.toString(x);
						 //liuy 2015-12-04
						 }else if("2".equals(isShowPercentVal)){
							 float tempScore = Float.parseFloat(score);
							 level = getLevel(tempScore, theScore, perGradeCompetenceList, getDomainFlag(planid));
						 }//liuy 2015-12-04	
							 minScore = Float.parseFloat(score);
							 maxScore = Float.parseFloat(score);	
						 if(minScore > Float.parseFloat(score)) {
                             minScore = Float.parseFloat(score);
                         }
						 if(maxScore < Float.parseFloat(score)) {
                             maxScore = Float.parseFloat(score);
                         }
						 
						 	//5.0以上版本
							if(this.userView.getVersion()>=50)
							{
								String name = (String)pointNameMap.get(pointid);
								
								if("true".equalsIgnoreCase(signLogo))
								{
									if(name.length()<m)
									{
										for(int k=name.length();k<=m;k++)
										{
											name = name+" ";
										}
									}
								}
								
								if("41".equals(chart_type))
								 {
									 if(logo!=null && logo.trim().length()>0 && "logo".equalsIgnoreCase(logo)) {
                                         name=this.warpRowStr(name,13);
                                     } else {
                                         name=this.warpRowStr(name,25);
                                     }
								 }else {
                                    name=this.warpRowStr(name,6);
                                }
								//liuy 2015-12-04
								if("2".equals(isShowPercentVal)) {
                                    list.add(new CommonData(Integer.toString(level),name));
                                } else {
                                    list.add(new CommonData(score,name));
                                }
							}else
								if("2".equals(isShowPercentVal)) {
                                    list.add(new CommonData(Integer.toString(level),pointid));
                                } else {
                                    list.add(new CommonData(score,pointid));
                                }
								//liuy 2015-12-04
	
						 if(minRadarScore > minScore) {
                             minRadarScore = minScore;
                         }
							 
						 if(maxRadarScore < maxScore) {
                             maxRadarScore = maxScore;
                         }
	
						 dataMap.put(ResourceFactory.getProperty("lable.examine.hightestScore"),list);
					 }		 
			 }
			 /**********平均得分**********/
			 if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.avgScore")+",")!=-1)
			 {
				 rowSet=dao.search(sql_avg);//平均得分
				 list=new ArrayList();
				 while(rowSet.next())
				 {			
						 String pointid = rowSet.getString("point_id")!=null?rowSet.getString("point_id"):"";
						 String score = rowSet.getString("score")!=null?rowSet.getString("score"):"0";
						 if(condition.indexOf(pointid)==-1){
							 continue;
						 }
						 if(pointid==null|| "".equals(pointid)){
							 continue;
						 }
						 float theScore=Float.parseFloat((String)pointScoreMap.get(pointid)==null?"0":(String)pointScoreMap.get(pointid));
						 if("1".equals(isShowPercentVal) && theScore>0)
						 {
							 float x = Float.parseFloat(score)*100/theScore;
							 score = Float.toString(x);
						 //liuy 2015-12-04
						 }else if("2".equals(isShowPercentVal)){
							 float tempScore = Float.parseFloat(score);
							 level = getLevel(tempScore, theScore, perGradeCompetenceList, getDomainFlag(planid));
						 }//liuy 2015-12-04	
							 minScore = Float.parseFloat(score);
							 maxScore = Float.parseFloat(score);	
						 if(minScore > Float.parseFloat(score)) {
                             minScore = Float.parseFloat(score);
                         }
						 if(maxScore < Float.parseFloat(score)) {
                             maxScore = Float.parseFloat(score);
                         }
						 
						 	//5.0以上版本
							if(this.userView.getVersion()>=50)
							{
								String name = (String)pointNameMap.get(pointid);
								
								if("true".equalsIgnoreCase(signLogo))
								{
									if(name.length()<m)
									{
										for(int k=name.length();k<=m;k++)
										{
											name = name+" ";
										}
									}
								}
								
								if("41".equals(chart_type))
								 {
									 if(logo!=null && logo.trim().length()>0 && "logo".equalsIgnoreCase(logo)) {
                                         name=this.warpRowStr(name,13);
                                     } else {
                                         name=this.warpRowStr(name,25);
                                     }
								 }else {
                                    name=this.warpRowStr(name,6);
                                }
								//liuy 2015-12-04
								if("2".equals(isShowPercentVal)) {
                                    list.add(new CommonData(Integer.toString(level),name));
                                } else {
                                    list.add(new CommonData(score,name));
                                }
							}else
								if("2".equals(isShowPercentVal)) {
                                    list.add(new CommonData(Integer.toString(level),pointid));
                                } else {
                                    list.add(new CommonData(score,pointid));
                                }
								//liuy 2015-12-04
	
						 if(minRadarScore > minScore) {
                             minRadarScore = minScore;
                         }
							 
						 if(maxRadarScore < maxScore) {
                             maxRadarScore = maxScore;
                         }
				
						 dataMap.put(ResourceFactory.getProperty("lable.examine.avgScore"),list);
					 }		 
			 }
			 /**********最低得分**********/
			if((","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.lowestscore")+",")!=-1)
	     	{
				 rowSet=dao.search(sql_min);//最低得分
				 list=new ArrayList();
				 while(rowSet.next())
				 {
						 String pointid =rowSet.getString("point_id")!=null?rowSet.getString("point_id"):"";
						 String score = rowSet.getString("score")!=null?rowSet.getString("score"):"0";
						 if(condition.indexOf(pointid)==-1){
							 continue;
						 }
						 if(pointid==null|| "".equals(pointid)){
							 continue;
						 }
						 float theScore=Float.parseFloat((String)pointScoreMap.get(pointid)==null?"0":(String)pointScoreMap.get(pointid));
						 if("1".equals(isShowPercentVal) && theScore>0)
						 {
							 float x = Float.parseFloat(score)*100/theScore;
							 score = Float.toString(x);
						 //liuy 2015-12-04
						 }else if("2".equals(isShowPercentVal)){
							 float tempScore = Float.parseFloat(score);
							 level = getLevel(tempScore, theScore, perGradeCompetenceList, getDomainFlag(planid));
						 }//liuy 2015-12-04
							 minScore = Float.parseFloat(score);
							 maxScore = Float.parseFloat(score);	
						 if(minScore > Float.parseFloat(score)) {
                             minScore = Float.parseFloat(score);
                         }
						 if(maxScore < Float.parseFloat(score)) {
                             maxScore = Float.parseFloat(score);
                         }
						 
						 	//5.0以上版本
							if(this.userView.getVersion()>=50)
							{
								String name = (String)pointNameMap.get(pointid);
								
								if("true".equalsIgnoreCase(signLogo))
								{
									if(name.length()<m)
									{
										for(int k=name.length();k<=m;k++)
										{
											name = name+" ";
										}
									}
								}
								
								if("41".equals(chart_type))
								 {
									 if(logo!=null && logo.trim().length()>0 && "logo".equalsIgnoreCase(logo)) {
                                         name=this.warpRowStr(name,13);
                                     } else {
                                         name=this.warpRowStr(name,25);
                                     }
								 }else {
                                    name=this.warpRowStr(name,6);
                                }
								//liuy 2015-12-04
								if("2".equals(isShowPercentVal)) {
                                    list.add(new CommonData(Integer.toString(level),name));
                                } else {
                                    list.add(new CommonData(score,name));
                                }
							}else
								if("2".equals(isShowPercentVal)) {
                                    list.add(new CommonData(Integer.toString(level),pointid));
                                } else {
                                    list.add(new CommonData(score,pointid));
                                }
								//liuy 2015-12-04
	
						 if(minRadarScore > minScore) {
                             minRadarScore = minScore;
                         }
							 
						 if(maxRadarScore < maxScore) {
                             maxRadarScore = maxScore;
                         }
					 }

				
			     		dataMap.put(ResourceFactory.getProperty("lable.examine.lowestscore"),list);
			   }		 
			 if(minRadarScore>=maxRadarScore) {
                 minRadarScore=0;
             }
			 if(maxRadarScore==0 || maxRadarScore==0.0) {
                 maxRadarScore=5;
             }
			 if("41".equals(chart_type))
			 {
				 //liuy 2015-12-04
				 if("2".equals(isShowPercentVal)) {
                     dataMap.put("minmax",0+","+maxLevel);
                 } else{
					 //dataMap.put("minmax",minRadarScore+","+maxRadarScore);
					 dataMap.put("minmax",0+","+maxRadarScore);
				 }//liuy 2015-12-04
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return dataMap;
	 }
	 /**换行处理横坐标（由于最新的echarts里面自己进行了截断，这边不需要再截断）*/
	private String warpRowStr(String name,int num)
	{
		/*if((name.indexOf(":")!=-1))
			name = name.substring(0,name.indexOf(":"));
		else if((name.indexOf("：")!=-1))
			name = name.substring(0,name.indexOf("："));		
		
		if(name.length()>num)
		{
			int div = name.length()/num;
			String temp = "";
			for(int index=0;index<div;index++)
			{
				temp+=name.substring(index*num, (index+1)*num)+"\r";
			}
			temp+=name.substring(div*num);
			name=temp;
		}*/
		return name;
	}
	 
	 /**
	  * 根据考核对象 和 绩效指标 找到相应计划
	  * @param planList
	  * @param point_id
	  * @param a0100
	  * @return
	  */
	 public String getPlanByPoint(String point_id,String a0100)
	 {
		 StringBuffer planids=new StringBuffer();
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 StringBuffer sql=new StringBuffer("select per_plan.*,per_template.name templateName ");
			 sql.append(" from per_plan,per_template,per_object where per_plan.template_id=per_template.template_id ");
			 sql.append(" and per_plan.plan_id=per_object.plan_id and per_plan.status=7 ");
			 if(a0100.trim().length()>0) {
                 sql.append(" and per_object.object_id='"+a0100+"' ");
             }
			 RowSet rowSet=dao.search(sql.toString());
			 while(rowSet.next())
			 {
				 String temp=rowSet.getString("plan_id");
				 String template_id=rowSet.getString("template_id");
				 
				 String sql2="";
				 if("i_".equalsIgnoreCase(point_id.substring(0,2)))//个性项目
                 {
                     sql2="select * from per_template_item where template_id='"+template_id+"' and item_id="+point_id.substring(2);
                 } else {
                     sql2 = "select po.point_id,po.pointname from per_template_item pi,per_template_point pp,per_point po "
                           +" where pi.item_id=pp.item_id and pp.point_id=po.point_id "
                           +" and template_id='"+template_id+"' and lower(po.point_id)='"+point_id.toLowerCase()+"'";
                 }
				 RowSet rowset2=dao.search(sql2);
				 if(rowset2.next()) {
                     planids.append(temp+",");
                 }
			 }
			 if(planids.length()>0) {
                 planids.setLength(planids.length()-1);
             }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return planids.toString();
	 }
	 
	 public ArrayList getPlanList(String planIds)
	 {
	     ArrayList list = new ArrayList();
	     if(planIds==null || planIds.trim().length()<=0) {
             return list;
         }
	     ContentDAO dao = new ContentDAO(this.conn);
	     RowSet rowSet=null;
	     try
	     {
	    	 rowSet=dao.search("select plan_id,name from per_plan where plan_id in ("+planIds+") order by plan_id");
	    	 while(rowSet.next())
	    	 {
			
	    		 String plan_id=rowSet.getString("plan_id");
	    		 String name=rowSet.getString("name");
	    		 list.add("plan_"+plan_id+":"+name);
			 
	    	 }
	     } catch (SQLException e)
	     {
	    	 e.printStackTrace();
	     }
	     return list;
	 }
	 
	 /**
	  * 取得单指标分析数据
	  * @param planIds
	  * @param pointID
	  * @param a0100
	  * @return
	  */
	 public HashMap getSinglePointAnalyse(String planIds,String pointID,String a0100,String pointName)
	 {
		 HashMap dataMap=new HashMap();
		 if(a0100==null || pointID.length()==0) {
             return dataMap;
         }
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 planIds=planIds.replaceAll(",","','");
			 boolean isItem=false;
			 if("i_".equalsIgnoreCase(pointID.substring(0,2)))
			 {
				 pointID=pointID.substring(2);
				 isItem=true;
			 }
			 RowSet rowSet=null;
			 rowSet=dao.search("select plan_id,name,template_id from per_plan where plan_id in ('"+planIds+"') order by plan_id");
			
//			 while(rowSet.next())
//			 {
//				 ArrayList list=new ArrayList();
//				 String plan_id=rowSet.getString("plan_id");
//				 String name=rowSet.getString("name");
//				
//				 String template_id=rowSet.getString("template_id");
//				 
//				 String sql="select score from  per_history_result where lower(point_id)='"+pointID.toLowerCase()+"' and object_id='"+a0100+"' and plan_id="+plan_id;
//				 if(isItem)
//					 sql+=" and status=1 ";
//				 else
//					 sql+=" and status=0 ";
//				 
//				 RowSet rowSet2=dao.search(sql);
//				 if(rowSet2.next())
//				 {
//					list.add(new CommonData(rowSet2.getString(1)!=null?PubFunc.round(rowSet2.getString(1),1):"0",name));
//				 }
//				 else
//					 list.add(new CommonData("0",name));
//				 dataMap.put(name,list);
//			 }
			 ArrayList list=new ArrayList();
			 while(rowSet.next())
			 {
				
				 String plan_id=rowSet.getString("plan_id");
				 String name=rowSet.getString("name");
				 String categoryName = "plan_"+plan_id;
				 //5.0以上版本
					if(this.userView.getVersion()>=50)
					{
						categoryName =name;
						categoryName = categoryName;//让横坐标竖写
					}
				 String template_id=rowSet.getString("template_id");
				 
				 String sql="select score from  per_history_result where lower(point_id)='"+pointID.toLowerCase()+"' and object_id='"+a0100+"' and plan_id="+plan_id;
				 if(isItem) {
                     sql+=" and status=1 ";
                 } else {
                     sql+=" and status=0 ";
                 }
				 
				 RowSet rowSet2=dao.search(sql);
				 if(rowSet2.next())
				 {
//					list.add(new CommonData(rowSet2.getString(1)!=null?PubFunc.round(rowSet2.getString(1),1):"0",name));
					list.add(new CommonData(rowSet2.getString(1)!=null?rowSet2.getString(1):"0",categoryName));
				 }
				 else
//					 list.add(new CommonData("0",name));	
                 {
                     list.add(new CommonData("0",categoryName));
                 }
			 }
//			 dataMap.put(pointID,list);
			 dataMap.put(pointName, list);
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 
		 return dataMap; 
	 }
	 
	 /**
	  * 取得计划中的模板列表
	  * @param planList
	  * @return
	  */
	 public ArrayList getStencilList(ArrayList planList)
	 {
		 ArrayList list=new ArrayList();
		 list.add(new CommonData("-1",ResourceFactory.getProperty("edit_report.All")));
		 StringBuffer exists=new StringBuffer(",");
		 for(int i=0;i<planList.size();i++)
		 {
				LazyDynaBean abean=(LazyDynaBean)planList.get(i);
				String template_id=(String)abean.get("template_id");
				String templateName=(String)abean.get("templateName");
				if(exists.indexOf(","+template_id+",")==-1)
				{	
					list.add(new CommonData(template_id,templateName));
					exists.append(template_id+",");
				}
		 }
		 return list;
	 }
	 
	 /**
	  * 取得考核周期列表
	  * @return
	  */
	 public ArrayList getPeriodList()
	 {
		 ArrayList list=new ArrayList();
		 list.add(new CommonData("-1",ResourceFactory.getProperty("edit_report.All")));
		 list.add(new CommonData("0",ResourceFactory.getProperty("jx.khplan.yeardu")));
		 list.add(new CommonData("1",ResourceFactory.getProperty("jx.khplan.halfyeardu")));
		 list.add(new CommonData("2",ResourceFactory.getProperty("jx.khplan.quarter")));
		 list.add(new CommonData("3",ResourceFactory.getProperty("jx.khplan.monthdu")));
		 list.add(new CommonData("7",ResourceFactory.getProperty("jx.khplan.indefinetime")));
		 
		 return list;
	 }
	 
	/***************************************     取得统计分析图表      *******************************/ 
	 /**
	  * 取得计划的考核对象数
	  * @param plan_id
	  * @return
	  */
	 public int getPlanObjectNum(String plan_id)
	 {
		int num=0; 
		try
		{
			 ContentDAO dao = new ContentDAO(this.conn);
			 PerformanceImplementBo pb=new PerformanceImplementBo(this.conn);
			 String whl = pb.getPrivWhere(this.userView);//根据用户权限先得到一个考核对象的范围	
			 String sql = "select count(object_id) from per_result_"+plan_id+" where 1=1 "+whl;
			 RowSet rowSet=dao.search(sql);
			 if(rowSet.next()) {
                 num=rowSet.getInt(1);
             }
		}
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		return num;
	 }	 
	 
	 /**
	  * 取得标准标度数据列表
	  * @return
	  */
	 public ArrayList getStandGradeList(String template_id)
	 {
		 ArrayList list=new ArrayList();
		 RowSet rowSet = null;
		 try
		 {
			 PersonPostModalBo ppo = new PersonPostModalBo(this.conn);
			 String per_comTable = "per_grade_template"; // 绩效标准标度
			 if(ppo.getComOrPer(template_id,"temp")) {
                 per_comTable = "per_grade_competence"; // 能力素质标准标度
             }
			 ContentDAO dao = new ContentDAO(this.conn);
			 rowSet = dao.search("select * from "+per_comTable+"");
			 LazyDynaBean abean=new LazyDynaBean();
			 while(rowSet.next())
			 {
				 abean=new LazyDynaBean();
				 abean.set("grade_template_id", rowSet.getString("grade_template_id"));
				 abean.set("gradevalue", rowSet.getString("gradevalue")!=null?rowSet.getString("gradevalue"):"0");
				 abean.set("gradedesc", rowSet.getString("gradedesc")!=null?rowSet.getString("gradedesc"):"");
				 abean.set("top_value", rowSet.getString("top_value")!=null?rowSet.getString("top_value"):"0");
				 abean.set("bottom_value",rowSet.getString("bottom_value")!=null?rowSet.getString("bottom_value"):"0");
				 list.add(abean);
			 }
			 
			 if(rowSet!=null) {
                 rowSet.close();
             }
			 
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return list;
	 }
	 
	 public String getStatTitle(String context)
	 {
		 StringBuffer title = new StringBuffer();
		 String[] temp=context.split("/");
		 RowSet rowSet=null;
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 String sql0="select pointname from per_point where point_id='"+temp[0]+"'";
			 rowSet = dao.search(sql0);
			 if(rowSet.next()) {
                 title.append(rowSet.getString(1));
             }
			 sql0="select gradedesc from per_grade where point_id='"+temp[0]+"' and gradecode='"+temp[1].toUpperCase()+"'";
			 rowSet=dao.search(sql0);
			 if(rowSet.next()) {
                 title.append("("+rowSet.getString(1)+")");
             }
			 
			 if(rowSet!=null) {
                 rowSet.close();
             }
			 
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 
		 return title.toString();
	 }
	 /**
	  * 取得反查数据
	  * @param plan_id
	  * @param context
	  * @param object_type  1:团队  2：人员  3:单位 4:部门
	  * @return
	  */
	 public ArrayList getReversResult_list(String plan_id,String context,int object_type)
	 {
		 /**
		  * 根据用户权限先得到一个考核对象的范围  JinChunhai 2011.05.17
		  */
		 PerformanceImplementBo pb=new PerformanceImplementBo(this.conn);
		 String whl = pb.getPrivReverseWhere(this.userView);
		 
		 ArrayList list=new ArrayList();
		 String[] temp=context.split("/");
		 String sql="select po.object_id,"+temp[0]+",po.b0110,po.e0122,po.e01a1,po.a0101 from per_result_"+plan_id+" pr,per_object po where pr.object_id=po.object_id and po.plan_id="+plan_id+" "+whl;
	
		 try
		 {
			 LoadXml loadxml = new LoadXml(this.conn, plan_id);
			 Hashtable planParamSet = new Hashtable();
			 planParamSet = loadxml.getDegreeWhole();			 
			 int KeepDecimal = Integer.parseInt((String)planParamSet.get("KeepDecimal")); // 保留的小数位数			 			 
			 
			 ContentDAO dao = new ContentDAO(this.conn);
			 RowSet rowSet=null;
			if("C_".equalsIgnoreCase(temp[0].substring(0, 2)))//指标反查
			{
				 ArrayList pointDescList=new ArrayList();
				 String sql0="select pp.point_id,pp.item_id,per_point.pointname,per_grade.gradevalue,per_grade.gradecode,pp.score from per_template_item pi,per_template_point pp,per_plan,per_point,per_grade "
				 		+" where pi.item_id=pp.item_id " 
				 		+" and pi.template_id=per_plan.template_id and pp.point_id=per_point.point_id and per_point.point_id=per_grade.point_id and per_plan.plan_id="+plan_id+" and pp.point_id='"+temp[0].substring(2)+"' order by pp.point_id,per_grade.gradevalue desc";
				 rowSet=dao.search(sql0);
				 double maxGradeValue = 0;//记录下最大分数=最大的系数*分值，现在采取的是上限不封闭，下限封闭，但是在满分的时候没有显示出满分的人
				 while(rowSet.next())
				 {
					 LazyDynaBean abean=new LazyDynaBean();
					 String gradevalue = rowSet.getString("gradevalue")!=null?rowSet.getString("gradevalue"):"0";
					 String score = rowSet.getString("score")!=null?rowSet.getString("score"):"0";
					 if(maxGradeValue == 0) {
						 maxGradeValue = Double.parseDouble(gradevalue)*Double.parseDouble(score);
					 }
					 abean.set("gradevalue", gradevalue);
					 abean.set("gradecode", rowSet.getString("gradecode")!=null?rowSet.getString("gradecode"):"");
					 abean.set("score", score);
					 pointDescList.add(abean);
				 }
				 
				 rowSet=dao.search(sql);
				 while(rowSet.next())
				 {
					 float score=rowSet.getFloat(2);
					 String b0110=rowSet.getString("b0110")!=null?rowSet.getString("b0110"):"";
					 String e0122=rowSet.getString("e0122")!=null?rowSet.getString("e0122"):"";
					 String e01a1=rowSet.getString("e01a1")!=null?rowSet.getString("e01a1"):"";
					 String a0101=rowSet.getString("a0101");
					 double gradeValue1=0;
					 double gradeValue2=0;
					 for(int i=0;i<pointDescList.size();i++)
					 {
						 LazyDynaBean abean=(LazyDynaBean)pointDescList.get(i);
						 if(((String)abean.get("gradecode")).equalsIgnoreCase(temp[1]))
						 {	 
							 double a_score=Double.parseDouble((String)abean.get("score"));
							 gradeValue1=Double.parseDouble((String)abean.get("gradevalue"))*a_score;
							 if(i<pointDescList.size()-1) {
                                 gradeValue2=Double.parseDouble((String)((LazyDynaBean)pointDescList.get(i+1)).get("gradevalue"))*a_score;
                             } else {
                                 gradeValue2=-1*a_score;
                             }
							 break;
						 }
					 }
					 //当是最大分数的时候，score<=gradeValue1
					 if(((maxGradeValue==gradeValue1)?(score<=gradeValue1):(score<gradeValue1))&&score>=gradeValue2)
					 {
						 LazyDynaBean abean=new LazyDynaBean();
						 if(object_type==1) //团队
						 {
							 abean.set("b0110", AdminCode.getCodeName("UN", b0110));
							 abean.set("a0101",a0101);
							 abean.set("score", PubFunc.round(String.valueOf(score), KeepDecimal));
//							 abean.set("score", String.valueOf(score));							 
						 }
						 else if(object_type==2) //人员
						 {
							 abean.set("b0110", AdminCode.getCodeName("UN", b0110));
							 abean.set("e0122", AdminCode.getCodeName("UM", e0122));
							 abean.set("e01a1", AdminCode.getCodeName("@K", e01a1));
							 abean.set("a0101", a0101);
							 abean.set("score", PubFunc.round(String.valueOf(score), KeepDecimal));
//							 abean.set("score", String.valueOf(score));
						 } else if(object_type==3) //单位
						 {
							 abean.set("b0110", AdminCode.getCodeName("UN", b0110));
							 abean.set("a0101", a0101);
							 abean.set("score", PubFunc.round(String.valueOf(score), KeepDecimal));
//							 abean.set("score", String.valueOf(score));
						 } else if(object_type==4) //部门
						 {
							 abean.set("b0110", AdminCode.getCodeName("UN", b0110));
							 abean.set("e0122", AdminCode.getCodeName("UM", e0122));
							 abean.set("a0101", a0101);
							 abean.set("score", PubFunc.round(String.valueOf(score), KeepDecimal));
//							 abean.set("score", String.valueOf(score));
						 }
						 list.add(abean);
					 }
					 
				 }
				 
			}else if( "T_".equalsIgnoreCase(temp[0].substring(0, 2)))//个性项目反查
			{
				RecordVo planVo = getPlanVo(plan_id);
				ArrayList standGradeList = getStandGradeList(planVo.getString("template_id"));
				String itemscore = "";
				rowSet=dao.search("select pi.* from per_template_item pi,per_plan where per_plan.template_id=pi.template_id and pi.kind=2 and per_plan.plan_id="+plan_id+" and item_id="+temp[0].substring(2));
				if(rowSet.next()) {
                    itemscore=rowSet.getString("score")!=null?rowSet.getString("score"):"0";
                }
					
//				PerformanceImplementBo pb=new PerformanceImplementBo(this.conn);
//				String whl = pb.getPrivWhere(userView);//根据用户权限先得到一个考核对象的范围
//				String sqlStr = "select sum(p0413*p0415) from p04 where plan_id="+plan_id+" and item_id="+temp[0].substring(2)+" and ( ( state=-1 and chg_type!=3 ) or state is null or state<>-1 ) ";
//				sqlStr+=whl;
//				rowSet=dao.search(sqlStr);	
//				if(rowSet.next())					
//					itemscore=new Float(rowSet.getFloat(1)).toString();					
					
				 rowSet=dao.search(sql);
				 while(rowSet.next())
				 {
					 float score=rowSet.getFloat(2);
					 String b0110=rowSet.getString("b0110")!=null?rowSet.getString("b0110"):"";
					 String e0122=rowSet.getString("e0122")!=null?rowSet.getString("e0122"):"";
					 String e01a1=rowSet.getString("e01a1")!=null?rowSet.getString("e01a1"):"";
					 String a0101=rowSet.getString("a0101");
	
					 for(int i=0;i<standGradeList.size();i++)
					 {
						 LazyDynaBean tempbean=(LazyDynaBean)standGradeList.get(i);
						 if(((String)tempbean.get("grade_template_id")).equalsIgnoreCase(temp[1]))
						 {	 
							 float top_value=Float.parseFloat((String)tempbean.get("top_value"))*Float.parseFloat(itemscore);
							 float bottom_value=Float.parseFloat((String)tempbean.get("bottom_value"))*Float.parseFloat(itemscore);
							 
							 // 【4888】绩效管理：绩效分析/统计分析/操作如图点击表格中的数据，却不显示内容 by lium
							 if((score>0 && score<=top_value&&score>bottom_value) || (score==0 && score<=/* "<" 改成 "<=" */top_value&&score>=bottom_value))
							 {
								 LazyDynaBean abean=new LazyDynaBean();
								 if(object_type==1) //团队
								 {
									 abean.set("b0110", AdminCode.getCodeName("UN", b0110));
									 abean.set("a0101",a0101);
									 abean.set("score", PubFunc.round(String.valueOf(score), KeepDecimal));
//									 abean.set("score", String.valueOf(score));									 
								 }
								 else if(object_type==2) //人员
								 {
									 abean.set("b0110", AdminCode.getCodeName("UN", b0110));
									 abean.set("e0122", AdminCode.getCodeName("UM", e0122));
									 abean.set("e01a1", AdminCode.getCodeName("@K", e01a1));
									 abean.set("a0101", a0101);
									 abean.set("score", PubFunc.round(String.valueOf(score), KeepDecimal));
//									 abean.set("score", String.valueOf(score));
								 } else if(object_type==3) //单位
								 {
									 abean.set("b0110", AdminCode.getCodeName("UN", b0110));
									 abean.set("a0101", a0101);
									 abean.set("score", PubFunc.round(String.valueOf(score), KeepDecimal));
//									 abean.set("score", String.valueOf(score));
								 } else if(object_type==4) //部门
								 {
									 abean.set("b0110", AdminCode.getCodeName("UN", b0110));
									 abean.set("e0122", AdminCode.getCodeName("UM", e0122));
									 abean.set("a0101", a0101);
									 abean.set("score", PubFunc.round(String.valueOf(score), KeepDecimal));
//									 abean.set("score", String.valueOf(score));
								 }
								 list.add(abean);
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
		 
		 return list;
	 }
	 
	 
	 
	 
	 
	 /**
	  * 取得各个指标的标度
	  * @param plan_id
	  * @return
	  */
	 public HashMap getPointGradeMap(String plan_id)
	 {
		 HashMap map=new HashMap();
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 String sql="select pp.point_id,pp.score,pp.item_id,per_point.pointname,per_grade.gradevalue,per_grade.gradecode from per_template_item pi,per_template_point pp,per_plan,per_point,per_grade "
				 	   +" where pi.item_id=pp.item_id "
				 	   +" and pi.template_id=per_plan.template_id and pp.point_id=per_point.point_id and per_point.point_id=per_grade.point_id and per_plan.plan_id="+plan_id+"  order by pp.point_id,per_grade.gradevalue desc";
			 RowSet rowSet=dao.search(sql);
			 String point_id="";
			 ArrayList list=new ArrayList();
			 while(rowSet.next())
			 {
				 String pointID=rowSet.getString("point_id");
				 if("".equals(point_id)) {
                     point_id=pointID;
                 }
				 if(!point_id.equals(pointID))
				 {
					 map.put(point_id, list);
					 list=new ArrayList();
					 point_id=pointID;
				 }
				 list.add(new CommonData(rowSet.getString("gradevalue"),rowSet.getString("gradecode")));
			 }
			 map.put(point_id, list);
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return map;
	 }
	 
	 
	 
	 
	 
	 
	 /**
	  * 取得考核计划涉及到的考核指标和对应分数
	  * @param plan_id
	  * @return
	  */
	 public ArrayList getPlanPointList(String plan_id)
	 {
		 ArrayList list=new ArrayList();
		 if(plan_id.trim().length()==0) {
             return list;
         }
		 RowSet rowSet=null;
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 String sql="select pp.point_id,pp.score,pp.item_id,per_point.pointname,pp.rank from per_template_item pi,per_template_point pp,per_plan,per_point "
				 	   +" where pi.item_id=pp.item_id " 	
				 	   +" and pi.template_id=per_plan.template_id and pp.point_id=per_point.point_id and per_plan.plan_id="+plan_id+" order by pp.seq";
			 rowSet=dao.search(sql);
			 LazyDynaBean abean=new LazyDynaBean();
			 while(rowSet.next())
			 {
				 abean=new LazyDynaBean();
				 abean.set("point_id", rowSet.getString("point_id"));
				 abean.set("score", rowSet.getString("score")!=null?rowSet.getString("score"):"0");
				 abean.set("rank",rowSet.getString("rank"));
				 abean.set("item_id", rowSet.getString("item_id"));
				 abean.set("pointname",rowSet.getString("pointname"));
				 list.add(abean);
			 }
			 
			 if(rowSet!=null) {
                 rowSet.close();
             }
			 
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return list;
	 }
	 
	 
	 /**
	  * 取得考核结果集里的数据
	  * @param planPointList
	  * @param plan_id
	  * @return
	  */
	 public HashMap getPointScoreResult(ArrayList planPointList,String plan_id,ArrayList itemList)
	 {
		HashMap map=new HashMap(); 
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 PerformanceImplementBo pb=new PerformanceImplementBo(this.conn);
			 String whl = pb.getPrivWhere(userView);//根据用户权限先得到一个考核对象的范围
			 String sql = "select * from per_result_"+plan_id+" where 1=1 "+whl;
			 RowSet rowSet=dao.search(sql);
			 LazyDynaBean abean=null;
			 while(rowSet.next())
			 {
				 for(int i=0;i<planPointList.size();i++)
				 {
					 abean=(LazyDynaBean)planPointList.get(i);
					 String point_id=(String)abean.get("point_id");
					 if(map.get(point_id)==null)
					 {
						 ArrayList list=new ArrayList();
						 list.add(rowSet.getString("C_"+point_id)!=null?rowSet.getString("C_"+point_id):"0");
						 map.put(point_id, list);
					 }
					 else
					 {
						 ArrayList list=(ArrayList)map.get(point_id);
						 list.add(rowSet.getString("C_"+point_id)!=null?rowSet.getString("C_"+point_id):"0");
						 map.put(point_id, list);
					 }
				 }
				 
				 
				 for(int i=0;i<itemList.size();i++)
				 {
					 abean=(LazyDynaBean)itemList.get(i);
					 String item_id=(String)abean.get("item_id");
					 if(map.get("T_"+item_id)==null)
					 {
						 ArrayList list=new ArrayList();
						 list.add(rowSet.getString("T_"+item_id)!=null?rowSet.getString("T_"+item_id):"0");
						 map.put("T_"+item_id, list);
					 }
					 else
					 {
						 ArrayList list=(ArrayList)map.get("T_"+item_id);
						 list.add(rowSet.getString("T_"+item_id)!=null?rowSet.getString("T_"+item_id):"0");
						 map.put("T_"+item_id, list);
					 }
				 }
			 }
			 
			 
			 
			 
			 
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		return map;
	 }
	 
	 
	 
	 /**
	  * 取得计划内指标标度个数
	  * @param plan_id
	  * @return
	  */
	 public HashMap getPointGradeNumMap(String plan_id,ArrayList standGradeList,ArrayList planPointList)
	 {
		 HashMap map=new HashMap();
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 RowSet  rowSet=dao.search("select pi.* from per_template_item pi,per_plan where per_plan.template_id=pi.template_id and pi.kind=2 and per_plan.plan_id="+plan_id);
			 ArrayList itemList=new ArrayList();
			 while(rowSet.next())
			 {
				 LazyDynaBean abean=new LazyDynaBean();
				 abean.set("item_id", rowSet.getString("item_id"));
				 abean.set("kind", rowSet.getString("kind"));
				 abean.set("itemdesc",rowSet.getString("itemdesc"));
				 abean.set("score",rowSet.getString("score")!=null?rowSet.getString("score"):"0");
				 abean.set("rank", rowSet.getString("rank")!=null?rowSet.getString("rank"):"0");
				 itemList.add(abean);
			 }
			 
			 HashMap pointResultMap=getPointScoreResult(planPointList,plan_id,itemList);
			 LazyDynaBean gradeBean=null;
			 HashMap pointGradeMap=getPointGradeMap(plan_id);
			 CommonData  data=null;
			 CommonData  data1=null;
			 for(int i=0;i<planPointList.size();i++)
			 {
				 LazyDynaBean abean=(LazyDynaBean)planPointList.get(i);
				 String point_id=(String)abean.get("point_id");
				 double score=Double.parseDouble((String)abean.get("score"));
				 ArrayList resultList=(ArrayList)pointResultMap.get(point_id);
				 if(resultList==null) {
                     continue;
                 }
  				 ArrayList  pointGradeList=(ArrayList)pointGradeMap.get(point_id);
				 
				 for(int n=0;n<resultList.size();n++)
				 {
					 float resultScore=Float.parseFloat((String)resultList.get(n));
					
					 for(int j=0;j<pointGradeList.size();j++)
					 {
						 data=(CommonData)pointGradeList.get(j);
						 String dataValue=data.getDataValue();
						 String dataName=data.getDataName().toLowerCase();
						 String dataValue1="-1";
						 if(j<pointGradeList.size()-1)
						 {
							 data1=(CommonData)pointGradeList.get(j+1);
							 dataValue1=data1.getDataValue();
						 }
						 
						 double topValue=Double.parseDouble(dataValue)*score;
						 double bottom_value=Double.parseDouble(dataValue1)*score;
						
						 if(resultScore<=topValue&&resultScore>=bottom_value)
						 {
							if(map.get(point_id+"_"+dataName)==null) 
							{
								map.put(point_id+"_"+dataName, "1");
							}
							else
							{
								int temp=Integer.parseInt((String)map.get(point_id+"_"+dataName));
								map.put(point_id+"_"+dataName, String.valueOf(++temp));
							}
							break; 
						 }
					 }
				 
				 }
			 }
			 
			 PerformanceImplementBo pb=new PerformanceImplementBo(this.conn);
			 String whl = pb.getPrivWhere(this.userView);//根据用户权限先得到一个考核对象的范围			 
			 
			////////////
			 LazyDynaBean bean=null;
			 LazyDynaBean bean2=null;
			 for(int i=0;i<itemList.size();i++)
			 {
				 LazyDynaBean abean=(LazyDynaBean)itemList.get(i);
				 String item_id=(String)abean.get("item_id");
				 String itemdesc = (String)abean.get("itemdesc");
				 String kind=(String)abean.get("kind");
				 float score=Float.parseFloat((String)abean.get("score"));
				 //说明： 对于个性项目的算法 暂时直接取模版中定义的项目分 其实应该针对每个考核对象从p04表中找到自己的项目分(由于每个考核对象个性项目下的任务指标都不一样),再从结果表取自己的项目得分 再确定标度去
//				 if(kind.equals("2"))//个性项目的值通过
//				 {
//					 String sql = "select sum(p0413*p0415) from p04 where plan_id="+plan_id+" and item_id="+item_id+" and ( ( state=-1 and chg_type!=3 ) or state is null or state<>-1 )";
//					 sql+=whl;
//					 rowSet=dao.search(sql);
//					 if(rowSet.next())
//						 score = rowSet.getFloat(1);
//				 }
				 ArrayList resultList=(ArrayList)pointResultMap.get("T_"+item_id);
  				
				 if(resultList!=null && resultList.size()>0)
				 {
					 for(int n=0;n<resultList.size();n++)
					 {
						 float resultScore=Float.parseFloat((String)resultList.get(n));
						 if(score!=0.0)
						 {
							 for(int j=0;j<standGradeList.size();j++)
							 {
								 bean=(LazyDynaBean)standGradeList.get(j); 
								 String dataValue=(String)bean.get("gradevalue");
								 String dataName=((String)bean.get("grade_template_id")).toLowerCase();
								 String dataValue1="-1";
								 if(j<standGradeList.size()-1)
								 {
									 bean2=(LazyDynaBean)standGradeList.get(j+1);
									 dataValue1=(String)bean2.get("gradevalue");
								 }
								 
								 float topValue=Float.parseFloat(dataValue)*score;
								 float bottom_value=Float.parseFloat(dataValue1)*score;
								
								 if(resultScore<=topValue&&resultScore>bottom_value)
								 {
									if(map.get(item_id+"_"+dataName)==null) 
									{
										map.put(item_id+"_"+dataName, "1");
									}
									else
									{
										int temp=Integer.parseInt((String)map.get(item_id+"_"+dataName));
										map.put(item_id+"_"+dataName, String.valueOf(++temp));
									}
									break; 
								 }
							 }
						 }else
						 {
							 	bean=(LazyDynaBean)standGradeList.get(standGradeList.size()-1); 
							 	String dataValue=(String)bean.get("gradevalue");
							 	String dataName=((String)bean.get("grade_template_id")).toLowerCase();
								if(map.get(item_id+"_"+dataName)==null) 
								{
									map.put(item_id+"_"+dataName, "1");
								}
								else
								{
									int temp=Integer.parseInt((String)map.get(item_id+"_"+dataName));
									map.put(item_id+"_"+dataName, String.valueOf(++temp));
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
		
		 return map;
	 }
	 
	 
	 
	 
	 
	 
	 public String getNameByStatus(int status)
	    {

		String temp = "";
		switch (status)
		{
		case 3:
		    temp = ResourceFactory.getProperty("org.performance.Published");
		    break;
		case 4:
		    temp = ResourceFactory.getProperty("kh.field.started");
		    break;
		case 5:
		    temp = ResourceFactory.getProperty("kh.field.pause");
		    break;
		case 6:
		    temp = ResourceFactory.getProperty("kh.field.startEvaluation");
		    break;
		case 7:
		    temp =ResourceFactory.getProperty("kh.field.finished");
		    break;
		}
		return temp;
	    }
	 
	 
	 /**
	  * 取得考核计划 对应的考核等级数据
	  * @param planid
	  * @return
	  */
	 public ArrayList getPerDegreeList(String planid)
	 {
		 ArrayList list=new ArrayList();
		 try
		 {
			 if(planid.trim().length()==0) {
                 return list;
             }
			 ContentDAO dao = new ContentDAO(this.conn);
			 LoadXml loadxml = new LoadXml(this.conn,planid);
			 Hashtable  planParamSet = loadxml.getDegreeWhole();
			 
			 // GradeClass:与目标卡评分时的逻辑保持一致 modify by 刘蒙
			 String EvalClass = (String) planParamSet.get("EvalClass"); // 在计划参数中的等级分类ID
			 String GradeClass =(String) planParamSet.get("GradeClass");
			 GradeClass = EvalClass == null || "0".equals(EvalClass.trim()) ? GradeClass : EvalClass;
			 
			 RowSet rowSet = dao.search("select pds.id,pds.itemname from per_degree pd,per_degreedesc pds where pd.degree_id=pds.degree_id and pd.degree_id=" + GradeClass
					    + " order by pds.topscore desc");
				    while (rowSet.next())
				    {
						LazyDynaBean abean = new LazyDynaBean();
						abean.set("id", rowSet.getString("id"));
						abean.set("itemname", rowSet.getString("itemname"));
						list.add(abean);
				    }
		 
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return list;
	 }
	 
	 
	 /**
	  * 取得 考核计划 总体评价数据
	  * @param planid
	  * @param codeitemid
	  * @param perDegreeList
	  * @return
	  */
	 public ArrayList getWholeEvalDataList(String planid,String codeitemid,ArrayList perDegreeList)
	 {
	     
		 ArrayList list=new ArrayList();
		 try
		 {
			 if(planid.trim().length()==0) {
                 return list;
             }
				 
			 ContentDAO dao = new ContentDAO(this.conn);
			 HashMap objectNum=new HashMap(); //考核对象总体评价数量
			 RowSet rowSet=dao.search("select count(object_id) as v_sum,object_id from per_mainbody where plan_id="+planid+" and whole_grade_id is not null group by object_id");
			// RowSet rowSet=dao.search("select object_id,v_sum from per_result_"+ planid +" ");
			 while(rowSet.next())
			 {
				 objectNum.put(rowSet.getString("object_id"),rowSet.getString("v_sum"));
			 }
			 ///////////////////////////////////////////////
			 rowSet=dao.search("select * from per_mainbody where plan_id="+planid+" and whole_grade_id is not null order by object_id,whole_grade_id");
			 HashMap objectRecordMap=new HashMap();
			 String object_id="";
			 ArrayList templist=new ArrayList();
			 while(rowSet.next())
			 {
				 String objectid=(String)rowSet.getString("object_id");
				 if(object_id.length()==0) {
                     object_id=objectid;
                 }
				 if(!object_id.equals(objectid))
				 {
					 objectRecordMap.put(object_id,templist);
					 object_id=objectid;
					 templist=new ArrayList();
				 }
				 templist.add(rowSet.getString("whole_grade_id"));
			 }
			 objectRecordMap.put(object_id,templist);
			 ////////////////////////////////////////////////
			 String sql="select * from per_object where plan_id="+planid;
			 PerformanceImplementBo pb=new PerformanceImplementBo(this.conn,this.userView,planid);
			 sql+=pb.getPrivWhere(this.userView);			 
			 
			 if(!"-1".equals(codeitemid))
			 {
				 if("UN".equalsIgnoreCase(codeitemid.substring(0,2))) {
                     sql+=" and b0110 like '"+codeitemid.substring(2)+"%'";
                 } else if("UM".equalsIgnoreCase(codeitemid.substring(0,2))) {
                     sql+=" and e0122 like '"+codeitemid.substring(2)+"%'";
                 } else {
                     sql+=" and object_id = '" + codeitemid + "'";
                 }
			 }
			 sql+=" order by B0110, E0122, E01A1, A0000";
			 rowSet=dao.search(sql);
			 LazyDynaBean abean=new LazyDynaBean();
			 while(rowSet.next())
			 {
				 abean=new LazyDynaBean();
				 abean.set("object_id",rowSet.getString("object_id"));
				 abean.set("a0101",rowSet.getString("a0101"));
				 
				 for(int i=0;i<perDegreeList.size();i++)
				 {
					 LazyDynaBean a_bean=(LazyDynaBean)perDegreeList.get(i);
					 String id=(String)a_bean.get("id");
					 int num=0;
					 if(objectRecordMap.get(rowSet.getString("object_id"))!=null)
					 {
						 ArrayList tempList=(ArrayList)objectRecordMap.get(rowSet.getString("object_id"));
						 for(int j=0;j<tempList.size();j++)
						 {
							 if(id.equalsIgnoreCase((String)tempList.get(j))) {
                                 num++;
                             }
						 }
					 }
					 abean.set(id,String.valueOf(num));
					 String percent_str="0%";
					 // 非空判断
					 Object objectScore = objectNum.get(rowSet.getString("object_id"));
					 if(num>0 && objectScore != null)
					 {
					//	 double total=((Integer)objectNum.get(rowSet.getString("object_id"))).intValue()*1.0;
						 double total=Double.parseDouble((String)objectScore);
						 double d=(num/total)*100;
//						 percent_str=PubFunc.round(String.valueOf(d),0)+"%";
						 
						 DecimalFormat df = new DecimalFormat("0.00");//保留两位小数
						 String strPer = df.format(d) ;
						 df = new DecimalFormat("###############.#####");//去掉小数点后面的0
						 strPer=df.format(Double.parseDouble(strPer));
						 percent_str=strPer+"%";
						 
					 }
					 abean.set(id+"%",percent_str);
				 }
				 list.add(abean);
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return list;
	 }
	 
	 
	 /**
	  * 取得考核对象评语
	  * @param planid
	  * @param object_id
	  * @return
	  */
	 public String getPerObjectRemark(String planid,String object_id)
	 {
		 String remark="";
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 RowSet rowSet=dao.search("select appraise from per_result_"+planid+" where object_id='"+object_id+"'");
			 if(rowSet.next()) {
                 remark=Sql_switcher.readMemo(rowSet,"appraise");
             }
			 
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 
		 return remark;
	 }
	 
	 
	 
	 
	 /**
	  * 取得考核计划列表
	  * @param status
	  * @param flag    1：包含总体评价 2:包含了解程度
	  * @param type    0:所有  1：不包括目标考核
	  * @return
	  */
	 public ArrayList getPlanList_commonData(String status,int flag,int type,UserView _userView,String plan_id,String busitype)
	 {
		 ArrayList list=new ArrayList();
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);			 
			 String sql="select * from per_plan where 1=1 ";
			 
			 if(busitype==null || busitype.trim().length()<=0) {
                 busitype = "0";
             }
			 if(busitype!=null && busitype.trim().length()>0 && "0".equalsIgnoreCase(busitype)) {
                 sql += " and ( busitype is null or busitype='' or busitype = '" + busitype + "') ";
             } else if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype)) {
                 sql += " and busitype = '" + busitype + "' ";
             }
			 
			 StringBuffer whlSql = new StringBuffer();	
			 ExamPlanBo bo = new ExamPlanBo(this.conn);		
			 whlSql.append(bo.getPlanWhlByObjTypePriv(_userView,busitype));
			 if(!"-1".equals(status))
			 {
				 if(plan_id==null||plan_id.trim().length()==0) {
                     whlSql.append(" and status in ("+status+")");
                 }
			 }
			 if(type==1) {
                 whlSql.append("  and ( Method=1 or method is null ) ");
             }
			 if(plan_id!=null&&plan_id.trim().length()>0) {
                 whlSql.append(" and plan_id="+plan_id);
             }
			 
			 sql += whlSql.toString();			 
			 sql += " order by "+Sql_switcher.isnull("a0000", "999999999")+" asc,plan_id desc";
			
			 HashMap map = bo.getPlansByUserView(_userView, whlSql.toString());	
			 
			 RowSet rowSet=dao.search(sql);
			 while(rowSet.next())
			 {
				 if(map.get(rowSet.getString("plan_id"))==null) {
                     continue;
                 }
				 
				 if(flag==1)
				 {
					 LoadXml loadxml = new LoadXml(this.conn,rowSet.getString("plan_id"));
					 Hashtable  planParamSet = loadxml.getDegreeWhole();
					 if (planParamSet.get("WholeEval") != null && ((String) planParamSet.get("WholeEval")).length() > 0 && "True".equalsIgnoreCase((String) planParamSet.get("WholeEval")))
					 {
						if (planParamSet.get("GradeClass") != null) {
                            list.add(new CommonData(rowSet.getString("plan_id"),rowSet.getString("plan_id")+"."+rowSet.getString("name")+"("+getNameByStatus(rowSet.getInt("status"))+")"));
                        }
					 }
				}
				else if(flag==2)
				{
					LoadXml loadxml = new LoadXml(this.conn,rowSet.getString("plan_id"));
					 Hashtable  planParamSet = loadxml.getDegreeWhole();
					 String NodeKnowDegree = (String) planParamSet.get("NodeKnowDegree");
					 if (NodeKnowDegree!=null&& "true".equalsIgnoreCase(NodeKnowDegree))
					 {
						 list.add(new CommonData(rowSet.getString("plan_id"),rowSet.getString("plan_id")+"."+rowSet.getString("name")+"("+getNameByStatus(rowSet.getInt("status"))+")"));
					 }
				}
				else {
                     list.add(new CommonData(rowSet.getString("plan_id"),rowSet.getString("plan_id")+"."+rowSet.getString("name")+"("+getNameByStatus(rowSet.getInt("status"))+")"));
                 }
				 
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return list;
	 }
	 /**
	  * 取得非按岗位素质测评的考核计划列表
	  * @param status
	  * @param flag    1：包含总体评价 2:包含了解程度
	  * @param type    0:所有  1：不包括目标考核
	  * @return
	  */
	 public ArrayList getPlanList_commonDataByModel(String status,int flag,int type,UserView _userView,String plan_id,String busitype)
	 {
		 ArrayList list=new ArrayList();
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);			 
			 String sql="select * from per_plan where 1=1  and ByModel ='0' or ByModel is null";
			 
			 if(busitype==null || busitype.trim().length()<=0) {
                 busitype = "0";
             }
			 if(busitype!=null && busitype.trim().length()>0 && "0".equalsIgnoreCase(busitype)) {
                 sql += " and ( busitype is null or busitype='' or busitype = '" + busitype + "') ";
             } else if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype)) {
                 sql += " and busitype = '" + busitype + "' ";
             }
			 
			 StringBuffer whlSql = new StringBuffer();	
			 ExamPlanBo bo = new ExamPlanBo(this.conn);		
			 whlSql.append(bo.getPlanWhlByObjTypePriv(_userView,busitype));
			 if(!"-1".equals(status))
			 {
				 if(plan_id==null||plan_id.trim().length()==0) {
                     whlSql.append(" and status in ("+status+")");
                 }
			 }
			 if(type==1) {
                 whlSql.append("  and ( Method=1 or method is null ) ");
             }
			 if(plan_id!=null&&plan_id.trim().length()>0) {
                 whlSql.append(" and plan_id="+plan_id);
             }
			 
			 sql += whlSql.toString();			 
			 sql += " order by "+Sql_switcher.isnull("a0000", "999999999")+" asc,plan_id desc";
			
			 HashMap map = bo.getPlansByUserView(_userView, whlSql.toString());	
			 
			 RowSet rowSet=dao.search(sql);
			 while(rowSet.next())
			 {
				 if(map.get(rowSet.getString("plan_id"))==null) {
                     continue;
                 }
				 
				 if(flag==1)
				 {
					 LoadXml loadxml = new LoadXml(this.conn,rowSet.getString("plan_id"));
					 Hashtable  planParamSet = loadxml.getDegreeWhole();
					 if (planParamSet.get("WholeEval") != null && ((String) planParamSet.get("WholeEval")).length() > 0 && "True".equalsIgnoreCase((String) planParamSet.get("WholeEval")))
					 {
						if (planParamSet.get("GradeClass") != null) {
                            list.add(new CommonData(rowSet.getString("plan_id"),rowSet.getString("plan_id")+"."+rowSet.getString("name")+"("+getNameByStatus(rowSet.getInt("status"))+")"));
                        }
					 }
				}
				else if(flag==2)
				{
					LoadXml loadxml = new LoadXml(this.conn,rowSet.getString("plan_id"));
					 Hashtable  planParamSet = loadxml.getDegreeWhole();
					 String NodeKnowDegree = (String) planParamSet.get("NodeKnowDegree");
					 if (NodeKnowDegree!=null&& "true".equalsIgnoreCase(NodeKnowDegree))
					 {
						 list.add(new CommonData(rowSet.getString("plan_id"),rowSet.getString("plan_id")+"."+rowSet.getString("name")+"("+getNameByStatus(rowSet.getInt("status"))+")"));
					 }
				}
				else {
                     list.add(new CommonData(rowSet.getString("plan_id"),rowSet.getString("plan_id")+"."+rowSet.getString("name")+"("+getNameByStatus(rowSet.getInt("status"))+")"));
                 }
				 
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return list;
	 }
	 
	 
	 public RecordVo get_TemplateVo(RecordVo plan_vo)
	{
			RecordVo vo=new RecordVo("per_template");
			try
			{
				vo.setString("template_id",plan_vo.getString("template_id"));
				ContentDAO dao = new ContentDAO(this.conn);
				vo=dao.findByPrimaryKey(vo);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return vo;
	}
	 
	 
	 private ArrayList leafItemList=new ArrayList();      //叶子项目列表
	 private ArrayList templateItemList=new ArrayList();  //模板项目记录表
	 private ArrayList planPointList=new ArrayList();
	 private HashMap   itemToPointMap=new HashMap();      //项目对应任务map
	 private HashMap    leafItemLinkMap=new HashMap();     //叶子项目对应的继承关系
	 private int lay=0;
	 private HashMap itemPointNum=new HashMap();
	 private int td_width=150;
	 private int td_height=30;
	 
	 /**
	  * 取得统计分析html
	  * @return
	  */
	 public String  getStatHtml(String plan_id)
	 {
		 String html="";
		 try
		 {
			 RecordVo planVo=getPlanVo(plan_id);
			 RecordVo templateVo=get_TemplateVo(planVo);
			 ArrayList standGradeList=getStandGradeList(planVo.getString("template_id"));
			 planPointList=getPlanPointList(plan_id);
			 HashMap pointGradeNumMap=getPointGradeNumMap(plan_id,standGradeList,this.planPointList);
			 this.templateItemList=getTemplateItemList(planVo.getString("template_id"));
			 get_LeafItemList();
			 this.itemToPointMap=getItemToPointMap();
			 this.leafItemLinkMap=getLeafItemLinkMap();
			 this.itemPointNum=getItemPointNum();
			 html=writeHtml(standGradeList,templateVo,pointGradeNumMap);
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 
		 return html;
	 }
	 
	 
	 /**
		 * 写html
		 * @return
		 */
		public String writeHtml(ArrayList standGradeList, RecordVo templateVo,HashMap pointGradeNumMap)
		{
		    	DecimalFormat myformat1 = new DecimalFormat("##########.#####");
			StringBuffer htmlContext=new StringBuffer("");
			HashMap existWriteItem=new HashMap();
			LazyDynaBean abean=null;
			LazyDynaBean a_bean=null;
			
			StringBuffer html=new StringBuffer("<table   class='ListTable' width='"+((lay+1)*this.td_width+(standGradeList.size()+2)*60)+"' >");
			 //输出表头
			html.append("<tr   style='position:relative;top:expression(this.offsetParent.scrollTop);'   height='25' >");
			html.append("<td class='TableRow'  valign='middle' align='center'  colspan='"+this.lay+"'>"+ResourceFactory.getProperty("train.job.itemName")+"</td>\r\n");
			html.append("<td class='TableRow'  valign='middle' align='center' >"+ResourceFactory.getProperty("kjg.title.indexname")+"</td>\r\n");
			html.append("<td class='TableRow'  valign='middle' align='center' >"+ResourceFactory.getProperty("jx.param.mark")+"</td>\r\n");
			if("1".equals(templateVo.getString("status"))) //权重
            {
                html.append("<td class='TableRow'  valign='middle' align='center' >"+ResourceFactory.getProperty("label.kh.template.qz")+"</td>\r\n");
            }
			for(int i=0;i<standGradeList.size();i++)
			{
				LazyDynaBean aa_bean=(LazyDynaBean)standGradeList.get(i);
				html.append("<td class='TableRow'  valign='middle' align='center' >"+aa_bean.get("gradedesc")+"</td>\r\n");
			}
			html.append("</tr>");
			
			
			
			int rowNum=0;
			for(int i=0;i<this.leafItemList.size();i++)
			{
				abean=(LazyDynaBean)this.leafItemList.get(i);
				String item_id=(String)abean.get("item_id");
				int num=((Integer)this.itemPointNum.get(item_id)).intValue();
				ArrayList pointList=(ArrayList)this.itemToPointMap.get(item_id);
				if(pointList!=null)
				{
					for(int j=0;j<pointList.size();j++)
					{
						htmlContext.append("<tr>\r\n");
						rowNum++;
						ArrayList linkParentList=(ArrayList)this.leafItemLinkMap.get(item_id);
						int current=linkParentList.size();
						for(int e=linkParentList.size()-1;e>=0;e--)
						{
							a_bean=(LazyDynaBean)linkParentList.get(e);
							String itemid=(String)a_bean.get("item_id");String kind = (String)a_bean.get("kind");
							if(existWriteItem.get(itemid)!=null) {
                                continue;
                            }
							existWriteItem.put(itemid,"1");
							String itemdesc=(String)a_bean.get("itemdesc");
							
//							int num1=((Integer)this.itemPointNum.get(item_id)).intValue();
							htmlContext.append(writeTd(itemdesc,((Integer)itemPointNum.get(itemid)).intValue(),"left",this.td_width,"RecordRow"));

							if(item_id.equals(itemid)) {
                                continue;
                            }
							
							String parent_id = (String)a_bean.get("parent_id");
							if(this.lay>1 && parent_id.length()==0 && this.itemToPointMap.get(itemid)!=null)//顶层项目有指标
							{
								ArrayList pointList2=(ArrayList)this.itemToPointMap.get(itemid);			
								for(int k=0;k<pointList2.size();k++)
								{
									htmlContext.append(writePointGrid(1,pointList2,k,itemid));
									if(pointList2==null)
									{
										StringBuffer ext_html=new StringBuffer("");
										String score=abean.get("score")==null?"0":(String)abean.get("score");
										score=myformat1.format(Double.parseDouble(score));//去掉小数点后面的0										
										ext_html.append(writeTd(score,0,"center",80,"RecordRow"));
										
										if("1".equals(templateVo.getString("status"))) //权重
										{
											String rank=(String)abean.get("rank")==null?"0":(String)abean.get("rank");
											rank=myformat1.format(Double.parseDouble(rank));//去掉小数点后面的0											
											ext_html.append(writeTd(rank,0,"center",80,"RecordRow"));
										}
//										if(templateVo.getString("status").equals("1")) //权重 
//										ext_html.append(writeTd((String)abean.get("rank"),0,"center",80,"RecordRow"));
										
										for(int e1=0;e1<standGradeList.size();e1++)
										{
											LazyDynaBean aa_bean=(LazyDynaBean)standGradeList.get(e1);
											String grade_template_id=((String)aa_bean.get("grade_template_id")).toLowerCase();
											String context="&nbsp;";
											if(pointGradeNumMap.get(itemid+"_"+grade_template_id)!=null) {
                                                context=(String)pointGradeNumMap.get(itemid+"_"+grade_template_id);
                                            }
											ext_html.append(writeTd2(context,0,"center",60,"RecordRow","T_"+itemid+"/"+grade_template_id));
										}
										htmlContext.append(ext_html.toString()); 
									}
									else {
                                        htmlContext.append(getExtendTd((LazyDynaBean)pointList2.get(k), standGradeList,templateVo,pointGradeNumMap));
                                    }
									htmlContext.append("</tr>\r\n");
									htmlContext.append("<tr>\r\n");
									rowNum++;
								}
							}
						}
						
						htmlContext.append(writePointGrid(current,pointList,j,item_id));
						if(pointList==null)
						{
							StringBuffer ext_html=new StringBuffer("");
							String score=abean.get("score")==null?"0":(String)abean.get("score");
							score=myformat1.format(Double.parseDouble(score));//去掉小数点后面的0							
							ext_html.append(writeTd(score,0,"center",80,"RecordRow"));
							
							if("1".equals(templateVo.getString("status"))) //权重
							{
								String rank=(String)abean.get("rank")==null?"0":(String)abean.get("rank");
								rank=myformat1.format(Double.parseDouble(rank));//去掉小数点后面的0											
								ext_html.append(writeTd(rank,0,"center",80,"RecordRow"));
							}
//							if(templateVo.getString("status").equals("1")) //权重 
//								ext_html.append(writeTd((String)abean.get("rank"),0,"center",80,"RecordRow"));
							
							for(int e=0;e<standGradeList.size();e++)
							{
								LazyDynaBean aa_bean=(LazyDynaBean)standGradeList.get(e);
								String grade_template_id=((String)aa_bean.get("grade_template_id")).toLowerCase();
								String context="&nbsp;";
								if(pointGradeNumMap.get(item_id+"_"+grade_template_id)!=null) {
                                    context=(String)pointGradeNumMap.get(item_id+"_"+grade_template_id);
                                }
								ext_html.append(writeTd2(context,0,"center",60,"RecordRow","T_"+item_id+"/"+grade_template_id));
							}
							htmlContext.append(ext_html.toString()); 
						}
						else {
                            htmlContext.append(getExtendTd((LazyDynaBean)pointList.get(j), standGradeList,templateVo,pointGradeNumMap));
                        }
						htmlContext.append("</tr>\r\n");
					}
				}else//项目后面没有指标的情况
				{
					htmlContext.append("<tr>\r\n");
					rowNum++;
					ArrayList linkParentList=(ArrayList)this.leafItemLinkMap.get(item_id);
					int current=linkParentList.size();
					for(int e=linkParentList.size()-1;e>=0;e--)
					{
						a_bean=(LazyDynaBean)linkParentList.get(e);
						String itemid=(String)a_bean.get("item_id");String kind = (String)a_bean.get("kind");
						if(existWriteItem.get(itemid)!=null) {
                            continue;
                        }
						existWriteItem.put(itemid,"1");
						String itemdesc=(String)a_bean.get("itemdesc");
						
//						int num1=((Integer)this.itemPointNum.get(item_id)).intValue();
						htmlContext.append(writeTd(itemdesc,((Integer)itemPointNum.get(itemid)).intValue(),"left",this.td_width,"RecordRow"));

						if(item_id.equals(itemid)) {
                            continue;
                        }
						
						String parent_id = (String)a_bean.get("parent_id");
						if(this.lay>1 && parent_id.length()==0 && this.itemToPointMap.get(itemid)!=null)//顶层项目有指标
						{
							ArrayList pointList2=(ArrayList)this.itemToPointMap.get(itemid);			
							for(int k=0;k<pointList2.size();k++)
							{
								htmlContext.append(writePointGrid(1,pointList2,k,itemid));
								if(pointList2==null)
								{
									StringBuffer ext_html=new StringBuffer("");
									String score=abean.get("score")==null?"0":(String)abean.get("score");
									score=myformat1.format(Double.parseDouble(score));//去掉小数点后面的0									
									ext_html.append(writeTd(score,0,"center",80,"RecordRow"));
									
									if("1".equals(templateVo.getString("status"))) //权重
									{
										String rank=(String)abean.get("rank")==null?"0":(String)abean.get("rank");
										rank=myformat1.format(Double.parseDouble(rank));//去掉小数点后面的0											
										ext_html.append(writeTd(rank,0,"center",80,"RecordRow"));
									}
//									if(templateVo.getString("status").equals("1")) //权重 
//										ext_html.append(writeTd((String)abean.get("rank"),0,"center",80,"RecordRow"));
									
									for(int e1=0;e1<standGradeList.size();e1++)
									{
										LazyDynaBean aa_bean=(LazyDynaBean)standGradeList.get(e1);
										String grade_template_id=((String)aa_bean.get("grade_template_id")).toLowerCase();
										String context="&nbsp;";
										if(pointGradeNumMap.get(itemid+"_"+grade_template_id)!=null) {
                                            context=(String)pointGradeNumMap.get(itemid+"_"+grade_template_id);
                                        }
										ext_html.append(writeTd2(context,0,"center",60,"RecordRow","T_"+itemid+"/"+grade_template_id));
									}
									htmlContext.append(ext_html.toString()); 
								}
								else {
                                    htmlContext.append(getExtendTd((LazyDynaBean)pointList2.get(k), standGradeList,templateVo,pointGradeNumMap));
                                }
								htmlContext.append("</tr>\r\n");
								htmlContext.append("<tr>\r\n");
								rowNum++;
							}
						}
					}
					
					htmlContext.append(writePointGrid(current,pointList,0,item_id));
					if(pointList==null)
					{
						StringBuffer ext_html=new StringBuffer("");
						String score=abean.get("score")==null?"0":(String)abean.get("score");
						score=myformat1.format(Double.parseDouble(score));//去掉小数点后面的0						
						ext_html.append(writeTd(score,0,"center",80,"RecordRow"));
						
						if("1".equals(templateVo.getString("status"))) //权重
						{
							String rank=(String)abean.get("rank")==null?"0":(String)abean.get("rank");
							rank=myformat1.format(Double.parseDouble(rank));//去掉小数点后面的0											
							ext_html.append(writeTd(rank,0,"center",80,"RecordRow"));
						}
//						if(templateVo.getString("status").equals("1")) //权重 
//							ext_html.append(writeTd((String)abean.get("rank"),0,"center",80,"RecordRow"));
						
						for(int e=0;e<standGradeList.size();e++)
						{
							LazyDynaBean aa_bean=(LazyDynaBean)standGradeList.get(e);
							String grade_template_id=((String)aa_bean.get("grade_template_id")).toLowerCase();
							String context="&nbsp;";
							if(pointGradeNumMap.get(item_id+"_"+grade_template_id)!=null) {
                                context=(String)pointGradeNumMap.get(item_id+"_"+grade_template_id);
                            }
							ext_html.append(writeTd2(context,0,"center",60,"RecordRow","T_"+item_id+"/"+grade_template_id));
						}
						htmlContext.append(ext_html.toString()); 
					}
					
					htmlContext.append("</tr>\r\n");
				}

			}
			
			html.append(htmlContext.toString());
			
			
			
			html.append("</table>");
			return html.toString();
		}
	 
	 
		
		
		
		
		
		public String getExtendTd(LazyDynaBean point_bean,ArrayList standGradeList, RecordVo templateVo,HashMap pointGradeNumMap)
		{
		    DecimalFormat myformat1 = new DecimalFormat("##########.#####");
			StringBuffer ext_html=new StringBuffer("");
			String point_id=(String)point_bean.get("point_id");
			String score = point_bean.get("score")==null?"0":(String)point_bean.get("score");
			score=myformat1.format(Double.parseDouble(score));//去掉小数点后面的0
			ext_html.append(writeTd(score,0,"center",80,"RecordRow"));
			
			if("1".equals(templateVo.getString("status"))) //权重
			{
				String rank=(String)point_bean.get("rank")==null?"0":(String)point_bean.get("rank");
				rank=myformat1.format(Double.parseDouble(rank));//去掉小数点后面的0											
				ext_html.append(writeTd(rank,0,"center",80,"RecordRow"));
			}
//			if(templateVo.getString("status").equals("1")) //权重 
//				ext_html.append(writeTd((String)point_bean.get("rank"),0,"center",80,"RecordRow"));
			
			for(int i=0;i<standGradeList.size();i++)
			{
				LazyDynaBean aa_bean=(LazyDynaBean)standGradeList.get(i);
				String grade_template_id=((String)aa_bean.get("grade_template_id")).toLowerCase();
				String context="&nbsp;";
				if(pointGradeNumMap.get(point_id+"_"+grade_template_id)!=null) {
                    context=(String)pointGradeNumMap.get(point_id+"_"+grade_template_id);
                }
				ext_html.append(writeTd2(context,0,"center",60,"RecordRow","C_"+point_id+"/"+grade_template_id));
			}
			return ext_html.toString();
		}
		
		
		
		
		private String writeTd2(String context,int rowspan,String align,int width,String classname,String id)
		{
			StringBuffer td=new StringBuffer("");
			td.append("\r\n<td class='"+classname+"' id='"+id+"' valign='middle' align='"+align+"' ");
			if(rowspan!=0) {
                td.append(" rowspan='"+rowspan+"' ");
            } else {
                td.append(" height='30' ");
            }
			td.append("  width='"+width+"' >");
			if(!"&nbsp;".equals(context))
			{
				td.append("<a href='javascript:reverseResult(\""+id+"\")' >");
			}
			td.append(context);
			if(!"&nbsp;".equals(context))
			{
				td.append("</a>");
			}
			td.append("</td>");
			return td.toString();
		}
		
		
		
		
		
		
		
		

		/**
		 * 画空格　和　指标格
		 * @param current
		 * @param pointList
		 * @param j
		 * @return
		 */
		public String writePointGrid(int current,ArrayList pointList,int j,String item_id)
		{
			StringBuffer tempHtml=new StringBuffer("");
			LazyDynaBean point_bean=null;
			for(int e=current;e<this.lay;e++) {
                tempHtml.append(writeTd("&nbsp;",1,"left",this.td_width,"RecordRow"));
            }
			if(pointList==null) {
                tempHtml.append(writeTd("&nbsp;",0,"left",this.td_width,"RecordRow"));
            } else
			{
				point_bean=(LazyDynaBean)pointList.get(j);
			//	tempHtml.append(writeTd((String)point_bean.get("p0407"),0,"left",this.td_width,"1","RecordRow_self_locked"));
			    
				StringBuffer td=new StringBuffer("");
				td.append("\r\n<td class='RecordRow'  valign='middle' align='left' ");
				td.append(" height='"+td_height+"' ");
				td.append("  width='"+this.td_width+"'");
				
				td.append(" >");
				td.append(((String)point_bean.get("pointname")).replaceAll("\r\n","<br>").replaceAll(" ","&nbsp;&nbsp;"));
				td.append("&nbsp;</td>");
				
				tempHtml.append(td.toString());
			}
			return tempHtml.toString();
		}
		
		
		
		
		private String writeTd(String context,int rowspan,String align,int width,String classname)
		{
			StringBuffer td=new StringBuffer("");
			td.append("\r\n<td class='"+classname+"'  valign='middle' align='"+align+"' ");
			if(rowspan!=0) {
                td.append(" rowspan='"+rowspan+"' ");
            } else {
                td.append(" height='30' ");
            }
			td.append("  width='"+width+"'");
			
			td.append(" >");
			td.append(context);
			td.append("</td>");
			return td.toString();
		}
		
		
	 
	 
	 /**
		 * 取得项目拥有的节点数
		 * @return
		 */
		public HashMap getItemPointNum()
		{
			HashMap map=new HashMap();
			LazyDynaBean a_bean=null;
			LazyDynaBean aa_bean=null;
			for(int i=0;i<templateItemList.size();i++)
			{
				a_bean=(LazyDynaBean)this.templateItemList.get(i);
				ArrayList list=new ArrayList();
				getLeafItemList(a_bean,list);
				int n=0;
				for(int j=0;j<list.size();j++)
				{
					aa_bean=(LazyDynaBean)list.get(j);
					String item_id=(String)aa_bean.get("item_id");
					if(itemToPointMap.get(item_id)!=null) {
                        n+=((ArrayList)itemToPointMap.get(item_id)).size();
                    } else {
                        n+=1;
                    }
				}
				
				String parent_id = (String)a_bean.get("parent_id");
				if(parent_id.length()==0 && this.lay>1)//考虑顶层项目直接跟指标的特例
				{
					if(itemToPointMap.get((String)a_bean.get("item_id"))!=null) {
                        n+=((ArrayList)itemToPointMap.get((String)a_bean.get("item_id"))).size();
                    }
				}else if(parent_id.length()==0 && this.lay==1)	
				{
					if(itemToPointMap.get((String)a_bean.get("item_id"))!=null) {
                        n=((ArrayList)itemToPointMap.get((String)a_bean.get("item_id"))).size();
                    }
				}
					
				map.put((String)a_bean.get("item_id"),new Integer(n));
			}
			return map;
		}
		
		
		public void getLeafItemList(LazyDynaBean abean,ArrayList list)
		{
			String item_id=(String)abean.get("item_id");
			String child_id=(String)abean.get("child_id");
			String parent_id=(String)abean.get("parent_id");
			
			if(child_id.length()==0 && parent_id.length()>0)
			{
				list.add(abean);
					return;
			}
			LazyDynaBean a_bean=null;
			for(int j=0;j<this.templateItemList.size();j++)
			{
					a_bean=(LazyDynaBean)this.templateItemList.get(j);
					parent_id=(String)a_bean.get("parent_id");
					if(parent_id.equals(item_id)) {
                        getLeafItemList(a_bean,list);
                    }
			}
			
		}
		
	 
	 /**
		 * 叶子项目对应的继承关系
		 * @return
		 */
		public  HashMap getLeafItemLinkMap()
		{
			HashMap map=new HashMap();
			try
			{
				LazyDynaBean abean=null;
				for(int i=0;i<this.leafItemList.size();i++)
				{
					abean=(LazyDynaBean)this.leafItemList.get(i);
					String item_id=(String)abean.get("item_id");
					String parent_id=(String)abean.get("parent_id");
					ArrayList linkList=new ArrayList();
					getParentItem(linkList,abean);
					if(linkList.size()>lay) {
                        lay=linkList.size();
                    }
					map.put(item_id,linkList);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return map;
		}
		
		//寻找继承关系
		public void getParentItem(ArrayList list,LazyDynaBean abean)
		{
			String item_id=(String)abean.get("item_id");
			String parent_id=(String)abean.get("parent_id");
			if(parent_id.length()==0)
			{
				list.add(abean);
				return;
			}
			LazyDynaBean a_bean=null;
			for(int i=0;i<templateItemList.size();i++)
			{
				a_bean=(LazyDynaBean)this.templateItemList.get(i);
				String itemid=(String)a_bean.get("item_id");
				String parentid=(String)a_bean.get("parent_id");
				if(itemid.equals(parent_id))
				{
					list.add(abean);
					getParentItem(list,a_bean);
				}
				
			}
			
			
		}
		
	 
	 
	 
	 
	 
	 
	 
	 
	 /**
		 * 取得项目对应任务map
		 * @return
		 */
		public HashMap getItemToPointMap()
		{
			HashMap map=new HashMap();
			try
			{
				ContentDAO dao = new ContentDAO(this.conn);
				LazyDynaBean abean=null;
				for(int i=0;i<this.planPointList.size();i++)
				{
					abean=(LazyDynaBean)this.planPointList.get(i);
					String point_id=(String)abean.get("point_id");
					String item_id=(String)abean.get("item_id");
					if(map.get(item_id)!=null)
					{
						ArrayList tempList=(ArrayList)map.get(item_id);
						tempList.add(abean);
						map.put(item_id,tempList);
					}
					else
					{
						ArrayList tempList=new ArrayList();
						tempList.add(abean);
						map.put(item_id,tempList);
					}
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return map;
		}
	 
	 
	 
	 /**
		 * 叶子项目列表
		 *
		 */
		public void get_LeafItemList()
		{
			LazyDynaBean abean=null;
			for(int i=0;i<this.templateItemList.size();i++)
			{
				abean=(LazyDynaBean)this.templateItemList.get(i);
				String parent_id=(String)abean.get("parent_id");
				if(parent_id.length()==0)
				{
				//	ArrayList tempList=new ArrayList();
				//	tempList.add(abean);
					setLeafItemFunc(abean);
				}
			}
		}
		
	   //	递归查找叶子项目
		public void setLeafItemFunc(LazyDynaBean abean)
		{
			String item_id=(String)abean.get("item_id");
			String child_id=(String)abean.get("child_id");
			if(child_id.length()==0)
			{
				this.leafItemList.add(abean);
					return;
			}
			LazyDynaBean a_bean=null;
			for(int j=0;j<this.templateItemList.size();j++)
			{
					a_bean=(LazyDynaBean)this.templateItemList.get(j);
					String parent_id=(String)a_bean.get("parent_id");
					if(parent_id.equals(item_id)) {
                        setLeafItemFunc(a_bean);
                    }
			}
		}
	 
	 
	 
	 public RecordVo getPlanVo(String planid)
		{
			RecordVo vo=new RecordVo("per_plan");
			try
			{
				vo.setInt("plan_id",Integer.parseInt(planid));
				ContentDAO dao = new ContentDAO(this.conn);
				vo=dao.findByPrimaryKey(vo);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return vo;
		}
	 
	 /**
		 * 取得 模板项目记录
		 * @return
		 */
		public ArrayList getTemplateItemList(String template_id)
		{
			ArrayList list=new ArrayList();
			try
			{
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rowSet=dao.search("select * from  per_template_item where template_id='"+template_id+"'  order by seq");
			    LazyDynaBean abean=null;
				while(rowSet.next())
			    {
					abean=new LazyDynaBean();
			    	abean.set("item_id",rowSet.getString("item_id"));
			    	abean.set("parent_id",rowSet.getString("parent_id")!=null?rowSet.getString("parent_id"):"");
			    	abean.set("child_id",rowSet.getString("child_id")!=null?rowSet.getString("child_id"):"");
			    	abean.set("template_id",rowSet.getString("template_id"));
			    	abean.set("itemdesc",rowSet.getString("itemdesc"));
			    	abean.set("seq",rowSet.getString("seq"));
			    	abean.set("kind",rowSet.getString("kind")!=null?rowSet.getString("kind"):"");
			    	
			    	abean.set("score",rowSet.getString("score")!=null?moveZero(rowSet.getString("score")):"");
			    	
			    	abean.set("rank",rowSet.getString("rank")!=null?moveZero(rowSet.getString("rank")):"");
			    	
			    	abean.set("rank_type",rowSet.getString("rank_type")!=null?rowSet.getString("rank_type"):"");
			    	list.add(abean);
			    }
			
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return list;
		}
	 
		/**
		 * 移除小数点后面的零
		 * @param number
		 */
		public String moveZero(String number)
		{
			DecimalFormat df = new DecimalFormat("###############.#####"); 
			if(number==null||number.length()==0) {
                return "";
            }
			return df.format(Double.parseDouble(number));
		}
	 
	 /****************************   end *****************************************/
	 
	 
	 
	 
	 
	 
	 /**
	  * 取得 所有已归档计划中涉及到的指标列表
	  * @return
	  */
	 public ArrayList getPointList()
	 {
		 ArrayList list=new ArrayList();
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 StringBuffer templateids=new StringBuffer("");
			 RowSet rowSet=dao.search("select distinct template_id from per_plan where status=7");
			 while(rowSet.next())
			 {
				 templateids.append(",'"+rowSet.getString(1)+"'");
			 }
			 if(templateids.length()>0)
			 {
				 String sql="select po.point_id,po.pointname from per_template_item pi,per_template_point pp,per_point po "
					      +" where pi.item_id=pp.item_id and pp.point_id=po.point_id  "
					      +" and template_id in ("+(templateids.substring(1))+")  order by pp.seq";
				
				 rowSet=dao.search(sql);
				 while(rowSet.next())
				 {
					 list.add(new CommonData(rowSet.getString("point_id"),"["+rowSet.getString("point_id")+"]"+rowSet.getString("pointname")));
				 }
				 
				 sql="select distinct item_id,itemdesc from per_template_item  where   template_id in ("+(templateids.substring(1))+") and kind=2";
				 rowSet=dao.search(sql);
				 while(rowSet.next())
				 {
					 list.add(new CommonData("i_"+rowSet.getString("item_id"),"["+rowSet.getString("item_id")+"]"+rowSet.getString("itemdesc")));
				 }
				 
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return list;
	 }
	 /**
	  * 取得 所有已归档计划中涉及到的指标列表
	  * 指标按顺序排序
	  * @return
	  */
	 public ArrayList getPointList2()
	 {
		 ArrayList list=new ArrayList();
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 StringBuffer templateids=new StringBuffer("");
			 ArrayList templateList = new ArrayList();
			 RowSet rowSet=dao.search("select distinct template_id from per_plan where status=7");
			 while(rowSet.next())
			 {
				 templateids.append(",'"+rowSet.getString(1)+"'");
				 templateList.add(rowSet.getString(1));
			 }
			 if(templateids.length()>0)
			 {	
			     HashMap allPoints = new HashMap();
				BatchGradeBo bo = new BatchGradeBo(this.conn);			
				String sql="";				
				 for(int k=0;k<templateList.size();k++)
				 {
					// 解决排列顺序问题
					ArrayList seqList = new ArrayList();
					ArrayList tempPointList = new ArrayList();
				     HashMap map  = new HashMap();
				     String templateId = (String)templateList.get(k);
				     sql="select po.point_id,po.pointname,pp.item_id from per_template_item pi,per_template_point pp,per_point po "
					      +" where pi.item_id=pp.item_id and pp.point_id=po.point_id  "
					      +" and template_id ='"+templateId+"'  order by pp.seq";
				
				     rowSet=dao.search(sql);
				     while(rowSet.next())
				     {
					 String[] temp = new String[11];
						temp[0] = rowSet.getString(1);
						temp[3] = rowSet.getString(3);
						tempPointList.add(temp);
						map.put(rowSet.getString("point_id"), rowSet.getString("pointname"));
						
//					 list.add(new CommonData(rowSet.getString("point_id"),"["+rowSet.getString("point_id")+"]"+rowSet.getString("pointname")));
				     }
				     bo.get_LeafItemList(templateId, tempPointList, seqList);
				     for (int i = 0; i < seqList.size(); i++)
				     {
					String pointId = (String) seqList.get(i);
					pointId=pointId.toUpperCase();
					if(allPoints.get(pointId)!=null) {
                        continue;
                    }
					list.add(new CommonData(pointId,"["+pointId+"]"+(String)map.get(pointId)));
				     }
				     allPoints.putAll(map);
				 }
				 
				 sql="select distinct item_id,itemdesc from per_template_item  where   template_id in ("+(templateids.substring(1))+") and kind=2";
				 rowSet=dao.search(sql);
				 while(rowSet.next())
				 {
					 list.add(new CommonData("i_"+rowSet.getString("item_id"),"["+rowSet.getString("item_id")+"]"+rowSet.getString("itemdesc")));
				 }
				 
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return list;
	 }
	 /**
	  * 取得考核对象参与的计划id（已归档的）
	  * @param objID
	  * @return
	  */
	 public String getPlanIds(String objID)
	 {
		 String planIDs="";
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 RowSet rowSet=dao.search(" select plan_id from per_plan where plan_id in (select plan_id from per_object where object_id='"+objID+"') and status=7");
			 while(rowSet.next()) {
                 planIDs+=","+rowSet.getString(1);
             }
			 if(planIDs.length()>0) {
                 planIDs=planIDs.substring(1);
             }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return planIDs;
	 }
	 /**
	  * 
	  * @return
	  */
	 public String getPlanIDs(UserView userView,String busitype)
	 {
		 StringBuffer plan_ids=new StringBuffer("");
		 ArrayList planList=getPlanList(1,"-1","-1",userView,busitype);
		 for(int i=0;i<planList.size();i++)
		 {
			 LazyDynaBean abean=(LazyDynaBean)planList.get(i);
			 plan_ids.append(","+(String)abean.get("plan_id"));
			 
		 }
		 if(plan_ids.length()>0) {
             return plan_ids.substring(1);
         }
		 return plan_ids.toString();
	 }
	 
	 
	 /**
	  * 
	  * @return
	  */
	 public String getPlanIDs(UserView userView,String busitype,String objSelected)
	 {
		 StringBuffer plan_ids=new StringBuffer("");
		 ArrayList planList=getPlanListByModel(1,"-1","-1",userView,busitype,objSelected);
		 for(int i=0;i<planList.size();i++)
		 {
			 LazyDynaBean abean=(LazyDynaBean)planList.get(i);
			 plan_ids.append(","+(String)abean.get("plan_id"));
			 
		 }
		 if(plan_ids.length()>0) {
             return plan_ids.substring(1);
         }
		 return plan_ids.toString();
	 }
	 
	 
	 /**
	  * 取得考核计划信息列表
	  * @param flag  1：已归档  2：已归档或已开始评估
	  * @param period  考核周期
	  * @param templateid 模板
	  * @return
	  */
	 public ArrayList getPlanList(int flag,String period,String templateid,UserView _userView,String busitype)
	 {
		 ArrayList list=new ArrayList();
		 try
		 {
			 StringBuffer sql=new StringBuffer("select per_plan.*,per_template.name templateName from per_plan,per_template where per_plan.template_id=per_template.template_id and ( per_plan.status=7");
			 if(flag==2)
			 {
				 sql.append(" or status=6");
				 
			 } 
			 sql.append(" ) ");
			 
			 if(busitype==null || busitype.trim().length()<=0) {
                 busitype = "0";
             }
			 if(busitype!=null && busitype.trim().length()>0 && "0".equalsIgnoreCase(busitype)) {
                 sql.append(" and ( busitype is null or busitype='' or busitype = '" + busitype + "') ");
             } else if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype)) {
                 sql.append(" and busitype = '" + busitype + "' ");
             }
			 
			 StringBuffer whlSql = new StringBuffer(" and (status=7 ");	
			 if(flag==2) {
                 whlSql.append(" or status=6");
             }
			 whlSql.append(" ) ");			 
			 
			 if(!"-1".equals(period))
			 {
				 sql.append(" and per_plan.cycle="+period);
				 whlSql.append(" and cycle="+period);
			 }
				 
			 if(!"-1".equals(templateid))
			 {
				 sql.append(" and per_plan.template_id='"+templateid+"'");
				 whlSql.append(" and template_id='"+templateid+"'");
			 }	
			 
			 ExamPlanBo bo = new ExamPlanBo(this.conn);
			 sql.append(bo.getPlanWhlByObjTypePriv(this.userView,busitype));			 
			 
			 sql.append(" order by "+Sql_switcher.isnull("per_plan.a0000", "999999999")+",per_plan.plan_id desc");
			 
		
			HashMap map = bo.getPlansByUserView(_userView, whlSql.toString());		 
			 
			 ContentDAO dao = new ContentDAO(this.conn);
			 RowSet rowSet=dao.search(sql.toString());
			 LazyDynaBean abean=null;
			 SimpleDateFormat fm=new SimpleDateFormat("yyyy-MM-dd");
			 while(rowSet.next())
			 {
				 abean=new LazyDynaBean();
				 String plan_id = rowSet.getString("plan_id");
				 if(map.get(plan_id)==null) {
                     continue;
                 }
				 abean.set("plan_id",rowSet.getString("plan_id"));
				 abean.set("name",rowSet.getString("name"));
				 abean.set("template_id",rowSet.getString("template_id"));
				 abean.set("templateName",rowSet.getString("templateName"));
				 int cycle=rowSet.getInt("cycle");  //考核周期:(0,1,2,3,7)=(年度,半年,季度,月度,不定期)
				 String cycle_name="";
				 switch(cycle){
				 	case 0:cycle_name=ResourceFactory.getProperty("jx.khplan.yeardu");break;
				 	case 1:cycle_name=ResourceFactory.getProperty("jx.khplan.halfyear");break;
				 	case 2:cycle_name=ResourceFactory.getProperty("jx.khplan.quarter");break;
				 	case 3:cycle_name=ResourceFactory.getProperty("jx.khplan.monthdu");break;
				 	case 7:cycle_name=ResourceFactory.getProperty("jx.khplan.indefinetime");break;
				 }
				 abean.set("cycle",cycle_name);
				 String theyear=rowSet.getString("theyear");
				 String themonth=rowSet.getString("themonth");
				 String startDate=rowSet.getDate("start_Date")!=null?fm.format(rowSet.getDate("start_Date")):"";
				 String endDate=rowSet.getDate("end_Date")!=null?fm.format(rowSet.getDate("end_Date")):"";
				 String thequarter = rowSet.getString("thequarter");
				 abean.set("timeScope",getTimeScope(thequarter,theyear,themonth,cycle,startDate,endDate));
				 list.add(abean);
			 }
			 if(rowSet!=null) {
                 rowSet.close();
             }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return list;
	 }
	 /**
	  * 按岗位素质模型 取得考核计划信息列表
	  * @param flag  1：已归档  2：已归档或已开始评估
	  * @param period  考核周期
	  * @param templateid 模板
	  * @return
	  */
	 public ArrayList getPlanListByModel(int flag,String period,String templateid,UserView _userView,String busitype,String objSelected)
	 {
		 ArrayList list=new ArrayList();
		 try
		 {
			 StringBuffer sql=new StringBuffer("select per_plan.* ,per_template.name templateName from per_plan,per_object,per_template where per_plan.template_id=per_template.template_id and per_plan.plan_id=per_object.plan_id and per_object.object_id='"+objSelected+"' and ( per_plan.status=7 " );
			 if(flag==2)
			 {
				 sql.append(" or status=6");
				 
			 } 
			 sql.append(" ) ");
			 
			 if(busitype==null || busitype.trim().length()<=0) {
                 busitype = "0";
             }
			 if(busitype!=null && busitype.trim().length()>0 && "0".equalsIgnoreCase(busitype)) {
                 sql.append(" and ( busitype is null or busitype='' or busitype = '" + busitype + "') ");
             } else if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype)) {
                 sql.append(" and busitype = '" + busitype + "' ");
             }
			 
			 StringBuffer whlSql = new StringBuffer(" and (status=7 ");	
			 if(flag==2) {
                 whlSql.append(" or status=6");
             }
			 whlSql.append(" ) ");			 
			 
			 if(!"-1".equals(period))
			 {
				 sql.append(" and per_plan.cycle="+period);
				 whlSql.append(" and cycle="+period);
			 }
				 
			 if(!"-1".equals(templateid))
			 {
				 sql.append(" and per_plan.template_id='"+templateid+"'");
				 whlSql.append(" and template_id='"+templateid+"'");
			 }	
			 
			 ExamPlanBo bo = new ExamPlanBo(this.conn);
			 sql.append(bo.getPlanWhlByObjTypePriv(this.userView,busitype));			 
			 
			 sql.append(" order by "+Sql_switcher.isnull("per_plan.a0000", "999999999")+",per_plan.plan_id desc");
			 
		
			HashMap map = bo.getPlansByUserView(_userView, whlSql.toString());		 
			 
			 ContentDAO dao = new ContentDAO(this.conn);
			 RowSet rowSet=dao.search(sql.toString());
			 LazyDynaBean abean=null;
			 SimpleDateFormat fm=new SimpleDateFormat("yyyy-MM-dd");
			 while(rowSet.next())
			 {
				 abean=new LazyDynaBean();
				 String plan_id = rowSet.getString("plan_id");
				 if(map.get(plan_id)==null) {
                     continue;
                 }
				 abean.set("plan_id",rowSet.getString("plan_id"));
				 abean.set("name",rowSet.getString("name"));
				 abean.set("template_id",rowSet.getString("template_id"));
				 abean.set("templateName",rowSet.getString("templateName"));
				 int cycle=rowSet.getInt("cycle");  //考核周期:(0,1,2,3,7)=(年度,半年,季度,月度,不定期)
				 String cycle_name="";
				 switch(cycle){
				 	case 0:cycle_name=ResourceFactory.getProperty("jx.khplan.yeardu");break;
				 	case 1:cycle_name=ResourceFactory.getProperty("jx.khplan.halfyear");break;
				 	case 2:cycle_name=ResourceFactory.getProperty("jx.khplan.quarter");break;
				 	case 3:cycle_name=ResourceFactory.getProperty("jx.khplan.monthdu");break;
				 	case 7:cycle_name=ResourceFactory.getProperty("jx.khplan.indefinetime");break;
				 }
				 abean.set("cycle",cycle_name);
				 String theyear=rowSet.getString("theyear");
				 String themonth=rowSet.getString("themonth");
				 String startDate=rowSet.getDate("start_Date")!=null?fm.format(rowSet.getDate("start_Date")):"";
				 String endDate=rowSet.getDate("end_Date")!=null?fm.format(rowSet.getDate("end_Date")):"";
				 String thequarter = rowSet.getString("thequarter");
				 abean.set("timeScope",getTimeScope(thequarter,theyear,themonth,cycle,startDate,endDate));
				 list.add(abean);
			 }
			 if(rowSet!=null) {
                 rowSet.close();
             }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return list;
	 }
	  
	 
	 public String getPrivCode(UserView userView)
	 {

		String code = "";
		if (userView.isSuper_admin()) {
            code = "-1";
        } else if (userView.getManagePrivCodeValue() != null && userView.getManagePrivCodeValue().length() > 0)
		{    
			code = userView.getManagePrivCodeValue();
			if("UN".equalsIgnoreCase(code)) {
                code="-1";
            }
		}
		else
		{
		    String userDeptId = userView.getUserDeptId();
		    String userOrgId = userView.getUserOrgId();
		    if (userDeptId != null && !"null".equalsIgnoreCase(userDeptId) && userDeptId.trim().length() > 0)
		    {
			code = userDeptId;
		    } else if (userOrgId != null && userOrgId.trim().length() > 0)
		    {
			code = userOrgId;
		    }
		}
		return code;
	 }
	 
	 
	 
	 
	 
	 
	 /**
	  * 取得时间范围描述
	  * @param theYear
	  * @param theMonth
	  * @param cycle 考核周期:(0,1,2,3,7)=(年度,半年,季度,月度,不定期)
	  * @return
	  */
	 private String getTimeScope(String thequarter,String theYear,String theMonth,int cycle,String startDate,String endDate)
	 {
		 
		 String desc="";
		 switch(cycle){
		 	case 0:desc=theYear+ResourceFactory.getProperty("columns.archive.year");break;
		 	case 1: desc=theYear+ResourceFactory.getProperty("columns.archive.year");
		 	        if("1".equals(thequarter)) {
                        desc+=ResourceFactory.getProperty("report.pigeonhole.uphalfyear");
                    }
		 	        if("2".equals(thequarter)) {
                        desc+=ResourceFactory.getProperty("report.pigeonhole.downhalfyear");
                    }
		 	        break;
		 	case 2:desc=theYear+ResourceFactory.getProperty("columns.archive.year");
		 	        if("01".equals(thequarter)) {
                        desc+="一";
                    } else if("02".equals(thequarter)) {
                        desc+="二";
                    } else if("03".equals(thequarter)) {
                        desc+="三";
                    } else if("04".equals(thequarter)) {
                        desc+="四";
                    }
		 	        desc+=ResourceFactory.getProperty("jx.khplan.quarter");
		 	        break;
		 	case 3:desc=theYear+ResourceFactory.getProperty("columns.archive.year");
		 		   desc+=theMonth+ResourceFactory.getProperty("columns.archive.month");
		 			break;
		 	case 7:desc=startDate+ResourceFactory.getProperty("kq.shift.cycle.dateto")+endDate;break;
		 }
		 return desc;
	 }
	/**
	 * 根据人员编码获得所在岗位是否有岗位素质指标
	 */
	 public String getE01A1ByA0100(String a0100){
		 String e01a1="";
		 String sql="select distinct object_id from per_competency_modal where object_id in(select e01a1 from usra01 where a0100='"+a0100+"') and object_type='3'";
		 ContentDAO dao = new ContentDAO(this.conn);
		 RowSet rowSet=null;
		 try {
			 rowSet = dao.search(sql.toString());
			 if(rowSet.next()){
				 e01a1=rowSet.getString("object_id")==null?"":rowSet.getString("object_id");
			 }
		 } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}finally{
				if(rowSet!=null){
					try {
						rowSet.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			}	
			}
			return e01a1;
		}

	 /**
	  * 判断计划是否按岗位素质模型 ByModel=1按
	  */
	 public String getByModelByPlanId(String planId){
		 String byModel="";
		 String sql="select ByModel from per_plan where plan_id='"+planId+"'";
		 ContentDAO dao = new ContentDAO(this.conn);
		 RowSet rowSet=null;
		 try {
			 rowSet = dao.search(sql.toString());
			 while(rowSet.next()){
				 byModel=rowSet.getString("ByModel")==null?"":rowSet.getString("ByModel");
			 }
		 } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(rowSet!=null){
				try {
					rowSet.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}	
		}

		return byModel; 
	 }
}
