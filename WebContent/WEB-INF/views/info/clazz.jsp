<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/common.jsp"%>
<!DOCTYPE html>
<html>
  <head>
    <title>班级管理</title>
<script type="text/javascript">
	var url;
	
	

	//打开班级信息添加对话框
	function openClazzAddDialog() {
		$("#dlg").dialog("open").dialog("setTitle", "添加班级信息");
		url = "reserveClazz.htm";
	}

	//打开班级信息修改对话框
	function openClazzModifyDialog() {
		var selectedRows = $("#dg").datagrid('getSelections');//获取选中的行
		if (selectedRows.length != 1) {
			$.messager.alert('系统提示', '请选择一条数据进行操作');
			return;
		}
		var row = selectedRows[0];
		$("#dlg").dialog("open").dialog("setTitle", "修改班级信息");
		$("#fm").form("load", row);//数据回显，row含有的信息会自动回显到form表单对应的文本输入框中
		url = "reserveClazz.htm?id=" + row.id;
	}

	//保存班级信息
	function saveClazz() {
		var v = $("#teacherno").combobox('getValue');//获取选中组合框的值
		if(v==null || v.length==0){
			$.messager.alert('系统提示', '请选择一名辅导员');
			return;
		}
		$("#fm").form("submit",{
			url : url,
			onSubmit : function() {
				var name = document.getElementById("name").value;//班级名
				var xueyuan = document.getElementById("xueyuan").value;//学院
				var zhuanye = document.getElementById("zhuanye").value;//专业
				/* var zy = zhuanye.substring(0,2); */
				
				/* $.messager.alert('系统提示',zy.toString()); */
				var reg1 = /^[a-zA-Z0-9#+\u4e00-\u9fa5]+$/;//名字用中文，字母
				/* var reg2 = new RegExp(zy.toString()+"[\u4e00-\u9fa5]+"); */
				
				var flag=$(this).form("validate");//表单验证是否输入
				if(flag){
					if(!reg1.test(name)){
					    $.messager.alert('系统提示','班级名用英文、中文、数字、#和+组成！');
						return false;
					}
					else if (computedStrLen(name) > 16||computedStrLen(name) < 8) {
						$.messager.alert('系统提示','班级名8~16个字符（一个汉字两个字符）专业+班级！');
						return false;
					}
					else if(!reg1.test(xueyuan)){
					    $.messager.alert('系统提示','学院名用英文、中文、数字、#和+组成！');
						return false;
					    }
					else if (computedStrLen(xueyuan) > 20||computedStrLen(xueyuan) <6) {
						$.messager.alert('系统提示','学院名6~20个字符（一个汉字两个字符）！');
						return false;
					}
					else if (!reg1.test(zhuanye)) {
						$.messager.alert('系统提示','专业名用英文、中文、数字、#和+组成！');
					    return false;
					}
					else if (computedStrLen(zhuanye) > 16||computedStrLen(zhuanye) < 6) {
						$.messager.alert('系统提示','专业名6~16个字符（一个汉字两个字符）！');
						return false;
					}
					/*  else if (!reg2.test(name)) {
						$.messager.alert('系统提示','班级名不对应专业,班级名前两字必须对应专业！');
						return false;
					}  */
    				else 
    					return true;
				}else 
					return false;
			},
			//数据加载成功触发
			//回调函数 result表示服务端返回的json都封装在里面
			success : function(result) {
				var result = eval('(' + result + ')');
				if (result.errorMsg) {//保存失败
					$.messager.alert('系统提示', "<font color=red>"+ result.errorMsg + "</font>");
					return;
				} else {//保存成功
					$.messager.alert('系统提示', '保存成功');
					closeClazzAddDialog();
					//重新加载，显示保留在当前页
					$("#dg").datagrid("reload");
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
    //关闭新增班级信息对话框
	function closeClazzAddDialog() {
		$("#dlg").dialog("close");
		$("#fm").form('clear');//清除表单数据
	}

    //删除班级信息
	function deleteClazz() {
		var selectedRows = $("#dg").datagrid('getSelections');//获取选中的行
		if (selectedRows.length == 0) {
			$.messager.alert('系统提示', '请至少选择一条数据操作');
			return;
		}
		var strIds = [];//1.批量审核，就是一个数组存入多条选中的数据。
		for ( var i = 0; i < selectedRows.length; i++) {
			strIds.push(selectedRows[i].id);
		}
		var ids = strIds.join(","); //2.然后将数组转为字符串，逗号分隔。
		//消息框messager
		$.messager.confirm("系统提示", "确定删除<font color=red>"
				+ selectedRows.length + "</font>数据吗？", function(r) {
			//管理员点击确定
			if (r) {
				$.post("deleteClazz.htm", {
					ids : ids
				}, function(result) {
					if (result.success) {//删除成功
						$.messager.alert('系统提示', "删除<font color=red>"	+ result.delNums + "</font>条数据成功");
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
<!-- 班级信息数据列表 -->
<!-- datagrid会以post方式发送row和page -->
<table id="dg" class="easyui-datagrid" fitColumns="true" 
    pagination="true" rownumbers="true" url="clazzList.htm" fit="true" toolbar="#tb">
    <thead>
    	<tr>
    		<th field="ck" checkbox="true" align="center"></th>
    		<th field="id" width="50" align="center" data-options="hidden:true"></th>
    		<th field="name" width="60" align="center">班级名称</th>
    		<th field="xueyuan" width="60" align="center">学院</th>
    		<th field="zhuanye" width="60" align="center">专业</th>
    		<th field="teachername" width="60" align="center">辅导员</th>
    	</tr>
    </thead>
</table>
<!-- 添加、修改、删除按钮 -->
<div id="tb">
	<div>
		<privilege:operation operationId="100015" clazz="easyui-linkbutton" onClick="openClazzAddDialog()" name="添加"  iconCls="icon-add" ></privilege:operation>
		<privilege:operation operationId="100016" clazz="easyui-linkbutton" onClick="openClazzModifyDialog()" name="修改"  iconCls="icon-edit" ></privilege:operation>
		<privilege:operation operationId="100017" clazz="easyui-linkbutton" onClick="deleteClazz()()" name="删除"  iconCls="icon-remove" ></privilege:operation>
	</div>
	<div class="updownInterval"> </div>
</div>








<!-- 新增或修改对话框 -->
<div id="dlg" class="easyui-dialog" style="width: 560px;height: 300px;padding: 10px 20px"
  closed="true" buttons="#dlg-buttons">
  <form id="fm" method="post">
  	<table cellspacing="5px;">
  		<tr>
  			<td>名称</td>
  			<td><input type="text" id="name" name="name" class="easyui-validatebox" required="true"/></td>
  			<td>&nbsp;</td>
  			<td>辅导员</td>
  			<td><input class="easyui-combobox" name="teacherno" id="teacherno"  size="20" data-options="editable:false,panelHeight:'auto',valueField:'no',textField:'name',url:'${path }/teacher/comboList.htm'"/></td>
  		</tr>
  		<tr>
  			<td>学院</td>
  			<td><input type="text" id="xueyuan" name="xueyuan" class="easyui-validatebox" required="true"/></td>
  			<td>&nbsp;</td>
  			<td>专业</td>
  			<td><input type="text" id="zhuanye" name="zhuanye" class="easyui-validatebox" required="true"/></td>
  		</tr>
  	</table>
  </form>
</div>

<div id="dlg-buttons">
	<a href="javascript:saveClazz()" class="easyui-linkbutton" iconCls="icon-ok" >保存 </a>
	<a href="javascript:closeClazzAddDialog()" class="easyui-linkbutton" iconCls="icon-cancel" >关闭</a>
</div>


</body>
</html>