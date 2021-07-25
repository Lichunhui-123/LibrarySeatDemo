<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/common.jsp"%>
<!DOCTYPE html>
<html>
  <head>
    <title>学生管理</title>
<script type="text/javascript">
	var url;
	
	

	//打开新增学生信息对话框
	function openStudentAddDialog() {
		$("#dlg").dialog("open").dialog("setTitle", "添加班级信息");
		url = "reserveStudent.htm";
			$("#showImg").attr('src',"");
	}

	//打开修改学生信息对话框
	function openStudentModifyDialog() {
		var selectedRows = $("#dg").datagrid('getSelections');//获取选中的行
		if (selectedRows.length != 1) {
			$.messager.alert('系统提示', '请选择一条数据进行操作');
			return;
		}
		var row = selectedRows[0];
		$("#dlg").dialog("open").dialog("setTitle", "修改班级信息");
		$("#fm").form("load", row);//加载数据显示在表单上
		$("#showImg").attr('src',row.photo);
		url = "reserveStudent.htm?id=" + row.id;
	}

	//保存学生信息
	function saveStudent() {
		var v = $("#clazzid").combobox('getValue');//获取组合框的值
		if(v==null || v.length==0){
			$.messager.alert('系统提示', '请选择班级');
			return;
		}
		var egetFile=$('#photo');
		$("#fm").form("submit",{
			url : url,
			onSubmit : function() {
				var name = document.getElementById("name").value;//学生名
				var no = document.getElementById("no").value;//学号
				var sex = document.getElementById("sex").value;//性别
				var phone = document.getElementById("phone").value;//电话
				var mail = document.getElementById("mail").value;//邮箱
				//var birth = document.getElementById("birth").value;//生日
				var reg1 = /^[a-zA-Z\u4e00-\u9fa5]+$/;//名字用中文，字母
				var reg2 = /xs\d+$/;//工号以js开头
				var reg3 = /^['男'|'女']$/;//性别用中文
				var reg4 = /^1[3456789]\d+$/;//电话固定11位
				var reg5 = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;//只允许英文字母、数字、下划线、英文句号、以及中划线组成**@**.**
				//var reg6 = /^[(19\d{2})-(20[[0-1]\d|2[0]])]-[(0[1-9])|(1[0-2])]-[(0[1-9])|([12]\d)|(3[01])]$/;
				var flag=$(this).form("validate");//表单验证是否输入
				if(flag){
					if(!reg1.test(name)){
					    $.messager.alert('系统提示','学生名用英文或中文！');
						return false;
					}
					else if (computedStrLen(name) > 10||computedStrLen(name) < 2) {
						$.messager.alert('系统提示','学生名2~10个字符（一个汉字两个字符）！');
						return false;
					}
					else if(!reg2.test(no)){
					        $.messager.alert('系统提示','学号号以xs开头，后接3~5个数字！');
						    return false;
					    }
					else if (computedStrLen(no) > 7||computedStrLen(no) <5) {
						$.messager.alert('系统提示','学号5~7个字符！');
						return false;
					}
					else if (!reg3.test(sex)) {
    					$.messager.alert('系统提示','性别只能填男或女!');
    					return false;
    				} 
					else if (computedStrLen(sex) != 2) {
    					$.messager.alert('系统提示','性别只能一个汉字！');
    					return false;
    				}
					else if(!reg4.test(phone)){
					        $.messager.alert('系统提示','电话格式不对1[3456789]接9个数字！');
						    return false;
					    }
					else if (computedStrLen(phone) !=11) {
						$.messager.alert('系统提示','电话11个字符！');
						return false;
					}
					else if (!reg5.test(mail)) {
    					$.messager.alert('系统提示','邮箱只允许英文字母、数字、下划线、英文句号、以及中划线组成**@**.**!');
    					return false;
    				} 
					/* else if (!reg6.test(birth)) {
    					$.messager.alert('系统提示','生日格式为YYYY-MM-DD!');
    					return false;
    				} 
					else if (computedStrLen(birth) !=10) {
    					$.messager.alert('系统提示','生日固定长度10个字符！');
    					return false;
    				}   */
    				else 
    					return true;
				}else 
					return false;
				
			},
			//数据加载成功触发
			//回调函数 result表示服务端返回的json都封装在里面
			success : function(result) {
				var result = eval('(' + result + ')');
				if (result.errorMsg) {
					$.messager.alert('系统提示', "<font color=red>"+ result.errorMsg + "</font>");
					return;
				} else {//保存成功
					$.messager.alert('系统提示', '保存成功');
					closeStudentAddDialog();
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
	//关闭添加学生信息对话框
	function closeStudentAddDialog() {
		$("#dlg").dialog("close");
		$("#fm").form('clear');//清除表单数据
	}

	//删除学生信息
	function deleteStudent() {
		var selectedRows = $("#dg").datagrid('getSelections');//获取选中的行
		if (selectedRows.length == 0) {
			$.messager.alert('系统提示', '请至少选择一条数据操作');
			return;
		}
		var strIds = [];   //1.批量审核，就是一个数组存入多条选中的数据。
		var strNos = [];
		for ( var i = 0; i < selectedRows.length; i++) {
			strIds.push(selectedRows[i].id);
			strNos.push(selectedRows[i].no);
		}
		var ids = strIds.join(",");//2.然后将数组转为字符串，逗号分隔。
		var nos = strNos.join(",");
		
		//消息框messager
		$.messager.confirm("系统提示", "确定删除<font color=red>"
				+ selectedRows.length + "</font>数据吗？", function(r) {
			if (r) {//管理员点击确定
				$.post("deleteStudent.htm", {
					ids : ids ,nos:nos
				}, function(result) {
					if (result.success) {
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
<!-- 学生信息列表 -->
<table id="dg" class="easyui-datagrid" fitColumns="true" 
    pagination="true" rownumbers="true" url="studentList.htm" fit="true" toolbar="#tb">
    <thead>
    	<tr>
    		<th field="ck" checkbox="true" align="center"></th>
    		<th field="id" width="50" align="center" data-options="hidden:true"></th>
    		<th field="no" width="60" align="center">学号</th>
    		<th field="name" width="60" align="center">姓名</th>
    		<th field="sex" width="60" align="center">性别</th>
    		<th field="clazzname" width="60" align="center">班级</th>
    		<th field="birth" width="60" align="center">生日</th>
    		<th field="phone" width="60" align="center">电话</th>
    		<th field="mail" width="60" align="center">邮箱</th>
    	</tr>
    </thead>
</table>
<div id="tb">
	<div>
		<privilege:operation operationId="100021" clazz="easyui-linkbutton" onClick="openStudentAddDialog()" name="添加"  iconCls="icon-add" ></privilege:operation>
		<privilege:operation operationId="100022" clazz="easyui-linkbutton" onClick="openStudentModifyDialog()" name="修改"  iconCls="icon-edit" ></privilege:operation>
		<privilege:operation operationId="100023" clazz="easyui-linkbutton" onClick="deleteStudent()()" name="删除"  iconCls="icon-remove" ></privilege:operation>
	</div>
	<div class="updownInterval"> </div>
</div>








<!-- 新增或修改学生信息对话框 -->
<div id="dlg" class="easyui-dialog" style="width: 660px;height: 300px;padding: 10px 20px"
  closed="true" buttons="#dlg-buttons">
  <form id="fm" method="post" >
  	<table cellspacing="5px;">
  		<input type="hidden" id="photoHidden" name="photo" />
  		<tr>
  			<td>学号</td>
  			<td><input type="text" id="no" name="no" class="easyui-validatebox" required="true"/></td>
  			<td>&nbsp;</td>
  			<td>姓名</td>
  			<td><input type="text" id="name" name="name" class="easyui-validatebox" required="true"/></td>
  		</tr>
  		<tr>
  			<td>性别</td>
  			<td><input type="text" id="sex" name="sex" class="easyui-validatebox" required="true"/></td>
  			<td>&nbsp;</td>
  			<td>生日</td>
  			<td><input type="text" id="birth" name="birth" data-options="editable:false" class="easyui-datebox" required="true"/></td>
  		</tr>
  		<tr>
  			<td>电话</td>
  			<td><input type="text" id="phone" name="phone" class="easyui-validatebox" required="true"/></td>
  			<td>&nbsp;</td>
  			<td>邮箱</td>
  			<td><input type="mail" id="mail" name="mail" required="true"/></td>
  		</tr>
  		<tr>
  			<td>班级</td>
  			<td><input class="easyui-combobox" name="clazzid" id="clazzid"  size="20" data-options="editable:false,panelHeight:'auto',valueField:'id',textField:'name',url:'${path }/clazz/comboList.htm'"/></td>
  		</tr>
  		
  	</table>
  </form>
  
  <div id="dlg-buttons">
	<a href="javascript:saveStudent()" class="easyui-linkbutton" iconCls="icon-ok" >保存 </a>
	<a href="javascript:closeStudentAddDialog()" class="easyui-linkbutton" iconCls="icon-cancel" >关闭</a>
</div>
  
  
</div>




</body>
</html>