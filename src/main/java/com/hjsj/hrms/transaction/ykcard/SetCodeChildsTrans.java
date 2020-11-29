package com.hjsj.hrms.transaction.ykcard;

import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * 得到子集孩子子集
 * <p>Title:SetCodeChildsTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 27, 2007 10:05:50 AM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class SetCodeChildsTrans  extends IBusiness {

	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		String codename = (String) this.getFormHM().get("codename");
		String fashion_flag = (String) this.getFormHM().get("fashion_flag");
		this.getFormHM().put("codename", codename);
		XmlParameter xml = new XmlParameter("UN", userView.getUserOrgId(), "00");
		this.getFormHM().put("fashion_flag", fashion_flag);
		xml.ReadOutParameterXml("SS_SETCARD", this.getFrameconn(), codename);
		ArrayList codenamelist = xml.getCodenamelist();
		ArrayList codesetlist = xml.getCodesetlist();
		ArrayList cardnolist = xml.getCardnolist();
		String codeChlidname = "";
		if (codenamelist != null && codenamelist.size() > 0)
			codeChlidname = (String) codenamelist.get(0);
		if (codeChlidname != null && codeChlidname.length() > 0) {
			if (codeChlidname.equalsIgnoreCase(codename)) {
				this.getFormHM().put("codesetlist", codesetlist);
				this.getFormHM().put("codenamelist", codenamelist);
				this.getFormHM().put("cardnolist", cardnolist);
				CardConstantSet cardConstantSet = new CardConstantSet(this.userView, this.getFrameconn());
				ArrayList cardnomesslist = cardConstantSet.getCardMessList(cardnolist);
				this.getFormHM().put("cardnomesslist", cardnomesslist);
				ArrayList mobcardnolist = xml.getMobcardnoList();
				ArrayList mobcardnomesslist = cardConstantSet.getCardMessList(mobcardnolist);
				this.getFormHM().put("mobcardnomesslist", mobcardnomesslist);
			} else {
				this.getChilds(codename);
			}
		} else {
			this.getChilds(codename);
		}
	}
	
    private void getChilds(String codename) {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer sql = new StringBuffer();
		sql.append("select codesetid from  fielditem");
		sql.append(" where itemid='" + codename + "'");
		ArrayList codesetlist = new ArrayList();
		ArrayList cardnolist = new ArrayList();
		try {
			String codesetid = "";
			this.frowset = dao.search(sql.toString());
			if (this.frowset.next())
				codesetid = this.frowset.getString("codesetid");
			if (codesetid == null || codesetid.length() <= 0) {
				//【51659】如果选择的代码不存在，将这两个属性置空，否则页面配置信息不会清空 guodd 2019-08-09
				this.getFormHM().put("codesetlist", codesetlist);
				this.getFormHM().put("cardnolist", cardnolist);
				return;
			}
			sql = new StringBuffer();
			sql.append("SELECT codeitemid,codesetid FROM codeitem");
			sql.append(" where codesetid='" + codesetid + "'");
			this.frowset = dao.search(sql.toString());
			CommonData dataobj = null;
			while (this.frowset.next()) {
				dataobj = new CommonData();
				dataobj.setDataName(this.frowset.getString("codesetid"));
				dataobj.setDataValue(this.frowset.getString("codeitemid"));
				codesetlist.add(dataobj);
				cardnolist.add("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		CardConstantSet cardConstantSet = new CardConstantSet(this.userView, this.getFrameconn());
		ArrayList cardnomesslist = cardConstantSet.getCardMessList(cardnolist);
		this.getFormHM().put("cardnomesslist", cardnomesslist);
		this.getFormHM().put("mobcardnomesslist", cardnomesslist);
		this.getFormHM().put("codesetlist", codesetlist);
		this.getFormHM().put("cardnolist", cardnolist);
	}
}
