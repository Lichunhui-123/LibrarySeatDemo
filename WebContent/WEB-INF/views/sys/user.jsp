<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/common.jsp"%>

<!DOCTYPE html>
<html>
  <head>
    <title>用户主页</title>
    
    <script type="text/javascript">
		
    
    	$(function(){
			$('#dlg').dialog({
		   	 	onClose:function(){
					//closeUserDialog();
					//关闭对话框时，清除表单数据
		   	 		$("#fm").form('clear');
		   	 	}
			});
		});
    
		var url;
		// 条件搜索用户信息，根据用户名和角色进行搜索
		function searchUser(){
    		$('#dg').datagrid('load',{
    			userName:$("#s_userName").val(),
    			roleId:$('#s_roleId').combobox("getValue") //获取下拉框的值
    		});
        }

		// 打开新增用户信息对话框
        function openUserAddDialog(){
        	$("#dlg").dialog("open").dialog("setTitle","添加用户信息");//打开对话框，并设置title
        	$("#userName").removeAttr("readonly");//取消只读属性
        	url = 'reserveUser.htm';
        }

		// 打开修改用户信息对话框
        function openUserUpdateDialog(){
    		var selectedRows=$("#dg").datagrid('getSelections');
    		if(selectedRows.length!=1){
    			$.messager.alert('系统提示','请选择一条要编辑的数据！');
    			return;
    		}
    		var row=selectedRows[0];
    		$("#dlg").dialog("open").dialog("setTitle","修改用户信息");
    		$("#fm").form("load",row);//加载选择的用户信息在表单中
    		$("#userName").attr("readonly","readonly");//用户名变成只读
    		url="reserveUser.htm?userId="+row.userId;
    	}

        // 保存用户信息
        function reserveUser(){
        	$("#fm").form("submit",{
    			url:url,
    			//提交前的回调函数
    			onSubmit:function(){
    				var userName = document.getElementById("userName").value;//用户名
    				var password = document.getElementById("password").value;//密码
    				var userDescription = document.getElementById("userDescription").value;//备注
    				var roleId = document.getElementById("roleId").value;//用户名
    				var uN = userName.substring(0,2);
    				
    				var reg1=/[xs,js]\d+$/;//用户名以xs或js开头
    				var reg2=/^[\w]+$/;//密码在3位到12位
    				var reg3 = /^[a-zA-Z0-9_\u4e00-\u9fa5]*$/;//中文，数字，字母，下划线
    				var flag=$(this).form("validate");//表单验证是否输入
    				if(flag){
    					if(!reg1.test(userName)){
    					    $.messager.alert('系统提示','用户名必须以xs或js开头，后面接数字！');
    						return false;
    					}
    					else if (computedStrLen(userName) > 7||computedStrLen(userName) < 5) {
    						$.messager.alert('系统提示','用户名5~7位！');
    						return false;
    					}
    					else if (uN=="xs"&&roleId != 3) {
    						$.messager.alert('系统提示','xs与学生要对应！');
    						return false;
    					}
    					else if ( uN == "js"&&roleId != 2) {
    						$.messager.alert('系统提示','js与教师要对应！');
    						return false;
    					}
    					else if(!reg2.test(password)){
 					        $.messager.alert('系统提示','密码由数字、字母或下划线组成！');
 						    return false;
 					    }
    					else if (computedStrLen(password) > 6||computedStrLen(password) <3) {
    						$.messager.alert('系统提示','密码3~6位！');
    						return false;
    					}
    					else if (!reg3.test(userDescription)) {
        					$.messager.alert('系统提示','备注名称中不能包含特殊符号!');
        					return false;
        				} 
    					else if (computedStrLen(userDescription) > 16) {
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
    			success:function(result){
    				var result=eval('('+result+')');
    				//保存失败
    				if(result.errorMsg){
    					$.messager.alert('系统提示',"<font color=red>"+result.errorMsg+"</font>");
    					return;
    				}else{
    					//保存成功
    					$.messager.alert('系统提示','保存成功');
    					closeUserDialog();
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

        // 关闭添加修改角色对话框
        function closeUserDialog(){
        	$("#fm").form('clear');
        	$("#dlg").dialog("close");
        }

		// 打开选择角色对话框
        function openRoleChooseDialog(){
        	$("#dlg2").dialog("open").dialog("setTitle","选择角色");
        }

		// 搜索角色
        function searchRole(){
			//根据角色名加载角色信息
    		$('#dg2').datagrid('load',{
    			roleName:$("#s_roleName").val()
    		});
    	}

		// 关闭选择角色对话框
    	function closeRoleDialog(){
    		$("#s_roleName").val("");
    		$('#dg2').datagrid('load',{
    			s_roleName:""
    		});
    		$("#dlg2").dialog("close");
    	}

    	// 选择角色
    	function chooseRole(){
    		//返回所有选中的行
    		var selectedRows=$("#dg2").datagrid('getSelections');
    		//没有选择，就提示
    		if(selectedRows.length!=1){
    			$.messager.alert('系统提示','请选择一个角色！');
    			return;
    		}
    		var row=selectedRows[0];
    		//设置被选元素的值
    		$("#roleId").val(row.roleId);
    		$("#roleName").val(row.roleName);
    		//选完自动关闭角色对话框
    		closeRoleDialog();
    	}

		//双击选中
    	$(function(){
    		$("#dg2").datagrid({
    			//onDblClickRow事件：当用户双击时触发
    			onDblClickRow:function(rowIndex,rowData){
    				chooseRole();
    			}
    		});
    	})
    	
    	// 批量删除用户
    	function deleteUser(){
    		var selectedRows=$("#dg").datagrid('getSelections');//得到选中的行
    		if(selectedRows.length==0){
    			$.messager.alert('系统提示','请选择要删除的数据！');
    			return;
    		}
    		var strIds=[];    //1.批量审核，就是一个数组存入多条选中的数据。
    		for(var i=0;i<selectedRows.length;i++){
    			strIds.push(selectedRows[i].userId);
    		}
    		var ids=strIds.join(",");  //2.然后将数组转为字符串，逗号分隔。
    		//消息框messager
    		$.messager.confirm("系统提示","您确认要删除这<font color=red>"+selectedRows.length+"</font>条数据吗？",function(r){
    			//管理员点击确定
    			if(r){
    				$.post("deleteUser.htm",{ids:ids},function(result){
    					if(result.success){
    						$.messager.alert('系统提示',"您已成功删除<font color=red>"+result.delNums+"</font>条数据！");
    						//重新加载
    						$("#dg").datagrid("reload");
    					}else{
    						$.messager.alert('系统提示',result.errorMsg);
    					}
    				},"json");
    			}
    		});
    	}


		// 加载选择角色数据  并添加 请选择
    	$(function(){
        	var relation_id_sign = 0;//标签
    		$("#s_roleId").combobox({
                url: '${path }/role/roleCombobox.htm',//表示服务器的路径
                method: 'post',  //请求类型
                valueField: 'roleId',//绑定到ComboBox的value的名称为：角色id
                textField: 'roleName',//绑定到ComboBox的text的名称为：角色名
                editable: false,
                panelHeight: 'auto',
                //当远程数据加载成功时触发
                onLoadSuccess: function() {
                    if (relation_id_sign == 0){
                        var data = $(this).combobox('getData');//getDate返回加载的数据
                        data.unshift({'roleId':'','roleName':'-----全部-----'});//把'-----全部-----'插到data的头部
                        relation_id_sign++;
                        $("#s_roleId").combobox("loadData", data);//重新加载数据，且当 relation_id_sign==1时加载
                    }
                }
            });
        });
    </script>
    </head>
 
 
<body style="margin:1px">

<!-- 加载数据表格 -->
<table  id="dg" class="easyui-datagrid" fitColumns="true"
   				 pagination="true" rownumbers="true" url="userList.htm" fit="true" toolbar="#tb">
        <thead>
            	<tr>
            		<th data-options="field:'ck',checkbox:true"></th>
            		<th data-options="field:'roleId',hidden:'true'">
                	<th data-options="field:'userId',width:80" align="center">用户编号</th>
                	<th field="userName" width="60" align="center">用户名</th>
                	<th field="password" width="60" align="center">密码</th>
                	<th field="roleName" width="60" align="center">用户角色</th>
                	<th field="userDescription" width="60" align="center">备注</th>
            	</tr>
        </thead>
</table>
    	
<!-- 数据表格上的搜索和添加等操作按钮 -->
<div id="tb" >
	<div class="updownInterval"> </div>
	<div>
		<privilege:operation operationId="10003" clazz="easyui-linkbutton" onClick="openUserAddDialog()" name="添加"  iconCls="icon-add" ></privilege:operation>
		<privilege:operation operationId="10004" clazz="easyui-linkbutton" onClick="openUserUpdateDialog()" name="修改"  iconCls="icon-edit" ></privilege:operation>
		<privilege:operation operationId="10005" clazz="easyui-linkbutton" onClick="deleteUser()()" name="删除"  iconCls="icon-remove" ></privilege:operation>
	</div>
	<div class="updownInterval"> </div>
	<div>
		&nbsp;用户名：&nbsp;<input type="text" name="s_userName" id="s_userName" size="20" onkeydown="if(event.keyCode==13) searchUser()"/>
		&nbsp;用户角色：&nbsp;<input class="easyui-combobox" id="s_roleId" name="s_roleId"  />
		<a href="javascript:searchUser()" class="easyui-linkbutton" iconCls="icon-search" >搜索</a>
	</div>
	<div class="updownInterval"> </div>
</div>




<!-- 新增和修改对话框 -->
<div id="dlg" class="easyui-dialog" style="text-align:right;width: 620px;height: 320px;padding: 10px 20px"
  closed="true" buttons="#dlg-buttons">
 <form id="fm" method="post">
 	<table cellspacing="5px;">
  		<tr>
  			<td>用户名：</td>
  			<!-- 验证框 easyui-validatebox-->
  			<td><input type="text" id="userName" name="userName" class="easyui-validatebox" required="true"/></td>
  			<td>&nbsp;&nbsp;</td>
  			<td>密码：</td>
  			<td><input type="text" id="password" name="password" class="easyui-validatebox" required="true"/></td>
  		</tr>
  		<tr>
  			<td>角色：</td>
  			<td><input type="hidden" id="roleId" name="roleId" /><input type="text" id="roleName" name="roleName" readonly="readonly" class="easyui-validatebox" required="true"/></td>
  			<td>&nbsp;&nbsp;</td>
  			<!-- easyui-linkbutton连接按钮 -->
  			<td colspan="2"><a href="javascript:openRoleChooseDialog()" class="easyui-linkbutton" >选择角色</a></td>
  		</tr>
  		<tr>
  			<td valign="top">备注：</td>
  			<td colspan="4">
  				<textarea rows="7" cols="50" name="userDescription" id="userDescription"></textarea>
  			</td>
  		</tr>
  	</table>
 </form>
</div>
<div id="dlg-buttons" style="text-align:center">
	<a href="javascript:reserveUser()" class="easyui-linkbutton" iconCls="icon-ok">保存</a>
	<a href="javascript:closeUserDialog()" class="easyui-linkbutton" iconCls="icon-cancel">关闭</a>
</div>


<!-- 用户角色对话框 -->
<div id="dlg2" class="easyui-dialog" iconCls="icon-search" style="width: 500px;height: 480px;padding: 10px 20px"
  closed="true" buttons="#dlg2-buttons">
  <div style="height: 40px;" align="center">
  	角色名称：<input type="text" id="s_roleName" name="s_roleName" onkeydown="if(event.keyCode==13) searchRole()"/>
  	<a href="javascript:searchRole()" class="easyui-linkbutton" iconCls="icon-search" >搜索</a>
  </div>
  <div style="height: 350px;">
  	<table id="dg2" title="查询结果" class="easyui-datagrid" fitColumns="true"  pagination="true" rownumbers="true" url="${path }/role/roleList.htm" singleSelect="true" fit="true" >
    <thead>
    	<tr>
    		<th field="roleId" width="50" align="center">编号</th>
    		<th field="roleName" width="100" align="center">角色名称</th>
    		<th field="roleDescription" width="200" align="center">备注</th>
    	</tr>
    </thead>
</table>
  </div>
</div>

<div id="dlg2-buttons">
	<a href="javascript:chooseRole()" class="easyui-linkbutton" iconCls="icon-ok" >确定</a>
	<a href="javascript:closeRoleDialog()" class="easyui-linkbutton" iconCls="icon-cancel" >关闭</a>
</div>

</body>
</html>
