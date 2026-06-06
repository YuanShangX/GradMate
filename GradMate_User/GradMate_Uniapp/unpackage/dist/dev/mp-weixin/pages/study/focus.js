"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
if (!Array) {
  const _component_circle = common_vendor.resolveComponent("circle");
  const _component_svg = common_vendor.resolveComponent("svg");
  (_component_circle + _component_svg)();
}
const _sfc_main = {
  __name: "focus",
  setup(__props) {
    const taskId = common_vendor.ref(null);
    const taskName = common_vendor.ref("");
    const duration = common_vendor.ref(25);
    const remainingSeconds = common_vendor.ref(0);
    const timer = common_vendor.ref(null);
    const isPaused = common_vendor.ref(false);
    const startTime = common_vendor.ref(null);
    const pauseTime = common_vendor.ref(null);
    const totalPauseDuration = common_vendor.ref(0);
    const focusCount = common_vendor.ref(1);
    const pauseCount = common_vendor.ref(0);
    const quotes = [
      "微尘莫若都可让我动容，却无事可使我动摇",
      "你的努力，时间看得见",
      "每一次专注，都是在靠近梦想",
      "坚持下去，未来可期",
      "今天的努力，是明天的底气",
      "专注当下，成就未来",
      "不积跬步，无以至千里",
      "天道酬勤，功不唐捐"
    ];
    const currentQuote = common_vendor.ref("");
    const formattedTime = common_vendor.computed(() => {
      const minutes = Math.floor(remainingSeconds.value / 60);
      const seconds = remainingSeconds.value % 60;
      return `${String(minutes).padStart(2, "0")}:${String(seconds).padStart(2, "0")}`;
    });
    const statusText = common_vendor.computed(() => {
      if (isPaused.value) {
        return "已暂停";
      }
      return "进行中";
    });
    const progressOffset = common_vendor.computed(() => {
      const totalSeconds = duration.value * 60;
      const progress = remainingSeconds.value / totalSeconds;
      const circumference = 2 * Math.PI * 90;
      return circumference * (1 - progress);
    });
    common_vendor.onMounted(() => {
      const pages = getCurrentPages();
      const currentPage = pages[pages.length - 1];
      const options = currentPage.options;
      taskId.value = options.taskId;
      taskName.value = options.taskName || "学习任务";
      duration.value = parseInt(options.duration) || 25;
      remainingSeconds.value = duration.value * 60;
      currentQuote.value = quotes[Math.floor(Math.random() * quotes.length)];
      startFocus();
    });
    common_vendor.onUnmounted(() => {
      clearTimer();
    });
    const startFocus = () => {
      startTime.value = Date.now();
      isPaused.value = false;
      startTimer();
      common_vendor.index.vibrateShort();
    };
    const startTimer = () => {
      clearTimer();
      timer.value = setInterval(() => {
        if (remainingSeconds.value > 0) {
          remainingSeconds.value--;
        } else {
          onComplete();
        }
      }, 1e3);
    };
    const clearTimer = () => {
      if (timer.value) {
        clearInterval(timer.value);
        timer.value = null;
      }
    };
    const togglePause = () => {
      if (isPaused.value) {
        totalPauseDuration.value += Date.now() - pauseTime.value;
        isPaused.value = false;
        startTimer();
        common_vendor.index.vibrateShort();
      } else {
        pauseTime.value = Date.now();
        isPaused.value = true;
        pauseCount.value++;
        clearTimer();
        common_vendor.index.vibrateShort();
      }
    };
    const stopFocus = () => {
      common_vendor.index.showModal({
        title: "确认停止",
        content: "确定要停止专注吗？学习时长将被记录。",
        success: async (res) => {
          if (res.confirm) {
            await saveFocusRecord();
            common_vendor.index.navigateBack();
          }
        }
      });
    };
    const confirmExit = () => {
      common_vendor.index.showModal({
        title: "确认退出",
        content: "确定要退出专注吗？当前进度将不会保存。",
        success: (res) => {
          if (res.confirm) {
            clearTimer();
            common_vendor.index.navigateBack();
          }
        }
      });
    };
    const onComplete = async () => {
      clearTimer();
      common_vendor.index.vibrateLong();
      common_vendor.index.showToast({
        title: "专注完成！",
        icon: "success",
        duration: 2e3
      });
      await saveFocusRecord();
      setTimeout(() => {
        common_vendor.index.navigateBack();
      }, 2e3);
    };
    const saveFocusRecord = async () => {
      try {
        const userId = common_vendor.index.getStorageSync("userId") || 1;
        const actualDuration = Math.floor((duration.value * 60 - remainingSeconds.value) / 60);
        if (actualDuration < 1) {
          return;
        }
        await utils_request.post("/study/checkin", {
          userId,
          studyDuration: actualDuration,
          focusCount: focusCount.value,
          pauseCount: pauseCount.value,
          notes: `完成任务：${taskName.value}`
        });
        if (taskId.value) {
          await utils_request.post(`/study/task/${taskId.value}/complete`);
        }
      } catch (error) {
        console.error("保存专注记录失败:", error);
      }
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.o(confirmExit),
        b: common_vendor.t(currentQuote.value),
        c: common_vendor.p({
          cx: "100",
          cy: "100",
          r: "90"
        }),
        d: progressOffset.value,
        e: common_vendor.p({
          cx: "100",
          cy: "100",
          r: "90"
        }),
        f: common_vendor.p({
          viewBox: "0 0 200 200"
        }),
        g: common_vendor.t(formattedTime.value),
        h: common_vendor.t(statusText.value),
        i: isPaused.value ? 1 : "",
        j: common_vendor.t(taskName.value),
        k: common_vendor.t(duration.value),
        l: common_vendor.t(focusCount.value),
        m: common_vendor.t(pauseCount.value),
        n: isPaused.value
      }, isPaused.value ? {} : {}, {
        o: common_vendor.t(isPaused.value ? "继续" : "暂停"),
        p: common_vendor.o(togglePause),
        q: common_vendor.o(stopFocus)
      });
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-f88b364c"]]);
wx.createPage(MiniProgramPage);
