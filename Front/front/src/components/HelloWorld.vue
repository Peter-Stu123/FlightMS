<script setup>
import { ref, onMounted } from 'vue';

const userMessage = ref('');
const chatMessages = ref([]);
const isLoading = ref(false);

// 发送消息到后端AI接口 (使用数据库信息)
const sendMessage = async () => {
  if (!userMessage.value.trim()) return;
  
  try {
    // 添加用户消息到聊天记录
    chatMessages.value.push({
      type: 'user',
      content: userMessage.value
    });
    
    const message = userMessage.value;
    userMessage.value = '';
    isLoading.value = true;
    
    // 调用后端API
    const response = await fetch('http://localhost:8080/ai/chat-with-database', {
      method: 'POST',
      headers: {
        'Content-Type': 'text/plain'
      },
      body: message
    });
    
    if (!response.ok) {
      throw new Error('网络响应不正常');
    }
    
    const responseText = await response.text();
    
    // 添加AI回复到聊天记录
    chatMessages.value.push({
      type: 'ai',
      content: responseText
    });
  } catch (error) {
    console.error('发送消息失败:', error);
    chatMessages.value.push({
      type: 'system',
      content: '发送消息失败: ' + error.message
    });
  } finally {
    isLoading.value = false;
  }
};
</script>

<template>
  <div class="ai-chat-container">
    <h1>航班信息智能助手</h1>
    <p class="description">这个AI助手可以访问航班数据库信息，请询问任何关于航班的问题</p>
    
    <div class="chat-messages" ref="chatContainer">
      <div v-if="chatMessages.length === 0" class="empty-state">
        还没有消息，开始和航班AI助手聊天吧!
      </div>
      
      <div v-for="(message, index) in chatMessages" :key="index"
           :class="['message', message.type]">
        <div class="avatar">
          {{ message.type === 'user' ? '用户' : 'AI' }}
        </div>
        <div class="content">{{ message.content }}</div>
      </div>
      
      <div v-if="isLoading" class="message ai loading">
        <div class="avatar">AI</div>
        <div class="content">
          <span class="loading-dots">
            <span></span><span></span><span></span>
          </span>
        </div>
      </div>
    </div>
    
    <div class="message-input">
      <input 
        type="text" 
        v-model="userMessage" 
        @keyup.enter="sendMessage" 
        placeholder="输入您的问题，例如: '有哪些从北京到上海的航班?'" 
        :disabled="isLoading"
      />
      <button @click="sendMessage" :disabled="isLoading">
        {{ isLoading ? '发送中...' : '发送' }}
      </button>
    </div>
  </div>
</template>

<style scoped>
.ai-chat-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

h1 {
  color: #2c3e50;
  text-align: center;
}

.description {
  text-align: center;
  color: #666;
  margin-bottom: 20px;
}

.chat-messages {
  height: 400px;
  overflow-y: auto;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 10px;
  margin-bottom: 20px;
  background-color: #f9f9f9;
}

.empty-state {
  color: #999;
  text-align: center;
  margin-top: 180px;
}

.message {
  display: flex;
  margin-bottom: 15px;
}

.message.user {
  flex-direction: row-reverse;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 12px;
}

.message.user .avatar {
  background-color: #4caf50;
  color: white;
}

.message.ai .avatar {
  background-color: #2196f3;
  color: white;
}

.message.system .avatar {
  background-color: #ff5722;
  color: white;
}

.content {
  max-width: 70%;
  padding: 10px;
  border-radius: 8px;
  margin: 0 10px;
  word-break: break-word;
}

.message.user .content {
  background-color: #e3f2fd;
  text-align: right;
}

.message.ai .content {
  background-color: white;
  border: 1px solid #e0e0e0;
}

.message.system .content {
  background-color: #ffebee;
  color: #d32f2f;
}

.message-input {
  display: flex;
  gap: 10px;
}

input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

button {
  padding: 10px 20px;
  background-color: #2196f3;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  background-color: #ccc;
}

.loading-dots span {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #888;
  margin: 0 3px;
  animation: loading 1.4s infinite ease-in-out both;
}

.loading-dots span:nth-child(1) {
  animation-delay: -0.32s;
}

.loading-dots span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes loading {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1.0); }
}
</style>
