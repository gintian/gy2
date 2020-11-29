package com.hjsj.hrms.transaction.general.sprelationmap;

import com.hjsj.hrms.businessobject.general.sprelationmap.RelationMapBo;
import com.hjsj.hrms.businessobject.general.sprelationmap.RelationMapLine;
import com.hjsj.hrms.businessobject.general.sprelationmap.RelationMapNode;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchRelationMapTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			/*String clientWidth=(String)this.getFormHM().get("clientWidth");
			String clientHeight=(String)this.getFormHM().get("clientHeight");*/
			String clientWidth="1900";
			String clientHeight="1500";
			String relationType=(String)this.getFormHM().get("relationType");
			String a_code=(String)this.getFormHM().get("a_code");
			/* 使默认进来显示顶级机构*/
			if("UN".equalsIgnoreCase(a_code)){
				a_code="UN01";
			}
			
			String imagesPath=SafeCode.decode((String)this.getFormHM().get("imagesPath"));
			RelationMapBo bo=new RelationMapBo(this.getFrameconn(),this.userView,relationType);
			bo.setClientHeight(Double.parseDouble(clientHeight));
			bo.setClientWidth(Double.parseDouble(clientWidth));
			RelationMapBo.imagesPath=imagesPath;
			RelationMapBo.colorMap.clear();
			RelationMapBo.colorMap2.clear();
			HashMap dataMap = bo.drawChart(a_code);
			
			
			ArrayList nodeList = (ArrayList)dataMap.get("node");
			 ArrayList lineList = (ArrayList)dataMap.get("line");
			 String truewidth=bo.getTrueWidth()+"";
			 String trueheight=bo.getTrueHeight()+"";
			 String fontStyle=bo.getFontStyle();
			StringBuffer xml=new StringBuffer("<?xml version='1.0' encoding='utf-8'?>" );
			xml.append("<chart caption='' bgColor='AEEEEE,FFFFFF'  BorderAlpha='0'  canvasBgAlpha='0,0' canvasBorderAlpha='0' xAxisMinValue='0'  xAxisMaxValue='"+truewidth+"' yAxisMinValue='0' viewMode='1'  yAxisMaxValue='"+trueheight+"'");
			xml.append("   allowDrag='1'   bubbleScale='1' is3D='1' numDivLines='0' showFormBtn='0'>");
			xml.append("<dataset plotborderAlpha='100'   >");
			for(int i=0;i<nodeList.size();i++){
			   RelationMapNode node=(RelationMapNode)nodeList.get(i);
			   xml.append(node.toNodeXml());
			   xml.append(" ");
			} 
			xml.append("</dataset>");
			xml.append("<connectors color='000000' stdThickness='8'>");
			for(int j=0;j<lineList.size();j++){
			   RelationMapLine line = (RelationMapLine)lineList.get(j);
			   xml.append(line.toConnectorXml());
			   xml.append(" ");
			}
			xml.append("</connectors>");
			xml.append("<styles>");
			xml.append("<definition>");
			xml.append(fontStyle);
			xml.append("</definition>");
			xml.append("<application>");
			xml.append("<apply toObject='DATALABELS' styles='MyFirstFontStyle' />"); 
			xml.append("<apply toObject='DATAVALUES' styles='MyFirstFontStyle' />");

			xml.append("</application>");
			xml.append("</styles>");
			xml.append("</chart>");
			
			this.getFormHM().put("xmlData", xml.toString());
			
			a_code=bo.getA_code();
			this.getFormHM().put("clientWidth", clientWidth);
			this.getFormHM().put("clientHeight", clientHeight);
			this.getFormHM().put("trueWidth", bo.getTrueWidth()+"");
			this.getFormHM().put("trueHeight",bo.getTrueHeight()+"");
			this.getFormHM().put("dataMap",dataMap);
			this.getFormHM().put("a_code", a_code);
			this.getFormHM().put("relationType", relationType);
			this.getFormHM().put("menuConstantTop", bo.getMenuConstantTop());
			this.getFormHM().put("menuConstantLeft",bo.getMenuConstantLeft());
			this.getFormHM().put("fontStyle",bo.getFontStyle());
			this.getFormHM().put("nodeType", "");
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
}
