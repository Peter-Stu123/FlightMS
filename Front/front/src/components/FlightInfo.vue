<template>
  <div class="flight-management-system">
    <!-- 顶部导航栏 -->
    <div class="top-navbar">
      <div class="navbar-content">
        <div class="logo-section">
          <div class="logo-icon">✈️</div>
          <h1 class="system-title">航班管理系统</h1>
        </div>
        <div class="navbar-actions">
          <el-button type="primary" class="premium-btn" @click="handleAddFlight">
            <el-icon><Plus /></el-icon>
            添加航班
          </el-button>
          <el-tooltip content="刷新数据" placement="bottom" effect="light">
            <el-button class="refresh-btn" @click="fetchFlights">
              <el-icon><Refresh /></el-icon>
            </el-button>
          </el-tooltip>
          <el-button class="ai-btn" @click="openAiAssistant">
            <el-icon><ChatDotRound /></el-icon>
            AI助手
          </el-button>
        </div>
      </div>
    </div>

    <!-- 数据统计面板 -->
    <div class="stats-dashboard">
      <div class="dashboard-header">
        <h2 class="section-heading">数据概览</h2>
        <div class="date-display">{{ currentDate }}</div>
      </div>
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon total-flights">
            <el-icon :size="32"><Promotion /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ total }}</div>
            <div class="stat-label">总航班数</div>
            <div class="stat-progress">
              <div class="progress-bar" :style="{width: `${Math.min(100, total/2)}%`}"></div>
            </div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon active-flights">
            <span class="custom-icon">🟢</span>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ activeFlights }}</div>
            <div class="stat-label">活跃航班</div>
            <div class="stat-progress">
              <div class="progress-bar" :style="{width: `${Math.min(100, (activeFlights/total)*100)}%`}"></div>
            </div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon total-seats">
            <span class="custom-icon">💺</span>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ totalSeats }}</div>
            <div class="stat-label">总座位数</div>
            <div class="stat-progress">
              <div class="progress-bar" :style="{width: `${Math.min(100, totalSeats/1000)}%`}"></div>
            </div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon routes">
            <span class="custom-icon">🗺️</span>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ uniqueRoutes }}</div>
            <div class="stat-label">航线数量</div>
            <div class="stat-progress">
              <div class="progress-bar" :style="{width: `${Math.min(100, (uniqueRoutes/20)*100)}%`}"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 甘特图区域优化 -->
    <div class="gantt-chart-section">
      <div class="section-card">
        <div class="chart-header">
          <h2 class="section-heading">机场航班甘特图</h2>
          <div class="chart-controls">
            <el-dropdown @command="handleChartView" trigger="click">
              <el-button type="primary" plain size="small" class="view-selector">
                视图选择
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="today">今日视图</el-dropdown-item>
                  <el-dropdown-item command="week">周视图</el-dropdown-item>
                  <el-dropdown-item command="month">月视图</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
        <div ref="ganttChartContainer" class="gantt-chart-container"></div>
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="search-filter-section">
      <div class="search-container">
        <el-input
          v-model="searchQuery"
          placeholder="搜索航班号、城市..."
          class="search-input"
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #suffix>
            <el-icon v-if="searchQuery" class="clear-icon" @click="searchQuery = ''"><Close /></el-icon>
          </template>
        </el-input>
      </div>
      <div class="filter-container">
        <el-select v-model="filterStartCity" placeholder="出发城市" clearable class="filter-select" @change="handleFilter">
          <template #prefix>
            <el-icon><LocationFilled /></el-icon>
          </template>
          <el-option label="全部城市" value=""></el-option>
          <el-option v-for="city in uniqueCities" :key="city" :label="city" :value="city"></el-option>
        </el-select>
        <el-select v-model="filterEndCity" placeholder="抵达城市" clearable class="filter-select" @change="handleFilter">
          <template #prefix>
            <el-icon><Location /></el-icon>
          </template>
          <el-option label="全部城市" value=""></el-option>
          <el-option v-for="city in uniqueCities" :key="city" :label="city" :value="city"></el-option>
        </el-select>
        <el-date-picker
          v-model="filterDateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          :shortcuts="dateRangeShortcuts"
          class="filter-date"
          @change="handleFilter"
          clearable
        >
          <template #prefix>
            <el-icon><Calendar /></el-icon>
          </template>
        </el-date-picker>
        <el-button class="filter-reset" @click="resetFilters">
          <el-icon><Refresh /></el-icon>
          重置筛选
        </el-button>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <div class="table-container">
        <div class="table-header">
          <h2 class="section-heading">航班列表</h2>
          <div class="table-actions">
            <el-button size="small" @click="exportData" class="export-btn">
              <el-icon><Download /></el-icon>
              导出数据
            </el-button>
          </div>
        </div>
        <el-table 
          :data="filteredFlights" 
          style="width: 100%" 
          v-loading="loading"
          element-loading-text="加载航班数据中..."
          element-loading-background="rgba(10, 36, 99, 0.7)"
          class="premium-table"
          :row-class-name="getRowClassName"
          @row-click="handleRowClick"
        >
          <el-table-column prop="flightid" label="航班ID" width="80" sortable>
            <template #default="scope">
              <div class="flight-id">{{ scope.row.flightid }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="flightnum" label="航班号" width="120" sortable>
            <template #default="scope">
              <div class="flight-number">
                <span class="flight-code">{{ scope.row.flightnum }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="flydate" label="起飞日期" width="120" sortable>
            <template #default="scope">
              <div class="date-display-cell">
                <el-icon><Calendar /></el-icon>
                {{ scope.row.flydate }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="starttime" label="起飞时间" width="100">
            <template #default="scope">
              <div class="time-display">
                <el-icon><Clock /></el-icon>
                {{ scope.row.starttime }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="flytime" label="飞行时长" width="100">
            <template #default="scope">
              <el-tag class="duration-tag">{{ scope.row.flytime }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="航线" width="200">
            <template #default="scope">
              <div class="route-display">
                <div class="city-badge start">{{ scope.row.startCityName }}</div>
                <el-icon class="route-arrow"><ArrowRight /></el-icon>
                <div class="city-badge end">{{ scope.row.endCityName }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="seatnum" label="座位数" width="100">
            <template #default="scope">
              <div class="seat-info">
                <el-icon><User /></el-icon>
                {{ scope.row.seatnum }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getFlightStatus(scope.row).type" class="status-tag" :effect="getFlightStatus(scope.row).effect">
                <el-icon class="status-icon" v-if="getFlightStatus(scope.row).icon">
                  <component :is="getFlightStatus(scope.row).icon"></component>
                </el-icon>
                {{ getFlightStatus(scope.row).text }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="scope">
              <div class="action-buttons">
                <el-tooltip content="编辑航班" placement="top" effect="light">
                  <el-button size="small" type="primary" @click.stop="handleEditFlight(scope.row)" class="action-btn">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="查看详情" placement="top" effect="light">
                  <el-button size="small" type="info" @click.stop="handleViewDetails(scope.row)" class="action-btn">
                    <el-icon><View /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="删除航班" placement="top" effect="light">
                  <el-button size="small" type="danger" @click.stop="handleDeleteFlight(scope.row.flightid)" class="action-btn">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页组件 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[5, 10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            class="premium-pagination"
            background
          />
        </div>
      </div>
    </div>
    
    <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" class="error-alert"></el-alert>

    <!-- 添加/编辑航班抽屉 -->
    <el-drawer
      v-model="addDrawerVisible" 
      :title="isEditing ? '编辑航班信息' : '添加航班信息'"
      direction="rtl" 
      size="600px"
      class="premium-drawer"
    >
      <div class="drawer-content">
        <el-form :model="addFlightForm" ref="addFlightFormRef" label-width="120px" class="premium-form">
          <div class="form-section">
            <h3 class="section-title">基本信息</h3>
            <el-form-item label="航班号" prop="flightnum" :rules="[{ required: true, message: '请输入航班号', trigger: 'blur' }]">
              <el-input v-model="addFlightForm.flightnum" placeholder="例如：CA1234" class="premium-input">
                <template #prefix>
                  <el-icon><LocationFilled /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="起飞日期" prop="flydate" :rules="[{ required: true, message: '请选择起飞日期', trigger: 'change' }]">
              <el-date-picker 
                v-model="addFlightForm.flydate" 
                type="date" 
                placeholder="选择起飞日期" 
                format="YYYY-MM-DD" 
                value-format="YYYY-MM-DD"
                class="premium-input"
              ></el-date-picker>
            </el-form-item>
            <el-form-item label="起飞时间" prop="starttime" :rules="[{ required: true, message: '请输入起飞时间', trigger: 'blur' }]">
              <el-time-picker 
                v-model="addFlightForm.starttime" 
                placeholder="选择起飞时间" 
                format="HH:mm" 
                value-format="HH:mm"
                class="premium-input"
              ></el-time-picker>
            </el-form-item>
            <el-form-item label="飞行时长" prop="flytime" :rules="[{ required: true, message: '请输入飞行时长', trigger: 'blur' }]">
              <el-input v-model="addFlightForm.flytime" placeholder="例如：2h30m" class="premium-input">
                <template #prefix>
                  <el-icon><Clock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </div>
          
          <div class="form-section">
            <h3 class="section-title">航线信息</h3>
            <el-form-item label="出发城市" prop="startCityName" :rules="[{ required: true, message: '请输入出发城市名称', trigger: 'blur' }]">
              <el-input v-model="addFlightForm.startCityName" placeholder="例如：北京" class="premium-input">
                <template #prefix>
                  <el-icon><LocationFilled /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="到达城市" prop="endCityName" :rules="[{ required: true, message: '请输入到达城市名称', trigger: 'blur' }]">
              <el-input v-model="addFlightForm.endCityName" placeholder="例如：上海" class="premium-input">
                <template #prefix>
                  <el-icon><Location /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </div>
          
          <div class="form-section">
            <h3 class="section-title">座位配置</h3>
            <el-form-item label="座位数" prop="seatnum" :rules="[{ required: true, message: '请输入座位数', trigger: 'blur' }, { type: 'number', message: '座位数必须为数字'}]">
              <el-input v-model.number="addFlightForm.seatnum" placeholder="例如：180" class="premium-input">
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="座位类型" prop="seatType">
              <el-select v-model="addFlightForm.seatType" placeholder="选择座位类型" class="premium-input">
                <el-option label="经济舱" value="economy"></el-option>
                <el-option label="商务舱" value="business"></el-option>
                <el-option label="头等舱" value="first"></el-option>
                <el-option label="混合配置" value="mixed"></el-option>
              </el-select>
            </el-form-item>
          </div>
        </el-form>
      </div>
      <template #footer>
        <div class="drawer-footer">
          <el-button @click="addDrawerVisible = false" class="cancel-btn">取消</el-button>
          <el-button type="primary" @click="submitAddFlight" class="submit-btn">
            <el-icon><Check /></el-icon>
            {{ isEditing ? '更新' : '添加' }}
          </el-button>
        </div>
      </template>
    </el-drawer>

    <!-- 航班详情对话框优化 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="航班详情"
      width="600px"
      class="detail-dialog"
      :show-close="true"
      :close-on-click-modal="true"
      :close-on-press-escape="true"
    >
      <div v-if="selectedFlight" class="flight-details">
        <div class="detail-header">
          <div class="flight-info-header">
            <div class="flight-title">
              <div class="flight-badge">{{ selectedFlight.flightnum }}</div>
              <el-tag :type="getFlightStatus(selectedFlight).type" class="detail-status-tag">
                <el-icon v-if="getFlightStatus(selectedFlight).icon">
                  <component :is="getFlightStatus(selectedFlight).icon"></component>
                </el-icon>
                {{ getFlightStatus(selectedFlight).text }}
              </el-tag>
            </div>
            <div class="flight-actions">
              <el-button type="primary" size="small" @click="handleEditFlight(selectedFlight)">
                <el-icon><Edit /></el-icon> 编辑航班
              </el-button>
            </div>
          </div>
        </div>
        <div class="detail-content">
          <div class="detail-section">
            <h4 class="section-title">航班信息</h4>
            <div class="detail-grid">
              <div class="detail-item">
                <span class="label">航班ID:</span>
                <span class="value">{{ selectedFlight.flightid }}</span>
              </div>
              <div class="detail-item">
                <span class="label">起飞日期:</span>
                <span class="value">{{ selectedFlight.flydate }}</span>
              </div>
              <div class="detail-item">
                <span class="label">起飞时间:</span>
                <span class="value">{{ selectedFlight.starttime }}</span>
              </div>
              <div class="detail-item">
                <span class="label">飞行时长:</span>
                <span class="value">{{ selectedFlight.flytime }}</span>
              </div>
            </div>
          </div>
          <div class="detail-section">
            <h4 class="section-title">航线信息</h4>
            <div class="route-info">
              <div class="route-point">
                <div class="route-icon start">
                  <el-icon><LocationFilled /></el-icon>
                </div>
                <div class="city-info">
                  <strong>{{ selectedFlight.startCityName }}</strong>
                  <span class="departure-label">出发</span>
                </div>
              </div>
              <div class="route-line">
                <div class="route-line-inner"></div>
                <el-icon class="route-plane"><Promotion /></el-icon>
              </div>
              <div class="route-point">
                <div class="route-icon end">
                  <el-icon><Location /></el-icon>
                </div>
                <div class="city-info">
                  <strong>{{ selectedFlight.endCityName }}</strong>
                  <span class="arrival-label">到达</span>
                </div>
              </div>
            </div>
          </div>
          <div class="detail-section">
            <h4 class="section-title">座位信息</h4>
            <div class="seat-details">
              <div class="seat-stat">
                <el-icon><User /></el-icon>
                <span>总座位数: {{ selectedFlight.seatnum }}</span>
              </div>
              <div class="seat-chart">
                <div class="seat-progress">
                  <div class="seat-progress-bar" :style="{width: `${Math.min(100, (selectedFlight.seatnum/300)*100)}%`}"></div>
                </div>
                <div class="seat-labels">
                  <span>0</span>
                  <span>150</span>
                  <span>300</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- AI助手对话框优化 -->
    <el-drawer
      v-model="aiDrawerVisible"
      title="智能航班助手"
      direction="rtl"
      size="500px"
      class="ai-drawer"
      :modal-append-to-body="false"
      :before-close="closeAiAssistant"
    >
      <template #header>
        <div class="ai-drawer-header">
          <div class="ai-avatar">
            <el-icon><ChatLineRound /></el-icon>
          </div>
          <div class="ai-title">
            <h3>智能航班助手</h3>
            <div class="ai-subtitle">由AI提供支持，连接实时数据库</div>
          </div>
        </div>
      </template>
      
      <div class="ai-assistant-container">
        <div class="ai-chat-window" ref="chatWindow">
          <div v-if="chatMessages.length === 0" class="empty-chat">
            <div class="empty-icon">💬</div>
            <div class="empty-text">您好！我是航班管理系统的智能助手。有任何关于航班、航线或系统操作的问题，请随时向我咨询。</div>
          </div>
          
          <div v-for="(message, index) in chatMessages" :key="index" :class="['message-bubble', message.type]">
            <div class="message-avatar" v-if="message.type === 'ai'">
              <el-icon><ChatLineRound /></el-icon>
            </div>
            <div class="message-content">
              <div class="message-text" v-html="formatMessage(message.content)"></div>
              <div class="message-time">{{ message.time }}</div>
            </div>
          </div>
          <div class="typing-indicator" v-if="aiIsTyping">
            <div class="typing-dot"></div>
            <div class="typing-dot"></div>
            <div class="typing-dot"></div>
          </div>
        </div>
        <div class="ai-input-container">
          <el-input
            v-model="userInput"
            placeholder="输入您的问题..."
            class="ai-input"
            :disabled="aiIsTyping"
            @keyup.enter="sendMessage"
          >
            <template #prefix>
              <el-icon><ChatDotRound /></el-icon>
            </template>
            <template #append>
              <el-button :disabled="aiIsTyping || !userInput.trim()" @click="sendMessage">
                <el-icon><Position /></el-icon>
              </el-button>
            </template>
          </el-input>
          <div class="ai-suggestions">
            <el-tag 
              v-for="(suggestion, index) in suggestions" 
              :key="index"
              class="suggestion-tag"
              @click="useSuggestion(suggestion)"
            >
              {{ suggestion }}
            </el-tag>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed, nextTick, onBeforeUnmount } from 'vue';
import axios from 'axios';
import * as echarts from 'echarts'; // 引入 ECharts
import { 
  ElTable, ElTableColumn, ElAlert, ElLoading, ElButton, ElDrawer, ElForm, ElFormItem, 
  ElInput, ElDatePicker, ElTimePicker, ElMessage, ElMessageBox, ElPagination, ElSelect, 
  ElOption, ElTag, ElIcon, ElDialog, ElTooltip, ElBadge, ElAvatar, ElCheckbox,
  ElDropdown, ElDropdownMenu, ElDropdownItem
} from 'element-plus';
import { 
  Plus, Refresh, Search, Clock, ArrowRight, User, Edit, View, Delete, Check,
  Location, LocationFilled, ChatDotRound, ChatLineRound, Position, Promotion,
  DataAnalysis, Calendar, CaretTop, CaretBottom, Right, Setting, SwitchButton,
  VideoCameraFilled, CreditCard, TrendCharts, ArrowDown, Download, Loading, 
  Close, Document
} from '@element-plus/icons-vue';

// 基础数据
const flights = ref([]);
const loading = ref(true);
const error = ref(null);
const isDataLoading = ref(false);
const showUserMenu = ref(false);

// 抽屉和对话框控制
const addDrawerVisible = ref(false);
const detailDialogVisible = ref(false);
const addFlightFormRef = ref(null);
const isEditing = ref(false);
const selectedFlight = ref(null);

// 搜索和筛选
const searchQuery = ref('');
const filterStartCity = ref('');
const filterEndCity = ref('');
const filterDateRange = ref(null); // 添加日期范围选择

// 分页相关变量
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 表单数据
const initialAddFlightForm = () => ({
  flightnum: '',
  flydate: '',
  starttime: '',
  flytime: '',
  startCityName: '',
  endCityName: '',
  seatnum: null,
  seatType: 'economy'
});

const addFlightForm = reactive(initialAddFlightForm());

// 计算属性
const filteredFlights = computed(() => {
  let result = flights.value;
  
  // 搜索过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(flight => 
      flight.flightnum.toLowerCase().includes(query) ||
      flight.startCityName.toLowerCase().includes(query) ||
      flight.endCityName.toLowerCase().includes(query)
    );
  }
  
  // 出发城市过滤
  if (filterStartCity.value) {
    result = result.filter(flight => flight.startCityName === filterStartCity.value);
  }
  
  // 抵达城市过滤
  if (filterEndCity.value) {
    result = result.filter(flight => flight.endCityName === filterEndCity.value);
  }
  
  // 日期范围过滤
  if (filterDateRange.value && filterDateRange.value.length === 2) {
    const startDate = filterDateRange.value[0];
    const endDate = filterDateRange.value[1];
    
    result = result.filter(flight => {
      return flight.flydate >= startDate && flight.flydate <= endDate;
    });
  }
  
  // 按航班状态排序：今日航班 -> 计划中 -> 已完成
  const today = new Date().toISOString().split('T')[0];
  
  // 对结果进行排序
  result.sort((a, b) => {
    const statusA = a.flydate === today ? 0 : (a.flydate > today ? 1 : 2);
    const statusB = b.flydate === today ? 0 : (b.flydate > today ? 1 : 2);
    
    // 首先按状态排序
    if (statusA !== statusB) {
      return statusA - statusB;
    }
    
    // 如果状态相同，则按日期升序排序
    if (a.flydate !== b.flydate) {
      return a.flydate.localeCompare(b.flydate);
    }
    
    // 如果日期也相同，则按起飞时间排序
    return a.starttime.localeCompare(b.starttime);
  });
  
  return result;
});

// 统计数据
const activeFlights = computed(() => {
  const today = new Date().toISOString().split('T')[0];
  return flights.value.filter(flight => flight.flydate >= today).length;
});

const totalSeats = computed(() => {
  return flights.value.reduce((sum, flight) => sum + (flight.seatnum || 0), 0);
});

const uniqueRoutes = computed(() => {
  const routes = new Set();
  flights.value.forEach(flight => {
    routes.add(`${flight.startCityName}-${flight.endCityName}`);
  });
  return routes.size;
});

const uniqueCities = computed(() => {
  const cities = new Set();
  flights.value.forEach(flight => {
    cities.add(flight.startCityName);
    cities.add(flight.endCityName);
  });
  return Array.from(cities);
});

// 用户信息
const username = computed(() => {
  return localStorage.getItem('username') || '用户';
});

const userInitials = computed(() => {
  return username.value.slice(0, 2).toUpperCase();
});

// 当前日期
const currentDate = computed(() => {
  const now = new Date();
  const options = { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' };
  return now.toLocaleDateString('zh-CN', options);
});

// 处理表格行点击
const handleRowClick = (row) => {
  handleViewDetails(row);
};

// 处理甘特图视图切换
const handleChartView = (command) => {
  switch(command) {
    case 'today':
      ElMessage.success('已切换到今日视图');
      break;
    case 'week':
      ElMessage.success('已切换到周视图');
      break;
    case 'month':
      ElMessage.success('已切换到月视图');
      break;
  }
  // 实际实现中这里需要重新配置和绘制甘特图
};

// 日期范围快捷选项
const dateRangeShortcuts = [
  {
    text: '今天',
    value: () => {
      const today = new Date();
      return [today, today];
    }
  },
  {
    text: '本周',
    value: () => {
      const end = new Date();
      const start = new Date();
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
      return [start, end];
    }
  },
  {
    text: '未来7天',
    value: () => {
      const start = new Date();
      const end = new Date();
      end.setTime(start.getTime() + 3600 * 1000 * 24 * 6);
      return [start, end];
    }
  },
  {
    text: '未来30天',
    value: () => {
      const start = new Date();
      const end = new Date();
      end.setTime(start.getTime() + 3600 * 1000 * 24 * 29);
      return [start, end];
    }
  },
  {
    text: '上个月',
    value: () => {
      const end = new Date();
      const start = new Date();
      start.setMonth(start.getMonth() - 1);
      start.setDate(1);
      end.setDate(0);
      return [start, end];
    }
  }
];

// 切换用户菜单
const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value;
};

// 处理登出
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('isLoggedIn');
    localStorage.removeItem('username');
    localStorage.removeItem('rememberMe');
    window.location.href = '/#/login';
    ElMessage.success('已安全退出系统');
  }).catch(() => {});
};

// 点击其他地方关闭用户菜单
const closeUserMenuOnClickOutside = (e) => {
  if (showUserMenu.value && !e.target.closest('.user-profile')) {
    showUserMenu.value = false;
  }
};

onMounted(() => {
  document.addEventListener('click', closeUserMenuOnClickOutside);
  fetchFlights();
  
  // 确保在DOM完全更新后初始化图表
  nextTick(() => {
    setTimeout(() => {
      console.log("DOM已更新，开始初始化甘特图");
      initGanttChart(); 
    }, 500); // 延迟500ms确保DOM完全渲染
  });
  
  // 添加键盘快捷键支持
  document.addEventListener('keydown', handleKeyboardShortcuts);
});

// 处理键盘快捷键
const handleKeyboardShortcuts = (e) => {
  // Alt + N: 添加新航班
  if (e.altKey && e.key === 'n') {
    e.preventDefault();
    handleAddFlight();
  }
  
  // Alt + R: 刷新航班数据
  if (e.altKey && e.key === 'r') {
    e.preventDefault();
    fetchFlights();
  }
  
  // Alt + F: 聚焦搜索框
  if (e.altKey && e.key === 'f') {
    e.preventDefault();
    document.querySelector('.search-input input').focus();
  }
  
  // Esc: 关闭所有对话框
  if (e.key === 'Escape') {
    if (addDrawerVisible.value) addDrawerVisible.value = false;
    if (detailDialogVisible.value) detailDialogVisible.value = false;
    if (showUserMenu.value) showUserMenu.value = false;
  }
};

// 确保组件卸载时清理资源
onBeforeUnmount(() => {
  document.removeEventListener('click', closeUserMenuOnClickOutside);
  document.removeEventListener('keydown', handleKeyboardShortcuts);
  
  if (myGanttChart) {
    myGanttChart.dispose();
    myGanttChart = null;
  }
  
  // 移除resize监听器
  if (ganttChartContainer.value && ganttChartContainer.value._resizeHandler) {
    window.removeEventListener('resize', ganttChartContainer.value._resizeHandler);
    ganttChartContainer.value._resizeHandler = null;
  }
});

// 分页查询航班信息
const fetchFlights = async () => {
  loading.value = true;
  isDataLoading.value = true;
  error.value = null;
  
  try {
    const response = await axios.get('/flight/flightsByPage', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value
      }
    });
    if (response.data && response.data.code === 0 && response.data.data) {
      const pageData = response.data.data;
      flights.value = pageData.records || [];
      total.value = pageData.total || 0;
      
      // 获取数据后更新甘特图
      nextTick(() => {
        updateGanttChart();
      });
    } else {
      console.error('获取航班数据格式不正确:', response.data);
      error.value = '获取航班数据格式不正确或未返回数据';
      flights.value = [];
      total.value = 0;
    }
  } catch (err) {
    console.error('获取航班信息失败:', err);
    error.value = `获取航班信息失败: ${err.message}`;
    flights.value = [];
    total.value = 0;
  } finally {
  loading.value = false;
    // 稍微延迟以便显示加载动画效果
    setTimeout(() => {
      isDataLoading.value = false;
    }, 600);
  }
};

// 处理页码变化
const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
  fetchFlights();
};

// 处理每页显示条数变化
const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  currentPage.value = 1; // 重置为第一页
  fetchFlights();
};

// 搜索和筛选方法
const handleSearch = () => {
  // 搜索时重置分页
  currentPage.value = 1;
};

const handleFilter = () => {
  // 筛选时重置分页
  currentPage.value = 1;
};

// 获取航班状态
const getFlightStatus = (flight) => {
  const today = new Date().toISOString().split('T')[0];
  const flightDate = flight.flydate;
  
  if (flightDate < today) {
    return { 
      type: 'info', 
      text: '已完成', 
      effect: 'plain',
      icon: 'Check'
    };
  } else if (flightDate === today) {
    return { 
      type: 'warning', 
      text: '今日航班',
      effect: 'light',
      icon: 'Timer' 
    };
  } else {
    return { 
      type: 'success', 
      text: '计划中',
      effect: 'dark',
      icon: 'Calendar'
    };
  }
};

// 获取表格行样式
const getRowClassName = ({ row }) => {
  const status = getFlightStatus(row);
  return `flight-row-${status.type}`;
};

// 添加航班
const handleAddFlight = () => {
  Object.assign(addFlightForm, initialAddFlightForm());
  isEditing.value = false;
  addDrawerVisible.value = true;
  if (addFlightFormRef.value) {
    addFlightFormRef.value.clearValidate();
  }
};

// 编辑航班
const handleEditFlight = (flight) => {
  Object.assign(addFlightForm, {
    flightid: flight.flightid,
    flightnum: flight.flightnum,
    flydate: flight.flydate,
    starttime: flight.starttime,
    flytime: flight.flytime,
    startCityName: flight.startCityName,
    endCityName: flight.endCityName,
    seatnum: flight.seatnum,
    seatType: flight.seatType || 'economy'
  });
  isEditing.value = true;
  addDrawerVisible.value = true;
  if (addFlightFormRef.value) {
    addFlightFormRef.value.clearValidate();
  }
};

// 查看详情
const handleViewDetails = (flight) => {
  selectedFlight.value = flight;
  detailDialogVisible.value = true;
};

// 提交表单
const submitAddFlight = async () => {
  if (!addFlightFormRef.value) return;
  addFlightFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let response;
        if (isEditing.value) {
          // 编辑模式 - 这里需要后端提供更新接口
          response = await axios.put(`/flight/updateFlight/${addFlightForm.flightid}`, addFlightForm);
        } else {
          // 添加模式
          response = await axios.post('/flight/addFlight', addFlightForm);
        }
        
        if (response.data && response.data.code === 0) {
          ElMessage.success(isEditing.value ? '更新航班成功' : '添加航班成功');
          addDrawerVisible.value = false;
          fetchFlights();
        } else {
          ElMessage.error(response.data.msg || (isEditing.value ? '更新航班失败' : '添加航班失败'));
        }
      } catch (err) {
        console.error(isEditing.value ? '更新航班失败:' : '添加航班失败:', err);
        ElMessage.error(`${isEditing.value ? '更新' : '添加'}航班失败: ${err.message}`);
      }
    } else {
      console.log('表单校验失败');
      return false;
    }
  });
};

const handleDeleteFlight = async (flightid) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该航班信息吗?',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );
    // 用户点击了确定
    const response = await axios.delete(`/flight/deleteFlight/${flightid}`);
    if (response.data && response.data.code === 0) {
      ElMessage.success('删除航班成功');
      fetchFlights(); // 重新获取列表
    } else {
      ElMessage.error(response.data.msg || '删除航班失败');
    }
  } catch (err) {
    // 用户点击了取消或API请求失败
    if (err !== 'cancel') {
      console.error('删除航班失败:', err);
      ElMessage.error(`删除航班失败: ${err.message}`);
    }
  }
};

// AI助手相关
const aiDrawerVisible = ref(false);
const chatWindow = ref(null);
const chatMessages = ref([]);
const userInput = ref('');
const aiIsTyping = ref(false);
const suggestions = ref([
  '如何查询航班信息？',
  '我想了解航班延误情况',
  '如何添加新航班？',
  '帮我分析航线数据',
  '如何优化航班调度？'
]);

// 用于AI数据支持的变量
const aiFlightData = ref([]);
const aiCityData = ref([]);
const aiUserData = ref([]);
const aiDataLoading = ref(false);
const currentUser = ref(null);

// 获取AI所需的数据
const fetchAiData = async () => {
  aiDataLoading.value = true;
  try {
    // 获取航班数据
    const flightResponse = await axios.get('/flight/flightsList');
    if (flightResponse.data && flightResponse.data.code === 0) {
      aiFlightData.value = flightResponse.data.data || [];
      console.log('AI航班数据加载成功', aiFlightData.value.length);
    }
    
    // 获取城市数据
    const cityResponse = await axios.get('/city/citysList');
    if (cityResponse.data && cityResponse.data.code === 0) {
      aiCityData.value = cityResponse.data.data || [];
      console.log('AI城市数据加载成功', aiCityData.value.length);
    }
    
    // 获取用户数据 (根据权限可能需要限制)
    const userResponse = await axios.get('/user/usersList');
    if (userResponse.data && userResponse.data.code === 0) {
      aiUserData.value = userResponse.data.data || [];
      console.log('AI用户数据加载成功', aiUserData.value.length);
    }
    
    // 获取当前用户信息
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const userInfoResponse = await axios.get('/user/current', {
          headers: { Authorization: `Bearer ${token}` }
        });
        if (userInfoResponse.data && userInfoResponse.data.code === 0) {
          currentUser.value = userInfoResponse.data.data;
        }
      } catch (err) {
        console.warn('获取当前用户信息失败', err);
      }
    }
  } catch (err) {
    console.error('加载AI数据源失败:', err);
  }
  aiDataLoading.value = false;
};

// 保存聊天记录到本地存储
const saveChatHistory = () => {
  try {
    const userId = currentUser.value?.userId || 'guest';
    localStorage.setItem(`chat_history_${userId}`, JSON.stringify(chatMessages.value));
    console.log('聊天记录已保存');
  } catch (err) {
    console.error('保存聊天记录失败:', err);
  }
};

// 加载历史聊天记录
const loadChatHistory = () => {
  try {
    const userId = currentUser.value?.userId || 'guest';
    const savedHistory = localStorage.getItem(`chat_history_${userId}`);
    if (savedHistory) {
      chatMessages.value = JSON.parse(savedHistory);
      console.log('已加载聊天记录', chatMessages.value.length);
      return true;
    }
  } catch (err) {
    console.error('加载聊天记录失败:', err);
  }
  return false;
};

const openAiAssistant = () => {
  aiDrawerVisible.value = true;
  
  // 加载AI数据源(如果尚未加载)
  if (aiFlightData.value.length === 0) {
    fetchAiData();
  }
  
  // 尝试加载历史聊天记录
  const historyLoaded = loadChatHistory();
  
  // 如果没有历史记录，初始化欢迎消息
  if (!historyLoaded) {
    chatMessages.value = [{
      type: 'ai',
      content: '您好！我是航班管理系统的智能助手。有任何关于航班、航线或系统操作的问题，请随时向我咨询。',
      time: new Date().toLocaleTimeString()
    }];
  }
  
  userInput.value = '';
  aiIsTyping.value = false;
  scrollToBottom();
};

const closeAiAssistant = () => {
  // 关闭前保存聊天记录
  saveChatHistory();
  aiDrawerVisible.value = false;
};

const sendMessage = async () => {
  if (!userInput.value.trim()) return;
  const userMessage = {
    type: 'user',
    content: userInput.value,
    time: new Date().toLocaleTimeString()
  };
  chatMessages.value.push(userMessage);
  userInput.value = '';
  aiIsTyping.value = true;
  scrollToBottom();

  // 保存聊天记录
  saveChatHistory();
  
  // 更新相关建议
  updateSuggestions(userMessage.content);
  
  let apiSuccess = false;

  try {
    console.log('使用数据库增强的AI接口');
    
    // 直接调用连接数据库的AI接口
    const response = await fetch('http://localhost:8080/ai/chat-with-database', {
      method: 'POST',
      headers: {
        'Content-Type': 'text/plain',
        'Access-Control-Allow-Origin': '*'
      },
      body: userMessage.content,
      mode: 'cors',
    });
    
    if (response.ok) {
      const responseText = await response.text();
      console.log('AI响应:', responseText);
      await typeWriterEffect(responseText);
      apiSuccess = true;
      return;
    } else {
      console.log('请求失败:', response.status, response.statusText);
      const errorText = await response.text().catch(e => 'No response text');
      console.log('错误响应内容:', errorText);
    }
  } catch (err) {
    console.log('数据库增强AI接口调用失败:', err.message);
  }
  
  // 备选方法1: 使用标准AI接口
  if (!apiSuccess) {
    try {
      console.log('尝试标准AI接口');
      
      const response = await fetch('http://localhost:8080/ai/chat', {
        method: 'POST',
        headers: {
          'Content-Type': 'text/plain',
          'Access-Control-Allow-Origin': '*'
        },
        body: userMessage.content,
        mode: 'cors',
      });
      
      if (response.ok) {
        const responseText = await response.text();
        console.log('标准AI响应:', responseText);
        await typeWriterEffect(responseText);
        apiSuccess = true;
        return;
      }
    } catch (err) {
      console.log('标准AI接口调用失败:', err.message);
    }
  }
  
  // 如果所有在线方法都失败，使用本地回退
  if (!apiSuccess) {
    console.log('API调用失败，使用本地模拟响应');
    
    try {
      // 模拟短暂延迟
      await new Promise(resolve => setTimeout(resolve, 800));
      
      // 使用模拟响应，但基于本地数据提供更智能的回答
      const mockResponse = generateLocalResponse(userMessage.content);
      await typeWriterEffect(mockResponse);
    } catch (err) {
      console.error('AI助手完全失败:', err);
      chatMessages.value.push({
        type: 'ai',
        content: '系统出现异常，请稍后重试。',
        time: new Date().toLocaleTimeString()
      });
      aiIsTyping.value = false;
      scrollToBottom();
      
      // 即使出错也保存聊天记录
      saveChatHistory();
    }
  }
};

// 基于本地数据生成模拟响应
const generateLocalResponse = (userQuery) => {
  const query = userQuery.toLowerCase();
  
  // 如果有航班数据，尝试提供相关信息
  if (aiFlightData.value.length > 0 && (query.includes('航班') || query.includes('飞机'))) {
    // 检查是否包含特定航班号
    const flightNumMatch = query.match(/([A-Z]{2}\d{3,4})/i);
    if (flightNumMatch) {
      const flightNum = flightNumMatch[1].toUpperCase();
      const matchedFlight = aiFlightData.value.find(f => f.flightnum === flightNum);
      if (matchedFlight) {
        return `找到航班 ${flightNum}：\n起飞日期: ${matchedFlight.flydate}\n起飞时间: ${matchedFlight.starttime}\n飞行时长: ${matchedFlight.flytime}\n出发城市: ${matchedFlight.startCityName}\n到达城市: ${matchedFlight.endCityName}\n座位数: ${matchedFlight.seatnum}`;
      }
    }
    
    // 检查是否查询特定城市航班
    if (query.includes('城市') || query.includes('从') || query.includes('到')) {
      // 尝试匹配城市名
      const cityMatch = aiCityData.value.find(city => query.includes(city.cityname));
      if (cityMatch) {
        const cityName = cityMatch.cityname;
        const departures = aiFlightData.value.filter(f => f.startCityName === cityName);
        const arrivals = aiFlightData.value.filter(f => f.endCityName === cityName);
        
        let response = `关于${cityName}的航班信息：\n`;
        if (departures.length > 0) {
          response += `从${cityName}出发的航班有${departures.length}个，包括：\n`;
          departures.slice(0, 3).forEach(f => {
            response += `${f.flightnum}: ${cityName} → ${f.endCityName}，起飞时间：${f.flydate} ${f.starttime}\n`;
          });
          if (departures.length > 3) response += `以及更多...\n`;
        }
        
        if (arrivals.length > 0) {
          response += `到达${cityName}的航班有${arrivals.length}个，包括：\n`;
          arrivals.slice(0, 3).forEach(f => {
            response += `${f.flightnum}: ${f.startCityName} → ${cityName}，起飞时间：${f.flydate} ${f.starttime}\n`;
          });
          if (arrivals.length > 3) response += `以及更多...\n`;
        }
        
        return response;
      }
    }
    
    // 提供航班统计信息
    return `当前系统中共有${aiFlightData.value.length}个航班记录。\n活跃航班数量: ${activeFlights.value}\n总座位数: ${totalSeats.value}\n航线数量: ${uniqueRoutes.value}\n\n您可以查询特定航班号(如CA1234)或城市(如北京到上海)的航班信息。`;
  }
  
  // 城市信息查询
  if (aiCityData.value.length > 0 && (query.includes('城市') || query.includes('地点'))) {
    const totalCities = aiCityData.value.length;
    let response = `系统中共有${totalCities}个城市：\n`;
    
    // 列出前10个城市
    const citiesToShow = aiCityData.value.slice(0, 10);
    citiesToShow.forEach(city => {
      response += `${city.cityname} `;
    });
    
    if (totalCities > 10) {
      response += `等${totalCities}个城市`;
        }
        
        return response;
      }
  
  // 简单的关键词匹配
  if (query.includes('你好') || query.includes('嗨') || query.includes('hi')) {
    const username = currentUser.value?.username || '用户';
    return `您好，${username}！很高兴为您服务。请问有什么航班相关问题需要帮助吗？`;
  } else if (query.includes('查询航班') || query.includes('搜索航班')) {
    return '您可以通过以下步骤查询航班信息：\n1. 在页面顶部的搜索框中输入航班号或城市名称\n2. 使用筛选器选择特定日期或城市\n3. 系统将显示符合条件的航班列表\n4. 点击航班行中的"查看"按钮获取详细信息';
  } else if (query.includes('添加航班') || query.includes('新增航班')) {
    return '添加新航班的步骤如下：\n1. 点击页面右上角的"添加航班"按钮\n2. 在弹出的表单中填写航班基本信息（航班号、起飞日期、时间等）\n3. 填写航线信息（出发城市、到达城市）\n4. 设置座位配置\n5. 点击提交按钮完成添加';
  } else if (query.includes('删除航班')) {
    return '要删除航班，只需在航班列表中找到目标航班，然后点击操作栏中的删除按钮。系统会要求您确认此操作，以防止意外删除。';
  } else if (query.includes('修改') || query.includes('编辑')) {
    return '编辑航班信息的步骤：\n1. 在航班列表中找到需要修改的航班\n2. 点击操作栏中的编辑按钮\n3. 在弹出的表单中修改需要更新的信息\n4. 点击更新按钮保存修改';
  } else if (query.includes('统计') || query.includes('数据')) {
    return '本系统提供了多种航班数据统计功能：\n1. 总航班数量统计\n2. 活跃航班数量统计\n3. 总座位数统计\n4. 航线数量统计\n这些统计数据可以在系统顶部的统计面板中实时查看。';
  } else if (query.includes('航线') || query.includes('路线')) {
    return '航线管理是本系统的核心功能之一。您可以查看所有航线信息，分析航线效率，并基于航线数据进行航班调度优化。要添加新航线，只需在添加航班时指定新的出发城市和到达城市组合。';
  } else if (query.includes('座位') || query.includes('容量')) {
    return '本系统支持灵活的座位配置管理。您可以在添加或编辑航班时设置座位数量和类型。系统会自动计算总座位数并在统计面板中显示。';
  } else if (query.includes('报表') || query.includes('导出')) {
    return '您可以通过以下步骤导出航班报表：\n1. 使用筛选功能选择要包含在报表中的航班\n2. 点击页面底部的"导出报表"按钮\n3. 选择导出格式（Excel、PDF或CSV）\n4. 系统将生成报表并提供下载链接';
  } else {
    return '感谢您的提问。作为航班管理系统的AI助手，我可以帮助您了解系统功能、查询航班信息、指导操作流程等。如果您有具体的航班相关问题，请详细描述，我会尽力提供帮助。';
  }
};

// 模拟打字效果
const typeWriterEffect = async (text) => {
  const fullResponse = text;
  let displayedResponse = '';
  const aiMessage = {
    type: 'ai',
    content: displayedResponse,
    time: new Date().toLocaleTimeString()
  };
  chatMessages.value.push(aiMessage);
  
  // 模拟打字速度
  const typingSpeed = 30; // 毫秒/字符
  
  for (let i = 0; i < fullResponse.length; i++) {
    await new Promise(resolve => setTimeout(resolve, typingSpeed));
    displayedResponse += fullResponse.charAt(i);
    aiMessage.content = displayedResponse;
    scrollToBottom();
  }
  
  aiIsTyping.value = false;
  
  // 完成打字后保存聊天记录
  saveChatHistory();
};

// 更新相关建议
const updateSuggestions = (userQuery) => {
  const query = userQuery.toLowerCase();
  
  // 根据用户提问更新建议问题
  if (query.includes('航班') || query.includes('飞机')) {
    suggestions.value = [
      '如何添加新航班?',
      '如何查询特定航班?',
      '航班延误了怎么办?'
    ];
  } else if (query.includes('城市') || query.includes('航线')) {
    suggestions.value = [
      '哪些城市的航班最多?',
      '如何分析航线效率?',
      '添加新航线的步骤是什么?'
    ];
  } else if (query.includes('座位') || query.includes('乘客')) {
    suggestions.value = [
      '如何调整座位数量?',
      '如何查看座位使用率?',
      '如何优化座位配置?'
    ];
            } else {
    suggestions.value = [
      '系统的主要功能有哪些?',
      '如何使用数据分析功能?',
      '如何导出航班报表?'
    ];
  }
};

const formatMessage = (message) => {
  return message.replace(/\n/g, '<br>');
};

const scrollToBottom = () => {
  nextTick(() => {
    if (chatWindow.value) {
      chatWindow.value.scrollTop = chatWindow.value.scrollHeight;
    }
  });
};

const useSuggestion = (suggestion) => {
  userInput.value = suggestion;
  suggestions.value = [];
};

const ganttChartContainer = ref(null);
let myGanttChart = null;

// ECharts 甘特图相关常量和函数 (移植自用户提供的代码)
const HEIGHT_RATIO = 0.6;
const DIM_CATEGORY_INDEX = 0;
const DIM_TIME_ARRIVAL = 1;
const DIM_TIME_DEPARTURE = 2;
// 注意: 示例代码中的 _draggable 等变量和相关拖拽逻辑较为复杂，
// 并且依赖 jQuery ($.get)，在初始集成时会进行简化或移除，以确保核心甘特图功能优先实现。
// 后续如果需要完整的拖拽交互，可以再逐步实现。

// 从模拟数据改为使用真实航班数据
const convertFlightsToGanttData = () => {
  // 创建甘特图所需的数据结构
  const ganttData = {
    flight: {
      dimensions: [
        'Parking Apron',
        'Arrival Time',
        'Departure Time',
        'Flight Number',
        'VIP'
      ],
      data: []
    },
    parkingApron: {
      dimensions: ['Parking Apron Index', 'Parking Apron Name', 'Type'],
      data: []
    }
  };
  
  // 如果没有航班数据，返回空结构
  if (!flights.value || flights.value.length === 0) {
    return ganttData;
  }
  
  // 提取所有唯一的机位ID作为Y轴
  const parkingPositions = new Set();
  flights.value.forEach(flight => {
    // 使用flightid作为唯一机位ID，改进映射逻辑，使用更直观的数字
    parkingPositions.add(flight.flightid % 20); // 扩大到20个机位以增加多样性
  });
  
  // 将机位ID转换为甘特图所需的机位数据
  ganttData.parkingApron.data = Array.from(parkingPositions).sort((a, b) => a - b).map(pos => {
    // 格式: [机位ID字符串, 机位名称, 机位类型]
    return [pos.toString(), (pos + 675).toString(), pos % 2 === 0 ? 'X' : 'W'];
  });
  
  // 构建机位ID到索引的映射
  const positionToIndex = {};
  ganttData.parkingApron.data.forEach((item, index) => {
    positionToIndex[item[0]] = index;
  });
  
  // 确保有足够的停机位
  if (ganttData.parkingApron.data.length < 5) {
    // 添加一些额外的停机位以确保图表显示正常
    for (let i = ganttData.parkingApron.data.length; i < 5; i++) {
      const pos = i + 10;
      ganttData.parkingApron.data.push([pos.toString(), (pos + 675).toString(), pos % 2 === 0 ? 'X' : 'W']);
      positionToIndex[pos.toString()] = i;
    }
  }
  
  // 获取当前日期作为基准日期，用于设置合理的时间范围
  const baseDate = new Date();
  baseDate.setHours(0, 0, 0, 0); // 设置为当天的0点
  const baseDateTimestamp = baseDate.getTime();
  
  // 将航班数据转换为甘特图所需的格式
  flights.value.forEach(flight => {
    try {
      // 从flightid计算机位索引 (简单示例，可调整)
      const posIndex = flight.flightid % 20;
      const ganttIndex = positionToIndex[posIndex.toString()] || 0;
      
      // 解析日期和时间字符串
      let flyDate = flight.flydate; // 例如 "2023-05-15"
      if (!flyDate) {
        // 如果没有日期，使用当前日期
        flyDate = baseDate.toISOString().split('T')[0];
      }
      
      let startTime = flight.starttime; // 例如 "14:30"
      if (!startTime || startTime === '00:00') {
        // 如果没有时间或时间为00:00，分配一个随机的合理时间
        const hour = 6 + Math.floor(Math.random() * 18); // 6:00 到 23:59
        const minute = Math.floor(Math.random() * 60);
        startTime = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
      }
      
      // 计算起飞时间戳
      const [hours, minutes] = startTime.split(':').map(Number);
      const departureDate = new Date(flyDate);
      departureDate.setHours(hours, minutes, 0, 0);
      let departureTimestamp = departureDate.getTime();
      
      // 确保时间戳在合理范围内
      if (isNaN(departureTimestamp) || departureTimestamp < baseDateTimestamp) {
        departureTimestamp = baseDateTimestamp + (hours * 3600 + minutes * 60) * 1000;
      }
      
      // 解析飞行时长 (假设格式为 "2h30m")
      let durationHours = 1; // 默认1小时
      let durationMinutes = 30; // 默认30分钟
      
      if (flight.flytime) {
        const durationMatch = flight.flytime.match(/(\d+)h(?:(\d+)m)?/);
        if (durationMatch) {
          durationHours = parseInt(durationMatch[1]) || 0;
          durationMinutes = parseInt(durationMatch[2] || '0') || 0;
        } else {
          // 如果没有匹配标准格式，尝试其他可能的格式
          const simpleMatch = flight.flytime.match(/(\d+)/);
          if (simpleMatch) {
            // 假设是小时数
            durationHours = parseInt(simpleMatch[1]) || 1;
            durationMinutes = 0;
          }
        }
      }
      
      // 确保时长至少为30分钟，不超过10小时
      if (durationHours === 0 && durationMinutes < 30) {
        durationMinutes = 30;
      }
      if (durationHours > 10) {
        durationHours = 10;
      }
      
      // 计算到达时间戳 (起飞时间 + 飞行时长)
      const arrivalDate = new Date(departureTimestamp);
      arrivalDate.setHours(arrivalDate.getHours() + durationHours);
      arrivalDate.setMinutes(arrivalDate.getMinutes() + durationMinutes);
      const arrivalTimestamp = arrivalDate.getTime();
      
      // 确定VIP状态
      const isVip = flight.flightid % 5 === 0; // 每5个航班有一个VIP
      
      // 添加到甘特图数据中
      ganttData.flight.data.push([
        ganttIndex,
        departureTimestamp,
        arrivalTimestamp,
        flight.flightnum || `FL${flight.flightid}`, // 确保有航班号
        isVip
      ]);
    } catch (err) {
      console.error('处理航班数据出错:', flight, err);
    }
  });
  
  return ganttData;
};

// 更新甘特图数据和配置
const updateGanttChart = () => {
  if (!myGanttChart) return;
  
  try {
    // 将航班数据转换为甘特图数据
    const ganttData = convertFlightsToGanttData();
    
    // 更新图表配置
    const option = makeGanttOption(ganttData);
    myGanttChart.setOption(option, true); // true表示不合并之前的选项
    
    // 强制重绘
    myGanttChart.resize();
  } catch (err) {
    console.error('更新甘特图出错:', err);
  }
};

function makeGanttOption(ganttData) {
  const data = ganttData || convertFlightsToGanttData();
  
  // 设置合理的时间范围
        const now = new Date();
  const startOfDay = new Date(now);
  startOfDay.setHours(0, 0, 0, 0);
  
  const endOfDay = new Date(now);
  endOfDay.setHours(23, 59, 59, 999);
  
  // 确定x轴的最小最大值
  let minTime = startOfDay.getTime();
  let maxTime = endOfDay.getTime();
  
  // 如果有数据，根据数据调整时间范围
  if (data.flight.data && data.flight.data.length > 0) {
    // 找出所有航班的最早起飞时间和最晚到达时间
    const allTimes = data.flight.data.reduce((times, flight) => {
      times.push(flight[1]); // 添加起飞时间
      times.push(flight[2]); // 添加到达时间
      return times;
    }, []);
    
    if (allTimes.length > 0) {
      const dataMinTime = Math.min(...allTimes);
      const dataMaxTime = Math.max(...allTimes);
      
      // 只有当数据时间范围合理时才使用
      if (!isNaN(dataMinTime) && !isNaN(dataMaxTime) && dataMinTime < dataMaxTime) {
        minTime = dataMinTime;
        maxTime = dataMaxTime;
        
        // 为了可视化效果，在时间范围前后添加一些余量
        const buffer = (maxTime - minTime) * 0.1; // 10%的缓冲
        minTime = Math.max(startOfDay.getTime(), minTime - buffer);
        maxTime = Math.min(endOfDay.getTime(), maxTime + buffer);
      }
    }
  }
  
  return {
    tooltip: {
      formatter: function(params) {
        if (params.seriesType === 'custom' && params.seriesIndex === 0) {
          const flightData = params.value;
          // 提取航班信息
          const flightNumber = flightData[3];
          const departureTime = new Date(flightData[1]).toLocaleTimeString();
          const arrivalTime = new Date(flightData[2]).toLocaleTimeString();
          const parkingApronIndex = flightData[0];
          const parkingApron = data.parkingApron.data.find(item => parseInt(item[0]) === parkingApronIndex)?.[1] || '未知';
          const isVip = flightData[4] ? '是' : '否';
          
          return `
            <div style="padding: 5px; background: rgba(50, 50, 50, 0.9); color: white; border-radius: 4px;">
              <div style="font-weight: bold; margin-bottom: 5px; font-size: 14px;">${flightNumber}</div>
              <div>停机位: ${parkingApron}</div>
              <div>起飞: ${departureTime}</div>
              <div>到达: ${arrivalTime}</div>
              <div>VIP: ${isVip}</div>
            </div>
          `;
        }
        return '';
      }
    },
    animation: false,
    title: {
      text: '机场航班甘特图',
      left: 'center',
      textStyle: {
        color: '#FFFFFF'
      }
    },
    dataZoom: [
      {
        type: 'slider',
        xAxisIndex: 0,
        filterMode: 'weakFilter',
        height: 20,
        bottom: 0,
        start: 0,
        end: 100,
        handleIcon:
          'path://M10.7,11.9H9.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4h1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9z M13.3,24.4H6.7V23h6.6V24.4z M13.3,19.6H6.7v-1.4h6.6V19.6z',
        handleSize: '80%',
        showDetail: false,
        backgroundColor: 'rgba(255,255,255,0.1)',
        borderColor: 'rgba(255,255,255,0.3)',
        fillerColor: 'rgba(66, 165, 245, 0.5)',
        textStyle: { color: '#FFF' }
      },
      {
        type: 'inside',
        id: 'insideX',
        xAxisIndex: 0,
        filterMode: 'weakFilter',
        start: 0,
        end: 100,
        zoomOnMouseWheel: true,
        moveOnMouseMove: true
      },
      {
        type: 'slider',
        yAxisIndex: 0,
        zoomLock: true,
        width: 10,
        right: 10,
        top: 70,
        bottom: 40,
        start: 0,
        end: 100,
        handleSize: 0,
        showDetail: false,
        backgroundColor: 'rgba(255,255,255,0.1)',
        borderColor: 'rgba(255,255,255,0.3)',
        fillerColor: 'rgba(66, 165, 245, 0.5)',
      },
      {
        type: 'inside',
        id: 'insideY',
        yAxisIndex: 0,
        start: 0,
        end: 100,
        zoomOnMouseWheel: false,
        moveOnMouseMove: true,
        moveOnMouseWheel: true
      }
    ],
    grid: {
      show: true,
      top: 70,
      bottom: 40,
      left: 100,
      right: 40,
      backgroundColor: 'rgba(255,255,255,0.05)',
      borderWidth: 0
    },
    xAxis: {
      type: 'time',
      position: 'top',
      min: minTime,
      max: maxTime,
      splitLine: {
        lineStyle: {
          color: ['rgba(255,255,255,0.2)']
        }
      },
      axisLine: {
        show: false
      },
      axisTick: {
        lineStyle: {
          color: 'rgba(255,255,255,0.7)'
        }
      },
      axisLabel: {
        color: 'rgba(255,255,255,0.7)',
        inside: false,
        align: 'center',
        formatter: function(value) {
          const date = new Date(value);
          return date.getHours() + ':' + String(date.getMinutes()).padStart(2, '0');
        }
      }
    },
    yAxis: {
      axisTick: { show: false },
      splitLine: { show: false },
      axisLine: { show: false },
      axisLabel: { show: false },
      min: 0,
      max: data.parkingApron.data.length
    },
    series: [
      {
        id: 'flightData',
        type: 'custom',
        renderItem: renderGanttItem,
        dimensions: data.flight.dimensions,
        encode: {
          x: [DIM_TIME_ARRIVAL, DIM_TIME_DEPARTURE],
          y: DIM_CATEGORY_INDEX,
          tooltip: [DIM_CATEGORY_INDEX, DIM_TIME_ARRIVAL, DIM_TIME_DEPARTURE]
        },
        data: data.flight.data,
        itemStyle: {
            color: '#FFC107'
        }
      },
      {
        type: 'custom',
        renderItem: renderAxisLabelItem,
        dimensions: data.parkingApron.dimensions,
        encode: {
          x: -1,
          y: 0
        },
        data: data.parkingApron.data.map(function (item, index) {
          return [index].concat(item);
        })
      }
    ]
  };
}

function renderGanttItem(params, api) {
  var categoryIndex = api.value(DIM_CATEGORY_INDEX);
  var timeArrival = api.coord([api.value(DIM_TIME_ARRIVAL), categoryIndex]);
  var timeDeparture = api.coord([api.value(DIM_TIME_DEPARTURE), categoryIndex]);
  
  // 获取当前数据用于判断范围
  const currentData = convertFlightsToGanttData();

  // 添加检查，如果时间无效或 categoryIndex 超出范围则不渲染
  if (!timeArrival || !timeDeparture || categoryIndex < 0 || 
      categoryIndex >= currentData.parkingApron.data.length) {
    return;
  }
  
  // 确保时间段长度合理，至少有最小宽度
  const MIN_BAR_WIDTH = 30; // 最小宽度为30像素
  var barLength = Math.max(MIN_BAR_WIDTH, timeDeparture[0] - timeArrival[0]);
  
  // 如果计算出的长度为负或无效，使用最小宽度
  if (isNaN(barLength) || barLength <= 0) {
    barLength = MIN_BAR_WIDTH;
  }
  
  var barHeight = api.size([0, 1])[1] * HEIGHT_RATIO;
  var x = timeArrival[0];
  var y = timeArrival[1] - barHeight;

  var flightNumber = api.value(3) + ''; // Flight Number
  var flightNumberWidth = echarts.format.getTextRect(flightNumber).width;
  var text = barLength > flightNumberWidth + 20 ? flightNumber : '';

  var rectNormal = clipRectByRect(params, {
    x: x,
    y: y,
    width: barLength,
    height: barHeight
  });

  // 根据VIP状态决定颜色
  const isVip = api.value(4); // VIP
  const barColor = isVip ? '#1E90FF' : '#FFC107'; // VIP蓝色，普通使用金色

  return {
    type: 'group',
    children: [
      {
        type: 'rect',
        ignore: !rectNormal,
        shape: rectNormal,
        style: {
          fill: barColor,
          stroke: isVip ? '#0D47A1' : '#FFA000',
          lineWidth: 1,
          shadowBlur: 3,
          shadowColor: 'rgba(0,0,0,0.3)',
          shadowOffsetX: 1,
          shadowOffsetY: 1
        }
      },
      {
        type: 'rect',
        ignore: !rectNormal || !text, // 只有当有文本且矩形有效时才显示
        shape: rectNormal,
        style: {
          fill: 'transparent',
          stroke: 'transparent',
          text: text,
          textFill: '#fff',
          textFont: 'bold 12px Arial',
          textAlign: 'center',
          textVerticalAlign: 'middle'
        }
      }
    ]
  };
}

function renderAxisLabelItem(params, api) {
  var y = api.coord([0, api.value(0)])[1]; // api.value(0) is the index from the mapped data
  
  // 添加检查，如果y无效则不渲染
  if (isNaN(y) || y < params.coordSys.y + 5) {
    return;
  }

  // api.value(1) 是原始 parkingApron.data 中的 parkingApronName (例如 '692')
  // api.value(2) 是原始 parkingApron.data 中的 Type (例如 'X' 或 'W')
  const parkingApronName = api.value(1);
  const parkingApronType = api.value(2);
  
  // 计算barHeight (与renderGanttItem函数中相同的计算方式)
  const barHeight = api.size([0, 1])[1] * HEIGHT_RATIO;

  return {
    type: 'group',
    position: [10, y], // 调整位置以适应容器
    children: [
      {
        type: 'path',
        shape: {
          d: 'M0,0 L0,-20 L30,-20 C42,-20 38,-1 50,-1 L70,-1 L70,0 Z',
          x: 0,
          y: -barHeight / 2, // 使用计算的barHeight
          width: 90,
          height: 20, 
          layout: 'cover'
        },
        style: {
          fill: 'rgba(54, 140, 108, 0.7)' // 使用半透明颜色
        }
      },
      {
        type: 'text',
        style: {
          x: 24,
          y: -barHeight/2 + 17, // 使用计算的barHeight
          text: parkingApronName, // 使用机位名称
          textVerticalAlign: 'bottom',
          textAlign: 'center',
          textFill: '#fff'
        }
      },
      {
        type: 'text',
        style: {
          x: 75,
          y: -barHeight/2 + 18, // 使用计算的barHeight
          textVerticalAlign: 'bottom',
          textAlign: 'center',
          text: parkingApronType, // 使用机位类型
          textFill: '#000'
        }
      }
    ]
  };
}
let _cartesianXBounds = [];
let _cartesianYBounds = [];

function clipRectByRect(params, rect) {
    // 在renderGanttItem中更新 _cartesianXBounds 和 _cartesianYBounds
    const coordSys = params.coordSys;
    _cartesianXBounds[0] = coordSys.x;
    _cartesianXBounds[1] = coordSys.x + coordSys.width;
    _cartesianYBounds[0] = coordSys.y;
    _cartesianYBounds[1] = coordSys.y + coordSys.height;

  return echarts.graphic.clipRectByRect(rect, {
    x: params.coordSys.x,
    y: params.coordSys.y,
    width: params.coordSys.width,
    height: params.coordSys.height
  });
}

// 初始化甘特图
const initGanttChart = () => {
  try {
    console.log("尝试初始化甘特图", ganttChartContainer.value);
    
    // 确保销毁之前的实例
    if (myGanttChart) {
      myGanttChart.dispose();
      myGanttChart = null;
    }
    
    if (ganttChartContainer.value) {
      console.log("初始化ECharts实例");
      
      // 确保容器有尺寸
      if (ganttChartContainer.value.clientWidth === 0 || ganttChartContainer.value.clientHeight === 0) {
        console.warn("甘特图容器尺寸为0", ganttChartContainer.value.clientWidth, ganttChartContainer.value.clientHeight);
        // 强制设置容器尺寸
        ganttChartContainer.value.style.width = '100%';
        ganttChartContainer.value.style.height = '500px';
      }
      
      // 使用不带主题的初始化
      myGanttChart = echarts.init(ganttChartContainer.value);
      
      // 初始化时使用空的配置，数据将在fetchFlights完成后更新
      const option = makeGanttOption();
      console.log("设置ECharts选项", option);
      
      // 应用选项
      myGanttChart.setOption(option);
      
      // 强制更新图表
      setTimeout(() => {
        if (myGanttChart) {
          myGanttChart.resize();
          console.log("强制更新甘特图大小");
        }
      }, 300);

      // 监听窗口大小变化，重新渲染图表
      const resizeHandler = () => {
        if (myGanttChart) {
          myGanttChart.resize();
        }
      };
      
      window.addEventListener('resize', resizeHandler);
      
      // 保存resize处理函数以便后续移除
      ganttChartContainer.value._resizeHandler = resizeHandler;
      
      console.log("甘特图初始化完成");
    } else {
      console.error("甘特图容器未找到", ganttChartContainer.value);
    }
  } catch (err) {
    console.error("初始化甘特图出错:", err);
  }
};

onMounted(() => {
  fetchFlights();
  
  // 确保在DOM完全更新后初始化图表
  nextTick(() => {
    setTimeout(() => {
      console.log("DOM已更新，开始初始化甘特图");
      initGanttChart(); 
    }, 500); // 延迟500ms确保DOM完全渲染
  });
});

// 确保组件卸载时清理资源
onBeforeUnmount(() => {
  if (myGanttChart) {
    myGanttChart.dispose();
    myGanttChart = null;
  }
  
  // 移除resize监听器
  if (ganttChartContainer.value && ganttChartContainer.value._resizeHandler) {
    window.removeEventListener('resize', ganttChartContainer.value._resizeHandler);
    ganttChartContainer.value._resizeHandler = null;
  }
});

// 导出航班数据
const exportData = () => {
  try {
    // 准备CSV数据
    let csvContent = "航班ID,航班号,起飞日期,起飞时间,飞行时长,出发城市,到达城市,座位数\n";
    
    filteredFlights.value.forEach(flight => {
      csvContent += `${flight.flightid},${flight.flightnum},${flight.flydate},${flight.starttime},${flight.flytime},${flight.startCityName},${flight.endCityName},${flight.seatnum}\n`;
    });
    
    // 创建Blob对象并下载
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    
    link.setAttribute("href", url);
    link.setAttribute("download", `航班数据_${new Date().toISOString().split('T')[0]}.csv`);
    link.style.display = "none";
    
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    ElMessage.success('数据导出成功');
  } catch (err) {
    console.error('导出数据失败:', err);
    ElMessage.error('导出数据失败');
  }
};

// 重置所有筛选条件
const resetFilters = () => {
  searchQuery.value = '';
  filterStartCity.value = '';
  filterEndCity.value = '';
  filterDateRange.value = null;
  handleFilter();
};
</script>

<style scoped>
/* 主容器样式 */
.flight-management-system {
  min-height: 100vh;
  background: linear-gradient(135deg, #0A2463 0%, #0F2E7A 50%, #164996 100%);
  color: #FFFFFF;
  font-family: 'SF Pro Display', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

/* 顶部导航栏 */
.top-navbar {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(10, 36, 99, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding: 0;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.navbar-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  font-size: 32px;
  filter: drop-shadow(0 0 10px rgba(255, 255, 255, 0.3));
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

.system-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0;
  background: linear-gradient(45deg, #FFFFFF, #E3F2FD);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 2px 10px rgba(255, 255, 255, 0.1);
}

.navbar-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.premium-btn {
  background: linear-gradient(135deg, #1976D2, #1565C0);
  border: none;
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 600;
  box-shadow: 0 4px 16px rgba(25, 118, 210, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.premium-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(25, 118, 210, 0.4);
  background: linear-gradient(135deg, #1E88E5, #1976D2);
}

.refresh-btn {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  padding: 12px;
  color: #FFFFFF;
  transition: all 0.3s ease;
}

.refresh-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: rotate(180deg);
}

.ai-btn {
  background: linear-gradient(135deg, #FFC107, #FFA000);
  border: none;
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 600;
  box-shadow: 0 4px 16px rgba(255, 193, 7, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.ai-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(255, 193, 7, 0.4);
  background: linear-gradient(135deg, #FFCA28, #FFC107);
}

/* 统计面板 */
.stats-dashboard {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-heading {
  font-size: 22px;
  font-weight: 600;
  margin: 0;
  color: #FFFFFF;
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-heading::before {
  content: '';
  display: block;
  width: 4px;
  height: 24px;
  background: linear-gradient(to bottom, #1976D2, #42A5F5);
  border-radius: 2px;
}

.date-display {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  background: rgba(255, 255, 255, 0.1);
  padding: 8px 16px;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 20px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, #1976D2, #42A5F5, #1976D2);
  background-size: 200% 100%;
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.3);
  background: rgba(255, 255, 255, 0.12);
}

.stat-icon {
  font-size: 48px;
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.1);
  position: relative;
  overflow: hidden;
}

.stat-icon::after {
  content: '';
  position: absolute;
  top: -10px;
  left: -10px;
  right: -10px;
  bottom: -10px;
  background: radial-gradient(circle at center, rgba(255, 255, 255, 0.2) 0%, transparent 70%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.stat-card:hover .stat-icon::after {
  opacity: 1;
}

.custom-icon {
  font-size: 36px;
  filter: drop-shadow(0 2px 5px rgba(0, 0, 0, 0.3));
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 4px;
  background: linear-gradient(45deg, #FFFFFF, #E3F2FD);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
  margin-bottom: 8px;
}

.stat-progress {
  height: 4px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 2px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #42A5F5, #1976D2);
  border-radius: 2px;
  transition: width 1s ease-out;
}

/* 搜索筛选区域 */
.search-filter-section {
  padding: 0 24px 24px;
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  align-items: center;
}

.search-container {
  flex: 1;
  min-width: 300px;
}

.search-input {
  border-radius: 16px;
  transition: all 0.3s ease;
}

.search-input:focus-within {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.clear-icon {
  cursor: pointer;
  color: rgba(255, 255, 255, 0.6);
  transition: color 0.2s ease;
}

.clear-icon:hover {
  color: rgba(255, 255, 255, 1);
}

.filter-container {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  align-items: center;
}

.filter-select,
.filter-date {
  min-width: 160px;
  transition: all 0.3s ease;
}

.filter-select:hover,
.filter-date:hover {
  transform: translateY(-2px);
}

.filter-date {
  min-width: 320px; /* 增加日期范围选择器的宽度 */
}

.filter-reset {
  background: rgba(245, 108, 108, 0.1);
  border: 1px solid rgba(245, 108, 108, 0.2);
  color: #F56C6C;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.filter-reset:hover {
  background: rgba(245, 108, 108, 0.2);
  transform: translateY(-2px);
}

/* 主要内容区域 */
.main-content {
  padding: 0 24px 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.table-container {
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 20px;
  padding: 24px;
  overflow: hidden;
}

/* 表格样式 */
.premium-table {
  border-radius: 12px;
  overflow: hidden;
}

.flight-number {
  display: flex;
  align-items: center;
  gap: 8px;
}

.flight-code {
  font-weight: 600;
  color: #42A5F5;
}

.time-display {
  display: flex;
  align-items: center;
  gap: 6px;
}

.duration-tag {
  background: rgba(76, 175, 80, 0.2);
  color: #81C784;
  border: none;
  border-radius: 8px;
}

.route-display {
  display: flex;
  align-items: center;
  gap: 8px;
}

.city-name {
  font-weight: 500;
}

.route-arrow {
  color: #42A5F5;
}

.seat-info {
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-tag {
  border-radius: 8px;
  font-weight: 500;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.action-btn {
  border-radius: 8px;
  padding: 8px;
  min-width: auto;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 分页样式 */
.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

.premium-pagination {
  background: rgba(255, 255, 255, 0.05);
  padding: 16px;
  border-radius: 16px;
}

/* 抽屉样式 */
.premium-drawer {
  border-radius: 20px 0 0 20px;
}

.drawer-content {
  padding: 0;
}

.premium-form {
  padding: 0;
}

.form-section {
  margin-bottom: 32px;
  padding: 24px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 20px 0;
  color: #FFFFFF;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title::before {
  content: '';
  width: 4px;
  height: 20px;
  background: linear-gradient(135deg, #1976D2, #42A5F5);
  border-radius: 2px;
}

.premium-input {
  border-radius: 12px;
}

.drawer-footer {
  padding: 24px;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  background: rgba(255, 255, 255, 0.05);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.cancel-btn {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: #FFFFFF;
  border-radius: 12px;
  padding: 12px 24px;
}

.submit-btn {
  background: linear-gradient(135deg, #1976D2, #1565C0);
  border: none;
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 600;
}

/* 详情对话框样式 */
.detail-dialog {
  border-radius: 20px;
}

.flight-details {
  padding: 0;
}

.detail-header {
  padding: 24px 24px 0;
}

.flight-info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.flight-info-header h2 {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
}

.detail-content {
  padding: 0 24px 24px;
}

.detail-section {
  margin-bottom: 24px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
}

.detail-section h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #42A5F5;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.detail-item .label {
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}

.detail-item .value {
  color: #FFFFFF;
  font-weight: 600;
}

.route-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
}

.route-point {
  text-align: center;
}

.city-info strong {
  display: block;
  font-size: 18px;
  margin-bottom: 4px;
}

.departure-label,
.arrival-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  text-transform: uppercase;
  letter-spacing: 1px;
}

.route-line {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 20px;
}

.route-line .el-icon {
  font-size: 24px;
  color: #42A5F5;
}

.seat-details {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
}

.seat-stat {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

/* 错误提示样式 */
.error-alert {
  margin: 24px;
  border-radius: 12px;
}

/* Element Plus 深度样式覆盖 */
:deep(.el-table) {
  background-color: transparent;
  color: #FFFFFF;
  border-radius: 12px;
}

:deep(.el-table th),
:deep(.el-table tr),
:deep(.el-table td) {
  background-color: transparent !important;
  color: #FFFFFF !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1) !important;
}

:deep(.el-table th) {
  color: #FFFFFF !important;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.05) !important;
}

:deep(.el-table .flight-row-success) {
  background: rgba(76, 175, 80, 0.1) !important;
}

:deep(.el-table .flight-row-warning) {
  background: rgba(255, 193, 7, 0.1) !important;
}

:deep(.el-table .flight-row-info) {
  background: rgba(96, 125, 139, 0.1) !important;
}

:deep(.el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  border-radius: 12px !important;
  color: #FFFFFF !important;
}

:deep(.el-input__inner) {
  color: #FFFFFF !important;
  background: transparent !important;
}

:deep(.el-input__wrapper:hover) {
  border-color: rgba(255, 255, 255, 0.4) !important;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #42A5F5 !important;
  box-shadow: 0 0 0 2px rgba(66, 165, 245, 0.2) !important;
}

:deep(.el-select .el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

:deep(.el-button) {
  border-radius: 12px !important;
  font-weight: 500 !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #1976D2, #1565C0) !important;
  border: none !important;
  box-shadow: 0 4px 12px rgba(25, 118, 210, 0.3) !important;
}

:deep(.el-button--primary:hover) {
  transform: translateY(-1px) !important;
  box-shadow: 0 6px 16px rgba(25, 118, 210, 0.4) !important;
}

:deep(.el-button--danger) {
  background: linear-gradient(135deg, #F44336, #D32F2F) !important;
  border: none !important;
}

:deep(.el-button--info) {
  background: linear-gradient(135deg, #607D8B, #455A64) !important;
  border: none !important;
}

:deep(.el-pagination) {
  color: #FFFFFF !important;
}

:deep(.el-pagination .el-pager li),
:deep(.el-pagination button) {
  background: rgba(255, 255, 255, 0.1) !important;
  color: #FFFFFF !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  border-radius: 8px !important;
  margin: 0 4px !important;
}

:deep(.el-pagination .el-pager li.is-active) {
  background: linear-gradient(135deg, #1976D2, #1565C0) !important;
  color: #FFFFFF !important;
}

:deep(.el-drawer) {
  background: rgba(10, 36, 99, 0.95) !important;
  backdrop-filter: blur(20px) !important;
  color: #FFFFFF !important;
}

:deep(.el-drawer__header) {
  margin-bottom: 0;
  padding: 20px;
  background: linear-gradient(90deg, #0A2463, #164996);
  color: white;
}

:deep(.el-drawer__header)::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at top right, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  pointer-events: none;
}

:deep(.el-drawer__title) {
  color: #FFFFFF;
  font-weight: 700;
  font-size: 20px;
  display: flex;
  align-items: center;
}

:deep(.el-drawer__title)::before {
  content: '🤖';
  margin-right: 10px;
  font-size: 22px;
}

:deep(.el-drawer__body) {
  padding: 0 !important;
}

:deep(.el-form-item__label) {
  color: #FFFFFF !important;
  font-weight: 500 !important;
}

:deep(.el-dialog) {
  background: rgba(10, 36, 99, 0.95) !important;
  backdrop-filter: blur(20px) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  color: #FFFFFF !important;
}

:deep(.el-dialog__header) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1) !important;
  padding: 24px !important;
}

:deep(.el-dialog__title) {
  color: #FFFFFF !important;
  font-weight: 600 !important;
}

:deep(.el-date-editor .el-input__wrapper),
:deep(.el-time-picker .el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .navbar-content {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .search-filter-section {
    flex-direction: column;
  }
  
  .filter-container {
    width: 100%;
    justify-content: center;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}

@media (max-width: 480px) {
  .main-content,
  .stats-dashboard,
  .search-filter-section {
    padding-left: 12px;
    padding-right: 12px;
  }
  
  .table-container {
    padding: 16px;
  }
  
  .stat-card {
    padding: 16px;
  }
  
  .system-title {
    font-size: 20px;
  }
}

/* 甘特图区域样式 */
.gantt-chart-section {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.section-card {
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.section-card:hover {
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.3);
  transform: translateY(-2px);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chart-controls {
  display: flex;
  gap: 10px;
}

.view-selector {
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: white;
}

.gantt-chart-container {
  width: 100%;
  height: 500px;
  position: relative;
  border-radius: 12px;
  overflow: hidden;
}

/* 表格区域样式 */
.main-content {
  padding: 0 24px 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.table-container {
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 20px;
  padding: 24px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.table-container:hover {
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.3);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.table-actions {
  display: flex;
  gap: 10px;
}

.export-btn {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: white;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.export-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

/* 表格样式 */
.premium-table {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.flight-id {
  font-family: 'SF Mono', monospace;
  font-weight: 600;
  color: #64B5F6;
}

.flight-number {
  display: flex;
  align-items: center;
  gap: 8px;
}

.flight-code {
  font-weight: 600;
  color: #42A5F5;
  font-family: 'SF Mono', monospace;
  background: rgba(66, 165, 245, 0.1);
  padding: 4px 8px;
  border-radius: 4px;
  border: 1px solid rgba(66, 165, 245, 0.2);
}

.date-display-cell {
  display: flex;
  align-items: center;
  gap: 6px;
}

.time-display {
  display: flex;
  align-items: center;
  gap: 6px;
}

.duration-tag {
  background: rgba(76, 175, 80, 0.2);
  color: #81C784;
  border: none;
  border-radius: 8px;
  padding: 2px 8px;
  font-family: 'SF Mono', monospace;
}

.route-display {
  display: flex;
  align-items: center;
  gap: 8px;
}

.city-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.city-badge.start {
  background: rgba(33, 150, 243, 0.2);
  color: #64B5F6;
  border: 1px solid rgba(33, 150, 243, 0.3);
}

.city-badge.end {
  background: rgba(156, 39, 176, 0.2);
  color: #BA68C8;
  border: 1px solid rgba(156, 39, 176, 0.3);
}

.route-arrow {
  color: #42A5F5;
}

.suggestion-tag:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

/* 详情对话框样式 */
.detail-dialog :deep(.el-dialog__header) {
  background: linear-gradient(90deg, #0A2463, #164996);
  padding: 20px;
  margin: 0;
  border-radius: 20px 20px 0 0;
}

.detail-dialog :deep(.el-dialog__title) {
  color: white;
  font-weight: 600;
  font-size: 22px;
}

.detail-dialog :deep(.el-dialog__headerbtn .el-dialog__close) {
  color: white;
}

.detail-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.flight-details {
  padding: 0;
}

.detail-header {
  padding: 24px 24px 0;
}

.flight-info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.flight-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.flight-badge {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #1976D2, #42A5F5);
  color: white;
  padding: 8px 16px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(25, 118, 210, 0.3);
}

.detail-status-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 14px;
}

.flight-actions {
  display: flex;
  gap: 8px;
}

.detail-content {
  padding: 0 24px 24px;
}

.detail-section {
  margin-bottom: 24px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.detail-section:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  background: rgba(255, 255, 255, 0.08);
}

.section-title {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #42A5F5;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title::before {
  content: '';
  width: 3px;
  height: 16px;
  background: linear-gradient(to bottom, #1976D2, #42A5F5);
  border-radius: 2px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.detail-item .label {
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}

.detail-item .value {
  color: #FFFFFF;
  font-weight: 600;
}

.route-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
}

.route-point {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.route-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.route-icon.start {
  background: linear-gradient(135deg, #1976D2, #42A5F5);
  box-shadow: 0 4px 12px rgba(25, 118, 210, 0.3);
}

.route-icon.end {
  background: linear-gradient(135deg, #9C27B0, #BA68C8);
  box-shadow: 0 4px 12px rgba(156, 39, 176, 0.3);
}

.city-info {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.city-info strong {
  display: block;
  font-size: 18px;
  margin-bottom: 4px;
}

.departure-label,
.arrival-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  text-transform: uppercase;
  letter-spacing: 1px;
}

.route-line {
  flex: 1;
  position: relative;
  height: 4px;
  margin: 0 20px;
}

.route-line-inner {
  height: 100%;
  background: linear-gradient(90deg, #1976D2, #9C27B0);
  width: 100%;
  position: relative;
}

.route-plane {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 24px;
  color: white;
  background: #FFC107;
  border-radius: 50%;
  padding: 8px;
  box-shadow: 0 4px 12px rgba(255, 193, 7, 0.3);
  animation: fly 3s infinite ease-in-out;
}

@keyframes fly {
  0%, 100% { transform: translate(-50%, -50%); }
  50% { transform: translate(-50%, calc(-50% - 10px)); }
}

.seat-details {
  padding: 16px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
}

.seat-stat {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  margin-bottom: 12px;
}

.seat-chart {
  margin-top: 16px;
}

.seat-progress {
  height: 8px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  overflow: hidden;
}

.seat-progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #4CAF50, #8BC34A);
  border-radius: 4px;
  transition: width 1s ease-out;
}

.seat-labels {
  display: flex;
  justify-content: space-between;
  margin-top: 4px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
}

/* AI助手样式 */
.ai-drawer :deep(.el-drawer__header) {
  margin-bottom: 0;
  padding: 0;
  background: linear-gradient(90deg, #0A2463, #164996);
  color: white;
}

.ai-drawer-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
}

.ai-avatar {
  width: 48px;
  height: 48px;
  border-radius: 24px;
  background: linear-gradient(135deg, #FFC107, #FF9800);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  box-shadow: 0 4px 16px rgba(255, 193, 7, 0.3);
}

.ai-title {
  flex: 1;
}

.ai-title h3 {
  margin: 0 0 4px 0;
  font-size: 20px;
  font-weight: 600;
}

.ai-subtitle {
  font-size: 12px;
  opacity: 0.8;
}

.ai-assistant-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.ai-chat-window {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: rgba(10, 36, 99, 0.05);
}

.empty-chat {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  gap: 16px;
  opacity: 0.7;
  padding: 20px;
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.8);
}

.message-bubble {
  margin-bottom: 16px;
  display: flex;
  align-items: flex-start;
  animation: fadeInUp 0.3s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-bubble.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1976D2, #42A5F5);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 12px;
  box-shadow: 0 4px 8px rgba(25, 118, 210, 0.3);
  animation: pulseEffect 2s infinite;
}

@keyframes pulseEffect {
  0% {
    box-shadow: 0 0 0 0 rgba(25, 118, 210, 0.4);
  }
  70% {
    box-shadow: 0 0 0 6px rgba(25, 118, 210, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(25, 118, 210, 0);
  }
}

.message-content {
  max-width: 80%;
  padding: 12px 16px;
  border-radius: 18px;
  position: relative;
}

.user .message-content {
  background: linear-gradient(135deg, #1976D2, #1565C0);
  color: white;
  border-top-right-radius: 4px;
  margin-right: 12px;
}

.ai .message-content {
  background: rgba(255, 255, 255, 0.9);
  color: #333;
  border-top-left-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.message-text {
  line-height: 1.5;
}

.message-time {
  font-size: 10px;
  margin-top: 6px;
  opacity: 0.7;
  text-align: right;
}

.typing-indicator {
  display: flex;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 18px;
  border-top-left-radius: 4px;
  width: fit-content;
  margin-bottom: 16px;
}

.typing-dot {
  width: 8px;
  height: 8px;
  background: #42A5F5;
  border-radius: 50%;
  margin: 0 3px;
  animation: typingAnimation 1.2s infinite ease-in-out;
}

.typing-dot:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typingAnimation {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-6px); }
}

.ai-input-container {
  padding: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(10, 36, 99, 0.8);
}

.ai-input {
  margin-bottom: 12px;
}

.ai-input :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.1) !important;
  border-radius: 24px !important;
  padding: 4px 8px;
}

.ai-input :deep(.el-input__inner) {
  height: 40px;
  color: white !important;
}

.ai-input :deep(.el-input-group__append) {
  background: linear-gradient(135deg, #1976D2, #1565C0);
  border-radius: 0 24px 24px 0 !important;
  padding: 0 16px;
}

.ai-input :deep(.el-input-group__append button) {
  background: transparent;
  border: none;
  color: white;
  padding: 0;
  width: 24px;
  height: 24px;
}

.ai-suggestions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.suggestion-tag {
  cursor: pointer;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  padding: 4px 12px;
  transition: all 0.3s ease;
}

.suggestion-tag:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}
</style>