package com.hjsj.hrms.transaction.general.deci.leader;


import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Date;
/**
 * 
 *<p>Title:QueryLeaderTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 26, 2007</p> 
 *@author sunxin
 *@version 4.0
 */
public class QueryLeaderTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String a_code=(String)this.getFormHM().get("a_code");
		String code="";
		String kind="";
		if(a_code==null||a_code.length()<=0)
		{
			if("UN".equalsIgnoreCase(this.userView.getManagePrivCode()))
				kind="2";
			else if("UM".equalsIgnoreCase(this.userView.getManagePrivCode()))
				kind="1";
			else if("@K".equalsIgnoreCase(this.userView.getManagePrivCode()))
				kind="0";
			code=this.userView.getManagePrivCodeValue();
			if(code.length()==0){
				/*ContentDAO dao = new ContentDAO(this.frameconn);
				try {
					this.frowset = dao.search("select min(b0110) b0110 from b01");
					if(frowset.next()){
						code=frowset.getString("b0110");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				code = getMinCodeitemid(); //暂时先这样固定一下
				a_code="UN"+code;
			}else{
				a_code = this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
			}
		}else
		{
			if(a_code.indexOf("UN")!=-1)
			{
				kind="2";
			}else if(a_code.indexOf("UM")!=-1)
			{
				kind="1";
			}else if(a_code.indexOf("@K")!=-1)
			{
				kind="0";
			}
			code=a_code.substring(2);
		}
		//a_code ="UM1";
		//code = "1";
		this.getFormHM().put("code",code);
		this.getFormHM().put("kind",kind);
		this.getFormHM().put("a_code",a_code);		
		LeadarParamXML leadarParamXML=new LeadarParamXML(this.getFrameconn());
		String unit_card=leadarParamXML.getTextValue(LeadarParamXML.UNIT_ZJ);
		String loadtype=leadarParamXML.getTextValue(LeadarParamXML.LOADTYPE);
		if("".equalsIgnoreCase(loadtype))
			loadtype="2";
		this.getFormHM().put("columns",unit_card);
		this.getFormHM().put("loadtype",loadtype);
		String gcond=leadarParamXML.getTextValue(LeadarParamXML.GCOND);	
		this.getFormHM().put("gcond", gcond);
		String display_field=leadarParamXML.getTextValue(LeadarParamXML.OUTPUT);//显示指标
		this.getFormHM().put("display_field", display_field);
	}

	/**
	 * @param args
	 */
	
	private String getMinCodeitemid(){
		//liuy 2014-10-31 4701:总裁桌面/关键人才/领导班子下默认不显示“单位人员编制”子集数据，要手工定位到相应的单位下才可以出数据。而演示库默认定位的最高的单位。 start
		//String sql = "select min(codeitemid) as codeitemid from organization where codesetid = 'UN'";
		StringBuffer sql=new StringBuffer();
		sql.append("select min(codeitemid) as codeitemid from organization where codesetid = 'UN'");
		String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
		sql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
		//liuy end
		ContentDAO dao = new ContentDAO(this.frameconn);
		String codeitemid = "";
		try {
			this.frowset = dao.search(sql.toString());
			if(this.frowset.next()){
				codeitemid = this.frowset.getString("codeitemid");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return codeitemid;
	} 
}
