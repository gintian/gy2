package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class AutoNumberVindicateTrans extends IBusiness{

	/**人员自动编号*/
	public void execute() throws GeneralException {
		String p0201=(String) this.getFormHM().get("p0201");
		String autoNumber=(String) this.getFormHM().get("autoNumber");
		//System.out.println(p0201+"---"+autoNumber);
		try {
			if(p0201!=null&&p0201.length()>0&&autoNumber!=null&&autoNumber.length()>4){
				String[] autos=autoNumber.split("`");
				if(autos.length==2&&autos[1]!=null&&autos[0]!=null){
					ContentDAO dao=new ContentDAO(this.getFrameconn());
					int lth=Integer.parseInt(autos[1]);
					String sql="select p0300 from p03 where p0201="+p0201+" order by a0100";
					this.frecset=dao.search(sql);
					int i=1;
					while(this.frecset.next()){
						String p0300=this.frecset.getString("p0300");
						sql="update p03 set "+autos[0]+"='"+autoNumber(i,lth)+"' where p0300="+p0300;
						dao.update(sql);
						i++;
					}
					
					editXmlP02(dao,p0201,autos[0]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**添加自动编码标识 */
	private void editXmlP02(ContentDAO dao,String p0201, String autoNumber) throws Exception {
		String xmlstr="";
		String sql="select extendattr from p02 where p0201="+p0201;
		this.frecset=dao.search(sql);
		if(this.frecset.next()){
			xmlstr=this.frecset.getString("extendattr");
		}
		if(xmlstr!=null&&xmlstr.length()>10){
			Document doc=DocumentHelper.parseText(xmlstr);
			Element root = doc.getRootElement();
			Element personcode_menu=root.element("personcode_menu");
			if(personcode_menu==null)
				personcode_menu=root.addElement("personcode_menu");
			personcode_menu.setText(autoNumber);
			dao.update("update p02 set extendattr='"+doc.asXML()+"' where p0201="+p0201);
		}
	}

	//位数不足lth 前面补0
	private String autoNumber(int i,int lth){
		String str=String.valueOf(i);
		while (str.length() < lth)
			str = "0" + str;
		return str;
	}
}
