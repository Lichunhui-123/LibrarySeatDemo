<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/common.jsp"%>
<!DOCTYPE html>
<html>
  <head>
    <title>积分管理</title>
<script type="text/javascript">
	var url;
	
	//根据学号和姓名加载学生信用积分信息
	function searchScore() {
		$('#dg').datagrid('load', {
			studentno : $("#s_no").val(),
			studentname : $('#s_name').val()
		});
	}

	//打开积分违章详情添加对话框
	function openIllegalAddDialog() {
		$("#dlg").dialog("open").dialog("setTitle", "添加违章信息");
		url = "${path}/illegal/reserveIllegal.htm";
	}


	//保存积分违章详情
	function saveIllegal() {
		$("#fm").form("submit",
				{
					url : url,
					//提交前的回调函数
					onSubmit : function() {
						var score = document.getElementById("score").value;//扣分
	    				var remarks = document.getElementById("remarks").value;//备注
	    				var reg1 = /^-?([1-9]|10)$/;//扣分在1-9
	    				var reg2 = /^[a-zA-Z0-9_\u4e00-\u9fa5]*$/;//中文，数字，字母，下划线
	    				var flag=$(this).form("validate");//表单验证是否输入
	    				if(flag){
	    					if(!reg1.test(score)){
	    					    $.messager.alert('系统提示','扣分在-10~10分！');
	    						return false;
	    					}
	    					else if (!reg2.test(remarks)) {
	        					$.messager.alert('系统提示','备注名称中不能包含特殊符号!');
	        					return false;
	        				} 
	    					else if (computedStrLen(remarks) > 16) {
	        					$.messager.alert('系统提示','最多输入16个字符(1个汉字2个字符)');
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
						if (result.errorMsg) {//保存成功
							$.messager.alert('系统提示', "<font color=red>"+ result.errorMsg + "</font>");
							return;
						} else {//保存失败
							$.messager.alert('系统提示', '保存成功');
							closeIllegalAddDialog();
							$("#dg2").datagrid("reload");//重新加载积分违章详情对话框
							$("#dg").datagrid("reload");//重新加载信用积分列表
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
	//关闭积分违章详情添加对话框
	function closeIllegalAddDialog() {
		$("#dlg").dialog("close");//关闭
		var no = $("#hiddenStudentno").val();
		$("#fm").form('clear');
		$("#hiddenStudentno").val(no);
	}

	//删除积分违章详情
	function deleteIllegal() {
		var selectedRows = $("#dg2").datagrid('getSelections');//获取选中的行
		if (selectedRows.length == 0) {
			$.messager.alert('系统提示', '请至少选择一条数据操作');
			return;
		}
		var strIds = [];   //1.批量审核，就是一个数组存入多条选中的数据。
		for ( var i = 0; i < selectedRows.length; i++) {
			strIds.push(selectedRows[i].id);
		}
		var ids = strIds.join(",");//2.然后将数组转为字符串，逗号分隔。
		//消息框messager
		$.messager.confirm("系统提示", "确定删除<font color=red>"+ selectedRows.length + "</font>数据吗？", function(r) {
			//管理员点击确定
			if (r) {
				$.post("${path}/illegal/deleteIllegal.htm", {
					ids : ids
				}, function(result) {
					if (result.success) {//删除成功
						$.messager.alert('系统提示', "删除<font color=red>" + result.delNums + "</font>条数据成功");
						$("#dg2").datagrid("reload");//重新加载，显示当前页
					} else {//删除失败
						$.messager.alert('系统提示', result.errorMsg);
					}
				}, "json");
			}
		});
	}
	
	//根据学号显示积分违章详情对话框
	function showDetail(studentno){
		$("#dlg2").dialog("open").dialog("setTitle", "积分违章详情");
		$("#hiddenStudentno").val(studentno);//设置隐藏对话框的值
		$("#dg2").datagrid({
			url:'${path}/illegal/illegalList.htm?studentno='+studentno,
			pagination:true,//分页
			fitColumns:true,
			rownumbers:true,
			fit:true,
			//加载成功触发
			 //easyUI的doCellTip 就是鼠标放到单元格上有个提示的功能
			onLoadSuccess: function (data) {
				               
                 				$(this).datagrid('doCellTip', { 'max-width': '400px', 'delay': 500 });
           					}
		});
		
	} 
</script>
</head>
<body style="margin: 1px;">
<!-- 信用积分数据列表 -->
<table id="dg" class="easyui-datagrid" fitColumns="true" 
    pagination="true" rownumbers="true" url="scoreList.htm" fit="true" toolbar="#tb">
    <thead>
    	<tr>
    		<th field="id" width="50" align="center" data-options="hidden:true"></th>
    		<th field="studentno" width="60" align="center">学号</th>
    		<th field="studentname" width="60" align="center">姓名</th>
    		<th field="total" width="60" align="center" data-options="formatter:formatTotal">积分</th><!-- datagrid的列属性formatter:单元格的格式化函数 -->
    	</tr>
    </thead>
</table>
<script type="text/javascript">

/* value：表示当前单元格中的值
  row：表示当前行
  index：表示当前行的下标 */
	function formatTotal(value,row){
		return "<a href='javascript:showDetail(\""+row.studentno+"\")' >"+value+"</a>";
	} 
</script>
<!-- 搜索工具栏 -->
<div id="tb">
	<div class="updownInterval"> </div>
	<div>
		&nbsp;姓名：&nbsp;<input type="text" name="s_name" id="s_name" size="20" onkeydown="if(event.keyCode==13) searchScore()"/>
		&nbsp;学号：&nbsp;<input type="text" name="s_no" id="s_no" size="20" onkeydown="if(event.keyCode==13) searchScore()"/>
		<a href="javascript:searchScore()" class="easyui-linkbutton" iconCls="icon-search" >搜索</a>
	</div>
	<div class="updownInterval"> </div>
</div>








<!-- 新增或修改积分违章详情对话框 -->
<div id="dlg" class="easyui-dialog" style="width: 520px;height: 320px;padding: 10px 20px"
  closed="true" buttons="#dlg-buttons">
  <form id="fm" method="post">
  	<table cellspacing="5px;">
  		<input type="hidden" id="hiddenStudentno" name="studentno"/><!-- 隐藏的输入框 -->
  		<tr>
  			<td>时间</td><!--datetimebox:日期时间框  -->
  			<td><input type="text" id="time" name="time" data-options="editable:false" class="easyui-datetimebox" required="true"/></td>
  			<td>&nbsp;</td>
  			<td>扣分</td>
  			<td><input type="number" id="score" name="score" class="easyui-validatebox" required="true"/></td>
  		</tr>
  		<tr>
  			<td>备注</td>
  			<td colspan="4"><textarea id="remarks" name="remarks"></textarea></td>
  		</tr>
  	</table>
  </form>
</div>
<!-- 保存关闭按钮 -->
<div id="dlg-buttons">
	<a href="javascript:saveIllegal()" class="easyui-linkbutton" iconCls="icon-ok" >保存 </a>
	<a href="javascript:closeIllegalAddDialog()" class="easyui-linkbutton" iconCls="icon-cancel" >关闭</a>
</div>







<!-- 积分详情对话框 -->
<div id="dlg2" class="easyui-dialog" iconCls="icon-search" style="width: 500px;height: 480px;padding: 10px 20px"
  closed="true" buttons="#dlg2-buttons">
  <div class="updownInterval"> </div>
  <!-- 添加删除按钮 -->
	<div>
		<privilege:operation operationId="100030" clazz="easyui-linkbutton" onClick="openIllegalAddDialog()" name="添加"  iconCls="icon-add" ></privilege:operation>
		<privilege:operation operationId="100032" clazz="easyui-linkbutton" onClick="deleteIllegal()" name="删除"  iconCls="icon-remove" ></privilege:operation>
	</div>
  <div class="updownInterval"> </div>
  <div style="height: 400px;" align="center">
  	<table id="dg2" title="积分详情" class="easyui-datagrid"  >
    <thead>
    	<tr>
    		<th field="studentno" width="200" align="center">学号</th>
    		<th field="studentname" width="200" align="center">姓名</th>
    		<th field="time" width="200" align="center">时间</th>
    		<th field="score" width="200" align="center">扣分</th>
    		<th field="remarks" width="200" align="center">违章</th>
    	</tr>
    </thead>
</table>
  </div>
</div>







</body>
</html>