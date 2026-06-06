"use strict";
Object.defineProperty(exports, Symbol.toStringTag, { value: "Module" });
const common_vendor = require("./common/vendor.js");
if (!Math) {
  "./pages/login/login.js";
  "./pages/school/index.js";
  "./pages/school/search.js";
  "./pages/school/calendar.js";
  "./pages/school/list.js";
  "./pages/school/detail.js";
  "./pages/school/assistant.js";
  "./pages/school/compare.js";
  "./pages/school/select.js";
  "./pages/major/search.js";
  "./pages/major/detail.js";
  "./pages/major/schoolDetail.js";
  "./pages/major/compare.js";
  "./pages/major/select.js";
  "./pages/info/index.js";
  "./pages/study/tasklist.js";
  "./pages/study/calendar.js";
  "./pages/study/stats.js";
  "./pages/study/record.js";
  "./pages/study/focus.js";
  "./pages/ai/chat.js";
  "./pages/profile/profile.js";
  "./pages/profile/edit.js";
  "./pages/profile/target.js";
  "./pages/profile/favorite.js";
}
const _sfc_main = {
  onLaunch: function() {
    console.log("App Launch");
  },
  onShow: function() {
    console.log("App Show");
  },
  onHide: function() {
    console.log("App Hide");
  }
};
function createApp() {
  const app = common_vendor.createSSRApp(_sfc_main);
  return {
    app
  };
}
createApp().app.mount("#app");
exports.createApp = createApp;
