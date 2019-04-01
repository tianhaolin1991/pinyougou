$(function(){
	$(".address").hover(function(){
		//$(this).addClass("address-hover");
        $(this).css("background-color","#ddd");
	},function(){
		$(this).removeAttr("style");
	});
})

$(function(){
    $(".ng-binding").hover(function(){
        //$(this).addClass("address-hover");
        $(this).css("background-color","#ddd");
    },function(){
        $(this).removeAttr("style");
    });
})

$(function(){
	$(".addr-item .name").click(function(){
		 $(this).toggleClass("selected").siblings().removeClass("selected");	
	});
	$(".payType li").click(function(){
		 $(this).toggleClass("selected").siblings().removeClass("selected");	
	});
})
