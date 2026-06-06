"use strict";
const common_vendor = require("../common/vendor.js");
const BASE_URL = "http://127.0.0.1:6062/api";
function buildApiUrl(path = "") {
  if (!path)
    return BASE_URL;
  return path.startsWith("/") ? `${BASE_URL}${path}` : `${BASE_URL}/${path}`;
}
function request(options) {
  return new Promise((resolve, reject) => {
    if (options.loading !== false) {
      common_vendor.index.showLoading({
        title: "加载中...",
        mask: true
      });
    }
    common_vendor.index.request({
      url: buildApiUrl(options.url),
      method: options.method || "GET",
      data: options.data || {},
      header: {
        "Content-Type": "application/json",
        // 如果有token，添加到请求头
        "Authorization": common_vendor.index.getStorageSync("token") || ""
      },
      success: (res) => {
        if (options.loading !== false) {
          common_vendor.index.hideLoading();
        }
        console.log("API响应:", options.url, res);
        if (res.statusCode === 200) {
          if (res.data.code === 200) {
            resolve(res.data);
          } else {
            console.error("业务错误:", res.data);
            if (options.showError !== false) {
              common_vendor.index.showToast({
                title: res.data.message || "请求失败",
                icon: "none",
                duration: 2e3
              });
            }
            reject(res.data);
          }
        } else {
          console.error("HTTP错误:", res.statusCode, res);
          if (options.showError !== false) {
            common_vendor.index.showToast({
              title: `请求失败(${res.statusCode})`,
              icon: "none",
              duration: 2e3
            });
          }
          reject(res);
        }
      },
      fail: (err) => {
        if (options.loading !== false) {
          common_vendor.index.hideLoading();
        }
        console.error("请求失败:", err);
        if (options.showError !== false) {
          common_vendor.index.showToast({
            title: "网络连接失败，请检查后端服务",
            icon: "none",
            duration: 2e3
          });
        }
        reject(err);
      }
    });
  });
}
function get(url, data = {}, loading = true) {
  return request({
    url,
    method: "GET",
    data,
    loading
  });
}
function post(url, data = {}, loading = true) {
  return request({
    url,
    method: "POST",
    data,
    loading
  });
}
function put(url, data = {}, loading = true) {
  return request({
    url,
    method: "PUT",
    data,
    loading
  });
}
function del(url, data = {}, loading = true) {
  return request({
    url,
    method: "DELETE",
    data,
    loading
  });
}
exports.buildApiUrl = buildApiUrl;
exports.del = del;
exports.get = get;
exports.post = post;
exports.put = put;
