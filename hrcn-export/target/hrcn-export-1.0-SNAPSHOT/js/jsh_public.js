
//公共的功能函数 及配置
(function($) {
    if(typeof JSH.utils=="undefined"){
        JSH.utils  = {};
    }


        // 阻止表单提交 页面执行方法

     $(function(){
         (function(window,$){
             var  preventreset=function(obj,interval){
                 var $item,$item1,$item2;
                 if(!obj){
                      $item=$('input[preventreset]');
                      $item1=$('button[preventreset]');
                     setdisable($item,interval);
                     setdisable($item1,interval);
                 } else{
                     $item=$(obj);
                     setdisable($item,interval);
                 }
                 function setdisable($obj,interval){
                     var  interval= parseInt(interval)||2000;
                     if($obj[0]){
                         $obj.each(function(){
                             $(this).on("click",function(){
                                 $(this).attr("disabled","disabled");
                                 alert("done");
                                 var that=this;
                                 setTimeout(function(){
                                     $(that).removeAttr("disabled");
                                 },interval)
                             })
                         })
                     }
                 }

             }
             preventreset();
             window["preventreset"]=preventreset;
         })(window,jQuery)

     })




    /*
     * 三级联动下拉框
     * 一级、二级也可用
     * 一级只传一个参数
     * 二级传两个参数
     * $divfirst 一级div
     * $divsecond 二级div
     * $divthird 三级div
     * */
    JSH.utils.areaSelect = function($divfirst,$divsecond,$divthird){
        $divfirst.empty();
        $divfirst.append('<option value="-1">==请选择==</option>');
        $(JSH.province).each(function(index,record){
            $divfirst.append('<option value="'+record.key+'">'+record.text+'</option>');
        });
        if($divsecond){
            $divfirst.change(function(){
                var _this = $(this);
                $divsecond.empty();
                if (  $divthird ) {
                    $divthird.empty();
                }
                $divsecond.append('<option value="-1">==请选择==</option>');
                $(JSH.city[_this.val()]).each(function(index,record){
                    $divsecond.append('<option value="'+record.key+'">'+record.text+'</option>');
                });
                //填写三级数据
                if($divthird){
                    $divsecond.change(function(){
                        var _this = $(this);
                        $divthird.empty();
                        $divthird.append('<option value="-1">==请选择==</option>');
                        $divthird.append('<option value="1">==三级数据待维护==</option>');
                    });
                }
            });
        }

    };
    /*
     * 省份选项卡
     * */
    JSH.utils.areaTab = function($div){
        $div.parent().css("position","relative");
        $div.empty();
        var html = '';
        html += '<div class="tabArea">';
        html += '<div class="tabTitle"><ul></ul><div class="close">x</div></div>';
        html += '<div class="tabCon clearfix">';
        html += '</div>';
        html += '</div>';
        $div.append(html);
        $(JSH.area).each(function(index,record){
            if(index==0){
                $div.find(".tabTitle ul").append('<li class="active">'+record.text+'</li>');
                $div.find(".tabCon").append('<div></div>');
            }else{
                $div.find(".tabTitle ul").append('<li>'+record.text+'</li>');
                $div.find(".tabCon").append('<div class="hide"></div>');
            }
            $(JSH.province).each(function(pindex,precord){
                if(precord.area==record.key){
                    $div.find(".tabCon").children().eq(index).append('<div class="tabAreaItem"><input type="checkbox" name="areaprovince" value="'+precord.key+'"/><span>'+precord.text+'</span></div>');
                }
            });
        });
        $div.find(".tabTitle li").each(function(index){
            var _this = $(this);
            _this.unbind("click").click(function(){
                $div.find(".tabTitle li").removeClass("active");
                _this.addClass("active");
                $div.find(".tabCon").children("div").addClass("hide");
                $div.find(".tabCon").children("div").eq(index).removeClass("hide");
            });
        });
        $div.find(".close").unbind("click").click(function(){
            $div.empty();
        });
    };


//运费模板里显示所有的省市
//参数设置，第一个穿div的id，第二个参数传城市code（显示变灰不能选择），第3个参数是显示或隐藏城市参数，穿0是显示，穿其他是不显示
    JSH.utils.showProvince = function ($div,cityCodeList,isSelfTransport){
        var provicefun_arr=[];
        $div.empty();
        var html = '';
        $(JSH.area).each(function(index,record){
            html += '<div class="provinceItem clearfix">';
            html += '<div class="provinceArea"><input class="checkboxInput" type="checkbox" name="area" value="'+record.key+'"/><span>'+record.text+'</span></div>';
            html += '<div class="provinceList">';

            $(JSH.province).each(function(pindex,precord){

                if(precord.area==record.key){
                    html += '<div class="province">';
                    if(isSelfTransport!=0) {
                        html += '<input class="checkboxInput" type="checkbox" name="province" value="'+precord.key+'"/><span class="hasArrow" style="background: none;">'+precord.text+'</span>';
                    }else{
                        html += '<input class="checkboxInput" type="checkbox" name="province" value="'+precord.key+'"/><span class="hasArrow" >'+precord.text+'</span>';

                        html += '<div class="cityList">';
                        var count=0;
                        $(JSH.city[precord.key]).each(function (cindex, crecord) {
                            var cCode = crecord.key;
                            if (cityCodeList == null || cityCodeList == ""||cityCodeList=="on") {
                                html += '<div class="city"><input class="checkboxInput" type="checkbox" name="city"  value="' + crecord.key + '" provinceCode="' + precord.key + '"/><span>' + crecord.text + '</span></div>';
                            } else {
                                if (cityCodeList.indexOf(cCode) >= 0) {
                                    html += '<div class="city cityDisabled"><input class="checkboxInput" type="checkbox" name="city"  disabled="disabled" value="' + crecord.key + '" provinceCode="' + precord.key + '"/><span>' + crecord.text + '</span></div>';
                                    count++;
                                } else {
                                    html += '<div class="city"><input class="checkboxInput" type="checkbox" name="city"   value="' + crecord.key + '" provinceCode="' + precord.key + '"/><span>' + crecord.text + '</span></div>';
                                }
                            }
                        });
                        //console.log("count:"+count+"citylength:"+JSH.city[precord.key].length);
                        // console.log(JSH.city[precord.key].length);
                        if(JSH.city[precord.key].length==count){
                            var delay="delay"+precord.key;
                            var key=precord.key;
                            var delay=(function(count,key){
                                return function(){
                                    $('input[name="province"][value='+key+']').prop("disabled","disabled");
                                }
                            })(count,key)
                            provicefun_arr.push(delay);
                        }
                        html += '</div>';
                    }
                    html += '</div>';
                }
            });
            html += '</div>';
            html += '</div>';
        });
        $div.append(html);
        if(provicefun_arr.length>0){
            $.each(provicefun_arr,function(index){
                provicefun_arr[index]();
            })
            provicefun_arr.length=0;
        }


        //点击下拉箭头，显示所有城市
        $div.find(".hasArrow").unbind("click").click(function(){
            var _this = $(this);
            $div.find(".cityList").hide();
            _this.parents(".province").find(".cityList").show();
        });

        //如果选择区前面的多选框，则该区的全部省份和市都被选中
        $div.find("input[name='area']").unbind("click").click(function(){
            var _areaThis = $(this);
            if(_areaThis.is(":checked")){
                _areaThis.parents('.provinceItem').find("input[name='province']:not(:disabled)").prop("checked",true);
                _areaThis.parents('.provinceItem').find("input[name='city']:not(:disabled)").prop("checked",true);
                // _areaThis.parents('.provinceItem').find("input[name='province']:disabled").prop("checked",false);
                // _areaThis.parents('.provinceItem').find("input[name='city']:disabled").prop("checked",false);
            }else{
                _areaThis.parents('.provinceItem').find("input[name='province']").prop("checked",false);
                _areaThis.parents('.provinceItem').find("input[name='city']").prop("checked",false);
            }
        });

        //勾选省份，则城市全部被选中
        $div.find("input[name='province']").unbind("click").click(function(){
            var _provinceThis = $(this);
            if(_provinceThis.is(":checked")){

                _provinceThis.parents('.province').find("input[name='city']").prop("checked",true);
                _provinceThis.parents('.province').find("input[name='city']:disabled").prop("checked",false);
            }else{
                _provinceThis.parents('.province').find("input[name='city']").prop("checked",false);
            }
        });

        //点击页面其他处，隐藏城市下拉框
        $(document).click(function(event) {
            if(event.target != $(".cityList") && $(event.target).closest($(".cityList")).length == 0&&event.target != $(".hasArrow") && $(event.target).closest($(".hasArrow")).length == 0){
                $(".cityList").hide();
            }
        });

        //勾选城市的同时，勾选上它所在的省份
        $div.find("input[name='city']").unbind("click").click(function(){
            var _this = $(this);
            if(_this.is(":checked")){
                _this.parents('.province').find("input[name='province']").prop("checked",true);
            }else{
                if(_this.parents('.province').find("input[name='city']:checked").length==0){
                    _this.parents('.province').find("input[name='province']").prop("checked",false);
                }
            }
        });
    };


    /*弹框
     * $div弹框
     * $divMask 弹框遮罩层
     * $close 关闭弹框按钮
     * */
    JSH.utils.pop = function($div,$divMask,$close,defaultFixed){

        //defaultFixed=defaultFixed||false;
        var pageSizeArr = [
                document.body.scrollWidth||document.documentElement.scrollWidth,
                document.body.scrollHeight||document.documentElement.scrollHeight,
                document.body.clientWidth||document.documentElement.clientWidth,
                document.body.clientHeight||document.documentElement.clientHeight
        ];
        $div.css({
            position: (defaultFixed ===false ? 'absolute' : 'fixed'),
            left: '50%',
            marginLeft: 0 - $div.width() / 2,
            top: '50%',
            marginTop: 0 - $div.height() / 2
        }).show();
        var isIE6 = (!!window.ActiveXObject) && (!window.XMLHttpRequest);
        var backgroundiframe ;
        if(isIE6){
            $div.css({
                position:"absolute",
                top:$(window).scrollTop()+$(window).height()/2+"px"
            });
            backgroundiframe = $('<iframe class="dialogMask fixiepng">',{
                id: 'dialogBackgroundiframe',
                frameborder: '0'
            }).css({
                position: 'absolute',
                background: '#343434',
                zIndex: 1002,
                top: 0,
                left: 0,
                width: pageSizeArr[2] > pageSizeArr[0] ? pageSizeArr[2] : pageSizeArr[0],
                height: pageSizeArr[3] > pageSizeArr[1] ? pageSizeArr[3] : pageSizeArr[1],
                zoom:1,
                display: 'none'
            });
            backgroundiframe.css('filter', 'Alpha(Opacity=60)');
            backgroundiframe.appendTo('body');
            backgroundiframe.show();
        }else{
            $("body").append($('<div class="dialogMask fixiepng"></div>').css({
                top: 0,
                left: 0,
                width: pageSizeArr[2] > pageSizeArr[0] ? pageSizeArr[2] : pageSizeArr[0],
                height: pageSizeArr[3] > pageSizeArr[1] ? pageSizeArr[3] : pageSizeArr[1],
                zoom:1}).show());
        }
        $("body").append($div);
        $close.unbind("click").click(function(){
            $div.hide();
            $(".dialogMask").hide().remove();
            if(isIE6){
                $("#dialogBackgroundiframe").hide().remove();
            }
            $div.find("input[type='text']").val("");//清空弹框里的输入内容
            $div.find("select").val("");//重置下拉框
        });
    };
    /**功能组件 与ui相关*/
    JSH.Widget=function(){
        this.boudingBox = null; //属性：最外层容器
    }
    JSH.Widget.prototype={
        on:function(type,handler){
            if (typeof this.handlers[type]=='undefined') {
                this.handlers[type]=[];
            }
            this.handlers[type].push(handler);
            return this;
        },
        fire:function(type,data){
            if (this.handlers[type] instanceof Array) {
                var handlers = this.handlers[type];
                for(var i=0,len=handlers.length;i<len;i++){
                    handlers[i](data);
                }
            };
        },
        render:function(container){		//方法：渲染组件
            this.renderUI();
            this.handlers={};
            this.syncUI(container);

            this.bindUI();

        },
        destroyInternal:function(){
            this.boudingBox.off();
            this.boudingBox.remove();
        },
        destroy:function(){		 //方法：销毁
            this.destructor();
            this.boudingBox.off();
            this.boudingBox.remove();
        },
        renderUI:function(){},	//接口：添加dom节点
        bindUI:function(){},	//接口：监听事件
        syncUI:function(container){},	//接口：初始化组件属性
        destructor:function(){} //接口：销毁前的处理函数 外部的东西
    }
    JSH.utils.newpop=function(options){
        this.cfg = {
            width:558,
            height:245,
            title:'',
            content:"",
            type:0,
            displeartime:3000,
            handler4Displear:"",
            hasCloseBtn:true,
            hasMask:true,
            hasScroll:true,
            isDraggable:false,
            dragHandle:null,
            skinClassName:null,
            handler4CloseBtn:null,
            handler4CancleBtn:null,
            handler4SureBtn:null,
            eventHandler:null   //事件句柄 content内容之间的事件绑定
        };
    }
    JSH.utils.newpop.prototype= $.extend({},new JSH.Widget(),{
        renderUI:function(){
            var html='';
            switch(this.cfg.winType){
                case "show":
                    this.cfg.hasCloseBtn?html='<div class="dialogTop"><span class="dialogTitle">'+this.cfg.title+'</span><a class="dialogClose oppcloseBtn fixiepng" title="关闭"></a></div>':html='<div class="dialogTop"><span class="dialogTitle">'+this.cfg.title+'</span></div>';
                    break;
                case "displear":
                    html='';
                    break;
                case "delete":
                    var title=this.cfg.title||"确认删除";
                    html= '<div class="dialogTop"><span class="dialogTitle">'+title+'</span><a class="dialogClose oppcloseBtn fixiepng" title="关闭"></a></div>';
                    var contenttext=this.cfg.content||"确定要删除吗?";
                    this.cfg.content=
                        '<div class="dialogCon">'+
                        '<div class="windowText">'+contenttext+'</div>'+
                        '<div class="dialogBtnArea clearfix">'+
                        '<input type="button" class="mainSearchBtn J_sureBtn " value="确定"><input type="button" class="mainResetBtn btnLeft J_cancelBtn"   value="取消">'+
                        '</div>'+
                        '</div>';
                    break;
                case "showhtml":
                    html='';
                    break;
                default :
                    html='';
                    break;
            }
            this.boudingBox=$(
                    '<div class="windowWrap">'+
                    '<div class="window_top">'+html+'</div>'+
                    '<div class="window_body">'+this.cfg.content+'</div>'+
                    '</div>'
            )

            /*处理模态*/
            if (this.cfg.hasMask) {
                var window_height=$(document).height();
                this._mask = $('<div class="windowMask"></div>').css("height",window_height);
                this._mask.appendTo("body");
            }

        },
        bindUI:function(){
            var that = this;
            this.boudingBox.delegate(".oppcloseBtn","click",function(){
                that.fire('close');
                that.destroy();
            });
            if(this.boudingBox.find(".J_cancelBtn")[0]){
                this.boudingBox.delegate(".J_cancelBtn","click",function(){
                    that.fire('cancel');
                    that.destroy();
                })
            }
            if(this.boudingBox.find(".J_sureBtn")[0]){
                this.boudingBox.delegate(".J_sureBtn","click",function(){
                    that.fire('sure');
                    that.destroy();
                })
            }
            if(this.cfg.winType=="displear"){
                if(this.cfg.displeartype=="ok"){
                    this.boudingBox.addClass("displear_ok");
                }
                this.boudingBox.fadeOut(this.cfg.displeartime,function(){
                    that.fire("displear");
                    that.destroyInternal();
                });

                this.on("displear",function(){
                    $(window).on("scroll",function(){
                        var scrollTop=$(window).scrollTop();
                        var newtop=  (that.cfg.windowHeight-that.cfg.height)/2+scrollTop+(that.cfg.yfloat||0);
                        that.boudingBox.css("top",newtop);
                    })
                })

            }

            if (this.cfg.handler4CloseBtn) {
                this.on('close',this.cfg.handler4CloseBtn);
            };
            if(this.cfg.handler4Displear){
                this.on("displear",this.cfg.handler4Displear);
            }
            if(this.cfg.handler4CancleBtn){
                this.on("cancel",this.cfg.handler4CancleBtn);
            }
            if(this.cfg.handler4SureBtn){
                this.on("sure",this.cfg.handler4SureBtn);
            }
            this.on("height",function(){
                var scrollTop=$(window).scrollTop();
                var newtop=  (that.cfg.windowHeight-that.boudingBox.height())/2+scrollTop+(that.cfg.yfloat||0);
                that.boudingBox.css("top",newtop);
            })
            this.on("scroll",function(){
                $(window).on("scroll",function(){
//                    var scrollTop=$(window).scrollTop();
//                    var newtop=  (that.cfg.windowHeight-that.cfg.height)/2+scrollTop+(that.cfg.yfloat||0);
//                    that.boudingBox.css("top",newtop);
                    that.fire("height");
                })
            })
            this.on("resize",function(){
                $(window).on("resize",function(){
//                    var scrollTop=$(window).scrollTop();
//                    var newtop=  (that.cfg.windowHeight-that.cfg.height)/2+scrollTop+(that.cfg.yfloat||0);
//                   // var newtop=  200+scrollTop+(that.cfg.yfloat||0); 如果固定top值 弹窗高度很高的话 则显示不完全
//                    that.boudingBox.css("top",newtop);
                    that.fire("height");
                })
            })
            if(this.cfg.hasScroll){
                this.fire("scroll");
            }
            if(this.cfg.eventHandler){
                this.cfg.eventHandler.call(this);
            }
        },
        syncUI:function(container){
            this.cfg.windowScrollTop=$(document).scrollTop();
            this.cfg.windowHeight=$(window).height();
            var float=this.cfg.yfloat||0;
            $(container||document.body).append(this.boudingBox);
            var y=(this.cfg.windowHeight-this.boudingBox.height())/2+this.cfg.windowScrollTop+float; // 相对于浏览器视图高度居中
            this.cfg.newy=y;
            //left:(this.cfg.x||(window.innerWidth-this.cfg.width)/2) + 'px',height:this.cfg.height, 在这里为什么要设置高度呢?
            this.boudingBox.css({
                width:this.cfg.width + 'px',
                marginLeft:-(this.cfg.width/2),
                top:(this.cfg.y||y )+ 'px'
            });
            if (this.cfg.skinClassName) {
                this.boudingBox.addClass(this.cfg.skinClassName);
            };
            if (this.cfg.isDraggable) {
                if (this.cfg.dragHandle) {
                    //$.fn.draggable 判断插件是否存在
                    this.boudingBox.draggable({handle:this.cfg.dragHandle});
                }else{
                    this.boudingBox.draggable();
                }
            };
        },
        destructor:function(){
            this._mask && this._mask.remove();
            var $elsewindowmask=$(".windowMask");
            // 如果页面内还有其他的遮罩 一块删除 这是 打开一个弹窗 对弹窗内容进行更新 后操作
//            if($elsewindowmask[0]){
//                $elsewindowmask.remove();
//            }
        },
        show:function(cfg,container){
            $.extend(this.cfg,cfg,{winType:'show'});
            this.render(container);
            return this;
        },
        displear:function(cfg,container){
            $.extend(this.cfg,cfg,{winType:"displear",skinClassName:"displear",isDraggable:false,hasMask:false});
            this.render(container);
            return this;
        },
        delcfm:function(cfg,container){
            $.extend(this.cfg,cfg,{winType:"delete",isDraggable:false});
            this.render(container);
            return this;
        },
        showhtml:function(cfg,container){
            $.extend(this.cfg,cfg,{winType:"showhtml",isDraggable:false});
            this.render(container);
            return this;
        }
    });
// 弹窗组件 displear 对外的接口
    JSH.utils.displear=function(option,container){
        var options=$.extend({height:50,width:200},option);
        var newpop=new JSH.utils.newpop().displear(options,container);
        return newpop;
    }
    JSH.utils.delcfm=function(option,container){
        var options=$.extend({height:130,width:300},option);
        var newpop=new JSH.utils.newpop().delcfm(options,container);
        return newpop;
    }
    JSH.utils.showhtml=function(option,container){
        var options=$.extend({},option);
        var newpop=new JSH.utils.newpop().showhtml(options,container);
        return newpop;
    }
    JSH.utils.loading=function(option,container){
        var option=option||{};
        var options=$.extend({},option,{content:"<a class=\"loading\"></a>",width:32,skinClassName:"loadingwrap"});
        var newpop=new JSH.utils.newpop().showhtml(options,container);
        return newpop;
    }

    //获取异步加载的html的宽高 这个方法 在ie7下获取的宽度有问题
    JSH.utils.getHtmlWH=function(html,type,floatwidth){
        type=type||"displear";
        var $div=$("<div id='containerTest'></div>");
        $("body").append($div);
        $div.hide();
        $div.html(html);
        var width=$div.width();
        var height=$div.height();
        $div.remove();
        if(type=="displear"){
            floatwidth=  floatwidth||80;
            width=width+floatwidth;
        }
        return  {
            width:width,
            height:height
        }
    }

    /**tab函数**/
    var displaytab = function (option) {
        this.config = {
            root: ".J_tab",
            tab_menu: ".J_tab_menu",
            tab_box: ".J_tab_box",
            listmenu_tag: "a",
            listbox_tag: "",
            efftct: "display",
            currcls: ".curr",
            hovercls: ".hover",
            event: "click",
            intervaltime: 3000,
            clsposition: 0,
            type: 0,
            autoplay: false
        };
        this.init(option);
    }
    displaytab.prototype = {
        init: function (option) {
            this.config = $.extend(true, this.config, option);
            var self = this,
                _config = self.config;
            var firstrun = true;
            $(_config.root).each(function () {
                var $root = $(this),
                    $menu = $root.find(_config.tab_menu),
                    $list_menu = $menu.find(_config.listmenu_tag),
                    $list_box = _config.listbox_tag == "" ? $root.find(_config.tab_box).children() : $root.find(_config.tab_box).find(_config.listbox_tag);
                var index = 0,
                    length = $list_menu.length;
                $menu.on(_config.event, _config.listmenu_tag, function (event) {
                    index = getindex(this);
                    tab(index);
                    event.preventDefault();
                })

                function getindex(obj) {
                    var num;
                    if (_config.clsposition != 0) {
                        switch (_config.clsposition) {
                            case 1:
                                num = $(obj).parent().index();
                                break;
                            case 2:
                                num = $(obj).parent().parent().index();
                                break;
                        }
                    } else {
                        num = $(obj).index();
                    }

                    return num;

                }
                function tab(index) {
                    $list_menu.addClass(self.formatclass(_config.currcls)).removeClass(self.formatclass(_config.hovercls));
                    $list_menu.eq(index).removeClass(self.formatclass(_config.currcls)).addClass(self.formatclass(_config.hovercls));
                    $list_box.css("display", "none");
                    $list_box.eq(index).css("display", "block");
                }
                if (_config.autoplay) {
                    var timer = setInterval(function () {
                        index++;
                        if (index >= length) {
                            index = 0;
                        }
                        tab(index);
                    }, _config.intervaltime)

                    $list_box.hover(function () {
                        clearInterval(timer);
                    }, function () {
                        timer = setInterval(function () {
                            index++;
                            if (index >= length) {
                                index = 0;
                            }
                            tab(index);
                        }, _config.intervaltime)
                    })
                }
            })
        },
        formatclass: function (str) {
            var result = "";
            if (str.charAt(0) == ".") {
                result = str.slice(1);
            } else {
                result = str;
            }
            return result;
        }
    };
    JSH.utils.distab=function(options){

        return new displaytab(options);
    }
    /*处理ajax*/
    JSH.utils.ajax = function(params,url){

    };
    /*** input text 的 时时值change事件改变 要操作的函数***/
    JSH.utils.textchange=function(id,fn){
        if("\v"=="v"){
            document.getElementById(id).attachEvent("onpropertychange",function(){
                if(fn){
                    fn();
                }
            })
        } else{
            document.getElementById(id).addEventListener("input",function(){
                if(fn){
                    fn();
                }
            },false);
        }
    }
    JSH.utils.mobileVerify=function(selector){
        var count=60;
        var $btn1=$(selector);
        $btn1.attr("disabled", true);
        $btn1.html("倒计时:" + count + "s");
        var  timer=setInterval(function(){
            count--;
            $btn1.html("倒计时:" + count + "s");
            if(count<0){
                $btn1.html("再次获取校验码");
                $btn1.attr("disabled", false);
                clearInterval(timer);
            }
        },1000)
    }
    /***分页函数****/
    var paging=function(options){
        var defaults={
            root:"",
            url:"",
            type:"post",
            data:null,
            pageNumber:1,
            pageSize:10,
            successeventhandler:null,
            elementpagenow:".ext-pager-pn",
            elementpagesize:".ext-pager-ps",
            elementpagebutton:".ext-pager-button button"
        }
        this.options= $.extend({},defaults,options);
        this.options['$root']=$(this.options.root);
        this.options.oldpageNumber=this.options.pageNumber;
        this.options.oldpageSize=this.options.pageSize;
        this.init();
    }
    paging.prototype={
        init:function(){
            this.getInfo();
            this.blindUI();
        },
        reset:function(){
          this.setPageNumber(this.options.oldpageNumber);
          this.setPageSize(this.options.oldpageSize);
        },
        getPageNumber:function(){
            return this.options.pageNumber;
        },
        getPageSize:function(){
            return this.options.pageSize;
        },
        setPageNumber:function(num){
            if(!isNaN(num)){
                this.options.pageNumber=num;
            }
        },
        setPageSize:function(num){
            if(!isNaN(num)){
                this.options.pageSize=num;
            }
        },
        getInfo:function(){
            var that=this.options;
            var newdata="";
            if(typeof that.data=="string"){
                newdata='pageNumber='+that.pageNumber+''+"&"+'pageSize='+that.pageSize+''+"&"+that.data;
            } else if(typeof that.data=="object") {
                newdata = $.extend({}, {pageNumber: that.pageNumber, pageSize: that.pageSize}, this.data);
            }
            $.ajax({
                type: that.type,
                url: that.url,
                data: newdata,
                dataType: 'html',
                success: function(html) {
                    that.$root.html(html);
                    if(that.successeventhandler){
                        that.successeventhandler();
                    }
                },
                error:function(err){

                }
            })
        },
        blindUI:function(){
            var  that=this.options;
            var _this=this;
            var $root=that.$root;
            $root.on("click",that.elementpagebutton,function(){
               var data=$(this).data("pn");
               _this.setPageNumber(data);
               _this.getInfo();
           });
            $root.on("keydown",that.elementpagenow,function(event){
                if (event.keyCode == '13') {
                    var pageNumber = $(this).val();
                    if(isNaN(pageNumber)) {
                        JSH.utils.displear({
                            content:"请输入正整数"
                        })
                        $(this).val(1);
                        return false;
                    } else {
                         _this.setPageNumber(pageNumber);
                         _this.getInfo();
                    }
                }
            })
            $root.on("keydown",that.elementpagesize,function(event){
                if (event.keyCode == '13') {
                    var pagesize = $(this).val();
                    if(isNaN(pagesize)) {
                        JSH.utils.displear({
                            content:"请输入正整数"
                        })
                        $(this).val(that.oldpageSize);
                        return false;
                    } else {
                        _this.setPageSize(pagesize);
                        _this.getInfo();
                    }
                }
            })
        }
    }

    JSH.utils.paging=function(options){
        return new paging(options);
    }


})(jQuery);

