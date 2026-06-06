"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
const _sfc_main = {
  __name: "favorite",
  setup(__props) {
    const statusBarHeight = common_vendor.ref(0);
    const userId = common_vendor.ref(1);
    const currentTab = common_vendor.ref("school");
    const tabs = common_vendor.ref([
      { label: "院校", value: "school", count: 0 },
      { label: "专业", value: "major", count: 0 }
    ]);
    const list = common_vendor.ref([]);
    const pageNum = common_vendor.ref(1);
    const pageSize = common_vendor.ref(10);
    const hasMore = common_vendor.ref(true);
    common_vendor.onMounted(() => {
      const systemInfo = common_vendor.index.getSystemInfoSync();
      statusBarHeight.value = systemInfo.statusBarHeight || 0;
      const userInfo = common_vendor.index.getStorageSync("userInfo");
      if (userInfo && userInfo.id) {
        userId.value = userInfo.id;
      }
      loadCounts();
      loadList();
    });
    const loadCounts = async () => {
      try {
        const res = await utils_request.get(`/favorite/count?userId=${userId.value}`);
        if (res.code === 200) {
          tabs.value[0].count = res.data.school || 0;
          tabs.value[1].count = res.data.major || 0;
        }
      } catch (error) {
        console.error("加载数量失败:", error);
      }
    };
    const loadList = async () => {
      try {
        common_vendor.index.showLoading({ title: "加载中..." });
        const type = currentTab.value;
        const res = await utils_request.get(`/favorite/list?userId=${userId.value}&type=${type}&pageNum=${pageNum.value}&pageSize=${pageSize.value}`);
        if (res.code === 200) {
          const newList = res.data.records || [];
          list.value = pageNum.value === 1 ? newList : [...list.value, ...newList];
          hasMore.value = newList.length === pageSize.value;
        }
      } catch (error) {
        console.error("加载列表失败:", error);
      } finally {
        common_vendor.index.hideLoading();
      }
    };
    const switchTab = (tab) => {
      currentTab.value = tab;
      pageNum.value = 1;
      list.value = [];
      loadList();
    };
    const loadMore = () => {
      if (hasMore.value) {
        pageNum.value++;
        loadList();
      }
    };
    const removeFavorite = async (item) => {
      try {
        const res = await utils_request.get(`/favorite/remove?userId=${userId.value}&type=${item.type}&targetId=${item.targetId}`);
        if (res.code === 200) {
          common_vendor.index.showToast({ title: "取消成功", icon: "success" });
          pageNum.value = 1;
          list.value = [];
          loadCounts();
          loadList();
        }
      } catch (error) {
        console.error("取消收藏失败:", error);
      }
    };
    const goToDetail = (item) => {
      if (item.type === "school") {
        common_vendor.index.navigateTo({
          url: `/pages/school/detail?id=${item.targetId}`
        });
      } else if (item.type === "major") {
        let schoolId = "";
        let schoolName = "";
        if (item.metadata) {
          try {
            const metadata = JSON.parse(item.metadata);
            schoolId = metadata.schoolId || "";
            schoolName = metadata.schoolName || "";
          } catch (e) {
            console.error("解析 metadata 失败:", e);
          }
        }
        if (!schoolName && item.targetName && item.targetName.includes(" - ")) {
          const nameParts = item.targetName.split(" - ");
          schoolName = nameParts[0];
        }
        if (schoolId) {
          common_vendor.index.navigateTo({
            url: `/pages/school/detail?id=${schoolId}`
          });
        } else if (schoolName) {
          querySchoolByName(schoolName);
        } else {
          common_vendor.index.showToast({
            title: "无法打开详情",
            icon: "none"
          });
        }
      } else {
        common_vendor.index.navigateTo({ url: "/pages/info/index" });
      }
    };
    const querySchoolByName = async (schoolName) => {
      try {
        common_vendor.index.showLoading({ title: "加载中..." });
        const res = await utils_request.get(`/school/list?schoolName=${encodeURIComponent(schoolName)}&pageNum=1&pageSize=1`);
        common_vendor.index.hideLoading();
        if (res.code === 200 && res.data.records && res.data.records.length > 0) {
          const school = res.data.records[0];
          common_vendor.index.navigateTo({
            url: `/pages/school/detail?id=${school.id}`
          });
        } else {
          common_vendor.index.showToast({
            title: "未找到该院校",
            icon: "none"
          });
        }
      } catch (error) {
        common_vendor.index.hideLoading();
        console.error("查询院校失败:", error);
        common_vendor.index.showToast({
          title: "查询失败",
          icon: "none"
        });
      }
    };
    const getIconClass = (type) => {
      const classMap = {
        "school": "school-icon",
        "major": "major-icon",
        "brochure": "info-icon",
        "adjust": "info-icon",
        "information": "info-icon"
      };
      return classMap[type] || "default-icon";
    };
    const goBack = () => {
      common_vendor.index.navigateBack();
    };
    const formatTime = (time) => {
      if (!time)
        return "";
      const date = new Date(time);
      return `${date.getMonth() + 1}-${date.getDate()}`;
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: statusBarHeight.value + "px",
        b: common_vendor.o(goBack),
        c: common_vendor.f(tabs.value, (tab, k0, i0) => {
          return {
            a: common_vendor.t(tab.label),
            b: common_vendor.t(tab.count),
            c: tab.value,
            d: currentTab.value === tab.value ? 1 : "",
            e: common_vendor.o(($event) => switchTab(tab.value), tab.value)
          };
        }),
        d: list.value.length > 0
      }, list.value.length > 0 ? {
        e: common_vendor.f(list.value, (item, k0, i0) => {
          return {
            a: common_vendor.n(getIconClass(item.type)),
            b: common_vendor.t(item.targetName),
            c: common_vendor.t(formatTime(item.createTime)),
            d: common_vendor.o(($event) => removeFavorite(item), item.id),
            e: item.id,
            f: common_vendor.o(($event) => goToDetail(item), item.id)
          };
        })
      } : {}, {
        f: common_vendor.o(loadMore)
      });
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-5d334561"]]);
wx.createPage(MiniProgramPage);
