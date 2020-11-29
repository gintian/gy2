package com.hjsj.hrms.transaction.pos.posreport;

import com.hjsj.hrms.interfaces.xmlparameter.SetOrgOptionParameter;
import com.hjsj.hrms.valueobject.common.LabelValueView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;

public class SaveOrgOptionSetTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		List dblist=this.userView.getPrivDbList();
		String dbnames="Usr";
		if(dblist!=null && !dblist.isEmpty())
			dbnames=(String)dblist.get(0);
		ArrayList nodenamevaluelist=new ArrayList();
		//LabelValueView labelvalue0=new LabelValueView((String)this.getFormHM().get("cellletteralignleft")!=null?(String)this.getFormHM().get("cellletteralignleft"):"noalign-left","cellletteralignleft");
		//LabelValueView labelvalue1=new LabelValueView((String)this.getFormHM().get("cellletteralignright")!=null?(String)this.getFormHM().get("cellletteralignright"):"noalign-right","cellletteralignright");
		//LabelValueView labelvalue2=new LabelValueView((String)this.getFormHM().get("cellletteraligncenter")!=null?(String)this.getFormHM().get("cellletteraligncenter"):"noalign-center","cellletteraligncenter");
		LabelValueView labelvalue0=new LabelValueView((String)this.getFormHM().get("cellletteralignleft")!=null?(String)this.getFormHM().get("cellletteralignleft"):"1","cellletteralignleft");
		LabelValueView labelvalue3=new LabelValueView((String)this.getFormHM().get("celllettervaligncenter")!=null?(String)this.getFormHM().get("celllettervaligncenter"):"novalign-center","celllettervaligncenter");
		LabelValueView labelvalue4=new LabelValueView((String)this.getFormHM().get("cellletterfitsize")!=null?(String)this.getFormHM().get("cellletterfitsize"):"false","cellletterfitsize");
		LabelValueView labelvalue5=new LabelValueView((String)this.getFormHM().get("cellletterfitline")!=null?(String)this.getFormHM().get("cellletterfitline"):"false","cellletterfitline");
		LabelValueView labelvalue6=new LabelValueView((String)this.getFormHM().get("fontfamily")!=null?(String)this.getFormHM().get("fontfamily"):"song","fontfamily");
		LabelValueView labelvalue7=new LabelValueView((String)this.getFormHM().get("fontstyle")!=null?(String)this.getFormHM().get("fontstyle"):"general","fontstyle");
		LabelValueView labelvalue8=new LabelValueView((String)this.getFormHM().get("fontsize")!=null?(String)this.getFormHM().get("fontsize"):"12","fontsize");
		LabelValueView labelvalue9=new LabelValueView((String)this.getFormHM().get("fontcolor")!=null?(String)this.getFormHM().get("fontcolor"):"#000000","fontcolor");
		LabelValueView labelvalue10=new LabelValueView((String)this.getFormHM().get("cellhspacewidth")!=null?(String)this.getFormHM().get("cellhspacewidth"):"10","cellhspacewidth");
		LabelValueView labelvalue11=new LabelValueView((String)this.getFormHM().get("cellvspacewidth")!=null?(String)this.getFormHM().get("cellvspacewidth"):"10","cellvspacewidth");
		LabelValueView labelvalue12=new LabelValueView((String)this.getFormHM().get("celllinestrokewidth")!=null?(String)this.getFormHM().get("celllinestrokewidth"):"1","celllinestrokewidth");
		LabelValueView labelvalue13=new LabelValueView((String)this.getFormHM().get("cellshape")!=null?(String)this.getFormHM().get("cellshape"):"rect","cellshape");
		LabelValueView labelvalue14=new LabelValueView((String)this.getFormHM().get("cellwidth")!=null?(String)this.getFormHM().get("cellwidth"):"80","cellwidth");
		LabelValueView labelvalue15=new LabelValueView((String)this.getFormHM().get("cellheight")!=null?(String)this.getFormHM().get("cellheight"):"60","cellheight");
		LabelValueView labelvalue16=new LabelValueView((String)this.getFormHM().get("isshowpersonconut")!=null?(String)this.getFormHM().get("isshowpersonconut"):"false","isshowpersonconut");
		LabelValueView labelvalue17=new LabelValueView((String)this.getFormHM().get("isshowpersonname")!=null?(String)this.getFormHM().get("isshowpersonname"):"false","isshowpersonname");
		LabelValueView labelvalue18=new LabelValueView((String)this.getFormHM().get("namesinglecell")!=null?(String)this.getFormHM().get("namesinglecell"):"false","namesinglecell");
		LabelValueView labelvalue19=new LabelValueView((String)this.getFormHM().get("cellcolor")!=null?(String)this.getFormHM().get("cellcolor"):"#000000","cellcolor");
		LabelValueView labelvalue20=new LabelValueView((String)this.getFormHM().get("cellaspect")!=null?(String)this.getFormHM().get("cellaspect"):"true","cellaspect");
		LabelValueView labelvalue21=new LabelValueView((String)this.getFormHM().get("graph3d")!=null?(String)this.getFormHM().get("graph3d"):"false","graph3d");
		LabelValueView labelvalue22=new LabelValueView((String)this.getFormHM().get("graphaspect")!=null?(String)this.getFormHM().get("graphaspect"):"true","graphaspect");
		LabelValueView labelvalue23=new LabelValueView((String)this.getFormHM().get("dbnames")!=null?(String)this.getFormHM().get("dbnames"):dbnames,"dbnames");
		LabelValueView labelvalue24=new LabelValueView((String)this.getFormHM().get("isshowposup")!=null?(String)this.getFormHM().get("isshowposup"):"false","isshowposup");
		nodenamevaluelist.add(labelvalue0);
       	//nodenamevaluelist.add(labelvalue1);
       	//nodenamevaluelist.add(labelvalue2);
       	nodenamevaluelist.add(labelvalue3);
       	nodenamevaluelist.add(labelvalue4);
       	nodenamevaluelist.add(labelvalue5);
       	nodenamevaluelist.add(labelvalue6);
       	nodenamevaluelist.add(labelvalue7);
       	nodenamevaluelist.add(labelvalue8);
       	nodenamevaluelist.add(labelvalue9);
       	nodenamevaluelist.add(labelvalue10);
       	nodenamevaluelist.add(labelvalue11);
       	nodenamevaluelist.add(labelvalue12);
       	nodenamevaluelist.add(labelvalue13);
       	nodenamevaluelist.add(labelvalue14);
       	nodenamevaluelist.add(labelvalue15);
       	nodenamevaluelist.add(labelvalue16);
       	nodenamevaluelist.add(labelvalue17);
       	nodenamevaluelist.add(labelvalue18);
       	nodenamevaluelist.add(labelvalue19);
       	nodenamevaluelist.add(labelvalue20);
       	nodenamevaluelist.add(labelvalue21);
       	nodenamevaluelist.add(labelvalue22);
       	nodenamevaluelist.add(labelvalue23);
       	nodenamevaluelist.add(labelvalue24);
       	new SetOrgOptionParameter().WriteOutParameterXml("POS_MAPOPTION",this.getFrameconn(),nodenamevaluelist);
   	}

}
