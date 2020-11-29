package com.hjsj.hrms.transaction.dtgh.party.person;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * @author xujian
 *Feb 4, 2010
 */
public class SaveParameterTrans extends IBusiness {


	public void execute() throws GeneralException {
		// TODO Auto-generated method stub

		/**
		 * 
		 * <?xml version="1.0" encoding="GB2312"?>
		 *	<Param>
		 *		<polity colum=''>
		 *			<party value='01'></party>
		 *			<preparty></preparty>
		 *			<important></important>
		 *			<active></active>
		 *			<application></application>
		 *			<member></member>
		 *			<person></person>
		 *		</polity>
		 *		<belongparty></belongparty>
		 *		<belongmember></belongmember>
		 *		<belongmeet></belongmeet>
		 *	</Param>
		 *
		 */
		try{
			ConstantXml xml = new ConstantXml(this.frameconn,"PARTY_PARAM");
			xml.ifNoParameterInsert("PARTY_PARAM");
			String belongparty = (String)this.getFormHM().get("belongparty");
			belongparty = belongparty!=null&&belongparty.length()>0?belongparty:"";
			String belongmember = (String)this.getFormHM().get("belongmember");
			belongmember = belongmember!=null&&belongmember.length()>0?belongmember:"";
			String belongmeet = (String)this.getFormHM().get("belongmeet");
			belongmeet = belongmeet!=null&&belongmeet.length()>0?belongmeet:"";
			String polity = (String)this.getFormHM().get("polity");
			polity = polity!=null&&polity.length()>0?polity:"";
			String party = (String)this.getFormHM().get("party");
			party = party!=null&&party.length()>0?party:"";
			String preparty = (String)this.getFormHM().get("preparty");
			preparty = preparty!=null&&preparty.length()>0?preparty:"";
			String important = (String)this.getFormHM().get("important");
			important = important!=null&&important.length()>0?important:"";
			String active = (String)this.getFormHM().get("active");
			active = active!=null&&active.length()>0?active:"";
			String application = (String)this.getFormHM().get("application");
			application = application!=null&&application.length()>0?application:"";
			String member = (String)this.getFormHM().get("member");
			member = member!=null&&member.length()>0?member:"";
			String person = (String)this.getFormHM().get("person");
			person = person!=null&&person.length()>0?person:"";
			xml.setValue("belongparty", belongparty);
			xml.setValue("belongmember", belongmember);
			xml.setValue("belongmeet", belongmeet);
			xml.setAttributeValue("/param/polity", "column", polity);
			xml.setAttributeValue("/param/polity/party", "value",party);
			if("".equals(party)){
				xml.setTextValue("/param/polity/party/add", "");
				xml.setTextValue("/param/polity/party/leave", "");
				xml.setTextValue("/param/polity/party/iin", "");
				xml.setTextValue("/param/polity/party/out", "");
			}
			xml.setAttributeValue("/param/polity/preparty", "value",preparty);
			if("".equals(preparty)){
				xml.setTextValue("/param/polity/preparty/add", "");
				xml.setTextValue("/param/polity/preparty/up", "");
				xml.setTextValue("/param/polity/preparty/leave", "");
				xml.setTextValue("/param/polity/preparty/iin", "");
				xml.setTextValue("/param/polity/preparty/out", "");
			}
			xml.setAttributeValue("/param/polity/important", "value",important);
			if("".equals(important)){
				xml.setTextValue("/param/polity/important/add", "");
				xml.setTextValue("/param/polity/important/up", "");
				xml.setTextValue("/param/polity/important/leave", "");
				xml.setTextValue("/param/polity/important/iin", "");
				xml.setTextValue("/param/polity/important/out", "");
			}
			xml.setAttributeValue("/param/polity/active", "value",active);
			if("".equals(active)){
				xml.setTextValue("/param/polity/active/add", "");
				xml.setTextValue("/param/polity/active/up", "");
				xml.setTextValue("/param/polity/active/leave", "");
				xml.setTextValue("/param/polity/active/iin", "");
				xml.setTextValue("/param/polity/active/out", "");
			}
			xml.setAttributeValue("/param/polity/application", "value",application);
			if("".equals(application)){
				xml.setTextValue("/param/polity/application/add", "");
				xml.setTextValue("/param/polity/application/up", "");
				xml.setTextValue("/param/polity/application/leave", "");
				xml.setTextValue("/param/polity/application/iin", "");
				xml.setTextValue("/param/polity/application/out", "");
			}
			xml.setAttributeValue("/param/polity/member", "value",member);
			if("".equals(member)){
				xml.setTextValue("/param/polity/member/add", "");
				xml.setTextValue("/param/polity/member/leave", "");
				xml.setTextValue("/param/polity/member/iin", "");
				xml.setTextValue("/param/polity/member/out", "");
			}
			xml.setAttributeValue("/param/polity/person", "value",person);
			if("".equals(person)){
				xml.setTextValue("/param/polity/person/up", "");
				xml.setTextValue("/param/polity/person/leave", "");
				xml.setTextValue("/param/polity/person/resumeparty", "");
				xml.setTextValue("/param/polity/person/iin", "");
				xml.setTextValue("/param/polity/person/resumemember", "");
			}
			xml.saveStrValue();
		}catch(Exception e){
			
		}
	}

}
