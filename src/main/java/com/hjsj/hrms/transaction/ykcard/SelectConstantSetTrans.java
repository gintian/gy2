package com.hjsj.hrms.transaction.ykcard;

import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 
 * <p>Title: SelectConstantSetTrans </p>
 * <p>Description: 选择薪酬表</p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2014-9-11 下午3:55:45</p>
 * @author yangj
 * @version 1.0
 */
public class SelectConstantSetTrans extends IBusiness {

	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		String fashion_flag=(String)this.getFormHM().get("fashion_flag");
		String codeitemid=(String)this.getFormHM().get("codeitemid");
		String orderid=(String)this.getFormHM().get("orderid");
		String codesetname=(String)this.getFormHM().get("codesetname");
		String codename=(String)this.getFormHM().get("codename");
		XmlParameter xml=new XmlParameter("UN",userView.getUserOrgId(),"00");
		xml.ReadOutParameterXml("SS_SETCARD",this.getFrameconn(),codename);	
		ArrayList cardfieldlist=new ArrayList();
		// 是否读取移动薪酬表设置
		String mobapp = (String)this.getFormHM().get("mobapp");
		if ("true".equals(mobapp)) {
			if ("0".equals(fashion_flag)) {        // 按单位
				String mobcardid = xml.getMobcardid();
				CardConstantSet cardConstantSet = new CardConstantSet(this.userView, this.getFrameconn());
				cardfieldlist = cardConstantSet.getCardlist(mobcardid);
			} else if ("1".equals(fashion_flag)) { // 按代码类
				ArrayList cardnolist = xml.getMobcardnoList();
				cardfieldlist = getCardFieldS(cardnolist, orderid, codename);
			}
		} else {
			// 按单位
			if ("0".equals(fashion_flag)) {
				String cardid = xml.getCard_id();
				CardConstantSet cardConstantSet = new CardConstantSet(this.userView, this.getFrameconn());
				cardfieldlist = cardConstantSet.getCardlist(cardid);
			} else if ("1".equals(fashion_flag)) {
				// 安代码类
				ArrayList cardnolist = xml.getCardnolist();
				cardfieldlist = getCardFieldS(cardnolist, orderid, codename);
			}
		}
		this.getFormHM().put("cardfieldlist",cardfieldlist);
		this.getFormHM().put("codeitemid",codeitemid);
		this.getFormHM().put("codesetname",codesetname);
		this.getFormHM().put("fashion_flag",fashion_flag);
		this.getFormHM().put("codename",codename);
		ArrayList yklist=new ArrayList();
		String sql="select tabid,name from rname where flagA='A'";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		CommonData dataobj=null;
		try {
			RowSet rs=dao.search(sql);
			while(rs.next())
			{
				String tabid=rs.getString("tabid");
				if(this.userView.isHaveResource(IResourceConstant.CARD, tabid))
				{
					dataobj=new CommonData();
			        dataobj.setDataName("("+rs.getString("tabid")+")"+rs.getString("name"));
			        dataobj.setDataValue(rs.getString("tabid"));
			        yklist.add(dataobj);
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.getFormHM().put("yklist", yklist);
	}
	
    private ArrayList getCardFieldS(ArrayList cardnolist ,String orderid,String codename)
    {
    	ArrayList list=new ArrayList();
    	if(orderid==null||orderid.length()<=0)
    		return list;
    	if(cardnolist==null||cardnolist.size()<=0)
    	{
    		return list;
    	}else
    	{
    		int len=Integer.parseInt(orderid);
    		if(len<cardnolist.size())
    		{
    			String mess=(String)cardnolist.get(len);
    			CardConstantSet cardConstantSet=new CardConstantSet(this.userView,this.getFrameconn());
    			list=cardConstantSet.getCardlist(mess);
    		}else
    		{
    			return list;
    		}
    	}
    	return list;
    }
   
}
