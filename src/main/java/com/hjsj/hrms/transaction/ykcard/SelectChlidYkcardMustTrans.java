package com.hjsj.hrms.transaction.ykcard;

import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;

public class SelectChlidYkcardMustTrans extends IBusiness {


	public void execute() throws GeneralException 
	{
		/**关于花名册**/
		String  nFlag="3";
		String infor_kind="1";
		ArrayList hmusterlist=new ArrayList();
		ArrayList a_hmusterlist=getMusterList(infor_kind);					
		for(int i=0;i<a_hmusterlist.size();i++)
		{
			CommonData vo=new CommonData();
			String[] temp=(String[])a_hmusterlist.get(i);		
			if(!this.getUserView().isHaveResource(IResourceConstant.HIGHMUSTER,temp[0]))
				continue;
			vo.setDataName(temp[0]+"."+temp[1]);				
			vo.setDataValue(temp[0]);
			hmusterlist.add(vo);
		}
		this.getFormHM().put("hmusterlist",hmusterlist);
		String mustflag=(String)this.getFormHM().get("mustflag");
		String codeitemid=(String)this.getFormHM().get("codeitemid");
		String orderid=(String)this.getFormHM().get("orderid");
		String codesetname=(String)this.getFormHM().get("codesetname");
		String codename=(String)this.getFormHM().get("codename");
		String org="";
		if(!this.userView.isSuper_admin())
			org=userView.getUserOrgId();
		XmlParameter xml=new XmlParameter("UN",org,"00");
		xml.ReadOutParameterXml("SS_SETCARD",this.getFrameconn(),codename);	
		ArrayList mustfieldlist=new ArrayList();
		if("0".equals(mustflag))//按单位
		{
			String musterid=xml.getMusterid();
			CardConstantSet cardConstantSet=new CardConstantSet(this.userView,this.getFrameconn());
			mustfieldlist=cardConstantSet.getMustlist(musterid,infor_kind,true);
		}else if("1".equals(mustflag))//安代码类
		{
			ArrayList mustlist=xml.getMusteredlist();
			mustfieldlist=getMustFieldS(mustlist ,orderid,codename);
		}
		this.getFormHM().put("mustfieldlist",mustfieldlist);
		this.getFormHM().put("codeitemid",codeitemid);
		this.getFormHM().put("codesetname",codesetname);
		this.getFormHM().put("mustflag",mustflag);
		this.getFormHM().put("codename",codename);
	}
	public ArrayList getMusterList(String inforkind) throws GeneralException {
		ArrayList list = new ArrayList();
		StringBuffer strsql = new StringBuffer();
		int nModule = 0;
		if ("1".equals(inforkind)) // 人员库
		{
			inforkind = "A";
			nModule = 3;
		} else if ("3".equals(inforkind)) // 职位库
		{
			inforkind = "K";
			nModule = 1;
		} else if ("2".equals(inforkind)) // 单位库
		{
			inforkind = "B";
			nModule = 2;

		}
		strsql.append("select tabid,cname from muster_name where flagA='");
		strsql.append(inforkind);
		strsql.append("'");
		if ("A".equals(inforkind))
			strsql.append(" and nModule=" + nModule);
		/* 此三条记录不予显示 */
		strsql.append(" and tabid!=1000 and tabid!=1010 and tabid!=1020");
		strsql.append(" order by tabid");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet recset = null;
		try {
			recset = dao.search(strsql.toString());
			while (recset.next()) {
				String[] temp = new String[2];
				temp[0] = recset.getString("tabid");
				temp[1] = recset.getString("cname");
				list.add(temp);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}
	private ArrayList getMustFieldS(ArrayList cardnolist ,String orderid,String codename)
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
    			list=cardConstantSet.getMustlist(mess,"1",true);
    		}else
    		{
    			return list;
    		}
    	}
    	return list;
    }
}
