package com.hjsj.hrms.transaction.sys.sms.weixin;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SaveWXEnterpriseAppCustomMenuTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		Integer wxitemid = Integer.decode((String)this.formHM.get("serverid"));
		HashMap menuDataHM = PubFunc.DynaBean2Map((MorphDynaBean)this.formHM.get("menuData"));
		String servertype = (String) menuDataHM.get("servertype");
		ArrayList paramsList = (ArrayList) menuDataHM.get("params");
		
		String sql = "select wxname,appid,app_secret,url,app_type,description,str_value from t_sys_weixin_param where wxsetid=? and wxitemid=?";
		ArrayList list = new ArrayList();
		list.add(servertype);
		list.add(wxitemid);
		ContentDAO dao = new ContentDAO(this.frameconn);
		String str_value = "";
		Document doc = null;
		RecordVo vo = new RecordVo("t_sys_weixin_param");
		try {
			this.frowset = dao.search(sql,list);
			if(this.frowset.next()){
				vo.setString("wxsetid", servertype);//企业号标识
				vo.setInt("wxitemid", wxitemid);
				vo.setString("wxname", this.frowset.getString("wxname"));
				vo.setString("appid", this.frowset.getString("appid"));
				vo.setString("app_secret",this.frowset.getString("app_secret"));
				vo.setString("url", this.frowset.getString("url"));
				vo.setString("app_type", this.frowset.getString("app_type"));
				vo.setString("description", this.frowset.getString("description"));
				str_value = this.frowset.getString("str_value");
			}
			
			if(str_value == null || str_value.trim().length() == 0){
				doc = new Document();
				doc.addContent(new Element("params"));
			}else{
				doc = PubFunc.generateDom(str_value);
				Element root = doc.getRootElement();
				List menuList = root.getChildren("menu");
				if(menuList !=null || menuList.size() > 0)
					root.removeChildren("menu");
			}
			
			for(int i =0 ; i < paramsList.size() ; i++){
				if(paramsList.get(i) == null)
					continue;
				HashMap param = PubFunc.DynaBean2Map((MorphDynaBean)paramsList.get(i));
				String menuname = (String) param.get("menuname");
				String menuurl = (String) param.get("menuurl");
				String order = (String) param.get("firstorder");
				Element child = new Element("menu");
				child.setAttribute("menuname", menuname);
				child.setAttribute("menuurl", menuurl);
				child.setAttribute("order", order);
				ArrayList funcList = param.get("functions")==null? new ArrayList():(ArrayList)param.get("functions");
				for(int j =0 ; j < funcList.size() ; j++){
					HashMap func = PubFunc.DynaBean2Map((MorphDynaBean)funcList.get(j));
					String functionmenu = (String) func.get("functionmenu");
					String functionname = (String) func.get("functionname");
					String functionurl = (String) func.get("functionurl");
					String functionorder = (String) func.get("secondorder");
					Element funcChild = new Element("function");
					funcChild.setAttribute("functionmenu", functionmenu);
					funcChild.setAttribute("functionname", functionname);
					funcChild.setAttribute("functionurl", functionurl);
					funcChild.setAttribute("order", functionorder);
					child.addContent(funcChild);
				}
				Element root = doc.getRootElement();
				root.addContent(child);
			}
			StringBuffer xmls = new StringBuffer();
		    XMLOutputter outputter = new XMLOutputter();
		    Format format=Format.getPrettyFormat();
   	     	format.setEncoding("UTF-8");
   	     	outputter.setFormat(format);
   	        xmls.setLength(0);
   	        xmls.append(outputter.outputString(doc));
   	        sql = "update t_sys_weixin_param set str_value=? where wxsetid=? and wxitemid=?";
   	        list.clear();
   	        list.add(xmls.toString());
   	        list.add(servertype);
   	        list.add(wxitemid);
   	        vo.setString("str_value",xmls.toString());
   	        int i = dao.update(sql,list);
			if(i > 0){
				this.formHM.put("result","1");
				String release = (String) this.formHM.get("release");
				if(release != null || "release".equalsIgnoreCase(release)) // 发布标识
					this.formHM.put("releaseInfo", vo);
			}else{
				this.formHM.put("result", "2");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
