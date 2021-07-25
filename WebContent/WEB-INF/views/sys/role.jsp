<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/common.jsp"%>
<!DOCTYPE html>
<html>
  <head>
    <title></title>
    
    <script type="text/javascript">

    //关闭角色信息添加或者修改对话框，清除表单数据
	$(function(){
		$('#dlg').dialog({
		    onClose:function(){
				$("#fm").form('clear');
		    }
		});
	});
    
    //根据角色名加载角色信息
	function searchRole(){
		$('#dg').datagrid('load',{
			roleName:$("#s_roleName").val()
		});
	}
	
    //批量删除角色信息
	function deleteRole(){
		var selectedRows=$("#dg").datagrid('getSelections');//得到选中的行
		if(selectedRows.length==0){
			$.messager.alert('系统提示','请选择要删除的数据！');
			return;
		} 
		var strIds=[];    //1.批量审核，就是一个数组存入多条选中的数据。
		for(var i=0;i<selectedRows.length;i++){
			strIds.push(selectedRows[i].roleId);
		}
		var ids=strIds.join(",");   //2.然后将数组转为字符串，逗号分隔。
		//消息框messager
		$.messager.confirm("系统提示","您确认要删除这<font color=red>"+selectedRows.length+"</font>条数据吗？",function(r){
			//管理员点击确定
			if(r){
				$.post("deleteRole.htm",{ids:ids},function(result){
					//删除成功
					if(result.success){
						$.messager.alert('系统提示',"您已成功删除<font color=red>"+result.delNums+"</font>条数据！");
						//重新加载
						$("#dg").datagrid("reload");
					}else{
						$.messager.alert('系统提示','<font color=red>'+selectedRows[result.errorIndex].roleName+'</font>'+result.errorMsg);
					}
				},"json");
			}
		});
	}
	
	var url;

	//打开新增角色对话框
	function openRoleAddDialog(){
		$("#dlg").dialog("open").dialog("setTitle","添加角色信息");
		$("#roleName").removeAttr("readonly");//去除只读属性
		url="reserveRole.htm";
	}

	// 打开修改角色对话框
	function openRoleUpdateDialog(){
		var selectedRows=$("#dg").datagrid('getSelections');//获取选中的行
		if(selectedRows.length!=1){
			$.messager.alert('系统提示','请选择一条要编辑的数据！');
			return;
		}
		var row=selectedRows[0];
		$("#dlg").dialog("open").dialog("setTitle","修改角色信息");
		$("#fm").form("load",row);//加载选择的角色信息在表单中
		$("#roleName").attr("readonly","readonly");//角色名变成只读
		url="reserveRole.htm?roleId="+row.roleId;
	}

	// 保存角色信息
	function reserveRole(){
		$("#fm").form("submit",{
			url:url,
			onSubmit:function(){
				var roleName = document.getElementById("roleName").value;//角色名称
				var roleDescription = document.getElementById("roleDescription").value;//备注
				var reg1 = /^[\u4e00-\u9fa5]+$/;//中文
				var reg2 = /^[a-zA-Z0-9_\u4e00-\u9fa5]*$/;//中文，数字，字母，下划线
				var flag=$(this).form("validate");//进行表单验证
				if(flag){
					if(!reg1.test(roleName)){
					    $.messager.alert('系统提示','角色名只能用中文！');
						return false;
					}
					else if (computedStrLen(roleName) > 20||computedStrLen(roleName) < 4) {
						$.messager.alert('系统提示','角色名至少2个字，最多10个！');
						return false;
					}
					else if(!reg2.test(roleDescription)){
					    $.messager.alert('系统提示','备注名称中不能包含特殊符号!');
						return false;
					    }
					else if (computedStrLen(roleDescription) > 30) {
						$.messager.alert('系统提示','备注最多30个字符(一个汉字两个字符)！');
						return false;
					}
					else 
						return true;
				}else 
					return false;
			},
			//加载成功时触发
			//回调函数的 result表示服务端返回的json都封装在里面
			success:function(result){
				var result=eval('('+result+')');
				//保存失败
				if(result.errorMsg){
					$.messager.alert('系统提示',"<font color=red>"+result.errorMsg+"</font>");
					return;
				}else{
					$.messager.alert('系统提示','保存成功');
					closeRoleDialog();
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
	// 关闭新增修改对话框
	function closeRoleDialog(){
		$("#dlg").dialog("close");
		$("#fm").form('clear');//清除表单数据
	}

	// 打开授权对话框
	function openMenuDialog(){
		var selectedRows=$("#dg").datagrid('getSelections');//获取选中的所有行
		if(selectedRows.length!=1){
			$.messager.alert('系统提示','请选择一条要授权的角色！');
			return;
		}
		var row=selectedRows[0];
		roleId=row.roleId; //获取该角色的id
		$("#dlg2").dialog("open").dialog("setTitle","角色授权");
		url="chooseMenu.htm?parentId=-1&roleId="+roleId;
		// 加载权限菜单表格树
		$("#tree").tree({
			lines:true,//显示树线
			url:url,
			checkbox:true,//复选框
			cascadeCheck:false,//连级检查
			//加载成功触发
			onLoadSuccess:function(){
				$("#tree").tree('expandAll');//展开所有的节点
			},
			//当用户点击复选框时触发
			onCheck:function(node,checked){
				//$('#tree').tree('getParent',node.target):通过子节点获取父节点，
				//如果当前节点被选中,就让当前目标节点的父节点也被选中
				if(checked){
					checkNode($('#tree').tree('getParent',node.target));
				}
			}
		});
	}

	// 递归检查勾选他的上级
	function checkNode(node){
		if(!node){//根节点没有父节点了,就返回
			return;
		}else{//该节点还有父节点，继续递归选中
			checkNode($('#tree').tree('getParent',node.target));
			$('#tree').tree('check',node.target);
		}
	}

	// 关闭授权框
	function closeMenuDialog(){
		$("#dlg2").dialog("close");
	}

	// 授权保存
	function saveMenu(){
		var nodes=$('#tree').tree('getChecked');//获取选中的所有节点
		var menuArrIds=[];        //1.批量审核，就是一个数组存入多条选中的数据。
		for(var i=0;i<nodes.length;i++){
			menuArrIds.push(nodes[i].id);
		}
		var menuIds=menuArrIds.join(",");  //2.然后将数组转为字符串，逗号分隔。menuIds存放的是菜单id和按钮id的混合数据
		/* $.messager.alert('tis',menuIds); */
		$.post("updateRoleMenu.htm",{menuIds:menuIds,roleId:roleId},function(result){
			if(result.success){
				$.messager.alert('系统提示','授权成功！');
				closeMenuDialog();
			}else{
				$.messager.alert('系统提示',result.errorMsg);
			}
		},"json");
	}
</script>
<title>角色管理</title>
</head>
<body style="margin: 1px;">

<!-- 展示列表 -->
<table id="dg" class="easyui-datagrid" fitColumns="true" 
    pagination="true" rownumbers="true" url="roleList.htm" fit="true" toolbar="#tb">
    <thead>
    	<tr>
    		<th field="cb" checkbox="true" align="center"></th>
    		<th field="roleId" width="50" align="center">编号</th>
    		<th field="roleName" width="100" align="center">角色名称</th>
    		<th field="roleDescription" width="200" align="center">备注</th>
    	</tr>
    </thead>
</table>


<!-- 数据表格上的搜索和添加等操作按钮 -->
<div id="tb">
	<div class="updownInterval"> </div>
	<div>
		<privilege:operation operationId="10006" clazz="easyui-linkbutton" onClick="openRoleAddDialog()" name="添加"  iconCls="icon-add" ></privilege:operation>
		<privilege:operation operationId="10007" clazz="easyui-linkbutton" onClick="openRoleUpdateDialog()" name="修改"  iconCls="icon-edit" ></privilege:operation>
		<privilege:operation operationId="10008" clazz="easyui-linkbutton" onClick="deleteRole()" name="删除"  iconCls="icon-remove" ></privilege:operation>
		<privilege:operation operationId="10009" clazz="easyui-linkbutton" onClick="openMenuDialog()" name="授权"  iconCls="icon-edit" ></privilege:operation>
	</div>
	<div class="updownInterval"> </div>
	<div>
		&nbsp;角色名称：&nbsp;<input type="text" name="s_roleName" id="s_roleName" size="20" onkeydown="if(event.keyCode==13) searchRole()"/>
		<a href="javascript:searchRole()" class="easyui-linkbutton" iconCls="icon-search" >搜索</a>
	</div>
	<div class="updownInterval"> </div>
</div>



<!-- 角色新增修改表单 -->
<div id="dlg" class="easyui-dialog" style="width: 570px;height: 350px;padding: 10px 20px"
  closed="true" buttons="#dlg-buttons">
  <form id="fm" method="post">
  	<table cellspacing="5px;">
  		<tr>
  			<td>角色名称：</td>
  			<td width="80%"><input type="text" id="roleName" name="roleName" class="easyui-validatebox" required="true"/></td>
  		</tr>
  		<tr>
  			<td valign="top">备注：</td>
  			<td colspan="2">
  				<textarea rows="7" cols="50" name="roleDescription" id="roleDescription"></textarea>
  			</td>
  		</tr>
  	</table>
  </form>
</div>

<div id="dlg-buttons"  style="text-align:center">
	<a href="javascript:reserveRole()" class="easyui-linkbutton" iconCls="icon-ok" >保存</a>
	<a href="javascript:closeRoleDialog()" class="easyui-linkbutton" iconCls="icon-cancel" >关闭</a>
</div>




<!-- 权限菜单表格树 -->
<div id="dlg2" class="easyui-dialog" style="width: 300px;height: 450px;padding: 10px 20px"
  closed="true" buttons="#dlg2-buttons">
	<ul id="tree" class="easyui-tree"></ul>
</div>

<div id="dlg2-buttons">
	<a href="javascript:saveMenu()" class="easyui-linkbutton" iconCls="icon-ok" >授权</a>
	<a href="javascript:closeMenuDialog()" class="easyui-linkbutton" iconCls="icon-cancel" >关闭</a>
</div>
</body>
</html>