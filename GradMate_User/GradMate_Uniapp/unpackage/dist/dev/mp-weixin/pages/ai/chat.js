"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
const common_assets = require("../../common/assets.js");
const MarkdownText = () => "../../components/MarkdownText.js";
const _sfc_main = {
  components: {
    MarkdownText
  },
  data() {
    return {
      statusBarHeight: 0,
      userId: 1,
      sessions: [],
      currentSessionId: null,
      currentSessionTitle: "研小伴",
      messages: [],
      inputText: "",
      loading: false,
      scrollIntoView: "",
      showSidebar: false,
      showRename: false,
      renameTitle: "",
      renameSessionId: null,
      quickQuestions: [
        "如何选择考研院校?",
        "考研英语怎么复习?",
        "高数极限怎么求?",
        "我感觉压力好大..."
      ],
      typingTimer: null,
      typingSpeed: 30
      // 打字速度（毫秒/字）
    };
  },
  onLoad() {
    const systemInfo = common_vendor.index.getSystemInfoSync();
    this.statusBarHeight = systemInfo.statusBarHeight || 0;
    this.userId = common_vendor.index.getStorageSync("userId") || 1;
    this.loadSessions();
  },
  onUnload() {
    if (this.typingTimer) {
      clearInterval(this.typingTimer);
      this.typingTimer = null;
    }
  },
  methods: {
    async loadSessions() {
      try {
        const res = await utils_request.get(`/ai/session/list?userId=${this.userId}`, {}, false);
        if (res.code === 200) {
          this.sessions = res.data;
          if (this.sessions.length > 0 && !this.currentSessionId) {
            this.currentSessionId = this.sessions[0].sessionId;
            this.currentSessionTitle = this.sessions[0].title;
            await this.loadMessages();
          }
        }
      } catch (error) {
        console.error("加载会话列表失败:", error);
      }
    },
    async loadMessages() {
      if (!this.currentSessionId) {
        this.messages = [];
        return;
      }
      try {
        const res = await utils_request.get(`/ai/session/${this.currentSessionId}/messages`, {}, false);
        if (res.code === 200 && res.data) {
          this.messages = res.data.map((msg) => {
            let role = msg.role;
            if (role === "assistant") {
              role = "ai";
            }
            return {
              role,
              content: msg.content
            };
          });
          console.log("加载的历史消息:", this.messages);
          this.messages.forEach((msg, index) => {
            console.log(`消息${index}: role="${msg.role}", content长度=${msg.content.length}`);
          });
          this.scrollToBottom();
        }
      } catch (error) {
        console.error("加载历史消息失败:", error);
        this.messages = [];
      }
    },
    async createNewSession() {
      try {
        const res = await utils_request.post("/ai/session/create", {
          userId: this.userId
        });
        if (res.code === 200) {
          const newSession = {
            sessionId: res.data.sessionId,
            title: res.data.title,
            messageCount: 0,
            createTime: res.data.createTime,
            updateTime: res.data.createTime
          };
          this.sessions.unshift(newSession);
          this.currentSessionId = newSession.sessionId;
          this.currentSessionTitle = newSession.title;
          this.messages = [];
          this.showSidebar = false;
          common_vendor.index.showToast({
            title: "新对话已创建",
            icon: "success"
          });
        }
      } catch (error) {
        console.error("创建会话失败:", error);
        common_vendor.index.showToast({
          title: "创建失败",
          icon: "none"
        });
      }
    },
    async switchSession(sessionId) {
      if (this.currentSessionId === sessionId) {
        this.showSidebar = false;
        return;
      }
      this.currentSessionId = sessionId;
      const session = this.sessions.find((s) => s.sessionId === sessionId);
      if (session) {
        this.currentSessionTitle = session.title;
      }
      this.messages = [];
      this.showSidebar = false;
      await this.loadMessages();
    },
    showRenameDialog(session) {
      this.renameSessionId = session.sessionId;
      this.renameTitle = session.title;
      this.showRename = true;
    },
    async confirmRename() {
      if (!this.renameTitle.trim()) {
        common_vendor.index.showToast({
          title: "标题不能为空",
          icon: "none"
        });
        return;
      }
      try {
        const res = await utils_request.put(`/ai/session/${this.renameSessionId}/title`, {
          title: this.renameTitle
        });
        if (res.code === 200) {
          const session = this.sessions.find((s) => s.sessionId === this.renameSessionId);
          if (session) {
            session.title = this.renameTitle;
          }
          if (this.currentSessionId === this.renameSessionId) {
            this.currentSessionTitle = this.renameTitle;
          }
          this.showRename = false;
          common_vendor.index.showToast({
            title: "重命名成功",
            icon: "success"
          });
        }
      } catch (error) {
        console.error("重命名失败:", error);
        common_vendor.index.showToast({
          title: "重命名失败",
          icon: "none"
        });
      }
    },
    deleteSessionConfirm(sessionId) {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要删除这个对话吗?",
        success: async (res) => {
          if (res.confirm) {
            await this.deleteSession(sessionId);
          }
        }
      });
    },
    async deleteSession(sessionId) {
      try {
        const res = await utils_request.del(`/ai/session/${sessionId}`);
        if (res.code === 200) {
          this.sessions = this.sessions.filter((s) => s.sessionId !== sessionId);
          if (this.currentSessionId === sessionId) {
            if (this.sessions.length > 0) {
              this.currentSessionId = this.sessions[0].sessionId;
              this.currentSessionTitle = this.sessions[0].title;
              await this.loadMessages();
            } else {
              await this.createNewSession();
            }
          }
          common_vendor.index.showToast({
            title: "已删除",
            icon: "success"
          });
        }
      } catch (error) {
        console.error("删除会话失败:", error);
        common_vendor.index.showToast({
          title: "删除失败",
          icon: "none"
        });
      }
    },
    async sendMessage(e) {
      if (e && e.preventDefault) {
        e.preventDefault();
      }
      if (!this.inputText.trim() || this.loading)
        return;
      if (!this.currentSessionId) {
        await this.createNewSession();
      }
      const userMessage = this.inputText.trim();
      this.inputText = "";
      this.messages.push({
        role: "user",
        content: userMessage
      });
      this.scrollToBottom();
      this.loading = true;
      try {
        console.log("发送消息:", {
          userId: this.userId,
          sessionId: this.currentSessionId,
          message: userMessage
        });
        const res = await utils_request.post("/ai/chat", {
          userId: this.userId,
          sessionId: this.currentSessionId,
          message: userMessage
        }, false);
        console.log("收到响应:", res);
        if (res.code === 200) {
          console.log("AI回复内容:", res.data);
          const aiMessageIndex = this.messages.length;
          this.messages.push({
            role: "ai",
            content: ""
          });
          this.typewriterEffect(res.data, aiMessageIndex);
          this.loadSessions();
        } else {
          console.error("业务错误:", res.message);
          this.messages.push({
            role: "ai",
            content: res.message || "抱歉,我现在有点累了,请稍后再试~"
          });
        }
      } catch (error) {
        console.error("发送消息失败:", error);
        this.messages.push({
          role: "ai",
          content: "网络连接失败,请检查网络后重试。"
        });
        this.loading = false;
      }
      return false;
    },
    sendQuickQuestion(question) {
      this.inputText = question;
      this.sendMessage();
    },
    scrollToBottom() {
      this.$nextTick(() => {
        this.scrollIntoView = "msg-" + (this.messages.length - 1);
      });
    },
    // 打字机效果
    typewriterEffect(fullText, messageIndex) {
      if (this.typingTimer) {
        clearInterval(this.typingTimer);
      }
      let currentIndex = 0;
      const textLength = fullText.length;
      this.typingTimer = setInterval(() => {
        if (currentIndex < textLength) {
          this.messages[messageIndex].content = fullText.substring(0, currentIndex + 1);
          currentIndex++;
          this.scrollToBottom();
        } else {
          clearInterval(this.typingTimer);
          this.typingTimer = null;
          this.loading = false;
        }
      }, this.typingSpeed);
    }
  }
};
if (!Array) {
  const _component_MarkdownText = common_vendor.resolveComponent("MarkdownText");
  _component_MarkdownText();
}
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $data.statusBarHeight + "px",
    b: common_vendor.o(($event) => $data.showSidebar = !$data.showSidebar),
    c: common_vendor.t($data.currentSessionTitle),
    d: common_vendor.o((...args) => $options.createNewSession && $options.createNewSession(...args)),
    e: $data.messages.length === 0
  }, $data.messages.length === 0 ? {
    f: common_assets._imports_0$2,
    g: common_vendor.f($data.quickQuestions, (q, index, i0) => {
      return {
        a: common_vendor.t(q),
        b: index,
        c: common_vendor.o(($event) => $options.sendQuickQuestion(q), index)
      };
    })
  } : {}, {
    h: common_vendor.f($data.messages, (msg, index, i0) => {
      return common_vendor.e({
        a: msg.role === "ai"
      }, msg.role === "ai" ? {
        b: "c4ce47e4-0-" + i0,
        c: common_vendor.p({
          content: msg.content
        })
      } : {
        d: common_vendor.t(msg.content)
      }, {
        e: common_vendor.n(msg.role),
        f: index,
        g: "msg-" + index,
        h: common_vendor.n(msg.role)
      });
    }),
    i: $data.loading
  }, $data.loading ? {} : {}, {
    j: $data.scrollIntoView,
    k: $data.loading,
    l: common_vendor.o((...args) => $options.sendMessage && $options.sendMessage(...args)),
    m: $data.inputText,
    n: common_vendor.o(($event) => $data.inputText = $event.detail.value),
    o: !$data.inputText.trim() || $data.loading ? 1 : "",
    p: common_vendor.o((...args) => $options.sendMessage && $options.sendMessage(...args)),
    q: common_vendor.o(($event) => $data.showSidebar = false),
    r: common_vendor.f($data.sessions, (session, k0, i0) => {
      return {
        a: common_vendor.t(session.title),
        b: common_vendor.t(session.messageCount),
        c: common_vendor.o(($event) => $options.showRenameDialog(session), session.sessionId),
        d: common_vendor.o(($event) => $options.deleteSessionConfirm(session.sessionId), session.sessionId),
        e: session.sessionId,
        f: $data.currentSessionId === session.sessionId ? 1 : "",
        g: common_vendor.o(($event) => $options.switchSession(session.sessionId), session.sessionId)
      };
    }),
    s: $data.showSidebar ? 1 : "",
    t: $data.showSidebar
  }, $data.showSidebar ? {
    v: common_vendor.o(($event) => $data.showSidebar = false)
  } : {}, {
    w: $data.showRename
  }, $data.showRename ? {
    x: $data.renameTitle,
    y: common_vendor.o(($event) => $data.renameTitle = $event.detail.value),
    z: common_vendor.o(($event) => $data.showRename = false),
    A: common_vendor.o((...args) => $options.confirmRename && $options.confirmRename(...args)),
    B: common_vendor.o(() => {
    }),
    C: common_vendor.o(($event) => $data.showRename = false)
  } : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-c4ce47e4"]]);
wx.createPage(MiniProgramPage);
