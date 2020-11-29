package com.hjsj.hrms.module.muster.showmuster.transaction;

import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.module.muster.showmuster.businessobject.ShowManageService;
import com.hjsj.hrms.module.muster.showmuster.businessobject.impl.ShowManageServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @Titile: ShowMusterPageConfigTrans
 * @Description:打开页面设置信息类
 * @Company:hjsj
 * @Create time: 2019年4月4日下午5:05:10
 * @author: hjsoft
 * @version 1.0
 *
 */
public class ShowMusterPageConfigTrans extends IBusiness {
	
	@Override
    public void execute() throws GeneralException {
		try {
			HashMap hm = (HashMap) this.getFormHM();
			String opt = (String) this.getFormHM().get("opt");
			String tabid = (String) this.getFormHM().get("tabid");
			ShowManageService showManageService = new ShowManageServiceImpl(this.frameconn,this.userView);
			Map mpc=showManageService.getMusterPageConfig(tabid);//获取页面设置数据 Map类型
			// 页面打开数据回显
			if ("1".equalsIgnoreCase(opt)) {
				this.getFormHM().put("isExcel", "0");
				// 页面设置页签数据
				ReportParseVo reportParseVo=(ReportParseVo) mpc.get("reportParseVo");
				this.getFormHM().put("Pagetype", reportParseVo.getPagetype());
				this.getFormHM().put("Top", reportParseVo.getTop());
				this.getFormHM().put("Left", reportParseVo.getLeft());
				this.getFormHM().put("Orientation", reportParseVo.getOrientation());
				this.getFormHM().put("Right", reportParseVo.getRight());
				this.getFormHM().put("Bottom", reportParseVo.getBottom());
				this.getFormHM().put("Height",reportParseVo.getHeight());
				this.getFormHM().put("Width", reportParseVo.getWidth());
				// 标题页签数据
				this.getFormHM().put("title_content", reportParseVo.getTitle_fw());
				this.getFormHM().put("title_fontface", reportParseVo.getTitle_fn());
				this.getFormHM().put("title_fontsize", reportParseVo.getTitle_fz());
				this.getFormHM().put("title_fontblob", reportParseVo.getTitle_fb());
				this.getFormHM().put("title_underline",reportParseVo.getTitle_fu());
				this.getFormHM().put("title_fontitalic",reportParseVo.getTitle_fi());
				this.getFormHM().put("title_delline", reportParseVo.getTitle_fs());
				this.getFormHM().put("title_color", reportParseVo.getTitle_fc());
				// 页头页签数据
				this.getFormHM().put("head_left", mpc.get("lhead"));
				this.getFormHM().put("head_center",mpc.get("mhead"));
				this.getFormHM().put("head_right", mpc.get("rhead"));
				this.getFormHM().put("head_fontblob", reportParseVo.getHead_fb());
				this.getFormHM().put("head_underline", reportParseVo.getHead_fu());
				this.getFormHM().put("head_fontitalic", reportParseVo.getHead_fi());
				this.getFormHM().put("head_delline",reportParseVo.getHead_fs());
				this.getFormHM().put("head_fontface", reportParseVo.getHead_fn());
				this.getFormHM().put("head_fontsize", reportParseVo.getHead_fz());
				this.getFormHM().put("head_fc", reportParseVo.getHead_fc());
				// 页尾页签数据
				this.getFormHM().put("tail_left", mpc.get("lfoot"));
				this.getFormHM().put("tail_center", mpc.get("mfoot"));
				this.getFormHM().put("tail_right", mpc.get("rfoot"));
				this.getFormHM().put("tail_fontface", reportParseVo.getTile_fn());
				this.getFormHM().put("tail_fontsize", reportParseVo.getTile_fz());
				this.getFormHM().put("tail_fontblob", reportParseVo.getTile_fb());
				this.getFormHM().put("tail_underline", reportParseVo.getTile_fu());
				this.getFormHM().put("tail_fontitalic", reportParseVo.getTile_fi());
				this.getFormHM().put("tail_delline", reportParseVo.getTile_fs());
				this.getFormHM().put("tail_fc", reportParseVo.getTile_fc());
				// 正文页签数据
				this.getFormHM().put("text_fn", reportParseVo.getBody_fn());
				this.getFormHM().put("text_fz", reportParseVo.getBody_fz());
				this.getFormHM().put("text_fb", reportParseVo.getBody_fb());
				this.getFormHM().put("text_fu", reportParseVo.getBody_fu());
				this.getFormHM().put("text_fi", reportParseVo.getBody_fi());
				this.getFormHM().put("text_fc", reportParseVo.getBody_fc());
			} else if ("3".equalsIgnoreCase(opt)){//初始化
				String initXml="<?xml version=\"1.0\" encoding=\"GB2312\"?>\r\n" + 
						"<report name=\"\" pagetype=\"\" width=\"\" height=\"\" orientation=\"\" top=\"\" bottom=\"\" left=\"\" right=\"\">\r\n" + 
						"  <title content=\"\" />\r\n" + 
						"  <head content=\"\" />\r\n" + 
						"  <tile content=\"\" />\r\n" + 
						"  <body content=\"\" />\r\n" + 
						"</report>";
				String sql="update LName set lhead='',mhead='',rhead='',lfoot='',mfoot='',rfoot='',xml_style=? where tabid=?";
				ArrayList values=new ArrayList();
				values.add(initXml);
				values.add(tabid);
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				dao.update(sql,values);
			}else {
				MorphDynaBean pagesetupValue = (MorphDynaBean) this.getFormHM().get("pagesetupValue");
				MorphDynaBean titleValue = (MorphDynaBean) this.getFormHM().get("titleValue");
				MorphDynaBean pageheadValue = (MorphDynaBean) this.getFormHM().get("pageheadValue");
				MorphDynaBean pagetailidValue = (MorphDynaBean) this.getFormHM().get("pagetailidValue");
				MorphDynaBean textValueValue = (MorphDynaBean) this.getFormHM().get("textValueValue");
				
				//得到Map类型的页面设置数据
				Map pageValue = setPageDetailMap(pagesetupValue,titleValue,pageheadValue,pagetailidValue,textValueValue);
				// 保存页面数据
				pageValue.put("tabid", tabid);
				Map saveRes=showManageService.saveMusterPageConfig(pageValue);
				
				if("sucess".equals(saveRes.get("result"))) {
					this.getFormHM().put("code", "2");
				}else {
					this.getFormHM().put("code", "-1");
				}
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * @param pagesetupValue 页面设置标签数据
	 * @param titleValue 标题标签数据
	 * @param pageheadValue 页头标签数据
	 * @param pagetailidValue 页尾标签数据
	 * @param textValueValue 正文标签数据
	 * @return map类型的页面设置的数据
	 */
	private Map setPageDetailMap(MorphDynaBean pagesetupValue,MorphDynaBean titleValue,MorphDynaBean pageheadValue,MorphDynaBean pagetailidValue,MorphDynaBean textValueValue) {
		Map map=new HashMap();
		try{
			/** 页面设置标签 **/
			String width = "";
			String height = "";
			String pagetype = (String) pagesetupValue.get("pagetype-input");
			if (pagetype == null || pagetype.length() <= 0) {
				pagetype = "A4";
			}else if("a3".equalsIgnoreCase(pagetype)){//由于页面上宽和高是置灰不让修改的form表单提交不上来
				width="297";height="420";
			}else if("a4".equalsIgnoreCase(pagetype)){
				width="210";height="297";
			}else if("a5".equalsIgnoreCase(pagetype)){
				width="148";height="201";
			}else if("b5".equalsIgnoreCase(pagetype)){
				width="182";height="257";
			}else if("16开".equalsIgnoreCase(pagetype)){
				width="184";height="260";
			}else if("32开".equalsIgnoreCase(pagetype)){
				width="130";height="184";
			}
			map.put("pagetype",pagetype);
			map.put("page_width",StringUtils.isBlank(width)?StringUtils.deleteWhitespace((String)pagesetupValue.get("pagewidth-input")):width);
			map.put("page_height",StringUtils.isBlank(height)?StringUtils.deleteWhitespace((String) pagesetupValue.get("pageheight-input")):height);
			map.put("page_range",StringUtils.deleteWhitespace((String) pagesetupValue.get("Orientation")));
			map.put("pagemargin_top",StringUtils.deleteWhitespace((String) pagesetupValue.get("pagetop-input")));
			map.put("pagemargin_bottom",StringUtils.deleteWhitespace((String) pagesetupValue.get("pagebottom-input")));
			map.put("pagemargin_left",StringUtils.deleteWhitespace((String) pagesetupValue.get("pageleft-input")));
			map.put("pagemargin_right",StringUtils.deleteWhitespace((String) pagesetupValue.get("pageright-input")));

			/** 标题 **/
			StringBuffer title=new StringBuffer();
			Map<String,String> titleValuetemp = PubFunc.DynaBean2Map(titleValue);
			if(titleValuetemp.get("checkboxgroupTitle")!=null){
				Object titleCheckboxgroup = (Object) titleValue.get("checkboxgroupTitle");						
				if (titleCheckboxgroup instanceof ArrayList) {
					ArrayList tcpList = (ArrayList) titleCheckboxgroup;
					for (int i = 0; i < tcpList.size(); i++) {
						title.append(tcpList.get(i)+",");
					}
				} else if (titleCheckboxgroup instanceof String) {
					String tcpListStr = (String) titleCheckboxgroup;
					if (tcpListStr != null) {
						title.append(tcpListStr+",");
					}
				}
			}
			title.append("#fn"+(String) titleValue.get("title_fn-input")+",");
			title.append("#fz"+(String) titleValue.get("title_fz-input")+",");
			title.append("#fw"+(String) titleValue.get("titleTextarea")+",");
			title.append("#fc"+(String) titleValue.get("colorTitle-input"));
			map.put("title", title.toString());

			/** 页头 **/
			map.put("lhead", (String)pageheadValue.get("hlTextarea"));
			map.put("mhead",(String)pageheadValue.get("hcTextarea"));
			map.put("rhead", (String)pageheadValue.get("hrTextarea"));
			StringBuffer head=new StringBuffer();
			Map<String,String> pageheadValuetemp = PubFunc.DynaBean2Map(pageheadValue);	
			if(pageheadValuetemp.get("phCheckboxgroup")!=null){
				Object phCheckboxgroup = (Object) pageheadValue.get("phCheckboxgroup");
				if (phCheckboxgroup instanceof ArrayList) {
					ArrayList phList = (ArrayList) phCheckboxgroup;
					for (int i = 0; i < phList.size(); i++) {
						head.append(phList.get(i)+",");
					}
				} else if (phCheckboxgroup instanceof String) {
					String phStr = (String) phCheckboxgroup;
					if (phStr != null) {
						head.append(phStr+",");
					}
				}
				
			}
			
			head.append("#fn"+(String) pageheadValue.get("head_fn-input")+",");
			head.append("#fz"+(String) pageheadValue.get("head_fz-input")+",");
			head.append("#fc"+(String) pageheadValue.get("colorHead-input"));
			map.put("head", head.toString());
			
			/** 页尾 **/
			map.put("lfoot", pagetailidValue.get("tlTextarea").toString());
			map.put("mfoot",pagetailidValue.get("tcTextarea").toString());
			map.put("rfoot", pagetailidValue.get("trTextarea").toString());
			
			StringBuffer tile=new StringBuffer();
			Map<String,String> pagetailidValuetemp = PubFunc.DynaBean2Map(pagetailidValue);
			if(pagetailidValuetemp.get("ptCheckboxgroup")!=null){			
				Object ptCheckboxgroup = (Object) pagetailidValue.get("ptCheckboxgroup");
				if (ptCheckboxgroup instanceof ArrayList) {
					ArrayList ptList = (ArrayList) ptCheckboxgroup;
					for (int i = 0; i < ptList.size(); i++) {
						tile.append(ptList.get(i)+",");
					}
				} else if (ptCheckboxgroup instanceof String) {
					String ptStr = (String) ptCheckboxgroup;
					if (ptStr != null) {
						tile.append(ptStr+",");
					}
				}
			}
			tile.append("#fn"+(String) pagetailidValue.get("tail_fn-input")+",");
			tile.append("#fz"+(String) pagetailidValue.get("tail_fz-input")+",");
			tile.append("#fc"+(String) pagetailidValue.get("colorTail-input"));
			map.put("tile", tile.toString());

			/** 获取正文信息 **/	
			StringBuffer body=new StringBuffer();
			Map<String,String> textValueValuetemp = PubFunc.DynaBean2Map(textValueValue);	
			if(textValueValuetemp.get("hiCheckboxgroup")!=null){
				Object hiCheckboxgroup = (Object)textValueValue.get("hiCheckboxgroup");		
				if (hiCheckboxgroup instanceof ArrayList) {
					ArrayList hiList = (ArrayList) hiCheckboxgroup;
					for (int i = 0; i < hiList.size(); i++) {		
						body.append(hiList.get(i)+",");
					}
				} else if (hiCheckboxgroup instanceof String) {
					String hiStr = (String) hiCheckboxgroup;
					if (hiStr != null) {
						body.append(hiStr+",");
					}
				}
			}
			body.append("#fn"+(String)textValueValue.get("text_fn-input")+",");
			body.append("#fz"+(String)textValueValue.get("text_fz-input")+",");
			body.append("#fc"+(String)textValueValue.get("text_fc-input"));
			map.put("body", body.toString());
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		return map;
	}
}
