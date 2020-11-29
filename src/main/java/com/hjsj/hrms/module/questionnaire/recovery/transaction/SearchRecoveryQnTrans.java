package com.hjsj.hrms.module.questionnaire.recovery.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

public class SearchRecoveryQnTrans extends IBusiness{
	
	@Override
    public void execute() throws GeneralException {

		String planid = (String)this.getFormHM().get("planid");
		int qnid = Integer.parseInt((String)this.getFormHM().get("qnid"));
		HashMap<String, Object> map = getTemplateInfo(qnid,this.getFrameconn());
		Object qnset = map.get("qnset");
		this.getFormHM().put("qnset",qnset);
		try {
			String EnyPassword = PubFunc.encrypt(planid);
			this.getFormHM().put("planid",EnyPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}  
		
	}
	/**
	 * 获得问卷配置参数
	 * @param qnid
	 * @param conn
	 * @return
	 */
	private HashMap<String, Object> getTemplateInfo(int qnid,Connection conn){
		HashMap<String, Object> map = new HashMap<String, Object>();
		RowSet rs = null;
		try{
			String sql = "select tp_options from qn_template where qnId = '"+qnid+"'";
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			while(rs.next()){
				HashMap<String, String> qnset = new HashMap<String, String>();
				String xml = rs.getString("tp_options");
				if(xml==null|| "".equals(xml)){
					map.put("qnset", qnset);
				}else{
					qnset = parseXml(xml);
					map.put("qnset", qnset);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return map;
	}
	/**
	 * 解析xml
	 * @param xml
	 * @return
	 */
	private HashMap<String, String> parseXml(String xml){
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			Document doc = PubFunc.generateDom(xml);
			Element ele = doc.getRootElement();
			String pushnamearr ="";
			@SuppressWarnings("unchecked")
			List<Element> list = ele.getChildren();
			for (int i = 0; i < list.size(); i++) {
				Element e = list.get(i);
				if("pushids".equals(e.getName())){
					String pushids = e.getText();
					String[] pushidarr = pushids.split(",");//UM010101单位,PEUsr00000028人员,PEUsr00000035,ROLER00000002角色
					for(int j=0;j<pushidarr.length;j++){
						String pushid = pushidarr[j];
						String pushflag="";
						if(pushid.startsWith("UN")||pushid.startsWith("UM")){
							pushflag=pushid.substring(0,2);
							pushid = pushid.substring(2,pushid.length());
							String sql = "select codeitemdesc from organization where codeitemid='"+pushid+"'";
							this.frowset = dao.search(sql);
							if(this.frowset.next()){
								String pushname = this.frowset.getString("codeitemdesc");
								if(pushname==null){
									pushname="";
								}
								pushnamearr += pushflag+":"+pushid+":"+pushname+",";
							}
						}
						if(pushid.startsWith("ROLE")){
							pushflag="ROLE";
							pushid = pushid.substring(5,pushid.length());
							String sql = "select role_name from t_sys_role where role_id='"+pushid+"'";
							this.frowset = dao.search(sql);
							if(this.frowset.next()){
								String pushname = this.frowset.getString("role_name");
								if(pushname==null){
									pushname="";
								}
								pushnamearr += pushflag+":"+"R"+pushid+":"+pushname+",";
							}
						}
						if(pushid.startsWith("PE")){
							pushflag=pushid.substring(0,2);
							pushid = pushid.substring(2,pushid.length());
							String nbaseString = pushid.substring(0,3);
							String a0100 = pushid.substring(3,pushid.length());
							String sql = "select a0101 from "+nbaseString+"A01 where a0100='"+a0100+"'";
							this.frowset = dao.search(sql);
							if(this.frowset.next()){
								String pushname = this.frowset.getString("a0101");
								if(pushname==null){
									pushname="";
								}
								pushnamearr += pushflag+":"+nbaseString+":"+a0100+":"+pushname+",";
							}
						}
						
					}
					if(!"".equals(pushnamearr)){
						pushnamearr=pushnamearr.substring(0,pushnamearr.length()-1);
					}
					map.put("pushids", pushnamearr);
				}else{
					map.put(e.getName(), e.getText());
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

}
