<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="false" %>
<%--@elvariable id="oauthParams" type="org.apache.oltu.oauth2.client.demo.model.OAuthParams"--%>
<style type="text/css">
    .search{width:50px;height: 20px;}
</style>
<html>
  <head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>巨商汇日志平台</title>
  </head>
  <link rel="stylesheet" href="/css/index.css">
  <!-- 新 Bootstrap 核心 CSS 文件 -->
  <link rel="stylesheet" href="/js/bootstrap-3.3.2/css/bootstrap.min.css">
  <link rel="stylesheet" href="/js/jquery/DataTables/css/jquery.dataTables.min.css">
  <link rel="stylesheet" href="/js/jquery/jquery-ui/css/jquery-ui.min.css">
  <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
  <script type="text/javascript" src="/js/jquery.min.js"></script>
  <script type="text/javascript" src="/js/jquery/jquery-form/jquery.form.min.js"></script>
  <script type="text/javascript"  src="/js/jquery/jquery-ui/js/jquery-ui.min.js"></script>
  <script type="text/javascript"  src="/js/jquery/jquery-ui/js/jquery-ui.datepicker.zh-CN.js"></script>

  <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
  <script type="text/javascript"  src="/js/bootstrap-3.3.2/js/bootstrap.min.js"></script>
  <!-- ECharts单文件引入 -->
  <script type="text/javascript"  src="/js/echart/echarts-all.js"></script>
  <script type="text/javascript">
      $(document).ready( function () {

          $("#logSearch input[name='startTime']").datepicker();
          $("#logSearch input[name='endTime']").datepicker();
          // 绑定表单提交事件处理器
          $('#logSearch').submit(function () {
              // 提交表单
              $(this).ajaxSubmit({
                  type: 'post',
                  url: '/stat/statis.do',
                  dataType: 'html',
                  beforeSubmit: function (formArray, jqForm) {
                      var start=$("#logSearch input[name='startTime']").val();
                      var end=$("#logSearch input[name='endTime']").val();
                      var startTime = Date.parse(start);
                      var endTime = Date.parse(end);
                      if (isNaN(startTime)) {
                          $("#errMsg").html("请输入开始时间!");
                          $("#logSearch input[name='startTime']").focus();
                          return false;
                      }
                      if( isNaN(endTime)){
                          $("#errMsg").html("请输入结束时间!");
                          $("#logSearch input[name='endTime']").focus();
                          return false;
                      }
                      if(!/^(\d{4})-(\d{2})-(\d{2})$/.test(start)||!/^(\d{4})-(\d{2})-(\d{2})$/.test(end)){
                          $("#errMsg").html("格式错误!正确格式：2015-01-01");
                          return false;
                      }
                      if (startTime > endTime) {
                          $("#errMsg").html("开始时间不能大于结束时间!");
                          return false;
                      }
                  },
                  success: function (html) {
                      $('#statisDIV').html(html);
                      $("#errMsg").html("");
                  },
                  error: function () {
                      $("#errMsg").html("系统错误!请稍后重试!");
                  }
              });
              // 为了防止普通浏览器进行表单提交和产生页面导航（防止页面刷新？）返回false
              return false;
          });
      });
  </script>
  <body>
  <nav class="navbar navbar-inverse navbar-fixed-top">
      <div class="container-fluid">
          <div class="navbar-header">
              <button class="navbar-toggle collapsed" aria-expanded="false" aria-controls="navbar" type="button" data-toggle="collapse" data-target="#navbar">
                  <span class="sr-only">Toggle navigation</span>
                  <span class="icon-bar"></span>
                  <span class="icon-bar"></span>
                  <span class="icon-bar"></span>
              </button>
              <a class="navbar-brand" href="/log/index.do">巨商汇日志平台</a>
          </div>
          <div class="navbar-collapse collapse" id="navbar">
              <ul class="nav navbar-nav navbar-right" style="margin-right: 30%;">
                  <li><a href="/stat/showStat.do">图表统计</a></li>
              </ul>
          </div>
      </div>
  </nav>

  <div class="panel panel-default">
      <div class="panel-body">
          <form id="logSearch" class="navbar-form navbar-collapse">
              <div style="text-align: center;">
                  <div id="errMsg" style="color: red;font-size: 10pt;text-align: center;width: 100%; height: 12px;margin-bottom: 5px;margin-top: -5px;"></div>
                  <input class="form-control " name="startTime" type="text" placeholder="起始日期">
                  到
                  <input class="form-control " name="endTime" type="text" placeholder="结束日期">
                  <button type="search"  class="btn" >查询</button>
              </div>
          </form>
      </div>
  </div>
  <div id="statisDIV">
        <jsp:include page="statis.jsp"></jsp:include>
  </div>
  </body>
</html>
