"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
const _sfc_main = {
  __name: "compare",
  setup(__props) {
    const majors = common_vendor.ref([]);
    const analysis = common_vendor.ref("");
    common_vendor.onLoad((options) => {
      if (options.majorIds) {
        const majorIds = JSON.parse(decodeURIComponent(options.majorIds));
        loadCompareData(majorIds);
      }
    });
    const loadCompareData = async (majorIds) => {
      try {
        const res = await common_vendor.index.request({
          url: utils_request.buildApiUrl("/compare/majors"),
          method: "POST",
          data: { majorIds },
          header: {
            "Content-Type": "application/json"
          }
        });
        if (res.data.code === 200) {
          majors.value = res.data.data.majors;
          analysis.value = res.data.data.analysis;
        } else {
          common_vendor.index.showToast({
            title: res.data.message || "加载失败",
            icon: "none"
          });
        }
      } catch (error) {
        console.error("加载对比数据失败:", error);
        common_vendor.index.showToast({
          title: "加载失败",
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
        b: majors.value.length > 0
      }, majors.value.length > 0 ? common_vendor.e({
        c: common_vendor.f(majors.value, (major, index, i0) => {
          return {
            a: common_vendor.t(major.schoolName),
            b: common_vendor.t(major.majorName),
            c: index
          };
        }),
        d: common_vendor.f(majors.value, (major, index, i0) => {
          return {
            a: common_vendor.t(major.majorCode || "-"),
            b: index
          };
        }),
        e: common_vendor.f(majors.value, (major, index, i0) => {
          return {
            a: common_vendor.t(major.degreeType || "-"),
            b: index
          };
        }),
        f: common_vendor.f(majors.value, (major, index, i0) => {
          return {
            a: common_vendor.t(major.studyMode || "-"),
            b: index
          };
        }),
        g: common_vendor.f(majors.value, (major, index, i0) => {
          return {
            a: common_vendor.t(major.plannedEnrollment || "-"),
            b: index
          };
        }),
        h: common_vendor.f(majors.value, (major, index, i0) => {
          return common_vendor.e({
            a: major.examSubject1
          }, major.examSubject1 ? {
            b: common_vendor.t(major.examSubject1)
          } : {}, {
            c: major.examSubject2
          }, major.examSubject2 ? {
            d: common_vendor.t(major.examSubject2)
          } : {}, {
            e: major.examSubject3
          }, major.examSubject3 ? {
            f: common_vendor.t(major.examSubject3)
          } : {}, {
            g: major.examSubject4
          }, major.examSubject4 ? {
            h: common_vendor.t(major.examSubject4)
          } : {}, {
            i: !major.examSubject1
          }, !major.examSubject1 ? {} : {}, {
            j: index
          });
        }),
        i: analysis.value
      }, analysis.value ? {
        j: common_vendor.t(analysis.value)
      } : {}) : {});
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-a6c1bada"]]);
wx.createPage(MiniProgramPage);
