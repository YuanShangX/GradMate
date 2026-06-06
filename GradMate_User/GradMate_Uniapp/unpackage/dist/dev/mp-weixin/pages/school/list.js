"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
const _sfc_main = {
  __name: "list",
  setup(__props) {
    const searchKeyword = common_vendor.ref("");
    const selectedLocation = common_vendor.ref("");
    const selectedSchoolType = common_vendor.ref("");
    const selectedTag = common_vendor.ref("");
    const selectedTagLabel = common_vendor.computed(() => {
      if (!selectedTag.value)
        return "";
      const tag = tagList.value.find((item) => item.value === selectedTag.value);
      return tag ? tag.label : "";
    });
    const showLocationPicker = common_vendor.ref(false);
    const showSchoolTypePicker = common_vendor.ref(false);
    const showTagPicker = common_vendor.ref(false);
    const showBackTop = common_vendor.ref(false);
    const selectedRegion = common_vendor.ref(1);
    const schoolList = common_vendor.ref([]);
    const loading = common_vendor.ref(false);
    const pageNum = common_vendor.ref(1);
    const noMore = common_vendor.ref(false);
    const region1List = common_vendor.ref([
      { name: "北京", count: 148 },
      { name: "天津", count: 28 },
      { name: "河北", count: 26 },
      { name: "山西", count: 17 },
      { name: "辽宁", count: 45 },
      { name: "吉林", count: 22 },
      { name: "黑龙江", count: 31 },
      { name: "上海", count: 49 },
      { name: "江苏", count: 56 },
      { name: "浙江", count: 36 },
      { name: "安徽", count: 25 },
      { name: "福建", count: 21 },
      { name: "江西", count: 20 },
      { name: "山东", count: 41 },
      { name: "河南", count: 29 },
      { name: "湖北", count: 51 },
      { name: "湖南", count: 29 },
      { name: "广东", count: 37 },
      { name: "重庆", count: 19 },
      { name: "四川", count: 38 },
      { name: "陕西", count: 58 }
    ]);
    const region2List = common_vendor.ref([
      { name: "内蒙古", count: 13 },
      { name: "广西", count: 17 },
      { name: "海南", count: 5 },
      { name: "贵州", count: 13 },
      { name: "云南", count: 18 },
      { name: "西藏", count: 3 },
      { name: "甘肃", count: 17 },
      { name: "青海", count: 3 },
      { name: "宁夏", count: 5 },
      { name: "新疆", count: 11 }
    ]);
    common_vendor.ref([
      "北京",
      "上海",
      "天津",
      "重庆",
      "河北",
      "山西",
      "辽宁",
      "吉林",
      "黑龙江",
      "江苏",
      "浙江",
      "安徽",
      "福建",
      "江西",
      "山东",
      "河南",
      "湖北",
      "湖南",
      "广东",
      "广西",
      "海南",
      "四川",
      "贵州",
      "云南",
      "西藏",
      "陕西",
      "甘肃",
      "青海",
      "宁夏",
      "新疆",
      "内蒙古",
      "台湾",
      "香港",
      "澳门"
    ]);
    const schoolTypeList = common_vendor.ref([
      { label: "教育部", value: "教育部" },
      { label: "工业与信息化部", value: "工业与信息化部" },
      { label: "中国科学院", value: "中国科学院" },
      { label: "北京市", value: "北京市" },
      { label: "上海市", value: "上海市" },
      { label: "天津市", value: "天津市" },
      { label: "重庆市", value: "重庆市" },
      { label: "国家民族事务委员会", value: "国家民族事务委员会" },
      { label: "国家体育总局", value: "国家体育总局" },
      { label: "中央办公厅", value: "中央办公厅" }
    ]);
    const tagList = common_vendor.ref([
      { label: "211", value: "yjsy" },
      { label: "双一流", value: "syl" },
      { label: "自划线", value: "zhx" },
      { label: "普通院校", value: "normal" }
    ]);
    common_vendor.onMounted(() => {
      loadSchoolList();
    });
    const loadSchoolList = async () => {
      if (loading.value || noMore.value)
        return;
      loading.value = true;
      try {
        const params = {
          pageNum: pageNum.value,
          pageSize: 20
        };
        if (searchKeyword.value)
          params.schoolName = searchKeyword.value;
        if (selectedLocation.value)
          params.location = selectedLocation.value;
        if (selectedTag.value === "yjsy")
          params.yjsy = 1;
        if (selectedTag.value === "syl")
          params.syl = 1;
        if (selectedTag.value === "zhx")
          params.zhx = 1;
        if (selectedTag.value === "normal") {
          params.yjsy = 0;
          params.syl = 0;
          params.zhx = 0;
        }
        const res = await common_vendor.index.request({
          url: utils_request.buildApiUrl("/school/list"),
          method: "GET",
          data: params
        });
        if (res.data.code === 200 && res.data.data) {
          const records = res.data.data.records || [];
          schoolList.value = [...schoolList.value, ...records];
          if (records.length < 20) {
            noMore.value = true;
          } else {
            pageNum.value++;
          }
        }
      } catch (error) {
        console.error("加载院校列表失败:", error);
        common_vendor.index.showToast({
          title: "加载失败",
          icon: "none"
        });
      } finally {
        loading.value = false;
      }
    };
    const handleSearch = () => {
      resetList();
      loadSchoolList();
    };
    const selectLocation = (location) => {
      selectedLocation.value = location;
      showLocationPicker.value = false;
      resetList();
      loadSchoolList();
    };
    const selectSchoolType = (type) => {
      selectedSchoolType.value = type;
      showSchoolTypePicker.value = false;
      resetList();
      loadSchoolList();
    };
    const selectTag = (tag) => {
      selectedTag.value = tag;
      showTagPicker.value = false;
      resetList();
      loadSchoolList();
    };
    const resetList = () => {
      schoolList.value = [];
      pageNum.value = 1;
      noMore.value = false;
    };
    const loadMore = () => {
      loadSchoolList();
    };
    const scrollToTop = () => {
      common_vendor.index.pageScrollTo({
        scrollTop: 0,
        duration: 300
      });
    };
    const goToDetail = (school) => {
      common_vendor.index.navigateTo({
        url: `/pages/school/detail?id=${school.id}`
      });
    };
    const goBack = () => {
      common_vendor.index.navigateBack();
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.o(goBack),
        b: common_vendor.o(handleSearch),
        c: searchKeyword.value,
        d: common_vendor.o(($event) => searchKeyword.value = $event.detail.value),
        e: common_vendor.t(selectedLocation.value || "所在地区"),
        f: common_vendor.o(($event) => showLocationPicker.value = true),
        g: common_vendor.t(selectedTagLabel.value || "院校标签"),
        h: common_vendor.o(($event) => showTagPicker.value = true),
        i: common_vendor.f(schoolList.value, (school, index, i0) => {
          return common_vendor.e({
            a: school.logo
          }, school.logo ? {
            b: school.logo
          } : {}, {
            c: common_vendor.t(school.schoolName),
            d: common_vendor.t(school.location),
            e: school.yjsy === 1
          }, school.yjsy === 1 ? {} : {}, {
            f: school.syl === 1
          }, school.syl === 1 ? {} : {}, {
            g: school.zhx === 1
          }, school.zhx === 1 ? {} : {}, {
            h: common_vendor.t(school.followCount || "8214"),
            i: common_vendor.t(school.viewCount || "24.1w"),
            j: common_vendor.t(school.enrollmentCount || "3058"),
            k: common_vendor.t(school.majorCount || "142"),
            l: index,
            m: common_vendor.o(($event) => goToDetail(school), index)
          });
        }),
        j: loading.value
      }, loading.value ? {} : {}, {
        k: !loading.value && schoolList.value.length === 0
      }, !loading.value && schoolList.value.length === 0 ? {} : {}, {
        l: !loading.value && noMore.value && schoolList.value.length > 0
      }, !loading.value && noMore.value && schoolList.value.length > 0 ? {} : {}, {
        m: common_vendor.o(loadMore),
        n: showBackTop.value
      }, showBackTop.value ? {
        o: common_vendor.o(scrollToTop)
      } : {}, {
        p: showLocationPicker.value
      }, showLocationPicker.value ? common_vendor.e({
        q: common_vendor.o(($event) => showLocationPicker.value = false),
        r: selectedRegion.value === 1 ? 1 : "",
        s: common_vendor.o(($event) => selectedRegion.value = 1),
        t: selectedRegion.value === 2 ? 1 : "",
        v: common_vendor.o(($event) => selectedRegion.value = 2),
        w: selectedRegion.value === 1
      }, selectedRegion.value === 1 ? {
        x: common_vendor.f(region1List.value, (item, index, i0) => {
          return {
            a: common_vendor.t(item.name),
            b: common_vendor.t(item.count),
            c: index,
            d: common_vendor.o(($event) => selectLocation(item.name), index)
          };
        })
      } : {}, {
        y: selectedRegion.value === 2
      }, selectedRegion.value === 2 ? {
        z: common_vendor.f(region2List.value, (item, index, i0) => {
          return {
            a: common_vendor.t(item.name),
            b: common_vendor.t(item.count),
            c: index,
            d: common_vendor.o(($event) => selectLocation(item.name), index)
          };
        })
      } : {}, {
        A: common_vendor.o(($event) => selectLocation("")),
        B: common_vendor.o(($event) => showLocationPicker.value = false),
        C: common_vendor.o(() => {
        }),
        D: common_vendor.o(($event) => showLocationPicker.value = false)
      }) : {}, {
        E: showSchoolTypePicker.value
      }, showSchoolTypePicker.value ? {
        F: common_vendor.o(($event) => showSchoolTypePicker.value = false),
        G: common_vendor.f(schoolTypeList.value, (item, index, i0) => {
          return {
            a: common_vendor.t(item.label),
            b: index,
            c: common_vendor.o(($event) => selectSchoolType(item.value), index)
          };
        }),
        H: common_vendor.o(($event) => selectSchoolType("")),
        I: common_vendor.o(($event) => showSchoolTypePicker.value = false),
        J: common_vendor.o(() => {
        }),
        K: common_vendor.o(($event) => showSchoolTypePicker.value = false)
      } : {}, {
        L: showTagPicker.value
      }, showTagPicker.value ? {
        M: common_vendor.o(($event) => showTagPicker.value = false),
        N: common_vendor.f(tagList.value, (item, index, i0) => {
          return {
            a: common_vendor.t(item.label),
            b: index,
            c: common_vendor.o(($event) => selectTag(item.value), index)
          };
        }),
        O: common_vendor.o(($event) => selectTag("")),
        P: common_vendor.o(($event) => showTagPicker.value = false),
        Q: common_vendor.o(() => {
        }),
        R: common_vendor.o(($event) => showTagPicker.value = false)
      } : {});
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-9d131e0a"]]);
wx.createPage(MiniProgramPage);
