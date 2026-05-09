# StarCourse Prompt Dry-Run Validation

离线验证 prompt 模板的正确性，无需调用 DeepSeek API。

## 运行

```bash
python3 dry-runs/render.py
```

退出码 0 = 全部通过，1 = 有失败。

## 三阶段流水线

| 阶段 | 名称 | 做什么 |
|------|------|--------|
| 1 | **parse** | 加载 prompt 模板，递归解析 `{{include "..."}}` 引入共享片段 |
| 2 | **candidate** | 将 fixture 中的 `input` 字段代入模板 `{{变量}}`，检查是否全部替换完成 |
| 3 | **script** | 校验 `expectedOutput` 的 JSON 结构和必填字段是否齐全 |

## 添加新测试用例

在 `fixtures/` 目录下创建 JSON 文件：

```json
{
  "promptType": "questionnaire | script-generation | rewrite",
  "description": "用例描述，会显示在输出中",
  "input": {
    "变量名": "变量值"
  },
  "expectedOutput": {
    "字段名": "类型描述"
  }
}
```

### 各 promptType 的 input 变量

**questionnaire**
`subject`, `gradeLevel`, `style`, `strengths`, `shootableContent`, `frequency`, `targetAudience`, `pricingRange`, `differentiator`, `platformPreference`

**script-generation**
`count`, `nickname`, `subject`, `gradeLevel`, `style`, `strengths`, `differentiator`, `positioning`, `materials`

**rewrite**
`direction`, `originalContent`

### expectedOutput 格式

- **questionnaire**: `{ "nickname": "string", "avatarSuggestion": "string", "positioning": "string" }`
- **script-generation**: `{ "type": "array", "minItems": N, "itemSchema": { "title": "string", "hook": "string", "fullScript": "string", "shootingTips": "string" } }`
- **rewrite**: `{ "rewrittenContent": "string" }`

## 目录结构

```
dry-runs/
├── README.md
├── render.py              # 验证脚本
└── fixtures/
    ├── questionnaire_case1.json   # 数学老师
    ├── questionnaire_case2.json   # 英语老师
    ├── script_case1.json          # 短视频脚本生成
    └── rewrite_case1.json         # 文案改写
```

## 工程铁律

根据项目规范，所有 LLM prompt 改动必须先跑 dry-run 回归，确认无误后再提交。
