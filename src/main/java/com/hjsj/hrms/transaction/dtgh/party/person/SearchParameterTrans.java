package com.hjsj.hrms.transaction.dtgh.party.person;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 
 * @author xujian
 *Feb 4, 2010
 */
public class SearchParameterTrans extends IBusiness {


	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		/**
		 * 
		 * <?xml version="1.0" encoding="GB2312"?>
		 *	<Param>
		 *		<polity column='A01.A0101'>
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
		ArrayList belongpartylist = new ArrayList();
		belongpartylist.add(new CommonData("",ResourceFactory.getProperty("label.select.dot")));
		ArrayList belongmemberlist = new ArrayList();
		belongmemberlist.add(new CommonData("",ResourceFactory.getProperty("label.select.dot")));
		ArrayList belongmeetlist = new ArrayList();
		belongmeetlist.add(new CommonData("",ResourceFactory.getProperty("label.select.dot")));
		String belongparty="";
		String belongmember="";
		String belongmeet="";
		String polity = "";
		String polityview = "";
		String party = "";
		String preparty="";
		String important="";
		String active="";
		String application="";
		String member="";
		String person="";
		String personview = "";
		String codesetid = "";
		ArrayList politylist = new ArrayList();
		politylist.add(new CommonData("",ResourceFactory.getProperty("label.select.dot")));
		try{
			ConstantXml xml = new ConstantXml(this.frameconn,"PARTY_PARAM");
			ArrayList fieldlist = DataDictionary.getFieldList("A01", Constant.USED_FIELD_SET);
			for(int i=0;i<fieldlist.size();i++){
				FieldItem fielditem = (FieldItem)fieldlist.get(i);
				if("64".equalsIgnoreCase(fielditem.getCodesetid())){
					CommonData cd = new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
					belongpartylist.add(cd);
				}else if("65".equalsIgnoreCase(fielditem.getCodesetid())){
					CommonData cd = new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
					belongmemberlist.add(cd);
				}else if("66".equalsIgnoreCase(fielditem.getCodesetid())){
					CommonData cd = new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
					belongmeetlist.add(cd);
				}
			}
			belongparty = xml.getValue("belongparty");
			belongparty = belongparty!=null&&belongparty.length()>0?belongparty:"";
			belongmember = xml.getValue("belongmember");
			belongmember = belongmember!=null&&belongmember.length()>0?belongmember:"";
			belongmeet = xml.getValue("belongmeet");
			belongmeet = belongmeet!=null&&belongmeet.length()>0?belongmeet:"";
			polity = xml.getNodeAttributeValue("/param/polity", "column");////政治面貌指标    存放 人员指标中的代码型指标  子集名称.子集指标
			polity = polity!=null&&polity.length()>0?polity:"";
			if(polity.length()>0){
				String []str = polity.split("\\.");
				if(str.length==2){
					FieldSet set = DataDictionary.getFieldSetVo(str[0]);//党费参数配置的子集不存在时,直接结束     bug 36109  wangb
					if(set == null)
						return;
					FieldItem item = DataDictionary.getFieldItem(str[1], str[0]);
					if(item!=null){
						polityview = item.getItemdesc();
						codesetid = item.getCodesetid();
					}
					String sql = "select codeitemid,codeitemdesc from codeitem where codesetid='"+codesetid+"'";
					ContentDAO dao = new ContentDAO(this.frameconn);
					this.frecset = dao.search(sql);
					while(this.frecset.next()){
						CommonData cd = new CommonData(this.frecset.getString("codeitemid"),this.getFrecset().getString("codeitemdesc"));
						politylist.add(cd);
					}
					party = xml.getNodeAttributeValue("/param/polity/party","value");//党员标识
					preparty=xml.getNodeAttributeValue("/param/polity/preparty","value");
					important=xml.getNodeAttributeValue("/param/polity/important","value");
					active=xml.getNodeAttributeValue("/param/polity/active","value");//入党积极分子标识
					application=xml.getNodeAttributeValue("/param/polity/application","value");//申请入党标识
					member=xml.getNodeAttributeValue("/param/polity/member","value");//团员标识
					person=xml.getNodeAttributeValue("/param/polity/person","value");//群众标识
					if(person.length()>0){
						String [] temp = person.split(",");
						for(int i=0;i<temp.length;i++){
							if(temp[i].length()>0)
							for(int n=0;n<politylist.size();n++){
								CommonData c = (CommonData)politylist.get(n);
								if(temp[i].equals(c.getDataValue())){
									personview+=c.getDataName()+",";
								}
							}
						}
					}
					if(personview.length()>1){
						personview = personview.substring(0, personview.length());
					}
				}
			}
		}catch(Exception e){
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("belongpartylist", belongpartylist);
			this.getFormHM().put("belongmemberlist", belongmemberlist);
			this.getFormHM().put("belongmeetlist", belongmeetlist);
			this.getFormHM().put("belongparty", belongparty);
			this.getFormHM().put("belongmember", belongmember);
			this.getFormHM().put("belongmeet", belongmeet);
			this.getFormHM().put("politylist", politylist);
			this.getFormHM().put("polity", polity);
			this.getFormHM().put("party", party);
			this.getFormHM().put("preparty", preparty);
			this.getFormHM().put("important", important);
			this.getFormHM().put("active", active);
			this.getFormHM().put("application", application);
			this.getFormHM().put("member", member);
			this.getFormHM().put("person", person);
			this.getFormHM().put("polityview", polityview);
			this.getFormHM().put("personview", personview);
			this.getFormHM().put("codesetid", codesetid);
			
		}
	}

}
