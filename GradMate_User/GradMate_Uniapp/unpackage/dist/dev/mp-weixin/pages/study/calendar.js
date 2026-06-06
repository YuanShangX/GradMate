"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
const _sfc_main = {
  data() {
    return {
      userId: 1,
      stats: {},
      currentYear: (/* @__PURE__ */ new Date()).getFullYear(),
      currentMonth: (/* @__PURE__ */ new Date()).getMonth() + 1,
      monthlyRecords: [],
      showDetail: false,
      showCheckIn: false,
      showCustomDuration: false,
      selectedDate: "",
      selectedRecord: null,
      checkInForm: {
        duration: null,
        studyType: "",
        mood: 5,
        note: ""
      },
      weekdays: ["日", "一", "二", "三", "四", "五", "六"],
      quickDurations: [15, 25, 30, 45, 60],
      studyTypes: [
        { value: "major", label: "专业课" },
        { value: "english", label: "英语" },
        { value: "math", label: "数学" },
        { value: "coding", label: "编程" },
        { value: "practice", label: "刷题" },
        { value: "reading", label: "阅读" },
        { value: "other", label: "其他" }
      ]
    };
  },
  computed: {
    calendarDates() {
      const year = this.currentYear;
      const month = this.currentMonth;
      const firstDay = new Date(year, month - 1, 1);
      const lastDay = new Date(year, month, 0);
      const startDate = new Date(firstDay);
      startDate.setDate(startDate.getDate() - firstDay.getDay());
      const endDate = new Date(lastDay);
      endDate.setDate(endDate.getDate() + (6 - lastDay.getDay()));
      const dates = [];
      const current = new Date(startDate);
      const today = /* @__PURE__ */ new Date();
      while (current <= endDate) {
        const dateStr = this.formatDate(current);
        const record = this.monthlyRecords.find(
          (r) => this.formatDate(new Date(r.checkDate)) === dateStr
        );
        dates.push({
          key: dateStr,
          day: current.getDate(),
          date: new Date(current),
          dateStr,
          isCurrentMonth: current.getMonth() === month - 1,
          isToday: this.isSameDate(current, today),
          hasRecord: !!record,
          duration: record ? (record.duration / 60).toFixed(1) : null,
          mood: record ? record.mood : null
        });
        current.setDate(current.getDate() + 1);
      }
      return dates;
    }
  },
  onShow() {
    this.userId = common_vendor.index.getStorageSync("userId") || 1;
    this.loadStats();
    this.loadMonthlyRecords();
  },
  methods: {
    goBack() {
      common_vendor.index.navigateBack();
    },
    async loadStats() {
      try {
        const res = await utils_request.get(`/study/stats?userId=${this.userId}`);
        if (res.code === 200) {
          this.stats = res.data;
        }
      } catch (error) {
        console.error("加载统计数据失败:", error);
      }
    },
    async loadMonthlyRecords() {
      try {
        const res = await utils_request.get(`/study/record/month?userId=${this.userId}&year=${this.currentYear}&month=${this.currentMonth}`);
        if (res.code === 200) {
          this.monthlyRecords = res.data;
        }
      } catch (error) {
        console.error("加载月度记录失败:", error);
      }
    },
    prevMonth() {
      if (this.currentMonth === 1) {
        this.currentYear--;
        this.currentMonth = 12;
      } else {
        this.currentMonth--;
      }
      this.loadMonthlyRecords();
    },
    nextMonth() {
      if (this.currentMonth === 12) {
        this.currentYear++;
        this.currentMonth = 1;
      } else {
        this.currentMonth++;
      }
      this.loadMonthlyRecords();
    },
    async selectDate(date) {
      if (!date.isCurrentMonth)
        return;
      this.selectedDate = this.formatDateChinese(date.date);
      this.selectedRecord = this.monthlyRecords.find(
        (record) => this.formatDate(new Date(record.checkDate)) === date.dateStr
      );
      this.showDetail = true;
    },
    hideDetailModal() {
      this.showDetail = false;
    },
    showCheckInModal() {
      this.showCheckIn = true;
      this.showCustomDuration = false;
      this.checkInForm = {
        duration: null,
        studyType: "",
        mood: 5,
        note: ""
      };
    },
    hideCheckInModal() {
      this.showCheckIn = false;
    },
    selectDuration(duration) {
      if (duration === "custom") {
        this.showCustomDuration = true;
        this.checkInForm.duration = null;
      } else {
        this.showCustomDuration = false;
        this.checkInForm.duration = duration;
      }
    },
    selectType(type) {
      this.checkInForm.studyType = type;
    },
    async submitCheckIn() {
      if (!this.checkInForm.duration || this.checkInForm.duration <= 0) {
        common_vendor.index.showToast({
          title: "请选择学习时长",
          icon: "none"
        });
        return;
      }
      try {
        const res = await utils_request.post("/study/checkin", {
          userId: this.userId,
          duration: this.checkInForm.duration,
          studyType: this.checkInForm.studyType,
          mood: this.checkInForm.mood,
          note: this.checkInForm.note
        });
        if (res.code === 200) {
          common_vendor.index.showToast({
            title: "打卡成功！",
            icon: "success"
          });
          this.hideCheckInModal();
          this.loadStats();
          this.loadMonthlyRecords();
          common_vendor.index.vibrateShort();
        } else {
          common_vendor.index.showToast({
            title: res.message || "打卡失败",
            icon: "none"
          });
        }
      } catch (error) {
        console.error("打卡失败:", error);
        common_vendor.index.showToast({
          title: "网络错误",
          icon: "none"
        });
      }
    },
    getDateColor(date) {
      if (!date.hasRecord)
        return "transparent";
      const duration = parseFloat(date.duration);
      if (duration >= 4)
        return "#4A90E2";
      if (duration >= 2)
        return "#7BA9E8";
      if (duration >= 1)
        return "#A5C5ED";
      return "#D0E3F7";
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
    formatDateChinese(date) {
      const month = date.getMonth() + 1;
      const day = date.getDate();
      return `${month}月${day}日`;
    },
    isSameDate(date1, date2) {
      return this.formatDate(date1) === this.formatDate(date2);
    },
    getMoodEmoji(mood) {
      const moods = ["", "★", "★★", "★★★", "★★★★", "★★★★★"];
      return moods[mood] || "";
    },
    getMoodText(mood) {
      const texts = ["", "很差", "一般", "不错", "很好", "超棒"];
      return texts[mood] || "不错";
    },
    getTypeIcon(type) {
      const typeObj = this.studyTypes.find((t) => t.value === type);
      return typeObj ? typeObj.label : "其他";
    },
    getTypeName(type) {
      const typeObj = this.studyTypes.find((t) => t.value === type);
      return typeObj ? typeObj.label : "其他";
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.o((...args) => $options.goBack && $options.goBack(...args)),
    b: common_vendor.t($data.stats.continuousDays || 0),
    c: common_vendor.t($data.stats.monthlyDays || 0),
    d: common_vendor.t($options.formatHours($data.stats.totalDuration)),
    e: common_vendor.t($data.currentYear),
    f: common_vendor.t($data.currentMonth),
    g: common_vendor.o((...args) => $options.prevMonth && $options.prevMonth(...args)),
    h: common_vendor.o((...args) => $options.nextMonth && $options.nextMonth(...args)),
    i: common_vendor.f($data.weekdays, (day, k0, i0) => {
      return {
        a: common_vendor.t(day),
        b: day
      };
    }),
    j: common_vendor.f($options.calendarDates, (date, k0, i0) => {
      return common_vendor.e({
        a: common_vendor.t(date.day),
        b: date.hasRecord ? 1 : "",
        c: date.duration
      }, date.duration ? {
        d: common_vendor.t(date.duration)
      } : {}, {
        e: date.key,
        f: !date.isCurrentMonth ? 1 : "",
        g: date.isToday ? 1 : "",
        h: date.hasRecord ? 1 : "",
        i: $options.getDateColor(date),
        j: common_vendor.o(($event) => $options.selectDate(date), date.key)
      });
    }),
    k: !$data.stats.todayChecked
  }, !$data.stats.todayChecked ? {
    l: common_vendor.o((...args) => $options.showCheckInModal && $options.showCheckInModal(...args))
  } : {}, {
    m: $data.showDetail
  }, $data.showDetail ? common_vendor.e({
    n: common_vendor.t($data.selectedDate),
    o: common_vendor.o((...args) => $options.hideDetailModal && $options.hideDetailModal(...args)),
    p: $data.selectedRecord
  }, $data.selectedRecord ? common_vendor.e({
    q: common_vendor.t($data.selectedRecord.duration),
    r: common_vendor.t($options.getMoodEmoji($data.selectedRecord.mood)),
    s: common_vendor.t($options.getMoodText($data.selectedRecord.mood)),
    t: $data.selectedRecord.studyType
  }, $data.selectedRecord.studyType ? {
    v: common_vendor.t($options.getTypeName($data.selectedRecord.studyType))
  } : {}, {
    w: $data.selectedRecord.note
  }, $data.selectedRecord.note ? {
    x: common_vendor.t($data.selectedRecord.note)
  } : {}) : {}, {
    y: common_vendor.o(() => {
    }),
    z: common_vendor.o((...args) => $options.hideDetailModal && $options.hideDetailModal(...args))
  }) : {}, {
    A: $data.showCheckIn
  }, $data.showCheckIn ? common_vendor.e({
    B: common_vendor.o((...args) => $options.hideCheckInModal && $options.hideCheckInModal(...args)),
    C: common_vendor.f($data.quickDurations, (duration, k0, i0) => {
      return {
        a: common_vendor.t(duration),
        b: duration,
        c: $data.checkInForm.duration === duration ? 1 : "",
        d: common_vendor.o(($event) => $options.selectDuration(duration), duration)
      };
    }),
    D: $data.showCustomDuration
  }, $data.showCustomDuration ? {
    E: $data.checkInForm.duration,
    F: common_vendor.o(common_vendor.m(($event) => $data.checkInForm.duration = $event.detail.value, {
      number: true
    }))
  } : {}, {
    G: common_vendor.f($data.studyTypes, (type, k0, i0) => {
      return {
        a: common_vendor.t(type.label),
        b: type.value,
        c: $data.checkInForm.studyType === type.value ? 1 : "",
        d: common_vendor.o(($event) => $options.selectType(type.value), type.value)
      };
    }),
    H: common_vendor.f(5, (i, k0, i0) => {
      return {
        a: common_vendor.t(i <= $data.checkInForm.mood ? "⭐" : "☆"),
        b: i,
        c: i <= $data.checkInForm.mood ? 1 : "",
        d: common_vendor.o(($event) => $data.checkInForm.mood = i, i)
      };
    }),
    I: $data.checkInForm.note,
    J: common_vendor.o(($event) => $data.checkInForm.note = $event.detail.value),
    K: common_vendor.o((...args) => $options.submitCheckIn && $options.submitCheckIn(...args)),
    L: common_vendor.o(() => {
    }),
    M: common_vendor.o((...args) => $options.hideCheckInModal && $options.hideCheckInModal(...args))
  }) : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-eb098735"]]);
wx.createPage(MiniProgramPage);
