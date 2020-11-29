package com.hjsj.hrms.module.recruitment.recruitprocess.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

/***
 * 简历评价功能相关Bo类
 * <p>Title: EvaluationBo </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-8-5 下午02:20:44</p>
 * @author xiexd
 * @version 1.0
 */
public class EvaluationBo {
	private Connection conn=null;
    private UserView userview;
    public String flag = "";	//flag=1 不允许评价
    public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public EvaluationBo(Connection conn, UserView userview)
    {
    	 this.conn=conn;
    	 this.userview=userview;
    }
    
    /***
     * 获取已勾选人员的相关基本信息
    * @Title:getInfoList
    * @Description：
    * @author xiexd
    * @param nbase
    * @param a0100
    * @return
     */
    public void getInfoList(String nbase,String a0100)
    {
    	this.userview.getHm().remove("emailInfoList");
    	ArrayList infoList = new ArrayList();
    	RowSet rs = null;
    	RowSet ai = null;
    	try {
    		String[] nbases = nbase.split(",");
    		String[] a0100s = a0100.split(","); 
    		ContentDAO dao = new ContentDAO(conn);
    		ArrayList a0100list = new ArrayList();
    		FieldItem ageFieldItem = this.getUsedField("A0112", "A01", "年龄");
            FieldItem sexFieldItem = this.getUsedField("A0107", "A01", "性别");
            FieldItem a0410Item = this.getUsedField("A0410", "A04", "所学专业类别");
            FieldItem a0435Item = this.getUsedField("A0435", "A04", "学校");
    		StringBuffer sql = new StringBuffer();
    		sql.append("select a01.A0100,A0101,");
    		if(sexFieldItem!=null)
			{
            	sql.append(" item1.codeitemdesc A0107,");
			}
    		if(ageFieldItem!=null)
    		{    			
    			sql.append(" a01."+ageFieldItem.getItemid()+" A0112,");
    		}else{
    			sql.append(" null A0112,");
    		}
    		if(a0435Item!=null)
    			sql.append(" a04."+a0435Item.getItemid()+" A0435,");
    		else
    			sql.append(" null A0435,");
    		if(a0410Item!=null)
    			sql.append(" a04."+a0410Item.getItemid()+" A0410 ");
    		else
    			sql.append(" null A0410 ");
    		
    		sql.append(" from "+PubFunc.decrypt(nbases[0])+"A01 a01 ");
    		sql.append("left join (select a.* from "+PubFunc.decrypt(nbases[0])+"A04 a where a.i9999=(select max(b.i9999) from "+PubFunc.decrypt(nbases[0])+"A04 b where a.a0100=b.a0100  ) ) a04 on a04.A0100 = a01.A0100 ");
    		if(sexFieldItem!=null)
			{
    			sql.append("left join (select * from codeitem where codesetid = 'AX') item1 on item1.codeitemid = a01."+sexFieldItem.getItemid());
			}
    		sql.append(" where a01.a0100 in (");
    		
			for(int i=0;i<a0100s.length;i++)
			{
				sql.append("?,");
				a0100list.add(PubFunc.decrypt(a0100s[i]));
			}
			
			sql.setLength(sql.length()-1);
			sql.append(")");
			
			LazyDynaBean bean = new LazyDynaBean();
			rs = dao.search(sql.toString(), a0100list);
			//获取人员相关信息
			while(rs.next())
			{
				bean = new LazyDynaBean();
				bean.set("nbase", PubFunc.decrypt(nbases[0]));
				bean.set("a0100", rs.getString("a0100")==null ? "未填" : rs.getString("a0100"));
				bean.set("a0101", rs.getString("A0101")==null ? "未填" : rs.getString("A0101"));
				if(sexFieldItem!=null)
					bean.set("a0107", rs.getString("A0107")==null ? "未填" : rs.getString("A0107"));
				if(ageFieldItem!=null)
					bean.set("a0112", rs.getString("A0112")==null ? "未填" : rs.getString("A0112"));
				bean.set("a0435", rs.getString("A0435")==null ? "未填" : rs.getString("A0435"));
				String a0410 = rs.getString("A0410");
				ai = dao.search("select codeitemdesc from codeitem where codesetid = 'AI' and codeitemid = '"+a0410+"'");
				if(ai.next())
				{
					bean.set("a0410", ai.getString("codeitemdesc"));
				}else{
					bean.set("a0410", "未填");//专业未填
				}
				infoList.add(bean);
			}
			this.userview.getHm().put("emailInfoList", infoList);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(ai);
		}
    }
    
    /***
     * 获取模板名称列表
    * @Title:getTemplateList
    * @Description：
    * @author xiexd
    * @param nModule
    * @param sub_module
    * @return
     */
    public String getTemplateList(String nModule,String sub_module)
    {
    	StringBuffer templateList = new StringBuffer("[");
    	try {
    		ContentDAO dao = new ContentDAO(this.conn);
			ArrayList value = new ArrayList();
			String b0110 = this.userview.getUserOrgId();
			StringBuffer sql= new StringBuffer("select id,name,nModule,nInfoclass,Subject,content,attach,address,");
			sql.append("Sub_module,Return_address,B0110,ownflag from email_name where (valid=1 or valid is null) and nModule=? "); 
			value.add(nModule);
			if(b0110!=null&&b0110.trim().length()>0)
			{				
				sql.append(" and ( B0110=? or B0110 = 'HJSJ' or B0110 is null) ");
				value.add(b0110);
			}
			if(sub_module!=null&&sub_module.trim().length()>0)
			{
				sql.append("and Sub_module=? ");
				value.add(sub_module);
			}
			RowSet rs=dao.search(sql.toString(),value);
			
			while(rs.next())
			{
				templateList.append("['"+rs.getString("id")+"','"+rs.getString("name")+"'],");
			}
			rs.close();
			//循环输出字段列，最后一个时将“,”号去掉
			if(templateList.length()>1)
				templateList.setLength(templateList.length()-1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		templateList.append("]");
    	return templateList.toString();
    }
    
    /***
     * 获取邮件模板内容
    * @Title:getTemplateInfo
    * @Description：
    * @author xiexd
    * @param nModule
    * @param Sub_module
    * @param id
    * @return
     */
    public LazyDynaBean getTemplateInfo(String nModule,String sub_module,String id,String z0301)
	{
		LazyDynaBean bean  = new LazyDynaBean();
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList value = new ArrayList();
			String b0110 = this.userview.getUserOrgId();
			StringBuffer sql= new StringBuffer(" select id,name,nModule,nInfoclass,Subject,content,attach,address,");
			sql.append("Sub_module,Return_address,B0110,ownflag from email_name where nModule=? and id=? "); 
			value.add(nModule);
			value.add(id);
			if(b0110!=null&&b0110.trim().length()>0)
			{				
				sql.append(" and ( B0110=? or B0110 = 'HJSJ' or B0110 is null) ");
				value.add(b0110);
			}
			if(sub_module!=null&&sub_module.trim().length()>0)
			{
				sql.append("and Sub_module=? ");
				value.add(sub_module);
			}
			rs=dao.search(sql.toString(),value);
			if(rs.next())
			{
				bean.set("id",rs.getString("id"));//主键Id
				bean.set("name",rs.getString("name"));//模板名称
				bean.set("nModule",rs.getString("nModule"));//模板编号
				bean.set("nInfoclass",rs.getString("nInfoclass"));//信息集
				bean.set("attach",rs.getString("attach"));//邮件附件
				bean.set("sub_module",rs.getString("Sub_module"));//子模块编号
				bean.set("return_address",(rs.getString("Return_address")!=null)?rs.getString("Return_address"):"");//回复地址
				bean.set("b0110",rs.getString("B0110"));//所属机构
				bean.set("ownflag",rs.getString("ownflag"));//系统模板  1：系统内置模板；0：自定义模板
				String emailField=rs.getString("address");//接收地址
				if(emailField!=null&&emailField.trim().length()>0)
				{
					emailField=emailField.substring(0,emailField.indexOf(":")).trim();
				}
				bean.set("address",emailField);
				bean.set("subject", rs.getString("subject")==null?"":rs.getString("subject"));//邮件主题
				bean.set("content", this.getEmailContent(rs.getString("content")==null?"":rs.getString("content"),z0301));//邮件内容
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null)
					rs.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return bean;
	} 
    
    /***
     * 组合邮件内容，将表格及定义的模板组合成一个字符串
    * @Title:getEmailContent
    * @Description：
    * @author xiexd
    * @param content
    * @return
     */
    public String getEmailContent(String content,String z0301)
    {
    	StringBuffer emailContent = new StringBuffer();
    	StringBuffer url = new StringBuffer(this.userview.getServerurl());
    	url.append("/recruitment/resumecenter/evaluationresume.do?b_search=link");
    	url.append("&encryptParam=");
    	emailContent.append(content);
    	emailContent.append("<br/><table style='width:550px;border-collapse:collapse;font-size: 12px;'>");
    	emailContent.append("<tr>");
    	emailContent.append("<th style='width:50px;background:#f0f0f0;height:30px; line-height:30px; font-weight:normal; text-align:center; border:1px #c5c5c5 solid;'>序号</th>");
    	emailContent.append("<th style='width:100px;background:#f0f0f0;height:30px; line-height:30px; font-weight:normal; text-align:center; border:1px #c5c5c5 solid;'>姓名</th>");
    	emailContent.append("<th style='width:80px;background:#f0f0f0;height:30px; line-height:30px; font-weight:normal; text-align:center; border:1px #c5c5c5 solid;'>性别</th>");
    	emailContent.append("<th style='width:60px;background:#f0f0f0;height:30px; line-height:30px; font-weight:normal; text-align:center; border:1px #c5c5c5 solid;'>年龄</th>");
    	emailContent.append("<th style='width:180px;background:#f0f0f0;height:30px; line-height:30px; font-weight:normal; text-align:center; border:1px #c5c5c5 solid;'>专业</th>");
    	emailContent.append("<th style='width:180px;background:#f0f0f0;height:30px; line-height:30px; font-weight:normal; text-align:center; border:1px #c5c5c5 solid;'>学校</th>");
    	emailContent.append("</tr>");
    	ArrayList list = (ArrayList)this.userview.getHm().get("emailInfoList");
    	for(int i=0;i<list.size();i++)
    	{
    		LazyDynaBean bean = (LazyDynaBean) list.get(i); 
    		StringBuffer url_new = new StringBuffer();
    		url_new.append(PubFunc.encryption("z0301="+z0301+this.flag+"&nbase_o="+PubFunc.encrypt(bean.get("nbase").toString())+"&a0100_o="+PubFunc.encrypt(bean.get("a0100").toString()))+"&paramEvaluationValue");
    		emailContent.append("<tr>");
    		emailContent.append("<td style='border:1px #c5c5c5 solid; text-align:right;border-top:none;padding-right:5px;'>"+(i+1)+"</td>");
    		emailContent.append("<td style='border:1px #c5c5c5 solid; text-align:left;border-left:none;border-top:none;padding-left:5px;'><a href='"+url.toString()+url_new.toString()+"' style='text-decoration:none;color:#1b4a98;'>"+bean.get("a0101")+"</a></td>");
    		String sex = bean.get("a0107")==null?"未填":(String) bean.get("a0107");
    		String age = bean.get("a0112")==null?"未填":(String) bean.get("a0112");
    		emailContent.append("<td style='border:1px #c5c5c5 solid; text-align:left;border-left:none;border-top:none;padding-left:5px;'>"+sex+"</td>");
    		emailContent.append("<td style='border:1px #c5c5c5 solid; text-align:left;border-left:none;border-top:none;padding-right:5px;'>"+age+"</td>");
    		emailContent.append("<td style='border:1px #c5c5c5 solid; text-align:left;border-left:none;border-top:none;padding-left:5px;'>"+bean.get("a0410")+"</td>");
    		emailContent.append("<td style='border:1px #c5c5c5 solid; text-align:left;border-left:none;border-top:none;padding-left:5px;'>"+bean.get("a0435")+"</td>");
    		emailContent.append("</tr>");
    	}
    	emailContent.append("<tr>");
		emailContent.append("<td style='text-align:light;' colspan='6'>");
		if("".equals(this.flag))
			emailContent.append("(*注:点击姓名进行评价)</td>");
		else
			emailContent.append("(*注:点击姓名进行查看)</td>");
    	emailContent.append("</tr>");
    	emailContent.append("</table><br/>");
    	this.userview.getHm().remove("emailInfoList");
    	return emailContent.toString();
    }
    
    /**
	 * @param itemId 需要用的字段名称
	 * @param itemSet 所在子集
	 * @param itemdesc 如果所需字段名称没拿到对应fieldItem的话，需要根据描述去查
	 * @return
	 */
	public FieldItem getUsedField(String itemId,String itemSet,String itemdesc){
		FieldItem item = DataDictionary.getFieldItem(itemId, itemSet);
        ArrayList<FieldItem> fieldList = DataDictionary.getFieldList(itemSet, Constant.USED_FIELD_SET);
        if(item==null||(Constant.USED_FIELD_SET+"").equals(item.getUseflag())){
			for (FieldItem fieldItem : fieldList) {
				if(itemdesc.equals(fieldItem.getItemdesc()))
					itemId = fieldItem.getItemid();
			}
        }
        FieldItem needItem = DataDictionary.getFieldItem(itemId, itemSet);
        if(needItem==null||!"1".equals(needItem.getUseflag()))
        	return null;
		return needItem;
	}
}
