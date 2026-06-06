"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
const _sfc_main = {
  __name: "detail",
  setup(__props) {
    const majorCode = common_vendor.ref("");
    const majorName = common_vendor.ref("");
    const selectedLocation = common_vendor.ref("");
    const selectedTag = common_vendor.ref("");
    const schoolList = common_vendor.ref([]);
    const loading = common_vendor.ref(false);
    const showLocationPicker = common_vendor.ref(false);
    const showTagPicker = common_vendor.ref(false);
    const selectedRegion = common_vendor.ref(1);
    const selectedTagLabel = common_vendor.computed(() => {
      if (!selectedTag.value)
        return "";
      const tag = tagList.value.find((item) => item.value === selectedTag.value);
      return tag ? tag.label : "";
    });
    const region1List = common_vendor.ref([
      { name: "北京" },
      { name: "天津" },
      { name: "河北" },
      { name: "山西" },
      { name: "辽宁" },
      { name: "吉林" },
      { name: "黑龙江" },
      { name: "上海" },
      { name: "江苏" },
      { name: "浙江" },
      { name: "安徽" },
      { name: "福建" },
      { name: "江西" },
      { name: "山东" },
      { name: "河南" },
      { name: "湖北" },
      { name: "湖南" },
      { name: "广东" },
      { name: "重庆" },
      { name: "四川" },
      { name: "陕西" }
    ]);
    const region2List = common_vendor.ref([
      { name: "内蒙古" },
      { name: "广西" },
      { name: "海南" },
      { name: "贵州" },
      { name: "云南" },
      { name: "西藏" },
      { name: "甘肃" },
      { name: "青海" },
      { name: "宁夏" },
      { name: "新疆" }
    ]);
    const tagList = common_vendor.ref([
      { label: "211", value: "211" },
      { label: "双一流", value: "syl" },
      { label: "自划线", value: "zhx" }
    ]);
    common_vendor.onMounted(() => {
      const pages = getCurrentPages();
      const currentPage = pages[pages.length - 1];
      const options = currentPage.options;
      if (options.id) {
        loadMajorById(options.id);
      } else if (options.code) {
        majorCode.value = options.code || "";
        majorName.value = decodeURIComponent(options.name || "");
        loadSchoolList();
      }
    });
    const loadMajorById = async (id) => {
      loading.value = true;
      try {
        const res = await common_vendor.index.request({
          url: utils_request.buildApiUrl(`/major/${id}`),
          method: "GET"
        });
        if (res.data.code === 200 && res.data.data) {
          const major = res.data.data;
          majorCode.value = major.majorCode || "";
          majorName.value = major.majorName || "";
          loadSchoolList();
        } else {
          common_vendor.index.showToast({
            title: "专业不存在",
            icon: "none"
          });
          loading.value = false;
        }
      } catch (error) {
        console.error("加载专业详情失败:", error);
        common_vendor.index.showToast({
          title: "加载失败",
          icon: "none"
        });
        loading.value = false;
      }
    };
    const loadSchoolList = async () => {
      loading.value = true;
      try {
        const params = {
          majorCode: majorCode.value
        };
        if (selectedLocation.value)
          params.location = selectedLocation.value;
        if (selectedTag.value)
          params.schoolType = selectedTag.value;
        const res = await common_vendor.index.request({
          url: utils_request.buildApiUrl("/major/schools"),
          method: "GET",
          data: params
        });
        if (res.data.code === 200 && res.data.data) {
          schoolList.value = res.data.data;
        }
      } catch (error) {
        console.error("加载学校列表失败:", error);
        common_vendor.index.showToast({
          title: "加载失败",
          icon: "none"
        });
      } finally {
        loading.value = false;
      }
    };
    const loadSchools = async () => {
      loadSchoolList();
    };
    const selectLocation = (location) => {
      selectedLocation.value = location;
      showLocationPicker.value = false;
      loadSchools();
    };
    const selectTag = (tag) => {
      selectedTag.value = tag;
      showTagPicker.value = false;
      loadSchools();
    };
    const goToSchoolDetail = (school) => {
      common_vendor.index.navigateTo({
        url: `/pages/major/schoolDetail?schoolName=${encodeURIComponent(school.schoolName)}&majorCode=${majorCode.value}&majorName=${encodeURIComponent(majorName.value)}`
      });
    };
    const goBack = () => {
      common_vendor.index.navigateBack();
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.o(goBack),
        b: common_vendor.t(majorCode.value),
        c: common_vendor.t(majorName.value),
        d: common_vendor.t(selectedLocation.value || "所在地区"),
        e: common_vendor.o(($event) => showLocationPicker.value = true),
        f: common_vendor.t(selectedTagLabel.value || "院校标签"),
        g: common_vendor.o(($event) => showTagPicker.value = true),
        h: common_vendor.f(schoolList.value, (school, index, i0) => {
          return common_vendor.e({
            a: school.logo
          }, school.logo ? {
            b: school.logo
          } : {}, {
            c: common_vendor.t(school.schoolName),
            d: common_vendor.t(school.location),
            e: school.is211
          }, school.is211 ? {} : {}, {
            f: school.isSyl
          }, school.isSyl ? {} : {}, {
            g: school.isZhx
          }, school.isZhx ? {} : {}, {
            h: common_vendor.t(school.studyMode),
            i: common_vendor.t(school.examType),
            j: common_vendor.t(school.department),
            k: common_vendor.t(school.followCount || 106),
            l: common_vendor.t(school.viewCount || 5840),
            m: common_vendor.t(school.plannedEnrollment || "--"),
            n: common_vendor.t(school.admissionScore || "--"),
            o: index,
            p: common_vendor.o(($event) => goToSchoolDetail(school), index)
          });
        }),
        i: loading.value
      }, loading.value ? {} : {}, {
        j: !loading.value && schoolList.value.length === 0
      }, !loading.value && schoolList.value.length === 0 ? {} : {}, {
        k: showLocationPicker.value
      }, showLocationPicker.value ? common_vendor.e({
        l: common_vendor.o(($event) => showLocationPicker.value = false),
        m: selectedRegion.value === 1 ? 1 : "",
        n: common_vendor.o(($event) => selectedRegion.value = 1),
        o: selectedRegion.value === 2 ? 1 : "",
        p: common_vendor.o(($event) => selectedRegion.value = 2),
        q: selectedRegion.value === 1
      }, selectedRegion.value === 1 ? {
        r: common_vendor.f(region1List.value, (item, index, i0) => {
          return {
            a: common_vendor.t(item.name),
            b: index,
            c: common_vendor.o(($event) => selectLocation(item.name), index)
          };
        })
      } : {}, {
        s: selectedRegion.value === 2
      }, selectedRegion.value === 2 ? {
        t: common_vendor.f(region2List.value, (item, index, i0) => {
          return {
            a: common_vendor.t(item.name),
            b: index,
            c: common_vendor.o(($event) => selectLocation(item.name), index)
          };
        })
      } : {}, {
        v: common_vendor.o(($event) => selectLocation("")),
        w: common_vendor.o(($event) => showLocationPicker.value = false),
        x: common_vendor.o(() => {
        }),
        y: common_vendor.o(($event) => showLocationPicker.value = false)
      }) : {}, {
        z: showTagPicker.value
      }, showTagPicker.value ? {
        A: common_vendor.o(($event) => showTagPicker.value = false),
        B: common_vendor.f(tagList.value, (item, index, i0) => {
          return {
            a: common_vendor.t(item.label),
            b: index,
            c: common_vendor.o(($event) => selectTag(item.value), index)
          };
        }),
        C: common_vendor.o(($event) => selectTag("")),
        D: common_vendor.o(($event) => showTagPicker.value = false),
        E: common_vendor.o(() => {
        }),
        F: common_vendor.o(($event) => showTagPicker.value = false)
      } : {});
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-33508239"]]);
wx.createPage(MiniProgramPage);
