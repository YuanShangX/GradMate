"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
const _sfc_main = {
  data() {
    return {
      statusBarHeight: 0,
      step: 1,
      userId: null,
      schoolKeyword: "",
      majorKeyword: "",
      schoolList: [],
      majorList: [],
      selectedSchool: {},
      selectedMajor: {}
    };
  },
  onLoad() {
    const systemInfo = common_vendor.index.getSystemInfoSync();
    this.statusBarHeight = systemInfo.statusBarHeight || 0;
    this.userId = common_vendor.index.getStorageSync("userId");
    if (!this.userId) {
      common_vendor.index.showToast({
        title: "请先登录",
        icon: "none"
      });
      setTimeout(() => {
        common_vendor.index.navigateBack();
      }, 1500);
    }
  },
  methods: {
    // 返回上一页
    goBack() {
      common_vendor.index.navigateBack();
    },
    // 搜索院校
    async searchSchool() {
      if (!this.schoolKeyword.trim()) {
        this.schoolList = [];
        return;
      }
      try {
        const res = await utils_request.get("/school/list", {
          schoolName: this.schoolKeyword,
          pageNum: 1,
          pageSize: 20
        });
        if (res.code === 200) {
          this.schoolList = res.data.records || [];
        }
      } catch (error) {
        console.error("搜索院校失败:", error);
      }
    },
    // 选择院校
    selectSchool(school) {
      this.selectedSchool = school;
      this.step = 2;
      this.loadMajors();
    },
    // 加载专业列表
    async loadMajors() {
      try {
        const res = await utils_request.get("/major/school/" + this.selectedSchool.schoolName, {
          pageNum: 1,
          pageSize: 100
        });
        if (res.code === 200) {
          this.majorList = res.data.records || [];
        }
      } catch (error) {
        console.error("加载专业失败:", error);
      }
    },
    // 搜索专业
    searchMajor() {
      if (!this.majorKeyword.trim()) {
        this.loadMajors();
        return;
      }
      this.majorList = this.majorList.filter(
        (major) => major.majorName.includes(this.majorKeyword)
      );
    },
    // 选择专业
    selectMajor(major) {
      this.selectedMajor = major;
      this.step = 3;
    },
    // 保存目标
    async saveTarget() {
      try {
        const res = await utils_request.put("/user/target", {
          userId: this.userId,
          targetSchoolId: this.selectedSchool.id,
          targetSchool: this.selectedSchool.schoolName,
          targetMajorId: this.selectedMajor.id,
          targetMajor: this.selectedMajor.majorName
        });
        if (res.code === 200) {
          common_vendor.index.showToast({
            title: "设置成功",
            icon: "success"
          });
          setTimeout(() => {
            common_vendor.index.navigateBack();
          }, 1500);
        }
      } catch (error) {
        console.error("保存失败:", error);
      }
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $data.statusBarHeight + "px",
    b: common_vendor.o((...args) => $options.goBack && $options.goBack(...args)),
    c: $data.step >= 1 ? 1 : "",
    d: $data.step > 1 ? 1 : "",
    e: $data.step > 1 ? 1 : "",
    f: $data.step >= 2 ? 1 : "",
    g: $data.step > 2 ? 1 : "",
    h: $data.step > 2 ? 1 : "",
    i: $data.step >= 3 ? 1 : "",
    j: $data.step === 1
  }, $data.step === 1 ? common_vendor.e({
    k: common_vendor.o([($event) => $data.schoolKeyword = $event.detail.value, (...args) => $options.searchSchool && $options.searchSchool(...args)]),
    l: $data.schoolKeyword,
    m: common_vendor.f($data.schoolList, (school, k0, i0) => {
      return {
        a: common_vendor.t(school.schoolName),
        b: common_vendor.t(school.location),
        c: school.id,
        d: common_vendor.o(($event) => $options.selectSchool(school), school.id)
      };
    }),
    n: $data.schoolList.length === 0
  }, $data.schoolList.length === 0 ? {} : {}) : {}, {
    o: $data.step === 2
  }, $data.step === 2 ? common_vendor.e({
    p: common_vendor.t($data.selectedSchool.schoolName),
    q: common_vendor.o([($event) => $data.majorKeyword = $event.detail.value, (...args) => $options.searchMajor && $options.searchMajor(...args)]),
    r: $data.majorKeyword,
    s: common_vendor.f($data.majorList, (major, k0, i0) => {
      return {
        a: common_vendor.t(major.majorName),
        b: common_vendor.t(major.majorCode),
        c: major.id,
        d: common_vendor.o(($event) => $options.selectMajor(major), major.id)
      };
    }),
    t: $data.majorList.length === 0
  }, $data.majorList.length === 0 ? {} : {}, {
    v: common_vendor.o(($event) => $data.step = 1)
  }) : {}, {
    w: $data.step === 3
  }, $data.step === 3 ? {
    x: common_vendor.t($data.selectedSchool.schoolName),
    y: common_vendor.t($data.selectedMajor.majorName),
    z: common_vendor.t($data.selectedMajor.majorCode),
    A: common_vendor.o((...args) => $options.saveTarget && $options.saveTarget(...args)),
    B: common_vendor.o(($event) => $data.step = 2)
  } : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-14293891"]]);
wx.createPage(MiniProgramPage);
