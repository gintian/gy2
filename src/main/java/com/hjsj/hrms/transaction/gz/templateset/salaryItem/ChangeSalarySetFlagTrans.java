package com.hjsj.hrms.transaction.gz.templateset.salaryItem;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 修改薪资项目标识
 *<p>Title:ChangeSalarySetFlagTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 4, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class ChangeSalarySetFlagTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String fieldid=(String)this.getFormHM().get("fieldid");
			String flag=(String)this.getFormHM().get("flag");
			String salaryid=(String)this.getFormHM().get("salaryid");
			String formula=SafeCode.decode((String)this.getFormHM().get("formula"));
			formula=PubFunc.keyWord_reback(formula);
			String heapFlag=(String)this.getFormHM().get("heapFlag");
			
			//----------------------------------------修改薪资项目表达式 变动日志  zhaoxg add 2015-4-29--------
			ContentDAO dao=new ContentDAO(this.frameconn);
			RecordVo vo=new RecordVo("salaryset");
			vo.setInt("salaryid",Integer.parseInt(salaryid));
			vo.setInt("fieldid",Integer.parseInt(fieldid));
			vo=dao.findByPrimaryKey(vo);
			String _flag = vo.getString("initflag");
			SalaryTemplateBo bo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.getUserView());
			String _formula = vo.getString("formula");
			_formula = _formula.replaceAll("\r\n", "<br>").replaceAll("\n", "<br>");//库里面查询出来的回车符可能是\n也可能是\r\n 此处统一把二者转成<br>  防止ajax报错以及equals比对错误 zhaoxg update
			String _formula1 = formula.replaceAll("\r\n", "<br>").replaceAll("\n", "<br>");
			if((_formula!=null&&!_formula.equals(_formula1))||(!_flag.equals(flag))){
				StringBuffer context = new StringBuffer();
				context.append("确定："+bo.getSalaryName(salaryid)+"("+salaryid+")修改薪资项目表达式<br>");
				context.append("<table>");
				context.append("<tr>");
				context.append("<td>属性名</td>");
				context.append("<td>变化前</td>");
				context.append("<td>变化后</td>");
				context.append("</tr>");
				if(_formula!=null&&!_formula.equals(_formula1)){
					context.append("<tr>");
					context.append("<td>"+vo.getString("itemdesc")+"</td>");
					context.append("<td>"+_formula.replaceAll("\"", "＂")+"</td>");
					context.append("<td>"+_formula1.replaceAll("\"", "＂")+"</td>");
					context.append("</tr>");
				}
				if(!_flag.equals(flag)){
					context.append("<tr>");
					context.append("<td>"+vo.getString("itemdesc")+"的处理方式</td>");
					context.append("<td>"+flagToContent(_flag)+"</td>");
					context.append("<td>"+flagToContent(flag)+"</td>");
					context.append("</tr>");
				}

				context.append("</table>");
				this.getFormHM().put("@eventlog", context.toString());
			}else{
				this.getFormHM().put("@eventlog", "确定："+bo.getSalaryName(salaryid)+"("+salaryid+")修改薪资项目表达式,但没修改具体内容");
			}
			//----------------------------------------------------------------------------------------------
			bo.updateSalarySetFlag(fieldid,flag,formula,heapFlag);
			this.getFormHM().put("fieldid",fieldid);
			this.getFormHM().put("flag",flag);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
/**
 * 代码转汉字
 * @param flag
 * @return
 */	
public String flagToContent(String flag){
	String str = "";
	try{
		if("0".equals(flag)){
			str="输入项";
		}
		if("1".equals(flag)){
			str="累计项";
		}
		if("2".equals(flag)){
			str="导入项";
		}
	}catch(Exception e)
	{
		e.printStackTrace();
	}
	return str;
}
}
