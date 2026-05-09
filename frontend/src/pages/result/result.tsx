import { useState, useEffect } from 'react'
import Taro, { useRouter } from '@tarojs/taro'
import { View, Text } from '@tarojs/components'
import './result.scss'

export default function Result() {
  const router = useRouter()
  const [data, setData] = useState<any>(null)

  useEffect(() => {
    try {
      const raw = decodeURIComponent(router.params.data || '{}')
      setData(JSON.parse(raw))
    } catch {
      setData(null)
    }
  }, [])

  if (!data) {
    return (
      <View className='result-container'>
        <Text className='error-text'>数据加载失败</Text>
      </View>
    )
  }

  return (
    <View className='result-container'>
      <View className='result-header'>
        <Text className='result-emoji'>🎉</Text>
        <Text className='result-title'>AI 已为你定制专属方案</Text>
        <Text className='result-subtitle'>基于你的教学特点，AI 在 30 秒内生成</Text>
      </View>

      <View className='result-card'>
        <Text className='card-label'>✨ 专属昵称</Text>
        <Text className='card-value'>{data.nickname || '生成中...'}</Text>
      </View>

      <View className='result-card'>
        <Text className='card-label'>🖼️ 头像建议</Text>
        <Text className='card-value'>{data.avatarSuggestion || '生成中...'}</Text>
      </View>

      <View className='result-card highlight'>
        <Text className='card-label'>🎯 一句话定位</Text>
        <Text className='card-value large'>{data.positioning || '生成中...'}</Text>
      </View>

      <View className='result-status'>
        <Text className='status-badge'>状态：{data.status === 'COMPLETED' ? '✅ 已完成' : '⏳ 处理中'}</Text>
      </View>

      <View className='btn-row'>
        <View className='btn btn-back' onClick={() => Taro.navigateBack()}>返回修改</View>
        <View className='btn btn-next' onClick={() => Taro.showToast({ title: '脚本生成功能开发中...', icon: 'none' })}>生成视频脚本</View>
      </View>
    </View>
  )
}
