"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
const _sfc_main = {
  __name: "compare",
  setup(__props) {
    const schools = common_vendor.ref([]);
    const analysis = common_vendor.ref("");
    common_vendor.onLoad((options) => {
      if (options.schoolIds) {
        const schoolIds = JSON.parse(decodeURIComponent(options.schoolIds));
        loadCompareData(schoolIds);
      }
    });
    const loadCompareData = async (schoolIds) => {
      try {
        console.log("请求对比数据，schoolIds:", schoolIds);
        const res = await common_vendor.index.request({
          url: utils_request.buildApiUrl("/compare/schools"),
          method: "POST",
          data: { schoolIds },
          header: {
            "Content-Type": "application/json"
          }
        });
        console.log("对比接口响应:", res);
        if (res.data.code === 200) {
          schools.value = res.data.data.schools;
          analysis.value = res.data.data.analysis;
        } else {
          console.error("对比失败:", res.data);
          common_vendor.index.showToast({
            title: res.data.message || "加载失败",
            icon: "none"
          });
        }
      } catch (error) {
        console.error("加载对比数据失败:", error);
        common_vendor.index.showToast({
          title: "加载失败: " + (error.message || "网络错误"),
          icon: "none"
        });
      }
    };
    const goBack = () => {
      common_vendor.index.navigateBack();
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.o(goBack),
        b: schools.value.length > 0
      }, schools.value.length > 0 ? common_vendor.e({
        c: common_vendor.f(schools.value, (school, index, i0) => {
          return common_vendor.e({
            a: school.logo
          }, school.logo ? {
            b: school.logo
          } : {}, {
            c: common_vendor.t(school.schoolName),
            d: index
          });
        }),
        d: common_vendor.f(schools.value, (school, index, i0) => {
          return {
            a: common_vendor.t(school.location || "-"),
            b: index
          };
        }),
        e: common_vendor.f(schools.value, (school, index, i0) => {
          return {
            a: common_vendor.t(school.subordinate || "-"),
            b: index
          };
        }),
        f: common_vendor.f(schools.value, (school, index, i0) => {
          return common_vendor.e({
            a: school.isSyl === 1
          }, school.isSyl === 1 ? {} : {}, {
            b: school.is211 === 1
          }, school.is211 === 1 ? {} : {}, {
            c: school.isZhx === 1
          }, school.isZhx === 1 ? {} : {}, {
            d: !school.isSyl && !school.is211 && !school.isZhx
          }, !school.isSyl && !school.is211 && !school.isZhx ? {} : {}, {
            e: index
          });
        }),
        g: common_vendor.f(schools.value, (school, index, i0) => {
          return {
            a: common_vendor.t(school.majorCount || 0),
            b: index
          };
        }),
        h: analysis.value
      }, analysis.value ? {
        i: common_vendor.t(analysis.value)
      } : {}) : {});
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-28f4d9c7"]]);
wx.createPage(MiniProgramPage);
