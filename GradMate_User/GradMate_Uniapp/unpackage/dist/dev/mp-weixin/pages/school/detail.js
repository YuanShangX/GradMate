"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
const _sfc_main = {
  __name: "detail",
  setup(__props) {
    const schoolId = common_vendor.ref(null);
    const schoolName = common_vendor.ref("");
    const schoolDetail = common_vendor.ref(null);
    const majorList = common_vendor.ref([]);
    const infoList = common_vendor.ref([]);
    const loading = common_vendor.ref(true);
    const isFavorited = common_vendor.ref(false);
    const userId = common_vendor.ref(1);
    const showAllMajors = common_vendor.ref(false);
    common_vendor.onMounted(() => {
      const pages = getCurrentPages();
      const currentPage = pages[pages.length - 1];
      const options = currentPage.options;
      schoolId.value = options.id;
      schoolName.value = decodeURIComponent(options.name || "");
      const userInfo = common_vendor.index.getStorageSync("userInfo");
      if (userInfo && userInfo.id) {
        userId.value = userInfo.id;
      }
      loadSchoolDetail();
      checkFavoriteStatus();
    });
    const loadSchoolDetail = async () => {
      loading.value = true;
      try {
        const res = await common_vendor.index.request({
          url: utils_request.buildApiUrl(`/school/${schoolId.value}`),
          method: "GET"
        });
        if (res.data.code === 200) {
          schoolDetail.value = res.data.data;
          if (schoolDetail.value && schoolDetail.value.school) {
            loadMajorList(schoolDetail.value.school.schoolName);
            loadInfoList();
          }
        }
      } catch (error) {
        console.error("加载学校详情失败:", error);
        common_vendor.index.showToast({
          title: "加载失败",
          icon: "none"
        });
      } finally {
        loading.value = false;
      }
    };
    const loadMajorList = async (name) => {
      if (!name) {
        console.error("学校名称为空");
        return;
      }
      try {
        const res = await common_vendor.index.request({
          url: utils_request.buildApiUrl(`/major/school/${encodeURIComponent(name)}`),
          method: "GET",
          data: {
            pageNum: 1,
            pageSize: 100
            // 获取更多专业，以便显示全部
          }
        });
        if (res.data.code === 200 && res.data.data && res.data.data.records) {
          majorList.value = res.data.data.records;
        }
      } catch (error) {
        console.error("加载专业列表失败:", error);
      }
    };
    const loadInfoList = async () => {
      if (!schoolId.value) {
        console.error("学校ID为空");
        return;
      }
      try {
        const [brochureRes, adjustRes, informationRes] = await Promise.all([
          // 招生简章
          common_vendor.index.request({
            url: utils_request.buildApiUrl("/info/brochure/list"),
            method: "GET",
            data: {
              schoolId: schoolId.value,
              pageNum: 1,
              pageSize: 5
            }
          }),
          // 调剂公告
          common_vendor.index.request({
            url: utils_request.buildApiUrl("/info/adjust/list"),
            method: "GET",
            data: {
              schoolId: schoolId.value,
              pageNum: 1,
              pageSize: 5
            }
          }),
          // 招生通知
          common_vendor.index.request({
            url: utils_request.buildApiUrl("/info/information/list"),
            method: "GET",
            data: {
              schoolId: schoolId.value,
              pageNum: 1,
              pageSize: 5
            }
          })
        ]);
        const allInfo = [];
        if (brochureRes.data.code === 200 && brochureRes.data.data && brochureRes.data.data.records) {
          allInfo.push(...brochureRes.data.data.records.map((item) => ({
            ...item,
            type: "招生简章"
          })));
        }
        if (adjustRes.data.code === 200 && adjustRes.data.data && adjustRes.data.data.records) {
          allInfo.push(...adjustRes.data.data.records.map((item) => ({
            ...item,
            type: "调剂公告"
          })));
        }
        if (informationRes.data.code === 200 && informationRes.data.data && informationRes.data.data.records) {
          allInfo.push(...informationRes.data.data.records.map((item) => ({
            ...item,
            type: "招生通知"
          })));
        }
        allInfo.sort((a, b) => {
          const dateA = new Date(a.publishDate || 0);
          const dateB = new Date(b.publishDate || 0);
          return dateB - dateA;
        });
        infoList.value = allInfo.slice(0, 10);
      } catch (error) {
        console.error("加载资讯列表失败:", error);
      }
    };
    const checkFavoriteStatus = async () => {
      try {
        const res = await common_vendor.index.request({
          url: utils_request.buildApiUrl("/favorite/check"),
          method: "GET",
          data: {
            userId: userId.value,
            type: "school",
            targetId: schoolId.value
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
            url: utils_request.buildApiUrl("/favorite/remove"),
            method: "GET",
            data: {
              userId: userId.value,
              type: "school",
              targetId: schoolId.value
            }
          });
          if (res.data.code === 200) {
            isFavorited.value = false;
            common_vendor.index.showToast({
              title: "取消关注成功",
              icon: "success"
            });
          }
        } else {
          const res = await common_vendor.index.request({
            url: utils_request.buildApiUrl("/favorite/add"),
            method: "POST",
            data: {
              userId: userId.value,
              type: "school",
              targetId: schoolId.value,
              targetName: schoolDetail.value.school.schoolName
            },
            header: {
              "Content-Type": "application/json"
            }
          });
          if (res.data.code === 200) {
            isFavorited.value = true;
            common_vendor.index.showToast({
              title: "关注成功",
              icon: "success"
            });
          }
        }
      } catch (error) {
        console.error("关注操作失败:", error);
        common_vendor.index.showToast({
          title: "操作失败",
          icon: "none"
        });
      }
    };
    const goToMajorDetail = (major) => {
      if (schoolDetail.value && schoolDetail.value.school) {
        common_vendor.index.navigateTo({
          url: `/pages/major/schoolDetail?schoolName=${encodeURIComponent(schoolDetail.value.school.schoolName)}&majorCode=${major.majorCode}&majorName=${encodeURIComponent(major.majorName)}`
        });
      }
    };
    const openWebsite = () => {
      if (schoolDetail.value && schoolDetail.value.school && schoolDetail.value.school.link) {
        common_vendor.index.navigateTo({
          url: `/pages/webview/index?url=${encodeURIComponent(schoolDetail.value.school.link)}`
        });
      } else {
        common_vendor.index.showToast({
          title: "暂无官网链接",
          icon: "none"
        });
      }
    };
    const openInfoUrl = (info) => {
      if (info && info.url) {
        common_vendor.index.navigateTo({
          url: `/pages/webview/index?url=${encodeURIComponent(info.url)}`
        });
      } else {
        common_vendor.index.showToast({
          title: "暂无资讯链接",
          icon: "none"
        });
      }
    };
    const formatDate = (dateStr) => {
      if (!dateStr)
        return "--";
      try {
        const date = new Date(dateStr);
        return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
      } catch {
        return "--";
      }
    };
    const goBack = () => {
      common_vendor.index.navigateBack();
    };
    return (_ctx, _cache) => {
      var _a, _b, _c, _d, _e, _f, _g, _h, _i, _j, _k, _l, _m, _n;
      return common_vendor.e({
        a: common_vendor.o(goBack),
        b: schoolDetail.value && schoolDetail.value.school && schoolDetail.value.school.logo
      }, schoolDetail.value && schoolDetail.value.school && schoolDetail.value.school.logo ? {
        c: schoolDetail.value.school.logo
      } : {}, {
        d: common_vendor.t(((_b = (_a = schoolDetail.value) == null ? void 0 : _a.school) == null ? void 0 : _b.schoolName) || ""),
        e: common_vendor.t(((_d = (_c = schoolDetail.value) == null ? void 0 : _c.school) == null ? void 0 : _d.location) || ""),
        f: common_vendor.t(((_f = (_e = schoolDetail.value) == null ? void 0 : _e.school) == null ? void 0 : _f.subordinate) || "教育部"),
        g: (_g = schoolDetail.value) == null ? void 0 : _g.is985
      }, ((_h = schoolDetail.value) == null ? void 0 : _h.is985) ? {} : {}, {
        h: (_i = schoolDetail.value) == null ? void 0 : _i.is211
      }, ((_j = schoolDetail.value) == null ? void 0 : _j.is211) ? {} : {}, {
        i: (_k = schoolDetail.value) == null ? void 0 : _k.isSyl
      }, ((_l = schoolDetail.value) == null ? void 0 : _l.isSyl) ? {} : {}, {
        j: (_m = schoolDetail.value) == null ? void 0 : _m.isZhx
      }, ((_n = schoolDetail.value) == null ? void 0 : _n.isZhx) ? {} : {}, {
        k: common_vendor.t(isFavorited.value ? "♥" : "♡"),
        l: common_vendor.t(isFavorited.value ? "已关注" : "关注"),
        m: common_vendor.o(handleFavorite),
        n: loading.value
      }, loading.value ? {} : schoolDetail.value ? common_vendor.e({
        p: schoolDetail.value.school.link
      }, schoolDetail.value.school.link ? {
        q: common_vendor.o(openWebsite)
      } : {}, {
        r: majorList.value.length > 0
      }, majorList.value.length > 0 ? common_vendor.e({
        s: common_vendor.f(showAllMajors.value ? majorList.value : majorList.value.slice(0, 8), (major, index, i0) => {
          return {
            a: common_vendor.t(major.majorCode),
            b: common_vendor.t(major.majorName),
            c: index,
            d: common_vendor.o(($event) => goToMajorDetail(major), index)
          };
        }),
        t: !showAllMajors.value && majorList.value.length > 8
      }, !showAllMajors.value && majorList.value.length > 8 ? {
        v: common_vendor.t(majorList.value.length),
        w: common_vendor.o(($event) => showAllMajors.value = true)
      } : {}, {
        x: showAllMajors.value && majorList.value.length > 8
      }, showAllMajors.value && majorList.value.length > 8 ? {
        y: common_vendor.o(($event) => showAllMajors.value = false)
      } : {}) : {}, {
        z: infoList.value.length > 0
      }, infoList.value.length > 0 ? {
        A: common_vendor.f(infoList.value.slice(0, 5), (info, index, i0) => {
          return {
            a: common_vendor.t(info.title),
            b: common_vendor.t(info.type),
            c: common_vendor.t(formatDate(info.publishDate)),
            d: index,
            e: common_vendor.o(($event) => openInfoUrl(info), index)
          };
        })
      } : {}) : {}, {
        o: schoolDetail.value,
        B: !loading.value && schoolDetail.value
      }, !loading.value && schoolDetail.value ? {} : {});
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-6b1a9528"]]);
wx.createPage(MiniProgramPage);
