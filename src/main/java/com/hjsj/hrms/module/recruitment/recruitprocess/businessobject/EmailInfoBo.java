package com.hjsj.hrms.module.recruitment.recruitprocess.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class EmailInfoBo {
	private Connection conn=null;
    private UserView userview;
    
    public EmailInfoBo(Connection conn, UserView userview)
    {
    	 this.conn=conn;
    	 this.userview=userview;
    }
    /**
     * 得到当前邮件所需要的信息
     * @param a0100 人员编号
     * @return
     */
    public LazyDynaBean getInfo(String a0100,String z0301,String nbase)
    {
    	LazyDynaBean bean = new LazyDynaBean();
    	try {
    		ContentDAO dao = new ContentDAO(conn);
    		boolean flag = true;
    		String z0321 = "";
    		RowSet z0321rs = dao.search("select Z0321 from Z03 where Z0301='"+z0301+"'");
    		if(z0321rs.next())
    		{    			
    			z0321 = z0321rs.getString("Z0321");
    		}
    		while(flag)
    		{
    			RowSet codeRs = dao.search("select codeitemid,parentid from  organization where codeitemid='"+z0321+"'");
    			if(codeRs.next())
    			{    				
    				String parentid = codeRs.getString("parentid");
    				z0321 = codeRs.getString("codeitemid");
    				if(parentid.equalsIgnoreCase(z0321))
    				{
    					flag = false;
    				}else{
    					z0321 = parentid;
    				}
    			}
    		}
    		StringBuffer sqlstr = new StringBuffer();
    		ArrayList list = new ArrayList();
    		sqlstr.append(" select Z0301,Z0351,Z0375,custom_name,codeitemdesc UM, ");
    		sqlstr.append(" (select codeitemdesc from organization ");
    		sqlstr.append(" where codeitemid = ? ) UN ");
    		sqlstr.append(" from Z03 ");
    		sqlstr.append(" left join zp_pos_tache on z03.z0301=zp_pos_tache.zp_pos_id ");
    		sqlstr.append(" left join organization on z03.Z0325=organization.codeitemid  " );
    		sqlstr.append(" left join zp_flow_links on zp_pos_tache.link_id = zp_flow_links.id ");
    		sqlstr.append(" where zp_pos_tache.A0100 = ? and zp_pos_tache.nbase = ?  and Z03.Z0301=? ");
    		list.add(z0321.trim());
    		list.add(a0100.trim());
    		list.add(nbase.trim());
    		list.add(z0301.trim());
			RowSet rs = dao.search(sqlstr.toString(), list);
			if(rs.next())
			{
				bean.set("Z0301",rs.getString("Z0301") );
				bean.set("Z0351",rs.getString("Z0351") );
				if(rs.getDate("Z0375")!=null)
				{					
					bean.set("Z0375",rs.getDate("Z0375"));
				}else{
					bean.set("Z0375","未设置");
				}
				bean.set("custom_name",rs.getString("custom_name") );
				bean.set("UM",rs.getString("UM")==null?"":rs.getString("UM") );
				bean.set("UN",rs.getString("UN")==null?"":rs.getString("UN") );
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return bean;
    }
    /***
     * offer通知
     * @param a0100人员编号
     * @param codeitemdesc单位名称
     * @param a0101人员姓名
     * @param z0325需求部门
     * @param z0351职位名称
     * @param z0375到岗时间
     * @param userName操作人名字
     * @param userPhone操作人电话
     * @return
     */
    public LazyDynaBean offerModel(String a0100,String codeitemdesc,String a0101,String z0325,String z0351,String z0375,String userName,String userPhone,String c0102)
    {
    	LazyDynaBean bean = new LazyDynaBean();
    	java.util.Calendar c=java.util.Calendar.getInstance();    
        java.text.SimpleDateFormat f=new java.text.SimpleDateFormat("yyyy年MM月dd日");
    	StringBuffer title = new StringBuffer(codeitemdesc+" "+z0351+"录用通知");
    	StringBuffer content = new StringBuffer();
    	content.append(a0101+"先生/女士，您好：\\n");
    	content.append("    非常高兴地通知您，经过我公司的面试和讨论，我们一致认为您是我公司"+z0325+"的"+z0351+"职位的合适人选。\\n\\n");
    	content.append("    请您于"+z0375+"日到我公司报到，报到时需要携带以下入职文件：\\n");
    	content.append("   （一）、二代身份证原件\\n");
    	content.append("   （二）、毕业证、学位证原件\\n");
    	content.append("   （三）、相关资格证书原件\\n");
    	content.append("   （四）、一寸彩色免冠照片2张\\n");
    	content.append("   （五）、入职体检证明（公司指定的体检机构）\\n");
    	content.append("   （六）、原单位离职证明（加盖单位印章）\\n\\n");
    	content.append("    公司仅保留复印件，原件退换本人。\\n\\n");
    	content.append("    以上内容如有疑问，请与人力资源部联系\\n\\n");
    	content.append("    联系人："+userName+"\\n");
    	content.append("    电话："+userPhone+"\\n");
    	content.append("    "+codeitemdesc+" 人力资源部\\n");
    	content.append("    "+f.format(c.getTime())+"\\n");
    	bean.set("a0100", a0100);
    	bean.set("c0102", c0102);
    	bean.set("title", title);
    	bean.set("content", content);
    	return bean;
    }
    /***
     * 面试安排邮件模板
     * @param a0100人员编号
     * @param codeitemdesc部门
     * @param a0101人员姓名
     * @param z0351职位
     * @param userName发件人姓名
     * @param userPhone发件人电话
     * @param c0102收件人邮箱
     * @param custom_name环节名
     * @return
     */
    public LazyDynaBean getNotice(String a0100,String codeitemdesc,String a0101,String z0351,String userName,String userPhone,String c0102,String custom_name)
    {
    	LazyDynaBean bean = new LazyDynaBean();
    	java.util.Calendar c=java.util.Calendar.getInstance();    
        java.text.SimpleDateFormat f=new java.text.SimpleDateFormat("yyyy年MM月dd日");
    	StringBuffer title = new StringBuffer(codeitemdesc+"面试通知");
    	StringBuffer content = new StringBuffer();
    	content.append(a0101+"先生/女士，您好：\\n");
    	content.append("    我公司已收到您的应聘资料，经初步审核，恭喜您符合本公司的职缺招募条件，特邀请您参加公司安排的"+custom_name+"环节\\n");
    	content.append("    职位名称："+z0351+"\\n");
    	content.append("    面试时间：\\n");
    	content.append("    来时，请注意以下事项：\\n");
    	content.append("    一、请着正装参加面试\\n");
    	content.append("    二、携带以下资料：\\n");
    	content.append("      1、\\n");
    	content.append("    三、若不便前往，请致电说明，并约定适合的时段，公司做另外安排。\\n");
    	content.append("    祝您面试顺利！如有疑问或其他需要我们帮助的地方，请及时与我们联系\\n\\n");
    	content.append("    联系电话:"+userPhone+"，联系人:"+userName+"\\n\\n");
    	content.append("    以上内容如有疑问，请与人力资源部联系\\n\\n");
    	content.append("    联系人："+userName+"\\n");
    	content.append("    电话："+userPhone+"\\n");
    	content.append("    附：\\n");
    	content.append("    一、乘车路线\\n");
    	content.append("      1、地铁：......\\n");
    	content.append("      2、公交：......\\n");
    	content.append("    二、职位说明\\n");
    	content.append("      岗位职责：\\n      .....\\n");
    	content.append("      任职资格：\\n      .....\\n");
    	content.append("    "+codeitemdesc+" 人力资源部\\n");
    	content.append("    "+f.format(c.getTime())+"\\n");
    	bean.set("a0100", a0100);
    	bean.set("c0102", c0102);
    	bean.set("title", title);
    	bean.set("content", content.toString().replace("\\n", "\n"));
    	return bean;
    }
    /***
     * 面试官邮件信息模板
     * @param a0101面试官姓名
     * @param z0351面试岗位
     * @param z0325需求部门
     * @param userName发件人名字
     * @param c0102收件人邮箱
     * @param candidate面试者
     * @param dateTime面试时间
     * @param address面试地点
     * @return
     */
    public LazyDynaBean getInterviewer(HashMap<String, String> sendInfo)
    {
    	StringBuffer url = new StringBuffer(this.userview.getServerurl());
    	url.append("/recruitment/resumecenter/evaluationresume.do?b_search=link");
    	url.append("&encryptParam=");
    	url.append(PubFunc.encryption("z0301="+PubFunc.encrypt(sendInfo.get("z0301"))+"&flag=1&nbase_o="+PubFunc.encrypt(sendInfo.get("nbase"))+"&a0100_o="+PubFunc.encrypt(sendInfo.get("a0100")))+"&paramEvaluationValue");
    	LazyDynaBean bean = new LazyDynaBean();
    	java.util.Calendar c=java.util.Calendar.getInstance();    
    	java.text.SimpleDateFormat f=new java.text.SimpleDateFormat("yyyy年MM月dd日");
    	StringBuffer title = new StringBuffer(sendInfo.get("z0351")+"面试通知");
    	StringBuffer content = new StringBuffer();
    	content.append(sendInfo.get("examiner")+"，您好：\\n");
    	content.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;人力资源部邀请您参加"+sendInfo.get("z0351")+"的面试工作，时间安排如下：\\n\\n");
    	content.append("<table style='width:550px;border-collapse:collapse;'>");
    	content.append("<tr>");
    	content.append("<th style='width:70px;background:#f0f0f0;height:30px; line-height:30px; font-weight:normal; text-align:center; border:1px #c5c5c5 solid;'>姓名</th>");
    	if(sendInfo.get("sex")!=null)
    		content.append("<th style='width:50px;background:#f0f0f0;height:30px; line-height:30px; font-weight:normal; text-align:center; border:1px #c5c5c5 solid;'>性别</th>");
    	if(sendInfo.get("age")!=null)
    		content.append("<th style='width:50px;background:#f0f0f0;height:30px; line-height:30px; font-weight:normal; text-align:center; border:1px #c5c5c5 solid;'>年龄</th>");
    	content.append("<th style='width:80px;background:#f0f0f0;height:30px; line-height:30px; font-weight:normal; text-align:center; border:1px #c5c5c5 solid;'>招聘部门</th>");
    	content.append("<th style='width:160px;background:#f0f0f0;height:30px; line-height:30px; font-weight:normal; text-align:center; border:1px #c5c5c5 solid;'>招聘职位</th>");
    	content.append("<th style='width:140px;background:#f0f0f0;height:30px; line-height:30px; font-weight:normal; text-align:center; border:1px #c5c5c5 solid;'>面试时间</th>");
    	content.append("<th style='width:150px;background:#f0f0f0;height:30px; line-height:30px; font-weight:normal; text-align:center; border:1px #c5c5c5 solid;'>面试地点</th>");
    	content.append("</tr>");
    	content.append("<tr>");
    	content.append("<td style='border:1px #c5c5c5 solid; text-align:center;'><a href='"+url.toString()+"' style='text-decoration:none;color:#1b4a98;'>"+sendInfo.get("a0101")+"</a></td>");
    	if(sendInfo.get("sex")!=null)
    		content.append("<td style='border:1px #c5c5c5 solid; text-align:center;'>"+sendInfo.get("sex")+"</td>");
    	if(sendInfo.get("age")!=null)
    		content.append("<td style='border:1px #c5c5c5 solid; text-align:center;'>"+sendInfo.get("age")+"</td>");
    	content.append("<td style='border:1px #c5c5c5 solid; text-align:center;'>"+sendInfo.get("z0325")+"</td>");
    	content.append("<td style='border:1px #c5c5c5 solid; text-align:center;'>"+sendInfo.get("z0351")+"</td>");
    	content.append("<td style='border:1px #c5c5c5 solid; text-align:center;'>"+sendInfo.get("dateTime")+"</td>");
    	content.append("<td style='border:1px #c5c5c5 solid; text-align:center;'>"+sendInfo.get("address")+"</td>");
    	content.append("</tr>");
    	content.append("<tr>");
    	content.append("<td style='text-align:light;' colspan='9'>");
    	content.append("(*注:点击姓名进行查看)</td>");
    	content.append("</tr>");
    	content.append("</table>\\n");
    	content.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;人力资源部\\n");
    	content.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+sendInfo.get("username")+"\\n");
    	content.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+f.format(c.getTime())+"\\n");
    	bean.set("c0102", sendInfo.get("email"));
    	bean.set("title", title.toString());
    	bean.set("content", content.toString().replace("\\n", "\n"));
    	return bean;
    }
    /**
     * 面试通知，通过
     * @param a0100人员编号
     * @param codeitemdesc公司名字
     * @param a0101人员姓名
     * @param z0351职位名称
     * @param c0102发送邮箱
     * @param custom_name环节名
     * @return
     */
    public LazyDynaBean getPassChoice(String a0100,String codeitemdesc,String a0101,String z0351,String c0102,String custom_name)
    {
    	LazyDynaBean bean = new LazyDynaBean();
    	java.util.Calendar c=java.util.Calendar.getInstance();    
        java.text.SimpleDateFormat f=new java.text.SimpleDateFormat("yyyy年MM月dd日");
    	StringBuffer title = new StringBuffer(codeitemdesc+" "+custom_name+"结果通知");
    	StringBuffer content = new StringBuffer();
    	content.append(a0101+"先生/女士，您好：\\n");
    	content.append("    恭喜您通过了我公司"+z0351+"的"+custom_name+"，请您耐心等待我们的下一轮面试安排。\\n\\n");
    	content.append("    "+codeitemdesc+" 人力资源部\\n");
    	content.append("    "+f.format(c.getTime())+"\\n");
    	bean.set("a0100", a0100.toString());
    	bean.set("c0102", c0102.toString());
    	bean.set("title", title.toString());
    	bean.set("content", content.toString());
    	return bean;
    }
    /**
     * 面试通知，淘汰
     * @param a0100人员编号
     * @param codeitemdesc公司名字
     * @param a0101人员姓名
     * @param z0351职位名称
     * @param c0102发送邮箱
     * @param custom_name环节名
     * @return
     */
    public LazyDynaBean getObsolete(String a0100,String codeitemdesc,String a0101,String z0351,String c0102,String custom_name)
    {
    	LazyDynaBean bean = new LazyDynaBean();
    	java.util.Calendar c=java.util.Calendar.getInstance();    
        java.text.SimpleDateFormat f=new java.text.SimpleDateFormat("yyyy年MM月dd日");
    	StringBuffer title = new StringBuffer(codeitemdesc+" "+custom_name+"结果通知");
    	StringBuffer content = new StringBuffer();
    	content.append(a0101+"先生/女士，您好：\\n");
    	content.append("    感谢您参加"+codeitemdesc+"的面试！本公司对您在面试过程中所展现出来的积极努力和认真参与的态度，" +
    			"谨致以由衷的敬意和真诚的赞赏！您在面试中表现出了许多方面的优秀潜质。经过本公司慎重的考虑和评估，" +
    			"觉得您暂时不适合这个职位，但是我们对您仍然是充满了信心，相信您会找到更加适合您的舞台，我们由衷地祝福您！\\n\\n");
    	content.append("    您的相关资料已存入我公司储备人才库，如有合适职位，我们将尽快与您联系。\\n");
    	content.append("    "+codeitemdesc+" 人力资源部\\n");
    	content.append("    "+f.format(c.getTime())+"\\n");
    	bean.set("a0100", a0100.toString());
    	bean.set("c0102", c0102.toString());
    	bean.set("title", title.toString());
    	bean.set("content", content.toString());
    	return bean;
    }
}
