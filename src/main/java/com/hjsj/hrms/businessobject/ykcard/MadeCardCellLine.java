/*
 * Created on 2006-4-25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.businessobject.ykcard;

import com.hjsj.hrms.valueobject.ykcard.RGridView;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MadeCardCellLine {
	
	public String GetReportCellLineShowcss(String l,String r,String t,String b)
	{
		String recordrowcss="RecordRow_self";
		if("0".equals(l) && "0".equals(r)&& "0".equals(t)&& "0".equals(b))
		{
			recordrowcss="RecordRow_self_lrtb";
		}else if("0".equals(l) && "0".equals(r)&& "0".equals(t))
		{
			recordrowcss="RecordRow_self_lrt";
		}else if("0".equals(l) && "0".equals(r)&& "0".equals(b))
		{
			recordrowcss="RecordRow_self_lrb";
		}else if("0".equals(l) && "0".equals(t) && "0".equals(b))
		{
			recordrowcss="RecordRow_self_ltb";
		}else if("0".equals(t) && "0".equals(r) && "0".equals(b))
		{
			recordrowcss="RecordRow_self_rtb";
		}else if("0".equals(l) && "0".equals(r))
		{
			recordrowcss="RecordRow_self_lr";
		}else if("0".equals(l) && "0".equals(t))
		{
			recordrowcss="RecordRow_self_lt";
		}else if("0".equals(l) && "0".equals(b))
		{
			recordrowcss="RecordRow_self_lb";
		}else if("0".equals(t) && "0".equals(r))
		{
			recordrowcss="RecordRow_self_rt";
		}
		else if("0".equals(b) && "0".equals(r))
		{
			recordrowcss="RecordRow_self_rb";
		}		
		else if("0".equals(t)  && "0".equals(b))
		{
			recordrowcss="RecordRow_self_tb";
		}else if("0".equals(l))
		{
			recordrowcss="RecordRow_self_l";
		}else if("0".equals(r))
		{
			recordrowcss="RecordRow_self_r";
		}else if("0".equals(t))
		{
			recordrowcss="RecordRow_self_t";
		}else if("0".equals(b))
		{
			recordrowcss="RecordRow_self_b";
		}		
		
		
		return recordrowcss;
	}
	
	/****
	 * 设置单元格边框是否显示 边框粗细
	 * */
	public String GetCardCellLineShowcss(RGridView rgridView) {
		String lSize=rgridView.getLsize();
		String tSize=rgridView.getTsize();
		String bSize=rgridView.getBsize();
		String rSize=rgridView.getRsize();
		StringBuilder sbl=new StringBuilder();
		if("2".equals(lSize)) {
            sbl.append(";BORDER-LEFT-Width:2pt");
        }
		if("2".equals(tSize)) {
            sbl.append(";BORDER-TOP-Width:2pt");
        }
		if("2".equals(bSize)) {
            sbl.append(";BORDER-BOTTOM-Width:2pt");
        }
		if("2".equals(rSize)) {
            sbl.append(";BORDER-RIGHT-Width:2pt");
        }
		
		return sbl.toString();
	}
	
	public String GetCardCellLineShowcss(String l,String r,String t,String b)
	{
		String recordrowcss="RecordRow_ltbr";
		if("0".equals(l) && "0".equals(r)&& "0".equals(t)&& "0".equals(b))
		{
			recordrowcss="RecordRow_self_lrtb";
		}else if("0".equals(l) && "0".equals(r)&& "0".equals(t))
		{
			recordrowcss="RecordRow_self_lrt";
		}else if("0".equals(l) && "0".equals(r)&& "0".equals(b))
		{
			recordrowcss="RecordRow_self_lrb";
		}else if("0".equals(l) && "0".equals(t) && "0".equals(b))
		{
			recordrowcss="RecordRow_self_ltb";
		}else if("0".equals(t) && "0".equals(r) && "0".equals(b))
		{
			recordrowcss="RecordRow_self_rtb";
		}else if("0".equals(l) && "0".equals(r))
		{
			recordrowcss="RecordRow_self_lr";
		}else if("0".equals(l) && "0".equals(t))
		{
			recordrowcss="RecordRow_self_lt";
		}else if("0".equals(l) && "0".equals(b))
		{
			recordrowcss="RecordRow_self_lb";
		}else if("0".equals(t) && "0".equals(r))
		{
			recordrowcss="RecordRow_self_rt";
		}
		else if("0".equals(b) && "0".equals(r))
		{
			recordrowcss="RecordRow_self_rb";
		}		
		else if("0".equals(t)  && "0".equals(b))
		{
			recordrowcss="RecordRow_self_tb";
		}else if("0".equals(l))
		{
			recordrowcss="RecordRow_self_l";
		}else if("0".equals(r))
		{
			recordrowcss="RecordRow_self_r";
		}else if("0".equals(t))
		{
			recordrowcss="RecordRow_self_t";
		}else if("0".equals(b))
		{
			recordrowcss="RecordRow_self_b";
		}		
		
		
		return recordrowcss;
	}
	
	public String GetCardCellLineShowcss(String l,String r,String t,String b,String hl,String vl)
	{
		String recordrowcss="RecordRow_ltbr";
		if(hl!=null&&!"true".equals(hl)) {
            t=b="0";
        }
		if(vl!=null&&!"true".equals(vl)) {
            l=r="0";
        }
		if("0".equals(l) && "0".equals(r)&& "0".equals(t)&& "0".equals(b))
		{
			recordrowcss="RecordRow_self_lrtb";
		}else if("0".equals(l) && "0".equals(r)&& "0".equals(t))
		{
			recordrowcss="RecordRow_self_lrt";
		}else if("0".equals(l) && "0".equals(r)&& "0".equals(b))
		{
			recordrowcss="RecordRow_self_lrb";
		}else if("0".equals(l) && "0".equals(t) && "0".equals(b))
		{
			recordrowcss="RecordRow_self_ltb";
		}else if("0".equals(t) && "0".equals(r) && "0".equals(b))
		{
			recordrowcss="RecordRow_self_rtb";
		}else if("0".equals(l) && "0".equals(r))
		{
			recordrowcss="RecordRow_self_lr";
		}else if("0".equals(l) && "0".equals(t))
		{
			recordrowcss="RecordRow_self_lt";
		}else if("0".equals(l) && "0".equals(b))
		{
			recordrowcss="RecordRow_self_lb";
		}else if("0".equals(t) && "0".equals(r))
		{
			recordrowcss="RecordRow_self_rt";
		}
		else if("0".equals(b) && "0".equals(r))
		{
			recordrowcss="RecordRow_self_rb";
		}		
		else if("0".equals(t)  && "0".equals(b))
		{
			recordrowcss="RecordRow_self_tb";
		}else if("0".equals(l))
		{
			recordrowcss="RecordRow_self_l";
		}else if("0".equals(r))
		{
			recordrowcss="RecordRow_self_r";
		}else if("0".equals(t))
		{
			recordrowcss="RecordRow_self_t";
		}else if("0".equals(b))
		{
			recordrowcss="RecordRow_self_b";
		}		
		
		
		return recordrowcss;
	}
}
