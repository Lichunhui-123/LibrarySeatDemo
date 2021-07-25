<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/common.jsp"%>
<!DOCTYPE html>
<html>
  <head>
    <title>座位选择详情</title>
<script type="text/javascript">
//签到
function come() {
	var selectedRows = $("#dg").datagrid('getSelections');//获取选中的行
	if (selectedRows.length != 1) {
		$.messager.alert('系统提示', '请选择一条数据进行操作');
		return;
	}
	var row = selectedRows[0];
	//消息框messager
	$.messager.confirm("系统提示", "确定你来了吗？", function(r) {
		if (r) {//管理员点击确定
			$.post("${path}/illegal/addIllegal.htm", {
				studentno:row.studentno,seatkeyword:row.seatkeyword,status:row.status
			}, function(result) {
				if (result.success) {
					$.messager.alert('系统提示', result.success);
				} else {
					$.messager.alert('系统提示', result.errorMsg);
				}
			}, "json");
		}
	});
}

//走了就删除选座记录，并取消占座
function leave() {
	var selectedRows = $("#dg").datagrid('getSelections');//获取选中的行
	if (selectedRows.length != 1) {
		$.messager.alert('系统提示', '请选择一条数据进行操作');
		return;
	}
	var row = selectedRows[0];
	//消息框messager
	$.messager.confirm("系统提示", "确定你要走了吗？", function(r) {
		if (r) {//管理员点击确定
			$.post("${path}/seat/cancelSeat.htm", {
				studentno:row.studentno,seatkeyword:row.seatkeyword
			}, function(data) {
				if(data=="ok"){//取消成功
					$.messager.alert('系统提示',"再见，祝你心情愉快！");
					$("#dg").datagrid("reload");
				} else{//取消失败
					$.messager.alert('系统提示',result);return;
				}
			});
		}
	});
}
</script>
</head>
<body style="margin: 1px;">

<div id="tb">
	<div class="updownInterval"> </div>
	<div>
		<a href="javascript:come()" class="easyui-linkbutton" plain="ture" iconCls="icon-ok" >我來了</a>
		<a href="javascript:leave()" class="easyui-linkbutton" plain="ture" iconCls="icon-cancel" >我走了</a>
	</div>
	<div class="updownInterval"> </div>
</div>

<!-- 剩余座位数据列表 -->
	<table id="dg" title="座位选择信息 " class="easyui-datagrid" fitColumns="true" 
    pagination="true" rownumbers="true" url="${path }/choice/choiceList.htm" fit="true" toolbar="#tb">
    <thead>
    	<tr>
    	    <th field="ck" checkbox="true" align="center"></th>
    		<th field="id" width="50" align="center" data-options="hidden:true"></th>
    		<th field="studentno" width="60" align="center">学号</th>
    		<th field="seatkeyword" width="60" align="center">关键字</th>
    		<th field="time" width="60" align="center">时间</th>
    		<th field="status" width="60" align="center">状态</th>
    	</tr>
    </thead>
</table>
  	
</body>
</html>