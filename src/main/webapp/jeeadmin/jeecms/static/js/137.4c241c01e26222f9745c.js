webpackJsonp([137],{JFsn:function(a,t,n){"use strict";function e(a){n("aYiK")}Object.defineProperty(t,"__esModule",{value:!0});var s=n("a3Yh"),l=n.n(s),o=n("lcoF"),i=n("2sCs"),c=n.n(i),r=n("x1ym"),d={mixins:[o.a],data:function(){var a,t=r.a.required("此项必填");r.a.number("只能输入数字");return a={root:this.$route.query.pid,params:{queryUsername:"",https:""},domainCheck:!1,accessPathCheck:!1,rules:{name:[t],shortName:[t],path:[t],relativePath:[t],protocol:[t],dynamicSuffix:[t],staticSuffix:[t],afterCheck:[t],master:[t],resycleOn:[t],mobileStaticSync:[t],resouceSync:[t],pageSync:[t],staticIndex:[t],domain:[t],accessPath:""},ftpList:[],ossList:[],tplList:[],editTable:!1,dateList:!0},l()(a,"domainCheck",!1),l()(a,"accessPathCheck",!1),l()(a,"siteIdCheckRes",!1),a},methods:{siteIdCheck:function(a){var t=this;if(!a)return this.siteIdCheckRes=!1,!1;c.a.post(this.$api.siteCheckMaster,{siteId:""}).then(function(a){a.body.result||(t.siteIdCheckRes=!0,t.loading=!1)}).catch(function(a){t.loading=!1})},checkDomain:function(a){var t=this;if(""==a)return this.domainCheck=!1,!1;c.a.post(this.$api.siteCheckDomain,{siteId:"",domain:a}).then(function(a){a.body.result?(t.domainCheck=!1,t.loading=!1):(t.domainCheck=!0,t.loading=!1)}).catch(function(a){t.loading=!1})},checkAccessPath:function(a){var t=this;if(""==a)return this.accessPathCheck=!1,!1;c.a.post(this.$api.siteCheckAccessPath,{siteId:"",accessPath:a}).then(function(a){a.body.result?(t.accessPathCheck=!1,t.loading=!1):(t.accessPathCheck=!0,t.loading=!1)}).catch(function(a){t.loading=!1})},getDataInfo:function(){var a=this;this.domainCheck=!1,this.accessPathCheck=!1,this.siteIdCheckRes=!1;var t=this.$api;c.a.all([c.a.post(t.configGet),c.a.post(t.siteGet,{id:"0",root:""}),c.a.post(t.ftpList),c.a.post(t.ossList),c.a.post(t.tplList)]).then(c.a.spread(function(t,n,e,s,l){t.body.insideSite&&(a.rules.accessPath=[r.a.required("此项必填")]),a.dataInfo=n.body,a.ftpList=e.body,a.ossList=s.body,a.tplList=l.body,a.$refs.form.resetFields(),a.loading=!1})).catch(function(t){a.loading=!1})},save:function(){this.domainCheck||this.accessPathCheck||this.siteIdCheckRes||(this.dataInfo.root=this.root,this.saveDataInfo(!1,this.$api.siteSave,this.dataInfo,"/site/list"))}},created:function(){this.getDataInfo()}},f=function(){var a=this,t=a.$createElement,n=a._self._c||t;return n("section",{directives:[{name:"loading",rawName:"v-loading",value:a.loading,expression:"loading"}],staticClass:"cms-body"},[n("cms-back"),a._v(" "),n("el-form",{ref:"form",staticClass:"cms-form",attrs:{model:a.dataInfo,rules:a.rules,"label-width":"162px"}},[n("el-form-item",{staticClass:"flex-50",attrs:{label:"名称",prop:"name"}},[n("el-input",{staticClass:"cms-width",model:{value:a.dataInfo.name,callback:function(t){a.$set(a.dataInfo,"name",t)},expression:"dataInfo.name"}})],1),a._v(" "),n("el-form-item",{staticClass:"flex-50",attrs:{label:"站点简称",prop:"shortName"}},[n("el-input",{staticClass:"cms-width",model:{value:a.dataInfo.shortName,callback:function(t){a.$set(a.dataInfo,"shortName",t)},expression:"dataInfo.shortName"}})],1),a._v(" "),n("el-form-item",{staticClass:"flex-100",attrs:{label:"关键字",prop:"keywords"}},[n("el-input",{staticClass:"cms-width",model:{value:a.dataInfo.keywords,callback:function(t){a.$set(a.dataInfo,"keywords",t)},expression:"dataInfo.keywords"}})],1),a._v(" "),n("el-form-item",{staticClass:"flex-100",attrs:{label:"描述",prop:"description"}},[n("el-input",{staticClass:"cms-width",model:{value:a.dataInfo.description,callback:function(t){a.$set(a.dataInfo,"description",t)},expression:"dataInfo.description"}})],1),a._v(" "),n("el-form-item",{staticClass:"flex-50",attrs:{label:"域名",prop:"domain"}},[n("el-input",{staticClass:"cms-width",on:{change:a.checkDomain},model:{value:a.dataInfo.domain,callback:function(t){a.$set(a.dataInfo,"domain",t)},expression:"dataInfo.domain"}}),a._v(" "),n("span",{directives:[{name:"show",rawName:"v-show",value:a.domainCheck,expression:"domainCheck"}],staticClass:"red"},[a._v("域名已存在，无法使用")])],1),a._v(" "),n("el-form-item",{staticClass:"flex-50",attrs:{label:"路径",prop:"path"}},[n("el-input",{staticClass:"cms-width",model:{value:a.dataInfo.path,callback:function(t){a.$set(a.dataInfo,"path",t)},expression:"dataInfo.path"}}),a._v(" "),n("span",{staticClass:"gray"},[a._v("各站点资源的存放路径")])],1),a._v(" "),n("el-form-item",{staticClass:"flex-50",attrs:{label:"域名别名",prop:"domainAlias"}},[n("el-input",{staticClass:"cms-width",model:{value:a.dataInfo.domainAlias,callback:function(t){a.$set(a.dataInfo,"domainAlias",t)},expression:"dataInfo.domainAlias"}}),a._v(" "),n("span",{staticClass:"gray"},[a._v('多个请用","分开')])],1),a._v(" "),n("el-form-item",{staticClass:"flex-50",attrs:{label:"域名重定向",prop:"domainRedirect"}},[n("el-input",{staticClass:"cms-width",model:{value:a.dataInfo.domainRedirect,callback:function(t){a.$set(a.dataInfo,"domainRedirect",t)},expression:"dataInfo.domainRedirect"}}),a._v(" "),n("span",{staticClass:"gray"},[a._v('多个请用","分开')])],1),a._v(" "),n("el-form-item",{staticClass:"flex-50",attrs:{label:"使用相对路径",prop:"relativePath"}},[n("el-radio",{attrs:{label:!0},model:{value:a.dataInfo.relativePath,callback:function(t){a.$set(a.dataInfo,"relativePath",t)},expression:"dataInfo.relativePath"}},[a._v("是")]),a._v(" "),n("el-radio",{attrs:{label:!1},model:{value:a.dataInfo.relativePath,callback:function(t){a.$set(a.dataInfo,"relativePath",t)},expression:"dataInfo.relativePath"}},[a._v("否")])],1),a._v(" "),n("el-form-item",{staticClass:"flex-50",attrs:{label:"访问协议",prop:"protocol"}},[n("el-select",{staticClass:"cms-width",model:{value:a.dataInfo.protocol,callback:function(t){a.$set(a.dataInfo,"protocol",t)},expression:"dataInfo.protocol"}},[n("el-option",{attrs:{value:"http://",label:"http://"}}),a._v(" "),n("el-option",{attrs:{value:"https://",label:"https://"}})],1)],1),a._v(" "),n("el-form-item",{staticClass:"flex-50",attrs:{label:"动态页后缀",prop:"dynamicSuffix"}},[n("el-select",{staticClass:"cms-width",model:{value:a.dataInfo.dynamicSuffix,callback:function(t){a.$set(a.dataInfo,"dynamicSuffix",t)},expression:"dataInfo.dynamicSuffix"}},[n("el-option",{attrs:{value:".jhtm",label:".jhtm"}}),a._v(" "),n("el-option",{attrs:{value:".html",label:".html"}})],1),a._v(" "),n("span",{staticClass:"gray"},[a._v("建议使用.Jhtml为后缀，以避免冲突")])],1),a._v(" "),n("el-form-item",{staticClass:"flex-50",attrs:{label:"静态页后缀",prop:"staticSuffix"}},[n("el-select",{staticClass:"cms-width",model:{value:a.dataInfo.staticSuffix,callback:function(t){a.$set(a.dataInfo,"staticSuffix",t)},expression:"dataInfo.staticSuffix"}},[n("el-option",{attrs:{value:".html",label:".html"}}),a._v(" "),n("el-option",{attrs:{value:".shtml",label:".shtml"}})],1)],1),a._v(" "),n("el-form-item",{staticClass:"flex-50",attrs:{label:"静态页目录",prop:"staticDir"}},[n("el-input",{staticClass:"cms-width",model:{value:a.dataInfo.staticDir,callback:function(t){a.$set(a.dataInfo,"staticDir",t)},expression:"dataInfo.staticDir"}}),a._v(" "),n("el-checkbox",{attrs:{label:!0},model:{value:a.dataInfo.indexToRoot,callback:function(t){a.$set(a.dataInfo,"indexToRoot",t)},expression:"dataInfo.indexToRoot"}},[a._v("使用根目录")])],1),a._v(" "),n("el-form-item",{staticClass:"flex-50",attrs:{label:"手机静态页目录",prop:"staticMobileDir"}},[n("el-input",{staticClass:"cms-width",model:{value:a.dataInfo.staticMobileDir,callback:function(t){a.$set(a.dataInfo,"staticMobileDir",t)},expression:"dataInfo.staticMobileDir"}})],1),a._v(" "),n("el-form-item",{staticClass:"flex-50",attrs:{label:"手机静态页同步生成",prop:"mobileStaticSync"}},[n("el-radio",{attrs:{label:!0},model:{value:a.dataInfo.mobileStaticSync,callback:function(t){a.$set(a.dataInfo,"mobileStaticSync",t)},expression:"dataInfo.mobileStaticSync"}},[a._v("是")]),a._v(" "),n("el-radio",{attrs:{label:!1},model:{value:a.dataInfo.mobileStaticSync,callback:function(t){a.$set(a.dataInfo,"mobileStaticSync",t)},expression:"dataInfo.mobileStaticSync"}},[a._v("否")])],1),a._v(" "),n("el-form-item",{staticClass:"flex-50",attrs:{label:"资源自动同步FTP",prop:"resouceSync"}},[n("el-radio",{attrs:{label:!0},model:{value:a.dataInfo.resouceSync,callback:function(t){a.$set(a.dataInfo,"resouceSync",t)},expression:"dataInfo.resouceSync"}},[a._v("是")]),a._v(" "),n("el-radio",{attrs:{label:!1},model:{value:a.dataInfo.resouceSync,callback:function(t){a.$set(a.dataInfo,"resouceSync",t)},expression:"dataInfo.resouceSync"}},[a._v("否")])],1),a._v(" "),n("el-form-item",{staticClass:"flex-50",attrs:{label:"静态页自动同步FTP",prop:"pageSync"}},[n("el-radio",{attrs:{label:!0},model:{value:a.dataInfo.pageSync,callback:function(t){a.$set(a.dataInfo,"pageSync",t)},expression:"dataInfo.pageSync"}},[a._v("是")]),a._v(" "),n("el-radio",{attrs:{label:!1},model:{value:a.dataInfo.pageSync,callback:function(t){a.$set(a.dataInfo,"pageSync",t)},expression:"dataInfo.pageSync"}},[a._v("否")])],1),a._v(" "),n("el-form-item",{staticClass:"flex-50",attrs:{label:"静态页同步FTP",prop:"syncPageFtpId"}},[n("el-select",{staticClass:"cms-width",model:{value:a.dataInfo.syncPageFtpId,callback:function(t){a.$set(a.dataInfo,"syncPageFtpId",t)},expression:"dataInfo.syncPageFtpId"}},[n("el-option",{attrs:{label:"无",value:""}}),a._v(" "),a._l(a.ftpList,function(t,e){return n("el-option",{key:e,attrs:{value:t.id,label:t.name}},[a._v(a._s(t.name))])})],2)],1),a._v(" "),n("el-form-item",{staticClass:"flex-50",attrs:{label:"开启静态首页",prop:"staticIndex"}},[n("el-radio",{attrs:{label:!0},model:{value:a.dataInfo.staticIndex,callback:function(t){a.$set(a.dataInfo,"staticIndex",t)},expression:"dataInfo.staticIndex"}},[a._v("是")]),a._v(" "),n("el-radio",{attrs:{label:!1},model:{value:a.dataInfo.staticIndex,callback:function(t){a.$set(a.dataInfo,"staticIndex",t)},expression:"dataInfo.staticIndex"}},[a._v("否")])],1),a._v(" "),n("el-form-item",{staticClass:"flex-50",attrs:{label:"附件FTP"}},[n("el-select",{staticClass:"cms-width",model:{value:a.dataInfo.uploadFtpId,callback:function(t){a.$set(a.dataInfo,"uploadFtpId",t)},expression:"dataInfo.uploadFtpId"}},[n("el-option",{attrs:{value:"",label:"无"}},[a._v("无")]),a._v(" "),a._l(a.ftpList,function(t,e){return n("el-option",{key:e,attrs:{value:t.id,label:t.name}},[a._v(a._s(t.name))])})],2)],1),a._v(" "),n("el-form-item",{staticClass:"flex-50",attrs:{label:"开启回收站",prop:"resycleOn"}},[n("el-radio",{attrs:{label:!0},model:{value:a.dataInfo.resycleOn,callback:function(t){a.$set(a.dataInfo,"resycleOn",t)},expression:"dataInfo.resycleOn"}},[a._v("是")]),a._v(" "),n("el-radio",{attrs:{label:!1},model:{value:a.dataInfo.resycleOn,callback:function(t){a.$set(a.dataInfo,"resycleOn",t)},expression:"dataInfo.resycleOn"}},[a._v("否")])],1),a._v(" "),n("el-form-item",{staticClass:"flex-50",attrs:{label:"审核后"}},[n("el-select",{staticClass:"cms-width",model:{value:a.dataInfo.afterCheck,callback:function(t){a.$set(a.dataInfo,"afterCheck",t)},expression:"dataInfo.afterCheck"}},[n("el-option",{attrs:{value:1,label:"不能修改删除"}},[a._v("不能修改删除")]),a._v(" "),n("el-option",{attrs:{value:2,label:"修改后退回"}},[a._v("修改后退回")]),a._v(" "),n("el-option",{attrs:{value:3,label:"修改后不变"}},[a._v("修改后不变")])],1)],1),a._v(" "),n("el-form-item",{staticClass:"flex-50",attrs:{label:"云储存"}},[n("el-select",{staticClass:"cms-width",model:{value:a.dataInfo.ossId,callback:function(t){a.$set(a.dataInfo,"ossId",t)},expression:"dataInfo.ossId"}},[n("el-option",{attrs:{value:"",label:"无"}},[a._v("无")]),a._v(" "),a._l(a.ossList,function(a,t){return n("el-option",{key:t,attrs:{value:a.id,label:a.bucketName}})})],2)],1),a._v(" "),n("el-form-item",{staticClass:"flex-50"}),a._v(" "),n("div",{staticClass:"form-footer"},[n("el-button",{directives:[{name:"perms",rawName:"v-perms",value:"/siteConfig/add",expression:"'/siteConfig/add'"}],attrs:{type:"primary"},on:{click:function(t){a.save()}}},[a._v("提交")]),a._v(" "),n("el-button",{attrs:{type:"info"},on:{click:a.reset}},[a._v("重置")])],1)],1)],1)},m=[],p={render:f,staticRenderFns:m},u=p,v=n("8AGX"),h=e,I=v(d,u,!1,h,null,null);t.default=I.exports},aYiK:function(a,t,n){var e=n("bqXD");"string"==typeof e&&(e=[[a.i,e,""]]),e.locals&&(a.exports=e.locals);n("8bSs")("66f1193a",e,!0)},bqXD:function(a,t,n){t=a.exports=n("l95E")(!1),t.push([a.i,"\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n",""])}});