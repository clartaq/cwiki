/*
  Highlight.js 10.5.0 (af20048d)
  License: BSD-3-Clause
  Copyright (c) 2006-2020, Ivan Sagalaev
*/
var hljs=function(){"use strict";function e(t){
return t instanceof Map?t.clear=t.delete=t.set=()=>{
throw Error("map is read-only")}:t instanceof Set&&(t.add=t.clear=t.delete=()=>{
throw Error("set is read-only")
}),Object.freeze(t),Object.getOwnPropertyNames(t).forEach((n=>{var s=t[n]
;"object"!=typeof s||Object.isFrozen(s)||e(s)})),t}var t=e,n=e;t.default=n
;class s{constructor(e){void 0===e.data&&(e.data={}),this.data=e.data}
ignoreMatch(){this.ignore=!0}}function r(e){
return e.replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/"/g,"&quot;").replace(/'/g,"&#x27;")
}function a(e,...t){const n=Object.create(null);for(const t in e)n[t]=e[t]
;return t.forEach((e=>{for(const t in e)n[t]=e[t]})),n}const i=e=>!!e.kind
;class o{constructor(e,t){
this.buffer="",this.classPrefix=t.classPrefix,e.walk(this)}addText(e){
this.buffer+=r(e)}openNode(e){if(!i(e))return;let t=e.kind
;e.sublanguage||(t=`${this.classPrefix}${t}`),this.span(t)}closeNode(e){
i(e)&&(this.buffer+="</span>")}value(){return this.buffer}span(e){
this.buffer+=`<span class="${e}">`}}class l{constructor(){this.rootNode={
children:[]},this.stack=[this.rootNode]}get top(){
return this.stack[this.stack.length-1]}get root(){return this.rootNode}add(e){
this.top.children.push(e)}openNode(e){const t={kind:e,children:[]}
;this.add(t),this.stack.push(t)}closeNode(){
if(this.stack.length>1)return this.stack.pop()}closeAllNodes(){
for(;this.closeNode(););}toJSON(){return JSON.stringify(this.rootNode,null,4)}
walk(e){return this.constructor._walk(e,this.rootNode)}static _walk(e,t){
return"string"==typeof t?e.addText(t):t.children&&(e.openNode(t),
t.children.forEach((t=>this._walk(e,t))),e.closeNode(t)),e}static _collapse(e){
"string"!=typeof e&&e.children&&(e.children.every((e=>"string"==typeof e))?e.children=[e.children.join("")]:e.children.forEach((e=>{
l._collapse(e)})))}}class c extends l{constructor(e){super(),this.options=e}
addKeyword(e,t){""!==e&&(this.openNode(t),this.addText(e),this.closeNode())}
addText(e){""!==e&&this.add(e)}addSublanguage(e,t){const n=e.root
;n.kind=t,n.sublanguage=!0,this.add(n)}toHTML(){
return new o(this,this.options).value()}finalize(){return!0}}function u(e){
return e?"string"==typeof e?e:e.source:null}
const g="[a-zA-Z]\\w*",d="[a-zA-Z_]\\w*",h="\\b\\d+(\\.\\d+)?",f="(-?)(\\b0[xX][a-fA-F0-9]+|(\\b\\d+(\\.\\d*)?|\\.\\d+)([eE][-+]?\\d+)?)",p="\\b(0b[01]+)",m={
begin:"\\\\[\\s\\S]",relevance:0},b={className:"string",begin:"'",end:"'",
illegal:"\\n",contains:[m]},x={className:"string",begin:'"',end:'"',
illegal:"\\n",contains:[m]},E={
begin:/\b(a|an|the|are|I'm|isn't|don't|doesn't|won't|but|just|should|pretty|simply|enough|gonna|going|wtf|so|such|will|you|your|they|like|more)\b/
},v=(e,t,n={})=>{const s=a({className:"comment",begin:e,end:t,contains:[]},n)
;return s.contains.push(E),s.contains.push({className:"doctag",
begin:"(?:TODO|FIXME|NOTE|BUG|OPTIMIZE|HACK|XXX):",relevance:0}),s
},N=v("//","$"),w=v("/\\*","\\*/"),R=v("#","$");var y=Object.freeze({
__proto__:null,IDENT_RE:g,UNDERSCORE_IDENT_RE:d,NUMBER_RE:h,C_NUMBER_RE:f,
BINARY_NUMBER_RE:p,
RE_STARTERS_RE:"!|!=|!==|%|%=|&|&&|&=|\\*|\\*=|\\+|\\+=|,|-|-=|/=|/|:|;|<<|<<=|<=|<|===|==|=|>>>=|>>=|>=|>>>|>>|>|\\?|\\[|\\{|\\(|\\^|\\^=|\\||\\|=|\\|\\||~",
SHEBANG:(e={})=>{const t=/^#![ ]*\//
;return e.binary&&(e.begin=((...e)=>e.map((e=>u(e))).join(""))(t,/.*\b/,e.binary,/\b.*/)),
a({className:"meta",begin:t,end:/$/,relevance:0,"on:begin":(e,t)=>{
0!==e.index&&t.ignoreMatch()}},e)},BACKSLASH_ESCAPE:m,APOS_STRING_MODE:b,
QUOTE_STRING_MODE:x,PHRASAL_WORDS_MODE:E,COMMENT:v,C_LINE_COMMENT_MODE:N,
C_BLOCK_COMMENT_MODE:w,HASH_COMMENT_MODE:R,NUMBER_MODE:{className:"number",
begin:h,relevance:0},C_NUMBER_MODE:{className:"number",begin:f,relevance:0},
BINARY_NUMBER_MODE:{className:"number",begin:p,relevance:0},CSS_NUMBER_MODE:{
className:"number",
begin:h+"(%|em|ex|ch|rem|vw|vh|vmin|vmax|cm|mm|in|pt|pc|px|deg|grad|rad|turn|s|ms|Hz|kHz|dpi|dpcm|dppx)?",
relevance:0},REGEXP_MODE:{begin:/(?=\/[^/\n]*\/)/,contains:[{className:"regexp",
begin:/\//,end:/\/[gimuy]*/,illegal:/\n/,contains:[m,{begin:/\[/,end:/\]/,
relevance:0,contains:[m]}]}]},TITLE_MODE:{className:"title",begin:g,relevance:0
},UNDERSCORE_TITLE_MODE:{className:"title",begin:d,relevance:0},METHOD_GUARD:{
begin:"\\.\\s*[a-zA-Z_]\\w*",relevance:0},END_SAME_AS_BEGIN:e=>Object.assign(e,{
"on:begin":(e,t)=>{t.data._beginMatch=e[1]},"on:end":(e,t)=>{
t.data._beginMatch!==e[1]&&t.ignoreMatch()}})});function _(e,t){
"."===e.input[e.index-1]&&t.ignoreMatch()}function k(e,t){
t&&e.beginKeywords&&(e.begin="\\b("+e.beginKeywords.split(" ").join("|")+")(?!\\.)(?=\\b|\\s)",
e.__beforeBegin=_,e.keywords=e.keywords||e.beginKeywords,delete e.beginKeywords)
}function M(e,t){
Array.isArray(e.illegal)&&(e.illegal=((...e)=>"("+e.map((e=>u(e))).join("|")+")")(...e.illegal))
}function O(e,t){if(e.match){
if(e.begin||e.end)throw Error("begin & end are not supported with match")
;e.begin=e.match,delete e.match}}function A(e,t){
void 0===e.relevance&&(e.relevance=1)}
const L=["of","and","for","in","not","or","if","then","parent","list","value"]
;function B(e,t){return t?Number(t):(e=>L.includes(e.toLowerCase()))(e)?0:1}
function I(e,{plugins:t}){function n(t,n){
return RegExp(u(t),"m"+(e.case_insensitive?"i":"")+(n?"g":""))}class s{
constructor(){
this.matchIndexes={},this.regexes=[],this.matchAt=1,this.position=0}
addRule(e,t){
t.position=this.position++,this.matchIndexes[this.matchAt]=t,this.regexes.push([t,e]),
this.matchAt+=(e=>RegExp(e.toString()+"|").exec("").length-1)(e)+1}compile(){
0===this.regexes.length&&(this.exec=()=>null)
;const e=this.regexes.map((e=>e[1]));this.matcherRe=n(((e,t="|")=>{
const n=/\[(?:[^\\\]]|\\.)*\]|\(\??|\\([1-9][0-9]*)|\\./;let s=0,r=""
;for(let a=0;a<e.length;a++){s+=1;const i=s;let o=u(e[a])
;for(a>0&&(r+=t),r+="(";o.length>0;){const e=n.exec(o);if(null==e){r+=o;break}
r+=o.substring(0,e.index),
o=o.substring(e.index+e[0].length),"\\"===e[0][0]&&e[1]?r+="\\"+(Number(e[1])+i):(r+=e[0],
"("===e[0]&&s++)}r+=")"}return r})(e),!0),this.lastIndex=0}exec(e){
this.matcherRe.lastIndex=this.lastIndex;const t=this.matcherRe.exec(e)
;if(!t)return null
;const n=t.findIndex(((e,t)=>t>0&&void 0!==e)),s=this.matchIndexes[n]
;return t.splice(0,n),Object.assign(t,s)}}class r{constructor(){
this.rules=[],this.multiRegexes=[],
this.count=0,this.lastIndex=0,this.regexIndex=0}getMatcher(e){
if(this.multiRegexes[e])return this.multiRegexes[e];const t=new s
;return this.rules.slice(e).forEach((([e,n])=>t.addRule(e,n))),
t.compile(),this.multiRegexes[e]=t,t}resumingScanAtSamePosition(){
return 0!==this.regexIndex}considerAll(){this.regexIndex=0}addRule(e,t){
this.rules.push([e,t]),"begin"===t.type&&this.count++}exec(e){
const t=this.getMatcher(this.regexIndex);t.lastIndex=this.lastIndex
;let n=t.exec(e)
;if(this.resumingScanAtSamePosition())if(n&&n.index===this.lastIndex);else{
const t=this.getMatcher(0);t.lastIndex=this.lastIndex+1,n=t.exec(e)}
return n&&(this.regexIndex+=n.position+1,
this.regexIndex===this.count&&this.considerAll()),n}}
if(e.compilerExtensions||(e.compilerExtensions=[]),
e.contains&&e.contains.includes("self"))throw Error("ERR: contains `self` is not supported at the top-level of a language.  See documentation.")
;return e.classNameAliases=a(e.classNameAliases||{}),function t(s,i){const o=s
;if(s.compiled)return o
;[O].forEach((e=>e(s,i))),e.compilerExtensions.forEach((e=>e(s,i))),
s.__beforeBegin=null,[k,M,A].forEach((e=>e(s,i))),s.compiled=!0;let l=null
;if("object"==typeof s.keywords&&(l=s.keywords.$pattern,
delete s.keywords.$pattern),s.keywords&&(s.keywords=((e,t)=>{const n={}
;return"string"==typeof e?s("keyword",e):Object.keys(e).forEach((t=>{s(t,e[t])
})),n;function s(e,s){t&&(s=s.toLowerCase()),s.split(" ").forEach((t=>{
const s=t.split("|");n[s[0]]=[e,B(s[0],s[1])]}))}
})(s.keywords,e.case_insensitive)),
s.lexemes&&l)throw Error("ERR: Prefer `keywords.$pattern` to `mode.lexemes`, BOTH are not allowed. (see mode reference) ")
;return l=l||s.lexemes||/\w+/,
o.keywordPatternRe=n(l,!0),i&&(s.begin||(s.begin=/\B|\b/),
o.beginRe=n(s.begin),s.endSameAsBegin&&(s.end=s.begin),
s.end||s.endsWithParent||(s.end=/\B|\b/),
s.end&&(o.endRe=n(s.end)),o.terminatorEnd=u(s.end)||"",
s.endsWithParent&&i.terminatorEnd&&(o.terminatorEnd+=(s.end?"|":"")+i.terminatorEnd)),
s.illegal&&(o.illegalRe=n(s.illegal)),
s.contains||(s.contains=[]),s.contains=[].concat(...s.contains.map((e=>(e=>(e.variants&&!e.cachedVariants&&(e.cachedVariants=e.variants.map((t=>a(e,{
variants:null},t)))),e.cachedVariants?e.cachedVariants:T(e)?a(e,{
starts:e.starts?a(e.starts):null
}):Object.isFrozen(e)?a(e):e))("self"===e?s:e)))),s.contains.forEach((e=>{t(e,o)
})),s.starts&&t(s.starts,i),o.matcher=(e=>{const t=new r
;return e.contains.forEach((e=>t.addRule(e.begin,{rule:e,type:"begin"
}))),e.terminatorEnd&&t.addRule(e.terminatorEnd,{type:"end"
}),e.illegal&&t.addRule(e.illegal,{type:"illegal"}),t})(o),o}(e)}function T(e){
return!!e&&(e.endsWithParent||T(e.starts))}function j(e){const t={
props:["language","code","autodetect"],data:()=>({detectedLanguage:"",
unknownLanguage:!1}),computed:{className(){
return this.unknownLanguage?"":"hljs "+this.detectedLanguage},highlighted(){
if(!this.autoDetect&&!e.getLanguage(this.language))return console.warn(`The language "${this.language}" you specified could not be found.`),
this.unknownLanguage=!0,r(this.code);let t={}
;return this.autoDetect?(t=e.highlightAuto(this.code),
this.detectedLanguage=t.language):(t=e.highlight(this.language,this.code,this.ignoreIllegals),
this.detectedLanguage=this.language),t.value},autoDetect(){
return!(this.language&&(e=this.autodetect,!e&&""!==e));var e},
ignoreIllegals:()=>!0},render(e){return e("pre",{},[e("code",{
class:this.className,domProps:{innerHTML:this.highlighted}})])}};return{
Component:t,VuePlugin:{install(e){e.component("highlightjs",t)}}}}const S={
"after:highlightBlock":({block:e,result:t,text:n})=>{const s=D(e)
;if(!s.length)return;const a=document.createElement("div")
;a.innerHTML=t.value,t.value=((e,t,n)=>{let s=0,a="";const i=[];function o(){
return e.length&&t.length?e[0].offset!==t[0].offset?e[0].offset<t[0].offset?e:t:"start"===t[0].event?e:t:e.length?e:t
}function l(e){a+="<"+P(e)+[].map.call(e.attributes,(function(e){
return" "+e.nodeName+'="'+r(e.value)+'"'})).join("")+">"}function c(e){
a+="</"+P(e)+">"}function u(e){("start"===e.event?l:c)(e.node)}
for(;e.length||t.length;){let t=o()
;if(a+=r(n.substring(s,t[0].offset)),s=t[0].offset,t===e){i.reverse().forEach(c)
;do{u(t.splice(0,1)[0]),t=o()}while(t===e&&t.length&&t[0].offset===s)
;i.reverse().forEach(l)
}else"start"===t[0].event?i.push(t[0].node):i.pop(),u(t.splice(0,1)[0])}
return a+r(n.substr(s))})(s,D(a),n)}};function P(e){
return e.nodeName.toLowerCase()}function D(e){const t=[];return function e(n,s){
for(let r=n.firstChild;r;r=r.nextSibling)3===r.nodeType?s+=r.nodeValue.length:1===r.nodeType&&(t.push({
event:"start",offset:s,node:r}),s=e(r,s),P(r).match(/br|hr|img|input/)||t.push({
event:"stop",offset:s,node:r}));return s}(e,0),t}const C=e=>{console.error(e)
},H=(e,...t)=>{console.log("WARN: "+e,...t)},$=(e,t)=>{
console.log(`Deprecated as of ${e}. ${t}`)},U=r,z=a,K=Symbol("nomatch")
;return(e=>{const n=Object.create(null),r=Object.create(null),a=[];let i=!0
;const o=/(^(<[^>]+>|\t|)+|\n)/gm,l="Could not find the language '{}', did you forget to load/include a language module?",u={
disableAutodetect:!0,name:"Plain text",contains:[]};let g={
noHighlightRe:/^(no-?highlight)$/i,
languageDetectRe:/\blang(?:uage)?-([\w-]+)\b/i,classPrefix:"hljs-",
tabReplace:null,useBR:!1,languages:null,__emitter:c};function d(e){
return g.noHighlightRe.test(e)}function h(e,t,n,s){const r={code:t,language:e}
;_("before:highlight",r);const a=r.result?r.result:f(r.language,r.code,n,s)
;return a.code=r.code,_("after:highlight",a),a}function f(e,t,r,o){const c=t
;function u(e,t){const n=w.case_insensitive?t[0].toLowerCase():t[0]
;return Object.prototype.hasOwnProperty.call(e.keywords,n)&&e.keywords[n]}
function d(){null!=_.subLanguage?(()=>{if(""===O)return;let e=null
;if("string"==typeof _.subLanguage){
if(!n[_.subLanguage])return void M.addText(O)
;e=f(_.subLanguage,O,!0,k[_.subLanguage]),k[_.subLanguage]=e.top
}else e=p(O,_.subLanguage.length?_.subLanguage:null)
;_.relevance>0&&(A+=e.relevance),M.addSublanguage(e.emitter,e.language)
})():(()=>{if(!_.keywords)return void M.addText(O);let e=0
;_.keywordPatternRe.lastIndex=0;let t=_.keywordPatternRe.exec(O),n="";for(;t;){
n+=O.substring(e,t.index);const s=u(_,t);if(s){const[e,r]=s
;M.addText(n),n="",A+=r;const a=w.classNameAliases[e]||e;M.addKeyword(t[0],a)
}else n+=t[0];e=_.keywordPatternRe.lastIndex,t=_.keywordPatternRe.exec(O)}
n+=O.substr(e),M.addText(n)})(),O=""}function h(e){
return e.className&&M.openNode(w.classNameAliases[e.className]||e.className),
_=Object.create(e,{parent:{value:_}}),_}function m(e,t,n){let r=((e,t)=>{
const n=e&&e.exec(t);return n&&0===n.index})(e.endRe,n);if(r){if(e["on:end"]){
const n=new s(e);e["on:end"](t,n),n.ignore&&(r=!1)}if(r){
for(;e.endsParent&&e.parent;)e=e.parent;return e}}
if(e.endsWithParent)return m(e.parent,t,n)}function b(e){
return 0===_.matcher.regexIndex?(O+=e[0],1):(T=!0,0)}function x(e){
const t=e[0],n=c.substr(e.index),s=m(_,e,n);if(!s)return K;const r=_
;r.skip?O+=t:(r.returnEnd||r.excludeEnd||(O+=t),d(),r.excludeEnd&&(O=t));do{
_.className&&M.closeNode(),_.skip||_.subLanguage||(A+=_.relevance),_=_.parent
}while(_!==s.parent)
;return s.starts&&(s.endSameAsBegin&&(s.starts.endRe=s.endRe),
h(s.starts)),r.returnEnd?0:t.length}let E={};function v(t,n){const a=n&&n[0]
;if(O+=t,null==a)return d(),0
;if("begin"===E.type&&"end"===n.type&&E.index===n.index&&""===a){
if(O+=c.slice(n.index,n.index+1),!i){const t=Error("0 width match regex")
;throw t.languageName=e,t.badRule=E.rule,t}return 1}
if(E=n,"begin"===n.type)return function(e){
const t=e[0],n=e.rule,r=new s(n),a=[n.__beforeBegin,n["on:begin"]]
;for(const n of a)if(n&&(n(e,r),r.ignore))return b(t)
;return n&&n.endSameAsBegin&&(n.endRe=RegExp(t.replace(/[-/\\^$*+?.()|[\]{}]/g,"\\$&"),"m")),
n.skip?O+=t:(n.excludeBegin&&(O+=t),
d(),n.returnBegin||n.excludeBegin||(O=t)),h(n),n.returnBegin?0:t.length}(n)
;if("illegal"===n.type&&!r){
const e=Error('Illegal lexeme "'+a+'" for mode "'+(_.className||"<unnamed>")+'"')
;throw e.mode=_,e}if("end"===n.type){const e=x(n);if(e!==K)return e}
if("illegal"===n.type&&""===a)return 1
;if(B>1e5&&B>3*n.index)throw Error("potential infinite loop, way more iterations than matches")
;return O+=a,a.length}const w=N(e)
;if(!w)throw C(l.replace("{}",e)),Error('Unknown language: "'+e+'"')
;const R=I(w,{plugins:a});let y="",_=o||R;const k={},M=new g.__emitter(g);(()=>{
const e=[];for(let t=_;t!==w;t=t.parent)t.className&&e.unshift(t.className)
;e.forEach((e=>M.openNode(e)))})();let O="",A=0,L=0,B=0,T=!1;try{
for(_.matcher.considerAll();;){
B++,T?T=!1:_.matcher.considerAll(),_.matcher.lastIndex=L
;const e=_.matcher.exec(c);if(!e)break;const t=v(c.substring(L,e.index),e)
;L=e.index+t}return v(c.substr(L)),M.closeAllNodes(),M.finalize(),y=M.toHTML(),{
relevance:A,value:y,language:e,illegal:!1,emitter:M,top:_}}catch(t){
if(t.message&&t.message.includes("Illegal"))return{illegal:!0,illegalBy:{
msg:t.message,context:c.slice(L-100,L+100),mode:t.mode},sofar:y,relevance:0,
value:U(c),emitter:M};if(i)return{illegal:!1,relevance:0,value:U(c),emitter:M,
language:e,top:_,errorRaised:t};throw t}}function p(e,t){
t=t||g.languages||Object.keys(n);const s=(e=>{const t={relevance:0,
emitter:new g.__emitter(g),value:U(e),illegal:!1,top:u}
;return t.emitter.addText(e),t})(e),r=t.filter(N).filter(R).map((t=>f(t,e,!1)))
;r.unshift(s);const a=r.sort(((e,t)=>{
if(e.relevance!==t.relevance)return t.relevance-e.relevance
;if(e.language&&t.language){if(N(e.language).supersetOf===t.language)return 1
;if(N(t.language).supersetOf===e.language)return-1}return 0})),[i,o]=a,l=i
;return l.second_best=o,l}const m={"before:highlightBlock":({block:e})=>{
g.useBR&&(e.innerHTML=e.innerHTML.replace(/\n/g,"").replace(/<br[ /]*>/g,"\n"))
},"after:highlightBlock":({result:e})=>{
g.useBR&&(e.value=e.value.replace(/\n/g,"<br>"))}},b=/^(<[^>]+>|\t)+/gm,x={
"after:highlightBlock":({result:e})=>{
g.tabReplace&&(e.value=e.value.replace(b,(e=>e.replace(/\t/g,g.tabReplace))))}}
;function E(e){let t=null;const n=(e=>{let t=e.className+" "
;t+=e.parentNode?e.parentNode.className:"";const n=g.languageDetectRe.exec(t)
;if(n){const t=N(n[1])
;return t||(H(l.replace("{}",n[1])),H("Falling back to no-highlight mode for this block.",e)),
t?n[1]:"no-highlight"}return t.split(/\s+/).find((e=>d(e)||N(e)))})(e)
;if(d(n))return;_("before:highlightBlock",{block:e,language:n}),t=e
;const s=t.textContent,a=n?h(n,s,!0):p(s);_("after:highlightBlock",{block:e,
result:a,text:s}),e.innerHTML=a.value,((e,t,n)=>{const s=t?r[t]:n
;e.classList.add("hljs"),s&&e.classList.add(s)})(e,n,a.language),e.result={
language:a.language,re:a.relevance,relavance:a.relevance
},a.second_best&&(e.second_best={language:a.second_best.language,
re:a.second_best.relevance,relavance:a.second_best.relevance})}const v=()=>{
v.called||(v.called=!0,document.querySelectorAll("pre code").forEach(E))}
;function N(e){return e=(e||"").toLowerCase(),n[e]||n[r[e]]}
function w(e,{languageName:t}){"string"==typeof e&&(e=[e]),e.forEach((e=>{r[e]=t
}))}function R(e){const t=N(e);return t&&!t.disableAutodetect}function _(e,t){
const n=e;a.forEach((e=>{e[n]&&e[n](t)}))}Object.assign(e,{highlight:h,
highlightAuto:p,fixMarkup:e=>{
return $("10.2.0","fixMarkup will be removed entirely in v11.0"),
$("10.2.0","Please see https://github.com/highlightjs/highlight.js/issues/2534"),
t=e,
g.tabReplace||g.useBR?t.replace(o,(e=>"\n"===e?g.useBR?"<br>":e:g.tabReplace?e.replace(/\t/g,g.tabReplace):e)):t
;var t},highlightBlock:E,configure:e=>{
e.useBR&&($("10.3.0","'useBR' will be removed entirely in v11.0"),
$("10.3.0","Please see https://github.com/highlightjs/highlight.js/issues/2559")),
g=z(g,e)},initHighlighting:v,initHighlightingOnLoad:()=>{
window.addEventListener("DOMContentLoaded",v,!1)},registerLanguage:(t,s)=>{
let r=null;try{r=s(e)}catch(e){
if(C("Language definition for '{}' could not be registered.".replace("{}",t)),
!i)throw e;C(e),r=u}
r.name||(r.name=t),n[t]=r,r.rawDefinition=s.bind(null,e),r.aliases&&w(r.aliases,{
languageName:t})},listLanguages:()=>Object.keys(n),getLanguage:N,
registerAliases:w,requireLanguage:e=>{
$("10.4.0","requireLanguage will be removed entirely in v11."),
$("10.4.0","Please see https://github.com/highlightjs/highlight.js/pull/2844")
;const t=N(e);if(t)return t
;throw Error("The '{}' language is required, but not loaded.".replace("{}",e))},
autoDetection:R,inherit:z,addPlugin:e=>{a.push(e)},vuePlugin:j(e).VuePlugin
}),e.debugMode=()=>{i=!1},e.safeMode=()=>{i=!0},e.versionString="10.5.0"
;for(const e in y)"object"==typeof y[e]&&t(y[e])
;return Object.assign(e,y),e.addPlugin(m),e.addPlugin(S),e.addPlugin(x),e})({})
}();"object"==typeof exports&&"undefined"!=typeof module&&(module.exports=hljs);hljs.registerLanguage("bash",(()=>{"use strict";function e(...e){
return e.map((e=>{return(s=e)?"string"==typeof s?s:s.source:null;var s
})).join("")}return s=>{const n={},t={begin:/\$\{/,end:/\}/,contains:["self",{
begin:/:-/,contains:[n]}]};Object.assign(n,{className:"variable",variants:[{
begin:e(/\$[\w\d#@][\w\d_]*/,"(?![\\w\\d])(?![$])")},t]});const a={
className:"subst",begin:/\$\(/,end:/\)/,contains:[s.BACKSLASH_ESCAPE]},i={
begin:/<<-?\s*(?=\w+)/,starts:{contains:[s.END_SAME_AS_BEGIN({begin:/(\w+)/,
end:/(\w+)/,className:"string"})]}},c={className:"string",begin:/"/,end:/"/,
contains:[s.BACKSLASH_ESCAPE,n,a]};a.contains.push(c);const o={begin:/\$\(\(/,
end:/\)\)/,contains:[{begin:/\d+#[0-9a-f]+/,className:"number"},s.NUMBER_MODE,n]
},r=s.SHEBANG({binary:"(fish|bash|zsh|sh|csh|ksh|tcsh|dash|scsh)",relevance:10
}),l={className:"function",begin:/\w[\w\d_]*\s*\(\s*\)\s*\{/,returnBegin:!0,
contains:[s.inherit(s.TITLE_MODE,{begin:/\w[\w\d_]*/})],relevance:0};return{
name:"Bash",aliases:["sh","zsh"],keywords:{$pattern:/\b[a-z._-]+\b/,
keyword:"if then else elif fi for while in do done case esac function",
literal:"true false",
built_in:"break cd continue eval exec exit export getopts hash pwd readonly return shift test times trap umask unset alias bind builtin caller command declare echo enable help let local logout mapfile printf read readarray source type typeset ulimit unalias set shopt autoload bg bindkey bye cap chdir clone comparguments compcall compctl compdescribe compfiles compgroups compquote comptags comptry compvalues dirs disable disown echotc echoti emulate fc fg float functions getcap getln history integer jobs kill limit log noglob popd print pushd pushln rehash sched setcap setopt stat suspend ttyctl unfunction unhash unlimit unsetopt vared wait whence where which zcompile zformat zftp zle zmodload zparseopts zprof zpty zregexparse zsocket zstyle ztcp"
},contains:[r,s.SHEBANG(),l,o,s.HASH_COMMENT_MODE,i,c,{className:"",begin:/\\"/
},{className:"string",begin:/'/,end:/'/},n]}}})());hljs.registerLanguage("shell",(()=>{"use strict";return s=>({
name:"Shell Session",aliases:["console"],contains:[{className:"meta",
begin:/^\s{0,3}[/~\w\d[\]()@-]*[>%$#]/,starts:{end:/[^\\](?=\s*$)/,
subLanguage:"bash"}}]})})());hljs.registerLanguage("c",(()=>{"use strict";function e(e){
return((...e)=>e.map((e=>(e=>e?"string"==typeof e?e:e.source:null)(e))).join(""))("(",e,")?")
}return t=>{const n=(t=>{const n=t.COMMENT("//","$",{contains:[{begin:/\\\n/}]
}),r="[a-zA-Z_]\\w*::",a="(decltype\\(auto\\)|"+e(r)+"[a-zA-Z_]\\w*"+e("<[^<>]+>")+")",s={
className:"keyword",begin:"\\b[a-z\\d_]*_t\\b"},i={className:"string",
variants:[{begin:'(u8?|U|L)?"',end:'"',illegal:"\\n",
contains:[t.BACKSLASH_ESCAPE]},{
begin:"(u8?|U|L)?'(\\\\(x[0-9A-Fa-f]{2}|u[0-9A-Fa-f]{4,8}|[0-7]{3}|\\S)|.)",
end:"'",illegal:"."},t.END_SAME_AS_BEGIN({
begin:/(?:u8?|U|L)?R"([^()\\ ]{0,16})\(/,end:/\)([^()\\ ]{0,16})"/})]},o={
className:"number",variants:[{begin:"\\b(0b[01']+)"},{
begin:"(-?)\\b([\\d']+(\\.[\\d']*)?|\\.[\\d']+)((ll|LL|l|L)(u|U)?|(u|U)(ll|LL|l|L)?|f|F|b|B)"
},{
begin:"(-?)(\\b0[xX][a-fA-F0-9']+|(\\b[\\d']+(\\.[\\d']*)?|\\.[\\d']+)([eE][-+]?[\\d']+)?)"
}],relevance:0},c={className:"meta",begin:/#\s*[a-z]+\b/,end:/$/,keywords:{
"meta-keyword":"if else elif endif define undef warning error line pragma _Pragma ifdef ifndef include"
},contains:[{begin:/\\\n/,relevance:0},t.inherit(i,{className:"meta-string"}),{
className:"meta-string",begin:/<.*?>/,end:/$/,illegal:"\\n"
},n,t.C_BLOCK_COMMENT_MODE]},l={className:"title",begin:e(r)+t.IDENT_RE,
relevance:0},d=e(r)+t.IDENT_RE+"\\s*\\(",u={
keyword:"int float while private char char8_t char16_t char32_t catch import module export virtual operator sizeof dynamic_cast|10 typedef const_cast|10 const for static_cast|10 union namespace unsigned long volatile static protected bool template mutable if public friend do goto auto void enum else break extern using asm case typeid wchar_t short reinterpret_cast|10 default double register explicit signed typename try this switch continue inline delete alignas alignof constexpr consteval constinit decltype concept co_await co_return co_yield requires noexcept static_assert thread_local restrict final override atomic_bool atomic_char atomic_schar atomic_uchar atomic_short atomic_ushort atomic_int atomic_uint atomic_long atomic_ulong atomic_llong atomic_ullong new throw return and and_eq bitand bitor compl not not_eq or or_eq xor xor_eq",
built_in:"std string wstring cin cout cerr clog stdin stdout stderr stringstream istringstream ostringstream auto_ptr deque list queue stack vector map set pair bitset multiset multimap unordered_set unordered_map unordered_multiset unordered_multimap priority_queue make_pair array shared_ptr abort terminate abs acos asin atan2 atan calloc ceil cosh cos exit exp fabs floor fmod fprintf fputs free frexp fscanf future isalnum isalpha iscntrl isdigit isgraph islower isprint ispunct isspace isupper isxdigit tolower toupper labs ldexp log10 log malloc realloc memchr memcmp memcpy memset modf pow printf putchar puts scanf sinh sin snprintf sprintf sqrt sscanf strcat strchr strcmp strcpy strcspn strlen strncat strncmp strncpy strpbrk strrchr strspn strstr tanh tan vfprintf vprintf vsprintf endl initializer_list unique_ptr _Bool complex _Complex imaginary _Imaginary",
literal:"true false nullptr NULL"},m=[c,s,n,t.C_BLOCK_COMMENT_MODE,o,i],p={
variants:[{begin:/=/,end:/;/},{begin:/\(/,end:/\)/},{
beginKeywords:"new throw return else",end:/;/}],keywords:u,contains:m.concat([{
begin:/\(/,end:/\)/,keywords:u,contains:m.concat(["self"]),relevance:0}]),
relevance:0},_={className:"function",begin:"("+a+"[\\*&\\s]+)+"+d,
returnBegin:!0,end:/[{;=]/,excludeEnd:!0,keywords:u,illegal:/[^\w\s\*&:<>.]/,
contains:[{begin:"decltype\\(auto\\)",keywords:u,relevance:0},{begin:d,
returnBegin:!0,contains:[l],relevance:0},{className:"params",begin:/\(/,
end:/\)/,keywords:u,relevance:0,contains:[n,t.C_BLOCK_COMMENT_MODE,i,o,s,{
begin:/\(/,end:/\)/,keywords:u,relevance:0,
contains:["self",n,t.C_BLOCK_COMMENT_MODE,i,o,s]}]
},s,n,t.C_BLOCK_COMMENT_MODE,c]};return{
aliases:["c","cc","h","c++","h++","hpp","hh","hxx","cxx"],keywords:u,
disableAutodetect:!0,illegal:"</",contains:[].concat(p,_,m,[c,{
begin:"\\b(deque|list|queue|priority_queue|pair|stack|vector|map|set|bitset|multiset|multimap|unordered_map|unordered_set|unordered_multiset|unordered_multimap|array)\\s*<",
end:">",keywords:u,contains:["self",s]},{begin:t.IDENT_RE+"::",keywords:u},{
className:"class",beginKeywords:"enum class struct union",end:/[{;:<>=]/,
contains:[{beginKeywords:"final class struct"},t.TITLE_MODE]}]),exports:{
preprocessor:c,strings:i,keywords:u}}})(t)
;return n.name="C",n.aliases=["c","h"],n}})());hljs.registerLanguage("julia",(()=>{"use strict";return e=>{
var r="[A-Za-z_\\u00A1-\\uFFFF][A-Za-z_0-9\\u00A1-\\uFFFF]*",t={$pattern:r,
keyword:"baremodule begin break catch ccall const continue do else elseif end export false finally for function global if import in isa let local macro module quote return true try using where while",
literal:"ARGS C_NULL DEPOT_PATH ENDIAN_BOM ENV Inf Inf16 Inf32 Inf64 InsertionSort LOAD_PATH MergeSort NaN NaN16 NaN32 NaN64 PROGRAM_FILE QuickSort RoundDown RoundFromZero RoundNearest RoundNearestTiesAway RoundNearestTiesUp RoundToZero RoundUp VERSION|0 devnull false im missing nothing pi stderr stdin stdout true undef \u03c0 \u212f",
built_in:"AbstractArray AbstractChannel AbstractChar AbstractDict AbstractDisplay AbstractFloat AbstractIrrational AbstractMatrix AbstractRange AbstractSet AbstractString AbstractUnitRange AbstractVecOrMat AbstractVector Any ArgumentError Array AssertionError BigFloat BigInt BitArray BitMatrix BitSet BitVector Bool BoundsError CapturedException CartesianIndex CartesianIndices Cchar Cdouble Cfloat Channel Char Cint Cintmax_t Clong Clonglong Cmd Colon Complex ComplexF16 ComplexF32 ComplexF64 CompositeException Condition Cptrdiff_t Cshort Csize_t Cssize_t Cstring Cuchar Cuint Cuintmax_t Culong Culonglong Cushort Cvoid Cwchar_t Cwstring DataType DenseArray DenseMatrix DenseVecOrMat DenseVector Dict DimensionMismatch Dims DivideError DomainError EOFError Enum ErrorException Exception ExponentialBackOff Expr Float16 Float32 Float64 Function GlobalRef HTML IO IOBuffer IOContext IOStream IdDict IndexCartesian IndexLinear IndexStyle InexactError InitError Int Int128 Int16 Int32 Int64 Int8 Integer InterruptException InvalidStateException Irrational KeyError LinRange LineNumberNode LinearIndices LoadError MIME Matrix Method MethodError Missing MissingException Module NTuple NamedTuple Nothing Number OrdinalRange OutOfMemoryError OverflowError Pair PartialQuickSort PermutedDimsArray Pipe ProcessFailedException Ptr QuoteNode Rational RawFD ReadOnlyMemoryError Real ReentrantLock Ref Regex RegexMatch RoundingMode SegmentationFault Set Signed Some StackOverflowError StepRange StepRangeLen StridedArray StridedMatrix StridedVecOrMat StridedVector String StringIndexError SubArray SubString SubstitutionString Symbol SystemError Task TaskFailedException Text TextDisplay Timer Tuple Type TypeError TypeVar UInt UInt128 UInt16 UInt32 UInt64 UInt8 UndefInitializer UndefKeywordError UndefRefError UndefVarError Union UnionAll UnitRange Unsigned Val Vararg VecElement VecOrMat Vector VersionNumber WeakKeyDict WeakRef"
},n={keywords:t,illegal:/<\//},a={className:"subst",begin:/\$\(/,end:/\)/,
keywords:t},i={className:"variable",begin:"\\$"+r},o={className:"string",
contains:[e.BACKSLASH_ESCAPE,a,i],variants:[{begin:/\w*"""/,end:/"""\w*/,
relevance:10},{begin:/\w*"/,end:/"\w*/}]},s={className:"string",
contains:[e.BACKSLASH_ESCAPE,a,i],begin:"`",end:"`"},l={className:"meta",
begin:"@"+r};return n.name="Julia",n.contains=[{className:"number",
begin:/(\b0x[\d_]*(\.[\d_]*)?|0x\.\d[\d_]*)p[-+]?\d+|\b0[box][a-fA-F0-9][a-fA-F0-9_]*|(\b\d[\d_]*(\.[\d_]*)?|\.\d[\d_]*)([eEfF][-+]?\d+)?/,
relevance:0},{className:"string",begin:/'(.|\\[xXuU][a-zA-Z0-9]+)'/},o,s,l,{
className:"comment",variants:[{begin:"#=",end:"=#",relevance:10},{begin:"#",
end:"$"}]},e.HASH_COMMENT_MODE,{className:"keyword",
begin:"\\b(((abstract|primitive)\\s+)type|(mutable\\s+)?struct)\\b"},{begin:/<:/
}],a.contains=n.contains,n}})());hljs.registerLanguage("julia-repl",(()=>{"use strict";return a=>({
name:"Julia REPL",contains:[{className:"meta",begin:/^julia>/,relevance:10,
starts:{end:/^(?![ ]{6})/,subLanguage:"julia"},aliases:["jldoctest"]}]})})());hljs.registerLanguage("kotlin",(()=>{"use strict"
;var e="\\.([0-9](_*[0-9])*)",n="[0-9a-fA-F](_*[0-9a-fA-F])*",a={
className:"number",variants:[{
begin:`(\\b([0-9](_*[0-9])*)((${e})|\\.)?|(${e}))[eE][+-]?([0-9](_*[0-9])*)[fFdD]?\\b`
},{begin:`\\b([0-9](_*[0-9])*)((${e})[fFdD]?\\b|\\.([fFdD]\\b)?)`},{
begin:`(${e})[fFdD]?\\b`},{begin:"\\b([0-9](_*[0-9])*)[fFdD]\\b"},{
begin:`\\b0[xX]((${n})\\.?|(${n})?\\.(${n}))[pP][+-]?([0-9](_*[0-9])*)[fFdD]?\\b`
},{begin:"\\b(0|[1-9](_*[0-9])*)[lL]?\\b"},{begin:`\\b0[xX](${n})[lL]?\\b`},{
begin:"\\b0(_*[0-7])*[lL]?\\b"},{begin:"\\b0[bB][01](_*[01])*[lL]?\\b"}],
relevance:0};return e=>{const n={
keyword:"abstract as val var vararg get set class object open private protected public noinline crossinline dynamic final enum if else do while for when throw try catch finally import package is in fun override companion reified inline lateinit init interface annotation data sealed internal infix operator out by constructor super tailrec where const inner suspend typealias external expect actual",
built_in:"Byte Short Char Int Long Boolean Float Double Void Unit Nothing",
literal:"true false null"},i={className:"symbol",begin:e.UNDERSCORE_IDENT_RE+"@"
},s={className:"subst",begin:/\$\{/,end:/\}/,contains:[e.C_NUMBER_MODE]},t={
className:"variable",begin:"\\$"+e.UNDERSCORE_IDENT_RE},r={className:"string",
variants:[{begin:'"""',end:'"""(?=[^"])',contains:[t,s]},{begin:"'",end:"'",
illegal:/\n/,contains:[e.BACKSLASH_ESCAPE]},{begin:'"',end:'"',illegal:/\n/,
contains:[e.BACKSLASH_ESCAPE,t,s]}]};s.contains.push(r);const l={
className:"meta",
begin:"@(?:file|property|field|get|set|receiver|param|setparam|delegate)\\s*:(?:\\s*"+e.UNDERSCORE_IDENT_RE+")?"
},c={className:"meta",begin:"@"+e.UNDERSCORE_IDENT_RE,contains:[{begin:/\(/,
end:/\)/,contains:[e.inherit(r,{className:"meta-string"})]}]
},o=a,b=e.COMMENT("/\\*","\\*/",{contains:[e.C_BLOCK_COMMENT_MODE]}),E={
variants:[{className:"type",begin:e.UNDERSCORE_IDENT_RE},{begin:/\(/,end:/\)/,
contains:[]}]},d=E;return d.variants[1].contains=[E],E.variants[1].contains=[d],
{name:"Kotlin",aliases:["kt"],keywords:n,contains:[e.COMMENT("/\\*\\*","\\*/",{
relevance:0,contains:[{className:"doctag",begin:"@[A-Za-z]+"}]
}),e.C_LINE_COMMENT_MODE,b,{className:"keyword",
begin:/\b(break|continue|return|this)\b/,starts:{contains:[{className:"symbol",
begin:/@\w+/}]}},i,l,c,{className:"function",beginKeywords:"fun",end:"[(]|$",
returnBegin:!0,excludeEnd:!0,keywords:n,relevance:5,contains:[{
begin:e.UNDERSCORE_IDENT_RE+"\\s*\\(",returnBegin:!0,relevance:0,
contains:[e.UNDERSCORE_TITLE_MODE]},{className:"type",begin:/</,end:/>/,
keywords:"reified",relevance:0},{className:"params",begin:/\(/,end:/\)/,
endsParent:!0,keywords:n,relevance:0,contains:[{begin:/:/,end:/[=,\/]/,
endsWithParent:!0,contains:[E,e.C_LINE_COMMENT_MODE,b],relevance:0
},e.C_LINE_COMMENT_MODE,b,l,c,r,e.C_NUMBER_MODE]},b]},{className:"class",
beginKeywords:"class interface trait",end:/[:\{(]|$/,excludeEnd:!0,
illegal:"extends implements",contains:[{
beginKeywords:"public protected internal private constructor"
},e.UNDERSCORE_TITLE_MODE,{className:"type",begin:/</,end:/>/,excludeBegin:!0,
excludeEnd:!0,relevance:0},{className:"type",begin:/[,:]\s*/,end:/[<\(,]|$/,
excludeBegin:!0,returnEnd:!0},l,c]},r,{className:"meta",begin:"^#!/usr/bin/env",
end:"$",illegal:"\n"},o]}}})());hljs.registerLanguage("json",(()=>{"use strict";return n=>{const e={
literal:"true false null"
},i=[n.C_LINE_COMMENT_MODE,n.C_BLOCK_COMMENT_MODE],a=[n.QUOTE_STRING_MODE,n.C_NUMBER_MODE],l={
end:",",endsWithParent:!0,excludeEnd:!0,contains:a,keywords:e},t={begin:/\{/,
end:/\}/,contains:[{className:"attr",begin:/"/,end:/"/,
contains:[n.BACKSLASH_ESCAPE],illegal:"\\n"},n.inherit(l,{begin:/:/
})].concat(i),illegal:"\\S"},s={begin:"\\[",end:"\\]",contains:[n.inherit(l)],
illegal:"\\S"};return a.push(t,s),i.forEach((n=>{a.push(n)})),{name:"JSON",
contains:a,keywords:e,illegal:"\\S"}}})());hljs.registerLanguage("lua",(()=>{"use strict";return e=>{
const t="\\[=*\\[",a="\\]=*\\]",n={begin:t,end:a,contains:["self"]
},o=[e.COMMENT("--(?!\\[=*\\[)","$"),e.COMMENT("--\\[=*\\[",a,{contains:[n],
relevance:10})];return{name:"Lua",keywords:{$pattern:e.UNDERSCORE_IDENT_RE,
literal:"true false nil",
keyword:"and break do else elseif end for goto if in local not or repeat return then until while",
built_in:"_G _ENV _VERSION __index __newindex __mode __call __metatable __tostring __len __gc __add __sub __mul __div __mod __pow __concat __unm __eq __lt __le assert collectgarbage dofile error getfenv getmetatable ipairs load loadfile loadstring module next pairs pcall print rawequal rawget rawset require select setfenv setmetatable tonumber tostring type unpack xpcall arg self coroutine resume yield status wrap create running debug getupvalue debug sethook getmetatable gethook setmetatable setlocal traceback setfenv getinfo setupvalue getlocal getregistry getfenv io lines write close flush open output type read stderr stdin input stdout popen tmpfile math log max acos huge ldexp pi cos tanh pow deg tan cosh sinh random randomseed frexp ceil floor rad abs sqrt modf asin min mod fmod log10 atan2 exp sin atan os exit setlocale date getenv difftime remove time clock tmpname rename execute package preload loadlib loaded loaders cpath config path seeall string sub upper len gfind rep find match char dump gmatch reverse byte format gsub lower table setn insert getn foreachi maxn foreach concat sort remove"
},contains:o.concat([{className:"function",beginKeywords:"function",end:"\\)",
contains:[e.inherit(e.TITLE_MODE,{
begin:"([_a-zA-Z]\\w*\\.)*([_a-zA-Z]\\w*:)?[_a-zA-Z]\\w*"}),{className:"params",
begin:"\\(",endsWithParent:!0,contains:o}].concat(o)
},e.C_NUMBER_MODE,e.APOS_STRING_MODE,e.QUOTE_STRING_MODE,{className:"string",
begin:t,end:a,contains:[n],relevance:5}])}}})());hljs.registerLanguage("haskell",(()=>{"use strict";return e=>{const n={
variants:[e.COMMENT("--","$"),e.COMMENT(/\{-/,/-\}/,{contains:["self"]})]},i={
className:"meta",begin:/\{-#/,end:/#-\}/},a={className:"meta",begin:"^#",end:"$"
},s={className:"type",begin:"\\b[A-Z][\\w']*",relevance:0},l={begin:"\\(",
end:"\\)",illegal:'"',contains:[i,a,{className:"type",
begin:"\\b[A-Z][\\w]*(\\((\\.\\.|,|\\w+)\\))?"},e.inherit(e.TITLE_MODE,{
begin:"[_a-z][\\w']*"}),n]};return{name:"Haskell",aliases:["hs"],
keywords:"let in if then else case of where do module import hiding qualified type data newtype deriving class instance as default infix infixl infixr foreign export ccall stdcall cplusplus jvm dotnet safe unsafe family forall mdo proc rec",
contains:[{beginKeywords:"module",end:"where",keywords:"module where",
contains:[l,n],illegal:"\\W\\.|;"},{begin:"\\bimport\\b",end:"$",
keywords:"import qualified as hiding",contains:[l,n],illegal:"\\W\\.|;"},{
className:"class",begin:"^(\\s*)?(class|instance)\\b",end:"where",
keywords:"class family instance where",contains:[s,l,n]},{className:"class",
begin:"\\b(data|(new)?type)\\b",end:"$",
keywords:"data family type newtype deriving",contains:[i,s,l,{begin:/\{/,
end:/\}/,contains:l.contains},n]},{beginKeywords:"default",end:"$",
contains:[s,l,n]},{beginKeywords:"infix infixl infixr",end:"$",
contains:[e.C_NUMBER_MODE,n]},{begin:"\\bforeign\\b",end:"$",
keywords:"foreign import export ccall stdcall cplusplus jvm dotnet safe unsafe",
contains:[s,e.QUOTE_STRING_MODE,n]},{className:"meta",
begin:"#!\\/usr\\/bin\\/env runhaskell",end:"$"
},i,a,e.QUOTE_STRING_MODE,e.C_NUMBER_MODE,s,e.inherit(e.TITLE_MODE,{
begin:"^[_a-z][\\w']*"}),n,{begin:"->|<-"}]}}})());hljs.registerLanguage("csharp",(()=>{"use strict";return e=>{var n={
keyword:["abstract","as","base","break","case","class","const","continue","do","else","event","explicit","extern","finally","fixed","for","foreach","goto","if","implicit","in","interface","internal","is","lock","namespace","new","operator","out","override","params","private","protected","public","readonly","record","ref","return","sealed","sizeof","stackalloc","static","struct","switch","this","throw","try","typeof","unchecked","unsafe","using","virtual","void","volatile","while"].concat(["add","alias","and","ascending","async","await","by","descending","equals","from","get","global","group","init","into","join","let","nameof","not","notnull","on","or","orderby","partial","remove","select","set","unmanaged","value|0","var","when","where","with","yield"]).join(" "),
built_in:"bool byte char decimal delegate double dynamic enum float int long nint nuint object sbyte short string ulong unit ushort",
literal:"default false null true"},a=e.inherit(e.TITLE_MODE,{
begin:"[a-zA-Z](\\.?\\w)*"}),i={className:"number",variants:[{
begin:"\\b(0b[01']+)"},{
begin:"(-?)\\b([\\d']+(\\.[\\d']*)?|\\.[\\d']+)(u|U|l|L|ul|UL|f|F|b|B)"},{
begin:"(-?)(\\b0[xX][a-fA-F0-9']+|(\\b[\\d']+(\\.[\\d']*)?|\\.[\\d']+)([eE][-+]?[\\d']+)?)"
}],relevance:0},s={className:"string",begin:'@"',end:'"',contains:[{begin:'""'}]
},t=e.inherit(s,{illegal:/\n/}),r={className:"subst",begin:/\{/,end:/\}/,
keywords:n},l=e.inherit(r,{illegal:/\n/}),c={className:"string",begin:/\$"/,
end:'"',illegal:/\n/,contains:[{begin:/\{\{/},{begin:/\}\}/
},e.BACKSLASH_ESCAPE,l]},o={className:"string",begin:/\$@"/,end:'"',contains:[{
begin:/\{\{/},{begin:/\}\}/},{begin:'""'},r]},d=e.inherit(o,{illegal:/\n/,
contains:[{begin:/\{\{/},{begin:/\}\}/},{begin:'""'},l]})
;r.contains=[o,c,s,e.APOS_STRING_MODE,e.QUOTE_STRING_MODE,i,e.C_BLOCK_COMMENT_MODE],
l.contains=[d,c,t,e.APOS_STRING_MODE,e.QUOTE_STRING_MODE,i,e.inherit(e.C_BLOCK_COMMENT_MODE,{
illegal:/\n/})];var g={variants:[o,c,s,e.APOS_STRING_MODE,e.QUOTE_STRING_MODE]
},E={begin:"<",end:">",contains:[{beginKeywords:"in out"},a]
},_=e.IDENT_RE+"(<"+e.IDENT_RE+"(\\s*,\\s*"+e.IDENT_RE+")*>)?(\\[\\])?",b={
begin:"@"+e.IDENT_RE,relevance:0};return{name:"C#",aliases:["cs","c#"],
keywords:n,illegal:/::/,contains:[e.COMMENT("///","$",{returnBegin:!0,
contains:[{className:"doctag",variants:[{begin:"///",relevance:0},{
begin:"\x3c!--|--\x3e"},{begin:"</?",end:">"}]}]
}),e.C_LINE_COMMENT_MODE,e.C_BLOCK_COMMENT_MODE,{className:"meta",begin:"#",
end:"$",keywords:{
"meta-keyword":"if else elif endif define undef warning error line region endregion pragma checksum"
}},g,i,{beginKeywords:"class interface",relevance:0,end:/[{;=]/,
illegal:/[^\s:,]/,contains:[{beginKeywords:"where class"
},a,E,e.C_LINE_COMMENT_MODE,e.C_BLOCK_COMMENT_MODE]},{beginKeywords:"namespace",
relevance:0,end:/[{;=]/,illegal:/[^\s:]/,
contains:[a,e.C_LINE_COMMENT_MODE,e.C_BLOCK_COMMENT_MODE]},{
beginKeywords:"record",relevance:0,end:/[{;=]/,illegal:/[^\s:]/,
contains:[a,E,e.C_LINE_COMMENT_MODE,e.C_BLOCK_COMMENT_MODE]},{className:"meta",
begin:"^\\s*\\[",excludeBegin:!0,end:"\\]",excludeEnd:!0,contains:[{
className:"meta-string",begin:/"/,end:/"/}]},{
beginKeywords:"new return throw await else",relevance:0},{className:"function",
begin:"("+_+"\\s+)+"+e.IDENT_RE+"\\s*(<.+>\\s*)?\\(",returnBegin:!0,
end:/\s*[{;=]/,excludeEnd:!0,keywords:n,contains:[{
beginKeywords:"public private protected static internal protected abstract async extern override unsafe virtual new sealed partial",
relevance:0},{begin:e.IDENT_RE+"\\s*(<.+>\\s*)?\\(",returnBegin:!0,
contains:[e.TITLE_MODE,E],relevance:0},{className:"params",begin:/\(/,end:/\)/,
excludeBegin:!0,excludeEnd:!0,keywords:n,relevance:0,
contains:[g,i,e.C_BLOCK_COMMENT_MODE]
},e.C_LINE_COMMENT_MODE,e.C_BLOCK_COMMENT_MODE]},b]}}})());hljs.registerLanguage("plaintext",(()=>{"use strict";return t=>({
name:"Plain text",aliases:["text","txt"],disableAutodetect:!0})})());hljs.registerLanguage("xml",(()=>{"use strict";function e(e){
return e?"string"==typeof e?e:e.source:null}function n(e){return a("(?=",e,")")}
function a(...n){return n.map((n=>e(n))).join("")}function s(...n){
return"("+n.map((n=>e(n))).join("|")+")"}return e=>{
const t=a(/[A-Z_]/,a("(",/[A-Z0-9_.-]+:/,")?"),/[A-Z0-9_.-]*/),i={
className:"symbol",begin:/&[a-z]+;|&#[0-9]+;|&#x[a-f0-9]+;/},r={begin:/\s/,
contains:[{className:"meta-keyword",begin:/#?[a-z_][a-z1-9_-]+/,illegal:/\n/}]
},c=e.inherit(r,{begin:/\(/,end:/\)/}),l=e.inherit(e.APOS_STRING_MODE,{
className:"meta-string"}),g=e.inherit(e.QUOTE_STRING_MODE,{
className:"meta-string"}),m={endsWithParent:!0,illegal:/</,relevance:0,
contains:[{className:"attr",begin:/[A-Za-z0-9._:-]+/,relevance:0},{begin:/=\s*/,
relevance:0,contains:[{className:"string",endsParent:!0,variants:[{begin:/"/,
end:/"/,contains:[i]},{begin:/'/,end:/'/,contains:[i]},{begin:/[^\s"'=<>`]+/}]}]
}]};return{name:"HTML, XML",
aliases:["html","xhtml","rss","atom","xjb","xsd","xsl","plist","wsf","svg"],
case_insensitive:!0,contains:[{className:"meta",begin:/<![a-z]/,end:/>/,
relevance:10,contains:[r,g,l,c,{begin:/\[/,end:/\]/,contains:[{className:"meta",
begin:/<![a-z]/,end:/>/,contains:[r,c,g,l]}]}]},e.COMMENT(/<!--/,/-->/,{
relevance:10}),{begin:/<!\[CDATA\[/,end:/\]\]>/,relevance:10},i,{
className:"meta",begin:/<\?xml/,end:/\?>/,relevance:10},{className:"tag",
begin:/<style(?=\s|>)/,end:/>/,keywords:{name:"style"},contains:[m],starts:{
end:/<\/style>/,returnEnd:!0,subLanguage:["css","xml"]}},{className:"tag",
begin:/<script(?=\s|>)/,end:/>/,keywords:{name:"script"},contains:[m],starts:{
end:/<\/script>/,returnEnd:!0,subLanguage:["javascript","handlebars","xml"]}},{
className:"tag",begin:/<>|<\/>/},{className:"tag",
begin:a(/</,n(a(t,s(/\/>/,/>/,/\s/)))),end:/\/?>/,contains:[{className:"name",
begin:t,relevance:0,starts:m}]},{className:"tag",begin:a(/<\//,n(a(t,/>/))),
contains:[{className:"name",begin:t,relevance:0},{begin:/>/,relevance:0}]}]}}
})());hljs.registerLanguage("markdown",(()=>{"use strict";function n(...n){
return n.map((n=>{return(e=n)?"string"==typeof e?e:e.source:null;var e
})).join("")}return e=>{const a={begin:/<\/?[A-Za-z_]/,end:">",
subLanguage:"xml",relevance:0},i={variants:[{begin:/\[.+?\]\[.*?\]/,relevance:0
},{begin:/\[.+?\]\(((data|javascript|mailto):|(?:http|ftp)s?:\/\/).*?\)/,
relevance:2},{begin:n(/\[.+?\]\(/,/[A-Za-z][A-Za-z0-9+.-]*/,/:\/\/.*?\)/),
relevance:2},{begin:/\[.+?\]\([./?&#].*?\)/,relevance:1},{
begin:/\[.+?\]\(.*?\)/,relevance:0}],returnBegin:!0,contains:[{
className:"string",relevance:0,begin:"\\[",end:"\\]",excludeBegin:!0,
returnEnd:!0},{className:"link",relevance:0,begin:"\\]\\(",end:"\\)",
excludeBegin:!0,excludeEnd:!0},{className:"symbol",relevance:0,begin:"\\]\\[",
end:"\\]",excludeBegin:!0,excludeEnd:!0}]},s={className:"strong",contains:[],
variants:[{begin:/_{2}/,end:/_{2}/},{begin:/\*{2}/,end:/\*{2}/}]},c={
className:"emphasis",contains:[],variants:[{begin:/\*(?!\*)/,end:/\*/},{
begin:/_(?!_)/,end:/_/,relevance:0}]};s.contains.push(c),c.contains.push(s)
;let t=[a,i]
;return s.contains=s.contains.concat(t),c.contains=c.contains.concat(t),
t=t.concat(s,c),{name:"Markdown",aliases:["md","mkdown","mkd"],contains:[{
className:"section",variants:[{begin:"^#{1,6}",end:"$",contains:t},{
begin:"(?=^.+?\\n[=-]{2,}$)",contains:[{begin:"^[=-]*$"},{begin:"^",end:"\\n",
contains:t}]}]},a,{className:"bullet",begin:"^[ \t]*([*+-]|(\\d+\\.))(?=\\s+)",
end:"\\s+",excludeEnd:!0},s,c,{className:"quote",begin:"^>\\s+",contains:t,
end:"$"},{className:"code",variants:[{begin:"(`{3,})[^`](.|\\n)*?\\1`*[ ]*"},{
begin:"(~{3,})[^~](.|\\n)*?\\1~*[ ]*"},{begin:"```",end:"```+[ ]*$"},{
begin:"~~~",end:"~~~+[ ]*$"},{begin:"`.+?`"},{begin:"(?=^( {4}|\\t))",
contains:[{begin:"^( {4}|\\t)",end:"(\\n)$"}],relevance:0}]},{
begin:"^[-\\*]{3,}",end:"$"},i,{begin:/^\[[^\n]+\]:/,returnBegin:!0,contains:[{
className:"symbol",begin:/\[/,end:/\]/,excludeBegin:!0,excludeEnd:!0},{
className:"link",begin:/:\s*/,end:/$/,excludeBegin:!0}]}]}}})());hljs.registerLanguage("fortran",(()=>{"use strict";function e(...e){
return e.map((e=>{return(n=e)?"string"==typeof n?n:n.source:null;var n
})).join("")}return n=>{const a={variants:[n.COMMENT("!","$",{relevance:0
}),n.COMMENT("^C[ ]","$",{relevance:0}),n.COMMENT("^C$","$",{relevance:0})]
},t=/(_[a-z_\d]+)?/,i=/([de][+-]?\d+)?/,c={className:"number",variants:[{
begin:e(/\b\d+/,/\.(\d*)/,i,t)},{begin:e(/\b\d+/,i,t)},{begin:e(/\.\d+/,i,t)}],
relevance:0},o={className:"function",
beginKeywords:"subroutine function program",illegal:"[${=\\n]",
contains:[n.UNDERSCORE_TITLE_MODE,{className:"params",begin:"\\(",end:"\\)"}]}
;return{name:"Fortran",case_insensitive:!0,aliases:["f90","f95"],keywords:{
literal:".False. .True.",
keyword:"kind do concurrent local shared while private call intrinsic where elsewhere type endtype endmodule endselect endinterface end enddo endif if forall endforall only contains default return stop then block endblock endassociate public subroutine|10 function program .and. .or. .not. .le. .eq. .ge. .gt. .lt. goto save else use module select case access blank direct exist file fmt form formatted iostat name named nextrec number opened rec recl sequential status unformatted unit continue format pause cycle exit c_null_char c_alert c_backspace c_form_feed flush wait decimal round iomsg synchronous nopass non_overridable pass protected volatile abstract extends import non_intrinsic value deferred generic final enumerator class associate bind enum c_int c_short c_long c_long_long c_signed_char c_size_t c_int8_t c_int16_t c_int32_t c_int64_t c_int_least8_t c_int_least16_t c_int_least32_t c_int_least64_t c_int_fast8_t c_int_fast16_t c_int_fast32_t c_int_fast64_t c_intmax_t C_intptr_t c_float c_double c_long_double c_float_complex c_double_complex c_long_double_complex c_bool c_char c_null_ptr c_null_funptr c_new_line c_carriage_return c_horizontal_tab c_vertical_tab iso_c_binding c_loc c_funloc c_associated  c_f_pointer c_ptr c_funptr iso_fortran_env character_storage_size error_unit file_storage_size input_unit iostat_end iostat_eor numeric_storage_size output_unit c_f_procpointer ieee_arithmetic ieee_support_underflow_control ieee_get_underflow_mode ieee_set_underflow_mode newunit contiguous recursive pad position action delim readwrite eor advance nml interface procedure namelist include sequence elemental pure impure integer real character complex logical codimension dimension allocatable|10 parameter external implicit|10 none double precision assign intent optional pointer target in out common equivalence data",
built_in:"alog alog10 amax0 amax1 amin0 amin1 amod cabs ccos cexp clog csin csqrt dabs dacos dasin datan datan2 dcos dcosh ddim dexp dint dlog dlog10 dmax1 dmin1 dmod dnint dsign dsin dsinh dsqrt dtan dtanh float iabs idim idint idnint ifix isign max0 max1 min0 min1 sngl algama cdabs cdcos cdexp cdlog cdsin cdsqrt cqabs cqcos cqexp cqlog cqsin cqsqrt dcmplx dconjg derf derfc dfloat dgamma dimag dlgama iqint qabs qacos qasin qatan qatan2 qcmplx qconjg qcos qcosh qdim qerf qerfc qexp qgamma qimag qlgama qlog qlog10 qmax1 qmin1 qmod qnint qsign qsin qsinh qsqrt qtan qtanh abs acos aimag aint anint asin atan atan2 char cmplx conjg cos cosh exp ichar index int log log10 max min nint sign sin sinh sqrt tan tanh print write dim lge lgt lle llt mod nullify allocate deallocate adjustl adjustr all allocated any associated bit_size btest ceiling count cshift date_and_time digits dot_product eoshift epsilon exponent floor fraction huge iand ibclr ibits ibset ieor ior ishft ishftc lbound len_trim matmul maxexponent maxloc maxval merge minexponent minloc minval modulo mvbits nearest pack present product radix random_number random_seed range repeat reshape rrspacing scale scan selected_int_kind selected_real_kind set_exponent shape size spacing spread sum system_clock tiny transpose trim ubound unpack verify achar iachar transfer dble entry dprod cpu_time command_argument_count get_command get_command_argument get_environment_variable is_iostat_end ieee_arithmetic ieee_support_underflow_control ieee_get_underflow_mode ieee_set_underflow_mode is_iostat_eor move_alloc new_line selected_char_kind same_type_as extends_type_of acosh asinh atanh bessel_j0 bessel_j1 bessel_jn bessel_y0 bessel_y1 bessel_yn erf erfc erfc_scaled gamma log_gamma hypot norm2 atomic_define atomic_ref execute_command_line leadz trailz storage_size merge_bits bge bgt ble blt dshiftl dshiftr findloc iall iany iparity image_index lcobound ucobound maskl maskr num_images parity popcnt poppar shifta shiftl shiftr this_image sync change team co_broadcast co_max co_min co_sum co_reduce"
},illegal:/\/\*/,contains:[{className:"string",relevance:0,
variants:[n.APOS_STRING_MODE,n.QUOTE_STRING_MODE]},o,{begin:/^C\s*=(?!=)/,
relevance:0},a,c]}}})());hljs.registerLanguage("css",(()=>{"use strict";return e=>{
var n="[a-zA-Z-][a-zA-Z0-9_-]*",a={
begin:/([*]\s?)?(?:[A-Z_.\-\\]+|--[a-zA-Z0-9_-]+)\s*(\/\*\*\/)?:/,
returnBegin:!0,end:";",endsWithParent:!0,contains:[{className:"attribute",
begin:/\S/,end:":",excludeEnd:!0,starts:{endsWithParent:!0,excludeEnd:!0,
contains:[{begin:/[\w-]+\(/,returnBegin:!0,contains:[{className:"built_in",
begin:/[\w-]+/},{begin:/\(/,end:/\)/,
contains:[e.APOS_STRING_MODE,e.QUOTE_STRING_MODE,e.CSS_NUMBER_MODE]}]
},e.CSS_NUMBER_MODE,e.QUOTE_STRING_MODE,e.APOS_STRING_MODE,e.C_BLOCK_COMMENT_MODE,{
className:"number",begin:"#[0-9A-Fa-f]+"},{className:"meta",begin:"!important"}]
}}]};return{name:"CSS",case_insensitive:!0,illegal:/[=|'\$]/,
contains:[e.C_BLOCK_COMMENT_MODE,{className:"selector-id",
begin:/#[A-Za-z0-9_-]+/},{className:"selector-class",begin:"\\."+n},{
className:"selector-attr",begin:/\[/,end:/\]/,illegal:"$",
contains:[e.APOS_STRING_MODE,e.QUOTE_STRING_MODE]},{className:"selector-pseudo",
begin:/:(:)?[a-zA-Z0-9_+()"'.-]+/},{begin:"@(page|font-face)",
lexemes:"@[a-z-]+",keywords:"@page @font-face"},{begin:"@",end:"[{;]",
illegal:/:/,returnBegin:!0,contains:[{className:"keyword",
begin:/@-?\w[\w]*(-\w+)*/},{begin:/\s/,endsWithParent:!0,excludeEnd:!0,
relevance:0,keywords:"and or not only",contains:[{begin:/[a-z-]+:/,
className:"attribute"},e.APOS_STRING_MODE,e.QUOTE_STRING_MODE,e.CSS_NUMBER_MODE]
}]},{className:"selector-tag",begin:n,relevance:0},{begin:/\{/,end:/\}/,
illegal:/\S/,contains:[e.C_BLOCK_COMMENT_MODE,{begin:/;/},a]}]}}})());hljs.registerLanguage("scheme",(()=>{"use strict";return e=>{
const t="[^\\(\\)\\[\\]\\{\\}\",'`;#|\\\\\\s]+",n={$pattern:t,
"builtin-name":"case-lambda call/cc class define-class exit-handler field import inherit init-field interface let*-values let-values let/ec mixin opt-lambda override protect provide public rename require require-for-syntax syntax syntax-case syntax-error unit/sig unless when with-syntax and begin call-with-current-continuation call-with-input-file call-with-output-file case cond define define-syntax delay do dynamic-wind else for-each if lambda let let* let-syntax letrec letrec-syntax map or syntax-rules ' * + , ,@ - ... / ; < <= = => > >= ` abs acos angle append apply asin assoc assq assv atan boolean? caar cadr call-with-input-file call-with-output-file call-with-values car cdddar cddddr cdr ceiling char->integer char-alphabetic? char-ci<=? char-ci<? char-ci=? char-ci>=? char-ci>? char-downcase char-lower-case? char-numeric? char-ready? char-upcase char-upper-case? char-whitespace? char<=? char<? char=? char>=? char>? char? close-input-port close-output-port complex? cons cos current-input-port current-output-port denominator display eof-object? eq? equal? eqv? eval even? exact->inexact exact? exp expt floor force gcd imag-part inexact->exact inexact? input-port? integer->char integer? interaction-environment lcm length list list->string list->vector list-ref list-tail list? load log magnitude make-polar make-rectangular make-string make-vector max member memq memv min modulo negative? newline not null-environment null? number->string number? numerator odd? open-input-file open-output-file output-port? pair? peek-char port? positive? procedure? quasiquote quote quotient rational? rationalize read read-char real-part real? remainder reverse round scheme-report-environment set! set-car! set-cdr! sin sqrt string string->list string->number string->symbol string-append string-ci<=? string-ci<? string-ci=? string-ci>=? string-ci>? string-copy string-fill! string-length string-ref string-set! string<=? string<? string=? string>=? string>? string? substring symbol->string symbol? tan transcript-off transcript-on truncate values vector vector->list vector-fill! vector-length vector-ref vector-set! with-input-from-file with-output-to-file write write-char zero?"
},r={className:"literal",begin:"(#t|#f|#\\\\"+t+"|#\\\\.)"},a={
className:"number",variants:[{begin:"(-|\\+)?\\d+([./]\\d+)?",relevance:0},{
begin:"(-|\\+)?\\d+([./]\\d+)?[+\\-](-|\\+)?\\d+([./]\\d+)?i",relevance:0},{
begin:"#b[0-1]+(/[0-1]+)?"},{begin:"#o[0-7]+(/[0-7]+)?"},{
begin:"#x[0-9a-f]+(/[0-9a-f]+)?"}]},i=e.QUOTE_STRING_MODE,c=[e.COMMENT(";","$",{
relevance:0}),e.COMMENT("#\\|","\\|#")],s={begin:t,relevance:0},l={
className:"symbol",begin:"'"+t},o={endsWithParent:!0,relevance:0},g={variants:[{
begin:/'/},{begin:"`"}],contains:[{begin:"\\(",end:"\\)",
contains:["self",r,i,a,s,l]}]},u={className:"name",relevance:0,begin:t,
keywords:n},d={variants:[{begin:"\\(",end:"\\)"},{begin:"\\[",end:"\\]"}],
contains:[{begin:/lambda/,endsWithParent:!0,returnBegin:!0,contains:[u,{
endsParent:!0,variants:[{begin:/\(/,end:/\)/},{begin:/\[/,end:/\]/}],
contains:[s]}]},u,o]};return o.contains=[r,a,i,s,l,g,d].concat(c),{
name:"Scheme",illegal:/\S/,contains:[e.SHEBANG(),a,i,l,g,d].concat(c)}}})());hljs.registerLanguage("glsl",(()=>{"use strict";return e=>({name:"GLSL",
keywords:{
keyword:"break continue discard do else for if return while switch case default attribute binding buffer ccw centroid centroid varying coherent column_major const cw depth_any depth_greater depth_less depth_unchanged early_fragment_tests equal_spacing flat fractional_even_spacing fractional_odd_spacing highp in index inout invariant invocations isolines layout line_strip lines lines_adjacency local_size_x local_size_y local_size_z location lowp max_vertices mediump noperspective offset origin_upper_left out packed patch pixel_center_integer point_mode points precise precision quads r11f_g11f_b10f r16 r16_snorm r16f r16i r16ui r32f r32i r32ui r8 r8_snorm r8i r8ui readonly restrict rg16 rg16_snorm rg16f rg16i rg16ui rg32f rg32i rg32ui rg8 rg8_snorm rg8i rg8ui rgb10_a2 rgb10_a2ui rgba16 rgba16_snorm rgba16f rgba16i rgba16ui rgba32f rgba32i rgba32ui rgba8 rgba8_snorm rgba8i rgba8ui row_major sample shared smooth std140 std430 stream triangle_strip triangles triangles_adjacency uniform varying vertices volatile writeonly",
type:"atomic_uint bool bvec2 bvec3 bvec4 dmat2 dmat2x2 dmat2x3 dmat2x4 dmat3 dmat3x2 dmat3x3 dmat3x4 dmat4 dmat4x2 dmat4x3 dmat4x4 double dvec2 dvec3 dvec4 float iimage1D iimage1DArray iimage2D iimage2DArray iimage2DMS iimage2DMSArray iimage2DRect iimage3D iimageBuffer iimageCube iimageCubeArray image1D image1DArray image2D image2DArray image2DMS image2DMSArray image2DRect image3D imageBuffer imageCube imageCubeArray int isampler1D isampler1DArray isampler2D isampler2DArray isampler2DMS isampler2DMSArray isampler2DRect isampler3D isamplerBuffer isamplerCube isamplerCubeArray ivec2 ivec3 ivec4 mat2 mat2x2 mat2x3 mat2x4 mat3 mat3x2 mat3x3 mat3x4 mat4 mat4x2 mat4x3 mat4x4 sampler1D sampler1DArray sampler1DArrayShadow sampler1DShadow sampler2D sampler2DArray sampler2DArrayShadow sampler2DMS sampler2DMSArray sampler2DRect sampler2DRectShadow sampler2DShadow sampler3D samplerBuffer samplerCube samplerCubeArray samplerCubeArrayShadow samplerCubeShadow image1D uimage1DArray uimage2D uimage2DArray uimage2DMS uimage2DMSArray uimage2DRect uimage3D uimageBuffer uimageCube uimageCubeArray uint usampler1D usampler1DArray usampler2D usampler2DArray usampler2DMS usampler2DMSArray usampler2DRect usampler3D samplerBuffer usamplerCube usamplerCubeArray uvec2 uvec3 uvec4 vec2 vec3 vec4 void",
built_in:"gl_MaxAtomicCounterBindings gl_MaxAtomicCounterBufferSize gl_MaxClipDistances gl_MaxClipPlanes gl_MaxCombinedAtomicCounterBuffers gl_MaxCombinedAtomicCounters gl_MaxCombinedImageUniforms gl_MaxCombinedImageUnitsAndFragmentOutputs gl_MaxCombinedTextureImageUnits gl_MaxComputeAtomicCounterBuffers gl_MaxComputeAtomicCounters gl_MaxComputeImageUniforms gl_MaxComputeTextureImageUnits gl_MaxComputeUniformComponents gl_MaxComputeWorkGroupCount gl_MaxComputeWorkGroupSize gl_MaxDrawBuffers gl_MaxFragmentAtomicCounterBuffers gl_MaxFragmentAtomicCounters gl_MaxFragmentImageUniforms gl_MaxFragmentInputComponents gl_MaxFragmentInputVectors gl_MaxFragmentUniformComponents gl_MaxFragmentUniformVectors gl_MaxGeometryAtomicCounterBuffers gl_MaxGeometryAtomicCounters gl_MaxGeometryImageUniforms gl_MaxGeometryInputComponents gl_MaxGeometryOutputComponents gl_MaxGeometryOutputVertices gl_MaxGeometryTextureImageUnits gl_MaxGeometryTotalOutputComponents gl_MaxGeometryUniformComponents gl_MaxGeometryVaryingComponents gl_MaxImageSamples gl_MaxImageUnits gl_MaxLights gl_MaxPatchVertices gl_MaxProgramTexelOffset gl_MaxTessControlAtomicCounterBuffers gl_MaxTessControlAtomicCounters gl_MaxTessControlImageUniforms gl_MaxTessControlInputComponents gl_MaxTessControlOutputComponents gl_MaxTessControlTextureImageUnits gl_MaxTessControlTotalOutputComponents gl_MaxTessControlUniformComponents gl_MaxTessEvaluationAtomicCounterBuffers gl_MaxTessEvaluationAtomicCounters gl_MaxTessEvaluationImageUniforms gl_MaxTessEvaluationInputComponents gl_MaxTessEvaluationOutputComponents gl_MaxTessEvaluationTextureImageUnits gl_MaxTessEvaluationUniformComponents gl_MaxTessGenLevel gl_MaxTessPatchComponents gl_MaxTextureCoords gl_MaxTextureImageUnits gl_MaxTextureUnits gl_MaxVaryingComponents gl_MaxVaryingFloats gl_MaxVaryingVectors gl_MaxVertexAtomicCounterBuffers gl_MaxVertexAtomicCounters gl_MaxVertexAttribs gl_MaxVertexImageUniforms gl_MaxVertexOutputComponents gl_MaxVertexOutputVectors gl_MaxVertexTextureImageUnits gl_MaxVertexUniformComponents gl_MaxVertexUniformVectors gl_MaxViewports gl_MinProgramTexelOffset gl_BackColor gl_BackLightModelProduct gl_BackLightProduct gl_BackMaterial gl_BackSecondaryColor gl_ClipDistance gl_ClipPlane gl_ClipVertex gl_Color gl_DepthRange gl_EyePlaneQ gl_EyePlaneR gl_EyePlaneS gl_EyePlaneT gl_Fog gl_FogCoord gl_FogFragCoord gl_FragColor gl_FragCoord gl_FragData gl_FragDepth gl_FrontColor gl_FrontFacing gl_FrontLightModelProduct gl_FrontLightProduct gl_FrontMaterial gl_FrontSecondaryColor gl_GlobalInvocationID gl_InstanceID gl_InvocationID gl_Layer gl_LightModel gl_LightSource gl_LocalInvocationID gl_LocalInvocationIndex gl_ModelViewMatrix gl_ModelViewMatrixInverse gl_ModelViewMatrixInverseTranspose gl_ModelViewMatrixTranspose gl_ModelViewProjectionMatrix gl_ModelViewProjectionMatrixInverse gl_ModelViewProjectionMatrixInverseTranspose gl_ModelViewProjectionMatrixTranspose gl_MultiTexCoord0 gl_MultiTexCoord1 gl_MultiTexCoord2 gl_MultiTexCoord3 gl_MultiTexCoord4 gl_MultiTexCoord5 gl_MultiTexCoord6 gl_MultiTexCoord7 gl_Normal gl_NormalMatrix gl_NormalScale gl_NumSamples gl_NumWorkGroups gl_ObjectPlaneQ gl_ObjectPlaneR gl_ObjectPlaneS gl_ObjectPlaneT gl_PatchVerticesIn gl_Point gl_PointCoord gl_PointSize gl_Position gl_PrimitiveID gl_PrimitiveIDIn gl_ProjectionMatrix gl_ProjectionMatrixInverse gl_ProjectionMatrixInverseTranspose gl_ProjectionMatrixTranspose gl_SampleID gl_SampleMask gl_SampleMaskIn gl_SamplePosition gl_SecondaryColor gl_TessCoord gl_TessLevelInner gl_TessLevelOuter gl_TexCoord gl_TextureEnvColor gl_TextureMatrix gl_TextureMatrixInverse gl_TextureMatrixInverseTranspose gl_TextureMatrixTranspose gl_Vertex gl_VertexID gl_ViewportIndex gl_WorkGroupID gl_WorkGroupSize gl_in gl_out EmitStreamVertex EmitVertex EndPrimitive EndStreamPrimitive abs acos acosh all any asin asinh atan atanh atomicAdd atomicAnd atomicCompSwap atomicCounter atomicCounterDecrement atomicCounterIncrement atomicExchange atomicMax atomicMin atomicOr atomicXor barrier bitCount bitfieldExtract bitfieldInsert bitfieldReverse ceil clamp cos cosh cross dFdx dFdy degrees determinant distance dot equal exp exp2 faceforward findLSB findMSB floatBitsToInt floatBitsToUint floor fma fract frexp ftransform fwidth greaterThan greaterThanEqual groupMemoryBarrier imageAtomicAdd imageAtomicAnd imageAtomicCompSwap imageAtomicExchange imageAtomicMax imageAtomicMin imageAtomicOr imageAtomicXor imageLoad imageSize imageStore imulExtended intBitsToFloat interpolateAtCentroid interpolateAtOffset interpolateAtSample inverse inversesqrt isinf isnan ldexp length lessThan lessThanEqual log log2 matrixCompMult max memoryBarrier memoryBarrierAtomicCounter memoryBarrierBuffer memoryBarrierImage memoryBarrierShared min mix mod modf noise1 noise2 noise3 noise4 normalize not notEqual outerProduct packDouble2x32 packHalf2x16 packSnorm2x16 packSnorm4x8 packUnorm2x16 packUnorm4x8 pow radians reflect refract round roundEven shadow1D shadow1DLod shadow1DProj shadow1DProjLod shadow2D shadow2DLod shadow2DProj shadow2DProjLod sign sin sinh smoothstep sqrt step tan tanh texelFetch texelFetchOffset texture texture1D texture1DLod texture1DProj texture1DProjLod texture2D texture2DLod texture2DProj texture2DProjLod texture3D texture3DLod texture3DProj texture3DProjLod textureCube textureCubeLod textureGather textureGatherOffset textureGatherOffsets textureGrad textureGradOffset textureLod textureLodOffset textureOffset textureProj textureProjGrad textureProjGradOffset textureProjLod textureProjLodOffset textureProjOffset textureQueryLevels textureQueryLod textureSize transpose trunc uaddCarry uintBitsToFloat umulExtended unpackDouble2x32 unpackHalf2x16 unpackSnorm2x16 unpackSnorm4x8 unpackUnorm2x16 unpackUnorm4x8 usubBorrow",
literal:"true false"},illegal:'"',
contains:[e.C_LINE_COMMENT_MODE,e.C_BLOCK_COMMENT_MODE,e.C_NUMBER_MODE,{
className:"meta",begin:"#",end:"$"}]})})());hljs.registerLanguage("clojure",(()=>{"use strict";return e=>{
var t="a-zA-Z_\\-!.?+*=<>&#'",n="["+t+"]["+t+"0-9/;:]*",r="def defonce defprotocol defstruct defmulti defmethod defn- defn defmacro deftype defrecord",a={
$pattern:n,
"builtin-name":r+" cond apply if-not if-let if not not= =|0 <|0 >|0 <=|0 >=|0 ==|0 +|0 /|0 *|0 -|0 rem quot neg? pos? delay? symbol? keyword? true? false? integer? empty? coll? list? set? ifn? fn? associative? sequential? sorted? counted? reversible? number? decimal? class? distinct? isa? float? rational? reduced? ratio? odd? even? char? seq? vector? string? map? nil? contains? zero? instance? not-every? not-any? libspec? -> ->> .. . inc compare do dotimes mapcat take remove take-while drop letfn drop-last take-last drop-while while intern condp case reduced cycle split-at split-with repeat replicate iterate range merge zipmap declare line-seq sort comparator sort-by dorun doall nthnext nthrest partition eval doseq await await-for let agent atom send send-off release-pending-sends add-watch mapv filterv remove-watch agent-error restart-agent set-error-handler error-handler set-error-mode! error-mode shutdown-agents quote var fn loop recur throw try monitor-enter monitor-exit macroexpand macroexpand-1 for dosync and or when when-not when-let comp juxt partial sequence memoize constantly complement identity assert peek pop doto proxy first rest cons cast coll last butlast sigs reify second ffirst fnext nfirst nnext meta with-meta ns in-ns create-ns import refer keys select-keys vals key val rseq name namespace promise into transient persistent! conj! assoc! dissoc! pop! disj! use class type num float double short byte boolean bigint biginteger bigdec print-method print-dup throw-if printf format load compile get-in update-in pr pr-on newline flush read slurp read-line subvec with-open memfn time re-find re-groups rand-int rand mod locking assert-valid-fdecl alias resolve ref deref refset swap! reset! set-validator! compare-and-set! alter-meta! reset-meta! commute get-validator alter ref-set ref-history-count ref-min-history ref-max-history ensure sync io! new next conj set! to-array future future-call into-array aset gen-class reduce map filter find empty hash-map hash-set sorted-map sorted-map-by sorted-set sorted-set-by vec vector seq flatten reverse assoc dissoc list disj get union difference intersection extend extend-type extend-protocol int nth delay count concat chunk chunk-buffer chunk-append chunk-first chunk-rest max min dec unchecked-inc-int unchecked-inc unchecked-dec-inc unchecked-dec unchecked-negate unchecked-add-int unchecked-add unchecked-subtract-int unchecked-subtract chunk-next chunk-cons chunked-seq? prn vary-meta lazy-seq spread list* str find-keyword keyword symbol gensym force rationalize"
},s={begin:n,relevance:0},o={className:"number",begin:"[-+]?\\d+(\\.\\d+)?",
relevance:0},i=e.inherit(e.QUOTE_STRING_MODE,{illegal:null
}),c=e.COMMENT(";","$",{relevance:0}),d={className:"literal",
begin:/\b(true|false|nil)\b/},l={begin:"[\\[\\{]",end:"[\\]\\}]"},m={
className:"comment",begin:"\\^"+n},p=e.COMMENT("\\^\\{","\\}"),u={
className:"symbol",begin:"[:]{1,2}"+n},f={begin:"\\(",end:"\\)"},h={
endsWithParent:!0,relevance:0},y={keywords:a,className:"name",begin:n,
relevance:0,starts:h},g=[f,i,m,p,c,u,l,o,d,s],b={beginKeywords:r,lexemes:n,
end:'(\\[|#|\\d|"|:|\\{|\\)|\\(|$)',contains:[{className:"title",begin:n,
relevance:0,excludeEnd:!0,endsParent:!0}].concat(g)}
;return f.contains=[e.COMMENT("comment",""),b,y,h],
h.contains=g,l.contains=g,p.contains=[l],{name:"Clojure",aliases:["clj"],
illegal:/\S/,contains:[f,i,m,p,c,u,l,o,d]}}})());hljs.registerLanguage("diff",(()=>{"use strict";return e=>({name:"Diff",
aliases:["patch"],contains:[{className:"meta",relevance:10,variants:[{
begin:/^@@ +-\d+,\d+ +\+\d+,\d+ +@@/},{begin:/^\*\*\* +\d+,\d+ +\*\*\*\*$/},{
begin:/^--- +\d+,\d+ +----$/}]},{className:"comment",variants:[{begin:/Index: /,
end:/$/},{begin:/^index/,end:/$/},{begin:/={3,}/,end:/$/},{begin:/^-{3}/,end:/$/
},{begin:/^\*{3} /,end:/$/},{begin:/^\+{3}/,end:/$/},{begin:/^\*{15}$/},{
begin:/^diff --git/,end:/$/}]},{className:"addition",begin:/^\+/,end:/$/},{
className:"deletion",begin:/^-/,end:/$/},{className:"addition",begin:/^!/,
end:/$/}]})})());hljs.registerLanguage("nginx",(()=>{"use strict";return e=>{const n={
className:"variable",variants:[{begin:/\$\d+/},{begin:/\$\{/,end:/\}/},{
begin:/[$@]/+e.UNDERSCORE_IDENT_RE}]},a={endsWithParent:!0,keywords:{
$pattern:"[a-z/_]+",
literal:"on off yes no true false none blocked debug info notice warn error crit select break last permanent redirect kqueue rtsig epoll poll /dev/poll"
},relevance:0,illegal:"=>",contains:[e.HASH_COMMENT_MODE,{className:"string",
contains:[e.BACKSLASH_ESCAPE,n],variants:[{begin:/"/,end:/"/},{begin:/'/,end:/'/
}]},{begin:"([a-z]+):/",end:"\\s",endsWithParent:!0,excludeEnd:!0,contains:[n]
},{className:"regexp",contains:[e.BACKSLASH_ESCAPE,n],variants:[{begin:"\\s\\^",
end:"\\s|\\{|;",returnEnd:!0},{begin:"~\\*?\\s+",end:"\\s|\\{|;",returnEnd:!0},{
begin:"\\*(\\.[a-z\\-]+)+"},{begin:"([a-z\\-]+\\.)+\\*"}]},{className:"number",
begin:"\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}(:\\d{1,5})?\\b"},{
className:"number",begin:"\\b\\d+[kKmMgGdshdwy]*\\b",relevance:0},n]};return{
name:"Nginx config",aliases:["nginxconf"],contains:[e.HASH_COMMENT_MODE,{
begin:e.UNDERSCORE_IDENT_RE+"\\s+\\{",returnBegin:!0,end:/\{/,contains:[{
className:"section",begin:e.UNDERSCORE_IDENT_RE}],relevance:0},{
begin:e.UNDERSCORE_IDENT_RE+"\\s",end:";|\\{",returnBegin:!0,contains:[{
className:"attribute",begin:e.UNDERSCORE_IDENT_RE,starts:a}],relevance:0}],
illegal:"[^\\s\\}]"}}})());hljs.registerLanguage("sql",(()=>{"use strict";function e(e){
return e?"string"==typeof e?e:e.source:null}function r(...r){
return r.map((r=>e(r))).join("")}function t(...r){
return"("+r.map((r=>e(r))).join("|")+")"}return e=>{
const n=e.COMMENT("--","$"),a=["true","false","unknown"],i=["bigint","binary","blob","boolean","char","character","clob","date","dec","decfloat","decimal","float","int","integer","interval","nchar","nclob","national","numeric","real","row","smallint","time","timestamp","varchar","varying","varbinary"],s=["abs","acos","array_agg","asin","atan","avg","cast","ceil","ceiling","coalesce","corr","cos","cosh","count","covar_pop","covar_samp","cume_dist","dense_rank","deref","element","exp","extract","first_value","floor","json_array","json_arrayagg","json_exists","json_object","json_objectagg","json_query","json_table","json_table_primitive","json_value","lag","last_value","lead","listagg","ln","log","log10","lower","max","min","mod","nth_value","ntile","nullif","percent_rank","percentile_cont","percentile_disc","position","position_regex","power","rank","regr_avgx","regr_avgy","regr_count","regr_intercept","regr_r2","regr_slope","regr_sxx","regr_sxy","regr_syy","row_number","sin","sinh","sqrt","stddev_pop","stddev_samp","substring","substring_regex","sum","tan","tanh","translate","translate_regex","treat","trim","trim_array","unnest","upper","value_of","var_pop","var_samp","width_bucket"],o=["create table","insert into","primary key","foreign key","not null","alter table","add constraint","grouping sets","on overflow","character set","respect nulls","ignore nulls","nulls first","nulls last","depth first","breadth first"],c=s,l=["abs","acos","all","allocate","alter","and","any","are","array","array_agg","array_max_cardinality","as","asensitive","asin","asymmetric","at","atan","atomic","authorization","avg","begin","begin_frame","begin_partition","between","bigint","binary","blob","boolean","both","by","call","called","cardinality","cascaded","case","cast","ceil","ceiling","char","char_length","character","character_length","check","classifier","clob","close","coalesce","collate","collect","column","commit","condition","connect","constraint","contains","convert","copy","corr","corresponding","cos","cosh","count","covar_pop","covar_samp","create","cross","cube","cume_dist","current","current_catalog","current_date","current_default_transform_group","current_path","current_role","current_row","current_schema","current_time","current_timestamp","current_path","current_role","current_transform_group_for_type","current_user","cursor","cycle","date","day","deallocate","dec","decimal","decfloat","declare","default","define","delete","dense_rank","deref","describe","deterministic","disconnect","distinct","double","drop","dynamic","each","element","else","empty","end","end_frame","end_partition","end-exec","equals","escape","every","except","exec","execute","exists","exp","external","extract","false","fetch","filter","first_value","float","floor","for","foreign","frame_row","free","from","full","function","fusion","get","global","grant","group","grouping","groups","having","hold","hour","identity","in","indicator","initial","inner","inout","insensitive","insert","int","integer","intersect","intersection","interval","into","is","join","json_array","json_arrayagg","json_exists","json_object","json_objectagg","json_query","json_table","json_table_primitive","json_value","lag","language","large","last_value","lateral","lead","leading","left","like","like_regex","listagg","ln","local","localtime","localtimestamp","log","log10","lower","match","match_number","match_recognize","matches","max","member","merge","method","min","minute","mod","modifies","module","month","multiset","national","natural","nchar","nclob","new","no","none","normalize","not","nth_value","ntile","null","nullif","numeric","octet_length","occurrences_regex","of","offset","old","omit","on","one","only","open","or","order","out","outer","over","overlaps","overlay","parameter","partition","pattern","per","percent","percent_rank","percentile_cont","percentile_disc","period","portion","position","position_regex","power","precedes","precision","prepare","primary","procedure","ptf","range","rank","reads","real","recursive","ref","references","referencing","regr_avgx","regr_avgy","regr_count","regr_intercept","regr_r2","regr_slope","regr_sxx","regr_sxy","regr_syy","release","result","return","returns","revoke","right","rollback","rollup","row","row_number","rows","running","savepoint","scope","scroll","search","second","seek","select","sensitive","session_user","set","show","similar","sin","sinh","skip","smallint","some","specific","specifictype","sql","sqlexception","sqlstate","sqlwarning","sqrt","start","static","stddev_pop","stddev_samp","submultiset","subset","substring","substring_regex","succeeds","sum","symmetric","system","system_time","system_user","table","tablesample","tan","tanh","then","time","timestamp","timezone_hour","timezone_minute","to","trailing","translate","translate_regex","translation","treat","trigger","trim","trim_array","true","truncate","uescape","union","unique","unknown","unnest","update   ","upper","user","using","value","values","value_of","var_pop","var_samp","varbinary","varchar","varying","versioning","when","whenever","where","width_bucket","window","with","within","without","year","add","asc","collation","desc","final","first","last","view"].filter((e=>!s.includes(e))),u={
begin:r(/\b/,t(...c),/\s*\(/),keywords:{built_in:c.join(" ")}};return{
name:"SQL",case_insensitive:!0,illegal:/[{}]|<\//,keywords:{
$pattern:/\b[\w\.]+/,keyword:((e,{exceptions:r,when:t}={})=>{const n=t
;return r=r||[],e.map((e=>e.match(/\|\d+$/)||r.includes(e)?e:n(e)?e+"|0":e))
})(l,{when:e=>e.length<3}).join(" "),literal:a.join(" "),type:i.join(" "),
built_in:"current_catalog current_date current_default_transform_group current_path current_role current_schema current_transform_group_for_type current_user session_user system_time system_user current_time localtime current_timestamp localtimestamp"
},contains:[{begin:t(...o),keywords:{$pattern:/[\w\.]+/,
keyword:l.concat(o).join(" "),literal:a.join(" "),type:i.join(" ")}},{
className:"type",
begin:t("double precision","large object","with timezone","without timezone")
},u,{className:"variable",begin:/@[a-z0-9]+/},{className:"string",variants:[{
begin:/'/,end:/'/,contains:[{begin:/''/}]}]},{begin:/"/,end:/"/,contains:[{
begin:/""/}]},e.C_NUMBER_MODE,e.C_BLOCK_COMMENT_MODE,n,{className:"operator",
begin:/[-+*/=%^~]|&&?|\|\|?|!=?|<(?:=>?|<|>)?|>[>=]?/,relevance:0}]}}})());hljs.registerLanguage("clojure-repl",(()=>{"use strict";return e=>({
name:"Clojure REPL",contains:[{className:"meta",begin:/^([\w.-]+|\s*#_)?=>/,
starts:{end:/$/,subLanguage:"clojure"}}]})})());hljs.registerLanguage("scala",(()=>{"use strict";return e=>{const n={
className:"subst",variants:[{begin:"\\$[A-Za-z0-9_]+"},{begin:/\$\{/,end:/\}/}]
},a={className:"string",variants:[{begin:'"',end:'"',illegal:"\\n",
contains:[e.BACKSLASH_ESCAPE]},{begin:'"""',end:'"""',relevance:10},{
begin:'[a-z]+"',end:'"',illegal:"\\n",contains:[e.BACKSLASH_ESCAPE,n]},{
className:"string",begin:'[a-z]+"""',end:'"""',contains:[n],relevance:10}]},s={
className:"type",begin:"\\b[A-Z][A-Za-z0-9_]*",relevance:0},t={
className:"title",
begin:/[^0-9\n\t "'(),.`{}\[\]:;][^\n\t "'(),.`{}\[\]:;]+|[^0-9\n\t "'(),.`{}\[\]:;=]/,
relevance:0},i={className:"class",beginKeywords:"class object trait type",
end:/[:={\[\n;]/,excludeEnd:!0,
contains:[e.C_LINE_COMMENT_MODE,e.C_BLOCK_COMMENT_MODE,{
beginKeywords:"extends with",relevance:10},{begin:/\[/,end:/\]/,excludeBegin:!0,
excludeEnd:!0,relevance:0,contains:[s]},{className:"params",begin:/\(/,end:/\)/,
excludeBegin:!0,excludeEnd:!0,relevance:0,contains:[s]},t]},l={
className:"function",beginKeywords:"def",end:/[:={\[(\n;]/,excludeEnd:!0,
contains:[t]};return{name:"Scala",keywords:{literal:"true false null",
keyword:"type yield lazy override def with val var sealed abstract private trait object if forSome for while throw finally protected extends import final return else break new catch super class case package default try this match continue throws implicit"
},contains:[e.C_LINE_COMMENT_MODE,e.C_BLOCK_COMMENT_MODE,a,{className:"symbol",
begin:"'\\w[\\w\\d_]*(?!')"},s,l,i,e.C_NUMBER_MODE,{className:"meta",
begin:"@[A-Za-z]+"}]}}})());hljs.registerLanguage("less",(()=>{"use strict";return e=>{
var n="([\\w-]+|@\\{[\\w-]+\\})",a=[],s=[],t=e=>({className:"string",
begin:"~?"+e+".*?"+e}),r=(e,n,a)=>({className:e,begin:n,relevance:a}),i={
begin:"\\(",end:"\\)",contains:s,relevance:0}
;s.push(e.C_LINE_COMMENT_MODE,e.C_BLOCK_COMMENT_MODE,t("'"),t('"'),e.CSS_NUMBER_MODE,{
begin:"(url|data-uri)\\(",starts:{className:"string",end:"[\\)\\n]",
excludeEnd:!0}
},r("number","#[0-9A-Fa-f]+\\b"),i,r("variable","@@?[\\w-]+",10),r("variable","@\\{[\\w-]+\\}"),r("built_in","~?`[^`]*?`"),{
className:"attribute",begin:"[\\w-]+\\s*:",end:":",returnBegin:!0,excludeEnd:!0
},{className:"meta",begin:"!important"});var c=s.concat({begin:/\{/,end:/\}/,
contains:a}),l={beginKeywords:"when",endsWithParent:!0,contains:[{
beginKeywords:"and not"}].concat(s)},g={begin:n+"\\s*:",returnBegin:!0,
end:"[;}]",relevance:0,contains:[{className:"attribute",begin:n,end:":",
excludeEnd:!0,starts:{endsWithParent:!0,illegal:"[<=$]",relevance:0,contains:s}
}]},d={className:"keyword",
begin:"@(import|media|charset|font-face|(-[a-z]+-)?keyframes|supports|document|namespace|page|viewport|host)\\b",
starts:{end:"[;{}]",returnEnd:!0,contains:s,relevance:0}},o={
className:"variable",variants:[{begin:"@[\\w-]+\\s*:",relevance:15},{
begin:"@[\\w-]+"}],starts:{end:"[;}]",returnEnd:!0,contains:c}},b={variants:[{
begin:"[\\.#:&\\[>]",end:"[;{}]"},{begin:n,end:/\{/}],returnBegin:!0,
returnEnd:!0,illegal:"[<='$\"]",relevance:0,
contains:[e.C_LINE_COMMENT_MODE,e.C_BLOCK_COMMENT_MODE,l,r("keyword","all\\b"),r("variable","@\\{[\\w-]+\\}"),r("selector-tag",n+"%?",0),r("selector-id","#"+n),r("selector-class","\\."+n,0),r("selector-tag","&",0),{
className:"selector-attr",begin:"\\[",end:"\\]"},{className:"selector-pseudo",
begin:/:(:)?[a-zA-Z0-9_\-+()"'.]+/},{begin:"\\(",end:"\\)",contains:c},{
begin:"!important"}]}
;return a.push(e.C_LINE_COMMENT_MODE,e.C_BLOCK_COMMENT_MODE,d,o,g,b),{
name:"Less",case_insensitive:!0,illegal:"[=>'/<($\"]",contains:a}}})());hljs.registerLanguage("swift",(()=>{"use strict";function e(e){
return e?"string"==typeof e?e:e.source:null}function n(e){return i("(?=",e,")")}
function i(...n){return n.map((n=>e(n))).join("")}function a(...n){
return"("+n.map((n=>e(n))).join("|")+")"}
const t=e=>i(/\b/,e,/\w$/.test(e)?/\b/:/\B/),u=["Protocol","Type"].map(t),s=["init","self"].map(t),r=["Any","Self"],o=["associatedtype",/as\?/,/as!/,"as","break","case","catch","class","continue","convenience","default","defer","deinit","didSet","do","dynamic","else","enum","extension","fallthrough","fileprivate(set)","fileprivate","final","for","func","get","guard","if","import","indirect","infix",/init\?/,/init!/,"inout","internal(set)","internal","in","is","lazy","let","mutating","nonmutating","open(set)","open","operator","optional","override","postfix","precedencegroup","prefix","private(set)","private","protocol","public(set)","public","repeat","required","rethrows","return","set","some","static","struct","subscript","super","switch","throws","throw",/try\?/,/try!/,"try","typealias","unowned(safe)","unowned(unsafe)","unowned","var","weak","where","while","willSet"],l=["false","nil","true"],c=["#colorLiteral","#column","#dsohandle","#else","#elseif","#endif","#error","#file","#fileID","#fileLiteral","#filePath","#function","#if","#imageLiteral","#keyPath","#line","#selector","#sourceLocation","#warn_unqualified_access","#warning"],b=["abs","all","any","assert","assertionFailure","debugPrint","dump","fatalError","getVaList","isKnownUniquelyReferenced","max","min","numericCast","pointwiseMax","pointwiseMin","precondition","preconditionFailure","print","readLine","repeatElement","sequence","stride","swap","swift_unboxFromSwiftValueWithType","transcode","type","unsafeBitCast","unsafeDowncast","withExtendedLifetime","withUnsafeMutablePointer","withUnsafePointer","withVaList","withoutActuallyEscaping","zip"],p=a(/[/=\-+!*%<>&|^~?]/,/[\u00A1-\u00A7]/,/[\u00A9\u00AB]/,/[\u00AC\u00AE]/,/[\u00B0\u00B1]/,/[\u00B6\u00BB\u00BF\u00D7\u00F7]/,/[\u2016-\u2017]/,/[\u2020-\u2027]/,/[\u2030-\u203E]/,/[\u2041-\u2053]/,/[\u2055-\u205E]/,/[\u2190-\u23FF]/,/[\u2500-\u2775]/,/[\u2794-\u2BFF]/,/[\u2E00-\u2E7F]/,/[\u3001-\u3003]/,/[\u3008-\u3020]/,/[\u3030]/),F=a(p,/[\u0300-\u036F]/,/[\u1DC0-\u1DFF]/,/[\u20D0-\u20FF]/,/[\uFE00-\uFE0F]/,/[\uFE20-\uFE2F]/),d=i(p,F,"*"),g=a(/[a-zA-Z_]/,/[\u00A8\u00AA\u00AD\u00AF\u00B2-\u00B5\u00B7-\u00BA]/,/[\u00BC-\u00BE\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u00FF]/,/[\u0100-\u02FF\u0370-\u167F\u1681-\u180D\u180F-\u1DBF]/,/[\u1E00-\u1FFF]/,/[\u200B-\u200D\u202A-\u202E\u203F-\u2040\u2054\u2060-\u206F]/,/[\u2070-\u20CF\u2100-\u218F\u2460-\u24FF\u2776-\u2793]/,/[\u2C00-\u2DFF\u2E80-\u2FFF]/,/[\u3004-\u3007\u3021-\u302F\u3031-\u303F\u3040-\uD7FF]/,/[\uF900-\uFD3D\uFD40-\uFDCF\uFDF0-\uFE1F\uFE30-\uFE44]/,/[\uFE47-\uFFFD]/),f=a(g,/\d/,/[\u0300-\u036F\u1DC0-\u1DFF\u20D0-\u20FF\uFE20-\uFE2F]/),m=i(g,f,"*"),w=i(/[A-Z]/,f,"*"),E=["autoclosure",i(/convention\(/,a("swift","block","c"),/\)/),"discardableResult","dynamicCallable","dynamicMemberLookup","escaping","frozen","GKInspectable","IBAction","IBDesignable","IBInspectable","IBOutlet","IBSegueAction","inlinable","main","nonobjc","NSApplicationMain","NSCopying","NSManaged",i(/objc\(/,m,/\)/),"objc","objcMembers","propertyWrapper","requires_stored_property_inits","testable","UIApplicationMain","unknown","usableFromInline"],y=["iOS","iOSApplicationExtension","macOS","macOSApplicationExtension","macCatalyst","macCatalystApplicationExtension","watchOS","watchOSApplicationExtension","tvOS","tvOSApplicationExtension","swift"]
;return e=>{const p=e.COMMENT("/\\*","\\*/",{contains:["self"]}),g={
className:"keyword",begin:i(/\./,n(a(...u,...s))),end:a(...u,...s),
excludeBegin:!0},A={begin:i(/\./,a(...o)),relevance:0
},C=o.filter((e=>"string"==typeof e)).concat(["_|0"]),v={variants:[{
className:"keyword",
begin:a(...o.filter((e=>"string"!=typeof e)).concat(r).map(t),...s)}]},_={
$pattern:a(/\b\w+(\(\w+\))?/,/#\w+/),keyword:C.concat(c).join(" "),
literal:l.join(" ")},N=[g,A,v],D=[{begin:i(/\./,a(...b)),relevance:0},{
className:"built_in",begin:i(/\b/,a(...b),/(?=\()/)}],B={begin:/->/,relevance:0
},M=[B,{className:"operator",relevance:0,variants:[{begin:d},{
begin:`\\.(\\.|${F})+`}]}],h="([0-9a-fA-F]_*)+",S={className:"number",
relevance:0,variants:[{
begin:"\\b(([0-9]_*)+)(\\.(([0-9]_*)+))?([eE][+-]?(([0-9]_*)+))?\\b"},{
begin:`\\b0x(${h})(\\.(${h}))?([pP][+-]?(([0-9]_*)+))?\\b`},{
begin:/\b0o([0-7]_*)+\b/},{begin:/\b0b([01]_*)+\b/}]},O=(e="")=>({
className:"subst",variants:[{begin:i(/\\/,e,/[0\\tnr"']/)},{
begin:i(/\\/,e,/u\{[0-9a-fA-F]{1,8}\}/)}]}),x=(e="")=>({className:"subst",
begin:i(/\\/,e,/[\t ]*(?:[\r\n]|\r\n)/)}),k=(e="")=>({className:"subst",
label:"interpol",begin:i(/\\/,e,/\(/),end:/\)/}),L=(e="")=>({begin:i(e,/"""/),
end:i(/"""/,e),contains:[O(e),x(e),k(e)]}),I=(e="")=>({begin:i(e,/"/),
end:i(/"/,e),contains:[O(e),k(e)]}),$={className:"string",
variants:[L(),L("#"),L("##"),L("###"),I(),I("#"),I("##"),I("###")]},T=[{
begin:i(/`/,m,/`/)},{className:"variable",begin:/\$\d+/},{className:"variable",
begin:`\\$${f}+`}],j=[{begin:/(@|#)available\(/,end:/\)/,keywords:{
$pattern:/[@#]?\w+/,keyword:y.concat(["@available","#available"]).join(" ")},
contains:[...M,S,$]},{className:"keyword",begin:i(/@/,a(...E))},{
className:"meta",begin:i(/@/,m)}],K={begin:n(/\b[A-Z]/),relevance:0,contains:[{
className:"type",
begin:i(/(AV|CA|CF|CG|CI|CL|CM|CN|CT|MK|MP|MTK|MTL|NS|SCN|SK|UI|WK|XC)/,f,"+")
},{className:"type",begin:w,relevance:0},{begin:/[?!]+/,relevance:0},{
begin:/\.\.\./,relevance:0},{begin:i(/\s+&\s+/,n(w)),relevance:0}]},P={
begin:/</,end:/>/,keywords:_,contains:[...N,...j,B,K]};K.contains.push(P)
;for(const e of $.variants){const n=e.contains.find((e=>"interpol"===e.label))
;n.keywords=_;const i=[...N,...D,...M,S,$,...T];n.contains=[...i,{begin:/\(/,
end:/\)/,contains:["self",...i]}]}return{name:"Swift",keywords:_,
contains:[e.C_LINE_COMMENT_MODE,p,{className:"function",beginKeywords:"func",
end:/\{/,excludeEnd:!0,contains:[e.inherit(e.TITLE_MODE,{
begin:/[A-Za-z$_][0-9A-Za-z$_]*/}),{begin:/</,end:/>/},{className:"params",
begin:/\(/,end:/\)/,endsParent:!0,keywords:_,
contains:["self",...N,S,$,e.C_BLOCK_COMMENT_MODE,{begin:":"}],illegal:/["']/}],
illegal:/\[|%/},{className:"class",
beginKeywords:"struct protocol class extension enum",end:"\\{",excludeEnd:!0,
keywords:_,contains:[e.inherit(e.TITLE_MODE,{
begin:/[A-Za-z$_][\u00C0-\u02B80-9A-Za-z$_]*/}),...N]},{beginKeywords:"import",
end:/$/,contains:[e.C_LINE_COMMENT_MODE,p],relevance:0
},...N,...D,...M,S,$,...T,...j,K]}}})());hljs.registerLanguage("php",(()=>{"use strict";return e=>{const r={
className:"variable",
begin:"\\$+[a-zA-Z_\x7f-\xff][a-zA-Z0-9_\x7f-\xff]*(?![A-Za-z0-9])(?![$])"},t={
className:"meta",variants:[{begin:/<\?php/,relevance:10},{begin:/<\?[=]?/},{
begin:/\?>/}]},a={className:"subst",variants:[{begin:/\$\w+/},{begin:/\{\$/,
end:/\}/}]},n=e.inherit(e.APOS_STRING_MODE,{illegal:null
}),i=e.inherit(e.QUOTE_STRING_MODE,{illegal:null,
contains:e.QUOTE_STRING_MODE.contains.concat(a)}),o=e.END_SAME_AS_BEGIN({
begin:/<<<[ \t]*(\w+)\n/,end:/[ \t]*(\w+)\b/,
contains:e.QUOTE_STRING_MODE.contains.concat(a)}),l={className:"string",
contains:[e.BACKSLASH_ESCAPE,t],variants:[e.inherit(n,{begin:"b'",end:"'"
}),e.inherit(i,{begin:'b"',end:'"'}),i,n,o]},c={
variants:[e.BINARY_NUMBER_MODE,e.C_NUMBER_MODE]},s={
keyword:"__CLASS__ __DIR__ __FILE__ __FUNCTION__ __LINE__ __METHOD__ __NAMESPACE__ __TRAIT__ die echo exit include include_once print require require_once array abstract and as binary bool boolean break callable case catch class clone const continue declare default do double else elseif empty enddeclare endfor endforeach endif endswitch endwhile eval extends final finally float for foreach from global goto if implements instanceof insteadof int integer interface isset iterable list match|0 new object or private protected public real return string switch throw trait try unset use var void while xor yield",
literal:"false null true",
built_in:"Error|0 AppendIterator ArgumentCountError ArithmeticError ArrayIterator ArrayObject AssertionError BadFunctionCallException BadMethodCallException CachingIterator CallbackFilterIterator CompileError Countable DirectoryIterator DivisionByZeroError DomainException EmptyIterator ErrorException Exception FilesystemIterator FilterIterator GlobIterator InfiniteIterator InvalidArgumentException IteratorIterator LengthException LimitIterator LogicException MultipleIterator NoRewindIterator OutOfBoundsException OutOfRangeException OuterIterator OverflowException ParentIterator ParseError RangeException RecursiveArrayIterator RecursiveCachingIterator RecursiveCallbackFilterIterator RecursiveDirectoryIterator RecursiveFilterIterator RecursiveIterator RecursiveIteratorIterator RecursiveRegexIterator RecursiveTreeIterator RegexIterator RuntimeException SeekableIterator SplDoublyLinkedList SplFileInfo SplFileObject SplFixedArray SplHeap SplMaxHeap SplMinHeap SplObjectStorage SplObserver SplObserver SplPriorityQueue SplQueue SplStack SplSubject SplSubject SplTempFileObject TypeError UnderflowException UnexpectedValueException ArrayAccess Closure Generator Iterator IteratorAggregate Serializable Throwable Traversable WeakReference Directory __PHP_Incomplete_Class parent php_user_filter self static stdClass"
};return{aliases:["php","php3","php4","php5","php6","php7","php8"],
case_insensitive:!0,keywords:s,
contains:[e.HASH_COMMENT_MODE,e.COMMENT("//","$",{contains:[t]
}),e.COMMENT("/\\*","\\*/",{contains:[{className:"doctag",begin:"@[A-Za-z]+"}]
}),e.COMMENT("__halt_compiler.+?;",!1,{endsWithParent:!0,
keywords:"__halt_compiler"}),t,{className:"keyword",begin:/\$this\b/},r,{
begin:/(::|->)+[a-zA-Z_\x7f-\xff][a-zA-Z0-9_\x7f-\xff]*/},{className:"function",
relevance:0,beginKeywords:"fn function",end:/[;{]/,excludeEnd:!0,
illegal:"[$%\\[]",contains:[e.UNDERSCORE_TITLE_MODE,{begin:"=>"},{
className:"params",begin:"\\(",end:"\\)",excludeBegin:!0,excludeEnd:!0,
keywords:s,contains:["self",r,e.C_BLOCK_COMMENT_MODE,l,c]}]},{className:"class",
beginKeywords:"class interface",relevance:0,end:/\{/,excludeEnd:!0,
illegal:/[:($"]/,contains:[{beginKeywords:"extends implements"
},e.UNDERSCORE_TITLE_MODE]},{beginKeywords:"namespace",relevance:0,end:";",
illegal:/[.']/,contains:[e.UNDERSCORE_TITLE_MODE]},{beginKeywords:"use",
relevance:0,end:";",contains:[e.UNDERSCORE_TITLE_MODE]},l,c]}}})());hljs.registerLanguage("php-template",(()=>{"use strict";return n=>({
name:"PHP template",subLanguage:"xml",contains:[{begin:/<\?(php|=)?/,end:/\?>/,
subLanguage:"php",contains:[{begin:"/\\*",end:"\\*/",skip:!0},{begin:'b"',
end:'"',skip:!0},{begin:"b'",end:"'",skip:!0},n.inherit(n.APOS_STRING_MODE,{
illegal:null,className:null,contains:null,skip:!0
}),n.inherit(n.QUOTE_STRING_MODE,{illegal:null,className:null,contains:null,
skip:!0})]}]})})());hljs.registerLanguage("javascript",(()=>{"use strict"
;const e="[A-Za-z$_][0-9A-Za-z$_]*",n=["as","in","of","if","for","while","finally","var","new","function","do","return","void","else","break","catch","instanceof","with","throw","case","default","try","switch","continue","typeof","delete","let","yield","const","class","debugger","async","await","static","import","from","export","extends"],a=["true","false","null","undefined","NaN","Infinity"],s=[].concat(["setInterval","setTimeout","clearInterval","clearTimeout","require","exports","eval","isFinite","isNaN","parseFloat","parseInt","decodeURI","decodeURIComponent","encodeURI","encodeURIComponent","escape","unescape"],["arguments","this","super","console","window","document","localStorage","module","global"],["Intl","DataView","Number","Math","Date","String","RegExp","Object","Function","Boolean","Error","Symbol","Set","Map","WeakSet","WeakMap","Proxy","Reflect","JSON","Promise","Float64Array","Int16Array","Int32Array","Int8Array","Uint16Array","Uint32Array","Float32Array","Array","Uint8Array","Uint8ClampedArray","ArrayBuffer"],["EvalError","InternalError","RangeError","ReferenceError","SyntaxError","TypeError","URIError"])
;function r(e){return i("(?=",e,")")}function i(...e){return e.map((e=>{
return(n=e)?"string"==typeof n?n:n.source:null;var n})).join("")}return t=>{
const c=e,o={begin:/<[A-Za-z0-9\\._:-]+/,end:/\/[A-Za-z0-9\\._:-]+>|\/>/,
isTrulyOpeningTag:(e,n)=>{const a=e[0].length+e.index,s=e.input[a]
;"<"!==s?">"===s&&(((e,{after:n})=>{const a="</"+e[0].slice(1)
;return-1!==e.input.indexOf(a,n)})(e,{after:a
})||n.ignoreMatch()):n.ignoreMatch()}},l={$pattern:e,keyword:n.join(" "),
literal:a.join(" "),built_in:s.join(" ")
},b="\\.([0-9](_?[0-9])*)",g="0|[1-9](_?[0-9])*|0[0-7]*[89][0-9]*",d={
className:"number",variants:[{
begin:`(\\b(${g})((${b})|\\.)?|(${b}))[eE][+-]?([0-9](_?[0-9])*)\\b`},{
begin:`\\b(${g})\\b((${b})\\b|\\.)?|(${b})\\b`},{
begin:"\\b(0|[1-9](_?[0-9])*)n\\b"},{
begin:"\\b0[xX][0-9a-fA-F](_?[0-9a-fA-F])*n?\\b"},{
begin:"\\b0[bB][0-1](_?[0-1])*n?\\b"},{begin:"\\b0[oO][0-7](_?[0-7])*n?\\b"},{
begin:"\\b0[0-7]+n?\\b"}],relevance:0},E={className:"subst",begin:"\\$\\{",
end:"\\}",keywords:l,contains:[]},u={begin:"html`",end:"",starts:{end:"`",
returnEnd:!1,contains:[t.BACKSLASH_ESCAPE,E],subLanguage:"xml"}},_={
begin:"css`",end:"",starts:{end:"`",returnEnd:!1,
contains:[t.BACKSLASH_ESCAPE,E],subLanguage:"css"}},m={className:"string",
begin:"`",end:"`",contains:[t.BACKSLASH_ESCAPE,E]},N={className:"comment",
variants:[t.COMMENT(/\/\*\*(?!\/)/,"\\*/",{relevance:0,contains:[{
className:"doctag",begin:"@[A-Za-z]+",contains:[{className:"type",begin:"\\{",
end:"\\}",relevance:0},{className:"variable",begin:c+"(?=\\s*(-)|$)",
endsParent:!0,relevance:0},{begin:/(?=[^\n])\s/,relevance:0}]}]
}),t.C_BLOCK_COMMENT_MODE,t.C_LINE_COMMENT_MODE]
},y=[t.APOS_STRING_MODE,t.QUOTE_STRING_MODE,u,_,m,d,t.REGEXP_MODE]
;E.contains=y.concat({begin:/\{/,end:/\}/,keywords:l,contains:["self"].concat(y)
});const f=[].concat(N,E.contains),A=f.concat([{begin:/\(/,end:/\)/,keywords:l,
contains:["self"].concat(f)}]),p={className:"params",begin:/\(/,end:/\)/,
excludeBegin:!0,excludeEnd:!0,keywords:l,contains:A};return{name:"Javascript",
aliases:["js","jsx","mjs","cjs"],keywords:l,exports:{PARAMS_CONTAINS:A},
illegal:/#(?![$_A-z])/,contains:[t.SHEBANG({label:"shebang",binary:"node",
relevance:5}),{label:"use_strict",className:"meta",relevance:10,
begin:/^\s*['"]use (strict|asm)['"]/
},t.APOS_STRING_MODE,t.QUOTE_STRING_MODE,u,_,m,N,d,{
begin:i(/[{,\n]\s*/,r(i(/(((\/\/.*$)|(\/\*(\*[^/]|[^*])*\*\/))\s*)*/,c+"\\s*:"))),
relevance:0,contains:[{className:"attr",begin:c+r("\\s*:"),relevance:0}]},{
begin:"("+t.RE_STARTERS_RE+"|\\b(case|return|throw)\\b)\\s*",
keywords:"return throw case",contains:[N,t.REGEXP_MODE,{className:"function",
begin:"(\\([^()]*(\\([^()]*(\\([^()]*\\)[^()]*)*\\)[^()]*)*\\)|"+t.UNDERSCORE_IDENT_RE+")\\s*=>",
returnBegin:!0,end:"\\s*=>",contains:[{className:"params",variants:[{
begin:t.UNDERSCORE_IDENT_RE,relevance:0},{className:null,begin:/\(\s*\)/,skip:!0
},{begin:/\(/,end:/\)/,excludeBegin:!0,excludeEnd:!0,keywords:l,contains:A}]}]
},{begin:/,/,relevance:0},{className:"",begin:/\s/,end:/\s*/,skip:!0},{
variants:[{begin:"<>",end:"</>"},{begin:o.begin,"on:begin":o.isTrulyOpeningTag,
end:o.end}],subLanguage:"xml",contains:[{begin:o.begin,end:o.end,skip:!0,
contains:["self"]}]}],relevance:0},{className:"function",
beginKeywords:"function",end:/[{;]/,excludeEnd:!0,keywords:l,
contains:["self",t.inherit(t.TITLE_MODE,{begin:c}),p],illegal:/%/},{
beginKeywords:"while if switch catch for"},{className:"function",
begin:t.UNDERSCORE_IDENT_RE+"\\([^()]*(\\([^()]*(\\([^()]*\\)[^()]*)*\\)[^()]*)*\\)\\s*\\{",
returnBegin:!0,contains:[p,t.inherit(t.TITLE_MODE,{begin:c})]},{variants:[{
begin:"\\."+c},{begin:"\\$"+c}],relevance:0},{className:"class",
beginKeywords:"class",end:/[{;=]/,excludeEnd:!0,illegal:/[:"[\]]/,contains:[{
beginKeywords:"extends"},t.UNDERSCORE_TITLE_MODE]},{begin:/\b(?=constructor)/,
end:/[{;]/,excludeEnd:!0,contains:[t.inherit(t.TITLE_MODE,{begin:c}),"self",p]
},{begin:"(get|set)\\s+(?="+c+"\\()",end:/\{/,keywords:"get set",
contains:[t.inherit(t.TITLE_MODE,{begin:c}),{begin:/\(\)/},p]},{begin:/\$[(.]/}]
}}})());hljs.registerLanguage("ini",(()=>{"use strict";function e(e){
return e?"string"==typeof e?e:e.source:null}function n(...n){
return n.map((n=>e(n))).join("")}return s=>{const a={className:"number",
relevance:0,variants:[{begin:/([+-]+)?[\d]+_[\d_]+/},{begin:s.NUMBER_RE}]
},i=s.COMMENT();i.variants=[{begin:/;/,end:/$/},{begin:/#/,end:/$/}];const t={
className:"variable",variants:[{begin:/\$[\w\d"][\w\d_]*/},{begin:/\$\{(.*?)\}/
}]},r={className:"literal",begin:/\bon|off|true|false|yes|no\b/},l={
className:"string",contains:[s.BACKSLASH_ESCAPE],variants:[{begin:"'''",
end:"'''",relevance:10},{begin:'"""',end:'"""',relevance:10},{begin:'"',end:'"'
},{begin:"'",end:"'"}]},c={begin:/\[/,end:/\]/,contains:[i,r,t,l,a,"self"],
relevance:0
},g="("+[/[A-Za-z0-9_-]+/,/"(\\"|[^"])*"/,/'[^']*'/].map((n=>e(n))).join("|")+")"
;return{name:"TOML, also INI",aliases:["toml"],case_insensitive:!0,illegal:/\S/,
contains:[i,{className:"section",begin:/\[+/,end:/\]+/},{
begin:n(g,"(\\s*\\.\\s*",g,")*",n("(?=",/\s*=\s*[^#\s]/,")")),className:"attr",
starts:{end:/$/,contains:[i,c,r,t,l,a]}}]}}})());hljs.registerLanguage("apache",(()=>{"use strict";return e=>{const n={
className:"number",begin:/\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}(:\d{1,5})?/}
;return{name:"Apache config",aliases:["apacheconf"],case_insensitive:!0,
contains:[e.HASH_COMMENT_MODE,{className:"section",begin:/<\/?/,end:/>/,
contains:[n,{className:"number",begin:/:\d{1,5}/
},e.inherit(e.QUOTE_STRING_MODE,{relevance:0})]},{className:"attribute",
begin:/\w+/,relevance:0,keywords:{
nomarkup:"order deny allow setenv rewriterule rewriteengine rewritecond documentroot sethandler errordocument loadmodule options header listen serverroot servername"
},starts:{end:/$/,relevance:0,keywords:{literal:"on off all deny allow"},
contains:[{className:"meta",begin:/\s\[/,end:/\]$/},{className:"variable",
begin:/[\$%]\{/,end:/\}/,contains:["self",{className:"number",begin:/[$%]\d+/}]
},n,{className:"number",begin:/\d+/},e.QUOTE_STRING_MODE]}}],illegal:/\S/}}
})());hljs.registerLanguage("ruby",(()=>{"use strict";function e(...e){
return e.map((e=>{return(n=e)?"string"==typeof n?n:n.source:null;var n
})).join("")}return n=>{
var a,i="([a-zA-Z_]\\w*[!?=]?|[-+~]@|<<|>>|=~|===?|<=>|[<>]=?|\\*\\*|[-/+%^&*~`|]|\\[\\]=?)",s={
keyword:"and then defined module in return redo if BEGIN retry end for self when next until do begin unless END rescue else break undef not super class case require yield alias while ensure elsif or include attr_reader attr_writer attr_accessor __FILE__",
built_in:"proc lambda",literal:"true false nil"},r={className:"doctag",
begin:"@[A-Za-z]+"},b={begin:"#<",end:">"},t=[n.COMMENT("#","$",{contains:[r]
}),n.COMMENT("^=begin","^=end",{contains:[r],relevance:10
}),n.COMMENT("^__END__","\\n$")],c={className:"subst",begin:/#\{/,end:/\}/,
keywords:s},d={className:"string",contains:[n.BACKSLASH_ESCAPE,c],variants:[{
begin:/'/,end:/'/},{begin:/"/,end:/"/},{begin:/`/,end:/`/},{begin:/%[qQwWx]?\(/,
end:/\)/},{begin:/%[qQwWx]?\[/,end:/\]/},{begin:/%[qQwWx]?\{/,end:/\}/},{
begin:/%[qQwWx]?</,end:/>/},{begin:/%[qQwWx]?\//,end:/\//},{begin:/%[qQwWx]?%/,
end:/%/},{begin:/%[qQwWx]?-/,end:/-/},{begin:/%[qQwWx]?\|/,end:/\|/},{
begin:/\B\?(\\\d{1,3}|\\x[A-Fa-f0-9]{1,2}|\\u[A-Fa-f0-9]{4}|\\?\S)\b/},{
begin:/<<[-~]?'?(\w+)\n(?:[^\n]*\n)*?\s*\1\b/,returnBegin:!0,contains:[{
begin:/<<[-~]?'?/},n.END_SAME_AS_BEGIN({begin:/(\w+)/,end:/(\w+)/,
contains:[n.BACKSLASH_ESCAPE,c]})]}]},g="[0-9](_?[0-9])*",l={className:"number",
relevance:0,variants:[{
begin:`\\b([1-9](_?[0-9])*|0)(\\.(${g}))?([eE][+-]?(${g})|r)?i?\\b`},{
begin:"\\b0[dD][0-9](_?[0-9])*r?i?\\b"},{begin:"\\b0[bB][0-1](_?[0-1])*r?i?\\b"
},{begin:"\\b0[oO][0-7](_?[0-7])*r?i?\\b"},{
begin:"\\b0[xX][0-9a-fA-F](_?[0-9a-fA-F])*r?i?\\b"},{
begin:"\\b0(_?[0-7])+r?i?\\b"}]},o={className:"params",begin:"\\(",end:"\\)",
endsParent:!0,keywords:s},_=[d,{className:"class",beginKeywords:"class module",
end:"$|;",illegal:/=/,contains:[n.inherit(n.TITLE_MODE,{
begin:"[A-Za-z_]\\w*(::\\w+)*(\\?|!)?"}),{begin:"<\\s*",contains:[{
begin:"("+n.IDENT_RE+"::)?"+n.IDENT_RE}]}].concat(t)},{className:"function",
begin:e(/def\s*/,(a=i+"\\s*(\\(|;|$)",e("(?=",a,")"))),keywords:"def",end:"$|;",
contains:[n.inherit(n.TITLE_MODE,{begin:i}),o].concat(t)},{begin:n.IDENT_RE+"::"
},{className:"symbol",begin:n.UNDERSCORE_IDENT_RE+"(!|\\?)?:",relevance:0},{
className:"symbol",begin:":(?!\\s)",contains:[d,{begin:i}],relevance:0},l,{
className:"variable",
begin:"(\\$\\W)|((\\$|@@?)(\\w+))(?=[^@$?])(?![A-Za-z])(?![@$?'])"},{
className:"params",begin:/\|/,end:/\|/,relevance:0,keywords:s},{
begin:"("+n.RE_STARTERS_RE+"|unless)\\s*",keywords:"unless",contains:[{
className:"regexp",contains:[n.BACKSLASH_ESCAPE,c],illegal:/\n/,variants:[{
begin:"/",end:"/[a-z]*"},{begin:/%r\{/,end:/\}[a-z]*/},{begin:"%r\\(",
end:"\\)[a-z]*"},{begin:"%r!",end:"![a-z]*"},{begin:"%r\\[",end:"\\][a-z]*"}]
}].concat(b,t),relevance:0}].concat(b,t);c.contains=_,o.contains=_;var E=[{
begin:/^\s*=>/,starts:{end:"$",contains:_}},{className:"meta",
begin:"^([>?]>|[\\w#]+\\(\\w+\\):\\d+:\\d+>|(\\w+-)?\\d+\\.\\d+\\.\\d+(p\\d+)?[^\\d][^>]+>)(?=[ ])",
starts:{end:"$",contains:_}}];return t.unshift(b),{name:"Ruby",
aliases:["rb","gemspec","podspec","thor","irb"],keywords:s,illegal:/\/\*/,
contains:[n.SHEBANG({binary:"ruby"})].concat(E).concat(t).concat(_)}}})());hljs.registerLanguage("properties",(()=>{"use strict";return e=>{
var n="[ \\t\\f]*",a=n+"[:=]"+n,t="("+a+"|[ \\t\\f]+)",r="([^\\\\\\W:= \\t\\f\\n]|\\\\.)+",s="([^\\\\:= \\t\\f\\n]|\\\\.)+",i={
end:t,relevance:0,starts:{className:"string",end:/$/,relevance:0,contains:[{
begin:"\\\\\\\\"},{begin:"\\\\\\n"}]}};return{name:".properties",
case_insensitive:!0,illegal:/\S/,contains:[e.COMMENT("^\\s*[!#]","$"),{
returnBegin:!0,variants:[{begin:r+a,relevance:1},{begin:r+"[ \\t\\f]+",
relevance:0}],contains:[{className:"attr",begin:r,endsParent:!0,relevance:0}],
starts:i},{begin:s+t,returnBegin:!0,relevance:0,contains:[{className:"meta",
begin:s,endsParent:!0,relevance:0}],starts:i},{className:"attr",relevance:0,
begin:s+n+"$"}]}}})());hljs.registerLanguage("rust",(()=>{"use strict";return e=>{
const n="([ui](8|16|32|64|128|size)|f(32|64))?",t="drop i8 i16 i32 i64 i128 isize u8 u16 u32 u64 u128 usize f32 f64 str char bool Box Option Result String Vec Copy Send Sized Sync Drop Fn FnMut FnOnce ToOwned Clone Debug PartialEq PartialOrd Eq Ord AsRef AsMut Into From Default Iterator Extend IntoIterator DoubleEndedIterator ExactSizeIterator SliceConcatExt ToString assert! assert_eq! bitflags! bytes! cfg! col! concat! concat_idents! debug_assert! debug_assert_eq! env! panic! file! format! format_args! include_bin! include_str! line! local_data_key! module_path! option_env! print! println! select! stringify! try! unimplemented! unreachable! vec! write! writeln! macro_rules! assert_ne! debug_assert_ne!"
;return{name:"Rust",aliases:["rs"],keywords:{$pattern:e.IDENT_RE+"!?",
keyword:"abstract as async await become box break const continue crate do dyn else enum extern false final fn for if impl in let loop macro match mod move mut override priv pub ref return self Self static struct super trait true try type typeof unsafe unsized use virtual where while yield",
literal:"true false Some None Ok Err",built_in:t},illegal:"</",
contains:[e.C_LINE_COMMENT_MODE,e.COMMENT("/\\*","\\*/",{contains:["self"]
}),e.inherit(e.QUOTE_STRING_MODE,{begin:/b?"/,illegal:null}),{
className:"string",variants:[{begin:/r(#*)"(.|\n)*?"\1(?!#)/},{
begin:/b?'\\?(x\w{2}|u\w{4}|U\w{8}|.)'/}]},{className:"symbol",
begin:/'[a-zA-Z_][a-zA-Z0-9_]*/},{className:"number",variants:[{
begin:"\\b0b([01_]+)"+n},{begin:"\\b0o([0-7_]+)"+n},{
begin:"\\b0x([A-Fa-f0-9_]+)"+n},{
begin:"\\b(\\d[\\d_]*(\\.[0-9_]+)?([eE][+-]?[0-9_]+)?)"+n}],relevance:0},{
className:"function",beginKeywords:"fn",end:"(\\(|<)",excludeEnd:!0,
contains:[e.UNDERSCORE_TITLE_MODE]},{className:"meta",begin:"#!?\\[",end:"\\]",
contains:[{className:"meta-string",begin:/"/,end:/"/}]},{className:"class",
beginKeywords:"type",end:";",contains:[e.inherit(e.UNDERSCORE_TITLE_MODE,{
endsParent:!0})],illegal:"\\S"},{className:"class",
beginKeywords:"trait enum struct union",end:/\{/,
contains:[e.inherit(e.UNDERSCORE_TITLE_MODE,{endsParent:!0})],illegal:"[\\w\\d]"
},{begin:e.IDENT_RE+"::",keywords:{built_in:t}},{begin:"->"}]}}})());hljs.registerLanguage("yaml",(()=>{"use strict";return e=>{
var n="true false yes no null",a="[\\w#;/?:@&=+$,.~*'()[\\]]+",s={
className:"string",relevance:0,variants:[{begin:/'/,end:/'/},{begin:/"/,end:/"/
},{begin:/\S+/}],contains:[e.BACKSLASH_ESCAPE,{className:"template-variable",
variants:[{begin:/\{\{/,end:/\}\}/},{begin:/%\{/,end:/\}/}]}]},i=e.inherit(s,{
variants:[{begin:/'/,end:/'/},{begin:/"/,end:/"/},{begin:/[^\s,{}[\]]+/}]}),l={
end:",",endsWithParent:!0,excludeEnd:!0,contains:[],keywords:n,relevance:0},t={
begin:/\{/,end:/\}/,contains:[l],illegal:"\\n",relevance:0},g={begin:"\\[",
end:"\\]",contains:[l],illegal:"\\n",relevance:0},b=[{className:"attr",
variants:[{begin:"\\w[\\w :\\/.-]*:(?=[ \t]|$)"},{
begin:'"\\w[\\w :\\/.-]*":(?=[ \t]|$)'},{begin:"'\\w[\\w :\\/.-]*':(?=[ \t]|$)"
}]},{className:"meta",begin:"^---\\s*$",relevance:10},{className:"string",
begin:"[\\|>]([1-9]?[+-])?[ ]*\\n( +)[^ ][^\\n]*\\n(\\2[^\\n]+\\n?)*"},{
begin:"<%[%=-]?",end:"[%-]?%>",subLanguage:"ruby",excludeBegin:!0,excludeEnd:!0,
relevance:0},{className:"type",begin:"!\\w+!"+a},{className:"type",
begin:"!<"+a+">"},{className:"type",begin:"!"+a},{className:"type",begin:"!!"+a
},{className:"meta",begin:"&"+e.UNDERSCORE_IDENT_RE+"$"},{className:"meta",
begin:"\\*"+e.UNDERSCORE_IDENT_RE+"$"},{className:"bullet",begin:"-(?=[ ]|$)",
relevance:0},e.HASH_COMMENT_MODE,{beginKeywords:n,keywords:{literal:n}},{
className:"number",
begin:"\\b[0-9]{4}(-[0-9][0-9]){0,2}([Tt \\t][0-9][0-9]?(:[0-9][0-9]){2})?(\\.[0-9]*)?([ \\t])*(Z|[-+][0-9][0-9]?(:[0-9][0-9])?)?\\b"
},{className:"number",begin:e.C_NUMBER_RE+"\\b",relevance:0},t,g,s],r=[...b]
;return r.pop(),r.push(i),l.contains=r,{name:"YAML",case_insensitive:!0,
aliases:["yml","YAML"],contains:b}}})());hljs.registerLanguage("go",(()=>{"use strict";return e=>{const n={
keyword:"break default func interface select case map struct chan else goto package switch const fallthrough if range type continue for import return var go defer bool byte complex64 complex128 float32 float64 int8 int16 int32 int64 string uint8 uint16 uint32 uint64 int uint uintptr rune",
literal:"true false iota nil",
built_in:"append cap close complex copy imag len make new panic print println real recover delete"
};return{name:"Go",aliases:["golang"],keywords:n,illegal:"</",
contains:[e.C_LINE_COMMENT_MODE,e.C_BLOCK_COMMENT_MODE,{className:"string",
variants:[e.QUOTE_STRING_MODE,e.APOS_STRING_MODE,{begin:"`",end:"`"}]},{
className:"number",variants:[{begin:e.C_NUMBER_RE+"[i]",relevance:1
},e.C_NUMBER_MODE]},{begin:/:=/},{className:"function",beginKeywords:"func",
end:"\\s*(\\{|$)",excludeEnd:!0,contains:[e.TITLE_MODE,{className:"params",
begin:/\(/,end:/\)/,keywords:n,illegal:/["']/}]}]}}})());hljs.registerLanguage("cpp",(()=>{"use strict";function e(e){
return((...e)=>e.map((e=>(e=>e?"string"==typeof e?e:e.source:null)(e))).join(""))("(",e,")?")
}return t=>{const n=(t=>{const n=t.COMMENT("//","$",{contains:[{begin:/\\\n/}]
}),r="[a-zA-Z_]\\w*::",a="(decltype\\(auto\\)|"+e(r)+"[a-zA-Z_]\\w*"+e("<[^<>]+>")+")",s={
className:"keyword",begin:"\\b[a-z\\d_]*_t\\b"},i={className:"string",
variants:[{begin:'(u8?|U|L)?"',end:'"',illegal:"\\n",
contains:[t.BACKSLASH_ESCAPE]},{
begin:"(u8?|U|L)?'(\\\\(x[0-9A-Fa-f]{2}|u[0-9A-Fa-f]{4,8}|[0-7]{3}|\\S)|.)",
end:"'",illegal:"."},t.END_SAME_AS_BEGIN({
begin:/(?:u8?|U|L)?R"([^()\\ ]{0,16})\(/,end:/\)([^()\\ ]{0,16})"/})]},c={
className:"number",variants:[{begin:"\\b(0b[01']+)"},{
begin:"(-?)\\b([\\d']+(\\.[\\d']*)?|\\.[\\d']+)((ll|LL|l|L)(u|U)?|(u|U)(ll|LL|l|L)?|f|F|b|B)"
},{
begin:"(-?)(\\b0[xX][a-fA-F0-9']+|(\\b[\\d']+(\\.[\\d']*)?|\\.[\\d']+)([eE][-+]?[\\d']+)?)"
}],relevance:0},o={className:"meta",begin:/#\s*[a-z]+\b/,end:/$/,keywords:{
"meta-keyword":"if else elif endif define undef warning error line pragma _Pragma ifdef ifndef include"
},contains:[{begin:/\\\n/,relevance:0},t.inherit(i,{className:"meta-string"}),{
className:"meta-string",begin:/<.*?>/,end:/$/,illegal:"\\n"
},n,t.C_BLOCK_COMMENT_MODE]},l={className:"title",begin:e(r)+t.IDENT_RE,
relevance:0},d=e(r)+t.IDENT_RE+"\\s*\\(",u={
keyword:"int float while private char char8_t char16_t char32_t catch import module export virtual operator sizeof dynamic_cast|10 typedef const_cast|10 const for static_cast|10 union namespace unsigned long volatile static protected bool template mutable if public friend do goto auto void enum else break extern using asm case typeid wchar_t short reinterpret_cast|10 default double register explicit signed typename try this switch continue inline delete alignas alignof constexpr consteval constinit decltype concept co_await co_return co_yield requires noexcept static_assert thread_local restrict final override atomic_bool atomic_char atomic_schar atomic_uchar atomic_short atomic_ushort atomic_int atomic_uint atomic_long atomic_ulong atomic_llong atomic_ullong new throw return and and_eq bitand bitor compl not not_eq or or_eq xor xor_eq",
built_in:"std string wstring cin cout cerr clog stdin stdout stderr stringstream istringstream ostringstream auto_ptr deque list queue stack vector map set pair bitset multiset multimap unordered_set unordered_map unordered_multiset unordered_multimap priority_queue make_pair array shared_ptr abort terminate abs acos asin atan2 atan calloc ceil cosh cos exit exp fabs floor fmod fprintf fputs free frexp fscanf future isalnum isalpha iscntrl isdigit isgraph islower isprint ispunct isspace isupper isxdigit tolower toupper labs ldexp log10 log malloc realloc memchr memcmp memcpy memset modf pow printf putchar puts scanf sinh sin snprintf sprintf sqrt sscanf strcat strchr strcmp strcpy strcspn strlen strncat strncmp strncpy strpbrk strrchr strspn strstr tanh tan vfprintf vprintf vsprintf endl initializer_list unique_ptr _Bool complex _Complex imaginary _Imaginary",
literal:"true false nullptr NULL"},p=[o,s,n,t.C_BLOCK_COMMENT_MODE,c,i],m={
variants:[{begin:/=/,end:/;/},{begin:/\(/,end:/\)/},{
beginKeywords:"new throw return else",end:/;/}],keywords:u,contains:p.concat([{
begin:/\(/,end:/\)/,keywords:u,contains:p.concat(["self"]),relevance:0}]),
relevance:0},_={className:"function",begin:"("+a+"[\\*&\\s]+)+"+d,
returnBegin:!0,end:/[{;=]/,excludeEnd:!0,keywords:u,illegal:/[^\w\s\*&:<>.]/,
contains:[{begin:"decltype\\(auto\\)",keywords:u,relevance:0},{begin:d,
returnBegin:!0,contains:[l],relevance:0},{className:"params",begin:/\(/,
end:/\)/,keywords:u,relevance:0,contains:[n,t.C_BLOCK_COMMENT_MODE,i,c,s,{
begin:/\(/,end:/\)/,keywords:u,relevance:0,
contains:["self",n,t.C_BLOCK_COMMENT_MODE,i,c,s]}]
},s,n,t.C_BLOCK_COMMENT_MODE,o]};return{
aliases:["c","cc","h","c++","h++","hpp","hh","hxx","cxx"],keywords:u,
disableAutodetect:!0,illegal:"</",contains:[].concat(m,_,p,[o,{
begin:"\\b(deque|list|queue|priority_queue|pair|stack|vector|map|set|bitset|multiset|multimap|unordered_map|unordered_set|unordered_multiset|unordered_multimap|array)\\s*<",
end:">",keywords:u,contains:["self",s]},{begin:t.IDENT_RE+"::",keywords:u},{
className:"class",beginKeywords:"enum class struct union",end:/[{;:<>=]/,
contains:[{beginKeywords:"final class struct"},t.TITLE_MODE]}]),exports:{
preprocessor:o,strings:i,keywords:u}}})(t)
;return n.disableAutodetect=!1,n.name="C++",
n.aliases=["cc","c++","h++","hpp","hh","hxx","cxx"],n}})());hljs.registerLanguage("makefile",(()=>{"use strict";return e=>{const i={
className:"variable",variants:[{begin:"\\$\\("+e.UNDERSCORE_IDENT_RE+"\\)",
contains:[e.BACKSLASH_ESCAPE]},{begin:/\$[@%<?\^\+\*]/}]},a={className:"string",
begin:/"/,end:/"/,contains:[e.BACKSLASH_ESCAPE,i]},n={className:"variable",
begin:/\$\([\w-]+\s/,end:/\)/,keywords:{
built_in:"subst patsubst strip findstring filter filter-out sort word wordlist firstword lastword dir notdir suffix basename addsuffix addprefix join wildcard realpath abspath error warning shell origin flavor foreach if or and call eval file value"
},contains:[i]},s={begin:"^"+e.UNDERSCORE_IDENT_RE+"\\s*(?=[:+?]?=)"},r={
className:"section",begin:/^[^\s]+:/,end:/$/,contains:[i]};return{
name:"Makefile",aliases:["mk","mak","make"],keywords:{$pattern:/[\w-]+/,
keyword:"define endef undefine ifdef ifndef ifeq ifneq else endif include -include sinclude override export unexport private vpath"
},contains:[e.HASH_COMMENT_MODE,i,a,n,s,{className:"meta",begin:/^\.PHONY:/,
end:/$/,keywords:{$pattern:/[\.\w]+/,"meta-keyword":".PHONY"}},r]}}})());hljs.registerLanguage("bnf",(()=>{"use strict";return e=>({
name:"Backus\u2013Naur Form",contains:[{className:"attribute",begin:/</,end:/>/
},{begin:/::=/,end:/$/,contains:[{begin:/</,end:/>/
},e.C_LINE_COMMENT_MODE,e.C_BLOCK_COMMENT_MODE,e.APOS_STRING_MODE,e.QUOTE_STRING_MODE]
}]})})());hljs.registerLanguage("perl",(()=>{"use strict";function e(...e){
return e.map((e=>{return(n=e)?"string"==typeof n?n:n.source:null;var n
})).join("")}return n=>{const t=/[dualxmsipn]{0,12}/,s={$pattern:/[\w.]+/,
keyword:"getpwent getservent quotemeta msgrcv scalar kill dbmclose undef lc ma syswrite tr send umask sysopen shmwrite vec qx utime local oct semctl localtime readpipe do return format read sprintf dbmopen pop getpgrp not getpwnam rewinddir qq fileno qw endprotoent wait sethostent bless s|0 opendir continue each sleep endgrent shutdown dump chomp connect getsockname die socketpair close flock exists index shmget sub for endpwent redo lstat msgctl setpgrp abs exit select print ref gethostbyaddr unshift fcntl syscall goto getnetbyaddr join gmtime symlink semget splice x|0 getpeername recv log setsockopt cos last reverse gethostbyname getgrnam study formline endhostent times chop length gethostent getnetent pack getprotoent getservbyname rand mkdir pos chmod y|0 substr endnetent printf next open msgsnd readdir use unlink getsockopt getpriority rindex wantarray hex system getservbyport endservent int chr untie rmdir prototype tell listen fork shmread ucfirst setprotoent else sysseek link getgrgid shmctl waitpid unpack getnetbyname reset chdir grep split require caller lcfirst until warn while values shift telldir getpwuid my getprotobynumber delete and sort uc defined srand accept package seekdir getprotobyname semop our rename seek if q|0 chroot sysread setpwent no crypt getc chown sqrt write setnetent setpriority foreach tie sin msgget map stat getlogin unless elsif truncate exec keys glob tied closedir ioctl socket readlink eval xor readline binmode setservent eof ord bind alarm pipe atan2 getgrent exp time push setgrent gt lt or ne m|0 break given say state when"
},r={className:"subst",begin:"[$@]\\{",end:"\\}",keywords:s},i={begin:/->\{/,
end:/\}/},a={variants:[{begin:/\$\d/},{
begin:e(/[$%@](\^\w\b|#\w+(::\w+)*|\{\w+\}|\w+(::\w*)*)/,"(?![A-Za-z])(?![@$%])")
},{begin:/[$%@][^\s\w{]/,relevance:0}]
},o=[n.BACKSLASH_ESCAPE,r,a],c=[a,n.HASH_COMMENT_MODE,n.COMMENT(/^=\w/,/=cut/,{
endsWithParent:!0}),i,{className:"string",contains:o,variants:[{
begin:"q[qwxr]?\\s*\\(",end:"\\)",relevance:5},{begin:"q[qwxr]?\\s*\\[",
end:"\\]",relevance:5},{begin:"q[qwxr]?\\s*\\{",end:"\\}",relevance:5},{
begin:"q[qwxr]?\\s*\\|",end:"\\|",relevance:5},{begin:"q[qwxr]?\\s*<",end:">",
relevance:5},{begin:"qw\\s+q",end:"q",relevance:5},{begin:"'",end:"'",
contains:[n.BACKSLASH_ESCAPE]},{begin:'"',end:'"'},{begin:"`",end:"`",
contains:[n.BACKSLASH_ESCAPE]},{begin:/\{\w+\}/,contains:[],relevance:0},{
begin:"-?\\w+\\s*=>",contains:[],relevance:0}]},{className:"number",
begin:"(\\b0[0-7_]+)|(\\b0x[0-9a-fA-F_]+)|(\\b[1-9][0-9_]*(\\.[0-9_]+)?)|[0_]\\b",
relevance:0},{
begin:"(\\/\\/|"+n.RE_STARTERS_RE+"|\\b(split|return|print|reverse|grep)\\b)\\s*",
keywords:"split return print reverse grep",relevance:0,
contains:[n.HASH_COMMENT_MODE,{className:"regexp",
begin:e(/(s|tr|y)/,/\//,/(\\.|[^\\\/])*/,/\//,/(\\.|[^\\\/])*/,/\//,t),
relevance:10},{className:"regexp",begin:/(m|qr)?\//,end:e(/\//,t),
contains:[n.BACKSLASH_ESCAPE],relevance:0}]},{className:"function",
beginKeywords:"sub",end:"(\\s*\\(.*?\\))?[;{]",excludeEnd:!0,relevance:5,
contains:[n.TITLE_MODE]},{begin:"-\\w\\b",relevance:0},{begin:"^__DATA__$",
end:"^__END__$",subLanguage:"mojolicious",contains:[{begin:"^@@.*",end:"$",
className:"comment"}]}];return r.contains=c,i.contains=c,{name:"Perl",
aliases:["pl","pm"],keywords:s,contains:c}}})());hljs.registerLanguage("smalltalk",(()=>{"use strict";return e=>{
const n="[a-z][a-zA-Z0-9_]*",a={className:"string",begin:"\\$.{1}"},s={
className:"symbol",begin:"#"+e.UNDERSCORE_IDENT_RE};return{name:"Smalltalk",
aliases:["st"],keywords:"self super nil true false thisContext",
contains:[e.COMMENT('"','"'),e.APOS_STRING_MODE,{className:"type",
begin:"\\b[A-Z][A-Za-z0-9_]*",relevance:0},{begin:n+":",relevance:0
},e.C_NUMBER_MODE,s,a,{begin:"\\|[ ]*"+n+"([ ]+"+n+")*[ ]*\\|",returnBegin:!0,
end:/\|/,illegal:/\S/,contains:[{begin:"(\\|[ ]*)?"+n}]},{begin:"#\\(",
end:"\\)",contains:[e.APOS_STRING_MODE,a,e.C_NUMBER_MODE,s]}]}}})());hljs.registerLanguage("mathematica",(()=>{"use strict"
;const e=["AASTriangle","AbelianGroup","Abort","AbortKernels","AbortProtect","AbortScheduledTask","Above","Abs","AbsArg","AbsArgPlot","Absolute","AbsoluteCorrelation","AbsoluteCorrelationFunction","AbsoluteCurrentValue","AbsoluteDashing","AbsoluteFileName","AbsoluteOptions","AbsolutePointSize","AbsoluteThickness","AbsoluteTime","AbsoluteTiming","AcceptanceThreshold","AccountingForm","Accumulate","Accuracy","AccuracyGoal","ActionDelay","ActionMenu","ActionMenuBox","ActionMenuBoxOptions","Activate","Active","ActiveClassification","ActiveClassificationObject","ActiveItem","ActivePrediction","ActivePredictionObject","ActiveStyle","AcyclicGraphQ","AddOnHelpPath","AddSides","AddTo","AddToSearchIndex","AddUsers","AdjacencyGraph","AdjacencyList","AdjacencyMatrix","AdjacentMeshCells","AdjustmentBox","AdjustmentBoxOptions","AdjustTimeSeriesForecast","AdministrativeDivisionData","AffineHalfSpace","AffineSpace","AffineStateSpaceModel","AffineTransform","After","AggregatedEntityClass","AggregationLayer","AircraftData","AirportData","AirPressureData","AirTemperatureData","AiryAi","AiryAiPrime","AiryAiZero","AiryBi","AiryBiPrime","AiryBiZero","AlgebraicIntegerQ","AlgebraicNumber","AlgebraicNumberDenominator","AlgebraicNumberNorm","AlgebraicNumberPolynomial","AlgebraicNumberTrace","AlgebraicRules","AlgebraicRulesData","Algebraics","AlgebraicUnitQ","Alignment","AlignmentMarker","AlignmentPoint","All","AllowAdultContent","AllowedCloudExtraParameters","AllowedCloudParameterExtensions","AllowedDimensions","AllowedFrequencyRange","AllowedHeads","AllowGroupClose","AllowIncomplete","AllowInlineCells","AllowKernelInitialization","AllowLooseGrammar","AllowReverseGroupClose","AllowScriptLevelChange","AllowVersionUpdate","AllTrue","Alphabet","AlphabeticOrder","AlphabeticSort","AlphaChannel","AlternateImage","AlternatingFactorial","AlternatingGroup","AlternativeHypothesis","Alternatives","AltitudeMethod","AmbientLight","AmbiguityFunction","AmbiguityList","Analytic","AnatomyData","AnatomyForm","AnatomyPlot3D","AnatomySkinStyle","AnatomyStyling","AnchoredSearch","And","AndersonDarlingTest","AngerJ","AngleBisector","AngleBracket","AnglePath","AnglePath3D","AngleVector","AngularGauge","Animate","AnimationCycleOffset","AnimationCycleRepetitions","AnimationDirection","AnimationDisplayTime","AnimationRate","AnimationRepetitions","AnimationRunning","AnimationRunTime","AnimationTimeIndex","Animator","AnimatorBox","AnimatorBoxOptions","AnimatorElements","Annotate","Annotation","AnnotationDelete","AnnotationKeys","AnnotationRules","AnnotationValue","Annuity","AnnuityDue","Annulus","AnomalyDetection","AnomalyDetector","AnomalyDetectorFunction","Anonymous","Antialiasing","AntihermitianMatrixQ","Antisymmetric","AntisymmetricMatrixQ","Antonyms","AnyOrder","AnySubset","AnyTrue","Apart","ApartSquareFree","APIFunction","Appearance","AppearanceElements","AppearanceRules","AppellF1","Append","AppendCheck","AppendLayer","AppendTo","Apply","ApplySides","ArcCos","ArcCosh","ArcCot","ArcCoth","ArcCsc","ArcCsch","ArcCurvature","ARCHProcess","ArcLength","ArcSec","ArcSech","ArcSin","ArcSinDistribution","ArcSinh","ArcTan","ArcTanh","Area","Arg","ArgMax","ArgMin","ArgumentCountQ","ARIMAProcess","ArithmeticGeometricMean","ARMAProcess","Around","AroundReplace","ARProcess","Array","ArrayComponents","ArrayDepth","ArrayFilter","ArrayFlatten","ArrayMesh","ArrayPad","ArrayPlot","ArrayQ","ArrayResample","ArrayReshape","ArrayRules","Arrays","Arrow","Arrow3DBox","ArrowBox","Arrowheads","ASATriangle","Ask","AskAppend","AskConfirm","AskDisplay","AskedQ","AskedValue","AskFunction","AskState","AskTemplateDisplay","AspectRatio","AspectRatioFixed","Assert","AssociateTo","Association","AssociationFormat","AssociationMap","AssociationQ","AssociationThread","AssumeDeterministic","Assuming","Assumptions","AstronomicalData","Asymptotic","AsymptoticDSolveValue","AsymptoticEqual","AsymptoticEquivalent","AsymptoticGreater","AsymptoticGreaterEqual","AsymptoticIntegrate","AsymptoticLess","AsymptoticLessEqual","AsymptoticOutputTracker","AsymptoticProduct","AsymptoticRSolveValue","AsymptoticSolve","AsymptoticSum","Asynchronous","AsynchronousTaskObject","AsynchronousTasks","Atom","AtomCoordinates","AtomCount","AtomDiagramCoordinates","AtomList","AtomQ","AttentionLayer","Attributes","Audio","AudioAmplify","AudioAnnotate","AudioAnnotationLookup","AudioBlockMap","AudioCapture","AudioChannelAssignment","AudioChannelCombine","AudioChannelMix","AudioChannels","AudioChannelSeparate","AudioData","AudioDelay","AudioDelete","AudioDevice","AudioDistance","AudioEncoding","AudioFade","AudioFrequencyShift","AudioGenerator","AudioIdentify","AudioInputDevice","AudioInsert","AudioInstanceQ","AudioIntervals","AudioJoin","AudioLabel","AudioLength","AudioLocalMeasurements","AudioLooping","AudioLoudness","AudioMeasurements","AudioNormalize","AudioOutputDevice","AudioOverlay","AudioPad","AudioPan","AudioPartition","AudioPause","AudioPitchShift","AudioPlay","AudioPlot","AudioQ","AudioRecord","AudioReplace","AudioResample","AudioReverb","AudioReverse","AudioSampleRate","AudioSpectralMap","AudioSpectralTransformation","AudioSplit","AudioStop","AudioStream","AudioStreams","AudioTimeStretch","AudioTracks","AudioTrim","AudioType","AugmentedPolyhedron","AugmentedSymmetricPolynomial","Authenticate","Authentication","AuthenticationDialog","AutoAction","Autocomplete","AutocompletionFunction","AutoCopy","AutocorrelationTest","AutoDelete","AutoEvaluateEvents","AutoGeneratedPackage","AutoIndent","AutoIndentSpacings","AutoItalicWords","AutoloadPath","AutoMatch","Automatic","AutomaticImageSize","AutoMultiplicationSymbol","AutoNumberFormatting","AutoOpenNotebooks","AutoOpenPalettes","AutoQuoteCharacters","AutoRefreshed","AutoRemove","AutorunSequencing","AutoScaling","AutoScroll","AutoSpacing","AutoStyleOptions","AutoStyleWords","AutoSubmitting","Axes","AxesEdge","AxesLabel","AxesOrigin","AxesStyle","AxiomaticTheory","Axis","BabyMonsterGroupB","Back","Background","BackgroundAppearance","BackgroundTasksSettings","Backslash","Backsubstitution","Backward","Ball","Band","BandpassFilter","BandstopFilter","BarabasiAlbertGraphDistribution","BarChart","BarChart3D","BarcodeImage","BarcodeRecognize","BaringhausHenzeTest","BarLegend","BarlowProschanImportance","BarnesG","BarOrigin","BarSpacing","BartlettHannWindow","BartlettWindow","BaseDecode","BaseEncode","BaseForm","Baseline","BaselinePosition","BaseStyle","BasicRecurrentLayer","BatchNormalizationLayer","BatchSize","BatesDistribution","BattleLemarieWavelet","BayesianMaximization","BayesianMaximizationObject","BayesianMinimization","BayesianMinimizationObject","Because","BeckmannDistribution","Beep","Before","Begin","BeginDialogPacket","BeginFrontEndInteractionPacket","BeginPackage","BellB","BellY","Below","BenfordDistribution","BeniniDistribution","BenktanderGibratDistribution","BenktanderWeibullDistribution","BernoulliB","BernoulliDistribution","BernoulliGraphDistribution","BernoulliProcess","BernsteinBasis","BesselFilterModel","BesselI","BesselJ","BesselJZero","BesselK","BesselY","BesselYZero","Beta","BetaBinomialDistribution","BetaDistribution","BetaNegativeBinomialDistribution","BetaPrimeDistribution","BetaRegularized","Between","BetweennessCentrality","BeveledPolyhedron","BezierCurve","BezierCurve3DBox","BezierCurve3DBoxOptions","BezierCurveBox","BezierCurveBoxOptions","BezierFunction","BilateralFilter","Binarize","BinaryDeserialize","BinaryDistance","BinaryFormat","BinaryImageQ","BinaryRead","BinaryReadList","BinarySerialize","BinaryWrite","BinCounts","BinLists","Binomial","BinomialDistribution","BinomialProcess","BinormalDistribution","BiorthogonalSplineWavelet","BipartiteGraphQ","BiquadraticFilterModel","BirnbaumImportance","BirnbaumSaundersDistribution","BitAnd","BitClear","BitGet","BitLength","BitNot","BitOr","BitSet","BitShiftLeft","BitShiftRight","BitXor","BiweightLocation","BiweightMidvariance","Black","BlackmanHarrisWindow","BlackmanNuttallWindow","BlackmanWindow","Blank","BlankForm","BlankNullSequence","BlankSequence","Blend","Block","BlockchainAddressData","BlockchainBase","BlockchainBlockData","BlockchainContractValue","BlockchainData","BlockchainGet","BlockchainKeyEncode","BlockchainPut","BlockchainTokenData","BlockchainTransaction","BlockchainTransactionData","BlockchainTransactionSign","BlockchainTransactionSubmit","BlockMap","BlockRandom","BlomqvistBeta","BlomqvistBetaTest","Blue","Blur","BodePlot","BohmanWindow","Bold","Bond","BondCount","BondList","BondQ","Bookmarks","Boole","BooleanConsecutiveFunction","BooleanConvert","BooleanCountingFunction","BooleanFunction","BooleanGraph","BooleanMaxterms","BooleanMinimize","BooleanMinterms","BooleanQ","BooleanRegion","Booleans","BooleanStrings","BooleanTable","BooleanVariables","BorderDimensions","BorelTannerDistribution","Bottom","BottomHatTransform","BoundaryDiscretizeGraphics","BoundaryDiscretizeRegion","BoundaryMesh","BoundaryMeshRegion","BoundaryMeshRegionQ","BoundaryStyle","BoundedRegionQ","BoundingRegion","Bounds","Box","BoxBaselineShift","BoxData","BoxDimensions","Boxed","Boxes","BoxForm","BoxFormFormatTypes","BoxFrame","BoxID","BoxMargins","BoxMatrix","BoxObject","BoxRatios","BoxRotation","BoxRotationPoint","BoxStyle","BoxWhiskerChart","Bra","BracketingBar","BraKet","BrayCurtisDistance","BreadthFirstScan","Break","BridgeData","BrightnessEqualize","BroadcastStationData","Brown","BrownForsytheTest","BrownianBridgeProcess","BrowserCategory","BSplineBasis","BSplineCurve","BSplineCurve3DBox","BSplineCurve3DBoxOptions","BSplineCurveBox","BSplineCurveBoxOptions","BSplineFunction","BSplineSurface","BSplineSurface3DBox","BSplineSurface3DBoxOptions","BubbleChart","BubbleChart3D","BubbleScale","BubbleSizes","BuildingData","BulletGauge","BusinessDayQ","ButterflyGraph","ButterworthFilterModel","Button","ButtonBar","ButtonBox","ButtonBoxOptions","ButtonCell","ButtonContents","ButtonData","ButtonEvaluator","ButtonExpandable","ButtonFrame","ButtonFunction","ButtonMargins","ButtonMinHeight","ButtonNote","ButtonNotebook","ButtonSource","ButtonStyle","ButtonStyleMenuListing","Byte","ByteArray","ByteArrayFormat","ByteArrayQ","ByteArrayToString","ByteCount","ByteOrdering","C","CachedValue","CacheGraphics","CachePersistence","CalendarConvert","CalendarData","CalendarType","Callout","CalloutMarker","CalloutStyle","CallPacket","CanberraDistance","Cancel","CancelButton","CandlestickChart","CanonicalGraph","CanonicalizePolygon","CanonicalizePolyhedron","CanonicalName","CanonicalWarpingCorrespondence","CanonicalWarpingDistance","CantorMesh","CantorStaircase","Cap","CapForm","CapitalDifferentialD","Capitalize","CapsuleShape","CaptureRunning","CardinalBSplineBasis","CarlemanLinearize","CarmichaelLambda","CaseOrdering","Cases","CaseSensitive","Cashflow","Casoratian","Catalan","CatalanNumber","Catch","CategoricalDistribution","Catenate","CatenateLayer","CauchyDistribution","CauchyWindow","CayleyGraph","CDF","CDFDeploy","CDFInformation","CDFWavelet","Ceiling","CelestialSystem","Cell","CellAutoOverwrite","CellBaseline","CellBoundingBox","CellBracketOptions","CellChangeTimes","CellContents","CellContext","CellDingbat","CellDynamicExpression","CellEditDuplicate","CellElementsBoundingBox","CellElementSpacings","CellEpilog","CellEvaluationDuplicate","CellEvaluationFunction","CellEvaluationLanguage","CellEventActions","CellFrame","CellFrameColor","CellFrameLabelMargins","CellFrameLabels","CellFrameMargins","CellGroup","CellGroupData","CellGrouping","CellGroupingRules","CellHorizontalScrolling","CellID","CellLabel","CellLabelAutoDelete","CellLabelMargins","CellLabelPositioning","CellLabelStyle","CellLabelTemplate","CellMargins","CellObject","CellOpen","CellPrint","CellProlog","Cells","CellSize","CellStyle","CellTags","CellularAutomaton","CensoredDistribution","Censoring","Center","CenterArray","CenterDot","CentralFeature","CentralMoment","CentralMomentGeneratingFunction","Cepstrogram","CepstrogramArray","CepstrumArray","CForm","ChampernowneNumber","ChangeOptions","ChannelBase","ChannelBrokerAction","ChannelDatabin","ChannelHistoryLength","ChannelListen","ChannelListener","ChannelListeners","ChannelListenerWait","ChannelObject","ChannelPreSendFunction","ChannelReceiverFunction","ChannelSend","ChannelSubscribers","ChanVeseBinarize","Character","CharacterCounts","CharacterEncoding","CharacterEncodingsPath","CharacteristicFunction","CharacteristicPolynomial","CharacterName","CharacterNormalize","CharacterRange","Characters","ChartBaseStyle","ChartElementData","ChartElementDataFunction","ChartElementFunction","ChartElements","ChartLabels","ChartLayout","ChartLegends","ChartStyle","Chebyshev1FilterModel","Chebyshev2FilterModel","ChebyshevDistance","ChebyshevT","ChebyshevU","Check","CheckAbort","CheckAll","Checkbox","CheckboxBar","CheckboxBox","CheckboxBoxOptions","ChemicalData","ChessboardDistance","ChiDistribution","ChineseRemainder","ChiSquareDistribution","ChoiceButtons","ChoiceDialog","CholeskyDecomposition","Chop","ChromaticityPlot","ChromaticityPlot3D","ChromaticPolynomial","Circle","CircleBox","CircleDot","CircleMinus","CirclePlus","CirclePoints","CircleThrough","CircleTimes","CirculantGraph","CircularOrthogonalMatrixDistribution","CircularQuaternionMatrixDistribution","CircularRealMatrixDistribution","CircularSymplecticMatrixDistribution","CircularUnitaryMatrixDistribution","Circumsphere","CityData","ClassifierFunction","ClassifierInformation","ClassifierMeasurements","ClassifierMeasurementsObject","Classify","ClassPriors","Clear","ClearAll","ClearAttributes","ClearCookies","ClearPermissions","ClearSystemCache","ClebschGordan","ClickPane","Clip","ClipboardNotebook","ClipFill","ClippingStyle","ClipPlanes","ClipPlanesStyle","ClipRange","Clock","ClockGauge","ClockwiseContourIntegral","Close","Closed","CloseKernels","ClosenessCentrality","Closing","ClosingAutoSave","ClosingEvent","ClosingSaveDialog","CloudAccountData","CloudBase","CloudConnect","CloudConnections","CloudDeploy","CloudDirectory","CloudDisconnect","CloudEvaluate","CloudExport","CloudExpression","CloudExpressions","CloudFunction","CloudGet","CloudImport","CloudLoggingData","CloudObject","CloudObjectInformation","CloudObjectInformationData","CloudObjectNameFormat","CloudObjects","CloudObjectURLType","CloudPublish","CloudPut","CloudRenderingMethod","CloudSave","CloudShare","CloudSubmit","CloudSymbol","CloudUnshare","CloudUserID","ClusterClassify","ClusterDissimilarityFunction","ClusteringComponents","ClusteringTree","CMYKColor","Coarse","CodeAssistOptions","Coefficient","CoefficientArrays","CoefficientDomain","CoefficientList","CoefficientRules","CoifletWavelet","Collect","Colon","ColonForm","ColorBalance","ColorCombine","ColorConvert","ColorCoverage","ColorData","ColorDataFunction","ColorDetect","ColorDistance","ColorFunction","ColorFunctionScaling","Colorize","ColorNegate","ColorOutput","ColorProfileData","ColorQ","ColorQuantize","ColorReplace","ColorRules","ColorSelectorSettings","ColorSeparate","ColorSetter","ColorSetterBox","ColorSetterBoxOptions","ColorSlider","ColorsNear","ColorSpace","ColorToneMapping","Column","ColumnAlignments","ColumnBackgrounds","ColumnForm","ColumnLines","ColumnsEqual","ColumnSpacings","ColumnWidths","CombinedEntityClass","CombinerFunction","CometData","CommonDefaultFormatTypes","Commonest","CommonestFilter","CommonName","CommonUnits","CommunityBoundaryStyle","CommunityGraphPlot","CommunityLabels","CommunityRegionStyle","CompanyData","CompatibleUnitQ","CompilationOptions","CompilationTarget","Compile","Compiled","CompiledCodeFunction","CompiledFunction","CompilerOptions","Complement","ComplementedEntityClass","CompleteGraph","CompleteGraphQ","CompleteKaryTree","CompletionsListPacket","Complex","ComplexContourPlot","Complexes","ComplexExpand","ComplexInfinity","ComplexityFunction","ComplexListPlot","ComplexPlot","ComplexPlot3D","ComplexRegionPlot","ComplexStreamPlot","ComplexVectorPlot","ComponentMeasurements","ComponentwiseContextMenu","Compose","ComposeList","ComposeSeries","CompositeQ","Composition","CompoundElement","CompoundExpression","CompoundPoissonDistribution","CompoundPoissonProcess","CompoundRenewalProcess","Compress","CompressedData","CompressionLevel","ComputeUncertainty","Condition","ConditionalExpression","Conditioned","Cone","ConeBox","ConfidenceLevel","ConfidenceRange","ConfidenceTransform","ConfigurationPath","ConformAudio","ConformImages","Congruent","ConicHullRegion","ConicHullRegion3DBox","ConicHullRegionBox","ConicOptimization","Conjugate","ConjugateTranspose","Conjunction","Connect","ConnectedComponents","ConnectedGraphComponents","ConnectedGraphQ","ConnectedMeshComponents","ConnectedMoleculeComponents","ConnectedMoleculeQ","ConnectionSettings","ConnectLibraryCallbackFunction","ConnectSystemModelComponents","ConnesWindow","ConoverTest","ConsoleMessage","ConsoleMessagePacket","Constant","ConstantArray","ConstantArrayLayer","ConstantImage","ConstantPlusLayer","ConstantRegionQ","Constants","ConstantTimesLayer","ConstellationData","ConstrainedMax","ConstrainedMin","Construct","Containing","ContainsAll","ContainsAny","ContainsExactly","ContainsNone","ContainsOnly","ContentFieldOptions","ContentLocationFunction","ContentObject","ContentPadding","ContentsBoundingBox","ContentSelectable","ContentSize","Context","ContextMenu","Contexts","ContextToFileName","Continuation","Continue","ContinuedFraction","ContinuedFractionK","ContinuousAction","ContinuousMarkovProcess","ContinuousTask","ContinuousTimeModelQ","ContinuousWaveletData","ContinuousWaveletTransform","ContourDetect","ContourGraphics","ContourIntegral","ContourLabels","ContourLines","ContourPlot","ContourPlot3D","Contours","ContourShading","ContourSmoothing","ContourStyle","ContraharmonicMean","ContrastiveLossLayer","Control","ControlActive","ControlAlignment","ControlGroupContentsBox","ControllabilityGramian","ControllabilityMatrix","ControllableDecomposition","ControllableModelQ","ControllerDuration","ControllerInformation","ControllerInformationData","ControllerLinking","ControllerManipulate","ControllerMethod","ControllerPath","ControllerState","ControlPlacement","ControlsRendering","ControlType","Convergents","ConversionOptions","ConversionRules","ConvertToBitmapPacket","ConvertToPostScript","ConvertToPostScriptPacket","ConvexHullMesh","ConvexPolygonQ","ConvexPolyhedronQ","ConvolutionLayer","Convolve","ConwayGroupCo1","ConwayGroupCo2","ConwayGroupCo3","CookieFunction","Cookies","CoordinateBoundingBox","CoordinateBoundingBoxArray","CoordinateBounds","CoordinateBoundsArray","CoordinateChartData","CoordinatesToolOptions","CoordinateTransform","CoordinateTransformData","CoprimeQ","Coproduct","CopulaDistribution","Copyable","CopyDatabin","CopyDirectory","CopyFile","CopyTag","CopyToClipboard","CornerFilter","CornerNeighbors","Correlation","CorrelationDistance","CorrelationFunction","CorrelationTest","Cos","Cosh","CoshIntegral","CosineDistance","CosineWindow","CosIntegral","Cot","Coth","Count","CountDistinct","CountDistinctBy","CounterAssignments","CounterBox","CounterBoxOptions","CounterClockwiseContourIntegral","CounterEvaluator","CounterFunction","CounterIncrements","CounterStyle","CounterStyleMenuListing","CountRoots","CountryData","Counts","CountsBy","Covariance","CovarianceEstimatorFunction","CovarianceFunction","CoxianDistribution","CoxIngersollRossProcess","CoxModel","CoxModelFit","CramerVonMisesTest","CreateArchive","CreateCellID","CreateChannel","CreateCloudExpression","CreateDatabin","CreateDataStructure","CreateDataSystemModel","CreateDialog","CreateDirectory","CreateDocument","CreateFile","CreateIntermediateDirectories","CreateManagedLibraryExpression","CreateNotebook","CreatePacletArchive","CreatePalette","CreatePalettePacket","CreatePermissionsGroup","CreateScheduledTask","CreateSearchIndex","CreateSystemModel","CreateTemporary","CreateUUID","CreateWindow","CriterionFunction","CriticalityFailureImportance","CriticalitySuccessImportance","CriticalSection","Cross","CrossEntropyLossLayer","CrossingCount","CrossingDetect","CrossingPolygon","CrossMatrix","Csc","Csch","CTCLossLayer","Cube","CubeRoot","Cubics","Cuboid","CuboidBox","Cumulant","CumulantGeneratingFunction","Cup","CupCap","Curl","CurlyDoubleQuote","CurlyQuote","CurrencyConvert","CurrentDate","CurrentImage","CurrentlySpeakingPacket","CurrentNotebookImage","CurrentScreenImage","CurrentValue","Curry","CurryApplied","CurvatureFlowFilter","CurveClosed","Cyan","CycleGraph","CycleIndexPolynomial","Cycles","CyclicGroup","Cyclotomic","Cylinder","CylinderBox","CylindricalDecomposition","D","DagumDistribution","DamData","DamerauLevenshteinDistance","DampingFactor","Darker","Dashed","Dashing","DatabaseConnect","DatabaseDisconnect","DatabaseReference","Databin","DatabinAdd","DatabinRemove","Databins","DatabinUpload","DataCompression","DataDistribution","DataRange","DataReversed","Dataset","DatasetDisplayPanel","DataStructure","DataStructureQ","Date","DateBounds","Dated","DateDelimiters","DateDifference","DatedUnit","DateFormat","DateFunction","DateHistogram","DateInterval","DateList","DateListLogPlot","DateListPlot","DateListStepPlot","DateObject","DateObjectQ","DateOverlapsQ","DatePattern","DatePlus","DateRange","DateReduction","DateString","DateTicksFormat","DateValue","DateWithinQ","DaubechiesWavelet","DavisDistribution","DawsonF","DayCount","DayCountConvention","DayHemisphere","DaylightQ","DayMatchQ","DayName","DayNightTerminator","DayPlus","DayRange","DayRound","DeBruijnGraph","DeBruijnSequence","Debug","DebugTag","Decapitalize","Decimal","DecimalForm","DeclareKnownSymbols","DeclarePackage","Decompose","DeconvolutionLayer","Decrement","Decrypt","DecryptFile","DedekindEta","DeepSpaceProbeData","Default","DefaultAxesStyle","DefaultBaseStyle","DefaultBoxStyle","DefaultButton","DefaultColor","DefaultControlPlacement","DefaultDuplicateCellStyle","DefaultDuration","DefaultElement","DefaultFaceGridsStyle","DefaultFieldHintStyle","DefaultFont","DefaultFontProperties","DefaultFormatType","DefaultFormatTypeForStyle","DefaultFrameStyle","DefaultFrameTicksStyle","DefaultGridLinesStyle","DefaultInlineFormatType","DefaultInputFormatType","DefaultLabelStyle","DefaultMenuStyle","DefaultNaturalLanguage","DefaultNewCellStyle","DefaultNewInlineCellStyle","DefaultNotebook","DefaultOptions","DefaultOutputFormatType","DefaultPrintPrecision","DefaultStyle","DefaultStyleDefinitions","DefaultTextFormatType","DefaultTextInlineFormatType","DefaultTicksStyle","DefaultTooltipStyle","DefaultValue","DefaultValues","Defer","DefineExternal","DefineInputStreamMethod","DefineOutputStreamMethod","DefineResourceFunction","Definition","Degree","DegreeCentrality","DegreeGraphDistribution","DegreeLexicographic","DegreeReverseLexicographic","DEigensystem","DEigenvalues","Deinitialization","Del","DelaunayMesh","Delayed","Deletable","Delete","DeleteAnomalies","DeleteBorderComponents","DeleteCases","DeleteChannel","DeleteCloudExpression","DeleteContents","DeleteDirectory","DeleteDuplicates","DeleteDuplicatesBy","DeleteFile","DeleteMissing","DeleteObject","DeletePermissionsKey","DeleteSearchIndex","DeleteSmallComponents","DeleteStopwords","DeleteWithContents","DeletionWarning","DelimitedArray","DelimitedSequence","Delimiter","DelimiterFlashTime","DelimiterMatching","Delimiters","DeliveryFunction","Dendrogram","Denominator","DensityGraphics","DensityHistogram","DensityPlot","DensityPlot3D","DependentVariables","Deploy","Deployed","Depth","DepthFirstScan","Derivative","DerivativeFilter","DerivedKey","DescriptorStateSpace","DesignMatrix","DestroyAfterEvaluation","Det","DeviceClose","DeviceConfigure","DeviceExecute","DeviceExecuteAsynchronous","DeviceObject","DeviceOpen","DeviceOpenQ","DeviceRead","DeviceReadBuffer","DeviceReadLatest","DeviceReadList","DeviceReadTimeSeries","Devices","DeviceStreams","DeviceWrite","DeviceWriteBuffer","DGaussianWavelet","DiacriticalPositioning","Diagonal","DiagonalizableMatrixQ","DiagonalMatrix","DiagonalMatrixQ","Dialog","DialogIndent","DialogInput","DialogLevel","DialogNotebook","DialogProlog","DialogReturn","DialogSymbols","Diamond","DiamondMatrix","DiceDissimilarity","DictionaryLookup","DictionaryWordQ","DifferenceDelta","DifferenceOrder","DifferenceQuotient","DifferenceRoot","DifferenceRootReduce","Differences","DifferentialD","DifferentialRoot","DifferentialRootReduce","DifferentiatorFilter","DigitalSignature","DigitBlock","DigitBlockMinimum","DigitCharacter","DigitCount","DigitQ","DihedralAngle","DihedralGroup","Dilation","DimensionalCombinations","DimensionalMeshComponents","DimensionReduce","DimensionReducerFunction","DimensionReduction","Dimensions","DiracComb","DiracDelta","DirectedEdge","DirectedEdges","DirectedGraph","DirectedGraphQ","DirectedInfinity","Direction","Directive","Directory","DirectoryName","DirectoryQ","DirectoryStack","DirichletBeta","DirichletCharacter","DirichletCondition","DirichletConvolve","DirichletDistribution","DirichletEta","DirichletL","DirichletLambda","DirichletTransform","DirichletWindow","DisableConsolePrintPacket","DisableFormatting","DiscreteAsymptotic","DiscreteChirpZTransform","DiscreteConvolve","DiscreteDelta","DiscreteHadamardTransform","DiscreteIndicator","DiscreteLimit","DiscreteLQEstimatorGains","DiscreteLQRegulatorGains","DiscreteLyapunovSolve","DiscreteMarkovProcess","DiscreteMaxLimit","DiscreteMinLimit","DiscretePlot","DiscretePlot3D","DiscreteRatio","DiscreteRiccatiSolve","DiscreteShift","DiscreteTimeModelQ","DiscreteUniformDistribution","DiscreteVariables","DiscreteWaveletData","DiscreteWaveletPacketTransform","DiscreteWaveletTransform","DiscretizeGraphics","DiscretizeRegion","Discriminant","DisjointQ","Disjunction","Disk","DiskBox","DiskMatrix","DiskSegment","Dispatch","DispatchQ","DispersionEstimatorFunction","Display","DisplayAllSteps","DisplayEndPacket","DisplayFlushImagePacket","DisplayForm","DisplayFunction","DisplayPacket","DisplayRules","DisplaySetSizePacket","DisplayString","DisplayTemporary","DisplayWith","DisplayWithRef","DisplayWithVariable","DistanceFunction","DistanceMatrix","DistanceTransform","Distribute","Distributed","DistributedContexts","DistributeDefinitions","DistributionChart","DistributionDomain","DistributionFitTest","DistributionParameterAssumptions","DistributionParameterQ","Dithering","Div","Divergence","Divide","DivideBy","Dividers","DivideSides","Divisible","Divisors","DivisorSigma","DivisorSum","DMSList","DMSString","Do","DockedCells","DocumentGenerator","DocumentGeneratorInformation","DocumentGeneratorInformationData","DocumentGenerators","DocumentNotebook","DocumentWeightingRules","Dodecahedron","DomainRegistrationInformation","DominantColors","DOSTextFormat","Dot","DotDashed","DotEqual","DotLayer","DotPlusLayer","Dotted","DoubleBracketingBar","DoubleContourIntegral","DoubleDownArrow","DoubleLeftArrow","DoubleLeftRightArrow","DoubleLeftTee","DoubleLongLeftArrow","DoubleLongLeftRightArrow","DoubleLongRightArrow","DoubleRightArrow","DoubleRightTee","DoubleUpArrow","DoubleUpDownArrow","DoubleVerticalBar","DoublyInfinite","Down","DownArrow","DownArrowBar","DownArrowUpArrow","DownLeftRightVector","DownLeftTeeVector","DownLeftVector","DownLeftVectorBar","DownRightTeeVector","DownRightVector","DownRightVectorBar","Downsample","DownTee","DownTeeArrow","DownValues","DragAndDrop","DrawEdges","DrawFrontFaces","DrawHighlighted","Drop","DropoutLayer","DSolve","DSolveValue","Dt","DualLinearProgramming","DualPolyhedron","DualSystemsModel","DumpGet","DumpSave","DuplicateFreeQ","Duration","Dynamic","DynamicBox","DynamicBoxOptions","DynamicEvaluationTimeout","DynamicGeoGraphics","DynamicImage","DynamicLocation","DynamicModule","DynamicModuleBox","DynamicModuleBoxOptions","DynamicModuleParent","DynamicModuleValues","DynamicName","DynamicNamespace","DynamicReference","DynamicSetting","DynamicUpdating","DynamicWrapper","DynamicWrapperBox","DynamicWrapperBoxOptions","E","EarthImpactData","EarthquakeData","EccentricityCentrality","Echo","EchoFunction","EclipseType","EdgeAdd","EdgeBetweennessCentrality","EdgeCapacity","EdgeCapForm","EdgeColor","EdgeConnectivity","EdgeContract","EdgeCost","EdgeCount","EdgeCoverQ","EdgeCycleMatrix","EdgeDashing","EdgeDelete","EdgeDetect","EdgeForm","EdgeIndex","EdgeJoinForm","EdgeLabeling","EdgeLabels","EdgeLabelStyle","EdgeList","EdgeOpacity","EdgeQ","EdgeRenderingFunction","EdgeRules","EdgeShapeFunction","EdgeStyle","EdgeTaggedGraph","EdgeTaggedGraphQ","EdgeTags","EdgeThickness","EdgeWeight","EdgeWeightedGraphQ","Editable","EditButtonSettings","EditCellTagsSettings","EditDistance","EffectiveInterest","Eigensystem","Eigenvalues","EigenvectorCentrality","Eigenvectors","Element","ElementData","ElementwiseLayer","ElidedForms","Eliminate","EliminationOrder","Ellipsoid","EllipticE","EllipticExp","EllipticExpPrime","EllipticF","EllipticFilterModel","EllipticK","EllipticLog","EllipticNomeQ","EllipticPi","EllipticReducedHalfPeriods","EllipticTheta","EllipticThetaPrime","EmbedCode","EmbeddedHTML","EmbeddedService","EmbeddingLayer","EmbeddingObject","EmitSound","EmphasizeSyntaxErrors","EmpiricalDistribution","Empty","EmptyGraphQ","EmptyRegion","EnableConsolePrintPacket","Enabled","Encode","Encrypt","EncryptedObject","EncryptFile","End","EndAdd","EndDialogPacket","EndFrontEndInteractionPacket","EndOfBuffer","EndOfFile","EndOfLine","EndOfString","EndPackage","EngineEnvironment","EngineeringForm","Enter","EnterExpressionPacket","EnterTextPacket","Entity","EntityClass","EntityClassList","EntityCopies","EntityFunction","EntityGroup","EntityInstance","EntityList","EntityPrefetch","EntityProperties","EntityProperty","EntityPropertyClass","EntityRegister","EntityStore","EntityStores","EntityTypeName","EntityUnregister","EntityValue","Entropy","EntropyFilter","Environment","Epilog","EpilogFunction","Equal","EqualColumns","EqualRows","EqualTilde","EqualTo","EquatedTo","Equilibrium","EquirippleFilterKernel","Equivalent","Erf","Erfc","Erfi","ErlangB","ErlangC","ErlangDistribution","Erosion","ErrorBox","ErrorBoxOptions","ErrorNorm","ErrorPacket","ErrorsDialogSettings","EscapeRadius","EstimatedBackground","EstimatedDistribution","EstimatedProcess","EstimatorGains","EstimatorRegulator","EuclideanDistance","EulerAngles","EulerCharacteristic","EulerE","EulerGamma","EulerianGraphQ","EulerMatrix","EulerPhi","Evaluatable","Evaluate","Evaluated","EvaluatePacket","EvaluateScheduledTask","EvaluationBox","EvaluationCell","EvaluationCompletionAction","EvaluationData","EvaluationElements","EvaluationEnvironment","EvaluationMode","EvaluationMonitor","EvaluationNotebook","EvaluationObject","EvaluationOrder","Evaluator","EvaluatorNames","EvenQ","EventData","EventEvaluator","EventHandler","EventHandlerTag","EventLabels","EventSeries","ExactBlackmanWindow","ExactNumberQ","ExactRootIsolation","ExampleData","Except","ExcludedForms","ExcludedLines","ExcludedPhysicalQuantities","ExcludePods","Exclusions","ExclusionsStyle","Exists","Exit","ExitDialog","ExoplanetData","Exp","Expand","ExpandAll","ExpandDenominator","ExpandFileName","ExpandNumerator","Expectation","ExpectationE","ExpectedValue","ExpGammaDistribution","ExpIntegralE","ExpIntegralEi","ExpirationDate","Exponent","ExponentFunction","ExponentialDistribution","ExponentialFamily","ExponentialGeneratingFunction","ExponentialMovingAverage","ExponentialPowerDistribution","ExponentPosition","ExponentStep","Export","ExportAutoReplacements","ExportByteArray","ExportForm","ExportPacket","ExportString","Expression","ExpressionCell","ExpressionGraph","ExpressionPacket","ExpressionUUID","ExpToTrig","ExtendedEntityClass","ExtendedGCD","Extension","ExtentElementFunction","ExtentMarkers","ExtentSize","ExternalBundle","ExternalCall","ExternalDataCharacterEncoding","ExternalEvaluate","ExternalFunction","ExternalFunctionName","ExternalIdentifier","ExternalObject","ExternalOptions","ExternalSessionObject","ExternalSessions","ExternalStorageBase","ExternalStorageDownload","ExternalStorageGet","ExternalStorageObject","ExternalStoragePut","ExternalStorageUpload","ExternalTypeSignature","ExternalValue","Extract","ExtractArchive","ExtractLayer","ExtractPacletArchive","ExtremeValueDistribution","FaceAlign","FaceForm","FaceGrids","FaceGridsStyle","FacialFeatures","Factor","FactorComplete","Factorial","Factorial2","FactorialMoment","FactorialMomentGeneratingFunction","FactorialPower","FactorInteger","FactorList","FactorSquareFree","FactorSquareFreeList","FactorTerms","FactorTermsList","Fail","Failure","FailureAction","FailureDistribution","FailureQ","False","FareySequence","FARIMAProcess","FeatureDistance","FeatureExtract","FeatureExtraction","FeatureExtractor","FeatureExtractorFunction","FeatureNames","FeatureNearest","FeatureSpacePlot","FeatureSpacePlot3D","FeatureTypes","FEDisableConsolePrintPacket","FeedbackLinearize","FeedbackSector","FeedbackSectorStyle","FeedbackType","FEEnableConsolePrintPacket","FetalGrowthData","Fibonacci","Fibonorial","FieldCompletionFunction","FieldHint","FieldHintStyle","FieldMasked","FieldSize","File","FileBaseName","FileByteCount","FileConvert","FileDate","FileExistsQ","FileExtension","FileFormat","FileHandler","FileHash","FileInformation","FileName","FileNameDepth","FileNameDialogSettings","FileNameDrop","FileNameForms","FileNameJoin","FileNames","FileNameSetter","FileNameSplit","FileNameTake","FilePrint","FileSize","FileSystemMap","FileSystemScan","FileTemplate","FileTemplateApply","FileType","FilledCurve","FilledCurveBox","FilledCurveBoxOptions","Filling","FillingStyle","FillingTransform","FilteredEntityClass","FilterRules","FinancialBond","FinancialData","FinancialDerivative","FinancialIndicator","Find","FindAnomalies","FindArgMax","FindArgMin","FindChannels","FindClique","FindClusters","FindCookies","FindCurvePath","FindCycle","FindDevices","FindDistribution","FindDistributionParameters","FindDivisions","FindEdgeCover","FindEdgeCut","FindEdgeIndependentPaths","FindEquationalProof","FindEulerianCycle","FindExternalEvaluators","FindFaces","FindFile","FindFit","FindFormula","FindFundamentalCycles","FindGeneratingFunction","FindGeoLocation","FindGeometricConjectures","FindGeometricTransform","FindGraphCommunities","FindGraphIsomorphism","FindGraphPartition","FindHamiltonianCycle","FindHamiltonianPath","FindHiddenMarkovStates","FindImageText","FindIndependentEdgeSet","FindIndependentVertexSet","FindInstance","FindIntegerNullVector","FindKClan","FindKClique","FindKClub","FindKPlex","FindLibrary","FindLinearRecurrence","FindList","FindMatchingColor","FindMaximum","FindMaximumCut","FindMaximumFlow","FindMaxValue","FindMeshDefects","FindMinimum","FindMinimumCostFlow","FindMinimumCut","FindMinValue","FindMoleculeSubstructure","FindPath","FindPeaks","FindPermutation","FindPostmanTour","FindProcessParameters","FindRepeat","FindRoot","FindSequenceFunction","FindSettings","FindShortestPath","FindShortestTour","FindSpanningTree","FindSystemModelEquilibrium","FindTextualAnswer","FindThreshold","FindTransientRepeat","FindVertexCover","FindVertexCut","FindVertexIndependentPaths","Fine","FinishDynamic","FiniteAbelianGroupCount","FiniteGroupCount","FiniteGroupData","First","FirstCase","FirstPassageTimeDistribution","FirstPosition","FischerGroupFi22","FischerGroupFi23","FischerGroupFi24Prime","FisherHypergeometricDistribution","FisherRatioTest","FisherZDistribution","Fit","FitAll","FitRegularization","FittedModel","FixedOrder","FixedPoint","FixedPointList","FlashSelection","Flat","Flatten","FlattenAt","FlattenLayer","FlatTopWindow","FlipView","Floor","FlowPolynomial","FlushPrintOutputPacket","Fold","FoldList","FoldPair","FoldPairList","FollowRedirects","Font","FontColor","FontFamily","FontForm","FontName","FontOpacity","FontPostScriptName","FontProperties","FontReencoding","FontSize","FontSlant","FontSubstitutions","FontTracking","FontVariations","FontWeight","For","ForAll","ForceVersionInstall","Format","FormatRules","FormatType","FormatTypeAutoConvert","FormatValues","FormBox","FormBoxOptions","FormControl","FormFunction","FormLayoutFunction","FormObject","FormPage","FormTheme","FormulaData","FormulaLookup","FortranForm","Forward","ForwardBackward","Fourier","FourierCoefficient","FourierCosCoefficient","FourierCosSeries","FourierCosTransform","FourierDCT","FourierDCTFilter","FourierDCTMatrix","FourierDST","FourierDSTMatrix","FourierMatrix","FourierParameters","FourierSequenceTransform","FourierSeries","FourierSinCoefficient","FourierSinSeries","FourierSinTransform","FourierTransform","FourierTrigSeries","FractionalBrownianMotionProcess","FractionalGaussianNoiseProcess","FractionalPart","FractionBox","FractionBoxOptions","FractionLine","Frame","FrameBox","FrameBoxOptions","Framed","FrameInset","FrameLabel","Frameless","FrameMargins","FrameRate","FrameStyle","FrameTicks","FrameTicksStyle","FRatioDistribution","FrechetDistribution","FreeQ","FrenetSerretSystem","FrequencySamplingFilterKernel","FresnelC","FresnelF","FresnelG","FresnelS","Friday","FrobeniusNumber","FrobeniusSolve","FromAbsoluteTime","FromCharacterCode","FromCoefficientRules","FromContinuedFraction","FromDate","FromDigits","FromDMS","FromEntity","FromJulianDate","FromLetterNumber","FromPolarCoordinates","FromRomanNumeral","FromSphericalCoordinates","FromUnixTime","Front","FrontEndDynamicExpression","FrontEndEventActions","FrontEndExecute","FrontEndObject","FrontEndResource","FrontEndResourceString","FrontEndStackSize","FrontEndToken","FrontEndTokenExecute","FrontEndValueCache","FrontEndVersion","FrontFaceColor","FrontFaceOpacity","Full","FullAxes","FullDefinition","FullForm","FullGraphics","FullInformationOutputRegulator","FullOptions","FullRegion","FullSimplify","Function","FunctionCompile","FunctionCompileExport","FunctionCompileExportByteArray","FunctionCompileExportLibrary","FunctionCompileExportString","FunctionDomain","FunctionExpand","FunctionInterpolation","FunctionPeriod","FunctionRange","FunctionSpace","FussellVeselyImportance","GaborFilter","GaborMatrix","GaborWavelet","GainMargins","GainPhaseMargins","GalaxyData","GalleryView","Gamma","GammaDistribution","GammaRegularized","GapPenalty","GARCHProcess","GatedRecurrentLayer","Gather","GatherBy","GaugeFaceElementFunction","GaugeFaceStyle","GaugeFrameElementFunction","GaugeFrameSize","GaugeFrameStyle","GaugeLabels","GaugeMarkers","GaugeStyle","GaussianFilter","GaussianIntegers","GaussianMatrix","GaussianOrthogonalMatrixDistribution","GaussianSymplecticMatrixDistribution","GaussianUnitaryMatrixDistribution","GaussianWindow","GCD","GegenbauerC","General","GeneralizedLinearModelFit","GenerateAsymmetricKeyPair","GenerateConditions","GeneratedCell","GeneratedDocumentBinding","GenerateDerivedKey","GenerateDigitalSignature","GenerateDocument","GeneratedParameters","GeneratedQuantityMagnitudes","GenerateFileSignature","GenerateHTTPResponse","GenerateSecuredAuthenticationKey","GenerateSymmetricKey","GeneratingFunction","GeneratorDescription","GeneratorHistoryLength","GeneratorOutputType","Generic","GenericCylindricalDecomposition","GenomeData","GenomeLookup","GeoAntipode","GeoArea","GeoArraySize","GeoBackground","GeoBoundingBox","GeoBounds","GeoBoundsRegion","GeoBubbleChart","GeoCenter","GeoCircle","GeoContourPlot","GeoDensityPlot","GeodesicClosing","GeodesicDilation","GeodesicErosion","GeodesicOpening","GeoDestination","GeodesyData","GeoDirection","GeoDisk","GeoDisplacement","GeoDistance","GeoDistanceList","GeoElevationData","GeoEntities","GeoGraphics","GeogravityModelData","GeoGridDirectionDifference","GeoGridLines","GeoGridLinesStyle","GeoGridPosition","GeoGridRange","GeoGridRangePadding","GeoGridUnitArea","GeoGridUnitDistance","GeoGridVector","GeoGroup","GeoHemisphere","GeoHemisphereBoundary","GeoHistogram","GeoIdentify","GeoImage","GeoLabels","GeoLength","GeoListPlot","GeoLocation","GeologicalPeriodData","GeomagneticModelData","GeoMarker","GeometricAssertion","GeometricBrownianMotionProcess","GeometricDistribution","GeometricMean","GeometricMeanFilter","GeometricOptimization","GeometricScene","GeometricTransformation","GeometricTransformation3DBox","GeometricTransformation3DBoxOptions","GeometricTransformationBox","GeometricTransformationBoxOptions","GeoModel","GeoNearest","GeoPath","GeoPosition","GeoPositionENU","GeoPositionXYZ","GeoProjection","GeoProjectionData","GeoRange","GeoRangePadding","GeoRegionValuePlot","GeoResolution","GeoScaleBar","GeoServer","GeoSmoothHistogram","GeoStreamPlot","GeoStyling","GeoStylingImageFunction","GeoVariant","GeoVector","GeoVectorENU","GeoVectorPlot","GeoVectorXYZ","GeoVisibleRegion","GeoVisibleRegionBoundary","GeoWithinQ","GeoZoomLevel","GestureHandler","GestureHandlerTag","Get","GetBoundingBoxSizePacket","GetContext","GetEnvironment","GetFileName","GetFrontEndOptionsDataPacket","GetLinebreakInformationPacket","GetMenusPacket","GetPageBreakInformationPacket","Glaisher","GlobalClusteringCoefficient","GlobalPreferences","GlobalSession","Glow","GoldenAngle","GoldenRatio","GompertzMakehamDistribution","GoochShading","GoodmanKruskalGamma","GoodmanKruskalGammaTest","Goto","Grad","Gradient","GradientFilter","GradientOrientationFilter","GrammarApply","GrammarRules","GrammarToken","Graph","Graph3D","GraphAssortativity","GraphAutomorphismGroup","GraphCenter","GraphComplement","GraphData","GraphDensity","GraphDiameter","GraphDifference","GraphDisjointUnion","GraphDistance","GraphDistanceMatrix","GraphElementData","GraphEmbedding","GraphHighlight","GraphHighlightStyle","GraphHub","Graphics","Graphics3D","Graphics3DBox","Graphics3DBoxOptions","GraphicsArray","GraphicsBaseline","GraphicsBox","GraphicsBoxOptions","GraphicsColor","GraphicsColumn","GraphicsComplex","GraphicsComplex3DBox","GraphicsComplex3DBoxOptions","GraphicsComplexBox","GraphicsComplexBoxOptions","GraphicsContents","GraphicsData","GraphicsGrid","GraphicsGridBox","GraphicsGroup","GraphicsGroup3DBox","GraphicsGroup3DBoxOptions","GraphicsGroupBox","GraphicsGroupBoxOptions","GraphicsGrouping","GraphicsHighlightColor","GraphicsRow","GraphicsSpacing","GraphicsStyle","GraphIntersection","GraphLayout","GraphLinkEfficiency","GraphPeriphery","GraphPlot","GraphPlot3D","GraphPower","GraphPropertyDistribution","GraphQ","GraphRadius","GraphReciprocity","GraphRoot","GraphStyle","GraphUnion","Gray","GrayLevel","Greater","GreaterEqual","GreaterEqualLess","GreaterEqualThan","GreaterFullEqual","GreaterGreater","GreaterLess","GreaterSlantEqual","GreaterThan","GreaterTilde","Green","GreenFunction","Grid","GridBaseline","GridBox","GridBoxAlignment","GridBoxBackground","GridBoxDividers","GridBoxFrame","GridBoxItemSize","GridBoxItemStyle","GridBoxOptions","GridBoxSpacings","GridCreationSettings","GridDefaultElement","GridElementStyleOptions","GridFrame","GridFrameMargins","GridGraph","GridLines","GridLinesStyle","GroebnerBasis","GroupActionBase","GroupBy","GroupCentralizer","GroupElementFromWord","GroupElementPosition","GroupElementQ","GroupElements","GroupElementToWord","GroupGenerators","Groupings","GroupMultiplicationTable","GroupOrbits","GroupOrder","GroupPageBreakWithin","GroupSetwiseStabilizer","GroupStabilizer","GroupStabilizerChain","GroupTogetherGrouping","GroupTogetherNestedGrouping","GrowCutComponents","Gudermannian","GuidedFilter","GumbelDistribution","HaarWavelet","HadamardMatrix","HalfLine","HalfNormalDistribution","HalfPlane","HalfSpace","HalftoneShading","HamiltonianGraphQ","HammingDistance","HammingWindow","HandlerFunctions","HandlerFunctionsKeys","HankelH1","HankelH2","HankelMatrix","HankelTransform","HannPoissonWindow","HannWindow","HaradaNortonGroupHN","HararyGraph","HarmonicMean","HarmonicMeanFilter","HarmonicNumber","Hash","HatchFilling","HatchShading","Haversine","HazardFunction","Head","HeadCompose","HeaderAlignment","HeaderBackground","HeaderDisplayFunction","HeaderLines","HeaderSize","HeaderStyle","Heads","HeavisideLambda","HeavisidePi","HeavisideTheta","HeldGroupHe","HeldPart","HelpBrowserLookup","HelpBrowserNotebook","HelpBrowserSettings","Here","HermiteDecomposition","HermiteH","HermitianMatrixQ","HessenbergDecomposition","Hessian","HeunB","HeunBPrime","HeunC","HeunCPrime","HeunD","HeunDPrime","HeunG","HeunGPrime","HeunT","HeunTPrime","HexadecimalCharacter","Hexahedron","HexahedronBox","HexahedronBoxOptions","HiddenItems","HiddenMarkovProcess","HiddenSurface","Highlighted","HighlightGraph","HighlightImage","HighlightMesh","HighpassFilter","HigmanSimsGroupHS","HilbertCurve","HilbertFilter","HilbertMatrix","Histogram","Histogram3D","HistogramDistribution","HistogramList","HistogramTransform","HistogramTransformInterpolation","HistoricalPeriodData","HitMissTransform","HITSCentrality","HjorthDistribution","HodgeDual","HoeffdingD","HoeffdingDTest","Hold","HoldAll","HoldAllComplete","HoldComplete","HoldFirst","HoldForm","HoldPattern","HoldRest","HolidayCalendar","HomeDirectory","HomePage","Horizontal","HorizontalForm","HorizontalGauge","HorizontalScrollPosition","HornerForm","HostLookup","HotellingTSquareDistribution","HoytDistribution","HTMLSave","HTTPErrorResponse","HTTPRedirect","HTTPRequest","HTTPRequestData","HTTPResponse","Hue","HumanGrowthData","HumpDownHump","HumpEqual","HurwitzLerchPhi","HurwitzZeta","HyperbolicDistribution","HypercubeGraph","HyperexponentialDistribution","Hyperfactorial","Hypergeometric0F1","Hypergeometric0F1Regularized","Hypergeometric1F1","Hypergeometric1F1Regularized","Hypergeometric2F1","Hypergeometric2F1Regularized","HypergeometricDistribution","HypergeometricPFQ","HypergeometricPFQRegularized","HypergeometricU","Hyperlink","HyperlinkAction","HyperlinkCreationSettings","Hyperplane","Hyphenation","HyphenationOptions","HypoexponentialDistribution","HypothesisTestData","I","IconData","Iconize","IconizedObject","IconRules","Icosahedron","Identity","IdentityMatrix","If","IgnoreCase","IgnoreDiacritics","IgnorePunctuation","IgnoreSpellCheck","IgnoringInactive","Im","Image","Image3D","Image3DProjection","Image3DSlices","ImageAccumulate","ImageAdd","ImageAdjust","ImageAlign","ImageApply","ImageApplyIndexed","ImageAspectRatio","ImageAssemble","ImageAugmentationLayer","ImageBoundingBoxes","ImageCache","ImageCacheValid","ImageCapture","ImageCaptureFunction","ImageCases","ImageChannels","ImageClip","ImageCollage","ImageColorSpace","ImageCompose","ImageContainsQ","ImageContents","ImageConvolve","ImageCooccurrence","ImageCorners","ImageCorrelate","ImageCorrespondingPoints","ImageCrop","ImageData","ImageDeconvolve","ImageDemosaic","ImageDifference","ImageDimensions","ImageDisplacements","ImageDistance","ImageEffect","ImageExposureCombine","ImageFeatureTrack","ImageFileApply","ImageFileFilter","ImageFileScan","ImageFilter","ImageFocusCombine","ImageForestingComponents","ImageFormattingWidth","ImageForwardTransformation","ImageGraphics","ImageHistogram","ImageIdentify","ImageInstanceQ","ImageKeypoints","ImageLabels","ImageLegends","ImageLevels","ImageLines","ImageMargins","ImageMarker","ImageMarkers","ImageMeasurements","ImageMesh","ImageMultiply","ImageOffset","ImagePad","ImagePadding","ImagePartition","ImagePeriodogram","ImagePerspectiveTransformation","ImagePosition","ImagePreviewFunction","ImagePyramid","ImagePyramidApply","ImageQ","ImageRangeCache","ImageRecolor","ImageReflect","ImageRegion","ImageResize","ImageResolution","ImageRestyle","ImageRotate","ImageRotated","ImageSaliencyFilter","ImageScaled","ImageScan","ImageSize","ImageSizeAction","ImageSizeCache","ImageSizeMultipliers","ImageSizeRaw","ImageSubtract","ImageTake","ImageTransformation","ImageTrim","ImageType","ImageValue","ImageValuePositions","ImagingDevice","ImplicitRegion","Implies","Import","ImportAutoReplacements","ImportByteArray","ImportOptions","ImportString","ImprovementImportance","In","Inactivate","Inactive","IncidenceGraph","IncidenceList","IncidenceMatrix","IncludeAromaticBonds","IncludeConstantBasis","IncludeDefinitions","IncludeDirectories","IncludeFileExtension","IncludeGeneratorTasks","IncludeHydrogens","IncludeInflections","IncludeMetaInformation","IncludePods","IncludeQuantities","IncludeRelatedTables","IncludeSingularTerm","IncludeWindowTimes","Increment","IndefiniteMatrixQ","Indent","IndentingNewlineSpacings","IndentMaxFraction","IndependenceTest","IndependentEdgeSetQ","IndependentPhysicalQuantity","IndependentUnit","IndependentUnitDimension","IndependentVertexSetQ","Indeterminate","IndeterminateThreshold","IndexCreationOptions","Indexed","IndexEdgeTaggedGraph","IndexGraph","IndexTag","Inequality","InexactNumberQ","InexactNumbers","InfiniteFuture","InfiniteLine","InfinitePast","InfinitePlane","Infinity","Infix","InflationAdjust","InflationMethod","Information","InformationData","InformationDataGrid","Inherited","InheritScope","InhomogeneousPoissonProcess","InitialEvaluationHistory","Initialization","InitializationCell","InitializationCellEvaluation","InitializationCellWarning","InitializationObjects","InitializationValue","Initialize","InitialSeeding","InlineCounterAssignments","InlineCounterIncrements","InlineRules","Inner","InnerPolygon","InnerPolyhedron","Inpaint","Input","InputAliases","InputAssumptions","InputAutoReplacements","InputField","InputFieldBox","InputFieldBoxOptions","InputForm","InputGrouping","InputNamePacket","InputNotebook","InputPacket","InputSettings","InputStream","InputString","InputStringPacket","InputToBoxFormPacket","Insert","InsertionFunction","InsertionPointObject","InsertLinebreaks","InsertResults","Inset","Inset3DBox","Inset3DBoxOptions","InsetBox","InsetBoxOptions","Insphere","Install","InstallService","InstanceNormalizationLayer","InString","Integer","IntegerDigits","IntegerExponent","IntegerLength","IntegerName","IntegerPart","IntegerPartitions","IntegerQ","IntegerReverse","Integers","IntegerString","Integral","Integrate","Interactive","InteractiveTradingChart","Interlaced","Interleaving","InternallyBalancedDecomposition","InterpolatingFunction","InterpolatingPolynomial","Interpolation","InterpolationOrder","InterpolationPoints","InterpolationPrecision","Interpretation","InterpretationBox","InterpretationBoxOptions","InterpretationFunction","Interpreter","InterpretTemplate","InterquartileRange","Interrupt","InterruptSettings","IntersectedEntityClass","IntersectingQ","Intersection","Interval","IntervalIntersection","IntervalMarkers","IntervalMarkersStyle","IntervalMemberQ","IntervalSlider","IntervalUnion","Into","Inverse","InverseBetaRegularized","InverseCDF","InverseChiSquareDistribution","InverseContinuousWaveletTransform","InverseDistanceTransform","InverseEllipticNomeQ","InverseErf","InverseErfc","InverseFourier","InverseFourierCosTransform","InverseFourierSequenceTransform","InverseFourierSinTransform","InverseFourierTransform","InverseFunction","InverseFunctions","InverseGammaDistribution","InverseGammaRegularized","InverseGaussianDistribution","InverseGudermannian","InverseHankelTransform","InverseHaversine","InverseImagePyramid","InverseJacobiCD","InverseJacobiCN","InverseJacobiCS","InverseJacobiDC","InverseJacobiDN","InverseJacobiDS","InverseJacobiNC","InverseJacobiND","InverseJacobiNS","InverseJacobiSC","InverseJacobiSD","InverseJacobiSN","InverseLaplaceTransform","InverseMellinTransform","InversePermutation","InverseRadon","InverseRadonTransform","InverseSeries","InverseShortTimeFourier","InverseSpectrogram","InverseSurvivalFunction","InverseTransformedRegion","InverseWaveletTransform","InverseWeierstrassP","InverseWishartMatrixDistribution","InverseZTransform","Invisible","InvisibleApplication","InvisibleTimes","IPAddress","IrreduciblePolynomialQ","IslandData","IsolatingInterval","IsomorphicGraphQ","IsotopeData","Italic","Item","ItemAspectRatio","ItemBox","ItemBoxOptions","ItemDisplayFunction","ItemSize","ItemStyle","ItoProcess","JaccardDissimilarity","JacobiAmplitude","Jacobian","JacobiCD","JacobiCN","JacobiCS","JacobiDC","JacobiDN","JacobiDS","JacobiNC","JacobiND","JacobiNS","JacobiP","JacobiSC","JacobiSD","JacobiSN","JacobiSymbol","JacobiZeta","JankoGroupJ1","JankoGroupJ2","JankoGroupJ3","JankoGroupJ4","JarqueBeraALMTest","JohnsonDistribution","Join","JoinAcross","Joined","JoinedCurve","JoinedCurveBox","JoinedCurveBoxOptions","JoinForm","JordanDecomposition","JordanModelDecomposition","JulianDate","JuliaSetBoettcher","JuliaSetIterationCount","JuliaSetPlot","JuliaSetPoints","K","KagiChart","KaiserBesselWindow","KaiserWindow","KalmanEstimator","KalmanFilter","KarhunenLoeveDecomposition","KaryTree","KatzCentrality","KCoreComponents","KDistribution","KEdgeConnectedComponents","KEdgeConnectedGraphQ","KeepExistingVersion","KelvinBei","KelvinBer","KelvinKei","KelvinKer","KendallTau","KendallTauTest","KernelExecute","KernelFunction","KernelMixtureDistribution","KernelObject","Kernels","Ket","Key","KeyCollisionFunction","KeyComplement","KeyDrop","KeyDropFrom","KeyExistsQ","KeyFreeQ","KeyIntersection","KeyMap","KeyMemberQ","KeypointStrength","Keys","KeySelect","KeySort","KeySortBy","KeyTake","KeyUnion","KeyValueMap","KeyValuePattern","Khinchin","KillProcess","KirchhoffGraph","KirchhoffMatrix","KleinInvariantJ","KnapsackSolve","KnightTourGraph","KnotData","KnownUnitQ","KochCurve","KolmogorovSmirnovTest","KroneckerDelta","KroneckerModelDecomposition","KroneckerProduct","KroneckerSymbol","KuiperTest","KumaraswamyDistribution","Kurtosis","KuwaharaFilter","KVertexConnectedComponents","KVertexConnectedGraphQ","LABColor","Label","Labeled","LabeledSlider","LabelingFunction","LabelingSize","LabelStyle","LabelVisibility","LaguerreL","LakeData","LambdaComponents","LambertW","LaminaData","LanczosWindow","LandauDistribution","Language","LanguageCategory","LanguageData","LanguageIdentify","LanguageOptions","LaplaceDistribution","LaplaceTransform","Laplacian","LaplacianFilter","LaplacianGaussianFilter","Large","Larger","Last","Latitude","LatitudeLongitude","LatticeData","LatticeReduce","Launch","LaunchKernels","LayeredGraphPlot","LayerSizeFunction","LayoutInformation","LCHColor","LCM","LeaderSize","LeafCount","LeapYearQ","LearnDistribution","LearnedDistribution","LearningRate","LearningRateMultipliers","LeastSquares","LeastSquaresFilterKernel","Left","LeftArrow","LeftArrowBar","LeftArrowRightArrow","LeftDownTeeVector","LeftDownVector","LeftDownVectorBar","LeftRightArrow","LeftRightVector","LeftTee","LeftTeeArrow","LeftTeeVector","LeftTriangle","LeftTriangleBar","LeftTriangleEqual","LeftUpDownVector","LeftUpTeeVector","LeftUpVector","LeftUpVectorBar","LeftVector","LeftVectorBar","LegendAppearance","Legended","LegendFunction","LegendLabel","LegendLayout","LegendMargins","LegendMarkers","LegendMarkerSize","LegendreP","LegendreQ","LegendreType","Length","LengthWhile","LerchPhi","Less","LessEqual","LessEqualGreater","LessEqualThan","LessFullEqual","LessGreater","LessLess","LessSlantEqual","LessThan","LessTilde","LetterCharacter","LetterCounts","LetterNumber","LetterQ","Level","LeveneTest","LeviCivitaTensor","LevyDistribution","Lexicographic","LibraryDataType","LibraryFunction","LibraryFunctionError","LibraryFunctionInformation","LibraryFunctionLoad","LibraryFunctionUnload","LibraryLoad","LibraryUnload","LicenseID","LiftingFilterData","LiftingWaveletTransform","LightBlue","LightBrown","LightCyan","Lighter","LightGray","LightGreen","Lighting","LightingAngle","LightMagenta","LightOrange","LightPink","LightPurple","LightRed","LightSources","LightYellow","Likelihood","Limit","LimitsPositioning","LimitsPositioningTokens","LindleyDistribution","Line","Line3DBox","Line3DBoxOptions","LinearFilter","LinearFractionalOptimization","LinearFractionalTransform","LinearGradientImage","LinearizingTransformationData","LinearLayer","LinearModelFit","LinearOffsetFunction","LinearOptimization","LinearProgramming","LinearRecurrence","LinearSolve","LinearSolveFunction","LineBox","LineBoxOptions","LineBreak","LinebreakAdjustments","LineBreakChart","LinebreakSemicolonWeighting","LineBreakWithin","LineColor","LineGraph","LineIndent","LineIndentMaxFraction","LineIntegralConvolutionPlot","LineIntegralConvolutionScale","LineLegend","LineOpacity","LineSpacing","LineWrapParts","LinkActivate","LinkClose","LinkConnect","LinkConnectedQ","LinkCreate","LinkError","LinkFlush","LinkFunction","LinkHost","LinkInterrupt","LinkLaunch","LinkMode","LinkObject","LinkOpen","LinkOptions","LinkPatterns","LinkProtocol","LinkRankCentrality","LinkRead","LinkReadHeld","LinkReadyQ","Links","LinkService","LinkWrite","LinkWriteHeld","LiouvilleLambda","List","Listable","ListAnimate","ListContourPlot","ListContourPlot3D","ListConvolve","ListCorrelate","ListCurvePathPlot","ListDeconvolve","ListDensityPlot","ListDensityPlot3D","Listen","ListFormat","ListFourierSequenceTransform","ListInterpolation","ListLineIntegralConvolutionPlot","ListLinePlot","ListLogLinearPlot","ListLogLogPlot","ListLogPlot","ListPicker","ListPickerBox","ListPickerBoxBackground","ListPickerBoxOptions","ListPlay","ListPlot","ListPlot3D","ListPointPlot3D","ListPolarPlot","ListQ","ListSliceContourPlot3D","ListSliceDensityPlot3D","ListSliceVectorPlot3D","ListStepPlot","ListStreamDensityPlot","ListStreamPlot","ListSurfacePlot3D","ListVectorDensityPlot","ListVectorPlot","ListVectorPlot3D","ListZTransform","Literal","LiteralSearch","LocalAdaptiveBinarize","LocalCache","LocalClusteringCoefficient","LocalizeDefinitions","LocalizeVariables","LocalObject","LocalObjects","LocalResponseNormalizationLayer","LocalSubmit","LocalSymbol","LocalTime","LocalTimeZone","LocationEquivalenceTest","LocationTest","Locator","LocatorAutoCreate","LocatorBox","LocatorBoxOptions","LocatorCentering","LocatorPane","LocatorPaneBox","LocatorPaneBoxOptions","LocatorRegion","Locked","Log","Log10","Log2","LogBarnesG","LogGamma","LogGammaDistribution","LogicalExpand","LogIntegral","LogisticDistribution","LogisticSigmoid","LogitModelFit","LogLikelihood","LogLinearPlot","LogLogisticDistribution","LogLogPlot","LogMultinormalDistribution","LogNormalDistribution","LogPlot","LogRankTest","LogSeriesDistribution","LongEqual","Longest","LongestCommonSequence","LongestCommonSequencePositions","LongestCommonSubsequence","LongestCommonSubsequencePositions","LongestMatch","LongestOrderedSequence","LongForm","Longitude","LongLeftArrow","LongLeftRightArrow","LongRightArrow","LongShortTermMemoryLayer","Lookup","Loopback","LoopFreeGraphQ","Looping","LossFunction","LowerCaseQ","LowerLeftArrow","LowerRightArrow","LowerTriangularize","LowerTriangularMatrixQ","LowpassFilter","LQEstimatorGains","LQGRegulator","LQOutputRegulatorGains","LQRegulatorGains","LUBackSubstitution","LucasL","LuccioSamiComponents","LUDecomposition","LunarEclipse","LUVColor","LyapunovSolve","LyonsGroupLy","MachineID","MachineName","MachineNumberQ","MachinePrecision","MacintoshSystemPageSetup","Magenta","Magnification","Magnify","MailAddressValidation","MailExecute","MailFolder","MailItem","MailReceiverFunction","MailResponseFunction","MailSearch","MailServerConnect","MailServerConnection","MailSettings","MainSolve","MaintainDynamicCaches","Majority","MakeBoxes","MakeExpression","MakeRules","ManagedLibraryExpressionID","ManagedLibraryExpressionQ","MandelbrotSetBoettcher","MandelbrotSetDistance","MandelbrotSetIterationCount","MandelbrotSetMemberQ","MandelbrotSetPlot","MangoldtLambda","ManhattanDistance","Manipulate","Manipulator","MannedSpaceMissionData","MannWhitneyTest","MantissaExponent","Manual","Map","MapAll","MapAt","MapIndexed","MAProcess","MapThread","MarchenkoPasturDistribution","MarcumQ","MardiaCombinedTest","MardiaKurtosisTest","MardiaSkewnessTest","MarginalDistribution","MarkovProcessProperties","Masking","MatchingDissimilarity","MatchLocalNameQ","MatchLocalNames","MatchQ","Material","MathematicalFunctionData","MathematicaNotation","MathieuC","MathieuCharacteristicA","MathieuCharacteristicB","MathieuCharacteristicExponent","MathieuCPrime","MathieuGroupM11","MathieuGroupM12","MathieuGroupM22","MathieuGroupM23","MathieuGroupM24","MathieuS","MathieuSPrime","MathMLForm","MathMLText","Matrices","MatrixExp","MatrixForm","MatrixFunction","MatrixLog","MatrixNormalDistribution","MatrixPlot","MatrixPower","MatrixPropertyDistribution","MatrixQ","MatrixRank","MatrixTDistribution","Max","MaxBend","MaxCellMeasure","MaxColorDistance","MaxDate","MaxDetect","MaxDuration","MaxExtraBandwidths","MaxExtraConditions","MaxFeatureDisplacement","MaxFeatures","MaxFilter","MaximalBy","Maximize","MaxItems","MaxIterations","MaxLimit","MaxMemoryUsed","MaxMixtureKernels","MaxOverlapFraction","MaxPlotPoints","MaxPoints","MaxRecursion","MaxStableDistribution","MaxStepFraction","MaxSteps","MaxStepSize","MaxTrainingRounds","MaxValue","MaxwellDistribution","MaxWordGap","McLaughlinGroupMcL","Mean","MeanAbsoluteLossLayer","MeanAround","MeanClusteringCoefficient","MeanDegreeConnectivity","MeanDeviation","MeanFilter","MeanGraphDistance","MeanNeighborDegree","MeanShift","MeanShiftFilter","MeanSquaredLossLayer","Median","MedianDeviation","MedianFilter","MedicalTestData","Medium","MeijerG","MeijerGReduce","MeixnerDistribution","MellinConvolve","MellinTransform","MemberQ","MemoryAvailable","MemoryConstrained","MemoryConstraint","MemoryInUse","MengerMesh","Menu","MenuAppearance","MenuCommandKey","MenuEvaluator","MenuItem","MenuList","MenuPacket","MenuSortingValue","MenuStyle","MenuView","Merge","MergeDifferences","MergingFunction","MersennePrimeExponent","MersennePrimeExponentQ","Mesh","MeshCellCentroid","MeshCellCount","MeshCellHighlight","MeshCellIndex","MeshCellLabel","MeshCellMarker","MeshCellMeasure","MeshCellQuality","MeshCells","MeshCellShapeFunction","MeshCellStyle","MeshConnectivityGraph","MeshCoordinates","MeshFunctions","MeshPrimitives","MeshQualityGoal","MeshRange","MeshRefinementFunction","MeshRegion","MeshRegionQ","MeshShading","MeshStyle","Message","MessageDialog","MessageList","MessageName","MessageObject","MessageOptions","MessagePacket","Messages","MessagesNotebook","MetaCharacters","MetaInformation","MeteorShowerData","Method","MethodOptions","MexicanHatWavelet","MeyerWavelet","Midpoint","Min","MinColorDistance","MinDate","MinDetect","MineralData","MinFilter","MinimalBy","MinimalPolynomial","MinimalStateSpaceModel","Minimize","MinimumTimeIncrement","MinIntervalSize","MinkowskiQuestionMark","MinLimit","MinMax","MinorPlanetData","Minors","MinRecursion","MinSize","MinStableDistribution","Minus","MinusPlus","MinValue","Missing","MissingBehavior","MissingDataMethod","MissingDataRules","MissingQ","MissingString","MissingStyle","MissingValuePattern","MittagLefflerE","MixedFractionParts","MixedGraphQ","MixedMagnitude","MixedRadix","MixedRadixQuantity","MixedUnit","MixtureDistribution","Mod","Modal","Mode","Modular","ModularInverse","ModularLambda","Module","Modulus","MoebiusMu","Molecule","MoleculeContainsQ","MoleculeEquivalentQ","MoleculeGraph","MoleculeModify","MoleculePattern","MoleculePlot","MoleculePlot3D","MoleculeProperty","MoleculeQ","MoleculeRecognize","MoleculeValue","Moment","Momentary","MomentConvert","MomentEvaluate","MomentGeneratingFunction","MomentOfInertia","Monday","Monitor","MonomialList","MonomialOrder","MonsterGroupM","MoonPhase","MoonPosition","MorletWavelet","MorphologicalBinarize","MorphologicalBranchPoints","MorphologicalComponents","MorphologicalEulerNumber","MorphologicalGraph","MorphologicalPerimeter","MorphologicalTransform","MortalityData","Most","MountainData","MouseAnnotation","MouseAppearance","MouseAppearanceTag","MouseButtons","Mouseover","MousePointerNote","MousePosition","MovieData","MovingAverage","MovingMap","MovingMedian","MoyalDistribution","Multicolumn","MultiedgeStyle","MultigraphQ","MultilaunchWarning","MultiLetterItalics","MultiLetterStyle","MultilineFunction","Multinomial","MultinomialDistribution","MultinormalDistribution","MultiplicativeOrder","Multiplicity","MultiplySides","Multiselection","MultivariateHypergeometricDistribution","MultivariatePoissonDistribution","MultivariateTDistribution","N","NakagamiDistribution","NameQ","Names","NamespaceBox","NamespaceBoxOptions","Nand","NArgMax","NArgMin","NBernoulliB","NBodySimulation","NBodySimulationData","NCache","NDEigensystem","NDEigenvalues","NDSolve","NDSolveValue","Nearest","NearestFunction","NearestMeshCells","NearestNeighborGraph","NearestTo","NebulaData","NeedCurrentFrontEndPackagePacket","NeedCurrentFrontEndSymbolsPacket","NeedlemanWunschSimilarity","Needs","Negative","NegativeBinomialDistribution","NegativeDefiniteMatrixQ","NegativeIntegers","NegativeMultinomialDistribution","NegativeRationals","NegativeReals","NegativeSemidefiniteMatrixQ","NeighborhoodData","NeighborhoodGraph","Nest","NestedGreaterGreater","NestedLessLess","NestedScriptRules","NestGraph","NestList","NestWhile","NestWhileList","NetAppend","NetBidirectionalOperator","NetChain","NetDecoder","NetDelete","NetDrop","NetEncoder","NetEvaluationMode","NetExtract","NetFlatten","NetFoldOperator","NetGANOperator","NetGraph","NetInformation","NetInitialize","NetInsert","NetInsertSharedArrays","NetJoin","NetMapOperator","NetMapThreadOperator","NetMeasurements","NetModel","NetNestOperator","NetPairEmbeddingOperator","NetPort","NetPortGradient","NetPrepend","NetRename","NetReplace","NetReplacePart","NetSharedArray","NetStateObject","NetTake","NetTrain","NetTrainResultsObject","NetworkPacketCapture","NetworkPacketRecording","NetworkPacketRecordingDuring","NetworkPacketTrace","NeumannValue","NevilleThetaC","NevilleThetaD","NevilleThetaN","NevilleThetaS","NewPrimitiveStyle","NExpectation","Next","NextCell","NextDate","NextPrime","NextScheduledTaskTime","NHoldAll","NHoldFirst","NHoldRest","NicholsGridLines","NicholsPlot","NightHemisphere","NIntegrate","NMaximize","NMaxValue","NMinimize","NMinValue","NominalVariables","NonAssociative","NoncentralBetaDistribution","NoncentralChiSquareDistribution","NoncentralFRatioDistribution","NoncentralStudentTDistribution","NonCommutativeMultiply","NonConstants","NondimensionalizationTransform","None","NoneTrue","NonlinearModelFit","NonlinearStateSpaceModel","NonlocalMeansFilter","NonNegative","NonNegativeIntegers","NonNegativeRationals","NonNegativeReals","NonPositive","NonPositiveIntegers","NonPositiveRationals","NonPositiveReals","Nor","NorlundB","Norm","Normal","NormalDistribution","NormalGrouping","NormalizationLayer","Normalize","Normalized","NormalizedSquaredEuclideanDistance","NormalMatrixQ","NormalsFunction","NormFunction","Not","NotCongruent","NotCupCap","NotDoubleVerticalBar","Notebook","NotebookApply","NotebookAutoSave","NotebookClose","NotebookConvertSettings","NotebookCreate","NotebookCreateReturnObject","NotebookDefault","NotebookDelete","NotebookDirectory","NotebookDynamicExpression","NotebookEvaluate","NotebookEventActions","NotebookFileName","NotebookFind","NotebookFindReturnObject","NotebookGet","NotebookGetLayoutInformationPacket","NotebookGetMisspellingsPacket","NotebookImport","NotebookInformation","NotebookInterfaceObject","NotebookLocate","NotebookObject","NotebookOpen","NotebookOpenReturnObject","NotebookPath","NotebookPrint","NotebookPut","NotebookPutReturnObject","NotebookRead","NotebookResetGeneratedCells","Notebooks","NotebookSave","NotebookSaveAs","NotebookSelection","NotebookSetupLayoutInformationPacket","NotebooksMenu","NotebookTemplate","NotebookWrite","NotElement","NotEqualTilde","NotExists","NotGreater","NotGreaterEqual","NotGreaterFullEqual","NotGreaterGreater","NotGreaterLess","NotGreaterSlantEqual","NotGreaterTilde","Nothing","NotHumpDownHump","NotHumpEqual","NotificationFunction","NotLeftTriangle","NotLeftTriangleBar","NotLeftTriangleEqual","NotLess","NotLessEqual","NotLessFullEqual","NotLessGreater","NotLessLess","NotLessSlantEqual","NotLessTilde","NotNestedGreaterGreater","NotNestedLessLess","NotPrecedes","NotPrecedesEqual","NotPrecedesSlantEqual","NotPrecedesTilde","NotReverseElement","NotRightTriangle","NotRightTriangleBar","NotRightTriangleEqual","NotSquareSubset","NotSquareSubsetEqual","NotSquareSuperset","NotSquareSupersetEqual","NotSubset","NotSubsetEqual","NotSucceeds","NotSucceedsEqual","NotSucceedsSlantEqual","NotSucceedsTilde","NotSuperset","NotSupersetEqual","NotTilde","NotTildeEqual","NotTildeFullEqual","NotTildeTilde","NotVerticalBar","Now","NoWhitespace","NProbability","NProduct","NProductFactors","NRoots","NSolve","NSum","NSumTerms","NuclearExplosionData","NuclearReactorData","Null","NullRecords","NullSpace","NullWords","Number","NumberCompose","NumberDecompose","NumberExpand","NumberFieldClassNumber","NumberFieldDiscriminant","NumberFieldFundamentalUnits","NumberFieldIntegralBasis","NumberFieldNormRepresentatives","NumberFieldRegulator","NumberFieldRootsOfUnity","NumberFieldSignature","NumberForm","NumberFormat","NumberLinePlot","NumberMarks","NumberMultiplier","NumberPadding","NumberPoint","NumberQ","NumberSeparator","NumberSigns","NumberString","Numerator","NumeratorDenominator","NumericalOrder","NumericalSort","NumericArray","NumericArrayQ","NumericArrayType","NumericFunction","NumericQ","NuttallWindow","NValues","NyquistGridLines","NyquistPlot","O","ObservabilityGramian","ObservabilityMatrix","ObservableDecomposition","ObservableModelQ","OceanData","Octahedron","OddQ","Off","Offset","OLEData","On","ONanGroupON","Once","OneIdentity","Opacity","OpacityFunction","OpacityFunctionScaling","Open","OpenAppend","Opener","OpenerBox","OpenerBoxOptions","OpenerView","OpenFunctionInspectorPacket","Opening","OpenRead","OpenSpecialOptions","OpenTemporary","OpenWrite","Operate","OperatingSystem","OperatorApplied","OptimumFlowData","Optional","OptionalElement","OptionInspectorSettings","OptionQ","Options","OptionsPacket","OptionsPattern","OptionValue","OptionValueBox","OptionValueBoxOptions","Or","Orange","Order","OrderDistribution","OrderedQ","Ordering","OrderingBy","OrderingLayer","Orderless","OrderlessPatternSequence","OrnsteinUhlenbeckProcess","Orthogonalize","OrthogonalMatrixQ","Out","Outer","OuterPolygon","OuterPolyhedron","OutputAutoOverwrite","OutputControllabilityMatrix","OutputControllableModelQ","OutputForm","OutputFormData","OutputGrouping","OutputMathEditExpression","OutputNamePacket","OutputResponse","OutputSizeLimit","OutputStream","Over","OverBar","OverDot","Overflow","OverHat","Overlaps","Overlay","OverlayBox","OverlayBoxOptions","Overscript","OverscriptBox","OverscriptBoxOptions","OverTilde","OverVector","OverwriteTarget","OwenT","OwnValues","Package","PackingMethod","PackPaclet","PacletDataRebuild","PacletDirectoryAdd","PacletDirectoryLoad","PacletDirectoryRemove","PacletDirectoryUnload","PacletDisable","PacletEnable","PacletFind","PacletFindRemote","PacletInformation","PacletInstall","PacletInstallSubmit","PacletNewerQ","PacletObject","PacletObjectQ","PacletSite","PacletSiteObject","PacletSiteRegister","PacletSites","PacletSiteUnregister","PacletSiteUpdate","PacletUninstall","PacletUpdate","PaddedForm","Padding","PaddingLayer","PaddingSize","PadeApproximant","PadLeft","PadRight","PageBreakAbove","PageBreakBelow","PageBreakWithin","PageFooterLines","PageFooters","PageHeaderLines","PageHeaders","PageHeight","PageRankCentrality","PageTheme","PageWidth","Pagination","PairedBarChart","PairedHistogram","PairedSmoothHistogram","PairedTTest","PairedZTest","PaletteNotebook","PalettePath","PalindromeQ","Pane","PaneBox","PaneBoxOptions","Panel","PanelBox","PanelBoxOptions","Paneled","PaneSelector","PaneSelectorBox","PaneSelectorBoxOptions","PaperWidth","ParabolicCylinderD","ParagraphIndent","ParagraphSpacing","ParallelArray","ParallelCombine","ParallelDo","Parallelepiped","ParallelEvaluate","Parallelization","Parallelize","ParallelMap","ParallelNeeds","Parallelogram","ParallelProduct","ParallelSubmit","ParallelSum","ParallelTable","ParallelTry","Parameter","ParameterEstimator","ParameterMixtureDistribution","ParameterVariables","ParametricFunction","ParametricNDSolve","ParametricNDSolveValue","ParametricPlot","ParametricPlot3D","ParametricRampLayer","ParametricRegion","ParentBox","ParentCell","ParentConnect","ParentDirectory","ParentForm","Parenthesize","ParentList","ParentNotebook","ParetoDistribution","ParetoPickandsDistribution","ParkData","Part","PartBehavior","PartialCorrelationFunction","PartialD","ParticleAcceleratorData","ParticleData","Partition","PartitionGranularity","PartitionsP","PartitionsQ","PartLayer","PartOfSpeech","PartProtection","ParzenWindow","PascalDistribution","PassEventsDown","PassEventsUp","Paste","PasteAutoQuoteCharacters","PasteBoxFormInlineCells","PasteButton","Path","PathGraph","PathGraphQ","Pattern","PatternFilling","PatternSequence","PatternTest","PauliMatrix","PaulWavelet","Pause","PausedTime","PDF","PeakDetect","PeanoCurve","PearsonChiSquareTest","PearsonCorrelationTest","PearsonDistribution","PercentForm","PerfectNumber","PerfectNumberQ","PerformanceGoal","Perimeter","PeriodicBoundaryCondition","PeriodicInterpolation","Periodogram","PeriodogramArray","Permanent","Permissions","PermissionsGroup","PermissionsGroupMemberQ","PermissionsGroups","PermissionsKey","PermissionsKeys","PermutationCycles","PermutationCyclesQ","PermutationGroup","PermutationLength","PermutationList","PermutationListQ","PermutationMax","PermutationMin","PermutationOrder","PermutationPower","PermutationProduct","PermutationReplace","Permutations","PermutationSupport","Permute","PeronaMalikFilter","Perpendicular","PerpendicularBisector","PersistenceLocation","PersistenceTime","PersistentObject","PersistentObjects","PersistentValue","PersonData","PERTDistribution","PetersenGraph","PhaseMargins","PhaseRange","PhysicalSystemData","Pi","Pick","PIDData","PIDDerivativeFilter","PIDFeedforward","PIDTune","Piecewise","PiecewiseExpand","PieChart","PieChart3D","PillaiTrace","PillaiTraceTest","PingTime","Pink","PitchRecognize","Pivoting","PixelConstrained","PixelValue","PixelValuePositions","Placed","Placeholder","PlaceholderReplace","Plain","PlanarAngle","PlanarGraph","PlanarGraphQ","PlanckRadiationLaw","PlaneCurveData","PlanetaryMoonData","PlanetData","PlantData","Play","PlayRange","Plot","Plot3D","Plot3Matrix","PlotDivision","PlotJoined","PlotLabel","PlotLabels","PlotLayout","PlotLegends","PlotMarkers","PlotPoints","PlotRange","PlotRangeClipping","PlotRangeClipPlanesStyle","PlotRangePadding","PlotRegion","PlotStyle","PlotTheme","Pluralize","Plus","PlusMinus","Pochhammer","PodStates","PodWidth","Point","Point3DBox","Point3DBoxOptions","PointBox","PointBoxOptions","PointFigureChart","PointLegend","PointSize","PoissonConsulDistribution","PoissonDistribution","PoissonProcess","PoissonWindow","PolarAxes","PolarAxesOrigin","PolarGridLines","PolarPlot","PolarTicks","PoleZeroMarkers","PolyaAeppliDistribution","PolyGamma","Polygon","Polygon3DBox","Polygon3DBoxOptions","PolygonalNumber","PolygonAngle","PolygonBox","PolygonBoxOptions","PolygonCoordinates","PolygonDecomposition","PolygonHoleScale","PolygonIntersections","PolygonScale","Polyhedron","PolyhedronAngle","PolyhedronCoordinates","PolyhedronData","PolyhedronDecomposition","PolyhedronGenus","PolyLog","PolynomialExtendedGCD","PolynomialForm","PolynomialGCD","PolynomialLCM","PolynomialMod","PolynomialQ","PolynomialQuotient","PolynomialQuotientRemainder","PolynomialReduce","PolynomialRemainder","Polynomials","PoolingLayer","PopupMenu","PopupMenuBox","PopupMenuBoxOptions","PopupView","PopupWindow","Position","PositionIndex","Positive","PositiveDefiniteMatrixQ","PositiveIntegers","PositiveRationals","PositiveReals","PositiveSemidefiniteMatrixQ","PossibleZeroQ","Postfix","PostScript","Power","PowerDistribution","PowerExpand","PowerMod","PowerModList","PowerRange","PowerSpectralDensity","PowersRepresentations","PowerSymmetricPolynomial","Precedence","PrecedenceForm","Precedes","PrecedesEqual","PrecedesSlantEqual","PrecedesTilde","Precision","PrecisionGoal","PreDecrement","Predict","PredictionRoot","PredictorFunction","PredictorInformation","PredictorMeasurements","PredictorMeasurementsObject","PreemptProtect","PreferencesPath","Prefix","PreIncrement","Prepend","PrependLayer","PrependTo","PreprocessingRules","PreserveColor","PreserveImageOptions","Previous","PreviousCell","PreviousDate","PriceGraphDistribution","PrimaryPlaceholder","Prime","PrimeNu","PrimeOmega","PrimePi","PrimePowerQ","PrimeQ","Primes","PrimeZetaP","PrimitivePolynomialQ","PrimitiveRoot","PrimitiveRootList","PrincipalComponents","PrincipalValue","Print","PrintableASCIIQ","PrintAction","PrintForm","PrintingCopies","PrintingOptions","PrintingPageRange","PrintingStartingPageNumber","PrintingStyleEnvironment","Printout3D","Printout3DPreviewer","PrintPrecision","PrintTemporary","Prism","PrismBox","PrismBoxOptions","PrivateCellOptions","PrivateEvaluationOptions","PrivateFontOptions","PrivateFrontEndOptions","PrivateKey","PrivateNotebookOptions","PrivatePaths","Probability","ProbabilityDistribution","ProbabilityPlot","ProbabilityPr","ProbabilityScalePlot","ProbitModelFit","ProcessConnection","ProcessDirectory","ProcessEnvironment","Processes","ProcessEstimator","ProcessInformation","ProcessObject","ProcessParameterAssumptions","ProcessParameterQ","ProcessStateDomain","ProcessStatus","ProcessTimeDomain","Product","ProductDistribution","ProductLog","ProgressIndicator","ProgressIndicatorBox","ProgressIndicatorBoxOptions","Projection","Prolog","PromptForm","ProofObject","Properties","Property","PropertyList","PropertyValue","Proportion","Proportional","Protect","Protected","ProteinData","Pruning","PseudoInverse","PsychrometricPropertyData","PublicKey","PublisherID","PulsarData","PunctuationCharacter","Purple","Put","PutAppend","Pyramid","PyramidBox","PyramidBoxOptions","QBinomial","QFactorial","QGamma","QHypergeometricPFQ","QnDispersion","QPochhammer","QPolyGamma","QRDecomposition","QuadraticIrrationalQ","QuadraticOptimization","Quantile","QuantilePlot","Quantity","QuantityArray","QuantityDistribution","QuantityForm","QuantityMagnitude","QuantityQ","QuantityUnit","QuantityVariable","QuantityVariableCanonicalUnit","QuantityVariableDimensions","QuantityVariableIdentifier","QuantityVariablePhysicalQuantity","Quartics","QuartileDeviation","Quartiles","QuartileSkewness","Query","QueueingNetworkProcess","QueueingProcess","QueueProperties","Quiet","Quit","Quotient","QuotientRemainder","RadialGradientImage","RadialityCentrality","RadicalBox","RadicalBoxOptions","RadioButton","RadioButtonBar","RadioButtonBox","RadioButtonBoxOptions","Radon","RadonTransform","RamanujanTau","RamanujanTauL","RamanujanTauTheta","RamanujanTauZ","Ramp","Random","RandomChoice","RandomColor","RandomComplex","RandomEntity","RandomFunction","RandomGeoPosition","RandomGraph","RandomImage","RandomInstance","RandomInteger","RandomPermutation","RandomPoint","RandomPolygon","RandomPolyhedron","RandomPrime","RandomReal","RandomSample","RandomSeed","RandomSeeding","RandomVariate","RandomWalkProcess","RandomWord","Range","RangeFilter","RangeSpecification","RankedMax","RankedMin","RarerProbability","Raster","Raster3D","Raster3DBox","Raster3DBoxOptions","RasterArray","RasterBox","RasterBoxOptions","Rasterize","RasterSize","Rational","RationalFunctions","Rationalize","Rationals","Ratios","RawArray","RawBoxes","RawData","RawMedium","RayleighDistribution","Re","Read","ReadByteArray","ReadLine","ReadList","ReadProtected","ReadString","Real","RealAbs","RealBlockDiagonalForm","RealDigits","RealExponent","Reals","RealSign","Reap","RebuildPacletData","RecognitionPrior","RecognitionThreshold","Record","RecordLists","RecordSeparators","Rectangle","RectangleBox","RectangleBoxOptions","RectangleChart","RectangleChart3D","RectangularRepeatingElement","RecurrenceFilter","RecurrenceTable","RecurringDigitsForm","Red","Reduce","RefBox","ReferenceLineStyle","ReferenceMarkers","ReferenceMarkerStyle","Refine","ReflectionMatrix","ReflectionTransform","Refresh","RefreshRate","Region","RegionBinarize","RegionBoundary","RegionBoundaryStyle","RegionBounds","RegionCentroid","RegionDifference","RegionDimension","RegionDisjoint","RegionDistance","RegionDistanceFunction","RegionEmbeddingDimension","RegionEqual","RegionFillingStyle","RegionFunction","RegionImage","RegionIntersection","RegionMeasure","RegionMember","RegionMemberFunction","RegionMoment","RegionNearest","RegionNearestFunction","RegionPlot","RegionPlot3D","RegionProduct","RegionQ","RegionResize","RegionSize","RegionSymmetricDifference","RegionUnion","RegionWithin","RegisterExternalEvaluator","RegularExpression","Regularization","RegularlySampledQ","RegularPolygon","ReIm","ReImLabels","ReImPlot","ReImStyle","Reinstall","RelationalDatabase","RelationGraph","Release","ReleaseHold","ReliabilityDistribution","ReliefImage","ReliefPlot","RemoteAuthorizationCaching","RemoteConnect","RemoteConnectionObject","RemoteFile","RemoteRun","RemoteRunProcess","Remove","RemoveAlphaChannel","RemoveAsynchronousTask","RemoveAudioStream","RemoveBackground","RemoveChannelListener","RemoveChannelSubscribers","Removed","RemoveDiacritics","RemoveInputStreamMethod","RemoveOutputStreamMethod","RemoveProperty","RemoveScheduledTask","RemoveUsers","RemoveVideoStream","RenameDirectory","RenameFile","RenderAll","RenderingOptions","RenewalProcess","RenkoChart","RepairMesh","Repeated","RepeatedNull","RepeatedString","RepeatedTiming","RepeatingElement","Replace","ReplaceAll","ReplaceHeldPart","ReplaceImageValue","ReplaceList","ReplacePart","ReplacePixelValue","ReplaceRepeated","ReplicateLayer","RequiredPhysicalQuantities","Resampling","ResamplingAlgorithmData","ResamplingMethod","Rescale","RescalingTransform","ResetDirectory","ResetMenusPacket","ResetScheduledTask","ReshapeLayer","Residue","ResizeLayer","Resolve","ResourceAcquire","ResourceData","ResourceFunction","ResourceObject","ResourceRegister","ResourceRemove","ResourceSearch","ResourceSubmissionObject","ResourceSubmit","ResourceSystemBase","ResourceSystemPath","ResourceUpdate","ResourceVersion","ResponseForm","Rest","RestartInterval","Restricted","Resultant","ResumePacket","Return","ReturnEntersInput","ReturnExpressionPacket","ReturnInputFormPacket","ReturnPacket","ReturnReceiptFunction","ReturnTextPacket","Reverse","ReverseApplied","ReverseBiorthogonalSplineWavelet","ReverseElement","ReverseEquilibrium","ReverseGraph","ReverseSort","ReverseSortBy","ReverseUpEquilibrium","RevolutionAxis","RevolutionPlot3D","RGBColor","RiccatiSolve","RiceDistribution","RidgeFilter","RiemannR","RiemannSiegelTheta","RiemannSiegelZ","RiemannXi","Riffle","Right","RightArrow","RightArrowBar","RightArrowLeftArrow","RightComposition","RightCosetRepresentative","RightDownTeeVector","RightDownVector","RightDownVectorBar","RightTee","RightTeeArrow","RightTeeVector","RightTriangle","RightTriangleBar","RightTriangleEqual","RightUpDownVector","RightUpTeeVector","RightUpVector","RightUpVectorBar","RightVector","RightVectorBar","RiskAchievementImportance","RiskReductionImportance","RogersTanimotoDissimilarity","RollPitchYawAngles","RollPitchYawMatrix","RomanNumeral","Root","RootApproximant","RootIntervals","RootLocusPlot","RootMeanSquare","RootOfUnityQ","RootReduce","Roots","RootSum","Rotate","RotateLabel","RotateLeft","RotateRight","RotationAction","RotationBox","RotationBoxOptions","RotationMatrix","RotationTransform","Round","RoundImplies","RoundingRadius","Row","RowAlignments","RowBackgrounds","RowBox","RowHeights","RowLines","RowMinHeight","RowReduce","RowsEqual","RowSpacings","RSolve","RSolveValue","RudinShapiro","RudvalisGroupRu","Rule","RuleCondition","RuleDelayed","RuleForm","RulePlot","RulerUnits","Run","RunProcess","RunScheduledTask","RunThrough","RuntimeAttributes","RuntimeOptions","RussellRaoDissimilarity","SameQ","SameTest","SameTestProperties","SampledEntityClass","SampleDepth","SampledSoundFunction","SampledSoundList","SampleRate","SamplingPeriod","SARIMAProcess","SARMAProcess","SASTriangle","SatelliteData","SatisfiabilityCount","SatisfiabilityInstances","SatisfiableQ","Saturday","Save","Saveable","SaveAutoDelete","SaveConnection","SaveDefinitions","SavitzkyGolayMatrix","SawtoothWave","Scale","Scaled","ScaleDivisions","ScaledMousePosition","ScaleOrigin","ScalePadding","ScaleRanges","ScaleRangeStyle","ScalingFunctions","ScalingMatrix","ScalingTransform","Scan","ScheduledTask","ScheduledTaskActiveQ","ScheduledTaskInformation","ScheduledTaskInformationData","ScheduledTaskObject","ScheduledTasks","SchurDecomposition","ScientificForm","ScientificNotationThreshold","ScorerGi","ScorerGiPrime","ScorerHi","ScorerHiPrime","ScreenRectangle","ScreenStyleEnvironment","ScriptBaselineShifts","ScriptForm","ScriptLevel","ScriptMinSize","ScriptRules","ScriptSizeMultipliers","Scrollbars","ScrollingOptions","ScrollPosition","SearchAdjustment","SearchIndexObject","SearchIndices","SearchQueryString","SearchResultObject","Sec","Sech","SechDistribution","SecondOrderConeOptimization","SectionGrouping","SectorChart","SectorChart3D","SectorOrigin","SectorSpacing","SecuredAuthenticationKey","SecuredAuthenticationKeys","SeedRandom","Select","Selectable","SelectComponents","SelectedCells","SelectedNotebook","SelectFirst","Selection","SelectionAnimate","SelectionCell","SelectionCellCreateCell","SelectionCellDefaultStyle","SelectionCellParentStyle","SelectionCreateCell","SelectionDebuggerTag","SelectionDuplicateCell","SelectionEvaluate","SelectionEvaluateCreateCell","SelectionMove","SelectionPlaceholder","SelectionSetStyle","SelectWithContents","SelfLoops","SelfLoopStyle","SemanticImport","SemanticImportString","SemanticInterpretation","SemialgebraicComponentInstances","SemidefiniteOptimization","SendMail","SendMessage","Sequence","SequenceAlignment","SequenceAttentionLayer","SequenceCases","SequenceCount","SequenceFold","SequenceFoldList","SequenceForm","SequenceHold","SequenceLastLayer","SequenceMostLayer","SequencePosition","SequencePredict","SequencePredictorFunction","SequenceReplace","SequenceRestLayer","SequenceReverseLayer","SequenceSplit","Series","SeriesCoefficient","SeriesData","SeriesTermGoal","ServiceConnect","ServiceDisconnect","ServiceExecute","ServiceObject","ServiceRequest","ServiceResponse","ServiceSubmit","SessionSubmit","SessionTime","Set","SetAccuracy","SetAlphaChannel","SetAttributes","Setbacks","SetBoxFormNamesPacket","SetCloudDirectory","SetCookies","SetDelayed","SetDirectory","SetEnvironment","SetEvaluationNotebook","SetFileDate","SetFileLoadingContext","SetNotebookStatusLine","SetOptions","SetOptionsPacket","SetPermissions","SetPrecision","SetProperty","SetSecuredAuthenticationKey","SetSelectedNotebook","SetSharedFunction","SetSharedVariable","SetSpeechParametersPacket","SetStreamPosition","SetSystemModel","SetSystemOptions","Setter","SetterBar","SetterBox","SetterBoxOptions","Setting","SetUsers","SetValue","Shading","Shallow","ShannonWavelet","ShapiroWilkTest","Share","SharingList","Sharpen","ShearingMatrix","ShearingTransform","ShellRegion","ShenCastanMatrix","ShiftedGompertzDistribution","ShiftRegisterSequence","Short","ShortDownArrow","Shortest","ShortestMatch","ShortestPathFunction","ShortLeftArrow","ShortRightArrow","ShortTimeFourier","ShortTimeFourierData","ShortUpArrow","Show","ShowAutoConvert","ShowAutoSpellCheck","ShowAutoStyles","ShowCellBracket","ShowCellLabel","ShowCellTags","ShowClosedCellArea","ShowCodeAssist","ShowContents","ShowControls","ShowCursorTracker","ShowGroupOpenCloseIcon","ShowGroupOpener","ShowInvisibleCharacters","ShowPageBreaks","ShowPredictiveInterface","ShowSelection","ShowShortBoxForm","ShowSpecialCharacters","ShowStringCharacters","ShowSyntaxStyles","ShrinkingDelay","ShrinkWrapBoundingBox","SiderealTime","SiegelTheta","SiegelTukeyTest","SierpinskiCurve","SierpinskiMesh","Sign","Signature","SignedRankTest","SignedRegionDistance","SignificanceLevel","SignPadding","SignTest","SimilarityRules","SimpleGraph","SimpleGraphQ","SimplePolygonQ","SimplePolyhedronQ","Simplex","Simplify","Sin","Sinc","SinghMaddalaDistribution","SingleEvaluation","SingleLetterItalics","SingleLetterStyle","SingularValueDecomposition","SingularValueList","SingularValuePlot","SingularValues","Sinh","SinhIntegral","SinIntegral","SixJSymbol","Skeleton","SkeletonTransform","SkellamDistribution","Skewness","SkewNormalDistribution","SkinStyle","Skip","SliceContourPlot3D","SliceDensityPlot3D","SliceDistribution","SliceVectorPlot3D","Slider","Slider2D","Slider2DBox","Slider2DBoxOptions","SliderBox","SliderBoxOptions","SlideView","Slot","SlotSequence","Small","SmallCircle","Smaller","SmithDecomposition","SmithDelayCompensator","SmithWatermanSimilarity","SmoothDensityHistogram","SmoothHistogram","SmoothHistogram3D","SmoothKernelDistribution","SnDispersion","Snippet","SnubPolyhedron","SocialMediaData","Socket","SocketConnect","SocketListen","SocketListener","SocketObject","SocketOpen","SocketReadMessage","SocketReadyQ","Sockets","SocketWaitAll","SocketWaitNext","SoftmaxLayer","SokalSneathDissimilarity","SolarEclipse","SolarSystemFeatureData","SolidAngle","SolidData","SolidRegionQ","Solve","SolveAlways","SolveDelayed","Sort","SortBy","SortedBy","SortedEntityClass","Sound","SoundAndGraphics","SoundNote","SoundVolume","SourceLink","Sow","Space","SpaceCurveData","SpaceForm","Spacer","Spacings","Span","SpanAdjustments","SpanCharacterRounding","SpanFromAbove","SpanFromBoth","SpanFromLeft","SpanLineThickness","SpanMaxSize","SpanMinSize","SpanningCharacters","SpanSymmetric","SparseArray","SpatialGraphDistribution","SpatialMedian","SpatialTransformationLayer","Speak","SpeakerMatchQ","SpeakTextPacket","SpearmanRankTest","SpearmanRho","SpeciesData","SpecificityGoal","SpectralLineData","Spectrogram","SpectrogramArray","Specularity","SpeechCases","SpeechInterpreter","SpeechRecognize","SpeechSynthesize","SpellingCorrection","SpellingCorrectionList","SpellingDictionaries","SpellingDictionariesPath","SpellingOptions","SpellingSuggestionsPacket","Sphere","SphereBox","SpherePoints","SphericalBesselJ","SphericalBesselY","SphericalHankelH1","SphericalHankelH2","SphericalHarmonicY","SphericalPlot3D","SphericalRegion","SphericalShell","SpheroidalEigenvalue","SpheroidalJoiningFactor","SpheroidalPS","SpheroidalPSPrime","SpheroidalQS","SpheroidalQSPrime","SpheroidalRadialFactor","SpheroidalS1","SpheroidalS1Prime","SpheroidalS2","SpheroidalS2Prime","Splice","SplicedDistribution","SplineClosed","SplineDegree","SplineKnots","SplineWeights","Split","SplitBy","SpokenString","Sqrt","SqrtBox","SqrtBoxOptions","Square","SquaredEuclideanDistance","SquareFreeQ","SquareIntersection","SquareMatrixQ","SquareRepeatingElement","SquaresR","SquareSubset","SquareSubsetEqual","SquareSuperset","SquareSupersetEqual","SquareUnion","SquareWave","SSSTriangle","StabilityMargins","StabilityMarginsStyle","StableDistribution","Stack","StackBegin","StackComplete","StackedDateListPlot","StackedListPlot","StackInhibit","StadiumShape","StandardAtmosphereData","StandardDeviation","StandardDeviationFilter","StandardForm","Standardize","Standardized","StandardOceanData","StandbyDistribution","Star","StarClusterData","StarData","StarGraph","StartAsynchronousTask","StartExternalSession","StartingStepSize","StartOfLine","StartOfString","StartProcess","StartScheduledTask","StartupSound","StartWebSession","StateDimensions","StateFeedbackGains","StateOutputEstimator","StateResponse","StateSpaceModel","StateSpaceRealization","StateSpaceTransform","StateTransformationLinearize","StationaryDistribution","StationaryWaveletPacketTransform","StationaryWaveletTransform","StatusArea","StatusCentrality","StepMonitor","StereochemistryElements","StieltjesGamma","StippleShading","StirlingS1","StirlingS2","StopAsynchronousTask","StoppingPowerData","StopScheduledTask","StrataVariables","StratonovichProcess","StreamColorFunction","StreamColorFunctionScaling","StreamDensityPlot","StreamMarkers","StreamPlot","StreamPoints","StreamPosition","Streams","StreamScale","StreamStyle","String","StringBreak","StringByteCount","StringCases","StringContainsQ","StringCount","StringDelete","StringDrop","StringEndsQ","StringExpression","StringExtract","StringForm","StringFormat","StringFreeQ","StringInsert","StringJoin","StringLength","StringMatchQ","StringPadLeft","StringPadRight","StringPart","StringPartition","StringPosition","StringQ","StringRepeat","StringReplace","StringReplaceList","StringReplacePart","StringReverse","StringRiffle","StringRotateLeft","StringRotateRight","StringSkeleton","StringSplit","StringStartsQ","StringTake","StringTemplate","StringToByteArray","StringToStream","StringTrim","StripBoxes","StripOnInput","StripWrapperBoxes","StrokeForm","StructuralImportance","StructuredArray","StructuredArrayHeadQ","StructuredSelection","StruveH","StruveL","Stub","StudentTDistribution","Style","StyleBox","StyleBoxAutoDelete","StyleData","StyleDefinitions","StyleForm","StyleHints","StyleKeyMapping","StyleMenuListing","StyleNameDialogSettings","StyleNames","StylePrint","StyleSheetPath","Subdivide","Subfactorial","Subgraph","SubMinus","SubPlus","SubresultantPolynomialRemainders","SubresultantPolynomials","Subresultants","Subscript","SubscriptBox","SubscriptBoxOptions","Subscripted","Subsequences","Subset","SubsetCases","SubsetCount","SubsetEqual","SubsetMap","SubsetPosition","SubsetQ","SubsetReplace","Subsets","SubStar","SubstitutionSystem","Subsuperscript","SubsuperscriptBox","SubsuperscriptBoxOptions","SubtitleEncoding","SubtitleTracks","Subtract","SubtractFrom","SubtractSides","SubValues","Succeeds","SucceedsEqual","SucceedsSlantEqual","SucceedsTilde","Success","SuchThat","Sum","SumConvergence","SummationLayer","Sunday","SunPosition","Sunrise","Sunset","SuperDagger","SuperMinus","SupernovaData","SuperPlus","Superscript","SuperscriptBox","SuperscriptBoxOptions","Superset","SupersetEqual","SuperStar","Surd","SurdForm","SurfaceAppearance","SurfaceArea","SurfaceColor","SurfaceData","SurfaceGraphics","SurvivalDistribution","SurvivalFunction","SurvivalModel","SurvivalModelFit","SuspendPacket","SuzukiDistribution","SuzukiGroupSuz","SwatchLegend","Switch","Symbol","SymbolName","SymletWavelet","Symmetric","SymmetricGroup","SymmetricKey","SymmetricMatrixQ","SymmetricPolynomial","SymmetricReduction","Symmetrize","SymmetrizedArray","SymmetrizedArrayRules","SymmetrizedDependentComponents","SymmetrizedIndependentComponents","SymmetrizedReplacePart","SynchronousInitialization","SynchronousUpdating","Synonyms","Syntax","SyntaxForm","SyntaxInformation","SyntaxLength","SyntaxPacket","SyntaxQ","SynthesizeMissingValues","SystemCredential","SystemCredentialData","SystemCredentialKey","SystemCredentialKeys","SystemCredentialStoreObject","SystemDialogInput","SystemException","SystemGet","SystemHelpPath","SystemInformation","SystemInformationData","SystemInstall","SystemModel","SystemModeler","SystemModelExamples","SystemModelLinearize","SystemModelParametricSimulate","SystemModelPlot","SystemModelProgressReporting","SystemModelReliability","SystemModels","SystemModelSimulate","SystemModelSimulateSensitivity","SystemModelSimulationData","SystemOpen","SystemOptions","SystemProcessData","SystemProcesses","SystemsConnectionsModel","SystemsModelDelay","SystemsModelDelayApproximate","SystemsModelDelete","SystemsModelDimensions","SystemsModelExtract","SystemsModelFeedbackConnect","SystemsModelLabels","SystemsModelLinearity","SystemsModelMerge","SystemsModelOrder","SystemsModelParallelConnect","SystemsModelSeriesConnect","SystemsModelStateFeedbackConnect","SystemsModelVectorRelativeOrders","SystemStub","SystemTest","Tab","TabFilling","Table","TableAlignments","TableDepth","TableDirections","TableForm","TableHeadings","TableSpacing","TableView","TableViewBox","TableViewBoxBackground","TableViewBoxItemSize","TableViewBoxOptions","TabSpacings","TabView","TabViewBox","TabViewBoxOptions","TagBox","TagBoxNote","TagBoxOptions","TaggingRules","TagSet","TagSetDelayed","TagStyle","TagUnset","Take","TakeDrop","TakeLargest","TakeLargestBy","TakeList","TakeSmallest","TakeSmallestBy","TakeWhile","Tally","Tan","Tanh","TargetDevice","TargetFunctions","TargetSystem","TargetUnits","TaskAbort","TaskExecute","TaskObject","TaskRemove","TaskResume","Tasks","TaskSuspend","TaskWait","TautologyQ","TelegraphProcess","TemplateApply","TemplateArgBox","TemplateBox","TemplateBoxOptions","TemplateEvaluate","TemplateExpression","TemplateIf","TemplateObject","TemplateSequence","TemplateSlot","TemplateSlotSequence","TemplateUnevaluated","TemplateVerbatim","TemplateWith","TemporalData","TemporalRegularity","Temporary","TemporaryVariable","TensorContract","TensorDimensions","TensorExpand","TensorProduct","TensorQ","TensorRank","TensorReduce","TensorSymmetry","TensorTranspose","TensorWedge","TestID","TestReport","TestReportObject","TestResultObject","Tetrahedron","TetrahedronBox","TetrahedronBoxOptions","TeXForm","TeXSave","Text","Text3DBox","Text3DBoxOptions","TextAlignment","TextBand","TextBoundingBox","TextBox","TextCases","TextCell","TextClipboardType","TextContents","TextData","TextElement","TextForm","TextGrid","TextJustification","TextLine","TextPacket","TextParagraph","TextPosition","TextRecognize","TextSearch","TextSearchReport","TextSentences","TextString","TextStructure","TextStyle","TextTranslation","Texture","TextureCoordinateFunction","TextureCoordinateScaling","TextWords","Therefore","ThermodynamicData","ThermometerGauge","Thick","Thickness","Thin","Thinning","ThisLink","ThompsonGroupTh","Thread","ThreadingLayer","ThreeJSymbol","Threshold","Through","Throw","ThueMorse","Thumbnail","Thursday","Ticks","TicksStyle","TideData","Tilde","TildeEqual","TildeFullEqual","TildeTilde","TimeConstrained","TimeConstraint","TimeDirection","TimeFormat","TimeGoal","TimelinePlot","TimeObject","TimeObjectQ","TimeRemaining","Times","TimesBy","TimeSeries","TimeSeriesAggregate","TimeSeriesForecast","TimeSeriesInsert","TimeSeriesInvertibility","TimeSeriesMap","TimeSeriesMapThread","TimeSeriesModel","TimeSeriesModelFit","TimeSeriesResample","TimeSeriesRescale","TimeSeriesShift","TimeSeriesThread","TimeSeriesWindow","TimeUsed","TimeValue","TimeWarpingCorrespondence","TimeWarpingDistance","TimeZone","TimeZoneConvert","TimeZoneOffset","Timing","Tiny","TitleGrouping","TitsGroupT","ToBoxes","ToCharacterCode","ToColor","ToContinuousTimeModel","ToDate","Today","ToDiscreteTimeModel","ToEntity","ToeplitzMatrix","ToExpression","ToFileName","Together","Toggle","ToggleFalse","Toggler","TogglerBar","TogglerBox","TogglerBoxOptions","ToHeldExpression","ToInvertibleTimeSeries","TokenWords","Tolerance","ToLowerCase","Tomorrow","ToNumberField","TooBig","Tooltip","TooltipBox","TooltipBoxOptions","TooltipDelay","TooltipStyle","ToonShading","Top","TopHatTransform","ToPolarCoordinates","TopologicalSort","ToRadicals","ToRules","ToSphericalCoordinates","ToString","Total","TotalHeight","TotalLayer","TotalVariationFilter","TotalWidth","TouchPosition","TouchscreenAutoZoom","TouchscreenControlPlacement","ToUpperCase","Tr","Trace","TraceAbove","TraceAction","TraceBackward","TraceDepth","TraceDialog","TraceForward","TraceInternal","TraceLevel","TraceOff","TraceOn","TraceOriginal","TracePrint","TraceScan","TrackedSymbols","TrackingFunction","TracyWidomDistribution","TradingChart","TraditionalForm","TraditionalFunctionNotation","TraditionalNotation","TraditionalOrder","TrainingProgressCheckpointing","TrainingProgressFunction","TrainingProgressMeasurements","TrainingProgressReporting","TrainingStoppingCriterion","TrainingUpdateSchedule","TransferFunctionCancel","TransferFunctionExpand","TransferFunctionFactor","TransferFunctionModel","TransferFunctionPoles","TransferFunctionTransform","TransferFunctionZeros","TransformationClass","TransformationFunction","TransformationFunctions","TransformationMatrix","TransformedDistribution","TransformedField","TransformedProcess","TransformedRegion","TransitionDirection","TransitionDuration","TransitionEffect","TransitiveClosureGraph","TransitiveReductionGraph","Translate","TranslationOptions","TranslationTransform","Transliterate","Transparent","TransparentColor","Transpose","TransposeLayer","TrapSelection","TravelDirections","TravelDirectionsData","TravelDistance","TravelDistanceList","TravelMethod","TravelTime","TreeForm","TreeGraph","TreeGraphQ","TreePlot","TrendStyle","Triangle","TriangleCenter","TriangleConstruct","TriangleMeasurement","TriangleWave","TriangularDistribution","TriangulateMesh","Trig","TrigExpand","TrigFactor","TrigFactorList","Trigger","TrigReduce","TrigToExp","TrimmedMean","TrimmedVariance","TropicalStormData","True","TrueQ","TruncatedDistribution","TruncatedPolyhedron","TsallisQExponentialDistribution","TsallisQGaussianDistribution","TTest","Tube","TubeBezierCurveBox","TubeBezierCurveBoxOptions","TubeBox","TubeBoxOptions","TubeBSplineCurveBox","TubeBSplineCurveBoxOptions","Tuesday","TukeyLambdaDistribution","TukeyWindow","TunnelData","Tuples","TuranGraph","TuringMachine","TuttePolynomial","TwoWayRule","Typed","TypeSpecifier","UnateQ","Uncompress","UnconstrainedParameters","Undefined","UnderBar","Underflow","Underlined","Underoverscript","UnderoverscriptBox","UnderoverscriptBoxOptions","Underscript","UnderscriptBox","UnderscriptBoxOptions","UnderseaFeatureData","UndirectedEdge","UndirectedGraph","UndirectedGraphQ","UndoOptions","UndoTrackedVariables","Unequal","UnequalTo","Unevaluated","UniformDistribution","UniformGraphDistribution","UniformPolyhedron","UniformSumDistribution","Uninstall","Union","UnionedEntityClass","UnionPlus","Unique","UnitaryMatrixQ","UnitBox","UnitConvert","UnitDimensions","Unitize","UnitRootTest","UnitSimplify","UnitStep","UnitSystem","UnitTriangle","UnitVector","UnitVectorLayer","UnityDimensions","UniverseModelData","UniversityData","UnixTime","Unprotect","UnregisterExternalEvaluator","UnsameQ","UnsavedVariables","Unset","UnsetShared","UntrackedVariables","Up","UpArrow","UpArrowBar","UpArrowDownArrow","Update","UpdateDynamicObjects","UpdateDynamicObjectsSynchronous","UpdateInterval","UpdatePacletSites","UpdateSearchIndex","UpDownArrow","UpEquilibrium","UpperCaseQ","UpperLeftArrow","UpperRightArrow","UpperTriangularize","UpperTriangularMatrixQ","Upsample","UpSet","UpSetDelayed","UpTee","UpTeeArrow","UpTo","UpValues","URL","URLBuild","URLDecode","URLDispatcher","URLDownload","URLDownloadSubmit","URLEncode","URLExecute","URLExpand","URLFetch","URLFetchAsynchronous","URLParse","URLQueryDecode","URLQueryEncode","URLRead","URLResponseTime","URLSave","URLSaveAsynchronous","URLShorten","URLSubmit","UseGraphicsRange","UserDefinedWavelet","Using","UsingFrontEnd","UtilityFunction","V2Get","ValenceErrorHandling","ValidationLength","ValidationSet","Value","ValueBox","ValueBoxOptions","ValueDimensions","ValueForm","ValuePreprocessingFunction","ValueQ","Values","ValuesData","Variables","Variance","VarianceEquivalenceTest","VarianceEstimatorFunction","VarianceGammaDistribution","VarianceTest","VectorAngle","VectorAround","VectorAspectRatio","VectorColorFunction","VectorColorFunctionScaling","VectorDensityPlot","VectorGlyphData","VectorGreater","VectorGreaterEqual","VectorLess","VectorLessEqual","VectorMarkers","VectorPlot","VectorPlot3D","VectorPoints","VectorQ","VectorRange","Vectors","VectorScale","VectorScaling","VectorSizes","VectorStyle","Vee","Verbatim","Verbose","VerboseConvertToPostScriptPacket","VerificationTest","VerifyConvergence","VerifyDerivedKey","VerifyDigitalSignature","VerifyFileSignature","VerifyInterpretation","VerifySecurityCertificates","VerifySolutions","VerifyTestAssumptions","Version","VersionedPreferences","VersionNumber","VertexAdd","VertexCapacity","VertexColors","VertexComponent","VertexConnectivity","VertexContract","VertexCoordinateRules","VertexCoordinates","VertexCorrelationSimilarity","VertexCosineSimilarity","VertexCount","VertexCoverQ","VertexDataCoordinates","VertexDegree","VertexDelete","VertexDiceSimilarity","VertexEccentricity","VertexInComponent","VertexInDegree","VertexIndex","VertexJaccardSimilarity","VertexLabeling","VertexLabels","VertexLabelStyle","VertexList","VertexNormals","VertexOutComponent","VertexOutDegree","VertexQ","VertexRenderingFunction","VertexReplace","VertexShape","VertexShapeFunction","VertexSize","VertexStyle","VertexTextureCoordinates","VertexWeight","VertexWeightedGraphQ","Vertical","VerticalBar","VerticalForm","VerticalGauge","VerticalSeparator","VerticalSlider","VerticalTilde","Video","VideoEncoding","VideoExtractFrames","VideoFrameList","VideoFrameMap","VideoPause","VideoPlay","VideoQ","VideoStop","VideoStream","VideoStreams","VideoTimeSeries","VideoTracks","VideoTrim","ViewAngle","ViewCenter","ViewMatrix","ViewPoint","ViewPointSelectorSettings","ViewPort","ViewProjection","ViewRange","ViewVector","ViewVertical","VirtualGroupData","Visible","VisibleCell","VoiceStyleData","VoigtDistribution","VolcanoData","Volume","VonMisesDistribution","VoronoiMesh","WaitAll","WaitAsynchronousTask","WaitNext","WaitUntil","WakebyDistribution","WalleniusHypergeometricDistribution","WaringYuleDistribution","WarpingCorrespondence","WarpingDistance","WatershedComponents","WatsonUSquareTest","WattsStrogatzGraphDistribution","WaveletBestBasis","WaveletFilterCoefficients","WaveletImagePlot","WaveletListPlot","WaveletMapIndexed","WaveletMatrixPlot","WaveletPhi","WaveletPsi","WaveletScale","WaveletScalogram","WaveletThreshold","WeaklyConnectedComponents","WeaklyConnectedGraphComponents","WeaklyConnectedGraphQ","WeakStationarity","WeatherData","WeatherForecastData","WebAudioSearch","WebElementObject","WeberE","WebExecute","WebImage","WebImageSearch","WebSearch","WebSessionObject","WebSessions","WebWindowObject","Wedge","Wednesday","WeibullDistribution","WeierstrassE1","WeierstrassE2","WeierstrassE3","WeierstrassEta1","WeierstrassEta2","WeierstrassEta3","WeierstrassHalfPeriods","WeierstrassHalfPeriodW1","WeierstrassHalfPeriodW2","WeierstrassHalfPeriodW3","WeierstrassInvariantG2","WeierstrassInvariantG3","WeierstrassInvariants","WeierstrassP","WeierstrassPPrime","WeierstrassSigma","WeierstrassZeta","WeightedAdjacencyGraph","WeightedAdjacencyMatrix","WeightedData","WeightedGraphQ","Weights","WelchWindow","WheelGraph","WhenEvent","Which","While","White","WhiteNoiseProcess","WhitePoint","Whitespace","WhitespaceCharacter","WhittakerM","WhittakerW","WienerFilter","WienerProcess","WignerD","WignerSemicircleDistribution","WikidataData","WikidataSearch","WikipediaData","WikipediaSearch","WilksW","WilksWTest","WindDirectionData","WindingCount","WindingPolygon","WindowClickSelect","WindowElements","WindowFloating","WindowFrame","WindowFrameElements","WindowMargins","WindowMovable","WindowOpacity","WindowPersistentStyles","WindowSelected","WindowSize","WindowStatusArea","WindowTitle","WindowToolbars","WindowWidth","WindSpeedData","WindVectorData","WinsorizedMean","WinsorizedVariance","WishartMatrixDistribution","With","WolframAlpha","WolframAlphaDate","WolframAlphaQuantity","WolframAlphaResult","WolframLanguageData","Word","WordBoundary","WordCharacter","WordCloud","WordCount","WordCounts","WordData","WordDefinition","WordFrequency","WordFrequencyData","WordList","WordOrientation","WordSearch","WordSelectionFunction","WordSeparators","WordSpacings","WordStem","WordTranslation","WorkingPrecision","WrapAround","Write","WriteLine","WriteString","Wronskian","XMLElement","XMLObject","XMLTemplate","Xnor","Xor","XYZColor","Yellow","Yesterday","YuleDissimilarity","ZernikeR","ZeroSymmetric","ZeroTest","ZeroWidthTimes","Zeta","ZetaZero","ZIPCodeData","ZipfDistribution","ZoomCenter","ZoomFactor","ZTest","ZTransform","$Aborted","$ActivationGroupID","$ActivationKey","$ActivationUserRegistered","$AddOnsDirectory","$AllowDataUpdates","$AllowExternalChannelFunctions","$AllowInternet","$AssertFunction","$Assumptions","$AsynchronousTask","$AudioDecoders","$AudioEncoders","$AudioInputDevices","$AudioOutputDevices","$BaseDirectory","$BasePacletsDirectory","$BatchInput","$BatchOutput","$BlockchainBase","$BoxForms","$ByteOrdering","$CacheBaseDirectory","$Canceled","$ChannelBase","$CharacterEncoding","$CharacterEncodings","$CloudAccountName","$CloudBase","$CloudConnected","$CloudConnection","$CloudCreditsAvailable","$CloudEvaluation","$CloudExpressionBase","$CloudObjectNameFormat","$CloudObjectURLType","$CloudRootDirectory","$CloudSymbolBase","$CloudUserID","$CloudUserUUID","$CloudVersion","$CloudVersionNumber","$CloudWolframEngineVersionNumber","$CommandLine","$CompilationTarget","$ConditionHold","$ConfiguredKernels","$Context","$ContextPath","$ControlActiveSetting","$Cookies","$CookieStore","$CreationDate","$CurrentLink","$CurrentTask","$CurrentWebSession","$DataStructures","$DateStringFormat","$DefaultAudioInputDevice","$DefaultAudioOutputDevice","$DefaultFont","$DefaultFrontEnd","$DefaultImagingDevice","$DefaultLocalBase","$DefaultMailbox","$DefaultNetworkInterface","$DefaultPath","$DefaultProxyRules","$DefaultSystemCredentialStore","$Display","$DisplayFunction","$DistributedContexts","$DynamicEvaluation","$Echo","$EmbedCodeEnvironments","$EmbeddableServices","$EntityStores","$Epilog","$EvaluationCloudBase","$EvaluationCloudObject","$EvaluationEnvironment","$ExportFormats","$ExternalIdentifierTypes","$ExternalStorageBase","$Failed","$FinancialDataSource","$FontFamilies","$FormatType","$FrontEnd","$FrontEndSession","$GeoEntityTypes","$GeoLocation","$GeoLocationCity","$GeoLocationCountry","$GeoLocationPrecision","$GeoLocationSource","$HistoryLength","$HomeDirectory","$HTMLExportRules","$HTTPCookies","$HTTPRequest","$IgnoreEOF","$ImageFormattingWidth","$ImageResolution","$ImagingDevice","$ImagingDevices","$ImportFormats","$IncomingMailSettings","$InitialDirectory","$Initialization","$InitializationContexts","$Input","$InputFileName","$InputStreamMethods","$Inspector","$InstallationDate","$InstallationDirectory","$InterfaceEnvironment","$InterpreterTypes","$IterationLimit","$KernelCount","$KernelID","$Language","$LaunchDirectory","$LibraryPath","$LicenseExpirationDate","$LicenseID","$LicenseProcesses","$LicenseServer","$LicenseSubprocesses","$LicenseType","$Line","$Linked","$LinkSupported","$LoadedFiles","$LocalBase","$LocalSymbolBase","$MachineAddresses","$MachineDomain","$MachineDomains","$MachineEpsilon","$MachineID","$MachineName","$MachinePrecision","$MachineType","$MaxExtraPrecision","$MaxLicenseProcesses","$MaxLicenseSubprocesses","$MaxMachineNumber","$MaxNumber","$MaxPiecewiseCases","$MaxPrecision","$MaxRootDegree","$MessageGroups","$MessageList","$MessagePrePrint","$Messages","$MinMachineNumber","$MinNumber","$MinorReleaseNumber","$MinPrecision","$MobilePhone","$ModuleNumber","$NetworkConnected","$NetworkInterfaces","$NetworkLicense","$NewMessage","$NewSymbol","$NotebookInlineStorageLimit","$Notebooks","$NoValue","$NumberMarks","$Off","$OperatingSystem","$Output","$OutputForms","$OutputSizeLimit","$OutputStreamMethods","$Packages","$ParentLink","$ParentProcessID","$PasswordFile","$PatchLevelID","$Path","$PathnameSeparator","$PerformanceGoal","$Permissions","$PermissionsGroupBase","$PersistenceBase","$PersistencePath","$PipeSupported","$PlotTheme","$Post","$Pre","$PreferencesDirectory","$PreInitialization","$PrePrint","$PreRead","$PrintForms","$PrintLiteral","$Printout3DPreviewer","$ProcessID","$ProcessorCount","$ProcessorType","$ProductInformation","$ProgramName","$PublisherID","$RandomState","$RecursionLimit","$RegisteredDeviceClasses","$RegisteredUserName","$ReleaseNumber","$RequesterAddress","$RequesterWolframID","$RequesterWolframUUID","$RootDirectory","$ScheduledTask","$ScriptCommandLine","$ScriptInputString","$SecuredAuthenticationKeyTokens","$ServiceCreditsAvailable","$Services","$SessionID","$SetParentLink","$SharedFunctions","$SharedVariables","$SoundDisplay","$SoundDisplayFunction","$SourceLink","$SSHAuthentication","$SubtitleDecoders","$SubtitleEncoders","$SummaryBoxDataSizeLimit","$SuppressInputFormHeads","$SynchronousEvaluation","$SyntaxHandler","$System","$SystemCharacterEncoding","$SystemCredentialStore","$SystemID","$SystemMemory","$SystemShell","$SystemTimeZone","$SystemWordLength","$TemplatePath","$TemporaryDirectory","$TemporaryPrefix","$TestFileName","$TextStyle","$TimedOut","$TimeUnit","$TimeZone","$TimeZoneEntity","$TopDirectory","$TraceOff","$TraceOn","$TracePattern","$TracePostAction","$TracePreAction","$UnitSystem","$Urgent","$UserAddOnsDirectory","$UserAgentLanguages","$UserAgentMachine","$UserAgentName","$UserAgentOperatingSystem","$UserAgentString","$UserAgentVersion","$UserBaseDirectory","$UserBasePacletsDirectory","$UserDocumentsDirectory","$Username","$UserName","$UserURLBase","$Version","$VersionNumber","$VideoDecoders","$VideoEncoders","$VoiceStyles","$WolframDocumentsDirectory","$WolframID","$WolframUUID"]
;function t(e){return e?"string"==typeof e?e:e.source:null}function i(e){
return o("(",e,")?")}function o(...e){return e.map((e=>t(e))).join("")}
function a(...e){return"("+e.map((e=>t(e))).join("|")+")"}return t=>{
const n=a(o(/([2-9]|[1-2]\d|[3][0-5])\^\^/,/(\w*\.\w+|\w+\.\w*|\w+)/),/(\d*\.\d+|\d+\.\d*|\d+)/),r={
className:"number",relevance:0,
begin:o(n,i(a(/``[+-]?(\d*\.\d+|\d+\.\d*|\d+)/,/`([+-]?(\d*\.\d+|\d+\.\d*|\d+))?/)),i(/\*\^[+-]?\d+/))
},l=/[a-zA-Z$][a-zA-Z0-9$]*/,s=new Set(e),c={variants:[{
className:"builtin-symbol",begin:l,"on:begin":(e,t)=>{
s.has(e[0])||t.ignoreMatch()}},{className:"symbol",relevance:0,begin:l}]},u={
className:"message-name",relevance:0,begin:o("::",l)};return{name:"Mathematica",
aliases:["mma","wl"],classNameAliases:{brace:"punctuation",pattern:"type",
slot:"type",symbol:"variable","named-character":"variable",
"builtin-symbol":"built_in","message-name":"string"},
contains:[t.COMMENT(/\(\*/,/\*\)/,{contains:["self"]}),{className:"pattern",
relevance:0,begin:/([a-zA-Z$][a-zA-Z0-9$]*)?_+([a-zA-Z$][a-zA-Z0-9$]*)?/},{
className:"slot",relevance:0,begin:/#[a-zA-Z$][a-zA-Z0-9$]*|#+[0-9]?/},u,c,{
className:"named-character",begin:/\\\[[$a-zA-Z][$a-zA-Z0-9]+\]/
},t.QUOTE_STRING_MODE,r,{className:"operator",relevance:0,
begin:/[+\-*/,;.:@~=><&|_`'^?!%]+/},{className:"brace",relevance:0,
begin:/[[\](){}]/}]}}})());hljs.registerLanguage("objectivec",(()=>{"use strict";return e=>{
const n=/[a-zA-Z@][a-zA-Z0-9_]*/,_={$pattern:n,
keyword:"@interface @class @protocol @implementation"};return{
name:"Objective-C",aliases:["mm","objc","obj-c","obj-c++","objective-c++"],
keywords:{$pattern:n,
keyword:"int float while char export sizeof typedef const struct for union unsigned long volatile static bool mutable if do return goto void enum else break extern asm case short default double register explicit signed typename this switch continue wchar_t inline readonly assign readwrite self @synchronized id typeof nonatomic super unichar IBOutlet IBAction strong weak copy in out inout bycopy byref oneway __strong __weak __block __autoreleasing @private @protected @public @try @property @end @throw @catch @finally @autoreleasepool @synthesize @dynamic @selector @optional @required @encode @package @import @defs @compatibility_alias __bridge __bridge_transfer __bridge_retained __bridge_retain __covariant __contravariant __kindof _Nonnull _Nullable _Null_unspecified __FUNCTION__ __PRETTY_FUNCTION__ __attribute__ getter setter retain unsafe_unretained nonnull nullable null_unspecified null_resettable class instancetype NS_DESIGNATED_INITIALIZER NS_UNAVAILABLE NS_REQUIRES_SUPER NS_RETURNS_INNER_POINTER NS_INLINE NS_AVAILABLE NS_DEPRECATED NS_ENUM NS_OPTIONS NS_SWIFT_UNAVAILABLE NS_ASSUME_NONNULL_BEGIN NS_ASSUME_NONNULL_END NS_REFINED_FOR_SWIFT NS_SWIFT_NAME NS_SWIFT_NOTHROW NS_DURING NS_HANDLER NS_ENDHANDLER NS_VALUERETURN NS_VOIDRETURN",
literal:"false true FALSE TRUE nil YES NO NULL",
built_in:"BOOL dispatch_once_t dispatch_queue_t dispatch_sync dispatch_async dispatch_once"
},illegal:"</",contains:[{className:"built_in",
begin:"\\b(AV|CA|CF|CG|CI|CL|CM|CN|CT|MK|MP|MTK|MTL|NS|SCN|SK|UI|WK|XC)\\w+"
},e.C_LINE_COMMENT_MODE,e.C_BLOCK_COMMENT_MODE,e.C_NUMBER_MODE,e.QUOTE_STRING_MODE,e.APOS_STRING_MODE,{
className:"string",variants:[{begin:'@"',end:'"',illegal:"\\n",
contains:[e.BACKSLASH_ESCAPE]}]},{className:"meta",begin:/#\s*[a-z]+\b/,end:/$/,
keywords:{
"meta-keyword":"if else elif endif define undef warning error line pragma ifdef ifndef include"
},contains:[{begin:/\\\n/,relevance:0},e.inherit(e.QUOTE_STRING_MODE,{
className:"meta-string"}),{className:"meta-string",begin:/<.*?>/,end:/$/,
illegal:"\\n"},e.C_LINE_COMMENT_MODE,e.C_BLOCK_COMMENT_MODE]},{
className:"class",begin:"("+_.keyword.split(" ").join("|")+")\\b",end:/(\{|$)/,
excludeEnd:!0,keywords:_,contains:[e.UNDERSCORE_TITLE_MODE]},{
begin:"\\."+e.UNDERSCORE_IDENT_RE,relevance:0}]}}})());hljs.registerLanguage("http",(()=>{"use strict";function e(...e){
return e.map((e=>{return(n=e)?"string"==typeof n?n:n.source:null;var n
})).join("")}return n=>{const a="HTTP/(2|1\\.[01])",s=[{className:"attribute",
begin:e("^",/[A-Za-z][A-Za-z0-9-]*/,"(?=\\:\\s)"),starts:{contains:[{
className:"punctuation",begin:/: /,relevance:0,starts:{end:"$",relevance:0}}]}
},{begin:"\\n\\n",starts:{subLanguage:[],endsWithParent:!0}}];return{
name:"HTTP",aliases:["https"],illegal:/\S/,contains:[{begin:"^(?="+a+" \\d{3})",
end:/$/,contains:[{className:"meta",begin:a},{className:"number",
begin:"\\b\\d{3}\\b"}],starts:{end:/\b\B/,illegal:/\S/,contains:s}},{
begin:"(?=^[A-Z]+ (.*?) "+a+"$)",end:/$/,contains:[{className:"string",
begin:" ",end:" ",excludeBegin:!0,excludeEnd:!0},{className:"meta",begin:a},{
className:"keyword",begin:"[A-Z]+"}],starts:{end:/\b\B/,illegal:/\S/,contains:s}
}]}}})());hljs.registerLanguage("vbnet",(()=>{"use strict";function e(e){
return e?"string"==typeof e?e:e.source:null}function n(...n){
return n.map((n=>e(n))).join("")}function t(...n){
return"("+n.map((n=>e(n))).join("|")+")"}return e=>{
const a=/\d{1,2}\/\d{1,2}\/\d{4}/,i=/\d{4}-\d{1,2}-\d{1,2}/,s=/(\d|1[012])(:\d+){0,2} *(AM|PM)/,r=/\d{1,2}(:\d{1,2}){1,2}/,o={
className:"literal",variants:[{begin:n(/# */,t(i,a),/ *#/)},{
begin:n(/# */,r,/ *#/)},{begin:n(/# */,s,/ *#/)},{
begin:n(/# */,t(i,a),/ +/,t(s,r),/ *#/)}]},l=e.COMMENT(/'''/,/$/,{contains:[{
className:"doctag",begin:/<\/?/,end:/>/}]}),c=e.COMMENT(null,/$/,{variants:[{
begin:/'/},{begin:/([\t ]|^)REM(?=\s)/}]});return{name:"Visual Basic .NET",
aliases:["vb"],case_insensitive:!0,classNameAliases:{label:"symbol"},keywords:{
keyword:"addhandler alias aggregate ansi as async assembly auto binary by byref byval call case catch class compare const continue custom declare default delegate dim distinct do each equals else elseif end enum erase error event exit explicit finally for friend from function get global goto group handles if implements imports in inherits interface into iterator join key let lib loop me mid module mustinherit mustoverride mybase myclass namespace narrowing new next notinheritable notoverridable of off on operator option optional order overloads overridable overrides paramarray partial preserve private property protected public raiseevent readonly redim removehandler resume return select set shadows shared skip static step stop structure strict sub synclock take text then throw to try unicode until using when where while widening with withevents writeonly yield",
built_in:"addressof and andalso await directcast gettype getxmlnamespace is isfalse isnot istrue like mod nameof new not or orelse trycast typeof xor cbool cbyte cchar cdate cdbl cdec cint clng cobj csbyte cshort csng cstr cuint culng cushort",
type:"boolean byte char date decimal double integer long object sbyte short single string uinteger ulong ushort",
literal:"true false nothing"},
illegal:"//|\\{|\\}|endif|gosub|variant|wend|^\\$ ",contains:[{
className:"string",begin:/"(""|[^/n])"C\b/},{className:"string",begin:/"/,
end:/"/,illegal:/\n/,contains:[{begin:/""/}]},o,{className:"number",relevance:0,
variants:[{begin:/\b\d[\d_]*((\.[\d_]+(E[+-]?[\d_]+)?)|(E[+-]?[\d_]+))[RFD@!#]?/
},{begin:/\b\d[\d_]*((U?[SIL])|[%&])?/},{begin:/&H[\dA-F_]+((U?[SIL])|[%&])?/},{
begin:/&O[0-7_]+((U?[SIL])|[%&])?/},{begin:/&B[01_]+((U?[SIL])|[%&])?/}]},{
className:"label",begin:/^\w+:/},l,c,{className:"meta",
begin:/[\t ]*#(const|disable|else|elseif|enable|end|externalsource|if|region)\b/,
end:/$/,keywords:{
"meta-keyword":"const disable else elseif enable end externalsource if region then"
},contains:[c]}]}}})());hljs.registerLanguage("coffeescript",(()=>{"use strict"
;const e=["as","in","of","if","for","while","finally","var","new","function","do","return","void","else","break","catch","instanceof","with","throw","case","default","try","switch","continue","typeof","delete","let","yield","const","class","debugger","async","await","static","import","from","export","extends"],n=["true","false","null","undefined","NaN","Infinity"],a=[].concat(["setInterval","setTimeout","clearInterval","clearTimeout","require","exports","eval","isFinite","isNaN","parseFloat","parseInt","decodeURI","decodeURIComponent","encodeURI","encodeURIComponent","escape","unescape"],["arguments","this","super","console","window","document","localStorage","module","global"],["Intl","DataView","Number","Math","Date","String","RegExp","Object","Function","Boolean","Error","Symbol","Set","Map","WeakSet","WeakMap","Proxy","Reflect","JSON","Promise","Float64Array","Int16Array","Int32Array","Int8Array","Uint16Array","Uint32Array","Float32Array","Array","Uint8Array","Uint8ClampedArray","ArrayBuffer"],["EvalError","InternalError","RangeError","ReferenceError","SyntaxError","TypeError","URIError"])
;return r=>{const t={
keyword:e.concat(["then","unless","until","loop","by","when","and","or","is","isnt","not"]).filter((i=["var","const","let","function","static"],
e=>!i.includes(e))).join(" "),
literal:n.concat(["yes","no","on","off"]).join(" "),
built_in:a.concat(["npm","print"]).join(" ")};var i
;const s="[A-Za-z$_][0-9A-Za-z$_]*",o={className:"subst",begin:/#\{/,end:/\}/,
keywords:t},c=[r.BINARY_NUMBER_MODE,r.inherit(r.C_NUMBER_MODE,{starts:{
end:"(\\s*/)?",relevance:0}}),{className:"string",variants:[{begin:/'''/,
end:/'''/,contains:[r.BACKSLASH_ESCAPE]},{begin:/'/,end:/'/,
contains:[r.BACKSLASH_ESCAPE]},{begin:/"""/,end:/"""/,
contains:[r.BACKSLASH_ESCAPE,o]},{begin:/"/,end:/"/,
contains:[r.BACKSLASH_ESCAPE,o]}]},{className:"regexp",variants:[{begin:"///",
end:"///",contains:[o,r.HASH_COMMENT_MODE]},{begin:"//[gim]{0,3}(?=\\W)",
relevance:0},{begin:/\/(?![ *]).*?(?![\\]).\/[gim]{0,3}(?=\W)/}]},{begin:"@"+s
},{subLanguage:"javascript",excludeBegin:!0,excludeEnd:!0,variants:[{
begin:"```",end:"```"},{begin:"`",end:"`"}]}];o.contains=c
;const l=r.inherit(r.TITLE_MODE,{begin:s}),d="(\\(.*\\)\\s*)?\\B[-=]>",g={
className:"params",begin:"\\([^\\(]",returnBegin:!0,contains:[{begin:/\(/,
end:/\)/,keywords:t,contains:["self"].concat(c)}]};return{name:"CoffeeScript",
aliases:["coffee","cson","iced"],keywords:t,illegal:/\/\*/,
contains:c.concat([r.COMMENT("###","###"),r.HASH_COMMENT_MODE,{
className:"function",begin:"^\\s*"+s+"\\s*=\\s*"+d,end:"[-=]>",returnBegin:!0,
contains:[l,g]},{begin:/[:\(,=]\s*/,relevance:0,contains:[{className:"function",
begin:d,end:"[-=]>",returnBegin:!0,contains:[g]}]},{className:"class",
beginKeywords:"class",end:"$",illegal:/[:="\[\]]/,contains:[{
beginKeywords:"extends",endsWithParent:!0,illegal:/[:="\[\]]/,contains:[l]},l]
},{begin:s+":",end:":",returnBegin:!0,returnEnd:!0,relevance:0}])}}})());hljs.registerLanguage("python",(()=>{"use strict";return e=>{const n={
keyword:"and as assert async await break class continue def del elif else except finally for  from global if import in is lambda nonlocal|10 not or pass raise return try while with yield",
built_in:"__import__ abs all any ascii bin bool breakpoint bytearray bytes callable chr classmethod compile complex delattr dict dir divmod enumerate eval exec filter float format frozenset getattr globals hasattr hash help hex id input int isinstance issubclass iter len list locals map max memoryview min next object oct open ord pow print property range repr reversed round set setattr slice sorted staticmethod str sum super tuple type vars zip",
literal:"__debug__ Ellipsis False None NotImplemented True"},a={
className:"meta",begin:/^(>>>|\.\.\.) /},s={className:"subst",begin:/\{/,
end:/\}/,keywords:n,illegal:/#/},i={begin:/\{\{/,relevance:0},r={
className:"string",contains:[e.BACKSLASH_ESCAPE],variants:[{
begin:/([uU]|[bB]|[rR]|[bB][rR]|[rR][bB])?'''/,end:/'''/,
contains:[e.BACKSLASH_ESCAPE,a],relevance:10},{
begin:/([uU]|[bB]|[rR]|[bB][rR]|[rR][bB])?"""/,end:/"""/,
contains:[e.BACKSLASH_ESCAPE,a],relevance:10},{
begin:/([fF][rR]|[rR][fF]|[fF])'''/,end:/'''/,
contains:[e.BACKSLASH_ESCAPE,a,i,s]},{begin:/([fF][rR]|[rR][fF]|[fF])"""/,
end:/"""/,contains:[e.BACKSLASH_ESCAPE,a,i,s]},{begin:/([uU]|[rR])'/,end:/'/,
relevance:10},{begin:/([uU]|[rR])"/,end:/"/,relevance:10},{
begin:/([bB]|[bB][rR]|[rR][bB])'/,end:/'/},{begin:/([bB]|[bB][rR]|[rR][bB])"/,
end:/"/},{begin:/([fF][rR]|[rR][fF]|[fF])'/,end:/'/,
contains:[e.BACKSLASH_ESCAPE,i,s]},{begin:/([fF][rR]|[rR][fF]|[fF])"/,end:/"/,
contains:[e.BACKSLASH_ESCAPE,i,s]},e.APOS_STRING_MODE,e.QUOTE_STRING_MODE]
},t="[0-9](_?[0-9])*",l=`(\\b(${t}))?\\.(${t})|\\b(${t})\\.`,b={
className:"number",relevance:0,variants:[{
begin:`(\\b(${t})|(${l}))[eE][+-]?(${t})[jJ]?\\b`},{begin:`(${l})[jJ]?`},{
begin:"\\b([1-9](_?[0-9])*|0+(_?0)*)[lLjJ]?\\b"},{
begin:"\\b0[bB](_?[01])+[lL]?\\b"},{begin:"\\b0[oO](_?[0-7])+[lL]?\\b"},{
begin:"\\b0[xX](_?[0-9a-fA-F])+[lL]?\\b"},{begin:`\\b(${t})[jJ]\\b`}]},o={
className:"params",variants:[{begin:/\(\s*\)/,skip:!0,className:null},{
begin:/\(/,end:/\)/,excludeBegin:!0,excludeEnd:!0,keywords:n,
contains:["self",a,b,r,e.HASH_COMMENT_MODE]}]};return s.contains=[r,b,a],{
name:"Python",aliases:["py","gyp","ipython"],keywords:n,
illegal:/(<\/|->|\?)|=>/,contains:[a,b,{begin:/\bself\b/},{beginKeywords:"if",
relevance:0},r,e.HASH_COMMENT_MODE,{variants:[{className:"function",
beginKeywords:"def"},{className:"class",beginKeywords:"class"}],end:/:/,
illegal:/[${=;\n,]/,contains:[e.UNDERSCORE_TITLE_MODE,o,{begin:/->/,
endsWithParent:!0,keywords:"None"}]},{className:"meta",begin:/^[\t ]*@/,
end:/(?=#)|$/,contains:[b,o,r]},{begin:/\b(print|exec)\(/}]}}})());hljs.registerLanguage("python-repl",(()=>{"use strict";return s=>({
aliases:["pycon"],contains:[{className:"meta",starts:{end:/ |$/,starts:{end:"$",
subLanguage:"python"}},variants:[{begin:/^>>>(?=[ ]|$)/},{
begin:/^\.\.\.(?=[ ]|$)/}]}]})})());hljs.registerLanguage("scss",(()=>{"use strict";return e=>{var t="@[a-z-]+",i={
className:"variable",begin:"(\\$[a-zA-Z-][a-zA-Z0-9_-]*)\\b"},r={
className:"number",begin:"#[0-9A-Fa-f]+"}
;return e.CSS_NUMBER_MODE,e.QUOTE_STRING_MODE,
e.APOS_STRING_MODE,e.C_BLOCK_COMMENT_MODE,{name:"SCSS",case_insensitive:!0,
illegal:"[=/|']",contains:[e.C_LINE_COMMENT_MODE,e.C_BLOCK_COMMENT_MODE,{
className:"selector-id",begin:"#[A-Za-z0-9_-]+",relevance:0},{
className:"selector-class",begin:"\\.[A-Za-z0-9_-]+",relevance:0},{
className:"selector-attr",begin:"\\[",end:"\\]",illegal:"$"},{
className:"selector-tag",
begin:"\\b(a|abbr|acronym|address|area|article|aside|audio|b|base|big|blockquote|body|br|button|canvas|caption|cite|code|col|colgroup|command|datalist|dd|del|details|dfn|div|dl|dt|em|embed|fieldset|figcaption|figure|footer|form|frame|frameset|(h[1-6])|head|header|hgroup|hr|html|i|iframe|img|input|ins|kbd|keygen|label|legend|li|link|map|mark|meta|meter|nav|noframes|noscript|object|ol|optgroup|option|output|p|param|pre|progress|q|rp|rt|ruby|samp|script|section|select|small|span|strike|strong|style|sub|sup|table|tbody|td|textarea|tfoot|th|thead|time|title|tr|tt|ul|var|video)\\b",
relevance:0},{className:"selector-pseudo",
begin:":(visited|valid|root|right|required|read-write|read-only|out-range|optional|only-of-type|only-child|nth-of-type|nth-last-of-type|nth-last-child|nth-child|not|link|left|last-of-type|last-child|lang|invalid|indeterminate|in-range|hover|focus|first-of-type|first-line|first-letter|first-child|first|enabled|empty|disabled|default|checked|before|after|active)"
},{className:"selector-pseudo",
begin:"::(after|before|choices|first-letter|first-line|repeat-index|repeat-item|selection|value)"
},i,{className:"attribute",
begin:"\\b(src|z-index|word-wrap|word-spacing|word-break|width|widows|white-space|visibility|vertical-align|unicode-bidi|transition-timing-function|transition-property|transition-duration|transition-delay|transition|transform-style|transform-origin|transform|top|text-underline-position|text-transform|text-shadow|text-rendering|text-overflow|text-indent|text-decoration-style|text-decoration-line|text-decoration-color|text-decoration|text-align-last|text-align|tab-size|table-layout|right|resize|quotes|position|pointer-events|perspective-origin|perspective|page-break-inside|page-break-before|page-break-after|padding-top|padding-right|padding-left|padding-bottom|padding|overflow-y|overflow-x|overflow-wrap|overflow|outline-width|outline-style|outline-offset|outline-color|outline|orphans|order|opacity|object-position|object-fit|normal|none|nav-up|nav-right|nav-left|nav-index|nav-down|min-width|min-height|max-width|max-height|mask|marks|margin-top|margin-right|margin-left|margin-bottom|margin|list-style-type|list-style-position|list-style-image|list-style|line-height|letter-spacing|left|justify-content|initial|inherit|ime-mode|image-orientation|image-resolution|image-rendering|icon|hyphens|height|font-weight|font-variant-ligatures|font-variant|font-style|font-stretch|font-size-adjust|font-size|font-language-override|font-kerning|font-feature-settings|font-family|font|float|flex-wrap|flex-shrink|flex-grow|flex-flow|flex-direction|flex-basis|flex|filter|empty-cells|display|direction|cursor|counter-reset|counter-increment|content|column-width|column-span|column-rule-width|column-rule-style|column-rule-color|column-rule|column-gap|column-fill|column-count|columns|color|clip-path|clip|clear|caption-side|break-inside|break-before|break-after|box-sizing|box-shadow|box-decoration-break|bottom|border-width|border-top-width|border-top-style|border-top-right-radius|border-top-left-radius|border-top-color|border-top|border-style|border-spacing|border-right-width|border-right-style|border-right-color|border-right|border-radius|border-left-width|border-left-style|border-left-color|border-left|border-image-width|border-image-source|border-image-slice|border-image-repeat|border-image-outset|border-image|border-color|border-collapse|border-bottom-width|border-bottom-style|border-bottom-right-radius|border-bottom-left-radius|border-bottom-color|border-bottom|border|background-size|background-repeat|background-position|background-origin|background-image|background-color|background-clip|background-attachment|background-blend-mode|background|backface-visibility|auto|animation-timing-function|animation-play-state|animation-name|animation-iteration-count|animation-fill-mode|animation-duration|animation-direction|animation-delay|animation|align-self|align-items|align-content)\\b",
illegal:"[^\\s]"},{
begin:"\\b(whitespace|wait|w-resize|visible|vertical-text|vertical-ideographic|uppercase|upper-roman|upper-alpha|underline|transparent|top|thin|thick|text|text-top|text-bottom|tb-rl|table-header-group|table-footer-group|sw-resize|super|strict|static|square|solid|small-caps|separate|se-resize|scroll|s-resize|rtl|row-resize|ridge|right|repeat|repeat-y|repeat-x|relative|progress|pointer|overline|outside|outset|oblique|nowrap|not-allowed|normal|none|nw-resize|no-repeat|no-drop|newspaper|ne-resize|n-resize|move|middle|medium|ltr|lr-tb|lowercase|lower-roman|lower-alpha|loose|list-item|line|line-through|line-edge|lighter|left|keep-all|justify|italic|inter-word|inter-ideograph|inside|inset|inline|inline-block|inherit|inactive|ideograph-space|ideograph-parenthesis|ideograph-numeric|ideograph-alpha|horizontal|hidden|help|hand|groove|fixed|ellipsis|e-resize|double|dotted|distribute|distribute-space|distribute-letter|distribute-all-lines|disc|disabled|default|decimal|dashed|crosshair|collapse|col-resize|circle|char|center|capitalize|break-word|break-all|bottom|both|bolder|bold|block|bidi-override|below|baseline|auto|always|all-scroll|absolute|table|table-cell)\\b"
},{begin:":",end:";",
contains:[i,r,e.CSS_NUMBER_MODE,e.QUOTE_STRING_MODE,e.APOS_STRING_MODE,{
className:"meta",begin:"!important"}]},{begin:"@(page|font-face)",lexemes:t,
keywords:"@page @font-face"},{begin:"@",end:"[{;]",returnBegin:!0,
keywords:"and or not only",contains:[{begin:t,className:"keyword"
},i,e.QUOTE_STRING_MODE,e.APOS_STRING_MODE,r,e.CSS_NUMBER_MODE]}]}}})());hljs.registerLanguage("java",(()=>{"use strict"
;var e="\\.([0-9](_*[0-9])*)",n="[0-9a-fA-F](_*[0-9a-fA-F])*",a={
className:"number",variants:[{
begin:`(\\b([0-9](_*[0-9])*)((${e})|\\.)?|(${e}))[eE][+-]?([0-9](_*[0-9])*)[fFdD]?\\b`
},{begin:`\\b([0-9](_*[0-9])*)((${e})[fFdD]?\\b|\\.([fFdD]\\b)?)`},{
begin:`(${e})[fFdD]?\\b`},{begin:"\\b([0-9](_*[0-9])*)[fFdD]\\b"},{
begin:`\\b0[xX]((${n})\\.?|(${n})?\\.(${n}))[pP][+-]?([0-9](_*[0-9])*)[fFdD]?\\b`
},{begin:"\\b(0|[1-9](_*[0-9])*)[lL]?\\b"},{begin:`\\b0[xX](${n})[lL]?\\b`},{
begin:"\\b0(_*[0-7])*[lL]?\\b"},{begin:"\\b0[bB][01](_*[01])*[lL]?\\b"}],
relevance:0};return e=>{
var n="false synchronized int abstract float private char boolean var static null if const for true while long strictfp finally protected import native final void enum else break transient catch instanceof byte super volatile case assert short package default double public try this switch continue throws protected public private module requires exports do",s={
className:"meta",begin:"@[\xc0-\u02b8a-zA-Z_$][\xc0-\u02b8a-zA-Z_$0-9]*",
contains:[{begin:/\(/,end:/\)/,contains:["self"]}]};const r=a;return{
name:"Java",aliases:["jsp"],keywords:n,illegal:/<\/|#/,
contains:[e.COMMENT("/\\*\\*","\\*/",{relevance:0,contains:[{begin:/\w+@/,
relevance:0},{className:"doctag",begin:"@[A-Za-z]+"}]}),{
begin:/import java\.[a-z]+\./,keywords:"import",relevance:2
},e.C_LINE_COMMENT_MODE,e.C_BLOCK_COMMENT_MODE,e.APOS_STRING_MODE,e.QUOTE_STRING_MODE,{
className:"class",beginKeywords:"class interface enum",end:/[{;=]/,
excludeEnd:!0,keywords:"class interface enum",illegal:/[:"\[\]]/,contains:[{
beginKeywords:"extends implements"},e.UNDERSCORE_TITLE_MODE]},{
beginKeywords:"new throw return else",relevance:0},{className:"class",
begin:"record\\s+"+e.UNDERSCORE_IDENT_RE+"\\s*\\(",returnBegin:!0,excludeEnd:!0,
end:/[{;=]/,keywords:n,contains:[{beginKeywords:"record"},{
begin:e.UNDERSCORE_IDENT_RE+"\\s*\\(",returnBegin:!0,relevance:0,
contains:[e.UNDERSCORE_TITLE_MODE]},{className:"params",begin:/\(/,end:/\)/,
keywords:n,relevance:0,contains:[e.C_BLOCK_COMMENT_MODE]
},e.C_LINE_COMMENT_MODE,e.C_BLOCK_COMMENT_MODE]},{className:"function",
begin:"([\xc0-\u02b8a-zA-Z_$][\xc0-\u02b8a-zA-Z_$0-9]*(<[\xc0-\u02b8a-zA-Z_$][\xc0-\u02b8a-zA-Z_$0-9]*(\\s*,\\s*[\xc0-\u02b8a-zA-Z_$][\xc0-\u02b8a-zA-Z_$0-9]*)*>)?\\s+)+"+e.UNDERSCORE_IDENT_RE+"\\s*\\(",
returnBegin:!0,end:/[{;=]/,excludeEnd:!0,keywords:n,contains:[{
begin:e.UNDERSCORE_IDENT_RE+"\\s*\\(",returnBegin:!0,relevance:0,
contains:[e.UNDERSCORE_TITLE_MODE]},{className:"params",begin:/\(/,end:/\)/,
keywords:n,relevance:0,
contains:[s,e.APOS_STRING_MODE,e.QUOTE_STRING_MODE,r,e.C_BLOCK_COMMENT_MODE]
},e.C_LINE_COMMENT_MODE,e.C_BLOCK_COMMENT_MODE]},r,s]}}})());hljs.registerLanguage("cmake",(()=>{"use strict";return e=>({name:"CMake",
aliases:["cmake.in"],case_insensitive:!0,keywords:{
keyword:"break cmake_host_system_information cmake_minimum_required cmake_parse_arguments cmake_policy configure_file continue elseif else endforeach endfunction endif endmacro endwhile execute_process file find_file find_library find_package find_path find_program foreach function get_cmake_property get_directory_property get_filename_component get_property if include include_guard list macro mark_as_advanced math message option return separate_arguments set_directory_properties set_property set site_name string unset variable_watch while add_compile_definitions add_compile_options add_custom_command add_custom_target add_definitions add_dependencies add_executable add_library add_link_options add_subdirectory add_test aux_source_directory build_command create_test_sourcelist define_property enable_language enable_testing export fltk_wrap_ui get_source_file_property get_target_property get_test_property include_directories include_external_msproject include_regular_expression install link_directories link_libraries load_cache project qt_wrap_cpp qt_wrap_ui remove_definitions set_source_files_properties set_target_properties set_tests_properties source_group target_compile_definitions target_compile_features target_compile_options target_include_directories target_link_directories target_link_libraries target_link_options target_sources try_compile try_run ctest_build ctest_configure ctest_coverage ctest_empty_binary_directory ctest_memcheck ctest_read_custom_files ctest_run_script ctest_sleep ctest_start ctest_submit ctest_test ctest_update ctest_upload build_name exec_program export_library_dependencies install_files install_programs install_targets load_command make_directory output_required_files remove subdir_depends subdirs use_mangled_mesa utility_source variable_requires write_file qt5_use_modules qt5_use_package qt5_wrap_cpp on off true false and or not command policy target test exists is_newer_than is_directory is_symlink is_absolute matches less greater equal less_equal greater_equal strless strgreater strequal strless_equal strgreater_equal version_less version_greater version_equal version_less_equal version_greater_equal in_list defined"
},contains:[{className:"variable",begin:/\$\{/,end:/\}/
},e.HASH_COMMENT_MODE,e.QUOTE_STRING_MODE,e.NUMBER_MODE]})})());hljs.registerLanguage("elm",(()=>{"use strict";return e=>{const n={
variants:[e.COMMENT("--","$"),e.COMMENT(/\{-/,/-\}/,{contains:["self"]})]},i={
className:"type",begin:"\\b[A-Z][\\w']*",relevance:0},s={begin:"\\(",end:"\\)",
illegal:'"',contains:[{className:"type",
begin:"\\b[A-Z][\\w]*(\\((\\.\\.|,|\\w+)\\))?"},n]};return{name:"Elm",
keywords:"let in if then else case of where module import exposing type alias as infix infixl infixr port effect command subscription",
contains:[{beginKeywords:"port effect module",end:"exposing",
keywords:"port effect module where command subscription exposing",
contains:[s,n],illegal:"\\W\\.|;"},{begin:"import",end:"$",
keywords:"import as exposing",contains:[s,n],illegal:"\\W\\.|;"},{begin:"type",
end:"$",keywords:"type alias",contains:[i,s,{begin:/\{/,end:/\}/,
contains:s.contains},n]},{beginKeywords:"infix infixl infixr",end:"$",
contains:[e.C_NUMBER_MODE,n]},{begin:"port",end:"$",keywords:"port",contains:[n]
},{className:"string",begin:"'\\\\?.",end:"'",illegal:"."
},e.QUOTE_STRING_MODE,e.C_NUMBER_MODE,i,e.inherit(e.TITLE_MODE,{
begin:"^[_a-z][\\w']*"}),n,{begin:"->|<-"}],illegal:/;/}}})());hljs.registerLanguage("latex",(()=>{"use strict";return e=>{const n=[{
begin:/\^{6}[0-9a-f]{6}/},{begin:/\^{5}[0-9a-f]{5}/},{begin:/\^{4}[0-9a-f]{4}/
},{begin:/\^{3}[0-9a-f]{3}/},{begin:/\^{2}[0-9a-f]{2}/},{
begin:/\^{2}[\u0000-\u007f]/}],a=[{className:"keyword",begin:/\\/,relevance:0,
contains:[{endsParent:!0,begin:((...e)=>"("+e.map((e=>{
return(n=e)?"string"==typeof n?n:n.source:null;var n
})).join("|")+")")(...["(?:NeedsTeXFormat|RequirePackage|GetIdInfo)","Provides(?:Expl)?(?:Package|Class|File)","(?:DeclareOption|ProcessOptions)","(?:documentclass|usepackage|input|include)","makeat(?:letter|other)","ExplSyntax(?:On|Off)","(?:new|renew|provide)?command","(?:re)newenvironment","(?:New|Renew|Provide|Declare)(?:Expandable)?DocumentCommand","(?:New|Renew|Provide|Declare)DocumentEnvironment","(?:(?:e|g|x)?def|let)","(?:begin|end)","(?:part|chapter|(?:sub){0,2}section|(?:sub)?paragraph)","caption","(?:label|(?:eq|page|name)?ref|(?:paren|foot|super)?cite)","(?:alpha|beta|[Gg]amma|[Dd]elta|(?:var)?epsilon|zeta|eta|[Tt]heta|vartheta)","(?:iota|(?:var)?kappa|[Ll]ambda|mu|nu|[Xx]i|[Pp]i|varpi|(?:var)rho)","(?:[Ss]igma|varsigma|tau|[Uu]psilon|[Pp]hi|varphi|chi|[Pp]si|[Oo]mega)","(?:frac|sum|prod|lim|infty|times|sqrt|leq|geq|left|right|middle|[bB]igg?)","(?:[lr]angle|q?quad|[lcvdi]?dots|d?dot|hat|tilde|bar)"].map((e=>e+"(?![a-zA-Z@:_])")))
},{endsParent:!0,
begin:RegExp(["(?:__)?[a-zA-Z]{2,}_[a-zA-Z](?:_?[a-zA-Z])+:[a-zA-Z]*","[lgc]__?[a-zA-Z](?:_?[a-zA-Z])*_[a-zA-Z]{2,}","[qs]__?[a-zA-Z](?:_?[a-zA-Z])+","use(?:_i)?:[a-zA-Z]*","(?:else|fi|or):","(?:if|cs|exp):w","(?:hbox|vbox):n","::[a-zA-Z]_unbraced","::[a-zA-Z:]"].map((e=>e+"(?![a-zA-Z:_])")).join("|"))
},{endsParent:!0,variants:n},{endsParent:!0,relevance:0,variants:[{
begin:/[a-zA-Z@]+/},{begin:/[^a-zA-Z@]?/}]}]},{className:"params",relevance:0,
begin:/#+\d?/},{variants:n},{className:"built_in",relevance:0,begin:/[$&^_]/},{
className:"meta",begin:"% !TeX",end:"$",relevance:10},e.COMMENT("%","$",{
relevance:0})],i={begin:/\{/,end:/\}/,relevance:0,contains:["self",...a]
},t=e.inherit(i,{relevance:0,endsParent:!0,contains:[i,...a]}),r={begin:/\[/,
end:/\]/,endsParent:!0,relevance:0,contains:[i,...a]},s={begin:/\s+/,relevance:0
},c=[t],l=[r],o=(e,n)=>({contains:[s],starts:{relevance:0,contains:e,starts:n}
}),d=(e,n)=>({begin:"\\\\"+e+"(?![a-zA-Z@:_])",keywords:{$pattern:/\\[a-zA-Z]+/,
keyword:"\\"+e},relevance:0,contains:[s],starts:n}),g=(n,a)=>e.inherit({
begin:"\\\\begin(?=[ \t]*(\\r?\\n[ \t]*)?\\{"+n+"\\})",keywords:{
$pattern:/\\[a-zA-Z]+/,keyword:"\\begin"},relevance:0
},o(c,a)),m=(n="string")=>e.END_SAME_AS_BEGIN({className:n,begin:/(.|\r?\n)/,
end:/(.|\r?\n)/,excludeBegin:!0,excludeEnd:!0,endsParent:!0}),b=e=>({
className:"string",end:"(?=\\\\end\\{"+e+"\\})"}),p=(e="string")=>({relevance:0,
begin:/\{/,starts:{endsParent:!0,contains:[{className:e,end:/(?=\})/,
endsParent:!0,contains:[{begin:/\{/,end:/\}/,relevance:0,contains:["self"]}]}]}
});return{name:"LaTeX",aliases:["TeX"],
contains:[...["verb","lstinline"].map((e=>d(e,{contains:[m()]}))),d("mint",o(c,{
contains:[m()]})),d("mintinline",o(c,{contains:[p(),m()]})),d("url",{
contains:[p("link"),p("link")]}),d("hyperref",{contains:[p("link")]
}),d("href",o(l,{contains:[p("link")]
})),...[].concat(...["","\\*"].map((e=>[g("verbatim"+e,b("verbatim"+e)),g("filecontents"+e,o(c,b("filecontents"+e))),...["","B","L"].map((n=>g(n+"Verbatim"+e,o(l,b(n+"Verbatim"+e)))))]))),g("minted",o(l,o(c,b("minted")))),...a]
}}})());hljs.registerLanguage("delphi",(()=>{"use strict";return e=>{
const r="exports register file shl array record property for mod while set ally label uses raise not stored class safecall var interface or private static exit index inherited to else stdcall override shr asm far resourcestring finalization packed virtual out and protected library do xorwrite goto near function end div overload object unit begin string on inline repeat until destructor write message program with read initialization except default nil if case cdecl in downto threadvar of try pascal const external constructor type public then implementation finally published procedure absolute reintroduce operator as is abstract alias assembler bitpacked break continue cppdecl cvar enumerator experimental platform deprecated unimplemented dynamic export far16 forward generic helper implements interrupt iochecks local name nodefault noreturn nostackframe oldfpccall otherwise saveregisters softfloat specialize strict unaligned varargs ",a=[e.C_LINE_COMMENT_MODE,e.COMMENT(/\{/,/\}/,{
relevance:0}),e.COMMENT(/\(\*/,/\*\)/,{relevance:10})],t={className:"meta",
variants:[{begin:/\{\$/,end:/\}/},{begin:/\(\*\$/,end:/\*\)/}]},n={
className:"string",begin:/'/,end:/'/,contains:[{begin:/''/}]},s={
className:"string",begin:/(#\d+)+/},i={begin:e.IDENT_RE+"\\s*=\\s*class\\s*\\(",
returnBegin:!0,contains:[e.TITLE_MODE]},c={className:"function",
beginKeywords:"function constructor destructor procedure",end:/[:;]/,
keywords:"function constructor|10 destructor|10 procedure|10",
contains:[e.TITLE_MODE,{className:"params",begin:/\(/,end:/\)/,keywords:r,
contains:[n,s,t].concat(a)},t].concat(a)};return{name:"Delphi",
aliases:["dpr","dfm","pas","pascal","freepascal","lazarus","lpr","lfm"],
case_insensitive:!0,keywords:r,illegal:/"|\$[G-Zg-z]|\/\*|<\/|\|/,
contains:[n,s,e.NUMBER_MODE,{className:"number",relevance:0,variants:[{
begin:"\\$[0-9A-Fa-f]+"},{begin:"&[0-7]+"},{begin:"%[01]+"}]},i,c,t].concat(a)}}
})());hljs.registerLanguage("tcl",(()=>{"use strict";return e=>({name:"Tcl",
aliases:["tk"],
keywords:"after append apply array auto_execok auto_import auto_load auto_mkindex auto_mkindex_old auto_qualify auto_reset bgerror binary break catch cd chan clock close concat continue dde dict encoding eof error eval exec exit expr fblocked fconfigure fcopy file fileevent filename flush for foreach format gets glob global history http if incr info interp join lappend|10 lassign|10 lindex|10 linsert|10 list llength|10 load lrange|10 lrepeat|10 lreplace|10 lreverse|10 lsearch|10 lset|10 lsort|10 mathfunc mathop memory msgcat namespace open package parray pid pkg::create pkg_mkIndex platform platform::shell proc puts pwd read refchan regexp registry regsub|10 rename return safe scan seek set socket source split string subst switch tcl_endOfWord tcl_findLibrary tcl_startOfNextWord tcl_startOfPreviousWord tcl_wordBreakAfter tcl_wordBreakBefore tcltest tclvars tell time tm trace unknown unload unset update uplevel upvar variable vwait while",
contains:[e.COMMENT(";[ \\t]*#","$"),e.COMMENT("^[ \\t]*#","$"),{
beginKeywords:"proc",end:"[\\{]",excludeEnd:!0,contains:[{className:"title",
begin:"[ \\t\\n\\r]+(::)?[a-zA-Z_]((::)?[a-zA-Z0-9_])*",end:"[ \\t\\n\\r]",
endsWithParent:!0,excludeEnd:!0}]},{excludeEnd:!0,variants:[{
begin:"\\$(\\{)?(::)?[a-zA-Z_]((::)?[a-zA-Z0-9_])*\\(([a-zA-Z0-9_])*\\)",
end:"[^a-zA-Z0-9_\\}\\$]"},{begin:"\\$(\\{)?(::)?[a-zA-Z_]((::)?[a-zA-Z0-9_])*",
end:"(\\))?[^a-zA-Z0-9_\\}\\$]"}]},{className:"string",
contains:[e.BACKSLASH_ESCAPE],variants:[e.inherit(e.QUOTE_STRING_MODE,{
illegal:null})]},{className:"number",
variants:[e.BINARY_NUMBER_MODE,e.C_NUMBER_MODE]}]})})());hljs.registerLanguage("lisp",(()=>{"use strict";return e=>{
var n="[a-zA-Z_\\-+\\*\\/<=>&#][a-zA-Z0-9_\\-+*\\/<=>&#!]*",a="\\|[^]*?\\|",i="(-|\\+)?\\d+(\\.\\d+|\\/\\d+)?((d|e|f|l|s|D|E|F|L|S)(\\+|-)?\\d+)?",s={
className:"literal",begin:"\\b(t{1}|nil)\\b"},l={className:"number",variants:[{
begin:i,relevance:0},{begin:"#(b|B)[0-1]+(/[0-1]+)?"},{
begin:"#(o|O)[0-7]+(/[0-7]+)?"},{begin:"#(x|X)[0-9a-fA-F]+(/[0-9a-fA-F]+)?"},{
begin:"#(c|C)\\("+i+" +"+i,end:"\\)"}]},b=e.inherit(e.QUOTE_STRING_MODE,{
illegal:null}),g=e.COMMENT(";","$",{relevance:0}),r={begin:"\\*",end:"\\*"},t={
className:"symbol",begin:"[:&]"+n},c={begin:n,relevance:0},d={begin:a},o={
contains:[l,b,r,t,{begin:"\\(",end:"\\)",contains:["self",s,b,l,c]},c],
variants:[{begin:"['`]\\(",end:"\\)"},{begin:"\\(quote ",end:"\\)",keywords:{
name:"quote"}},{begin:"'"+a}]},v={variants:[{begin:"'"+n},{
begin:"#'"+n+"(::"+n+")*"}]},m={begin:"\\(\\s*",end:"\\)"},u={endsWithParent:!0,
relevance:0};return m.contains=[{className:"name",variants:[{begin:n,relevance:0
},{begin:a}]},u],u.contains=[o,v,m,s,l,b,g,r,t,d,c],{name:"Lisp",illegal:/\S/,
contains:[l,e.SHEBANG(),s,b,g,o,v,m,c]}}})());hljs.registerLanguage("x86asm",(()=>{"use strict";return s=>({
name:"Intel x86 Assembly",case_insensitive:!0,keywords:{
$pattern:"[.%]?"+s.IDENT_RE,
keyword:"lock rep repe repz repne repnz xaquire xrelease bnd nobnd aaa aad aam aas adc add and arpl bb0_reset bb1_reset bound bsf bsr bswap bt btc btr bts call cbw cdq cdqe clc cld cli clts cmc cmp cmpsb cmpsd cmpsq cmpsw cmpxchg cmpxchg486 cmpxchg8b cmpxchg16b cpuid cpu_read cpu_write cqo cwd cwde daa das dec div dmint emms enter equ f2xm1 fabs fadd faddp fbld fbstp fchs fclex fcmovb fcmovbe fcmove fcmovnb fcmovnbe fcmovne fcmovnu fcmovu fcom fcomi fcomip fcomp fcompp fcos fdecstp fdisi fdiv fdivp fdivr fdivrp femms feni ffree ffreep fiadd ficom ficomp fidiv fidivr fild fimul fincstp finit fist fistp fisttp fisub fisubr fld fld1 fldcw fldenv fldl2e fldl2t fldlg2 fldln2 fldpi fldz fmul fmulp fnclex fndisi fneni fninit fnop fnsave fnstcw fnstenv fnstsw fpatan fprem fprem1 fptan frndint frstor fsave fscale fsetpm fsin fsincos fsqrt fst fstcw fstenv fstp fstsw fsub fsubp fsubr fsubrp ftst fucom fucomi fucomip fucomp fucompp fxam fxch fxtract fyl2x fyl2xp1 hlt ibts icebp idiv imul in inc incbin insb insd insw int int01 int1 int03 int3 into invd invpcid invlpg invlpga iret iretd iretq iretw jcxz jecxz jrcxz jmp jmpe lahf lar lds lea leave les lfence lfs lgdt lgs lidt lldt lmsw loadall loadall286 lodsb lodsd lodsq lodsw loop loope loopne loopnz loopz lsl lss ltr mfence monitor mov movd movq movsb movsd movsq movsw movsx movsxd movzx mul mwait neg nop not or out outsb outsd outsw packssdw packsswb packuswb paddb paddd paddsb paddsiw paddsw paddusb paddusw paddw pand pandn pause paveb pavgusb pcmpeqb pcmpeqd pcmpeqw pcmpgtb pcmpgtd pcmpgtw pdistib pf2id pfacc pfadd pfcmpeq pfcmpge pfcmpgt pfmax pfmin pfmul pfrcp pfrcpit1 pfrcpit2 pfrsqit1 pfrsqrt pfsub pfsubr pi2fd pmachriw pmaddwd pmagw pmulhriw pmulhrwa pmulhrwc pmulhw pmullw pmvgezb pmvlzb pmvnzb pmvzb pop popa popad popaw popf popfd popfq popfw por prefetch prefetchw pslld psllq psllw psrad psraw psrld psrlq psrlw psubb psubd psubsb psubsiw psubsw psubusb psubusw psubw punpckhbw punpckhdq punpckhwd punpcklbw punpckldq punpcklwd push pusha pushad pushaw pushf pushfd pushfq pushfw pxor rcl rcr rdshr rdmsr rdpmc rdtsc rdtscp ret retf retn rol ror rdm rsdc rsldt rsm rsts sahf sal salc sar sbb scasb scasd scasq scasw sfence sgdt shl shld shr shrd sidt sldt skinit smi smint smintold smsw stc std sti stosb stosd stosq stosw str sub svdc svldt svts swapgs syscall sysenter sysexit sysret test ud0 ud1 ud2b ud2 ud2a umov verr verw fwait wbinvd wrshr wrmsr xadd xbts xchg xlatb xlat xor cmove cmovz cmovne cmovnz cmova cmovnbe cmovae cmovnb cmovb cmovnae cmovbe cmovna cmovg cmovnle cmovge cmovnl cmovl cmovnge cmovle cmovng cmovc cmovnc cmovo cmovno cmovs cmovns cmovp cmovpe cmovnp cmovpo je jz jne jnz ja jnbe jae jnb jb jnae jbe jna jg jnle jge jnl jl jnge jle jng jc jnc jo jno js jns jpo jnp jpe jp sete setz setne setnz seta setnbe setae setnb setnc setb setnae setcset setbe setna setg setnle setge setnl setl setnge setle setng sets setns seto setno setpe setp setpo setnp addps addss andnps andps cmpeqps cmpeqss cmpleps cmpless cmpltps cmpltss cmpneqps cmpneqss cmpnleps cmpnless cmpnltps cmpnltss cmpordps cmpordss cmpunordps cmpunordss cmpps cmpss comiss cvtpi2ps cvtps2pi cvtsi2ss cvtss2si cvttps2pi cvttss2si divps divss ldmxcsr maxps maxss minps minss movaps movhps movlhps movlps movhlps movmskps movntps movss movups mulps mulss orps rcpps rcpss rsqrtps rsqrtss shufps sqrtps sqrtss stmxcsr subps subss ucomiss unpckhps unpcklps xorps fxrstor fxrstor64 fxsave fxsave64 xgetbv xsetbv xsave xsave64 xsaveopt xsaveopt64 xrstor xrstor64 prefetchnta prefetcht0 prefetcht1 prefetcht2 maskmovq movntq pavgb pavgw pextrw pinsrw pmaxsw pmaxub pminsw pminub pmovmskb pmulhuw psadbw pshufw pf2iw pfnacc pfpnacc pi2fw pswapd maskmovdqu clflush movntdq movnti movntpd movdqa movdqu movdq2q movq2dq paddq pmuludq pshufd pshufhw pshuflw pslldq psrldq psubq punpckhqdq punpcklqdq addpd addsd andnpd andpd cmpeqpd cmpeqsd cmplepd cmplesd cmpltpd cmpltsd cmpneqpd cmpneqsd cmpnlepd cmpnlesd cmpnltpd cmpnltsd cmpordpd cmpordsd cmpunordpd cmpunordsd cmppd comisd cvtdq2pd cvtdq2ps cvtpd2dq cvtpd2pi cvtpd2ps cvtpi2pd cvtps2dq cvtps2pd cvtsd2si cvtsd2ss cvtsi2sd cvtss2sd cvttpd2pi cvttpd2dq cvttps2dq cvttsd2si divpd divsd maxpd maxsd minpd minsd movapd movhpd movlpd movmskpd movupd mulpd mulsd orpd shufpd sqrtpd sqrtsd subpd subsd ucomisd unpckhpd unpcklpd xorpd addsubpd addsubps haddpd haddps hsubpd hsubps lddqu movddup movshdup movsldup clgi stgi vmcall vmclear vmfunc vmlaunch vmload vmmcall vmptrld vmptrst vmread vmresume vmrun vmsave vmwrite vmxoff vmxon invept invvpid pabsb pabsw pabsd palignr phaddw phaddd phaddsw phsubw phsubd phsubsw pmaddubsw pmulhrsw pshufb psignb psignw psignd extrq insertq movntsd movntss lzcnt blendpd blendps blendvpd blendvps dppd dpps extractps insertps movntdqa mpsadbw packusdw pblendvb pblendw pcmpeqq pextrb pextrd pextrq phminposuw pinsrb pinsrd pinsrq pmaxsb pmaxsd pmaxud pmaxuw pminsb pminsd pminud pminuw pmovsxbw pmovsxbd pmovsxbq pmovsxwd pmovsxwq pmovsxdq pmovzxbw pmovzxbd pmovzxbq pmovzxwd pmovzxwq pmovzxdq pmuldq pmulld ptest roundpd roundps roundsd roundss crc32 pcmpestri pcmpestrm pcmpistri pcmpistrm pcmpgtq popcnt getsec pfrcpv pfrsqrtv movbe aesenc aesenclast aesdec aesdeclast aesimc aeskeygenassist vaesenc vaesenclast vaesdec vaesdeclast vaesimc vaeskeygenassist vaddpd vaddps vaddsd vaddss vaddsubpd vaddsubps vandpd vandps vandnpd vandnps vblendpd vblendps vblendvpd vblendvps vbroadcastss vbroadcastsd vbroadcastf128 vcmpeq_ospd vcmpeqpd vcmplt_ospd vcmpltpd vcmple_ospd vcmplepd vcmpunord_qpd vcmpunordpd vcmpneq_uqpd vcmpneqpd vcmpnlt_uspd vcmpnltpd vcmpnle_uspd vcmpnlepd vcmpord_qpd vcmpordpd vcmpeq_uqpd vcmpnge_uspd vcmpngepd vcmpngt_uspd vcmpngtpd vcmpfalse_oqpd vcmpfalsepd vcmpneq_oqpd vcmpge_ospd vcmpgepd vcmpgt_ospd vcmpgtpd vcmptrue_uqpd vcmptruepd vcmplt_oqpd vcmple_oqpd vcmpunord_spd vcmpneq_uspd vcmpnlt_uqpd vcmpnle_uqpd vcmpord_spd vcmpeq_uspd vcmpnge_uqpd vcmpngt_uqpd vcmpfalse_ospd vcmpneq_ospd vcmpge_oqpd vcmpgt_oqpd vcmptrue_uspd vcmppd vcmpeq_osps vcmpeqps vcmplt_osps vcmpltps vcmple_osps vcmpleps vcmpunord_qps vcmpunordps vcmpneq_uqps vcmpneqps vcmpnlt_usps vcmpnltps vcmpnle_usps vcmpnleps vcmpord_qps vcmpordps vcmpeq_uqps vcmpnge_usps vcmpngeps vcmpngt_usps vcmpngtps vcmpfalse_oqps vcmpfalseps vcmpneq_oqps vcmpge_osps vcmpgeps vcmpgt_osps vcmpgtps vcmptrue_uqps vcmptrueps vcmplt_oqps vcmple_oqps vcmpunord_sps vcmpneq_usps vcmpnlt_uqps vcmpnle_uqps vcmpord_sps vcmpeq_usps vcmpnge_uqps vcmpngt_uqps vcmpfalse_osps vcmpneq_osps vcmpge_oqps vcmpgt_oqps vcmptrue_usps vcmpps vcmpeq_ossd vcmpeqsd vcmplt_ossd vcmpltsd vcmple_ossd vcmplesd vcmpunord_qsd vcmpunordsd vcmpneq_uqsd vcmpneqsd vcmpnlt_ussd vcmpnltsd vcmpnle_ussd vcmpnlesd vcmpord_qsd vcmpordsd vcmpeq_uqsd vcmpnge_ussd vcmpngesd vcmpngt_ussd vcmpngtsd vcmpfalse_oqsd vcmpfalsesd vcmpneq_oqsd vcmpge_ossd vcmpgesd vcmpgt_ossd vcmpgtsd vcmptrue_uqsd vcmptruesd vcmplt_oqsd vcmple_oqsd vcmpunord_ssd vcmpneq_ussd vcmpnlt_uqsd vcmpnle_uqsd vcmpord_ssd vcmpeq_ussd vcmpnge_uqsd vcmpngt_uqsd vcmpfalse_ossd vcmpneq_ossd vcmpge_oqsd vcmpgt_oqsd vcmptrue_ussd vcmpsd vcmpeq_osss vcmpeqss vcmplt_osss vcmpltss vcmple_osss vcmpless vcmpunord_qss vcmpunordss vcmpneq_uqss vcmpneqss vcmpnlt_usss vcmpnltss vcmpnle_usss vcmpnless vcmpord_qss vcmpordss vcmpeq_uqss vcmpnge_usss vcmpngess vcmpngt_usss vcmpngtss vcmpfalse_oqss vcmpfalsess vcmpneq_oqss vcmpge_osss vcmpgess vcmpgt_osss vcmpgtss vcmptrue_uqss vcmptruess vcmplt_oqss vcmple_oqss vcmpunord_sss vcmpneq_usss vcmpnlt_uqss vcmpnle_uqss vcmpord_sss vcmpeq_usss vcmpnge_uqss vcmpngt_uqss vcmpfalse_osss vcmpneq_osss vcmpge_oqss vcmpgt_oqss vcmptrue_usss vcmpss vcomisd vcomiss vcvtdq2pd vcvtdq2ps vcvtpd2dq vcvtpd2ps vcvtps2dq vcvtps2pd vcvtsd2si vcvtsd2ss vcvtsi2sd vcvtsi2ss vcvtss2sd vcvtss2si vcvttpd2dq vcvttps2dq vcvttsd2si vcvttss2si vdivpd vdivps vdivsd vdivss vdppd vdpps vextractf128 vextractps vhaddpd vhaddps vhsubpd vhsubps vinsertf128 vinsertps vlddqu vldqqu vldmxcsr vmaskmovdqu vmaskmovps vmaskmovpd vmaxpd vmaxps vmaxsd vmaxss vminpd vminps vminsd vminss vmovapd vmovaps vmovd vmovq vmovddup vmovdqa vmovqqa vmovdqu vmovqqu vmovhlps vmovhpd vmovhps vmovlhps vmovlpd vmovlps vmovmskpd vmovmskps vmovntdq vmovntqq vmovntdqa vmovntpd vmovntps vmovsd vmovshdup vmovsldup vmovss vmovupd vmovups vmpsadbw vmulpd vmulps vmulsd vmulss vorpd vorps vpabsb vpabsw vpabsd vpacksswb vpackssdw vpackuswb vpackusdw vpaddb vpaddw vpaddd vpaddq vpaddsb vpaddsw vpaddusb vpaddusw vpalignr vpand vpandn vpavgb vpavgw vpblendvb vpblendw vpcmpestri vpcmpestrm vpcmpistri vpcmpistrm vpcmpeqb vpcmpeqw vpcmpeqd vpcmpeqq vpcmpgtb vpcmpgtw vpcmpgtd vpcmpgtq vpermilpd vpermilps vperm2f128 vpextrb vpextrw vpextrd vpextrq vphaddw vphaddd vphaddsw vphminposuw vphsubw vphsubd vphsubsw vpinsrb vpinsrw vpinsrd vpinsrq vpmaddwd vpmaddubsw vpmaxsb vpmaxsw vpmaxsd vpmaxub vpmaxuw vpmaxud vpminsb vpminsw vpminsd vpminub vpminuw vpminud vpmovmskb vpmovsxbw vpmovsxbd vpmovsxbq vpmovsxwd vpmovsxwq vpmovsxdq vpmovzxbw vpmovzxbd vpmovzxbq vpmovzxwd vpmovzxwq vpmovzxdq vpmulhuw vpmulhrsw vpmulhw vpmullw vpmulld vpmuludq vpmuldq vpor vpsadbw vpshufb vpshufd vpshufhw vpshuflw vpsignb vpsignw vpsignd vpslldq vpsrldq vpsllw vpslld vpsllq vpsraw vpsrad vpsrlw vpsrld vpsrlq vptest vpsubb vpsubw vpsubd vpsubq vpsubsb vpsubsw vpsubusb vpsubusw vpunpckhbw vpunpckhwd vpunpckhdq vpunpckhqdq vpunpcklbw vpunpcklwd vpunpckldq vpunpcklqdq vpxor vrcpps vrcpss vrsqrtps vrsqrtss vroundpd vroundps vroundsd vroundss vshufpd vshufps vsqrtpd vsqrtps vsqrtsd vsqrtss vstmxcsr vsubpd vsubps vsubsd vsubss vtestps vtestpd vucomisd vucomiss vunpckhpd vunpckhps vunpcklpd vunpcklps vxorpd vxorps vzeroall vzeroupper pclmullqlqdq pclmulhqlqdq pclmullqhqdq pclmulhqhqdq pclmulqdq vpclmullqlqdq vpclmulhqlqdq vpclmullqhqdq vpclmulhqhqdq vpclmulqdq vfmadd132ps vfmadd132pd vfmadd312ps vfmadd312pd vfmadd213ps vfmadd213pd vfmadd123ps vfmadd123pd vfmadd231ps vfmadd231pd vfmadd321ps vfmadd321pd vfmaddsub132ps vfmaddsub132pd vfmaddsub312ps vfmaddsub312pd vfmaddsub213ps vfmaddsub213pd vfmaddsub123ps vfmaddsub123pd vfmaddsub231ps vfmaddsub231pd vfmaddsub321ps vfmaddsub321pd vfmsub132ps vfmsub132pd vfmsub312ps vfmsub312pd vfmsub213ps vfmsub213pd vfmsub123ps vfmsub123pd vfmsub231ps vfmsub231pd vfmsub321ps vfmsub321pd vfmsubadd132ps vfmsubadd132pd vfmsubadd312ps vfmsubadd312pd vfmsubadd213ps vfmsubadd213pd vfmsubadd123ps vfmsubadd123pd vfmsubadd231ps vfmsubadd231pd vfmsubadd321ps vfmsubadd321pd vfnmadd132ps vfnmadd132pd vfnmadd312ps vfnmadd312pd vfnmadd213ps vfnmadd213pd vfnmadd123ps vfnmadd123pd vfnmadd231ps vfnmadd231pd vfnmadd321ps vfnmadd321pd vfnmsub132ps vfnmsub132pd vfnmsub312ps vfnmsub312pd vfnmsub213ps vfnmsub213pd vfnmsub123ps vfnmsub123pd vfnmsub231ps vfnmsub231pd vfnmsub321ps vfnmsub321pd vfmadd132ss vfmadd132sd vfmadd312ss vfmadd312sd vfmadd213ss vfmadd213sd vfmadd123ss vfmadd123sd vfmadd231ss vfmadd231sd vfmadd321ss vfmadd321sd vfmsub132ss vfmsub132sd vfmsub312ss vfmsub312sd vfmsub213ss vfmsub213sd vfmsub123ss vfmsub123sd vfmsub231ss vfmsub231sd vfmsub321ss vfmsub321sd vfnmadd132ss vfnmadd132sd vfnmadd312ss vfnmadd312sd vfnmadd213ss vfnmadd213sd vfnmadd123ss vfnmadd123sd vfnmadd231ss vfnmadd231sd vfnmadd321ss vfnmadd321sd vfnmsub132ss vfnmsub132sd vfnmsub312ss vfnmsub312sd vfnmsub213ss vfnmsub213sd vfnmsub123ss vfnmsub123sd vfnmsub231ss vfnmsub231sd vfnmsub321ss vfnmsub321sd rdfsbase rdgsbase rdrand wrfsbase wrgsbase vcvtph2ps vcvtps2ph adcx adox rdseed clac stac xstore xcryptecb xcryptcbc xcryptctr xcryptcfb xcryptofb montmul xsha1 xsha256 llwpcb slwpcb lwpval lwpins vfmaddpd vfmaddps vfmaddsd vfmaddss vfmaddsubpd vfmaddsubps vfmsubaddpd vfmsubaddps vfmsubpd vfmsubps vfmsubsd vfmsubss vfnmaddpd vfnmaddps vfnmaddsd vfnmaddss vfnmsubpd vfnmsubps vfnmsubsd vfnmsubss vfrczpd vfrczps vfrczsd vfrczss vpcmov vpcomb vpcomd vpcomq vpcomub vpcomud vpcomuq vpcomuw vpcomw vphaddbd vphaddbq vphaddbw vphadddq vphaddubd vphaddubq vphaddubw vphaddudq vphadduwd vphadduwq vphaddwd vphaddwq vphsubbw vphsubdq vphsubwd vpmacsdd vpmacsdqh vpmacsdql vpmacssdd vpmacssdqh vpmacssdql vpmacsswd vpmacssww vpmacswd vpmacsww vpmadcsswd vpmadcswd vpperm vprotb vprotd vprotq vprotw vpshab vpshad vpshaq vpshaw vpshlb vpshld vpshlq vpshlw vbroadcasti128 vpblendd vpbroadcastb vpbroadcastw vpbroadcastd vpbroadcastq vpermd vpermpd vpermps vpermq vperm2i128 vextracti128 vinserti128 vpmaskmovd vpmaskmovq vpsllvd vpsllvq vpsravd vpsrlvd vpsrlvq vgatherdpd vgatherqpd vgatherdps vgatherqps vpgatherdd vpgatherqd vpgatherdq vpgatherqq xabort xbegin xend xtest andn bextr blci blcic blsi blsic blcfill blsfill blcmsk blsmsk blsr blcs bzhi mulx pdep pext rorx sarx shlx shrx tzcnt tzmsk t1mskc valignd valignq vblendmpd vblendmps vbroadcastf32x4 vbroadcastf64x4 vbroadcasti32x4 vbroadcasti64x4 vcompresspd vcompressps vcvtpd2udq vcvtps2udq vcvtsd2usi vcvtss2usi vcvttpd2udq vcvttps2udq vcvttsd2usi vcvttss2usi vcvtudq2pd vcvtudq2ps vcvtusi2sd vcvtusi2ss vexpandpd vexpandps vextractf32x4 vextractf64x4 vextracti32x4 vextracti64x4 vfixupimmpd vfixupimmps vfixupimmsd vfixupimmss vgetexppd vgetexpps vgetexpsd vgetexpss vgetmantpd vgetmantps vgetmantsd vgetmantss vinsertf32x4 vinsertf64x4 vinserti32x4 vinserti64x4 vmovdqa32 vmovdqa64 vmovdqu32 vmovdqu64 vpabsq vpandd vpandnd vpandnq vpandq vpblendmd vpblendmq vpcmpltd vpcmpled vpcmpneqd vpcmpnltd vpcmpnled vpcmpd vpcmpltq vpcmpleq vpcmpneqq vpcmpnltq vpcmpnleq vpcmpq vpcmpequd vpcmpltud vpcmpleud vpcmpnequd vpcmpnltud vpcmpnleud vpcmpud vpcmpequq vpcmpltuq vpcmpleuq vpcmpnequq vpcmpnltuq vpcmpnleuq vpcmpuq vpcompressd vpcompressq vpermi2d vpermi2pd vpermi2ps vpermi2q vpermt2d vpermt2pd vpermt2ps vpermt2q vpexpandd vpexpandq vpmaxsq vpmaxuq vpminsq vpminuq vpmovdb vpmovdw vpmovqb vpmovqd vpmovqw vpmovsdb vpmovsdw vpmovsqb vpmovsqd vpmovsqw vpmovusdb vpmovusdw vpmovusqb vpmovusqd vpmovusqw vpord vporq vprold vprolq vprolvd vprolvq vprord vprorq vprorvd vprorvq vpscatterdd vpscatterdq vpscatterqd vpscatterqq vpsraq vpsravq vpternlogd vpternlogq vptestmd vptestmq vptestnmd vptestnmq vpxord vpxorq vrcp14pd vrcp14ps vrcp14sd vrcp14ss vrndscalepd vrndscaleps vrndscalesd vrndscaless vrsqrt14pd vrsqrt14ps vrsqrt14sd vrsqrt14ss vscalefpd vscalefps vscalefsd vscalefss vscatterdpd vscatterdps vscatterqpd vscatterqps vshuff32x4 vshuff64x2 vshufi32x4 vshufi64x2 kandnw kandw kmovw knotw kortestw korw kshiftlw kshiftrw kunpckbw kxnorw kxorw vpbroadcastmb2q vpbroadcastmw2d vpconflictd vpconflictq vplzcntd vplzcntq vexp2pd vexp2ps vrcp28pd vrcp28ps vrcp28sd vrcp28ss vrsqrt28pd vrsqrt28ps vrsqrt28sd vrsqrt28ss vgatherpf0dpd vgatherpf0dps vgatherpf0qpd vgatherpf0qps vgatherpf1dpd vgatherpf1dps vgatherpf1qpd vgatherpf1qps vscatterpf0dpd vscatterpf0dps vscatterpf0qpd vscatterpf0qps vscatterpf1dpd vscatterpf1dps vscatterpf1qpd vscatterpf1qps prefetchwt1 bndmk bndcl bndcu bndcn bndmov bndldx bndstx sha1rnds4 sha1nexte sha1msg1 sha1msg2 sha256rnds2 sha256msg1 sha256msg2 hint_nop0 hint_nop1 hint_nop2 hint_nop3 hint_nop4 hint_nop5 hint_nop6 hint_nop7 hint_nop8 hint_nop9 hint_nop10 hint_nop11 hint_nop12 hint_nop13 hint_nop14 hint_nop15 hint_nop16 hint_nop17 hint_nop18 hint_nop19 hint_nop20 hint_nop21 hint_nop22 hint_nop23 hint_nop24 hint_nop25 hint_nop26 hint_nop27 hint_nop28 hint_nop29 hint_nop30 hint_nop31 hint_nop32 hint_nop33 hint_nop34 hint_nop35 hint_nop36 hint_nop37 hint_nop38 hint_nop39 hint_nop40 hint_nop41 hint_nop42 hint_nop43 hint_nop44 hint_nop45 hint_nop46 hint_nop47 hint_nop48 hint_nop49 hint_nop50 hint_nop51 hint_nop52 hint_nop53 hint_nop54 hint_nop55 hint_nop56 hint_nop57 hint_nop58 hint_nop59 hint_nop60 hint_nop61 hint_nop62 hint_nop63",
built_in:"ip eip rip al ah bl bh cl ch dl dh sil dil bpl spl r8b r9b r10b r11b r12b r13b r14b r15b ax bx cx dx si di bp sp r8w r9w r10w r11w r12w r13w r14w r15w eax ebx ecx edx esi edi ebp esp eip r8d r9d r10d r11d r12d r13d r14d r15d rax rbx rcx rdx rsi rdi rbp rsp r8 r9 r10 r11 r12 r13 r14 r15 cs ds es fs gs ss st st0 st1 st2 st3 st4 st5 st6 st7 mm0 mm1 mm2 mm3 mm4 mm5 mm6 mm7 xmm0  xmm1  xmm2  xmm3  xmm4  xmm5  xmm6  xmm7  xmm8  xmm9 xmm10  xmm11 xmm12 xmm13 xmm14 xmm15 xmm16 xmm17 xmm18 xmm19 xmm20 xmm21 xmm22 xmm23 xmm24 xmm25 xmm26 xmm27 xmm28 xmm29 xmm30 xmm31 ymm0  ymm1  ymm2  ymm3  ymm4  ymm5  ymm6  ymm7  ymm8  ymm9 ymm10  ymm11 ymm12 ymm13 ymm14 ymm15 ymm16 ymm17 ymm18 ymm19 ymm20 ymm21 ymm22 ymm23 ymm24 ymm25 ymm26 ymm27 ymm28 ymm29 ymm30 ymm31 zmm0  zmm1  zmm2  zmm3  zmm4  zmm5  zmm6  zmm7  zmm8  zmm9 zmm10  zmm11 zmm12 zmm13 zmm14 zmm15 zmm16 zmm17 zmm18 zmm19 zmm20 zmm21 zmm22 zmm23 zmm24 zmm25 zmm26 zmm27 zmm28 zmm29 zmm30 zmm31 k0 k1 k2 k3 k4 k5 k6 k7 bnd0 bnd1 bnd2 bnd3 cr0 cr1 cr2 cr3 cr4 cr8 dr0 dr1 dr2 dr3 dr8 tr3 tr4 tr5 tr6 tr7 r0 r1 r2 r3 r4 r5 r6 r7 r0b r1b r2b r3b r4b r5b r6b r7b r0w r1w r2w r3w r4w r5w r6w r7w r0d r1d r2d r3d r4d r5d r6d r7d r0h r1h r2h r3h r0l r1l r2l r3l r4l r5l r6l r7l r8l r9l r10l r11l r12l r13l r14l r15l db dw dd dq dt ddq do dy dz resb resw resd resq rest resdq reso resy resz incbin equ times byte word dword qword nosplit rel abs seg wrt strict near far a32 ptr",
meta:"%define %xdefine %+ %undef %defstr %deftok %assign %strcat %strlen %substr %rotate %elif %else %endif %if %ifmacro %ifctx %ifidn %ifidni %ifid %ifnum %ifstr %iftoken %ifempty %ifenv %error %warning %fatal %rep %endrep %include %push %pop %repl %pathsearch %depend %use %arg %stacksize %local %line %comment %endcomment .nolist __FILE__ __LINE__ __SECT__  __BITS__ __OUTPUT_FORMAT__ __DATE__ __TIME__ __DATE_NUM__ __TIME_NUM__ __UTC_DATE__ __UTC_TIME__ __UTC_DATE_NUM__ __UTC_TIME_NUM__  __PASS__ struc endstruc istruc at iend align alignb sectalign daz nodaz up down zero default option assume public bits use16 use32 use64 default section segment absolute extern global common cpu float __utf16__ __utf16le__ __utf16be__ __utf32__ __utf32le__ __utf32be__ __float8__ __float16__ __float32__ __float64__ __float80m__ __float80e__ __float128l__ __float128h__ __Infinity__ __QNaN__ __SNaN__ Inf NaN QNaN SNaN float8 float16 float32 float64 float80m float80e float128l float128h __FLOAT_DAZ__ __FLOAT_ROUND__ __FLOAT__"
},contains:[s.COMMENT(";","$",{relevance:0}),{className:"number",variants:[{
begin:"\\b(?:([0-9][0-9_]*)?\\.[0-9_]*(?:[eE][+-]?[0-9_]+)?|(0[Xx])?[0-9][0-9_]*(\\.[0-9_]*)?(?:[pP](?:[+-]?[0-9_]+)?)?)\\b",
relevance:0},{begin:"\\$[0-9][0-9A-Fa-f]*",relevance:0},{
begin:"\\b(?:[0-9A-Fa-f][0-9A-Fa-f_]*[Hh]|[0-9][0-9_]*[DdTt]?|[0-7][0-7_]*[QqOo]|[0-1][0-1_]*[BbYy])\\b"
},{
begin:"\\b(?:0[Xx][0-9A-Fa-f_]+|0[DdTt][0-9_]+|0[QqOo][0-7_]+|0[BbYy][0-1_]+)\\b"
}]},s.QUOTE_STRING_MODE,{className:"string",variants:[{begin:"'",end:"[^\\\\]'"
},{begin:"`",end:"[^\\\\]`"}],relevance:0},{className:"symbol",variants:[{
begin:"^\\s*[A-Za-z._?][A-Za-z0-9_$#@~.?]*(:|\\s+label)"},{
begin:"^\\s*%%[A-Za-z0-9_$#@~.?]*:"}],relevance:0},{className:"subst",
begin:"%[0-9]+",relevance:0},{className:"subst",begin:"%!S+",relevance:0},{
className:"meta",begin:/^\s*\.[\w_-]+/}]})})());hljs.registerLanguage("typescript",(()=>{"use strict"
;const e="[A-Za-z$_][0-9A-Za-z$_]*",n=["as","in","of","if","for","while","finally","var","new","function","do","return","void","else","break","catch","instanceof","with","throw","case","default","try","switch","continue","typeof","delete","let","yield","const","class","debugger","async","await","static","import","from","export","extends"],a=["true","false","null","undefined","NaN","Infinity"],s=[].concat(["setInterval","setTimeout","clearInterval","clearTimeout","require","exports","eval","isFinite","isNaN","parseFloat","parseInt","decodeURI","decodeURIComponent","encodeURI","encodeURIComponent","escape","unescape"],["arguments","this","super","console","window","document","localStorage","module","global"],["Intl","DataView","Number","Math","Date","String","RegExp","Object","Function","Boolean","Error","Symbol","Set","Map","WeakSet","WeakMap","Proxy","Reflect","JSON","Promise","Float64Array","Int16Array","Int32Array","Int8Array","Uint16Array","Uint32Array","Float32Array","Array","Uint8Array","Uint8ClampedArray","ArrayBuffer"],["EvalError","InternalError","RangeError","ReferenceError","SyntaxError","TypeError","URIError"])
;function t(e){return i("(?=",e,")")}function i(...e){return e.map((e=>{
return(n=e)?"string"==typeof n?n:n.source:null;var n})).join("")}return r=>{
const c={$pattern:e,
keyword:n.concat(["type","namespace","typedef","interface","public","private","protected","implements","declare","abstract","readonly"]).join(" "),
literal:a.join(" "),
built_in:s.concat(["any","void","number","boolean","string","object","never","enum"]).join(" ")
},o={className:"meta",begin:"@[A-Za-z$_][0-9A-Za-z$_]*"},l=(e,n,a)=>{
const s=e.contains.findIndex((e=>e.label===n))
;if(-1===s)throw Error("can not find mode to replace");e.contains.splice(s,1,a)
},b=(r=>{const c=e,o={begin:/<[A-Za-z0-9\\._:-]+/,
end:/\/[A-Za-z0-9\\._:-]+>|\/>/,isTrulyOpeningTag:(e,n)=>{
const a=e[0].length+e.index,s=e.input[a];"<"!==s?">"===s&&(((e,{after:n})=>{
const a="</"+e[0].slice(1);return-1!==e.input.indexOf(a,n)})(e,{after:a
})||n.ignoreMatch()):n.ignoreMatch()}},l={$pattern:e,keyword:n.join(" "),
literal:a.join(" "),built_in:s.join(" ")
},b="\\.([0-9](_?[0-9])*)",d="0|[1-9](_?[0-9])*|0[0-7]*[89][0-9]*",g={
className:"number",variants:[{
begin:`(\\b(${d})((${b})|\\.)?|(${b}))[eE][+-]?([0-9](_?[0-9])*)\\b`},{
begin:`\\b(${d})\\b((${b})\\b|\\.)?|(${b})\\b`},{
begin:"\\b(0|[1-9](_?[0-9])*)n\\b"},{
begin:"\\b0[xX][0-9a-fA-F](_?[0-9a-fA-F])*n?\\b"},{
begin:"\\b0[bB][0-1](_?[0-1])*n?\\b"},{begin:"\\b0[oO][0-7](_?[0-7])*n?\\b"},{
begin:"\\b0[0-7]+n?\\b"}],relevance:0},u={className:"subst",begin:"\\$\\{",
end:"\\}",keywords:l,contains:[]},E={begin:"html`",end:"",starts:{end:"`",
returnEnd:!1,contains:[r.BACKSLASH_ESCAPE,u],subLanguage:"xml"}},m={
begin:"css`",end:"",starts:{end:"`",returnEnd:!1,
contains:[r.BACKSLASH_ESCAPE,u],subLanguage:"css"}},_={className:"string",
begin:"`",end:"`",contains:[r.BACKSLASH_ESCAPE,u]},y={className:"comment",
variants:[r.COMMENT(/\/\*\*(?!\/)/,"\\*/",{relevance:0,contains:[{
className:"doctag",begin:"@[A-Za-z]+",contains:[{className:"type",begin:"\\{",
end:"\\}",relevance:0},{className:"variable",begin:c+"(?=\\s*(-)|$)",
endsParent:!0,relevance:0},{begin:/(?=[^\n])\s/,relevance:0}]}]
}),r.C_BLOCK_COMMENT_MODE,r.C_LINE_COMMENT_MODE]
},p=[r.APOS_STRING_MODE,r.QUOTE_STRING_MODE,E,m,_,g,r.REGEXP_MODE]
;u.contains=p.concat({begin:/\{/,end:/\}/,keywords:l,contains:["self"].concat(p)
});const N=[].concat(y,u.contains),f=N.concat([{begin:/\(/,end:/\)/,keywords:l,
contains:["self"].concat(N)}]),A={className:"params",begin:/\(/,end:/\)/,
excludeBegin:!0,excludeEnd:!0,keywords:l,contains:f};return{name:"Javascript",
aliases:["js","jsx","mjs","cjs"],keywords:l,exports:{PARAMS_CONTAINS:f},
illegal:/#(?![$_A-z])/,contains:[r.SHEBANG({label:"shebang",binary:"node",
relevance:5}),{label:"use_strict",className:"meta",relevance:10,
begin:/^\s*['"]use (strict|asm)['"]/
},r.APOS_STRING_MODE,r.QUOTE_STRING_MODE,E,m,_,y,g,{
begin:i(/[{,\n]\s*/,t(i(/(((\/\/.*$)|(\/\*(\*[^/]|[^*])*\*\/))\s*)*/,c+"\\s*:"))),
relevance:0,contains:[{className:"attr",begin:c+t("\\s*:"),relevance:0}]},{
begin:"("+r.RE_STARTERS_RE+"|\\b(case|return|throw)\\b)\\s*",
keywords:"return throw case",contains:[y,r.REGEXP_MODE,{className:"function",
begin:"(\\([^()]*(\\([^()]*(\\([^()]*\\)[^()]*)*\\)[^()]*)*\\)|"+r.UNDERSCORE_IDENT_RE+")\\s*=>",
returnBegin:!0,end:"\\s*=>",contains:[{className:"params",variants:[{
begin:r.UNDERSCORE_IDENT_RE,relevance:0},{className:null,begin:/\(\s*\)/,skip:!0
},{begin:/\(/,end:/\)/,excludeBegin:!0,excludeEnd:!0,keywords:l,contains:f}]}]
},{begin:/,/,relevance:0},{className:"",begin:/\s/,end:/\s*/,skip:!0},{
variants:[{begin:"<>",end:"</>"},{begin:o.begin,"on:begin":o.isTrulyOpeningTag,
end:o.end}],subLanguage:"xml",contains:[{begin:o.begin,end:o.end,skip:!0,
contains:["self"]}]}],relevance:0},{className:"function",
beginKeywords:"function",end:/[{;]/,excludeEnd:!0,keywords:l,
contains:["self",r.inherit(r.TITLE_MODE,{begin:c}),A],illegal:/%/},{
beginKeywords:"while if switch catch for"},{className:"function",
begin:r.UNDERSCORE_IDENT_RE+"\\([^()]*(\\([^()]*(\\([^()]*\\)[^()]*)*\\)[^()]*)*\\)\\s*\\{",
returnBegin:!0,contains:[A,r.inherit(r.TITLE_MODE,{begin:c})]},{variants:[{
begin:"\\."+c},{begin:"\\$"+c}],relevance:0},{className:"class",
beginKeywords:"class",end:/[{;=]/,excludeEnd:!0,illegal:/[:"[\]]/,contains:[{
beginKeywords:"extends"},r.UNDERSCORE_TITLE_MODE]},{begin:/\b(?=constructor)/,
end:/[{;]/,excludeEnd:!0,contains:[r.inherit(r.TITLE_MODE,{begin:c}),"self",A]
},{begin:"(get|set)\\s+(?="+c+"\\()",end:/\{/,keywords:"get set",
contains:[r.inherit(r.TITLE_MODE,{begin:c}),{begin:/\(\)/},A]},{begin:/\$[(.]/}]
}})(r)
;return Object.assign(b.keywords,c),b.exports.PARAMS_CONTAINS.push(o),b.contains=b.contains.concat([o,{
beginKeywords:"namespace",end:/\{/,excludeEnd:!0},{beginKeywords:"interface",
end:/\{/,excludeEnd:!0,keywords:"interface extends"
}]),l(b,"shebang",r.SHEBANG()),l(b,"use_strict",{className:"meta",relevance:10,
begin:/^\s*['"]use strict['"]/
}),b.contains.find((e=>"function"===e.className)).relevance=0,Object.assign(b,{
name:"TypeScript",aliases:["ts"]}),b}})());hljs.registerLanguage("r",(()=>{"use strict";function e(...e){return e.map((e=>{
return(a=e)?"string"==typeof a?a:a.source:null;var a})).join("")}return a=>{
const n=/(?:(?:[a-zA-Z]|\.[._a-zA-Z])[._a-zA-Z0-9]*)|\.(?!\d)/;return{name:"R",
illegal:/->/,keywords:{$pattern:n,
keyword:"function if in break next repeat else for while",
literal:"NULL NA TRUE FALSE Inf NaN NA_integer_|10 NA_real_|10 NA_character_|10 NA_complex_|10",
built_in:"LETTERS letters month.abb month.name pi T F abs acos acosh all any anyNA Arg as.call as.character as.complex as.double as.environment as.integer as.logical as.null.default as.numeric as.raw asin asinh atan atanh attr attributes baseenv browser c call ceiling class Conj cos cosh cospi cummax cummin cumprod cumsum digamma dim dimnames emptyenv exp expression floor forceAndCall gamma gc.time globalenv Im interactive invisible is.array is.atomic is.call is.character is.complex is.double is.environment is.expression is.finite is.function is.infinite is.integer is.language is.list is.logical is.matrix is.na is.name is.nan is.null is.numeric is.object is.pairlist is.raw is.recursive is.single is.symbol lazyLoadDBfetch length lgamma list log max min missing Mod names nargs nzchar oldClass on.exit pos.to.env proc.time prod quote range Re rep retracemem return round seq_along seq_len seq.int sign signif sin sinh sinpi sqrt standardGeneric substitute sum switch tan tanh tanpi tracemem trigamma trunc unclass untracemem UseMethod xtfrm"
},compilerExtensions:[(a,n)=>{if(!a.beforeMatch)return
;if(a.starts)throw Error("beforeMatch cannot be used with starts")
;const i=Object.assign({},a);Object.keys(a).forEach((e=>{delete a[e]
})),a.begin=e(i.beforeMatch,e("(?=",i.begin,")")),a.starts={relevance:0,
contains:[Object.assign(i,{endsParent:!0})]},a.relevance=0,delete i.beforeMatch
}],contains:[a.COMMENT(/#'/,/$/,{contains:[{className:"doctag",
begin:"@examples",starts:{contains:[{begin:/\n/},{begin:/#'\s*(?=@[a-zA-Z]+)/,
endsParent:!0},{begin:/#'/,end:/$/,excludeBegin:!0}]}},{className:"doctag",
begin:"@param",end:/$/,contains:[{className:"variable",variants:[{begin:n},{
begin:/`(?:\\.|[^`\\])+`/}],endsParent:!0}]},{className:"doctag",
begin:/@[a-zA-Z]+/},{className:"meta-keyword",begin:/\\[a-zA-Z]+/}]
}),a.HASH_COMMENT_MODE,{className:"string",contains:[a.BACKSLASH_ESCAPE],
variants:[a.END_SAME_AS_BEGIN({begin:/[rR]"(-*)\(/,end:/\)(-*)"/
}),a.END_SAME_AS_BEGIN({begin:/[rR]"(-*)\{/,end:/\}(-*)"/
}),a.END_SAME_AS_BEGIN({begin:/[rR]"(-*)\[/,end:/\](-*)"/
}),a.END_SAME_AS_BEGIN({begin:/[rR]'(-*)\(/,end:/\)(-*)'/
}),a.END_SAME_AS_BEGIN({begin:/[rR]'(-*)\{/,end:/\}(-*)'/
}),a.END_SAME_AS_BEGIN({begin:/[rR]'(-*)\[/,end:/\](-*)'/}),{begin:'"',end:'"',
relevance:0},{begin:"'",end:"'",relevance:0}]},{className:"number",relevance:0,
beforeMatch:/([^a-zA-Z0-9._])/,variants:[{
match:/0[xX][0-9a-fA-F]+\.[0-9a-fA-F]*[pP][+-]?\d+i?/},{
match:/0[xX][0-9a-fA-F]+([pP][+-]?\d+)?[Li]?/},{
match:/(\d+(\.\d*)?|\.\d+)([eE][+-]?\d+)?[Li]?/}]},{begin:"%",end:"%"},{
begin:e(/[a-zA-Z][a-zA-Z_0-9]*/,"\\s+<-\\s+")},{begin:"`",end:"`",contains:[{
begin:/\\./}]}]}}})());