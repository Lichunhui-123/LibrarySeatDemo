<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/common.jsp"%>
<!DOCTYPE html>
<html>
  <head>
    <title>阅览室类型管理</title>
<script type="text/javascript">
	var url;
	
	//打开添加阅览室类型对话框
	function openTypeAddDialog() {
		$("#dlg").dialog("open").dialog("setTitle", "添加类型信息");
		url = "reserveType.htm";
	}
    
	//打开修改阅览室类型对话框
	function openTypeModifyDialog() {
		var selectedRows = $("#dg").datagrid('getSelections');//获取选中的行
		if (selectedRows.length != 1) {
			$.messager.alert('系统提示', '请选择一条数据进行操作');
			return;
		}
		var row = selectedRows[0];
		$("#dlg").dialog("open").dialog("setTitle", "修改类型信息");
		$("#fm").form("load", row);//数据回显，把获取的row显示在form表单中
		url = "reserveType.htm?id=" + row.id;
	}

	//保存阅览室类型
	function saveType() {
		//表单提交
		$("#fm").form("submit",
				{
					url : url,
					//提交之前的回调函数
					onSubmit : function() {
						var name = document.getElementById("name").value;//类型名
						var score = document.getElementById("score").value;//分数
						var reg1 = /^(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})['类']$/;//只能是I-XC即1-90类
						var reg2 = /^([6-9]\d|100)$/;//分数60-100
						var flag=$(this).form("validate");//表单验证是否输入
						if(flag){
							if(!reg1.test(name)){
							    $.messager.alert('系统提示','类型名只能是罗马数字和汉字[类]组成！最大：XCIX！');
								return false;
							}
							else if (computedStrLen(name) < 3) {
								$.messager.alert('系统提示','类型名至少3个字符（一个汉字两个字符）！');
								return false;
							}
							else if(!reg2.test(score)){
							    $.messager.alert('系统提示','分数只能在60~100！');
								return false;
							}
		    				else 
		    					return true;
						}else 
							return false;
					},
					//提交成功之后的回调函数
	    			//回调函数 result表示服务端返回的json都封装在里面
					success : function(result) {
						var result = eval('(' + result + ')');
						if (result.errorMsg) {
							$.messager.alert('系统提示', "<font color=red>" + result.errorMsg + "</font>");
							return;
						} else {//保存成功
							$.messager.alert('系统提示', '保存成功');
							closeTypeAddDialog();
							$("#dg").datagrid("reload");//重新加载，仍然显示在当页
						}
					}
				});
	}

	 //计算字符串长度，英文1个字符，中文2个字符
	function  computedStrLen(str) {
		var len = 0;
		for (var i = 0; i < str.length; i++) {
			var c = str.charCodeAt(i);
			//单字节加1
			if ((c >= 0x0001 && c <= 0x007e) || (0xff60 <= c && c <= 0xff9f)) {
				len++;
			}
			else {
				len += 2;
			}
		}
		return len;
	}
	//关闭阅览室类型对话框
	function closeTypeAddDialog() {
		$("#dlg").dialog("close");
		$("#fm").form('clear');//清除表单数据
	}

	//删除阅览室类型
	function deleteType() {
		var selectedRows = $("#dg").datagrid('getSelections');//获取选中的行
		if (selectedRows.length == 0) {
			$.messager.alert('系统提示', '请至少选择一条数据操作');
			return;
		}
		var strIds = [];   //1.批量审核，就是一个数组存入多条选中的数据。
		for ( var i = 0; i < selectedRows.length; i++) {
			strIds.push(selectedRows[i].id);
		}
		var ids = strIds.join(",");    //2.然后将数组转为字符串，逗号分隔。
		$.messager.confirm("系统提示", "确定删除<font color=red>"
				+ selectedRows.length + "</font>数据吗？", function(r) {
			//管理员点击确定
			if (r) {
				$.post("deleteType.htm", {ids : ids}, 
				function(result) {
					if (result.success) {
						$.messager.alert('系统提示', "删除<font color=red>"
								+ result.delNums + "</font>条数据成功");
						$("#dg").datagrid("reload");//重新加载
					} else {
						$.messager.alert('系统提示', result.errorMsg);
					}
				}, "json");
			}
		});
	}
</script>
</head>
<body style="margin: 1px;">
<!-- 阅览室类型列表 -->
<table id="dg" class="easyui-datagrid" fitColumns="true" 
    pagination="true" rownumbers="true" url="typeList.htm" fit="true" toolbar="#tb">
    <thead>
    	<tr>
    		<th field="ck"    checkbox="true" align="center"></th>
    		<th field="id"    width="50" align="center" data-options="hidden:true"></th>
    		<th field="name"  width="60" align="center">名称</th>
    		<th field="score" width="60" align="center">信用分数限制</th>
    	</tr>
    </thead>
</table>
<div id="tb">
	<div>
		<privilege:operation operationId="100024" clazz="easyui-linkbutton" onClick="openTypeAddDialog()" name="添加"  iconCls="icon-add" ></privilege:operation>
		<privilege:operation operationId="100025" clazz="easyui-linkbutton" onClick="openTypeModifyDialog()" name="修改"  iconCls="icon-edit" ></privilege:operation>
		<privilege:operation operationId="100026" clazz="easyui-linkbutton" onClick="deleteType()()" name="删除"  iconCls="icon-remove" ></privilege:operation>
	</div>
	<div class="updownInterval"> </div>
</div>








<!-- 新增或修改阅览室信息对话框 -->
<div id="dlg" class="easyui-dialog" style="width: 320px;height: 220px;padding: 10px 20px"
  closed="true" buttons="#dlg-buttons">
  <form id="fm" method="post">
  	<table cellspacing="5px;">
  		<tr>
  			<td>名称</td>
  			<td><input type="text" id="name" name="name" class="easyui-validatebox" required="true"/></td>
  		</tr>
  		<tr>
  			<td>信用分数限制</td>
  			<td><input type="number" id="score" name="score" class="easyui-validatebox" required="true"/></td>
  		</tr>
  	</table>
  </form>
</div>

<div id="dlg-buttons">
	<a href="javascript:saveType()" class="easyui-linkbutton" iconCls="icon-ok" >保存 </a>
	<a href="javascript:closeTypeAddDialog()" class="easyui-linkbutton" iconCls="icon-cancel" >关闭</a>
</div>


</body>
</html>