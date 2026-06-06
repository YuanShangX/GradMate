"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const utils_request = require("../../utils/request.js");
const _sfc_main = {
  __name: "edit",
  setup(__props) {
    const statusBarHeight = common_vendor.ref(0);
    const formData = common_vendor.ref({
      nickname: "",
      phone: "",
      password: "",
      confirmPassword: ""
    });
    common_vendor.onMounted(() => {
      const systemInfo = common_vendor.index.getSystemInfoSync();
      statusBarHeight.value = systemInfo.statusBarHeight || 0;
      loadUserInfo();
    });
    const goBack = () => {
      common_vendor.index.navigateBack();
    };
    const loadUserInfo = () => {
      const userId = common_vendor.index.getStorageSync("userId");
      if (!userId) {
        common_vendor.index.showToast({
          title: "请先登录",
          icon: "none"
        });
        setTimeout(() => {
          common_vendor.index.navigateBack();
        }, 1500);
        return;
      }
      common_vendor.index.request({
        url: utils_request.buildApiUrl(`/user/info/${userId}`),
        method: "GET",
        success: (res) => {
          if (res.data.code === 200) {
            const user = res.data.data;
            formData.value = {
              nickname: user.nickname || "",
              phone: user.phone || "",
              password: "",
              confirmPassword: ""
            };
          }
        }
      });
    };
    const saveProfile = () => {
      const userId = common_vendor.index.getStorageSync("userId");
      if (formData.value.phone && !/^1[3-9]\d{9}$/.test(formData.value.phone)) {
        common_vendor.index.showToast({
          title: "手机号格式不正确",
          icon: "none"
        });
        return;
      }
      if (formData.value.password) {
        if (formData.value.password.length < 6) {
          common_vendor.index.showToast({
            title: "密码至少6位",
            icon: "none"
          });
          return;
        }
        if (formData.value.password !== formData.value.confirmPassword) {
          common_vendor.index.showToast({
            title: "两次密码不一致",
            icon: "none"
          });
          return;
        }
      }
      common_vendor.index.showLoading({ title: "保存中..." });
      const updateData = {
        id: userId,
        nickname: formData.value.nickname,
        phone: formData.value.phone
      };
      if (formData.value.password) {
        updateData.password = formData.value.password;
      }
      common_vendor.index.request({
        url: utils_request.buildApiUrl("/user/update"),
        method: "PUT",
        data: updateData,
        success: (res) => {
          common_vendor.index.hideLoading();
          if (res.data.code === 200) {
            common_vendor.index.showToast({
              title: "保存成功",
              icon: "success"
            });
            const userInfo = common_vendor.index.getStorageSync("userInfo") || {};
            userInfo.nickname = formData.value.nickname;
            userInfo.phone = formData.value.phone;
            common_vendor.index.setStorageSync("userInfo", userInfo);
            setTimeout(() => {
              common_vendor.index.navigateBack();
            }, 1500);
          } else {
            common_vendor.index.showToast({
              title: res.data.message || "保存失败",
              icon: "none"
            });
          }
        },
        fail: () => {
          common_vendor.index.hideLoading();
          common_vendor.index.showToast({
            title: "网络错误",
            icon: "none"
          });
        }
      });
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: statusBarHeight.value + "px",
        b: common_vendor.o(goBack),
        c: common_assets._imports_0,
        d: formData.value.nickname,
        e: common_vendor.o(($event) => formData.value.nickname = $event.detail.value),
        f: formData.value.phone,
        g: common_vendor.o(($event) => formData.value.phone = $event.detail.value),
        h: formData.value.password,
        i: common_vendor.o(($event) => formData.value.password = $event.detail.value),
        j: formData.value.password
      }, formData.value.password ? {
        k: formData.value.confirmPassword,
        l: common_vendor.o(($event) => formData.value.confirmPassword = $event.detail.value)
      } : {}, {
        m: common_vendor.o(saveProfile)
      });
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-ead3e541"]]);
wx.createPage(MiniProgramPage);
