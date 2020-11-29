package com.hjsj.hrms.transaction.performance.achivement.dataCollection;

import com.hjsj.hrms.businessobject.performance.achivement.dataCollection.DataCollectBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**  
 *<p>Title:ImportExcelTrans.java</p>
 *<p>Description:数据采集导入模板</p>
 *<p>Company:hjsj</p>
 *<p>create time:2009-03-02 13:32:31</p>
 *@author JinChunhai
 *@version 5.0
 */

public class ImportExcelTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
    	try{

		String plan_id = (String) this.getFormHM().get("planId");
		String determine = (String) this.getFormHM().get("determine");
		String fileName = (String) this.getFormHM().get("filename");//加密后的文件名
        fileName = PubFunc.decrypt(fileName);
        String path = (String) this.getFormHM().get("path");//路径
        path = PubFunc.decrypt(path);
        String filePath = path + fileName;
        
        File file = new File(filePath);
    	boolean flag = FileTypeUtil.isFileTypeEqual(file);
    	if(!flag){
    		throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
    	}
		DataCollectBo bo = new DataCollectBo(this.getFrameconn(), plan_id,this.userView);
		
	//  两种导入模板样式   JinChunhai  2011.03.30
		HashMap pointsMap = new HashMap();
//		HashMap obj_pointsMap = new HashMap();
		if("true".equalsIgnoreCase(determine))
		{
//			throw GeneralExceptionHandler.Handle(new Exception("此功能正在完善中......(暂不可用)"));
			pointsMap = bo.getExcelDatas_special(file);
		}
		else
			pointsMap = bo.getExcelDatas(file);	
			
		if("true".equalsIgnoreCase(determine))
		{
			Set obj_points = pointsMap.keySet();
			Iterator t = obj_points.iterator();
			while (t.hasNext())
			{
				String obj_id = (String) t.next();
				HashMap obj_pointsMap = (HashMap)pointsMap.get(obj_id);
			    if(obj_pointsMap.size()==0)
			    	continue;			    
			
			    Set points = obj_pointsMap.keySet();
				Iterator it = points.iterator();
				while (it.hasNext())
				{
				    String pointid = (String) it.next();
				    bo = new DataCollectBo(this.getFrameconn(), plan_id, pointid,this.userView);
				    HashMap map = (HashMap)obj_pointsMap.get(pointid);
				    if(map.size()==0)
				    	continue;
				    String pointype = bo.getTypeOfPoint();
				    String rule = bo.getRule();
			
				    if (!"0".equals(pointype))// 加减分指标的保存
				    {
				//		HashMap map = bo.getExcelData(form_file);
						String[] scores = (String[]) map.get("scores");
						String[] dfScores = (String[]) map.get("dfScores");
						String objs = (String) map.get("objs");
						ArrayList theObjs = getTheObjs(objs,bo);
						if(theObjs.size()>0)
							bo.save(scores, dfScores, theObjs);
				    } else if ("0".equals(pointype) && !"0".equals(rule)) // 基本指标非录分规则导入Excel
				    {
						if ("3".equals(rule))// 排名的保存(对所有考核对象所以如果在Excel中没有的对象数据也要取出来)
						{
				//		    HashMap map = bo.getExcelData2(form_file);
						    String[] pricticalVal = (String[]) map.get("pricticalVal");
						    String[] standardVal = (String[]) map.get("standVal");
						    String[] basicVal = (String[]) map.get("basicVal");
						    String objs = (String) map.get("objs");
						    
						    StringBuffer standardVals = new StringBuffer();
						    StringBuffer praticalVals = new StringBuffer();
						    StringBuffer basicVals = new StringBuffer();
						    
						    for(int i=0;i<pricticalVal.length;i++)
						    {
								praticalVals.append(pricticalVal[i]+"<@>");
								standardVals.append(standardVal[i]+"<@>");
								basicVals.append(basicVal[i]+"<@>");
						    }
						    
						    ArrayList allObjs = bo.getKhObjs();
						    HashMap basicFenMap = bo.getBasicFen();
					        for (int i = 0; i < allObjs.size(); i++)
						    {
								LazyDynaBean bean = (LazyDynaBean) allObjs.get(i);
								String object_id = (String) bean.get("object_id");
								if (objs.indexOf(object_id) == -1)
								{
								    String standVal1 = bo.getStandardFen(object_id);
								    String basicVal1 = (String)basicFenMap.get(object_id);
								    String praticalVal1 = bo.getPraticalVal(object_id);
								    standardVals.append(object_id + "_standard=" + standVal1 + "<@>");
								    basicVals.append(object_id + "_basic=" + basicVal1 + "<@>");
								    praticalVals.append(object_id + "_pratical=" + praticalVal1 + "<@>");
								}
						    }
					        if(allObjs.size()>0)
					        	bo.save2(standardVals.toString().split("<@>"), praticalVals.toString().split("<@>"), basicVals.toString().split("<@>"), allObjs);
						} else if ("1".equals(rule) || "2".equals(rule))// 简单｜分段导入Excel
						{
				//		    HashMap map = bo.getExcelData1(form_file);
						    String[] pricticalVal = (String[]) map.get("pricticalVal");
						    String[] standardVal = (String[]) map.get("standVal");
						    String[] addF = (String[]) map.get("addF");
						    String[] basicVal = (String[]) map.get("basicVal");
						    String[] objDF = (String[]) map.get("objDF");    
						    String[] deducF = (String[]) map.get("deducF");
						    String objs = (String) map.get("objs");
						    ArrayList theObjs = getTheObjs(objs,bo);
							if(theObjs.size()>0)
								bo.save1(standardVal, pricticalVal, basicVal, addF, deducF, objDF,theObjs);
						}
				    } else if ("0".equals(pointype) && "0".equals(rule)) // 基本指标录分规则导入Excel
				    {
			//		HashMap map = bo.getExcelData3(form_file);
					String[] fzScores = (String[]) map.get("fzScores");
					String objs = (String)map.get("objs");
					ArrayList theObjs = getTheObjs(objs,bo);
					if(theObjs.size()>0)
						bo.save3(fzScores, theObjs);
				    }
				}
			}
		}else
		{
			Set points = pointsMap.keySet();			
			Iterator it = points.iterator();
			while (it.hasNext())
			{
			    String pointid = (String) it.next();
			    bo = new DataCollectBo(this.getFrameconn(), plan_id, pointid,this.userView);
			    HashMap map = (HashMap)pointsMap.get(pointid);
			    if(map.size()==0)
			    	continue;
			    String pointype = bo.getTypeOfPoint();
			    String rule = bo.getRule();
		
			    if (!"0".equals(pointype))// 加减分指标的保存
			    {
			//		HashMap map = bo.getExcelData(form_file);
					String[] scores = (String[]) map.get("scores");
					String[] dfScores = (String[]) map.get("dfScores");
					String objs = (String) map.get("objs");
					ArrayList theObjs = getTheObjs(objs,bo);
					if(theObjs.size()>0)
						bo.save(scores, dfScores, theObjs);
			    } else if ("0".equals(pointype) && !"0".equals(rule)) // 基本指标非录分规则导入Excel
			    {
					if ("3".equals(rule))// 排名的保存(对所有考核对象所以如果在Excel中没有的对象数据也要取出来)
					{
			//		    HashMap map = bo.getExcelData2(form_file);
					    String[] pricticalVal = (String[]) map.get("pricticalVal");
					    String[] standardVal = (String[]) map.get("standVal");
					    String[] basicVal = (String[]) map.get("basicVal");
					    String objs = (String) map.get("objs");
					    
					    StringBuffer standardVals = new StringBuffer();
					    StringBuffer praticalVals = new StringBuffer();
					    StringBuffer basicVals = new StringBuffer();
					    
					    for(int i=0;i<pricticalVal.length;i++)
					    {
							praticalVals.append(pricticalVal[i]+"<@>");
							standardVals.append(standardVal[i]+"<@>");
							basicVals.append(basicVal[i]+"<@>");
					    }
					    
					    ArrayList allObjs = bo.getKhObjs();
					    HashMap basicFenMap = bo.getBasicFen();
				        for (int i = 0; i < allObjs.size(); i++)
					    {
							LazyDynaBean bean = (LazyDynaBean) allObjs.get(i);
							String object_id = (String) bean.get("object_id");
							if (objs.indexOf(object_id) == -1)
							{
							    String standVal1 = bo.getStandardFen(object_id);
							    String basicVal1 = (String)basicFenMap.get(object_id);
							    String praticalVal1 = bo.getPraticalVal(object_id);
							    standardVals.append(object_id + "_standard=" + standVal1 + "<@>");
							    basicVals.append(object_id + "_basic=" + basicVal1 + "<@>");
							    praticalVals.append(object_id + "_pratical=" + praticalVal1 + "<@>");
							}
					    }
				        if(allObjs.size()>0)
				        	bo.save2(standardVals.toString().split("<@>"), praticalVals.toString().split("<@>"), basicVals.toString().split("<@>"), allObjs);
					} else if ("1".equals(rule) || "2".equals(rule))// 简单｜分段导入Excel
					{
			//		    HashMap map = bo.getExcelData1(form_file);
					    String[] pricticalVal = (String[]) map.get("pricticalVal");
					    String[] standardVal = (String[]) map.get("standVal");
					    String[] addF = (String[]) map.get("addF");
					    String[] basicVal = (String[]) map.get("basicVal");
					    String[] objDF = (String[]) map.get("objDF");    
					    String[] deducF = (String[]) map.get("deducF");
					    String objs = (String) map.get("objs");
					    ArrayList theObjs = getTheObjs(objs,bo);
						if(theObjs.size()>0)
							bo.save1(standardVal, pricticalVal, basicVal, addF, deducF, objDF,theObjs);
					}
			    } else if ("0".equals(pointype) && "0".equals(rule)) // 基本指标录分规则导入Excel
			    {
		//		HashMap map = bo.getExcelData3(form_file);
				String[] fzScores = (String[]) map.get("fzScores");
				String objs = (String)map.get("objs");
				ArrayList theObjs = getTheObjs(objs,bo);
				if(theObjs.size()>0)
					bo.save3(fzScores, theObjs);
			    }
			}
		}		
		this.getFormHM().put("error", "0");
    	}catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    }
    /**获得导入excel页面的有效考核对象*/
    public ArrayList getTheObjs(String objs,DataCollectBo bo)
    {	
		ArrayList allObjs = bo.getKhObjs();
		ArrayList theObjs = new ArrayList();
		for(int i=0;i<allObjs.size();i++)
		{
		    LazyDynaBean bean = (LazyDynaBean)allObjs.get(i);
		    String objectId = (String)bean.get("object_id");
		    if(objs.indexOf(objectId)!=-1)
		    {
		    	theObjs.add(bean);
		    }		
		}
		return theObjs;
    }
    
}
