<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="background-shapes"></div>
    
    <!-- 登录卡片 -->
    <div class="login-card" :class="{'register-mode': !isLogin}">
      <!-- 表单区 -->
      <div class="form-container">
        <div class="form-section">
          <div class="logo">
            <img src="https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/2708.svg" alt="航班管理系统" class="logo-img">
            <span>航班管理系统</span>
          </div>
          
          <h2 class="section-title">{{ isLogin ? '登录账户' : '创建账户' }}</h2>
          <div class="social-login">
            
          </div>
          <div class="divider">
            <span>{{ isLogin ? '请使用账号密码登录' : '或使用邮箱注册' }}</span>
          </div>
          <!-- 表单滑动容器 -->
          <div class="forms-container">
            <div class="forms-slider" :style="sliderStyle">
              <!-- 登录表单 -->
              <form @submit.prevent="handleSubmit" class="login-form">
                <div class="form-group">
                  <div class="input-field">
                    <i class="field-icon">👤</i>
                    <input 
                      type="text" 
                      id="username" 
                      v-model="form.username" 
                      placeholder="请输入用户名"
                      required
                    />
                  </div>
                </div>
                
                <div class="form-group">
                  <div class="input-field">
                    <i class="field-icon">🔒</i>
                    <input 
                      :type="showPassword ? 'text' : 'password'" 
                      id="password" 
                      v-model="form.password" 
                      placeholder="请输入密码"
                      required
                    />
                    <span class="toggle-password" @click="showPassword = !showPassword">
                      {{ showPassword ? '👁️' : '👁️‍🗨️' }}
                    </span>
                  </div>
                </div>
                
                <div class="login-options">
                  <div class="remember-me">
                    <input type="checkbox" id="remember" v-model="rememberMe">
                    <label for="remember">记住我</label>
                  </div>
                  <a href="#" class="forgot-link">忘记密码?</a>
                </div>
                
                <button 
                  type="submit" 
                  class="submit-button" 
                  :disabled="loading"
                >
                  {{ loading ? '登录中...' : '登 录' }}
                </button>
              </form>
              
              <!-- 注册表单 -->
              <form @submit.prevent="handleSubmit" class="register-form">
                <div class="form-group">
                  <div class="input-field">
                    <i class="field-icon">👤</i>
                    <input 
                      type="text" 
                      id="reg-username" 
                      v-model="form.username" 
                      placeholder="请输入用户名"
                      required
                    />
                  </div>
                </div>
                
                <div class="form-group">
                  <div class="input-field">
                    <i class="field-icon">🔒</i>
                    <input 
                      :type="showPassword ? 'text' : 'password'" 
                      id="reg-password" 
                      v-model="form.password" 
                      placeholder="请输入密码"
                      required
                    />
                    <span class="toggle-password" @click="showPassword = !showPassword">
                      {{ showPassword ? '👁️' : '👁️‍🗨️' }}
                    </span>
                  </div>
                </div>
                
                <div class="form-group">
                  <div class="input-field">
                    <i class="field-icon">🔒</i>
                    <input 
                      :type="showConfirmPassword ? 'text' : 'password'" 
                      id="confirmPassword" 
                      v-model="form.confirmPassword" 
                      placeholder="请再次输入密码"
                      required
                    />
                    <span class="toggle-password" @click="showConfirmPassword = !showConfirmPassword">
                      {{ showConfirmPassword ? '👁️' : '👁️‍🗨️' }}
                    </span>
                  </div>
                </div>
                
                <button 
                  type="submit" 
                  class="submit-button" 
                  :disabled="loading"
                >
                  {{ loading ? '注册中...' : '创建账户' }}
                </button>
              </form>
            </div>
          </div>
          
          <p class="switch-text">
            {{ isLogin ? '还没有账户? ' : '已有账户? ' }}
            <a @click="toggleForm" class="switch-link">
              {{ isLogin ? '立即注册' : '立即登录' }}
            </a>
          </p>
        </div>
      </div>
      
      <!-- 信息区 -->
      <div class="info-container">
        <div class="info-section">
          <div class="info-content">
            <h1 class="info-title">{{ isLogin ? '您好，旅客！' : '欢迎回来！' }}</h1>
            <p class="info-description">
              {{ isLogin ? '注册账户，开始您的航空之旅' : '登录系统，管理您的航班信息' }}
            </p>
            <button class="action-button" @click="toggleForm">
              {{ isLogin ? '创建账户' : '立即登录' }}
            </button>
            
            <div class="illustration">
              <div class="plane-icon">✈️</div>
              <div class="cloud cloud-1">☁️</div>
              <div class="cloud cloud-2">☁️</div>
              <div class="cloud cloud-3">☁️</div>
              <div class="ticket ticket-1">🎫</div>
              <div class="ticket ticket-2">🎫</div>
              <div class="globe">🌍</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 底部版权 -->
    <div class="footer">
      <p>© 2025 航班管理系统 xumingkang版权所有</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const isLogin = ref(true);
const showPassword = ref(false);
const showConfirmPassword = ref(false);
const rememberMe = ref(false);
const loading = ref(false);

const form = reactive({
  username: '',
  password: '',
  confirmPassword: ''
});

// 表单滑动样式
const sliderStyle = computed(() => {
  return {
    transform: isLogin.value ? 'translateX(0)' : 'translateX(-50%)'
  };
});

// 切换登录/注册表单
const toggleForm = () => {
  isLogin.value = !isLogin.value;
};

const handleSubmit = async () => {
  if (loading.value) return;
  
  // 表单验证
  if (!form.username || !form.password) {
    alert('请填写所有必填字段');
    return;
  }
  
  if (!isLogin.value && form.password !== form.confirmPassword) {
    alert('两次输入的密码不一致');
    return;
  }
  
  loading.value = true;
  
  try {
    // 模拟登录/注册请求
    setTimeout(() => {
      if (isLogin.value) {
        // 登录成功
        localStorage.setItem('isLoggedIn', 'true');
        localStorage.setItem('username', form.username);
        
        if (rememberMe.value) {
          localStorage.setItem('rememberMe', 'true');
        }
        
        // 重定向到主页
        router.push('/flight');
      } else {
        // 注册成功
        alert('注册成功，请登录');
        isLogin.value = true;
        form.confirmPassword = '';
      }
      loading.value = false;
    }, 1000);
  } catch (error) {
    console.error('请求失败:', error);
    alert('服务器错误，请稍后再试');
    loading.value = false;
  }
};
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  background-color: #f0f8ff;
  padding: 20px;
  overflow: hidden;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

/* 背景装饰 */
.background-shapes {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  overflow: hidden;
}

.background-shapes::before {
  content: '';
  position: absolute;
  width: 600px;
  height: 600px;
  top: -100px;
  right: -100px;
  border-radius: 50%;
  background: rgba(25, 118, 210, 0.1);
  animation: pulse-bg 15s infinite alternate ease-in-out;
}

.background-shapes::after {
  content: '';
  position: absolute;
  width: 800px;
  height: 800px;
  bottom: -300px;
  left: -300px;
  border-radius: 50%;
  background: rgba(25, 118, 210, 0.1);
  animation: pulse-bg 20s infinite alternate-reverse ease-in-out;
}

@keyframes pulse-bg {
  0% {
    transform: translate(0, 0) scale(1);
  }
  100% {
    transform: translate(50px, 50px) scale(1.1);
  }
}

/* 登录卡片 */
.login-card {
  width: 1000px;
  max-width: 90%;
  min-height: 600px;
  display: flex;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 1;
  background: white;
  transition: all 0.6s cubic-bezier(0.68, -0.55, 0.265, 1.55);
  will-change: transform, box-shadow;
}

/* 注册模式下的卡片 */
.login-card.register-mode {
  flex-direction: row-reverse;
}

/* 悬浮效果 */
.login-card:hover {
  box-shadow: 0 25px 50px rgba(25, 118, 210, 0.2);
}

/* 表单容器和信息容器 */
.form-container, .info-container {
  width: 50%;
  position: relative;
  transition: all 0.8s cubic-bezier(0.68, -0.55, 0.265, 1.55);
  will-change: transform, opacity;
}

/* 表单区域 */
.form-section {
  width: 100%;
  height: 100%;
  padding: 50px;
  display: flex;
  flex-direction: column;
  background: white;
  transition: all 0.7s cubic-bezier(0.68, -0.55, 0.265, 1.55);
  opacity: 1;
  transform: translateZ(0);
}

/* 信息区域 */
.info-section {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #1976d2 0%, #0d47a1 100%);
  color: white;
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  overflow: hidden;
  transition: all 0.8s cubic-bezier(0.68, -0.55, 0.265, 1.55);
  will-change: transform, opacity;
}

/* 添加平滑过渡动画 */
.login-card:not(.register-mode) .form-container {
  animation: slideRightIn 0.8s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
}

.login-card.register-mode .form-container {
  animation: slideLeftIn 0.8s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
}

.login-card:not(.register-mode) .info-container {
  animation: slideLeftIn 0.8s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
}

.login-card.register-mode .info-container {
  animation: slideRightIn 0.8s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
}

@keyframes slideRightIn {
  0% {
    transform: translateX(-80px);
    opacity: 0.2;
  }
  50% {
    transform: translateX(10px);
    opacity: 0.8;
  }
  100% {
    transform: translateX(0);
    opacity: 1;
  }
}

@keyframes slideLeftIn {
  0% {
    transform: translateX(80px);
    opacity: 0.2;
  }
  50% {
    transform: translateX(-10px);
    opacity: 0.8;
  }
  100% {
    transform: translateX(0);
    opacity: 1;
  }
}

/* 添加边界发光效果 */
.login-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 50%;
  width: 2px;
  height: 100%;
  background: linear-gradient(to bottom, transparent, rgba(25, 118, 210, 0.5), transparent);
  z-index: 2;
  opacity: 0;
  transition: opacity 0.5s ease;
}

.login-card:hover::before {
  opacity: 1;
}

/* 添加形变过渡效果 */
.login-card.register-mode .form-container {
  border-radius: 0 20px 20px 0;
  box-shadow: -5px 0 15px rgba(0, 0, 0, 0.1);
}

.login-card:not(.register-mode) .form-container {
  border-radius: 20px 0 0 20px;
  box-shadow: 5px 0 15px rgba(0, 0, 0, 0.1);
}

.login-card.register-mode .info-container {
  border-radius: 20px 0 0 20px;
}

.login-card:not(.register-mode) .info-container {
  border-radius: 0 20px 20px 0;
}

.logo {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  transition: all 0.3s ease;
}

.logo-img {
  width: 30px;
  height: 30px;
  margin-right: 10px;
  transition: all 0.3s ease;
}

.logo span {
  font-size: 18px;
  font-weight: bold;
  color: #0d47a1;
  transition: all 0.3s ease;
}

.section-title {
  font-size: 26px;
  font-weight: 700;
  color: #01579b;
  margin-bottom: 30px;
  transition: all 0.5s ease;
}

.social-login {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-bottom: 20px;
  transition: all 0.4s ease;
}

.social-btn {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e1e8ed;
  background: white;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  font-size: 20px;
  will-change: transform, box-shadow;
}

.social-btn.wechat {
  color: #1976d2;
}

.social-btn.qq {
  color: #0288d1;
}

.social-btn.alipay {
  color: #039be5;
}

.social-btn:hover {
  transform: translateY(-5px) scale(1.05);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.divider {
  position: relative;
  text-align: center;
  margin: 20px 0;
  transition: all 0.4s ease;
}

.divider::before,
.divider::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 35%;
  height: 1px;
  background: #e1e8ed;
  transition: all 0.4s ease;
}

.divider::before {
  left: 0;
}

.divider::after {
  right: 0;
}

.divider span {
  background: white;
  padding: 0 15px;
  font-size: 14px;
  color: #999;
  position: relative;
  z-index: 1;
  transition: all 0.4s ease;
}

/* 表单滑动效果 */
.forms-container {
  position: relative;
  overflow: hidden;
  margin-bottom: 20px;
}

.forms-slider {
  display: flex;
  width: 200%;
  transition: transform 0.7s cubic-bezier(0.645, 0.045, 0.355, 1);
  will-change: transform;
}

.login-form, .register-form {
  width: 50%;
  padding-right: 20px;
  transition: opacity 0.4s ease;
}

.login-form {
  opacity: 1;
  transition: opacity 0.4s ease 0.3s;
}

.register-form {
  opacity: 1;
  transition: opacity 0.4s ease 0.3s;
}

.login-card.register-mode .login-form {
  opacity: 0.5;
  transition: opacity 0.4s ease;
}

.login-card:not(.register-mode) .register-form {
  opacity: 0.5;
  transition: opacity 0.4s ease;
}

.form-group {
  margin-bottom: 20px;
  transition: all 0.4s ease;
  transform: translateZ(0);
}

.input-field {
  display: flex;
  align-items: center;
  border: 1px solid #e1e8ed;
  border-radius: 50px;
  padding: 0 20px;
  height: 50px;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  background: #f7fbff;
  will-change: transform, box-shadow;
}

.input-field:focus-within {
  border-color: #1976d2;
  box-shadow: 0 0 0 3px rgba(25, 118, 210, 0.15);
  background: white;
  transform: translateY(-2px);
}

.field-icon {
  margin-right: 10px;
  font-size: 18px;
  color: #999;
  font-style: normal;
  transition: all 0.3s ease;
}

.input-field:focus-within .field-icon {
  color: #1976d2;
}

.input-field input {
  flex: 1;
  border: none;
  outline: none;
  height: 100%;
  font-size: 16px;
  color: #333;
  background: transparent;
  transition: all 0.3s ease;
}

.toggle-password {
  cursor: pointer;
  font-size: 18px;
  color: #999;
  transition: all 0.3s ease;
}

.toggle-password:hover {
  color: #1976d2;
  transform: scale(1.1);
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  font-size: 14px;
  transition: all 0.4s ease;
}

.remember-me {
  display: flex;
  align-items: center;
  transition: all 0.3s ease;
}

.remember-me input {
  margin-right: 8px;
  transition: all 0.3s ease;
}

.forgot-link {
  color: #1976d2;
  text-decoration: none;
  transition: all 0.3s ease;
}

.forgot-link:hover {
  text-decoration: underline;
  transform: translateX(3px);
}

.submit-button {
  width: 100%;
  height: 50px;
  background: #1976d2;
  color: white;
  border: none;
  border-radius: 50px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  margin-bottom: 20px;
  position: relative;
  overflow: hidden;
  will-change: transform, box-shadow;
}

.submit-button::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 5px;
  height: 5px;
  background: rgba(255, 255, 255, 0.5);
  opacity: 0;
  border-radius: 100%;
  transform: scale(1, 1) translate(-50%);
  transform-origin: 50% 50%;
}

.submit-button:hover {
  background: #1565c0;
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 10px 25px rgba(25, 118, 210, 0.4);
}

.submit-button:hover::after {
  animation: ripple 1s ease-out;
}

@keyframes ripple {
  0% {
    transform: scale(0, 0);
    opacity: 0.5;
  }
  100% {
    transform: scale(20, 20);
    opacity: 0;
  }
}

.submit-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.switch-text {
  text-align: center;
  font-size: 14px;
  color: #666;
  transition: all 0.4s ease;
}

.switch-link {
  color: #1976d2;
  text-decoration: none;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  padding-bottom: 2px;
}

.switch-link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 1px;
  background: #1976d2;
  transition: width 0.3s ease;
}

.switch-link:hover::after {
  width: 100%;
}

.info-content {
  position: relative;
  z-index: 1;
  text-align: center;
  transition: all 0.6s cubic-bezier(0.68, -0.55, 0.265, 1.55);
  transform: translateZ(0);
}

.login-card.register-mode .info-content {
  animation: contentFade 0.8s ease forwards;
}

.login-card:not(.register-mode) .info-content {
  animation: contentFade 0.8s ease forwards;
}

@keyframes contentFade {
  0% {
    opacity: 0.8;
    transform: scale(0.95);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}

.info-title {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 20px;
  transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  opacity: 0;
  animation: fadeUp 0.5s 0.3s forwards;
}

.info-description {
  font-size: 18px;
  font-weight: 300;
  margin-bottom: 40px;
  opacity: 0;
  transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  animation: fadeUp 0.5s 0.4s forwards;
}

@keyframes fadeUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.action-button {
  background: transparent;
  color: white;
  border: 2px solid white;
  border-radius: 50px;
  padding: 12px 30px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  opacity: 0;
  animation: fadeUp 0.5s 0.5s forwards;
  position: relative;
  overflow: hidden;
}

.action-button::after {
  content: '';
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: -100%;
  background: rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
  z-index: -1;
}

.action-button:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-3px) scale(1.03);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.action-button:hover::after {
  left: 100%;
}

/* 插图动画 */
.illustration {
  position: relative;
  margin-top: 60px;
  height: 200px;
  opacity: 0;
  animation: fadeUp 0.5s 0.6s forwards;
}

.plane-icon, .cloud, .ticket, .globe {
  position: absolute;
  font-size: 30px;
  transition: all 0.3s ease;
}

.plane-icon {
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 50px;
  animation: float 4s ease-in-out infinite;
  filter: drop-shadow(0 5px 15px rgba(255, 255, 255, 0.3));
}

.cloud {
  opacity: 0.8;
  filter: drop-shadow(0 5px 10px rgba(255, 255, 255, 0.2));
}

.cloud-1 {
  top: 20%;
  left: 20%;
  animation: float 5s ease-in-out infinite;
}

.cloud-2 {
  top: 30%;
  right: 20%;
  animation: float 7s ease-in-out infinite 1s;
}

.cloud-3 {
  bottom: 20%;
  left: 30%;
  animation: float 6s ease-in-out infinite 0.5s;
}

.ticket {
  font-size: 25px;
  filter: drop-shadow(0 5px 10px rgba(0, 0, 0, 0.1));
}

.ticket-1 {
  top: 60%;
  right: 25%;
  animation: spin 10s linear infinite;
  color: #81d4fa;
}

.ticket-2 {
  bottom: 15%;
  right: 40%;
  animation: spin 8s linear infinite reverse;
  color: #4fc3f7;
}

.globe {
  bottom: 10%;
  right: 10%;
  animation: pulse 3s ease-in-out infinite;
  filter: drop-shadow(0 5px 15px rgba(255, 255, 255, 0.2));
  color: #e3f2fd;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-20px) rotate(3deg);
  }
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

/* 页脚 */
.footer {
  margin-top: 20px;
  text-align: center;
  color: #999;
  font-size: 14px;
  transition: all 0.3s ease;
}

/* 响应式调整 */
@media (max-width: 900px) {
  .login-card, 
  .login-card.register-mode {
    flex-direction: column-reverse;
    animation: none;
    transform: none !important;
  }
  
  .login-card::before {
    left: 0;
    top: 50%;
    width: 100%;
    height: 2px;
    background: linear-gradient(to right, transparent, rgba(25, 118, 210, 0.5), transparent);
  }
  
  .form-container, 
  .info-container {
    width: 100%;
    transform: none !important;
    animation: none;
  }
  
  .login-card.register-mode .form-container,
  .login-card:not(.register-mode) .form-container,
  .login-card.register-mode .info-container,
  .login-card:not(.register-mode) .info-container {
    border-radius: 0;
    box-shadow: none;
  }
  
  .form-container {
    border-radius: 0 0 20px 20px !important;
  }
  
  .info-container {
    border-radius: 20px 20px 0 0 !important;
  }
  
  @keyframes formSlideDown {
    0% {
      transform: translateY(-100px);
      opacity: 0.5;
    }
    100% {
      transform: translateY(0);
      opacity: 1;
    }
  }
  
  @keyframes infoSlideUp {
    0% {
      transform: translateY(100px);
      opacity: 0.5;
    }
    100% {
      transform: translateY(0);
      opacity: 1;
    }
  }
  
  .login-card:not(.register-mode) .form-container,
  .login-card.register-mode .form-container {
    animation: formSlideDown 0.8s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
  }
  
  .login-card:not(.register-mode) .info-container,
  .login-card.register-mode .info-container {
    animation: infoSlideUp 0.8s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
  }
}

@media (max-width: 480px) {
  .form-section {
    padding: 30px 20px;
  }
  
  .info-title {
    font-size: 28px;
  }
  
  .info-description {
    font-size: 16px;
    margin-bottom: 30px;
  }
  
  .illustration {
    height: 150px;
  }
}
</style> 