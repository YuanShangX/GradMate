"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
const _sfc_main = {
  __name: "search",
  setup(__props) {
    const degreeType = common_vendor.ref("academic");
    const selectedCategory = common_vendor.ref("02");
    const expandedDisciplines = common_vendor.ref([]);
    const loading = common_vendor.ref(false);
    const disciplineList = common_vendor.ref([]);
    const categoryList = common_vendor.ref([
      { code: "01", name: "哲学" },
      { code: "02", name: "经济学" },
      { code: "03", name: "法学" },
      { code: "04", name: "教育学" },
      { code: "05", name: "文学" },
      { code: "06", name: "历史学" },
      { code: "07", name: "理学" },
      { code: "08", name: "工学" },
      { code: "09", name: "农学" },
      { code: "10", name: "医学" },
      { code: "11", name: "军事学" },
      { code: "12", name: "管理学" },
      { code: "13", name: "艺术学" },
      { code: "14", name: "交叉学科" }
    ]);
    common_vendor.onMounted(() => {
      loadDisciplines();
    });
    common_vendor.watch(selectedCategory, () => {
      expandedDisciplines.value = [];
      loadDisciplines();
    });
    common_vendor.watch(degreeType, () => {
      expandedDisciplines.value = [];
      loadDisciplines();
    });
    const selectCategory = (code) => {
      selectedCategory.value = code;
    };
    const toggleDiscipline = (code) => {
      const index = expandedDisciplines.value.indexOf(code);
      if (index > -1) {
        expandedDisciplines.value.splice(index, 1);
      } else {
        expandedDisciplines.value.push(code);
      }
    };
    const loadDisciplines = async () => {
      loading.value = true;
      try {
        const res = await common_vendor.index.request({
          url: utils_request.buildApiUrl("/major/disciplines-v2"),
          method: "GET",
          data: {
            categoryCode: selectedCategory.value,
            degreeType: degreeType.value
          }
        });
        if (res.data.code === 200 && res.data.data) {
          disciplineList.value = res.data.data;
        }
      } catch (error) {
        console.error("加载学科列表失败:", error);
        common_vendor.index.showToast({
          title: "加载失败",
          icon: "none"
        });
      } finally {
        loading.value = false;
      }
    };
    const goToMajorDetail = (major) => {
      common_vendor.index.navigateTo({
        url: `/pages/major/detail?code=${major.code}&name=${encodeURIComponent(major.name)}`
      });
    };
    const goBack = () => {
      common_vendor.index.navigateBack();
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.o(goBack),
        b: degreeType.value === "academic" ? 1 : "",
        c: common_vendor.o(($event) => degreeType.value = "academic"),
        d: degreeType.value === "professional" ? 1 : "",
        e: common_vendor.o(($event) => degreeType.value = "professional"),
        f: common_vendor.f(categoryList.value, (category, index, i0) => {
          return {
            a: common_vendor.t(category.name),
            b: index,
            c: selectedCategory.value === category.code ? 1 : "",
            d: common_vendor.o(($event) => selectCategory(category.code), index)
          };
        }),
        g: common_vendor.f(disciplineList.value, (discipline, index, i0) => {
          return common_vendor.e({
            a: common_vendor.t(discipline.code),
            b: common_vendor.t(discipline.name),
            c: common_vendor.t(expandedDisciplines.value.includes(discipline.code) ? "︿" : "﹀"),
            d: common_vendor.o(($event) => toggleDiscipline(discipline.code), index),
            e: expandedDisciplines.value.includes(discipline.code)
          }, expandedDisciplines.value.includes(discipline.code) ? {
            f: common_vendor.f(discipline.majors, (major, idx, i1) => {
              return {
                a: common_vendor.t(major.code),
                b: common_vendor.t(major.name),
                c: idx,
                d: common_vendor.o(($event) => goToMajorDetail(major), idx)
              };
            })
          } : {}, {
            g: index
          });
        }),
        h: loading.value
      }, loading.value ? {} : {}, {
        i: !loading.value && disciplineList.value.length === 0
      }, !loading.value && disciplineList.value.length === 0 ? {} : {});
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-dccd4508"]]);
wx.createPage(MiniProgramPage);
