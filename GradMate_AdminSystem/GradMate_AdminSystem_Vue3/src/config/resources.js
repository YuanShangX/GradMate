function field(prop, label, options = {}) {
  return { prop, label, ...options }
}

const schoolFields = [
  field('school_name', '院校名称', { required: true }),
  field('logo', 'Logo URL'),
  field('link', '官网链接'),
  field('location', '地区'),
  field('subordinate', '主管部门'),
  field('syl', '双一流', { type: 'switch' }),
  field('yjsy', '研究生院', { type: 'switch' }),
  field('zhx', '自划线', { type: 'switch' })
]

const majorFields = [
  field('school_name', '院校名称', { required: true }),
  field('major_name', '专业名称', { required: true }),
  field('province', '省份'),
  field('major_code', '专业代码'),
  field('degree_type', '学位类型'),
  field('exam_type', '考试方式'),
  field('department', '院系所'),
  field('study_mode', '学习方式'),
  field('research_direction', '研究方向'),
  field('veteran_program', '退役计划'),
  field('shaogu_program', '少骨计划'),
  field('advisor', '指导教师', { type: 'textarea' }),
  field('planned_enrollment', '拟招生人数'),
  field('exam_subject1', '考试科目1'),
  field('exam_subject2', '考试科目2'),
  field('exam_subject3', '考试科目3'),
  field('exam_subject4', '考试科目4')
]

const categoryFields = [
  field('category_code', '门类代码', { required: true }),
  field('category_name', '门类名称', { required: true }),
  field('discipline_code', '学科代码', { required: true }),
  field('discipline_name', '学科名称', { required: true }),
  field('major_code', '专业代码', { required: true }),
  field('major_name', '专业名称', { required: true }),
  field('degree_type', '学位类型', { required: true }),
  field('sort_order', '排序', { type: 'number' })
]

const infoFields = [
  field('school_id', '院校ID', { required: true, type: 'number' }),
  field('title', '标题', { required: true }),
  field('url', '原文链接'),
  field('publish_date', '发布日期', { type: 'datetime' })
]

const userFields = [
  field('username', '账号'),
  field('password', '密码'),
  field('openid', '微信OpenID'),
  field('nickname', '昵称'),
  field('avatar', '头像URL'),
  field('phone', '手机号'),
  field('target_school', '目标院校'),
  field('target_major', '目标专业'),
  field('target_school_id', '目标院校ID', { type: 'number' }),
  field('target_major_id', '目标专业ID', { type: 'number' })
]

const favoriteFields = [
  field('user_id', '用户ID', { required: true, type: 'number' }),
  field('type', '类型', { required: true }),
  field('target_id', '目标ID', { required: true, type: 'number' }),
  field('target_name', '目标名称'),
  field('metadata', '元数据', { type: 'textarea' })
]

const historyFields = [
  ...favoriteFields,
  field('view_time', '浏览时间', { type: 'datetime' })
]

const taskFields = [
  field('user_id', '用户ID', { required: true, type: 'number' }),
  field('task_content', '任务内容', { required: true, type: 'textarea' }),
  field('task_date', '任务日期', { required: true, type: 'date' }),
  field('is_completed', '是否完成', { type: 'switch' }),
  field('priority', '优先级', { type: 'number' }),
  field('complete_time', '完成时间', { type: 'datetime' }),
  field('color_theme', '颜色主题'),
  field('estimated_duration', '预计时长', { type: 'number' }),
  field('actual_duration', '实际时长', { type: 'number' }),
  field('tags', '标签'),
  field('parent_task_id', '父任务ID', { type: 'number' }),
  field('repeat_type', '重复类型'),
  field('sort_order', '排序', { type: 'number' }),
  field('description', '描述', { type: 'textarea' }),
  field('reminder_time', '提醒时间', { type: 'datetime' }),
  field('status', '状态')
]

const recordFields = [
  field('user_id', '用户ID', { required: true, type: 'number' }),
  field('check_date', '打卡日期', { required: true, type: 'date' }),
  field('duration', '学习时长', { type: 'number' }),
  field('note', '备注', { type: 'textarea' }),
  field('mood', '心情', { type: 'number' }),
  field('focus_count', '专注次数', { type: 'number' }),
  field('pause_count', '暂停次数', { type: 'number' }),
  field('study_type', '学习类型')
]

const aiSessionFields = [
  field('user_id', '用户ID', { required: true, type: 'number' }),
  field('session_id', '会话ID', { required: true }),
  field('title', '标题'),
  field('message_count', '消息数', { type: 'number' })
]

const aiMessageFields = [
  field('session_id', '会话ID', { required: true }),
  field('user_id', '用户ID', { required: true, type: 'number' }),
  field('role', '角色', { required: true }),
  field('content', '内容', { required: true, type: 'textarea' }),
  field('tokens', 'Token数', { type: 'number' }),
  field('tool_calls', '工具调用', { type: 'textarea' })
]

const adminFields = [
  field('username', '账号', { required: true }),
  field('password', '密码', { type: 'password' }),
  field('nickname', '昵称'),
  field('role', '角色'),
  field('status', '状态', { type: 'switch' })
]

export const menuGroups = [
  {
    title: '基础数据',
    icon: 'DataBoard',
    items: [
      { path: '/schools', title: '院校管理', resource: 'schools', apiPath: '/schools', fields: schoolFields, filters: ['location', 'subordinate'] },
      { path: '/majors', title: '专业管理', resource: 'majors', apiPath: '/majors', fields: majorFields, filters: ['province', 'degreeType', 'examType', 'studyMode'] },
      { path: '/major-categories', title: '专业分类', resource: 'major-categories', apiPath: '/major-categories', fields: categoryFields, filters: ['degreeType', 'categoryCode'] }
    ]
  },
  {
    title: '资讯内容',
    icon: 'Document',
    items: [
      { path: '/infos/brochure', title: '招生简章', resource: 'brochure', apiPath: '/infos/brochure', fields: infoFields, filters: ['schoolId'] },
      { path: '/infos/adjust', title: '调剂通知', resource: 'adjust', apiPath: '/infos/adjust', fields: infoFields, filters: ['schoolId'] },
      { path: '/infos/information', title: '校园资讯', resource: 'information', apiPath: '/infos/information', fields: infoFields, filters: ['schoolId'] }
    ]
  },
  {
    title: '用户数据',
    icon: 'User',
    items: [
      { path: '/users', title: '用户管理', resource: 'users', apiPath: '/users', fields: userFields, filters: ['username', 'nickname', 'targetSchoolId', 'targetMajorId'] },
      { path: '/favorites', title: '收藏管理', resource: 'favorites', apiPath: '/favorites', fields: favoriteFields, filters: ['userId', 'type'] },
      { path: '/histories', title: '浏览历史', resource: 'histories', apiPath: '/histories', fields: historyFields, filters: ['userId', 'type'] }
    ]
  },
  {
    title: '学习与AI',
    icon: 'Reading',
    items: [
      { path: '/study/tasks', title: '学习任务', resource: 'study-tasks', apiPath: '/study/tasks', fields: taskFields, filters: ['userId', 'status', 'isCompleted'] },
      { path: '/study/records', title: '学习记录', resource: 'study-records', apiPath: '/study/records', fields: recordFields, filters: ['userId', 'studyType'] },
      { path: '/ai/sessions', title: 'AI会话', resource: 'ai-sessions', apiPath: '/ai/sessions', fields: aiSessionFields, filters: ['userId'] },
      { path: '/ai/messages', title: 'AI消息', resource: 'ai-messages', apiPath: '/ai/messages', fields: aiMessageFields, filters: ['userId', 'sessionId', 'role'] }
    ]
  },
  {
    title: '系统管理',
    icon: 'Setting',
    items: [
      { path: '/admins', title: '管理员账号', resource: 'admins', apiPath: '/admins', fields: adminFields, filters: ['status', 'role'] },
      { path: '/logs', title: '系统日志', resource: 'logs', apiPath: '/logs', fields: [], filters: ['actionType', 'resourceKey', 'adminUsername'], readonly: true }
    ]
  }
]

export const flatMenus = menuGroups.flatMap((group) => group.items)
