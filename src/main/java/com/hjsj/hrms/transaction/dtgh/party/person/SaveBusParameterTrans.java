package com.hjsj.hrms.transaction.dtgh.party.person;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveBusParameterTrans extends IBusiness {


	public void execute() throws GeneralException {
		/**
		 * 
		 * <?xml version="1.0" encoding="GB2312"?>
		 *	<Param>
		 *		<polity column='A01.A0101'>
		 *			<party value='01'>
		 *				<add></add>
		 *				<leave/>
		 *				<iin/>
		 *				<out/>				
		 *			</party>
		 *			<preparty>
		 *				<add></add>
		 *				<up/>
		 *				<leave/>
		 *				<iin/>
		 *				<out/>
		 *			</preparty>
		 *			<important>
		 *				<add></add>
		 *				<up/>
		 *				<leave/>
		 *				<iin/>
		 *				<out/>
		 *			</important>
		 *			<active>
		 *				<add></add>
		 *				<up/>
		 *				<leave/>
		 *				<iin/>
		 *				<out/>
		 *			</active>
		 *			<application>
		 *				<add></add>
		 *				<up/>
		 *				<leave/>
		 *				<iin/>
		 *				<out/>
		 *			</application>
		 *			<member>
		 *				<add></add>
		 *				<leave/>
		 *				<iin/>
		 *				<out/>	
		 *			</member>
		 *			<person>
		 *				<up/>
		 *				<leave/>
		 *				<resumeparty/>
		 *				<iin/>
		 *				<resumemember/>	
		 *			</person>
		 *		</polity>
		 *		<belongparty></belongparty>
		 *		<belongmember></belongmember>
		 *		<belongmeet></belongmeet>
		 *	</Param>
		 *
		 */
		String param = (String)this.getFormHM().get("param");
		String add = (String)this.getFormHM().get("add");
		String up = (String)this.getFormHM().get("up");
		String leave = (String)this.getFormHM().get("leave");
		String iin = (String)this.getFormHM().get("iin");
		String out = (String)this.getFormHM().get("out");
		String resumeparty = (String)this.getFormHM().get("resumeparty");
		String resumemember = (String)this.getFormHM().get("resumemember");
		try{
			ConstantXml xml = new ConstantXml(this.frameconn,"PARTY_PARAM");
			if("party".equals(param)){
				xml.setTextValue("/param/polity/party/add", add);
				xml.setTextValue("/param/polity/party/leave", leave);
				xml.setTextValue("/param/polity/party/iin", iin);
				xml.setTextValue("/param/polity/party/out", out);
			}else if("preparty".equals(param)){
				xml.setTextValue("/param/polity/preparty/add", add);
				xml.setTextValue("/param/polity/preparty/up", up);
				xml.setTextValue("/param/polity/preparty/leave", leave);
				xml.setTextValue("/param/polity/preparty/iin", iin);
				xml.setTextValue("/param/polity/preparty/out", out);
			}else if("important".equals(param)){
				xml.setTextValue("/param/polity/important/add", add);
				xml.setTextValue("/param/polity/important/up", up);
				xml.setTextValue("/param/polity/important/leave", leave);
				xml.setTextValue("/param/polity/important/iin", iin);
				xml.setTextValue("/param/polity/important/out", out);
			}else if("active".equals(param)){
				xml.setTextValue("/param/polity/active/add", add);
				xml.setTextValue("/param/polity/active/up", up);
				xml.setTextValue("/param/polity/active/leave", leave);
				xml.setTextValue("/param/polity/active/iin", iin);
				xml.setTextValue("/param/polity/active/out", out);
			}else if("application".equals(param)){
				xml.setTextValue("/param/polity/application/add", add);
				xml.setTextValue("/param/polity/application/up", up);
				xml.setTextValue("/param/polity/application/leave", leave);
				xml.setTextValue("/param/polity/application/iin", iin);
				xml.setTextValue("/param/polity/application/out", out);
			}else if("member".equals(param)){
				xml.setTextValue("/param/polity/member/add", add);
				xml.setTextValue("/param/polity/member/leave", leave);
				xml.setTextValue("/param/polity/member/iin", iin);
				xml.setTextValue("/param/polity/member/out", out);
			}else if("person".equals(param)){
				xml.setTextValue("/param/polity/person/up", up);
				xml.setTextValue("/param/polity/person/leave", leave);
				xml.setTextValue("/param/polity/person/resumeparty", resumeparty);
				xml.setTextValue("/param/polity/person/iin", iin);
				xml.setTextValue("/param/polity/person/resumemember", resumemember);
			}
			xml.saveStrValue();
		}catch(Exception e){
			GeneralExceptionHandler.Handle(e);
		}finally{
			
		}
	}

}
