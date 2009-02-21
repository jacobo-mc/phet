/*
	IE7, version 0.9 (alpha) (2005-08-19)
	Copyright: 2004-2005, Dean Edwards (http://dean.edwards.name/)
	License: http://creativecommons.org/licenses/LGPL/2.1/
*/
IE7.addModule("ie7-fixed",function(){ie7CSS.addRecalc("position","fixed",_6,"absolute");ie7CSS.addRecalc("background(-attachment)?","[^};]*fixed",_7);var _10=(quirksMode)?"body":"documentElement";var _8=function(){if(body.currentStyle.backgroundAttachment!="fixed"){if(body.currentStyle.backgroundImage=="none"){body.runtimeStyle.backgroundRepeat="no-repeat";body.runtimeStyle.backgroundImage="url("+BLANK_GIF+")"}body.runtimeStyle.backgroundAttachment="fixed"}_8=DUMMY};var _0=createTempElement("img");function _1(f){return _2.exec(String(f))};var _2=new ParseMaster;_2.add(/Left/,"Top");_2.add(/left/,"top");_2.add(/Width/,"Height");_2.add(/width/,"height");_2.add(/right/,"bottom");_2.add(/X/,"Y");function _3(e){return(e)?isFixed(e)||_3(e.parentElement):false};function setExpression(e,p,ex){setTimeout("document.all."+e.uniqueID+".runtimeStyle.setExpression('"+p+"','"+ex+"')",0)};function _7(e){if(register(_7,e,e.currentStyle.backgroundAttachment=="fixed"&&!e.contains(body))){_8();backgroundLeft(e);backgroundTop(e);_9(e)}};function _9(e){_0.src=e.currentStyle.backgroundImage.slice(5,-2);var p=(e.canHaveChildren)?e:e.parentElement;p.appendChild(_0);setOffsetLeft(e);setOffsetTop(e);p.removeChild(_0)};function backgroundLeft(e){e.style.backgroundPositionX=e.currentStyle.backgroundPositionX;if(!_3(e)){var ex="(parseInt(runtimeStyle.offsetLeft)+document."+_10+".scrollLeft)||0";setExpression(e,"backgroundPositionX",ex)}};eval(_1(backgroundLeft));function setOffsetLeft(e){var p=_3(e)?"backgroundPositionX":"offsetLeft";e.runtimeStyle[p]=getOffsetLeft(e,e.style.backgroundPositionX)-e.getBoundingClientRect().left-e.clientLeft+2};eval(_1(setOffsetLeft));function getOffsetLeft(e,p){switch(p){case"left":case"top":return 0;case"right":case"bottom":return viewport.clientWidth-_0.offsetWidth;case"center":return(viewport.clientWidth-_0.offsetWidth)/2;default:if(PERCENT.test(p)){return parseInt((viewport.clientWidth-_0.offsetWidth)*parseFloat(p)/100)}_0.style.left=p;return _0.offsetLeft}};eval(_1(getOffsetLeft));function _6(e){if(register(_6,e,isFixed(e))){setOverrideStyle(e,"position","absolute");setOverrideStyle(e,"left",e.currentStyle.left);setOverrideStyle(e,"top",e.currentStyle.top);_8();if(ie7Layout)ie7Layout.fixRight(e);_5(e)}};function _5(e,r){positionTop(e,r);positionLeft(e,r,true);if(!e.runtimeStyle.autoLeft&&e.currentStyle.marginLeft=="auto"&&e.currentStyle.right!="auto"){var l=viewport.clientWidth-getPixelWidth(e,e.currentStyle.right)-getPixelWidth(e,e.runtimeStyle._12)-e.clientWidth;if(e.currentStyle.marginRight=="auto")l=parseInt(l/2);if(_3(e.offsetParent))e.runtimeStyle.pixelLeft+=l;else e.runtimeStyle.shiftLeft=l}clipWidth(e);clipHeight(e)};function clipWidth(e){if(e.currentStyle.width!="auto"){var r=e.getBoundingClientRect();var w=e.offsetWidth-viewport.clientWidth+r.left-2;if(w>=0){w=Math.max(getPixelValue(e,e.currentStyle.width)-w,0);setOverrideStyle(e,"width",w)}}};eval(_1(clipWidth));function positionLeft(e,r){if(!r&&PERCENT.test(e.currentStyle.width)){e.runtimeStyle.fixWidth=e.currentStyle.width}if(e.runtimeStyle.fixWidth){e.runtimeStyle.width=getPixelWidth(e,e.runtimeStyle.fixWidth)}if(r){if(!e.runtimeStyle.autoLeft)return}else{e.runtimeStyle.shiftLeft=0;e.runtimeStyle._12=e.currentStyle.left;e.runtimeStyle.autoLeft=e.currentStyle.right!="auto"&&e.currentStyle.left=="auto"}e.runtimeStyle.left="";e.runtimeStyle.screenLeft=getScreenLeft(e);e.runtimeStyle.pixelLeft=e.runtimeStyle.screenLeft;if(!r&&!_3(e.offsetParent)){var ex="runtimeStyle.screenLeft+runtimeStyle.shiftLeft+document."+_10+".scrollLeft";setExpression(e,"pixelLeft",ex)}};eval(_1(positionLeft));function getScreenLeft(e){var s=e.offsetLeft,n=1;if(e.runtimeStyle.autoLeft){s=viewport.clientWidth-e.offsetWidth-getPixelWidth(e,e.currentStyle.right)}if(e.currentStyle.marginLeft!="auto"){s-=getPixelWidth(e,e.currentStyle.marginLeft)}while(e=e.offsetParent){if(e.currentStyle.position!="static")n=-1;s+=e.offsetLeft*n}return s};eval(_1(getScreenLeft));function getPixelWidth(e,v){if(PERCENT.test(v))return parseInt(parseFloat(v)/100*viewport.clientWidth);return getPixelValue(e,v)};eval(_1(getPixelWidth));function _11(){var e=_7.elements;for(var i in e)_9(e[i]);e=_6.elements;for(i in e){_5(e[i],true);_5(e[i],true)}_4=0};var _4;addResize(function(){if(!_4)_4=setTimeout(_11,0)})});