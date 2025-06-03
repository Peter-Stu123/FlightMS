package org.isoft.service;

import org.isoft.Entity.Flightinfo;
import org.isoft.Entity.User;
import org.isoft.mapper.FlightMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AIService {
    private final ChatClient chatClient;

    public AIService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    @Autowired
    private FlightMapper flightMapper;

    // 根据航班ID生成AI分析提示词
    public Mono<String> analyzeFlightWithCities(int flightId) {
        return Mono.fromCallable(() -> {
            // 1. 查询带城市名称的航班信息（通过内连接）
            List<Flightinfo> flights = flightMapper.getFlightWithCityNamesById(flightId);

            if (flights.isEmpty()) {
                throw new RuntimeException("航班不存在");
            }

            Flightinfo flight = flights.get(0);

            // 2. 构建提示词（包含航班和城市信息）
            String prompt = String.format(
                    "请分析以下航班数据：\n" +
                            "航班编号：%s\n" +
                            "出发时间：%s %s\n" +
                            "出发城市：%s\n" +
                            "到达城市：%s\n" +
                            "座位数：%d\n" +
                            "飞行时长：%s\n" +
                            "请生成航班运营优化建议（如：定价策略、延误风险预测）",
                    flight.getFlightnum(),
                    flight.getFlydate(), flight.getStarttime(),
                    flight.getStartCityName(),
                    flight.getEndCityName(),
                    flight.getSeatnum(),
                    flight.getFlytime()
            );

            // 3. 调用 AI 模型
            return chatClient.prompt().user(prompt).call().content();
        });
    }
    // 单条记录处理（带异常处理）
    public String processUser(User user) {
        try {
            String prompt = String.format(
                    "分析用户信息：\n用户名：%s\n密码哈希：%s\n请生成用户画像报告",
                    user.getUsername(),
                    user.getPassword()
            );
            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
        } catch (Exception e) {
            // 记录错误日志（建议使用slf4j）
            System.err.println("AI处理失败: " + e.getMessage());
            return "处理失败，请重试";
        }
    }

    // 批量处理优化版（添加并行处理）
    public List<String> batchProcessUsers(List<User> users) {
        return users.parallelStream()
                .map(this::processUser)
                .collect(Collectors.toList());
    }
    
    // 获取所有航班信息用于AI处理
    public String getFlightDatabaseInfo() {
        try {
            // 查询带城市名称的所有航班信息
            List<Flightinfo> flights = flightMapper.getFlightWithCityNames();
            
            if (flights.isEmpty()) {
                return "数据库中暂无航班信息";
            }
            
            StringBuilder data = new StringBuilder("数据库中的航班信息:\n");
            
            for (Flightinfo flight : flights) {
                data.append(String.format("航班号: %s, 航班ID: %d, 出发城市: %s, 目的城市: %s, 日期: %s, 起飞时间: %s, 飞行时长: %s\n", 
                    flight.getFlightnum(), flight.getFlightid(), flight.getStartCityName(), 
                    flight.getEndCityName(), flight.getFlydate(), flight.getStarttime(), flight.getFlytime()));
            }
            
            return data.toString();
        } catch (Exception e) {
            System.err.println("获取航班数据失败: " + e.getMessage());
            return "获取航班数据失败，请重试";
        }
    }
}
