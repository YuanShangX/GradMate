# GradMate 阶段2：问题修复说明

## 修复的问题列表

### ✅ 问题1：省份下拉菜单无法关闭
**问题描述**：菜单总是固定在页面底部，无法关闭或隐藏

**修复方案**：
- 移除 `uni-popup` 组件，改用原生弹窗实现
- 添加遮罩层 `.popup-mask`，点击遮罩关闭弹窗
- 添加关闭按钮，点击关闭弹窗
- 添加滑入动画，提升用户体验

**修复文件**：
- `GradMate_Uniapp/pages/school/list.vue`
- `GradMate_Uniapp/pages/major/search.vue`

---

### ✅ 问题2：搜索交互逻辑不合理
**问题描述**：用户必须点击"搜索"按钮才能应用过滤，而不是点击省份/985后立即刷新列表

**修复方案**：
- 修改 `toggleFilter()` 方法：点击985/211标签后立即刷新列表
- 修改 `selectProvince()` 方法：选择省份后立即刷新列表
- 修改 `selectDegreeType()` 方法：选择学位类型后立即刷新列表
- 修改 `selectExamType()` 方法：选择考试方式后立即刷新列表

**用户体验改进**：
- 点击筛选条件 → 立即看到结果
- 无需额外点击"搜索"按钮
- 符合用户直觉操作习惯

**修复文件**：
- `GradMate_Uniapp/pages/school/list.vue`
- `GradMate_Uniapp/pages/major/search.vue`

---

### ✅ 问题3：搜索功能逻辑问题
**问题描述**：直接搜索学校/专业名字时，会被当前过滤条件限制（如省份选海南，但搜索"北京大学"时不显示结果）

**修复方案**：
- 修改 `handleSearch()` 方法
- 当用户输入搜索关键词时，自动重置其他筛选条件
- 优先显示搜索结果，不受省份等条件限制

**逻辑改进**：
```javascript
// 院校搜索
handleSearch() {
    // 如果有搜索关键词，重置其他筛选条件
    if (this.searchForm.schoolName && this.searchForm.schoolName.trim()) {
        this.searchForm.location = '';
        this.searchForm.is985 = null;
        this.searchForm.is211 = null;
    }
    this.searchForm.pageNum = 1;
    this.loadSchools(true);
}

// 专业搜索
handleSearch() {
    // 如果有搜索关键词，重置其他筛选条件（但保留从院校页面传来的schoolName）
    const fromSchoolPage = this.$route && this.$route.query && this.$route.query.schoolName;
    if (this.searchForm.majorName && this.searchForm.majorName.trim() && !fromSchoolPage) {
        this.searchForm.schoolName = '';
        this.searchForm.degreeType = '';
        this.searchForm.examType = '';
    }
    this.searchForm.pageNum = 1;
    this.loadMajors(true);
}
```

**修复文件**：
- `GradMate_Uniapp/pages/school/list.vue`
- `GradMate_Uniapp/pages/major/search.vue`

---

### ✅ 问题4：查看详情功能不完善
**问题描述**：
- 进入某个院校后，无法查看该院校的所有专业
- 进入某个专业后，无法查看该专业所属的院校

**修复方案**：

#### 4.1 院校详情页 → 查看专业
- 已实现：点击"查看专业"按钮跳转到专业搜索页
- 自动预设院校名称筛选条件
- 显示该院校的所有专业

#### 4.2 专业详情页 → 查看院校
- 修改 `viewSchool()` 方法
- 先通过院校名称查询院校ID
- 跳转到院校详情页，而不是院校列表页
- 用户可以直接查看院校完整信息

**修复代码**：
```javascript
// 专业详情页 - 查看院校
async viewSchool() {
    if (this.majorDetail && this.majorDetail.schoolName) {
        try {
            // 先查询该院校的ID
            const res = await get(`/school/list?schoolName=${encodeURIComponent(this.majorDetail.schoolName)}&pageSize=1`);
            if (res.code === 200 && res.data.records && res.data.records.length > 0) {
                const school = res.data.records[0];
                // 跳转到院校详情页
                uni.navigateTo({
                    url: `/pages/school/detail?id=${school.id}&name=${encodeURIComponent(school.schoolName)}`
                });
            }
        } catch (error) {
            uni.showToast({
                title: '查询失败',
                icon: 'none'
            });
        }
    }
}
```

**修复文件**：
- `GradMate_Uniapp/pages/school/detail.vue`
- `GradMate_Uniapp/pages/major/detail.vue`

---

### ⚠️ 问题5：收藏功能未实现（待阶段4完成）
**问题描述**：
- 前端显示有收藏按钮，但点击后收藏院校/专业不生效
- 数据库里没有相关表（如user_favorites_school或user_favorites_major）

**当前状态**：
- 前端已预留收藏按钮和交互逻辑
- 点击收藏会显示提示，但不会真正保存数据
- 标记为 TODO，将在阶段4（个人中心模块）中实现

**计划实现**（阶段4）：
1. 创建 `user_favorite` 表（已在阶段1规划中定义）
2. 实现收藏API：
   - `POST /api/favorite/add` - 添加收藏
   - `DELETE /api/favorite/remove` - 取消收藏
   - `GET /api/favorite/list` - 收藏列表
3. 前端对接收藏API
4. 个人中心显示收藏列表

**相关文件**：
- `GradMate_Uniapp/pages/school/detail.vue`（收藏院校）
- `GradMate_Uniapp/pages/major/detail.vue`（收藏专业）
- 阶段4将完成完整实现

---

### ✅ 问题6：查看全部功能优化
**问题描述**：点击"查看全部"不生效，或显示不全

**修复方案**：
- 院校详情页的"查看全部专业"功能已正常工作
- 跳转到专业搜索页，自动筛选该院校的专业
- 支持分页加载，可以查看所有专业
- 专业预览列表支持点击跳转到专业详情页

**修复代码**：
```javascript
// 院校详情页 - 专业预览列表添加点击事件
<view v-for="major in majorList.slice(0, 3)" :key="major.id" class="major-item" @click="goToMajorDetail(major)">
    <text class="major-name">{{ major.majorName }}</text>
    <text class="major-department">{{ major.department }}</text>
</view>

// 跳转到专业详情
goToMajorDetail(major) {
    uni.navigateTo({
        url: `/pages/major/detail?id=${major.id}`
    });
}
```

**验证方法**：
1. 进入任意院校详情页
2. 点击"查看专业"按钮或"查看全部"链接 → 跳转到专业搜索页
3. 点击专业预览列表中的任意专业 → 跳转到专业详情页
4. 显示该院校的所有专业（支持分页）

---

### ✅ 问题7：院校官网链接查看功能
**问题描述**：院校详情页无法查看学校官网链接

**修复方案**：
- 创建 webview 页面用于在应用内浏览外部网页
- 点击"官方网站"链接时，在 webview 中打开学校官网
- 支持自定义页面标题显示学校名称

**新增文件**：
- `GradMate_Uniapp/pages/webview/webview.vue` - 网页浏览器页面
- 在 `pages.json` 中注册 webview 页面

**修复代码**：
```javascript
// 院校详情页 - 打开官网
openWebsite() {
    if (this.schoolDetail && this.schoolDetail.school.link) {
        uni.navigateTo({
            url: `/pages/webview/webview?url=${encodeURIComponent(this.schoolDetail.school.link)}&title=${encodeURIComponent(this.schoolDetail.school.schoolName + '官网')}`
        });
    } else {
        uni.showToast({
            title: '暂无官网链接',
            icon: 'none'
        });
    }
}
```

**验证方法**：
1. 进入任意院校详情页
2. 在"基本信息"区块找到"官方网站"
3. 点击"点击访问"链接
4. 验证：在应用内打开学校官网，页面标题显示"XX大学官网"

**修复文件**：
- `GradMate_Uniapp/pages/school/detail.vue`
- `GradMate_Uniapp/pages/webview/webview.vue`（新增）
- `GradMate_Uniapp/pages.json`

---

## 修复后的用户体验改进

### 1. 筛选交互更直观
- ✅ 点击省份 → 立即显示该省份的院校
- ✅ 点击985标签 → 立即显示985院校
- ✅ 点击学位类型 → 立即显示对应专业
- ✅ 无需额外点击"搜索"按钮

### 2. 搜索逻辑更智能
- ✅ 搜索院校名称 → 自动重置省份等筛选
- ✅ 搜索专业名称 → 自动重置其他筛选
- ✅ 优先显示搜索结果，不受其他条件限制

### 3. 弹窗交互更友好
- ✅ 点击遮罩关闭弹窗
- ✅ 点击关闭按钮关闭弹窗
- ✅ 添加滑入动画效果
- ✅ 弹窗不会固定在页面底部

### 4. 页面跳转更合理
- ✅ 院校详情 → 查看专业 → 显示该院校所有专业
- ✅ 专业详情 → 查看院校 → 跳转到院校详情页
- ✅ 院校详情 → 专业预览列表 → 点击跳转到专业详情
- ✅ 页面间数据传递正确

### 5. 外部链接访问
- ✅ 院校详情页可以查看学校官网
- ✅ 在应用内使用 webview 打开外部网页
- ✅ 页面标题显示学校名称

---

## 测试建议

### 测试场景1：筛选功能
1. 进入院校列表页
2. 点击"省份"筛选，选择"北京"
3. 验证：列表立即刷新，只显示北京的院校
4. 点击"985"标签
5. 验证：列表立即刷新，只显示北京的985院校

### 测试场景2：搜索功能
1. 在院校列表页，先选择省份"海南"
2. 在搜索框输入"北京大学"，点击搜索
3. 验证：省份筛选自动重置，显示北京大学

### 测试场景3：弹窗交互
1. 点击"省份"筛选
2. 验证：弹窗从底部滑入
3. 点击遮罩或关闭按钮
4. 验证：弹窗关闭

### 测试场景4：页面跳转
1. 进入任意院校详情页
2. 点击"查看专业"按钮或"查看全部"链接
3. 验证：跳转到专业搜索页，显示该院校的专业
4. 点击专业预览列表中的任意专业
5. 验证：跳转到专业详情页
6. 点击"查看院校"按钮
7. 验证：跳转到院校详情页

### 测试场景5：官网链接访问
1. 进入任意院校详情页
2. 在"基本信息"区块找到"官方网站"
3. 点击"点击访问"链接
4. 验证：在应用内打开学校官网
5. 验证：页面标题显示"XX大学官网"
6. 点击返回按钮
7. 验证：返回到院校详情页

---

## 待完成功能（后续阶段）

### 阶段4：个人中心模块
- [ ] 实现收藏功能（院校、专业、资讯）
- [ ] 创建 user_favorite 表
- [ ] 实现收藏API
- [ ] 个人中心显示收藏列表

### 阶段5：AI智能模块
- [ ] 基于用户收藏的智能推荐
- [ ] 相似院校/专业推荐

---

**修复时间**：2026-02-13  
**修复状态**：✅ 已全部完成（除收藏功能外）  
**测试状态**：待前端测试验证  

**注意**：收藏功能将在阶段4完整实现，当前仅为UI占位。

---

## 本次修复新增文件

### 前端文件
- `GradMate_Uniapp/pages/webview/webview.vue` - 网页浏览器页面（新增）

### 配置文件
- `GradMate_Uniapp/pages.json` - 注册 webview 页面路由

---

## 阶段2完成总结

### 已实现功能
1. ✅ 院校库搜索与筛选（省份、985/211标签）
2. ✅ 专业库搜索与筛选（院校、学位类型、考试方式）
3. ✅ 院校详情页展示（基本信息、特性、专业预览）
4. ✅ 专业详情页展示（招生信息、考试科目、研究方向）
5. ✅ 院校与专业双向导航
6. ✅ 专业预览列表点击跳转
7. ✅ 学校官网链接访问
8. ✅ 筛选条件立即生效
9. ✅ 搜索智能重置筛选
10. ✅ 弹窗交互优化

### 待后续阶段实现
- 收藏功能（阶段4：个人中心模块）
- AI智能推荐（阶段5：AI模块）
- 招生简章查看（阶段3：资讯模块）
- 调剂信息查看（阶段3：资讯模块）

**下一步**：进入阶段3开发（资讯模块）或阶段4开发（个人中心模块）