package com.hjsj.hrms.transaction.general.sprelationmap.yfilesmap;

import com.hjsj.hrms.businessobject.general.sprelationmap.ChartParameterCofig;
import com.hjsj.hrms.businessobject.general.sprelationmap.RelationMapBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveParamConfigTrans extends IBusiness{

	public void execute() throws GeneralException {
		
		try{
			String relationType = (String)this.formHM.get("relationType");
			RelationMapBo rmb= new RelationMapBo(this.getFrameconn(),this.userView,relationType);
			ChartParameterCofig cpc = rmb.chartParam;
			cpc.setDirection((String)this.formHM.get("direction")+"");
			cpc.setTheme((String)this.formHM.get("theme"));
			cpc.setBgColor((String)this.formHM.get("bgColor"));
			cpc.setTransitcolor((String)this.formHM.get("transitcolor"));
			cpc.setBorder_color((String)this.formHM.get("border_color"));
			cpc.setBorder_width((String)this.formHM.get("border_width"));
			cpc.setLinecolor((String)this.formHM.get("linecolor"));
			cpc.setLinewidth((String)this.formHM.get("linewidth"));
			cpc.setLr_spacing((String)this.formHM.get("lr_spacing"));
			cpc.setTb_spacing((String)this.formHM.get("tb_spacing"));
			cpc.setIsshowshadow((String)this.formHM.get("isshowshadow"));
			cpc.setFontName((String)this.formHM.get("fontName"));
			cpc.setFontstyle((String)this.formHM.get("fontstyle"));
			cpc.setFontSize((String)this.formHM.get("fontSize"));
			cpc.setFontcolor((String)this.formHM.get("fontcolor"));
			cpc.setHint_items((String)this.formHM.get("hint_items"));
			cpc.setShow_pic((String)this.formHM.get("show_pic"));
			
			rmb.saveParameterConfig(cpc);
		}catch(Exception e){
			 e.printStackTrace();
		}
	}

	
}
