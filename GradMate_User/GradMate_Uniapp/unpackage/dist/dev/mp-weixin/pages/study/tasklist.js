"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
const _sfc_main = {
  __name: "tasklist",
  setup(__props) {
    const userId = common_vendor.ref(1);
    const tasks = common_vendor.ref([]);
    const userTags = common_vendor.ref([]);
    const filteredTasks = common_vendor.ref([]);
    const searchKeyword = common_vendor.ref("");
    const selectedStatus = common_vendor.ref("");
    const selectedPriority = common_vendor.ref("");
    const selectedTag = common_vendor.ref("");
    const showStatusPicker = common_vendor.ref(false);
    const showPriorityPicker = common_vendor.ref(false);
    const showTagPicker = common_vendor.ref(false);
    const showAdd = common_vendor.ref(false);
    const loading = common_vendor.ref(false);
    const editingTask = common_vendor.ref(null);
    const newTask = common_vendor.ref({
      taskContent: "",
      description: "",
      estimatedDuration: 25,
      priority: 2,
      tags: ""
    });
    const statusList = common_vendor.ref([
      { label: "全部", value: "" },
      { label: "待完成", value: 0 },
      { label: "已完成", value: 1 }
    ]);
    const priorityList = common_vendor.ref([
      { label: "全部", value: "" },
      { label: "低", value: 1 },
      { label: "中", value: 2 },
      { label: "高", value: 3 }
    ]);
    const durations = [10, 15, 25, 30, 45, 60, 90, 120];
    const selectedStatusLabel = common_vendor.computed(() => {
      if (selectedStatus.value === "")
        return "";
      const status = statusList.value.find((item) => item.value === selectedStatus.value);
      return status ? status.label : "";
    });
    const selectedPriorityLabel = common_vendor.computed(() => {
      if (selectedPriority.value === "")
        return "";
      const priority = priorityList.value.find((item) => item.value === selectedPriority.value);
      return priority ? priority.label : "";
    });
    const selectedTagLabel = common_vendor.computed(() => {
      return selectedTag.value || "";
    });
    common_vendor.onMounted(() => {
      userId.value = common_vendor.index.getStorageSync("userId") || 1;
      loadTasks();
      loadTags();
    });
    const loadTasks = async () => {
      loading.value = true;
      try {
        const res = await utils_request.get(`/study/task/today?userId=${userId.value}`);
        if (res.code === 200) {
          tasks.value = res.data || [];
          applyFilters();
        }
      } catch (error) {
        console.error("加载任务失败:", error);
      } finally {
        loading.value = false;
      }
    };
    const loadTags = async () => {
      try {
        const res = await utils_request.get(`/study/tags?userId=${userId.value}`);
        if (res.code === 200) {
          userTags.value = res.data || [];
        }
      } catch (error) {
        console.error("加载标签失败:", error);
      }
    };
    const applyFilters = () => {
      let result = [...tasks.value];
      if (searchKeyword.value) {
        const keyword = searchKeyword.value.toLowerCase();
        result = result.filter(
          (task) => task.taskContent.toLowerCase().includes(keyword) || task.description && task.description.toLowerCase().includes(keyword)
        );
      }
      if (selectedStatus.value !== "") {
        result = result.filter((task) => task.isCompleted === selectedStatus.value);
      }
      if (selectedPriority.value !== "") {
        result = result.filter((task) => task.priority === selectedPriority.value);
      }
      if (selectedTag.value) {
        result = result.filter(
          (task) => task.tags && task.tags.includes(selectedTag.value)
        );
      }
      filteredTasks.value = result;
    };
    const onSearch = () => {
      applyFilters();
    };
    const selectStatus = (status) => {
      selectedStatus.value = status;
      showStatusPicker.value = false;
      applyFilters();
    };
    const selectPriority = (priority) => {
      selectedPriority.value = priority;
      showPriorityPicker.value = false;
      applyFilters();
    };
    const selectTag = (tag) => {
      selectedTag.value = tag;
      showTagPicker.value = false;
      applyFilters();
    };
    const toggleComplete = async (task) => {
      try {
        const res = await utils_request.put(`/study/task/${task.id}/complete`);
        if (res.code === 200) {
          task.isCompleted = task.isCompleted === 1 ? 0 : 1;
          applyFilters();
        }
      } catch (error) {
        console.error("切换任务状态失败:", error);
      }
    };
    const startTask = (task) => {
      if (task.isCompleted === 1)
        return;
      common_vendor.index.navigateTo({
        url: `/pages/study/focus?taskId=${task.id}&taskName=${task.taskContent}&duration=${task.estimatedDuration || 25}`
      });
    };
    const showAddModal = () => {
      editingTask.value = null;
      newTask.value = {
        taskContent: "",
        description: "",
        estimatedDuration: 25,
        priority: 2,
        tags: ""
      };
      showAdd.value = true;
    };
    const hideAddModal = () => {
      showAdd.value = false;
    };
    const editTask = (task) => {
      editingTask.value = task;
      newTask.value = {
        taskContent: task.taskContent,
        description: task.description || "",
        estimatedDuration: task.estimatedDuration || 25,
        priority: task.priority || 2,
        tags: task.tags || ""
      };
      showAdd.value = true;
    };
    const saveTask = async () => {
      if (!newTask.value.taskContent.trim()) {
        common_vendor.index.showToast({
          title: "请输入任务内容",
          icon: "none"
        });
        return;
      }
      try {
        const date = /* @__PURE__ */ new Date();
        const taskDate = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
        const taskData = {
          userId: userId.value,
          taskContent: newTask.value.taskContent,
          description: newTask.value.description,
          taskDate,
          priority: newTask.value.priority,
          estimatedDuration: newTask.value.estimatedDuration,
          tags: newTask.value.tags
        };
        if (editingTask.value) {
          const res = await utils_request.put(`/study/task/${editingTask.value.id}`, taskData);
          if (res.code === 200) {
            common_vendor.index.showToast({
              title: "更新成功",
              icon: "success"
            });
          }
        } else {
          const res = await utils_request.post("/study/task", taskData);
          if (res.code === 200) {
            common_vendor.index.showToast({
              title: "添加成功",
              icon: "success"
            });
          }
        }
        hideAddModal();
        loadTasks();
      } catch (error) {
        console.error("保存任务失败:", error);
      }
    };
    const onDurationChange = (e) => {
      newTask.value.estimatedDuration = durations[e.detail.value];
    };
    const onPriorityChange = (e) => {
      newTask.value.priority = [1, 2, 3][e.detail.value];
    };
    const getPriorityText = (priority) => {
      const map = { 1: "低", 2: "中", 3: "高" };
      return map[priority] || "中";
    };
    const loadMore = () => {
    };
    const goBack = () => {
      common_vendor.index.navigateBack();
    };
    const goToCalendar = () => {
      common_vendor.index.navigateTo({
        url: "/pages/study/calendar"
      });
    };
    const goToStats = () => {
      common_vendor.index.navigateTo({
        url: "/pages/study/stats"
      });
    };
    const goToFocus = () => {
      common_vendor.index.navigateTo({
        url: "/pages/study/focus"
      });
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.o(goBack),
        b: common_vendor.o([($event) => searchKeyword.value = $event.detail.value, onSearch]),
        c: searchKeyword.value,
        d: common_vendor.o(goToCalendar),
        e: common_vendor.o(goToStats),
        f: common_vendor.o(goToFocus),
        g: common_vendor.t(selectedStatusLabel.value || "任务状态"),
        h: common_vendor.o(($event) => showStatusPicker.value = true),
        i: common_vendor.t(selectedPriorityLabel.value || "优先级"),
        j: common_vendor.o(($event) => showPriorityPicker.value = true),
        k: common_vendor.t(selectedTagLabel.value || "标签"),
        l: common_vendor.o(($event) => showTagPicker.value = true),
        m: common_vendor.f(filteredTasks.value, (task, k0, i0) => {
          return common_vendor.e({
            a: task.isCompleted === 1
          }, task.isCompleted === 1 ? {} : {}, {
            b: common_vendor.o(($event) => toggleComplete(task), task.id),
            c: common_vendor.t(task.taskContent),
            d: task.isCompleted === 1 ? 1 : "",
            e: task.description
          }, task.description ? {
            f: common_vendor.t(task.description)
          } : {}, {
            g: task.tags
          }, task.tags ? {
            h: common_vendor.f(task.tags.split(","), (tag, index, i1) => {
              return {
                a: common_vendor.t(tag),
                b: index
              };
            })
          } : {}, {
            i: common_vendor.t(task.estimatedDuration || 25),
            j: common_vendor.t(getPriorityText(task.priority)),
            k: common_vendor.n("priority-" + task.priority),
            l: common_vendor.o(($event) => editTask(task), task.id),
            m: common_vendor.t(task.isCompleted === 1 ? "已完成" : "开始"),
            n: common_vendor.o(($event) => startTask(task), task.id),
            o: task.id
          });
        }),
        n: filteredTasks.value.length === 0
      }, filteredTasks.value.length === 0 ? {} : {}, {
        o: !loading.value && filteredTasks.value.length > 0
      }, !loading.value && filteredTasks.value.length > 0 ? {} : {}, {
        p: common_vendor.o(loadMore),
        q: common_vendor.o(showAddModal),
        r: showStatusPicker.value
      }, showStatusPicker.value ? {
        s: common_vendor.o(($event) => showStatusPicker.value = false),
        t: common_vendor.f(statusList.value, (item, index, i0) => {
          return {
            a: common_vendor.t(item.label),
            b: index,
            c: common_vendor.o(($event) => selectStatus(item.value), index)
          };
        }),
        v: common_vendor.o(($event) => selectStatus("")),
        w: common_vendor.o(($event) => showStatusPicker.value = false),
        x: common_vendor.o(() => {
        }),
        y: common_vendor.o(($event) => showStatusPicker.value = false)
      } : {}, {
        z: showPriorityPicker.value
      }, showPriorityPicker.value ? {
        A: common_vendor.o(($event) => showPriorityPicker.value = false),
        B: common_vendor.f(priorityList.value, (item, index, i0) => {
          return {
            a: common_vendor.t(item.label),
            b: index,
            c: common_vendor.o(($event) => selectPriority(item.value), index)
          };
        }),
        C: common_vendor.o(($event) => selectPriority("")),
        D: common_vendor.o(($event) => showPriorityPicker.value = false),
        E: common_vendor.o(() => {
        }),
        F: common_vendor.o(($event) => showPriorityPicker.value = false)
      } : {}, {
        G: showTagPicker.value
      }, showTagPicker.value ? {
        H: common_vendor.o(($event) => showTagPicker.value = false),
        I: common_vendor.f(userTags.value, (tag, k0, i0) => {
          return {
            a: common_vendor.t(tag.tagName),
            b: tag.id,
            c: common_vendor.o(($event) => selectTag(tag.tagName), tag.id)
          };
        }),
        J: common_vendor.o(($event) => selectTag("")),
        K: common_vendor.o(($event) => showTagPicker.value = false),
        L: common_vendor.o(() => {
        }),
        M: common_vendor.o(($event) => showTagPicker.value = false)
      } : {}, {
        N: showAdd.value
      }, showAdd.value ? {
        O: common_vendor.o(hideAddModal),
        P: common_vendor.t(editingTask.value ? "编辑任务" : "新建任务"),
        Q: common_vendor.o(hideAddModal),
        R: newTask.value.taskContent,
        S: common_vendor.o(($event) => newTask.value.taskContent = $event.detail.value),
        T: newTask.value.description,
        U: common_vendor.o(($event) => newTask.value.description = $event.detail.value),
        V: common_vendor.t(newTask.value.estimatedDuration),
        W: durations,
        X: common_vendor.o(onDurationChange),
        Y: common_vendor.t(getPriorityText(newTask.value.priority)),
        Z: ["低", "中", "高"],
        aa: common_vendor.o(onPriorityChange),
        ab: common_vendor.o(saveTask)
      } : {});
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-80c341a7"]]);
wx.createPage(MiniProgramPage);
