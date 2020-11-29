/*
 * Simplified Chinese translation
 * By DavidHu
 * 09 April 2007
 */

Ext.UpdateManager.defaults.indicatorText = '<div class="loading-indicator">鍔犺浇涓�...</div>';

if(Ext.View){
   Ext.View.prototype.emptyText = "";
}

if(Ext.grid.Grid){
   Ext.grid.Grid.prototype.ddText = "{0} 閫夋嫨琛�";
}

if(Ext.TabPanelItem){
   Ext.TabPanelItem.prototype.closeText = "鍏抽棴";
}

if(Ext.form.Field){
   Ext.form.Field.prototype.invalidText = "杈撳叆鍊奸潪娉�";
}

Date.monthNames = [
   "涓�鏈�",
   "浜屾湀",
   "涓夋湀",
   "鍥涙湀",
   "浜旀湀",
   "鍏湀",
   "涓冩湀",
   "鍏湀",
   "涔濇湀",
   "鍗佹湀",
   "鍗佷竴鏈�",
   "鍗佷簩鏈�"
];

Date.dayNames = [
   "鏃�",
   "涓�",
   "浜�",
   "涓�",
   "鍥�",
   "浜�",
   "鍏�"
];

if(Ext.MessageBox){
   Ext.MessageBox.buttonText = {
      ok     : "纭畾",
      cancel : "鍙栨秷",
      yes    : "鏄�",
      no     : "鍚�"
   };
}

if(Ext.util.Format){
   Ext.util.Format.date = function(v, format){
      if(!v) return "";
      if(!(v instanceof Date)) v = new Date(Date.parse(v));
      return v.dateFormat(format || "y骞磎鏈坉鏃�");
   };
}

if(Ext.DatePicker){
   Ext.apply(Ext.DatePicker.prototype, {
      todayText         : "浠婂ぉ",
      minText           : "鏃ユ湡鍦ㄦ渶灏忔棩鏈熶箣鍓�",
      maxText           : "鏃ユ湡鍦ㄦ渶澶ф棩鏈熶箣鍚�",
      disabledDaysText  : "",
      disabledDatesText : "",
      monthNames	: Date.monthNames,
      dayNames		: Date.dayNames,      
      nextText          : '涓嬫湀 (Control+Right)',
      prevText          : '涓婃湀 (Control+Left)',
      monthYearText     : '閫夋嫨涓�涓湀 (Control+Up/Down 鏉ユ敼鍙樺勾)',
      todayTip          : "{0} (绌烘牸閿�夋嫨)",
      format            : "y骞磎鏈坉鏃�"
   });
}

if(Ext.PagingToolbar){
   Ext.apply(Ext.PagingToolbar.prototype, {
      beforePageText : "椤�",
   	  afterPageText  : "椤靛叡 {0} 椤�", 
      firstText      : "绗竴椤�",
      prevText       : "鍓嶄竴椤�",
      nextText       : "涓嬩竴椤�",
      lastText       : "鏈�鍚庨〉",
      refreshText    : "鍒锋柊",
	  displayMsg     : "鏄剧ず {0} - {1}锛屽叡 {2} 鏉�",
      emptyMsg       : '娌℃湁鏁版嵁闇�瑕佹樉绀�'
   });
}

if(Ext.form.TextField){
   Ext.apply(Ext.form.TextField.prototype, {
      minLengthText : "璇ヨ緭鍏ラ」鐨勬渶灏忛暱搴︽槸 {0}",
      maxLengthText : "璇ヨ緭鍏ラ」鐨勬渶澶ч暱搴︽槸 {0}",
      blankText     : "璇ヨ緭鍏ラ」涓哄繀杈撻」",
      regexText     : "",
      emptyText     : null
   });
}

if(Ext.form.NumberField){
   Ext.apply(Ext.form.NumberField.prototype, {
      minText : "璇ヨ緭鍏ラ」鐨勬渶灏忓�兼槸 {0}",
      maxText : "璇ヨ緭鍏ラ」鐨勬渶澶у�兼槸 {0}",
      nanText : "{0} 涓嶆槸鏈夋晥鏁板��"
   });
}

if(Ext.form.DateField){
   Ext.apply(Ext.form.DateField.prototype, {
      disabledDaysText  : "绂佺敤",
      disabledDatesText : "绂佺敤",
      minText           : "璇ヨ緭鍏ラ」鐨勬棩鏈熷繀椤诲湪 {0} 涔嬪悗",
      maxText           : "璇ヨ緭鍏ラ」鐨勬棩鏈熷繀椤诲湪 {0} 涔嬪墠",
      invalidText       : "{0} 鏄棤鏁堢殑鏃ユ湡 - 蹇呴』绗﹀悎鏍煎紡锛� {1}",
      format            : "y骞磎鏈坉鏃�"
   });
}

if(Ext.form.ComboBox){
   Ext.apply(Ext.form.ComboBox.prototype, {
      loadingText       : "鍔犺浇...",
      valueNotFoundText : undefined
   });
}

if(Ext.form.VTypes){
   Ext.apply(Ext.form.VTypes, {
      emailText    : '璇ヨ緭鍏ラ」蹇呴』鏄數瀛愰偖浠跺湴鍧�锛屾牸寮忓锛� "user@domain.com"',
      urlText      : '璇ヨ緭鍏ラ」蹇呴』鏄疷RL鍦板潃锛屾牸寮忓锛� "http:/'+'/www.domain.com"',
      alphaText    : '璇ヨ緭鍏ラ」鍙兘鍖呭惈瀛楃鍜宊',
      alphanumText : '璇ヨ緭鍏ラ」鍙兘鍖呭惈瀛楃,鏁板瓧鍜宊'
   });
}

if(Ext.grid.GridView){
   Ext.apply(Ext.grid.GridView.prototype, {
      sortAscText  : "姝ｅ簭",
      sortDescText : "閫嗗簭",
      lockText     : "閿佸垪",
      unlockText   : "瑙ｉ攣鍒�",
      columnsText  : "鍒�"
   });
}

if(Ext.grid.PropertyColumnModel){
   Ext.apply(Ext.grid.PropertyColumnModel.prototype, {
      nameText   : "鍚嶇О",
      valueText  : "鍊�",
      dateFormat : "y骞磎鏈坉鏃�"
   });
}

if(Ext.layout.BorderLayout.SplitRegion){
   Ext.apply(Ext.layout.BorderLayout.SplitRegion.prototype, {
      splitTip            : "鎷栧姩鏉ユ敼鍙樺昂瀵�.",
      collapsibleSplitTip : "鎷栧姩鏉ユ敼鍙樺昂瀵�. 鍙屽嚮闅愯棌."
   });
}
