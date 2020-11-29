package com.hjsj.hrms.module.gz.salarytype.transaction.salaryitem;

import com.hjsj.hrms.module.gz.salarytype.businessobject.SalaryTypeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 项目名称 ：ehr7.x
 * 类名称：ChangeSalarySetFlagTrans
 * 类描述：修改薪资项目标识
 * 创建人： lis
 * 创建时间：2015-11-6
 */
public class ChangeSalarySetFlagTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{
			String fieldid=(String)this.getFormHM().get("fieldid");
			String flag=(String)this.getFormHM().get("flag");
			String salaryid=(String)this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			String formula=SafeCode.decode((String)this.getFormHM().get("formula"));
			formula=formula.replaceAll("!","\r");
			formula=formula.replaceAll("`","\n");
			formula=PubFunc.keyWord_reback(formula);
			String heapFlag=(String)this.getFormHM().get("heapFlag");
			
			//----------------------------------------修改薪资项目表达式 变动日志  zhaoxg add 2015-4-29--------
			ContentDAO dao=new ContentDAO(this.frameconn);
			RecordVo vo=new RecordVo("salaryset");
			vo.setInt("salaryid",Integer.parseInt(salaryid));
			vo.setInt("fieldid",Integer.parseInt(fieldid));
			vo=dao.findByPrimaryKey(vo);
			String _flag = vo.getString("initflag");
			SalaryTypeBo bo=new SalaryTypeBo(this.getFrameconn(),this.getUserView());
			String _formula = vo.getString("formula");
			_formula = _formula.replaceAll("\r\n", "<br>").replaceAll("\n", "<br>");//库里面查询出来的回车符可能是\n也可能是\r\n 此处统一把二者转成<br>  防止ajax报错以及equals比对错误 zhaoxg update
			String _formula1 = formula.replaceAll("\r\n", "<br>").replaceAll("\n", "<br>");
			String ok = ResourceFactory.getProperty("button.ok");//确定
			if((_formula!=null&&!_formula.equals(_formula1))||(!_flag.equals(flag))){
				String editSalaryItem = ResourceFactory.getProperty("gz_new.gz_editSalaryItemCon");//修改薪资项目表达式
				StringBuffer context = new StringBuffer();
				context.append(ok + "："+bo.getSalaryName(salaryid)+"("+salaryid+")"+editSalaryItem+"<br>");
				context.append("<table>");
				context.append("<tr>");
				context.append("<td>"+ResourceFactory.getProperty("gz_new.gz_propertyName")+"</td>");//属性名
				context.append("<td>"+ResourceFactory.getProperty("gz.gz_acounting.change.before")+"</td>");//变化前
				context.append("<td>"+ResourceFactory.getProperty("gz.gz_acounting.change.affter")+"</td>");//变化后
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
					context.append("<td>"+vo.getString("itemdesc")+ResourceFactory.getProperty("gz_new.gz_processingMode")+"</td>");//的处理方式
					context.append("<td>"+flagToContent(_flag)+"</td>");
					context.append("<td>"+flagToContent(flag)+"</td>");
					context.append("</tr>");
				}

				context.append("</table>");
				this.getFormHM().put("@eventlog", context.toString());
			}else{
				this.getFormHM().put("@eventlog", ok + "："+bo.getSalaryName(salaryid)+"("+salaryid+")"+ResourceFactory.getProperty("gz_new.gz_editNoContent"));//修改薪资项目表达式,但没修改具体内容
			}
			//----------------------------------------------------------------------------------------------
			bo.updateSalarySetFlag(Integer.valueOf(salaryid),fieldid,flag,formula,heapFlag);
			this.getFormHM().put("fieldid",fieldid);
			this.getFormHM().put("flag",flag);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
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
			str=ResourceFactory.getProperty("gz_new.gz_inputItem");//输入项
		}
		if("1".equals(flag)){
			str=ResourceFactory.getProperty("gz_new.gz_cumulateItem");//累计项
		}
		if("2".equals(flag)){
			str=ResourceFactory.getProperty("gz_new.gz_importItem");//导入项
		}
	}catch(Exception e)
	{
		e.printStackTrace();
	}
	return str;
}
}
