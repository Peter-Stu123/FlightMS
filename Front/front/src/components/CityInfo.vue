<template>
  <div class="city-info-container">
    <h2>城市信息</h2>
    <el-table :data="cities" style="width: 100%" v-loading="loading">
      <el-table-column prop="cityId" label="城市ID" width="180"></el-table-column>
      <el-table-column prop="cityName" label="城市名称" width="180"></el-table-column>
      <el-table-column prop="province" label="省份"></el-table-column>
    </el-table>
    <el-alert v-if="error" :title="error" type="error" show-icon :closable="false"></el-alert>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { ElTable, ElTableColumn, ElAlert, ElLoading } from 'element-plus';

const cities = ref([]);
const loading = ref(true);
const error = ref(null);

const fetchCities = async () => {
  loading.value = true;
  error.value = null;
  try {
    // 确保这里的URL与你的后端API端点匹配
    // 例如: http://localhost:8080/cityinfo/all (如果后端运行在8080端口且路径为/cityinfo/all)
    const response = await axios.get('http://localhost:8080/city/all'); 
    if (response.data && response.data.code === 0 && Array.isArray(response.data.data)) {
      cities.value = response.data.data;
    } else {
      console.error('获取城市数据格式不正确:', response.data);
      error.value = '获取城市数据格式不正确或未返回数据';
      cities.value = []; // 清空数据以避免显示旧数据或错误数据
    }
  } catch (err) {
    console.error('获取城市信息失败:', err);
    error.value = `获取城市信息失败: ${err.message}`;
    cities.value = []; // 清空数据
  }
  loading.value = false;
};

onMounted(() => {
  fetchCities();
});
</script>

<style scoped>
.city-info-container {
  margin: 20px;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #303133;
}
</style>