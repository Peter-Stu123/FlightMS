package org.isoft.controller;

import org.isoft.Entity.User;
import org.isoft.mapper.UserMapper;
import org.isoft.service.AIService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai") // 设置访问路径，即 http://localhost:8080/ai
@CrossOrigin // 实现跨域请求
public class AiController {
     @Autowired
      private ChatClient chatClient; // 注入ChatClient
    @Autowired
       private UserMapper userMapper;

    @Autowired  // 新增AI服务注入
      private AIService aiService; // 假设服务名称为AIService
    // 单用户分析接口
    @GetMapping("/analyze")
    public ResponseEntity<?> analyzeUser(@RequestParam String username) {
        try {
            // 查询用户
            User user = userMapper.selectByUsername(username).stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            // 调用 AI 服务
            String result = aiService.processUser(user);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            // 返回结构化错误信息
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * 批量用户分析接口（建议后台任务）
     * 示例请求：GET /api/ai/batch-analyze
     */
    @GetMapping("/batch-analyze")
    public ResponseEntity<?> batchAnalyzeUsers() {
        try {
            // 分页读取所有用户
            List<User> users = userMapper.getAllUsers(userMapper, 100);

            // 并行处理（注意：实际生产环境应使用异步任务）
            List<String> results = aiService.batchProcessUsers(users);

            return ResponseEntity.ok(results);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "批量处理失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    @GetMapping("/analyze-flight") // 示例：GET /ai/analyze-flight?flightId=1001
    public Flux<String> analyzeFlight(@RequestParam int flightId) {
        return aiService.analyzeFlightWithCities(flightId)
                .onErrorReturn("分析失败: " + flightId)
                .flatMapMany(Flux::just);
    }
    @PostMapping(value = "/chat") // 声明为POST请求，设置访问路径，即 http://localhost:8080/ai/chat
    public Flux chat(@RequestBody String message) {
        Flux<String> content = chatClient.prompt(message).stream().content();
        System.out.println(content);
        return content;
    }
    
    /**
     * 新增带数据库信息的AI对话接口
     * 该接口会将数据库中的航班信息与用户问题结合，让AI能够回答与航班相关的问题
     */
    @PostMapping("/chat-with-database")
    public Flux<String> chatWithDatabase(@RequestBody String message) {
        try {
            // 获取数据库中的航班信息
            String databaseInfo = aiService.getFlightDatabaseInfo();
            
            // 将数据库信息与用户问题结合成一个提示词
            String prompt = "以下是数据库中的航班信息:\n" + databaseInfo + 
                    "\n\n请根据上述数据回答用户问题: " + message;
            
            // 调用AI模型并返回结果
            return chatClient.prompt(prompt).stream().content();
        } catch (Exception e) {
            System.err.println("AI访问数据库失败: " + e.getMessage());
            return Flux.just("查询数据库时发生错误，请稍后再试");
        }
    }
}
