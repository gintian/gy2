package com.hjsj.hrms.module.questionnaire.analysis.transaction;

import com.hjsj.hrms.businessobject.sys.EchartsBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * <p>Title: InitChartAnalysisTrans </p>
 * <p>Description: 查询图表分析数据</p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2018-6-29 上午10:28:04</p>
 * @author xiegh
 * @version 1.0
 */
public class InitChartAnalysisTrans extends IBusiness{

	private static final long serialVersionUID = 1L;

	@Override
    public void execute() throws GeneralException {
           try {
        	   String charttype = (String)this.getFormHM().get("type"); 
			   String name = (String)this.getFormHM().get("name"); 
			   String showpercent = (String)this.getFormHM().get("showpercent"); 
			   String group = (String)this.getFormHM().get("group"); 
			   charttype = (charttype==null || charttype.length()==0 || "column".equals(charttype)?"11":"pie".equals(charttype)?"20":"1000");
			   if(group!=null && "1".equalsIgnoreCase(group) && "11".equalsIgnoreCase(charttype))
				   charttype="299";
			   if(this.getFormHM().get("data") == null){
				   this.getFormHM().put("dataHtml", "");
				   return;
			   }
			   ArrayList<MorphDynaBean> datalist = (ArrayList<MorphDynaBean>)this.getFormHM().get("data");
			   ArrayList  list = new ArrayList();
			   LinkedHashMap hashMap = null;
			   CommonData commondata = null;
			   if("1000".equals(charttype)){
				   hashMap = new LinkedHashMap();
				   if(group == null){
					   for(MorphDynaBean obj : datalist){
				    	   String dataName = (String)obj.get("dataname");
				    	   String dataValue = String.valueOf(obj.get("datavalue"));
				    	   commondata = new CommonData();
				    	   commondata.setDataName(dataName);
				    	   commondata.setDataValue(dataValue);
				    	   list.add(commondata);
				       }
				   hashMap.put(name, list);
				   }else{
					   ArrayList<MorphDynaBean> fieldlist = (ArrayList<MorphDynaBean>)this.getFormHM().get("field");
					   for(MorphDynaBean obj : fieldlist){
						   String text = (String) obj.get("text");
						   String valueid =(String)obj.get("value");
						   for(MorphDynaBean data : datalist){
							   String dataName = (String)data.get("matrixname");
					    	   String dataValue = String.valueOf(data.get(valueid));
					    	   commondata = new CommonData();
					    	   commondata.setDataName(dataName);
					    	   commondata.setDataValue(dataValue);
					    	   list.add(commondata);
						   }
						   hashMap.put(text,list);
					   }
				   }
			   }else{
				   if(group == null){
					   for(MorphDynaBean obj : datalist){
						   String dataName = (String)obj.get("dataname");
						   String dataValue = String.valueOf(obj.get("datavalue"));
				    	   commondata = new CommonData();
				    	   commondata.setDataName(dataName);
				    	   commondata.setDataValue(dataValue);
						   list.add(commondata);
					   }
				   }else{
					   ArrayList<MorphDynaBean> fieldlist = (ArrayList<MorphDynaBean>)this.getFormHM().get("field");
					   for(MorphDynaBean obj : fieldlist){
						   String text = (String) obj.get("text");
						   String valueid =(String)obj.get("value");
						   ArrayList itemlist = new ArrayList();
						   for(MorphDynaBean data : datalist){
							   String dataName = (String)data.get("matrixname");
					    	   String dataValue = String.valueOf(data.get(valueid));
					    	   commondata = new CommonData();
					    	   commondata.setDataName(dataName);
					    	   commondata.setDataValue(dataValue);
					    	   itemlist.add(commondata);
						   }
						   LazyDynaBean  lazyDynaBean= new LazyDynaBean();
						   lazyDynaBean.set("categoryName", text);
						   lazyDynaBean.set("dataList", itemlist);
						   list.add(lazyDynaBean);
					   }
				   }
			   }
			   String option ="";
			   int height = -1;
			   if(group == null && datalist.size()> 15)
				   height = 268;
			   if(group != null && ((ArrayList)this.getFormHM().get("field")).size()>15 )
				   height = 268;
			   EchartsBo bo = new EchartsBo("", Integer.valueOf(charttype), -1, height, "false");
			   bo.setNumDecimals(2);
			   boolean showPercent = "percentage".equals(showpercent)?true:false;
			   bo.setShowpercent(showPercent);
			   if("11".equals(charttype) && group == null)
				   option =  bo.outEchartBarXml(list, "", "");
			   else if("11".equals(charttype) && "1".equals(group))
				   option = bo.outEchartLineGroupsXml(hashMap,"","");
			   else if("1000".equals(charttype))
				   option =  bo.outEchartLineXml(hashMap, "", "");
			   else if("299".equals(charttype))
				   option = bo.outEchartTwoDimXml(list,"","");
			   else{
				   option = bo.outEchartPieXml(list, "", "");
			   }
			   option = option.replace(",\nheight:chartHeight\n","");
			   this.getFormHM().put("dataHtml", option);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

}
