<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/common.jsp"%>
<!DOCTYPE html>
<html>
  <head>
    <title>阅览室管理</title>
<script type="text/javascript">
	var url;
	
	

	//打开阅览室信息添加对话框
	function openRoomAddDialog() {
		$("#dlg").dialog("open").dialog("setTitle", "添加阅览室信息");
		url = "reserveRoom.htm";
	}

	//打开阅览室信息修改对话框
	function openRoomModifyDialog() {
		var selectedRows = $("#dg").datagrid('getSelections');//获取选中的行
		if (selectedRows.length != 1) {
			$.messager.alert('系统提示', '请选择一条数据进行操作');
			return;
		}
		var row = selectedRows[0];
		$("#dlg").dialog("open").dialog("setTitle", "修改阅览室信息");//打开dialog并设置title
		$("#fm").form("load", row);//数据回显，把获取的数据显示在foem表单中
		url = "reserveRoom.htm?id=" + row.id;
	}

	//保存阅览室信息
	function saveRoom() {
		var v = $("#typeid").combobox('getValue');//获取组合框的数据
		if(v==null || v.length==0){
			$.messager.alert('系统提示', '请选择阅览室类型');
			return;
		}
		//表单提交
		$("#fm").form("submit",{
			url : url,
			//提交前的回调函数
			onSubmit : function() {
				var name = document.getElementById("name").value;//类型名
				var row = document.getElementById("row").value;//分数
				var col = document.getElementById("col").value;//分数
				/* var reg1 = /^([1-9]|[1-9][0-9])["号阅览室"]$/;//只能是I-XC即1-90类 */
				var reg1 = new RegExp("([1-9]|[1-9][0-9])"+"号阅览室"); 
				var reg2 = /^([5-9]|[1-2]\d|30)$/;//行和列控制在5-30
				var flag=$(this).form("validate");//表单验证是否输入
				if(flag){
					if(!reg1.test(name)){
					    $.messager.alert('系统提示','名称只能是数字和汉字[号阅览室]组成！1-99！');
						return false;
					}
					else if(!reg2.test(row)){
					    $.messager.alert('系统提示','行数只能在5-30！');
						return false;
					}
					else if(!reg2.test(col)){
					    $.messager.alert('系统提示','列数只能在5-30！');
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
				if (result.errorMsg) {//保存失败
					$.messager.alert('系统提示', "<font color=red>"+ result.errorMsg + "</font>");
					return;
				} else {//保存成功
					$.messager.alert('系统提示', '保存成功');
					closeRoomAddDialog();
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
	//关闭阅览室信息对话框
	function closeRoomAddDialog() {
		$("#dlg").dialog("close");
		$("#fm").form('clear');//清除表单数据
	}

	//删除阅览室信息
	function deleteRoom() {
		var selectedRows = $("#dg").datagrid('getSelections');//获取选中的行
		if (selectedRows.length == 0) {
			$.messager.alert('系统提示', '请至少选择一条数据操作');
			return;
		}
		var strIds = []; //1.批量审核，就是一个数组存入多条选中的数据。
		for ( var i = 0; i < selectedRows.length; i++) {
			strIds.push(selectedRows[i].id);
		}
		var ids = strIds.join(","); //2.然后将数组转为字符串，逗号分隔。
		$.messager.confirm("系统提示", "确定删除<font color=red>"
				+ selectedRows.length + "</font>数据吗？", function(r) {
			//管理员点击确定
			if (r) {
				$.post("deleteRoom.htm", {
					ids : ids
				}, function(result) {
					if (result.success) {//删除成功
						$.messager.alert('系统提示', "删除<font color=red>"	+ result.delNums + "</font>条数据成功");
						$("#dg").datagrid("reload");
					} else {//删除失败
						$.messager.alert('系统提示', result.errorMsg);
					}
				}, "json");
			}
		});
	}
</script>
</head>
<body style="margin: 1px;">
<!-- 阅览室信息数据列表 -->
<table id="dg" class="easyui-datagrid" fitColumns="true" 
    pagination="true" rownumbers="true" url="roomList.htm" fit="true" toolbar="#tb">
    <thead>
    	<tr>
    		<th field="ck" checkbox="true" align="center"></th>
    		<th field="id" width="50" align="center" data-options="hidden:true"></th>
    		<th field="name" width="60" align="center">名称</th>
    		<th field="typename" width="60" align="center">类型</th>
    		<th field="row" width="60" align="center">行数</th>
    		<th field="col" width="60" align="center">列数</th>
    		<th field="total" width="60" align="center">总座位数</th>
    	</tr>
    </thead>
</table>
<!-- 添加、修改、删除按钮 -->
<div id="tb">
	<div>
		<privilege:operation operationId="100027" clazz="easyui-linkbutton" onClick="openRoomAddDialog()" name="添加"  iconCls="icon-add" ></privilege:operation>
		<privilege:operation operationId="100028" clazz="easyui-linkbutton" onClick="openRoomModifyDialog()" name="修改"  iconCls="icon-edit" ></privilege:operation>
		<privilege:operation operationId="100029" clazz="easyui-linkbutton" onClick="deleteRoom()()" name="删除"  iconCls="icon-remove" ></privilege:operation>
	</div>
	<div class="updownInterval"> </div>
</div>








<!-- 新增或修改阅览室信息对话框 -->
<div id="dlg" class="easyui-dialog" style="width: 560px;height: 240px;padding: 10px 20px"
  closed="true" buttons="#dlg-buttons">
  <form id="fm" method="post">
  	<table cellspacing="5px;">
  		<tr>
  			<td>名称</td>
  			<td><input type="text" id="name" name="name" class="easyui-validatebox" required="true"/></td>
  			<td>&nbsp;</td>
  			<td>类型</td>
  			<td><input class="easyui-combobox" name="typeid" id="typeid"  size="20" data-options="editable:false,panelHeight:'auto',valueField:'id',textField:'name',url:'${path }/type/comboList.htm'"/></td>
  		</tr>
  		<tr>
  			<td>行数</td>
  			<td><input type="text" id="row" name="row" class="easyui-validatebox" required="true"/></td>
  			<td>&nbsp;</td>
  			<td>列数</td>
  			<td><input type="text" id="col" name="col" class="easyui-validatebox" required="true"/></td>
  		</tr>
  	</table>
  </form>
</div>

<div id="dlg-buttons">
	<a href="javascript:saveRoom()" class="easyui-linkbutton" iconCls="icon-ok" >保存 </a>
	<a href="javascript:closeRoomAddDialog()" class="easyui-linkbutton" iconCls="icon-cancel" >关闭</a>
</div>


</body>
</html>