package com.hjsj.hrms.transaction.sys.roleexcle;

import com.hjsj.hrms.businessobject.sys.rolemanagement.RoleManagementBo;
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
 * 
 * <p>Title:角色管理导出excel</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Nov 6, 2008:3:59:34 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class RoleExcelTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HSSFWorkbook workbook = null;
		try{
			String usename = (String)this.getFormHM().get("usename");
			RoleManagementBo role = new RoleManagementBo(this.getFrameconn());
			
			ArrayList toplist = role.roletop(); //role头信息
			ArrayList dbnamelist = role.getdbname(); //人员库信息
			//子集指标
			HashMap map=role.infomap();
			ArrayList classlist = (ArrayList)map.get("1");
			HashMap fielsetidmap = (HashMap)map.get("2");
			HashMap itemdescmap = (HashMap)map.get("3");
			ArrayList sublist = (ArrayList)map.get("4");
			//管理范围
			ArrayList listvalue = role.uuu();
			//功能权限
			ArrayList searchlist = role.searchFunctionXmlHtml();
			workbook= new HSSFWorkbook();   // 创建新的Excel 工作簿
			HSSFRow row = null;  //行
			HSSFCell cell=null;   //单元格
			HSSFComment comment=null;  //定义注释
			
			String outName="权限分配表模板_"+PubFunc.getStrg()+".xls";
			role.setxmlexcel(searchlist,workbook,row,cell,toplist);  //写入功能权限
			role.setStaff(toplist,dbnamelist,workbook,row,cell); //写入人员库信息;
			role.setmanagementexcel(toplist,listvalue,workbook, row, cell);//写入管理范围
			role.setinfoexcel(classlist, fielsetidmap, itemdescmap, workbook, row, cell,toplist,sublist);//写入子集指标
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outName);
			workbook.write(fileOut);
			fileOut.close();
			//outName=outName.replace(".xls","#");
			outName = PubFunc.encrypt(outName);
			this.getFormHM().put("outName",outName);
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(workbook);
		}
			
	}
	
}
