"use strict";
const common_vendor = require("../common/vendor.js");
const _sfc_main = {
  name: "MarkdownText",
  props: {
    content: {
      type: String,
      default: ""
    }
  },
  computed: {
    parsedBlocks() {
      return this.parseMarkdown(this.content);
    }
  },
  methods: {
    parseMarkdown(text) {
      if (!text)
        return [];
      const blocks = [];
      const lines = text.split("\n");
      let inCodeBlock = false;
      let codeContent = [];
      let codeLang = "";
      for (let i = 0; i < lines.length; i++) {
        const line = lines[i];
        if (line.trim().startsWith("```")) {
          if (!inCodeBlock) {
            inCodeBlock = true;
            codeLang = line.trim().substring(3);
            codeContent = [];
          } else {
            blocks.push({
              type: "code",
              content: codeContent.join("\n"),
              lang: codeLang
            });
            inCodeBlock = false;
            codeContent = [];
            codeLang = "";
          }
          continue;
        }
        if (inCodeBlock) {
          codeContent.push(line);
          continue;
        }
        if (line.startsWith("### ")) {
          blocks.push({ type: "h3", content: line.substring(4) });
          continue;
        }
        if (line.startsWith("## ")) {
          blocks.push({ type: "h2", content: line.substring(3) });
          continue;
        }
        if (line.startsWith("# ")) {
          blocks.push({ type: "h1", content: line.substring(2) });
          continue;
        }
        if (line.match(/^[\-\*] /)) {
          blocks.push({
            type: "list",
            content: line.substring(2),
            indent: 0
          });
          continue;
        }
        if (line.match(/^\d+\. /)) {
          const match = line.match(/^(\d+)\. (.+)$/);
          if (match) {
            blocks.push({
              type: "list",
              content: match[2],
              number: match[1],
              indent: 0
            });
          }
          continue;
        }
        if (line.trim() === "") {
          if (blocks.length > 0 && blocks[blocks.length - 1].type !== "space") {
            blocks.push({ type: "space" });
          }
          continue;
        }
        blocks.push({
          type: "paragraph",
          spans: this.parseInlineStyles(line)
        });
      }
      return blocks;
    },
    parseInlineStyles(text) {
      const spans = [];
      const regex = /(\$\$[^\$]+\$\$)|(\$[^\$]+\$)|(`[^`]+`)|(\*\*[^*]+\*\*)|(\*[^*]+\*)|(\[[^\]]+\]\([^\)]+\))/g;
      let lastIndex = 0;
      let match;
      while ((match = regex.exec(text)) !== null) {
        if (match.index > lastIndex) {
          spans.push({
            text: text.substring(lastIndex, match.index),
            style: "normal-text"
          });
        }
        const matched = match[0];
        if (matched.startsWith("$$")) {
          spans.push({
            text: matched.substring(2, matched.length - 2),
            style: "math-block"
          });
        } else if (matched.startsWith("$")) {
          spans.push({
            text: matched.substring(1, matched.length - 1),
            style: "math-inline"
          });
        } else if (matched.startsWith("`")) {
          spans.push({
            text: matched.substring(1, matched.length - 1),
            style: "inline-code"
          });
        } else if (matched.startsWith("**")) {
          spans.push({
            text: matched.substring(2, matched.length - 2),
            style: "bold"
          });
        } else if (matched.startsWith("*")) {
          spans.push({
            text: matched.substring(1, matched.length - 1),
            style: "italic"
          });
        } else if (matched.startsWith("[")) {
          const linkMatch = matched.match(/\[([^\]]+)\]\(([^\)]+)\)/);
          if (linkMatch) {
            spans.push({
              text: linkMatch[1],
              style: "link"
            });
          }
        }
        lastIndex = regex.lastIndex;
      }
      if (lastIndex < text.length) {
        spans.push({
          text: text.substring(lastIndex),
          style: "normal-text"
        });
      }
      if (spans.length === 0) {
        spans.push({
          text,
          style: "normal-text"
        });
      }
      return spans;
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: common_vendor.f($options.parsedBlocks, (block, index, i0) => {
      return common_vendor.e({
        a: block.type === "h1"
      }, block.type === "h1" ? {
        b: common_vendor.t(block.content)
      } : {}, {
        c: block.type === "h2"
      }, block.type === "h2" ? {
        d: common_vendor.t(block.content)
      } : {}, {
        e: block.type === "h3"
      }, block.type === "h3" ? {
        f: common_vendor.t(block.content)
      } : {}, {
        g: block.type === "code"
      }, block.type === "code" ? {
        h: common_vendor.t(block.content)
      } : {}, {
        i: block.type === "list"
      }, block.type === "list" ? common_vendor.e({
        j: block.number
      }, block.number ? {
        k: common_vendor.t(block.number)
      } : {}, {
        l: common_vendor.f($options.parseInlineStyles(block.content), (span, spanIndex, i1) => {
          return {
            a: common_vendor.t(span.text),
            b: spanIndex,
            c: common_vendor.n(span.style)
          };
        }),
        m: block.indent * 30 + 16 + "rpx"
      }) : {}, {
        n: block.type === "space"
      }, block.type === "space" ? {} : {}, {
        o: block.type === "paragraph"
      }, block.type === "paragraph" ? {
        p: common_vendor.f(block.spans, (span, spanIndex, i1) => {
          return {
            a: common_vendor.t(span.text),
            b: spanIndex,
            c: common_vendor.n(span.style)
          };
        })
      } : {}, {
        q: index
      });
    })
  };
}
const Component = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-113eb076"]]);
wx.createComponent(Component);
