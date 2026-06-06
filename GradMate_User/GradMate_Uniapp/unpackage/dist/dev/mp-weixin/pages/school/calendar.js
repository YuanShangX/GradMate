"use strict";
const common_vendor = require("../../common/vendor.js");
const _sfc_main = {
  __name: "calendar",
  setup(__props) {
    const currentTab = common_vendor.ref(0);
    const tabs = ["时间线", "考试科目", "调剂时间线"];
    const timelineData = common_vendor.ref([
      {
        year: "2025年",
        items: [
          { title: "2026届考研初试择校", desc: "建议7月之前完成", link: "智能择校" },
          { title: "2026届考研大纲发布", desc: "9月中旬", link: "大纲解读" },
          { title: "2026届院校招生简章发布", desc: "9-10月" },
          { title: "2026届考研预报名", desc: "9月24-27日" },
          { title: "2026届考研正式报名", desc: "10月5-25日" },
          { title: "2026届考研网上确认", desc: "11月初" },
          { title: "2026届考研准考证打印", desc: "12月中旬" },
          { title: "2026届考研初试", desc: "12月20-21日" }
        ]
      },
      {
        year: "2026年",
        items: [
          { title: "2026届考研初试成绩查询", desc: "2月中旬" },
          { title: "2026届国家线公布", desc: "3月中旬" },
          { title: "2026届考研复试", desc: "3-4月" },
          { title: "2026届考研调剂", desc: "3-4月", link: "调剂常识" },
          { title: "2026届拟录取公示", desc: "5月" },
          { title: "2026届录取通知书发放", desc: "6-7月" }
        ]
      }
    ]);
    const subjects = common_vendor.ref([
      { name: "政治/管综", date: "12月20日", time: "8:30—11:30" },
      { name: "外国语", date: "12月20日", time: "14:00—17:00" },
      { name: "业务课一/专业基础综合", date: "12月21日", time: "8:30—11:30" },
      { name: "业务课二", date: "12月21日", time: "14:00—17:00" }
    ]);
    const adjustData = common_vendor.ref([
      { title: "2025届考研初试估分", desc: "初试结束后即可进行" },
      { title: "2025届考研初试成绩查询", desc: "2月24日" },
      { title: "2025届国家线公布", desc: "2月24日" },
      { title: "2025届部分院校预调剂", desc: "预计3月开始，请关注院校招生网站", link: "调剂常识" },
      { title: "2025届调剂意向采集系统开放", desc: "3月28日", link: "历年调剂信息" },
      { title: "2025届正式调剂系统开放", desc: "4月8号" }
    ]);
    const switchTab = (index) => {
      currentTab.value = index;
    };
    const goBack = () => {
      common_vendor.index.navigateBack();
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.o(goBack),
        b: common_vendor.f(tabs, (tab, index, i0) => {
          return common_vendor.e({
            a: common_vendor.t(tab),
            b: currentTab.value === index
          }, currentTab.value === index ? {} : {}, {
            c: index,
            d: currentTab.value === index ? 1 : "",
            e: common_vendor.o(($event) => switchTab(index), index)
          });
        }),
        c: currentTab.value === 0
      }, currentTab.value === 0 ? {
        d: common_vendor.f(timelineData.value, (year, yearIndex, i0) => {
          return {
            a: common_vendor.t(year.year),
            b: common_vendor.f(year.items, (item, itemIndex, i1) => {
              return common_vendor.e({
                a: itemIndex < year.items.length - 1
              }, itemIndex < year.items.length - 1 ? {} : {}, {
                b: common_vendor.t(item.title),
                c: item.link
              }, item.link ? {
                d: common_vendor.t(item.link)
              } : {}, {
                e: common_vendor.t(item.desc),
                f: itemIndex
              });
            }),
            c: yearIndex
          };
        })
      } : {}, {
        e: currentTab.value === 1
      }, currentTab.value === 1 ? {
        f: common_vendor.f(subjects.value, (subject, index, i0) => {
          return {
            a: common_vendor.t(subject.name),
            b: common_vendor.t(subject.date),
            c: common_vendor.t(subject.time),
            d: index
          };
        })
      } : {}, {
        g: currentTab.value === 2
      }, currentTab.value === 2 ? {
        h: common_vendor.f(adjustData.value, (item, index, i0) => {
          return common_vendor.e({
            a: index < adjustData.value.length - 1
          }, index < adjustData.value.length - 1 ? {} : {}, {
            b: common_vendor.t(item.title),
            c: item.link
          }, item.link ? {
            d: common_vendor.t(item.link)
          } : {}, {
            e: common_vendor.t(item.desc),
            f: index
          });
        })
      } : {});
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-dd6d9ddf"]]);
wx.createPage(MiniProgramPage);
