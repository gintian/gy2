package com.hjsj.hrms.transaction.hire.demandPlan.hireOrder;

import com.hjsj.hrms.businessobject.hire.HireOrderBo;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:AssignOrderTrans.java
 * </p>
 * <p>
 * Description:派单
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-05-13 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class AssignOrderTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	//

	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String a0100 = (String) hm.get("zh");// 对于用户管理中用户为登录帐号，对于帐号分配里用户为库前缀+a0100
	String a0101 = (String) hm.get("name");
	String objecttype = (String) hm.get("objecttype");// 1-帐号分配 4-用户管理
	String assignObjFld = (String) hm.get("assignObjFld");// 指派给招聘负责人【z0409】
                                                                // 还是人力资源部负责人【z04z9】

	String responsibleFld = "";// 负责人姓名字段
	String responsibleIDFld = assignObjFld;// 负责人帐号字段

	if ("z0409".equalsIgnoreCase(responsibleIDFld))// 招聘负责人帐号
	    responsibleFld = "z0412";// 招聘负责人姓名
	else if ("z0414".equalsIgnoreCase(responsibleIDFld))// 人力资源部负责人帐号
	    responsibleFld = "z0416";// 人力资源部负责人姓名

	a0101 = SafeCode.decode(a0101);
	a0100 = SafeCode.decode(a0100);
	String paramStr = (String) this.getFormHM().get("paramStr");
	String[] orders = paramStr.split(",");
	// 更新招聘负责人和下单时间。
	Date nowData = Date.valueOf(PubFunc.getStringDate("yyyy-MM-dd"));
	ArrayList list = new ArrayList();
	ContentDAO dao = new ContentDAO(this.getFrameconn());

	StringBuffer content = new StringBuffer();
	content.append(a0101 + ":<p>");
	content.append("现将招聘订单");
	try
	{
	    /** 取派单对象的帐号 */
	    // 先取到帐号对应的指标
	    String zpFld = "";
	    RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
	    if (login_vo != null)
	    {
		String login_name = login_vo.getString("str_value");
		int idx = login_name.indexOf(",");
		if (idx != -1)
		    zpFld = login_name.substring(0, idx);
	    }
	    if ("".equals(zpFld) || "#".equals(zpFld))
		zpFld = "username";
	    String zh = "";// 负责人帐号的值
	    if ("1".equals(objecttype))// 帐号分配里面的用户需要由【库前缀+a0100】和【帐号指标】取到对应的帐号
	    {
		String sqlStr = "select " + zpFld + " from " + a0100.substring(0, 3) + "A01 where a0100='" + a0100.substring(3) + "'";
		RowSet rs = dao.search(sqlStr);
		if (rs.next())
		    zh = rs.getString(1);
	    } else if ("4".equals(objecttype))
		zh = a0100;

	    // String nameFld = "";
	    // ArrayList fieldlist = DataDictionary.getFieldList("Z04",
                // Constant.USED_FIELD_SET);
	    // for (int i = 0; i < fieldlist.size(); i++)
	    // {
	    // FieldItem field = (FieldItem) fieldlist.get(i);
	    // if (field.getItemdesc().equals("姓名"))
	    // {
	    // nameFld = field.getItemid();
	    // break;
	    // }
	    // }

	    for (int i = 0; i < orders.length; i++)
	    {
		RecordVo vo = new RecordVo("Z04");
		vo.setString("z0400", orders[i]);
		vo = dao.findByPrimaryKey(vo);
		String b0110 = vo.getString("z0404");
		String e0122 = vo.getString("z0405");
		String e01a1 = vo.getString("z0403");
		// vo.setString("z0409", z0409);// 招聘负责人
		vo.setString(responsibleIDFld, zh);// 负责人帐号
		vo.setString(responsibleFld, a0101);// 负责人
		vo.setDate("z0402", nowData);

		// if (nameFld.length() > 0)
		// vo.setString(nameFld, a0101);
		list.add(vo);
		content.append("[" + AdminCode.getCodeName("UN", b0110) + "/" + AdminCode.getCodeName("UM", e0122) + "/" + AdminCode.getCodeName("@K", e01a1) + "招聘需求的订单：" + orders[i] + "]");
	    }
	    dao.updateValueObject(list);
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
	// 给接单人发送邮件
	String title = "招聘派单";
	content.append("分配给你，请及时处理！<p>");
	content.append(this.getUserView().getUserName() + "<p>");
	content.append(PubFunc.getStringDate("yyyy-MM-dd"));

	HireOrderBo hirebo = new HireOrderBo();
	String fromAddr = hirebo.getFromAddr();

	RecordVo vo = ConstantParamter.getConstantVo("SS_EMAIL");
	if ("#".equals(vo.getString("str_value")))
	    throw GeneralExceptionHandler.Handle(new Exception("系统没有设置邮件指标,运行错误!"));

	String email = "";
	String base = "";
	if ("4".equals(objecttype))// 用户管理里的用户
	{
	    String sql = "select a0100,nbase,email from operuser where username='" + a0100 + "'";
	    try
	    {
		RowSet rs = dao.search(sql);
		if (rs.next())
		{
		    String a = rs.getString("a0100") == null ? "" : rs.getString("a0100");
		    base = rs.getString("nbase") == null ? "" : rs.getString("nbase");
		    if (a.length() > 0 && base.length() > 0)
		    {
			a0100 = base + a;
			EMailBo bo = new EMailBo(this.frameconn, true, a0100.substring(0, 3));
			email = bo.getEmailAddrByA0100(a0100);
		    }
		    if (email.length() == 0)
		    {
			email = rs.getString("email") == null ? "" : rs.getString("email");
		    }
		    if (email.length() == 0)
			throw new GeneralException("收件人的邮箱地址没有设置！");

		}
	    } catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	}
	if ("1".equals(objecttype))// 帐号分配里面的用户 a0100=usr000009
	{
	    EMailBo bo = new EMailBo(this.frameconn, true, a0100.substring(0, 3));
	    String toAddr = bo.getEmailAddrByA0100(a0100);
	    if (toAddr == null || (toAddr != null && ("".equals(toAddr.trim()) || "无".equals(toAddr.trim()))))
		throw new GeneralException("收件人的邮箱地址没有设置！");

	    bo.sendEmail(title, content.toString(), "", fromAddr, toAddr);
	} else if ("4".equals(objecttype))// 用户管理中用户 a0100=zpzg
	{
	    EMailBo bo = new EMailBo(this.frameconn, true, base);
	    String toAddr = email;
	    if (toAddr == null || (toAddr != null && ("".equals(toAddr.trim()) || "无".equals(toAddr.trim()))))
		throw new GeneralException("收件人的邮箱地址没有设置！");

	    bo.sendEmail(title, content.toString(), "", fromAddr, toAddr);
	}

    }

}
