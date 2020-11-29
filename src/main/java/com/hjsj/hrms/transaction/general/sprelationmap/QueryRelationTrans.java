package com.hjsj.hrms.transaction.general.sprelationmap;

import com.hjsj.hrms.businessobject.general.sprelationmap.RelationMapBo;
import com.hjsj.hrms.businessobject.general.sprelationmap.RelationMapLine;
import com.hjsj.hrms.businessobject.general.sprelationmap.RelationMapNode;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class QueryRelationTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
			HashMap reqmap = (HashMap)this.getFormHM().get("requestPamaHM");
			String freshType="0";
			if(reqmap!=null&&reqmap.get("freshType")!=null){
				freshType=(String)reqmap.get("freshType");
				reqmap.remove("freshType");
				RelationMapBo.chartParam=null;
			}
			String clientWidth=(String)this.getFormHM().get("clientWidth");
			String clientHeight=(String)this.getFormHM().get("clientHeight");
			String trueHeight=(String)this.getFormHM().get("trueHeight");
			String trueWidth=(String)this.getFormHM().get("trueWidth");
			String xmlData = PubFunc.keyWord_reback(SafeCode.decode((String)this.getFormHM().get("xmlData")));
			String relationType=(String)this.getFormHM().get("relationType");
			String currentNodeId=(String)this.getFormHM().get("currentNodeId");
			String nodeType=(String)this.getFormHM().get("nodeType");
			RelationMapBo bo = new RelationMapBo(this.getFrameconn(),this.getUserView(),relationType);
			bo.setClientHeight(Double.parseDouble(clientHeight));
			bo.setClientWidth(Double.parseDouble(clientWidth));
			bo.setTrueHeight(Double.parseDouble(trueHeight));
			bo.setTrueWidth(Double.parseDouble(trueWidth));
			String a_code=(String)this.getFormHM().get("a_code");
			this.getFormHM().put("a_code", a_code);
			bo.setA_code(a_code);
			HashMap dataMap =bo.parseDataXml(xmlData,currentNodeId,freshType,nodeType);
			
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
			
			
			
			this.getFormHM().put("clientWidth", clientWidth);
			this.getFormHM().put("clientHeight", clientHeight);
			this.getFormHM().put("trueWidth", bo.getTrueWidth()+"");
			this.getFormHM().put("trueHeight",bo.getTrueHeight()+"");
			this.getFormHM().put("dataMap", dataMap);
			this.getFormHM().put("fontStyle",bo.getFontStyle());
			this.getFormHM().put("nodeType", nodeType);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}