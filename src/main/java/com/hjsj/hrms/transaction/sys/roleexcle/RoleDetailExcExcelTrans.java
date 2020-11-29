package com.hjsj.hrms.transaction.sys.roleexcle;

import com.hjsj.hrms.businessobject.sys.rolemanagement.IndividualManagementBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 角色对象对应关系导出excel
 * @author xujian
 *Apr 13, 2010
 */
public class RoleDetailExcExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HSSFWorkbook workbook = null;
		try{
				System.gc();
				String role_id = (String)this.getFormHM().get("roleid");
				String roleid[]=role_id.split(",");
				if(roleid==null||roleid.length==0)
					  return;
				IndividualManagementBo ind = new IndividualManagementBo(this.getFrameconn());
				workbook= new HSSFWorkbook();   // 创建新的Excel 工作簿
				HSSFRow row = null;  //行
				HSSFCell cell=null;   //单元格
				HSSFComment comment=null;  //定义注释
				String outName="角色对象对应关系_"+PubFunc.getStrg()+".xls";
				//top黄色部分
				ArrayList topinfolist = ind.toprole(roleid);
				HashMap maprole=ind.getRoleDetail(roleid);//	导出角色的人员列表
				ind.creatRoleDetailSheet(topinfolist,maprole, workbook, row, cell);//	导出角色的人员列表
				FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outName);
				workbook.write(fileOut);
				fileOut.close();
				outName = PubFunc.encrypt(outName);
				this.getFormHM().put("outName",outName);
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(workbook);
		}
	}

}
