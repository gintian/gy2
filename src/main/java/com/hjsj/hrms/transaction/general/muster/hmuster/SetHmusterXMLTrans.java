package com.hjsj.hrms.transaction.general.muster.hmuster;

import com.hjsj.hrms.interfaces.general.HmusterXML;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SetHmusterXMLTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
	
		String tabid = (String)this.getFormHM().get("tabid");
		tabid=tabid!=null&&tabid.trim().length()>0?tabid:"0";
		
		HmusterXML hmxml = new HmusterXML(this.getFrameconn(),tabid);
		
		String column = (String)this.getFormHM().get("column");
		column=column!=null&&column.trim().length()>0?column:"0";

		if("0".equals(column)){
			hmxml.setValue(HmusterXML.COLUMN,"0");
		}else{
			hmxml.setValue(HmusterXML.COLUMN,"1");
		}
		hmxml.setValue(HmusterXML.HZ,column);

		String dataarea = (String)this.getFormHM().get("dataarea");
		dataarea=dataarea!=null&&dataarea.trim().length()>0?dataarea:"0";
		
		hmxml.setValue(HmusterXML.DATAAREA,dataarea);
		
		String pix = (String)this.getFormHM().get("pix");
			pix=pix!=null&&pix.trim().length()>0?pix:"0";
			hmxml.setValue(HmusterXML.PIX,pix);
			
		String columnLine = (String)this.getFormHM().get("columnLine");
			columnLine=columnLine!=null&&columnLine.trim().length()>0?columnLine:"0";
			hmxml.setValue(HmusterXML.COLUMNLINE,columnLine);
			
		String pointgroup = (String)this.getFormHM().get("pointgroup");
			pointgroup=pointgroup!=null&&pointgroup.trim().length()>0?pointgroup:"0";
	
		String groupPoint = (String)this.getFormHM().get("groupPoint");
			groupPoint=groupPoint!=null&&groupPoint.trim().length()>0?groupPoint:"";
		String pointgroup2= (String)this.getFormHM().get("pointgroup2");
		pointgroup2=pointgroup2!=null&&pointgroup2.trim().length()>0?pointgroup2:"0";
		String groupPoint2 = (String)this.getFormHM().get("groupPoint2");
		groupPoint2=groupPoint2!=null&&groupPoint2.trim().length()>0?groupPoint2:"";
		String multigroups = (String)this.getFormHM().get("multigroups");
			multigroups=multigroups!=null&&multigroups.trim().length()>0?multigroups:"0";
			
		String layerid = (String)this.getFormHM().get("layerid");
		layerid=layerid!=null&&layerid.trim().length()>0?layerid:"";
		String layerid2 = (String)this.getFormHM().get("layerid2");
		layerid2=layerid2!=null&&layerid2.trim().length()>0?layerid2:"";
			
		String emptyRow = (String)this.getFormHM().get("emptyRow");
			emptyRow=emptyRow!=null&&emptyRow.trim().length()>0?emptyRow:"0";
			
		String sortitem = (String)this.getFormHM().get("sortitem");
			sortitem=sortitem!=null&&sortitem.trim().length()>0?sortitem:"";
			hmxml.setValue(HmusterXML.SORTSTR,sortitem);
		
		hmxml.setNgrid(emptyRow);
		hmxml.setValue(HmusterXML.GROUPLAYER,layerid);	
		hmxml.setValue(HmusterXML.GROUPLAYER2, layerid2);
		if("1".equals(pointgroup)){
			hmxml.setValue(HmusterXML.GROUPFIELD,groupPoint);
			hmxml.setValue(HmusterXML.MULTIGROUPS,multigroups);
		}else{
			hmxml.setValue(HmusterXML.GROUPFIELD,"");
			hmxml.setValue(HmusterXML.MULTIGROUPS,"0");
		}
		if("1".equals(pointgroup2))
		{
			hmxml.setValue(HmusterXML.GROUPFIELD2,groupPoint2);
		}else
		{
			hmxml.setValue(HmusterXML.GROUPFIELD2,"");
		}
		String showPartJob=(String)this.getFormHM().get("showPartJob");
		if(showPartJob!=null&&!"".equals(showPartJob))
			hmxml.setValue(HmusterXML.SHOW_PART_JOB,showPartJob);
		
		hmxml.saveValue();
	}

}
