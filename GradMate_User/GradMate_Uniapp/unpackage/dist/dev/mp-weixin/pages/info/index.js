"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
const _sfc_main = {
  __name: "index",
  setup(__props) {
    const currentTab = common_vendor.ref(0);
    const tabs = ["招生简章", "调剂信息", "院校信息"];
    const searchKeyword = common_vendor.ref("");
    const brochureList = common_vendor.ref([]);
    const brochureLoading = common_vendor.ref(false);
    const brochurePageNum = common_vendor.ref(1);
    const brochureNoMore = common_vendor.ref(false);
    const adjustList = common_vendor.ref([]);
    const adjustLoading = common_vendor.ref(false);
    const adjustPageNum = common_vendor.ref(1);
    const adjustNoMore = common_vendor.ref(false);
    const informationList = common_vendor.ref([]);
    const informationLoading = common_vendor.ref(false);
    const informationPageNum = common_vendor.ref(1);
    const informationNoMore = common_vendor.ref(false);
    common_vendor.onMounted(() => {
      loadBrochureList();
    });
    const switchTab = (index) => {
      currentTab.value = index;
      if (index === 0 && brochureList.value.length === 0) {
        loadBrochureList();
      } else if (index === 1 && adjustList.value.length === 0) {
        loadAdjustList();
      } else if (index === 2 && informationList.value.length === 0) {
        loadInformationList();
      }
    };
    const loadBrochureList = async () => {
      if (brochureLoading.value || brochureNoMore.value)
        return;
      brochureLoading.value = true;
      try {
        const res = await common_vendor.index.request({
          url: utils_request.buildApiUrl("/info/brochure/list"),
          method: "GET",
          data: {
            pageNum: brochurePageNum.value,
            pageSize: 20,
            schoolName: searchKeyword.value || void 0
          }
        });
        if (res.data.code === 200 && res.data.data) {
          const records = res.data.data.records || [];
          brochureList.value = [...brochureList.value, ...records];
          if (records.length < 20) {
            brochureNoMore.value = true;
          } else {
            brochurePageNum.value++;
          }
        }
      } catch (error) {
        console.error("加载招生简章失败:", error);
        common_vendor.index.showToast({
          title: "加载失败",
          icon: "none"
        });
      } finally {
        brochureLoading.value = false;
      }
    };
    const loadAdjustList = async () => {
      if (adjustLoading.value || adjustNoMore.value)
        return;
      adjustLoading.value = true;
      try {
        const res = await common_vendor.index.request({
          url: utils_request.buildApiUrl("/info/adjust/list"),
          method: "GET",
          data: {
            pageNum: adjustPageNum.value,
            pageSize: 20,
            schoolName: searchKeyword.value || void 0
          }
        });
        if (res.data.code === 200 && res.data.data) {
          const records = res.data.data.records || [];
          adjustList.value = [...adjustList.value, ...records];
          if (records.length < 20) {
            adjustNoMore.value = true;
          } else {
            adjustPageNum.value++;
          }
        }
      } catch (error) {
        console.error("加载调剂信息失败:", error);
        common_vendor.index.showToast({
          title: "加载失败",
          icon: "none"
        });
      } finally {
        adjustLoading.value = false;
      }
    };
    const loadInformationList = async () => {
      if (informationLoading.value || informationNoMore.value)
        return;
      informationLoading.value = true;
      try {
        const res = await common_vendor.index.request({
          url: utils_request.buildApiUrl("/info/information/list"),
          method: "GET",
          data: {
            pageNum: informationPageNum.value,
            pageSize: 20,
            schoolName: searchKeyword.value || void 0
          }
        });
        if (res.data.code === 200 && res.data.data) {
          const records = res.data.data.records || [];
          informationList.value = [...informationList.value, ...records];
          if (records.length < 20) {
            informationNoMore.value = true;
          } else {
            informationPageNum.value++;
          }
        }
      } catch (error) {
        console.error("加载院校信息失败:", error);
        common_vendor.index.showToast({
          title: "加载失败",
          icon: "none"
        });
      } finally {
        informationLoading.value = false;
      }
    };
    const loadMore = () => {
      if (currentTab.value === 0) {
        loadBrochureList();
      } else if (currentTab.value === 1) {
        loadAdjustList();
      } else if (currentTab.value === 2) {
        loadInformationList();
      }
    };
    const formatDate = (dateStr) => {
      if (!dateStr)
        return "";
      const date = new Date(dateStr);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      return `${year}-${month}-${day}`;
    };
    const viewDetail = (item) => {
      if (item.url) {
        common_vendor.index.setClipboardData({
          data: item.url,
          success: () => {
            common_vendor.index.showToast({
              title: "链接已复制",
              icon: "success"
            });
          }
        });
      } else {
        common_vendor.index.showToast({
          title: "暂无链接",
          icon: "none"
        });
      }
    };
    const goBack = () => {
      common_vendor.index.navigateBack();
    };
    const handleSearch = () => {
      brochureList.value = [];
      brochurePageNum.value = 1;
      brochureNoMore.value = false;
      adjustList.value = [];
      adjustPageNum.value = 1;
      adjustNoMore.value = false;
      informationList.value = [];
      informationPageNum.value = 1;
      informationNoMore.value = false;
      if (currentTab.value === 0) {
        loadBrochureList();
      } else if (currentTab.value === 1) {
        loadAdjustList();
      } else if (currentTab.value === 2) {
        loadInformationList();
      }
    };
    const clearSearch = () => {
      searchKeyword.value = "";
      handleSearch();
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.o(goBack),
        b: common_vendor.o(handleSearch),
        c: searchKeyword.value,
        d: common_vendor.o(($event) => searchKeyword.value = $event.detail.value),
        e: searchKeyword.value
      }, searchKeyword.value ? {
        f: common_vendor.o(clearSearch)
      } : {}, {
        g: common_vendor.o(handleSearch),
        h: common_vendor.f(tabs, (tab, index, i0) => {
          return common_vendor.e({
            a: common_vendor.t(tab),
            b: currentTab.value === index
          }, currentTab.value === index ? {} : {}, {
            c: index,
            d: currentTab.value === index ? 1 : "",
            e: common_vendor.o(($event) => switchTab(index), index)
          });
        }),
        i: currentTab.value === 0
      }, currentTab.value === 0 ? common_vendor.e({
        j: common_vendor.f(brochureList.value, (item, index, i0) => {
          return {
            a: common_vendor.t(item.schoolName),
            b: common_vendor.t(formatDate(item.publishDate)),
            c: common_vendor.t(item.title),
            d: index,
            e: common_vendor.o(($event) => viewDetail(item), index)
          };
        }),
        k: brochureLoading.value
      }, brochureLoading.value ? {} : {}, {
        l: !brochureLoading.value && brochureList.value.length === 0
      }, !brochureLoading.value && brochureList.value.length === 0 ? {} : {}, {
        m: !brochureLoading.value && brochureNoMore.value
      }, !brochureLoading.value && brochureNoMore.value ? {} : {}) : {}, {
        n: currentTab.value === 1
      }, currentTab.value === 1 ? common_vendor.e({
        o: common_vendor.f(adjustList.value, (item, index, i0) => {
          return {
            a: common_vendor.t(item.schoolName),
            b: common_vendor.t(formatDate(item.publishDate)),
            c: common_vendor.t(item.title),
            d: index,
            e: common_vendor.o(($event) => viewDetail(item), index)
          };
        }),
        p: adjustLoading.value
      }, adjustLoading.value ? {} : {}, {
        q: !adjustLoading.value && adjustList.value.length === 0
      }, !adjustLoading.value && adjustList.value.length === 0 ? {} : {}, {
        r: !adjustLoading.value && adjustNoMore.value
      }, !adjustLoading.value && adjustNoMore.value ? {} : {}) : {}, {
        s: currentTab.value === 2
      }, currentTab.value === 2 ? common_vendor.e({
        t: common_vendor.f(informationList.value, (item, index, i0) => {
          return {
            a: common_vendor.t(item.schoolName),
            b: common_vendor.t(formatDate(item.publishDate)),
            c: common_vendor.t(item.title),
            d: index,
            e: common_vendor.o(($event) => viewDetail(item), index)
          };
        }),
        v: informationLoading.value
      }, informationLoading.value ? {} : {}, {
        w: !informationLoading.value && informationList.value.length === 0
      }, !informationLoading.value && informationList.value.length === 0 ? {} : {}, {
        x: !informationLoading.value && informationNoMore.value
      }, !informationLoading.value && informationNoMore.value ? {} : {}) : {}, {
        y: common_vendor.o(loadMore)
      });
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-f7870db2"]]);
wx.createPage(MiniProgramPage);
