<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/common.jsp"%>
<!DOCTYPE html>
<html>
  <head>
    <title>菜单主页</title>
   
<script type="text/javascript">
	var url;   // 菜单添加和修改的url
	var url2;  // 按钮添加和修改的url
	
	//加载菜单树形表格
	$(function(){
		$('#treeGrid').treegrid({
			url:'treeGridMenu.htm?parentId=-1',
			//加载成功时触发
			onLoadSuccess:function(){
				$("#treeGrid").treegrid('expandAll');//展开所有的节点
			}
		});

		$('#dlg').dialog({
			
	        //新增或修改对话框关闭时触发	
		    onClose:function(){
				$("#fm").form('clear');//清除表单数据
				$("#iconCls").val("icon-item");
		    }
		});

		$('#operationReserveDlg').dialog({
		    onClose:function(){
				$("#fm2").form('clear');
		    }
		});

	});
	//默认设置按钮的样式
	function formatIconCls(value,row,index){
		return "<div class="+value+">&nbsp;</div>";
	}
	
	//打开菜单信息添加对话框
	function openMenuAddDialog(){
		var node=$('#treeGrid').treegrid('getSelected');//获取选中的节点并返回它，如果没有选中节点则返回 null。
		if(node==null){
			$.messager.alert('系统提示','请选择一个父菜单节点！');
			return;
		}
		$("#dlg").dialog("open").dialog("setTitle","添加菜单");
		url="reserveMenu.htm?parentId="+node.id;
	}

	//打开修改菜单信息对话框
	function openMenuUpdateDialog(){
		var node=$('#treeGrid').treegrid('getSelected');//获取选中的节点
		if(node==null){
			$.messager.alert('系统提示','请选择一个要修改的菜单！');
			return;
		}
		$("#dlg").dialog("open").dialog("setTitle","修改菜单");
		$("#fm").form("load",node);//把获取的节点数据显示再form表单中
		$("#menuName").val(node.text);
		url="reserveMenu.htm?menuId="+node.id;
	}
	
	//删除菜单信息
	function deleteMenu(){
		var node=$("#treeGrid").treegrid('getSelected');//获取选中的节点
		if(node==null){
			$.messager.alert('系统提示','请选择一个要删除的菜单节点！');
			return;
		}
		var parentNode=$("#treeGrid").treegrid('getParent',node.id);//获取该节点的父节点
		$.messager.confirm("系统提示","您确认要删除这个菜单节点吗?",function(r){
			//管理员点击确定
			if(r){
				$.post("deleteMenu.htm",{menuId:node.id,parentId:parentNode.id},function(result){
					if(result.success){
						$.messager.alert('系统提示',"您已成功删除这个菜单节点！");
						$("#treeGrid").treegrid("reload");
					}else{
						$.messager.alert('系统提示',result.errorMsg);
					}
				},"json");
			}
		});
	}
	
	//保存菜单信息
	function reserveMenu(){
		$("#fm").form("submit",{
			url:url,
			onSubmit:function(){
				var menuName = document.getElementById("menuName").value;//菜单名
				var seq = document.getElementById("seq").value;//序号
				var menuDescription = document.getElementById("menuDescription").value;//备注
				var reg1 = /^[\u4e00-\u9fa5]+$/;//中文
				var reg2 = /^[a-zA-Z0-9_\u4e00-\u9fa5]*$/;//中文，数字，字母，下划线
				var reg3 = /^[\d]+$/;//数字
				var flag=$(this).form("validate");//进行表单验证
				if(flag){
					if(!reg1.test(menuName)){
					    $.messager.alert('系统提示','菜单名只能用中文！');
						return false;
					}
					else if (computedStrLen(menuName) > 20||computedStrLen(menuName) < 4) {
						$.messager.alert('系统提示','菜单名至少2个字，最多10个！');
						return false;
					}
					else if(!reg2.test(menuDescription)){
					    $.messager.alert('系统提示','备注名称中不能包含特殊符号!');
						return false;
					}
					else if (computedStrLen(menuDescription) > 30) {
						$.messager.alert('系统提示','备注最多30个字符(一个汉字两个字符)！');
						return false;
					}
					else if(!reg3.test(seq)){
					    $.messager.alert('系统提示','序号只能是数字!');
						return false;
					}
					else if (computedStrLen(seq) > 4) {
						$.messager.alert('系统提示','序号最多4个字符！');
						return false;
					}
					else 
						return true;
				}else 
					return false;
			},
			//远程数据加载成功时触发
			success:function(result){
				var result=eval('('+result+')');
				if(result.errorMsg){//保存失败
					$.messager.alert('系统提示',"<font color=red>"+result.errorMsg+"</font>");
					return;
				}else{//保存成功
					$.messager.alert('系统提示','保存成功');
					$('#treeGrid').treegrid('reload');//重新加载菜单列表，显示当前页
					closeMenuDialog();//关闭对话框
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
	//关闭菜单添加或修改对话框
	function closeMenuDialog(){
		$("#dlg").dialog("close");
		$("#fm").form('clear');//清除表单数据
		$("#iconCls").val("icon-item");
	}



    //打开按钮管理对话框
	function openOperationDialog(){
		var selectedRows=$("#treeGrid").treegrid('getSelections');//获取选中的所有节点
		if(selectedRows.length!=1){
			$.messager.alert('系统提示','请选择一条要编辑的数据！');
			return;
		}
		var row=selectedRows[0];
		$("#operationDlg").dialog("open").dialog("setTitle","按钮管理");
		var url = "../operation/operationList.htm?menuId="+row.id;//根据menuid加载按钮列表
		//加载完显示按钮列表
		$('#operationTable').datagrid({       
		        nowrap : false,//设置为true，当数据长度超出列宽时将会自动截取  
		        striped : true,//设置为true将交替显示行背景。  
		        collapsible : true,//显示可折叠按钮 
		    	url:url,//url调用Action方法  
		        singleSelect:false,//为true时只能选择单行  
		        fitColumns:true,//允许表格自动缩放，以适应父容器  
		        remoteSort : false, 
		        pagination : true,//分页  
		        rownumbers : true//行数  
		 });   
	}




    //按钮添加
	function openOperationAddDialog(){
		var node=$('#treeGrid').treegrid('getSelected');
		if(node==null){
			$.messager.alert('系统提示','请选择一个父菜单节点！');
			return;
		}
		$("#o_menuName").val(node.text);
		$("#o_menuId").val(node.id);
		$("#operationReserveDlg").dialog("open").dialog("setTitle","添加菜单");
		url2="../operation/reserveOperation.htm";

	}

    //修改按钮
	function openOperationUpdateDialog(){
		var selectedRows=$("#operationTable").datagrid('getSelections');
		if(selectedRows.length!=1){
			$.messager.alert('系统提示','请选择一条要编辑的数据！');
			return;
		}
		var row=selectedRows[0];
		$("#operationReserveDlg").dialog("open").dialog("setTitle","修改角色信息");
		$("#fm2").form("load",row);
		$("#o_menuName").attr("readonly","readonly");
		url2="../operation/reserveOperation.htm?operationId="+row.operationId;
	}


    //保存按钮
	function reserveOperation(){
		$("#fm2").form("submit",{
			url:url2,
			onSubmit:function(){
				var operationName = document.getElementById("operationName").value;//按钮名
				var reg1 = /^[\u4e00-\u9fa5]+$/;//中文
				var flag=$(this).form("validate");//进行表单验证
				if(flag){
					if(!reg1.test(operationName)){
					    $.messager.alert('系统提示','按钮名只能用中文！');
						return false;
					}
					else if (computedStrLen(operationName) > 10||computedStrLen(operationName) < 4) {
						$.messager.alert('系统提示','菜单名至少2个字，最多5个！');
						return false;
					}
					else 
						return true;
				}else 
					return false;
			},
			success:function(result){
				var result=eval('('+result+')');
				if(result.errorMsg){
					$.messager.alert('系统提示',"<font color=red>"+result.errorMsg+"</font>");
					return;
				}else{
					$.messager.alert('系统提示','保存成功');
					$("#operationTable").datagrid("reload");
					closeOperationReserveDialog();
				}
			}
		});
	}


    //关闭按钮新增或修改对话框
	function closeOperationReserveDialog(){
		$("#operationReserveDlg").dialog("close");
		$("#fm2").form('clear');
	}

    //删除按钮
	function deleteOperation(){
		var selectedRows=$("#operationTable").datagrid('getSelections');
		if(selectedRows.length==0){
			$.messager.alert('系统提示','请选择要删除的数据！');
			return;
		} 
		var strIds=[];
		for(var i=0;i<selectedRows.length;i++){
			strIds.push(selectedRows[i].operationId);
		}
		var ids=strIds.join(",");
		$.messager.confirm("系统提示","您确认要删除这<font color=red>"+selectedRows.length+"</font>条数据吗？",function(r){
			if(r){
				$.post("../operation/deleteOperation.htm",{ids:ids},function(result){
					if(result.success){
						$.messager.alert('系统提示',"您已成功删除<font color=red>"+result.delNums+"</font>条数据！");
						$("#operationTable").datagrid("reload");
					}else{
						$.messager.alert('系统提示','<font color=red>'+selectedRows[result.errorIndex].roleName+'</font>'+result.errorMsg);
					}
				},"json");
			}
		});
	}







	




	
	
</script>
</head>
<body style="margin: 1px;">

<!-- 菜单显示主页面 -->
<!-- easyui-treegrid：树形网格 -->
<!-- idField:定义标识树节点的键名字段--id:菜单编号
     treeField:定义树节点的字段--text:菜单名称
 -->
<table id="treeGrid"  class="easyui-treegrid" 
  toolbar="#tb" data-options="idField:'id',treeField:'text',fit:true,fitColumns:true">
    <thead>
    	<tr>
    		<th field="id" width="30" align="center">菜单编号</th>
    		<th field="text" width="80">菜单名称</th>
    		<th field="iconCls" width="35" align="center" formatter="formatIconCls" >图标</th>
    		<th field="operationNames" width="80" align="center"  >包含按钮</th>
    		<th field="menuUrl" width="100" align="center">链接地址</th>
    		<th field="seq" width="100" align="center">顺序</th>
    		<th field="menuDescription" width="100" align="center">备注</th>
    	</tr>
    </thead>
</table>

<!-- 菜单显示主页面上的操作按钮 -->
<div id="tb">
	<div>
		<privilege:operation operationId="10000" clazz="easyui-linkbutton" onClick="openMenuAddDialog()" name="添加"  iconCls="icon-add" ></privilege:operation>
		<privilege:operation operationId="10001" clazz="easyui-linkbutton" onClick="openMenuUpdateDialog()" name="修改"  iconCls="icon-edit" ></privilege:operation>
		<privilege:operation operationId="10002" clazz="easyui-linkbutton" onClick="deleteMenu()" name="删除"  iconCls="icon-remove" ></privilege:operation>
		<privilege:operation operationId="10014" clazz="easyui-linkbutton" onClick="openOperationDialog()" name="按钮管理"  iconCls="icon-edit" ></privilege:operation>
		
	</div>
</div>


<!-- 菜单的新增/修改form -->
<div id="dlg" class="easyui-dialog" style="width: 630px;height: 350px;padding: 10px 20px"
  closed="true" buttons="#dlg-buttons" >
  <form id="fm" method="post">
  	<table cellspacing="5px;">
  		<tr>
  			<td>名称：</td>
  			<td><input type="text" id="menuName" name="menuName" class="easyui-validatebox" required="true"/></td>
  			<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
  			<td>样式：</td>
  			<td><input type="text" id="iconCls" name="iconCls" value="icon-item" class="easyui-validatebox" required="true"/>
  			</td>
  		</tr>
  		<tr>
  			<td>路径：</td>
  			<td ><input type="text" id="menuUrl" name="menuUrl" class="easyui-validatebox" /></td>
  			<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
  			<td>序号：</td>
  			<td ><input type="text" id="seq" name="seq" class="easyui-validatebox" required="true" onkeyup="this.value=this.value.replace(/\D/g,'')"/></td>
  		</tr>
  		<tr>
  			<td valign="top">备注：</td>
  			<td colspan="4">
  				<textarea rows="7" cols="50" name="menuDescription" id="menuDescription"></textarea>
  			</td>
  		</tr>
  	</table>
  </form>
</div>

<!-- 菜单新增/修改的保存按钮 -->
<div id="dlg-buttons"  style="text-align:center">
	<a href="javascript:reserveMenu()" class="easyui-linkbutton" iconCls="icon-ok" >保存</a>
	<a href="javascript:closeMenuDialog()" class="easyui-linkbutton" iconCls="icon-cancel" >关闭</a>
</div>












<!-- 按钮展示列表 -->
<div id="operationDlg" class="easyui-dialog" style="width: 450px;height: 300px;padding: 10px 20px" closed="true" >
<table  class="easyui-datagrid" id="operationTable"   toolbar="#operationTb">
    <thead>
    	<tr>
    		<th field="cb2" checkbox="true" align="center"></th>
    		<th field="menuId" width="30" align="center" data-options="hidden:true"></th>
    		<th field="operationId" width="100" align="center">按钮编号</th>
    		<th field="operationName" width="100" align="center">按钮名称</th>
    		<th field="menuName" width="100" align="center">所属菜单</th>
    	</tr>
    </thead>
</table>
</div>

<!-- 按钮列表上的操作 -->
<div id="operationTb">
	<div>
		<a href="javascript:openOperationAddDialog()" class="easyui-linkbutton" iconCls="icon-add" plain="true">添加</a>
		<a href="javascript:openOperationUpdateDialog()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">修改</a>
		<a href="javascript:deleteOperation()" class="easyui-linkbutton" iconCls="icon-remove" plain="true">删除</a>
	</div>
</div>




<!-- 按钮的新增/修改form -->
<div id="operationReserveDlg" class="easyui-dialog" style="width: 350px;height: 200px;padding: 10px 20px"
  closed="true" buttons="#operationdlg-buttons" >
  <form id="fm2" method="post">
  	<table cellspacing="5px;">
  		<tr>
  			<td>所属菜单：</td>
  			<td><input type="text" id="o_menuName" name="menuName" class="easyui-validatebox" readonly="readonly"/></td>
  		</tr>
  		<tr>
  			<td>按钮名称：</td>
  			<td><input type="text" id="operationName" name="operationName" class="easyui-validatebox" required="true"/></td>
  		</tr>
  		<input type="hidden" id="o_menuId" name="menuId" />
  	</table>
  </form>
</div>

<div id="operationdlg-buttons"  style="text-align:center">
	<a href="javascript:reserveOperation()" class="easyui-linkbutton" iconCls="icon-ok" >保存</a>
	<a href="javascript:closeOperationReserveDialog()" class="easyui-linkbutton" iconCls="icon-cancel" >关闭</a>
</div>

</body>
</html>