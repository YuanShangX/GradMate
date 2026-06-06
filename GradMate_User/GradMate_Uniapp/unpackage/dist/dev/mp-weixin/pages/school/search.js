"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const utils_request = require("../../utils/request.js");
const _sfc_main = {
  __name: "search",
  setup(__props) {
    const keyword = common_vendor.ref("");
    const showResult = common_vendor.ref(false);
    const resultTab = common_vendor.ref("school");
    const schoolResults = common_vendor.ref([]);
    const majorResults = common_vendor.ref([]);
    const hotTags = common_vendor.ref([
      "人工智能",
      "中国科学院大学",
      "中国科学技术大学",
      "哈尔滨工业大学",
      "储能科学与工程",
      "南京大学"
    ]);
    const hotNews = common_vendor.ref([
      "复试备考全流程规划",
      "2026研考网上确认时间...",
      "26考研网上确认，一次...",
      "26考研预报名流程图",
      "2026年统考考生需准备...",
      "2026考研时间定了！",
      "别学错！这些学校参考...",
      "超重要！2026院校初试...",
      "提醒！这些学校专业学...",
      "快检漏！这些学校专业..."
    ]);
    const hotSchools = common_vendor.ref([
      "北京大学",
      "中国科学院大学",
      "清华大学",
      "中国人民大学",
      "苏州大学",
      "浙江大学",
      "南京大学",
      "北京师范大学",
      "郑州大学",
      "厦门大学"
    ]);
    const handleInput = () => {
      if (keyword.value) {
        showResult.value = false;
      }
    };
    const handleSearch = async () => {
      if (!keyword.value.trim()) {
        common_vendor.index.showToast({
          title: "请输入搜索内容",
          icon: "none"
        });
        return;
      }
      showResult.value = true;
      try {
        const schoolRes = await utils_request.get("/school/list", {
          schoolName: keyword.value,
          pageNum: 1,
          pageSize: 20
        });
        if (schoolRes.code === 200) {
          schoolResults.value = schoolRes.data.records.map((item) => ({
            ...item,
            tag: "双一流"
          }));
        }
        const majorRes = await utils_request.get("/major/search", {
          majorName: keyword.value,
          pageNum: 1,
          pageSize: 20
        });
        if (majorRes.code === 200) {
          majorResults.value = majorRes.data.records;
        }
      } catch (error) {
        console.error("搜索失败:", error);
      }
    };
    const searchTag = (tag) => {
      keyword.value = tag;
      handleSearch();
    };
    const clearKeyword = () => {
      keyword.value = "";
      showResult.value = false;
      schoolResults.value = [];
      majorResults.value = [];
    };
    const goBack = () => {
      common_vendor.index.navigateBack();
    };
    const goToSchoolDetail = (school) => {
      common_vendor.index.navigateTo({
        url: `/pages/school/detail?id=${school.id}`
      });
    };
    const goToMajorDetail = (major) => {
      common_vendor.index.navigateTo({
        url: `/pages/major/detail?id=${major.id}`
      });
    };
    const showToast = (title) => {
      common_vendor.index.showToast({
        title,
        icon: "none"
      });
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_assets._imports_0$1,
        b: common_vendor.o(handleSearch),
        c: common_vendor.o([($event) => keyword.value = $event.detail.value, handleInput]),
        d: keyword.value,
        e: keyword.value
      }, keyword.value ? {
        f: common_assets._imports_1,
        g: common_vendor.o(clearKeyword)
      } : {}, {
        h: common_vendor.o(goBack),
        i: keyword.value && !showResult.value
      }, keyword.value && !showResult.value ? {} : {}, {
        j: !keyword.value && !showResult.value
      }, !keyword.value && !showResult.value ? {
        k: common_vendor.f(hotTags.value, (tag, index, i0) => {
          return {
            a: common_vendor.t(tag),
            b: index,
            c: common_vendor.o(($event) => searchTag(tag), index)
          };
        }),
        l: common_vendor.f(hotNews.value, (item, index, i0) => {
          return {
            a: common_vendor.t(index + 1),
            b: index < 3 ? 1 : "",
            c: common_vendor.t(item),
            d: index,
            e: common_vendor.o(($event) => showToast("查看资讯"), index)
          };
        }),
        m: common_vendor.f(hotSchools.value, (item, index, i0) => {
          return {
            a: common_vendor.t(index + 1),
            b: index < 3 ? 1 : "",
            c: common_vendor.t(item),
            d: index,
            e: common_vendor.o(($event) => showToast("查看院校"), index)
          };
        })
      } : {}, {
        n: showResult.value
      }, showResult.value ? common_vendor.e({
        o: resultTab.value === "school"
      }, resultTab.value === "school" ? {} : {}, {
        p: resultTab.value === "school" ? 1 : "",
        q: common_vendor.o(($event) => resultTab.value = "school"),
        r: resultTab.value === "major"
      }, resultTab.value === "major" ? {} : {}, {
        s: resultTab.value === "major" ? 1 : "",
        t: common_vendor.o(($event) => resultTab.value = "major"),
        v: resultTab.value === "school"
      }, resultTab.value === "school" ? common_vendor.e({
        w: common_vendor.f(schoolResults.value, (school, index, i0) => {
          return {
            a: school.logo || "/static/default-school.png",
            b: common_vendor.t(school.schoolName),
            c: common_vendor.t(school.tag),
            d: index,
            e: common_vendor.o(($event) => goToSchoolDetail(school), index)
          };
        }),
        x: schoolResults.value.length === 0
      }, schoolResults.value.length === 0 ? {
        y: common_assets._imports_2
      } : {}) : {}, {
        z: resultTab.value === "major"
      }, resultTab.value === "major" ? common_vendor.e({
        A: common_vendor.f(majorResults.value, (major, index, i0) => {
          return {
            a: common_vendor.t(major.majorName),
            b: common_vendor.t(major.schoolName),
            c: index,
            d: common_vendor.o(($event) => goToMajorDetail(major), index)
          };
        }),
        B: majorResults.value.length === 0
      }, majorResults.value.length === 0 ? {
        C: common_assets._imports_2
      } : {}) : {}) : {});
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-55e16762"]]);
wx.createPage(MiniProgramPage);
