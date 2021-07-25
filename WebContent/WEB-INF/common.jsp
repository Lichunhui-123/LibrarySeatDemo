<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<!-- 使用新的标签库 -->
<%@ taglib uri="/priveliege" prefix="privilege" %>
<!-- 标签库别名为privilege，自定义uri为priveliege -->


<%
	/**-====================================================================
	*                               基本常量的设定
	*=====================================================================**/
	//设定context path
	String path = request.getContextPath();  //为了解决相对路径的问题，可返回站点的根路径
	if("/".equals(path.trim())) path = "";//你的工程文件就是在根目录下，相当于path=""
	pageContext.setAttribute("path",path);
%>
<!-- CSS -->
<link rel="stylesheet" type="text/css" href="${path }/jquery-easyui-1.3.3/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="${path }/jquery-easyui-1.3.3/themes/icon.css">
<link rel="stylesheet" type="text/css" href="${path }/style/main.css">



<style>
/*座位样式*/
/* .squaredOne */
.squaredOne {
  width: 28px;
  height: 28px;
  position: relative;
  margin: 20px auto;
  background: #fcfff4;
  background: -moz-linear-gradient(top, #fcfff4 0%, #dfe5d7 40%, #b3bead 100%);   /*背景*/
  background: -webkit-linear-gradient(top, #fcfff4 0%, #dfe5d7 40%, #b3bead 100%);
  background: linear-gradient(to bottom, #fcfff4 0%, #dfe5d7 40%, #b3bead 100%);
  -moz-box-shadow: inset 0px 1px 1px white, 0px 1px 3px rgba(0, 0, 0, 0.5);
  -webkit-box-shadow: inset 0px 1px 1px white, 0px 1px 3px rgba(0, 0, 0, 0.5);
  box-shadow: inset 0px 1px 1px white, 0px 1px 3px rgba(0, 0, 0, 0.5);  /*阴影*/
}
.squaredOne label {
  width: 20px;
  height: 20px;
  position: absolute;
  top: 4px;
  left: 4px;
  cursor: pointer;
  background: -moz-linear-gradient(top, #222222 0%, #45484d 100%);
  background: -webkit-linear-gradient(top, #222222 0%, #45484d 100%);
  background: linear-gradient(to bottom, #222222 0%, #45484d 100%);
  -moz-box-shadow: inset 0px 1px 1px rgba(0, 0, 0, 0.5), 0px 1px 0px white;
  -webkit-box-shadow: inset 0px 1px 1px rgba(0, 0, 0, 0.5), 0px 1px 0px white;
  box-shadow: inset 0px 1px 1px rgba(0, 0, 0, 0.5), 0px 1px 0px white;
}
.squaredOne label:after {
  content: '';
  width: 16px;
  height: 16px;
  position: absolute;
  top: 2px;
  left: 2px;
  background: #00bf00;
  background: -moz-linear-gradient(top, #00bf00 0%, #009400 100%);
  background: -webkit-linear-gradient(top, #00bf00 0%, #009400 100%);
  background: linear-gradient(to bottom, #00bf00 0%, #009400 100%);
  -moz-box-shadow: inset 0px 1px 1px white, 0px 1px 3px rgba(0, 0, 0, 0.5);
  -webkit-box-shadow: inset 0px 1px 1px white, 0px 1px 3px rgba(0, 0, 0, 0.5);
  box-shadow: inset 0px 1px 1px white, 0px 1px 3px rgba(0, 0, 0, 0.5);
  filter: progid:DXImageTransform.Microsoft.Alpha(Opacity=0);
  opacity: 0;
}
.squaredOne label:hover::after {
  filter: progid:DXImageTransform.Microsoft.Alpha(Opacity=30);
  opacity: 0.3;
}
.squaredOne input[type=checkbox] {
  visibility: hidden;
}
.squaredOne input[type=checkbox]:checked + label:after {
  filter: progid:DXImageTransform.Microsoft.Alpha(enabled=false);
  opacity: 1;
}

/* end .squaredOne */
</style>

<!-- JAVASCRIPT -->
<script type="text/javascript" src="${path }/jquery-easyui-1.3.3/jquery.min.js"></script>
<script type="text/javascript" src="${path }/jquery-easyui-1.3.3/jquery.easyui.min.js"></script>
<script type="text/javascript" src="${path }/jquery-easyui-1.3.3/locale/easyui-lang-zh_CN.js"></script>

<script type="text/javascript" src="${path }/jquery-easyui-1.3.3/kz.js" ></script>
<!-- 文本编辑器ueditorjs引入 -->
<script type="text/javascript" charset="utf-8" src="${path }/ueditor1.4.3/ueditor.config.js"></script>
<script type="text/javascript" charset="utf-8" src="${path }/ueditor1.4.3/ueditor.all.min.js"> </script>
<script type="text/javascript" charset="utf-8" src="${path }/ueditor1.4.3/lang/zh-cn/zh-cn.js"></script>
