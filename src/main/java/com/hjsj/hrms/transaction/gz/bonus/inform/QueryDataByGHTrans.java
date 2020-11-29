package com.hjsj.hrms.transaction.gz.bonus.inform;

import com.hjsj.hrms.businessobject.ht.ContractBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:QueryDataByA0101Trans
 * </p>
 * <p>
 * Description:按姓名进行查询
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * create time :2009-7-8:下午03:48:28
 * 
 * @author FanZhiGuo
 * @version 1.0
 */
public class QueryDataByGHTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	ContractBo bo = new ContractBo(this.frameconn, this.userView);
	ConstantXml xml = new ConstantXml(this.frameconn, "GZ_PARAM", "Params");
	// 人员库
	String nbaseStr = xml.getTextValue("/Params/Bonus/base");
	String[] nbaseArray = nbaseStr.split(",");
	// 工号字段
	String jobnumFld = xml.getTextValue("/Params/Bonus/num");
	if(jobnumFld==null || (jobnumFld!=null && jobnumFld.length()==0))
	    jobnumFld="A0101";
	// 工号的值
	String jobnumVal = (String) this.getFormHM().get("jobnumVal");
	jobnumVal = SafeCode.decode(jobnumVal);
	//type=1:工号字段指定了，且模糊查询姓名 type=0:模糊查询工号，工号可以指定为姓名
	String type = (String) this.getFormHM().get("type");
	HashMap dbmap = bo.searchNbase();
	DbNameBo dbbo = new DbNameBo(this.getFrameconn());
	ArrayList dblist = dbbo.getAllDbNameVoList(this.userView);
	ContentDAO dao = new ContentDAO(this.getFrameconn());
	try
	{
	    int i = 0;
	    ArrayList objlist = new ArrayList();
	    if (!(jobnumVal == null || "".equalsIgnoreCase(jobnumVal)))
	    {
		RowSet rset = dao.search(getQueryString(jobnumFld, jobnumVal, dblist, nbaseStr,type));
		while (rset.next())
		{
		    if (i > 40)
			break;
		    
		    jobnumVal = rset.getString(jobnumFld)==null?"":rset.getString(jobnumFld).trim();
		    String dbpri = rset.getString("dbpre");
		    String dbname = (String) dbmap.get(dbpri);
		    CommonData objvo = new CommonData();
		    String b0110 = rset.getString("b0110");		    
		    String b0110name = AdminCode.getCodeName("UN", b0110);
		    String e0122 = rset.getString("e0122");		    
		    String e0122name = AdminCode.getCodeName("UM", e0122);
		    if("0".equals(type))
		    {
			    if("a0101".equalsIgnoreCase(jobnumFld))
				objvo.setDataName(jobnumVal + "|" + b0110name+"|"+ e0122name+"|"+dbname);
			    else
				objvo.setDataName(jobnumVal + "|" + rset.getString("a0101").trim() + "|"+dbname);
		    }else if("1".equals(type))//type=1表示如果工号字段指定了，且模糊查询姓名
		    {
//			b0110name=b0110name.length()>5?b0110name.substring(0, 5)+"...":b0110name;
//			e0122name=e0122name.length()>5?e0122name.substring(0, 5)+"...":e0122name;
			objvo.setDataName(rset.getString("a0101").trim() + "|" + b0110name+"|"+ e0122name+"|"+dbname);
		    }
		    objvo.setDataValue( dbpri + rset.getString("a0100")+":"+b0110name+":"+e0122name+":"+rset.getString("a0101")+":"+jobnumVal);
		    objlist.add(objvo);
		    ++i;
		}
	    }
	    this.getFormHM().put("objlist", objlist);
	} catch (Exception ex)
	{
	    ex.printStackTrace();
	    throw GeneralExceptionHandler.Handle(ex);
	}
    }
//type=1:工号字段指定了，且模糊查询姓名 type=0:模糊查询工号，工号可以指定为姓名
    private String getQueryString(String jobnumFld, String jobnumVal, ArrayList dblist, String nbaseStr,String type) throws GeneralException
    {

	StringBuffer buf = new StringBuffer();
	ArrayList fieldlist = new ArrayList();
	String strWhere = null;
	String sexpr = "1";
	String sfactor = jobnumFld + "=" + jobnumVal + "*`";
	if ("1".equals(type))
	    sfactor = "a0101=" + jobnumVal + "*`";
	
	for (int i = 0; i < dblist.size(); i++)
	{
	    RecordVo vo = (RecordVo) dblist.get(i);
	    String pre = vo.getString("pre");
	    if (nbaseStr.toLowerCase().indexOf(pre.toLowerCase()) == -1)
		continue;
	    strWhere = userView.getPrivSQLExpression(sexpr + "|" + sfactor, pre, false, fieldlist);

	    if ("0".equals(type))
	    {
		buf.append("select a0000, a0101,b0110,e0122,a0100, '");
		buf.append(vo.getString("pre"));
		buf.append("' as dbpre  ");
		if (!"a0101".equalsIgnoreCase(jobnumFld))
		    buf.append("," + jobnumFld + " ");
	    }else if ("1".equals(type))
	    {
		buf.append("select a0000,b0110,e0122,a0100, '");
		buf.append(vo.getString("pre"));
		buf.append("' as dbpre  ");
		buf.append(","+jobnumFld);
		if (!"a0101".equalsIgnoreCase(jobnumFld))
		    buf.append(",a0101");
		buf.append(" ");
	    }
	  
	    buf.append(strWhere);
	    buf.append(" UNION ");
	}
	buf.setLength(buf.length() - 7);
//	buf.append(" order by a0000,a0100,dbpre desc");
	if ("1".equals(type))
	    buf.append(" order by dbpre desc,a0000");
	else
	    buf.append(" order by dbpre desc,"+jobnumFld);
	return buf.toString();
    }
}
