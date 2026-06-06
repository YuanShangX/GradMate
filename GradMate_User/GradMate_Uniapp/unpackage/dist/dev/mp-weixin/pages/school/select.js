"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
const _sfc_main = {
  __name: "select",
  setup(__props) {
    const searchKeyword = common_vendor.ref("");
    const schoolList = common_vendor.ref([]);
    const selectedSchools = common_vendor.ref([]);
    const loading = common_vendor.ref(false);
    common_vendor.onLoad(() => {
      loadSchools();
    });
    const loadSchools = async () => {
      loading.value = true;
      try {
        const res = await common_vendor.index.request({
          url: utils_request.buildApiUrl("/school/list"),
          method: "GET",
          data: {
            schoolName: searchKeyword.value || "",
            pageNum: 1,
            pageSize: 100
          }
        });
        if (res.data.code === 200) {
          schoolList.value = res.data.data.records || [];
        }
      } catch (error) {
        console.error("加载院校列表失败:", error);
      } finally {
        loading.value = false;
      }
    };
    const handleSearch = async () => {
      loadSchools();
    };
    const toggleSchool = (school) => {
      const index = selectedSchools.value.findIndex((s) => s.id === school.id);
      if (index > -1) {
        selectedSchools.value.splice(index, 1);
      } else {
        if (selectedSchools.value.length >= 3) {
          common_vendor.index.showToast({
            title: "最多选择3个院校",
            icon: "none"
          });
          return;
        }
        selectedSchools.value.push(school);
      }
    };
    const removeSchool = (index) => {
      selectedSchools.value.splice(index, 1);
    };
    const isSelected = (schoolId) => {
      return selectedSchools.value.some((s) => s.id === schoolId);
    };
    const goCompare = () => {
      if (selectedSchools.value.length < 2) {
        common_vendor.index.showToast({
          title: "请至少选择2个院校",
          icon: "none"
        });
        return;
      }
      const schoolIds = selectedSchools.value.map((s) => s.id);
      common_vendor.index.navigateTo({
        url: `/pages/school/compare?schoolIds=${encodeURIComponent(JSON.stringify(schoolIds))}`
      });
    };
    const goBack = () => {
      common_vendor.index.navigateBack();
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.o(goBack),
        b: selectedSchools.value.length > 0
      }, selectedSchools.value.length > 0 ? {
        c: common_vendor.f(selectedSchools.value, (school, index, i0) => {
          return {
            a: common_vendor.t(school.schoolName),
            b: common_vendor.o(($event) => removeSchool(index), school.id),
            c: school.id
          };
        }),
        d: common_vendor.t(selectedSchools.value.length),
        e: common_vendor.o(goCompare)
      } : {}, {
        f: common_vendor.o(handleSearch),
        g: searchKeyword.value,
        h: common_vendor.o(($event) => searchKeyword.value = $event.detail.value),
        i: common_vendor.f(schoolList.value, (school, k0, i0) => {
          return common_vendor.e({
            a: school.logo
          }, school.logo ? {
            b: school.logo
          } : {}, {
            c: common_vendor.t(school.schoolName),
            d: common_vendor.t(school.location),
            e: isSelected(school.id)
          }, isSelected(school.id) ? {} : {}, {
            f: isSelected(school.id) ? 1 : "",
            g: school.id,
            h: common_vendor.o(($event) => toggleSchool(school), school.id)
          });
        }),
        j: loading.value
      }, loading.value ? {} : {}, {
        k: !loading.value && schoolList.value.length === 0
      }, !loading.value && schoolList.value.length === 0 ? {} : {});
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-4acce469"]]);
wx.createPage(MiniProgramPage);
