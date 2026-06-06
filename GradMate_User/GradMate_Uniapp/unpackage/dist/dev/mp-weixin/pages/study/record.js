"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
const _sfc_main = {
  data() {
    return {
      userId: 1,
      currentTab: "record",
      currentPeriod: "日",
      periodTabs: ["日", "周", "总计"],
      userName: "用户07UXIo",
      userAvatar: "/static/logo.png",
      todayStudy: "1",
      totalHours: "2.46",
      totalDays: "23",
      stats: {
        weeklyDays: 0,
        monthlyDays: 0,
        averageDays: 0,
        mustDays: 0,
        longTermDays: 0,
        eachDays: 0
      },
      chartData: [],
      xLabels: [],
      typeList: [
        { name: "听课", duration: "0min", color: "#4A90E2" },
        { name: "刷题", duration: "0min", color: "#FF6B6B" },
        { name: "闪过", duration: "0min", color: "#FF4444" },
        { name: "阅读", duration: "0min", color: "#FFD700" },
        { name: "长难句学习", duration: "0min", color: "#9B59B6" },
        { name: "专业", duration: "0min", color: "#3498DB" },
        { name: "其他", duration: "1min", color: "#1ABC9C" }
      ]
    };
  },
  computed: {
    chartPoints() {
      return this.chartData.map((p) => `${p.x},${100 - p.y}`).join(" ");
    }
  },
  onShow() {
    this.userId = common_vendor.index.getStorageSync("userId") || 1;
    this.loadAllData();
  },
  methods: {
    async loadAllData() {
      await Promise.all([
        this.loadStats(),
        this.loadChartData(),
        this.loadTypeStats()
      ]);
    },
    async loadStats() {
      try {
        const res = await utils_request.get(`/study/stats?userId=${this.userId}`);
        if (res.code === 200) {
          const data = res.data;
          this.todayStudy = Math.floor((data.todayDuration || 0) / 60) || "1";
          this.totalHours = ((data.totalDuration || 0) / 60).toFixed(2);
          this.totalDays = data.totalDays || 23;
          this.stats = {
            weeklyDays: data.weeklyDays || 0,
            monthlyDays: data.monthlyDays || 0,
            averageDays: data.averageDays || 0,
            mustDays: 0,
            longTermDays: 0,
            eachDays: 0
          };
        }
      } catch (error) {
        console.error("加载统计失败:", error);
      }
    },
    async loadChartData() {
      try {
        const days = this.currentPeriod === "日" ? 7 : 30;
        const today = /* @__PURE__ */ new Date();
        const startDate = new Date(today);
        startDate.setDate(today.getDate() - days + 1);
        const res = await utils_request.get(`/study/record/range?userId=${this.userId}&startDate=${this.formatDate(startDate)}&endDate=${this.formatDate(today)}`);
        if (res.code === 200) {
          const dataMap = {};
          res.data.forEach((record) => {
            const date = record.checkDate.split(" ")[0];
            dataMap[date] = (dataMap[date] || 0) + (record.duration || 0);
          });
          const maxDuration = Math.max(...Object.values(dataMap), 1);
          this.chartData = [];
          this.xLabels = [];
          for (let i = 0; i < days; i++) {
            const date = new Date(startDate);
            date.setDate(startDate.getDate() + i);
            const dateStr = this.formatDate(date);
            const duration = dataMap[dateStr] || 0;
            const isToday = this.isSameDate(date, today);
            this.chartData.push({
              x: i / (days - 1) * 100,
              y: duration / maxDuration * 80 + 10,
              isToday
            });
            if (days === 7) {
              this.xLabels.push(`${date.getMonth() + 1}/${date.getDate()}`);
            } else if (i % 5 === 0) {
              this.xLabels.push(`${date.getMonth() + 1}/${date.getDate()}`);
            }
          }
        }
      } catch (error) {
        console.error("加载图表数据失败:", error);
      }
    },
    async loadTypeStats() {
      try {
        const today = /* @__PURE__ */ new Date();
        const monthStart = new Date(today.getFullYear(), today.getMonth(), 1);
        const res = await utils_request.get(`/study/record/range?userId=${this.userId}&startDate=${this.formatDate(monthStart)}&endDate=${this.formatDate(today)}`);
        if (res.code === 200) {
          const typeMap = {};
          res.data.forEach((record) => {
            const type = record.studyType || "other";
            typeMap[type] = (typeMap[type] || 0) + (record.duration || 0);
          });
          const typeNameMap = {
            major: "专业",
            english: "听课",
            math: "刷题",
            coding: "阅读",
            practice: "闪过",
            reading: "长难句学习",
            other: "其他"
          };
          this.typeList = this.typeList.map((item) => {
            const typeKey = Object.keys(typeNameMap).find((k) => typeNameMap[k] === item.name) || "other";
            const duration = typeMap[typeKey] || 0;
            return {
              ...item,
              duration: duration > 0 ? `${duration}min` : "0min"
            };
          });
        }
      } catch (error) {
        console.error("加载类型统计失败:", error);
      }
    },
    switchTab(tab) {
      if (tab === "plan") {
        common_vendor.index.navigateTo({
          url: "/pages/study/index"
        });
      }
    },
    shareRecord() {
      common_vendor.index.showToast({
        title: "分享功能开发中",
        icon: "none"
      });
    },
    formatDate(date) {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      return `${year}-${month}-${day}`;
    },
    isSameDate(date1, date2) {
      return this.formatDate(date1) === this.formatDate(date2);
    }
  }
};
if (!Array) {
  const _component_polyline = common_vendor.resolveComponent("polyline");
  const _component_svg = common_vendor.resolveComponent("svg");
  (_component_polyline + _component_svg)();
}
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: $data.currentTab === "plan" ? 1 : "",
    b: common_vendor.o(($event) => $options.switchTab("plan")),
    c: $data.currentTab === "record" ? 1 : "",
    d: common_vendor.o(($event) => $options.switchTab("record")),
    e: $data.userAvatar,
    f: common_vendor.t($data.userName),
    g: common_vendor.t($data.todayStudy),
    h: common_vendor.t($data.totalHours),
    i: common_vendor.t($data.totalDays),
    j: common_vendor.f($data.periodTabs, (tab, k0, i0) => {
      return {
        a: common_vendor.t(tab),
        b: tab,
        c: $data.currentPeriod === tab ? 1 : "",
        d: common_vendor.o(($event) => $data.currentPeriod = tab, tab)
      };
    }),
    k: common_vendor.f(6, (i, k0, i0) => {
      return {
        a: i
      };
    }),
    l: common_vendor.f($data.chartData, (point, index, i0) => {
      return common_vendor.e({
        a: point.isToday
      }, point.isToday ? {} : {}, {
        b: index,
        c: point.x + "%",
        d: point.y + "%",
        e: point.isToday ? "#FF9500" : "#FFD700"
      });
    }),
    m: common_vendor.p({
      points: $options.chartPoints,
      fill: "none",
      stroke: "#FFD700",
      ["stroke-width"]: "2"
    }),
    n: common_vendor.p({
      viewBox: "0 0 100 100",
      preserveAspectRatio: "none"
    }),
    o: common_vendor.f($data.xLabels, (label, k0, i0) => {
      return {
        a: common_vendor.t(label),
        b: label
      };
    }),
    p: common_vendor.t($data.stats.weeklyDays),
    q: common_vendor.t($data.stats.monthlyDays),
    r: common_vendor.t($data.stats.averageDays),
    s: common_vendor.t($data.stats.mustDays),
    t: common_vendor.t($data.stats.longTermDays),
    v: common_vendor.t($data.stats.eachDays),
    w: common_vendor.f($data.typeList, (type, k0, i0) => {
      return {
        a: type.color,
        b: common_vendor.t(type.name),
        c: common_vendor.t(type.duration),
        d: type.name
      };
    }),
    x: common_vendor.o((...args) => $options.shareRecord && $options.shareRecord(...args))
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-2aa327e7"]]);
wx.createPage(MiniProgramPage);
