"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
const _sfc_main = {
  __name: "index",
  setup(__props) {
    const statusBarHeight = common_vendor.ref(0);
    const currentEncouragement = common_vendor.ref("");
    const currentIndex = common_vendor.ref(0);
    const isAnimating = common_vendor.ref(false);
    const encouragementList = [
      "千研万语陪你，遇见不一样的自己！",
      "每一次努力，都是成功的铺垫",
      "坚持下去，你会感谢现在拼搏的自己",
      "梦想不会逃跑，逃跑的永远是自己",
      "今天的汗水，是明天的荣耀",
      "相信自己，你比想象中更强大",
      "不负韶华，不负梦想，加油！"
    ];
    const newsList = common_vendor.ref([]);
    const loading = common_vendor.ref(false);
    common_vendor.onMounted(() => {
      const systemInfo = common_vendor.index.getSystemInfoSync();
      statusBarHeight.value = systemInfo.statusBarHeight || 0;
      currentEncouragement.value = encouragementList[0];
      loadNewsList();
    });
    const loadNewsList = async () => {
      loading.value = true;
      try {
        const res = await common_vendor.index.request({
          url: utils_request.buildApiUrl("/info/mixed/list"),
          method: "GET",
          data: {
            pageNum: 1,
            pageSize: 10
          }
        });
        if (res.data.code === 200 && res.data.data) {
          const records = res.data.data.records || [];
          newsList.value = records.map((item) => ({
            id: item.id,
            type: item.type,
            tag: item.tag || "院校资讯",
            title: item.title,
            source: item.schoolName || "未知院校",
            time: formatDate(item.publishDate),
            url: item.url,
            comment: Math.floor(Math.random() * 100)
            // 模拟评论数
          }));
        }
      } catch (error) {
        console.error("加载资讯失败:", error);
        common_vendor.index.showToast({
          title: "加载资讯失败",
          icon: "none"
        });
      } finally {
        loading.value = false;
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
    const switchEncouragement = () => {
      isAnimating.value = true;
      setTimeout(() => {
        currentIndex.value = (currentIndex.value + 1) % encouragementList.length;
        currentEncouragement.value = encouragementList[currentIndex.value];
        isAnimating.value = false;
      }, 300);
    };
    const navigateTo = (url) => {
      const tabBarPages = [
        "/pages/school/index",
        "/pages/study/tasklist",
        "/pages/ai/chat",
        "/pages/profile/profile"
      ];
      if (tabBarPages.includes(url)) {
        common_vendor.index.switchTab({ url });
      } else {
        common_vendor.index.navigateTo({ url });
      }
    };
    const goToSearch = () => {
      common_vendor.index.navigateTo({ url: "/pages/school/search" });
    };
    const showToast = (title) => {
      common_vendor.index.showToast({
        title: `${title}功能开发中`,
        icon: "none"
      });
    };
    const viewNewsDetail = (item) => {
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
        showToast("暂无链接");
      }
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: statusBarHeight.value + "px",
        b: common_vendor.o(goToSearch),
        c: common_vendor.o(($event) => navigateTo("/pages/school/list")),
        d: common_vendor.o(($event) => navigateTo("/pages/major/search")),
        e: common_vendor.t(currentEncouragement.value),
        f: isAnimating.value ? 1 : "",
        g: common_vendor.o(switchEncouragement),
        h: common_vendor.o(($event) => navigateTo("/pages/school/assistant")),
        i: common_vendor.o(($event) => navigateTo("/pages/school/select")),
        j: common_vendor.o(($event) => navigateTo("/pages/major/select")),
        k: common_vendor.o(($event) => navigateTo("/pages/info/index")),
        l: common_vendor.o(($event) => navigateTo("/pages/school/calendar")),
        m: common_vendor.o(($event) => navigateTo("/pages/ai/chat")),
        n: common_vendor.o(($event) => navigateTo("/pages/school/calendar")),
        o: common_vendor.o(($event) => navigateTo("/pages/info/index")),
        p: common_vendor.f(newsList.value, (item, index, i0) => {
          return {
            a: common_vendor.t(item.tag),
            b: common_vendor.t(item.title),
            c: common_vendor.t(item.source),
            d: common_vendor.t(item.time),
            e: common_vendor.t(item.comment),
            f: index,
            g: common_vendor.o(($event) => viewNewsDetail(item), index)
          };
        }),
        q: loading.value
      }, loading.value ? {} : {}, {
        r: !loading.value && newsList.value.length === 0
      }, !loading.value && newsList.value.length === 0 ? {} : {}, {
        s: common_vendor.o(($event) => navigateTo("/pages/info/index"))
      });
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-7d897bab"]]);
wx.createPage(MiniProgramPage);
