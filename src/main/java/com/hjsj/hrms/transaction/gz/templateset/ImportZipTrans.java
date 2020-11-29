package com.hjsj.hrms.transaction.gz.templateset;

import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hjsj.hrms.businessobject.gz.templateset.DownLoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:ImportZipTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 7, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class ImportZipTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String[] salarySetIDs=(String[])this.getFormHM().get("salarySetIDs");
			//salaryid`repeat`oldid, 
			String isrepeat = (String)this.getFormHM().get("isrepeat");
			FormFile form_file = (FormFile) getFormHM().get("file");
			String gz_module=(String)this.getFormHM().get("gz_module");
			SalaryPkgBo bo=new SalaryPkgBo(this.getFrameconn(),this.getUserView(),Integer.parseInt(gz_module));
		    bo.deleteRepeatRecord(isrepeat);
			ArrayList list=bo.getSalaryTemplateList(form_file,"salarytemplate.xml","salaryid","cname");
			HashMap map=getNewSalaryID(list,salarySetIDs,isrepeat);
			String[] arrids=(String[])map.get("1");//导入的类别
			HashMap hm=(HashMap)map.get("2");//覆盖导入，现类别号《====》原类别号
			bo.importPkg(form_file,arrids,hm);
			DownLoadXml dowmloadxml = new DownLoadXml();
			StringBuffer error1 = dowmloadxml.getError1();
			StringBuffer error2 = dowmloadxml.getError2();
			StringBuffer error = new StringBuffer("");
			if(error1!=null && !"".equals(error1.toString())){
				error.append("以下临时变量跟库中临时变量重名，未导入：");
				error.append("\r\n");
				error.append(error1);
				error.append("\r\n");
			}
			
			if(error2!=null && !"".equals(error2.toString())){
				error.append("以下临时变量编号跟库中临时变量编号重复，新增导入：");
				error.append("\r\n");
				error.append(error2);
			}
			if(error!=null&&error.length()>0){
				String filename = PubFunc.getTxtFile(error.toString());
				filename = PubFunc.encrypt(filename);
				filename = SafeCode.encode(filename);
				this.getFormHM().put("filename", filename);
			}else{
				this.getFormHM().put("filename", "");//如果没有内容，把名字置为空，否则会导出上次的那个txt  zhaoxg add 2015-2-16
			}
			dowmloadxml.setError1(new StringBuffer(""));
			dowmloadxml.setError2(new StringBuffer(""));
		    bo.saveSalarySetResource(salarySetIDs);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception("导入失败!"));
		}

	}
	/**
	 * 
	 * @param list
	 * @param ids
	 * @return
	 */
	public HashMap getNewSalaryID(ArrayList list,String[] ids,String isrepeat)
	{
		HashMap map = new HashMap();
		try
		{
			String[] ret= new String[ids.length];
			HashMap hm = new HashMap();
			//salaryid`repeat`oldid, salaryid`repeat`oldid
			String[] arr= isrepeat.substring(1).split(",");
			for(int i=0;i<ids.length;i++)//被选择的要导入的工资类别id
			{
				String[] temp=arr[i].split("`");
    			String salaryid = temp[0];
    			String repeatid=temp[1];
    			String oldid1="";
    			if(temp.length>2)
    				oldid1=temp[2];
    			if("1".equals(repeatid))//覆盖导入的类别
    			{
    				hm.put(salaryid, oldid1);
    			}   			
    			ret[i]=salaryid;
			}
			map.put("1",ret);
			map.put("2",hm);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

}
