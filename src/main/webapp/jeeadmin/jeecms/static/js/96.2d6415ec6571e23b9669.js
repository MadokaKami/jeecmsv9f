webpackJsonp([96],{"6DhR":function(e,n,a){"use strict";function t(e){a("bBrM")}Object.defineProperty(n,"__esModule",{value:!0});var l=a("5HJ5"),i={mixins:[l.a],data:function(){return{params:{pageNo:"",pageSize:""}}},created:function(){this.initTableData(this.$api.apiInfoList,this.params)}},s=function(){var e=this,n=e.$createElement,a=e._self._c||n;return a("section",{directives:[{name:"loading",rawName:"v-loading",value:e.loading,expression:"loading"}],staticClass:"cms-body"},[a("div",{staticClass:"cms-list-header"},[a("el-button",{directives:[{name:"perms",rawName:"v-perms",value:"/apiManage/apiInfo/add",expression:"'/apiManage/apiInfo/add'"}],attrs:{type:"primary",icon:"el-icon-plus"},on:{click:function(n){e.routerLink("/apiManage/apiInfo/add","save",0)}}},[e._v("添加")])],1),e._v(" "),a("el-table",{staticStyle:{width:"100%"},attrs:{data:e.tableData,stripe:""},on:{"selection-change":e.checkIds}},[a("el-table-column",{attrs:{type:"selection",width:"65",align:"right"}}),e._v(" "),a("el-table-column",{attrs:{prop:"id",label:"ID",width:"50",align:"center"}}),e._v(" "),a("el-table-column",{attrs:{prop:"name",label:"名称",align:"center"}}),e._v(" "),a("el-table-column",{attrs:{prop:"url",label:"地址",align:"center"}}),e._v(" "),a("el-table-column",{attrs:{prop:"code",label:"接口代码",align:"center"}}),e._v(" "),a("el-table-column",{attrs:{prop:"disabled",label:"禁用",align:"center"},scopedSlots:e._u([{key:"default",fn:function(n){return a("div",{},[n.row.disabled?a("span",[e._v("是")]):a("span",[e._v("否")])])}}])}),e._v(" "),a("el-table-column",{attrs:{prop:"callTotalCount",label:"调用总次数",align:"center"}}),e._v(" "),a("el-table-column",{attrs:{prop:"callMonthCount",label:"月调用次数",align:"center"}}),e._v(" "),a("el-table-column",{attrs:{prop:"callWeekCount",label:"周调用次数",align:"center"}}),e._v(" "),a("el-table-column",{attrs:{prop:"callDayCount",label:"日调用次数",align:"center"}}),e._v(" "),a("el-table-column",{attrs:{label:"操作",align:"center"},scopedSlots:e._u([{key:"default",fn:function(n){return a("div",{},[a("cms-button",{directives:[{name:"perms",rawName:"v-perms",value:"/apiManage/apiInfo/edit",expression:"'/apiManage/apiInfo/edit'"}],attrs:{type:"edit"},nativeOn:{click:function(a){e.routerLink("/apiManage/apiInfo/edit","update",n.row.id)}}}),e._v(" "),a("cms-button",{directives:[{name:"perms",rawName:"v-perms",value:"/apiManage/apiInfo/delete",expression:"'/apiManage/apiInfo/delete'"}],attrs:{type:"delete"},nativeOn:{click:function(a){e.deleteBatch(e.$api.apiInfoDelete,n.row.id)}}})],1)}}])})],1),e._v(" "),a("div",{staticClass:"cms-list-footer"},[a("div",{staticClass:"cms-left"},[a("el-button",{directives:[{name:"perms",rawName:"v-perms",value:"/apiManage/apiMan/delete",expression:"'/apiManage/apiMan/delete'"}],attrs:{disabled:e.disabled},on:{click:function(n){e.deleteBatch(e.$api.apiInfoDelete,e.ids)}}},[e._v("批量删除")])],1),e._v(" "),a("cms-pagination",{attrs:{total:e.total},on:{change:e.getPages}})],1)],1)},r=[],o={render:s,staticRenderFns:r},c=o,p=a("8AGX"),d=t,u=p(i,c,!1,d,null,null);n.default=u.exports},bBrM:function(e,n,a){var t=a("zI/1");"string"==typeof t&&(t=[[e.i,t,""]]),t.locals&&(e.exports=t.locals);a("8bSs")("6c4e78e5",t,!0)},"zI/1":function(e,n,a){n=e.exports=a("l95E")(!1),n.push([e.i,"\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n",""])}});