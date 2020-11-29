package com.hjsj.hrms.servlet.reportprint;

import com.hjsj.hrms.actionform.report.report_collect.IntegrateTableForm;
import com.hjsj.hrms.businessobject.report.ReportPrint;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.reportCollect.IntegrateTableBo;
import com.hjsj.hrms.businessobject.report.reportCollect.IntegrateTableHtmlBo;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.interfaces.report.ReportParseXml;
import com.hjsj.hrms.transaction.report.reportdisk.UpDisk;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class IntegratePrint extends HttpServlet {

	public IntegratePrint() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//tabid=8
		//condition=UN:集团总公司:'01':0:`UN:北京A分公司:'0101':0:`UN:河北分公司:'0102':0:
		//username=su
		//nums=b1
		//unitcode=01
		//cols=3

		IntegrateTableForm itf = (IntegrateTableForm)request.getSession().getAttribute("integrateTableForm");
		UserView userview = (UserView)request.getSession().getAttribute(WebConstant.userView);
	
		String tabid = itf.getTabid(); //表号
		String str = itf.getCondition();//条件
		
		String[] condition = str.split("`");
		String nums = itf.getNums();//a选中列 b 选中行
		String username = request.getParameter("username");//用户
		
		String cols = itf.getCols();		//列
		String unitcode = itf.getUnitcode();//填报单位
		if(unitcode!=null){
			unitcode=PubFunc.keyWord_reback(unitcode);
		}
		if(cols!=null){
			cols=PubFunc.keyWord_reback(cols);
		}
		if(str!=null){
			str=PubFunc.keyWord_reback(str);
		}
		if(condition!=null){
			for(int i=0;i<condition.length;i++){
				
				condition[i]=PubFunc.keyWord_reback(condition[i]);
			}
		}
		//改为下载pdf;
		response.setHeader("Content-Disposition", 
                "attachment;filename=IntegratePrint.pdf");
		OutputStream out = response.getOutputStream();
		Connection con = null;
		RowSet rs = null;
		try {
			con = (Connection) AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(con);//综合表页面样式
			rs = dao.search("select xmlstyle, name from tname where tabid="
							+ tabid);
			String xmlContent = "";
			if (rs.next()) {
				xmlContent = rs.getString(1);
			}
			
			/**报表打印页面设置处理*/
			ReportParseVo paramtervo = null; //页面设置对象
			ReportParseXml parseXml = new ReportParseXml();//页面设置XML解析器
			if (xmlContent != null) {
				paramtervo = new ReportParseVo();
				paramtervo = parseXml.ReadOutParseXml(xmlContent, "/report");
			}
			
			String sortid = UpDisk.getSortId(con, tabid); //获得报表表类ID
			
			//综合表数据集合
			IntegrateTableBo integrateTableBo = new IntegrateTableBo(con);
			ArrayList resultList = integrateTableBo.getIntegrateTableData(
					condition, tabid, sortid, nums, unitcode, Integer.parseInt(cols));
			
			// 根据条件生成新的报表格式信息 [0]:a_rowInfoBGrid [1]:b_colInfoBGrid [2]:a_gridList
			TnameBo namebo = new TnameBo(tabid,unitcode,username,"",con); // new TnameBo(con, tabid, "", username, "");
			namebo.setUserview(userview);
			IntegrateTableHtmlBo bo = new IntegrateTableHtmlBo();
			ArrayList ar = bo.getNewGridList(namebo, nums, condition);
			
			ReportPrint report_print = new ReportPrint(namebo, con, "");
			report_print.setResultList(resultList);
			
			int left_pyl = 0;
			int top_pyl = 0;
			if (paramtervo != null) {
				RecordVo rvo = namebo.getTnameVo();
				if (!"".equals(paramtervo.getLeft())) { //左边距
					int left = (int) Float.parseFloat(paramtervo.getLeft());	
					int b_left = rvo.getInt("lmargin");
					left_pyl=left-b_left;
				}	
				
				if (!"".equals(paramtervo.getTop())) {//顶边距
					int  top = (int) Float.parseFloat(paramtervo.getTop());
					int b_top = rvo.getInt("tmargin");
					top_pyl = top - b_top;
				}
			
			}
			report_print.setIntegrate_left_pyl(left_pyl);
			report_print.setIntegrate_top_pyl(top_pyl);
			ArrayList rowList = (ArrayList) ar.get(0);
			ArrayList colList = (ArrayList) ar.get(1);
			ArrayList gridList = (ArrayList) ar.get(2);
			for(int i=0; i< gridList.size(); i++){
				RecordVo vo = (RecordVo) gridList.get(i);
				int rleft = vo.getInt("rleft");
				int rtop = vo.getInt("rtop");
				vo.setInt("rleft",rleft+left_pyl);
				vo.setInt("rtop",rtop+top_pyl);
				
				rleft = vo.getInt("rleft");
				rtop = vo.getInt("rtop");
			}
			
			int [] itemGridArea = namebo.getItemGridArea();//表项目区域 l,t,w,h
			itemGridArea[0] = itemGridArea[0]+ left_pyl;
			itemGridArea[1] =itemGridArea[1]+top_pyl;
			report_print.setItemGridArea(itemGridArea);
			
			report_print.setRowInfoBGrid(rowList);//a_rowInfoBGrid
			report_print.setColInfoBGrid(colList);//b_colInfoBGrid
			report_print.setGridList(gridList);//a_gridList
			
			if (paramtervo != null) {
				if (!"".equals(paramtervo.getLeft())) { //左边距
					report_print.setIntegrate_l_margin(Float
							.parseFloat(paramtervo.getLeft()));
				}
				if (!"".equals(paramtervo.getRight())) {//右边距
					report_print.setIntegrate_r_margin(Float
							.parseFloat(paramtervo.getRight()));
				}
				if (!"".equals(paramtervo.getTop())) {//顶边距
					report_print.setIntegrate_t_margin(Float
							.parseFloat(paramtervo.getTop()));
				}
				if (!"".equals(paramtervo.getBottom())) {//底边距
					report_print.setIntegrate_b_margin(Float
							.parseFloat(paramtervo.getBottom()));
				}
				if (!"".equals(paramtervo.getHeight())) {//高度
					report_print.setPage_height(Float.parseFloat(paramtervo
							.getHeight()));
				}
				if (!"".equals(paramtervo.getWidth())) {//宽度
					report_print.setPage_width(Float.parseFloat(paramtervo
							.getWidth()));
				}
				if (!"".equals(paramtervo.getOrientation())) {
					report_print.setIntegeratePaperOri(paramtervo
							.getOrientation());
				}
				// 设置内容
				if (!"".equals(paramtervo.getTitle_fw())) {
					report_print.setIntegerateTitle(paramtervo.getTitle_fw());
				}
				// 设置字体大小
				if (!"".equals(paramtervo.getTitle_fz())) {
					report_print
							.setIntegerateFontSize(paramtervo.getTitle_fz());
				}
				// 设置字体名称
				if (!"".equals(paramtervo.getTitle_fn())) {
					report_print
							.setIntegerateFontName(paramtervo.getTitle_fn());
				}
				// 设置字体效果是否具有斜体牲
				if (!"".equals(paramtervo.getTitle_fi())) {
					report_print.setIntegerateFontItalic(paramtervo
							.getTitle_fi());
				}
				// 设置字体效果是否具有粗体特征
				if (!"".equals(paramtervo.getTitle_fb())) {
					report_print
							.setIntegerateFontBold(paramtervo.getTitle_fb());
				}
				// 设置字体效果是否具有删除线特征
				if (!"".equals(paramtervo.getTitle_fs())) {
					report_print.setIntegerateFontStrike(paramtervo
							.getTitle_fs());
				}
				// 设置字体效果是否具有下划线特征
				if (!"".equals(paramtervo.getTitle_fu())) {
					report_print.setIntegerateFontUnderLine(paramtervo
							.getTitle_fu());
				}
				// 设置字体颜色
				if (!"".equals(paramtervo.getTitle_fc())) {
					report_print.setIntegerateFontColor(paramtervo
							.getTitle_fc());
				}

				// 设置上左内容
				if (!"".equals(paramtervo.getHead_flw())) {
					report_print
							.setIntegerateHead_flw(paramtervo.getHead_flw());
				}
				// 设置上中内容
				if (!"".equals(paramtervo.getHead_fmw())) {
					report_print
							.setIntegerateHead_fmw(paramtervo.getHead_fmw());
				}
				// 设置上右内容
				if (!"".equals(paramtervo.getHead_frw())) {
					report_print
							.setIntegerateHead_frw(paramtervo.getHead_frw());
				}

				// 设置表头包括上左、上中、上右附标题字体大小
				if (!"".equals(paramtervo.getHead_fz())) {
					report_print.setIntegerateHeadFontSize(paramtervo
							.getHead_fz());
				}
				// 设置表头包括上左、上中、上右附标题字体名称
				if (!"".equals(paramtervo.getHead_fn())) {
					report_print.setIntegerateHeadFontName(paramtervo
							.getHead_fn());
				}
				// 设置字体效果是否具有斜体牲
				if (!"".equals(paramtervo.getHead_fi())) {
					report_print.setIntegerateHeadFontItalic(paramtervo
							.getHead_fi());
				}
				// 设置字体效果是否具有粗体特征
				if (!"".equals(paramtervo.getHead_fb())) {
					report_print.setIntegerateHeadFontBold(paramtervo
							.getHead_fb());
				}
				// 设置字体效果是否具有删除线特征
				if (!"".equals(paramtervo.getHead_fs())) {
					report_print.setIntegerateHeadFontStrike(paramtervo
							.getHead_fs());
				}
				// 设置字体效果是否具有下划线特征
				if (!"".equals(paramtervo.getHead_fu())) {
					report_print.setIntegerateHeadFontUnderLine(paramtervo
							.getHead_fu());
				}
				// 设置字体颜色
				if (!"".equals(paramtervo.getHead_fc())) {
					report_print.setIntegerateHeadFontColor(paramtervo
							.getHead_fc());
				}

				// 设置表体的字体名称
				if (!"".equals(paramtervo.getBody_fn())) {
					report_print.setIntegerateBodyFontName(paramtervo
							.getBody_fn());
				}
				// 设置表体的字体效果是否具有斜体牲
				if (!"".equals(paramtervo.getBody_fi())) {
					report_print.setIntegerateBodyFontItalic(paramtervo
							.getBody_fi());
				}
				// 设置表体的字体效果是否具有粗体特征
				if (!"".equals(paramtervo.getBody_fb())) {
					report_print.setIntegerateBodyFontBold(paramtervo
							.getBody_fb());
				}

				// 设置表体的字体效果是否具有下划线特征
				if (!"".equals(paramtervo.getBody_fu())) {
					report_print.setIntegerateBodyFontUnderLine(paramtervo
							.getBody_fu());
				}

				// 设置字体颜色
				if (!"".equals(paramtervo.getBody_fc())) {
					report_print.setIntegerateBodyFontColor(paramtervo
							.getBody_fc());
				}

				// 设置下表头
				// 设置下左内容
				if (!"".equals(paramtervo.getTile_flw())) {
					report_print.setIntegerateUnderHead_flw(paramtervo
							.getTile_flw());
				}
				// 设置下中内容
				if (!"".equals(paramtervo.getTile_fmw())) {
					report_print.setIntegerateUnderHead_fmw(paramtervo
							.getTile_fmw());
				}
				// 设置下右内容
				if (!"".equals(paramtervo.getTile_frw())) {
					report_print.setIntegerateUnderHead_frw(paramtervo
							.getTile_frw());
				}

				// 设置表头包括下左、下中、下右附标题字体大小
				if (!"".equals(paramtervo.getTile_fz())) {
					report_print.setIntegerateUnderHeadFontSize(paramtervo
							.getTile_fz());
				}
				// 设置表头包括上左、上中、上右附标题字体名称
				if (!"".equals(paramtervo.getTile_fn())) {
					report_print.setIntegerateUnderHeadFontName(paramtervo
							.getTile_fn());
				}
				// 设置字体效果是否具有斜体牲
				if (!"".equals(paramtervo.getTile_fi())) {
					report_print.setIntegerateUnderHeadFontItalic(paramtervo
							.getTile_fi());
				}
				// 设置字体效果是否具有粗体特征
				if (!"".equals(paramtervo.getTile_fb())) {
					report_print.setIntegerateUnderHeadFontBold(paramtervo
							.getHead_fb());
				}
				// 设置字体效果是否具有删除线特征
				if (!"".equals(paramtervo.getTile_fs())) {
					report_print.setIntegerateUnderHeadFontStrike(paramtervo
							.getTile_fs());
				}
				// 设置字体效果是否具有下划线特征
				if (!"".equals(paramtervo.getTile_fu())) {
					report_print.setIntegerateUnderHeadFontUnderLine(paramtervo
							.getTile_fu());
				}
				// 设置字体颜色
				if (!"".equals(paramtervo.getTile_fc())) {
					report_print.setIntegerateUnderHeadFontColor(paramtervo
							.getTile_fc());
				}
				
				//如果用户自定义了页面大小和边距
				report_print.setZflg(true); //已用户自定义页面设置
				report_print.setPageList(new ArrayList());
			}else{ 
				report_print.setZflg(false);//系统默认页面设置
			//	report_print.setPageList(new ArrayList());
			}
			
			report_print.setIntegrateFlag(true);//综合表处理标识
			
			report_print.init();
			//report_print.createPDF(out);
			report_print.createZHBPDF(out);
			report_print.getDocument().close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if (con != null){
					con.close();
				}
			}catch (SQLException sql){
				sql.printStackTrace();
			}
			PubFunc.closeResource(rs);
			PubFunc.closeIoResource(out);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	public void init() throws ServletException {

	}

}
