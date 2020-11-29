 package com.hjsj.hrms.transaction.general.statics;

 import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
 import com.hjsj.hrms.utils.ResourceFactory;
 import com.hrms.struts.exception.GeneralException;
 import com.hrms.struts.exception.GeneralExceptionHandler;
 import com.hrms.struts.facade.transaction.IBusiness;
 import com.hrms.struts.taglib.CommonData;
 import org.apache.commons.beanutils.LazyDynaBean;
 import org.apache.commons.lang.StringUtils;

 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.LinkedHashMap;
 import java.util.List;

public class ShowTwoDimTrans extends IBusiness {
	
	
	
	

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
	try
	{
		HashMap hm = (HashMap)(this.getFormHM().get("requestPamaHM"));
		String userbase=(String)this.getFormHM().get("userbase");
		if(userbase==null|| "".equals(userbase))
			 userbase="Usr";
		String userbases=(String)this.getFormHM().get("userbases");
		String nam=(String)this.getFormHM().get("mess");
		String querycond=(String)this.getFormHM().get("querycond");
		String sone=(String)this.getFormHM().get("selOne");
        String stwo=(String)this.getFormHM().get("selTwo");
        if(sone==null||stwo==null|| "#".equalsIgnoreCase(sone)|| "#".equalsIgnoreCase(stwo))
        {
        	throw new GeneralException(ResourceFactory.getProperty("error.static.notselect"));
        }
        this.getFormHM().put("selOne", sone);
        this.getFormHM().put("selTwo", stwo);
        String infoFlag = (String)this.getFormHM().get("infor_Flag");
        String result = (String)this.getFormHM().get("result");
        boolean ret=true;
	     if(result==null|| "".equals(result)|| "0".equals(result))
	     {
	    	ret=true; 
	     }else{
	    	 ret =false;
	     }
	     String vtotal = (String)this.getFormHM().get("vtotal");
			vtotal=vtotal==null||vtotal.length()==0?"0":vtotal;
			String htotal = (String)this.getFormHM().get("htotal");
			htotal=htotal==null||htotal.length()==0?"0":htotal;
			
		StatDataEncapsulation simplestat=new StatDataEncapsulation();
		int[][] getValues =null;
		if(userbases==null||userbases.length()==0){
			getValues =simplestat.getDoubleLexprData(sone,stwo,nam,userbase,"",userView.getManagePrivCode()+userView.getManagePrivCodeValue(),userView,infoFlag,ret,vtotal,htotal);
		}else
			getValues =simplestat.getDoubleLexprData(sone,stwo,nam,userbase.toUpperCase(),"",userView.getManagePrivCode()+userView.getManagePrivCodeValue(),userView,infoFlag,ret,userbases,vtotal,htotal);
		if(getValues==null)
        	throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("planar.two.error")));
	    
	    List dlist=simplestat.getVerticalArray();
		List hlist=simplestat.getHorizonArray();
		String snameplay=simplestat.getSNameDisplay();
		int tolvalue=simplestat.getTotalValue();
		this.getFormHM().put("doublevalues",getValues);
		this.getFormHM().put("querycond",querycond);
		this.getFormHM().put("dlist",dlist);
		this.getFormHM().put("hlist",hlist);
		this.getFormHM().put("snamedisplay",snameplay);
		this.getFormHM().put("tovalue",String.valueOf(tolvalue));
		this.getFormHM().put("querycond",querycond);
		this.getFormHM().put("mess",nam);
		this.getFormHM().put("result",result);
		
		
		//二位统计平面直方图、折线图
		String zcFlag = "no";//是否总裁桌面调用
		String chartType ="299";//=29平面直方图  =11折线图  历史时点的分组柱状图结构被改了  为了二位统计与其调用不同的方法  现在修改他的chartype为299
		String chartWidth ="1180";//宽
		//String chartHeight ="356";//高
		String chartHeight ="400";//高
		if(!StringUtils.isEmpty((String)this.getFormHM().get("zcFlag")))
			zcFlag=(String)this.getFormHM().get("zcFlag");
		if("no".equals(zcFlag))
			this.getFormHM().put("chartFlag" ,"no");
		else
			this.getFormHM().put("chartFlag" ,"yes");
		/*if(!StringUtils.isEmpty((String)this.getFormHM().get("chartWidth")))
			chartWidth=(String)this.getFormHM().get("chartWidth");
		if(!StringUtils.isEmpty((String)this.getFormHM().get("chartHeight"))) //zhangcq 2016/8/15 统计值显示不全
			chartHeight=(String)this.getFormHM().get("chartHeight");*/
		ArrayList list = new ArrayList();//平面直方图数据集
		LinkedHashMap dbMap = new LinkedHashMap();//折线图数据集
		for(int i=0;i<dlist.size();i++){
			ArrayList commList = new ArrayList();
			for(int j=0;j<hlist.size();j++){
				CommonData vo=new CommonData(String.valueOf(getValues[i][j]),(String)((LazyDynaBean)hlist.get(j)).get("legend"));
				commList.add(vo);
			}
			LazyDynaBean abean = new LazyDynaBean();
			abean.set("categoryName",(String)((LazyDynaBean)dlist.get(i)).get("legend"));
			abean.set("dataList",commList);
			dbMap.put((String)((LazyDynaBean)dlist.get(i)).get("legend"), commList);
			list.add(abean);
		}
		if(!StringUtils.isEmpty((String)hm.get("chartType")))
			chartType=(String)hm.get("chartType");
		if("299".equals(chartType))
			this.getFormHM().put("histogramlist",list);
		else
			this.getFormHM().put("dataMap",dbMap);
		this.getFormHM().put("chartWidth", chartWidth);
		this.getFormHM().put("chartHeight", chartHeight);
		this.getFormHM().put("chartType", chartType);
		this.getFormHM().put("chartTitle" ,snameplay);	
	}
	catch(Exception ex)
	{
		ex.printStackTrace();
		throw GeneralExceptionHandler.Handle(ex);
	}
 }

}
