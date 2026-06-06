"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
const common_assets = require("../../common/assets.js");
const _sfc_main = {
  data() {
    return {
      statusBarHeight: 0,
      currentTab: 0,
      // 0-登录 1-注册
      loginForm: {
        username: "",
        password: ""
      },
      registerForm: {
        username: "",
        password: "",
        confirmPassword: "",
        nickname: ""
      },
      loginLoading: false,
      registerLoading: false
    };
  },
  onLoad() {
    const systemInfo = common_vendor.index.getSystemInfoSync();
    this.statusBarHeight = systemInfo.statusBarHeight || 0;
    this.checkAutoLogin();
  },
  computed: {
    canLogin() {
      return this.loginForm.username.trim().length > 0 && this.loginForm.password.trim().length > 0 && !this.loginLoading;
    },
    canRegister() {
      return this.registerForm.username.trim().length > 0 && this.registerForm.password.trim().length >= 6 && this.registerForm.confirmPassword.trim().length > 0 && !this.registerLoading;
    }
  },
  methods: {
    // 检查自动登录
    checkAutoLogin() {
      const isLogin = common_vendor.index.getStorageSync("isLogin");
      const userInfo = common_vendor.index.getStorageSync("userInfo");
      if (isLogin && userInfo && userInfo.id) {
        common_vendor.index.showToast({
          title: "自动登录中...",
          icon: "loading",
          duration: 1e3
        });
        setTimeout(() => {
          common_vendor.index.switchTab({
            url: "/pages/school/index"
          });
        }, 1e3);
      }
    },
    // 登录
    async handleLogin() {
      if (!this.canLogin)
        return;
      this.loginLoading = true;
      try {
        const res = await utils_request.post("/user/login", {
          username: this.loginForm.username,
          password: this.loginForm.password
        });
        if (res.code === 200) {
          common_vendor.index.setStorageSync("userInfo", res.data);
          common_vendor.index.setStorageSync("userId", res.data.id);
          common_vendor.index.setStorageSync("isLogin", true);
          common_vendor.index.showToast({
            title: "登录成功",
            icon: "success"
          });
          setTimeout(() => {
            common_vendor.index.switchTab({
              url: "/pages/school/index"
            });
          }, 1500);
        }
      } catch (error) {
        console.error("登录失败:", error);
      } finally {
        this.loginLoading = false;
      }
    },
    // 注册
    async handleRegister() {
      if (!this.canRegister)
        return;
      if (this.registerForm.password !== this.registerForm.confirmPassword) {
        common_vendor.index.showToast({
          title: "两次密码不一致",
          icon: "none"
        });
        return;
      }
      if (this.registerForm.password.length < 6) {
        common_vendor.index.showToast({
          title: "密码至少6位",
          icon: "none"
        });
        return;
      }
      this.registerLoading = true;
      try {
        const res = await utils_request.post("/user/register", {
          username: this.registerForm.username,
          password: this.registerForm.password,
          nickname: this.registerForm.nickname || this.registerForm.username
        });
        if (res.code === 200) {
          common_vendor.index.showToast({
            title: "注册成功，请登录",
            icon: "success"
          });
          setTimeout(() => {
            this.currentTab = 0;
            this.loginForm.username = this.registerForm.username;
            this.loginForm.password = this.registerForm.password;
          }, 1500);
        }
      } catch (error) {
        console.error("注册失败:", error);
      } finally {
        this.registerLoading = false;
      }
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $data.statusBarHeight + "px",
    b: common_assets._imports_0,
    c: $data.currentTab === 0 ? 1 : "",
    d: common_vendor.o(($event) => $data.currentTab = 0),
    e: $data.currentTab === 1 ? 1 : "",
    f: common_vendor.o(($event) => $data.currentTab = 1),
    g: $data.currentTab === 0
  }, $data.currentTab === 0 ? {
    h: $data.loginForm.username,
    i: common_vendor.o(($event) => $data.loginForm.username = $event.detail.value),
    j: $data.loginForm.password,
    k: common_vendor.o(($event) => $data.loginForm.password = $event.detail.value),
    l: common_vendor.t($data.loginLoading ? "登录中..." : "立即登录"),
    m: common_vendor.o((...args) => $options.handleLogin && $options.handleLogin(...args)),
    n: !$options.canLogin
  } : {
    o: $data.registerForm.username,
    p: common_vendor.o(($event) => $data.registerForm.username = $event.detail.value),
    q: $data.registerForm.password,
    r: common_vendor.o(($event) => $data.registerForm.password = $event.detail.value),
    s: $data.registerForm.confirmPassword,
    t: common_vendor.o(($event) => $data.registerForm.confirmPassword = $event.detail.value),
    v: $data.registerForm.nickname,
    w: common_vendor.o(($event) => $data.registerForm.nickname = $event.detail.value),
    x: common_vendor.t($data.registerLoading ? "注册中..." : "立即注册"),
    y: common_vendor.o((...args) => $options.handleRegister && $options.handleRegister(...args)),
    z: !$options.canRegister
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-e4e4508d"]]);
wx.createPage(MiniProgramPage);
