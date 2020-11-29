package com.hjsj.hrms.module.questionnaire.recovery.transaction;

import com.hjsj.hrms.module.questionnaire.template.businessobject.TemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveRecoveryQnSetTrans extends IBusiness{
	@Override
    public void execute() throws GeneralException {
		String questionres = (String)this.getFormHM().get("questionres");
		questionres = questionres.substring(1,questionres.length()-1);
		String [] questionlist = questionres.split(",");
		Map que_map = new HashMap();
		for(int i=0;i<questionlist.length;i++){
			String question = questionlist[i];
			question = question.replaceAll("\"", "");
			if("enddatevalue-inputEl:".equals(question)){
				String enddatevalue_inputEl = question.substring(0,question.length()-1);
				que_map.put(enddatevalue_inputEl,"");
				continue;
			}
			if("autoclosevalue-inputEl:".equals(question)){
				String autoclosevalue_inputEl = question.substring(0,question.length()-1);
				que_map.put(autoclosevalue_inputEl,"");
				continue;
			}
			String [] ques = question.split(":");
			que_map.put(ques[0],ques[1]);
		}
		String qnid = (String)this.getFormHM().get("qnid");
		String qnName = (String)this.getFormHM().get("qnName");
		String oneip = (String)que_map.get("oneip");//每个IP只能搭一次
		String requiredlogin = (String)que_map.get("requiredlogin");//不登录不允许答题
	    String onlyone = (String)que_map.get("onlyone"); //每台电脑或手机只能答一次
	    String enddateselected = (String)que_map.get("enddateselected");//
	    String enddatevalue = (String)que_map.get("enddatevalue-inputEl");//选择的结束日期
	    String searchanswer = (String)que_map.get("searchanswer");//允许答题人提交问卷后可以查看结果
	    String autocloseselected = (String)que_map.get("autocloseselected");//收集多少份问卷后自动结束
	    String autoclosevalue = (String)que_map.get("autoclosevalue-inputEl");//
	    String flagarray = this.getFormHM().get("flagarray").toString();
        String pushids = getpushid(flagarray);
        //每次保存配置信息之前先删除cacheMap对应qnid缓存 changxy  20160825
        TemplateBo bo=new TemplateBo();
        bo.removeCachMap(qnid);
	    StringBuffer strxml = new StringBuffer();
	    Document doc = null;
	    strxml.append("<?xml version='1.0' encoding='GB2312'?>");
        strxml.append("<root>");
        strxml.append("</root>");
        ResultSet  rset = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		try {
			doc = PubFunc.generateDom(strxml.toString());
			List<Map<String,String>> listmap = new ArrayList<Map<String,String>>();
		    Map<String, String> map = new HashMap<String, String>();
		    map.put("oneip", oneip);
		    map.put("requiredlogin", requiredlogin);
		    map.put("onlyone", onlyone);
		    map.put("enddateselected", enddateselected);
		    map.put("enddatevalue", enddatevalue);
		    map.put("searchanswer", searchanswer);
		    map.put("autocloseselected", autocloseselected);
		    map.put("autoclosevalue", autoclosevalue);
		    map.put("pushids", pushids);
		    listmap.add(map);
		    ArrayList nodeList = new ArrayList();
		    for (Map<String, String> m : listmap) {
		        for (String k : m.keySet()) {
		            LazyDynaBean nodeBean=new LazyDynaBean();
					nodeBean.set("name",k);
					nodeBean.set("content",m.get(k));
					nodeList.add(nodeBean);
		        }
		    }
	       	doc = getXmldoc(doc,nodeList);
			StringBuffer strsql=new StringBuffer();
			StringBuffer buf=new StringBuffer();
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(doc));
			//线判断有没有这个qnid
			String sql = "select * from qn_template where qnId ='"+qnid+"'";
			conn = this.getFrameconn();
			pstmt2 = conn.prepareStatement(sql);
			rset = pstmt2.executeQuery();
			if(rset.next()){
				strsql.append("update qn_template set tp_options=? where qnId='"+qnid+"'");
				pstmt = conn.prepareStatement(strsql.toString());
					
				switch(Sql_switcher.searchDbServer()){
					 case Constant.MSSQL:
						  pstmt.setString(1, buf.toString());
						  break;
					 case Constant.ORACEL:
						  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(buf.toString().
						          getBytes())), buf.length());
						  break;
					  case Constant.DB2:
						  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(buf.toString().
						          getBytes())), buf.length());
						  break;
				}
				pstmt.executeUpdate();
			}else{
				String inssql = "insert into qn_template(qnId,qnName,tp_options)values(?,?,?)";
				pstmt1 = conn.prepareStatement(inssql);
				
				switch(Sql_switcher.searchDbServer()){
					 case Constant.MSSQL:
						  pstmt1.setString(1, qnid);
						  pstmt1.setString(2, qnName);
						  pstmt1.setString(3, buf.toString());
						  break;
					 case Constant.ORACEL:
						  pstmt1.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(qnid.
						          getBytes())), qnid.length());
						  pstmt1.setCharacterStream(2,new InputStreamReader(new ByteArrayInputStream(qnName.
						          getBytes())), qnName.length());
						  pstmt1.setCharacterStream(3,new InputStreamReader(new ByteArrayInputStream(buf.toString().
						          getBytes())), buf.length());
						  break;
					  case Constant.DB2:
						  pstmt1.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(qnid.
						          getBytes())), qnid.length());
						  pstmt1.setCharacterStream(2,new InputStreamReader(new ByteArrayInputStream(qnName.
						          getBytes())), qnName.length());
						  pstmt1.setCharacterStream(3,new InputStreamReader(new ByteArrayInputStream(buf.toString().
						          getBytes())), buf.length());
						  break;
				}
				pstmt1.executeUpdate();
				} 
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
		    PubFunc.closeResource(rset);
		    PubFunc.closeResource(pstmt);
		    PubFunc.closeResource(pstmt1);
		    PubFunc.closeResource(pstmt2);
		} 
	}
	/**
	 * 得到新的xml
	 * @param doc
	 * @param nodeList
	 * @return
	 */
	private Document getXmldoc(Document doc,ArrayList nodeList){
		try {
		    String str_path="/root";
			XPath xpath = XPath.newInstance(str_path);
			Element element=(Element)xpath.selectSingleNode(doc);
			Element element_child= null;
			if(element!=null){
				LazyDynaBean nodeBean=(LazyDynaBean)nodeList.get(0);
				String name1=(String)nodeBean.get("name");
				element.removeChildren(name1);
			} 
			else
			{
				String[] arr = str_path.split("/");
				if(arr!=null&&arr.length>0){
					for(int i=1;i<arr.length;i++){
						String path = "";
						for(int j=1;j<=i;j++){
							path+="/"+arr[j];
						}
						xpath=XPath.newInstance(path);
						Element bbElement=(Element)xpath.selectSingleNode(doc);
						if(bbElement==null){
							Element element1= null;
							element1 = new Element(arr[i]);
							element.addContent(element1);
							element=element1;
						}else{
							element = bbElement;
						}
					}
				}
			}
			for(int i=0;i<nodeList.size();i++)
			{
				LazyDynaBean nodeBean=(LazyDynaBean)nodeList.get(i);
				String name=(String)nodeBean.get("name");
				String content=(String)nodeBean.get("content"); 
				 
				element_child = new Element(name); 
				if(content!=null&&content.length()>0)
					element_child.addContent(content);
				element.removeChildren(name);
				element.addContent(element_child);				 
			}
			} catch (JDOMException e) {
				e.printStackTrace();
			}
			return doc;
	}	
	/**
	 * 得到推送人员id
	 * @param flagarray
	 * @return
	 */
	private String getpushid(String flagarray){
		String ids = "";
		flagarray = flagarray.substring(1,flagarray.length()-1);
		if("".equals(flagarray)||flagarray==null){
			ids=flagarray;
		}else{
			flagarray = flagarray.replaceAll("\"", "");
			String [] idarray = flagarray.split(",");
			for(int i=0;i<idarray.length;i++){
				String flagid = idarray[i];
	    		ids+=flagid+",";
	    	}
	    	ids=ids.substring(0,ids.length()-1);
		}
    	return ids;
	}
}
