<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="false" %>
<html>
  <head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>巨商汇日志平台</title>
  </head>
  <!-- 新 Bootstrap 核心 CSS 文件 -->
  <link rel="stylesheet" href="/js/bootstrap-3.3.2/css/bootstrap.min.css">
  <link rel="stylesheet" href="/css/index.css">
  <link rel="stylesheet" href="/js/jquery/DataTables/css/jquery.dataTables.min.css">
  <link rel="stylesheet" href="/js/jquery/jquery-ui/css/jquery-ui.min.css">
  <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
  <script type="text/javascript" src="/js/jquery.min.js"></script>
  <script type="text/javascript" src="/js/jquery/jquery-form/jquery.form.min.js"></script>

  <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
  <script src="/js/bootstrap-3.3.2/js/bootstrap.min.js"></script>
  <script src="/js/jquery/jquery-ui/js/jquery-ui.min.js"></script>
  <script src="/js/jquery/jquery-ui/js/jquery-ui.js"></script>
  <%--<script src="/js/jquery/globalize/globalize.js"></script>
  <script src="/js/jquery/globalize/globalize.culture.zh-CN.js"></script>--%>
  <style>
      .logList {margin-top: 10px;}
      .search{width:50px;height: 20px;}
  </style>
  <script type="text/javascript">
     /* Globalize.culture("zh-CN");
      $(function () {

          function GetDatePickerRegion(locale) {

              // Try to get region directly (with the same name)
              var region = $.datepicker.regional[locale];
              if (region != undefined)
                  return region;

              // Fallback when region specific (e.g. "de-DE" to "de")
              if (locale.length >
                      2) {
                  region = $.datepicker.regional[locale.substring(0, 2)];
                  if (region != undefined)
                      return region;
              }

              // Return default region
              region = $.datepicker.regional[""];
              return region;
          }

          var region = GetDatePickerRegion('zh-CN');
          $.datepicker.setDefaults(region);*/
     $(function () {
          $("#logSearch input[name='start']").datepicker({showButtonPanel:true, dateFormat:"yy-MM-dd"});
          $("#logSearch input[name='end']").datepicker({showButtonPanel:true, dateFormat:"yy-MM-dd"});
      });
  </script>
  <body>
  <nav class="navbar navbar-inverse navbar-fixed-top">
      <div class="container-fluid">
          <div class="navbar-header">
              <a class="navbar-brand" href="#">巨商汇日志平台</a>
          </div>
          <div class="navbar-collapse collapse" id="navbar">
              <ul class="nav navbar-nav navbar-right" style="margin-right: 30%;">
                  <li><a href="#">文档</a></li>
                  <li><a href="#">API</a></li>
                  <li><a href="#">支持</a></li>
                  <li><a href="#">控制台</a></li>
              </ul>

          </div>
      </div>
  </nav>

  <div class="panel panel-default">
      <div class="panel-body" >
          <form id="logSearch" class="panel" action="/log/search.do" method="post">
              <div>
                  <input  style="width: 300px;" name="key" type="text" placeholder="Search...">
                  <select name="tablename" style="width: 100px;">
                      <option value="">选择模块</option>
                    <c:forEach var="name" items="${names}" varStatus="status">
                      <option value="${name}">${name}</option>
                    </c:forEach>
                  </select>
                  <button type="search"  class="btn">查询</button>
              </div>
          </form>
          <div class="logList" id="logList1">
          </div>
          <div class="logList" >
              <table id="logList" class="table table-bordered">
                  <thead>
                      <tr>
                          <th>序号</th>
                          <th>内容</th>
                      </tr>
                  </thead>
                  <%--<tbody>
                      <c:forEach var="app" items="${logList}" varStatus="status">
                        <tr>
                            <td>${app.id}</td>
                            <td>${app.ip}</td>
                            <td>${app.userName}</td>
                            <td>
                                <fmt:formatDate value="${app.loginTime}" pattern="yyyy-MM-dd H:mm:ss" ></fmt:formatDate>
                            </td>
                            <td>
                                <c:if test="${app.loginType==1}">PC</c:if>
                                <c:if test="${app.loginType==2}">移动端</c:if>
                            </td>
                        </tr>
                      </c:forEach>
                  </tbody>--%>
              </table>
          </div>
      </div>
  </div>
  <!-- 为ECharts准备一个具备大小（宽高）的Dom -->
  <div id="main" style="height:400px"></div>
  <!-- ECharts单文件引入 -->
  <script src="/js/echart/echarts-all.js"></script>
  <script src="/js/jquery/DataTables/js/jquery.dataTables.min.js"></script>
  <script type="text/javascript">
      $(document).ready( function () {
          $("#logSearch").ajaxForm({
              type: 'post',
              url: '/log/search.do',
              data: {"key":$("input[name='key']").val(),"tablename":$("input[name='tablename']").val(),"pageindex":1,pagesize:10},
              dataType: 'html',
              success: function(html) {
                  $('#logList').parent().html(html);
              }
          });
         /* $('#logList1').dataTable({
              "oLanguage": {//语言国际化
                  "sUrl": "/js/grid/jquery.dataTable.cn.txt"
              }, "ajax": "/log/login/list.do?pagesize=10"});*/
          $('#logList').dataTable({
              "oLanguage": {//语言国际化
                  "sUrl": "/js/jquery/DataTables/js/jquery.dataTable.cn.txt"
              },
              "bServerSide":false,//服务端处理分页
              "sAjaxSource": "/log/list.do",//"/log/login/list.do",
              'bPaginate': true,  //是否分页。
              "bProcessing": true, //当datatable获取数据时候是否显示正在处理提示信息。
              'bFilter': true,  //是否使用内置的过滤功能
              'bLengthChange': true, //是否允许自定义每页显示条数.
            //'iDisplayLength':13, //每页显示10条记录
              "sPaginationType": "full_numbers" //分页样式   full_numbers
          /*  "aoColumns": [
                  { "sClass": "center", "sName": "id" },
                  { "sClass": "center", "sName": "content" }
                 { //自定义列
                      "sName": "操作",
                      "sClass": "center",
                      "bSearchable": false,
                      "bStorable": false,
                      "fnRender": function (obj) {
                          return '<a class="ajaxify" href=\"/admin/Article/edit?Id=' + obj.aData[0] + '\">编辑</a> ' + ' <a href=\"#\" onclick=\"DeleteArticle('+obj.aData[0]+')\">删除</a>';
                      }
                  }
              ]*/
          });
          /*$('#logList').dataTable({

              "bPaginate": true, //开关，是否显示分页器
                //"bInfo": true, //开关，是否显示表格的一些信息
//                "bFilter": true, //开关，是否启用客户端过滤器
//               "sDom": "<>lfrtip<>",
//               "bAutoWith": false,//自适应宽度
//               "bDeferRender": false,
 //               "bJQueryUI": false, //开关，是否启用JQueryUI风格
//               "bLengthChange": true, //开关，是否显示每页大小的下拉框
              "bProcessing": true,//开关，以指定当正在处理数据的时候，是否显示“正在处理”这个提示信息
//                "bScrollInfinite": false,
               "sScrollY": "280px", //是否开启垂直滚动，以及指定滚动区域大小,可设值：'disabled','2000px'
               "sScrollX": "100%", //是否开启水平滚动，以及指定滚动区域大小,可设值：'disabled','2000%'
               "sScrollXInner": "110%",
              "bScrollCollapse": true,
//                "bSort": true, //开关，是否启用各列具有按列排序的功能
//                "bSortClasses": true,
              "bStateSave": true, //开关，是否打开客户端状态记录功能。这个数据是记录在cookies中的，打开了这个记录后，即使刷新一次页面，或重新打开浏览器，之前的状态都是保存下来的-  ------当值为true时aoColumnDefs不能隐藏列
//                "aoColumnDefs": [{ "bVisible": false, "aTargets": [0]}]//隐藏列
//                "sDom": '<"H"if>t<"F"if>',
                 // "aaSorting": [[1, "asc"]],
              "sPaginationType": "full_numbers",
              "oLanguage": {
                      "sProcessing": "正在加载中......",//"<img src='./loading.gif'
                      "sLengthMenu": "每页显示 _MENU_ 条记录",
                      "sZeroRecords": "对不起，查询不到相关数据！",
                      "sEmptyTable": "表中无数据存在！",
                      "sInfo": "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录",
                      "sInfoFiltered": "数据表中共为 _MAX_ 条记录",
                      "sSearch": "搜索",
                      "oPaginate": {
                          "sFirst": "首页",
                          "sPrevious": "上一页",
                          "sNext": "下一页",
                          "sLast": "末页"
                      }
               } //多语言配置



      });*/
      });
          // 基于准备好的dom，初始化echarts图表
      var myChart = echarts.init(document.getElementById('main'));

      var option = {
          legend: {
              data:['高度(km)与气温(°C)变化关系']
          },
          toolbox: {
              show : true,
              feature : {
                  mark : {show: true},
                  dataView : {show: true, readOnly: false},
                  magicType : {show: true, type: ['line', 'bar']},
                  restore : {show: true},
                  saveAsImage : {show: true}
              }
          },
          calculable : true,
          tooltip : {
              trigger: 'axis',
              formatter: "Temperature : <br/>{b}km : {c}°C"
          },
          xAxis : [
              {
                  type : 'value',
                  axisLabel : {
                      formatter: '{value} °C'
                  }
              }
          ],
          yAxis : [
              {
                  type : 'category',
                  axisLine : {onZero: false},
                  axisLabel : {
                      formatter: '{value} km'
                  },
                  boundaryGap : false,
                  data : ['0', '10', '20', '30', '40', '50', '60', '70', '80']
              }
          ],
          series : [
              {
                  name:'高度(km)与气温(°C)变化关系',
                  type:'line',
                  smooth:true,
                  itemStyle: {
                      normal: {
                          lineStyle: {
                              shadowColor : 'rgba(0,0,0,0.4)'
                          }
                      }
                  },
                  data:[15, -50, -56.5, -46.5, -22.1, -2.5, -27.7, -55.7, -76.5]
              }
          ]
      };
      // 为echarts对象加载数据
      myChart.setOption(option);

  </script>
  </body>
</html>
