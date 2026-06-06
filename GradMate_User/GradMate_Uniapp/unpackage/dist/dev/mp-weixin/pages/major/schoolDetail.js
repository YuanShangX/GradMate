"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
const _sfc_main = {
  __name: "schoolDetail",
  setup(__props) {
    const schoolName = common_vendor.ref("");
    const majorCode = common_vendor.ref("");
    const majorName = common_vendor.ref("");
    const majorList = common_vendor.ref([]);
    const loading = common_vendor.ref(false);
    const isFavorited = common_vendor.ref(false);
    const schoolLogo = common_vendor.ref("");
    const userId = common_vendor.ref(1);
    common_vendor.ref(null);
    common_vendor.onMounted(() => {
      const pages = getCurrentPages();
      const currentPage = pages[pages.length - 1];
      const options = currentPage.options;
      schoolName.value = decodeURIComponent(options.schoolName || "");
      majorCode.value = options.majorCode || "";
      majorName.value = decodeURIComponent(options.majorName || "");
      const userInfo = common_vendor.index.getStorageSync("userInfo");
      if (userInfo && userInfo.id) {
        userId.value = userInfo.id;
      }
      loadMajorDetail();
      checkFavoriteStatus();
    });
    const loadMajorDetail = async () => {
      loading.value = true;
      try {
        const res = await common_vendor.index.request({
          url: utils_request.buildApiUrl("/major/schoolMajorDetail"),
          method: "GET",
          data: {
            schoolName: schoolName.value,
            majorCode: majorCode.value
          }
        });
        if (res.data.code === 200 && res.data.data) {
          majorList.value = res.data.data;
          if (res.data.data.length > 0) {
            schoolLogo.value = res.data.data[0].schoolLogo || "";
          }
        }
      } catch (error) {
        console.error("加载专业详情失败:", error);
        common_vendor.index.showToast({
          title: "加载失败",
          icon: "none"
        });
      } finally {
        loading.value = false;
      }
    };
    const checkFavoriteStatus = async () => {
      try {
        const res = await common_vendor.index.request({
          url: utils_request.buildApiUrl("/favorite/checkMajor"),
          method: "GET",
          data: {
            userId: userId.value,
            schoolName: schoolName.value,
            majorCode: majorCode.value
          }
        });
        if (res.data.code === 200) {
          isFavorited.value = res.data.data;
        }
      } catch (error) {
        console.error("检查收藏状态失败:", error);
      }
    };
    const handleFavorite = async () => {
      try {
        if (isFavorited.value) {
          const res = await common_vendor.index.request({
            url: utils_request.buildApiUrl("/favorite/removeMajor"),
            method: "GET",
            data: {
              userId: userId.value,
              schoolName: schoolName.value,
              majorCode: majorCode.value
            }
          });
          if (res.data.code === 200) {
            isFavorited.value = false;
            common_vendor.index.showToast({
              title: "取消收藏成功",
              icon: "success"
            });
          }
        } else {
          const res = await common_vendor.index.request({
            url: utils_request.buildApiUrl("/favorite/addMajor"),
            method: "POST",
            data: {
              userId: userId.value,
              schoolName: schoolName.value,
              majorCode: majorCode.value,
              majorName: majorName.value
            },
            header: {
              "Content-Type": "application/json"
            }
          });
          if (res.data.code === 200) {
            isFavorited.value = true;
            common_vendor.index.showToast({
              title: "收藏成功",
              icon: "success"
            });
          } else {
            common_vendor.index.showToast({
              title: res.data.message || "收藏失败",
              icon: "none"
            });
          }
        }
      } catch (error) {
        console.error("收藏操作失败:", error);
        common_vendor.index.showToast({
          title: "操作失败",
          icon: "none"
        });
      }
    };
    const goBack = () => {
      common_vendor.index.navigateBack();
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.o(goBack),
        b: schoolLogo.value
      }, schoolLogo.value ? {
        c: schoolLogo.value
      } : {}, {
        d: common_vendor.t(schoolName.value),
        e: common_vendor.t(majorCode.value),
        f: common_vendor.t(majorName.value),
        g: common_vendor.t(isFavorited.value ? "★" : "☆"),
        h: common_vendor.t(isFavorited.value ? "已收藏" : "收藏"),
        i: common_vendor.o(handleFavorite),
        j: loading.value
      }, loading.value ? {} : {}, {
        k: !loading.value && majorList.value.length > 0
      }, !loading.value && majorList.value.length > 0 ? {
        l: common_vendor.f(majorList.value, (item, index, i0) => {
          return common_vendor.e({
            a: item.researchDirection
          }, item.researchDirection ? {
            b: common_vendor.t(item.researchDirection)
          } : {}, {
            c: common_vendor.t(item.department || "--"),
            d: common_vendor.t(item.studyMode || "--"),
            e: common_vendor.t(item.examType || "--"),
            f: common_vendor.t(item.plannedEnrollment || "--"),
            g: item.examSubject1
          }, item.examSubject1 ? {
            h: common_vendor.t(item.examSubject1)
          } : {}, {
            i: item.examSubject2
          }, item.examSubject2 ? {
            j: common_vendor.t(item.examSubject2)
          } : {}, {
            k: item.examSubject3
          }, item.examSubject3 ? {
            l: common_vendor.t(item.examSubject3)
          } : {}, {
            m: item.examSubject4
          }, item.examSubject4 ? {
            n: common_vendor.t(item.examSubject4)
          } : {}, {
            o: item.advisor
          }, item.advisor ? {
            p: common_vendor.t(item.advisor)
          } : {}, {
            q: index
          });
        })
      } : {}, {
        m: !loading.value && majorList.value.length === 0
      }, !loading.value && majorList.value.length === 0 ? {} : {}, {
        n: !loading.value && majorList.value.length > 0
      }, !loading.value && majorList.value.length > 0 ? {} : {});
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-bb60bfd6"]]);
wx.createPage(MiniProgramPage);
