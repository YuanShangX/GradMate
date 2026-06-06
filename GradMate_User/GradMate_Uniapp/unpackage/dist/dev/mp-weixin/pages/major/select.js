"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
const _sfc_main = {
  __name: "select",
  setup(__props) {
    const searchKeyword = common_vendor.ref("");
    const majorList = common_vendor.ref([]);
    const selectedMajors = common_vendor.ref([]);
    const loading = common_vendor.ref(false);
    const page = common_vendor.ref(1);
    const noMore = common_vendor.ref(false);
    common_vendor.onLoad(() => {
      loadMajors();
    });
    const loadMajors = async () => {
      if (loading.value || noMore.value)
        return;
      loading.value = true;
      try {
        const res = await common_vendor.index.request({
          url: utils_request.buildApiUrl("/major/search"),
          method: "GET",
          data: {
            majorName: searchKeyword.value || "",
            pageNum: page.value,
            pageSize: 20
          }
        });
        if (res.data.code === 200) {
          const newData = res.data.data.records || [];
          if (newData.length === 0) {
            noMore.value = true;
          } else {
            majorList.value = [...majorList.value, ...newData];
            page.value++;
          }
        }
      } catch (error) {
        console.error("加载专业列表失败:", error);
      } finally {
        loading.value = false;
      }
    };
    const loadMore = () => {
      loadMajors();
    };
    const handleSearch = async () => {
      majorList.value = [];
      page.value = 1;
      noMore.value = false;
      loadMajors();
    };
    const toggleMajor = (major) => {
      const index = selectedMajors.value.findIndex((m) => m.id === major.id);
      if (index > -1) {
        selectedMajors.value.splice(index, 1);
      } else {
        if (selectedMajors.value.length >= 3) {
          common_vendor.index.showToast({
            title: "最多选择3个专业",
            icon: "none"
          });
          return;
        }
        selectedMajors.value.push(major);
      }
    };
    const removeMajor = (index) => {
      selectedMajors.value.splice(index, 1);
    };
    const isSelected = (majorId) => {
      return selectedMajors.value.some((m) => m.id === majorId);
    };
    const goCompare = () => {
      if (selectedMajors.value.length < 2) {
        common_vendor.index.showToast({
          title: "请至少选择2个专业",
          icon: "none"
        });
        return;
      }
      const majorIds = selectedMajors.value.map((m) => m.id);
      common_vendor.index.navigateTo({
        url: `/pages/major/compare?majorIds=${encodeURIComponent(JSON.stringify(majorIds))}`
      });
    };
    const goBack = () => {
      common_vendor.index.navigateBack();
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.o(goBack),
        b: selectedMajors.value.length > 0
      }, selectedMajors.value.length > 0 ? {
        c: common_vendor.f(selectedMajors.value, (major, index, i0) => {
          return {
            a: common_vendor.t(major.majorName),
            b: common_vendor.o(($event) => removeMajor(index), major.id),
            c: major.id
          };
        }),
        d: common_vendor.t(selectedMajors.value.length),
        e: common_vendor.o(goCompare)
      } : {}, {
        f: common_vendor.o(handleSearch),
        g: searchKeyword.value,
        h: common_vendor.o(($event) => searchKeyword.value = $event.detail.value),
        i: common_vendor.f(majorList.value, (major, k0, i0) => {
          return common_vendor.e({
            a: common_vendor.t(major.majorName),
            b: common_vendor.t(major.schoolName),
            c: common_vendor.t(major.majorCode),
            d: isSelected(major.id)
          }, isSelected(major.id) ? {} : {}, {
            e: isSelected(major.id) ? 1 : "",
            f: major.id,
            g: common_vendor.o(($event) => toggleMajor(major), major.id)
          });
        }),
        j: loading.value
      }, loading.value ? {} : {}, {
        k: !loading.value && majorList.value.length === 0
      }, !loading.value && majorList.value.length === 0 ? {} : {}, {
        l: !loading.value && noMore.value && majorList.value.length > 0
      }, !loading.value && noMore.value && majorList.value.length > 0 ? {} : {}, {
        m: common_vendor.o(loadMore)
      });
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-1dd71b77"]]);
wx.createPage(MiniProgramPage);
