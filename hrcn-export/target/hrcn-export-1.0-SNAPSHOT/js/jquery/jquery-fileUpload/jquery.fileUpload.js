/**
 * Created by dyl on 2015/1/30.
 */
jQuery.extend({
    createUploadIframe: function(id, uri)
    {
        //create frame
        var frameId = 'jUploadFrame' + id;

        if(window.ActiveXObject && $.browser && $.browser.version < 9.0) {
            var io = document.createElement('<iframe id="' + frameId + '" name="' + frameId + '" ></iframe>');
            if(typeof uri== 'boolean'){
                io.src = 'javascript:false';
            }
            else if(typeof uri== 'string'){
                io.src = uri;
            }
        } else {
            var io = document.createElement('iframe');
            io.id = frameId;
            io.name = frameId;
        }
        io.style.position = 'absolute';
        io.style.top = '-1000px';
        io.style.left = '-1000px';

        document.body.appendChild(io);

        return io
    },
    createUploadForm: function(id, fileElementId, data)
    {
        //create form
        var formId = 'jUploadForm' + id;
        var fileId = 'jUploadFile' + id;
        var form = $('<form  action="" method="POST" name="' + formId + '" id="' + formId + '" enctype="multipart/form-data"></form>');
        if (data) {
            for (var i in data) {
                if (data[i].name != null && data[i].value != null) {
                    jQuery('<input type="hidden" name="' + data[i].name + '" value="' + data[i].value + '" />').appendTo(form);
                } else {
                    jQuery('<input type="hidden" name="' + i + '" value="' + data[i] + '" />').appendTo(form);
                }
            }
        }
        var oldElement = $('#' + fileElementId);
        var newElement = $(oldElement).clone();
        $(oldElement).attr('id', fileId);
        $(oldElement).before(newElement);
        $(oldElement).appendTo(form);
        //set attributes
        $(form).css('position', 'absolute');
        $(form).css('top', '-1200px');
        $(form).css('left', '-1200px');
        $(form).appendTo('body');
        return form;
    },

    ajaxFileUpload: function(s) {
        s = jQuery.extend({}, jQuery.ajaxSettings, s);
        var id = new Date().getTime()
        var form = jQuery.createUploadForm(id, s.fileElementId, s.data);
        var io = jQuery.createUploadIframe(id, s.secureuri);
        var frameId = 'jUploadFrame' + id;
        var formId = 'jUploadForm' + id;
        // Watch for a new set of requests
        if ( s.global && ! jQuery.active++ )
        {
            jQuery.event.trigger( "ajaxStart" );
        }
        var requestDone = false;
        // Create the request object
        var xml = {}
        if ( s.global )
            jQuery.event.trigger("ajaxSend", [xml, s]);
        // Wait for a response to come back
        var uploadCallback = function(isTimeout)
        {
            var io = document.getElementById(frameId);
            try
            {
                if(io.contentWindow)
                {
                    xml.responseText = io.contentWindow.document.body?io.contentWindow.document.body.innerHTML:null;
                    xml.responseXML = io.contentWindow.document.XMLDocument?io.contentWindow.document.XMLDocument:io.contentWindow.document;

                }else if(io.contentDocument)
                {
                    xml.responseText = io.contentDocument.document.body?io.contentDocument.document.body.innerHTML:null;
                    xml.responseXML = io.contentDocument.document.XMLDocument?io.contentDocument.document.XMLDocument:io.contentDocument.document;
                }
            }catch(e)
            {
                jQuery.handleError(s, xml, null, e);
            }
            if ( xml || isTimeout == "timeout")
            {
                requestDone = true;
                var status;
                try {
                    status = isTimeout != "timeout" ? "success" : "error";
                    // Make sure that the request was successful or notmodified
                    if ( status != "error" )
                    {
                        // process the data (runs the xml through httpData regardless of callback)
                        var data = jQuery.uploadHttpData( xml, s.dataType );
                        // If a local callback was specified, fire it and pass it the data
                        if ( s.success )
                            s.success( data, status );

                        // Fire the global callback
                        if( s.global )
                            jQuery.event.trigger( "ajaxSuccess", [xml, s] );
                    } else
                        jQuery.handleError(s, xml, status);
                } catch(e)
                {
                    status = "error";
                    jQuery.handleError(s, xml, status, e);
                }

                // The request was completed
                if( s.global )
                    jQuery.event.trigger( "ajaxComplete", [xml, s] );

                // Handle the global AJAX counter
                if ( s.global && ! --jQuery.active )
                    jQuery.event.trigger( "ajaxStop" );

                // Process result
                if ( s.complete )
                    s.complete(xml, status);

                jQuery(io).unbind()

                setTimeout(function()
                {	try
                {
                    $(io).remove();
                    $(form).remove();

                } catch(e)
                {
                    jQuery.handleError(s, xml, null, e);
                }

                }, 100)

                xml = null

            }
        }
        // Timeout checker
        if ( s.timeout > 0 )
        {
            setTimeout(function(){
                // Check to see if the request is still happening
                if( !requestDone ) uploadCallback( "timeout" );
            }, s.timeout);
        }
        try
        {
            // var io = $('#' + frameId);
            var form = $('#' + formId);
            $(form).attr('action', s.url);
            $(form).attr('method', 'POST');
            $(form).attr('target', frameId);
            if(form.encoding)
            {
                form.encoding = 'multipart/form-data';
            }
            else
            {
                form.enctype = 'multipart/form-data';
            }
            $(form).submit();

        } catch(e)
        {
            jQuery.handleError(s, xml, null, e);
        }
        if(window.attachEvent){
            document.getElementById(frameId).attachEvent('onload', uploadCallback);
        }
        else{
            document.getElementById(frameId).addEventListener('load', uploadCallback, false);
        }
        return {abort: function () {}};

    },
    uploadHttpData: function( r, type ) {
        var data = !type;
        data = type == "xml" || data ? r.responseXML : r.responseText;
        // If the type is "script", eval it in global context
        if ( type == "script" )
            jQuery.globalEval( data );
        // Get the JavaScript object, if JSON is used.
        if ( type == "json" )
            eval( "data = " + data );
        // evaluate scripts within html
        if ( type == "html" )
            jQuery("<div>").html(data).evalScripts();
        //alert($('param', data).each(function(){alert($(this).attr('value'));}));
        return data;
    }
});

var JshFileUpload = {
    defaults: {
        uploadNumber:1,
        fix:["jpg","jpeg","png","gif","bmp"],
        fileSize: 819200,
        width:100,
        height:100
    },
    init: function( obj ) {
        for( var o in obj ) {
            if ( this.defaults[ o ] && obj[o] ) {
                this.defaults[ o ] = obj[o];
            }
        }
    },
    validate: function( param ) {
        var isValidate = true;
        var imgPath = $("#"+param.id).val() || "";
        if (imgPath == "") {
            alert("请选择上传的文件！");
            isValidate = false;
            return false ;
        }
        //判断上传文件的后缀名
        var isfix = true,
            fixArr = param && param.config && param.config.fix || this.defaults.fix,
            strExtension = imgPath.substr(imgPath.lastIndexOf('.') + 1);

        for ( var fix in fixArr ) {
            if ( fixArr[fix].toLowerCase() == strExtension.toLowerCase() ) {
                isfix = false;
                break;
            }
        }
        if ( isfix ) {
            alert("上传的文件类型不正确！");
            isValidate = false;
            return false ;
        }
        return isValidate;
    },
    fileLoad: function( param ) {
        var id = param.id;
        var url = param.url ||  "../../../ex/jshupload/file";
        var success = param.success || function( data ){};
        var error = param.error || function(data){alert("上传文件过大或网络连接超时！");};
        var returnDataType = param.dataType || "json";
        var data = param.data || "";

        if ( !!url && !!id && !!this.validate( param )) {
            $.ajaxFileUpload({
                url: url,
                data:data,
                secureuri:false,
                fileElementId: id,
                dataType: returnDataType,
                success: function (data) {
                    success( data );
                },
                error: function (data){
                    error( data );
                }
            });
        }
    }
};


function jshFileUpload_add( _this, number, _config ){
    var _this =  _this;
    var number =  number;
    var config = window[_config] || JshFileUpload.defaults;
    var url = config && config.url;
    var filePath =  config.filePath || "";
    var fileId =  "jshUploadFile_file" + number;
    var fileSize = config && config.fileSize || JshFileUpload.defaults.fileSize;
    var fileFix = (config && config.fix && config.fix.length > 0 ? config.fix : JshFileUpload.defaults.fix).join(",");
    // fileSize  fix  uploadNumber width  height

    $("#jshUploadFile_file" + number).attr("name","jshFileName");

    JshFileUpload.fileLoad({
        url: url,
        id: fileId,
        config: config,
        data:{"fileSize": fileSize, "fileFix": fileFix, "filePath": filePath},
        success:function( json ) {

            $("#jshUploadFile_file" + number).removeAttr("name");

            if ( !json.success && json.data[0].fileSize > fileSize ) {
                alert("抱歉，图片因大于" + parseFloat(fileSize/1024).toFixed(0) + "KB。图片无法上传。");
                return false;
            }

            var src = json.data[0].filePath;
            var img = document.getElementById("jshUploadFile_view_img" + number);
            if ( !!img ) {
                img = $(img);
                img.attr("defaultImgSrc", img.attr("src"));
                img.attr("src", src);
            }
            // 清空原有的file中的数据
            $("#jshUploadFile_file" + number).val("");

            // 为隐藏域的赋值
            $("#jshUploadFile_filePath" + number).val(src);
            // 切换按钮
            $("#jshUploadFile_upload" + number).hide();
            $("#jshUploadFile_delete" + number).show();
        }
    });
}
/////////////////////////////框架以后函数///////////////////////////////////
function jshFileUpload_delete( _this, number ) {
    var _this = _this;
    var number = number;
    // 切换按钮
    $("#jshUploadFile_upload" + number).show();
    $("#jshUploadFile_delete" + number).hide();
    $("#jshUploadFile_filePath" + number).val("");
    //  还原图预览图片
    var img = document.getElementById("jshUploadFile_view_img" + number);
    if ( !!img ) {
        img = $(img);
        var defaultImgSrc = img.attr("defaultImgSrc");
        img.attr("src", defaultImgSrc);
    }
}

// 修改时给图片隐藏域的input赋值
var jshUploadFileModify = function ( name, imgPath ) {
    if ( name instanceof Array ) {
        for (var i = 0, len = name.length; i < len; i++ ) {
            var obj = name[ i ];
            $("#jshUploadFile_filePath" + obj.name).val(obj.imgPath);
        }
    } else {
        $("#jshUploadFile_filePath" + name).val(imgPath);
    }
}
// 再修改页面，切换上传成功状态
$(function() {
    if (typeof jshUploadFileModifyArr != "undefined" && jshUploadFileModifyArr instanceof Array ) {
        for (var i = 0, len = jshUploadFileModifyArr.length; i < len; i++ ) {
            var obj = jshUploadFileModifyArr[ i ];
            if (!!obj.imgPath) {
                $("#jshUploadFile_upload" + obj.name).hide();
                $("#jshUploadFile_delete" + obj.name).show();
            }
        }
    }
});