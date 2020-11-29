package com.hjsj.hrms.module.gz.salaryreport.transaction;

import com.hjsj.hrms.businessobject.gz.ReportPageOptionsBo;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

/**
 * 获取打印信息
 * @author sunjian
 *
 */
public class SalaryReportPrintDataTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try {
			HashMap hm = (HashMap) this.getFormHM();
			String rsid = (String) this.getFormHM().get("rsid");
			String rsdtlid = (String) this.getFormHM().get("rsdtlid");
			String opt = (String) this.getFormHM().get("opt");
			String isExcel = (String) this.getFormHM().get("isExcel");//excel不显示仅首页显示和仅尾页显示  0是excel
			ReportPageOptionsBo rpob = new ReportPageOptionsBo(this.getFrameconn(), this.userView, rsid + "", StringUtils.isBlank(rsdtlid)?"0":rsdtlid);
			
			int type = 0;
			// 页面打开数据的传入
			if ("3".equalsIgnoreCase(opt)) {
				ReportParseVo orp = rpob.analyse(type);
				this.getFormHM().put("isExcel", isExcel);
				// 页面设置页签数据
				this.getFormHM().put("Pagetype", orp.getPagetype());
				this.getFormHM().put("Top", orp.getTop());
				this.getFormHM().put("Left", orp.getLeft());
				this.getFormHM().put("Top", orp.getTop());
				this.getFormHM().put("Orientation", orp.getOrientation());
				this.getFormHM().put("Right", orp.getRight());
				this.getFormHM().put("Bottom", orp.getBottom());
				this.getFormHM().put("Height", orp.getHeight());
				this.getFormHM().put("Width", orp.getWidth());
				// 标题页签数据
				this.getFormHM().put("title_content", orp.getTitle_fw());
				this.getFormHM().put("title_fontface", orp.getTitle_fn());
				this.getFormHM().put("title_fontsize", orp.getTitle_fz());
				this.getFormHM().put("title_fontblob", orp.getTitle_fb());
				this.getFormHM().put("title_underline", orp.getTitle_fu());
				this.getFormHM().put("title_fontitalic", orp.getTitle_fi());
				this.getFormHM().put("title_delline", orp.getTitle_fs());
				this.getFormHM().put("title_color", orp.getTitle_fc());
				// 页头页签数据
				this.getFormHM().put("head_left", orp.getHead_flw());
				// System.out.print(orp.getHead_flw());
				this.getFormHM().put("head_center", orp.getHead_fmw());
				this.getFormHM().put("head_right", orp.getHead_frw());
				this.getFormHM().put("head_fontblob", orp.getHead_fb());
				this.getFormHM().put("head_underline", orp.getHead_fu());
				this.getFormHM().put("head_fontitalic", orp.getHead_fi());
				this.getFormHM().put("head_delline", orp.getHead_fs());
				this.getFormHM().put("head_fontface", orp.getHead_fn());
				this.getFormHM().put("head_fontsize", orp.getHead_fz());
				this.getFormHM().put("head_fc", orp.getHead_fc());
				this.getFormHM().put("head_flw_hs", orp.getHead_flw_hs());
				this.getFormHM().put("head_fmw_hs", orp.getHead_fmw_hs());
				this.getFormHM().put("head_frw_hs", orp.getHead_frw_hs());
				// 页尾页签数据
				this.getFormHM().put("tail_left", orp.getTile_flw());
				this.getFormHM().put("tail_center", orp.getTile_fmw());
				this.getFormHM().put("tail_right", orp.getTile_frw());
				// this.getFormHM().put("title_content",orp.getHead_fw());
				this.getFormHM().put("tail_fontface", orp.getTile_fn());
				this.getFormHM().put("tail_fontsize", orp.getTile_fz());
				this.getFormHM().put("tail_fontblob", orp.getTile_fb());
				this.getFormHM().put("tail_underline", orp.getTile_fu());
				this.getFormHM().put("tail_fontitalic", orp.getTile_fi());
				this.getFormHM().put("tail_delline", orp.getTile_fs());
				this.getFormHM().put("tail_fc", orp.getTile_fc());
				this.getFormHM().put("tail_flw_hs", orp.getTile_flw_hs());
				this.getFormHM().put("tail_fmw_hs", orp.getTile_fmw_hs());
				this.getFormHM().put("tail_frw_hs", orp.getTile_frw_hs());
				// 正文页签数据
				this.getFormHM().put("text_fn", orp.getBody_fn());
				this.getFormHM().put("text_fz", orp.getBody_fz());
				this.getFormHM().put("text_fb", orp.getBody_fb());
				this.getFormHM().put("text_fu", orp.getBody_fu());
				this.getFormHM().put("text_fi", orp.getBody_fi());
				this.getFormHM().put("text_fc", orp.getBody_fc());
				this.getFormHM().put("phead_fn", orp.getThead_fn());
				this.getFormHM().put("phead_fz", orp.getThead_fz());
				this.getFormHM().put("phead_fb", orp.getThead_fb());
				this.getFormHM().put("phead_fu", orp.getThead_fu());
				this.getFormHM().put("phead_fi", orp.getThead_fi());
				this.getFormHM().put("phead_fc", orp.getThead_fc());
				
				// this.getFormHM().put("pagesetupValue",pagesetupValue);
				// getForm().setValues
			} else {
				// 页面数据保存与初始化
				if ("1".equalsIgnoreCase(opt))
					type = 1;// 初始化
				
				MorphDynaBean pagesetupValue = (MorphDynaBean) this.getFormHM().get("pagesetupValue");
				MorphDynaBean titleValue = (MorphDynaBean) this.getFormHM().get("titleValue");
				MorphDynaBean pageheadValue = (MorphDynaBean) this.getFormHM().get("pageheadValue");
				MorphDynaBean pagetailidValue = (MorphDynaBean) this.getFormHM().get("pagetailidValue");
				MorphDynaBean textValueValue = (MorphDynaBean) this.getFormHM().get("textValueValue");
				ReportParseVo rpv = ReportParseVo.setReportDetailXml(pagesetupValue,titleValue,pageheadValue,pagetailidValue,textValueValue);
				String xml = rpob.createXML(rpv, type);
				rpob.saveXML(xml,rpv);
				this.getFormHM().put("xmltype", "ok");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
