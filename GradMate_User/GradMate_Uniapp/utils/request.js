/**
 * HTTP请求封装工具
 */

// 后端API基础地址
//export const BASE_URL = 'http://192.168.167.3:9090/api';
export const BASE_URL = 'http://127.0.0.1:6062/api';
/**
 * 构建完整API地址
 * @param {String} path 接口路径（支持 /xxx 或 xxx）
 * @returns {String}
 */
export function buildApiUrl(path = '') {
	if (!path) return BASE_URL;
	return path.startsWith('/') ? `${BASE_URL}${path}` : `${BASE_URL}/${path}`;
}

/**
 * 发起HTTP请求s
 * @param {Object} options 请求配置
 * @returns {Promise}
 */
function request(options) {
	return new Promise((resolve, reject) => {
		// 显示加载提示
		if (options.loading !== false) {
			uni.showLoading({
				title: '加载中...',
				mask: true
			});
		}

		uni.request({
			url: buildApiUrl(options.url),
			method: options.method || 'GET',
			data: options.data || {},
			header: {
				'Content-Type': 'application/json',
				// 如果有token，添加到请求头
				'Authorization': uni.getStorageSync('token') || ''
			},
			success: (res) => {
				// 隐藏加载提示
				if (options.loading !== false) {
					uni.hideLoading();
				}

				console.log('API响应:', options.url, res);

				// 请求成功
				if (res.statusCode === 200) {
					// 后端返回的统一格式：{code, message, data}
					if (res.data.code === 200) {
						resolve(res.data);
					} else {
						// 业务错误
						console.error('业务错误:', res.data);
						if (options.showError !== false) {
							uni.showToast({
								title: res.data.message || '请求失败',
								icon: 'none',
								duration: 2000
							});
						}
						reject(res.data);
					}
				} else {
					// HTTP错误
					console.error('HTTP错误:', res.statusCode, res);
					if (options.showError !== false) {
						uni.showToast({
							title: `请求失败(${res.statusCode})`,
							icon: 'none',
							duration: 2000
						});
					}
					reject(res);
				}
			},
			fail: (err) => {
				// 隐藏加载提示
				if (options.loading !== false) {
					uni.hideLoading();
				}

				// 请求失败
				console.error('请求失败:', err);
				if (options.showError !== false) {
					uni.showToast({
						title: '网络连接失败，请检查后端服务',
						icon: 'none',
						duration: 2000
					});
				}
				reject(err);
			}
		});
	});
}

/**
 * GET请求
 */
export function get(url, data = {}, loading = true) {
	return request({
		url,
		method: 'GET',
		data,
		loading
	});
}

/**
 * POST请求
 */
export function post(url, data = {}, loading = true) {
	return request({
		url,
		method: 'POST',
		data,
		loading
	});
}

/**
 * PUT请求
 */
export function put(url, data = {}, loading = true) {
	return request({
		url,
		method: 'PUT',
		data,
		loading
	});
}

/**
 * DELETE请求
 */
export function del(url, data = {}, loading = true) {
	return request({
		url,
		method: 'DELETE',
		data,
		loading
	});
}

export default {
	BASE_URL,
	buildApiUrl,
	get,
	post,
	put,
	del,
	request
};
