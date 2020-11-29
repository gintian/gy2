package com.hjsj.hrms.transaction.dtgh.party.person;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SearchBusParameterTrans extends IBusiness {


	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		
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
		 *
		 *	</Param>
		 *
		 */
		
		String param = (String)this.getFormHM().get("param");
		String add = "";
		String addview = "";
		String up = "";
		String upview="";
		String leave = "";
		String leaveview = "";
		String iin = "";
		String iinview = "";
		String out = "";
		String outview = "";
		String resumeparty = "";
		String resumepartyview = "";
		String resumemember = "";
		String resumememberview = "";
		try{
			ConstantXml xml = new ConstantXml(this.frameconn,"PARTY_PARAM");
			ContentDAO dao = new ContentDAO(this.frameconn);
			String sql = "select tabid,name from template_table where tabid in (";
			if("party".equals(param)){
				StringBuffer temp = new StringBuffer();
				add = xml.getTextValue("/param/polity/party/add");
				leave = xml.getTextValue("/param/polity/party/leave");
				iin = xml.getTextValue("/param/polity/party/iin");
				out = xml.getTextValue("/param/polity/party/out");
				temp.append(add!=null&&add.length()>0?add+",":"");
				temp.append(leave!=null&&leave.length()>0?leave+",":"");
				temp.append(iin!=null&&iin.length()>0?iin+",":"");
				temp.append(out!=null&&out.length()>0?out+",":"");
				if(temp.length()>0){
					sql +=temp.substring(0,temp.length()-1)+")";
					this.frecset = dao.search(sql);
					HashMap map = new HashMap();
					while(this.frecset.next()){
						map.put(this.frecset.getInt("tabid")+"key", this.frecset.getInt("tabid")+":"+this.frecset.getString("name"));
					}
					//String a = (String)map.get(add+"key");
					addview = map.get(add+"key")!=null?(String)map.get(add+"key"):"";
					leaveview = map.get(leave+"key")!=null?(String)map.get(leave+"key"):"";
					iinview = map.get(iin+"key")!=null?(String)map.get(iin+"key"):"";
					outview = map.get(out+"key")!=null?(String)map.get(out+"key"):"";
				}
			}else if("preparty".equals(param)){
				StringBuffer temp = new StringBuffer();
				add = xml.getTextValue("/param/polity/preparty/add");
				up = xml.getTextValue("/param/polity/preparty/up");
				leave = xml.getTextValue("/param/polity/preparty/leave");
				iin = xml.getTextValue("/param/polity/preparty/iin");
				out = xml.getTextValue("/param/polity/preparty/out");
				temp.append(add!=null&&add.length()>0?add+",":"");
				temp.append(up!=null&&up.length()>0?up+",":"");
				temp.append(leave!=null&&leave.length()>0?leave+",":"");
				temp.append(iin!=null&&iin.length()>0?iin+",":"");
				temp.append(out!=null&&out.length()>0?out+",":"");
				if(temp.length()>0){
					sql +=temp.substring(0,temp.length()-1)+")";
					this.frecset = dao.search(sql);
					HashMap map = new HashMap();
					while(this.frecset.next()){
						map.put(this.frecset.getInt("tabid")+"key", this.frecset.getInt("tabid")+":"+this.frecset.getString("name"));
					}
					//String a = (String)map.get(add+"key");
					addview = map.get(add+"key")!=null?(String)map.get(add+"key"):"";
					upview = map.get(up+"key")!=null?(String)map.get(up+"key"):"";
					leaveview = map.get(leave+"key")!=null?(String)map.get(leave+"key"):"";
					iinview = map.get(iin+"key")!=null?(String)map.get(iin+"key"):"";
					outview = map.get(out+"key")!=null?(String)map.get(out+"key"):"";
				}
			}else if("important".equals(param)){
				StringBuffer temp = new StringBuffer();
				add = xml.getTextValue("/param/polity/important/add");
				up = xml.getTextValue("/param/polity/important/up");
				leave = xml.getTextValue("/param/polity/important/leave");
				iin = xml.getTextValue("/param/polity/important/iin");
				out = xml.getTextValue("/param/polity/important/out");
				temp.append(add!=null&&add.length()>0?add+",":"");
				temp.append(up!=null&&up.length()>0?up+",":"");
				temp.append(leave!=null&&leave.length()>0?leave+",":"");
				temp.append(iin!=null&&iin.length()>0?iin+",":"");
				temp.append(out!=null&&out.length()>0?out+",":"");
				if(temp.length()>0){
					sql +=temp.substring(0,temp.length()-1)+")";
					this.frecset = dao.search(sql);
					HashMap map = new HashMap();
					while(this.frecset.next()){
						map.put(this.frecset.getInt("tabid")+"key", this.frecset.getInt("tabid")+":"+this.frecset.getString("name"));
					}
					//String a = (String)map.get(add+"key");
					addview = map.get(add+"key")!=null?(String)map.get(add+"key"):"";
					upview = map.get(up+"key")!=null?(String)map.get(up+"key"):"";
					leaveview = map.get(leave+"key")!=null?(String)map.get(leave+"key"):"";
					iinview = map.get(iin+"key")!=null?(String)map.get(iin+"key"):"";
					outview = map.get(out+"key")!=null?(String)map.get(out+"key"):"";
				}
			}else if("active".equals(param)){
				StringBuffer temp = new StringBuffer();
				add = xml.getTextValue("/param/polity/active/add");
				up = xml.getTextValue("/param/polity/active/up");
				leave = xml.getTextValue("/param/polity/active/leave");
				iin = xml.getTextValue("/param/polity/active/iin");
				out = xml.getTextValue("/param/polity/active/out");
				temp.append(add!=null&&add.length()>0?add+",":"");
				temp.append(up!=null&&up.length()>0?up+",":"");
				temp.append(leave!=null&&leave.length()>0?leave+",":"");
				temp.append(iin!=null&&iin.length()>0?iin+",":"");
				temp.append(out!=null&&out.length()>0?out+",":"");
				if(temp.length()>0){
					sql +=temp.substring(0,temp.length()-1)+")";
					this.frecset = dao.search(sql);
					HashMap map = new HashMap();
					while(this.frecset.next()){
						map.put(this.frecset.getInt("tabid")+"key", this.frecset.getInt("tabid")+":"+this.frecset.getString("name"));
					}
					//String a = (String)map.get(add+"key");
					addview = map.get(add+"key")!=null?(String)map.get(add+"key"):"";
					upview = map.get(up+"key")!=null?(String)map.get(up+"key"):"";
					leaveview = map.get(leave+"key")!=null?(String)map.get(leave+"key"):"";
					iinview = map.get(iin+"key")!=null?(String)map.get(iin+"key"):"";
					outview = map.get(out+"key")!=null?(String)map.get(out+"key"):"";
				}
			}else if("application".equals(param)){
				StringBuffer temp = new StringBuffer();
				add = xml.getTextValue("/param/polity/application/add");
				up = xml.getTextValue("/param/polity/application/up");
				leave = xml.getTextValue("/param/polity/application/leave");
				iin = xml.getTextValue("/param/polity/application/iin");
				out = xml.getTextValue("/param/polity/application/out");
				temp.append(add!=null&&add.length()>0?add+",":"");
				temp.append(up!=null&&up.length()>0?up+",":"");
				temp.append(leave!=null&&leave.length()>0?leave+",":"");
				temp.append(iin!=null&&iin.length()>0?iin+",":"");
				temp.append(out!=null&&out.length()>0?out+",":"");
				if(temp.length()>0){
					sql +=temp.substring(0,temp.length()-1)+")";
					this.frecset = dao.search(sql);
					HashMap map = new HashMap();
					while(this.frecset.next()){
						map.put(this.frecset.getInt("tabid")+"key", this.frecset.getInt("tabid")+":"+this.frecset.getString("name"));
					}
					//String a = (String)map.get(add+"key");
					addview = map.get(add+"key")!=null?(String)map.get(add+"key"):"";
					upview = map.get(up+"key")!=null?(String)map.get(up+"key"):"";
					leaveview = map.get(leave+"key")!=null?(String)map.get(leave+"key"):"";
					iinview = map.get(iin+"key")!=null?(String)map.get(iin+"key"):"";
					outview = map.get(out+"key")!=null?(String)map.get(out+"key"):"";
				}
			}else if("member".equals(param)){
				StringBuffer temp = new StringBuffer();
				add = xml.getTextValue("/param/polity/member/add");
				leave = xml.getTextValue("/param/polity/member/leave");
				iin = xml.getTextValue("/param/polity/member/iin");
				out = xml.getTextValue("/param/polity/member/out");
				temp.append(add!=null&&add.length()>0?add+",":"");
				temp.append(leave!=null&&leave.length()>0?leave+",":"");
				temp.append(iin!=null&&iin.length()>0?iin+",":"");
				temp.append(out!=null&&out.length()>0?out+",":"");
				if(temp.length()>0){
					sql +=temp.substring(0,temp.length()-1)+")";
					this.frecset = dao.search(sql);
					HashMap map = new HashMap();
					while(this.frecset.next()){
						map.put(this.frecset.getInt("tabid")+"key", this.frecset.getInt("tabid")+":"+this.frecset.getString("name"));
					}
					//String a = (String)map.get(add+"key");
					addview = map.get(add+"key")!=null?(String)map.get(add+"key"):"";
					leaveview = map.get(leave+"key")!=null?(String)map.get(leave+"key"):"";
					iinview = map.get(iin+"key")!=null?(String)map.get(iin+"key"):"";
					outview = map.get(out+"key")!=null?(String)map.get(out+"key"):"";
				}
			}else if("person".equals(param)){
				StringBuffer temp = new StringBuffer();
				up = xml.getTextValue("/param/polity/person/up");
				leave = xml.getTextValue("/param/polity/person/leave");
				resumeparty = xml.getTextValue("/param/polity/person/resumeparty");
				iin = xml.getTextValue("/param/polity/person/iin");
				resumemember = xml.getTextValue("/param/polity/person/resumemember");
				temp.append(resumeparty!=null&&resumeparty.length()>0?resumeparty+",":"");
				temp.append(up!=null&&up.length()>0?up+",":"");
				temp.append(leave!=null&&leave.length()>0?leave+",":"");
				temp.append(iin!=null&&iin.length()>0?iin+",":"");
				temp.append(resumemember!=null&&resumemember.length()>0?resumemember+",":"");
				if(temp.length()>0){
					sql +=temp.substring(0,temp.length()-1)+")";
					this.frecset = dao.search(sql);
					HashMap map = new HashMap();
					while(this.frecset.next()){
						map.put(this.frecset.getInt("tabid")+"key", this.frecset.getInt("tabid")+":"+this.frecset.getString("name"));
					}
					//String a = (String)map.get(add+"key");
					resumepartyview = map.get(resumeparty+"key")!=null?(String)map.get(resumeparty+"key"):"";
					upview = map.get(up+"key")!=null?(String)map.get(up+"key"):"";
					leaveview = map.get(leave+"key")!=null?(String)map.get(leave+"key"):"";
					iinview = map.get(iin+"key")!=null?(String)map.get(iin+"key"):"";
					resumememberview = map.get(resumemember+"key")!=null?(String)map.get(resumemember+"key"):"";
				}
			}
		}catch(Exception e){
			GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("add", add);
			this.getFormHM().put("addview", addview);
			this.getFormHM().put("up", up);
			this.getFormHM().put("upview", upview);
			this.getFormHM().put("leave", leave);
			this.getFormHM().put("leaveview", leaveview);
			this.getFormHM().put("iin", iin);
			this.getFormHM().put("iinview", iinview);
			this.getFormHM().put("out", out);
			this.getFormHM().put("outview", outview);
			this.getFormHM().put("resumeparty", resumeparty);
			this.getFormHM().put("resumepartyview", resumepartyview);
			this.getFormHM().put("resumemember", resumemember);
			this.getFormHM().put("resumememberview", resumememberview);
		}
		
	}

}
