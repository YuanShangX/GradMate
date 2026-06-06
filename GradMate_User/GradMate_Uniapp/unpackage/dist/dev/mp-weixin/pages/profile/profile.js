"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const utils_request = require("../../utils/request.js");
const _sfc_main = {
  __name: "profile",
  setup(__props) {
    const statusBarHeight = common_vendor.ref(0);
    const isLogin = common_vendor.ref(false);
    const userInfo = common_vendor.ref({});
    const favoriteCount = common_vendor.ref(0);
    const stats = common_vendor.ref({
      totalStudyDuration: 0,
      continuousDays: 0,
      totalFavorites: 0,
      completedTasks: 0
    });
    const targetDetail = common_vendor.ref({
      targetSchool: null,
      targetMajor: null
    });
    common_vendor.onMounted(() => {
      const systemInfo = common_vendor.index.getSystemInfoSync();
      statusBarHeight.value = systemInfo.statusBarHeight || 0;
      checkLoginStatus();
      if (isLogin.value) {
        loadFavoriteCount();
        loadUserStats();
        loadTargetDetail();
      }
    });
    const checkLoginStatus = () => {
      const loginStatus = common_vendor.index.getStorageSync("isLogin");
      const userData = common_vendor.index.getStorageSync("userInfo");
      isLogin.value = !!loginStatus;
      if (loginStatus && userData) {
        userInfo.value = userData;
      }
    };
    const loadFavoriteCount = async () => {
      try {
        const res = await utils_request.get(`/favorite/count?userId=${userInfo.value.id}`);
        if (res.code === 200) {
          favoriteCount.value = res.data.total || 0;
          stats.value.totalFavorites = res.data.total || 0;
        }
      } catch (error) {
        console.error("加载收藏数量失败:", error);
      }
    };
    const loadUserStats = async () => {
      try {
        const res = await utils_request.get(`/study/stats?userId=${userInfo.value.id}`);
        if (res.code === 200) {
          stats.value = {
            ...stats.value,
            ...res.data
          };
        }
      } catch (error) {
        console.error("加载统计数据失败:", error);
      }
    };
    const loadTargetDetail = async () => {
      try {
        const res = await utils_request.get(`/user/target/${userInfo.value.id}`);
        if (res.code === 200 && res.data) {
          targetDetail.value = {
            targetSchool: res.data.targetSchool ? {
              id: res.data.targetSchoolId,
              name: res.data.targetSchool
            } : null,
            targetMajor: res.data.targetMajor ? {
              id: res.data.targetMajorId,
              name: res.data.targetMajor
            } : null
          };
        }
      } catch (error) {
        console.error("加载目标详情失败:", error);
      }
    };
    const goLogin = () => {
      common_vendor.index.navigateTo({
        url: "/pages/login/login"
      });
    };
    const goToEdit = () => {
      common_vendor.index.navigateTo({
        url: "/pages/profile/edit"
      });
    };
    const goToFavorite = () => {
      common_vendor.index.navigateTo({
        url: "/pages/profile/favorite"
      });
    };
    const goToTargetPage = () => {
      common_vendor.index.navigateTo({
        url: "/pages/profile/target"
      });
    };
    const goToStudy = () => {
      common_vendor.index.navigateTo({
        url: "/pages/study/stats"
      });
    };
    const goToSchoolDetail = () => {
      if (targetDetail.value.targetSchool && targetDetail.value.targetSchool.id) {
        common_vendor.index.navigateTo({
          url: `/pages/school/detail?id=${targetDetail.value.targetSchool.id}`
        });
      }
    };
    const goToMajorDetail = () => {
      if (targetDetail.value.targetMajor && targetDetail.value.targetMajor.id) {
        common_vendor.index.navigateTo({
          url: `/pages/major/detail?id=${targetDetail.value.targetMajor.id}`
        });
      }
    };
    const handleLogout = () => {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要退出登录吗？",
        success: (res) => {
          if (res.confirm) {
            common_vendor.index.removeStorageSync("userInfo");
            common_vendor.index.removeStorageSync("isLogin");
            isLogin.value = false;
            userInfo.value = {};
            common_vendor.index.showToast({
              title: "已退出登录",
              icon: "success"
            });
          }
        }
      });
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: statusBarHeight.value + "px",
        b: !isLogin.value
      }, !isLogin.value ? {
        c: common_vendor.o(goLogin)
      } : common_vendor.e({
        d: common_assets._imports_0,
        e: common_vendor.t(userInfo.value.nickname || "未设置昵称"),
        f: common_vendor.t(userInfo.value.phone || "未绑定手机号"),
        g: common_vendor.o(goToEdit),
        h: targetDetail.value.targetSchool
      }, targetDetail.value.targetSchool ? {
        i: common_vendor.t(targetDetail.value.targetSchool.name),
        j: common_vendor.o(goToSchoolDetail)
      } : {
        k: common_vendor.t(userInfo.value.targetSchool || "未设置")
      }, {
        l: targetDetail.value.targetMajor
      }, targetDetail.value.targetMajor ? {
        m: common_vendor.t(targetDetail.value.targetMajor.name),
        n: common_vendor.o(goToMajorDetail)
      } : {
        o: common_vendor.t(userInfo.value.targetMajor || "未设置")
      }, {
        p: common_vendor.t(targetDetail.value.targetSchool || targetDetail.value.targetMajor ? "重新设置目标" : "设置目标"),
        q: common_vendor.o(goToTargetPage),
        r: common_vendor.t(stats.value.totalStudyDuration || 0),
        s: common_vendor.t(stats.value.continuousDays || 0),
        t: common_vendor.t(stats.value.totalFavorites || 0),
        v: common_vendor.t(stats.value.completedTasks || 0),
        w: common_vendor.t(favoriteCount.value),
        x: common_vendor.o(goToFavorite),
        y: common_vendor.o(goToStudy),
        z: common_vendor.o(handleLogout)
      }));
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-dd383ca2"]]);
wx.createPage(MiniProgramPage);
