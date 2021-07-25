<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/common.jsp"%>
<!DOCTYPE html>
<html>
  <head>
    <title>教师管理</title>
<script type="text/javascript">
	var url;
	
	//根据工号和姓名查询
	function searchTeacher() {
		$('#dg').datagrid('load', {
			no : $("#s_no").val(),
			name : $('#s_name').val()
		});
	}

	//打开添加教师信息对话框
	function openTeacherAddDialog() {
		$("#dlg").dialog("open").dialog("setTitle", "添加教师信息");
		url = "reserveTeacher.htm";
	}

	//打开修改教师信息对话框
	function openTeacherModifyDialog() {
		var selectedRows = $("#dg").datagrid('getSelections');//获取选中的行
		if (selectedRows.length != 1) {
			$.messager.alert('系统提示', '请选择一条数据进行操作');
			return;
		}
		var row = selectedRows[0];
		$("#dlg").dialog("open").dialog("setTitle", "修改教师信息");
		$("#fm").form("load", row);//数据回显，将获取的row数据显示到表单上
		url = "reserveTeacher.htm?id=" + row.id;
	}

	//保存教师信息
	function saveTeacher() {
		$("#fm").form(
				"submit",
				{
					url : url,
					onSubmit : function() {
						var name = document.getElementById("name").value;//教师名
						var no = document.getElementById("no").value;//工号
						var sex = document.getElementById("sex").value;//性别
						var phone = document.getElementById("phone").value;//电话
						var mail = document.getElementById("mail").value;//邮箱
						var position = document.getElementById("position").value;//职位
						var reg1 =  /^['男'|'女']$/;//性别用中文
						var reg2 = /js\d+$/;//工号以js开头
	    				var reg3 = /^1[3456789]\d+$/;//电话固定11位
	    				var reg4 = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;//只允许英文字母、数字、下划线、英文句号、以及中划线组成**@**.**
	    				var reg5 = /^[a-zA-Z#+\u4e00-\u9fa5]+$/;//名字和职位中文，字母
	    				var flag=$(this).form("validate");//表单验证是否输入
	    				if(flag){
	    					if(!reg5.test(name)){
	    					    $.messager.alert('系统提示','教师名用英文或中文！');
	    						return false;
	    					}
	    					else if (computedStrLen(name) > 10||computedStrLen(name) < 2) {
	    						$.messager.alert('系统提示','教师名2~10个字符（一个汉字两个字符）！');
	    						return false;
	    					}
	    					else if(!reg2.test(no)){
	 					        $.messager.alert('系统提示','工号以js开头，后接3~5个数字！');
	 						    return false;
	 					    }
	    					else if (computedStrLen(no) > 7||computedStrLen(no) <5) {
	    						$.messager.alert('系统提示','工号5~7个字符！');
	    						return false;
	    					}
	    					else if(!reg3.test(phone)){
	 					        $.messager.alert('系统提示','电话格式不对1[3456789]接9个数字！');
	 						    return false;
	 					    }
	    					else if (computedStrLen(phone) !=11) {
	    						$.messager.alert('系统提示','电话11个字符！');
	    						return false;
	    					}
	    					else if (!reg1.test(sex)) {
	        					$.messager.alert('系统提示','性别只能填男或女!');
	        					return false;
	        				} 
	    					else if (computedStrLen(sex) != 2) {
	        					$.messager.alert('系统提示','性别只能一个汉字！');
	        					return false;
	        				}
	    					else if (!reg4.test(mail)) {
	        					$.messager.alert('系统提示','邮箱只允许英文字母、数字、下划线、英文句号、以及中划线组成**@**.**!');
	        					return false;
	        				} 
	    					else if (!reg5.test(position)) {
	        					$.messager.alert('系统提示','职位只能用中文、英文和#+组成!');
	        					return false;
	        				} 
	    					else if (computedStrLen(position)  < 4||computedStrLen(position)  > 20) {
	        					$.messager.alert('系统提示','职位4~20个字符（一个汉字两个字符）！');
	        					return false;
	        				} 
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
							$.messager.alert('系统提示', "<font color=red>"
									+ result.errorMsg + "</font>");
							return;
						} else {//保存失败
							$.messager.alert('系统提示', '保存成功');
							closeTeacherAddDialog();
							//重新加载数据，保持在当前页
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
	//关闭教师信息添加对话框
	function closeTeacherAddDialog() {
		$("#dlg").dialog("close");
		$("#fm").form('clear');//清除表单数据
	}

	//删除教师信息
	function deleteTeacher() {
		var selectedRows = $("#dg").datagrid('getSelections');//获取选中的行
		if (selectedRows.length == 0) {
			$.messager.alert('系统提示', '请至少选择一条数据操作');
			return;
		}
		var strIds = [];    //1.批量审核，就是一个数组存入多条选中的数据。
		for ( var i = 0; i < selectedRows.length; i++) {
			strIds.push(selectedRows[i].id);
		}
		var ids = strIds.join(","); //2.然后将数组转为字符串，逗号分隔。
		$.messager.confirm("系统提示", "确定删除<font color=red>"
				+ selectedRows.length + "</font>数据吗？", function(r) {
			if (r) {//管理员点击确定
				$.post("deleteTeacher.htm", {
					ids : ids
				}, function(result) {
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
<!-- 教师信息数据列表 -->
<table id="dg" class="easyui-datagrid" fitColumns="true" 
    pagination="true" rownumbers="true" url="teacherList.htm" fit="true" toolbar="#tb">
    <thead>
    	<tr>
    		<th field="ck" checkbox="true" align="center"></th>
    		<th field="id" width="50" align="center" data-options="hidden:true"></th>
    		<th field="no" width="60" align="center">工号</th>
    		<th field="name" width="60" align="center">姓名</th>
    		<th field="phone" width="60" align="center">电话</th>
    		<th field="position" width="60" align="center">职位</th>
    		<th field="sex" width="60" align="center">姓名</th>
    		<th field="mail" width="60" align="center">邮箱</th>
    	</tr>
    </thead>
</table>
<!-- 添加、修改、删除按钮 -->
<div id="tb">
	<div>
		<privilege:operation operationId="100018" clazz="easyui-linkbutton" onClick="openTeacherAddDialog()" name="添加"  iconCls="icon-add" ></privilege:operation>
		<privilege:operation operationId="100019" clazz="easyui-linkbutton" onClick="openTeacherModifyDialog()" name="修改"  iconCls="icon-edit" ></privilege:operation>
		<privilege:operation operationId="100020" clazz="easyui-linkbutton" onClick="deleteTeacher()()" name="删除"  iconCls="icon-remove" ></privilege:operation>
	</div>
	<div class="updownInterval"> </div>
	<div>
		&nbsp;姓名：&nbsp;<input type="text" name="s_name" id="s_name" size="20" onkeydown="if(event.keyCode==13) searchTeacher()"/>
		&nbsp;工号：&nbsp;<input type="text" name="s_no" id="s_no" size="20" onkeydown="if(event.keyCode==13) searchTeacher()"/>
		<a href="javascript:searchTeacher()" class="easyui-linkbutton" iconCls="icon-search" >搜索</a>
	</div>
	<div class="updownInterval"> </div>
</div>








<!-- 新增或修改教师信息对话框 -->
<div id="dlg" class="easyui-dialog" style="width: 520px;height: 320px;padding: 10px 20px"
  closed="true" buttons="#dlg-buttons">
  <form id="fm" method="post">
  	<table cellspacing="5px;">
  		<tr>
  			<td>姓名</td>
  			<td><input type="text" id="name" name="name" class="easyui-validatebox" required="true"/></td>
  			<td>&nbsp;</td>
  			<td>工号</td>
  			<td><input type="text" id="no" name="no" class="easyui-validatebox" required="true"/></td>
  		</tr>
  		<tr>
  			<td>性别</td>
  			<td><input type="text" id="sex" name="sex" class="easyui-validatebox" required="true"/></td>
  			<td>&nbsp;</td>
  			<td>电话</td>
  			<td><input type="text" id="phone" name="phone" class="easyui-validatebox" required="true"/></td>
  		</tr>
  		<tr>
  			<td>邮箱</td>
  			<td><input type="email" id="mail" name="mail" class="easyui-validatebox" required="true"/></td>
  			<td>&nbsp;</td>
  			<td>职位</td>
  			<td><input type="text" id="position" name="position" class="easyui-validatebox" required="true"/></td>
  		</tr>
  	</table>
  </form>
</div>

<div id="dlg-buttons">
	<a href="javascript:saveTeacher()" class="easyui-linkbutton" iconCls="icon-ok" >保存 </a>
	<a href="javascript:closeTeacherAddDialog()" class="easyui-linkbutton" iconCls="icon-cancel" >关闭</a>
</div>


</body>
</html>