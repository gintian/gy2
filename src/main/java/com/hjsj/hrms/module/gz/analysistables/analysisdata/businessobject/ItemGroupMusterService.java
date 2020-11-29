package com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject;

import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;
import java.util.HashMap;

public interface ItemGroupMusterService {

	ArrayList getRecordList(HashMap paramMap);

	ArrayList getEchartDataList(ArrayList recordList, ArrayList headList);

	ArrayList<ColumnsInfo> getItemGroupMusterColumnsInfo(UserView userView, String rsid, String rsdtlid);

	ArrayList getTableHeadlist(String rsdtlid, String rsid, String string, String string2, UserView userView);

	int[] getChartTextaera(ArrayList recordList, ArrayList<ColumnsInfo> column);

	
}
