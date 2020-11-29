package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * 归档显示信息集设置
 * @author xujian
 *Mar 22, 2010
 */
public class SetSformulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String snameid = (String)hm.get("id");
		String infokind = (String)hm.get("infokind");
		String chart_type = "12";
		String minvalue = "";
		String maxvalue = "";
		String outsminvalue = "";
		String outsmaxvalue = "";
		StringBuffer valve = new StringBuffer();
		StringBuffer sql = new StringBuffer();
		sql.append("select viewtype from sname where id=?");
		ArrayList idparam = new ArrayList();
		idparam.add(snameid);
		List rs =ExecuteSQL.executePreMyQuery(sql.toString(), idparam, this.getFrameconn());
		if (!rs.isEmpty()) {
			LazyDynaBean rec=(LazyDynaBean)rs.get(0);
			chart_type = rec.get("viewtype")!=null?rec.get("viewtype").toString():"";
		}
		/*----------------------------------------------------*/
		if("42".equalsIgnoreCase(chart_type)|| "43".equalsIgnoreCase(chart_type)|| "44".equalsIgnoreCase(chart_type)){
				ArrayList valves =new ArrayList();
				valves=getValve(snameid);
				if(valves.size()==0){}else{
				minvalue=(String)((ArrayList)valves.get(0)).get(0);
				maxvalue=(String)((ArrayList)valves.get(1)).get(0);
				ArrayList valves1 =new ArrayList();
				valves1=(ArrayList)valves.get(2);
				if("43".equalsIgnoreCase(chart_type)){
				for(int i=0;i<valves1.size();i++){
					valve.append(valves1.get(i));
					if(i!=valves1.size()-1){
						valve.append(";");
					}
				}
					}else{
				valve.append(valves1.get(0));
					}
				}
				
		}
		
		
		
		
	
		/*----------------------------------------------------*/
		ArrayList noitems = new ArrayList();
		ArrayList yesitems = new ArrayList();
		ArrayList fieldsetlist = new ArrayList();
		String title = "";
		String sformula = "";
		try{
			SformulaXml xml = new SformulaXml(this.frameconn,snameid);
			List list = xml.getAllChildren();
			boolean flag = false;
			for(int i=0;i<list.size();i++){
				Element element = (Element) list.get(i);
				CommonData cd = new CommonData();
				cd.setDataName(element.getAttributeValue("title"));
				cd.setDataValue(element.getAttributeValue("id"));
				String del = element.getAttributeValue("del");
				if (del == null) {
					yesitems.add(cd);
					flag = true;
				} else if ("0".equals(del)) {
					flag = true;
					yesitems.add(cd);
				} else if("1".equals(del)) {
					flag = true;
				}
				
				
			}
			
			if(!flag) {
				CommonData cd = new CommonData();
				cd.setDataName("个数");
				cd.setDataValue("0");
				xml.setCountValue("0", "个数", "", "count", "0","0");
				xml.saveStrValue();
				yesitems.add(cd);
			}
			
			CommonData cd = new CommonData("count",ResourceFactory.getProperty("org.maip.number.count"));
			noitems.add(cd);
			cd = new CommonData("sum",ResourceFactory.getProperty("kq.formula.sum"));
			noitems.add(cd);
			cd = new CommonData("max",ResourceFactory.getProperty("kq.formula.max"));
			noitems.add(cd);
			cd = new CommonData("min",ResourceFactory.getProperty("kq.formula.min"));
			noitems.add(cd);
			cd = new CommonData("avg",ResourceFactory.getProperty("kq.formula.average"));
		    noitems.add(cd);
			
			TempvarBo tempvarbo = new TempvarBo();
			if("1".equals(infokind))
				infokind="4";
			fieldsetlist = tempvarbo.fieldList(this.userView,infokind);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.getFormHM().put("snameid",snameid);
			this.getFormHM().put("minvalue", "".equals(minvalue)?"0":minvalue);
			this.getFormHM().put("maxvalue", "".equals(maxvalue)?"0":maxvalue);
			this.getFormHM().put("outsminvalue", "".equals(minvalue)?"0":minvalue);
			this.getFormHM().put("outsmaxvalue", "".equals(maxvalue)?"0":maxvalue);
			this.getFormHM().put("valve", valve.toString());
			this.getFormHM().put("chart_type", chart_type);
			this.getFormHM().put("yesitems", yesitems);
			this.getFormHM().put("noitems", noitems);
			this.getFormHM().put("title", title);
			this.getFormHM().put("sformula", sformula);
			this.getFormHM().put("fieldsetlist", fieldsetlist);
		}
		
	}
	
	/**
	 * 获取各个阀值
	 * 
	 * valvesList
	 * get（0） String 最小值
	 *  get（1） String 最大值
	 * get（2） ArrayList   阀值
	 *	 
	 * 
	 * */
	private ArrayList getValve(String statId){
		ArrayList valvesList = new ArrayList();
		ArrayList valves = new ArrayList();

		StringBuffer sql =new StringBuffer();
		sql.append("select valve from SLegend where id=");
		sql.append(statId);
		List rs =ExecuteSQL.executeMyQuery(sql.toString());
		if (!rs.isEmpty()) {
			LazyDynaBean rec=(LazyDynaBean)rs.get(0);
			String valve = rec.get("valve")!=null?rec.get("valve").toString():"";
			if(valve==null|| "".equals(valve)||valve==""){
			}else{
			StringReader reader=new StringReader(valve.toString());
			try {
				Document doc=PubFunc.generateDom(valve.toString());
				Element et = doc.getRootElement();;
				ArrayList minvalue = new ArrayList();
				minvalue.add(((Element)et.getChild("minValue")).getText());
				ArrayList maxvalue = new ArrayList();
				maxvalue.add(((Element)et.getChild("maxValue")).getText());
				List valveList = et.getChild("valves").getChildren();
				for(int i=0;i<valveList.size();i++){
					valves.add(((Element)valveList.get(i)).getText());
				}
				valvesList.add(minvalue);
				valvesList.add(maxvalue);
				valvesList.add(valves);
				
			} catch (JDOMException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
			}
		}
		return valvesList;
	}

}
