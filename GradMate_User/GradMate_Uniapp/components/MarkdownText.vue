<template>
	<view class="markdown-body">
		<view v-for="(block, index) in parsedBlocks" :key="index" class="markdown-block">
			<!-- 标题 -->
			<text v-if="block.type === 'h1'" class="h1">{{ block.content }}</text>
			<text v-if="block.type === 'h2'" class="h2">{{ block.content }}</text>
			<text v-if="block.type === 'h3'" class="h3">{{ block.content }}</text>
			
			<!-- 代码块 -->
			<view v-if="block.type === 'code'" class="code-block">
				<text class="code-text">{{ block.content }}</text>
			</view>
			
			<!-- 列表项 -->
			<view v-if="block.type === 'list'" class="list-item" :style="{ paddingLeft: (block.indent * 30 + 16) + 'rpx' }">
				<text class="list-text">
					<text v-if="block.number" class="list-number">{{ block.number }}. </text>
					<text v-else class="list-bullet">• </text>
					<text 
						v-for="(span, spanIndex) in parseInlineStyles(block.content)" 
						:key="spanIndex"
						:class="span.style"
					>{{ span.text }}</text>
				</text>
			</view>
			
			<!-- 空行间距 -->
			<view v-if="block.type === 'space'" class="space-block"></view>
			
			<!-- 普通段落（支持行内样式） -->
			<view v-if="block.type === 'paragraph'" class="paragraph">
				<text 
					v-for="(span, spanIndex) in block.spans" 
					:key="spanIndex"
					:class="span.style"
				>{{ span.text }}</text>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	name: 'MarkdownText',
	props: {
		content: {
			type: String,
			default: ''
		}
	},
	computed: {
		parsedBlocks() {
			return this.parseMarkdown(this.content);
		}
	},
	methods: {
		parseMarkdown(text) {
			if (!text) return [];
			
			const blocks = [];
			const lines = text.split('\n');
			let inCodeBlock = false;
			let codeContent = [];
			let codeLang = '';
			
			for (let i = 0; i < lines.length; i++) {
				const line = lines[i];
				
				// 代码块开始/结束
				if (line.trim().startsWith('```')) {
					if (!inCodeBlock) {
						inCodeBlock = true;
						codeLang = line.trim().substring(3);
						codeContent = [];
					} else {
						blocks.push({
							type: 'code',
							content: codeContent.join('\n'),
							lang: codeLang
						});
						inCodeBlock = false;
						codeContent = [];
						codeLang = '';
					}
					continue;
				}
				
				// 代码块内容
				if (inCodeBlock) {
					codeContent.push(line);
					continue;
				}
				
				// 标题
				if (line.startsWith('### ')) {
					blocks.push({ type: 'h3', content: line.substring(4) });
					continue;
				}
				if (line.startsWith('## ')) {
					blocks.push({ type: 'h2', content: line.substring(3) });
					continue;
				}
				if (line.startsWith('# ')) {
					blocks.push({ type: 'h1', content: line.substring(2) });
					continue;
				}
				
				// 列表（支持嵌套的子列表）
				if (line.match(/^[\-\*] /)) {
					blocks.push({ 
						type: 'list', 
						content: line.substring(2),
						indent: 0
					});
					continue;
				}
				if (line.match(/^\d+\. /)) {
					const match = line.match(/^(\d+)\. (.+)$/);
					if (match) {
						blocks.push({ 
							type: 'list', 
							content: match[2],
							number: match[1],
							indent: 0
						});
					}
					continue;
				}
				
				// 空行（添加间距）
				if (line.trim() === '') {
					// 如果上一个block不是空行，添加一个空行block
					if (blocks.length > 0 && blocks[blocks.length - 1].type !== 'space') {
						blocks.push({ type: 'space' });
					}
					continue;
				}
				
				// 普通段落（解析行内样式）
				blocks.push({
					type: 'paragraph',
					spans: this.parseInlineStyles(line)
				});
			}
			
			return blocks;
		},
		
		parseInlineStyles(text) {
			const spans = [];
			let remaining = text;
			
			// 正则匹配：LaTeX公式、行内代码、粗体、斜体、链接
			// LaTeX: $...$ 或 $$...$$
			const regex = /(\$\$[^\$]+\$\$)|(\$[^\$]+\$)|(`[^`]+`)|(\*\*[^*]+\*\*)|(\*[^*]+\*)|(\[[^\]]+\]\([^\)]+\))/g;
			let lastIndex = 0;
			let match;
			
			while ((match = regex.exec(text)) !== null) {
				// 添加前面的普通文本
				if (match.index > lastIndex) {
					spans.push({
						text: text.substring(lastIndex, match.index),
						style: 'normal-text'
					});
				}
				
				const matched = match[0];
				
				// LaTeX公式（块级）
				if (matched.startsWith('$$')) {
					spans.push({
						text: matched.substring(2, matched.length - 2),
						style: 'math-block'
					});
				}
				// LaTeX公式（行内）
				else if (matched.startsWith('$')) {
					spans.push({
						text: matched.substring(1, matched.length - 1),
						style: 'math-inline'
					});
				}
				// 行内代码
				else if (matched.startsWith('`')) {
					spans.push({
						text: matched.substring(1, matched.length - 1),
						style: 'inline-code'
					});
				}
				// 粗体
				else if (matched.startsWith('**')) {
					spans.push({
						text: matched.substring(2, matched.length - 2),
						style: 'bold'
					});
				}
				// 斜体
				else if (matched.startsWith('*')) {
					spans.push({
						text: matched.substring(1, matched.length - 1),
						style: 'italic'
					});
				}
				// 链接
				else if (matched.startsWith('[')) {
					const linkMatch = matched.match(/\[([^\]]+)\]\(([^\)]+)\)/);
					if (linkMatch) {
						spans.push({
							text: linkMatch[1],
							style: 'link'
						});
					}
				}
				
				lastIndex = regex.lastIndex;
			}
			
			// 添加剩余的普通文本
			if (lastIndex < text.length) {
				spans.push({
					text: text.substring(lastIndex),
					style: 'normal-text'
				});
			}
			
			// 如果没有匹配到任何样式，返回整个文本
			if (spans.length === 0) {
				spans.push({
					text: text,
					style: 'normal-text'
				});
			}
			
			return spans;
		}
	}
};
</script>

<style scoped>
.markdown-body {
	line-height: 1.8;
	word-wrap: break-word;
	width: 100%;
	box-sizing: border-box;
}

.markdown-block {
	margin-bottom: 8rpx;
}

/* 标题样式 */
.h1 {
	font-size: 32rpx;
	font-weight: 600;
	color: #1a1a1a;
	display: block;
	margin: 20rpx 0 16rpx;
	padding-bottom: 8rpx;
	border-bottom: 1rpx solid #e0e0e0;
}

.h2 {
	font-size: 30rpx;
	font-weight: 600;
	color: #2a2a2a;
	display: block;
	margin: 18rpx 0 14rpx;
}

.h3 {
	font-size: 29rpx;
	font-weight: 600;
	color: #3a3a3a;
	display: block;
	margin: 16rpx 0 12rpx;
}

/* 代码块样式 */
.code-block {
	background: #f6f8fa;
	border: 1rpx solid #e1e4e8;
	border-radius: 8rpx;
	padding: 16rpx;
	margin: 12rpx 0;
	overflow-x: auto;
}

.code-text {
	font-family: 'Courier New', Courier, monospace;
	font-size: 24rpx;
	color: #24292e;
	line-height: 1.6;
	white-space: pre-wrap;
	word-break: break-all;
}

/* 列表样式 */
.list-item {
	margin: 4rpx 0;
	padding-left: 16rpx;
}

.list-text {
	color: #333;
	line-height: 1.8;
	font-size: 32rpx;
}

.list-bullet {
	color: #5b8ff9;
	font-weight: 600;
}

.list-number {
	color: #5b8ff9;
	font-weight: 600;
}

/* 空行间距 */
.space-block {
	height: 12rpx;
}

/* 段落样式 */
.paragraph {
	margin: 6rpx 0;
	line-height: 1.8;
}

.normal-text {
	color: #333;
	font-size: 32rpx;
}

/* 行内代码样式 */
.inline-code {
	background: #f0f0f0;
	color: #e83e8c;
	padding: 2rpx 8rpx;
	border-radius: 6rpx;
	font-family: 'Courier New', Courier, monospace;
	font-size: 28rpx;
}

/* 数学公式样式 */
.math-inline {
	background: #fff8e1;
	color: #d84315;
	padding: 2rpx 8rpx;
	border-radius: 6rpx;
	font-family: 'Times New Roman', serif;
	font-size: 28rpx;
	font-style: italic;
}

.math-block {
	background: #fff8e1;
	color: #d84315;
	padding: 8rpx 12rpx;
	border-radius: 8rpx;
	font-family: 'Times New Roman', serif;
	font-size: 28rpx;
	font-style: italic;
	display: block;
	margin: 8rpx 0;
	text-align: center;
}

/* 粗体样式 */
.bold {
	font-weight: 600;
	color: #1a1a1a;
	font-size: 32rpx;
}

/* 斜体样式 */
.italic {
	font-style: italic;
	color: #4a4a4a;
	font-size: 32rpx;
}

/* 链接样式 */
.link {
	color: #0366d6;
	text-decoration: underline;
	font-size: 32rpx;
}
</style>
