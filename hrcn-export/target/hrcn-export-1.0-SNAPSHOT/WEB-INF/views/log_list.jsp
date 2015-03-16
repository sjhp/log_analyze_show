<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page session="false" %>
<html>
  <head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>巨商汇日志平台</title>
    <script type="text/javascript" src="/js/jquery.min.js"></script>
    <script src="/js/jquery/DataTables/js/jquery.dataTables.min.js"></script>
      <script type="text/javascript" src="/js/jquery/jquery-form/jquery.form.min.js"></script>
      <script  type="text/javascript">
       $(document).ready(function() {

          /*
           * 初始化表格参数
           */
          var oTable = $('#baseLogListTable').dataTable({
              "bProcessing" : true, //DataTables载入数据时，是否显示‘进度’提示
              "bServerSide" : true, //是否启动服务器端数据导入
              //"aLengthMenu" : [10, 20, 50], //更改显示记录数选项
              //"iDisplayLength" : 10, //默认显示的记录数
              "bPaginate" : true, //是否显示（应用）分页器
              'bLengthChange': true, //是否允许自定义每页显示条数.
              "bInfo" : true, //是否显示页脚信息，DataTables插件左下角显示记录数
              "bSort" : false, //是否启动各个字段的排序功能
              //"sDom": "t<'row-fluid'<'span6'i><'span6'p>>",//定义表格的显示方式
              "sPaginationType": "full_numbers",
              //"aaSorting" : [[0, "desc"]], //默认的排序方式，第0列，降序排列
              "bFilter" : false, //是否启动过滤、搜索功能
              "aoColumns" : [{
                  "mData" : "id",	//列标识，和服务器返回数据中的属性名称对应
                  "sTitle" : "ID",//列标题
                  "sDefaultContent" : "", //此列默认值为""，以防数据中没有此值，DataTables加载数据的时候报错
                  //"bVisible" : false //此列不显示
                  //"sClass" : "hidden"//定义列的class参数，隐藏列也可以通过这种方式设置
              }, {
                  "mData" : "tomcatName",
                  "sTitle" : "服务名称",
                  "sWidth":"10%",//定义列宽度，以百分比表示
                  "sDefaultContent" : ""
              }, {
                  "mData" : "serverIp",
                  "sTitle" : "服务IP",
                  "sDefaultContent" : ""
              }, {
                  "mData" : "content",
                  "sTitle" : "内容",
                  "sDefaultContent" : "",
                  "bSortable":false	//此列不需要排序
              },  {
                  "mData" : "oper",
                  "sTitle" : "操作",
                  "sDefaultContent" : "<button>Click!</button>"
              }],
              "oLanguage": {//语言国际化
                  "sUrl": "/js/jquery/DataTables/js/jquery.dataTable.cn.txt"
              },
              /*
               * 修改状态值
               */
              "fnRowCallback" : function(nRow, aData, iDisplayIndex) {
                  //console.log(nRow);
                  //console.log(aData);
                  //console.log(iDisplayIndex);
                  return nRow;
              },
              /*
               * 向服务器传递的参数
               */
              "fnServerParams": function ( aoData ) {
                  aoData.push(
                          { "name": "key", "value": "${key}" },
                          { "name": "tablename", "value": "${tablename}" }
                  );
              },
              //请求url
              "sAjaxSource" : "/log/search.do?r=" + new Date().getTime(),
              //服务器端，数据回调处理
              "fnServerData" : function(sSource, aDataSet, fnCallback) {
                  $.ajax({
                      "dataType": 'json',
                      "type": "post",
                      "url": sSource,
                      "data": aDataSet,
                      "success": function (resp) {
                          fnCallback(resp);
                      }
                  });
              }
          });
          $('#baseLogListTable tbody').on( 'click', 'button', function () {
              alert("row ID:"+$(this).closest('tr').children("td").eq(0).text());
             // var data = oTable.row($(this).parents('tr')).data();
          } );
       });
        /*$('#baseLogListTable').dataTable({
            "oLanguage": {//语言国际化
                "sUrl": "/js/jquery/DataTables/js/jquery.dataTable.cn.txt"
            },
            "bServerSide":true,//服务端处理分页
            "sAjaxSource": "/log/search.do?r=" + new Date().getTime(),
            "fnServerData":function(sSource,aoData,fnCallback){
                    //console.log(aoData);
                    //console.log(JSON.stringify(aoData));
                  $.ajax({
                      "type" : "post",
                     // "contentType": "application/json",
                      "url" : sSource,
                      "dataType" : "json",
                      "data" : {
                           aoData:JSON.stringify(aoData),
                           "key":"${key}",
                           "tablename":"${tablename}"
                       },// 以json格式传递*//* 
                      "success" : function(resp) {
                          console.log(resp);
                          console.log(encodeURI(resp));
                          fnCallback(resp);
                      }
                    });

            },
            'bPaginate': true,  //是否分页。
            "bProcessing": true, //当datatable获取数据时候是否显示正在处理提示信息。
            'bFilter': false,  //是否使用内置的过滤功能
            'bLengthChange': true, //是否允许自定义每页显示条数.
            "bInfo": true, //开关，是否显示表格的一些信息
            "bSort": false, //开关，是否启用各列具有按列排序的功能
            "sPaginationType": "full_numbers"//分页样式   full_numbers
        });*/
    </script>
  </head>
  <body>
      <table id="baseLogListTable" class="dataTable table-bordered display" cellspacing="0" width="100%" style="font-size: 10pt;">
      </table>
  </body>
</html>
