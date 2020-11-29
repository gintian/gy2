/**
 * @license Copyright (c) 2003-2016, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here. For example:
	// config.language = 'fr';
	// config.uiColor = '#AADC6E';
    config.font_names='宋体/宋体;黑体/黑体;仿宋/仿宋_GB2312;楷体/楷体_GB2312;隶书/隶书;幼圆/幼圆;微软雅黑/微软雅黑;'+ config.font_names;
    config.filebrowserImageUploadUrl="/ckeditor/uploader?type=Image";
    config.filebrowserUploadUrl = "/ckeditor/uploader?type=File";
    config.filebrowserFlashUploadUrl = "/ckeditor/uploader?type=Flash";
    config.extraPlugins += (config.extraPlugins ? ',lineheight' : 'lineheight');
	config.allowedContent = true;
};
