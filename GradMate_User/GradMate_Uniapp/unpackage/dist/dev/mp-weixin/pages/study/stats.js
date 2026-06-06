"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
const _sfc_main = {
  data() {
    return {
      userId: 1,
      totalStats: {},
      weekStats: {
        thisWeek: 0,
        lastWeek: 0,
        change: 0,
        lastWeekDays: 0
      },
      trendData: [],
      typeStats: [],
      currentTrendTab: "7天",
      trendTabs: ["7天", "30天"],
      bestTimeSlot: "暂无数据",
      avgDaily: 0,
      completionRate: 0,
      svgWidth: 600,
      svgHeight: 180,
      pieSize: 200,
      pieRadius: 70,
      pieStrokeWidth: 40
    };
  },
  computed: {
    weekRange() {
      const today = /* @__PURE__ */ new Date();
      const weekStart = new Date(today);
      weekStart.setDate(today.getDate() - today.getDay());
      const weekEnd = new Date(weekStart);
      weekEnd.setDate(weekStart.getDate() + 6);
      return `${this.formatShortDate(weekStart)} - ${this.formatShortDate(weekEnd)}`;
    },
    yAxisLabels() {
      if (this.trendData.length === 0)
        return ["0", "1h", "2h", "3h", "4h"];
      const maxDuration = Math.max(...this.trendData.map((d) => d.duration), 60);
      const maxHours = Math.ceil(maxDuration / 60);
      const step = Math.ceil(maxHours / 4);
      return ["0", `${step}h`, `${step * 2}h`, `${step * 3}h`, `${step * 4}h`].reverse();
    },
    dataPoints() {
      if (this.trendData.length === 0)
        return [];
      const maxDuration = Math.max(...this.trendData.map((d) => d.duration), 60);
      const padding = 10;
      const width = this.svgWidth - padding * 2;
      const height = this.svgHeight - padding * 2;
      const stepX = width / (this.trendData.length - 1 || 1);
      return this.trendData.map((item, index) => ({
        x: padding + index * stepX,
        y: padding + height - item.duration / maxDuration * height
      }));
    },
    linePoints() {
      return this.dataPoints.map((p) => `${p.x},${p.y}`).join(" ");
    },
    areaPoints() {
      if (this.dataPoints.length === 0)
        return "";
      const points = this.dataPoints.map((p) => `${p.x},${p.y}`).join(" ");
      const lastPoint = this.dataPoints[this.dataPoints.length - 1];
      const firstPoint = this.dataPoints[0];
      return `${points} ${lastPoint.x},${this.svgHeight - 10} ${firstPoint.x},${this.svgHeight - 10}`;
    },
    totalTypeTime() {
      return this.typeStats.reduce((sum, type) => sum + type.duration, 0);
    },
    pieCircumference() {
      return 2 * Math.PI * this.pieRadius;
    },
    pieSegments() {
      let currentOffset = 0;
      return this.typeStats.map((type) => {
        const length = type.percent / 100 * this.pieCircumference;
        const segment = {
          color: type.color,
          length,
          offset: -currentOffset
        };
        currentOffset += length;
        return segment;
      });
    }
  },
  onShow() {
    this.userId = common_vendor.index.getStorageSync("userId") || 1;
    this.loadAllStats();
  },
  watch: {
    currentTrendTab() {
      this.loadTrendData();
    }
  },
  methods: {
    async loadAllStats() {
      await Promise.all([
        this.loadTotalStats(),
        this.loadWeekComparison(),
        this.loadTrendData(),
        this.loadTypeStats()
      ]);
    },
    async loadTotalStats() {
      try {
        const res = await utils_request.get(`/study/stats?userId=${this.userId}`);
        if (res.code === 200) {
          this.totalStats = res.data;
          this.calculateHabitStats();
        }
      } catch (error) {
        console.error("加载统计失败:", error);
      }
    },
    async loadWeekComparison() {
      try {
        const today = /* @__PURE__ */ new Date();
        const thisWeekStart = new Date(today);
        thisWeekStart.setDate(today.getDate() - today.getDay());
        const lastWeekStart = new Date(thisWeekStart);
        lastWeekStart.setDate(thisWeekStart.getDate() - 7);
        const lastWeekEnd = new Date(thisWeekStart);
        lastWeekEnd.setDate(thisWeekStart.getDate() - 1);
        const [thisWeekRes, lastWeekRes] = await Promise.all([
          utils_request.get(`/study/record/range?userId=${this.userId}&startDate=${this.formatDate(thisWeekStart)}&endDate=${this.formatDate(today)}`),
          utils_request.get(`/study/record/range?userId=${this.userId}&startDate=${this.formatDate(lastWeekStart)}&endDate=${this.formatDate(lastWeekEnd)}`)
        ]);
        if (thisWeekRes.code === 200 && lastWeekRes.code === 200) {
          const thisWeekTotal = thisWeekRes.data.reduce((sum, r) => sum + (r.duration || 0), 0);
          const lastWeekTotal = lastWeekRes.data.reduce((sum, r) => sum + (r.duration || 0), 0);
          this.weekStats = {
            thisWeek: thisWeekTotal,
            lastWeek: lastWeekTotal,
            change: lastWeekTotal > 0 ? Math.round((thisWeekTotal - lastWeekTotal) / lastWeekTotal * 100) : 0,
            lastWeekDays: lastWeekRes.data.length
          };
        }
      } catch (error) {
        console.error("加载周对比失败:", error);
      }
    },
    async loadTrendData() {
      try {
        const days = this.currentTrendTab === "7天" ? 7 : 30;
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
          this.trendData = [];
          for (let i = 0; i < days; i++) {
            const date = new Date(startDate);
            date.setDate(startDate.getDate() + i);
            const dateStr = this.formatDate(date);
            this.trendData.push({
              label: this.currentTrendTab === "7天" ? this.getWeekday(date) : `${date.getMonth() + 1}/${date.getDate()}`,
              duration: dataMap[dateStr] || 0
            });
          }
        }
      } catch (error) {
        console.error("加载趋势数据失败:", error);
      }
    },
    async loadTypeStats() {
      try {
        const today = /* @__PURE__ */ new Date();
        const monthStart = new Date(today.getFullYear(), today.getMonth(), 1);
        const res = await utils_request.get(`/study/record/range?userId=${this.userId}&startDate=${this.formatDate(monthStart)}&endDate=${this.formatDate(today)}`);
        if (res.code === 200) {
          const typeMap = {};
          let total = 0;
          res.data.forEach((record) => {
            const type = record.studyType || "other";
            typeMap[type] = (typeMap[type] || 0) + (record.duration || 0);
            total += record.duration || 0;
          });
          const typeConfig = {
            major: { name: "专业课", color: "#4A90E2" },
            english: { name: "英语", color: "#50C878" },
            math: { name: "数学", color: "#FF6B6B" },
            coding: { name: "编程", color: "#9B59B6" },
            practice: { name: "刷题", color: "#F39C12" },
            reading: { name: "阅读", color: "#3498DB" },
            other: { name: "其他", color: "#95A5A6" }
          };
          this.typeStats = Object.entries(typeMap).map(([type, duration]) => ({
            type,
            ...typeConfig[type],
            duration,
            percent: total > 0 ? Math.round(duration / total * 100) : 0
          })).sort((a, b) => b.duration - a.duration);
        }
      } catch (error) {
        console.error("加载类型统计失败:", error);
      }
    },
    calculateHabitStats() {
      const totalDays = this.totalStats.totalDays || 1;
      this.avgDaily = Math.round((this.totalStats.totalDuration || 0) / totalDays);
      const targetDaily = 120;
      this.completionRate = Math.min(100, Math.round(this.avgDaily / targetDaily * 100));
      this.bestTimeSlot = "9:00-12:00";
    },
    formatHours(minutes) {
      if (!minutes)
        return "0.0h";
      return (minutes / 60).toFixed(1) + "h";
    },
    formatDate(date) {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      return `${year}-${month}-${day}`;
    },
    formatShortDate(date) {
      return `${date.getMonth() + 1}/${date.getDate()}`;
    },
    getWeekday(date) {
      const days = ["周日", "周一", "周二", "周三", "周四", "周五", "周六"];
      return days[date.getDay()];
    },
    goBack() {
      common_vendor.index.navigateBack();
    },
    goToCalendar() {
      common_vendor.index.navigateTo({
        url: "/pages/study/calendar"
      });
    }
  }
};
if (!Array) {
  const _component_polyline = common_vendor.resolveComponent("polyline");
  const _component_polygon = common_vendor.resolveComponent("polygon");
  const _component_circle = common_vendor.resolveComponent("circle");
  const _component_stop = common_vendor.resolveComponent("stop");
  const _component_linearGradient = common_vendor.resolveComponent("linearGradient");
  const _component_defs = common_vendor.resolveComponent("defs");
  const _component_svg = common_vendor.resolveComponent("svg");
  (_component_polyline + _component_polygon + _component_circle + _component_stop + _component_linearGradient + _component_defs + _component_svg)();
}
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.o((...args) => $options.goBack && $options.goBack(...args)),
    b: common_vendor.t($options.formatHours($data.totalStats.totalDuration)),
    c: common_vendor.t($data.totalStats.continuousDays || 0),
    d: common_vendor.t($data.totalStats.totalDays || 0),
    e: common_vendor.t($options.weekRange),
    f: common_vendor.t($options.formatHours($data.weekStats.thisWeek)),
    g: common_vendor.t($data.weekStats.change >= 0 ? "+" : ""),
    h: common_vendor.t($data.weekStats.change),
    i: $data.weekStats.change >= 0 ? 1 : "",
    j: $data.weekStats.change < 0 ? 1 : "",
    k: common_vendor.t($options.formatHours($data.weekStats.lastWeek)),
    l: common_vendor.t($data.weekStats.lastWeekDays),
    m: common_vendor.f($data.trendTabs, (tab, k0, i0) => {
      return {
        a: common_vendor.t(tab),
        b: tab,
        c: $data.currentTrendTab === tab ? 1 : "",
        d: common_vendor.o(($event) => $data.currentTrendTab = tab, tab)
      };
    }),
    n: $data.trendData.length > 0
  }, $data.trendData.length > 0 ? {
    o: common_vendor.f($options.yAxisLabels, (val, idx, i0) => {
      return {
        a: common_vendor.t(val),
        b: idx
      };
    }),
    p: common_vendor.f(5, (i, k0, i0) => {
      return {
        a: i
      };
    }),
    q: common_vendor.p({
      points: $options.linePoints,
      fill: "none",
      stroke: "#4A90E2",
      ["stroke-width"]: "2",
      ["stroke-linecap"]: "round",
      ["stroke-linejoin"]: "round"
    }),
    r: common_vendor.p({
      points: $options.areaPoints,
      fill: "url(#areaGradient)"
    }),
    s: common_vendor.f($options.dataPoints, (point, index, i0) => {
      return {
        a: index,
        b: "4ab29f21-3-" + i0 + ",4ab29f21-0",
        c: common_vendor.p({
          cx: point.x,
          cy: point.y,
          r: "3",
          fill: "#4A90E2",
          stroke: "#fff",
          ["stroke-width"]: "2"
        })
      };
    }),
    t: common_vendor.p({
      offset: "0%"
    }),
    v: common_vendor.p({
      offset: "100%"
    }),
    w: common_vendor.p({
      id: "areaGradient",
      x1: "0%",
      y1: "0%",
      x2: "0%",
      y2: "100%"
    }),
    x: common_vendor.p({
      viewBox: `0 0 ${$data.svgWidth} ${$data.svgHeight}`
    }),
    y: common_vendor.f($data.trendData, (item, index, i0) => {
      return {
        a: common_vendor.t(item.label),
        b: index
      };
    })
  } : {
    z: common_vendor.o((...args) => $options.goToCalendar && $options.goToCalendar(...args))
  }, {
    A: $data.typeStats.length > 0
  }, $data.typeStats.length > 0 ? {
    B: common_vendor.f($options.pieSegments, (segment, index, i0) => {
      return {
        a: index,
        b: "4ab29f21-9-" + i0 + ",4ab29f21-8",
        c: common_vendor.p({
          cx: $data.pieSize / 2,
          cy: $data.pieSize / 2,
          r: $data.pieRadius,
          fill: "none",
          stroke: segment.color,
          ["stroke-width"]: $data.pieStrokeWidth,
          ["stroke-dasharray"]: `${segment.length} ${$options.pieCircumference}`,
          ["stroke-dashoffset"]: segment.offset,
          transform: `rotate(-90 ${$data.pieSize / 2} ${$data.pieSize / 2})`
        })
      };
    }),
    C: $data.pieSize / 2,
    D: $data.pieSize / 2 - 8,
    E: common_vendor.t($options.formatHours($options.totalTypeTime)),
    F: $data.pieSize / 2,
    G: $data.pieSize / 2 + 16,
    H: common_vendor.p({
      viewBox: `0 0 ${$data.pieSize} ${$data.pieSize}`
    }),
    I: common_vendor.f($data.typeStats, (type, k0, i0) => {
      return {
        a: type.color,
        b: common_vendor.t(type.name),
        c: common_vendor.t(type.percent),
        d: common_vendor.t($options.formatHours(type.duration)),
        e: type.type
      };
    })
  } : {}, {
    J: common_vendor.t($data.bestTimeSlot),
    K: common_vendor.t($options.formatHours($data.avgDaily)),
    L: common_vendor.t($data.completionRate)
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-4ab29f21"]]);
wx.createPage(MiniProgramPage);
