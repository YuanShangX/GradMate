"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
const _sfc_main = {
  __name: "assistant",
  setup(__props) {
    const currentStep = common_vendor.ref(1);
    const messages = common_vendor.ref([]);
    const userInput = common_vendor.ref("");
    const loading = common_vendor.ref(false);
    const scrollIntoView = common_vendor.ref("");
    const userProfile = common_vendor.ref({
      school: "",
      major: "",
      majorLevel: "",
      englishLevel: "",
      mathLevel: "",
      targetMajor: "",
      targetSchoolType: ""
    });
    const inputPlaceholder = common_vendor.computed(() => {
      if (currentStep.value === 1) {
        return "点击输入本科/专科院校名称";
      } else if (currentStep.value === 2) {
        return "输入你的专业倾向";
      } else {
        return "输入你的学校倾向";
      }
    });
    const initChat = () => {
      messages.value = [
        {
          role: "assistant",
          content: "Hi，我是你的AI择校小助手~接下来我将和你一起完成择校测评，时长约5分钟，测评结束后即可领取你的专属择校报告啦！\n现在就让我们一起开始吧~"
        },
        {
          role: "assistant",
          content: "Part1 基础信息采集\n\n请填写你的本科/专科院校，让小助手更了解你一点~"
        }
      ];
    };
    const sendMessage = async () => {
      if (!userInput.value.trim() || loading.value)
        return;
      const message = userInput.value.trim();
      messages.value.push({
        role: "user",
        content: message
      });
      userInput.value = "";
      scrollToBottom();
      await handleUserInput(message);
    };
    const handleUserInput = async (input) => {
      loading.value = true;
      try {
        if (currentStep.value === 1) {
          await handleStep1(input);
        } else if (currentStep.value === 2) {
          await handleStep2(input);
        } else if (currentStep.value === 3) {
          await handleStep3(input);
        }
      } catch (error) {
        console.error("处理失败:", error);
        messages.value.push({
          role: "assistant",
          content: "抱歉，处理出错了，请重试~"
        });
      } finally {
        loading.value = false;
        scrollToBottom();
      }
    };
    const handleStep1 = async (input) => {
      const res = await common_vendor.index.request({
        url: utils_request.buildApiUrl("/ai/assistant/step1"),
        method: "POST",
        data: {
          userInput: input,
          profile: userProfile.value
        },
        header: {
          "Content-Type": "application/json"
        }
      });
      if (res.data.code === 200) {
        const response = res.data.data;
        if (response.profile) {
          Object.assign(userProfile.value, response.profile);
        }
        messages.value.push({
          role: "assistant",
          content: response.message
        });
        if (response.nextStep) {
          currentStep.value = 2;
        }
      }
    };
    const handleStep2 = async (input) => {
      const res = await common_vendor.index.request({
        url: utils_request.buildApiUrl("/ai/assistant/step2"),
        method: "POST",
        data: {
          userInput: input,
          profile: userProfile.value
        },
        header: {
          "Content-Type": "application/json"
        }
      });
      if (res.data.code === 200) {
        const response = res.data.data;
        if (response.profile) {
          Object.assign(userProfile.value, response.profile);
        }
        messages.value.push({
          role: "assistant",
          content: response.message
        });
        if (response.nextStep) {
          currentStep.value = 3;
        }
      }
    };
    const handleStep3 = async (input) => {
      const res = await common_vendor.index.request({
        url: utils_request.buildApiUrl("/ai/assistant/step3"),
        method: "POST",
        data: {
          userInput: input,
          profile: userProfile.value
        },
        header: {
          "Content-Type": "application/json"
        }
      });
      if (res.data.code === 200) {
        const response = res.data.data;
        messages.value.push({
          role: "assistant",
          content: response.message
        });
        if (response.completed) {
          messages.value.push({
            role: "assistant",
            content: "🎉 测评完成！\n\n" + response.report
          });
        }
      }
    };
    const scrollToBottom = () => {
      common_vendor.nextTick$1(() => {
        const lastIndex = messages.value.length - 1;
        scrollIntoView.value = "msg-" + lastIndex;
      });
    };
    const goBack = () => {
      common_vendor.index.navigateBack();
    };
    initChat();
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.o(goBack),
        b: currentStep.value >= 1 ? 1 : "",
        c: currentStep.value >= 2 ? 1 : "",
        d: currentStep.value >= 3 ? 1 : "",
        e: common_vendor.f(messages.value, (msg, index, i0) => {
          return common_vendor.e({
            a: msg.role === "assistant"
          }, msg.role === "assistant" ? {
            b: common_vendor.t(msg.content)
          } : {
            c: common_vendor.t(msg.content)
          }, {
            d: index,
            e: "msg-" + index,
            f: msg.role === "user" ? 1 : ""
          });
        }),
        f: loading.value
      }, loading.value ? {} : {}, {
        g: scrollIntoView.value,
        h: inputPlaceholder.value,
        i: common_vendor.o(sendMessage),
        j: userInput.value,
        k: common_vendor.o(($event) => userInput.value = $event.detail.value),
        l: common_vendor.o(sendMessage)
      });
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-a7d40b14"]]);
wx.createPage(MiniProgramPage);
