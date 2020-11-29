/**
 * 
 */
package com.hjsj.hrms.taglib.train;

import com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class ExamQuestionsOptionsTag extends TagSupport {
	private String type_id;//试题类型
	private String xml;//试题内容xml
	private String name_id;//选项标签名字  将来取值用
	private String style="";//样式
	private String answer;//答案
	//flag=1 预览试卷 flag=2 考试 flag=3评卷   flag=4考试结果查看  flag=5自测考试
	private String flag;// 业务模块标志
	private String disabled = "false";// true为不可用，默认为false即可用
	public String getDisabled() {
		return disabled;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public int doStartTag() throws JspException {
		
		try {
			if("1".equals(type_id)||"2".equals(type_id)){
				pageContext.getOut().println(getSelectHtml());
			}else if("3".equals(type_id)){
				pageContext.getOut().println(getJudgeHtml());
			}else
				pageContext.getOut().println("");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.doStartTag();
	}
	
	private String getSelectHtml(){
		StringBuffer buffer = new StringBuffer();
		QuestionesBo bo = new QuestionesBo();
		String tmpString = bo.getStrSelection(QuestionesBo.toHtml(xml)); 
		String type="radio";
		if("2".equals(type_id))
			type="checkbox";
		buffer.append("<ul style=\"border: 0px;padding: 0px;margin: 0px;"+style+"\">");
		String tmps[] = tmpString.split("`~&~`");
		for(int i=0;i<tmps.length;i++){
			if(tmps[i]!=null&&tmps[i].length()>0){
				String tmp[] = tmps[i].split("`:`");
			    if(tmp!=null&&tmp.length==2){
			    	String checked = "";
			    	if(answer!=null&&answer.length()>0)
			    		checked = answer.indexOf(tmp[0])!=-1?"checked":"";
			    	buffer.append("<li><input type=\""+type+"\" name=\""+name_id+"\" id=\""+name_id+"_"+tmp[0]+"\" value=\""+tmp[0]+"\"");
			    	if ("5".equals(flag) || "2".equals(flag)) {
			    		buffer.append(" onclick=\"collectAnswer('"+name_id+"_"+tmp[0]+"','"+name_id+"_"+flag+"_answer','"+type+"')\" ");
			    	}
			    	if ("true".equalsIgnoreCase(disabled)) {
			    		buffer.append(" disabled=\"disabled\" ");
			    	}
			    	buffer.append(checked+"/>&nbsp;"+tmp[0]+":&nbsp;"+QuestionesBo.toHtml(tmp[1])+"</li>");
			    }
			}
		}
		buffer.append("</ul>");
		if ("5".equals(flag) || "2".equals(flag)) {
			buffer.append("<input type='hidden' name='"+name_id+"_"+flag+"_answer' id='"+name_id+"_"+flag+"_answer' value='");
			if(answer!=null&&answer.length()>0) {
				buffer.append(answer);
			}
			buffer.append("'/>");
		}
		return buffer.toString();
	}
	
	private String getJudgeHtml(){
		StringBuffer buffer = new StringBuffer();
		String checked="";
		buffer.append("<ul style=\"border: 0px;padding: 0px;margin: 0px;"+style+"\">");
    	if(answer!=null&&answer.length()>0)
    		checked = answer.indexOf("A")!=-1?"checked":"";
		buffer.append("<li><input type=\"radio\" name=\""+name_id+"\" id=\""+name_id+"_"+"A"+"\" value=\"A\" ");
		if ("5".equals(flag) || "2".equals(flag)) {
    		buffer.append(" onclick=\"collectAnswer('"+name_id+"_A','"+name_id+"_"+flag+"_answer','radio')\" ");
    	}
		if ("true".equalsIgnoreCase(disabled)) {
    		buffer.append(" disabled=\"disabled\" ");
    	}
		buffer.append(" "+checked+"/>&nbsp;对</li>");
		if(answer!=null&&answer.length()>0)
    		checked = answer.indexOf("B")!=-1?"checked":"";
		buffer.append("<li><input type=\"radio\" name=\""+name_id+"\" id=\""+name_id+"_"+"B"+"\" value=\"B\" ");
		if ("5".equals(flag) || "2".equals(flag)) {
    		buffer.append(" onclick=\"collectAnswer('"+name_id+"_B','"+name_id+"_"+flag+"_answer','radio')\" ");
    	}
		if ("true".equalsIgnoreCase(disabled)) {
    		buffer.append(" disabled=\"disabled\" ");
    	}
		buffer.append(" "+checked+"/>&nbsp;错</li>");
		buffer.append("</ul>");
		if ("5".equals(flag) || "2".equals(flag)) {
			buffer.append("<input type='hidden' name='"+name_id+"_"+flag+"_answer' id='"+name_id+"_"+flag+"_answer' value='");
			if(answer!=null&&answer.length()>0) {
				buffer.append(answer);
			}
			buffer.append("'/>");
		}
		return buffer.toString();
	}
	
	public String getType_id() {
		return type_id;
	}
	public void setType_id(String type_id) {
		this.type_id = type_id;
	}
	public String getXml() {
		return xml;
	}
	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getName_id() {
		return name_id;
	}

	public void setName_id(String name_id) {
		this.name_id = name_id;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}
