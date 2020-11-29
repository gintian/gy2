package com.hjsj.hrms.module.jobtitle.configfile.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;

public class DomXml {

	private HashMap templatesMap=null;//所有配置模板的参数，用于读取各类模板
	private Boolean college_eval = null; //是否支持二级单位
	private Boolean support_checking = null;//是否支持材料审核
	private Boolean support_word = null;// 公示、投票环节显示申报材料表单上传的word模板内容
	private Boolean isshowrefresh = null;//是否支持分批次投票免重复登陆
	private HashMap vote_type=null;
	private Boolean show_Validatecode = null;
	
	public HashMap getVote_type() {
		return vote_type;
	}


	public void setVote_type(HashMap vote_type) {
		this.vote_type = vote_type;
	}
	
	public Boolean getShow_Validatecode() {
		return show_Validatecode;
	}


	public void setShow_Validatecode(Boolean show_Validatecode) {
		this.show_Validatecode = show_Validatecode;
	}

	public Boolean getCollege_eval() {
		return college_eval;
	}


	public void setCollege_eval(Boolean college_eval) {
		this.college_eval = college_eval;
	}


	public Boolean getSupport_checking() {
		return support_checking;
	}


	public void setSupport_checking(Boolean support_checking) {
		this.support_checking = support_checking;
	}
	
	public Boolean getSupport_word() {
		return support_word;
	}
	
	
	public void setSupport_word(Boolean support_word) {
		this.support_word = support_word;
	}

	public Boolean getIsshowrefresh() {
		return isshowrefresh;
	}


	public void setIsshowrefresh(Boolean isshowrefresh) {
		this.isshowrefresh = isshowrefresh;
	}


	/**
	 * 
	 * @param xmlDoc
	 * @param flagid
	 * @param mid
	 * @param xmlDocMid
	 * @param flagmap
	 * @param college_eval	是否支持学院聘任组评审
	 * @param support_checking	支持审核
	 * @return
	 */
    public List parse(String xmlDoc,String flagid,String mid,String xmlDocMid,String flagmap) {
        //创建一个新的字符串
        String upxml="";
        List list=new ArrayList();
        Map map=new HashMap();
        try {
            //通过输入源构造一个Document
            Document doc = PubFunc.generateDom(xmlDoc);
            //取的根元素
            Element root = doc.getRootElement();
            //子元素对象
            Element et = null;
            //haosl 20160728 添加college_eval节点
            if(college_eval != null){//支持二级单位
            	Element collegeEl = root.getChild("college_eval");
            	if(collegeEl == null){
            		collegeEl = new Element("college_eval");//增加子节点
            		root.addContent(collegeEl);
            	}
            	collegeEl.setText(String.valueOf(college_eval));
            } else if(support_checking != null){//支持材料审核
            	Element collegeEl = root.getChild("support_checking");
            	if(collegeEl == null){
            		collegeEl = new Element("support_checking");//增加子节点
            		root.addContent(collegeEl);
            	}
            	collegeEl.setText(String.valueOf(support_checking));
            }  else if(support_word != null){//公示、投票环节显示申报材料表单上传的word模板内容
            	Element collegeEl = root.getChild("support_word");
            	if(collegeEl == null){
            		collegeEl = new Element("support_word");//增加子节点
            		root.addContent(collegeEl);
            	}
            	collegeEl.setText(String.valueOf(support_word));
            } else if(isshowrefresh !=null){//分批次投票面重复登录
            	Element isshowrefreshEl = root.getChild("isshowrefresh");
            	if(isshowrefreshEl == null){
            		isshowrefreshEl = new Element("isshowrefresh");//增加子节点
            		root.addContent(isshowrefreshEl);
            	}
            	isshowrefreshEl.setText(String.valueOf(isshowrefresh));
            }else if(vote_type !=null&&!vote_type.isEmpty()){//xus 18/4/8 投票参数设置保存
            	Element votetypeEl = root.getChild("vote_type");
            	if(votetypeEl == null){
            		votetypeEl = new Element("vote_type");//增加子节点
            		root.addContent(votetypeEl);
            	}
            	String voteType=(String)vote_type.get("type")==null?"1":(String)vote_type.get("type");
            	String voteColumns=(String)vote_type.get("type")==null?"":(String)vote_type.get("columns");
            	votetypeEl.setAttribute("type", voteType);
            	if(!"false".equals(voteColumns))
            		votetypeEl.setAttribute("columns", voteColumns);
            }if(show_Validatecode != null){//支持二级单位
            	Element collegeEl = root.getChild("show_Validatecode");
            	if(collegeEl == null){
            		collegeEl = new Element("show_Validatecode");//增加子节点
            		root.addContent(collegeEl);
            	}
            	collegeEl.setText(String.valueOf(show_Validatecode));
            }else{
                    et = (Element) root.getChildren().get(0);//得到templates节点
                    List subNode = et.getChildren();  
    	            Element subEt = null;
    	            boolean sign=true;//为true，创建子节点
    	            for (int j = 0; j < subNode.size(); j++) {   
    	                subEt = (Element) subNode.get(j); //循环依次得到子节点
    	                //修改存储
    	                if("savemap".equals(flagmap)){
    	                	if(subEt.getAttributeValue("type").equals(flagid)){
    	                			subEt.setAttribute("template_id", mid);
    	                			sign=false;
    	                	}
    	                }
    	                //查询
    	                else if("initmap".equals(flagmap)){//根据标识判断，此为初始化业务模版
    	                	map.put(subEt.getAttributeValue("type"), subEt.getAttributeValue("template_id"));
    	                }
    	            }
    	          //创建存储，不能在循环里创建子节点
    	            if(sign&& "savemap".equals(flagmap)){//查询数据库后，没有该节点才会创建
    	            	Element newClass = new Element("template");//增加子节点template
    	            	newClass.setAttribute("type", flagid); 
    	            	newClass.setAttribute("template_id", mid);
    	            	et.addContent(newClass); 
    	            }
    	            Element subEt1 = null;
    	            Element subEt2 = null;
    	            for(int m=0;m<subNode.size();m++){//最后对新的xml子元素进行排序，按照type属性的值
    	            	for(int n=m+1;n<subNode.size();n++){
    	            		subEt1 = (Element) subNode.get(m); //循环依次得到子节点
    	            		subEt2 = (Element) subNode.get(n); //循环依次得到子节点
    	            		if(Integer.parseInt(subEt1.getAttributeValue("type").toString())>Integer.parseInt(subEt2.getAttributeValue("type").toString())){
    	            			String type1=subEt1.getAttributeValue("type");
    	            			String type2=subEt2.getAttributeValue("type");
    	            			String template_id1=subEt1.getAttributeValue("template_id");
    	            			String template_id2=subEt2.getAttributeValue("template_id");
    	            			subEt1.setAttribute("type", type2);
    	            			subEt1.setAttribute("template_id", template_id2);
    	            			subEt2.setAttribute("type", type1);
    	            			subEt2.setAttribute("template_id", template_id1);
    	            		}
    	            	}
    	            }
    	            
//    	            //xus 18/4/8 
//    	            Object children=root.getChildren();
//    	            for(Object obj:root.getChildren()){
//    	            	Element el=(Element)obj;
//    	            	if("vote_type".equals(el.getName())){
//    	            		String voteType=el.getAttributeValue("type")==null?"":el.getAttributeValue("type");
//    	            		String voteColumns=el.getAttributeValue("columns")==null?"":el.getAttributeValue("columns");
//    	            		map.put("voteType", voteType);
//    	            		map.put("voteColumns", voteColumns);
//    	            	}
//    	            }
                }
            //设置xml字体编码，然后输出为字符串
            Format format=Format.getRawFormat();
            	format.setEncoding("UTF-8");
            XMLOutputter output=new XMLOutputter(format);
            	upxml=output.outputString(doc);//最终处理后xml
            list.add(upxml);//要存储的xml
            list.add(map);//要查询的xml元素信息
            
        } catch (JDOMException e) {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        } catch (IOException e) {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return list;
    }
    
    
    /** 
    * @Title: getJobtitleTemplates 
    * @Description: 获取所有职称评审所有模板配置参数
    * @param @param conn
    * @param @return
    * @return HashMap
    */ 
    public HashMap getJobtitleTemplates(Connection conn){    	
    	HashMap map=new HashMap();
    	ContentDAO dao=new ContentDAO(conn);
		try {
			RowSet rs = dao.search("select Str_Value from constant where Constant=?",
					Arrays.asList(new String[]{"JOBTITLE_CONFIG"}));
			String xmlDoc="";
			if(rs.next()){
				xmlDoc=rs.getString("Str_Value");
			} 
			if (xmlDoc!=null && xmlDoc.length()>0){
			   	//创建一个新的字符串
		        List list=new ArrayList();
		        try {
		            //通过输入源构造一个Document
		            Document doc = PubFunc.generateDom(xmlDoc);
		            //取的根元素
		            Element root = doc.getRootElement();
		            //得到根元素所有子元素的集合
		            List jiedian = root.getChildren();
		            //子元素对象
		            Element et = null;
		            for(int i=0;i<jiedian.size();i++){
		                et = (Element) jiedian.get(i);//循环依次得到子元素
		                List subNode = et.getChildren();  
			            Element subEt = null;
			            boolean sign=true;//为true，创建子节点
			            for (int j = 0; j < subNode.size(); j++) {   
			                subEt = (Element) subNode.get(j); //循环依次得到子节点		       
			                map.put(subEt.getAttributeValue("type"), subEt.getAttributeValue("template_id"));
			            }
		            }
		            
		        } catch (JDOMException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			}
	
	   } catch (Exception e) {
            e.printStackTrace();
       }	
	   return map;
	}
    
    /** 
    * @Title: getJobtitleTemplateByType 
    * @Description: 根据职称评审类型，获取配置的模板号
    * @param @param conn
    * @param @param type  1:职称认定 2：考试认定   3:免试备案   4:破格认定   5：论文送审 6：材料送审
    * @param @return
    * @return HashMap
    */ 
    public String getJobtitleTemplateByType(Connection conn,String type){   
    	String templateId="";
		try {
			if (templatesMap==null ) {
				templatesMap= getJobtitleTemplates(conn);
			}
			Iterator it = templatesMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				Object key = entry.getKey();
				if (type.equals((String)key)){
					templateId = (String)entry.getValue();
					break;
				}
			}
	   } catch (Exception e) {
            e.printStackTrace();
       }	
	   return templateId;
	}
	
}