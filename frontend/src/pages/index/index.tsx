import { useState } from 'react'
import Taro from '@tarojs/taro'
import { View, Text, Picker } from '@tarojs/components'
import './index.scss'

const SUBJECTS = ['小学数学', '初中英语', '少儿编程', '艺考美术', '高中物理', '小学语文', '其他']
const GRADES = ['小学1-3年级', '小学4-6年级', '初中', '高中', '全年龄段']
const STYLES = ['幽默风趣', '严谨专业', '温柔耐心', '激情活力']
const STRENGTHS_LIST = ['提分快', '互动强', '方法独特', '经验丰富', '学生好评多']
const CONTENTS = ['课堂片段', '知识点讲解', '学生作品', '教学日常', '备课过程']
const FREQUENCIES = ['每周1-3条', '每周4-7条', '每天1条以上']
const PRICINGS = ['50-100元/课时', '100-200元/课时', '200-500元/课时', '500元以上/课时']
const PLATFORMS = ['小红书', '抖音', '都做']

export default function Index() {
  const [step, setStep] = useState(0)
  const [loading, setLoading] = useState(false)
  const [form, setForm] = useState({
    subject: '',
    gradeRange: '',
    teachingStyle: '',
    strengths: [] as string[],
    shootableContent: [] as string[],
    willingToShowFace: true,
    frequency: '',
    monthlyGoal: '',
    pricingRange: '',
    platformPreference: '',
  })

  const update = (key: string, val: any) => setForm(prev => ({ ...prev, [key]: val }))

  const toggleArr = (key: 'strengths' | 'shootableContent', val: string) => {
    const arr = form[key]
    update(key, arr.includes(val) ? arr.filter(v => v !== val) : [...arr, val])
  }

  const questions = [
    // Q1: 学科
    <View key='q1' className='question'>
      <Text className='q-label'>📚 你教什么学科？</Text>
      <View className='option-grid'>
        {SUBJECTS.map(s => (
          <View key={s} className={`option ${form.subject === s ? 'active' : ''}`} onClick={() => update('subject', s)}>
            {s}
          </View>
        ))}
      </View>
    </View>,

    // Q2: 年级
    <View key='q2' className='question'>
      <Text className='q-label'>🎒 主要面向哪个年级？</Text>
      <View className='option-grid'>
        {GRADES.map(g => (
          <View key={g} className={`option ${form.gradeRange === g ? 'active' : ''}`} onClick={() => update('gradeRange', g)}>
            {g}
          </View>
        ))}
      </View>
    </View>,

    // Q3: 教学风格
    <View key='q3' className='question'>
      <Text className='q-label'>🎨 你的教学风格？</Text>
      <View className='option-grid'>
        {STYLES.map(s => (
          <View key={s} className={`option ${form.teachingStyle === s ? 'active' : ''}`} onClick={() => update('teachingStyle', s)}>
            {s}
          </View>
        ))}
      </View>
    </View>,

    // Q4: 优势（多选）
    <View key='q4' className='question'>
      <Text className='q-label'>💪 教学优势（可多选）</Text>
      <View className='option-grid'>
        {STRENGTHS_LIST.map(s => (
          <View key={s} className={`option ${form.strengths.includes(s) ? 'active' : ''}`} onClick={() => toggleArr('strengths', s)}>
            {s}
          </View>
        ))}
      </View>
    </View>,

    // Q5: 可拍摄内容（多选）
    <View key='q5' className='question'>
      <Text className='q-label'>🎬 你能拍什么内容？（可多选）</Text>
      <View className='option-grid'>
        {CONTENTS.map(c => (
          <View key={c} className={`option ${form.shootableContent.includes(c) ? 'active' : ''}`} onClick={() => toggleArr('shootableContent', c)}>
            {c}
          </View>
        ))}
      </View>
    </View>,

    // Q6: 露脸
    <View key='q6' className='question'>
      <Text className='q-label'>😊 愿意在视频里露脸吗？</Text>
      <View className='option-grid two-col'>
        <View className={`option ${form.willingToShowFace ? 'active' : ''}`} onClick={() => update('willingToShowFace', true)}>👍 愿意</View>
        <View className={`option ${!form.willingToShowFace ? 'active' : ''}`} onClick={() => update('willingToShowFace', false)}>🙅 不愿意</View>
      </View>
    </View>,

    // Q7: 频率
    <View key='q7' className='question'>
      <Text className='q-label'>📅 计划多久发一条？</Text>
      <View className='option-grid'>
        {FREQUENCIES.map(f => (
          <View key={f} className={`option ${form.frequency === f ? 'active' : ''}`} onClick={() => update('frequency', f)}>
            {f}
          </View>
        ))}
      </View>
    </View>,

    // Q8: 目标
    <View key='q8' className='question'>
      <Text className='q-label'>🎯 每月想招多少新学员？</Text>
      <View className='option-grid'>
        {['3个', '5个', '10个', '20个以上'].map(g => (
          <View key={g} className={`option ${form.monthlyGoal === g ? 'active' : ''}`} onClick={() => update('monthlyGoal', g)}>
            {g}
          </View>
        ))}
      </View>
    </View>,

    // Q9: 定价
    <View key='q9' className='question'>
      <Text className='q-label'>💰 课时定价区间？</Text>
      <View className='option-grid'>
        {PRICINGS.map(p => (
          <View key={p} className={`option ${form.pricingRange === p ? 'active' : ''}`} onClick={() => update('pricingRange', p)}>
            {p}
          </View>
        ))}
      </View>
    </View>,

    // Q10: 平台
    <View key='q10' className='question'>
      <Text className='q-label'>📱 想在哪个平台获客？</Text>
      <View className='option-grid'>
        {PLATFORMS.map(p => (
          <View key={p} className={`option ${form.platformPreference === p ? 'active' : ''}`} onClick={() => update('platformPreference', p)}>
            {p}
          </View>
        ))}
      </View>
    </View>,
  ]

  const canNext = () => {
    if (step === 0) return !!form.subject
    if (step === 1) return !!form.gradeRange
    if (step === 2) return !!form.teachingStyle
    if (step === 3) return form.strengths.length > 0
    if (step === 4) return form.shootableContent.length > 0
    if (step === 5) return true
    if (step === 6) return !!form.frequency
    if (step === 7) return !!form.monthlyGoal
    if (step === 8) return !!form.pricingRange
    if (step === 9) return !!form.platformPreference
    return true
  }

  const handleSubmit = async () => {
    setLoading(true)
    try {
      const res = await new Promise<any>((resolve, reject) => {
        Taro.request({
          url: 'http://localhost:8080/api/questionnaire/submit',
          method: 'POST',
          header: { 'Content-Type': 'application/json' },
          data: {
            openId: 'wx_' + Date.now(),
            subject: form.subject,
            gradeLevel: form.gradeRange,
            style: form.teachingStyle,
            strengths: form.strengths.join(','),
            shootableContent: form.shootableContent.join(','),
            frequency: form.frequency,
            pricingRange: form.pricingRange,
            platformPreference: form.platformPreference,
          },
          success: (r) => resolve(r),
          fail: (e) => reject(e),
        })
      })

      if (res.statusCode === 200) {
        Taro.navigateTo({ url: `/pages/result/result?data=${encodeURIComponent(JSON.stringify(res.data))}` })
      } else {
        Taro.showToast({ title: '提交失败，请重试', icon: 'none' })
      }
    } catch (e) {
      Taro.showToast({ title: '网络错误，请检查后端是否运行', icon: 'none' })
    } finally {
      setLoading(false)
    }
  }

  return (
    <View className='container'>
      {/* 进度条 */}
      <View className='progress-bar'>
        <View className='progress-fill' style={{ width: `${((step + 1) / 10) * 100}%` }} />
      </View>
      <Text className='step-text'>{step + 1} / 10</Text>

      {/* 问题 */}
      {questions[step]}

      {/* 按钮 */}
      <View className='btn-row'>
        {step > 0 && (
          <View className='btn btn-back' onClick={() => setStep(step - 1)}>上一题</View>
        )}
        {step < 9 ? (
          <View className={`btn btn-next ${canNext() ? '' : 'disabled'}`} onClick={() => canNext() && setStep(step + 1)}>下一题</View>
        ) : (
          <View className={`btn btn-submit ${canNext() && !loading ? '' : 'disabled'}`} onClick={() => canNext() && !loading && handleSubmit()}>
            {loading ? 'AI 生成中...' : '🚀 提交问卷'}
          </View>
        )}
      </View>
    </View>
  )
}
